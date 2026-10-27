package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import coach.presentation.AnswerField
import coach.presentation.AnswerInput
import coach.presentation.RunPosition
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.ui.semantics.Role
import coach.presentation.Spacing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import android.content.ClipData
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import kotlinx.coroutines.launch
import coach.model.AssistanceLevel
import coach.model.ExplanationForm
import coach.model.ProvenanceOrigin
import coach.model.RunItemKind
import coach.model.TutorIntent
import coach.presentation.ComprehensionView
import coach.presentation.HelpPanel
import coach.presentation.HelpView
import coach.presentation.MeasurementCopy
import coach.presentation.MotionPurpose
import coach.presentation.OpenResponseCopy
import coach.presentation.RunHelpCopy
import coach.presentation.TaskOrientationView
import coach.presentation.TaskRunCopy
import coach.presentation.TaskRunStage
import coach.presentation.EntryCondition
import coach.presentation.ResumeCondition
import coach.presentation.Revalidation
import coach.presentation.RunnerCopy
import coach.presentation.RunnerState
import coach.presentation.tone

/**
 * The Task Runner's focused flow. It renders what `core-presentation` decided and nothing more.
 *
 * The exit comes first and is always there: a focused flow suspends the shell, so this screen is
 * the only way back, and `TRUX-v0` requires it to be one deliberate action from every state with
 * no guilt gate in front of it.
 *
 * Turkish text is working microcopy owned by 14.
 */

private val stateLabelsTr: Map<RunnerState, String> = mapOf(
    RunnerState.ENTERING_REVALIDATING to "Görev kontrol ediliyor",
    RunnerState.BLOCKED_NOT_STARTABLE to "Bu görev şu an başlatılamıyor",
    RunnerState.ORIENTATION to "Göreve hazırlan",
    RunnerState.ACTIVE_WORK to "Çalışıyorsun",
    RunnerState.ASSISTANCE_OPEN to "Yardım açık",
    RunnerState.SUBMITTING to "Gönderiliyor",
    RunnerState.FEEDBACK_RESOLVED to "Geri bildirim hazır",
    RunnerState.EVALUATION_PENDING to "Değerlendirme bekliyor",
    RunnerState.CHECKPOINT_PAUSED to "Kaydedilmiş noktada duraklatıldı",
    RunnerState.RESUME_REVALIDATING to "Devam etmeden önce kontrol ediliyor",
    RunnerState.RESUME_INVALIDATED to "Bu çalışma artık devam ettirilemiyor",
    RunnerState.REPLAN_INTERRUPTED to "Plan değişti",
    RunnerState.STOPPED_NO_PENALTY to "Durduruldu",
    RunnerState.OFFLINE_LOCAL_CAPABLE to "Çevrimdışı",
    RunnerState.AI_UNAVAILABLE_DETERMINISTIC_CORE to "Yapay zekâ kullanılamıyor",
    RunnerState.ERROR_RECOVERABLE to "Açılamadı",
    RunnerState.DATA_RECOVERY_REQUIRED to "Veri kurtarma gerekiyor",
)

private val conditionLabelsTr: Map<String, String> = mapOf(
    EntryCondition.PLANNED_TASK_STILL_SELECTED.id to "görev hâlâ planda seçili değil ya da doğrulanamadı",
    EntryCondition.HARD_PREREQUISITES_SATISFIED.id to "ön koşullar doğrulanamadı",
    EntryCondition.LEARNING_NEED_STILL_OPEN.id to "öğrenme ihtiyacının hâlâ açık olduğu doğrulanamadı",
    EntryCondition.CONTENT_VERSION_COMPATIBLE.id to "içerik sürümü doğrulanamadı",
    EntryCondition.REQUIRED_LOCAL_CAPABILITY_AVAILABLE.id to "gereken yerel yetenek doğrulanamadı",
    // Resume conditions (11C). Two ids are shared with entry and keep the entry label.
    ResumeCondition.PREREQUISITES_STILL_ELIGIBLE.id to "ön koşulların hâlâ uygun olduğu doğrulanamadı",
    ResumeCondition.RUNNER_AND_ARTIFACT_STATE_INTACT.id to "kaydedilen çalışma durumunun eksiksiz olduğu doğrulanamadı",
    ResumeCondition.HIGH_STAKES_GAP_INTEGRITY_ACCEPTABLE.id to
        "aradan geçen süreden sonra bu çalışmanın bağımsız sayılabileceği doğrulanamadı",
)

/**
 * What the learner can do inside a run (16F). Each is a request the composition root carries to core; the screen decides
 * nothing about what it means.
 */
class TaskRunActions(
    val onBegin: (ProvenanceOrigin) -> Unit = {},
    val onToQuestions: () -> Unit = {},
    val onShowLesson: () -> Unit = {},
    val onRequestSolution: () -> Unit = {},
    val onAcknowledgeSolution: () -> Unit = {},
    val onDeclineSolution: () -> Unit = {},
    val onSubmit: (String) -> Unit = {},
    val onReveal: () -> Unit = {},
    val onNext: () -> Unit = {},
    val onFinish: () -> Unit = {},
    // 16G: judged answers, help, the comprehension check and a durable pause.
    val onSubmitCode: (code: String, report: String) -> Unit = { _, _ -> },
    val onReevaluate: () -> Unit = {},
    val onSelfCheck: () -> Unit = {},
    val onOpenHelp: () -> Unit = {},
    val onCloseHelp: () -> Unit = {},
    val onHelpIntent: (TutorIntent) -> Unit = {},
    val onHelpLevel: (AssistanceLevel) -> Unit = {},
    val onHelpAcknowledge: () -> Unit = {},
    val onHelpQuestion: (String) -> Unit = {},
    val onExplanationForm: (ExplanationForm) -> Unit = {},
    val onCheckChoice: (String?) -> Unit = {},
    val onNextCheck: () -> Unit = {},
    val onPracticeAsk: () -> Unit = {},
    val onPracticeAnswer: (String) -> Unit = {},
    val onPause: () -> Unit = {},
    // 16H: in a measurement, leaving a question unanswered and reporting an answered one.
    val onSkip: () -> Unit = {},
    val onContest: () -> Unit = {},
)

@Composable
fun TaskRunnerScreen(
    state: RunnerState,
    entry: Revalidation?,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    // 16F: the run's interior, decided in core; without one the screen is 11B's frame alone.
    stage: TaskRunStage? = null,
    actions: TaskRunActions = TaskRunActions(),
) {
    Column(modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        // The safe exit: first, always present, one action, 48dp (TRUX-v0, WFPX-v0). 17C: it sits above the scrolling
        // run, so it stays in view however long the lesson is.
        Box(Modifier.fillMaxWidth().padding(horizontal = Spacing.M.dp, vertical = Spacing.XS.dp)) {
            OutlinedButton(onClick = onExit, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
                Text("Bugün'e dön")
            }
        }
        ScreenColumn(modifier = Modifier.weight(1f), insets = false) {

        StateChip(
            label = stateLabelsTr.getValue(state),
            tone = state.tone,
            modifier = Modifier.semantics {
                heading()
                liveRegion = LiveRegionMode.Polite
            },
        )

        when (state) {
            RunnerState.BLOCKED_NOT_STARTABLE -> {
                // Not the learner's error and not a verdict: the plan moved, or nothing can yet
                // confirm what the task needs. The unmet conditions are named, not blamed.
                Text(
                    "Başlamadan önce doğrulanması gerekenler doğrulanamadı. Bu bir hata ya da başarısızlık değil.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                (entry as? Revalidation.NotStartable)?.unmet?.sorted()?.forEach { id ->
                    Text("• " + conditionLabelsTr.getValue(id), style = MaterialTheme.typography.bodyMedium)
                }
            }
            RunnerState.CHECKPOINT_PAUSED -> Text(RunnerCopy.CHECKPOINT_SAVED, style = MaterialTheme.typography.bodyLarge)
            RunnerState.RESUME_INVALIDATED -> {
                Text(RunnerCopy.RESUME_INVALIDATED, style = MaterialTheme.typography.bodyLarge)
                (entry as? Revalidation.NotStartable)?.unmet?.sorted()?.forEach { id ->
                    Text("• " + conditionLabelsTr.getValue(id), style = MaterialTheme.typography.bodyMedium)
                }
            }
            RunnerState.ORIENTATION -> Text(RunnerCopy.ASSISTANCE_POLICY, style = MaterialTheme.typography.bodyLarge)
            RunnerState.STOPPED_NO_PENALTY -> Text(RunnerCopy.STOPPED, style = MaterialTheme.typography.bodyLarge)
            RunnerState.EVALUATION_PENDING -> Text(
                "Deneme kaydedildi. Değerlendirme şu an yapılamıyor; bu ne başarı ne başarısızlık sayılır.",
                style = MaterialTheme.typography.bodyLarge,
            )
            else -> Unit
        }

        stage?.let { TaskRunInterior(it, actions) }
        }
    }
}

@Composable
private fun TaskRunInterior(stage: TaskRunStage, actions: TaskRunActions) {
    when (stage) {
        is TaskRunStage.Blocked -> stage.capabilityLine?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        is TaskRunStage.Orient -> OrientationInterior(stage.view, actions.onBegin)
        is TaskRunStage.Lesson -> {
            SectionHeader(TaskRunCopy.LESSON_TITLE)
            stage.texts.forEach { AuthoredText(it) }
            Button(onClick = actions.onToQuestions, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.TO_QUESTIONS) }
        }
        is TaskRunStage.Question -> QuestionInterior(stage, actions)
        is TaskRunStage.ShownBeforeAnswer -> {
            AuthoredText(stage.text)
            NextOrFinish(actions)
        }
        TaskRunStage.Working -> Unit
        is TaskRunStage.Answered -> AnsweredInterior(stage, actions)
        is TaskRunStage.Finished -> stage.lines.forEach { Text(it, style = MaterialTheme.typography.bodyLarge) }
        is TaskRunStage.Paused -> stage.lines.forEach { Text(it, style = MaterialTheme.typography.bodyLarge) }
    }
}

@Composable
private fun OrientationInterior(view: TaskOrientationView, onStart: (ProvenanceOrigin) -> Unit) {
    ScreenTitle(view.title)
    Text(view.purpose + (view.minutes?.let { " · $it" } ?: ""), style = MaterialTheme.typography.bodyMedium)
    // 16H: which session this is and what independence means here, before any work (`ASUX-v0` §6).
    view.measurement.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
    view.teachNote?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
    Text(view.provenanceQuestion, style = MaterialTheme.typography.bodyLarge)
    var chosen by remember { mutableStateOf<ProvenanceOrigin?>(null) }
    // 17A: the whole row selects, not only the radio, and the group is announced as one choice.
    Column(modifier = Modifier.selectableGroup()) {
        view.provenanceOptions.forEach { (origin, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().minimumTouchTarget()
                    .selectable(selected = chosen == origin, onClick = { chosen = origin }, role = Role.RadioButton),
            ) {
                RadioButton(selected = chosen == origin, onClick = null)
                Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = Spacing.S.dp))
            }
        }
    }
    // Nothing is preselected: the learner's own answer is recorded, never a default (`TRUX-v0` §9).
    Button(onClick = { chosen?.let(onStart) }, enabled = chosen != null, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
        Text(TaskRunCopy.START)
    }
}

@Composable
private fun QuestionInterior(stage: TaskRunStage.Question, actions: TaskRunActions) {
    // 17C: where the learner is among this run's questions — a position, never a score.
    SectionHeader(RunPosition.label(stage.number, stage.total),
        modifier = Modifier.semantics { contentDescription = RunPosition.spoken(stage.number, stage.total) })
    AuthoredText(stage.prompt)
    stage.notice?.let { Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) }
    if (stage.disclosure) {
        ConsequenceDisclosure(onAcknowledge = actions.onAcknowledgeSolution, onDecline = actions.onDeclineSolution)
        return
    }
    when (stage.kind) {
        RunItemKind.CODE_TESTS -> CodeAnswer(stage, actions)
        RunItemKind.RUBRIC, RunItemKind.ANSWER_KEY -> {
            var typed by remember(stage.number) { mutableStateOf("") }
            val field = if (stage.kind == RunItemKind.RUBRIC) AnswerField.OPEN else AnswerField.ANSWER
            ExactTextInput(field) { OutlinedTextField(
                value = typed,
                onValueChange = { typed = it },
                label = { Text(if (stage.kind == RunItemKind.RUBRIC) TaskRunCopy.OPEN_LABEL else TaskRunCopy.ANSWER_LABEL) },
                minLines = if (stage.kind == RunItemKind.RUBRIC) 4 else 1,
                keyboardOptions = answerKeyboard(field),
                modifier = Modifier.fillMaxWidth(),
            ) }
            Button(onClick = { actions.onSubmit(typed) }, enabled = typed.isNotBlank(), modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
                Text(TaskRunCopy.SUBMIT)
            }
        }
    }
    // Help is always requestable (`TRUX-v0` §8.1): the lesson again, the tutor, or — after the consequence is said — the answer.
    val lesson = stage.lessonShown
    if (lesson == null) {
        TextButton(onClick = actions.onShowLesson, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.SHOW_LESSON) }
    } else {
        lesson.forEach { AuthoredText(it, style = MaterialTheme.typography.bodyMedium) }
    }
    HelpSection(stage.help, actions)
    // 16H: in a measurement a question may be left unanswered; that is not incorrect.
    if (stage.canSkip) TextButton(onClick = actions.onSkip, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(MeasurementCopy.SKIP) }
    // Only an answer key has an accepted answer to show (14F); a code item and an open answer do not.
    if (stage.kind == RunItemKind.ANSWER_KEY) {
        TextButton(onClick = actions.onRequestSolution, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.SHOW_SOLUTION_BEFORE) }
    }
}

/** A code item (16G): the learner copies the runner and the suite, runs them on their computer and pastes the report. */
@Composable
private fun CodeAnswer(stage: TaskRunStage.Question, actions: TaskRunActions) {
    // The system clipboard only: no file or network permission (user decision, 2026-10-26).
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    fun copy(text: String) = scope.launch { clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(TaskRunCopy.CODE_LABEL, text))) }
    stage.code?.let { transfer ->
        transfer.steps.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
        OutlinedButton(onClick = { copy(transfer.runner) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text(TaskRunCopy.COPY_RUNNER)
        }
        OutlinedButton(onClick = { copy(transfer.suite) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text(TaskRunCopy.COPY_SUITE)
        }
    }
    var code by remember(stage.number) { mutableStateOf("") }
    var report by remember(stage.number) { mutableStateOf("") }
    ExactTextInput(AnswerField.CODE) { OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text(TaskRunCopy.CODE_LABEL) }, minLines = 4,
        textStyle = codeFieldStyle, keyboardOptions = answerKeyboard(AnswerField.CODE), modifier = Modifier.fillMaxWidth()) }
    ExactTextInput(AnswerField.REPORT) { OutlinedTextField(value = report, onValueChange = { report = it }, label = { Text(TaskRunCopy.REPORT_LABEL) }, minLines = 3,
        textStyle = codeFieldStyle, keyboardOptions = answerKeyboard(AnswerField.REPORT), modifier = Modifier.fillMaxWidth()) }
    Button(
        onClick = { actions.onSubmitCode(code, report) },
        enabled = code.isNotBlank() && report.isNotBlank(),
        modifier = Modifier.fillMaxWidth().minimumTouchTarget(),
    ) { Text(TaskRunCopy.SUBMIT) }
}

@Composable
private fun AnsweredInterior(stage: TaskRunStage.Answered, actions: TaskRunActions) {
    // 17B: the result arriving is feedback; it is announced and said in text, never shown by motion alone.
    Box(Modifier.semantics { liveRegion = LiveRegionMode.Polite; contentDescription = stage.result }) {
        ArriveOnChange(stage.result, MotionPurpose.FEEDBACK) {
            Text(stage.result, style = MaterialTheme.typography.titleMedium, modifier = Modifier.clearAndSetSemantics {})
        }
    }
    stage.details.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
    stage.notice?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
    if (stage.canReveal) {
        val revealed = stage.revealed
        if (revealed != null) {
            AuthoredText(revealed, style = MaterialTheme.typography.bodyMedium)
        } else {
            // Seeing the answer is a separate step the learner chooses (user decision, 2026-10-26).
            TextButton(onClick = actions.onReveal, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.SHOW_ANSWER) }
        }
    }
    if (stage.pending) {
        // 14F: nothing retries on its own; the learner may ask again.
        Text(OpenResponseCopy.ASK_AGAIN, style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(onClick = actions.onReevaluate, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.REEVALUATE) }
    }
    val selfCheck = stage.selfCheck
    if (selfCheck != null) {
        selfCheck.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
    } else if (stage.canSelfCheck) {
        TextButton(onClick = actions.onSelfCheck, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.SELF_CHECK) }
    }
    stage.wrongAnswer?.let { summary ->
        Text(summary.headline, style = MaterialTheme.typography.bodyMedium)
        (summary.lines + summary.named + summary.openQuestions).forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
    }
    // 16H: an answered measurement question may be reported; its evidence is then held, never counted against.
    if (stage.canContest) TextButton(onClick = actions.onContest, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(MeasurementCopy.CONTEST) }
    stage.comprehension?.let { ComprehensionInterior(it, actions) }
    HelpSection(stage.help, actions)
    TextButton(onClick = actions.onPause, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.PAUSE) }
    NextOrFinish(actions)
}

/** The comprehension check after code the learner did not write alone (14E): optional, and skipping is not wrong. */
@Composable
private fun ComprehensionInterior(view: ComprehensionView, actions: TaskRunActions) {
    when (view) {
        is ComprehensionView.Check -> {
            Text(view.offer, style = MaterialTheme.typography.bodyMedium)
            AuthoredText(view.prompt)
            view.choices.forEach { (key, text) ->
                OutlinedButton(onClick = { actions.onCheckChoice(key) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text("$key) $text") }
            }
            TextButton(onClick = { actions.onCheckChoice(null) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(RunHelpCopy.SKIP_CHECK) }
        }
        is ComprehensionView.Result -> {
            view.lines.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
            if (view.more) TextButton(onClick = actions.onNextCheck, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(RunHelpCopy.NEXT_CHECK) }
        }
        is ComprehensionView.TutorPractice -> {
            Text(view.offer, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = actions.onPracticeAsk, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(RunHelpCopy.PRACTICE_ASK) }
        }
    }
}

/** Help inside the run (16G): always one action away, and every panel only says what core decided. */
@Composable
private fun HelpSection(help: HelpView?, actions: TaskRunActions) {
    // 17B: the help panel opening and closing is one object growing (continuity); nothing else in the run is animated.
    Column(Modifier.continuousSize(), verticalArrangement = Arrangement.spacedBy(Spacing.M.dp)) { HelpPanelContent(help, actions) }
}

@Composable
private fun HelpPanelContent(help: HelpView?, actions: TaskRunActions) {
    if (help == null) {
        TextButton(onClick = actions.onOpenHelp, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(RunHelpCopy.HELP) }
        return
    }
    help.intents.forEach { (intent, label) ->
        OutlinedButton(onClick = { actions.onHelpIntent(intent) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(label) }
    }
    when (val panel = help.panel) {
        null -> Unit
        is HelpPanel.ChooseLevel -> {
            Text(panel.prompt, style = MaterialTheme.typography.bodyMedium)
            panel.levels.forEach { (level, label) ->
                OutlinedButton(onClick = { actions.onHelpLevel(level) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(label) }
            }
        }
        is HelpPanel.Disclose -> {
            Text(panel.text, style = MaterialTheme.typography.bodyLarge)
            OutlinedButton(onClick = actions.onHelpAcknowledge, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(RunHelpCopy.ACKNOWLEDGE) }
            OutlinedButton(onClick = actions.onCloseHelp, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(RunHelpCopy.DECLINE) }
        }
        is HelpPanel.AskQuestion -> {
            var question by remember { mutableStateOf("") }
            OutlinedTextField(value = question, onValueChange = { question = it }, label = { Text(RunHelpCopy.QUESTION_LABEL) }, modifier = Modifier.fillMaxWidth())
            OutlinedButton(onClick = { actions.onHelpQuestion(question) }, enabled = question.isNotBlank(), modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
                Text(RunHelpCopy.ASK)
            }
        }
        is HelpPanel.Menu -> {
            Text(panel.prompt, style = MaterialTheme.typography.bodyMedium)
            panel.options.forEach { (form, label) ->
                OutlinedButton(onClick = { actions.onExplanationForm(form) }, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(label) }
            }
        }
        is HelpPanel.Message -> Text(panel.text, style = MaterialTheme.typography.bodyMedium)
        HelpPanel.Waiting -> Text(RunHelpCopy.WAITING, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        is HelpPanel.Shown -> {
            AuthoredText(panel.text)
            panel.notes.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
        is HelpPanel.Practice -> {
            Text(panel.question, style = MaterialTheme.typography.bodyLarge)
            panel.notes.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
            var answer by remember(panel.question) { mutableStateOf("") }
            OutlinedTextField(value = answer, onValueChange = { answer = it }, label = { Text(RunHelpCopy.PRACTICE_ANSWER_LABEL) }, modifier = Modifier.fillMaxWidth())
            OutlinedButton(onClick = { actions.onPracticeAnswer(answer) }, enabled = answer.isNotBlank(), modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
                Text(RunHelpCopy.PRACTICE_SEND)
            }
        }
    }
    TextButton(onClick = actions.onCloseHelp, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(RunHelpCopy.CLOSE) }
}

@Composable
private fun NextOrFinish(actions: TaskRunActions) {
    Button(onClick = actions.onNext, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.NEXT) }
    TextButton(onClick = actions.onFinish, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.FINISH) }
}

/** Shown before H3 or H4 is granted, in measurement terms (`TRUX-v0` §8.3). */
@Composable
fun ConsequenceDisclosure(onAcknowledge: () -> Unit, onDecline: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS.dp)) {
        Text(RunnerCopy.CONSEQUENCE_DISCLOSURE, style = MaterialTheme.typography.bodyLarge)
        OutlinedButton(onClick = onAcknowledge, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text("Anladım, yardımı göster")
        }
        OutlinedButton(onClick = onDecline, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text("Şimdilik istemiyorum")
        }
    }
}

/**
 * 17C: the keyboard options for a run's field. An exact field ([AnswerInput.exact]) keeps capitalisation off (Compose's
 * default already) and asks for no autocorrect — which most keyboards ignore on a multi-line field, so [ExactTextInput]
 * also asks for no suggestions at all. An open answer gets sentence capitals and the keyboard's help.
 */
private fun answerKeyboard(field: AnswerField): KeyboardOptions =
    if (AnswerInput.exact(field)) KeyboardOptions(capitalization = KeyboardCapitalization.None, autoCorrectEnabled = false)
    else KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
