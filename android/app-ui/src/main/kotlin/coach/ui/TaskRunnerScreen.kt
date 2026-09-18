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
import coach.presentation.EntryCondition
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
)

@Composable
fun TaskRunnerScreen(
    state: RunnerState,
    entry: Revalidation?,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
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
            RunnerState.ORIENTATION -> Text(RunnerCopy.ASSISTANCE_POLICY, style = MaterialTheme.typography.bodyLarge)
            RunnerState.STOPPED_NO_PENALTY -> Text(RunnerCopy.STOPPED, style = MaterialTheme.typography.bodyLarge)
            RunnerState.EVALUATION_PENDING -> Text(
                "Deneme kaydedildi. Değerlendirme şu an yapılamıyor; bu ne başarı ne başarısızlık sayılır.",
                style = MaterialTheme.typography.bodyLarge,
            )
            else -> Unit
        }
    }
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
