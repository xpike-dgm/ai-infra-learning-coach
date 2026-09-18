package coach.presentation

import coach.model.AssistanceLevel
import coach.model.AssistanceScope
import coach.model.AssistanceSource
import coach.model.AssistanceTiming
import coach.model.EvaluationResult
import coach.model.PendingReason
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * `TRUX-v0 / D-070` as tests. The rules worth breaking are the coercive and the untrue ones: help
 * withheld or forced, a solution revealed unasked, a consequence not disclosed, a stop that costs
 * something, a pending evaluation that passes or fails, a stale task that starts anyway.
 */
class TaskRunnerTest {

    @Test
    fun `the seventeen semantic states are TRUX-v0's, in its order`() {
        assertEquals(
            listOf(
                "entering_revalidating", "blocked_not_startable", "orientation", "active_work",
                "assistance_open", "submitting", "feedback_resolved", "evaluation_pending",
                "checkpoint_paused", "resume_revalidating", "resume_invalidated", "replan_interrupted",
                "stopped_no_penalty", "offline_local_capable", "ai_unavailable_deterministic_core",
                "error_recoverable", "data_recovery_required",
            ),
            RunnerState.entries.map { it.id },
        )
    }

    @Test
    fun `the lifecycle has six phases in order`() {
        assertEquals(
            listOf("enter", "orient", "work", "submit", "resolve", "transition"),
            RunnerPhase.entries.map { it.id },
        )
    }

    @Test
    fun `every runner state wears the tone VDSX-v0 assigned it`() {
        val accepted = mapOf(
            "entering_revalidating" to Tone.NEUTRAL, "blocked_not_startable" to Tone.ATTENTION,
            "orientation" to Tone.NEUTRAL, "active_work" to Tone.ACTIVE, "assistance_open" to Tone.ACTIVE,
            "submitting" to Tone.ACTIVE, "feedback_resolved" to Tone.NEUTRAL,
            "evaluation_pending" to Tone.PENDING_UNRESOLVED, "checkpoint_paused" to Tone.NEUTRAL,
            "resume_revalidating" to Tone.NEUTRAL, "resume_invalidated" to Tone.NEUTRAL,
            "replan_interrupted" to Tone.NEUTRAL, "stopped_no_penalty" to Tone.NEUTRAL,
            "offline_local_capable" to Tone.NEUTRAL, "ai_unavailable_deterministic_core" to Tone.NEUTRAL,
            "error_recoverable" to Tone.SYSTEM_FAULT, "data_recovery_required" to Tone.SYSTEM_FAULT,
        )
        assertEquals(accepted, RunnerState.entries.associate { it.id to it.tone })
    }

    // ---------------------------------------------------------------- entry and resume

    @Test
    fun `a task that still meets every entry condition may start`() {
        val result = RunnerRevalidation.atEntry(EntryCondition.entries.toSet())
        assertEquals(Revalidation.Startable, result)
        assertEquals(RunnerState.ORIENTATION, RunnerRevalidation.stateAtEntry(result))
    }

    @Test
    fun `any unmet entry condition blocks the start and names what changed`() {
        EntryCondition.entries.forEach { missing ->
            val result = RunnerRevalidation.atEntry(EntryCondition.entries.toSet() - missing)
            val blocked = assertIs<Revalidation.NotStartable>(result, "$missing did not block the start")
            assertEquals(setOf(missing.id), blocked.unmet)
            assertEquals(RunnerState.BLOCKED_NOT_STARTABLE, RunnerRevalidation.stateAtEntry(result))
        }
    }

    @Test
    fun `a stale paused task cannot resume and is offered no bypass`() {
        ResumeCondition.entries.forEach { missing ->
            val result = RunnerRevalidation.atResume(ResumeCondition.entries.toSet() - missing)
            assertIs<Revalidation.NotStartable>(result, "$missing let a stale task resume")
            assertEquals(RunnerState.RESUME_INVALIDATED, RunnerRevalidation.stateAtResume(result))
        }
        assertEquals(
            RunnerState.ACTIVE_WORK,
            RunnerRevalidation.stateAtResume(RunnerRevalidation.atResume(ResumeCondition.entries.toSet())),
        )
    }

    @Test
    fun `a failed revalidation carries no failure or evidence field`() {
        val fields = Revalidation.NotStartable::class.java.declaredFields.filterNot { it.isSynthetic }.map { it.name }
        assertEquals(listOf("unmet"), fields, "a blocked start must only name what is not confirmed")
    }

    @Test
    fun `nothing Today cannot confirm is assumed to hold at entry`() {
        val empty = TodayView(
            state = TodayState.EMPTY_NO_OPEN_NEED, primaryActionKind = PrimaryActionKind.VALID_EMPTY_OR_CAPACITY_LIMITED_STATE,
            primaryTask = null, resumable = null, capacity = null, remainingPlan = emptyList(),
            attention = emptyList(), contexts = emptyList(), supportingNavigation = emptyList(),
        )
        assertEquals(emptySet(), RunnerRevalidation.confirmedFromToday(empty))
        val withTask = empty.copy(
            primaryTask = TodayTaskRow(
                plannedTaskRef = 1, displayTitle = "Döngüler", primaryPurpose = coach.model.TaskPurpose.PRACTICE,
                targetSkillRefs = emptyList(), disposition = TaskDisposition.CURRENT, reason = null,
            )
        )
        val confirmed = RunnerRevalidation.confirmedFromToday(withTask)
        assertEquals(setOf(EntryCondition.PLANNED_TASK_STILL_SELECTED), confirmed)
        assertIs<Revalidation.NotStartable>(
            RunnerRevalidation.atEntry(confirmed),
            "a task started on prerequisites and content nothing has confirmed",
        )
    }

    // ---------------------------------------------------------------- assistance

    @Test
    fun `help at H1 and H2 is granted on request and recorded as requested`() {
        listOf(AssistanceLevel.H1, AssistanceLevel.H2).forEach { level ->
            val outcome = AssistancePolicy.request(
                level, AssistanceTiming.DURING_ATTEMPT, AssistanceScope.TARGET_OBJECTIVE,
                AssistanceSource.DETERMINISTIC_CONTENT, consequenceAcknowledged = false,
            )
            val granted = assertIs<AssistanceOutcome.Granted>(outcome)
            assertEquals(level, granted.event.level)
            assertTrue(granted.event.requestedByUser)
        }
    }

    @Test
    fun `H3 and H4 are never granted before the consequence is disclosed`() {
        listOf(AssistanceLevel.H3, AssistanceLevel.H4).forEach { level ->
            AssistanceScope.entries.forEach { scope ->
                val first = AssistancePolicy.request(
                    level, AssistanceTiming.DURING_ATTEMPT, scope,
                    AssistanceSource.DETERMINISTIC_CONTENT, consequenceAcknowledged = false,
                )
                assertIs<AssistanceOutcome.ConsequenceDisclosureRequired>(first, "$level/$scope skipped the disclosure")
                val second = AssistancePolicy.request(
                    level, AssistanceTiming.DURING_ATTEMPT, scope,
                    AssistanceSource.DETERMINISTIC_CONTENT, consequenceAcknowledged = true,
                )
                assertIs<AssistanceOutcome.Granted>(second, "$level/$scope was refused after acknowledgement")
            }
        }
    }

    @Test
    fun `an incorrect attempt reveals nothing on its own and leaves every level requestable`() {
        assertEquals(AssistanceLevel.entries, AssistancePolicy.afterIncorrectAttempt())
    }

    @Test
    fun `the consequence is framed as measurement, never as punishment`() {
        val punitive = Regex("ceza|cezalan|kaybed|başarısız|puan", RegexOption.IGNORE_CASE)
        assertFalse(punitive.containsMatchIn(RunnerCopy.CONSEQUENCE_DISCLOSURE))
        assertTrue("kanıtlayabileceğini değiştirir" in RunnerCopy.CONSEQUENCE_DISCLOSURE)
    }

    // ---------------------------------------------------------------- pause, stop, exit

    @Test
    fun `only durable pauses may be presented as saved progress`() {
        assertFalse(PauseClass.MID_SEGMENT_PAUSE.mayBePresentedAsSavedProgress)
        assertFalse(PauseClass.MID_SEGMENT_PAUSE.producesResumeContext)
        assertTrue(PauseClass.CHECKPOINT_PAUSE.mayBePresentedAsSavedProgress)
        assertTrue(PauseClass.HIGH_STAKES_PAUSE.durable)
    }

    @Test
    fun `stopping costs nothing and is always the same state`() {
        assertEquals(RunnerState.STOPPED_NO_PENALTY, RunnerFlow.stop())
        val coercive = Regex("borç oluşur|seri kırıl|kaybedeceksin|sadece .* dakika", RegexOption.IGNORE_CASE)
        assertFalse(coercive.containsMatchIn(RunnerCopy.STOPPED))
    }

    @Test
    fun `the exit is available from every state`() {
        assertTrue(RunnerState.entries.all { RunnerFlow.exitAvailable(it) })
    }

    @Test
    fun `the runner is a focused flow that suspends the shell`() {
        assertTrue(RunnerFlow.surface.isFocusedFlow)
        assertEquals("task_runner_flow", RunnerFlow.surface.id)
    }

    // ---------------------------------------------------------------- evaluation and transition

    @Test
    fun `a pending evaluation is its own state, never a pass or a fail`() {
        PendingReason.entries.forEach { reason ->
            assertEquals(
                RunnerState.EVALUATION_PENDING,
                RunnerFlow.afterEvaluation(EvaluationResult.EvaluationPending(reason)),
                "$reason was treated as a verdict",
            )
        }
    }

    @Test
    fun `the next task always comes from a recomputed planner selection`() {
        assertEquals(listOf(NextTaskSource.RECOMPUTED_PLANNER_SELECTION), NextTaskSource.entries)
        assertEquals(NextTaskSource.RECOMPUTED_PLANNER_SELECTION, RunnerFlow.nextTaskSource)
    }
}
