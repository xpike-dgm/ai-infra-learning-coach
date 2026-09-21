package coach.persistence

import coach.model.ContentOrigin
import coach.model.CurriculumPackage
import coach.model.LifecycleStatus
import coach.model.NamedEntity
import coach.model.ObjectiveRow
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.TopicSkillLink
import coach.model.ValidationRecord
import coach.model.VersionedRef
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Publishing curriculum into real SQLite (11D, `TVSX-v0` tier T2).
 *
 * The rules that matter here are the ones a fake store cannot prove: a published version is never
 * overwritten, a refused package leaves the store exactly as it was, and the schema's own composite
 * foreign keys refuse a reference that is not pinned to a version that exists.
 */
class CurriculumPublishingTest {

    private val skillRef = VersionedRef("skill.python.loops", 1)
    private val objectiveRef = VersionedRef("objective.python.loops.trace", 1)
    private val itemRef = VersionedRef("item.python.loops.q1", 1)
    private val topicRef = VersionedRef("topic.python.control_flow", 1)

    private fun skill(ref: VersionedRef = skillRef) =
        SkillRow(ref, "Loops", "Trace a loop", "stable", "concept", "standard", false, "src", "authored")

    private fun package1(
        version: Int = 1,
        objectives: List<ObjectiveRow> = listOf(
            ObjectiveRow(objectiveRef, skillRef, true, "standard", listOf("code_reading", "explanation"),
                listOf("code_reading"), "code_reading")
        ),
        resources: List<ResourceVersion> = listOf(
            ResourceVersion(itemRef, "item://q1", "code_reading", "documentation", ContentOrigin.HUMAN_AUTHORED,
                variantFamilyId = "family.trace")
        ),
        validations: List<ValidationRecord> = listOf(
            ValidationRecord(itemRef, 1_788_000_000_000, LifecycleStatus.VALIDATED, "human_review", ContentOrigin.HUMAN_AUTHORED)
        ),
    ) = CurriculumPackage(
        version = version,
        sourceRefs = "curriculum/decomposition",
        provenance = "authored",
        topics = listOf(NamedEntity(topicRef.logicalId, topicRef.version, "Control flow")),
        skills = listOf(skill()),
        objectives = objectives,
        topicSkillLinks = listOf(TopicSkillLink(topicRef, skillRef)),
        prerequisiteEdges = emptyList(),
        resources = resources,
        validationRecords = validations,
    )

    private fun <T> withStore(block: (SqlitePersistence) -> T): T =
        SqlitePersistence.open(SqlitePersistence.IN_MEMORY).use(block)

    @Test
    fun `a package is published once and the store then reports curriculum present`() {
        withStore { db ->
            assertEquals(false, db.curriculumPublished())
            val outcome = assertIs<PublishOutcome.Published>(db.publishCurriculum(package1(), 1_789_000_000_000))
            assertEquals(1, outcome.version)
            assertEquals(true, db.curriculumPublished())
            assertEquals(1, db.count("skill"))
            assertEquals(1, db.count("objective"))
            assertEquals(1, db.count("assessment_resource_version"))
            assertEquals(1, db.count("resource_validation_record"))
        }
    }

    @Test
    fun `a published version is never overwritten`() {
        withStore { db ->
            db.publishCurriculum(package1(), 1_789_000_000_000)
            val renamed = package1().copy(skills = listOf(skill().copy(canonicalName = "Rewritten")))
            assertEquals(PublishOutcome.AlreadyPublished(1), db.publishCurriculum(renamed, 1_789_000_100_000))
            assertEquals(listOf("Loops"), db.query("SELECT canonical_name FROM skill") { it.getText(0) })
        }
    }

    @Test
    fun `a correction is a new version, and both versions stay readable`() {
        withStore { db ->
            db.publishCurriculum(package1(), 1_789_000_000_000)
            val v2Skill = VersionedRef("skill.python.loops", 2)
            val v2Objective = VersionedRef("objective.python.loops.trace", 2)
            val second = CurriculumPackage(
                version = 2, sourceRefs = "curriculum", provenance = "authored",
                skills = listOf(skill(v2Skill).copy(canonicalName = "Loops, corrected")),
                objectives = listOf(
                    ObjectiveRow(v2Objective, v2Skill, true, "standard", listOf("code_reading"), listOf("code_reading"), "code_reading")
                ),
            )
            assertIs<PublishOutcome.Published>(db.publishCurriculum(second, 1_789_000_200_000))
            // The v1 Objective still reads exactly as it was published; v2 is a separate identity.
            assertEquals("code_reading", assertNotNull(db.objectiveProfile(objectiveRef)).requiredDirectType)
            assertEquals(
                listOf("code_reading", "explanation"),
                assertNotNull(db.objectiveProfile(objectiveRef)).acceptableEvidenceTypes,
            )
            assertEquals(listOf("code_reading"), assertNotNull(db.objectiveProfile(v2Objective)).acceptableEvidenceTypes)
            assertEquals(2, db.count("skill"))
            assertEquals(listOf("Loops", "Loops, corrected"), db.query("SELECT canonical_name FROM skill ORDER BY version") { it.getText(0) })
        }
    }

    @Test
    fun `a package with an unresolved reference is refused whole and writes nothing`() {
        withStore { db ->
            val orphan = package1().copy(
                objectives = listOf(
                    ObjectiveRow(objectiveRef, VersionedRef("skill.python.loops", 7), true, "standard",
                        listOf("code_reading"), listOf("code_reading"), "code_reading")
                ),
            )
            val refused = assertIs<PublishOutcome.Refused>(db.publishCurriculum(orphan, 1_789_000_000_000))
            assertTrue(refused.reasons.any { "unresolved reference" in it }, refused.reasons.toString())
            Schema.curriculumTables.forEach { assertEquals(0, db.count(it), "$it was written by a refused publish") }
            assertEquals(false, db.curriculumPublished())
        }
    }

    @Test
    fun `a row carrying another version than the package is refused`() {
        withStore { db ->
            val mixed = package1().copy(topics = listOf(NamedEntity(topicRef.logicalId, 2, "Control flow")))
            val refused = assertIs<PublishOutcome.Refused>(db.publishCurriculum(mixed, 1_789_000_000_000))
            assertTrue(refused.reasons.any { "not 1" in it || "version 2" in it }, refused.reasons.toString())
        }
    }

    @Test
    fun `published curriculum cannot be updated or deleted at the storage layer`() {
        withStore { db ->
            db.publishCurriculum(package1(), 1_789_000_000_000)
            assertFailsWith<Throwable> { db.execute("UPDATE skill SET canonical_name = 'rewritten'") }
            assertFailsWith<Throwable> { db.execute("DELETE FROM objective") }
            assertEquals(listOf("Loops"), db.query("SELECT canonical_name FROM skill") { it.getText(0) })
        }
    }

    @Test
    fun `what was published reads back pinned, including the Objective's evidence profile`() {
        withStore { db ->
            db.publishCurriculum(package1(), 1_789_000_000_000)

            val profile = assertNotNull(db.objectiveProfile(objectiveRef))
            assertEquals(listOf("code_reading", "explanation"), profile.acceptableEvidenceTypes)
            assertEquals(listOf("code_reading"), profile.directEvidenceTypes)
            assertEquals("code_reading", profile.requiredDirectType)

            val resource = assertNotNull(db.resourceVersion(itemRef))
            assertEquals("item://q1", resource.contentRef)
            assertEquals(ContentOrigin.HUMAN_AUTHORED, resource.contentOrigin)
            assertEquals("family.trace", resource.variantFamilyId)

            val validation = assertNotNull(db.latestValidation(itemRef))
            assertEquals(LifecycleStatus.VALIDATED, validation.status)

            // A reference that is not pinned to a published version is unknown, never the newest.
            assertNull(db.objectiveProfile(VersionedRef(objectiveRef.logicalId, 2)))
            assertNull(db.resourceVersion(VersionedRef(itemRef.logicalId, 2)))
        }
    }

    @Test
    fun `the latest validation record wins and the earlier one stays as history`() {
        withStore { db ->
            db.publishCurriculum(package1(), 1_789_000_000_000)
            val later = CurriculumPackage(
                version = 2, sourceRefs = "curriculum", provenance = "authored",
                validationRecords = listOf(
                    ValidationRecord(itemRef, 1_789_500_000_000, LifecycleStatus.INVALIDATED, "validator.deterministic",
                        ContentOrigin.HUMAN_AUTHORED)
                ),
            )
            assertIs<PublishOutcome.Published>(db.publishCurriculum(later, 1_789_500_000_000))
            assertEquals(LifecycleStatus.INVALIDATED, assertNotNull(db.latestValidation(itemRef)).status)
            assertEquals(2, db.count("resource_validation_record"))
        }
    }

    @Test
    fun `publishing writes nothing into the user regions`() {
        withStore { db ->
            db.publishCurriculum(package1(), 1_789_000_000_000)
            Schema.truthTables.forEach { assertEquals(0, db.count(it), "$it was written by a publish") }
            assertEquals(0, db.truthWatermark())
        }
    }

    @Test
    fun `a prerequisite edge is refused unless both Skills are published with it`() {
        withStore { db ->
            val dangling = package1().copy(
                prerequisiteEdges = listOf(
                    PrerequisiteEdge(VersionedRef("skill.python.basics", 1), skillRef, 1, "hard", "conceptual",
                        "strict", "stable", "authored")
                ),
            )
            assertIs<PublishOutcome.Refused>(db.publishCurriculum(dangling, 1_789_000_000_000))
            assertEquals(0, db.count("skill_prerequisite_edge"))
        }
    }
}
