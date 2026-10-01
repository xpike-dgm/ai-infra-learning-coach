package coach.presentation

import coach.engines.virtual.VirtualUsers
import coach.engines.virtual.VirtualUsers.FastPath
import coach.engines.virtual.VirtualUsers.needKey
import coach.model.CoverageWaiver
import coach.model.DiagnosticCodes
import coach.model.DiagnosticObjectiveState
import coach.model.DiagnosticResult
import coach.model.DiagnosticScope
import coach.model.DiagnosticSource
import coach.model.DiagnosticStage
import coach.model.DiagnosticTarget
import coach.model.NeedTrigger
import coach.model.ObjectiveDiagnosis
import coach.model.PlannerExplanationFacts
import coach.model.ProgramChangeReport
import coach.model.StateChange
import coach.model.StateChangeKind
import coach.model.VersionedRef
import coach.model.WaiverOutcome
import coach.model.recordedReasonCodes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * A diagnostic's result and its consequences as the learner reads them (13F; `PDT-v0` invariant 12): only a waived
 * Objective is said to be skipped, an Objective not shown goes back to normal learning without being called a failure
 * or a weakness, help is never a penalty, and nothing says a Skill or a Topic is learned.
 */
class DiagnosticPresentationTest {

    private val skill = FastPath.skill
    private val names = mapOf(
        FastPath.addressVsValue to "Adres ve değer", FastPath.declaration to "Pointer tanımlama",
        FastPath.dereference to "Dereference", FastPath.writeThrough to "Pointer ile yazma", skill to "Pointer temelleri",
    )
    private val label: (VersionedRef) -> String = { names.getValue(it) }

    private fun diagnosis(objective: VersionedRef, state: DiagnosticObjectiveState, stage: DiagnosticStage? = null) = ObjectiveDiagnosis(
        target = DiagnosticTarget(objective, skill, critical = false), state = state, stage = stage, failedGates = emptyList(),
        windowVariantFamilies = emptyList(), windowDependencyGroups = emptyList(), reasonCodes = emptyList(),
        waiver = if (state == DiagnosticObjectiveState.WAIVED) CoverageWaiver(objective, skill, 4, listOf(1, 2), 2, "2026-10-01") else null,
    )

    private fun result(diagnoses: List<ObjectiveDiagnosis>, outcome: WaiverOutcome, inProgress: Boolean) = DiagnosticResult(
        sessionId = 4,
        scope = DiagnosticScope(DiagnosticSource.USER_REQUESTED_FAST_PATH, "2026-10-01", 1, diagnoses.map { it.target }),
        objectives = diagnoses, outcome = outcome, inProgress = inProgress, reasonCodes = emptyList(),
    )

    /** S06 after the diagnostic: O1 and O2 shown, O3 missed, O4 still being checked. */
    private val s06 = result(listOf(
        diagnosis(FastPath.addressVsValue, DiagnosticObjectiveState.WAIVED),
        diagnosis(FastPath.declaration, DiagnosticObjectiveState.WAIVED),
        diagnosis(FastPath.dereference, DiagnosticObjectiveState.NOT_DEMONSTRATED),
        diagnosis(FastPath.writeThrough, DiagnosticObjectiveState.CONFIRM_NEEDED, DiagnosticStage.CONFIRM),
    ), WaiverOutcome.PARTIAL, inProgress = true)

    private val forbidden = listOf("%", "puan", "not:", "geçti", "kaldı", "başarısız", "eksik", "zayıf", "ceza aldın", "öğrendin", "ustalaştın")

    @Test
    fun `S06 the result says only the validated parts are skipped`() {
        val summary = DiagnosticResults.summary(s06, label)
        assertEquals(DiagnosticCopy.IN_PROGRESS_SOME_SKIPPED, summary.headline)
        val skipped = summary.lines.filter { "atlanıyor" in it }
        assertEquals(listOf("Adres ve değer", "Pointer tanımlama"), skipped.map { it.substringBefore(":") })
        assertTrue(summary.lines.single { it.startsWith("Dereference") }.contains("normal öğrenme akışında"))
        assertTrue(summary.lines.single { it.startsWith("Pointer ile yazma") }.contains("doğrulama"))
    }

    @Test
    fun `every outcome reads as coverage, never as a grade, a failure or learned`() {
        val full = result(listOf(diagnosis(FastPath.addressVsValue, DiagnosticObjectiveState.WAIVED)), WaiverOutcome.FULL, false)
        val none = result(listOf(diagnosis(FastPath.dereference, DiagnosticObjectiveState.NOT_DEMONSTRATED)), WaiverOutcome.NONE, false)
        val starting = result(listOf(diagnosis(FastPath.dereference, DiagnosticObjectiveState.PROBE_NEEDED, DiagnosticStage.PROBE)), WaiverOutcome.NONE, true)
        val partial = s06.copy(inProgress = false)
        assertEquals(DiagnosticCopy.FULL, DiagnosticCopy.headline(full))
        assertEquals(DiagnosticCopy.NONE, DiagnosticCopy.headline(none))
        assertEquals(DiagnosticCopy.IN_PROGRESS_NOTHING_YET, DiagnosticCopy.headline(starting))
        assertEquals(DiagnosticCopy.PARTIAL, DiagnosticCopy.headline(partial))
        val all = listOf(full, none, starting, partial, s06).flatMap { DiagnosticResults.summary(it, label).let { s -> s.lines + s.headline } } +
            DiagnosticObjectiveState.entries.flatMap { state -> DiagnosticStage.entries.map { DiagnosticCopy.line(state, it) } }
        // Turkish casing, never the default locale's (`AMTS-v0`).
        all.forEach { text -> forbidden.forEach { assertFalse(it in text.lowercase(java.util.Locale.forLanguageTag("tr")), "'$it' in: $text") } }
        all.forEach { assertFalse(Regex("\\d").containsMatchIn(it), "a number in: $it") }
    }

    @Test
    fun `help taken is never a penalty, and a stage is said as what is checked next`() {
        val assisted = DiagnosticCopy.line(DiagnosticObjectiveState.ASSISTANCE_ENDED_FAST_PATH, null)
        assertTrue("cezalandırılmaz" in assisted, assisted)
        assertTrue("kritik" in DiagnosticCopy.line(DiagnosticObjectiveState.CONFIRM_NEEDED, DiagnosticStage.CRITICAL_CONFIRM))
        assertTrue("yeni bir bağlamda" in DiagnosticCopy.line(DiagnosticObjectiveState.CONFIRM_NEEDED, DiagnosticStage.TRANSFER_CONFIRM))
        assertFalse("atlanıyor" in DiagnosticCopy.line(DiagnosticObjectiveState.ALREADY_DEMONSTRATED, null))
    }

    @Test
    fun `a waiver change names the Objective, sits in its family, and is never called mastery`() {
        val report = ProgramChangeReport(1, 2, listOf(
            StateChange(skill, StateChangeKind.COVERAGE_WAIVED, "none", "active", objective = FastPath.addressVsValue),
            StateChange(skill, StateChangeKind.COVERAGE_WAIVER_WITHDRAWN, "active", "none", objective = FastPath.declaration),
        ), emptyList(), emptyList(), emptyList(), firstPlan = false)
        val families = ProgramChangeResults.canonicalChanges(report, label)
        val confirmed = families.getValue(ResultFamily.CONFIRMED_CAPABILITIES).single()
        assertTrue(confirmed.startsWith("Adres ve değer:"), confirmed)
        assertTrue("atlanacak" in confirmed)
        assertFalse("Pointer temelleri" in confirmed)
        val unreliable = families.getValue(ResultFamily.NOT_RELIABLY_MEASURED).single()
        assertTrue(unreliable.startsWith("Pointer tanımlama:"), unreliable)
        (families.values.flatten()).forEach { text -> listOf("öğrendin", "ustalaştın", "başarısız", "%").forEach { assertFalse(it in text, text) } }
    }

    @Test
    fun `S06 the plan's explanation says only the validated parts are skipped, from the trace alone`() {
        val trace = VirtualUsers.s06().plan()
        val view = PlannerExplanationPresentation.of(PlannerExplanationFacts.Readable(trace, names))
        assertEquals(ExplanationState.EXPLAINED, view.state)
        val lesson = view.today.single()
        assertEquals("need.continue_learning_active", lesson.why.code)
        assertTrue(lesson.supporting.any { it.code == DiagnosticCodes.PARTIAL_COVERAGE_WAIVER }, lesson.supporting.toString())
        assertTrue(view.statements.none { it.code == DiagnosticCodes.FULL_COVERAGE_WAIVER })
        val recorded = trace.recordedReasonCodes()
        view.statements.forEach { s -> s.code?.let { assertTrue(it in recorded, "$it was never recorded") } }
        val text = ExplanationCopy.text(lesson.supporting.single { it.code == DiagnosticCodes.PARTIAL_COVERAGE_WAIVER })
        assertTrue("gösterilmeyen kısımlar normal akışta kalıyor" in text, text)
    }

    @Test
    fun `a need whose lessons wait for the diagnostic is not called a need with no task`() {
        val fast = FastPath
        // Every lesson of the need is still being checked: it waits for the diagnostic, it is not unserved.
        val trace = VirtualUsers.s06(stillChecking = setOf(fast.dereference, fast.writeThrough)).plan()
        val view = PlannerExplanationPresentation.of(PlannerExplanationFacts.Readable(trace, names))
        val waiting = view.notToday.single { needKey(NeedTrigger.CONTINUE_LEARNING, skill) in it.needKeys }
        assertEquals(DiagnosticCodes.USER_REQUESTED_FAST_PATH, waiting.whyNot.code)
        assertEquals(Reconsideration.NEXT_PLAN, waiting.reconsideration)
        assertTrue(view.notToday.none { it.whyNot.fact == TraceFact.NO_TASK_FOR_NEED })
    }
}
