package coach.application

import coach.model.AllowedToolsPolicy
import coach.model.AssessmentItem
import coach.model.AssessmentScope
import coach.model.CodeNotMeasured
import coach.model.CodeTest
import coach.model.CodeTestSuite
import coach.model.CodeVerdict
import coach.model.ComponentResult
import coach.model.ContentOrigin
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
import coach.model.OutcomeSignal
import coach.model.PendingReason
import coach.model.PrerequisiteEdge
import coach.model.PublishOutcome
import coach.model.ResourceVersion
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
import kotlin.test.assertTrue

/**
 * Evaluating code (14D): the course's tests decide wherever they exist and no AI is asked about such a task; without
 * tests, an AI may judge only a task that allows a provisional result, and only the task text and the code leave the
 * device. Nothing that went wrong with a report, a runner or an evaluator becomes a wrong answer.
 */
class EvaluateCodeTest {

    private val reads = VersionedRef("objective.py.io.read_two_ints", 1)
    private val adds = VersionedRef("objective.py.arith.add_ints", 1)
    private val skill = VersionedRef("skill.py.sum_two", 1)
    private val itemRef = VersionedRef("item.py.sum_two", 1)

    private val suite = CodeTestSuite(
        VersionedRef("codetest.py.sum_two", 1), itemRef,
        listOf(CodeTest("reads_two_ints", reads), CodeTest("adds_small", adds), CodeTest("adds_negative", adds)),
    )

    // Exactly what `tools/code_test_runner.py` prints for a program that subtracts instead of adding (no build step).
    private val wrongReport = """
        code_test_report/1
        item: item.py.sum_two@v1
        suite: codetest.py.sum_two@v1
        build: not_required
        test: reads_two_ints passed
        test: adds_small failed
        test: adds_negative failed
        end
    """.trimIndent() + "\n"

    private val code = "a, b = map(int, input().split())\nprint(a - b)\n"

    private fun item(required: EvaluatorStatusRequirement) = AssessmentItem(
        ref = itemRef,
        targetObjectives = listOf(reads, adds),
        targetSkills = listOf(skill),
        requiredSkills = emptyList(),
        evidenceType = "coding_production",
        expectedAnswerOrRubricRef = "tests://codetest.py.sum_two@v1",
        evaluatorRequirement = EvaluatorRequirement(required, false, "policy.v1"),
        allowedTools = AllowedToolsPolicy(listOf("compiler", "terminal")),
        independenceMode = IndependenceMode.H0_REQUIRED,
        difficultyClass = "core",
        lifecycleStatus = LifecycleStatus.VALIDATED,
        contentOrigin = ContentOrigin.HUMAN_AUTHORED,
        declaredUseCeiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE,
        scopeEligibility = setOf(AssessmentScope.DAILY_MICRO),
        variantFamilyId = "family.py.sum_two",
    )

    private class Content(val suite: CodeTestSuite?, val task: String? = "İki tamsayıyı okuyup toplamını yazdır.") : ContentPort {
        override fun resource(ref: VersionedRef): ContentDocument? = task?.let { ContentDocument(ref, it) }
        override fun assessmentItem(ref: VersionedRef): AssessmentItem? = null
        override fun curriculumPackage(): CurriculumPackage? = null
        override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = emptyList()
        override fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem> = emptyList()
        override fun explanationsFor(objective: VersionedRef): List<ExplanationVariant> = emptyList()
        override fun codeTestsFor(item: VersionedRef): CodeTestSuite? = suite?.takeIf { it.item == item }
    }

    private class Evaluator(val answer: (EvaluationRequest) -> EvaluationResult) : EvaluatorPort {
        val asked = mutableListOf<EvaluationRequest>()
        override fun evaluate(request: EvaluationRequest): EvaluationResult { asked += request; return answer(request) }
    }

    private val aiRef = EvaluatorRef("provider", "model", "code_evaluation/1")
    private fun aiSays(signal: OutcomeSignal) = Evaluator { EvaluationResult.Provisional(listOf(ComponentResult(adds, signal)), aiRef) }

    @Test
    fun `where the course has tests they decide, and no AI is asked about the task even if it allows a provisional result`() {
        val ai = aiSays(OutcomeSignal.MET)
        for (required in EvaluatorStatusRequirement.entries) {
            val verdict = assertIs<CodeVerdict.Measured>(EvaluateCode(Content(suite), ai).evaluate(item(required), code, wrongReport))
            val result = assertIs<EvaluationResult.Verified>(verdict.result)
            assertEquals(mapOf(reads to OutcomeSignal.MET, adds to OutcomeSignal.NOT_MET), result.componentResults.associate { it.objectiveRef to it.signal })
        }
        assertTrue(ai.asked.isEmpty(), "the code of a tested task never leaves the device")
    }

    @Test
    fun `a tested task without a usable report measures nothing, and says why`() {
        val ai = aiSays(OutcomeSignal.MET)
        val use = EvaluateCode(Content(suite), ai)
        val target = item(EvaluatorStatusRequirement.PROVISIONAL_ALLOWED)
        assertEquals(CodeNotMeasured.REPORT_MISSING, assertIs<CodeVerdict.NotMeasured>(use.evaluate(target, code, null)).reason)
        assertEquals(CodeNotMeasured.REPORT_MISSING, assertIs<CodeVerdict.NotMeasured>(use.evaluate(target, code, "  \n")).reason)
        val malformed = assertIs<CodeVerdict.NotMeasured>(use.evaluate(target, code, "all tests passed!"))
        assertEquals(CodeNotMeasured.REPORT_MALFORMED, malformed.reason)
        assertTrue(malformed.details.isNotEmpty())
        assertTrue(ai.asked.isEmpty(), "a missing or broken report is never replaced by an AI's opinion")
    }

    @Test
    fun `a task that needs a verified result and has no tests is never put to an AI`() {
        val ai = aiSays(OutcomeSignal.MET)
        val use = EvaluateCode(Content(null), ai)
        val target = item(EvaluatorStatusRequirement.VERIFIED)
        assertEquals(CodeNotMeasured.TESTS_REQUIRED, assertIs<CodeVerdict.NotMeasured>(use.evaluate(target, code, null)).reason)
        assertEquals(CodeNotMeasured.NO_SUITE_FOR_REPORT, assertIs<CodeVerdict.NotMeasured>(use.evaluate(target, code, wrongReport)).reason)
        assertTrue(ai.asked.isEmpty())
    }

    @Test
    fun `without tests an AI may judge a task that allows it — provisionally, and from the task text and the code alone`() {
        val ai = aiSays(OutcomeSignal.NOT_MET)
        val verdict = assertIs<CodeVerdict.Measured>(EvaluateCode(Content(null), ai).evaluate(item(EvaluatorStatusRequirement.PROVISIONAL_ALLOWED), code, null))
        assertIs<EvaluationResult.Provisional>(verdict.result)
        assertEquals(listOf(EvaluationRequest(listOf(reads, adds), "İki tamsayıyı okuyup toplamını yazdır.", code)), ai.asked)
    }

    @Test
    fun `an evaluator that gives no answer, or answers outside its contract, is never a wrong answer`() {
        val target = item(EvaluatorStatusRequirement.PROVISIONAL_ALLOWED)
        for (reason in PendingReason.entries) {
            val verdict = assertIs<CodeVerdict.NotMeasured>(
                EvaluateCode(Content(null), Evaluator { EvaluationResult.EvaluationPending(reason) }).evaluate(target, code, null))
            assertEquals(CodeNotMeasured.EVALUATOR_DID_NOT_ANSWER, verdict.reason)
            assertEquals(reason, verdict.pending)
        }
        val claimsVerified = Evaluator { EvaluationResult.Verified(listOf(ComponentResult(adds, OutcomeSignal.NOT_MET)), aiRef) }
        val verdict = assertIs<CodeVerdict.NotMeasured>(EvaluateCode(Content(null), claimsVerified).evaluate(target, code, null))
        assertEquals(PendingReason.INVALID_RESPONSE, verdict.pending)
    }

    @Test
    fun `nothing is sent for an empty submission, for a task with no text, or with a report no tests can check`() {
        val ai = aiSays(OutcomeSignal.MET)
        val target = item(EvaluatorStatusRequirement.PROVISIONAL_ALLOWED)
        assertEquals(CodeNotMeasured.NOTHING_SUBMITTED, assertIs<CodeVerdict.NotMeasured>(EvaluateCode(Content(null), ai).evaluate(target, " \n", null)).reason)
        assertEquals(CodeNotMeasured.TASK_TEXT_MISSING, assertIs<CodeVerdict.NotMeasured>(EvaluateCode(Content(null, task = null), ai).evaluate(target, code, null)).reason)
        assertEquals(CodeNotMeasured.NO_SUITE_FOR_REPORT, assertIs<CodeVerdict.NotMeasured>(EvaluateCode(Content(null), ai).evaluate(target, code, wrongReport)).reason)
        assertTrue(ai.asked.isEmpty())
    }

    @Test
    fun `a measured result is recorded like any other evaluation — verified, one row per Objective, an unrun test as invalid`() {
        val unrun = wrongReport.replace("test: reads_two_ints passed", "test: reads_two_ints error")
        val verdict = assertIs<CodeVerdict.Measured>(EvaluateCode(Content(suite), aiSays(OutcomeSignal.MET)).evaluate(item(EvaluatorStatusRequirement.VERIFIED), code, unrun))
        val store = Store()
        RecordEvidence(store, clock).record(7, skill, "coding_production", verdict.result, IndependenceClass.INDEPENDENT)
        val rows = store.appended.filter { it.kind == "evidence_event" }.map { it.payload }
        assertEquals(listOf("invalid", "negative"), rows.map { it["outcome"] })
        assertTrue(rows.all { it["evaluator_status"] == "verified" && it["evaluator"] == "deterministic/code_tests@codetest.py.sum_two@v1|code_test_report/1" })
    }

    private val clock = object : ClockPort {
        override fun now() = StudyTimestamp(1_790_000_000_000, "2026-10-01", 3 * 3600)
    }

    private class Store : PersistencePort {
        val appended = mutableListOf<TruthRecord>()
        override fun <T> inTransaction(block: () -> T): T = block()
        override fun appendTruth(record: TruthRecord): Long { appended += record; return appended.size.toLong() }
        override fun readTruth(kind: String, id: Long): TruthRecord? = null
        override fun readProjection(key: String): ProjectionRecord? = null
        override fun writeProjection(record: ProjectionRecord) = error("recording writes no projection")
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
