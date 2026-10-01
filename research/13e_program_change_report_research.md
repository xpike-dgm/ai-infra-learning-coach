# 13E Program Change Report — Research & Decision Synthesis

**Stage step:** 13E — Program değişiklik raporu  
**Purpose:** Make the consequence of an assessment visible and honest — what canonical state and the plan actually changed, and why — now that mastery, retention, weakness and readiness all write state from evidence.

## 1. Research-need decision

**No web research pass was needed.** What a result may claim is already fixed by accepted specs: `ASUX-v0` §13 (six result families; a state change only if canonical state actually changed; "nothing changed" said plainly; a contradiction is verification, never demotion), `SPWX-v0` (hypothesis ≠ deficiency), `WBA-v0` / `MCA-v0` §30 (the session summary says what changed in the plan), `PDT-v0` §8.10 (replan reasons) and `V1_SCOPE` V1 release tanımı 3 (an assessment changes the next plan). The work was to connect the engines that already exist without letting the report decide anything.

## 2. Canonical source set reviewed

- `docs/ASSESSMENT_SESSION_UX_SPEC.md` (`ASUX-v0`) §13–§14.
- `docs/PROGRESS_SKILL_UX_SPEC.md` (`SPWX-v0`) — vocabulary, hypothesis ≠ deficiency, `assessment_report` as a longitudinal surface.
- `docs/WEEKLY_ASSESSMENT_SPEC.md` (`WBA-v0`), `docs/MONTHLY_ASSESSMENT_SPEC.md` (`MCA-v0`) §30; their implementations `WBAX-v0`, `MCAX-v0`.
- `docs/PLANNER_EXPLAINABILITY_SPEC.md` (`PDT-v0`) §8.10; `docs/PLANNER_ENGINE_IMPL_SPEC.md`, `docs/REPLAN_SPEC.md` (12D), `docs/PLANNER_EXPLANATION_IMPL_SPEC.md`.
- `docs/MASTERY_ENGINE_IMPL_SPEC.md`, `docs/RETENTION_IMPL_SPEC.md`, `docs/WEAKNESS_REMEDIATION_IMPL_SPEC.md`, `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md` — the axes and their value sets.
- `docs/V1_SCOPE.md` V1 release tanımı, item 3.

## 3. What was found

1. No single place recomputed a Skill after evidence, and no port listed a Skill's Objectives, so the gate profiles every rebuild needs had no source.
2. The result families and `stateChangeRefs` existed and were empty by design, waiting for an engine-reported change.
3. The replan existed; nothing chose its event from what an assessment did.
4. A first reading has no before; a naive diff would report "confirmed" for a Skill nobody had evaluated.

## 4. Positions taken

- **The report is a diff of two snapshots**, never an interpretation of answers.
- **Unwritten is not a before**; such Skills are named in `unknownBefore` and no change is claimed.
- **Time alone reports nothing** (review coming due, untracked → fresh, development between non-mastered states).
- **A contradiction is `verification_opened`**, one per Skill however many axes opened it; correction downgrades report nothing.
- **Replan only when state changed**, through the planner's own replan with the event the change names; a first plan is not a change.
- **Readiness is not diffed separately** — it is a function of the reported axes; its effect on dependents shows as plan changes.
- **Profiles come from the published curriculum**; an unknown criticality is refused; transfer/artifact parameters stay at `GRE-v0` defaults until the package carries them (15).
- **One port refinement**, `objectivesOf`; four ports, schema unchanged.

## 5. Explicitly not decided in 13E

Durable `assessment_report` and longitudinal history (16C); the app calling recompute/report and filling `stateChangeRefs` (16D); transfer/artifact gate fields (15); final wording (14). T6 was not run.
