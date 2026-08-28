# 8B Today / Home UX — Research & Contract Synthesis

**Stage step:** 8B — Ana ekran  
**Purpose:** Translate accepted product/planner/IA contracts into a Today/Home content hierarchy without inventing a second source of truth.

## 1. Research-need decision

8B does **not** introduce a new external algorithm, learning-science rule, technology choice or platform-specific implementation. Its main problem is internal contract synthesis:

```text
accepted product goal
+ UXIA-v0 Today ownership
+ accepted planner/capacity/prerequisite/explainability contracts
+ accepted assessment/English behavior
→ Today overview semantics
```

A new separate external Research AI is **not required** for acceptance. Existing 8A external UX research remains provenance for the already-accepted action-first semantic IA and progressive-disclosure direction. 8B must not reinterpret those sources to justify fixed card counts, pixel geometry, exact typography or a new navigation component.

Independent QA remains required because 8B can still accidentally violate planner/mastery semantics even without new external research.

## 2. Canonical source set reviewed

### Product / V1
- `docs/PRODUCT_REQUIREMENTS.md`
- `docs/V1_SCOPE.md`

Key constraint: opening the app should answer `Bugün ne yapmalıyım?`; Today must show daily time context, next task, task list, current topic/skill, relevant state and why the task was selected without treating time or completion as mastery.

### Parent IA
- `docs/INFORMATION_ARCHITECTURE_SPEC.md`
- `ux/8a_information_architecture/ia.yaml`

Key constraint: `today` owns current plan, next task, current capacity context, attention items and planner-reason entry. Assessment, Technical English, AI and remediation are contextual; exact Skill state/history have other primary homes.

### Planner / state
- `docs/ADAPTIVE_PLANNER_SPEC.md`
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/PRIORITY_POLICY_SPEC.md`
- `docs/PREREQUISITE_POLICY_SPEC.md`
- `docs/MISSED_DAY_RECOVERY_SPEC.md`
- `docs/PLANNER_EXPLAINABILITY_SPEC.md`

Key constraints:
- daily capacity is a hard budget, not progress;
- LearningNeed / TaskCandidate / PlannedTask / Attempt / Evidence are different entities;
- eligibility precedes priority;
- blocked work cannot become actionable because UI wants to show it;
- deferred work and missed days are not debt/failure;
- user-facing reasons are a bounded projection of PDT-v0 trace facts, not AI reasoning.

### Assessment
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
- accepted WBA/MCA/QAB/AIV contracts via UXIA-v0

Key constraint: daily assessment is not a quota. A question-like UI does not determine canonical purpose, and scores do not directly own broad mastery.

### Technical English
- `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`
- `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`
- accepted TEPM-v0 profile contract via UXIA-v0

Key constraints:
- Technical English shares the common capacity;
- no fixed daily English minutes/percentage/streak/debt;
- track, activity and primary purpose remain separate;
- integrated task technical/English attribution remains component-specific;
- qualified profile belongs under Progress, not Today.

## 3. Derived design conclusions

### 3.1 The next valid action must dominate

The Today question is operational, not analytical. Therefore the screen hierarchy should make the current valid start/resume action easiest to find before broader status detail.

This is a product-contract derivation, not a claim that one physical card design is universally optimal.

### 3.2 Capacity belongs near the plan but must not look like mastery

The user needs to understand how much time was accepted for today and how much work the planner estimated. However a circular `% complete` treatment would easily imply learning progress. 8B therefore defines semantic capacity/plan context but leaves final visual treatment to 8F/8G.

### 3.3 The queue should represent the current plan, not the entire need universe

Showing every open need/candidate would expose planner internals, create decision burden and make deferred work look like debt. The overview therefore projects selected PlannedTasks only. Deeper state remains under Progress/Skill detail/planner explanation.

### 3.4 Reasons need progressive disclosure

PDT-v0 explicitly distinguishes bounded user-facing explanation from detailed audit trace. Today should therefore show one primary reason and at most one useful supporting reason, with full trace-derived explanation on the shared `planner_explanation` surface.

### 3.5 Attention is explanatory, not a parallel priority engine

Verification/remediation/blocked/review/assessment information can matter today, but the UI must not rescore it. Attention should explain selected work or an important consequence and link to canonical detail.

### 3.6 Assessment and English should look like work in the plan, not daily quotas

A selected assessment or English task can be the next action or an upcoming row. They should retain their canonical purpose/track semantics rather than becoming permanent Home modules such as `Daily Quiz` or `English 10 min`.

### 3.7 Empty states need cause-specific semantics

`No open need`, `open needs but no eligible task`, `no capacity`, `capacity too small`, `loading` and `error` have different product meanings. Collapsing them into `Nothing to do` would either hide a blocker or overclaim completion.

### 3.8 Offline / AI unavailable should not erase deterministic local value

V1 is local-first and AI is not the canonical core. Home therefore remains available when local state supports it; provider/network limitations become contextual unless a specific task truly cannot execute and must be replanned.

## 4. Decisions deliberately deferred

8B does not choose:
- card vs list vs pane implementation,
- exact number of visible tasks before expansion,
- typography/color/icon system,
- exact CTA copy,
- animation/banner timing,
- exact task-runner or assessment flow controls,
- physical responsive breakpoints,
- mobile framework/navigation library.

Those belong to 8C–10/17–18 as already indexed.

## 5. QA risk list

Independent 8B QA should actively look for:
- candidate/backlog rows leaking into Today,
- blocked work accidentally startable,
- task completion rendered as mastery,
- capacity rendered as learning progress,
- English quota/streak/debt leakage,
- assessment score owning mastery,
- reasons not trace-backed,
- stale plan exposed during replan,
- missed-day debt wording,
- `no task` overclaimed as curriculum/readiness completion,
- AI/network failure unnecessarily blocking local core,
- visual/component decisions accidentally locked before 8F/8G/10.

## 6. Confidence

**High** for semantic Today/Home hierarchy because it is constrained by already accepted product/planner/IA contracts.  
**Not yet validated** for empirical usability or final visual ergonomics; those remain 8G/17/18 responsibilities.
