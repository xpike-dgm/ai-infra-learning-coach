"""Independent 14D QA — CDEX-v0 Code Evaluation.

The evidence rules are read from `AIAX-v0` §5–§6, `MSS-v0` §5.4, `2D` "Test runner", `QAB-v0` §21 and
`LEARNING_BEHAVIOR_RULES` §1, and compared with the real Kotlin and the real runner — never with 14D's own contract
alone. The runner is executed on its fixtures and its output must equal, byte for byte, the reports the Kotlin tests
read. The rules the step exists for must be **unrepresentable** as a PASS here: an AI asked about a tested task, an
AI's judgement recorded as verified, a verified-only task put to an AI, a broken report replaced by an AI, one failing
test counted against every Objective, a failed build blamed on Objectives it does not speak for, a test that did not
run counted as a failure, an environment error treated as a wrong answer, and passing tests presented as understanding.
"""

from __future__ import annotations

from pathlib import Path
import re
import subprocess
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/14d_code_evaluation/code_evaluation.yaml"
SPEC = ROOT / "docs/CODE_EVALUATION_IMPL_SPEC.md"
RESEARCH = ROOT / "research/14d_code_evaluation_research.md"
QA_OUT = ROOT / "arch/14d_code_evaluation/qa_report.yaml"
AIAX = ROOT / "docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md"
MSS = ROOT / "docs/MASTERY_SIGNALS_SPEC.md"
AAE = ROOT / "docs/AI_ASSISTANCE_EVIDENCE_SPEC.md"
QAB = ROOT / "docs/QUESTION_BANK_SPEC.md"
LBR = ROOT / "docs/LEARNING_BEHAVIOR_RULES.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
RUNNER = ROOT / "tools/code_test_runner.py"
FIXTURES = ROOT / "tools/fixtures/code_test_14d"
RESOURCES = ANDROID / "core-model/src/test/resources/code_test_14d"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/CodeEvaluationFacts.kt"
EVAL_KT = ANDROID / "core-application/src/main/kotlin/coach/application/EvaluateCode.kt"
PRES_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/CodeEvaluationPresentation.kt"
PIPE_KT = ANDROID / "core-application/src/main/kotlin/coach/application/EvidencePipeline.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
FORMAT_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"
SOURCE_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
PKG_KT = ANDROID / "core-model/src/main/kotlin/coach/CurriculumPackage.kt"

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"(?<![:\"])//[^\n]*", "", source)


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


def body(source: str, signature: str) -> str:
    at = source.find(signature)
    if at < 0:
        return ""
    open_at = source.find("{", at)
    if open_at < 0:
        return ""
    depth = 0
    for index in range(open_at, len(source)):
        if source[index] == "{":
            depth += 1
        elif source[index] == "}":
            depth -= 1
            if depth == 0:
                return source[at:index + 1]
    return ""


def expression(source: str, signature: str) -> str:
    """A member up to the next declaration at its own indentation (expression-bodied functions included)."""
    at = source.find(signature)
    if at < 0:
        return ""
    line_start = source.rfind("\n", 0, at) + 1
    indent = at - line_start
    end = re.search(r"\n {0,%d}(?=[^\s)])" % indent, source[at:])
    return source[at:at + (end.start() if end else len(source) - at)]


def load(path: Path):
    try:
        return yaml.safe_load(read(path)) or {}
    except yaml.YAMLError:
        return {}


for path in (CONTRACT, SPEC, RESEARCH, FACTS_KT, EVAL_KT, PRES_KT, RUNNER):
    check(f"E14D-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

contract = load(CONTRACT)
facts, evaluate, pres = strip_comments(read(FACTS_KT)), strip_comments(read(EVAL_KT)), strip_comments(read(PRES_KT))
pipe, ports, schema, fmt, source, pkg = (strip_comments(read(PIPE_KT)), strip_comments(read(PORTS_KT)), strip_comments(read(SCHEMA_KT)),
                                         strip_comments(read(FORMAT_KT)), strip_comments(read(SOURCE_KT)), strip_comments(read(PKG_KT)))
runner = read(RUNNER)
aiax, mss, aae, qab, lbr, spec_text, research_text = read(AIAX), read(MSS), read(AAE), read(QAB), read(LBR), read(SPEC), read(RESEARCH)

# ---------------------------------------------------------------- contract head and the accepted texts it rests on
check("E14D-01_model", contract.get("model") == "CDEX-v0", str(contract.get("model")))
check("E14D-01_status", contract.get("status") == "accepted_14d", str(contract.get("status")))
check("E14D-01_decision", contract.get("decision") == "D-108", str(contract.get("decision")))
scope = contract.get("scope", {})
for key, expected in {"tests_as_content": True, "report_format": True, "reference_runner": True, "per_objective_attribution": True,
                      "provisional_ai_route": True, "schema_changed": False, "state_family_added": False, "mastery_rule_changed": False,
                      "weakness_rule_changed": False, "planner_rule_changed": False, "code_runs_on_device": False, "tests_authored": False,
                      "adapter_prompt_written": False, "app_calls_evaluation": False}.items():
    check(f"E14D-01_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")
check("E14D-01_user_decisions", contract.get("user_decisions") == {"date": "2026-10-01", "tests_run": "on_learners_computer_report_imported",
      "ai_without_tests": "provisional_only_where_task_allows"}, str(contract.get("user_decisions")))
check("E14D-01_aiax_verified_path", "`verified` requires a deterministic path — an answer key, objective-specific tests, a compiler or runtime verifier" in aiax, "AIAX-v0 §5.2 moved")
check("E14D-01_aiax_uncalibrated", "uncalibrated single-LLM evaluation -> evaluator_status = provisional" in aiax, "AIAX-v0 §5.2 moved")
check("E14D-01_aiax_refusal", "**A refusal is not a wrong answer.**" in aiax, "AIAX-v0 §6 moved")
check("E14D-01_aiax_minimum", "only the **minimum content needed to evaluate the current attempt** may be sent" in aiax, "AIAX-v0 §11 moved")
check("E14D-01_mss_tests_not_understanding", "testlerin geçmesi tek başına kullanıcı anlayışının tüm boyutlarını kanıtlamaz" in mss, "MSS-v0 §5.4 moved")
check("E14D-01_2d_test_runner", "test sonucu tek başına understanding kanıtı değildir" in aae, "2D Test runner moved")
check("E14D-01_qab_test_output", "test_output_required" in qab and "coding_task" in qab, "QAB-v0 §2/§21 moved")
check("E14D-01_lbr_not_ide", "telefon tam IDE olmak zorunda değildir" in lbr, "LEARNING_BEHAVIOR_RULES §1 moved")

# ---------------------------------------------------------------- statuses and the report format
build_enum = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\)', body(facts, "enum class CodeBuildStatus"), re.M)
test_enum = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)", ran = (true|false)\)', body(facts, "enum class CodeTestStatus"), re.M)
check("E14D-02_build_statuses", build_enum == contract.get("report", {}).get("build_statuses") == ["ok", "failed", "not_required", "environment_error"], str(build_enum))
check("E14D-02_test_statuses", [t[0] for t in test_enum] == contract.get("report", {}).get("test_statuses") == ["passed", "failed", "timed_out", "not_run", "error"], str(test_enum))
check("E14D-02_ran", dict(test_enum) == {"passed": "true", "failed": "true", "timed_out": "true", "not_run": "false", "error": "false"}, str(test_enum))
check("E14D-02_format", 'const val FORMAT = "code_test_report/1"' in facts and contract.get("report", {}).get("format") == "code_test_report/1", "")
decode = body(facts, "fun decode(")
for fragment, cid in (('if (lines.first() != FORMAT) reasons +=', "first_line"), ('if (lines.last() != "end") reasons +=', "end"),
                      ("is reported twice", "duplicate"), ("unknown test status", "unknown_status"), ("unknown build status", "unknown_build"),
                      ("no test is reported", "empty"), ('replace("\\r\\n", "\\n")', "crlf")):
    check(f"E14D-02_strict_{cid}", fragment in decode or fragment in facts, fragment)
check("E14D-02_version_positive", "version == null || version < 1" in facts, "")

# ---------------------------------------------------------------- suites
suite_cls = body(facts, "data class CodeTestSuite")
check("E14D-03_suite_id", 'Regex("^codetest(\\\\.[a-z0-9]+(_[a-z0-9]+)*){2,}$")' in suite_cls, "")
check("E14D-03_suite_nonempty", "require(tests.isNotEmpty())" in suite_cls, "")
check("E14D-03_suite_unique", "require(tests.map { it.id }.toSet().size == tests.size)" in suite_cls, "")
check("E14D-03_build_apart", "require(buildObjective == null || tests.none { it.objective == buildObjective })" in suite_cls, "")
check("E14D-03_test_id", 'Regex("^[a-z0-9]+(_[a-z0-9]+)*$")' in body(facts, "data class CodeTest("), "")

# ---------------------------------------------------------------- what a report proves
ev = body(facts, "fun evaluate(item: AssessmentItem, suite: CodeTestSuite, report: CodeTestReport)")
check("E14D-04_targets_only", "require(suite.tests.all { it.objective in item.targetObjectives })" in ev, "")
order = [ev.find(s) for s in ("REPORT_FOR_ANOTHER_ITEM", "REPORT_FOR_ANOTHER_SUITE", "REPORT_DOES_NOT_MATCH_SUITE", "CodeNotMeasured.ENVIRONMENT_ERROR", "EvaluationResult.Verified(")]
check("E14D-04_refusals_before_result", all(i >= 0 for i in order) and order == sorted(order), str(order))
check("E14D-04_exact_suite_version", "if (report.suite != suite.ref)" in ev and "if (report.item != item.ref)" in ev, "")
check("E14D-04_test_set", "report.results.keys != suite.tests.map { it.id }.toSet()" in ev, "")
check("E14D-04_build_only_build_objective", "add(ComponentResult(it, if (built) OutcomeSignal.MET else OutcomeSignal.NOT_MET))" in ev, "")
check("E14D-04_failed_build_unmeasured", "if (built) signalOf(statuses) else OutcomeSignal.NOT_RELIABLY_MEASURED" in ev, "")
check("E14D-04_per_objective", "suite.tests.filter { it.objective == objective }" in ev, "")
sig = body(facts, "fun signalOf(")
check("E14D-04_signal_rules", all(f in sig for f in ("failed == 0 && ran.size == statuses.size -> OutcomeSignal.MET", "failed == 0 -> OutcomeSignal.NOT_RELIABLY_MEASURED",
      "failed == ran.size -> OutcomeSignal.NOT_MET", "else -> OutcomeSignal.PARTIALLY_MET")), sig[:300])
check("E14D-04_misconception_only_on_failure", "s.ran && s != CodeTestStatus.PASSED" in ev and "MisconceptionHypothesis(it.objective, it.misconceptionOnFailure!!)" in ev, "")
check("E14D-04_evaluator_named", 'EvaluatorRef(EVALUATOR_PROVIDER, EVALUATOR_MODEL, "${suite.ref.logicalId}@v${suite.ref.version}|${CodeTestReports.FORMAT}")' in ev
      and 'const val EVALUATOR_PROVIDER = "deterministic"' in facts, "")
check("E14D-04_no_threshold", not re.search(r"\d+\.\d+|percent|ratio|threshold", ev + sig, re.I), "no invented numbers")

# ---------------------------------------------------------------- who may judge
route = expression(facts, "fun route(")
check("E14D-05_tests_first", route.find("suite != null -> CodeEvaluationRoute.TESTS") >= 0
      and route.find("suite != null -> CodeEvaluationRoute.TESTS") < route.find("CodeEvaluationRoute.TESTS_REQUIRED"), route[:200])
check("E14D-05_verified_never_ai", all(f in route for f in ("requiredStatus == EvaluatorStatusRequirement.VERIFIED", "deterministicRequired", "item.deterministicVerification")), "")
accept = expression(facts, "fun acceptAi(")
check("E14D-05_ai_never_verifies", "is EvaluationResult.Verified -> EvaluationResult.EvaluationPending(PendingReason.INVALID_RESPONSE)" in accept, "")
check("E14D-05_ai_contract", all(f in accept for f in ("objectives.isEmpty()", "objectives.toSet().size != objectives.size", "objectives.any { it !in item.targetObjectives }")), "")
check("E14D-05_pending_passes_through", "is EvaluationResult.EvaluationPending -> result" in accept, "")
use = body(evaluate, "fun evaluate(")
prov = body(evaluate, "private fun provisional(")
check("E14D-05_no_ai_fallback", "report.isNullOrBlank() -> CodeVerdict.NotMeasured(CodeNotMeasured.REPORT_MISSING)" in use
      and "is CodeTestReports.Decoded.Malformed -> CodeVerdict.NotMeasured(CodeNotMeasured.REPORT_MALFORMED" in use, "")
check("E14D-05_ai_only_on_ai_route", evaluate.count("evaluator.evaluate(") == 1 and "evaluator.evaluate(" in prov
      and "CodeEvaluationRoute.AI_PROVISIONAL -> provisional(" in use, "")
check("E14D-05_minimum_content", "EvaluationRequest(item.targetObjectives, task, code)" in prov, "only task text and code")
check("E14D-05_ai_checked", "CodeEvaluation.acceptAi(item, evaluator.evaluate(" in prov, "")
check("E14D-05_skip_sends_nothing", prov.find("code.isBlank()") >= 0 and prov.find("code.isBlank()") < prov.find("evaluator.evaluate("), "")
check("E14D-05_writes_nothing", not re.search(r"appendTruth|writeProjection|inTransaction|RecordEvidence", evaluate), "the use case decides, the caller records")
ereq = re.search(r"data class EvaluationRequest\(([^)]*)\)", ports)
check("E14D-05_request_unchanged", ereq is not None and re.findall(r"val (\w+):", ereq.group(1)) == ["objectiveRefs", "promptText", "learnerResponse"], ereq.group(1) if ereq else "")
measured = body(facts, "data class Measured")
check("E14D-05_measured_not_pending", "require(result !is EvaluationResult.EvaluationPending)" in measured, "")
check("E14D-05_pipeline_pending_writes_nothing", "is EvaluationResult.EvaluationPending -> return Recorded(emptyList())" in pipe, "12A")

# ---------------------------------------------------------------- the runner, executed
check("E14D-06_runner_no_shell", "shell=True" not in runner and "os.system" not in runner, "")
check("E14D-06_runner_stdlib", set(re.findall(r"^(?:from|import) (\w+)", runner, re.M)) <= {"__future__", "json", "subprocess", "sys", "pathlib"}, str(sorted(set(re.findall(r"^(?:from|import) (\w+)", runner, re.M)))))
check("E14D-06_runner_timeout_authored", "'timeout_seconds' must be authored and positive" in runner, "")
check("E14D-06_runner_bytes", "sys.stdout.buffer.write(" in runner, "")
check("E14D-06_runner_env_error", 'return "environment_error"' in runner and 'return "error"' in runner and 'return "timed_out"' in runner, "")
expected_reports = {"correct": "build: ok", "wrong": "test: adds_small failed", "broken": "build: failed", "loop": "timed_out", "environment_error": "build: environment_error"}
for name, marker in expected_reports.items():
    directory = FIXTURES / ("nowhere" if name == "environment_error" else name)
    try:
        out = subprocess.run([sys.executable, str(RUNNER), str(FIXTURES / "suite.json"), "--dir", str(directory)], capture_output=True, timeout=120).stdout
    except (OSError, subprocess.TimeoutExpired) as error:
        out = str(error).encode()
    resource = RESOURCES / f"report_{name}.txt"
    check(f"E14D-06_runner_{name}_matches_kotlin_resource", resource.is_file() and out == resource.read_bytes() and marker.encode() in out, out.decode(errors="replace")[:200])

# ---------------------------------------------------------------- content, ports, storage
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
check("E14D-07_port_count", len(interfaces) == contract.get("port_count") == 5, str(interfaces))
check("E14D-07_content_refinement", "fun codeTestsFor(item: VersionedRef): CodeTestSuite?" in body(ports, "interface ContentPort")
      and contract.get("port_refinements") == [{"port": "ContentPort", "method": "codeTestsFor"}], "")
check("E14D-07_source_pins_version", "singleOrNull { it.item == item }" in source, "")
check("E14D-07_sections", re.search(r'KNOWN_SECTIONS = setOf\([^)]*"code_test_suite", "code_test"', fmt, re.S) is not None, "")
check("E14D-07_suite_keys", '"code_test_suite" to setOf("logical_id", "version", "item", "build_objective")' in fmt, "")
check("E14D-07_test_keys", '"code_test" to setOf("suite", "id", "objective", "misconception")' in fmt, "")
assemble = body(fmt, "private fun codeTests(")
check("E14D-07_undeclared_suite_refused", "is not declared" in assemble and "has more than one suite" in assemble, "")
check("E14D-07_not_published", "codetest" not in pkg.lower() and "code_test" not in schema.lower(), "content, never published")
check("E14D-07_schema_unchanged", "const val VERSION = 8" in schema and contract.get("schema_version") == 8, "")

# ---------------------------------------------------------------- what is said
copy = body(pres, "object CodeEvaluationCopy")
notes = expression(pres, "fun notes(")
check("E14D-08_ai_label", contract.get("presentation", {}).get("ai_label", "?") in copy, "")
check("E14D-08_tests_not_understanding", contract.get("presentation", {}).get("tests_note", "?") in copy, "")
check("E14D-08_notes_by_source", "if (verdict.result is EvaluationResult.Provisional) listOf(CodeEvaluationCopy.AI_LABEL) else listOf(CodeEvaluationCopy.TESTS_ARE_NOT_UNDERSTANDING)" in notes, notes[:200])
check("E14D-08_ai_worded_as_view", all(f'const val AI_{k} = "AI\'a göre' in copy or (k == "NOT_MEASURED" and 'const val AI_NOT_MEASURED = "AI bu hedefi' in copy) for k in ("MET", "PARTIALLY_MET", "NOT_MET", "NOT_MEASURED")), "")
strings = " ".join(re.findall(r'"([^"]*)"', copy))
check("E14D-08_no_numbers", not re.search(r"\d|%", strings), strings[:200])
check("E14D-08_no_blame", not re.search(r"başarısız|hata yaptın|kopya|puan", strings, re.I), "")
not_learner = [m.group(1) for m in re.finditer(r"CodeNotMeasured\.([A-Z_]+) -> \"([^\"]*)\"", copy) if "yanlış sayılmadı" not in m.group(2)]
check("E14D-08_not_measured_not_wrong", set(not_learner) <= {"REPORT_MISSING", "NO_SUITE_FOR_REPORT", "NOTHING_SUBMITTED"} and "boş bırakmak yanlış sayılmaz" in copy, str(not_learner))
reasons = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\)', body(facts, "enum class CodeNotMeasured"), re.M)
check("E14D-08_every_reason_said", len(reasons) == len(re.findall(r"CodeNotMeasured\.[A-Z_]+ ->", copy)) == len(contract.get("not_measured_reasons", [])), str(reasons))
check("E14D-08_reasons_match_contract", reasons == contract.get("not_measured_reasons"), str(reasons))

# ---------------------------------------------------------------- verification claims
for name, suite in (contract.get("suites") or {}).items():
    check(f"E14D-09_suite_{name}", (ROOT / suite.get("file", "")).is_file(), suite.get("file", ""))
mr = contract.get("mutation_results", {})
ids = [m.get("id") for m in mr.get("mutants", [])]
check("E14D-09_mutation_all_detected", mr.get("total") == mr.get("detected") == len(ids) and len(set(ids)) == len(ids)
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("E14D-09_compile_failure_not_detection", mr.get("compile_failure_is_detection") is False and mr.get("final_run_is_a_single_clean_run") is True, "")
check("E14D-09_control", mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation", {})
check("E14D-09_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs", [])
check("E14D-09_runs_pass", len(runs) == 7 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification", {})
check("E14D-09_t6_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False, "")
check("E14D-09_c_build_not_claimed", contract.get("c_build_run") is False, "")
check("E14D-09_forbidden", len(contract.get("forbidden_patterns", [])) == 11, str(contract.get("forbidden_patterns")))
check("E14D-09_open_loops", set(contract.get("open_loops", {})) == {"suites_for_real_tasks", "report_artifact_and_app_call", "adapter_prompt_for_code",
      "ai_code_comprehension", "open_ended_evaluation", "ai_code_evaluator_calibration"}, str(contract.get("open_loops")))

# ---------------------------------------------------------------- spec, research, decision
check("E14D-10_spec_status", "**Status:** ACCEPTED — independent 14D QA PASS" in spec_text and "`D-108`" in spec_text, "")
check("E14D-10_spec_next", "**14E — AI-generated code comprehension check**" in spec_text, "")
check("E14D-10_spec_not_run", "**Not run: T6.**" in spec_text and "**Not run: a C build.**" in spec_text, "")
check("E14D-10_research", "No web research pass was needed" in research_text, "")
check("E14D-10_decision", re.search(r"^#+ .*D-108", read(DECISIONS), re.M) is not None, "D-108 heading in DECISIONS")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {"model": "CDEX-v0", "stage_step": "14D", "decision": "D-108", "result": "PASS" if not failures else "FAIL",
          "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
if "--no-write" not in sys.argv:
    QA_OUT.parent.mkdir(parents=True, exist_ok=True)
    QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"14D QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
for failure in failures:
    print("  FAIL", failure)
sys.exit(0 if not failures else 1)
