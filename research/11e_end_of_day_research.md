# 11E End of Day — Research & Decision Synthesis

**Stage step:** 11E — Gün sonu  
**Purpose:** Decide what the end of a day may honestly say about what was recorded, and make everything it must never claim structurally impossible.

## 1. Research-need decision

**No web research pass was needed**, and the reason matters: an end-of-day screen is the single place where products of this kind reach for streaks, rings, daily goals and "you studied 42 minutes". None of those is a research question — each is already forbidden by an accepted contract in this repository. The work was to find the owners and follow them, not to look for ideas.

## 2. Canonical source set reviewed

- `SPWX-v0 / D-072` — presentation states; counts allowed **as labelled inventory**, never a ratio or percentage; `learning_history` records what changed and is explicitly not a streak calendar, contribution graph or attendance heatmap; a gap is not a failure or a missed obligation; a consecutive-day count is not a success metric.
- `SRR-v0` — absence is not debt, not negative evidence and not a failure; old planned work is not homework; re-entry rebuilds from current state.
- `THUX-v0 / D-069`, `TDYX-v0 / D-087` — Today's regions and precedence; the day's context region; no streak pressure, no completion percentage.
- `APHX-v0 / D-086` — a store that cannot be trusted supersedes normal presentation; every failure is a state, never a crash.
- `DDM-v0 / D-077` — three-value time: instant, learner-local study day and offset, each stored because none can be derived from the others.
- `VDSX-v0 / D-073` — tones; only real faults may wear the fault tone.
- `NSHX-v0 / D-083` — the surface set is closed.
- D-001/D-002/D-009/D-010 — elapsed time, activity completion and streaks are not mastery.

## 3. Synthesis problems 11E had to solve

1. **No accepted spec defines an end-of-day surface**, so its semantics had to be derived rather than invented — and an invented vocabulary here would have been the easiest place in the product to smuggle in a scoreboard.
2. **Nothing could count what a day recorded.**
3. **A day has three plausible definitions** — the instant range, the calendar day of the reader, or the study day the row recorded — and only one of them survives travel and daylight saving.
4. **The only data that exists today is activity**, and activity is exactly what must not be presented as progress.
5. **A read can fail per kind**, and a failed read looks identical to a zero unless it is kept distinct.

## 4. Positions taken

- **The day is the study day the row recorded.** Counting is done against the row's own day column; a table whose rows carry no day of their own is refused rather than counted through a parent.
- **Nothing crosses the boundary.** A new day starts empty, a day cannot roll backwards, and there is no API that could carry an obligation forward.
- **Counts are inventory and say so.** No total, no ratio, no percentage, no goal — not as a rule to remember, but as fields that do not exist.
- **A change requires a canonical engine to have reported one.** With no evidence pipeline yet, the summary states plainly that nothing changed.
- **An unreadable count is named, never zero.**
- **An empty day is neutral**, and a gap between days produces no history entry at all.
- **No new surface.** The summary lives in Today's day context; longitudinal history is 16B's.

## 5. Explicitly not decided in 11E

The engines that report a change and the open-need count (12), assessment/retention/remediation history entries (13), final microcopy and degraded day states (14), longitudinal history and the assessment report (16B), end-of-day notifications (16E). No streak, goal, percentage or minutes claim is made anywhere.
