package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class RetentionFactsTest {

    private val skill = VersionedRef("skill.c.pointers", 1)

    @Test
    fun `the V0 numbers are RVR-v0 section 20's, and nothing else`() {
        assertEquals(2, RetentionPolicyV0.initialReviewDays(RetentionProfile.FACTUAL))
        assertEquals(4, RetentionPolicyV0.initialReviewDays(RetentionProfile.STANDARD))
        assertEquals(7, RetentionPolicyV0.initialReviewDays(RetentionProfile.COMPLEX))
        assertEquals(3, RetentionPolicyV0.CRITICAL_INITIAL_REVIEW_CAP_DAYS)
        assertEquals(2.0, RetentionPolicyV0.STANDARD_GROWTH_FACTOR)
        assertEquals(1.6, RetentionPolicyV0.CRITICAL_GROWTH_FACTOR)
        assertEquals(180, RetentionPolicyV0.STANDARD_MAX_INTERVAL_DAYS)
        assertEquals(90, RetentionPolicyV0.CRITICAL_MAX_INTERVAL_DAYS)
        assertEquals(1, RetentionPolicyV0.VERIFICATION_DELAY_DAYS)
        assertEquals("RVR-v0", RetentionPolicyV0.POLICY_VERSION)
    }

    @Test
    fun `a critical Skill's first review is capped, and growth rounds down and stops at the cap`() {
        assertEquals(3, RetentionPolicyV0.initialInterval(RetentionProfile.COMPLEX, critical = true))
        assertEquals(2, RetentionPolicyV0.initialInterval(RetentionProfile.FACTUAL, critical = true))
        assertEquals(7, RetentionPolicyV0.initialInterval(RetentionProfile.COMPLEX, critical = false))
        assertEquals(8, RetentionPolicyV0.grownInterval(4, critical = false))
        assertEquals(4, RetentionPolicyV0.grownInterval(3, critical = true)) // 4.8 rounds down
        assertEquals(3, RetentionPolicyV0.grownInterval(2, critical = true)) // still grows
        assertEquals(180, RetentionPolicyV0.grownInterval(128, critical = false))
        assertEquals(90, RetentionPolicyV0.grownInterval(80, critical = true))
    }

    @Test
    fun `an unknown profile is not guessed`() {
        assertNull(RetentionProfile.of("medium"))
        assertNull(RetentionProfile.of(null))
        assertEquals(RetentionProfile.COMPLEX, RetentionProfile.of("complex"))
    }

    @Test
    fun `time moves a schedule to review_due and nothing else`() {
        val fresh = RetentionSnapshot(skill, RetentionProfile.STANDARD, false, RetentionAxis.FRESH, 4, "2026-10-05")
        assertEquals(RetentionAxis.FRESH, fresh.axisOn("2026-10-04"))
        assertEquals(RetentionAxis.REVIEW_DUE, fresh.axisOn("2026-10-05"))
        assertEquals(RetentionAxis.REVIEW_DUE, fresh.axisOn("2027-10-05"))
        val verifying = fresh.copy(state = RetentionAxis.VERIFICATION_DUE, unresolvedVerificationEvidenceId = 9)
        assertEquals(RetentionAxis.VERIFICATION_DUE, verifying.axisOn("2027-10-05"))
        val risk = fresh.copy(state = RetentionAxis.AT_RISK, atRiskReasons = listOf(RetentionReason.RETENTION_AT_RISK))
        assertEquals(RetentionAxis.AT_RISK, risk.axisOn("2027-10-05"))
        assertEquals(listOf(RetentionReason.FIRST_DELAYED_REVIEW, RetentionReason.REVIEW_DUE), fresh.reasonsOn("2026-10-05"))
        assertEquals(listOf(RetentionReason.CRITICAL_REVIEW_DUE),
            fresh.copy(critical = true, successfulDelayedReviews = 2).reasonsOn("2026-10-05"))
    }

    @Test
    fun `a state that cannot say why is not representable`() {
        assertFailsWith<IllegalArgumentException> { RetentionSnapshot(skill, RetentionProfile.STANDARD, false, RetentionAxis.STABLE) }
        assertFailsWith<IllegalArgumentException> { RetentionSnapshot(skill, RetentionProfile.STANDARD, false, RetentionAxis.VERIFICATION_DUE) }
        assertFailsWith<IllegalArgumentException> { RetentionSnapshot(skill, RetentionProfile.STANDARD, false, RetentionAxis.AT_RISK) }
    }

    @Test
    fun `study days cross months and years by the calendar`() {
        assertEquals("2026-11-02", StudyDays.plus("2026-10-30", 3))
        assertEquals("2027-01-03", StudyDays.plus("2026-12-31", 3))
        assertEquals(365, StudyDays.between("2026-01-01", "2027-01-01"))
    }

    @Test
    fun `the reason codes are RVR-v0 section 17's, in order`() {
        assertEquals(listOf("FIRST_DELAYED_REVIEW", "REVIEW_DUE", "CRITICAL_REVIEW_DUE", "NATURAL_REUSE_VERIFIED",
            "RETENTION_FAILURE_FIRST", "RETENTION_RECHECK_PASS", "RETENTION_RECHECK_FAIL", "RETENTION_AT_RISK",
            "REMEDIATION_AFTER_RETENTION_FAILURE", "OVERDUE_REPRESENTATIVE_CHECK", "DEPENDENT_PREREQ_VERIFICATION_REQUIRED"),
            RetentionReason.entries.map { it.id })
    }
}
