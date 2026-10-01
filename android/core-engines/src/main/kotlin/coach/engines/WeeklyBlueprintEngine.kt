package coach.engines

import coach.engines.BlueprintComposer.Pool
import coach.engines.BlueprintComposer.PoolEntry
import coach.model.AssessmentBlueprint
import coach.model.AssessmentBlueprintResult
import coach.model.AssessmentBlueprintSlot
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.BlueprintExclusion
import coach.model.BlueprintRole
import coach.model.BlueprintSlotOutcome
import coach.model.Criticality
import coach.model.ExposureFact
import coach.model.LearningNeed
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.PoolExclusion
import coach.model.PrerequisiteDecision
import coach.model.SkillPlanningState
import coach.model.TaskCandidate
import coach.model.VersionedRef

/**
 * The weekly blueprint (13A, `WBA-v0 / D-045`): **what is worth measuring this week is decided from
 * state before any item is chosen**, and the week is never a quota.
 *
 * The composer does not open needs of its own. It reads the needs the planner already opens from each
 * engine's axis (`PlannerEngine.needsFromSkillStates`), plus the ones only an owner can supply (the
 * parallel track's cadence, integration opportunities), and decides for each whether it is a weekly
 * measurement and under which `WBA-v0` §7 role. Then, per slot, it looks for an item in `QAB-v0` §31's
 * order and takes the first that the store trusts, the Objective accepts, the prerequisite gate lets
 * through, the learner has not already seen, and that does not repeat a variant family or dependency
 * group already in the week.
 *
 * Everything is a pure function of its arguments: the same state, items, exposures and decisions give
 * the same blueprint.
 */
object WeeklyBlueprintEngine {

    const val MODEL = "WBAX-v0"
    const val POLICY_VERSION = "WBA-v0"

    /**
     * How many items the composer examines per slot, after the indexed facets (scope, role, lifecycle)
     * and before the per-learner filters, as `QAB-v0` §33 orders it. Like `PlannerEngine`'s per-need cap
     * it is an engineering bound on how much is read, with no learning meaning; 18E may move it.
     */
    const val MAX_ITEMS_PER_SLOT_V0 = BlueprintComposer.MAX_ITEMS_PER_SLOT_V0

    /** `PLNX-v0`'s owner-supplied parallel track; only it has a weekly role (`WBA-v0` §26). */
    const val ENGLISH_TRACK = "technical_english"

    // ------------------------------------------------------------------------------------ target pool

    /**
     * `WBA-v0` §8's target pool, in §9's order.
     *
     * [recentlyEvidenced] is the set of Skills that received evidence since the previous weekly
     * blueprint; `null` means there was no previous blueprint, so there is no "since" to measure from and
     * every Skill in progress counts as recent. [holdingBack] are the Skills that held dependent work back
     * when the planner last planned — the gate's own answer, read from its trace.
     */
    fun targetPool(
        states: List<SkillPlanningState>,
        ownerNeeds: List<LearningNeed>,
        recentlyEvidenced: Set<VersionedRef>?,
        holdingBack: Set<VersionedRef>,
    ): Pool {
        val needs = (PlannerEngine.needsFromSkillStates(states) + ownerNeeds).sortedBy { it.needKey }
        val exclusions = mutableListOf<PoolExclusion>()
        // A Skill under open remediation is repaired first and measured under no role this week: measuring
        // it again is the bombardment `WBA-v0` §10 forbids, and its closure check is remediation's (13D).
        val underRepair = needs.filter { it.trigger == NeedTrigger.REMEDIATION_REQUIRED }.flatMap { it.targetSkills }.toSet()
        val candidates = needs.mapNotNull { need ->
            if (need.targetSkills.first() in underRepair) {
                exclusions += PoolExclusion(need.needKey, need.targetSkills.first(), BlueprintExclusion.REMEDIATION_OPEN)
                return@mapNotNull null
            }
            when (val role = roleOf(need, recentlyEvidenced, holdingBack)) {
                is Either.Excluded -> { exclusions += PoolExclusion(need.needKey, need.targetSkills.first(), role.reason); null }
                is Either.Role -> entry(need, role.role, holdingBack)
            }
        }
        // One Skill is measured once, under the role that comes first in §9's order: measuring a weak
        // Skill under three roles is the bombardment §10 forbids, and it would not add independent evidence.
        val kept = BlueprintComposer.onePerSkill(BlueprintRole.SELECTION_ORDER, candidates, exclusions)
        return Pool(kept, exclusions.sortedBy { it.needKey })
    }

    private sealed interface Either {
        data class Role(val role: BlueprintRole) : Either
        data class Excluded(val reason: BlueprintExclusion) : Either
    }

    /**
     * Which role a need is measured under, or why it is not measured this week. Each branch is a
     * sentence of `WBA-v0` §7/§8 or `DMA-v0` §5/§7, and none of them is a quota.
     */
    private fun roleOf(need: LearningNeed, recentlyEvidenced: Set<VersionedRef>?, holdingBack: Set<VersionedRef>): Either {
        val skill = need.targetSkills.first()
        return when (need.trigger) {
            NeedTrigger.VERIFICATION_DUE, NeedTrigger.WEAKNESS_DETECTED -> Either.Role(BlueprintRole.WEAKNESS_OR_VERIFICATION)
            // Unreachable from [targetPool], which excludes the whole Skill first; kept total and explicit.
            NeedTrigger.REMEDIATION_REQUIRED -> Either.Excluded(BlueprintExclusion.REMEDIATION_OPEN)
            NeedTrigger.CONTINUE_LEARNING -> when {
                // A critical prerequisite still in progress that really holds dependent work back: one fresh
                // measurement could settle the next branch (`WBA-v0` §7, `PBR-v0` §6.7 decision value).
                need.criticality == Criticality.CRITICAL_PREREQUISITE && skill in holdingBack ->
                    Either.Role(BlueprintRole.CRITICAL_PREREQUISITE_CONFIDENCE)
                recentlyEvidenced == null || skill in recentlyEvidenced -> Either.Role(BlueprintRole.RECENT_REQUIRED_PROGRESS)
                else -> Either.Excluded(BlueprintExclusion.NOT_ACTIVE_SINCE_LAST_CYCLE)
            }
            NeedTrigger.RETENTION_REVIEW_DUE -> Either.Role(BlueprintRole.RETENTION_DUE)
            NeedTrigger.INTEGRATION_OPPORTUNITY -> Either.Role(BlueprintRole.INTEGRATION_OR_TRANSFER)
            NeedTrigger.PARALLEL_TRACK_DUE ->
                if (need.track == ENGLISH_TRACK) Either.Role(BlueprintRole.PARALLEL_ENGLISH)
                else Either.Excluded(BlueprintExclusion.NOT_A_WEEKLY_MEASUREMENT)
            NeedTrigger.NEW_LEARNING -> Either.Excluded(BlueprintExclusion.NOT_TAUGHT_YET)
            NeedTrigger.DIAGNOSTIC_OPPORTUNITY, NeedTrigger.REINFORCEMENT_OPPORTUNITY ->
                Either.Excluded(BlueprintExclusion.NOT_A_WEEKLY_MEASUREMENT)
        }
    }

    /** The need's `PBR-v0` band and rank vector — the planner's own, not a weekly weighting. */
    private fun entry(need: LearningNeed, role: BlueprintRole, holdingBack: Set<VersionedRef>): PoolEntry =
        BlueprintComposer.entry(need, role, holdingBack)

    // ------------------------------------------------------------------------------------ composition

    /**
     * Composes the weekly blueprint for [cycleId] from a pool (`WBA-v0` §5/§28). The item choice, the
     * planner bridge and the result are [BlueprintComposer]'s, shared with the monthly scope.
     */
    fun compose(
        cycleId: String,
        studyDay: String,
        curriculumVersion: Int,
        truthWatermark: Long,
        pool: Pool,
        items: Map<VersionedRef, List<AssessmentItem>>,
        profiles: Map<VersionedRef, ObjectiveEvidenceProfile>,
        decisions: Map<VersionedRef, PrerequisiteDecision>,
        exposures: List<ExposureFact>,
        evaluatorAvailable: Boolean,
        recentSince: String?,
        previousCycleId: String? = null,
    ): AssessmentBlueprint = BlueprintComposer.compose(
        AssessmentScope.WEEKLY_BLUEPRINT, POLICY_VERSION, cycleId, studyDay, curriculumVersion, truthWatermark,
        pool, items, profiles, decisions, exposures, evaluatorAvailable, recentSince, previousCycleId, priorSessionId = null,
    )

    fun recompose(
        previous: AssessmentBlueprint,
        previousSessionId: Long,
        slotIds: Set<String>,
        items: Map<VersionedRef, List<AssessmentItem>>,
        profiles: Map<VersionedRef, ObjectiveEvidenceProfile>,
        decisions: Map<VersionedRef, PrerequisiteDecision>,
        exposures: List<ExposureFact>,
        evaluatorAvailable: Boolean,
    ): AssessmentBlueprint {
        require(previous.scope == AssessmentScope.WEEKLY_BLUEPRINT) { "the weekly engine recomposes a weekly blueprint" }
        return BlueprintComposer.recompose(previous, previousSessionId, slotIds, items, profiles, decisions, exposures, evaluatorAvailable)
    }

    // ------------------------------------------------------------------------------------ planning

    fun slotCandidates(blueprint: AssessmentBlueprint, servedItems: Set<VersionedRef>): List<TaskCandidate> =
        BlueprintComposer.slotCandidates(blueprint, servedItems, TITLE)

    fun candidateId(cycleId: String, slotId: String) = BlueprintComposer.candidateId(AssessmentScope.WEEKLY_BLUEPRINT, cycleId, slotId)

    const val ACTIVITY = BlueprintComposer.ACTIVITY

    /** Working text; the words are 14's. */
    const val TITLE = "Haftalık değerlendirme"

    // ------------------------------------------------------------------------------------ result

    fun contaminatedBy(slot: AssessmentBlueprintSlot, cleanlyFailedSkills: Set<VersionedRef>): Boolean =
        BlueprintComposer.contaminatedBy(slot, cleanlyFailedSkills)

    fun cleanlyFailedSkills(blueprint: AssessmentBlueprint, outcomes: List<BlueprintSlotOutcome>): Set<VersionedRef> =
        BlueprintComposer.cleanlyFailedSkills(blueprint, outcomes)

    fun result(
        blueprint: AssessmentBlueprint,
        sessionId: Long,
        outcomes: List<BlueprintSlotOutcome>,
        stateChangeRefs: List<String>,
    ): AssessmentBlueprintResult = BlueprintComposer.result(blueprint, sessionId, outcomes, stateChangeRefs)
}
