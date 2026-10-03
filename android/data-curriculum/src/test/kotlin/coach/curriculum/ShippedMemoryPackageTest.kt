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
 * The fourth package that ships (15D, `MMFX-v0`, `D-116`): `app-wiring/src/main/assets/curriculum_package_v4.txt`, read by
 * the real strict parser together with 15A's, 15B's and 15C's packages, as the app reads them. If the shipped file stops
 * parsing, stops building on the earlier packages, shows C code wrongly, or stops answering the planner, this fails.
 */
class ShippedMemoryPackageTest {

    private val first = File("../app-wiring/src/main/assets/curriculum_package.txt").readText()
    private val second = File("../app-wiring/src/main/assets/curriculum_package_v2.txt").readText()
    private val third = File("../app-wiring/src/main/assets/curriculum_package_v3.txt").readText()
    private val text = File("../app-wiring/src/main/assets/curriculum_package_v4.txt").readText()
    private val parsed = PackageFormat.parse(text)
    private val curriculum = parsed.curriculum
    private val earlier = listOf(first, second, third).map { PackageFormat.parse(it).curriculum }
    private val source = FileContentSource(later = { listOf(second, third, text) }, source = { first })

    private fun need(trigger: NeedTrigger, skill: VersionedRef) =
        LearningNeed("${trigger.id}:$skill", trigger, listOf(skill), Criticality.REQUIRED)

    private val memorySkills = setOf(
        "skill.memory.address_value_distinction", "skill.c.pointer_formation",
        "skill.c.pointer_dereference", "skill.memory.storage_lifetime_intuition",
    ).map { VersionedRef(it, 1) }.toSet()

    @Test
    fun `the package is version 4 of the four memory and pointer Skills, published, and resolves only against what was published`() {
        assertNull(source.failure)
        assertEquals(listOf(1, 2, 3, 4), source.curriculumPackages().map { it.version })
        assertEquals(memorySkills, curriculum.skills.map { it.ref }.toSet())
        assertEquals(6, curriculum.objectives.size)
        assertTrue(curriculum.skills.all { it.lifecycleStatus == "published" })
        assertTrue(curriculum.prerequisiteEdges.all { it.lifecycleStatus == "published" && it.target in memorySkills })
        val publishedSkills = earlier.flatMap { c -> c.skills.map { it.ref } }.toSet()
        val publishedTopics = earlier.flatMap { c -> c.topics.map { VersionedRef(it.logicalId, it.version) } }.toSet()
        assertTrue(curriculum.skills.none { it.ref in publishedSkills }, "nothing published is carried again")
        val unresolved = curriculum.unresolvedReferences { kind, ref ->
            (kind == "skill" && ref in publishedSkills) || (kind == "topic" && ref in publishedTopics)
        }
        assertTrue(unresolved.isEmpty(), unresolved.toString())
        // Memory builds on 15C's C: the address lesson on types, the lifetime lesson on functions.
        assertTrue(curriculum.prerequisiteEdges.any { it.prerequisite == VersionedRef("skill.c.declaration_type_model", 1) })
        assertTrue(curriculum.prerequisiteEdges.any { it.prerequisite == VersionedRef("skill.c.functions_basic", 1) })
        for (keys in listOf(curriculum.skills.map { it.ref }, curriculum.objectives.map { it.ref }, curriculum.resources.map { it.ref },
            curriculum.misconceptions.map { it.ref }, curriculum.topics.map { it.logicalId }, curriculum.topicSkillLinks,
            curriculum.prerequisiteEdges.map { Triple(it.prerequisite, it.target, it.edgeVersion) },
            curriculum.validationRecords.map { it.resource to it.validatedAtInstant })) {
            assertEquals(keys.size, keys.toSet().size, "a duplicate key: ${keys.groupBy { it }.filterValues { it.size > 1 }.keys}")
        }
    }

    @Test
    fun `C code is shown exactly as written, with its backslash-n and its address-of, and the lines of the program are lines`() {
        val item = VersionedRef("item.memory.address_value_distinction.explain_address_vs_value.f01", 1)
        val prompt = assertNotNull(source.resource(item)).body
        assertTrue("    int n = 12;\n    printf(\"%d %d\\n\", m == n, &m == &n);" in prompt, prompt)
        val lesson = source.explanationsFor(VersionedRef("objective.memory.address_value_distinction.explain_address_vs_value", 1))
            .single { it.form == ExplanationForm.WORKED_EXAMPLE }.text
        assertTrue("printf(\"%d %d %d\\n\", x == y, &x == &y, y);" in lesson, "the lesson shows the escape the learner types")
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
        assertEquals(20, parsed.tasks.size)
        // Following a pointer is not a graph prerequisite of the lifetime Skill (6C gives it functions only); its work
        // reads through returned pointers, so the work declares it (3B §10).
        val lifetime = source.taskCandidates(need(NeedTrigger.CONTINUE_LEARNING, VersionedRef("skill.memory.storage_lifetime_intuition", 1))).single()
        assertTrue(VersionedRef("skill.c.pointer_dereference", 1) in lifetime.requiredSkills, lifetime.requiredSkills.toString())
    }

    @Test
    fun `everything shipped was validated, so nothing is a candidate`() {
        val records = curriculum.validationRecords.associate { it.resource to it.status }
        val unvalidated = curriculum.resources.filter { records[it.ref] != LifecycleStatus.VALIDATED }.map { it.ref }
        assertTrue(unvalidated.isEmpty(), "not validated: $unvalidated")
        assertTrue(parsed.tasks.all { it.validationStatus == LifecycleStatus.VALIDATED })
    }
}
