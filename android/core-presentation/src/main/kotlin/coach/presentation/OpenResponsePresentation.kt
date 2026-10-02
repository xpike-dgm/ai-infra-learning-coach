package coach.presentation

import coach.model.CriterionVerdict
import coach.model.EvaluationResult
import coach.model.OpenResponseNotMeasured
import coach.model.OpenResponseVerdict
import coach.model.OutcomeSignal
import coach.model.RubricCriterion
import coach.model.VersionedRef

/**
 * What is said after an open response is evaluated (14F, `OREX-v0 / D-110`).
 *
 * A short answer is said to match the accepted answers or not; an AI's judgement is worded as its view, criterion by
 * criterion, and always labelled provisional, with the reminder that length and style are not judged. A response that
 * waits is never called wrong, and the self-check is labelled as practice and as a shown solution. No score, count or
 * percentage. Wording is working microcopy.
 */
object OpenResponseCopy {
    const val KEY_MET = "Cevabın kabul edilen cevaplardan biriyle eşleşti."
    const val KEY_NOT_MET = "Cevabın kabul edilen cevaplarla eşleşmedi."

    const val AI_MET = "AI'a göre bu hedefin ölçütleri karşılanmış görünüyor."
    const val AI_PARTIALLY_MET = "AI'a göre bu hedefin ölçütlerinin bir kısmı eksik görünüyor."
    const val AI_NOT_MET = "AI'a göre bu hedefin ölçütleri henüz karşılanmamış görünüyor."
    const val AI_NOT_MEASURED = "AI bu hedefi güvenilir biçimde değerlendiremedi. Bu bir yanlış sayılmadı."

    const val CRITERION_MET = "Karşılandı:"
    const val CRITERION_NOT_MET = "Karşılanmadı:"
    const val CRITERION_UNCLEAR = "Anlaşılamadı:"

    const val AI_LABEL = "AI değerlendirmesi: doğrulanmamıştır. Bilgi verir ama tek başına bir beceriyi geçirmez."
    const val NOT_STYLE = "Yalnız hedef kavramların doğruluğuna bakıldı; cevabın uzunluğu ya da üslubu değerlendirilmedi."

    const val SELF_CHECK_OFFER = "İstersen cevabını dersin ölçütleriyle kendin karşılaştırabilirsin. Bu bir öz-kontroldür, kanıt sayılmaz; ölçütleri görmek bu soruyu ileride taze ölçüm olmaktan çıkarır."
    const val SELF_CHECK_LABEL = "Öz-kontrol: kanıt sayılmaz."
    const val ASK_AGAIN = "Değerlendirmeyi istediğin zaman yeniden isteyebilirsin; kendiliğinden yeniden denenmez."

    fun notMeasured(reason: OpenResponseNotMeasured): String = when (reason) {
        OpenResponseNotMeasured.NOTHING_SUBMITTED -> "Cevap yazılmadı; boş bırakmak yanlış sayılmaz."
        OpenResponseNotMeasured.NOTHING_TO_JUDGE_BY -> "Bu görev için dersin bir cevap anahtarı ya da ölçütü yok; değerlendirilmedi. Bu bir yanlış sayılmadı."
        OpenResponseNotMeasured.VERIFIED_EVALUATOR_REQUIRED -> "Bu görev doğrulanmış bir değerlendirme istiyor ve AI bunu veremez; şimdilik değerlendirilmeyecek. Bu bir yanlış sayılmadı."
        OpenResponseNotMeasured.TASK_TEXT_MISSING -> "Görevin metni bulunamadı, bu yüzden değerlendirilemedi. Bu bir yanlış sayılmadı."
        OpenResponseNotMeasured.EVALUATOR_DID_NOT_ANSWER -> "Değerlendirme şu an yapılamadı; cevabın bekliyor. Bu bir yanlış sayılmadı."
    }
}

object OpenResponsePresentation {

    /** One line per Objective, in the result's order. */
    fun lines(verdict: OpenResponseVerdict.Measured): List<Pair<VersionedRef, String>> = when (val result = verdict.result) {
        is EvaluationResult.Verified -> result.componentResults.map {
            it.objectiveRef to if (it.signal == OutcomeSignal.MET) OpenResponseCopy.KEY_MET else OpenResponseCopy.KEY_NOT_MET
        }
        is EvaluationResult.Provisional -> result.componentResults.map { it.objectiveRef to ai(it.signal) }
        is EvaluationResult.EvaluationPending -> emptyList()
    }

    /** The AI's finding on each criterion, in the rubric's order: what was and was not found, never a grade. */
    fun findings(verdict: OpenResponseVerdict.Measured, criteria: List<RubricCriterion>): List<String> {
        val result = verdict.result as? EvaluationResult.Provisional ?: return emptyList()
        val byId = result.rubricFindings.associate { it.criterion to it.verdict }
        return criteria.mapNotNull { criterion ->
            byId[criterion.id]?.let { "${prefix(it)} ${criterion.statement}" }
        }
    }

    /** Beside an AI's judgement: the provisional label and that style is not judged. Nothing beside a key's verdict. */
    fun notes(verdict: OpenResponseVerdict.Measured): List<String> =
        if (verdict.result is EvaluationResult.Provisional) listOf(OpenResponseCopy.AI_LABEL, OpenResponseCopy.NOT_STYLE) else emptyList()

    /** A response that produced no evidence: why, and — while it waits for an evaluator — the self-check and asking again. */
    fun notMeasured(verdict: OpenResponseVerdict.NotMeasured): List<String> = buildList {
        add(OpenResponseCopy.notMeasured(verdict.reason))
        if (verdict.reason == OpenResponseNotMeasured.EVALUATOR_DID_NOT_ANSWER || verdict.reason == OpenResponseNotMeasured.VERIFIED_EVALUATOR_REQUIRED) {
            add(OpenResponseCopy.SELF_CHECK_OFFER)
        }
        if (verdict.reason == OpenResponseNotMeasured.EVALUATOR_DID_NOT_ANSWER) add(OpenResponseCopy.ASK_AGAIN)
    }

    /** The rubric shown for the self-check, always under its label. */
    fun selfCheck(criteria: List<RubricCriterion>): List<String> = listOf(OpenResponseCopy.SELF_CHECK_LABEL) + criteria.map { it.statement }

    private fun ai(signal: OutcomeSignal): String = when (signal) {
        OutcomeSignal.MET -> OpenResponseCopy.AI_MET
        OutcomeSignal.PARTIALLY_MET -> OpenResponseCopy.AI_PARTIALLY_MET
        OutcomeSignal.NOT_MET -> OpenResponseCopy.AI_NOT_MET
        OutcomeSignal.NOT_RELIABLY_MEASURED -> OpenResponseCopy.AI_NOT_MEASURED
    }

    private fun prefix(verdict: CriterionVerdict): String = when (verdict) {
        CriterionVerdict.MET -> OpenResponseCopy.CRITERION_MET
        CriterionVerdict.NOT_MET -> OpenResponseCopy.CRITERION_NOT_MET
        CriterionVerdict.UNCLEAR -> OpenResponseCopy.CRITERION_UNCLEAR
    }
}
