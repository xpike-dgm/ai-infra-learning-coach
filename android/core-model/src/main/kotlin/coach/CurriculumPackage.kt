package coach.model

/**
 * What ingestion publishes into the immutable curriculum store (11D).
 *
 * The package carries exactly the fields `DDM-v0` names for the curriculum region — no more. An
 * item's remaining `QAB-v0` metadata (its targets, use ceiling, scope eligibility, evaluator
 * requirement, difficulty and independence mode) travels in the authored **content document**, not
 * in invented columns; naming those in the schema would be a `DDM-v0` decision, and 10D forbade
 * inventing fields the data model does not name.
 *
 * A published version is never overwritten (`LFPS-v0` §curriculum): a correction is a new version,
 * and every user-side reference stays pinned to the version it was recorded against.
 */
data class CurriculumPackage(
    val version: Int,
    val sourceRefs: String,
    val provenance: String,
    val domains: List<NamedEntity> = emptyList(),
    val modules: List<NamedEntity> = emptyList(),
    val topics: List<NamedEntity> = emptyList(),
    val skills: List<SkillRow> = emptyList(),
    val objectives: List<ObjectiveRow> = emptyList(),
    val topicSkillLinks: List<TopicSkillLink> = emptyList(),
    val prerequisiteEdges: List<PrerequisiteEdge> = emptyList(),
    val resources: List<ResourceVersion> = emptyList(),
    val validationRecords: List<ValidationRecord> = emptyList(),
) {
    init {
        require(version >= 1) { "a curriculum version is 1 or greater" }
        require(sourceRefs.isNotBlank() && provenance.isNotBlank()) {
            "a published curriculum version records where it came from (DDM-v0 §curriculum_version)"
        }
    }

    /**
     * References that resolve neither **inside this package** nor in what is [alreadyPublished].
     * Publishing half a graph would leave the store with an Objective whose Skill does not exist, so
     * ingestion refuses instead.
     *
     * The published side matters, and a T2 check found it missing: a later version may carry only a
     * revalidation of a resource an earlier version published, and refusing that would have made
     * `AIV-v0`'s revalidation impossible to express.
     *
     * Version pins are part of the identity everywhere: a reference that matches only by logical id
     * is unresolved here, exactly as the schema's composite foreign keys would refuse it.
     */
    fun unresolvedReferences(
        alreadyPublished: (kind: String, ref: VersionedRef) -> Boolean = { _, _ -> false },
    ): List<String> {
        val skillKeys = skills.map { it.ref }.toSet()
        val topicKeys = topics.map { VersionedRef(it.logicalId, it.version) }.toSet()
        val resourceKeys = resources.map { it.ref }.toSet()
        fun resolves(kind: String, ref: VersionedRef, inPackage: Set<VersionedRef>) =
            ref in inPackage || alreadyPublished(kind, ref)

        return buildList {
            objectives.filterNot { resolves(SKILL, it.parentSkill, skillKeys) }
                .forEach { add("objective ${it.ref} -> skill ${it.parentSkill}") }
            topicSkillLinks.forEach {
                if (!resolves(TOPIC, it.topic, topicKeys)) add("topic_skill_link -> topic ${it.topic}")
                if (!resolves(SKILL, it.skill, skillKeys)) add("topic_skill_link -> skill ${it.skill}")
            }
            prerequisiteEdges.forEach {
                if (!resolves(SKILL, it.prerequisite, skillKeys)) add("prerequisite_edge -> skill ${it.prerequisite}")
                if (!resolves(SKILL, it.target, skillKeys)) add("prerequisite_edge -> skill ${it.target}")
            }
            validationRecords.filterNot { resolves(RESOURCE, it.resource, resourceKeys) }
                .forEach { add("validation_record -> resource ${it.resource}") }
        }
    }

    companion object {
        const val SKILL = "skill"
        const val TOPIC = "topic"
        const val RESOURCE = "assessment_resource_version"
    }
}

data class NamedEntity(val logicalId: String, val version: Int, val name: String) {
    init {
        require(logicalId.isNotBlank() && name.isNotBlank()) { "a curriculum entity has an id and a name" }
        require(version >= 1) { "a curriculum entity version is 1 or greater" }
    }
}

data class SkillRow(
    val ref: VersionedRef,
    val canonicalName: String,
    val capabilityStatement: String,
    val lifecycleStatus: String,
    val capabilityKind: String,
    val retentionProfile: String,
    val criticalPrerequisite: Boolean,
    val sourceRefs: String,
    val provenance: String,
)

data class ObjectiveRow(
    val ref: VersionedRef,
    val parentSkill: VersionedRef,
    val required: Boolean,
    val criticality: String,
    val acceptableEvidenceTypes: List<String>,
    val directEvidenceTypes: List<String>,
    val requiredDirectType: String? = null,
) {
    init {
        require(acceptableEvidenceTypes.isNotEmpty()) { "an Objective declares what it accepts as evidence" }
        require(directEvidenceTypes.all { it in acceptableEvidenceTypes }) {
            "a direct evidence type the Objective does not accept is a contradiction"
        }
        require(requiredDirectType == null || requiredDirectType in directEvidenceTypes) {
            "the required direct type must itself be a direct type"
        }
    }

    fun profile(): ObjectiveEvidenceProfile =
        ObjectiveEvidenceProfile(ref, acceptableEvidenceTypes, directEvidenceTypes, requiredDirectType)
}

data class TopicSkillLink(val topic: VersionedRef, val skill: VersionedRef)

data class PrerequisiteEdge(
    val prerequisite: VersionedRef,
    val target: VersionedRef,
    val edgeVersion: Int,
    val edgeKind: String,
    val reasonKind: String,
    val strictnessProfile: String,
    val lifecycleStatus: String,
    val provenance: String,
) {
    init {
        require(edgeKind == "hard" || edgeKind == "soft") { "PRG-v0 knows two edge kinds: hard and soft" }
    }
}

/** The `DDM-v0`-named fields of an assessment resource version. */
data class ResourceVersion(
    val ref: VersionedRef,
    val contentRef: String,
    val evidenceType: String,
    val allowedToolsPolicy: String,
    val contentOrigin: ContentOrigin,
    val variantFamilyId: String? = null,
    val dependencyGroupId: String? = null,
    val rubricRef: String? = null,
) {
    init {
        require(contentRef.isNotBlank()) { "a resource version points at its content" }
        require(evidenceType.isNotBlank()) { "a resource version declares the evidence type it produces" }
    }
}

/** `AIV-v0` §31: a validation run is recorded, with who validated and what the item's origin was. */
data class ValidationRecord(
    val resource: VersionedRef,
    val validatedAtInstant: Long,
    val status: LifecycleStatus,
    val validator: String,
    val origin: ContentOrigin,
) {
    init {
        require(validator.isNotBlank()) { "a validation record names its validator" }
    }
}

/** What publishing did, or why it refused. A refusal changes nothing in the store. */
sealed interface PublishOutcome {
    data class Published(val version: Int, val rows: Int) : PublishOutcome

    /**
     * The version is already published. Curriculum is immutable once published, so this is not an
     * error to retry but the rule working: a correction is a new version.
     */
    data class AlreadyPublished(val version: Int) : PublishOutcome

    data class Refused(val reasons: List<String>) : PublishOutcome
}
