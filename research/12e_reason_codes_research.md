# 12E Explanation / Reason Codes — Research & Decision Synthesis

**Stage step:** 12E — Explanation / reason codes  
**Purpose:** Turn the planner's decision trace into what the learner reads — why this task, why not that one, why this waits, why the plan changed — and let Today read the plan at all, without the explanation ever saying something the planner did not decide.

## 1. Research-need decision

**No web research pass was needed.** Stage 3 fixed the whole explanation contract in `PDT-v0`: the two layers (§2), the LLM's limits (§3), the reason-code format and `related_refs` (§7), the ten code families (§8), what "why today", "why not today", "why waiting", "why review" and "why after absence" may say (§9–§14), the template fallback (§3, invariant 16) and the 3H invariants (§23). `THUX-v0` fixed what a Today row may show (§7.1–§7.3) and `UXIA-v0` fixed the `planner_explanation` surface. The familiar alternatives — an LLM writing the rationale, a "you are behind" nudge, a score behind the ordering — are exactly what those contracts rejected. The work was to find where the running code could not yet honour them.

## 2. Canonical source set reviewed

- `docs/PLANNER_EXPLAINABILITY_SPEC.md` (`PDT-v0`) — §2.2 `user_facing_explanation ⊆ facts_in_decision_trace`; §3 LLM limits and template fallback; §6 `CandidateDecisionTrace`; §7 `ReasonRef` with `related_refs`; §8 the ten families; §9–§14 the user-facing rules and example sentences; §18 a high-priority need that did not fit is not "lower priority"; §19 anti-patterns; §21 `why_today`, `why_not_today`, `reconsideration_condition`; §23 invariants 4–8, 10, 15, 16, 18, 20.
- `docs/PREREQUISITE_POLICY_SPEC.md` (`PRG-v0`) — §20 the nine explainability inputs "for 3G's final texts", including `independent_branch_available`.
- `docs/TODAY_HOME_SCREEN_SPEC.md` (`THUX-v0`) — §4.4 attention families; §6 the task row; §7.1 one primary and at most one supporting reason, and the eight semantic families; §7.2 the source rule; §7.3 `plan_changed`.
- `docs/TODAY_INTERIOR_SPEC.md` (`TDYX-v0`) — the reason summary is constructible only from trace facts; the read path held `plan = null` until the planner could supply complete rows.
- `docs/INFORMATION_ARCHITECTURE_SPEC.md` (`UXIA-v0`) — `planner_explanation` answers "Why this task?", "Why not today?", "Why blocked?" and "Why did the plan change?" as a projection of `PDT-v0` facts.
- `docs/PLANNER_ENGINE_IMPL_SPEC.md` (`PLNX-v0`) and `docs/REPLAN_SPEC.md` (`RPLX-v0`) — what the trace already carries and where (`planner_trace/2`).
- `SPWX-v0 / D-072` — hypothesis != deficiency; `RVR-v0` — `review_due` is not forgetting; `SRR-v0` — absence is not failure, decay or debt.

## 3. What was found before any code was written

1. **The trace could not name a blocker.** The planner used the gate's hard blockers to decide and to rank, then dropped them; `CandidateTrace` had no `related_refs`. Re-running the gate at explanation time would explain today's plan with a decision the planner never made, because state may have moved since.
2. **Today could not read a plan.** `TodayFactsQuery` returned `plan = null`, and `latestPlan()` returned only a count and the trace text, so a Today row had no stored `planned_task` id.
3. **The planner writes a code `PDT-v0` §8 does not list** (`independent_branch_available`) — legitimately, from `PRG-v0` §20.
4. **11A's label for continuing on the route would claim a start** once new learning reached Today, because `THUX-v0` §7.1 gives new learning no separate family.
5. **Kept work would be offered again** after a replan.

## 4. Positions taken

- **A closed catalogue** of `PDT-v0` §8 and `PRG-v0` §20, in their order. A code outside it cannot become a statement.
- **The trace records the gate's answer about Skills at planning time** (`planner_trace/3`); older formats read strictly and cannot claim it.
- **The plan is read only when its trace describes its own rows**; the day is the row's; anything else is unreadable and Today says so as a recoverable fault, rather than showing guessed rows or an empty day.
- **Today's row families are trace facts**: the need's trigger first; a continued safe pause is the more specific fact; an unconfirmed weakness is verified, not "repaired"; English is a track and the reason only for the parallel track's own cadence; the fit the planner had to make is the supporting fact.
- **The continuation label now says progress on the route**, true for new and continued learning; `THUX-v0`'s vocabulary is unchanged.
- **Kept work is part of the plan and not startable**; continuing a run is the resumable session's path.
- **Every explanation statement is a recorded catalogue code or a trace fact**, through a private constructor. Band codes are rank bookkeeping and are never shown as the reason; deferral for time is never lower importance; a waiting need names its Skills; a need nothing serves says so with no invented code; an uncoded replan says only that the plan changed.
- **The template fallback lives in core** beside `DayCopy`, and a check reads every sentence against the rules; the wording itself stays 14's.

## 5. Explicitly not decided in 12E

The 3H scenarios run against this code (12F); retention and weakness needs and the codes only they write (13); final microcopy and any LLM paraphrase (14); authored tasks (15); calling the planner from the app and the capacity setting (16D); starvation thresholds (18C); reverse invalidation and candidate caps (18E). Nothing in the app calls the planner yet, so no plan exists on a device to read or explain.
