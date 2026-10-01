"""Independent 11E QA — EODX-v0 End of Day.

The implementation is validated against the accepted contracts, not against its own. The
presentation states and counting rules are read out of `SPWX-v0`'s `progress.yaml`, the history
rules out of its `learning_history` section, the tones out of `VDSX-v0`, the surface set out of
`NSHX-v0`'s `ia.yaml`, the time model out of `DDM-v0` — each compared with the actual Kotlin.

The checks that matter most are structural: a streak, a percentage, a daily goal and a carried debt
must be **unrepresentable**, not merely absent from this version of the code.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/11e_end_of_day/end_of_day.yaml"
SPEC = ROOT / "docs/END_OF_DAY_SPEC.md"
RESEARCH = ROOT / "research/11e_end_of_day_research.md"
QA_OUT = ROOT / "arch/11e_end_of_day/qa_report.yaml"

PROGRESS = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
VDSX = ROOT / "ux/8f_design_system/design_system.yaml"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
DDM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

DAY_KT = ANDROID / "core-model/src/main/kotlin/coach/DayFacts.kt"
EOD_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/EndOfDay.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/DayCloseFacts.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
ADAPTER_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
VIEW_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/EndOfDayView.kt"
TODAY_SCREEN_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/TodayScreen.kt"
APPLICATION_KT = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
SURFACES_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/Navigation.kt"

DAY_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/DayFactsTest.kt"
EOD_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/EndOfDayTest.kt"
APP_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/DayCloseFactsTest.kt"
T2_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/DayCountingTest.kt"

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


def body(source: str, signature: str) -> str:
    start = source.find(signature)
    if start < 0:
        return ""
    brace = source.find("{", start + len(signature))
    equals = source.find("=", start + len(signature))
    if brace < 0 or (0 <= equals < brace):
        end = source.find("\n\n", start)
        return source[start:end if end > 0 else len(source)]
    depth = 0
    for i in range(brace, len(source)):
        depth += {"{": 1, "}": -1}.get(source[i], 0)
        if depth == 0:
            return source[brace + 1:i]
    return ""


def constructor_params(source: str, cls: str) -> list[str]:
    match = re.search(rf"class {cls}\b[^(]*\((.*?)\n\)", source, re.S)
    return re.findall(r"val (\w+)\s*:", match.group(1)) if match else []


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


contract = load(CONTRACT)
progress = load(PROGRESS)
vdsx = load(VDSX)
ddm = load(DDM)
msbx = load(MSBX)

for path in (DAY_KT, EOD_KT, APP_KT, VIEW_KT, DAY_TEST, EOD_TEST, APP_TEST, T2_TEST, SPEC, RESEARCH):
    check(f"E11E-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

day = strip_comments(read(DAY_KT))
eod = strip_comments(read(EOD_KT))
app = strip_comments(read(APP_KT))
ports = strip_comments(read(PORTS_KT))
adapter = strip_comments(read(ADAPTER_KT))
view = strip_comments(read(VIEW_KT))
today_screen = strip_comments(read(TODAY_SCREEN_KT))
application = strip_comments(read(APPLICATION_KT))
day_test = read(DAY_TEST)
eod_test = read(EOD_TEST)
app_test = read(APP_TEST)
t2_test = read(T2_TEST)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E11E-01_model", contract.get("model") == "EODX-v0", str(contract.get("model")))
check("E11E-01_status", contract.get("status") == "accepted_11e", str(contract.get("status")))
check("E11E-01_decision", contract.get("decision") == "D-091", str(contract.get("decision")))
for key in ("evidence_written", "planner_implemented", "mastery_engine_implemented",
            "longitudinal_history_implemented", "new_surface_added", "schema_changed",
            "migration_added", "ports_added", "boundaries_changed", "surface_semantics_changed"):
    check(f"E11E-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")
for key in ("streak_claim", "daily_goal_claim", "completion_percentage_claim", "minutes_studied_claim"):
    check(f"E11E-01_no_{key}", contract["scope"].get(key) is None, f"{key} was claimed")

# ---------------------------------------------------------------- vocabularies from SPWX-v0
check("E11E-02_states", enum_ids(eod, "DaySummaryState") == progress["semantic_states"],
      f"kotlin={enum_ids(eod, 'DaySummaryState')}")
check("E11E-02_history_families",
      enum_ids(day, "HistoryEventFamily") == progress["learning_history"]["event_families"],
      f"kotlin={enum_ids(day, 'HistoryEventFamily')}")
kinds = re.search(r"enum class DayRecordKind\b[^{]*\{(.*?)\n\}", day, re.S)
kind_tables = re.findall(r'"[a-z_]+",\s*"([a-z_]+)"', kinds.group(1) if kinds else "")
ddm_truth = [e["id"] for e in ddm["truth_entities"]]
check("E11E-02_counted_kinds_are_truth_entities", all(t in ddm_truth for t in kind_tables),
      f"kinds={kind_tables}")

# ---------------------------------------------------------------- tones from VDSX-v0
tones = vdsx["surface_state_tones"]
tone_body = body(eod, "val DaySummaryState.tone")
kotlin_tones: dict[str, str] = {}
for names, value in re.findall(r"([A-Za-z_.,\s]+?)->\s*Tone\.([A-Z_]+)", tone_body):
    for name in re.sub(r"\s", "", names).split(","):
        if name.startswith("DaySummaryState."):
            kotlin_tones[name.removeprefix("DaySummaryState.")] = value.lower()
for state in enum_ids(eod, "DaySummaryState"):
    expected = tones.get(state)
    if expected is None:
        continue
    check(f"E11E-03_tone_{state}", kotlin_tones.get(state.upper()) == expected,
          f"kotlin={kotlin_tones.get(state.upper())} vdsx={expected}")
check("E11E-03_every_state_has_a_tone", len(kotlin_tones) == len(enum_ids(eod, "DaySummaryState")),
      f"mapped={len(kotlin_tones)}")
check("E11E-03_fault_tone_only_system",
      {s for s, t in kotlin_tones.items() if t == "system_fault"} == {"ERROR_RECOVERABLE", "DATA_RECOVERY_REQUIRED"},
      f"fault={[s for s, t in kotlin_tones.items() if t == 'system_fault']}")
check("E11E-03_empty_day_is_neutral", kotlin_tones.get("EMPTY_NO_EVIDENCE_YET") == "neutral",
      "an empty day is not neutral")

# ---------------------------------------------------------------- the day boundary
check("E11E-04_day_is_study_day", 'Regex("""\\d{4}-\\d{2}-\\d{2}""")' in day or "STUDY_DAY" in day,
      "a day is not an ISO study day")
check("E11E-04_row_day_decides",
      "fun belongsTo(rowStudyDay: String, studyDay: String): Boolean = rowStudyDay == studyDay" in day,
      "a row's day is not what decides")
rollover = body(day, "fun rollOver(")
check("E11E-04_new_day_starts_empty", "return startOf(nextStudyDay)" in rollover,
      "the new day is not started empty")
check("E11E-04_no_backward_day", "nextStudyDay > previous.studyDay" in rollover, "a day may roll backwards")
check("E11E-04_no_carry_api",
      not re.search(r"fun\s+\w*(carry|debt|backlog|rollForward|catchUp)\w*\s*\(", day, re.I),
      "there is an API that carries something across the day")

# ---------------------------------------------------------------- counting
check("E11E-05_counts_labelled_inventory", "val isLabelledInventory: Boolean get() = true" in day,
      "counts are not labelled as inventory")
check("E11E-05_no_ratio_or_total",
      not re.search(r"\b(percent|percentage|ratio|total|average|goal)\b", day, re.I),
      "an inventory carries a ratio, total or goal")
check("E11E-05_negative_count_refused", "counts.values.none { it < 0 }" in day, "a count may go below zero")
check("E11E-05_recorded_means_nonzero", "counts.values.any { it > 0 }" in day,
      "a zero count reads as a day with work")
FORBIDDEN = ("streak", "percent", "score", "grade", "minutes", "elapsed", "goal", "debt", "carried",
             "rank", "consecutive", "ratio")
for cls in ("DayInventory", "DayRecord", "DayChange"):
    fields = constructor_params(day, cls)
    check(f"E11E-05_no_verdict_field_{cls}",
          not [f for f in fields if any(w in f.lower() for w in FORBIDDEN)], f"{cls} fields={fields}")
for cls in ("DaySummaryView", "DaySummaryInput"):
    fields = constructor_params(eod, cls)
    check(f"E11E-05_no_verdict_field_{cls}",
          not [f for f in fields if any(w in f.lower() for w in FORBIDDEN)], f"{cls} fields={fields}")

# ---------------------------------------------------------------- history
entries = body(day, "fun entries(")
check("E11E-06_history_skips_silent_days",
      "filter { it.inventory.recordedAnything || it.changes.isNotEmpty() }" in entries,
      "history fills in the days that recorded nothing")
check("E11E-06_no_streak_computation",
      not re.search(r"(consecutive|streak|inARow|gapDays)", day + eod, re.I),
      "a streak or gap count is computed")
check("E11E-06_history_families_from_spwx",
      progress["learning_history"]["is_streak_calendar"] is False
      and progress["learning_history"]["gap_marked_as_failure_or_missed_obligation"] is False,
      "SPWX-v0's history rules changed")

# ---------------------------------------------------------------- the projection
of_body = body(eod, "fun of(")
check("E11E-07_recovery_supersedes",
      "CrossCuttingState.DATA_RECOVERY_REQUIRED -> DaySummaryState.DATA_RECOVERY_REQUIRED" in of_body,
      "data recovery does not supersede the day summary")
check("E11E-07_error_reported", "CrossCuttingState.ERROR_RECOVERABLE -> DaySummaryState.ERROR_RECOVERABLE" in of_body,
      "a recoverable failure is not reported")
check("E11E-07_partial_state", "input.unreadKinds.isNotEmpty() -> DaySummaryState.PARTIAL_PROJECTION_AVAILABLE" in of_body,
      "an unread kind does not produce the partial state")
check("E11E-07_empty_state",
      "!input.record.inventory.recordedAnything && input.record.changes.isEmpty() ->" in of_body,
      "an empty day is not distinguished")
check("E11E-07_loading_shows_no_counts",
      "if (state == DaySummaryState.LOADING_PROJECTION) DayInventory(emptyMap())" in of_body,
      "a partial count is presented while loading")
check("E11E-07_changes_are_given_not_derived", "changes = input.record.changes," in of_body,
      "a change is derived rather than reported")
check("E11E-07_empty_is_not_failure", "val isFailure: Boolean get() = false" in eod,
      "an empty day can be a failure")

# ---------------------------------------------------------------- reading a day
load_body = body(app, "fun load(")
check("E11E-08_day_from_clock", "studyDay: String = clock.now().studyDay" in app,
      "the day does not come from the clock")
check("E11E-08_unread_named", "onFailure { unread += kind.id }" in load_body,
      "an unreadable kind is not named")
check("E11E-08_no_zero_substitute", "counts[kind] = 0" not in load_body, "an unreadable kind becomes a zero")
check("E11E-08_reads_only",
      "appendTruth" not in app and "writeProjection" not in app and "inTransaction" not in app,
      "reading a day writes something")
check("E11E-08_no_change_invented", "DayChange(" not in app, "the day summary invents a change")

# ---------------------------------------------------------------- port and adapter
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
msbx_ports = [p["id"] for p in msbx["ports"]["set"]]
check("E11E-09_port_count", sorted(interfaces) == sorted(list(msbx_ports) + declared_port_extensions(ROOT)), f"interfaces={interfaces}")
check("E11E-09_count_truth_declared", "fun countTruth(kind: String, studyDay: String): Int" in ports,
      "countTruth is not a port refinement")
count_impl = body(adapter, "override fun countTruth(")
check("E11E-09_counts_by_study_day", 'endsWith("_study_day")' in count_impl,
      "a day is counted by something other than its recorded study day")
check("E11E-09_never_by_instant", "_at_instant" not in count_impl, "a day is counted by an instant range")
check("E11E-09_truth_only", "require(kind in Schema.truthTables)" in count_impl,
      "a projection or curriculum table can be counted as truth")
check("E11E-09_dayless_table_refused", "requireNotNull(dayColumn)" in count_impl,
      "a table with no day of its own is counted anyway")
check("E11E-09_count_writes_nothing", not re.search(r"\b(INSERT|UPDATE|DELETE)\b", count_impl),
      "counting writes")

# ---------------------------------------------------------------- where it renders
ia = load(IA)
check("E11E-10_no_new_surface",
      not re.search(r"(EndOfDay|DaySummary)\s*:\s*Surface|object\s+(EndOfDay|DaySummary)\s*:\s*Surface",
                    read(SURFACES_KT)),
      "a surface was invented for the day")
check("E11E-10_rendered_in_today", "daySummary?.let { EndOfDayView(it) }" in today_screen,
      "the day summary is not rendered in Today's day context")
check("E11E-10_no_chart_or_grid",
      not re.search(r"(Canvas|drawArc|LinearProgressIndicator|CircularProgressIndicator|Grid|Heatmap)", view),
      "the day summary draws a chart, ring or grid")
check("E11E-10_state_in_text", "stateLabelsTr" in view and "liveRegion" in view,
      "the day state is not conveyed in text")
check("E11E-10_ui_names_no_tone", "Tone." not in view, "app-ui chooses a tone")
check("E11E-11_read_on_store_thread", "DayCloseFacts(" in application and "storeThread" in application,
      "the day is not read on the store thread")
check("E11E-11_refreshed_on_resume", "refreshToday" in application and "DayCloseFacts(" in application,
      "the day is not refreshed with Today")

# ---------------------------------------------------------------- tests
for name in ["nothing crosses the day boundary",
             "history shows the days that recorded something and never fills the gaps",
             "an inventory counts and says it is an inventory, and carries no ratio",
             "a day is a study day, and a row belongs to the day it recorded"]:
    check(f"E11E-12_t1_model_{name[:40]}", f"`{name}`" in day_test, f"missing test: {name}")
for name in ["a day with nothing recorded is empty and never a failure",
             "activity alone changes nothing, and the summary says so",
             "a count that could not be read is named, never treated as zero",
             "a store that cannot be trusted supersedes the day summary",
             "the view can hold no streak, percentage, score, minutes or debt"]:
    check(f"E11E-12_t1_eod_{name[:40]}", f"`{name}`" in eod_test, f"missing test: {name}")
for name in ["the day is the clock's study day, and every kind is counted against it",
             "a kind that cannot be read is named unread, not counted as zero",
             "reading a day writes nothing and claims no change"]:
    check(f"E11E-12_t1_app_{name[:40]}", f"`{name}`" in app_test, f"missing test: {name}")
for name in ["the recorded study day decides, even when the instant says otherwise",
             "every kind the day summary counts is countable, and the rest are refused",
             "counting a day writes nothing"]:
    check(f"E11E-12_t2_{name[:40]}", f"`{name}`" in t2_test, f"missing T2 test: {name}")

# ---------------------------------------------------------------- honesty
device = contract["device_verification"]
check("E11E-13_device_not_claimed", device["t6_run"] is False and device["claimed"] is False, "a device result is claimed")
mutation = contract["mutation_results"]
check("E11E-13_mutation_all_detected",
      mutation["detected"] == mutation["total"] == len(mutation["mutants"]) >= 16,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E11E-13_mutation_ran_gradle", mutation.get("harness_verified_to_run_gradle") is True,
      "the mutation harness was not verified to have run anything")
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E11E-13_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
check("E11E-13_not_verified_named", len(contract.get("not_verified", [])) >= 3, "what was not verified must be named")
boundaries = {str(k) for k in contract.get("future_stage_boundaries", {})}
for owner in ("12", "13", "14", "16B"):
    check(f"E11E-13_boundary_{owner}", owner in boundaries, f"missing {owner}")
forbidden = set(contract.get("forbidden_end_of_day_patterns", []))
for pattern in ("day_declared_successful_or_failed", "streak_or_consecutive_day_count",
                "completion_percentage_or_ratio", "minutes_studied_as_progress",
                "unfinished_work_carried_as_debt", "change_claimed_without_canonical_change",
                "unreadable_count_presented_as_zero", "gap_between_days_rendered_as_missed_obligation",
                "day_recomputed_from_instant_instead_of_recorded_study_day"):
    check(f"E11E-14_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 11E QA PASS", "**Decision:** `D-091`",
                 "boundary in time, not a verdict", "T6", "16B"]:
    check(f"E11E-15_spec_{fragment[:26]}", fragment in spec_text, f"missing={fragment!r}")
for fragment in ["No web research pass was needed", "SPWX-v0", "study day"]:
    check(f"E11E-16_research_{fragment[:24]}", fragment in research_text, f"missing={fragment!r}")

# living documents must not repeat a numbered section or an adjacent line (11C's sync defect)
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E11E-17_no_repeated_context_heading", len(context_headings) == len(set(context_headings)),
      f"repeated={[h for h in set(context_headings) if context_headings.count(h) > 1]}")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "EODX-v0", "stage_step": "11E", "decision": "D-091",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"11E_END_OF_DAY_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
