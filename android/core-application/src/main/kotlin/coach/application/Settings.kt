package coach.application

import coach.engines.PlannerEngine
import coach.model.BlueprintScopes
import coach.model.CapacityPreferences
import coach.model.CapacityProfile
import coach.model.PlanTraceCodec
import coach.model.ReplanTrigger
import coach.model.TodayCapacityChoice
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * The capacity preferences as use cases (16D, `STGX-v0`; D-033, `THUX-v0` capacity context).
 *
 * Reading is a fold over the stored rows ([CapacityPreferences.of]); writing appends one row, in one transaction. A
 * preference is the learner's decision and nothing else: it moves what fits a day, never what anyone has learned.
 */
class CapacitySettings(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun read(): CapacityPreferences = CapacityPreferences.of(persistence.preferenceRecords(), clock.now().studyDay)

    /** One profile's standing length. Out-of-range minutes are refused here, before anything is written. */
    fun setProfile(profile: CapacityProfile, minutes: Int) {
        require(minutes in CapacityPreferences.MINUTES_RANGE) { "a profile length is 0 to ${CapacityPreferences.MAX_MINUTES} minutes" }
        append(CapacityPreferences.keyOf(profile), minutes.toString(), appliesToStudyDay = null)
    }

    /** Today's choice, for today's study day only; tomorrow it no longer applies. */
    fun setToday(choice: TodayCapacityChoice) {
        append(CapacityPreferences.TODAY_KEY, CapacityPreferences.encode(choice), appliesToStudyDay = clock.now().studyDay)
    }

    private fun append(key: String, value: String, appliesToStudyDay: String?) {
        val at = clock.now()
        persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(
                    PREFERENCE_KIND, at,
                    buildMap {
                        put("preference_name", key)
                        put("preference_value", value)
                        appliesToStudyDay?.let { put("applies_to_day", it) }
                    },
                )
            )
        }
    }

    companion object {
        const val PREFERENCE_KIND = "preference_record"
    }
}

/**
 * Today's plan, made by the app (16D, `STGX-v0`): the step that turns a published course and the learner's time into
 * the queue Today shows.
 *
 * It decides nothing itself. This week's and this month's assessment blueprints are composed once per cycle by their
 * own use case (13A, 13B), so the planner can offer their slots; then the planner builds or keeps today's plan from the
 * capacity preferences (12C, 12D). A capacity change that changes today's budget is a replan with D-033 §16's
 * `today_capacity_changed`; one that leaves the budget as it was writes no plan at all.
 */
class PlanToday(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
    private val evaluatorAvailable: Boolean = false,
) {
    /** Makes sure today has a plan: composes the cycles, then builds — `AlreadyPlanned` when today's plan stands. */
    fun ensure(): BuildDailyPlan.Built {
        compose()
        return builder().build(CapacitySettings(persistence, clock).read().input())
    }

    /**
     * After a capacity preference changed: today's plan is replanned only if today's hard budget is now different.
     * Nothing the learner started is kept, because nothing can be started from Today yet (16F); the replan therefore
     * reports no kept positions rather than inventing any.
     */
    fun afterCapacityChange(): BuildDailyPlan.Built {
        compose()
        val input = CapacitySettings(persistence, clock).read().input()
        val built = builder().build(input)
        if (built !is BuildDailyPlan.Built.AlreadyPlanned) return built
        val trace = persistence.latestPlan()?.traceText?.let(PlanTraceCodec::decode) ?: return built
        if (trace.capacity.hardBudgetMinutes == PlannerEngine.resolveCapacity(input).hardBudgetMinutes) return built
        return builder().replan(BuildDailyPlan.ReplanRequest(ReplanTrigger.TODAY_CAPACITY_CHANGED, input))
    }

    private fun builder() = BuildDailyPlan(persistence, content, clock, evaluatorAvailable)

    /** Once per cycle; a cycle with nothing worth measuring writes nothing and leaves no debt (13A, 13B). */
    private fun compose() {
        if (persistence.latestCurriculumVersion() == null) return
        BlueprintScopes.COMPOSED.forEach { scope ->
            ComposeAssessmentBlueprint(scope, persistence, content, clock).compose(evaluatorAvailable)
        }
    }
}
