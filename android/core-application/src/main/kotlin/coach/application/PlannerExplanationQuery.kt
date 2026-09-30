package coach.application

import coach.model.PlannerExplanationFacts
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.PersistencePort

/**
 * Reads what the `planner_explanation` surface may explain (12E): today's stored plan trace, checked
 * against the plan's own rows exactly as Today checks it, and the published names of the Skills it
 * mentions.
 *
 * It is read-only and bounded by the trace itself (`PDT-v0` §20): no history is scanned and nothing is
 * recomputed. Re-running the prerequisite gate here would explain today's plan with a decision the
 * planner never made, because state may have moved since; the trace is the decision.
 */
class PlannerExplanationQuery(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun load(): PlannerExplanationFacts {
        val stored = persistence.latestPlan() ?: return PlannerExplanationFacts.NoPlan
        return when (val read = PlanReading.read(stored, clock.now().studyDay)) {
            is PlanReading.Read.FromAnotherDay -> PlannerExplanationFacts.PlanFromAnotherDay(read.planStudyDay)
            is PlanReading.Read.Unreadable -> PlannerExplanationFacts.Unreadable(read.reason)
            is PlanReading.Read.Today -> {
                val trace = read.trace
                val mentioned: Set<VersionedRef> = buildSet {
                    trace.selected.forEach { add(it.primarySkill) }
                    trace.needs.forEach { addAll(it.targetSkills) }
                    trace.candidates.forEach { addAll(it.relatedSkills) }
                }
                val names = mentioned.sortedBy { it.toString() }
                    .mapNotNull { ref -> persistence.skill(ref)?.let { ref to it.canonicalName } }
                    .toMap()
                PlannerExplanationFacts.Readable(trace, names)
            }
        }
    }
}
