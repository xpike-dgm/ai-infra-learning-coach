package coach.presentation

/**
 * SPWX-v0 / D-072: exactly eight Skill presentation states, one vocabulary for every Skill.
 * This projection lives in core rather than in the UI toolkit because it is the most
 * safety-critical labelling in the product — what a chip claims about the learner — and it
 * must be testable as a pure function (MSBX-v0 §presentation).
 */
enum class SkillPresentationState {
    REMEDIATION_REQUIRED,
    CONFIRMATION_VERIFICATION_DUE,
    CONFIRMED_REVIEW_DUE,
    CONFIRMED_CURRENT,
    PREREQUISITE_UNRESOLVED,
    DEVELOPING_INDEPENDENT,
    DEVELOPING_WITH_SUPPORT,
    NOT_YET_EVIDENCED,
    ;

    companion object {
        /**
         * Declared precedence from SPWX-v0. Enum order is the precedence order, so resolving a
         * multi-signal Skill is a deterministic pick rather than a judgement call.
         */
        val precedence: List<SkillPresentationState> = entries.toList()

        fun resolve(candidates: Set<SkillPresentationState>): SkillPresentationState =
            precedence.firstOrNull(candidates::contains) ?: NOT_YET_EVIDENCED
    }
}

/**
 * `at_risk` is an attention qualifier, never a ninth state (SPWX-v0). It rides alongside the
 * state instead of replacing it, so multi-axis truth is ordered rather than collapsed.
 */
data class SkillPresentation(
    val state: SkillPresentationState,
    val atRisk: Boolean = false,
)
