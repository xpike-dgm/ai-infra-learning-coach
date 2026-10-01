package coach.application

import coach.model.AssessmentItem
import coach.model.CheckpointKind
import coach.model.CurriculumPackage
import coach.model.DailyCapacityInput
import coach.model.EvidenceRow
import coach.model.GenerationKind
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MasteryAxisState
import coach.model.ObjectiveEvidenceProfile
import coach.model.PlanTraceCodec
import coach.model.PrerequisiteEdge
import coach.model.PriorityBand
import coach.model.PublishOutcome
import coach.model.ReplanTrigger
import coach.model.ResourceVersion
import coach.model.ResumeContext
import coach.model.ResumeContextCodec
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StoredPlannedTask
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
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Replacing a plan (12D): a new version with a reason, never an edit. The store here keeps what it is
 * given, so each test sees the plans earlier calls really appended.
 */
class ReplanTest {

    private val pointer = VersionedRef("skill.c.pointer_dereference", 1)
    private val linux = VersionedRef("skill.linux.filesystem_navigation", 1)
    private val shell = VersionedRef("skill.linux.shell_basics", 1)
    private val capacity = DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)

    private class Store : PersistencePort {
        val skills = mutableListOf<SkillRow>()
        val projections = mutableMapOf<String, ProjectionRecord>()
        val rows = mutableListOf<StoredTruth>()
        var transactions = 0
        private var inside = false
        private var nextId = 1L

        fun appended(kind: String) = rows.filter { it.record.kind == kind }

        override fun <T> inTransaction(block: () -> T): T {
            transactions += 1
            inside = true
            try { return block() } finally { inside = false }
        }
        override fun appendTruth(record: TruthRecord): Long {
            check(inside) { "${record.kind} was appended outside a transaction" }
            val id = nextId++
            rows += StoredTruth(id, record)
            return id
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = rows.firstOrNull { it.id == id && it.record.kind == kind }?.record
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) = error("planning writes no projection")
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = error("planning reads no evidence")
        override fun truthWatermark(): Long = rows.size.toLong()
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = skills.sortedBy { it.ref.logicalId }
        override fun latestPlan(): StoredPlan? {
            val plan = appended("plan_version").lastOrNull() ?: return null
            val tasks = appended("planned_task").filter { it.record.payload["plan_version_id"] == plan.id.toString() }
            val trace = appended("planner_decision_trace").lastOrNull { it.record.payload["plan_version_id"] == plan.id.toString() }
            return StoredPlan(plan.id, plan.record.recordedAt, tasks.size, trace?.record?.payload?.get("trace"),
                tasks.map { row ->
                    StoredPlannedTask(row.id, row.record.payload.getValue("position").toInt(),
                        VersionedRef(row.record.payload.getValue("skill_logical_id"), row.record.payload.getValue("skill_version").toInt()))
                }.sortedBy { it.position })
        }
        override fun resumeCheckpointRows(): List<StoredTruth> = appended("resume_checkpoint")
        override fun latestAssessmentSessionIn(scope: coach.model.AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: coach.model.AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<coach.model.ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()

        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()


        override fun objectivesOf(skill: VersionedRef): List<coach.model.ObjectiveRow> = emptyList()
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()
    }

    private class Content(val tasks: Map<String, List<TaskCandidate>>) : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = tasks[need.needKey].orEmpty()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = emptyList()
    }

    private class Clock(var day: String) : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, day, 3 * 3600)
    }

    private fun Store.publish(ref: VersionedRef) {
        skills += SkillRow(ref, ref.logicalId, "capability", "published", "concept", "standard", false, "src", "authored")
    }

    private fun Store.developing(ref: VersionedRef) {
        val key = "skill_state:${ref.logicalId}@v${ref.version}"
        projections[key] = ProjectionRecord(key, "GRE-v0", 1, 1, 1, mapOf(
            "mastery_axis_state" to MasteryAxisState.DEVELOPING_INDEPENDENT.id, "retention_axis_state" to "not_yet_evaluated",
            "prerequisite_axis_state" to "not_yet_evaluated", "weakness_axis_state" to "not_yet_evaluated",
            "primary_presentation_state" to "x",
        ))
    }

    private fun Store.pause(context: ResumeContext, clock: Clock) = inTransaction {
        appendTruth(TruthRecord("resume_checkpoint", clock.now(), mapOf("context" to ResumeContextCodec.encode(context))))
    }

    private fun task(needKey: String, skill: VersionedRef, minutes: Int, id: String = "t-$needKey") =
        TaskCandidate(id, needKey, TaskPurpose.PRACTICE, "coding", "Task", skill, minutes, LifecycleStatus.VALIDATED)

    /** Three open needs of 20 minutes each, in a 60-minute day: 54 planning minutes hold two of them. */
    private fun world(): Triple<Store, Content, Clock> {
        val store = Store().apply { publish(pointer); publish(linux); publish(shell) }
        val content = Content(mapOf(
            "new_learning:$pointer" to listOf(task("new_learning:$pointer", pointer, 20)),
            "new_learning:$linux" to listOf(task("new_learning:$linux", linux, 20)),
            "new_learning:$shell" to listOf(task("new_learning:$shell", shell, 20)),
        ))
        return Triple(store, content, Clock("2026-09-28"))
    }

    @Test
    fun `asked again on the same day with no event, the planner keeps today's plan`() {
        val (store, content, clock) = world()
        val first = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        val rowsBefore = store.rows.size
        assertEquals(BuildDailyPlan.Built.AlreadyPlanned(first.planVersionId), BuildDailyPlan(store, content, clock).build(capacity))
        assertEquals(rowsBefore, store.rows.size)
    }

    @Test
    fun `a replan keeps what was started and solves only the rest`() {
        val (store, content, clock) = world()
        val first = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        assertEquals(2, first.trace.selected.size)
        val startedNeed = first.trace.selected[0].needKey

        val second = BuildDailyPlan(store, content, clock).replan(
            BuildDailyPlan.ReplanRequest(ReplanTrigger.TASK_FINISHED_EARLY, capacity, keptPositions = setOf(0)),
        ) as BuildDailyPlan.Built.Planned

        assertEquals(GenerationKind.REPLAN.id, second.trace.generationKind)
        val kept = second.trace.selected.first()
        assertTrue(kept.preserved)
        assertEquals(first.trace.selected[0].candidateId, kept.candidateId)
        // The kept task's need is not planned twice.
        assertEquals(1, second.trace.selected.count { it.needKey == startedNeed })
        val record = second.trace.replan!!
        assertEquals(first.planVersionId, record.previousPlanVersionId)
        assertEquals(listOf(1), record.invalidatedPositions)
        assertEquals(40, record.remainderHardMinutes)
        assertTrue(second.trace.invariantChecks.values.all { it }, second.trace.invariantChecks.toString())
        // A new version was appended; the first one is exactly as it was.
        assertEquals(2, store.appended("plan_version").size)
        assertEquals(first.trace, PlanTraceCodec.decode(store.appended("planner_decision_trace").first().record.payload.getValue("trace")))
    }

    @Test
    fun `after a replan the day's budget still counts the work that was kept`() {
        // Five 13-minute needs: a first plan fits four (52 of 54 minutes).
        val skills = (1..5).map { VersionedRef("skill.extra.s$it", 1) }
        val store = Store().apply { skills.forEach { publish(it) } }
        val content = Content(skills.associate { "new_learning:$it" to listOf(task("new_learning:$it", it, 13)) })
        val clock = Clock("2026-09-28")
        BuildDailyPlan(store, content, clock).build(capacity)
        val replanned = BuildDailyPlan(store, content, clock).replan(
            BuildDailyPlan.ReplanRequest(ReplanTrigger.NEW_EVIDENCE_RECORDED, capacity, keptPositions = setOf(0)),
        ) as BuildDailyPlan.Built.Planned
        // 13 kept plus what the remaining 47 minutes hold is more than the remainder, and within the day.
        assertTrue(replanned.trace.selected.sumOf { it.plannedMinutes } > replanned.trace.capacity.hardBudgetMinutes)
        assertTrue(replanned.trace.invariantChecks.getValue("day_within_hard_budget"))
    }

    @Test
    fun `a declared remaining time is the whole remaining budget`() {
        val (store, content, clock) = world()
        BuildDailyPlan(store, content, clock).build(capacity)
        val replanned = BuildDailyPlan(store, content, clock).replan(
            BuildDailyPlan.ReplanRequest(ReplanTrigger.SESSION_REMAINING_TIME_CHANGED, capacity, remainingMinutes = 25),
        ) as BuildDailyPlan.Built.Planned
        assertEquals(25, replanned.trace.capacity.hardBudgetMinutes)
        assertEquals(22, replanned.trace.capacity.planningBudgetMinutes)
        assertEquals(1, replanned.trace.selected.size)
        assertEquals(listOf("replan.remaining_time_changed"), replanned.trace.planReasonCodes.take(1))
    }

    @Test
    fun `a smaller capacity today never undoes work already done`() {
        val (store, content, clock) = world()
        BuildDailyPlan(store, content, clock).build(capacity)
        val replanned = BuildDailyPlan(store, content, clock).replan(
            BuildDailyPlan.ReplanRequest(ReplanTrigger.TODAY_CAPACITY_CHANGED, capacity.copy(todayOverrideMinutes = 10), keptPositions = setOf(0, 1)),
        ) as BuildDailyPlan.Built.Planned
        assertEquals(listOf(true, true), replanned.trace.selected.map { it.preserved })
        assertEquals(0, replanned.trace.capacity.hardBudgetMinutes)
        assertEquals(false, replanned.trace.invariantChecks.getValue("day_within_hard_budget"))
    }

    @Test
    fun `a replan that cannot be made honestly is refused and writes nothing`() {
        val (store, content, clock) = world()
        BuildDailyPlan(store, content, clock).build(capacity)
        val before = store.rows.size
        assertIs<BuildDailyPlan.Built.Refused>(BuildDailyPlan(store, content, clock).replan(
            BuildDailyPlan.ReplanRequest(ReplanTrigger.TASK_COMPLETED, capacity, keptPositions = setOf(5))))
        assertIs<BuildDailyPlan.Built.Refused>(BuildDailyPlan(store, content, clock).replan(
            BuildDailyPlan.ReplanRequest(ReplanTrigger.USER_REQUESTED_EXTRA_TIME, capacity)))
        assertEquals(before, store.rows.size)
    }

    @Test
    fun `a plan that cannot be read is not replaced by guesswork, but a new day still starts`() {
        val (store, content, clock) = world()
        store.inTransaction {
            val plan = store.appendTruth(TruthRecord("plan_version", clock.now(), mapOf("policy_version" to "PLNX-v0")))
            store.appendTruth(TruthRecord("planned_task", clock.now(), mapOf("plan_version_id" to plan.toString(),
                "skill_logical_id" to pointer.logicalId, "skill_version" to "1", "position" to "0")))
            store.appendTruth(TruthRecord("planner_decision_trace", clock.now(), mapOf("plan_version_id" to plan.toString(), "trace" to "garbage")))
        }
        assertIs<BuildDailyPlan.Built.Refused>(BuildDailyPlan(store, content, clock).replan(
            BuildDailyPlan.ReplanRequest(ReplanTrigger.NEW_EVIDENCE_RECORDED, capacity)))
        clock.day = "2026-09-29"
        val back = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        assertEquals(GenerationKind.REENTRY.id, back.trace.generationKind)
        assertEquals(1, back.trace.reentry!!.stalePlannedTaskCount)
    }

    @Test
    fun `a new study day is re-entry, and yesterday's plan is not replayed`() {
        val (store, content, clock) = world()
        val yesterday = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        clock.day = "2026-10-05"
        // Even an event on a new day does not turn yesterday's plan into today's.
        val today = BuildDailyPlan(store, content, clock).replan(
            BuildDailyPlan.ReplanRequest(ReplanTrigger.TASK_COMPLETED, capacity, keptPositions = setOf(0)),
        ) as BuildDailyPlan.Built.Planned

        assertEquals(GenerationKind.REENTRY.id, today.trace.generationKind)
        assertTrue(today.trace.selected.none { it.preserved })
        // Yesterday's reported task does not shape today: its need is planned again from current state,
        // exactly as a first plan would plan it.
        assertEquals(yesterday.trace.selected.map { it.needKey }, today.trace.selected.map { it.needKey })
        val context = today.trace.reentry!!
        assertEquals(yesterday.planVersionId, context.previousPlanVersionId)
        assertEquals(7, context.absenceStudyDays)
        assertEquals(2, context.stalePlannedTaskCount)
        assertTrue("reentry.stale_plan_not_replayed" in today.trace.planReasonCodes)
        assertTrue("reentry.absence_not_task_debt" in today.trace.planReasonCodes)
        // Absence changed nothing about the day's size: the same 54 planning minutes, not a backlog.
        assertEquals(54, today.trace.capacity.planningBudgetMinutes)
        assertNull(today.trace.replan)
    }

    @Test
    fun `a safe pause makes its need paused work, and a high-stakes pause is not resumed`() {
        val (store, _, clock) = world()
        store.developing(pointer)
        store.developing(linux)
        val content = Content(mapOf(
            "continue_learning:$pointer" to listOf(task("continue_learning:$pointer", pointer, 10)),
            "continue_learning:$linux" to listOf(task("continue_learning:$linux", linux, 10)),
        ))
        store.pause(ResumeContext(CheckpointKind.CHECKPOINT_PAUSE, "continue_learning:$pointer", "task-1", "cp1", listOf("a"), listOf("b")), clock)
        store.pause(ResumeContext(CheckpointKind.HIGH_STAKES_PAUSE, "continue_learning:$linux", "task-2", "cp1", listOf("a"), listOf("b")), clock)
        store.inTransaction { store.appendTruth(TruthRecord("resume_checkpoint", clock.now(), mapOf("context" to "not a context"))) }

        val built = BuildDailyPlan(store, content, clock).build(capacity) as BuildDailyPlan.Built.Planned
        val bands = built.trace.needs.associate { it.needKey to it.band }
        assertEquals(PriorityBand.P2, bands.getValue("continue_learning:$pointer"))
        assertEquals(PriorityBand.P3, bands.getValue("continue_learning:$linux"))
        // Paused work is ranked, not replayed: it is first because P2 comes before P3.
        assertEquals("continue_learning:$pointer", built.trace.selected.first().needKey)
    }
}
