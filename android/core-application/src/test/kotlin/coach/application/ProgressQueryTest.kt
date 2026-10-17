package coach.application

import coach.model.AssessmentScope
import coach.model.AttentionQualifier
import coach.model.CurriculumPackage
import coach.model.EvaluatorStatus
import coach.model.EvidenceKind
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PrerequisiteEdge
import coach.model.PrerequisiteEligibility
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillPresentationProjection
import coach.model.SkillPresentationState
import coach.model.SkillRow
import coach.model.SkillStateProjection
import coach.model.StoredPlan
import coach.model.StudyTimestamp
import coach.model.UnevaluatedAxis
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.ports.ClockPort
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
 * 16A (`SKAX-v0`): Progress reads each published Skill's axes as their engines wrote them, asks the prerequisite gate
 * now, and lets one function decide the state — the same function the recompute writes the stored column with.
 */
class ProgressQueryTest {

    private val basics = VersionedRef("skill.python.basics", 1)
    private val loops = VersionedRef("skill.python.loops", 1)
    private val basicsObjective = VersionedRef("objective.python.basics.write", 1)
    private val loopsObjective = VersionedRef("objective.python.loops.trace", 1)

    private class Clock(var day: String) : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, day, 3 * 3600)
    }

    private class Store(val skills: List<SkillRow>, val objectives: List<ObjectiveRow>, val edges: List<PrerequisiteEdge>) : PersistencePort {
        val evidence = mutableListOf<EvidenceRow>()
        val projections = mutableMapOf<String, ProjectionRecord>()
        var published = true

        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long = error("Progress writes no truth")
        override fun readTruth(kind: String, id: Long): TruthRecord? =
            evidence.firstOrNull { it.id == id }?.let { TruthRecord(kind, StudyTimestamp(0, it.studyDay!!, 0), emptyMap()) }
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) { projections[record.key] = record }
        override fun curriculumPublished(): Boolean = published
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = evidence.filter { it.objective == objective }
        override fun truthWatermark(): Long = evidence.maxOfOrNull { it.sequence } ?: 0L
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = skills.firstOrNull { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = edges.filter { it.target == target }
        override fun publishedSkills(): List<SkillRow> = skills
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = evidence.map { it.skill }.distinct()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = objectives.filter { it.parentSkill == skill }
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()
    }

    private fun skillRow(ref: VersionedRef, name: String) =
        SkillRow(ref, name, "statement of $name", "published", "concept", "standard", false, "src", "authored")

    private fun world(): Triple<Store, Clock, RecomputeSkillState> {
        val store = Store(
            skills = listOf(skillRow(basics, "Basics"), skillRow(loops, "Loops")),
            objectives = listOf(
                ObjectiveRow(basicsObjective, basics, true, "standard", listOf("code_writing"), listOf("code_writing"), null),
                ObjectiveRow(loopsObjective, loops, true, "standard", listOf("code_reading"), listOf("code_reading"), null),
            ),
            edges = listOf(PrerequisiteEdge(basics, loops, 1, "hard", "conceptual_dependency", "default_prg_v0", "published", "authored")),
        )
        val clock = Clock("2026-10-01")
        return Triple(store, clock, RecomputeSkillState(store, clock))
    }

    private var nextEvidence = 1L

    private fun Store.record(
        skill: VersionedRef,
        objective: VersionedRef,
        outcome: EvidenceOutcome,
        day: String,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        status: EvaluatorStatus = EvaluatorStatus.VERIFIED,
    ) {
        val id = nextEvidence++
        evidence += EvidenceRow(id = id, sequence = id, objective = objective, skill = skill, evidenceType = "code_writing",
            outcome = outcome, evaluatorStatus = status, independenceClass = independence, contested = false,
            quality = if (outcome == EvidenceOutcome.POSITIVE) 1.0 else 0.0, difficulty = null, variantFamilyId = "family.$id",
            resource = VersionedRef("item.$id", 1), studyDay = day)
    }

    private fun ProgressQuery.rowOf(skill: VersionedRef) = load().skills.single { it.skill == skill }

    private fun stateOf(projection: SkillStateProjection) = assertIs<SkillStateProjection.Derived>(projection).state

    @Test
    fun `a learner who has done nothing is not yet evidenced, and a Skill waits on its own prerequisite`() {
        val (store, _, _) = world()
        val query = ProgressQuery(store)
        assertEquals(SkillPresentationState.NOT_YET_EVIDENCED, stateOf(query.rowOf(basics).projection))
        val loopsRow = query.rowOf(loops)
        assertEquals(SkillPresentationState.PREREQUISITE_UNRESOLVED, stateOf(loopsRow.projection))
        assertEquals(PrerequisiteEligibility.BLOCKED, loopsRow.prerequisite.eligibility)
        assertEquals(listOf(basics), loopsRow.prerequisite.hardBlockers)
        // Progress only reads.
        assertTrue(store.projections.isEmpty())
    }

    @Test
    fun `nothing published is said, not shown as empty Skills`() {
        val (store, _, _) = world()
        store.published = false
        val facts = ProgressQuery(store).load()
        assertEquals(false, facts.curriculumLoaded)
        assertTrue(facts.skills.isEmpty())
    }

    @Test
    fun `evidence no engine has read yet is recomputing, never nothing yet`() {
        val (store, _, _) = world()
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-09-21")
        val projection = assertIs<SkillStateProjection.Recomputing>(ProgressQuery(store).rowOf(basics).projection)
        assertEquals(listOf(UnevaluatedAxis.MASTERY), projection.unevaluated)
    }

    @Test
    fun `after the recompute the Skill is confirmed, and its dependent no longer waits`() {
        val (store, _, recompute) = world()
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-09-30"); recompute.recompute(basics)
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-10-01"); recompute.recompute(basics)
        val query = ProgressQuery(store)
        val row = query.rowOf(basics)
        assertEquals(SkillPresentationState.CONFIRMED_CURRENT, stateOf(row.projection))
        // The stored column is written by the same function the read uses.
        assertEquals("confirmed_current", store.projections.getValue("skill_state:${basics.logicalId}@v1").payload["primary_presentation_state"])
        assertEquals("eligible", store.projections.getValue("skill_state:${basics.logicalId}@v1").payload["prerequisite_axis_state"])
        // The dependent's row was never re-assembled; Progress asks the gate now instead of trusting a stale axis.
        assertEquals(SkillPresentationState.NOT_YET_EVIDENCED, stateOf(query.rowOf(loops).projection))
        assertEquals(PrerequisiteEligibility.ELIGIBLE, query.rowOf(loops).prerequisite.eligibility)
    }

    @Test
    fun `when the first review comes the Skill stays confirmed, with review due`() {
        val (store, clock, recompute) = world()
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-09-30"); recompute.recompute(basics)
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-10-01"); recompute.recompute(basics)
        clock.day = "2026-11-30"
        recompute.recompute(basics)
        assertEquals(SkillPresentationState.CONFIRMED_REVIEW_DUE, stateOf(ProgressQuery(store).rowOf(basics).projection))
    }

    @Test
    fun `a contradiction after confirmation is a verification, and a row older than new evidence is recomputing`() {
        val (store, _, recompute) = world()
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-09-21"); recompute.recompute(basics)
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-09-22"); recompute.recompute(basics)
        store.record(basics, basicsObjective, EvidenceOutcome.NEGATIVE, "2026-09-25")
        val stale = assertIs<SkillStateProjection.Recomputing>(ProgressQuery(store).rowOf(basics).projection)
        assertTrue(stale.stale)
        recompute.recompute(basics)
        assertEquals(SkillPresentationState.CONFIRMATION_VERIFICATION_DUE, stateOf(ProgressQuery(store).rowOf(basics).projection))
    }

    @Test
    fun `assisted work alone develops with support and carries no confirmation`() {
        val (store, _, recompute) = world()
        repeat(3) { i ->
            store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-09-2$i", independence = IndependenceClass.ASSISTED)
            recompute.recompute(basics)
        }
        assertEquals(SkillPresentationState.DEVELOPING_WITH_SUPPORT, stateOf(ProgressQuery(store).rowOf(basics).projection))
    }

    @Test
    fun `a provisional judgement is a qualifier and is counted as provisional, never as independent`() {
        val (store, _, recompute) = world()
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-09-21", status = EvaluatorStatus.PROVISIONAL)
        recompute.recompute(basics)
        val query = ProgressQuery(store)
        val projection = assertIs<SkillStateProjection.Derived>(query.rowOf(basics).projection)
        assertTrue(AttentionQualifier.PROVISIONAL_EVALUATION_PRESENT in projection.qualifiers)
        val detail = query.detail(basics)!!
        assertEquals(1, detail.evidence.counts[EvidenceKind.PROVISIONAL])
        assertEquals(0, detail.evidence.counts[EvidenceKind.INDEPENDENT])
    }

    @Test
    fun `skill detail shows the same state as the overview, every Objective, and names its blocker`() {
        val (store, _, _) = world()
        val query = ProgressQuery(store)
        val detail = query.detail(loops)!!
        assertEquals(query.rowOf(loops), detail.row)
        assertEquals(listOf(loopsObjective), detail.objectives.map { it.objective })
        assertNull(detail.objectives.single().gatesPassed)
        assertEquals("Basics", detail.skillNames[basics])
        assertNull(query.detail(VersionedRef("skill.unpublished", 1)))
    }

    @Test
    fun `the assembled row keeps its watermark and derives the presentation from its own axes`() {
        val (store, clock, recompute) = world()
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-09-21"); recompute.recompute(basics)
        val before = store.projections.getValue("skill_state:${basics.logicalId}@v1")
        val assembled = AssembleSkillState(store, clock).assemble(basics)!!
        val after = store.projections.getValue("skill_state:${basics.logicalId}@v1")
        assertEquals(before.truthWatermark, after.truthWatermark)
        assertEquals(PrerequisiteEligibility.ELIGIBLE, assembled.prerequisite)
        assertEquals(SkillPresentationProjection.storedId(SkillStateRows.reading(after.payload, evidenced = true)), assembled.presentation)
        // A Skill no engine has written has no row to assemble.
        assertNull(AssembleSkillState(store, clock).assemble(loops))
    }

    @Test
    fun `every axis writer alone leaves the presentation its row's own axes determine`() {
        val (store, clock, recompute) = world()
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-09-30"); recompute.recompute(basics)
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-10-01"); recompute.recompute(basics)
        val key = "skill_state:${basics.logicalId}@v1"
        assertEquals("confirmed_current", store.projections.getValue(key).payload["primary_presentation_state"])
        val profiles = store.objectivesOf(basics).map(coach.model.ObjectiveGateProfiles::of)
        fun consistent(writer: String) {
            val payload = store.projections.getValue(key).payload
            assertEquals(SkillPresentationProjection.storedId(SkillStateRows.reading(payload, evidenced = true)),
                payload["primary_presentation_state"], "$writer left $payload")
        }
        // The planner refreshes due reviews with the retention engine alone (13C): the column must follow the new axis.
        clock.day = "2026-11-30"
        RebuildRetention(store, clock).rebuild(basics, profiles)
        consistent("retention")
        assertEquals("confirmed_review_due", store.projections.getValue(key).payload["primary_presentation_state"])
        store.record(basics, basicsObjective, EvidenceOutcome.NEGATIVE, "2026-11-30")
        RebuildMastery(store, clock).rebuild(basics, profiles)
        consistent("mastery")
        RebuildWeakness(store, clock).rebuild(basics, profiles)
        consistent("weakness")
    }

    @Test
    fun `an axis write alone never leaves an old presentation behind`() {
        val (store, clock, _) = world()
        store.record(basics, basicsObjective, EvidenceOutcome.POSITIVE, "2026-09-21")
        val profiles = store.objectivesOf(basics).map(coach.model.ObjectiveGateProfiles::of)
        RebuildMastery(store, clock).rebuild(basics, profiles)
        // The weakness axis is not written yet, so the state is not derivable: not a guess and not the mastery value.
        assertEquals(SkillPresentationProjection.NOT_DERIVED,
            store.projections.getValue("skill_state:${basics.logicalId}@v1").payload["primary_presentation_state"])
    }
}
