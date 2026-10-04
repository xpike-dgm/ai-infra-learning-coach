package coach.wiring

import coach.application.BuildDailyPlan
import coach.application.IngestCurriculum
import coach.curriculum.FileContentSource
import coach.model.Criticality
import coach.model.DailyCapacityInput
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.NeedDisposition
import coach.model.NeedTrigger
import coach.model.PublishOutcome
import coach.model.StudyTimestamp
import coach.model.TaskPurpose
import coach.model.VersionedRef
import coach.persistence.SqlitePersistence
import coach.ports.ClockPort
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The course as it ships after 15B (`PYFX-v0`, `D-113`) and every package since: the two packages in `assets`, read together by the real content
 * adapter, published into the real SQLite store by the real ingestion, and planned by the real gate and planner. The
 * first package is 15A's, untouched; the second only adds. Nothing here builds a package or a store of its own.
 */
class ShippedCourseTest {

    private val first = File("src/main/assets/curriculum_package.txt").readText()
    private val python = File("src/main/assets/curriculum_package_v2.txt").readText()
    private val content = FileContentSource(later = { listOf(python) }, source = { first })

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_790_942_400_000, "2026-10-02", 3 * 3600)
    }

    private val directory = Files.createTempDirectory("shipped-course").toFile()
    private val store = SqlitePersistence.open(File(directory, "coach.db").path)

    @AfterTest
    fun close() {
        store.close()
        directory.deleteRecursively()
    }

    private fun skill(id: String) = VersionedRef(id, 1)

    @Test
    fun `both shipped packages are read as one course, and the second only adds`() {
        assertNull(content.failure, "the shipped packages must read together")
        val packages = content.curriculumPackages()
        assertEquals(listOf(1, 2), packages.map { it.version })
        val (computing, pythonPackage) = packages
        assertEquals(20, pythonPackage.skills.size)
        assertEquals(21, pythonPackage.objectives.size)
        assertTrue(pythonPackage.skills.all { it.ref.logicalId.startsWith("skill.python.") && it.ref.version == 1 && it.lifecycleStatus == "published" })
        assertTrue(pythonPackage.prerequisiteEdges.all { it.lifecycleStatus == "published" })
        // Every edge points into the new package; its prerequisite is new or already published by the first package.
        val known = (computing.skills + pythonPackage.skills).map { it.ref }.toSet()
        assertTrue(pythonPackage.prerequisiteEdges.all { it.target in pythonPackage.skills.map { s -> s.ref } && it.prerequisite in known })
        assertTrue(pythonPackage.prerequisiteEdges.any { it.prerequisite in computing.skills.map { s -> s.ref } }, "Python builds on 15A")
        // Nothing the first package published is carried again.
        assertTrue(pythonPackage.skills.none { it.ref in computing.skills.map { s -> s.ref } })
        assertTrue(pythonPackage.resources.none { it.ref in computing.resources.map { r -> r.ref } })
    }

    @Test
    fun `both packages are published into the real store, oldest first, and a second start publishes nothing`() {
        val outcomes = IngestCurriculum(store, content, clock).ingestAll()
        assertEquals(listOf(1, 2), outcomes.map { assertIs<PublishOutcome.Published>(it, "$it").version })
        assertEquals(2, store.latestCurriculumVersion())
        assertEquals(32, store.publishedSkills().size)
        // A Python Skill waits on a 15A Skill through the store, exactly as the gate will read it.
        assertTrue(store.prerequisiteEdgesInto(skill("skill.python.run_repl_script")).any { it.prerequisite.logicalId.startsWith("skill.computing.") })

        val again = IngestCurriculum(store, content, clock).ingestAll()
        assertEquals(listOf(PublishOutcome.AlreadyPublished(1), PublishOutcome.AlreadyPublished(2)), again)
    }

    @Test
    fun `with the Python package, a learner who has done nothing is still offered only the two entry lessons`() {
        IngestCurriculum(store, content, clock).ingestAll()
        val built = BuildDailyPlan(store, content, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))
        val trace = assertIs<BuildDailyPlan.Built.Planned>(built).trace

        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"))
        assertEquals(entry, trace.selected.map { it.primarySkill }.toSet(), "no Python Skill starts before what it builds on")
        assertTrue(trace.selected.all { it.purpose == TaskPurpose.TEACH })

        assertEquals(32, trace.needs.count { it.trigger == NeedTrigger.NEW_LEARNING })
        val waiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        assertEquals(30, waiting.size, "everything downstream waits: $waiting")
        assertTrue(waiting.all { it.logicalId.startsWith("skill.python.") || it !in entry })
        assertTrue(trace.needs.none { it.disposition == NeedDisposition.NO_VALID_CANDIDATE }, "every Skill has its lesson")
    }

    // ---------------------------------------------------------------- 15C (`CFNX-v0`, `D-114`): the course with C

    private val c = File("src/main/assets/curriculum_package_v3.txt").readText()
    private val course = FileContentSource(later = { listOf(python, c) }, source = { first })
    private val terminal = skill("skill.linux.terminal_filesystem_navigation")

    @Test
    fun `all three shipped packages are published into the real store, oldest first, and C waits on the terminal`() {
        assertEquals(listOf(1, 2, 3), IngestCurriculum(store, course, clock).ingestAll().map { assertIs<PublishOutcome.Published>(it, "$it").version })
        assertEquals(3, store.latestCurriculumVersion())
        assertEquals(41, store.publishedSkills().size)
        // The first C Skill waits on the Linux terminal Skill (6C) and on 15A's execution model, through the store.
        val into = store.prerequisiteEdgesInto(skill("skill.c.compile_link_run_basic")).map { it.prerequisite }.toSet()
        assertEquals(setOf(terminal, skill("skill.computing.program_execution_model")), into)
        assertEquals((1..3).map { PublishOutcome.AlreadyPublished(it) }, IngestCurriculum(store, course, clock).ingestAll())
    }

    @Test
    fun `with the C package, a learner who has done nothing may also start the terminal lesson, and no C Skill starts`() {
        IngestCurriculum(store, course, clock).ingestAll()
        val trace = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, course, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))).trace

        // The terminal Skill has no prerequisite: it is a third entry point, next to 15A's two.
        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"), terminal)
        assertTrue(entry.containsAll(trace.selected.map { it.primarySkill }), "only entry Skills start: ${trace.selected.map { it.primarySkill }}")
        assertTrue(trace.selected.all { it.purpose == TaskPurpose.TEACH })
        assertTrue(trace.selected.sumOf { it.plannedMinutes } <= 60, "the day is never extended")

        assertEquals(41, trace.needs.count { it.trigger == NeedTrigger.NEW_LEARNING })
        val waiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        assertEquals(38, waiting.size, "everything downstream waits: $waiting")
        assertTrue(waiting.none { it in entry })
        assertTrue(waiting.containsAll(listOf("compile_link_run_basic", "declaration_type_model", "functions_basic").map { skill("skill.c.$it") }))
        assertTrue(trace.needs.none { it.disposition == NeedDisposition.NO_VALID_CANDIDATE }, "every Skill has its lesson")
    }

    // ------------------------------------------------- 15D (`MMFX-v0`, `D-116`): the course with memory and pointers

    private val memory = File("src/main/assets/curriculum_package_v4.txt").readText()
    private val withMemory = FileContentSource(later = { listOf(python, c, memory) }, source = { first })
    private val memorySkills = listOf("skill.memory.address_value_distinction", "skill.c.pointer_formation",
        "skill.c.pointer_dereference", "skill.memory.storage_lifetime_intuition").map { skill(it) }

    @Test
    fun `all four shipped packages are published into the real store, oldest first, and pointers wait on addresses`() {
        assertEquals(listOf(1, 2, 3, 4), IngestCurriculum(store, withMemory, clock).ingestAll().map { assertIs<PublishOutcome.Published>(it, "$it").version })
        assertEquals(4, store.latestCurriculumVersion())
        assertEquals(45, store.publishedSkills().size)
        // A pointer is formed only after address and value are told apart, and on 15C's types (6C), through the store.
        val into = store.prerequisiteEdgesInto(skill("skill.c.pointer_formation")).map { it.prerequisite }.toSet()
        assertEquals(setOf(skill("skill.memory.address_value_distinction"), skill("skill.c.declaration_type_model")), into)
        assertEquals((1..4).map { PublishOutcome.AlreadyPublished(it) }, IngestCurriculum(store, withMemory, clock).ingestAll())
    }

    @Test
    fun `with the memory package, a learner who has done nothing is offered the same three entry lessons, and every memory Skill waits`() {
        IngestCurriculum(store, withMemory, clock).ingestAll()
        val trace = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, withMemory, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))).trace

        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"), terminal)
        assertTrue(entry.containsAll(trace.selected.map { it.primarySkill }), "only entry Skills start: ${trace.selected.map { it.primarySkill }}")
        assertTrue(trace.selected.all { it.purpose == TaskPurpose.TEACH })
        assertTrue(trace.selected.sumOf { it.plannedMinutes } <= 60, "the day is never extended")

        assertEquals(45, trace.needs.count { it.trigger == NeedTrigger.NEW_LEARNING })
        val waiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        assertEquals(42, waiting.size, "everything downstream waits: $waiting")
        assertTrue(waiting.none { it in entry })
        assertTrue(waiting.containsAll(memorySkills))
        assertTrue(trace.needs.none { it.disposition == NeedDisposition.NO_VALID_CANDIDATE }, "every Skill has its lesson")
    }

    // ------------------------------------------------ 15E (`LGSX-v0`, `D-118`): the course with Linux, the shell and Git

    private val shellGit = File("src/main/assets/curriculum_package_v5.txt").readText()
    private val withShellGit = FileContentSource(later = { listOf(python, c, memory, shellGit) }, source = { first })

    @Test
    fun `all five shipped packages are published into the real store, oldest first, and Git waits on the terminal`() {
        assertEquals((1..5).toList(), IngestCurriculum(store, withShellGit, clock).ingestAll().map { assertIs<PublishOutcome.Published>(it, "$it").version })
        assertEquals(5, store.latestCurriculumVersion())
        assertEquals(50, store.publishedSkills().size)
        // Reading a repository waits on 15C's terminal Skill; recording waits on reading (6C), through the store.
        assertEquals(setOf(terminal), store.prerequisiteEdgesInto(skill("skill.git.repository_status_diff")).map { it.prerequisite }.toSet())
        assertEquals(setOf(skill("skill.git.repository_status_diff")),
            store.prerequisiteEdgesInto(skill("skill.git.stage_commit_history_basic")).map { it.prerequisite }.toSet())
        assertEquals((1..5).map { PublishOutcome.AlreadyPublished(it) }, IngestCurriculum(store, withShellGit, clock).ingestAll())
    }

    @Test
    fun `with the Linux, shell and Git package, a learner who has done nothing still starts only the three entry lessons`() {
        IngestCurriculum(store, withShellGit, clock).ingestAll()
        val trace = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, withShellGit, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))).trace

        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"), terminal)
        assertTrue(entry.containsAll(trace.selected.map { it.primarySkill }), "only entry Skills start: ${trace.selected.map { it.primarySkill }}")
        assertTrue(trace.selected.all { it.purpose == TaskPurpose.TEACH })
        assertTrue(trace.selected.sumOf { it.plannedMinutes } <= 60, "the day is never extended")

        assertEquals(50, trace.needs.count { it.trigger == NeedTrigger.NEW_LEARNING })
        val waiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        assertEquals(47, waiting.size, "everything downstream waits: $waiting")
        assertTrue(waiting.none { it in entry })
        assertTrue(waiting.containsAll(listOf("skill.linux.process_exit_stdout_stderr_basic", "skill.git.repository_status_diff",
            "skill.git.stage_commit_history_basic").map { skill(it) }))
        assertTrue(trace.needs.none { it.disposition == NeedDisposition.NO_VALID_CANDIDATE }, "every Skill has its lesson")
    }

    // ------------------------------------------------ 15F (`EAAX-v0`, `D-119`): the course with the A1/A2 English start

    private val english = File("src/main/assets/curriculum_package_v6.txt").readText()
    private val withEnglish = FileContentSource(later = { listOf(python, c, memory, shellGit, english) }, source = { first })
    private val labels = skill("skill.english.recognize_core_technical_labels")

    @Test
    fun `all six shipped packages are published into the real store, oldest first, and English waits only on English`() {
        assertEquals((1..6).toList(), IngestCurriculum(store, withEnglish, clock).ingestAll().map { assertIs<PublishOutcome.Published>(it, "$it").version })
        assertEquals(6, store.latestCurriculumVersion())
        assertEquals(60, store.publishedSkills().size)
        assertTrue(store.prerequisiteEdgesInto(labels).isEmpty(), "the first English Skill has no prerequisite")
        val into = store.prerequisiteEdgesInto(skill("skill.english.negation_question_comprehension")).map { it.prerequisite }.toSet()
        assertEquals(setOf(skill("skill.english.be_and_simple_present_comprehension")), into)
        assertEquals((1..6).map { PublishOutcome.AlreadyPublished(it) }, IngestCurriculum(store, withEnglish, clock).ingestAll())
    }

    @Test
    fun `with the English package, a learner who has done nothing may also start the first English lesson, and nothing technical waits on English`() {
        IngestCurriculum(store, withEnglish, clock).ingestAll()
        val trace = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, withEnglish, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))).trace

        // The first English Skill has no prerequisite: a fourth entry point, next to 15A's two and the terminal.
        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"), terminal, labels)
        assertTrue(entry.containsAll(trace.selected.map { it.primarySkill }), "only entry Skills start: ${trace.selected.map { it.primarySkill }}")
        assertTrue(trace.selected.all { it.purpose == TaskPurpose.TEACH })
        assertTrue(trace.selected.sumOf { it.plannedMinutes } <= 60, "the day is never extended")

        assertEquals(60, trace.needs.count { it.trigger == NeedTrigger.NEW_LEARNING })
        val waiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        assertEquals(56, waiting.size, "everything downstream waits: $waiting")
        assertTrue(waiting.none { it in entry })
        assertTrue(trace.needs.none { it.disposition == NeedDisposition.NO_VALID_CANDIDATE }, "every Skill has its lesson")
    }

    // ------------------------------------------- 15G (`ACNX-v0`, `D-120`): the assessment supplement adds and changes nothing

    private val assessment = File("src/main/assets/curriculum_package_v7.txt").readText()
    private val withAssessment = FileContentSource(later = { listOf(python, c, memory, shellGit, english, assessment) }, source = { first })
    private val addedItems = Regex("""\[item]\nref=(\S+)@v1\n""").findAll(assessment).map { skill(it.groupValues[1]) }.toList()

    @Test
    fun `all seven shipped packages are published into the real store, oldest first, and the supplement adds items, not Skills`() {
        assertEquals((1..7).toList(), IngestCurriculum(store, withAssessment, clock).ingestAll().map { assertIs<PublishOutcome.Published>(it, "$it").version })
        assertEquals(7, store.latestCurriculumVersion())
        assertEquals(60, store.publishedSkills().size, "a supplement adds no Skill")
        assertEquals(613, addedItems.size)
        // Every added item is a published resource the store itself trusts: trust is the store's, never the item's (11D).
        addedItems.forEach { ref ->
            assertTrue(store.resourceVersion(ref) != null, "$ref is not published")
            assertEquals(LifecycleStatus.VALIDATED, store.latestValidation(ref)?.status, "$ref")
        }
        assertEquals((1..7).map { PublishOutcome.AlreadyPublished(it) }, IngestCurriculum(store, withAssessment, clock).ingestAll())
    }

    @Test
    fun `with the supplement, a learner who has done nothing is offered the same entry lessons, and no transfer opens before learning`() {
        IngestCurriculum(store, withAssessment, clock).ingestAll()
        val trace = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, withAssessment, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))).trace

        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"), terminal, labels)
        assertTrue(entry.containsAll(trace.selected.map { it.primarySkill }), "only entry Skills start: ${trace.selected.map { it.primarySkill }}")
        assertTrue(trace.selected.all { it.purpose == TaskPurpose.TEACH })
        assertTrue(trace.selected.sumOf { it.plannedMinutes } <= 60, "the day is never extended")
        // A lesson presents the items it always did: the supplement grew practice, check, review and repair only.
        assertTrue(trace.selected.all { "@v1:" in it.candidateId }, trace.selected.map { it.candidateId }.toString())

        assertEquals(60, trace.needs.count { it.trigger == NeedTrigger.NEW_LEARNING })
        assertTrue(trace.needs.none { it.trigger == NeedTrigger.TRANSFER_OPPORTUNITY }, "nothing is learned, so nothing transfers")
        val waiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        assertEquals(56, waiting.size, "everything downstream waits: $waiting")
        assertTrue(trace.needs.none { it.disposition == NeedDisposition.NO_VALID_CANDIDATE }, "every Skill has its lesson")

        // Later work on an entry Skill is the grown task, its second version.
        val check = withAssessment.taskCandidates(LearningNeed("${NeedTrigger.VERIFICATION_DUE.id}:$labels", NeedTrigger.VERIFICATION_DUE,
            listOf(labels), Criticality.REQUIRED)).single()
        assertTrue("@v2:" in check.id, check.id)
    }
}
