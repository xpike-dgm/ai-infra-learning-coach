package coach.engines

import coach.engines.BlueprintComposer.Pool
import coach.model.AssessmentBlueprint
import coach.model.AssessmentBlueprintResult
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.BlueprintExclusion
import coach.model.BlueprintSlotOutcome
import coach.model.Criticality
import coach.model.ExposureFact
import coach.model.LearningNeed
import coach.model.MonthlyRole
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.PoolExclusion
import coach.model.PrerequisiteDecision
import coach.model.SkillPlanningState
import coach.model.TaskCandidate
import coach.model.VersionedRef

/**
 * The monthly blueprint (13B, `MCA-v0 / D-046`): **selective longitudinal sampling, not a cumulative
 * everything exam** (`MCA-v0` §8). Like the week, what is worth measuring is decided from state before any
 * item is chosen, and the month is never a quota, a percentage split or a debt.
 *
 * It reads the same needs the planner already opens (`PlannerEngine.needsFromSkillStates`) plus the
 * owner-supplied ones, and decides for each whether it is a monthly measurement and under which `MCA-v0`
 * §5 role. Item choice, the planner bridge and the result are [BlueprintComposer]'s, shared with the week:
 * a monthly label adds no evidence weight (`MCA-v0` §2).
 *
 * `cross_topic_transfer` is opened by its owner, [TransferEngine] (15G, `D-120`), from authored transfer metadata.
 * `professional_evidence_checkpoint` still has no state producer: it needs a professional evidence profile
 * (`MCA-v0` §6.8), whose owner is AŞAMA 20. No need opens it, so no slot is ever composed under it, and nothing
 * here invents one.
 */
object MonthlyBlueprintEngine {

    const val MODEL = "MCAX-v0"
    const val POLICY_VERSION = "MCA-v0"

    /** Working text; the words are 14's. */
    const val TITLE = "Aylık değerlendirme"

    /** Critical means a critical prerequisite (`PBR-v0`); a `critical=true` flag alone produces no slot (`MCA-v0` §11). */
    private val REQUIRED = setOf(Criticality.CRITICAL_PREREQUISITE, Criticality.REQUIRED)

    // ------------------------------------------------------------------------------------ target pool

    /**
     * `MCA-v0` §6's target pool, in §7's order.
     *
     * [recentlyEvidenced] is the set of Skills that received evidence since the previous monthly blueprint —
     * the longitudinal window (`MCA-v0` §4 `longitudinal_window_ref`); `null` means there was none, so every
     * Skill in progress is inside the window. [holdingBack] are the Skills that held dependent work back when
     * the planner last planned — the gate's own answer, read from its trace.
     */
    fun targetPool(
        states: List<SkillPlanningState>,
        ownerNeeds: List<LearningNeed>,
        recentlyEvidenced: Set<VersionedRef>?,
        holdingBack: Set<VersionedRef>,
    ): Pool {
        val needs = (PlannerEngine.needsFromSkillStates(states) + ownerNeeds).sortedBy { it.needKey }
        val exclusions = mutableListOf<PoolExclusion>()
        // Open remediation is repaired first; its fresh recheck is remediation's closure (13D), and a month
        // does not measure a Skill while it is being repaired (`MCA-v0` §12, `WBA-v0` §10).
        val underRepair = needs.filter { it.trigger == NeedTrigger.REMEDIATION_REQUIRED }.flatMap { it.targetSkills }.toSet()
        val candidates = needs.mapNotNull { need ->
            val skill = need.targetSkills.first()
            if (skill in underRepair) {
                exclusions += PoolExclusion(need.needKey, skill, BlueprintExclusion.REMEDIATION_OPEN)
                return@mapNotNull null
            }
            when (val role = roleOf(need, recentlyEvidenced, holdingBack)) {
                is Either.Excluded -> { exclusions += PoolExclusion(need.needKey, skill, role.reason); null }
                is Either.Role -> BlueprintComposer.entry(need, role.role, holdingBack)
            }
        }
        // One Skill is measured once, under the role that comes first in §7's order.
        val kept = BlueprintComposer.onePerSkill(MonthlyRole.SELECTION_ORDER, candidates, exclusions)
        return Pool(kept, exclusions.sortedBy { it.needKey })
    }

    private sealed interface Either {
        data class Role(val role: MonthlyRole) : Either
        data class Excluded(val reason: BlueprintExclusion) : Either
    }

    /**
     * Which role a need is measured under this month, or why it is not. Each branch is a sentence of
     * `MCA-v0` §5/§6/§11/§12 or `DMA-v0` §5, and none of them is a quota.
     */
    private fun roleOf(need: LearningNeed, recentlyEvidenced: Set<VersionedRef>?, holdingBack: Set<VersionedRef>): Either {
        val skill = need.targetSkills.first()
        val critical = need.criticality == Criticality.CRITICAL_PREREQUISITE
        return when (need.trigger) {
            // §11: an unresolved `verification_due` or a recent clean contradiction on a critical capability is
            // a reason to revalidate it; anywhere else it is §12's state-supported concern. The engines raised
            // it from their own state, never from one wrong item.
            NeedTrigger.VERIFICATION_DUE, NeedTrigger.WEAKNESS_DETECTED ->
                if (critical) Either.Role(MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION)
                else Either.Role(MonthlyRole.PERSISTENT_WEAKNESS_OR_VERIFICATION)
            // Unreachable from [targetPool], which excludes the whole Skill first; kept total and explicit.
            NeedTrigger.REMEDIATION_REQUIRED -> Either.Excluded(BlueprintExclusion.REMEDIATION_OPEN)
            NeedTrigger.CONTINUE_LEARNING -> when {
                // §11: the next path really depends on it — the gate held dependent work back on it.
                critical && skill in holdingBack -> Either.Role(MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION)
                need.criticality !in REQUIRED -> Either.Excluded(BlueprintExclusion.NOT_REQUIRED_CAPABILITY)
                recentlyEvidenced == null || skill in recentlyEvidenced -> Either.Role(MonthlyRole.LONGITUDINAL_REQUIRED_CAPABILITY)
                else -> Either.Excluded(BlueprintExclusion.NOT_ACTIVE_SINCE_LAST_CYCLE)
            }
            // §11: a meaningfully due review of a critical capability is a revalidation; any other due review is
            // §5's delayed retention sample. `review_due` is not forgetting (`RVR-v0`), and the slot stays `retain`.
            NeedTrigger.RETENTION_REVIEW_DUE ->
                if (critical) Either.Role(MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION)
                else Either.Role(MonthlyRole.DELAYED_RETENTION_SAMPLING)
            NeedTrigger.INTEGRATION_OPPORTUNITY -> Either.Role(MonthlyRole.INTEGRATED_APPLICATION)
            // 15G (`D-120`): §5's `cross_topic_transfer`, opened by its owner only for a learned Skill that has an unseen,
            // trusted item whose context really comes from another Topic and no clean transfer measurement yet.
            NeedTrigger.TRANSFER_OPPORTUNITY -> Either.Role(MonthlyRole.CROSS_TOPIC_TRANSFER)
            NeedTrigger.PARALLEL_TRACK_DUE ->
                if (need.track == WeeklyBlueprintEngine.ENGLISH_TRACK) Either.Role(MonthlyRole.PARALLEL_TECHNICAL_ENGLISH)
                else Either.Excluded(BlueprintExclusion.NOT_A_MONTHLY_MEASUREMENT)
            NeedTrigger.NEW_LEARNING -> Either.Excluded(BlueprintExclusion.NOT_TAUGHT_YET)
            NeedTrigger.DIAGNOSTIC_OPPORTUNITY, NeedTrigger.REINFORCEMENT_OPPORTUNITY ->
                Either.Excluded(BlueprintExclusion.NOT_A_MONTHLY_MEASUREMENT)
        }
    }

    // ------------------------------------------------------------------------------------ composition

    /**
     * Composes the monthly blueprint for [cycleId] (`MCA-v0` §4). [priorSessionId] is the previous month's
     * session (`prior_monthly_result_ref`); it is a reference, never a debt to settle.
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
        priorSessionId: Long? = null,
    ): AssessmentBlueprint = BlueprintComposer.compose(
        AssessmentScope.MONTHLY_CAPABILITY, POLICY_VERSION, cycleId, studyDay, curriculumVersion, truthWatermark,
        pool, items, profiles, decisions, exposures, evaluatorAvailable, recentSince, previousCycleId, priorSessionId,
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
        require(previous.scope == AssessmentScope.MONTHLY_CAPABILITY) { "the monthly engine recomposes a monthly blueprint" }
        return BlueprintComposer.recompose(previous, previousSessionId, slotIds, items, profiles, decisions, exposures, evaluatorAvailable)
    }

    // ------------------------------------------------------------------------------------ planning and result

    fun slotCandidates(blueprint: AssessmentBlueprint, servedItems: Set<VersionedRef>): List<TaskCandidate> =
        BlueprintComposer.slotCandidates(blueprint, servedItems, TITLE)

    fun candidateId(cycleId: String, slotId: String) = BlueprintComposer.candidateId(AssessmentScope.MONTHLY_CAPABILITY, cycleId, slotId)

    fun result(
        blueprint: AssessmentBlueprint,
        sessionId: Long,
        outcomes: List<BlueprintSlotOutcome>,
        stateChangeRefs: List<String>,
    ): AssessmentBlueprintResult = BlueprintComposer.result(blueprint, sessionId, outcomes, stateChangeRefs)
}
