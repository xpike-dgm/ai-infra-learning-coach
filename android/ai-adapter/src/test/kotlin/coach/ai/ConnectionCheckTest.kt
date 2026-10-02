package coach.ai

import coach.ai.OpenAiResponsesTest.Companion.completed
import coach.ai.OpenAiResponsesTest.Scripted
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The settings screen's connection check (14G): a fixed word out, nothing about the learner, and an honest result. */
class ConnectionCheckTest {

    private fun check(transport: HttpTransport, key: String? = OpenAiResponsesTest.KEY) =
        ConnectionCheck(OpenAiResponses(ProviderConfig(), { key }, transport) { 0L }).run()

    @Test
    fun `each outcome is reported as what it is`() {
        assertEquals(ConnectionResult.WORKS, check(Scripted({ completed("{\"ok\":true}") })))
        assertEquals(ConnectionResult.UNEXPECTED_REPLY, check(Scripted({ completed("{\"ok\":false}") })))
        assertEquals(ConnectionResult.KEY_REJECTED, check(Scripted({ HttpResponse(401, "{}") })))
        assertEquals(ConnectionResult.NO_KEY, check(Scripted(), key = null))
        assertEquals(ConnectionResult.UNREACHABLE, check(Scripted({ throw IOException("x") }, { throw IOException("x") })))
        assertEquals(ConnectionResult.TIMED_OUT, check(Scripted({ throw SocketTimeoutException("x") })))
    }

    @Test
    fun `the check sends only a fixed instruction and a fixed word`() {
        val transport = Scripted({ completed("{\"ok\":true}") })
        check(transport)
        val sent = Json.parse(transport.calls.single().third) as JsonValue.Obj
        assertEquals(Json.str(ConnectionCheck.MESSAGE), sent["input"])
        assertEquals(Json.str(ConnectionCheck.INSTRUCTIONS), sent["instructions"])
        assertTrue(ConnectionCheck.MESSAGE == "ping")
    }
}
