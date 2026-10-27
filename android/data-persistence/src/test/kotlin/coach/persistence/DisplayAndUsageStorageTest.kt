package coach.persistence

import coach.model.AiCall
import coach.model.AiCallOutcome
import coach.model.AiCallUse
import coach.model.DisplayPreference
import coach.model.StudyTimestamp
import coach.model.ThemeChoice
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * 17E (`D-138`): the theme and motion setting and the AI calls counted live in the store like every preference —
 * appended, never edited — so a backup carries them; schema v12 adds both tables to a populated v11 database without
 * touching any stored row, and the AI table has no column that could hold what a call carried.
 */
class DisplayAndUsageStorageTest {

    private val at = StudyTimestamp(1_793_000_000_000, "2026-10-27", 3 * 3600)

    private fun display(theme: String, reduce: String) =
        TruthRecord("display_preference", at, mapOf("theme" to theme, "reduce_motion" to reduce))

    private fun aiCall(use: String = "tutor_help", outcome: String = "answered", attempts: String = "1", input: String? = "120", output: String? = "40") =
        TruthRecord("ai_call_record", at, buildMap {
            put("call_use", use); put("model", "gpt-6-astra"); put("outcome", outcome); put("attempts", attempts)
            input?.let { put("input_usage", it) }
            output?.let { put("output_usage", it) }
        })

    private fun <T> withStore(block: (SqlitePersistence) -> T): T =
        SqlitePersistence.open(Fixtures.tempDb().absolutePath).use(block)

    @Test
    fun `a display setting is appended whole, the newest is the setting, and nothing is updated or deleted`() {
        withStore { db ->
            assertEquals(DisplayPreference.SYSTEM_DEFAULT, DisplayPreference.of(db.displayPreferences()))
            db.appendTruth(display("dark", "0"))
            db.appendTruth(display("light", "1"))
            assertEquals(DisplayPreference(ThemeChoice.LIGHT, reduceMotion = true, recorded = true), DisplayPreference.of(db.displayPreferences()))
            assertFailsWith<Throwable> { db.execute("UPDATE display_preference SET theme = 'dark'") }
            assertFailsWith<Throwable> { db.execute("DELETE FROM display_preference") }
            assertEquals(2, db.displayPreferences().size)
        }
    }

    @Test
    fun `the schema refuses a theme it does not know and a motion value that is neither`() {
        withStore { db ->
            assertFailsWith<Throwable> { db.appendTruth(display("sepia", "0")) }
            assertFailsWith<Throwable> { db.appendTruth(display("dark", "2")) }
            assertEquals(emptyList(), db.displayPreferences())
        }
    }

    @Test
    fun `an AI call is a count, read back as stored, with unreported tokens unknown`() {
        withStore { db ->
            db.appendTruth(aiCall())
            db.appendTruth(aiCall(use = "connection_check", outcome = "transport_error", attempts = "2", input = null, output = null))
            val calls = db.aiCalls()
            assertEquals(
                listOf(
                    AiCall(AiCallUse.TUTOR_HELP, "gpt-6-astra", AiCallOutcome.ANSWERED, 1, 120, 40),
                    AiCall(AiCallUse.CONNECTION_CHECK, "gpt-6-astra", AiCallOutcome.TRANSPORT_ERROR, 2, null, null),
                ),
                calls.map { it.call },
            )
            assertEquals(listOf("2026-10-27", "2026-10-27"), calls.map { it.studyDay })
            assertFailsWith<Throwable> { db.execute("UPDATE ai_call_record SET attempts = 1") }
            assertFailsWith<Throwable> { db.execute("DELETE FROM ai_call_record") }
        }
    }

    @Test
    fun `the schema refuses an unknown use or outcome, no attempt, a blank model and a negative count`() {
        withStore { db ->
            assertFailsWith<Throwable> { db.appendTruth(aiCall(use = "planning")) }
            assertFailsWith<Throwable> { db.appendTruth(aiCall(outcome = "maybe")) }
            assertFailsWith<Throwable> { db.appendTruth(aiCall(attempts = "0")) }
            assertFailsWith<Throwable> { db.appendTruth(aiCall(input = "-1")) }
            assertFailsWith<Throwable> {
                db.appendTruth(TruthRecord("ai_call_record", at, mapOf("call_use" to "tutor_help", "model" to " ", "outcome" to "answered", "attempts" to "1")))
            }
            assertEquals(emptyList(), db.aiCalls())
        }
    }

    @Test
    fun `the AI table has columns for counts only — nothing a call carried can be stored`() {
        withStore { db ->
            val columns = db.query("PRAGMA table_info(ai_call_record)") { it.getText(1) }.toSet()
            assertEquals(
                setOf("id", "sequence", "call_use", "model", "outcome", "attempts", "input_usage", "output_usage",
                    "called_at_instant", "called_on_study_day", "utc_offset_minutes"),
                columns,
            )
        }
    }

    @Test
    fun `schema v12 adds both tables to a populated v11 database and leaves every stored row as it was`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 11)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { Fixtures.truthContent(it) }
        SqlitePersistence.open(file.absolutePath).use { db ->
            // Narrowed at 18A (`D-139`): version 13 adds the setup record; v12's tables are what this checks.
            assertTrue(Schema.VERSION >= 12)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            Schema.appendOnlyTables.forEach { assertEquals(before[it], after[it], it) }
            db.appendTruth(display("dark", "1"))
            db.appendTruth(aiCall())
            assertEquals(1, db.displayPreferences().size)
            assertEquals(1, db.aiCalls().size)
        }
    }

    @Test
    fun `a backup carries the display setting and the AI counts with the profile`() {
        val source = Fixtures.tempDb().also { Fixtures.populated(it) }
        SqlitePersistence.open(source.absolutePath).use { it.appendTruth(display("dark", "1")); it.appendTruth(aiCall()) }
        val archive = Fixtures.tempDb().resolveSibling("display-${System.nanoTime()}.coachdb").also { it.deleteOnExit() }
        SqlitePersistence.open(source.absolutePath).use { assertEquals(Backup.ExportResult.Exported, Backup.export(it, archive)) }
        val restored = Fixtures.tempDb()
        assertEquals(Backup.RestoreResult.Restored, Backup.restore(archive, restored))
        SqlitePersistence.open(restored.absolutePath).use { db ->
            assertEquals(DisplayPreference(ThemeChoice.DARK, reduceMotion = true, recorded = true), DisplayPreference.of(db.displayPreferences()))
            assertTrue(db.aiCalls().single().call.outcome == AiCallOutcome.ANSWERED)
        }
    }
}
