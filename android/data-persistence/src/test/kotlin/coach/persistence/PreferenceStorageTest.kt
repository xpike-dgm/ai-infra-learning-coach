package coach.persistence

import coach.model.CapacityPreferences
import coach.model.PreferenceRecord
import coach.model.StudyTimestamp
import coach.ports.TruthRecord
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * 16D (`STGX-v0`): the learner's preferences live in the store (`LFPS-v0` §11 puts them in the export), appended and
 * never edited; schema v9 adds them to a populated v8 database without touching any truth; and replacing the profile
 * from a backup keeps the replaced one aside.
 */
class PreferenceStorageTest {

    private val at = StudyTimestamp(1_790_942_400_000, "2026-10-25", 3 * 3600)

    private fun preference(key: String, value: String, day: String? = null) = TruthRecord(
        "preference_record", at,
        buildMap {
            put("preference_name", key)
            put("preference_value", value)
            day?.let { put("applies_to_day", it) }
        },
    )

    private fun <T> withStore(block: (SqlitePersistence) -> T): T {
        val file = Fixtures.tempDb()
        return SqlitePersistence.open(file.absolutePath).use(block)
    }

    @Test
    fun `a preference is appended, read back oldest first, and never updated or deleted`() {
        withStore { db ->
            db.appendTruth(preference(CapacityPreferences.NORMAL_KEY, "45"))
            db.appendTruth(preference(CapacityPreferences.TODAY_KEY, "short", day = "2026-10-25"))
            db.appendTruth(preference(CapacityPreferences.NORMAL_KEY, "50"))
            val rows = db.preferenceRecords()
            assertEquals(listOf("45", "short", "50"), rows.map { it.value })
            assertEquals(listOf(null, "2026-10-25", null), rows.map { it.appliesToStudyDay })
            assertEquals(rows.map { it.sequence }.sorted(), rows.map { it.sequence })
            assertEquals(50, CapacityPreferences.of(rows, "2026-10-25").normalMinutes)
            assertFailsWith<Throwable> { db.execute("UPDATE preference_record SET preference_value = '90'") }
            assertFailsWith<Throwable> { db.execute("DELETE FROM preference_record") }
            assertEquals(3, db.preferenceRecords().size)
        }
    }

    @Test
    fun `the schema refuses an unknown key, an empty value, and a today choice without its day or a standing one with a day`() {
        withStore { db ->
            assertFailsWith<Throwable> { db.appendTruth(preference("capacity.weekend_minutes", "30")) }
            assertFailsWith<Throwable> { db.appendTruth(preference(CapacityPreferences.NORMAL_KEY, " ")) }
            assertFailsWith<Throwable> { db.appendTruth(preference(CapacityPreferences.TODAY_KEY, "short")) }
            assertFailsWith<Throwable> { db.appendTruth(preference(CapacityPreferences.NORMAL_KEY, "45", day = "2026-10-25")) }
            assertEquals(emptyList<PreferenceRecord>(), db.preferenceRecords())
        }
    }

    @Test
    fun `schema v9 adds preferences to a populated v8 database and leaves every truth row as it was`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 8)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { Fixtures.truthContent(it) }
        SqlitePersistence.open(file.absolutePath).use { db ->
            // Narrowed at 16E (`RMDX-v0` / `D-127`): version 10 adds the reminder table; v9's preferences are what this checks.
            assertTrue(Schema.VERSION >= 9)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            Schema.appendOnlyTables.forEach { assertEquals(before[it], after[it], it) }
            val columns = db.query("PRAGMA table_info(preference_record)") { it.getText(1) }
            assertTrue(listOf("preference_name", "preference_value", "applies_to_day", "recorded_on_study_day").all { it in columns }, columns.toString())
            db.appendTruth(preference(CapacityPreferences.SHORT_KEY, "20"))
            assertEquals(listOf("20"), db.preferenceRecords().map { it.value })
        }
    }

    private fun dir(): File = Fixtures.tempDb().parentFile.resolve("coach-replace-${System.nanoTime()}").also { it.mkdirs(); it.deleteOnExit() }

    @Test
    fun `restoring a backup keeps the replaced profile aside, preferences included, and the backup becomes the profile`() {
        val work = dir()
        val live = work.resolve("coach.db").also { Fixtures.populated(it, skillPrefix = "skill.live.s") }
        val backupSource = work.resolve("backup-source.db").also { Fixtures.populated(it, skillPrefix = "skill.backup.s") }
        SqlitePersistence.open(backupSource.absolutePath).use { it.appendTruth(preference(CapacityPreferences.NORMAL_KEY, "75")) }
        val archive = work.resolve("archive.coachdb")
        SqlitePersistence.open(backupSource.absolutePath).use { assertEquals(Backup.ExportResult.Exported, Backup.export(it, archive)) }
        val liveStore = SqlitePersistence.open(live.absolutePath)
        liveStore.appendTruth(preference(CapacityPreferences.NORMAL_KEY, "40"))
        val liveBefore = Fixtures.truthContent(liveStore)

        val outcome = assertIs<ProfileReplacement.Outcome.Restored>(
            ProfileReplacement.replace(liveStore, live, archive, work.resolve("kept"), stamp = "20261025-0900")
        )
        // The replaced profile is kept, whole.
        assertEquals(liveBefore, SqlitePersistence.openWithoutMigrating(outcome.keptAt.absolutePath).use { Fixtures.truthContent(it) })
        // The backup is the profile now, its preferences with it.
        SqlitePersistence.open(live.absolutePath).use { db ->
            assertEquals(listOf("75"), db.preferenceRecords().map { it.value })
            assertTrue(db.query("SELECT skill_logical_id FROM evidence_event") { it.getText(0) }.all { it.startsWith("skill.backup.s") })
        }
    }

    @Test
    fun `a refused backup replaces nothing and keeps no copy, because nothing was lost`() {
        val work = dir()
        val live = work.resolve("coach.db").also { Fixtures.populated(it) }
        val notAnArchive = work.resolve("photo.jpg").also { it.writeText("not a database") }
        val liveStore = SqlitePersistence.open(live.absolutePath)
        val before = Fixtures.truthContent(liveStore)
        val outcome = ProfileReplacement.replace(liveStore, live, notAnArchive, work.resolve("kept"), stamp = "20261025-0901")
        assertEquals(ProfileReplacement.Outcome.Refused(Backup.Refusal.INTEGRITY_CHECK_FAILED), outcome)
        assertFalse(work.resolve("kept").listFiles().orEmpty().any { it.name.endsWith(".db") }, "an unneeded copy was left")
        assertEquals(before, SqlitePersistence.open(live.absolutePath).use { Fixtures.truthContent(it) })
    }
}
