package coach.presentation

import coach.model.AttributionOutcome
import coach.model.MisconceptionRow
import coach.model.VersionedRef
import coach.model.WeaknessSignal
import coach.model.WrongAnswerFinding
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** A wrong answer as the learner reads it (14B, `WAAX-v0`): a signal, never a verdict. */
class WrongAnswerPresentationTest {

    private val turkish: Locale = Locale.forLanguageTag("tr")
    private val objective = VersionedRef("objective.c.pointers.write_through", 1)
    private val addressValue = MisconceptionRow(VersionedRef("misconception.c.pointers.address_value", 1), objective,
        "adres ile değer karışıklığı", "Adres ile değeri karıştırmış olabilir misin?")
    private val label: (VersionedRef) -> String = { "Pointer üzerinden yazma" }

    private fun finding(outcome: AttributionOutcome?, signal: WeaknessSignal) =
        WrongAnswerFinding(1, objective, outcome, listOf(addressValue to signal))

    @Test
    fun `a hypothesis is only ever put as its open question`() {
        val summary = WrongAnswerSummary.of(listOf(finding(AttributionOutcome.OBJECTIVE_WEAKNESS_HYPOTHESIS, WeaknessSignal.HYPOTHESIS)), label)
        assertEquals(listOf(addressValue.openQuestion), summary.openQuestions)
        assertTrue(summary.named.isEmpty())
        assertTrue(summary.lines.none { addressValue.name in it }, "a hypothesis is never stated as a finding")
    }

    @Test
    fun `a supported or confirmed label is named, a resolved one is not`() {
        for (signal in listOf(WeaknessSignal.SUPPORTED, WeaknessSignal.CONFIRMED)) {
            val summary = WrongAnswerSummary.of(listOf(finding(AttributionOutcome.OBJECTIVE_WEAKNESS_SUPPORTED, signal)), label)
            assertEquals(listOf(WrongAnswerCopy.named(addressValue.name)), summary.named)
            assertTrue(summary.openQuestions.isEmpty())
        }
        for (signal in listOf(WeaknessSignal.RESOLVED, WeaknessSignal.NONE)) {
            val summary = WrongAnswerSummary.of(listOf(finding(AttributionOutcome.OBJECTIVE_WEAKNESS_SUPPORTED, signal)), label)
            assertTrue(summary.named.isEmpty() && summary.openQuestions.isEmpty())
        }
    }

    @Test
    fun `a label on a row that blamed nothing is not mentioned`() {
        for (outcome in listOf(null, AttributionOutcome.NOT_ATTRIBUTABLE, AttributionOutcome.CONTENT_OR_ENVIRONMENT_ISSUE,
                               AttributionOutcome.PREREQUISITE_SIGNAL, AttributionOutcome.POSITIVE_RECOVERY_EVIDENCE)) {
            for (signal in WeaknessSignal.entries) {
                val summary = WrongAnswerSummary.of(listOf(finding(outcome, signal)), label)
                assertTrue(summary.openQuestions.isEmpty() && summary.named.isEmpty(), "$outcome $signal")
            }
        }
    }

    @Test
    fun `work that cannot be attributed, or rests on a missing prerequisite, is said not to count against the learner`() {
        assertTrue("hanene yazılmadı" in WrongAnswerCopy.outcome(AttributionOutcome.NOT_ATTRIBUTABLE))
        assertTrue("hanene yazılmadı" in WrongAnswerCopy.outcome(AttributionOutcome.CONTENT_OR_ENVIRONMENT_ISSUE))
        assertTrue("suçlamıyor" in WrongAnswerCopy.outcome(AttributionOutcome.PREREQUISITE_SIGNAL))
        assertTrue("bir sonuç çıkarılmadı" in WrongAnswerCopy.outcome(AttributionOutcome.OBJECTIVE_WEAKNESS_HYPOTHESIS))
        assertTrue("silinmedi" in WrongAnswerCopy.outcome(AttributionOutcome.VERIFICATION_DUE))
    }

    @Test
    fun `nothing the summary says claims, counts or blames`() {
        val copy = (AttributionOutcome.entries.map { WrongAnswerCopy.outcome(it) } + WrongAnswerCopy.outcome(null) +
            WrongAnswerCopy.HEADLINE + WrongAnswerCopy.named("x"))
        val banned = listOf("%", "puan", "başarısız", "zayıf", "eksik", "öğrendin", "ustalaştın", "geride", "borç", "hile", "tembel", "dikkatsiz", "kaldın", "geçemedin")
        for (text in copy) {
            val lower = text.lowercase(turkish)
            for (word in banned) assertFalse(word in lower, "$word in: $text")
            assertFalse(text.any { it.isDigit() }, "a number in: $text")
        }
    }
}
