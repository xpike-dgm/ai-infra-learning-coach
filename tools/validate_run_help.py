"""Independent 16G QA — HLPX-v0, help and judged answers inside a planned task run.

The rules are read from the accepted contracts — `TRUX-v0` (§7 pauses, §8 assistance, §12.2 evaluation_pending), `TUTX-v0`
(14A), `WAAX-v0` (14B), `ALEX-v0` (14C), `CDEX-v0` (14D), `ACCX-v0` (14E), `OREX-v0` (14F), `SESX-v0` (11C) and 16F's
`moved_to_16g` — and compared with the real Kotlin, the shipped assets, the manifests, the contract, the tests, the spec,
the research and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a code item served without its own suite, or a suite
served for another version; a report that measured nothing written as an attempt; an AI asked for a task that needs a
verified result; an open answer judged before it froze, retried on its own, or called wrong when no evaluator answered;
help sent before the rules say the ask is ready, recorded when not shown, or recorded twice; a gloss guessed; a pause
saved mid-question or past a pending evaluation; a high-stakes pause continued as independent work; a network or file
permission added for copy-paste; and measurement from Today silently claimed instead of moved to 16H.
"""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/16g_run_help/run_help.yaml"
QA_OUT = ROOT / "arch/16g_run_help/qa_report.yaml"
SPEC = ROOT / "docs/RUN_HELP_SPEC.md"
RESEARCH = ROOT / "research/16g_run_help_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
FLOW_SPEC = ROOT / "docs/DAILY_WORKING_FLOW_SPEC.md"
EXECUTION_INDEX = ROOT / "docs/EXECUTION_INDEX.md"
MASTER_PLAN = ROOT / "docs/MASTER_PLAN.md"
F16_CONTRACT = ROOT / "arch/16f_end_to_end_loop/end_to_end_loop.yaml"
SHIP = ROOT / "tools/ship_code_suites.py"
RUNNER_SRC = ROOT / "tools/code_test_runner.py"
ASSETS = ANDROID / "app-wiring/src/main/assets"

FACTS = ANDROID / "core-model/src/main/kotlin/coach/TaskRunFacts.kt"
TUTOR_FACTS = ANDROID / "core-model/src/main/kotlin/coach/TutorFacts.kt"
PORTS = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
CONTENT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
RUN = ANDROID / "core-application/src/main/kotlin/coach/application/RunTask.kt"
HELP = ANDROID / "core-application/src/main/kotlin/coach/application/RunHelp.kt"
SUBMIT = ANDROID / "core-application/src/main/kotlin/coach/application/SubmitAttempt.kt"
PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TaskRunPresentation.kt"
HELP_PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/RunHelpPresentation.kt"
SESSION_STATE = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/SessionState.kt"
SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/TaskRunnerScreen.kt"
APPLICATION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
ACTIVITY = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
SESSION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/TaskRunSession.kt"
FLOW = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/TaskRunFlow.kt"
MANIFEST = ANDROID / "app-wiring/src/main/AndroidManifest.xml"
AI_MANIFEST = ANDROID / "app-wiring/src/withAi/AndroidManifest.xml"
MODEL_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/TaskRunFactsTest.kt"
PRESENTATION_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/RunHelpPresentationTest.kt"
APPLICATION_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/RunHelpTest.kt"
CURRICULUM_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/CodeSuiteTextTest.kt"
COURSE_TEST = ANDROID / "app-wiring/src/test/kotlin/coach/wiring/ShippedCourseTest.kt"

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def load(path: Path):
    return yaml.safe_load(read(path)) if path.is_file() else None


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"(?<![:\"])//[^\n]*", "", source)


def body(source: str, signature: str) -> str:
    start = source.find(signature)
    if start < 0:
        return ""
    depth, opened = 0, False
    for i in range(start, len(source)):
        if source[i] == "{":
            depth, opened = depth + 1, True
        elif source[i] == "}":
            depth -= 1
            if opened and depth == 0:
                return source[start:i + 1]
    return source[start:]


def strings(source: str) -> list[str]:
    return re.findall(r'"((?:[^"\\]|\\.)*)"', source)


def finish() -> None:
    passed = sum(1 for r in results if r["result"] == "PASS")
    out = {"model": "HLPX-v0", "stage_step": "16G", "decision": "D-130", "result": "PASS" if not failures else "FAIL",
           "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
    if "--no-write" not in sys.argv:
        QA_OUT.parent.mkdir(parents=True, exist_ok=True)
        QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
    print(f"16G QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(0 if not failures else 1)


for path in (CONTRACT, SPEC, RESEARCH, FLOW_SPEC, SHIP, RUNNER_SRC, FACTS, TUTOR_FACTS, PORTS, CONTENT, RUN, HELP, SUBMIT, PRESENTATION,
             HELP_PRESENTATION, SESSION_STATE, SCREEN, APPLICATION, ACTIVITY, SESSION, FLOW, MANIFEST, AI_MANIFEST, MODEL_TEST,
             PRESENTATION_TEST, APPLICATION_TEST, CURRICULUM_TEST, COURSE_TEST):
    check(f"A16G-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
run_src = strip_comments(read(RUN))
help_src = strip_comments(read(HELP))
facts = strip_comments(read(FACTS))
presentation = strip_comments(read(PRESENTATION))
help_presentation = strip_comments(read(HELP_PRESENTATION))
screen = strip_comments(read(SCREEN))
application = strip_comments(read(APPLICATION))
activity = read(ACTIVITY)
flow = strip_comments(read(FLOW))
session = strip_comments(read(SESSION))

# ---------------------------------------------------------------- contract and its source
check("A16G-01_model", contract.get("model") == "HLPX-v0" and contract.get("stage_step") == "16G" and contract.get("decision") == "D-130"
      and contract.get("new_step_decision") == "D-131", "")
ud = contract.get("user_decisions") or {}
check("A16G-01_user_decisions", ud.get("approval") == "explicit_user_approval_2026-10-26"
      and ud.get("scope", "").startswith("everything_inside_the_task_run") and ud.get("new_step", "").startswith("16H_added_like_d_099_no_renumbering")
      and ud.get("code_transport", "").startswith("copy_paste") and "no_network_or_file_permission" in ud.get("code_transport", ""), str(ud))
scope = contract.get("scope") or {}
check("A16G-01_scope", all(scope.get(k) is False for k in ("schema_changed", "boundaries_changed", "planner_rule_changed", "mastery_rule_changed",
      "evidence_rule_changed", "network_permission_added", "file_permission_added", "assessment_sessions_wired", "fast_path_entry",
      "dispositions_from_the_app"))
      and all(scope.get(k) is True for k in ("code_report_import", "open_response_ai", "tutor_panel", "alternative_explanation",
      "comprehension_check", "wrong_answer_analysis", "durable_pause", "screen_drawn"))
      and scope.get("schema_version") == 11 and scope.get("ports_changed") == "refinements_only" and scope.get("code_transport") == "copy_paste", "")
flow_spec = read(FLOW_SPEC)
check("A16G-01_trux_rules_are_the_source", all(x in flow_spec for x in ("post_submit_explanation_contaminates_prior_attempt = false",
      "Before granting H3 or H4", "evaluation_pending")), "")
items = contract.get("items") or {}
check("A16G-01_items_recorded", items.get("by_kind") == {"answer_key": 618, "code_tests": 480, "rubric": 14, "not_judged_here": 0}
      and items.get("shipped") == 1112 and items.get("suites_shipped") == 480
      and items.get("skills_with_a_runnable_task") == {"with_answer_keys_only": 36, "with_shipped_suites": 60, "total": 60}
      and items.get("written_comprehension_checks_shipped") == 0, str(items))
f16 = load(F16_CONTRACT) or {}
check("A16G-01_16f_list_items_are_strings", all(isinstance(x, str) for x in f16.get("moved_to_16g") or [None]), "the 16F contract's moved_to_16g")

# ---------------------------------------------------------------- 1. the suites ship as authored
sources: dict[str, bytes] = {}
for path in sorted((ROOT / "curriculum/content").glob("*/suites/*.json")):
    document = json.loads(path.read_bytes().decode("utf-8"))
    logical, version = document["item"].rsplit("@v", 1)
    sources[f"{logical}-v{int(version)}.json"] = path.read_bytes()
shipped = {p.name: p.read_bytes() for p in (ASSETS / "code_suites").glob("*.json")}
check("A16G-02_suites_equal_their_sources", len(sources) == 480 and shipped == sources,
      f"sources={len(sources)} shipped={len(shipped)} missing={len(set(sources) - set(shipped))} extra={len(set(shipped) - set(sources))}")
check("A16G-02_runner_equals_its_source", (ASSETS / "code_test_runner.py").read_bytes() == RUNNER_SRC.read_bytes(), "")
check("A16G-02_ship_tool_checks", "--check" in read(SHIP) and "write_bytes(raw)" in read(SHIP) and "json.dumps" not in read(SHIP), "copies bytes, never rewrites")
ports = read(PORTS)
check("A16G-02_ports_defaulted", "fun codeSuiteText(item: VersionedRef): String? = null" in ports and "fun codeRunnerText(): String? = null" in ports
      and sorted(re.findall(r"^interface (\w+)", ports, re.M)) == sorted(["ClockPort", "PersistencePort", "ContentPort", "EvaluatorPort", "TutorPort"]), "")
content = strip_comments(read(CONTENT))
suite_text = body(content, "override fun codeSuiteText(")
check("A16G-02_suite_only_for_its_versions", 'field("format") == "code_test_suite/1"' in suite_text and 'field("item") == item.toString()' in suite_text
      and 'field("suite") == suite.ref.toString()' in suite_text and "val suite = codeTestsFor(item) ?: return null" in suite_text, "")
check("A16G-02_app_reads_its_assets", "codeSuites = ::codeSuite, codeRunner = ::codeRunner" in application
      and 'assets.open("$CODE_SUITES/$name")' in application and 'const val CODE_RUNNER = "code_test_runner.py"' in application, "")

# ---------------------------------------------------------------- 2. what the run serves
kind = body(run_src, "object RunItems")
check("A16G-03_kind_rules", "if (content.answerKeyFor(item.ref) != null) return RunItemKind.ANSWER_KEY" in kind
      and "CodeEvaluation.route(item, suite) == CodeEvaluationRoute.TESTS && content.codeSuiteText(item.ref) != null" in kind
      and "OpenResponse.route(item, null, content.rubricFor(item.ref)) == OpenResponseRoute.AI_RUBRIC" in kind, "no AI for a task that needs a verified result")
servable = body(run_src, "private fun servable(")
check("A16G-03_servable_is_judgeable_and_usable", "RunItems.kind(it, content) } != null && server.usable(ref, INTENT, evaluatorAvailable)" in servable
      and "if (task.purpose == TaskPurpose.ASSESS)" in servable, "")

# ---------------------------------------------------------------- 3. code: the course's tests, nothing written for nothing measured
code = body(run_src, "fun answerCode(")
guard = "if (verdict is CodeVerdict.NotMeasured && verdict.reason != CodeNotMeasured.EVALUATOR_DID_NOT_ANSWER) return RunAnswer.NotRecorded(code = verdict.reason)"
check("A16G-04_nothing_measured_nothing_written", guard in code and code.find(guard) < code.find("record(run, item, submission, body"), "")
check("A16G-04_blank_and_long_refused", "if (code.isBlank()) return RunAnswer.NotRecorded(code = CodeNotMeasured.NOTHING_SUBMITTED, blank = true)" in code
      and "ArtifactBody.store(code) as? ArtifactBody.Stored.Inline ?: return RunAnswer.NotRecorded(tooLong = true)" in code, "")
check("A16G-04_report_not_stored", "EvaluateCode(content, evaluator).evaluate(item, code, report)" in code and "ArtifactBody.store(report" not in run_src
      and (contract.get("code") or {}).get("report_stored") is False and (contract.get("code") or {}).get("artifact") == "the_code", "the code is the artifact")
check("A16G-04_comprehension_offered_after_code", "offer = Comprehension.offer(submission, content.comprehensionChecksFor(item.ref))" in code, "")
record = body(run_src, "private fun record(")
check("A16G-04_one_action_with_its_task", "SubmitAttempt(persistence, clock).submit(submission)" in record
      and "AttemptSubmission(item.ref, body.contentRef, provenance, assistance, plannedTaskId = run.plannedTaskId)" in code
      and "val independence = submission.independence(run.purpose)" in record, "independence derived, never decided")

# ---------------------------------------------------------------- 4. open answers: frozen first, provisional at most, never retried alone
open_ = body(run_src, "fun answerOpen(")
check("A16G-05_frozen_first", "val frozen = record(run, item, submission, body, result = null)" in open_ and "EvaluateOpenResponse" not in open_
      and "return judge(run, item, frozen, response)" in open_, "the answer is in the store before any evaluator is asked")
judge = body(run_src, "private fun judge(")
check("A16G-05_pending_has_no_evidence", "require(!pending || evidenceIds.isEmpty())" in facts
      and "AnswerResult.NOT_MEASURED, emptyList(), pending = true" in record
      and "?: return RunAnswer.Recorded(pending, open = verdict" in judge, "")
re_ = body(run_src, "fun reevaluate(")
check("A16G-05_same_attempt_on_request", 'require(pending.pending) { "only a pending evaluation is asked again" }' in re_
      and "evidence(run, item, pending.attemptId, pending.artifactId, result, checkNotNull(pending.independence)" in judge, "")
check("A16G-05_no_automatic_retry", not re.search(r"repeat\s*\(|while\s*\(|retry", run_src + flow, re.I)
      and (contract.get("open_response") or {}).get("retried_automatically") is False, "")
check("A16G-05_self_check_is_14f", "EvaluateOpenResponse(content, persistence, clock, evaluator).selfCheck(item, attemptId)" in run_src, "")

# ---------------------------------------------------------------- 5. help: ready only when the rules say so, recorded once
ask = body(help_src, "fun ask(ask: TutorAsk")
check("A16G-06_rules_decide_readiness", "when (val preparation = TutorRules.prepare(ask))" in ask and "else -> Asked.NotReady(preparation)" in ask
      and "val preparation = TutorRules.prepare(ask)" in flow and "if (preparation !is TutorPreparation.Ready)" in flow, "")
after = body(help_src, "private fun recordAfterAnswer(")
check("A16G-06_after_freeze_recorded_on_the_attempt", "event.timing !in setOf(AssistanceTiming.AFTER_SUBMIT, AssistanceTiming.AFTER_FAILURE)" in after
      and '"assistance_event"' in after and '"attempt_id" to attemptId.toString()' in after
      and "(outcome as? TutorOutcome.Shown)?.event ?: return" in after, "only shown help, only after the answer froze")
submit_keys = re.findall(r'"(\w+)" to', body(strip_comments(read(SUBMIT)), '"assistance_event"'))
check("A16G-06_same_shape_as_submission", set(re.findall(r'"(\w+)" to', after)) == set(submit_keys) and submit_keys, str(submit_keys))
check("A16G-06_during_attempt_travels_with_it", "if (!frozen) (asked.outcome as? TutorOutcome.Shown)?.event?.let(run.pendingHelp::add)" in flow
      and "return lesson + pendingHelp" in session and "run.helpWithAnswer()" in flow and "run.helpWithAnswer()" in activity, "")
# An expression body: read from the signature to the next declaration, not to the first brace.
ask_for = help_src[help_src.find("fun askFor("):help_src.find("private fun recordAfterAnswer(")]
check("A16G-06_only_tutor_context_leaves", "learnerWork = frozenWork?.takeIf { frozen != null }" in ask_for
      and "ceiling = ceiling.takeIf { frozen == null }" in ask_for and "it.objective in item.targetObjectives" in ask_for
      and "instructionMode = InstructionMode.TURKISH_PRIMARY" in ask_for, "")
check("A16G-06_failure_timing", "frozen.result == AnswerResult.NOT_RIGHT || frozen.result == AnswerResult.PARTLY -> AssistanceTiming.AFTER_FAILURE" in ask_for, "")
view = body(help_presentation, "fun view(")
check("A16G-06_no_gloss_guessed", "TutorIntent.GLOSS" not in help_presentation and "GLOSS" not in flow
      and (contract.get("help") or {}).get("gloss_offered") is False, "")
check("A16G-06_asks_that_fit", "listOf(TutorIntent.EXPLAIN_MISTAKE, TutorIntent.EXPLAIN_DIFFERENTLY, TutorIntent.QUESTION)" in view
      and "listOf(TutorIntent.HINT, TutorIntent.EXPLAIN_DIFFERENTLY, TutorIntent.QUESTION)" in view, "")
check("A16G-06_explanation_is_14c", "ExplainDifferently(content, persistence, askTutor)" in help_src and "explain.prepare(base, objective, form)" in help_src, "")
check("A16G-06_not_shown_is_no_fault", "TutorPanelState.NOT_SHOWN)" in help_presentation and "Tone.FAULT" not in help_presentation
      and "TutorPanelState.WAITING -> Tone.PENDING_UNRESOLVED" in read(ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TutorPresentation.kt"), "")

# ---------------------------------------------------------------- 6. wrong answers and the comprehension check
check("A16G-07_wrong_answer_is_14b", "AnalyzeWrongAnswer(persistence).analyze(" in help_src
      and "answered.result != AnswerResult.NOT_RIGHT && answered.result != AnswerResult.PARTLY" in application
      and "WrongAnswerSummary.of(findings" in application, "read from the stored attribution, after a wrong answer only")
comp = body(help_src, "fun comprehensionAnswer(")
check("A16G-07_comprehension_is_14e", "CheckUnderstanding(content, askTutor).answer(item, check, chosen)" in comp
      and "if (result is ComprehensionResult.Measured)" in comp and "check.evidenceType" in comp
      and "AssistanceInterpretation.independence(emptyList(), ProvenanceOrigin.USER_AUTHORED, run.purpose)" in comp, "a skip writes nothing; teach is practice")
check("A16G-07_practice_is_labelled", "ComprehensionPresentation.practiceNotes(outcome)" in help_presentation
      and "CheckUnderstanding(content, askTutor).practice(" in help_src, "written first, the tutor's question is practice")

# ---------------------------------------------------------------- 7. a durable pause
conditions = body(presentation, "fun pauseConditions(")
check("A16G-08_boundary_only", "if (!betweenQuestions) return emptySet()" in conditions
      and "if (!evaluationPending) add(SafeCheckpointCondition.NO_HALF_EVALUATED_INDEPENDENT_ATTEMPT)" in conditions
      and "if (!helpInFlight) add(SafeCheckpointCondition.NO_PARTIALLY_EXPOSED_SOLUTION_STATE)" in conditions, "")
pause = body(flow, "fun pause(")
check("A16G-08_policy_decides", "PausePolicy.classify(highStakesWork = run.run.purpose in TutorRules.measuringPurposes, confirmed = confirmed)" in pause
      and "is PauseDecision.NotDurable ->" in pause and "pauseRun(" in body(pause, "is PauseDecision.Durable ->") and "pauseRun(" not in body(pause, "is PauseDecision.NotDurable ->"), "")
check("A16G-08_kind_is_the_policy_s", "kind = kind," in body(help_src, "fun pause(") and "if (shown.isEmpty() || remaining.isEmpty()) return null" in help_src, "")
check("A16G-08_saved_state_only_after_durable", "data class Paused(val decision: PauseDecision.Durable" in presentation
      and "PausePolicy.stateAfter(decision, RunnerState.FEEDBACK_RESOLVED)" in presentation
      and screen.count("RunnerCopy.CHECKPOINT_SAVED") == 1, "")
check("A16G-08_high_stakes_marked", "TaskRunCopy.HIGH_STAKES_PAUSED.takeIf { decision.kind == CheckpointKind.HIGH_STAKES_PAUSE }" in presentation, "")
check("A16G-08_today_offers_no_checkpoint", (contract.get("pause") or {}).get("today_offers_checkpoint") is False
      and "ResumeCheckpoints" not in read(ANDROID / "core-application/src/main/kotlin/coach/application/TodayFactsQuery.kt"), "")

# ---------------------------------------------------------------- 8. the words and the screen
copy = strings(body(presentation, "object TaskRunCopy")) + strings(body(help_presentation, "object RunHelpCopy"))
# "başarısız" alone is not listed: 16F's own copy says "Bu bir başarısızlık değil", a negation.
forbidden = ["yanlış yaptın", "başarısız oldun", "puan", "%", "seviye", "ustalaştın", "hata yaptın", "suç", "ceza", "hile", "seri", "mastered"]
check("A16G-09_no_verdict_words", copy and not [w for w in forbidden if any(w in c.lower() for c in copy)], str([w for w in forbidden if any(w in c.lower() for c in copy)]))
answered = body(presentation, "fun answered(")
check("A16G-09_code_never_reveals", "canReveal = kind == RunItemKind.ANSWER_KEY" in answered and "CodeEvaluationPresentation.notes(code)" in answered, "")
check("A16G-09_unmeasured_is_not_wrong", "answer.code?.let(CodeEvaluationCopy::notMeasured) ?: TaskRunCopy.resultLines.getValue(AnswerResult.NOT_MEASURED)" in presentation
      and "pending -> RunnerState.EVALUATION_PENDING" in presentation, "")
check("A16G-09_screen_decides_nothing", "coach.application" not in screen and "Tone." not in screen and not re.search(r"ProgressIndicator|Slider|\d+\s*%", screen), "")
check("A16G-09_clipboard_only", "ClipData.newPlainText(" in screen and "setClipEntry(" in screen
      and not re.search(r"Intent\.ACTION_SEND|openOutputStream|CreateDocument|java\.net|HttpURLConnection", screen + flow), "")
def permissions(path: Path) -> set[str]:
    return set(re.findall(r'uses-permission android:name="([^"]+)"', read(path)))
check("A16G-09_no_permission_added", permissions(MANIFEST) == {"android.permission.POST_NOTIFICATIONS", "android.permission.RECEIVE_BOOT_COMPLETED"}
      and permissions(AI_MANIFEST) == {"android.permission.INTERNET", "android.permission.POST_NOTIFICATIONS", "android.permission.RECEIVE_BOOT_COMPLETED"}, "")
check("A16G-09_exit_still_first", screen.find("OutlinedButton(onClick = onExit") < screen.find("StateChip(") < screen.find("stage?.let { TaskRunInterior(it, actions) }"), "")
check("A16G-09_help_always_reachable", screen.count("HelpSection(stage.help, actions)") == 2 and "TextButton(onClick = actions.onOpenHelp" in screen, "in a question and after an answer")
check("A16G-09_wired_in_core_order", all(x in activity for x in ("onSubmitCode = { code, report -> flow.submitJudged(code, report) }", "onPause = { flow.pause() }",
      "onHelpIntent = { flow.intent(it) }", "current.newQuestion(item, prompt.orEmpty(), served)")), "")

# ---------------------------------------------------------------- 9. the rest moved, not claimed
moved = contract.get("moved_to_16h") or []
check("A16G-10_rest_moved_not_claimed", len(moved) >= 4 and all(isinstance(m, str) for m in moved)
      and any("assessment sessions" in m for m in moved) and any("fast path" in m for m in moved), str(len(moved)))
check("A16G-10_16h_in_the_plan", "**16H — Uçtan uca günlük döngü: ölçme oturumları**" in read(EXECUTION_INDEX)
      and "16H — Uçtan uca günlük döngü: ölçme oturumları" in read(MASTER_PLAN), "the step's own checklist lines")

# ---------------------------------------------------------------- tests name what they guard
for path, names in ((MODEL_TEST, ["a pending evaluation is recorded with no evidence, and an answer is either recorded or says why nothing was"]),
                    (PRESENTATION_TEST, ["the asks that fit the moment are offered, and a gloss is never guessed",
                                         "what an ask still needs is shown in the rules' order, and nothing is a fault",
                                         "an open help panel is assistance open, and only a boundary between questions can be a safe checkpoint"]),
                    (APPLICATION_TEST, ["while the answer is open the ask is during the attempt, keeps the learner's level and carries no answer"]),
                    (CURRICULUM_TEST, ["the shipped suite is served for its own item version and suite version only"]),
                    (COURSE_TEST, ["every shipped suite is served for exactly its item version, and the runner ships as the course authored it",
                                   "with the shipped suites every Skill has a task the app can run, where an answer key alone left the coding Skills out",
                                   "a code answer is judged by the course's tests from the pasted report, and a report that measures nothing writes nothing",
                                   "an open answer waits when no evaluator answers, is judged again only when asked, and the rubric is the learner's own check",
                                   "help after an answer froze is recorded on that attempt, and help that was not shown is recorded nowhere",
                                   "a pause between questions writes one checkpoint the planner can read, and nothing is written when nothing is left"])):
    text = read(path)
    for name in names:
        check(f"A16G-11_test_{re.sub(r'[^a-z0-9]+', '_', name[:40].lower()).strip('_')}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- narrowed gates
gates = (contract.get("narrowed_gates") or {}).get("gates") or []
check("A16G-12_narrowed_gates_marked", len(gates) >= 1 and all(
    read(ROOT / g.get("file", "")).count("Narrowed at 16G (`HLPX-v0 / D-130`)") >= max(1, len(g.get("checks") or [])) for g in gates),
    "one marker per narrowed check: " + str([g.get("file") for g in gates]))

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results_kotlin") if isinstance(contract.get("mutation_results_kotlin"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("A16G-13_kotlin_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("A16G-13_kotlin_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("A16G-13_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm.get("total")))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("A16G-13_runs_pass", len(runs) >= 6 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") or {}
check("A16G-13_device_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("run_seen_on_device") is False
      and dv.get("live_provider_called") is False, "")
text_all = "\n".join(line for line in read(CONTRACT).splitlines() if not line.lstrip().startswith("#"))  # a comment is not a claim
check("A16G-13_no_claim_words", not re.search(r"\bsurvived\b(?!_as_expected)|\b100%|streak|banana", text_all.split("validator_mutation:")[0]), "")

# ---------------------------------------------------------------- decisions, spec, research
decisions = read(DECISIONS)
check("A16G-14_decisions", "## D-130 — Görev içinde yardım ve değerlendirilen cevaplar = HLPX-v0" in decisions
      and "## D-131 — 16H eklendi: Uçtan uca günlük döngü — ölçme oturumları" in decisions, "")
spec = read(SPEC)
check("A16G-14_spec_accepted", "**Status:** ACCEPTED — independent 16G QA PASS" in spec and "`HLPX-v0" in spec, "")
check("A16G-14_spec_next_is_16h", "Next numbered step: `16H — Uçtan uca günlük döngü: ölçme oturumları`" in spec, "")
research = read(RESEARCH)
check("A16G-14_research_sources", all(x in research for x in ("TRUX-v0", "TUTX-v0", "CDEX-v0", "OREX-v0", "ACCX-v0", "Shute 2008", "Aleven et al. 2003")), "")

finish()
