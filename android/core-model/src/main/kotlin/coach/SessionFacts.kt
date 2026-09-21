package coach.model

/**
 * What a durable pause saves, in `TRUX-v0` §7.2's `ResumeContext` fields and nothing more (11C).
 *
 * A checkpoint records **where the work is** — which segments are done, which remain, which of the
 * task's declared checkpoints this is. It records neither how long the work took nor how well it
 * went: there is no elapsed time, attempt count, score or progress fraction here, because time spent
 * is not progress and a pause is not a verdict.
 *
 * These live in `core-model` because the runner (`core-presentation`) decides that a pause is
 * durable and the use case (`core-application`) persists it, and neither may depend on the other.
 */

/**
 * The durable pause classes — and only those. `TRUX-v0`'s third class, `mid_segment_pause`, is
 * transient UI state and has no stored representation at all, so a mid-segment pause cannot be
 * written and then later shown as saved progress.
 *
 * The kind is stored because `TRUX-v0` requires a high-stakes pause to be **marked**: a mark that is
 * not persisted cannot be honoured on resume.
 */
enum class CheckpointKind(val id: String) {
    CHECKPOINT_PAUSE("checkpoint_pause"),
    HIGH_STAKES_PAUSE("high_stakes_pause"),
}

/**
 * `SRR-v0`'s `ResumeContext`, stored in `resume_checkpoint.context`.
 *
 * - [learningNeedKey] and [sourceTaskId] are the planner's identities (12); the checkpoint pins
 *   them but does not interpret them.
 * - [checkpointId] is one of the task's declared `checkpoint_ids[]` (`TASK_TAXONOMY_SPEC` §12) —
 *   a content identity, not the row id.
 * - [artifactStateRef] points at artifact state whose storage belongs to 11D.
 */
data class ResumeContext(
    val kind: CheckpointKind,
    val learningNeedKey: String,
    val sourceTaskId: String,
    val checkpointId: String,
    val completedSegments: List<String>,
    val remainingSegments: List<String>,
    val artifactStateRef: String? = null,
) {
    init {
        listOf(learningNeedKey, sourceTaskId, checkpointId).forEach(::requireToken)
        (completedSegments + remainingSegments).forEach(::requireToken)
        artifactStateRef?.let(::requireToken)
        // A safe checkpoint follows a segment that is meaningful on its own (`TRUX-v0` §7.1), so
        // something is done; and something remains, or this is a finished task, not a paused one.
        require(completedSegments.isNotEmpty()) { "a checkpoint follows at least one completed segment" }
        require(remainingSegments.isNotEmpty()) { "nothing remains, so there is nothing to resume" }
        require(completedSegments.toSet().size == completedSegments.size) { "a completed segment is listed twice" }
        require(remainingSegments.toSet().size == remainingSegments.size) { "a remaining segment is listed twice" }
        require(completedSegments.intersect(remainingSegments.toSet()).isEmpty()) {
            "a segment cannot be both completed and remaining"
        }
    }

    companion object {
        /**
         * Identities are `GNS-v0`-shaped tokens. A value outside this set is refused at construction
         * rather than escaped, so the stored form never needs an escaping rule to be read back.
         */
        private val TOKEN = Regex("""[a-z0-9][a-z0-9_.:@/-]*""")

        private fun requireToken(value: String) =
            require(TOKEN.matches(value)) { "not an identity token: '$value'" }
    }
}

/**
 * The stored form of a [ResumeContext]: versioned, one `key=value` per line, fixed key order.
 *
 * Decoding is **strict**. A missing key, an unknown key, a repeated key, a wrong format version or a
 * value the constructor refuses all decode to `null` — never to a best guess — because a checkpoint
 * that cannot be read back exactly is not "runner state intact" (`TRUX-v0` §7.3, condition 4), and
 * resuming from a guessed context would claim work nobody saved.
 */
object ResumeContextCodec {
    const val FORMAT = "resume_context/1"

    private val KEYS = listOf(
        "kind", "learning_need_key", "source_task_id", "checkpoint_id",
        "completed_segments", "remaining_segments", "artifact_state_ref",
    )

    fun encode(context: ResumeContext): String = buildList {
        add(FORMAT)
        add("kind=${context.kind.id}")
        add("learning_need_key=${context.learningNeedKey}")
        add("source_task_id=${context.sourceTaskId}")
        add("checkpoint_id=${context.checkpointId}")
        add("completed_segments=${context.completedSegments.joinToString(",")}")
        add("remaining_segments=${context.remainingSegments.joinToString(",")}")
        add("artifact_state_ref=${context.artifactStateRef.orEmpty()}")
    }.joinToString("\n")

    fun decode(stored: String): ResumeContext? {
        val lines = stored.split("\n")
        if (lines.firstOrNull() != FORMAT) return null
        val pairs = lines.drop(1).map { line ->
            val at = line.indexOf('=')
            if (at <= 0) return null
            line.substring(0, at) to line.substring(at + 1)
        }
        if (pairs.map { it.first } != KEYS) return null
        val values = pairs.toMap()
        val kind = CheckpointKind.entries.singleOrNull { it.id == values.getValue("kind") } ?: return null
        return runCatching {
            ResumeContext(
                kind = kind,
                learningNeedKey = values.getValue("learning_need_key"),
                sourceTaskId = values.getValue("source_task_id"),
                checkpointId = values.getValue("checkpoint_id"),
                completedSegments = segments(values.getValue("completed_segments")),
                remainingSegments = segments(values.getValue("remaining_segments")),
                artifactStateRef = values.getValue("artifact_state_ref").ifEmpty { null },
            )
        }.getOrNull()
    }

    private fun segments(value: String): List<String> = if (value.isEmpty()) emptyList() else value.split(",")
}

/** A checkpoint as the store holds it: the row id the planner's `resume_context_ref` names, and when. */
data class StoredCheckpoint(
    val checkpointRowId: Long,
    val recordedAt: StudyTimestamp,
    /** `null` when the stored text does not decode exactly — which is not the same as absent. */
    val context: ResumeContext?,
)
