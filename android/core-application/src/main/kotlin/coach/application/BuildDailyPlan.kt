package coach.application

import coach.engines.PlannerEngine
import coach.engines.ReplanEngine
import coach.engines.WeaknessEngine
import coach.model.DailyCapacityInput
import coach.model.GenerationKind
import coach.model.PlanTrace
import coach.model.PlanTraceCodec
import coach.model.PrerequisiteCandidate
import coach.model.ReplanTrigger
import coach.model.ResumeContextCodec
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * Producing today's plan (12C), and replacing it (12D) — `PDT-v0`'s initial, replan and re-entry
 * generations.
 *
 * The plan is **truth**: `plan_version`, its `planned_task` rows and its decision trace are appended in
 * one transaction and never edited, so a later plan is a new version and an earlier one still says
 * exactly what it said. A new version always has a reason: no plan yet, a new study day, or an event.
 * Asked again on the same day with no event, the planner returns the plan it already has.
 *
 * `planned_task` carries only the Skill and the position. Everything else a reader needs — purpose,
 * title, activity, minutes, and why — is in the trace, keyed by position, because `DDM-v0` names no
 * column for it and 10D forbade inventing one.
 */
class BuildDailyPlan(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
    /**
     * Whether an evaluator can run today (13F). It decides which diagnostic items can measure anything; unknown is
     * `false`, so only deterministically checked items are routed — never one whose answer nothing could check.
     */
    private val evaluatorAvailable: Boolean = false,
) {
    /**
     * What happened, for a replan within one study day (D-033 §16). [keptPositions] are the previous
     * plan's tasks the learner has started or finished: `DDM-v0` gives an attempt no link to a planned
     * task, so the caller that ran them reports them, and each is checked against the previous plan.
     */
    data class ReplanRequest(
        val trigger: ReplanTrigger,
        val capacity: DailyCapacityInput,
        val keptPositions: Set<Int> = emptySet(),
        val remainingMinutes: Int? = null,
    )

    sealed interface Built {
        /** No curriculum is published, so there is nothing to plan against and nothing is written. */
        data object NothingPublished : Built

        /** Today already has a plan and nothing asked for a new one; nothing is written. */
        data class AlreadyPlanned(val planVersionId: Long) : Built

        /** The replan could not be made honestly; nothing is written, and the reason says why. */
        data class Refused(val reason: String) : Built

        data class Planned(val planVersionId: Long, val trace: PlanTrace) : Built
    }

    fun replan(request: ReplanRequest): Built = build(request.capacity, request)

    fun build(capacity: DailyCapacityInput, replan: ReplanRequest? = null): Built {
        // Read first, like every projection: the trace can then never claim to have seen more truth
        // than the state it was planned from.
        val watermark = persistence.truthWatermark()
        val curriculumVersion = persistence.latestCurriculumVersion() ?: return Built.NothingPublished
        val now = clock.now()

        val previous = persistence.latestPlan()
        val kind = ReplanEngine.classify(previous?.recordedAt?.studyDay, now.studyDay)
        val previousTrace = previous?.traceText?.let(PlanTraceCodec::decode)
        if (kind == GenerationKind.REPLAN) {
            if (replan == null) return Built.AlreadyPlanned(previous!!.planVersionId)
            if (previousTrace == null) return Built.Refused("the plan being replaced cannot be read")
            val known = previousTrace.selected.map { it.position }.toSet()
            if (!known.containsAll(replan.keptPositions)) {
                return Built.Refused("kept tasks ${replan.keptPositions - known} are not in the plan being replaced")
            }
            if (replan.trigger.setsRemainingTime && replan.remainingMinutes == null) {
                return Built.Refused("${replan.trigger.id} must say how much time remains")
            }
        }

        // The day is the only thing that moves a retention schedule (`RVR-v0` §7, 13C): a review whose day
        // has come is `review_due` before the needs are read. It writes no truth and changes no competence.
        RefreshDueRetention(persistence, clock).refresh()
        val skills = persistence.publishedSkills()
        val states = PlanningStates.read(persistence, skills)
        // A stored pause is a continuation signal for the need it names; a row that does not decode is
        // not a pause anyone can resume.
        val pauses = persistence.resumeCheckpointRows()
            .mapNotNull { row -> row.record.payload["context"]?.let(ResumeContextCodec::decode) }
        // `weakness_detected` is supplied by its owner (12C's list; `WLRM-v0` §8, 13D), from the axis it wrote, and
        // `diagnostic_opportunity` by the learner's open diagnostic (13F, `VDW-v0` §3), from its projection.
        val diagnostic = DiagnosticPlanning.read(persistence, states)
        // `transfer_opportunity` by its owner (15G, `D-120`): a learned Skill with an unseen cross-topic item and no
        // clean transfer measurement. Only the month's transfer slot serves it; no daily task does.
        val paused = ReplanEngine.withPausedWork(
            PlannerEngine.needsFromSkillStates(states) + WeaknessEngine.needs(states) + diagnostic?.needs.orEmpty() +
                TransferPlanning.needs(persistence, content, states), pauses)
        val kept = if (kind == GenerationKind.REPLAN) {
            previousTrace!!.selected.filter { it.position in replan!!.keptPositions }
        } else {
            emptyList()
        }
        val needs = ReplanEngine.unservedNeeds(paused.needs, kept)
        // This week's assessment slots serve needs already open here (13A); they add no queue of their own. The
        // diagnostic's next checks serve its own needs (13F).
        val candidates = needs.flatMap { content.taskCandidates(it) } + BlueprintSlots.candidates(persistence, now.studyDay, needs) +
            DiagnosticPlanning.candidates(persistence, content, diagnostic, needs, evaluatorAvailable)
        // A lesson whose Objectives were waived is not taught again; one still under the fast path waits (`VDW-v0` §17).
        val coverage = DiagnosticPlanning.holds(persistence, candidates, diagnostic)

        // The prerequisite gate is asked about every candidate before priority is computed at all.
        val gate = ResolvePrerequisites(persistence)
        val decisions = candidates.associate { candidate ->
            candidate.id to gate.resolve(
                PrerequisiteCandidate(
                    candidateId = candidate.id,
                    target = candidate.primarySkill,
                    requiredSkills = candidate.requiredSkills,
                    requiresStrictPrerequisiteConfidence = candidate.requiresStrictPrerequisiteConfidence,
                )
            )
        }

        val budget = if (kind == GenerationKind.REPLAN) {
            val request = replan!!
            val minutes = ReplanEngine.remainderMinutes(
                trigger = request.trigger,
                previous = previousTrace!!,
                preservedMinutes = kept.sumOf { it.plannedMinutes },
                newDayMinutes = PlannerEngine.resolveCapacity(request.capacity).hardBudgetMinutes,
                declaredRemainingMinutes = request.remainingMinutes,
            )
            ReplanEngine.remainderCapacity(request.trigger, previousTrace, minutes)
        } else {
            PlannerEngine.resolveCapacity(capacity)
        }

        val fresh = PlannerEngine.plan(
            capacity = budget,
            needs = needs,
            candidates = candidates,
            decisions = decisions,
            studyDay = now.studyDay,
            curriculumVersion = curriculumVersion,
            truthWatermark = watermark,
            skillsNotOnRoute = skills.count { !PlannerEngine.onRoute(it.lifecycleStatus) },
            coverage = coverage,
        )
        val trace = when (kind) {
            GenerationKind.INITIAL -> fresh
            GenerationKind.REENTRY -> ReplanEngine.composeReentry(
                fresh = fresh,
                previousPlanVersionId = previous!!.planVersionId,
                lastPlannedStudyDay = previous.recordedAt.studyDay,
                stalePlannedTaskCount = previous.plannedTaskCount,
                paused = paused,
                states = states,
            )
            GenerationKind.REPLAN -> ReplanEngine.composeReplan(
                fresh = fresh,
                previous = previousTrace!!,
                previousPlanVersionId = previous!!.planVersionId,
                trigger = replan!!.trigger,
                preservedPositions = replan.keptPositions.sorted(),
                dayHardMinutes = if (replan.trigger == ReplanTrigger.TODAY_CAPACITY_CHANGED) {
                    PlannerEngine.resolveCapacity(replan.capacity).hardBudgetMinutes
                } else {
                    kept.sumOf { it.plannedMinutes } + budget.hardBudgetMinutes
                },
            )
        }

        val planVersionId = persistence.inTransaction {
            val planId = persistence.appendTruth(
                TruthRecord("plan_version", now, mapOf("policy_version" to PlannerEngine.PLANNER_MODEL))
            )
            trace.selected.forEach { entry ->
                persistence.appendTruth(
                    TruthRecord(
                        "planned_task", now,
                        mapOf(
                            "plan_version_id" to planId.toString(),
                            "skill_logical_id" to entry.primarySkill.logicalId,
                            "skill_version" to entry.primarySkill.version.toString(),
                            "position" to entry.position.toString(),
                        ),
                    )
                )
            }
            persistence.appendTruth(
                TruthRecord(
                    "planner_decision_trace", now,
                    mapOf("plan_version_id" to planId.toString(), "trace" to PlanTraceCodec.encode(trace)),
                )
            )
            planId
        }
        return Built.Planned(planVersionId, trace)
    }
}
