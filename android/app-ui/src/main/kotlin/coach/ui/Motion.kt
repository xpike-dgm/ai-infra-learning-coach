package coach.ui

import android.provider.Settings
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import coach.presentation.MotionEasing
import coach.presentation.MotionPolicy
import coach.presentation.MotionPurpose
import coach.presentation.ReducedMotion

/**
 * 17B (`MOTX-v0`): `VDSX-v0` §10 turned into Compose. Every animation in `app-ui` is made here, from a purpose; which
 * purpose and how long are decided in core ([MotionPolicy]). Nothing here repeats, pulses, counts down or celebrates, and
 * nothing is the only sign that something changed: the text beside it already says so.
 */

/** Whether the platform asked for reduced motion; read once by [CoachTheme] and decided by [ReducedMotion]. */
val LocalReducedMotion = staticCompositionLocalOf { false }

/** The animator duration scale Android's "remove animations" sets to 0. */
@Composable
fun platformReducedMotion(inApp: Boolean = false): Boolean {
    val resolver = LocalContext.current.contentResolver
    // 17E: the learner's own switch only adds to the platform's ([ReducedMotion]).
    return remember(resolver, inApp) {
        ReducedMotion.of(Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f), inApp)
    }
}

/** Whether Android itself removed animations, apart from the learner's switch (17E: Profile says which one is on). */
@Composable
fun systemReducedMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) { ReducedMotion.of(Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)) }
}

/** The Compose animation for [purpose]: the declared band, or an immediate snap when motion is reduced. */
@Composable
@ReadOnlyComposable
fun <T> motionSpec(purpose: MotionPurpose): FiniteAnimationSpec<T> {
    val spec = MotionPolicy.spec(purpose, LocalReducedMotion.current)
    if (spec.durationMs == 0) return snap()
    val easing = when (spec.easing) {
        MotionEasing.STANDARD -> FastOutSlowInEasing
        MotionEasing.DECELERATE -> LinearOutSlowInEasing
    }
    return tween(durationMillis = spec.durationMs, easing = easing)
}

/**
 * Content that fades in when [key] changes — a destination or a detail arriving ([MotionPurpose.ORIENTATION]), or an
 * answer's result ([MotionPurpose.FEEDBACK]). It only fades: it never delays input, and leaving is never animated. It is
 * never put around a focused flow, whose exit must be visible from the first frame (`VDSX-v0` §10.3).
 */
@Composable
fun ArriveOnChange(key: Any?, purpose: MotionPurpose, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    // With motion reduced the content is visible from its first frame: no fade, and no blank frame before it.
    val reduced = LocalReducedMotion.current
    val alpha = remember(key) { Animatable(if (reduced) 1f else 0f) }
    val spec = motionSpec<Float>(purpose)
    LaunchedEffect(key) { if (!reduced) alpha.animateTo(1f, spec) }
    Box(modifier.graphicsLayer { this.alpha = alpha.value }) { content() }
}

/** A group that grows or shrinks smoothly when something inside it opens or closes ([MotionPurpose.CONTINUITY]). */
@Composable
fun Modifier.continuousSize(): Modifier = this.animateContentSize(animationSpec = motionSpec(MotionPurpose.CONTINUITY))
