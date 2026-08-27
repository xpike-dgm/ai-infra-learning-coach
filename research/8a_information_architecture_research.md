# 8A Research — Mobile Learning Information Architecture

**Stage step:** 8A — Bilgi mimarisi  
**Role:** Research AI evidence package; final product decisions belong to the 8A canonical spec.  
**Date:** 2026-08-27

## Research question

How should AI Infra Learning Coach organize its mobile information architecture so that the user can answer “What should I do today?”, inspect the curriculum and exact capability state, understand adaptive decisions, and reach settings/history without turning the app into a dense course catalog or exposing misleading progress metrics?

## Canonical product constraints reviewed

- `docs/PRODUCT_REQUIREMENTS.md`: the home experience must answer “Bugün ne yapmalıyım?”, progress is evidence/mastery based, not elapsed time/streak/course percentage.
- `docs/V1_SCOPE.md`: V1 needs Today, Task Runner, assessments, granular Skill progress/weakness, Technical English, settings/reminders, local-first data controls and modern/professional UI.
- `docs/NON_GOALS.md`: no social/commerce/leaderboard, no full mobile IDE, no broad fake progress, no English global gate.
- `docs/TASK_TAXONOMY_SPEC.md`: LearningNeed, TaskCandidate, PlannedTask, Attempt/Artifact and EvidenceEvent are different entities.
- `docs/PLANNER_EXPLAINABILITY_SPEC.md`: user-facing “why?” explanations must be derived from structured planner decision traces.
- `docs/TOPIC_STATE_MACHINE.md`: Topic state is derived orchestration/UX state, not canonical mastery.
- `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`: English profile is a qualified Technical English projection over exact Skill state, not a general CEFR certificate.

## External evidence

### Android Developers — navigation bar / mobile navigation patterns

Sources:
- https://developer.android.com/develop/ui/compose/components/navigation-bar
- https://developer.android.com/design/ui/mobile/guides/layout-and-content/layout-and-nav-patterns
- https://developer.android.com/develop/adaptive-apps/guides/build-adaptive-navigation
- https://developer.android.com/guide/navigation

Relevant guidance:
- A navigation bar is intended for **three to five destinations of equal importance** on compact windows.
- Primary destinations should be consistent across screens.
- Android navigation guidance emphasizes predictable navigation and explicit top-level destinations.
- Adaptive navigation may use a bottom navigation bar on compact windows and a navigation rail on larger windows without changing the semantic destination set.

8A implication:
- The product should have a small stable set of top-level destinations rather than one tab per subsystem.
- Semantic navigation identity must be independent from the later concrete Android component implementation.

### W3C/WCAG — consistent navigation and identification

Sources:
- https://www.w3.org/WAI/WCAG21/Understanding/consistent-navigation
- https://www.w3.org/WAI/WCAG22/Understanding/consistent-identification

Relevant guidance:
- Repeated navigation mechanisms should occur in a consistent relative order.
- Components with the same functionality should be identified consistently.

8A implication:
- Top-level destination IDs/order and repeated action semantics should be stable.
- The same action (for example “why this task?”, “resume”, “review due”) must not receive unrelated labels in different surfaces merely for visual novelty.

### Nielsen Norman Group — mobile IA and progressive disclosure

Sources:
- https://www.nngroup.com/articles/mobile-sharpens-usability-guidelines/
- https://www.nngroup.com/articles/mini-ia-structuring-information/
- https://www.nngroup.com/articles/explicit-differences/

Relevant guidance:
- Mobile navigation should be comparatively shallow because the viewport provides less contextual orientation.
- Information structures should follow user tasks/mental models rather than internal organizational charts.
- Progressive disclosure can keep primary decisions simple while exposing secondary detail on demand.

8A implication:
- The primary shell should center user jobs: do today’s work, browse/understand the learning map, inspect evidence-backed progress, manage personal settings/data.
- Internal engines such as mastery, remediation, retention and AI evaluation should not each become top-level destinations.
- Exact Skill/evidence/prerequisite detail remains reachable through shared detail surfaces rather than being dumped onto every overview.

## Reconciliation with product constraints

### Candidate top-level architecture

The strongest fit is four semantic primary destinations:

1. `today` — action-now / current adaptive plan.
2. `learn` — curriculum map and capability browse.
3. `progress` — evidence-backed capability status, weaknesses, review/verification, history and qualified Technical English profile.
4. `profile` — capacity/preferences/reminders/theme/accessibility/data controls and later provider settings.

This sits inside the Android 3–5 primary-destination guidance and matches the product’s four major user jobs without turning internal engines into navigation categories.

### Why Assessment is not a fifth top-level destination

Weekly/monthly/daily assessment is a **workflow and evidence source**, not a permanent peer of Today/Learn/Progress/Profile. Due assessment should surface from Today; completed assessment reports/history belong under Progress; active assessment uses a focused session flow. This preserves the product rule that assessment changes state/planner instead of becoming a detached gradebook.

### Why English is not a top-level destination

Stage 7 established Technical English as a parallel track integrated with the same planner/capability system. English tasks appear in Today, English capabilities are browsable through Learn, and the qualified Technical English profile lives under Progress. A separate permanent English tab would falsely imply a second product/engine.

### Why AI Tutor is not a top-level destination

AI is contextual assistance/evaluation, not the source of truth. It should be invoked from the task/detail contexts where it can help. A permanent AI destination would over-emphasize a non-canonical subsystem and encourage chat-first rather than learning-loop-first behavior.

### Why Remediation/Retention are not top-level destinations

They are states/needs that affect Today and Progress. They should appear as attention/reason states and contextual actions, not as separate content silos.

## Research conclusion

8A should use a **task-centered, four-destination semantic shell** with shared entity detail routes and focused learning/assessment flows. The IA must explicitly separate:

- overview/navigation state from canonical mastery truth,
- browse hierarchy from prerequisite hierarchy,
- planner explanation from free-form AI explanation,
- assessment session/reporting from direct mastery writing,
- English profile summary from general CEFR certification,
- primary destinations from contextual/focused flows.

No external source justifies a fixed card count, exact visual styling, fixed navigation component implementation, or specific screen layout. Those belong to 8B–8G and later implementation stages.
