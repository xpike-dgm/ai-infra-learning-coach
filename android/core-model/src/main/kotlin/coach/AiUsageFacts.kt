package coach.model

/**
 * What the app sent to an AI provider, counted (17E, `D-138`) — never what was said.
 *
 * A call is recorded only when something left the device: without the learner's key nothing is sent, and nothing is
 * recorded. A record holds what the call was for, the model that was asked, how it ended, how many attempts it took and
 * the token counts **the provider itself reported**; when the provider reported none, they are unknown, never zero.
 * There is no instruction, message, reply, schema or key here, and no field to put one in (`AIAX-v0` §11).
 *
 * There is no price either (user decision, 2026-10-27): the provider's reply carries tokens, not money, and a price
 * table the app does not have is not invented.
 */
enum class AiCallUse(val id: String) {
    TUTOR_HELP("tutor_help"),
    OPEN_RESPONSE_EVALUATION("open_response_evaluation"),
    CODE_EVALUATION("code_evaluation"),
    CONNECTION_CHECK("connection_check"),
    ;

    companion object {
        fun of(id: String): AiCallUse? = entries.firstOrNull { it.id == id }
    }
}

/** How a call ended, as the adapter read it (`AIAX-v0` §6). Only [ANSWERED] returned an answer. */
enum class AiCallOutcome(val id: String) {
    ANSWERED("answered"),
    REFUSED("refused"),
    INVALID_RESPONSE("invalid_response"),
    TIMED_OUT("timed_out"),
    TRANSPORT_ERROR("transport_error"),
    KEY_REJECTED("key_rejected"),
    ;

    companion object {
        fun of(id: String): AiCallOutcome? = entries.firstOrNull { it.id == id }
    }
}

data class AiCall(
    val use: AiCallUse,
    val model: String,
    val outcome: AiCallOutcome,
    val attempts: Int,
    /** The provider's own count; `null` when it reported none. */
    val inputTokens: Long?,
    val outputTokens: Long?,
) {
    init {
        require(model.isNotBlank()) { "a call names the model it asked" }
        require(attempts >= 1) { "a recorded call reached the provider at least once" }
        require((inputTokens ?: 0) >= 0 && (outputTokens ?: 0) >= 0) { "a token count cannot be below zero" }
    }
}

/** One stored call, with the study day it was made on (the row's own day, never recomputed from its instant). */
data class AiCallRecord(val call: AiCall, val studyDay: String, val sequence: Long)
