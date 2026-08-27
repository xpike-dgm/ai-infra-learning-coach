# UX Information Architecture Specification — UXIA-v0

**Stage step:** 8A — Bilgi mimarisi  
**Status:** CANDIDATE — independent 8A QA pending  
**Candidate decision:** `D-068`  
**Model:** `UXIA-v0 — Adaptive Learning Information Architecture`  
**Research:** `research/8a_information_architecture_research.md`

## 1. Purpose

8A defines **where product information lives, how users move between semantic destinations, which information belongs to overview vs detail vs focused workflows, and which canonical engine owns the truth behind each surface**.

It does **not** draw final screens, choose typography/colors, code navigation, or specify exact interaction choreography.

The IA must make the product’s core loop understandable:

```text
open app
→ know what to do now
→ perform learning / assessment work
→ understand why it was selected
→ inspect exact capability state when needed
→ return to an updated adaptive plan
```

Primary invariant:

> **The UI may summarize canonical learning state, but it must never create a second source of truth for mastery, prerequisite, retention, remediation, planner or Technical English state.**

---

## 2. Binding inputs

8A must preserve:

- D-001/D-002/D-009/D-010 — no time/streak/course-completion-as-progress; modern simple UI; Today answers “Bugün ne yapmalıyım?”.
- GRE-v0 — mastery comes from valid evidence, not UI completion.
- RVR-v0 — `review_due` is not automatic forgetting/demotion.
- Task taxonomy — LearningNeed / TaskCandidate / PlannedTask / Attempt / Evidence are distinct.
- PBR-v0/PRG-v0 — planner/prerequisite semantics remain canonical.
- PDT-v0 — user-facing “why?” must come from structured decision trace.
- DMA/WBA/MCA/QAB/AIV — assessments are evidence workflows, not a detached gradebook truth.
- KGC/FDM/SDM/GIM/PEM/WLRM — browse organization is not runtime prerequisite truth; exact Skill/Objective state remains traceable.
- EED/TECP/DECP/TEIP/TEPM — Technical English is parallel/integrated; qualified profile only; no general CEFR overclaim.
- V1 local-first, single-user, Android-focused scope.

---

## 3. 8A scope boundaries

### 8A decides

- semantic top-level destinations,
- start destination,
- primary information ownership,
- shared detail route families,
- focused flow families,
- first-run/setup IA boundary,
- semantic route reachability and back/up expectations,
- where planner explanations, assessment reports, Skill state, English profile and settings live,
- cross-cutting loading/empty/offline/degraded state ownership,
- semantic adaptive-navigation rule across compact/expanded windows,
- explicit boundaries for 8B–8G and later implementation.

### 8A does not decide

- exact Today card layout — 8B,
- step-by-step Task Runner choreography — 8C,
- exact assessment interaction/result layouts — 8D,
- final Skill/progress/weakness visual presentation/copy — 8E,
- typography/color/spacing/components — 8F,
- wireframes/prototype — 8G,
- Compose/React Native/navigation library — 9A/10,
- physical persistence schema — 9C,
- runtime implementation — 10–14,
- final analytics features — 16,
- final accessibility/polish testing — 17,
- empirical usability calibration — 18.

---

# 4. User jobs that drive the IA

The app must make these jobs obvious without exposing engine internals as navigation categories:

1. **Act now:** What should I do today, and how much fits my current capacity?
2. **Understand why:** Why did this task/review/remediation/assessment appear?
3. **Browse the path:** What am I learning, what is available, and what depends on what?
4. **Inspect capability state:** Which exact Skills are confirmed, developing, due, uncertain or needing remediation?
5. **Inspect history/evidence context:** What changed after learning/assessment/review?
6. **Understand Technical English state:** What is my qualified Technical English base profile and exact uneven Skill detail?
7. **Manage personal operation:** Capacity, reminders, theme/accessibility preferences, backup/restore and data controls.

These jobs, not internal service/module names, define the navigation architecture.

---

# 5. Primary application shell

## 5.1 Exactly four semantic top-level destinations

Compact-phone semantic order is fixed:

```text
Today → Learn → Progress → Profile
```

Canonical IDs:

```text
today
learn
progress
profile
```

`today` is the normal authenticated/local-profile start destination.

The concrete Android navigation component is **not** locked in 8A. On compact windows the semantic set is compatible with a bottom navigation bar; on expanded windows it may be rendered as a rail/panel later. Window adaptation must not change destination meaning or order.

### Stable semantic ownership

| Destination | Primary user question | Owns |
|---|---|---|
| `today` | What should I do now? | current adaptive plan, next action, current capacity context, attention items, contextual planner reason entry |
| `learn` | What is the learning map and where can I go? | curriculum browse, route/domain/module/topic organization, availability/locked context, entity discovery |
| `progress` | What have I actually demonstrated and what needs attention? | evidence-backed capability summaries, weakness/review/verification/remediation views, history, assessment reports, Technical English profile |
| `profile` | How should this personal app operate for me? | capacity/preferences, reminders, theme/accessibility preferences, data backup/export/restore controls, later provider settings |

Top-level destinations are peers. `Progress` is not a child of `Learn`; `Profile` is not a dumping ground for learning state.

---

# 6. Things that are deliberately NOT top-level destinations

## 6.1 Assessment

Assessment is a **workflow/evidence source**.

- due/selected assessment appears contextually in `Today`,
- completed assessment reports/history live under `Progress`,
- active assessment uses a focused flow,
- assessment results cannot directly become broad mastery/progress truth.

No permanent `Exams`/`Assessment` bottom-nav destination in UXIA-v0.

## 6.2 Technical English

English is a parallel/integrated curriculum track.

- daily English tasks appear in `Today`,
- English capabilities are browsable through `Learn`,
- qualified Technical English profile and exact state live under `Progress`,
- English is not a separate product shell.

No permanent `English` top-level destination.

## 6.3 AI Tutor

AI Tutor is contextual assistance/evaluation.

- invoked from task/detail contexts,
- cannot own planner/mastery truth,
- AI service failure cannot block the primary shell.

No permanent `AI Chat` top-level destination in V1 IA.

## 6.4 Retention / Remediation / Weakness

These are learning states/needs, not independent content silos.

- attention appears in `Today`,
- detailed state/history appears in `Progress`,
- related curriculum/entity context is reachable through shared detail routes.

---

# 7. Surface families

A **surface family** is semantic IA, not a final screen count. 8B–8G may split or combine visual screens if semantic ownership remains intact.

## 7.1 Shell roots

1. `today_overview`
2. `learn_overview`
3. `progress_overview`
4. `profile_overview`

These are the only primary-shell roots.

## 7.2 Shared entity/detail surfaces

### `topic_detail`
Reachable from Learn, Today context and Progress context.

Shows derived Topic orchestration state and its Skill structure. It does not create mastery or prerequisite truth.

### `skill_detail`
Canonical shared Skill detail surface, reachable from Today/Learn/Progress.

It is the primary place for exact Skill-level explanation such as:
- current derived presentation state,
- prerequisite/blocked context,
- review/verification/remediation context,
- evidence summary/history links,
- related Topic placements.

The same Skill must not have separate contradictory “Learn Skill page” and “Progress Skill page” truths.

### `planner_explanation`
Contextual detail for “Why this task?”, “Why not today?”, “Why blocked?”, or “Why did the plan change?”.

Content must be a projection of PDT-v0 reason codes/trace facts. It is not free-form AI reasoning.

### `assessment_report`
Historical/reporting surface under the Progress information domain. It can explain observed results and state/planner consequences but cannot behave as an independent score-based mastery authority.

### `technical_english_profile`
Detailed TEPM-v0 surface under Progress. It presents qualified Technical English A1/A2/B1 base profile + exact uneven Skill states + bounded B2+ per-capability extension evidence. It must not claim general/official CEFR certification.

### `learning_history`
Progress-owned chronology of meaningful learning/assessment/review/remediation events. It is not a streak calendar and does not treat attendance as mastery.

## 7.3 Focused workflow surfaces

### `task_runner_flow`
Focused learning/practice/remediation/retention/diagnostic work entered primarily from Today or contextual entity actions.

Primary shell navigation may be visually suppressed during focused work, but the flow must preserve a safe pause/exit/resume path. Exact choreography belongs to 8C.

### `assessment_session_flow`
Focused daily/weekly/monthly assessment execution. Exact question navigation, pause/resume and submission behavior belong to 8D.

## 7.4 First-run/setup family

### `initial_setup_flow`
Pre-shell setup for local personal operation such as initial daily capacity/preferences and introduction to diagnostic opportunities.

No mandatory diagnostic completion may be invented by IA. Diagnostic execution remains governed by VDW/EED and planner eligibility.

After minimum required local setup, normal entry is `today`.

---

# 8. Information ownership map

Every major information object has one primary UI home, even if contextual summaries/links appear elsewhere.

| Information object | Primary home | Contextual exposure allowed |
|---|---|---|
| Current plan / next task | Today | task runner, notifications |
| Current capacity | Today summary; Profile setting | replan/settings |
| Planner reason | Planner Explanation | Today/task/context snippets |
| Curriculum hierarchy | Learn | entity breadcrumbs/context |
| Topic availability/derived state | Learn / Topic Detail | Today/Progress summary |
| Exact Skill state | Skill Detail / Progress | Today/Learn summary |
| Weakness/remediation set | Progress | Today attention |
| Review/verification due | Progress | Today attention |
| Assessment due opportunity | Today | Progress history after completion |
| Assessment report/history | Progress | contextual links |
| Technical English profile | Progress | Today/Learn exact task/entity context |
| Learning history | Progress | Skill/assessment links |
| Daily capacity preference | Profile | Today current-day context |
| Reminder settings | Profile | notification entry points |
| Theme/accessibility preferences | Profile | global application |
| Backup/export/restore | Profile | recovery/degraded flows |
| AI assistance availability | contextual | task/detail/profile later; never mastery authority |

Duplication rule:

> A summary may repeat facts, but state mutation/semantic ownership must remain singular and traceable to canonical engine/data owners.

---

# 9. Navigation graph

## 9.1 Shell reachability

From any shell root, the user can switch directly to any other shell root.

```text
Today <-> Learn <-> Progress <-> Profile
```

This notation means peer switching, not a linear path.

## 9.2 Shared-detail reachability

```text
Today -> task_runner_flow
Today -> planner_explanation
Today -> skill_detail / topic_detail
Today -> assessment_session_flow when eligible/selected

Learn -> topic_detail -> skill_detail
Learn -> skill_detail

Progress -> skill_detail
Progress -> topic_detail
Progress -> technical_english_profile
Progress -> assessment_report
Progress -> learning_history

Profile -> settings/data-control details
```

Shared detail routes preserve origin context for Back/Up behavior; they do not clone the underlying entity.

## 9.3 Focus-flow return rule

A completed/paused focused flow returns to a deterministic relevant destination:
- normal daily work → Today,
- assessment completed from Today → Today with result/state-change entry; report remains under Progress,
- a focused flow opened from an entity context may return to that context if no replan invalidates it.

Exact runtime back-stack implementation is deferred, but the semantic return target must not strand the user in an unrelated section.

---

# 10. Hierarchy and progressive disclosure

## 10.1 Default overview rule

Overview surfaces show only information required for the destination’s primary user question.

Advanced detail is revealed through entity/detail/history/explanation surfaces.

Examples:
- Today shows a concise task reason; full decision detail opens `planner_explanation`.
- Progress shows attention summaries; exact Skill evidence opens `skill_detail`/history.
- Learn shows curriculum structure; evidence details do not flood the browse map.

## 10.2 Browse hierarchy is not prerequisite hierarchy

`Domain → Module → Topic → Skill → Objective` is curriculum organization.

UI browsing placement must never imply:

```text
parent screen == hard prerequisite
previous list item == required predecessor
```

Runtime prerequisite truth remains PRG-v0 Skill→Skill edges.

## 10.3 Avoid artificial deep navigation

The app should not require users to traverse every organizational layer to reach a known Skill. Search/context/deep-link entry may jump directly to shared entity detail while preserving orientation metadata.

Exact search UX is deferred; IA reserves direct entity reachability.

---

# 11. Progress semantics guard

Progress must not default to:
- career-completion percentage,
- elapsed-day percentage,
- streak as success,
- lesson/task completion as mastery,
- numeric CEFR average,
- broad Domain failure that hides exact weak Skill.

Allowed high-level summaries are **derived** from canonical state and must preserve drill-down to exact Skills/requirements.

`not_started`, `developing`, `review_due`, `verification_due`, `remediation_required`, `prerequisite_unresolved`, and confirmed states must not be visually collapsed into one generic “incomplete” bucket when the distinction matters.

Final visual labels and grouping belong to 8E.

---

# 12. Assessment IA guard

Assessment architecture must preserve:

```text
assessment session
→ Attempt/Artifact
→ validity/attribution/evidence pipeline
→ canonical state update
→ planner replan
→ report/explanation
```

Forbidden IA shortcut:

```text
assessment score screen -> directly writes broad mastery/course progress
```

Daily micro assessment may appear inside the daily flow without creating a separate permanent section.

---

# 13. English IA guard

Technical English can be visible in three contexts without duplication:

1. **Today:** selected English task / integrated task.
2. **Learn:** D01 capability placement and exact Skill context.
3. **Progress:** TEPM-v0 qualified profile and exact state.

The interface must not imply that English must be globally completed before technical routes unlock.

---

# 14. Loading, empty, offline and degraded states

These are cross-cutting surface states, not top-level destinations.

Required semantic states:

```text
loading
empty_valid
error_recoverable
offline_local_available
ai_unavailable_core_available
data_recovery_required
```

Key rules:
- an empty Today plan must distinguish “nothing currently eligible/needed” from load/error failure,
- offline/local-first data should remain accessible where the canonical local core supports it,
- AI unavailable state must not make Today/Learn/Progress/Profile unusable,
- recovery/data problems route to explicit recovery actions rather than silently resetting progress.

Exact components/copy belong to 8B/8F/17.

---

# 15. Entry points outside the shell

Notifications/deep links may target:
- Today,
- a selected/due task,
- a due review/verification Skill,
- an eligible assessment session,
- assessment report,
- Skill/Topic detail,
- Profile data/recovery/settings context.

Entry validation must occur before opening a stale or no-longer-eligible action. A stale notification cannot bypass planner/prerequisite/current-state checks.

Concrete deep-link implementation belongs to later technical stages.

---

# 16. Adaptive layout semantics

Semantic destination set and order remain:

```text
Today · Learn · Progress · Profile
```

across supported window sizes.

Later UI may map this to:
- compact: bottom navigation,
- expanded: navigation rail/panel,
- larger layouts: list-detail/supporting panes where useful.

Adaptive rendering must not create a different information architecture or rename the same function unpredictably.

---

# 17. Accessibility and consistency baseline

8A requires semantic consistency now so 17D can validate implementation later:

- repeated destinations keep stable IDs/order,
- same function keeps consistent semantic label/accessibility name,
- icons never become the only source of meaning for critical state,
- color alone cannot be the only carrier of mastery/review/remediation state,
- focused flows retain an explicit safe exit/pause route,
- navigation order must remain predictable.

Exact touch targets, contrast, screen-reader implementation and motion preferences belong to 8F/17.

---

# 18. Forbidden IA patterns

UXIA-v0 forbids:

1. A top-level tab for every engine (`Mastery`, `Retention`, `Remediation`, `AI`, etc.).
2. A permanent English silo that duplicates the main learning engine.
3. A permanent gradebook-first Assessment tab detached from adaptive planning.
4. Different contradictory Skill detail pages under Learn and Progress.
5. UI hierarchy being treated as hard prerequisite truth.
6. Career/streak/time/course-completion percentage as the main navigation/progress concept.
7. AI chat as the default start destination.
8. A stale deep link/notification bypassing eligibility/current-state validation.
9. Hiding all planner reasoning behind an LLM-only explanation surface.
10. Making Profile a public/social identity surface; V1 is personal/single-user.

---

# 19. 8B–8G handoff

## 8B — Ana ekran
Use `today` ownership to design the Today content hierarchy, next-action emphasis, plan summary and contextual attention presentation.

## 8C — Günlük çalışma akışı
Use `task_runner_flow`, focused-flow return semantics, pause/resume and contextual explanation entry.

## 8D — Sınav UX
Use `assessment_session_flow` + `assessment_report`; preserve evidence/state/replan separation.

## 8E — Skill/progress/weakness UX
Use `progress`, `skill_detail`, `topic_detail`, `technical_english_profile`, `learning_history`; define exact labels/grouping/visual state semantics.

## 8F — Tasarım sistemi
Implement consistent semantic labels/states/components across the IA; do not alter IA truth ownership.

## 8G — Wireframe/prototip
Validate the complete route graph and user jobs with concrete wireframes/prototype before technical architecture/implementation.

---

# 20. 8A acceptance contract

8A can be accepted only if independent QA verifies at least:

1. Exactly four top-level semantic destinations: Today/Learn/Progress/Profile.
2. Today is the normal start destination.
3. Assessment/English/AI/remediation are not top-level silos.
4. Every required V1 information domain has a primary UI home.
5. Skill detail is shared, not cloned by top-level section.
6. Task/assessment focused flows are distinct from the persistent shell.
7. Planner explanation is PDT-v0 trace-derived.
8. Browse hierarchy cannot masquerade as prerequisite hierarchy.
9. Progress forbids time/streak/task-completion/general-CEFR overclaim.
10. English profile remains TEPM-v0 qualified Technical English only.
11. Assessment report cannot directly own mastery truth.
12. Local/offline/AI-degraded states keep deterministic core semantics.
13. All shell roots and required shared routes are reachable.
14. Semantic navigation remains stable across adaptive layouts.
15. 8B–8G boundaries are explicit and no final visual implementation is prematurely locked.

Until independent QA and D-050 POST are complete, this document remains candidate rather than accepted.
