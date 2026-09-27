# Prerequisite Engine Implementation Specification — PRQX-v0

**Stage step:** 12B — Prerequisite Engine  
**Status:** ACCEPTED — independent 12B QA PASS  
**Decision:** `D-093`  
**Model:** `PRQX-v0 — Prerequisite Engine`  
**Prerequisite contract:** `PRG-v0` (`docs/PREREQUISITE_POLICY_SPEC.md`)  
**Graph contract:** `KGC-v0` (`docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`)  
**Mastery engine:** `MSTX-v0 / D-092`  
**Presentation:** `SPWX-v0 / D-072`  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

12B builds the second engine: the gate that decides whether work on a target would produce evidence anyone could interpret.

It answers one primary question:

> **Bu hedef üzerinde çalışmak yorumlanabilir ve adil bir kanıt üretir mi?**

Primary invariant:

> **A missing prerequisite holds back only the work that really depends on it.** Review due is not forgetting, a soft gap never locks anything, priority cannot buy its way past a hard prerequisite, and a blocked candidate is a candidate that waits — never a need that failed.

---

# 2. What was found before writing any engine code

- **Every authored edge is still `draft`.** All 950 edges in `curriculum/decomposition` carry `lifecycle_status: draft`, 851 of them hard. `KGC-v0` §27 gives a draft entity no runtime selection — so the obvious reading, "ignore draft edges", would have silently dropped every hard prerequisite the moment content shipped.
- **One strictness profile exists.** Every edge names `default_prg_v0`, and `KGC-v0` §12.2 says a profile only expresses `PRG-v0` metadata. There is no second profile whose meaning could be implemented.
- **`contamination_risk_if_missing` has no column.** `PRG-v0` §4.2 names it as a strictness trigger; `DDM-v0` never gave the edge that field, and 10D forbids inventing one.
- **Retention and weakness have never been written.** Their engines are Stage 13.
- **`skill_state` carries four engines' axes under one watermark.** If each engine wrote its own axis there under its own watermark, the row would claim to be as current as its newest writer while carrying another engine's older axis.
- **Two per-Skill questions look alike and are not.** `prerequisite_readiness` is what a Skill is worth *as a prerequisite*; `prerequisite_axis_state`, which drives `prerequisite_unresolved`, is whether a Skill's *own* prerequisites are resolved.
- **The "contaminated" value lived in the adapter.** Core could not produce the one value the mastery engine reads back without repeating a string.

---

# 3. Scope boundary

## 3.1 12B decides

- a Skill's readiness as a prerequisite, from the axes their engines wrote,
- a candidate's eligibility from its graph edges and its exact task's requirements,
- which edges are in force and what is wrong with the ones that are not,
- the snapshot an attempt records when its candidate should have waited,
- how the `prerequisite_readiness` projection is rebuilt and what provenance it carries.

## 3.2 12B does not decide

- what to do about a blocked candidate, repair needs and independent branches → 12C,
- replan triggers, reverse invalidation, and assembling `skill_state` under one watermark → 12D,
- user-facing text from the reason inputs → 12E,
- retention and weakness values → 13,
- undeclared prerequisites found during evaluation → 14,
- promoting authored edges out of `draft`, and storing `contamination_risk_if_missing` → 15,
- any index on `skill_prerequisite_edge` → 18, if a bounded scan is ever shown to matter.

No priority is an input and no readiness is a number.

---

# 4. Readiness

`PRG-v0` §3's four values, read from the axes `MSBX-v0` lets `PRG-v0` read:

| Mastery axis | Retention axis | Weakness axis | Readiness |
|---|---|---|---|
| any | any | `remediation_required` | `not_ready` |
| `confirmation_verification_due` | any | not open | `uncertain` |
| not confirmed, or none written | any | not open | `not_ready` |
| `confirmed_current` | `review_due` | not open | `ready_due` |
| `confirmed_current` | `verification_due`, `at_risk` | not open | `uncertain` |
| `confirmed_current` | `fresh`, `stable`, or not yet evaluated | not open | `ready` |

**An axis nobody has evaluated yet is named, never read as bad news.** Until the retention engine exists, nothing has said a confirmed Skill needs review, and inventing that it does would lock work on no evidence at all. The decision carries `retention_not_yet_evaluated` so the gap is visible. Missing mastery is the exception, because `PRG-v0` §3 itself says a Skill that is not mastered is `not_ready`.

**A closed remediation does not unblock by itself** (§12): readiness needs confirmed mastery, which only fresh evidence produces.

---

# 5. Eligibility

`PRG-v0` §4 and §5 applied to one candidate:

| Readiness | Hard, normal | Hard, strict | Soft |
|---|---|---|---|
| `ready` | eligible | eligible | eligible |
| `ready_due` | eligible | eligible | eligible |
| `uncertain` | conditional_eligible | blocked | eligible_with_support |
| `not_ready` | blocked | blocked | eligible_with_support |

- **Strict** means the prerequisite Skill is a `critical_prerequisite`, or the candidate asked for strict confidence — as a high-stakes assessment or transfer task may (§4.1).
- **A task-level requirement is always hard** (§6), whether or not the graph names it; a Skill needed both softly and by the task is needed hard.
- **Only dependent work waits** (§9). The gate reads only what is declared, so an independent branch, the blocker's own teaching task (§17) and a parallel English track carry on, and English is never a hidden technical prerequisite (§16).
- **The gate fails closed**: a readiness it was not given is `not_ready`.
- **There is no priority input and no failure field.** Priority cannot override eligibility (§8), and a blocked need is not a failed one.
- The decision is a pure function of its inputs and is ordered by Skill identity, so the same state gives the same decision in whatever order it arrives (§21).

---

# 6. The graph

| Edge lifecycle (`KGC-v0` §27) | Effect |
|---|---|
| `published`, `deprecated` | in force — `deprecated` is not `wrong` |
| `retired` | not in force: deliberately taken off the route |
| `draft` | reported as `edge_not_published`; the candidate waits |
| `invalidated` | reported as `edge_invalidated` |
| anything else | reported as `unknown_edge_lifecycle` |

- **A draft edge is neither dropped nor quietly enforced.** Dropping it lets a task through on a prerequisite the learner may not have; enforcing it would treat unpublished authoring as runtime truth. Naming it makes the candidate `invalid_prerequisite_metadata`, which waits, and nothing is attributed to the target meanwhile.
- The **newest `edge_version`** of an edge is the one in force.
- `default_prg_v0` is the **only strictness profile**; any other is a metadata problem, not a guess.
- A **cycle** through the target is `prerequisite_cycle`. The walk reads each Skill's incoming edges at most once, so it is bounded by the graph, not by the number of paths through it.
- Readiness is **not computed transitively**: a Skill's readiness is its own confirmed mastery, so a gap in an unmastered Skill already shows as that Skill being `not_ready`.

Metadata problems: `unpublished_target`, `unpublished_required_skill`, `self_requirement`, `prerequisite_cycle`, `edge_not_published`, `edge_invalidated`, `unknown_edge_lifecycle`, `unknown_strictness_profile`.

---

# 7. Contamination

A candidate that should have waited — `blocked` or `invalid_prerequisite_metadata` — gives an attempt the snapshot `contaminated` (§13). The mastery engine already excludes such evidence as `prerequisite_contaminated`, so a failure on something never taught stays in history and is never written against the target. The value is now core's (`PrerequisiteSnapshot.CONTAMINATED`), and the adapter reads it instead of owning it.

---

# 8. The projection

`RebuildReadiness` writes one `prerequisite_readiness` row per Skill — the one state family `PRG-v0` owns — and nothing else.

- It **does not write `skill_state`.** That row carries four engines' axes under one watermark, so assembling it belongs where recomputation is orchestrated (12D), under one watermark read. The same finding applies to 12A's carry; it is harmless today only because no other engine writes that row.
- Its **watermark is the mastery row's**, or `0` when there is none: a derived projection cannot claim to have seen more truth than its input had.
- With no curriculum published there is nothing to pin to, so nothing is written.
- Rebuilding from the same state writes the same row.

---

# 9. Port refinements

Two refinements of `PersistencePort`, no new interface; the port count stays four.

- `skill(ref)` — the gate needs `critical_prerequisite`, and must tell an unpublished Skill from an unlearned one.
- `prerequisiteEdgesInto(target)` — every version of every edge into one pinned target, **in every lifecycle**. The store filters nothing: which edges gate is the engine's decision, and a store that dropped `draft` edges would make a missing hard prerequisite invisible.

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

`data-persistence` may not depend on `core-engines` or `core-application`, so the proof is split as in 11B and 12A: the gate's decisions are T1 checks, and the storage guarantees — an unfiltered, version-pinned graph read that writes nothing, and contamination reaching the mastery engine — are T2 against real SQLite.

**Mutation 44/44.** Every mutant had to be caught by a test, not by the compiler, and the harness refuses to classify a run that shows no Gradle verdict. A control mutant that changes only a comment survives, which proves the harness can report a survivor at all. **P29 survived the first run**: removing the sort that makes a decision independent of input order left the determinism test green, because graph edges already arrive sorted and the test never reordered a task's own requirements. The test now does; it was a gap in the test, not in the rule.

**Not run: T6.** The phone was not connected, and no planner asks the gate yet, so no prerequisite decision has ever been made outside a test.

---

# 11. Anti-patterns explicitly rejected

- Topic completion used as prerequisite mastery, or a whole domain locked for one missing Skill,
- `review_due` made `not_ready`, or a soft gap hard-locking a task,
- priority bypassing the gate,
- an untaught prerequisite's failure written against the target,
- a started Topic re-locked on regression, or a critical label freezing the curriculum,
- English as a hidden technical prerequisite,
- a completed remediation counted as readiness without new evidence,
- a blocked need recorded as failed,
- a draft edge silently dropped,
- readiness as a number,
- an unevaluated axis read as bad news.

---

# 12. 12B acceptance contract

1. `PRQX-v0` is the accepted prerequisite engine.
2. Readiness has `PRG-v0`'s four values and eligibility its five; neither is a number.
3. The eligibility matrix equals `PRG-v0` §4 and §5, and priority is not an input.
4. `review_due` does not block, a soft gap never blocks, and only dependent work waits.
5. A task-level requirement is hard even when the graph does not name it.
6. Draft, invalidated and unknown edges, unknown strictness profiles, self requirements and cycles are named metadata problems.
7. An unevaluated axis is named and never read as bad news; the gate fails closed on a missing readiness.
8. Work on a candidate that should have waited is recorded as contaminated evidence.
9. Only `prerequisite_readiness` is written, with provenance that cannot claim more than its input saw.
10. The schema is unchanged, no migration or index was added, and the port count stays four.
11. Nothing is claimed that was not run; T6 was not run.
12. Independent 12B QA passes and Stage 6–12A regressions pass.

---

# 13. Handoff after acceptance

If accepted, 12B becomes `PRQX-v0 / D-093`.

Next numbered step: **12C — Planner Engine v1**. It must receive a fresh PRE-STEP and explicit user approval before execution. Open loops carried: the planner that asks the gate and builds repair needs (12C), replan triggers, reverse invalidation and one-watermark `skill_state` assembly (12D), reason text (12E), retention and weakness axes (13), undeclared prerequisites during evaluation (14), promoting authored edges and storing `contamination_risk_if_missing` (15), calibration (18C) and the T6 device run.
