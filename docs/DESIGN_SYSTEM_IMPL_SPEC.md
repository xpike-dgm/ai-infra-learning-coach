# Design System Implementation Specification — DSIX-v0

**Stage step:** 10C — Design system implementation  
**Status:** ACCEPTED — independent 10C QA PASS  
**Decision:** `D-084`  
**Model:** `DSIX-v0 — Design System Implementation`  
**Design system:** `VDSX-v0 / D-073`  
**Geometry and palette:** `WFPX-v0 / D-074`  
**State vocabulary:** `SPWX-v0 / D-072`  
**Project:** `MPSX-v0 / D-082`

## 1. Purpose

10C turns the accepted expression rules into a Compose theme.

It answers one primary question:

> **Kabul edilmiş ifade kuralları, onları bozamayacak bir temaya nasıl dönüşür?**

Primary invariant:

> **The design system may not add severity the canonical state does not claim** — and where a rule can be made unrepresentable instead of merely reviewed, it is.

---

# 2. Where the tokens live, and why it matters

The obvious place for colour values is the theme file. That would have been a mistake.

If the hex values exist only inside Compose, verifying their contrast needs the UI toolkit, and the natural shortcut becomes asserting a remembered ratio instead of computing one. That shortcut already failed once in this project: at 8G a hand-declared minimum was wrong, and only the recomputation caught it.

So the tokens are plain data in `core-presentation`, the WCAG formulas sit beside them, and the product's own suite **recomputes every ratio** in an ordinary JVM test — no device, no Compose. The theme file's whole job is converting data into Compose types.

---

# 3. Scope boundary

## 3.1 10C decides

- where the design tokens live and in what form,
- how the measured palette becomes a `ColorScheme` in both themes,
- how the six tones are exposed and mapped to tokens,
- how the fault-tone prohibition is enforced,
- how state→tone and qualifier→tone maps are expressed,
- how the touch-target floor and text scaling reach components,
- how state avoids being carried by colour alone.

## 3.2 10C does not decide

- real screen components and destination interiors → 11 and later,
- motion implementation,
- persistence, schema and content loading → 10D,
- app health and diagnostics → 10E,
- final microcopy → 14,
- on-device accessibility verification → 17,
- any accepted visual semantic, palette value or state vocabulary.

No aesthetic-quality or rendering-performance claim is canonical in 10C. **The palette is not revised here**: if a check had failed, the fix would have been the code, never the measured token.

---

# 4. The palette

`WFPX-v0`'s measured palette, copied exactly: 18 tokens per theme, the same token set in both, dark measured separately rather than inverted from light.

There is no green–amber–red ramp. `attention` is violet and red belongs to `system_fault` alone, because a traffic-light ramp would encode learning states as severity levels.

The validator compares every token in the Kotlin source byte-for-byte with the accepted palette, so a quiet adjustment to make a check pass would fail instead.

---

# 5. Contrast is recomputed, never asserted

Both the product test and the repository validator recompute WCAG 2.2 relative luminance from the token hex values and check, **in both themes**:

- text and muted text on both surfaces → ≥ 4.5:1,
- outline and focus ring against both surfaces → ≥ 3:1,
- on-tone text against every one of the six tone containers → ≥ 4.5:1,
- every tone container against the surface → ≥ 3:1.

The validator also re-derives the recorded minima (`6.08` text, `3.79` non-text, `6.06` text-on-tone) from the tokens rather than trusting the stored numbers. Ratios are evidence, not the source of truth.

---

# 6. The fault tone is unrepresentable for a learning state

`VDSX-v0` forbids any learning state from wearing the fault tone. That is easy to state and easy to violate a year later, so it is enforced by the type system:

- `Tone` has six values, including `SYSTEM_FAULT`,
- **`LearningTone` has five, and no fault value exists to assign**,
- `SkillPresentationState.tone` returns `LearningTone`.

There is no code path to review, because there is no expression to write. `SystemFaultState` — `error_recoverable` and `data_recovery_required` — is the only thing that yields the fault tone, and Material's `error` role carries that tone and nothing else, so a component cannot reach for "the error colour" and apply it to a learner's state.

---

# 7. Tone assignment

Each of the eight Skill states has exactly one declared tone, matching the accepted map. Three of them stay neutral on purpose: `not_yet_evidenced`, `confirmed_review_due` and `prerequisite_unresolved` are states of **waiting**, not of failing.

The four qualifiers carry their own tones and never replace the state's.

**Appearing in an attention group does not change a tone.** That rule is a named function returning the state's own tone, so the intent is testable rather than implied by the absence of code — grouping is a grouping fact, not a severity fact.

---

# 8. Targets, scaling and colour

- `minimumTouchTarget()` applies the 48dp floor, the stricter platform rule `VDSX-v0` adopted over WCAG's 24px, as a modifier rather than as per-component discipline,
- supported text scales are 100 / 150 / 200%,
- a state chip always renders its label as **text** and exposes it as `stateDescription`, so colour is never the only carrier of meaning,
- no locale-naive case transform appears anywhere; locked labels render as authored.

---

# 9. Dynamic colour, and what the scan caught

Dynamic colour stays off, and because that is an **absence**, it is enforced by a source scan rather than a unit test.

The scan failed on this step's own **prose**. The theme file's comment explained the rule by naming the two dynamic scheme builders, and a plain text scan cannot tell a comment from a call.

The comment was reworded rather than the gate loosened. Strict absence is the stronger guarantee: a rule that tolerates "mentions but not calls" needs a parser and a judgement call where it currently needs neither.

---

# 10. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-03 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-04 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

---

# 11. Anti-patterns explicitly rejected

- deriving the palette from the wallpaper,
- naming a dynamic scheme builder anywhere, including in prose,
- asserting a contrast ratio instead of recomputing it,
- revising a measured token to make a check pass,
- giving a learning state the fault tone,
- letting a component choose a different tone than the state declares,
- upgrading a tone because the state appears in an attention group,
- conveying state by colour alone,
- an interactive target below the accepted floor,
- a locale-naive case transform on a locked label,
- introducing a visual severity the canonical state does not claim.

---

# 12. 10C acceptance contract

1. `DSIX-v0` is the accepted design-system implementation.
2. Tokens are plain data in `core-presentation`; the theme file only converts them.
3. The palette equals `WFPX-v0`'s measured values exactly and is not revised here.
4. Contrast is recomputed from hex in both the product suite and the validator, in both themes.
5. The recorded minima are re-derived rather than trusted.
6. `LearningTone` has no fault value, so the forbidden assignment cannot be written.
7. Material's `error` role carries the system fault tone and nothing else.
8. Each Skill state has exactly one declared tone, matching the accepted map.
9. Attention-group membership never changes a tone, and that is a tested function.
10. The 48dp floor is a modifier; text scales to 200%.
11. State is always available as text, never as colour alone.
12. No dynamic scheme builder and no locale-naive case transform appears anywhere.
13. Nothing is claimed that was not run, and the no-adapter build still passes.
14. 10C changes no accepted visual semantic, palette value or state vocabulary.
15. Independent 10C QA must pass, and Stage 6, Stage 7, AŞAMA 8, AŞAMA 9, 10A and 10B regressions must pass.

---

# 13. Handoff after acceptance

If accepted, 10C becomes `DSIX-v0 / D-084`.

Next numbered step: **10D — Local database**. 10D implements `DDM-v0`'s physical schema on the `androidx.sqlite` bundled driver pinned at 10A: the three store regions, `(logical_id, version)` composite keys with version-carrying foreign keys, truth tables with **no UPDATE or DELETE path** enforced by the storage layer and proven by attempted violation, the four evidence axes as four columns, three-value time on every timestamped row, projection provenance with policy version and truth watermark, indexed and permanent exposure records, and forward-only migrations testable against populated fixtures. It must receive a fresh PRE-STEP and explicit user approval before execution.
