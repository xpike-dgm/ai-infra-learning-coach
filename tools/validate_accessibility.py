"""Independent 17D QA — A11X-v0, accessibility.

The rules are read from `VDSX-v0` §8 and §12 (48dp targets, state said in text, 200% text), `UXIA-v0` (every surface
has one title), `TRUX-v0` (the focused-flow exit) and WCAG 2.2 (name, role, value; label in name; status messages), and
compared with the real Kotlin, the emulator audit's reports, the tests, the spec, the contract and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a control a screen reader cannot name; two controls
it cannot tell apart; a target under 48dp; an identifier or a help code read aloud; a screen with no heading; a field
whose error is not its own; an emulator audit that did not run, did not reach the screens it names, or did not pass at
200% text; and a claim that TalkBack was heard when it was not.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
UI = ANDROID / "app-ui/src/main/kotlin/coach/ui"
CONTRACT = ROOT / "arch/17d_accessibility/accessibility.yaml"
QA_OUT = ROOT / "arch/17d_accessibility/qa_report.yaml"
REPORT_100 = ROOT / "arch/17d_accessibility/a11y_emulator_report.yaml"
REPORT_200 = ROOT / "arch/17d_accessibility/a11y_emulator_report_text_200.yaml"
SPEC = ROOT / "docs/ACCESSIBILITY_SPEC.md"
RESEARCH = ROOT / "research/17d_accessibility_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
EXECUTION_INDEX = ROOT / "docs/EXECUTION_INDEX.md"
MASTER_PLAN = ROOT / "docs/MASTER_PLAN.md"
AUDIT_TOOL = ROOT / "tools/audit_a11y_emulator.py"
SETTINGS = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/SettingsPresentation.kt"
TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/AccessibleNamesTest.kt"
COMPONENTS = UI / "Components.kt"
THEME = UI / "CoachTheme.kt"
SHELL = UI / "AppShell.kt"
PROFILE = UI / "ProfileScreen.kt"

RULES = ["R1_no_accessible_name", "R2_target_below_48dp", "R3_identifier_on_screen", "R4_image_only_control",
         "R5_checkable_unnamed", "R6_text_field_unnamed", "R7_ambiguous_duplicate_name", "R8_reading_order"]
# Every surface a learner reaches without content of their own, in both text sizes; each is read whole (scrolled).
SCREENS = ["today_overview", "today_capacity_dialog", "planner_explanation", "learn_overview", "topic_detail",
           "progress_overview", "learning_history", "assessment_report", "technical_english_profile", "profile_overview",
           "task_runner_orientation", "task_runner_lesson", "task_runner_question", "task_runner_help"]

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
    return re.sub(r"//[^\n]*", "", src)


def call_args(src: str, open_paren: int) -> str:
    depth = 0
    for j in range(open_paren, len(src)):
        if src[j] == "(":
            depth += 1
        elif src[j] == ")":
            depth -= 1
            if depth == 0:
                return src[open_paren:j + 1]
    return src[open_paren:]


def calls(pattern: str) -> list[tuple[str, str]]:
    found = []
    for f in sorted(UI.glob("*.kt")):
        src = code_only(read(f))
        for m in re.finditer(pattern, src):
            found.append((f"{f.name}:{src[:m.start()].count(chr(10)) + 1}", call_args(src, m.end() - 1)))
    return found


contract = yaml.safe_load(read(CONTRACT)) or {}
r100 = yaml.safe_load(read(REPORT_100)) or {}
r200 = yaml.safe_load(read(REPORT_200)) or {}
spec = read(SPEC)
decisions = read(DECISIONS)
components = code_only(read(COMPONENTS))
theme = code_only(read(THEME))
shell = code_only(read(SHELL))
profile = code_only(read(PROFILE))
settings = read(SETTINGS)
test = read(TEST)
tool = read(AUDIT_TOOL)
ui_all = {f.name: code_only(read(f)) for f in sorted(UI.glob("*.kt"))}

# --- A17D-01: everything the step claims exists.
missing = [str(p.relative_to(ROOT)) for p in (CONTRACT, SPEC, RESEARCH, REPORT_100, REPORT_200, AUDIT_TOOL, TEST) if not p.is_file()]
check("A17D-01_files_exist", not missing, str(missing))
check("A17D-01_contract_identity", contract.get("model") == "A11X-v0" and contract.get("decision") == "D-137"
      and contract.get("stage_step") == "17D", f"{contract.get('model')} {contract.get('decision')}")

# --- A17D-02: the emulator audit ran, at both text sizes, over every named screen, with every rule, and passed.
for label, rep, scale in (("100", r100, 1.0), ("200", r200, 2.0)):
    screens = [s.get("screen") for s in rep.get("screens", [])]
    check(f"A17D-02_report_{label}_passed", rep.get("result") == "PASS" and rep.get("findings_total") == 0
          and not any(s.get("findings") for s in rep.get("screens", [])), f"{rep.get('result')} {rep.get('findings_total')}")
    check(f"A17D-02_report_{label}_font_scale", rep.get("font_scale") == scale, str(rep.get("font_scale")))
    check(f"A17D-02_report_{label}_covers_screens", screens == SCREENS, f"missing {sorted(set(SCREENS) - set(screens))}")
    check(f"A17D-02_report_{label}_all_rules", rep.get("rules") == RULES, str(rep.get("rules")))
    check(f"A17D-02_report_{label}_screens_not_empty", all(s.get("actionable", 0) > 0 for s in rep.get("screens", [])) and screens != [],
          "every screen had actionable nodes (the audit read a real screen, not a blank one)")
    # Reaching a screen is shown by its marker, not by a tap that returned; a node cut by the viewport was seen whole.
    check(f"A17D-02_report_{label}_screens_reached", screens != [] and all(s.get("marker_found") is True and s.get("marker", {}).get("present")
          for s in rep.get("screens", [])), str([s.get("screen") for s in rep.get("screens", []) if s.get("marker_found") is not True]))
    check(f"A17D-02_report_{label}_every_clipped_node_judged", screens != [] and all(s.get("clipped_never_judged") == []
          for s in rep.get("screens", [])), str([(s.get("screen"), s.get("clipped_never_judged")) for s in rep.get("screens", []) if s.get("clipped_never_judged")]))
    check(f"A17D-02_report_{label}_read_whole", screens != [] and all(s.get("readings", 0) >= 1 and s.get("read_to_end") is True and s.get("unanchored_readings") == 0
          for s in rep.get("screens", [])), str([s.get("screen") for s in rep.get("screens", []) if s.get("read_to_end") is not True]))
    check(f"A17D-02_report_{label}_device","API" in str(rep.get("device", "")) and rep.get("density_scale", 0) > 0, str(rep.get("device")))
# The tool checks what the report says it checks.
check("A17D-02_tool_has_every_rule", all(rule in tool for rule in RULES) and "MIN_DP = 48" in tool, "rules in the audit tool")
check("A17D-02_tool_clears_app_data", '"pm", "clear"' in tool, "a fresh store, so every screen is reachable")
check("A17D-02_tool_field_named_by_label", "never by the value typed into it" in tool and 'inner = [t for c in node for t in texts(c) if t]' in tool,
      "a field's name excludes its own value")
check("A17D-02_tool_fails_on_unreached_or_unjudged", 'r["clipped_never_judged"] or not r["marker_found"]' in tool, "the report's result")

# --- A17D-03: every icon names itself, or is decorative beside a word that does.
icon_calls = calls(r"\bIcon\(")
check("A17D-03_icons_declare_description", icon_calls != [] and all("contentDescription" in a for _, a in icon_calls),
      str([w for w, a in icon_calls if "contentDescription" not in a]))
# An icon alone in a button is the button's only name: it may never be decorative there.
check("A17D-03_icon_button_named", all("contentDescription = null" not in a for _, a in calls(r"\bIconButton\(")), "IconButton")
check("A17D-03_no_image_without_description", all("contentDescription" in a for _, a in calls(r"\bImage\(")), "Image")
check("A17D-03_back_button_has_word", re.search(r'fun BackButton[^}]*Icon\([^)]*contentDescription = null[^}]*Text\("Geri"\)', components, re.S) is not None,
      "the back arrow is decorative beside 'Geri'")

# --- A17D-04: every surface has a heading, and titles are headings.
check("A17D-04_title_is_heading", re.search(r"fun ScreenTitle[^\n]*\n[^\n]*semantics \{ heading\(\) \}", components) is not None, "ScreenTitle")
check("A17D-04_section_is_heading", re.search(r"fun SectionHeader[^\n]*\n[^\n]*semantics \{ heading\(\) \}", components) is not None, "SectionHeader")
no_heading = [n for n, s in ui_all.items() if (n.endswith("Screen.kt") or n in ("EndOfDayView.kt", "HealthSurface.kt"))
              and not re.search(r"ScreenTitle\(|SectionHeader\(|heading\(\)", s)]
check("A17D-04_every_screen_has_heading", not no_heading, str(no_heading))

# --- A17D-05: every target is 48dp and every control is announced with its role and state.
small = [w for w, a in calls(r"\b(?:Outlined|Text|FilledTonal|Elevated|Icon)?Button\(|\bFloatingActionButton\(") if "minimumTouchTarget" not in a]
check("A17D-05_buttons_48dp", not small, str(small))
# Every hand-made target (a clickable, selectable or toggleable modifier) carries the floor in the same modifier chain.
loose_targets = [f"{n}:{src[:m.start()].count(chr(10)) + 1}" for n, src in ui_all.items()
                 for m in re.finditer(r"\.(?:clickable|selectable|toggleable)\(", src)
                 if "minimumTouchTarget()" not in src[max(0, m.start() - 160):m.end() + 200]]
check("A17D-05_hand_made_targets_48dp", not loose_targets, str(loose_targets))
# Narrowed at 17E (`RUXX-v0` / `D-138`): the 48dp target (which now draws the focus ring) comes before the clickable, so the
# ring sees the row's focus; the floor and the role are what this checks.
check("A17D-05_navrow_48dp_and_role", re.search(r"\.minimumTouchTarget\(\)[\s\S]{0,160}?\.clickable\([^)]*role = Role\.Button", components) is not None, "NavRow")
check("A17D-05_floor_is_48", "MINIMUM_TOUCH_TARGET_DP" in theme and re.search(r"MINIMUM_TOUCH_TARGET_DP\s*=\s*48\b", read(ANDROID / "core-presentation/src/main/kotlin/coach/presentation/DesignTokens.kt")) is not None, "48dp")
chip = theme[theme.find("fun StateChip("):theme.find("fun Modifier.minimumTouchTarget")]
# One leaf: the label is spoken once, and a caller's heading or live region lands on a node a screen reader can focus.
check("A17D-05_state_chip_one_leaf", "clearAndSetSemantics { contentDescription = label }" in chip and "stateDescription" not in chip, "StateChip")
check("A17D-05_nav_says_selected", "stateDescription = if (destination == state.selected)" in shell and "traversalIndex" in shell, "AppShell")
check("A17D-05_radio_group", all("selectableGroup()" in s for n, s in ui_all.items() if "Role.RadioButton" in s), "a choice is one group")

# --- A17D-06: every field is named by a label, and its error is its own.
fields = calls(r"\bOutlinedTextField\(")
check("A17D-06_fields_labelled", fields != [] and all("label =" in a for _, a in fields), str([w for w, a in fields if "label =" not in a]))
check("A17D-06_no_abbreviation_label", 'label = { Text("dk") }' not in profile, "a field named only 'dk'")
# An input error is its field's supporting text, never loose text, and never the fault tone (17A: a mistyped number is not a fault).
loose = [f"{n}:{m.group(0)[:60]}" for n, src in ui_all.items() for m in re.finditer(r"Text\(\w+Copy\.\w*INVALID\b", src)
         if "supportingText =" not in src[max(0, m.start() - 100):m.start()]]
invalid_fields = sum(src.count("supportingText =") for src in ui_all.values())
check("A17D-06_error_belongs_to_field", not loose and invalid_fields >= 3, f"loose={loose} supporting={invalid_fields}")
check("A17D-06_no_fault_tone_for_input", not any("isError" in src for src in ui_all.values()), "isError in app-ui")

# --- A17D-07: repeated controls are told apart; a spoken name keeps the visible word.
check("A17D-07_length_names_per_day", "SettingsCopy.lengthField(line.label)" in profile and "SettingsCopy.saveLength(line.label)" in profile, "ProfileScreen")
tabs = set(re.findall(r'Destination\.[A-Z_]+ to "([^"]+)"', shell))
today_rows = re.search(r"routeLabelsTr[^=]*=\s*mapOf\((.*?)\n\)", ui_all.get("TodayScreen.kt", ""), re.S)
row_names = set(re.findall(r'"[a-z_]+" to "([^"]+)"', today_rows.group(1))) if today_rows else set()
check("A17D-07_rows_not_tab_names", len(tabs) == 4 and row_names != set() and not (tabs & row_names), f"shared: {sorted(tabs & row_names)}")
check("A17D-07_label_in_name",'fun saveLength(label: String) = "$label süresini kaydet"' in settings and 'Text("Kaydet")' in profile, "Kaydet inside the spoken name")

# --- A17D-08: status messages are polite; nothing interrupts the learner.
assertive = [n for n, s in ui_all.items() if "LiveRegionMode.Assertive" in s]
check("A17D-08_no_assertive_live_region", not assertive, str(assertive))
check("A17D-08_run_result_announced", "liveRegion = LiveRegionMode.Polite; contentDescription = stage.result" in ui_all.get("TaskRunnerScreen.kt", ""), "the result is announced")
check("A17D-08_no_help_code_in_ui", not any(re.search(r'"[^"\n]*\bH[1-4]\b[^"\n]*"', s) for s in ui_all.values()), "H1-H4 in UI strings")
# An id interpolated into a shown string (a help level's or a tool's) is an identifier on screen.
id_templates = [n for n, s in ui_all.items() if re.search(r"\$\{[^}]*\.id\}", s)]
check("A17D-08_no_id_in_ui_template", not id_templates, str(id_templates))
assessment_ui = ui_all.get("AssessmentSessionScreen.kt", "")
check("A17D-08_tools_in_words", "ToolWords.of(policy.allowed)" in assessment_ui and "allowed.joinToString" not in assessment_ui
      and "prohibited.joinToString" not in assessment_ui, "allowed tools")

# --- A17D-09: tests.
check("A17D-09_tests", test.count("@Test") >= 3 and "saveLength" in test and "lengthField" in test and "ToolWords.of" in test,
      "AccessibleNamesTest")

# --- A17D-10: scope, honesty and what moved on.
verification = contract.get("verification") if isinstance(contract.get("verification"), dict) else {}
check("A17D-10_talkback_not_claimed", verification.get("talkback_heard") is False and verification.get("t6_run") is False
      and "TalkBack" in str(verification.get("talkback_reason", "")), str(verification.get("talkback_heard")))
nis = " ".join(contract.get("not_in_scope", []))
check("A17D-10_not_in_scope_named", all(w in nis for w in ("focus ring", "theme", "reduced motion")), nis[:120])
moved = " ".join(contract.get("moved_to_17e", []))
check("A17D-10_moved_to_17e", all(w in moved for w in ("day summary", "skill_detail -> planner_explanation", "technical_english_profile -> skill_detail", "AI usage")), moved[:120])

# --- A17D-11: the living record.
check("A17D-11_decisions_recorded", "## D-137" in decisions and "A11X-v0" in decisions and "## D-136" in decisions and "17E" in decisions, "DECISIONS")
check("A17D-11_execution_index", "[x] **17D — Accessibility**" in read(EXECUTION_INDEX) and "[x] 17D — Accessibility" in read(MASTER_PLAN)
      and "17E — Kalan UX parçaları" in read(EXECUTION_INDEX) and "17E — Kalan UX parçaları" in read(MASTER_PLAN), "17D checked, 17E added")
check("A17D-11_spec_accepted", "**Decision:** `D-137`" in spec, "spec")
mut = contract.get("mutation_results_kotlin") if isinstance(contract.get("mutation_results_kotlin"), dict) else {}
vmut = contract.get("mutation_results_validator") if isinstance(contract.get("mutation_results_validator"), dict) else {}
check("A17D-11_kotlin_mutation_all_detected", mut.get("total", 0) > 0 and mut.get("detected") == mut.get("total"), f"{mut.get('detected')}/{mut.get('total')}")
check("A17D-11_validator_mutation_all_detected", vmut.get("total", 0) > 0 and vmut.get("detected") == vmut.get("total"), f"{vmut.get('detected')}/{vmut.get('total')}")

report = {"model": "A11X-v0", "stage_step": "17D", "decision": "D-137", "result": "PASS" if not failures else "FAIL",
          "checks_total": len(results), "checks_failed": len(failures), "checks": results}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, allow_unicode=True, sort_keys=False, width=200), encoding="utf-8", newline="\n")
print(f"17D QA: {len(results) - len(failures)}/{len(results)} PASS")
for f in failures:
    print("FAIL", f)
sys.exit(1 if failures else 0)
