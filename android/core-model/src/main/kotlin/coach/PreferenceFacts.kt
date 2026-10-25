package coach.model

/**
 * The learner's own preferences (16D, `STGX-v0`): how this personal app should operate for them (`UXIA-v0` Profile).
 *
 * A preference is the learner's decision, recorded as they made it. It is never a learning measure: changing today's
 * time changes what fits today, never what anyone has learned (D-033). Like every user truth it is appended, never
 * edited — the newest row of a key is the preference, and the older rows are what was chosen before.
 */

/** One stored preference row, as the store wrote it. [appliesToStudyDay] is set only for today's own choice. */
data class PreferenceRecord(
    val key: String,
    val value: String,
    val appliesToStudyDay: String?,
    val sequence: Long,
)

/** What the learner said about today's time: nothing, one of the three profiles, or a number of minutes. */
sealed interface TodayCapacityChoice {
    /** No choice for today: the standing normal profile applies (D-033 §2's last step). */
    data object None : TodayCapacityChoice

    data class Profile(val profile: CapacityProfile) : TodayCapacityChoice

    data class Minutes(val minutes: Int) : TodayCapacityChoice {
        init {
            require(minutes in CapacityPreferences.MINUTES_RANGE) { "minutes for today are 0 to ${CapacityPreferences.MAX_MINUTES}" }
        }
    }
}

/**
 * The capacity preferences (D-033 §2–3, `ADAPTIVE_PLANNER_SPEC` §3) as they stand on one study day.
 *
 * The three profile lengths start at `ADAPTIVE_PLANNER_SPEC` §3's accepted v0 presets — short 30, normal 60,
 * intensive 90 — and the learner may change each. A choice for today applies to today only: tomorrow it is gone,
 * because yesterday's plan for the day is not today's (`SRR-v0`), and nothing carries over as a debt.
 */
data class CapacityPreferences(
    val shortMinutes: Int,
    val normalMinutes: Int,
    val intensiveMinutes: Int,
    val today: TodayCapacityChoice,
    /** Which of the three lengths the learner set themselves; the others are the presets. */
    val customized: Set<CapacityProfile> = emptySet(),
) {
    init {
        listOf(shortMinutes, normalMinutes, intensiveMinutes).forEach {
            require(it in MINUTES_RANGE) { "a profile length is 0 to $MAX_MINUTES minutes" }
        }
    }

    fun minutesOf(profile: CapacityProfile): Int = when (profile) {
        CapacityProfile.SHORT -> shortMinutes
        CapacityProfile.NORMAL -> normalMinutes
        CapacityProfile.INTENSIVE -> intensiveMinutes
    }

    /** The planner's input (D-033 §2): today's minutes first, then today's profile, then the normal profile. */
    fun input(): DailyCapacityInput = DailyCapacityInput(
        normalProfileMinutes = normalMinutes,
        shortProfileMinutes = shortMinutes,
        intensiveProfileMinutes = intensiveMinutes,
        selectedProfile = (today as? TodayCapacityChoice.Profile)?.profile,
        todayOverrideMinutes = (today as? TodayCapacityChoice.Minutes)?.minutes,
    )

    companion object {
        /** `ADAPTIVE_PLANNER_SPEC` §3's v0 presets: product defaults the learner changes, not scientific values. */
        const val SHORT_PRESET_V0 = 30
        const val NORMAL_PRESET_V0 = 60
        const val INTENSIVE_PRESET_V0 = 90

        /** A day has 1440 minutes; a time budget is never more than the day it is for. */
        const val MAX_MINUTES = 1440
        val MINUTES_RANGE = 0..MAX_MINUTES

        const val SHORT_KEY = "capacity.short_minutes"
        const val NORMAL_KEY = "capacity.normal_minutes"
        const val INTENSIVE_KEY = "capacity.intensive_minutes"
        const val TODAY_KEY = "capacity.today"
        const val TODAY_NONE = "none"

        fun keyOf(profile: CapacityProfile): String = when (profile) {
            CapacityProfile.SHORT -> SHORT_KEY
            CapacityProfile.NORMAL -> NORMAL_KEY
            CapacityProfile.INTENSIVE -> INTENSIVE_KEY
        }

        /** How a today's choice is stored: `none`, a profile's id, or a whole number of minutes. */
        fun encode(choice: TodayCapacityChoice): String = when (choice) {
            TodayCapacityChoice.None -> TODAY_NONE
            is TodayCapacityChoice.Profile -> choice.profile.id
            is TodayCapacityChoice.Minutes -> choice.minutes.toString()
        }

        /** The opposite of [encode], strict: a value that is none of these is not a choice anyone made. */
        fun decodeToday(value: String): TodayCapacityChoice? {
            if (value == TODAY_NONE) return TodayCapacityChoice.None
            CapacityProfile.entries.firstOrNull { it.id == value }?.let { return TodayCapacityChoice.Profile(it) }
            return minutesOf(value)?.let(TodayCapacityChoice::Minutes)
        }

        /** A whole number of minutes in range, written as digits only; anything else is not read. */
        fun minutesOf(value: String): Int? =
            value.takeIf { it.isNotEmpty() && it.length <= 4 && it.all(Char::isDigit) }?.toInt()?.takeIf { it in MINUTES_RANGE }

        /**
         * The preferences as they stand on [studyDay]: the newest readable row of each standing key, and the newest
         * readable choice recorded **for** that day. A row that cannot be read is skipped, never guessed; with no row,
         * the preset holds.
         */
        fun of(records: List<PreferenceRecord>, studyDay: String): CapacityPreferences {
            val ordered = records.sortedBy { it.sequence }
            fun standing(key: String): Int? = ordered.lastOrNull { it.key == key && it.appliesToStudyDay == null && minutesOf(it.value) != null }
                ?.let { minutesOf(it.value) }
            val today = ordered.lastOrNull { it.key == TODAY_KEY && it.appliesToStudyDay == studyDay && decodeToday(it.value) != null }
                ?.let { decodeToday(it.value) } ?: TodayCapacityChoice.None
            val set = CapacityProfile.entries.associateWith { standing(keyOf(it)) }
            return CapacityPreferences(
                shortMinutes = set[CapacityProfile.SHORT] ?: SHORT_PRESET_V0,
                normalMinutes = set[CapacityProfile.NORMAL] ?: NORMAL_PRESET_V0,
                intensiveMinutes = set[CapacityProfile.INTENSIVE] ?: INTENSIVE_PRESET_V0,
                today = today,
                customized = set.filterValues { it != null }.keys,
            )
        }
    }
}
