package coach.presentation

import coach.model.AssistanceLevel
import coach.model.EvidenceKind
import coach.model.EvidenceOutcome
import coach.model.HistoryEntry
import coach.model.HistoryFacts
import coach.model.HistoryFamily
import coach.model.SessionReport
import coach.model.VersionedRef

/**
 * `SPWX-v0 / D-072` §§12–13 as pure functions (16B, `LHRX-v0`): the learning history is a chronology of what happened
 * and what it changed; the assessment report is each session's `ASUX-v0` families across time.
 *
 * Neither owns anything. Every "what changed" sentence is 13E's own, chosen by the engines' replayed transition, so a
 * change no engine made cannot be said. Days appear only when something happened on them: a day without an entry is
 * not drawn, so nothing here can render a gap as a miss, a streak or attendance. A report holds no grade, score,
 * average or trend — a session is its families, and when nothing changed it says so plainly (`ASUX-v0` §13.4).
 */

/** Working Turkish microcopy (17 owns its polish); the change sentences are 13E's. */
object HistoryCopy {
    val familyLabels: Map<HistoryFamily, String> = mapOf(
        HistoryFamily.ASSESSMENT to "Ölçme",
        HistoryFamily.REMEDIATION to "Pekiştirme",
        HistoryFamily.REVIEW to "Tekrar",
        HistoryFamily.LEARNING to "Öğrenme",
    )
    /** What happened, never a verdict on the learner. */
    val outcomeLabels: Map<EvidenceOutcome, String> = mapOf(
        EvidenceOutcome.POSITIVE to "beklenen sonuç",
        EvidenceOutcome.NEGATIVE to "beklenen sonuç değil",
        EvidenceOutcome.PARTIAL to "kısmen",
        EvidenceOutcome.INVALID to "ölçülemedi",
    )
    const val NO_CHANGE = "Durum değişmedi."
    const val ASSISTANCE = "Açılan yardım"
    const val HISTORY_EMPTY = "Henüz kayıtlı bir öğrenme olayı yok."
    const val REPORT_EMPTY = "Henüz bir ölçme oturumu yok."
    const val NO_CURRICULUM = "Müfredat henüz yüklenmedi."
    const val NO_REPLAN_FOUND = "Bu oturumun ardından yazılmış bir plan sürümü bulunmadı; plan değişikliği söylenmiyor."
    const val STATE_UNCHANGED = "Bu oturum becerilerinin durumunu değiştirmedi."
    const val UNANSWERED_SLOT = "yanıtlanmamış slot"

    val scopeLabels: Map<String, String> = mapOf("daily" to "Günlük ölçme", "weekly" to "Haftalık ölçme", "monthly" to "Aylık ölçme")

    /** `ASUX-v0`'s family names, the same words the in-session result uses. */
    val reportFamilyLabels: Map<ResultFamily, String> = mapOf(
        ResultFamily.CONFIRMED_CAPABILITIES to "Doğrulanan yetkinlikler",
        ResultFamily.VERIFICATION_NEEDED to "Doğrulama gerekiyor",
        ResultFamily.PERSISTENT_TARGETED_GAPS to "Süren hedefli açıklar",
        ResultFamily.RETENTION_REVALIDATED to "Hatırlama tazelendi",
        ResultFamily.NOT_RELIABLY_MEASURED to "Güvenilir biçimde ölçülemeyenler",
        ResultFamily.PLAN_CHANGES to "Plan değişiklikleri",
    )
    val notMeasuredLabels: Map<NotReliablyMeasured, String> = mapOf(
        NotReliablyMeasured.INVALID_ITEM to "değerlendirilemeyen deneme",
        NotReliablyMeasured.PROVISIONAL_EVALUATION to "geçici değerlendirme",
        NotReliablyMeasured.ASSISTED_ATTEMPT to "yardımlı deneme",
        NotReliablyMeasured.SOLUTION_EXPOSED_ATTEMPT to "çözümü görülen deneme",
        NotReliablyMeasured.CONTESTED_ITEM to "itirazlı soru",
        NotReliablyMeasured.UNSUBMITTED_SLOT to "yanıtlanmamış slot",
    )
}

data class HistoryLine(
    val sequence: Long,
    val family: HistoryFamily,
    val familyLabel: String,
    val skill: VersionedRef,
    val skillName: String,
    val objectiveLabel: String,
    /** What happened: the kind of evidence, the outcome and any assistance recorded on the attempt. */
    val happened: String,
    /** What it changed, as 13E says it; [HistoryCopy.NO_CHANGE] when the engines changed nothing. */
    val changed: List<String>,
)

/** One study day that has entries. A day without any is not a line here, so it cannot be drawn as missed. */
data class HistoryDay(val studyDay: String, val lines: List<HistoryLine>)

data class LearningHistoryView(val state: ProgressState, val text: String, val days: List<HistoryDay>)

data class ReportFamilyBlock(val family: ResultFamily, val label: String, val statements: List<String>)

data class SessionReportView(
    val sessionId: Long,
    val title: String,
    val studyDay: String,
    /** Only the families this session filled, in `ASUX-v0`'s order; [summary] stands in when no state or plan changed. */
    val families: List<ReportFamilyBlock>,
    val summary: String?,
)

data class AssessmentReportView(val state: ProgressState, val text: String, val sessions: List<SessionReportView>)

object HistoryPresentation {

    fun history(facts: HistoryFacts?, health: AppHealth): LearningHistoryView {
        blocked(health)?.let { return LearningHistoryView(it, ProgressCopy.stateTexts.getValue(it), emptyList()) }
        if (facts == null) return LearningHistoryView(ProgressState.LOADING_PROJECTION, ProgressCopy.stateTexts.getValue(ProgressState.LOADING_PROJECTION), emptyList())
        if (!facts.curriculumLoaded) return LearningHistoryView(ProgressState.EMPTY_NO_EVIDENCE_YET, HistoryCopy.NO_CURRICULUM, emptyList())
        if (facts.entries.isEmpty()) return LearningHistoryView(ProgressState.EMPTY_NO_EVIDENCE_YET, HistoryCopy.HISTORY_EMPTY, emptyList())
        // Newest first, grouped by the rows' own study day; the day is never recomputed from an instant (11E).
        val days = facts.entries.sortedByDescending { it.sequence }.groupBy { it.studyDay.orEmpty() }
            .map { (day, entries) -> HistoryDay(day, entries.map { line(it, facts) }) }
        return LearningHistoryView(ProgressState.READY_WITH_EVIDENCE, ProgressCopy.stateTexts.getValue(ProgressState.READY_WITH_EVIDENCE), days)
    }

    fun report(facts: HistoryFacts?, health: AppHealth): AssessmentReportView {
        blocked(health)?.let { return AssessmentReportView(it, ProgressCopy.stateTexts.getValue(it), emptyList()) }
        if (facts == null) return AssessmentReportView(ProgressState.LOADING_PROJECTION, ProgressCopy.stateTexts.getValue(ProgressState.LOADING_PROJECTION), emptyList())
        if (!facts.curriculumLoaded) return AssessmentReportView(ProgressState.EMPTY_NO_EVIDENCE_YET, HistoryCopy.NO_CURRICULUM, emptyList())
        if (facts.sessions.isEmpty()) return AssessmentReportView(ProgressState.EMPTY_NO_EVIDENCE_YET, HistoryCopy.REPORT_EMPTY, emptyList())
        val sessions = facts.sessions.sortedByDescending { it.sessionId }.map { session(it, facts) }
        return AssessmentReportView(ProgressState.READY_WITH_EVIDENCE, ProgressCopy.stateTexts.getValue(ProgressState.READY_WITH_EVIDENCE), sessions)
    }

    fun line(entry: HistoryEntry, facts: HistoryFacts): HistoryLine {
        val happened = buildList {
            add(ProgressCopy.evidenceLabels.getValue(entry.kind))
            add(HistoryCopy.outcomeLabels.getValue(entry.outcome))
            if (entry.assistance.isNotEmpty()) add("${HistoryCopy.ASSISTANCE}: ${entry.assistance.sortedBy(AssistanceLevel::ordinal).joinToString(", ") { it.id }}")
        }.joinToString(" · ")
        val name = { ref: VersionedRef -> facts.skillNames[ref] ?: ref.logicalId }
        return HistoryLine(
            sequence = entry.sequence,
            family = entry.family,
            familyLabel = HistoryCopy.familyLabels.getValue(entry.family),
            skill = entry.skill,
            skillName = name(entry.skill),
            objectiveLabel = entry.objective.logicalId.substringAfterLast('.').replace('_', ' '),
            happened = happened,
            changed = entry.changes.map { ProgramChangeCopy.stateTemplates.getValue(it.kind) }.ifEmpty { listOf(HistoryCopy.NO_CHANGE) },
        )
    }

    fun session(report: SessionReport, facts: HistoryFacts): SessionReportView {
        val name = { ref: VersionedRef -> facts.skillNames[ref] ?: ref.logicalId }
        val blocks = linkedMapOf<ResultFamily, MutableList<String>>()
        report.stateChanges.forEach { change ->
            blocks.getOrPut(ResultFamily.entries.single { it.id == change.kind.family }) { mutableListOf() } +=
                "${name(change.skill)}: ${ProgramChangeCopy.stateTemplates.getValue(change.kind)}"
        }
        // `ASUX-v0` §13.3: always shown when non-empty, never folded into a failure.
        val notMeasured = report.notMeasured.evidence.map { entry ->
            "${name(entry.skill)}: ${HistoryCopy.notMeasuredLabels.getValue(reason(entry))}"
        } + report.notMeasured.unansweredSlots.map { HistoryCopy.notMeasuredLabels.getValue(NotReliablyMeasured.UNSUBMITTED_SLOT) }
        if (notMeasured.isNotEmpty()) blocks.getOrPut(ResultFamily.NOT_RELIABLY_MEASURED) { mutableListOf() } += notMeasured
        val plan = report.planChanges.map { change ->
            (if (change.skills.isEmpty()) "" else change.skills.joinToString(", ", postfix = ": ") { name(it) }) + ProgramChangeCopy.planTemplate(change)
        }
        if (plan.isNotEmpty()) blocks[ResultFamily.PLAN_CHANGES] = plan.toMutableList()
        val ordered = ResultFamily.entries.mapNotNull { family ->
            blocks[family]?.let { ReportFamilyBlock(family, HistoryCopy.reportFamilyLabels.getValue(family), it) }
        }
        val summary = when {
            report.stateChanges.isNotEmpty() || report.planChanges.isNotEmpty() -> null
            // Without the plan version this session's recompute wrote, "the plan did not change" is not known.
            !report.replanFound -> HistoryCopy.STATE_UNCHANGED + " " + HistoryCopy.NO_REPLAN_FOUND
            else -> ProgramChangeCopy.NOTHING_CHANGED
        }
        return SessionReportView(
            sessionId = report.sessionId,
            title = HistoryCopy.scopeLabels[report.scope] ?: report.scope,
            studyDay = report.studyDay,
            families = ordered,
            summary = summary,
        )
    }

    /** `ASUX-v0` §13.3's reason for a row that did not count independently, from the row's own facts. */
    fun reason(entry: HistoryEntry): NotReliablyMeasured = when (entry.kind) {
        EvidenceKind.NOT_COUNTED -> if (entry.contested) NotReliablyMeasured.CONTESTED_ITEM else NotReliablyMeasured.INVALID_ITEM
        EvidenceKind.PROVISIONAL -> NotReliablyMeasured.PROVISIONAL_EVALUATION
        EvidenceKind.ASSISTED -> if (entry.solutionExposed) NotReliablyMeasured.SOLUTION_EXPOSED_ATTEMPT else NotReliablyMeasured.ASSISTED_ATTEMPT
        EvidenceKind.INDEPENDENT -> error("an independent row is measured")
    }

    private fun blocked(health: AppHealth): ProgressState? = when (health.blocking) {
        null -> null
        CrossCuttingState.DATA_RECOVERY_REQUIRED -> ProgressState.DATA_RECOVERY_REQUIRED
        CrossCuttingState.ERROR_RECOVERABLE -> ProgressState.ERROR_RECOVERABLE
        else -> ProgressState.LOADING_PROJECTION
    }

}
