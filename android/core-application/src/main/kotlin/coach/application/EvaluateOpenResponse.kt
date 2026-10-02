package coach.application

import coach.model.AssessmentItem
import coach.model.AssistanceLevel
import coach.model.EvaluationResult
import coach.model.OpenResponse
import coach.model.OpenResponseNotMeasured
import coach.model.OpenResponseRoute
import coach.model.OpenResponseVerdict
import coach.model.RubricCriterion
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.EvaluationRequest
import coach.ports.EvaluatorPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * Evaluating one open response (14F, `OREX-v0 / D-110`).
 *
 * A short answer is judged by the course's accepted answers and no AI is asked; a longer one by its rubric, through the
 * evaluator, one criterion at a time — only where the task allows a provisional result. This decides what the response
 * proves and records no evidence: the caller hands an [OpenResponseVerdict.Measured] result to the evidence pipeline,
 * and a [OpenResponseVerdict.NotMeasured] writes nothing. **Nothing here retries**: a response that is still waiting is
 * evaluated again only when the learner asks (user decision; `AIAX-v0` §7.1).
 */
class EvaluateOpenResponse(
    private val content: ContentPort,
    private val persistence: PersistencePort,
    private val clock: ClockPort,
    private val evaluator: EvaluatorPort,
) {
    fun evaluate(item: AssessmentItem, response: String): OpenResponseVerdict {
        val key = content.answerKeyFor(item.ref)
        val rubric = content.rubricFor(item.ref)
        return when (OpenResponse.route(item, key, rubric)) {
            OpenResponseRoute.ANSWER_KEY -> OpenResponse.byKey(item, key!!, response)
            OpenResponseRoute.NOTHING_TO_JUDGE_BY -> OpenResponseVerdict.NotMeasured(OpenResponseNotMeasured.NOTHING_TO_JUDGE_BY)
            OpenResponseRoute.VERIFIED_EVALUATOR_REQUIRED -> OpenResponseVerdict.NotMeasured(OpenResponseNotMeasured.VERIFIED_EVALUATOR_REQUIRED)
            OpenResponseRoute.AI_RUBRIC -> {
                // An empty answer is a skip, and a skip is not a wrong answer (`ASUX-v0`): nothing is sent.
                if (response.isBlank()) return OpenResponseVerdict.NotMeasured(OpenResponseNotMeasured.NOTHING_SUBMITTED)
                val task = content.resource(item.ref)?.body?.takeIf { it.isNotBlank() }
                    ?: return OpenResponseVerdict.NotMeasured(OpenResponseNotMeasured.TASK_TEXT_MISSING)
                val catalog = item.targetObjectives.flatMap { persistence.misconceptionsOf(it) }.map { it.ref.logicalId }.distinct()
                val request = EvaluationRequest(item.targetObjectives, task, response, rubric!!.criteria, catalog)
                when (val result = OpenResponse.acceptAi(item, rubric, evaluator.evaluate(request))) {
                    is EvaluationResult.EvaluationPending ->
                        OpenResponseVerdict.NotMeasured(OpenResponseNotMeasured.EVALUATOR_DID_NOT_ANSWER, pending = result.reason)
                    else -> OpenResponseVerdict.Measured(result)
                }
            }
        }
    }

    /**
     * The rubric, for the learner to check their own submitted answer against while it waits (user decision): practice,
     * never evidence. A rubric says what a correct answer contains, so seeing it is a shown solution — recorded as an
     * exposure exactly like one the tutor wrote (`TUTX-v0`), so this item is not offered later as a fresh measurement.
     * `null` when the item has no rubric.
     */
    fun selfCheck(item: AssessmentItem, attemptId: Long): List<RubricCriterion>? {
        val rubric = content.rubricFor(item.ref) ?: return null
        val at = clock.now()
        persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(
                    "exposure_record", at,
                    mapOf(
                        "resource_logical_id" to item.ref.logicalId,
                        "resource_version" to item.ref.version.toString(),
                        "variant_family_id" to item.variantFamilyId,
                        "exposure_kind" to ServeDailyMicroItem.SOLUTION_EXPOSURE,
                        "max_exposure_level" to AssistanceLevel.H4.id,
                        "source_attempt_id" to attemptId.toString(),
                    ),
                )
            )
        }
        return rubric.criteria
    }
}
