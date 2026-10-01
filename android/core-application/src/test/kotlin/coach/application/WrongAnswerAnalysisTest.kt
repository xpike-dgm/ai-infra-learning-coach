package coach.application

import coach.model.AssessmentScope
import coach.model.AttributionOutcome
import coach.model.ComponentResult
import coach.model.CurriculumPackage
import coach.model.EvaluationResult
import coach.model.EvaluatorRef
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.MisconceptionHypothesis
import coach.model.MisconceptionRow
import coach.model.MisconceptionSource
import coach.model.MisconceptionTag
import coach.model.MisconceptionTags
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveGateProfile
import coach.model.ObjectiveRow
import coach.model.OutcomeSignal
import coach.model.PendingReason
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StudyTimestamp
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.model.WeaknessSignal
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Wrong-answer analysis end to end in core (14B): the evaluation's proposals become catalog tags on the evidence row,
 * the rebuild remembers every catalog label, and the analysis a learner reads is the stored attribution. Storage
 * itself — the schema refusing what the contract forbids — is proven against real SQLite in T2.
 */
class WrongAnswerAnalysisTest {

    private val skill = VersionedRef("skill.c.pointers", 1)
    private val objective = VersionedRef("objective.c.pointers.write_through", 1)
    private val addressValue = MisconceptionRow(VersionedRef("misconception.c.pointers.address_value", 1), objective,
        "adres ile değer karışıklığı", "Adres ile değeri karıştırmış olabilir misin?")
    private val starAmp = MisconceptionRow(VersionedRef("misconception.c.pointers.star_amp_roles", 1), objective,
        "* ile & rolleri", "* ile & işaretlerinin rolünü karıştırmış olabilir misin?")
    private val profiles = listOf(ObjectiveGateProfile(objective, required = true, critical = false,
        acceptableEvidenceTypes = listOf("coding"), directEvidenceTypes = listOf("coding")))
    private val ref = EvaluatorRef("recorded", "recorded", "schema/1")

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_790_000_000_000, "2026-10-01", 3 * 3600)
    }

    private class Store(val catalog: List<MisconceptionRow>) : PersistencePort {
        val truth = mutableListOf<StoredTruth>()
        var evidence: List<EvidenceRow> = emptyList()
        val projections = mutableMapOf<String, ProjectionRecord>()
        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long {
            val id = 500L + truth.size
            truth += StoredTruth(id, record)
            return id
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = truth.firstOrNull { it.id == id }?.record
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) { projections[record.key] = record }
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = evidence.filter { it.objective == objective }
        override fun truthWatermark(): Long = 77
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = null
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = emptyList()
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = emptyList()
        override fun misconceptionsOf(objective: VersionedRef): List<MisconceptionRow> = catalog.filter { it.objective == objective }
    }

    private fun tagsWritten(store: Store) =
        store.truth.filter { it.record.kind == "evidence_event" }.map { MisconceptionTags.decode(it.record.payload["misconception_tags"]) }

    private fun record(store: Store, evaluation: EvaluationResult) =
        RecordEvidence(store, clock).record(attemptId = 1, skill = skill, evidenceType = "coding", evaluation = evaluation,
            independence = IndependenceClass.INDEPENDENT)

    @Test
    fun `only catalog labels on a row that went wrong are recorded, each with where it came from`() {
        val proposals = listOf(
            MisconceptionHypothesis(objective, addressValue.ref.logicalId),
            MisconceptionHypothesis(objective, "misconception.c.pointers.invented_by_a_model"),
            MisconceptionHypothesis(VersionedRef("objective.other", 1), starAmp.ref.logicalId),
        )
        val det = Store(listOf(addressValue, starAmp))
        record(det, EvaluationResult.Verified(listOf(ComponentResult(objective, OutcomeSignal.NOT_MET)), ref, proposals))
        assertEquals(listOf(listOf(MisconceptionTag(addressValue.ref, MisconceptionSource.DETERMINISTIC))), tagsWritten(det))

        val llm = Store(listOf(addressValue, starAmp))
        record(llm, EvaluationResult.Provisional(listOf(ComponentResult(objective, OutcomeSignal.PARTIALLY_MET)), ref, proposals))
        assertEquals(listOf(listOf(MisconceptionTag(addressValue.ref, MisconceptionSource.AI_PROPOSED))), tagsWritten(llm))

        val right = Store(listOf(addressValue))
        record(right, EvaluationResult.Verified(listOf(ComponentResult(objective, OutcomeSignal.MET)), ref, proposals))
        assertTrue(right.truth.none { it.record.payload.containsKey("misconception_tags") }, "a right answer carries no misconception")

        val pending = Store(listOf(addressValue))
        record(pending, EvaluationResult.EvaluationPending(PendingReason.REFUSED))
        assertTrue(pending.truth.isEmpty(), "a refusal writes nothing, so it can carry no misconception")
    }

    private var next = 1L

    private fun row(tags: List<MisconceptionTag>, outcome: EvidenceOutcome = EvidenceOutcome.NEGATIVE,
                    independence: IndependenceClass = IndependenceClass.INDEPENDENT, prerequisiteValid: Boolean = true): EvidenceRow {
        val id = next++
        return EvidenceRow(id = id, sequence = id, objective = objective, skill = skill, evidenceType = "coding", outcome = outcome,
            evaluatorStatus = EvaluatorStatus.VERIFIED, independenceClass = independence, contested = false,
            quality = if (outcome == EvidenceOutcome.POSITIVE) 1.0 else 0.0, difficulty = null, variantFamilyId = "family.$id",
            resource = VersionedRef("item.$id", 1), studyDay = "2026-10-01", prerequisiteValid = prerequisiteValid, misconceptionTags = tags)
    }

    @Test
    fun `the rebuild remembers every catalog label, carried or not`() {
        val store = Store(listOf(addressValue, starAmp))
        store.evidence = listOf(row(listOf(MisconceptionTag(addressValue.ref, MisconceptionSource.DETERMINISTIC))))
        val rebuilt = RebuildWeakness(store, clock).rebuild(skill, profiles)
        assertEquals(listOf(WeaknessSignal.SUPPORTED, WeaknessSignal.NONE), rebuilt.misconceptions.map { it.signal })
        val carried = assertNotNull(store.projections["misconception_state:${addressValue.ref.logicalId}@v1"])
        assertEquals("supported", carried.payload["state"])
        assertEquals("deterministic", carried.payload["source"])
        assertEquals(objective.logicalId, carried.payload["objective_logical_id"])
        assertEquals("WLRM-v0", carried.policyVersion)
        assertEquals(77, carried.truthWatermark)
        val untouched = assertNotNull(store.projections["misconception_state:${starAmp.ref.logicalId}@v1"])
        assertEquals("none", untouched.payload["state"])
        assertEquals("", untouched.payload["source"])
    }

    @Test
    fun `the analysis a learner reads is the stored attribution, not a second opinion`() {
        val store = Store(listOf(addressValue))
        val contaminated = row(listOf(MisconceptionTag(addressValue.ref, MisconceptionSource.DETERMINISTIC)), prerequisiteValid = false)
        val assisted = row(listOf(MisconceptionTag(addressValue.ref, MisconceptionSource.AI_PROPOSED)), independence = IndependenceClass.ASSISTED)
        store.evidence = listOf(contaminated, assisted)
        val findings = AnalyzeWrongAnswer(store).analyze(skill, profiles, listOf(contaminated.id, assisted.id))
        assertEquals(listOf(AttributionOutcome.PREREQUISITE_SIGNAL, AttributionOutcome.OBJECTIVE_WEAKNESS_HYPOTHESIS), findings.map { it.outcome })
        assertEquals(listOf(addressValue to WeaknessSignal.NONE), findings[0].misconceptions)
        assertEquals(listOf(addressValue to WeaknessSignal.HYPOTHESIS), findings[1].misconceptions)
        assertTrue(store.projections.isEmpty() && store.truth.isEmpty(), "the analysis writes nothing")
        assertNull(AnalyzeWrongAnswer(store).analyze(skill, profiles, listOf(999L)).firstOrNull())
    }
}
