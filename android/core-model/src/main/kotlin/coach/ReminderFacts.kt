package coach.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * The daily reminder (16E, `RMDX-v0`): one quiet note at a time the learner chose, saying what today's plan holds.
 *
 * A reminder is never a measure and never an obligation. It does not count days away, keep a series, raise its voice
 * after an absence or say what the learner did not do (`SRR-v0`, `THUX-v0`, 11E). It is off until the learner turns it
 * on, and like every preference the decision is appended to the store and never edited (16D), so a backup carries it.
 */

/** One stored reminder row, as the store wrote it: the whole setting, both values together. */
data class ReminderPreferenceRecord(
    val enabled: Boolean,
    val minuteOfDay: Int,
    val sequence: Long,
)

/**
 * The reminder setting as it stands: on or off, and the local time of day. With no row it is off (user decision,
 * 2026-10-25), and the time shown for turning it on is [SUGGESTED_MINUTE] — a product suggestion the learner changes,
 * never a recommendation about when to learn.
 */
data class ReminderPreference(
    val enabled: Boolean,
    val minuteOfDay: Int,
    /** Whether the learner has ever recorded a setting; until then the time is only the suggestion. */
    val recorded: Boolean = false,
) {
    init {
        require(minuteOfDay in MINUTE_RANGE) { "a time of day is 0 to ${MINUTES_PER_DAY - 1} minutes after midnight" }
    }

    companion object {
        const val MINUTES_PER_DAY = 1440
        val MINUTE_RANGE = 0 until MINUTES_PER_DAY

        /** 19:00: where the time picker starts; nothing is scheduled until the learner turns the reminder on. */
        const val SUGGESTED_MINUTE = 19 * 60

        val OFF = ReminderPreference(enabled = false, minuteOfDay = SUGGESTED_MINUTE)

        /** The newest readable row is the setting; a row out of range is skipped, never guessed. With none, [OFF]. */
        fun of(records: List<ReminderPreferenceRecord>): ReminderPreference =
            records.sortedBy { it.sequence }.lastOrNull { it.minuteOfDay in MINUTE_RANGE }
                ?.let { ReminderPreference(it.enabled, it.minuteOfDay, recorded = true) }
                ?: OFF
    }
}

/** A local time of day written as `SS:DD`, strictly: hours 0–23, minutes 00–59. */
object ReminderTime {
    private val PATTERN = Regex("""^([01]?\d|2[0-3]):([0-5]\d)$""")

    /** The minute of the day [text] names, or `null` when it is not a time of day. */
    fun parse(text: String): Int? = PATTERN.matchEntire(text.trim())?.let { m ->
        m.groupValues[1].toInt() * 60 + m.groupValues[2].toInt()
    }

    fun format(minuteOfDay: Int): String {
        require(minuteOfDay in ReminderPreference.MINUTE_RANGE) { "not a minute of the day: $minuteOfDay" }
        return "%02d:%02d".format(minuteOfDay / 60, minuteOfDay % 60)
    }
}

/**
 * When the next reminder is due. Each day's reminder is its own alarm for its own local date, so a reminder is always
 * about the day it is shown on: a late delivery that lands on another date is not shown ([ReminderSchedule.sameDay]).
 */
object ReminderSchedule {
    /**
     * The first moment strictly after [now] at which the local clock in [zone] reads [minuteOfDay]. A time that a DST
     * change skips is moved forward by the gap, as `java.time` resolves it; a time that occurs twice is the first.
     */
    fun nextTrigger(now: Instant, zone: ZoneId, minuteOfDay: Int): Instant {
        require(minuteOfDay in ReminderPreference.MINUTE_RANGE) { "not a minute of the day: $minuteOfDay" }
        val time = LocalTime.of(minuteOfDay / 60, minuteOfDay % 60)
        val today = now.atZone(zone).toLocalDate()
        val candidate = ZonedDateTime.of(today, time, zone).toInstant()
        return if (candidate.isAfter(now)) candidate else ZonedDateTime.of(today.plusDays(1), time, zone).toInstant()
    }

    /** The local date in [zone] a reminder scheduled for [scheduled] belongs to. */
    fun dateOf(scheduled: Instant, zone: ZoneId): LocalDate = scheduled.atZone(zone).toLocalDate()

    /** Whether a reminder scheduled for [scheduled] is being delivered on its own local date. */
    fun sameDay(scheduled: Instant, now: Instant, zone: ZoneId): Boolean = dateOf(scheduled, zone) == dateOf(now, zone)
}

/**
 * What a reminder may say, read from today's plan at the moment it is due: only what the planner itself selected for
 * today, inside today's time. A review or an assessment that did not fit today is not mentioned — not fitting is not a
 * debt (D-033, `SRR-v0`) — and a weekly or monthly assessment is named only when a slot of its cycle is in the plan.
 */
data class ReminderPlanContent(
    /** New tasks in today's plan: those the planner selected today, never one kept because it was already started. */
    val taskCount: Int,
    /** The plan's own estimate of those tasks' minutes, a fact the trace recorded — never a target. */
    val plannedMinutes: Int,
    val includesReview: Boolean,
    val includesWeeklyAssessment: Boolean,
    val includesMonthlyAssessment: Boolean,
) {
    init {
        require(taskCount > 0) { "a reminder names at least one task" }
        require(plannedMinutes >= 0) { "minutes cannot be negative" }
    }
}

/** Why no reminder is shown. Each is silence, never a message about the learner. */
enum class ReminderSilence(val id: String) {
    /** The learner has not turned it on, or turned it off. */
    DISABLED("disabled"),

    /** Today's reminder was already shown: at most one per study day. */
    ALREADY_SHOWN_TODAY("already_shown_today"),

    /** It was scheduled for another date and arrived late; a reminder is only ever about its own day. */
    DELIVERED_ON_ANOTHER_DAY("delivered_on_another_day"),

    /** The store could not be read in time; nothing is guessed in its place. */
    STORE_NOT_READY("store_not_ready"),

    /** No curriculum is published, so there is no plan to speak of. */
    NO_CURRICULUM("no_curriculum"),

    /** Today's plan exists but cannot be read; nothing is said about it. */
    PLAN_UNREADABLE("plan_unreadable"),

    /** The planner has nothing to select today (`empty_valid`), or nothing it selected is new. */
    NOTHING_PLANNED("nothing_planned"),

    /** Work is already recorded today, so a reminder could only repeat what the learner already knows. */
    WORK_RECORDED_TODAY("work_recorded_today"),

    /** Whether work was recorded today could not be read, and a reminder might then be stale. */
    DAY_UNREADABLE("day_unreadable"),

    /** Android will not show notifications for this app; the system decides that, not this product. */
    NOTIFICATIONS_BLOCKED("notifications_blocked"),
}

/** The reminder's decision for one moment: silence, with its reason, or what today's plan holds. */
sealed interface ReminderDecision {
    data class Silent(val reason: ReminderSilence) : ReminderDecision
    data class Show(val studyDay: String, val content: ReminderPlanContent) : ReminderDecision
}
