package coach.persistence

import coach.model.AssessmentScope
import coach.model.StudyTimestamp
import coach.model.VersionedRef
import coach.persistence.Fixtures.evidence
import coach.persistence.Fixtures.exposure
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 13A's storage against a real SQLite file: `assessment_session` completed with its blueprint by a
 * forward migration, the refusals that make a weekly session without one impossible, and the three read
 * paths composition uses.
 */
class WeeklySessionStorageTest {

    private fun weekly(blueprint: String? = "weekly_blueprint/1", at: StudyTimestamp = Fixtures.at) = TruthRecord(
        "assessment_session", at, buildMap {
            put("scope", "weekly")
            blueprint?.let { put("blueprint", it) }
        },
    )

    @Test
    fun `migrating a populated schema-2 database completes the session table and preserves every truth row`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 2)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
            db.appendTruth(Fixtures.session())
            Fixtures.truthContent(db)
        }
        SqlitePersistence.open(file.absolutePath).use { db ->
            assertEquals(3, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            // Every earlier row is still there, unchanged; the session row only gained an empty column.
            (Schema.truthTables - "assessment_session").forEach { assertEquals(before[it], after[it], it) }
            assertEquals(before.getValue("assessment_session").map { "$it|∅" }, after.getValue("assessment_session"))
            assertTrue(db.query("PRAGMA table_info(assessment_session)") { it.getText(1) }.contains("blueprint"))
        }
    }

    @Test
    fun `a weekly session without its blueprint is refused by the engine`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            val refusal = assertFailsWith<Exception> { db.appendTruth(weekly(blueprint = null)) }
            assertTrue("CHECK constraint" in refusal.message.orEmpty(), refusal.message)
            assertEquals(0, db.count("assessment_session"))
            db.appendTruth(weekly())
            // A daily session never had a blueprint and still needs none.
            db.appendTruth(Fixtures.session())
            assertEquals(2, db.count("assessment_session"))
        }
    }

    @Test
    fun `a stored blueprint is never edited or removed`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.appendTruth(weekly())
            assertFailsWith<Exception> { db.execute("UPDATE assessment_session SET blueprint = 'weekly_blueprint/1\nx'") }
            assertFailsWith<Exception> { db.execute("DELETE FROM assessment_session") }
            assertEquals(listOf("weekly_blueprint/1"), db.query("SELECT blueprint FROM assessment_session") { it.getText(0) })
        }
    }

    @Test
    fun `the newest session of a scope reads back exactly as written`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.appendTruth(weekly("weekly_blueprint/1\nfirst"))
            db.appendTruth(Fixtures.session())
            val later = StudyTimestamp(Fixtures.at.instantEpochMillis + 86_400_000, "2026-09-01", 3 * 3600)
            val id = db.appendTruth(weekly("weekly_blueprint/1\nsecond", later))
            val latest = db.latestAssessmentSession(AssessmentScope.WEEKLY_BLUEPRINT)!!
            assertEquals(id, latest.id)
            assertEquals("weekly_blueprint/1\nsecond", latest.record.payload["blueprint"])
            assertEquals(later, latest.record.recordedAt)
            assertNull(db.latestAssessmentSession(AssessmentScope.MONTHLY_CAPABILITY))
            assertEquals("daily", db.latestAssessmentSession(AssessmentScope.DAILY_MICRO)!!.record.payload["scope"])
        }
    }

    @Test
    fun `exposure is found by pinned item version or by variant family`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.appendTruth(exposure(resource = "item.a"))
            db.appendTruth(TruthRecord("exposure_record", Fixtures.at, mapOf("resource_logical_id" to "item.b",
                "resource_version" to "2", "variant_family_id" to "fam.b", "exposure_kind" to "solution_exposure")))
            assertEquals(listOf("item.a@v1"), db.exposuresFor(listOf(VersionedRef("item.a", 1)), emptyList()).map { it.resource.toString() })
            // Another version of the same item is a different item.
            assertTrue(db.exposuresFor(listOf(VersionedRef("item.b", 1)), emptyList()).isEmpty())
            val byFamily = db.exposuresFor(emptyList(), listOf("fam.b")).single()
            assertTrue(byFamily.solutionExposed)
            assertTrue(db.exposuresFor(emptyList(), emptyList()).isEmpty())
        }
    }

    @Test
    fun `recent Skills are read by the study day each row recorded, never by instant`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            // Recorded late on the 24th in Istanbul: the instant is already the 25th in UTC.
            val lateEvening = StudyTimestamp(1_790_300_000_000, "2026-09-24", 3 * 3600)
            db.appendTruth(evidence(skill = "skill.old").copy(recordedAt = lateEvening))
            db.appendTruth(evidence(skill = "skill.new").copy(recordedAt = StudyTimestamp(1_790_400_000_000, "2026-09-25", 3 * 3600)))
            db.appendTruth(evidence(skill = "skill.new").copy(recordedAt = StudyTimestamp(1_790_500_000_000, "2026-09-26", 3 * 3600)))
            assertEquals(listOf(VersionedRef("skill.new", 1)), db.skillsEvidencedSince("2026-09-25"))
            assertEquals(listOf("skill.new", "skill.old"), db.skillsEvidencedSince("2026-09-24").map { it.logicalId })
        }
    }
}
