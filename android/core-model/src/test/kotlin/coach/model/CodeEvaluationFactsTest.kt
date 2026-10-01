package coach.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Code evaluation (14D, `CDEX-v0`): what the course's tests prove, read from the real runner's reports
 * (`tools/code_test_runner.py`, stored under `code_test_14d/`), and how far an AI's judgement may go.
 */
class CodeEvaluationFactsTest {

    private val reads = VersionedRef("objective.py.io.read_two_ints", 1)
    private val adds = VersionedRef("objective.py.arith.add_ints", 1)
    private val builds = VersionedRef("objective.py.syntax.valid_program", 1)
    private val itemRef = VersionedRef("item.py.sum_two", 1)
    private val suiteRef = VersionedRef("codetest.py.sum_two", 1)

    private fun item(
        required: EvaluatorStatusRequirement = EvaluatorStatusRequirement.VERIFIED,
        deterministicRequired: Boolean = false,
        deterministic: Boolean = false,
        targets: List<VersionedRef> = listOf(reads, adds, builds),
    ) = AssessmentItem(
        ref = itemRef,
        targetObjectives = targets,
        targetSkills = listOf(VersionedRef("skill.py.sum_two", 1)),
        requiredSkills = emptyList(),
        evidenceType = "coding_production",
        expectedAnswerOrRubricRef = "tests://codetest.py.sum_two@v1",
        evaluatorRequirement = EvaluatorRequirement(required, deterministicRequired, "policy.v1"),
        allowedTools = AllowedToolsPolicy(listOf("compiler", "terminal")),
        independenceMode = IndependenceMode.H0_REQUIRED,
        difficultyClass = "core",
        lifecycleStatus = LifecycleStatus.VALIDATED,
        contentOrigin = ContentOrigin.HUMAN_AUTHORED,
        declaredUseCeiling = UseCeiling.STANDARD_MASTERY_ELIGIBLE,
        scopeEligibility = setOf(AssessmentScope.DAILY_MICRO),
        variantFamilyId = "family.py.sum_two",
        deterministicVerification = deterministic,
    )

    private val suite = CodeTestSuite(
        ref = suiteRef,
        item = itemRef,
        tests = listOf(
            CodeTest("reads_two_ints", reads),
            CodeTest("adds_small", adds, misconceptionOnFailure = "misconception.py.arith.sign_flip"),
            CodeTest("adds_negative", adds),
        ),
        buildObjective = builds,
    )

    private fun report(name: String): CodeTestReport {
        val text = requireNotNull(javaClass.getResource("/code_test_14d/report_$name.txt")).readText()
        return assertIs<CodeTestReports.Decoded.Ok>(CodeTestReports.decode(text)).report
    }

    private fun verified(verdict: CodeVerdict): EvaluationResult.Verified =
        assertIs<EvaluationResult.Verified>(assertIs<CodeVerdict.Measured>(verdict).result)

    private fun signals(verdict: CodeVerdict) = verified(verdict).componentResults.associate { it.objectiveRef to it.signal }

    @Test
    fun `the runner's own report of passing code verifies every Objective it tests, and names its suite as the evaluator`() {
        val result = verified(CodeEvaluation.evaluate(item(), suite, report("correct")))
        assertEquals(mapOf(builds to OutcomeSignal.MET, reads to OutcomeSignal.MET, adds to OutcomeSignal.MET), result.componentResults.associate { it.objectiveRef to it.signal })
        assertEquals(EvaluatorRef("deterministic", "code_tests", "codetest.py.sum_two@v1|code_test_report/1"), result.evaluatorRef)
        assertTrue(result.misconceptionHypotheses.isEmpty())
    }

    @Test
    fun `a test speaks only for its own Objective, and a failing test names its declared misconception`() {
        val verdict = CodeEvaluation.evaluate(item(), suite, report("wrong"))
        assertEquals(mapOf(builds to OutcomeSignal.MET, reads to OutcomeSignal.MET, adds to OutcomeSignal.NOT_MET), signals(verdict))
        assertEquals(listOf(MisconceptionHypothesis(adds, "misconception.py.arith.sign_flip")), verified(verdict).misconceptionHypotheses)
    }

    @Test
    fun `a failed build counts only against the build's own Objective, and tests that did not run measured nothing`() {
        val verdict = CodeEvaluation.evaluate(item(), suite, report("broken"))
        assertEquals(mapOf(builds to OutcomeSignal.NOT_MET, reads to OutcomeSignal.NOT_RELIABLY_MEASURED, adds to OutcomeSignal.NOT_RELIABLY_MEASURED), signals(verdict))
        assertTrue(verified(verdict).misconceptionHypotheses.isEmpty(), "a test that did not run points at nothing")
        val noBuildObjective = suite.copy(buildObjective = null)
        assertEquals(mapOf(reads to OutcomeSignal.NOT_RELIABLY_MEASURED, adds to OutcomeSignal.NOT_RELIABLY_MEASURED),
            signals(CodeEvaluation.evaluate(item(), noBuildObjective, report("broken"))), "without a build Objective a failed build blames nothing")
    }

    @Test
    fun `a program that never finishes fails its tests, because the time limit is part of the test`() {
        assertEquals(mapOf(builds to OutcomeSignal.MET, reads to OutcomeSignal.NOT_MET, adds to OutcomeSignal.NOT_MET),
            signals(CodeEvaluation.evaluate(item(), suite, report("loop"))))
    }

    @Test
    fun `a runner that could not run says nothing about the code, and nothing is measured`() {
        val verdict = assertIs<CodeVerdict.NotMeasured>(CodeEvaluation.evaluate(item(), suite, report("environment_error")))
        assertEquals(CodeNotMeasured.ENVIRONMENT_ERROR, verdict.reason)
    }

    @Test
    fun `passes are never claimed as met while another of that Objective's tests did not run`() {
        assertEquals(OutcomeSignal.MET, CodeEvaluation.signalOf(listOf(CodeTestStatus.PASSED, CodeTestStatus.PASSED)))
        assertEquals(OutcomeSignal.NOT_RELIABLY_MEASURED, CodeEvaluation.signalOf(listOf(CodeTestStatus.PASSED, CodeTestStatus.ERROR)))
        assertEquals(OutcomeSignal.NOT_RELIABLY_MEASURED, CodeEvaluation.signalOf(listOf(CodeTestStatus.NOT_RUN)))
        assertEquals(OutcomeSignal.PARTIALLY_MET, CodeEvaluation.signalOf(listOf(CodeTestStatus.PASSED, CodeTestStatus.FAILED)))
        assertEquals(OutcomeSignal.NOT_MET, CodeEvaluation.signalOf(listOf(CodeTestStatus.FAILED, CodeTestStatus.TIMED_OUT)))
        assertEquals(OutcomeSignal.NOT_MET, CodeEvaluation.signalOf(listOf(CodeTestStatus.FAILED, CodeTestStatus.ERROR)), "what ran and failed still counts")
    }

    @Test
    fun `a report for another item, another suite version or other tests proves nothing`() {
        val good = report("correct")
        fun reason(r: CodeTestReport) = assertIs<CodeVerdict.NotMeasured>(CodeEvaluation.evaluate(item(), suite, r)).reason
        assertEquals(CodeNotMeasured.REPORT_FOR_ANOTHER_ITEM, reason(good.copy(item = VersionedRef("item.py.sum_two", 2))))
        assertEquals(CodeNotMeasured.REPORT_FOR_ANOTHER_SUITE, reason(good.copy(suite = VersionedRef("codetest.py.sum_two", 2))))
        assertEquals(CodeNotMeasured.REPORT_DOES_NOT_MATCH_SUITE, reason(good.copy(results = good.results - "adds_small")))
        assertEquals(CodeNotMeasured.REPORT_DOES_NOT_MATCH_SUITE, reason(good.copy(build = CodeBuildStatus.NOT_REQUIRED)))
        assertIs<CodeVerdict.Measured>(CodeEvaluation.evaluate(item(), suite.copy(buildObjective = null), good.copy(build = CodeBuildStatus.NOT_REQUIRED)))
    }

    @Test
    fun `a report is read completely or not at all`() {
        val text = requireNotNull(javaClass.getResource("/code_test_14d/report_correct.txt")).readText()
        assertIs<CodeTestReports.Decoded.Ok>(CodeTestReports.decode("\n\n" + text.replace("\n", "\r\n") + "\n"))
        for (broken in listOf(
            text.replace("code_test_report/1", "code_test_report/2"),
            text.replace("\nend", ""),
            text.replace("passed", "ok"),
            // One unknown status among known ones is not skipped: the report is unreadable, not shorter.
            text.replace("test: adds_small passed", "test: adds_small skipped"),
            text.replace("item.py.sum_two@v1", "item.py.sum_two@v0"),
            text.replace("build: ok", "build: fine"),
            text.replace("item.py.sum_two@v1", "item.py.sum_two"),
            text.replace("test: adds_small passed", "test: reads_two_ints passed"),
            text.replace("test: adds_small passed", "note: looks fine"),
            text.replace("test: reads_two_ints passed\n", "test: reads_two_ints passed\nhello\n"),
        )) assertIs<CodeTestReports.Decoded.Malformed>(CodeTestReports.decode(broken), broken)
    }

    @Test
    fun `the tests decide wherever they exist, and a task that needs a verified result is never put to an AI`() {
        assertEquals(CodeEvaluationRoute.TESTS, CodeEvaluation.route(item(EvaluatorStatusRequirement.PROVISIONAL_ALLOWED), suite))
        assertEquals(CodeEvaluationRoute.TESTS_REQUIRED, CodeEvaluation.route(item(EvaluatorStatusRequirement.VERIFIED), null))
        assertEquals(CodeEvaluationRoute.TESTS_REQUIRED, CodeEvaluation.route(item(EvaluatorStatusRequirement.PROVISIONAL_ALLOWED, deterministicRequired = true), null))
        assertEquals(CodeEvaluationRoute.TESTS_REQUIRED, CodeEvaluation.route(item(EvaluatorStatusRequirement.PROVISIONAL_ALLOWED, deterministic = true), null))
        assertEquals(CodeEvaluationRoute.AI_PROVISIONAL, CodeEvaluation.route(item(EvaluatorStatusRequirement.PROVISIONAL_ALLOWED), null))
    }

    @Test
    fun `an AI's judgement of code is at most provisional, and only about what the task targets`() {
        val ref = EvaluatorRef("provider", "model", "code_evaluation/1")
        val target = item(EvaluatorStatusRequirement.PROVISIONAL_ALLOWED)
        val ok = EvaluationResult.Provisional(listOf(ComponentResult(adds, OutcomeSignal.NOT_MET)), ref)
        assertEquals(ok, CodeEvaluation.acceptAi(target, ok))
        val invalid = EvaluationResult.EvaluationPending(PendingReason.INVALID_RESPONSE)
        assertEquals(invalid, CodeEvaluation.acceptAi(target, EvaluationResult.Verified(listOf(ComponentResult(adds, OutcomeSignal.MET)), ref)), "an evaluator port never verifies")
        assertEquals(invalid, CodeEvaluation.acceptAi(target, EvaluationResult.Provisional(listOf(ComponentResult(VersionedRef("objective.other", 1), OutcomeSignal.MET)), ref)))
        assertEquals(invalid, CodeEvaluation.acceptAi(target, EvaluationResult.Provisional(listOf(ComponentResult(adds, OutcomeSignal.MET), ComponentResult(adds, OutcomeSignal.NOT_MET)), ref)))
        assertEquals(invalid, CodeEvaluation.acceptAi(target, EvaluationResult.Provisional(emptyList(), ref)))
        val refused = EvaluationResult.EvaluationPending(PendingReason.REFUSED)
        assertEquals(refused, CodeEvaluation.acceptAi(target, refused), "a refusal stays a refusal — never a wrong answer")
    }

    @Test
    fun `a suite is pinned, named, non-empty, and keeps the build and the tests apart`() {
        assertFailsWith<IllegalArgumentException> { suite.copy(ref = VersionedRef("tests.py.sum_two", 1)).let { CodeTestSuite(it.ref, it.item, it.tests, it.buildObjective) } }
        assertFailsWith<IllegalArgumentException> { CodeTestSuite(suiteRef, itemRef, emptyList()) }
        assertFailsWith<IllegalArgumentException> { CodeTestSuite(suiteRef, itemRef, listOf(CodeTest("a", adds), CodeTest("a", reads))) }
        assertFailsWith<IllegalArgumentException> { CodeTestSuite(suiteRef, itemRef, listOf(CodeTest("a", builds)), buildObjective = builds) }
        assertFailsWith<IllegalArgumentException> { CodeTest("Adds Small", adds) }
        assertFailsWith<IllegalArgumentException> { CodeEvaluation.evaluate(item(targets = listOf(reads, builds)), suite, report("correct")) }
        assertFailsWith<IllegalArgumentException> { CodeEvaluation.evaluate(item(), suite.copy(item = VersionedRef("item.py.sum_two", 2)), report("correct")) }
    }
}
