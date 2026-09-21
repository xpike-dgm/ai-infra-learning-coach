# Session State Specification — SESX-v0

**Stage step:** 11C — Session state  
**Status:** ACCEPTED — independent 11C QA PASS  
**Decision:** `D-089`  
**Model:** `SESX-v0 — Session State`  
**Semantics:** `TRUX-v0 / D-070` §4, §7; `SRR-v0` `ResumeContext`  
**Runner:** `RNRX-v0 / D-088`  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

11C decides what stopping actually keeps. It fills the `resume_checkpoint.context` column 10D left to step 11, decides when a pause is durable, what a later resume can truthfully confirm from what was stored, and what a working session is while it lasts.

It answers one primary question:

> **Öğrenci durduğunda gerçekte ne saklanır, sonraki bir devam neyi dürüstçe doğrulayabilir ve oturum neydi?**

Primary invariant:

> **A pause saves where the work is, never how long it took or how well it went.** Only a durable pause is written, and only a written pause may be shown as saved; a resume confirms only what the stored checkpoint can prove; and a session is history, never a score.

---

# 2. What was found before writing any session code

- **The high-stakes mark had nowhere to live.** `TRUX-v0` §7.2 requires a high-stakes pause to be *marked*, so that a long gap is not silently continued as independent work. But the `ResumeContext` field list does not carry the pause class. A mark that is not stored cannot be honoured on resume. The stored context therefore carries the durable pause kind — that is the mark `TRUX-v0` requires, not a new field of meaning.
- **A resume could not read what it resumes from.** `PersistencePort` could append truth and read projections, but not read a truth row back. See §6.
- **Two broken Turkish words on `main`**, both from 11A's sync (commit `1cbe4aa`): a doubled dotless *ı* in "çalıştırılmadı" (four living documents) and a dropped *ş* in "doğrulanmamış oturum" (`STEP_STATUS.md`). 11B's guard hunts U+0307 and could not see either. Both were restored, and the 11C validator fails if either exact broken form comes back.
- The worktree had no `android/local.properties` (it is gitignored), so the first `assembleDebug` could not find the SDK. That is the build environment, not the repository, and nothing about it was committed.

---

# 3. Scope boundary

## 3.1 11C decides

- the stored content of `resume_checkpoint.context` and its format,
- when a pause is durable, and the fact that a mid-segment pause has no stored form,
- which resume conditions a stored checkpoint can confirm, and which it never can,
- the emergent working session: when it starts, what it holds, how it ends.

## 3.2 11C does not decide

- which checkpoint a resume uses, and whether it is offered at all → the planner's `resume_context_ref` and `continue_learning` need, 12,
- content compatibility, prerequisites and the open need at resume → 12, 11D,
- segment boundaries, `checkpoint_ids[]`, artifact body storage → 11D,
- a gap policy for continuing high-stakes work → 13, calibrated in 18D,
- durable session and learning history → 16B.

No gap threshold, session length or task count is claimed.

---

# 4. What a checkpoint saves

A checkpoint is `SRR-v0`'s `ResumeContext`, stored in `resume_checkpoint.context`:

| Field | Meaning | Owner of its value |
|---|---|---|
| `kind` | `checkpoint_pause` or `high_stakes_pause` — the mark | 11C |
| `learning_need_key` | the need this work served | planner, 12 |
| `source_task_id` | the planned task that was paused | planner, 12 |
| `checkpoint_id` | one of the task's declared `checkpoint_ids[]` — a content identity, not the row id | content, 11D |
| `completed_segments` | at least one | content, 11D |
| `remaining_segments` | at least one, none also completed | content, 11D |
| `artifact_state_ref` | optional pointer to artifact state | 11D |

**What it does not save:** elapsed time, attempt count, score, progress fraction, streak, remaining minutes. Time spent is not progress (D-001/D-002), and a pause is not a verdict.

## 4.1 Stored form

`resume_context/1`, then one `key=value` per line in fixed order. Values are identity tokens, and a value that would need escaping is **refused at construction rather than escaped** — so reading it back never depends on an escaping rule.

Decoding is **strict**. A missing, unknown, repeated or reordered key, a wrong format version, or a value the type refuses all decode to *nothing* — never to a best guess — because a checkpoint that cannot be read back exactly is not "runner state intact", and resuming from a guessed context would claim work nobody saved. A row that does not decode is still returned as a row, so "cannot be resumed as-is" stays distinct from "there is no such checkpoint".

## 4.2 One pause, one row

`ResumeCheckpoints.record` writes **one transaction and one `resume_checkpoint` row** — no attempt, no evidence, no projection. The row is append-only like every truth row: a later pause of the same task appends beside the earlier one, and there is no "consumed" or "latest" flag, because that would be either an `UPDATE` of truth or a stored copy of something derivable. Which checkpoint a resume refers to is the planner's `resume_context_ref`.

The schema is unchanged and no migration was added: `context TEXT NOT NULL` was already the column 10D reserved for this step.

---

# 5. When a pause is durable

`PausePolicy.classify` in `core-presentation`:

- **High-stakes work** (an independent/H0 attempt, a diagnostic, a verification) pauses durably and **marked**, wherever it stops (`TRUX-v0` §7.2).
- **Any other work** pauses durably only when all four safe-checkpoint conditions (`TRUX-v0` §7.1) are *confirmed*. Otherwise the pause is `mid_segment_pause`, and the unconfirmed conditions are named `unmet` — the same rule, and the same word, as entry.

A mid-segment pause is not written, and **it does not change the runner's state**: only a durable pause becomes `checkpoint_paused`, the state whose label says the place was saved. `CheckpointKind` has exactly the two durable values, so a mid-segment pause has no stored representation to leak through.

Whether a boundary is pedagogically meaningful and whether artifact state can be persisted are content and artifact facts (11D). **So today no ordinary pause is durable** — the truthful outcome, which will change by those facts arriving, not by the rule being loosened.

---

# 6. What a resume can confirm

`ResumeConfirmation.fromCheckpoint` gives at most two of the five resume conditions:

- **`runner_and_artifact_state_intact`** — when the context decodes exactly **and** references no artifact state. Artifact storage is 11D's; a referenced artifact cannot yet be checked, so it is not assumed intact.
- **`high_stakes_gap_integrity_acceptable`** — for an ordinary `checkpoint_pause` only. `TRUX-v0` scopes that condition to high-stakes work, and the pause kind is a recorded fact, not an assumption. **For a high-stakes pause no gap policy has been accepted**, so the gap is never confirmed acceptable and the work is never silently continued as independent. Inventing a threshold is forbidden; the policy belongs to 13 and its calibration to 18D.

Content compatibility, prerequisites and the open need are never confirmed from a checkpoint. **So no checkpoint is resumable today**, and `resume_invalidated` says it without blame: *not an error, not a failure, and nothing saved was erased* — the row is still there.

**Today does not offer checkpoints.** A paused checkpoint re-enters as the planner's `continue_learning` need and is re-ranked (`TRUX-v0`, `SRR-v0`); surfacing it directly would make Today a second planner.

## 6.1 Port refinement

`PersistencePort.readTruth(kind, id)` reads one truth row back as the `TruthRecord` that was appended: payload columns, and the three time columns as the `StudyTimestamp` they were written from. It adds no mutation path. A row with no time of its own (an evidence event's Objectives) is read through its parent, not alone. Like `curriculumPublished()`, it is a refinement, and the port count stays four.

---

# 7. The working session

`WorkingSession` is `TRUX-v0` §4's emergent sequence of runs: `session_id`, `started_at`, `entry_source`, `task_runs`, `remaining_capacity_context_ref`, `ended_reason`. A task run holds the task and the workflow state it was left in — never a result. **No field can hold a score, grade, percentage, duration or required count**, and a test reads the class's fields to keep it so.

- **It starts with the first run that actually starts.** A blocked entry is not a run. While nothing is startable, no session starts at all.
- **It ends once**, with the reason of the event that ended it:

| Event | `ended_reason` |
|---|---|
| the learner used the exit | `user_stopped` |
| recomputed selection empty, planner's capacity verdict reached | `capacity_reached` |
| recomputed selection empty otherwise | `plan_exhausted` |
| recomputed selection not empty | — the session continues |
| the focused flow went away without an exit | `interrupted` |
| store integrity became uncertain | `recovery_required` |

  The capacity verdict is the planner's, reported and never derived here (11A). A later event cannot rewrite an ended session, and an ended session does not grow.
- **None of the reasons is a verdict.** `plan_exhausted` is not success and `user_stopped` is not failure, so no reason carries a tone, a flag or a score.
- **It is not stored.** `DDM-v0` names no working-session entity and 10D forbade inventing one; what a session did is already truth (attempts, checkpoints). Durable history is 16B's. A session therefore lives as long as the process: a killed process resumes no session, and a later resume starts a new one from `resume_entry`.

In the app, Today's start action starts a session only if entry was startable, and the exit ends it as `user_stopped`. The other reasons have no producer yet: `plan_exhausted` and `capacity_reached` need the planner (12), an in-memory session is discarded with its process, and on `recovery_required` the recovery surface replaces the shell and the session with it.

---

# 8. Mutation testing

Twenty deliberate mutations, **all twenty caught on the first run, every one by a failing test** rather than by a compilation error — the run was repeated to record the mechanism. Among them: a decoder that accepts an extra key or ignores the format version, a storable mid-segment pause, a pause that is durable when *any* condition is confirmed, a mid-segment pause shown as saved, an artifact assumed intact, a high-stakes gap assumed acceptable, a checkpoint that confirms content compatibility, a blocked entry that starts a session, an ended session rewritten or extended, a session with a duration field, a pause outside its transaction or writing an attempt, and a read that loses the offset's unit.

**Correction (11D).** The harness that produced this number had never actually run Gradle: it invoked `cmd /c gradlew.bat` with a working directory, which Windows does not resolve, so every mutant returned a non-zero exit code for the wrong reason and was recorded as caught. 11D found it, fixed the harness — the wrapper is now invoked by absolute path, and a run whose output carries no Gradle build result is refused rather than classified — and **re-ran this suite honestly: 20/20, every one caught by a failing test.** The number stands and is now verified, which it was not when it was first published.

The first regression sweep then failed a **standing** check, not a new one: 10C's validator found `.lowercase()` in the session-field test — a locale-naive case transform this repository forbids everywhere, tests included. The test now compares with `ignoreCase`, and the mutant it guards (a session duration field) was re-run and is still caught.

---

# 9. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS — 55 tests |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

**Not run: T6.** The phone was not connected. In the real app no activity reaches active work, so nothing can pause, no checkpoint is written, no resume is reachable and no session starts; the new texts were not seen on a device.

---

# 10. Anti-patterns explicitly rejected

- writing a mid-segment pause, or showing one as saved,
- a checkpoint that stores elapsed time, a score or a count,
- decoding a checkpoint by best guess,
- updating a checkpoint or flagging it consumed,
- confirming a resume condition on an assumption, or inventing a gap threshold,
- presenting an invalidated resume as a failure or an erasure,
- offering a checkpoint on Today past the planner,
- scoring, grading or timing a session, or persisting it as an invented entity,
- rewriting or extending an ended session,
- deriving a capacity verdict from a session.

---

# 11. 11C acceptance contract

1. `SESX-v0` is the accepted session state.
2. Safe-checkpoint conditions, durable pause kinds, `ResumeContext` fields, entry sources and ended reasons equal `TRUX-v0`, in its order.
3. A checkpoint stores the `ResumeContext` plus its durable kind, and nothing about time or result.
4. The stored form is versioned and decoded strictly; undecodable is distinct from missing.
5. One pause is one transaction and one append-only row, with no attempt, evidence or projection.
6. Only a durable pause is written and shown as saved; a mid-segment pause has no stored form.
7. A resume confirms only what the stored checkpoint proves; no gap threshold is invented; nothing is resumable today.
8. No checkpoint is offered on Today; re-entry is the planner's `continue_learning` need.
9. The working session is emergent, unscored and not stored; it starts with a started run and ends once with a `TRUX-v0` reason.
10. `readTruth` is a recorded port refinement; the port count stays four; the schema is unchanged.
11. 20/20 mutations are caught.
12. Nothing is claimed that was not run; T6 was not run.
13. Independent 11C QA must pass, and Stage 6–11B regressions must pass.

---

# 12. Handoff after acceptance

If accepted, 11C becomes `SESX-v0 / D-089`.

Next numbered step: **11D — Günlük mikro quiz**. It must receive a fresh PRE-STEP and explicit user approval before execution. Open loops carried: artifact body storage, segment boundaries and the first runnable activity (11D and 15), the facts that confirm entry and resume and the `continue_learning` re-entry (12), the high-stakes gap policy (13, 18D), durable session history (16B), and the T6 device run.
