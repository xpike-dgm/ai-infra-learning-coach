package coach.persistence

import coach.model.StudyTimestamp
import coach.model.VersionedRef
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A solution shown is read back as an exposure on the evidence it precedes (14A, `TUTX-v0` §16, `TVSX-v0` T2).
 *
 * Before 14A every evidence row came back unexposed, so the H3/H4 disclosure — "the same item or a near
 * variant cannot serve as the independent recheck" — was kept by item selection but not by the engines.
 * These checks run against real SQLite because the order that matters is the store's own sequence.
 */
class SolutionExposureStorageTest {

    private val at = StudyTimestamp(1_790_000_000_000, "2026-10-01", 3 * 3600)
    private val objective = VersionedRef("objective.c.pointers.write_through", 1)

    private fun <T> withStore(block: (SqlitePersistence) -> T): T =
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use(block)

    private fun SqlitePersistence.exposure(resource: String, family: String?, kind: String = "solution_exposure", attemptId: Long? = null) =
        inTransaction {
            appendTruth(
                TruthRecord(
                    "exposure_record", at,
                    buildMap {
                        put("resource_logical_id", resource)
                        put("resource_version", "1")
                        family?.let { put("variant_family_id", it) }
                        put("exposure_kind", kind)
                        put("max_exposure_level", "H4")
                        attemptId?.let { put("source_attempt_id", it.toString()) }
                    },
                )
            )
        }

    private fun SqlitePersistence.attempt(): Long = inTransaction { appendTruth(Fixtures.attempt()) }

    private fun SqlitePersistence.evidence(attemptId: Long, resource: String?, family: String?, version: Int = 1): Long = inTransaction {
        val id = appendTruth(
            TruthRecord(
                "evidence_event", at,
                buildMap {
                    put("skill_logical_id", "skill.c.pointers")
                    put("skill_version", "1")
                    put("evidence_type", "coding")
                    put("source_attempt_id", attemptId.toString())
                    put("outcome", "positive")
                    put("evaluator_status", "verified")
                    put("independence_class", "independent")
                    put("contested", "0")
                    resource?.let {
                        put("resource_logical_id", it)
                        put("resource_version", version.toString())
                    }
                    family?.let { put("variant_family_id", it) }
                },
            )
        )
        appendTruth(
            TruthRecord(
                "evidence_event_objective", at,
                mapOf("evidence_event_id" to id.toString(), "objective_logical_id" to objective.logicalId, "objective_version" to "1"),
            )
        )
        id
    }

    private fun SqlitePersistence.exposed(): List<Boolean> = evidenceFor(objective).map { it.solutionExposed }

    @Test
    fun `an attempt made after a solution was shown for its variant family reads as solution-exposed`() {
        withStore { db ->
            db.exposure("item.c.pointers.q1", "family.write_through")
            db.evidence(db.attempt(), "item.c.pointers.q7", "family.write_through")
            assertEquals(listOf(true), db.exposed())
        }
    }

    @Test
    fun `an attempt on the same item reads as solution-exposed whatever its version`() {
        withStore { db ->
            db.exposure("item.c.pointers.q1", null)
            db.evidence(db.attempt(), "item.c.pointers.q1", "family.other", version = 2)
            assertEquals(listOf(true), db.exposed())
        }
    }

    @Test
    fun `an explanation of a frozen answer does not reach back into that answer`() {
        withStore { db ->
            val attemptId = db.attempt()
            db.exposure("item.c.pointers.q1", "family.write_through", attemptId = attemptId)
            db.evidence(attemptId, "item.c.pointers.q1", "family.write_through")
            assertEquals(listOf(false), db.exposed())
            // The next attempt on that family was made after the solution was shown.
            db.evidence(db.attempt(), "item.c.pointers.q2", "family.write_through")
            assertEquals(listOf(false, true), db.exposed())
        }
    }

    @Test
    fun `an item merely seen, another family or a row without an item is not solution-exposed`() {
        withStore { db ->
            db.exposure("item.c.pointers.q1", "family.write_through", kind = "item_version_seen")
            db.exposure("item.c.pointers.q9", "family.address_of")
            db.evidence(db.attempt(), "item.c.pointers.q1", "family.write_through")
            db.evidence(db.attempt(), null, null)
            assertEquals(listOf(false, false), db.exposed())
        }
    }
}
