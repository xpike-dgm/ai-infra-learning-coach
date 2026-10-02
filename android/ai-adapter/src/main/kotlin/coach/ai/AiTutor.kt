package coach.ai

import coach.model.AssistanceLevel
import coach.model.InstructionMode
import coach.model.PendingReason
import coach.model.TutorContent
import coach.model.TutorInstructions
import coach.model.TutorIntent
import coach.model.TutorRef
import coach.model.TutorReply
import coach.model.TutorRequest
import coach.ports.TutorPort

/**
 * The tutor's call site (14G; `TUTX-v0 / D-105`). Optional module: the product builds, runs and teaches without it
 * (`MSBX-v0` §ai_absence).
 *
 * It sends `TutorInstructions.TEXT`, `TutorInstructions.userMessage(request)` and `TutorInstructions.REPLY_SCHEMA` —
 * built in core, unchanged — and maps only a reply with exactly the schema's four fields to `Delivered`. Whether the
 * reply may be shown is still core's decision (`TutorRules.accept`); this only reads it.
 */
class AiTutor(private val client: OpenAiResponses?, private val provider: String = "openai") : TutorPort {

    /** The build without a client (no adapter configured) is unavailable — never a crash, never a reply. */
    constructor() : this(null)

    override fun assist(request: TutorRequest): TutorReply {
        val client = client ?: return TutorReply.NotDelivered(PendingReason.UNAVAILABLE)
        return when (val outcome = client.call(TaskClass.TUTOR_HELP, TutorInstructions.TEXT, schemaName(TutorInstructions.REPLY_SCHEMA_ID),
            TutorInstructions.REPLY_SCHEMA, TutorInstructions.userMessage(request))) {
            is ProviderOutcome.NotDelivered -> TutorReply.NotDelivered(outcome.reason)
            is ProviderOutcome.Delivered -> content(outcome.json)
                ?.let { TutorReply.Delivered(it, TutorRef(provider, outcome.model, TutorInstructions.VERSION)) }
                ?: TutorReply.NotDelivered(PendingReason.INVALID_RESPONSE)
        }
    }

    /** Exactly the schema's four fields, each from its closed vocabulary; anything else is no reply. */
    private fun content(json: JsonValue.Obj): TutorContent? {
        if (json.fields.keys != setOf("intent", "text", "revealed_level", "instruction_mode")) return null
        val intent = (json["intent"] as? JsonValue.Str)?.value?.let { id -> TutorIntent.entries.firstOrNull { it.id == id } } ?: return null
        val text = (json["text"] as? JsonValue.Str)?.value?.takeIf { it.isNotBlank() } ?: return null
        val level = when (val raw = json["revealed_level"]) {
            JsonValue.Null -> null
            is JsonValue.Str -> AssistanceLevel.entries.firstOrNull { it.id == raw.value } ?: return null
            else -> return null
        }
        val mode = (json["instruction_mode"] as? JsonValue.Str)?.value?.let { id -> InstructionMode.entries.firstOrNull { it.id == id } } ?: return null
        return TutorContent(intent, text, level, mode)
    }
}

/** A schema id as the provider's format name: `tutor_reply/2` → `tutor_reply_2` (letters, digits, `_` only). */
internal fun schemaName(id: String): String = id.replace('/', '_').replace('.', '_')
