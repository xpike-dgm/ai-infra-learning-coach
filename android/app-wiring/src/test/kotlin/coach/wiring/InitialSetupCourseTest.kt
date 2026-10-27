package coach.wiring

import coach.application.BuildDailyPlan
import coach.application.CapacitySettings
import coach.application.CaptureProgramSnapshot
import coach.application.IngestCurriculum
import coach.application.InitialSetup
import coach.application.RequestDiagnostic
import coach.curriculum.FileContentSource
import coach.model.DiagnosticRecord
import coach.model.DiagnosticScopeCodec
import coach.model.DiagnosticSource
import coach.persistence.SqlitePersistence as Store
import coach.model.SetupOutcome
import coach.model.TaskPurpose
import coach.model.StudyTimestamp
import coach.persistence.SqlitePersistence
import coach.ports.ClockPort
import coach.presentation.SetupPresentation
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 18A (`D-139`): the first-run setup over the course as it ships — the eight packages, the real ingestion, the real store
 * and the real planner. It is offered once, before any plan; it records the learner's normal day and, only for the areas
 * the learner ticks, one diagnostic whose source is the entry placement; skipping records that it was done and nothing
 * else; and Today is planned after it, from what it recorded.
 */
class InitialSetupCourseTest {

    private fun asset(name: String) = File("src/main/assets/$name").readText()
    private val content = FileContentSource(
        later = { (2..8).map { asset("curriculum_package_v$it.txt") } },
        source = { asset("curriculum_package.txt") },
    )
    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_793_000_000_000, "2026-10-28", 3 * 3600)
    }
    private val directory = Files.createTempDirectory("initial-setup").toFile()
    private val store = SqlitePersistence.open(File(directory, "coach.db").path)

    @AfterTest
    fun close() {
        store.close()
        directory.deleteRecursively()
    }

    private fun setup(): InitialSetup {
        IngestCurriculum(store, content, clock).ingestAll()
        return InitialSetup(store, content, clock)
    }

    /** The diagnostic requests stored, newest last — read through the model's own codec, as the engines read them. */
    private fun requests(db: Store) = db.assessmentSessions().mapNotNull { row ->
        row.record.payload["blueprint"]?.let(DiagnosticScopeCodec::decode) as? DiagnosticRecord.Request
    }

    private fun view(setup: InitialSetup, db: Store = store) =
        SetupPresentation.of(setup.topics().map { it.topic to it.skills }, CapacitySettings(db, clock).read().normalMinutes)

    @Test
    fun `a fresh install is offered the setup once, with every area of the course and the preset length`() {
        assertFalse(InitialSetup(store, content, clock).needed(), "nothing published: nothing to set up against")
        val setup = setup()
        assertTrue(setup.needed())
        val view = view(setup)
        assertEquals(60, view.normalMinutes, "the preset until the learner sets it")
        assertEquals(listOf("python", "c", "linux", "shell", "git", "english"), view.areas.map { it.id }.filter { it != "other" }.take(6))
        val everySkill = store.publishedSkills().map { it.ref }.toSet()
        assertEquals(everySkill, view.areas.flatMap { it.skills }.toSet(), "every published Skill sits in one area")
        assertTrue(view.areas.all { it.topicCount > 0 && it.skills.isNotEmpty() })
    }

    @Test
    fun `finishing records the normal day and only the ticked areas' diagnostic, then the planner plans from them`() {
        val setup = setup()
        val view = view(setup)
        val linux = SetupPresentation.placement(view, setOf("linux"))
        assertTrue(linux.isNotEmpty())
        val finished = setup.finish(SetupOutcome.COMPLETED, normalMinutes = 45, placement = linux)
        assertIs<RequestDiagnostic.Requested.Written>(finished.placement)
        val active = requests(store).single().scope
        assertEquals(DiagnosticSource.CURRICULUM_ENTRY_PLACEMENT, active.source)
        assertTrue(active.skills.all { it in linux }, "only the ticked area's Skills are checked")
        assertEquals(45, CapacitySettings(store, clock).read().normalMinutes)
        assertEquals(listOf(SetupOutcome.COMPLETED), store.setupRecords().map { it.outcome })
        assertFalse(setup.needed(), "offered once")

        // Nothing was planned by the setup; the planner plans after it, within the learner's own length.
        assertNull(store.latestPlan())
        val built = assertIs<BuildDailyPlan.Built.Planned>(
            BuildDailyPlan(store, content, clock).build(CapacitySettings(store, clock).read().input())
        )
        assertTrue(built.trace.capacity.hardBudgetMinutes <= 45)
    }

    @Test
    fun `nothing ticked opens no diagnostic, and skipping records only that the setup was done`() {
        val setup = setup()
        assertNull(setup.finish(SetupOutcome.COMPLETED, normalMinutes = 60, placement = emptyList()).placement)
        assertEquals(emptyList(), requests(store))

        val other = Files.createTempDirectory("initial-setup-skip").toFile()
        SqlitePersistence.open(File(other, "coach.db").path).use { skipped ->
            IngestCurriculum(skipped, content, clock).ingestAll()
            val watermark = skipped.truthWatermark()
            InitialSetup(skipped, content, clock).finish(SetupOutcome.SKIPPED, normalMinutes = 30, placement = SetupPresentation.placement(view(setup), setOf("python")))
            assertEquals(listOf(SetupOutcome.SKIPPED), skipped.setupRecords().map { it.outcome })
            assertEquals(watermark + 1, skipped.truthWatermark(), "one row: the setup record, and nothing the learner did not choose")
            assertEquals(emptyList(), requests(skipped))
            assertEquals(60, CapacitySettings(skipped, clock).read().normalMinutes, "a skip keeps the preset")
            assertFalse(InitialSetup(skipped, content, clock).needed())
        }
        other.deleteRecursively()
    }

    private fun plannedChecks(built: BuildDailyPlan.Built.Planned) = built.trace.selected.filter { it.purpose == TaskPurpose.DIAGNOSE }

    @Test
    fun `a check joins the plan only when its Skill may start, as the setup says`() {
        // Linux's first Skill has no prerequisite: its check can come today.
        val setup = setup()
        setup.finish(SetupOutcome.COMPLETED, normalMinutes = 60, placement = SetupPresentation.placement(view(setup), setOf("linux")))
        val linux = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, content, clock).build(CapacitySettings(store, clock).read().input()))
        assertTrue(plannedChecks(linux).isNotEmpty(), "a startable Skill's check is planned")

        // C waits on the terminal Skill: nothing of it is checked today, and nothing of it is skipped either.
        val other = Files.createTempDirectory("initial-setup-c").toFile()
        Store.open(File(other, "coach.db").path).use { db ->
            IngestCurriculum(db, content, clock).ingestAll()
            val c = InitialSetup(db, content, clock)
            c.finish(SetupOutcome.COMPLETED, normalMinutes = 60, placement = SetupPresentation.placement(view(c, db), setOf("c")))
            val built = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(db, content, clock).build(CapacitySettings(db, clock).read().input()))
            assertEquals(emptyList(), plannedChecks(built), "no C check before its prerequisite")
        }
        other.deleteRecursively()
    }

    @Test
    fun `a setup interrupted after its diagnostic, then skipped, leaves no diagnostic the learner declined (review)`() {
        val setup = setup()
        val python = SetupPresentation.placement(view(setup), setOf("python"))
        // The diagnostic was written, then the process died before the setup row: the setup is offered again.
        assertIs<RequestDiagnostic.Requested.Written>(RequestDiagnostic(store, clock).request(DiagnosticSource.CURRICULUM_ENTRY_PLACEMENT, python))
        assertTrue(setup.needed())
        setup.finish(SetupOutcome.SKIPPED)
        val newest = store.assessmentSessions().last().record.payload["blueprint"]?.let(DiagnosticScopeCodec::decode)
        assertIs<DiagnosticRecord.Withdrawal>(newest, "the leftover placement diagnostic is withdrawn")
        val built = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, content, clock).build(CapacitySettings(store, clock).read().input()))
        assertEquals(emptyList(), plannedChecks(built), "learning starts at the beginning")
    }

    @Test
    fun `a store that was used before is not asked again`() {
        IngestCurriculum(store, content, clock).ingestAll()
        BuildDailyPlan(store, content, clock).build(CapacitySettings(store, clock).read().input())
        assertFalse(InitialSetup(store, content, clock).needed(), "a plan was made: the store was used before 18A")
        assertTrue(CaptureProgramSnapshot(store, clock).capture().planVersionId != null)
    }
}
