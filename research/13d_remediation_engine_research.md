# 13D Remediation Engine — Research & Decision Synthesis

**Stage step:** 13D — Remediation Engine  
**Purpose:** Turn `WLRM-v0`'s weakness localization and remediation map into running code — Objective-local attribution, a derived Skill weakness axis, closure by evidence, and corrections read from dispositions.

## 1. Research-need decision

**No web research pass was needed.** `WLRM-v0` (6G) already fixes the attribution rules, the lifecycle, the need mappings, the aggregation guards and the closure contract, and it passed the independent 6H external Research QA (`S6ERQA-v0 / D-062`), which added the guidance-fading, reverification-first and AI-scaffold-not-closure guards. Calibration and misconception taxonomy are explicitly deferred (18C, 14B). The work was to implement the accepted contract without inventing a score, a threshold or a reset, and to find what the code was missing.

## 2. Canonical source set reviewed

- `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` (`WLRM-v0`) §3–§9 and the dataset: `state_contract.yaml`, `failure_attribution_rules.yaml`, `learning_need_mappings.yaml`, `aggregation_guards.yaml`, `misconception_contract.yaml`, `remediation_strategies.yaml`, `remediation_routes.yaml`, `objective_weakness_profiles.yaml`.
- `docs/MASTERY_ENGINE_IMPL_SPEC.md` (`MSTX-v0`) — the gates whose failure confirms and whose recovery closes.
- `docs/RETENTION_IMPL_SPEC.md` (`RVRX-v0`) — the per-row mastery replay, now shared.
- `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md`, `docs/PLANNER_ENGINE_IMPL_SPEC.md` — `remediation_required` read by the gate and the planner; `weakness_detected` listed as owner-supplied.
- `docs/WEEKLY_ASSESSMENT_IMPL_SPEC.md`, `docs/MONTHLY_ASSESSMENT_IMPL_SPEC.md` — root-cause contamination left forward-only.
- `docs/LOCAL_FIRST_PERSISTENCE_SPEC.md`, `docs/DOMAIN_DATA_MODEL_SPEC.md` — corrections as appended dispositions.
- `docs/TOPIC_STATE_MACHINE.md` (`TSM-v0`) — the six Topic states, moved to 16C by the user.

## 3. What was found

1. Nothing wrote the weakness axis; `weakness_state` had one column.
2. `weakness_detected` was owner-supplied but had no owner in code.
3. Dispositions were written but no engine read them; retroactive contamination was left here by 13A/13B.
4. Retention and weakness both need the per-row mastery replay.
5. Topic state and the 11C high-stakes gap policy pointed at "13" with no sub-step to own them.

## 4. Positions taken

- **Attribution is `WLRM-v0`'s twelve rules in priority order**; four can never match a stored row and are kept so the set stays the accepted one. A clean but indirect failure is corroboration (hypothesis).
- **Confirmation and closure follow the mastery engine's gates**; closure needs a fresh, clean check; help, the same item, one success and a finished task never close.
- **The engine supplies `weakness_detected`** to the planner and the composers; one concern is one need; no planner rule changed.
- **Dispositions are read newest-first at the store**, mapped onto `WLRM-v0` §4's own rules, so every engine sees corrected rows without a second exclusion rule; the learner's outcome is never rewritten.
- **Retroactive contamination** appends a disposition only for slots that really required the failed Skill.
- **One mastery timeline** is shared by retention and weakness.
- **User decisions:** Topic state → 16C; high-stakes gap policy → 18D.

## 5. Explicitly not decided in 13D

Topic state and `weakening` (16C); the high-stakes gap policy (18D); remediation content from strategy routes (15/20); misconception memory (14B); guidance fading (14); calling rebuilds and retroactive contamination from the app (16D); disposition reasons in traces (16C); calibration (18C). T6 was not run.
