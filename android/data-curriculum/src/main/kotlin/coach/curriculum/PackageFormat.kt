package coach.curriculum

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.BlueprintScopes
import coach.model.ContentOrigin
import coach.model.CurriculumPackage
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatusRequirement
import coach.model.IndependenceMode
import coach.model.AssistanceLevel
import coach.model.AuthoredTask
import coach.model.NeedTrigger
import coach.model.TaskPurpose
import coach.model.CodeTest
import coach.model.CodeTestSuite
import coach.model.ComprehensionCheck
import coach.model.ComprehensionKind
import coach.model.AcceptedAnswers
import coach.model.Rubric
import coach.model.RubricCriterion
import coach.model.ExplanationForm
import coach.model.ExplanationVariant
import coach.model.LifecycleStatus
import coach.model.MisconceptionRow
import coach.model.NamedEntity
import coach.model.ObjectiveRow
import coach.model.PrerequisiteEdge
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.TopicSkillLink
import coach.model.UseCeiling
import coach.model.ValidationRecord
import coach.model.VersionedRef

/**
 * The authored curriculum package format (11D): a versioned, line-based text format of `[section]`
 * blocks with `key=value` lines.
 *
 * It is **strict**. An unknown section, an unknown or repeated key, a missing required key, a
 * malformed reference or an unknown enum value refuses the whole package rather than importing a
 * guess. Authored content that is half understood is how a store ends up with items nobody can
 * explain, so a refusal names the line and changes nothing.
 *
 * Only the fields `DDM-v0` names reach the store; the rest of an item's `QAB-v0` metadata stays in
 * the `[item]` document, which is content rather than schema.
 */
object PackageFormat {

    const val FORMAT = "curriculum_package/1"

    data class Parsed(
        val curriculum: CurriculumPackage,
        val items: Map<VersionedRef, AssessmentItem>,
        val documents: Map<VersionedRef, String>,
        /** Written explanations (14C): content, served by the content port, never published into the store. */
        val explanations: List<ExplanationVariant> = emptyList(),
        /** The course's code tests (14D): content, served by the content port, never published into the store. */
        val codeTests: List<CodeTestSuite> = emptyList(),
        /** Written comprehension checks (14E): content, served by the content port, never published into the store. */
        val comprehensionChecks: List<ComprehensionCheck> = emptyList(),
        /** Accepted answers to short-answer items (14F): content, never published into the store. */
        val answerKeys: List<AcceptedAnswers> = emptyList(),
        /** Rubrics of open-response items (14F): content, never published into the store. */
        val rubrics: List<Rubric> = emptyList(),
        /** Authored tasks (15A): content the planner asks for by need, never published into the store. */
        val tasks: List<AuthoredTask> = emptyList(),
    )

    class ParseFailure(val reasons: List<String>) : IllegalArgumentException(reasons.joinToString("; "))

    /**
     * A multi-line value (a prompt, an explanation, a comprehension check) is one package line: a backslash-n is a line
     * break (15A), and — 15C (`D-114`) — a doubled backslash is one backslash, so C code can show `printf("%d\\n", x)`.
     * Any other backslash is itself (`C:\\Users` stays as written), which reads every earlier package exactly as before.
     */
    fun unescape(value: String): String = buildString(value.length) {
        var i = 0
        while (i < value.length) {
            val next = value.getOrNull(i + 1)
            if (value[i] == '\\' && (next == 'n' || next == '\\')) {
                append(if (next == 'n') '\n' else '\\')
                i += 2
            } else {
                append(value[i])
                i += 1
            }
        }
    }

    private data class Section(val name: String, val line: Int, val values: Map<String, String>)

    fun parse(text: String): Parsed {
        val reasons = mutableListOf<String>()
        val lines = text.split("\n").map { it.trim() }
        if (lines.firstOrNull() != FORMAT) throw ParseFailure(listOf("not a $FORMAT document"))

        val header = mutableMapOf<String, String>()
        val sections = mutableListOf<Section>()
        var current: MutableMap<String, String>? = null
        var currentName: String? = null
        var currentLine = 0

        lines.forEachIndexed { index, raw ->
            val number = index + 1
            // 15A: only a whole line is a comment. Content is code and prose, and both contain `#` — a Python
            // comment, a C preprocessor line, a heading — so a `#` inside a value is never cut off.
            val line = if (raw.startsWith("#")) "" else raw
            when {
                index == 0 || line.isEmpty() -> Unit
                line.startsWith("[") && line.endsWith("]") -> {
                    currentName?.let { sections += Section(it, currentLine, current!!.toMap()) }
                    currentName = line.removePrefix("[").removeSuffix("]")
                    currentLine = number
                    current = mutableMapOf()
                    if (currentName !in KNOWN_SECTIONS) reasons += "line $number: unknown section [$currentName]"
                }
                "=" in line -> {
                    val key = line.substringBefore('=').trim()
                    val value = line.substringAfter('=').trim()
                    val target = current ?: header
                    if (key in target) reasons += "line $number: repeated key '$key'"
                    target[key] = value
                }
                else -> reasons += "line $number: not a section or key=value"
            }
        }
        currentName?.let { sections += Section(it, currentLine, current!!.toMap()) }

        val reader = Reader(reasons)
        val version = reader.int(header, "version", 0)
        val curriculum = runCatching {
            CurriculumPackage(
                version = version,
                sourceRefs = reader.text(header, "source_refs", 0),
                provenance = reader.text(header, "provenance", 0),
                domains = sections.filter { it.name == "domain" }.map { reader.named(it) },
                modules = sections.filter { it.name == "module" }.map { reader.named(it) },
                topics = sections.filter { it.name == "topic" }.map { reader.named(it) },
                skills = sections.filter { it.name == "skill" }.map { reader.skill(it) },
                objectives = sections.filter { it.name == "objective" }.map { reader.objective(it) },
                topicSkillLinks = sections.filter { it.name == "topic_skill" }.map { reader.link(it) },
                prerequisiteEdges = sections.filter { it.name == "prerequisite_edge" }.map { reader.edge(it) },
                resources = sections.filter { it.name == "resource" }.map { reader.resource(it) },
                validationRecords = sections.filter { it.name == "validation" }.map { reader.validation(it) },
                misconceptions = sections.filter { it.name == "misconception" }.map { reader.misconception(it) },
            )
        }.getOrElse { failure ->
            reasons += "package: ${failure.message}"
            null
        }

        val items = sections.filter { it.name == "item" }.mapNotNull { section ->
            runCatching { reader.item(section) }.getOrElse { reasons += "item: ${it.message}"; null }
        }
        val documents = sections.filter { it.name == "item" }.mapNotNull { section ->
            val ref = runCatching { reader.ref(section, "ref") }.getOrNull() ?: return@mapNotNull null
            // 15A: a prompt carries code, so a backslash-n is a line break exactly as in an explanation.
            ref to unescape(section.values["prompt"].orEmpty())
        }

        val explanations = sections.filter { it.name == "explanation" }.mapNotNull { section ->
            runCatching { reader.explanation(section) }.getOrElse { reasons += "explanation: ${it.message}"; null }
        }

        val codeTests = codeTests(sections, reader, reasons)
        val comprehensionChecks = sections.filter { it.name == "comprehension_check" }.mapNotNull { section ->
            runCatching { reader.comprehensionCheck(section) }.getOrElse { reasons += "comprehension_check: ${it.message}"; null }
        }

        val answerKeys = answerKeys(sections, reader, reasons)
        val rubrics = rubrics(sections, reader, reasons)
        val tasks = sections.filter { it.name == "task" }.mapNotNull { section ->
            runCatching { reader.task(section) }.getOrElse { reasons += "task: ${it.message}"; null }
        }
        if (curriculum != null) reasons += taskReferences(tasks, curriculum, items.map { it.ref }.toSet(), explanations.map { it.ref }.toSet())

        if (reasons.isNotEmpty() || curriculum == null) throw ParseFailure(reasons)
        return Parsed(curriculum, items.associateBy { it.ref }, documents.toMap(), explanations, codeTests, comprehensionChecks, answerKeys, rubrics,
            tasks)
    }

    /**
     * 15A: a task may only present what this package carries, may only name its own Skill's Objectives, and may not
     * claim more trust than the items it presents. The planner reads a task's validation status directly, so a task
     * declaring itself `validated` over candidate items would be an item promoting itself through its wrapper
     * (`AIV-v0` §23) — the package is refused instead.
     */
    private fun taskReferences(
        tasks: List<AuthoredTask>,
        curriculum: CurriculumPackage,
        itemRefs: Set<VersionedRef>,
        explanationRefs: Set<VersionedRef>,
    ): List<String> = buildList {
        tasks.groupBy { it.ref }.filterValues { it.size > 1 }.keys.forEach { add("task ${it.logicalId}@v${it.version} is declared twice") }
        // A task's own Skill needs no check of its own: every Objective it names must belong to that Skill in this
        // package, so a Skill the package lacks is already refused there (15A mutation F10 was equivalent).
        val parents = curriculum.objectives.associate { it.ref to it.parentSkill }
        val validated = curriculum.validationRecords.groupBy { it.resource }
            .mapValues { (_, records) -> records.maxBy { it.validatedAtInstant }.status }
        tasks.forEach { task ->
            val id = "task ${task.ref.logicalId}@v${task.ref.version}"
            task.targetObjectives.filter { parents[it] != task.primarySkill }
                .forEach { add("$id: objective $it is not an Objective of ${task.primarySkill}") }
            task.items.filterNot { it in itemRefs }.forEach { add("$id: item $it is not in this package") }
            task.explanations.filterNot { it in explanationRefs }.forEach { add("$id: explanation $it is not in this package") }
            if (task.validationStatus in TRUSTING) {
                task.items.filter { validated[it] !in TRUSTING || (task.validationStatus == LifecycleStatus.TRUSTED && validated[it] != LifecycleStatus.TRUSTED) }
                    .forEach { add("$id: declares ${task.validationStatus.id} over item $it, which this package does not validate") }
            }
        }
    }

    private val TRUSTING = setOf(LifecycleStatus.VALIDATED, LifecycleStatus.TRUSTED)

    /**
     * 14C's pattern for 14D: a `[code_test_suite]` names the item version it tests; each `[code_test]` names its suite
     * and the one Objective it speaks for. A test of an undeclared suite, a suite with no test, or two suites for one
     * item version make the package unreadable — the app never guesses which tests apply.
     */
    private fun codeTests(sections: List<Section>, reader: Reader, reasons: MutableList<String>): List<CodeTestSuite> {
        val heads = sections.filter { it.name == "code_test_suite" }.mapNotNull { section ->
            runCatching { reader.codeTestSuite(section) }.getOrElse { reasons += "code_test_suite: ${it.message}"; null }
        }
        val tests = sections.filter { it.name == "code_test" }.mapNotNull { section ->
            runCatching { reader.codeTest(section) }.getOrElse { reasons += "code_test: ${it.message}"; null }
        }
        tests.map { it.first }.filter { suite -> heads.none { it.ref == suite } }.distinct()
            .forEach { reasons += "code_test: suite ${it.logicalId}@v${it.version} is not declared" }
        heads.groupBy { it.item }.filterValues { it.size > 1 }.keys
            .forEach { reasons += "code_test_suite: item ${it.logicalId}@v${it.version} has more than one suite" }
        return heads.mapNotNull { head ->
            runCatching { CodeTestSuite(head.ref, head.item, tests.filter { it.first == head.ref }.map { it.second }, head.buildObjective) }
                .getOrElse { reasons += "code_test_suite ${head.ref.logicalId}: ${it.message}"; null }
        }
    }

    /** 14F: an answer key's accepted answers are attached once every `[accepted_answer]` is read; an orphan is refused. */
    private fun answerKeys(sections: List<Section>, reader: Reader, reasons: MutableList<String>): List<AcceptedAnswers> {
        val heads = sections.filter { it.name == "answer_key" }.mapNotNull { section ->
            runCatching { reader.answerKeyHead(section) }.getOrElse { reasons += "answer_key: ${it.message}"; null }
        }
        val answers = sections.filter { it.name == "accepted_answer" }.map { reader.acceptedAnswer(it) }
        answers.map { it.first }.filter { key -> heads.none { it.ref == key } }.distinct()
            .forEach { reasons += "accepted_answer: key ${it.logicalId}@v${it.version} is not declared" }
        heads.groupBy { it.item }.filterValues { it.size > 1 }.keys
            .forEach { reasons += "answer_key: item ${it.logicalId}@v${it.version} has more than one key" }
        return heads.mapNotNull { head ->
            runCatching { AcceptedAnswers(head.ref, head.item, head.objective, answers.filter { it.first == head.ref }.map { it.second }, head.caseSensitive) }
                .getOrElse { reasons += "answer_key ${head.ref.logicalId}: ${it.message}"; null }
        }
    }

    /** 14F: a rubric's criteria are attached the same way; a criterion of an undeclared rubric is refused. */
    private fun rubrics(sections: List<Section>, reader: Reader, reasons: MutableList<String>): List<Rubric> {
        val heads = sections.filter { it.name == "rubric" }.mapNotNull { section ->
            runCatching { reader.rubricHead(section) }.getOrElse { reasons += "rubric: ${it.message}"; null }
        }
        val criteria = sections.filter { it.name == "rubric_criterion" }.mapNotNull { section ->
            runCatching { reader.rubricCriterion(section) }.getOrElse { reasons += "rubric_criterion: ${it.message}"; null }
        }
        criteria.map { it.first }.filter { rubric -> heads.none { it.ref == rubric } }.distinct()
            .forEach { reasons += "rubric_criterion: rubric ${it.logicalId}@v${it.version} is not declared" }
        heads.groupBy { it.item }.filterValues { it.size > 1 }.keys
            .forEach { reasons += "rubric: item ${it.logicalId}@v${it.version} has more than one rubric" }
        return heads.mapNotNull { head ->
            runCatching { Rubric(head.ref, head.item, criteria.filter { it.first == head.ref }.map { it.second }) }
                .getOrElse { reasons += "rubric ${head.ref.logicalId}: ${it.message}"; null }
        }
    }

    private val KNOWN_SECTIONS = setOf(
        "domain", "module", "topic", "skill", "objective", "topic_skill",
        "prerequisite_edge", "resource", "validation", "item", "misconception", "explanation",
        "code_test_suite", "code_test", "comprehension_check", "answer_key", "accepted_answer", "rubric", "rubric_criterion",
        "task",
    )

    private val KNOWN_KEYS = mapOf(
        "domain" to setOf("logical_id", "version", "name"),
        "module" to setOf("logical_id", "version", "name"),
        "topic" to setOf("logical_id", "version", "name"),
        "skill" to setOf(
            "logical_id", "version", "canonical_name", "capability_statement", "lifecycle_status",
            "capability_kind", "retention_profile", "critical_prerequisite", "source_refs", "provenance",
        ),
        "objective" to setOf(
            "logical_id", "version", "parent_skill", "required", "criticality",
            "acceptable_evidence_types", "direct_evidence_types", "required_direct_type",
        ),
        "topic_skill" to setOf("topic", "skill"),
        "prerequisite_edge" to setOf(
            "prerequisite", "target", "edge_version", "edge_kind", "reason_kind",
            "strictness_profile", "lifecycle_status", "provenance",
        ),
        "resource" to setOf(
            "logical_id", "version", "content_ref", "rubric_ref", "evidence_type",
            "allowed_tools_policy", "variant_family_id", "dependency_group_id", "content_origin",
        ),
        "validation" to setOf("resource", "validated_at_instant", "status", "validator", "origin"),
        // 14B (`D-106`): one closed-catalog label, pinned to one Objective version.
        "misconception" to setOf("logical_id", "version", "objective", "name", "open_question"),
        // 14C (`D-107`): one written explanation of one Objective version. `text` is one line; a backslash-n is a line break.
        "explanation" to setOf("logical_id", "version", "objective", "form", "level", "misconception", "text"),
        // 14D (`D-108`): a suite pinned to one item version, and one section per test naming the Objective it speaks for.
        "code_test_suite" to setOf("logical_id", "version", "item", "build_objective"),
        "code_test" to setOf("suite", "id", "objective", "misconception"),
        // 14F (`D-110`): accepted answers to a short-answer item version, and the rubric of an open-response one.
        "answer_key" to setOf("logical_id", "version", "item", "objective", "case_sensitive"),
        "accepted_answer" to setOf("key", "text"),
        "rubric" to setOf("logical_id", "version", "item"),
        "rubric_criterion" to setOf("rubric", "id", "objective", "statement"),
        // 14E (`D-109`): one written comprehension check after an item version, judged by its answer key.
        "comprehension_check" to setOf(
            "logical_id", "version", "item", "objective", "kind", "evidence_type", "prompt", "choice_a", "choice_b", "choice_c", "choice_d", "answer",
        ),
        // 15A (`D-112`): an authored task — 3B §15's content side of a TaskCandidate.
        "task" to setOf(
            "logical_id", "version", "title", "primary_skill", "target_objectives", "purpose", "activity_kind", "serves",
            "cost_minutes", "required_skills", "explanations", "items", "lifecycle_status", "content_origin", "track",
            "splittable", "minimum_safe_chunk_minutes", "atomic_evidence_boundary",
        ),
        "item" to setOf(
            "ref", "prompt", "target_objectives", "target_skills", "required_skills", "evidence_type",
            "expected_answer_or_rubric_ref", "evaluator_required_status", "evaluator_deterministic_required",
            "evaluator_policy_version", "deterministic_verification", "allowed_tools", "prohibited_solution_sources",
            "independence_mode", "difficulty_class", "lifecycle_status", "content_origin", "declared_use_ceiling",
            "scope_eligibility", "variant_family_id", "dependency_group_id", "forbidden_not_yet_concepts",
            // `QAB-v0` §8/§14/§22, needed by a weekly slot (13A). Both are optional: an item that does not
            // declare them simply cannot fill a weekly slot, and no default is invented for either.
            "expected_active_minutes", "blueprint_roles",
        ),
    )

    private class Reader(val reasons: MutableList<String>) {

        fun text(values: Map<String, String>, key: String, line: Int): String {
            val value = values[key]
            if (value.isNullOrEmpty()) {
                reasons += "line $line: missing '$key'"
                return ""
            }
            return value
        }

        fun optional(section: Section, key: String): String? = section.values[key]?.takeIf { it.isNotEmpty() }

        fun int(values: Map<String, String>, key: String, line: Int): Int {
            val value = values[key]?.toIntOrNull()
            if (value == null) reasons += "line $line: '$key' is not a number"
            return value ?: 1
        }

        fun boolean(section: Section, key: String, default: Boolean? = null): Boolean {
            val value = section.values[key]
            return when (value) {
                "true" -> true
                "false" -> false
                null, "" -> default ?: run { reasons += "[${section.name}] line ${section.line}: missing '$key'"; false }
                else -> run { reasons += "[${section.name}] line ${section.line}: '$key' must be true or false"; false }
            }
        }

        fun list(section: Section, key: String): List<String> =
            section.values[key].orEmpty().split(",").map { it.trim() }.filter { it.isNotEmpty() }

        fun ref(section: Section, key: String): VersionedRef = parseRef(text(section.values, key, section.line), section)

        fun refs(section: Section, key: String): List<VersionedRef> = list(section, key).map { parseRef(it, section) }

        private fun parseRef(value: String, section: Section): VersionedRef {
            val parts = value.split("@v")
            if (parts.size != 2 || parts[1].toIntOrNull() == null) {
                reasons += "[${section.name}] line ${section.line}: '$value' is not a pinned reference (id@vN)"
                return VersionedRef("unresolved", 1)
            }
            return VersionedRef(parts[0], parts[1].toInt())
        }

        private fun <T : Enum<T>> enum(section: Section, key: String, values: Array<T>, id: (T) -> String): T {
            val raw = text(section.values, key, section.line)
            return values.firstOrNull { id(it) == raw } ?: run {
                reasons += "[${section.name}] line ${section.line}: '$raw' is not one of ${values.map(id)}"
                values.first()
            }
        }

        private fun check(section: Section) {
            val known = KNOWN_KEYS[section.name].orEmpty()
            section.values.keys.filterNot { it in known }
                .forEach { reasons += "[${section.name}] line ${section.line}: unknown key '$it'" }
        }

        fun named(section: Section): NamedEntity {
            check(section)
            return NamedEntity(text(section.values, "logical_id", section.line), int(section.values, "version", section.line),
                text(section.values, "name", section.line))
        }

        fun skill(section: Section): SkillRow {
            check(section)
            return SkillRow(
                ref = VersionedRef(text(section.values, "logical_id", section.line), int(section.values, "version", section.line)),
                canonicalName = text(section.values, "canonical_name", section.line),
                capabilityStatement = text(section.values, "capability_statement", section.line),
                lifecycleStatus = text(section.values, "lifecycle_status", section.line),
                capabilityKind = text(section.values, "capability_kind", section.line),
                retentionProfile = text(section.values, "retention_profile", section.line),
                criticalPrerequisite = boolean(section, "critical_prerequisite", default = false),
                sourceRefs = text(section.values, "source_refs", section.line),
                provenance = text(section.values, "provenance", section.line),
            )
        }

        fun objective(section: Section): ObjectiveRow {
            check(section)
            return ObjectiveRow(
                ref = VersionedRef(text(section.values, "logical_id", section.line), int(section.values, "version", section.line)),
                parentSkill = ref(section, "parent_skill"),
                required = boolean(section, "required", default = true),
                criticality = text(section.values, "criticality", section.line),
                acceptableEvidenceTypes = list(section, "acceptable_evidence_types"),
                directEvidenceTypes = list(section, "direct_evidence_types"),
                requiredDirectType = optional(section, "required_direct_type"),
            )
        }

        fun link(section: Section): TopicSkillLink {
            check(section)
            return TopicSkillLink(ref(section, "topic"), ref(section, "skill"))
        }

        fun edge(section: Section): PrerequisiteEdge {
            check(section)
            return PrerequisiteEdge(
                prerequisite = ref(section, "prerequisite"),
                target = ref(section, "target"),
                edgeVersion = int(section.values, "edge_version", section.line),
                edgeKind = text(section.values, "edge_kind", section.line),
                reasonKind = text(section.values, "reason_kind", section.line),
                strictnessProfile = text(section.values, "strictness_profile", section.line),
                lifecycleStatus = text(section.values, "lifecycle_status", section.line),
                provenance = text(section.values, "provenance", section.line),
            )
        }

        fun resource(section: Section): ResourceVersion {
            check(section)
            return ResourceVersion(
                ref = VersionedRef(text(section.values, "logical_id", section.line), int(section.values, "version", section.line)),
                contentRef = text(section.values, "content_ref", section.line),
                evidenceType = text(section.values, "evidence_type", section.line),
                allowedToolsPolicy = text(section.values, "allowed_tools_policy", section.line),
                contentOrigin = enum(section, "content_origin", ContentOrigin.entries.toTypedArray()) { it.id },
                variantFamilyId = optional(section, "variant_family_id"),
                dependencyGroupId = optional(section, "dependency_group_id"),
                rubricRef = optional(section, "rubric_ref"),
            )
        }

        fun validation(section: Section): ValidationRecord {
            check(section)
            return ValidationRecord(
                resource = ref(section, "resource"),
                validatedAtInstant = section.values["validated_at_instant"]?.toLongOrNull()
                    ?: run { reasons += "[validation] line ${section.line}: 'validated_at_instant' is not a number"; 0L },
                status = enum(section, "status", LifecycleStatus.entries.toTypedArray()) { it.id },
                validator = text(section.values, "validator", section.line),
                origin = enum(section, "origin", ContentOrigin.entries.toTypedArray()) { it.id },
            )
        }

        fun misconception(section: Section): MisconceptionRow {
            check(section)
            return MisconceptionRow(
                ref = VersionedRef(text(section.values, "logical_id", section.line), int(section.values, "version", section.line)),
                objective = ref(section, "objective"),
                name = text(section.values, "name", section.line),
                openQuestion = text(section.values, "open_question", section.line),
            )
        }

        fun explanation(section: Section): ExplanationVariant {
            check(section)
            return ExplanationVariant(
                ref = VersionedRef(text(section.values, "logical_id", section.line), int(section.values, "version", section.line)),
                objective = ref(section, "objective"),
                form = enum(section, "form", ExplanationForm.entries.toTypedArray()) { it.id },
                level = optional(section, "level")?.let { id -> AssessmentLevels.byId[id] ?: run { reasons += "[explanation] line ${section.line}: unknown level '$id'"; AssistanceLevel.H1 } },
                text = unescape(text(section.values, "text", section.line)),
                misconception = if (section.values.containsKey("misconception")) ref(section, "misconception") else null,
            )
        }

        /** The suite's head; its tests are attached once every `[code_test]` is read. */
        fun codeTestSuite(section: Section): SuiteHead {
            check(section)
            return SuiteHead(
                ref = VersionedRef(text(section.values, "logical_id", section.line), int(section.values, "version", section.line)),
                item = ref(section, "item"),
                buildObjective = if (section.values.containsKey("build_objective")) ref(section, "build_objective") else null,
            )
        }

        fun answerKeyHead(section: Section): AnswerKeyHead {
            check(section)
            return AnswerKeyHead(
                ref = VersionedRef(text(section.values, "logical_id", section.line), int(section.values, "version", section.line)),
                item = ref(section, "item"),
                objective = ref(section, "objective"),
                caseSensitive = boolean(section, "case_sensitive"),
            )
        }

        fun acceptedAnswer(section: Section): Pair<VersionedRef, String> {
            check(section)
            return ref(section, "key") to text(section.values, "text", section.line)
        }

        fun rubricHead(section: Section): RubricHead {
            check(section)
            return RubricHead(
                VersionedRef(text(section.values, "logical_id", section.line), int(section.values, "version", section.line)),
                ref(section, "item"),
            )
        }

        fun rubricCriterion(section: Section): Pair<VersionedRef, RubricCriterion> {
            check(section)
            return ref(section, "rubric") to RubricCriterion(
                id = text(section.values, "id", section.line),
                objective = ref(section, "objective"),
                statement = text(section.values, "statement", section.line),
            )
        }

        fun comprehensionCheck(section: Section): ComprehensionCheck {
            check(section)
            return ComprehensionCheck(
                ref = VersionedRef(text(section.values, "logical_id", section.line), int(section.values, "version", section.line)),
                item = ref(section, "item"),
                objective = ref(section, "objective"),
                kind = enum(section, "kind", ComprehensionKind.entries.toTypedArray()) { it.id },
                evidenceType = text(section.values, "evidence_type", section.line),
                prompt = unescape(text(section.values, "prompt", section.line)),
                choices = ComprehensionCheck.CHOICE_KEYS.mapNotNull { key -> optional(section, "choice_$key")?.let { key to unescape(it) } }.toMap(),
                answer = text(section.values, "answer", section.line),
            )
        }

        fun task(section: Section): AuthoredTask {
            check(section)
            return AuthoredTask(
                ref = VersionedRef(text(section.values, "logical_id", section.line), int(section.values, "version", section.line)),
                title = text(section.values, "title", section.line),
                primarySkill = ref(section, "primary_skill"),
                targetObjectives = refs(section, "target_objectives"),
                purpose = enum(section, "purpose", TaskPurpose.entries.toTypedArray()) { it.id },
                activityKind = text(section.values, "activity_kind", section.line),
                serves = list(section, "serves").mapNotNull { raw ->
                    NeedTrigger.entries.firstOrNull { it.id == raw }
                        ?: run { reasons += "[task] line ${section.line}: unknown need trigger '$raw'"; null }
                }.toSet(),
                costMinutes = section.values["cost_minutes"]?.toIntOrNull()?.takeIf { it > 0 }
                    ?: run { reasons += "[task] line ${section.line}: 'cost_minutes' is not a positive number"; 1 },
                validationStatus = enum(section, "lifecycle_status", LifecycleStatus.entries.toTypedArray()) { it.id },
                contentOrigin = enum(section, "content_origin", ContentOrigin.entries.toTypedArray()) { it.id },
                requiredSkills = refs(section, "required_skills"),
                explanations = refs(section, "explanations"),
                items = refs(section, "items"),
                track = optional(section, "track"),
                splittable = boolean(section, "splittable", default = false),
                minimumSafeChunkMinutes = optional(section, "minimum_safe_chunk_minutes")?.let { raw ->
                    raw.toIntOrNull() ?: run { reasons += "[task] line ${section.line}: 'minimum_safe_chunk_minutes' is not a number"; null }
                },
                atomicEvidenceBoundary = boolean(section, "atomic_evidence_boundary", default = false),
            )
        }

        fun codeTest(section: Section): Pair<VersionedRef, CodeTest> {
            check(section)
            return ref(section, "suite") to CodeTest(
                id = text(section.values, "id", section.line),
                objective = ref(section, "objective"),
                misconceptionOnFailure = optional(section, "misconception"),
            )
        }

        fun item(section: Section): AssessmentItem {
            check(section)
            return AssessmentItem(
                ref = ref(section, "ref"),
                targetObjectives = refs(section, "target_objectives"),
                targetSkills = refs(section, "target_skills"),
                requiredSkills = refs(section, "required_skills"),
                evidenceType = text(section.values, "evidence_type", section.line),
                expectedAnswerOrRubricRef = text(section.values, "expected_answer_or_rubric_ref", section.line),
                evaluatorRequirement = EvaluatorRequirement(
                    requiredStatus = enum(section, "evaluator_required_status", EvaluatorStatusRequirement.entries.toTypedArray()) { it.id },
                    deterministicRequired = boolean(section, "evaluator_deterministic_required", default = false),
                    evaluatorPolicyVersion = text(section.values, "evaluator_policy_version", section.line),
                ),
                allowedTools = AllowedToolsPolicy(
                    allowed = list(section, "allowed_tools"),
                    prohibitedSolutionSources = list(section, "prohibited_solution_sources"),
                ),
                independenceMode = enum(section, "independence_mode", IndependenceMode.entries.toTypedArray()) { it.id },
                difficultyClass = text(section.values, "difficulty_class", section.line),
                lifecycleStatus = enum(section, "lifecycle_status", LifecycleStatus.entries.toTypedArray()) { it.id },
                contentOrigin = enum(section, "content_origin", ContentOrigin.entries.toTypedArray()) { it.id },
                declaredUseCeiling = enum(section, "declared_use_ceiling", UseCeiling.entries.toTypedArray()) { it.id },
                scopeEligibility = list(section, "scope_eligibility").mapNotNull { raw ->
                    AssessmentScope.entries.firstOrNull { it.id == raw }
                        ?: run { reasons += "[item] line ${section.line}: unknown scope '$raw'"; null }
                }.toSet(),
                variantFamilyId = text(section.values, "variant_family_id", section.line),
                dependencyGroupId = optional(section, "dependency_group_id"),
                forbiddenNotYetConcepts = list(section, "forbidden_not_yet_concepts"),
                deterministicVerification = boolean(section, "deterministic_verification", default = false),
                expectedActiveMinutes = optional(section, "expected_active_minutes")?.let { raw ->
                    raw.toIntOrNull()?.takeIf { it > 0 }
                        ?: run { reasons += "[item] line ${section.line}: 'expected_active_minutes' is not a positive number"; null }
                },
                blueprintRoles = list(section, "blueprint_roles").mapNotNull { raw ->
                    // A role belongs to one scope; declaring it on an item that scope cannot use is a
                    // contradiction in the package, not a role to quietly ignore (13B).
                    val scope = BlueprintScopes.COMPOSED.firstOrNull { scope -> BlueprintScopes.roles(scope).any { it.id == raw } }
                    when {
                        scope == null -> run { reasons += "[item] line ${section.line}: unknown blueprint role '$raw'"; null }
                        scope.id !in list(section, "scope_eligibility") ->
                            run { reasons += "[item] line ${section.line}: blueprint role '$raw' needs scope '${scope.id}'"; null }
                        else -> BlueprintScopes.roles(scope).single { it.id == raw }
                    }
                }.toSet(),
            )
        }
    }
}

/** The assistance levels by id, for the one section that declares one (14C). */
private object AssessmentLevels {
    val byId: Map<String, AssistanceLevel> = AssistanceLevel.entries.associateBy { it.id }
}

/** A suite's head before its tests are attached (14D). */
private data class SuiteHead(val ref: VersionedRef, val item: VersionedRef, val buildObjective: VersionedRef?)

/** An answer key's head before its accepted answers are attached (14F). */
private data class AnswerKeyHead(val ref: VersionedRef, val item: VersionedRef, val objective: VersionedRef, val caseSensitive: Boolean)

/** A rubric's head before its criteria are attached (14F). */
private data class RubricHead(val ref: VersionedRef, val item: VersionedRef)
