# Assessment Session UX Specification — ASUX-v0

**Stage step:** 8D — Sınav UX  
**Status:** ACCEPTED — independent 8D QA PASS  
**Decision:** `D-071`  
**Model:** `ASUX-v0 — Assessment Session & Result UX`  
**Parent IA:** `UXIA-v0 / D-068`  
**Focused-flow frame:** `TRUX-v0 / D-070`

## 1. Purpose

8D defines the **assessment session interior and the result presentation**: how a selected assessment task is executed item by item, and what the user is honestly told afterwards.

It answers one primary question:

> **Bu ölçüm nasıl yürüyecek ve sonucunda gerçekte ne değişti?**

Primary invariant:

> **An assessment session is an evidence-collection workflow. It never becomes a gradebook, a score-based mastery authority, a pass/fail verdict or a second state engine.**

---

## 2. Binding inputs

8D consumes and preserves:

- `UXIA-v0 / D-068` — `assessment_session_flow` is the focused flow for daily/weekly/monthly assessment execution; `assessment_report` is a Progress-owned historical surface that cannot behave as an independent score-based mastery authority.
- `TRUX-v0 / D-070` — the shared focused-flow frame (entry revalidation, safe pause/exit availability, resume revalidation, capacity/replan interaction, degraded/recovery behavior, deterministic semantic return) is inherited, not redefined. Submission freezes the attempt. Provenance is asked, not inferred.
- `THUX-v0 / D-069` — assessment appears on Today only when selected or meaningful; it is never a permanent exam section, a daily quota or a gradebook shortcut.
- `DMA-v0` — daily micro assessment intent, item minimum validity contract, evaluator status, invalid-item safety, same-item/variant guard, assessment reason codes and the result contract.
- `WBA-v0` — weekly blueprint composition, atomic block/split behavior, pause/resume recomposition, incomplete-session semantics and `h0_required` default.
- `MCA-v0` — monthly capability composition, longitudinal and transfer coverage, and the semantic result summary families.
- `QAB-v0` / `AIV-v0` — item trust: high-stakes use requires trusted/validated items or objective-specific deterministic verification; unvalidated AI-generated items cannot produce strong mastery-changing evidence.
- 2D assistance contract — H0–H4 levels, timing, artifact origin and the four evidence-use classes.
- `GRE-v0` / `RVR-v0` / `PRG-v0` / `PBR-v0` / `PDT-v0` — mastery, retention, prerequisite readiness, priority and user-facing reasons remain externally owned.
- `TEIP-v0` / `TEPM-v0` — Technical English construct separation and qualified profile semantics.
- V1 local-first Android scope and the AI-degraded deterministic-core requirement.

8D introduces no new blueprint policy, passing threshold, scoring rule, item-validation standard, retention interval, assessment quota or fixed question count.

---

# 3. Scope boundary

## 3.1 8D decides

- the single assessment session interior shared by all assessment scopes,
- block and atomic-evidence-boundary presentation,
- the item presentation contract,
- in-session navigation and submission semantics,
- in-session assistance choreography and its visible evidence consequence,
- independence mode and allowed-tools disclosure,
- in-session pause/resume and slot recomposition semantics,
- incomplete-session presentation,
- item dispute reporting semantics,
- evaluator-status presentation,
- the semantic result surface and its relationship to canonical state and replan,
- the boundary between the in-session result view and the Progress-owned `assessment_report`,
- Technical English safety inside an assessment session,
- assessment session semantic states,
- session accessibility baseline,
- explicit handoff boundaries to 8E–8G and implementation stages.

## 3.2 8D does not decide

- what gets measured, when, or in what composition → Stage 4 (`DMA-v0` / `WBA-v0` / `MCA-v0`), unchanged,
- diagnostic execution → `task_runner_flow` under `TRUX-v0`, with `VDW-v0` / `EED-v0` governing behavior,
- question-bank schema and content authoring → 4D/15,
- item validation pipeline internals → 4E/14,
- final Skill/progress/weakness labels and visualisation → 8E,
- typography, color, spacing, iconography, motion and component library → 8F,
- final wireframes and prototype geometry → 8G,
- navigation framework and UI technology → 9A/10,
- physical persistence schema → 9C,
- evaluator, code-runner and provider integration → 9E/13/14,
- notification design → 16,
- final accessibility and usability calibration → 17–18.

No passing threshold, percentage grade, fixed question count, countdown clock or fixed geometry is canonical in 8D.

---

# 4. One interior for every assessment scope

Daily, weekly and monthly assessments share the same evidence validity, assistance, prerequisite, item-trust and result rules. They differ in blueprint composition and target pool — decisions that belong to Stage 4 and are already made.

Binding rule:

> **There is exactly one assessment session interior. `assessment_scope` is displayed context, never a different screen family and never a different rule set.**

```text
AssessmentSession
- session_id
- assessment_scope: daily_micro | weekly_blueprint | monthly_capability
- assessment_intent[]
- planned_task_ref
- blocks[]
- independence_mode
- allowed_tools_policy
- session_state
- resume_context_ref?
```

A weekly or monthly label does not raise the stakes of an item, change what evidence it can produce, or earn extra weight. It describes coverage breadth, not authority.

---

# 5. Blocks and atomic evidence boundaries

A session is a sequence of blocks; a block is a sequence of **atomic evidence boundaries**.

```text
AssessmentSession
→ Block A
   → boundary A1 (item)
   → boundary A2 (testlet)
→ safe checkpoint
→ Block B
```

Binding rules:

- The **atomic evidence boundary is the unit of submission**, not "a page" or "a screen".
- A boundary is never split, interrupted mid-way by a forced cut, or partially scored.
- Blocks are separated by safe checkpoints, inheriting the `TRUX-v0` checkpoint contract.
- Coding, project or artifact work without a safe checkpoint is not arbitrarily divided.
- Items in a declared `dependency_group_id` behave as one evidence group and are not presented as independent confirmations of each other.

---

# 6. Item presentation contract

An item view is a projection of a validated assessment item. It is not a new item identity and it does not carry state authority.

```text
AssessmentItemView
- item_ref
- item_version
- boundary_ref
- prompt_content
- response_affordance
- independence_mode
- allowed_tools_policy
- assistance_entry
- dispute_entry
- position_context          # e.g. block progress; never a countdown
- integration_mode?         # Technical English context when relevant
```

The item view must not show:

- the target Objective's current mastery state,
- a running score, percentage or grade,
- a countdown clock or time-pressure device,
- a difficulty rating presented as the user's level,
- other users, ranks or comparisons,
- the answer or a solution-revealing hint before an attempt,
- a streak, quota or exam-completion percentage as a success metric.

Position context may orient the user inside the session ("second of four blocks"). It is orientation, not performance.

## 6.1 Independence mode and allowed tools are disclosed up front

Mastery- or verification-bearing slots default to:

```text
independence_mode = h0_required
```

The user is told, before responding, what independent means for this item and which tools are permitted. Objective-appropriate tool use — a real terminal, debugger or profiler when the Objective measures exactly that — does not break H0 and must not be implied to.

Undisclosed independence rules are forbidden. A user must never learn after the fact that a permitted action compromised the attempt.

---

# 7. Navigation and submission

## 7.1 Submission freezes

Consistent with `TRUX-v0`:

```text
submitted_boundary_is_frozen = true
```

A submitted boundary cannot be revisited, edited, retracted or resubmitted within the session. Post-submission feedback does not retroactively contaminate it.

## 7.2 Navigation before submission

Within an open block, unsubmitted boundaries remain freely navigable — the user may move forward, return, and answer in any order. This is ordinary exam ergonomics and creates no evidence problem, because nothing is frozen until it is submitted.

## 7.3 Skipping

A boundary may be left unanswered.

```text
unsubmitted_boundary != incorrect
```

Skipping is not failure, not negative evidence and not a penalty. It leaves the underlying measurement need unresolved in current state.

## 7.4 Exit

Exit and pause remain available at all times through the inherited `TRUX-v0` frame, reachable in one deliberate action. Leaving an assessment is never framed as quitting, losing or wasting the session.

---

# 8. Assistance inside an assessment

## 8.1 Help is never blocked

Requesting help during an assessment is permitted. Blocking it to force independence is forbidden.

```text
assistance_request_is_negative_evidence = false
```

## 8.2 The consequence is visible and non-punitive

Before assistance that would reveal target reasoning, the session states — in measurement language, not penalty language — what the attempt will still be able to prove:

- `H1`/`H2` → the attempt remains meaningful but is assisted; it does not produce independent mastery evidence, and a fresh independent check may still be needed.
- `H3`/`H4` → the attempt becomes practice-only and solution-exposed; the same item or near variant cannot serve as the independent recheck, and a fresh unseen item is required.

## 8.3 Mode conversion is explicit

When revealing help is taken on a measuring item, that item converts to learning within the same session. The session may continue teaching immediately. The conversion is shown, never silent, and never described as a violation.

## 8.4 The session does not schedule the recheck

Consistent with `TRUX-v0`, the session raises `requires_independent_recheck` for the affected Objective. Timing, form and target belong to the planner and the remediation/retention pipelines.

---

# 9. Pause, resume and slot recomposition

The `TRUX-v0` frame governs pause and resume. Assessment sessions are high-stakes by default, so 8D adds recomposition semantics.

Pause is not failure, not assistance and not a mastery signal.

On resume, an unresolved slot is **recomposed with a fresh item** when any of the following holds:

1. a solution or explanation was exposed for that slot,
2. the item version or validation status changed,
3. prerequisite state changed meaningfully,
4. the gap is long enough that item freshness is no longer trustworthy,
5. the user requested a reset or an alternative.

Completed valid evidence is never deleted by recomposition. A recomposed slot is presented as a fresh measurement, not as a retry of a failure.

---

# 10. Incomplete sessions

An incomplete session is a legitimate outcome, not an abandonment.

- Submitted valid attempts produce normal evidence.
- Unsubmitted items are not counted as incorrect.
- Unresolved slots produce no mastery penalty.
- The session may be reported as `partial`.
- Unresolved measurement needs remain in current state and may be re-approached later from a fresh plan.

Two incomplete weekly sessions do not become two owed exams. Missing a monthly cycle is not a failure and creates no exam debt.

---

# 11. Item disputes

A user must be able to report that an item was ambiguous, wrong or unanswerable, cheaply and without friction.

Binding rules:

- a report does **not** by itself set `item_invalid = true`,
- the affected evidence is held as **contested** pending revalidation,
- a critical mastery transition is not decided on a contested item alone,
- deterministic validators, answer keys and rubrics are rechecked,
- the item or template is flagged for QA,
- reporting is never treated as an excuse, a complaint or an undo button, and never affects the user's state negatively.

---

# 12. Evaluator status presentation

Evaluator status follows `GRE-v0`:

```text
verified | provisional | invalid
```

- **Verified** results may be presented as settled outcomes of that attempt.
- **Provisional** results must be visibly labelled provisional wherever they appear. They may inform and may open a confirmation need, but must never be rendered as a settled verdict or a state change.
- **Invalid** results produce neither credit nor penalty:

```text
invalid item -> no mastery penalty, no mastery credit
```

An invalid slot is not counted as covered. The user is told plainly that the question could not be evaluated reliably and that it was not used against their progress.

---

# 13. Result presentation

## 13.1 The result is semantic, not a grade

The in-session result answers **"what just changed?"**. Its content families are:

```text
confirmed_capabilities
verification_needed
persistent_targeted_gaps
retention_revalidated
not_reliably_measured
plan_changes
```

Forbidden as the result verdict:

- a pass/fail banner,
- a percentage or letter grade,
- a passing threshold,
- `8/10 = mastered` or `below 60 = failed` framing,
- a broad domain score such as `Python 72%`,
- a career-completion or readiness percentage,
- a comparison to other people or to a target curve.

## 13.2 Raw counts are informational only

The session may show how many responses were correct, incorrect or partial. This is orientation, explicitly separated from state, and must never be presented as the outcome of the assessment.

## 13.3 `not_reliably_measured` is first-class

Invalid items, provisional evaluations, assisted or solution-exposed attempts, contested items and unsubmitted slots all land in `not_reliably_measured`. This family is always shown when non-empty.

It is never silently omitted, and never folded into an incorrect or failed category.

## 13.4 State and plan consequences are stated truthfully

```text
attempt -> evidence validation -> GRE/RVR update
-> weakness/remediation/verification update -> PRG readiness
-> Topic derived state -> open LearningNeeds -> PBR-v0
-> remaining capacity -> new plan version -> PDT-v0 trace
```

The result may state a Skill state change **only if canonical state actually changed**. A first clean contradiction on an already-mastered Skill opens `verification_due` and is presented as such — never as instant unmastery, and never as a demotion event.

If nothing changed, the result says so plainly rather than manufacturing a progress claim.

## 13.5 Reasons are trace-derived

Any in-session explanation of why an assessment ran, why a slot was replaced or why the plan changed is a bounded projection of `PDT-v0` trace facts, including the assessment-specific reason codes. The full explanation lives on the shared `planner_explanation` surface.

---

# 14. Session result view versus `assessment_report`

| Surface | Owner | Question answered |
|---|---|---|
| In-session result view | 8D | What just changed, right now? |
| `assessment_report` | Progress / 8E | What has happened across sessions over time? |

The in-session view is immediate and bounded. Long-term history, cross-session trends and per-Skill reporting belong to Progress. Neither surface owns mastery, and the two must not present contradictory truths about the same attempt.

---

# 15. Technical English inside an assessment

- Task language and target construct remain separate; the instruction/scaffold mode is item metadata.
- Language demand that is not the measured construct is scaffolded or neutralised rather than left as a hidden gate.
- In dual-target items, component results stay separable; a single overall pass/fail broadcast across technical and English targets is forbidden.
- Technical ignorance must not masquerade as English failure, and weak English must not produce negative technical evidence.
- No general or official CEFR claim is made from an assessment session; qualified profile semantics remain `TEPM-v0`'s.

---

# 16. Degraded and recovery behavior

The `TRUX-v0` frame applies. Specific to assessment:

- **AI evaluator unavailable** → the attempt is preserved and marked `evaluation_pending`; no evidence is written, and it is neither pass nor fail.
- **Offline** → deterministically evaluable items remain fully runnable; items requiring remote evaluation park as pending rather than failing.
- **Interruption or crash** → recovery restores the last durable checkpoint and the frozen submitted boundaries only; unpersisted work is never claimed.
- **Data recovery required** → supersedes normal session work; silent progress reset is forbidden.

---

# 17. Assessment session semantic states

```text
entering_revalidating
blocked_not_startable
session_orientation
item_active
assistance_open
submitting_boundary
boundary_frozen
block_checkpoint
session_paused
resume_revalidating
slot_recomposed
evaluation_pending
result_ready
result_partial
item_contested
offline_local_capable
ai_unavailable_deterministic_core
error_recoverable
data_recovery_required
```

Each state must be distinguishable in text, not by color or motion alone.

---

# 18. Accessibility baseline

- The response affordance, submit action and exit/pause action all have textual accessible names.
- Independence mode and allowed-tools policy are conveyed in text before responding.
- Assistance level and its evidence consequence are conveyed in text.
- `evaluation_pending`, `item_contested`, `slot_recomposed` and recovery states are announceable.
- Block and boundary position is conveyed semantically, not by a decorative progress bar alone.
- Result families are readable as structured text, not only as a chart or color coding.
- No result meaning is carried by color alone.

---

# 19. Anti-patterns explicitly rejected

An assessment session must not become:

- a gradebook,
- a pass/fail verdict screen,
- a percentage or letter-grade report,
- a broad numeric domain-mastery dashboard,
- a countdown-timer pressure surface,
- a leaderboard or social comparison,
- an exam-streak or exam-quota tracker,
- a place where asking for help is visibly penalised,
- a place where skipping is treated as a wrong answer,
- a surface that presents a provisional evaluation as settled,
- a surface that hides what could not be reliably measured,
- an undo button for inconvenient results,
- a second mastery or retention engine,
- a duplicate of the Progress-owned `assessment_report`,
- a diagnostic runner competing with `task_runner_flow`.

---

# 20. 8D acceptance contract

8D can be accepted only if independent QA verifies at minimum:

1. Exactly one assessment session interior serves all three assessment scopes, with scope as displayed context only.
2. The atomic evidence boundary is the submission unit and is never split or partially scored.
3. Submitted boundaries are frozen and cannot be revisited, edited or resubmitted.
4. Unsubmitted boundaries within an open block remain navigable.
5. Skipping is not incorrect, not negative evidence and not a penalty.
6. Independence mode and allowed-tools policy are disclosed before responding, and objective-appropriate tool use does not break H0.
7. Assistance is never blocked; its evidence consequence is disclosed in measurement language and mode conversion is explicit.
8. The session raises `requires_independent_recheck` but never schedules it.
9. Pause is not failure, assistance or a mastery signal, and resume recomposes unresolved slots under the five declared conditions without deleting valid evidence.
10. Incomplete sessions produce partial results, no penalty and no exam debt.
11. Item disputes hold evidence as contested without auto-invalidating it, and never harm the user's state.
12. Provisional evaluations are visibly provisional and cannot settle a critical transition; invalid items give neither credit nor penalty.
13. The result surface is semantic, with no pass/fail banner, grade, threshold or broad numeric mastery.
14. Raw counts, if shown, are explicitly informational and separated from state.
15. `not_reliably_measured` is first-class and always shown when non-empty.
16. A state-change claim appears only when canonical state actually changed; a first contradiction on a mastered Skill opens `verification_due` rather than unmastery.
17. In-session reasons are bounded projections of PDT-v0 trace facts.
18. The in-session result view and the Progress-owned `assessment_report` have distinct, non-contradictory roles.
19. Technical English construct separation and dual-target component separability are preserved.
20. The TRUX-v0 shared focused-flow frame is inherited rather than redefined, and diagnostics remain `task_runner_flow` work.
21. Degraded modes preserve deterministic core capability and never fake a result.
22. 8E/8F/8G/9/10 implementation boundaries remain open.
23. Stage 4, Stage 6, Stage 7, 8A, 8B and 8C accepted contracts still validate.

---

# 21. Handoff after acceptance

If accepted, 8D becomes `ASUX-v0 / D-071`.

Next numbered step:

**8E — Skill/progress/weakness UX**

8E will define the Progress information domain: exact Skill/Topic state labels, weakness and remediation presentation, the Technical English profile surface, learning history and the `assessment_report` this session hands off to. It must receive a fresh PRE-STEP and explicit user approval before execution.
