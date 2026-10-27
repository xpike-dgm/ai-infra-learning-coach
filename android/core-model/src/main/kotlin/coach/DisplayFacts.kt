package coach.model

/**
 * How the app looks and moves (17E, `D-138`): the learner's theme and whether the app's own motion is turned off.
 *
 * Stored like every preference — appended, never edited — so a backup carries it (16D). With no row the theme follows the
 * system and motion follows Android's own setting. Turning motion off here only ever reduces motion: when Android has
 * removed animations, the app's motion stays off whatever is recorded here.
 */
enum class ThemeChoice(val id: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark"),
    ;

    companion object {
        fun of(id: String): ThemeChoice? = entries.firstOrNull { it.id == id }
    }
}

data class DisplayPreferenceRecord(val theme: ThemeChoice, val reduceMotion: Boolean, val sequence: Long)

data class DisplayPreference(
    val theme: ThemeChoice,
    val reduceMotion: Boolean,
    /** Whether the learner has ever recorded a setting; until then both follow the system. */
    val recorded: Boolean = false,
) {
    /** Whether the app is dark, given whether the system is. */
    fun dark(systemDark: Boolean): Boolean = when (theme) {
        ThemeChoice.SYSTEM -> systemDark
        ThemeChoice.LIGHT -> false
        ThemeChoice.DARK -> true
    }

    companion object {
        val SYSTEM_DEFAULT = DisplayPreference(ThemeChoice.SYSTEM, reduceMotion = false)

        /** The newest stored row is the setting; with none, everything follows the system. */
        fun of(records: List<DisplayPreferenceRecord>): DisplayPreference =
            records.maxByOrNull { it.sequence }?.let { DisplayPreference(it.theme, it.reduceMotion, recorded = true) } ?: SYSTEM_DEFAULT
    }
}
