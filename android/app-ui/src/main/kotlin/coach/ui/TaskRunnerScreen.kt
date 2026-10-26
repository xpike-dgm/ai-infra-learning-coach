package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
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
import coach.model.ProvenanceOrigin
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // The safe exit: first, always present, one action, 48dp (TRUX-v0, WFPX-v0).
        OutlinedButton(onClick = onExit, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text("Bugün'e dön")
        }

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

@Composable
private fun TaskRunInterior(stage: TaskRunStage, actions: TaskRunActions) {
    when (stage) {
        is TaskRunStage.Blocked -> stage.capabilityLine?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        is TaskRunStage.Orient -> OrientationInterior(stage.view, actions.onBegin)
        is TaskRunStage.Lesson -> {
            Text(TaskRunCopy.LESSON_TITLE, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
            stage.texts.forEach { Text(it, style = MaterialTheme.typography.bodyLarge) }
            OutlinedButton(onClick = actions.onToQuestions, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.TO_QUESTIONS) }
        }
        is TaskRunStage.Question -> QuestionInterior(stage, actions)
        is TaskRunStage.ShownBeforeAnswer -> {
            Text(stage.text, style = MaterialTheme.typography.bodyLarge)
            NextOrFinish(actions)
        }
        TaskRunStage.Working -> Unit
        is TaskRunStage.Answered -> {
            Text(stage.result, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            val revealed = stage.revealed
            if (revealed != null) {
                Text(revealed, style = MaterialTheme.typography.bodyMedium)
            } else {
                // Seeing the answer is a separate step the learner chooses (user decision, 2026-10-26).
                TextButton(onClick = actions.onReveal, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.SHOW_ANSWER) }
            }
            NextOrFinish(actions)
        }
        is TaskRunStage.Finished -> stage.lines.forEach { Text(it, style = MaterialTheme.typography.bodyLarge) }
    }
}

@Composable
private fun OrientationInterior(view: TaskOrientationView, onStart: (ProvenanceOrigin) -> Unit) {
    Text(view.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
    Text(view.purpose + (view.minutes?.let { " · $it" } ?: ""), style = MaterialTheme.typography.bodyMedium)
    view.teachNote?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
    Text(view.provenanceQuestion, style = MaterialTheme.typography.bodyLarge)
    var chosen by remember { mutableStateOf<ProvenanceOrigin?>(null) }
    view.provenanceOptions.forEach { (origin, label) ->
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            RadioButton(selected = chosen == origin, onClick = { chosen = origin })
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
    // Nothing is preselected: the learner's own answer is recorded, never a default (`TRUX-v0` §9).
    OutlinedButton(onClick = { chosen?.let(onStart) }, enabled = chosen != null, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
        Text(TaskRunCopy.START)
    }
}

@Composable
private fun QuestionInterior(stage: TaskRunStage.Question, actions: TaskRunActions) {
    Text("Soru ${stage.number}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
    Text(stage.prompt, style = MaterialTheme.typography.bodyLarge)
    stage.notice?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
    if (stage.disclosure) {
        ConsequenceDisclosure(onAcknowledge = actions.onAcknowledgeSolution, onDecline = actions.onDeclineSolution)
        return
    }
    var typed by remember(stage.number) { mutableStateOf("") }
    OutlinedTextField(
        value = typed,
        onValueChange = { typed = it },
        label = { Text(TaskRunCopy.ANSWER_LABEL) },
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedButton(onClick = { actions.onSubmit(typed) }, enabled = typed.isNotBlank(), modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
        Text(TaskRunCopy.SUBMIT)
    }
    // Help is always requestable (`TRUX-v0` §8.1): the lesson again, or — after the consequence is said — the answer.
    val lesson = stage.lessonShown
    if (lesson == null) {
        TextButton(onClick = actions.onShowLesson, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.SHOW_LESSON) }
    } else {
        lesson.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
    }
    TextButton(onClick = actions.onRequestSolution, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.SHOW_SOLUTION_BEFORE) }
}

@Composable
private fun NextOrFinish(actions: TaskRunActions) {
    OutlinedButton(onClick = actions.onNext, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.NEXT) }
    TextButton(onClick = actions.onFinish, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) { Text(TaskRunCopy.FINISH) }
}

/** Shown before H3 or H4 is granted, in measurement terms (`TRUX-v0` §8.3). */
@Composable
fun ConsequenceDisclosure(onAcknowledge: () -> Unit, onDecline: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(RunnerCopy.CONSEQUENCE_DISCLOSURE, style = MaterialTheme.typography.bodyLarge)
        OutlinedButton(onClick = onAcknowledge, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text("Anladım, yardımı göster")
        }
        OutlinedButton(onClick = onDecline, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text("Şimdilik istemiyorum")
        }
    }
}
