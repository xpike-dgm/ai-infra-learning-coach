package coach.ai

import coach.model.PendingReason
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * The provider client against a scripted transport — no check here calls a live provider (`TVSX-v0`). Request shape
 * and refusal shape follow the reference recorded in [ProviderConfig].
 */
class OpenAiResponsesTest {

    class Scripted(vararg replies: () -> HttpResponse) : HttpTransport {
        private val queue = ArrayDeque(replies.toList())
        val calls = mutableListOf<Triple<String, Map<String, String>, String>>()
        val timeouts = mutableListOf<Long>()
        override fun post(url: String, headers: Map<String, String>, body: String, timeoutMillis: Long): HttpResponse {
            calls += Triple(url, headers, body)
            timeouts += timeoutMillis
            return queue.removeFirst()()
        }
    }

    companion object {
        const val KEY = "sk-test-not-a-real-key"

        fun completed(text: String, model: String = "gpt-6-astra"): HttpResponse = HttpResponse(200, Json.write(Json.obj(
            "status" to Json.str("completed"),
            "model" to Json.str(model),
            "output" to JsonValue.Arr(listOf(Json.obj(
                "type" to Json.str("message"),
                "content" to JsonValue.Arr(listOf(Json.obj("type" to Json.str("output_text"), "text" to Json.str(text)))),
            ))),
        )))

        fun refusal(): HttpResponse = HttpResponse(200, Json.write(Json.obj(
            "status" to Json.str("completed"),
            "output" to JsonValue.Arr(listOf(Json.obj(
                "type" to Json.str("message"),
                "content" to JsonValue.Arr(listOf(Json.obj("type" to Json.str("refusal"), "refusal" to Json.str("I can't help with that.")))),
            ))),
        )))

        fun incomplete(reason: String): HttpResponse = HttpResponse(200, Json.write(Json.obj(
            "status" to Json.str("incomplete"),
            "incomplete_details" to Json.obj("reason" to Json.str(reason)),
            "output" to JsonValue.Arr(emptyList()),
        )))
    }

    private var now = 0L
    private fun client(transport: HttpTransport, key: String? = KEY, config: ProviderConfig = ProviderConfig()) =
        OpenAiResponses(config, { key }, transport) { now }

    private val schema = "{\"type\":\"object\",\"additionalProperties\":false,\"required\":[\"ok\"],\"properties\":{\"ok\":{\"type\":\"boolean\"}}}"
    private fun call(c: OpenAiResponses, taskClass: TaskClass = TaskClass.TUTOR_HELP) = c.call(taskClass, "the rules", "connection_check_1", schema, "the message")

    @Test
    fun `without a key nothing leaves the device`() {
        val transport = Scripted()
        assertEquals(ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE), call(client(transport, key = null)))
        assertEquals(ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE), call(client(transport, key = "  ")))
        assertTrue(transport.calls.isEmpty())
    }

    @Test
    fun `the request carries core's instructions, message and schema unchanged, strict, not stored, and the key only in the header`() {
        val transport = Scripted({ completed("{\"ok\":true}") })
        assertIs<ProviderOutcome.Delivered>(call(client(transport), TaskClass.OPEN_RESPONSE_EVALUATION))
        val (url, headers, body) = transport.calls.single()
        assertEquals("https://api.openai.com/v1/responses", url)
        assertEquals("Bearer $KEY", headers["Authorization"])
        assertFalse(KEY in body, "the key travels only in the Authorization header")
        val sent = Json.parse(body) as JsonValue.Obj
        assertEquals(setOf("model", "instructions", "input", "text", "store"), sent.fields.keys)
        assertEquals(Json.str("gpt-6-astra"), sent["model"])
        assertEquals(Json.str("the rules"), sent["instructions"])
        assertEquals(Json.str("the message"), sent["input"])
        assertEquals(Json.bool(false), sent["store"], "the provider keeps nothing")
        val format = ((sent["text"] as JsonValue.Obj)["format"] as JsonValue.Obj)
        assertEquals(Json.str("json_schema"), format["type"])
        assertEquals(Json.bool(true), format["strict"])
        assertEquals(Json.str("connection_check_1"), format["name"])
        assertEquals(Json.parse(schema), format["schema"])
    }

    @Test
    fun `the router names the model of each task class from configuration`() {
        val config = ProviderConfig(models = mapOf(TaskClass.TUTOR_HELP to "tutor-model", TaskClass.OPEN_RESPONSE_EVALUATION to "eval-model",
            TaskClass.CODE_EVALUATION to "code-model"))
        for ((taskClass, model) in config.models) {
            val transport = Scripted({ completed("{\"ok\":true}", model) })
            val outcome = assertIs<ProviderOutcome.Delivered>(call(client(transport, config = config), taskClass))
            assertEquals(Json.str(model), (Json.parse(transport.calls.single().third) as JsonValue.Obj)["model"])
            assertEquals(model, outcome.model)
        }
    }

    @Test
    fun `a refusal is a refusal, never a wrong answer, and a content-filtered reply is one too`() {
        assertEquals(ProviderOutcome.NotDelivered(PendingReason.REFUSED), call(client(Scripted({ refusal() }))))
        assertEquals(ProviderOutcome.NotDelivered(PendingReason.REFUSED), call(client(Scripted({ incomplete("content_filter") }))))
        assertEquals(ProviderOutcome.NotDelivered(PendingReason.INVALID_RESPONSE), call(client(Scripted({ incomplete("max_output_tokens") }))))
    }

    @Test
    fun `only exactly one JSON object is an answer`() {
        for (bad in listOf(completed("not json"), completed("[1,2]"), HttpResponse(200, "<html>"), HttpResponse(200, "{\"status\":\"failed\"}"),
            HttpResponse(200, "{\"status\":\"completed\",\"output\":[]}"))) {
            assertEquals(ProviderOutcome.NotDelivered(PendingReason.INVALID_RESPONSE), call(client(Scripted({ bad }))), bad.body)
        }
        val two = HttpResponse(200, Json.write(Json.obj("status" to Json.str("completed"), "output" to JsonValue.Arr(listOf(Json.obj(
            "type" to Json.str("message"),
            "content" to JsonValue.Arr(listOf(Json.obj("type" to Json.str("output_text"), "text" to Json.str("{}")),
                Json.obj("type" to Json.str("output_text"), "text" to Json.str("{}")))),
        ))))))
        assertEquals(ProviderOutcome.NotDelivered(PendingReason.INVALID_RESPONSE), call(client(Scripted({ two }))))
    }

    @Test
    fun `a rejected key is said as such and never retried`() {
        for (status in listOf(401, 403)) {
            val transport = Scripted({ HttpResponse(status, "{}") }, { completed("{\"ok\":true}") })
            assertEquals(ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE, keyRejected = true), call(client(transport)))
            assertEquals(1, transport.calls.size)
        }
    }

    @Test
    fun `a network failure, a 429 or a 5xx is tried again only while attempts remain`() {
        val recovers = Scripted({ HttpResponse(429, "{}") }, { completed("{\"ok\":true}") })
        assertIs<ProviderOutcome.Delivered>(call(client(recovers)))
        assertEquals(2, recovers.calls.size)
        val fails = Scripted({ HttpResponse(503, "{}") }, { throw IOException("reset") }, { completed("{\"ok\":true}") })
        assertEquals(ProviderOutcome.NotDelivered(PendingReason.TRANSPORT_ERROR), call(client(fails)))
        assertEquals(2, fails.calls.size, "maxAttempts bounds the retries")
        val badRequest = Scripted({ HttpResponse(400, "{}") }, { completed("{\"ok\":true}") })
        assertEquals(ProviderOutcome.NotDelivered(PendingReason.TRANSPORT_ERROR), call(client(badRequest)))
        assertEquals(1, badRequest.calls.size, "a rejected request is not sent again")
    }

    @Test
    fun `the budget is end-to-end — a timeout ends the call, and no attempt starts once it is spent`() {
        val slow = Scripted({ throw SocketTimeoutException("read") }, { completed("{\"ok\":true}") })
        assertEquals(ProviderOutcome.NotDelivered(PendingReason.TIMED_OUT), call(client(slow)))
        assertEquals(1, slow.calls.size)
        val spent = Scripted({ now += 60_000; HttpResponse(503, "{}") }, { completed("{\"ok\":true}") })
        assertEquals(ProviderOutcome.NotDelivered(PendingReason.TIMED_OUT), call(client(spent)))
        assertEquals(1, spent.calls.size)
        now = 0
        val shrinking = Scripted({ now += 20_000; HttpResponse(503, "{}") }, { completed("{\"ok\":true}") })
        assertIs<ProviderOutcome.Delivered>(call(client(shrinking)))
        assertEquals(listOf(60_000L, 40_000L), shrinking.timeouts, "each attempt gets only what is left")
    }

    @Test
    fun `the configuration refuses an endpoint without TLS and a task class without a model`() {
        kotlin.test.assertFailsWith<IllegalArgumentException> { ProviderConfig(endpoint = "http://api.openai.com/v1/responses") }
        kotlin.test.assertFailsWith<IllegalArgumentException> { ProviderConfig(models = mapOf(TaskClass.TUTOR_HELP to "m")) }
    }
}
