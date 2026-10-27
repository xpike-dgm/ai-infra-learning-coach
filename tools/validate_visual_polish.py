"""Independent 17A QA — VPLX-v0, visual polish.

The rules are read from the accepted contracts — `VDSX-v0` (8F: type roles, the 4dp rhythm, tones, marks, restricted
shapes), `WFPX-v0` (8G: the measured palette), `DSIX-v0` (10C: tokens in core, contrast recomputed, fault tone only on
Material's error role, no dynamic colour), `UXIA-v0` (8A: `learn_overview`, browse is not prerequisite), `NSHX-v0` (10B:
the accepted edges) — and compared with the real Kotlin, the shipped packages, the tests, the spec, the contract, the
emulator report and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a Material default colour or type style reaching the
screen; a drawn pair or a chip colour below the WCAG anchor in either theme; code drawn in the body font, or an authored
line changed to draw it; a chip that reads as a button or says its state by colour alone; an identifier on screen; spacing
off the declared rhythm; a Learn map that claims an order, a count or a completion, or contradicts Progress; a first
launch that crashes; a screen drawn under the system bars or on an unmeasured window.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/17a_visual_polish/visual_polish.yaml"
EMULATOR = ROOT / "arch/17a_visual_polish/emulator_report.yaml"
SCREENS = ROOT / "arch/17a_visual_polish/screens"
QA_OUT = ROOT / "arch/17a_visual_polish/qa_report.yaml"
SPEC = ROOT / "docs/VISUAL_POLISH_SPEC.md"
RESEARCH = ROOT / "research/17a_visual_polish_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
VDSX = ROOT / "docs/DESIGN_SYSTEM_SPEC.md"
EXECUTION_INDEX = ROOT / "docs/EXECUTION_INDEX.md"
MASTER_PLAN = ROOT / "docs/MASTER_PLAN.md"
ASSETS = ANDROID / "app-wiring/src/main/assets"

P = ANDROID / "core-presentation/src/main/kotlin/coach/presentation"
UI = ANDROID / "app-ui/src/main/kotlin/coach/ui"
VISUAL = P / "VisualSystem.kt"
TOKENS = P / "DesignTokens.kt"
TONE = P / "Tone.kt"
TEXT_LAYOUT = P / "TextLayout.kt"
NAMES = P / "CurriculumNames.kt"
LEARN = P / "LearnPresentation.kt"
HEALTH = P / "AppHealth.kt"
TOPIC_PRESENTATION = P / "TopicPresentation.kt"
NAVIGATION = P / "Navigation.kt"
THEME = UI / "CoachTheme.kt"
COMPONENTS = UI / "Components.kt"
LEARN_SCREEN = UI / "LearnScreen.kt"
TODAY_SCREEN = UI / "TodayScreen.kt"
RUNNER_SCREEN = UI / "TaskRunnerScreen.kt"
END_OF_DAY = UI / "EndOfDayView.kt"
APP_SHELL = UI / "AppShell.kt"
TOPIC_SCREEN = UI / "TopicScreen.kt"
ACTIVITY = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
THEMES_LIGHT = ANDROID / "app-wiring/src/main/res/values/themes.xml"
THEMES_DARK = ANDROID / "app-wiring/src/main/res/values-night/themes.xml"
PORTS = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
FILE_CONTENT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
TOPIC_QUERY = ANDROID / "core-application/src/main/kotlin/coach/application/TopicQuery.kt"
TOPIC_FACTS = ANDROID / "core-model/src/main/kotlin/coach/TopicFacts.kt"
TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/VisualPolishTest.kt"
COURSE_TEST = ANDROID / "app-wiring/src/test/kotlin/coach/wiring/VisualPolishCourseTest.kt"
NARROWED = [
    ANDROID / "core-presentation/src/test/kotlin/coach/presentation/ExplanationCopyTest.kt",
    ANDROID / "core-presentation/src/test/kotlin/coach/presentation/PlannerExplanationTest.kt",
    ANDROID / "core-presentation/src/test/kotlin/coach/presentation/VirtualUserExplanationsTest.kt",
]

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def block(src: str, start: str) -> str:
    """The text from `start` to its matching closing brace or parenthesis."""
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
    """A declaration from `start` (which opens a parameter list) through the end of its `{ ... }` body."""
    head = block(src, start)
    if not head:
        return ""
    i = src.find(start) + len(head)
    j = src.find("{", i)
    return head + block(src[j:], "{") if j >= 0 else head


def pairs(src: str) -> dict[str, str]:
    return dict(re.findall(r'"([^"\n]+)" to "([^"\n]+)"', src))


def pair_list(src: str) -> list[tuple[str, str]]:
    return re.findall(r'"([A-Za-z_]+)" to "([A-Za-z_]+)"', src)


# WCAG 2.2 relative luminance, recomputed here and not read from the Kotlin.
def _channel(v: int) -> float:
    c = v / 255
    return c / 12.92 if c <= 0.03928 else ((c + 0.055) / 1.055) ** 2.4


def luminance(hex_color: str) -> float:
    h = hex_color.lstrip("#")
    r, g, b = (int(h[i:i + 2], 16) for i in (0, 2, 4))
    return 0.2126 * _channel(r) + 0.7152 * _channel(g) + 0.0722 * _channel(b)


def contrast(a: str, b: str) -> float:
    la, lb = luminance(a), luminance(b)
    return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)


contract = yaml.safe_load(read(CONTRACT)) or {}
visual = read(VISUAL)
tokens_src = read(TOKENS)
text_layout = read(TEXT_LAYOUT)
names_src = read(NAMES)
learn_src = read(LEARN)
health_src = read(HEALTH)
topic_presentation = read(TOPIC_PRESENTATION)
navigation = read(NAVIGATION)
theme_src = read(THEME)
components = read(COMPONENTS)
learn_screen = read(LEARN_SCREEN)
today_screen = read(TODAY_SCREEN)
runner_screen = read(RUNNER_SCREEN)
end_of_day = read(END_OF_DAY)
app_shell = read(APP_SHELL)
topic_screen = read(TOPIC_SCREEN)
activity = read(ACTIVITY)
ports = read(PORTS)
file_content = read(FILE_CONTENT)
topic_query = read(TOPIC_QUERY)
topic_facts = read(TOPIC_FACTS)
test_src = read(TEST)
course_test = read(COURSE_TEST)
spec = read(SPEC)
decisions = read(DECISIONS)
vdsx = read(VDSX)

# --- A17A-01: everything the step claims exists.
required = [CONTRACT, EMULATOR, SPEC, RESEARCH, VISUAL, TEXT_LAYOUT, NAMES, LEARN, THEME, COMPONENTS, LEARN_SCREEN, TEST,
            COURSE_TEST, THEMES_LIGHT, THEMES_DARK]
missing = [str(p.relative_to(ROOT)) for p in required if not p.is_file()]
check("A17A-01_files_exist", not missing, f"missing: {missing}")
check("A17A-01_contract_identity", contract.get("model") == "VPLX-v0" and contract.get("decision") == "D-133"
      and contract.get("stage_step") == "17A", f"{contract.get('model')} {contract.get('decision')}")

# --- A17A-02: the type roles are VDSX-v0 §6.1's eight, and every Material slot maps to one of them.
m = re.search(r"## 6\.1 Roles\s+```text\n(.*?)```", vdsx, re.S)
vdsx_roles = m.group(1).split() if m else []
kotlin_roles = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\),?$', body(visual, "enum class TypeRole(") or "", re.M)
check("A17A-02_type_roles_are_vdsx", vdsx_roles and kotlin_roles == vdsx_roles, f"{kotlin_roles} vs {vdsx_roles}")
roles_block = block(visual, "val roles: Map<TypeRole, TypeSpec> = mapOf(")
specs = re.findall(r"TypeRole\.([A-Z_]+) to TypeSpec\(sizeSp = (\d+), lineHeightSp = (\d+), weight = (\d+)(, monospace = true)?\)", roles_block)
check("A17A-02_every_role_has_a_spec", len(specs) == 8 and all(int(s) >= 12 and int(lh) >= int(s) for _, s, lh, _, _ in specs),
      f"{len(specs)} specs")
check("A17A-02_only_mono_is_monospace", [r for r, _, _, _, mono in specs if mono] == ["MONO"], str(specs))
slots = re.findall(r'"([a-zA-Z]+)" to TypeRole\.([A-Z_]+)', block(visual, "val materialSlots: Map<String, TypeRole> = mapOf("))
material_slots = ["displayLarge", "displayMedium", "displaySmall", "headlineLarge", "headlineMedium", "headlineSmall",
                  "titleLarge", "titleMedium", "titleSmall", "bodyLarge", "bodyMedium", "bodySmall", "labelLarge",
                  "labelMedium", "labelSmall"]
check("A17A-02_every_material_slot_mapped_never_to_mono", [s for s, _ in slots] == material_slots
      and all(r != "MONO" for _, r in slots), str(slots))
theme_slots = re.findall(r'(\w+) = slot\("(\w+)"\)', theme_src)
check("A17A-02_theme_builds_every_slot_from_the_map", sorted(a for a, b in theme_slots if a == b) == sorted(material_slots),
      f"{len(theme_slots)} slots in the theme")
check("A17A-02_sizes_are_sp", "fontSize = spec.sizeSp.sp" in theme_src and "lineHeight = spec.lineHeightSp.sp" in theme_src,
      "roleStyle")
check("A17A-02_mono_is_monospace_family", "if (spec.monospace) FontFamily.Monospace" in theme_src, "roleStyle")

# --- A17A-03: the rhythm is VDSX-v0 §8's, and app-ui spaces with it alone.
m = re.search(r"declared step set of `([\d, ]+)`", vdsx)
vdsx_steps = [int(x) for x in m.group(1).split(",")] if m else []
steps = [int(x) for x in re.findall(r"const val [A-Z]+ = (\d+)", block(visual, "object Spacing {"))]
check("A17A-03_spacing_is_vdsx_rhythm", vdsx_steps and steps == vdsx_steps, f"{steps} vs {vdsx_steps}")
dp_literals = []
for f in sorted(UI.glob("*.kt")):
    for n, line in enumerate(read(f).splitlines(), 1):
        for lit in re.findall(r"\b(\d+)\.dp\b", line):
            dp_literals.append((f.name, n, lit, line.strip()))
allowed = [d for d in dp_literals if d[0] == "CoachTheme.kt" and d[2] == "1" and "val HAIRLINE = 1.dp" in d[3]]
check("A17A-03_no_dp_literal_but_the_hairline", len(dp_literals) == 1 and len(allowed) == 1,
      f"{[(d[0], d[1], d[2]) for d in dp_literals]}")
check("A17A-03_hairline_is_only_a_border", theme_src.count("HAIRLINE") == 2 and "BorderStroke(HAIRLINE" in theme_src, "HAIRLINE")

# --- A17A-04: every Material colour role is a measured token in both themes.
light = dict(re.findall(r'"(\w+)" to "(#[0-9A-F]{6})"', block(tokens_src, "private val light: Map<String, String> = mapOf(")))
dark = dict(re.findall(r'"(\w+)" to "(#[0-9A-F]{6})"', block(tokens_src, "private val dark: Map<String, String> = mapOf(")))
roles = pairs(block(visual, "val roles: Map<String, String> = mapOf("))
check("A17A-04_thirty_six_roles", len(roles) == 36, f"{len(roles)}")
check("A17A-04_every_role_a_measured_token", roles and all(t in light and t in dark for t in roles.values()),
      f"unmeasured: {[r for r, t in roles.items() if t not in light or t not in dark]}")
check("A17A-04_fault_tone_is_error_alone", sorted(r for r, t in roles.items() if t == "tone_fault") == ["error", "errorContainer"],
      str(sorted(r for r, t in roles.items() if t == "tone_fault")))
copied = set(re.findall(r'(\w+) = role\("(\w+)"\)', theme_src))
copied_names = {a for a, b in copied if a == b}
expected_copied = set(roles) - {"error", "onError"}
check("A17A-04_theme_copies_every_role", copied_names == expected_copied,
      f"missing: {sorted(expected_copied - copied_names)} extra: {sorted(copied_names - expected_copied)}")
check("A17A-04_error_is_fault", 'val fault = role("error")' in theme_src and "error = fault" in theme_src
      and "onError = onFault" in theme_src, "error role")
dynamic = [str(p.relative_to(ROOT)) for p in ANDROID.rglob("*.kt") if re.search(r"dynamic(Light|Dark)ColorScheme", read(p))]
check("A17A-04_no_dynamic_colour", not dynamic, str(dynamic))

# --- A17A-05: every drawn pair is recomputed against the WCAG anchor, per theme.
text_pairs = pair_list(block(visual, "val textPairs: List<Pair<String, String>> = listOf("))
non_text = pair_list(block(visual, "val nonTextPairs: List<Pair<String, String>> = listOf("))
low = []
for name, theme in (("light", light), ("dark", dark)):
    for fg, bg in text_pairs:
        r = contrast(theme[roles[fg]], theme[roles[bg]])
        if r < 4.5:
            low.append((name, fg, bg, round(r, 2)))
    for fg, bg in non_text:
        r = contrast(theme[roles[fg]], theme[roles[bg]])
        if r < 3.0:
            low.append((name, fg, bg, round(r, 2)))
check("A17A-05_drawn_pairs_meet_the_anchor", len(text_pairs) == 19 and len(non_text) == 4 and not low,
      f"{len(text_pairs)} text, {len(non_text)} non-text, below: {low}")
# The pairs the screens actually draw are in the list.
for needed in [("onPrimary", "primary"), ("onSurface", "surface"), ("onSurface", "surfaceVariant"),
               ("onSurfaceVariant", "surface"), ("primary", "surface"), ("onSecondaryContainer", "secondaryContainer")]:
    check(f"A17A-05_pair_listed_{needed[0]}_on_{needed[1]}", needed in text_pairs, str(needed))
tone_tokens = re.findall(r'Tone\.(\w+) to \("(\w+)" to "(\w+)"\)', tokens_src)
chip_low = [(name, tok, s, round(contrast(theme[tok], theme[s]), 2))
            for name, theme in (("light", light), ("dark", dark)) for _, tok, _ in tone_tokens
            for s in ("surface", "surface_variant") if contrast(theme[tok], theme[s]) < 4.5]
check("A17A-05_chip_colour_reads_on_both_surfaces", len(tone_tokens) == 6 and not chip_low, str(chip_low))
check("A17A-05_chip_surfaces_declared", 'listOf("surface", "surface_variant")' in visual
      and "DesignTokens.toneTokens.getValue(tone).first" in visual, "ChipColors")
check("A17A-05_kotlin_recomputes_too", "MaterialColorRoles.textPairs.forEach" in test_src
      and "ChipColors.surfaces.forEach" in test_src and "Contrast.ratio" in test_src, "VisualPolishTest")

# --- A17A-06: a chip is a state, never a button, and never colour alone.
chip = body(theme_src, "fun StateChip(")
check("A17A-06_chip_outlined_not_filled", ".border(BorderStroke(HAIRLINE, color)" in chip and ".background(" not in chip, "StateChip")
check("A17A-06_chip_has_mark_and_text", "Icon(ToneMark.of(tone).icon(), contentDescription = null" in chip
      # Narrowed at 17D: the label is the chip leaf's spoken name instead of a stateDescription.
      and "text = label" in chip and ("stateDescription = label" in chip or "clearAndSetSemantics { contentDescription = label }" in chip), "StateChip")
check("A17A-06_chip_not_clickable", "clickable" not in chip and "onClick" not in chip, "StateChip")
mark_when = block(visual, "fun of(tone: Tone): ToneMark = when (tone) {")
marks = dict(re.findall(r"Tone\.(\w+) -> (\w+)", mark_when))
tones = re.findall(r"^\s+([A-Z_]+)\(\"[a-z_]+\"\),?$", body(read(TONE), "enum class Tone(") or "", re.M)
check("A17A-06_one_distinct_mark_per_tone", sorted(marks) == sorted(tones) and len(set(marks.values())) == len(tones),
      f"{marks}")
check("A17A-06_warning_only_for_fault", [t for t, mk in marks.items() if mk == "WARNING"] == ["SYSTEM_FAULT"], str(marks))
icons = dict(re.findall(r"ToneMark\.(\w+) -> Icons\.\w+\.(\w+)", theme_src))
check("A17A-06_every_mark_drawn", sorted(icons) == sorted(set(marks.values())), str(icons))

# --- A17A-07: code reads as code, and no authored line changes.
WORD_RUN = re.compile(r"(?<!\w)(?:[^\W\d_]{2,}[.,;:!?]?\s){3,}[^\W\d_]{2,}")
CODE_CHAR = re.compile(r"[=(){}\[\];<>|$#*+/\"'`]")
OPTION = re.compile(r"^[A-E]\)\s")
SENTENCE_END = re.compile(r"[.?!]\s*$")
WORD = re.compile(r"(?<![^\W\d_])[^\W\d_]+(?![^\W\d_])")


def klass(line: str) -> str:
    if OPTION.search(line):
        return "prose"
    if line.startswith((" ", "\t", "$ ", ">>>")):
        return "code"
    if WORD_RUN.search(line):
        return "prose"
    if SENTENCE_END.search(line) and len(WORD.findall(line)) >= 3:
        return "prose"
    if CODE_CHAR.search(line):
        return "code"
    return "neutral"


def is_code(paragraph: str) -> bool:
    kinds = [klass(x) for x in paragraph.split("\n") if x.strip()]
    return kinds.count("code") >= 1 and kinds.count("prose") * 2 <= kinds.count("code")


def unescape(value: str) -> str:
    out, i = [], 0
    while i < len(value):
        if value[i] == "\\" and i + 1 < len(value) and value[i + 1] in "n\\":
            out.append("\n" if value[i + 1] == "n" else "\\")
            i += 2
        else:
            out.append(value[i])
            i += 1
    return "".join(out)


check("A17A-07_kotlin_rule_matches_contract",
      all(s in text_layout for s in ['line.startsWith("$ ")', 'line.startsWith(">>>")', "sentenceEnd.containsMatchIn(line) && word.findAll(line).count() >= 3 -> Line.PROSE",
                                     "code >= 1 && prose * 2 <= code", r'[=(){}\[\];<>|$#*+/"' + "'`]"]),
      "TextBlocks rules")
authored = []
for f in sorted(ASSETS.glob("curriculum_package*.txt")):
    section = None
    for line in read(f).splitlines():
        if line.startswith("["):
            section = line.strip()
        elif (section == "[item]" and line.startswith("prompt=")) or (section == "[explanation]" and line.startswith("text=")):
            authored.append(unescape(line.split("=", 1)[1]))
code_count = sum(1 for t in authored for p in re.split(r"\n\s*\n", t) if p.strip() and is_code(p))
check("A17A-07_shipped_code_paragraphs_found", len(authored) > 1400 and code_count > 500,
      f"{code_count} code paragraphs in {len(authored)} shipped prompts and explanations")
check("A17A-07_sentence_about_code_is_prose",
      not is_code("Kopyalama: b = a satırı çalıştığı anda a'nın değeri b'ye atanır. Sonra a değişse bile b değişmez; b o anki değeri almıştı.")
      and is_code("Traceback (most recent call last):\n  File \"cozum.py\", line 2, in <module>\n    print(sonuc)\nNameError: name 'sonuc' is not defined")
      and not is_code("A) Bir şey aramak\nB) Bir şeyi silmek"), "rule examples")
check("A17A-07_lines_preserved_by_construction", "paragraphs += current.joinToString(\"\\n\")" in text_layout
      and "TextBlock(if (isCode(it)) BlockKind.CODE else BlockKind.PROSE, it)" in text_layout
      and "blocks.flatMap { it.text.lines() }" in course_test, "split keeps lines; the course test compares them")
code_block = body(components, "fun CodeBlock(")
check("A17A-07_code_block_is_mono_unwrapped_selectable", "roleStyle(TypeRole.MONO)" in code_block and "softWrap = false" in code_block
      and "SelectionContainer" in code_block and "horizontalScroll" in code_block, "CodeBlock")
check("A17A-07_runner_draws_authored_text", runner_screen.count("AuthoredText(") >= 6
      and "stage.texts.forEach { AuthoredText(it) }" in runner_screen and "AuthoredText(stage.prompt)" in runner_screen, "TaskRunnerScreen")
check("A17A-07_code_fields_are_mono", runner_screen.count("textStyle = codeFieldStyle") == 2, "code and report fields")

# --- A17A-08: words, not identifiers.
kinds = sorted({line.split("=", 1)[1] for f in ASSETS.glob("curriculum_package*.txt") for line in read(f).splitlines()
                if line.startswith("activity_kind=")})
labelled = pairs(block(text_layout, "val activityKinds: Map<String, String> = mapOf("))
check("A17A-08_every_shipped_activity_kind_labelled", kinds and all(k in labelled for k in kinds), f"{kinds}")
check("A17A-08_today_says_words", "DisplayCopy.activityKind(task.activityKind)" in today_screen
      and "DisplayCopy.track(task.curriculumTrack)" in today_screen and re.search(r"^\s+task\.activityKind,$", today_screen, re.M) is None,
      "TaskHeadline")
check("A17A-08_unknown_is_left_out", "fun activityKind(id: String?): String? = id?.let(activityKinds::get)" in text_layout
      and "fun track(id: String?): String? = id?.let(tracks::get)" in text_layout, "DisplayCopy")
check("A17A-08_study_day_in_words", "DisplayCopy.studyDay(view.studyDay)" in end_of_day
      and '"26 Ekim 2026, Pazartesi"' in test_src and "Locale" not in text_layout, "no device locale")
shipped_topics = sorted({line.split("=", 1)[1] for f in ASSETS.glob("curriculum_package*.txt") for sec in re.split(r"\n(?=\[)", read(f))
                         if sec.startswith("[topic]") for line in sec.splitlines() if line.startswith("logical_id=")})
shipped_skills = sorted({line.split("=", 1)[1] for f in ASSETS.glob("curriculum_package*.txt") for sec in re.split(r"\n(?=\[)", read(f))
                         if sec.startswith("[skill]") for line in sec.splitlines() if line.startswith("logical_id=")})
topic_names = pairs(block(names_src, "val topics: Map<String, String> = mapOf("))
skill_names = dict(re.findall(r'"(skill\.[a-z_.]+)" to "([^"]+)"', block(names_src, "val skills: Map<String, String> = mapOf(")))
topic_names = dict(re.findall(r'"(topic\.[a-z_.]+)" to "([^"]+)"', block(names_src, "val topics: Map<String, String> = mapOf(")))
check("A17A-08_every_shipped_topic_named", len(shipped_topics) == 27 and sorted(topic_names) == shipped_topics,
      f"{len(shipped_topics)} shipped, {len(topic_names)} named")
check("A17A-08_every_shipped_skill_named", len(shipped_skills) == 60 and sorted(skill_names) == shipped_skills,
      f"{len(shipped_skills)} shipped, {len(skill_names)} named")
claims = [n for n in list(topic_names.values()) + list(skill_names.values())
          if re.search(r"%|tamamland|öğrenildi|başar|seviye|puan", n, re.I)]
check("A17A-08_names_claim_nothing", not claims, str(claims))
check("A17A-08_names_fall_back_to_authored", "skills[ref.logicalId] ?: authored ?: ref.logicalId" in names_src
      and "topics[ref.logicalId] ?: authored ?: ref.logicalId" in names_src, "CurriculumNames")
areas = re.findall(r'"(\w+)" to "[^"]+"', block(names_src, "val areas: List<Pair<String, String>> = listOf("))
check("A17A-08_every_topic_in_a_known_area", areas and all(t.split(".")[1] in areas for t in shipped_topics), str(areas))
check("A17A-08_objective_named_by_lesson", "substringAfterLast('.').replace('_', ' ')" not in topic_presentation
      and "facts.objectiveTitles[objective] ?: CurriculumNames.skill(member.skill" in topic_presentation, "TopicPresentation")
check("A17A-08_lesson_title_port_refinement", "fun lessonTitleFor(objective: VersionedRef): String? = null" in ports
      and "override fun lessonTitleFor(objective: VersionedRef): String? = LessonTitles.of(parsed?.tasks.orEmpty(), objective)" in file_content
      and "tasks.filter { it.purpose == TaskPurpose.TEACH && objective in it.targetObjectives }" in read(ANDROID / "core-model/src/main/kotlin/coach/AuthoredTaskFacts.kt")
      and "compareByDescending<AuthoredTask> { it.ref.version }" in read(ANDROID / "core-model/src/main/kotlin/coach/AuthoredTaskFacts.kt")
      and "content.lessonTitleFor(objective)" in topic_query and "val objectiveTitles: Map<VersionedRef, String> = emptyMap()" in topic_facts,
      "ContentPort.lessonTitleFor")
# Found by the independent review: Objectives, Topics and days on Skill detail, in the history and on the fast path.
id_words = [f.name for f in P.glob("*.kt") if "substringAfterLast('.').replace('_', ' ')" in read(f) and f.name != "TaskRunPresentation.kt"]
check("A17A-08_no_objective_identifier_label_in_presentation", not id_words, str(id_words))
progress_src = read(P / "ProgressPresentation.kt")
history_src = read(P / "HistoryPresentation.kt")
history_screen = read(UI / "HistoryScreen.kt")
application = read(ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt")
check("A17A-08_skill_detail_objectives_and_topics_in_words",
      "facts.objectiveTitles[o.objective] ?: CurriculumNames.skill(row.skill, row.name)" in progress_src
      and "CurriculumNames.topic(VersionedRef(it.logicalId, it.version), it.name)" in progress_src, "ProgressPresentation.detail")
check("A17A-08_history_objectives_and_days_in_words",
      "facts.objectiveTitles[entry.objective] ?: name(entry.skill)" in history_src and "DisplayCopy.studyDay(studyDay)" in history_src
      and "SectionHeader(day.label)" in history_screen and "session.dayLabel" in history_screen and "session.studyDay" not in history_screen,
      "HistoryPresentation, HistoryScreen")
check("A17A-08_run_and_fast_path_name_objectives_by_lesson", "TaskRunPresentation::objectiveLabel" not in application
      and "content?.lessonTitleFor(ref) ?: TaskRunPresentation.objectiveLabel(ref)" in application
      and "LearningHistoryQuery(opened.graph.persistence, opened.graph.content)" in application, "CoachApplication")
check("A17A-08_input_validation_not_fault_tone", "isError" not in read(UI / "ProfileScreen.kt"), "ProfileScreen")
presentation_names = [f.name for f in P.glob("*.kt") if "?: it.logicalId }" in read(f) or "?: ref.logicalId }" in read(f)]
check("A17A-08_presentation_names_go_through_curriculum_names", not presentation_names, str(presentation_names))

# --- A17A-09: Learn is the map, not the truth.
check("A17A-09_learn_uses_progress_chip", ".map(TopicPresentation::chip)" in learn_src, "LearnPresentation")
check("A17A-09_learn_edge_accepted", '"learn" to "topic_detail"' in navigation
      and "NavigationGraph.canOpen(Destination.LEARN.id, Surface.TopicDetail.id)" in activity, "learn -> topic_detail")
check("A17A-09_learn_replaces_placeholder", "else -> LearnScreen(" in activity and "AppRoot(statusLine" not in activity
      and "· study day" not in activity, "MainActivity")
learn_copy = block(learn_src, "object LearnCopy {")
check("A17A-09_learn_says_order_is_not_prerequisite", "ön koşul zinciri değildir" in learn_copy
      and "hiçbir teknik konu onu beklemez" in learn_copy, "LearnCopy")
forbidden = re.findall(r"%|puan|seviye|tamamland|başarısız|ilerleme yüzdesi", learn_copy + learn_screen, re.I)
check("A17A-09_learn_claims_nothing", not forbidden, str(forbidden))
check("A17A-09_learn_counts_nothing", not re.search(r"\.count\b|\.size\b|sumOf|count\(", learn_src), "no count in LearnPresentation")
check("A17A-09_unloaded_said", "LearnCopy.NOT_LOADED" in learn_screen and "LearnCopy.LOADING" in learn_screen
      and "!facts.curriculumLoaded) return null" in learn_src, "LearnScreen")

# --- A17A-10: the first launch does not crash.
check("A17A-10_startup_gate", "StoreStatus.Ready && !(storeInHand && todayInHand)) StoreStatus.Opening" in health_src, "AppHealth.atStartup")
check("A17A-10_activity_uses_gate", "AppHealth.atStartup(storeStatus.value, evaluatorAvailability(), storeInHand = current != null, todayInHand = facts != null)" in activity
      and "AppHealth.of(status" not in activity, "MainActivity")
check("A17A-10_gate_tested", "the app is still opening until the store and today's facts are both in hand" in test_src, "VisualPolishTest")

# --- A17A-11: every screen on a measured surface, in the safe drawing area.
theme_body = body(theme_src, "fun CoachTheme(")
check("A17A-11_surface_under_every_screen", "Surface(" in theme_body and "color = MaterialTheme.colorScheme.background" in theme_body, "CoachTheme")
light_xml, dark_xml = read(THEMES_LIGHT), read(THEMES_DARK)
lx = re.search(r'<color name="coach_surface">(#[0-9A-F]{6})</color>', light_xml)
dx = re.search(r'<color name="coach_surface">(#[0-9A-F]{6})</color>', dark_xml)
check("A17A-11_launch_theme_is_measured_surface", lx and dx and lx.group(1) == light["surface"] and dx.group(1) == dark["surface"]
      and 'parent="android:Theme.Material.Light.NoActionBar"' in light_xml and 'parent="android:Theme.Material.NoActionBar"' in dark_xml
      and "@color/coach_surface" in light_xml and "@color/coach_surface" in dark_xml, f"{lx and lx.group(1)} {dx and dx.group(1)}")
check("A17A-11_edge_to_edge", "enableEdgeToEdge()" in activity and "WindowInsets.safeDrawing" in body(components, "fun ScreenColumn("), "MainActivity, ScreenColumn")
scrollers = [f.name for f in UI.glob("*.kt") if "verticalScroll(rememberScrollState())" in read(f)
             and f.name not in ("Components.kt",) and not (f.name == "AiSettingsScreen.kt" and "WindowInsets.safeDrawing" in read(f))]
check("A17A-11_screens_scroll_through_screen_column", not scrollers, str(scrollers))

# --- A17A-12: nothing adds a state, a severity or a progress claim.
shapes = [f.name for f in UI.glob("*.kt") if re.search(r"ProgressIndicator|Slider|RatingBar|Badge\(", read(f))]
check("A17A-12_no_restricted_shapes", not shapes, str(shapes))
check("A17A-12_no_star_icon", "Icons.Filled.Star" not in app_shell and "Icons.Filled.Menu" not in app_shell, "AppShell")
check("A17A-12_group_block_has_no_tone", "LocalToneColors" not in components and "Tone" not in body(components, "fun SectionBlock("), "SectionBlock")
check("A17A-12_navrow_is_one_target", "minimumTouchTarget()" in body(components, "fun NavRow(") and "onClickLabel = title" in body(components, "fun NavRow("),
      "NavRow")

# --- A17A-13: tests and narrowing.
for name in ["the type scale has exactly VDSX-v0's eight roles", "every Material role pair the theme draws meets the WCAG anchor",
             "a chip's tone colour reads as text and as a border", "text blocks keep every line as authored",
             "learn groups Topics by area", "an Objective is named by its lesson title"]:
    check(f"A17A-13_test_{name[:40]}", name in test_src, name)
for name in ["every shipped Topic and Skill has a Turkish name", "every shipped activity kind has a Turkish label",
             "every Objective a lesson teaches is named by that lesson's own title", "code blocks in shipped lessons and prompts keep every line"]:
    check(f"A17A-13_course_test_{name[:40]}", name in course_test, name)
check("A17A-13_narrowed_marked", all("Narrowed at 17A" in read(p) for p in NARROWED), str([p.name for p in NARROWED]))

# --- A17A-14: the emulator report.
emu = yaml.safe_load(read(EMULATOR)) or {}
shots = emu.get("screens", [])
check("A17A-14_emulator_not_t6", emu.get("t6_run") is False and "Pixel_7" in str(emu.get("device", "")) and emu.get("api") == 34, str(emu.get("device")))
check("A17A-14_screens_exist", shots and all((SCREENS / s["file"]).is_file() for s in shots), f"{len(shots)} screens")
check("A17A-14_light_dark_and_200", {s.get("theme") for s in shots} >= {"light", "dark"} and any(s.get("font_scale") == 2.0 for s in shots)
      and any(s.get("when") == "before" for s in shots), "coverage")

# --- A17A-15: the living record.
check("A17A-15_decision_recorded", "## D-133" in decisions and "VPLX-v0" in decisions, "DECISIONS")
check("A17A-15_spec_acceptance_matches_contract",
      len(re.findall(r"^\d+\. ", spec.split("# 10. Acceptance criteria")[-1].split("# 11.")[0], re.M)) == len(contract.get("acceptance_criteria", [])),
      "acceptance criteria")
check("A17A-15_execution_index", "[x] **17A — Görsel polish**" in read(EXECUTION_INDEX) and "[x] 17A — Görsel polish" in read(MASTER_PLAN), "17A checked")
mut = contract.get("mutation_results_kotlin", {})
vmut = contract.get("mutation_results_validator", {})
check("A17A-15_kotlin_mutation_all_detected", mut.get("total", 0) > 0 and mut.get("detected") == mut.get("total"), f"{mut.get('detected')}/{mut.get('total')}")
check("A17A-15_validator_mutation_all_detected", vmut.get("total", 0) > 0 and vmut.get("detected") == vmut.get("total"), f"{vmut.get('detected')}/{vmut.get('total')}")

report = {
    "model": "VPLX-v0",
    "stage_step": "17A",
    "decision": "D-133",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results),
    "checks_failed": len(failures),
    "checks": results,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, allow_unicode=True, sort_keys=False, width=200), encoding="utf-8", newline="\n")
print(f"17A QA: {len(results) - len(failures)}/{len(results)} PASS")
for f in failures:
    print("FAIL", f)
sys.exit(1 if failures else 0)
