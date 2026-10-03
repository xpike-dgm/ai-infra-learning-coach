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
 * The sixth package that ships (15F, `EAAX-v0`, `D-119`): `app-wiring/src/main/assets/curriculum_package_v6.txt`, read
 * by the real strict parser together with the five earlier packages, as the app reads them. If the shipped file stops
 * parsing, shows the English wrongly, makes English a gate for anything technical, or stops answering the planner, this
 * fails.
 */
class ShippedEnglishPackageTest {

    private val assets = File("../app-wiring/src/main/assets")
    private val first = File(assets, "curriculum_package.txt").readText()
    private val later = (2..5).map { File(assets, "curriculum_package_v$it.txt").readText() }
    private val text = File(assets, "curriculum_package_v6.txt").readText()
    private val parsed = PackageFormat.parse(text)
    private val curriculum = parsed.curriculum
    private val earlier = (listOf(first) + later).map { PackageFormat.parse(it).curriculum }
    private val source = FileContentSource(later = { later + text }, source = { first })

    private fun need(trigger: NeedTrigger, skill: VersionedRef) =
        LearningNeed("${trigger.id}:$skill", trigger, listOf(skill), Criticality.REQUIRED)

    private val a1 = listOf("recognize_core_technical_labels", "technical_noun_phrase_recognition", "be_and_simple_present_comprehension",
        "imperative_instruction_comprehension", "preposition_function_word_comprehension")
    private val a2 = listOf("negation_question_comprehension", "follow_bilingual_technical_instruction", "read_simple_terminal_error_fragments",
        "documentation_navigation", "write_command_result_note")
    private val englishSkills = (a1 + a2).map { VersionedRef("skill.english.$it", 1) }.toSet()

    @Test
    fun `the package is version 6 of the ten A1 and A2 Technical English Skills, published, and closed on its own`() {
        assertNull(source.failure)
        assertEquals(listOf(1, 2, 3, 4, 5, 6), source.curriculumPackages().map { it.version })
        assertEquals(englishSkills, curriculum.skills.map { it.ref }.toSet())
        assertEquals(10, curriculum.objectives.size)
        assertTrue(curriculum.skills.all { it.lifecycleStatus == "published" })
        // Every English edge stays inside the English route: English is never a gate for a technical Skill, and no
        // technical Skill gates English here (TEIP-v0, TECP-v0 §7).
        assertTrue(curriculum.prerequisiteEdges.all { it.lifecycleStatus == "published" && it.target in englishSkills && it.prerequisite in englishSkills })
        val publishedSkills = earlier.flatMap { c -> c.skills.map { it.ref } }.toSet()
        assertTrue(curriculum.skills.none { it.ref in publishedSkills }, "nothing published is carried again")
        val publishedTopics = earlier.flatMap { c -> c.topics.map { VersionedRef(it.logicalId, it.version) } }.toSet()
        val unresolved = curriculum.unresolvedReferences { kind, ref ->
            (kind == "skill" && ref in publishedSkills) || (kind == "topic" && ref in publishedTopics)
        }
        assertTrue(unresolved.isEmpty(), unresolved.toString())
        val root = VersionedRef("skill.english.recognize_core_technical_labels", 1)
        assertTrue(curriculum.prerequisiteEdges.none { it.target == root }, "the first English Skill has no prerequisite")
        for (keys in listOf(curriculum.skills.map { it.ref }, curriculum.objectives.map { it.ref }, curriculum.resources.map { it.ref },
            curriculum.misconceptions.map { it.ref }, curriculum.topics.map { it.logicalId }, curriculum.topicSkillLinks,
            curriculum.prerequisiteEdges.map { Triple(it.prerequisite, it.target, it.edgeVersion) },
            curriculum.validationRecords.map { it.resource to it.validatedAtInstant })) {
            assertEquals(keys.size, keys.toSet().size, "a duplicate key: ${keys.groupBy { it }.filterValues { it.size > 1 }.keys}")
        }
    }

    @Test
    fun `the English a learner reads is shown exactly, with a tool's own message word for word`() {
        val item = VersionedRef("item.english.read_simple_terminal_error_fragments.extract_known_signal.f01", 1)
        val prompt = assertNotNull(source.resource(item)).body
        assertTrue("\n\nls: cannot access 'notes.txt': No such file or directory" in prompt, prompt)
        val note = VersionedRef("item.english.write_command_result_note.demonstrate_capability.f01", 1)
        assertTrue("`ls` ___ four files." in assertNotNull(source.resource(note)).body)
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
    fun `every item is judged by exactly one thing, nothing in English is done at a terminal, and an AI never answers`() {
        parsed.items.values.forEach { item ->
            assertEquals(item, source.assessmentItem(item.ref), "${item.ref} is not served with the earlier packages")
            val judges = listOfNotNull(source.answerKeyFor(item.ref), source.rubricFor(item.ref), source.codeTestsFor(item.ref))
            assertEquals(1, judges.size, "${item.ref} is judged by exactly one of a key, a rubric or a test suite")
            assertTrue("external_ai" in item.allowedTools.prohibitedSolutionSources, "${item.ref}: an AI never does the learner's work")
            // Reading and writing English is the construct: a terminal or a translator would answer it.
            assertTrue("terminal" !in item.allowedTools.allowed, "${item.ref}: no terminal for an English item")
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
        assertEquals(50, parsed.tasks.size)
    }

    @Test
    fun `everything shipped was validated, so nothing is a candidate`() {
        val records = curriculum.validationRecords.associate { it.resource to it.status }
        val unvalidated = curriculum.resources.filter { records[it.ref] != LifecycleStatus.VALIDATED }.map { it.ref }
        assertTrue(unvalidated.isEmpty(), "not validated: $unvalidated")
        assertTrue(parsed.tasks.all { it.validationStatus == LifecycleStatus.VALIDATED })
    }
}
