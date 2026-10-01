package coach.presentation

import coach.model.DiagnosticObjectiveState
import coach.model.DiagnosticResult
import coach.model.DiagnosticStage
import coach.model.VersionedRef
import coach.model.WaiverOutcome

/**
 * A diagnostic's result as the learner reads it (13F, `VDW-v0` §10, §21; `PDT-v0` invariant 12).
 *
 * Every sentence comes from one Objective's recorded state, so the result says exactly which parts are skipped
 * and never more: only a waived Objective is said to be skipped, an Objective not shown goes back to normal
 * learning without being called a failure or a weakness, and help taken is never treated as a penalty. A waiver
 * is coverage — nothing here says a Skill or a Topic is learned. There is no score, percentage, grade or pass/fail.
 * Wording is working microcopy owned by 14.
 */
object DiagnosticResults {

    data class Summary(val headline: String, val lines: List<String>)

    fun summary(result: DiagnosticResult, label: (VersionedRef) -> String): Summary = Summary(
        headline = DiagnosticCopy.headline(result),
        lines = result.objectives.map { "${label(it.target.objective)}: ${DiagnosticCopy.line(it.state, it.stage)}" },
    )
}

object DiagnosticCopy {

    fun headline(result: DiagnosticResult): String = when {
        result.inProgress && result.waived.isEmpty() -> IN_PROGRESS_NOTHING_YET
        result.inProgress -> IN_PROGRESS_SOME_SKIPPED
        result.outcome == WaiverOutcome.FULL -> FULL
        result.outcome == WaiverOutcome.PARTIAL -> PARTIAL
        else -> NONE
    }

    fun line(state: DiagnosticObjectiveState, stage: DiagnosticStage?): String = when (state) {
        DiagnosticObjectiveState.WAIVED -> "bağımsız olarak gösterildi; başlangıç anlatımı atlanıyor."
        DiagnosticObjectiveState.ALREADY_DEMONSTRATED -> "zaten gösterilmişti; yeniden kontrol edilmedi."
        DiagnosticObjectiveState.NOT_DEMONSTRATED -> "bu kontrolde gösterilmedi; normal öğrenme akışında kalıyor."
        DiagnosticObjectiveState.ASSISTANCE_ENDED_FAST_PATH ->
            "yardım alındığı için hızlı yoldan çıktı; normal öğrenme akışında devam ediyor. Yardım istemek cezalandırılmaz."
        DiagnosticObjectiveState.PROBE_NEEDED -> "kısa bir yoklama bekliyor."
        DiagnosticObjectiveState.CONFIRM_NEEDED -> when (stage) {
            DiagnosticStage.CRITICAL_CONFIRM -> "kritik olduğu için atlamadan önce ayrıca doğrulanacak."
            DiagnosticStage.TRANSFER_CONFIRM -> "atlama kararı için yeni bir bağlamda bir doğrulama daha gerekiyor."
            else -> "atlama kararı için farklı bir doğrulama daha gerekiyor."
        }
    }

    const val IN_PROGRESS_NOTHING_YET = "Hızlı ilerleme kontrolü sürüyor; henüz atlanan bir bölüm yok."
    const val IN_PROGRESS_SOME_SKIPPED = "Hızlı ilerleme kontrolü sürüyor; şimdiye kadar bağımsız gösterdiğin bölümler atlanıyor."
    const val FULL = "Bu kapsamın tamamını bağımsız olarak gösterdin; başlangıç anlatımları atlanıyor."
    const val PARTIAL = "Yalnız bağımsız olarak gösterdiğin bölümler atlanıyor; diğerleri normal öğrenme akışında kalıyor."
    const val NONE = "Bu kontrol hiçbir bölümü atlatmadı; normal öğrenme akışından devam ediliyor. Henüz öğretilmemiş bir şeyi bilmemek olağandır."
}
