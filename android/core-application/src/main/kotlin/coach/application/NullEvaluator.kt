package coach.application

import coach.model.EvaluationResult
import coach.model.PendingReason
import coach.ports.EvaluationRequest
import coach.ports.EvaluatorPort

/**
 * Ships with the product. This is not a test fixture (MSBX-v0 §ai_absence): it is the
 * implementation the app uses when no AI adapter is present, and it is what makes V1
 * criterion 8 a property of the wiring rather than a hope.
 *
 * An open-ended attempt becomes evaluation_pending and writes no evidence. Nothing
 * deterministic degrades: planning, answer-key scoring, mastery, retention, readiness,
 * progress and history are unaffected.
 */
object NullEvaluator : EvaluatorPort {
    override fun evaluate(request: EvaluationRequest): EvaluationResult =
        EvaluationResult.EvaluationPending(PendingReason.UNAVAILABLE)
}
