package coach.application

import coach.model.CurriculumPackage
import coach.model.DailyCapacityInput
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.MasteryAxisState
import coach.model.NeedTrigger
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveGateProfile
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.RetentionAxis
import coach.model.RetentionReason
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StudyTimestamp
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
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Retention rebuilt from evidence and moved by the day (13C), against a store that keeps what it is given,
 * and the planner reading the result.
 */
class RebuildRetentionTest {

    private val skill = VersionedRef("skill.python.loops", 1)
    private val objective = VersionedRef("objective.python.loops.trace", 1)
    private val profiles = listOf(ObjectiveGateProfile(objective, required = true, critical = false,
        acceptableEvidenceTypes = listOf("code_reading"), directEvidenceTypes = listOf("code_reading")))

    private class Clock(var day: String) : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, day, 3 * 3600)
    }

    private class Store(val skillRow: SkillRow?) : PersistencePort {
        var evidence: List<EvidenceRow> = emptyList()
        var watermark = 42L
        var curriculumVersion: Int? = 1
        val projections = mutableMapOf<String, ProjectionRecord>()
        val truth = mutableListOf<StoredTruth>()

        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long {
            val id = truth.size + 1L
            truth += StoredTruth(id, record)
            return id
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = truth.firstOrNull { it.id == id && it.record.kind == kind }?.record
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) { projections[record.key] = record }
        override fun curriculumPublished(): Boolean = curriculumVersion != null
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = evidence.filter { it.objective == objective }
        override fun truthWatermark(): Long = watermark
        override fun latestCurriculumVersion(): Int? = curriculumVersion
        override fun skill(ref: VersionedRef): SkillRow? = skillRow?.takeIf { it.ref == ref }
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = listOfNotNull(skillRow)
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: coach.model.AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: coach.model.AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<coach.model.ObjectiveRow> = emptyList()
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> =
            projections.filterKeys { it.startsWith("retention_state:") }.values.filter { row ->
                val next = row.payload["next_review_on_study_day"].orEmpty()
                row.payload["state"] in setOf("fresh", "stable") && next.isNotEmpty() && next <= studyDay
            }.map { row ->
                val ref = row.key.removePrefix("retention_state:")
                VersionedRef(ref.substringBefore("@v"), ref.substringAfter("@v").toInt())
            }
    }

    private object NoContent : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef) = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: coach.model.LearningNeed) = emptyList<coach.model.TaskCandidate>()
        override fun assessmentItemsFor(skill: VersionedRef) = emptyList<coach.model.AssessmentItem>()
        override fun explanationsFor(objective: VersionedRef): List<coach.model.ExplanationVariant> = emptyList()
    }

    private fun row(profile: String = "standard", critical: Boolean = false) =
        SkillRow(skill, skill.logicalId, "capability", "published", "concept", profile, critical, "src", "authored")

    private var nextId = 1L

    private fun evidence(day: String, outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE, family: String = "family.$nextId", studyDay: String? = day): EvidenceRow {
        val id = nextId++
        return EvidenceRow(id = id, sequence = id, objective = objective, skill = skill, evidenceType = "code_reading",
            outcome = outcome, evaluatorStatus = EvaluatorStatus.VERIFIED, independenceClass = IndependenceClass.INDEPENDENT,
            contested = false, quality = if (outcome == EvidenceOutcome.POSITIVE) 1.0 else 0.0, difficulty = null,
            variantFamilyId = family, resource = VersionedRef("item.$id", 1), studyDay = studyDay)
    }

    private fun retentionRow(store: Store) = assertNotNull(store.projections["retention_state:${skill.logicalId}@v1"])
    private fun skillRow(store: Store) = assertNotNull(store.projections["skill_state:${skill.logicalId}@v1"])

    @Test
    fun `a mastered Skill gets a fresh schedule, and skill_state carries the retention axis`() {
        val store = Store(row())
        store.evidence = listOf(evidence("2026-09-21"), evidence("2026-09-21"))
        val clock = Clock("2026-09-21")
        RebuildMastery(store, clock).rebuild(skill, profiles)
        store.watermark = 50
        val rebuilt = RebuildRetention(store, clock).rebuild(skill, profiles)
        assertEquals(RetentionAxis.FRESH, rebuilt.axis)
        val retention = retentionRow(store).payload
        assertEquals("fresh", retention["state"])
        assertEquals("4", retention["current_interval_days"])
        assertEquals("2026-09-25", retention["next_review_on_study_day"])
        assertEquals("standard", retention["retention_profile"])
        assertEquals("RVR-v0", retentionRow(store).policyVersion)
        assertEquals(50, retentionRow(store).truthWatermark)
        val state = skillRow(store)
        assertEquals("fresh", state.payload["retention_axis_state"])
        assertEquals(MasteryAxisState.CONFIRMED_CURRENT.id, state.payload["mastery_axis_state"])
        // The row never claims more truth than its oldest axis saw: mastery was built at 42.
        assertEquals(42, state.truthWatermark)
        assertTrue(store.truth.isEmpty(), "a rebuild writes no truth")
    }

    @Test
    fun `the replayed mastery timeline agrees with the mastery engine rebuilt after every row`() {
        val store = Store(row())
        val clock = Clock("2026-09-30")
        val rows = listOf(evidence("2026-09-21"), evidence("2026-09-21"),
            evidence("2026-09-25", EvidenceOutcome.NEGATIVE), evidence("2026-09-26", EvidenceOutcome.NEGATIVE))
        val mastery = RebuildMastery(store, clock)
        rows.indices.forEach { i ->
            store.evidence = rows.take(i + 1)
            mastery.rebuild(skill, profiles)
        }
        val masteredNow = skillRow(store).payload["mastery_axis_state"] in
            setOf(MasteryAxisState.CONFIRMED_CURRENT.id, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE.id)
        assertFalse(masteredNow)
        val retention = RebuildRetention(store, clock).rebuild(skill, profiles)
        assertEquals(RetentionAxis.UNTRACKED, retention.axis)
        assertEquals(listOf(RetentionReason.RETENTION_RECHECK_FAIL, RetentionReason.REMEDIATION_AFTER_RETENTION_FAILURE),
            retention.snapshot.reasons)
        // One failure only: verification is open and mastery is kept.
        store.evidence = rows.take(3)
        assertEquals(RetentionAxis.VERIFICATION_DUE, RebuildRetention(store, clock).rebuild(skill, profiles).axis)
    }

    @Test
    fun `the day moves a fresh schedule to review_due, and nothing else is touched`() {
        val store = Store(row())
        store.evidence = listOf(evidence("2026-09-21"), evidence("2026-09-21"))
        val clock = Clock("2026-09-21")
        RebuildRetention(store, clock).rebuild(skill, profiles)
        val before = retentionRow(store)

        clock.day = "2026-09-24"
        assertTrue(RefreshDueRetention(store, clock).refresh().nowDue.isEmpty())
        assertEquals("fresh", retentionRow(store).payload["state"])

        clock.day = "2026-09-25"
        store.watermark = 99
        val refreshed = RefreshDueRetention(store, clock).refresh()
        assertEquals(listOf(skill), refreshed.nowDue)
        val after = retentionRow(store)
        assertEquals("review_due", after.payload["state"])
        assertEquals("FIRST_DELAYED_REVIEW,REVIEW_DUE", after.payload["reason_codes"])
        // No truth was read, only the day: the watermark is the one the schedule was built from.
        assertEquals(before.truthWatermark, after.truthWatermark)
        assertEquals(before.payload["next_review_on_study_day"], after.payload["next_review_on_study_day"])
        assertEquals("review_due", skillRow(store).payload["retention_axis_state"])
        assertTrue(store.truth.isEmpty())
    }

    @Test
    fun `a stored row that cannot be read is named, never guessed into a state`() {
        val store = Store(row())
        store.evidence = listOf(evidence("2026-09-21"), evidence("2026-09-21"))
        val clock = Clock("2026-09-21")
        RebuildRetention(store, clock).rebuild(skill, profiles)
        val key = "retention_state:${skill.logicalId}@v1"
        store.projections[key] = store.projections.getValue(key).let { it.copy(payload = it.payload + ("retention_profile" to "medium")) }
        clock.day = "2026-10-01"
        val refreshed = RefreshDueRetention(store, clock).refresh()
        assertEquals(listOf(skill), refreshed.unreadable)
        assertEquals("fresh", store.projections.getValue(key).payload["state"])
    }

    @Test
    fun `nothing published writes nothing, and a row with no study day is refused`() {
        val store = Store(row())
        store.curriculumVersion = null
        store.evidence = listOf(evidence("2026-09-21"), evidence("2026-09-21"))
        assertFalse(RebuildRetention(store, Clock("2026-09-21")).rebuild(skill, profiles).written)
        assertTrue(store.projections.isEmpty())
        store.curriculumVersion = 1
        store.evidence = listOf(evidence("2026-09-21", studyDay = null))
        assertFailsWith<IllegalArgumentException> { RebuildRetention(store, Clock("2026-09-21")).rebuild(skill, profiles) }
    }

    @Test
    fun `a review repeating a family already met, or not direct for its Objective, is not a complex review`() {
        val store = Store(row(profile = "complex"))
        val clock = Clock("2026-10-01")
        val mastery = listOf(evidence("2026-09-21", family = "family.a"), evidence("2026-09-21", family = "family.b"))
        store.evidence = mastery + evidence("2026-09-28", family = "family.a")
        assertEquals(0, RebuildRetention(store, clock).rebuild(skill, profiles).snapshot.successfulDelayedReviews)
        store.evidence = mastery + evidence("2026-09-28").copy(evidenceType = "recognition_quiz")
        assertEquals(0, RebuildRetention(store, clock).rebuild(skill, profiles).snapshot.successfulDelayedReviews)
        store.evidence = mastery + evidence("2026-09-28", family = "family.c")
        assertEquals(1, RebuildRetention(store, clock).rebuild(skill, profiles).snapshot.successfulDelayedReviews)
    }

    @Test
    fun `an unknown authored profile schedules nothing`() {
        val store = Store(row(profile = "medium"))
        store.evidence = listOf(evidence("2026-09-21"), evidence("2026-09-21"))
        val rebuilt = RebuildRetention(store, Clock("2026-12-01")).rebuild(skill, profiles)
        assertEquals(RetentionAxis.NOT_YET_EVALUATED, rebuilt.axis)
        assertEquals("", retentionRow(store).payload["next_review_on_study_day"])
    }

    @Test
    fun `the planner sees a review that came due overnight, and it is not a failure`() {
        val store = Store(row())
        store.evidence = listOf(evidence("2026-09-21"), evidence("2026-09-21"))
        val clock = Clock("2026-09-21")
        RebuildMastery(store, clock).rebuild(skill, profiles)
        RebuildRetention(store, clock).rebuild(skill, profiles)
        val capacity = DailyCapacityInput(normalProfileMinutes = 60, shortProfileMinutes = 30, intensiveProfileMinutes = 90)

        val before = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, NoContent, clock).build(capacity))
        assertTrue(before.trace.needs.none { it.trigger == NeedTrigger.RETENTION_REVIEW_DUE })

        clock.day = "2026-09-25"
        val due = assertIs<BuildDailyPlan.Built.Planned>(BuildDailyPlan(store, NoContent, clock).build(capacity))
        val need = due.trace.needs.single { it.trigger == NeedTrigger.RETENTION_REVIEW_DUE }
        assertEquals(listOf(skill), need.targetSkills)
        assertTrue(due.trace.needs.none { it.trigger == NeedTrigger.VERIFICATION_DUE || it.trigger == NeedTrigger.REMEDIATION_REQUIRED })
        // The mastery axis did not move because a day passed.
        assertEquals(MasteryAxisState.CONFIRMED_CURRENT.id, skillRow(store).payload["mastery_axis_state"])
        assertNull(store.projections.keys.firstOrNull { it.startsWith("weakness_state:") })
    }
}
