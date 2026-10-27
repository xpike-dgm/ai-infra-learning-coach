package coach.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import coach.model.DayRecordKind
import coach.presentation.DayChanges
import coach.presentation.DayCopy
import coach.presentation.DaySummaryState
import coach.presentation.DaySummaryView
import coach.presentation.DisplayCopy
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

// 17E: the day's changes can be unread too, and are named rather than reported as "nothing changed".
private val unreadLabelsTr: Map<String, String> =
    DayRecordKind.entries.associate { it.id to kindLabelsTr.getValue(it) } + (DayChanges.UNREAD_KIND to "durum değişiklikleri")

@Composable
fun EndOfDayView(view: DaySummaryView, modifier: Modifier = Modifier) {
    // 17A: the day's own context is one group, apart from the plan above it.
    SectionBlock(modifier = modifier) {
        StateChip(
            label = stateLabelsTr.getValue(view.state),
            tone = view.state.tone,
            modifier = Modifier.semantics {
                heading()
                liveRegion = LiveRegionMode.Polite
            },
        )
        Text(DisplayCopy.studyDay(view.studyDay), style = MaterialTheme.typography.bodySmall)

        when (view.state) {
            DaySummaryState.EMPTY_NO_EVIDENCE_YET ->
                Text(DayCopy.NOTHING_RECORDED, style = MaterialTheme.typography.bodyMedium)

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
                    if (DayChanges.UNREAD_KIND !in view.unreadKinds) Text(DayCopy.NOTHING_CHANGED, style = MaterialTheme.typography.bodyMedium)
                } else {
                    SectionHeader("Bugün değişenler")
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
