package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coach.model.DayRecordKind
import coach.presentation.DayCopy
import coach.presentation.DaySummaryState
import coach.presentation.DaySummaryView
import coach.presentation.tone

/**
 * The end of the day, rendered inside Today's `day_plan_context` region (11E).
 *
 * It is not a new surface: `NSHX-v0`'s surface set is closed, and a day summary is Today's own
 * context at the end of a day. Longitudinal history across days belongs to Progress (16B).
 *
 * Everything here is text. There is no chart, no ring, no bar and no calendar grid, because a
 * filled square is exactly how attendance turns into a score (`SPWX-v0` §learning_history).
 *
 * Turkish text is working microcopy owned by 14.
 */

private val stateLabelsTr: Map<DaySummaryState, String> = mapOf(
    DaySummaryState.LOADING_PROJECTION to "Gün dökümü okunuyor",
    DaySummaryState.RECOMPUTING_PROJECTION to "Gün dökümü yeniden hesaplanıyor",
    DaySummaryState.READY_WITH_EVIDENCE to "Bugün kaydedilenler",
    DaySummaryState.EMPTY_NO_EVIDENCE_YET to "Bugün kayıt yok",
    DaySummaryState.EMPTY_NO_ATTENTION_NEEDED to "Bekleyen bir şey yok",
    DaySummaryState.PARTIAL_PROJECTION_AVAILABLE to "Gün dökümü eksik okundu",
    DaySummaryState.OFFLINE_LOCAL_CAPABLE to "Çevrimdışı",
    DaySummaryState.AI_UNAVAILABLE_FULL_STATE_AVAILABLE to "Yapay zekâ kullanılamıyor",
    DaySummaryState.ERROR_RECOVERABLE to "Açılamadı",
    DaySummaryState.DATA_RECOVERY_REQUIRED to "Veri kurtarma gerekiyor",
)

private val kindLabelsTr: Map<DayRecordKind, String> = mapOf(
    DayRecordKind.ATTEMPTS_RECORDED to "kaydedilen deneme",
    DayRecordKind.ITEMS_SEEN to "görülen soru",
    DayRecordKind.CHECKPOINTS_SAVED to "kaydedilen durak",
    DayRecordKind.EVIDENCE_INTERPRETED to "değerlendirilen kanıt",
)

private val unreadLabelsTr: Map<String, String> = DayRecordKind.entries.associate { it.id to kindLabelsTr.getValue(it) }

@Composable
fun EndOfDayView(view: DaySummaryView, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StateChip(
            label = stateLabelsTr.getValue(view.state),
            tone = view.state.tone,
            modifier = Modifier.semantics {
                heading()
                liveRegion = LiveRegionMode.Polite
            },
        )
        Text(view.studyDay, style = MaterialTheme.typography.bodySmall)

        when (view.state) {
            DaySummaryState.EMPTY_NO_EVIDENCE_YET ->
                Text(DayCopy.NOTHING_RECORDED, style = MaterialTheme.typography.bodyLarge)

            DaySummaryState.READY_WITH_EVIDENCE, DaySummaryState.PARTIAL_PROJECTION_AVAILABLE -> {
                // Labelled as inventory, in text, and never divided into a ratio (SPWX-v0).
                Text(DayCopy.RECORDED_IS_NOT_PROGRESS, style = MaterialTheme.typography.bodyMedium)
                DayRecordKind.entries.forEach { kind ->
                    val count = view.inventory.countOf(kind)
                    if (count > 0) Text("• $count ${kindLabelsTr.getValue(kind)}", style = MaterialTheme.typography.bodyMedium)
                }
                if (view.unreadKinds.isNotEmpty()) {
                    Text(DayCopy.PARTIAL, style = MaterialTheme.typography.bodyMedium)
                    view.unreadKinds.sorted().forEach {
                        Text("• okunamadı: " + unreadLabelsTr.getValue(it), style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (view.changes.isEmpty()) {
                    Text(DayCopy.NOTHING_CHANGED, style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("Bugün değişenler", style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.semantics { heading() })
                    view.changes.forEach { change ->
                        Text("• ${change.statement}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            else -> Unit
        }

        Text(DayCopy.TOMORROW, style = MaterialTheme.typography.bodySmall)
    }
}
