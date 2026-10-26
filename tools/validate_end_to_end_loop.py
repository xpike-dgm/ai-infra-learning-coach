"""Independent 16F QA — E2EX-v0, a planned task run from Today to its end.

The rules are read from the accepted contracts — `TRUX-v0` (`ux/8c_daily_working_flow/`, `docs/DAILY_WORKING_FLOW_SPEC.md`:
entry revalidation, orientation, submission, resolve, transition, assistance, provenance, in-flight protection),
`RNRX-v0` (11B), `DMAX-v0` (11D), `OREX-v0` (14F), `RPLX-v0` (12D), `PCRX-v0` (13E), `RMDX-v0`'s open loop (16E) — and
compared with the real Kotlin, the schema, the contract, the tests, the spec, the research and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a task started on an assumed condition; a runner
that decides what an answer proves, asks an AI, or claims a level; an attempt that does not name its planned task, or
one recorded apart from its help and provenance; a question served twice or a cached list advanced; provenance
preselected; a reveal that rewrites a frozen answer's evidence or skips the consequence; a replan that drops started
work; Today offering started work again; and the rest of the loop silently claimed instead of moved to 16G.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/16f_end_to_end_loop/end_to_end_loop.yaml"
QA_OUT = ROOT / "arch/16f_end_to_end_loop/qa_report.yaml"
SPEC = ROOT / "docs/END_TO_END_LOOP_SPEC.md"
RESEARCH = ROOT / "research/16f_end_to_end_loop_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
FLOW_SPEC = ROOT / "docs/DAILY_WORKING_FLOW_SPEC.md"
EXECUTION_INDEX = ROOT / "docs/EXECUTION_INDEX.md"
MASTER_PLAN = ROOT / "docs/MASTER_PLAN.md"

FACTS = ANDROID / "core-model/src/main/kotlin/coach/TaskRunFacts.kt"
ATTEMPT_FACTS = ANDROID / "core-model/src/main/kotlin/coach/AttemptFacts.kt"
TODAY_FACTS = ANDROID / "core-model/src/main/kotlin/coach/TodayFacts.kt"
TASK_FACTS = ANDROID / "core-model/src/main/kotlin/coach/AuthoredTaskFacts.kt"
PORTS = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
ADAPTER = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
CONTENT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
RUN = ANDROID / "core-application/src/main/kotlin/coach/application/RunTask.kt"
SUBMIT = ANDROID / "core-application/src/main/kotlin/coach/application/SubmitAttempt.kt"
BUILD = ANDROID / "core-application/src/main/kotlin/coach/application/BuildDailyPlan.kt"
SERVE = ANDROID / "core-application/src/main/kotlin/coach/application/DailyMicroAssessment.kt"
CHANGES = ANDROID / "core-application/src/main/kotlin/coach/application/ProgramChanges.kt"
SETTINGS = ANDROID / "core-application/src/main/kotlin/coach/application/Settings.kt"
TODAY_QUERY = ANDROID / "core-application/src/main/kotlin/coach/application/TodayFactsQuery.kt"
REMINDERS = ANDROID / "core-application/src/main/kotlin/coach/application/Reminders.kt"
PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TaskRunPresentation.kt"
TODAY_PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TodayPresentation.kt"
SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/TaskRunnerScreen.kt"
APPLICATION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
ACTIVITY = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
SESSION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/TaskRunSession.kt"
MODEL_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/TaskRunFactsTest.kt"
STORAGE_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/TaskLinkStorageTest.kt"
PRESENTATION_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/TaskRunPresentationTest.kt"
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
    out = {"model": "E2EX-v0", "stage_step": "16F", "decision": "D-128", "result": "PASS" if not failures else "FAIL",
           "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
    if "--no-write" not in sys.argv:
        QA_OUT.parent.mkdir(parents=True, exist_ok=True)
        QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
    print(f"16F QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(0 if not failures else 1)


for path in (CONTRACT, SPEC, RESEARCH, FLOW_SPEC, FACTS, ATTEMPT_FACTS, TODAY_FACTS, TASK_FACTS, PORTS, SCHEMA, MIGRATIONS, ADAPTER,
             CONTENT, RUN, SUBMIT, BUILD, SERVE, CHANGES, SETTINGS, TODAY_QUERY, REMINDERS, PRESENTATION, TODAY_PRESENTATION, SCREEN,
             APPLICATION, ACTIVITY, SESSION, MODEL_TEST, STORAGE_TEST, PRESENTATION_TEST, COURSE_TEST):
    check(f"A16F-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
schema = strip_comments(read(SCHEMA))
run_src = strip_comments(read(RUN))
facts = strip_comments(read(FACTS))
presentation = strip_comments(read(PRESENTATION))
screen = strip_comments(read(SCREEN))
application = strip_comments(read(APPLICATION))
activity = read(ACTIVITY)
run_cfg = contract.get("run") or {}

# ---------------------------------------------------------------- contract and its source
check("A16F-01_model", contract.get("model") == "E2EX-v0" and contract.get("stage_step") == "16F" and contract.get("decision") == "D-128"
      and contract.get("new_step_decision") == "D-129", "")
ud = contract.get("user_decisions") or {}
check("A16F-01_user_decisions", ud.get("approval") == "explicit_user_approval_2026-10-25"
      and ud.get("scope", "").startswith("core_loop_only") and ud.get("new_step", "").startswith("16G_added_like_d_099_no_renumbering")
      and ud.get("feedback") == "right_or_wrong_after_submit_seeing_the_answer_is_a_separate_optional_step_recorded_as_h4", str(ud))
scope = contract.get("scope") or {}
check("A16F-01_scope", all(scope.get(k) is False for k in ("boundaries_changed", "planner_rule_changed", "mastery_rule_changed", "evidence_rule_changed",
      "assessment_sessions_wired", "code_report_import", "open_response_ai", "tutor_panel", "fast_path_entry", "dispositions_from_the_app"))
      and scope.get("app_runs_tasks") is True and scope.get("schema_version") == 11 and scope.get("ports_changed") == "refinements_only", "")
flow = read(FLOW_SPEC)
check("A16F-01_trux_rules_are_the_source", all(x in flow for x in ("the `PlannedTask` is still selected and current", "post_submit_explanation_contaminates_prior_attempt = false",
      "The next task is always the planner's recomputed current selection", "provenance is **asked, not inferred**", "Before granting H3 or H4")), "")
check("A16F-01_entry_conditions_are_trux", run_cfg.get("entry_conditions") == ["planned_task_still_selected", "hard_prerequisites_satisfied",
      "learning_need_still_open", "content_version_compatible", "required_local_capability_available"], "")

# ---------------------------------------------------------------- 1. the attempt names its planned task (schema v11)
v11 = schema[schema.find("val v11: List<String>"):schema.find("const val MISCONCEPTION_SOURCES")]
check("A16F-02_schema_v11", "const val VERSION = 11" in schema and "10 to Schema.v11," in strip_comments(read(MIGRATIONS)), "")
check("A16F-02_one_added_column", '"ALTER TABLE attempt ADD COLUMN planned_task_id INTEGER REFERENCES planned_task (id)"' in v11
      and len(re.findall(r"ALTER TABLE", v11)) == 1 and not re.search(r"DROP|UPDATE|DELETE", v11), "an added nullable column, nothing rewritten")
check("A16F-02_migration_owned", (contract.get("schema_migration") or {}) == {"from": 10, "to": 11, "owner": "16F", "tables": ["attempt"],
      "truth_altered": False, "column_added": "planned_task_id", "decision": "D-128"}, str(contract.get("schema_migration")))
ports = read(PORTS)
check("A16F-02_ports_defaulted", "fun startedPlannedTasks(plannedTaskIds: Collection<Long>): Set<Long> = emptySet()" in ports
      and "fun authoredTask(ref: VersionedRef): AuthoredTask? = null" in ports
      and sorted(re.findall(r"^interface (\w+)", ports, re.M)) == sorted(["ClockPort", "PersistencePort", "ContentPort", "EvaluatorPort", "TutorPort"]), "")
check("A16F-02_adapter_reads_the_link", "SELECT DISTINCT planned_task_id FROM attempt WHERE planned_task_id IN" in read(ADAPTER)
      and "override fun authoredTask(ref: VersionedRef): AuthoredTask? = parsed?.tasks.orEmpty().singleOrNull { it.ref == ref }" in read(CONTENT), "")
check("A16F-02_attempt_carries_the_link", "val plannedTaskId: Long? = null," in read(ATTEMPT_FACTS)
      and 'submission.plannedTaskId?.let { put("planned_task_id", it.toString()) }' in read(SUBMIT), "")

# ---------------------------------------------------------------- 2. entry: confirmed, never assumed (TRUX-v0 §6.1)
open_ = body(run_src, "class OpenTask(")
check("A16F-03_today_s_plan_only", "PlanReading.read(stored, clock.now().studyDay) as? PlanReading.Read.Today ?: return notSelected" in open_, "")
check("A16F-03_started_is_not_selected", "if (row.position in StartedTasks.positions(persistence, stored, read.trace)) return notSelected" in open_, "")
check("A16F-03_gate_asked", "ResolvePrerequisites(persistence)" in open_ and ".eligibility.waits" in open_, "")
check("A16F-03_need_still_open", "openNeeds().any { it.needKey == entry.needKey }" in open_, "")
check("A16F-03_content_read_not_assumed", "AuthoredTask.refOfCandidate(entry.candidateId)" in open_ and "content::authoredTask" in open_
      and "persistence.skill(entry.primarySkill) != null" in open_, "")
servable = body(run_src, "private fun servable(")
check("A16F-03_assess_is_16g", "if (task.purpose == TaskPurpose.ASSESS) return" in servable and "CapabilityGap.ASSESSMENT_INTERIOR_NOT_AVAILABLE" in servable, "")
# Narrowed at 16G (`HLPX-v0 / D-130`): an item is servable when the app can judge it at all — an answer key, the course's
# tests with a shipped suite, or a rubric the evaluator may judge (`RunItems.kind`), still only when usable.
check("A16F-03_answer_key_only", ("content.answerKeyFor(it) != null && server.usable(it, INTENT, evaluatorAvailable)" in servable
       or "RunItems.kind(it, content) } != null && server.usable(ref, INTENT, evaluatorAvailable)" in servable)
      and "CapabilityGap.NO_ITEM_THIS_APP_CAN_EVALUATE" in servable and "CapabilityGap.NO_UNSEEN_ITEM" in servable, "")
check("A16F-03_entry_writes_nothing", not re.search(r"appendTruth|inTransaction|\.build\(|\.replan\(|offer\(", open_), "")
check("A16F-03_runs_only_when_all_confirmed", "require((run != null) == startable)" in facts
      and "val startable: Boolean get() = stillSelected && prerequisitesSatisfied && needStillOpen && contentCompatible && capabilityAvailable" in facts, "")
build = strip_comments(read(BUILD))
check("A16F-03_open_needs_is_build_s_composition", "fun openNeeds(): List<LearningNeed>" in build
      and "PlannerEngine.needsFromSkillStates(states) + WeaknessEngine.needs(states) + diagnostic?.needs.orEmpty() +" in body(build, "fun openNeeds()")
      and not re.search(r"appendTruth|inTransaction", body(build, "fun openNeeds()")), "")
check("A16F-03_usable_records_nothing", "if (serve) recordExposure(item.ref, item.variantFamilyId, ITEM_VERSION_SEEN, null)" in read(SERVE)
      and "offer(ref, intent, evaluatorAvailable, serve = false) is ItemOffer.Ready" in read(SERVE), "")

# ---------------------------------------------------------------- 3. the run: serve, answer, judge, record
runtask = body(run_src, "class RunTask(")
serve = body(runtask, "fun serve(")
check("A16F-04_unseen_in_order_never_twice", "run.items.filterNot { it in served }.forEach" in serve and "server.offer(ref, OpenTask.INTENT, evaluatorAvailable)" in serve
      and run_cfg.get("items_served") == "unseen_in_authored_order", "")
answer = body(runtask, "fun answer(")
check("A16F-04_blank_is_a_skip", "if (response.isBlank()) return null" in answer and run_cfg.get("blank_answer_writes_nothing") is True, "")
check("A16F-04_one_action_with_its_task", "AttemptSubmission(item.ref, body.contentRef, provenance, assistance, plannedTaskId = run.plannedTaskId)" in answer
      and answer.find("SubmitAttempt(persistence, clock).submit(submission)") < answer.find("RecordEvidence(persistence, clock).record("), "")
# Narrowed at 16G (`HLPX-v0 / D-130`): the answer-key path still asks no AI; code and rubric answers are 16G's own paths.
check("A16F-04_judged_by_the_key_only", "OpenResponse.byKey(item, key, response.trim())" in answer and "evaluator" not in answer.replace("evaluatorAvailable", "")
      and run_cfg.get("evaluation") == "answer_key_only", "no AI judges an answer here")
check("A16F-04_independence_derived", "independence = submission.independence(run.purpose)," in answer, "never decided by the runner")
check("A16F-04_contamination_named", "PrerequisiteSnapshot.CONTAMINATED" in answer and "gate.eligibility.waits" in answer, "")
check("A16F-04_too_long_refused", "ArtifactBody.store(response.trim()) as? ArtifactBody.Stored.Inline ?: return null" in answer, "")

# ---------------------------------------------------------------- 4. seeing the answer
reveal = body(runtask, "fun reveal(")
check("A16F-05_after_submit_is_h4_after_submit", '"level" to AssistanceLevel.H4.id' in reveal and '"timing" to AssistanceTiming.AFTER_SUBMIT.id' in reveal
      and '"exposure_kind" to ServeDailyMicroItem.SOLUTION_EXPOSURE' in reveal and reveal.count("persistence.inTransaction {") == 1
      and "evidence_event" not in reveal, "the frozen answer's evidence is not touched")
before = body(runtask, "fun revealBeforeAnswer(")
check("A16F-05_before_writes_no_attempt", "recordSolutionExposure(item, attemptId = null, maxLevel = AssistanceLevel.H4.id)" in before
      and "SubmitAttempt" not in before and "RecordEvidence" not in before, "")
check("A16F-05_disclosure_first", "AssistancePolicy.request(AssistanceLevel.H4, AssistanceTiming.DURING_ATTEMPT" in activity
      and "if (outcome is AssistanceOutcome.ConsequenceDisclosureRequired) runStage = question(run, disclosure = true)" in activity
      and "ConsequenceDisclosure(onAcknowledge = actions.onAcknowledgeSolution" in screen, "nothing is shown before the consequence is said")
check("A16F-05_reveal_only_when_asked", "TextButton(onClick = actions.onReveal" in screen and "revealed = null" in activity, "never automatic")

# ---------------------------------------------------------------- 5. the end: report, replan keeping started work
fin = body(runtask, "fun finish(")
check("A16F-06_nothing_answered_changes_nothing", "if (answered.none { it.evidenceIds.isNotEmpty() }) return null" in fin, "")
check("A16F-06_started_work_kept", "ReportProgramChanges(persistence, content, clock).report(before, touched, capacity, StartedTasks.today(persistence, clock))" in fin
      and "keptPositions = keptPositions))" in strip_comments(read(CHANGES))
      and "keptPositions = StartedTasks.today(persistence, clock)" in strip_comments(read(SETTINGS)), "every replan keeps the tasks the learner started")
started = body(run_src, "object StartedTasks")
check("A16F-06_started_is_read", "persistence.startedPlannedTasks(stored.plannedTasks.map { it.plannedTaskId })" in started
      and "trace.selected.filter { it.preserved }" in started, "")
check("A16F-06_today_does_not_offer_started", "val started: Boolean = false," in read(TODAY_FACTS)
      and "!task.blocked && !task.kept && !task.started" in strip_comments(read(TODAY_PRESENTATION))
      and "StartedTasks.positions(persistence, stored, read.trace)" in read(TODAY_QUERY), "")
check("A16F-06_no_cached_list", "current.shown.map { it.ref }.toSet()" in activity and "NextTaskSource.RECOMPUTED_PLANNER_SELECTION" in
      read(ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TaskRunner.kt") and run_cfg.get("next_task_source") == "recomputed_planner_selection", "")
check("A16F-06_processed_once_even_on_exit", "if (run.finished) return" in activity and "run?.let { finishRun(app, it, onTodayFacts) {} }" in activity, "")
check("A16F-06_report_words_from_13e", "ProgramChangeResults.summary(reported.report)" in application and "ProgramChangeResults.canonicalChanges(reported.report, label)" in application
      and "TaskRunCopy.NOTHING_ANSWERED" in application, "")

# ---------------------------------------------------------------- 6. words: no verdict, no level
words = (r"yanlış yaptın", r"başarısız oldun", r"\bpuan", r"%", r"seviye", r"ustalaştın", r"hata yaptın", r"\bsuç", r"\bceza", r"hile")
copy = strings(body(presentation, "object TaskRunCopy"))
bad = [s for s in copy for w in words if re.search(w, s, re.I)]
check("A16F-07_no_verdict_words", bad == [], str(bad[:3]))
check("A16F-07_not_measured_is_not_wrong", '"Bu cevap ölçülemedi; bu bir yanlış sayılmaz."' in presentation
      and run_cfg.get("answer_results") == ["right", "partly", "not_right", "not_measured"], "")
check("A16F-07_provenance_all_five_nothing_chosen", all(f"ProvenanceOrigin.{o} to" in presentation for o in
      ("USER_AUTHORED", "USER_AUTHORED_WITH_ASSISTANCE", "MIXED_AUTHORSHIP", "GENERATED_OR_COPIED", "UNKNOWN_PROVENANCE"))
      and "mutableStateOf<ProvenanceOrigin?>(null)" in screen and "enabled = chosen != null" in screen and run_cfg.get("provenance_preselected") is False, "")
check("A16F-07_teach_is_practice_said", "const val TEACH_NOTE" in presentation and run_cfg.get("teach_task_independence") == "practice_only", "")
check("A16F-07_screen_decides_nothing", "coach.application" not in screen and "Tone." not in screen
      and not re.search(r"ProgressIndicator|Slider|\d+\s*%", screen), "")
check("A16F-07_exit_still_first", screen.find("OutlinedButton(onClick = onExit") < screen.find("StateChip(") < screen.find("stage?.let { TaskRunInterior(it, actions) }"), "")

# ---------------------------------------------------------------- 7. the reminder closes 16E's loop
reminders = strip_comments(read(REMINDERS))
check("A16F-08_reminder_counts_unstarted", "contentOf(trace, started)" in reminders and "it.preserved || it.position in started" in reminders
      and "countTruth" not in body(reminders, "fun decide("), "")
check("A16F-08_reminder_rule_recorded", (contract.get("reminder_rule_change") or {}).get("silences_changed") is False, "")

# ---------------------------------------------------------------- 8. the rest moved, not claimed
moved = contract.get("moved_to_16g") or []
check("A16F-09_rest_moved_not_claimed", len(moved) >= 7 and all(scope.get(k) is False for k in ("assessment_sessions_wired", "code_report_import", "tutor_panel")), str(len(moved)))
check("A16F-09_16g_in_the_plan", "**16G — Uçtan uca günlük döngü: yardım ve ölçüm**" in read(EXECUTION_INDEX)
      and "16G — Uçtan uca günlük döngü: yardım ve ölçüm" in read(MASTER_PLAN), "the step's own checklist lines")

# ---------------------------------------------------------------- tests name what they guard
for path, names in ((MODEL_TEST, ["an answer is right, partly right, not right or not measurable, and nothing else",
                                  "a task runs exactly when every entry condition is confirmed, and only a checked capability names its gap"]),
                    (STORAGE_TEST, ["schema v11 links attempts to planned tasks on a populated v10 database and leaves every truth row as it was",
                                    "the store says which planned tasks were started, and an attempt outside a planned task names none"]),
                    (PRESENTATION_TEST, ["no result, capability or end-of-run line blames the learner or claims a level",
                                         "orientation says what, why and how long, the help policy before any work, and asks provenance with nothing chosen"]),
                    (COURSE_TEST, ["a new learner's first task enters with every condition confirmed, from the store and nothing assumed",
                                   "a run serves unseen questions, records each answer with its planned task, judges it by its key and writes its evidence",
                                   "a started task is not offered again, entering it again is refused, and the reminder no longer counts it",
                                   "a replan keeps every task the learner started and re-resolves only the rest",
                                   "the end of a run reports what canonical state did, and a run with nothing answered changes nothing",
                                   "entry names what it could not confirm, and a task the app cannot run says why without blaming anyone",
                                   "the open needs entry reads are the very needs today's plan was built from"])):
    text = read(path)
    for name in names:
        check(f"A16F-10_test_{re.sub(r'[^a-z0-9]+', '_', name[:40].lower()).strip('_')}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- narrowed gates
gates = (contract.get("narrowed_gates") or {}).get("gates") or []
check("A16F-11_narrowed_gates_marked", len(gates) >= 6 and all(
    read(ROOT / g.get("file", "")).count("Narrowed at 16F (`E2EX-v0` / `D-128`)") >= max(1, len(g.get("checks") or [])) for g in gates),
    "one marker per narrowed check: " + str([g.get("file") for g in gates]))

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results_kotlin") if isinstance(contract.get("mutation_results_kotlin"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("A16F-12_kotlin_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("A16F-12_kotlin_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("A16F-12_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("A16F-12_runs_pass", len(runs) >= 6 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") or {}
check("A16F-12_device_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("run_seen_on_device") is False, "")

# ---------------------------------------------------------------- spec, research, decisions
spec = read(SPEC)
check("A16F-13_spec_status", "**Status:** ACCEPTED — independent 16F QA PASS" in spec and "`D-128`" in spec and "`D-129`" in spec
      and not re.search(r"__[A-Z][A-Z_]*__", spec), "")
check("A16F-13_spec_next", "**16G — Uçtan uca günlük döngü: yardım ve ölçüm**" in spec and "**Not run: T6.**" in spec, "")
research = read(RESEARCH)
check("A16F-13_research", all(r in research for r in ("TRUX-v0", "RNRX-v0", "DMAX-v0", "OREX-v0", "RPLX-v0", "PCRX-v0", "RMDX-v0", "D-125")), "")
decisions = read(DECISIONS)
check("A16F-13_decisions", re.search(r"^## D-128 .*E2EX-v0", decisions, re.M) is not None and re.search(r"^## D-129 .*16G", decisions, re.M) is not None, "")

finish()
