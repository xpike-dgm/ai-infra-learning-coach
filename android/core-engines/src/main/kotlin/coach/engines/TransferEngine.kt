package coach.engines

import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.EvidenceSeverity
import coach.model.IndependenceClass
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.MonthlyRole
import coach.model.NeedTrigger
import coach.model.SkillPlanningState
import coach.model.TemporalUrgency
import coach.model.VersionedRef

/**
 * The owner of `MCA-v0` §6.5's cross-topic transfer opportunities (15G, `D-120`, user decision): **a learned Skill is
 * measured once in a context built from another Topic**, and only when something trustworthy can measure it.
 *
 * A transfer opportunity is not a schedule and not a quota. It opens for a Skill only when all of these hold:
 *
 * - the Skill is learned — its mastery is confirmed and current (`MCA-v0` §5: "daha önce öğrenilmiş Skill'lerin ...
 *   transferi"); a Skill still being learned or being verified has its own needs first;
 * - an authored item can measure it there: one whose declared context comes from another Topic (`QAB-v0` §17), that the
 *   store trusts, that the prerequisite gate admits and that the learner has not seen (`MCA-v0` §9: unseen structure,
 *   no unknown prerequisite);
 * - the Skill has no clean transfer measurement yet: no verified, independent, uncontested, prerequisite-valid row from
 *   such an item. One clean measurement — positive or negative — answers the question; what it means for the Skill is
 *   the mastery engine's (a clean miss after mastery opens verification there, never here).
 *
 * Nothing here is a threshold. The Skills that meet the item conditions are read by the application layer, which owns
 * the store, the content and the gate; this engine only says what those facts mean, as pure functions.
 */
object TransferEngine {

    /** `KGC-v0` §27: the Skill lifecycles a learner is routed through (the planner's own rule). */
    private val ON_ROUTE = setOf("published", "deprecated")

    /** What an item has to be trusted as before it measures anything a planned slot claims (`PBR-v0` §3). */
    private val TRUSTED = setOf(LifecycleStatus.VALIDATED, LifecycleStatus.TRUSTED, LifecycleStatus.DEPRECATED)

    /** Whether the Skill is learned and on route, so that a transfer measurement could be asked of it at all. */
    fun learned(state: SkillPlanningState): Boolean =
        state.lifecycleStatus in ON_ROUTE && state.mastery == MasteryAxisState.CONFIRMED_CURRENT

    /**
     * Whether [item] is a cross-topic transfer item for the month's slot: its context comes from another Topic, it is
     * eligible for the monthly scope and declared for the role. A role declared without such a context is not one
     * (`AIV-v0` §16) — the composer refuses it for the slot, and it opens nothing here.
     */
    fun isTransferItem(item: AssessmentItem): Boolean =
        item.transferProfile?.crossTopic == true &&
            AssessmentScope.MONTHLY_CAPABILITY in item.scopeEligibility &&
            MonthlyRole.CROSS_TOPIC_TRANSFER in item.blueprintRoles

    /** Whether the store trusts [item] for a planned measurement (its trust already taken from the store). */
    fun trusted(item: AssessmentItem): Boolean = item.lifecycleStatus in TRUSTED

    /**
     * Whether [evidence] already holds a clean transfer measurement: a row produced by one of [transferItems], verified,
     * independent, uncontested, prerequisite-valid and not invalid. Assisted, provisional or contaminated work measured
     * something else, so it leaves the question open.
     */
    fun measured(evidence: List<EvidenceRow>, transferItems: Set<VersionedRef>): Boolean = evidence.any { row ->
        row.resource in transferItems &&
            row.evaluatorStatus == EvaluatorStatus.VERIFIED &&
            row.independenceClass == IndependenceClass.INDEPENDENT &&
            !row.contested && row.prerequisiteValid && !row.solutionExposed &&
            row.outcome != EvidenceOutcome.INVALID
    }

    /**
     * The `transfer_opportunity` needs (a declared `PDT-v0` §8.1 extension, `D-120`), one per learned Skill in [open] — the
     * Skills for which the application layer found an unseen, trusted, admitted transfer item and no clean measurement.
     * A Skill not learned opens nothing, whatever the content holds.
     */
    fun needs(states: List<SkillPlanningState>, open: Set<VersionedRef>): List<LearningNeed> = states.mapNotNull { state ->
        if (state.skill !in open || !learned(state)) return@mapNotNull null
        LearningNeed(
            needKey = needKey(state.skill),
            trigger = NeedTrigger.TRANSFER_OPPORTUNITY,
            targetSkills = listOf(state.skill),
            criticality = if (state.critical) Criticality.CRITICAL_PREREQUISITE else Criticality.REQUIRED,
            sourceStateRefs = listOfNotNull(state.snapshotRef),
            evidenceSeverity = EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
            temporalUrgency = TemporalUrgency.NOT_TIME_SENSITIVE,
            continuation = ContinuationValue.FRESH_NEW_CONTEXT,
        )
    }.sortedBy { it.needKey }

    fun needKey(skill: VersionedRef) = "${NeedTrigger.TRANSFER_OPPORTUNITY.id}:$skill"
}
