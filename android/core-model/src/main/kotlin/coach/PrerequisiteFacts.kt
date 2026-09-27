package coach.model

/**
 * Prerequisite readiness and eligibility (12B), in the vocabulary `PRG-v0` fixed at 3D.
 *
 * The gate exists so that target evidence is **interpretable and fair**, not to lock the
 * curriculum. So nothing here is a score: readiness is one of four named values, eligibility is
 * one of five, and a blocked candidate is a candidate that waits — never a need that failed.
 */

/** `PRG-v0` §3: what a Skill is worth *as a prerequisite* right now. Exactly four values. */
enum class PrerequisiteReadiness(val id: String) {
    READY("ready"),
    READY_DUE("ready_due"),
    UNCERTAIN("uncertain"),
    NOT_READY("not_ready"),
}

/** `PRG-v0` §7: what the gate says about one candidate. Exactly five values. */
enum class PrerequisiteEligibility(val id: String) {
    ELIGIBLE("eligible"),
    CONDITIONAL_ELIGIBLE("conditional_eligible"),
    ELIGIBLE_WITH_SUPPORT("eligible_with_support"),
    BLOCKED("blocked"),
    INVALID_PREREQUISITE_METADATA("invalid_prerequisite_metadata"),
    ;

    /** Whether new work on the target has to wait. Waiting is not failing. */
    val waits: Boolean get() = this == BLOCKED || this == INVALID_PREREQUISITE_METADATA
}

/** `PRG-v0` §2 / `KGC-v0` §12: two edge kinds and no third. */
enum class EdgeKind(val id: String) {
    HARD("hard"),
    SOFT("soft"),
    ;

    companion object {
        fun of(id: String): EdgeKind? = entries.firstOrNull { it.id == id }
    }
}

/**
 * Where a requirement came from. `PRG-v0` §6: the graph states the general learning dependency,
 * the candidate states what its exact task really needs, and a task requirement is always hard.
 */
enum class RequirementSource(val id: String) {
    GRAPH_EDGE("graph_edge"),
    TASK_REQUIRED("task_required"),
}

/**
 * `RVR-v0`'s retention axis as the prerequisite gate reads it, plus the one value that is true
 * before the retention engine (13) has ever written anything: it has not been evaluated.
 */
enum class RetentionAxis(val id: String) {
    UNTRACKED("untracked"),
    FRESH("fresh"),
    STABLE("stable"),
    REVIEW_DUE("review_due"),
    VERIFICATION_DUE("verification_due"),
    AT_RISK("at_risk"),
    NOT_YET_EVALUATED("not_yet_evaluated"),
    ;

    companion object {
        /** An absent or unrecognised value is reported as not evaluated — never guessed at. */
        fun of(id: String?): RetentionAxis = entries.firstOrNull { it.id == id } ?: NOT_YET_EVALUATED
    }
}

/**
 * `PRG-v0` §20: the reason inputs a single candidate decision can produce. The other three §20
 * names belong to whoever owns the situation they describe: `independent_branch_available` to the
 * planner (12C), `prerequisite_repaired_replan` to replan (12D), and
 * `invalid_due_to_prerequisite_contamination` to the evidence a contaminated attempt produced.
 */
enum class PrerequisiteReason(val id: String) {
    BLOCKED_MISSING_HARD_PREREQUISITE("blocked_missing_hard_prerequisite"),
    BLOCKED_CRITICAL_PREREQUISITE_VERIFICATION_DUE("blocked_critical_prerequisite_verification_due"),
    BLOCKED_PREREQUISITE_REMEDIATION_REQUIRED("blocked_prerequisite_remediation_required"),
    ELIGIBLE_PREREQUISITE_REVIEW_DUE_NOT_BLOCKING("eligible_prerequisite_review_due_not_blocking"),
    ELIGIBLE_WITH_SOFT_PREREQUISITE_GAP("eligible_with_soft_prerequisite_gap"),
    CONDITIONAL_PREREQUISITE_UNCERTAIN("conditional_prerequisite_uncertain"),
}

/**
 * Something about the metadata itself is wrong, so no readiness answer would be fair. `PRG-v0`
 * §14: the planner must not keep selecting a flawed candidate, and nothing here guesses a repair.
 */
enum class MetadataProblem(val id: String) {
    UNPUBLISHED_TARGET("unpublished_target"),
    UNPUBLISHED_REQUIRED_SKILL("unpublished_required_skill"),
    SELF_REQUIREMENT("self_requirement"),
    PREREQUISITE_CYCLE("prerequisite_cycle"),
    EDGE_NOT_PUBLISHED("edge_not_published"),
    EDGE_INVALIDATED("edge_invalidated"),
    UNKNOWN_EDGE_LIFECYCLE("unknown_edge_lifecycle"),
    UNKNOWN_STRICTNESS_PROFILE("unknown_strictness_profile"),
}

/** An input the gate could not read yet because its engine has not written it. Said, not hidden. */
enum class ReadinessNote(val id: String) {
    MASTERY_NOT_YET_EVALUATED("mastery_not_yet_evaluated"),
    RETENTION_NOT_YET_EVALUATED("retention_not_yet_evaluated"),
    REMEDIATION_NOT_YET_EVALUATED("remediation_not_yet_evaluated"),
}

/**
 * The state a Skill's readiness is derived from. Each axis comes from the engine that owns it
 * (`MSBX-v0`); `null` means that engine has not written it for this Skill.
 */
data class ReadinessInputs(
    val skill: VersionedRef,
    val mastery: MasteryAxisState?,
    val retention: RetentionAxis,
    val remediationRequired: Boolean?,
    /** Which projection row this was read from, so a decision can name its snapshot (`PRG-v0` §7). */
    val snapshotRef: String? = null,
)

data class SkillReadiness(
    val skill: VersionedRef,
    val readiness: PrerequisiteReadiness,
    val remediationRequired: Boolean,
    val notes: List<ReadinessNote>,
    val snapshotRef: String?,
)

/** One prerequisite a candidate has, and how strongly. */
data class PrerequisiteRequirement(
    val skill: VersionedRef,
    val kind: EdgeKind,
    val source: RequirementSource,
)

/**
 * What the gate is asked about. [requiredSkills] are the exact task's own hard requirements
 * (`PRG-v0` §6); [requiresStrictPrerequisiteConfidence] is the candidate asking for strict handling
 * of an uncertain prerequisite, as a high-stakes assessment or transfer task may (§4.1).
 */
data class PrerequisiteCandidate(
    val candidateId: String,
    val target: VersionedRef,
    val requiredSkills: List<VersionedRef> = emptyList(),
    val requiresStrictPrerequisiteConfidence: Boolean = false,
)

/**
 * `PRG-v0` §7. There is deliberately no priority input and no failure field: priority cannot
 * override eligibility (§8), and a blocked dependent need is not a failed need.
 */
data class PrerequisiteDecision(
    val candidateId: String,
    val target: VersionedRef,
    val eligibility: PrerequisiteEligibility,
    val hardBlockerSkills: List<VersionedRef>,
    val uncertainSkills: List<VersionedRef>,
    val softGapSkills: List<VersionedRef>,
    val reviewDueSkills: List<VersionedRef>,
    val metadataProblems: List<String>,
    val readinessSnapshotRefs: List<String>,
    val requiresStrictPrerequisiteConfidence: Boolean,
    val prerequisitePolicyVersion: String,
    val reasonInputs: List<PrerequisiteReason>,
    val notes: List<ReadinessNote>,
) {
    /**
     * What an attempt made under this decision records as its prerequisite snapshot (`PRG-v0` §13).
     * Work done on a candidate that should have waited cannot be attributed to the target, so its
     * evidence is marked contaminated and the mastery engine keeps it out of the score.
     */
    val evidenceSnapshot: String
        get() = if (eligibility.waits) PrerequisiteSnapshot.CONTAMINATED else eligibility.id
}

object PrerequisiteSnapshot {
    /** The one snapshot value the mastery engine reads as "the target's prerequisites were not there". */
    const val CONTAMINATED = "contaminated"
}
