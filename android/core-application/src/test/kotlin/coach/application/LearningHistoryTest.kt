package coach.application

import coach.model.AssessmentScope
import coach.model.AssistanceLevel
import coach.model.CurriculumPackage
import coach.model.DailyCapacityInput
import coach.model.EvaluatorStatus
import coach.model.EvidenceKind
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.HistoryFamily
import coach.model.IndependenceClass
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
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
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * 16B (`LHRX-v0`): the history is the stored evidence with what the engines, replayed over it, say each row changed;
 * the report is each session's families. Replaying writes nothing, agrees with the live recompute, and a session's
 * report says what the session's own program change report (13E) said.
 */
class LearningHistoryTest {

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
        /** One global sequence: every appended row and every evidence row moves truth forward. */
        override fun truthWatermark(): Long = (rows.map { it.id - 1000 } + evidence.map { it.sequence }).maxOrNull() ?: 0L
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = skills
        override fun latestPlan(): StoredPlan? = planVersions().lastOrNull()
        override fun planVersions(): List<StoredPlan> = appended("plan_version").map { plan ->
            val tasks = appended("planned_task").filter { it.record.payload["plan_version_id"] == plan.id.toString() }
            val trace = appended("planner_decision_trace").lastOrNull { it.record.payload["plan_version_id"] == plan.id.toString() }
            StoredPlan(plan.id, plan.record.recordedAt, tasks.size, trace?.record?.payload?.get("trace"),
                tasks.map { row ->
                    StoredPlannedTask(row.id, row.record.payload.getValue("position").toInt(),
                        VersionedRef(row.record.payload.getValue("skill_logical_id"), row.record.payload.getValue("skill_version").toInt()))
                }.sortedBy { it.position })
        }
        override fun assessmentSessions(): List<StoredTruth> = appended("assessment_session")
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = objectives.filter { it.parentSkill == skill }
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()
    }

    private object Content : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef) = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed) = listOf(TaskCandidate("t-${need.needKey}", need.needKey, TaskPurpose.PRACTICE,
            "coding", "Task", need.targetSkills.first(), 15, LifecycleStatus.VALIDATED))
        override fun assessmentItemsFor(skill: VersionedRef) = emptyList<coach.model.AssessmentItem>()
        override fun explanationsFor(objective: VersionedRef): List<coach.model.ExplanationVariant> = emptyList()
        override fun codeTestsFor(item: VersionedRef): coach.model.CodeTestSuite? = null
        override fun comprehensionChecksFor(item: VersionedRef): List<coach.model.ComprehensionCheck> = emptyList()
        override fun answerKeyFor(item: VersionedRef): coach.model.AcceptedAnswers? = null
        override fun rubricFor(item: VersionedRef): coach.model.Rubric? = null
    }

    private fun world(): Triple<Store, Clock, RecomputeSkillState> {
        val store = Store(
            listOf(SkillRow(skill, "Loops", "capability", "published", "concept", "standard", false, "src", "authored")),
            listOf(ObjectiveRow(objective, skill, true, "standard", listOf("code_reading"), listOf("code_reading"), null)),
        )
        val clock = Clock("2026-10-01")
        return Triple(store, clock, RecomputeSkillState(store, clock))
    }

    /** Evidence takes the next number of the store's one sequence, after any truth already appended. */
    private fun Store.record(
        outcome: EvidenceOutcome,
        day: String,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        session: Long? = null,
        assistance: List<AssistanceLevel> = emptyList(),
    ) {
        val sequence = truthWatermark() + 1
        evidence += EvidenceRow(id = sequence, sequence = sequence, objective = objective, skill = skill, evidenceType = "code_reading",
            outcome = outcome, evaluatorStatus = EvaluatorStatus.VERIFIED, independenceClass = independence, contested = false,
            quality = if (outcome == EvidenceOutcome.POSITIVE) 1.0 else 0.0, difficulty = null, variantFamilyId = "family.$sequence",
            resource = VersionedRef("item.$sequence", 1), studyDay = day, assessmentSessionId = session,
            attemptId = sequence + 500, assistanceLevels = assistance)
    }

    private fun mastered(): Triple<Store, Clock, RecomputeSkillState> {
        val (store, clock, recompute) = world()
        store.record(EvidenceOutcome.POSITIVE, "2026-09-30"); recompute.recompute(skill)
        store.record(EvidenceOutcome.POSITIVE, "2026-10-01"); recompute.recompute(skill)
        assertEquals("confirmed_current", store.projections.getValue("skill_state:${skill.logicalId}@v1").payload["mastery_axis_state"])
        return Triple(store, clock, recompute)
    }

    @Test
    fun `recomputing again changes nothing, and a passed recheck keeps mastery (D-123)`() {
        val (store, clock, recompute) = mastered()
        fun axis() = store.projections.getValue("skill_state:${skill.logicalId}@v1").payload["mastery_axis_state"]
        clock.day = "2026-10-05"
        store.record(EvidenceOutcome.NEGATIVE, "2026-10-05"); recompute.recompute(skill)
        assertEquals("confirmation_verification_due", axis())
        // Before 16B a second rebuild of the same evidence took mastery away.
        recompute.recompute(skill)
        assertEquals("confirmation_verification_due", axis())
        // Help is not a recheck.
        store.record(EvidenceOutcome.POSITIVE, "2026-10-05", independence = IndependenceClass.ASSISTED); recompute.recompute(skill)
        assertEquals("confirmation_verification_due", axis())
        // A fresh, clean, independent recheck that passes closes the verification and keeps mastery (`GRE-v0` §16.2).
        store.record(EvidenceOutcome.POSITIVE, "2026-10-06"); recompute.recompute(skill)
        assertEquals("confirmed_current", axis())
        recompute.recompute(skill)
        assertEquals("confirmed_current", axis())
        val last = LearningHistoryQuery(store).load().entries.first()
        assertTrue(StateChangeKind.VERIFICATION_RESOLVED in last.changes.map { it.kind }, last.changes.toString())
        assertTrue(StateChangeKind.MASTERY_NO_LONGER_CONFIRMED !in last.changes.map { it.kind })
    }

    @Test
    fun `a learner who has done nothing has an empty history and no reports`() {
        val (store, _, _) = world()
        val facts = LearningHistoryQuery(store).load()
        assertTrue(facts.curriculumLoaded && facts.entries.isEmpty() && facts.sessions.isEmpty())
    }

    @Test
    fun `each row says what the engines say it changed, and nothing where nothing changed`() {
        val (store, _, _) = mastered()
        val entries = LearningHistoryQuery(store).load().entries.sortedBy { it.sequence }
        assertEquals(2, entries.size)
        assertEquals(emptyList(), entries[0].changes, "a first independent answer develops; it confirms nothing yet")
        assertEquals(listOf(StateChangeKind.CAPABILITY_CONFIRMED), entries[1].changes.map { it.kind })
        assertTrue(entries.all { it.family == HistoryFamily.LEARNING })
        assertTrue(entries.all { it.kind == EvidenceKind.INDEPENDENT })
    }

    @Test
    fun `work on a mastered Skill is a review, and a contradiction opens a verification, never a loss`() {
        val (store, clock, recompute) = mastered()
        clock.day = "2026-10-05"
        store.record(EvidenceOutcome.NEGATIVE, "2026-10-05"); recompute.recompute(skill)
        val last = LearningHistoryQuery(store).load().entries.first()
        assertEquals(HistoryFamily.REVIEW, last.family)
        assertTrue(StateChangeKind.VERIFICATION_OPENED in last.changes.map { it.kind }, last.changes.toString())
        assertTrue(StateChangeKind.MASTERY_NO_LONGER_CONFIRMED !in last.changes.map { it.kind })
    }

    @Test
    fun `work while a remediation is open is remediation`() {
        val (store, clock, recompute) = mastered()
        clock.day = "2026-10-05"
        store.record(EvidenceOutcome.NEGATIVE, "2026-10-05"); recompute.recompute(skill)
        store.record(EvidenceOutcome.NEGATIVE, "2026-10-06"); recompute.recompute(skill)
        store.record(EvidenceOutcome.POSITIVE, "2026-10-07"); recompute.recompute(skill)
        val entries = LearningHistoryQuery(store).load().entries.sortedBy { it.sequence }
        assertTrue(StateChangeKind.REMEDIATION_OPENED in entries[3].changes.map { it.kind }, entries[3].changes.toString())
        assertEquals(HistoryFamily.REMEDIATION, entries[4].family)
    }

    @Test
    fun `replaying writes nothing and ends where the live recompute ended`() {
        val (store, clock, recompute) = mastered()
        clock.day = "2026-10-05"
        store.record(EvidenceOutcome.NEGATIVE, "2026-10-05"); recompute.recompute(skill)
        val projections = store.projections.toMap()
        val truth = store.rows.toList()
        val steps = HistoryReplay.steps(store, skill, store.objectivesOf(skill))
        assertEquals(projections, store.projections.toMap(), "the replay touched the store's projections")
        assertEquals(truth, store.rows.toList(), "the replay wrote truth")
        val live = store.projections.getValue("skill_state:${skill.logicalId}@v1").payload
        val replayed = steps.last().after
        assertEquals(live["mastery_axis_state"], replayed.mastery)
        assertEquals(live["retention_axis_state"], replayed.retention)
        assertEquals(live["weakness_axis_state"], replayed.weakness)
    }

    @Test
    fun `the assistance recorded on an attempt is shown as levels, and assisted work is not independent`() {
        val (store, _, recompute) = world()
        store.record(EvidenceOutcome.POSITIVE, "2026-10-01", independence = IndependenceClass.ASSISTED, assistance = listOf(AssistanceLevel.H2))
        recompute.recompute(skill)
        val entry = LearningHistoryQuery(store).load().entries.single()
        assertEquals(listOf(AssistanceLevel.H2), entry.assistance)
        assertEquals(EvidenceKind.ASSISTED, entry.kind)
    }

    @Test
    fun `a session's report says what its own program change report said, plan changes included`() {
        val (store, clock, _) = mastered()
        assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, Content, clock).build(capacity))
        val before = CaptureProgramSnapshot(store, clock).capture()
        val session = store.appendTruth(TruthRecord("assessment_session", clock.now(), mapOf("scope" to "daily")))
        store.record(EvidenceOutcome.NEGATIVE, "2026-10-01", session = session)
        store.record(EvidenceOutcome.POSITIVE, "2026-10-01", independence = IndependenceClass.ASSISTED, session = session)
        val reported = ReportProgramChanges(store, Content, clock).report(before, listOf(skill), capacity)

        val report = LearningHistoryQuery(store).load().sessions.single()
        assertEquals(session, report.sessionId)
        assertTrue(report.evidence.isNotEmpty() && report.evidence.all { it.family == HistoryFamily.ASSESSMENT }, "a session's own work is assessment")
        assertEquals("daily", report.scope)
        assertEquals(reported.report.stateChanges.map { it.skill to it.kind }.toSet(), report.stateChanges.map { it.skill to it.kind }.toSet())
        assertTrue(report.replanFound)
        assertEquals(reported.report.planChanges, report.planChanges)
        // The assisted answer could not be measured independently; it is named, never counted as a failure.
        assertEquals(listOf(EvidenceKind.ASSISTED), report.notMeasured.evidence.map { it.kind })
        assertEquals(emptyList(), report.notMeasured.unansweredSlots, "a daily session composes no blueprint")
    }

    @Test
    fun `without a plan written for the session, no plan change is claimed`() {
        val (store, clock, recompute) = mastered()
        val session = store.appendTruth(TruthRecord("assessment_session", clock.now(), mapOf("scope" to "daily")))
        store.record(EvidenceOutcome.POSITIVE, "2026-10-01", session = session); recompute.recompute(skill)
        val report = LearningHistoryQuery(store).load().sessions.single()
        assertEquals(false, report.replanFound)
        assertEquals(emptyList(), report.planChanges)
    }

    @Test
    fun `one Skill's history has only that Skill and no reports`() {
        val (store, _, _) = mastered()
        val facts = LearningHistoryQuery(store).load(skill)
        assertTrue(facts.entries.isNotEmpty() && facts.entries.all { it.skill == skill })
        assertTrue(facts.sessions.isEmpty())
        assertEquals("Loops", facts.skillNames[skill])
    }
}
