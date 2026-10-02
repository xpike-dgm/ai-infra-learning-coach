package coach.curriculum

import coach.model.AssessmentItem
import coach.model.CodeTestSuite
import coach.model.ComprehensionCheck
import coach.model.AcceptedAnswers
import coach.model.Rubric
import coach.model.CurriculumPackage
import coach.model.ExplanationVariant
import coach.model.LearningNeed
import coach.model.TaskCandidate
import coach.model.VersionedRef
import coach.ports.ContentDocument
import coach.ports.ContentPort

/**
 * Implements ContentPort. Curriculum is versioned separately from user state and a published
 * version is never overwritten (LFPS-v0), so lookups are always by logical id and version.
 *
 * The authored package is supplied as text by the composition root — the platform knows where files
 * live, core does not — and parsed once, strictly ([PackageFormat]). **When no package ships, every
 * lookup answers `null`**, which is the truthful "no such resource" rather than a crash; that was
 * the defect 11A found here, and the answer has not changed now that a package can exist.
 *
 * A package that does not parse is not half-loaded: the failure is kept and every lookup answers
 * `null`, so the app behaves exactly as it does with no content rather than serving fragments.
 */
class FileContentSource(private val source: () -> String? = { null }) : ContentPort {

    private val parsed: PackageFormat.Parsed? by lazy {
        val text = source() ?: return@lazy null
        runCatching { PackageFormat.parse(text) }.onFailure { failure = it }.getOrNull()
    }

    /** Why the authored package could not be read, if it could not. Never a guess about its content. */
    var failure: Throwable? = null
        private set

    override fun resource(ref: VersionedRef): ContentDocument? =
        parsed?.documents?.get(ref)?.let { ContentDocument(ref, it) }

    override fun assessmentItem(ref: VersionedRef): AssessmentItem? = parsed?.items?.get(ref)

    override fun curriculumPackage(): CurriculumPackage? = parsed?.curriculum

    /**
     * The authored tasks that serve [need] (15A): those declaring its trigger and working on its Skill, in a stable
     * order. Content never opens a need and never widens one; with no package, or no task for the need, the answer is
     * still the truthful empty list and the planner records the need as having no valid candidate.
     */
    override fun taskCandidates(need: LearningNeed): List<TaskCandidate> =
        parsed?.tasks.orEmpty().mapNotNull { it.candidateFor(need) }.sortedBy { it.id }

    /** The authored items naming [skill] among their targets, in a stable order (13A). */
    override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> =
        parsed?.items?.values.orEmpty().filter { skill in it.targetSkills }.sortedBy { it.ref.toString() }

    /** The written explanations of [objective], pinned by version, in a stable order (14C). */
    override fun explanationsFor(objective: VersionedRef): List<ExplanationVariant> =
        parsed?.explanations.orEmpty().filter { it.objective == objective }.sortedBy { it.ref.toString() }

    /** The tests for exactly this item version (14D); another version's tests do not test it. */
    override fun codeTestsFor(item: VersionedRef): CodeTestSuite? = parsed?.codeTests.orEmpty().singleOrNull { it.item == item }

    /** The checks written for exactly this item version (14E), in a stable order. */
    override fun comprehensionChecksFor(item: VersionedRef): List<ComprehensionCheck> =
        parsed?.comprehensionChecks.orEmpty().filter { it.item == item }.sortedBy { it.ref.toString() }

    /** The accepted answers for exactly this item version (14F). */
    override fun answerKeyFor(item: VersionedRef): AcceptedAnswers? = parsed?.answerKeys.orEmpty().singleOrNull { it.item == item }

    /** The rubric for exactly this item version (14F). */
    override fun rubricFor(item: VersionedRef): Rubric? = parsed?.rubrics.orEmpty().singleOrNull { it.item == item }
}
