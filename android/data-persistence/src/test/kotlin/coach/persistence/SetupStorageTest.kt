package coach.persistence

import coach.model.SetupOutcome
import coach.model.StudyTimestamp
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * 18A (`D-139`): that the learner went through the first-run setup lives in the store, appended and never edited, so it
 * is offered once and a backup carries it; schema v13 adds it to a populated v12 database without touching a stored row.
 */
class SetupStorageTest {

    private val at = StudyTimestamp(1_793_000_000_000, "2026-10-28", 3 * 3600)

    private fun setup(outcome: String) = TruthRecord("setup_record", at, mapOf("setup_outcome" to outcome))

    @Test
    fun `a setup record is appended, read back, never updated or deleted, and only a known outcome is stored`() {
        SqlitePersistence.open(Fixtures.tempDb().absolutePath).use { db ->
            assertEquals(emptyList(), db.setupRecords())
            db.appendTruth(setup("skipped"))
            assertEquals(listOf(SetupOutcome.SKIPPED), db.setupRecords().map { it.outcome })
            assertEquals("2026-10-28", db.setupRecords().single().studyDay)
            assertFailsWith<Throwable> { db.appendTruth(setup("half_done")) }
            assertFailsWith<Throwable> { db.execute("UPDATE setup_record SET setup_outcome = 'completed'") }
            assertFailsWith<Throwable> { db.execute("DELETE FROM setup_record") }
            assertEquals(1, db.setupRecords().size)
        }
    }

    @Test
    fun `schema v13 adds the setup record to a populated v12 database and leaves every stored row as it was`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 12)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { Fixtures.truthContent(it) }
        SqlitePersistence.open(file.absolutePath).use { db ->
            // Narrowed at 18G (`D-141`): version 14 adds the pilot journal; v13's table is what this checks.
            assertTrue(Schema.VERSION >= 13)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            Schema.appendOnlyTables.forEach { assertEquals(before[it], after[it], it) }
            db.appendTruth(setup("completed"))
            assertEquals(listOf(SetupOutcome.COMPLETED), db.setupRecords().map { it.outcome })
        }
    }

    @Test
    fun `a backup carries that the setup was done`() {
        val source = Fixtures.tempDb().also { Fixtures.populated(it) }
        SqlitePersistence.open(source.absolutePath).use { it.appendTruth(setup("completed")) }
        val archive = Fixtures.tempDb().resolveSibling("setup-${System.nanoTime()}.coachdb").also { it.deleteOnExit() }
        SqlitePersistence.open(source.absolutePath).use { assertEquals(Backup.ExportResult.Exported, Backup.export(it, archive)) }
        val restored = Fixtures.tempDb()
        assertEquals(Backup.RestoreResult.Restored, Backup.restore(archive, restored))
        SqlitePersistence.open(restored.absolutePath).use { db -> assertTrue(db.setupRecords().isNotEmpty()) }
    }
}
