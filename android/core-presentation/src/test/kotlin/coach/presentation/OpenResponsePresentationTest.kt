package coach.presentation

import coach.model.ComponentResult
import coach.model.CriterionVerdict
import coach.model.EvaluationResult
import coach.model.EvaluatorRef
import coach.model.OpenResponseNotMeasured
import coach.model.OpenResponseVerdict
import coach.model.OutcomeSignal
import coach.model.RubricCriterion
import coach.model.RubricFinding
import coach.model.VersionedRef
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** What is said after an open response (14F): no grade, an AI labelled, a waiting answer never called wrong. */
class OpenResponsePresentationTest {

    private val objective = VersionedRef("objective.c.pointers.explain_write_through", 1)
    private val criteria = listOf(
        RubricCriterion("names_target", objective, "*p, p'nin gösterdiği yeri ifade eder."),
        RubricCriterion("names_effect", objective, "*p = 5, o yerdeki değeri 5 yapar."),
    )

    @Test
    fun `a key's verdict says only whether the answer matched, with nothing beside it`() {
        val met = OpenResponseVerdict.Measured(EvaluationResult.Verified(listOf(ComponentResult(objective, OutcomeSignal.MET)), EvaluatorRef("deterministic", "answer_key", "k")))
        assertEquals(listOf(objective to OpenResponseCopy.KEY_MET), OpenResponsePresentation.lines(met))
        assertTrue(OpenResponsePresentation.notes(met).isEmpty())
        val notMet = OpenResponseVerdict.Measured(EvaluationResult.Verified(listOf(ComponentResult(objective, OutcomeSignal.NOT_MET)), EvaluatorRef("deterministic", "answer_key", "k")))
        assertEquals(listOf(objective to OpenResponseCopy.KEY_NOT_MET), OpenResponsePresentation.lines(notMet))
    }

    @Test
    fun `an AI's judgement is its view, criterion by criterion, labelled, and style is said not to count`() {
        val verdict = OpenResponseVerdict.Measured(EvaluationResult.Provisional(listOf(ComponentResult(objective, OutcomeSignal.PARTIALLY_MET)),
            EvaluatorRef("p", "m", "v"), rubricFindings = listOf(RubricFinding("names_target", CriterionVerdict.MET), RubricFinding("names_effect", CriterionVerdict.UNCLEAR))))
        assertEquals(listOf(objective to OpenResponseCopy.AI_PARTIALLY_MET), OpenResponsePresentation.lines(verdict))
        assertEquals(listOf("${OpenResponseCopy.CRITERION_MET} ${criteria[0].statement}", "${OpenResponseCopy.CRITERION_UNCLEAR} ${criteria[1].statement}"),
            OpenResponsePresentation.findings(verdict, criteria))
        assertEquals(listOf(OpenResponseCopy.AI_LABEL, OpenResponseCopy.NOT_STYLE), OpenResponsePresentation.notes(verdict))
        assertTrue(OpenResponseCopy.AI_PARTIALLY_MET.startsWith("AI'a göre"))
    }

    @Test
    fun `a waiting answer is never called wrong, offers the self-check and asking again, and nothing counts`() {
        val waiting = OpenResponsePresentation.notMeasured(OpenResponseVerdict.NotMeasured(OpenResponseNotMeasured.EVALUATOR_DID_NOT_ANSWER))
        assertEquals(listOf(OpenResponseCopy.notMeasured(OpenResponseNotMeasured.EVALUATOR_DID_NOT_ANSWER), OpenResponseCopy.SELF_CHECK_OFFER, OpenResponseCopy.ASK_AGAIN), waiting)
        for (reason in OpenResponseNotMeasured.entries - OpenResponseNotMeasured.NOTHING_SUBMITTED) {
            assertTrue("yanlış sayılmadı" in OpenResponseCopy.notMeasured(reason), reason.id)
        }
        assertTrue("kanıt sayılmaz" in OpenResponseCopy.SELF_CHECK_OFFER && "taze ölçüm" in OpenResponseCopy.SELF_CHECK_OFFER)
        assertEquals(listOf(OpenResponseCopy.SELF_CHECK_LABEL) + criteria.map { it.statement }, OpenResponsePresentation.selfCheck(criteria))
        val all = (OpenResponseNotMeasured.entries.map { OpenResponseCopy.notMeasured(it) } + listOf(OpenResponseCopy.KEY_MET, OpenResponseCopy.KEY_NOT_MET,
            OpenResponseCopy.AI_MET, OpenResponseCopy.AI_PARTIALLY_MET, OpenResponseCopy.AI_NOT_MET, OpenResponseCopy.AI_NOT_MEASURED, OpenResponseCopy.AI_LABEL,
            OpenResponseCopy.NOT_STYLE, OpenResponseCopy.SELF_CHECK_OFFER, OpenResponseCopy.SELF_CHECK_LABEL, OpenResponseCopy.ASK_AGAIN)).joinToString(" ")
        assertFalse(Regex("\\d|%").containsMatchIn(all), "no score, count or percentage")
        for (word in listOf("puan", "not:", "başarısız", "kopya")) assertFalse(word in all.lowercase(Locale.forLanguageTag("tr")), word)
    }
}
