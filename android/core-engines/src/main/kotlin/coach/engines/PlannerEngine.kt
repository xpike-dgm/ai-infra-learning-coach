package coach.engines

import coach.model.BlockingScope
import coach.model.CandidateDisposition
import coach.model.CandidateTrace
import coach.model.CapacityProfile
import coach.model.CapacitySource
import coach.model.ContinuationValue
import coach.model.Criticality
import coach.model.DailyCapacity
import coach.model.DailyCapacityInput
import coach.model.DecisionValue
import coach.model.DurationFit
import coach.model.EvidenceSeverity
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.NeedDisposition
import coach.model.NeedTrace
import coach.model.NeedTrigger
import coach.model.PlanTrace
import coach.model.PlannedEntry
import coach.model.PrerequisiteDecision
import coach.model.PrerequisiteEligibility
import coach.model.PriorityBand
import coach.model.RankVector
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.model.StarvationBucket
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.TemporalUrgency
import coach.model.TrackBalance
import coach.model.VersionedRef

/**
 * The planner (12C): which of today's open needs are served, by which task, within the minutes the
 * learner actually has.
 *
 * It follows `PDT-v0` §17's gate order and nothing else: current state → needs → bounded candidates →
 * trust → prerequisite eligibility (`PRG-v0`) → priority (`PBR-v0`) → capacity fit (D-033). Priority
 * never rescues a blocked or invalid candidate, capacity never rewrites priority, and a need no task
 * served stays open — it is not tomorrow's debt and it is not a failure.
 *
 * Everything is a pure function of its arguments (`PBR-v0` §19): the same state, candidates and
 * capacity always give the same plan.
 */
object PlannerEngine {

    const val PLANNER_MODEL = "PLNX-v0"
    /** The trace format this planner writes; `PlanTraceCodec.FORMAT` is the authority. 12D moved it to `/2`, 12E to `/3`. */
    const val TRACE_SCHEMA = "planner_trace/3"

    // D-033 §3 and §19: engineering defaults the learner can change, not ideal study times. They are
    // what a settings screen (16D) offers; the planner itself never substitutes one for a setting.
    const val SHORT_PROFILE_MINUTES_V0 = 30
    const val NORMAL_PROFILE_MINUTES_V0 = 60
    const val INTENSIVE_PROFILE_MINUTES_V0 = 90

    /** D-033 §4: `planning_reserve_ratio_v0 = 0.10`, in whole percent so the floor is exact. */
    const val PLANNING_RESERVE_PERCENT_V0 = 10

    /** D-033 §5. Below this, only work that genuinely fits is offered and nothing new is taught. */
    const val MINIMUM_PLANNABLE_BLOCK_MINUTES_V0 = 10

    /**
     * 3B §17 leaves the per-need cap to 12C. It is an engineering bound on how much the planner reads,
     * with no learning meaning; the first candidates in a stable order are kept, and 18E may move it.
     */
    const val MAX_CANDIDATES_PER_NEED_V0 = 5

    val POLICY_VERSIONS: Map<String, String> = linkedMapOf(
        "mastery_policy" to MasteryEngine.MASTERY_FORMULA_VERSION,
        "retention_policy" to "RVR-v0",
        "capacity_policy" to "D-033",
        "task_taxonomy_policy" to "D-034",
        "priority_policy" to "PBR-v0",
        "prerequisite_policy" to PrerequisiteEngine.PREREQUISITE_POLICY_VERSION,
        "diagnostic_policy" to "VDW-v0",
        "reentry_policy" to "SRR-v0",
        "explainability_policy" to "PDT-v0",
        "planner" to PLANNER_MODEL,
    )

    /** Purposes whose evidence can settle mastery, so an unvalidated candidate may not serve them. */
    private val HIGH_STAKES = setOf(TaskPurpose.ASSESS, TaskPurpose.RETAIN, TaskPurpose.DIAGNOSE)
    private val TRUSTED_FOR_HIGH_STAKES = setOf(LifecycleStatus.VALIDATED, LifecycleStatus.TRUSTED, LifecycleStatus.DEPRECATED)

    /** `KGC-v0` §27: a Skill new learning may start on. `deprecated` keeps its history but takes no new starts. */
    private const val SKILL_PUBLISHED = "published"
    private val SKILL_ON_ROUTE = setOf("published", "deprecated")

    /** `PRG-v0` §20's reason input for "dependent work waited, independent work went ahead". */
    const val INDEPENDENT_BRANCH_AVAILABLE = "independent_branch_available"

    /** `SPWX-v0`'s state for open remediation, as the weakness axis carries it (13). */
    private const val REMEDIATION_REQUIRED = "remediation_required"

    /** Whether a Skill with this lifecycle is on the learner's route at all (`KGC-v0` §27). */
    fun onRoute(lifecycleStatus: String): Boolean = lifecycleStatus in SKILL_ON_ROUTE

    // ------------------------------------------------------------------------------------ capacity

    /**
     * D-033 §2 and §4. The first value present wins: today's override, then the profile chosen for the
     * day, then the scheduled default, then the normal profile. The hard budget is never exceeded.
     */
    fun resolveCapacity(input: DailyCapacityInput): DailyCapacity {
        val override = input.todayOverrideMinutes
        val scheduled = input.scheduledDefaultMinutes
        val (source, minutes) = when {
            override != null -> CapacitySource.TODAY_OVERRIDE to override
            input.selectedProfile == CapacityProfile.SHORT -> CapacitySource.SELECTED_SHORT to input.shortProfileMinutes
            input.selectedProfile == CapacityProfile.NORMAL -> CapacitySource.SELECTED_NORMAL to input.normalProfileMinutes
            input.selectedProfile == CapacityProfile.INTENSIVE -> CapacitySource.SELECTED_INTENSIVE to input.intensiveProfileMinutes
            scheduled != null -> CapacitySource.SCHEDULED_DEFAULT to scheduled
            else -> CapacitySource.NORMAL_PROFILE to input.normalProfileMinutes
        }
        return capacityOf(source, minutes)
    }

    /**
     * D-033 §4 and §5 for a given number of minutes: the whole day's, or what remains of it after a
     * replan (§8, 12D) — the same rule either way.
     */
    fun capacityOf(source: CapacitySource, minutes: Int): DailyCapacity {
        val below = minutes < MINIMUM_PLANNABLE_BLOCK_MINUTES_V0
        // A reserve taken from a few minutes would leave nothing to plan; §4 lets it relax so that one
        // genuine micro-task can still run, and the hard budget still holds.
        val planning = if (below) minutes else minutes * (100 - PLANNING_RESERVE_PERCENT_V0) / 100
        return DailyCapacity(source, minutes, planning, reserveRelaxed = below, belowMinimumBlock = below)
    }

    // ------------------------------------------------------------------------------------ needs

    /**
     * 3B §2.1 needs that current state alone can open. Each comes from the axis its engine owns and
     * from nothing else: remediation from the weakness axis, verification from a contradicted mastery or
     * a retention verification, review from retention, continuation from mastery in progress, new
     * learning from mastery not yet evidenced. A Skill off the route opens nothing new.
     *
     * Opportunities (diagnostic, reinforcement, integration), paused work and the parallel-track cadence
     * need content and history this engine is not given; they arrive as needs from their owners.
     */
    fun needsFromSkillStates(states: List<SkillPlanningState>): List<LearningNeed> =
        states.flatMap { state ->
            if (!onRoute(state.lifecycleStatus)) return@flatMap emptyList()
            val criticality = if (state.critical) Criticality.CRITICAL_PREREQUISITE else Criticality.REQUIRED
            val refs = listOfNotNull(state.snapshotRef)
            fun need(trigger: NeedTrigger, severity: EvidenceSeverity, urgency: TemporalUrgency,
                     continuation: ContinuationValue) = LearningNeed(
                needKey = "${trigger.id}:${state.skill}",
                trigger = trigger,
                targetSkills = listOf(state.skill),
                criticality = criticality,
                sourceStateRefs = refs,
                evidenceSeverity = severity,
                temporalUrgency = urgency,
                continuation = continuation,
            )
            buildList {
                if (state.weaknessAxis == REMEDIATION_REQUIRED) {
                    add(need(NeedTrigger.REMEDIATION_REQUIRED, EvidenceSeverity.CONFIRMED_REPEATED_FAILURE,
                        TemporalUrgency.NOT_TIME_SENSITIVE, ContinuationValue.FRESH_NEW_CONTEXT))
                }
                if (state.mastery == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE ||
                    (state.mastery == MasteryAxisState.CONFIRMED_CURRENT && state.retention == RetentionAxis.VERIFICATION_DUE)
                ) {
                    add(need(NeedTrigger.VERIFICATION_DUE, EvidenceSeverity.CLEAN_CONTRADICTION_OR_VERIFICATION_DUE,
                        TemporalUrgency.NOT_TIME_SENSITIVE, ContinuationValue.FRESH_NEW_CONTEXT))
                }
                if (state.mastery == MasteryAxisState.CONFIRMED_CURRENT && state.retention == RetentionAxis.REVIEW_DUE) {
                    // `review_due` is due, never forgotten; how overdue is RVR-v0's to say (13).
                    add(need(NeedTrigger.RETENTION_REVIEW_DUE, EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
                        TemporalUrgency.JUST_DUE, ContinuationValue.FRESH_NEW_CONTEXT))
                }
                if (state.mastery == MasteryAxisState.DEVELOPING_INDEPENDENT ||
                    state.mastery == MasteryAxisState.DEVELOPING_WITH_SUPPORT
                ) {
                    add(need(NeedTrigger.CONTINUE_LEARNING, EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
                        TemporalUrgency.NOT_TIME_SENSITIVE, ContinuationValue.ACTIVE_LEARNING_CONTEXT))
                }
                if ((state.mastery == null || state.mastery == MasteryAxisState.NOT_YET_EVIDENCED) &&
                    state.lifecycleStatus == SKILL_PUBLISHED
                ) {
                    add(need(NeedTrigger.NEW_LEARNING, EvidenceSeverity.NO_NEGATIVE_EVIDENCE,
                        TemporalUrgency.NOT_TIME_SENSITIVE, ContinuationValue.FRESH_NEW_CONTEXT))
                }
            }
        }.sortedBy { it.needKey }

    // ------------------------------------------------------------------------------------ priority

    /**
     * `PBR-v0` §7's decision table. P0 is not given for a `critical` label: only a critical Skill that
     * really holds dependent work back gets it, and `review_due` alone never does.
     */
    fun band(need: LearningNeed, blocks: BlockingScope, starvation: StarvationBucket): PriorityBand {
        val blocking = blocks != BlockingScope.NON_BLOCKING
        val critical = need.criticality == Criticality.CRITICAL_PREREQUISITE
        val base = when (need.trigger) {
            NeedTrigger.VERIFICATION_DUE, NeedTrigger.REMEDIATION_REQUIRED ->
                if (blocking && critical) PriorityBand.P0 else PriorityBand.P1
            NeedTrigger.WEAKNESS_DETECTED -> PriorityBand.P1
            NeedTrigger.RETENTION_REVIEW_DUE ->
                if (critical || need.temporalUrgency == TemporalUrgency.OVERDUE_HIGH) PriorityBand.P2 else PriorityBand.P3
            NeedTrigger.CONTINUE_LEARNING ->
                if (need.continuation == ContinuationValue.PAUSED_SAFE_CHECKPOINT) PriorityBand.P2 else PriorityBand.P3
            NeedTrigger.NEW_LEARNING, NeedTrigger.PARALLEL_TRACK_DUE, NeedTrigger.DIAGNOSTIC_OPPORTUNITY -> PriorityBand.P3
            NeedTrigger.REINFORCEMENT_OPPORTUNITY -> PriorityBand.P4
            NeedTrigger.INTEGRATION_OPPORTUNITY -> if (need.requiredByCurriculum) PriorityBand.P3 else PriorityBand.P4
        }
        // §6.5: promotion lifts planned progress one band and no further; it never passes repair work
        // and never turns an optional P4 into a P2.
        return if (base == PriorityBand.P3 && starvation == StarvationBucket.PROMOTE) PriorityBand.P2 else base
    }

    // ------------------------------------------------------------------------------------ the plan

    private class Assessed(
        val candidate: TaskCandidate,
        val eligibility: PrerequisiteEligibility?,
        val usable: Boolean,
        val disposition: CandidateDisposition?,
        val reasons: List<String>,
        /** The Skills the eligibility reason is about (`PDT-v0` §7 `related_refs`, §11). */
        val related: List<VersionedRef> = emptyList(),
    )

    /**
     * `PDT-v0` §16 for an initial generation. [decisions] are the prerequisite gate's answers for each
     * candidate id; a candidate with no answer is treated as blocked, because the gate fails closed.
     * [starvation] and [trackBalance] are the pressures history has built up; with no calibrated
     * threshold (18C) the product supplies none, and nothing here invents one.
     */
    fun plan(
        capacity: DailyCapacity,
        needs: List<LearningNeed>,
        candidates: List<TaskCandidate>,
        decisions: Map<String, PrerequisiteDecision>,
        studyDay: String,
        curriculumVersion: Int,
        truthWatermark: Long,
        starvation: Map<String, StarvationBucket> = emptyMap(),
        trackBalance: Set<String> = emptySet(),
        skillsNotOnRoute: Int = 0,
    ): PlanTrace {
        val openNeeds = needs.associateBy { it.needKey }
        val candidateTraces = mutableListOf<CandidateTrace>()
        fun trace(a: TaskCandidate, eligibility: PrerequisiteEligibility?, disposition: CandidateDisposition, reasons: List<String>,
                  related: List<VersionedRef> = emptyList()) {
            candidateTraces += CandidateTrace(a.id, a.needKey, a.validationStatus, eligibility, a.costMinutes, disposition, reasons, related)
        }

        // Bounded candidate set per need, in a stable order: deprecated versions last (preference is
        // the selector's, `LifecycleStatus.selectable`), then by id. A candidate for a need that is not
        // open has nothing left to serve.
        val bounded = candidates.groupBy { it.needKey }.flatMap { (needKey, group) ->
            val ordered = group.sortedWith(compareBy({ it.validationStatus == LifecycleStatus.DEPRECATED }, { it.id }))
            if (needKey !in openNeeds) {
                ordered.forEach { trace(it, null, CandidateDisposition.RESOLVED_BEFORE_SELECTION, listOf("selection.resolved_before_selection")) }
                emptyList()
            } else {
                ordered.drop(MAX_CANDIDATES_PER_NEED_V0).forEach {
                    trace(it, null, CandidateDisposition.DUPLICATE_SUPPRESSED, listOf("candidate.duplicate_suppressed"))
                }
                ordered.take(MAX_CANDIDATES_PER_NEED_V0)
            }
        }

        // Trust, then the prerequisite gate. Priority is not consulted for either.
        val assessed = bounded.map { candidate ->
            val decision = decisions[candidate.id]
            when {
                !candidate.validationStatus.selectable ->
                    Assessed(candidate, null, false, CandidateDisposition.INVALID_CANDIDATE, listOf("candidate.invalid_content"))
                candidate.purpose in HIGH_STAKES && candidate.validationStatus !in TRUSTED_FOR_HIGH_STAKES ->
                    Assessed(candidate, null, false, CandidateDisposition.INVALID_CANDIDATE,
                        listOf("candidate.untrusted_for_high_stakes_use"))
                decision == null ->
                    Assessed(candidate, PrerequisiteEligibility.BLOCKED, false, CandidateDisposition.BLOCKED_PREREQUISITE,
                        listOf("eligibility.blocked_hard_prerequisite"))
                decision.eligibility == PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA ->
                    Assessed(candidate, decision.eligibility, false, CandidateDisposition.INVALID_CANDIDATE,
                        listOf("candidate.invalid_prerequisite_metadata", "eligibility.invalid_prerequisite_metadata"))
                decision.eligibility == PrerequisiteEligibility.BLOCKED ->
                    Assessed(candidate, decision.eligibility, false, CandidateDisposition.BLOCKED_PREREQUISITE,
                        listOf(when {
                            decision.hardBlockerSkills.isNotEmpty() -> "eligibility.blocked_hard_prerequisite"
                            decision.requiresStrictPrerequisiteConfidence -> "eligibility.blocked_strict_prerequisite_confidence"
                            else -> "eligibility.blocked_critical_verification"
                        }),
                        related = relatedSkills(decision))
                else -> Assessed(candidate, decision.eligibility, true, null, listOf(eligibilityCode(decision)),
                    related = relatedSkills(decision))
            }
        }
        assessed.filter { !it.usable }.forEach { trace(it.candidate, it.eligibility, it.disposition!!, it.reasons, it.related) }

        // Which Skills really hold dependent work back (`PBR-v0` §6.1). Work the learner is already in
        // is the current path; a new start is the next ready dependency.
        val holdsBack = mutableMapOf<String, BlockingScope>()
        assessed.filter { it.eligibility == PrerequisiteEligibility.BLOCKED }.forEach { blocked ->
            val decision = decisions[blocked.candidate.id] ?: return@forEach
            val scope = if (openNeeds[blocked.candidate.needKey]?.trigger == NeedTrigger.CONTINUE_LEARNING) {
                BlockingScope.BLOCKS_CURRENT_REQUIRED_PATH
            } else {
                BlockingScope.BLOCKS_NEXT_READY_DEPENDENCY
            }
            (decision.hardBlockerSkills + decision.uncertainSkills).forEach { skill ->
                val key = skill.toString()
                holdsBack[key] = minOf(holdsBack[key] ?: BlockingScope.NON_BLOCKING, scope)
            }
        }

        val usableByNeed = assessed.filter { it.usable }.groupBy { it.candidate.needKey }
        val budget = capacity.planningBudgetMinutes

        // Below the minimum block nothing new is taught (D-033 §5); that is a time fact, not a verdict.
        fun offerable(a: Assessed) = !(capacity.belowMinimumBlock && a.candidate.purpose == TaskPurpose.TEACH)

        fun fitAgainst(alternatives: List<Assessed>, remaining: Int): DurationFit {
            val offered = alternatives.filter(::offerable)
            val first = offered.firstOrNull() ?: return DurationFit.CANNOT_FIT_TODAY
            return when {
                first.candidate.costMinutes <= remaining -> DurationFit.FITS_REMAINING
                first.candidate.splittable && first.candidate.minimumSafeChunkMinutes!! <= remaining -> DurationFit.FITS_VIA_SAFE_SPLIT
                offered.drop(1).any { it.candidate.costMinutes <= remaining } -> DurationFit.FITS_VIA_SMALLER_ALTERNATIVE
                else -> DurationFit.CANNOT_FIT_TODAY
            }
        }

        data class Ranked(val need: LearningNeed, val band: PriorityBand, val rank: RankVector, val reasons: List<String>)

        val ranked = needs.map { need ->
            val scope = need.targetSkills.map { holdsBack[it.toString()] ?: BlockingScope.NON_BLOCKING }.minOrNull()
                ?: BlockingScope.NON_BLOCKING
            val pressure = starvation[need.needKey] ?: StarvationBucket.NONE
            val band = band(need, scope, pressure)
            val rank = RankVector(
                blockingScope = scope,
                criticality = need.criticality,
                evidenceSeverity = need.evidenceSeverity,
                temporalUrgency = need.temporalUrgency,
                starvation = pressure,
                continuation = need.continuation,
                decisionValue = need.decisionValue,
                trackBalance = if (need.needKey in trackBalance) TrackBalance.PRESSURE else TrackBalance.NONE,
                durationFit = fitAgainst(usableByNeed[need.needKey].orEmpty(), budget),
                tieBreakKey = need.needKey,
            )
            val reasons = buildList {
                add(band.reasonCode)
                scope.reasonCode?.let(::add)
                if (band != band(need, scope, StarvationBucket.NONE)) add("priority.starvation_promoted")
                if (need.continuation != ContinuationValue.FRESH_NEW_CONTEXT) add("priority.continuation_value")
                if (need.decisionValue == DecisionValue.DECISIVE) add("priority.decision_value")
                if (rank.trackBalance == TrackBalance.PRESSURE) add("priority.track_balance_pressure")
            }
            Ranked(need, band, rank, reasons)
        }.sortedWith(compareBy({ it.band }, { it.rank }))

        // Selection: semantic priority first, then physical fit (`PBR-v0` §9). Never the most tasks
        // per minute, and never a knapsack.
        var remaining = budget
        val selected = mutableListOf<PlannedEntry>()
        val needTraces = mutableListOf<NeedTrace>()
        ranked.forEach { r ->
            val need = r.need
            val alternatives = usableByNeed[need.needKey].orEmpty()
            fun finish(disposition: NeedDisposition, chosen: String?, reasons: List<String>) {
                needTraces += NeedTrace(need.needKey, need.trigger, need.targetSkills, need.sourceStateRefs, r.band, r.rank,
                    r.reasons, chosen, disposition, reasons)
            }
            if (alternatives.isEmpty()) {
                val blockedHere = assessed.any { it.candidate.needKey == need.needKey && it.disposition == CandidateDisposition.BLOCKED_PREREQUISITE }
                val anyCandidate = assessed.any { it.candidate.needKey == need.needKey }
                when {
                    blockedHere -> finish(NeedDisposition.BLOCKED, null, listOf("selection.blocked_prerequisite"))
                    anyCandidate -> finish(NeedDisposition.NO_VALID_CANDIDATE, null, listOf("selection.invalid_candidate"))
                    // No task exists for it at all. PDT-v0 has no reason code for that and none is
                    // invented: the disposition is the whole fact.
                    else -> finish(NeedDisposition.NO_VALID_CANDIDATE, null, emptyList())
                }
                return@forEach
            }
            val offered = alternatives.filter(::offerable)
            alternatives.filterNot(::offerable).forEach {
                trace(it.candidate, it.eligibility, CandidateDisposition.ELIGIBLE_CAPACITY_DEFERRED,
                    it.reasons + "capacity.deferred_not_enough_time", it.related)
            }
            val choice = offered.firstOrNull()
            fun supersedeAllBut(kept: Assessed) = offered.filter { it !== kept }.forEach {
                trace(it.candidate, it.eligibility, CandidateDisposition.SUPERSEDED_SAME_NEED_ALTERNATIVE,
                    it.reasons + "selection.same_need_alternative_not_used", it.related)
            }
            fun take(a: Assessed, minutes: Int, split: Boolean) {
                selected += PlannedEntry(
                    position = selected.size, candidateId = a.candidate.id, needKey = need.needKey,
                    purpose = a.candidate.purpose, activityKind = a.candidate.activityKind, title = a.candidate.title,
                    primarySkill = a.candidate.primarySkill, track = a.candidate.track,
                    estimatedMinutes = a.candidate.costMinutes, plannedMinutes = minutes, split = split,
                )
                remaining -= minutes
            }
            when {
                choice == null -> {
                    finish(NeedDisposition.ELIGIBLE_NOT_SELECTED, null, listOf("capacity.deferred_not_enough_time"))
                }
                choice.candidate.costMinutes <= remaining -> {
                    take(choice, choice.candidate.costMinutes, split = false)
                    trace(choice.candidate, choice.eligibility, CandidateDisposition.SELECTED,
                        choice.reasons + listOf("selection.selected", "capacity.selected_within_budget"), choice.related)
                    supersedeAllBut(choice)
                    finish(NeedDisposition.SELECTED, choice.candidate.id, listOf("selection.selected", "capacity.selected_within_budget"))
                }
                choice.candidate.splittable && choice.candidate.minimumSafeChunkMinutes!! <= remaining -> {
                    // A split plans the safe part that fits; the rest resumes from its checkpoint later.
                    take(choice, remaining, split = true)
                    trace(choice.candidate, choice.eligibility, CandidateDisposition.SELECTED_SPLIT,
                        choice.reasons + listOf("selection.selected_split", "capacity.split_to_fit"), choice.related)
                    supersedeAllBut(choice)
                    finish(NeedDisposition.PARTIALLY_SERVED, choice.candidate.id, listOf("selection.selected_split", "capacity.split_to_fit"))
                }
                else -> {
                    val smaller = offered.drop(1).firstOrNull { it.candidate.costMinutes <= remaining }
                    if (smaller != null) {
                        take(smaller, smaller.candidate.costMinutes, split = false)
                        trace(smaller.candidate, smaller.eligibility, CandidateDisposition.SELECTED_SMALLER_ALTERNATIVE,
                            smaller.reasons + listOf("selection.selected_smaller_alternative", "capacity.smaller_alternative_to_fit"),
                            smaller.related)
                        supersedeAllBut(smaller)
                        finish(NeedDisposition.SELECTED, smaller.candidate.id,
                            listOf("selection.selected_smaller_alternative", "capacity.smaller_alternative_to_fit"))
                    } else {
                        // The need stays open. It did not lose to anything less important; it did not fit.
                        offered.forEach {
                            trace(it.candidate, it.eligibility, CandidateDisposition.ELIGIBLE_CAPACITY_DEFERRED,
                                it.reasons + "capacity.deferred_not_enough_time", it.related)
                        }
                        finish(NeedDisposition.ELIGIBLE_NOT_SELECTED, null,
                            listOf("selection.not_selected_capacity", "capacity.deferred_not_enough_time"))
                    }
                }
            }
        }

        val plannedMinutes = selected.sumOf { it.plannedMinutes }
        val selectedIds = selected.map { it.candidateId }.toSet()
        val invariants = linkedMapOf(
            "planned_within_planning_budget" to (plannedMinutes <= capacity.planningBudgetMinutes),
            "hard_budget_not_exceeded" to (plannedMinutes <= capacity.hardBudgetMinutes),
            "no_blocked_or_invalid_candidate_selected" to assessed.none { !it.usable && it.candidate.id in selectedIds },
            "one_task_per_need" to (selected.map { it.needKey }.toSet().size == selected.size),
            "no_teaching_below_minimum_block" to
                (!capacity.belowMinimumBlock || selected.none { it.purpose == TaskPurpose.TEACH }),
        )
        return PlanTrace(
            generationKind = "initial",
            studyDay = studyDay,
            curriculumVersion = curriculumVersion,
            truthWatermark = truthWatermark,
            policyVersions = POLICY_VERSIONS,
            capacity = capacity,
            needs = needTraces,
            candidates = candidateTraces.sortedWith(compareBy({ it.needKey }, { it.candidateId })),
            selected = selected,
            planReasonCodes = buildList {
                add(capacity.source.reasonCode)
                add(if (capacity.reserveRelaxed) "capacity.reserve_relaxed_for_microtask" else "capacity.reserve_applied")
                if (invariants.getValue("hard_budget_not_exceeded")) add("capacity.hard_budget_not_exceeded")
                // `PRG-v0` §20, handed to the planner by 12B: some work waited on a prerequisite and other,
                // independent work went ahead — the branch that waited did not stop the day.
                if (selected.isNotEmpty() && needTraces.any { it.disposition == NeedDisposition.BLOCKED }) {
                    add(INDEPENDENT_BRANCH_AVAILABLE)
                }
            },
            invariantChecks = invariants,
            skillsNotOnRoute = skillsNotOnRoute,
        )
    }

    /**
     * `PDT-v0` §7 `related_refs` and §11: the Skills the gate's answer is about, in the gate's own order.
     * A waiting candidate names its hard blockers, or — when none is missing outright — the prerequisites
     * whose confidence it waits on; a candidate that went ahead names what it went ahead with.
     */
    private fun relatedSkills(decision: PrerequisiteDecision): List<VersionedRef> = when {
        decision.eligibility == PrerequisiteEligibility.BLOCKED ->
            decision.hardBlockerSkills.ifEmpty { decision.uncertainSkills }
        decision.eligibility == PrerequisiteEligibility.CONDITIONAL_ELIGIBLE -> decision.uncertainSkills
        decision.eligibility == PrerequisiteEligibility.ELIGIBLE_WITH_SUPPORT -> decision.softGapSkills
        else -> decision.reviewDueSkills
    }

    private fun eligibilityCode(decision: PrerequisiteDecision): String = when {
        decision.eligibility == PrerequisiteEligibility.CONDITIONAL_ELIGIBLE -> "eligibility.conditional_uncertain"
        decision.eligibility == PrerequisiteEligibility.ELIGIBLE_WITH_SUPPORT -> "eligibility.soft_gap_support"
        decision.reviewDueSkills.isNotEmpty() -> "eligibility.ready_due_allowed"
        else -> "eligibility.ready"
    }
}
