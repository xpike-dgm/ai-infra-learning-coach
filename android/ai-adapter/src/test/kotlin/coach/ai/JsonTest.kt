package coach.ai

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The adapter's strict JSON (14G): what it cannot read completely, it does not read at all. */
class JsonTest {

    @Test
    fun `a value round-trips, escapes included`() {
        val value = Json.obj("text" to Json.str("satır\n\"tırnak\" \\ \t ü"), "n" to JsonValue.Num("-1.5e3"), "b" to Json.bool(true), "z" to JsonValue.Null,
            "a" to JsonValue.Arr(listOf(Json.str("x"), Json.obj())))
        assertEquals(value, Json.parse(Json.write(value)))
        assertEquals(Json.str("é"), Json.parse("\"\\u00e9\""))
    }

    @Test
    fun `anything malformed is refused, not half-read`() {
        for (bad in listOf("{\"a\":1,}", "[1,]", "{\"a\":1} x", "{\"a\":1,\"a\":2}", "\"tab\there\"", "{a:1}", "01", "{\"a\":tru}", "", "[", "\"\\x\"")) {
            assertFailsWith<JsonException>(bad) { Json.parse(bad) }
        }
    }
}
