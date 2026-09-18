package coach.application

import coach.model.CheckpointKind
import coach.model.ResumeContext
import coach.model.ResumeContextCodec
import coach.model.StudyTimestamp
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * The shape of a durable pause: one transaction, one checkpoint row, nothing else. Real SQLite
 * round-trips and rollback are proven in T2.
 */
class ResumeCheckpointsTest {

    private class RecordingStore : PersistencePort {
        val appended = mutableListOf<TruthRecord>()
        var transactions = 0
        var inside = false
        override fun <T> inTransaction(block: () -> T): T {
            transactions += 1
            inside = true
            try { return block() } finally { inside = false }
        }
        override fun appendTruth(record: TruthRecord): Long {
            check(inside) { "${record.kind} was written outside the pause's transaction" }
            appended += record
            return appended.size.toLong()
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? =
            appended.getOrNull((id - 1).toInt())?.takeIf { it.kind == kind }
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) = error("a pause writes no projection")
        override fun curriculumPublished(): Boolean = true
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, "2026-09-19", 3 * 3600)
    }

    private val context = ResumeContext(
        kind = CheckpointKind.CHECKPOINT_PAUSE,
        learningNeedKey = "need.skill.python.loops@v1",
        sourceTaskId = "42",
        checkpointId = "checkpoint.after_example",
        completedSegments = listOf("seg.read_example"),
        remainingSegments = listOf("seg.write_loop"),
    )

    @Test
    fun `a pause is one transaction writing one checkpoint row and nothing else`() {
        val store = RecordingStore()
        ResumeCheckpoints(store, clock).record(context)
        assertEquals(1, store.transactions)
        assertEquals(listOf("resume_checkpoint"), store.appended.map { it.kind })
        assertEquals(setOf("context"), store.appended.single().payload.keys)
    }

    @Test
    fun `the row holds the encoded context and the action's single timestamp`() {
        val store = RecordingStore()
        ResumeCheckpoints(store, clock).record(context)
        val row = store.appended.single()
        assertEquals(ResumeContextCodec.encode(context), row.payload.getValue("context"))
        assertEquals(clock.now(), row.recordedAt)
    }

    @Test
    fun `a recorded checkpoint reads back as the context that was saved`() {
        val store = RecordingStore()
        val checkpoints = ResumeCheckpoints(store, clock)
        val id = checkpoints.record(context)
        val stored = assertNotNull(checkpoints.read(id))
        assertEquals(id, stored.checkpointRowId)
        assertEquals(context, stored.context)
        assertEquals(clock.now(), stored.recordedAt)
    }

    @Test
    fun `an unreadable checkpoint is kept distinct from a missing one`() {
        val store = RecordingStore()
        store.inTransaction { store.appendTruth(TruthRecord("resume_checkpoint", clock.now(), mapOf("context" to "{}"))) }
        val stored = assertNotNull(ResumeCheckpoints(store, clock).read(1))
        assertNull(stored.context)
        assertNull(ResumeCheckpoints(store, clock).read(99))
    }

    @Test
    fun `a second pause of the same task appends rather than replaces`() {
        val store = RecordingStore()
        val checkpoints = ResumeCheckpoints(store, clock)
        val first = checkpoints.record(context)
        val second = checkpoints.record(context.copy(completedSegments = listOf("seg.read_example", "seg.write_loop"),
            remainingSegments = listOf("seg.explain")))
        assertEquals(2, store.appended.size)
        assertEquals(context, checkpoints.read(first)?.context)
        assertEquals(listOf("seg.explain"), checkpoints.read(second)?.context?.remainingSegments)
    }
}
