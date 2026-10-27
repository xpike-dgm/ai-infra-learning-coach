package coach.presentation

/**
 * 17B (`MOTX-v0 / D-134`): `VDSX-v0` §10 as plain data. Motion may orient, keep an object continuous and confirm that
 * something happened; it may not persuade, pressure or reward (§10.1). Which motion a change gets is decided here, by
 * its purpose, so `app-ui` only turns a [MotionSpec] into a Compose animation and every rule can be checked on the JVM.
 *
 * Motion is never the only carrier of a change: every state is already said in text and announced (`VDSX-v0` §12), and
 * with motion reduced nothing is lost (§10.4).
 */

/** `VDSX-v0` §10.2's four purpose classes, and no others. */
enum class MotionPurpose(val id: String) {
    /** Where did this come from, where did it go: a destination or a detail appearing. */
    ORIENTATION("orientation"),

    /** This is the same object, moved or expanded: a panel opening, a focused flow entered. */
    CONTINUITY("continuity"),

    /** Your input was received: an answer's result appearing. */
    FEEDBACK("feedback"),

    /** Canonical state actually changed: a state chip's colour following its new state. */
    STATE_CHANGE("state_change"),
}

/** How a motion eases. Standard for things that stay on screen; decelerate for things that arrive. */
enum class MotionEasing { STANDARD, DECELERATE }

/** One motion: how long it takes and how it eases. A zero duration means the change is immediate. */
data class MotionSpec(val purpose: MotionPurpose, val durationMs: Int, val easing: MotionEasing)

/**
 * The motions the system forbids (`VDSX-v0` §10.3). [MotionPolicy] offers none of them — no repeating motion, nothing
 * longer than [MotionPolicy.LONGEST_MS], no celebration — and `tools/validate_motion.py` scans the app's Compose code for
 * the APIs that would draw them anyway; that scan, not this type, is what keeps them out of the screens.
 */
enum class ForbiddenMotion(val id: String) {
    COUNTDOWN_OR_URGENCY("countdown_or_urgency"),
    PRESSURE_PULSE("pressure_pulse"),
    CELEBRATION_ON_COMPLETION("celebration_on_completion"),
    DECAY_OR_LOSS("decay_or_loss"),
    STREAK_COMBO_OR_SCORE("streak_combo_or_score"),
    SOLE_INDICATOR_OF_CHANGE("sole_indicator_of_change"),
    BLOCKS_EXIT("blocks_exit"),
}

object MotionPolicy {

    /**
     * The duration bands are **declared product defaults** and calibration inputs for 18 (`VDSX-v0` §10.2): feedback is
     * brief, orientation and continuity are short, and nothing is long enough to delay the next action. They follow
     * Material 3's short and medium duration tokens.
     */
    val bands: Map<MotionPurpose, MotionSpec> = mapOf(
        MotionPurpose.FEEDBACK to MotionSpec(MotionPurpose.FEEDBACK, durationMs = 100, easing = MotionEasing.STANDARD),
        MotionPurpose.ORIENTATION to MotionSpec(MotionPurpose.ORIENTATION, durationMs = 200, easing = MotionEasing.DECELERATE),
        MotionPurpose.CONTINUITY to MotionSpec(MotionPurpose.CONTINUITY, durationMs = 250, easing = MotionEasing.STANDARD),
        MotionPurpose.STATE_CHANGE to MotionSpec(MotionPurpose.STATE_CHANGE, durationMs = 200, easing = MotionEasing.STANDARD),
    )

    /** Nothing in normal use is longer than this; a motion is over before a reader reaches the next line. */
    const val LONGEST_MS = 300

    /**
     * 17B user decision (2026-10-27): no celebration at all, not even when canonical state genuinely changed. The change
     * is said in text and the chip follows its state; nothing celebrates, because a celebration would claim more than the
     * evidence says (`task_completed != mastery_confirmed`).
     */
    const val CELEBRATION_ALLOWED = false

    /** The motion for [purpose], or an immediate change when motion is reduced (`VDSX-v0` §10.4). */
    fun spec(purpose: MotionPurpose, reduced: Boolean): MotionSpec {
        val band = bands.getValue(purpose)
        return if (reduced) band.copy(durationMs = 0) else band
    }
}

/**
 * Reduced motion follows the platform (17B user decision): Android's "remove animations" sets the animator duration
 * scale to 0, and then every motion is immediate. Any other scale keeps the declared bands; Compose itself scales its
 * durations by the same setting. An in-app preference is 17D's.
 */
object ReducedMotion {
    fun of(animatorDurationScale: Float): Boolean = animatorDurationScale <= 0f
}
