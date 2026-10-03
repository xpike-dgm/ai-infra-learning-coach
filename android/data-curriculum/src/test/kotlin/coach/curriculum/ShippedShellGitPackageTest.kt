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
 * The fifth package that ships (15E, `LGSX-v0`, `D-118`): `app-wiring/src/main/assets/curriculum_package_v5.txt`, read
 * by the real strict parser together with the four earlier packages, as the app reads them. If the shipped file stops
 * parsing, stops building on the earlier packages, shows the commands wrongly, or stops answering the planner, this
 * fails.
 */
class ShippedShellGitPackageTest {

    private val assets = File("../app-wiring/src/main/assets")
    private val first = File(assets, "curriculum_package.txt").readText()
    private val later = (2..4).map { File(assets, "curriculum_package_v$it.txt").readText() }
    private val text = File(assets, "curriculum_package_v5.txt").readText()
    private val parsed = PackageFormat.parse(text)
    private val curriculum = parsed.curriculum
    private val earlier = (listOf(first) + later).map { PackageFormat.parse(it).curriculum }
    private val source = FileContentSource(later = { later + text }, source = { first })

    private fun need(trigger: NeedTrigger, skill: VersionedRef) =
        LearningNeed("${trigger.id}:$skill", trigger, listOf(skill), Criticality.REQUIRED)

    private val shellGitSkills = setOf(
        "skill.linux.process_exit_stdout_stderr_basic", "skill.shell.command_options_redirection_basic",
        "skill.shell.pipeline_redirection", "skill.git.repository_status_diff", "skill.git.stage_commit_history_basic",
    ).map { VersionedRef(it, 1) }.toSet()

    @Test
    fun `the package is version 5 of the five Linux, shell and Git Skills, published, and resolves only against what was published`() {
        assertNull(source.failure)
        assertEquals(listOf(1, 2, 3, 4, 5), source.curriculumPackages().map { it.version })
        assertEquals(shellGitSkills, curriculum.skills.map { it.ref }.toSet())
        assertEquals(5, curriculum.objectives.size)
        assertTrue(curriculum.skills.all { it.lifecycleStatus == "published" })
        assertTrue(curriculum.prerequisiteEdges.all { it.lifecycleStatus == "published" && it.target in shellGitSkills })
        val publishedSkills = earlier.flatMap { c -> c.skills.map { it.ref } }.toSet()
        val publishedTopics = earlier.flatMap { c -> c.topics.map { VersionedRef(it.logicalId, it.version) } }.toSet()
        assertTrue(curriculum.skills.none { it.ref in publishedSkills }, "nothing published is carried again")
        val unresolved = curriculum.unresolvedReferences { kind, ref ->
            (kind == "skill" && ref in publishedSkills) || (kind == "topic" && ref in publishedTopics)
        }
        assertTrue(unresolved.isEmpty(), unresolved.toString())
        // Linux and Git build on 15C's terminal Skill and on 15A's execution model.
        assertTrue(curriculum.prerequisiteEdges.any { it.prerequisite == VersionedRef("skill.linux.terminal_filesystem_navigation", 1) })
        assertTrue(curriculum.prerequisiteEdges.any { it.prerequisite == VersionedRef("skill.computing.program_execution_model", 1) })
        for (keys in listOf(curriculum.skills.map { it.ref }, curriculum.objectives.map { it.ref }, curriculum.resources.map { it.ref },
            curriculum.misconceptions.map { it.ref }, curriculum.topics.map { it.logicalId }, curriculum.topicSkillLinks,
            curriculum.prerequisiteEdges.map { Triple(it.prerequisite, it.target, it.edgeVersion) },
            curriculum.validationRecords.map { it.resource to it.validatedAtInstant })) {
            assertEquals(keys.size, keys.toSet().size, "a duplicate key: ${keys.groupBy { it }.filterValues { it.size > 1 }.keys}")
        }
    }

    @Test
    fun `the commands are shown exactly as the check runs them, setup lines included, one command per line`() {
        val item = VersionedRef("item.git.repository_status_diff.inspect_change_state.f03", 1)
        val prompt = assertNotNull(source.resource(item)).body
        assertTrue("echo 1 > sayac.txt\ngit add sayac.txt                 # hazırlık\ngit commit -q -m \"baslangic\"      # hazırlık\n" in prompt, prompt)
        val lesson = source.explanationsFor(VersionedRef("objective.shell.pipeline_redirection.demonstrate_capability", 1))
            .single { it.form == ExplanationForm.CANONICAL }.text
        assertTrue("komut > dosya 2>&1" in lesson && "$ cat x.txt | wc -l" in lesson, "the lesson shows redirection and the pipe as typed")
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
    fun `every item is judged by exactly one thing, and work at the terminal allows it but never an AI`() {
        parsed.items.values.forEach { item ->
            assertEquals(item, source.assessmentItem(item.ref), "${item.ref} is not served with the earlier packages")
            val judges = listOfNotNull(source.answerKeyFor(item.ref), source.rubricFor(item.ref), source.codeTestsFor(item.ref))
            assertEquals(1, judges.size, "${item.ref} is judged by exactly one of a key, a rubric or a test suite")
            assertTrue("external_ai" in item.allowedTools.prohibitedSolutionSources, "${item.ref}: an AI never does the learner's work")
            val atComputer = item.evidenceType == "authored_code" || item.evidenceType == "hands_on_system_task"
            assertEquals(atComputer, "terminal" in item.allowedTools.allowed, "${item.ref}: the terminal is part of the work exactly at the computer")
        }
        // Predicting what a process leaves behind is observation: the terminal would answer it.
        val observation = parsed.items.values.filter { it.evidenceType == "system_observation" }
        assertTrue(observation.isNotEmpty() && observation.none { "terminal" in it.allowedTools.allowed })
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
        assertEquals(25, parsed.tasks.size)
        // Making a file to inspect is not a graph prerequisite of reading a repository (6C gives it the terminal only);
        // the work declares it (3B §10).
        val status = source.taskCandidates(need(NeedTrigger.CONTINUE_LEARNING, VersionedRef("skill.git.repository_status_diff", 1))).single()
        assertTrue(VersionedRef("skill.shell.command_options_redirection_basic", 1) in status.requiredSkills, status.requiredSkills.toString())
    }

    @Test
    fun `everything shipped was validated, so nothing is a candidate`() {
        val records = curriculum.validationRecords.associate { it.resource to it.status }
        val unvalidated = curriculum.resources.filter { records[it.ref] != LifecycleStatus.VALIDATED }.map { it.ref }
        assertTrue(unvalidated.isEmpty(), "not validated: $unvalidated")
        assertTrue(parsed.tasks.all { it.validationStatus == LifecycleStatus.VALIDATED })
    }
}
