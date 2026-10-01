package coach.presentation

import coach.model.CodeNotMeasured
import coach.model.CodeVerdict
import coach.model.ComponentResult
import coach.model.EvaluationResult
import coach.model.EvaluatorRef
import coach.model.OutcomeSignal
import coach.model.VersionedRef
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** What is said after code is evaluated (14D): no score, nothing blamed that was not the code, an AI always labelled. */
class CodeEvaluationPresentationTest {

    private val reads = VersionedRef("objective.py.io.read_two_ints", 1)
    private val adds = VersionedRef("objective.py.arith.add_ints", 1)
    private val components = listOf(ComponentResult(reads, OutcomeSignal.MET), ComponentResult(adds, OutcomeSignal.NOT_RELIABLY_MEASURED))

    @Test
    fun `a tested result is said per Objective, an unrun test measured nothing, and passing is not understanding`() {
        val verdict = CodeVerdict.Measured(EvaluationResult.Verified(components, EvaluatorRef("deterministic", "code_tests", "x")))
        assertEquals(listOf(reads to CodeEvaluationCopy.MET, adds to CodeEvaluationCopy.NOT_MEASURED), CodeEvaluationPresentation.lines(verdict))
        assertTrue("yanlış sayılmadı" in CodeEvaluationCopy.NOT_MEASURED)
        assertEquals(listOf(CodeEvaluationCopy.TESTS_ARE_NOT_UNDERSTANDING), CodeEvaluationPresentation.notes(verdict))
    }

    @Test
    fun `an AI's judgement is worded as its view and always labelled unverified`() {
        val verdict = CodeVerdict.Measured(EvaluationResult.Provisional(listOf(ComponentResult(adds, OutcomeSignal.NOT_MET)), EvaluatorRef("p", "m", "v")))
        assertEquals(listOf(adds to CodeEvaluationCopy.AI_NOT_MET), CodeEvaluationPresentation.lines(verdict))
        assertTrue(CodeEvaluationCopy.AI_NOT_MET.startsWith("AI'a göre"))
        assertEquals(listOf(CodeEvaluationCopy.AI_LABEL), CodeEvaluationPresentation.notes(verdict))
        assertTrue("doğrulanmamıştır" in CodeEvaluationCopy.AI_LABEL)
    }

    @Test
    fun `nothing that went wrong with a report, a runner or an evaluator is the learner's mistake, and nothing is counted`() {
        val learnerSide = setOf(CodeNotMeasured.REPORT_MISSING, CodeNotMeasured.NO_SUITE_FOR_REPORT, CodeNotMeasured.NOTHING_SUBMITTED)
        for (reason in CodeNotMeasured.entries) {
            val text = CodeEvaluationPresentation.notMeasured(CodeVerdict.NotMeasured(reason))
            assertTrue(text.isNotBlank(), reason.id)
            if (reason !in learnerSide) assertTrue("yanlış sayılmadı" in text, reason.id)
        }
        val all = (CodeNotMeasured.entries.map { CodeEvaluationCopy.notMeasured(it) } + listOf(
            CodeEvaluationCopy.MET, CodeEvaluationCopy.PARTIALLY_MET, CodeEvaluationCopy.NOT_MET, CodeEvaluationCopy.NOT_MEASURED,
            CodeEvaluationCopy.AI_MET, CodeEvaluationCopy.AI_PARTIALLY_MET, CodeEvaluationCopy.AI_NOT_MET, CodeEvaluationCopy.AI_NOT_MEASURED,
            CodeEvaluationCopy.TESTS_ARE_NOT_UNDERSTANDING, CodeEvaluationCopy.AI_LABEL,
        )).joinToString(" ")
        assertFalse(Regex("\\d|%").containsMatchIn(all), "no score, count or percentage")
        for (word in listOf("başarısız", "hata yaptın", "kopya", "puan")) assertFalse(word in all.lowercase(Locale.forLanguageTag("tr")), word)
    }
}
