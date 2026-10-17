---
type: session-log
status: completed
stage_step: 16A
model: SKAX-v0
decision: D-122
date: 2026-10-17
---

# 16A — Skill analytics — one derivation for a Skill's state, Progress reads the store

On 2026-10-17 the user said "merge et ve devam et 16a ya başla". 15H (#59) was squash-merged after green CI (6ba4d67). The fresh 16A PRE showed `15H ✅ / 16A active-not-executed`, and the same message was the explicit approval for 16A.

## Result
- Canonical: `docs/SKILL_ANALYTICS_SPEC.md`
- Machine-readable: `arch/16a_skill_analytics/skill_analytics.yaml`
- QA: `arch/16a_skill_analytics/qa_report.yaml`
- Stale audit: `arch/16a_skill_analytics/stale_reference_audit.yaml`
- Research/synthesis: `research/16a_skill_analytics_research.md`
- Code: `core-model` (`SkillPresentationFacts.kt`, `ProgressFacts.kt`), `core-application` (`SkillStateAssembly.kt`, `ProgressQuery.kt`), `core-presentation` (`ProgressPresentation.kt`), `app-ui` (`ProgressScreen.kt`), `app-wiring`
- Final: `SKAX-v0 / D-122`
- Not run: T6. The screen has not been seen on a device.

## User decisions
- Scope:
  - the Skill projection: eight states derived from four axes and written correctly to `skill_state`, with `at_risk` as a qualifier;
  - `skill_detail`;
  - `progress_overview`.
  
  Topic, weakness and English views are 16C's; history and `assessment_report` are 16B's.
- Delivery: core plus a real Compose screen that reads the store.
- An unevaluated axis is shown as recomputing, never guessed.

## Found
- `skill_state.primary_presentation_state` held 12A's mastery value, and 13C and 13D carried it forward.
- The row assembly in `core-application` and the screen in `core-presentation` need the same derivation, and `MSBX-v0` forbids either depending on the other. The derivation moved to `core-model`.
- A dependent's stored prerequisite axis goes stale, so the Progress read asks the gate again.
- For a learner who has done nothing, 4 of 60 Skills are not yet evidenced and 56 wait on a prerequisite. The 56 are exactly the Skills the planner holds back.
- `TEPM-v0` §4.1 rule 4 names fresh and stable only. `at_risk` on a confirmed Skill stays confirmed, with a qualifier (`RVR-v0` §2).
- The first Kotlin mutation run left three mutants alive:
  - the retention writer not deriving the presentation;
  - the weakness writer not deriving the presentation;
  - the order of the two empty states.
  
  The end-of-chain assembly hid the first two, because it re-derives the column. In the third, the no-evidence test always had something waiting. Targeted tests closed all three, and the whole set ran again.

## Next
`16B — Öğrenme geçmişi` needs a fresh PRE and the user's new explicit approval.
