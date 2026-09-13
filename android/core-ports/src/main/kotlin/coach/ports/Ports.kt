package coach.ports

import coach.model.EvaluationResult
import coach.model.StudyTimestamp
import coach.model.VersionedRef

/**
 * The four ports core owns (MSBX-v0 §ports). They are expressed in core types only:
 * no platform type, no storage type and no AI type may appear in a signature here.
 */

/**
 * Time is an input, not an ambient fact. If an engine read the system clock directly, timezone
 * logic would become untestable and planner output would stop being a function of its declared
 * inputs — determinism would die quietly and look like flakiness (MSBX-v0 §clock_port).
 */
interface ClockPort {
    fun now(): StudyTimestamp
}

/**
 * Truth is appended, never updated. A correction is an appended disposition, not an edit
 * (LFPS-v0). Projections are rebuildable and carry their own provenance.
 */
interface PersistencePort {
    fun <T> inTransaction(block: () -> T): T

    fun appendTruth(record: TruthRecord)

    fun readProjection(key: String): ProjectionRecord?

    fun writeProjection(record: ProjectionRecord)
}

/** Curriculum content is addressed by logical id and version; no reference is version-free. */
interface ContentPort {
    fun resource(ref: VersionedRef): ContentDocument?
}

/**
 * Open-ended evaluation. A null implementation ships with the product, so the app builds and
 * runs with no adapter present (MSBX-v0 §ai_absence, V1 criterion 8).
 */
interface EvaluatorPort {
    fun evaluate(request: EvaluationRequest): EvaluationResult
}

data class TruthRecord(
    val kind: String,
    val recordedAt: StudyTimestamp,
    val payload: Map<String, String>,
)

/**
 * A rebuildable projection row with the provenance `DDM-v0` requires on every one: which policy
 * produced it, the truth watermark it was computed from, when it was built and against which
 * curriculum version. Without a watermark a stale projection is indistinguishable from a current
 * one.
 */
data class ProjectionRecord(
    val key: String,
    val policyVersion: String,
    val truthWatermark: Long,
    val builtAtInstant: Long,
    val inputCurriculumVersion: Int,
    val payload: Map<String, String>,
)

data class ContentDocument(
    val ref: VersionedRef,
    val body: String,
)

/**
 * AIAX-v0 privacy boundary: only the minimum content needed to evaluate the current attempt
 * may leave the device. Evidence history, mastery state, plan, profile, exposure, provenance
 * and planner traces are absent from this type by construction.
 */
data class EvaluationRequest(
    val objectiveRefs: List<VersionedRef>,
    val promptText: String,
    val learnerResponse: String,
)
