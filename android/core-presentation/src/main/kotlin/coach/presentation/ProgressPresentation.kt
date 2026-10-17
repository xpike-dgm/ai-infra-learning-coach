package coach.presentation

import coach.model.AttentionQualifier
import coach.model.EvidenceKind
import coach.model.EvidenceSummary
import coach.model.MasteryAxisState
import coach.model.PrerequisiteEligibility
import coach.model.ProgressFacts
import coach.model.RetentionAxis
import coach.model.SkillDetailFacts
import coach.model.SkillPresentationState
import coach.model.SkillProgressRow
import coach.model.SkillStateProjection
import coach.model.UnevaluatedAxis
import coach.model.VersionedRef
import coach.model.WeaknessAxis
import coach.model.WeaknessSignal

/**
 * `SPWX-v0 / D-072` §§7–8, 14, 16–18 as pure functions (16A, `SKAX-v0`): **Progress is a projection of canonical
 * evidence state.** It counts and never scores — no ratio, percentage, bar, level or rank can be built from these
 * types, because none of them holds a total to divide by — and it decides no Skill state: every state comes from
 * [coach.model.SkillPresentationProjection], the one function the stored row is written with too.
 */

/** `SPWX-v0` §17's ten Progress states, in its order. No eleventh exists. */
enum class ProgressState(val id: String, val tone: Tone) {
    LOADING_PROJECTION("loading_projection", Tone.NEUTRAL),
    RECOMPUTING_PROJECTION("recomputing_projection", Tone.NEUTRAL),
    READY_WITH_EVIDENCE("ready_with_evidence", Tone.NEUTRAL),
    EMPTY_NO_EVIDENCE_YET("empty_no_evidence_yet", Tone.NEUTRAL),
    EMPTY_NO_ATTENTION_NEEDED("empty_no_attention_needed", Tone.NEUTRAL),
    PARTIAL_PROJECTION_AVAILABLE("partial_projection_available", Tone.PENDING_UNRESOLVED),
    OFFLINE_LOCAL_CAPABLE("offline_local_capable", Tone.NEUTRAL),
    AI_UNAVAILABLE_FULL_STATE_AVAILABLE("ai_unavailable_full_state_available", Tone.NEUTRAL),
    ERROR_RECOVERABLE("error_recoverable", SystemFaultState.ERROR_RECOVERABLE.tone),
    DATA_RECOVERY_REQUIRED("data_recovery_required", SystemFaultState.DATA_RECOVERY_REQUIRED.tone),
}

/**
 * `SPWX-v0` §7.2: attention grouped by the reason it exists. A set of current needs — not a backlog, a debt or a
 * to-do count — and it gives the planner no priority.
 */
enum class AttentionGroup(val id: String) {
    REMEDIATION_OPEN("remediation_open"),
    VERIFICATION_DUE("verification_due"),
    REVIEW_DUE("review_due"),
    RETENTION_AT_RISK("retention_at_risk"),
    PREREQUISITE_BLOCKED("prerequisite_blocked"),
    /** Named by `SPWX-v0`; nothing produces it yet — the app composes no assessment session for a Skill (16D). */
    ASSESSMENT_OPPORTUNITY("assessment_opportunity"),
}

/** Working Turkish microcopy (17 owns its polish). The eight state labels are `SPWX-v0` §4.3's, locked. */
object ProgressCopy {
    val stateLabels: Map<SkillPresentationState, String> = mapOf(
        SkillPresentationState.NOT_YET_EVIDENCED to "Henüz Kanıt Yok",
        SkillPresentationState.DEVELOPING_WITH_SUPPORT to "Destekle Gelişiyor",
        SkillPresentationState.DEVELOPING_INDEPENDENT to "Bağımsız Gelişiyor",
        SkillPresentationState.CONFIRMED_CURRENT to "Doğrulanmış",
        SkillPresentationState.CONFIRMED_REVIEW_DUE to "Doğrulanmış · Tekrar Zamanı",
        SkillPresentationState.CONFIRMATION_VERIFICATION_DUE to "Doğrulanmış · Yeniden Kontrol Gerekli",
        SkillPresentationState.REMEDIATION_REQUIRED to "Pekiştirme Gerekli",
        SkillPresentationState.PREREQUISITE_UNRESOLVED to "Ön Koşul Bekliyor",
    )
    const val RECOMPUTING = "Yeniden hesaplanıyor"

    val qualifierLabels: Map<AttentionQualifier, String> = mapOf(
        // `RVR-v0` §2: mixed or repeated retention signal — never elapsed time alone.
        AttentionQualifier.RETENTION_AT_RISK to "Kalıcılık izlemede: karışık sinyal var",
        AttentionQualifier.REQUIRES_INDEPENDENT_RECHECK to "Bağımsız bir kontrol bekleniyor",
        AttentionQualifier.CONTESTED_EVIDENCE_PRESENT to "İtiraz edilmiş bir soru var",
        AttentionQualifier.PROVISIONAL_EVALUATION_PRESENT to "Kesinleşmemiş bir değerlendirme var",
    )

    val attentionLabels: Map<AttentionGroup, String> = mapOf(
        AttentionGroup.REMEDIATION_OPEN to "Pekiştirme",
        AttentionGroup.VERIFICATION_DUE to "Yeniden kontrol",
        AttentionGroup.REVIEW_DUE to "Tekrar zamanı",
        AttentionGroup.RETENTION_AT_RISK to "Kalıcılık izlemede",
        AttentionGroup.PREREQUISITE_BLOCKED to "Ön koşul bekliyor",
        AttentionGroup.ASSESSMENT_OPPORTUNITY to "Ölçme fırsatı",
    )

    val stateTexts: Map<ProgressState, String> = mapOf(
        ProgressState.LOADING_PROJECTION to "Durum okunuyor.",
        ProgressState.RECOMPUTING_PROJECTION to "Durum yeniden hesaplanıyor; eski bir durum güncelmiş gibi gösterilmiyor.",
        ProgressState.READY_WITH_EVIDENCE to "Kanıtladıkların ve dikkat isteyenler.",
        // A legitimate start, never a failure (`SPWX-v0` §17).
        ProgressState.EMPTY_NO_EVIDENCE_YET to "Henüz kanıt yok. İlk denemelerinden sonra burada neyi gösterdiğin görünecek.",
        // Never "everything is mastered" and never "ready" (`SPWX-v0` §17).
        ProgressState.EMPTY_NO_ATTENTION_NEEDED to "Şu an dikkat isteyen bir şey yok. Bu her şeyin öğrenildiği anlamına gelmez.",
        ProgressState.PARTIAL_PROJECTION_AVAILABLE to "Bazı Skill'ler yeniden hesaplanıyor; onlar ayrıca işaretli.",
        ProgressState.OFFLINE_LOCAL_CAPABLE to "Çevrimdışı; yerel durum eksiksiz görünüyor.",
        ProgressState.AI_UNAVAILABLE_FULL_STATE_AVAILABLE to "Yapay zekâ kullanılamıyor; durumun tamamı yine görünüyor.",
        ProgressState.ERROR_RECOVERABLE to "Durum okunamadı. Hiçbir şey silinmedi.",
        ProgressState.DATA_RECOVERY_REQUIRED to "Veri kurtarma gerekiyor. Hiçbir şey sıfırlanmadı.",
    )

    const val INVENTORY_TITLE = "Kanıtlanmış yetkinlik"
    const val INVENTORY_NOTE = "Bugünkü durumun envanteri: her satır o durumdaki Skill sayısı."
    const val ATTENTION_TITLE = "Dikkat gerektirenler"
    const val ATTENTION_EMPTY = "Şu an dikkat isteyen bir şey yok."
    const val NO_CURRICULUM = "Müfredat henüz yüklenmedi."

    val axisTitles: Map<UnevaluatedAxis, String> = mapOf(
        UnevaluatedAxis.MASTERY to "Ustalık",
        UnevaluatedAxis.RETENTION to "Kalıcılık",
        UnevaluatedAxis.PREREQUISITE to "Ön koşul",
        UnevaluatedAxis.WEAKNESS to "Zayıflık",
    )
    const val NOT_EVALUATED = "Henüz değerlendirilmedi"

    val masteryLabels: Map<MasteryAxisState, String> = mapOf(
        MasteryAxisState.NOT_YET_EVIDENCED to "Henüz kanıt yok",
        MasteryAxisState.DEVELOPING_WITH_SUPPORT to "Destekle gelişiyor",
        MasteryAxisState.DEVELOPING_INDEPENDENT to "Bağımsız gelişiyor",
        MasteryAxisState.CONFIRMED_CURRENT to "Doğrulanmış",
        MasteryAxisState.CONFIRMATION_VERIFICATION_DUE to "Yeniden kontrol gerekli",
    )
    val retentionLabels: Map<RetentionAxis, String> = mapOf(
        RetentionAxis.UNTRACKED to "İzlenmiyor (henüz doğrulanmadı)",
        RetentionAxis.FRESH to "Yeni doğrulandı",
        RetentionAxis.STABLE to "Kalıcı",
        // `review_due` is not forgetting (`RVR-v0`).
        RetentionAxis.REVIEW_DUE to "Tekrar zamanı",
        RetentionAxis.VERIFICATION_DUE to "Yeniden kontrol gerekli",
        RetentionAxis.AT_RISK to "İzlemede",
        RetentionAxis.NOT_YET_EVALUATED to NOT_EVALUATED,
    )
    val prerequisiteLabels: Map<PrerequisiteEligibility, String> = mapOf(
        PrerequisiteEligibility.ELIGIBLE to "Hazır",
        PrerequisiteEligibility.CONDITIONAL_ELIGIBLE to "Koşullu hazır",
        PrerequisiteEligibility.ELIGIBLE_WITH_SUPPORT to "Destekle hazır",
        PrerequisiteEligibility.BLOCKED to "Ön koşul bekliyor",
        PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA to "Ön koşul bilgisi eksik",
    )
    val weaknessLabels: Map<WeaknessAxis, String> = mapOf(
        WeaknessAxis.NONE to "Yok",
        // A hypothesis is a question, never a deficiency (`SPWX-v0` §10.1).
        WeaknessAxis.HYPOTHESIS to "Açık bir soru var",
        WeaknessAxis.SUPPORTED to "Yerel bir sinyal var",
        WeaknessAxis.REMEDIATION_REQUIRED to "Pekiştirme gerekli",
        WeaknessAxis.RESOLVED to "Giderildi",
        WeaknessAxis.NOT_YET_EVALUATED to NOT_EVALUATED,
    )
    val objectiveWeaknessLabels: Map<WeaknessSignal, String> = mapOf(
        WeaknessSignal.HYPOTHESIS to "Açık bir soru var",
        WeaknessSignal.SUPPORTED to "Yerel bir sinyal var",
        WeaknessSignal.CONFIRMED to "Pekiştirme gerekli",
        WeaknessSignal.RESOLVED to "Giderildi (geçmiş)",
    )
    val evidenceLabels: Map<EvidenceKind, String> = mapOf(
        EvidenceKind.INDEPENDENT to "Bağımsız",
        EvidenceKind.ASSISTED to "Destekli",
        EvidenceKind.PROVISIONAL to "Kesinleşmemiş",
        EvidenceKind.NOT_COUNTED to "Sayılmayan (geçersiz, itirazlı ya da ön koşulu eksik)",
    )
    const val OBJECTIVE_SHOWN = "Bağımsız olarak gösterildi"
    const val OBJECTIVE_NOT_SHOWN = "Henüz gösterilmedi"
    const val OBJECTIVE_REQUIRED = "zorunlu"
    const val BLOCKED_BY = "Önce bunlar gerekiyor"
    const val UNCERTAIN = "Durumu kesin olmayan ön koşullar"
    const val SOFT_GAPS = "Önerilen ön koşullar"
    const val METADATA = "Ön koşul bilgisinde sorun var; tahmin edilmiyor"
    const val PREREQUISITES_CLEAR = "Ön koşulları çözülmüş."

    /** The id namespace of a Skill, for orientation only — never a domain score (`SPWX-v0` §7.3). */
    val trackLabels: Map<String, String> = mapOf(
        "computing" to "Bilgisayar",
        "programming" to "Programlama",
        "python" to "Python",
        "c" to "C",
        "memory" to "Bellek",
        "linux" to "Linux",
        "shell" to "Kabuk",
        "git" to "Git",
        "english" to "Teknik İngilizce",
    )
}

data class InventoryLine(val state: SkillPresentationState, val label: String, val count: Int)

data class AttentionLine(val group: AttentionGroup, val label: String, val skills: List<VersionedRef>) {
    val count: Int get() = skills.size
}

/** One Skill in a list: its chip and qualifiers, as text. A recomputing Skill has no state, only its status. */
data class SkillChip(
    val skill: VersionedRef,
    val name: String,
    val state: SkillPresentationState?,
    val label: String,
    val tone: Tone,
    val qualifiers: List<String>,
)

data class SkillGroup(val track: String, val label: String, val skills: List<SkillChip>)

data class ProgressOverview(
    val state: ProgressState,
    val text: String,
    val inventory: List<InventoryLine>,
    /** How many Skills are recomputing: the projection's status, never a ninth Skill state. */
    val recomputing: Int,
    val attention: List<AttentionLine>,
    val groups: List<SkillGroup>,
    /** Non-blocking context alongside a working core, such as AI being unavailable (`SPWX-v0` §16). */
    val contexts: List<ProgressState> = emptyList(),
)

data class AxisLine(val axis: UnevaluatedAxis, val title: String, val value: String)

data class EvidenceLine(val kind: EvidenceKind, val label: String, val count: Int)

data class ObjectiveLine(
    val objective: VersionedRef,
    val label: String,
    val required: Boolean,
    val shown: String,
    val weakness: String?,
    val evidence: List<EvidenceLine>,
)

data class PrerequisiteLine(val title: String, val skills: List<Pair<VersionedRef, String>>)

data class SkillDetailView(
    val skill: VersionedRef,
    val name: String,
    val statement: String,
    val chip: SkillChip,
    /** All four axes, always — the chip orders them and never replaces them (`SPWX-v0` §5). */
    val axes: List<AxisLine>,
    val objectives: List<ObjectiveLine>,
    val evidence: List<EvidenceLine>,
    val prerequisite: List<PrerequisiteLine>,
    val prerequisiteNote: String?,
    val topics: List<String>,
)

object ProgressPresentation {

    /**
     * The overview. [facts] is `null` while the store has not been read. A blocking health state supersedes
     * everything and shows nothing that is not true (`SPWX-v0` §16, `APHX-v0`).
     */
    fun overview(facts: ProgressFacts?, health: AppHealth): ProgressOverview {
        health.blocking?.let { blocking ->
            val state = when (blocking) {
                CrossCuttingState.DATA_RECOVERY_REQUIRED -> ProgressState.DATA_RECOVERY_REQUIRED
                CrossCuttingState.ERROR_RECOVERABLE -> ProgressState.ERROR_RECOVERABLE
                else -> ProgressState.LOADING_PROJECTION
            }
            return empty(state)
        }
        if (facts == null) return empty(ProgressState.LOADING_PROJECTION)
        if (!facts.curriculumLoaded) return empty(ProgressState.EMPTY_NO_EVIDENCE_YET).copy(text = ProgressCopy.NO_CURRICULUM)

        val chips = facts.skills.map(::chip)
        val derived = facts.skills.mapNotNull { row -> (row.projection as? SkillStateProjection.Derived)?.let { row to it } }
        val recomputing = facts.skills.size - derived.size
        val inventory = SkillPresentationState.precedence.mapNotNull { state ->
            val count = derived.count { it.second.state == state }
            if (count == 0) null else InventoryLine(state, ProgressCopy.stateLabels.getValue(state), count)
        }
        val attention = AttentionGroup.entries.mapNotNull { group ->
            val skills = derived.filter { (_, d) -> inGroup(group, d) }.map { it.first.skill }
            if (skills.isEmpty()) null else AttentionLine(group, ProgressCopy.attentionLabels.getValue(group), skills)
        }
        val evidenced = facts.skills.any { it.projection.reading.evidenced }
        val state = when {
            facts.skills.isNotEmpty() && recomputing == facts.skills.size -> ProgressState.RECOMPUTING_PROJECTION
            recomputing > 0 -> ProgressState.PARTIAL_PROJECTION_AVAILABLE
            !evidenced -> ProgressState.EMPTY_NO_EVIDENCE_YET
            attention.isEmpty() -> ProgressState.EMPTY_NO_ATTENTION_NEEDED
            else -> ProgressState.READY_WITH_EVIDENCE
        }
        val groups = chips.groupBy { track(it.skill) }.map { (track, list) ->
            SkillGroup(track, ProgressCopy.trackLabels[track] ?: track, list)
        }
        val contexts = if (CrossCuttingState.AI_UNAVAILABLE_CORE_AVAILABLE in health.contexts)
            listOf(ProgressState.AI_UNAVAILABLE_FULL_STATE_AVAILABLE) else emptyList()
        return ProgressOverview(state, ProgressCopy.stateTexts.getValue(state), inventory, recomputing, attention, groups, contexts)
    }

    /** `skill_detail` (`SPWX-v0` §8): the same chip the overview shows, and every axis behind it. */
    fun detail(facts: SkillDetailFacts): SkillDetailView {
        val row = facts.row
        val reading = row.projection.reading
        val axes = listOf(
            AxisLine(UnevaluatedAxis.MASTERY, ProgressCopy.axisTitles.getValue(UnevaluatedAxis.MASTERY),
                reading.mastery?.let(ProgressCopy.masteryLabels::getValue)
                    ?: if (reading.evidenced) ProgressCopy.NOT_EVALUATED else ProgressCopy.masteryLabels.getValue(MasteryAxisState.NOT_YET_EVIDENCED)),
            AxisLine(UnevaluatedAxis.RETENTION, ProgressCopy.axisTitles.getValue(UnevaluatedAxis.RETENTION),
                ProgressCopy.retentionLabels.getValue(reading.retention)),
            AxisLine(UnevaluatedAxis.PREREQUISITE, ProgressCopy.axisTitles.getValue(UnevaluatedAxis.PREREQUISITE),
                ProgressCopy.prerequisiteLabels.getValue(row.prerequisite.eligibility)),
            AxisLine(UnevaluatedAxis.WEAKNESS, ProgressCopy.axisTitles.getValue(UnevaluatedAxis.WEAKNESS),
                if (!reading.evidenced && reading.weakness == WeaknessAxis.NOT_YET_EVALUATED) ProgressCopy.weaknessLabels.getValue(WeaknessAxis.NONE)
                else ProgressCopy.weaknessLabels.getValue(reading.weakness)),
        )
        val objectives = facts.objectives.map { o ->
            ObjectiveLine(
                objective = o.objective,
                label = o.objective.logicalId.substringAfterLast('.').replace('_', ' '),
                required = o.required,
                shown = when (o.gatesPassed) {
                    true -> ProgressCopy.OBJECTIVE_SHOWN
                    false -> ProgressCopy.OBJECTIVE_NOT_SHOWN
                    null -> ProgressCopy.NOT_EVALUATED
                },
                weakness = o.weakness?.let(ProgressCopy.objectiveWeaknessLabels::get),
                evidence = evidenceLines(o.evidence),
            )
        }
        val context = row.prerequisite
        fun named(refs: List<VersionedRef>) = refs.map { it to (facts.skillNames[it] ?: it.logicalId) }
        val prerequisite = buildList {
            if (context.hardBlockers.isNotEmpty()) add(PrerequisiteLine(ProgressCopy.BLOCKED_BY, named(context.hardBlockers)))
            if (context.uncertain.isNotEmpty()) add(PrerequisiteLine(ProgressCopy.UNCERTAIN, named(context.uncertain)))
            if (context.softGaps.isNotEmpty()) add(PrerequisiteLine(ProgressCopy.SOFT_GAPS, named(context.softGaps)))
        }
        val note = when {
            context.metadataProblems.isNotEmpty() -> ProgressCopy.METADATA
            prerequisite.isEmpty() -> ProgressCopy.PREREQUISITES_CLEAR
            else -> null
        }
        return SkillDetailView(
            skill = row.skill,
            name = row.name,
            statement = facts.capabilityStatement,
            chip = chip(row),
            axes = axes,
            objectives = objectives,
            evidence = evidenceLines(facts.evidence),
            prerequisite = prerequisite,
            prerequisiteNote = note,
            topics = facts.topics.map { it.name },
        )
    }

    fun chip(row: SkillProgressRow): SkillChip = when (val p = row.projection) {
        is SkillStateProjection.Derived -> SkillChip(
            skill = row.skill,
            name = row.name,
            state = p.state,
            label = ProgressCopy.stateLabels.getValue(p.state),
            tone = p.state.tone.tone,
            qualifiers = p.qualifiers.map(ProgressCopy.qualifierLabels::getValue),
        )
        is SkillStateProjection.Recomputing -> SkillChip(
            skill = row.skill,
            name = row.name,
            state = null,
            label = ProgressCopy.RECOMPUTING,
            tone = ProgressState.RECOMPUTING_PROJECTION.tone,
            qualifiers = emptyList(),
        )
    }

    private fun inGroup(group: AttentionGroup, d: SkillStateProjection.Derived): Boolean = when (group) {
        AttentionGroup.REMEDIATION_OPEN -> d.state == SkillPresentationState.REMEDIATION_REQUIRED
        AttentionGroup.VERIFICATION_DUE -> d.state == SkillPresentationState.CONFIRMATION_VERIFICATION_DUE
        AttentionGroup.REVIEW_DUE -> d.state == SkillPresentationState.CONFIRMED_REVIEW_DUE
        AttentionGroup.RETENTION_AT_RISK -> AttentionQualifier.RETENTION_AT_RISK in d.qualifiers
        AttentionGroup.PREREQUISITE_BLOCKED -> d.state == SkillPresentationState.PREREQUISITE_UNRESOLVED
        AttentionGroup.ASSESSMENT_OPPORTUNITY -> false
    }

    private fun evidenceLines(summary: EvidenceSummary): List<EvidenceLine> =
        EvidenceKind.entries.reversed().map { kind ->
            EvidenceLine(kind, ProgressCopy.evidenceLabels.getValue(kind), summary.counts[kind] ?: 0)
        }

    private fun track(skill: VersionedRef): String = skill.logicalId.split('.').getOrElse(1) { skill.logicalId }

    private fun empty(state: ProgressState) =
        ProgressOverview(state, ProgressCopy.stateTexts.getValue(state), emptyList(), 0, emptyList(), emptyList())
}
