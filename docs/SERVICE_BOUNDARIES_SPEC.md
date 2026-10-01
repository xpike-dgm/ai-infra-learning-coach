# Module & Service Boundaries Specification — MSBX-v0

**Stage step:** 9D — Servis sınırları  
**Status:** ACCEPTED — independent 9D QA PASS  
**Decision:** `D-078`  
**Model:** `MSBX-v0 — Module & Service Boundaries`  
**Platform:** `AMTS-v0 / D-075`  
**Persistence:** `LFPS-v0 / D-076`  
**Data model:** `DDM-v0 / D-077`

## 1. Purpose

9D defines **where each part of the system lives and which direction every dependency points**.

It answers one primary question:

> **Hangi garanti hangi sınırla korunuyor?**

Primary invariant:

> **The boundaries make the guarantees structural. A deterministic core that survives AI and network absence is enforced by the dependency rule, not by remembering.**

---

## 2. Binding inputs

- `AMTS-v0 / D-075` — the domain core has no Android, UI, network or AI dependency; module layout is assigned to this step.
- `LFPS-v0 / D-076` — persistence interfaces are core-owned and the platform implements them; module layout is assigned to this step; one learner action is one transaction.
- `DDM-v0 / D-077` — core-visible types use no platform type; the truth/projection split and each entity's owner.
- `docs/V1_SCOPE.md` — the deterministic local core must not collapse when the AI Tutor is absent; local-first with no network in the core path.
- `ADAPTIVE_PLANNER_SPEC` §18 / `PDT-v0` / `PBR-v0` — identical inputs must produce identical planner results; rank is deterministic.
- `GRE-v0` / `RVR-v0` / `PRG-v0` / `TSM-v0` / `WLRM-v0` / `TEPM-v0` — each engine owns a distinct state family.
- `SPWX-v0` — derived presentation state is a deterministic projection with declared precedence.
- `TRUX-v0` / `ASUX-v0` — `evaluation_pending` writes no evidence when the evaluator is unavailable.

9D changes no accepted semantic, persistence rule or data model, and names no library.

---

# 3. Scope boundary

## 3.1 9D decides

- the module set and each module's responsibility,
- the dependency direction and the forbidden-edge rule,
- the port set the core requires from outside,
- which engine owns which state family,
- where the transaction boundary lives,
- where presentation projection lives,
- how AI absence and determinism are made structural,
- the composition-root rule.

## 3.2 9D does not decide

- dependency-injection library and wiring syntax → 10A,
- build-system module declaration syntax → 10A,
- test strategy and CI enforcement of the dependency rule → 9F,
- AI provider, prompts and evaluation behaviour → 9E,
- concrete class, function or package names,
- any accepted semantic, persistence rule or data model.

No module-count target, layer-depth rule, package-naming convention or performance claim is canonical in 9D.

---

# 4. Module set

```text
core-model          domain types, state vocabularies, logical IDs
core-ports          interfaces the core needs from outside
core-engines        GRE, RVR, PRG, PBR, PDT/planner, TSM, WLRM, TEPM
core-application    use cases and the transaction boundary
core-presentation   SPWX-v0 state projection and surface view models
data-persistence    SQLite adapters implementing persistence ports
data-curriculum     curriculum and resource content adapters
ai-adapter          evaluator/tutor port implementation — optional
app-ui              Compose rendering only
app-wiring          composition root
```

## 4.1 Dependency rule

```text
dependencies point inward only
core-* may never depend on data-*, ai-* or app-*
```

The graph is acyclic. `app-wiring` is the only module permitted to depend on every implementation, and it contains no domain logic.

| Module | May depend on |
|---|---|
| `core-model` | — |
| `core-ports` | `core-model` |
| `core-engines` | `core-model`, `core-ports` |
| `core-application` | `core-model`, `core-ports`, `core-engines` |
| `core-presentation` | `core-model`, `core-engines` |
| `data-persistence` | `core-model`, `core-ports` |
| `data-curriculum` | `core-model`, `core-ports` |
| `ai-adapter` | `core-model`, `core-ports` |
| `app-ui` | `core-model`, `core-presentation`, `core-application` |
| `app-wiring` | all |

This rule is checkable. "The core survives without AI" stops being a promise that everyone must remember for years and becomes a property of the module graph.

---

# 5. Ports

Everything the core needs from outside is a port. Ports live in `core-ports`, are expressed in core types, and expose no platform type.

| Port | Provides | Implemented by |
|---|---|---|
| `PersistencePort` family | truth append, projection read/write, transactions | `data-persistence` |
| `ContentPort` | curriculum and assessment resource content by `(logical_id, version)` | `data-curriculum` |
| `ClockPort` | current instant, learner-local study day, UTC offset | platform, injected |
| `EvaluatorPort` | open-ended evaluation | `ai-adapter`, **or the null implementation** |

## 5.1 The clock is a port

`DDM-v0` requires instant, learner-local study day and offset on every timestamped record, and `ADAPTIVE_PLANNER_SPEC` §18 requires reproducible planner output.

If an engine reads the system clock directly, both fail: the timezone logic becomes untestable, and planner output stops being a function of its declared inputs. Time is therefore an **input**, never an ambient fact.

## 5.2 There is no randomness port

The core contains no randomness. Ties are broken by a **declared total ordering**, consistent with `PBR-v0`'s deterministic rank.

A seeded random source was rejected: it would make determinism a configuration that can be set wrongly, rather than a property that cannot be violated.

---

# 6. AI absence is structural

```text
core-engines  →  EvaluatorPort        (never an AI client)
ai-adapter    →  implements EvaluatorPort
null evaluator→  implements EvaluatorPort, ships with the product
```

Binding rules:

- no `core-*` module may reference an AI client, HTTP client or network type,
- a **null evaluator implementation is part of the shipped product**, not a test fixture,
- the application must build and run with `ai-adapter` absent,
- with the null evaluator, open-ended attempts become `evaluation_pending` and write **no** evidence, per `TRUX-v0` and `ASUX-v0`,
- no deterministic capability degrades when the evaluator is null.

V1 release criterion 8 — "AI Tutor yokken deterministic local core çökmemeli" — is therefore satisfied by wiring rather than by hope, and can be demonstrated by building without the adapter.

---

# 7. Engine ownership

Each engine owns exactly one state family and writes no other.

| Engine | Owns | May read |
|---|---|---|
| `GRE-v0` | mastery state | evidence, curriculum |
| `RVR-v0` | retention state | evidence, mastery |
| `PRG-v0` | prerequisite readiness | mastery, retention, curriculum |
| `TSM-v0` | Topic state | mastery, retention, readiness |
| `WLRM-v0` | weakness and remediation state | evidence, mastery |
| `PBR-v0` | candidate priority and rank | all read-only state |
| `PDT-v0` / planner | plan versions, planned tasks, decision traces | all read-only state |
| `TEPM-v0` | Technical English profile | English Skill state |

Binding rules:

- the planner never writes mastery, retention, readiness or weakness,
- assessment never writes retention directly,
- cross-engine effects happen by `core-application` calling engines in a declared order,
- no engine reaches into another engine's state.

Allowing one engine to write another's state would silently recreate the second source of truth that every earlier stage forbade.

---

# 8. `core-application` owns the transaction

`LFPS-v0` requires one learner action to commit atomically across attempt, artifact, assistance metadata, provenance, evidence and the resulting derived-state update.

That orchestration lives in `core-application`, not in UI handlers and not inside an engine.

```text
use case: submit attempt
→ validate against current state
→ append truth records
→ run engines in declared order
→ write updated projections
→ commit as one transaction
```

Engines remain pure policy: given inputs, produce outputs. They do not open transactions and do not call persistence directly except through ports handed to them by the application layer.

---

# 9. Presentation projection is core

`SPWX-v0` defines the derived presentation state as a deterministic projection with a declared precedence.

`core-presentation` computes it, along with the surface view models for Today, the task runner, the assessment session and Progress, as **pure data**.

`app-ui` renders that data and does nothing else that carries meaning.

If presentation projection lived inside the UI toolkit, the most safety-critical labelling in the product — what a chip claims about a learner's capability — could only be tested on a device. Here it is testable as a pure function.

---

# 10. `app-wiring` is the composition root

- it is the only module that knows every implementation,
- it contains no domain logic, no policy and no state,
- it chooses the real or null evaluator,
- swapping an implementation touches only this module.

---

# 11. Anti-patterns explicitly rejected

- a `core-*` module depending on `data-*`, `ai-*` or `app-*`,
- a cycle anywhere in the module graph,
- an AI client, HTTP client or network type referenced from the core,
- the core reading the system clock directly,
- randomness anywhere in the core, seeded or otherwise,
- an engine writing another engine's state,
- the planner writing mastery, retention, readiness or weakness,
- a transaction opened inside an engine,
- persistence called directly from `app-ui`,
- presentation-state computation inside Compose,
- a platform type in a port signature,
- domain logic in `app-wiring`,
- shipping without a null evaluator,
- treating the null evaluator as a test-only fixture,
- naming a DI or build library in this step.

---

# 12. 9D acceptance contract

9D can be accepted only if independent QA verifies at minimum:

1. The module set is declared with a single responsibility each.
2. The dependency graph is acyclic.
3. No `core-*` module depends on `data-*`, `ai-*` or `app-*`.
4. `app-wiring` is the only module depending on every implementation and holds no domain logic.
5. Every outside need of the core is a port, expressed in core types, with no platform type in any port signature.
6. The clock is a port, with the determinism and timezone rationale recorded.
7. There is no randomness in the core and ties are broken by a declared total ordering.
8. A null evaluator ships with the product and the app builds and runs without `ai-adapter`.
9. With the null evaluator, open-ended attempts become `evaluation_pending` and write no evidence.
10. No deterministic capability degrades when the evaluator is null.
11. Each engine owns exactly one state family and writes no other; the planner writes no learner state.
12. The transaction boundary lives in `core-application` and engines do not open transactions.
13. Presentation projection lives in `core-presentation` and `app-ui` only renders.
14. No library is named and no module-count or performance claim is asserted.
15. 9E/9F/10A boundaries remain open.
16. Stage 6, Stage 7, AŞAMA 8, 9A, 9B and 9C accepted contracts still validate.

---

# 13. Handoff after acceptance

If accepted, 9D becomes `MSBX-v0 / D-078`.

Next numbered step:

**9E — AI entegrasyon mimarisi**

9E will define how the AI adapter behaves behind `EvaluatorPort` and any tutor port: what AI may and may not decide, how provisional evaluation is bounded, how failures degrade, and how the validation requirements of `AIV-v0` are met — without ever giving AI authority over mastery, prerequisite or planner truth. It must receive a fresh PRE-STEP and explicit user approval before execution.

---

**13F note (2026-10-01, `D-104`):** the engine-ownership map is extended by one family, `VDW-v0` → `diagnostic_coverage_state` (`EngineStateFamily.DIAGNOSTIC`); a waiver is not mastery, so it is not folded into another engine's state. The port count stays four (`PersistencePort.latestAssessmentSessionIn` is a refinement). Details: `docs/DIAGNOSTIC_WAIVER_IMPL_SPEC.md`.
