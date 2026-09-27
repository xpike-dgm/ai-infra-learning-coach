package coach.ports

import coach.model.AssessmentItem
import coach.model.CurriculumPackage
import coach.model.EvaluationResult
import coach.model.EvidenceRow
import coach.model.LearningNeed
import coach.model.ObjectiveEvidenceProfile
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StudyTimestamp
import coach.model.TaskCandidate
import coach.model.ValidationRecord
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

    /**
     * Publishes one curriculum version into the immutable curriculum region, in one transaction
     * (11D). A version that is already published is **not** overwritten: the outcome says so, and
     * a correction is a new version (`LFPS-v0`). Publishing writes nothing in the user regions.
     */
    fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome

    /** The `DDM-v0`-named row of a published assessment resource version, or `null` if unpublished. */
    fun resourceVersion(ref: VersionedRef): ResourceVersion?

    /**
     * The most recent validation record for a resource version (`AIV-v0` §31). Trust is the store's
     * answer, not the item document's own claim about itself.
     */
    fun latestValidation(ref: VersionedRef): ValidationRecord?

    /** What the published Objective accepts as evidence; the Objective decides, not the item. */
    fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile?

    /**
     * How many rows of [kind] were recorded on [studyDay] (11E).
     *
     * The day is the learner-local study day each row carries, never a range over instants:
     * recomputing a row's day from its instant is how a DST change or a flight silently moves work
     * from one day into another. A truth table with no study day of its own cannot be counted here.
     */
    fun countTruth(kind: String, studyDay: String): Int

    /**
     * Every evidence row recorded for one pinned Objective, oldest first (12A).
     *
     * Mastery is a **projection of evidence**, so the engine has to be able to read all of it and
     * recompute from scratch; a state that could only be updated incrementally would become a second
     * source of truth the moment one update was missed.
     */
    fun evidenceFor(objective: VersionedRef): List<EvidenceRow>

    /**
     * The global truth sequence a projection was computed from (`DDM-v0` §projection_provenance).
     * Without it a stale projection is indistinguishable from a current one.
     */
    fun truthWatermark(): Long

    /** The newest published curriculum version, or `null` when nothing has been published. */
    fun latestCurriculumVersion(): Int?

    /**
     * One published Skill version, or `null` if it was never published (12B). The prerequisite gate
     * needs `critical_prerequisite` from it, and treats an unpublished Skill as a metadata problem
     * rather than as a Skill nobody has learned yet.
     */
    fun skill(ref: VersionedRef): SkillRow?

    /**
     * Every version of every prerequisite edge into one pinned target Skill (12B), in any lifecycle.
     * Which version is in force and which lifecycles gate is the engine's decision, not the store's:
     * a store that filtered `draft` edges out would make a missing hard prerequisite invisible.
     */
    fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge>

    /**
     * The newest published version of every Skill, in a stable order (12C). Needs are opened from
     * current Skill state, so the planner has to know which Skills exist; lifecycle is returned rather
     * than filtered, because which lifecycles are on the route is the planner's decision.
     */
    fun publishedSkills(): List<SkillRow>
}

/** Curriculum content is addressed by logical id and version; no reference is version-free. */
interface ContentPort {
    fun resource(ref: VersionedRef): ContentDocument?

    /**
     * The authored assessment item behind a pinned reference (11D). The metadata `DDM-v0` does not
     * name — the item's targets, use ceiling, scope eligibility, evaluator requirement, difficulty
     * and independence mode — is authored **content**, so it is parsed by the content adapter
     * rather than stored in invented columns.
     */
    fun assessmentItem(ref: VersionedRef): AssessmentItem?

    /** The authored curriculum package awaiting ingestion, or `null` when none ships with the app. */
    fun curriculumPackage(): CurriculumPackage?

    /**
     * The authored tasks that could serve one open need (12C). Which purpose serves which need, and
     * how long a task takes, is authored content (15), so the content side answers and the planner never
     * invents a task. An empty list is a truthful answer: nothing authored serves this need yet.
     */
    fun taskCandidates(need: LearningNeed): List<TaskCandidate>
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
