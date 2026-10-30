package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/** 18G (`D-141`): the journal's closed kinds and the stored note's shape. */
class PilotJournalFactsTest {

    @Test
    fun `the kinds are closed and read back by id`() {
        assertEquals(listOf("prerequisite_impossible", "time_wrong", "wrong_branch", "personal_preference", "rule_violation"),
            PilotJournalKind.entries.map { it.id })
        PilotJournalKind.entries.forEach { assertEquals(it, PilotJournalKind.of(it.id)) }
        assertNull(PilotJournalKind.of("too_hard"))
    }

    @Test
    fun `a stored note is trimmed and short`() {
        PilotJournalEntry(PilotJournalKind.TIME_WRONG, "x".repeat(PilotJournal.NOTE_LIMIT), 1, "2026-10-28", 1)
        assertFailsWith<IllegalArgumentException> { PilotJournalEntry(PilotJournalKind.TIME_WRONG, "x".repeat(PilotJournal.NOTE_LIMIT + 1), 1, "2026-10-28", 1) }
        assertFailsWith<IllegalArgumentException> { PilotJournalEntry(PilotJournalKind.TIME_WRONG, " x", null, "2026-10-28", 1) }
    }
}
