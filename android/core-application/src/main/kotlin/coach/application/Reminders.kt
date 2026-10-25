package coach.application

import coach.engines.BlueprintComposer
import coach.model.AssessmentScope
import coach.model.PlanTrace
import coach.model.ReminderDecision
import coach.model.ReminderPlanContent
import coach.model.ReminderPreference
import coach.model.ReminderSilence
import coach.model.TaskPurpose
import coach.ports.ClockPort
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * The reminder setting as a use case (16E, `RMDX-v0`). Reading is a fold over the stored rows
 * ([ReminderPreference.of]); writing appends one row holding the whole setting, in one transaction. It moves when a
 * note may arrive, never what anyone has learned.
 */
class ReminderSettings(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun read(): ReminderPreference = ReminderPreference.of(persistence.reminderPreferences())

    /** Records the learner's setting. A minute outside the day is refused here, before anything is written. */
    fun set(enabled: Boolean, minuteOfDay: Int) {
        require(minuteOfDay in ReminderPreference.MINUTE_RANGE) { "a time of day is 0 to 1439 minutes after midnight" }
        val at = clock.now()
        persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(
                    KIND, at,
                    mapOf("reminder_enabled" to if (enabled) "1" else "0", "minute_of_day" to minuteOfDay.toString()),
                )
            )
        }
    }

    companion object {
        const val KIND = "reminder_preference"
    }
}

/**
 * Whether today's reminder may be shown now, and what it may say (16E, `RMDX-v0`; V1 SC-043).
 *
 * It **writes no truth.** A reminder speaks of today's plan: the stored one when the learner has already opened the app
 * today, otherwise the very plan the planner would write if asked now ([BuildDailyPlan.preview]) — composed, never
 * written, so a background wake never puts a plan on a day the learner did not open.
 *
 * Everything that could make a reminder stale or unfounded is silence: the learner turned it off, Android blocks it,
 * it arrived on another date, today's was already shown, nothing is published, the plan cannot be read, nothing new is
 * planned, work is already recorded today, or the day cannot be read. Silence is never a message about the learner.
 */
class ReminderCheck(
    private val persistence: PersistencePort,
    private val content: ContentPort,
    private val clock: ClockPort,
    private val evaluatorAvailable: Boolean = false,
) {
    /**
     * [scheduledStudyDay] is the local date the reminder was scheduled for; [lastShownStudyDay] the study day of the
     * last reminder shown; [notificationsAllowed] what Android says about this app's notifications.
     */
    fun decide(scheduledStudyDay: String, lastShownStudyDay: String?, notificationsAllowed: Boolean): ReminderDecision {
        val today = clock.now().studyDay
        if (!ReminderSettings(persistence, clock).read().enabled) return silent(ReminderSilence.DISABLED)
        if (!notificationsAllowed) return silent(ReminderSilence.NOTIFICATIONS_BLOCKED)
        if (scheduledStudyDay != today) return silent(ReminderSilence.DELIVERED_ON_ANOTHER_DAY)
        if (lastShownStudyDay == today) return silent(ReminderSilence.ALREADY_SHOWN_TODAY)
        if (persistence.latestCurriculumVersion() == null) return silent(ReminderSilence.NO_CURRICULUM)

        // Work recorded today means the learner has been here today; a reminder would only repeat what they know. An
        // attempt is the record of work (11B); a count that cannot be read is not a zero (11E).
        val attempts = runCatching { persistence.countTruth(ATTEMPT, today) }.getOrNull()
            ?: return silent(ReminderSilence.DAY_UNREADABLE)
        if (attempts > 0) return silent(ReminderSilence.WORK_RECORDED_TODAY)

        val trace = when (val plan = todaysPlan(today)) {
            is TodaysPlan.Unreadable -> return silent(ReminderSilence.PLAN_UNREADABLE)
            is TodaysPlan.Trace -> plan.trace
            TodaysPlan.None -> return silent(ReminderSilence.NOTHING_PLANNED)
        }
        return contentOf(trace)?.let { ReminderDecision.Show(today, it) } ?: silent(ReminderSilence.NOTHING_PLANNED)
    }

    private sealed interface TodaysPlan {
        data class Trace(val trace: PlanTrace) : TodaysPlan
        data object Unreadable : TodaysPlan
        data object None : TodaysPlan
    }

    /** Today's stored plan when there is one, read only when its trace describes its rows (12E); else the preview. */
    private fun todaysPlan(today: String): TodaysPlan {
        val stored = persistence.latestPlan()
        if (stored != null && stored.recordedAt.studyDay == today) {
            return when (val read = PlanReading.read(stored, today)) {
                is PlanReading.Read.Today -> TodaysPlan.Trace(read.trace)
                else -> TodaysPlan.Unreadable
            }
        }
        val capacity = CapacitySettings(persistence, clock).read().input()
        return BuildDailyPlan(persistence, content, clock, evaluatorAvailable).preview(capacity)
            ?.let { TodaysPlan.Trace(it) } ?: TodaysPlan.None
    }

    companion object {
        private const val ATTEMPT = "attempt"

        private fun silent(reason: ReminderSilence) = ReminderDecision.Silent(reason)

        /**
         * What a reminder may name, all of it from the trace: the tasks the planner selected today — never one kept
         * because it was already started — their planned minutes, whether one is a review, and whether a weekly or
         * monthly slot is among them. `null` when nothing new is planned.
         */
        fun contentOf(trace: PlanTrace): ReminderPlanContent? {
            val fresh = trace.selected.filterNot { it.preserved }
            if (fresh.isEmpty()) return null
            val scopes = fresh.mapNotNull { BlueprintComposer.scopeOfCandidate(it.candidateId) }.toSet()
            return ReminderPlanContent(
                taskCount = fresh.size,
                plannedMinutes = fresh.sumOf { it.plannedMinutes },
                includesReview = fresh.any { it.purpose == TaskPurpose.RETAIN },
                includesWeeklyAssessment = AssessmentScope.WEEKLY_BLUEPRINT in scopes,
                includesMonthlyAssessment = AssessmentScope.MONTHLY_CAPABILITY in scopes,
            )
        }
    }
}
