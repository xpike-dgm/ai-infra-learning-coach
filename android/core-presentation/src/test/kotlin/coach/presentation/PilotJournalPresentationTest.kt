package coach.presentation

import coach.model.PilotJournal
import coach.model.PilotJournalKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 18G (`D-141`): the journal names every kind in SC-049's own words, saves only with a kind chosen and a note that fits,
 * and its copy says that reporting changes no plan.
 */
class PilotJournalPresentationTest {

    @Test
    fun `every kind has its own words, and SC-049's three structural kinds come first`() {
        assertEquals(PilotJournalKind.entries.toSet(), PilotJournalCopy.kindLabels.keys)
        assertEquals(PilotJournalKind.entries.size, PilotJournalCopy.kindLabels.values.toSet().size, "no two kinds read alike")
        assertEquals(listOf(true, true, true, false, false), PilotJournalPresentation.kinds.map { it.structural })
        assertEquals(listOf(PilotJournalKind.RULE_VIOLATION), PilotJournalKind.entries.filter { it.ruleViolation })
        assertTrue(PilotJournalKind.entries.none { it.structural && it.ruleViolation })
    }

    @Test
    fun `a kind must be chosen, the note is optional and must fit once trimmed`() {
        assertFalse(PilotJournalPresentation.canSave(null, "not"))
        assertTrue(PilotJournalPresentation.canSave(PilotJournalKind.TIME_WRONG, ""))
        assertEquals("not", PilotJournalPresentation.note("  not  "))
        assertTrue(PilotJournalPresentation.canSave(PilotJournalKind.TIME_WRONG, " " + "x".repeat(PilotJournal.NOTE_LIMIT) + " "))
        assertNull(PilotJournalPresentation.note("x".repeat(PilotJournal.NOTE_LIMIT + 1)))
        assertFalse(PilotJournalPresentation.canSave(PilotJournalKind.TIME_WRONG, "x".repeat(PilotJournal.NOTE_LIMIT + 1)))
    }

    @Test
    fun `the copy says reporting changes nothing and a preference is not counted`() {
        assertTrue("planı değiştirmez" in PilotJournalCopy.NOTE)
        assertTrue("Plan değiştirilmedi" in PilotJournalCopy.SAVED)
        assertTrue("sayılmaz" in PilotJournalCopy.PREFERENCE_NOTE)
    }
}
