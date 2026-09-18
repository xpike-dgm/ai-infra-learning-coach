# 11A Today — Research & Decision Synthesis

**Stage step:** 11A — Today ekranı  
**Purpose:** Turn `THUX-v0`'s Today into running code on top of `APHX-v0`'s health path, and decide what Today may truthfully show while the planner that fills it does not exist yet.

## 1. Research-need decision

**No web research pass was needed.** Everything 11A implements is already accepted: the content hierarchy, precedence, row contract, reason rules, attention families and empty-state distinctions (`THUX-v0`), the cross-cutting states (`UXIA-v0`), tones (`VDSX-v0`), region order (`WFPX-v0`), module placement (`MSBX-v0`), the health path (`APHX-v0`) and the schema (`DDM-v0`/`LDBX-v0`). The toolchain was verified and pinned at 10A and nothing new was added — 11A declares no dependency at all.

## 2. Canonical source set reviewed

- `THUX-v0 / D-069` — five semantic regions in order; six-step primary-action precedence; the queue is the current selection and never a backlog or debt list; the row keeps purpose, activity and track separate; one primary reason and at most one supporting reason, both subsets of `PDT-v0` trace facts; seven attention families that explain but never rank; twelve semantic states; empty and capacity-limited days are legitimate, and none of them implies mastery or readiness.
- `UXIA-v0 / D-068` — Today owns the current plan, next task, capacity context, attention and the planner-reason entry; `empty_valid` is one of six cross-cutting states.
- `VDSX-v0 / D-073` — the tone of every one of those states; only `error_recoverable` and `data_recovery_required` may look alarming.
- `WFPX-v0 / D-074` — `today_overview` region order, identical in all three window classes.
- `MSBX-v0 / D-078` — presentation state is computed in core, never in the UI toolkit; `core-application` does not depend on `core-presentation`; four ports; no domain logic in wiring.
- `APHX-v0 / D-086` — the shell exists only when normal use is available; `ai_unavailable_core_available` is context; `empty_valid` was explicitly left to 11.
- `LDBX-v0 / D-085` — `planned_task` holds id, plan version, skill ref, position and time, and nothing else; purpose, trace and duration were recorded as 12's.
- `TASK_TAXONOMY_SPEC` §3.1 — the seven canonical purposes; English is a track, not a purpose.

## 3. What reading the code found

Two defects that no handoff mentioned, both the same shape as ones earlier steps had already paid for:

1. **`FileContentSource.resource` was `TODO()`.** The first caller asking for curriculum content would have crashed — the `AiEvaluator` defect 10E found, in the other adapter. `ContentPort` returns a nullable document, so "not published" already has a truthful answer.
2. **`Surface.all` was an initialised `val`.** Referencing a surface from another module's initialiser produced a registry of nulls, and 10B's own test failed with a `NullPointerException` the moment `TodayView` named `Surface.ProgressOverview`. This is the Kotlin initialisation-order trap 10D hit with `truthGuards()`; the fix is the same shape, and this time it has a structural test.

## 4. Synthesis problems 11A actually had to solve

1. **The screen's whole subject does not exist yet.** The planner is 12, content is 15, capacity settings are 16D. A Today that renders something anyway is a Today that lies.
2. **`planned_task` cannot produce a complete row.** Purpose, reason and duration are not columns yet.
3. **"No plan" has more than one truth.** Nothing published, a published curriculum with no plan, a zero capacity and a capacity nothing fits are four different statements.
4. **A stale plan is worse than no plan.** A process open past midnight, or a plan from yesterday, must not become "today's".
5. **`core-application` cannot speak presentation.** The dependency rule forbids it, so the facts have to live somewhere both sides may see.
6. **A rule written against the wrong subject still passes its test.** The attention de-duplication rule was written against the primary action *kind* and tested where it could not fire.

## 5. Positions taken

- **Today renders only what it was given.** The projection is a pure function; every filter that protects truthfulness — stale plan, blocked task, replaced plan, unrevalidated session — removes the row rather than styling it differently, so the unsafe state has no code path.
- **The read path does not read a plan.** It reports the study day and whether any curriculum is published, and nothing else. Reading partial `planned_task` rows and defaulting the rest is how a screen begins claiming what no engine decided.
- **`empty_valid` is produced here**, distinguished from `loading_initial_plan` by whether a curriculum has been published, and from the capacity states by whether the day's budget explains the absence by itself.
- **The reason type has no string.** A closed vocabulary plus a private constructor makes an invented reason unconstructible rather than forbidden.
- **The capacity verdict is an input.** Comparing estimates in the projection would be Today deciding what the planner may select.
- **Facts and vocabularies live in `core-model`**, the read path in `core-application`, the projection in `core-presentation`, rendering in `app-ui` — and the presentation input is assembled by a `core-presentation` function so the composition root stays free of domain logic.
- **`curriculumPublished()` is a port refinement**, not a fifth port, following 10D's precedent with `ProjectionRecord`.
- **Today's facts are read on the store thread** and refreshed on resume, because 10E's rule about disk work did not stop being true when the disk work became a read.

## 6. Explicitly not decided in 11A

The planner, plans, traces and capacity summaries (12); the start action and Task Runner entry (11B); resume checkpoint content and session state (11C); curriculum ingestion (11D) and authored content (15); capacity settings and Profile controls (16D); final microcopy and the offline state (14); on-device accessibility polish (17).

No task-row count, geometry or timing is claimed, and no Today state other than the empty and loading ones has been seen against a real store.
