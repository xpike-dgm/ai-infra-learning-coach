package coach.persistence

import coach.model.EvaluatorStatus
import coach.model.StudyTimestamp
import coach.model.VersionedRef
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * 13D's storage against a real SQLite file: `weakness_state` completed by a forward migration to version 6,
 * the signal lifecycle enforced by SQLite itself, and an appended disposition read back as every engine must
 * see the row — never by editing it.
 */
class WeaknessStorageTest {

    private val objective = VersionedRef("objective.python.loops.trace", 1)

    private fun weakness(state: String) = ProjectionRecord(
        key = "weakness_state:${objective.logicalId}@v1",
        policyVersion = "WLRM-v0",
        truthWatermark = 1,
        builtAtInstant = Fixtures.at.instantEpochMillis,
        inputCurriculumVersion = 1,
        payload = mapOf(
            "state" to state, "skill_logical_id" to "skill.python.loops", "skill_version" to "1",
            "last_attribution_outcome" to "objective_weakness_supported", "last_failure_rule" to "failure.clean_premastery_h0_direct",
            "verification_open" to "0", "signal_evidence_ids" to "1", "first_seen_on_study_day" to "2026-10-01",
            "last_seen_on_study_day" to "2026-10-01", "resolution_evidence_id" to "", "as_of_study_day" to "2026-10-01",
        ),
    )

    private fun SqlitePersistence.evidenceWithObjective(outcome: String = "negative"): Long {
        val id = appendTruth(Fixtures.evidence(skill = "skill.python.loops", outcome = outcome))
        appendTruth(TruthRecord("evidence_event_objective", Fixtures.at, mapOf("evidence_event_id" to id.toString(),
            "objective_logical_id" to objective.logicalId, "objective_version" to "1")))
        return id
    }

    private fun SqlitePersistence.dispose(id: Long, disposition: String, reason: String, at: StudyTimestamp = Fixtures.at) =
        appendTruth(TruthRecord("evidence_disposition", at, mapOf("evidence_event_id" to id.toString(), "disposition" to disposition,
            "reason_code" to reason, "decided_by" to "deterministic_rule")))

    @Test
    fun `migrating a populated schema-5 database completes weakness_state and preserves every truth row`() {
        val file = Fixtures.tempDb()
        Fixtures.populated(file, version = 5)
        val before = SqlitePersistence.openWithoutMigrating(file.absolutePath).use { db ->
            db.appendTruth(Fixtures.session())
            Fixtures.truthContent(db)
        }
        SqlitePersistence.open(file.absolutePath).use { db ->
            // Narrowed at 13F: a later version (v7, `diagnostic_coverage`) migrates through this one.
            assertTrue(Schema.VERSION >= 6)
            assertEquals(Schema.VERSION, db.query("SELECT schema_version FROM schema_metadata") { it.getLong(0).toInt() }.single())
            val after = Fixtures.truthContent(db)
            Schema.truthTables.forEach { assertEquals(before[it], after[it], it) }
            val columns = db.query("PRAGMA table_info(weakness_state)") { it.getText(1) }
            listOf("skill_logical_id", "last_attribution_outcome", "last_failure_rule", "verification_open", "signal_evidence_ids",
                "first_seen_on_study_day", "resolution_evidence_id").forEach { assertTrue(it in columns, it) }
            assertEquals(listOf("weakness_by_skill"),
                db.query("SELECT name FROM sqlite_master WHERE type = 'index' AND name = 'weakness_by_skill'") { it.getText(0) })
        }
    }

    @Test
    fun `weakness_state holds only the WLRM-v0 lifecycle, on insert, upsert and update`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            val insert = assertFailsWith<Exception> { db.writeProjection(weakness("failed")) }
            assertTrue("WLRM-v0 signal lifecycle" in insert.message.orEmpty(), insert.message)
            db.writeProjection(weakness("supported"))
            assertFailsWith<Exception> { db.writeProjection(weakness("bad_at_python")) }
            val update = assertFailsWith<Exception> { db.execute("UPDATE weakness_state SET state = 'bad_at_python'") }
            assertTrue("WLRM-v0 signal lifecycle" in update.message.orEmpty(), update.message)
            assertEquals("supported", db.readProjection("weakness_state:${objective.logicalId}@v1")!!.payload["state"])
        }
    }

    @Test
    fun `a disposition is read back by the store, the newest decides, and the row itself never changes`() {
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use { db ->
            val contaminated = db.evidenceWithObjective()
            val keyError = db.evidenceWithObjective()
            val contested = db.evidenceWithObjective()
            val reinstated = db.evidenceWithObjective()
            db.dispose(contaminated, "invalidated", "prerequisite_contaminated")
            db.dispose(keyError, "invalidated", "answer_key_error")
            db.dispose(contested, "contested", "user_report")
            db.dispose(reinstated, "invalidated", "answer_key_error")
            db.dispose(reinstated, "reinstated", "appeal_upheld")
            val rows = db.evidenceFor(objective).associateBy { it.id }
            assertFalse(rows.getValue(contaminated).prerequisiteValid)
            assertEquals(EvaluatorStatus.INVALID, rows.getValue(keyError).evaluatorStatus)
            assertTrue(rows.getValue(contested).contested)
            assertEquals(EvaluatorStatus.VERIFIED, rows.getValue(reinstated).evaluatorStatus)
            assertTrue(rows.getValue(reinstated).prerequisiteValid)
            // The stored row is what was recorded; only the reading changed.
            assertEquals(listOf("verified"), db.query("SELECT evaluator_status FROM evidence_event WHERE id = $keyError") { it.getText(0) })
        }
    }
}
