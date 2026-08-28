# Daily Working Flow & Task Runner Specification — TRUX-v0

**Stage step:** 8C — Günlük çalışma akışı  
**Status:** ACCEPTED — independent 8C QA PASS  
**Decision:** `D-070`  
**Model:** `TRUX-v0 — Task Runner & Daily Working Flow UX`  
**Parent IA:** `UXIA-v0 / D-068`  
**Home contract:** `THUX-v0 / D-069`

## 1. Purpose

8C defines the **focused daily working flow**: what happens between the moment the user starts the action Today offered and the moment canonical state has absorbed the result.

It answers one primary question:

> **Bu işi şimdi nasıl yapacağım ve bıraktığımda ne olacak?**

Primary invariant:

> **The Task Runner is an execution surface. It produces attempts, artifacts, assistance metadata and provenance. It never becomes a planner, a mastery engine, a prerequisite engine, an evidence evaluator or a motivational scoring device.**

---

## 2. Binding inputs

8C consumes and preserves:

- `UXIA-v0 / D-068` — `task_runner_flow` is a focused flow; the shell may be suppressed but a safe pause/exit/resume path and a deterministic semantic return target must survive; `assessment_session_flow` is a separate focused flow.
- `THUX-v0 / D-069` — Today owns entry, primary-action precedence, resume surfacing and the post-return render; a resumable session must be revalidated and cannot bypass current prerequisite/state rules.
- D-001/D-002/D-009/D-010 — elapsed time, activity completion and streaks are not mastery.
- `GRE-v0` — task completion is not mastery; evidence comes from real attempts and artifacts.
- `RVR-v0` — retention/review ownership stays outside the runner.
- `PRG-v0` — startability and resumability are Skill-prerequisite state, not UI position.
- `PBR-v0` — task ordering and selection stay planner-owned.
- `PDT-v0` — every user-facing reason inside the flow is derived from structured trace facts.
- `SRR-v0` — pausing and stopping are not debt; an interrupted high-stakes attempt is not negative evidence.
- D-033 capacity contract — the daily budget is hard; replan changes only the remaining budget.
- D-034 task taxonomy — `LearningNeed`, `TaskCandidate`, `PlannedTask`, `Attempt`/`Artifact` and `Evidence` remain distinct; lifecycle state is not evidence.
- 2D assistance contract — H0–H4 content levels, artifact origin, assistance timing and the four evidence-use classes.
- `DMA/WBA/MCA` — assessment execution is an evidence workflow, not a gradebook.
- `TEIP-v0` — task language and target construct remain separate; dual-target results stay component-separable.
- `WLRM-v0` — weakness localisation and remediation routing stay outside the runner.
- V1 local-first Android scope and the AI-degraded deterministic-core requirement.

8C introduces no new planner priority, mastery threshold, prerequisite edge, evidence weight, retention interval, English quota, fixed session length or required daily task count.

---

# 3. Scope boundary

## 3.1 8C decides

- what a daily working session is and is not,
- the shared focused-flow frame inherited by both focused flows,
- the task-run lifecycle and its phases,
- entry and resume revalidation semantics,
- checkpoint, pause and abandon semantics,
- assistance request/escalation choreography and consequence disclosure,
- artifact provenance capture semantics,
- submission and post-attempt resolution semantics,
- in-flight behavior during replan and capacity change,
- task-to-task transition and return-to-Today semantics,
- runner behavior for Technical English integration modes,
- runner semantic states including degraded and recovery states,
- focused-flow accessibility baseline,
- explicit handoff boundaries to 8D–8G and implementation stages.

## 3.2 8C does not decide

- assessment question navigation, submission and result layout → 8D,
- final Skill/progress/weakness labels and visualisation → 8E,
- typography, color, spacing, iconography, motion and component library → 8F,
- final wireframes and prototype geometry → 8G,
- navigation framework and UI technology → 9A/10,
- physical persistence schema → 9C,
- code runner, compiler, sandbox and provider integration → 9E/14D,
- runtime planner/mastery implementation → 12–14,
- AI tutor prompt behavior and evaluator implementation → 14,
- notification design → 16,
- final accessibility and usability calibration → 17–18.

No fixed step count, fixed screen sequence, fixed session length, countdown timer or motion specification is canonical in 8C.

---

# 4. What a daily working session is

A working session is the **emergent sequence of focused task runs the user chooses to do**. It is not a container the planner fills, not a unit of achievement and not a graded object.

```text
WorkingSession
- session_id
- started_at
- entry_source: today_primary_action | today_queue_row | entity_context | resume_entry
- task_runs[]
- remaining_capacity_context_ref
- ended_reason: user_stopped | plan_exhausted | capacity_reached | interrupted | recovery_required
```

Binding consequences:

- `plan_exhausted` does not mean the day succeeded.
- `user_stopped` does not mean the day failed.
- A session has no required task count, no required duration and no completion percentage.
- Session identity is history, not evidence.
- The runner never reports a session-level score, grade or level-up.

---

# 5. Shared focused-flow frame

Both `task_runner_flow` and `assessment_session_flow` inherit one frame so that two focused flows cannot hold contradictory truths:

```text
entry_revalidation
safe_pause_and_exit_availability
resume_revalidation
capacity_and_replan_interaction
degraded_and_recovery_behavior
deterministic_semantic_return
```

8C owns this frame for both flows. 8C owns the **interior choreography only for `task_runner_flow`**. The assessment interior — question navigation, item-level submission, result presentation — belongs to 8D and must not be duplicated here.

When a selected `PlannedTask` has `primary_purpose = assess`, the frame applies and the interior is delegated to `assessment_session_flow`.

---

# 6. Task-run lifecycle

Canonical phases:

```text
enter -> orient -> work -> submit -> resolve -> transition
```

Non-linear transitions available from `work`:

```text
pause | abandon | recover
```

## 6.1 `enter` — revalidation before anything is shown as startable

Entry must revalidate against current canonical state:

1. the `PlannedTask` is still selected and current,
2. hard prerequisites are still satisfied (`PRG-v0`),
3. the underlying `LearningNeed` is still open,
4. content/curriculum version is still compatible,
5. required local capability is available for the chosen activity.

If revalidation fails the flow does **not** start. It returns to Today with a `PDT-v0` trace-derived reason. A failed entry is never negative evidence and never a user error message.

## 6.2 `orient` — what, why, how long, and what will count

Orientation is minimal but must be able to express:

- the task purpose in learner terms,
- the target Skill/Objective context at progressive-disclosure depth,
- the duration estimate as planning context, not a promise,
- the entry to the full `planner_explanation` surface,
- **the assistance policy for this task**, including which help is available and how revealing help changes evidence interpretation.

Assistance-policy disclosure before independent work is mandatory. A user must never discover after the fact that the help they took removed the attempt from independent evidence.

Orientation must not preview the answer, and must not state a mastery prediction.

## 6.3 `work` — activity-specific, segmented

The interior is activity-shaped (`activity_kind`), not one universal question screen. Work is divided into **segments** whose boundaries are candidate safe checkpoints.

The runner must not:
- force linear completion of the whole day plan before allowing exit,
- impose a visible countdown that converts the duration estimate into pressure,
- treat slower work as a mastery signal.

## 6.4 `submit` — the attempt boundary

Submission freezes the attempt. From that moment:

```text
post_submit_explanation_contaminates_prior_attempt = false
```

Feedback, worked examples and AI explanation given after submission do not retroactively downgrade the completed attempt. A **new attempt on the same item after solution exposure** is not independent evidence.

Not every task ends in a submission. Teaching and exposure segments may end without an attempt; when there is no attempt there is no evidence, and that is a valid outcome rather than a missing result.

## 6.5 `resolve` — feedback without mastery claims

The runner may present correctness feedback, worked explanation and the fact that an attempt was recorded. It may not:

- state or imply a mastery/level change,
- present a score as a capability verdict,
- claim a Skill state that canonical state did not actually produce.

The runner hands off:

```text
AttemptSubmission
- planned_task_ref
- target_objective_refs[]
- artifact_refs[]
- artifact_origin
- assistance_events[]
- highest_assistance_level
- assistance_timing_summary
- component_results[]        # dual-target tasks
- integration_mode
- runner_completion_state
- requires_independent_recheck_flag
```

Interpretation belongs to the evidence/mastery/retention/remediation pipelines. `runner_completion_state` is workflow state, never a mastery value.

## 6.6 `transition` — continuity without a second planner

After resolution the user may return to Today or continue working.

Binding rule:

> **The next task is always the planner's recomputed current selection after post-attempt processing and any replan. The runner must not advance through a locally cached list.**

Continuity is a convenience over a re-resolved plan, not a pre-baked queue. If replan removed the expected next task, the transition shows the current truth with a trace-derived reason rather than the stale expectation.

---

# 7. Checkpoint, pause and abandon

## 7.1 Safe checkpoint

A boundary qualifies as a safe checkpoint when:

- the completed segment is pedagogically meaningful on its own,
- artifact/runner state can be persisted honestly,
- no partially exposed solution state would make resumption invalid,
- no independent attempt is left half-evaluated.

## 7.2 Pause classes

### `checkpoint_pause`
Real `paused_progress`. Produces a `ResumeContext` (`learning_need_key`, `source_task_id`, `checkpoint_id`, `completed_segments`, `remaining_segments`, `artifact_state_ref?`).

### `mid_segment_pause`
Transient local UI state only. It may be discarded and must never be presented as durable saved progress.

### `high_stakes_pause`
A pause inside an independent/H0 attempt, a diagnostic or a verification. Allowed, but marked. After a long gap the continuation may no longer be trustworthy as independent evidence; the runner must not silently continue it as such.

## 7.3 Resume revalidation

Resuming runs the same deterministic gate:

1. content/curriculum version compatible,
2. prerequisites still eligible,
3. the underlying `LearningNeed` still open,
4. runner/artifact state intact,
5. for high-stakes work, the interruption gap does not compromise independent-evidence integrity.

If any condition fails, the checkpoint is not resumable as-is. The system offers a fresh alternative for the same need. An invalidated resume is **never** negative evidence and never a failure message.

A paused checkpoint is never automatically the next day's first task; it re-enters as a `continue_learning` need and is re-ranked by `PBR-v0`.

## 7.4 Abandon and stop

Stopping is always available and reachable in one deliberate action from the runner.

- `planned_but_not_started` is not a learning gap.
- `started_but_user_stopped` is session history only.
- An incomplete attempt is not a wrong answer.
- Stopping produces no debt, no streak loss and no catch-up obligation.

Forbidden exit framing:
- `you will lose your progress`,
- `your streak will break`,
- `you only need N more minutes to succeed today`,
- any confirmation dialog whose purpose is to make stopping feel like failure.

---

# 8. Assistance choreography

## 8.1 Availability

In teaching and practice work, assistance is always requestable. Withholding help to force independence is forbidden — the product teaches first and measures separately.

## 8.2 Escalation

Assistance escalates on request:

```text
H1 orientation -> H2 targeted concept -> H3 partial scaffold -> H4 full solution
```

The runner must not:
- jump to H4 without an explicit request,
- auto-reveal the solution on a first incorrect attempt,
- silently deliver H3-level scaffolding while labelling it a hint.

## 8.3 Consequence disclosure

Before granting H3 or H4 the runner states, in measurement terms rather than punishment terms, that the attempt will not count as independent evidence and that a fresh unseen variant will be used later to confirm the capability.

Framing rule:

```text
"this changes what this attempt can prove"   # allowed
"you will be penalised for this"             # forbidden
```

## 8.4 After solution exposure

- The same item may be repeated for learning, explicitly labelled as practice.
- The same item repeated after exposure is never presented as a mastery path.
- The runner raises `requires_independent_recheck` for the affected Objective.
- **The runner does not schedule the recheck.** Timing, form and target belong to the planner and the remediation/retention pipelines.

## 8.5 Recorded assistance metadata

Each granted assistance records at least:

```text
AssistanceEvent
- level: H1 | H2 | H3 | H4
- timing: before_attempt | during_attempt | after_submit | after_failure
- target_scope: target_objective | non_target_support
- source: deterministic_content | ai_generated
- requested_by_user: true | false
```

Help targeted at non-target support (environment setup, boilerplate, non-target language segments) is distinguished from help that reveals the target reasoning.

## 8.6 Assessment-mode conversion

When a task is measuring current independent capability and the user requests revealing help, the item converts to learning and a fresh recheck is flagged. The conversion is explicit and non-punitive. Exact assessment-session presentation of this conversion belongs to 8D.

---

# 9. Artifact provenance

The system cannot reliably detect pasted or externally generated work, and must not try to police it by suspicion.

Binding rules:

- provenance is **asked, not inferred**,
- the disclosure is cheap and low-friction,
- honest disclosure is never framed as a confession or a penalty,
- unknown provenance is recorded as `unknown_provenance` rather than guessed,
- a `generated_or_copied` artifact still has learning value for reading, tracing and explanation tasks.

Captured origin values follow 2D: `user_authored`, `user_authored_with_assistance`, `mixed_authorship`, `generated_or_copied`, `unknown_provenance`.

Forbidden:
- accusing the user of cheating,
- silently downgrading state on suspicion without a recorded provenance answer,
- making the honest answer the slow or discouraged path.

---

# 10. Capacity and replan during a run

## 10.1 In-flight protection

An in-flight task run is never destroyed by a replan. Replan preserves completed evidence and re-resolves only unstarted work.

If the in-flight task itself becomes invalid, the runner drives to the nearest safe checkpoint, preserves whatever evidence legitimately exists and exits with a trace-derived explanation.

## 10.2 Remaining time decreased

The current run may finish or checkpoint-pause. Unstarted tasks are re-resolved. Removed unstarted tasks are not failures and not debt.

## 10.3 Remaining time increased

A fresh mini-plan is produced from current state. The previously deferred list is not blindly replayed.

## 10.4 Early finish and overrun

- Early finish triggers `TASK_FINISHED_EARLY`; the transition shows the recomputed next selection.
- Overrun does not force-cut work whose evidence integrity depends on completion. The runner surfaces the overrun truthfully and lets the user decide; `TASK_OVERRAN_ESTIMATE` fires afterwards.

## 10.5 Reason presentation inside the flow

Any in-flow explanation of a plan change is a bounded projection of `PDT-v0` trace facts, with the full explanation available on the shared `planner_explanation` surface. Free-form AI reasoning is never the truth source.

---

# 11. Technical English inside the runner

- The instruction/scaffold mode is task metadata, not runner improvisation.
- All four `TEIP-v0` integration modes are supported without changing the runner's frame.
- In `dual_target_integrated`, component submission and component feedback stay separable; a single overall pass/fail broadcast across technical and English targets is forbidden.
- Requesting a gloss or translation for a **non-target** language segment is support, not assistance against the target construct.
- Scaffold usage is never displayed as a penalty, a level or a deficiency.
- No English quota, streak or debt exists inside the flow.

---

# 12. Degraded and recovery behavior

## 12.1 Offline

Locally runnable work remains fully runnable. Network-dependent capability degrades with an explicit truthful state. The runner never fakes a completed remote step.

## 12.2 AI unavailable

The deterministic core still runs: content, deterministic validators and locally available tooling.

Binding rule for AI-evaluated open-ended work:

```text
ai_evaluator_unavailable -> evaluation_pending
evaluation_pending -> no evidence written
```

The attempt is neither auto-passed nor auto-failed. `evaluation_pending` is a visible, truthful state.

## 12.3 Interruption and crash

Recovery restores the last durable checkpoint only. The runner must not claim work that was never persisted, and must not silently discard work that was.

## 12.4 Data recovery

When local state integrity is uncertain, recovery supersedes normal work, consistent with the Today precedence contract. Silent progress reset is forbidden.

---

# 13. Runner semantic states

```text
entering_revalidating
blocked_not_startable
orientation
active_work
assistance_open
submitting
feedback_resolved
evaluation_pending
checkpoint_paused
resume_revalidating
resume_invalidated
replan_interrupted
stopped_no_penalty
offline_local_capable
ai_unavailable_deterministic_core
error_recoverable
data_recovery_required
```

Each state must be distinguishable in text, not by color or motion alone.

---

# 14. Accessibility baseline for focused work

- The primary work action and the exit/pause action both have textual accessible names.
- Exit and pause remain reachable when the shell is visually suppressed.
- Assistance level and its evidence consequence are conveyed in text.
- `evaluation_pending`, `resume_invalidated` and recovery states are announceable.
- Segment progress is conveyed semantically, not by a decorative progress ring alone.
- Duration estimates are never the only identifier of a task or segment.

---

# 15. Anti-patterns explicitly rejected

The daily working flow must not become:

- a guilt-driven exit gate,
- a streak or daily-goal enforcement device,
- a countdown-timer pressure surface,
- a second planner advancing through a cached list,
- a mastery or level-up announcer,
- a solution auto-reveal on first error,
- a same-item retry presented as independent proof,
- a cheating-detection interrogation,
- a place where asking for help is visibly penalised,
- a surface that writes evidence without a real attempt,
- a surface that fakes success while the evaluator is unavailable,
- a duplicate assessment interior competing with 8D,
- a place where pausing is presented as losing progress.

---

# 16. 8C acceptance contract

8C can be accepted only if independent QA verifies at minimum:

1. The runner remains an execution surface and never claims planner, mastery, prerequisite or evidence-evaluation authority.
2. A working session is emergent and ungraded; no required length, task count or completion percentage exists.
3. Entry revalidation prevents non-startable work from being started.
4. Orientation discloses the assistance policy before independent work.
5. Assistance is always available in teaching/practice work and escalates only on request.
6. Solution exposure never becomes a same-item mastery path, and recheck scheduling stays planner-owned.
7. Post-submit explanation does not retroactively contaminate a completed attempt.
8. Artifact provenance is asked rather than inferred, and honest disclosure is non-punitive.
9. Checkpoint, mid-segment and high-stakes pauses are semantically distinct.
10. Resume revalidation cannot bypass prerequisite or version validity, and an invalidated resume is not negative evidence.
11. Stopping produces no debt, streak loss or catch-up obligation, and no guilt framing.
12. An in-flight run survives replan; only unstarted work is re-resolved.
13. Continuity uses the planner's recomputed selection, never a cached local list.
14. Task completion does not imply mastery, and the runner makes no state claim canonical state did not produce.
15. `evaluation_pending` writes no evidence and is neither pass nor fail.
16. Offline and AI-degraded modes preserve deterministic core capability truthfully.
17. All four Technical English integration modes are supported with component-separable dual-target results and no quota/streak/debt.
18. The shared focused-flow frame is defined once and the assessment interior remains 8D's.
19. In-flow reasons are bounded projections of PDT-v0 trace facts.
20. 8D/8E/8F/8G/9/10 implementation boundaries remain open.
21. Stage 6, Stage 7, 8A and 8B accepted contracts still validate.

---

# 17. Handoff after acceptance

If accepted, 8C becomes `TRUX-v0 / D-070`.

Next numbered step:

**8D — Sınav UX**

8D will design the assessment session interior — question navigation, item submission, pause/resume within an assessment and result presentation — inheriting this shared focused-flow frame and preserving the evidence/state/replan separation. It must receive a fresh PRE-STEP and explicit user approval before execution.
