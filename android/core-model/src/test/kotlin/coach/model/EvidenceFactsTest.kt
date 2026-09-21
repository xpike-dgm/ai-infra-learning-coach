package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** What an evidence row and an Objective's gate profile may say at all (12A). */
class EvidenceFactsTest {

    private val objective = VersionedRef("objective.python.loops.trace", 1)

    private fun row(quality: Double?) = EvidenceRow(
        id = 1, sequence = 1, objective = objective, skill = VersionedRef("skill.python.loops", 1),
        evidenceType = "code_reading", outcome = EvidenceOutcome.POSITIVE,
        evaluatorStatus = EvaluatorStatus.VERIFIED, independenceClass = IndependenceClass.INDEPENDENT,
        contested = false, quality = quality, difficulty = null, variantFamilyId = "family.a",
    )

    @Test
    fun `the four axes are DDM-v0's value sets`() {
        assertEquals(listOf("positive", "negative", "partial", "invalid"), EvidenceOutcome.entries.map { it.id })
        assertEquals(listOf("verified", "provisional", "invalid"), EvaluatorStatus.entries.map { it.id })
        assertEquals(
            listOf("independent", "assisted", "practice_only", "requires_independent_recheck"),
            IndependenceClass.entries.map { it.id },
        )
    }

    @Test
    fun `difficulty is a label, and only the basic one is basic`() {
        assertEquals(listOf("basic", "authentic_application", "transfer_integration"), DifficultyClass.entries.map { it.id })
        assertEquals(
            listOf(DifficultyClass.BASIC),
            DifficultyClass.entries.filter { it.isBasic },
        )
    }

    @Test
    fun `a group result outside the unit interval cannot be constructed`() {
        assertFailsWith<IllegalArgumentException> { row(1.5) }
        assertFailsWith<IllegalArgumentException> { row(-0.1) }
        // A row with no result at all is legitimate: it says nothing was measured.
        row(null)
        row(0.0)
        row(1.0)
    }

    @Test
    fun `a gate profile cannot ask for fewer than one group or family`() {
        assertFailsWith<IllegalArgumentException> {
            ObjectiveGateProfile(objective, required = true, critical = false,
                acceptableEvidenceTypes = listOf("code_reading"), directEvidenceTypes = listOf("code_reading"),
                minIndependentGroups = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            ObjectiveGateProfile(objective, required = true, critical = false,
                acceptableEvidenceTypes = listOf("code_reading"), directEvidenceTypes = listOf("code_reading"),
                minVariantFamilies = 0)
        }
    }

    @Test
    fun `the mastery axis values are SPWX-v0's, and none of them is a percentage`() {
        assertEquals(
            listOf("not_yet_evidenced", "developing_with_support", "developing_independent",
                "confirmed_current", "confirmation_verification_due"),
            MasteryAxisState.entries.map { it.id },
        )
    }
}
