package coach.curriculum

import coach.model.AssessmentItem
import coach.model.AuthoredTask
import coach.model.CodeTestSuite
import coach.model.ComprehensionCheck
import coach.model.AcceptedAnswers
import coach.model.Rubric
import coach.model.CurriculumPackage
import coach.model.ExplanationVariant
import coach.model.LearningNeed
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.VersionedRef
import coach.model.WrongAnswerMisconception
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
 *
 * 15B (`D-113`): each step of Stage 15 ships its own package — [source] is the first, [later] the rest in order. They are
 * read together and served as one body of content; versions must rise strictly and no item, explanation, task, test
 * suite, key or rubric may appear in two packages. If any one of them does not read, none is served: a later package
 * builds on the earlier ones, and half of a course is not a course.
 *
 * 15G (`D-120`): a later package may add to what an earlier one published without changing it — a new **version** of a
 * task (only the highest version of each task is offered; the earlier stays readable by its pinned reference) and wrong
 * answers mapped to misconceptions for an earlier package's keys. Each package is read with the ones before it, so such
 * a reference is checked before anything is served.
 */
class FileContentSource(
    private val later: () -> List<String> = { emptyList() },
    /** 16G: a shipped suite's text by its asset name (`<item logical id>-v<N>.json`), or `null`. */
    private val codeSuites: (String) -> String? = { null },
    /** 16G: the shipped PC runner's text, or `null`. */
    private val codeRunner: () -> String? = { null },
    // Last, so `FileContentSource { text }` still means "this one package".
    private val source: () -> String? = { null },
) : ContentPort {

    private val loaded: List<PackageFormat.Parsed>? by lazy {
        val first = source() ?: return@lazy null
        runCatching {
            (listOf(first) + later()).fold(emptyList<PackageFormat.Parsed>()) { read, text -> read + PackageFormat.parse(text, read) }
                .also(::checkSequence)
        }.onFailure { failure = it }.getOrNull()
    }

    private val parsed: PackageFormat.Parsed? by lazy { loaded?.let(::merge) }

    /** Why the authored package could not be read, if it could not. Never a guess about its content. */
    var failure: Throwable? = null
        private set

    override fun resource(ref: VersionedRef): ContentDocument? =
        parsed?.documents?.get(ref)?.let { ContentDocument(ref, it) }

    override fun assessmentItem(ref: VersionedRef): AssessmentItem? = parsed?.items?.get(ref)

    /** The first package (11D); [curriculumPackages] is every one, oldest first. */
    override fun curriculumPackage(): CurriculumPackage? = loaded?.first()?.curriculum

    override fun curriculumPackages(): List<CurriculumPackage> = loaded.orEmpty().map { it.curriculum }

    /**
     * The authored tasks that serve [need] (15A): those declaring its trigger and working on its Skill, in a stable
     * order. Content never opens a need and never widens one; with no package, or no task for the need, the answer is
     * still the truthful empty list and the planner records the need as having no valid candidate.
     */
    override fun taskCandidates(need: LearningNeed): List<TaskCandidate> =
        parsed?.tasks.orEmpty().groupBy { it.ref.logicalId }.values.map { versions -> versions.maxBy { it.ref.version } }
            .mapNotNull { it.candidateFor(need) }.sortedBy { it.id }

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

    /** The items of every teach task targeting exactly this Objective version, every task version included (16C). */
    override fun authoredTask(ref: VersionedRef): AuthoredTask? = parsed?.tasks.orEmpty().singleOrNull { it.ref == ref }

    /**
     * The shipped suite for exactly this item version (16G), only when the text names this item version **and** the suite
     * version the package declares for it — a suite for another version would run tests the course does not judge by.
     */
    override fun codeSuiteText(item: VersionedRef): String? {
        val suite = codeTestsFor(item) ?: return null
        val text = codeSuites("${item.logicalId}-v${item.version}.json") ?: return null
        fun field(key: String) = Regex(""""$key"\s*:\s*"([^"]+)"""").find(text)?.groupValues?.get(1)
        return text.takeIf { field("format") == "code_test_suite/1" && field("item") == item.toString() && field("suite") == suite.ref.toString() }
    }

    override fun codeRunnerText(): String? = codeRunner()

    override fun lessonItemsFor(objective: VersionedRef): List<VersionedRef> =
        parsed?.tasks.orEmpty().filter { it.purpose == TaskPurpose.TEACH && objective in it.targetObjectives }
            .flatMap { it.items }.distinct().sortedBy { it.toString() }

    private fun checkSequence(packages: List<PackageFormat.Parsed>) {
        val reasons = buildList {
            packages.zipWithNext().filter { (a, b) -> b.curriculum.version <= a.curriculum.version }
                .forEach { (a, b) -> add("package version ${b.curriculum.version} does not follow ${a.curriculum.version}") }
            fun <T> once(kind: String, refs: List<T>) =
                refs.groupBy { it }.filterValues { it.size > 1 }.keys.forEach { add("$kind $it is authored in more than one package") }
            once("item", packages.flatMap { it.items.keys })
            once("explanation", packages.flatMap { p -> p.explanations.map { it.ref } })
            once("task", packages.flatMap { p -> p.tasks.map { it.ref } })
            once("code test suite", packages.flatMap { p -> p.codeTests.map { it.ref } })
            once("comprehension check", packages.flatMap { p -> p.comprehensionChecks.map { it.ref } })
            once("answer key", packages.flatMap { p -> p.answerKeys.map { it.ref } })
            once("rubric", packages.flatMap { p -> p.rubrics.map { it.ref } })
            once("wrong answer", packages.flatMap { p -> p.answerMisconceptions.map { it.key to it.text.trim() } })
        }
        if (reasons.isNotEmpty()) throw PackageFormat.ParseFailure(reasons)
    }

    private fun merge(packages: List<PackageFormat.Parsed>): PackageFormat.Parsed = PackageFormat.Parsed(
        curriculum = packages.first().curriculum,
        items = packages.fold(emptyMap()) { all, p -> all + p.items },
        documents = packages.fold(emptyMap()) { all, p -> all + p.documents },
        explanations = packages.flatMap { it.explanations },
        codeTests = packages.flatMap { it.codeTests },
        comprehensionChecks = packages.flatMap { it.comprehensionChecks },
        // A key's mapped wrong answers may come from a later package; they are attached here, already checked by `parse`.
        answerKeys = packages.flatMap { it.answerKeys }.let { keys ->
            val wrong = packages.flatMap { it.answerMisconceptions }.groupBy { it.key }
            keys.map { key ->
                wrong[key.ref]?.let { list -> key.copy(wrongAnswerMisconceptions = list.map { WrongAnswerMisconception(it.text, it.misconception) }) }
                    ?: key
            }
        },
        rubrics = packages.flatMap { it.rubrics },
        tasks = packages.flatMap { it.tasks },
        answerMisconceptions = packages.flatMap { it.answerMisconceptions },
    )
}
