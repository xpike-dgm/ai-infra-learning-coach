package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** What a day may hold, and what crosses its boundary — which is nothing (11E). */
class DayFactsTest {

    private fun record(
        day: String = "2026-09-21",
        counts: Map<DayRecordKind, Int> = mapOf(DayRecordKind.ATTEMPTS_RECORDED to 2),
        changes: List<DayChange> = emptyList(),
    ) = DayRecord(day, DayInventory(counts), changes)

    @Test
    fun `what can be counted is tied to the truth table it is counted from`() {
        assertEquals(
            listOf("attempts_recorded", "items_seen", "checkpoints_saved", "evidence_interpreted"),
            DayRecordKind.entries.map { it.id },
        )
        assertEquals(
            listOf("attempt", "exposure_record", "resume_checkpoint", "evidence_event"),
            DayRecordKind.entries.map { it.truthTable },
        )
    }

    @Test
    fun `history event families are SPWX-v0's`() {
        assertEquals(listOf("learning", "assessment", "review", "remediation"), HistoryEventFamily.entries.map { it.id })
    }

    @Test
    fun `an inventory counts and says it is an inventory, and carries no ratio`() {
        val inventory = DayInventory(mapOf(DayRecordKind.ATTEMPTS_RECORDED to 3, DayRecordKind.ITEMS_SEEN to 5))
        assertTrue(inventory.isLabelledInventory)
        assertEquals(3, inventory.countOf(DayRecordKind.ATTEMPTS_RECORDED))
        assertEquals(0, inventory.countOf(DayRecordKind.EVIDENCE_INTERPRETED))
        assertTrue(inventory.recordedAnything)

        val forbidden = listOf("percent", "ratio", "total", "score", "streak", "minutes", "goal", "target")
        val fields = DayInventory::class.java.declaredFields.map { it.name } +
            DayRecord::class.java.declaredFields.map { it.name }
        forbidden.forEach { word ->
            assertTrue(fields.none { it.contains(word, ignoreCase = true) }, "a day field names '$word': $fields")
        }
    }

    @Test
    fun `a day with nothing recorded is empty, not negative`() {
        val empty = DayInventory(mapOf(DayRecordKind.ATTEMPTS_RECORDED to 0))
        assertFalse(empty.recordedAnything)
        assertFailsWith<IllegalArgumentException> { DayInventory(mapOf(DayRecordKind.ATTEMPTS_RECORDED to -1)) }
    }

    @Test
    fun `a day is a study day, and a row belongs to the day it recorded`() {
        assertFailsWith<IllegalArgumentException> { record(day = "21-09-2026") }
        assertTrue(DayBoundary.belongsTo("2026-09-21", "2026-09-21"))
        assertFalse(DayBoundary.belongsTo("2026-09-20", "2026-09-21"))
    }

    @Test
    fun `nothing crosses the day boundary`() {
        val busy = record(counts = mapOf(DayRecordKind.ATTEMPTS_RECORDED to 4, DayRecordKind.ITEMS_SEEN to 9))
        val next = DayBoundary.rollOver(busy, "2026-09-22")
        assertEquals("2026-09-22", next.studyDay)
        assertFalse(next.inventory.recordedAnything, "yesterday's work was carried into today")
        assertTrue(next.changes.isEmpty())
        assertEquals(null, next.openNeedCount)
        assertFailsWith<IllegalArgumentException> { DayBoundary.rollOver(busy, "2026-09-20") }
    }

    @Test
    fun `history shows the days that recorded something and never fills the gaps`() {
        val days = listOf(
            record("2026-09-15", counts = mapOf(DayRecordKind.ATTEMPTS_RECORDED to 1)),
            record("2026-09-16", counts = emptyMap()),
            record("2026-09-17", counts = mapOf(DayRecordKind.ATTEMPTS_RECORDED to 0)),
            record("2026-09-21", counts = emptyMap(), changes = listOf(
                DayChange(HistoryEventFamily.REVIEW, "retention revalidated", VersionedRef("skill.python.loops", 1))
            )),
        )
        val entries = DayHistory.entries(days)
        assertEquals(listOf("2026-09-21", "2026-09-15"), entries.map { it.studyDay })
        // Five silent days between them produce no entries at all: a gap is not an obligation.
        assertEquals(2, entries.size)
    }

    @Test
    fun `a recorded change says what changed and for which subject`() {
        assertFailsWith<IllegalArgumentException> {
            DayChange(HistoryEventFamily.LEARNING, "  ", null)
        }
    }
}
