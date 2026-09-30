package coach.model

/**
 * The reason codes a plan's trace may carry (12E), as the contracts fixed them: `PDT-v0` §8's ten
 * families, in its order, and `PRG-v0` §20's explainability inputs.
 *
 * A reason code is a stable machine-readable identity, not a UI string (`PDT-v0` §7). The catalogue is
 * closed on purpose: an explanation can only ever say something a code here stands for, and a code the
 * contracts never defined has no entry to be explained through.
 */
enum class ReasonCodeFamily(val id: String, val source: String) {
    LEARNING_NEED("learning_need", "PDT-v0 §8.1"),
    VALIDATION_TRUST("validation_trust", "PDT-v0 §8.2"),
    PREREQUISITE("prerequisite", "PDT-v0 §8.3"),
    RETENTION("retention", "PDT-v0 §8.4"),
    PRIORITY("priority", "PDT-v0 §8.5"),
    CAPACITY("capacity", "PDT-v0 §8.6"),
    DIAGNOSTIC("diagnostic", "PDT-v0 §8.7"),
    REENTRY("reentry", "PDT-v0 §8.8"),
    SELECTION("selection", "PDT-v0 §8.9"),
    REPLAN("replan", "PDT-v0 §8.10"),

    /** `PRG-v0` §20: the prerequisite engine's reason inputs "for 3G's final texts". They have no namespace. */
    PREREQUISITE_INPUT("prerequisite_input", "PRG-v0 §20"),
}

object ReasonCatalog {

    /** Every code, grouped by family, each family in the order its contract lists it. */
    val codes: Map<ReasonCodeFamily, List<String>> = linkedMapOf(
        ReasonCodeFamily.LEARNING_NEED to listOf(
            "need.new_learning_available",
            "need.continue_learning_active",
            "need.weakness_detected",
            "need.remediation_required",
            "need.retention_review_due",
            "need.verification_due",
            "need.diagnostic_opportunity",
            "need.reinforcement_opportunity",
            "need.parallel_track_due",
            "need.integration_opportunity",
        ),
        ReasonCodeFamily.VALIDATION_TRUST to listOf(
            "candidate.validated",
            "candidate.invalid_content",
            "candidate.invalid_evidence_contract",
            "candidate.invalid_prerequisite_metadata",
            "candidate.untrusted_for_high_stakes_use",
            "candidate.duplicate_suppressed",
            "candidate.same_need_alternative_superseded",
        ),
        ReasonCodeFamily.PREREQUISITE to listOf(
            "eligibility.ready",
            "eligibility.ready_due_allowed",
            "eligibility.conditional_uncertain",
            "eligibility.soft_gap_support",
            "eligibility.blocked_hard_prerequisite",
            "eligibility.blocked_critical_verification",
            "eligibility.blocked_strict_prerequisite_confidence",
            "eligibility.invalid_prerequisite_metadata",
            "eligibility.prerequisite_contamination_guard",
        ),
        ReasonCodeFamily.RETENTION to listOf(
            "retention.review_due_not_failure",
            "retention.overdue_priority_signal",
            "retention.verification_due",
            "retention.at_risk",
            "retention.remediation_confirmed",
            "retention.natural_reuse_satisfied",
            "retention.no_auto_decay",
        ),
        ReasonCodeFamily.PRIORITY to listOf(
            "priority.p0_integrity_blocker",
            "priority.p1_repair_or_verify",
            "priority.p2_maintain_or_continue",
            "priority.p3_planned_progress",
            "priority.p4_reinforce_or_optimize",
            "priority.blocks_current_required_path",
            "priority.blocks_next_ready_dependency",
            "priority.criticality_advantage",
            "priority.evidence_severity_advantage",
            "priority.temporal_urgency_advantage",
            "priority.starvation_promoted",
            "priority.continuation_value",
            "priority.decision_value",
            "priority.track_balance_pressure",
            "priority.stable_tie_break",
        ),
        ReasonCodeFamily.CAPACITY to listOf(
            "capacity.source_today_override",
            "capacity.source_short_profile",
            "capacity.source_normal_profile",
            "capacity.source_intensive_profile",
            "capacity.source_scheduled_default",
            "capacity.selected_within_budget",
            "capacity.split_to_fit",
            "capacity.smaller_alternative_to_fit",
            "capacity.deferred_not_enough_time",
            "capacity.reserve_applied",
            "capacity.reserve_relaxed_for_microtask",
            "capacity.hard_budget_not_exceeded",
            "capacity.no_penalty_user_stopped",
        ),
        ReasonCodeFamily.DIAGNOSTIC to listOf(
            "diagnostic.user_requested_fast_path",
            "diagnostic.probe_selected",
            "diagnostic.confirm_needed",
            "diagnostic.critical_confirm_needed",
            "diagnostic.partial_coverage_waiver",
            "diagnostic.full_coverage_waiver",
            "diagnostic.no_waiver",
            "diagnostic.h0_required_for_waiver",
            "diagnostic.prerequisite_blocked",
            "diagnostic.component_not_separately_attributable",
        ),
        ReasonCodeFamily.REENTRY to listOf(
            "reentry.return_after_absence",
            "reentry.absence_not_failure",
            "reentry.absence_not_task_debt",
            "reentry.stale_plan_not_replayed",
            "reentry.current_state_regenerated",
            "reentry.paused_checkpoint_candidate",
            "reentry.incomplete_high_stakes_attempt_not_scored",
            "reentry.due_inventory_not_daily_plan",
        ),
        ReasonCodeFamily.SELECTION to listOf(
            "selection.selected",
            "selection.selected_split",
            "selection.selected_smaller_alternative",
            "selection.not_selected_lower_priority",
            "selection.not_selected_capacity",
            "selection.blocked_prerequisite",
            "selection.invalid_candidate",
            "selection.same_need_alternative_not_used",
            "selection.resolved_before_selection",
        ),
        ReasonCodeFamily.REPLAN to listOf(
            "replan.capacity_changed",
            "replan.remaining_time_changed",
            "replan.task_finished_early",
            "replan.task_overran_estimate",
            "replan.new_remediation_created",
            "replan.new_verification_created",
            "replan.evidence_state_changed",
            "replan.prerequisite_state_changed",
            "replan.topic_state_changed",
            "replan.return_after_absence",
            "replan.user_requested_extra_time",
            "replan.user_stopped_session",
            "replan.curriculum_version_changed",
        ),
        ReasonCodeFamily.PREREQUISITE_INPUT to listOf(
            "blocked_missing_hard_prerequisite",
            "blocked_critical_prerequisite_verification_due",
            "blocked_prerequisite_remediation_required",
            "eligible_prerequisite_review_due_not_blocking",
            "eligible_with_soft_prerequisite_gap",
            "conditional_prerequisite_uncertain",
            "independent_branch_available",
            "prerequisite_repaired_replan",
            "invalid_due_to_prerequisite_contamination",
        ),
    )

    private val familyByCode: Map<String, ReasonCodeFamily> =
        codes.flatMap { (family, list) -> list.map { it to family } }.toMap()

    val all: List<String> = codes.values.flatten()

    /** The family a code belongs to, or `null` when no contract defines it. */
    fun familyOf(code: String): ReasonCodeFamily? = familyByCode[code]

    fun isKnown(code: String): Boolean = code in familyByCode
}

/**
 * Every reason code this trace recorded, wherever it recorded it: the plan's own codes, each need's
 * trigger, priority and final codes, each candidate's codes and the replan trigger's code. An
 * explanation may say only what is in this set (`PDT-v0` §2.2: `user_facing_explanation ⊆
 * facts_in_decision_trace`).
 */
fun PlanTrace.recordedReasonCodes(): Set<String> = buildSet {
    addAll(planReasonCodes)
    needs.forEach { need ->
        add(need.trigger.reasonCode)
        addAll(need.priorityReasonCodes)
        addAll(need.finalReasonCodes)
    }
    candidates.forEach { addAll(it.reasonCodes) }
    replan?.trigger?.reasonCode?.let(::add)
}
