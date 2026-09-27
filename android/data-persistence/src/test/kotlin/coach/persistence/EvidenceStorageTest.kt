package coach.persistence

import coach.model.DifficultyClass
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.IndependenceClass
import coach.model.StudyTimestamp
import coach.model.VersionedRef
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Evidence and mastery projections against real SQLite (12A, `TVSX-v0` tier T2).
 *
 * `data-persistence` may not depend on `core-application`, so the proof is split exactly as 11B
 * split it: the use case's shape is a T1 check with a recording store, and the guarantees that need
 * the real engine live here — that an evidence row and its Objective attribution commit together,
 * that an Objective sees only evidence pinned to its own version, and that a projection carries the
 * provenance that makes a stale one detectable.
 */
class EvidenceStorageTest {

    private val at = StudyTimestamp(1_789_000_000_000, "2026-09-21", 3 * 3600)
    private val skill = VersionedRef("skill.python.loops", 1)
    private val objective = VersionedRef("objective.python.loops.trace", 1)
    private val objectiveV2 = VersionedRef("objective.python.loops.trace", 2)

    private fun <T> withStore(block: (SqlitePersistence) -> T): T =
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use(block)

    private fun evidenceRecord(
        attemptId: Long,
        outcome: String = "positive",
        evaluatorStatus: String = "verified",
        independence: String = "independent",
        quality: String? = "1.0",
        family: String? = "family.a",
        difficulty: String? = "authentic_application",
        contested: String = "0",
        prerequisiteSnapshot: String? = null,
    ) = TruthRecord(
        "evidence_event", at,
        buildMap {
            put("skill_logical_id", skill.logicalId)
            put("skill_version", skill.version.toString())
            put("evidence_type", "code_reading")
            put("source_attempt_id", attemptId.toString())
            put("outcome", outcome)
            put("evaluator_status", evaluatorStatus)
            put("independence_class", independence)
            put("contested", contested)
            quality?.let { put("correctness_or_rubric_result", it) }
            family?.let { put("variant_family_id", it) }
            difficulty?.let { put("difficulty", it) }
            prerequisiteSnapshot?.let { put("prerequisite_snapshot", it) }
        },
    )

    private fun attributionRecord(evidenceId: Long, target: VersionedRef) = TruthRecord(
        "evidence_event_objective", at,
        mapOf(
            "evidence_event_id" to evidenceId.toString(),
            "objective_logical_id" to target.logicalId,
            "objective_version" to target.version.toString(),
        ),
    )

    private fun SqlitePersistence.record(
        target: VersionedRef = objective,
        outcome: String = "positive",
        evaluatorStatus: String = "verified",
        independence: String = "independent",
        quality: String? = "1.0",
        family: String? = "family.a",
        difficulty: String? = "authentic_application",
        contested: String = "0",
        prerequisiteSnapshot: String? = null,
    ): Long = inTransaction {
        val attemptId = appendTruth(Fixtures.attempt())
        val evidenceId = appendTruth(
            evidenceRecord(attemptId, outcome, evaluatorStatus, independence, quality, family, difficulty,
                contested, prerequisiteSnapshot)
        )
        appendTruth(attributionRecord(evidenceId, target))
        evidenceId
    }

    @Test
    fun `an evidence row reads back with its four axes and its group result intact`() {
        withStore { db ->
            db.record(outcome = "partial", evaluatorStatus = "provisional", independence = "assisted",
                quality = "0.5", contested = "1", difficulty = "basic")
            val row = db.evidenceFor(objective).single()
            assertEquals(EvidenceOutcome.PARTIAL, row.outcome)
            assertEquals(EvaluatorStatus.PROVISIONAL, row.evaluatorStatus)
            assertEquals(IndependenceClass.ASSISTED, row.independenceClass)
            assertTrue(row.contested)
            assertEquals(0.5, row.quality)
            assertEquals(DifficultyClass.BASIC, row.difficulty)
            assertEquals("family.a", row.variantFamilyId)
            assertEquals(skill, row.skill)
        }
    }

    @Test
    fun `an unmeasurable row has no group result rather than a zero`() {
        withStore { db ->
            db.record(outcome = "invalid", quality = null)
            assertNull(db.evidenceFor(objective).single().quality)
        }
    }

    @Test
    fun `an Objective sees only evidence pinned to its own version, oldest first`() {
        withStore { db ->
            db.record(target = objective, family = "family.a")
            db.record(target = objectiveV2, family = "family.b")
            db.record(target = objective, family = "family.c")

            val rows = db.evidenceFor(objective)
            assertEquals(2, rows.size)
            assertEquals(listOf("family.a", "family.c"), rows.map { it.variantFamilyId })
            assertTrue(rows[0].sequence < rows[1].sequence)
            assertEquals(1, db.evidenceFor(objectiveV2).size)
            assertEquals(0, db.evidenceFor(VersionedRef("objective.unknown", 1)).size)
        }
    }

    @Test
    fun `a contaminated prerequisite snapshot is carried through to the engine's input`() {
        withStore { db ->
            db.record(prerequisiteSnapshot = SqlitePersistence.CONTAMINATED)
            assertEquals(false, db.evidenceFor(objective).single().prerequisiteValid)

            db.record(family = "family.b", prerequisiteSnapshot = "eligible")
            assertTrue(db.evidenceFor(objective).last().prerequisiteValid)
        }
    }

    @Test
    fun `an evidence row and its attribution commit together or not at all`() {
        withStore { db ->
            val attemptId = db.inTransaction { db.appendTruth(Fixtures.attempt()) }
            runCatching {
                db.inTransaction {
                    db.appendTruth(evidenceRecord(attemptId))
                    error("crashed before the Objective attribution was written")
                }
            }
            assertEquals(0, db.count("evidence_event"))
            assertEquals(0, db.count("evidence_event_objective"))
            assertEquals(0, db.evidenceFor(objective).size)
        }
    }

    @Test
    fun `an attribution cannot name an evidence row that does not exist`() {
        withStore { db ->
            val refused = runCatching { db.inTransaction { db.appendTruth(attributionRecord(999, objective)) } }
            assertTrue(refused.isFailure, "an orphan attribution was accepted")
            assertEquals(0, db.count("evidence_event_objective"))
        }
    }

    @Test
    fun `the watermark advances with every truth row and stamps the projection`() {
        withStore { db ->
            assertEquals(0, db.truthWatermark())
            db.record()
            val watermark = db.truthWatermark()
            assertTrue(watermark >= 3, "three truth rows did not advance the watermark")

            db.writeProjection(
                ProjectionRecord(
                    key = "skill_state:${skill.logicalId}@v1",
                    policyVersion = "GRE-v0",
                    truthWatermark = watermark,
                    builtAtInstant = at.instantEpochMillis,
                    inputCurriculumVersion = 1,
                    payload = mapOf(
                        "mastery_axis_state" to "confirmed_current",
                        "retention_axis_state" to "not_yet_evaluated",
                        "prerequisite_axis_state" to "not_yet_evaluated",
                        "weakness_axis_state" to "not_yet_evaluated",
                        "primary_presentation_state" to "confirmed_current",
                    ),
                )
            )
            val row = assertNotNull(db.readProjection("skill_state:${skill.logicalId}@v1"))
            assertEquals("GRE-v0", row.policyVersion)
            assertEquals(watermark, row.truthWatermark)
            assertEquals(1, row.inputCurriculumVersion)

            // A later write replaces the row: projections are rebuildable, not appended.
            db.record(family = "family.b")
            db.writeProjection(row.copy(truthWatermark = db.truthWatermark(), payload = row.payload))
            assertEquals(db.truthWatermark(), assertNotNull(db.readProjection(row.key)).truthWatermark)
            assertEquals(1, db.count("skill_state"))
        }
    }

    @Test
    fun `the newest published curriculum version is what a projection pins to`() {
        withStore { db ->
            assertNull(db.latestCurriculumVersion())
            db.execute("INSERT INTO curriculum_version (version, source_refs, provenance) VALUES (1, 's', 'a')")
            db.execute("INSERT INTO curriculum_version (version, source_refs, provenance) VALUES (2, 's', 'a')")
            assertEquals(2, db.latestCurriculumVersion())
        }
    }

    @Test
    fun `reading evidence writes nothing`() {
        withStore { db ->
            db.record()
            val watermark = db.truthWatermark()
            db.evidenceFor(objective)
            assertEquals(watermark, db.truthWatermark())
            assertEquals(1, db.count("evidence_event"))
        }
    }
}
