package coach.presentation

import coach.model.CodeNotMeasured
import coach.model.CodeVerdict
import coach.model.EvaluationResult
import coach.model.OutcomeSignal
import coach.model.VersionedRef

/**
 * What is said after a code submission is evaluated (14D, `CDEX-v0 / D-108`).
 *
 * One sentence per Objective, from the result alone — no score, count or percentage. A test that did not run is said
 * to have measured nothing, and nothing that went wrong with a report or a runner is said to be the learner's mistake.
 * An AI's judgement is always labelled provisional, and passing tests are never presented as understanding.
 * Wording is working microcopy.
 */
object CodeEvaluationCopy {
    const val MET = "Bu hedefin kontrolleri geçti."
    const val PARTIALLY_MET = "Bu hedefin kontrollerinden bazıları geçmedi."
    const val NOT_MET = "Bu hedefin kontrolleri geçmedi."
    const val NOT_MEASURED = "Bu hedef ölçülemedi: kontrolleri çalışmadı. Bu bir yanlış sayılmadı."

    const val AI_MET = "AI'a göre bu hedef karşılanmış görünüyor."
    const val AI_PARTIALLY_MET = "AI'a göre bu hedefin bir kısmı eksik görünüyor."
    const val AI_NOT_MET = "AI'a göre bu hedef henüz karşılanmamış görünüyor."
    const val AI_NOT_MEASURED = "AI bu hedefi güvenilir biçimde değerlendiremedi. Bu bir yanlış sayılmadı."

    const val TESTS_ARE_NOT_UNDERSTANDING =
        "Testlerin geçmesi kodun istenen şekilde çalıştığını gösterir; neden çalıştığını açıklayabilmek ayrıca ölçülür."
    const val AI_LABEL = "AI değerlendirmesi: doğrulanmamıştır. Bilgi verir ama tek başına bir beceriyi geçirmez."

    fun notMeasured(reason: CodeNotMeasured): String = when (reason) {
        CodeNotMeasured.REPORT_MISSING -> "Bu görevin testleri var. Test koşucusunu bilgisayarında çalıştırıp çıkan raporu buraya yapıştır."
        CodeNotMeasured.REPORT_MALFORMED -> "Rapor okunamadı. Koşucunun çıktısını baştan sona, değiştirmeden yapıştır. Bu bir yanlış sayılmadı."
        CodeNotMeasured.REPORT_FOR_ANOTHER_ITEM -> "Bu rapor başka bir görevin. Bu görevin testlerini çalıştırıp raporunu yapıştır. Bu bir yanlış sayılmadı."
        CodeNotMeasured.REPORT_FOR_ANOTHER_SUITE -> "Bu rapor testlerin başka bir sürümünden. Güncel testleri çalıştırıp yeniden yapıştır. Bu bir yanlış sayılmadı."
        CodeNotMeasured.REPORT_DOES_NOT_MATCH_SUITE -> "Rapordaki testler bu görevin testleriyle eşleşmiyor. Bu bir yanlış sayılmadı."
        CodeNotMeasured.ENVIRONMENT_ERROR -> "Test ortamı kodu çalıştıramadı (örneğin derleyici bulunamadı). Bu kodun hakkında bir şey söylemez ve yanlış sayılmadı."
        CodeNotMeasured.NO_SUITE_FOR_REPORT -> "Bu görev için dersin testi yok, bu yüzden rapor kullanılamadı."
        CodeNotMeasured.TESTS_REQUIRED -> "Bu görev doğrulanmış bir sonuç istiyor ve testleri henüz yazılmadı; şimdilik değerlendirilmeyecek. Bu bir yanlış sayılmadı."
        CodeNotMeasured.NOTHING_SUBMITTED -> "Kod gönderilmedi; boş bırakmak yanlış sayılmaz."
        CodeNotMeasured.TASK_TEXT_MISSING -> "Görevin metni bulunamadı, bu yüzden değerlendirilemedi. Bu bir yanlış sayılmadı."
        CodeNotMeasured.EVALUATOR_DID_NOT_ANSWER -> "Değerlendirme şu an yapılamadı; daha sonra yeniden denenebilir. Bu bir yanlış sayılmadı."
    }
}

object CodeEvaluationPresentation {

    /** One line per Objective, in the result's order. */
    fun lines(verdict: CodeVerdict.Measured): List<Pair<VersionedRef, String>> {
        val ai = verdict.result is EvaluationResult.Provisional
        val components = when (val result = verdict.result) {
            is EvaluationResult.Verified -> result.componentResults
            is EvaluationResult.Provisional -> result.componentResults
            is EvaluationResult.EvaluationPending -> emptyList()
        }
        return components.map { it.objectiveRef to sentence(it.signal, ai) }
    }

    /** What is said beside the lines: the AI label for an AI's judgement; for tests, that passing is not understanding. */
    fun notes(verdict: CodeVerdict.Measured): List<String> =
        if (verdict.result is EvaluationResult.Provisional) listOf(CodeEvaluationCopy.AI_LABEL) else listOf(CodeEvaluationCopy.TESTS_ARE_NOT_UNDERSTANDING)

    fun notMeasured(verdict: CodeVerdict.NotMeasured): String = CodeEvaluationCopy.notMeasured(verdict.reason)

    private fun sentence(signal: OutcomeSignal, ai: Boolean): String = when (signal) {
        OutcomeSignal.MET -> if (ai) CodeEvaluationCopy.AI_MET else CodeEvaluationCopy.MET
        OutcomeSignal.PARTIALLY_MET -> if (ai) CodeEvaluationCopy.AI_PARTIALLY_MET else CodeEvaluationCopy.PARTIALLY_MET
        OutcomeSignal.NOT_MET -> if (ai) CodeEvaluationCopy.AI_NOT_MET else CodeEvaluationCopy.NOT_MET
        OutcomeSignal.NOT_RELIABLY_MEASURED -> if (ai) CodeEvaluationCopy.AI_NOT_MEASURED else CodeEvaluationCopy.NOT_MEASURED
    }
}
