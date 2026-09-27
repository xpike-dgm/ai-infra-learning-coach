# 12C Planner Engine — Research & Decision Synthesis

**Stage step:** 12C — Planner Engine v1  
**Purpose:** Turn the Stage 3 planner contracts into running code: which of today's open needs are served, by which task, inside the minutes the learner actually has — and a trace that can say why.

## 1. Research-need decision

**No web research pass was needed.** Stage 3 designed the planner in full — capacity (3A / D-033), task taxonomy (3B / D-034), priority bands and the rank vector (`PBR-v0`), the prerequisite gate (`PRG-v0`, in code since 12B), explainability (`PDT-v0`) — and 3H verified it at policy level (16/16 scenarios, 20/20 invariants). The familiar alternatives are exactly what those contracts rejected: a weighted utility score, a knapsack that maximises tasks per minute, a fixed category split. The work was to implement the decision order they fixed and to find where today's store and content do not yet carry what they assume.

## 2. Canonical source set reviewed

- `docs/ADAPTIVE_PLANNER_SPEC.md` (3A / D-033) — capacity resolution order, hard vs planning budget, the `0.10` reserve and `10`-minute minimum block as engineering defaults, no auto-overrun, split → smaller alternative → defer, overflow is not debt.
- `docs/TASK_TAXONOMY_SPEC.md` (3B / D-034) — `LearningNeed` before task, the ten trigger kinds, the seven purposes, the `TaskCandidate` contract, bounded candidate generation with the cap left to 12C.
- `docs/PRIORITY_POLICY_SPEC.md` (`PBR-v0`) — five bands, the ten-field rank vector compared in order and never summed, the band decision table, starvation promotion limits, the knapsack and short-task-bias anti-patterns, the 80-minute / 50-minute worked example.
- `docs/PLANNER_EXPLAINABILITY_SPEC.md` (`PDT-v0`) — the gate order, `PlannerDecisionTrace` / `NeedDecisionTrace` / `CandidateDecisionTrace`, the dispositions, the reason-code families, §18's "higher priority did not fit" rule, and replan as a new plan version.
- `docs/DAILY_ENGLISH_COMPONENT_SPEC.md` (`DECP-v0`) and 7C's QA — English reuses `PBR-v0`'s balance and starvation semantics, and **no fixed omission threshold may be invented**.
- `PRQX-v0 / D-093` — the gate every candidate passes before priority is computed.
- `DDM-v0` / `LDBX-v0` — `plan_version`, `planned_task` and `planner_decision_trace` are append-only truth tables; `planned_task` carries only the Skill and the position.
- `TDYX-v0 / D-087` — Today does not read a plan yet, deliberately, because a row needs a purpose, a duration and a trace-backed reason the store could not hold.

## 3. What was found before any code was written

1. **`planned_task` cannot hold a Today row.** It has the Skill and the position; the purpose, title, activity, minutes and reasons have no column. 11A recorded this as 12's.
2. **No authored task exists.** `curriculum_package/1` has no task section and the authored curriculum has no task metadata, so every need a real store opens today has no candidate.
3. **No starvation threshold exists anywhere.** `PBR-v0` §6.5 leaves it to 18B/18C, and 7C's QA explicitly records "no invented N-day starvation threshold".
4. **Retention and weakness have never been written.** Overdue buckets, `at_risk` and weakness-derived needs have no input yet (13).
5. **Every authored Skill is `draft`**, like every authored edge (12B). A draft Skill has no runtime selection (`KGC-v0` §27).
6. **The per-need candidate cap is explicitly 12C's** (3B §17), and nothing else in the contracts sets it.

## 4. Positions taken

- **The trace carries what `planned_task` cannot.** `planner_decision_trace.trace` is one text column, so the whole `PDT-v0` trace — including each selected task's purpose, title, activity and minutes, keyed by position — is stored there in a strict, versioned format (`planner_trace/1`). No column was invented, and a trace that does not parse is refused, never guessed at.
- **Candidates are content's answer to a need.** `ContentPort.taskCandidates(need)` is a port refinement; the file adapter answers with an empty list because the package format has no tasks. The planner then records the need as having no valid candidate and invents no reason code for it.
- **No starvation threshold is invented.** The engine takes starvation and track-balance pressure as inputs and implements the promotion limits `PBR-v0` fixed; the product supplies none, so every need is at `none` until 18C calibrates a threshold.
- **Needs come from the axis each engine owns.** Remediation from the weakness axis, verification from a contradicted mastery or a retention verification, review from retention, continuation from mastery in progress, new learning from mastery not yet evidenced. An axis nobody has written opens nothing.
- **A draft or retired Skill opens nothing**, and a deprecated one keeps its verification, review and remediation needs but opens no new learning; the count of Skills off the route is recorded.
- **The cap is five candidates per need**, an engineering bound on how much the planner reads, kept in a stable order (deprecated versions last); 18E may move it.
- **High-stakes purposes need a validated candidate.** `assess`, `retain` and `diagnose` produce evidence that can settle mastery, so a `candidate`-status task may not serve them (`PBR-v0` §3).
- **The plan is truth.** It is appended in one transaction, never edited, and a replan will be a new version (12D).

## 5. Explicitly not decided in 12C

Replan triggers, re-entry after absence, paused-work continuation and the `skill_state` assembly under one watermark (12D); user-facing reason text, and Today reading the plan once its rows can carry trace-backed reasons (12E); virtual-user runs of the 3H scenarios (12F); retention due buckets, `at_risk` and weakness needs (13); authored tasks and their format (15); the capacity setting itself (16D); the starvation and track-balance thresholds (18C); the per-need cap against real performance (18E). Nothing in the app calls the planner yet.
