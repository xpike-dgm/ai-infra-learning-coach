package coach.model

/**
 * The facts Today is built from.
 *
 * They live in `core-model` because both the read path (`core-application`) and the projection
 * (`core-presentation`) need them, and `MSBX-v0`'s dependency rule points both at this module and
 * never at each other. Nothing here is a presentation decision: these are the vocabularies and rows
 * other accepted contracts already own.
 */

/** `TASK_TAXONOMY_SPEC` §3.1 — one primary purpose per task. English is a track, never a purpose. */
enum class TaskPurpose(val id: String) {
    TEACH("teach"),
    PRACTICE("practice"),
    ASSESS("assess"),
    REMEDIATE("remediate"),
    RETAIN("retain"),
    DIAGNOSE("diagnose"),
    REINFORCE("reinforce"),
}

/**
 * The reason families `THUX-v0` §7.1 allows a Today row to show, each standing for a fact the
 * planner's `PDT-v0` trace recorded.
 *
 * A closed vocabulary rather than text is what makes a free-form AI justification unrepresentable
 * as a canonical reason: there is no string for one to occupy.
 */
enum class ReasonFamily(val id: String) {
    CONTINUE_CURRENT_LEARNING("continue_current_learning"),
    REPAIR_CONFIRMED_WEAKNESS("repair_confirmed_weakness"),
    VERIFY_UNCERTAIN_STATE("verify_uncertain_state"),
    REVIEW_DUE_KNOWLEDGE("review_due_knowledge"),
    COLLECT_MISSING_EVIDENCE("collect_missing_evidence"),
    PARALLEL_TECHNICAL_ENGLISH("parallel_technical_english"),
    FIT_AVAILABLE_CAPACITY("fit_available_capacity"),
    RESUME_VALID_PAUSED_WORK("resume_valid_paused_work"),
}

/**
 * D-033 and `THUX-v0` §4.2: capacity is a **time budget**, never progress or mastery. Only the five
 * allowed summary fields exist, so there is no percentage, required minimum or countdown to render.
 */
data class CapacityContext(
    val resolvedDailyMinutes: Int,
    val currentDayOverride: Boolean = false,
    val estimatedTotalPlannedMinutes: Int? = null,
    val estimatedRemainingPlannedMinutes: Int? = null,
    val planRecalculated: Boolean = false,
    /**
     * The planner's own finding that nothing it had fits the resolved capacity (D-033, `PBR-v0`).
     * It is a fact rather than a derivation because comparing two estimates in the projection would
     * be Today quietly deciding what the planner is allowed to select.
     */
    val tooSmallForAnyCandidate: Boolean = false,
)

/** One selected `PlannedTask` as the store holds it, plus the eligibility the engines decided. */
data class PlannedTaskFact(
    val plannedTaskId: Long,
    val displayTitle: String,
    val primaryPurpose: TaskPurpose,
    val targetSkillRefs: List<VersionedRef>,
    val position: Int = 0,
    val traceFacts: List<ReasonFamily> = emptyList(),
    val blocked: Boolean = false,
    val activityKind: String? = null,
    val curriculumTrack: String? = null,
    val topicRef: VersionedRef? = null,
    val estimatedMinutes: Int? = null,
    val integrationMode: String? = null,
)

/**
 * A plan version and the tasks it selected, for one learner-local study day. The study day is part
 * of the plan because a plan produced on another day is not today's plan (`SRR-v0`).
 */
data class PlanSnapshot(
    val planVersionId: Long,
    val policyVersion: String,
    val studyDay: String,
    val tasks: List<PlannedTaskFact>,
)

/** Everything the store and the clock can say about today. */
data class TodayFacts(
    val studyDay: String,
    val plan: PlanSnapshot? = null,
    val capacity: CapacityContext? = null,
    val curriculumLoaded: Boolean = false,
)
