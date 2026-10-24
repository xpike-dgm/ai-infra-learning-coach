package coach.persistence

import coach.model.AssistanceLevel
import coach.model.StudyTimestamp
import coach.model.VersionedRef
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * What the learning history reads from the real store (16B, `LHRX-v0`): an evidence row's attempt and the assistance
 * levels recorded on it, every plan version and every assessment session, in the store's own order. Only levels are
 * read — no conversation text exists to read (user decision).
 */
class HistoryStorageTest {

    private val at = StudyTimestamp(1_790_000_000_000, "2026-10-01", 3 * 3600)
    private val objective = VersionedRef("objective.c.pointers.write_through", 1)

    private fun <T> withStore(block: (SqlitePersistence) -> T): T =
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use(block)

    private fun SqlitePersistence.attempt(sessionId: Long? = null): Long = inTransaction { appendTruth(Fixtures.attempt(sessionId)) }

    private fun SqlitePersistence.assistance(attemptId: Long, level: String) = inTransaction {
        appendTruth(TruthRecord("assistance_event", at, mapOf(
            "attempt_id" to attemptId.toString(), "level" to level, "timing" to "during_attempt",
            "target_scope" to "target_objective", "source" to "deterministic_content", "requested_by_user" to "1",
        )))
    }

    private fun SqlitePersistence.evidence(attemptId: Long?): Long = inTransaction {
        val id = appendTruth(TruthRecord("evidence_event", at, buildMap {
            put("skill_logical_id", "skill.c.pointers"); put("skill_version", "1"); put("evidence_type", "coding")
            attemptId?.let { put("source_attempt_id", it.toString()) }
            put("outcome", "positive"); put("evaluator_status", "verified"); put("independence_class", "assisted"); put("contested", "0")
        }))
        appendTruth(TruthRecord("evidence_event_objective", at,
            mapOf("evidence_event_id" to id.toString(), "objective_logical_id" to objective.logicalId, "objective_version" to "1")))
        id
    }

    @Test
    fun `an evidence row carries its attempt and the distinct assistance levels recorded on it, in order`() {
        withStore { db ->
            val attempt = db.attempt()
            db.assistance(attempt, "H3")
            db.assistance(attempt, "H1")
            db.assistance(attempt, "H3")
            db.evidence(attempt)
            db.evidence(null)
            val rows = db.evidenceFor(objective)
            assertEquals(attempt, rows[0].attemptId)
            assertEquals(listOf(AssistanceLevel.H1, AssistanceLevel.H3), rows[0].assistanceLevels)
            assertNull(rows[1].attemptId)
            assertEquals(emptyList(), rows[1].assistanceLevels)
        }
    }

    @Test
    fun `every plan version and every assessment session read back oldest first`() {
        withStore { db ->
            val plans = (1..3).map { n -> db.inTransaction { db.appendTruth(TruthRecord("plan_version", at, mapOf("policy_version" to "p$n"))) } }
            assertEquals(plans, db.planVersions().map { it.planVersionId })
            assertEquals(plans.last(), db.latestPlan()?.planVersionId)
            // A weekly or monthly row must carry its blueprint (13B's trigger); daily rows keep this about order.
            val sessions = listOf("daily", "daily", "daily").map { scope ->
                db.inTransaction { db.appendTruth(TruthRecord("assessment_session", at, mapOf("scope" to scope))) }
            }
            assertEquals(sessions, db.assessmentSessions().map { it.id })
            assertEquals(listOf("daily", "daily", "daily"), db.assessmentSessions().map { it.record.payload["scope"] })
        }
    }

    @Test
    fun `reading the history's inputs writes nothing`() {
        withStore { db ->
            db.evidence(db.attempt())
            val before = Fixtures.truthContent(db)
            db.evidenceFor(objective); db.planVersions(); db.assessmentSessions()
            assertTrue(before == Fixtures.truthContent(db))
        }
    }
}
