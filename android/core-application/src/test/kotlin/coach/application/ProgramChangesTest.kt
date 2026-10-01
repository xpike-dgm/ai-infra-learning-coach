package coach.application

import coach.model.AssessmentScope
import coach.model.CurriculumPackage
import coach.model.DailyCapacityInput
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.GenerationKind
import coach.model.IndependenceClass
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PlanChangeKind
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ReplanTrigger
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StateChange
import coach.model.StateChangeKind
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The program change report end to end (13E): evidence is recorded, the touched Skill is recomputed by its
 * own engines, the planner replans only if canonical state actually changed, and the report names exactly
 * what changed — against a store that keeps every projection and plan it is given.
 */
class ProgramChangesTest {

    private val skill = VersionedRef("skill.python.loops", 1)
    private val objective = VersionedRef("objective.python.loops.trace", 1)
    private val capacity = DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)

    private class Clock(var day: String) : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, day, 3 * 3600)
    }

    private class Store(val skills: List<SkillRow>, val objectives: List<ObjectiveRow>) : PersistencePort {
        val evidence = mutableListOf<EvidenceRow>()
        val projections = mutableMapOf<String, ProjectionRecord>()
        val rows = mutableListOf<StoredTruth>()
        private var nextId = 1L

        fun appended(kind: String) = rows.filter { it.record.kind == kind }

        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long {
            val id = 1000 + nextId++
            rows += StoredTruth(id, record)
            return id
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? =
            if (kind == "evidence_event") evidence.firstOrNull { it.id == id }?.let { TruthRecord(kind, StudyTimestamp(0, it.studyDay!!, 0), emptyMap()) }
            else rows.firstOrNull { it.id == id && it.record.kind == kind }?.record
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) { projections[record.key] = record }
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = evidence.filter { it.objective == objective }
        /** Every appended row and every evidence row moves truth forward. */
        override fun truthWatermark(): Long = rows.size.toLong() + evidence.size
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = skills
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
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: coach.model.AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = objectives.filter { it.parentSkill == skill }
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()
    }

    /** Every need has one validated task, so a need opened or closed shows in the selected tasks too. */
    private object Content : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef) = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed) = listOf(TaskCandidate("t-${need.needKey}", need.needKey, TaskPurpose.PRACTICE,
            "coding", "Task", need.targetSkills.first(), 15, LifecycleStatus.VALIDATED))
        override fun assessmentItemsFor(skill: VersionedRef) = emptyList<coach.model.AssessmentItem>()
        override fun explanationsFor(objective: VersionedRef): List<coach.model.ExplanationVariant> = emptyList()
    }

    private fun world(): Triple<Store, Clock, RecomputeSkillState> {
        val store = Store(
            listOf(SkillRow(skill, skill.logicalId, "capability", "published", "concept", "standard", false, "src", "authored")),
            listOf(ObjectiveRow(objective, skill, true, "standard", listOf("code_reading"), listOf("code_reading"), null)),
        )
        val clock = Clock("2026-10-01")
        return Triple(store, clock, RecomputeSkillState(store, clock))
    }

    private var nextEvidence = 1L

    private fun Store.record(outcome: EvidenceOutcome, day: String, independence: IndependenceClass = IndependenceClass.INDEPENDENT) {
        val id = nextEvidence++
        evidence += EvidenceRow(id = id, sequence = id, objective = objective, skill = skill, evidenceType = "code_reading",
            outcome = outcome, evaluatorStatus = EvaluatorStatus.VERIFIED, independenceClass = independence, contested = false,
            quality = if (outcome == EvidenceOutcome.POSITIVE) 1.0 else 0.0, difficulty = null, variantFamilyId = "family.$id",
            resource = VersionedRef("item.$id", 1), studyDay = day)
    }

    private fun kinds(changes: List<StateChange>) = changes.map { it.kind }

    /** A mastered Skill, recomputed after each answer as the app would, and today's plan built from it. */
    private fun mastered(): Triple<Store, Clock, RecomputeSkillState> {
        val (store, clock, recompute) = world()
        store.record(EvidenceOutcome.POSITIVE, "2026-09-21"); recompute.recompute(skill)
        store.record(EvidenceOutcome.POSITIVE, "2026-09-22"); recompute.recompute(skill)
        assertEquals("confirmed_current", store.projections.getValue("skill_state:${skill.logicalId}@v1").payload["mastery_axis_state"])
        assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, Content, clock).build(capacity))
        return Triple(store, clock, recompute)
    }

    @Test
    fun `a contradiction opens a verification and the next plan carries it`() {
        val (store, clock, _) = mastered()
        val before = CaptureProgramSnapshot(store, clock).capture()
        store.record(EvidenceOutcome.NEGATIVE, "2026-10-01")

        val reported = ReportProgramChanges(store, Content, clock).report(before, listOf(skill), capacity)
        val report = reported.report
        assertTrue(StateChangeKind.VERIFICATION_OPENED in kinds(report.stateChanges), report.toString())
        // One verification, named once, however many axes opened it.
        assertEquals(1, report.stateChanges.count { it.kind == StateChangeKind.VERIFICATION_OPENED })
        assertTrue(StateChangeKind.MASTERY_NO_LONGER_CONFIRMED !in kinds(report.stateChanges), "a contradiction does not take mastery away")

        val plan = assertIs<BuildDailyPlan.Built.Planned>(reported.plan)
        assertEquals(GenerationKind.REPLAN.id, plan.trace.generationKind)
        assertEquals(ReplanTrigger.NEW_VERIFICATION_DUE_CREATED, plan.trace.replan!!.trigger)
        assertTrue(report.planChanges.any { it.kind == PlanChangeKind.NEED_OPENED && it.trigger == NeedTrigger.VERIFICATION_DUE },
            report.planChanges.toString())
        assertTrue(report.planChanges.any { it.kind == PlanChangeKind.TASK_ADDED && it.skills == listOf(skill) })
        assertTrue("replan.new_verification_created" in report.reasonCodes)
        assertEquals(before.truthWatermark, report.fromWatermark)
        assertTrue(report.toWatermark > report.fromWatermark)
        assertEquals(2, store.appended("plan_version").size)
    }

    @Test
    fun `a failed re-check confirms a remediation and the next plan repairs it`() {
        val (store, clock, recompute) = mastered()
        store.record(EvidenceOutcome.NEGATIVE, "2026-09-25"); recompute.recompute(skill)
        val before = CaptureProgramSnapshot(store, clock).capture()
        assertEquals("confirmation_verification_due", before.skills.getValue(skill).mastery)
        store.record(EvidenceOutcome.NEGATIVE, "2026-10-01")

        val reported = ReportProgramChanges(store, Content, clock).report(before, listOf(skill), capacity)
        val report = reported.report
        assertTrue(StateChangeKind.REMEDIATION_OPENED in kinds(report.stateChanges), report.toString())
        assertTrue(StateChangeKind.MASTERY_NO_LONGER_CONFIRMED in kinds(report.stateChanges))
        val plan = assertIs<BuildDailyPlan.Built.Planned>(reported.plan)
        assertEquals(ReplanTrigger.NEW_REMEDIATION_CREATED, plan.trace.replan!!.trigger)
        assertTrue(report.planChanges.any { it.kind == PlanChangeKind.NEED_OPENED && it.trigger == NeedTrigger.REMEDIATION_REQUIRED })
        assertTrue("replan.new_remediation_created" in report.reasonCodes)
    }

    @Test
    fun `evidence that changes no state changes no plan, and the report says nothing changed`() {
        val (store, clock, _) = mastered()
        val before = CaptureProgramSnapshot(store, clock).capture()
        val plans = store.appended("plan_version").size
        // Help used on a mastered Skill is not independent evidence: no axis moves.
        store.record(EvidenceOutcome.POSITIVE, "2026-10-01", IndependenceClass.ASSISTED)

        val reported = ReportProgramChanges(store, Content, clock).report(before, listOf(skill), capacity)
        assertTrue(reported.report.nothingChanged, reported.report.toString())
        assertNull(reported.plan, "no state change, no new plan")
        assertEquals(plans, store.appended("plan_version").size)
        assertEquals(emptyList(), reported.report.reasonCodes)
    }

    @Test
    fun `the first reading of a Skill claims no change from a state nobody had written`() {
        val (store, clock, _) = world()
        val before = CaptureProgramSnapshot(store, clock).capture()
        assertEquals("not_yet_evaluated", before.skills.getValue(skill).mastery)
        store.record(EvidenceOutcome.NEGATIVE, "2026-10-01")

        val reported = ReportProgramChanges(store, Content, clock).report(before, listOf(skill), capacity)
        assertEquals(emptyList(), reported.report.stateChanges)
        assertEquals(listOf(skill), reported.report.unknownBefore)
        assertNull(reported.plan)
        assertTrue(store.appended("plan_version").isEmpty())
    }

    @Test
    fun `a Skill with no published Objective has nothing to recompute and nothing is written`() {
        val (store, clock, _) = world()
        val stranger = VersionedRef("skill.unpublished", 1)
        assertFalse(RecomputeSkillState(store, clock).recompute(stranger))
        assertTrue(store.projections.isEmpty())
    }

    @Test
    fun `recomputing writes every axis from the published Objectives`() {
        val (store, clock, recompute) = world()
        store.record(EvidenceOutcome.NEGATIVE, "2026-10-01")
        assertTrue(recompute.recompute(skill))
        val axes = store.projections.getValue("skill_state:${skill.logicalId}@v1").payload
        assertEquals("developing_independent", axes["mastery_axis_state"])
        assertEquals("supported", axes["weakness_axis_state"])
        assertEquals("untracked", axes["retention_axis_state"], "the retention engine wrote its axis")
        assertTrue("retention_state:${skill.logicalId}@v1" in store.projections)
        assertTrue("prerequisite_readiness:${skill.logicalId}@v1" in store.projections)
        assertTrue("weakness_state:${objective.logicalId}@v1" in store.projections)
        assertTrue(store.rows.isEmpty(), "recomputing writes no truth")
        assertEquals(clock.now().studyDay, CaptureProgramSnapshot(store, clock).capture().studyDay)
    }
}
