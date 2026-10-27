package coach.presentation

import coach.model.AssistanceLevel

/**
 * 17C (`USBX-v0 / D-135`): small rules that make a run easier to use. None of them changes what is recorded, judged or
 * claimed; each only decides how the run is presented or typed into, so it can be checked on the JVM.
 */

/**
 * Where the learner is in the run: "Soru 2 / 5". It is a bounded, factual position among this run's questions — the
 * one kind of count `VDSX-v0` §11.1 allows — and never a score, a progress claim or a share of anything learned. It is
 * drawn as text, never as a bar. With no total known, only the question's number is said.
 */
object RunPosition {
    fun label(number: Int, total: Int?): String =
        if (total != null && total >= number && total > 1) "Soru $number / $total" else "Soru $number"

    /** What a screen reader says: words, never "2 bölü 5", which would sound like a score. */
    fun spoken(number: Int, total: Int?): String =
        if (total != null && total >= number && total > 1) "Soru $number, toplam $total soru" else "Soru $number"
}

/** The fields a learner types into during a run. */
enum class AnswerField { ANSWER, OPEN, CODE, REPORT }

object AnswerInput {
    /**
     * Whether the field must keep exactly what was typed: no autocorrect and no automatic capital letter. A short
     * answer is compared with the answer key character by character, and code and a pasted test report must arrive as
     * written — a keyboard that turns `print(x)` into `Print(x)` or "4 4" into "4.4" would make a right answer wrong.
     * An open answer is prose, judged by its rubric, so the keyboard may help with it.
     */
    fun exact(field: AnswerField): Boolean = field != AnswerField.OPEN
}

/** The assistance levels in words, for the learning history — what was shown, never a code like "H3". */
object AssistanceWords {
    val labels: Map<AssistanceLevel, String> = mapOf(
        AssistanceLevel.H1 to "yön ipucu",
        AssistanceLevel.H2 to "kavram yardımı",
        AssistanceLevel.H3 to "kısmi çözüm",
        AssistanceLevel.H4 to "tam çözüm",
    )

    fun of(levels: List<AssistanceLevel>): String =
        levels.distinct().sortedBy(AssistanceLevel::ordinal).joinToString(", ") { labels.getValue(it) }
}
