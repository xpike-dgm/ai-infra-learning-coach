# Mastery Engine Implementation Specification — MSTX-v0

**Stage step:** 12A — Mastery Engine v1  
**Status:** ACCEPTED — independent 12A QA PASS  
**Decision:** `D-092`  
**Model:** `MSTX-v0 — Mastery Engine v1`  
**Mastery contract:** `GRE-v0` (`docs/MASTERY_FORMULA_V0.md`)  
**Presentation:** `SPWX-v0 / D-072`  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085`  
**Boundaries:** `MSBX-v0 / D-078`  
**AI:** `AIAX-v0 / D-079`

## 1. Purpose

12A builds the first engine: evidence is interpreted from recorded attempts, and mastery is projected from that evidence.

It answers one primary question:

> **Bu deneme gerçekte neyi kanıtlıyor — ve neyi kanıtlamayı reddediyor?**

Primary invariant:

> **Mastery asks one question: can the learner do it without help?** Assisted work, exposed solutions, unverified evaluations, contested items and work done on a contaminated prerequisite are all kept out of the score. None of that is a penalty — they are answers to a different question.

---

# 2. What was found before writing any engine code

- **Nothing wrote evidence.** 11B and 11D both refused to, deliberately, and recorded the pipeline as 12's. So mastery had nothing to read, and the four axes `DDM-v0` reserved had never been written by the product.
- **`GRE-v0`'s per-Objective gate profile has no columns.** `min_independent_groups`, `min_variant_families`, `requires_non_basic_evidence`, `requires_user_authored_artifact` and `requires_transfer` are `KGC-v0` authoring fields; `DDM-v0`'s `objective` table names only `required`, `criticality` and the evidence types. They therefore travel as authored content, exactly as 11D's item metadata does, and fall back to `GRE-v0`'s declared defaults rather than to a number invented here.
- **The group rubric does not exist yet.** `GRE-v0` §4 wants an objective-specific rubric to turn a testlet into one `q_g`. Until rubrics exist (14), the mean of the group's rows stands in — and it can never *raise* the group count, which is the guard that actually matters.

---

# 3. Scope boundary

## 3.1 12A decides

- which evidence may enter a mastery score, and why each excluded row was excluded,
- how correlated evidence is grouped and how far back the window reaches,
- the Objective gates, the Skill aggregation and the hysteresis around a contradiction,
- what an attempt's evaluation is written as,
- how the mastery projection is rebuilt and what provenance it carries.

## 3.2 12A does not decide

- prerequisite readiness → 12B (the axis this engine leaves untouched),
- what to measure next and when to recompute → 12C, 12D,
- user-facing reason codes built from the trace → 12E,
- retention, remediation and the weakness axis → 13,
- rubrics and evaluator behaviour → 14,
- authored Objective gate profiles → 15,
- calibration of the threshold, window and minimums → 18C.

No threshold is calibrated here, and no number acquires a meaning `GRE-v0` did not give it.

---

# 4. What may enter a score

`GRE-v0` §3.1's conditions are a single function that returns **the rules a row failed**, so an exclusion can always be explained rather than merely happening:

| Excluded because | Meaning |
|---|---|
| `not_direct_evidence_for_objective` | it is not the Objective's own direct evidence type |
| `not_independent` | help was taken: H1–H4 is formative, not a mastery score |
| `evaluator_not_verified` | a provisional evaluation cannot settle a gate |
| `outcome_invalid` | it could not be evaluated |
| `contested` | the learner reported the item; it is held, not counted |
| `prerequisite_contaminated` | it failed on something never taught |
| `solution_exposed` | the answer had already been seen |
| `no_group_result` | there is no `q_g` to average |

**None of these is a penalty.** Assisted evidence is kept, can trigger a recheck and informs remediation; it simply is not an answer to "can they do this alone?".

---

# 5. Grouping, window and score

- A **dependency group is one group** (`GRE-v0` §4): answering five parts of one question is one piece of evidence. The same item repeated cannot inflate independent evidence.
- The window is the **last five** eligible independent direct groups (§5). Recency is a bounded window, not a decay multiplier.
- The score is the **equal-weighted mean** of those groups. There is no assistance, evaluator, difficulty or recency multiplier — `GRE-v0` removed them all rather than invent uncalibrated coefficients at cold start.

Every constant here — `5`, `0.80`, `2`, `3`, `2` — is `GRE-v0`'s declared cold-start heuristic, to be calibrated at 18C. None is a probability, a confidence or an ability estimate, and none is rendered as a percentage anywhere.

---

# 6. Gates and aggregation

A standard Objective passes when the score clears the threshold, the window holds enough independent groups from enough variant families, any required direct type actually appears, and no recheck or verification is unresolved. A critical Objective needs more groups and **cannot be passed on basic evidence alone**. An Objective may additionally require the learner's own artifact or transfer evidence.

A Skill is mastered when **every required and every critical Objective passes on its own** (§14). The aggregation is deliberately non-compensatory: an average would let a brilliant result on one Objective hide a missing one, which is exactly the failure mode this product exists to avoid.

---

# 7. Hysteresis

The **first** clean, prerequisite-valid, independent contradiction of confirmed work opens `verification_due` and **keeps** mastery: one bad day is not evidence that knowledge vanished.

If verification is already open and the fresh check contradicts it too, that is no longer noise: mastery is no longer held up, the gates decide again, and the verification is **resolved** rather than left open forever. Both halves are tested, because collapsing either one produces a different product — one that panics, or one that never updates.

---

# 8. Writing evidence

`RecordEvidence` turns one submitted attempt into one evidence row per targeted Objective, in one transaction, with the Objective's version pinned in its own row.

- **A pending evaluation writes nothing.** A refusal, a timeout or an unusable answer is not a wrong answer (`AIAX-v0`), and there is no code path that turns one into evidence.
- **The axes are never collapsed**: outcome, evaluator status, independence and contested are separate fields, so a provisional result can never be read as settled.
- **`not_reliably_measured` is `invalid` with no result** — not a zero. A zero is something the learner did; "we could not tell" is not.
- **Independence comes from the assistance that was recorded**, never inferred from how the answer looks.

---

# 9. The projection

Mastery is **rebuilt, never edited**. Everything the projection says is a function of the evidence that exists now, so dropping it and rebuilding produces the same row — which is what `LFPS-v0` means by rebuildable, and what stops mastery from drifting from its evidence.

It writes only the axis this engine owns (`MSBX-v0` §engine_ownership); retention, readiness and weakness are carried as whatever their engines last wrote. Every row carries `policy_version`, `truth_watermark`, `built_at_instant` and `input_curriculum_version`, and the watermark is read **before** the evidence, so a row written while a new attempt lands is detectably stale rather than silently wrong. With no curriculum published there is nothing to pin to, so nothing is written at all.

`primary_presentation_state` is reported as the mastery axis for now: `SPWX-v0` derives it from all four axes, and inventing one from a single axis would be exactly the "primary state claimed as the whole truth" that contract forbids. It becomes real when 12B and 13 supply their axes.

---

# 10. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

`data-persistence` may not depend on `core-application`, so the proof is split exactly as 11B split it: the use cases' shape is proven at T1 with a recording store, and the storage guarantees — atomic attribution, version-pinned reads, projection provenance — at T2 against real SQLite.

**Not run: T6.** The phone was not connected, and nothing in the app produces an evaluation yet, so no mastery decision has ever been made outside a test.

---

# 11. Anti-patterns explicitly rejected

- assisted work counted as independent mastery, or a provisional evaluation settling a gate,
- an exposed solution counted as fresh evidence,
- a contaminated prerequisite counted against the learner,
- the same item repeated inflating independent groups,
- a compensatory average across Objectives,
- instant unmastery on one contradiction, or a verification left open forever,
- an unmeasurable answer scored as zero, or a pending evaluation written as evidence,
- a mastery percentage, probability or confidence,
- an engine writing another engine's axis,
- a projection written without provenance.

---

# 12. 12A acceptance contract

1. `MSTX-v0` is the accepted mastery engine.
2. Eligibility, grouping, window, gates, aggregation, hysteresis and support band equal `GRE-v0`.
3. Every declared constant is `GRE-v0`'s, recorded as an uncalibrated cold-start heuristic.
4. Assisted, exposed, unverified, contested and contaminated evidence never enters a score.
5. A dependency group is one group; a repeated item cannot inflate independent evidence.
6. A Skill is mastered only when every required and critical Objective passes on its own.
7. The first clean contradiction opens verification; a failed recheck lets the gates decide again.
8. A pending evaluation writes no evidence, and an unmeasurable answer is not a zero.
9. The projection is rebuilt from evidence, carries full provenance, and writes only its own axis.
10. No percentage, probability or confidence is produced anywhere.
11. The schema is unchanged, no migration was added, and the port count stays four.
12. Nothing is claimed that was not run; T6 was not run.
13. Independent 12A QA passes and Stage 6–11E regressions pass.

---

# 13. Handoff after acceptance

If accepted, 12A becomes `MSTX-v0 / D-092`.

Next numbered step: **12B — Prerequisite Engine**. It must receive a fresh PRE-STEP and explicit user approval before execution. Open loops carried: the readiness axis this engine leaves untouched (12B), the trigger that recomputes after an attempt and what to measure next (12C, 12D), reason codes from the decision trace (12E), retention and weakness (13), rubrics and evaluator behaviour (14), authored gate profiles (15), calibration (18C) and the T6 device run.
