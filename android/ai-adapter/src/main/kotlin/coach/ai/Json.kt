package coach.ai

/**
 * A small, strict JSON reader and writer (RFC 8259) for the provider's request and reply (14G).
 *
 * No library is added for it: the adapter needs only objects, arrays, strings, numbers, booleans and null, and a
 * reader that **refuses** anything else — a duplicate key, a trailing comma, trailing text, an unescaped control
 * character — so a malformed reply becomes `invalid_response` rather than a half-read verdict (`AIAX-v0` §5.1).
 */
sealed interface JsonValue {
    data class Obj(val fields: LinkedHashMap<String, JsonValue>) : JsonValue {
        operator fun get(key: String): JsonValue? = fields[key]
    }

    data class Arr(val items: List<JsonValue>) : JsonValue
    data class Str(val value: String) : JsonValue
    data class Num(val literal: String) : JsonValue
    data class Bool(val value: Boolean) : JsonValue
    data object Null : JsonValue
}

class JsonException(message: String) : IllegalArgumentException(message)

object Json {

    fun obj(vararg fields: Pair<String, JsonValue>): JsonValue.Obj = JsonValue.Obj(linkedMapOf(*fields))
    fun str(value: String): JsonValue.Str = JsonValue.Str(value)
    fun bool(value: Boolean): JsonValue.Bool = JsonValue.Bool(value)

    fun parse(text: String): JsonValue {
        val reader = Reader(text)
        reader.whitespace()
        val value = reader.value(depth = 0)
        reader.whitespace()
        if (!reader.atEnd()) throw JsonException("trailing text at ${reader.position}")
        return value
    }

    fun write(value: JsonValue): String = StringBuilder().also { write(value, it) }.toString()

    private fun write(value: JsonValue, out: StringBuilder) {
        when (value) {
            is JsonValue.Obj -> {
                out.append('{')
                value.fields.entries.forEachIndexed { index, (key, item) ->
                    if (index > 0) out.append(',')
                    string(key, out)
                    out.append(':')
                    write(item, out)
                }
                out.append('}')
            }
            is JsonValue.Arr -> {
                out.append('[')
                value.items.forEachIndexed { index, item ->
                    if (index > 0) out.append(',')
                    write(item, out)
                }
                out.append(']')
            }
            is JsonValue.Str -> string(value.value, out)
            is JsonValue.Num -> out.append(value.literal)
            is JsonValue.Bool -> out.append(if (value.value) "true" else "false")
            JsonValue.Null -> out.append("null")
        }
    }

    private fun string(text: String, out: StringBuilder) {
        out.append('"')
        for (c in text) {
            when {
                c == '"' -> out.append("\\\"")
                c == '\\' -> out.append("\\\\")
                c == '\n' -> out.append("\\n")
                c == '\r' -> out.append("\\r")
                c == '\t' -> out.append("\\t")
                c.code < 0x20 -> out.append("\\u").append(c.code.toString(16).padStart(4, '0'))
                else -> out.append(c)
            }
        }
        out.append('"')
    }

    private class Reader(private val text: String) {
        var position = 0

        fun atEnd(): Boolean = position >= text.length

        fun whitespace() {
            while (!atEnd() && text[position] in " \t\n\r") position++
        }

        fun value(depth: Int): JsonValue {
            if (depth > MAX_DEPTH) throw JsonException("nested too deeply")
            if (atEnd()) throw JsonException("unexpected end")
            return when (val c = text[position]) {
                '{' -> obj(depth)
                '[' -> arr(depth)
                '"' -> JsonValue.Str(string())
                't' -> literal("true", JsonValue.Bool(true))
                'f' -> literal("false", JsonValue.Bool(false))
                'n' -> literal("null", JsonValue.Null)
                else -> if (c == '-' || c in '0'..'9') number() else throw JsonException("unexpected '$c' at $position")
            }
        }

        private fun obj(depth: Int): JsonValue.Obj {
            position++
            val fields = LinkedHashMap<String, JsonValue>()
            whitespace()
            if (peek() == '}') { position++; return JsonValue.Obj(fields) }
            while (true) {
                whitespace()
                if (peek() != '"') throw JsonException("expected a key at $position")
                val key = string()
                if (key in fields) throw JsonException("duplicate key '$key'")
                whitespace()
                expect(':')
                whitespace()
                fields[key] = value(depth + 1)
                whitespace()
                when (peek()) {
                    ',' -> position++
                    '}' -> { position++; return JsonValue.Obj(fields) }
                    else -> throw JsonException("expected ',' or '}' at $position")
                }
            }
        }

        private fun arr(depth: Int): JsonValue.Arr {
            position++
            val items = mutableListOf<JsonValue>()
            whitespace()
            if (peek() == ']') { position++; return JsonValue.Arr(items) }
            while (true) {
                whitespace()
                items += value(depth + 1)
                whitespace()
                when (peek()) {
                    ',' -> position++
                    ']' -> { position++; return JsonValue.Arr(items) }
                    else -> throw JsonException("expected ',' or ']' at $position")
                }
            }
        }

        private fun string(): String {
            expect('"')
            val out = StringBuilder()
            while (true) {
                if (atEnd()) throw JsonException("unterminated string")
                val c = text[position++]
                when {
                    c == '"' -> return out.toString()
                    c == '\\' -> {
                        if (atEnd()) throw JsonException("unterminated escape")
                        when (val e = text[position++]) {
                            '"' -> out.append('"')
                            '\\' -> out.append('\\')
                            '/' -> out.append('/')
                            'b' -> out.append('\b')
                            'f' -> out.append('\u000C')
                            'n' -> out.append('\n')
                            'r' -> out.append('\r')
                            't' -> out.append('\t')
                            'u' -> {
                                if (position + 4 > text.length) throw JsonException("short unicode escape")
                                val hex = text.substring(position, position + 4)
                                out.append(hex.toIntOrNull(16)?.toChar() ?: throw JsonException("bad unicode escape '$hex'"))
                                position += 4
                            }
                            else -> throw JsonException("bad escape '\\$e'")
                        }
                    }
                    c.code < 0x20 -> throw JsonException("unescaped control character in string")
                    else -> out.append(c)
                }
            }
        }

        private fun number(): JsonValue.Num {
            val match = NUMBER.matchAt(text, position) ?: throw JsonException("bad number at $position")
            position += match.value.length
            return JsonValue.Num(match.value)
        }

        private fun literal(word: String, value: JsonValue): JsonValue {
            if (!text.startsWith(word, position)) throw JsonException("unexpected literal at $position")
            position += word.length
            return value
        }

        private fun peek(): Char = if (atEnd()) throw JsonException("unexpected end") else text[position]

        private fun expect(c: Char) {
            if (peek() != c) throw JsonException("expected '$c' at $position")
            position++
        }
    }

    private const val MAX_DEPTH = 64
    private val NUMBER = Regex("-?(0|[1-9][0-9]*)(\\.[0-9]+)?([eE][+-]?[0-9]+)?")
}
