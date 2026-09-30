package coach.engines

import coach.model.CapacitySource
import coach.model.CheckpointKind
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.DailyCapacity
import coach.model.GenerationKind
import coach.model.LearningNeed
import coach.model.MasteryAxisState
import coach.model.NeedTrigger
import coach.model.PlanTrace
import coach.model.PlannedEntry
import coach.model.ReplanRecord
import coach.model.ReplanTrigger
import coach.model.ResumeContext
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.StarvationBucket
import coach.model.TaskPurpose
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * `PDT-v0` §15, D-033 §8 and `SRR-v0` as code: a plan is replaced by a new version, what was started is
 * kept, only the rest is solved again, and a return after absence is neither debt nor failure.
 */
class ReplanEngineTest {

    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val linux = VersionedRef("skill.linux.filesystem_navigation", 1)
    private val english = VersionedRef("skill.english.present_simple", 1)

    private fun entry(position: Int, needKey: String, minutes: Int, skill: VersionedRef = pointer) =
        PlannedEntry(position, "c$position", needKey, TaskPurpose.PRACTICE, "coding", "Task $position", skill, null,
            minutes, minutes, split = false)

    private fun trace(
        selected: List<PlannedEntry> = emptyList(),
        hard: Int = 60,
        day: String = "2026-09-28",
        replan: ReplanRecord? = null,
    ) = PlanTrace("initial", day, 1, 10, emptyMap(), DailyCapacity(CapacitySource.NORMAL_PROFILE, hard, hard * 9 / 10, false, false),
        emptyList(), emptyList(), selected, listOf("capacity.source_normal_profile"), mapOf("one_task_per_need" to true), replan = replan)

    private fun need(trigger: NeedTrigger, skill: VersionedRef) =
        LearningNeed("${trigger.id}:$skill", trigger, listOf(skill), Criticality.REQUIRED)

    private fun pause(needKey: String, kind: CheckpointKind = CheckpointKind.CHECKPOINT_PAUSE, checkpoint: String = "cp1") =
        ResumeContext(kind, needKey, "task-1", checkpoint, listOf("s1"), listOf("s2"))

    @Test
    fun `a plan from another study day is never today's plan`() {
        assertEquals(GenerationKind.INITIAL, ReplanEngine.classify(null, "2026-09-28"))
        assertEquals(GenerationKind.REPLAN, ReplanEngine.classify("2026-09-28", "2026-09-28"))
        assertEquals(GenerationKind.REENTRY, ReplanEngine.classify("2026-09-27", "2026-09-28"))
        assertEquals(GenerationKind.REENTRY, ReplanEngine.classify("2026-07-01", "2026-09-28"))
    }

    @Test
    fun `only the remainder is solved again, and replanning never grows the day by itself`() {
        val previous = trace(hard = 60)
        // Any other event keeps the day's budget; what was kept comes off the top.
        assertEquals(40, ReplanEngine.remainderMinutes(ReplanTrigger.NEW_EVIDENCE_RECORDED, previous, 20, 90, null))
        // A new capacity for today replaces the day's budget.
        assertEquals(10, ReplanEngine.remainderMinutes(ReplanTrigger.TODAY_CAPACITY_CHANGED, previous, 20, 30, null))
        // A smaller capacity never undoes kept work: the remainder is empty, not negative.
        assertEquals(0, ReplanEngine.remainderMinutes(ReplanTrigger.TODAY_CAPACITY_CHANGED, previous, 20, 10, null))
        // A declared remaining time is the remainder itself.
        assertEquals(15, ReplanEngine.remainderMinutes(ReplanTrigger.SESSION_REMAINING_TIME_CHANGED, previous, 20, 90, 15))
        assertEquals(25, ReplanEngine.remainderMinutes(ReplanTrigger.USER_REQUESTED_EXTRA_TIME, previous, 20, 90, 25))
        assertFailsWith<IllegalArgumentException> {
            ReplanEngine.remainderMinutes(ReplanTrigger.SESSION_REMAINING_TIME_CHANGED, previous, 20, 90, null)
        }
        // A chain of replans keeps the learner's day: a version made after keeping 20 minutes of a
        // 60-minute day still counts those 20.
        val second = trace(hard = 40, replan = ReplanRecord(ReplanTrigger.TASK_COMPLETED, 1, "2026-09-28", listOf(0), emptyList(), 20, 40))
        assertEquals(60, ReplanEngine.dayHardBudget(second))
        assertEquals(30, ReplanEngine.remainderMinutes(ReplanTrigger.TASK_FINISHED_EARLY, second, 30, 90, null))
    }

    @Test
    fun `the remainder follows the same capacity rule as a whole day`() {
        val previous = trace(hard = 60)
        val remainder = ReplanEngine.remainderCapacity(ReplanTrigger.NEW_EVIDENCE_RECORDED, previous, 30)
        assertEquals(27, remainder.planningBudgetMinutes)
        assertEquals(CapacitySource.NORMAL_PROFILE, remainder.source)
        val small = ReplanEngine.remainderCapacity(ReplanTrigger.TODAY_CAPACITY_CHANGED, previous, 6)
        assertEquals(CapacitySource.TODAY_OVERRIDE, small.source)
        assertTrue(small.belowMinimumBlock && small.reserveRelaxed)
    }

    @Test
    fun `a safe pause makes continuation paused work, and a high-stakes pause is not resumed`() {
        val continuing = need(NeedTrigger.CONTINUE_LEARNING, pointer)
        val verification = need(NeedTrigger.VERIFICATION_DUE, linux)
        val newLearning = need(NeedTrigger.NEW_LEARNING, english)
        val result = ReplanEngine.withPausedWork(
            listOf(continuing, verification, newLearning),
            listOf(
                pause(continuing.needKey, checkpoint = "old"),
                pause(continuing.needKey, checkpoint = "latest"),
                pause(verification.needKey, CheckpointKind.HIGH_STAKES_PAUSE),
                pause("continue_learning:skill.gone@v1"),
            ),
        )
        val byKey = result.needs.associateBy { it.needKey }
        assertEquals(ContinuationValue.PAUSED_SAFE_CHECKPOINT, byKey.getValue(continuing.needKey).continuation)
        assertEquals(ContinuationValue.FRESH_NEW_CONTEXT, byKey.getValue(verification.needKey).continuation)
        assertEquals(newLearning, byKey.getValue(newLearning.needKey))
        assertEquals(listOf(continuing.needKey), result.continuedNeedKeys)
        assertEquals(1, result.highStakesNotResumed)
        // A pause for a need that is no longer open opens nothing: state has moved on.
        assertEquals(3, result.needs.size)
        // The latest pause for a need is the one that counts: a safe pause later superseded by a
        // high-stakes one is not continued.
        val superseded = ReplanEngine.withPausedWork(listOf(continuing),
            listOf(pause(continuing.needKey), pause(continuing.needKey, CheckpointKind.HIGH_STAKES_PAUSE)))
        assertEquals(ContinuationValue.FRESH_NEW_CONTEXT, superseded.needs.single().continuation)
        assertEquals(1, superseded.highStakesNotResumed)
        // And a paused need is still only a candidate: it becomes P2, never an automatic first task.
        assertEquals(coach.model.PriorityBand.P2,
            PlannerEngine.band(byKey.getValue(continuing.needKey), coach.model.BlockingScope.NON_BLOCKING, StarvationBucket.NONE))
    }

    @Test
    fun `a need a kept task already serves is not served twice`() {
        val a = need(NeedTrigger.CONTINUE_LEARNING, pointer)
        val b = need(NeedTrigger.NEW_LEARNING, linux)
        assertEquals(listOf(b), ReplanEngine.unservedNeeds(listOf(a, b), listOf(entry(0, a.needKey, 20))))
    }

    @Test
    fun `a replan keeps started work first and solves only the rest`() {
        val previous = trace(listOf(entry(0, "n0", 20), entry(1, "n1", 15), entry(2, "n2", 10)))
        val fresh = trace(listOf(entry(0, "n3", 10, linux), entry(1, "n4", 5, english)), hard = 25)
        val replanned = ReplanEngine.composeReplan(fresh, previous, 7, ReplanTrigger.SESSION_REMAINING_TIME_CHANGED, listOf(0), 45)

        assertEquals(GenerationKind.REPLAN.id, replanned.generationKind)
        assertEquals(listOf("c0", "c0", "c1"), replanned.selected.map { it.candidateId })
        assertEquals(listOf(0, 1, 2), replanned.selected.map { it.position })
        assertEquals(listOf(true, false, false), replanned.selected.map { it.preserved })
        val record = replanned.replan!!
        assertEquals(listOf(0), record.preservedPositions)
        assertEquals(listOf(1, 2), record.invalidatedPositions)
        assertEquals(20, record.preservedMinutes)
        assertEquals(25, record.remainderHardMinutes)
        assertEquals(7, record.previousPlanVersionId)
        assertEquals("replan.remaining_time_changed", replanned.planReasonCodes.first())
        assertTrue(replanned.invariantChecks.getValue("day_within_hard_budget"))
        assertTrue(replanned.invariantChecks.getValue("kept_tasks_unchanged"))
        assertNull(replanned.reentry)
        // The day's check is real: kept work plus the remainder over the budget says so.
        val over = ReplanEngine.composeReplan(fresh, previous, 7, ReplanTrigger.TASK_COMPLETED, listOf(0), 30)
        assertEquals(false, over.invariantChecks.getValue("day_within_hard_budget"))
    }

    @Test
    fun `an event PDT-v0 has no code for gets none`() {
        val replanned = ReplanEngine.composeReplan(trace(), trace(), 1, ReplanTrigger.TASK_COMPLETED, emptyList(), 60)
        assertEquals(trace().planReasonCodes, replanned.planReasonCodes)
        assertNull(ReplanTrigger.TASK_COMPLETED.reasonCode)
    }

    @Test
    fun `re-entry does not replay the old plan and records what the return looked like`() {
        val fresh = trace(listOf(entry(0, "n0", 20)), day = "2026-09-28")
        val paused = ReplanEngine.Paused(emptyList(), listOf("continue_learning:$pointer"), 1)
        val states = listOf(
            SkillPlanningState(pointer, "published", false, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE, null),
            SkillPlanningState(linux, "published", false, MasteryAxisState.CONFIRMED_CURRENT, RetentionAxis.REVIEW_DUE, null),
            SkillPlanningState(english, "published", false, null, RetentionAxis.NOT_YET_EVALUATED, null),
        )
        val back = ReplanEngine.composeReentry(fresh, 7, "2026-08-29", 6, paused, states)

        assertEquals(GenerationKind.REENTRY.id, back.generationKind)
        assertEquals(fresh.selected, back.selected)
        val context = back.reentry!!
        assertEquals(30, context.absenceStudyDays)
        assertEquals(6, context.stalePlannedTaskCount)
        assertEquals(mapOf("review_due" to 2), context.dueSkillCountByRetention)
        assertEquals(1, context.highStakesPausesNotResumed)
        assertTrue(back.planReasonCodes.containsAll(listOf(
            "replan.return_after_absence", "reentry.absence_not_failure", "reentry.absence_not_task_debt",
            "reentry.stale_plan_not_replayed", "reentry.current_state_regenerated", "reentry.paused_checkpoint_candidate",
            "reentry.incomplete_high_stakes_attempt_not_scored", "reentry.due_inventory_not_daily_plan",
        )))
        assertNull(back.replan)
    }

    @Test
    fun `absence is only recorded, never turned into pressure`() {
        assertEquals(1, ReplanEngine.absenceStudyDays("2026-09-27", "2026-09-28"))
        assertEquals(0, ReplanEngine.absenceStudyDays("not-a-day", "2026-09-28"))
        // SRR-v0 §11: nothing about the return reaches starvation.
        val back = ReplanEngine.composeReentry(trace(), 1, "2026-06-01", 0, ReplanEngine.Paused(emptyList(), emptyList(), 0), emptyList())
        assertTrue(back.needs.all { it.rank.starvation == StarvationBucket.NONE })
        assertTrue("reentry.paused_checkpoint_candidate" !in back.planReasonCodes)
    }
}
