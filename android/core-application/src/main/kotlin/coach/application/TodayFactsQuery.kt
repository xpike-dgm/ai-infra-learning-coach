package coach.application

import coach.model.TodayFacts
import coach.ports.ClockPort
import coach.ports.PersistencePort

/**
 * Reads what the store and the clock can truthfully say about today (`THUX-v0`, 11A).
 *
 * It is a read-only use case: Today never writes, and the transaction boundary that does belongs to
 * the engines (`MSBX-v0` §transaction_boundary).
 *
 * **It does not read a plan, and that is the honest position rather than an omission.** A Today row
 * needs the task's purpose, its trace-backed reason and its estimated duration; `DDM-v0`'s
 * `planned_task` carries none of those columns yet, and 10D recorded them as 12's to complete along
 * with the planner that writes them. Reading the rows that do exist and filling the rest with
 * plausible defaults is precisely how a screen starts claiming things no engine decided, so the plan
 * stays absent here and the projection renders the truthful empty and loading states instead.
 */
class TodayFactsQuery(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun load(): TodayFacts = TodayFacts(
        studyDay = clock.now().studyDay,
        // The planner is 12. Until it writes plans whose rows can be complete, there is no plan.
        plan = null,
        // Capacity is a Profile setting (D-033, owner 16D); nothing stores one yet, and a default
        // would be a number the learner never chose.
        capacity = null,
        curriculumLoaded = persistence.curriculumPublished(),
    )
}
