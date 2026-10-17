package coach.presentation

import coach.model.AttentionQualifier
import coach.model.SkillPresentationState

/**
 * SPWX-v0 / D-072: exactly eight Skill presentation states, one vocabulary for every Skill.
 *
 * The states and their derivation moved to `core-model` in 16A (`SKAX-v0`): the row assembly in `core-application`
 * writes the very state this layer projects, and `MSBX-v0` lets neither depend on the other. One definition is what
 * keeps a Skill from carrying two answers. The labelling stays here, a pure function as before (`MSBX-v0`
 * §presentation).
 *
 * `at_risk` is an attention qualifier, never a ninth state (SPWX-v0). It rides alongside the state instead of
 * replacing it, so multi-axis truth is ordered rather than collapsed.
 */
data class SkillPresentation(
    val state: SkillPresentationState,
    val qualifiers: List<AttentionQualifier> = emptyList(),
) {
    val atRisk: Boolean get() = AttentionQualifier.RETENTION_AT_RISK in qualifiers
}
