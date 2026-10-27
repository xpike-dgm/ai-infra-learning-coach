package coach.presentation

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * 17A (`VPLX-v0 / D-133`): authored text drawn with the right `VDSX-v0` role, and identifiers turned into words.
 *
 * Shipped lessons and prompts carry code with no markup: a code block is a paragraph of its own, set apart by blank
 * lines. [TextBlocks] finds those paragraphs so they can be drawn in the `mono` role. It changes **how** a paragraph is
 * drawn, never what it says: every line comes back exactly as authored, in order, and a paragraph it is unsure about
 * stays prose.
 */
enum class BlockKind { PROSE, CODE }

data class TextBlock(val kind: BlockKind, val text: String)

object TextBlocks {

    // Four or more words of letters in a row: a sentence, in Turkish or English, not a statement of code.
    private val wordRun = Regex("""(?<![\p{L}\p{N}_])(?:\p{L}{2,}[.,;:!?]?\s){3,}\p{L}{2,}""")
    private val codeCharacter = Regex("""[=(){}\[\];<>|$#*+/"'`]""")
    // A multiple-choice option ("A) ...") is prose even though it has a parenthesis.
    private val option = Regex("""^[A-E]\)\s""")

    private enum class Line { PROSE, CODE, NEUTRAL }

    // A sentence ends with . ? or !; a line of code almost never does ("cd .." has one word).
    private val sentenceEnd = Regex("""[.?!]\s*$""")
    private val word = Regex("""(?<!\p{L})\p{L}+(?!\p{L})""")

    private fun classify(line: String): Line = when {
        option.containsMatchIn(line) -> Line.PROSE
        line.startsWith(" ") || line.startsWith("\t") || line.startsWith("$ ") || line.startsWith(">>>") -> Line.CODE
        wordRun.containsMatchIn(line) -> Line.PROSE
        sentenceEnd.containsMatchIn(line) && word.findAll(line).count() >= 3 -> Line.PROSE
        codeCharacter.containsMatchIn(line) -> Line.CODE
        else -> Line.NEUTRAL
    }

    /**
     * A paragraph is code when it has at least one line that reads as code and at most half as many lines that read as
     * sentences — so a traceback, whose first line is words, is still code, and a list of explained steps is not.
     */
    fun isCode(paragraph: String): Boolean {
        val lines = paragraph.lines().filter { it.isNotBlank() }.map(::classify)
        val code = lines.count { it == Line.CODE }
        val prose = lines.count { it == Line.PROSE }
        return code >= 1 && prose * 2 <= code
    }

    /** The paragraphs of [text], in order, each with its kind. Blank lines separate paragraphs and are not returned. */
    fun split(text: String): List<TextBlock> {
        val paragraphs = mutableListOf<String>()
        val current = mutableListOf<String>()
        for (line in text.lines()) {
            if (line.isBlank()) {
                if (current.isNotEmpty()) paragraphs += current.joinToString("\n")
                current.clear()
            } else {
                current += line
            }
        }
        if (current.isNotEmpty()) paragraphs += current.joinToString("\n")
        return paragraphs.map { TextBlock(if (isCode(it)) BlockKind.CODE else BlockKind.PROSE, it) }
    }
}

/**
 * Identifiers that reached the screen raw before 17A, as Turkish words. Each map is closed: a value it does not know
 * is **left out**, never shown as an identifier and never guessed into a word.
 */
object DisplayCopy {

    /** The authored task's `activity_kind` (`THUX-v0` §6.1: context, separate from purpose). */
    val activityKinds: Map<String, String> = mapOf(
        "content_explanation" to "anlatım",
        "code_reading_trace" to "kod okuma",
        "coding_production" to "kod yazma",
        "debugging_diagnosis" to "hata ayıklama",
        "explanation_justification" to "açıklama yazma",
        "hands_on_system_task" to "terminalde uygulama",
        "recognition_selection" to "seçmeli soru",
    )

    /** The planner's track. Only the parallel English line has one today (`THUX-v0` §6.1). */
    val tracks: Map<String, String> = mapOf(
        "technical_english" to "Teknik İngilizce",
    )

    fun activityKind(id: String?): String? = id?.let(activityKinds::get)

    fun track(id: String?): String? = id?.let(tracks::get)

    private val months = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık",
    )

    private val weekdays: Map<DayOfWeek, String> = mapOf(
        DayOfWeek.MONDAY to "Pazartesi",
        DayOfWeek.TUESDAY to "Salı",
        DayOfWeek.WEDNESDAY to "Çarşamba",
        DayOfWeek.THURSDAY to "Perşembe",
        DayOfWeek.FRIDAY to "Cuma",
        DayOfWeek.SATURDAY to "Cumartesi",
        DayOfWeek.SUNDAY to "Pazar",
    )

    /**
     * A study day (`YYYY-MM-DD`) as Turkish words — "26 Ekim 2026, Pazartesi". The names are written out here rather
     * than taken from the device locale, so the words cannot change with a setting and no case transform is involved
     * (`VDSX-v0` §6.3). A value that is not a date comes back unchanged; nothing is guessed.
     */
    fun studyDay(isoDate: String): String {
        val date = runCatching { LocalDate.parse(isoDate) }.getOrNull() ?: return isoDate
        return "${date.dayOfMonth} ${months[date.monthValue - 1]} ${date.year}, ${weekdays.getValue(date.dayOfWeek)}"
    }
}
