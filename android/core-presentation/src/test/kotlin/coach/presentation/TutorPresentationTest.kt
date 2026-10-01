package coach.presentation

import coach.model.AssistanceEvent
import coach.model.AssistanceLevel
import coach.model.AssistanceScope
import coach.model.AssistanceSource
import coach.model.AssistanceTiming
import coach.model.PendingReason
import coach.model.TutorIntent
import coach.model.TutorMissing
import coach.model.TutorOutcome
import coach.model.TutorPreparation
import coach.model.TutorRedirectReason
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** What the tutor panel says (14A, `TUTX-v0` §14): never a fault, never a verdict, never the learner's blame. */
class TutorPresentationTest {

    /** Turkish copy is lowercased as Turkish (`AMTS-v0`): a default-locale transform would mangle the dotted capital I. */
    private val turkish: Locale = Locale.forLanguageTag("tr")

    private fun shown(source: AssistanceSource, converts: Boolean = false, ends: Boolean = false) = TutorOutcome.Shown(
        text = "…",
        source = source,
        event = AssistanceEvent(AssistanceLevel.H4, AssistanceTiming.DURING_ATTEMPT, AssistanceScope.TARGET_OBJECTIVE, source, true),
        tutorRef = null,
        revealsTargetReasoning = true,
        convertsItemToLearning = converts,
        endsDiagnosticFastPath = ends,
    )

    private val allCopy: List<String> = buildList {
        TutorIntent.entries.forEach { add(TutorCopy.intent(it)) }
        AssistanceLevel.entries.forEach { add(TutorCopy.level(it)) }
        PendingReason.entries.forEach { add(TutorPresentation.notShown(it)) }
        TutorRedirectReason.entries.forEach { add(TutorPresentation.redirect(it)) }
        TutorMissing.entries.forEach { add(TutorPresentation.missing(it)) }
        AssistanceTiming.entries.forEach { add(TutorPresentation.disclosure(it)) }
        add(TutorCopy.LEVEL_PROMPT)
        addAll(TutorPresentation.notes(shown(AssistanceSource.AI_GENERATED, converts = true, ends = true)))
        addAll(TutorPresentation.notes(shown(AssistanceSource.DETERMINISTIC_CONTENT)))
    }

    @Test
    fun `no tutor state wears the fault tone`() {
        for (state in TutorPanelState.entries) assertNotEquals(Tone.SYSTEM_FAULT, state.tone, state.id)
        assertEquals(Tone.NEUTRAL, TutorPanelState.NOT_SHOWN.tone)
        assertEquals(Tone.PENDING_UNRESOLVED, TutorPanelState.WAITING.tone)
    }

    @Test
    fun `the disclosure after an answer is frozen does not claim the attempt changed`() {
        for (timing in listOf(AssistanceTiming.BEFORE_ATTEMPT, AssistanceTiming.DURING_ATTEMPT)) {
            assertEquals(RunnerCopy.CONSEQUENCE_DISCLOSURE, TutorPresentation.disclosure(timing))
        }
        for (timing in listOf(AssistanceTiming.AFTER_SUBMIT, AssistanceTiming.AFTER_FAILURE)) {
            val text = TutorPresentation.disclosure(timing)
            assertEquals(TutorCopy.DISCLOSURE_AFTER_ANSWER, text)
            assertFalse("bağımsız kanıt sayılmaz" in text)
            assertTrue("etkilenmez" in text)
        }
    }

    @Test
    fun `nothing the tutor panel says claims progress, blames the learner or counts`() {
        val banned = listOf("%", "puan", "başarısız", "öğrendin", "ustalaştın", "seviye atla", "geride kaldın", "borç", "seri", "hile", "kopya", "tembel", "yine mi")
        for (text in allCopy) {
            val lower = text.lowercase(turkish)
            for (word in banned) assertFalse(word in lower, "$word in: $text")
            assertFalse(text.any { it.isDigit() }, "a number in: $text")
            assertFalse(text.contains(0x0307.toChar()), "U+0307 in: $text")
        }
    }

    @Test
    fun `help from AI is labelled as teaching text, never as a judgement`() {
        assertEquals(TutorCopy.AI_LABEL, TutorPresentation.notes(shown(AssistanceSource.AI_GENERATED)).first())
        assertEquals(TutorCopy.AUTHORED_LABEL, TutorPresentation.notes(shown(AssistanceSource.DETERMINISTIC_CONTENT)).first())
        assertTrue("değerlendirme ya da ilerleme bilgisi değildir" in TutorCopy.AI_LABEL)
    }

    @Test
    fun `a conversion and an ended fast path are said, never silent`() {
        val notes = TutorPresentation.notes(shown(AssistanceSource.AI_GENERATED, converts = true, ends = true))
        assertTrue(TutorCopy.CONVERTED_TO_LEARNING in notes)
        assertTrue(TutorCopy.FAST_PATH_ENDED in notes)
        assertEquals(listOf(TutorCopy.AI_LABEL), TutorPresentation.notes(shown(AssistanceSource.AI_GENERATED)))
    }

    @Test
    fun `every reason a reply was not shown says nothing was recorded and blames no one`() {
        for (reason in PendingReason.entries) {
            val text = TutorPresentation.notShown(reason)
            assertTrue("kaydedilmedi" in text, "$reason: $text")
        }
        assertTrue("senin hatan değil" in TutorPresentation.notShown(PendingReason.REFUSED))
    }

    @Test
    fun `the panel's state follows the contract's outcomes`() {
        assertEquals(TutorPanelState.LEVEL_NEEDED, TutorPresentation.state(TutorPreparation.LevelNeeded))
        assertEquals(TutorPanelState.DISCLOSURE_NEEDED, TutorPresentation.state(TutorPreparation.DisclosureNeeded(AssistanceLevel.H3, AssistanceTiming.DURING_ATTEMPT)))
        assertEquals(TutorPanelState.REDIRECTED, TutorPresentation.state(TutorPreparation.Redirect(TutorIntent.HINT, TutorRedirectReason.NO_ANSWER_IS_FROZEN_YET)))
        assertEquals(TutorPanelState.INCOMPLETE, TutorPresentation.state(TutorPreparation.Incomplete(TutorMissing.SEGMENT)))
        assertEquals(TutorPanelState.NOT_SHOWN, TutorPresentation.state(TutorOutcome.NotShown(PendingReason.REFUSED)))
        assertEquals(TutorPanelState.SHOWN, TutorPresentation.state(shown(AssistanceSource.AI_GENERATED)))
    }
}
