package coach.persistence

import coach.model.PilotJournalKind
import coach.model.StudyTimestamp
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 18G (`D-141`): the pilot journal lives in the store, appended and never edited, so the pilot report counts rows that
 * cannot have been changed and a backup carries them; schema v14 adds it to a populated v13 database without touching a
 * stored row.
 */
class PilotJournalStorageTest {

    private val at = StudyTimestamp(1_793_000_000_000, "2026-10-28", 3 * 3600)

    private fun entry(kind: String, note: String = "", plan: Long? = null) = TruthRecord("pilot_journal_entry", at,
        buildMap {
            put("entry_kind", kind)
            put("note", note)
            plan?.let { put("plan_version_id", it.toString()) }
        })

    @Test
    fun `an entry is appended, read back, never updated or deleted, and only a known kind and a short trimmed note are stored`() {
        SqlitePersistence.open(Fixtures.tempDb().absolutePath).use { db ->
            assertEquals(emptyList(), db.pilotJournalEntries())
            db.appendTruth(entry("wrong_branch", "Linux'a geçti"))
            val stored = db.pilotJournalEntries().single()
            assertEquals(PilotJournalKind.WRONG_BRANCH, stored.kind)
            assertEquals("Linux'a geçti", stored.note)
            assertNull(stored.planVersionId)
            assertEquals("2026-10-28", stored.studyDay)
            assertFailsWith<Throwable> { db.appendTruth(entry("too_hard")) }
            assertFailsWith<Throwable> { db.appendTruth(entry("time_wrong", "x".repeat(201))) }
            assertFailsWith<Throwable> { db.appendTruth(entry("time_wrong", " untrimmed")) }
            assertFailsWith<Throwable> { db.appendTruth(entry("time_wrong", plan = 999)) }
            assertFailsWith<Throwable> { db.execute("UPDATE pilot_journal_entry SET entry_kind = 'personal_preference'") }
            assertFailsWith<Throwable> { db.execute("DELETE FROM pilot_journal_entry") }
            assertEquals(1, db.pilotJournalEntries().size)
            db.appendTruth(entry("time_wrong", "x".repeat(200)))
            assertEquals(2, db.pilotJournalEntries().size)
        }
    }

    @Test
    fun `schema v14 adds the journal to a populated v13 database and leaves every stored row as it was`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 13)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { Fixtures.truthContent(it) }
        SqlitePersistence.open(file.absolutePath).use { db ->
            assertEquals(14, Schema.VERSION)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            Schema.appendOnlyTables.forEach { assertEquals(before[it], after[it], it) }
            db.appendTruth(entry("rule_violation", "ön koşul atlandı"))
            assertEquals(listOf(PilotJournalKind.RULE_VIOLATION), db.pilotJournalEntries().map { it.kind })
        }
    }

    @Test
    fun `a backup carries the journal`() {
        val source = Fixtures.tempDb().also { Fixtures.populated(it) }
        SqlitePersistence.open(source.absolutePath).use { it.appendTruth(entry("prerequisite_impossible", "007")) }
        val archive = Fixtures.tempDb().resolveSibling("journal-${System.nanoTime()}.coachdb").also { it.deleteOnExit() }
        SqlitePersistence.open(source.absolutePath).use { assertEquals(Backup.ExportResult.Exported, Backup.export(it, archive)) }
        val restored = Fixtures.tempDb()
        assertEquals(Backup.RestoreResult.Restored, Backup.restore(archive, restored))
        SqlitePersistence.open(restored.absolutePath).use { db ->
            assertTrue(db.pilotJournalEntries().isNotEmpty())
            assertEquals("007", db.pilotJournalEntries().single().note)
        }
    }
}
