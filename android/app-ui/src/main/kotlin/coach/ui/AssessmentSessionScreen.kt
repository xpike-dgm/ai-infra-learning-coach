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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coach.model.AllowedToolsPolicy
import coach.model.AssistanceLevel
import coach.presentation.AssessmentSessionView
import coach.presentation.AssistanceConsequence
import coach.presentation.BoundaryStatus
import coach.presentation.NotReliablyMeasured
import coach.presentation.ResultFamily
import coach.presentation.SessionAssistance
import coach.presentation.SessionCopy
import coach.presentation.SessionResult
import coach.presentation.SessionState
import coach.presentation.tone

/**
 * The assessment session's focused flow. It renders what `core-presentation` decided and decides
 * nothing itself: no score is computed here, because there is no score anywhere.
 *
 * The exit comes first, as the shared `TRUX-v0` frame requires, and independence and allowed tools
 * are stated **before** the response affordance — a learner must never find out afterwards that
 * something they did changed what the attempt could prove.
 *
 * Turkish text is working microcopy owned by 14.
 */

private val stateLabelsTr: Map<SessionState, String> = mapOf(
    SessionState.ENTERING_REVALIDATING to "Ölçüm kontrol ediliyor",
    SessionState.BLOCKED_NOT_STARTABLE to "Bu ölçüm şu an başlatılamıyor",
    SessionState.SESSION_ORIENTATION to "Ölçüme hazırlan",
    SessionState.ITEM_ACTIVE to "Soru açık",
    SessionState.ASSISTANCE_OPEN to "Yardım açık",
    SessionState.SUBMITTING_BOUNDARY to "Gönderiliyor",
    SessionState.BOUNDARY_FROZEN to "Yanıt gönderildi ve kilitlendi",
    SessionState.BLOCK_CHECKPOINT to "Bölüm sonu",
    SessionState.SESSION_PAUSED to "Duraklatıldı",
    SessionState.RESUME_REVALIDATING to "Devam etmeden önce kontrol ediliyor",
    SessionState.SLOT_RECOMPOSED to "Bu soru yenisiyle değiştirildi",
    SessionState.EVALUATION_PENDING to "Değerlendirme bekliyor",
    SessionState.RESULT_READY to "Sonuç hazır",
    SessionState.RESULT_PARTIAL to "Kısmi sonuç",
    SessionState.ITEM_CONTESTED to "Soru itirazlı",
    SessionState.OFFLINE_LOCAL_CAPABLE to "Çevrimdışı",
    SessionState.AI_UNAVAILABLE_DETERMINISTIC_CORE to "Yapay zekâ kullanılamıyor",
    SessionState.ERROR_RECOVERABLE to "Açılamadı",
    SessionState.DATA_RECOVERY_REQUIRED to "Veri kurtarma gerekiyor",
)

private val familyLabelsTr: Map<ResultFamily, String> = mapOf(
    ResultFamily.CONFIRMED_CAPABILITIES to "Doğrulanan yetkinlikler",
    ResultFamily.VERIFICATION_NEEDED to "Doğrulama gerekiyor",
    ResultFamily.PERSISTENT_TARGETED_GAPS to "Süren hedefli açıklar",
    ResultFamily.RETENTION_REVALIDATED to "Hatırlama tazelendi",
    ResultFamily.NOT_RELIABLY_MEASURED to "Güvenilir biçimde ölçülemeyenler",
    ResultFamily.PLAN_CHANGES to "Plan değişiklikleri",
)

private val unreliableLabelsTr: Map<NotReliablyMeasured, String> = mapOf(
    NotReliablyMeasured.INVALID_ITEM to "değerlendirilemeyen soru",
    NotReliablyMeasured.PROVISIONAL_EVALUATION to "geçici değerlendirme",
    NotReliablyMeasured.ASSISTED_ATTEMPT to "yardımlı deneme",
    NotReliablyMeasured.SOLUTION_EXPOSED_ATTEMPT to "çözümü görülen deneme",
    NotReliablyMeasured.CONTESTED_ITEM to "itirazlı soru",
    NotReliablyMeasured.UNSUBMITTED_SLOT to "boş bırakılan soru",
)

@Composable
fun AssessmentSessionScreen(
    session: AssessmentSessionView,
    boundaryId: String,
    prompt: String,
    response: String,
    onResponseChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    onRequestHelp: (AssistanceLevel) -> Unit,
    onContest: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // The inherited frame: one deliberate action out, from every state, with no guilt gate.
        OutlinedButton(onClick = onExit, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text("Bugün'e dön")
        }

        StateChip(
            label = stateLabelsTr.getValue(session.state),
            tone = session.state.tone,
            modifier = Modifier.semantics {
                heading()
                liveRegion = LiveRegionMode.Polite
            },
        )

        // Orientation, never performance: where the learner is, with no score and no clock.
        session.positionContext(boundaryId)?.let {
            Text(it, style = MaterialTheme.typography.bodySmall)
        }

        // Disclosed before the response affordance, in text (ASUX-v0 §6.1, §18).
        Text(SessionCopy.INDEPENDENCE_DISCLOSURE, style = MaterialTheme.typography.bodyMedium)
        Text(allowedToolsTextTr(session.allowedTools), style = MaterialTheme.typography.bodyMedium)

        when (session.statusOf(boundaryId)) {
            BoundaryStatus.FROZEN -> Text(
                "Bu yanıt gönderildi. Gönderilen bir yanıt değiştirilemez; sonrasında gelen açıklama onu geriye dönük etkilemez.",
                style = MaterialTheme.typography.bodyLarge,
            )
            BoundaryStatus.UNSUBMITTED -> Text(SessionCopy.SKIPPED, style = MaterialTheme.typography.bodyLarge)
            BoundaryStatus.CONTESTED -> Text(SessionCopy.CONTESTED, style = MaterialTheme.typography.bodyLarge)
            BoundaryStatus.RECOMPOSED -> Text(
                "Bu soru yenisiyle değiştirildi. Bu bir tekrar denemesi değil, yeni bir ölçüm.",
                style = MaterialTheme.typography.bodyLarge,
            )
            BoundaryStatus.OPEN -> {
                Text(prompt, style = MaterialTheme.typography.bodyLarge)
                OutlinedTextField(
                    value = response,
                    onValueChange = onResponseChange,
                    label = { Text("Yanıtın") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(onClick = onSubmit, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
                    Text("Yanıtı gönder")
                }
                // Skipping is offered as plainly as answering: it is not a failure.
                OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
                    Text("Bu soruyu boş bırak")
                }
                AssistanceLevel.entries.forEach { level ->
                    OutlinedButton(
                        onClick = { onRequestHelp(level) },
                        modifier = Modifier.fillMaxWidth().minimumTouchTarget(),
                    ) {
                        Text("${level.id} yardım iste")
                    }
                }
                Text(consequenceTextTr(AssistanceLevel.H1), style = MaterialTheme.typography.bodySmall)
                Text(consequenceTextTr(AssistanceLevel.H3), style = MaterialTheme.typography.bodySmall)
            }
        }

        // Low friction, no explanation demanded, and it never harms the learner's state.
        OutlinedButton(onClick = onContest, modifier = Modifier.fillMaxWidth().minimumTouchTarget()) {
            Text("Bu soru hatalı ya da anlaşılmaz")
        }
    }
}

/** The semantic result: families as text, never a banner, a grade or a percentage. */
@Composable
fun AssessmentResultView(result: SessionResult, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StateChip(
            label = stateLabelsTr.getValue(result.state),
            tone = result.state.tone,
            modifier = Modifier.semantics {
                heading()
                liveRegion = LiveRegionMode.Polite
            },
        )

        if (result.families.isEmpty()) {
            Text(SessionCopy.NOTHING_CHANGED, style = MaterialTheme.typography.bodyLarge)
        } else {
            ResultFamily.entries.forEach { family ->
                val entries = result.families[family].orEmpty()
                if (entries.isNotEmpty()) {
                    Text(familyLabelsTr.getValue(family), style = MaterialTheme.typography.titleMedium)
                    entries.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }

        // Always shown when non-empty, and never folded into "incorrect".
        if (result.showsNotReliablyMeasured) {
            Text(familyLabelsTr.getValue(ResultFamily.NOT_RELIABLY_MEASURED), style = MaterialTheme.typography.titleMedium)
            result.notReliablyMeasured.forEach {
                Text("• " + unreliableLabelsTr.getValue(it), style = MaterialTheme.typography.bodyMedium)
            }
        }

        result.counts?.let { counts ->
            Text(
                "Sayım (yalnız bilgi için, sonuç değil): doğru ${counts.correct}, yanlış ${counts.incorrect}, " +
                    "kısmi ${counts.partial}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

private fun allowedToolsTextTr(policy: AllowedToolsPolicy): String {
    val allowed = if (policy.allowed.isEmpty()) "yok" else policy.allowed.joinToString(", ")
    val prohibited = policy.prohibitedSolutionSources
    val tail = if (prohibited.isEmpty()) "" else " İzin verilmeyen çözüm kaynakları: ${prohibited.joinToString(", ")}."
    return "İzin verilen araçlar: $allowed.$tail"
}

private fun consequenceTextTr(level: AssistanceLevel): String =
    when (SessionAssistance.consequenceOf(level)) {
        AssistanceConsequence.ASSISTED -> SessionCopy.ASSISTED_CONSEQUENCE
        AssistanceConsequence.PRACTICE_ONLY_SOLUTION_EXPOSED -> SessionCopy.SOLUTION_EXPOSED_CONSEQUENCE
    }
