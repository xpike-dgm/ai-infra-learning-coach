package coach.persistence

import coach.model.AssessmentScope
import coach.model.StudyTimestamp
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * 13B's storage against a real SQLite file: the monthly row completed by a forward migration to version 4,
 * the refusals that make a weekly or monthly session without a blueprint of its own scope impossible, and
 * the monthly read path.
 */
class MonthlySessionStorageTest {

    private fun session(scope: String, blueprint: String?, at: StudyTimestamp = Fixtures.at) = TruthRecord(
        "assessment_session", at, buildMap {
            put("scope", scope)
            blueprint?.let { put("blueprint", it) }
        },
    )

    private fun refused(db: SqlitePersistence, record: TruthRecord) {
        val refusal = assertFailsWith<Exception> { db.appendTruth(record) }
        assertTrue("its own scope's format" in refusal.message.orEmpty(), refusal.message)
    }

    @Test
    fun `migrating a populated schema-3 database installs the format rule and preserves every truth row`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 3)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
            db.appendTruth(Fixtures.session())
            db.appendTruth(session("weekly", "weekly_blueprint/1\nstored at v3"))
            Fixtures.truthContent(db)
        }
        SqlitePersistence.open(file.absolutePath).use { db ->
            assertEquals(4, Schema.VERSION)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            // Nothing earlier is touched: a trigger guards inserts only.
            Schema.truthTables.forEach { assertEquals(before[it], after[it], it) }
            assertEquals(
                listOf("assessment_session_blueprint_format"),
                db.query("SELECT name FROM sqlite_master WHERE type = 'trigger' AND name LIKE '%blueprint_format'") { it.getText(0) },
            )
            refused(db, session("monthly", null))
        }
    }

    @Test
    fun `a monthly session without a monthly blueprint is refused by the engine`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            refused(db, session("monthly", null))
            refused(db, session("monthly", "weekly_blueprint/1"))
            refused(db, session("weekly", "monthly_blueprint/1"))
            refused(db, session("monthly", "something else"))
            assertEquals(0, db.count("assessment_session"))
            db.appendTruth(session("monthly", "monthly_blueprint/1"))
            db.appendTruth(session("weekly", "weekly_blueprint/1"))
            // A daily session never had a blueprint and still needs none.
            db.appendTruth(Fixtures.session())
            assertEquals(3, db.count("assessment_session"))
        }
    }

    @Test
    fun `a stored monthly blueprint is never edited or removed`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.appendTruth(session("monthly", "monthly_blueprint/1"))
            assertFailsWith<Exception> { db.execute("UPDATE assessment_session SET blueprint = 'monthly_blueprint/1\nx'") }
            assertFailsWith<Exception> { db.execute("DELETE FROM assessment_session") }
            assertEquals(listOf("monthly_blueprint/1"), db.query("SELECT blueprint FROM assessment_session") { it.getText(0) })
        }
    }

    @Test
    fun `the newest monthly session reads back exactly as written and never as a weekly one`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.appendTruth(session("monthly", "monthly_blueprint/1\nfirst"))
            val later = StudyTimestamp(Fixtures.at.instantEpochMillis + 86_400_000, "2026-09-01", 3 * 3600)
            val id = db.appendTruth(session("monthly", "monthly_blueprint/1\nsecond", later))
            val weekly = db.appendTruth(session("weekly", "weekly_blueprint/1\nnewer", later))
            val latest = db.latestAssessmentSession(AssessmentScope.MONTHLY_CAPABILITY)!!
            assertEquals(id, latest.id)
            assertEquals("monthly_blueprint/1\nsecond", latest.record.payload["blueprint"])
            assertEquals(later, latest.record.recordedAt)
            assertEquals(weekly, db.latestAssessmentSession(AssessmentScope.WEEKLY_BLUEPRINT)!!.id)
        }
    }
}
