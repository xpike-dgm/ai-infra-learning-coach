package coach.application

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.AttemptSubmission
import coach.model.CodeTestSuite
import coach.model.ComprehensionCheck
import coach.model.ComprehensionKind
import coach.model.ComprehensionOffer
import coach.model.ComprehensionResult
import coach.model.ContentOrigin
import coach.model.CurriculumPackage
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatusRequirement
import coach.model.EvidenceRow
import coach.model.ExplanationVariant
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.IndependenceMode
import coach.model.InstructionMode
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MisconceptionRow
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.PrerequisiteEdge
import coach.model.ProvenanceOrigin
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StudyTimestamp
import coach.model.AssistanceTiming
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
import coach.model.TutorRules
import coach.model.UseCeiling
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.model.independence
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
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The comprehension check after code the learner did not write alone (14E): written checks first and judged by their
 * key, the tutor's question only where nothing is written and never evidence, and AI-written code opening a fresh
 * independent check rather than counting as practice that measured nothing.
 */
class CheckUnderstandingTest {

    private val writes = VersionedRef("objective.c.pointers.write_through", 1)
    private val skill = VersionedRef("skill.c.pointers", 1)
    private val itemRef = VersionedRef("item.c.pointers.set_five", 1)

    private val item = AssessmentItem(
        ref = itemRef,
        targetObjectives = listOf(writes),
        targetSkills = listOf(skill),
        requiredSkills = emptyList(),
        evidenceType = "coding_production",
        expectedAnswerOrRubricRef = "tests://codetest.c.pointers.set_five@v1",
        evaluatorRequirement = EvaluatorRequirement(EvaluatorStatusRequirement.VERIFIED, false, "policy.v1"),
        allowedTools = AllowedToolsPolicy(listOf("compiler")),
        independenceMode = IndependenceMode.H0_REQUIRED,
        difficultyClass = "core",
        lifecycleStatus = LifecycleStatus.VALIDATED,
        contentOrigin = ContentOrigin.HUMAN_AUTHORED,
        declaredUseCeiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE,
        scopeEligibility = setOf(AssessmentScope.DAILY_MICRO),
        variantFamilyId = "family.c.pointers.set_five",
    )

    private val check = ComprehensionCheck(
        VersionedRef("comprehension.c.pointers.set_five_star", 1), itemRef, writes, ComprehensionKind.STATE_EFFECT, "explanation",
        "*p = 5; satırı neyi değiştirir?", mapOf("a" to "p'nin tuttuğu adresi", "b" to "p'nin gösterdiği yerdeki değeri"), "b",
    )

    private val generated = AttemptSubmission(resource = itemRef, artifactContentRef = "inline:code", provenance = ProvenanceOrigin.GENERATED_OR_COPIED)

    private class Content(val checks: List<ComprehensionCheck>) : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = null
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = emptyList()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = emptyList()
        override fun explanationsFor(objective: VersionedRef): List<ExplanationVariant> = emptyList()
        override fun codeTestsFor(item: VersionedRef): CodeTestSuite? = null
        override fun comprehensionChecksFor(item: VersionedRef): List<ComprehensionCheck> = checks.filter { it.item == item }
    }

    private class Tutor : TutorPort {
        val asked = mutableListOf<TutorRequest>()
        override fun assist(request: TutorRequest): TutorReply {
            asked += request
            return TutorReply.Delivered(TutorContent(request.intent, "*p = 5 satırını silersen x ne olur?", null, request.instructionMode),
                TutorRef("recorded", "recorded", TutorInstructions.VERSION))
        }
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_790_000_000_000, "2026-10-02", 3 * 3600)
    }

    private fun practiceRequest(): TutorRequest = assertIs<TutorPreparation.Ready>(TutorRules.prepare(TutorAsk(
        TutorIntent.CHECK_UNDERSTANDING, TaskPurpose.PRACTICE, AssistanceTiming.AFTER_SUBMIT, null, consequenceAcknowledged = true,
        instructionMode = InstructionMode.TURKISH_PRIMARY, context = TutorContext(listOf(writes), "x'i p üzerinden 5 yap.", learnerWork = "*p = 5;"),
    ))).request

    @Test
    fun `written checks are offered first, judged by their key and recorded under their own evidence type`() {
        val store = Store()
        val tutor = Tutor()
        val use = CheckUnderstanding(Content(listOf(check)), AskTutor(tutor, store, clock))
        assertEquals(ComprehensionOffer.Written(listOf(check)), use.offer(item, generated))
        val measured = assertIs<ComprehensionResult.Measured>(use.answer(item, check, "b"))
        RecordEvidence(store, clock).record(9, skill, measured.check.evidenceType, measured.result, IndependenceClass.INDEPENDENT)
        val row = store.appended.single { it.kind == "evidence_event" }.payload
        assertEquals("explanation", row["evidence_type"], "never the item's own coding_production")
        assertEquals("positive", row["outcome"])
        assertEquals("verified", row["evaluator_status"])
        assertTrue(tutor.asked.isEmpty())
    }

    @Test
    fun `a skipped check writes nothing, and only a check written for this item version is answered`() {
        val use = CheckUnderstanding(Content(listOf(check)), AskTutor(Tutor(), Store(), clock))
        assertEquals(ComprehensionResult.Skipped, use.answer(item, check, null))
        assertFailsWith<IllegalArgumentException> { use.answer(item, check.copy(ref = VersionedRef("comprehension.c.pointers.other", 1)), "b") }
    }

    @Test
    fun `the tutor asks only where nothing is written, and what it asks is practice, never evidence`() {
        val store = Store()
        val tutor = Tutor()
        val written = CheckUnderstanding(Content(listOf(check)), AskTutor(tutor, store, clock))
        assertNull(written.practice(item, practiceRequest()), "written first")
        assertTrue(tutor.asked.isEmpty())
        val none = CheckUnderstanding(Content(emptyList()), AskTutor(tutor, store, clock))
        assertEquals(ComprehensionOffer.TutorPractice, none.offer(item, generated))
        assertIs<TutorOutcome.Shown>(none.practice(item, practiceRequest()))
        assertEquals(1, tutor.asked.size)
        assertTrue(store.appended.none { it.kind == "evidence_event" }, "the tutor's question is never evidence")
        assertFailsWith<IllegalArgumentException> {
            none.practice(item, assertIs<TutorPreparation.Ready>(TutorRules.prepare(TutorAsk(TutorIntent.QUESTION, TaskPurpose.PRACTICE,
                AssistanceTiming.AFTER_SUBMIT, null, true, InstructionMode.TURKISH_PRIMARY, TutorContext(listOf(writes), "x"), learnerQuestion = "neden?"))).request)
        }
    }

    @Test
    fun `code the learner did not write opens a fresh independent check, and is not offered a check when they wrote it`() {
        assertEquals(IndependenceClass.REQUIRES_INDEPENDENT_RECHECK, generated.independence(TaskPurpose.PRACTICE))
        assertEquals(IndependenceClass.PRACTICE_ONLY, generated.independence(TaskPurpose.TEACH))
        val own = generated.copy(provenance = ProvenanceOrigin.USER_AUTHORED)
        assertEquals(ComprehensionOffer.NotOffered, CheckUnderstanding(Content(listOf(check)), AskTutor(Tutor(), Store(), clock)).offer(item, own))
        assertFailsWith<IllegalArgumentException> {
            CheckUnderstanding(Content(listOf(check)), AskTutor(Tutor(), Store(), clock)).offer(item, own.copy(resource = VersionedRef("item.other", 1)))
        }
    }

    private class Store : PersistencePort {
        val appended = mutableListOf<TruthRecord>()
        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long { appended += record; return appended.size.toLong() }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) = error("no projection is written here")
        override fun curriculumPublished(): Boolean = true
        override fun publishCurriculum(curriculum: CurriculumPackage, publishedAtInstant: Long): PublishOutcome = error("no publishing")
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
        override fun latestPlan(): StoredPlan? = null
        override fun resumeCheckpointRows(): List<StoredTruth> = emptyList()
        override fun latestAssessmentSessionIn(scope: AssessmentScope, format: String): StoredTruth? = null
        override fun latestAssessmentSession(scope: AssessmentScope): StoredTruth? = null
        override fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact> = emptyList()
        override fun skillsEvidencedSince(studyDay: String): List<VersionedRef> = emptyList()
        override fun retentionDueBy(studyDay: String): List<VersionedRef> = emptyList()
        override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = emptyList()
        override fun misconceptionsOf(objective: VersionedRef): List<MisconceptionRow> = emptyList()
    }
}
