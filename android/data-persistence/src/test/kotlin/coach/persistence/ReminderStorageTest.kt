package coach.persistence

import coach.model.ReminderPreference
import coach.model.StudyTimestamp
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * 16E (`RMDX-v0`): the reminder setting lives in the store like every preference, appended and never edited; schema v10
 * adds it as its own table to a populated v9 database without touching any truth, and a backup carries it.
 */
class ReminderStorageTest {

    private val at = StudyTimestamp(1_790_942_400_000, "2026-10-25", 3 * 3600)

    private fun reminder(enabled: String, minute: String) =
        TruthRecord("reminder_preference", at, mapOf("reminder_enabled" to enabled, "minute_of_day" to minute))

    private fun <T> withStore(block: (SqlitePersistence) -> T): T =
        SqlitePersistence.open(Fixtures.tempDb().absolutePath).use(block)

    @Test
    fun `a reminder setting is appended whole, read back oldest first, and never updated or deleted`() {
        withStore { db ->
            db.appendTruth(reminder("1", "1140"))
            db.appendTruth(reminder("0", "1140"))
            db.appendTruth(reminder("1", "480"))
            val rows = db.reminderPreferences()
            assertEquals(listOf(true, false, true), rows.map { it.enabled })
            assertEquals(listOf(1140, 1140, 480), rows.map { it.minuteOfDay })
            assertEquals(rows.map { it.sequence }.sorted(), rows.map { it.sequence })
            assertEquals(ReminderPreference(true, 480, recorded = true), ReminderPreference.of(rows))
            assertFailsWith<Throwable> { db.execute("UPDATE reminder_preference SET reminder_enabled = 0") }
            assertFailsWith<Throwable> { db.execute("DELETE FROM reminder_preference") }
            assertEquals(3, db.reminderPreferences().size)
        }
    }

    @Test
    fun `the schema refuses a minute outside the day and an on or off that is neither`() {
        withStore { db ->
            assertFailsWith<Throwable> { db.appendTruth(reminder("1", "1440")) }
            assertFailsWith<Throwable> { db.appendTruth(reminder("1", "-1")) }
            assertFailsWith<Throwable> { db.appendTruth(reminder("2", "600")) }
            assertFailsWith<Throwable> { db.appendTruth(TruthRecord("reminder_preference", at, mapOf("reminder_enabled" to "1"))) }
            assertEquals(emptyList(), db.reminderPreferences())
        }
    }

    @Test
    fun `schema v10 adds the reminder to a populated v9 database and leaves every truth row as it was`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 9)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { Fixtures.truthContent(it) }
        SqlitePersistence.open(file.absolutePath).use { db ->
            // Narrowed at 16F (`E2EX-v0` / `D-128`): version 11 links attempts to planned tasks; v10's reminder is what this checks.
            assertTrue(Schema.VERSION >= 10)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            Schema.appendOnlyTables.forEach { assertEquals(before[it], after[it], it) }
            val columns = db.query("PRAGMA table_info(reminder_preference)") { it.getText(1) }
            assertTrue(listOf("reminder_enabled", "minute_of_day", "recorded_on_study_day", "sequence").all { it in columns }, columns.toString())
            db.appendTruth(reminder("1", "1200"))
            assertEquals(listOf(1200), db.reminderPreferences().map { it.minuteOfDay })
        }
    }

    @Test
    fun `a backup carries the reminder setting with the profile`() {
        val source = Fixtures.tempDb().also { Fixtures.populated(it) }
        SqlitePersistence.open(source.absolutePath).use { it.appendTruth(reminder("1", "1260")) }
        val archive = Fixtures.tempDb().resolveSibling("reminder-${System.nanoTime()}.coachdb").also { it.deleteOnExit() }
        SqlitePersistence.open(source.absolutePath).use { assertEquals(Backup.ExportResult.Exported, Backup.export(it, archive)) }
        val restored = Fixtures.tempDb()
        assertEquals(Backup.RestoreResult.Restored, Backup.restore(archive, restored))
        SqlitePersistence.open(restored.absolutePath).use { db ->
            assertEquals(ReminderPreference(true, 1260, recorded = true), ReminderPreference.of(db.reminderPreferences()))
        }
    }
}
