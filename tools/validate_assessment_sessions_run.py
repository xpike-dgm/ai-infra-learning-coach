"""Independent 16H QA — ASRX-v0, measurement sessions from Today.

The rules are read from the accepted contracts — `ASUX-v0` (8D: one interior, skip != incorrect, contested evidence held,
no score), `DMAX-v0` (11D), `WBAX-v0`/`MCAX-v0` (13A/13B slots and slot evidence), `VDWX-v0` (13F fast path), `WLRX-v0`
(13D dispositions), `SPWX-v0` §7.2 (`assessment_opportunity`), `PDT-v0` §15 (replan triggers) — and compared with the real
Kotlin, the contract, the tests, the spec, the research and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a planned slot or check entered on a guess (another
cycle, another diagnostic, an unpublished or seen item); an attempt that does not carry its session; a slot answer that
is not the slot's evidence; entry that writes truth; a skip written or counted as wrong; a report that edits a row or
counts against the learner; a result with a score; a fast path the learner did not open, that drops started work or
that replans under an undeclared trigger; and work silently claimed instead of moved on.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/16h_assessment_sessions/assessment_sessions.yaml"
QA_OUT = ROOT / "arch/16h_assessment_sessions/qa_report.yaml"
SPEC = ROOT / "docs/ASSESSMENT_SESSIONS_RUN_SPEC.md"
RESEARCH = ROOT / "research/16h_assessment_sessions_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
ASUX_SPEC = ROOT / "docs/ASSESSMENT_SESSION_UX_SPEC.md"
EXECUTION_INDEX = ROOT / "docs/EXECUTION_INDEX.md"
MASTER_PLAN = ROOT / "docs/MASTER_PLAN.md"

FACTS = ANDROID / "core-model/src/main/kotlin/coach/TaskRunFacts.kt"
PLANNER_FACTS = ANDROID / "core-model/src/main/kotlin/coach/PlannerFacts.kt"
EVIDENCE_FACTS = ANDROID / "core-model/src/main/kotlin/coach/EvidenceFacts.kt"
WEAKNESS_FACTS = ANDROID / "core-model/src/main/kotlin/coach/WeaknessFacts.kt"
PROGRESS_FACTS = ANDROID / "core-model/src/main/kotlin/coach/ProgressFacts.kt"
RUN = ANDROID / "core-application/src/main/kotlin/coach/application/RunTask.kt"
FAST = ANDROID / "core-application/src/main/kotlin/coach/application/FastPath.kt"
BLUEPRINT = ANDROID / "core-application/src/main/kotlin/coach/application/BlueprintAssessment.kt"
PROGRESS_QUERY = ANDROID / "core-application/src/main/kotlin/coach/application/ProgressQuery.kt"
HISTORY = ANDROID / "core-application/src/main/kotlin/coach/application/LearningHistory.kt"
SETTINGS = ANDROID / "core-application/src/main/kotlin/coach/application/Settings.kt"
PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TaskRunPresentation.kt"
PROGRESS_PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/ProgressPresentation.kt"
HISTORY_PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/HistoryPresentation.kt"
SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/TaskRunnerScreen.kt"
TOPIC_SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/TopicScreen.kt"
APPLICATION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
ACTIVITY = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
FLOW = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/TaskRunFlow.kt"
SESSION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/TaskRunSession.kt"
MODEL_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/TaskRunFactsTest.kt"
PRESENTATION_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/MeasurementPresentationTest.kt"
PROGRESS_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/ProgressPresentationTest.kt"
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
    out = {"model": "ASRX-v0", "stage_step": "16H", "decision": "D-132", "result": "PASS" if not failures else "FAIL",
           "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
    if "--no-write" not in sys.argv:
        QA_OUT.parent.mkdir(parents=True, exist_ok=True)
        QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
    print(f"16H QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(0 if not failures else 1)


for path in (CONTRACT, SPEC, RESEARCH, ASUX_SPEC, FACTS, PLANNER_FACTS, EVIDENCE_FACTS, WEAKNESS_FACTS, PROGRESS_FACTS, RUN, FAST, BLUEPRINT,
             PROGRESS_QUERY, HISTORY, SETTINGS, PRESENTATION, PROGRESS_PRESENTATION, HISTORY_PRESENTATION, SCREEN, TOPIC_SCREEN, APPLICATION,
             ACTIVITY, FLOW, SESSION, MODEL_TEST, PRESENTATION_TEST, PROGRESS_TEST, COURSE_TEST):
    check(f"A16H-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
facts = strip_comments(read(FACTS))
run_src = strip_comments(read(RUN))
fast = strip_comments(read(FAST))
presentation = strip_comments(read(PRESENTATION))
screen = strip_comments(read(SCREEN))
topic_screen = strip_comments(read(TOPIC_SCREEN))
activity = read(ACTIVITY)
flow = strip_comments(read(FLOW))
application = strip_comments(read(APPLICATION))

# ---------------------------------------------------------------- contract and its source
check("A16H-01_model", contract.get("model") == "ASRX-v0" and contract.get("stage_step") == "16H" and contract.get("decision") == "D-132", "")
ud = contract.get("user_decisions") or {}
check("A16H-01_user_decisions", ud.get("approval") == "explicit_user_approval_2026-10-26"
      and ud.get("scope", "").startswith("everything_in_moved_to_16h")
      and ud.get("blueprint_composition", "").startswith("automatic_when_the_learner_opens_the_app")
      and ud.get("contest", "").startswith("yes_a_reported_answer_is_held_as_contested")
      and ud.get("fast_path_replan", "").startswith("immediately_a_new_replan_trigger_diagnostic_requested")
      and ud.get("fast_path_entry", "").startswith("on_the_topic_detail"), str(ud))
scope = contract.get("scope") or {}
check("A16H-01_scope", all(scope.get(k) is False for k in ("schema_changed", "ports_changed", "boundaries_changed", "planner_rule_changed",
      "mastery_rule_changed", "evidence_rule_changed", "entry_placement_diagnostic_source", "learn_tab_screen"))
      and all(scope.get(k) is True for k in ("replan_trigger_added", "sessions_from_today", "slot_session_ids", "fast_path_entry",
      "contest_from_the_app", "disposition_reason_in_traces", "assessment_opportunity", "screen_drawn")) and scope.get("schema_version") == 11, "")
asux = read(ASUX_SPEC)
check("A16H-01_asux_is_the_source", all(x in asux for x in ("skip", "contested", "not_reliably_measured")), "")
m = contract.get("measurement") or {}
check("A16H-01_measurement_recorded", m.get("kinds") == ["daily_assessment", "weekly_slot", "monthly_slot", "diagnostic"] and m.get("interior") == "one"
      and m.get("kind_adds_weight") is False and m.get("entry_writes_truth") is False and m.get("score_or_grade") is False
      and (m.get("skip") or {}).get("incorrect") is False and (m.get("contest") or {}).get("edits_the_row") is False
      and (m.get("contest") or {}).get("held_against_the_learner") is False, "")

# ---------------------------------------------------------------- 1. the measurement model
check("A16H-02_four_kinds_one_interior", all(f'{k}("{v}", AssessmentScope.{s})' in facts for k, v, s in (
      ("DAILY_ASSESSMENT", "daily_assessment", "DAILY_MICRO"), ("WEEKLY_SLOT", "weekly_slot", "WEEKLY_BLUEPRINT"),
      ("MONTHLY_SLOT", "monthly_slot", "MONTHLY_CAPABILITY"), ("DIAGNOSTIC", "diagnostic", "DAILY_MICRO"))), "")
check("A16H-02_measurement_invariants", 'require(kind.isSlot == (slotId != null))' in facts
      and 'require(kind == MeasurementKind.DAILY_ASSESSMENT || sessionId != null)' in facts
      and 'require(task != null || (measurement != null && measurement.kind != MeasurementKind.DAILY_ASSESSMENT))' in facts
      and 'require(measurement == null || lesson.isEmpty())' in facts, "a measurement teaches nothing first, a slot names its slot")

# ---------------------------------------------------------------- 2. entry confirms, guesses nothing, writes nothing
open_ = body(run_src, "class OpenTask(")
slot = body(open_, "private fun openSlot(")
check("A16H-03_slot_this_cycle_only", "it.cycleId == BlueprintScopes.cycleOf(scope, clock.now().studyDay)" in slot
      and "it.targetSkill == entry.primarySkill" in slot and "persistence.resourceVersion(it) != null" in slot, "an earlier cycle's slot is not debt")
check("A16H-03_slot_gate_reads_the_item", "PrerequisiteCandidate(entry.candidateId, entry.primarySkill, slot.itemRequiredSkills)" in slot, "")
diag = body(open_, "private fun openDiagnostic(")
check("A16H-03_check_of_the_open_diagnostic_only", "DiagnosticSessions.active(persistence)" in diag and "it.sessionId == sessionId" in diag
      and "objective in it.targetObjectives" in diag and "AssessmentIntent.MASTERY_EVIDENCE" in diag, "")
measured = body(open_, "private fun measured(")
check("A16H-03_judgeable_usable_unseen", "RunItems.kind(item, content) == null || !server.usable(item.ref, measurement.intent, evaluatorAvailable)" in measured
      and "seen(item.ref) -> CapabilityGap.NO_UNSEEN_ITEM" in measured and "lesson = emptyList()" in measured and "task = null" in measured, "")
check("A16H-03_entry_writes_nothing", not re.search(r"appendTruth|inTransaction|\.build\(|\.replan\(|offer\(", open_), "")
check("A16H-03_assess_is_a_daily_assessment", "Measurement(MeasurementKind.DAILY_ASSESSMENT, intentOf(task), sessionId = null)" in open_
      and "if (task.purpose == TaskPurpose.ASSESS) AssessmentIntent.VERIFICATION else INTENT" in open_
      and "lesson = if (task.purpose == TaskPurpose.ASSESS) emptyList()" in open_
      and "CapabilityGap.ASSESSMENT_INTERIOR_NOT_AVAILABLE" not in open_, "")

# ---------------------------------------------------------------- 3. inside the session
runtask = body(run_src, "class RunTask(")
check("A16H-04_serves_for_its_intent", "server.offer(ref, run.measurement?.intent ?: OpenTask.INTENT, evaluatorAvailable)" in runtask, "")
begin = body(runtask, "fun beginSession(")
check("A16H-04_daily_session_at_begin", 'it.kind == MeasurementKind.DAILY_ASSESSMENT && it.sessionId == null' in begin
      and 'TruthRecord("assessment_session", clock.now(), mapOf("scope" to AssessmentScope.DAILY_MICRO.storedAs))' in begin, "")
check("A16H-04_every_attempt_carries_its_session", "assessmentSessionId = run.measurement?.sessionId)" in body(runtask, "private fun submission(")
      and run_src.count("submission(run, item, body, provenance, assistance)") == 3, "key, tests and rubric alike")
evidence = body(runtask, "private fun evidence(")
check("A16H-04_slot_evidence_is_the_slot_s", "if (measurement != null && measurement.kind.isSlot)" in evidence and "RecordSlotEvidence(persistence, clock).record(" in evidence
      and "sessionSoFar = SlotSessions.outcomes(persistence, blueprint, measurement.sessionId!!)" in evidence, "")
outcomes = body(run_src, "fun outcomes(")
check("A16H-04_session_read_from_attempts", "it.assessmentSessionId == sessionId && it.resource == slot.item" in outcomes, "never kept in memory")
finish_ = body(runtask, "fun finish(")
_retro = finish_.find("ApplyRetroactiveContamination(persistence, clock).apply(")
check("A16H-04_retroactive_before_recompute", 0 <= _retro < finish_.find("ReportProgramChanges(persistence"), "present, and before the recompute")
contest = body(runtask, "fun contest(")
check("A16H-04_contest_appends_never_edits", 'require(run.measurement != null)' in contest
      and "disposition.record(it, EvidenceDispositions.CONTESTED, CONTESTED_REASON, DECIDED_BY_LEARNER)" in contest
      and not re.search(r"UPDATE|DELETE|writeProjection", contest)
      and 'const val DECIDED_BY_LEARNER = "user_report"' in run_src, "")
check("A16H-04_skip_writes_nothing", "fun skip(next: () -> Unit)" in flow and "app." not in body(flow, "fun skip("), "")
check("A16H-04_contest_only_on_evidence", "run.run.measurement != null && answered.evidenceIds.isNotEmpty() && answered.item !in run.contested" in flow, "")

# ---------------------------------------------------------------- 4. dispositions read back, named in traces
weak = strip_comments(read(WEAKNESS_FACTS))
check("A16H-05_row_carries_its_correction", "if (disposition == null || disposition == REINSTATED) it else it.copy(disposition = disposition, dispositionReason = reason)" in weak
      and "val disposition: String? = null," in read(EVIDENCE_FACTS), "a reinstated row is the row again")
check("A16H-05_history_names_it", "disposition = step.row.disposition," in read(HISTORY)
      and "entry.disposition?.let { add(HistoryCopy.disposition(it, entry.dispositionReason)) }" in read(HISTORY_PRESENTATION), "")

# ---------------------------------------------------------------- 5. the result
res = body(presentation, "fun result(")
check("A16H-06_boundaries_honest", all(x in res for x in ("ref in contested -> BoundaryStatus.CONTESTED", "answered.any { it.item == ref } -> BoundaryStatus.FROZEN",
      "ref in skipped -> BoundaryStatus.UNSUBMITTED", "else -> BoundaryStatus.OPEN")) and "SessionResults.of(view, unreliable = unreliable)" in res, "")
check("A16H-06_not_measured_named", all(x in res for x in ("NotReliablyMeasured.PROVISIONAL_EVALUATION", "NotReliablyMeasured.INVALID_ITEM",
      "NotReliablyMeasured.ASSISTED_ATTEMPT", "NotReliablyMeasured.SOLUTION_EXPOSED_ATTEMPT")), "")
copy = strings(body(presentation, "object MeasurementCopy"))
forbidden = ["puan", "%", "seviye", "ustalaştın", "mastered", "geçtin", "kaldın", "başarısız oldun", "not:", "seri"]
check("A16H-06_no_verdict_words", copy and not [w for w in forbidden if any(w in c.lower() for c in copy)], str([w for w in forbidden if any(w in c.lower() for c in copy)]))
check("A16H-06_wired_result", "MeasurementPresentation.result(measurement.scope, items.map { it.ref }, answered, skipped, contested)" in application, "")

# ---------------------------------------------------------------- 6. the fast path
req = body(fast, "fun request(")
check("A16H-07_only_startable_skills", "skills.filter { persistence.skill(it)?.lifecycleStatus == PUBLISHED }" in req
      and "DiagnosticSource.USER_REQUESTED_FAST_PATH" in req, "the learner opens it; the rest are left out, not refusing all")
check("A16H-07_replans_keeping_started", "ReplanTrigger.DIAGNOSTIC_REQUESTED, capacity, keptPositions = StartedTasks.today(persistence, clock)" in req
      and "if (request !is RequestDiagnostic.Requested.Written) return Requested(request, null)" in req, "nothing planned when nothing was requested")
planner_facts = strip_comments(read(PLANNER_FACTS))
check("A16H-07_declared_trigger", 'DIAGNOSTIC_REQUESTED("diagnostic_requested", DiagnosticCodes.USER_REQUESTED_FAST_PATH)' in planner_facts
      and any(e.get("what") == "ReplanTrigger.DIAGNOSTIC_REQUESTED" for e in contract.get("declared_extensions") or []), "")
check("A16H-07_withdraw_owes_nothing", "WithdrawDiagnostic(persistence, clock).withdraw()" in fast and "replan" not in body(fast, "fun withdraw("), "")
check("A16H-07_on_the_topic", "fastPath: FastPathView? = null" in topic_screen and "onRequestFastPath = { app.requestFastPath(topicSkills)" in activity
      and "onWithdrawFastPath = { app.withdrawFastPath" in activity, "")

# ---------------------------------------------------------------- 7. assessment_opportunity
opp = body(strip_comments(read(BLUEPRINT)), "internal object AssessmentOpportunities")
check("A16H-08_opportunity_is_a_session_fact", "blueprint.cycleId != BlueprintScopes.cycleOf(scope, studyDay)" in opp
      and "ready.filterNot { it.item in served }" in opp and "?.state?.open == true" in opp, "this cycle's unserved slots and open checks")
check("A16H-08_opportunity_shown", "row.skill in facts.assessmentOpportunities" in read(PROGRESS_PRESENTATION)
      and "assessmentOpportunities = clock?.let { AssessmentOpportunities.of(persistence, it.now().studyDay) }.orEmpty()" in read(PROGRESS_QUERY)
      and "ProgressQuery(opened.graph.persistence, opened.graph.content, opened.graph.clock).load()" in application, "")

# ---------------------------------------------------------------- 8. the screen
check("A16H-09_orientation_before_work", "view.measurement.forEach" in screen
      and "listOf(MeasurementCopy.scope(it.kind), SessionCopy.INDEPENDENCE_DISCLOSURE, MeasurementCopy.NOT_A_VERDICT)" in presentation, "")
check("A16H-09_skip_and_report_drawn", "if (stage.canSkip) TextButton(onClick = actions.onSkip" in screen and "if (stage.canContest) TextButton(onClick = actions.onContest" in screen, "")
check("A16H-09_screen_decides_nothing", "coach.application" not in screen + topic_screen and "Tone." not in screen
      and not re.search(r"ProgressIndicator|Slider|\d+\s*%", screen + topic_screen), "")
check("A16H-09_session_opened_on_begin", "app.beginRun(run.run) { begun ->" in activity and "run.run = begun" in activity, "")
check("A16H-09_exit_still_first", screen.find("OutlinedButton(onClick = onExit") < screen.find("StateChip(") < screen.find("stage?.let { TaskRunInterior(it, actions) }"), "")

# ---------------------------------------------------------------- 9. what moved on, not claimed
moved = contract.get("moved_on") or []
check("A16H-10_moved_on_named", len(moved) >= 5 and all(isinstance(x, dict) and x.get("owner") for x in moved)
      and any("entry placement" in x.get("what", "") and x.get("owner") == "18A" for x in moved), "")
check("A16H-10_stage_16_complete", "**16H — Uçtan uca günlük döngü: ölçme oturumları**" in read(EXECUTION_INDEX)
      and "16H — Uçtan uca günlük döngü: ölçme oturumları" in read(MASTER_PLAN), "")

# ---------------------------------------------------------------- tests name what they guard
for path, names in ((MODEL_TEST, ["a measurement names its session, a slot its slot, and only a slot or a diagnostic runs without an authored task"]),
                    (PRESENTATION_TEST, ["orientation names the session and what independence means, before any work",
                                         "a skipped question leaves the session partial and is not incorrect, and a reported one is not measured",
                                         "the result says its state, what could not be measured and what the engines reported, and nothing else"]),
                    (PROGRESS_TEST, ["a Skill an assessment session could measure now is an assessment opportunity, and nothing else is one"]),
                    (COURSE_TEST, ["a planned blueprint slot opens as a measurement of its session, and its answer is that session's slot evidence",
                                   "a daily assessment opens its own session as the learner begins, and a reported answer is held as contested",
                                   "the fast path joins today's plan at once, keeps started work, opens its checks in its own session, and leaving it owes nothing"])):
    text = read(path)
    for name in names:
        check(f"A16H-11_test_{re.sub(r'[^a-z0-9]+', '_', name[:40].lower()).strip('_')}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- narrowed gates
gates = (contract.get("narrowed_gates") or {}).get("gates") or []
check("A16H-12_narrowed_gates_marked", len(gates) >= 3 and all(
    read(ROOT / g.get("file", "")).count("Narrowed at 16H (`ASRX-v0") >= max(1, len(g.get("checks") or [])) for g in gates),
    "one marker per narrowed check: " + str([g.get("file") for g in gates]))

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results_kotlin") if isinstance(contract.get("mutation_results_kotlin"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("A16H-13_kotlin_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("A16H-13_kotlin_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("A16H-13_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm.get("total")))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("A16H-13_runs_pass", len(runs) >= 6 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") or {}
check("A16H-13_device_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("run_seen_on_device") is False, "")
claims = "\n".join(line for line in read(CONTRACT).splitlines() if not line.lstrip().startswith("#"))
check("A16H-13_no_claim_words", not re.search(r"\bsurvived\b(?!_as_expected)|\b100%|streak|banana", claims.split("validator_mutation:")[0]), "")

# ---------------------------------------------------------------- decisions, spec, research
decisions = read(DECISIONS)
check("A16H-14_decision", "## D-132 — Today'den ölçme oturumları = ASRX-v0" in decisions, "")
spec = read(SPEC)
check("A16H-14_spec_accepted", "**Status:** ACCEPTED — independent 16H QA PASS" in spec and "`ASRX-v0" in spec, "")
check("A16H-14_spec_next_is_17a", "Next numbered step: `17A — Görsel polish`" in spec and "AŞAMA 16 is complete with 16H" in spec, "")
research = read(RESEARCH)
check("A16H-14_research_sources", all(x in research for x in ("ASUX-v0", "WBAX-v0", "VDWX-v0", "WLRX-v0", "PDT-v0", "Black & Wiliam")), "")

finish()
