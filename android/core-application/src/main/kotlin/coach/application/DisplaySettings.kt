package coach.application

import coach.model.DisplayPreference
import coach.model.ThemeChoice
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * The theme and motion setting as a use case (17E, `D-138`). Reading is a fold over the stored rows
 * ([DisplayPreference.of]); writing appends one row holding the whole setting, in one transaction. It changes how the
 * app looks and moves, never what anyone has learned, and it replans nothing.
 */
class DisplaySettings(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun read(): DisplayPreference = DisplayPreference.of(persistence.displayPreferences())

    fun set(theme: ThemeChoice, reduceMotion: Boolean) {
        val at = clock.now()
        persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(KIND, at, mapOf("theme" to theme.id, "reduce_motion" to if (reduceMotion) "1" else "0"))
            )
        }
    }

    companion object {
        const val KIND = "display_preference"
    }
}
