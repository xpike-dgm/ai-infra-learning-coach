package coach.wiring

import coach.model.AnsweredItem
import coach.model.AssessmentItem
import coach.model.AssistanceEvent
import coach.model.AssistanceLevel
import coach.presentation.AssistanceOutcome
import coach.presentation.AssistancePolicy
import coach.model.AssistanceScope
import coach.model.AssistanceSource
import coach.model.AssistanceTiming
import coach.model.ComprehensionCheck
import coach.model.ExplanationForm
import coach.model.ProgramSnapshot
import coach.model.ProvenanceOrigin
import coach.model.RunAnswer
import coach.model.RunItemKind
import coach.model.RunnableTask
import coach.model.TutorIntent
import coach.model.VersionedRef
import coach.presentation.CodeTransferView
import coach.presentation.HelpPanel
import coach.presentation.HelpView
import coach.presentation.RunHelpPresentation
import coach.presentation.TaskRunStage

/**
 * One task run's working memory on the main thread (16F). It holds what the run showed and what the learner answered
 * — nothing it decides: every judgement is core's, and everything that counts is already in the store the moment it is
 * recorded. Like 11C's working session it is not stored, so losing it loses nothing the learner did.
 */
internal class TaskRunSession(run: RunnableTask, val before: ProgramSnapshot) {
    /** The run; a daily assessment's gains its session id when the learner begins (16H). */
    var run: RunnableTask = run

    /** 16H: questions left unanswered, and answered ones the learner reported — for the session's result. */
    val skipped = mutableSetOf<VersionedRef>()
    val contested = mutableSetOf<VersionedRef>()

    /** The learner's own answer to the provenance question, asked once at orientation (`TRUX-v0` §9). */
    var provenance: ProvenanceOrigin? = null

    /** Every question shown in this run, in order; none is shown twice. */
    val shown = mutableListOf<AssessmentItem>()
    var current: AssessmentItem? = null
    var currentPrompt: String = ""

    /** Whether the learner asked to see the lesson again during the current question: help recorded with the answer. */
    var lessonShown = false

    val answered = mutableListOf<AnsweredItem>()
    var lastAnswer: AnsweredItem? = null

    /** Whether the end-of-run processing already ran, so a stop after finishing does not run it twice. */
    var finished = false

    // 16G — how the current question is answered, and help inside it.
    var kind: RunItemKind = RunItemKind.ANSWER_KEY
    var code: CodeTransferView? = null

    /** The learner's frozen answer text, for "explain my answer" and a re-evaluation; held for the run only. */
    var lastResponse: String = ""

    /** Help shown while the answer is open: it travels with the attempt when it is submitted (`TRUX-v0` §8.5). */
    val pendingHelp = mutableListOf<AssistanceEvent>()

    /** Whether the help section is open, the open panel, and what the ask has gathered so far. */
    var helpOpen = false
    var helpPanel: HelpPanel? = null
    var helpQuestion: String? = null
    var helpIntent: TutorIntent? = null
    var helpLevel: AssistanceLevel? = null
    var helpAcknowledged = false
    var helpForm: ExplanationForm? = null

    /** Forms of another explanation seen in this run (14C's session memory, never stored). */
    val seenForms = mutableSetOf<ExplanationForm>()

    /** The answered stage as last drawn, so help, the rubric or a check can be added to it. */
    var answeredStage: TaskRunStage.Answered? = null

    /** The written comprehension checks for the current code answer, and which one is next (14E). */
    var checks: List<ComprehensionCheck> = emptyList()
    var checkIndex = 0

    /** The tutor's practice question about the code, while its answer is awaited (14E). */
    var practiceQuestion: String? = null

    /** Starts one question: everything that belonged to the previous one is let go. */
    fun newQuestion(item: AssessmentItem, prompt: String, served: ServedItem?) {
        shown += item
        current = item
        currentPrompt = prompt
        lessonShown = false
        kind = served?.kind ?: RunItemKind.ANSWER_KEY
        code = served?.code
        lastResponse = ""
        lastAnswer = null
        pendingHelp.clear()
        closeHelp()
        answeredStage = null
        checks = emptyList()
        checkIndex = 0
        practiceQuestion = null
    }

    fun closeHelp() {
        helpOpen = false
        helpQuestion = null
        helpPanel = null
        helpIntent = null
        helpLevel = null
        helpAcknowledged = false
        helpForm = null
    }
}

/**
 * Help that goes with the answer when it is submitted (`TRUX-v0` §8.5): the lesson seen again as the concept help it
 * was, and whatever the tutor or another explanation showed while the answer was open.
 */
internal fun TaskRunSession.helpWithAnswer(): List<AssistanceEvent> {
    val lesson = if (!lessonShown) emptyList() else listOfNotNull(
        (AssistancePolicy.request(AssistanceLevel.H2, AssistanceTiming.DURING_ATTEMPT, AssistanceScope.TARGET_OBJECTIVE,
            AssistanceSource.DETERMINISTIC_CONTENT, consequenceAcknowledged = false) as? AssistanceOutcome.Granted)?.event
    )
    return lesson + pendingHelp
}

/** The help section as it now is; `null` when it is closed (then only "ask for help" is offered). */
internal fun TaskRunSession.helpView(frozen: Boolean): HelpView? = if (!helpOpen) null else RunHelpPresentation.view(frozen, helpPanel)

/** The current question as the runner draws it, with how it is answered and the help section. */
internal fun TaskRunSession.questionStage(disclosure: Boolean = false, notice: String? = null) = TaskRunStage.Question(
    shown.size, currentPrompt, if (lessonShown) run.lesson.map { it.text } else null, disclosure, notice,
    kind = kind, code = code, help = helpView(frozen = false),
    canSkip = run.measurement != null,
    total = run.items.size,
)

/** How a served question is answered, and what a code item's learner copies (16G). */
internal data class ServedItem(val kind: RunItemKind, val code: CodeTransferView?)

/** One judged answer as core worded it (16G): the answered stage when it was recorded, else why nothing was. */
internal class Judged(val answer: RunAnswer, val stage: TaskRunStage.Answered?, val notice: String?)
