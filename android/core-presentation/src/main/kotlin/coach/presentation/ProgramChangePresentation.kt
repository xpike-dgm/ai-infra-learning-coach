package coach.presentation

import coach.model.NeedTrigger
import coach.model.PlanChange
import coach.model.PlanChangeKind
import coach.model.ProgramChangeReport
import coach.model.StateChangeKind
import coach.model.VersionedRef

/**
 * The program change report as the result's families (13E): `ASUX-v0` §13.1's sections, filled only with
 * what [ProgramChangeReport] says canonical state and the plan did.
 *
 * Each statement comes from one recorded change; there is no sentence for a change that was not recorded,
 * so a result cannot claim progress the engines did not write. When nothing changed the result says so
 * plainly (§13.4) instead of showing empty sections. Wording is working microcopy owned by 14; what each
 * sentence may claim is canonical and checked:
 * - a weakness hypothesis is an open question, never a deficiency (`SPWX-v0`),
 * - a verification is a re-check, not a loss of mastery,
 * - a closed need or a dropped task is not called less important, failed or owed,
 * - no score, percentage, grade or pass/fail.
 */
object ProgramChangeResults {

    /** The sections [SessionResults.of] takes as `canonicalChanges`. */
    fun canonicalChanges(report: ProgramChangeReport, label: (VersionedRef) -> String): Map<ResultFamily, List<String>> {
        val families = linkedMapOf<ResultFamily, MutableList<String>>()
        report.stateChanges.forEach { change ->
            val family = ResultFamily.entries.single { it.id == change.kind.family }
            // A coverage change is about one Objective (13F), so the Objective is what is named.
            families.getOrPut(family) { mutableListOf() } +=
                "${label(change.objective ?: change.skill)}: ${ProgramChangeCopy.stateTemplates.getValue(change.kind)}"
        }
        val plan = planStatements(report, label)
        if (plan.isNotEmpty()) families[ResultFamily.PLAN_CHANGES] = plan.toMutableList()
        return families
    }

    /**
     * The plan section: what the new plan opened, closed, added or dropped. A first plan has nothing to be
     * compared with and is not described as a change.
     */
    fun planStatements(report: ProgramChangeReport, label: (VersionedRef) -> String): List<String> =
        report.planChanges.map { change -> "${skillsOf(change, label)}${ProgramChangeCopy.planTemplate(change)}" }

    /** The single sentence shown instead of the sections when nothing changed (`ASUX-v0` §13.4). */
    fun summary(report: ProgramChangeReport): String? = when {
        !report.nothingChanged -> null
        report.firstPlan -> ProgramChangeCopy.FIRST_PLAN
        else -> ProgramChangeCopy.NOTHING_CHANGED
    }

    private fun skillsOf(change: PlanChange, label: (VersionedRef) -> String): String =
        if (change.skills.isEmpty()) "" else change.skills.joinToString(", ", postfix = ": ") { label(it) }
}

object ProgramChangeCopy {

    const val NOTHING_CHANGED = "Bu oturum becerilerinin durumunu ve planını değiştirmedi."

    const val FIRST_PLAN = "Bu ilk planın; karşılaştırılacak önceki bir plan yok."

    /** A Skill whose earlier state no engine had written: nothing is claimed about how it changed. */
    const val UNKNOWN_BEFORE = "Bu becerinin önceki durumu henüz değerlendirilmemişti; bir değişiklik iddia edilmiyor."

    val stateTemplates: Map<StateChangeKind, String> = linkedMapOf(
        StateChangeKind.CAPABILITY_CONFIRMED to "Bağımsız yapabildiğin artık doğrulandı.",
        StateChangeKind.VERIFICATION_OPENED to "Önceki sonuçla çelişen yeni bir sonuç var; farklı bir görevle yeniden kontrol edilecek.",
        StateChangeKind.VERIFICATION_RESOLVED to "Yeniden kontrol tamamlandı; bağımsız yapabildiğin yeniden doğrulandı.",
        StateChangeKind.MASTERY_NO_LONGER_CONFIRMED to "Yeniden kontrol de doğrulamadı; bu beceri yeniden çalışılacak.",
        StateChangeKind.REMEDIATION_OPENED to "Doğrulanmış bir eksik var; hedefli bir onarım çalışması planlanacak.",
        StateChangeKind.REMEDIATION_CLOSED to "Onarım taze ve bağımsız bir kontrolle kapandı.",
        StateChangeKind.WEAKNESS_QUESTION_OPENED to "Bir zorluk olabilir; bu henüz bir eksik değil, netleştirilecek bir soru.",
        StateChangeKind.WEAKNESS_SUPPORTED to "Bir zorluk birden fazla sonuçla destekleniyor; henüz doğrulanmış bir eksik değil.",
        StateChangeKind.WEAKNESS_RESOLVED to "Netleştirilen soru kapandı; bağımsız bir kontrol bunu gösterdi.",
        StateChangeKind.RETENTION_REVALIDATED to "Gecikmeli bir kontrolle hâlâ yapabildiğin doğrulandı.",
        StateChangeKind.RETENTION_AT_RISK to "Birden fazla sinyal bu beceriyi yeniden kontrol etmeyi değerli kılıyor.",
        // 13F: a waiver is coverage, not mastery — it says why a lesson is skipped, never that the Skill is learned.
        StateChangeKind.COVERAGE_WAIVED to "Bu bölümü bağımsız olarak gösterdin; başlangıç anlatımı atlanacak.",
        StateChangeKind.COVERAGE_WAIVER_WITHDRAWN to "Bu bölümü atlatan sonuç güvenilir biçimde ölçülemedi; başlangıç anlatımı yeniden planda.",
    )

    fun planTemplate(change: PlanChange): String = when (change.kind) {
        PlanChangeKind.NEED_OPENED -> "Planda yeni bir ihtiyaç açıldı (${triggerName(change.trigger)})."
        PlanChangeKind.NEED_CLOSED -> "Bu ihtiyaç yeni planda yer almıyor."
        PlanChangeKind.TASK_ADDED -> "Plana yeni bir görev eklendi."
        PlanChangeKind.TASK_REMOVED -> "Bu görev yeni planda yer almıyor."
    }

    private fun triggerName(trigger: NeedTrigger?): String = when (trigger) {
        NeedTrigger.NEW_LEARNING -> "yeni öğrenme"
        NeedTrigger.CONTINUE_LEARNING -> "süren öğrenme"
        NeedTrigger.WEAKNESS_DETECTED -> "netleştirilecek bir zorluk"
        NeedTrigger.REMEDIATION_REQUIRED -> "hedefli onarım"
        NeedTrigger.RETENTION_REVIEW_DUE -> "tekrar zamanı"
        NeedTrigger.VERIFICATION_DUE -> "yeniden kontrol"
        NeedTrigger.DIAGNOSTIC_OPPORTUNITY -> "tespit fırsatı"
        NeedTrigger.REINFORCEMENT_OPPORTUNITY -> "pekiştirme"
        NeedTrigger.PARALLEL_TRACK_DUE -> "paralel hat"
        NeedTrigger.INTEGRATION_OPPORTUNITY -> "birlikte kullanma"
        NeedTrigger.TRANSFER_OPPORTUNITY -> "başka bir bağlamda kullanma"
        null -> "nedeni kayıtlı değil"
    }
}
