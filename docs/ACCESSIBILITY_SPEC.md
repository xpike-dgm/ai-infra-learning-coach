# Accessibility Specification — A11X-v0

**Stage step:** 17D — Accessibility  
**Status:** ACCEPTED — independent 17D QA PASS  
**Decision:** `D-137` (and `D-136`: 17E added)  
**Model:** `A11X-v0 — accessibility: every control has a name, a role and its state for a screen reader, no two controls share a spoken name, every target is at least 48dp, no identifier or help code reaches the screen, every surface has a heading, controls are in reading order, a field's error is its own, and the real app is audited through the accessibility tree on an emulator at 100% and 200% text`  
**Behaviour it implements:** `VDSX-v0` §8 and §12 (48dp, state in text, 200% text); `UXIA-v0` (one title per surface); `TRUX-v0` and 17C (the exit, the spoken position); WCAG 2.2 4.1.2, 2.5.3, 3.3.1, 4.1.3, 1.4.4, 2.4.6  
**Code:** `core-presentation` (`SettingsCopy.lengthField`, `SettingsCopy.saveLength`, `ReminderCopy.TIME_LABEL`, `ToolWords`), `app-ui` (`ProfileScreen.kt`, `TodayScreen.kt`, `CoachTheme.kt` — `StateChip`, `AssessmentSessionScreen.kt`), `tools/audit_a11y_emulator.py`  
**Persistence / schema:** none — schema v11 unchanged

## 1. Purpose

> **Ekran okuyucu, büyük metin ya da anahtarla kullanan biri her ekrana ve her kontrole ulaşabiliyor, her birinin ne olduğunu ve hangi durumda olduğunu duyabiliyor mu?**

Primary invariant:

> **Every control is named, with its role and state.** Every actionable node has a name (R1), every checkable control is named (R5), every field is named by its label and never by what was typed into it (R6), and no control is an icon alone (R4).
>
> **No two controls share a spoken name** (R7), anywhere on a screen. Three "Kaydet" buttons cannot be told apart; each now names its own day, and the spoken name still contains the visible word (label in name).
>
> **Every target is at least 48 x 48 dp** (R2), at 100% and at 200% text.
>
> **Nothing reaches the screen as an identifier** — no snake_case word, no logical id, no help code H1–H4 (R3).
>
> **Every surface has a heading**; titles and section headers are headings. A state chip is one leaf, so its state is spoken once and a heading on it is on a node a screen reader focuses.
>
> **Controls are in reading order** (R8): the tree's order runs down the screen.
>
> **A field's error is its own:** it is the field's supporting text, in the body colour — a mistyped number is not a fault (17A, `VDSX-v0`), so the field is never drawn in the error colour.
>
> **Nothing interrupts:** every live region is polite.
>
> **It is checked on the real app,** not only read from the code: the app runs on an emulator and every screen a learner reaches is read whole through the accessibility tree at 100% and 200% text. The tree carries names, sizes, clickability and order; headings, state descriptions, live regions, roles and errors are not in it and are checked in the code.

---

# 2. What was found

- **Three fields named "dk" and three buttons named "Kaydet".** Profile's daily-length rows (short, normal, intensive day) each had a field labelled "dk" and a button "Kaydet"; a screen reader heard the same names three times. Each field is now "Kısa gün süresi (dakika)" (and so on), and each button's spoken name is "Kısa gün süresini kaydet", which keeps the visible "Kaydet".
- **Today's row to Progress had the tab's name.** It was "İlerleme", like the shell's tab, so a screen reader heard two "İlerleme" controls; the new rule R7 caught it on the emulator. The row now says its action, "İlerlemeyi aç".
- **Today's plan explanation appeared twice.** "Bu plan neden böyle?" sat beside the selected task and again in the supporting rows below; R7 caught one control heard twice. It is drawn once, beside the task.
- **Input errors were loose text** below their fields (minutes and, found by the independent review, the reminder time). Each is now the field's supporting text. The first fix also marked the field in error, which draws it in Material's error colour; 17A's check (a mistyped number is not a fault) caught that, and `isError` is not used.
- **The state chip could be spoken twice** (independent review). It carried a state description beside the same visible text, and a heading or live region a caller put on it sat on a node a screen reader does not focus. It is now one leaf whose spoken name is its label; three earlier checks were narrowed ("Narrowed at 17D").
- **The unwired assessment interior showed help codes and tool ids** ("H1 yardım iste", `external_ai`) through string templates (independent review). Help levels are words and tools go through `ToolWords`.
- **The reminder time's label was "Saat (SS:DD)"**, which a screen reader spells; it is "Hatırlatma saati (ör. 19:00)".
- **The first audit had holes** (independent review): a cut node was never re-judged, a field was named by its own value (so R7 could not have caught the "dk" fields), R7 compared one reading only, and reaching a screen was assumed from a tap. Now every screen is read whole with overlapping readings, a cut node must be seen whole somewhere, fields are named by their label, R7 spans all readings, a marker proves each screen was reached, R8 checks reading order, and the today dialog is audited.
- **At 200% text** the run's orientation choices and its start button fall below the fold; they are reached by scrolling, which is what a learner does. The first audit stopped there because it did not scroll.
- **The dump's bounds are clipped to the window, not to the scrolling container,** so a node cut by the scroll edge looked smaller than 48dp, and its name may be cut too. A row can even sit entirely outside the viewport and still be in the tree. The audit treats a node at the same place in every reading as fixed (the tabs, the exit bar); any other node not well inside the viewport is cut, and is matched to a reading where a whole node contains it and carries its name. Two consecutive readings must share an anchor text, or the screen is not counted as read whole.
- **TalkBack is not on the emulator image** (no Play services). An APK from outside the Play store is an untrusted download and was not fetched, so what TalkBack actually says was not heard.
- **The rest already held:** every icon declares its description (decorative beside a word), titles and section headers are headings, every button has the 48dp floor, the choice of answer source is one radio group, the shell's destinations say whether they are selected, and the run's result is announced politely.

---

# 3. Decisions

User decisions (2026-10-27):

- **Scope: the accessibility audit and tests only** — names, headings, state as text, 48dp targets, reading order; automated checks on the emulator; TalkBack tried on the emulator.
- **A new step, 17E — Kalan UX parçaları (`D-136`),** at the end of AŞAMA 17 with no renumbering, takes the three parts not chosen in 17C: the day summary naming a run's real changes; the `skill_detail -> planner_explanation` and `technical_english_profile -> skill_detail` edges; AI usage/cost visibility.
- **Not chosen for 17D, asked at 17E's PRE:** a visible focus ring (the `focus_ring` token is unused), a theme preference in Profile, an in-app reduced-motion preference.

Assistant defaults:

- the audit drives the real app with adb and reads each screen with `uiautomator dump`, the tree an accessibility service reads; no instrumented-test dependency was added;
- eight rules, R1–R8; every screen is read whole in overlapping readings; a cut node is matched by place to a reading where it is whole; a marker shows each screen was reached;
- every audit clears the app's data first, so every screen (the run's question and help included) is reachable, and runs at 100% and 200% text;
- a button's spoken name contains its visible word;
- an input error is its field's supporting text in the body colour, never the error colour;
- a state chip is one leaf whose spoken name is its label;
- an allowed tool is said in words (`ToolWords`); a tool with no word is said as such, never as its id;
- live regions are polite only.

---

# 4. What moved on

- The three parts not chosen in 17C, to 17E (`D-136`); the focus ring, theme and in-app reduced-motion preferences, asked at 17E's PRE.
- TalkBack's spoken output and the device (19), with two review suggestions to hear there: an explicit error announcement for input errors (without the fault colour), and whether a tab's "seçili" is said twice.
- One source for the help levels' two wordings; `AssessmentSessionScreen` (no screen calls it) — still open.

---

# 5. Verification

- Kotlin: `AccessibleNamesTest` (3). Narrowed: `validate_design_system_impl.py` E10C-14, `validate_visual_polish.py` A17A-06, `validate_motion.py` A17B-07, `validate_today_interior.py` E11A-14 ("Narrowed at 17D").
- Emulator audit: `tools/audit_a11y_emulator.py`; reports `arch/17d_accessibility/a11y_emulator_report.yaml` (100%) and `a11y_emulator_report_text_200.yaml` (200%): fourteen screens each, read whole, every screen reached, every cut node judged, 0 findings, rules R1–R8. Pixel 7 AVD, API 34; not T6.
- Validator: `tools/validate_accessibility.py`, 59/59 PASS.
- Mutation: Kotlin 10/10 and validator 35/35, each a single clean run; the controls survived. The validator's first run detected 32/34: a shell heredoc had turned a regex's word-boundary escape into a backspace character, so the loose-error check matched nothing, and the test check asked for two tests of three; both were fixed and the whole set run again.
- Independent review: a separate agent found six blocking problems — the reminder error, the chip spoken twice, codes in the unwired assessment interior, and three holes in the audit — all fixed; its re-review found none blocking. Suggestions taken: the reminder label, wider validator checks, anchored readings, name-matched cut nodes, Profile's own marker. Left for the device (19): an explicit error announcement without the fault colour, and whether a tab's "seçili" is said twice.
- 1297 JVM tests, `verifyModuleBoundaries`, `assembleDebug` with and without the AI adapter.
- The fast standing sweep passes; the content validators were not re-run (the packages are unchanged).

---

# 6. Acceptance criteria

17D is accepted only if independent QA verifies at minimum:

1. The emulator audit ran at 100% and 200% text over every named screen, read each whole, reached each (marker), judged every cut node, with rules R1–R8, and passed.
2. Every icon declares its description; every surface has a heading.
3. Every button and hand-made target is at least 48dp; a choice is one group; the shell says its state; a state chip is one leaf.
4. Every field is labelled, and a field's error is its own supporting text, never the error colour.
5. Repeated controls have distinct spoken names that contain the visible word.
6. No live region is assertive; no help code is in the UI.
7. TalkBack is not claimed as heard; the unchosen items and 17E are named.
8. Earlier gates still pass.

---

# 7. Open loops

See §4. **17D is complete.** Next numbered step: `17E — Kalan UX parçaları` (D-136). It needs a fresh PRE and the user's new explicit approval.
