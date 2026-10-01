package coach.application

import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.AssistanceLevel
import coach.model.AssistanceSource
import coach.model.AssistanceTiming
import coach.model.CurriculumPackage
import coach.model.EvidenceRow
import coach.model.ExplanationForm
import coach.model.ExplanationSource
import coach.model.ExplanationVariant
import coach.model.ExposureFact
import coach.model.InstructionMode
import coach.model.LearningNeed
import coach.model.MisconceptionRow
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PendingReason
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StudyTimestamp
import coach.model.TaskCandidate
import coach.model.TaskPurpose
import coach.model.TutorAsk
import coach.model.TutorContent
import coach.model.TutorContext
import coach.model.TutorInstructions
import coach.model.TutorIntent
import coach.model.TutorOutcome
import coach.model.TutorPreparation
import coach.model.TutorRef
import coach.model.TutorReply
import coach.model.TutorRequest
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.ContentDocument
import coach.ports.ContentPort
import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.StoredTruth
import coach.ports.TruthRecord
import coach.ports.TutorPort
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Explaining again (14C): the learner chooses the form, written explanations come first, the tutor is asked only where
 * nothing written fits — grounded in the course's own explanation — and the learner's misconception memory never
 * leaves the device.
 */
class ExplainDifferentlyTest {

    private val objective = VersionedRef("objective.c.pointers.write_through", 1)
    private val label = MisconceptionRow(VersionedRef("misconception.c.pointers.address_value", 1), objective,
        "adres ile değer karışıklığı", "Adres ile değeri karıştırmış olabilir misin?")
    private val canonical = ExplanationVariant(VersionedRef("explanation.c.pointers.write_through", 1), objective, ExplanationForm.CANONICAL, null,
        "*p = 5 ifadesi p'nin gösterdiği yere 5 yazar.")
    private val worked = ExplanationVariant(VersionedRef("explanation.c.pointers.write_through_worked", 1), objective, ExplanationForm.WORKED_EXAMPLE,
        AssistanceLevel.H2, "int y = 1; int *q = &y; *q = 7; artık y 7.")
    private val contrast = ExplanationVariant(VersionedRef("explanation.c.pointers.address_value_contrast", 1), objective,
        ExplanationForm.MISCONCEPTION_CONTRAST, AssistanceLevel.H2, "p = 5 adresi değiştirir; *p = 5 değeri.", misconception = label.ref)

    private class Content(val variants: List<ExplanationVariant>) : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = emptyList()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = emptyList()
        override fun explanationsFor(objective: VersionedRef): List<ExplanationVariant> = variants.filter { it.objective == objective }
    }

    private class Store(val catalog: List<MisconceptionRow>, val memory: Map<String, String>) : PersistencePort {
        val appended = mutableListOf<TruthRecord>()
        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long { appended += record; return appended.size.toLong() }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? =
            memory[key]?.let { ProjectionRecord(key, "WLRM-v0", 1, 0, 1, mapOf("state" to it)) }
        override fun writeProjection(record: ProjectionRecord) = error("explaining writes no projection")
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
        override fun resourceVersion(ref: VersionedRef): ResourceVersion? = null
        override fun latestValidation(ref: VersionedRef): ValidationRecord? = null
        override fun objectiveProfile(ref: VersionedRef): ObjectiveEvidenceProfile? = null
        override fun countTruth(kind: String, studyDay: String): Int = 0
        override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = error("explaining reads no evidence")
        override fun truthWatermark(): Long = 0
        override fun latestCurriculumVersion(): Int? = 1
        override fun skill(ref: VersionedRef): SkillRow? = null
        override fun prerequisiteEdgesInto(target: VersionedRef): List<PrerequisiteEdge> = emptyList()
        override fun publishedSkills(): List<SkillRow> = emptyList()
        override fun latestPlan(): StoredPlan? = error("explaining reads no plan")
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = emptyList()
        override fun misconceptionsOf(objective: VersionedRef): List<MisconceptionRow> = catalog.filter { it.objective == objective }
    }

    private class Tutor : TutorPort {
        val asked = mutableListOf<TutorRequest>()
        override fun assist(request: TutorRequest): TutorReply {
            asked += request
            return TutorReply.Delivered(TutorContent(request.intent, "Bir kutu düşün: p kutunun adresini tutar.", request.ceiling, request.instructionMode),
                TutorRef("recorded", "recorded", TutorInstructions.VERSION))
        }
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_790_000_000_000, "2026-10-01", 3 * 3600)
    }

    private fun setup(variants: List<ExplanationVariant>, memory: Map<String, String> = emptyMap(), tutor: TutorPort = Tutor()): Triple<ExplainDifferently, Store, TutorPort> {
        val store = Store(listOf(label), memory)
        return Triple(ExplainDifferently(Content(variants), store, AskTutor(tutor, store, clock)), store, tutor)
    }

    private fun base(timing: AssistanceTiming? = null, ceiling: AssistanceLevel? = null, purpose: TaskPurpose = TaskPurpose.TEACH) =
        TutorAsk(TutorIntent.EXPLAIN_DIFFERENTLY, purpose, timing, ceiling, true, InstructionMode.TURKISH_PRIMARY,
            TutorContext(listOf(objective), "Pointer üzerinden yazma."))

    private fun ready(use: ExplainDifferently, form: ExplanationForm, timing: AssistanceTiming? = null, ceiling: AssistanceLevel? = null) =
        assertIs<TutorPreparation.Ready>(use.prepare(base(timing, ceiling), objective, form)).request

    @Test
    fun `written first, a written explanation is shown and the tutor is never asked`() {
        val (use, _, tutor) = setup(listOf(canonical, worked))
        val shown = assertIs<TutorOutcome.Shown>(use.explain(objective, ExplanationForm.WORKED_EXAMPLE, ready(use, ExplanationForm.WORKED_EXAMPLE)))
        assertEquals(worked.text, shown.text)
        assertEquals(AssistanceSource.DETERMINISTIC_CONTENT, shown.source)
        assertTrue((tutor as Tutor).asked.isEmpty())
    }

    @Test
    fun `without a written explanation the tutor is asked, grounded in the course's own`() {
        val (use, _, tutor) = setup(listOf(canonical))
        val request = ready(use, ExplanationForm.DIFFERENT_EXAMPLE)
        val shown = assertIs<TutorOutcome.Shown>(use.explain(objective, ExplanationForm.DIFFERENT_EXAMPLE, request))
        assertEquals(AssistanceSource.AI_GENERATED, shown.source)
        val asked = (tutor as Tutor).asked.single()
        assertEquals(ExplanationForm.DIFFERENT_EXAMPLE, asked.form)
        assertEquals(canonical.text, asked.context.canonicalExplanation)
        assertTrue("<canonical>" in TutorInstructions.userMessage(asked))
    }

    @Test
    fun `a written explanation above the learner's ceiling is not shown, and the tutor answers within it`() {
        val deep = worked.copy(level = AssistanceLevel.H3)
        val (use, _, tutor) = setup(listOf(canonical, deep))
        val request = ready(use, ExplanationForm.WORKED_EXAMPLE, AssistanceTiming.DURING_ATTEMPT, AssistanceLevel.H2)
        val shown = assertIs<TutorOutcome.Shown>(use.explain(objective, ExplanationForm.WORKED_EXAMPLE, request))
        assertEquals(AssistanceSource.AI_GENERATED, shown.source)
        assertEquals(AssistanceLevel.H2, shown.event!!.level)
        assertEquals(1, (tutor as Tutor).asked.size)
    }

    @Test
    fun `a contrast is offered and shown only from writing, and the learner's memory never leaves the device`() {
        val key = MisconceptionRows.key(label.ref)
        val (closed, _, _) = setup(listOf(canonical, contrast))
        assertTrue(closed.options(objective, emptySet()).none { it.form == ExplanationForm.MISCONCEPTION_CONTRAST })
        val (resolved, _, _) = setup(listOf(canonical, contrast), memory = mapOf(key to "resolved"))
        assertTrue(resolved.options(objective, emptySet()).none { it.form == ExplanationForm.MISCONCEPTION_CONTRAST }, "a resolved label is not open")
        val (use, _, tutor) = setup(listOf(canonical, contrast), memory = mapOf(key to "supported"))
        assertEquals(ExplanationSource.WRITTEN, use.options(objective, emptySet()).single { it.form == ExplanationForm.MISCONCEPTION_CONTRAST }.source)
        val request = ready(use, ExplanationForm.MISCONCEPTION_CONTRAST)
        assertNull(request.form, "the tutor is never asked for a contrast")
        val shown = assertIs<TutorOutcome.Shown>(use.explain(objective, ExplanationForm.MISCONCEPTION_CONTRAST, request))
        assertEquals(contrast.text, shown.text)
        assertTrue((tutor as Tutor).asked.isEmpty())
    }

    @Test
    fun `with nothing written and no tutor answer there is nothing to show, truthfully`() {
        val unavailable = object : TutorPort {
            override fun assist(request: TutorRequest): TutorReply = TutorReply.NotDelivered(PendingReason.UNAVAILABLE)
        }
        val (use, store, _) = setup(listOf(canonical), tutor = unavailable)
        assertEquals(TutorOutcome.NotShown(PendingReason.UNAVAILABLE),
            use.explain(objective, ExplanationForm.STATE_TRACE, ready(use, ExplanationForm.STATE_TRACE)))
        val (closed, _, _) = setup(listOf(canonical, contrast))
        assertNull(closed.explain(objective, ExplanationForm.MISCONCEPTION_CONTRAST, ready(closed, ExplanationForm.MISCONCEPTION_CONTRAST)))
        assertTrue(store.appended.isEmpty())
    }

    @Test
    fun `a written solution shown while an answer is open is an exposure, like one the tutor wrote`() {
        val full = worked.copy(level = AssistanceLevel.H4)
        val (use, store, _) = setup(listOf(canonical, full))
        val request = ready(use, ExplanationForm.WORKED_EXAMPLE, AssistanceTiming.DURING_ATTEMPT, AssistanceLevel.H4)
        val item = AskTutor.Item(VersionedRef("item.c.pointers.q1", 1), "family.write_through")
        val shown = assertIs<TutorOutcome.Shown>(use.explain(objective, ExplanationForm.WORKED_EXAMPLE, request, item))
        assertTrue(shown.revealsTargetReasoning)
        assertEquals("solution_exposure", store.appended.single().payload["exposure_kind"])
        val (lesson, lessonStore, _) = setup(listOf(canonical, full))
        lesson.explain(objective, ExplanationForm.WORKED_EXAMPLE, ready(lesson, ExplanationForm.WORKED_EXAMPLE), item)
        assertFalse(lessonStore.appended.any(), "outside an attempt nothing is recorded")
    }
}
