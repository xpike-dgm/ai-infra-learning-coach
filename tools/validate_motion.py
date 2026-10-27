"""Independent 17B QA — MOTX-v0, motion.

The rules are read from `VDSX-v0` §10 (four purpose classes, duration bands, forbidden motion, reduced motion) and §12
(motion never the only carrier of a change), and compared with the real Kotlin, the tests, the spec, the contract and
DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a motion that repeats, pulses, counts down or
celebrates; a motion long enough to delay the next action; an animation made outside the one motion helper, with a
duration nobody declared; a motion that is the only sign of a change; a motion that stands before the exit of a focused
flow; and a platform reduced-motion setting that is not honoured.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/17b_motion/motion.yaml"
QA_OUT = ROOT / "arch/17b_motion/qa_report.yaml"
SPEC = ROOT / "docs/MOTION_SPEC.md"
RESEARCH = ROOT / "research/17b_motion_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
VDSX = ROOT / "docs/DESIGN_SYSTEM_SPEC.md"
EXECUTION_INDEX = ROOT / "docs/EXECUTION_INDEX.md"
MASTER_PLAN = ROOT / "docs/MASTER_PLAN.md"
UI = ANDROID / "app-ui/src/main/kotlin/coach/ui"
CORE = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/Motion.kt"
UI_MOTION = UI / "Motion.kt"
THEME = UI / "CoachTheme.kt"
SHELL = UI / "AppShell.kt"
COMPONENTS = UI / "Components.kt"
RUNNER = UI / "TaskRunnerScreen.kt"
TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/MotionTest.kt"

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def block(src: str, start: str) -> str:
    i = src.find(start)
    if i < 0:
        return ""
    open_ch = start[-1]
    close_ch = {"(": ")", "{": "}"}[open_ch]
    depth = 0
    for j in range(i + len(start) - 1, len(src)):
        if src[j] == open_ch:
            depth += 1
        elif src[j] == close_ch:
            depth -= 1
            if depth == 0:
                return src[i:j + 1]
    return ""


def body(src: str, start: str) -> str:
    head = block(src, start)
    if not head:
        return ""
    i = src.find(start) + len(head)
    j = src.find("{", i)
    return head + block(src[j:], "{") if j >= 0 else head


contract = yaml.safe_load(read(CONTRACT)) or {}
core = read(CORE)
ui_motion = read(UI_MOTION)
theme = read(THEME)
shell = read(SHELL)
components = read(COMPONENTS)
runner = read(RUNNER)
test = read(TEST)
vdsx = read(VDSX)
spec = read(SPEC)
decisions = read(DECISIONS)


def code_only(src: str) -> str:
    """Kotlin without its comments, so prose about what is forbidden is not read as doing it."""
    src = re.sub(r"/\*.*?\*/", "", src, flags=re.S)
    src = re.sub(r"^import [^\n]*\n", "", src, flags=re.M)
    return re.sub(r"//[^\n]*", "", src)


# Every Compose source of the app shell: app-ui, recursively, and the wiring's activity.
ui_files = {str(f.relative_to(ANDROID)): code_only(read(f)) for f in sorted(UI.rglob("*.kt"))}
ui_files.update({str(f.relative_to(ANDROID)): code_only(read(f))
                 for f in sorted((ANDROID / "app-wiring/src/main/kotlin").rglob("*.kt"))})
MOTION_KEY = str(UI_MOTION.relative_to(ANDROID))
THEME_KEY = str(THEME.relative_to(ANDROID))

# --- A17B-01: everything the step claims exists.
missing = [str(p.relative_to(ROOT)) for p in (CONTRACT, SPEC, RESEARCH, CORE, UI_MOTION, TEST) if not p.is_file()]
check("A17B-01_files_exist", not missing, str(missing))
check("A17B-01_contract_identity", contract.get("model") == "MOTX-v0" and contract.get("decision") == "D-134"
      and contract.get("stage_step") == "17B", f"{contract.get('model')} {contract.get('decision')}")

# --- A17B-02: the purposes are VDSX-v0 §10.2's four.
m = re.search(r"## 10\.2 Purpose classes\s+```text\n(.*?)```", vdsx, re.S)
vdsx_purposes = [line.split("#")[0].strip() for line in m.group(1).splitlines() if line.strip()] if m else []
purposes = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\),?$', body(core, "enum class MotionPurpose("), re.M)
check("A17B-02_purposes_are_vdsx", vdsx_purposes and purposes == vdsx_purposes, f"{purposes} vs {vdsx_purposes}")

# --- A17B-03: every band is short, feedback is the briefest, nothing exceeds the ceiling.
bands = re.findall(r"MotionPurpose\.([A-Z_]+) to MotionSpec\(MotionPurpose\.([A-Z_]+), durationMs = (\d+)", core)
longest = re.search(r"const val LONGEST_MS = (\d+)", core)
longest_ms = int(longest.group(1)) if longest else 10_000
check("A17B-03_one_band_per_purpose", sorted(a for a, b, _ in bands) == sorted(p.upper() for p in purposes) and all(a == b for a, b, _ in bands),
      str(bands))
check("A17B-03_ceiling_is_short", longest_ms <= 300 and all(0 < int(ms) <= longest_ms for _, _, ms in bands), f"{bands} ceiling {longest_ms}")
feedback = [int(ms) for a, _, ms in bands if a == "FEEDBACK"]
check("A17B-03_feedback_briefest", feedback and all(feedback[0] <= int(ms) for _, _, ms in bands) and feedback[0] <= 150, str(feedback))

# --- A17B-04: reduced motion follows the platform and makes every change immediate.
check("A17B-04_reduced_is_immediate", "return if (reduced) band.copy(durationMs = 0) else band" in core, "MotionPolicy.spec")
check("A17B-04_platform_setting_read", "Settings.Global.ANIMATOR_DURATION_SCALE" in ui_motion
      and "ReducedMotion.of(" in ui_motion and "fun of(animatorDurationScale: Float): Boolean = animatorDurationScale <= 0f" in core,
      "ANIMATOR_DURATION_SCALE")
check("A17B-04_theme_provides_it", "LocalReducedMotion provides platformReducedMotion()" in theme, "CoachTheme")
spec_fn = body(ui_motion, "fun <T> motionSpec(")
check("A17B-04_zero_is_snap", "MotionPolicy.spec(purpose, LocalReducedMotion.current)" in spec_fn and "return snap()" in spec_fn, "motionSpec")

# --- A17B-05: one place makes animations; nothing repeats, pulses, counts down or travels.
raw = {name: re.findall(r"\b(tween|spring|keyframes|snap)\s*(?:<[^>]*>)?\s*\(", src) for name, src in ui_files.items() if name != MOTION_KEY}
raw = {k: v for k, v in raw.items() if v}
check("A17B-05_animation_specs_only_in_motion_helper", not raw, str(raw))
repeating = [name for name, src in ui_files.items()
             if re.search(r"infiniteRepeatable|rememberInfiniteTransition|repeatable\s*(?:<[^>]*>)?\s*\(|RepeatMode|withInfiniteAnimationFrame|withFrameNanos|while\s*\(\s*(?:true|isActive)\s*\)", src)]
check("A17B-05_nothing_repeats", not repeating, str(repeating))
ANIMATION_API = (r"\b(animate\w*AsState|animateContentSize|AnimatedVisibility|AnimatedContent|Crossfade|Animatable|updateTransition|"
                 r"animateTo|animate\s*\(|animateItem|animateEnterExit|ValueAnimator|ObjectAnimator|fadeIn|fadeOut|slideIn\w*|slideOut\w*|"
                 r"scaleIn|scaleOut|expandIn|expandVertically|expandHorizontally|shrink\w+)\b")
animate_calls = {name: re.findall(ANIMATION_API, src) for name, src in ui_files.items()}
outside = {k: v for k, v in animate_calls.items() if v and k not in (MOTION_KEY, THEME_KEY)}
check("A17B-05_animations_only_through_helpers", not outside, str(outside))
motion_apis = sorted(set(animate_calls.get(MOTION_KEY, [])))
check("A17B-05_motion_helper_uses_only_fade_and_size", motion_apis == ["Animatable", "animateContentSize", "animateTo"], str(motion_apis))
theme_apis = animate_calls.get(THEME_KEY, [])
chip = body(theme, "fun StateChip(")
check("A17B-05_theme_animates_only_the_chip_colour", theme_apis == ["animateColorAsState"] and "animateColorAsState(" in chip
      and "motionSpec(MotionPurpose.STATE_CHANGE)" in chip, str(theme_apis))
check("A17B-05_chip_restarts_for_a_new_subject", "subject: Any? = null" in chip and ") = key(subject) {" in theme, "StateChip subject")
travel = [name for name, src in ui_files.items()
          if re.search(r"scaleX|scaleY|rotationZ|rotationX|rotationY|translationY|translationX|Modifier\.scale\(|\.scale\(|\.rotate\(|\.offset\s*\{", src)]
check("A17B-05_no_scale_rotation_or_travel", not travel, str(travel))

# --- A17B-06: nothing celebrates.
check("A17B-06_no_celebration", "const val CELEBRATION_ALLOWED = false" in core
      and not any(re.search(r"(?i)confetti|celebrat|lottie|firework", src) for src in ui_files.values()), "no celebration")
forbidden_ids = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\),?$', body(core, "enum class ForbiddenMotion("), re.M)
check("A17B-06_forbidden_named", len(forbidden_ids) == 7 and "celebration_on_completion" in forbidden_ids and "blocks_exit" in forbidden_ids,
      str(forbidden_ids))

# --- A17B-07: motion is never the only sign of a change, and never stands before the exit.
arrive = body(ui_motion, "fun ArriveOnChange(")
check("A17B-07_arrival_only_fades", "this.alpha = alpha.value" in arrive and "pointerInput" not in arrive and "delay(" not in arrive
      and not re.search(r"scale|rotation|translation|offset", arrive), "ArriveOnChange")
check("A17B-07_reduced_arrival_visible_from_first_frame", "Animatable(if (reduced) 1f else 0f)" in arrive
      and "if (!reduced) alpha.animateTo(1f, spec)" in arrive and "val reduced = LocalReducedMotion.current" in arrive, "no blank frame")
result_part = runner.split("ArriveOnChange(stage.result, MotionPurpose.FEEDBACK)")
check("A17B-07_result_announced_outside_the_fade", len(result_part) == 2
      and "Box(Modifier.semantics { liveRegion = LiveRegionMode.Polite; contentDescription = stage.result })" in result_part[0][-300:]
      and "clearAndSetSemantics" in result_part[1][:300], "the live region is not on the faded node")
# Narrowed at 17D: the label is the chip leaf's spoken name instead of a stateDescription.
check("A17B-07_chip_text_is_not_animated", "text = label" in chip and ("stateDescription = label" in chip or "clearAndSetSemantics { contentDescription = label }" in chip), "the label says the state")
focused = shell.split("if (!state.showsShell) {")[1].split("return")[0] if "if (!state.showsShell) {" in shell else ""
check("A17B-07_focused_flow_never_faded", focused and "ArriveOnChange" not in focused and "Box(modifier.fillMaxSize()) { content() }" in focused,
      "a focused flow's exit is visible from the first frame")
check("A17B-07_exit_first_in_the_runner", 'OutlinedButton(onClick = onExit, modifier = Modifier.fillMaxWidth().minimumTouchTarget())' in runner
      and "ArriveOnChange" not in runner.split('Text("Bugün\'e dön")')[0].split("fun TaskRunnerScreen(")[-1], "exit first, never behind motion")
check("A17B-07_destinations_orient", "content = { ArriveOnChange(state.surface.id, MotionPurpose.ORIENTATION) { content() } }" in shell, "AppShell")
check("A17B-07_panels_are_continuity", "fun Modifier.continuousSize(): Modifier = this.animateContentSize(animationSpec = motionSpec(MotionPurpose.CONTINUITY))" in ui_motion
      and ".continuousSize()" in components and "Column(Modifier.continuousSize()" in body(runner, "private fun HelpSection(")
      and "stage?.let { TaskRunInterior(it, actions) }" in runner, "only the help panel and grouping blocks grow")

# --- A17B-08: tests.
for name in ["the purposes are VDSX-v0's four", "nothing is long enough to delay the next action", "with motion reduced every change is immediate",
             "nothing celebrates"]:
    check(f"A17B-08_test_{name[:36]}", name in test, name)

# --- A17B-09: the living record.
check("A17B-09_decision_recorded", "## D-134" in decisions and "MOTX-v0" in decisions, "DECISIONS")
check("A17B-09_execution_index", "[x] **17B — Motion**" in read(EXECUTION_INDEX) and "[x] 17B — Motion" in read(MASTER_PLAN), "17B checked")
check("A17B-09_spec_accepted", "**Decision:** `D-134`" in spec, "spec")
mut = contract.get("mutation_results_kotlin", {})
vmut = contract.get("mutation_results_validator", {})
check("A17B-09_kotlin_mutation_all_detected", mut.get("total", 0) > 0 and mut.get("detected") == mut.get("total"), f"{mut.get('detected')}/{mut.get('total')}")
check("A17B-09_validator_mutation_all_detected", vmut.get("total", 0) > 0 and vmut.get("detected") == vmut.get("total"), f"{vmut.get('detected')}/{vmut.get('total')}")

report = {"model": "MOTX-v0", "stage_step": "17B", "decision": "D-134", "result": "PASS" if not failures else "FAIL",
          "checks_total": len(results), "checks_failed": len(failures), "checks": results}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, allow_unicode=True, sort_keys=False, width=200), encoding="utf-8", newline="\n")
print(f"17B QA: {len(results) - len(failures)}/{len(results)} PASS")
for f in failures:
    print("FAIL", f)
sys.exit(1 if failures else 0)
