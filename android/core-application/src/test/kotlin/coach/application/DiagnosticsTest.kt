package coach.application

import coach.engines.virtual.VirtualUsers.FastPath
import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.CandidateDisposition
import coach.model.ContentOrigin
import coach.model.CurriculumPackage
import coach.model.DailyCapacityInput
import coach.model.DiagnosticCodes
import coach.model.DiagnosticObjectiveState
import coach.model.DiagnosticRecord
import coach.model.DiagnosticScopeCodec
import coach.model.DiagnosticSource
import coach.model.DiagnosticStage
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatus
import coach.model.EvaluatorStatusRequirement
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.IndependenceMode
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ReplanTrigger
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StateChangeKind
import coach.model.StoredPlan
import coach.model.StoredPlannedTask
import coach.model.StudyTimestamp
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.UseCeiling
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.model.WaiverOutcome
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The diagnostic waiver end to end (13F): the learner asks, the diagnostic is a session row, its checks reach the
 * planner as candidates, answers go through the ordinary evidence pipeline, the touched Skill is recomputed, and a
 * waiver — only where the gates first passed on diagnostic evidence — takes exactly those lessons out of the plan.
 * 3H S06 runs here against real use cases.
 */
class DiagnosticsTest {

    private val capacity = DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)
    private val skill = FastPath.skill
    private val o1 = FastPath.addressVsValue
    private val o2 = FastPath.declaration
    private val o3 = FastPath.dereference
    private val o4 = FastPath.writeThrough

    private class Clock(var day: String = "2026-10-01") : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, day, 3 * 3600)
    }

    /** A learner's world: curriculum, items, evidence, truth rows and projections, kept as given. */
    private class World(val skills: List<SkillRow>, val objectives: List<ObjectiveRow>, val items: List<AssessmentItem>) : PersistencePort, ContentPort {
        val evidence = mutableListOf<EvidenceRow>()
        val projections = mutableMapOf<String, ProjectionRecord>()
        val rows = mutableListOf<StoredTruth>()
        val exposures = mutableListOf<ExposureFact>()
        var evidenceReads = 0
        private var nextId = 1L

        fun appended(kind: String) = rows.filter { it.record.kind == kind }

        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long {
            val id = nextId++
            rows += StoredTruth(id, record)
            return id
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = rows.firstOrNull { it.id == id && it.record.kind == kind }?.record
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) { projections[record.key] = record }
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = items.firstOrNull { it.ref == ref }?.let {
            ResourceVersion(it.ref, "content://${it.ref}", it.evidenceType, "editor", ContentOrigin.HUMAN_AUTHORED, it.variantFamilyId)
        }
        override fun latestValidation(ref: VersionedRef): ValidationRecord? =
            items.firstOrNull { it.ref == ref }?.let { ValidationRecord(ref, 1, LifecycleStatus.TRUSTED, "validator", ContentOrigin.HUMAN_AUTHORED) }
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = objectives.firstOrNull { it.ref == ref }?.profile()
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> { evidenceReads += 1; return evidence.filter { it.objective == objective } }
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
        override fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth? =
            appended("assessment_session").lastOrNull {
                it.record.payload["scope"] == scope.storedAs && it.record.payload["blueprint"].orEmpty().startsWith(format.substringBefore("/") + "/")
            }
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? =
            appended("assessment_session").lastOrNull { it.record.payload["scope"] == scope.storedAs }
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> =
            exposures.filter { it.resource in resources || it.variantFamilyId in variantFamilies }
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = objectives.filter { it.parentSkill == skill }
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()

        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = items.firstOrNull { it.ref == ref }
        override fun curriculumPackage(): CurriculumPackage? = null
        /** One lesson per Objective for the Skill's learning needs; a diagnostic's checks come from the diagnostic. */
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = when (need.trigger) {
            NeedTrigger.NEW_LEARNING, NeedTrigger.CONTINUE_LEARNING ->
                objectives.filter { it.parentSkill in need.targetSkills }.map { FastPath.lesson(it.ref, need.needKey) }
            else -> emptyList()
        }
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = items.filter { skill in it.targetSkills }
        override fun explanationsFor(objective: VersionedRef): List<coach.model.ExplanationVariant> = emptyList()
    }

    private fun item(objective: VersionedRef, family: String) = AssessmentItem(
        ref = VersionedRef("item.${objective.logicalId.substringAfterLast('.')}.$family", 1), targetObjectives = listOf(objective),
        targetSkills = listOf(skill), requiredSkills = emptyList(), evidenceType = "code_reading", expectedAnswerOrRubricRef = "key",
        evaluatorRequirement = EvaluatorRequirement(EvaluatorStatusRequirement.VERIFIED, false, "e1"),
        allowedTools = AllowedToolsPolicy(listOf("editor")), independenceMode = IndependenceMode.H0_REQUIRED, difficultyClass = "basic",
        lifecycleStatus = LifecycleStatus.CANDIDATE, contentOrigin = ContentOrigin.HUMAN_AUTHORED,
        declaredUseCeiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE, scopeEligibility = setOf(AssessmentScope.DAILY_MICRO),
        variantFamilyId = "fam.${objective.logicalId.substringAfterLast('.')}.$family", deterministicVerification = true, expectedActiveMinutes = 5,
    )

    private fun world(lifecycle: String = "published", optional: VersionedRef? = null): Pair<World, Clock> {
        val skills = listOf(SkillRow(skill, skill.logicalId, "capability", lifecycle, "concept", "standard", false, "src", "authored"))
        val objectives = FastPath.objectives.map { ObjectiveRow(it, skill, true, "standard", listOf("code_reading"), listOf("code_reading")) } +
            listOfNotNull(optional?.let { ObjectiveRow(it, skill, false, "standard", listOf("code_reading"), listOf("code_reading")) })
        val items = FastPath.objectives.flatMap { listOf(item(it, "a"), item(it, "b"), item(it, "c")) }
        return World(skills, objectives, items) to Clock()
    }

    private var nextEvidence = 1L

    /** Records an answer the way the pipeline would, after `SubmitAttempt` made the attempt in [session]. */
    private fun World.answer(
        objective: VersionedRef,
        family: String,
        outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE,
        session: Long? = null,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
    ): EvidenceRow {
        val id = 10_000 + nextEvidence++
        val it = item(objective, family)
        val row = EvidenceRow(id = id, sequence = id, objective = objective, skill = skill, evidenceType = "code_reading", outcome = outcome,
            evaluatorStatus = EvaluatorStatus.VERIFIED, independenceClass = independence, contested = false,
            quality = if (outcome == EvidenceOutcome.POSITIVE) 1.0 else 0.0, difficulty = null, variantFamilyId = it.variantFamilyId,
            resource = it.ref, studyDay = "2026-10-01", assessmentSessionId = session)
        evidence += row
        exposures += ExposureFact(it.ref, it.variantFamilyId, ExposureFact.ITEM_VERSION_SEEN)
        return row
    }

    private fun request(world: World, clock: Clock, source: DiagnosticSource = DiagnosticSource.USER_REQUESTED_FAST_PATH) =
        assertIs<RequestDiagnostic.Requested.Written>(RequestDiagnostic(world, clock).request(source, listOf(skill)))

    private fun build(world: World, clock: Clock) = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(world, world, clock).build(capacity))

    private fun result(world: World) = assertIs<ReadDiagnosticResult.Read.Result>(ReadDiagnosticResult(world).read()).result

    // ------------------------------------------------------------------------------------ the request

    @Test
    fun `saying I know this opens a diagnostic and waives nothing`() {
        val (world, clock) = world(optional = VersionedRef("objective.c.pointer_basics.history", 1))
        val written = request(world, clock, DiagnosticSource.PRIOR_EXPERIENCE_CLAIM)
        val row = world.appended("assessment_session").single()
        assertEquals(written.sessionId, row.id)
        assertEquals("daily", row.record.payload["scope"])
        val decoded = assertIs<DiagnosticRecord.Request>(DiagnosticScopeCodec.decode(row.record.payload.getValue("blueprint")))
        // Required Objectives only; an optional one is not something whose lesson anyone must take.
        assertEquals(FastPath.objectives, decoded.scope.targets.map { it.objective })
        assertEquals(DiagnosticSource.PRIOR_EXPERIENCE_CLAIM, decoded.scope.source)
        // No evidence was written and nothing is waived: the claim is only the scope.
        assertTrue(world.appended("evidence_event").isEmpty())
        FastPath.objectives.forEach {
            val coverage = world.projections.getValue("diagnostic_coverage:${it.logicalId}@v1").payload
            assertEquals("none", coverage["waiver"])
            assertEquals("probe_needed", coverage["diagnostic_state"])
            assertEquals("probe", coverage["diagnostic_stage"])
        }
        val read = result(world)
        assertEquals(WaiverOutcome.NONE, read.outcome)
        assertTrue(read.inProgress)
        assertEquals(listOf(DiagnosticCodes.USER_REQUESTED_FAST_PATH), read.reasonCodes)
    }

    @Test
    fun `a request that names nothing checkable writes nothing`() {
        val (world, clock) = world()
        assertIs<RequestDiagnostic.Requested.Refused>(RequestDiagnostic(world, clock).request(DiagnosticSource.USER_REQUESTED_FAST_PATH, emptyList()))
        assertIs<RequestDiagnostic.Requested.Refused>(RequestDiagnostic(world, clock).request(DiagnosticSource.USER_REQUESTED_FAST_PATH,
            listOf(VersionedRef("skill.unknown", 1))))
        val (deprecated, clock2) = world(lifecycle = "deprecated")
        assertIs<RequestDiagnostic.Requested.Refused>(RequestDiagnostic(deprecated, clock2).request(DiagnosticSource.USER_REQUESTED_FAST_PATH, listOf(skill)))
        assertTrue(world.rows.isEmpty() && deprecated.rows.isEmpty())
    }

    @Test
    fun `what is already shown is not tested again, and a fully shown Skill has nothing to diagnose`() {
        val (world, clock) = world()
        FastPath.objectives.forEach { world.answer(it, "a"); world.answer(it, "b") }
        assertEquals(RequestDiagnostic.Requested.NothingToDiagnose, RequestDiagnostic(world, clock).request(DiagnosticSource.USER_REQUESTED_FAST_PATH, listOf(skill)))
        assertTrue(world.rows.isEmpty())

        val (partly, clock2) = world()
        partly.answer(o1, "a"); partly.answer(o1, "b")
        val written = request(partly, clock2)
        assertEquals(listOf(o2, o3, o4), written.scope.targets.map { it.objective })
    }

    // ------------------------------------------------------------------------------------ planning

    @Test
    fun `the planner offers the diagnostic's checks, holds the lessons it is checking, and reads no evidence`() {
        val (world, clock) = world()
        request(world, clock)
        val reads = world.evidenceReads
        val plan = build(world, clock).trace
        assertEquals(reads, world.evidenceReads, "planning a day reads no evidence history")
        val need = plan.needs.single { it.trigger == NeedTrigger.DIAGNOSTIC_OPPORTUNITY }
        assertEquals("diagnostic_opportunity:$skill", need.needKey)
        assertTrue("priority.decision_value" in need.priorityReasonCodes)
        val selected = plan.selected.single { it.needKey == need.needKey }
        assertEquals(TaskPurpose.DIAGNOSE, selected.purpose)
        // Four Objectives, four distinct fresh items; one task per need.
        val offered = plan.candidates.filter { it.needKey == need.needKey }
        assertEquals(4, offered.size)
        assertEquals(4, offered.map { it.candidateId.substringAfterLast(":") }.toSet().size)
        // Every lesson waits for the diagnostic: teaching first would make "already knew it" untrue.
        plan.candidates.filter { it.candidateId.startsWith("lesson-") }.forEach {
            assertEquals(CandidateDisposition.CONDITIONAL_NOT_SELECTED, it.disposition, it.candidateId)
            assertEquals(listOf(DiagnosticCodes.USER_REQUESTED_FAST_PATH), it.reasonCodes)
        }
    }

    // ------------------------------------------------------------------------------------ S06, end to end

    /** Records one answer in the open diagnostic and reports what it changed, as the app will after a session. */
    private fun World.diagnose(clock: Clock, session: Long, objective: VersionedRef, family: String,
                               outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE,
                               independence: IndependenceClass = IndependenceClass.INDEPENDENT) =
        ReportProgramChanges(this, this, clock).let { report ->
            val before = CaptureProgramSnapshot(this, clock).capture()
            answer(objective, family, outcome, session, independence)
            report.report(before, listOf(skill), capacity)
        }

    @Test
    fun `S06 a partial diagnostic waives only the validated Objectives and the plan and the result say exactly that`() {
        val (world, clock) = world()
        val session = request(world, clock).sessionId
        build(world, clock)

        world.diagnose(clock, session, o1, "a")
        val waivedO1 = world.diagnose(clock, session, o1, "b")
        assertEquals(listOf(StateChangeKind.COVERAGE_WAIVED), waivedO1.report.stateChanges.map { it.kind })
        assertEquals(o1, waivedO1.report.stateChanges.single().objective)
        val replan = assertIs<BuildDailyPlan.Built.Planned>(waivedO1.plan)
        assertEquals(ReplanTrigger.DIAGNOSTIC_WAIVER_GRANTED, replan.trace.replan!!.trigger)
        assertTrue("replan.prerequisite_state_changed" in waivedO1.report.reasonCodes)

        world.diagnose(clock, session, o2, "a")
        world.diagnose(clock, session, o2, "b")
        // O3: a clean, independent miss. O4: one success, not yet enough for the gates.
        val missed = world.diagnose(clock, session, o3, "a", EvidenceOutcome.NEGATIVE)
        assertTrue(missed.report.stateChanges.none { it.kind == StateChangeKind.WEAKNESS_SUPPORTED || it.kind == StateChangeKind.REMEDIATION_OPENED })
        world.diagnose(clock, session, o4, "a")

        val read = result(world)
        assertEquals(listOf(o1, o2), read.waived.map { it.target.objective })
        val byObjective = read.objectives.associateBy { it.target.objective }
        assertEquals(DiagnosticObjectiveState.NOT_DEMONSTRATED, byObjective.getValue(o3).state)
        assertEquals(DiagnosticObjectiveState.CONFIRM_NEEDED, byObjective.getValue(o4).state)
        assertEquals(DiagnosticStage.CONFIRM, byObjective.getValue(o4).stage)
        assertEquals(WaiverOutcome.PARTIAL, read.outcome)
        assertTrue(read.inProgress)
        // Each waiver names the evidence of the window that passed, and the session that gathered it.
        read.waived.forEach { assertEquals(session, it.waiver!!.sessionId); assertEquals(2, it.waiver!!.sourceEvidenceIds.size) }

        // The whole Topic is not mastered: the Skill is not, and nothing claims a full waiver.
        assertEquals("developing_independent", world.projections.getValue("skill_state:${skill.logicalId}@v1").payload["mastery_axis_state"])
        // Not knowing something never taught opened no weakness.
        assertEquals("none", world.projections.getValue("weakness_state:${o3.logicalId}@v1").payload["state"])

        val plan = build(world, clock.also { it.day = "2026-10-02" }).trace
        val lessons = plan.candidates.filter { it.candidateId.startsWith("lesson-") }.associateBy { it.candidateId }
        listOf(o1, o2).forEach {
            assertEquals(CandidateDisposition.RESOLVED_BEFORE_SELECTION, lessons.getValue(FastPath.lessonId(it)).disposition)
            assertEquals(listOf(DiagnosticCodes.PARTIAL_COVERAGE_WAIVER), lessons.getValue(FastPath.lessonId(it)).reasonCodes)
        }
        // O3 returns to normal learning; O4 is still being checked, so its lesson waits for the check.
        assertEquals(CandidateDisposition.CONDITIONAL_NOT_SELECTED, lessons.getValue(FastPath.lessonId(o4)).disposition)
        assertTrue(plan.selected.any { it.candidateId == FastPath.lessonId(o3) }, plan.selected.toString())
        val check = plan.candidates.filter { it.needKey == "diagnostic_opportunity:$skill" }
        assertTrue(check.isNotEmpty() && check.all { o4.logicalId in it.candidateId }, check.toString())

        // Withdrawing ends the fast path: O4 returns to normal learning, O1 and O2 stay waived.
        assertIs<WithdrawDiagnostic.Withdrawn.Written>(WithdrawDiagnostic(world, clock).withdraw())
        assertEquals(ReadDiagnosticResult.Read.NothingOpen, ReadDiagnosticResult(world).read())
        val after = build(world, clock.also { it.day = "2026-10-03" }).trace
        val lessonsAfter = after.candidates.filter { it.candidateId.startsWith("lesson-") }.associateBy { it.candidateId }
        assertEquals(CandidateDisposition.RESOLVED_BEFORE_SELECTION, lessonsAfter.getValue(FastPath.lessonId(o1)).disposition)
        assertFalse(lessonsAfter.getValue(FastPath.lessonId(o4)).disposition == CandidateDisposition.CONDITIONAL_NOT_SELECTED)
        assertTrue(after.needs.none { it.trigger == NeedTrigger.DIAGNOSTIC_OPPORTUNITY })
    }

    @Test
    fun `a full diagnostic waives every lesson and the Skill is mastered by the mastery engine, not by the waiver`() {
        val (world, clock) = world()
        val session = request(world, clock).sessionId
        FastPath.objectives.forEach { world.diagnose(clock, session, it, "a"); world.diagnose(clock, session, it, "b") }
        val read = result(world)
        assertEquals(WaiverOutcome.FULL, read.outcome)
        assertFalse(read.inProgress)
        assertEquals(listOf(DiagnosticCodes.USER_REQUESTED_FAST_PATH, DiagnosticCodes.FULL_COVERAGE_WAIVER), read.reasonCodes)
        assertEquals("confirmed_current", world.projections.getValue("skill_state:${skill.logicalId}@v1").payload["mastery_axis_state"])
    }

    @Test
    fun `evidence gathered in another kind of session, or in a diagnostic that does not hold the Objective, never waives`() {
        val (world, clock) = world()
        val weekly = world.appendTruth(TruthRecord("assessment_session", clock.now(), mapOf("scope" to "weekly", "blueprint" to "weekly_blueprint/1")))
        world.answer(o1, "a", session = weekly)
        world.answer(o1, "b", session = weekly)
        // O1 is now shown by ordinary measurement, so a diagnostic for the Skill leaves it out.
        val written = request(world, clock)
        assertFalse(o1 in written.scope.targets.map { it.objective })
        RecomputeSkillState(world, clock).recompute(skill)
        assertEquals("none", world.projections.getValue("diagnostic_coverage:${o1.logicalId}@v1").payload["waiver"])
    }

    @Test
    fun `a lesson whose whole Skill was waived is resolved as a full waiver`() {
        val (world, clock) = world()
        val session = request(world, clock).sessionId
        FastPath.objectives.forEach { world.diagnose(clock, session, it, "a"); world.diagnose(clock, session, it, "b") }
        val diagnostic = DiagnosticPlanning.read(world, PlanningStates.read(world, world.skills))
        val holds = DiagnosticPlanning.holds(world, FastPath.objectives.map { FastPath.lesson(it) }, diagnostic)
        assertEquals(FastPath.objectives.toSet(), holds.keys)
        holds.values.forEach { assertEquals(DiagnosticCodes.FULL_COVERAGE_WAIVER, it.reasonCode); assertTrue(it.waived) }
        // A practice task is never held by coverage, and a lesson that declares nothing is never covered.
        assertTrue(DiagnosticPlanning.holds(world, listOf(FastPath.lesson(o1).copy(purpose = TaskPurpose.PRACTICE)), diagnostic).isEmpty())
        assertTrue(DiagnosticPlanning.holds(world, listOf(FastPath.lesson(o1).copy(targetObjectives = emptyList())), diagnostic).isEmpty())
    }

    @Test
    fun `help taken ends the fast path for that Objective, and a later success does not waive it`() {
        val (world, clock) = world()
        val session = request(world, clock).sessionId
        world.diagnose(clock, session, o1, "a", EvidenceOutcome.NEGATIVE, IndependenceClass.ASSISTED)
        world.diagnose(clock, session, o1, "b")
        val report = world.diagnose(clock, session, o1, "c")
        assertTrue(report.report.stateChanges.none { it.kind == StateChangeKind.COVERAGE_WAIVED })
        val o1Result = result(world).objectives.single { it.target.objective == o1 }
        assertEquals(DiagnosticObjectiveState.ASSISTANCE_ENDED_FAST_PATH, o1Result.state)
        assertEquals(listOf(DiagnosticCodes.H0_REQUIRED_FOR_WAIVER), o1Result.reasonCodes)
        // Help is never a penalty: nothing opened a weakness for it.
        assertTrue(world.projections.getValue("weakness_state:${o1.logicalId}@v1").payload["state"] in setOf("none", "resolved"))
    }

    @Test
    fun `a waiver whose evidence is corrected away is withdrawn and its lesson comes back`() {
        val (world, clock) = world()
        val session = request(world, clock).sessionId
        world.diagnose(clock, session, o1, "a")
        val granted = world.diagnose(clock, session, o1, "b")
        assertEquals(StateChangeKind.COVERAGE_WAIVED, granted.report.stateChanges.single().kind)
        // The store reads the newest disposition into the row; here the second answer's evaluation no longer stands.
        val before = CaptureProgramSnapshot(world, clock).capture()
        val index = world.evidence.indexOfLast { it.objective == o1 }
        world.evidence[index] = world.evidence[index].copy(evaluatorStatus = EvaluatorStatus.INVALID)
        val withdrawn = ReportProgramChanges(world, world, clock).report(before, listOf(skill), capacity)
        assertEquals(listOf(StateChangeKind.COVERAGE_WAIVER_WITHDRAWN), withdrawn.report.stateChanges.map { it.kind })
        assertEquals("none", world.projections.getValue("diagnostic_coverage:${o1.logicalId}@v1").payload["waiver"])
    }

    @Test
    fun `a waiver is coverage, not competence - a later miss in ordinary learning does not take it back`() {
        val (world, clock) = world()
        val session = request(world, clock).sessionId
        world.diagnose(clock, session, o1, "a")
        world.diagnose(clock, session, o1, "b")
        world.answer(o1, "c", EvidenceOutcome.NEGATIVE)
        RecomputeSkillState(world, clock).recompute(skill)
        assertEquals("active", world.projections.getValue("diagnostic_coverage:${o1.logicalId}@v1").payload["waiver"])
    }

    @Test
    fun `a new request replaces an open one and leaves nothing owed`() {
        val (world, clock) = world()
        val first = request(world, clock).sessionId
        world.diagnose(clock, first, o1, "a")
        val second = request(world, clock).sessionId
        assertTrue(second > first)
        val read = result(world)
        assertEquals(second, read.sessionId)
        // The new diagnostic starts from what the evidence says: O1 already has one diagnostic success.
        assertEquals(DiagnosticObjectiveState.CONFIRM_NEEDED, read.objectives.single { it.target.objective == o1 }.state)
    }

    @Test
    fun `a result is never guessed from coverage that was not built for the diagnostic`() {
        val (world, clock) = world()
        request(world, clock)
        world.projections.remove("diagnostic_coverage:${o2.logicalId}@v1")
        val read = assertIs<ReadDiagnosticResult.Read.Unreadable>(ReadDiagnosticResult(world).read())
        assertEquals(listOf(o2), read.objectives)
        assertEquals(WithdrawDiagnostic.Withdrawn.NothingOpen, WithdrawDiagnostic(world().first, clock).withdraw())
    }

    @Test
    fun `a diagnostic miss after ordinary learning is the accepted weakness rule, unchanged`() {
        val (world, clock) = world()
        world.answer(o3, "a")
        val session = request(world, clock).sessionId
        world.diagnose(clock, session, o3, "b", EvidenceOutcome.NEGATIVE)
        assertEquals("supported", world.projections.getValue("weakness_state:${o3.logicalId}@v1").payload["state"])
        assertNotNull(world.projections["diagnostic_coverage:${o3.logicalId}@v1"])
        assertNull(result(world).objectives.single { it.target.objective == o3 }.waiver)
    }
}
