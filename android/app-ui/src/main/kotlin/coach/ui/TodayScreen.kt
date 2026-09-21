package coach.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coach.model.CapacityContext
import coach.model.ReasonFamily
import coach.model.TaskPurpose
import coach.presentation.AttentionFamily
import coach.presentation.AttentionItem
import coach.presentation.PrimaryActionKind
import coach.presentation.DaySummaryView
import coach.presentation.Surface
import coach.presentation.TodayState
import coach.presentation.TodayTaskRow
import coach.presentation.TodayView
import coach.presentation.tone

/**
 * Today, as `THUX-v0` ordered it and `WFPX-v0` placed it. This file renders and decides nothing:
 * which state applies, which task is dominant, what the queue contains and which reason may be
 * shown were all settled by `TodayPresentation` in core (`MSBX-v0` §presentation).
 *
 * Turkish text is working microcopy owned by 14. What is canonical here is that the action comes
 * first, every state is said in words, and no number on this screen is a progress claim.
 */

private val stateLabelsTr: Map<TodayState, String> = mapOf(
    TodayState.LOADING_INITIAL_PLAN to "Plan hazırlanıyor",
    TodayState.REPLANNING to "Plan yeniden hesaplanıyor",
    TodayState.READY_PLAN to "Bugünün planı hazır",
    TodayState.RESUMABLE_SESSION to "Yarım kalan çalışma var",
    TodayState.EMPTY_NO_OPEN_NEED to "Bugün için açık bir ihtiyaç yok",
    TodayState.EMPTY_NO_ELIGIBLE_TASK to "Şu an uygun görev yok",
    TodayState.CAPACITY_ZERO to "Bugün için süre ayrılmamış",
    TodayState.CAPACITY_TOO_SMALL_NO_CANDIDATE to "Bugünkü süreye sığan görev yok",
    TodayState.OFFLINE_LOCAL_AVAILABLE to "Çevrimdışı",
    TodayState.AI_UNAVAILABLE_CORE_AVAILABLE to "Yapay zekâ kullanılamıyor",
    TodayState.ERROR_RECOVERABLE to "Açılamadı",
    TodayState.DATA_RECOVERY_REQUIRED to "Veri kurtarma gerekiyor",
)

/** The body line for a state that has no task to show. None of them says anything went wrong. */
private val stateExplanationsTr: Map<TodayState, String> = mapOf(
    TodayState.LOADING_INITIAL_PLAN to "Bugünün planı henüz üretilmedi.",
    TodayState.REPLANNING to "Plan güncel duruma göre yeniden kuruluyor. Eski plan gösterilmiyor.",
    TodayState.EMPTY_NO_OPEN_NEED to
        "Şu an çalışılacak açık bir ihtiyaç yok. Bu, her şeyin öğrenildiği anlamına gelmez.",
    TodayState.EMPTY_NO_ELIGIBLE_TASK to
        "Planda görev var ama şu an başlanabilir durumda değil. Bu bir eksiklik değil.",
    TodayState.CAPACITY_ZERO to "Bugün için süre ayrılmamış. Bu bir başarısızlık değil ve borç oluşturmaz.",
    TodayState.CAPACITY_TOO_SMALL_NO_CANDIDATE to
        "Bugünkü süreye sığan uygun bir görev bulunamadı. Bu bir başarısızlık değil ve borç oluşturmaz.",
)

private val purposeLabelsTr: Map<TaskPurpose, String> = mapOf(
    TaskPurpose.TEACH to "öğren",
    TaskPurpose.PRACTICE to "uygula",
    TaskPurpose.ASSESS to "ölç",
    TaskPurpose.REMEDIATE to "onar",
    TaskPurpose.RETAIN to "hatırla",
    TaskPurpose.DIAGNOSE to "tespit et",
    TaskPurpose.REINFORCE to "pekiştir",
)

private val reasonLabelsTr: Map<ReasonFamily, String> = mapOf(
    ReasonFamily.CONTINUE_CURRENT_LEARNING to "Başlanan öğrenmeyi sürdürüyor",
    ReasonFamily.REPAIR_CONFIRMED_WEAKNESS to "Doğrulanmış bir zayıflığı onarıyor",
    ReasonFamily.VERIFY_UNCERTAIN_STATE to "Belirsiz kalan bir durumu doğruluyor",
    ReasonFamily.REVIEW_DUE_KNOWLEDGE to "Tekrar zamanı gelen bilgiyi yokluyor",
    ReasonFamily.COLLECT_MISSING_EVIDENCE to "Eksik kanıtı topluyor",
    ReasonFamily.PARALLEL_TECHNICAL_ENGLISH to "Teknik İngilizceyi paralel ilerletiyor",
    ReasonFamily.FIT_AVAILABLE_CAPACITY to "Bugünkü süreye sığıyor",
    ReasonFamily.RESUME_VALID_PAUSED_WORK to "Geçerli bir duraklamış çalışmayı sürdürüyor",
)

private val attentionLabelsTr: Map<AttentionFamily, String> = mapOf(
    AttentionFamily.PLAN_CHANGED to "Plan değişti",
    AttentionFamily.VERIFICATION_ATTENTION to "Doğrulama bekleyen durum var",
    AttentionFamily.REMEDIATION_ATTENTION to "Onarım bekleyen durum var",
    AttentionFamily.PREREQUISITE_BLOCKER to "Ön koşul bekliyor",
    AttentionFamily.RETENTION_ATTENTION to "Tekrar zamanı gelen konu var",
    AttentionFamily.ASSESSMENT_ATTENTION to "Ölçüm bekliyor",
    AttentionFamily.RECOVERY_ATTENTION to "Veri kurtarma bekliyor",
)

private val routeLabelsTr: Map<String, String> = mapOf(
    "planner_explanation" to "Bu plan neden böyle?",
    "skill_detail" to "Yetkinlik ayrıntısı",
    "topic_detail" to "Konu ayrıntısı",
    "progress_overview" to "İlerleme",
    "profile_overview" to "Profil ve süre ayarları",
)

@Composable
fun TodayScreen(
    view: TodayView,
    onStart: () -> Unit,
    onOpen: (Surface) -> Unit,
    modifier: Modifier = Modifier,
    // The end of the day belongs to Today's own day context (11E); NSHX-v0's surface set is closed,
    // so no new destination is invented for it and longitudinal history stays Progress's (16B).
    daySummary: DaySummaryView? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 1. primary_action — the highest-emphasis region in every window class.
        PrimaryAction(view, onStart, onOpen)

        // 2. day_plan_context
        view.capacity?.let { CapacityLine(it) }
        daySummary?.let { EndOfDayView(it) }

        // 3. remaining_plan
        if (view.remainingPlan.isNotEmpty()) {
            Text("Sıradaki planlanan işler", style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.semantics { heading() })
            view.remainingPlan.forEach { TaskLine(it) }
        }

        // 4. attention_context
        view.attention.forEach { AttentionLine(it, onOpen) }

        // 5. supporting_navigation — never a precondition for starting work, so it comes last.
        view.supportingNavigation.forEach { surface ->
            TextButton(onClick = { onOpen(surface) }, modifier = Modifier.minimumTouchTarget()) {
                Text(routeLabelsTr.getValue(surface.id))
            }
        }
    }
}

@Composable
private fun PrimaryAction(view: TodayView, onStart: () -> Unit, onOpen: (Surface) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // The state is always available as text and announced when it changes; colour never carries
        // it alone (`VDSX-v0`, `THUX-v0` §14).
        StateChip(
            label = stateLabelsTr.getValue(view.state),
            tone = view.state.tone,
            modifier = Modifier.semantics {
                heading()
                liveRegion = LiveRegionMode.Polite
            },
        )

        when (view.primaryActionKind) {
            PrimaryActionKind.SELECTED_NEXT_PLANNED_TASK -> view.primaryTask?.let { task ->
                TaskHeadline(task)
                Button(onClick = onStart, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
                    Text("Başla")
                }
                TextButton(
                    onClick = { onOpen(Surface.PlannerExplanation) },
                    modifier = Modifier.minimumTouchTarget(),
                ) {
                    Text(routeLabelsTr.getValue("planner_explanation"))
                }
            }
            PrimaryActionKind.VALID_RESUMABLE_FOCUSED_SESSION -> view.resumable?.let { session ->
                Text(session.displayTitle, style = MaterialTheme.typography.titleMedium)
                Button(onClick = onStart, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
                    Text("Devam et")
                }
            }
            else -> stateExplanationsTr[view.state]?.let { explanation ->
                Text(explanation, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun TaskHeadline(task: TodayTaskRow) {
    Text(task.displayTitle, style = MaterialTheme.typography.titleMedium)
    // Purpose, activity and track stay separate labels; none of them becomes the other
    // (`THUX-v0` §6.1).
    val context = listOfNotNull(
        purposeLabelsTr[task.primaryPurpose],
        task.activityKind,
        task.curriculumTrack,
        task.estimatedMinutes?.let { "yaklaşık $it dk" },
    )
    if (context.isNotEmpty()) {
        Text(context.joinToString(" · "), style = MaterialTheme.typography.bodyMedium)
    }
    task.reason?.let { reason ->
        Text(reasonLabelsTr.getValue(reason.primary), style = MaterialTheme.typography.bodyMedium)
        reason.supporting?.let { Text(reasonLabelsTr.getValue(it), style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
private fun TaskLine(task: TodayTaskRow) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(task.displayTitle, style = MaterialTheme.typography.bodyLarge)
        val context = listOfNotNull(
            purposeLabelsTr[task.primaryPurpose],
            task.estimatedMinutes?.let { "yaklaşık $it dk" },
        )
        if (context.isNotEmpty()) {
            Text(context.joinToString(" · "), style = MaterialTheme.typography.bodySmall)
        }
    }
}

/**
 * Capacity is a time budget and is written as one: minutes, described as an estimate. There is no
 * percentage, no ring and no countdown, because none of those would be about what the learner can
 * do (`THUX-v0` §4.2, D-033).
 */
@Composable
private fun CapacityLine(capacity: CapacityContext) {
    val parts = listOfNotNull(
        "Bugün için ayrılan süre: ${capacity.resolvedDailyMinutes} dk",
        capacity.estimatedRemainingPlannedMinutes?.let { "planlanan kalan yaklaşık $it dk" },
        if (capacity.currentDayOverride) "bugüne özel değiştirildi" else null,
        if (capacity.planRecalculated) "plan yeniden hesaplandı" else null,
    )
    Text(parts.joinToString(" · "), style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun AttentionLine(item: AttentionItem, onOpen: (Surface) -> Unit) {
    val label = attentionLabelsTr.getValue(item.family)
    if (item.link == null) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
    } else {
        TextButton(onClick = { onOpen(item.link!!) }, modifier = Modifier.minimumTouchTarget()) {
            Text(label)
        }
    }
}
