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
import kotlin.test.assertTrue

/**
 * The package that actually ships (15A, `CPFX-v0`): `app-wiring/src/main/assets/curriculum_package.txt`, read by the
 * real strict parser and served by the real content adapter. Nothing here builds a package of its own — if the shipped
 * file stops parsing, or stops answering the planner, this fails.
 */
class ShippedPackageTest {

    private val text = File("../app-wiring/src/main/assets/curriculum_package.txt").readText()
    private val parsed = PackageFormat.parse(text)
    private val curriculum = parsed.curriculum
    private val source = FileContentSource { text }

    private fun need(trigger: NeedTrigger, skill: VersionedRef) =
        LearningNeed("${trigger.id}:$skill", trigger, listOf(skill), Criticality.REQUIRED)

    @Test
    fun `the shipped package is version 1 of exactly the fifteen-A subgraph, published and closed`() {
        assertEquals(1, curriculum.version)
        assertEquals(12, curriculum.skills.size)
        assertEquals(13, curriculum.objectives.size)
        assertTrue(curriculum.skills.all { it.ref.logicalId.startsWith("skill.computing.") || it.ref.logicalId.startsWith("skill.programming.") })
        assertTrue(curriculum.skills.all { it.lifecycleStatus == "published" }, "the subgraph is ratified, not left draft")
        assertTrue(curriculum.prerequisiteEdges.all { it.lifecycleStatus == "published" })
        assertEquals(11, curriculum.prerequisiteEdges.size, "6C has ten hard and one soft edge inside this subgraph")
        assertTrue(curriculum.unresolvedReferences().isEmpty(), curriculum.unresolvedReferences().toString())
        // The store refuses an entity whose version is not the package's (11D).
        assertTrue(curriculum.skills.all { it.ref.version == 1 } && curriculum.objectives.all { it.ref.version == 1 })
        // Every primary key the store would write is written once: SQLite would refuse the whole transaction otherwise.
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
            measuring.forEach { assertNotNull(source.answerKeyFor(it.ref), "${it.ref} has no answer key") }
        }
    }

    @Test
    fun `every item is answerable and every prompt is readable`() {
        parsed.items.values.forEach { item ->
            val keyed = source.answerKeyFor(item.ref) != null
            val rubric = source.rubricFor(item.ref) != null
            assertTrue(keyed != rubric, "${item.ref} is judged by exactly one of a key or a rubric")
            val prompt = assertNotNull(source.resource(item.ref)).body
            assertTrue("\\n" !in prompt, "${item.ref} shows an escape instead of a line break")
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
        assertEquals(60, parsed.tasks.size)

        // A requirement the graph does not state travels with the candidate, so the gate can hold the work back:
        // every while condition is a comparison, taught by expression_boolean_reasoning (3B §10).
        val iteration = VersionedRef("skill.programming.iteration_reasoning", 1)
        val lesson = source.taskCandidates(need(NeedTrigger.NEW_LEARNING, iteration)).single()
        assertEquals(listOf(VersionedRef("skill.programming.expression_boolean_reasoning", 1)), lesson.requiredSkills)
        assertTrue(lesson.targetObjectives.size == 2, "the lesson names both iteration Objectives")
    }

    @Test
    fun `everything shipped was validated, so nothing is a candidate`() {
        val records = curriculum.validationRecords.associate { it.resource to it.status }
        val unvalidated = curriculum.resources.filter { records[it.ref] != LifecycleStatus.VALIDATED }.map { it.ref }
        assertTrue(unvalidated.isEmpty(), "not validated: $unvalidated")
        assertTrue(parsed.tasks.all { it.validationStatus == LifecycleStatus.VALIDATED })
    }
}
