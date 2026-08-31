package coach.presentation

import kotlin.test.Test
import kotlin.test.assertEquals

/** SPWX-v0: eight states, one declared precedence, `at_risk` as a qualifier. */
class SkillPresentationTest {

    @Test
    fun `there are exactly eight states`() {
        assertEquals(8, SkillPresentationState.entries.size)
    }

    @Test
    fun `precedence resolves a multi signal skill deterministically`() {
        val candidates = setOf(
            SkillPresentationState.CONFIRMED_CURRENT,
            SkillPresentationState.REMEDIATION_REQUIRED,
            SkillPresentationState.DEVELOPING_INDEPENDENT,
        )
        assertEquals(SkillPresentationState.REMEDIATION_REQUIRED, SkillPresentationState.resolve(candidates))
    }

    @Test
    fun `at risk qualifies a state instead of replacing it`() {
        val presentation = SkillPresentation(SkillPresentationState.CONFIRMED_REVIEW_DUE, atRisk = true)
        assertEquals(SkillPresentationState.CONFIRMED_REVIEW_DUE, presentation.state)
    }
}
