package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * 16D (`STGX-v0`): the capacity preferences are the newest readable row of each key, the accepted presets where there
 * is none, and today's choice for today only — so the planner's input is exactly what the learner last said.
 */
class CapacityPreferencesTest {

    private var sequence = 0L
    private fun row(key: String, value: String, day: String? = null) = PreferenceRecord(key, value, day, ++sequence)

    @Test
    fun `with nothing stored the accepted presets hold and nothing is chosen for today`() {
        val p = CapacityPreferences.of(emptyList(), "2026-10-25")
        assertEquals(listOf(30, 60, 90), listOf(p.shortMinutes, p.normalMinutes, p.intensiveMinutes))
        assertEquals(TodayCapacityChoice.None, p.today)
        assertEquals(emptySet(), p.customized)
        assertEquals(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90), p.input())
    }

    @Test
    fun `the newest row of a key wins, and an unreadable row is skipped rather than guessed`() {
        val rows = listOf(
            row(CapacityPreferences.NORMAL_KEY, "45"),
            row(CapacityPreferences.NORMAL_KEY, "50"),
            row(CapacityPreferences.NORMAL_KEY, "fifty"),
            row(CapacityPreferences.SHORT_KEY, "2000"),
        )
        val p = CapacityPreferences.of(rows.shuffled(java.util.Random(7)), "2026-10-25")
        assertEquals(50, p.normalMinutes)
        assertEquals(30, p.shortMinutes, "an out-of-range length is not a preference")
        assertEquals(setOf(CapacityProfile.NORMAL), p.customized)
    }

    @Test
    fun `today's choice applies to its own day only and carries no debt into tomorrow`() {
        val rows = listOf(
            row(CapacityPreferences.TODAY_KEY, "intensive", day = "2026-10-24"),
            row(CapacityPreferences.TODAY_KEY, "25", day = "2026-10-25"),
        )
        assertEquals(TodayCapacityChoice.Minutes(25), CapacityPreferences.of(rows, "2026-10-25").today)
        assertEquals(TodayCapacityChoice.None, CapacityPreferences.of(rows, "2026-10-26").today)
        val input = CapacityPreferences.of(rows, "2026-10-25").input()
        assertEquals(25, input.todayOverrideMinutes)
        assertNull(input.selectedProfile)
        // Clearing today's choice is a choice too: the newest row says none.
        val cleared = rows + row(CapacityPreferences.TODAY_KEY, "none", day = "2026-10-25")
        assertEquals(TodayCapacityChoice.None, CapacityPreferences.of(cleared, "2026-10-25").today)
    }

    @Test
    fun `a profile chosen for today reaches the planner as that profile`() {
        val p = CapacityPreferences.of(listOf(row(CapacityPreferences.TODAY_KEY, "short", day = "2026-10-25")), "2026-10-25")
        assertEquals(CapacityProfile.SHORT, p.input().selectedProfile)
        assertNull(p.input().todayOverrideMinutes)
    }

    @Test
    fun `a choice is encoded and decoded strictly, and no time budget is outside one day`() {
        listOf(TodayCapacityChoice.None, TodayCapacityChoice.Profile(CapacityProfile.INTENSIVE), TodayCapacityChoice.Minutes(0),
            TodayCapacityChoice.Minutes(1440)).forEach {
            assertEquals(it, CapacityPreferences.decodeToday(CapacityPreferences.encode(it)))
        }
        listOf("", "-5", "1441", "12.5", "SHORT", " 30").forEach { assertNull(CapacityPreferences.decodeToday(it), it) }
        assertFailsWith<IllegalArgumentException> { TodayCapacityChoice.Minutes(1441) }
        assertFailsWith<IllegalArgumentException> { CapacityPreferences(30, -1, 90, TodayCapacityChoice.None) }
    }
}
