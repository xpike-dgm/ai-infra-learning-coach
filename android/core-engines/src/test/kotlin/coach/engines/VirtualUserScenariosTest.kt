package coach.engines

import coach.engines.virtual.VirtualUsers
import coach.engines.virtual.VirtualUsers.englishErrors
import coach.engines.virtual.VirtualUsers.needKey
import coach.engines.virtual.VirtualUsers.pointer
import coach.model.CandidateDisposition
import coach.model.NeedDisposition
import coach.model.NeedTrigger
import coach.model.PlanTrace
import coach.model.PriorityBand
import coach.model.ReasonCatalog
import coach.model.TaskPurpose
import coach.model.recordedReasonCodes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * `docs/PLANNER_SIMULATION_SUITE.md` (3H) against the real gate and the real planner (12F).
 *
 * 3H passed these at policy level, by reasoning. Here the virtual users are state and the engines
 * decide; each test asserts what 3H said the outcome must be, and nothing about how the engine got
 * there. Store-level journeys (re-entry, replans, reading the plan) are in core-application, and the
 * explanations of these same plans are in core-presentation.
 */
class VirtualUserScenariosTest {

    private fun PlanTrace.need(key: String) = needs.single { it.needKey == key }
    private fun PlanTrace.candidate(id: String) = candidates.single { it.candidateId == id }
    private fun PlanTrace.selectedIds() = selected.map { it.candidateId }
    private val PlanTrace.plannedMinutes get() = selected.sumOf { it.plannedMinutes }

    @Test
    fun `S01 a normal day takes new learning and the parallel track and defers what does not fit`() {
        val plan = VirtualUsers.s01().plan()
        assertEquals(listOf("c-arrays", "english"), plan.selectedIds())
        assertEquals(54, plan.capacity.planningBudgetMinutes)
        val integration = plan.need(needKey(NeedTrigger.INTEGRATION_OPPORTUNITY, VirtualUsers.integrationProject))
        assertEquals(PriorityBand.P4, integration.band)
        assertEquals(NeedDisposition.ELIGIBLE_NOT_SELECTED, integration.disposition)
        assertEquals(listOf("selection.not_selected_capacity", "capacity.deferred_not_enough_time"), integration.finalReasonCodes)
        // Every selected task is tied to an open need and a real selection reason.
        plan.selected.forEach { entry ->
            val need = plan.need(entry.needKey)
            assertEquals(entry.candidateId, need.selectedCandidateId)
            assertTrue("selection.selected" in need.finalReasonCodes)
        }
    }

    @Test
    fun `S02 an open critical verification holds only the branch that depends on it`() {
        val plan = VirtualUsers.s02().plan()
        assertEquals(listOf("pointer-verify", "english"), plan.selectedIds())
        val verify = plan.need(needKey(NeedTrigger.VERIFICATION_DUE, pointer))
        assertEquals(PriorityBand.P0, verify.band)
        assertTrue("priority.p0_integrity_blocker" in verify.priorityReasonCodes)
        assertTrue("priority.blocks_next_ready_dependency" in verify.priorityReasonCodes)
        val linked = plan.need(needKey(NeedTrigger.NEW_LEARNING, VirtualUsers.linkedList))
        assertEquals(NeedDisposition.BLOCKED, linked.disposition)
        assertEquals(listOf("selection.blocked_prerequisite"), linked.finalReasonCodes)
        val linkedTask = plan.candidate("linked-list")
        assertEquals(listOf("eligibility.blocked_critical_verification"), linkedTask.reasonCodes)
        assertEquals(listOf(pointer), linkedTask.relatedSkills, "the blocker is the exact Skill ref (invariant 6)")
        assertTrue("independent_branch_available" in plan.planReasonCodes, "the independent branch went ahead (invariant 13)")
        assertEquals(PriorityBand.P3, plan.need(needKey(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors)).band)
    }

    @Test
    fun `S03 review due does not lock the dependent branch`() {
        val plan = VirtualUsers.s03().plan()
        assertEquals(listOf("pointer-review", "linked-list"), plan.selectedIds())
        assertEquals(PriorityBand.P2, plan.need(needKey(NeedTrigger.RETENTION_REVIEW_DUE, pointer)).band)
        val linked = plan.candidate("linked-list")
        assertEquals(CandidateDisposition.SELECTED, linked.disposition)
        assertTrue("eligibility.ready_due_allowed" in linked.reasonCodes, "review due is ready_due, not a block (invariant 19)")
        assertEquals(listOf(pointer), linked.relatedSkills)
        assertTrue(plan.needs.none { it.disposition == NeedDisposition.BLOCKED })
    }

    @Test
    fun `S04 a higher priority that does not fit is deferred for time, never called lower priority`() {
        val plan = VirtualUsers.s04().plan()
        assertEquals(12, plan.capacity.planningBudgetMinutes)
        assertEquals(listOf("english"), plan.selectedIds())
        val repair = plan.need(needKey(NeedTrigger.REMEDIATION_REQUIRED, VirtualUsers.cFunctions))
        assertEquals(PriorityBand.P1, repair.band)
        assertEquals(NeedDisposition.ELIGIBLE_NOT_SELECTED, repair.disposition)
        assertEquals(listOf("selection.not_selected_capacity", "capacity.deferred_not_enough_time"), repair.finalReasonCodes)
        assertFalse(plan.recordedReasonCodes().contains("selection.not_selected_lower_priority"), "invariant 10")
        val english = plan.need(needKey(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors))
        assertTrue("capacity.selected_within_budget" in english.finalReasonCodes)
        assertTrue(repair.band < english.band, "the repair stays the higher band; only time kept it out")
    }

    @Test
    fun `S05 an 8-minute day fits a micro-task, teaches nothing new and is not a failure`() {
        val plan = VirtualUsers.s05().plan()
        assertEquals(8, plan.capacity.hardBudgetMinutes)
        assertTrue(plan.capacity.reserveRelaxed && plan.capacity.belowMinimumBlock)
        assertEquals(listOf("loops-retrieval"), plan.selectedIds())
        assertTrue(plan.plannedMinutes <= 8)
        assertTrue(plan.selected.none { it.purpose == TaskPurpose.TEACH })
        val lesson = plan.need(needKey(NeedTrigger.NEW_LEARNING, VirtualUsers.cArrays))
        assertEquals(NeedDisposition.ELIGIBLE_NOT_SELECTED, lesson.disposition)
        assertTrue("capacity.reserve_relaxed_for_microtask" in plan.planReasonCodes)
        assertTrue(plan.invariantChecks.values.all { it }, plan.invariantChecks.toString())
        // A lesson short enough to fit is still not taught in an 8-minute day (D-033 §5).
        val withShortLesson = VirtualUsers.s05(shortLesson = true).plan()
        assertEquals(listOf("loops-retrieval"), withShortLesson.selectedIds())
        assertEquals(CandidateDisposition.ELIGIBLE_CAPACITY_DEFERRED, withShortLesson.candidate("arrays-micro-lesson").disposition)
    }

    @Test
    fun `S07 eighty due Skills are an inventory, not eighty tasks, and the day stays within its budget`() {
        val plan = VirtualUsers.s07().plan()
        assertEquals(45, plan.capacity.planningBudgetMinutes)
        assertEquals(listOf("pointer-verify", "functions-review", "arrays-lesson"), plan.selectedIds())
        assertEquals(40, plan.plannedMinutes)
        assertEquals(PriorityBand.P0, plan.need(needKey(NeedTrigger.VERIFICATION_DUE, pointer)).band)
        assertEquals(PriorityBand.P2, plan.need(needKey(NeedTrigger.RETENTION_REVIEW_DUE, VirtualUsers.cFunctions)).band)
        // English (10) does not fit the remaining 5 and is deferred for time — not debt.
        val english = plan.need(needKey(NeedTrigger.PARALLEL_TRACK_DUE, englishErrors))
        assertEquals(NeedDisposition.ELIGIBLE_NOT_SELECTED, english.disposition)
        // The 78 other due Skills are open needs with no task: not failures, not tasks, nothing invented.
        val reviews = plan.needs.filter { it.trigger == NeedTrigger.RETENTION_REVIEW_DUE && it.targetSkills.single() != VirtualUsers.cFunctions }
        assertEquals(78, reviews.size)
        assertTrue(reviews.all { it.disposition == NeedDisposition.NO_VALID_CANDIDATE && it.finalReasonCodes.isEmpty() })
        assertTrue(reviews.none { it.band < PriorityBand.P2 }, "review due is never P0/P1 because of absence")
    }

    @Test
    fun `S07 when every due review has a task, urgency fills the rest of the day and new learning waits for time`() {
        // 3H's example day holds new C learning. With every due Skill carrying an authored review, PBR-v0's
        // temporal urgency ranks those reviews ahead of new learning in P3, so the rest of the day goes to
        // them; SRR-v0 §15 lets new learning in only while capacity remains after that order. The guard
        // against review-only return days is starvation/track balance, whose thresholds are 18C's.
        val plan = VirtualUsers.s07(reviewsHaveTasks = true).plan()
        assertEquals(listOf("pointer-verify", "functions-review"), plan.selectedIds().take(2))
        assertTrue(plan.plannedMinutes <= 45)
        assertTrue(plan.selected.size < 80, "the due inventory is not the plan")
        val lesson = plan.need(needKey(NeedTrigger.NEW_LEARNING, VirtualUsers.cArrays))
        assertEquals(NeedDisposition.ELIGIBLE_NOT_SELECTED, lesson.disposition)
        assertEquals(listOf("selection.not_selected_capacity", "capacity.deferred_not_enough_time"), lesson.finalReasonCodes)
        assertTrue(plan.needs.none { it.rank.starvation != coach.model.StarvationBucket.NONE }, "absence feeds no starvation")
    }

    @Test
    fun `S11 priority cannot rescue an untrusted high-stakes task`() {
        val withAlternative = VirtualUsers.s11(withTrustedAlternative = true).plan()
        assertEquals(listOf("b-trusted"), withAlternative.selectedIds())
        assertEquals(CandidateDisposition.INVALID_CANDIDATE, withAlternative.candidate("a-untrusted").disposition)
        assertEquals(listOf("candidate.untrusted_for_high_stakes_use"), withAlternative.candidate("a-untrusted").reasonCodes)

        // A critical verification that holds nothing back is repair work, not an integrity blocker (invariant 18).
        assertEquals(PriorityBand.P1, withAlternative.need(needKey(NeedTrigger.VERIFICATION_DUE, pointer)).band)
        assertFalse(withAlternative.recordedReasonCodes().contains("priority.p0_integrity_blocker"))

        val alone = VirtualUsers.s11(withTrustedAlternative = false).plan()
        assertTrue(alone.selected.isEmpty())
        val need = alone.need(needKey(NeedTrigger.VERIFICATION_DUE, pointer))
        assertEquals(NeedDisposition.NO_VALID_CANDIDATE, need.disposition)
        assertEquals(listOf("selection.invalid_candidate"), need.finalReasonCodes)
    }

    @Test
    fun `S12 one need gets one task and the other alternative is superseded`() {
        val plan = VirtualUsers.s12().plan()
        assertEquals(1, plan.selected.size)
        val other = plan.candidates.single { it.candidateId != plan.selected.single().candidateId }
        assertEquals(CandidateDisposition.SUPERSEDED_SAME_NEED_ALTERNATIVE, other.disposition)
        assertTrue(plan.invariantChecks.getValue("one_task_per_need"))
    }

    @Test
    fun `S13 a critical label alone is not an integrity blocker`() {
        val plan = VirtualUsers.s13().plan()
        assertTrue(plan.needs.none { it.band == PriorityBand.P0 })
        assertFalse(plan.recordedReasonCodes().contains("priority.p0_integrity_blocker"))
        assertEquals(PriorityBand.P3, plan.need(needKey(NeedTrigger.CONTINUE_LEARNING, VirtualUsers.cArrays)).band)
        assertEquals(PriorityBand.P4, plan.need(needKey(NeedTrigger.REINFORCEMENT_OPPORTUNITY, pointer)).band)
    }

    @Test
    fun `S14 the same virtual user planned twice gives the same plan and the same trace`() {
        VirtualUsers.all().forEach { scenario ->
            assertEquals(scenario.plan(), scenario.plan(), "${scenario.id} is not deterministic")
        }
    }

    // ------------------------------------------------------------------------------------ PDT-v0 §23

    @Test
    fun `across every virtual user, nothing blocked or invalid is selected and the hard budget holds`() {
        VirtualUsers.all().forEach { scenario ->
            val plan = scenario.plan()
            val selected = plan.selectedIds().toSet()
            val unusable = plan.candidates.filter {
                it.disposition == CandidateDisposition.BLOCKED_PREREQUISITE || it.disposition == CandidateDisposition.INVALID_CANDIDATE
            }
            assertTrue(unusable.none { it.candidateId in selected }, "${scenario.id}: invariant 2")
            assertTrue(plan.plannedMinutes <= plan.capacity.hardBudgetMinutes, "${scenario.id}: invariant 3")
            assertEquals(plan.selected.map { it.needKey }.toSet().size, plan.selected.size, "${scenario.id}: invariant 11")
            assertTrue(plan.invariantChecks.values.all { it }, "${scenario.id}: ${plan.invariantChecks}")
        }
    }

    @Test
    fun `across every virtual user, every need has a disposition and every code is a contract code`() {
        VirtualUsers.all().forEach { scenario ->
            val plan = scenario.plan()
            assertEquals(scenario.needs.map { it.needKey }.toSet(), plan.needs.map { it.needKey }.toSet(), "${scenario.id}: invariant 5")
            plan.recordedReasonCodes().forEach { assertTrue(ReasonCatalog.isKnown(it), "${scenario.id}: $it") }
            // A deferred need is never tomorrow's debt: nothing in the trace carries a date or a carry-over.
            assertTrue(plan.recordedReasonCodes().none { "debt" in it && !it.startsWith("reentry.") }, "${scenario.id}: invariant 20")
        }
    }

    @Test
    fun `the trace stays bounded by the current state, not by history`() {
        // Invariant 17, structurally: at most five candidates per need are read, and the trace grows with
        // today's needs and candidates only. Runtime latency and memory are 18E's, on the device.
        VirtualUsers.all().forEach { scenario ->
            val plan = scenario.plan()
            val perNeed = plan.candidates.groupingBy { it.needKey }.eachCount()
            assertTrue(perNeed.values.all { it <= PlannerEngine.MAX_CANDIDATES_PER_NEED_V0 }, scenario.id)
            assertTrue(plan.needs.size == scenario.needs.size && plan.candidates.size <= scenario.candidates.size, scenario.id)
        }
        assertNull(VirtualUsers.s07(otherDueReviews = 0).plan().reentry, "a plan is not re-entry by itself")
    }
}
