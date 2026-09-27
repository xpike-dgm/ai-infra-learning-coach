package coach.application

import coach.model.ComponentResult
import coach.model.CurriculumPackage
import coach.model.EvaluationResult
import coach.model.EvaluatorRef
import coach.model.EvidenceRow
import coach.model.IndependenceClass
import coach.model.ObjectiveEvidenceProfile
import coach.model.OutcomeSignal
import coach.model.PendingReason
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
import kotlin.test.assertTrue
import coach.model.SkillRow
import coach.model.PrerequisiteEdge

/** Turning an attempt into evidence: the shape of it. Atomicity is proven against SQLite in T2. */
class EvidencePipelineTest {

    private val skill = VersionedRef("skill.python.loops", 1)
    private val first = VersionedRef("objective.python.loops.trace", 1)
    private val second = VersionedRef("objective.python.loops.explain", 1)

    // A second version of the *same* logical Objective. Pinning only ever shows up when two
    // versions exist, so the check that proves it has to use one.
    private val firstV2 = VersionedRef("objective.python.loops.trace", 2)

    private class RecordingStore : PersistencePort {
        val appended = mutableListOf<TruthRecord>()
        var transactions = 0
        var inside = false
        private var nextId = 10L

        override fun <T> inTransaction(block: () -> T): T {
            transactions += 1
            inside = true
            try { return block() } finally { inside = false }
        }
        override fun appendTruth(record: TruthRecord): Long {
            check(inside) { "${record.kind} was written outside the action's transaction" }
            appended += record
            return nextId++
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) = error("the pipeline writes no projection")
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome =
            error("the pipeline publishes nothing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = emptyList()
        override fun truthWatermark(): Long = 0
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = null
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = emptyList()
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, "2026-09-21", 3 * 3600)
    }

    private val evaluatorRef = EvaluatorRef("deterministic", "answer_key", "v1")

    private fun verified(vararg signals: Pair<VersionedRef, OutcomeSignal>) =
        EvaluationResult.Verified(signals.map { ComponentResult(it.first, it.second) }, evaluatorRef)

    @Test
    fun `evidence is written per targeted Objective, in one transaction, with its version pinned`() {
        val store = RecordingStore()
        RecordEvidence(store, clock).record(
            attemptId = 7, skill = skill, evidenceType = "code_reading",
            evaluation = verified(first to OutcomeSignal.MET, second to OutcomeSignal.PARTIALLY_MET),
            independence = IndependenceClass.INDEPENDENT,
        )
        assertEquals(1, store.transactions)
        assertEquals(
            listOf("evidence_event", "evidence_event_objective", "evidence_event", "evidence_event_objective"),
            store.appended.map { it.kind },
        )
        val objectiveRows = store.appended.filter { it.kind == "evidence_event_objective" }
        assertEquals(listOf("1", "1"), objectiveRows.map { it.payload.getValue("objective_version") })
        assertEquals(
            listOf(first.logicalId, second.logicalId),
            objectiveRows.map { it.payload.getValue("objective_logical_id") },
        )
    }

    @Test
    fun `the attribution carries the Objective version the evaluation named`() {
        val store = RecordingStore()
        RecordEvidence(store, clock).record(
            attemptId = 7, skill = skill, evidenceType = "code_reading",
            evaluation = verified(firstV2 to OutcomeSignal.MET, second to OutcomeSignal.MET),
            independence = IndependenceClass.INDEPENDENT,
        )
        val attributions = store.appended.filter { it.kind == "evidence_event_objective" }
        assertEquals(listOf("2", "1"), attributions.map { it.payload.getValue("objective_version") })
        assertEquals(
            listOf(firstV2.logicalId, second.logicalId),
            attributions.map { it.payload.getValue("objective_logical_id") },
        )
    }

    @Test
    fun `the four axes are written separately and never collapsed`() {
        val store = RecordingStore()
        RecordEvidence(store, clock).record(
            attemptId = 7, skill = skill, evidenceType = "code_reading",
            evaluation = EvaluationResult.Provisional(
                listOf(ComponentResult(first, OutcomeSignal.MET)), evaluatorRef,
            ),
            independence = IndependenceClass.ASSISTED,
        )
        val evidence = store.appended.first { it.kind == "evidence_event" }.payload
        assertEquals("positive", evidence.getValue("outcome"))
        assertEquals("provisional", evidence.getValue("evaluator_status"))
        assertEquals("assisted", evidence.getValue("independence_class"))
        assertEquals("0", evidence.getValue("contested"))
    }

    @Test
    fun `a pending evaluation writes nothing at all`() {
        PendingReason.entries.forEach { reason ->
            val store = RecordingStore()
            val recorded = RecordEvidence(store, clock).record(
                attemptId = 7, skill = skill, evidenceType = "code_reading",
                evaluation = EvaluationResult.EvaluationPending(reason),
                independence = IndependenceClass.INDEPENDENT,
            )
            assertTrue(recorded.evidenceIds.isEmpty(), reason.name)
            assertTrue(store.appended.isEmpty(), "a ${reason.name} evaluation produced evidence")
            assertEquals(0, store.transactions)
        }
    }

    @Test
    fun `what could not be measured is invalid with no result, never a wrong answer`() {
        val store = RecordingStore()
        RecordEvidence(store, clock).record(
            attemptId = 7, skill = skill, evidenceType = "code_reading",
            evaluation = verified(first to OutcomeSignal.NOT_RELIABLY_MEASURED),
            independence = IndependenceClass.INDEPENDENT,
        )
        val evidence = store.appended.first { it.kind == "evidence_event" }.payload
        assertEquals("invalid", evidence.getValue("outcome"))
        assertTrue("correctness_or_rubric_result" !in evidence, "an unmeasurable answer was scored zero")
    }

    @Test
    fun `a wrong answer is negative with a zero result, which is a different thing`() {
        val store = RecordingStore()
        RecordEvidence(store, clock).record(
            attemptId = 7, skill = skill, evidenceType = "code_reading",
            evaluation = verified(first to OutcomeSignal.NOT_MET),
            independence = IndependenceClass.INDEPENDENT,
        )
        val evidence = store.appended.first { it.kind == "evidence_event" }.payload
        assertEquals("negative", evidence.getValue("outcome"))
        assertEquals("0.0", evidence.getValue("correctness_or_rubric_result"))
    }

    @Test
    fun `the evaluator that produced the row is recorded on it`() {
        val store = RecordingStore()
        RecordEvidence(store, clock).record(
            attemptId = 7, skill = skill, evidenceType = "code_reading",
            evaluation = verified(first to OutcomeSignal.MET),
            independence = IndependenceClass.INDEPENDENT,
        )
        val evidence = store.appended.first { it.kind == "evidence_event" }.payload
        assertEquals("deterministic/answer_key@v1", evidence.getValue("evaluator"))
        assertEquals(clock.now(), store.appended.first().recordedAt)
    }

    @Test
    fun `the pipeline writes no attempt, artifact or projection of its own`() {
        val store = RecordingStore()
        RecordEvidence(store, clock).record(
            attemptId = 7, skill = skill, evidenceType = "code_reading",
            evaluation = verified(first to OutcomeSignal.MET),
            independence = IndependenceClass.INDEPENDENT,
        )
        assertTrue(store.appended.none { it.kind in setOf("attempt", "artifact", "assistance_event") })
    }
}
