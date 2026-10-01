package coach.persistence

import coach.model.StudyTimestamp
import coach.model.VersionedRef
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * 13C's storage against a real SQLite file: `retention_state` completed by a forward migration to version
 * 5, the axis it may hold enforced by SQLite itself, `RVR-v0` §19's indexed due query, and the study day an
 * evidence row carries back to the engine.
 */
class RetentionStorageTest {

    private fun retention(skill: String, state: String, next: String = "", watermark: Long = 1) = ProjectionRecord(
        key = "retention_state:$skill@v1",
        policyVersion = "RVR-v0",
        truthWatermark = watermark,
        builtAtInstant = Fixtures.at.instantEpochMillis,
        inputCurriculumVersion = 1,
        payload = mapOf(
            "state" to state, "retention_profile" to "standard", "critical_prerequisite" to "0",
            "current_interval_days" to if (next.isEmpty()) "" else "4", "next_review_on_study_day" to next,
            "last_strong_retention_on_study_day" to "", "last_retention_evidence_id" to "",
            "successful_delayed_review_count" to "0", "unresolved_verification_evidence_id" to "",
            "verification_failure_on_study_day" to "", "at_risk_reason_codes" to "",
            "last_natural_reuse_on_study_day" to "", "reason_codes" to "", "as_of_study_day" to "2026-10-01",
        ),
    )

    @Test
    fun `migrating a populated schema-4 database completes retention_state and preserves every truth row`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 4)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
            db.appendTruth(Fixtures.session())
            Fixtures.truthContent(db)
        }
        SqlitePersistence.open(file.absolutePath).use { db ->
            // Narrowed at 13D: version 5 is 13C's; later versions belong to their own steps.
            assertTrue(Schema.VERSION >= 5)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            Schema.truthTables.forEach { assertEquals(before[it], after[it], it) }
            val columns = db.query("PRAGMA table_info(retention_state)") { it.getText(1) }
            listOf("next_review_on_study_day", "current_interval_days", "unresolved_verification_evidence_id",
                "successful_delayed_review_count", "at_risk_reason_codes", "as_of_study_day").forEach { assertTrue(it in columns, it) }
            assertEquals(listOf("retention_due"),
                db.query("SELECT name FROM sqlite_master WHERE type = 'index' AND name = 'retention_due'") { it.getText(0) })
        }
    }

    @Test
    fun `retention_state holds only the RVR-v0 axis, on insert and on the upsert's update`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            val insert = assertFailsWith<Exception> { db.writeProjection(retention("skill.a", "forgotten")) }
            assertTrue("RVR-v0 retention axis" in insert.message.orEmpty(), insert.message)
            db.writeProjection(retention("skill.a", "fresh", "2026-10-05"))
            val upsert = assertFailsWith<Exception> { db.writeProjection(retention("skill.a", "decayed", "2026-10-05")) }
            assertTrue("RVR-v0 retention axis" in upsert.message.orEmpty(), upsert.message)
            // An upsert is refused by the insert guard before it reaches its update; a direct UPDATE is the
            // only path the update guard alone protects.
            val update = assertFailsWith<Exception> { db.execute("UPDATE retention_state SET state = 'decayed'") }
            assertTrue("RVR-v0 retention axis" in update.message.orEmpty(), update.message)
            assertEquals("fresh", db.readProjection("retention_state:skill.a@v1")!!.payload["state"])
        }
    }

    @Test
    fun `the due query returns only fresh and stable schedules whose study day has come`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            db.writeProjection(retention("skill.due_today", "fresh", "2026-10-05"))
            db.writeProjection(retention("skill.overdue", "stable", "2026-09-01"))
            db.writeProjection(retention("skill.future", "stable", "2026-10-06"))
            db.writeProjection(retention("skill.verifying", "verification_due", "2026-09-01"))
            db.writeProjection(retention("skill.untracked", "untracked"))
            db.writeProjection(retention("skill.already_due", "review_due", "2026-09-01"))
            assertEquals(listOf(VersionedRef("skill.due_today", 1), VersionedRef("skill.overdue", 1)), db.retentionDueBy("2026-10-05"))
            assertEquals(listOf(VersionedRef("skill.overdue", 1)), db.retentionDueBy("2026-10-04"))
        }
    }

    @Test
    fun `an evidence row carries its own study day back, never one recomputed from its instant`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            // 23:30 in Istanbul is already the next UTC day; the row's own study day is what counts.
            val late = StudyTimestamp(1_790_005_800_000, "2026-09-21", 3 * 3600)
            val id = db.appendTruth(Fixtures.evidence(skill = "skill.python.loops").copy(recordedAt = late))
            db.appendTruth(TruthRecord("evidence_event_objective", late, mapOf("evidence_event_id" to id.toString(),
                "objective_logical_id" to "objective.python.loops.trace", "objective_version" to "1")))
            val row = db.evidenceFor(VersionedRef("objective.python.loops.trace", 1)).single()
            assertEquals("2026-09-21", row.studyDay)
        }
    }
}
