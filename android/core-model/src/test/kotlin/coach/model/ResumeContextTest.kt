package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/** What a checkpoint saves, and that it is read back exactly or not at all (11C). */
class ResumeContextTest {

    private fun context(
        kind: CheckpointKind = CheckpointKind.CHECKPOINT_PAUSE,
        completed: List<String> = listOf("seg.read_example"),
        remaining: List<String> = listOf("seg.write_loop", "seg.explain"),
        artifact: String? = null,
    ) = ResumeContext(
        kind = kind,
        learningNeedKey = "need.skill.python.loops@v1",
        sourceTaskId = "42",
        checkpointId = "checkpoint.after_example",
        completedSegments = completed,
        remainingSegments = remaining,
        artifactStateRef = artifact,
    )

    @Test
    fun `the durable kinds are exactly the two durable pause classes`() {
        // mid_segment_pause has no stored representation at all.
        assertEquals(listOf("checkpoint_pause", "high_stakes_pause"), CheckpointKind.entries.map { it.id })
    }

    @Test
    fun `a context round-trips exactly`() {
        listOf(context(), context(kind = CheckpointKind.HIGH_STAKES_PAUSE, artifact = "artifact://draft/7")).forEach {
            assertEquals(it, ResumeContextCodec.decode(ResumeContextCodec.encode(it)))
        }
    }

    @Test
    fun `the stored form has a fixed order and carries no time score or count`() {
        val keys = ResumeContextCodec.encode(context()).lines().drop(1).map { it.substringBefore('=') }
        assertEquals(
            listOf("kind", "learning_need_key", "source_task_id", "checkpoint_id",
                "completed_segments", "remaining_segments", "artifact_state_ref"),
            keys,
        )
    }

    @Test
    fun `a checkpoint follows completed work and leaves work remaining`() {
        assertFailsWith<IllegalArgumentException> { context(completed = emptyList()) }
        assertFailsWith<IllegalArgumentException> { context(remaining = emptyList()) }
        assertFailsWith<IllegalArgumentException> { context(completed = listOf("a"), remaining = listOf("a")) }
        assertFailsWith<IllegalArgumentException> { context(completed = listOf("a", "a")) }
    }

    @Test
    fun `a value that would need escaping is refused rather than escaped`() {
        assertFailsWith<IllegalArgumentException> { context(completed = listOf("a,b")) }
        assertFailsWith<IllegalArgumentException> { context(completed = listOf("a\nkind=high_stakes_pause")) }
        assertFailsWith<IllegalArgumentException> { context(artifact = "") }
    }

    @Test
    fun `anything that does not decode exactly decodes to nothing rather than a guess`() {
        val good = ResumeContextCodec.encode(context())
        val lines = good.lines()
        listOf(
            "",
            "{}",
            good.replace(ResumeContextCodec.FORMAT, "resume_context/2"),
            lines.filterNot { it.startsWith("checkpoint_id=") }.joinToString("\n"),
            (lines + "elapsed_ms=90000").joinToString("\n"),
            (lines + "kind=checkpoint_pause").joinToString("\n"),
            listOf(lines[0], lines[2], lines[1]).plus(lines.drop(3)).joinToString("\n"),
            good.replace("kind=checkpoint_pause", "kind=mid_segment_pause"),
            good.replace("remaining_segments=seg.write_loop,seg.explain", "remaining_segments="),
        ).forEach { assertNull(ResumeContextCodec.decode(it), "decoded: $it") }
    }
}
