package coach.engines

import coach.model.AttributionOutcome
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.EvidenceSeverity
import coach.model.FailureRule
import coach.model.IndependenceClass
import coach.model.LearningNeed
import coach.model.MasteryAxisState
import coach.model.NeedTrigger
import coach.model.ObjectiveWeakness
import coach.model.SkillPlanningState
import coach.model.TemporalUrgency
import coach.model.VersionedRef
import coach.model.WeaknessAxis
import coach.model.WeaknessEvent
import coach.model.WeaknessSignal

/**
 * The weakness and remediation engine (13D, `WLRM-v0 / D-061`): **which Objective does this evidence
 * really say something about, and how sure is it?**
 *
 * It decides no mastery. The mastery engine says whether the Skill was mastered before and after each row;
 * this engine attributes each row by `WLRM-v0`'s failure-attribution rules, in priority order, and keeps an
 * Objective-local signal (`none → hypothesis → supported → confirmed → resolved`). A Skill's weakness axis is
 * only what its Objectives show; nothing here reaches a Topic, a Domain or another Skill.
 *
 * Everything is a pure function of the Objective's evidence in recording order, so the state can always be
 * rebuilt from truth.
 */
object WeaknessEngine {

    const val MODEL = "WLRX-v0"
    const val POLICY_VERSION = "WLRM-v0"

    /** The rule that decides a row, the first in `WLRM-v0`'s priority order that matches. `null`: no rule speaks. */
    fun rule(state: ObjectiveWeakness, event: WeaknessEvent): FailureRule? = when {
        event.outcome == EvidenceOutcome.INVALID || event.evaluatorStatus == EvaluatorStatus.INVALID || event.contested ->
            FailureRule.INVALID_OR_AMBIGUOUS
        !event.prerequisiteValid -> FailureRule.PREREQUISITE_CONTAMINATION
        event.outcome == EvidenceOutcome.POSITIVE ->
            if (event.clean && state.isFresh(event)) FailureRule.FRESH_RECOVERY_SUCCESS else null
        // Narrowed at 13F (`VDW-v0` §12.1, user decision): a failure on a diagnostic for an Objective never learned
        // here answers "did you already know it?", not "is something weak?". It is kept as evidence and the
        // Objective returns to normal learning; no weakness signal opens and nothing is blamed.
        event.diagnosticBaseline && !event.masteredBefore -> null
        // A failure with help taken or a solution seen says something only about dependence on help.
        event.independence != IndependenceClass.INDEPENDENT || event.solutionExposed -> FailureRule.ASSISTED_H1_H4
        // Uncertain: a partial result, an evaluator that is not verified, or evidence that is not direct for
        // this Objective — corroboration, never confirmation.
        event.evaluatorStatus != EvaluatorStatus.VERIFIED || event.outcome == EvidenceOutcome.PARTIAL || !event.direct ->
            FailureRule.PROVISIONAL_OR_PARTIAL
        !event.masteredBefore -> FailureRule.CLEAN_PREMASTERY_H0_DIRECT
        !state.verificationOpen && event.masteredAfter -> FailureRule.FIRST_CLEAN_POSTMASTERY_CONTRADICTION
        // `failed_fresh_recheck_or_GRE_gate_failure`: when the mastery engine's gates no longer pass, the
        // failure is confirmed whatever item showed it.
        !event.masteredAfter -> FailureRule.FRESH_RECHECK_FAIL
        state.isFresh(event) -> FailureRule.FRESH_RECHECK_FAIL
        // The same item or family failing again is not a fresh recheck: verification stays open.
        else -> null
    }

    /** Replays one Objective's evidence ([events] in recording order) into its weakness state. */
    fun replay(objective: VersionedRef, skill: VersionedRef, events: List<WeaknessEvent>): ObjectiveWeakness {
        var state = ObjectiveWeakness(objective, skill)
        events.sortedBy { it.sequence }.forEach { state = step(state, it) }
        return state
    }

    fun step(state: ObjectiveWeakness, event: WeaknessEvent): ObjectiveWeakness {
        val rule = rule(state, event) ?: return state
        return when (rule) {
            FailureRule.INVALID_OR_AMBIGUOUS -> state.copy(lastOutcome = AttributionOutcome.NOT_ATTRIBUTABLE, lastRule = rule)
            // The target is not blamed; the missing prerequisite is the prerequisite's own need.
            FailureRule.PREREQUISITE_CONTAMINATION -> state.copy(lastOutcome = AttributionOutcome.PREREQUISITE_SIGNAL, lastRule = rule)
            FailureRule.ASSISTED_H1_H4, FailureRule.PROVISIONAL_OR_PARTIAL ->
                raise(state, event, WeaknessSignal.HYPOTHESIS, AttributionOutcome.OBJECTIVE_WEAKNESS_HYPOTHESIS, rule)
            FailureRule.CLEAN_PREMASTERY_H0_DIRECT ->
                raise(state, event, WeaknessSignal.SUPPORTED, AttributionOutcome.OBJECTIVE_WEAKNESS_SUPPORTED, rule)
            // The first clean contradiction after mastery opens verification; it erases nothing.
            FailureRule.FIRST_CLEAN_POSTMASTERY_CONTRADICTION ->
                raise(state, event, WeaknessSignal.SUPPORTED, AttributionOutcome.VERIFICATION_DUE, rule).copy(verificationOpen = true)
            // A failed fresh recheck is confirmed only when the mastery engine's gates no longer pass.
            FailureRule.FRESH_RECHECK_FAIL ->
                if (!event.masteredAfter) {
                    raise(state, event, WeaknessSignal.CONFIRMED, AttributionOutcome.REMEDIATION_REQUIRED, rule).copy(verificationOpen = false)
                } else {
                    raise(state, event, WeaknessSignal.SUPPORTED, AttributionOutcome.VERIFICATION_DUE, rule).copy(verificationOpen = true)
                }
            FailureRule.FRESH_RECOVERY_SUCCESS -> recover(state, event)
            FailureRule.UNATTEMPTED_OR_DEFERRED, FailureRule.ENVIRONMENT_OUTSIDE_TARGET,
            FailureRule.INTEGRATED_GLOBAL_OUTCOME_GUARD, FailureRule.REVIEW_DUE_WITHOUT_NEGATIVE_EVIDENCE -> state
        }
    }

    /** A failure never lowers a signal: a weaker kind of evidence adds to it, a stronger one escalates it. */
    private fun raise(state: ObjectiveWeakness, event: WeaknessEvent, to: WeaknessSignal, outcome: AttributionOutcome, rule: FailureRule): ObjectiveWeakness {
        val open = state.signal == WeaknessSignal.HYPOTHESIS || state.signal == WeaknessSignal.SUPPORTED || state.signal == WeaknessSignal.CONFIRMED
        // A new signal after none or a resolution starts afresh from this row; it is built in one step so no
        // intermediate state ever claims a resolution without its evidence.
        val signal = if (!open || to.strength > state.signal.strength) to else state.signal
        return state.copy(
            signal = signal,
            lastOutcome = outcome,
            lastRule = rule,
            verificationOpen = open && state.verificationOpen && signal == WeaknessSignal.SUPPORTED,
            signalEvidenceIds = (if (open) state.signalEvidenceIds else emptyList()) + event.evidenceId,
            signalResources = ((if (open) state.signalResources else emptyList()) + listOfNotNull(event.resource)).distinct(),
            signalFamilies = ((if (open) state.signalFamilies else emptyList()) + listOfNotNull(event.variantFamilyId)).distinct(),
            firstSeenDay = if (open) state.firstSeenDay else event.studyDay,
            lastSeenDay = event.studyDay,
            resolutionEvidenceId = if (open) state.resolutionEvidenceId else null,
        )
    }

    /**
     * `WLRM-v0` §7: a fresh, clean, independent success closes what it can. A hypothesis or a supported
     * signal is resolved by it; a confirmed remediation only when the mastery engine's gates pass again —
     * one success, like a finished task, is not enough.
     */
    private fun recover(state: ObjectiveWeakness, event: WeaknessEvent): ObjectiveWeakness {
        val closes = when (state.signal) {
            WeaknessSignal.HYPOTHESIS, WeaknessSignal.SUPPORTED -> true
            WeaknessSignal.CONFIRMED -> event.masteredAfter
            WeaknessSignal.NONE, WeaknessSignal.RESOLVED -> false
        }
        if (!closes) return state.copy(lastOutcome = AttributionOutcome.POSITIVE_RECOVERY_EVIDENCE, lastRule = FailureRule.FRESH_RECOVERY_SUCCESS)
        return state.copy(
            signal = WeaknessSignal.RESOLVED,
            lastOutcome = AttributionOutcome.POSITIVE_RECOVERY_EVIDENCE,
            lastRule = FailureRule.FRESH_RECOVERY_SUCCESS,
            verificationOpen = false,
            resolutionEvidenceId = event.evidenceId,
        )
    }

    // ------------------------------------------------------------------------------------ needs

    /** `KGC-v0` §27: the Skill lifecycles a learner is routed through (the planner's own rule). */
    private val ON_ROUTE = setOf("published", "deprecated")

    /**
     * `learning_need_mappings.yaml` `need.map.weakness_hypothesis` and `need.map.supported_premastery_weakness`:
     * the `weakness_detected` needs this engine supplies to the planner (12C lists it as owner-supplied).
     *
     * A confirmed remediation and a post-mastery verification are not supplied here: the planner already
     * opens `remediation_required` from the weakness axis and `verification_due` from the mastery axis, and one
     * concern is one need.
     */
    fun needs(states: List<SkillPlanningState>): List<LearningNeed> = states.mapNotNull { state ->
        if (state.lifecycleStatus !in ON_ROUTE) return@mapNotNull null
        val severity = when (state.weaknessAxis) {
            WeaknessAxis.SUPPORTED.id -> EvidenceSeverity.CLEAN_CONTRADICTION_OR_VERIFICATION_DUE
            WeaknessAxis.HYPOTHESIS.id -> EvidenceSeverity.PARTIAL_OR_UNCERTAIN_CONCERN
            else -> return@mapNotNull null
        }
        if (state.mastery == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE) return@mapNotNull null
        LearningNeed(
            needKey = "${NeedTrigger.WEAKNESS_DETECTED.id}:${state.skill}",
            trigger = NeedTrigger.WEAKNESS_DETECTED,
            targetSkills = listOf(state.skill),
            criticality = if (state.critical) Criticality.CRITICAL_PREREQUISITE else Criticality.REQUIRED,
            sourceStateRefs = listOfNotNull(state.snapshotRef),
            evidenceSeverity = severity,
            temporalUrgency = TemporalUrgency.NOT_TIME_SENSITIVE,
            continuation = ContinuationValue.FRESH_NEW_CONTEXT,
        )
    }.sortedBy { it.needKey }
}
