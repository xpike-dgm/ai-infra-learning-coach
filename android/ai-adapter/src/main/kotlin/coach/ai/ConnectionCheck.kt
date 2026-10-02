package coach.ai

import coach.model.PendingReason

/** What the learner's "test the connection" found (14G). Nothing about any learner is sent to find it. */
enum class ConnectionResult(val id: String) {
    WORKS("works"),
    NO_KEY("no_key"),
    KEY_REJECTED("key_rejected"),
    UNREACHABLE("unreachable"),
    TIMED_OUT("timed_out"),
    UNEXPECTED_REPLY("unexpected_reply"),
}

/**
 * The connection check behind the settings screen. It sends a fixed instruction and a fixed word — no task, no answer,
 * no history — through the same client every real call uses, so a check that works means real calls can work.
 */
class ConnectionCheck(private val client: OpenAiResponses) {

    fun run(): ConnectionResult = when (val outcome = client.call(TaskClass.TUTOR_HELP, INSTRUCTIONS, SCHEMA_NAME, SCHEMA, MESSAGE)) {
        is ProviderOutcome.Delivered -> if (outcome.json.fields.keys == setOf("ok") && outcome.json["ok"] == JsonValue.Bool(true)) ConnectionResult.WORKS else ConnectionResult.UNEXPECTED_REPLY
        is ProviderOutcome.NotDelivered -> when {
            outcome.keyRejected -> ConnectionResult.KEY_REJECTED
            outcome.reason == PendingReason.UNAVAILABLE -> ConnectionResult.NO_KEY
            outcome.reason == PendingReason.TIMED_OUT -> ConnectionResult.TIMED_OUT
            outcome.reason == PendingReason.TRANSPORT_ERROR -> ConnectionResult.UNREACHABLE
            else -> ConnectionResult.UNEXPECTED_REPLY
        }
    }

    companion object {
        const val INSTRUCTIONS = "This is a connection check. Reply only with the JSON object {\"ok\": true}."
        const val MESSAGE = "ping"
        const val SCHEMA_NAME = "connection_check_1"
        const val SCHEMA = "{\"type\":\"object\",\"additionalProperties\":false,\"required\":[\"ok\"],\"properties\":{\"ok\":{\"type\":\"boolean\"}}}"
    }
}
