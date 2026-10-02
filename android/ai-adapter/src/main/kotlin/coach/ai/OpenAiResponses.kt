package coach.ai

import coach.model.PendingReason
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * One call to the OpenAI Responses API with a strict JSON-schema reply (14G, `PRVX-v0 / D-111`; request shape and
 * refusal shape confirmed against the current reference recorded in [ProviderConfig]).
 *
 * What it guarantees, each from `AIAX-v0`:
 *
 * - **No key, no call.** Without a key nothing leaves the device and the answer is `unavailable` (§10.3).
 * - **Only what core built is sent**: core's instructions, core's message and core's schema, unchanged; the response
 *   is not stored by the provider (`store: false`) — the minimum, kept no longer than the call (§11).
 * - **The stop reason before the content** (§6): a refusal item, or a reply cut off by the provider's content filter, is
 *   `refused` — never a wrong answer — and nothing else is read from it.
 * - **Only a schema-valid object is an answer** (§5.1): exactly one `output_text` that parses as a JSON object. Anything
 *   else — an incomplete reply, no text, two texts, text that is not JSON — is `invalid_response`.
 * - **One end-to-end budget** (§7.1): every attempt draws on it; a timeout ends the call as `timed_out`; only a network
 *   failure, a 408, a 429 or a 5xx is tried again, and only while attempts and budget remain. A refusal, an invalid
 *   reply or a rejected key is never retried. Nothing is retried later in the background.
 */
class OpenAiResponses(
    private val config: ProviderConfig,
    private val keys: ApiKeySource,
    private val transport: HttpTransport = UrlConnectionTransport(),
    private val monotonicMillis: () -> Long = { System.nanoTime() / 1_000_000 },
) {
    fun call(taskClass: TaskClass, instructions: String, schemaName: String, schema: String, message: String): ProviderOutcome {
        val key = keys.current()?.takeIf { it.isNotBlank() } ?: return ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE)
        val model = config.model(taskClass)
        val body = Json.write(requestBody(model, instructions, schemaName, schema, message))
        val headers = mapOf("Authorization" to "Bearer $key", "Content-Type" to "application/json")
        val started = monotonicMillis()
        var attempt = 0
        var last: ProviderOutcome = ProviderOutcome.NotDelivered(PendingReason.TRANSPORT_ERROR)
        while (attempt < config.maxAttempts) {
            val remaining = config.budgetMillis - (monotonicMillis() - started)
            if (remaining <= 0) return ProviderOutcome.NotDelivered(PendingReason.TIMED_OUT)
            attempt++
            val response = try {
                transport.post(config.endpoint, headers, body, remaining)
            } catch (timeout: SocketTimeoutException) {
                return ProviderOutcome.NotDelivered(PendingReason.TIMED_OUT)
            } catch (failure: IOException) {
                last = ProviderOutcome.NotDelivered(PendingReason.TRANSPORT_ERROR)
                continue
            }
            when {
                response.status == 401 || response.status == 403 -> return ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE, keyRejected = true)
                response.status == 408 || response.status == 429 || response.status >= 500 -> {
                    last = ProviderOutcome.NotDelivered(PendingReason.TRANSPORT_ERROR)
                    continue
                }
                response.status !in 200..299 -> return ProviderOutcome.NotDelivered(PendingReason.TRANSPORT_ERROR)
                else -> return read(response.body, model)
            }
        }
        return last
    }

    private fun requestBody(model: String, instructions: String, schemaName: String, schema: String, message: String): JsonValue.Obj {
        val schemaJson = Json.parse(schema) as? JsonValue.Obj ?: throw JsonException("a reply schema is an object")
        return Json.obj(
            "model" to Json.str(model),
            "instructions" to Json.str(instructions),
            "input" to Json.str(message),
            "text" to Json.obj(
                "format" to Json.obj(
                    "type" to Json.str("json_schema"),
                    "name" to Json.str(schemaName),
                    "strict" to Json.bool(true),
                    "schema" to schemaJson,
                ),
            ),
            "store" to Json.bool(false),
        )
    }

    /** The reply, read in the order `AIAX-v0` §6 requires: status and refusal first, content last. */
    private fun read(body: String, model: String): ProviderOutcome {
        val reply = runCatching { Json.parse(body) }.getOrNull() as? JsonValue.Obj
            ?: return ProviderOutcome.NotDelivered(PendingReason.INVALID_RESPONSE)
        val status = (reply["status"] as? JsonValue.Str)?.value
        if (status == "incomplete") {
            val reason = ((reply["incomplete_details"] as? JsonValue.Obj)?.get("reason") as? JsonValue.Str)?.value
            return ProviderOutcome.NotDelivered(if (reason == "content_filter") PendingReason.REFUSED else PendingReason.INVALID_RESPONSE)
        }
        if (status != "completed") return ProviderOutcome.NotDelivered(PendingReason.INVALID_RESPONSE)
        val contents = (reply["output"] as? JsonValue.Arr)?.items.orEmpty()
            .filterIsInstance<JsonValue.Obj>()
            .filter { (it["type"] as? JsonValue.Str)?.value == "message" }
            .flatMap { (it["content"] as? JsonValue.Arr)?.items.orEmpty() }
            .filterIsInstance<JsonValue.Obj>()
        if (contents.any { (it["type"] as? JsonValue.Str)?.value == "refusal" }) return ProviderOutcome.NotDelivered(PendingReason.REFUSED)
        val texts = contents.filter { (it["type"] as? JsonValue.Str)?.value == "output_text" }
        val text = (texts.singleOrNull()?.get("text") as? JsonValue.Str)?.value
            ?: return ProviderOutcome.NotDelivered(PendingReason.INVALID_RESPONSE)
        val json = runCatching { Json.parse(text) }.getOrNull() as? JsonValue.Obj
            ?: return ProviderOutcome.NotDelivered(PendingReason.INVALID_RESPONSE)
        val answered = (reply["model"] as? JsonValue.Str)?.value?.takeIf { it.isNotBlank() } ?: model
        return ProviderOutcome.Delivered(json, answered)
    }
}
