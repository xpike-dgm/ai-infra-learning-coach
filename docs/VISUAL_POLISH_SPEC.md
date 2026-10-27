# Visual Polish Specification — VPLX-v0

**Stage step:** 17A — Görsel polish  
**Status:** ACCEPTED — independent 17A QA PASS  
**Decision:** `D-133`  
**Model:** `VPLX-v0 — the accepted design system applied: every Material role a measured token, every type slot a VDSX-v0 role with mono for code, a 4dp rhythm, a surface under every screen, outlined state chips with a mark per tone, words instead of identifiers, and the Learn tab's learning map`  
**Behaviour it implements:** `VDSX-v0` (8F: tones, type roles, rhythm, marks, restricted shapes); `WFPX-v0` (8G: measured palette, 200% text); `DSIX-v0` (10C: tokens in core, contrast recomputed); `UXIA-v0` §§5, 7, 10.2 (`learn_overview`, browse is not prerequisite); `NSHX-v0` (`learn -> topic_detail`); `PWPX-v0` (16C Topic chips)  
**Code:** `core-model` (`SkillDetailFacts.objectiveTitles`, `HistoryFacts.objectiveTitles`), `core-application` (`ProgressQuery`, `LearningHistoryQuery(content)`), `core-presentation` (`VisualSystem.kt` — `TypeRole`, `TypeScale`, `Spacing`, `MaterialColorRoles`, `ChipColors`, `ToneMark`; `TextLayout.kt` — `TextBlocks`, `DisplayCopy`; `CurriculumNames.kt`; `LearnPresentation.kt`; `AppHealth.atStartup`), `core-ports` (`ContentPort.lessonTitleFor`), `core-model` (`TopicDetailFacts.objectiveTitles`), `core-application` (`TopicQuery.detail`), `data-curriculum` (`FileContentSource.lessonTitleFor`), `app-ui` (`CoachTheme.kt`, `Components.kt`, `LearnScreen.kt` and every screen), `app-wiring` (`MainActivity` — edge to edge, the startup gate, Learn; `res/values*/themes.xml`)  
**Persistence / schema:** none — schema v11 unchanged

## 1. Purpose

> **Uygulama, kabul edilmiş tasarım sisteminin söylediği gibi görünüyor mu — ölçülmüş renkler, rollerine göre yazı, kod için mono, düzenli aralık, durumu düğmeyle karıştırmayan çipler, kimlik değil kelime — ve Öğren sekmesi bana öğrenme haritasını gösteriyor mu, hiçbir görünüm bir durum, bir anlam ya da bir ilerleme iddiası eklemeden?**

Primary invariant:

> **The design system is applied, and it adds no meaning.** Every Material colour role is a measured token and every Material type slot is one of `VDSX-v0`'s eight roles; a Material default never reaches the screen.
>
> **Code reads as code.** A paragraph of an authored lesson or prompt that reads as code is drawn in the `mono` role; every line comes back exactly as authored, and a paragraph the rule is unsure about stays prose.
>
> **A state is never a button.** A chip is outlined, carries its tone's colour as label, mark and border, and one mark per tone says it without colour.
>
> **Words, not identifiers.** An activity kind, a track, a study day, a Topic, a Skill and an Objective are said in Turkish, or left out — never shown as an identifier and never guessed.
>
> **Learn is the map, not the truth.** Areas and their Topics, each with the very chip Progress shows, each opening `topic_detail`; the order is orientation, never a prerequisite chain.

---

# 2. What was found

The app was run for the first time on an emulator (Pixel 7 AVD, API 34, x86_64). It is not T6 — T6 is the Poco M6 Pro (19) — but it showed what eleven steps of JVM-only verification could not:

- **The app crashed on its first launch.** The store reported Ready a moment before the opened store and today's facts reached the activity, so the blocking surface was asked to render with nothing blocking (`requireNotNull` in `HealthBlockingSurface`). `AppHealth.atStartup` now keeps the app opening until both are in hand.
- **A focused flow had no surface.** The task runner drew dark text straight on the launch theme's dark window background (`android:Theme.Material`): about 1.4:1. Every screen now sits on the measured surface, and the launch theme is the light (and, at night, dark) measured `surface`.
- **Material's purple baseline reached the screen.** The navigation bar and its indicator wore Material defaults, close to the violet `attention` tone. Every Material role is now mapped to a token (`MaterialColorRoles`) and every drawn pair is recomputed.
- **Code was drawn in the body font.** `VDSX-v0` §6.1 has a `mono` role and nothing used it. Shipped content has no code markup; a deterministic paragraph rule finds 841 code paragraphs across the eight packages and changes no line.
- **A state chip looked like a button** — a filled rounded rectangle of the same weight as an action.
- **Identifiers on screen:** `content_explanation` under Today's task, `learn_overview · study day 2026-10-26` as the Learn tab, Topics as "Expressions Io", Skills as "Standard Io Basic", Objectives as "explain pipeline", and the study day as `2026-10-26`.
- **Off-rhythm spacing** (2dp, 6dp) and blue text-button lists that read as links rather than rows.
- **Android 15+ enforces edge to edge for this target SDK**, so the target device (API 36) would draw content under the status bar while the API 34 emulator did not. The app now asks for edge to edge on every level and every screen pads itself to the safe drawing area.
- **The independent review found identifiers 17A had not yet reached:** the study day in the learning history and the assessment report; Objectives named by their identifier on Skill detail, in the history, on the fast path and in a run's result; Topics by their English names on Skill detail. Each now goes through `DisplayCopy`, the lesson's own title (`objectiveTitles` on `SkillDetailFacts` and `HistoryFacts`, `lessonTitleFor` in the app) or `CurriculumNames`. It also found Material's error colour on a mistyped number in Profile — a fault tone on something that is not a fault — and it was removed; the hint below the field already says what is wrong.
- **Earlier gates pinned what 17A changed:** three explanation tests asserted "Pointer dereference" for a shipped Skill, two tests asserted the identifier label "trace loop", and one 16B check asserted the history query's exact constructor. Each was narrowed and marked "Narrowed at 17A".

---

# 3. Decisions

User decisions (2026-10-27):

- **Scope:** the design system applied — `VDSX-v0` type roles with mono for code, the 4dp rhythm, a surface under the focused flows, system bars and navigation colours from the palette, chip ≠ button, identifiers to Turkish labels, the date — plus the Learn tab's own screen. Run and notification polish and the day summary naming a run's real changes go to 17C.
- **The first-launch crash** is fixed inside 17A as a finding.
- **Verification:** emulator screenshots (Pixel 7 AVD, API 34), light and dark and 200% text, with a report in the repo. Not T6.

Assistant defaults:

- the type scale follows Material 3's baseline sizes (nothing below 12sp, body large 16sp, body 14sp) with heavier titles, as a declared product default and a calibration input for 18 (`VDSX-v0` §6.2);
- `mono` is the platform monospace face; no font file is shipped, and it covers Turkish letters;
- the chip is outlined: the tone's own `tone_*` colour as label, mark and border on the surface beneath. All six tone colours were measured as text on `surface` and `surface_variant` in both themes (lowest 5.45:1);
- one mark per tone: info (neutral), play (active), check (positive), look (attention — not an alarm), refresh (pending), warning (system fault only);
- navigation icons: Learn a map pin, Progress a list; the star read as a rating or reward (`VDSX-v0` §11.1);
- Topic and Skill names are presentation copy keyed by logical id, so a new version keeps its name, and they follow the lessons' own terms (the review's corrections: "kol", "çıkış durumu", "küçük ilişki kelimeleri", while and do-while); a Topic keeps its canonical name's scope even where only part of it is published yet ("Dosya sistemi ve izinler"), because more of it is 20's; an Objective is named by its lesson's own title, everywhere it is named;
- Learn groups Topics by the area in their logical id (`topic.<area>.<name>`) in a declared order, with the parallel-English note;
- the selected navigation item uses the `active` pair (`secondaryContainer`).

---

# 4. The theme (`MaterialColorRoles`, `TypeScale`, `CoachTheme`)

- 36 Material colour roles, each a measured token in both themes. The fault tone is `error` and `errorContainer` and nothing else.
- 19 text pairs and 4 non-text pairs the theme draws are recomputed against 4.5:1 and 3:1, per theme, as an ordinary JVM test.
- 15 Material type slots, each one of the 8 roles. Mono is asked for by name, never through a Material slot. Every size is in sp.
- Every screen is wrapped in a `Surface` of the measured background.

---

# 5. Shared pieces (`Components.kt`)

- `ScreenColumn`: the safe drawing area, a 16dp margin and the 4dp rhythm.
- `ScreenTitle`, `SectionHeader`: headings announced as headings.
- `SectionBlock`: grouping by structure, no tone.
- `CodeBlock`, `AuthoredText`: code paragraphs in mono, unwrapped and selectable; nothing reworded.
- `NavRow`: a row that opens a surface, title in the body colour and a chevron; the whole row is the 48dp target.
- `CountRow`: a label and its count — inventory, never a bar.
- `BackButton`.
- `app-ui` writes spacing only as `Spacing` steps; the one other dp is the chip's hairline border.

---

# 6. Code paragraphs (`TextBlocks`)

- A paragraph is a run of non-blank lines.
- A line is **code** when it is indented, starts with `$ ` or `>>>`, or has a code character (`= ( ) { } [ ] ; < > | $ # * + / " ' \``) — unless it reads as a sentence: four or more words in a row, or a sentence ending (`. ? !`) with three or more words. A multiple-choice option (`A) …`) is prose.
- A paragraph is code when it has a code line and at most half as many sentence lines.
- Every line comes back exactly as authored, in order.

---

# 7. Learn (`LearnPresentation`, `LearnScreen`)

- `learn_overview`: the curriculum's areas and their Topics, each with `TopicPresentation.chip` — the very chip Progress shows — each opening `topic_detail` through the accepted `learn -> topic_detail` edge.
- No count, no ratio, no "n of m", no area-level state.
- The order is orientation; the screen says it is not a prerequisite chain.
- An unloaded curriculum is said, never drawn as an empty map.

---

# 8. What moved on

- The focused-flow exit scrolls out of view in a long lesson; keeping it in view is usability (17C).
- Run polish and notification polish (17C), including the assistance levels the history shows as "H1, H3".
- The day summary naming a run's real changes (17C).
- Usage/cost visibility (17C).
- The `skill_detail -> planner_explanation` and `technical_english_profile -> skill_detail` edges and their micro text (17C).
- Inline code inside a prose sentence, and content markup for code (20I).
- Theme and accessibility preferences (17D, as before).
- Motion (17B).
- The Progress load time on the emulator (about 10 s) goes to 18E.
- T6 and the app on the Poco M6 Pro (19).

---

# 9. Verification

- Kotlin: `VisualPolishTest` (14) and `VisualPolishCourseTest` (4, against the eight shipped packages).
- Independent review: a separate agent read the names against the shipped capability statements and lessons, and the visual layer against `VDSX-v0`; its three blocking findings were fixed and its suggestions taken where they were right.
- Validator: `tools/validate_visual_polish.py`, 95/95 PASS.
- Mutation: Kotlin 27/27 and validator 31/31, each a single clean run; the controls survived. In the first runs one mutant of each survived — the lesson title from the oldest teach-task version (no shipped Objective has two) and a weakened sentence rule hidden by a prefix match; a synthetic test and a stronger check were added and both sets were run again.
- Emulator: before and after screenshots, light, dark and 200% text (`arch/17a_visual_polish/screens/`, `emulator_report.yaml`).
- 1287 JVM tests, `verifyModuleBoundaries`, `assembleDebug` with and without the AI adapter.
- The standing sweep (`tools/validate_*.py`): every validator passes; `validate_content_qa.py` was not run (disk space; its inputs, the packages, are unchanged) and `validate_assessment_content.py` ran without the earlier rebuilds.

---

# 10. Acceptance criteria

17A is accepted only if independent QA verifies at minimum:

1. Every Material colour role is a measured token, in both themes, and the fault tone is Material's error role alone.
2. Every drawn Material pair and every chip colour is recomputed against the WCAG anchor in both themes.
3. Every Material type slot is one of `VDSX-v0`'s eight roles, in sp; mono is the code role.
4. Code paragraphs are drawn in mono and no authored line changes.
5. A state chip is outlined, has its tone's mark, and states its label as text; only the fault tone has the warning mark.
6. No identifier reaches the screen where 17A found one; an unknown value is left out or falls back to the authored name.
7. Learn shows areas and Topics with Progress's chips, opens `topic_detail` through the accepted edge, and claims no order, count or completion.
8. The app does not crash on first launch; a shell-less health always has something blocking.
9. Every screen sits on a measured surface and the safe drawing area; edge to edge is asked for on every level.
10. Nothing adds a state, a severity, a percentage, a progress bar, a rank or a streak.
11. Earlier gates still pass, narrowed only where 17A changed what they pinned.

---

# 11. Open loops

See §8. **17A is complete.** Next numbered step: `17B — Motion`. It needs a fresh PRE and the user's new explicit approval.
