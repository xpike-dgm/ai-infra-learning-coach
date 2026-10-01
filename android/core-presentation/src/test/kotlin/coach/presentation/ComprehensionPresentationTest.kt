package coach.presentation

import coach.model.ComponentResult
import coach.model.ComprehensionCheck
import coach.model.ComprehensionKind
import coach.model.ComprehensionOffer
import coach.model.ComprehensionResult
import coach.model.EvaluationResult
import coach.model.EvaluatorRef
import coach.model.IndependenceClass
import coach.model.OutcomeSignal
import coach.model.VersionedRef
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** What is said around the comprehension check (14E): no accusation, skipping is fine, explaining is not writing. */
class ComprehensionPresentationTest {

    private val objective = VersionedRef("objective.c.pointers.write_through", 1)
    private val check = ComprehensionCheck(
        VersionedRef("comprehension.c.pointers.set_five_star", 1), VersionedRef("item.c.pointers.set_five", 1), objective,
        ComprehensionKind.STATE_EFFECT, "explanation", "*p = 5; satırı neyi değiştirir?",
        mapOf("a" to "p'nin tuttuğu adresi", "b" to "p'nin gösterdiği yerdeki değeri"), "b",
    )

    private fun measured(correct: Boolean) = ComprehensionResult.Measured(
        check,
        EvaluationResult.Verified(listOf(ComponentResult(objective, if (correct) OutcomeSignal.MET else OutcomeSignal.NOT_MET)), EvaluatorRef("deterministic", "comprehension_key", "x")),
        correct,
    )

    @Test
    fun `the offer names what happened without accusing anyone, and is absent when the learner wrote the code`() {
        assertNull(ComprehensionPresentation.offer(ComprehensionOffer.NotOffered))
        assertEquals(ComprehensionCopy.OFFER, ComprehensionPresentation.offer(ComprehensionOffer.Written(listOf(check))))
        assertTrue("geçmek yanlış sayılmaz" in ComprehensionCopy.OFFER)
        assertEquals(ComprehensionCopy.TUTOR_OFFER, ComprehensionPresentation.offer(ComprehensionOffer.TutorPractice))
        assertTrue("pratik" in ComprehensionCopy.TUTOR_OFFER)
    }

    @Test
    fun `an answer is judged plainly, the right choice is shown, and explaining is never presented as writing`() {
        assertEquals(listOf(ComprehensionCopy.CORRECT, ComprehensionCopy.NOT_PRODUCTION), ComprehensionPresentation.result(measured(true)))
        assertEquals(
            listOf(ComprehensionCopy.NOT_CORRECT, "${ComprehensionCopy.RIGHT_CHOICE} b) p'nin gösterdiği yerdeki değeri", ComprehensionCopy.NOT_PRODUCTION),
            ComprehensionPresentation.result(measured(false)),
        )
        assertEquals(listOf(ComprehensionCopy.SKIPPED), ComprehensionPresentation.result(ComprehensionResult.Skipped))
        assertTrue("yanlış sayılmadı" in ComprehensionCopy.SKIPPED)
    }

    @Test
    fun `a fresh independent check is said not to be a penalty, and nothing counts or accuses`() {
        assertEquals(ComprehensionCopy.RECHECK, ComprehensionPresentation.recheckNote(IndependenceClass.REQUIRES_INDEPENDENT_RECHECK))
        assertTrue("ceza değil" in ComprehensionCopy.RECHECK)
        for (other in IndependenceClass.entries - IndependenceClass.REQUIRES_INDEPENDENT_RECHECK) assertNull(ComprehensionPresentation.recheckNote(other))
        val all = listOf(ComprehensionCopy.OFFER, ComprehensionCopy.TUTOR_OFFER, ComprehensionCopy.CORRECT, ComprehensionCopy.NOT_CORRECT,
            ComprehensionCopy.RIGHT_CHOICE, ComprehensionCopy.SKIPPED, ComprehensionCopy.NOT_PRODUCTION, ComprehensionCopy.TUTOR_PRACTICE,
            ComprehensionCopy.RECHECK).joinToString(" ")
        assertFalse(Regex("\\d|%").containsMatchIn(all), "no score, count or percentage")
        for (word in listOf("kopya", "hile", "başarısız", "puan")) assertFalse(word in all.lowercase(Locale.forLanguageTag("tr")), word)
        assertTrue("kanıt sayılmaz" in ComprehensionCopy.TUTOR_PRACTICE)
    }

    @Test
    fun `the tutor's question is always labelled as practice, before the tutor's own notes`() {
        val shown = coach.model.TutorOutcome.Shown("*p = 5 satırını silersen x ne olur?", coach.model.AssistanceSource.AI_GENERATED, null, null,
            revealsTargetReasoning = false, convertsItemToLearning = false, endsDiagnosticFastPath = false)
        val notes = ComprehensionPresentation.practiceNotes(shown)
        assertEquals(ComprehensionCopy.TUTOR_PRACTICE, notes.first())
        assertEquals(TutorPresentation.notes(shown), notes.drop(1))
    }
}
