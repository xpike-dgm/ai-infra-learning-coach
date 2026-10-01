package coach.application

import coach.model.AssessmentScope
import coach.model.AssistanceLevel
import coach.model.AssistanceSource
import coach.model.AssistanceTiming
import coach.model.AuthoredHelp
import coach.model.CurriculumPackage
import coach.model.EvidenceRow
import coach.model.ExposureFact
import coach.model.InstructionMode
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PendingReason
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StudyTimestamp
import coach.model.TaskPurpose
import coach.model.TutorAsk
import coach.model.TutorContent
import coach.model.TutorContext
import coach.model.TutorIntent
import coach.model.TutorInstructions
import coach.model.TutorOutcome
import coach.model.TutorPreparation
import coach.model.TutorRef
import coach.model.TutorReply
import coach.model.TutorRequest
import coach.model.TutorRules
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import coach.ports.TutorPort
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Asking the tutor through the port (14A, `TUTX-v0` §10): the tutor proposes, the rules decide what is shown,
 * and the only write is the exposure a shown solution creates. The storage side of that exposure — that a
 * later attempt on the same family reads as solution-exposed — is proven against real SQLite in T2.
 */
class AskTutorTest {

    private class RecordingStore : PersistencePort {
        val appended = mutableListOf<TruthRecord>()
        private var inside = false
        override fun <T> inTransaction(block: () -> T): T {
            inside = true
            try { return block() } finally { inside = false }
        }
        override fun appendTruth(record: TruthRecord): Long {
            check(inside) { "${record.kind} was written outside a transaction" }
            appended += record
            return appended.size.toLong()
        }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) = error("asking the tutor writes no projection")
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = error("the tutor reads no evidence")
        override fun truthWatermark(): Long = 0
        override fun latestCurriculumVersion(): Int? = null
        override fun skill(ref: VersionedRef): SkillRow? = null
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = emptyList()
        override fun latestPlan(): StoredPlan? = error("the tutor reads no plan")
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = emptyList()
        override fun misconceptionsOf(objective: VersionedRef): List<coach.model.MisconceptionRow> = emptyList()
    }

    private class ScriptedTutor(private val answer: (TutorRequest) -> TutorReply) : TutorPort {
        val asked = mutableListOf<TutorRequest>()
        override fun assist(request: TutorRequest): TutorReply {
            asked += request
            return answer(request)
        }
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_790_000_000_000, "2026-10-01", 3 * 3600)
    }
    private val ref = TutorRef("recorded", "recorded", TutorInstructions.VERSION)
    private val item = AskTutor.Item(VersionedRef("item.c.pointers.q1", 2), "family.c.pointers.write_through")
    private val context = TutorContext(listOf(VersionedRef("objective.c.pointers.write_through", 1)), "Make x equal 5 through p.", learnerWork = "p = 5;")

    private fun request(
        intent: TutorIntent = TutorIntent.HINT,
        timing: AssistanceTiming? = AssistanceTiming.DURING_ATTEMPT,
        ceiling: AssistanceLevel? = AssistanceLevel.H4,
        purpose: TaskPurpose = TaskPurpose.PRACTICE,
    ) = assertIs<TutorPreparation.Ready>(
        TutorRules.prepare(TutorAsk(intent, purpose, timing, ceiling, true, InstructionMode.TURKISH_PRIMARY, context))
    ).request

    private fun echo(level: AssistanceLevel?) = { r: TutorRequest ->
        TutorReply.Delivered(TutorContent(r.intent, "*p = 5; yazınca x değişir.", level, r.instructionMode), ref)
    }

    @Test
    fun `a solution shown on an item is an exposure from the moment it is shown`() {
        val store = RecordingStore()
        val outcome = AskTutor(ScriptedTutor(echo(AssistanceLevel.H4)), store, clock).ask(request(), item)
        assertIs<TutorOutcome.Shown>(outcome)
        val exposure = store.appended.single()
        assertEquals("exposure_record", exposure.kind)
        assertEquals(
            mapOf(
                "resource_logical_id" to "item.c.pointers.q1",
                "resource_version" to "2",
                "variant_family_id" to "family.c.pointers.write_through",
                "exposure_kind" to "solution_exposure",
                "max_exposure_level" to "H4",
            ),
            exposure.payload,
        )
    }

    @Test
    fun `help that reveals no solution writes nothing`() {
        val store = RecordingStore()
        val outcome = AskTutor(ScriptedTutor(echo(AssistanceLevel.H2)), store, clock).ask(request(ceiling = AssistanceLevel.H2), item)
        assertEquals(AssistanceLevel.H2, assertIs<TutorOutcome.Shown>(outcome).event!!.level)
        assertTrue(store.appended.isEmpty())
    }

    @Test
    fun `nothing shown, nothing written`() {
        for (reason in PendingReason.entries) {
            val store = RecordingStore()
            val outcome = AskTutor(ScriptedTutor { TutorReply.NotDelivered(reason) }, store, clock).ask(request(), item)
            assertEquals(TutorOutcome.NotShown(reason), outcome)
            assertTrue(store.appended.isEmpty(), "$reason")
        }
        val store = RecordingStore()
        val tooFar = AskTutor(ScriptedTutor(echo(AssistanceLevel.H3)), store, clock).ask(request(ceiling = AssistanceLevel.H2), item)
        assertEquals(TutorOutcome.NotShown(PendingReason.INVALID_RESPONSE), tooFar)
        assertTrue(store.appended.isEmpty())
    }

    @Test
    fun `an explanation of a frozen answer is an exposure tied to that attempt`() {
        val store = RecordingStore()
        val outcome = AskTutor(ScriptedTutor(echo(AssistanceLevel.H4)), store, clock)
            .ask(request(TutorIntent.EXPLAIN_MISTAKE, AssistanceTiming.AFTER_SUBMIT, ceiling = null), item, attemptId = 41)
        assertEquals(AssistanceTiming.AFTER_SUBMIT, assertIs<TutorOutcome.Shown>(outcome).event!!.timing)
        assertEquals("41", store.appended.single().payload["source_attempt_id"])
    }

    @Test
    fun `the shipped null tutor answers nothing, and the learner still gets authored help`() {
        val store = RecordingStore()
        val authored = AuthoredHelp(TutorIntent.HINT, AssistanceLevel.H1, "Hangi değişkenin değişmesi gerekiyor?", VersionedRef("hint.c.pointers.1", 1))
        val shown = assertIs<TutorOutcome.Shown>(AskTutor(NullTutor, store, clock).ask(request(ceiling = AssistanceLevel.H2), item, authored))
        assertEquals(AssistanceSource.DETERMINISTIC_CONTENT, shown.source)
        assertEquals(AssistanceLevel.H1, shown.event!!.level)
        assertNull(shown.tutorRef)
        assertTrue(store.appended.isEmpty())
        assertEquals(TutorOutcome.NotShown(PendingReason.UNAVAILABLE), AskTutor(NullTutor, store, clock).ask(request(), item))
    }

    @Test
    fun `the port is asked exactly what the rules prepared, and nothing is asked twice`() {
        val tutor = ScriptedTutor { TutorReply.NotDelivered(PendingReason.TIMED_OUT) }
        val prepared = request(ceiling = AssistanceLevel.H1)
        AskTutor(tutor, RecordingStore(), clock).ask(prepared, item)
        assertEquals(listOf(prepared), tutor.asked)
    }
}
