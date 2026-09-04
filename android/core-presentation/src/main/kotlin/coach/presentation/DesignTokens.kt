package coach.presentation

/**
 * `WFPX-v0 / D-074`'s measured palette, as plain data.
 *
 * The tokens live in core rather than in the theme file for one reason: contrast has to be
 * **recomputed** from these hex values rather than asserted from a remembered ratio, and that
 * recomputation should run as an ordinary JVM test with no device and no Compose. The precedent
 * is 8G itself, where the validator caught a hand-declared minimum that was wrong.
 *
 * The palette is measured per theme. Dark is not an inversion of light, and there is no
 * green–amber–red ramp: `attention` is violet, and red belongs to `system_fault` alone, because a
 * traffic-light ramp would encode learning states as severity levels.
 */
enum class Theme { LIGHT, DARK }

object DesignTokens {

    private val light: Map<String, String> = mapOf(
        "surface" to "#FFFFFF",
        "surface_variant" to "#F1F3F5",
        "on_surface" to "#16181B",
        "on_surface_muted" to "#565C63",
        "outline" to "#767C84",
        "focus_ring" to "#0B5FBF",
        "tone_neutral" to "#4A5058",
        "on_tone_neutral" to "#FFFFFF",
        "tone_active" to "#0B5FBF",
        "on_tone_active" to "#FFFFFF",
        "tone_positive" to "#1B6E3C",
        "on_tone_positive" to "#FFFFFF",
        "tone_attention" to "#6A3FB5",
        "on_tone_attention" to "#FFFFFF",
        "tone_pending" to "#2F6B72",
        "on_tone_pending" to "#FFFFFF",
        "tone_fault" to "#B3261E",
        "on_tone_fault" to "#FFFFFF",
    )

    private val dark: Map<String, String> = mapOf(
        "surface" to "#121417",
        "surface_variant" to "#1D2126",
        "on_surface" to "#E6E9ED",
        "on_surface_muted" to "#A8B0B8",
        "outline" to "#7A828B",
        "focus_ring" to "#7FB0F5",
        "tone_neutral" to "#A9B1BA",
        "on_tone_neutral" to "#16181B",
        "tone_active" to "#7FB0F5",
        "on_tone_active" to "#0A1B2E",
        "tone_positive" to "#79D3A0",
        "on_tone_positive" to "#0A1F13",
        "tone_attention" to "#C0A6F5",
        "on_tone_attention" to "#1E1030",
        "tone_pending" to "#8FCBD3",
        "on_tone_pending" to "#0B2124",
        "tone_fault" to "#F2B8B5",
        "on_tone_fault" to "#3A0B08",
    )

    fun tokens(theme: Theme): Map<String, String> = when (theme) {
        Theme.LIGHT -> light
        Theme.DARK -> dark
    }

    fun hex(theme: Theme, token: String): String =
        tokens(theme)[token] ?: error("unknown design token: $token")

    /** Container and on-container token names for each tone (`WFPX-v0` §tone_token_map). */
    val toneTokens: Map<Tone, Pair<String, String>> = mapOf(
        Tone.NEUTRAL to ("tone_neutral" to "on_tone_neutral"),
        Tone.ACTIVE to ("tone_active" to "on_tone_active"),
        Tone.POSITIVE_CONFIRMED to ("tone_positive" to "on_tone_positive"),
        Tone.ATTENTION to ("tone_attention" to "on_tone_attention"),
        Tone.PENDING_UNRESOLVED to ("tone_pending" to "on_tone_pending"),
        Tone.SYSTEM_FAULT to ("tone_fault" to "on_tone_fault"),
    )

    /** WCAG 2.2 anchors adopted by `WFPX-v0`. */
    const val TEXT_CONTRAST_MIN = 4.5
    const val NON_TEXT_CONTRAST_MIN = 3.0

    /** `VDSX-v0` adopts the stricter platform rule over the 24px WCAG floor. */
    const val MINIMUM_TOUCH_TARGET_DP = 48

    /** Layout must survive all of these without losing content or function. */
    val supportedTextScalesPercent: List<Int> = listOf(100, 150, 200)

    /** Dynamic colour is never consulted; the palette is measured, not derived from a wallpaper. */
    const val DYNAMIC_COLOUR_ENABLED = false
}

/**
 * WCAG 2.2 relative luminance and contrast, computed from the token hex values.
 *
 * These are the formulas the 8G validator uses. Having them here means the same numbers can be
 * recomputed inside the product's own test suite instead of being copied across as constants.
 */
object Contrast {

    fun ratio(foregroundHex: String, backgroundHex: String): Double {
        val a = luminance(foregroundHex)
        val b = luminance(backgroundHex)
        val hi = maxOf(a, b)
        val lo = minOf(a, b)
        return (hi + 0.05) / (lo + 0.05)
    }

    fun luminance(hex: String): Double {
        val (r, g, b) = rgb(hex)
        return 0.2126 * channel(r) + 0.7152 * channel(g) + 0.0722 * channel(b)
    }

    private fun rgb(hex: String): Triple<Int, Int, Int> {
        val h = hex.removePrefix("#")
        require(h.length == 6) { "expected a 6-digit hex colour, got: $hex" }
        return Triple(
            h.substring(0, 2).toInt(16),
            h.substring(2, 4).toInt(16),
            h.substring(4, 6).toInt(16),
        )
    }

    private fun channel(value: Int): Double {
        val c = value / 255.0
        return if (c <= 0.03928) c / 12.92 else Math.pow((c + 0.055) / 1.055, 2.4)
    }
}
