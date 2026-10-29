package coach.wiring

import coach.application.BuildDailyPlan
import coach.application.CapacitySettings
import coach.application.IngestCurriculum
import coach.application.RecordPilotJournal
import coach.curriculum.FileContentSource
import coach.model.PilotJournal
import coach.model.PilotJournalKind
import coach.model.StudyTimestamp
import coach.persistence.SqlitePersistence
import coach.ports.ClockPort
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * 18G (`D-141`): the pilot journal over the course as it ships — the real ingestion, the real store and the real planner.
 * A report names the plan the learner was looking at, keeps their note as they wrote it (trimmed), and changes nothing
 * else: no plan is written, none is replanned, and the truth watermark moves by exactly the journal's own row.
 */
class PilotJournalCourseTest {

    private fun asset(name: String) = File("src/main/assets/$name").readText()
    private val content = FileContentSource(
        later = { (2..8).map { asset("curriculum_package_v$it.txt") } },
        source = { asset("curriculum_package.txt") },
    )
    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_793_000_000_000, "2026-10-28", 3 * 3600)
    }
    private val directory = Files.createTempDirectory("pilot-journal").toFile()
    private val store = SqlitePersistence.open(File(directory, "coach.db").path)

    @AfterTest
    fun close() {
        store.close()
        directory.deleteRecursively()
    }

    @Test
    fun `a report names today's plan, keeps the note, and changes nothing else`() {
        IngestCurriculum(store, content, clock).ingestAll()
        val built = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, content, clock).build(CapacitySettings(store, clock).read().input()))
        val plans = store.planVersions()
        val watermark = store.truthWatermark()

        val result = RecordPilotJournal(store, clock).record(PilotJournalKind.TIME_WRONG, "  ilk görev 10 dk değil 40 dk sürdü  ")

        assertIs<RecordPilotJournal.Result.Recorded>(result)
        assertEquals(watermark + 1, store.truthWatermark(), "one row: the journal entry, and nothing else")
        assertEquals(plans, store.planVersions(), "reporting a plan problem neither writes nor replans a plan")
        val entry = store.pilotJournalEntries().single()
        assertEquals(PilotJournalKind.TIME_WRONG, entry.kind)
        assertEquals("ilk görev 10 dk değil 40 dk sürdü", entry.note)
        assertEquals(built.planVersionId, entry.planVersionId)
        assertEquals("2026-10-28", entry.studyDay)
    }

    @Test
    fun `a report with no plan yet names none, and a note too long is refused without writing`() {
        val watermark = store.truthWatermark()
        assertEquals(RecordPilotJournal.Result.NoteTooLong,
            RecordPilotJournal(store, clock).record(PilotJournalKind.RULE_VIOLATION, "x".repeat(PilotJournal.NOTE_LIMIT + 1)))
        assertEquals(watermark, store.truthWatermark())

        assertIs<RecordPilotJournal.Result.Recorded>(RecordPilotJournal(store, clock).record(PilotJournalKind.RULE_VIOLATION, ""))
        val entry = store.pilotJournalEntries().single()
        assertNull(entry.planVersionId)
        assertEquals("", entry.note)
        // A note that only looks like a number is still text, kept exactly as written (leading zeros included).
        RecordPilotJournal(store, clock).record(PilotJournalKind.PERSONAL_PREFERENCE, "007")
        assertEquals("007", store.pilotJournalEntries().last().note)
    }
}
