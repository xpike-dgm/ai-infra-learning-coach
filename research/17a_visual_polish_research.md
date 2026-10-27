# 17A — Visual polish: research synthesis

**Step:** 17A — Görsel polish (`VPLX-v0` / `D-133`)  
**Question:** how is the accepted design system applied to every screen — colour roles, type roles, rhythm, chips, code — and how does the Learn tab show the learning map, without the visual layer adding a state, a severity or a progress claim?

## 1. Internal contracts (binding)

- **`VDSX-v0` (8F, `docs/DESIGN_SYSTEM_SPEC.md`):**
  - six tones defined by meaning; the fault tone only for a real technical fault (§§4–5);
  - eight type roles, `mono` for code, terminal output and identifiers (§6.1); the scale is a declared default and a calibration input (§6.2); no locale-naive case transform, labels rendered as authored (§6.3);
  - colour as semantic roles; every tone with text and a non-colour differentiator (§§7.1, 7.3);
  - a 4dp rhythm with the steps 4, 8, 12, 16, 24, 32, 48; 48dp targets; grouping by structure (§8);
  - icons supportive, one metaphor per state, decorative when the label says it (§9);
  - no progress bar for competence, no rank, badge, streak or heatmap (§11.1).
- **`WFPX-v0` (8G):** the measured palette per theme; 200% text without losing content.
- **`DSIX-v0` (10C):** tokens in `core-presentation`, contrast recomputed from hex in a JVM test; Material's `error` role is the fault tone; no dynamic colour.
- **`UXIA-v0` (8A):** `learn` answers "What is the learning map and where can I go?" (§5); `learn_overview` is a shell root (§7.1); browse hierarchy is not prerequisite hierarchy (§10.2); English is browsable through Learn and is not a gate (§13).
- **`NSHX-v0` (10B):** `learn -> topic_detail` and `learn -> skill_detail` are accepted edges.
- **`PWPX-v0` (16C):** a Topic's state and chip come from `TopicPresentation`; the same chip everywhere.

## 2. External facts used

- **WCAG 2.2** 1.4.3 (4.5:1 text, 3:1 large text), 1.4.11 (3:1 non-text), 1.4.4 (200% text) and 1.4.1 (colour not the only means) — the anchors `VDSX-v0` already cites.
- **Material 3 type scale** (baseline): body large 16/24sp, body medium 14/20sp, body small 12/16sp, title large 22/28sp — used as the declared default sizes.
- **Android 15 behaviour change:** an app targeting SDK 35 or higher is drawn edge to edge on Android 15+, and the status and navigation bar colours are no longer applied. This app targets 36 and the target device runs 36; `enableEdgeToEdge()` makes earlier levels behave the same, so the API 34 emulator shows what the device will.
- **Compose window insets:** `WindowInsets.safeDrawing` covers the status bar, the navigation bar, the display cutout and the IME.

## 3. What the emulator showed

The first run of the app anywhere but the JVM (Pixel 7 AVD, API 34, `emulator -no-window`, `adb screencap`):

- a crash on first launch (the blocking surface asked to render with nothing blocking);
- the focused flow drawn on the launch theme's dark window, dark text at about 1.4:1;
- Material's purple baseline on the navigation bar;
- code in the body font;
- state chips that looked like buttons;
- identifiers on screen (`content_explanation`, `learn_overview · study day`, "Expressions Io", "explain pipeline", `2026-10-26`).

## 4. Code without markup

Shipped lessons and prompts carry code as paragraphs of their own, set apart by blank lines, with no markup. A rule was prototyped over all 1481 shipped prompts and explanations before it was written in Kotlin:

- a line-level classification (indentation, a shell or REPL prompt, code characters; a run of four words or a sentence ending with three words is a sentence);
- a paragraph-level vote (at least one code line, at most half as many sentence lines);
- 56 paragraphs a first version called code were sentences about code ("Kopyalama: b = a satırı …"); the sentence-ending rule moved all 56 to prose, and each of them was read.

The rule only changes the font. It never changes a line, and a paragraph it is unsure about stays prose.

## 5. Not decided here

- Motion (17B), usability (17C), accessibility and theme preferences (17D).
- Content markup for code and inline code in prose (20I).
- Device verification and T6 (19).
