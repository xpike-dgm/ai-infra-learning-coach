"""Independent 17C QA — USBX-v0, run usability.

The rules are read from `TRUX-v0` (8C: the focused-flow exit, one action from every state), `VDSX-v0` §11.1 (a
progress-shaped quantity only for a bounded factual position, never competence) and the 14F answer-key rule (a short
answer is compared as typed), and compared with the real Kotlin, the resources, the tests, the spec, the contract and
DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: an exit that scrolls out of view; a position that is
a score, a share or a bar; a keyboard that rewrites an answer, code or a test report; an assistance level shown as a
code; a reminder with a borrowed system icon, an unmeasured accent or no reminder category.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/17c_usability/usability.yaml"
QA_OUT = ROOT / "arch/17c_usability/qa_report.yaml"
SPEC = ROOT / "docs/USABILITY_SPEC.md"
RESEARCH = ROOT / "research/17c_usability_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
EXECUTION_INDEX = ROOT / "docs/EXECUTION_INDEX.md"
MASTER_PLAN = ROOT / "docs/MASTER_PLAN.md"
CORE = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/RunUsability.kt"
TOKENS = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/DesignTokens.kt"
HISTORY = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/HistoryPresentation.kt"
TASK_RUN = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TaskRunPresentation.kt"
RUNNER = ANDROID / "app-ui/src/main/kotlin/coach/ui/TaskRunnerScreen.kt"
COMPONENTS = ANDROID / "app-ui/src/main/kotlin/coach/ui/Components.kt"
SESSION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/TaskRunSession.kt"
REMINDERS = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/Reminders.kt"
ICON = ANDROID / "app-wiring/src/main/res/drawable/ic_stat_today.xml"
TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/RunUsabilityTest.kt"
HISTORY_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/HistoryPresentationTest.kt"

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


contract = yaml.safe_load(read(CONTRACT)) or {}
core = read(CORE)
runner = code_only(read(RUNNER))
components = read(COMPONENTS)
session = read(SESSION)
reminders = code_only(read(REMINDERS))
icon = read(ICON)
history = read(HISTORY)
task_run = read(TASK_RUN)
test = read(TEST)
spec = read(SPEC)
decisions = read(DECISIONS)

# --- A17C-01: everything the step claims exists.
missing = [str(p.relative_to(ROOT)) for p in (CONTRACT, SPEC, RESEARCH, CORE, ICON, TEST) if not p.is_file()]
check("A17C-01_files_exist", not missing, str(missing))
check("A17C-01_contract_identity", contract.get("model") == "USBX-v0" and contract.get("decision") == "D-135"
      and contract.get("stage_step") == "17C", f"{contract.get('model')} {contract.get('decision')}")

# --- A17C-02: the exit stays in view — above the scrolling run, not inside it.
frame = runner.find("Column(modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing))")
exit_at = runner.find("OutlinedButton(onClick = onExit, modifier = Modifier.fillMaxWidth().minimumTouchTarget())")
scroll_at = runner.find("ScreenColumn(modifier = Modifier.weight(1f), insets = false)")
chip_at = runner.find("StateChip(")
check("A17C-02_exit_above_the_scrolling_run", 0 <= frame < exit_at < scroll_at < chip_at, f"{frame} {exit_at} {scroll_at} {chip_at}")
check("A17C-02_runner_has_one_scroll", runner.count("ScreenColumn(") == 1 and "verticalScroll" not in runner, "one scrolling column")
check("A17C-02_column_can_leave_insets_to_frame", "insets: Boolean = true" in components
      and ".then(if (insets) Modifier.windowInsetsPadding(WindowInsets.safeDrawing) else Modifier)" in components, "ScreenColumn")

# --- A17C-03: the position is a bounded fact, said in text.
check("A17C-03_position_rule", 'if (total != null && total >= number && total > 1) "Soru $number / $total" else "Soru $number"' in core, "RunPosition")
check("A17C-03_position_drawn", "SectionHeader(RunPosition.label(stage.number, stage.total)," in runner, "TaskRunnerScreen")
check("A17C-03_total_is_this_runs_questions", "total = run.items.size," in session and "val total: Int? = null," in task_run, "TaskRunSession")
bars = [p.name for p in (ANDROID / "app-ui/src/main/kotlin/coach/ui").glob("*.kt") if re.search(r"ProgressIndicator|Slider", code_only(read(p)))]
check("A17C-03_no_bar", not bars, str(bars))
check("A17C-03_position_words_clean", not re.search(r"%|puan|tamamla|kaldı", core.split("object RunPosition")[1].split("enum class AnswerField")[0], re.I), "RunPosition")

# --- A17C-04: the keyboard keeps exactly what was typed where the answer is compared as typed.
check("A17C-04_exact_fields", "fun exact(field: AnswerField): Boolean = field != AnswerField.OPEN" in core, "AnswerInput")
check("A17C-04_exact_keyboard", "if (AnswerInput.exact(field)) KeyboardOptions(capitalization = KeyboardCapitalization.None, autoCorrectEnabled = false)" in runner,
      "answerKeyboard")
# The independent review found that `autoCorrectEnabled = false` alone does not stop a keyboard correcting a multi-line
# field; the flag keyboards honour is NO_SUGGESTIONS, added by ExactTextInput after Compose fills the editor info.
exact_input = code_only(read(ANDROID / "app-ui/src/main/kotlin/coach/ui/ExactTextInput.kt"))
check("A17C-04_no_suggestions_flag_set", "fun noSuggestions(inputType: Int): Int = inputType or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS" in exact_input
      and "outAttributes.inputType = noSuggestions(outAttributes.inputType)" in exact_input
      and "val connection = request.createInputConnection(outAttributes)" in exact_input
      and exact_input.find("request.createInputConnection(outAttributes)") < exact_input.find("outAttributes.inputType = noSuggestions")
      and "if (!AnswerInput.exact(field)) {" in exact_input, "ExactTextInput")
# Every text field of the run is either wrapped for its kind or a named prose field sent to the tutor.
fields = [m.start() for m in re.finditer(r"OutlinedTextField\(", runner)]
PROSE = ("RunHelpCopy.QUESTION_LABEL", "RunHelpCopy.PRACTICE_ANSWER_LABEL")
unwrapped = []
for at in fields:
    window = runner[at:at + 600]
    before = runner[max(0, at - 80):at]
    wrapped = "ExactTextInput(" in before and "keyboardOptions = answerKeyboard(" in window
    prose = any(p in window[:300] for p in PROSE)
    if not (wrapped or prose):
        unwrapped.append(runner[at:at + 80])
check("A17C-04_every_field_has_its_keyboard", len(fields) >= 5 and not unwrapped
      and "ExactTextInput(field) { OutlinedTextField(" in runner and "ExactTextInput(AnswerField.CODE) { OutlinedTextField(" in runner
      and "ExactTextInput(AnswerField.REPORT) { OutlinedTextField(" in runner, f"{len(fields)} fields, unwrapped {unwrapped}")
# Both builds: the AI build uses its own full copy of the manifest, not the main one.
manifests = [read(ANDROID / "app-wiring/src/main/AndroidManifest.xml"), read(ANDROID / "app-wiring/src/withAi/AndroidManifest.xml")]
check("A17C-02_window_resizes_for_the_keyboard", all('android:windowSoftInputMode="adjustResize"' in m for m in manifests),
      "the exit stays on screen while typing, in the AI and the no-AI build")
check("A17C-03_position_spoken_in_words", "contentDescription = RunPosition.spoken(stage.number, stage.total)" in runner
      and '"Soru $number, toplam $total soru"' in core, "no fraction for a screen reader")

# --- A17C-05: the history says what help was shown, in words.
check("A17C-05_history_assistance_in_words", "AssistanceWords.of(entry.assistance)" in history and "joinToString(\", \") { it.id }" not in history,
      "HistoryPresentation")
words = dict(re.findall(r"AssistanceLevel\.(H\d) to \"([^\"]+)\"", core))
check("A17C-05_every_level_a_word", sorted(words) == ["H1", "H2", "H3", "H4"] and not any(re.search(r"H\d", w) for w in words.values()), str(words))

# --- A17C-06: the reminder is the app's own, measured, and filed as a reminder.
check("A17C-06_own_monochrome_icon", "setSmallIcon(R.drawable.ic_stat_today)" in reminders and "android.R.drawable" not in reminders
      and '<vector' in icon and re.findall(r'fillColor="(#[0-9A-F]+)"', icon) == ["#FFFFFFFF"], "ic_stat_today")
check("A17C-06_reminder_category", "setCategory(Notification.CATEGORY_REMINDER)" in reminders, "category")
check("A17C-06_accent_is_measured", 'DesignTokens.hex(Theme.LIGHT, "tone_active")' in reminders and "setColor(REMINDER_ACCENT)" in reminders
      and not re.search(r"setColor\(0x|Color\.parseColor", reminders), "accent")

# --- A17C-07: tests and narrowing.
for name in ["the position is a bounded fact", "answers, code and reports keep exactly what was typed",
             "the history names assistance in words"]:
    check(f"A17C-07_test_{name[:32]}", name in test, name)
check("A17C-07_narrowed_marked", "Narrowed at 17C" in read(HISTORY_TEST), "HistoryPresentationTest")

# --- A17C-08: the scope the user chose, and what stays open.
open_loops = " ".join(str(x) for x in contract.get("not_in_scope", []))
check("A17C-08_unchosen_items_named", all(w in open_loops for w in ("day summary", "edges", "usage")), "not_in_scope")

# --- A17C-09: the living record.
check("A17C-09_decision_recorded", "## D-135" in decisions and "USBX-v0" in decisions, "DECISIONS")
check("A17C-09_execution_index", "[x] **17C — Kullanılabilirlik**" in read(EXECUTION_INDEX) and "[x] 17C — Kullanılabilirlik" in read(MASTER_PLAN), "17C checked")
check("A17C-09_spec_accepted", "**Decision:** `D-135`" in spec, "spec")
mut = contract.get("mutation_results_kotlin", {})
vmut = contract.get("mutation_results_validator", {})
check("A17C-09_kotlin_mutation_all_detected", mut.get("total", 0) > 0 and mut.get("detected") == mut.get("total"), f"{mut.get('detected')}/{mut.get('total')}")
check("A17C-09_validator_mutation_all_detected", vmut.get("total", 0) > 0 and vmut.get("detected") == vmut.get("total"), f"{vmut.get('detected')}/{vmut.get('total')}")

report = {"model": "USBX-v0", "stage_step": "17C", "decision": "D-135", "result": "PASS" if not failures else "FAIL",
          "checks_total": len(results), "checks_failed": len(failures), "checks": results}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, allow_unicode=True, sort_keys=False, width=200), encoding="utf-8", newline="\n")
print(f"17C QA: {len(results) - len(failures)}/{len(results)} PASS")
for f in failures:
    print("FAIL", f)
sys.exit(1 if failures else 0)
