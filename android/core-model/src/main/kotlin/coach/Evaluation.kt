package coach.model

/**
 * AIAX-v0 outcome taxonomy. A refusal is not a wrong answer: every non-answer degrades to
 * [EvaluationPending] and writes no evidence, so a safety filter firing on the learner's own
 * code sample can never become negative evidence about them.
 */
sealed interface EvaluationResult {

    /** Deterministically verified. May carry full evidence weight. */
    data class Verified(
        val componentResults: List<ComponentResult>,
        val evaluatorRef: EvaluatorRef,
        /**
         * `AIAX-v0` §5.1 `misconception_hypotheses[]` (14B). From a deterministic path — an answer key mapping a
         * chosen option to a catalog label, a failing test that names one — they may grow as the Objective's own
         * evidence allows; they are still only proposals until the catalog and the evidence policy say otherwise.
         */
        val misconceptionHypotheses: List<MisconceptionHypothesis> = emptyList(),
    ) : EvaluationResult

    /**
     * An uncalibrated LLM judgement. It may inform and may open a confirmation need; it can
     * never pass a critical mastery gate on its own (AIAX-v0, AIV-v0 §18).
     */
    data class Provisional(
        val componentResults: List<ComponentResult>,
        val evaluatorRef: EvaluatorRef,
        /** An uncalibrated evaluator's proposals (14B): recorded as `ai_proposed`, never more than a hypothesis. */
        val misconceptionHypotheses: List<MisconceptionHypothesis> = emptyList(),
    ) : EvaluationResult

    /**
     * No usable answer. Neither pass nor fail, and no evidence is written.
     * The [reason] is recorded for diagnostics only — it never becomes a verdict.
     */
    data class EvaluationPending(val reason: PendingReason) : EvaluationResult
}

/** The five ways an evaluation can fail to produce an answer (AIAX-v0). */
enum class PendingReason {
    REFUSED,
    TIMED_OUT,
    TRANSPORT_ERROR,
    INVALID_RESPONSE,
    UNAVAILABLE,
}

data class ComponentResult(
    val objectiveRef: VersionedRef,
    val signal: OutcomeSignal,
)

/** A signal about one component. It is an input to the evidence pipeline, never a mastery verdict. */
enum class OutcomeSignal {
    MET,
    PARTIALLY_MET,
    NOT_MET,
    NOT_RELIABLY_MEASURED,
}

/**
 * AIAX-v0: every AI-derived evidence row records who produced it, so a past evaluation stays
 * explicable and a model change can be reasoned about later.
 */
data class EvaluatorRef(
    val provider: String,
    val model: String,
    val promptOrSchemaVersion: String,
)
