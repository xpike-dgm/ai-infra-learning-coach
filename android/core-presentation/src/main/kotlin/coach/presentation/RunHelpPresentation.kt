package coach.presentation

import coach.model.AssistanceLevel
import coach.model.ComprehensionCheck
import coach.model.ComprehensionOffer
import coach.model.ComprehensionResult
import coach.model.ExplanationForm
import coach.model.ExplanationOption
import coach.model.TutorIntent
import coach.model.TutorMissing
import coach.model.TutorOutcome
import coach.model.TutorPreparation

/**
 * Help inside a running task as the runner draws it (16G, `HLPX-v0`): which asks fit the moment, what an ask still needs,
 * and what came back — every sentence an earlier step's (`TUTX-v0` 14A, `ALEX-v0` 14C, `ACCX-v0` 14E), only arranged.
 *
 * Help is always requestable (`TRUX-v0` §8.1), and asking is never the slower or discouraged path. Nothing here claims
 * what the learner can do, and no panel is a fault.
 */
data class HelpView(
    /** The asks that fit this moment, in the vocabulary's order; asking any of them is never a weakness. */
    val intents: List<Pair<TutorIntent, String>>,
    val panel: HelpPanel? = null,
)

/** One open help panel. Each carries only text core already decided. */
sealed interface HelpPanel {
    val state: TutorPanelState

    /** How much the answer may reveal, chosen by the learner while the answer is open (14A). */
    data class ChooseLevel(val intent: TutorIntent, val prompt: String, val levels: List<Pair<AssistanceLevel, String>>) : HelpPanel {
        override val state = TutorPanelState.LEVEL_NEEDED
    }

    /** What H3/H4 changes, said before anything is shown (`TRUX-v0` §8.3). */
    data class Disclose(val intent: TutorIntent, val text: String) : HelpPanel {
        override val state = TutorPanelState.DISCLOSURE_NEEDED
    }

    /** The learner's own question, in their own words. */
    data class AskQuestion(val prompt: String) : HelpPanel {
        override val state = TutorPanelState.INCOMPLETE
    }

    /** The forms another explanation can come in (14C): the learner decides, no hidden ranking. */
    data class Menu(val prompt: String, val options: List<Pair<ExplanationForm, String>>) : HelpPanel {
        override val state = TutorPanelState.LEVEL_NEEDED
    }

    /** A redirect, something missing, or nothing shown — said plainly, never the learner's fault. */
    data class Message(val text: String, override val state: TutorPanelState) : HelpPanel

    data object Waiting : HelpPanel {
        override val state = TutorPanelState.WAITING
    }

    /** Help the learner actually saw, with where it came from and what it changed. */
    data class Shown(val text: String, val notes: List<String>) : HelpPanel {
        override val state = TutorPanelState.SHOWN
    }

    /** The tutor's practice question about the learner's own code (14E), waiting for the learner's answer. */
    data class Practice(val question: String, val notes: List<String>) : HelpPanel {
        override val state = TutorPanelState.SHOWN
    }
}

/** The comprehension check after code the learner did not write alone (14E), one written check at a time. */
sealed interface ComprehensionView {
    data class Check(val offer: String, val number: Int, val prompt: String, val choices: List<Pair<String, String>>) : ComprehensionView
    data class Result(val lines: List<String>, val more: Boolean) : ComprehensionView

    /** Nothing written: the tutor may ask about the code — practice, never evidence. */
    data class TutorPractice(val offer: String) : ComprehensionView
}

object RunHelpCopy {
    const val HELP = "Yardım iste"
    const val CLOSE = "Yardımı kapat"
    const val WAITING = "Cevap bekleniyor."
    const val ASK = "Sor"
    const val QUESTION_LABEL = "Sorun"
    const val ACKNOWLEDGE = "Anladım, yardımı göster"
    const val DECLINE = "Şimdilik istemiyorum"
    const val SKIP_CHECK = "Geç"
    const val NEXT_CHECK = "Sonraki kontrol"
    const val PRACTICE_ASK = "AI bir soru sorsun"
    const val PRACTICE_ANSWER_LABEL = "Cevabın"
    const val PRACTICE_SEND = "Cevabımı gönder"
}

object RunHelpPresentation {

    /**
     * The asks that fit the moment. While the answer is open: a hint, the concept another way, a question. Once it froze:
     * why it went the way it did, the concept another way, a question. A gloss is not offered: which segment is the
     * learning target is the task's metadata (`TEIP-v0` §4), no task declares it yet, and guessing it is forbidden.
     */
    fun view(answerFrozen: Boolean, panel: HelpPanel? = null): HelpView {
        val intents = if (answerFrozen) {
            listOf(TutorIntent.EXPLAIN_MISTAKE, TutorIntent.EXPLAIN_DIFFERENTLY, TutorIntent.QUESTION)
        } else {
            listOf(TutorIntent.HINT, TutorIntent.EXPLAIN_DIFFERENTLY, TutorIntent.QUESTION)
        }
        return HelpView(intents.map { it to TutorCopy.intent(it) }, panel)
    }

    /** What an ask still needs, in the rules' order. A ready ask is waiting for the tutor. */
    fun panel(intent: TutorIntent, preparation: TutorPreparation): HelpPanel = when (preparation) {
        is TutorPreparation.Ready -> HelpPanel.Waiting
        TutorPreparation.LevelNeeded -> HelpPanel.ChooseLevel(intent, TutorCopy.LEVEL_PROMPT, AssistanceLevel.entries.map { it to TutorCopy.level(it) })
        is TutorPreparation.DisclosureNeeded -> HelpPanel.Disclose(intent, TutorPresentation.disclosure(preparation.timing))
        is TutorPreparation.Redirect -> HelpPanel.Message(TutorPresentation.redirect(preparation.reason), TutorPanelState.REDIRECTED)
        is TutorPreparation.Incomplete ->
            if (preparation.missing == TutorMissing.QUESTION_TEXT) HelpPanel.AskQuestion(TutorCopy.MISSING_QUESTION)
            else HelpPanel.Message(TutorPresentation.missing(preparation.missing), TutorPanelState.INCOMPLETE)
    }

    /** What came back. [explanation] is an alternative explanation (14C), whose notes always give the way back. */
    fun panel(outcome: TutorOutcome, explanation: Boolean = false): HelpPanel = when (outcome) {
        is TutorOutcome.Shown -> HelpPanel.Shown(outcome.text, if (explanation) AlternativeExplanationPresentation.notes(outcome) else TutorPresentation.notes(outcome))
        is TutorOutcome.NotShown -> HelpPanel.Message(TutorPresentation.notShown(outcome.reason), TutorPanelState.NOT_SHOWN)
    }

    fun menu(options: List<ExplanationOption>): HelpPanel =
        HelpPanel.Menu(AlternativeExplanationCopy.MENU_PROMPT, options.map { it.form to AlternativeExplanationPresentation.line(it) })

    fun nothingToShow(): HelpPanel = HelpPanel.Message(AlternativeExplanationPresentation.nothingToShow(), TutorPanelState.NOT_SHOWN)

    /** The tutor's practice question about the code, labelled as practice (14E). */
    fun practice(outcome: TutorOutcome): HelpPanel = when (outcome) {
        is TutorOutcome.Shown -> HelpPanel.Practice(outcome.text, ComprehensionPresentation.practiceNotes(outcome))
        is TutorOutcome.NotShown -> HelpPanel.Message(TutorPresentation.notShown(outcome.reason), TutorPanelState.NOT_SHOWN)
    }

    /** The tutor's response to the learner's practice answer: still practice. */
    fun practiceResponse(outcome: TutorOutcome): HelpPanel = when (outcome) {
        is TutorOutcome.Shown -> HelpPanel.Shown(outcome.text, ComprehensionPresentation.practiceNotes(outcome))
        is TutorOutcome.NotShown -> HelpPanel.Message(TutorPresentation.notShown(outcome.reason), TutorPanelState.NOT_SHOWN)
    }

    /** The [index]th written check of [offer], or the tutor's practice offer; `null` when nothing is offered or none is left. */
    fun comprehension(offer: ComprehensionOffer, index: Int): ComprehensionView? = when (offer) {
        ComprehensionOffer.NotOffered -> null
        ComprehensionOffer.TutorPractice -> ComprehensionView.TutorPractice(ComprehensionCopy.TUTOR_OFFER)
        is ComprehensionOffer.Written -> offer.checks.getOrNull(index)?.let { check(it, index) }
    }

    fun comprehensionResult(result: ComprehensionResult, more: Boolean): ComprehensionView =
        ComprehensionView.Result(ComprehensionPresentation.result(result), more)

    private fun check(check: ComprehensionCheck, index: Int) =
        ComprehensionView.Check(ComprehensionCopy.OFFER, index + 1, check.prompt, check.choices.toList())
}
