package coach.application

import coach.engines.MasteryEngine
import coach.model.CurriculumPackage
import coach.model.EvaluatorStatus
import coach.model.EvidenceOutcome
import coach.model.EvidenceRow
import coach.model.IndependenceClass
import coach.model.MasteryAxisState
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveGateProfile
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.StudyTimestamp
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import coach.model.SkillRow
import coach.model.PrerequisiteEdge
import coach.model.StoredPlan
import coach.ports.StoredTruth

/**
 * Rebuilding the mastery projection: what it writes, what it refuses to write, and what it leaves
 * to other engines. The storage guarantees behind it are proven against real SQLite in T2.
 */
class RebuildMasteryTest {

    private val skill = VersionedRef("skill.python.loops", 1)
    private val objective = VersionedRef("objective.python.loops.trace", 1)

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, "2026-09-21", 3 * 3600)
    }

    private fun profile(critical: Boolean = false) = ObjectiveGateProfile(
        ref = objective,
        required = true,
        critical = critical,
        acceptableEvidenceTypes = listOf("code_reading"),
        directEvidenceTypes = listOf("code_reading"),
    )

    private var nextId = 1L

    private fun evidence(
        quality: Double = 1.0,
        outcome: EvidenceOutcome = EvidenceOutcome.POSITIVE,
        independence: IndependenceClass = IndependenceClass.INDEPENDENT,
        family: String = "family.a",
    ): EvidenceRow {
        val id = nextId++
        return EvidenceRow(
            id = id, sequence = id, objective = objective, skill = skill, evidenceType = "code_reading",
            outcome = outcome, evaluatorStatus = EvaluatorStatus.VERIFIED, independenceClass = independence,
            contested = false, quality = quality, difficulty = null, variantFamilyId = family,
        )
    }

    private class FakeStore(
        var evidence: List<EvidenceRow> = emptyList(),
        var watermark: Long = 42,
        var curriculumVersion: Int? = 1,
    ) : PersistencePort {
        val projections = mutableMapOf<String, ProjectionRecord>()
        val truthWrites = mutableListOf<TruthRecord>()

        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long {
            truthWrites += record
            return 1
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = projections[key]
        override fun writeProjection(record: ProjectionRecord) {
            projections[record.key] = record
        }
        override fun curriculumPublished(): Boolean = curriculumVersion != null
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome =
            error("the mastery engine publishes nothing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = evidence
        override fun truthWatermark(): Long = watermark
        override fun latestCurriculumVersion(): Int? = curriculumVersion
        override fun skill(ref: VersionedRef): SkillRow? = null
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = emptyList()
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSession(scope: coach.model.AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<coach.model.ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
    }

    private fun skillKey() = "skill_state:${skill.logicalId}@v1"

    @Test
    fun `a mastered Skill is written with the provenance DDM-v0 requires`() {
        val store = FakeStore(evidence = listOf(evidence(family = "family.a"), evidence(family = "family.b")))
        val rebuilt = RebuildMastery(store, clock).rebuild(skill, listOf(profile()))

        assertTrue(rebuilt.decision.mastered)
        val row = assertNotNull(store.projections[skillKey()])
        assertEquals(MasteryAxisState.CONFIRMED_CURRENT.id, row.payload.getValue("mastery_axis_state"))
        assertEquals(MasteryEngine.MASTERY_FORMULA_VERSION, row.policyVersion)
        assertEquals(42, row.truthWatermark)
        assertEquals(clock.now().instantEpochMillis, row.builtAtInstant)
        assertEquals(1, row.inputCurriculumVersion)
        assertEquals("passed", assertNotNull(store.projections["objective_state:${objective.logicalId}@v1"])
            .payload.getValue("state"))
    }

    @Test
    fun `the rebuild writes no truth at all`() {
        val store = FakeStore(evidence = listOf(evidence(), evidence(family = "family.b")))
        RebuildMastery(store, clock).rebuild(skill, listOf(profile()))
        assertTrue(store.truthWrites.isEmpty(), "rebuilding a projection wrote truth")
    }

    @Test
    fun `it writes only the mastery axis and carries the other engines' axes`() {
        val store = FakeStore(evidence = listOf(evidence(), evidence(family = "family.b")))
        store.projections[skillKey()] = ProjectionRecord(
            key = skillKey(), policyVersion = "RVR-v0", truthWatermark = 1, builtAtInstant = 1,
            inputCurriculumVersion = 1,
            payload = mapOf(
                "mastery_axis_state" to "not_yet_evidenced",
                "retention_axis_state" to "review_due",
                "prerequisite_axis_state" to "ready",
                "weakness_axis_state" to "none",
                "primary_presentation_state" to "not_yet_evidenced",
            ),
        )
        RebuildMastery(store, clock).rebuild(skill, listOf(profile()))

        val row = assertNotNull(store.projections[skillKey()])
        assertEquals("confirmed_current", row.payload.getValue("mastery_axis_state"))
        assertEquals("review_due", row.payload.getValue("retention_axis_state"))
        assertEquals("ready", row.payload.getValue("prerequisite_axis_state"))
        assertEquals("none", row.payload.getValue("weakness_axis_state"))
    }

    @Test
    fun `rebuilding twice from the same evidence writes the same row`() {
        val store = FakeStore(evidence = listOf(evidence(), evidence(family = "family.b")))
        val engine = RebuildMastery(store, clock)
        engine.rebuild(skill, listOf(profile()))
        val first = assertNotNull(store.projections[skillKey()])
        store.projections.clear()
        engine.rebuild(skill, listOf(profile()))
        assertEquals(first, assertNotNull(store.projections[skillKey()]))
    }

    @Test
    fun `with nothing published the projection is not pinned to a version that does not exist`() {
        val store = FakeStore(
            evidence = listOf(evidence(), evidence(family = "family.b")), curriculumVersion = null,
        )
        val rebuilt = RebuildMastery(store, clock).rebuild(skill, listOf(profile()))
        assertTrue(rebuilt.decision.mastered)
        assertTrue(store.projections.isEmpty(), "a projection was pinned to no curriculum version")
    }

    @Test
    fun `a contradiction after mastery opens verification rather than erasing it`() {
        val store = FakeStore(evidence = listOf(evidence(), evidence(family = "family.b")))
        val engine = RebuildMastery(store, clock)
        engine.rebuild(skill, listOf(profile()))

        store.evidence = store.evidence + evidence(quality = 0.0, outcome = EvidenceOutcome.NEGATIVE, family = "family.c")
        val second = engine.rebuild(skill, listOf(profile()))
        assertTrue(second.decision.mastered, "one contradiction erased a confirmed Skill")
        assertEquals(
            MasteryAxisState.CONFIRMATION_VERIFICATION_DUE.id,
            assertNotNull(store.projections[skillKey()]).payload.getValue("mastery_axis_state"),
        )

        store.evidence = store.evidence + evidence(quality = 0.0, outcome = EvidenceOutcome.NEGATIVE, family = "family.d")
        val third = engine.rebuild(skill, listOf(profile()))
        assertFalse(third.decision.mastered, "the failed recheck did not let the gates decide again")
    }

    @Test
    fun `assisted evidence alone leaves the Skill developing with support`() {
        val store = FakeStore(
            evidence = listOf(
                evidence(independence = IndependenceClass.ASSISTED),
                evidence(independence = IndependenceClass.ASSISTED, family = "family.b"),
            ),
        )
        val rebuilt = RebuildMastery(store, clock).rebuild(skill, listOf(profile()))
        assertFalse(rebuilt.decision.mastered)
        assertEquals(
            MasteryAxisState.DEVELOPING_WITH_SUPPORT.id,
            assertNotNull(store.projections[skillKey()]).payload.getValue("mastery_axis_state"),
        )
    }

    @Test
    fun `the watermark is read before the evidence, so a stale row is detectable rather than wrong`() {
        val store = FakeStore(evidence = listOf(evidence(), evidence(family = "family.b")), watermark = 7)
        RebuildMastery(store, clock).rebuild(skill, listOf(profile()))
        assertEquals(7, assertNotNull(store.projections[skillKey()]).truthWatermark)
    }
}
