# End-of-Day Specification — EODX-v0

**Stage step:** 11E — Gün sonu  
**Status:** ACCEPTED — independent 11E QA PASS  
**Decision:** `D-091`  
**Model:** `EODX-v0 — End of Day`  
**Presentation semantics:** `SPWX-v0 / D-072`  
**Home:** `THUX-v0 / D-069`, `TDYX-v0 / D-087`  
**Absence:** `SRR-v0`  
**Health:** `APHX-v0 / D-086`  
**Time:** `DDM-v0 / D-077`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

11E decides what the end of a day may honestly say, and what it may never claim.

It answers one primary question:

> **Bugün gerçekte ne kaydedildi — ve bundan asla ne iddia edilemez?**

Primary invariant:

> **The end of a day is a boundary in time, not a verdict.** A day closes because the learner-local study day changed, never because the learner finished or failed to finish something, and nothing about their standing changes when it does.

---

# 2. What was found before writing any end-of-day code

- **No accepted document defines an end-of-day surface.** Its rules were therefore derived from the contracts that already own the pieces rather than invented here: presentation states and counting rules from `SPWX-v0`, absence semantics from `SRR-v0`, the day itself from `DDM-v0`'s three-value time, precedence from `APHX-v0`, tones from `VDSX-v0`, and the region it renders in from `THUX-v0`/`WFPX-v0`.
- **Nothing could count what a day recorded.** The port could read one truth row by id; it could not answer "how many attempts were recorded on this study day". `countTruth(kind, studyDay)` is a recorded refinement, and it writes nothing.

---

# 3. Scope boundary

## 3.1 11E decides

- what a day summary may hold, and what it structurally cannot,
- the day boundary: which day a row belongs to, and that nothing crosses it,
- how days appear as history, and that gaps are not drawn,
- where the summary renders.

## 3.2 11E does not decide

- the engines that report a real change, and open-need counts → 12,
- assessment, retention and remediation history entries → 13,
- final microcopy and the offline/AI-unavailable day states → 14,
- longitudinal history across days and the assessment report → 16B,
- any end-of-day notification → 16E.

No streak, daily goal, completion percentage or "minutes studied" is introduced — there is nowhere to put one.

---

# 4. What a day is

A day is the **learner-local study day** each row recorded (`DDM-v0`'s three-value time). A row belongs to the day it says it does, and never to a day recomputed from its instant: recomputing it is exactly how a DST change or a flight silently moves work from one day into another. The count is taken against the row's own `*_study_day` column, and a truth table whose rows carry no day of their own is refused rather than counted through a parent.

A new day starts **empty**. Nothing crosses the boundary: not an inventory, not an unfinished plan, not an obligation. `SRR-v0` is explicit that a day away is not a debt and an unstarted task is not homework, so there is no function here that could carry one, and a day cannot roll backwards.

**The learner does not close a day.** There is no "finish my day" action to succeed or fail at; the day ends when the clock says so.

---

# 5. What the summary may say

**An inventory of what was really written**, labelled as inventory (`SPWX-v0` §counting_rules): attempts recorded, items seen, checkpoints saved, evidence interpreted. Counting is allowed; dividing is not. There is no total, ratio, percentage or goal, and the type has no field for one.

> Counting what was written is not the same as claiming what it proved.

**A change, only when a canonical engine reported one**, in `SPWX-v0`'s `learning_history` families (`learning`, `assessment`, `review`, `remediation`). Activity is not a change. With no evidence pipeline yet (12), the summary says plainly that nothing changed rather than manufacturing a claim from the fact that the learner did something.

**What could not be read** is named. An unreadable count is not a zero: "we did not read this" and "this was nothing" are different claims about the day, and only one of them is honest when a read fails.

---

# 6. What it may never say

- that a day succeeded or failed,
- a streak, a consecutive-day count or an attendance grid,
- a completion percentage, ratio or daily goal,
- minutes studied as progress,
- a debt, backlog or catch-up obligation carried into tomorrow,
- that activity was learning,
- a state change no engine reported.

An empty day is `empty_no_evidence_yet`, neutral in tone, and is not a failure. A gap between days produces no history entry at all — an empty square in a grid is precisely how absence becomes a scoreboard.

---

# 7. States and precedence

The day summary is a projection of the same canonical truth Progress projects, scoped to one study day, so it uses `SPWX-v0`'s ten presentation states in its order, with `VDSX-v0`'s tones. Only the two system conditions wear the fault tone.

Precedence follows `THUX-v0` and `APHX-v0`: data recovery, then a recoverable fault, then loading, then a partial read, then the honest empty day, then the day that recorded something. While loading, **no partial count is presented as the day's inventory**.

Four states are named but not produced yet, with owners rather than being quietly dropped: `recomputing_projection` (12), `empty_no_attention_needed` (16), and the two degraded states (14).

---

# 8. Where it renders

Inside Today's `day_plan_context` region. `NSHX-v0`'s surface set is closed, so no destination is invented for the end of a day, and longitudinal history across days stays Progress's (16B). Everything is text: no chart, ring, bar or calendar grid.

The day is read on the store thread, against the same clock as Today, and refreshed on resume — a process kept open past midnight reports the new day rather than yesterday's.

---

# 9. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

**Not run: T6.** The phone was not connected. On a device nothing has been verified: not the summary's rendering, not its announcement, and not the midnight rollover, which is proven in a JVM test with an injected clock instead. In the real app the day is genuinely readable, and it reads empty, because nothing can be started yet.

---

# 10. Anti-patterns explicitly rejected

- declaring a day successful or failed,
- a streak, consecutive-day count, contribution graph or attendance heatmap,
- a completion percentage, ratio or daily goal,
- minutes studied presented as progress,
- carrying unfinished work into tomorrow as debt,
- presenting activity as learning, or claiming a change no engine reported,
- presenting an unreadable count as zero,
- drawing a gap between days as a missed obligation,
- recomputing a row's day from its instant,
- inventing a second surface for the day.

---

# 11. 11E acceptance contract

1. `EODX-v0` is the accepted end-of-day projection.
2. Presentation states and counting rules equal `SPWX-v0`, in its order.
3. The day is the learner-local study day a row recorded, never one recomputed from its instant.
4. A new day starts empty and nothing crosses the boundary, least of all a debt.
5. Counts are labelled inventory with no ratio, total or percentage, and are not progress.
6. A change is claimed only when a canonical engine reported one.
7. An unreadable count is named, never presented as zero.
8. An empty day is neutral and never a failure.
9. History shows the days that recorded something and never fills the gaps.
10. The summary renders inside Today's day context; no surface is invented and no grid is drawn.
11. `countTruth` is a recorded port refinement that writes nothing; the port count stays four.
12. Nothing is claimed that was not run; T6 was not run.
13. Independent 11E QA passes and Stage 6–11D regressions pass.

---

# 12. Handoff after acceptance

If accepted, 11E becomes `EODX-v0 / D-091`, and **AŞAMA 11 is complete**: Today (`TDYX-v0`), the Task Runner (`RNRX-v0`), session state (`SESX-v0`), the daily micro assessment (`DMAX-v0`) and the end of the day (`EODX-v0`).

Next numbered step: **12A — Mastery Engine v1**. It must receive a fresh PRE-STEP and explicit user approval before execution. Open loops carried into Stage 12: the engines that report a change, planner selection and plan changes, open-need counts, the evidence pipeline that decides what an attempt proved, and the T6 device run.
