package coach.engines

import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.IndependenceClass
import coach.model.RetentionAxis
import coach.model.RetentionEvent
import coach.model.RetentionPolicyV0
import coach.model.RetentionProfile
import coach.model.RetentionReason
import coach.model.RetentionSnapshot
import coach.model.StudyDays
import coach.model.VersionedRef

/**
 * The retention engine (13C, `RVR-v0 / D-032`): **when is it time to check a proven Skill again, and
 * has anything contradicted the proof?**
 *
 * It never decides mastery. The mastery engine says whether the Skill was mastered before and after each
 * evidence row; this engine only schedules re-verification around those decisions and reads what a
 * delayed check showed. Time moves nothing but the schedule: a due review is not forgetting, mastery does
 * not decay, and nothing locks because a day passed.
 *
 * The state is a pure function of the Skill's evidence, replayed in the order it was recorded, so it can
 * always be rebuilt from truth (`LFPS-v0`) and the same evidence always gives the same schedule.
 */
object RetentionEngine {

    const val MODEL = "RVRX-v0"

    /**
     * Replays a Skill's evidence ([events], in recording order) into `RVR-v0` §18's compact state.
     * [profile] `null` means the authored profile is unknown: the Skill's mastery is still tracked by the
     * mastery engine, but no interval is invented for it.
     */
    fun replay(skill: VersionedRef, profile: RetentionProfile?, critical: Boolean, events: List<RetentionEvent>): RetentionSnapshot {
        var state = RetentionSnapshot(skill, profile, critical, RetentionAxis.UNTRACKED)
        events.sortedBy { it.sequence }.forEach { event -> state = step(state, event) }
        return state
    }

    /** One evidence row's effect. Each branch is a sentence of `RVR-v0`. */
    fun step(state: RetentionSnapshot, event: RetentionEvent): RetentionSnapshot {
        if (!event.masteredBefore) {
            // §5/§10: a Skill becomes `fresh` the day it is (re-)mastered, with its initial interval.
            return if (event.masteredAfter) startFresh(state, event) else state
        }
        if (!event.masteredAfter) {
            // The mastery engine's gates no longer pass (§10 recheck FAIL). Retention stops tracking;
            // repair is remediation's (13D), and re-mastery starts a fresh schedule.
            val failure = event.clean && event.outcome == EvidenceOutcome.NEGATIVE
            val reasons = when {
                failure && state.state in OPEN_CONCERN -> listOf(RetentionReason.RETENTION_RECHECK_FAIL,
                    RetentionReason.REMEDIATION_AFTER_RETENTION_FAILURE)
                failure -> listOf(RetentionReason.REMEDIATION_AFTER_RETENTION_FAILURE)
                else -> emptyList()
            }
            return RetentionSnapshot(state.skill, state.profile, state.critical, RetentionAxis.UNTRACKED,
                lastRetentionEvidenceId = event.evidenceId, reasons = reasons)
        }
        if (state.profile == null) return state
        return when (state.state) {
            RetentionAxis.FRESH, RetentionAxis.STABLE -> scheduled(state, event)
            RetentionAxis.VERIFICATION_DUE -> verifying(state, event)
            RetentionAxis.AT_RISK -> atRisk(state, event)
            // `review_due` is never stored by a replay (it is derived on a day), and an untracked Skill
            // that the mastery engine already called mastered was started by the branch above.
            RetentionAxis.REVIEW_DUE, RetentionAxis.UNTRACKED, RetentionAxis.NOT_YET_EVALUATED -> state
        }
    }

    private val OPEN_CONCERN = setOf(RetentionAxis.VERIFICATION_DUE, RetentionAxis.AT_RISK)

    private fun startFresh(state: RetentionSnapshot, event: RetentionEvent): RetentionSnapshot {
        val profile = state.profile
            ?: return RetentionSnapshot(state.skill, null, state.critical, RetentionAxis.NOT_YET_EVALUATED,
                lastRetentionEvidenceId = event.evidenceId)
        val interval = RetentionPolicyV0.initialInterval(profile, state.critical)
        return RetentionSnapshot(
            skill = state.skill, profile = profile, critical = state.critical, state = RetentionAxis.FRESH,
            intervalDays = interval, nextReviewDay = StudyDays.plus(event.studyDay, interval),
            lastRetentionEvidenceId = event.evidenceId,
        )
    }

    /** §3 + §13: strong retention evidence, and whether it is a near repeat that cannot carry it alone. */
    private fun strongPositive(state: RetentionSnapshot, event: RetentionEvent): Boolean =
        event.clean && event.outcome == EvidenceOutcome.POSITIVE &&
            !(event.nearRepeat && (state.profile == RetentionProfile.COMPLEX || state.critical))

    /** §10: a partial result, or one an unverified evaluator gave, is uncertainty — not a pass, not a fail. */
    private fun uncertain(event: RetentionEvent): Boolean {
        val attributable = !event.contested && event.prerequisiteValid && !event.solutionExposed && event.direct &&
            event.independence == IndependenceClass.INDEPENDENT
        return attributable && (
            (event.evaluatorStatus == EvaluatorStatus.VERIFIED && event.outcome == EvidenceOutcome.PARTIAL) ||
                (event.evaluatorStatus == EvaluatorStatus.PROVISIONAL && event.outcome != EvidenceOutcome.INVALID)
            )
    }

    private fun cleanNegative(event: RetentionEvent): Boolean = event.clean && event.outcome == EvidenceOutcome.NEGATIVE

    private fun scheduled(state: RetentionSnapshot, event: RetentionEvent): RetentionSnapshot {
        val due = state.isDueOn(event.studyDay)
        return when {
            // §9: the first clean contradiction of a mastered Skill opens verification; it erases nothing.
            cleanNegative(event) -> state.copy(
                state = RetentionAxis.VERIFICATION_DUE,
                unresolvedVerificationEvidenceId = event.evidenceId,
                verificationFailureDay = event.studyDay,
                lastRetentionEvidenceId = event.evidenceId,
                reasons = listOfNotNull(RetentionReason.RETENTION_FAILURE_FIRST,
                    RetentionReason.DEPENDENT_PREREQ_VERIFICATION_REQUIRED.takeIf { state.critical }),
            )
            // §8 and §11: a strong check on or after the due day — planned or natural — is a successful
            // delayed review. The interval grows from the day it happened.
            strongPositive(state, event) && due -> {
                val interval = RetentionPolicyV0.grownInterval(state.intervalDays!!, state.critical)
                state.copy(
                    state = RetentionAxis.STABLE,
                    intervalDays = interval,
                    nextReviewDay = StudyDays.plus(event.studyDay, interval),
                    lastStrongRetentionDay = event.studyDay,
                    lastRetentionEvidenceId = event.evidenceId,
                    successfulDelayedReviews = state.successfulDelayedReviews + 1,
                    reasons = emptyList(),
                )
            }
            // §11: strong use long before the due day is good mastery evidence, but in V0 it does not move
            // the review clock.
            strongPositive(state, event) -> state.copy(
                lastNaturalReuseDay = event.studyDay,
                reasons = (state.reasons + RetentionReason.NATURAL_REUSE_VERIFIED).distinct(),
            )
            // §10: an uncertain answer to a due check is a concern, never a failure.
            uncertain(event) && due -> state.copy(
                state = RetentionAxis.AT_RISK,
                atRiskReasons = listOf(RetentionReason.RETENTION_AT_RISK),
                lastRetentionEvidenceId = event.evidenceId,
                reasons = listOf(RetentionReason.RETENTION_AT_RISK),
            )
            else -> state
        }
    }

    /**
     * §10. Only a **fresh** recheck (not the item or family that failed or any other already met) made at
     * least [RetentionPolicyV0.VERIFICATION_DELAY_DAYS] study days after the failure can resolve it.
     */
    private fun verifying(state: RetentionSnapshot, event: RetentionEvent): RetentionSnapshot {
        val separated = StudyDays.between(state.verificationFailureDay!!, event.studyDay) >= RetentionPolicyV0.VERIFICATION_DELAY_DAYS
        if (!separated) return state
        return when {
            event.clean && event.outcome == EvidenceOutcome.POSITIVE && !event.nearRepeat -> recheckPassed(state, event)
            // A second clean failure the mastery engine still survives keeps verification open; the one it
            // does not survive left through the mastery-lost branch.
            cleanNegative(event) -> state.copy(
                unresolvedVerificationEvidenceId = event.evidenceId,
                verificationFailureDay = event.studyDay,
                lastRetentionEvidenceId = event.evidenceId,
                reasons = listOfNotNull(RetentionReason.RETENTION_RECHECK_FAIL,
                    RetentionReason.DEPENDENT_PREREQ_VERIFICATION_REQUIRED.takeIf { state.critical }),
            )
            uncertain(event) -> state.copy(
                state = RetentionAxis.AT_RISK,
                atRiskReasons = listOf(RetentionReason.RETENTION_AT_RISK),
                lastRetentionEvidenceId = event.evidenceId,
                reasons = listOf(RetentionReason.RETENTION_AT_RISK),
            )
            else -> state
        }
    }

    /** §10, §15: a fresh verified success clears a concern; another clean failure reopens verification. */
    private fun atRisk(state: RetentionSnapshot, event: RetentionEvent): RetentionSnapshot = when {
        event.clean && event.outcome == EvidenceOutcome.POSITIVE && !event.nearRepeat -> recheckPassed(state, event)
        cleanNegative(event) -> state.copy(
            state = RetentionAxis.VERIFICATION_DUE,
            atRiskReasons = emptyList(),
            unresolvedVerificationEvidenceId = event.evidenceId,
            verificationFailureDay = event.studyDay,
            lastRetentionEvidenceId = event.evidenceId,
            reasons = listOfNotNull(RetentionReason.RETENTION_RECHECK_FAIL,
                RetentionReason.DEPENDENT_PREREQ_VERIFICATION_REQUIRED.takeIf { state.critical }),
        )
        else -> state
    }

    /** §10 recheck PASS: stable again, and the interval does **not** grow — it restarts from today. */
    private fun recheckPassed(state: RetentionSnapshot, event: RetentionEvent): RetentionSnapshot = state.copy(
        state = RetentionAxis.STABLE,
        nextReviewDay = StudyDays.plus(event.studyDay, state.intervalDays!!),
        lastStrongRetentionDay = event.studyDay,
        lastRetentionEvidenceId = event.evidenceId,
        unresolvedVerificationEvidenceId = null,
        verificationFailureDay = null,
        atRiskReasons = emptyList(),
        reasons = listOf(RetentionReason.RETENTION_RECHECK_PASS),
    )
}
