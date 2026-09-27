package coach.model

/**
 * Evidence as the mastery engine reads it (12A), in `DDM-v0`'s four independent axes and the
 * vocabularies `GRE-v0` uses.
 *
 * The four axes stay four fields here for the same reason they are four columns: folding
 * `evaluator_status` into `outcome` makes a provisional result indistinguishable from a settled
 * one, and folding `independence_class` into it erases the difference between doing something
 * alone and doing it after seeing the answer.
 */

enum class EvidenceOutcome(val id: String) {
    POSITIVE("positive"),
    NEGATIVE("negative"),
    PARTIAL("partial"),
    INVALID("invalid"),
}

enum class EvaluatorStatus(val id: String) {
    VERIFIED("verified"),
    PROVISIONAL("provisional"),
    INVALID("invalid"),
}

enum class IndependenceClass(val id: String) {
    INDEPENDENT("independent"),
    ASSISTED("assisted"),
    PRACTICE_ONLY("practice_only"),
    REQUIRES_INDEPENDENT_RECHECK("requires_independent_recheck"),
}

/** `GRE-v0` §13: difficulty is a label used by gates, never a numeric multiplier. */
enum class DifficultyClass(val id: String) {
    BASIC("basic"),
    AUTHENTIC_APPLICATION("authentic_application"),
    TRANSFER_INTEGRATION("transfer_integration"),
    ;

    val isBasic: Boolean get() = this == BASIC
}

/**
 * One recorded evidence row, as the engine sees it.
 *
 * [quality] is the group-level result `GRE-v0` §4 calls `q_g`, in `[0,1]`. It is a rubric or
 * answer-key result for this attempt — **not** a confidence, a probability or a share of mastery.
 */
data class EvidenceRow(
    val id: Long,
    val sequence: Long,
    val objective: VersionedRef,
    val skill: VersionedRef,
    val evidenceType: String,
    val outcome: EvidenceOutcome,
    val evaluatorStatus: EvaluatorStatus,
    val independenceClass: IndependenceClass,
    val contested: Boolean,
    val quality: Double?,
    val difficulty: DifficultyClass?,
    val variantFamilyId: String?,
    val dependencyGroupId: String? = null,
    val resource: VersionedRef? = null,
    /** Whether the target prerequisites were eligible when this was produced (`PRG-v0`'s snapshot). */
    val prerequisiteValid: Boolean = true,
    /** Whether the learner had already been shown a solution for this item or a near variant. */
    val solutionExposed: Boolean = false,
    /** Whether the artifact's provenance says the learner produced the target behaviour. */
    val userAuthoredArtifact: Boolean = false,
) {
    init {
        require(quality == null || quality in 0.0..1.0) { "a group result is in [0,1], not a score out of anything" }
    }
}

/**
 * What the Objective requires, from `KGC-v0`'s profile plus `GRE-v0`'s defaults.
 *
 * `DDM-v0` names `required`, `criticality` and the evidence types as columns; the rest of the
 * profile (`min_independent_groups`, `min_variant_families`, `requires_*`) has no column, so it
 * travels as authored content exactly like 11D's item metadata, and falls back to `GRE-v0`'s
 * declared defaults rather than to something invented here.
 */
data class ObjectiveGateProfile(
    val ref: VersionedRef,
    val required: Boolean,
    val critical: Boolean,
    val acceptableEvidenceTypes: List<String>,
    val directEvidenceTypes: List<String>,
    val requiredDirectType: String? = null,
    val minIndependentGroups: Int? = null,
    val minVariantFamilies: Int? = null,
    val requiresNonBasicEvidence: Boolean = false,
    val requiresUserAuthoredArtifact: Boolean = false,
    val requiresTransfer: Boolean = false,
) {
    init {
        require(minIndependentGroups == null || minIndependentGroups >= 1) { "a gate asks for at least one group" }
        require(minVariantFamilies == null || minVariantFamilies >= 1) { "a gate asks for at least one family" }
    }
}

/** Why a row never entered a mastery score. Each value names a rule, never the learner. */
enum class EvidenceExclusion(val id: String) {
    NOT_DIRECT_EVIDENCE_FOR_OBJECTIVE("not_direct_evidence_for_objective"),
    NOT_INDEPENDENT("not_independent"),
    EVALUATOR_NOT_VERIFIED("evaluator_not_verified"),
    OUTCOME_INVALID("outcome_invalid"),
    CONTESTED("contested"),
    PREREQUISITE_CONTAMINATED("prerequisite_contaminated"),
    SOLUTION_EXPOSED("solution_exposed"),
    NO_GROUP_RESULT("no_group_result"),
}

/** `GRE-v0` §17. A band, not a statistical confidence interval, and never a percentage. */
enum class SupportBand(val id: String) {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
}

/** The mastery axis `DDM-v0` stores on `skill_state`, as `SPWX-v0` names its values. */
enum class MasteryAxisState(val id: String) {
    NOT_YET_EVIDENCED("not_yet_evidenced"),
    DEVELOPING_WITH_SUPPORT("developing_with_support"),
    DEVELOPING_INDEPENDENT("developing_independent"),
    CONFIRMED_CURRENT("confirmed_current"),
    CONFIRMATION_VERIFICATION_DUE("confirmation_verification_due"),
}
