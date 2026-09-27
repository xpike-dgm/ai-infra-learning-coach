# Planner Engine Implementation Specification — PLNX-v0

**Stage step:** 12C — Planner Engine v1  
**Status:** ACCEPTED — independent 12C QA PASS  
**Decision:** `D-094`  
**Model:** `PLNX-v0 — Planner Engine v1`  
**Capacity:** D-033 (`docs/ADAPTIVE_PLANNER_SPEC.md`)  
**Task taxonomy:** D-034 (`docs/TASK_TAXONOMY_SPEC.md`)  
**Priority:** `PBR-v0` (`docs/PRIORITY_POLICY_SPEC.md`)  
**Explainability:** `PDT-v0` (`docs/PLANNER_EXPLAINABILITY_SPEC.md`)  
**Prerequisite engine:** `PRQX-v0 / D-093`  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

12C builds the planner: from current state, which open needs are served today, by which task, within the minutes the learner actually has — and a trace that can say why.

It answers one primary question:

> **Bugün hangi açık ihtiyaçlar, hangi görevle, öğrencinin gerçekten ayırdığı süre içinde karşılanıyor?**

Primary invariant:

> **Semantic priority first, physical fit second.** Priority never rescues a blocked or untrusted task, capacity never rewrites priority, the day is never lengthened to fit more, and a need no task served stays open — it is not tomorrow's debt and not a failure.

---

# 2. What was found before writing any planner code

- **`planned_task` cannot hold a Today row.** It carries the Skill and the position; purpose, title, activity, minutes and reasons have no column. 11A recorded this as 12's.
- **No authored task exists.** `curriculum_package/1` has no task section, so every need a real store opens today has no candidate.
- **No starvation threshold exists anywhere.** `PBR-v0` §6.5 leaves it to 18B/18C, and 7C's QA explicitly refused to invent one.
- **Retention and weakness have never been written** (13), so there is no overdue bucket, no `at_risk` and no weakness-derived need yet.
- **Every authored Skill is `draft`**, like every authored edge (12B).
- **The per-need candidate cap is 12C's** (3B §17); nothing else sets it.

---

# 3. Scope boundary

## 3.1 12C decides

- today's capacity from the learner's settings (D-033),
- which needs current state opens, and from which engine's axis,
- which candidates are trusted enough to consider, and how many per need,
- the band and rank of every need (`PBR-v0`),
- what is selected, split, substituted or deferred, and why,
- how the plan and its trace are stored.

## 3.2 12C does not decide

- replan triggers, re-entry, paused continuation, calling the planner, and `skill_state` assembly → 12D,
- user-facing reason text, and Today reading the plan → 12E,
- the 3H virtual-user scenarios against this code → 12F,
- retention due buckets, `at_risk` and weakness needs → 13,
- authored tasks and their format → 15,
- the capacity setting itself → 16D,
- starvation and track-balance thresholds → 18C,
- the candidate cap against real performance → 18E.

No priority is a number, nothing is summed, and no tie is broken at random.

---

# 4. Capacity

D-033's resolution order, first value present wins: **today's override → the profile chosen for the day → the scheduled default → the normal profile.** The learner's number is the hard budget and is never exceeded.

- The planning budget keeps D-033's `0.10` reserve: `floor(hard × 0.90)`, computed in whole percent so the floor is exact.
- Below the `10`-minute minimum block the reserve relaxes so one genuine micro-task can still run, and **nothing new is taught**. That is a fact about time, not a verdict.
- The presets `30 / 60 / 90` are the defaults a settings screen offers (16D). The planner never substitutes one for a setting the learner did not make.

---

# 5. Needs

`LearningNeed` comes before any task (3B §2): the need survives a day on which no task served it; a task never does. Needs current state can open come from the axis each engine owns, and from nothing else:

| Axis | State | Need |
|---|---|---|
| weakness | `remediation_required` | `remediation_required` |
| mastery / retention | `confirmation_verification_due`, or confirmed with retention `verification_due` | `verification_due` |
| retention | confirmed with `review_due` | `retention_review_due` (due, never forgotten) |
| mastery | `developing_*` | `continue_learning` |
| mastery | not yet evidenced | `new_learning` |

- **An axis nobody has written opens nothing.** Before Stage 13, no review and no remediation need exists, rather than one being guessed.
- **A draft or retired Skill opens nothing**, and a deprecated one keeps its verification, review and remediation needs but takes no new start. The number of Skills off the route is recorded in the trace.
- Opportunities (diagnostic, reinforcement, integration), paused work and the parallel-track cadence come from their owners as needs; the planner accepts them and does not fabricate them.

---

# 6. Candidates and the gate order

`PDT-v0` §17, exactly: current state → needs → bounded candidates → trust → `PRG-v0` eligibility → `PBR-v0` priority → capacity fit.

- **Candidates are content's answer to a need** (`ContentPort.taskCandidates`). The planner never invents a task. Today the file adapter answers with nothing, and the need is recorded as having no valid candidate — with no reason code invented for it.
- **At most five candidates per need** are read, in a stable order with deprecated versions last. The cap is an engineering bound with no learning meaning.
- **Trust:** a `draft`, `invalidated` or `retired` candidate is never usable, and `assess`, `retain` and `diagnose` need a validated one, because their evidence can settle mastery.
- **The gate:** every candidate is asked about before any priority exists. `blocked` and `invalid_prerequisite_metadata` are never selectable, and a candidate the gate never answered for is treated as blocked — it fails closed.

---

# 7. Priority

`PBR-v0` §7's decision table gives a band; the ten-field rank vector orders a band and is compared field by field, never summed, with a stable tie-break key.

- **P0 needs a real blocker.** A critical verification or remediation becomes an integrity blocker only when that Skill actually holds dependent work back at the gate; a `critical` label alone is P1.
- **`review_due` is maintenance**, P3, or P2 when critical or strongly overdue — never negative evidence, never P0.
- **Paused work** is P2; ordinary continuation and new learning are P3; reinforcement is P4 unless the curriculum requires it.
- **Starvation** lifts planned progress to P2 and no further: it never passes repair work and never makes an optional P4 mandatory. The pressure is an input; the product supplies none because no threshold has been calibrated.

---

# 8. Selection

Semantic priority first, physical fit second (`PBR-v0` §9). For each need in order: **fits → safe split → smaller alternative → defer.**

- A **safe split** plans the part that fits, never smaller than the candidate's safe chunk; an **atomic evidence boundary is never cut**.
- A **smaller alternative** of the same need is tried before deferring.
- **One task serves a need**; the other alternatives are recorded as superseded.
- A need that did not fit is **deferred for time**, and the trace says so (`PDT-v0` §18): it is never labelled lower priority, and it is not debt.
- The planner does not stop at the first need that does not fit; it continues to the next.

Every plan records its invariant checks: within the planning budget, within the hard budget, no blocked or invalid candidate selected, one task per need, no new teaching below the minimum block.

---

# 9. The plan is truth

`plan_version`, its `planned_task` rows and `planner_decision_trace` are appended **in one transaction** and never edited. A later plan is a new version.

- The watermark is read **before** any state, so a trace can never claim to have seen more truth than it planned from.
- With no curriculum published there is nothing to plan against, and nothing is written.
- **The trace carries what `planned_task` cannot.** The whole `PDT-v0` trace — including each selected task's purpose, title, activity and minutes, keyed by position — is stored in `planner_decision_trace.trace` in the strict, versioned format `planner_trace/1`. It reads back exactly; an unknown version, section or value is refused, never guessed at.

---

# 10. Port refinements

Two refinements, no new interface; the port count stays four.

- `PersistencePort.publishedSkills()` — the newest version of every Skill, lifecycle included, in a stable order. Which lifecycles are on the route is the planner's decision.
- `ContentPort.taskCandidates(need)` — which task serves which need, and how long it takes, is authored content.

---

# 11. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

The proof is split as in 11B, 12A and 12B: the planner's choices are T1 checks, and what needs the real store — a plan appended as truth with its tasks and trace, a trace surviving storage exactly, a plan that cannot be edited, the newest Skill versions — is T2 against SQLite.

**Mutation 55/55, from a single clean run.** Every mutant had to be caught by a test, not by the compiler, and a run with no Gradle verdict is refused. A control mutant that changes only a comment survives, so the harness can report a survivor. **9 survived the first run** (Q06, Q09, Q15, Q22, Q25, Q49, Q50, Q51, Q52), and every one was a gap in the tests rather than in the rules: tests that never built the case the rule is about. Each gained a test, and the whole set was run again from scratch.

**Not run: T6.** The phone was not connected, and nothing in the app calls the planner yet, so no plan has been made outside a test.

---

# 12. Anti-patterns explicitly rejected

- an additive priority score, tasks per minute, or a knapsack,
- short-task bias, or duration considered before priority,
- `review_due` as forgetting, or a `critical` label as P0,
- a deferred task carried as next-day debt, or a day lengthened to fit new remediation,
- fixed category percentages, or English as a fixed share or a global blocker,
- a continuation bonus over repair work,
- priority bypassing the gate,
- a random tie-break,
- an invented starvation threshold, or an invented reason for an unserved need,
- a plan edited in place.

---

# 13. 12C acceptance contract

1. `PLNX-v0` is the accepted planner engine.
2. Capacity resolves in D-033's order; the hard budget is never exceeded and the planning budget keeps the reserve.
3. Needs come from the axis each engine owns; an unwritten axis and a Skill off the route open nothing.
4. The gate order is `PDT-v0` §17's; priority never rescues a blocked, invalid or untrusted candidate.
5. Bands and the rank vector equal `PBR-v0`; nothing is summed, no tie is random, and a critical label alone is not P0.
6. Selection is fit, safe split, smaller alternative, defer; an atomic boundary is never split and one task serves a need.
7. A need that did not fit is deferred for time, not called less important, and is neither debt nor failure.
8. No starvation threshold and no reason code is invented.
9. The plan is appended as truth in one transaction, with a trace that reads back exactly.
10. The schema is unchanged, no migration was added, and the port count stays four.
11. Nothing is claimed that was not run; T6 was not run.
12. Independent 12C QA passes and Stage 6–12B regressions pass.

---

# 14. Handoff after acceptance

If accepted, 12C becomes `PLNX-v0 / D-094`.

Next numbered step: **12D — Replan**. It must receive a fresh PRE-STEP and explicit user approval before execution. Open loops carried: replan triggers, re-entry, paused continuation, calling the planner and one-watermark `skill_state` assembly (12D), reason text and Today reading the plan (12E), virtual-user scenarios (12F), retention and weakness needs (13), authored tasks (15), the capacity setting (16D), starvation thresholds (18C), the candidate cap (18E) and the T6 device run.
