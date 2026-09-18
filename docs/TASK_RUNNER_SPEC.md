# Task Runner Specification — RNRX-v0

**Stage step:** 11B — Task runner  
**Status:** ACCEPTED — independent 11B QA PASS  
**Decision:** `D-088`  
**Model:** `RNRX-v0 — Task Runner`  
**Semantics:** `TRUX-v0 / D-070`  
**Entry point:** `TDYX-v0 / D-087`  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

11B builds the Task Runner: `TRUX-v0`'s focused flow as running code, and the first path in the product that writes a learner's work.

It answers one primary question:

> **Bu işi şimdi nasıl yaparım, durursam ne olur — ve henüz hiçbir şey çalıştırılamıyorken bunu nasıl dürüstçe söylerim?**

Primary invariant:

> **The Task Runner is an execution surface.** It is not the planner, the mastery authority, the prerequisite authority or the evidence evaluator, so it can neither rank a task, change a Skill state, advance a cached list nor turn an attempt into a pass or a fail.

---

# 2. What was found before writing any runner code

Reading `main` found a defect this project's own tooling had introduced: 11A's sync script built Turkish text with `"İ".lower()`, which in Python produces a dotted `i` followed by **U+0307 COMBINING DOT ABOVE**. Five words — four "açık", one "taşımaz" — reached `AGENTS.md`, `PROJECT_CONTEXT.md`, `HANDOFF_STATE.md` and `START_HERE.md`. They were replaced with the dotless `ı` the words need, and the 11B validator now fails if any text file in the repository contains U+0307. The guard caught itself on its first run — the tool that wrote it had turned the escape sequence into the character — so the character is now built from its code point.

---

# 3. Scope boundary

## 3.1 11B decides

- the runner's seventeen states, six phases, entry and resume revalidation, pause classes and exit,
- the assistance choreography: what may be granted, when, and what must be said first,
- what the runner records for an attempt, and that it is one transaction,
- the runner screen and its wiring from Today's start action.

## 3.2 11B does not decide

- evidence: outcomes, component results and target objectives → the evidence pipeline, 12,
- the facts that confirm a task may start — prerequisites, open need, content compatibility, local capability → 12 and 15,
- resume checkpoint content and session state → 11C,
- curriculum ingestion, artifact body storage and the first runnable activity → 11D,
- the assessment interior on the shared frame → 13,
- AI assistance content and final microcopy → 14.

No session length, step count or timing is claimed.

---

# 4. The runner model

`TaskRunner.kt` in `core-presentation` holds the vocabularies, copied from `TRUX-v0` in its order: **seventeen states**, **six phases** (`enter → orient → work → submit → resolve → transition`), **five entry** and **five resume conditions**, **three pause classes**, and **one** next-task source — a recomputed planner selection. There is no second source, so a cached list cannot be advanced.

Tones come from `VDSX-v0`: `blocked_not_startable` is `attention` (something must be resolved, nothing was done wrong), `evaluation_pending` is `pending_unresolved`, the working states are `active`, only the two system conditions wear the fault tone.

**The first draft computed these tones inside `app-ui` with values chosen by hand.** They happened to equal `VDSX-v0` exactly. A coincidence is not a guarantee and the UI toolkit is the wrong module, so the map moved to `core-presentation` with a test that pins every state, and the validator fails if `app-ui` names a tone.

## 4.1 Entry: nothing is assumed

Every entry condition must be **confirmed**. The result names the ones that are not — and the field is called `unmet`, not `failed`, because a condition nothing can confirm yet is not a failure, and the runner does not start on an assumption either.

Today can confirm exactly one thing about its own primary task: that it is still selected. Prerequisites, the open need, content compatibility and local capability are facts other engines supply, and none exists yet. **So no task is startable today, and that is the truthful outcome** — it will change by those engines supplying facts, never by this rule being loosened. A blocked start returns to Today, is not negative evidence and is not presented as the learner's error.

A paused task is revalidated on resume under the same rule; a stale one cannot bypass current state.

## 4.2 Assistance

- Help is always requestable. There is no state in which the runner refuses, because withholding help to force independence is forbidden.
- The runner grants nothing that was not requested, and records every grant as requested.
- **H3 and H4 are never granted before the consequence is disclosed**, in measurement terms: the help changes what the attempt can prove, and a fresh unseen variant will confirm the capability later. The outcome that asks for acknowledgement exists so the disclosure cannot be skipped.
- **The rule is applied as `TRUX-v0` states it — without a scope condition.** Narrowing it to target-scoped help was considered: it would be a reasonable reading, and still a quiet reinterpretation of an accepted contract. A mutant that narrows it is caught.
- An incorrect attempt reveals nothing on its own; it only leaves every level requestable.
- Revealed target reasoning raises `requires_independent_recheck`. The runner never schedules the recheck.

## 4.3 Stop, pause, exit, evaluation

- **Stopping is always `stopped_no_penalty`**: no debt, no streak loss, no catch-up. There is no parameter that could make it otherwise.
- **The exit is available from every state**, and on screen it is the first element, before the state itself.
- A mid-segment pause is not durable, so it may never be presented as saved progress.
- **A pending evaluation is its own state** — never a pass, never a fail.

---

# 5. Recording an attempt: one learner action, one transaction

`SubmitAttempt` in `core-application` records a frozen attempt as **one transaction**: the attempt, its artifact, the learner's provenance answer and every assistance event commit together or not at all. An attempt without its assistance metadata or provenance is the half-record `LFPS-v0` forbids, because it would later read as unassisted, independent work.

- **It writes no evidence.** The runner is not the evidence evaluator; what an attempt proves is decided by the evidence pipeline (12), and while the evaluator is unavailable an attempt is `evaluation_pending`.
- **Provenance is required and asked, not inferred.** `unknown_provenance` is an honest answer and is recorded as given.
- **One timestamp per action**, so its rows cannot disagree about when it happened.
- **Derived facts are not stored.** `highest_assistance_level` and `requires_independent_recheck` are computed from the recorded events; storing them beside their source would be a second source of truth.
- Every value set — assistance level, timing, scope, source, provenance origin — equals `DDM-v0`, `TRUX-v0` **and the schema's own CHECK lists**, so the runner cannot produce a value the store would refuse.

## 5.1 How it is proven

`data-persistence` may not depend on `core-application`, so the proof is split honestly:

- **T1** proves the use case's shape with a recording store: one transaction, linked ids, no evidence, one timestamp, nothing written outside the transaction.
- **T2** proves the storage guarantee on real SQLite: the four row kinds commit together, the returned ids are the ones the rows point at, and a failure after any of them leaves none.

## 5.2 Port refinement

`appendTruth` now returns the inserted row id: rows of one action must refer to each other inside one transaction. This is a refinement of `PersistencePort`, not a fifth port, following `LDBX-v0` and `TDYX-v0`.

## 5.3 Not stored, with owners

`DDM-v0` does not name `attempt`'s fields, and 10D forbade inventing them. So:

| Field | Owner | Why not here |
|---|---|---|
| `planned_task_ref` on the attempt | 12 | the data model does not name it; adding it would invent a field |
| `runner_completion_state` | 12 | `TRUX-v0` names the concept but no value set |
| component results, target objective refs | 12 | they belong to the evidence pipeline |
| artifact body storage behind `content_ref` | 11D | the first step whose activities produce artifacts decides it |
| resume checkpoint content | 11C | session state owns checkpoint persistence |

---

# 6. The screen and the wiring

`TaskRunnerScreen` renders what core decided: the exit first, then the state as text, announced through a polite live region, then the state's explanation. A blocked start lists what is unmet in plain language and says it is neither an error nor a failure. `ConsequenceDisclosure` renders the measurement framing with an explicit acknowledgement.

Today's start action now opens the runner: entry is revalidated in core, the runner suspends the shell as `NSHX-v0` derives for a focused flow, and the exit returns to Today.

---

# 7. Mutation testing

Eighteen deliberate mutations, **all eighteen caught on the first run.** One is worth recording precisely: R16 (`appendTruth` returning the truth sequence instead of the row id) was expected to slip through, because the first attempt's row id and sequence are both 1. It was caught — but by the **foreign key**, not by the id assertion: the artifact's sequence (2) names no artifact row, and SQLite refuses the provenance insert. The record names the mechanism that actually caught it.

---

# 8. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS — 50 tests |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

**Not run: T6.** The phone was not connected. In the real app the runner can currently only show `blocked_not_startable`, because nothing can yet be confirmed startable; the consequence disclosure has no reachable caller; the atomic submission is proven at T1 and T2 only, because no activity produces an attempt yet.

---

# 9. Anti-patterns explicitly rejected

- starting a task on an unconfirmed condition, or naming an unconfirmed condition a failure,
- granting help that was not requested, or H3/H4 before the consequence is disclosed,
- narrowing an accepted disclosure rule,
- revealing a solution after the first error, or withholding help to force independence,
- a stop that costs debt, streak or catch-up; a mid-segment pause presented as saved,
- treating a pending evaluation as a pass or a fail,
- advancing a cached task list,
- writing evidence from the runner,
- recording an attempt without its provenance and assistance in the same transaction,
- storing a derived fact beside its source,
- inventing attempt fields the data model does not name,
- computing runner tones in the UI.

---

# 10. 11B acceptance contract

1. `RNRX-v0` is the accepted Task Runner.
2. States, phases, entry and resume conditions, pause classes and the next-task source equal `TRUX-v0`, in its order.
3. Tones are `VDSX-v0`'s and are computed in core.
4. Entry requires every condition to be confirmed; unconfirmed conditions are named `unmet`; nothing Today cannot confirm is assumed.
5. Help is never withheld, never granted unrequested, and H3/H4 are never granted before the consequence is disclosed — without a scope condition.
6. Stopping costs nothing, the exit is everywhere, and a pending evaluation is neither a pass nor a fail.
7. An attempt is recorded as one transaction with its artifact, provenance and assistance, and no evidence.
8. Value sets equal `DDM-v0`, `TRUX-v0` and the schema's CHECK lists.
9. Derived facts are not stored; fields the data model does not name are listed with owners.
10. `appendTruth` returning the row id is a recorded refinement; the port count stays four.
11. No text file in the repository contains U+0307.
12. 18/18 mutations are caught.
13. Nothing is claimed that was not run; T6 was not run.
14. Independent 11B QA must pass, and Stage 6–11A regressions must pass.

---

# 11. Handoff after acceptance

If accepted, 11B becomes `RNRX-v0 / D-088`.

Next numbered step: **11C — Session state**. It owns the resume checkpoint's content and persistence, the emergent session and its ended reasons, and what a checkpoint pause actually saves. It must receive a fresh PRE-STEP and explicit user approval before execution.
