"""17D (A11X-v0): an accessibility audit of the real app on an emulator, through the platform's accessibility tree.

It drives the installed debug app across its screens with adb (launch, tap by text, scroll) and reads each screen with
`uiautomator dump`, the tree an accessibility service such as TalkBack reads. A screen is read whole: it is scrolled in
steps shorter than its viewport until two readings are the same, and every reading is audited. On every screen:

- R1 every actionable node has an accessible name (its own text or content description, or one merged from inside it);
- R2 every actionable node is at least 48 x 48 dp (`VDSX-v0` §8, §12);
- R3 no identifier reaches the tree as text (snake_case words, logical ids such as `skill.x.y`, help codes H1-H4);
  code shown as code (a horizontally scrolling code block) is not prose and is not read for identifiers;
- R4 no image-only control: an actionable node whose only content is an icon with no name;
- R5 every checkable control (a radio, a switch) is named;
- R6 every text field is named by its label, never by the value typed into it;
- R7 no two different controls on a screen share one spoken name (three "Kaydet" buttons cannot be told apart), across
  all of the screen's scroll positions;
- R8 actionable nodes are in reading order: the tree's order runs down the screen.

A node cut by the viewport's edge is not judged where it is cut; it must be judged on a reading where it is fully
visible, and a cut node never seen whole is reported (`clipped_never_judged`). Reaching a screen is verified by a marker:
text that screen shows and the screen before it does not.

What the tree does not carry (headings, state descriptions, live regions, roles, errors) is checked in the code by
`tools/validate_accessibility.py`; what TalkBack actually says is heard only on a device with TalkBack.

It is not a JVM gate (it needs an emulator) and is not part of the `validate_*` sweep; its report is the evidence and
`tools/validate_accessibility.py` checks that the report exists, passed and covers the screens it names.

Usage: python -X utf8 tools/audit_a11y_emulator.py [--font-scale=2.0]   (an emulator with the debug APK installed must be
running; the app's data is cleared first)
"""

from __future__ import annotations

import os
import re
import subprocess
import sys
import time
from collections import Counter
from pathlib import Path
from xml.etree import ElementTree

import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT_DIR = ROOT / "arch/17d_accessibility"
ADB = os.path.join(os.environ.get("LOCALAPPDATA", ""), "Android", "Sdk", "platform-tools", "adb.exe")
PACKAGE = "coach.wiring"
MIN_DP = 48
IDENTIFIER = re.compile(r"^[a-z]+(?:_[a-z]+)+$|^(?:skill|topic|objective|item|task)\.[a-z_.]+|\bH[1-4]\b")
PUNCTUATION = "`'\".,;:!?()[]{}<>«»“”‘’"
RULES = ["R1_no_accessible_name", "R2_target_below_48dp", "R3_identifier_on_screen", "R4_image_only_control",
         "R5_checkable_unnamed", "R6_text_field_unnamed", "R7_ambiguous_duplicate_name", "R8_reading_order"]
MAX_READINGS = 60
SAME_NODE_PX = 40


def adb(*args: str, capture: bool = True) -> str:
    env = dict(os.environ, MSYS_NO_PATHCONV="1")
    p = subprocess.run([ADB, *args], capture_output=capture, text=True, encoding="utf-8", errors="replace", env=env)
    return p.stdout if capture else ""


def density() -> float:
    m = re.findall(r"(\d+)", adb("shell", "wm", "density"))
    return int(m[-1]) / 160 if m else 2.625


def dump() -> ElementTree.Element:
    for _ in range(4):
        adb("shell", "uiautomator", "dump", "/sdcard/a11y.xml")
        xml = adb("exec-out", "cat", "/sdcard/a11y.xml")
        if xml.strip().startswith("<?xml"):
            return ElementTree.fromstring(xml.encode("utf-8"))
        time.sleep(2)
    raise RuntimeError("uiautomator dump failed")


def bounds(node: ElementTree.Element) -> tuple[int, int, int, int]:
    x1, y1, x2, y2 = map(int, re.findall(r"\d+", node.get("bounds", "[0,0][0,0]")))
    return x1, y1, x2, y2


def texts(node: ElementTree.Element) -> list[str]:
    return [n.get("text", "") or n.get("content-desc", "") for n in node.iter()]


def name_of(node: ElementTree.Element) -> str:
    if node.get("class", "").endswith("EditText"):
        # A field is named by its label (inside it) or its hint, never by the value typed into it.
        inner = [t for c in node for t in texts(c) if t]
        return " ".join(inner).strip() or node.get("hint", "")
    return " ".join(t for t in texts(node) if t).strip()


def spoken_name(node: ElementTree.Element) -> str:
    """The name a screen reader speaks: the node's own content description when it has one, else its merged text."""
    return node.get("content-desc", "") or name_of(node)


def app_nodes(root: ElementTree.Element) -> list[ElementTree.Element]:
    return [n for n in root.iter("node") if n.get("package") == PACKAGE]


def present(root: ElementTree.Element, text: str) -> bool:
    return any(n.get("text") == text or n.get("content-desc") == text for n in root.iter("node"))


def tap_text(text: str, tries: int = 3) -> bool:
    # A screen may still be loading (or a system dialog may be passing), so a missing node is looked for again.
    for attempt in range(tries):
        for node in dump().iter("node"):
            if node.get("text") == text or node.get("content-desc") == text:
                x1, y1, x2, y2 = bounds(node)
                adb("shell", "input", "tap", str((x1 + x2) // 2), str((y1 + y2) // 2))
                return True
        if attempt < tries - 1:
            time.sleep(3)
    return False


def wait_for(text: str, limit: float = 180) -> bool:
    end = time.time() + limit
    while time.time() < end:
        if present(dump(), text):
            return True
        time.sleep(3)
    return False


def main_viewport(nodes: list[ElementTree.Element]) -> tuple[int, int, int, int] | None:
    scrollables = [bounds(n) for n in nodes if n.get("scrollable") == "true"]
    return max(scrollables, key=lambda b: b[3] - b[1], default=None)


def swipe(viewport: tuple[int, int, int, int] | None, up: bool) -> None:
    # Shorter than the viewport, so consecutive readings overlap and every node is seen whole somewhere.
    top, bottom = (viewport[1], viewport[3]) if viewport else (400, 2000)
    step = max(200, (bottom - top) // 2)
    x = str((viewport[0] + viewport[2]) // 2 if viewport else 540)
    if up:
        adb("shell", "input", "swipe", x, str(top + 150), x, str(top + 150 + step), "400")
    else:
        adb("shell", "input", "swipe", x, str(bottom - 150), x, str(bottom - 150 - step), "400")
    time.sleep(1.2)


def signature(nodes: list[ElementTree.Element]) -> tuple:
    return tuple((n.get("text", ""), n.get("content-desc", ""), n.get("bounds")) for n in nodes)


def read_screen() -> tuple[list[list[ElementTree.Element]], bool]:
    """Every reading of the current screen, top to bottom, scrolling until two readings are the same (its end)."""
    readings, previous = [], None
    for _ in range(MAX_READINGS):
        nodes = app_nodes(dump())
        sig = signature(nodes)
        if sig == previous:
            return readings, True
        readings.append(nodes)
        previous = sig
        swipe(main_viewport(nodes), up=False)
    return readings, False


def back_to_top() -> None:
    previous = None
    for _ in range(MAX_READINGS):
        nodes = app_nodes(dump())
        sig = signature(nodes)
        if sig == previous:
            return
        previous = sig
        swipe(main_viewport(nodes), up=True)


def tap_scrolling(text: str, limit: int = 15) -> bool:
    """Taps the node with this text, scrolling down until it is on screen (larger text pushes controls below the fold)."""
    for _ in range(limit):
        if tap_text(text, tries=1):
            return True
        swipe(main_viewport(app_nodes(dump())), up=False)
    return False


def offsets(readings: list[list[ElementTree.Element]], viewport) -> tuple[list[int], int]:
    """Each reading's scroll offset from the first, from the text nodes two consecutive readings share, and how many
    consecutive readings shared none (a swipe that may have skipped content: the screen is then not read whole)."""
    def unique(nodes):
        seen = Counter(n.get("text") for n in nodes if n.get("text"))
        return {n.get("text"): bounds(n)[1] for n in nodes if n.get("text") and seen[n.get("text")] == 1
                and viewport and viewport[1] < bounds(n)[1] < viewport[3]}

    result, unanchored = [0], 0
    for prev, cur in zip(readings, readings[1:]):
        a, b = unique(prev), unique(cur)
        deltas = Counter(a[t] - b[t] for t in a.keys() & b.keys())
        unanchored += not deltas
        result.append(result[-1] + (deltas.most_common(1)[0][0] if deltas else 0))
    return result, unanchored


def audit(screen: str, marker: dict, scale: float, scroll: bool = True) -> dict:
    root = dump()
    # A dialog is read once: a swipe that starts on its scrim would dismiss it.
    readings, read_to_end = read_screen() if scroll else ([app_nodes(dump())], True)
    # The screen shows its marker somewhere (larger text may push it below the fold), and the screen before it is gone.
    marker_found = (all(any(n.get("text") == t or n.get("content-desc") == t for r in readings for n in r) for t in marker["present"])
                    and not any(present(root, t) for t in marker["absent"]))
    viewport = main_viewport(readings[0]) if readings else None
    shift, unanchored = offsets(readings, viewport)
    findings: list[dict] = []
    seen_findings: set = set()
    judged: dict[str, list[tuple[str, int]]] = {}
    clipped: list[tuple[str, tuple[int, int, int, int]]] = []
    whole: list[tuple[str, tuple[int, int, int, int]]] = []
    actionable_total = 0

    def add(finding: dict) -> None:
        key = (finding["rule"], str(finding.get("node")))
        if key not in seen_findings:
            seen_findings.add(key)
            findings.append(finding)

    # A node at the same place, with the same name, in every reading did not scroll: it is fixed (the shell's tabs, a
    # focused flow's exit bar) and is judged where it is.
    fixed = set.intersection(*({(spoken_name(n), bounds(n)) for n in r} for r in readings)) if readings else set()

    def inside(b) -> bool:
        return viewport is not None and b[1] > viewport[1] + 2 and b[3] < viewport[3] - 2

    def is_clipped(n) -> bool:
        # The dump clips bounds to the window, not to the viewport, so scrolled content that touches or leaves the viewport
        # (even a row whose visible part is only above or below it) may be cut, in size and in name.
        b = bounds(n)
        return viewport is not None and not inside(b) and (spoken_name(n), b) not in fixed

    for k, nodes in enumerate(readings):
        code = [bounds(n) for n in nodes if n.get("scrollable") == "true" and bounds(n) != viewport]
        actionable = [n for n in nodes if n.get("clickable") == "true" or n.get("checkable") == "true" or n.get("long-clickable") == "true"]
        actionable_total = max(actionable_total, len(actionable))
        order: list[tuple[int, int]] = []
        for n in actionable:
            b = bounds(n)
            name, spoken = name_of(n), spoken_name(n)
            scrolled = inside(b)
            if is_clipped(n):
                # Its visible part, placed in the screen's content; its name may be cut too, so it is matched by place.
                clipped.append((spoken, (b[0], b[1] + shift[k], b[2], b[3] + shift[k])))
                continue
            if scrolled:
                whole.append((spoken, (b[0], b[1] + shift[k], b[2], b[3] + shift[k])))
            judged.setdefault(spoken, []).append(("content", b[1] + shift[k]) if scrolled else ("fixed", b[1]))
            w, h = (b[2] - b[0]) / scale, (b[3] - b[1]) / scale
            label = (name or n.get("class", ""))[:60]
            if not name:
                add({"rule": "R5_checkable_unnamed" if n.get("checkable") == "true" else "R1_no_accessible_name", "node": label})
            if w < MIN_DP - 0.5 or h < MIN_DP - 0.5:
                add({"rule": "R2_target_below_48dp", "node": label, "size_dp": [round(w, 1), round(h, 1)]})
            if any(c.get("class", "").endswith("ImageView") for c in n.iter()) and not name:
                add({"rule": "R4_image_only_control", "node": label})
            if scrolled:
                order.append((b[1], b[3]))
        # R8: the tree's order of the viewport's controls runs down the screen (a control may share a row with the last).
        for (top_a, _), (top_b, bottom_b) in zip(order, order[1:]):
            if top_b < top_a - 4 and bottom_b <= top_a:
                add({"rule": "R8_reading_order", "node": f"a control at y={top_b} follows one at y={top_a}"})
        for n in nodes:
            if n.get("class", "").endswith("EditText") and not is_clipped(n) and not name_of(n):
                add({"rule": "R6_text_field_unnamed", "node": n.get("bounds")})
        for n in nodes:
            b = bounds(n)
            if any(c[0] <= b[0] and c[1] <= b[1] and b[2] <= c[2] and b[3] <= c[3] for c in code):
                continue
            for value in (n.get("text", ""), n.get("content-desc", "")):
                for part in re.split(r"[\s,·:]+", value):
                    part = part.strip(PUNCTUATION)
                    if part and IDENTIFIER.search(part):
                        add({"rule": "R3_identifier_on_screen", "node": value[:80]})
    # R7 over the whole screen: places of one spoken name more than a node's height apart are different controls.
    for spoken, places in judged.items():
        kinds = {kind for kind, _ in places}
        distinct = len(kinds) > 1
        for kind in kinds:
            ys = sorted(y for kk, y in places if kk == kind)
            distinct = distinct or any(b - a > SAME_NODE_PX for a, b in zip(ys, ys[1:]))
        if distinct:
            add({"rule": "R7_ambiguous_duplicate_name", "node": spoken[:60]})
    # A cut node is judged when a reading shows a whole node that contains its visible part and, when the cut part still
    # has a name, carries that name (a neighbouring row of the same width is not the same control).
    never = sorted({f"{name or '(adsız)'} @ y={c[1]}" for name, c in clipped
                    if not any(w[0] <= c[0] + 8 and w[1] <= c[1] + 8 and w[2] >= c[2] - 8 and w[3] >= c[3] - 8
                               and (not name or name in whole_name) for whole_name, w in whole)})
    if scroll:
        back_to_top()
    return {"screen": screen, "marker": marker, "marker_found": marker_found, "readings": len(readings), "read_to_end": read_to_end and unanchored == 0,
            "unanchored_readings": unanchored, "offsets": shift,
            "nodes": max((len(r) for r in readings), default=0), "actionable": actionable_total,
            "clipped_skipped": len(clipped), "clipped_never_judged": never, "findings": findings,
            **({} if marker_found else {"seen_at_top": [t for t in texts(root) if t][:15]})}


def main() -> int:
    font_scale = next((float(a.split("=", 1)[1]) for a in sys.argv[1:] if a.startswith("--font-scale=")), 1.0)
    scale = density()
    results: list[dict] = []
    adb("shell", "settings", "put", "system", "font_scale", str(font_scale))
    # A fresh store, so every screen (the run's question and help included) is reachable.
    adb("shell", "pm", "clear", PACKAGE)

    def visit(name: str, shown: list[str], absent: list[str] | None = None, wait: float = 3.0, scroll: bool = True) -> None:
        end = time.time() + 120
        while time.time() < end and not all(present(dump(), t) for t in shown):
            time.sleep(2)
        time.sleep(wait)
        results.append(audit(name, {"present": shown, "absent": absent or []}, scale, scroll))

    def go_back() -> None:
        back_to_top()
        tap_text("Geri")
        time.sleep(3)

    adb("shell", "monkey", "-p", PACKAGE, "-c", "android.intent.category.LAUNCHER", "1")
    # The first start opens the store and publishes the curriculum; the shell appears when it is done.
    if not wait_for("Öğren", 300):
        # A cold first start can outlast the wait once; the app is started again before giving up.
        adb("shell", "monkey", "-p", PACKAGE, "-c", "android.intent.category.LAUNCHER", "1")
        if not wait_for("Öğren", 300):
            raise RuntimeError("the app's shell did not appear")
    time.sleep(30)
    visit("today_overview", ["Başla"], ["Öğrenme haritası"])
    if tap_scrolling("Bugünkü süreyi değiştir"):
        visit("today_capacity_dialog", ["Bugün ne kadar zamanın var?"], None, 3.0, scroll=False)
        tap_text("Vazgeç")
        time.sleep(2)
    back_to_top()
    if tap_scrolling("Bu plan neden böyle?"):
        visit("planner_explanation", ["Bu plan neden böyle?", "Geri"], ["Başla"])
        go_back()
    if tap_text("Öğren"):
        visit("learn_overview", ["Öğrenme haritası"], None, 20)
        if tap_scrolling("Programın çalışması"):
            visit("topic_detail", ["Geri"], ["Öğrenme haritası"], 10)
            go_back()
    if tap_text("İlerleme"):
        visit("progress_overview", ["Kanıtlanmış yetkinlik"], None, 20)
        for target, surface in (("Öğrenme geçmişi", "learning_history"), ("Ölçme raporu", "assessment_report"),
                                ("Teknik İngilizce profili", "technical_english_profile")):
            back_to_top()
            if tap_scrolling(target):
                visit(surface, ["Geri", target], ["Kanıtlanmış yetkinlik"], 12)
                go_back()
    if tap_text("Profil"):
        visit("profile_overview", ["Günlük süre", "Yedekleme"], ["Kanıtlanmış yetkinlik", "Öğrenme haritası"])
    if tap_text("Bugün") and wait_for("Başla", 30) and tap_scrolling("Başla"):
        visit("task_runner_orientation", ["Bu görevdeki cevaplarını nasıl veriyorsun?"])
        if tap_scrolling("Kendim") and tap_scrolling("Başla"):
            visit("task_runner_lesson", ["Sorulara geç"], ["Bu görevdeki cevaplarını nasıl veriyorsun?"])
            if tap_scrolling("Sorulara geç", 25):
                visit("task_runner_question", ["Soru 1"], ["Sorulara geç"])
                if tap_scrolling("Yardım iste"):
                    visit("task_runner_help", ["Yardımı kapat"])
    adb("shell", "settings", "put", "system", "font_scale", "1.0")
    report = {
        "step": "17D",
        "font_scale": font_scale,
        "model": "A11X-v0",
        "device": adb("shell", "getprop", "ro.product.model").strip() + " API " + adb("shell", "getprop", "ro.build.version.sdk").strip(),
        "density_scale": scale,
        "rules": RULES,
        "screens": results,
        "findings_total": sum(len(r["findings"]) for r in results),
        "result": "PASS" if results and not any(r["findings"] or r["clipped_never_judged"] or not r["marker_found"] or not r["read_to_end"] for r in results) else "FAIL",
    }
    out = REPORT_DIR / ("a11y_emulator_report.yaml" if font_scale == 1.0 else f"a11y_emulator_report_text_{int(font_scale * 100)}.yaml")
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(yaml.safe_dump(report, allow_unicode=True, sort_keys=False, width=200), encoding="utf-8", newline="\n")
    print(f"17D emulator a11y audit: {report['result']} — {len(results)} screens, {report['findings_total']} findings")
    for r in results:
        if not r["marker_found"]:
            print(r["screen"], "MARKER NOT FOUND", r["marker"])
        if not r["read_to_end"]:
            print(r["screen"], "NOT READ TO THE END")
        if r["clipped_never_judged"]:
            print(r["screen"], "CLIPPED NEVER JUDGED", r["clipped_never_judged"])
        for f in r["findings"]:
            print(r["screen"], f)
    return 0 if report["result"] == "PASS" else 1


if __name__ == "__main__":
    sys.exit(main())
