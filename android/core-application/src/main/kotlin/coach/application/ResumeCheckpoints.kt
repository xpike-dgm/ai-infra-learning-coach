package coach.application

import coach.model.ResumeContext
import coach.model.ResumeContextCodec
import coach.model.StoredCheckpoint
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * Durable pauses: what a checkpoint pause actually saves, and reading it back for a resume (11C).
 *
 * A pause is one learner action, so it is one transaction and one `resume_checkpoint` row — the
 * `ResumeContext` and nothing else. It writes no attempt, no evidence and no projection: pausing is
 * not an outcome (`TRUX-v0` §7, `SRR-v0`).
 *
 * The row is append-only like every truth row. A later pause of the same task appends a new row;
 * the earlier one stays as history. Which checkpoint a resume refers to is the planner's
 * `resume_context_ref` (12) — there is no "consumed" or "latest" flag here, because that would be
 * either an UPDATE of truth or a stored copy of something derivable.
 */
class ResumeCheckpoints(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    /**
     * Records a durable pause and returns the checkpoint's row id. Only a [ResumeContext] can be
     * recorded, and it can only be of a durable kind, so a mid-segment pause has no way in.
     */
    fun record(context: ResumeContext): Long {
        val at = clock.now()
        return persistence.inTransaction {
            persistence.appendTruth(
                TruthRecord(KIND, at, mapOf("context" to ResumeContextCodec.encode(context)))
            )
        }
    }

    /**
     * Reads a checkpoint back. A row whose text does not decode exactly is returned with a `null`
     * context rather than dropped, so "this checkpoint cannot be resumed as-is" stays distinguishable
     * from "there is no such checkpoint".
     */
    fun read(checkpointRowId: Long): StoredCheckpoint? {
        val row = persistence.readTruth(KIND, checkpointRowId) ?: return null
        return StoredCheckpoint(
            checkpointRowId = checkpointRowId,
            recordedAt = row.recordedAt,
            context = row.payload["context"]?.let(ResumeContextCodec::decode),
        )
    }

    companion object {
        const val KIND = "resume_checkpoint"
    }
}
