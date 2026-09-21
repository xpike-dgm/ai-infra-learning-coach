package coach.presentation

import coach.model.DayChange
import coach.model.DayInventory
import coach.model.DayRecord
import coach.model.DayRecordKind
import coach.model.EvaluatorAvailability
import coach.model.HistoryEventFamily
import coach.model.RecoveryReason
import coach.model.StoreStatus
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** What the end of a day may say, and what it has no way to say (11E). */
class EndOfDayTest {

    private val healthy = AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.AVAILABLE)

    private fun input(
        counts: Map<DayRecordKind, Int> = emptyMap(),
        changes: List<DayChange> = emptyList(),
        health: AppHealth = healthy,
        loading: Boolean = false,
        unread: Set<String> = emptySet(),
    ) = DaySummaryInput(
        record = DayRecord("2026-09-21", DayInventory(counts), changes),
        health = health,
        loading = loading,
        unreadKinds = unread,
    )

    @Test
    fun `the states are SPWX-v0's in its order`() {
        assertEquals(
            listOf("loading_projection", "recomputing_projection", "ready_with_evidence", "empty_no_evidence_yet",
                "empty_no_attention_needed", "partial_projection_available", "offline_local_capable",
                "ai_unavailable_full_state_available", "error_recoverable", "data_recovery_required"),
            DaySummaryState.entries.map { it.id },
        )
    }

    @Test
    fun `only the two system conditions wear the fault tone, and an empty day is neutral`() {
        assertEquals(
            setOf(DaySummaryState.ERROR_RECOVERABLE, DaySummaryState.DATA_RECOVERY_REQUIRED),
            DaySummaryState.entries.filter { it.tone == Tone.SYSTEM_FAULT }.toSet(),
        )
        assertEquals(Tone.NEUTRAL, DaySummaryState.EMPTY_NO_EVIDENCE_YET.tone)
        assertEquals(Tone.PENDING_UNRESOLVED, DaySummaryState.PARTIAL_PROJECTION_AVAILABLE.tone)
    }

    @Test
    fun `a day that recorded something is ready, and its counts are inventory`() {
        val view = EndOfDay.of(input(counts = mapOf(DayRecordKind.ATTEMPTS_RECORDED to 2, DayRecordKind.ITEMS_SEEN to 3)))
        assertEquals(DaySummaryState.READY_WITH_EVIDENCE, view.state)
        assertTrue(view.countsAreLabelledInventory)
        assertEquals(2, view.inventory.countOf(DayRecordKind.ATTEMPTS_RECORDED))
        assertFalse(view.isFailure)
    }

    @Test
    fun `a day with nothing recorded is empty and never a failure`() {
        val view = EndOfDay.of(input())
        assertEquals(DaySummaryState.EMPTY_NO_EVIDENCE_YET, view.state)
        assertEquals(Tone.NEUTRAL, view.state.tone)
        assertFalse(view.isFailure)
        assertTrue(DayCopy.NOTHING_RECORDED.contains("başarısızlık değil"))
        assertTrue(DayCopy.NOTHING_RECORDED.contains("borç"))
    }

    @Test
    fun `activity alone changes nothing, and the summary says so`() {
        val view = EndOfDay.of(input(counts = mapOf(DayRecordKind.ATTEMPTS_RECORDED to 5)))
        assertTrue(view.changes.isEmpty(), "a change was claimed from activity alone")
        assertTrue(DayCopy.NOTHING_CHANGED.contains("iddia"))
    }

    @Test
    fun `a change appears only when a canonical engine reported one`() {
        val reported = DayChange(HistoryEventFamily.ASSESSMENT, "verification opened", VersionedRef("skill.python.loops", 1))
        val view = EndOfDay.of(input(counts = mapOf(DayRecordKind.ATTEMPTS_RECORDED to 1), changes = listOf(reported)))
        assertEquals(listOf(reported), view.changes)
    }

    @Test
    fun `a count that could not be read is named, never treated as zero`() {
        val view = EndOfDay.of(input(counts = mapOf(DayRecordKind.ATTEMPTS_RECORDED to 1), unread = setOf("items_seen")))
        assertEquals(DaySummaryState.PARTIAL_PROJECTION_AVAILABLE, view.state)
        assertEquals(setOf("items_seen"), view.unreadKinds)
        assertTrue(DayCopy.PARTIAL.contains("sıfır sayılmadı"))
    }

    @Test
    fun `a store that cannot be trusted supersedes the day summary`() {
        val recovery = AppHealth.of(
            StoreStatus.RecoveryRequired(RecoveryReason.INTEGRITY_CHECK_FAILED), EvaluatorAvailability.AVAILABLE,
        )
        assertEquals(DaySummaryState.DATA_RECOVERY_REQUIRED, EndOfDay.of(input(health = recovery)).state)

        val failure = AppHealth.of(StoreStatus.RecoverableFailure, EvaluatorAvailability.AVAILABLE)
        assertEquals(DaySummaryState.ERROR_RECOVERABLE, EndOfDay.of(input(health = failure)).state)

        val opening = AppHealth.of(StoreStatus.Opening, EvaluatorAvailability.AVAILABLE)
        assertEquals(DaySummaryState.LOADING_PROJECTION, EndOfDay.of(input(health = opening)).state)
    }

    @Test
    fun `while loading, no count is presented as the day's inventory`() {
        val view = EndOfDay.of(input(counts = mapOf(DayRecordKind.ATTEMPTS_RECORDED to 9), loading = true))
        assertEquals(DaySummaryState.LOADING_PROJECTION, view.state)
        assertFalse(view.inventory.recordedAnything)
    }

    @Test
    fun `the AI being away is context, not a fault, and the day still reads`() {
        val degraded = AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.UNAVAILABLE)
        val view = EndOfDay.of(input(counts = mapOf(DayRecordKind.ATTEMPTS_RECORDED to 1), health = degraded))
        assertEquals(DaySummaryState.READY_WITH_EVIDENCE, view.state)
        assertTrue(CrossCuttingState.AI_UNAVAILABLE_CORE_AVAILABLE in view.contexts)
    }

    @Test
    fun `the view can hold no streak, percentage, score, minutes or debt`() {
        val forbidden = listOf("streak", "percent", "score", "grade", "minutes", "elapsed", "goal", "debt",
            "carried", "rank", "consecutive")
        val fields = DaySummaryView::class.java.declaredFields.map { it.name } +
            DaySummaryInput::class.java.declaredFields.map { it.name }
        forbidden.forEach { word ->
            assertTrue(fields.none { it.contains(word, ignoreCase = true) }, "a day field names '$word': $fields")
        }
    }

    @Test
    fun `tomorrow inherits no debt, and the copy says it plainly`() {
        assertTrue(DayCopy.TOMORROW.contains("borç yok"))
        assertTrue(DayCopy.RECORDED_IS_NOT_PROGRESS.contains("ilerleme ölçüsü değil"))
    }
}
