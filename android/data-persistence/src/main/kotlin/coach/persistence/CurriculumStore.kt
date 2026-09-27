package coach.persistence

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import coach.model.ContentOrigin
import coach.model.CurriculumPackage
import coach.model.LifecycleStatus
import coach.model.ObjectiveEvidenceProfile
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.ValidationRecord
import coach.model.VersionedRef

/**
 * Publishing into and reading from the immutable curriculum region (11D).
 *
 * This is the **only** write path into that region, which is what makes its rules enforceable: a
 * published version is never overwritten, a package with an unresolved reference is refused whole,
 * and the value sets `QAB-v0` fixed are checked here because `DDM-v0` does not name them as columns
 * and 10D forbade inventing schema the data model does not describe.
 *
 * Publishing is one transaction. A refusal leaves the store byte-for-byte as it was, so a
 * half-published curriculum — an Objective whose Skill is missing — cannot be observed.
 */
internal class CurriculumStore(private val connection: SQLiteConnection) {

    /**
     * Why this package will not be published, decided **before** anything is written. A refusal is
     * therefore not a rollback story: nothing was attempted.
     */
    fun refusal(curriculum: CurriculumPackage): PublishOutcome? {
        if (versionExists(curriculum.version)) return PublishOutcome.AlreadyPublished(curriculum.version)
        val refusals = buildList {
            addAll(curriculum.unresolvedReferences(::isPublished).map { "unresolved reference: $it" })
            addAll(versionMismatches(curriculum))
            addAll(listSeparatorViolations(curriculum))
        }
        return if (refusals.isEmpty()) null else PublishOutcome.Refused(refusals)
    }

    /** Writes the package. The caller opens the transaction, so the whole version lands or none of it. */
    fun write(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome {
        var rows = 0
        execute(
            "INSERT INTO curriculum_version (version, published_at_instant, source_refs, provenance) VALUES (?, ?, ?, ?)",
            curriculum.version, publishedAtInstant, curriculum.sourceRefs, curriculum.provenance,
        )
        rows += 1
        listOf("domain" to curriculum.domains, "module" to curriculum.modules, "topic" to curriculum.topics)
            .forEach { (table, entities) ->
                entities.forEach {
                    execute("INSERT INTO $table (logical_id, version, name) VALUES (?, ?, ?)", it.logicalId, it.version, it.name)
                    rows += 1
                }
            }
        curriculum.skills.forEach {
            execute(
                """
                INSERT INTO skill (logical_id, version, canonical_name, capability_statement, lifecycle_status,
                                   capability_kind, retention_profile, critical_prerequisite, source_refs, provenance)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                it.ref.logicalId, it.ref.version, it.canonicalName, it.capabilityStatement, it.lifecycleStatus,
                it.capabilityKind, it.retentionProfile, if (it.criticalPrerequisite) 1L else 0L, it.sourceRefs, it.provenance,
            )
            rows += 1
        }
        curriculum.objectives.forEach {
            execute(
                """
                INSERT INTO objective (logical_id, version, parent_skill_logical_id, parent_skill_version, required,
                                       criticality, acceptable_evidence_types, direct_evidence_types, required_direct_type)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                it.ref.logicalId, it.ref.version, it.parentSkill.logicalId, it.parentSkill.version,
                if (it.required) 1L else 0L, it.criticality,
                it.acceptableEvidenceTypes.joinToString(","), it.directEvidenceTypes.joinToString(","),
                it.requiredDirectType,
            )
            rows += 1
        }
        curriculum.topicSkillLinks.forEach {
            execute(
                "INSERT INTO topic_skill_link (topic_logical_id, topic_version, skill_logical_id, skill_version) VALUES (?, ?, ?, ?)",
                it.topic.logicalId, it.topic.version, it.skill.logicalId, it.skill.version,
            )
            rows += 1
        }
        curriculum.prerequisiteEdges.forEach {
            execute(
                """
                INSERT INTO skill_prerequisite_edge (prerequisite_skill_logical_id, prerequisite_skill_version,
                    target_skill_logical_id, target_skill_version, edge_version, edge_kind, reason_kind,
                    strictness_profile, lifecycle_status, provenance)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                it.prerequisite.logicalId, it.prerequisite.version, it.target.logicalId, it.target.version,
                it.edgeVersion, it.edgeKind, it.reasonKind, it.strictnessProfile, it.lifecycleStatus, it.provenance,
            )
            rows += 1
        }
        curriculum.resources.forEach {
            if (!resourceLogicalIdExists(it.ref.logicalId)) {
                execute("INSERT INTO assessment_resource (logical_id) VALUES (?)", it.ref.logicalId)
                rows += 1
            }
            execute(
                """
                INSERT INTO assessment_resource_version (logical_id, version, content_ref, rubric_ref, evidence_type,
                    allowed_tools_policy, variant_family_id, dependency_group_id, content_origin)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                it.ref.logicalId, it.ref.version, it.contentRef, it.rubricRef, it.evidenceType,
                it.allowedToolsPolicy, it.variantFamilyId, it.dependencyGroupId, it.contentOrigin.id,
            )
            rows += 1
        }
        curriculum.validationRecords.forEach {
            execute(
                """
                INSERT INTO resource_validation_record (logical_id, version, validated_at_instant, validation_status,
                    validator, origin) VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                it.resource.logicalId, it.resource.version, it.validatedAtInstant, it.status.id, it.validator, it.origin.id,
            )
            rows += 1
        }
        return PublishOutcome.Published(curriculum.version, rows)
    }

    fun resourceVersion(ref: VersionedRef): ResourceVersion? = queryOne(
        """
        SELECT content_ref, rubric_ref, evidence_type, allowed_tools_policy, variant_family_id,
               dependency_group_id, content_origin
        FROM assessment_resource_version WHERE logical_id = ? AND version = ?
        """.trimIndent(),
        ref.logicalId, ref.version,
    ) { statement ->
        ResourceVersion(
            ref = ref,
            contentRef = statement.getText(0),
            rubricRef = statement.textOrNull(1),
            evidenceType = statement.getText(2),
            allowedToolsPolicy = statement.getText(3),
            variantFamilyId = statement.textOrNull(4),
            dependencyGroupId = statement.textOrNull(5),
            contentOrigin = ContentOrigin.entries.single { it.id == statement.getText(6) },
        )
    }

    /** The latest record wins, by the instant it was validated at; earlier records stay as history. */
    fun latestValidation(ref: VersionedRef): ValidationRecord? = queryOne(
        """
        SELECT validated_at_instant, validation_status, validator, origin FROM resource_validation_record
        WHERE logical_id = ? AND version = ? ORDER BY validated_at_instant DESC LIMIT 1
        """.trimIndent(),
        ref.logicalId, ref.version,
    ) { statement ->
        ValidationRecord(
            resource = ref,
            validatedAtInstant = statement.getLong(0),
            status = LifecycleStatus.entries.single { it.id == statement.getText(1) },
            validator = statement.getText(2),
            origin = ContentOrigin.entries.single { it.id == statement.getText(3) },
        )
    }

    fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = queryOne(
        "SELECT acceptable_evidence_types, direct_evidence_types, required_direct_type FROM objective " +
            "WHERE logical_id = ? AND version = ?",
        ref.logicalId, ref.version,
    ) { statement ->
        ObjectiveEvidenceProfile(
            ref = ref,
            acceptableEvidenceTypes = statement.getText(0).split(","),
            directEvidenceTypes = statement.getText(1).split(",").filter { it.isNotEmpty() },
            requiredDirectType = statement.textOrNull(2),
        )
    }

    /** One published Skill version (12B), or `null` if this version was never published. */
    fun skill(ref: VersionedRef): SkillRow? = queryOne(
        "SELECT canonical_name, capability_statement, lifecycle_status, capability_kind, retention_profile, " +
            "critical_prerequisite, source_refs, provenance FROM skill WHERE logical_id = ? AND version = ?",
        ref.logicalId, ref.version,
    ) { statement ->
        SkillRow(
            ref = ref,
            canonicalName = statement.getText(0),
            capabilityStatement = statement.getText(1),
            lifecycleStatus = statement.getText(2),
            capabilityKind = statement.getText(3),
            retentionProfile = statement.getText(4),
            criticalPrerequisite = statement.getLong(5) == 1L,
            sourceRefs = statement.getText(6),
            provenance = statement.getText(7),
        )
    }

    /**
     * Every version of every edge into one pinned target (12B), in every lifecycle, in a stable
     * order. Nothing is filtered here: which edges gate is the prerequisite engine's decision.
     */
    fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> =
        connection.prepare(
            "SELECT prerequisite_skill_logical_id, prerequisite_skill_version, edge_version, edge_kind, " +
                "reason_kind, strictness_profile, lifecycle_status, provenance FROM skill_prerequisite_edge " +
                "WHERE target_skill_logical_id = ? AND target_skill_version = ? " +
                "ORDER BY prerequisite_skill_logical_id, prerequisite_skill_version, edge_version",
        ).use { statement ->
            bind(statement, arrayOf<Any?>(target.logicalId, target.version))
            val edges = mutableListOf<PrerequisiteEdge>()
            while (statement.step()) {
                edges += PrerequisiteEdge(
                    prerequisite = VersionedRef(statement.getText(0), statement.getLong(1).toInt()),
                    target = target,
                    edgeVersion = statement.getLong(2).toInt(),
                    edgeKind = statement.getText(3),
                    reasonKind = statement.getText(4),
                    strictnessProfile = statement.getText(5),
                    lifecycleStatus = statement.getText(6),
                    provenance = statement.getText(7),
                )
            }
            edges
        }

    // ---------------------------------------------------------------- refusals

    /**
     * Every row of a package belongs to the version being published. A row carrying another version
     * would pin user evidence to a version this publish never wrote.
     */
    private fun versionMismatches(curriculum: CurriculumPackage): List<String> = buildList {
        val expected = curriculum.version
        (curriculum.domains + curriculum.modules + curriculum.topics)
            .filter { it.version != expected }
            .forEach { add("entity ${it.logicalId} carries version ${it.version}, not $expected") }
        curriculum.skills.filter { it.ref.version != expected }.forEach { add("skill ${it.ref} is not version $expected") }
        curriculum.objectives.filter { it.ref.version != expected }.forEach { add("objective ${it.ref} is not version $expected") }
    }

    /** The stored list columns are comma-separated, so a token containing a comma is refused. */
    private fun listSeparatorViolations(curriculum: CurriculumPackage): List<String> =
        curriculum.objectives.flatMap { objective ->
            (objective.acceptableEvidenceTypes + objective.directEvidenceTypes)
                .filter { "," in it || it.isBlank() }
                .map { "evidence type '$it' on ${objective.ref} is not a single token" }
        }

    // ---------------------------------------------------------------- helpers

    /**
     * Whether a reference is already in the immutable store. A row published by an earlier version
     * is as real as one in this package — and just as pinned.
     */
    private fun isPublished(kind: String, ref: VersionedRef): Boolean {
        val table = when (kind) {
            CurriculumPackage.SKILL, CurriculumPackage.TOPIC -> kind
            CurriculumPackage.RESOURCE -> "assessment_resource_version"
            else -> return false
        }
        return queryOne("SELECT 1 FROM $table WHERE logical_id = ? AND version = ?", ref.logicalId, ref.version) { true }
            ?: false
    }

    private fun versionExists(version: Int): Boolean =
        queryOne("SELECT 1 FROM curriculum_version WHERE version = ?", version) { true } ?: false

    private fun resourceLogicalIdExists(logicalId: String): Boolean =
        queryOne("SELECT 1 FROM assessment_resource WHERE logical_id = ?", logicalId) { true } ?: false

    private fun execute(sql: String, vararg values: Any?) {
        connection.prepare(sql).use { statement ->
            bind(statement, values)
            statement.step()
        }
    }

    private fun <T> queryOne(sql: String, vararg values: Any?, read: (SQLiteStatement) -> T): T? =
        connection.prepare(sql).use { statement ->
            bind(statement, values)
            if (statement.step()) read(statement) else null
        }

    private fun bind(statement: SQLiteStatement, values: Array<out Any?>) {
        values.forEachIndexed { index, value ->
            when (value) {
                null -> statement.bindNull(index + 1)
                is Int -> statement.bindLong(index + 1, value.toLong())
                is Long -> statement.bindLong(index + 1, value)
                else -> statement.bindText(index + 1, value.toString())
            }
        }
    }

    private fun SQLiteStatement.textOrNull(index: Int): String? = if (isNull(index)) null else getText(index)
}
