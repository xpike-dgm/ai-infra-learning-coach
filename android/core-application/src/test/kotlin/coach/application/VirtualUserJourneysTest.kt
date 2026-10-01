package coach.application

import coach.engines.virtual.VirtualUsers
import coach.engines.virtual.VirtualUsers.Learner
import coach.engines.virtual.VirtualUsers.cArrays
import coach.engines.virtual.VirtualUsers.cFunctions
import coach.engines.virtual.VirtualUsers.linkedList
import coach.engines.virtual.VirtualUsers.needKey
import coach.engines.virtual.VirtualUsers.pointer
import coach.engines.virtual.VirtualUsers.task
import coach.model.AssessmentItem
import coach.model.CheckpointKind
import coach.model.ContinuationValue
import coach.model.CurriculumPackage
import coach.model.DailyCapacityInput
import coach.model.EvidenceRow
import coach.model.GenerationKind
import coach.model.LearningNeed
import coach.model.MasteryAxisState
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.PlanTrace
import coach.model.PlanTraceCodec
import coach.model.PrerequisiteEdge
import coach.model.PriorityBand
import coach.model.PublishOutcome
import coach.model.ReasonCatalog
import coach.model.ReplanTrigger
import coach.model.ResourceVersion
import coach.model.ResumeContext
import coach.model.ResumeContextCodec
import coach.model.RetentionAxis
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StoredPlannedTask
import coach.model.StudyTimestamp
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.model.recordedReasonCodes
import coach.ports.ClockPort
import coach.ports.ContentDocument
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * The 3H virtual users as journeys through the real use cases (12F): state in a store, the real gate
 * and planner through [BuildDailyPlan], replans and re-entry through the same path, and Today reading
 * the result through [TodayFactsQuery]. Nothing here hands the planner a need or a decision; the store
 * holds Skill state, a prerequisite graph, authored tasks, pauses and earlier plans.
 */
class VirtualUserJourneysTest {

    private val capacity = DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)

    /** A learner's world. Reading evidence is an error: planning a day must not scan history. */
    private class World(learners: List<Learner>, val edges: List<PrerequisiteEdge>, tasks: List<TaskCandidate>) : PersistencePort, ContentPort {
        val skills = learners.map { SkillRow(it.skill, it.skill.logicalId, "capability", it.lifecycle, "concept", "standard", it.critical, "src", "virtual") }
        val projections = learners.associate { l ->
            val key = "skill_state:${l.skill.logicalId}@v${l.skill.version}"
            key to ProjectionRecord(key, "GRE-v0", 1, 1, 1, mapOf(
                "mastery_axis_state" to (l.mastery?.id ?: "not_yet_evaluated"),
                "retention_axis_state" to l.retention.id,
                "prerequisite_axis_state" to "not_yet_evaluated",
                "weakness_axis_state" to when (l.remediationRequired) { true -> "remediation_required"; false -> "none"; null -> "not_yet_evaluated" },
                "primary_presentation_state" to "x",
            ))
        }.toMutableMap()
        val tasksByNeed = tasks.groupBy { it.needKey }
        val rows = mutableListOf<StoredTruth>()
        var projectionReads = 0
        private var inside = false
        private var nextId = 1L

        fun appended(kind: String) = rows.filter { it.record.kind == kind }
        fun set(learner: Learner) {
            val key = "skill_state:${learner.skill.logicalId}@v${learner.skill.version}"
            projections[key] = ProjectionRecord(key, "GRE-v0", 2, 2, 1, projections.getValue(key).payload + mapOf(
                "weakness_axis_state" to if (learner.remediationRequired == true) "remediation_required" else "none",
            ))
        }

        override fun <T> inTransaction(block: () -> T): T { inside = true; try { return block() } finally { inside = false } }
        override fun appendTruth(record: TruthRecord): Long {
            check(inside) { "${record.kind} was appended outside a transaction" }
            val id = nextId++
            rows += StoredTruth(id, record)
            return id
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = rows.firstOrNull { it.id == id && it.record.kind == kind }?.record
        override fun readProjection(key: String): ProjectionRecord? { projectionReads += 1; return projections[key] }
        override fun writeProjection(record: ProjectionRecord) = error("planning a day writes no learner state")
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = error("planning a day reads no evidence history")
        override fun truthWatermark(): Long = rows.size.toLong()
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = edges.filter { it.target == target }
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

        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = tasksByNeed[need.needKey].orEmpty()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = emptyList()
    }

    private class Clock(var day: String) : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, day, 3 * 3600)
    }

    private fun planned(result: BuildDailyPlan.Built): PlanTrace = assertIs<BuildDailyPlan.Built.Planned>(result).trace

    /** A stale plan of [count] never-started tasks, stored on [day] exactly as the planner stores one. */
    private fun World.staleplan(day: String, count: Int) {
        val at = StudyTimestamp(1_786_000_000_000, day, 3 * 3600)
        inTransaction {
            val plan = appendTruth(TruthRecord("plan_version", at, mapOf("policy_version" to "PLNX-v0")))
            repeat(count) { position ->
                appendTruth(TruthRecord("planned_task", at, mapOf("plan_version_id" to plan.toString(),
                    "skill_logical_id" to cArrays.logicalId, "skill_version" to "1", "position" to position.toString())))
            }
            appendTruth(TruthRecord("planner_decision_trace", at, mapOf("plan_version_id" to plan.toString(), "trace" to "planner_trace/3\nlost")))
        }
    }

    /** S07's learner, as a store: a critical open verification with a dependent, a critical review, new C and 78 more due Skills. */
    private fun returningWorld(otherDue: Int = 78): World {
        val s07 = VirtualUsers.s07(otherDueReviews = otherDue)
        return World(s07.learners, s07.edges, s07.candidates.filterNot { it.track != null })
    }

    // ------------------------------------------------------------------------------------ S07 / S08 / S16

    @Test
    fun `S07 after thirty days, twenty-five stale tasks are not replayed and today is planned from current state`() {
        val world = returningWorld()
        world.staleplan("2026-09-01", 25)
        val clock = Clock("2026-10-01")
        val trace = planned(BuildDailyPlan(world, world, clock).build(capacity.copy(todayOverrideMinutes = 50)))

        assertEquals(GenerationKind.REENTRY.id, trace.generationKind)
        val reentry = trace.reentry!!
        assertEquals(25, reentry.stalePlannedTaskCount, "the stale plan is counted from the store even though its trace is unreadable")
        assertEquals(30, reentry.absenceStudyDays)
        assertTrue(trace.selected.none { it.preserved }, "nothing from the old plan is kept")
        listOf("reentry.absence_not_failure", "reentry.absence_not_task_debt", "reentry.stale_plan_not_replayed",
            "reentry.current_state_regenerated", "reentry.due_inventory_not_daily_plan").forEach {
            assertTrue(it in trace.planReasonCodes, it)
        }
        assertEquals(listOf("pointer-verify", "functions-review", "arrays-lesson"), trace.selected.map { it.candidateId })
        assertTrue(trace.selected.sumOf { it.plannedMinutes } <= 50)
        assertTrue(trace.needs.none { it.rank.starvation != coach.model.StarvationBucket.NONE }, "absence feeds no starvation")
        // Absence changed no learner state: nothing was written but the plan.
        assertEquals(setOf("plan_version", "planned_task", "planner_decision_trace"), world.rows.map { it.record.kind }.toSet())

        // Today reads the fresh plan, never the stale one.
        val today = TodayFactsQuery(world, clock).load()
        assertEquals("2026-10-01", today.plan!!.studyDay)
        assertEquals(trace.selected.map { it.title }, today.plan!!.tasks.map { it.displayTitle })
    }

    @Test
    fun `S08 a safe pause after a week is paused work, ranked after the verification and never first by itself`() {
        val learners = listOf(
            Learner(pointer, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE),
            Learner(cArrays, MasteryAxisState.DEVELOPING_INDEPENDENT),
            Learner(linkedList, MasteryAxisState.NOT_YET_EVIDENCED),
        )
        val world = World(learners, emptyList(), listOf(
            task("pointer-verify", needKey(NeedTrigger.VERIFICATION_DUE, pointer), pointer, 15, TaskPurpose.ASSESS),
            task("arrays-continue", needKey(NeedTrigger.CONTINUE_LEARNING, cArrays), cArrays, 20),
            task("linked-list", needKey(NeedTrigger.NEW_LEARNING, linkedList), linkedList, 10, TaskPurpose.TEACH),
        ))
        world.staleplan("2026-09-24", 2)
        val clock = Clock("2026-10-01")
        world.inTransaction {
            world.appendTruth(TruthRecord("resume_checkpoint", clock.now(), mapOf("context" to ResumeContextCodec.encode(
                ResumeContext(CheckpointKind.CHECKPOINT_PAUSE, needKey(NeedTrigger.CONTINUE_LEARNING, cArrays), "arrays-continue",
                    "cp1", listOf("part1"), listOf("part2"))))))
        }
        val trace = planned(BuildDailyPlan(world, world, clock).build(capacity.copy(todayOverrideMinutes = 50)))

        assertEquals(listOf("pointer-verify", "arrays-continue", "linked-list"), trace.selected.map { it.candidateId })
        val paused = trace.needs.single { it.needKey == needKey(NeedTrigger.CONTINUE_LEARNING, cArrays) }
        assertEquals(PriorityBand.P2, paused.band)
        assertEquals(ContinuationValue.PAUSED_SAFE_CHECKPOINT, paused.rank.continuation)
        assertEquals(PriorityBand.P1, trace.needs.single { it.trigger == NeedTrigger.VERIFICATION_DUE }.band)
        assertTrue("reentry.paused_checkpoint_candidate" in trace.planReasonCodes)
    }

    @Test
    fun `S16 an interrupted high-stakes attempt is not continued, not scored and not negative evidence`() {
        val learners = listOf(Learner(cArrays, MasteryAxisState.DEVELOPING_INDEPENDENT))
        val world = World(learners, emptyList(), listOf(task("arrays-continue", needKey(NeedTrigger.CONTINUE_LEARNING, cArrays), cArrays, 20)))
        world.staleplan("2026-08-01", 1)
        val clock = Clock("2026-10-01")
        world.inTransaction {
            world.appendTruth(TruthRecord("resume_checkpoint", clock.now(), mapOf("context" to ResumeContextCodec.encode(
                ResumeContext(CheckpointKind.HIGH_STAKES_PAUSE, needKey(NeedTrigger.CONTINUE_LEARNING, cArrays), "arrays-check",
                    "cp1", listOf("q1"), listOf("q2"))))))
        }
        val trace = planned(BuildDailyPlan(world, world, clock).build(capacity))
        val need = trace.needs.single()
        assertEquals(ContinuationValue.ACTIVE_LEARNING_CONTEXT, need.rank.continuation, "the high-stakes pause is not continued")
        assertEquals(PriorityBand.P3, need.band)
        assertEquals(1, trace.reentry!!.highStakesPausesNotResumed)
        assertTrue("reentry.incomplete_high_stakes_attempt_not_scored" in trace.planReasonCodes)
        assertTrue(world.rows.none { it.record.kind == "evidence_event" }, "nothing was scored")
    }

    // ------------------------------------------------------------------------------------ S09 / S10

    private fun dayWorld(): World {
        val learners = listOf(
            Learner(cArrays, MasteryAxisState.NOT_YET_EVIDENCED),
            Learner(cFunctions, MasteryAxisState.DEVELOPING_INDEPENDENT, remediationRequired = false),
            Learner(linkedList, MasteryAxisState.NOT_YET_EVIDENCED),
        )
        return World(learners, emptyList(), listOf(
            task("arrays-lesson", needKey(NeedTrigger.NEW_LEARNING, cArrays), cArrays, 20, TaskPurpose.TEACH),
            task("functions-continue", needKey(NeedTrigger.CONTINUE_LEARNING, cFunctions), cFunctions, 15),
            task("functions-repair", needKey(NeedTrigger.REMEDIATION_REQUIRED, cFunctions), cFunctions, 15, TaskPurpose.REMEDIATE),
            task("linked-list", needKey(NeedTrigger.NEW_LEARNING, linkedList), linkedList, 15, TaskPurpose.TEACH),
        ))
    }

    @Test
    fun `S09 fifteen minutes left keeps the finished task and solves only the rest within them`() {
        val world = dayWorld()
        val clock = Clock("2026-10-01")
        val first = planned(BuildDailyPlan(world, world, clock).build(capacity))
        assertEquals(3, first.selected.size)
        val second = planned(BuildDailyPlan(world, world, clock).replan(BuildDailyPlan.ReplanRequest(
            ReplanTrigger.SESSION_REMAINING_TIME_CHANGED, capacity, keptPositions = setOf(0), remainingMinutes = 15)))

        assertEquals(GenerationKind.REPLAN.id, second.generationKind)
        assertEquals(first.selected[0].candidateId, second.selected[0].candidateId)
        assertTrue(second.selected[0].preserved)
        assertEquals(15, second.replan!!.remainderHardMinutes)
        assertTrue(second.selected.drop(1).sumOf { it.plannedMinutes } <= 15)
        assertTrue("replan.remaining_time_changed" in second.planReasonCodes)
        assertEquals(listOf(1, 2), second.replan!!.invalidatedPositions)
        // Today does not offer the finished task again.
        val today = TodayFactsQuery(world, clock).load()
        assertTrue(today.plan!!.tasks.first().kept)
        assertTrue(today.planReplaced)
    }

    @Test
    fun `S10 a new remediation enters the rest of the day without lengthening it`() {
        val world = dayWorld()
        val clock = Clock("2026-10-01")
        val first = planned(BuildDailyPlan(world, world, clock).build(capacity))
        world.set(Learner(cFunctions, MasteryAxisState.DEVELOPING_INDEPENDENT, remediationRequired = true))
        val second = planned(BuildDailyPlan(world, world, clock).replan(BuildDailyPlan.ReplanRequest(
            ReplanTrigger.NEW_REMEDIATION_CREATED, capacity, keptPositions = setOf(0))))

        assertTrue("replan.new_remediation_created" in second.planReasonCodes)
        assertEquals(first.selected[0].candidateId, second.selected[0].candidateId, "completed work is kept")
        val repair = second.needs.single { it.trigger == NeedTrigger.REMEDIATION_REQUIRED }
        assertEquals(PriorityBand.P1, repair.band)
        assertTrue("functions-repair" in second.selected.map { it.candidateId })
        val day = first.capacity.hardBudgetMinutes
        assertEquals(day, second.replan!!.preservedMinutes + second.replan!!.remainderHardMinutes, "the day is not lengthened")
        assertTrue(second.selected.sumOf { it.plannedMinutes } <= day)
        assertTrue(second.invariantChecks.getValue("day_within_hard_budget"))
        assertEquals(second.selected.map { it.needKey }.distinct(), second.selected.map { it.needKey }, "a kept task's need is served twice")
    }

    // ------------------------------------------------------------------------------------ S14 / invariant 17

    @Test
    fun `S14 two identical learners get byte-identical plans through the store`() {
        val a = returningWorld()
        val b = returningWorld()
        val clock = Clock("2026-10-01")
        val first = planned(BuildDailyPlan(a, a, clock).build(capacity))
        val second = planned(BuildDailyPlan(b, b, clock).build(capacity))
        assertEquals(PlanTraceCodec.encode(first), PlanTraceCodec.encode(second))
        assertEquals(TodayFactsQuery(a, clock).load(), TodayFactsQuery(b, clock).load())
    }

    @Test
    fun `planning reads the current state once over, however much history there is`() {
        fun reads(stalePlans: Int): Int {
            val world = returningWorld()
            repeat(stalePlans) { world.staleplan("2026-09-%02d".format(1 + it % 28), 25) }
            BuildDailyPlan(world, world, Clock("2026-10-01")).build(capacity)
            return world.projectionReads
        }
        assertEquals(reads(1), reads(30), "more history made planning read more")
    }

    @Test
    fun `every journey records only contract codes`() {
        val world = dayWorld()
        val clock = Clock("2026-10-01")
        val traces = listOf(
            planned(BuildDailyPlan(world, world, clock).build(capacity)),
            planned(BuildDailyPlan(world, world, clock).replan(BuildDailyPlan.ReplanRequest(
                ReplanTrigger.TODAY_CAPACITY_CHANGED, capacity.copy(todayOverrideMinutes = 30), keptPositions = setOf(0)))),
            planned(BuildDailyPlan(world, world, clock.apply { day = "2026-10-09" }).build(capacity)),
        )
        traces.forEach { trace -> trace.recordedReasonCodes().forEach { assertTrue(ReasonCatalog.isKnown(it), "${trace.generationKind}: $it") } }
        assertFalse(traces.any { it.recordedReasonCodes().contains("selection.not_selected_lower_priority") })
    }
}
