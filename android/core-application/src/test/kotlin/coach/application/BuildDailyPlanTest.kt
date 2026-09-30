package coach.application

import coach.model.AssessmentItem
import coach.model.CurriculumPackage
import coach.model.Criticality
import coach.model.DailyCapacityInput
import coach.model.EvidenceRow
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.NeedDisposition
import coach.model.ObjectiveEvidenceProfile
import coach.model.PlanTraceCodec
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StudyTimestamp
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.ContentDocument
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import coach.model.StoredPlan
import coach.ports.StoredTruth

/** Planning from current state and appending the plan as truth. SQLite itself is proven in T2. */
class BuildDailyPlanTest {

    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val linkedList = VersionedRef("skill.c.linked_list_insert", 1)
    private val linux = VersionedRef("skill.linux.filesystem_navigation", 1)
    private val shell = VersionedRef("skill.linux.shell_basics", 1)
    private val capacity = DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)

    private class Store : PersistencePort {
        val calls = mutableListOf<String>()
        val skills = mutableListOf<SkillRow>()
        val edges = mutableListOf<PrerequisiteEdge>()
        val projections = mutableMapOf<String, ProjectionRecord>()
        val appended = mutableListOf<TruthRecord>()
        var transactions = 0
        private var inside = false
        var curriculumVersion: Int? = 2
        private var nextId = 100L

        override fun <T> inTransaction(block: () -> T): T {
            transactions += 1
            inside = true
            try { return block() } finally { inside = false }
        }
        override fun appendTruth(record: TruthRecord): Long {
            check(inside) { "${record.kind} was appended outside the plan's transaction" }
            appended += record
            return nextId++
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? { calls += "state"; return projections[key] }
        override fun writeProjection(record: ProjectionRecord) = error("planning writes no projection")
        override fun curriculumPublished(): Boolean = curriculumVersion != null
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = error("planning reads no evidence")
        override fun truthWatermark(): Long { calls += "watermark"; return 77 }
        override fun latestCurriculumVersion(): Int? = curriculumVersion
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = edges.filter { it.target == target }
        override fun publishedSkills(): List<SkillRow> { calls += "skills"; return skills.sortedBy { it.ref.logicalId } }
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
    }

    private class Content(val tasks: Map<String, List<TaskCandidate>> = emptyMap()) : ContentPort {
        val asked = mutableListOf<LearningNeed>()
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> { asked += need; return tasks[need.needKey].orEmpty() }
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, "2026-09-28", 3 * 3600)
    }

    private fun Store.publish(ref: VersionedRef, lifecycle: String = "published", critical: Boolean = false) {
        skills += SkillRow(ref, ref.logicalId, "capability", lifecycle, "concept", "standard", critical, "src", "authored")
    }

    private fun Store.mastery(ref: VersionedRef, state: MasteryAxisState) {
        val key = "skill_state:${ref.logicalId}@v${ref.version}"
        projections[key] = ProjectionRecord(key, "GRE-v0", 40, 1, 2, mapOf(
            "mastery_axis_state" to state.id, "retention_axis_state" to "not_yet_evaluated",
            "prerequisite_axis_state" to "not_yet_evaluated", "weakness_axis_state" to "not_yet_evaluated",
            "primary_presentation_state" to state.id,
        ))
    }

    private fun task(id: String, needKey: String, skill: VersionedRef, minutes: Int = 20, purpose: TaskPurpose = TaskPurpose.TEACH) =
        TaskCandidate(id, needKey, purpose, "reading", "Lesson $id", skill, minutes, LifecycleStatus.VALIDATED)

    @Test
    fun `with nothing published nothing is planned and nothing is written`() {
        val store = Store().apply { curriculumVersion = null }
        assertIs<BuildDailyPlan.Built.NothingPublished>(BuildDailyPlan(store, Content(), clock).build(capacity))
        assertTrue(store.appended.isEmpty())
        assertEquals(0, store.transactions)
    }

    @Test
    fun `the plan, its tasks and its trace are appended together in one transaction`() {
        val store = Store().apply { publish(linux); publish(pointer) }
        val content = Content(mapOf(
            "new_learning:$linux" to listOf(task("linux-1", "new_learning:$linux", linux)),
            "new_learning:$pointer" to listOf(task("ptr-1", "new_learning:$pointer", pointer)),
        ))
        val built = BuildDailyPlan(store, content, clock).build(capacity)
        assertIs<BuildDailyPlan.Built.Planned>(built)

        assertEquals(1, store.transactions)
        assertEquals(listOf("plan_version", "planned_task", "planned_task", "planner_decision_trace"), store.appended.map { it.kind })
        assertEquals(mapOf("policy_version" to "PLNX-v0"), store.appended[0].payload)
        val tasks = store.appended.filter { it.kind == "planned_task" }
        assertTrue(tasks.all { it.payload.getValue("plan_version_id") == built.planVersionId.toString() })
        assertEquals(listOf("0", "1"), tasks.map { it.payload.getValue("position") })
        assertEquals(built.trace.selected.map { it.primarySkill.logicalId }, tasks.map { it.payload.getValue("skill_logical_id") })
        // Everything planned_task has no column for is in the trace, and it reads back exactly.
        val stored = store.appended.last().payload.getValue("trace")
        assertEquals(built.trace, PlanTraceCodec.decode(stored))
        assertEquals(2, built.trace.curriculumVersion)
        assertEquals("2026-09-28", built.trace.studyDay)
        assertTrue(store.appended.all { it.recordedAt == clock.now() })
    }

    @Test
    fun `the watermark is read before any state, so the plan never claims to have seen more`() {
        val store = Store().apply { publish(linux) }
        BuildDailyPlan(store, Content(), clock).build(capacity)
        assertEquals("watermark", store.calls.first())
        assertEquals(77, (BuildDailyPlan(store, Content(), clock).build(capacity) as BuildDailyPlan.Built.Planned).trace.truthWatermark)
    }

    @Test
    fun `with no authored task the need is recorded as unserved and the empty plan is still written`() {
        val store = Store().apply { publish(linux) }
        val built = BuildDailyPlan(store, Content(), clock).build(capacity) as BuildDailyPlan.Built.Planned
        assertTrue(built.trace.selected.isEmpty())
        assertEquals(NeedDisposition.NO_VALID_CANDIDATE, built.trace.needs.single().disposition)
        assertEquals(listOf("plan_version", "planner_decision_trace"), store.appended.map { it.kind })
    }

    @Test
    fun `needs come from current state and only Skills on the route open one`() {
        val store = Store().apply {
            publish(linux)
            publish(pointer)
            publish(linkedList, lifecycle = "draft")
            mastery(pointer, MasteryAxisState.CONFIRMED_CURRENT)
        }
        val content = Content()
        val built = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        assertEquals(listOf("new_learning:$linux"), content.asked.map { it.needKey })
        assertEquals(1, built.trace.skillsNotOnRoute)
    }

    @Test
    fun `a candidate is asked about at the prerequisite gate before it is ranked`() {
        val store = Store().apply {
            publish(pointer)
            publish(linkedList)
            edges += PrerequisiteEdge(pointer, linkedList, 1, "hard", "conceptual_dependency", "default_prg_v0", "published", "authored")
            mastery(pointer, MasteryAxisState.DEVELOPING_INDEPENDENT)
        }
        val content = Content(mapOf(
            "new_learning:$linkedList" to listOf(task("ll-1", "new_learning:$linkedList", linkedList)),
            "continue_learning:$pointer" to listOf(task("ptr-1", "continue_learning:$pointer", pointer, purpose = TaskPurpose.PRACTICE)),
        ))
        val built = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        assertEquals(listOf("ptr-1"), built.trace.selected.map { it.candidateId })
        assertEquals(NeedDisposition.BLOCKED, built.trace.needs.single { it.needKey == "new_learning:$linkedList" }.disposition)
    }

    @Test
    fun `a task's own requirements and its strict request reach the gate`() {
        val store = Store().apply {
            publish(linux)
            publish(shell)
            publish(pointer)
            publish(linkedList)
            edges += PrerequisiteEdge(pointer, linkedList, 1, "hard", "conceptual_dependency", "default_prg_v0", "published", "authored")
            // Contradicted but not critical: a normal edge leaves the dependent conditionally eligible.
            mastery(pointer, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE)
        }
        val content = Content(mapOf(
            // The graph says nothing about the shell for this Linux task; the task itself needs it, and the
            // shell has never been learned.
            "new_learning:$linux" to listOf(task("linux-1", "new_learning:$linux", linux).copy(requiredSkills = listOf(shell))),
            "new_learning:$linkedList" to listOf(
                task("ll-strict", "new_learning:$linkedList", linkedList).copy(requiresStrictPrerequisiteConfidence = true)),
        ))
        val built = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        val byNeed = built.trace.needs.associateBy { it.needKey }
        assertEquals(NeedDisposition.BLOCKED, byNeed.getValue("new_learning:$linux").disposition)
        assertEquals(NeedDisposition.BLOCKED, byNeed.getValue("new_learning:$linkedList").disposition)
    }

    @Test
    fun `the weakness axis and the critical flag are read from the store`() {
        val store = Store().apply {
            publish(pointer, critical = true)
            val key = "skill_state:${pointer.logicalId}@v1"
            projections[key] = ProjectionRecord(key, "GRE-v0", 40, 1, 2, mapOf(
                "mastery_axis_state" to MasteryAxisState.CONFIRMATION_VERIFICATION_DUE.id,
                "retention_axis_state" to "not_yet_evaluated", "prerequisite_axis_state" to "not_yet_evaluated",
                "weakness_axis_state" to "remediation_required", "primary_presentation_state" to "x",
            ))
        }
        val content = Content()
        val built = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        assertEquals(listOf("remediation_required:$pointer", "verification_due:$pointer"), content.asked.map { it.needKey })
        assertTrue(built.trace.needs.all { it.rank.criticality == Criticality.CRITICAL_PREREQUISITE })
    }

    @Test
    fun `the same state plans the same way twice`() {
        fun run(): String {
            val store = Store().apply { publish(linux); publish(pointer) }
            val content = Content(mapOf("new_learning:$linux" to listOf(task("linux-1", "new_learning:$linux", linux))))
            BuildDailyPlan(store, content, clock).build(capacity)
            return store.appended.last().payload.getValue("trace")
        }
        assertEquals(run(), run())
    }
}
