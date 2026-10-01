package coach.application

import coach.model.AssessmentItem
import coach.model.CodeEvaluation
import coach.model.CodeEvaluationRoute
import coach.model.CodeNotMeasured
import coach.model.CodeTestReports
import coach.model.CodeVerdict
import coach.model.EvaluationResult
import coach.ports.ContentPort
import coach.ports.EvaluationRequest
import coach.ports.EvaluatorPort

/**
 * Evaluating one code submission (14D, `CDEX-v0 / D-108`).
 *
 * The course's tests decide wherever they exist: the learner runs them on their own computer and pastes the runner's
 * report (user decision), and no AI is asked for evidence about that task. Where no tests exist, an AI may judge the
 * code only if the task allows a provisional result (user decision); a task that needs a verified result waits for its
 * tests. Only the task's text and the learner's code leave the device, and only on that last path (`AIAX-v0` §11).
 *
 * This decides what the submission proves; it records nothing. The caller hands a [CodeVerdict.Measured] result to the
 * evidence pipeline (`RecordEvidence`, `RecordSlotEvidence`) like any other evaluation, and a
 * [CodeVerdict.NotMeasured] writes nothing at all.
 */
class EvaluateCode(
    private val content: ContentPort,
    private val evaluator: EvaluatorPort,
) {
    /** [code] is what the learner wrote; [report] the runner's output they pasted, if the task has tests. */
    fun evaluate(item: AssessmentItem, code: String, report: String?): CodeVerdict {
        val suite = content.codeTestsFor(item.ref)
        return when (CodeEvaluation.route(item, suite)) {
            CodeEvaluationRoute.TESTS -> when {
                report.isNullOrBlank() -> CodeVerdict.NotMeasured(CodeNotMeasured.REPORT_MISSING)
                else -> when (val decoded = CodeTestReports.decode(report)) {
                    is CodeTestReports.Decoded.Malformed -> CodeVerdict.NotMeasured(CodeNotMeasured.REPORT_MALFORMED, decoded.reasons)
                    is CodeTestReports.Decoded.Ok -> CodeEvaluation.evaluate(item, suite!!, decoded.report)
                }
            }
            // A report for a task with no tests cannot be checked against anything, so it is not used.
            CodeEvaluationRoute.TESTS_REQUIRED -> CodeVerdict.NotMeasured(
                if (report.isNullOrBlank()) CodeNotMeasured.TESTS_REQUIRED else CodeNotMeasured.NO_SUITE_FOR_REPORT
            )
            CodeEvaluationRoute.AI_PROVISIONAL -> provisional(item, code, report)
        }
    }

    private fun provisional(item: AssessmentItem, code: String, report: String?): CodeVerdict {
        if (!report.isNullOrBlank()) return CodeVerdict.NotMeasured(CodeNotMeasured.NO_SUITE_FOR_REPORT)
        // An empty submission is a skip, and a skip is not a wrong answer (`ASUX-v0`): nothing is sent.
        if (code.isBlank()) return CodeVerdict.NotMeasured(CodeNotMeasured.NOTHING_SUBMITTED)
        val task = content.resource(item.ref)?.body?.takeIf { it.isNotBlank() }
            ?: return CodeVerdict.NotMeasured(CodeNotMeasured.TASK_TEXT_MISSING)
        return when (val result = CodeEvaluation.acceptAi(item, evaluator.evaluate(EvaluationRequest(item.targetObjectives, task, code)))) {
            is EvaluationResult.EvaluationPending -> CodeVerdict.NotMeasured(CodeNotMeasured.EVALUATOR_DID_NOT_ANSWER, pending = result.reason)
            else -> CodeVerdict.Measured(result)
        }
    }
}
