package coach.presentation

import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `VDSX-v0` and `WFPX-v0` as checks.
 *
 * Contrast is **recomputed from the token hex values**, never asserted from a remembered ratio.
 * That is not pedantry: at 8G a hand-declared minimum turned out to be wrong and only the
 * recomputation caught it.
 */
class DesignSystemTest {

    private val textPairs = listOf(
        "on_surface" to "surface",
        "on_surface" to "surface_variant",
        "on_surface_muted" to "surface",
        "on_surface_muted" to "surface_variant",
    )

    private val nonTextPairs = listOf(
        "outline" to "surface",
        "outline" to "surface_variant",
        "focus_ring" to "surface",
        "focus_ring" to "surface_variant",
    )

    private fun round2(value: Double) = round(value * 100) / 100

    @Test
    fun `text on surface meets the WCAG anchor in both themes`() {
        Theme.entries.forEach { theme ->
            textPairs.forEach { (fg, bg) ->
                val ratio = Contrast.ratio(DesignTokens.hex(theme, fg), DesignTokens.hex(theme, bg))
                assertTrue(
                    ratio >= DesignTokens.TEXT_CONTRAST_MIN,
                    "$theme $fg on $bg is ${round2(ratio)}:1, below ${DesignTokens.TEXT_CONTRAST_MIN}",
                )
            }
        }
    }

    @Test
    fun `non-text and boundary colours meet the WCAG anchor in both themes`() {
        Theme.entries.forEach { theme ->
            nonTextPairs.forEach { (fg, bg) ->
                val ratio = Contrast.ratio(DesignTokens.hex(theme, fg), DesignTokens.hex(theme, bg))
                assertTrue(
                    ratio >= DesignTokens.NON_TEXT_CONTRAST_MIN,
                    "$theme $fg on $bg is ${round2(ratio)}:1, below ${DesignTokens.NON_TEXT_CONTRAST_MIN}",
                )
            }
        }
    }

    @Test
    fun `text on every tone container meets the WCAG anchor in both themes`() {
        Theme.entries.forEach { theme ->
            DesignTokens.toneTokens.forEach { (tone, tokens) ->
                val (container, onContainer) = tokens
                val ratio = Contrast.ratio(
                    DesignTokens.hex(theme, onContainer),
                    DesignTokens.hex(theme, container),
                )
                assertTrue(
                    ratio >= DesignTokens.TEXT_CONTRAST_MIN,
                    "$theme ${tone.id}: ${round2(ratio)}:1, below ${DesignTokens.TEXT_CONTRAST_MIN}",
                )
            }
        }
    }

    @Test
    fun `every tone container is distinguishable from the surface it sits on`() {
        Theme.entries.forEach { theme ->
            DesignTokens.toneTokens.forEach { (tone, tokens) ->
                val ratio = Contrast.ratio(
                    DesignTokens.hex(theme, tokens.first),
                    DesignTokens.hex(theme, "surface"),
                )
                assertTrue(
                    ratio >= DesignTokens.NON_TEXT_CONTRAST_MIN,
                    "$theme ${tone.id} container on surface is ${round2(ratio)}:1",
                )
            }
        }
    }

    @Test
    fun `both themes declare the same token set`() {
        assertEquals(
            DesignTokens.tokens(Theme.LIGHT).keys,
            DesignTokens.tokens(Theme.DARK).keys,
        )
    }

    @Test
    fun `dark is not an inversion of light`() {
        // If dark were derived by inverting light, the measured-per-theme claim would be false.
        val identical = DesignTokens.tokens(Theme.LIGHT).filter { (token, hex) ->
            DesignTokens.hex(Theme.DARK, token) == hex
        }
        assertTrue(identical.size < DesignTokens.tokens(Theme.LIGHT).size / 2,
            "dark and light share too many values: ${identical.keys}")
    }

    @Test
    fun `there are exactly six tones and every one has tokens`() {
        assertEquals(6, Tone.entries.size)
        Tone.entries.forEach { tone ->
            val tokens = DesignTokens.toneTokens[tone]
            assertTrue(tokens != null, "tone ${tone.id} has no tokens")
        }
    }

    @Test
    fun `no learning state can wear the fault tone`() {
        // Structural, not conventional: LearningTone has no SYSTEM_FAULT to assign.
        assertTrue(LearningTone.entries.none { it.tone == Tone.SYSTEM_FAULT })
        SkillPresentationState.entries.forEach { state ->
            assertTrue(state.tone.tone != Tone.SYSTEM_FAULT, "${state.name} wears the fault tone")
        }
        QualifierTone.entries.forEach { qualifier ->
            assertTrue(qualifier.tone.tone != Tone.SYSTEM_FAULT, "${qualifier.id} wears the fault tone")
        }
    }

    @Test
    fun `only the two system states may wear the fault tone`() {
        assertEquals(2, SystemFaultState.entries.size)
        assertEquals(
            listOf("error_recoverable", "data_recovery_required"),
            SystemFaultState.entries.map { it.id },
        )
    }

    @Test
    fun `every skill state has exactly one declared tone`() {
        val tones = SkillPresentationState.entries.associateWith { it.tone }
        assertEquals(8, tones.size)
    }

    @Test
    fun `states that are not failures never take an alarming tone`() {
        // VDSX-v0 lists these as requiring a non-negative tone: waiting is not failing.
        listOf(
            SkillPresentationState.NOT_YET_EVIDENCED,
            SkillPresentationState.CONFIRMED_REVIEW_DUE,
            SkillPresentationState.PREREQUISITE_UNRESOLVED,
        ).forEach { state ->
            assertEquals(LearningTone.NEUTRAL, state.tone, "${state.name} must stay neutral")
        }
    }

    @Test
    fun `appearing in an attention group does not upgrade a tone`() {
        SkillPresentationState.entries.forEach { state ->
            assertEquals(state.tone, toneInAttentionGroup(state),
                "${state.name} changed tone by being grouped")
        }
    }

    @Test
    fun `the touch target floor is the stricter platform rule`() {
        assertEquals(48, DesignTokens.MINIMUM_TOUCH_TARGET_DP)
    }

    @Test
    fun `text scaling support reaches 200 percent`() {
        assertEquals(listOf(100, 150, 200), DesignTokens.supportedTextScalesPercent)
    }

    @Test
    fun `dynamic colour is off`() {
        assertTrue(!DesignTokens.DYNAMIC_COLOUR_ENABLED)
    }

    @Test
    fun `a malformed token is rejected rather than silently rendered`() {
        val thrown = runCatching { Contrast.ratio("#FFF", "#000000") }.exceptionOrNull()
        assertTrue(thrown is IllegalArgumentException, "expected a rejection, got $thrown")
    }
}
