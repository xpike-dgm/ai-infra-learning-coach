package coach.presentation

import coach.model.AssistanceLevel
import coach.model.EvaluatorAvailability
import coach.model.EvidenceKind
import coach.model.EvidenceOutcome
import coach.model.HistoryEntry
import coach.model.HistoryFacts
import coach.model.HistoryFamily
import coach.model.NotMeasured
import coach.model.PlanChange
import coach.model.PlanChangeKind
import coach.model.SessionReport
import coach.model.StateChange
import coach.model.StateChangeKind
import coach.model.StoreStatus
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 16B (`LHRX-v0`): a history says what happened and what the engines say it changed, only on days something happened;
 * a report is a session's `ASUX-v0` families, never a grade, and says plainly when nothing changed — and never claims a
 * plan stayed the same when no plan written for the session was found.
 */
class HistoryPresentationTest {

    private val ready = AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.AVAILABLE)
    private val loops = VersionedRef("skill.python.loops", 1)
    private val objective = VersionedRef("objective.python.loops.trace_loop", 1)

    private fun entry(
        sequence: Long,
        day: String,
        kind: EvidenceKind = EvidenceKind.INDEPENDENT,
        outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE,
        family: HistoryFamily = HistoryFamily.LEARNING,
        changes: List<StateChangeKind> = emptyList(),
        session: Long? = null,
        assistance: List<AssistanceLevel> = emptyList(),
        contested: Boolean = false,
        solutionExposed: Boolean = false,
    ) = HistoryEntry(sequence, day, loops, objective, family, kind, outcome, assistance, null, session,
        changes.map { StateChange(loops, it, "a", "b") }, contested, solutionExposed)

    private fun facts(entries: List<HistoryEntry>, sessions: List<SessionReport> = emptyList()) =
        HistoryFacts(curriculumLoaded = true, entries = entries, sessions = sessions, skillNames = mapOf(loops to "Loops"))

    @Test
    fun `days appear only when something happened, newest first, and nothing marks a gap`() {
        val view = HistoryPresentation.history(facts(listOf(entry(1, "2026-10-01"), entry(2, "2026-10-09"))), ready)
        assertEquals(listOf("2026-10-09", "2026-10-01"), view.days.map { it.studyDay })
        assertEquals(ProgressState.READY_WITH_EVIDENCE, view.state)
    }

    @Test
    fun `a line says what happened and what the engines said it changed, in 13E's own words`() {
        val view = HistoryPresentation.history(facts(listOf(
            entry(1, "2026-10-01", kind = EvidenceKind.ASSISTED, assistance = listOf(AssistanceLevel.H3, AssistanceLevel.H1)),
            entry(2, "2026-10-01", changes = listOf(StateChangeKind.CAPABILITY_CONFIRMED)),
        )), ready)
        val (second, first) = view.days.single().lines
        assertEquals("Bağımsız · beklenen sonuç", second.happened)
        assertEquals(listOf(ProgramChangeCopy.stateTemplates.getValue(StateChangeKind.CAPABILITY_CONFIRMED)), second.changed)
        assertEquals("Destekli · beklenen sonuç · Açılan yardım: H1, H3", first.happened)
        assertEquals(listOf(HistoryCopy.NO_CHANGE), first.changed)
        assertEquals("Loops", first.skillName)
        // Narrowed at 17A: an Objective with no lesson title is named by its Skill, never by its identifier.
        assertEquals("Loops", first.objectiveLabel)
    }

    @Test
    fun `an empty history and an unread store are said, never shown as zero`() {
        assertEquals(HistoryCopy.HISTORY_EMPTY, HistoryPresentation.history(facts(emptyList()), ready).text)
        assertEquals(ProgressState.LOADING_PROJECTION, HistoryPresentation.history(null, ready).state)
        assertEquals(HistoryCopy.REPORT_EMPTY, HistoryPresentation.report(facts(emptyList()), ready).text)
        val recovery = AppHealth.of(StoreStatus.RecoverableFailure, EvaluatorAvailability.AVAILABLE)
        assertEquals(ProgressState.ERROR_RECOVERABLE, HistoryPresentation.report(facts(emptyList()), recovery).state)
    }

    private fun session(
        changes: List<StateChangeKind> = emptyList(),
        plan: List<PlanChange> = emptyList(),
        replanFound: Boolean = true,
        notMeasured: List<HistoryEntry> = emptyList(),
        unanswered: List<String> = emptyList(),
    ) = SessionReport(7, "weekly", "2026-10-09", emptyList(), changes.map { StateChange(loops, it, "a", "b") },
        NotMeasured(notMeasured, unanswered), plan, replanFound)

    @Test
    fun `a report holds its families in ASUX order, and a not-measured answer is named, never failed`() {
        val report = session(
            changes = listOf(StateChangeKind.VERIFICATION_OPENED, StateChangeKind.CAPABILITY_CONFIRMED),
            plan = listOf(PlanChange(PlanChangeKind.TASK_ADDED, "need.x", null, listOf(loops))),
            notMeasured = listOf(entry(3, "2026-10-09", kind = EvidenceKind.ASSISTED, solutionExposed = true),
                entry(4, "2026-10-09", kind = EvidenceKind.NOT_COUNTED, contested = true)),
            unanswered = listOf("slot.2"),
        )
        val view = HistoryPresentation.report(facts(emptyList(), listOf(report)), ready).sessions.single()
        assertEquals(listOf(ResultFamily.CONFIRMED_CAPABILITIES, ResultFamily.VERIFICATION_NEEDED, ResultFamily.NOT_RELIABLY_MEASURED, ResultFamily.PLAN_CHANGES),
            view.families.map { it.family })
        assertEquals(listOf("Loops: çözümü görülen deneme", "Loops: itirazlı soru", "yanıtlanmamış slot"),
            view.families.single { it.family == ResultFamily.NOT_RELIABLY_MEASURED }.statements)
        assertNull(view.summary)
        assertEquals("Haftalık ölçme", view.title)
    }

    @Test
    fun `nothing changed is said plainly, and an unfound plan is not claimed unchanged`() {
        val unchanged = HistoryPresentation.session(session(), facts(emptyList()))
        assertEquals(ProgramChangeCopy.NOTHING_CHANGED, unchanged.summary)
        val unknownPlan = HistoryPresentation.session(session(replanFound = false), facts(emptyList()))
        assertTrue(unknownPlan.summary!!.startsWith(HistoryCopy.STATE_UNCHANGED))
        assertFalse("planını değiştirmedi" in unknownPlan.summary!!, "a plan nobody found was claimed unchanged")
    }

    @Test
    fun `no string scores, ranks, streaks or blames`() {
        val texts = HistoryCopy.familyLabels.values + HistoryCopy.outcomeLabels.values + HistoryCopy.scopeLabels.values +
            HistoryCopy.reportFamilyLabels.values + HistoryCopy.notMeasuredLabels.values + listOf(
                HistoryCopy.NO_CHANGE, HistoryCopy.ASSISTANCE, HistoryCopy.HISTORY_EMPTY, HistoryCopy.REPORT_EMPTY,
                HistoryCopy.NO_CURRICULUM, HistoryCopy.NO_REPLAN_FOUND, HistoryCopy.STATE_UNCHANGED, HistoryCopy.UNANSWERED_SLOT)
        val forbidden = listOf("%", "puan", "skor", "not ortalaması", "seri", "streak", "kaçır", "başarısız", "yanlış", "geride", "borç", "gün üst üste")
        texts.forEach { text ->
            val lower = text.lowercase(java.util.Locale.ROOT)
            forbidden.forEach { assertFalse(it in lower, "'$text' says '$it'") }
            assertFalse(Regex("""\d+\s*/\s*\d+""").containsMatchIn(text), "'$text' is a ratio")
        }
    }

    @Test
    fun `no projected type holds a count, score or trend`() {
        listOf(HistoryLine::class, HistoryDay::class, LearningHistoryView::class, ReportFamilyBlock::class, SessionReportView::class,
            AssessmentReportView::class).forEach { type ->
            val members = type.java.declaredFields.map { it.name } + type.java.declaredMethods.map { it.name }
            members.forEach { member ->
                val name = member.lowercase(java.util.Locale.ROOT)
                listOf("score", "streak", "trend", "average", "percent", "grade", "total").forEach { assertFalse(it in name, "${type.simpleName}.$member") }
            }
        }
    }
}
