package coach.model

import java.time.LocalDate

/**
 * `RVR-v0 / D-032` as the retention engine reads and writes it (13C).
 *
 * Two principles carry everything here. **Time passing is not negative evidence**: a Skill whose review
 * is due has not been forgotten, its mastery does not decay, and nothing locks. **Mastery and retention
 * are two axes**: mastery asks "was it proven?", retention asks "is it time to check that proof again,
 * or has something contradicted it?".
 */

/** `RVR-v0` §5: a Skill's authored retention profile. `critical_prerequisite` is a separate overlay. */
enum class RetentionProfile(val id: String) {
    FACTUAL("factual"),
    STANDARD("standard"),
    COMPLEX("complex"),
    ;

    companion object {
        /** An unknown profile is not guessed into one: the engine schedules nothing for it. */
        fun of(id: String?): RetentionProfile? = entries.firstOrNull { it.id == id }
    }
}

/**
 * `RVR-v0` §5, §6, §9 and §20's V0 configuration. **Every number here is an engineering heuristic that
 * needs calibration** (`RVR-v0` §20, owner 18C): none is a probability, a half-life or a forgetting
 * curve, and none was chosen here — they are the accepted spec's own V0 values.
 */
object RetentionPolicyV0 {
    const val POLICY_VERSION = "RVR-v0"

    /** §5 `initial_review_days_v0`. */
    fun initialReviewDays(profile: RetentionProfile): Int = when (profile) {
        RetentionProfile.FACTUAL -> 2
        RetentionProfile.STANDARD -> 4
        RetentionProfile.COMPLEX -> 7
    }

    /** §5 `critical_initial_review_cap_days_v0`. */
    const val CRITICAL_INITIAL_REVIEW_CAP_DAYS = 3

    /** §6 growth factors and caps. */
    const val STANDARD_GROWTH_FACTOR = 2.0
    const val CRITICAL_GROWTH_FACTOR = 1.6
    const val STANDARD_MAX_INTERVAL_DAYS = 180
    const val CRITICAL_MAX_INTERVAL_DAYS = 90

    /**
     * §9 `verification_delay_days_v0`. It is a separation between a first failure and the recheck that
     * can resolve it, never a lock on the learner: the planner may offer the recheck whenever it fits,
     * and only a recheck at least this many study days later resolves the contradiction.
     */
    const val VERIFICATION_DELAY_DAYS = 1

    fun initialInterval(profile: RetentionProfile, critical: Boolean): Int {
        val base = initialReviewDays(profile)
        return if (critical) minOf(base, CRITICAL_INITIAL_REVIEW_CAP_DAYS) else base
    }

    /**
     * §6 `next_interval = min(current_interval * growth_factor, max_interval)`, in whole study days. The
     * product is rounded **down**: a shorter interval asks for an earlier check, never a later one, and
     * with every initial interval at least two days the interval still grows at every step.
     */
    fun grownInterval(current: Int, critical: Boolean): Int {
        val factor = if (critical) CRITICAL_GROWTH_FACTOR else STANDARD_GROWTH_FACTOR
        val cap = if (critical) CRITICAL_MAX_INTERVAL_DAYS else STANDARD_MAX_INTERVAL_DAYS
        return minOf(kotlin.math.floor(current * factor).toInt(), cap)
    }
}

/** `RVR-v0` §17's reason codes, in its order. A code names what happened, never a probability. */
enum class RetentionReason(val id: String) {
    FIRST_DELAYED_REVIEW("FIRST_DELAYED_REVIEW"),
    REVIEW_DUE("REVIEW_DUE"),
    CRITICAL_REVIEW_DUE("CRITICAL_REVIEW_DUE"),
    NATURAL_REUSE_VERIFIED("NATURAL_REUSE_VERIFIED"),
    RETENTION_FAILURE_FIRST("RETENTION_FAILURE_FIRST"),
    RETENTION_RECHECK_PASS("RETENTION_RECHECK_PASS"),
    RETENTION_RECHECK_FAIL("RETENTION_RECHECK_FAIL"),
    RETENTION_AT_RISK("RETENTION_AT_RISK"),
    REMEDIATION_AFTER_RETENTION_FAILURE("REMEDIATION_AFTER_RETENTION_FAILURE"),
    OVERDUE_REPRESENTATIVE_CHECK("OVERDUE_REPRESENTATIVE_CHECK"),
    DEPENDENT_PREREQ_VERIFICATION_REQUIRED("DEPENDENT_PREREQ_VERIFICATION_REQUIRED"),
}

/**
 * One evidence row as the retention engine reads it, in the order it was recorded, with the mastery
 * decision the mastery engine reached once this row existed.
 *
 * [direct] is whether the row's evidence type is direct for its Objective; [nearRepeat] whether it
 * repeats an item or variant family this Skill already met (`RVR-v0` §13). Both are facts about the row,
 * computed by whoever reads the store, never guessed here.
 */
data class RetentionEvent(
    val evidenceId: Long,
    val sequence: Long,
    val studyDay: String,
    val outcome: EvidenceOutcome,
    val evaluatorStatus: EvaluatorStatus,
    val independence: IndependenceClass,
    val contested: Boolean,
    val prerequisiteValid: Boolean,
    val solutionExposed: Boolean,
    val direct: Boolean,
    val nearRepeat: Boolean,
    val resource: VersionedRef?,
    val masteredBefore: Boolean,
    val masteredAfter: Boolean,
) {
    /** `RVR-v0` §3 minus the delay: attributable, prerequisite-valid, H0, direct and verified. */
    val clean: Boolean
        get() = !contested && prerequisiteValid && !solutionExposed && direct &&
            evaluatorStatus == EvaluatorStatus.VERIFIED && independence == IndependenceClass.INDEPENDENT
}

/**
 * `RVR-v0` §18's compact sufficient state. [state] is the scheduled state: `fresh` and `stable` become
 * `review_due` only by [axisOn], when a study day reaches [nextReviewDay] — the stored row never pretends
 * that time passing changed anything but the schedule.
 */
data class RetentionSnapshot(
    val skill: VersionedRef,
    val profile: RetentionProfile?,
    val critical: Boolean,
    val state: RetentionAxis,
    val intervalDays: Int? = null,
    val nextReviewDay: String? = null,
    val lastStrongRetentionDay: String? = null,
    val lastRetentionEvidenceId: Long? = null,
    val successfulDelayedReviews: Int = 0,
    val unresolvedVerificationEvidenceId: Long? = null,
    val verificationFailureDay: String? = null,
    val atRiskReasons: List<RetentionReason> = emptyList(),
    val lastNaturalReuseDay: String? = null,
    val reasons: List<RetentionReason> = emptyList(),
) {
    init {
        val scheduled = state == RetentionAxis.FRESH || state == RetentionAxis.STABLE || state == RetentionAxis.REVIEW_DUE
        require(!scheduled || (intervalDays != null && intervalDays > 0 && nextReviewDay != null)) {
            "a scheduled Skill knows its interval and its next review day"
        }
        require(state != RetentionAxis.VERIFICATION_DUE || unresolvedVerificationEvidenceId != null) {
            "verification is due because of one named contradiction"
        }
        require(state != RetentionAxis.AT_RISK || atRiskReasons.isNotEmpty()) { "at risk says why, never because time passed" }
    }

    /**
     * The retention axis on [today] (`RVR-v0` §7): `now >= next_review_at` makes a scheduled Skill
     * `review_due`. Nothing else changes with time — not mastery, not verification, not risk.
     */
    fun axisOn(today: String): RetentionAxis =
        if ((state == RetentionAxis.FRESH || state == RetentionAxis.STABLE) && isDueOn(today)) RetentionAxis.REVIEW_DUE
        else state

    fun isDueOn(today: String): Boolean =
        nextReviewDay != null && !LocalDate.parse(today).isBefore(LocalDate.parse(nextReviewDay))

    /** The reasons a reader sees on [today]: the stored ones plus `review_due` when the day has come. */
    fun reasonsOn(today: String): List<RetentionReason> {
        if (axisOn(today) != RetentionAxis.REVIEW_DUE) return reasons
        val due = if (critical) RetentionReason.CRITICAL_REVIEW_DUE else RetentionReason.REVIEW_DUE
        val first = if (successfulDelayedReviews == 0) listOf(RetentionReason.FIRST_DELAYED_REVIEW) else emptyList()
        return (first + due + reasons.filter { it == RetentionReason.RETENTION_RECHECK_PASS }).distinct()
    }
}

/** Whole study days between two learner-local study days; never computed from an instant. */
object StudyDays {
    fun plus(studyDay: String, days: Int): String = LocalDate.parse(studyDay).plusDays(days.toLong()).toString()

    fun between(from: String, to: String): Long =
        java.time.temporal.ChronoUnit.DAYS.between(LocalDate.parse(from), LocalDate.parse(to))
}
