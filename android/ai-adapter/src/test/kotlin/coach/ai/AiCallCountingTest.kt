package coach.ai

import coach.ai.OpenAiResponsesTest.Companion.KEY
import coach.ai.OpenAiResponsesTest.Companion.completed
import coach.ai.OpenAiResponsesTest.Companion.refusal
import coach.ai.OpenAiResponsesTest.Scripted
import coach.model.AiCall
import coach.model.AiCallOutcome
import coach.model.AiCallUse
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 17E (`D-138`): every call that left the device is counted once, with the tokens the provider's own reply reported —
 * and nothing it carried. Scripted transport only; no check calls a live provider (`TVSX-v0`).
 */
class AiCallCountingTest {

    private val counted = mutableListOf<AiCall>()
    private var now = 0L

    private fun client(transport: HttpTransport, key: String? = KEY) =
        OpenAiResponses(ProviderConfig(), { key }, transport, onCall = { counted += it }) { now }

    private val schema = "{\"type\":\"object\",\"additionalProperties\":false,\"required\":[\"ok\"],\"properties\":{\"ok\":{\"type\":\"boolean\"}}}"

    private fun withUsage(reply: HttpResponse, input: String, output: String): HttpResponse {
        val obj = Json.parse(reply.body) as JsonValue.Obj
        obj.fields["usage"] = Json.obj("input_tokens" to JsonValue.Num(input), "output_tokens" to JsonValue.Num(output))
        return HttpResponse(reply.status, Json.write(obj))
    }

    private fun call(c: OpenAiResponses, taskClass: TaskClass = TaskClass.OPEN_RESPONSE_EVALUATION) =
        c.call(taskClass, "the rules", "connection_check_1", schema, "the message")

    @Test
    fun `an answered call is counted once with the tokens its reply reported, and with nothing it carried`() {
        call(client(Scripted({ withUsage(completed("{\"ok\":true}"), "812", "64") })))
        assertEquals(listOf(AiCall(AiCallUse.OPEN_RESPONSE_EVALUATION, "gpt-6-astra", AiCallOutcome.ANSWERED, 1, 812, 64)), counted)
        val said = counted.single().toString()
        listOf("the rules", "the message", KEY, "true").forEach { assertTrue(it !in said, "a count carries \"$it\"") }
    }

    @Test
    fun `tokens the reply did not report are unknown, never zero`() {
        call(client(Scripted({ completed("{\"ok\":true}") })))
        assertEquals(null to null, counted.single().inputTokens to counted.single().outputTokens)
        counted.clear()
        call(client(Scripted({ withUsage(completed("{\"ok\":true}"), "-3", "1.5") })))
        assertEquals(null to null, counted.single().inputTokens to counted.single().outputTokens)
    }

    @Test
    fun `nothing is counted when nothing left the device`() {
        call(client(Scripted({ completed("{\"ok\":true}") }), key = null))
        assertEquals(emptyList(), counted)
    }

    @Test
    fun `retries are one call with their attempts, and every way a call can end is counted as itself`() {
        call(client(Scripted({ HttpResponse(503, "{}") }, { withUsage(completed("{\"ok\":true}"), "10", "2") })))
        assertEquals(AiCall(AiCallUse.OPEN_RESPONSE_EVALUATION, "gpt-6-astra", AiCallOutcome.ANSWERED, 2, 10, 2), counted.single())
        counted.clear()
        call(client(Scripted({ withUsage(refusal(), "30", "5") })))
        call(client(Scripted({ HttpResponse(401, "{}") })))
        call(client(Scripted({ throw SocketTimeoutException("read") })))
        call(client(Scripted({ throw IOException("reset") }, { HttpResponse(500, "{}") })))
        call(client(Scripted({ completed("not json") })))
        assertEquals(
            listOf(AiCallOutcome.REFUSED, AiCallOutcome.KEY_REJECTED, AiCallOutcome.TIMED_OUT, AiCallOutcome.TRANSPORT_ERROR, AiCallOutcome.INVALID_RESPONSE),
            counted.map { it.outcome },
        )
        assertEquals(30L to 5L, counted.first().inputTokens to counted.first().outputTokens, "a refusal still says what it used")
        assertEquals(2, counted[3].attempts)
    }

    @Test
    fun `a call is counted for what it was for, and the connection check is not tutor help`() {
        call(client(Scripted({ completed("{\"ok\":true}") })), TaskClass.TUTOR_HELP)
        call(client(Scripted({ completed("{\"ok\":true}") })), TaskClass.CODE_EVALUATION)
        ConnectionCheck(client(Scripted({ completed("{\"ok\":true}") }))).run()
        assertEquals(listOf(AiCallUse.TUTOR_HELP, AiCallUse.CODE_EVALUATION, AiCallUse.CONNECTION_CHECK), counted.map { it.use })
    }

    @Test
    fun `a connection that never opened sent nothing and is not counted (review, 17E)`() {
        call(client(Scripted({ throw NotSentException(timedOut = false, cause = IOException("no route")) },
            { throw NotSentException(timedOut = false, cause = IOException("no route")) })))
        call(client(Scripted({ throw NotSentException(timedOut = true, cause = SocketTimeoutException("connect")) })))
        assertEquals(emptyList(), counted, "offline, nothing left the device")
        call(client(Scripted({ throw NotSentException(timedOut = false, cause = IOException("no route")) }, { completed("{\"ok\":true}") })))
        assertEquals(1, counted.single().attempts, "only the request that was sent is an attempt")
    }

    @Test
    fun `every task class is counted as a use`() {
        TaskClass.entries.forEach { assertTrue(AiCallUse.of(it.id) != null, "${it.id} has no use") }
    }

    @Test
    fun `a listener that fails never fails the call`() {
        val failing = OpenAiResponses(ProviderConfig(), { KEY }, Scripted({ completed("{\"ok\":true}") }), onCall = { error("store gone") }) { now }
        assertTrue(call(failing) is ProviderOutcome.Delivered)
    }
}
