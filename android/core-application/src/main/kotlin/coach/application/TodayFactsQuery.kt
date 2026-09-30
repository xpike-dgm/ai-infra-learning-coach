package coach.application

import coach.model.NeedDisposition
import coach.model.PlanSnapshot
import coach.model.TodayFacts
import coach.ports.ClockPort
import coach.ports.PersistencePort

/**
 * Reads what the store and the clock can truthfully say about today (`THUX-v0`, 11A).
 *
 * It is a read-only use case: Today never writes, and the transaction boundary that does belongs to
 * the engines (`MSBX-v0` §transaction_boundary).
 *
 * **The plan comes only from what the planner stored (12E).** 11A read no plan because `planned_task`
 * carries no purpose, title, minutes or reason; 12C put all of that in the plan's trace, so Today now
 * reads the newest plan and its trace together, and only when the trace describes the stored rows
 * ([PlanReading]). A plan from another study day is passed on with its own day so the projection can
 * refuse it; an unreadable plan for today is said to be unreadable, never filled in.
 *
 * Capacity is the one the planner resolved and recorded for today's plan — the learner's own setting
 * as the planner read it. With no plan for today there is no capacity to report, because a default
 * would be a number the learner never chose (16D).
 */
class TodayFactsQuery(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun load(): TodayFacts {
        val studyDay = clock.now().studyDay
        val curriculumLoaded = persistence.curriculumPublished()
        val stored = persistence.latestPlan() ?: return TodayFacts(studyDay, curriculumLoaded = curriculumLoaded)
        return when (val read = PlanReading.read(stored, studyDay)) {
            // Only the day is passed on: the projection's rule is that such a plan never becomes today's.
            is PlanReading.Read.FromAnotherDay -> TodayFacts(
                studyDay = studyDay,
                plan = PlanSnapshot(stored.planVersionId, policyVersion = "", studyDay = read.planStudyDay, tasks = emptyList()),
                curriculumLoaded = curriculumLoaded,
            )
            is PlanReading.Read.Unreadable -> TodayFacts(studyDay, curriculumLoaded = curriculumLoaded, planUnreadable = true)
            is PlanReading.Read.Today -> TodayFacts(
                studyDay = studyDay,
                plan = PlanReading.snapshot(read),
                capacity = PlanReading.capacity(read.trace),
                curriculumLoaded = curriculumLoaded,
                planReplaced = read.trace.replan != null,
                prerequisiteWaiting = read.trace.needs.any { it.disposition == NeedDisposition.BLOCKED },
            )
        }
    }
}
