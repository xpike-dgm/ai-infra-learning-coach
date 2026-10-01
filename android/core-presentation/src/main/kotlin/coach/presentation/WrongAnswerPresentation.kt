package coach.presentation

import coach.model.AttributionOutcome
import coach.model.VersionedRef
import coach.model.WeaknessSignal
import coach.model.WrongAnswerFinding

/**
 * A wrong answer as the learner reads it, right after it (14B, `WAAX-v0 / D-106`).
 *
 * `LEARNING_BEHAVIOR_RULES` §5: a wrong answer is a learning signal, not a verdict. Every sentence comes from how
 * the weakness engine attributed the row: work it could not attribute, or work resting on a missing prerequisite,
 * is said not to count against the learner; assisted or uncertain work is a question mark, not a finding; only a
 * clean failure says the Objective needs more work — and never that the Topic, the Skill or the learner is weak.
 *
 * A misconception that is only a **hypothesis** may be put to the learner once, here, as its catalog's open
 * question, and nowhere else (user decision, 2026-10-01; `SPWX-v0`: a hypothesis is never a deficiency). A label
 * the evidence supports or confirms is named. A label on a row that blamed nothing is not mentioned at all.
 * There is no score, percentage, number or grade. Wording is working microcopy.
 */
object WrongAnswerSummary {

    data class Summary(val headline: String, val lines: List<String>, val openQuestions: List<String>, val named: List<String>)

    /** Outcomes after which a label on the row may be mentioned at all: the row said something about the target. */
    private val speaksAboutTarget = setOf(
        AttributionOutcome.OBJECTIVE_WEAKNESS_HYPOTHESIS,
        AttributionOutcome.OBJECTIVE_WEAKNESS_SUPPORTED,
        AttributionOutcome.VERIFICATION_DUE,
        AttributionOutcome.REMEDIATION_REQUIRED,
    )

    fun of(findings: List<WrongAnswerFinding>, label: (VersionedRef) -> String): Summary {
        val relevant = findings.filter { it.outcome in speaksAboutTarget }.flatMap { it.misconceptions }
        return Summary(
            headline = WrongAnswerCopy.HEADLINE,
            lines = findings.map { "${label(it.objective)}: ${WrongAnswerCopy.outcome(it.outcome)}" },
            openQuestions = relevant.filter { it.second == WeaknessSignal.HYPOTHESIS }.map { it.first.openQuestion }.distinct(),
            named = relevant.filter { it.second == WeaknessSignal.SUPPORTED || it.second == WeaknessSignal.CONFIRMED }
                .map { WrongAnswerCopy.named(it.first.name) }.distinct(),
        )
    }
}

object WrongAnswerCopy {

    const val HEADLINE = "Cevabın kaydedildi. Yanlış bir cevap bir öğrenme sinyalidir; senin hakkında bir hüküm değildir."

    fun outcome(outcome: AttributionOutcome?): String = when (outcome) {
        null -> "Bu cevap kaydedildi; tek başına bir sonuç çıkarılmadı."
        AttributionOutcome.NOT_ATTRIBUTABLE -> "Bu soru ölçüm için kullanılamadı; sonuç senin hanene yazılmadı."
        AttributionOutcome.CONTENT_OR_ENVIRONMENT_ISSUE -> "Sorun içerikte ya da ortamda görünüyor; sonuç senin hanene yazılmadı."
        AttributionOutcome.PREREQUISITE_SIGNAL -> "Bu sonuç hedefi suçlamıyor: önce gereken bir konu henüz hazır görünmüyor."
        AttributionOutcome.OBJECTIVE_WEAKNESS_HYPOTHESIS -> "Bu deneme yalnız bir soru işareti bıraktı; bir sonuç çıkarılmadı."
        AttributionOutcome.OBJECTIVE_WEAKNESS_SUPPORTED ->
            "Bu hedef üzerinde biraz daha çalışmak işe yarayacak; tek bir cevap konunun tamamı hakkında bir şey söylemez."
        AttributionOutcome.VERIFICATION_DUE ->
            "Daha önce gösterdiğin bir yetkinlikle çelişiyor; hiçbir şey silinmedi, ileride görmediğin bir soruyla yeniden bakılacak."
        AttributionOutcome.REMEDIATION_REQUIRED -> "Bu hedef hedefli bir tekrar için işaretlendi; ne zaman yapılacağına plan karar verir."
        AttributionOutcome.POSITIVE_RECOVERY_EVIDENCE -> "Bu cevap kaydedildi."
    }

    fun named(name: String): String = "Bu cevap şu kavram yanılgısıyla uyumlu: $name."
}
