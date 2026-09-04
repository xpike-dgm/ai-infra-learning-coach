package coach.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import coach.presentation.DesignTokens
import coach.presentation.Theme
import coach.presentation.Tone

/**
 * `VDSX-v0`'s expression layer and `WFPX-v0`'s measured palette, as a Compose theme.
 *
 * **Dynamic colour is never consulted.** Compose only derives a palette from the wallpaper where
 * the dynamic scheme builders are called, so disabling it means those builders appear nowhere in
 * the project — including in prose like this, since the check that enforces it is a plain source
 * scan. A wallpaper-derived palette would silently replace ratios that were measured.
 *
 * The tokens themselves live in `core-presentation` so their contrast can be recomputed by an
 * ordinary JVM test. This file only turns them into Compose types.
 */

private fun tokenColor(theme: Theme, token: String): Color =
    Color(DesignTokens.hex(theme, token).removePrefix("#").toLong(16) or 0xFF000000L)

/** Container and on-container colours for one tone. */
data class ToneColor(val container: Color, val onContainer: Color)

/**
 * The tone palette for the active theme. Exposed separately from Material's `ColorScheme` because
 * these six tones carry accepted **meaning**, and mapping them onto Material's semantic roles
 * would let a component pick "the error colour" for a learning state.
 */
val LocalToneColors = staticCompositionLocalOf<Map<Tone, ToneColor>> {
    error("CoachTheme not applied")
}

private fun toneColors(theme: Theme): Map<Tone, ToneColor> =
    DesignTokens.toneTokens.mapValues { (_, tokens) ->
        ToneColor(
            container = tokenColor(theme, tokens.first),
            onContainer = tokenColor(theme, tokens.second),
        )
    }

private fun colorScheme(theme: Theme): ColorScheme {
    val surface = tokenColor(theme, "surface")
    val surfaceVariant = tokenColor(theme, "surface_variant")
    val onSurface = tokenColor(theme, "on_surface")
    val onSurfaceMuted = tokenColor(theme, "on_surface_muted")
    val outline = tokenColor(theme, "outline")
    val active = tokenColor(theme, "tone_active")
    val onActive = tokenColor(theme, "on_tone_active")
    val fault = tokenColor(theme, "tone_fault")
    val onFault = tokenColor(theme, "on_tone_fault")

    val base = when (theme) {
        Theme.LIGHT -> lightColorScheme()
        Theme.DARK -> darkColorScheme()
    }

    return base.copy(
        primary = active,
        onPrimary = onActive,
        background = surface,
        onBackground = onSurface,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceMuted,
        outline = outline,
        // Material's error role is the system fault tone and nothing else. No learning state may
        // reach it, which is why LearningTone has no fault value to hand over.
        error = fault,
        onError = onFault,
    )
}

@Composable
fun CoachTheme(
    dark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val theme = if (dark) Theme.DARK else Theme.LIGHT
    CompositionLocalProvider(LocalToneColors provides toneColors(theme)) {
        MaterialTheme(colorScheme = colorScheme(theme), content = content)
    }
}

/**
 * A state chip.
 *
 * The label is always rendered as text and the state is always exposed to accessibility services
 * as text, so colour is never the only carrier of meaning (`VDSX-v0`, `UXIA-v0` §17).
 */
@Composable
fun StateChip(
    label: String,
    tone: Tone,
    modifier: Modifier = Modifier,
) {
    val colors = LocalToneColors.current.getValue(tone)
    Text(
        text = label,
        color = colors.onContainer,
        modifier = modifier
            .background(colors.container, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .semantics { stateDescription = label },
    )
}

/**
 * Every interactive target keeps the accepted floor. `VDSX-v0` adopts the stricter platform rule
 * over WCAG's 24px, and `WFPX-v0` keeps the focused-flow exit at the full target even when space
 * is tight, so this is a minimum rather than a suggestion.
 */
fun Modifier.minimumTouchTarget(): Modifier =
    this.sizeIn(
        minWidth = DesignTokens.MINIMUM_TOUCH_TARGET_DP.dp,
        minHeight = DesignTokens.MINIMUM_TOUCH_TARGET_DP.dp,
    )
