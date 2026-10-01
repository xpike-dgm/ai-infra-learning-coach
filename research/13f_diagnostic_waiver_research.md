# 13F Diagnostic Waiver — Research & Decision Synthesis

**Stage step:** 13F — Tanısal atlama (VDW-v0)  
**Purpose:** Run `VDW-v0 / D-037` against real code: a learner who already knows part of a Topic skips only the starting lessons of the Objectives they show — under the mastery engine's own gates — and nothing else.

## 1. Research-need decision

**No web research pass was needed.** What a diagnostic may waive is fixed by accepted specs: `VDW-v0` (`docs/DIAGNOSTIC_WAIVER_SPEC.md`) defines the waiver, its partial form and its guards; `GRE-v0` (`MSTX-v0`) defines the gates it may not lower; `PDT-v0` §8.7 closes its reason codes and §23 invariant 12 its explanation; 3H S06 is the scenario. The work was to give an accepted policy a home in storage, the planner and the result, and to settle three product questions the specs leave open or contradict — which went to the user (§4).

## 2. Canonical source set reviewed

- `docs/DIAGNOSTIC_WAIVER_SPEC.md` (`VDW-v0`) — §2–§25.
- `docs/MASTERY_FORMULA_V0.md`, `docs/MASTERY_ENGINE_IMPL_SPEC.md` — the gates, eligibility, groups and families.
- `docs/PLANNER_SIMULATION_SUITE.md` S06; `docs/PLANNER_EXPLAINABILITY_SPEC.md` §8.7 and §23; `docs/VIRTUAL_USER_TESTS_SPEC.md` (S06 re-pointed to 13).
- `docs/TASK_TAXONOMY_SPEC.md` (3B §15 `target_objective_ids`, `diagnostic_opportunity`), `docs/PRIORITY_POLICY_SPEC.md` (P3, decision value).
- `docs/WEAKNESS_REMEDIATION_IMPL_SPEC.md` and `curriculum/decomposition/6g_weakness_remediation/` (`need.map.supported_premastery_weakness`).
- `docs/PROGRAM_CHANGE_REPORT_IMPL_SPEC.md` (13E), `docs/REPLAN_SPEC.md` (`diagnostic_waiver_granted`).
- `docs/DOMAIN_DATA_MODEL_SPEC.md`, `arch/9c_domain_data_model/`, `arch/9d_service_boundaries/` — the projection inventory and the engine-ownership map.
- `D-099` (13F added), `D-102` (Topic state → 16C).

## 3. What was found

1. Nothing produced or read the diagnostic codes, the need or the replan event that already existed; S06 and invariant 12 had no owner until `D-099`.
2. No waiver entity in `DDM-v0` and no owner in `MSBX-v0`: both were accepted before `VDW-v0` had an implementation step.
3. No evidence row could say it came from a diagnostic, and teaching completion is recorded nowhere, so "gates passed before teaching" was not computable from what existed.
4. A task could not say which Objectives it teaches.
5. 13E would not have replanned after a partial waiver: it reports axis changes only.
6. `VDW-v0` §12.1 and `WLRM-v0`'s pre-mastery rule disagreed on an untaught diagnostic miss.
7. While writing the tests, a first implementation still waived an Objective after help had been taken in the same diagnostic (help then two clean successes); the waiver replay now stops counting a diagnostic's evidence once its fast path ended.
8. The 12E explanation could not say a lesson was skipped and called a need whose lessons wait for the diagnostic "a need with no task"; both are now read from the trace's own codes.

## 4. Positions taken

- **User decisions (2026-10-01):** a diagnostic baseline miss is not a weakness; only the learner opens a diagnostic; help taken ends the fast path for that Objective.
- **A diagnostic is a `daily` assessment session** whose content is the request (`diagnostic_scope/1`); the newest diagnostic row decides; a new request replaces, a withdrawal ends — nothing owed.
- **The waiver is `GRE-v0`'s first pass on diagnostic evidence**, naming its window; ordinary learning that got there first means no waiver; it is coverage, not competence.
- **Teaching an Objective waits while the learner's diagnostic is checking it**, because teaching first would make "already knew it" untrue; only lessons are held, and only for declared Objectives.
- **Its own projection and state family** (`diagnostic_coverage`, `VDW-v0`), so planning reads no evidence and no other engine's state is borrowed — an explicit extension of `DDM-v0` and `MSBX-v0` under `D-104`, the accepted contracts unedited.
- **Two report kinds** so a partial waiver replans through the planner's own event.

## 5. Explicitly not decided in 13F

Topic state (16C); the app's fast-path entry and calling the use cases (16D); entry placement (16D); planner-initiated diagnostics (18B); carrying a waiver across curriculum versions (15); authored items and lessons (15); replay cost (18E); wording (14). T6 was not run.
