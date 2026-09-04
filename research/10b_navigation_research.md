# 10B Navigation — Research & Decision Synthesis

**Stage step:** 10B — Navigation  
**Purpose:** Build the `UXIA-v0` shell on the APIs pinned at 10A, and decide where the navigation rules live.

## 1. Research-need decision

**No external research pass was required.** The one thing 10B would have needed from the ecosystem — the current adaptive navigation APIs — was verified and pinned at 10A (`WindowSizeClass` from `androidx.compose.material3.adaptive:adaptive`, `NavigationSuiteScaffold` from `androidx.compose.material3:material3-adaptive-navigation-suite`, both at the versions recorded there on 2026-08-31). Everything else in this step is derived from contracts that are already accepted.

That is the whole point of `AMTS-v0`'s "currency is verified, not asserted" discipline: the verification happened once, at the step that pinned the toolchain, and 10B spends it rather than repeating it.

## 2. Canonical source set reviewed

- `UXIA-v0 / D-068` — exactly four semantic destinations in fixed order (`today → learn → progress → profile`), `today` as start; assessment, Technical English, AI Tutor and retention/remediation are explicitly **not** top-level destinations; shared detail surfaces reachable from several destinations without cloning the entity; the enumerated navigation graph; the focus-flow return rule; adaptive rendering must not change the information architecture; ten forbidden IA patterns.
- `WFPX-v0 / D-074` — three window classes with breakpoints (compact ≤ 599, medium 600–839, expanded ≥ 840) and their shells; destination order invariant across classes; the detail pane shows the same surface and the same truth; `task_runner_flow` may suppress the shell and its exit is a fixed 48dp target.
- `TRUX-v0 / D-070` — the focused-flow frame, its always-present exit and pause, and protection of an in-flight run from replan.
- `THUX-v0 / D-069` — Today is action-first and owns the current plan; its interior is not 10B's business.
- `SPWX-v0 / D-072` — state must be distinguishable in text, never by colour or icon alone.
- `MSBX-v0 / D-078` — `core-presentation` computes presentation as pure data; `app-ui` only renders.
- `MPSX-v0 / D-082` — the module layout, the pinned adaptive libraries and the semantics APIs named for state text and traversal order.

## 3. Synthesis problems 10B actually has to solve

1. **Where does the navigation rule live?** A Compose `NavHost` full of route strings would put the destination set, its order and the focus-flow return rule inside the UI toolkit, where they could only be tested on a device. `MSBX-v0` already answers this for presentation state; navigation is the same class of decision.
2. **A shared surface must be genuinely single.** `UXIA-v0` forbids "different contradictory Skill detail pages under Learn and Progress". A route registry with one entry per origin makes that violation easy to write; one surface object per canonical entity makes it impossible.
3. **"Anything can open anything" quietly becomes a hierarchy claim.** The IA enumerates contextual edges deliberately. If the shell allowed arbitrary transitions, the browse path a learner happened to take would start looking like prerequisite structure — exactly what §10.2 forbids.
4. **A focused flow must not be a destination and must not lose its exit.** If "shell hidden" and "safe exit present" are two independent flags, a state exists where the shell is hidden and the exit is missing. That state should not be representable.
5. **Adaptive rendering must not become a second IA.** The window class may change how the shell is drawn and nothing else — not the set, not the order, not the meaning.
6. **The return rule is where a replan can strand someone.** A flow opened from an entity should return there, but only while that origin is still valid; otherwise the learner lands somewhere real instead of on a stale surface.

## 4. Positions taken

- **The navigation model lives in `core-presentation`** as pure Kotlin: the destination enum, the surface set, the window-class mapping, the contextual edge set, the shell state and the return rule. `app-ui` renders it.
- **Enum order is the canonical order.** There is no second list to keep in sync, so a reordering has to be a source change.
- **One surface object per canonical entity.** Skill detail is reachable from Today, Learn and Progress and is the same surface each time.
- **Contextual edges are enumerated and closed.** An unlisted transition is not navigable.
- **`showsShell` and `requiresSafeExit` are derived from the surface**, not set by callers, so "focused flow with no exit" cannot be constructed.
- **The window class is computed from the `WFPX-v0` breakpoints in core**, not from the UI toolkit's own bucketing, so the mapping stays canonical even if a library changes its defaults.
- **Turkish destination labels are working microcopy**, owned by 14. What is canonical here is the destination id and its order.
- **Icons never carry meaning alone**: every destination renders a text label, and the icon's content description is null because the label already names it.

## 5. Explicitly not decided in 10B

The interiors of the four destinations (`THUX-v0` Today at 11, Learn/Progress interiors as their features arrive), the design system and theme (10C), persistence and content loading (10D), app health and diagnostics (10E), the task runner and assessment session interiors (11–13), deep links and notification entry points, search UX, and any change to an accepted semantic, boundary or IA decision.

No claim is made here about animation, transition timing or navigation performance.
