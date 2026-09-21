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

    /**
     * Appends one truth row and returns its row id. The id is what lets rows written in the same
     * learner action refer to each other — an artifact to its attempt, provenance to its artifact —
     * inside one transaction (11B). Returning it is a refinement of this port, not a new one.
     */
    fun appendTruth(record: TruthRecord): Long

    /**
     * Reads one truth row back by its id, or `null` if there is none. Truth is only ever read, so
     * this adds no mutation path; it exists because a resume has to read the checkpoint it resumes
     * from (11C). Like `curriculumPublished()`, it is a refinement of this port, not a fifth port,
     * and the adapter-owned columns (id, sequence, time) come back as [TruthRecord.recordedAt].
     */
    fun readTruth(kind: String, id: Long): TruthRecord?

    fun readProjection(key: String): ProjectionRecord?

    fun writeProjection(record: ProjectionRecord)

    /**
     * Whether any curriculum version has been published into the immutable curriculum store.
     *
     * Today needs it to tell two truthful situations apart: nothing has been published yet, which is
     * `empty_valid`, and a published curriculum whose plan has not been produced yet, which is
     * `loading_initial_plan` (`THUX-v0`, 11A). This is a refinement of an existing port, not a fifth
     * port, and it stays in core types.
     */
    fun curriculumPublished(): Boolean
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
