package coach.application

import coach.model.AiCall
import coach.model.AiCallRecord
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * The AI calls the app made, counted (17E, `D-138`).
 *
 * [record] appends one row per call that left the device: what it was for, the model, how it ended, its attempts and the
 * tokens the provider reported — a token count the provider did not report is left empty, never written as zero. Nothing
 * the call carried is stored. [read] only reads.
 *
 * It owns no learning truth: a call's outcome is not evidence (the evaluator's result is, through its own pipeline), and
 * the count is not progress.
 */
class AiUsage(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun read(): List<AiCallRecord> = persistence.aiCalls()

    fun record(call: AiCall) {
        val at = clock.now()
        val payload = buildMap {
            put("call_use", call.use.id)
            put("model", call.model)
            put("outcome", call.outcome.id)
            put("attempts", call.attempts.toString())
            call.inputTokens?.let { put("input_usage", it.toString()) }
            call.outputTokens?.let { put("output_usage", it.toString()) }
        }
        persistence.inTransaction { persistence.appendTruth(TruthRecord(KIND, at, payload)) }
    }

    companion object {
        const val KIND = "ai_call_record"
    }
}
