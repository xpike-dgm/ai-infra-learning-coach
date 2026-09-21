package coach.application

import coach.model.AssistanceEvent
import coach.model.AssistanceLevel
import coach.model.AssistanceScope
import coach.model.AssistanceSource
import coach.model.AssistanceTiming
import coach.model.AttemptSubmission
import coach.model.CurriculumPackage
import coach.model.ObjectiveEvidenceProfile
import coach.model.ProvenanceOrigin
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
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The shape of the submission use case: one transaction, every row linked, no evidence. The storage
 * guarantee that the rows really commit or roll back together is proven against real SQLite in T2.
 */
class SubmitAttemptTest {

    private class RecordingStore : PersistencePort {
        val appended = mutableListOf<TruthRecord>()
        var transactions = 0
        var inside = false
        private var nextId = 100L
        override fun <T> inTransaction(block: () -> T): T {
            transactions += 1
            inside = true
            try { return block() } finally { inside = false }
        }
        override fun appendTruth(record: TruthRecord): Long {
            check(inside) { "${record.kind} was written outside the learner action's transaction" }
            appended += record
            return nextId++
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) = error("the runner writes no projection")
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome =
            error("this use case publishes no curriculum")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_789_000_000_000, "2026-09-19", 3 * 3600)
    }

    private fun submission(
        assistance: List<AssistanceEvent> = emptyList(),
        provenance: ProvenanceOrigin = ProvenanceOrigin.USER_AUTHORED,
    ) = AttemptSubmission(
        resource = VersionedRef("item.python.loops.q1", 1),
        artifactContentRef = "artifact://draft/1",
        provenance = provenance,
        assistance = assistance,
    )

    private fun help(level: AssistanceLevel, scope: AssistanceScope = AssistanceScope.TARGET_OBJECTIVE) =
        AssistanceEvent(level, AssistanceTiming.DURING_ATTEMPT, scope, AssistanceSource.DETERMINISTIC_CONTENT, true)

    @Test
    fun `an attempt is recorded as exactly one transaction`() {
        val store = RecordingStore()
        SubmitAttempt(store, clock).submit(submission(listOf(help(AssistanceLevel.H1), help(AssistanceLevel.H2))))
        assertEquals(1, store.transactions)
        assertEquals(
            listOf("attempt", "artifact", "artifact_provenance", "assistance_event", "assistance_event"),
            store.appended.map { it.kind },
        )
    }

    @Test
    fun `the runner writes no evidence`() {
        val store = RecordingStore()
        SubmitAttempt(store, clock).submit(submission(listOf(help(AssistanceLevel.H4))))
        assertFalse(
            store.appended.any { it.kind.startsWith("evidence") },
            "the runner is not the evidence evaluator (TRUX-v0)",
        )
    }

    @Test
    fun `every row of the action is linked to the rows it belongs to`() {
        val store = RecordingStore()
        val recorded = SubmitAttempt(store, clock).submit(submission(listOf(help(AssistanceLevel.H2))))
        val byKind = store.appended.groupBy { it.kind }
        assertEquals(recorded.attemptId.toString(), byKind.getValue("artifact").single().payload["attempt_id"])
        assertEquals(recorded.artifactId.toString(), byKind.getValue("artifact_provenance").single().payload["artifact_id"])
        assertEquals(recorded.attemptId.toString(), byKind.getValue("assistance_event").single().payload["attempt_id"])
    }

    @Test
    fun `the learner's provenance answer is recorded as given, unknown included`() {
        val store = RecordingStore()
        SubmitAttempt(store, clock).submit(submission(provenance = ProvenanceOrigin.UNKNOWN_PROVENANCE))
        assertEquals(
            "unknown_provenance",
            store.appended.single { it.kind == "artifact_provenance" }.payload["origin"],
        )
    }

    @Test
    fun `all rows of one action share one timestamp`() {
        val store = RecordingStore()
        SubmitAttempt(store, clock).submit(submission(listOf(help(AssistanceLevel.H1))))
        assertEquals(1, store.appended.map { it.recordedAt }.distinct().size)
    }

    @Test
    fun `revealed target reasoning raises the recheck flag, derived rather than stored`() {
        assertTrue(submission(listOf(help(AssistanceLevel.H3))).requiresIndependentRecheck)
        assertTrue(submission(listOf(help(AssistanceLevel.H4))).requiresIndependentRecheck)
        assertFalse(submission(listOf(help(AssistanceLevel.H2))).requiresIndependentRecheck)
        assertFalse(
            submission(listOf(help(AssistanceLevel.H4, AssistanceScope.NON_TARGET_SUPPORT))).requiresIndependentRecheck,
            "help with the environment does not reveal the target reasoning",
        )
        val store = RecordingStore()
        SubmitAttempt(store, clock).submit(submission(listOf(help(AssistanceLevel.H4))))
        assertFalse(
            store.appended.any { record -> record.payload.keys.any { "recheck" in it || "highest" in it } },
            "a derived fact stored beside its source is a second source of truth",
        )
    }

    @Test
    fun `an attempt cannot be submitted without the artifact it froze`() {
        assertFailsWith<IllegalArgumentException> {
            AttemptSubmission(VersionedRef("item.python.loops.q1", 1), " ", ProvenanceOrigin.USER_AUTHORED)
        }
    }
}
