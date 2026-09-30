package coach.presentation

import coach.model.CapacityContext
import coach.model.EvaluatorAvailability
import coach.model.PlanSnapshot
import coach.model.PlannedTaskFact
import coach.model.ReasonFamily
import coach.model.TaskPurpose
import coach.model.RecoveryReason
import coach.model.StoreStatus
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * `THUX-v0 / D-069` as tests: Today is a projection, not a second planner.
 *
 * The rules worth breaking are the ones about truthfulness — a stale plan shown as today's, a
 * blocked task offered as startable, a reason the planner never produced — so each of those is
 * exercised directly rather than inferred from a happy path.
 */
class TodayPresentationTest {

    private val today = "2026-09-17"
    private val healthy = AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.UNAVAILABLE)
    private val skill = VersionedRef("skill.python.loops", 1)

    private fun task(
        ref: Long,
        blocked: Boolean = false,
        traceFacts: List<ReasonFamily> = listOf(ReasonFamily.CONTINUE_CURRENT_LEARNING),
        purpose: TaskPurpose = TaskPurpose.PRACTICE,
    ) = PlannedTaskFact(
        plannedTaskId = ref,
        displayTitle = "Task $ref",
        primaryPurpose = purpose,
        targetSkillRefs = listOf(skill),
        traceFacts = traceFacts,
        blocked = blocked,
        estimatedMinutes = 20,
    )

    private fun input(
        plan: PlanSnapshot? = null,
        health: AppHealth = healthy,
        resumable: ResumableSession? = null,
        capacity: CapacityContext? = CapacityContext(resolvedDailyMinutes = 45),
        curriculumLoaded: Boolean = true,
        replanInFlight: Boolean = false,
        attention: List<AttentionItem> = emptyList(),
    ) = TodayInput(
        health = health,
        studyDay = today,
        plan = plan,
        resumable = resumable,
        capacity = capacity,
        curriculumLoaded = curriculumLoaded,
        replanInFlight = replanInFlight,
        attention = attention,
    )

    private fun plan(vararg tasks: PlannedTaskFact, studyDay: String = today) =
        PlanSnapshot(planVersionId = 1, policyVersion = "pdt-v0", studyDay = studyDay, tasks = tasks.toList())

    // ---------------------------------------------------------------- vocabulary

    @Test
    fun `the twelve semantic states are THUX-v0's, in its order`() {
        assertEquals(
            listOf(
                "loading_initial_plan", "replanning", "ready_plan", "resumable_session",
                "empty_no_open_need", "empty_no_eligible_task", "capacity_zero",
                "capacity_too_small_no_candidate", "offline_local_available",
                "ai_unavailable_core_available", "error_recoverable", "data_recovery_required",
            ),
            TodayState.entries.map { it.id },
        )
    }

    @Test
    fun `the primary action precedence is THUX-v0's, in its order`() {
        assertEquals(
            listOf(
                "data_recovery_required", "valid_resumable_focused_session", "selected_next_planned_task",
                "plan_loading_or_replanning", "valid_empty_or_capacity_limited_state", "recoverable_error",
            ),
            PrimaryActionKind.entries.map { it.id },
        )
    }

    @Test
    fun `purposes are the canonical seven and English is not one of them`() {
        assertEquals(
            listOf("teach", "practice", "assess", "remediate", "retain", "diagnose", "reinforce"),
            TaskPurpose.entries.map { it.id },
        )
        assertFalse(TaskPurpose.entries.any { it.id.contains("english") })
    }

    @Test
    fun `every Today state wears the tone VDSX-v0 assigned it`() {
        val accepted = mapOf(
            TodayState.LOADING_INITIAL_PLAN to Tone.NEUTRAL,
            TodayState.REPLANNING to Tone.NEUTRAL,
            TodayState.READY_PLAN to Tone.NEUTRAL,
            TodayState.RESUMABLE_SESSION to Tone.ACTIVE,
            TodayState.EMPTY_NO_OPEN_NEED to Tone.NEUTRAL,
            TodayState.EMPTY_NO_ELIGIBLE_TASK to Tone.NEUTRAL,
            TodayState.CAPACITY_ZERO to Tone.NEUTRAL,
            TodayState.CAPACITY_TOO_SMALL_NO_CANDIDATE to Tone.NEUTRAL,
            TodayState.OFFLINE_LOCAL_AVAILABLE to Tone.NEUTRAL,
            TodayState.AI_UNAVAILABLE_CORE_AVAILABLE to Tone.NEUTRAL,
            TodayState.ERROR_RECOVERABLE to Tone.SYSTEM_FAULT,
            TodayState.DATA_RECOVERY_REQUIRED to Tone.SYSTEM_FAULT,
        )
        assertEquals(accepted, TodayState.entries.associateWith { it.tone })
        // Only the two system conditions may look alarming; an empty or capacity-limited day is not
        // a fault and waiting is not a failure.
        assertEquals(
            listOf(TodayState.ERROR_RECOVERABLE, TodayState.DATA_RECOVERY_REQUIRED),
            TodayState.entries.filter { it.tone == Tone.SYSTEM_FAULT },
        )
    }

    // ---------------------------------------------------------------- precedence

    @Test
    fun `data recovery supersedes a perfectly good plan and renders none of it`() {
        val recovering = AppHealth.of(
            StoreStatus.RecoveryRequired(RecoveryReason.INTEGRITY_CHECK_FAILED),
            EvaluatorAvailability.UNAVAILABLE,
        )
        val view = TodayPresentation.of(input(plan = plan(task(1)), health = recovering))
        assertEquals(TodayState.DATA_RECOVERY_REQUIRED, view.state)
        assertEquals(PrimaryActionKind.DATA_RECOVERY_REQUIRED, view.primaryActionKind)
        assertNull(view.primaryTask, "a task was offered while the store needs recovery")
        assertTrue(view.remainingPlan.isEmpty(), "plan rows were rendered from an unusable store")
    }

    @Test
    fun `a revalidated resumable session outranks starting new work`() {
        val session = ResumableSession(Surface.TaskRunnerFlow, "Yarım kalan çalışma", revalidated = true)
        val view = TodayPresentation.of(input(plan = plan(task(1), task(2)), resumable = session))
        assertEquals(TodayState.RESUMABLE_SESSION, view.state)
        assertEquals(PrimaryActionKind.VALID_RESUMABLE_FOCUSED_SESSION, view.primaryActionKind)
        assertEquals(session, view.resumable)
        assertNull(view.primaryTask)
        assertEquals(listOf(1L, 2L), view.remainingPlan.map { it.plannedTaskRef })
    }

    @Test
    fun `a session that has not been revalidated cannot bypass the current plan`() {
        val stale = ResumableSession(Surface.TaskRunnerFlow, "Eski oturum", revalidated = false)
        val view = TodayPresentation.of(input(plan = plan(task(1)), resumable = stale))
        assertEquals(TodayState.READY_PLAN, view.state)
        assertNull(view.resumable, "a stale session was offered for resume")
    }

    @Test
    fun `a selected planned task becomes the dominant action and the rest become the queue`() {
        val view = TodayPresentation.of(input(plan = plan(task(1), task(2), task(3))))
        assertEquals(TodayState.READY_PLAN, view.state)
        assertEquals(PrimaryActionKind.SELECTED_NEXT_PLANNED_TASK, view.primaryActionKind)
        assertEquals(1L, assertNotNull(view.primaryTask).plannedTaskRef)
        assertEquals(TaskDisposition.CURRENT, view.primaryTask?.disposition)
        assertEquals(listOf(2L, 3L), view.remainingPlan.map { it.plannedTaskRef })
        assertTrue(view.remainingPlan.all { it.disposition == TaskDisposition.UPCOMING })
    }

    // ---------------------------------------------------------------- truthfulness

    @Test
    fun `yesterday's plan is never shown as today's`() {
        val view = TodayPresentation.of(input(plan = plan(task(1), task(2), studyDay = "2026-09-16")))
        assertEquals(TodayState.LOADING_INITIAL_PLAN, view.state)
        assertNull(view.primaryTask, "a stale plan was offered as today's action")
        assertTrue(view.remainingPlan.isEmpty(), "SRR-v0 forbids replaying an old plan as backlog")
    }

    @Test
    fun `a blocked task is never actionable and never fills the queue`() {
        val view = TodayPresentation.of(input(plan = plan(task(1, blocked = true), task(2))))
        assertEquals(2L, view.primaryTask?.plannedTaskRef, "a blocked task became the primary action")
        assertTrue(view.remainingPlan.none { it.plannedTaskRef == 1L }, "a blocked task was listed")
    }

    @Test
    fun `a plan whose every task is blocked is an empty state, not a blocked task list`() {
        val view = TodayPresentation.of(input(plan = plan(task(1, blocked = true), task(2, blocked = true))))
        assertEquals(TodayState.EMPTY_NO_ELIGIBLE_TASK, view.state)
        assertEquals(PrimaryActionKind.VALID_EMPTY_OR_CAPACITY_LIMITED_STATE, view.primaryActionKind)
        assertTrue(view.remainingPlan.isEmpty())
    }

    @Test
    fun `replanning never exposes the plan it is replacing`() {
        val view = TodayPresentation.of(input(plan = plan(task(1)), replanInFlight = true))
        assertEquals(TodayState.REPLANNING, view.state)
        assertNull(view.primaryTask)
        assertTrue(view.remainingPlan.isEmpty(), "a stale row survived a replan")
    }

    // ---------------------------------------------------------------- reasons

    @Test
    fun `a reason cannot be invented, only taken from the planner's trace facts`() {
        val withoutTrace = TodayPresentation.of(input(plan = plan(task(1, traceFacts = emptyList()))))
        assertNull(withoutTrace.primaryTask?.reason, "a reason appeared with no trace fact behind it")

        val withTrace = TodayPresentation.of(
            input(plan = plan(task(1, traceFacts = listOf(ReasonFamily.REVIEW_DUE_KNOWLEDGE))))
        )
        assertEquals(ReasonFamily.REVIEW_DUE_KNOWLEDGE, withTrace.primaryTask?.reason?.primary)
    }

    @Test
    fun `the overview shows one primary reason and at most one supporting reason`() {
        val many = listOf(
            ReasonFamily.CONTINUE_CURRENT_LEARNING,
            ReasonFamily.FIT_AVAILABLE_CAPACITY,
            ReasonFamily.PARALLEL_TECHNICAL_ENGLISH,
            ReasonFamily.REVIEW_DUE_KNOWLEDGE,
        )
        val reason = assertNotNull(ReasonSummary.fromTraceFacts(many))
        assertEquals(ReasonFamily.CONTINUE_CURRENT_LEARNING, reason.primary)
        assertEquals(ReasonFamily.FIT_AVAILABLE_CAPACITY, reason.supporting)
    }

    @Test
    fun `no reason carries free text that an AI could fill`() {
        val fields = ReasonSummary::class.java.declaredFields.filterNot { it.isSynthetic }
        assertTrue(
            fields.none { it.type == String::class.java },
            "a String on the reason type is where a free-form AI justification would live: ${fields.map { it.name }}",
        )
    }

    // ---------------------------------------------------------------- empty and capacity

    @Test
    fun `zero capacity is a legitimate state and not a failure`() {
        val view = TodayPresentation.of(input(plan = null, capacity = CapacityContext(resolvedDailyMinutes = 0)))
        assertEquals(TodayState.CAPACITY_ZERO, view.state)
        assertEquals(PrimaryActionKind.VALID_EMPTY_OR_CAPACITY_LIMITED_STATE, view.primaryActionKind)
    }

    @Test
    fun `capacity too small for any candidate is reported by the planner, never derived here`() {
        val reported = CapacityContext(
            resolvedDailyMinutes = 5,
            estimatedTotalPlannedMinutes = 90,
            tooSmallForAnyCandidate = true,
        )
        assertEquals(
            TodayState.CAPACITY_TOO_SMALL_NO_CANDIDATE,
            TodayPresentation.of(input(plan = null, capacity = reported)).state,
        )
        // The same two estimates without the planner's finding must not produce the same claim —
        // checked with a plan present, so the state selection really reaches the capacity branch.
        val unreported = reported.copy(tooSmallForAnyCandidate = false)
        assertEquals(
            TodayState.EMPTY_NO_OPEN_NEED,
            TodayPresentation.of(input(plan = plan(), capacity = unreported)).state,
            "Today derived a capacity verdict the planner never reported",
        )
    }

    @Test
    fun `a fresh install says nothing is published yet and claims no mastery`() {
        val view = TodayPresentation.of(input(plan = null, curriculumLoaded = false, capacity = null))
        assertEquals(TodayState.EMPTY_NO_OPEN_NEED, view.state)
        assertTrue(
            CrossCuttingState.EMPTY_VALID in view.contexts,
            "a first launch with no curriculum must produce UXIA-v0's empty_valid",
        )
    }

    @Test
    fun `a loaded curriculum with no plan yet is loading, not an empty day`() {
        val view = TodayPresentation.of(input(plan = null, curriculumLoaded = true))
        assertEquals(TodayState.LOADING_INITIAL_PLAN, view.state)
    }

    // ---------------------------------------------------------------- degraded context

    @Test
    fun `AI being unavailable is context and never supersedes a valid local plan`() {
        val view = TodayPresentation.of(input(plan = plan(task(1))))
        assertEquals(TodayState.READY_PLAN, view.state)
        assertEquals(
            listOf(CrossCuttingState.AI_UNAVAILABLE_CORE_AVAILABLE),
            view.contexts,
            "AI availability must ride alongside the plan, not replace it",
        )
    }

    // ---------------------------------------------------------------- attention and navigation

    @Test
    fun `attention does not duplicate what the primary task already represents`() {
        val repair = task(1, purpose = TaskPurpose.REMEDIATE)
        val both = listOf(
            AttentionItem(AttentionFamily.REMEDIATION_ATTENTION),
            AttentionItem(AttentionFamily.PLAN_CHANGED),
        )
        val view = TodayPresentation.of(input(plan = plan(repair), attention = both))
        assertEquals(
            listOf(AttentionFamily.PLAN_CHANGED),
            view.attention.map { it.family },
            "the remediation task is already the call to action; repeating it competes with itself",
        )
    }

    @Test
    fun `attention the primary task does not cover is kept`() {
        val practice = task(1, purpose = TaskPurpose.PRACTICE)
        val items = listOf(
            AttentionItem(AttentionFamily.REMEDIATION_ATTENTION),
            AttentionItem(AttentionFamily.RETENTION_ATTENTION),
        )
        val view = TodayPresentation.of(input(plan = plan(practice), attention = items))
        assertEquals(items.map { it.family }, view.attention.map { it.family })
    }

    @Test
    fun `attention families are THUX-v0's seven`() {
        assertEquals(
            listOf(
                "plan_changed", "verification_attention", "remediation_attention", "prerequisite_blocker",
                "retention_attention", "assessment_attention", "recovery_attention",
            ),
            AttentionFamily.entries.map { it.id },
        )
    }

    @Test
    fun `supporting navigation is reachable in every state and blocks nothing`() {
        val states = listOf(
            input(plan = plan(task(1))),
            input(plan = null, curriculumLoaded = false),
            input(replanInFlight = true),
        )
        states.forEach { state ->
            val view = TodayPresentation.of(state)
            // `THUX-v0` names the routes semantically (`progress`, `profile_capacity_settings`);
            // the canonical surface ids are `NSHX-v0`'s, and the mapping is recorded in the contract.
            assertEquals(
                listOf("planner_explanation", "skill_detail", "topic_detail", "progress_overview", "profile_overview"),
                view.supportingNavigation.map { it.id },
            )
        }
    }

    @Test
    fun `the region order is the accepted geometry`() {
        assertEquals(
            listOf("primary_action", "day_plan_context", "remaining_plan", "attention_context", "supporting_navigation"),
            TodayView.regionOrder,
        )
    }

    @Test
    fun `nothing on a task row can claim mastery, a score or a streak`() {
        val row = assertNotNull(TodayPresentation.of(input(plan = plan(task(1)))).primaryTask)
        val forbidden = Regex("mastery|score|percent|streak|grade|rank", RegexOption.IGNORE_CASE)
        val fields = TodayTaskRow::class.java.declaredFields.filterNot { it.isSynthetic }.map { it.name }
        assertEquals(emptyList(), fields.filter { forbidden.containsMatchIn(it) }, "row fields: $fields")
        assertEquals(TaskPurpose.PRACTICE, row.primaryPurpose)
    }

    @Test
    fun `capacity carries only the allowed summary fields`() {
        val forbidden = Regex("percent|progress|mastery|required|minimum|countdown", RegexOption.IGNORE_CASE)
        val fields = CapacityContext::class.java.declaredFields.filterNot { it.isSynthetic }.map { it.name }
        assertEquals(emptyList(), fields.filter { forbidden.containsMatchIn(it) }, "capacity fields: $fields")
    }

    // ---------------------------------------------------------------- reading the plan (12E)

    @Test
    fun `kept work is not offered as new work and never fills the queue`() {
        val kept = task(1).copy(kept = true, traceFacts = emptyList())
        val view = TodayPresentation.of(input(plan = plan(kept, task(2))))
        assertEquals(2L, view.primaryTask?.plannedTaskRef, "work already started was offered to start again")
        assertTrue(view.remainingPlan.none { it.plannedTaskRef == 1L }, "kept work was listed as upcoming")
        val onlyKept = TodayPresentation.of(input(plan = plan(kept)))
        assertNull(onlyKept.primaryTask)
        assertEquals(TodayState.EMPTY_NO_ELIGIBLE_TASK, onlyKept.state)
    }

    @Test
    fun `an unreadable plan for today is a recoverable fault and renders nothing from it`() {
        val view = TodayPresentation.of(input(plan = null, capacity = null).copy(planUnreadable = true))
        assertEquals(TodayState.ERROR_RECOVERABLE, view.state)
        assertEquals(PrimaryActionKind.RECOVERABLE_ERROR, view.primaryActionKind)
        assertNull(view.primaryTask)
        assertTrue(view.remainingPlan.isEmpty())
        assertEquals(Tone.SYSTEM_FAULT, view.state.tone)
        // Data recovery and a revalidated session still come first.
        val recovery = AppHealth.of(StoreStatus.RecoveryRequired(RecoveryReason.INTEGRITY_CHECK_FAILED), EvaluatorAvailability.UNAVAILABLE)
        assertEquals(TodayState.DATA_RECOVERY_REQUIRED,
            TodayPresentation.of(input(health = recovery).copy(planUnreadable = true)).state)
    }

    @Test
    fun `a replan and a waiting prerequisite the trace records become attention that links to the explanation`() {
        val facts = coach.model.TodayFacts(studyDay = today, plan = plan(task(1)), capacity = CapacityContext(45),
            curriculumLoaded = true, planReplaced = true, prerequisiteWaiting = true)
        val view = TodayPresentation.of(todayInput(facts, healthy))
        assertEquals(
            listOf(AttentionFamily.PLAN_CHANGED to Surface.PlannerExplanation,
                AttentionFamily.PREREQUISITE_BLOCKER to Surface.PlannerExplanation),
            view.attention.map { it.family to it.link },
        )
        val quiet = TodayPresentation.of(todayInput(facts.copy(planReplaced = false, prerequisiteWaiting = false), healthy))
        assertTrue(quiet.attention.isEmpty(), "attention appeared that the trace did not support")
    }
}
