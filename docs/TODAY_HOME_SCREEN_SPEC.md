# Today / Home Screen Specification — THUX-v0

**Stage step:** 8B — Ana ekran  
**Status:** ACCEPTED — independent 8B QA PASS  
**Decision:** `D-069`  
**Model:** `THUX-v0 — Today Home UX`  
**Parent IA:** `UXIA-v0 / D-068`

## 1. Purpose

8B defines the **semantic content hierarchy and user-facing behavior of the `Today` / home overview**. It answers one primary question:

> **Bugün şimdi ne yapmalıyım?**

The Today surface must reduce planning burden without turning the planner into an opaque black box. It presents the current adaptive plan, the next valid action, the current-day capacity context, concise trace-backed reasons and only the attention state required to understand or act on today’s plan.

Primary invariant:

> **Today is a projection of canonical planner/state truth. It never becomes a second planner, mastery engine, prerequisite engine, assessment gradebook or English quota system.**

---

## 2. Binding inputs

8B consumes and preserves:

- `UXIA-v0 / D-068` — `today` owns current plan, next task, current capacity context, attention items and planner-reason entry.
- D-001/D-002/D-009/D-010 — no elapsed-time/course-completion/streak-as-mastery; Today is simple and action-first.
- `GRE-v0` — task completion is not mastery.
- `RVR-v0` — review due is not forgetting.
- D-033 capacity contract — user-selected daily capacity is a hard budget.
- D-034 task taxonomy — LearningNeed, TaskCandidate, PlannedTask, Attempt/Artifact and Evidence remain distinct.
- `PBR-v0` — semantic planner priority and deterministic rank remain canonical.
- `PRG-v0` — blocked/conditional/ready behavior is Skill-prerequisite state, not list order.
- `PDT-v0` — every user-facing planner reason is derived from structured trace facts.
- `SRR-v0` — missed work is not debt; old daily plans are not replayed as backlog.
- `DMA/WBA/MCA` — assessment appears only when selected/meaningful; it is evidence workflow, not gradebook truth.
- `DECP-v0` — Technical English shares common capacity; no daily English quota, streak or debt.
- `TEIP-v0` — task language and target construct remain separate.
- V1 local-first Android scope and AI-degraded deterministic-core requirement.

8B introduces no new planner priority, mastery threshold, prerequisite edge, fixed category allocation, scientific duration target or English quota.

---

# 3. Scope boundary

## 3.1 8B decides

- Today overview semantic content hierarchy,
- primary next-action / resume precedence,
- capacity and plan-summary semantics,
- remaining-plan queue semantics,
- concise task-row information contract,
- planner-reason snippet behavior,
- attention / plan-change presentation semantics,
- Today handling of assessment and Technical English tasks,
- Today loading/empty/offline/AI-degraded/recovery states,
- behavior after capacity changes and replans,
- semantic responsive hierarchy constraints,
- explicit handoff boundaries to 8C–8G and implementation stages.

## 3.2 8B does not decide

- exact task-runner step choreography, pause mechanics or teaching interaction → 8C,
- exact assessment question/session/result interaction → 8D,
- final Skill/progress/weakness labels and visualization → 8E,
- typography, color, spacing tokens, card appearance, iconography, motion or final component library → 8F,
- final wireframes / prototype geometry → 8G,
- navigation framework or UI technology → 9A/10,
- physical persistence schema → 9C,
- runtime planner/mastery implementation → 12–14,
- final notification design → 16,
- final accessibility/usability calibration → 17–18.

No fixed pixel dimensions, fixed card count or device-specific geometry is canonical in 8B.

---

# 4. Today content hierarchy

The hierarchy is semantic, not a requirement that every region become a separate card.

```text
1. primary_action
2. day_plan_context
3. remaining_plan
4. attention_context            (conditional)
5. supporting_navigation        (progressive disclosure)
```

The first meaningful content must make the next valid action obvious. Supporting status cannot visually outrank the action the planner selected.

## 4.1 `primary_action`

The highest-emphasis Today region.

It communicates:
- what action can be taken now,
- whether the user is starting or resuming,
- the target Topic/Skill context necessary to orient the user,
- the task’s primary purpose and optional track context,
- estimated duration as an estimate,
- one trace-backed primary reason and, when useful, one secondary reason,
- a clear start/resume action,
- entry to `planner_explanation`.

It does **not** communicate:
- `mastery +X`,
- course/career completion percentage,
- streak pressure,
- a fake urgency score,
- raw PBR rank-vector internals,
- an LLM-invented reason.

## 4.2 `day_plan_context`

A compact context for the current plan, subordinate to the primary action.

It may communicate:
- resolved daily hard capacity,
- current-day capacity override if one exists,
- planner’s estimated total planned work,
- estimated remaining planned work,
- whether the plan has been recalculated after a state/capacity change.

Rules:
- capacity is a time budget, not progress/mastery,
- task durations and planned totals are estimates, not guaranteed countdowns,
- planner reserve heuristics do not need to be exposed as a fake precision metric,
- no fixed category percentages are shown or implied,
- persistent capacity presets/settings remain owned by Profile,
- Today may provide a current-day capacity override entry because D-033 says an explicit today override causes immediate replan.

## 4.3 `remaining_plan`

Shows the currently selected **PlannedTask** sequence that remains relevant after the primary action.

Rules:
- it is not a backlog of old TaskCandidates,
- it is not a list of every open LearningNeed,
- it is not a debt list,
- planner order is not curriculum prerequisite order,
- deferred/omitted work is not shown as failed or overdue debt merely because it did not fit today,
- blocked dependent work is never presented as a startable action,
- if a replan invalidates a previously selected task, Today reflects the new plan rather than preserving the stale row.

Completed activities may receive a low-emphasis current-session acknowledgement, but the main queue remains about **what is still actionable now**. Durable history belongs to Progress.

## 4.4 `attention_context`

Conditional context used only when the user needs to understand an important plan/state consequence.

Allowed attention families include:
- `plan_changed`,
- `verification_attention`,
- `remediation_attention`,
- `prerequisite_blocker`,
- `retention_attention`,
- `assessment_attention`,
- `recovery_attention`.

Attention does not create planner priority. It explains or links to already-canonical state.

If an attention item is already represented by the selected primary task, the overview should avoid duplicating it as a second competing call to action.

## 4.5 `supporting_navigation`

Low-emphasis contextual links may lead to:
- `planner_explanation`,
- `skill_detail`,
- `topic_detail`,
- Progress-owned report/history/state,
- Profile-owned persistent capacity settings.

The user must not be forced to inspect these details before starting a valid next action.

---

# 5. Primary-action precedence

Today must choose one dominant action state deterministically from already-canonical runtime state.

Semantic precedence:

```text
1. data_recovery_required
2. valid_resumable_focused_session
3. selected_next_planned_task
4. plan_loading_or_replanning
5. valid_empty_or_capacity_limited_state
6. recoverable_error
```

Clarifications:

- `data_recovery_required` can supersede normal work because continuing with uncertain local state may risk data integrity.
- A valid resumable task or assessment session is surfaced before starting unrelated new work. Exact session choreography remains 8C/8D.
- Resume eligibility must be revalidated; a stale paused session cannot bypass current prerequisite/state rules.
- When there is a selected next PlannedTask, that task is the dominant start action.
- Loading/replanning must not expose a stale task as if it were current.
- Empty/capacity-limited is a legitimate state, not failure.
- `ai_unavailable_core_available` does **not** supersede a valid deterministic local plan.
- Offline mode does not supersede the plan when required local data/actions are available.

---

# 6. Planned-task row contract

A Today task row is a user-facing projection of a selected PlannedTask; it is not a new task identity.

At minimum it can represent:

```text
TodayTaskRow
- planned_task_ref
- display_title
- primary_purpose
- activity_kind?              # expose only when useful
- curriculum_track_context?
- topic_ref?
- target_skill_refs[]
- estimated_minutes?
- disposition: current | upcoming | paused
- reason_summary_ref
- integration_mode?           # when relevant to understanding the task
```

## 6.1 Purpose, activity and track must remain separate

Examples:

```text
primary_purpose = retain
activity_kind = code_reading_trace
curriculum_track = python
```

or:

```text
primary_purpose = practice
activity_kind = language_activity
curriculum_track = technical_english
```

The UI may use simpler human labels, but it must not turn `English`, `coding` or `quiz` into the canonical reason the task exists.

## 6.2 Estimated duration

Duration is planning context.

- It may help the user understand whether the work fits the day.
- It is not a mastery multiplier.
- Finishing faster/slower does not itself change mastery.
- The UI must not imply exact guaranteed completion time.

## 6.3 State labels

Today may use only state needed for action/orientation. Final Skill-state visual semantics belong to 8E.

A task row cannot infer `mastered`, `failed` or `weak` merely from local task completion.

---

# 7. Planner reason presentation

Every task shown on Today must remain explainable.

## 7.1 Default snippet

The overview shows:
- **one primary reason**, and
- **at most one supporting reason when it materially improves understanding**.

Examples of semantic reason families:
- continue current learning,
- repair a confirmed weakness,
- verify uncertain prerequisite/state,
- review due knowledge,
- collect missing evidence,
- parallel Technical English progress,
- fit the available capacity,
- resume valid paused work.

These are UI semantics, not permission to invent reason codes.

## 7.2 Source rule

```text
today_reason_summary ⊆ PDT-v0 PlannerDecisionTrace facts
```

The full explanation opens shared `planner_explanation`.

Forbidden:
- free-form AI justification as canonical reason,
- hidden weighted score presented as truth,
- saying `you forgot this` merely because RVR says `review_due`,
- saying `you failed` merely because work was deferred by capacity.

## 7.3 Plan-level reason

When a meaningful replan occurs, Today may present a concise `plan_changed` explanation derived from the replan trace, such as a capacity change, new valid evidence, prerequisite repair or assessment result.

Exact animation/toast/banner treatment is deferred.

---

# 8. Capacity interaction on Today

Today owns **current-day capacity context**, while Profile owns persistent capacity preferences.

## 8.1 Current-day override

A user can reach a semantic action equivalent to:

```text
change_today_capacity
```

If a valid explicit override is supplied:

```text
today_explicit_override
→ planner replan
→ Today refreshes from new plan
→ optional trace-backed plan_changed explanation
```

No task becomes debt because it leaves the new smaller plan.

## 8.2 Capacity increase

Increasing today’s capacity can allow the planner to select more eligible work, but Today does not promise a fixed number of extra tasks.

## 8.3 Capacity decrease

Decreasing capacity can remove lower-priority work. Removed work is not marked failed, missed or overdue solely due to the change.

## 8.4 Zero / very small capacity

Today must distinguish:
- capacity = 0,
- capacity below normal plannable block but a safe micro-task exists,
- capacity too small and no valid micro-task exists.

None is a learning failure.

---

# 9. Assessment on Today

Assessment remains contextual.

A selected assessment can appear as:
- the primary action,
- an upcoming planned task,
- an attention item only when user awareness is useful.

Rules:
- no permanent `Exam` section is required on Home,
- no daily assessment quota is shown,
- a question-like task is not automatically labeled `assessment`; canonical `primary_purpose` wins,
- assessment score cannot be shown as direct broad mastery truth,
- a completed assessment may produce a trace-backed plan-change note and link to Progress-owned report/history,
- exact session/result UI is 8D.

---

# 10. Technical English on Today

Technical English is visible only through selected/integrated daily work and relevant context.

Rules:
- no separate English daily budget,
- no `English completed today` obligation,
- no English streak,
- no guilt/debt state when an eligible English candidate was not selected because higher-priority work/capacity won,
- `technical_english` remains track context, not `primary_purpose`,
- integrated tasks preserve TEIP-v0 technical/English target separation,
- Home never presents a general/official CEFR claim.

Qualified English profile remains under Progress.

---

# 11. Task completion and return-to-Today behavior

After a focused task/assessment returns to Today:

1. canonical Attempt/Artifact/evidence processing occurs,
2. mastery/retention/prerequisite/remediation state may or may not change,
3. planner may replan,
4. Today renders the current plan, not the stale pre-attempt plan.

Important:

```text
task_completed != mastery_confirmed
```

Today may acknowledge activity completion, but it may only claim a Skill state change if canonical state actually changed and the wording remains compatible with later 8E semantics.

If no mastery change occurred, the UI must not manufacture a progress claim for motivational effect.

---

# 12. Missed day / return after absence

Today obeys SRR-v0:

- yesterday’s unfinished plan is not replayed as a debt list,
- absence is not failure,
- Today starts from a fresh current-state plan,
- if retention/verification needs became relevant they appear because current canonical state requires them, not because a calendar task was missed.

Forbidden Home framing:
- `3 tasks overdue from yesterday`,
- `catch up your missed day`,
- streak-loss punishment.

---

# 13. Today semantic states

Today must explicitly support the following overview states.

## `loading_initial_plan`
No current plan is yet safe to show.

## `replanning`
A prior plan is being replaced because state/capacity changed. Stale actions are not presented as current.

## `ready_plan`
At least one valid selected PlannedTask exists.

## `resumable_session`
A paused focused session exists and is still valid after revalidation.

## `empty_no_open_need`
There is currently no open need requiring a Today task. This is not equivalent to professional readiness.

## `empty_no_eligible_task`
Open needs may exist, but none currently has a valid/eligible task. The UI may expose a trace-backed explanation or recovery/detail path without pretending the learner is done.

## `capacity_zero`
Today has no study budget because the resolved current-day capacity is zero.

## `capacity_too_small_no_candidate`
Current capacity cannot fit a valid safe candidate. No failure/debt is created.

## `offline_local_available`
Local canonical data and supported local actions remain usable; network absence is contextual.

## `ai_unavailable_core_available`
AI assistance is unavailable, but deterministic core plan/state/navigation remains usable.

## `error_recoverable`
A recoverable operational failure prevents the intended Today projection; safe retry/recovery is offered.

## `data_recovery_required`
Data integrity/recovery work is required before normal plan use is safe.

---

# 14. Empty-state truthfulness

Empty states must say **why Today has no action**, without converting absence of a task into a mastery claim.

Required distinctions:

```text
no_open_need
!= no_eligible_task
!= no_capacity
!= capacity_too_small
!= loading
!= recoverable_error
```

Forbidden implication:

```text
no task today -> all skills mastered / curriculum complete / professional ready
```

---

# 15. Offline and AI-degraded behavior

## 15.1 Offline

If the local-first core has enough data to show/execute supported work, Today remains usable and shows a non-blocking offline context.

A task that genuinely requires unavailable network resources may be revalidated/replanned; the whole Home must not become unusable solely because the device is offline.

## 15.2 AI unavailable

AI Tutor failure does not hide Today, remove deterministic tasks or invalidate canonical state.

If a selected task genuinely requires an unavailable evaluator/provider and no safe fallback exists, that candidate is handled by runtime eligibility/replan rules; AI failure does not grant the UI authority to improvise a replacement.

---

# 16. Progressive disclosure and information density

Today is an **overview/action surface**, not a dashboard of every engine state.

Default overview should not dump:
- all open LearningNeeds,
- full prerequisite graph,
- raw evidence history,
- raw PBR vectors,
- full planner trace,
- every weak Skill,
- entire curriculum hierarchy,
- Technical English profile,
- assessment history.

Those remain available through shared detail / Learn / Progress.

Long current plans may use progressive disclosure, scrolling or grouping later; 8B does not lock a fixed number of visible rows.

---

# 17. Semantic responsive behavior

Across compact and expanded layouts:

- `primary_action` remains the highest-priority content,
- current capacity/plan context remains associated with Today,
- the remaining plan remains understandable as one planner-produced sequence,
- attention does not overtake the primary action unless it represents a true recovery/integrity blocker,
- responsive rendering may place supporting context beside the main sequence, but it cannot change truth ownership or action precedence.

Exact geometry is 8F/8G/10 territory.

---

# 18. Accessibility baseline inherited by Today

Before final 17D calibration, 8B already requires semantic behavior that can be implemented accessibly:

- the dominant action has a meaningful accessible name independent of icon/color,
- task purpose/critical state is not conveyed by color alone,
- reason/attention entries have textual semantics,
- loading/replanning/recovery state changes can be announced without relying solely on animation,
- order remains understandable under linear screen-reader traversal,
- estimated duration is not the only label distinguishing tasks.

Exact touch targets, typography and final accessibility QA remain 8F/17.

---

# 19. Anti-patterns explicitly rejected

Today must not become:

- a streak dashboard,
- a career-completion dashboard,
- a calendar backlog/debt tracker,
- a fixed `new / review / English / quiz` percentage dashboard,
- an assessment gradebook,
- a broad `Python 72%` mastery dashboard,
- a daily English quota tracker,
- an AI chat home screen,
- a raw planner-debug console,
- a permanently blocked-task list,
- a duplicate Progress screen.

---

# 20. 8B acceptance contract

8B can be accepted only if independent QA verifies at minimum:

1. Today remains the normal action-first projection of UXIA-v0.
2. Primary action is deterministically chosen from valid recovery/resume/plan/empty state.
3. Only selected/current PlannedTasks are actionable; candidate/backlog/debt semantics do not leak into the queue.
4. Daily capacity is a hard-budget context, not progress; current-day override triggers replan.
5. Task purpose/activity/track remain semantically separable.
6. Task completion does not imply mastery.
7. Reason snippets are PDT-v0 trace-derived and bounded on overview.
8. Blocked work is never presented as startable.
9. Assessment remains contextual and score does not own mastery.
10. Technical English has no fixed daily quota/streak/debt and remains common-capacity track context.
11. Missed days do not create backlog/debt.
12. Empty states distinguish no need / no eligible task / no capacity / too-small capacity / loading / error.
13. Offline and AI-degraded modes preserve deterministic core when local capability exists.
14. Recovery state can supersede unsafe normal work.
15. Home does not expose career %, broad numeric mastery, general CEFR or streak as success.
16. 8C/8D/8E/8F/8G/9/10 implementation boundaries remain open.
17. Stage 6, Stage 7 and 8A accepted contracts still validate.

---

# 21. Handoff after acceptance

If accepted, 8B becomes `THUX-v0 / D-069`.

Next numbered step:

**8C — Günlük çalışma akışı**

8C will design task-runner / daily-session interaction choreography using this Home entry/return contract. It must receive a fresh PRE-STEP and explicit user approval before execution.
