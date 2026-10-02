# Today Interior Specification — TDYX-v0

**Stage step:** 11A — Today ekranı  
**Status:** ACCEPTED — independent 11A QA PASS  
**Decision:** `D-087`  
**Model:** `TDYX-v0 — Today Interior`  
**Semantics:** `THUX-v0 / D-069`  
**IA:** `UXIA-v0 / D-068`  
**Design system:** `VDSX-v0 / D-073`, `WFPX-v0 / D-074`, `DSIX-v0 / D-084`  
**Boundaries:** `MSBX-v0 / D-078`  
**App health:** `APHX-v0 / D-086`

## 1. Purpose

11A builds the first destination interior: `THUX-v0`'s Today as running code.

It answers one primary question:

> **Bugün şimdi ne yapmalıyım — ve cevap "hiçbir şey" olduğunda ne doğrudur?**

Primary invariant:

> **Today is a projection of canonical planner and state truth.** It computes nothing it was not given, so it cannot become a second planner, mastery engine, prerequisite engine, gradebook or English quota system. At this step that is not a promise about discipline: the planner does not exist yet, and Today renders the truthful empty and loading states rather than inventing a plan to fill the screen.

---

# 2. What reading the code against the contracts found

Two defects, neither of them in the handoff:

| Found | Contract it broke |
|---|---|
| `FileContentSource.resource` was `TODO()` — the first caller asking for unpublished curriculum would crash | `ContentPort` already has a word for a missing resource: `null`. Same defect class as the `AiEvaluator` `TODO()` 10E found |
| `Surface.all` was an initialised `val`, so a module that referenced a surface from its own initialiser got a registry containing **nulls** | It surfaced as a `NullPointerException` in 10B's own passing test the moment 11A referenced `Surface` from `TodayView`'s companion. Same Kotlin initialisation-order trap that cost 10D a whole suite |

The registry is now computed on access, and a structural test fails if it becomes an eager field again — because the bug only reproduces in the load order that happened to expose it, which is exactly the kind of fault that comes back.

---

# 3. Scope boundary

## 3.1 11A decides

- the Today projection: the twelve semantic states, the six-step primary-action precedence, the queue, the task row, the reason summary, attention and supporting navigation,
- which state a fresh install truthfully shows, and how `empty_valid` is distinguished from `loading_initial_plan`,
- the read path that supplies Today's facts, and that it runs off the main thread,
- the Today screen: region order, text, tone, announcement and touch targets.

## 3.2 11A does not decide

- the planner, the plan rows it writes, their purposes, traces and durations → 12,
- starting a task, the Task Runner entry → 11B; resume checkpoint content → 11C,
- curriculum ingestion → 11D; authored content → 15,
- capacity settings and Profile controls → 16D,
- final microcopy and the offline state → 14,
- accessibility polish on a device → 17,
- any accepted semantic, state, tone, geometry, boundary, port or schema.

No task-row count, pixel geometry or timing is claimed.

---

# 4. The projection

`TodayPresentation.of(TodayInput): TodayView` is a pure function in `core-presentation`. Its vocabularies are copied from the contracts that own them, in their order:

- **twelve semantic states** from `THUX-v0`; there is no thirteenth,
- **six-step precedence** from `THUX-v0` §5: data recovery, a revalidated resumable session, the selected next planned task, loading or replanning, a legitimate empty or capacity-limited day, a recoverable error,
- **seven canonical purposes** from `TASK_TAXONOMY_SPEC` §3.1 — English is a track, never a purpose,
- **eight reason families** and **seven attention families** from `THUX-v0`,
- **region order** from `WFPX-v0`,
- **tones** from `VDSX-v0`, copied rather than chosen: only `error_recoverable` and `data_recovery_required` wear the fault tone; an empty day, a capacity limit and waiting are all neutral, because none of them is a failure.

## 4.1 What cannot be written

- **A reason has no free-text field.** `ReasonSummary` has a private constructor and is only reachable through `fromTraceFacts`, so a reason the planner never recorded cannot be constructed and a free-form AI justification has nowhere to live.
- **A task row has no mastery, score, percentage, streak or rank field.**
- **Capacity carries only the five allowed summary fields**, plus the planner's own `tooSmallForAnyCandidate` finding.
- **The presentation never derives a capacity verdict.** Comparing two estimates here would be Today quietly deciding what the planner may select, so the verdict is an input.

## 4.2 What is filtered rather than shown

- **A plan from another study day never becomes a row.** `SRR-v0` forbids replaying yesterday as backlog; the state says a fresh plan is owed.
- **Blocked work is filtered out**, never offered as startable and never listed — `THUX-v0` forbids both a startable blocked task and a permanently blocked task list. A plan whose every task is blocked is `empty_no_eligible_task`.
- **Replanning shows no rows at all**, so the plan being replaced cannot leak.
- **A store that needs recovery renders no plan rows.**
- **A session that has not been revalidated is not offered for resume.**
- **Attention that the primary task already represents is dropped** — a remediation task *is* the remediation call to action; retention and assessment likewise. Attention the task does not cover survives, because that is what the region is for.

## 4.3 Degraded context

`ai_unavailable_core_available` is context, never a blocking state: it rides alongside a valid plan exactly as `APHX-v0` produces it. `offline_local_available` stays declared and unproduced until something uses the network (14).

---

# 5. `empty_valid`, handed over by 10E

10E declared `empty_valid` and left it to this step. Today produces it now, and the distinction is the honest part:

- **nothing published** → `empty_no_open_need`, with `empty_valid` as context and text that says content has not been loaded yet. It claims no mastery and no readiness,
- **a published curriculum with no plan yet** → `loading_initial_plan`,
- **a capacity of zero, or a capacity nothing fits** → the capacity states, which explain the absent plan by themselves. Calling that "loading" would promise work that is not coming.

---

# 6. The read path

`TodayFactsQuery` in `core-application` reads the study day from `ClockPort` and asks the store whether any curriculum is published. It is read-only, and it runs on the store thread — the disk work 10E moved off the main thread in the first place. The activity re-reads on resume, because a process kept open past midnight would otherwise keep yesterday's study day, and a stale study day is how a stale plan becomes "today's".

**It does not read a plan, and that is a position rather than an omission.** A Today row needs the task's purpose, its trace-backed reason and its estimated duration; `DDM-v0`'s `planned_task` carries none of those columns, and 10D recorded them as 12's to complete alongside the planner that writes them. Reading the partial rows and filling the rest with plausible defaults is precisely how a screen starts claiming what no engine decided.

Capacity is not read either: it is a Profile setting owned by 16D, and a default would be a number the learner never chose.

## 6.1 Port refinement

`PersistencePort` gained `curriculumPublished()`. It is a refinement of an existing port expressed in core types, not a fifth port — the precedent is 10D refining `ProjectionRecord`. The port count stays four and a check enforces it.

## 6.2 Module placement

`core-application` may not depend on `core-presentation`, so the facts both sides need — purposes, reason families, capacity, plan and task rows — live in `core-model`, and the presentation input is assembled by a `core-presentation` function rather than in the composition root.

---

# 7. The screen

`app-ui` renders and decides nothing. Regions appear in `WFPX-v0`'s order with the primary action first; the state is always text, carried by the design system's chip and announced through a polite live region; actions keep the 48dp floor; there is no locale-naive case transform. Capacity is written as minutes described as an estimate — no percentage, no ring, no countdown.

Turkish text is working microcopy owned by 14. What is canonical is that every state is said in words and that no number on this screen is a progress claim.

---

# 8. Mutation testing

Sixteen deliberate mutations, **all sixteen caught** — three only after the tests were strengthened, and the record says so:

- **M08 (a derived capacity verdict) survived at first.** The test case reached the loading branch before the capacity branch, so the derived claim was never evaluated. The case now has a plan present, which is the only way the state selection reaches that branch.
- **M09 (attention duplicating the primary action) survived at first**, and exposed something worse than a weak test: the rule had been written against the primary *action kind*, and the only test used the recovery branch whose attention list is fixed anyway. `THUX-v0`'s actual rule is about the primary *task's* subject, so the rule was rewritten to compare the task's purpose with the attention family, with tests for a dropped and a kept item.
- **M16 (the eager surface registry) survived at first**, because the `NullPointerException` only reproduces in the load order that first exposed it. A structural test now asserts the registry has no backing field.
- **M07's first version added an unused parameter and changed no behaviour**, which is not a detection; it was rewritten to stop health from blocking at all, and rerun.

---

# 9. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS — 47 tests |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

**Not run: T6.** The Poco M6 Pro was not connected. Nothing is claimed about how Today renders on the device, how TalkBack traverses it, how it behaves at 200% text, or that the resume refresh survives a real midnight rollover. No Today state other than the empty and loading ones has been seen against a real store, because no planner writes plans yet.

---

# 10. Anti-patterns explicitly rejected

- rendering a plan from another study day,
- offering a blocked task as startable, or listing blocked work as a permanent backlog,
- showing the previous plan while replanning,
- rendering plan rows while the store needs recovery,
- resuming a session that was not revalidated,
- showing a reason the planner never recorded, or a free-text reason field,
- deriving a capacity verdict in the presentation,
- letting AI absence supersede a valid local plan,
- repeating the primary task as a competing attention entry,
- implying mastery or readiness from an empty day,
- a mastery, score, percentage, streak or rank field on a row,
- inventing a capacity the learner never chose, or a plan the planner never produced.

---

# 11. 11A acceptance contract

1. `TDYX-v0` is the accepted Today interior.
2. The twelve states, six-step precedence, purposes, reason and attention families and region order equal their owning contracts, in their order.
3. Tones are `VDSX-v0`'s, and only the two system conditions wear the fault tone.
4. A stale plan, a blocked task, a replaced plan and an unrevalidated session are unrenderable, not merely discouraged.
5. A reason cannot be constructed without a planner trace fact, and carries no free text.
6. No row or capacity field can claim mastery, a score or progress.
7. The capacity verdict is reported by the planner, never derived in the presentation.
8. AI absence is context and never supersedes a valid plan.
9. `empty_valid` is produced and distinguished from `loading_initial_plan`.
10. The read path is read-only, runs off the main thread, refreshes on resume, and invents neither a plan nor a capacity.
11. `MSBX-v0`'s four ports are unchanged; `curriculumPublished()` is a recorded refinement.
12. `app-ui` renders only; the projection is a JVM-testable pure function.
13. The content port returns a missing resource instead of throwing.
14. 16/16 mutations are caught, and the three that survived first are recorded with their fixes.
15. Nothing is claimed that was not run; T6 was not run.
16. Independent 11A QA must pass, and Stage 6–10 regressions must pass.

---

# 12. Handoff after acceptance

If accepted, 11A becomes `TDYX-v0 / D-087`.

Next numbered step: **11B — Task runner**. It owns the start action Today now offers, the focused-flow entry from `TRUX-v0`, and the first thing that makes a task row do something. It must receive a fresh PRE-STEP and explicit user approval before execution.

---

**15B note (2026-10-02, `D-113`):** the content adapter now reads several packages; a bad sequence is refused by a throw inside its lazy read, which `runCatching` turns into "nothing is served". No lookup can throw, so `E11A-13_content_port_returns_null` was narrowed to exclude that sequence check. Details: `docs/PYTHON_FOUNDATIONS_CONTENT_SPEC.md`.
