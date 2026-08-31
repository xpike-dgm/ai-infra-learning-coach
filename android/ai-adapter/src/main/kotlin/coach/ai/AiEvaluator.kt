package coach.ai

import coach.model.EvaluationResult
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
 */
class AiEvaluator : EvaluatorPort {
    override fun evaluate(request: EvaluationRequest): EvaluationResult =
        TODO("AI call sites are 14; 10A only fixes the module boundary and the wiring seam.")
}
