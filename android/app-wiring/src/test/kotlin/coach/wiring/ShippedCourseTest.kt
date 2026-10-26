package coach.wiring

import coach.application.BuildDailyPlan
import coach.application.ReminderCheck
import coach.application.OpenTask
import coach.application.RunTask
import coach.application.StartedTasks
import coach.application.CaptureProgramSnapshot
import coach.model.AnswerResult
import coach.model.AuthoredTask
import coach.model.CapabilityGap
import coach.model.ExposureFact
import coach.model.TaskServing
import coach.ports.ContentPort
import coach.application.ReminderSettings
import coach.model.ReminderDecision
import coach.model.ReminderSilence
import coach.model.ReminderPlanContent
import coach.ports.PersistencePort
import coach.application.CapacitySettings
import coach.application.PlanToday
import coach.application.TodayFactsQuery
import coach.model.CapacityProfile
import coach.model.TodayCapacityChoice
import coach.presentation.TodayPresentation
import coach.presentation.todayInput
import kotlin.test.assertNotNull
import coach.application.EnglishProfileQuery
import coach.application.SubmitAttempt
import coach.application.TopicQuery
import coach.model.AttemptSubmission
import coach.model.BandCompletion
import coach.model.BaseProfileStatus
import coach.model.ExtensionState
import coach.model.ProvenanceOrigin
import coach.model.TopicCoverage
import coach.model.TopicProjection
import coach.model.TopicState
import coach.presentation.EnglishProfilePresentation
import coach.presentation.TopicPresentation
import coach.application.IngestCurriculum
import coach.application.LearningHistoryQuery
import coach.application.RecomputeSkillState
import coach.application.ProgressQuery
import coach.curriculum.FileContentSource
import coach.model.Criticality
import coach.model.DailyCapacityInput
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.NeedDisposition
import coach.model.NeedTrigger
import coach.model.PublishOutcome
import coach.model.SkillPresentationState
import coach.model.SkillStateProjection
import coach.model.StudyTimestamp
import coach.model.TaskPurpose
import coach.model.VersionedRef
import coach.persistence.SqlitePersistence
import coach.ports.ClockPort
import coach.ports.TruthRecord
import coach.model.HistoryFamily
import coach.presentation.AppHealth
import coach.presentation.ProgressPresentation
import coach.presentation.ProgressState
import coach.model.EvaluatorAvailability
import coach.model.StoreStatus
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

    // ------------------------------------------- 15H (`CQAX-v0`, `D-121`): content QA corrects by appending, never by editing

    private val contentQa = File("src/main/assets/curriculum_package_v8.txt").readText()
    private val withContentQa = FileContentSource(later = { listOf(python, c, memory, shellGit, english, assessment, contentQa) }, source = { first })
    private val revised = Regex("""\[item]\nref=(\S+)@v2\n""").findAll(contentQa).map { it.groupValues[1] }.toList()

    @Test
    fun `all eight shipped packages are published into the real store, and a revised item's old version is retired, not overwritten`() {
        assertEquals((1..8).toList(), IngestCurriculum(store, withContentQa, clock).ingestAll().map { assertIs<PublishOutcome.Published>(it, "$it").version })
        assertEquals(8, store.latestCurriculumVersion())
        assertEquals(60, store.publishedSkills().size, "content QA adds no Skill")
        assertEquals(81, revised.size)
        // The store keeps both versions and every record; the newest record decides, so nothing selects the old version again.
        revised.forEach { id ->
            assertEquals(LifecycleStatus.RETIRED, store.latestValidation(VersionedRef(id, 1))?.status, id)
            assertTrue(store.resourceVersion(VersionedRef(id, 1)) != null, "$id@v1 stays readable by its pinned reference")
            assertEquals(LifecycleStatus.VALIDATED, store.latestValidation(VersionedRef(id, 2))?.status, id)
        }
        val edges = store.publishedSkills().flatMap { store.prerequisiteEdgesInto(it.ref) }
        assertEquals(77 + 49, edges.size, "the graph now says what the lessons need")
        assertEquals((1..8).map { PublishOutcome.AlreadyPublished(it) }, IngestCurriculum(store, withContentQa, clock).ingestAll())
    }

    @Test
    fun `with content QA, a learner who has done nothing starts the same entry lessons, and a lesson waits for what it shows`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        val trace = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, withContentQa, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))).trace

        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"), terminal, labels)
        assertTrue(entry.containsAll(trace.selected.map { it.primarySkill }), "only entry Skills start: ${trace.selected.map { it.primarySkill }}")
        assertTrue(trace.selected.all { it.purpose == TaskPurpose.TEACH })
        assertTrue(trace.selected.sumOf { it.plannedMinutes } <= 60, "the day is never extended")
        val waiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        assertEquals(56, waiting.size, "everything downstream still waits: $waiting")
        assertTrue(trace.needs.none { it.disposition == NeedDisposition.NO_VALID_CANDIDATE }, "every Skill keeps its lesson")

        // The lifetime lesson reads through pointers: it now waits for the pointer Skill as well as for functions.
        val lifetime = trace.candidates.single { "task.memory.storage_lifetime_intuition.teach@v2:" in it.candidateId }
        assertTrue(skill("skill.c.pointer_dereference") in lifetime.relatedSkills, lifetime.relatedSkills.toString())
    }

    // ---------------------------------------------------------------- 16A — Skill analytics (SKAX-v0)

    @Test
    fun `with all eight packages, Progress for a learner who has done nothing is an honest start, in the eight states only`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        val facts = ProgressQuery(store, withContentQa).load()
        assertEquals(60, facts.skills.size)
        val states = facts.skills.associate { it.skill to assertIs<SkillStateProjection.Derived>(it.projection, it.skill.toString()).state }
        // Nothing recorded: every Skill is either not yet evidenced or waiting on its own prerequisite.
        assertEquals(setOf(SkillPresentationState.NOT_YET_EVIDENCED, SkillPresentationState.PREREQUISITE_UNRESOLVED), states.values.toSet())
        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"), terminal, labels)
        assertEquals(entry, states.filterValues { it == SkillPresentationState.NOT_YET_EVIDENCED }.keys)
        assertEquals(56, states.count { it.value == SkillPresentationState.PREREQUISITE_UNRESOLVED })

        val view = ProgressPresentation.overview(facts, AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.AVAILABLE))
        assertEquals(ProgressState.EMPTY_NO_EVIDENCE_YET, view.state)
        assertEquals(0, view.recomputing)
        assertEquals(60, view.inventory.sumOf { it.count }, "an inventory of current state, every Skill once")
        // Reading Progress writes nothing.
        assertTrue(store.publishedSkills().none { store.readProjection("skill_state:${it.ref.logicalId}@v1") != null })
    }

    @Test
    fun `the Skills Progress shows as waiting are the ones the planner holds back`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        val trace = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, withContentQa, clock)
            .build(DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90))).trace
        val plannerWaiting = trace.needs.filter { it.disposition == NeedDisposition.BLOCKED }.flatMap { it.targetSkills }.toSet()
        val progressWaiting = ProgressQuery(store, withContentQa).load().skills
            .filter { (it.projection as? SkillStateProjection.Derived)?.state == SkillPresentationState.PREREQUISITE_UNRESOLVED }
            .map { it.skill }.toSet()
        assertEquals(plannerWaiting, progressWaiting, "one gate, one answer")
        // A waiting Skill names what it waits for, and only published Skills.
        val detail = ProgressQuery(store, withContentQa).detail(skill("skill.c.pointer_formation"))!!
        assertTrue(detail.row.prerequisite.hardBlockers.isNotEmpty())
        assertTrue(detail.row.prerequisite.hardBlockers.all { store.skill(it) != null })
        assertTrue(detail.objectives.isNotEmpty() && detail.objectives.all { it.gatesPassed == null })
        assertTrue(detail.topics.isNotEmpty(), "where the curriculum places it, for orientation")
    }

    // ---------------------------------------------------------------- 16B — Öğrenme geçmişi (LHRX-v0)

    private fun recordExplanation(n: Int) = store.inTransaction {
        val id = store.appendTruth(TruthRecord("evidence_event", clock.now(), mapOf(
            "skill_logical_id" to "skill.computing.program_execution_model", "skill_version" to "1", "evidence_type" to "explanation",
            "outcome" to "positive", "evaluator_status" to "verified", "independence_class" to "independent", "contested" to "0",
            "correctness_or_rubric_result" to "1.0", "variant_family_id" to "family.history.$n",
        )))
        store.appendTruth(TruthRecord("evidence_event_objective", clock.now(), mapOf("evidence_event_id" to id.toString(),
            "objective_logical_id" to "objective.computing.program_execution_model.explain_pipeline", "objective_version" to "1")))
    }

    @Test
    fun `with all eight packages, a learner who has done nothing has no history, and recorded work is replayed as the engines decided`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        val empty = LearningHistoryQuery(store).load()
        assertTrue(empty.curriculumLoaded && empty.entries.isEmpty() && empty.sessions.isEmpty())

        val execution = skill("skill.computing.program_execution_model")
        recordExplanation(1); RecomputeSkillState(store, clock).recompute(execution)
        recordExplanation(2); RecomputeSkillState(store, clock).recompute(execution)
        val stored = store.readProjection("skill_state:${execution.logicalId}@v1")!!
        val before = store.readProjection("skill_state:${execution.logicalId}@v1")

        val history = LearningHistoryQuery(store).load(execution)
        assertEquals(2, history.entries.size)
        assertTrue(history.entries.all { it.family == HistoryFamily.LEARNING })
        // The replay ends where the real store's recompute ended, and reading the history wrote nothing.
        // One required Objective, two clean independent answers from two variant families: confirmed (`GRE-v0` §8).
        assertEquals("confirmed_current", stored.payload["mastery_axis_state"])
        assertEquals(listOf("capability_confirmed"), history.entries.first().changes.map { it.kind.id })
        assertEquals(emptyList(), history.entries.last().changes)
        assertEquals(before, store.readProjection("skill_state:${execution.logicalId}@v1"))
    }

    // ---------------------------------------------------------------- 16C — Progress / weakness (PWPX-v0)

    @Test
    fun `with all eight packages, a new learner's Topic is available exactly when one of its Skills can start, and nothing is written`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        val progress = ProgressQuery(store, withContentQa).load()
        val topics = TopicQuery(store, withContentQa).load(progress).topics
        assertEquals(27, topics.size)
        val states = topics.associate { it.topic to assertIs<TopicProjection.Derived>(it.projection, it.topic.toString()) }
        assertEquals(setOf(TopicState.AVAILABLE, TopicState.LOCKED), states.values.map { it.state }.toSet())
        // The four entry Skills sit in four Topics; everything else waits, exactly as the planner holds it back.
        assertEquals(4, states.values.count { it.state == TopicState.AVAILABLE })
        assertEquals(23, states.values.count { it.state == TopicState.LOCKED })
        // One gate, one answer: a Topic is open exactly when Progress shows one of its Skills as startable (not waiting),
        // so it is never locked while the planner offers one of its Skills; a locked one names the outside Skills it waits for.
        val skillStates = progress.skills.associate { it.skill to assertIs<SkillStateProjection.Derived>(it.projection).state }
        states.forEach { (topic, p) ->
            val open = p.reading.members.any { skillStates.getValue(it.skill) != SkillPresentationState.PREREQUISITE_UNRESOLVED }
            assertEquals(open, p.state == TopicState.AVAILABLE, topic.toString())
            if (p.state == TopicState.LOCKED) assertTrue(p.waitingFor.isNotEmpty() || p.metadataProblem, topic.toString())
            val members = p.reading.members.map { it.skill }.toSet()
            assertTrue(p.waitingFor.all { it !in members && store.skill(it) != null }, topic.toString())
        }
        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"), terminal, labels)
        assertTrue(states.values.filter { p -> p.reading.members.any { it.skill in entry } }.all { it.state == TopicState.AVAILABLE })
        // Every published Objective has a lesson to work, so none is "no lesson authored".
        assertTrue(states.values.flatMap { p -> p.reading.members.flatMap { it.objectives } }.all { it.second == TopicCoverage.NOT_COVERED })
        assertTrue(store.publishedSkills().none { store.readProjection("skill_state:${it.ref.logicalId}@v1") != null })
    }

    @Test
    fun `with all eight packages, the Technical English profile is honestly not complete, B1 is not published and nothing is claimed`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        val profile = EnglishProfileQuery(store, withContentQa).load()!!
        assertEquals(10, profile.reading.skills.count { it.row != null })
        assertEquals(BaseProfileStatus.NOT_YET_COMPLETE, profile.status)
        assertEquals(listOf(BandCompletion.INCOMPLETE, BandCompletion.INCOMPLETE, BandCompletion.NOT_PUBLISHED), profile.bands.map { it.completion })
        assertNull(profile.historicalHighestBand)
        assertEquals(listOf(skill("skill.english.documentation_navigation")), profile.extensions.map { it.skill })
        assertTrue(profile.extensions.all { it.state == ExtensionState.NO_EXTENSION_EVIDENCE })
        val view = EnglishProfilePresentation.of(profile)!!
        assertEquals("Teknik İngilizce — temel profil henüz tamamlanmadı", view.headline)
    }

    @Test
    fun `work recorded in a Topic starts it, its worked lesson covers that Objective, and the Topic stays learning`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        val execution = skill("skill.computing.program_execution_model")
        val objective = VersionedRef("objective.computing.program_execution_model.explain_pipeline", 1)
        val lesson = withContentQa.lessonItemsFor(objective)
        assertTrue(lesson.isNotEmpty())
        SubmitAttempt(store, clock).submit(AttemptSubmission(lesson.first(), "data:,x", ProvenanceOrigin.USER_AUTHORED))
        recordExplanation(1); RecomputeSkillState(store, clock).recompute(execution)
        recordExplanation(2); RecomputeSkillState(store, clock).recompute(execution)
        val topic = VersionedRef("topic.python.execution", 1)
        val p = assertIs<TopicProjection.Derived>(TopicQuery(store, withContentQa).load().topics.single { it.topic == topic }.projection)
        assertEquals(TopicState.LEARNING, p.state)
        assertEquals(TopicCoverage.LESSON_WORKED, p.reading.members.flatMap { it.objectives }.toMap()[objective])
        assertTrue(execution !in p.notYetConfirmed, "the confirmed Skill is not named as unconfirmed")
        val detail = TopicPresentation.detail(TopicQuery(store, withContentQa).detail(topic)!!)
        assertEquals("Öğreniliyor", detail.chip.label)
    }

    // ---------------------------------------------------------------- 16D — Ayarlar (STGX-v0)

    private class DayClock(var day: String) : ClockPort {
        override fun now() = StudyTimestamp(1_790_942_400_000 + 86_400_000L * day.takeLast(2).toLong(), day, 3 * 3600)
    }

    @Test
    fun `with all eight packages and no preference, the app plans today inside the normal preset, once`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        val built = assertIs<BuildDailyPlan.Built.Planned>(PlanToday(store, withContentQa, clock).ensure())
        assertEquals(60, built.trace.capacity.hardBudgetMinutes)
        assertEquals("normal_profile", built.trace.capacity.source.id)
        assertTrue(built.trace.selected.isNotEmpty())
        assertTrue(built.trace.selected.sumOf { it.plannedMinutes } <= built.trace.capacity.planningBudgetMinutes)
        // Today reads the plan the planner wrote; a second call today writes nothing.
        val facts = TodayFactsQuery(store, clock).load()
        assertEquals(built.trace.selected.size, facts.plan!!.tasks.size)
        assertEquals(60, facts.capacity!!.resolvedDailyMinutes)
        assertIs<BuildDailyPlan.Built.AlreadyPlanned>(PlanToday(store, withContentQa, clock).ensure())
        assertEquals(1, store.planVersions().size)
    }

    @Test
    fun `a shorter day replans today inside it, the same choice again writes nothing, and tomorrow it is gone`() {
        val day = DayClock("2026-10-25")
        IngestCurriculum(store, withContentQa, day).ingestAll()
        PlanToday(store, withContentQa, day).ensure()
        CapacitySettings(store, day).setToday(TodayCapacityChoice.Profile(CapacityProfile.SHORT))
        val replanned = assertIs<BuildDailyPlan.Built.Planned>(PlanToday(store, withContentQa, day).afterCapacityChange())
        assertEquals("today_capacity_changed", replanned.trace.replan!!.trigger.id)
        assertEquals(30, replanned.trace.capacity.hardBudgetMinutes)
        assertTrue(replanned.trace.selected.sumOf { it.plannedMinutes } <= replanned.trace.capacity.planningBudgetMinutes)
        // A preference that leaves today's budget as it is writes no plan.
        CapacitySettings(store, day).setProfile(CapacityProfile.INTENSIVE, 120)
        assertIs<BuildDailyPlan.Built.AlreadyPlanned>(PlanToday(store, withContentQa, day).afterCapacityChange())
        assertEquals(2, store.planVersions().size)
        // The next study day: today's choice is gone, nothing carries over as a debt, and the normal preset holds.
        day.day = "2026-10-26"
        assertEquals(TodayCapacityChoice.None, CapacitySettings(store, day).read().today)
        val next = assertIs<BuildDailyPlan.Built.Planned>(PlanToday(store, withContentQa, day).ensure())
        assertEquals("reentry", next.trace.generationKind)
        assertEquals(60, next.trace.capacity.hardBudgetMinutes)
    }

    @Test
    fun `the learner's own normal length is the first plan's budget, and nothing is chosen for today`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        CapacitySettings(store, clock).setProfile(CapacityProfile.NORMAL, 45)
        val built = assertIs<BuildDailyPlan.Built.Planned>(PlanToday(store, withContentQa, clock).ensure())
        assertEquals(45, built.trace.capacity.hardBudgetMinutes)
        assertEquals("normal_profile", built.trace.capacity.source.id)
    }

    @Test
    fun `a new learner's Today now shows a real plan, and its first task is one of the four starting lessons`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        PlanToday(store, withContentQa, clock).ensure()
        val view = TodayPresentation.of(todayInput(TodayFactsQuery(store, clock).load(), AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.AVAILABLE)))
        val first = assertNotNull(view.primaryTask)
        val entry = setOf(skill("skill.computing.program_execution_model"), skill("skill.programming.state_assignment_model"), terminal, labels)
        assertTrue(first.targetSkillRefs.any { it in entry }, first.targetSkillRefs.toString())
    }

    // ---------------------------------------------------------------- 16E — Bildirimler (RMDX-v0)

    private fun remind(clockNow: ClockPort, lastShown: String? = null, allowed: Boolean = true, scheduled: String = clockNow.now().studyDay) =
        ReminderCheck(store, withContentQa, clockNow).decide(scheduled, lastShown, allowed)

    @Test
    fun `with all eight packages, a new learner's reminder names the plan the planner would write, and writes nothing`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        ReminderSettings(store, clock).set(enabled = true, minuteOfDay = 19 * 60)
        val watermark = store.truthWatermark()
        val shown = assertIs<ReminderDecision.Show>(remind(clock))
        // Deciding wrote no truth: no plan sits on a day the learner has not opened.
        assertEquals(watermark, store.truthWatermark())
        assertEquals(0, store.planVersions().size)
        // When the learner opens the app, the planner writes exactly the plan the reminder spoke of.
        val built = assertIs<BuildDailyPlan.Built.Planned>(PlanToday(store, withContentQa, clock).ensure())
        assertEquals(ReminderCheck.contentOf(built.trace), shown.content)
        assertEquals("2026-10-02", shown.studyDay)
        assertTrue(shown.content.plannedMinutes <= built.trace.capacity.hardBudgetMinutes, "inside today's time, never more")
        // Once today's plan is stored, the reminder reads it and says the same.
        assertEquals(shown, remind(clock))
    }

    @Test
    fun `the reminder is silence when off, blocked by Android, late, already shown today, or when work is recorded today`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        assertEquals(ReminderDecision.Silent(ReminderSilence.DISABLED), remind(clock))
        ReminderSettings(store, clock).set(enabled = true, minuteOfDay = 19 * 60)
        assertEquals(ReminderDecision.Silent(ReminderSilence.NOTIFICATIONS_BLOCKED), remind(clock, allowed = false))
        assertEquals(ReminderDecision.Silent(ReminderSilence.DELIVERED_ON_ANOTHER_DAY), remind(clock, scheduled = "2026-10-01"))
        assertEquals(ReminderDecision.Silent(ReminderSilence.ALREADY_SHOWN_TODAY), remind(clock, lastShown = "2026-10-02"))
        assertIs<ReminderDecision.Show>(remind(clock, lastShown = "2026-10-01"))
        // Narrowed at 16F (`E2EX-v0` / `D-128`): attempts now name their planned task, so the reminder counts only tasks
        // not started yet, and is silent once every task in today's plan is started.
        val built = assertIs<BuildDailyPlan.Built.Planned>(PlanToday(store, withContentQa, clock).ensure())
        val tasks = store.latestPlan()!!.plannedTasks
        startTask(tasks.first().plannedTaskId)
        assertEquals(built.trace.selected.size - 1, assertIs<ReminderDecision.Show>(remind(clock)).content.taskCount)
        tasks.drop(1).forEach { startTask(it.plannedTaskId) }
        assertEquals(ReminderDecision.Silent(ReminderSilence.WORK_RECORDED_TODAY), remind(clock))
        // Turning it off is a row too, and the newest row is the setting.
        ReminderSettings(store, clock).set(enabled = false, minuteOfDay = 19 * 60)
        assertEquals(ReminderDecision.Silent(ReminderSilence.DISABLED), remind(clock))
        assertEquals(2, store.reminderPreferences().size)
    }

    @Test
    fun `after days away the reminder names today's fresh plan, never yesterday's, and still writes nothing`() {
        val day = DayClock("2026-10-25")
        IngestCurriculum(store, withContentQa, day).ingestAll()
        ReminderSettings(store, day).set(enabled = true, minuteOfDay = 8 * 60)
        PlanToday(store, withContentQa, day).ensure()
        day.day = "2026-10-29"
        val watermark = store.truthWatermark()
        val shown = assertIs<ReminderDecision.Show>(remind(day))
        assertEquals("2026-10-29", shown.studyDay)
        assertEquals(watermark, store.truthWatermark())
        assertEquals(1, store.planVersions().size, "the days away left no plan behind")
        val reentry = assertIs<BuildDailyPlan.Built.Planned>(PlanToday(store, withContentQa, day).ensure())
        assertEquals("reentry", reentry.trace.generationKind)
        assertEquals(ReminderCheck.contentOf(reentry.trace), shown.content)
    }

    /** The real store, with one read broken: what the reminder must do when it cannot know. */
    private class CannotCount(store: SqlitePersistence) : PersistencePort by store {
        override fun countTruth(kind: String, studyDay: String): Int = error("this day cannot be read")
        // Narrowed at 16F (`E2EX-v0` / `D-128`): the reminder reads which of today's tasks were started, not a count.
        override fun startedPlannedTasks(plannedTaskIds: Collection<Long>): Set<Long> = error("this day cannot be read")
    }

    private class CannotReadPlan(private val store: SqlitePersistence) : PersistencePort by store {
        override fun latestPlan() = store.latestPlan()?.copy(traceText = "not a trace")
    }

    @Test
    fun `a day or a plan that cannot be read is silence, never a zero or a guess`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        ReminderSettings(store, clock).set(enabled = true, minuteOfDay = 19 * 60)
        PlanToday(store, withContentQa, clock).ensure()
        assertEquals(ReminderDecision.Silent(ReminderSilence.DAY_UNREADABLE),
            ReminderCheck(CannotCount(store), withContentQa, clock).decide("2026-10-02", null, true))
        assertEquals(ReminderDecision.Silent(ReminderSilence.PLAN_UNREADABLE),
            ReminderCheck(CannotReadPlan(store), withContentQa, clock).decide("2026-10-02", null, true))
    }

    @Test
    fun `a task kept because it was started is not new, and a review or an assessment is named only when the plan holds one`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        val trace = assertIs<BuildDailyPlan.Built.Planned>(PlanToday(store, withContentQa, clock).ensure()).trace
        val plain = assertNotNull(ReminderCheck.contentOf(trace))
        assertEquals(ReminderPlanContent(trace.selected.size, trace.selected.sumOf { it.plannedMinutes }, false, false, false), plain)
        // A new learner's plan holds no review and no cycle slot, so none is named.
        assertTrue(trace.selected.none { it.purpose == TaskPurpose.RETAIN })
        // One task kept from an earlier version (already started) is not new work, and a plan of only kept work names none.
        val kept = trace.copy(selected = trace.selected.mapIndexed { i, e -> if (i == 0) e.copy(preserved = true) else e })
        assertEquals(trace.selected.size - 1, ReminderCheck.contentOf(kept)?.taskCount ?: 0)
        assertNull(ReminderCheck.contentOf(trace.copy(selected = trace.selected.map { it.copy(preserved = true) })))
        // A review, this week's slot and this month's slot are named exactly when the plan holds them.
        val review = trace.copy(selected = trace.selected.mapIndexed { i, e -> if (i == 0) e.copy(purpose = TaskPurpose.RETAIN) else e })
        assertEquals(Triple(true, false, false), ReminderCheck.contentOf(review)!!.let { Triple(it.includesReview, it.includesWeeklyAssessment, it.includesMonthlyAssessment) })
        val weekly = trace.copy(selected = trace.selected.mapIndexed { i, e -> if (i == 0) e.copy(candidateId = "weekly:2026-W40:slot-1") else e })
        assertEquals(Triple(false, true, false), ReminderCheck.contentOf(weekly)!!.let { Triple(it.includesReview, it.includesWeeklyAssessment, it.includesMonthlyAssessment) })
        val monthly = trace.copy(selected = trace.selected.mapIndexed { i, e -> if (i == 0) e.copy(candidateId = "monthly:2026-10:slot-1") else e })
        assertEquals(Triple(false, false, true), ReminderCheck.contentOf(monthly)!!.let { Triple(it.includesReview, it.includesWeeklyAssessment, it.includesMonthlyAssessment) })
    }

    @Test
    fun `with nothing published the reminder is silent`() {
        ReminderSettings(store, clock).set(enabled = true, minuteOfDay = 19 * 60)
        assertEquals(ReminderDecision.Silent(ReminderSilence.NO_CURRICULUM), remind(clock))
    }

    // ---------------------------------------------------------------- 16F — Uçtan uca günlük döngü (E2EX-v0)

    /** An attempt made for a planned task, as the runner records it: the learner started that task. */
    private fun startTask(plannedTaskId: Long) {
        val lesson = withContentQa.lessonItemsFor(VersionedRef("objective.computing.program_execution_model.explain_pipeline", 1))
        SubmitAttempt(store, clock).submit(AttemptSubmission(lesson.first(), "data:,x", ProvenanceOrigin.USER_AUTHORED, plannedTaskId = plannedTaskId))
    }

    private fun firstTaskId(): Long = TodayFactsQuery(store, clock).load().plan!!.tasks.first().plannedTaskId

    @Test
    fun `a new learner's first task enters with every condition confirmed, from the store and nothing assumed`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        PlanToday(store, withContentQa, clock).ensure()
        val watermark = store.truthWatermark()
        val facts = OpenTask(store, withContentQa, clock).open(firstTaskId())
        assertTrue(facts.startable, facts.toString())
        val run = assertNotNull(facts.run)
        assertTrue(run.items.isNotEmpty() && run.items.all { withContentQa.answerKeyFor(it) != null }, "only questions an answer key can judge")
        assertTrue(run.lesson.isNotEmpty() && run.lesson.all { it.objective in run.targetObjectives }, "the course's own lesson for its Objectives")
        assertEquals(watermark, store.truthWatermark(), "entry writes no truth")
        // A task that is not in today's plan cannot be entered.
        assertEquals(false, OpenTask(store, withContentQa, clock).open(999_999).stillSelected)
    }

    @Test
    fun `a run serves unseen questions, records each answer with its planned task, judges it by its key and writes its evidence`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        PlanToday(store, withContentQa, clock).ensure()
        val run = assertNotNull(OpenTask(store, withContentQa, clock).open(firstTaskId()).run)
        val runner = RunTask(store, withContentQa, clock)
        val item = assertNotNull(runner.serve(run, emptySet()))
        assertTrue(store.exposuresFor(listOf(item.ref), emptyList()).any { it.kind == ExposureFact.ITEM_VERSION_SEEN }, "shown is seen")
        // A blank answer is a skip: nothing is written.
        val before = store.truthWatermark()
        assertNull(runner.answer(run, item, "  ", ProvenanceOrigin.USER_AUTHORED, emptyList()))
        assertEquals(before, store.truthWatermark())

        val right = assertNotNull(runner.answer(run, item, withContentQa.answerKeyFor(item.ref)!!.answers.first(), ProvenanceOrigin.USER_AUTHORED, emptyList()))
        assertEquals(AnswerResult.RIGHT, right.result)
        assertEquals(setOf(run.plannedTaskId), store.startedPlannedTasks(listOf(run.plannedTaskId)))
        assertTrue(right.evidenceIds.isNotEmpty())
        fun row(id: Long) = item.targetObjectives.flatMap { store.evidenceFor(it) }.single { it.id == id }
        // A teaching task's answers are practice by design (`2D` §6), and never a mastery claim.
        assertEquals(if (run.purpose == TaskPurpose.TEACH) "practice_only" else "independent", row(right.evidenceIds.first()).independenceClass.id)
        assertEquals("positive", row(right.evidenceIds.first()).outcome.id)

        // This lesson holds one question; the run ends there rather than repeating it.
        assertNull(runner.serve(run, setOf(item.ref)).takeIf { run.items.size == 1 })
        val second = assertNotNull(OpenTask(store, withContentQa, clock).open(TodayFactsQuery(store, clock).load().plan!!.tasks[1].plannedTaskId).run)
        val next = assertNotNull(runner.serve(second, emptySet()))
        val wrong = assertNotNull(runner.answer(second, next, "zzz-not-an-answer", ProvenanceOrigin.USER_AUTHORED, emptyList()))
        assertEquals(AnswerResult.NOT_RIGHT, wrong.result)
        // Seeing the answer is asked for after the answer froze: it is recorded as help after submission and as a solution
        // exposure, and the frozen answer keeps what it showed.
        val shown = runner.reveal(next, wrong.attemptId)
        assertEquals(withContentQa.answerKeyFor(next.ref)!!.answers, shown)
        val wrongRow = next.targetObjectives.flatMap { store.evidenceFor(it) }.single { it.id == wrong.evidenceIds.first() }
        assertEquals(listOf(coach.model.AssistanceLevel.H4), wrongRow.assistanceLevels)
        assertTrue(store.exposuresFor(listOf(next.ref), listOf(next.variantFamilyId)).any { it.kind == ExposureFact.SOLUTION_EXPOSURE })
        // The frozen answer keeps what it showed: the evidence written at submission is unchanged by the reveal.
        assertEquals("negative", wrongRow.outcome.id)
    }

    @Test
    fun `a started task is not offered again, entering it again is refused, and the reminder no longer counts it`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        PlanToday(store, withContentQa, clock).ensure()
        val first = firstTaskId()
        val run = assertNotNull(OpenTask(store, withContentQa, clock).open(first).run)
        val runner = RunTask(store, withContentQa, clock)
        val item = assertNotNull(runner.serve(run, emptySet()))
        runner.answer(run, item, "B", ProvenanceOrigin.USER_AUTHORED, emptyList())
        val today = TodayFactsQuery(store, clock).load()
        assertTrue(today.plan!!.tasks.single { it.plannedTaskId == first }.started)
        val view = TodayPresentation.of(todayInput(today, AppHealth.of(StoreStatus.Ready, EvaluatorAvailability.AVAILABLE)))
        assertTrue(view.primaryTask?.plannedTaskRef != first, "a started task is not today's next action")
        assertEquals(false, OpenTask(store, withContentQa, clock).open(first).stillSelected)
        assertEquals(setOf(run.position), StartedTasks.today(store, clock))
    }

    @Test
    fun `a replan keeps every task the learner started and re-resolves only the rest`() {
        val day = DayClock("2026-10-25")
        IngestCurriculum(store, withContentQa, day).ingestAll()
        PlanToday(store, withContentQa, day).ensure()
        val first = TodayFactsQuery(store, day).load().plan!!.tasks.first()
        SubmitAttempt(store, day).submit(AttemptSubmission(VersionedRef("item.computing.program_execution_model.explain_pipeline.f01", 1), "data:,x",
            ProvenanceOrigin.USER_AUTHORED, plannedTaskId = first.plannedTaskId))
        CapacitySettings(store, day).setToday(TodayCapacityChoice.Profile(CapacityProfile.SHORT))
        val replanned = assertIs<BuildDailyPlan.Built.Planned>(PlanToday(store, withContentQa, day).afterCapacityChange())
        assertEquals(listOf(first.position), replanned.trace.replan!!.preservedPositions)
        val kept = replanned.trace.selected.single { it.preserved }
        assertEquals(first.targetSkillRefs.single(), kept.primarySkill)
        // The kept task is still started in the new version, and Today still does not offer it.
        assertTrue(TodayFactsQuery(store, day).load().plan!!.tasks.single { it.position == kept.position }.kept)
        assertEquals(setOf(kept.position), StartedTasks.today(store, day))
    }

    @Test
    fun `the end of a run reports what canonical state did, and a run with nothing answered changes nothing`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        PlanToday(store, withContentQa, clock).ensure()
        val execution = skill("skill.computing.program_execution_model")
        val taskId = TodayFactsQuery(store, clock).load().plan!!.tasks.first { execution in it.targetSkillRefs }.plannedTaskId
        val run = assertNotNull(OpenTask(store, withContentQa, clock).open(taskId).run)
        val runner = RunTask(store, withContentQa, clock)
        // A state no engine wrote is not a "before" (13E): the Skill's starting state is written first, as any earlier run would.
        RecomputeSkillState(store, clock).recompute(execution)
        val before = CaptureProgramSnapshot(store, clock).capture()
        assertNull(runner.finish(run, before, emptyList(), emptyList()), "nothing answered: nothing recomputed, nothing replanned")
        val item = assertNotNull(runner.serve(run, emptySet()))
        val answered = assertNotNull(runner.answer(run, item, withContentQa.answerKeyFor(item.ref)!!.answers.first(), ProvenanceOrigin.USER_AUTHORED, emptyList()))
        // Two clean independent answers on the Skill: canonical state really changes (`GRE-v0` §8), so the end replans.
        recordExplanation(1)
        recordExplanation(2)
        val plans = store.planVersions().size
        val reported = assertNotNull(runner.finish(run, before, listOf(answered), listOf(item)))
        assertTrue(reported.report.stateChanges.any { it.kind.id == "capability_confirmed" && it.skill == execution }, reported.report.stateChanges.toString())
        assertEquals(plans + 1, store.planVersions().size)
        // The new plan keeps the task the learner started, and re-resolves only the rest.
        val replanned = assertIs<BuildDailyPlan.Built.Planned>(reported.plan)
        assertEquals(listOf(run.position), replanned.trace.replan!!.preservedPositions)
    }

    /** The shipped content with one reading changed: what entry must say when the app cannot run a task. */
    private class Narrowed(val base: ContentPort, val noKeys: Boolean = false, val asAssessment: Boolean = false) : ContentPort by base {
        override fun answerKeyFor(item: VersionedRef) = if (noKeys) null else base.answerKeyFor(item)
        override fun authoredTask(ref: VersionedRef): AuthoredTask? = base.authoredTask(ref)?.let { task ->
            if (!asAssessment) task else task.copy(purpose = TaskPurpose.ASSESS, serves = TaskServing.servedBy(TaskPurpose.ASSESS).take(1).toSet())
        }
    }

    /** The real store, with every question already seen. */
    private class AllSeen(val base: SqlitePersistence) : PersistencePort by base {
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>) =
            resources.map { ExposureFact(it, null, ExposureFact.ITEM_VERSION_SEEN) }
    }

    /** The real store, whose gate makes every target wait on what pointer formation waits on. */
    private class Waiting(val base: SqlitePersistence) : PersistencePort by base {
        override fun prerequisiteEdgesInto(target: VersionedRef) =
            base.prerequisiteEdgesInto(VersionedRef("skill.c.pointer_formation", 1)).map { it.copy(target = target) }
    }

    /** The real store, which no longer knows the task's Skill. */
    private class SkillGone(val base: SqlitePersistence) : PersistencePort by base {
        override fun skill(ref: VersionedRef) = null
    }

    /** The real store, with no Skill published: no need can be open. */
    private class NoNeeds(val base: SqlitePersistence) : PersistencePort by base {
        override fun publishedSkills() = emptyList<coach.model.SkillRow>()
    }

    @Test
    fun `entry names what it could not confirm, and a task the app cannot run says why without blaming anyone`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        PlanToday(store, withContentQa, clock).ensure()
        val id = firstTaskId()
        val noKeys = OpenTask(store, Narrowed(withContentQa, noKeys = true), clock).open(id)
        assertEquals(CapabilityGap.NO_ITEM_THIS_APP_CAN_EVALUATE, noKeys.capabilityGap)
        assertNull(noKeys.run)
        assertEquals(CapabilityGap.ASSESSMENT_INTERIOR_NOT_AVAILABLE, OpenTask(store, Narrowed(withContentQa, asAssessment = true), clock).open(id).capabilityGap)
        assertEquals(CapabilityGap.NO_UNSEEN_ITEM, OpenTask(AllSeen(store), withContentQa, clock).open(id).capabilityGap)
        val noNeeds = OpenTask(NoNeeds(store), withContentQa, clock).open(id)
        assertEquals(false, noNeeds.needStillOpen)
        assertNull(noNeeds.run)
        assertTrue(noNeeds.stillSelected && noNeeds.contentCompatible)
        val waiting = OpenTask(Waiting(store), withContentQa, clock).open(id)
        assertEquals(false, waiting.prerequisitesSatisfied)
        assertNull(waiting.run)
        assertEquals(false, OpenTask(SkillGone(store), withContentQa, clock).open(id).contentCompatible)
    }

    @Test
    fun `the open needs entry reads are the very needs today's plan was built from`() {
        IngestCurriculum(store, withContentQa, clock).ingestAll()
        val built = assertIs<BuildDailyPlan.Built.Planned>(PlanToday(store, withContentQa, clock).ensure())
        assertEquals(built.trace.needs.map { it.needKey }.toSet(), BuildDailyPlan(store, withContentQa, clock).openNeeds().map { it.needKey }.toSet())
    }

    // ---------------------------------------------------------------- 16G — yardım ve ölçüm (HLPX-v0)

    /** The shipped course with its shipped suites and runner, exactly as `CoachApplication` reads them. */
    private val withSuites = FileContentSource(
        later = { listOf(python, c, memory, shellGit, english, assessment, contentQa) },
        codeSuites = { name -> File("src/main/assets/code_suites/$name").takeIf { it.isFile }?.readText() },
        codeRunner = { File("src/main/assets/code_test_runner.py").readText() },
        source = { first },
    )

    private fun shippedItems(): List<coach.model.AssessmentItem> =
        withSuites.curriculumPackages().flatMap { it.resources }.mapNotNull { withSuites.assessmentItem(it.ref) }

    /** What the course's runner prints when every test of the suite passes (`code_test_runner.py`, 14D). */
    private fun allPassed(suite: coach.model.CodeTestSuite): String = buildString {
        appendLine(coach.model.CodeTestReports.FORMAT)
        appendLine("item: ${suite.item}")
        appendLine("suite: ${suite.ref}")
        appendLine(if (suite.buildObjective != null) "build: ok" else "build: not_required")
        suite.tests.forEach { appendLine("test: ${it.id} passed") }
        appendLine("end")
    }

    /** A run of today's first task, aimed at [item]'s own Skill and Objectives: how the planner would hand it over. */
    private fun runFor(item: coach.model.AssessmentItem): coach.model.RunnableTask {
        IngestCurriculum(store, withSuites, clock).ingestAll()
        PlanToday(store, withSuites, clock).ensure()
        val run = assertNotNull(OpenTask(store, withSuites, clock).open(firstTaskId()).run)
        return run.copy(skill = item.targetSkills.first(), targetObjectives = item.targetObjectives, items = listOf(item.ref))
    }

    private class Tutor(private val text: String) : coach.ports.TutorPort {
        val asked = mutableListOf<coach.model.TutorRequest>()
        override fun assist(request: coach.model.TutorRequest): coach.model.TutorReply {
            asked += request
            return coach.model.TutorReply.Delivered(
                coach.model.TutorContent(request.intent, text, request.ceiling ?: coach.model.AssistanceLevel.H1, request.instructionMode),
                coach.model.TutorRef("test", "scripted", "tutor_instructions/3"),
            )
        }
    }

    @Test
    fun `every shipped suite is served for exactly its item version, and the runner ships as the course authored it`() {
        assertNull(withSuites.failure)
        val withTests = shippedItems().filter { withSuites.codeTestsFor(it.ref) != null }
        assertTrue(withTests.isNotEmpty())
        assertTrue(withTests.all { withSuites.codeSuiteText(it.ref) != null }, withTests.filter { withSuites.codeSuiteText(it.ref) == null }.map { it.ref }.toString())
        assertEquals(File("src/main/assets/code_suites").listFiles()!!.size, withTests.size, "no suite ships that no item version uses")
        assertEquals(File("../../tools/code_test_runner.py").readText(), withSuites.codeRunnerText())
        // A suite is never served for another version of its item, nor for an item with no tests.
        val item = withTests.first()
        assertNull(withSuites.codeSuiteText(item.ref.copy(version = item.ref.version + 100)))
        assertTrue(shippedItems().filter { withSuites.codeTestsFor(it.ref) == null }.all { withSuites.codeSuiteText(it.ref) == null })
        // Without the shipped suites a code item cannot be judged here, so it is not served (16F's reading).
        assertNull(coach.application.RunItems.kind(item, withContentQa))
        assertEquals(coach.model.RunItemKind.CODE_TESTS, coach.application.RunItems.kind(item, withSuites))
    }

    /** The Skills with at least one task whose items the app can serve and judge, read the way entry reads them. */
    private fun runnableSkills(content: ContentPort): Set<VersionedRef> {
        val server = coach.application.ServeDailyMicroItem(store, content, clock)
        return store.publishedSkills().map { it.ref }.filter { skill ->
            NeedTrigger.entries.flatMap { trigger -> content.taskCandidates(LearningNeed("${trigger.id}:$skill", trigger, listOf(skill), Criticality.REQUIRED)) }
                .mapNotNull { AuthoredTask.refOfCandidate(it.id)?.let(content::authoredTask) }
                .filter { it.purpose != TaskPurpose.ASSESS }
                .any { task -> task.items.any { ref -> content.assessmentItem(ref)?.let { coach.application.RunItems.kind(it, content) } != null &&
                    server.usable(ref, coach.application.OpenTask.INTENT, false) } }
        }.toSet()
    }

    @Test
    fun `how each shipped item is judged, by its key, the course's tests or a rubric, and a rubric that needs a verified result is not served`() {
        val kinds = shippedItems().groupingBy { coach.application.RunItems.kind(it, withSuites) }.eachCount()
        assertEquals(mapOf<coach.model.RunItemKind?, Int>(coach.model.RunItemKind.ANSWER_KEY to 618, coach.model.RunItemKind.RUBRIC to 14,
            coach.model.RunItemKind.CODE_TESTS to 480), kinds)
        // A rubric item whose task needs a verified result is not served: no AI is asked for it (14F).
        val rubric = shippedItems().first { coach.application.RunItems.kind(it, withSuites) == coach.model.RunItemKind.RUBRIC }
        val verified = object : ContentPort by withSuites {
            override fun assessmentItem(ref: VersionedRef) = withSuites.assessmentItem(ref)?.let {
                it.copy(evaluatorRequirement = it.evaluatorRequirement.copy(requiredStatus = coach.model.EvaluatorStatusRequirement.VERIFIED))
            }
        }
        assertNull(coach.application.RunItems.kind(verified.assessmentItem(rubric.ref)!!, verified))
    }

    @Test
    fun `with the shipped suites every Skill has a task the app can run, where an answer key alone left the coding Skills out`() {
        IngestCurriculum(store, withSuites, clock).ingestAll()
        val withKeys = runnableSkills(withContentQa)
        val withTests = runnableSkills(withSuites)
        val all = store.publishedSkills().map { it.ref }.toSet()
        assertEquals(60, all.size)
        assertEquals(36, withKeys.size, "16F: only answer-key items")
        assertEquals(all, withTests, (all - withTests).toString())
        assertTrue((withTests - withKeys).all { it.logicalId.startsWith("skill.python.") || it.logicalId.startsWith("skill.c.") })
    }

    @Test
    fun `a code answer is judged by the course's tests from the pasted report, and a report that measures nothing writes nothing`() {
        val item = shippedItems().first { withSuites.codeTestsFor(it.ref) != null && it.targetSkills.first().logicalId.startsWith("skill.python.") }
        val suite = assertNotNull(withSuites.codeTestsFor(item.ref))
        val run = runFor(item)
        val runner = RunTask(store, withSuites, clock, evaluatorAvailable = false)
        val code = "print(input())"
        val before = store.truthWatermark()
        assertIs<coach.model.RunAnswer.NotRecorded>(runner.answerCode(run, item, "  ", allPassed(suite), ProvenanceOrigin.USER_AUTHORED, emptyList())).also { assertTrue(it.blank) }
        assertIs<coach.model.RunAnswer.NotRecorded>(runner.answerCode(run, item, code, "not a report", ProvenanceOrigin.USER_AUTHORED, emptyList()))
        val other = shippedItems().first { it.ref != item.ref && withSuites.codeTestsFor(it.ref) != null }
        assertIs<coach.model.RunAnswer.NotRecorded>(runner.answerCode(run, item, code, allPassed(withSuites.codeTestsFor(other.ref)!!), ProvenanceOrigin.USER_AUTHORED, emptyList()))
        val environment = allPassed(suite).replace(Regex("build: \\w+"), "build: environment_error")
        if (suite.buildObjective != null) assertIs<coach.model.RunAnswer.NotRecorded>(runner.answerCode(run, item, code, environment, ProvenanceOrigin.USER_AUTHORED, emptyList()))
        assertEquals(before, store.truthWatermark(), "a report that measures nothing writes nothing")

        val recorded = assertIs<coach.model.RunAnswer.Recorded>(runner.answerCode(run, item, code, allPassed(suite), ProvenanceOrigin.USER_AUTHORED, emptyList()))
        assertEquals(AnswerResult.RIGHT, recorded.answered.result)
        assertEquals(false, recorded.answered.pending)
        assertNotNull(recorded.answered.artifactId, "the code is the attempt's artifact")
        assertEquals(setOf(run.plannedTaskId), store.startedPlannedTasks(listOf(run.plannedTaskId)))
        val rows = item.targetObjectives.flatMap { store.evidenceFor(it) }.filter { it.id in recorded.answered.evidenceIds }
        assertTrue(rows.isNotEmpty() && rows.all { it.evaluatorStatus.id == "verified" && it.outcome.id == "positive" }, rows.toString())
        assertEquals(coach.model.ComprehensionOffer.NotOffered, recorded.offer, "the learner wrote it: nothing to check")
    }

    /** The shipped course plus one written comprehension check for [item], as a later package would author it. */
    private class WithCheck(val base: ContentPort, val check: coach.model.ComprehensionCheck) : ContentPort by base {
        override fun comprehensionChecksFor(item: VersionedRef) = if (item == check.item) listOf(check) else base.comprehensionChecksFor(item)
    }

    @Test
    fun `code the learner did not write alone is followed by the comprehension check, and a right written choice is evidence of its own kind`() {
        val item = shippedItems().first { withSuites.codeTestsFor(it.ref) != null && it.targetSkills.first().logicalId.startsWith("skill.python.") }
        val run = runFor(item)
        // No shipped package authors a written check yet, so what follows is the tutor's practice (14E): never evidence.
        assertTrue(shippedItems().all { withSuites.comprehensionChecksFor(it.ref).isEmpty() })
        val practice = assertIs<coach.model.RunAnswer.Recorded>(RunTask(store, withSuites, clock).answerCode(run, item, "print(1)",
            allPassed(withSuites.codeTestsFor(item.ref)!!), ProvenanceOrigin.GENERATED_OR_COPIED, emptyList()))
        assertEquals(coach.model.ComprehensionOffer.TutorPractice, practice.offer)

        val authored = coach.model.ComprehensionCheck(
            VersionedRef("comprehension.test.line_purpose", 1), item.ref, item.targetObjectives.first(), coach.model.ComprehensionKind.LINE_PURPOSE,
            "explanation", "Bu satır ne yapar?", mapOf("a" to "Girdiyi yazdırır.", "b" to "Hiçbir şey yapmaz."), "a",
        )
        val content = WithCheck(withSuites, authored)
        val recorded = assertIs<coach.model.RunAnswer.Recorded>(RunTask(store, content, clock).answerCode(run, item, "print(2)",
            allPassed(withSuites.codeTestsFor(item.ref)!!), ProvenanceOrigin.GENERATED_OR_COPIED, emptyList()))
        val offer = assertIs<coach.model.ComprehensionOffer.Written>(recorded.offer)
        val check = offer.checks.first()
        val help = coach.application.RunHelp(store, content, clock, coach.application.NullTutor)
        val before = store.truthWatermark()
        assertIs<coach.model.ComprehensionResult.Skipped>(help.comprehensionAnswer(run, item, check, null, recorded.answered.attemptId))
        assertEquals(before, store.truthWatermark(), "a skip writes nothing")
        val measured = assertIs<coach.model.ComprehensionResult.Measured>(help.comprehensionAnswer(run, item, check, check.answer, recorded.answered.attemptId))
        assertTrue(measured.correct)
        val row = store.evidenceFor(check.objective).single { it.evidenceType == check.evidenceType && it.sequence > before }
        // Answered alone, with no help; a teaching task is still practice by design (`2D` §6).
        assertEquals(if (run.purpose == TaskPurpose.TEACH) "practice_only" else "independent", row.independenceClass.id)
    }

    @Test
    fun `an open answer waits when no evaluator answers, is judged again only when asked, and the rubric is the learner's own check`() {
        val item = shippedItems().first {
            withSuites.answerKeyFor(it.ref) == null && withSuites.codeTestsFor(it.ref) == null &&
                coach.application.RunItems.kind(it, withSuites) == coach.model.RunItemKind.RUBRIC
        }
        val run = runFor(item)
        val pending = assertIs<coach.model.RunAnswer.Recorded>(RunTask(store, withSuites, clock, evaluatorAvailable = true)
            .answerOpen(run, item, "Kendi cümlelerimle bir açıklama.", ProvenanceOrigin.USER_AUTHORED, emptyList()))
        assertTrue(pending.answered.pending)
        assertEquals(AnswerResult.NOT_MEASURED, pending.answered.result)
        assertTrue(pending.answered.evidenceIds.isEmpty(), "no evaluator: no evidence, and not a wrong answer")

        val criteria = assertNotNull(withSuites.rubricFor(item.ref)).criteria
        val ai = object : coach.ports.EvaluatorPort {
            override fun evaluate(request: coach.ports.EvaluationRequest) = coach.model.EvaluationResult.Provisional(
                item.targetObjectives.map { coach.model.ComponentResult(it, coach.model.OutcomeSignal.MET) },
                coach.model.EvaluatorRef("test", "scripted", "open_response_evaluation/1"),
                rubricFindings = criteria.map { coach.model.RubricFinding(it.id, coach.model.CriterionVerdict.MET) },
            )
        }
        val judged = assertIs<coach.model.RunAnswer.Recorded>(RunTask(store, withSuites, clock, evaluatorAvailable = true, evaluator = ai)
            .reevaluate(run, item, pending.answered, "Kendi cümlelerimle bir açıklama."))
        assertEquals(pending.answered.attemptId, judged.answered.attemptId, "the same frozen attempt, never a second answer")
        assertTrue(judged.answered.evidenceIds.isNotEmpty() && !judged.answered.pending)
        val rows = item.targetObjectives.flatMap { store.evidenceFor(it) }.filter { it.id in judged.answered.evidenceIds }
        assertTrue(rows.all { it.evaluatorStatus.id == "provisional" }, "an AI's judgement is provisional at most")
        assertTrue(rows.all { it.independenceClass == pending.answered.independence }, "judged later with the independence recorded with it")

        // The answer is frozen before any evaluator is asked: the attempt is already in the store when it judges.
        val before = store.truthWatermark()
        var storedWhenAsked = false
        val watching = object : coach.ports.EvaluatorPort {
            override fun evaluate(request: coach.ports.EvaluationRequest) = ai.evaluate(request).also { storedWhenAsked = store.truthWatermark() > before }
        }
        val direct = assertIs<coach.model.RunAnswer.Recorded>(RunTask(store, withSuites, clock, evaluatorAvailable = true, evaluator = watching)
            .answerOpen(run, item, "Başka bir açıklama.", ProvenanceOrigin.USER_AUTHORED, emptyList()))
        assertTrue(storedWhenAsked, "frozen first")
        assertTrue(!direct.answered.pending && direct.answered.evidenceIds.isNotEmpty())

        val shown = RunTask(store, withSuites, clock).selfCheck(item, pending.answered.attemptId)
        assertEquals(criteria, shown)
        assertTrue(store.exposuresFor(listOf(item.ref), listOf(item.variantFamilyId)).any { it.kind == ExposureFact.SOLUTION_EXPOSURE })
    }

    @Test
    fun `help after an answer froze is recorded on that attempt, and help that was not shown is recorded nowhere`() {
        IngestCurriculum(store, withSuites, clock).ingestAll()
        PlanToday(store, withSuites, clock).ensure()
        val run = assertNotNull(OpenTask(store, withSuites, clock).open(firstTaskId()).run)
        val runner = RunTask(store, withSuites, clock)
        val item = assertNotNull(runner.serve(run, emptySet()))
        val prompt = withSuites.resource(item.ref)?.body.orEmpty()
        val wrong = assertNotNull(runner.answer(run, item, "zzz", ProvenanceOrigin.USER_AUTHORED, emptyList()))
        val ask = coach.application.RunHelp.askFor(run, item, prompt, coach.model.TutorIntent.EXPLAIN_MISTAKE, wrong, "zzz")
        assertEquals(coach.model.AssistanceTiming.AFTER_FAILURE, ask.timing)
        assertNull(ask.ceiling, "no level is asked once the answer froze")
        assertEquals("zzz", ask.context.learnerWork)

        val before = store.truthWatermark()
        val unavailable = coach.application.RunHelp(store, withSuites, clock, coach.application.NullTutor).ask(ask.copy(consequenceAcknowledged = true), item, wrong.attemptId)
        assertIs<coach.model.TutorOutcome.NotShown>(assertIs<coach.application.RunHelp.Asked.Answered>(unavailable).outcome)
        assertEquals(before, store.truthWatermark(), "nothing shown, nothing recorded")

        // After the answer froze the explanation may show the solution, so the consequence is said first (14A).
        val tutor = Tutor("Şu adımı karıştırdın.")
        val help = coach.application.RunHelp(store, withSuites, clock, tutor)
        assertIs<coach.application.RunHelp.Asked.NotReady>(help.ask(ask, item, wrong.attemptId))
        assertTrue(tutor.asked.isEmpty(), "nothing is sent before the rules say the ask is ready")
        assertIs<coach.model.TutorOutcome.Shown>(assertIs<coach.application.RunHelp.Asked.Answered>(help.ask(ask.copy(consequenceAcknowledged = true), item, wrong.attemptId)).outcome)
        val events = generateSequence(1L) { it + 1 }.map { store.readTruth("assistance_event", it) }.takeWhile { it != null }.filterNotNull().toList()
        assertEquals(listOf(wrong.attemptId.toString()), events.map { it.payload["attempt_id"] })
        assertEquals("after_failure", events.single().payload["timing"])
        // The frozen answer keeps what it showed.
        assertEquals("negative", item.targetObjectives.flatMap { store.evidenceFor(it) }.single { it.id == wrong.evidenceIds.first() }.outcome.id)
    }

    @Test
    fun `a pause between questions writes one checkpoint the planner can read, and nothing is written when nothing is left`() {
        IngestCurriculum(store, withSuites, clock).ingestAll()
        PlanToday(store, withSuites, clock).ensure()
        val run = assertNotNull(OpenTask(store, withSuites, clock).open(firstTaskId()).run)
        val help = coach.application.RunHelp(store, withSuites, clock, coach.application.NullTutor)
        val a = VersionedRef("item.x.one", 1)
        val b = VersionedRef("item.x.two", 1)
        val before = store.truthWatermark()
        assertNull(help.pause(run, coach.model.CheckpointKind.CHECKPOINT_PAUSE, listOf(a), emptyList()))
        assertNull(help.pause(run, coach.model.CheckpointKind.CHECKPOINT_PAUSE, emptyList(), listOf(a)))
        assertEquals(before, store.truthWatermark())
        val id = assertNotNull(help.pause(run, coach.model.CheckpointKind.CHECKPOINT_PAUSE, listOf(a), listOf(b)))
        val context = assertNotNull(coach.application.ResumeCheckpoints(store, clock).read(id)?.context)
        assertEquals(run.needKey, context.learningNeedKey)
        assertEquals(listOf(a.toString()), context.completedSegments)
        assertEquals(listOf(b.toString()), context.remainingSegments)
        assertEquals("planned_task:${run.plannedTaskId}", context.sourceTaskId)
        assertEquals(coach.model.CheckpointKind.CHECKPOINT_PAUSE, context.kind)
        // High-stakes work pauses marked, as PausePolicy decided — never continued as the same independent attempt.
        val marked = assertNotNull(help.pause(run, coach.model.CheckpointKind.HIGH_STAKES_PAUSE, listOf(a), listOf(b)))
        assertEquals(coach.model.CheckpointKind.HIGH_STAKES_PAUSE, coach.application.ResumeCheckpoints(store, clock).read(marked)?.context?.kind)
    }
}
