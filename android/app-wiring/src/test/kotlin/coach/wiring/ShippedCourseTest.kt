package coach.wiring

import coach.application.BuildDailyPlan
import coach.application.IngestCurriculum
import coach.curriculum.FileContentSource
import coach.model.DailyCapacityInput
import coach.model.NeedDisposition
import coach.model.NeedTrigger
import coach.model.PublishOutcome
import coach.model.StudyTimestamp
import coach.model.TaskPurpose
import coach.model.VersionedRef
import coach.persistence.SqlitePersistence
import coach.ports.ClockPort
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The course as it ships after 15B (`PYFX-v0`, `D-113`): the two packages in `assets`, read together by the real content
 * adapter, published into the real SQLite store by the real ingestion, and planned by the real gate and planner. The
 * first package is 15A's, untouched; the second only adds. Nothing here builds a package or a store of its own.
 */
class ShippedCourseTest {

    private val first = File("src/main/assets/curriculum_package.txt").readText()
    private val python = File("src/main/assets/curriculum_package_v2.txt").readText()
    private val content = FileContentSource(later = { listOf(python) }, source = { first })

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_790_942_400_000, "2026-10-02", 3 * 3600)
    }

    private val directory = Files.createTempDirectory("shipped-course").toFile()
    private val store = SqlitePersistence.open(File(directory, "coach.db").path)

    @AfterTest
    fun close() {
        store.close()
        directory.deleteRecursively()
    }

    private fun skill(id: String) = VersionedRef(id, 1)

    @Test
    fun `both shipped packages are read as one course, and the second only adds`() {
        assertNull(content.failure, "the shipped packages must read together")
        val packages = content.curriculumPackages()
        assertEquals(listOf(1, 2), packages.map { it.version })
        val (computing, pythonPackage) = packages
        assertEquals(20, pythonPackage.skills.size)
        assertEquals(21, pythonPackage.objectives.size)
        assertTrue(pythonPackage.skills.all { it.ref.logicalId.startsWith("skill.python.") && it.ref.version == 1 && it.lifecycleStatus == "published" })
        assertTrue(pythonPackage.prerequisiteEdges.all { it.lifecycleStatus == "published" })
        // Every edge points into the new package; its prerequisite is new or already published by the first package.
        val known = (computing.skills + pythonPackage.skills).map { it.ref }.toSet()
        assertTrue(pythonPackage.prerequisiteEdges.all { it.target in pythonPackage.skills.map { s -> s.ref } && it.prerequisite in known })
        assertTrue(pythonPackage.prerequisiteEdges.any { it.prerequisite in computing.skills.map { s -> s.ref } }, "Python builds on 15A")
        // Nothing the first package published is carried again.
        assertTrue(pythonPackage.skills.none { it.ref in computing.skills.map { s -> s.ref } })
        assertTrue(pythonPackage.resources.none { it.ref in computing.resources.map { r -> r.ref } })
    }

    @Test
    fun `both packages are published into the real store, oldest first, and a second start publishes nothing`() {
        val outcomes = IngestCurriculum(store, content, clock).ingestAll()
        assertEquals(listOf(1, 2), outcomes.map { assertIs<PublishOutcome.Published>(it, "$it").version })
        assertEquals(2, store.latestCurriculumVersion())
        assertEquals(32, store.publishedSkills().size)
        // A Python Skill waits on a 15A Skill through the store, exactly as the gate will read it.
        assertTrue(store.prerequisiteEdgesInto(skill("skill.python.run_repl_script")).any { it.prerequisite.logicalId.startsWith("skill.computing.") })

        val again = IngestCurriculum(store, content, clock).ingestAll()
        assertEquals(listOf(PublishOutcome.AlreadyPublished(1), PublishOutcome.AlreadyPublished(2)), again)
    }

    @Test
    fun `with the Python package, a learner who has done nothing is still offered only the two entry lessons`() {
        IngestCurriculum(store, content, clock).ingestAll()
        val built = BuildDailyPlan(store, content, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))
        val trace = assertIs<BuildDailyPlan.Built.Planned>(built).trace

        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"))
        assertEquals(entry, trace.selected.map { it.primarySkill }.toSet(), "no Python Skill starts before what it builds on")
        assertTrue(trace.selected.all { it.purpose == TaskPurpose.TEACH })

        assertEquals(32, trace.needs.count { it.trigger == NeedTrigger.NEW_LEARNING })
        val waiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        assertEquals(30, waiting.size, "everything downstream waits: $waiting")
        assertTrue(waiting.all { it.logicalId.startsWith("skill.python.") || it !in entry })
        assertTrue(trace.needs.none { it.disposition == NeedDisposition.NO_VALID_CANDIDATE }, "every Skill has its lesson")
    }

    // ---------------------------------------------------------------- 15C (`CFNX-v0`, `D-114`): the course with C

    private val c = File("src/main/assets/curriculum_package_v3.txt").readText()
    private val course = FileContentSource(later = { listOf(python, c) }, source = { first })
    private val terminal = skill("skill.linux.terminal_filesystem_navigation")

    @Test
    fun `all three shipped packages are published into the real store, oldest first, and C waits on the terminal`() {
        assertEquals(listOf(1, 2, 3), IngestCurriculum(store, course, clock).ingestAll().map { assertIs<PublishOutcome.Published>(it, "$it").version })
        assertEquals(3, store.latestCurriculumVersion())
        assertEquals(41, store.publishedSkills().size)
        // The first C Skill waits on the Linux terminal Skill (6C) and on 15A's execution model, through the store.
        val into = store.prerequisiteEdgesInto(skill("skill.c.compile_link_run_basic")).map { it.prerequisite }.toSet()
        assertEquals(setOf(terminal, skill("skill.computing.program_execution_model")), into)
        assertEquals((1..3).map { PublishOutcome.AlreadyPublished(it) }, IngestCurriculum(store, course, clock).ingestAll())
    }

    @Test
    fun `with the C package, a learner who has done nothing may also start the terminal lesson, and no C Skill starts`() {
        IngestCurriculum(store, course, clock).ingestAll()
        val trace = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, course, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))).trace

        // The terminal Skill has no prerequisite: it is a third entry point, next to 15A's two.
        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"), terminal)
        assertTrue(entry.containsAll(trace.selected.map { it.primarySkill }), "only entry Skills start: ${trace.selected.map { it.primarySkill }}")
        assertTrue(trace.selected.all { it.purpose == TaskPurpose.TEACH })
        assertTrue(trace.selected.sumOf { it.plannedMinutes } <= 60, "the day is never extended")

        assertEquals(41, trace.needs.count { it.trigger == NeedTrigger.NEW_LEARNING })
        val waiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        assertEquals(38, waiting.size, "everything downstream waits: $waiting")
        assertTrue(waiting.none { it in entry })
        assertTrue(waiting.containsAll(listOf("compile_link_run_basic", "declaration_type_model", "functions_basic").map { skill("skill.c.$it") }))
        assertTrue(trace.needs.none { it.disposition == NeedDisposition.NO_VALID_CANDIDATE }, "every Skill has its lesson")
    }
}
