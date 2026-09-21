# 12A Mastery Engine — Research & Decision Synthesis

**Stage step:** 12A — Mastery Engine v1  
**Purpose:** Turn `GRE-v0` into running code — the first engine, and the first thing in the product allowed to say what an attempt proved.

## 1. Research-need decision

**No web research pass was needed**, and the reason is the interesting part: the temptation here is not a missing idea but a familiar one. BKT, IRT, Elo and "confidence %" are all well documented and all wrong for this product at cold start, which is precisely why `GRE-v0` removed the candidate Beta formula and the fixed `0.80` evaluator confidence before Stage 2 ended. The work was to implement the contract that already made those decisions, not to reopen them with literature.

## 2. Canonical source set reviewed

- `GRE-v0` (`docs/MASTERY_FORMULA_V0.md`) — the whole pipeline: eligibility (§3), dependency/variant guards (§4), the bounded window and equal-weighted mean (§5), the threshold as an engineering heuristic (§6), Objective and critical gates (§8, §9), coding and debugging rules (§10, §11), evaluator status instead of a confidence multiplier (§12), difficulty as a label (§13), non-compensatory Skill aggregation (§14), hysteresis (§16), the support band (§17), and the false-positive and false-negative guardrails (§18, §19) that became tests.
- `SPWX-v0 / D-072` — the eight presentation states, the four axes stored separately, and the rule that a primary state never replaces the axes.
- `DDM-v0 / D-077` — `evidence_event`'s four independent columns, `evidence_event_objective`'s version-pinned attribution, and the provenance every projection row carries.
- `LFPS-v0 / D-076` — evidence is truth, state is a rebuildable projection.
- `MSBX-v0 / D-078` — one engine owns one state family.
- `AIAX-v0 / D-079` — a refusal is not a wrong answer; every non-answer is `evaluation_pending` and writes no evidence.
- 2D assistance contract — H0–H4 and the four evidence-use classes.

## 3. Synthesis problems 12A had to solve

1. **Nothing had ever written evidence**, so the pipeline and the engine had to arrive together without the pipeline quietly becoming a second judge.
2. **`GRE-v0`'s gate profile is authoring metadata with no columns**, and 10D forbids inventing columns.
3. **The rubric that produces a testlet's `q_g` does not exist yet.**
4. **Hysteresis has two halves**, and implementing only the first produces a product that never updates while implementing only the second produces one that panics at a single mistake.
5. **`not_reliably_measured` looks like a zero** to any code that is not careful.
6. **`data-persistence` may not depend on `core-application`**, so the storage proof and the use-case proof had to be split.

## 4. Positions taken

- **Exclusions are returned, not silently applied.** Every row that did not count knows which rule excluded it, and that set is in the decision trace.
- **Defaults come from `GRE-v0`**; the per-Objective profile is authored content with the defaults as fallback, and every constant is labelled as an uncalibrated heuristic owned by 18C.
- **The group stand-in is the mean of the group's rows**, recorded as a stand-in, chosen because it cannot raise the independent-group count.
- **Both halves of hysteresis are implemented and tested**: first contradiction keeps mastery and opens verification; a failed recheck resolves it and lets the gates decide again.
- **`not_reliably_measured` is `invalid` with no `q_g`**, never `negative` with a zero.
- **The projection writes only the mastery axis** and carries the others, and reports `primary_presentation_state` as that axis rather than deriving a whole-truth state from one axis.
- **The proof is split** exactly as 11B split it: shape at T1, storage at T2.

## 5. Explicitly not decided in 12A

Prerequisite readiness (12B), the trigger and selection (12C, 12D), user-facing reason codes (12E), retention, remediation and weakness (13), rubrics and evaluator behaviour (14), authored gate profiles (15), calibration of the threshold, window and minimums (18C). No percentage, probability or confidence is produced anywhere, and nothing in the app reaches the engine yet.
