package coach.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coach.presentation.DesignTokens
import coach.presentation.MaterialColorRoles
import coach.presentation.MotionPurpose
import coach.presentation.Spacing
import coach.presentation.Theme
import coach.presentation.Tone
import coach.presentation.ToneMark
import coach.presentation.TypeRole
import coach.presentation.TypeScale

/**
 * `VDSX-v0`'s expression layer and `WFPX-v0`'s measured palette, as a Compose theme.
 *
 * **Dynamic colour is never consulted.** Compose only derives a palette from the wallpaper where
 * the dynamic scheme builders are called, so disabling it means those builders appear nowhere in
 * the project — including in prose like this, since the check that enforces it is a plain source
 * scan. A wallpaper-derived palette would silently replace ratios that were measured.
 *
 * The tokens themselves live in `core-presentation` so their contrast can be recomputed by an
 * ordinary JVM test. This file only turns them into Compose types: since 17A (`VPLX-v0`) every
 * Material colour role comes from [MaterialColorRoles] and every type slot from [TypeScale], so no
 * Material default — its purple baseline included — reaches the screen.
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
    fun role(name: String) = tokenColor(theme, MaterialColorRoles.token(name))
    val fault = role("error")
    val onFault = role("onError")

    val base = when (theme) {
        Theme.LIGHT -> lightColorScheme()
        Theme.DARK -> darkColorScheme()
    }

    return base.copy(
        primary = role("primary"),
        onPrimary = role("onPrimary"),
        primaryContainer = role("primaryContainer"),
        onPrimaryContainer = role("onPrimaryContainer"),
        inversePrimary = role("inversePrimary"),
        secondary = role("secondary"),
        onSecondary = role("onSecondary"),
        secondaryContainer = role("secondaryContainer"),
        onSecondaryContainer = role("onSecondaryContainer"),
        tertiary = role("tertiary"),
        onTertiary = role("onTertiary"),
        tertiaryContainer = role("tertiaryContainer"),
        onTertiaryContainer = role("onTertiaryContainer"),
        background = role("background"),
        onBackground = role("onBackground"),
        surface = role("surface"),
        onSurface = role("onSurface"),
        surfaceVariant = role("surfaceVariant"),
        onSurfaceVariant = role("onSurfaceVariant"),
        surfaceTint = role("surfaceTint"),
        inverseSurface = role("inverseSurface"),
        inverseOnSurface = role("inverseOnSurface"),
        // Material's error role is the system fault tone and nothing else. No learning state may
        // reach it, which is why LearningTone has no fault value to hand over.
        error = fault,
        onError = onFault,
        errorContainer = role("errorContainer"),
        onErrorContainer = role("onErrorContainer"),
        outline = role("outline"),
        outlineVariant = role("outlineVariant"),
        scrim = role("scrim"),
        surfaceBright = role("surfaceBright"),
        surfaceDim = role("surfaceDim"),
        surfaceContainerLowest = role("surfaceContainerLowest"),
        surfaceContainerLow = role("surfaceContainerLow"),
        surfaceContainer = role("surfaceContainer"),
        surfaceContainerHigh = role("surfaceContainerHigh"),
        surfaceContainerHighest = role("surfaceContainerHighest"),
    )
}

/** One `VDSX-v0` role as a Compose text style, in sp so the system font size is honoured. */
fun roleStyle(role: TypeRole): TextStyle {
    val spec = TypeScale.roles.getValue(role)
    return TextStyle(
        fontFamily = if (spec.monospace) FontFamily.Monospace else FontFamily.Default,
        fontWeight = FontWeight(spec.weight),
        fontSize = spec.sizeSp.sp,
        lineHeight = spec.lineHeightSp.sp,
    )
}

private fun typography(): Typography {
    fun slot(name: String) = roleStyle(TypeScale.materialSlots.getValue(name))
    return Typography(
        displayLarge = slot("displayLarge"),
        displayMedium = slot("displayMedium"),
        displaySmall = slot("displaySmall"),
        headlineLarge = slot("headlineLarge"),
        headlineMedium = slot("headlineMedium"),
        headlineSmall = slot("headlineSmall"),
        titleLarge = slot("titleLarge"),
        titleMedium = slot("titleMedium"),
        titleSmall = slot("titleSmall"),
        bodyLarge = slot("bodyLarge"),
        bodyMedium = slot("bodyMedium"),
        bodySmall = slot("bodySmall"),
        labelLarge = slot("labelLarge"),
        labelMedium = slot("labelMedium"),
        labelSmall = slot("labelSmall"),
    )
}

@Composable
fun CoachTheme(
    dark: Boolean = isSystemInDarkTheme(),
    // 17E: the learner's own "reduce motion" (`DisplayPreference`); it adds to the platform's, never overrides it.
    reduceMotionInApp: Boolean = false,
    content: @Composable () -> Unit,
) {
    val theme = if (dark) Theme.DARK else Theme.LIGHT
    // 17B: reduced motion follows the platform (`ReducedMotion`); every motion in app-ui reads it.
    CompositionLocalProvider(
        LocalToneColors provides toneColors(theme),
        LocalReducedMotion provides platformReducedMotion(reduceMotionInApp),
        // 17E: the measured `focus_ring` token, 3:1 against both surfaces (`WFPX-v0`), for the keyboard focus ring.
        LocalFocusRing provides tokenColor(theme, "focus_ring"),
    ) {
        MaterialTheme(colorScheme = colorScheme(theme), typography = typography()) {
            // 17A: every screen sits on the measured surface. Before 17A a focused flow had no surface of its own and
            // drew dark text on the window's default dark background.
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                content = content,
            )
        }
    }
}

/** One icon metaphor per tone (`VDSX-v0` §§7.3, 9), decided in core by [ToneMark]. */
private fun ToneMark.icon(): ImageVector = when (this) {
    ToneMark.INFO -> Icons.Outlined.Info
    ToneMark.PLAY -> Icons.Filled.PlayArrow
    ToneMark.CHECK -> Icons.Filled.CheckCircle
    ToneMark.LOOK -> Icons.Filled.Search
    ToneMark.REFRESH -> Icons.Filled.Refresh
    ToneMark.WARNING -> Icons.Filled.Warning
}

/** The one stroke width: a chip's border. Not a spacing step, so it is named here and nowhere else. */
val HAIRLINE = 1.dp

/**
 * A state chip.
 *
 * The label is always rendered as text and the state is always exposed to accessibility services
 * as text, so colour is never the only carrier of meaning (`VDSX-v0`, `UXIA-v0` §17). Since 17A it
 * also carries its tone's mark, the non-colour differentiator `VDSX-v0` §7.3 asks for, and it is
 * compact, outlined and unfilled so it never reads as a button: a chip is a state, not an action.
 */
@Composable
fun StateChip(
    label: String,
    tone: Tone,
    modifier: Modifier = Modifier,
    // 17B: what the chip is about. A new subject starts a new chip, so moving from one Skill to another, a list
    // reloading or a row reused is never shown as a state change (`VDSX-v0` §10.2 state_change).
    subject: Any? = null,
) = key(subject) {
    // The tone's own colour as label, mark and border (`ChipColors`): measured against the surface beneath.
    val color by animateColorAsState(LocalToneColors.current.getValue(tone).container, motionSpec(MotionPurpose.STATE_CHANGE), label = "state_chip")
    Row(
        modifier = modifier
            .border(BorderStroke(HAIRLINE, color), RoundedCornerShape(Spacing.XXS.dp))
            .heightIn(min = Spacing.XL.dp)
            .padding(horizontal = Spacing.XS.dp, vertical = Spacing.XXS.dp)
            // 17D: one leaf — the label is spoken once, and a caller's heading or live region is on a focusable node.
            .clearAndSetSemantics { contentDescription = label },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.XS.dp),
    ) {
        // Decorative: the label beside it already says the state.
        Icon(ToneMark.of(tone).icon(), contentDescription = null, tint = color, modifier = Modifier.size(Spacing.M.dp))
        Text(
            text = label,
            color = color,
            style = roleStyle(TypeRole.LABEL),
        )
    }
}

/**
 * Every interactive target keeps the accepted floor. `VDSX-v0` adopts the stricter platform rule
 * over WCAG's 24px, and `WFPX-v0` keeps the focused-flow exit at the full target even when space
 * is tight, so this is a minimum rather than a suggestion.
 *
 * 17E: every target also draws the focus ring ([focusRing]), so whatever keeps the 48dp floor shows where keyboard
 * focus is. It must come before the target's own clickable in the chain, where it can see that target's focus.
 */
fun Modifier.minimumTouchTarget(): Modifier =
    this.focusRing().sizeIn(
        minWidth = DesignTokens.MINIMUM_TOUCH_TARGET_DP.dp,
        minHeight = DesignTokens.MINIMUM_TOUCH_TARGET_DP.dp,
    )

/** The `focus_ring` colour of the theme in use (17E). */
val LocalFocusRing = staticCompositionLocalOf { Color.Unspecified }

/** The ring's width: wider than a chip's hairline, so focus never reads as a state chip's border. */
val FOCUS_RING_WIDTH = DesignTokens.FOCUS_RING_WIDTH_DP.dp

/**
 * A visible focus ring (17E, `D-138`; WCAG 2.2 2.4.7): while the target after it in the chain holds focus — moved there
 * by a keyboard, a D-pad or a switch — a ring in the measured `focus_ring` colour is drawn around it, just outside the
 * target so it never covers the target's own text. Touch does not move focus to a button, so a tap draws no ring. The
 * ring is the only change: no colour, size or layout moves, and nothing animates.
 */
fun Modifier.focusRing(): Modifier = composed {
    var focused by remember { mutableStateOf(false) }
    val ring = LocalFocusRing.current
    this
        .onFocusChanged { focused = it.hasFocus }
        .drawWithContent {
            drawContent()
            if (focused && ring != Color.Unspecified) {
                val width = FOCUS_RING_WIDTH.toPx()
                val outset = FOCUS_RING_OUTSET.toPx() + width / 2
                drawRoundRect(
                    color = ring,
                    topLeft = Offset(-outset, -outset),
                    size = Size(size.width + 2 * outset, size.height + 2 * outset),
                    cornerRadius = CornerRadius(Spacing.XS.dp.toPx() + outset),
                    style = Stroke(width),
                )
            }
        }
}

/** How far outside its target the ring is drawn; within the screen's side gutter, so it is never cut. */
val FOCUS_RING_OUTSET = DesignTokens.FOCUS_RING_OUTSET_DP.dp
