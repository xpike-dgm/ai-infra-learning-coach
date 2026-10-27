package coach.presentation

import coach.model.AiCall
import coach.model.AiCallOutcome
import coach.model.AiCallRecord
import coach.model.AiCallUse
import coach.model.BlockingScope
import coach.model.CandidateDisposition
import coach.model.CandidateTrace
import coach.model.CapacitySource
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.DailyCapacity
import coach.model.DayInventory
import coach.model.DayRecord
import coach.model.DayRecordKind
import coach.model.DecisionValue
import coach.model.DisplayPreference
import coach.model.DurationFit
import coach.model.EvaluatorAvailability
import coach.model.EvidenceKind
import coach.model.EvidenceOutcome
import coach.model.EvidenceSeverity
import coach.model.HistoryEntry
import coach.model.HistoryEventFamily
import coach.model.HistoryFacts
import coach.model.HistoryFamily
import coach.model.LifecycleStatus
import coach.model.NeedDisposition
import coach.model.NeedTrace
import coach.model.NeedTrigger
import coach.model.PlanTrace
import coach.model.PlannedEntry
import coach.model.PlannerExplanationFacts
import coach.model.PrerequisiteEligibility
import coach.model.PriorityBand
import coach.model.RankVector
import coach.model.StarvationBucket
import coach.model.StateChange
import coach.model.StateChangeKind
import coach.model.TaskPurpose
import coach.model.TemporalUrgency
import coach.model.StoreStatus
import coach.model.ThemeChoice
import coach.model.TrackBalance
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * 17E (`D-138`): the day summary says what the engines changed that day and nothing else; `skill_detail ->
 * planner_explanation` is the plan's own trace narrowed to one Skill; `technical_english_profile -> skill_detail` is an
 * accepted edge; AI use is counted with the provider's tokens and no money; the theme and motion setting only ever add.
 */
class RemainingUxTest {

    private val loops = VersionedRef("skill.python.loops", 1)
    private val paths = VersionedRef("skill.linux.paths", 1)
    private val lists = VersionedRef("skill.c.linked_list_insert", 1)
    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val healthy = AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.AVAILABLE)

    // ---------------------------------------------------------------- the day summary

    private fun entry(sequence: Long, day: String, skill: VersionedRef, vararg kinds: StateChangeKind) = HistoryEntry(
        sequence = sequence, studyDay = day, skill = skill, objective = VersionedRef("objective.$sequence", 1),
        family = HistoryFamily.LEARNING, kind = EvidenceKind.values().first(), outcome = EvidenceOutcome.POSITIVE,
        assistance = emptyList(), attemptId = sequence, assessmentSessionId = null,
        changes = kinds.map { StateChange(skill, it, "before", "after") },
    )

    private val record = DayRecord("2026-10-27", DayInventory(mapOf(DayRecordKind.ATTEMPTS_RECORDED to 3)))

    @Test
    fun `the day summary names each change an engine made that day, in 13E's sentence and by its Skill`() {
        val facts = HistoryFacts(
            curriculumLoaded = true,
            entries = listOf(
                entry(9, "2026-10-27", loops, StateChangeKind.CAPABILITY_CONFIRMED),
                entry(4, "2026-10-26", paths, StateChangeKind.RETENTION_REVALIDATED),
                entry(7, "2026-10-27", paths),
                entry(5, "2026-10-27", paths, StateChangeKind.WEAKNESS_QUESTION_OPENED),
            ),
            sessions = emptyList(),
            skillNames = mapOf(loops to "Döngüler"),
        )
        val changes = DayChanges.of(facts, "2026-10-27")
        assertEquals(listOf(paths, loops), changes.map { it.subject }, "in recording order, that day only, and only real changes")
        assertEquals("Döngüler: " + ProgramChangeCopy.stateTemplates.getValue(StateChangeKind.CAPABILITY_CONFIRMED), changes.last().statement)
        assertTrue(changes.first().statement.endsWith(ProgramChangeCopy.stateTemplates.getValue(StateChangeKind.WEAKNESS_QUESTION_OPENED)))
        assertEquals(listOf(HistoryEventFamily.LEARNING, HistoryEventFamily.LEARNING), changes.map { it.family })

        val view = EndOfDay.of(DayChanges.input(record, emptySet(), facts, healthy))
        assertEquals(DaySummaryState.READY_WITH_EVIDENCE, view.state)
        assertEquals(changes, view.changes)
    }

    @Test
    fun `activity without a change is still a day where nothing changed`() {
        val facts = HistoryFacts(true, listOf(entry(7, "2026-10-27", paths)), emptyList())
        val view = EndOfDay.of(DayChanges.input(record, emptySet(), facts, healthy))
        assertEquals(emptyList(), view.changes)
        assertFalse(DayChanges.UNREAD_KIND in view.unreadKinds)
    }

    @Test
    fun `changes that could not be read are named as unread, never reported as nothing changed`() {
        val view = EndOfDay.of(DayChanges.input(record, emptySet(), null, healthy))
        assertEquals(DaySummaryState.PARTIAL_PROJECTION_AVAILABLE, view.state)
        assertTrue(DayChanges.UNREAD_KIND in view.unreadKinds)
        assertEquals(emptyList(), view.changes)
    }

    // ---------------------------------------------------------------- the two edges

    @Test
    fun `the two edges are accepted, one way only`() {
        assertTrue(NavigationGraph.canOpen("skill_detail", "planner_explanation"))
        assertTrue(NavigationGraph.canOpen("technical_english_profile", "skill_detail"))
        assertFalse(NavigationGraph.canOpen("planner_explanation", "skill_detail"))
        assertFalse(NavigationGraph.canOpen("skill_detail", "technical_english_profile"))
    }

    private fun rank() = RankVector(
        BlockingScope.NON_BLOCKING, Criticality.REQUIRED, EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
        TemporalUrgency.NOT_TIME_SENSITIVE, StarvationBucket.NONE, ContinuationValue.FRESH_NEW_CONTEXT, DecisionValue.NONE,
        TrackBalance.NONE, DurationFit.FITS_REMAINING, "k")

    private fun need(key: String, trigger: NeedTrigger, skill: VersionedRef, disposition: NeedDisposition, final: List<String>) =
        NeedTrace(key, trigger, listOf(skill), emptyList(), PriorityBand.P3, rank(), listOf("priority.p3_planned_progress"),
            if (disposition == NeedDisposition.SELECTED) "c-$key" else null, disposition, final)

    private val trace = PlanTrace(
        generationKind = "initial", studyDay = "2026-10-27", curriculumVersion = 1, truthWatermark = 1,
        policyVersions = emptyMap(),
        capacity = DailyCapacity(CapacitySource.SELECTED_SHORT, 30, 27, reserveRelaxed = false, belowMinimumBlock = false),
        needs = listOf(
            need("new", NeedTrigger.NEW_LEARNING, loops, NeedDisposition.SELECTED, listOf("selection.selected")),
            need("review", NeedTrigger.RETENTION_REVIEW_DUE, paths, NeedDisposition.ELIGIBLE_NOT_SELECTED,
                listOf("selection.not_selected_capacity", "capacity.deferred_not_enough_time")),
            need("lists", NeedTrigger.NEW_LEARNING, lists, NeedDisposition.BLOCKED, listOf("selection.blocked_prerequisite")),
        ),
        candidates = listOf(
            CandidateTrace("c-lists", "lists", LifecycleStatus.VALIDATED, PrerequisiteEligibility.BLOCKED, 15,
                CandidateDisposition.BLOCKED_PREREQUISITE, listOf("eligibility.blocked_hard_prerequisite"), listOf(pointer)),
        ),
        selected = listOf(PlannedEntry(0, "c-new", "new", TaskPurpose.PRACTICE, "coding", "Döngü görevi", loops, null, 20, 20, false, false)),
        planReasonCodes = listOf("capacity.source_short_profile"),
        invariantChecks = linkedMapOf("hard_budget_not_exceeded" to true),
    )

    private val facts = PlannerExplanationFacts.Readable(trace, mapOf(loops to "Döngüler", paths to "Yollar", lists to "Bağlı liste", pointer to "İşaretçi"))

    @Test
    fun `a Skill's plan is the same explanation narrowed to its own needs and tasks, with nothing added`() {
        val whole = PlannerExplanationPresentation.of(facts)
        val planned = PlannerExplanationPresentation.forSkill(facts, loops, "Döngüler")
        assertEquals(whole.today.filter { it.skill.ref == loops }, planned.explanation.today)
        assertEquals(emptyList(), planned.explanation.notToday)
        assertEquals(emptyList(), planned.explanation.plan, "plan-wide reasons are not this Skill's")
        assertFalse(planned.notMentioned)

        val deferred = PlannerExplanationPresentation.forSkill(facts, paths, null).explanation
        assertEquals(emptyList(), deferred.today)
        assertEquals("capacity.deferred_not_enough_time", deferred.notToday.single().whyNot.code)

        // A waiting Skill names its real blocker, exactly as the whole plan does.
        val waiting = PlannerExplanationPresentation.forSkill(facts, lists, null).explanation.notToday.single()
        assertEquals(whole.notToday.single { lists in it.skills.map { s -> s.ref } }.whyNot, waiting.whyNot)

        // Every statement of a narrowed view is one the whole view already says.
        listOf(loops, paths, lists).forEach { skill ->
            assertTrue(whole.statements.containsAll(PlannerExplanationPresentation.forSkill(facts, skill, null).explanation.statements))
        }
    }

    @Test
    fun `a plan that recorded nothing about a Skill says so, and an unreadable plan is not explained`() {
        val unmentioned = PlannerExplanationPresentation.forSkill(facts, pointer, "İşaretçi")
        assertTrue(unmentioned.notMentioned)
        assertEquals("İşaretçi", unmentioned.skill.name)
        val noPlan = PlannerExplanationPresentation.forSkill(PlannerExplanationFacts.NoPlan, loops, "Döngüler")
        assertEquals(ExplanationState.NO_PLAN_YET, noPlan.explanation.state)
        assertFalse(noPlan.notMentioned, "no plan is not a plan that said nothing")
    }

    // ---------------------------------------------------------------- AI usage

    private fun call(use: AiCallUse, outcome: AiCallOutcome, day: String, input: Long?, output: Long?, sequence: Long) =
        AiCallRecord(AiCall(use, "gpt-6-astra", outcome, 1, input, output), day, sequence)

    @Test
    fun `AI use is counted per use with the provider's tokens, this month and in all, and never in money`() {
        val calls = listOf(
            call(AiCallUse.TUTOR_HELP, AiCallOutcome.ANSWERED, "2026-10-03", 1200, 300, 1),
            call(AiCallUse.TUTOR_HELP, AiCallOutcome.TIMED_OUT, "2026-10-20", null, null, 2),
            call(AiCallUse.OPEN_RESPONSE_EVALUATION, AiCallOutcome.ANSWERED, "2026-09-30", 2_500_000, 40, 3),
        )
        val view = AiUsagePresentation.of(calls, "2026-10-27", aiInBuild = true)
        assertEquals(listOf(AiUsageCopy.THIS_MONTH, AiUsageCopy.ALL), view.periods.map { it.title })
        val month = view.periods.first().lines.single()
        assertEquals(AiCallUse.TUTOR_HELP, month.use)
        assertEquals("2 çağrı: 1 yanıt geldi, 1 zaman aşımı", month.calls)
        assertEquals("1.200 giriş, 300 çıkış token · 1 çağrı token bildirmedi", month.tokens)
        assertEquals("2.500.000 giriş, 40 çıkış token", view.periods.last().lines.last().tokens)
        val said = (view.periods.flatMap { p -> p.lines.flatMap { listOf(it.calls, it.tokens.orEmpty()) } }).joinToString(" ")
        assertFalse(Regex("""[$€₺]|TL\b|USD|dolar|lira|tutar""").containsMatchIn(said), said)
        assertTrue(AiUsageCopy.NO_PRICE in view.notes)
    }

    @Test
    fun `no build, no calls and unreported tokens are each said as themselves`() {
        assertEquals(AiUsageCopy.NOT_IN_BUILD, AiUsagePresentation.of(emptyList(), "2026-10-27", aiInBuild = false).text)
        assertEquals(AiUsageCopy.NONE, AiUsagePresentation.of(emptyList(), "2026-10-27", aiInBuild = true).text)
        val only = AiUsagePresentation.of(listOf(call(AiCallUse.CONNECTION_CHECK, AiCallOutcome.TRANSPORT_ERROR, "2026-10-27", null, null, 1)), "2026-10-27", true)
        assertEquals("Sağlayıcı bu çağrılar için token bildirmedi.", only.periods.first().lines.single().tokens)
        assertEquals("1", AiUsagePresentation.number(1))
        assertEquals("12.345", AiUsagePresentation.number(12_345))
    }

    // ---------------------------------------------------------------- appearance

    @Test
    fun `the theme follows the system until chosen, and reduced motion only ever adds`() {
        assertTrue(DisplayPreference.SYSTEM_DEFAULT.dark(systemDark = true))
        assertFalse(DisplayPreference(ThemeChoice.LIGHT, false).dark(systemDark = true))
        assertTrue(DisplayPreference(ThemeChoice.DARK, false).dark(systemDark = false))
        assertTrue(ReducedMotion.of(1f, inApp = true))
        assertTrue(ReducedMotion.of(0f, inApp = false), "the learner cannot bring back motion Android removed")
        assertFalse(ReducedMotion.of(1f, inApp = false))
        assertEquals(AppearanceCopy.MOTION_SYSTEM, AppearancePresentation.of(DisplayPreference(ThemeChoice.SYSTEM, false), systemReducedMotion = true).motionNote)
        assertEquals(AppearanceCopy.MOTION_ON, AppearancePresentation.of(DisplayPreference(ThemeChoice.SYSTEM, true), systemReducedMotion = false).motionNote)
        assertEquals(ThemeChoice.entries, AppearancePresentation.of(DisplayPreference.SYSTEM_DEFAULT, false).themeOptions.map { it.first })
    }

    @Test
    fun `the focus ring colour is measured against both surfaces in both themes`() {
        Theme.entries.forEach { theme ->
            listOf("surface", "surface_variant").forEach { surface ->
                val ratio = Contrast.ratio(DesignTokens.hex(theme, "focus_ring"), DesignTokens.hex(theme, surface))
                assertTrue(ratio >= DesignTokens.NON_TEXT_CONTRAST_MIN, "$theme focus_ring on $surface: $ratio")
            }
        }
    }
}
