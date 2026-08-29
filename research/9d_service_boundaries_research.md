# 9D Module & Service Boundaries — Research & Decision Synthesis

**Stage step:** 9D — Servis sınırları  
**Purpose:** Turn two promises that are currently conventions — a deterministic core that survives AI and network absence, and a planner whose output is reproducible — into structural facts enforced by module boundaries.

## 1. Research-need decision

9D chooses no library, no framework and no external option. Everything it decides is derivable from constraints already accepted:

```text
AMTS-v0: the domain core must not depend on Android, UI, network or AI
+ LFPS-v0: persistence interfaces are core-owned, the platform implements them
+ DDM-v0: the core-visible model uses no platform type
+ V1 criterion 8: the deterministic core must not collapse when the AI Tutor is absent
+ PDT-v0 / PBR-v0: the same inputs must produce the same planner result
+ SPWX-v0: presentation state is a deterministic projection of canonical state
→ module set, port set and dependency direction
```

A separate external Research AI is **not required**. Independent QA is, because a boundary violation is invisible at runtime until the exact moment it matters — the day the network is down, or the day two identical inputs produce two different plans.

Both `AMTS-v0` and `LFPS-v0` explicitly deferred module layout to this step.

## 2. Canonical source set reviewed

- `AMTS-v0 / D-075` — core purity and its forbidden dependency list; `boundary_layout_owner: 9D`.
- `LFPS-v0 / D-076` — core-owned persistence interfaces, platform implementation, `module_layout_owner: 9D`; one learner action is one transaction.
- `DDM-v0 / D-077` — core-visible types, the truth/projection split, and which entities are whose.
- `docs/V1_SCOPE.md` — the deterministic local core must not collapse without the AI Tutor; local-first with no network in the core path.
- `PDT-v0` / `PBR-v0` / `ADAPTIVE_PLANNER_SPEC` §18 — the same curriculum state, mastery/retention state, candidate set, duration estimates, capacity input and planner config version must produce the same result.
- `GRE-v0` / `RVR-v0` / `PRG-v0` / `TSM-v0` / `WLRM-v0` / `TEPM-v0` — each engine owns a distinct piece of state.
- `SPWX-v0` — the derived presentation state is a deterministic projection, and the four axes must remain separately inspectable.
- `TRUX-v0` / `ASUX-v0` — `evaluation_pending` writes no evidence when the evaluator is unavailable.

## 3. Synthesis problems 9D actually has to solve

1. **"The core survives without AI" is currently a promise.** Nothing structurally prevents an engine from importing an AI client tomorrow. A promise that depends on everyone remembering it for years is not a guarantee; a dependency rule is.
2. **Determinism dies quietly.** `ADAPTIVE_PLANNER_SPEC` §18 requires identical inputs to produce identical results. A single call to the system clock or a hash-ordered iteration inside an engine breaks that, and the failure looks like flakiness rather than a bug.
3. **Time is an input, not an ambient fact.** `DDM-v0` requires instant, learner-local study day and offset on every record. If the core reads the system clock directly, that logic is untestable and timezone behaviour becomes unobservable until a user travels.
4. **Presentation logic must not live in the UI toolkit.** `SPWX-v0` makes the presentation state a deterministic projection with a declared precedence. If that computation lives inside Compose, the most safety-critical labelling in the product — what a chip says about a learner's capability — can only be tested on a device.
5. **Engines must not write each other's state.** Mastery, retention, readiness, Topic state, weakness and the English profile each have a canonical owner. A boundary that lets the planner write mastery, or assessment write retention, silently recreates the "second source of truth" every earlier stage forbade.
6. **Transactions need a home.** `LFPS-v0` requires one learner action to commit atomically across attempt, evidence and derived state. That orchestration belongs to a named layer, not scattered across UI handlers.

## 4. Positions taken

- **Ten modules with a strictly inward dependency rule.** `core-*` may never depend on `data-*`, `ai-*` or `app-*`. The graph is acyclic and the rule is checkable, not aspirational.
- **Everything the core needs from outside is a port**: persistence, content, clock and evaluator. Ports live in the core and are expressed in core types.
- **The clock is a port.** This makes the instant/study-day/offset logic testable and makes planner output reproducible.
- **There is no randomness port, because the core contains no randomness.** Ties are broken by a declared total ordering, per `PBR-v0`'s deterministic rank. Adding a seeded random source would make determinism a configuration rather than a property.
- **A null evaluator ships as part of the product.** The app must build and run with the AI adapter absent, in which case open-ended attempts become `evaluation_pending` and write no evidence. That turns V1 criterion 8 from a hope into a wiring fact.
- **Presentation projection is core, not UI.** `core-presentation` computes `SPWX-v0` states and the surface view models as pure data; `app-ui` only renders them.
- **Each engine owns exactly one state family and writes no other.** Cross-engine effects happen by the application layer calling engines in order, never by one engine reaching into another's state.
- **`core-application` owns the transaction boundary** for one learner action.
- **`app-wiring` is the only module that knows every implementation.** It is the composition root and contains no domain logic.

## 5. Explicitly not decided in 9D

The dependency-injection library and how wiring is expressed (10A), the build-system module declaration syntax (10A), test strategy and how the dependency rule is enforced in CI (9F), AI provider and prompt behaviour (9E), concrete class and function names, and any accepted semantic, persistence rule or data model.

No external source in this synthesis justifies a module count target, a layer-depth rule, a package-naming convention or a performance claim.
