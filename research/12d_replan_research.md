# 12D Replan — Research & Decision Synthesis

**Stage step:** 12D — Replan  
**Purpose:** Replace a plan the way the contracts say a plan is replaced: a new version with a reason, what was started kept, only the rest solved again — and a return after absence that is neither debt nor failure.

## 1. Research-need decision

**No web research pass was needed.** Stage 3 fixed every rule this step implements: the replan event and new plan version (`PDT-v0` §15), the remaining-budget rule (D-033 §8), the replan triggers (D-033 §16, `PBR-v0` §17, `PRG-v0` §19), and re-entry after absence (`SRR-v0`, whose 3H scenarios A–H already passed at policy level). The familiar alternatives — a backlog of missed tasks, a "catch-up" day, a streak penalty — are exactly what `SRR-v0` rejected. The work was to find what the store and the earlier steps cannot yet say, and not to fill those gaps with guesses.

## 2. Canonical source set reviewed

- `docs/PLANNER_EXPLAINABILITY_SPEC.md` (`PDT-v0`) — §4 `generation_kind: initial | replan | reentry`, §15 `PlannerReplanEvent`, §8.8 re-entry codes, §8.10 replan codes.
- `docs/ADAPTIVE_PLANNER_SPEC.md` (D-033) — §8 a replan changes only the remaining budget; less time re-solves unstarted work, more time gets a fresh mini-plan, never the old deferred list; §16 capacity triggers.
- `docs/MISSED_DAY_RECOVERY_SPEC.md` (`SRR-v0`) — absence is not failure, mastery decay or task debt; an old plan is never replayed; current state is regenerated; a safe checkpoint is a candidate, never automatic; an interrupted high-stakes attempt is not continued as independent evidence; starvation does not grow with absence; the day's hard budget still holds; §17 `ReentryContext`.
- `docs/PRIORITY_POLICY_SPEC.md` (`PBR-v0`) — §17 replan triggers, §10 one task per need, §15 focus preference.
- `docs/PREREQUISITE_POLICY_SPEC.md` (`PRG-v0`) — §19 prerequisite replan triggers.
- `docs/DAILY_WORKING_FLOW_SPEC.md` (`TRUX-v0`) — §10.1 an in-flight run is never destroyed by a replan.
- `SESX-v0 / D-089` — the stored `ResumeContext` carries the need key it pauses.
- `PLNX-v0 / D-094` — the planner this step replans with, and its trace format.

## 3. What was found before any code was written

1. **The store cannot say which planned task was started or finished.** `DDM-v0` gives `attempt` no link to `planned_task` (an open loop since 11B). A replan must keep in-flight work (`TRUX-v0` §10.1), yet nothing in the store records it.
2. **12C's `build()` would write a second "initial" plan on the same day.** Called twice with nothing changed, it appended an unexplained new version — against `PDT-v0` §15, which gives every new version a reason.
3. **The trace format had nowhere to put a replan.** `planner_trace/1` has no preserved flag, no replan record and no re-entry context, and its decoder rightly refuses unknown sections.
4. **Some 12D handoff items cannot be built yet.** Assembling `skill_state` under one watermark matters only once a second engine writes that row (13); the attempt → evidence → mastery chain needs authored Objective gate profiles (15); calling the planner from the app needs a stored capacity (16D); reverse invalidation of dependents is an optimisation, since a replan already regenerates from bounded current state.
5. **`PBR-v0`'s `USER_FOCUS_CHANGED` has nothing to act on.** The planner takes no focus preference, so accepting the event would be pretending.

## 4. Positions taken

- **What was started is reported, then checked.** The caller that ran the tasks — the one that owns in-flight state — reports their positions; every position must exist in the plan being replaced, or the replan is refused and nothing is written. The trace records them as kept, first and unchanged.
- **Same day, no event: the plan already there is the answer.** A new version needs a reason: no plan yet, a new study day, or an event. 12C's behaviour is corrected, not its contract.
- **`planner_trace/2`** carries the kept flag, the replan record and the re-entry context. `/1` still decodes, strictly: a `/1` trace cannot claim what its format never had.
- **The remainder follows the day's rule.** A new capacity replaces the day's budget; a declared remaining time *is* the remainder; every other event keeps the day's budget. Kept minutes come off the top, a remainder is never negative, and replanning never grows the day on its own — only the learner's explicit extra time does.
- **A new study day is re-entry**, even if an event arrives with it: yesterday's plan is not replayed, nothing is kept, the day's normal budget applies, and the trace says so with `PDT-v0` §8.8's codes and `SRR-v0` §17's context — informational only, no score, penalty or debt.
- **A safe pause makes its need paused work** (`PBR-v0` P2), and it is still ranked, gated and fitted like any other; a high-stakes pause is not continued as independent work; a pause whose need has closed changes nothing.
- **An event with no `PDT-v0` code carries none.** `TASK_COMPLETED` is an event, not a reason code anyone defined.
- **Items that cannot be built now are re-pointed, not faked** (see §5).

## 5. Explicitly not decided in 12D

User-facing reason text and Today reading the plan (12E); the 3H virtual-user scenarios against this code (12F); `skill_state` assembly under one watermark and retention/weakness needs (13); the attempt → evidence → mastery chain and a stored link from attempt to planned task (15, with the data model); calling the planner from the app and the focus preference (16D); starvation thresholds (18C); reverse invalidation of dependents and candidate caps (18E). Nothing in the app calls the planner yet.
