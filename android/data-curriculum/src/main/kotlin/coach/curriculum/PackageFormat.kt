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
    )

    class ParseFailure(val reasons: List<String>) : IllegalArgumentException(reasons.joinToString("; "))

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
            val line = raw.substringBefore('#').trim()
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
            ref to section.values["prompt"].orEmpty()
        }

        if (reasons.isNotEmpty() || curriculum == null) throw ParseFailure(reasons)
        return Parsed(curriculum, items.associateBy { it.ref }, documents.toMap())
    }

    private val KNOWN_SECTIONS = setOf(
        "domain", "module", "topic", "skill", "objective", "topic_skill",
        "prerequisite_edge", "resource", "validation", "item", "misconception",
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
