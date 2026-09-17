package coach.ai

import coach.model.EvaluationResult
import coach.model.EvaluatorAvailability
import coach.model.PendingReason
import coach.ports.EvaluationRequest
import coach.ports.EvaluatorPort

/**
 * Optional module. The product must build and run without it (MSBX-v0 §ai_absence), which is
 * why nothing outside app-wiring may reference this type.
 *
 * Behaviour is fixed by AIAX-v0 / D-079 and is implemented at 14, not here:
 *  - output is schema-constrained; a schema-invalid response is an error, never a verdict,
 *  - no verdict may be parsed out of free text,
 *  - the stop reason is inspected before the content is read,
 *  - refusal, timeout, transport error, invalid response and unavailability all degrade to
 *    EvaluationPending and write no evidence,
 *  - the timeout budget is end-to-end across retries, not per call,
 *  - only the minimum content for the current attempt may leave the device,
 *  - the key lives in platform secure storage and never appears in logs, exports or backups.
 *
 * Until those call sites exist the adapter is **unavailable**, and it says so the way AIAX-v0
 * says every non-answer is said. It used to throw here; in the default build that made the
 * first open-ended attempt a crash — exactly the "core fails because AI is absent" outcome V1
 * criterion 8 rules out (APHX-v0).
 */
class AiEvaluator : EvaluatorPort {
    val availability: EvaluatorAvailability = EvaluatorAvailability.UNAVAILABLE

    override fun evaluate(request: EvaluationRequest): EvaluationResult =
        EvaluationResult.EvaluationPending(PendingReason.UNAVAILABLE)
}
