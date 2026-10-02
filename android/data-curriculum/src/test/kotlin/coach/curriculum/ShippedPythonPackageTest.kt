package coach.curriculum

import coach.model.Criticality
import coach.model.ExplanationForm
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.NeedTrigger
import coach.model.TaskPurpose
import coach.model.TaskServing
import coach.model.VersionedRef
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The second package that ships (15B, `PYFX-v0`, `D-113`): `app-wiring/src/main/assets/curriculum_package_v2.txt`, read
 * by the real strict parser together with 15A's package, as the app reads them. If the shipped file stops parsing, stops
 * building on the first package, or stops answering the planner, this fails.
 */
class ShippedPythonPackageTest {

    private val first = File("../app-wiring/src/main/assets/curriculum_package.txt").readText()
    private val text = File("../app-wiring/src/main/assets/curriculum_package_v2.txt").readText()
    private val parsed = PackageFormat.parse(text)
    private val curriculum = parsed.curriculum
    private val firstCurriculum = PackageFormat.parse(first).curriculum
    private val source = FileContentSource(later = { listOf(text) }, source = { first })

    private fun need(trigger: NeedTrigger, skill: VersionedRef) =
        LearningNeed("${trigger.id}:$skill", trigger, listOf(skill), Criticality.REQUIRED)

    @Test
    fun `the package is version 2 of the twenty Python Skills, published, and resolves only against what 15A published`() {
        assertNull(source.failure)
        assertEquals(2, curriculum.version)
        assertEquals(20, curriculum.skills.size)
        assertEquals(21, curriculum.objectives.size)
        assertTrue(curriculum.skills.all { it.ref.logicalId.startsWith("skill.python.") && it.lifecycleStatus == "published" })
        assertTrue(curriculum.prerequisiteEdges.all { it.lifecycleStatus == "published" })
        // An entity's version is its own semantic revision, not the package's (D-113): everything new is version 1.
        assertTrue(curriculum.skills.all { it.ref.version == 1 } && curriculum.objectives.all { it.ref.version == 1 })
        val publishedSkills = firstCurriculum.skills.map { it.ref }.toSet()
        val publishedTopics = firstCurriculum.topics.map { VersionedRef(it.logicalId, it.version) }.toSet()
        val unresolved = curriculum.unresolvedReferences { kind, ref ->
            (kind == "skill" && ref in publishedSkills) || (kind == "topic" && ref in publishedTopics)
        }
        assertTrue(unresolved.isEmpty(), unresolved.toString())
        for (keys in listOf(curriculum.skills.map { it.ref }, curriculum.objectives.map { it.ref }, curriculum.resources.map { it.ref },
            curriculum.misconceptions.map { it.ref }, curriculum.topics.map { it.logicalId }, curriculum.topicSkillLinks,
            curriculum.prerequisiteEdges.map { Triple(it.prerequisite, it.target, it.edgeVersion) },
            curriculum.validationRecords.map { it.resource to it.validatedAtInstant })) {
            assertEquals(keys.size, keys.toSet().size, "a duplicate key: ${keys.groupBy { it }.filterValues { it.size > 1 }.keys}")
        }
    }

    @Test
    fun `every Objective is teachable, practisable and measurable by its own direct type`() {
        curriculum.objectives.forEach { objective ->
            val explanations = source.explanationsFor(objective.ref)
            assertNotNull(explanations.singleOrNull { it.form == ExplanationForm.CANONICAL }, "${objective.ref} has no lesson")
            val measuring = parsed.items.values.filter {
                objective.ref in it.targetObjectives && it.deterministicVerification && it.evidenceType == objective.requiredDirectType
            }
            assertTrue(measuring.map { it.variantFamilyId }.distinct().size >= 2,
                "${objective.ref}: GRE-v0 asks for two variant families; ${measuring.size} measuring items")
        }
    }

    @Test
    fun `every item is judged by exactly one thing, and every code item by tests the course runs`() {
        parsed.items.values.forEach { item ->
            assertEquals(item, source.assessmentItem(item.ref), "${item.ref} is not served with the first package")
            val judges = listOfNotNull(source.answerKeyFor(item.ref), source.rubricFor(item.ref), source.codeTestsFor(item.ref))
            assertEquals(1, judges.size, "${item.ref} is judged by exactly one of a key, a rubric or a test suite")
            val prompt = assertNotNull(source.resource(item.ref)).body
            assertTrue("\\n" !in prompt, "${item.ref} shows an escape instead of a line break")
            val suite = source.codeTestsFor(item.ref)
            if (item.evidenceType == "authored_code") {
                assertNotNull(suite, "${item.ref} asks for code but has no tests")
                // A program with a fixed output has one honest test; that a function is tried on several inputs is checked
                // against the suites themselves (tools/validate_python_foundations.py), since the package keeps no inputs.
                assertTrue(suite.tests.all { it.objective in item.targetObjectives }, "${item.ref}: a test speaks for another Objective")
                assertTrue("external_ai" in item.allowedTools.prohibitedSolutionSources, "${item.ref}: an AI never writes the learner's code")
            }
        }
        curriculum.misconceptions.forEach { label ->
            assertTrue(source.explanationsFor(label.objective).any { it.misconception == label.ref }, "${label.ref} has no written contrast")
        }
    }

    @Test
    fun `every Skill has a lesson for new learning and work for each later need, and nothing else answers`() {
        curriculum.skills.forEach { skill ->
            val lesson = source.taskCandidates(need(NeedTrigger.NEW_LEARNING, skill.ref)).single()
            assertEquals(TaskPurpose.TEACH, lesson.purpose)
            for (trigger in listOf(NeedTrigger.CONTINUE_LEARNING, NeedTrigger.VERIFICATION_DUE, NeedTrigger.RETENTION_REVIEW_DUE,
                NeedTrigger.REMEDIATION_REQUIRED, NeedTrigger.WEAKNESS_DETECTED)) {
                val served = source.taskCandidates(need(trigger, skill.ref))
                assertEquals(1, served.size, "$trigger for ${skill.ref}")
                assertTrue(trigger in TaskServing.servedBy(served.single().purpose))
            }
            assertTrue(source.taskCandidates(need(NeedTrigger.DIAGNOSTIC_OPPORTUNITY, skill.ref)).isEmpty())
        }
        assertEquals(100, parsed.tasks.size)

        // A requirement the graph does not state travels with the candidate: a list Skill's items are written as
        // functions, which functions_parameters_return teaches (3B §10).
        val lists = VersionedRef("skill.python.list_operations", 1)
        val practice = source.taskCandidates(need(NeedTrigger.CONTINUE_LEARNING, lists)).single()
        assertTrue(VersionedRef("skill.python.functions_parameters_return", 1) in practice.requiredSkills, practice.requiredSkills.toString())
    }

    @Test
    fun `everything shipped was validated, so nothing is a candidate`() {
        val records = curriculum.validationRecords.associate { it.resource to it.status }
        val unvalidated = curriculum.resources.filter { records[it.ref] != LifecycleStatus.VALIDATED }.map { it.ref }
        assertTrue(unvalidated.isEmpty(), "not validated: $unvalidated")
        assertTrue(parsed.tasks.all { it.validationStatus == LifecycleStatus.VALIDATED })
    }
}
