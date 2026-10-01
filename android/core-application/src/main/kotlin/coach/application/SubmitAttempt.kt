package coach.application

import coach.model.AttemptRecorded
import coach.model.AttemptSubmission
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.TruthRecord

/**
 * Records a frozen attempt as **one learner action, one transaction** (`LFPS-v0` §9, 11B).
 *
 * Attempt, artifact, provenance and every assistance event commit together or not at all, because
 * an attempt without its assistance metadata or its provenance is exactly the half-record `LFPS-v0`
 * forbids: it would later read as unassisted, independent work.
 *
 * **It writes no evidence, and that is the contract rather than a gap.** The runner is not the
 * evidence evaluator (`TRUX-v0`); what an attempt proves is decided by the evidence pipeline (12),
 * and while the evaluator is unavailable an attempt is `evaluation_pending` — recorded, neither
 * passed nor failed. Nothing in this class can reach the evidence tables.
 */
class SubmitAttempt(
    private val persistence: PersistencePort,
    private val clock: ClockPort,
) {
    fun submit(submission: AttemptSubmission): AttemptRecorded {
        // One timestamp for the whole action, so its rows cannot disagree about when it happened.
        val at = clock.now()
        return persistence.inTransaction {
            val attemptId = persistence.appendTruth(
                TruthRecord(
                    "attempt", at,
                    buildMap {
                        put("resource_logical_id", submission.resource.logicalId)
                        put("resource_version", submission.resource.version.toString())
                        // An attempt made inside an assessment session names it (13A); others name none.
                        submission.assessmentSessionId?.let { put("assessment_session_id", it.toString()) }
                    },
                )
            )
            val artifactId = persistence.appendTruth(
                TruthRecord(
                    "artifact", at,
                    mapOf("attempt_id" to attemptId.toString(), "content_ref" to submission.artifactContentRef),
                )
            )
            // Asked, not inferred: the value is the learner's own answer, unknown included.
            persistence.appendTruth(
                TruthRecord(
                    "artifact_provenance", at,
                    mapOf("artifact_id" to artifactId.toString(), "origin" to submission.provenance.id),
                )
            )
            val assistanceIds = submission.assistance.map { event ->
                persistence.appendTruth(
                    TruthRecord(
                        "assistance_event", at,
                        mapOf(
                            "attempt_id" to attemptId.toString(),
                            "level" to event.level.id,
                            "timing" to event.timing.id,
                            "target_scope" to event.scope.id,
                            "source" to event.source.id,
                            "requested_by_user" to if (event.requestedByUser) "1" else "0",
                        ),
                    )
                )
            }
            AttemptRecorded(attemptId, artifactId, assistanceIds)
        }
    }
}
