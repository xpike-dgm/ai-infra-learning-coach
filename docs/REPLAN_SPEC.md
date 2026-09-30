# Replan Implementation Specification — RPLX-v0

**Stage step:** 12D — Replan  
**Status:** ACCEPTED — independent 12D QA PASS  
**Decision:** `D-095`  
**Model:** `RPLX-v0 — Replan`  
**Planner:** `PLNX-v0 / D-094`  
**Explainability:** `PDT-v0` (`docs/PLANNER_EXPLAINABILITY_SPEC.md`)  
**Capacity:** D-033 (`docs/ADAPTIVE_PLANNER_SPEC.md`)  
**Re-entry:** `SRR-v0` (`docs/MISSED_DAY_RECOVERY_SPEC.md`)  
**Working flow:** `TRUX-v0 / D-070`  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`

## 1. Purpose

12D replaces a plan the way the contracts say a plan is replaced.

It answers one primary question:

> **Bir plan değiştiğinde ne korunur, ne yeniden çözülür ve neden?**

Primary invariant:

> **A plan is replaced by a new version with a reason, never edited.** What the learner started is kept, only the unstarted remainder is solved again, the day never grows by itself, and a return after absence replays nothing — absence is not debt, failure or decay.

---

# 2. What was found before writing any code

- **The store cannot say which planned task was started or finished.** `DDM-v0` gives `attempt` no link to `planned_task`, yet `TRUX-v0` §10.1 requires in-flight work to survive a replan.
- **12C's `build()` wrote a second "initial" plan on the same day** when called again with nothing changed — a new version with no reason, against `PDT-v0` §15.
- **`planner_trace/1` had nowhere to put a replan**: no kept flag, no replan record, no re-entry context.
- **Some handoff items cannot be built yet** (§10), and **`USER_FOCUS_CHANGED` has nothing to act on**: the planner takes no focus preference.

---

# 3. Scope boundary

## 3.1 12D decides

- whether a new plan is initial, a replan or a re-entry,
- which events may replace a plan, and which `PDT-v0` code each carries,
- what is kept, what is solved again, and with how many minutes,
- how a stored pause affects the need it names,
- what a replan and a re-entry record in the trace.

## 3.2 12D does not decide

- user-facing reason text, and Today reading the plan → 12E,
- the 3H scenarios run against this code → 12F,
- `skill_state` assembly, retention and weakness needs → 13,
- the recompute chain after an attempt, and a stored attempt → planned-task link → 15,
- calling the planner from the app, the capacity setting and the focus preference → 16D,
- reverse invalidation of dependents → 18E.

---

# 4. Why a new version exists

`PDT-v0` §4's three generation kinds, told apart by what the store holds:

| Newest plan | Generation |
|---|---|
| none | `initial` |
| from another study day | `reentry` — whatever event arrives with it |
| from today, and an event arrived | `replan` |
| from today, and nothing happened | **no new version**: the plan already there is returned, nothing is written |

A new version always has a reason. That corrects 12C's behaviour, not its contract.

---

# 5. Events

The triggers are D-033 §16's, `PBR-v0` §17's and `PRG-v0` §19's. Each carries the `PDT-v0` §8.10 code that describes it; `task_completed` has no such code and carries none — no code is invented for it. `user_focus_changed` is not accepted, because the planner has no focus preference to change (16D).

---

# 6. The remaining budget

D-033 §8: a replan changes only what is left.

- A **new capacity for today** replaces the day's budget; minutes already spent on kept work come off the top.
- A **declared remaining time** (`session_remaining_time_changed`, `user_requested_extra_time`) *is* the remainder.
- **Any other event** keeps the day's budget, less kept minutes. Over a chain of replans the day's budget is carried, so it is never quietly reset.
- The remainder is **never negative**, follows the **same rule as a whole day** (reserve, minimum block), and replanning **never grows the day by itself** — only the learner's explicit extra time does.
- **Less time never undoes kept work**: kept tasks stay, and the trace's `day_within_hard_budget` check says honestly when the day now overruns.

---

# 7. Kept work

`TRUX-v0` §10.1: an in-flight run is never destroyed by a replan.

- Because the store cannot say what was started, **the caller that owns in-flight state reports it** — the positions of the previous plan it kept.
- **Every reported position is checked** against the plan being replaced; an unknown one refuses the replan, and a refused replan writes nothing.
- Kept tasks come **first, unchanged and marked kept**; the rest of the previous plan is recorded as invalidated; **a need a kept task serves is not served twice**.
- A previous plan whose trace cannot be read is **not replaced by guesswork**: the replan is refused.

---

# 8. Re-entry

`SRR-v0`: absence is not failure, mastery decay or task debt.

- **Yesterday's plan is not replayed** and nothing from it is kept, even if an event arrives on the new day.
- The day's **normal budget** applies — no catch-up day, no backlog.
- Absence never feeds **starvation**.
- The trace records `SRR-v0` §17's context — last planned day, days away (for the record only), stale plan size, paused needs, high-stakes pauses not resumed, open needs by trigger, due Skills by retention state, P0/P1 count, capacity — and `PDT-v0` §8.8's codes. None of it is a score, a penalty or a debt.
- A re-entry still works when the old trace cannot be read: the stale plan's size is counted from the store itself.

---

# 9. Paused work

`SRR-v0` §5: a safe checkpoint is a candidate, never automatic.

- The **latest** safe pause for an open continuation need makes it paused work — `PBR-v0` P2 — and it is still ranked, gated and fitted like any other.
- A **high-stakes pause is not continued** as independent work; its need stays as it is and a fresh candidate serves it.
- A pause whose need has **closed changes nothing**, and a pause whose text does not decode is not a pause anyone can resume.

---

# 10. Re-pointed, not faked

| Handed to 12D | Now owned by | Why |
|---|---|---|
| `skill_state` assembly under one watermark | 13 | only 12A writes that row; the problem appears when 13 becomes the second writer |
| recompute chain after an attempt | 15 | rebuilding mastery needs Objective gate profiles, which are authored content |
| calling the planner from the app | 16D | the planner needs a stored capacity; a default would be a number the learner never chose |
| reverse invalidation of dependents | 18E | a replan already regenerates from bounded current state; it is an optimisation |
| attempt → planned-task link | 15 | `DDM-v0` names no such column; until tasks can start, the caller reports kept work |

---

# 11. The trace

`planner_trace/2` carries each task's kept flag, the replan record (trigger, previous version and day, kept and invalidated positions, kept minutes, remainder) and the re-entry context. `planner_trace/1` still decodes, strictly: a `/1` trace cannot claim a kept task, a replan or a re-entry.

Two port refinements, no new interface: `latestPlan()` returns the newest plan's row, its planned-task count and its raw trace text — interpreting it is core's job — and `resumeCheckpointRows()` returns stored pauses oldest first.

---

# 12. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

**Mutation 33/33, from a single clean run.** Every mutant had to be caught by a test, not by the compiler, and a run with no Gradle verdict is refused. A comment-only control mutant survives. **4 survived the first run** (R10, R23, R24, R28) — each a test that never built the case its rule is about — and R19 did not compile and was rewritten as the real regression. Each gained a test, and the whole set was run again from scratch.

**Not run: T6.** Nothing in the app calls the planner and no task can be started, so no replan has happened outside a test.

---

# 13. Anti-patterns explicitly rejected

- a plan edited in place, or a new version with no reason,
- an old plan replayed after absence, or absence treated as debt, failure, decay or starvation,
- in-flight work destroyed by a replan, or kept work undone by less time,
- a day grown by replanning on its own,
- paused work selected automatically, or a high-stakes pause continued as independent evidence,
- an invented reason code for an event, or unreported kept work guessed.

---

# 14. 12D acceptance contract

1. `RPLX-v0` is the accepted replan.
2. Initial, replan and re-entry are told apart by whether a plan exists and which study day it belongs to.
3. A new plan version always has a reason; the same day with no event writes nothing.
4. Only the unstarted remainder is solved again; kept work is first, unchanged and never served twice.
5. Kept work is reported by the caller and checked against the previous plan, or the replan is refused.
6. The remainder follows D-033; it is never negative and never grows the day by itself.
7. Re-entry replays nothing, keeps nothing and records what the return looked like, with no score or debt.
8. A safe pause makes its need P2 paused work without selecting it; a high-stakes pause is not continued.
9. Every trigger code is `PDT-v0`'s, and no code is invented for an event that has none.
10. `planner_trace/2` records the replan; `/1` still decodes strictly.
11. The schema is unchanged, no migration was added, and the port count stays four.
12. Nothing is claimed that was not run; T6 was not run.
13. Independent 12D QA passes and Stage 6–12C regressions pass.

---

# 15. Handoff after acceptance

If accepted, 12D becomes `RPLX-v0 / D-095`.

Next numbered step: **12E — Explanation / reason codes**. It must receive a fresh PRE-STEP and explicit user approval before execution. Open loops carried: reason text and Today reading the plan (12E), the 3H scenarios (12F), `skill_state` assembly, retention and weakness (13), the recompute chain and an attempt → planned-task link (15), calling the planner and the focus preference (16D), starvation thresholds (18C), reverse invalidation and candidate caps (18E), and the T6 device run.
