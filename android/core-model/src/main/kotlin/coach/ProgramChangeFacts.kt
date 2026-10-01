package coach.model

/**
 * The program change report (13E): **what canonical state and the plan actually changed, and why** — read
 * from what the engines wrote and the planner traced, never inferred from answers.
 *
 * `ASUX-v0` §13.4: a result may state a change **only if canonical state actually changed**; if nothing
 * changed, it says so plainly. `WBA-v0` §30 / `MCA-v0` §30 ask the session summary for "what changed in the
 * plan", and V1's success criteria ask a weekly or monthly assessment to change the next plan. A report is a
 * diff of two readings, so it can never claim more than the engines did.
 */

/**
 * An Objective's gate profile from the published curriculum (13E): `KGC-v0`'s required flag, criticality and
 * evidence types, with every other gate parameter left to `GRE-v0`'s own defaults. A criticality the
 * curriculum does not define is refused rather than read as standard.
 */
object ObjectiveGateProfiles {
    fun of(row: ObjectiveRow): ObjectiveGateProfile = ObjectiveGateProfile(
        ref = row.ref,
        required = row.required,
        critical = when (row.criticality) {
            "critical" -> true
            "standard" -> false
            else -> throw IllegalArgumentException("unknown Objective criticality ${row.criticality}")
        },
        acceptableEvidenceTypes = row.acceptableEvidenceTypes,
        directEvidenceTypes = row.directEvidenceTypes,
        requiredDirectType = row.requiredDirectType,
    )
}

/** One Skill's axes as `skill_state` held them at a moment — the stored ids, unread and uninterpreted. */
data class SkillAxes(
    val skill: VersionedRef,
    val mastery: String,
    val retention: String,
    val weakness: String,
)

/**
 * One Objective's coverage waiver as `diagnostic_coverage` held it at a moment (13F): `active`, `none`, or
 * `not_yet_evaluated` when no row had been written.
 */
data class ObjectiveCoverage(
    val objective: VersionedRef,
    val skill: VersionedRef,
    val waiver: String,
)

/**
 * What canonical state and the plan said at one moment. [truthWatermark] is the truth the reading could have
 * seen; [plan] is the newest plan's decoded trace, `null` when there was none or it does not decode.
 * [coverage] holds the Objectives of the active diagnostic (13F); a waiver can only be granted there.
 */
data class ProgramSnapshot(
    val truthWatermark: Long,
    val studyDay: String,
    val skills: Map<VersionedRef, SkillAxes>,
    val planVersionId: Long?,
    val plan: PlanTrace?,
    val coverage: Map<VersionedRef, ObjectiveCoverage> = emptyMap(),
)

/**
 * A change of canonical state, each with the `ASUX-v0` §13.1 result family it belongs to and the `PDT-v0`
 * §8.10 reason a replan records for it. Time alone produces none of these: a review coming due is a schedule,
 * not a change a session caused.
 */
enum class StateChangeKind(val id: String, val family: String, val reasonCode: String) {
    CAPABILITY_CONFIRMED("capability_confirmed", "confirmed_capabilities", "replan.evidence_state_changed"),
    VERIFICATION_OPENED("verification_opened", "verification_needed", "replan.new_verification_created"),
    VERIFICATION_RESOLVED("verification_resolved", "confirmed_capabilities", "replan.evidence_state_changed"),
    MASTERY_NO_LONGER_CONFIRMED("mastery_no_longer_confirmed", "persistent_targeted_gaps", "replan.evidence_state_changed"),
    REMEDIATION_OPENED("remediation_opened", "persistent_targeted_gaps", "replan.new_remediation_created"),
    REMEDIATION_CLOSED("remediation_closed", "confirmed_capabilities", "replan.evidence_state_changed"),
    /** `SPWX-v0`: a hypothesis is an open question at most, never shown as a deficiency. */
    WEAKNESS_QUESTION_OPENED("weakness_question_opened", "verification_needed", "replan.evidence_state_changed"),
    WEAKNESS_SUPPORTED("weakness_supported", "persistent_targeted_gaps", "replan.evidence_state_changed"),
    WEAKNESS_RESOLVED("weakness_resolved", "confirmed_capabilities", "replan.evidence_state_changed"),
    RETENTION_REVALIDATED("retention_revalidated", "retention_revalidated", "replan.evidence_state_changed"),
    RETENTION_AT_RISK("retention_at_risk", "verification_needed", "replan.evidence_state_changed"),

    /**
     * 13F: an Objective's starting lesson was waived because its gates first passed on diagnostic evidence
     * (`VDW-v0` §9). The Objective was shown independently, so it is a confirmed capability at that grain — not
     * the Skill's mastery. The code is the one 12D records for `diagnostic_waiver_granted`.
     */
    COVERAGE_WAIVED("coverage_waived", "confirmed_capabilities", "replan.prerequisite_state_changed"),

    /**
     * 13F: a waiver no longer stands because the evidence it named was corrected (a disposition). The answer was
     * not reliably measured; the lesson simply comes back, and nothing is the learner's failure.
     */
    COVERAGE_WAIVER_WITHDRAWN("coverage_waiver_withdrawn", "not_reliably_measured", "replan.evidence_state_changed"),
}

/** [objective] is set only for a coverage change (13F), which is about one Objective rather than the Skill. */
data class StateChange(
    val skill: VersionedRef,
    val kind: StateChangeKind,
    val from: String,
    val to: String,
    val objective: VersionedRef? = null,
) {
    /** The reference an assessment result carries for it (`AssessmentBlueprintResult.stateChangeRefs`). */
    val ref: String get() = if (objective != null) "diagnostic_coverage:$objective#${kind.id}" else "skill_state:$skill#${kind.id}"
}

/** How the plan differs between two versions. */
enum class PlanChangeKind(val id: String) {
    NEED_OPENED("need_opened"),
    NEED_CLOSED("need_closed"),
    TASK_ADDED("task_added"),
    TASK_REMOVED("task_removed"),
}

data class PlanChange(
    val kind: PlanChangeKind,
    val needKey: String,
    val trigger: NeedTrigger?,
    val skills: List<VersionedRef>,
    val candidateId: String? = null,
)

/**
 * The report. [unknownBefore] names Skills whose earlier state no engine had written: a change from "not
 * evaluated" is not claimed, because nobody knew what it was before. [firstPlan] says there was no earlier
 * plan to compare against — a first plan is not a change.
 */
data class ProgramChangeReport(
    val fromWatermark: Long,
    val toWatermark: Long,
    val stateChanges: List<StateChange>,
    val planChanges: List<PlanChange>,
    val reasonCodes: List<String>,
    val unknownBefore: List<VersionedRef>,
    val firstPlan: Boolean,
) {
    init {
        require(toWatermark >= fromWatermark) { "a report reads forward in truth" }
    }

    /** `ASUX-v0` §13.4: when this is true, the result says so plainly. */
    val nothingChanged: Boolean get() = stateChanges.isEmpty() && planChanges.isEmpty()
}
