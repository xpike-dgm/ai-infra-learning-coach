package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WeaknessFactsTest {

    private val skill = VersionedRef("skill.c.pointers", 1)
    private val objective = VersionedRef("objective.c.pointers.trace", 1)

    @Test
    fun `the lifecycle, the attribution outcomes and the twelve rules are WLRM-v0's, in order`() {
        assertEquals(listOf("none", "hypothesis", "supported", "confirmed", "resolved"), WeaknessSignal.entries.map { it.id })
        assertEquals(listOf("not_attributable", "content_or_environment_issue", "prerequisite_signal", "objective_weakness_hypothesis",
            "objective_weakness_supported", "verification_due", "remediation_required", "positive_recovery_evidence"),
            AttributionOutcome.entries.map { it.id })
        assertEquals((1..12).map { it * 10 }, FailureRule.entries.map { it.priority })
        assertEquals("failure.invalid_or_ambiguous", FailureRule.entries.first().id)
        assertEquals("failure.fresh_recovery_success", FailureRule.entries.last().id)
    }

    @Test
    fun `a Skill's weakness is the strongest open signal of its Objectives, and confirmed reads as remediation`() {
        fun w(signal: WeaknessSignal) = ObjectiveWeakness(objective, skill, signal,
            signalEvidenceIds = if (signal == WeaknessSignal.NONE || signal == WeaknessSignal.RESOLVED) emptyList() else listOf(1),
            firstSeenDay = "2026-10-01", resolutionEvidenceId = if (signal == WeaknessSignal.RESOLVED) 2 else null)
        assertEquals(WeaknessAxis.NONE, WeaknessAxis.of(emptyList()))
        assertEquals(WeaknessAxis.REMEDIATION_REQUIRED, WeaknessAxis.of(listOf(w(WeaknessSignal.HYPOTHESIS), w(WeaknessSignal.CONFIRMED))))
        assertEquals(WeaknessAxis.SUPPORTED, WeaknessAxis.of(listOf(w(WeaknessSignal.RESOLVED), w(WeaknessSignal.SUPPORTED))))
        // A supported weakness outranks a hypothesis on another Objective, in whichever order they come.
        assertEquals(WeaknessAxis.SUPPORTED, WeaknessAxis.of(listOf(w(WeaknessSignal.HYPOTHESIS), w(WeaknessSignal.SUPPORTED))))
        assertEquals(WeaknessAxis.SUPPORTED, WeaknessAxis.of(listOf(w(WeaknessSignal.SUPPORTED), w(WeaknessSignal.HYPOTHESIS))))
        assertEquals(WeaknessAxis.RESOLVED, WeaknessAxis.of(listOf(w(WeaknessSignal.RESOLVED), w(WeaknessSignal.NONE))))
        // The one value the prerequisite gate and the planner already read as open repair.
        assertEquals("remediation_required", WeaknessAxis.REMEDIATION_REQUIRED.id)
    }

    @Test
    fun `a weakness that cannot name its evidence, or is resolved without evidence, is not representable`() {
        assertFailsWith<IllegalArgumentException> { ObjectiveWeakness(objective, skill, WeaknessSignal.SUPPORTED) }
        assertFailsWith<IllegalArgumentException> { ObjectiveWeakness(objective, skill, WeaknessSignal.RESOLVED) }
        assertFailsWith<IllegalArgumentException> {
            ObjectiveWeakness(objective, skill, WeaknessSignal.CONFIRMED, verificationOpen = true, signalEvidenceIds = listOf(1), firstSeenDay = "2026-10-01")
        }
    }

    @Test
    fun `a closing check must be fresh - not the item or the family that showed the weakness`() {
        val open = ObjectiveWeakness(objective, skill, WeaknessSignal.SUPPORTED, signalEvidenceIds = listOf(1),
            signalResources = listOf(VersionedRef("item.a", 1)), signalFamilies = listOf("fam.a"), firstSeenDay = "2026-10-01")
        fun event(resource: String?, family: String?) = WeaknessEvent(9, 9, "2026-10-02", EvidenceOutcome.POSITIVE, EvaluatorStatus.VERIFIED,
            IndependenceClass.INDEPENDENT, false, true, false, true, resource?.let { VersionedRef(it, 1) }, family, false, false)
        assertFalse(open.isFresh(event("item.a", "fam.b")))
        assertFalse(open.isFresh(event("item.b", "fam.a")))
        assertTrue(open.isFresh(event("item.b", "fam.b")))
    }

    @Test
    fun `a disposition is read, never written over the row - the newest decides and reinstated restores`() {
        val row = EvidenceRow(1, 1, objective, skill, "code_reading", EvidenceOutcome.NEGATIVE, EvaluatorStatus.VERIFIED,
            IndependenceClass.INDEPENDENT, contested = false, quality = 0.0, difficulty = null, variantFamilyId = "fam.a")
        assertEquals(row, EvidenceDispositions.effective(row, null, null))
        assertEquals(row, EvidenceDispositions.effective(row, "reinstated", "appeal_upheld"))
        assertTrue(EvidenceDispositions.effective(row, "contested", "user_report").contested)
        assertFalse(EvidenceDispositions.effective(row, "invalidated", "prerequisite_contaminated").prerequisiteValid)
        assertEquals(EvaluatorStatus.INVALID, EvidenceDispositions.effective(row, "invalidated", "answer_key_error").evaluatorStatus)
        assertEquals(EvaluatorStatus.INVALID, EvidenceDispositions.effective(row, "superseded", "regraded").evaluatorStatus)
        // Only what the disposition says changes; the outcome the learner produced is never rewritten.
        assertEquals(EvidenceOutcome.NEGATIVE, EvidenceDispositions.effective(row, "invalidated", "answer_key_error").outcome)
    }
}
