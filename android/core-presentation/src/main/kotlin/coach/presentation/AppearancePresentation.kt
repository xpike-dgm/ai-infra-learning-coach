package coach.presentation

import coach.model.DisplayPreference
import coach.model.ThemeChoice

/**
 * The theme and motion setting on Profile (17E, `D-138`). A theme changes only how the app looks: both themes use the
 * same measured tokens, so no meaning, tone or contrast floor changes with it. The motion switch only ever turns the
 * app's own motion off; with Android's "remove animations" on, it is off whatever is chosen here.
 */
object AppearanceCopy {
    const val TITLE = "Görünüm"
    const val THEME = "Tema"
    const val MOTION = "Hareketi azalt"
    // Review (17E): "Açık" is also the light theme beside it, so the notes say what happens, not on/off.
    const val MOTION_ON = "Uygulamanın kendi geçişleri anında olur."
    const val MOTION_OFF = "Geçişler Android'in animasyon ayarını izler."
    const val MOTION_SYSTEM = "Android'de animasyonlar kaldırılmış; uygulamanın hareketi zaten kapalı."

    val themeLabels: Map<ThemeChoice, String> = mapOf(
        ThemeChoice.SYSTEM to "Sistemle aynı",
        ThemeChoice.LIGHT to "Açık tema",
        ThemeChoice.DARK to "Koyu tema",
    )
}

data class AppearanceView(
    val theme: ThemeChoice,
    val themeOptions: List<Pair<ThemeChoice, String>>,
    val reduceMotion: Boolean,
    val motionNote: String,
)

object AppearancePresentation {
    fun of(preference: DisplayPreference, systemReducedMotion: Boolean): AppearanceView = AppearanceView(
        theme = preference.theme,
        themeOptions = ThemeChoice.entries.map { it to AppearanceCopy.themeLabels.getValue(it) },
        reduceMotion = preference.reduceMotion,
        motionNote = when {
            systemReducedMotion -> AppearanceCopy.MOTION_SYSTEM
            preference.reduceMotion -> AppearanceCopy.MOTION_ON
            else -> AppearanceCopy.MOTION_OFF
        },
    )
}
