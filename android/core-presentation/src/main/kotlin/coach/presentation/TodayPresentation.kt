package coach.presentation

import coach.model.CapacityContext
import coach.model.PlanSnapshot
import coach.model.PlannedTaskFact
import coach.model.ReasonFamily
import coach.model.TaskPurpose
import coach.model.TodayFacts
import coach.model.VersionedRef

/**
 * `THUX-v0 / D-069` as a pure function: **Today is a projection of canonical planner/state truth.**
 * It never becomes a second planner, mastery engine, prerequisite engine, gradebook or English
 * quota system — and at this step it cannot become one, because it computes nothing it was not
 * given. The planner itself is 12; 11A only renders what the store already holds.
 *
 * This lives in `core-presentation` for the same reason navigation and tones do: what Today claims
 * about the learner is the most safety-critical part of the screen, and it has to be testable as a
 * plain JVM function rather than something noticed on a phone (`MSBX-v0` §presentation).
 */

/** The twelve semantic states `THUX-v0` defines, in its order. No thirteenth state exists. */
enum class TodayState(val id: String) {
    LOADING_INITIAL_PLAN("loading_initial_plan"),
    REPLANNING("replanning"),
    READY_PLAN("ready_plan"),
    RESUMABLE_SESSION("resumable_session"),
    EMPTY_NO_OPEN_NEED("empty_no_open_need"),
    EMPTY_NO_ELIGIBLE_TASK("empty_no_eligible_task"),
    CAPACITY_ZERO("capacity_zero"),
    CAPACITY_TOO_SMALL_NO_CANDIDATE("capacity_too_small_no_candidate"),
    OFFLINE_LOCAL_AVAILABLE("offline_local_available"),
    AI_UNAVAILABLE_CORE_AVAILABLE("ai_unavailable_core_available"),
    ERROR_RECOVERABLE("error_recoverable"),
    DATA_RECOVERY_REQUIRED("data_recovery_required"),
}

/**
 * Each Today state's tone, copied from `VDSX-v0`'s accepted map — not chosen here.
 *
 * Only the two system conditions are allowed the fault tone; waiting, an empty day and a capacity
 * limit are neutral because none of them is a failure, and `resumable_session` is `active` because
 * it is the live thing right now.
 */
val TodayState.tone: Tone
    get() = when (this) {
        TodayState.RESUMABLE_SESSION -> Tone.ACTIVE
        TodayState.ERROR_RECOVERABLE -> Tone.SYSTEM_FAULT
        TodayState.DATA_RECOVERY_REQUIRED -> Tone.SYSTEM_FAULT
        TodayState.LOADING_INITIAL_PLAN,
        TodayState.REPLANNING,
        TodayState.READY_PLAN,
        TodayState.EMPTY_NO_OPEN_NEED,
        TodayState.EMPTY_NO_ELIGIBLE_TASK,
        TodayState.CAPACITY_ZERO,
        TodayState.CAPACITY_TOO_SMALL_NO_CANDIDATE,
        TodayState.OFFLINE_LOCAL_AVAILABLE,
        TodayState.AI_UNAVAILABLE_CORE_AVAILABLE -> Tone.NEUTRAL
    }

/** `THUX-v0` §5 primary-action precedence, in order. The first that applies wins. */
enum class PrimaryActionKind(val id: String) {
    DATA_RECOVERY_REQUIRED("data_recovery_required"),
    VALID_RESUMABLE_FOCUSED_SESSION("valid_resumable_focused_session"),
    SELECTED_NEXT_PLANNED_TASK("selected_next_planned_task"),
    PLAN_LOADING_OR_REPLANNING("plan_loading_or_replanning"),
    VALID_EMPTY_OR_CAPACITY_LIMITED_STATE("valid_empty_or_capacity_limited_state"),
    RECOVERABLE_ERROR("recoverable_error"),
}

enum class TaskDisposition(val id: String) {
    CURRENT("current"),
    UPCOMING("upcoming"),
    PAUSED("paused"),
}

/**
 * One primary reason and at most one supporting reason (`THUX-v0` §7.1), both drawn from the
 * planner's own trace facts (`PDT-v0`).
 *
 * The private constructor plus [fromTraceFacts] is the whole enforcement of
 * `today_reason_summary ⊆ PlannerDecisionTrace facts`: a reason that is not in the trace cannot be
 * constructed, so the overview cannot explain a task with something the planner never decided.
 */
class ReasonSummary private constructor(
    val primary: ReasonFamily,
    val supporting: ReasonFamily?,
) {
    override fun equals(other: Any?): Boolean =
        other is ReasonSummary && other.primary == primary && other.supporting == supporting

    override fun hashCode(): Int = 31 * primary.hashCode() + (supporting?.hashCode() ?: 0)

    override fun toString(): String = "ReasonSummary($primary, $supporting)"

    companion object {
        /** Takes the planner's trace facts in their declared order; null when the trace says nothing. */
        fun fromTraceFacts(traceFacts: List<ReasonFamily>): ReasonSummary? {
            val distinct = traceFacts.distinct()
            val primary = distinct.firstOrNull() ?: return null
            return ReasonSummary(primary, distinct.getOrNull(1))
        }
    }
}

/**
 * A Today row is a projection of a selected `PlannedTask`; it is not a new task identity
 * (`THUX-v0` §6). Purpose, activity and track stay three separate fields, so "English" or "quiz"
 * can never become the canonical reason a task exists.
 */
data class TodayTaskRow(
    val plannedTaskRef: Long,
    val displayTitle: String,
    val primaryPurpose: TaskPurpose,
    val targetSkillRefs: List<VersionedRef>,
    val disposition: TaskDisposition,
    val reason: ReasonSummary?,
    val activityKind: String? = null,
    val curriculumTrack: String? = null,
    val topicRef: VersionedRef? = null,
    val estimatedMinutes: Int? = null,
    val integrationMode: String? = null,
)

/** The seven attention families `THUX-v0` §4.4 allows. Attention explains state; it never ranks it. */
enum class AttentionFamily(val id: String) {
    PLAN_CHANGED("plan_changed"),
    VERIFICATION_ATTENTION("verification_attention"),
    REMEDIATION_ATTENTION("remediation_attention"),
    PREREQUISITE_BLOCKER("prerequisite_blocker"),
    RETENTION_ATTENTION("retention_attention"),
    ASSESSMENT_ATTENTION("assessment_attention"),
    RECOVERY_ATTENTION("recovery_attention"),
}

data class AttentionItem(val family: AttentionFamily, val link: Surface? = null)

/** A paused focused flow offered for resume. Its interior and checkpoint content belong to 11B/11C. */
data class ResumableSession(
    val surface: Surface,
    val displayTitle: String,
    val revalidated: Boolean,
)

/**
 * Everything Today is allowed to know. Each field is something another owner already decided:
 * health from `APHX-v0`, the plan and its trace from the planner (12), capacity from D-033,
 * the resumable session from 11B/11C, the study day from `ClockPort`.
 */
data class TodayInput(
    val health: AppHealth,
    val studyDay: String,
    val plan: PlanSnapshot? = null,
    val resumable: ResumableSession? = null,
    val capacity: CapacityContext? = null,
    val curriculumLoaded: Boolean = false,
    val replanInFlight: Boolean = false,
    val attention: List<AttentionItem> = emptyList(),
)

/** What Today renders. `app-ui` draws this and decides nothing (`MSBX-v0`). */
data class TodayView(
    val state: TodayState,
    val primaryActionKind: PrimaryActionKind,
    val primaryTask: TodayTaskRow?,
    val resumable: ResumableSession?,
    val capacity: CapacityContext?,
    val remainingPlan: List<TodayTaskRow>,
    val attention: List<AttentionItem>,
    val contexts: List<CrossCuttingState>,
    val supportingNavigation: List<Surface>,
) {
    /** Region order is `WFPX-v0`'s and does not change with window class. */
    companion object {
        val regionOrder: List<String> = listOf(
            "primary_action", "day_plan_context", "remaining_plan", "attention_context", "supporting_navigation",
        )

        /**
         * `THUX-v0` §4.5: reachable from Today, never a precondition for starting work. Computed on
         * access for the same initialisation-order reason as `Surface`'s own registry.
         *
         * `THUX-v0` names these routes semantically (`progress`, `profile_capacity_settings`); the
         * canonical surface identities are `NSHX-v0`'s, and one surface per entity is exactly what
         * keeps a second Skill detail page unrepresentable.
         */
        val supportingRoutes: List<Surface>
            get() = listOf(
                Surface.PlannerExplanation,
                Surface.SkillDetail,
                Surface.TopicDetail,
                Surface.ProgressOverview,
                Surface.ProfileOverview,
            )
    }
}

/**
 * Assembles what the store said with what app health says. It lives here rather than in the
 * composition root because combining facts into a presentation input is a presentation decision,
 * and `MSBX-v0` keeps those out of wiring.
 */
fun todayInput(
    facts: TodayFacts,
    health: AppHealth,
    resumable: ResumableSession? = null,
    replanInFlight: Boolean = false,
    attention: List<AttentionItem> = emptyList(),
): TodayInput = TodayInput(
    health = health,
    studyDay = facts.studyDay,
    plan = facts.plan,
    resumable = resumable,
    capacity = facts.capacity,
    curriculumLoaded = facts.curriculumLoaded,
    replanInFlight = replanInFlight,
    attention = attention,
)

object TodayPresentation {

    /**
     * The whole rule, in `THUX-v0`'s precedence order.
     *
     * Two things are deliberately impossible here:
     *
     * - **a stale plan is never shown as today's.** A plan produced on another study day does not
     *   become a task row; the learner sees that a fresh plan is owed instead. `SRR-v0` forbids
     *   replaying yesterday's plan as backlog, and the safest way to honour that is to have no code
     *   path that can render it.
     * - **a blocked task is never actionable.** Blocked work is filtered out rather than shown as a
     *   greyed-out call to action, because `THUX-v0` forbids both a startable blocked task and a
     *   permanently blocked task list.
     */
    fun of(input: TodayInput): TodayView {
        val contexts = input.health.contexts
        val supporting = TodayView.supportingRoutes

        // 1. The two system conditions come from APHX-v0 and are rendered app-wide; Today reports
        //    the same state rather than inventing a second recovery surface.
        input.health.blocking?.let { blocking ->
            when (blocking) {
                CrossCuttingState.DATA_RECOVERY_REQUIRED -> return blocked(
                    TodayState.DATA_RECOVERY_REQUIRED, PrimaryActionKind.DATA_RECOVERY_REQUIRED,
                    listOf(AttentionItem(AttentionFamily.RECOVERY_ATTENTION)), supporting,
                )
                CrossCuttingState.ERROR_RECOVERABLE -> return blocked(
                    TodayState.ERROR_RECOVERABLE, PrimaryActionKind.RECOVERABLE_ERROR, emptyList(), supporting,
                )
                else -> return blocked(
                    TodayState.LOADING_INITIAL_PLAN, PrimaryActionKind.PLAN_LOADING_OR_REPLANNING,
                    emptyList(), supporting,
                )
            }
        }

        // 2. A valid resumable session outranks starting unrelated new work, but only after it has
        //    been revalidated: a stale paused session may not bypass current state (THUX-v0 §5).
        val resumable = input.resumable?.takeIf { it.revalidated }
        if (resumable != null) {
            return TodayView(
                state = TodayState.RESUMABLE_SESSION,
                primaryActionKind = PrimaryActionKind.VALID_RESUMABLE_FOCUSED_SESSION,
                primaryTask = null,
                resumable = resumable,
                capacity = input.capacity,
                remainingPlan = queueOf(input, current = null),
                attention = attentionFor(input, primary = null),
                contexts = contexts,
                supportingNavigation = supporting,
            )
        }

        // 3. A plan for *this* study day, with something eligible in it.
        val plan = input.plan?.takeIf { it.studyDay == input.studyDay }
        val eligible = plan?.tasks?.filterNot { it.blocked }.orEmpty()
        if (plan != null && eligible.isNotEmpty() && !input.replanInFlight) {
            val current = eligible.first()
            return TodayView(
                state = TodayState.READY_PLAN,
                primaryActionKind = PrimaryActionKind.SELECTED_NEXT_PLANNED_TASK,
                primaryTask = row(current, TaskDisposition.CURRENT),
                resumable = null,
                capacity = input.capacity,
                remainingPlan = queueOf(input, current = current),
                attention = attentionFor(input, primary = row(current, TaskDisposition.CURRENT)),
                contexts = contexts,
                supportingNavigation = supporting,
            )
        }

        // 4. Loading or replanning. A plan from another study day lands here too: it is owed a
        //    fresh one, and until then there is nothing safe to show.
        val stalePlan = input.plan != null && input.plan.studyDay != input.studyDay
        // A capacity-limited day explains the absent plan by itself: calling it "loading" would
        // promise work that is not coming, when the truthful answer is that today's budget is zero
        // or smaller than anything the planner had.
        val capacityExplainsAbsence =
            input.capacity?.resolvedDailyMinutes == 0 || input.capacity?.tooSmallForAnyCandidate == true
        val planOwedButAbsent = input.plan == null && input.curriculumLoaded && !capacityExplainsAbsence
        if (input.replanInFlight || stalePlan || planOwedButAbsent) {
            return TodayView(
                state = if (input.replanInFlight) TodayState.REPLANNING else TodayState.LOADING_INITIAL_PLAN,
                primaryActionKind = PrimaryActionKind.PLAN_LOADING_OR_REPLANNING,
                primaryTask = null,
                resumable = null,
                capacity = input.capacity,
                remainingPlan = emptyList(),
                attention = attentionFor(input, primary = null),
                contexts = contexts,
                supportingNavigation = supporting,
            )
        }

        // 5. A legitimate empty or capacity-limited day. None of these is a failure and none of them
        //    implies that everything is mastered or that the learner is professionally ready.
        val state = when {
            input.capacity?.resolvedDailyMinutes == 0 -> TodayState.CAPACITY_ZERO
            plan != null && plan.tasks.isNotEmpty() && eligible.isEmpty() -> TodayState.EMPTY_NO_ELIGIBLE_TASK
            input.capacity?.tooSmallForAnyCandidate == true -> TodayState.CAPACITY_TOO_SMALL_NO_CANDIDATE
            else -> TodayState.EMPTY_NO_OPEN_NEED
        }
        return TodayView(
            state = state,
            primaryActionKind = PrimaryActionKind.VALID_EMPTY_OR_CAPACITY_LIMITED_STATE,
            primaryTask = null,
            resumable = null,
            capacity = input.capacity,
            remainingPlan = emptyList(),
            attention = attentionFor(input, primary = null),
            // Nothing is published yet, and saying so is `UXIA-v0`'s `empty_valid` — the
            // cross-cutting state 10E declared and left to this step to produce.
            contexts = if (input.curriculumLoaded) contexts else contexts + CrossCuttingState.EMPTY_VALID,
            supportingNavigation = supporting,
        )
    }

    private fun blocked(
        state: TodayState,
        kind: PrimaryActionKind,
        attention: List<AttentionItem>,
        supporting: List<Surface>,
    ) = TodayView(
        state = state,
        primaryActionKind = kind,
        primaryTask = null,
        resumable = null,
        capacity = null,
        // Nothing from a store that is not usable may be rendered as today's plan.
        remainingPlan = emptyList(),
        attention = attention,
        contexts = emptyList(),
        supportingNavigation = supporting,
    )

    /** The queue is the rest of the *current* selection — never a backlog, never blocked work. */
    private fun queueOf(input: TodayInput, current: PlannedTaskFact?): List<TodayTaskRow> {
        val plan = input.plan?.takeIf { it.studyDay == input.studyDay } ?: return emptyList()
        return plan.tasks
            .filterNot { it.blocked }
            .filterNot { it.plannedTaskId == current?.plannedTaskId }
            .map { row(it, TaskDisposition.UPCOMING) }
    }

    private fun row(task: PlannedTaskFact, disposition: TaskDisposition) = TodayTaskRow(
        plannedTaskRef = task.plannedTaskId,
        displayTitle = task.displayTitle,
        primaryPurpose = task.primaryPurpose,
        targetSkillRefs = task.targetSkillRefs,
        disposition = disposition,
        reason = ReasonSummary.fromTraceFacts(task.traceFacts),
        activityKind = task.activityKind,
        curriculumTrack = task.curriculumTrack,
        topicRef = task.topicRef,
        estimatedMinutes = task.estimatedMinutes,
        integrationMode = task.integrationMode,
    )

    /**
     * Attention explains state; it never competes with the action that already represents it
     * (`THUX-v0` §4.4). A remediation task already *is* the remediation call to action, so repeating
     * it underneath as a second competing entry is dropped; the same holds for retention and
     * assessment. Everything else survives, because attention the primary action does not cover is
     * exactly what this region is for.
     */
    private fun attentionFor(input: TodayInput, primary: TodayTaskRow?): List<AttentionItem> {
        val representedByPrimary = when (primary?.primaryPurpose) {
            TaskPurpose.REMEDIATE -> AttentionFamily.REMEDIATION_ATTENTION
            TaskPurpose.RETAIN -> AttentionFamily.RETENTION_ATTENTION
            TaskPurpose.ASSESS -> AttentionFamily.ASSESSMENT_ATTENTION
            else -> null
        }
        return input.attention.filterNot { it.family == representedByPrimary }
    }
}
