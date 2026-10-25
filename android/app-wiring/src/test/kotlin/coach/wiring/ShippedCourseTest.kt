package coach.wiring

import coach.application.BuildDailyPlan
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
}
