package coach.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 17B (`MOTX-v0`): `VDSX-v0` §10 as checks — four purposes, short bands, no celebration, and nothing lost when reduced. */
class MotionTest {

    @Test
    fun `the purposes are VDSX-v0's four and each has one band`() {
        assertEquals(listOf("orientation", "continuity", "feedback", "state_change"), MotionPurpose.entries.map { it.id })
        assertEquals(MotionPurpose.entries.toSet(), MotionPolicy.bands.keys)
        MotionPolicy.bands.forEach { (purpose, spec) -> assertEquals(purpose, spec.purpose) }
    }

    @Test
    fun `nothing is long enough to delay the next action and feedback is the briefest`() {
        val bands = MotionPolicy.bands.values
        assertTrue(bands.all { it.durationMs in 1..MotionPolicy.LONGEST_MS }, bands.toString())
        assertTrue(MotionPolicy.LONGEST_MS <= 300)
        val feedback = MotionPolicy.bands.getValue(MotionPurpose.FEEDBACK).durationMs
        assertTrue(bands.all { it.durationMs >= feedback }, "feedback is micro")
        assertTrue(feedback <= 150)
    }

    @Test
    fun `with motion reduced every change is immediate`() {
        MotionPurpose.entries.forEach { purpose ->
            assertEquals(0, MotionPolicy.spec(purpose, reduced = true).durationMs)
            assertEquals(MotionPolicy.bands.getValue(purpose), MotionPolicy.spec(purpose, reduced = false))
        }
        assertTrue(ReducedMotion.of(0f))
        assertFalse(ReducedMotion.of(1f))
        assertFalse(ReducedMotion.of(0.5f))
    }

    @Test
    fun `nothing celebrates and the forbidden motions are VDSX-v0's`() {
        assertFalse(MotionPolicy.CELEBRATION_ALLOWED)
        assertEquals(
            listOf("countdown_or_urgency", "pressure_pulse", "celebration_on_completion", "decay_or_loss", "streak_combo_or_score",
                "sole_indicator_of_change", "blocks_exit"),
            ForbiddenMotion.entries.map { it.id },
        )
    }
}
