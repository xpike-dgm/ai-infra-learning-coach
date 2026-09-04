# Navigation Shell Specification — NSHX-v0

**Stage step:** 10B — Navigation  
**Status:** ACCEPTED — independent 10B QA PASS  
**Decision:** `D-083`  
**Model:** `NSHX-v0 — Navigation Shell`  
**Information architecture:** `UXIA-v0 / D-068`  
**Geometry:** `WFPX-v0 / D-074`  
**Boundaries:** `MSBX-v0 / D-078`  
**Project:** `MPSX-v0 / D-082`

## 1. Purpose

10B builds the shell `UXIA-v0` specified, on the adaptive APIs 10A pinned.

It answers one primary question:

> **IA kuralları nerede yaşar ve shell onları nasıl render eder?**

Primary invariant:

> **The navigation rules live in `core-presentation`, not in the UI toolkit.** The destination set, its order, which surfaces are shared, which transitions exist and where a focused flow returns are pure functions — so a violation is a failing test on a laptop rather than something noticed on a phone.

---

# 2. Why the model is not in Compose

A `NavHost` full of route strings is the ordinary way to build this, and it puts four accepted decisions inside the UI toolkit: the destination set, its order, the shared-surface identity rule and the focus-flow return rule. `MSBX-v0` already refused that for presentation state, for the same reason — the most safety-critical labelling in the product would then only be testable on a device.

Navigation is the same class of decision. `UXIA-v0` forbids "different contradictory Skill detail pages under Learn and Progress"; that is a claim about identity, and identity belongs in a model, not in a routing table.

There is a second reason. The IA enumerates contextual edges deliberately, because if any surface could open any other, the path a learner happened to take would start to look like curriculum structure — which §10.2 explicitly forbids. A closed edge set is only enforceable if something owns it.

---

# 3. Scope boundary

## 3.1 10B decides

- where the navigation model lives and what it contains,
- the destination enum, its canonical order and the start destination,
- the surface set: shell roots, shared details and focused flows,
- the closed contextual edge set and total peer switching,
- how a focused flow suspends the shell and keeps its safe exit,
- the focus-flow return rule,
- how window classes map to shell presentations,
- how destinations are exposed to accessibility services.

## 3.2 10B does not decide

- the interiors of the four destinations → 11 and later,
- the design system, theme and tokens → 10C,
- persistence, schema and content loading → 10D,
- app health and diagnostics → 10E,
- the task runner and assessment session interiors → 11–13,
- deep links, notification entry points and search UX,
- final microcopy and destination labels → 14,
- any accepted semantic, boundary or IA decision.

No animation, transition-timing or navigation-performance claim is canonical in 10B.

---

# 4. Destinations

Exactly four, in the order `UXIA-v0` fixed, with `today` as the start:

```text
today → learn → progress → profile
```

The Kotlin enum's declaration order **is** that order. There is no second list to keep in sync, so a reordering has to be a deliberate source change rather than something that drifts. The ids are canonical; the Turkish labels rendered today (`Bugün · Öğren · İlerleme · Profil`) are working microcopy owned by 14.

Assessment, Technical English, the AI Tutor and every engine state remain **not** destinations. `UXIA-v0` lists eleven forbidden top-level ids and a test asserts none of them is one.

---

# 5. Surfaces

| Kind | Surfaces |
|---|---|
| Shell roots | `today_overview`, `learn_overview`, `progress_overview`, `profile_overview` |
| Shared details | `skill_detail`, `topic_detail`, `planner_explanation`, `assessment_report`, `learning_history`, `technical_english_profile` |
| Focused flows | `task_runner_flow`, `assessment_session_flow` |

**One surface object per canonical entity.** Skill detail is reachable from Today, Learn and Progress and is the same object every time. A registry with one route per origin would make the contradictory-detail-page violation easy to write; this makes it unrepresentable, and the validator counts declarations to keep it that way.

---

# 6. The navigation graph

**Peer switching is total.** From any shell root the user reaches any other directly.

**Contextual edges are enumerated and closed**, exactly as accepted in the IA contract. An unlisted transition is not navigable — `profile → skill_detail` and `learn → task_runner_flow` are both refused, and both are tested.

The edge set in code is compared to `ia.yaml` by the validator, so the shell and the accepted IA cannot drift apart.

---

# 7. Focused flows

A focused flow is not a destination. It **suspends** the shell.

`showsShell` and `requiresSafeExit` are derived from the surface rather than passed in, which means the dangerous state — a hidden shell with no way out — cannot be constructed. `TRUX-v0` owns the exit itself and `WFPX-v0` fixes it at a 48dp target; the shell's only job is to not draw navigation over it.

The detail pane is likewise suppressed during a focused flow: an expanded window must not keep a browsing surface alive beside work that is meant to be focused.

---

# 8. The return rule

A completed or paused flow returns to a deterministic place (`UXIA-v0` §9.3):

- normal daily work → `today`,
- a flow opened from an entity context → back to that entity **while the origin is still valid**,
- a stale or invalidated origin → `today`.

The middle case is where a replan can strand someone. Returning to a surface a replan has invalidated would leave the learner looking at something that no longer describes their plan, so validity is an explicit input rather than an assumption. Asking for the return target of a surface that is not a focused flow fails loudly.

---

# 9. Window classes

Breakpoints and shells are `WFPX-v0`'s:

| Class | Width | Shell |
|---|---|---|
| compact | ≤ 599dp | bottom navigation bar |
| medium | 600–839dp | navigation rail |
| expanded | ≥ 840dp | navigation rail with detail pane |

The shell itself is drawn with `NavigationSuiteScaffold`, the adaptive component 10A pinned, which picks the bar or the rail from the current window size. What it must **not** do is decide the thresholds: the class is computed in `core-presentation` from the accepted breakpoints, not from the UI toolkit's own bucketing — if a library changed its default thresholds, the accepted geometry would still win. Boundary widths 599 / 600 / 839 / 840 are tested.

**The window class changes how the shell is drawn and nothing else.** The destination set, its order and every meaning are identical in all three classes, which is `UXIA-v0` §16's requirement that adaptive rendering must not create a second information architecture.

---

# 10. Accessibility

- every destination renders a **text label**; the icon's content description is null because the label already names it, so an icon is never the only carrier of meaning,
- selection is exposed as `stateDescription`, not by colour alone,
- `traversalIndex` follows the canonical destination order, so screen-reader traversal matches the accepted order rather than the layout's accident,
- the APIs are the ones 10A verified.

---

# 11. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-03 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS — APK produced |
| RUN-04 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

RUN-04 matters beyond this step: the shell landed without weakening V1 criterion 8, and the product still builds and runs with no AI adapter present.

---

# 12. Anti-patterns explicitly rejected

- deciding the destination set inside Compose,
- a second destination list that can drift from the enum,
- a separate Skill detail surface per origin,
- allowing an unlisted contextual transition,
- a top-level destination for assessment, English, AI or an engine,
- a focused flow that can hide the shell without an exit,
- changing the destination set or order with the window class,
- computing window breakpoints in the UI toolkit,
- an icon as the only carrier of a destination's meaning,
- stranding the learner on a stale origin after a replan,
- treating browse placement as prerequisite truth.

---

# 13. 10B acceptance contract

1. `NSHX-v0` is the accepted navigation shell.
2. The navigation model lives in `core-presentation`; `app-ui` renders it.
3. Four destinations in the accepted order, `today` first, enum order canonical.
4. No forbidden top-level id is a destination.
5. Shell roots, shared details and focused flows match the accepted surface families.
6. One surface object per canonical entity.
7. Contextual edges equal the accepted IA edge set and are closed.
8. Peer switching between shell roots is total.
9. A focused flow suspends the shell, and shell suppression and safe-exit are derived so the unsafe state is unrepresentable.
10. The return rule is deterministic and cannot strand the learner on a stale origin.
11. Window classes and shells match `WFPX-v0`, are computed in core, and change nothing but presentation.
12. Destinations expose text labels, state text and traversal order.
13. Nothing is claimed that was not run, and the no-adapter build still passes.
14. 10B changes no accepted semantic, boundary, geometry or IA decision.
15. Independent 10B QA must pass, and Stage 6, Stage 7, AŞAMA 8, AŞAMA 9 and 10A regressions must pass.

---

# 14. Handoff after acceptance

If accepted, 10B becomes `NSHX-v0 / D-083`.

Next numbered step: **10C — Design system implementation**. 10C implements `VDSX-v0`'s expression layer and `WFPX-v0`'s measured palette as a Compose theme: `lightColorScheme` / `darkColorScheme` built from the measured tokens with the dynamic colour functions still absent, the six tones mapped to the accepted state vocabulary, `visual_severity <= canonical_severity` enforced, Turkish casing protected against locale-naive transforms, and the 48dp and 200% text guarantees carried into real components. It must receive a fresh PRE-STEP and explicit user approval before execution.
