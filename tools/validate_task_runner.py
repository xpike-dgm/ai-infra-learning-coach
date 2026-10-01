"""Independent 11B QA — RNRX-v0 Task Runner.

The implementation is validated against the accepted contracts, not against its own. The seventeen
states, six phases, entry and resume conditions, pause classes, assistance order and provenance
values are read out of `TRUX-v0`'s `flow.yaml`; the assistance and provenance value sets out of
`DDM-v0` **and** out of the schema's own CHECK lists, so the runner cannot produce a value the store
would refuse; the tones out of `VDSX-v0`; the port set out of `MSBX-v0` — each compared with the
actual Kotlin source.

It also guards the repository against a defect this step found on main: a combining dot (U+0307)
that a Python `"İ".lower()` smuggled into Turkish words in the living documents.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/11b_task_runner/task_runner.yaml"
SPEC = ROOT / "docs/TASK_RUNNER_SPEC.md"
RESEARCH = ROOT / "research/11b_task_runner_research.md"
QA_OUT = ROOT / "arch/11b_task_runner/qa_report.yaml"

FLOW = ROOT / "ux/8c_daily_working_flow/flow.yaml"
VDSX = ROOT / "ux/8f_design_system/design_system.yaml"
DDM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

RUNNER_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TaskRunner.kt"
FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/AttemptFacts.kt"
SUBMIT_KT = ANDROID / "core-application/src/main/kotlin/coach/application/SubmitAttempt.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
ADAPTER_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCREEN_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/TaskRunnerScreen.kt"
ACTIVITY_KT = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"

RUNNER_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/TaskRunnerTest.kt"
SUBMIT_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/SubmitAttemptTest.kt"
T2_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/TransactionAndMigrationTest.kt"

def declared_port_extensions(root):
    """Ports added after MSBX-v0 by an accepted later contract (14A, D-105), never by an unrecorded edit."""
    import yaml as _yaml
    path = root / "arch/14a_tutor_contract/tutor_contract.yaml"
    tutor = (_yaml.safe_load(path.read_text(encoding="utf-8")) or {}) if path.is_file() else {}
    ext = tutor.get("port_extension") or {}
    return [ext["port"]] if tutor.get("status") == "accepted_14a" and ext.get("decision") == "D-105" else []


results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"//[^\n]*", "", source)


def enum_ids(source: str, name: str) -> list[str]:
    match = re.search(rf"enum class {name}\b[^{{]*\{{(.*?)\n\}}", source, re.S)
    return re.findall(r'\(\s*"([A-Za-z0-9_]+)"', match.group(1)) if match else []


def kotlin_list(source: str, name: str) -> list[str]:
    match = re.search(rf"val {name}\s*=\s*\n?\s*listOf\((.*?)\)", source, re.S)
    return re.findall(r'"([^"]+)"', match.group(1)) if match else []


def ordered(source: str, *needles: str) -> bool:
    positions = [source.find(n) for n in needles]
    return all(p >= 0 for p in positions) and positions == sorted(positions)


contract = load(CONTRACT)
flow = load(FLOW)
vdsx = load(VDSX)
ddm = load(DDM)
msbx = load(MSBX)

for path in (RUNNER_KT, FACTS_KT, SUBMIT_KT, SCREEN_KT, RUNNER_TEST, SUBMIT_TEST, SPEC, RESEARCH):
    check(f"E11B-00_exists_{path.name}", path.is_file(), f"missing {path}")

runner = strip_comments(read(RUNNER_KT))
facts = strip_comments(read(FACTS_KT))
submit = strip_comments(read(SUBMIT_KT))
schema = read(SCHEMA_KT)
adapter = strip_comments(read(ADAPTER_KT))
ports = strip_comments(read(PORTS_KT))
screen = read(SCREEN_KT)
screen_code = strip_comments(screen)
activity = strip_comments(read(ACTIVITY_KT))
runner_test = read(RUNNER_TEST)
submit_test = read(SUBMIT_TEST)
t2_test = read(T2_TEST)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E11B-01_model", contract.get("model") == "RNRX-v0", str(contract.get("model")))
check("E11B-01_status", contract.get("status") == "accepted_11b", str(contract.get("status")))
check("E11B-01_decision", contract.get("decision") == "D-088", str(contract.get("decision")))
for key in ("evidence_written", "evidence_pipeline_implemented", "planner_implemented", "surface_semantics_changed",
            "boundaries_changed", "ports_added", "schema_changed"):
    check(f"E11B-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")

# ---------------------------------------------------------------- vocabularies from TRUX-v0
check("E11B-02_states", enum_ids(runner, "RunnerState") == flow["semantic_states"],
      f"kotlin={enum_ids(runner, 'RunnerState')}")
check("E11B-02_phases", enum_ids(runner, "RunnerPhase") == [p["id"] for p in flow["task_run_lifecycle"]["phases"]],
      f"kotlin={enum_ids(runner, 'RunnerPhase')}")
check("E11B-02_entry_conditions", enum_ids(runner, "EntryCondition") == flow["entry_revalidation"]["conditions"],
      f"kotlin={enum_ids(runner, 'EntryCondition')}")
check("E11B-02_resume_conditions", enum_ids(runner, "ResumeCondition") == flow["resume_revalidation"]["conditions"],
      f"kotlin={enum_ids(runner, 'ResumeCondition')}")
check("E11B-02_pause_classes",
      enum_ids(runner, "PauseClass") == [c["id"] for c in flow["checkpoint_model"]["pause_classes"]],
      f"kotlin={enum_ids(runner, 'PauseClass')}")
check("E11B-02_next_task_source",
      enum_ids(runner, "NextTaskSource") == [flow["transition_phase"]["next_task_source"]]
      and flow["transition_phase"]["cached_local_list_advance"] is False,
      f"kotlin={enum_ids(runner, 'NextTaskSource')}")

# ---------------------------------------------------------------- value sets: TRUX == DDM == schema CHECK == Kotlin
ddm_assist = ddm["assistance_event"]
for kotlin_enum, ddm_key, schema_list, trux_key in [
    ("AssistanceLevel", "level_values", "assistanceLevelValues", "assistance_levels"),
    ("AssistanceTiming", "timing_values", "assistanceTimingValues", "assistance_timings"),
    ("AssistanceScope", "target_scope_values", "assistanceScopeValues", "target_scopes"),
    ("AssistanceSource", "source_values", "assistanceSourceValues", None),
]:
    kotlin_values = enum_ids(facts, kotlin_enum)
    check(f"E11B-03_{kotlin_enum}_ddm", kotlin_values == ddm_assist[ddm_key], f"kotlin={kotlin_values} ddm={ddm_assist[ddm_key]}")
    check(f"E11B-03_{kotlin_enum}_schema", kotlin_values == kotlin_list(schema, schema_list),
          f"kotlin={kotlin_values} schema={kotlin_list(schema, schema_list)}")
    if trux_key:
        check(f"E11B-03_{kotlin_enum}_trux", kotlin_values == flow["assistance_choreography"][trux_key],
              f"trux={flow['assistance_choreography'][trux_key]}")
origins = enum_ids(facts, "ProvenanceOrigin")
check("E11B-03_provenance_ddm", origins == ddm["artifact_provenance"]["origin_values"], f"kotlin={origins}")
check("E11B-03_provenance_schema", origins == kotlin_list(schema, "provenanceOriginValues"), f"kotlin={origins}")
check("E11B-03_provenance_trux", origins == flow["artifact_provenance"]["origin_values"], f"kotlin={origins}")
check("E11B-03_provenance_asked", flow["artifact_provenance"]["asked_not_inferred"] is True
      and "unknown_provenance" in origins, "an honest unknown must be recordable")

# ---------------------------------------------------------------- tones from VDSX-v0
tones = vdsx["surface_state_tones"]
tone_block = re.search(r"val RunnerState\.tone: Tone(.*?)\n    \}", runner, re.S)
tone_source = tone_block.group(1) if tone_block else ""
for state in flow["semantic_states"]:
    expected = tones.get(state, "").upper()
    name = state.upper()
    # The arm naming this state ends in its tone; for a multi-line arm that is the next arrow.
    full_arm = tone_source[tone_source.find(f"RunnerState.{name}"):]
    tone_found = re.search(r"->\s*Tone\.(\w+)", full_arm)
    check(f"E11B-04_tone_{state}", tone_found is not None and tone_found.group(1) == expected,
          f"kotlin={tone_found.group(1) if tone_found else None} vdsx={expected}")
check("E11B-04_ui_does_not_choose_tones", "Tone." not in screen_code and "toneOf" not in screen_code,
      "tones are decided in core-presentation, never in app-ui")

# ---------------------------------------------------------------- assistance choreography
ac = flow["assistance_choreography"]
check("E11B-05_upstream_rules",
      ac["always_requestable_in_teaching_practice"] is True and ac["jump_to_h4_without_request"] is False
      and ac["auto_reveal_on_first_error"] is False and ac["consequence_disclosed_before_h3_h4"] is True,
      str({k: ac[k] for k in ("jump_to_h4_without_request", "auto_reveal_on_first_error")}))
check("E11B-05_h3_h4_reveal", "val revealsTargetReasoning: Boolean get() = this == H3 || this == H4" in facts,
      "H3 and H4 are the levels that reveal target reasoning")
check("E11B-05_disclosure_unconditional_on_scope",
      "if (level.revealsTargetReasoning && !consequenceAcknowledged) {" in runner,
      "TRUX-v0 states the disclosure rule without a scope condition")
check("E11B-05_grants_are_requested", "requestedByUser = true" in runner and "requestedByUser = false" not in runner,
      "the runner never grants help that was not requested")
check("E11B-05_no_auto_reveal",
      "fun afterIncorrectAttempt(): List<AssistanceLevel> = AssistanceLevel.entries" in runner,
      "an incorrect attempt only makes every level requestable")
check("E11B-05_recheck_derived",
      re.search(r"val requiresIndependentRecheck: Boolean\s*\n\s*get\(\)", facts) is not None
      and re.search(r"val highestAssistanceLevel: AssistanceLevel\?\s*\n\s*get\(\)", facts) is not None,
      "derived facts are computed, not stored")
check("E11B-05_runner_does_not_schedule_recheck",
      ac["after_exposure"]["runner_schedules_recheck"] is False and "schedule" not in runner.lower(),
      "the runner never schedules the recheck")

# ---------------------------------------------------------------- entry, stop, exit, evaluation
check("E11B-06_entry_needs_confirmation",
      "val unmet = EntryCondition.entries.filterNot { it in confirmed }" in runner,
      "every entry condition must be confirmed")
check("E11B-06_unmet_not_failed", "data class NotStartable(val unmet: Set<String>)" in runner,
      "an unconfirmed condition is named unmet, not failed")
check("E11B-06_today_confirms_only_selection",
      "if (view.primaryTask != null) setOf(EntryCondition.PLANNED_TASK_STILL_SELECTED) else emptySet()" in runner,
      "nothing Today cannot confirm may be assumed")
check("E11B-06_upstream_entry",
      flow["entry_revalidation"]["failure_is_negative_evidence"] is False
      and flow["entry_revalidation"]["failure_starts_flow"] is False, "TRUX-v0 entry rules")
check("E11B-06_stop_no_penalty", "fun stop(): RunnerState = RunnerState.STOPPED_NO_PENALTY" in runner
      and flow["abandon_and_stop"]["creates_debt"] is False, "stopping costs nothing")
check("E11B-06_exit_everywhere", "fun exitAvailable(state: RunnerState): Boolean = true" in runner
      and flow["abandon_and_stop"]["exit_always_available"] is True, "the exit is always available")
mid = next(c for c in flow["checkpoint_model"]["pause_classes"] if c["id"] == "mid_segment_pause")
check("E11B-06_mid_segment_not_saved",
      mid["durable"] is False and 'MID_SEGMENT_PAUSE("mid_segment_pause", durable = false, producesResumeContext = false)' in runner,
      "a mid-segment pause is not durable and is never presented as saved")
check("E11B-06_pending_own_state",
      "is EvaluationResult.EvaluationPending -> RunnerState.EVALUATION_PENDING" in runner
      and flow["degraded_and_recovery"]["ai_unavailable"]["auto_pass"] is False
      and flow["degraded_and_recovery"]["ai_unavailable"]["auto_fail"] is False,
      "a pending evaluation is neither a pass nor a fail")
check("E11B-06_focused_flow", "val surface: Surface = Surface.TaskRunnerFlow" in runner,
      "the runner is the task_runner_flow focused flow")
punitive = re.compile(r"ceza|cezalan|kaybed|başarısız|puan", re.I)
disclosure = re.search(r'const val CONSEQUENCE_DISCLOSURE =\s*(.*?)\n\n', read(RUNNER_KT), re.S)
check("E11B-06_disclosure_is_measurement",
      disclosure is not None and not punitive.search(disclosure.group(1))
      and ac["consequence_framing_allowed"] == "changes_what_this_attempt_can_prove",
      "the consequence is framed as measurement")

# ---------------------------------------------------------------- the submission (LFPS-v0 one action one transaction)
check("E11B-07_one_transaction", "persistence.inTransaction {" in submit and submit.count("inTransaction") == 1,
      "one learner action is one transaction")
check("E11B-07_rows_in_order",
      ordered(submit, '"attempt", at', '"artifact", at', '"artifact_provenance", at', '"assistance_event", at'),
      "attempt, artifact, provenance, assistance")
check("E11B-07_no_evidence", "evidence" not in submit.replace("EvaluationPending", ""),
      "the runner writes no evidence")
check("E11B-07_one_timestamp", submit.count("clock.now()") == 1, "one timestamp per action")
check("E11B-07_derived_not_stored", not re.search(r'"(requires_independent_recheck|highest_assistance_level)"', submit),
      "a derived fact is not stored beside its source")
check("E11B-07_provenance_required", "val provenance: ProvenanceOrigin," in facts,
      "an attempt cannot be built without the learner's provenance answer")
check("E11B-07_upstream_no_evidence_without_attempt",
      "evidence_written_without_attempt" in flow["forbidden_flow_patterns"]
      and flow["invariants"]["runner_is_evidence_evaluator"] is False,
      "TRUX-v0: the runner is not the evidence evaluator")

# ---------------------------------------------------------------- port refinement
check("E11B-08_ports_unchanged", sorted(re.findall(r"^interface (\w+Port)\b", ports, re.M)) == sorted([p["id"] for p in msbx["ports"]["set"]] + declared_port_extensions(ROOT))
      and len(msbx["ports"]["set"]) == 4, "no undeclared port")
check("E11B-08_append_returns_id", "fun appendTruth(record: TruthRecord): Long" in ports
      and "last_insert_rowid()" in adapter, "appendTruth returns the row id")

# ---------------------------------------------------------------- the screen and the wiring
check("E11B-09_exit_first", ordered(screen_code, "OutlinedButton(onClick = onExit", "StateChip("),
      "the exit comes before everything else")
check("E11B-09_touch_target", "minimumTouchTarget()" in screen, "48dp floor")
check("E11B-09_announced", "liveRegion = LiveRegionMode.Polite" in screen, "state changes are announced")
for state in flow["semantic_states"]:
    check(f"E11B-09_label_{state}", f"RunnerState.{state.upper()} to" in screen, f"no label for {state}")
check("E11B-09_no_locale_naive_case", not re.search(r"\.(uppercase|lowercase|capitalize)\(\s*\)", screen),
      "no locale-naive case transform")
check("E11B-10_start_revalidates_in_core",
      "RunnerRevalidation.atEntry(RunnerRevalidation.confirmedFromToday(" in activity,
      "Today's start is revalidated in core")
check("E11B-10_runner_suspends_shell", "RunnerFlow.surface" in activity, "the runner is a focused flow in front of the shell")

# ---------------------------------------------------------------- the checks exist
for name in [
    "the seventeen semantic states are TRUX-v0's, in its order",
    "any unmet entry condition blocks the start and names what changed",
    "nothing Today cannot confirm is assumed to hold at entry",
    "H3 and H4 are never granted before the consequence is disclosed",
    "an incorrect attempt reveals nothing on its own and leaves every level requestable",
    "stopping costs nothing and is always the same state",
    "a pending evaluation is its own state, never a pass or a fail",
    "every runner state wears the tone VDSX-v0 assigned it",
]:
    check(f"E11B-11_test_{name[:44]}", f"`{name}`" in runner_test, f"missing test: {name}")
for name in [
    "an attempt is recorded as exactly one transaction",
    "the runner writes no evidence",
    "revealed target reasoning raises the recheck flag, derived rather than stored",
]:
    check(f"E11B-11_test_{name[:44]}", f"`{name}`" in submit_test, f"missing test: {name}")
for name in [
    "an attempt with its artifact provenance and assistance commits as one action",
    "an attempt never survives without its assistance metadata or provenance",
]:
    check(f"E11B-11_t2_{name[:44]}", f"`{name}`" in t2_test, f"missing T2 test: {name}")

# ---------------------------------------------------------------- no combining dot anywhere in the repo's text
# Built from its code point so this file never contains the character it hunts for.
COMBINING_DOT_ABOVE = chr(0x0307)
contaminated = []
for path in ROOT.rglob("*"):
    if not path.is_file() or {".git", "build", ".gradle", ".kotlin"} & set(path.parts):
        continue
    if path.suffix not in {".md", ".yaml", ".yml", ".kt", ".py", ".kts"}:
        continue
    try:
        if COMBINING_DOT_ABOVE in path.read_text(encoding="utf-8"):
            contaminated.append(str(path.relative_to(ROOT)))
    except UnicodeDecodeError:
        continue
check("E11B-12_no_combining_dot", not contaminated,
      f"U+0307 found in {contaminated} — a Python '\\u0130'.lower() produces it; Turkish needs the dotless \\u0131")

# A sync script that crashes and is re-run in pieces leaves adjacent duplicate lines behind; 11A's
# did, on main, and 11B's nearly did again. Living documents must not contain one.
LIVING = ["AGENTS.md", "PROJECT_CONTEXT.md", "docs/START_HERE.md", "docs/HANDOFF_STATE.md", "docs/STEP_STATUS.md",
          "docs/EXECUTION_INDEX.md", "docs/MASTER_PLAN.md", "docs/DECISIONS.md", "docs/PROGRESS_LOG.md",
          "docs/LOCAL_MANAGER_HANDOFF.md", "vault/agent/CURRENT_CONTEXT.md", "vault/agent/OPEN_LOOPS.md"]
adjacent = []
for rel in LIVING:
    lines = read(ROOT / rel).splitlines()
    adjacent += [f"{rel}:{n + 1}" for n in range(1, len(lines))
                 if len(lines[n].strip()) > 25 and lines[n] == lines[n - 1]]
check("E11B-12_no_adjacent_duplicate_lines", not adjacent, f"adjacent duplicates at {adjacent}")

# ---------------------------------------------------------------- honesty
device = contract["device_verification"]
check("E11B-13_device_not_claimed", device["t6_run"] is False and device["claimed"] is False, "no device result claimed")
mutation = contract["mutation_results"]
check("E11B-13_mutation_all_detected", mutation["detected"] == mutation["total"] >= 16,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E11B-13_not_stored_recorded", len(contract.get("not_stored_with_owner", [])) >= 4,
      "fields the runner does not store must be named with owners")
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E11B-13_runs_pass", len(runs) >= 5 and all(r["result"] == "PASS" for r in runs.values()), str(runs.keys()))
owners = {str(k) for k in contract.get("future_stage_boundaries", {})}
for owner in ("11C", "11D", "12", "13"):
    check(f"E11B-13_boundary_{owner}", owner in owners, f"missing {owner}")
forbidden = set(contract.get("forbidden_runner_patterns", []))
for pattern in ["starting_a_task_on_an_unconfirmed_condition", "granting_h3_or_h4_before_the_consequence_is_disclosed",
                "narrowing_an_accepted_disclosure_rule", "writing_evidence_from_the_runner",
                "storing_a_derived_fact_beside_its_source", "inventing_attempt_fields_the_data_model_does_not_name",
                "computing_runner_tones_in_the_ui"]:
    check(f"E11B-14_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 11B QA PASS", "**Decision:** `D-088`",
                 "evaluation_pending", "unmet", "one transaction"]:
    check(f"E11B-15_spec_{fragment[:26]}", fragment in spec_text, f"missing={fragment!r}")
for fragment in ["U+0307", "planned_task_ref", "runner_completion_state"]:
    check(f"E11B-16_research_{fragment}", fragment in research_text, f"missing={fragment!r}")

acceptance = contract.get("acceptance", {})
for key in ("independent_qa_required", "build_must_be_run_not_asserted", "atomicity_proven_on_real_storage"):
    check(f"E11B-17_{key}", acceptance.get(key) is True, f"{key}={acceptance.get(key)}")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "RNRX-v0", "stage_step": "11B", "decision": "D-088",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"11B_TASK_RUNNER_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
