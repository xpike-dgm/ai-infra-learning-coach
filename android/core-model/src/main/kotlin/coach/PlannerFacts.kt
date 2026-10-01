package coach.model

/**
 * The planner's vocabulary (12C), as the Stage 3 contracts fixed it: needs and candidates (3B / D-034),
 * capacity (3A / D-033), priority bands and the rank vector (`PBR-v0`), and the decision trace
 * (`PDT-v0`).
 *
 * Nothing here is a score. A band is a named class, the rank vector is compared field by field and is
 * never summed, and a deferred need is an open need — never a debt and never a failure.
 */

/** 3B §2.1: what opened a need. Not a priority order; `PBR-v0` decides that. */
enum class NeedTrigger(val id: String, val reasonCode: String) {
    NEW_LEARNING("new_learning", "need.new_learning_available"),
    CONTINUE_LEARNING("continue_learning", "need.continue_learning_active"),
    WEAKNESS_DETECTED("weakness_detected", "need.weakness_detected"),
    REMEDIATION_REQUIRED("remediation_required", "need.remediation_required"),
    RETENTION_REVIEW_DUE("retention_review_due", "need.retention_review_due"),
    VERIFICATION_DUE("verification_due", "need.verification_due"),
    DIAGNOSTIC_OPPORTUNITY("diagnostic_opportunity", "need.diagnostic_opportunity"),
    REINFORCEMENT_OPPORTUNITY("reinforcement_opportunity", "need.reinforcement_opportunity"),
    PARALLEL_TRACK_DUE("parallel_track_due", "need.parallel_track_due"),
    INTEGRATION_OPPORTUNITY("integration_opportunity", "need.integration_opportunity"),
}

/** `PBR-v0` §4: five named bands. A smaller number is handled earlier; it is not a grade. */
enum class PriorityBand(val id: String, val reasonCode: String) {
    P0("integrity_blocker", "priority.p0_integrity_blocker"),
    P1("repair_or_verify", "priority.p1_repair_or_verify"),
    P2("maintain_or_continue", "priority.p2_maintain_or_continue"),
    P3("planned_progress", "priority.p3_planned_progress"),
    P4("reinforce_or_optimize", "priority.p4_reinforce_or_optimize"),
}

// `PBR-v0` §6: each rank field is a named order, most pressing first. The declaration order *is* the
// order; nothing is converted to a number and added.

enum class BlockingScope(val id: String, val reasonCode: String?) {
    BLOCKS_CURRENT_REQUIRED_PATH("blocks_current_required_path", "priority.blocks_current_required_path"),
    BLOCKS_NEXT_READY_DEPENDENCY("blocks_next_ready_dependency", "priority.blocks_next_ready_dependency"),
    NON_BLOCKING("non_blocking", null),
}

enum class Criticality(val id: String) {
    CRITICAL_PREREQUISITE("critical_prerequisite"),
    REQUIRED("required"),
    SUPPORTING("supporting"),
    OPTIONAL("optional"),
}

enum class EvidenceSeverity(val id: String) {
    CONFIRMED_REPEATED_FAILURE("confirmed_repeated_failure"),
    CLEAN_CONTRADICTION_OR_VERIFICATION_DUE("clean_contradiction_or_verification_due"),
    PARTIAL_OR_UNCERTAIN_CONCERN("partial_or_uncertain_concern"),
    NO_NEGATIVE_EVIDENCE("no_negative_evidence"),
}

enum class TemporalUrgency(val id: String) {
    OVERDUE_HIGH("overdue_high"),
    JUST_DUE("just_due"),
    DUE_SOON("due_soon"),
    NOT_TIME_SENSITIVE("not_time_sensitive"),
}

/** `PBR-v0` §6.5. The threshold that moves a need out of `none` is uncalibrated (18B/18C). */
enum class StarvationBucket(val id: String) {
    PROMOTE("promote"),
    WATCH("watch"),
    NONE("none"),
}

enum class ContinuationValue(val id: String) {
    PAUSED_SAFE_CHECKPOINT("paused_safe_checkpoint"),
    ACTIVE_LEARNING_CONTEXT("active_learning_context"),
    FRESH_NEW_CONTEXT("fresh_new_context"),
}

/** `PBR-v0` §6.7: whether a short check would settle the next larger planning decision. */
enum class DecisionValue(val id: String) {
    DECISIVE("decisive"),
    NONE("none"),
}

/** `PBR-v0` §6.8: a parallel track repeatedly deferred while eligible. */
enum class TrackBalance(val id: String) {
    PRESSURE("pressure"),
    NONE("none"),
}

enum class DurationFit(val id: String) {
    FITS_REMAINING("fits_remaining"),
    FITS_VIA_SAFE_SPLIT("fits_via_safe_split"),
    FITS_VIA_SMALLER_ALTERNATIVE("fits_via_smaller_alternative"),
    CANNOT_FIT_TODAY("cannot_fit_today"),
}

/**
 * `PBR-v0` §5. Compared field by field in declaration order; the tie-break key makes the order total,
 * so the same needs always come out in the same order and no random tie-break is ever needed.
 */
data class RankVector(
    val blockingScope: BlockingScope,
    val criticality: Criticality,
    val evidenceSeverity: EvidenceSeverity,
    val temporalUrgency: TemporalUrgency,
    val starvation: StarvationBucket,
    val continuation: ContinuationValue,
    val decisionValue: DecisionValue,
    val trackBalance: TrackBalance,
    val durationFit: DurationFit,
    val tieBreakKey: String,
) : Comparable<RankVector> {
    override fun compareTo(other: RankVector): Int = compareValuesBy(
        this, other,
        { it.blockingScope }, { it.criticality }, { it.evidenceSeverity }, { it.temporalUrgency },
        { it.starvation }, { it.continuation }, { it.decisionValue }, { it.trackBalance },
        { it.durationFit }, { it.tieBreakKey },
    )
}

/** 3B §2: the need survives a day on which no task served it; a task never does. */
data class LearningNeed(
    val needKey: String,
    val trigger: NeedTrigger,
    val targetSkills: List<VersionedRef>,
    val criticality: Criticality,
    val sourceStateRefs: List<String> = emptyList(),
    val track: String? = null,
    val evidenceSeverity: EvidenceSeverity = EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
    val temporalUrgency: TemporalUrgency = TemporalUrgency.NOT_TIME_SENSITIVE,
    val continuation: ContinuationValue = ContinuationValue.FRESH_NEW_CONTEXT,
    val decisionValue: DecisionValue = DecisionValue.NONE,
    /** `PBR-v0` §7: an integration task the curriculum requires is progress, not an optional extra. */
    val requiredByCurriculum: Boolean = false,
) {
    init {
        require(needKey.isNotBlank()) { "a need is identified by its semantic key" }
        require(targetSkills.isNotEmpty()) { "a need names the Skill it is about" }
    }
}

/**
 * 3B §15, reduced to what planning reads. Candidates are authored content (15); the planner never
 * invents one, and a candidate that says nothing about its duration cannot be planned against a
 * time budget, so the cost is required.
 */
data class TaskCandidate(
    val id: String,
    val needKey: String,
    val purpose: TaskPurpose,
    val activityKind: String,
    val title: String,
    val primarySkill: VersionedRef,
    val costMinutes: Int,
    val validationStatus: LifecycleStatus,
    val track: String? = null,
    val requiredSkills: List<VersionedRef> = emptyList(),
    val requiresStrictPrerequisiteConfidence: Boolean = false,
    val splittable: Boolean = false,
    val minimumSafeChunkMinutes: Int? = null,
    val atomicEvidenceBoundary: Boolean = false,
    val generationVersion: String = "authored",
    /**
     * 3B §15 `target_objective_ids` (13F). A lesson whose Objectives were all waived is not taught again
     * (`VDW-v0` §17); a task that declares none is never treated as covered, because nothing says what it teaches.
     */
    val targetObjectives: List<VersionedRef> = emptyList(),
) {
    init {
        require(costMinutes > 0) { "a candidate takes some time" }
        require(!(splittable && atomicEvidenceBoundary)) {
            "an atomic evidence boundary is never cut in the middle (3A §12)"
        }
        require(!splittable || (minimumSafeChunkMinutes != null && minimumSafeChunkMinutes in 1 until costMinutes)) {
            "a splittable candidate names a safe chunk smaller than the whole"
        }
    }
}

/** D-033 §3. Editable shortcuts, not ideal study times. */
enum class CapacityProfile(val id: String) {
    SHORT("short"),
    NORMAL("normal"),
    INTENSIVE("intensive"),
}

/** D-033 §2 and `PDT-v0` §8.6: where today's minutes came from. */
enum class CapacitySource(val id: String, val reasonCode: String) {
    TODAY_OVERRIDE("today_override", "capacity.source_today_override"),
    SELECTED_SHORT("selected_short_profile", "capacity.source_short_profile"),
    SELECTED_NORMAL("selected_normal_profile", "capacity.source_normal_profile"),
    SELECTED_INTENSIVE("selected_intensive_profile", "capacity.source_intensive_profile"),
    SCHEDULED_DEFAULT("scheduled_default", "capacity.source_scheduled_default"),
    NORMAL_PROFILE("normal_profile", "capacity.source_normal_profile"),
}

/** D-033 §15. The learner's settings; the planner reads them and never stores a default of its own. */
data class DailyCapacityInput(
    val normalProfileMinutes: Int,
    val shortProfileMinutes: Int,
    val intensiveProfileMinutes: Int,
    val selectedProfile: CapacityProfile? = null,
    val scheduledDefaultMinutes: Int? = null,
    val todayOverrideMinutes: Int? = null,
) {
    init {
        listOfNotNull(normalProfileMinutes, shortProfileMinutes, intensiveProfileMinutes,
            scheduledDefaultMinutes, todayOverrideMinutes).forEach {
            require(it >= 0) { "a time budget is not negative" }
        }
    }
}

/** D-033 §4. The hard budget is the ceiling; the planning budget is what tasks are fitted into. */
data class DailyCapacity(
    val source: CapacitySource,
    val hardBudgetMinutes: Int,
    val planningBudgetMinutes: Int,
    val reserveRelaxed: Boolean,
    val belowMinimumBlock: Boolean,
)

/** `PDT-v0` §5. */
enum class NeedDisposition(val id: String) {
    SELECTED("selected"),
    PARTIALLY_SERVED("partially_served"),
    ELIGIBLE_NOT_SELECTED("eligible_not_selected"),
    BLOCKED("blocked"),
    RESOLVED_BEFORE_SELECTION("resolved_before_selection"),
    NO_VALID_CANDIDATE("no_valid_candidate"),
}

/** `PDT-v0` §6. */
enum class CandidateDisposition(val id: String) {
    SELECTED("selected"),
    SELECTED_SPLIT("selected_split"),
    SELECTED_SMALLER_ALTERNATIVE("selected_smaller_alternative"),
    BLOCKED_PREREQUISITE("blocked_prerequisite"),
    INVALID_CANDIDATE("invalid_candidate"),
    CONDITIONAL_NOT_SELECTED("conditional_not_selected"),
    ELIGIBLE_LOWER_PRIORITY("eligible_lower_priority"),
    ELIGIBLE_CAPACITY_DEFERRED("eligible_capacity_deferred"),
    SUPERSEDED_SAME_NEED_ALTERNATIVE("superseded_same_need_alternative"),
    DUPLICATE_SUPPRESSED("duplicate_suppressed"),
    RESOLVED_BEFORE_SELECTION("resolved_before_selection"),
}

/**
 * One selected task, with everything a Today row needs that `planned_task` has no column for:
 * purpose, title, activity and minutes live here, in the plan's trace, rather than in invented
 * columns (10D). [plannedMinutes] is below [estimatedMinutes] only when the task was split.
 */
data class PlannedEntry(
    val position: Int,
    val candidateId: String,
    val needKey: String,
    val purpose: TaskPurpose,
    val activityKind: String,
    val title: String,
    val primarySkill: VersionedRef,
    val track: String?,
    val estimatedMinutes: Int,
    val plannedMinutes: Int,
    val split: Boolean,
    /**
     * Carried over unchanged from the previous plan version because the learner had already started or
     * finished it (`TRUX-v0` §10.1: an in-flight run is never destroyed by a replan). 12D.
     */
    val preserved: Boolean = false,
)

data class NeedTrace(
    val needKey: String,
    val trigger: NeedTrigger,
    val targetSkills: List<VersionedRef>,
    val sourceStateRefs: List<String>,
    val band: PriorityBand,
    val rank: RankVector,
    val priorityReasonCodes: List<String>,
    val selectedCandidateId: String?,
    val disposition: NeedDisposition,
    val finalReasonCodes: List<String>,
)

data class CandidateTrace(
    val candidateId: String,
    val needKey: String,
    val validationStatus: LifecycleStatus,
    val eligibility: PrerequisiteEligibility?,
    val costMinutes: Int,
    val disposition: CandidateDisposition,
    val reasonCodes: List<String>,
    /**
     * `PDT-v0` §7 `related_refs`: the Skills the eligibility reason is about — the blocker a waiting
     * candidate waits on, the uncertain or soft-gap prerequisite it went ahead with, the one due for
     * review that did not block (12E). §11 requires a waiting task to name its real Skill blocker, and
     * only the gate's answer at planning time can say which that was.
     */
    val relatedSkills: List<VersionedRef> = emptyList(),
)

/** `PDT-v0` §4, for an `initial` generation. Replan and re-entry traces are 12D's. */
data class PlanTrace(
    val generationKind: String,
    val studyDay: String,
    val curriculumVersion: Int,
    val truthWatermark: Long,
    val policyVersions: Map<String, String>,
    val capacity: DailyCapacity,
    val needs: List<NeedTrace>,
    val candidates: List<CandidateTrace>,
    val selected: List<PlannedEntry>,
    val planReasonCodes: List<String>,
    val invariantChecks: Map<String, Boolean>,
    /** Published Skills no need could be opened for because their lifecycle keeps them off the route. */
    val skillsNotOnRoute: Int = 0,
    /** Present when this version replaced an earlier plan of the same day (12D). */
    val replan: ReplanRecord? = null,
    /** Present when the previous plan belonged to another study day and was not replayed (12D). */
    val reentry: ReentryContext? = null,
)

/**
 * What need generation reads about one published Skill: its curriculum lifecycle and critical flag,
 * and the axes their own engines last wrote. `null` means that engine has not written it.
 */
data class SkillPlanningState(
    val skill: VersionedRef,
    val lifecycleStatus: String,
    val critical: Boolean,
    val mastery: MasteryAxisState?,
    val retention: RetentionAxis,
    val weaknessAxis: String?,
    val snapshotRef: String? = null,
)

/** `PDT-v0` §4: why this plan version exists. */
enum class GenerationKind(val id: String) {
    INITIAL("initial"),
    REPLAN("replan"),
    REENTRY("reentry"),
}

/**
 * What asked for a replan (12D): D-033 §16, `PBR-v0` §17 and `PRG-v0` §19, each with the `PDT-v0` §8.10
 * code that describes it. An event with no such code carries none — no code is invented for it.
 */
enum class ReplanTrigger(val id: String, val reasonCode: String?) {
    TODAY_CAPACITY_CHANGED("today_capacity_changed", "replan.capacity_changed"),
    SESSION_REMAINING_TIME_CHANGED("session_remaining_time_changed", "replan.remaining_time_changed"),
    USER_REQUESTED_EXTRA_TIME("user_requested_extra_time", "replan.user_requested_extra_time"),
    USER_STOPPED_SESSION("user_stopped_session", "replan.user_stopped_session"),
    TASK_FINISHED_EARLY("task_finished_early", "replan.task_finished_early"),
    TASK_OVERRAN_ESTIMATE("task_overran_estimate", "replan.task_overran_estimate"),
    TASK_COMPLETED("task_completed", null),
    NEW_EVIDENCE_RECORDED("new_evidence_recorded", "replan.evidence_state_changed"),
    RETENTION_STATE_CHANGED("retention_state_changed", "replan.evidence_state_changed"),
    NEW_REMEDIATION_CREATED("new_remediation_created", "replan.new_remediation_created"),
    NEW_VERIFICATION_DUE_CREATED("new_verification_due_created", "replan.new_verification_created"),
    PREREQUISITE_STATE_CHANGED("prerequisite_state_changed", "replan.prerequisite_state_changed"),
    PREREQUISITE_MASTERY_CHANGED("prerequisite_mastery_changed", "replan.prerequisite_state_changed"),
    PREREQUISITE_RETENTION_STATE_CHANGED("prerequisite_retention_state_changed", "replan.prerequisite_state_changed"),
    PREREQUISITE_VERIFICATION_DUE_CREATED("prerequisite_verification_due_created", "replan.prerequisite_state_changed"),
    PREREQUISITE_VERIFICATION_RESOLVED("prerequisite_verification_resolved", "replan.prerequisite_state_changed"),
    PREREQUISITE_REMEDIATION_OPENED("prerequisite_remediation_opened", "replan.prerequisite_state_changed"),
    PREREQUISITE_REMEDIATION_RESOLVED("prerequisite_remediation_resolved", "replan.prerequisite_state_changed"),
    DIAGNOSTIC_WAIVER_GRANTED("diagnostic_waiver_granted", "replan.prerequisite_state_changed"),
    TASK_PREREQUISITE_METADATA_INVALID("task_prerequisite_metadata_invalid", "replan.prerequisite_state_changed"),
    CURRICULUM_PREREQUISITE_EDGE_CHANGED("curriculum_prerequisite_edge_changed", "replan.curriculum_version_changed"),
    ;

    /** D-033 §8: these change the remaining budget itself; every other event keeps the day's budget. */
    val setsRemainingTime: Boolean
        get() = this == SESSION_REMAINING_TIME_CHANGED || this == USER_REQUESTED_EXTRA_TIME
}

/**
 * `PDT-v0` §15, for a replan within one study day. Which earlier tasks were started or finished is
 * **reported by the caller** — `DDM-v0` gives an attempt no link to a planned task, so the store cannot
 * say — and every reported position is checked against the previous plan.
 */
data class ReplanRecord(
    val trigger: ReplanTrigger,
    val previousPlanVersionId: Long,
    val previousStudyDay: String,
    val preservedPositions: List<Int>,
    val invalidatedPositions: List<Int>,
    val preservedMinutes: Int,
    val remainderHardMinutes: Int,
)

/**
 * `SRR-v0` §17: what re-entry looked like. Informational only — no field here is a score, a penalty or
 * a debt, and absence is measured in study days purely for the record.
 */
data class ReentryContext(
    val previousPlanVersionId: Long,
    val lastPlannedStudyDay: String,
    val returnedStudyDay: String,
    val absenceStudyDays: Int,
    val stalePlannedTaskCount: Int,
    val pausedCheckpointNeedKeys: List<String>,
    val highStakesPausesNotResumed: Int,
    val openNeedCountByTrigger: Map<String, Int>,
    val dueSkillCountByRetention: Map<String, Int>,
    val p0P1NeedCount: Int,
    val resolvedDailyCapacityMinutes: Int,
)

/**
 * The newest plan version in the store, as a replan reads it (12D). The trace comes back as stored text;
 * interpreting it is core's job ([PlanTraceCodec]), not the adapter's.
 */
data class StoredPlan(
    val planVersionId: Long,
    val recordedAt: StudyTimestamp,
    /** Counted from `planned_task` itself, so re-entry can report it even when the trace does not decode. */
    val plannedTaskCount: Int,
    val traceText: String?,
    /**
     * The plan's own `planned_task` rows, in position order (12E). Today's rows point at these ids, and
     * the trace is only trusted to describe them when its positions and Skills are the rows' own.
     */
    val plannedTasks: List<StoredPlannedTask>,
)

/** One `planned_task` row as the store holds it: its id, its position and the Skill it serves. */
data class StoredPlannedTask(
    val plannedTaskId: Long,
    val position: Int,
    val skill: VersionedRef,
)
