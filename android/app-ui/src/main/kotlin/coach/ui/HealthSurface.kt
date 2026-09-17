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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coach.model.RecoveryReason
import coach.presentation.AppHealth
import coach.presentation.CrossCuttingState
import coach.presentation.HealthAction

/**
 * Renders the app's health. It decides nothing: which state applies, whether the shell is shown
 * and which actions exist all come from `AppHealth.of` in core-presentation (`MSBX-v0`).
 *
 * Turkish text is working microcopy owned by 14. What is canonical is that every state is said in
 * words — never by colour or motion alone — and that a change of state is announced (`THUX-v0`).
 */

private val stateLabelsTr: Map<CrossCuttingState, String> = mapOf(
    CrossCuttingState.LOADING to "Yükleniyor",
    CrossCuttingState.EMPTY_VALID to "Henüz içerik yok",
    CrossCuttingState.ERROR_RECOVERABLE to "Açılamadı",
    CrossCuttingState.OFFLINE_LOCAL_AVAILABLE to "Çevrimdışı",
    CrossCuttingState.AI_UNAVAILABLE_CORE_AVAILABLE to "Yapay zekâ kullanılamıyor",
    CrossCuttingState.DATA_RECOVERY_REQUIRED to "Veri kurtarma gerekiyor",
)

private val recoveryReasonsTr: Map<RecoveryReason, String> = mapOf(
    RecoveryReason.INTEGRITY_CHECK_FAILED to
        "Yerel veritabanının bütünlük kontrolü başarısız oldu.",
    RecoveryReason.NEWER_SCHEMA to
        "Veritabanı bu uygulamadan daha yeni bir sürümle yazılmış. Eski sürüm onu tahminle açmaz.",
    RecoveryReason.MIGRATION_INCOMPLETE to
        "Veritabanı güncellemesi tamamlanamadı; önceki hâli olduğu gibi korundu.",
)

private const val NOTHING_RESET_TR =
    "Hiçbir veri silinmedi, sıfırlanmadı ya da yeniden oluşturulmadı."

private const val RESTORE_NOT_YET_TR =
    "Yedekten geri yükleme kontrolleri bu sürümde henüz yok."

private val actionLabelsTr: Map<HealthAction, String> = mapOf(
    HealthAction.RECHECK to "Yeniden kontrol et",
)

/** Shown instead of the shell whenever [AppHealth.blocking] is set. */
@Composable
fun HealthBlockingSurface(
    health: AppHealth,
    onAction: (HealthAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = requireNotNull(health.blocking) { "nothing is blocking; render the shell instead" }
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StateChip(
                label = stateLabelsTr.getValue(state),
                tone = state.tone,
                modifier = Modifier.semantics {
                    heading()
                    liveRegion = LiveRegionMode.Polite
                },
            )
            bodyLines(health).forEach { line ->
                Text(text = line, style = MaterialTheme.typography.bodyLarge)
            }
            health.actions.forEach { action ->
                OutlinedButton(
                    onClick = { onAction(action) },
                    modifier = Modifier.fillMaxWidth().minimumTouchTarget(),
                ) {
                    Text(actionLabelsTr.getValue(action))
                }
            }
        }
    }
}

private fun bodyLines(health: AppHealth): List<String> = when (health.blocking) {
    CrossCuttingState.DATA_RECOVERY_REQUIRED -> listOf(
        recoveryReasonsTr.getValue(requireNotNull(health.recoveryReason)),
        NOTHING_RESET_TR,
        RESTORE_NOT_YET_TR,
    )
    CrossCuttingState.ERROR_RECOVERABLE -> listOf(
        "Yerel veritabanı açılamadı. Verilerin okunmadan önce durduk, bu yüzden hiçbir şeye dokunulmadı.",
    )
    CrossCuttingState.LOADING -> listOf("Yerel veriler açılıyor ve kontrol ediliyor.")
    else -> emptyList()
}

/**
 * A non-blocking context line shown with the shell. It is neutral: being without AI is a
 * capability fact while the local core works, not a fault (`VDSX-v0`).
 */
@Composable
fun HealthContextLine(state: CrossCuttingState, modifier: Modifier = Modifier) {
    StateChip(label = stateLabelsTr.getValue(state), tone = state.tone, modifier = modifier)
}
