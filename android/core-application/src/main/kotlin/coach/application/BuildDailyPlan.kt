package coach.application

import coach.engines.PlannerEngine
import coach.model.DailyCapacityInput
import coach.model.MasteryAxisState
import coach.model.PlanTrace
import coach.model.PlanTraceCodec
import coach.model.PrerequisiteCandidate
import coach.model.RetentionAxis
import coach.model.SkillPlanningState
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * Producing today's plan (12C) — an `initial` generation in `PDT-v0`'s terms.
 *
 * The plan is **truth**: `plan_version`, its `planned_task` rows and its decision trace are appended in
 * one transaction and never edited, so a later plan is a new version and yesterday's plan still says
 * exactly what it said. Replanning after an event is 12D's; this only plans from current state.
 *
 * `planned_task` carries only the Skill and the position. Everything else a reader needs — purpose,
 * title, activity, minutes, and why — is in the trace, keyed by position, because `DDM-v0` names no
 * column for it and 10D forbade inventing one.
 */
class BuildDailyPlan(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
) {
    sealed interface Built {
        /** No curriculum is published, so there is nothing to plan against and nothing is written. */
        data object NothingPublished : Built

        data class Planned(val planVersionId: Long, val trace: PlanTrace) : Built
    }

    fun build(capacity: DailyCapacityInput): Built {
        // Read first, like every projection: the trace can then never claim to have seen more truth
        // than the state it was planned from.
        val watermark = persistence.truthWatermark()
        val curriculumVersion = persistence.latestCurriculumVersion() ?: return Built.NothingPublished
        val now = clock.now()

        val skills = persistence.publishedSkills()
        val states = skills.map { skill ->
            val row = persistence.readProjection(ResolvePrerequisites.skillStateKey(skill.ref))
            val axes = row?.payload.orEmpty()
            SkillPlanningState(
                skill = skill.ref,
                lifecycleStatus = skill.lifecycleStatus,
                critical = skill.criticalPrerequisite,
                mastery = MasteryAxisState.entries.firstOrNull { it.id == axes["mastery_axis_state"] },
                retention = RetentionAxis.of(axes["retention_axis_state"]),
                weaknessAxis = axes["weakness_axis_state"]?.takeUnless { it.isEmpty() || it == ResolvePrerequisites.NOT_YET_EVALUATED },
                snapshotRef = row?.let { "${it.key}#watermark=${it.truthWatermark}" },
            )
        }
        val needs = PlannerEngine.needsFromSkillStates(states)
        val candidates = needs.flatMap { content.taskCandidates(it) }

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

        val trace = PlannerEngine.plan(
            capacity = PlannerEngine.resolveCapacity(capacity),
            needs = needs,
            candidates = candidates,
            decisions = decisions,
            studyDay = now.studyDay,
            curriculumVersion = curriculumVersion,
            truthWatermark = watermark,
            skillsNotOnRoute = skills.count { !PlannerEngine.onRoute(it.lifecycleStatus) },
        )

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
