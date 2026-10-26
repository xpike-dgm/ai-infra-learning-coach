package coach.wiring

import coach.application.RunHelp
import coach.model.AnsweredItem
import coach.model.AssistanceLevel
import coach.model.ComprehensionOffer
import coach.model.ExplanationForm
import coach.model.RunAnswer
import coach.model.TutorIntent
import coach.model.TutorOutcome
import coach.model.TutorPreparation
import coach.model.TutorRules
import coach.presentation.HelpPanel
import coach.presentation.PauseDecision
import coach.presentation.PausePolicy
import coach.presentation.RunHelpPresentation
import coach.presentation.TaskRunCopy
import coach.presentation.TaskRunPresentation
import coach.presentation.TaskRunStage

/**
 * The 16G actions inside a run (`HLPX-v0`): judged answers, help, the comprehension check and a durable pause. Like the
 * run session it decides nothing — every rule is core's, every sentence is `core-presentation`'s, and everything that
 * counts is in the store the moment it is recorded. It only carries the learner's actions there and draws what returns.
 *
 * [finish] is the run's own end (processed once, 16F); [show] draws a stage.
 */
internal class TaskRunFlow(
    private val app: CoachApplication,
    private val run: TaskRunSession,
    private val show: (TaskRunStage) -> Unit,
    private val finish: ((List<String>) -> Unit) -> Unit,
) {
    private val frozen get() = run.lastAnswer != null

    /** Draws the current question or answer again, with the help panel as it now is. */
    fun redraw() {
        val answered = run.answeredStage
        show(if (frozen && answered != null) answered.copy(help = run.helpView(frozen = true)) else run.questionStage())
    }

    // ------------------------------------------------------------ judged answers

    /** An answer-key answer was recorded (16F): kept for later additions, and what a wrong answer said is read (14B). */
    fun keyAnswered(answered: AnsweredItem, response: String, stage: TaskRunStage.Answered) {
        run.lastResponse = response.trim()
        run.answeredStage = stage
        run.closeHelp()
        val item = run.current ?: return
        app.wrongAnswer(run.run, item, answered) { summary ->
            if (summary != null && run.lastAnswer == answered) {
                run.answeredStage = run.answeredStage?.copy(wrongAnswer = summary)
                redraw()
            }
        }
    }

    /** A code answer with its pasted report, or an open answer (16G); help shown while it was open goes with it. */
    fun submitJudged(response: String, report: String) {
        val item = run.current ?: return
        val provenance = run.provenance ?: return
        show(TaskRunStage.Working)
        app.answerJudged(run.run, item, run.kind, response, report, provenance, run.helpWithAnswer()) { judged ->
            val stage = judged.stage
            val recorded = judged.answer as? RunAnswer.Recorded
            if (stage == null || recorded == null) {
                show(run.questionStage(notice = judged.notice))
            } else {
                run.answered += recorded.answered
                run.lastAnswer = recorded.answered
                run.lastResponse = response
                run.answeredStage = stage
                run.checks = (recorded.offer as? ComprehensionOffer.Written)?.checks.orEmpty()
                run.checkIndex = 0
                run.closeHelp()
                redraw()
            }
        }
    }

    /** A pending open answer judged again, only because the learner asked (14F). */
    fun reevaluate() {
        val item = run.current ?: return
        val pending = run.lastAnswer?.takeIf { it.pending } ?: return
        show(TaskRunStage.Working)
        app.reevaluate(run.run, item, pending, run.lastResponse) { judged ->
            val recorded = judged.answer as? RunAnswer.Recorded
            if (recorded != null && judged.stage != null) {
                // The same frozen attempt, now with evidence — never a second answer.
                run.answered.replaceAll { if (it.attemptId == pending.attemptId) recorded.answered else it }
                run.lastAnswer = recorded.answered
                run.answeredStage = judged.stage
            }
            redraw()
        }
    }

    fun selfCheck() {
        val item = run.current ?: return
        val attemptId = run.lastAnswer?.attemptId ?: return
        app.selfCheck(item, attemptId) { lines ->
            run.answeredStage = run.answeredStage?.copy(selfCheck = lines)
            redraw()
        }
    }

    // ------------------------------------------------------------ help

    fun openHelp() {
        run.closeHelp()
        run.helpOpen = true
        redraw()
    }

    fun closeHelp() {
        run.closeHelp()
        redraw()
    }

    fun intent(intent: TutorIntent) {
        run.closeHelp()
        run.helpOpen = true
        run.helpIntent = intent
        if (intent == TutorIntent.EXPLAIN_DIFFERENTLY) menu() else ask()
    }

    fun level(level: AssistanceLevel) {
        run.helpLevel = level
        run.helpForm?.let(::explain) ?: ask()
    }

    fun acknowledge() {
        run.helpAcknowledged = true
        run.helpForm?.let(::explain) ?: ask()
    }

    fun question(text: String) {
        run.helpQuestion = text
        ask()
    }

    /** Nothing is sent until the rules say the ask is ready; what it still needs is shown instead (14A). */
    private fun ask() {
        val item = run.current ?: return
        val intent = run.helpIntent ?: return
        val ask = RunHelp.askFor(run.run, item, run.currentPrompt, intent, run.lastAnswer, run.lastResponse, run.helpLevel, run.helpAcknowledged, run.helpQuestion)
        val preparation = TutorRules.prepare(ask)
        if (preparation !is TutorPreparation.Ready) {
            run.helpPanel = RunHelpPresentation.panel(intent, preparation)
            redraw()
            return
        }
        run.helpPanel = HelpPanel.Waiting
        redraw()
        app.askHelp(ask, item, run.lastAnswer?.attemptId) { asked -> shown(intent, asked, explanation = false) }
    }

    private fun menu() {
        val objective = run.current?.targetObjectives?.firstOrNull() ?: return
        app.explanationMenu(objective, run.seenForms.toSet()) { options ->
            run.helpPanel = RunHelpPresentation.menu(options)
            redraw()
        }
    }

    fun form(form: ExplanationForm) {
        run.helpForm = form
        explain(form)
    }

    /** One form, written first (14C); nothing true to show is said plainly. */
    private fun explain(form: ExplanationForm) {
        val item = run.current ?: return
        val objective = item.targetObjectives.firstOrNull() ?: return
        val base = RunHelp.askFor(run.run, item, run.currentPrompt, TutorIntent.EXPLAIN_DIFFERENTLY, run.lastAnswer, run.lastResponse, run.helpLevel, run.helpAcknowledged)
        run.helpPanel = HelpPanel.Waiting
        redraw()
        app.explain(base, objective, form, item, run.lastAnswer?.attemptId) { asked ->
            if (asked == null) {
                run.helpPanel = RunHelpPresentation.nothingToShow()
                redraw()
            } else {
                if (asked is RunHelp.Asked.Answered && asked.outcome is TutorOutcome.Shown) run.seenForms += form
                shown(TutorIntent.EXPLAIN_DIFFERENTLY, asked, explanation = true)
            }
        }
    }

    /**
     * What came back. Help shown while the answer is open travels with the attempt (`TRUX-v0` §8.5); after it froze, core
     * already recorded it on that attempt.
     */
    private fun shown(intent: TutorIntent, asked: RunHelp.Asked, explanation: Boolean) {
        run.helpPanel = when (asked) {
            is RunHelp.Asked.NotReady -> RunHelpPresentation.panel(intent, asked.preparation)
            is RunHelp.Asked.Answered -> {
                if (!frozen) (asked.outcome as? TutorOutcome.Shown)?.event?.let(run.pendingHelp::add)
                RunHelpPresentation.panel(asked.outcome, explanation)
            }
        }
        redraw()
    }

    // ------------------------------------------------------------ the comprehension check (14E)

    fun checkChoice(chosen: String?) {
        val item = run.current ?: return
        val attemptId = run.lastAnswer?.attemptId ?: return
        val check = run.checks.getOrNull(run.checkIndex) ?: return
        app.comprehensionAnswer(run.run, item, check, chosen, attemptId) { result ->
            run.checkIndex += 1
            run.answeredStage = run.answeredStage?.copy(comprehension = RunHelpPresentation.comprehensionResult(result, more = run.checkIndex < run.checks.size))
            redraw()
        }
    }

    fun nextCheck() {
        run.answeredStage = run.answeredStage?.copy(comprehension = RunHelpPresentation.comprehension(ComprehensionOffer.Written(run.checks), run.checkIndex))
        redraw()
    }

    /** The tutor's practice question about the code (14E): practice, never evidence. */
    fun practiceAsk() {
        val item = run.current ?: return
        val answered = run.lastAnswer ?: return
        run.closeHelp()
        run.helpOpen = true
        run.helpPanel = HelpPanel.Waiting
        redraw()
        val ask = RunHelp.askFor(run.run, item, run.currentPrompt, TutorIntent.CHECK_UNDERSTANDING, answered, run.lastResponse)
        app.practice(ask, item, answered.attemptId) { asked -> practiced(asked, response = false) }
    }

    fun practiceAnswer(answer: String) {
        val item = run.current ?: return
        val answered = run.lastAnswer ?: return
        val question = run.practiceQuestion ?: return
        run.helpPanel = HelpPanel.Waiting
        redraw()
        val ask = RunHelp.askFor(run.run, item, run.currentPrompt, TutorIntent.CHECK_UNDERSTANDING, answered, run.lastResponse,
            checkQuestion = question, learnerAnswer = answer)
        app.practice(ask, item, answered.attemptId) { asked -> practiced(asked, response = true) }
    }

    private fun practiced(asked: RunHelp.Asked?, response: Boolean) {
        run.helpPanel = when (asked) {
            null -> RunHelpPresentation.nothingToShow()
            is RunHelp.Asked.NotReady -> RunHelpPresentation.panel(TutorIntent.CHECK_UNDERSTANDING, asked.preparation)
            is RunHelp.Asked.Answered -> {
                run.practiceQuestion = if (response) null else (asked.outcome as? TutorOutcome.Shown)?.text
                if (response) RunHelpPresentation.practiceResponse(asked.outcome) else RunHelpPresentation.practice(asked.outcome)
            }
        }
        redraw()
    }

    // ------------------------------------------------------------ a durable pause (11C)

    /**
     * Pauses between questions. `PausePolicy` decides whether this is a safe checkpoint from what the boundary confirms;
     * a pause that is not durable writes nothing and says so. A durable one writes one checkpoint and ends the run the
     * way every run ends (16F), so what was recorded is processed.
     */
    fun pause() {
        val confirmed = TaskRunPresentation.pauseConditions(
            betweenQuestions = frozen,
            helpInFlight = run.helpPanel == HelpPanel.Waiting,
            evaluationPending = run.lastAnswer?.pending == true,
        )
        when (val decision = PausePolicy.classify(highStakesWork = run.run.purpose in TutorRules.measuringPurposes, confirmed = confirmed)) {
            is PauseDecision.NotDurable -> {
                run.answeredStage = run.answeredStage?.copy(notice = TaskRunCopy.PAUSE_NOT_SAFE)
                redraw()
            }
            is PauseDecision.Durable -> {
                val shown = run.shown.map { it.ref }
                val remaining = run.run.items.filterNot { it in shown.toSet() }
                show(TaskRunStage.Working)
                app.pauseRun(run.run, decision.kind, shown, remaining) { checkpoint ->
                    finish { lines ->
                        show(if (checkpoint == null) TaskRunStage.Finished(listOf(TaskRunCopy.NOTHING_TO_PAUSE) + lines) else TaskRunPresentation.paused(decision, lines))
                    }
                }
            }
        }
    }
}
