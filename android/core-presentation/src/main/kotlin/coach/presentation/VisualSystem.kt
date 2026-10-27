package coach.presentation

/**
 * 17A (`VPLX-v0 / D-133`): `VDSX-v0`'s typography, rhythm, colour roles and tone marks as plain data, so the theme in
 * `app-ui` only translates them and every rule here can be checked by an ordinary JVM test.
 *
 * Nothing in this file adds meaning. A type role, a spacing step or a mark is how an accepted state is drawn; which
 * state applies is decided elsewhere and is never derived from how it looks.
 */

/** `VDSX-v0` §6.1's eight roles, and no others. `MONO` is for code, terminal output and identifiers. */
enum class TypeRole(val id: String) {
    DISPLAY("display"),
    TITLE_LARGE("title_large"),
    TITLE("title"),
    BODY_LARGE("body_large"),
    BODY("body"),
    LABEL("label"),
    CAPTION("caption"),
    MONO("mono"),
}

/** One role's size, leading and weight, in scalable units (sp), so the system font size is honoured (`VDSX-v0` §6.2). */
data class TypeSpec(val sizeSp: Int, val lineHeightSp: Int, val weight: Int, val monospace: Boolean = false)

/**
 * The scale is a **declared product default**, not a measured value (`VDSX-v0` §6.2); it is a calibration input for 18.
 * It follows Material 3's baseline sizes so nothing is smaller than the platform's own body text, with heavier titles so
 * hierarchy does not rest on size alone. No role is all-caps and none applies a case transform (`VDSX-v0` §6.3).
 */
object TypeScale {
    val roles: Map<TypeRole, TypeSpec> = mapOf(
        TypeRole.DISPLAY to TypeSpec(sizeSp = 32, lineHeightSp = 40, weight = 400),
        TypeRole.TITLE_LARGE to TypeSpec(sizeSp = 22, lineHeightSp = 28, weight = 500),
        TypeRole.TITLE to TypeSpec(sizeSp = 18, lineHeightSp = 24, weight = 600),
        TypeRole.BODY_LARGE to TypeSpec(sizeSp = 16, lineHeightSp = 24, weight = 400),
        TypeRole.BODY to TypeSpec(sizeSp = 14, lineHeightSp = 20, weight = 400),
        TypeRole.LABEL to TypeSpec(sizeSp = 14, lineHeightSp = 20, weight = 500),
        TypeRole.CAPTION to TypeSpec(sizeSp = 12, lineHeightSp = 16, weight = 400),
        TypeRole.MONO to TypeSpec(sizeSp = 14, lineHeightSp = 20, weight = 400, monospace = true),
    )

    /**
     * Every Material type slot, and the one role it is drawn with. The theme builds Material's `Typography` from this
     * map alone, so a component that asks Material for a style still gets a `VDSX-v0` role.
     */
    val materialSlots: Map<String, TypeRole> = mapOf(
        "displayLarge" to TypeRole.DISPLAY,
        "displayMedium" to TypeRole.DISPLAY,
        "displaySmall" to TypeRole.DISPLAY,
        "headlineLarge" to TypeRole.TITLE_LARGE,
        "headlineMedium" to TypeRole.TITLE_LARGE,
        "headlineSmall" to TypeRole.TITLE_LARGE,
        "titleLarge" to TypeRole.TITLE_LARGE,
        "titleMedium" to TypeRole.TITLE,
        "titleSmall" to TypeRole.LABEL,
        "bodyLarge" to TypeRole.BODY_LARGE,
        "bodyMedium" to TypeRole.BODY,
        "bodySmall" to TypeRole.CAPTION,
        "labelLarge" to TypeRole.LABEL,
        "labelMedium" to TypeRole.CAPTION,
        "labelSmall" to TypeRole.CAPTION,
    )
}

/**
 * `VDSX-v0` §8: a 4dp rhythm with the declared steps `4, 8, 12, 16, 24, 32, 48`. Screens space with these names only;
 * a validator scans `app-ui` for any other dp literal.
 */
object Spacing {
    const val XXS = 4
    const val XS = 8
    const val S = 12
    const val M = 16
    const val L = 24
    const val XL = 32
    const val XXL = 48

    val steps: List<Int> = listOf(XXS, XS, S, M, L, XL, XXL)
}

/**
 * Every Material colour role, and the measured token it is drawn with, per theme. Material's own baseline palette is
 * purple; a role left to its default would put an unmeasured colour on screen (17A found the navigation bar wearing it,
 * close to the violet `attention` tone). The theme copies this map onto Material's `ColorScheme` and nothing else.
 *
 * The same token is used in both themes; only its hex differs (`VDSX-v0` §7.4).
 */
object MaterialColorRoles {
    val roles: Map<String, String> = mapOf(
        "primary" to "tone_active",
        "onPrimary" to "on_tone_active",
        "primaryContainer" to "surface_variant",
        "onPrimaryContainer" to "on_surface",
        "inversePrimary" to "surface_variant",
        // The selected navigation item's indicator: the current destination, drawn with the `active` pair.
        "secondary" to "tone_neutral",
        "onSecondary" to "on_tone_neutral",
        "secondaryContainer" to "tone_active",
        "onSecondaryContainer" to "on_tone_active",
        "tertiary" to "tone_pending",
        "onTertiary" to "on_tone_pending",
        "tertiaryContainer" to "surface_variant",
        "onTertiaryContainer" to "on_surface",
        "background" to "surface",
        "onBackground" to "on_surface",
        "surface" to "surface",
        "onSurface" to "on_surface",
        "surfaceVariant" to "surface_variant",
        "onSurfaceVariant" to "on_surface_muted",
        // A tint equal to the surface means elevation never recolours a surface away from its measured value.
        "surfaceTint" to "surface",
        "inverseSurface" to "on_surface",
        "inverseOnSurface" to "surface",
        // Material's error role is the system fault tone and nothing else (`DSIX-v0` §6).
        "error" to "tone_fault",
        "onError" to "on_tone_fault",
        "errorContainer" to "tone_fault",
        "onErrorContainer" to "on_tone_fault",
        "outline" to "outline",
        "outlineVariant" to "outline",
        "scrim" to "on_surface",
        "surfaceBright" to "surface",
        "surfaceDim" to "surface_variant",
        "surfaceContainerLowest" to "surface",
        "surfaceContainerLow" to "surface",
        "surfaceContainer" to "surface_variant",
        "surfaceContainerHigh" to "surface_variant",
        "surfaceContainerHighest" to "surface_variant",
    )

    /** Text drawn on a background: (foreground role, background role). Each pair is recomputed against 4.5:1. */
    val textPairs: List<Pair<String, String>> = listOf(
        "onPrimary" to "primary",
        "onPrimaryContainer" to "primaryContainer",
        "onSecondary" to "secondary",
        "onSecondaryContainer" to "secondaryContainer",
        "onTertiary" to "tertiary",
        "onTertiaryContainer" to "tertiaryContainer",
        "onBackground" to "background",
        "onSurface" to "surface",
        "onSurface" to "surfaceVariant",
        "onSurface" to "surfaceContainer",
        "onSurfaceVariant" to "surface",
        "onSurfaceVariant" to "surfaceVariant",
        "onSurfaceVariant" to "surfaceContainer",
        // A text button's label, and the selected navigation label on the bar.
        "primary" to "surface",
        "primary" to "surfaceContainer",
        "inverseOnSurface" to "inverseSurface",
        "inversePrimary" to "inverseSurface",
        "onError" to "error",
        "onErrorContainer" to "errorContainer",
    )

    /** Boundaries and indicators: (foreground role, background role), each recomputed against 3:1 (WCAG 1.4.11). */
    val nonTextPairs: List<Pair<String, String>> = listOf(
        "outline" to "surface",
        "outline" to "surfaceVariant",
        "secondaryContainer" to "surfaceContainer",
        "primary" to "surface",
    )

    fun token(role: String): String = roles[role] ?: error("unmapped Material colour role: $role")
}

/**
 * How a state chip is drawn since 17A: outlined, with its tone's colour as the label, the mark and the border, on the
 * surface it sits on — the page or a grouping block. A filled chip in the tone's container colour read as a button on
 * the emulator, which a state must never do. Each tone colour is recomputed as text (4.5:1) and as a boundary (3:1)
 * against both surfaces in both themes, the same measurement every other pair gets.
 */
object ChipColors {
    val surfaces: List<String> = listOf("surface", "surface_variant")

    /** The tone's own `tone_*` token: the colour that names the tone everywhere else. */
    fun foreground(tone: Tone): String = DesignTokens.toneTokens.getValue(tone).first
}

/**
 * `VDSX-v0` §7.3: every tone is accompanied by a non-colour differentiator. This is that differentiator, one icon
 * metaphor per tone, the same on every surface (§9). The icon never replaces the label; it is drawn beside it and
 * marked decorative, because the label already says the state.
 */
enum class ToneMark(val id: String) {
    INFO("info"),
    PLAY("play"),
    CHECK("check"),
    LOOK("look"),
    REFRESH("refresh"),
    WARNING("warning"),
    ;

    companion object {
        fun of(tone: Tone): ToneMark = when (tone) {
            Tone.NEUTRAL -> INFO
            Tone.ACTIVE -> PLAY
            Tone.POSITIVE_CONFIRMED -> CHECK
            // Something needs a closer look: a verification or a remediation. Not an alarm, so not a warning sign.
            Tone.ATTENTION -> LOOK
            Tone.PENDING_UNRESOLVED -> REFRESH
            // A real technical fault, and only that (`VDSX-v0` §fault_tone_allowed_states).
            Tone.SYSTEM_FAULT -> WARNING
        }
    }
}
