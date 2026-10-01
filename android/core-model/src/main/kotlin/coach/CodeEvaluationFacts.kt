package coach.model

/**
 * `CDEX-v0 / D-108` — code evaluation (14D).
 *
 * **Code is judged by running it, or it is only an opinion.** A code task is verified only by the course's own
 * tests — run on the learner's computer by the course's test runner, its report brought back and read strictly
 * (user decision, 2026-10-01) — and a test speaks only for the Objective it was written for. A test that did not run
 * measured nothing, and nothing is held against the learner for it. Without tests, an AI may judge the code only
 * where the task allows a provisional result (user decision), and never as more than provisional; a task that needs
 * a verified result is never put to an AI. Passing tests show that the code does what was asked — not that the
 * learner can explain why (`MSS-v0` §5.4, `2D` "Test runner").
 */

/** One authored test of a code task, and the one Objective its result speaks for. */
data class CodeTest(
    val id: String,
    val objective: VersionedRef,
    /** A catalog misconception (`WAAX-v0`) this test's failure points at, if its author declared one. */
    val misconceptionOnFailure: String? = null,
) {
    init {
        require(ID.matches(id)) { "a test id is lower_snake_case: $id" }
    }

    companion object {
        val ID = Regex("^[a-z0-9]+(_[a-z0-9]+)*$")
    }
}

/**
 * The course's tests for one item version: `codetest.<namespace>.<slug>` (`GNS-v0`), pinned to the item it tests.
 * [buildObjective] is the Objective "the code builds" speaks for, if the author declared one — only then can a failed
 * build count against anything.
 */
data class CodeTestSuite(
    val ref: VersionedRef,
    val item: VersionedRef,
    val tests: List<CodeTest>,
    val buildObjective: VersionedRef? = null,
) {
    init {
        require(ID.matches(ref.logicalId)) { "a suite id is codetest.<namespace>.<slug> (GNS-v0): ${ref.logicalId}" }
        require(tests.isNotEmpty()) { "a suite with no test measures nothing" }
        require(tests.map { it.id }.toSet().size == tests.size) { "test ids are unique within a suite" }
        require(buildObjective == null || tests.none { it.objective == buildObjective }) {
            "the build speaks for its own Objective; a test speaks for another"
        }
    }

    companion object {
        val ID = Regex("^codetest(\\.[a-z0-9]+(_[a-z0-9]+)*){2,}$")
    }
}

/** What the test runner reports about building the learner's code. */
enum class CodeBuildStatus(val id: String) {
    OK("ok"),
    /** The learner's code did not build. */
    FAILED("failed"),
    /** The task has no build step (an interpreted language). */
    NOT_REQUIRED("not_required"),
    /** The runner could not build at all — a missing compiler, a broken setup. Says nothing about the code. */
    ENVIRONMENT_ERROR("environment_error"),
}

/** What the test runner reports about one test. */
enum class CodeTestStatus(val id: String, val ran: Boolean) {
    PASSED("passed", ran = true),
    FAILED("failed", ran = true),
    /** The program did not finish within the suite's authored limit: the test's own specification, so it ran and failed. */
    TIMED_OUT("timed_out", ran = true),
    NOT_RUN("not_run", ran = false),
    /** The runner could not run the test — says nothing about the code. */
    ERROR("error", ran = false),
}

data class CodeTestReport(
    val item: VersionedRef,
    val suite: VersionedRef,
    val build: CodeBuildStatus,
    val results: Map<String, CodeTestStatus>,
)

/**
 * `code_test_report/1`, the runner's output, as the learner pastes it back. It is read completely or not at all:
 *
 * ```text
 * code_test_report/1
 * item: <logical_id>@v<N>
 * suite: <logical_id>@v<N>
 * build: ok | failed | not_required | environment_error
 * test: <id> passed | failed | timed_out | not_run | error
 * end
 * ```
 *
 * Blank lines around the report are ignored; anything else out of place makes it malformed, and a malformed report
 * is not evidence of anything.
 */
object CodeTestReports {
    const val FORMAT = "code_test_report/1"

    sealed interface Decoded {
        data class Ok(val report: CodeTestReport) : Decoded
        data class Malformed(val reasons: List<String>) : Decoded
    }

    fun decode(text: String): Decoded {
        val lines = text.replace("\r\n", "\n").split("\n").map { it.trimEnd() }.dropWhile { it.isBlank() }.dropLastWhile { it.isBlank() }
        val reasons = mutableListOf<String>()
        if (lines.size < 6) return Decoded.Malformed(listOf("too short to be a $FORMAT report"))
        if (lines.first() != FORMAT) reasons += "the first line is not $FORMAT"
        if (lines.last() != "end") reasons += "the last line is not end"
        val item = ref(lines[1], "item", reasons)
        val suite = ref(lines[2], "suite", reasons)
        val build = field(lines[3], "build", reasons)?.let { raw ->
            CodeBuildStatus.entries.firstOrNull { it.id == raw } ?: null.also { reasons += "unknown build status '$raw'" }
        }
        val results = linkedMapOf<String, CodeTestStatus>()
        for (line in lines.subList(4, lines.size - 1)) {
            val parts = field(line, "test", reasons)?.split(" ")
            if (parts == null) continue
            if (parts.size != 2) { reasons += "'$line' is not 'test: <id> <status>'"; continue }
            val status = CodeTestStatus.entries.firstOrNull { it.id == parts[1] }
            if (status == null) { reasons += "unknown test status '${parts[1]}'"; continue }
            if (parts[0] in results) { reasons += "test '${parts[0]}' is reported twice"; continue }
            results[parts[0]] = status
        }
        if (results.isEmpty() && reasons.isEmpty()) reasons += "no test is reported"
        if (reasons.isNotEmpty() || item == null || suite == null || build == null) return Decoded.Malformed(reasons.ifEmpty { listOf("incomplete") })
        return Decoded.Ok(CodeTestReport(item, suite, build, results))
    }

    private fun field(line: String, name: String, reasons: MutableList<String>): String? {
        if (!line.startsWith("$name: ")) { reasons += "'$line' is not a '$name:' line"; return null }
        return line.removePrefix("$name: ").takeIf { it.isNotEmpty() } ?: null.also { reasons += "'$name:' is empty" }
    }

    private fun ref(line: String, name: String, reasons: MutableList<String>): VersionedRef? {
        val value = field(line, name, reasons) ?: return null
        val parts = value.split("@v")
        val version = parts.getOrNull(1)?.toIntOrNull()
        if (parts.size != 2 || parts[0].isEmpty() || version == null || version < 1) {
            reasons += "'$value' is not a pinned reference (id@vN)"
            return null
        }
        return VersionedRef(parts[0], version)
    }
}

/** Why a code submission produced no evidence. None of these is a wrong answer. */
enum class CodeNotMeasured(val id: String) {
    REPORT_MISSING("report_missing"),
    REPORT_MALFORMED("report_malformed"),
    REPORT_FOR_ANOTHER_ITEM("report_for_another_item"),
    REPORT_FOR_ANOTHER_SUITE("report_for_another_suite"),
    REPORT_DOES_NOT_MATCH_SUITE("report_does_not_match_suite"),
    ENVIRONMENT_ERROR("environment_error"),
    /** A report for a task the course has no tests for: it cannot be checked, so it is not used. */
    NO_SUITE_FOR_REPORT("no_suite_for_report"),
    /** The task needs a verified result and the course has written no tests for it yet: no AI is asked. */
    TESTS_REQUIRED("tests_required"),
    NOTHING_SUBMITTED("nothing_submitted"),
    TASK_TEXT_MISSING("task_text_missing"),
    /** The evaluator refused, timed out, failed or answered outside its contract (`AIAX-v0` §6). */
    EVALUATOR_DID_NOT_ANSWER("evaluator_did_not_answer"),
}

sealed interface CodeVerdict {
    /** A result the evidence pipeline records as it records any other (`RecordEvidence`, 12A). */
    data class Measured(val result: EvaluationResult) : CodeVerdict {
        init {
            require(result !is EvaluationResult.EvaluationPending) { "a pending evaluation measured nothing" }
        }
    }

    /** Nothing is written. [details] say what was wrong with a report; [pending] why an evaluator gave no answer. */
    data class NotMeasured(
        val reason: CodeNotMeasured,
        val details: List<String> = emptyList(),
        val pending: PendingReason? = null,
    ) : CodeVerdict
}

/** Who may judge a code task (user decisions, 2026-10-01). */
enum class CodeEvaluationRoute {
    /** The course's tests decide; no AI is asked for evidence. */
    TESTS,
    /** No tests, and the task allows a provisional result: an AI may judge, provisionally. */
    AI_PROVISIONAL,
    /** No tests, and the task needs a verified result: nothing may judge it yet. */
    TESTS_REQUIRED,
}

object CodeEvaluation {

    const val EVALUATOR_PROVIDER = "deterministic"
    const val EVALUATOR_MODEL = "code_tests"

    fun route(item: AssessmentItem, suite: CodeTestSuite?): CodeEvaluationRoute = when {
        suite != null -> CodeEvaluationRoute.TESTS
        item.evaluatorRequirement.requiredStatus == EvaluatorStatusRequirement.VERIFIED ||
            item.evaluatorRequirement.deterministicRequired || item.deterministicVerification -> CodeEvaluationRoute.TESTS_REQUIRED
        else -> CodeEvaluationRoute.AI_PROVISIONAL
    }

    /**
     * What a test report proves. Each test speaks for its own Objective only; a failed build counts only against the
     * build's own Objective, and every test that did not run leaves its Objective unmeasured rather than failed.
     */
    fun evaluate(item: AssessmentItem, suite: CodeTestSuite, report: CodeTestReport): CodeVerdict {
        require(suite.item == item.ref) { "the suite tests another item" }
        require(suite.tests.all { it.objective in item.targetObjectives }) { "a test speaks only for an Objective the item targets" }
        require(suite.buildObjective == null || suite.buildObjective in item.targetObjectives) { "the build speaks only for a targeted Objective" }

        if (report.item != item.ref) return CodeVerdict.NotMeasured(CodeNotMeasured.REPORT_FOR_ANOTHER_ITEM)
        if (report.suite != suite.ref) return CodeVerdict.NotMeasured(CodeNotMeasured.REPORT_FOR_ANOTHER_SUITE)
        if (report.results.keys != suite.tests.map { it.id }.toSet()) return CodeVerdict.NotMeasured(CodeNotMeasured.REPORT_DOES_NOT_MATCH_SUITE)
        if (report.build == CodeBuildStatus.NOT_REQUIRED && suite.buildObjective != null) {
            return CodeVerdict.NotMeasured(CodeNotMeasured.REPORT_DOES_NOT_MATCH_SUITE)
        }
        if (report.build == CodeBuildStatus.ENVIRONMENT_ERROR) return CodeVerdict.NotMeasured(CodeNotMeasured.ENVIRONMENT_ERROR)

        val built = report.build != CodeBuildStatus.FAILED
        val components = buildList {
            suite.buildObjective?.let { add(ComponentResult(it, if (built) OutcomeSignal.MET else OutcomeSignal.NOT_MET)) }
            for (objective in suite.tests.map { it.objective }.distinct()) {
                val statuses = suite.tests.filter { it.objective == objective }.map { report.results.getValue(it.id) }
                add(ComponentResult(objective, if (built) signalOf(statuses) else OutcomeSignal.NOT_RELIABLY_MEASURED))
            }
        }
        val hypotheses = suite.tests
            .filter { built && it.misconceptionOnFailure != null && report.results.getValue(it.id).let { s -> s.ran && s != CodeTestStatus.PASSED } }
            .map { MisconceptionHypothesis(it.objective, it.misconceptionOnFailure!!) }
            .distinct()
        return CodeVerdict.Measured(
            EvaluationResult.Verified(
                componentResults = components,
                evaluatorRef = EvaluatorRef(EVALUATOR_PROVIDER, EVALUATOR_MODEL, "${suite.ref.logicalId}@v${suite.ref.version}|${CodeTestReports.FORMAT}"),
                misconceptionHypotheses = hypotheses,
            )
        )
    }

    /**
     * One Objective's tests: met only when every one ran and passed; any that ran and failed count against it; tests
     * that passed while others did not run are not claimed as met — what did not run measured nothing.
     */
    fun signalOf(statuses: List<CodeTestStatus>): OutcomeSignal {
        val ran = statuses.filter { it.ran }
        val failed = ran.count { it != CodeTestStatus.PASSED }
        return when {
            failed == 0 && ran.size == statuses.size -> OutcomeSignal.MET
            failed == 0 -> OutcomeSignal.NOT_RELIABLY_MEASURED
            failed == ran.size -> OutcomeSignal.NOT_MET
            else -> OutcomeSignal.PARTIALLY_MET
        }
    }

    /**
     * An evaluator's answer about code, as core accepts it (`AIAX-v0` §5). It is at most provisional: an evaluator
     * port that claims a verified result has answered outside its contract, as has one that judges an Objective the
     * task does not target or judges one twice. Either is no answer — never a wrong answer.
     */
    fun acceptAi(item: AssessmentItem, result: EvaluationResult): EvaluationResult = when (result) {
        is EvaluationResult.EvaluationPending -> result
        is EvaluationResult.Verified -> EvaluationResult.EvaluationPending(PendingReason.INVALID_RESPONSE)
        is EvaluationResult.Provisional -> {
            val objectives = result.componentResults.map { it.objectiveRef }
            if (objectives.isEmpty() || objectives.toSet().size != objectives.size || objectives.any { it !in item.targetObjectives }) {
                EvaluationResult.EvaluationPending(PendingReason.INVALID_RESPONSE)
            } else {
                result
            }
        }
    }
}
