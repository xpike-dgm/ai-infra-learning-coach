package coach.engines

import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.IndependenceClass
import coach.model.RetentionAxis
import coach.model.RetentionEvent
import coach.model.RetentionProfile
import coach.model.RetentionReason
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RetentionEngineTest {

    private val skill = VersionedRef("skill.c.pointers", 1)
    private var seq = 0L

    private fun event(
        day: String,
        outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE,
        before: Boolean = true,
        after: Boolean = true,
        status: EvaluatorStatus = EvaluatorStatus.VERIFIED,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        direct: Boolean = true,
        nearRepeat: Boolean = false,
        prerequisiteValid: Boolean = true,
        solutionExposed: Boolean = false,
        contested: Boolean = false,
    ): RetentionEvent {
        seq += 1
        return RetentionEvent(seq, seq, day, outcome, status, independence, contested, prerequisiteValid, solutionExposed,
            direct, nearRepeat, VersionedRef("item.$seq", 1), before, after)
    }

    private fun mastered(day: String) = event(day, before = false, after = true)

    private fun replay(vararg events: RetentionEvent, profile: RetentionProfile? = RetentionProfile.STANDARD, critical: Boolean = false) =
        RetentionEngine.replay(skill, profile, critical, events.toList())

    @Test
    fun `a Skill that was never mastered is untracked`() {
        assertEquals(RetentionAxis.UNTRACKED, replay().state)
        assertEquals(RetentionAxis.UNTRACKED, replay(event("2026-10-01", before = false, after = false)).state)
    }

    @Test
    fun `mastery starts a fresh schedule with the profile's initial interval`() {
        val standard = replay(mastered("2026-10-01"))
        assertEquals(RetentionAxis.FRESH, standard.state)
        assertEquals(4, standard.intervalDays)
        assertEquals("2026-10-05", standard.nextReviewDay)
        assertEquals("2026-10-03", replay(mastered("2026-10-01"), profile = RetentionProfile.FACTUAL).nextReviewDay)
        assertEquals("2026-10-08", replay(mastered("2026-10-01"), profile = RetentionProfile.COMPLEX).nextReviewDay)
        assertEquals("2026-10-04", replay(mastered("2026-10-01"), profile = RetentionProfile.COMPLEX, critical = true).nextReviewDay)
    }

    @Test
    fun `an unknown profile gets no invented interval`() {
        val unknown = replay(mastered("2026-10-01"), profile = null)
        assertEquals(RetentionAxis.NOT_YET_EVALUATED, unknown.state)
        assertNull(unknown.nextReviewDay)
        assertEquals(RetentionAxis.NOT_YET_EVALUATED, unknown.axisOn("2027-01-01"))
    }

    @Test
    fun `a year with no evidence makes a review due and nothing worse`() {
        val quiet = replay(mastered("2026-10-01"))
        assertEquals(RetentionAxis.FRESH, quiet.state)
        assertEquals(RetentionAxis.REVIEW_DUE, quiet.axisOn("2027-10-01"))
        assertTrue(quiet.reasonsOn("2027-10-01").none { it == RetentionReason.RETENTION_AT_RISK || it == RetentionReason.RETENTION_FAILURE_FIRST })
    }

    @Test
    fun `a strong check on the due day makes the Skill stable and the interval grows`() {
        val first = replay(mastered("2026-10-01"), event("2026-10-05"))
        assertEquals(RetentionAxis.STABLE, first.state)
        assertEquals(8, first.intervalDays)
        assertEquals("2026-10-13", first.nextReviewDay)
        assertEquals(1, first.successfulDelayedReviews)
        assertEquals("2026-10-05", first.lastStrongRetentionDay)
        val critical = replay(mastered("2026-10-01"), event("2026-10-04"), profile = RetentionProfile.COMPLEX, critical = true)
        assertEquals(4, critical.intervalDays) // 3 x 1.6 rounds down
    }

    @Test
    fun `strong use before the due day is reuse and does not move the clock`() {
        val early = replay(mastered("2026-10-01"), event("2026-10-03"))
        assertEquals(RetentionAxis.FRESH, early.state)
        assertEquals("2026-10-05", early.nextReviewDay)
        assertEquals("2026-10-03", early.lastNaturalReuseDay)
        assertTrue(RetentionReason.NATURAL_REUSE_VERIFIED in early.reasons)
    }

    @Test
    fun `only clean independent verified direct work counts as a review`() {
        // Each weak check is built after its mastery row, so it is replayed after it: a check recorded before
        // mastery would be ignored for that reason alone and prove nothing about cleanliness.
        listOf<() -> RetentionEvent>(
            { event("2026-10-05", independence = IndependenceClass.ASSISTED) },
            { event("2026-10-05", direct = false) },
            { event("2026-10-05", prerequisiteValid = false) },
            { event("2026-10-05", solutionExposed = true) },
            { event("2026-10-05", contested = true) },
        ).forEach { build ->
            val mastery = mastered("2026-10-01")
            val weak = build()
            val state = replay(mastery, weak)
            assertEquals(RetentionAxis.FRESH, state.state, weak.toString())
            assertEquals(0, state.successfulDelayedReviews)
            // The same check, clean, is a review: the weak one failed only on the rule it breaks.
            assertEquals(RetentionAxis.STABLE, replay(mastered("2026-10-01"), event("2026-10-05")).state)
        }
    }

    @Test
    fun `a near repeat cannot carry a complex or critical review alone`() {
        assertEquals(RetentionAxis.FRESH, replay(mastered("2026-10-01"), event("2026-10-08", nearRepeat = true),
            profile = RetentionProfile.COMPLEX).state)
        assertEquals(RetentionAxis.FRESH, replay(mastered("2026-10-01"), event("2026-10-05", nearRepeat = true),
            critical = true).state)
        assertEquals(RetentionAxis.STABLE, replay(mastered("2026-10-01"), event("2026-10-05", nearRepeat = true)).state)
    }

    @Test
    fun `the first clean contradiction opens verification and erases nothing`() {
        val failed = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.NEGATIVE))
        assertEquals(RetentionAxis.VERIFICATION_DUE, failed.state)
        assertEquals(listOf(RetentionReason.RETENTION_FAILURE_FIRST), failed.reasons)
        assertEquals(4, failed.intervalDays)
        assertEquals(RetentionAxis.VERIFICATION_DUE, failed.axisOn("2027-01-01"))
        val critical = replay(mastered("2026-10-01"), event("2026-10-04", EvidenceOutcome.NEGATIVE), critical = true)
        assertTrue(RetentionReason.DEPENDENT_PREREQ_VERIFICATION_REQUIRED in critical.reasons)
    }

    @Test
    fun `a fresh recheck a day later passes, without growing the interval`() {
        val sameDay = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.NEGATIVE), event("2026-10-05"))
        assertEquals(RetentionAxis.VERIFICATION_DUE, sameDay.state)
        val nearRepeat = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.NEGATIVE), event("2026-10-07", nearRepeat = true))
        assertEquals(RetentionAxis.VERIFICATION_DUE, nearRepeat.state)
        val passed = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.NEGATIVE), event("2026-10-06"))
        assertEquals(RetentionAxis.STABLE, passed.state)
        assertEquals(4, passed.intervalDays)
        assertEquals("2026-10-10", passed.nextReviewDay)
        assertNull(passed.unresolvedVerificationEvidenceId)
        assertEquals(listOf(RetentionReason.RETENTION_RECHECK_PASS), passed.reasons)
    }

    @Test
    fun `a second clean failure keeps verification open while mastery holds, and ends tracking when it does not`() {
        val holds = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.NEGATIVE), event("2026-10-06", EvidenceOutcome.NEGATIVE))
        assertEquals(RetentionAxis.VERIFICATION_DUE, holds.state)
        assertEquals(listOf(RetentionReason.RETENTION_RECHECK_FAIL), holds.reasons)
        val lost = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.NEGATIVE),
            event("2026-10-06", EvidenceOutcome.NEGATIVE, after = false))
        assertEquals(RetentionAxis.UNTRACKED, lost.state)
        assertEquals(listOf(RetentionReason.RETENTION_RECHECK_FAIL, RetentionReason.REMEDIATION_AFTER_RETENTION_FAILURE), lost.reasons)
        val remastered = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.NEGATIVE),
            event("2026-10-06", EvidenceOutcome.NEGATIVE, after = false), event("2026-10-20", before = false, after = true))
        assertEquals(RetentionAxis.FRESH, remastered.state)
        assertEquals(4, remastered.intervalDays)
        assertEquals("2026-10-24", remastered.nextReviewDay)
    }

    @Test
    fun `uncertainty on a due check is a concern, and a fresh success clears it`() {
        val partial = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.PARTIAL))
        assertEquals(RetentionAxis.AT_RISK, partial.state)
        assertEquals(listOf(RetentionReason.RETENTION_AT_RISK), partial.atRiskReasons)
        val provisional = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.NEGATIVE),
            event("2026-10-06", status = EvaluatorStatus.PROVISIONAL))
        assertEquals(RetentionAxis.AT_RISK, provisional.state)
        val cleared = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.PARTIAL), event("2026-10-06"))
        assertEquals(RetentionAxis.STABLE, cleared.state)
        val reopened = replay(mastered("2026-10-01"), event("2026-10-05", EvidenceOutcome.PARTIAL), event("2026-10-06", EvidenceOutcome.NEGATIVE))
        assertEquals(RetentionAxis.VERIFICATION_DUE, reopened.state)
        // A partial result long before any review is due is ordinary evidence, not a retention concern.
        assertEquals(RetentionAxis.FRESH, replay(mastered("2026-10-01"), event("2026-10-02", EvidenceOutcome.PARTIAL)).state)
    }

    @Test
    fun `the replay is the same whatever order the events arrive in`() {
        val events = listOf(mastered("2026-10-01"), event("2026-10-05"), event("2026-10-13", EvidenceOutcome.NEGATIVE), event("2026-10-15"))
        assertEquals(RetentionEngine.replay(skill, RetentionProfile.STANDARD, false, events),
            RetentionEngine.replay(skill, RetentionProfile.STANDARD, false, events.reversed()))
    }
}
