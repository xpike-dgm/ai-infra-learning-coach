# Virtual User Tests Specification — VUSX-v0

**Stage step:** 12F — Sanal kullanıcı testleri  
**Status:** ACCEPTED — independent 12F QA PASS  
**Decision:** `D-097`  
**Model:** `VUSX-v0 — Virtual User Scenarios`  
**Scenario source:** 3H (`docs/PLANNER_SIMULATION_SUITE.md`)  
**Engines under test:** `PRQX-v0 / D-093`, `PLNX-v0 / D-094`, `RPLX-v0 / D-095`, `RSNX-v0 / D-096`  
**Test strategy:** `TVSX-v0 / D-081`

## 1. Purpose

12F runs the 3H virtual users against the code that now exists.

It answers one primary question:

> **3H'nin sanal kullanıcıları gerçek kapıdan, planner'dan, replan'dan, depodan ve açıklamadan geçince sözleşmelerin söylediği gün çıkıyor mu?**

Primary invariant:

> **A virtual user is state, never an answer.** Each one is Skill state, a prerequisite graph, authored tasks, pauses and earlier plans; needs come from state the way the planner opens them, eligibility from the real gate, bands and selection from the real planner, and the explanation from the trace it wrote. A scenario that cannot be run against real code is named as not runnable, with its owner — it is never simulated by hand again.

---

# 2. What 3H was, and what this is

3H passed 16 scenarios and 20 `PDT-v0` invariants **at policy level, by reasoning**, before any planner code existed, and said so: its PASS "does not replace 12F". Here the same users run through `PrerequisiteEngine`, `PlannerEngine`, `ReplanEngine`, `BuildDailyPlan`, `PlanReading`, `TodayFactsQuery` and `PlannerExplanationPresentation`.

The virtual users are defined once, as Gradle test fixtures of `core-engines`, and used by three suites:

| Suite | Module | What it runs |
|---|---|---|
| `VirtualUserScenariosTest` | core-engines | the real gate and planner on each user's state |
| `VirtualUserJourneysTest` | core-application | re-entry, pauses, replans and Today through `BuildDailyPlan` and a store |
| `VirtualUserExplanationsTest` | core-presentation | the explanation of those same plans, from their own traces |

Test fixtures are a declared dependency on an edge `MSBX-v0` already allows (`core-application` and `core-presentation` → `core-engines`). **`verifyModuleBoundaries` failed the first build that used them** with `:core-engines -> :core-engines`: `java-test-fixtures` makes a module's own tests depend on its own fixtures, and the check read that as an edge. The check now drops only a module's reference to itself; to show it was not weakened, a real cycle (`core-model → core-engines → core-model`) was added, failed the build, and was reverted.

---

# 3. What running them found

- **The explanation listed a return day's due inventory row by row.** A learner back after thirty days with seventy-eight Skills due for review read seventy-eight "not today" entries on `planner_explanation` — the backlog `SRR-v0` §9.1 forbids (`due state inventory != DailyPlan`). Needs that did not come for the same recorded reason are now **one entry** that keeps every need and every Skill; a waiting need naming its own blockers stays apart. The screen names three Skills and counts the rest as labelled inventory.
- **3H's S07 example day is illustrative, not the rule.** With every due Skill carrying an authored review, `PBR-v0`'s temporal urgency ranks those reviews ahead of new learning inside P3, so the rest of the return day goes to them and new learning waits for time. That is what `SRR-v0` §15 allows ("if capacity remains after PBR ordering"); the guard against review-only return days is starvation and track balance, whose thresholds are 18C's. Both days are tested; no rule was changed.
- **S06 cannot be run.** `VDW-v0`'s diagnostic waiver has no implementation, and no step in the plan owns one. Invariant 12 had no owner; it is re-pointed to 13, where assessment evidence is implemented, and named as an open loop rather than simulated.
- **The boundary check read a module's own test fixtures as a self-cycle** (§2); only that self-reference is now ignored.
- **Two scenario tests were weaker than they looked** (mutation, §7): a critical verification that holds nothing back was never checked to stay P1, and S05's 20-minute lesson never fit 8 minutes, so "nothing new is taught below the minimum block" was never exercised. Both gained cases.

No planner, gate or replan rule changed.

---

# 4. Scenario matrix

| 3H | Virtual user | Run | Where |
|---|---|---|---|
| S01 | U-NOVICE, 60 minutes | yes | engine |
| S02 | U-BLOCKED, critical verification | yes | engine, explanation |
| S03 | U-REVIEW, review due prerequisite | yes | engine, explanation |
| S04 | U-TIGHT, P1 does not fit | yes | engine, explanation |
| S05 | 8-minute day | yes | engine |
| S06 | U-FASTPATH, partial diagnostic | **no** — `VDW-v0` unimplemented; owner 13 | — |
| S07 | U-RETURNING, 30 days, 25 stale tasks, 80 due | yes | engine, journey, explanation |
| S08 | re-entry with a safe pause | yes | journey |
| S09 | remaining time reduced mid-session | yes | journey |
| S10 | new remediation mid-session | yes | journey |
| S11 | untrusted high-stakes candidate | yes | engine |
| S12 | duplicate alternatives | yes | engine |
| S13 | `critical` label alone | yes | engine |
| S14 | same-input determinism | yes | engine, journey (byte-identical trace) |
| S15 | explanation within the trace | yes | explanation |
| S16 | interrupted high-stakes attempt | yes | journey |

All twenty `PDT-v0` §23 invariants are covered by at least one test except invariant 12 (S06). Invariant 17 is covered structurally — at most five candidates per need, a trace bounded by today's state, and planning reads no evidence and reads no more with thirty stale plans than with one; runtime latency and memory are 18E's, on the device.

---

# 5. What a virtual user is not allowed to do

- hand the planner a need that current state would not open — only the parallel-track cadence and integration or reinforcement opportunities, which `PLNX-v0` §5 says come from their owners,
- hand the planner a gate decision — every decision is `PrerequisiteEngine.decide` over the user's graph and readiness,
- hand the explanation a trace — every explained trace is one the real planner (and, for re-entry, `ReplanEngine`) wrote,
- read evidence history while planning a day — the journey store fails the test if it is asked.

---

# 6. Scope boundary

## 6.1 12F decides

- the virtual users and where they live,
- which 3H scenarios run against which code, and which cannot yet,
- the grouping of not-today needs on the explanation surface.

## 6.2 12F does not decide

- the diagnostic waiver → 13,
- retention, weakness and their needs, whose states the users supply directly → 13,
- starvation and track-balance thresholds → 18C,
- runtime performance budgets → 18E,
- any planner, gate, replan or capacity rule.

---

# 7. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

**Mutation 27/27, and only the virtual-user suites ran** — every mutant broke a 3H invariant in the real gate, planner, replan, reading or explanation code, and a catch had to come from `VirtualUserScenariosTest`, `VirtualUserJourneysTest` or `VirtualUserExplanationsTest`, never from another test. Every mutant had to compile, a run with no Gradle verdict is refused, and a comment-only control survives. **F01 and F06 survived the first run** — a critical verification holding nothing back was never checked to stay P1, and S05's lesson never fit, so the no-teaching rule was never exercised. Both gained cases, and the whole set was run again from one unchanged tree (two chunks under the tool's time limit).

**Not run: T6.** Nothing in the app calls the planner yet (16D); the virtual users have lived only in tests.

---

# 8. Anti-patterns explicitly rejected

- a scenario that passes because the fixture wrote the answer,
- a scenario simulated by hand because the code for it does not exist,
- a due inventory shown as a list of overdue work, on Today or in the explanation,
- a runtime claim without a device run,
- a rule changed to make a scenario's illustrative example come true.

---

# 9. 12F acceptance contract

1. `VUSX-v0` is the accepted virtual-user suite.
2. The 3H virtual users are defined once and used by the engine, journey and explanation suites.
3. Every 3H scenario runs against real code, or is named not runnable with an owner; only S06 is not runnable.
4. Every `PDT-v0` §23 invariant is covered by a test, except invariant 12, re-pointed with S06.
5. No virtual user supplies a gate decision, an explained trace, or a need current state would not open.
6. Needs that did not come for the same recorded reason are one explanation entry, with nothing dropped.
7. Mutation runs only the virtual-user suites, so every catch is theirs.
8. The schema is unchanged, no migration was added, and the port count stays four.
9. Nothing is claimed that was not run; T6 was not run.
10. Independent 12F QA passes and Stage 6–12E regressions pass.

---

# 10. Handoff after acceptance

If accepted, 12F becomes `VUSX-v0 / D-097` and **AŞAMA 12 closes**.

Next numbered step: **13A — Haftalık sınav**. It must receive a fresh PRE-STEP and explicit user approval before execution. Open loops carried: the diagnostic waiver and S06 (13); retention and weakness needs (13); starvation thresholds (18C); runtime budgets (18E); calling the planner from the app (16D); and the T6 device run.
