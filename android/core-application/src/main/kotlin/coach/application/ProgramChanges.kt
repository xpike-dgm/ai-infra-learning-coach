package coach.application

import coach.engines.ProgramChangeEngine
import coach.model.DailyCapacityInput
import coach.model.ObjectiveGateProfiles
import coach.model.PlanTraceCodec
import coach.model.ProgramChangeReport
import coach.model.ProgramSnapshot
import coach.model.ReplanTrigger
import coach.model.SkillAxes
import coach.model.StateChange
import coach.model.StateChangeKind
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort

/**
 * Reading what canonical state and the plan say right now (13E), for a later diff.
 *
 * The watermark is read first, like every projection: the snapshot can never claim to have seen more
 * truth than it did. Axes are copied as stored; an axis no engine has written reads `not_yet_evaluated`.
 */
class CaptureProgramSnapshot(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun capture(): ProgramSnapshot {
        val watermark = persistence.truthWatermark()
        val studyDay = clock.now().studyDay
        val skills = persistence.publishedSkills().associate { skill ->
            val axes = persistence.readProjection(ResolvePrerequisites.skillStateKey(skill.ref))?.payload.orEmpty()
            fun axis(column: String) = axes[column]?.ifEmpty { null } ?: ResolvePrerequisites.NOT_YET_EVALUATED
            skill.ref to SkillAxes(
                skill = skill.ref,
                mastery = axis("mastery_axis_state"),
                retention = axis("retention_axis_state"),
                weakness = axis("weakness_axis_state"),
            )
        }
        val plan = persistence.latestPlan()
        return ProgramSnapshot(
            truthWatermark = watermark,
            studyDay = studyDay,
            skills = skills,
            planVersionId = plan?.planVersionId,
            plan = plan?.traceText?.let(PlanTraceCodec::decode),
        )
    }
}

/**
 * Recomputing one Skill's state after new evidence (13E): mastery, then retention and weakness (which read
 * the mastery timeline), then readiness (which reads the axes they wrote). Each engine writes only its own
 * family; this only puts them in order, with the Objectives' gate profiles taken from the published
 * curriculum. A Skill with no published Objective has nothing to recompute and nothing is written.
 */
class RecomputeSkillState(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun recompute(skill: VersionedRef): Boolean {
        val profiles = persistence.objectivesOf(skill).map(ObjectiveGateProfiles::of)
        if (profiles.isEmpty()) return false
        RebuildMastery(persistence, clock).rebuild(skill, profiles)
        RebuildRetention(persistence, clock).rebuild(skill, profiles)
        RebuildWeakness(persistence, clock).rebuild(skill, profiles)
        RebuildReadiness(persistence, clock).rebuild(skill)
        return true
    }
}

/**
 * What an assessment (or any evidence) changed, and the plan it changed (13E) — `ASUX-v0` §13.4, `WBA-v0` /
 * `MCA-v0` §30 and V1's "a weekly or monthly assessment changes the next plan".
 *
 * [before] is captured by the caller before the evidence was recorded. The touched Skills are recomputed,
 * and only if canonical state actually changed is a new plan asked for — through the planner's own replan,
 * with the event the change names. Nothing changed means no new plan and a report that says so.
 */
class ReportProgramChanges(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
) {
    data class Reported(val report: ProgramChangeReport, val plan: BuildDailyPlan.Built?)

    fun report(before: ProgramSnapshot, touched: List<VersionedRef>, capacity: DailyCapacityInput): Reported {
        val recompute = RecomputeSkillState(persistence, clock)
        touched.distinct().forEach { recompute.recompute(it) }
        val capture = CaptureProgramSnapshot(persistence, clock)
        val changed = capture.capture()
        val states = ProgramChangeEngine.report(before, changed).stateChanges
        val trigger = triggerFor(states)
        val built = trigger?.let {
            BuildDailyPlan(persistence, content, clock).replan(BuildDailyPlan.ReplanRequest(trigger = it, capacity = capacity))
        }
        return Reported(ProgramChangeEngine.report(before, capture.capture()), built)
    }

    internal companion object {
        /**
         * The event a replan records for what changed (`PDT-v0` §8.10). Every change keeps the day's budget
         * (none of these sets remaining time); the report itself lists every change's own reason, so the one
         * named here only labels the new plan version.
         */
        fun triggerFor(changes: List<StateChange>): ReplanTrigger? {
            val kinds = changes.map { it.kind }.toSet()
            return when {
                kinds.isEmpty() -> null
                StateChangeKind.REMEDIATION_OPENED in kinds -> ReplanTrigger.NEW_REMEDIATION_CREATED
                StateChangeKind.VERIFICATION_OPENED in kinds -> ReplanTrigger.NEW_VERIFICATION_DUE_CREATED
                else -> ReplanTrigger.NEW_EVIDENCE_RECORDED
            }
        }
    }
}
