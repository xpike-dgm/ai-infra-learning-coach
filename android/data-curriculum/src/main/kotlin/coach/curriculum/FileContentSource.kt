package coach.curriculum

import coach.model.AssessmentItem
import coach.model.CurriculumPackage
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
     * `curriculum_package/1` has no task section: authored tasks, their purposes and durations are
     * content Stage 15 writes. Until a format carries them, no task serves any need, and saying so is
     * the truthful answer — the planner then records the need as having no valid candidate.
     */
    override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = emptyList()
}
