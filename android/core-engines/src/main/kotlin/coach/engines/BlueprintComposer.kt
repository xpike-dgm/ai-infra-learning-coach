package coach.engines

import coach.model.AssessmentBlueprint
import coach.model.AssessmentBlueprintResult
import coach.model.AssessmentBlueprintSlot
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.BlockingScope
import coach.model.BlueprintEvidenceFact
import coach.model.BlueprintScopes
import coach.model.BlueprintSessionStatus
import coach.model.BlueprintSlotOutcome
import coach.model.ContinuationValue
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
import coach.model.ObjectiveEvidenceProfile
import coach.model.PoolExclusion
import coach.model.PrerequisiteDecision
import coach.model.PriorityBand
import coach.model.RankVector
import coach.model.RoleEvidenceKind
import coach.model.SlotItemRefusal
import coach.model.SlotItemRejection
import coach.model.SlotRole
import coach.model.SlotStatus
import coach.model.StarvationBucket
import coach.model.TaskCandidate
import coach.model.TemporalUrgency
import coach.model.TrackBalance
import coach.model.VersionedRef

/**
 * What a weekly and a monthly blueprint share (13A, generalised at 13B): a pool entry's priority, choosing
 * an item for a slot, offering slots to the planner, the root-cause guard and the result.
 *
 * `MCA-v0` §4 makes monthly a policy extension of `WBA-v0` §28's contract, not a second architecture, so
 * this is one implementation with the scope as a parameter: the scope decides which items are eligible
 * (`QAB-v0` §13) and which reason-code namespace is written, never what a refusal, a freshness rule, a
 * trust ceiling or a result means. A `monthly` label adds no evidence weight.
 *
 * Everything is a pure function of its arguments.
 */
object BlueprintComposer {

    /**
     * How many items the composer examines per slot, after the indexed facets (scope, role, lifecycle)
     * and before the per-learner filters, as `QAB-v0` §33 orders it. Like `PlannerEngine`'s per-need cap
     * it is an engineering bound on how much is read, with no learning meaning; 18E may move it.
     */
    const val MAX_ITEMS_PER_SLOT_V0 = 5

    /** What an item has to be trusted as before it measures anything a planned slot claims (`PBR-v0` §3). */
    private val TRUSTED = setOf(LifecycleStatus.VALIDATED, LifecycleStatus.TRUSTED, LifecycleStatus.DEPRECATED)

    /** Every slot runs in the one assessment session interior (`ASUX-v0`). */
    const val ACTIVITY = "assessment_session"

    // ------------------------------------------------------------------------------------ pool

    /** One need that is a measurement target this cycle, with the role it is measured under. */
    data class PoolEntry(
        val need: LearningNeed,
        val role: SlotRole,
        val band: PriorityBand,
        val rank: RankVector,
    ) {
        val skill: VersionedRef get() = need.targetSkills.first()
    }

    data class Pool(val entries: List<PoolEntry>, val exclusions: List<PoolExclusion>)

    /** The need's `PBR-v0` band and rank vector — the planner's own, not a weekly or monthly weighting. */
    fun entry(need: LearningNeed, role: SlotRole, holdingBack: Set<VersionedRef>): PoolEntry {
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

    /**
     * Orders entries by the scope's role order, then band, then rank vector field by field, and keeps one
     * entry per Skill — the role that comes first. Measuring a weak Skill under several roles is the
     * bombardment `WBA-v0` §10 forbids, and it adds no independent evidence.
     */
    fun onePerSkill(selectionOrder: List<SlotRole>, candidates: List<PoolEntry>, exclusions: MutableList<PoolExclusion>): List<PoolEntry> {
        val order = compareBy<PoolEntry>({ selectionOrder.indexOf(it.role) }, { it.band }, { it.rank })
        val kept = mutableListOf<PoolEntry>()
        candidates.sortedWith(order).forEach { entry ->
            if (kept.any { it.skill == entry.skill }) {
                exclusions += PoolExclusion(entry.need.needKey, entry.skill, coach.model.BlueprintExclusion.MEASURED_IN_ANOTHER_ROLE)
            } else {
                kept += entry
            }
        }
        return kept
    }

    // ------------------------------------------------------------------------------------ composition

    /**
     * Composes the blueprint of [scope] for [cycleId] from a pool. [items] are the authored items targeting
     * each pool Skill, with their trust already taken from the store; [decisions] are the prerequisite gate's
     * answers for each item; [exposures] are what the learner has seen.
     */
    fun compose(
        scope: AssessmentScope,
        policyVersion: String,
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
        previousCycleId: String?,
        priorSessionId: Long?,
    ): AssessmentBlueprint {
        val codes = BlueprintScopes.codes(scope)
        val usedFamilies = mutableSetOf<String>()
        val usedGroups = mutableSetOf<String>()
        val slots = pool.entries.mapIndexed { index, entry ->
            slotFor(scope, "slot-${index + 1}", entry, items[entry.skill].orEmpty(), profiles, decisions, exposures,
                evaluatorAvailable, usedFamilies, usedGroups)
        }
        return AssessmentBlueprint(
            scope = scope,
            cycleId = cycleId,
            studyDay = studyDay,
            curriculumVersion = curriculumVersion,
            truthWatermark = truthWatermark,
            policyVersion = policyVersion,
            evaluatorAvailable = evaluatorAvailable,
            recentSince = recentSince,
            slots = slots,
            exclusions = pool.exclusions,
            reasonCodes = buildList {
                add(codes.due)
                add(codes.blueprintGenerated)
                if (pool.entries.isEmpty()) add(codes.noEligibleTarget)
                if (slots.any { it.status == SlotStatus.NO_VALID_ITEM }) add(codes.noValidItem)
                // A cycle that passed without its assessment leaves nothing behind (`WBA-v0` §3, `MCA-v0` §3).
                if (previousCycleId != null && previousCycleId != cycleId) add(codes.noExamDebt)
            },
            priorSessionId = priorSessionId,
        )
    }

    /**
     * Replaces the items of [slotIds] (`WBA-v0` §17, `MCA-v0` §16): after a solution was exposed, an item
     * version or its validation changed, a prerequisite changed, or the learner asked for an alternative.
     * Every other slot — and so every submitted boundary — is carried over unchanged; the replaced items and
     * their variant families are not used again, because a recomposed slot is a fresh measurement, never a
     * retry of the same one.
     */
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
        val codes = BlueprintScopes.codes(previous.scope)
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
            val recomposed = slotFor(previous.scope, slot.slotId, entry, fresh, profiles, decisions, exposures,
                evaluatorAvailable, usedFamilies, usedGroups)
            // The slot keeps its own place and reasons; a replacement is said, not hidden.
            recomposed.copy(
                sourceStateRefs = slot.sourceStateRefs,
                reasonCodes = slot.reasonCodes.filterNot { it == codes.noValidItem } +
                    listOfNotNull(
                        codes.invalidItemReplaced.takeIf { recomposed.status == SlotStatus.READY },
                        codes.noValidItem.takeIf { recomposed.status == SlotStatus.NO_VALID_ITEM },
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
        scope: AssessmentScope,
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
        val codes = BlueprintScopes.codes(scope)
        val role = entry.role
        val reasons = listOf(role.reasonCode, entry.need.trigger.reasonCode, entry.band.reasonCode)
        val base = AssessmentBlueprintSlot(
            slotId = slotId, role = role, needKey = entry.need.needKey, trigger = entry.need.trigger,
            targetSkill = entry.skill, criticality = entry.need.criticality, band = entry.band,
            sourceStateRefs = entry.need.sourceStateRefs, track = entry.need.track,
            status = SlotStatus.NO_VALID_ITEM, reasonCodes = reasons + codes.noValidItem,
        )

        // Steps 1–3 of `QAB-v0` §31 are the indexed facets: the item targets the Skill, is eligible for this
        // scope and declared for the role, and its lifecycle is selectable at all. Only then is the read
        // bounded; the per-learner filters come after the bound, as §33 orders it.
        val rejections = mutableListOf<SlotItemRejection>()
        val indexed = available
            .filter { entry.skill in it.targetSkills && scope in it.scopeEligibility && it.lifecycleStatus.selectable }
            .sortedWith(itemOrder)
        indexed.filterNot { role in it.blueprintRoles }.forEach {
            rejections += SlotItemRejection(it.ref, listOf(SlotItemRefusal.ROLE_NOT_DECLARED.id))
        }
        val examined = indexed.filter { role in it.blueprintRoles }.take(MAX_ITEMS_PER_SLOT_V0)

        val seen = exposures.map { it.resource }.toSet()
        val exposedFamilies = exposures.filter { it.solutionExposed }.mapNotNull { it.variantFamilyId }.toSet()
        val exposedItems = exposures.filter { it.solutionExposed }.map { it.resource }.toSet()

        examined.forEach { item ->
            val refusals = buildList {
                when (val fit = ItemSelection.fit(item, role.intent, scope, profiles, evaluatorAvailable)) {
                    is ItemFit.NotUsable -> {
                        add(SlotItemRefusal.NOT_USABLE_FOR_INTENT.id)
                        addAll(fit.reasons.map { it.id })
                    }
                    is ItemFit.Usable -> Unit
                }
                // A planned slot is high stakes (`PBR-v0` §3): the planner will not trust anything less.
                if (item.lifecycleStatus !in TRUSTED && SlotItemRefusal.NOT_USABLE_FOR_INTENT.id !in this) {
                    add(SlotItemRefusal.NOT_USABLE_FOR_INTENT.id)
                }
                val decision = decisions[item.ref]
                // The gate fails closed: an item it has not answered for waits (`PRG-v0`).
                if (decision == null || decision.eligibility.waits) add(SlotItemRefusal.PREREQUISITE_WAITS.id)
                if (item.ref in exposedItems || item.variantFamilyId in exposedFamilies) add(SlotItemRefusal.SOLUTION_EXPOSED.id)
                else if (item.ref in seen) add(SlotItemRefusal.ALREADY_SEEN.id)
                if (item.variantFamilyId in usedFamilies) add(SlotItemRefusal.VARIANT_FAMILY_IN_USE.id)
                item.dependencyGroupId?.let { if (it in usedGroups) add(SlotItemRefusal.DEPENDENCY_GROUP_IN_USE.id) }
                if (item.expectedActiveMinutes == null) add(SlotItemRefusal.EXPECTED_MINUTES_MISSING.id)
            }
            if (refusals.isNotEmpty()) {
                rejections += SlotItemRejection(item.ref, refusals)
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
                // `WBA-v0` §11 / `MCA-v0` §7: integrity, verification and repair are what a session is for;
                // everything else it holds is welcome but its absence does not leave the session open.
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
     * A blueprint's ready slots as the planner's candidates (`WBA-v0` §4, `MCA-v0` §15). Each slot serves the
     * **same need** the planner already opened, so a cycle adds no queue of its own, no band of its own and no
     * minutes of its own; a slot whose item the learner has since been shown is not offered again.
     */
    fun slotCandidates(blueprint: AssessmentBlueprint, servedItems: Set<VersionedRef>, title: String): List<TaskCandidate> =
        blueprint.readySlots.filterNot { it.item in servedItems }.map { slot ->
            TaskCandidate(
                id = candidateId(blueprint.scope, blueprint.cycleId, slot.slotId),
                needKey = slot.needKey,
                purpose = slot.role.purpose,
                activityKind = ACTIVITY,
                title = title,
                primarySkill = slot.targetSkill,
                costMinutes = slot.expectedActiveMinutes!!,
                validationStatus = slot.itemLifecycle!!,
                track = slot.track,
                requiredSkills = slot.itemRequiredSkills,
                atomicEvidenceBoundary = true,
                generationVersion = "${coach.model.BlueprintCodecs.format(blueprint.scope)}:${blueprint.cycleId}",
            )
        }

    fun candidateId(scope: AssessmentScope, cycleId: String, slotId: String): String = when (scope) {
        AssessmentScope.WEEKLY_BLUEPRINT -> "weekly:$cycleId:$slotId"
        AssessmentScope.MONTHLY_CAPABILITY -> "monthly:$cycleId:$slotId"
        AssessmentScope.DAILY_MICRO -> error("a daily measurement composes no blueprint")
    }

    // ------------------------------------------------------------------------------------ result

    /**
     * `WBA-v0` §25 / `MCA-v0` §20. A downstream slot whose item needs a Skill this very session has just shown,
     * cleanly and independently, to be missing cannot be scored against its own target: the snapshot it records
     * says contaminated, and the mastery engine keeps it out (`PRG-v0` §13). Independent branches are untouched.
     */
    fun contaminatedBy(slot: AssessmentBlueprintSlot, cleanlyFailedSkills: Set<VersionedRef>): Boolean =
        slot.itemRequiredSkills.any { it in cleanlyFailedSkills }

    /** The Skills a session's evidence shows cleanly missing: verified, independent, negative and not itself contaminated. */
    fun cleanlyFailedSkills(blueprint: AssessmentBlueprint, outcomes: List<BlueprintSlotOutcome>): Set<VersionedRef> {
        val slots = blueprint.slots.associateBy { it.slotId }
        return outcomes.filter { outcome ->
            outcome.evidence.any {
                it.outcome == EvidenceOutcome.NEGATIVE && it.evaluatorStatus == EvaluatorStatus.VERIFIED &&
                    it.independence == IndependenceClass.INDEPENDENT && !it.prerequisiteContaminated
            }
        }.mapNotNull { slots[it.slotId]?.targetSkill }.toSet()
    }

    /**
     * `WBA-v0` §27 / `MCA-v0` §27 from what happened in the session and what the engines said changed. There
     * is no score to compute: the result is which Objectives got which evidence, what could not be measured,
     * and the changes the canonical engines reported — none of which is inferred here from answers. The
     * monthly lists name only what a clean, independent, verified answer of the role's kind showed.
     */
    fun result(
        blueprint: AssessmentBlueprint,
        sessionId: Long,
        outcomes: List<BlueprintSlotOutcome>,
        stateChangeRefs: List<String>,
    ): AssessmentBlueprintResult {
        val codes = BlueprintScopes.codes(blueprint.scope)
        val ready = blueprint.readySlots.map { it.slotId }
        val bySlot = outcomes.associateBy { it.slotId }
        require(bySlot.keys.all { it in ready }) { "an outcome names a slot this blueprint cannot run" }
        val completed = ready.filter { bySlot[it]?.submitted == true }
        val unresolved = ready - completed.toSet()
        val evidence = outcomes.flatMap { it.evidence }
        fun usable(e: BlueprintEvidenceFact) = !e.prerequisiteContaminated && e.outcome != EvidenceOutcome.INVALID
        fun clean(e: BlueprintEvidenceFact) =
            usable(e) && e.evaluatorStatus == EvaluatorStatus.VERIFIED && e.independence == IndependenceClass.INDEPENDENT
        val status = when {
            completed.isEmpty() -> BlueprintSessionStatus.DEFERRED
            unresolved.isEmpty() -> BlueprintSessionStatus.COMPLETE
            else -> BlueprintSessionStatus.PARTIAL
        }
        val contaminated = outcomes.filter { o -> o.evidence.any { it.prerequisiteContaminated } }.map { it.slotId }
        val recheck = evidence.filter { usable(it) && it.independence != IndependenceClass.INDEPENDENT }.map { it.objective }.distinct()

        val slotOf = blueprint.slots.associateBy { it.slotId }
        fun cleanOf(kind: RoleEvidenceKind, positiveOnly: Boolean) = outcomes.filter { slotOf[it.slotId]?.role?.evidenceKind == kind }
            .flatMap { o -> o.evidence.filter { clean(it) && (!positiveOnly || it.outcome == EvidenceOutcome.POSITIVE) }.map { o.slotId to it } }
        fun skillsOf(pairs: List<Pair<String, BlueprintEvidenceFact>>) = pairs.mapNotNull { slotOf[it.first]?.targetSkill }.distinct()

        return AssessmentBlueprintResult(
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
                if (evidence.isNotEmpty()) add(codes.evidenceRecorded)
                if (status == BlueprintSessionStatus.PARTIAL) add(codes.partialSession)
                if (unresolved.isNotEmpty()) add(codes.incompleteNotFailure)
                if (recheck.isNotEmpty()) add(codes.assistanceRecheckRequired)
                if (contaminated.isNotEmpty()) add(codes.prerequisiteContaminated)
                if (stateChangeRefs.isNotEmpty()) add(codes.replanAfterResult)
            },
            assessmentPolicyVersion = blueprint.policyVersion,
            // A revalidation is a clean positive: a clean negative on a critical or retained Skill opens
            // verification through the engines, it does not revalidate anything (`MCA-v0` §24).
            revalidatedCriticalSkills = skillsOf(cleanOf(RoleEvidenceKind.CRITICAL_REVALIDATION, positiveOnly = true)),
            revalidatedRetentionSkills = skillsOf(cleanOf(RoleEvidenceKind.RETENTION_REVALIDATION, positiveOnly = true)),
            transferEvidenceObjectives = cleanOf(RoleEvidenceKind.TRANSFER, positiveOnly = false).map { it.second.objective }.distinct(),
            integratedEvidenceObjectives = cleanOf(RoleEvidenceKind.INTEGRATION, positiveOnly = false).map { it.second.objective }.distinct(),
        )
    }
}
