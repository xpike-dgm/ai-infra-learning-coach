package coach.application

import coach.model.AcceptedAnswers
import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.CodeTestSuite
import coach.model.ComponentResult
import coach.model.ComprehensionCheck
import coach.model.ContentOrigin
import coach.model.CriterionVerdict
import coach.model.CurriculumPackage
import coach.model.EvaluationResult
import coach.model.EvaluatorRef
import coach.model.EvaluatorRequirement
import coach.model.EvaluatorStatusRequirement
import coach.model.EvidenceRow
import coach.model.ExplanationVariant
import coach.model.ExposureFact
import coach.model.IndependenceClass
import coach.model.IndependenceMode
import coach.model.LearningNeed
import coach.model.LifecycleStatus
import coach.model.MisconceptionRow
import coach.model.ObjectiveEvidenceProfile
import coach.model.ObjectiveRow
import coach.model.OpenResponseNotMeasured
import coach.model.OpenResponseVerdict
import coach.model.OutcomeSignal
import coach.model.PendingReason
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
import coach.model.Rubric
import coach.model.RubricCriterion
import coach.model.RubricFinding
import coach.model.SkillRow
import coach.model.StoredPlan
import coach.model.StudyTimestamp
import coach.model.TaskCandidate
import coach.model.UseCeiling
import coach.model.ValidationRecord
import coach.model.VersionedRef
import coach.ports.ClockPort
import coach.ports.ContentDocument
import coach.ports.ContentPort
import coach.ports.EvaluationRequest
import coach.ports.EvaluatorPort
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
 * Evaluating an open response (14F): the key decides short answers without an AI; a rubric goes to the evaluator only
 * where the task allows a provisional result, with only the task, the rubric, the answer and the catalog's labels; a
 * response that waits writes nothing; the self-check is a recorded exposure, never evidence.
 */
class EvaluateOpenResponseTest {

    private val explains = VersionedRef("objective.c.pointers.explain_write_through", 1)
    private val skill = VersionedRef("skill.c.pointers", 1)
    private val itemRef = VersionedRef("item.c.pointers.explain_star", 1)
    private val label = MisconceptionRow(VersionedRef("misconception.c.pointers.address_value", 1), explains, "adres ile değer", "Adres ile değeri karıştırmış olabilir misin?")

    private fun item(required: EvaluatorStatusRequirement = EvaluatorStatusRequirement.PROVISIONAL_ALLOWED) = AssessmentItem(
        ref = itemRef,
        targetObjectives = listOf(explains),
        targetSkills = listOf(skill),
        requiredSkills = emptyList(),
        evidenceType = "explanation",
        expectedAnswerOrRubricRef = "rubric://rubric.c.pointers.explain_star@v1",
        evaluatorRequirement = EvaluatorRequirement(required, false, "policy.v1"),
        allowedTools = AllowedToolsPolicy(emptyList()),
        independenceMode = IndependenceMode.H0_REQUIRED,
        difficultyClass = "core",
        lifecycleStatus = LifecycleStatus.VALIDATED,
        contentOrigin = ContentOrigin.HUMAN_AUTHORED,
        declaredUseCeiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE,
        scopeEligibility = setOf(AssessmentScope.DAILY_MICRO),
        variantFamilyId = "family.c.pointers.explain_star",
    )

    private val rubric = Rubric(VersionedRef("rubric.c.pointers.explain_star", 1), itemRef, listOf(
        RubricCriterion("names_target", explains, "*p, p'nin gösterdiği yeri ifade eder."),
        RubricCriterion("names_effect", explains, "*p = 5, o yerdeki değeri 5 yapar."),
    ))
    private val key = AcceptedAnswers(VersionedRef("answerkey.c.pointers.star_name", 1), itemRef, explains, listOf("dereference"), false)

    private class Content(val key: AcceptedAnswers?, val rubric: Rubric?, val task: String? = "*p = 5; satırını açıkla.") : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = task?.let { ContentDocument(ref, it) }
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = emptyList()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = emptyList()
        override fun explanationsFor(objective: VersionedRef): List<ExplanationVariant> = emptyList()
        override fun codeTestsFor(item: VersionedRef): CodeTestSuite? = null
        override fun comprehensionChecksFor(item: VersionedRef): List<ComprehensionCheck> = emptyList()
        override fun answerKeyFor(item: VersionedRef): AcceptedAnswers? = key?.takeIf { it.item == item }
        override fun rubricFor(item: VersionedRef): Rubric? = rubric?.takeIf { it.item == item }
    }

    private class Evaluator(val answer: (EvaluationRequest) -> EvaluationResult) : EvaluatorPort {
        val asked = mutableListOf<EvaluationRequest>()
        override fun evaluate(request: EvaluationRequest): EvaluationResult { asked += request; return answer(request) }
    }

    private val aiRef = EvaluatorRef("provider", "model", "open_response_evaluation/1")
    private fun aiFinds(target: CriterionVerdict, effect: CriterionVerdict) = Evaluator {
        EvaluationResult.Provisional(listOf(ComponentResult(explains, OutcomeSignal.MET)), aiRef,
            rubricFindings = listOf(RubricFinding("names_target", target), RubricFinding("names_effect", effect)))
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_790_000_000_000, "2026-10-02", 3 * 3600)
    }

    private fun use(content: Content, evaluator: EvaluatorPort, store: Store = Store()) = EvaluateOpenResponse(content, store, clock, evaluator)

    @Test
    fun `a short answer is judged by the course's key and no AI is asked`() {
        val ai = aiFinds(CriterionVerdict.MET, CriterionVerdict.MET)
        val verdict = assertIs<OpenResponseVerdict.Measured>(use(Content(key, rubric), ai).evaluate(item(EvaluatorStatusRequirement.VERIFIED), "Dereference"))
        assertEquals(listOf(ComponentResult(explains, OutcomeSignal.MET)), assertIs<EvaluationResult.Verified>(verdict.result).componentResults)
        assertIs<OpenResponseVerdict.Measured>(use(Content(key, rubric), ai).evaluate(item(), "pointer"))
        assertTrue(ai.asked.isEmpty(), "a non-matching short answer never falls to an AI")
    }

    @Test
    fun `a rubric goes to the evaluator with the task, the rubric, the answer and the catalog's labels — nothing else`() {
        val ai = aiFinds(CriterionVerdict.MET, CriterionVerdict.NOT_MET)
        val verdict = assertIs<OpenResponseVerdict.Measured>(use(Content(null, rubric), ai, Store(listOf(label))).evaluate(item(), "p'nin gösterdiği yer"))
        val result = assertIs<EvaluationResult.Provisional>(verdict.result)
        assertEquals(listOf(ComponentResult(explains, OutcomeSignal.PARTIALLY_MET)), result.componentResults, "core decided, not the evaluator's own MET")
        assertEquals(listOf(EvaluationRequest(listOf(explains), "*p = 5; satırını açıkla.", "p'nin gösterdiği yer", rubric.criteria,
            listOf("misconception.c.pointers.address_value"))), ai.asked)
    }

    @Test
    fun `a task that needs a verified result, an empty answer or a missing task sends nothing`() {
        val ai = aiFinds(CriterionVerdict.MET, CriterionVerdict.MET)
        assertEquals(OpenResponseNotMeasured.VERIFIED_EVALUATOR_REQUIRED,
            assertIs<OpenResponseVerdict.NotMeasured>(use(Content(null, rubric), ai).evaluate(item(EvaluatorStatusRequirement.VERIFIED), "x")).reason)
        assertEquals(OpenResponseNotMeasured.NOTHING_SUBMITTED, assertIs<OpenResponseVerdict.NotMeasured>(use(Content(null, rubric), ai).evaluate(item(), " ")).reason)
        assertEquals(OpenResponseNotMeasured.TASK_TEXT_MISSING,
            assertIs<OpenResponseVerdict.NotMeasured>(use(Content(null, rubric, task = null), ai).evaluate(item(), "x")).reason)
        assertEquals(OpenResponseNotMeasured.NOTHING_TO_JUDGE_BY, assertIs<OpenResponseVerdict.NotMeasured>(use(Content(null, null), ai).evaluate(item(), "x")).reason)
        assertTrue(ai.asked.isEmpty())
    }

    @Test
    fun `an evaluator that gives no answer leaves the response waiting, and it is asked exactly once`() {
        for (reason in PendingReason.entries) {
            val ai = Evaluator { EvaluationResult.EvaluationPending(reason) }
            val verdict = assertIs<OpenResponseVerdict.NotMeasured>(use(Content(null, rubric), ai).evaluate(item(), "x"))
            assertEquals(OpenResponseNotMeasured.EVALUATOR_DID_NOT_ANSWER, verdict.reason)
            assertEquals(reason, verdict.pending)
            assertEquals(1, ai.asked.size, "nothing retries on its own")
        }
    }

    @Test
    fun `the self-check shows the rubric only as a recorded exposure, and writes no evidence`() {
        val store = Store()
        assertEquals(rubric.criteria, use(Content(null, rubric), aiFinds(CriterionVerdict.MET, CriterionVerdict.MET), store).selfCheck(item(), 42))
        val exposure = store.appended.single()
        assertEquals("exposure_record", exposure.kind)
        assertEquals("solution_exposure", exposure.payload["exposure_kind"])
        assertEquals("family.c.pointers.explain_star", exposure.payload["variant_family_id"])
        assertEquals("42", exposure.payload["source_attempt_id"])
        assertTrue(store.appended.none { it.kind == "evidence_event" })
        assertNull(use(Content(null, null), aiFinds(CriterionVerdict.MET, CriterionVerdict.MET), store).selfCheck(item(), 42))
    }

    @Test
    fun `a provisional result is recorded as provisional, never as verified`() {
        val verdict = assertIs<OpenResponseVerdict.Measured>(use(Content(null, rubric), aiFinds(CriterionVerdict.MET, CriterionVerdict.MET)).evaluate(item(), "x"))
        val store = Store()
        RecordEvidence(store, clock).record(5, skill, "explanation", verdict.result, IndependenceClass.INDEPENDENT)
        val row = store.appended.single { it.kind == "evidence_event" }.payload
        assertEquals("provisional", row["evaluator_status"])
        assertEquals("positive", row["outcome"])
    }

    private class Store(val catalog: List<MisconceptionRow> = emptyList()) : PersistencePort {
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
        override fun misconceptionsOf(objective: VersionedRef): List<MisconceptionRow> = catalog.filter { it.objective == objective }
    }
}
