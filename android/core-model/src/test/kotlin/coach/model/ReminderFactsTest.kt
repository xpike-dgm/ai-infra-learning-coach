package coach.model

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** 16E (`RMDX-v0`): the reminder setting, its time of day, and when the next one is due. */
class ReminderFactsTest {

    private val istanbul = ZoneId.of("Europe/Istanbul")
    private val berlin = ZoneId.of("Europe/Berlin")

    private fun at(zone: ZoneId, text: String): Instant = ZonedDateTime.of(java.time.LocalDateTime.parse(text), zone).toInstant()

    @Test
    fun `with no stored row the reminder is off and the time is only the suggestion`() {
        val p = ReminderPreference.of(emptyList())
        assertFalse(p.enabled)
        assertFalse(p.recorded)
        assertEquals(19 * 60, p.minuteOfDay)
    }

    @Test
    fun `the newest readable row is the setting, and a row outside the day is skipped, never guessed`() {
        val rows = listOf(
            ReminderPreferenceRecord(enabled = true, minuteOfDay = 8 * 60, sequence = 3),
            ReminderPreferenceRecord(enabled = false, minuteOfDay = 20 * 60, sequence = 9),
            ReminderPreferenceRecord(enabled = true, minuteOfDay = 1440, sequence = 12),
        )
        val p = ReminderPreference.of(rows)
        assertEquals(ReminderPreference(enabled = false, minuteOfDay = 20 * 60, recorded = true), p)
        assertEquals(true, ReminderPreference.of(rows.take(1)).enabled)
    }

    @Test
    fun `a time of day is read strictly as hours and minutes`() {
        assertEquals(19 * 60, ReminderTime.parse("19:00"))
        assertEquals(9 * 60 + 5, ReminderTime.parse("9:05"))
        assertEquals(0, ReminderTime.parse("00:00"))
        assertEquals(23 * 60 + 59, ReminderTime.parse(" 23:59 "))
        listOf("24:00", "19:60", "1900", "19:0", "-1:00", "", "7 pm").forEach { assertNull(ReminderTime.parse(it), it) }
        assertEquals("09:05", ReminderTime.format(9 * 60 + 5))
        assertFailsWith<IllegalArgumentException> { ReminderTime.format(1440) }
        assertFailsWith<IllegalArgumentException> { ReminderPreference(enabled = true, minuteOfDay = -1) }
    }

    @Test
    fun `the next reminder is today's time while it is still ahead, otherwise tomorrow's, never the moment itself`() {
        val minute = 19 * 60
        assertEquals(at(istanbul, "2026-10-25T19:00"), ReminderSchedule.nextTrigger(at(istanbul, "2026-10-25T08:30"), istanbul, minute))
        assertEquals(at(istanbul, "2026-10-26T19:00"), ReminderSchedule.nextTrigger(at(istanbul, "2026-10-25T19:00"), istanbul, minute))
        assertEquals(at(istanbul, "2026-10-26T19:00"), ReminderSchedule.nextTrigger(at(istanbul, "2026-10-25T21:15"), istanbul, minute))
    }

    @Test
    fun `a time a DST change skips moves forward by the gap, and a time that occurs twice is the first`() {
        // Berlin, 29 March 2026: 02:00 jumps to 03:00, so 02:30 does not exist that day.
        val gap = ReminderSchedule.nextTrigger(at(berlin, "2026-03-29T00:10"), berlin, 2 * 60 + 30)
        assertEquals(at(berlin, "2026-03-29T03:30"), gap)
        // Berlin, 25 October 2026: 03:00 falls back to 02:00, so 02:30 occurs twice; the first is +02:00.
        val overlap = ReminderSchedule.nextTrigger(at(berlin, "2026-10-25T00:10"), berlin, 2 * 60 + 30)
        assertEquals(ZonedDateTime.parse("2026-10-25T02:30+02:00[Europe/Berlin]").toInstant(), overlap)
    }

    @Test
    fun `a reminder belongs to its own local date, and a late one on another date is told apart`() {
        val scheduled = at(istanbul, "2026-10-25T19:00")
        assertTrue(ReminderSchedule.sameDay(scheduled, at(istanbul, "2026-10-25T23:40"), istanbul))
        assertFalse(ReminderSchedule.sameDay(scheduled, at(istanbul, "2026-10-26T00:20"), istanbul))
        assertEquals("2026-10-25", ReminderSchedule.dateOf(scheduled, istanbul).toString())
    }

    @Test
    fun `a reminder names at least one task and never negative minutes`() {
        assertFailsWith<IllegalArgumentException> { ReminderPlanContent(0, 10, false, false, false) }
        assertFailsWith<IllegalArgumentException> { ReminderPlanContent(1, -1, false, false, false) }
    }
}
