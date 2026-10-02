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
 * The third package that ships (15C, `CFNX-v0`, `D-114`): `app-wiring/src/main/assets/curriculum_package_v3.txt`, read by
 * the real strict parser together with 15A's and 15B's packages, as the app reads them. If the shipped file stops
 * parsing, stops building on the earlier packages, shows C code wrongly, or stops answering the planner, this fails.
 */
class ShippedCPackageTest {

    private val first = File("../app-wiring/src/main/assets/curriculum_package.txt").readText()
    private val second = File("../app-wiring/src/main/assets/curriculum_package_v2.txt").readText()
    private val text = File("../app-wiring/src/main/assets/curriculum_package_v3.txt").readText()
    private val parsed = PackageFormat.parse(text)
    private val curriculum = parsed.curriculum
    private val earlier = listOf(first, second).map { PackageFormat.parse(it).curriculum }
    private val source = FileContentSource(later = { listOf(second, text) }, source = { first })

    private fun need(trigger: NeedTrigger, skill: VersionedRef) =
        LearningNeed("${trigger.id}:$skill", trigger, listOf(skill), Criticality.REQUIRED)

    @Test
    fun `the package is version 3 of the nine C and Linux Skills, published, and resolves only against what was published`() {
        assertNull(source.failure)
        assertEquals(listOf(1, 2, 3), source.curriculumPackages().map { it.version })
        assertEquals(9, curriculum.skills.size)
        assertEquals(10, curriculum.objectives.size)
        assertTrue(curriculum.skills.all {
            (it.ref.logicalId.startsWith("skill.c.") || it.ref == VersionedRef("skill.linux.terminal_filesystem_navigation", 1))
                && it.lifecycleStatus == "published" && it.ref.version == 1
        })
        assertTrue(curriculum.prerequisiteEdges.all { it.lifecycleStatus == "published" })
        val publishedSkills = earlier.flatMap { c -> c.skills.map { it.ref } }.toSet()
        val publishedTopics = earlier.flatMap { c -> c.topics.map { VersionedRef(it.logicalId, it.version) } }.toSet()
        val unresolved = curriculum.unresolvedReferences { kind, ref ->
            (kind == "skill" && ref in publishedSkills) || (kind == "topic" && ref in publishedTopics)
        }
        assertTrue(unresolved.isEmpty(), unresolved.toString())
        assertTrue(curriculum.prerequisiteEdges.any { it.prerequisite in publishedSkills }, "C builds on 15A's reasoning Skills")
        for (keys in listOf(curriculum.skills.map { it.ref }, curriculum.objectives.map { it.ref }, curriculum.resources.map { it.ref },
            curriculum.misconceptions.map { it.ref }, curriculum.topics.map { it.logicalId }, curriculum.topicSkillLinks,
            curriculum.prerequisiteEdges.map { Triple(it.prerequisite, it.target, it.edgeVersion) },
            curriculum.validationRecords.map { it.resource to it.validatedAtInstant })) {
            assertEquals(keys.size, keys.toSet().size, "a duplicate key: ${keys.groupBy { it }.filterValues { it.size > 1 }.keys}")
        }
    }

    @Test
    fun `C code is shown exactly as written, with its backslash-n, and the lines of the program are lines`() {
        val classify = VersionedRef("item.c.compile_link_run_basic.classify_compile_vs_runtime_failure.f01", 1)
        val prompt = assertNotNull(source.resource(classify)).body
        assertTrue("    printf(\"Basla\\n\";\n    return 0;" in prompt, prompt)
        val lesson = source.explanationsFor(VersionedRef("objective.c.compile_link_run_basic.compile_and_run_small_program", 1))
            .single { it.form == ExplanationForm.CANONICAL }.text
        assertTrue("printf(\"Merhaba, C!\\n\");" in lesson, "the lesson shows the escape the learner types")
    }

    @Test
    fun `every Objective is teachable, practisable and measurable by its own direct type`() {
        curriculum.objectives.forEach { objective ->
            assertNotNull(source.explanationsFor(objective.ref).singleOrNull { it.form == ExplanationForm.CANONICAL }, "${objective.ref} has no lesson")
            val measuring = parsed.items.values.filter {
                objective.ref in it.targetObjectives && it.deterministicVerification && it.evidenceType == objective.requiredDirectType
            }
            assertTrue(measuring.map { it.variantFamilyId }.distinct().size >= 2,
                "${objective.ref}: GRE-v0 asks for two variant families; ${measuring.size} measuring items")
        }
    }

    @Test
    fun `every item is judged by exactly one thing, and work at the computer allows the terminal but never an AI`() {
        parsed.items.values.forEach { item ->
            assertEquals(item, source.assessmentItem(item.ref), "${item.ref} is not served with the earlier packages")
            val judges = listOfNotNull(source.answerKeyFor(item.ref), source.rubricFor(item.ref), source.codeTestsFor(item.ref))
            assertEquals(1, judges.size, "${item.ref} is judged by exactly one of a key, a rubric or a test suite")
            assertTrue("external_ai" in item.allowedTools.prohibitedSolutionSources, "${item.ref}: an AI never does the learner's work")
            val atComputer = item.evidenceType == "authored_code" || item.evidenceType == "hands_on_system_task"
            assertEquals(atComputer, "terminal" in item.allowedTools.allowed, "${item.ref}: the terminal is part of the work exactly at the computer")
            source.codeTestsFor(item.ref)?.let { suite -> assertTrue(suite.tests.all { it.objective in item.targetObjectives }) }
        }
        curriculum.misconceptions.forEach { label ->
            assertTrue(source.explanationsFor(label.objective).any { it.misconception == label.ref }, "${label.ref} has no written contrast")
        }
    }

    @Test
    fun `every Skill has a lesson for new learning and work for each later need, and nothing else answers`() {
        curriculum.skills.forEach { skill ->
            assertEquals(TaskPurpose.TEACH, source.taskCandidates(need(NeedTrigger.NEW_LEARNING, skill.ref)).single().purpose)
            for (trigger in listOf(NeedTrigger.CONTINUE_LEARNING, NeedTrigger.VERIFICATION_DUE, NeedTrigger.RETENTION_REVIEW_DUE,
                NeedTrigger.REMEDIATION_REQUIRED, NeedTrigger.WEAKNESS_DETECTED)) {
                val served = source.taskCandidates(need(trigger, skill.ref))
                assertEquals(1, served.size, "$trigger for ${skill.ref}")
                assertTrue(trigger in TaskServing.servedBy(served.single().purpose))
            }
            assertTrue(source.taskCandidates(need(NeedTrigger.DIAGNOSTIC_OPPORTUNITY, skill.ref)).isEmpty())
        }
        assertEquals(45, parsed.tasks.size)
        // Reading input is not a graph prerequisite of the C control-flow Skills; their work declares it (3B §10).
        val loops = source.taskCandidates(need(NeedTrigger.CONTINUE_LEARNING, VersionedRef("skill.c.for_iteration", 1))).single()
        assertTrue(VersionedRef("skill.c.standard_io_basic", 1) in loops.requiredSkills, loops.requiredSkills.toString())
    }

    @Test
    fun `everything shipped was validated, so nothing is a candidate`() {
        val records = curriculum.validationRecords.associate { it.resource to it.status }
        val unvalidated = curriculum.resources.filter { records[it.ref] != LifecycleStatus.VALIDATED }.map { it.ref }
        assertTrue(unvalidated.isEmpty(), "not validated: $unvalidated")
        assertTrue(parsed.tasks.all { it.validationStatus == LifecycleStatus.VALIDATED })
    }
}
