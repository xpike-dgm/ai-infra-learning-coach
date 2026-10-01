package coach.engines

import coach.model.AssessmentBlueprintSlot
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.BlockingScope
import coach.model.BlueprintRole
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.DecisionValue
import coach.model.DurationFit
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.EvidenceSeverity
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.ItemFit
import coach.model.ItemSelection
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.PoolExclusion
import coach.model.PrerequisiteDecision
import coach.model.PriorityBand
import coach.model.RankVector
import coach.model.SkillPlanningState
import coach.model.SlotStatus
import coach.model.StarvationBucket
import coach.model.TaskCandidate
import coach.model.TemporalUrgency
import coach.model.TrackBalance
import coach.model.VersionedRef
import coach.model.WeeklyAssessmentBlueprint
import coach.model.WeeklyAssessmentResult
import coach.model.WeeklyBlueprintCodec
import coach.model.WeeklyEvidenceFact
import coach.model.WeeklyExclusion
import coach.model.WeeklyItemRefusal
import coach.model.WeeklyItemRejection
import coach.model.WeeklyReasonCodes
import coach.model.WeeklySessionStatus
import coach.model.WeeklySlotOutcome

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
    const val MAX_ITEMS_PER_SLOT_V0 = 5

    /** `PLNX-v0`'s owner-supplied parallel track; only it has a weekly role (`WBA-v0` §26). */
    const val ENGLISH_TRACK = "technical_english"

    /** What an item has to be trusted as before it measures anything a planned slot claims (`PBR-v0` §3). */
    private val TRUSTED = setOf(LifecycleStatus.VALIDATED, LifecycleStatus.TRUSTED, LifecycleStatus.DEPRECATED)

    // ------------------------------------------------------------------------------------ target pool

    /** One need that is a weekly measurement target, with the role it is measured under. */
    data class PoolEntry(
        val need: LearningNeed,
        val role: BlueprintRole,
        val band: PriorityBand,
        val rank: RankVector,
    ) {
        val skill: VersionedRef get() = need.targetSkills.first()
    }

    data class Pool(val entries: List<PoolEntry>, val exclusions: List<PoolExclusion>)

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
                exclusions += PoolExclusion(need.needKey, need.targetSkills.first(), WeeklyExclusion.REMEDIATION_OPEN)
                return@mapNotNull null
            }
            when (val role = roleOf(need, recentlyEvidenced, holdingBack)) {
                is Either.Excluded -> { exclusions += PoolExclusion(need.needKey, need.targetSkills.first(), role.reason); null }
                is Either.Role -> entry(need, role.role, holdingBack)
            }
        }
        // One Skill is measured once, under the role that comes first in §9's order: measuring a weak
        // Skill under three roles is the bombardment §10 forbids, and it would not add independent evidence.
        val ordered = candidates.sortedWith(order)
        val kept = mutableListOf<PoolEntry>()
        ordered.forEach { entry ->
            if (kept.any { it.skill == entry.skill }) {
                exclusions += PoolExclusion(entry.need.needKey, entry.skill, WeeklyExclusion.MEASURED_IN_ANOTHER_ROLE)
            } else {
                kept += entry
            }
        }
        return Pool(kept, exclusions.sortedBy { it.needKey })
    }

    private sealed interface Either {
        data class Role(val role: BlueprintRole) : Either
        data class Excluded(val reason: WeeklyExclusion) : Either
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
            NeedTrigger.REMEDIATION_REQUIRED -> Either.Excluded(WeeklyExclusion.REMEDIATION_OPEN)
            NeedTrigger.CONTINUE_LEARNING -> when {
                // A critical prerequisite still in progress that really holds dependent work back: one fresh
                // measurement could settle the next branch (`WBA-v0` §7, `PBR-v0` §6.7 decision value).
                need.criticality == Criticality.CRITICAL_PREREQUISITE && skill in holdingBack ->
                    Either.Role(BlueprintRole.CRITICAL_PREREQUISITE_CONFIDENCE)
                recentlyEvidenced == null || skill in recentlyEvidenced -> Either.Role(BlueprintRole.RECENT_REQUIRED_PROGRESS)
                else -> Either.Excluded(WeeklyExclusion.NOT_ACTIVE_SINCE_LAST_CYCLE)
            }
            NeedTrigger.RETENTION_REVIEW_DUE -> Either.Role(BlueprintRole.RETENTION_DUE)
            NeedTrigger.INTEGRATION_OPPORTUNITY -> Either.Role(BlueprintRole.INTEGRATION_OR_TRANSFER)
            NeedTrigger.PARALLEL_TRACK_DUE ->
                if (need.track == ENGLISH_TRACK) Either.Role(BlueprintRole.PARALLEL_ENGLISH)
                else Either.Excluded(WeeklyExclusion.NOT_A_WEEKLY_MEASUREMENT)
            NeedTrigger.NEW_LEARNING -> Either.Excluded(WeeklyExclusion.NOT_TAUGHT_YET)
            NeedTrigger.DIAGNOSTIC_OPPORTUNITY, NeedTrigger.REINFORCEMENT_OPPORTUNITY ->
                Either.Excluded(WeeklyExclusion.NOT_A_WEEKLY_MEASUREMENT)
        }
    }

    /** The need's `PBR-v0` band and rank vector — the planner's own, not a weekly weighting. */
    private fun entry(need: LearningNeed, role: BlueprintRole, holdingBack: Set<VersionedRef>): PoolEntry {
        val scope = if (need.targetSkills.any { it in holdingBack }) BlockingScope.BLOCKS_NEXT_READY_DEPENDENCY else BlockingScope.NON_BLOCKING
        val rank = RankVector(
            blockingScope = scope,
            criticality = need.criticality,
            evidenceSeverity = need.evidenceSeverity,
            temporalUrgency = need.temporalUrgency,
            starvation = StarvationBucket.NONE,
            continuation = need.continuation,
            decisionValue = need.decisionValue,
            trackBalance = TrackBalance.NONE,
            // A blueprint is not fitted to a day; the planner does that with each slot's minutes.
            durationFit = DurationFit.FITS_REMAINING,
            tieBreakKey = need.needKey,
        )
        return PoolEntry(need, role, PlannerEngine.band(need, scope, StarvationBucket.NONE), rank)
    }

    /** §9's role order first, then the `PBR-v0` band, then the rank vector compared field by field. */
    private val order: Comparator<PoolEntry> = compareBy<PoolEntry>(
        { BlueprintRole.SELECTION_ORDER.indexOf(it.role) }, { it.band }, { it.rank },
    )

    // ------------------------------------------------------------------------------------ composition

    /**
     * Composes the blueprint for [cycleId] from a pool. [items] are the authored items targeting each
     * pool Skill, with their trust already taken from the store; [decisions] are the prerequisite gate's
     * answers for each item; [exposures] are what the learner has seen.
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
    ): WeeklyAssessmentBlueprint {
        val usedFamilies = mutableSetOf<String>()
        val usedGroups = mutableSetOf<String>()
        val slots = pool.entries.mapIndexed { index, entry ->
            slotFor("slot-${index + 1}", entry, items[entry.skill].orEmpty(), profiles, decisions, exposures,
                evaluatorAvailable, usedFamilies, usedGroups)
        }
        return WeeklyAssessmentBlueprint(
            cycleId = cycleId,
            studyDay = studyDay,
            curriculumVersion = curriculumVersion,
            truthWatermark = truthWatermark,
            policyVersion = POLICY_VERSION,
            evaluatorAvailable = evaluatorAvailable,
            recentSince = recentSince,
            slots = slots,
            exclusions = pool.exclusions,
            reasonCodes = buildList {
                add(WeeklyReasonCodes.DUE)
                add(WeeklyReasonCodes.BLUEPRINT_GENERATED)
                if (pool.entries.isEmpty()) add(WeeklyReasonCodes.NO_ELIGIBLE_TARGET)
                if (slots.any { it.status == SlotStatus.NO_VALID_ITEM }) add(WeeklyReasonCodes.NO_VALID_ITEM)
                // A week that passed without its assessment leaves nothing behind (`WBA-v0` §3).
                if (previousCycleId != null && previousCycleId != cycleId) add(WeeklyReasonCodes.NO_EXAM_DEBT)
            },
        )
    }

    /**
     * Replaces the items of [slotIds] (`WBA-v0` §17): after a solution was exposed, an item version or
     * its validation changed, a prerequisite changed, or the learner asked for an alternative. Every
     * other slot — and so every submitted boundary — is carried over unchanged; the replaced items and
     * their variant families are not used again, because a recomposed slot is a fresh measurement,
     * never a retry of the same one.
     */
    fun recompose(
        previous: WeeklyAssessmentBlueprint,
        previousSessionId: Long,
        slotIds: Set<String>,
        items: Map<VersionedRef, List<AssessmentItem>>,
        profiles: Map<VersionedRef, ObjectiveEvidenceProfile>,
        decisions: Map<VersionedRef, PrerequisiteDecision>,
        exposures: List<ExposureFact>,
        evaluatorAvailable: Boolean,
    ): WeeklyAssessmentBlueprint {
        val kept = previous.slots.filterNot { it.slotId in slotIds }
        val replaced = previous.slots.filter { it.slotId in slotIds }
        val usedFamilies = (kept.mapNotNull { it.variantFamilyId } + replaced.mapNotNull { it.variantFamilyId }).toMutableSet()
        val usedGroups = (kept.mapNotNull { it.dependencyGroupId } + replaced.mapNotNull { it.dependencyGroupId }).toMutableSet()
        val retired = replaced.mapNotNull { it.item }.toSet()
        val slots = previous.slots.map { slot ->
            if (slot.slotId !in slotIds) return@map slot
            val entry = PoolEntry(
                need = LearningNeed(slot.needKey, slot.trigger, listOf(slot.targetSkill), slot.criticality,
                    sourceStateRefs = slot.sourceStateRefs, track = slot.track),
                role = slot.role, band = slot.band,
                rank = RankVector(BlockingScope.NON_BLOCKING, slot.criticality, EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
                    TemporalUrgency.NOT_TIME_SENSITIVE, StarvationBucket.NONE,
                    ContinuationValue.FRESH_NEW_CONTEXT, DecisionValue.NONE, TrackBalance.NONE,
                    DurationFit.FITS_REMAINING, slot.needKey),
            )
            val fresh = items[slot.targetSkill].orEmpty().filterNot { it.ref in retired }
            val recomposed = slotFor(slot.slotId, entry, fresh, profiles, decisions, exposures, evaluatorAvailable, usedFamilies, usedGroups)
            // The slot keeps its own place and reasons; a replacement is said, not hidden.
            recomposed.copy(
                sourceStateRefs = slot.sourceStateRefs,
                reasonCodes = slot.reasonCodes.filterNot { it == WeeklyReasonCodes.NO_VALID_ITEM } +
                    listOfNotNull(
                        WeeklyReasonCodes.INVALID_ITEM_REPLACED.takeIf { recomposed.status == SlotStatus.READY },
                        WeeklyReasonCodes.NO_VALID_ITEM.takeIf { recomposed.status == SlotStatus.NO_VALID_ITEM },
                    ),
            )
        }
        return previous.copy(
            slots = slots,
            evaluatorAvailable = evaluatorAvailable,
            supersedesSessionId = previousSessionId,
        )
    }

    private fun slotFor(
        slotId: String,
        entry: PoolEntry,
        available: List<AssessmentItem>,
        profiles: Map<VersionedRef, ObjectiveEvidenceProfile>,
        decisions: Map<VersionedRef, PrerequisiteDecision>,
        exposures: List<ExposureFact>,
        evaluatorAvailable: Boolean,
        usedFamilies: MutableSet<String>,
        usedGroups: MutableSet<String>,
    ): AssessmentBlueprintSlot {
        val role = entry.role
        val reasons = listOf(role.reasonCode, entry.need.trigger.reasonCode, entry.band.reasonCode)
        val base = AssessmentBlueprintSlot(
            slotId = slotId, role = role, needKey = entry.need.needKey, trigger = entry.need.trigger,
            targetSkill = entry.skill, criticality = entry.need.criticality, band = entry.band,
            sourceStateRefs = entry.need.sourceStateRefs, track = entry.need.track,
            status = SlotStatus.NO_VALID_ITEM, reasonCodes = reasons + WeeklyReasonCodes.NO_VALID_ITEM,
        )

        // Steps 1–3 of `QAB-v0` §31 are the indexed facets: the item targets the Skill, is eligible for the
        // weekly scope and declared for the role, and its lifecycle is selectable at all. Only then is the
        // read bounded; the per-learner filters come after the bound, as §33 orders it.
        val rejections = mutableListOf<WeeklyItemRejection>()
        val indexed = available
            .filter { entry.skill in it.targetSkills && AssessmentScope.WEEKLY_BLUEPRINT in it.scopeEligibility && it.lifecycleStatus.selectable }
            .sortedWith(itemOrder)
        indexed.filterNot { role in it.blueprintRoles }.forEach {
            rejections += WeeklyItemRejection(it.ref, listOf(WeeklyItemRefusal.ROLE_NOT_DECLARED.id))
        }
        val examined = indexed.filter { role in it.blueprintRoles }.take(MAX_ITEMS_PER_SLOT_V0)

        val seen = exposures.map { it.resource }.toSet()
        val exposedFamilies = exposures.filter { it.solutionExposed }.mapNotNull { it.variantFamilyId }.toSet()
        val exposedItems = exposures.filter { it.solutionExposed }.map { it.resource }.toSet()

        examined.forEach { item ->
            val refusals = buildList {
                when (val fit = ItemSelection.fit(item, role.intent, AssessmentScope.WEEKLY_BLUEPRINT, profiles, evaluatorAvailable)) {
                    is ItemFit.NotUsable -> {
                        add(WeeklyItemRefusal.NOT_USABLE_FOR_INTENT.id)
                        addAll(fit.reasons.map { it.id })
                    }
                    is ItemFit.Usable -> Unit
                }
                // A planned slot is high stakes (`PBR-v0` §3): the planner will not trust anything less.
                if (item.lifecycleStatus !in TRUSTED && WeeklyItemRefusal.NOT_USABLE_FOR_INTENT.id !in this) {
                    add(WeeklyItemRefusal.NOT_USABLE_FOR_INTENT.id)
                }
                val decision = decisions[item.ref]
                // The gate fails closed: an item it has not answered for waits (`PRG-v0`).
                if (decision == null || decision.eligibility.waits) add(WeeklyItemRefusal.PREREQUISITE_WAITS.id)
                if (item.ref in exposedItems || item.variantFamilyId in exposedFamilies) add(WeeklyItemRefusal.SOLUTION_EXPOSED.id)
                else if (item.ref in seen) add(WeeklyItemRefusal.ALREADY_SEEN.id)
                if (item.variantFamilyId in usedFamilies) add(WeeklyItemRefusal.VARIANT_FAMILY_IN_USE.id)
                item.dependencyGroupId?.let { if (it in usedGroups) add(WeeklyItemRefusal.DEPENDENCY_GROUP_IN_USE.id) }
                if (item.expectedActiveMinutes == null) add(WeeklyItemRefusal.EXPECTED_MINUTES_MISSING.id)
            }
            if (refusals.isNotEmpty()) {
                rejections += WeeklyItemRejection(item.ref, refusals)
                return@forEach
            }
            usedFamilies += item.variantFamilyId
            item.dependencyGroupId?.let(usedGroups::add)
            return base.copy(
                status = SlotStatus.READY,
                item = item.ref,
                targetObjectives = item.targetObjectives,
                evidenceType = item.evidenceType,
                variantFamilyId = item.variantFamilyId,
                dependencyGroupId = item.dependencyGroupId,
                expectedActiveMinutes = item.expectedActiveMinutes,
                itemLifecycle = item.lifecycleStatus,
                itemRequiredSkills = item.requiredSkills,
                allowedTools = item.allowedTools.allowed,
                // `WBA-v0` §11: integrity, verification and repair are what a session is for; everything
                // else it holds is welcome but its absence does not leave the session open.
                requiredForSessionClosure = entry.band == PriorityBand.P0 || entry.band == PriorityBand.P1,
                reasonCodes = reasons,
                rejections = rejections,
            )
        }
        return base.copy(rejections = rejections)
    }

    /**
     * `QAB-v0` §32's semantic order among eligible items: the better-validated first, then a deterministic
     * check over an evaluator, then a stable tie-break. Never the shortest first.
     */
    private val itemOrder: Comparator<AssessmentItem> = compareBy<AssessmentItem>(
        { if (it.lifecycleStatus == LifecycleStatus.TRUSTED) 0 else 1 },
        { if (it.deterministicVerification) 0 else 1 },
        { it.ref.toString() },
    )

    // ------------------------------------------------------------------------------------ planning

    /**
     * The blueprint's ready slots as the planner's candidates (`WBA-v0` §4: blueprint slots → validated,
     * prerequisite-valid candidates → `PBR-v0` + capacity). Each slot serves the **same need** the planner
     * already opened, so the week adds no queue of its own, no band of its own and no minutes of its own;
     * a slot whose item the learner has since been shown is not offered again.
     */
    fun slotCandidates(blueprint: WeeklyAssessmentBlueprint, servedItems: Set<VersionedRef>): List<TaskCandidate> =
        blueprint.readySlots.filterNot { it.item in servedItems }.map { slot ->
            TaskCandidate(
                id = candidateId(blueprint.cycleId, slot.slotId),
                needKey = slot.needKey,
                purpose = slot.role.purpose,
                activityKind = ACTIVITY,
                title = TITLE,
                primarySkill = slot.targetSkill,
                costMinutes = slot.expectedActiveMinutes!!,
                validationStatus = slot.itemLifecycle!!,
                track = slot.track,
                requiredSkills = slot.itemRequiredSkills,
                atomicEvidenceBoundary = true,
                generationVersion = "${WeeklyBlueprintCodec.FORMAT}:${blueprint.cycleId}",
            )
        }

    fun candidateId(cycleId: String, slotId: String) = "weekly:$cycleId:$slotId"

    /** Every weekly slot runs in the one assessment session interior (`ASUX-v0`). */
    const val ACTIVITY = "assessment_session"

    /** Working text; the words are 14's. */
    const val TITLE = "Haftalık değerlendirme"

    // ------------------------------------------------------------------------------------ result

    /**
     * `WBA-v0` §25. A downstream slot whose item needs a Skill this very session has just shown, cleanly and
     * independently, to be missing cannot be scored against its own target: the snapshot it records says
     * contaminated, and the mastery engine keeps it out (`PRG-v0` §13). Independent branches are untouched.
     */
    fun contaminatedBy(slot: AssessmentBlueprintSlot, cleanlyFailedSkills: Set<VersionedRef>): Boolean =
        slot.itemRequiredSkills.any { it in cleanlyFailedSkills }

    /** The Skills a session's evidence shows cleanly missing: verified, independent, negative and not itself contaminated. */
    fun cleanlyFailedSkills(blueprint: WeeklyAssessmentBlueprint, outcomes: List<WeeklySlotOutcome>): Set<VersionedRef> {
        val slots = blueprint.slots.associateBy { it.slotId }
        return outcomes.filter { outcome ->
            outcome.evidence.any {
                it.outcome == EvidenceOutcome.NEGATIVE && it.evaluatorStatus == EvaluatorStatus.VERIFIED &&
                    it.independence == IndependenceClass.INDEPENDENT && !it.prerequisiteContaminated
            }
        }.mapNotNull { slots[it.slotId]?.targetSkill }.toSet()
    }

    /**
     * `WBA-v0` §27 from what happened in the session and what the engines said changed. There is no score
     * to compute: the result is which Objectives got which evidence, what could not be measured, and the
     * changes the canonical engines reported — none of which is inferred here from answers.
     */
    fun result(
        blueprint: WeeklyAssessmentBlueprint,
        sessionId: Long,
        outcomes: List<WeeklySlotOutcome>,
        stateChangeRefs: List<String>,
    ): WeeklyAssessmentResult {
        val ready = blueprint.readySlots.map { it.slotId }
        val bySlot = outcomes.associateBy { it.slotId }
        require(bySlot.keys.all { it in ready }) { "an outcome names a slot this blueprint cannot run" }
        val completed = ready.filter { bySlot[it]?.submitted == true }
        val unresolved = ready - completed.toSet()
        val evidence = outcomes.flatMap { it.evidence }
        fun usable(e: WeeklyEvidenceFact) = !e.prerequisiteContaminated && e.outcome != EvidenceOutcome.INVALID
        fun clean(e: WeeklyEvidenceFact) =
            usable(e) && e.evaluatorStatus == EvaluatorStatus.VERIFIED && e.independence == IndependenceClass.INDEPENDENT
        val status = when {
            completed.isEmpty() -> WeeklySessionStatus.DEFERRED
            unresolved.isEmpty() -> WeeklySessionStatus.COMPLETE
            else -> WeeklySessionStatus.PARTIAL
        }
        val contaminated = outcomes.filter { o -> o.evidence.any { it.prerequisiteContaminated } }.map { it.slotId }
        val recheck = evidence.filter { usable(it) && it.independence != IndependenceClass.INDEPENDENT }.map { it.objective }.distinct()
        return WeeklyAssessmentResult(
            assessmentSessionId = sessionId,
            cycleId = blueprint.cycleId,
            sessionStatus = status,
            completedSlotIds = completed,
            unresolvedSlotIds = unresolved,
            attemptIds = outcomes.mapNotNull { it.attemptId },
            evidenceIds = evidence.map { it.evidenceId },
            verifiedPositiveObjectives = evidence.filter { clean(it) && it.outcome == EvidenceOutcome.POSITIVE }.map { it.objective }.distinct(),
            verifiedNegativeObjectives = evidence.filter { clean(it) && it.outcome == EvidenceOutcome.NEGATIVE }.map { it.objective }.distinct(),
            partialObjectives = evidence.filter { clean(it) && it.outcome == EvidenceOutcome.PARTIAL }.map { it.objective }.distinct(),
            invalidOrUnusableEvidenceIds = evidence.filterNot(::usable).map { it.evidenceId },
            provisionalEvidenceIds = evidence.filter { usable(it) && it.evaluatorStatus == EvaluatorStatus.PROVISIONAL }.map { it.evidenceId },
            assistanceRecheckObjectives = recheck,
            prerequisiteContaminatedSlotIds = contaminated,
            stateChangeRefs = stateChangeRefs,
            reasonCodes = buildList {
                if (evidence.isNotEmpty()) add(WeeklyReasonCodes.EVIDENCE_BUNDLE_RECORDED)
                if (status == WeeklySessionStatus.PARTIAL) add(WeeklyReasonCodes.PARTIAL_SESSION)
                if (unresolved.isNotEmpty()) add(WeeklyReasonCodes.INCOMPLETE_NOT_FAILURE)
                if (recheck.isNotEmpty()) add(WeeklyReasonCodes.ASSISTANCE_RECHECK_REQUIRED)
                if (contaminated.isNotEmpty()) add(WeeklyReasonCodes.PREREQUISITE_CONTAMINATED)
                if (stateChangeRefs.isNotEmpty()) add(WeeklyReasonCodes.REPLAN_AFTER_RESULT)
            },
            assessmentPolicyVersion = POLICY_VERSION,
        )
    }
}
