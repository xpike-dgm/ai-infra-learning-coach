package coach.application

import coach.model.ComponentResult
import coach.model.EvaluationResult
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.IndependenceClass
import coach.model.OutcomeSignal
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * Turning a recorded attempt into evidence (12A).
 *
 * This is the step 11B deliberately refused to take: the runner records what happened, and only
 * here is it interpreted. The interpretation is still not a mastery decision — that is the engine's
 * — it is the honest translation of "this attempt, evaluated this way, with this much help" into
 * the four independent axes `DDM-v0` stores.
 *
 * Three rules do the work:
 *
 * - **A pending evaluation writes nothing.** `AIAX-v0` is explicit that a refusal, a timeout or an
 *   unusable answer is not a wrong answer; there is no code path here that turns one into evidence.
 * - **The axes are never collapsed.** Outcome, evaluator status, independence and contested are
 *   written separately, so a provisional result can never be read as a settled one.
 * - **Nothing is inferred about the learner.** Independence comes from the assistance that was
 *   actually recorded, not from how the answer looks.
 */
class RecordEvidence(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    data class Recorded(val evidenceIds: List<Long>)

    /**
     * Records one evidence row per targeted Objective of a submitted attempt, in one transaction.
     *
     * [independence] is what the runner observed (11B): `independent` only when no help was taken.
     * A caller cannot pass an evaluation that produced no answer — [EvaluationResult.EvaluationPending]
     * returns no rows at all.
     */
    fun record(
        attemptId: Long,
        skill: VersionedRef,
        evidenceType: String,
        evaluation: EvaluationResult,
        independence: IndependenceClass,
        artifactId: Long? = null,
        resource: VersionedRef? = null,
        variantFamilyId: String? = null,
        prerequisiteSnapshot: String? = null,
    ): Recorded {
        val components: List<ComponentResult> = when (evaluation) {
            is EvaluationResult.Verified -> evaluation.componentResults
            is EvaluationResult.Provisional -> evaluation.componentResults
            // Nothing was measured, so nothing is written. This is neither a pass nor a fail.
            is EvaluationResult.EvaluationPending -> return Recorded(emptyList())
        }
        val evaluatorStatus = when (evaluation) {
            is EvaluationResult.Verified -> EvaluatorStatus.VERIFIED
            is EvaluationResult.Provisional -> EvaluatorStatus.PROVISIONAL
            is EvaluationResult.EvaluationPending -> EvaluatorStatus.INVALID
        }
        val evaluatorRef = when (evaluation) {
            is EvaluationResult.Verified -> evaluation.evaluatorRef
            is EvaluationResult.Provisional -> evaluation.evaluatorRef
            is EvaluationResult.EvaluationPending -> null
        }

        val at = clock.now()
        val ids = persistence.inTransaction {
            components.map { component ->
                val evidenceId = persistence.appendTruth(
                    TruthRecord(
                        "evidence_event", at,
                        buildMap {
                            put("skill_logical_id", skill.logicalId)
                            put("skill_version", skill.version.toString())
                            put("evidence_type", evidenceType)
                            put("source_attempt_id", attemptId.toString())
                            put("outcome", outcomeOf(component.signal).id)
                            put("evaluator_status", evaluatorStatus.id)
                            put("independence_class", independence.id)
                            put("contested", "0")
                            qualityOf(component.signal)?.let { put("correctness_or_rubric_result", it.toString()) }
                            artifactId?.let { put("artifact_id", it.toString()) }
                            resource?.let {
                                put("resource_logical_id", it.logicalId)
                                put("resource_version", it.version.toString())
                            }
                            variantFamilyId?.let { put("variant_family_id", it) }
                            prerequisiteSnapshot?.let { put("prerequisite_snapshot", it) }
                            evaluatorRef?.let {
                                put("evaluator", "${it.provider}/${it.model}@${it.promptOrSchemaVersion}")
                            }
                        },
                    )
                )
                // The targeted Objective keeps its own version pin, in its own row (DDM-v0).
                persistence.appendTruth(
                    TruthRecord(
                        "evidence_event_objective", at,
                        mapOf(
                            "evidence_event_id" to evidenceId.toString(),
                            "objective_logical_id" to component.objectiveRef.logicalId,
                            "objective_version" to component.objectiveRef.version.toString(),
                        ),
                    )
                )
                evidenceId
            }
        }
        return Recorded(ids)
    }

    /**
     * `NOT_RELIABLY_MEASURED` is `invalid`, not `negative`: the difference between "they got it
     * wrong" and "we could not tell" is the whole reason `DDM-v0` keeps `outcome` separate from
     * `evaluator_status`.
     */
    private fun outcomeOf(signal: OutcomeSignal): EvidenceOutcome = when (signal) {
        OutcomeSignal.MET -> EvidenceOutcome.POSITIVE
        OutcomeSignal.PARTIALLY_MET -> EvidenceOutcome.PARTIAL
        OutcomeSignal.NOT_MET -> EvidenceOutcome.NEGATIVE
        OutcomeSignal.NOT_RELIABLY_MEASURED -> EvidenceOutcome.INVALID
    }

    /**
     * The group result `GRE-v0` §4 calls `q_g`. The three usable signals map to the ends and the
     * middle of `[0,1]`; an unmeasurable one has no result at all, and the engine excludes it rather
     * than scoring it as zero.
     */
    private fun qualityOf(signal: OutcomeSignal): Double? = when (signal) {
        OutcomeSignal.MET -> 1.0
        OutcomeSignal.PARTIALLY_MET -> 0.5
        OutcomeSignal.NOT_MET -> 0.0
        // No result at all, rather than a zero: a zero is a thing the learner did, and this is not.
        OutcomeSignal.NOT_RELIABLY_MEASURED -> null
    }
}
