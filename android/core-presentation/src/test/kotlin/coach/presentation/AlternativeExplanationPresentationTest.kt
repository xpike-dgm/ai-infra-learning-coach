package coach.presentation

import coach.model.AssistanceSource
import coach.model.ExplanationForm
import coach.model.ExplanationOption
import coach.model.ExplanationSource
import coach.model.TutorOutcome
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The "explain it another way" menu and its labels (14C, `ALEX-v0`). */
class AlternativeExplanationPresentationTest {

    private val turkish: Locale = Locale.forLanguageTag("tr")

    private fun shown(source: AssistanceSource) = TutorOutcome.Shown("…", source, null, null, false, false, false)

    @Test
    fun `a tutor-written explanation is always labelled unverified, and every alternative offers the way back`() {
        val ai = AlternativeExplanationPresentation.notes(shown(AssistanceSource.AI_GENERATED))
        assertEquals(AlternativeExplanationCopy.AI_LABEL, ai.first())
        assertTrue("doğrulanmış içeriği değildir" in ai.first())
        assertTrue("asıl anlatım geçerlidir" in ai.first())
        val written = AlternativeExplanationPresentation.notes(shown(AssistanceSource.DETERMINISTIC_CONTENT))
        assertEquals(AlternativeExplanationCopy.WRITTEN_LABEL, written.first())
        for (notes in listOf(ai, written)) assertTrue(AlternativeExplanationCopy.BACK_TO_CANONICAL in notes)
    }

    @Test
    fun `a menu line says where a form comes from and whether it was seen, and ranks nothing`() {
        assertEquals("Daha sade anlat · AI ile · bu oturumda gösterildi",
            AlternativeExplanationPresentation.line(ExplanationOption(ExplanationForm.PLAIN_RETEACH, ExplanationSource.AI, seenThisSession = true)))
        assertEquals("Çözümlü bir örnek göster",
            AlternativeExplanationPresentation.line(ExplanationOption(ExplanationForm.WORKED_EXAMPLE, ExplanationSource.WRITTEN, seenThisSession = false)))
    }

    @Test
    fun `a contrast is offered as a common mix-up, never as the learner's mistake, and nothing counts or blames`() {
        val contrast = AlternativeExplanationCopy.form(ExplanationForm.MISCONCEPTION_CONTRAST).lowercase(turkish)
        assertTrue("sık yapılan" in contrast)
        val copy = ExplanationForm.entries.map { AlternativeExplanationCopy.form(it) } + listOf(
            AlternativeExplanationCopy.MENU_PROMPT, AlternativeExplanationCopy.SEEN, AlternativeExplanationCopy.FROM_AI, AlternativeExplanationCopy.WRITTEN_LABEL,
            AlternativeExplanationCopy.AI_LABEL, AlternativeExplanationCopy.BACK_TO_CANONICAL, AlternativeExplanationCopy.NOTHING_TRUE_TO_SHOW,
        )
        for (text in copy) {
            val lower = text.lowercase(turkish)
            for (word in listOf("senin hatan", "yanılgın", "%", "puan", "başarısız", "zayıf", "eksik", "öğrendin", "tembel")) assertFalse(word in lower, "$word in: $text")
            assertFalse(text.any { it.isDigit() }, "a number in: $text")
        }
    }
}
