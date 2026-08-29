# Mobile Technology Selection Specification — AMTS-v0

**Stage step:** 9A — Mobil teknoloji seçimi  
**Status:** ACCEPTED — independent 9A QA PASS  
**Decision:** `D-075`  
**Model:** `AMTS-v0 — Android Mobile Technology Selection`  
**Implements:** AŞAMA 8 contracts `UXIA-v0`, `THUX-v0`, `TRUX-v0`, `ASUX-v0`, `SPWX-v0`, `VDSX-v0`, `WFPX-v0`

## 1. Purpose

9A chooses the **platform and UI technology** that will implement the semantics, design system and geometry locked in AŞAMA 8, and states which parts of that choice still require verification against current sources.

It answers one primary question:

> **Bu kontratları hangi teknoloji gerçekten karşılayabilir?**

Primary invariant:

> **The technology choice serves the accepted contracts. It may not weaken, reinterpret or override a single one of them; where a library default conflicts with an accepted contract, the contract wins.**

---

## 2. Binding inputs

- `docs/V1_SCOPE.md` — Android-focused personal app; local-first; **no** realtime multi-device cloud sync; **no** iOS/web/desktop clients in V1; progress preserved across restart/update with migration; backup/export/restore; installable release APK; critical flows independently QA'd on a **real Android device**; the deterministic local core must not collapse when the AI Tutor is absent.
- `WFPX-v0 / D-074` — three window classes (`compact` <600dp, `medium` 600–839dp, `expanded` ≥840dp), invariant destination identity and order, 48dp focused-flow exit targets, 200% text reflow.
- `VDSX-v0 / D-073` — measured token palette per theme, 48dp minimum targets, WCAG 1.4.3/1.4.11 thresholds, no locale-naive case transforms, Turkish glyph coverage including `mono`, platform reduced-motion honoured, no visual severity beyond canonical severity.
- `UXIA-v0 / D-068` — four peer destinations; compact renders a bottom bar, larger windows a rail.
- `SPWX-v0` / `TRUX-v0` / `ASUX-v0` — state exposed to assistive technology as text; AI absence degrades to visible pending states, never fabricated results.
- `docs/AI_AGENT_WORKFLOW.md` §3 — "which should we choose?" and "framework/library currency" are Research AI questions.

9A changes no accepted semantic, state, label, tone or geometry.

---

# 3. Scope boundary

## 3.1 9A decides

- the target platform and whether a cross-platform UI layer is used,
- the implementation language,
- the UI toolkit,
- the role of a component library relative to `VDSX-v0` tokens,
- the deterministic-core dependency rule,
- how the three `WFPX-v0` window classes are realised,
- the accessibility capability mapping,
- the locale and casing strategy,
- the `minSdk` policy,
- the release artifact policy,
- the bounded verification list to resolve at 10A.

## 3.2 9A does not decide

- storage engine and local-first persistence mechanics → 9B,
- domain data model → 9C,
- module and service boundaries → 9D,
- AI integration architecture and provider → 9E,
- test strategy and tooling → 9F,
- dependency-injection and navigation libraries → 10A,
- build pipeline detail → 19,
- any accepted AŞAMA 8 semantic, state, label, tone or geometry.

No library version, benchmark number or "currently best framework" claim is canonical in 9A.

---

# 4. Selection

## 4.1 Android native, no cross-platform UI layer in V1

A cross-platform toolkit exists to amortise one codebase across several platforms. V1 ships to **one** platform and explicitly excludes iOS, web and desktop. The benefit is unavailable; the cost — an extra abstraction between the app and the platform's accessibility, locale and adaptive APIs — is paid immediately.

The three places that cost would land are exactly the three places AŞAMA 8 made contractual: screen-reader state exposure, locale-correct Turkish casing, and window-class adaptive navigation.

**Decision:** Android native. No cross-platform UI framework in V1.

## 4.2 Kotlin

**Decision:** Kotlin as the implementation language.

## 4.3 Jetpack Compose

`VDSX-v0` defines the interface as tokens and states, and `SPWX-v0` defines a Skill's appearance as a deterministic projection of canonical state. A declarative, state-driven toolkit expresses that directly; an imperative view hierarchy would require manually re-synchronising view state with canonical state, which is precisely the class of bug that produces a UI claiming something the state does not assert.

**Decision:** Jetpack Compose as the UI toolkit.

## 4.4 A component library is a substrate, never a source of truth

Material 3 may be used for component mechanics — touch handling, layout primitives, adaptive navigation scaffolding.

Binding rules:

- **Dynamic colour (Material You) must be disabled.** It derives the palette from the user's wallpaper, which would discard the measured palette, invalidate the per-theme contrast evidence, and destroy the deliberate violet-not-amber hue policy that keeps the palette from reading as a severity ramp.
- Library default colours and typography must not reach the screen; `VDSX-v0` tokens are mapped onto the theme and are authoritative.
- No component may introduce a state, tone or affordance the owning surface spec does not define.
- Where a library default conflicts with an accepted contract, **the contract wins**.

## 4.5 The deterministic core is dependency-isolated

V1 success criterion 8 requires the deterministic local core to survive the AI Tutor's absence. A convention will not hold that line across years of development; a dependency boundary will.

**Decision:** the domain core — curriculum graph, mastery, retention, prerequisite, planner, evidence — is **pure Kotlin with no dependency on Android APIs, the UI toolkit, networking or any AI client**.

Two consequences:

- the core is testable and runnable without a device or a network,
- if another platform is ever added, the core is the portable asset and the change is a re-skin rather than a rewrite.

Exact module and service boundaries belong to 9D; 9A locks the rule, not the layout.

---

# 5. Window classes

The platform's window size classes map one-to-one onto `WFPX-v0`:

| `WFPX-v0` class | Width | Shell |
|---|---|---|
| `compact` | < 600dp | bottom navigation bar |
| `medium` | 600–839dp | navigation rail |
| `expanded` | ≥ 840dp | navigation rail + optional detail pane |

The platform concept and the specification agree by construction. Destination identity and order remain invariant across classes, and no class may add, remove or reorder a region.

---

# 6. Accessibility capability mapping

Each accepted requirement maps to first-class platform behaviour rather than to something re-implemented:

| Accepted requirement | Platform mechanism |
|---|---|
| state exposed as text | screen-reader semantics on every state-bearing component |
| 200% text without losing state | scalable text units and system font-scale support, with reflowing layout |
| 48dp minimum targets | platform minimum touch-target guidance, enforced on exit/pause affordances |
| focus indicator visible at 3:1 | focus semantics with `VDSX-v0` `focus_ring` token |
| reduced motion loses no information | platform animation-reduction setting honoured |
| meaning never carried by colour alone | text label plus non-colour differentiator on every tone |

Accessibility here is contractual, not aspirational: these are already locked by `VDSX-v0` and `WFPX-v0`, and 9A only names how they are satisfied.

---

# 7. Locale and casing

The interface language is Turkish and the `SPWX-v0` state labels are locked Turkish strings.

Binding rules:

- **Default-locale case transforms are forbidden in the codebase.** Any case conversion must pass an explicit locale.
- Turkish casing must round-trip correctly: `i ↔ İ` and `ı ↔ I`.
- Locked `SPWX-v0` labels are rendered as authored and are never case-transformed for styling.
- Chosen typefaces must cover `ı İ ş Ş ğ Ğ ç Ç ö Ö ü Ü`, including in the `mono` role.
- Identifier and technical content comparisons must not use locale-sensitive casing, to avoid the Turkish dotted/dotless `i` corrupting a lookup.

---

# 8. `minSdk`, target device and release

## 8.1 `minSdk` is a policy

`minSdk` is the **lowest API level that supports the required accessibility, locale, adaptive-layout and date/time behaviour without compatibility shims that weaken any of them**.

Working default: **API 26**, chosen because the modern date/time API is available natively from that level and this product is scheduling- and history-heavy; supporting lower levels would add a desugaring dependency in exactly the code that computes retention and review timing.

This is a product default, not a scientific value. It must be confirmed at 10A against the **actual target device**, which is not yet recorded in this repo.

## 8.2 Release artifacts

- An installable **APK** is required by V1 release criterion 10 and must remain producible for direct install and device QA.
- An app bundle may additionally be produced if store distribution is ever wanted; V1 does not require it.
- Critical flows must pass independent QA on a real Android device per V1 release criterion 9.

---

# 9. Bounded verification list — resolve at 10A

`AI_AGENT_WORKFLOW.md` §3 assigns "which should we choose?" and "framework/library currency" to Research AI. The selection logic in §4 follows from accepted contracts and does not depend on current ecosystem data. The following do, and are **explicitly unresolved here**:

1. Current recommended Compose and Material 3 adaptive-navigation APIs for the three window classes.
2. Current recommended mechanism for disabling dynamic colour and supplying a fixed token palette.
3. Current minimum API level that satisfies §8.1 without weakening shims, checked against the actual target device.
4. Current screen-reader semantics APIs for exposing state text and focus order.
5. Current platform mechanism for detecting the reduced-motion setting.
6. Whether any accessibility or locale behaviour named in §6–§7 requires a compatibility library at the chosen `minSdk`.

None of these can change the §4 selection; they determine how it is implemented. Each must be answered with a current source at 10A and recorded there.

---

# 10. Anti-patterns explicitly rejected

- adopting a cross-platform UI layer to serve platforms V1 explicitly excludes,
- letting dynamic colour or any library default palette reach the screen,
- letting a component library introduce a state, tone or affordance no surface spec defines,
- allowing the domain core to depend on Android APIs, the UI toolkit, networking or an AI client,
- calling a default-locale case transform anywhere in the codebase,
- case-transforming locked `SPWX-v0` labels for styling,
- re-implementing accessibility behaviour the platform already provides contractually,
- asserting a library version, benchmark or currency claim as canonical without a current source,
- treating `minSdk` as a fixed number rather than a policy verified against the real device,
- deciding storage, data model, service boundaries, AI integration or test strategy in this step.

---

# 11. 9A acceptance contract

9A can be accepted only if independent QA verifies at minimum:

1. The selection serves the accepted contracts and weakens none of them; on conflict, the contract wins.
2. Android native is chosen with no cross-platform UI layer in V1, justified by V1's single-platform scope.
3. Kotlin and Jetpack Compose are chosen, with the declarative/state-driven rationale recorded.
4. A component library is a substrate only, and `VDSX-v0` tokens are authoritative.
5. **Dynamic colour is explicitly disabled**, with the measured-palette rationale recorded.
6. The domain core is declared free of Android, UI, network and AI dependencies.
7. The three window classes map exactly onto `WFPX-v0`, with invariant destination identity and order.
8. Every accepted accessibility requirement is mapped to a platform mechanism.
9. Default-locale case transforms are forbidden and Turkish casing round-trips correctly.
10. Locked `SPWX-v0` labels are never case-transformed for styling.
11. `minSdk` is expressed as a policy with a stated working default and a device-verification requirement.
12. An installable APK remains producible and real-device QA is required.
13. The bounded verification list exists, is non-empty, and is assigned to 10A.
14. 9B/9C/9D/9E/9F and 10A boundaries remain open and no library is named for them.
15. Stage 6, Stage 7 and all of AŞAMA 8 accepted contracts still validate.

---

# 12. Handoff after acceptance

If accepted, 9A becomes `AMTS-v0 / D-075`.

Next numbered step:

**9B — Veri saklama / local-first**

9B will define local-first persistence: how granular Skill/Objective evidence, assessment records and years of history are stored, how curriculum data is separated from user state, and how migration, backup, export and restore behave. It must receive a fresh PRE-STEP and explicit user approval before execution.
