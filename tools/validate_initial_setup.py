"""Independent 18A QA — ISTX-v0, the first-run setup (and the run report's replayed "before").

The rules are read from `UXIA-v0` §7.4 (`initial_setup_flow`: pre-shell, the minimum local setup, then Today; no mandatory
diagnostic), `VDW-v0` §3 (a diagnostic's sources; "I know this" names the scope and is never evidence), 16D (the daily
length is a preference) and the user's decision on 13E's report (D-139), and compared with the real Kotlin, the schema,
the emulator audit's reports, the tests, the spec, the contract and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a setup that plans, is shown twice or to a used store,
pre-ticks an area, makes a diagnostic mandatory or counts "I know this" as evidence; a skip that records more than that
the setup was done; a plan made before the learner's choices are recorded; and a run report that replaces an axis an
engine had written, or still disagrees with the history.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
UI = ANDROID / "app-ui/src/main/kotlin/coach/ui"
PRES = ANDROID / "core-presentation/src/main/kotlin/coach/presentation"
APP = ANDROID / "core-application/src/main/kotlin/coach/application"
MODEL = ANDROID / "core-model/src/main/kotlin/coach"
WIRING = ANDROID / "app-wiring/src/main/kotlin/coach/wiring"
CONTRACT = ROOT / "arch/18a_initial_setup/initial_setup.yaml"
QA_OUT = ROOT / "arch/18a_initial_setup/qa_report.yaml"
REPORT_100 = ROOT / "arch/18a_initial_setup/a11y_emulator_report.yaml"
REPORT_200 = ROOT / "arch/18a_initial_setup/a11y_emulator_report_text_200.yaml"
SPEC = ROOT / "docs/INITIAL_SETUP_SPEC.md"
RESEARCH = ROOT / "research/18a_initial_setup_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
EXECUTION_INDEX = ROOT / "docs/EXECUTION_INDEX.md"
MASTER_PLAN = ROOT / "docs/MASTER_PLAN.md"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
SCHEMA = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
TESTS = {
    "InitialSetupCourseTest": (ANDROID / "app-wiring/src/test/kotlin/coach/wiring/InitialSetupCourseTest.kt", 6),
    "SetupStorageTest": (ANDROID / "data-persistence/src/test/kotlin/coach/persistence/SetupStorageTest.kt", 3),
}

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def code_only(src: str) -> str:
    src = re.sub(r"/\*.*?\*/", "", src, flags=re.S)
    return re.sub(r"(?m)^\s*//[^\n]*|\s//\s[^\n]*", "", src)


def segment(src: str, start: str, end: str) -> str:
    at = src.find(start)
    if at < 0:
        return ""
    stop = src.find(end, at + len(start))
    return src[at:stop if stop > 0 else len(src)]


def load_yaml(path: Path):
    try:
        return yaml.safe_load(read(path)) or {}
    except yaml.YAMLError:
        return {}


contract = load_yaml(CONTRACT)
setup = read(APP / "InitialSetup.kt")
setup_code = code_only(setup)
presentation = read(PRES / "SetupPresentation.kt")
screen = read(UI / "SetupScreen.kt")
activity = read(WIRING / "MainActivity.kt")
application = read(WIRING / "CoachApplication.kt")
navigation = read(PRES / "Navigation.kt")
schema = read(SCHEMA)
facts = read(MODEL / "DiagnosticFacts.kt")
changes = read(APP / "ProgramChanges.kt")
history = read(APP / "LearningHistory.kt")

# ---------------------------------------------------------------- 1. identity
check("A18A-01_contract", contract.get("model") == "ISTX-v0" and contract.get("decision") == "D-139"
      and contract.get("stage_step") == "18A" and contract.get("added_step") == "D-140", str({k: contract.get(k) for k in ("model", "decision")}))
check("A18A-01_spec_accepted", "ISTX-v0" in read(SPEC) and "ACCEPTED" in read(SPEC) and "D-139" in read(SPEC), "")
check("A18A-01_research", "initial_setup_flow" in read(RESEARCH) and "curriculum_entry_placement" in read(RESEARCH), "")
decisions = contract.get("user_decisions") or {}
check("A18A-01_user_decisions", "first-run setup only" in str(decisions.get("scope", "")) and "18G" in str(decisions.get("new_step", ""))
      and "replays" in str(decisions.get("what_changed", "")), "")

# ---------------------------------------------------------------- 2. shown once, before Today, never to a used store
needed = segment(setup, "fun needed()", "fun topics()")
check("A18A-02_shown_once_on_an_unused_store", "persistence.curriculumPublished() && persistence.setupRecords().isEmpty() && persistence.latestPlan() == null" in needed, "")
check("A18A-02_surface_pre_shell", "data object InitialSetupFlow : Surface" in navigation and 'override val id = "initial_setup_flow"' in navigation
      and "get() = listOf(InitialSetupFlow)" in navigation and "+ setupFlows" in navigation, "")
ia = load_yaml(IA)
ia_setup = ((ia.get("surface_families") or {}).get("setup") or []) if isinstance(ia.get("surface_families"), dict) else []
ia_text = read(IA)
check("A18A-02_ia_contract", "id: initial_setup_flow" in ia_text and "pre_shell: true" in ia_text and "mandatory_diagnostic_invented_by_ia: false" in ia_text, "")

# ---------------------------------------------------------------- 3. it plans nothing; Today is planned after it
check("A18A-03_setup_plans_nothing", not re.search(r"BuildDailyPlan|PlanToday|\.replan\(|\.build\(", setup_code), "the setup plans")
check("A18A-03_refresh_waits_for_setup", "if (setupState.value == SetupState.Settled) refreshToday()" in activity
      and "if (view == null) refreshToday()" in activity, "Today can be planned while the setup is open")
end_setup = segment(activity, "private fun endSetup(", "override fun onCreate(")
check("A18A-03_today_after_setup", "app.finishSetup(" in end_setup and end_setup.find("refreshToday()") > end_setup.find("app.finishSetup("), "")
check("A18A-03_ended_once", "if (setupState.value !is SetupState.Showing) return" in end_setup
      and end_setup.find("!is SetupState.Showing) return") < end_setup.find("app.finishSetup("), "two taps end the setup twice")
check("A18A-03_setup_before_shell", "if (current != null && setup is SetupState.Showing) {" in activity
      and activity.find("setup is SetupState.Showing") < activity.find("Shell(health, current, facts"), "")

# ---------------------------------------------------------------- 4. placement: the learner's areas only, never evidence
finish = segment(setup, "fun finish(", "companion object")
check("A18A-04_placement_source", "RequestDiagnostic(persistence, clock).request(DiagnosticSource.CURRICULUM_ENTRY_PLACEMENT, startable)" in finish, "")
check("A18A-04_only_when_completed_and_ticked", "if (outcome == SetupOutcome.COMPLETED && startable.isNotEmpty()) {" in finish, "")
check("A18A-04_only_startable", 'placement.distinct().filter { persistence.skill(it)?.lifecycleStatus == PUBLISHED }' in finish, "")
check("A18A-04_skip_records_only_that", "if (outcome == SetupOutcome.COMPLETED) {\n            normalMinutes?.let" in finish, "a skip writes a preference")
# Review (18A): a setup interrupted after its diagnostic is offered again; a leftover placement diagnostic is withdrawn first.
check("A18A-04_leftover_withdrawn", "DiagnosticSessions.active(persistence)?.scope?.source == DiagnosticSource.CURRICULUM_ENTRY_PLACEMENT" in finish
      and "WithdrawDiagnostic(persistence, clock).withdraw()" in finish
      and finish.find("WithdrawDiagnostic(") < finish.find("CapacitySettings("), "a skipped setup can leave a diagnostic")
# Review (18A): the entry placement is opened only by the setup, from what the learner ticked.
uses = [str(p.relative_to(ANDROID)) for p in ANDROID.rglob("*.kt") if "/build/" not in p.as_posix() and "/test/" not in p.as_posix()
        and "DiagnosticSource.CURRICULUM_ENTRY_PLACEMENT" in code_only(p.read_text(encoding="utf-8"))]
check("A18A-04_one_call_site", uses == [str((APP / "InitialSetup.kt").relative_to(ANDROID))]
      and code_only(setup).count("RequestDiagnostic(persistence, clock).request(DiagnosticSource.CURRICULUM_ENTRY_PLACEMENT") == 1, str(uses))
check("A18A-04_setup_row_last", finish.rfind("appendTruth(") > finish.find("RequestDiagnostic(") > finish.find("CapacitySettings("), "")
check("A18A-04_no_evidence_written", not re.search(r"evidence_event|RecordEvidence|EvidencePipeline|Waiver\(", setup_code), "the setup writes evidence or a waiver")
placement = segment(presentation, "fun placement(", "private const val")
check("A18A-04_ticked_areas_only", "view.areas.filter { it.id in ticked }.flatMap { it.skills }.distinct()" in placement, "")
check("A18A-04_source_representable", 'CURRICULUM_ENTRY_PLACEMENT("curriculum_entry_placement")' in facts
      and "PLANNER_DIAGNOSTIC_OPPORTUNITY" not in facts, "the app could open a diagnostic by itself")
check("A18A-04_source_reason_is_the_learner", "val reasonCode: String get() = DiagnosticCodes.USER_REQUESTED_FAST_PATH" in facts, "")

# ---------------------------------------------------------------- 5. the words: not evidence, not mandatory, not a failure
copy = segment(presentation, "object SetupCopy", "data class SetupArea")
check("A18A-05_not_evidence", "kanıt sayılmaz" in copy, "")
check("A18A-05_not_mandatory", "zorunda değilsin" in copy, "")
check("A18A-05_not_a_failure", "başarısızlık değildir" in copy, "")
check("A18A-05_length_is_a_ceiling", "üst sınır, hedef değil" in copy, "")
# Review (18A): a check joins the plan when its Skill may start; most of an area waits at first, so neither "today" nor "short".
check("A18A-05_no_false_promise", "başlatılabilir olduğunda" in copy and "bugünün planına" not in copy and "kısa kontrol" not in copy, "")
check("A18A-05_no_level_or_score", not re.search(r"seviye|puan|yüzde|%|test et|sınav", copy, re.I), "the setup speaks of a level, a score or a test")

# ---------------------------------------------------------------- 6. the screen
check("A18A-06_nothing_pre_ticked", "var ticked by rememberSaveable(stateSaver = TICKED_SAVER) { mutableStateOf(emptySet<String>()) }" in screen, "")
check("A18A-06_kept_across_recreation", "rememberSaveable(view.normalMinutes)" in screen, "")
check("A18A-06_skip_first", 0 < screen.find("Text(SetupCopy.SKIP)") < screen.find("Text(SetupCopy.SKIP_NOTE") < screen.find("ScreenTitle(SetupCopy.TITLE)"),
      "the skip, and what it does, are not first")
check("A18A-06_checkbox_rows", "role = Role.Checkbox" in screen and "Checkbox(checked = checked, onCheckedChange = null)" in screen
      and ".minimumTouchTarget()\n                        .toggleable(" in screen, "")
check("A18A-06_field_error_its_own", "supportingText = if (minutes == null) { { Text(SettingsCopy.MINUTES_INVALID) } } else null" in screen
      and "isError" not in code_only(screen), "")
check("A18A-06_buttons_48dp", screen.count("minimumTouchTarget()") >= 3, "")
check("A18A-06_decides_nothing", "coach.application" not in screen and "SettingsPresentation.parseMinutes(typed)" in screen, "")

# ---------------------------------------------------------------- 7. schema v13
# Narrowed at 18G (`D-141`): version 14 adds the pilot journal after v13, so v13's segment ends where v14 begins, and the
# version may be later when every later version is owned by an accepted contract's `schema_migration`.
v13 = segment(schema, "val v13: List<String>", "val v14: List<String>" if "val v14: List<String>" in schema else "const val MISCONCEPTION_SOURCES")


def _v13_or_owned_later(text: str) -> bool:
    found = re.search(r"const val VERSION = (\d+)", text)
    if not found:
        return False
    owned = set()
    for path in (ROOT / "arch").glob("*/*.yaml"):
        data = yaml.safe_load(path.read_text(encoding="utf-8")) or {}
        migration = data.get("schema_migration") if isinstance(data, dict) else None
        if isinstance(migration, dict) and isinstance(migration.get("to"), int):
            owned.add(migration["to"])
    return int(found.group(1)) >= 13 and all(v in owned for v in range(14, int(found.group(1)) + 1))


check("A18A-07_version", _v13_or_owned_later(schema) and "12 to Schema.v13," in code_only(read(MIGRATIONS)), "")
check("A18A-07_only_adds", len(re.findall(r"CREATE TABLE IF NOT EXISTS", v13)) == 1 and not re.search(r"\b(ALTER|DROP|UPDATE|DELETE)\b", code_only(v13)), "")
check("A18A-07_guarded", "setupTables.flatMap(::appendOnlyGuards)" in v13 and "+ setupTables" in schema
      and 'val setupOutcomeValues = listOf("completed", "skipped")' in schema, "")
mig = contract.get("schema_migration") or {}
check("A18A-07_owned", mig.get("from") == 12 and mig.get("to") == 13 and mig.get("truth_altered") is False, str(mig))

# ---------------------------------------------------------------- 8. the run report's before (user decision)
report = segment(changes, "fun report(captured: ProgramSnapshot", "private fun replayedBefore(")
replayed = segment(changes, "private fun replayedBefore(", "internal companion object")
check("A18A-08_before_replayed_first", "val before = replayedBefore(captured, touched.distinct())" in report
      and report.find("replayedBefore(") < report.find("recompute.recompute("), "the replay reads after the recompute")
check("A18A-08_only_unwritten_replaced", all(s in replayed for s in (
    "axes.mastery.takeUnless { it == unwritten } ?: replayed.mastery",
    "axes.retention.takeUnless { it == unwritten } ?: replayed.retention",
    "axes.weakness.takeUnless { it == unwritten } ?: replayed.weakness")), "a written axis may be replaced")
check("A18A-08_replay_up_to_the_snapshot", "HistoryReplay.axesAt(persistence, skill, persistence.objectivesOf(skill), captured.truthWatermark, captured.studyDay)" in replayed, "")
check("A18A-08_history_reading_shared", "fun axesAt(" in history and "fun read(cutoff: Long, day: String): SkillAxes = read(overlay, skill, profiles, cutoff, day)" in history, "")
check("A18A-08_report_names_in_turkish", "CurriculumNames.skill(ref, graph.persistence.skill(ref)?.canonicalName)" in application, "")

# ---------------------------------------------------------------- 9. tests
for name, (path, least) in TESTS.items():
    src = read(path)
    check(f"A18A-09_tests_{name}", src.count("@Test") >= least, f"{src.count('@Test')} < {least}")
pc = read(ANDROID / "core-application/src/test/kotlin/coach/application/ProgramChangesTest.kt")
check("A18A-09_report_agrees_with_history", "the first reading of a Skill reports what the history reports" in pc
      and "LearningHistoryQuery(store).load().entries.single()" in pc, "")
course = read(TESTS["InitialSetupCourseTest"][0])
check("A18A-09_course_covers_review", all(s in course for s in ("no C check before its prerequisite", "the leftover placement diagnostic is withdrawn")), "")
check("A18A-09_course_covers_rules", all(s in course for s in ("assertFalse(setup.needed(), \"offered once\")", "assertNull(store.latestPlan())",
                                                                  "DiagnosticSource.CURRICULUM_ENTRY_PLACEMENT", "a skip keeps the preset",
                                                                  "a plan was made: the store was used before 18A")), "")

# ---------------------------------------------------------------- 10. the real app, audited with the setup
for label, path, scale in (("100", REPORT_100, 1.0), ("200", REPORT_200, 2.0)):
    rep = load_yaml(path)
    screens = rep.get("screens") or []
    names = [s.get("screen") for s in screens]
    check(f"A18A-10_report_{label}_passed", rep.get("result") == "PASS" and rep.get("findings_total") == 0 and rep.get("step") == "18A", str(rep.get("result")))
    check(f"A18A-10_report_{label}_font_scale", rep.get("font_scale") == scale, str(rep.get("font_scale")))
    check(f"A18A-10_report_{label}_setup_screen", "initial_setup" in names and len(names) >= 17 and not rep.get("screens_missing"), str(names))
    check(f"A18A-10_report_{label}_reached_and_whole", screens and all(s.get("marker_found") and s.get("read_to_end") and not s.get("clipped_never_judged")
                                                                          and s.get("unanchored_readings", 0) == 0 for s in screens), "")
tool = read(ROOT / "tools/audit_a11y_emulator.py")
check("A18A-10_tool_fails_a_short_run", 'all(n in [r["screen"] for r in results] for n in EXPECTED_SCREENS)' in read(ROOT / "tools/audit_a11y_emulator.py")
      and '"initial_setup", "today_overview"' in read(ROOT / "tools/audit_a11y_emulator.py"), "a run that reached few screens can pass")
check("A18A-10_tool_reads_setup", 'visit("initial_setup", ["Hoş geldin", "Şimdilik atla"], ["Öğren"])' in tool and 'tap_text("Şimdilik atla")' in tool, "")

# ---------------------------------------------------------------- 11. the contract says what was not done
ver = contract.get("verification") or {}
check("A18A-11_not_claimed", ver.get("t6_run") is False, "")
check("A18A-11_moved_to_18g", any("18G" in str(m) for m in contract.get("moved_on") or []), "")
amend = contract.get("amendments") or []
check("A18A-11_amendments", any("PCRX-v0" in str(a.get("contract")) for a in amend) and any("VDW-v0" in str(a.get("contract")) for a in amend), "")
for key in ("mutation_results_kotlin", "mutation_results_validator"):
    m = contract.get(key) or {}
    check(f"A18A-12_{key}", isinstance(m.get("total"), int) and m.get("total") > 0 and m.get("detected") == m.get("total")
          and m.get("negative_control_result") == "survived_as_expected", str({k: m.get(k) for k in ("total", "detected")}))
check("A18A-12_independent_review", "found" in str(ver.get("independent_review", "")) and "pending" not in str(ver.get("independent_review", "")), "")

# ---------------------------------------------------------------- 12. the living record
dec = read(DECISIONS)
check("A18A-12_decision_recorded", "## D-139" in dec and "ISTX-v0" in dec and "## D-140" in dec and "18G" in dec, "")
check("A18A-12_execution_index", re.search(r"\[x\] \*\*18A", read(EXECUTION_INDEX)) is not None and "18G" in read(EXECUTION_INDEX), "")
check("A18A-12_master_plan", "### [x] 18A" in read(MASTER_PLAN) and "18G" in read(MASTER_PLAN), "")

passed = sum(r["result"] == "PASS" for r in results)
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump({"step": "18A", "model": "ISTX-v0", "checks": len(results), "passed": passed, "results": results},
                                 allow_unicode=True, sort_keys=False, width=200), encoding="utf-8", newline="\n")
print(f"18A QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
for f in failures:
    print("FAIL", f)
sys.exit(0 if not failures else 1)
