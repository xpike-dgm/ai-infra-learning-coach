package coach.presentation

import coach.model.AssistanceLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 17C (`USBX-v0`): a run's position, its fields and the history's assistance words. */
class RunUsabilityTest {

    @Test
    fun `the position is a bounded fact among this run's questions and nothing else`() {
        assertEquals("Soru 2 / 5", RunPosition.label(2, 5))
        assertEquals("Soru 1", RunPosition.label(1, 1), "one question needs no total")
        assertEquals("Soru 3", RunPosition.label(3, null))
        // A total smaller than the number would be untrue; it is not said.
        assertEquals("Soru 4", RunPosition.label(4, 3))
        // A screen reader hears words, never a fraction that sounds like a score.
        assertEquals("Soru 2, toplam 5 soru", RunPosition.spoken(2, 5))
        assertEquals("Soru 1", RunPosition.spoken(1, 1))
        assertEquals("Soru 4", RunPosition.spoken(4, 3))
        listOf(RunPosition.label(2, 5), RunPosition.label(1, null)).forEach { label ->
            listOf("%", "puan", "doğru", "tamam", "kaldı").forEach { w -> assertFalse(label.contains(w, ignoreCase = true), "$w in $label") }
        }
    }

    @Test
    fun `answers, code and reports keep exactly what was typed and an open answer keeps the keyboard's help`() {
        assertTrue(AnswerInput.exact(AnswerField.ANSWER))
        assertTrue(AnswerInput.exact(AnswerField.CODE))
        assertTrue(AnswerInput.exact(AnswerField.REPORT))
        assertFalse(AnswerInput.exact(AnswerField.OPEN))
    }

    @Test
    fun `the history names assistance in words, least revealing first, never as a code`() {
        assertEquals(AssistanceLevel.entries.toSet(), AssistanceWords.labels.keys)
        assertEquals("yön ipucu, kısmi çözüm", AssistanceWords.of(listOf(AssistanceLevel.H3, AssistanceLevel.H1, AssistanceLevel.H3)))
        assertTrue(AssistanceWords.labels.values.none { Regex("""H\d""").containsMatchIn(it) })
    }
}
