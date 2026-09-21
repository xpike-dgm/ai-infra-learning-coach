package coach.model

/**
 * Where a short learner response actually lives (11D).
 *
 * `DDM-v0` gives an artifact a `content_ref` and does not say what it points at; 11B recorded the
 * body's storage as this step's to decide. For the daily micro assessment the body is a short text
 * response, and it is carried **inside the reference itself** as an RFC 2397 `data:` URI.
 *
 * That keeps three guarantees that a side file would have cost: the body commits in the very same
 * transaction as its attempt, so the half-record `LFPS-v0` forbids cannot exist; it is inside the
 * database, so 10E's verified export already contains it and no second restore path appears; and no
 * table, column or port is invented for it.
 *
 * Its limit is explicit: anything larger than [MAX_BODY_CHARS] is **refused**, not truncated and not
 * silently written elsewhere. Code files, projects and execution output are larger artifacts whose
 * store belongs to the steps that produce them (14, 15).
 */
object ArtifactBody {

    /** A short response. Beyond this an artifact needs a real blob store, which 11D does not decide. */
    const val MAX_BODY_CHARS = 4096

    private const val PREFIX = "data:text/plain;charset=utf-8,"
    private const val UNRESERVED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"

    sealed interface Stored {
        /** The `content_ref` to record on the artifact row. */
        data class Inline(val contentRef: String) : Stored

        /** The body is too large to carry inline; the caller must not record a partial artifact. */
        data class TooLarge(val chars: Int, val limit: Int = MAX_BODY_CHARS) : Stored
    }

    fun store(body: String): Stored =
        if (body.length > MAX_BODY_CHARS) Stored.TooLarge(body.length) else Stored.Inline(PREFIX + encode(body))

    /** The body a `content_ref` carries, or `null` when the reference is not an inline body. */
    fun read(contentRef: String): String? =
        if (!contentRef.startsWith(PREFIX)) null else decode(contentRef.removePrefix(PREFIX))

    private fun encode(body: String): String = buildString {
        body.toByteArray(Charsets.UTF_8).forEach { byte ->
            val char = byte.toInt().toChar()
            if (byte >= 0 && char in UNRESERVED) append(char)
            else append('%').append(HEX[(byte.toInt() shr 4) and 0xF]).append(HEX[byte.toInt() and 0xF])
        }
    }

    private fun decode(encoded: String): String? {
        val bytes = ArrayList<Byte>(encoded.length)
        var i = 0
        while (i < encoded.length) {
            val char = encoded[i]
            when {
                char == '%' -> {
                    if (i + 2 >= encoded.length) return null
                    val value = encoded.substring(i + 1, i + 3).toIntOrNull(16) ?: return null
                    bytes += value.toByte()
                    i += 3
                }
                char in UNRESERVED -> {
                    bytes += char.code.toByte()
                    i += 1
                }
                // Anything else was never produced by the encoder, so the reference is not one of
                // ours and is not guessed at.
                else -> return null
            }
        }
        return String(bytes.toByteArray(), Charsets.UTF_8)
    }

    private const val HEX = "0123456789ABCDEF"
}
