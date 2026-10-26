package coach.presentation

import coach.model.AnswerResult
import coach.model.AnsweredItem
import coach.model.AssistanceLevel
import coach.model.AssistanceTiming
import coach.model.CheckpointKind
import coach.model.CodeNotMeasured
import coach.model.CodeVerdict
import coach.model.ComponentResult
import coach.model.ComprehensionCheck
import coach.model.ComprehensionKind
import coach.model.ComprehensionOffer
import coach.model.ComprehensionResult
import coach.model.EvaluationResult
import coach.model.EvaluatorRef
import coach.model.IndependenceClass
import coach.model.OpenResponseNotMeasured
import coach.model.OpenResponseVerdict
import coach.model.OutcomeSignal
import coach.model.PendingReason
import coach.model.RunAnswer
import coach.model.RunItemKind
import coach.model.TutorIntent
import coach.model.TutorMissing
import coach.model.TutorOutcome
import coach.model.TutorPreparation
import coach.model.TutorRedirectReason
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 16G (`HLPX-v0`): help, judged answers and a pause inside a run, as the runner says them — never a verdict or a level. */
class RunHelpPresentationTest {

    private val turkish = java.util.Locale.forLanguageTag("tr")
    private val forbidden = listOf("yanlış yaptın", "başarısız oldun", "puan", "%", "seviye", "ustalaştın", "mastered", "hata yaptın", "suç", "ceza", "hile")
    private val item = VersionedRef("item.python.read_input.f01", 2)
    private val objective = VersionedRef("objective.python.read_input.read_line", 1)
    private val ref = EvaluatorRef("course", "tests", "code_test_report/1")

    private fun clean(lines: List<String>) = lines.forEach { line ->
        forbidden.forEach { word -> assertFalse(word in line.lowercase(turkish), "\"$word\" in: $line") }
    }

    @Test
    fun `the asks that fit the moment are offered, and a gloss is never guessed`() {
        assertEquals(listOf(TutorIntent.HINT, TutorIntent.EXPLAIN_DIFFERENTLY, TutorIntent.QUESTION), RunHelpPresentation.view(answerFrozen = false).intents.map { it.first })
        assertEquals(listOf(TutorIntent.EXPLAIN_MISTAKE, TutorIntent.EXPLAIN_DIFFERENTLY, TutorIntent.QUESTION), RunHelpPresentation.view(answerFrozen = true).intents.map { it.first })
        listOf(true, false).forEach { frozen ->
            val view = RunHelpPresentation.view(frozen)
            assertTrue(view.intents.none { it.first == TutorIntent.GLOSS }, "which segment is the target is task metadata no task declares")
            assertEquals(view.intents.map { TutorCopy.intent(it.first) }, view.intents.map { it.second })
        }
    }

    @Test
    fun `what an ask still needs is shown in the rules' order, and nothing is a fault`() {
        val level = assertIs<HelpPanel.ChooseLevel>(RunHelpPresentation.panel(TutorIntent.HINT, TutorPreparation.LevelNeeded))
        assertEquals(AssistanceLevel.entries, level.levels.map { it.first })
        assertEquals(TutorCopy.LEVEL_PROMPT, level.prompt)
        assertEquals(RunnerCopy.CONSEQUENCE_DISCLOSURE,
            assertIs<HelpPanel.Disclose>(RunHelpPresentation.panel(TutorIntent.HINT, TutorPreparation.DisclosureNeeded(AssistanceLevel.H4, AssistanceTiming.DURING_ATTEMPT))).text)
        assertEquals(TutorCopy.DISCLOSURE_AFTER_ANSWER,
            assertIs<HelpPanel.Disclose>(RunHelpPresentation.panel(TutorIntent.EXPLAIN_MISTAKE, TutorPreparation.DisclosureNeeded(AssistanceLevel.H4, AssistanceTiming.AFTER_FAILURE))).text)
        assertIs<HelpPanel.AskQuestion>(RunHelpPresentation.panel(TutorIntent.QUESTION, TutorPreparation.Incomplete(TutorMissing.QUESTION_TEXT)))
        assertEquals(TutorCopy.REDIRECT_ANSWER_FROZEN,
            assertIs<HelpPanel.Message>(RunHelpPresentation.panel(TutorIntent.HINT, TutorPreparation.Redirect(TutorIntent.EXPLAIN_MISTAKE, TutorRedirectReason.ANSWER_ALREADY_FROZEN))).text)
        PendingReason.entries.forEach { reason ->
            val panel = assertIs<HelpPanel.Message>(RunHelpPresentation.panel(TutorOutcome.NotShown(reason)))
            assertEquals(TutorPresentation.notShown(reason), panel.text)
            assertEquals(Tone.NEUTRAL, panel.state.tone, "a tutor that cannot answer is not a fault")
        }
        assertEquals(Tone.PENDING_UNRESOLVED, HelpPanel.Waiting.state.tone)
    }

    @Test
    fun `help that was shown says where it came from, and another explanation always gives the way back`() {
        val shown = TutorOutcome.Shown("metin", coach.model.AssistanceSource.AI_GENERATED, null, null, false, false, false)
        assertEquals(TutorPresentation.notes(shown), assertIs<HelpPanel.Shown>(RunHelpPresentation.panel(shown)).notes)
        val alternative = assertIs<HelpPanel.Shown>(RunHelpPresentation.panel(shown, explanation = true))
        assertTrue(AlternativeExplanationCopy.BACK_TO_CANONICAL in alternative.notes)
        assertTrue(ComprehensionCopy.TUTOR_PRACTICE in assertIs<HelpPanel.Practice>(RunHelpPresentation.practice(shown)).notes, "practice is labelled as practice")
        assertTrue(ComprehensionCopy.TUTOR_PRACTICE in assertIs<HelpPanel.Shown>(RunHelpPresentation.practiceResponse(shown)).notes)
        assertEquals(AlternativeExplanationCopy.NOTHING_TRUE_TO_SHOW, assertIs<HelpPanel.Message>(RunHelpPresentation.nothingToShow()).text)
    }

    @Test
    fun `the comprehension check is offered one written check at a time, and skipping is not wrong`() {
        val check = ComprehensionCheck(VersionedRef("comprehension.python.read_input", 1), item, objective, ComprehensionKind.LINE_PURPOSE,
            "explanation", "Bu satır ne yapar?", mapOf("a" to "Okur.", "b" to "Yazar."), "a")
        assertNull(RunHelpPresentation.comprehension(ComprehensionOffer.NotOffered, 0))
        assertIs<ComprehensionView.TutorPractice>(RunHelpPresentation.comprehension(ComprehensionOffer.TutorPractice, 0))
        val first = assertIs<ComprehensionView.Check>(RunHelpPresentation.comprehension(ComprehensionOffer.Written(listOf(check)), 0))
        assertEquals(1, first.number)
        assertEquals(listOf("a" to "Okur.", "b" to "Yazar."), first.choices)
        assertNull(RunHelpPresentation.comprehension(ComprehensionOffer.Written(listOf(check)), 1))
        val skipped = assertIs<ComprehensionView.Result>(RunHelpPresentation.comprehensionResult(ComprehensionResult.Skipped, more = false))
        assertEquals(listOf(ComprehensionCopy.SKIPPED), skipped.lines)
    }

    @Test
    fun `a code item names what to copy, and a report that measured nothing says why without calling it wrong`() {
        assertNull(TaskRunPresentation.codeTransfer(item, runner = null, suite = "{}"))
        assertNull(TaskRunPresentation.codeTransfer(item, runner = "r", suite = null))
        val transfer = assertTypedNotNull(TaskRunPresentation.codeTransfer(item, "r", "s"))
        assertTrue(transfer.steps.any { "item.python.read_input.f01-v2.json" in it } && transfer.steps.any { TaskRunCopy.RUNNER_FILE in it })
        CodeNotMeasured.entries.forEach { reason ->
            assertEquals(CodeEvaluationCopy.notMeasured(reason), TaskRunPresentation.notRecorded(RunAnswer.NotRecorded(code = reason), RunItemKind.CODE_TESTS))
        }
        assertEquals(TaskRunCopy.CODE_BLANK, TaskRunPresentation.notRecorded(RunAnswer.NotRecorded(code = CodeNotMeasured.NOTHING_SUBMITTED, blank = true), RunItemKind.CODE_TESTS))
        assertEquals(TaskRunCopy.BLANK_IS_A_SKIP, TaskRunPresentation.notRecorded(RunAnswer.NotRecorded(blank = true), RunItemKind.RUBRIC))
        assertEquals(TaskRunCopy.TOO_LONG, TaskRunPresentation.notRecorded(RunAnswer.NotRecorded(tooLong = true), RunItemKind.RUBRIC))
        clean(transfer.steps + CodeNotMeasured.entries.map(CodeEvaluationCopy::notMeasured))
    }

    private fun <T : Any> assertTypedNotNull(value: T?): T { assertTrue(value != null); return value!! }

    @Test
    fun `a recorded code answer is worded per Objective, never revealed as an accepted answer, and passing is not understanding`() {
        val answered = AnsweredItem(item, 4, AnswerResult.RIGHT, listOf(1), independence = IndependenceClass.INDEPENDENT)
        val verdict = CodeVerdict.Measured(EvaluationResult.Verified(listOf(ComponentResult(objective, OutcomeSignal.MET)), ref))
        val stage = TaskRunPresentation.answered(RunAnswer.Recorded(answered, code = verdict), RunItemKind.CODE_TESTS, TaskRunPresentation::objectiveLabel)
        assertEquals(TaskRunCopy.resultLines.getValue(AnswerResult.RIGHT), stage.result)
        assertEquals("read line: ${CodeEvaluationCopy.MET}", stage.details.first())
        assertTrue(CodeEvaluationCopy.TESTS_ARE_NOT_UNDERSTANDING in stage.details)
        assertFalse(stage.canReveal, "a code item has no accepted answer to show")
        assertFalse(stage.canSelfCheck)
        assertEquals(RunnerState.FEEDBACK_RESOLVED, stage.state)
        // Code the learner did not write opens an independent recheck, said once and never as a penalty.
        val copied = TaskRunPresentation.answered(RunAnswer.Recorded(answered.copy(independence = IndependenceClass.REQUIRES_INDEPENDENT_RECHECK), code = verdict),
            RunItemKind.CODE_TESTS, TaskRunPresentation::objectiveLabel)
        assertTrue(ComprehensionCopy.RECHECK in copied.details)
        clean(stage.details) // RECHECK says "Bu bir ceza değil": a negation, 14E's own sentence.
    }

    @Test
    fun `an open answer that waits for an evaluator is evaluation pending, offers the rubric and asking again, and is not wrong`() {
        val pending = AnsweredItem(item, 5, AnswerResult.NOT_MEASURED, emptyList(), pending = true)
        val stage = TaskRunPresentation.answered(RunAnswer.Recorded(pending, open = OpenResponseVerdict.NotMeasured(OpenResponseNotMeasured.EVALUATOR_DID_NOT_ANSWER)),
            RunItemKind.RUBRIC, TaskRunPresentation::objectiveLabel)
        assertTrue(stage.pending)
        assertEquals(RunnerState.EVALUATION_PENDING, stage.state)
        assertTrue(stage.canSelfCheck)
        assertTrue(OpenResponseCopy.SELF_CHECK_OFFER in stage.details)
        assertEquals(TaskRunCopy.resultLines.getValue(AnswerResult.NOT_MEASURED), stage.result)
        clean(stage.details + stage.result)
    }

    @Test
    fun `an open help panel is assistance open, and only a boundary between questions can be a safe checkpoint`() {
        val question = TaskRunStage.Question(1, "soru", null, false, null, help = RunHelpPresentation.view(false, HelpPanel.Waiting))
        assertEquals(RunnerState.ASSISTANCE_OPEN, question.state)
        assertEquals(RunnerState.ACTIVE_WORK, question.copy(help = RunHelpPresentation.view(false)).state, "the section open with no panel is still work")
        assertEquals(emptySet(), TaskRunPresentation.pauseConditions(betweenQuestions = false, helpInFlight = false, evaluationPending = false))
        assertIs<PauseDecision.NotDurable>(PausePolicy.classify(false, TaskRunPresentation.pauseConditions(false, false, false)))
        assertEquals(SafeCheckpointCondition.entries.toSet(), TaskRunPresentation.pauseConditions(true, false, false))
        assertIs<PauseDecision.NotDurable>(PausePolicy.classify(false, TaskRunPresentation.pauseConditions(true, helpInFlight = true, evaluationPending = false)))
        assertIs<PauseDecision.NotDurable>(PausePolicy.classify(false, TaskRunPresentation.pauseConditions(true, helpInFlight = false, evaluationPending = true)))
        val durable = assertIs<PauseDecision.Durable>(PausePolicy.classify(false, TaskRunPresentation.pauseConditions(true, false, false)))
        assertEquals(CheckpointKind.CHECKPOINT_PAUSE, durable.kind)
        val paused = TaskRunPresentation.paused(durable, listOf("bitti"))
        assertEquals(RunnerState.CHECKPOINT_PAUSED, paused.state)
        assertEquals(listOf("bitti"), paused.lines)
        // High-stakes work pauses marked, and says it is not continued as the same independent attempt (`TRUX-v0` §7.2).
        val marked = TaskRunPresentation.paused(PauseDecision.Durable(CheckpointKind.HIGH_STAKES_PAUSE), emptyList())
        assertEquals(listOf(TaskRunCopy.HIGH_STAKES_PAUSED), marked.lines)
        clean(listOf(TaskRunCopy.PAUSE_NOT_SAFE, TaskRunCopy.NOTHING_TO_PAUSE, TaskRunCopy.HIGH_STAKES_PAUSED))
    }
}
