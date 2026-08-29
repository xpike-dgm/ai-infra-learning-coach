# Test & Verification Strategy Specification — TVSX-v0

**Stage step:** 9F — Test stratejisi  
**Status:** ACCEPTED — independent 9F QA PASS  
**Decision:** `D-081`  
**Model:** `TVSX-v0 — Test & Verification Strategy`  
**Boundaries:** `MSBX-v0 / D-078`  
**Distribution scope:** `D-080` — personal use, no store distribution

## 1. Purpose

9F defines **how the guarantees accepted in Stage 9 are actually verified, which failures are release-blocking, and what it means for a build to be releasable.**

It answers one primary question:

> **Kabul edilen garantiler nasıl doğrulanır ve bir build ne zaman releasable sayılır?**

Primary invariant:

> **A guarantee that nothing fails on is a preference.** Every accepted invariant has a named check that fails when it is violated; otherwise the invariant survives only as long as everyone remembers it.

---

# 2. Why this step exists

Stage 9 turned promises into structure: the dependency rule makes "the core survives without AI" a property rather than a habit, the schema makes append-only impossible to bypass rather than merely discouraged, and the clock port makes determinism testable rather than hoped for.

Structure alone still decays. A dependency rule with no check is a comment. An append-only schema with no negative test is an assumption about SQL that nobody has confirmed. `LFPS-v0` already states that a migration must be testable against a populated database — that requirement has no owner until this step gives it one.

This step exists to attach an owner to every claim the product makes about itself.

---

# 3. Scope boundary

## 3.1 9F decides

- the verification tiers and what belongs in each,
- how the `MSBX-v0` dependency rule is enforced,
- how `LFPS-v0` append-only, transaction, migration and restore guarantees are verified,
- how the null-evaluator path is verified,
- how planner and projection determinism is exercised,
- how `AIAX-v0` outcomes — including refusal — are verified without a live provider,
- how accessibility and presentation guarantees are checked,
- severity classes and what blocks a release,
- the release gate: what must pass before a build may be called V1,
- required tooling capabilities,
- the standing meta-rules that keep the suite honest.

## 3.2 9F does not decide

- concrete test libraries, versions, build files or CI platform configuration → 10A,
- individual test bodies for features that do not exist yet → 11–18,
- evaluator calibration against human judgement → 18,
- performance budget calibration on the real device → 18E,
- any accepted semantic, persistence rule, data model, boundary or AI integration decision.

No defect-detection rate, coverage number, suite runtime budget or tooling-currency claim is canonical in 9F.

---

# 4. What verification can and cannot establish

## 4.1 Verification can establish

- that an engine computes what its accepted model says it computes,
- that a forbidden operation is actually impossible at the layer that forbids it,
- that the same declared inputs produce the same result,
- that a guarantee still holds after a change,
- that the product still runs with the AI adapter absent.

## 4.2 Verification cannot establish

- that the learning model teaches well,
- that a mastery rule is pedagogically correct,
- that a curriculum decomposition is complete,
- that the learner is actually improving.

A green suite proves the product behaves as its accepted models say. It never proves the models are right; that is what real evidence from real use is for. **No release note, log entry or Progress surface may present a passing suite as evidence about learning quality.**

---

# 5. Verification tiers

Six tiers. Each named check belongs to exactly one.

## T1 — Pure domain

Runs on the JVM with **no device, no network, no database, no clock and no AI**. Covers `core-model`, `core-engines`, `core-application` policy and `core-presentation` projection.

`MSBX-v0` makes this tier possible: engines are pure policy, the clock is a port, and presentation projection lives in core rather than in the UI toolkit.

## T2 — Persistence contract

Runs against the **real storage engine** with a real schema. Covers transactions, append-only enforcement, migration against populated fixtures, backup/export/restore, integrity detection and recovery.

A fake in-memory repository may be used in T1 but **never** substitutes for T2: `DDM-v0`'s guarantees are properties of the schema, and a fake implements the guarantee it is supposed to be testing.

## T3 — Structural

Static analysis of the module graph and signatures, executed in CI on every change. Covers the dependency rule, port purity, absence of randomness in core, and absence of AI/HTTP/network types in `core-*`.

This tier fails at build time, not at runtime, because a boundary violation is a design defect and must not wait for a test to happen to exercise it.

## T4 — Presentation and accessibility

Covers rendering of derived presentation state, tone mapping, contrast, minimum target size, 200% text and Turkish casing. Contrast is **recomputed from the declared hex values**, never asserted from a stored number — the precedent is `WFPX-v0`, whose own validator caught a hand-declared minimum that was wrong.

## T5 — Adapter and integration

Covers `ai-adapter` behaviour against **recorded and synthesized responses only**, the null-evaluator path, and end-to-end flows wired through real ports with a fixed clock.

## T6 — Device smoke

Critical flows exercised on the **single target device** (`D-080`), satisfying V1 criterion 9. This tier is the smallest one: anything that can be verified off-device is verified off-device, because a device-only check is slow, harder to reproduce, and cannot run on every change.

---

# 6. Negative verification is a tier requirement

For every rule that forbids something, there must be a check that **attempts the forbidden thing and requires it to fail**. A check that only exercises the allowed path proves nothing about the prohibition.

Required negative checks:

- an UPDATE and a DELETE against a truth table must be **rejected by the storage layer**, not merely absent from repository code,
- a `core-*` module declaring a dependency on `data-*`, `ai-*` or `app-*` must fail the build,
- a port signature carrying a platform type must fail the build,
- a partial write must not be observable after a failed transaction,
- an interrupted migration must leave the previous state intact and surface `data_recovery_required`,
- a restore from a newer schema must be refused rather than best-effort,
- an evaluator response that does not validate against the schema must produce `invalid_response`, never a verdict,
- a refusal must not produce negative evidence.

---

# 7. Determinism verification

`ADAPTIVE_PLANNER_SPEC` §18 and `PBR-v0` require that the same declared inputs produce the same result. This is exercised, not asserted:

- the clock is injected through `ClockPort` with fixed instants, learner-local study days and UTC offsets; no check reads the system clock,
- planner and projection runs are **repeated** on identical inputs and required to produce byte-identical ordered output,
- tie-breaking is exercised with deliberately tied candidates and required to follow the declared total ordering,
- day-boundary, DST and travel cases are exercised through the three-value time contract of `DDM-v0`,
- no check may introduce randomness; there is no seeded random in core (`MSBX-v0`), so there is nothing to seed.

**A check that only passes sometimes is a failing check.** Retry-to-green is forbidden: in a product whose planner is required to be deterministic, normalising "run it again" would hide exactly the defect class the architecture forbids. A flaky check is quarantined and treated as a release blocker until its nondeterminism is explained.

---

# 8. Persistence verification

## 8.1 Append-only

Verified at the schema level (T2), by attempting UPDATE and DELETE against every truth table and requiring rejection. Correction is verified as an **appended `evidence_disposition` row**, and the original row is required to still be present and unchanged afterwards.

## 8.2 Transactions

One learner action commits as one transaction across attempt, artifact, assistance metadata, provenance, evidence and the resulting derived-state update. Verified by injecting a failure at each stage and requiring that **no partial state is observable**.

## 8.3 Migration

Verified against **populated fixtures**, never only an empty database. For each prior schema version a fixture carrying evidence, exposure, provenance and version pins is migrated forward, and afterwards:

- evidence, exposure and provenance rows are preserved **exactly** — counts and content compared, not sampled,
- derived state may be discarded and rebuilt, and rebuilding is verified to reproduce the same projection from the same truth,
- an interrupted migration leaves the previous state intact and surfaces `data_recovery_required`,
- downgrade is verified to be **unsupported**, not silently attempted.

## 8.4 Backup, export and restore

- export is verified to be complete enough to reconstruct the profile and to record schema and policy version,
- export is verified **not** to contain the API key (`AIAX-v0`),
- restore is verified to be atomic and verified-before-replace: a corrupted or newer-schema archive must leave the existing profile untouched,
- silent merge and silent reset are verified to be impossible.

## 8.5 Exposure

Exposure records are verified to survive migration, restore and derived-state rebuild. `LFPS-v0` classifies their loss as data loss, so an exposure check failure is release-blocking.

---

# 9. AI verification without a live provider

**No check calls a live AI provider.** Live calls are nondeterministic, cost the learner's own money, require their key, and make failures unattributable.

All seven `AIAX-v0` outcomes are reachable from recorded or synthesized responses:

| Outcome | Verified behaviour |
|---|---|
| `evaluated_verified` | evidence written with `evaluator_ref` |
| `evaluated_provisional` | evidence written, status `provisional`, cannot pass a critical gate alone |
| `refused` | `evaluation_pending`, **no evidence**, not a wrong answer |
| `timed_out` | `evaluation_pending`, no evidence, end-to-end budget respected across retries |
| `transport_error` | `evaluation_pending`, no evidence |
| `invalid_response` | `evaluation_pending`, no evidence, no verdict parsed |
| `unavailable` | `evaluation_pending`, no evidence, deterministic capability intact |

Additional required checks:

- a schema-invalid response is treated as an error, and **no free-text parsing path exists** to fall back to,
- the end-to-end timeout budget is verified across retries, not per call,
- the request payload is asserted to contain **only** the minimum content for the current attempt: evidence history, mastery state, plan, profile, exposure, provenance and planner traces must be absent,
- deterministic operations are verified to make **zero** evaluator calls.

---

# 10. Null-evaluator verification

V1 criterion 8 — the deterministic local core must survive the AI Tutor's absence — is verified **by building the product without `ai-adapter`**, not only by stubbing the port.

- the build without the adapter must succeed and the resulting app must run,
- in that build an open-ended attempt becomes `evaluation_pending` and writes no evidence,
- no deterministic capability degrades: planning, assessment scoring against answer keys, mastery, retention, readiness, progress and history all remain fully functional,
- removing a configured key at runtime returns the app to the same path with no data loss.

This is the check that turns criterion 8 from a hope into wiring, and its failure is release-blocking.

---

# 11. Presentation and accessibility verification

- derived presentation state is verified as a **pure projection** in T1: the eight Skill states, the `at_risk` qualifier, declared precedence and the four independent axes,
- `visual_severity <= canonical_severity` is verified for every mapped state, and no learning state may carry the fault tone,
- contrast is **recomputed from hex** for every declared pair in both themes and checked against the WCAG 2.2 anchors used by `WFPX-v0` (4.5:1 text, 3:1 large text and non-text),
- minimum target size is verified at 48dp, including the focused-flow exit,
- layout is verified at 100%, 150% and 200% text without loss of content or function,
- Turkish casing is verified against locale-naive transforms on the locked Skill and Topic labels,
- forbidden presentations are verified absent: pass/fail banner, grade, threshold, mastery percentage, streak and competence progress bar.

---

# 12. Severity classes

| Class | Meaning | Release effect |
|---|---|---|
| `evidence_correctness` | the product would make a wrong claim about what the learner can do, or write/lose evidence incorrectly | always blocking |
| `structural` | a boundary, port, determinism or append-only guarantee is violated | always blocking |
| `data_safety` | migration, restore, exposure or transaction integrity is at risk | always blocking |
| `behavioural` | a flow behaves incorrectly without misrepresenting learner capability | blocking for the affected V1 criterion |
| `presentation` | a labelling, tone, contrast or layout guarantee is violated | blocking when it changes what a surface claims; otherwise tracked |
| `advisory` | style, performance observation, non-guaranteed detail | non-blocking, recorded |

`evidence_correctness` is the highest class deliberately: a wrong claim about the learner is not a cosmetic defect, and this product exists to avoid exactly that.

---

# 13. The release gate

A build may be called V1 only when **all** of the following hold. There is no partial pass.

1. every T3 structural check passes — dependency rule, port purity, no randomness in core, no AI/HTTP/network type in `core-*`,
2. every invariant listed in the invariant register has a named check and that check passes,
3. the persistence suite passes, including migration from every prior schema version against populated fixtures, atomic restore, and exposure preservation,
4. the build **without** `ai-adapter` succeeds, runs, and behaves as §10 requires,
5. all seven AI outcomes behave as §9 requires against recorded responses,
6. determinism checks pass with repeated runs producing identical ordered output,
7. accessibility checks pass — recomputed contrast, 48dp, 100/150/200% text,
8. the ten `V1_SCOPE` release criteria each map to at least one passing check,
9. critical flows pass on the single target device (`D-080`),
10. no check is quarantined as flaky,
11. the repo's `tools/validate_*.py` spec gates pass — the full glob, not a maintained subset.

**Coverage percentage is not a gate.** This project refuses proxy numbers for learner state, and the same reasoning applies here: a percentage can rise while the invariants that matter go unchecked. The gate is **invariant coverage** — every accepted invariant has an owner, and an invariant without an owner blocks the release by itself.

---

# 14. The invariant register

The register is the machine-readable list of accepted invariants and the check that owns each. It is maintained in `arch/9f_test_strategy/test_strategy.yaml` and grows as later stages accept more.

Rules:

- an invariant with no owning check is a **release blocker**, not a backlog item,
- a check may own more than one invariant; an invariant may not be owned by zero,
- removing a check without removing its invariant is a structural failure,
- the register names invariants already accepted in earlier models; **9F does not invent new product semantics**.

---

# 15. Meta-rules that keep the suite honest

- **Mutation discipline.** For every invariant class, a deliberate violation must be shown to make a named check fail. The repo already applies this to its spec validators; the product suite inherits it. A check that passes against a deliberately broken implementation is not a check.
- **No check asserts a hand-computed number** where the value can be computed. Contrast, counts, orderings and hashes are recomputed.
- **Fakes may not implement the guarantee under test.** An append-only fake cannot verify append-only; a fixed-clock fake is fine for determinism because the clock is an input, not the guarantee.
- **No check depends on wall-clock time, locale defaults, network access or a live provider.**
- **A failing check is never disabled to unblock a release.** It is fixed, or the release does not happen.
- **Product tests and `tools/validate_*.py` are separate systems.** The validators guard the accepted specifications; the product suite guards the implementation. Neither substitutes for the other, and the sweep runs the full `validate_*.py` glob rather than a hand-maintained list.

---

# 16. Required tooling capabilities

9F names capabilities, not libraries or versions. Concrete selection and version pinning belong to 10A, where currency is **verified rather than asserted** — the `AMTS-v0` precedent.

Required:

- a JVM unit-test runner for T1 that needs no Android device,
- an execution path for T2 that runs the real storage engine with a real schema and real migrations,
- a static checker able to fail the build on a forbidden module dependency or a platform type in a port signature,
- a UI test harness able to inspect rendered semantics, target size and text scaling off-device where possible,
- an instrumented device runner for T6,
- a build configuration that can produce the product **without** `ai-adapter`,
- a fixture mechanism for recorded AI responses covering all seven outcomes,
- a populated-database fixture mechanism per prior schema version.

No claim is made here that any particular library provides these today; 10A verifies that against a current source and records the reference with the build.

---

# 17. Anti-patterns explicitly rejected

- treating a coverage percentage as a release gate,
- an invariant with no owning check,
- verifying only the allowed path for a rule that forbids something,
- an append-only check that never attempts an UPDATE or DELETE,
- migrating only an empty database,
- a fake implementing the guarantee it is meant to verify,
- calling a live AI provider from a check,
- parsing an evaluation verdict out of free text in a fixture to make a check pass,
- treating a refusal fixture as a wrong answer,
- asserting a per-call timeout as if it were the end-to-end budget,
- reading the system clock inside a check,
- retrying a flaky check until it goes green,
- disabling or quarantining a failing check to unblock a release,
- asserting a hand-computed contrast, count or ordering that could be recomputed,
- presenting a passing suite as evidence about learning quality,
- running a hand-maintained subset of `tools/validate_*.py` and calling it the sweep,
- letting the product suite stand in for the spec validators, or the reverse,
- device-only verification of something verifiable off-device,
- introducing new product semantics inside the test strategy.

---

# 18. 9F acceptance contract

1. `TVSX-v0` is the accepted test and verification strategy for the product.
2. A guarantee that nothing fails on is a preference; every accepted invariant has a named owning check.
3. Six verification tiers, with device verification the smallest.
4. Negative verification is required wherever a rule forbids something.
5. Determinism is exercised through an injected clock and repeated runs; a flaky check is a failing check.
6. Append-only is verified at the schema level by attempted violation.
7. Migrations are verified against populated fixtures with evidence, exposure and provenance preserved exactly.
8. Restore is verified atomic and verified-before-replace; the export is verified to carry no API key.
9. No check calls a live AI provider; all seven outcomes are covered by recorded responses.
10. The null-evaluator path is verified by building without `ai-adapter`.
11. Contrast is recomputed, not asserted; 48dp and 200% text are verified.
12. Six severity classes, with `evidence_correctness` always blocking.
13. The release gate is the eleven conditions of §13, and coverage percentage is not among them.
14. The invariant register is machine-readable and an unowned invariant blocks the release.
15. Mutation discipline applies to the product suite as it already does to the spec validators.
16. Tooling capabilities are named; concrete libraries and versions belong to 10A with verified currency.
17. Device QA means the single target device (`D-080`); store-release verification is out of scope.
18. 9F introduces no new product semantics and changes no accepted model.
19. Independent 9F QA must pass, and Stage 6, Stage 7, AŞAMA 8 and 9A–9E regressions must pass.
20. 10A/11–18/18E boundaries remain open.

---

# 19. Handoff after acceptance

If accepted, 9F becomes `TVSX-v0 / D-081` and **AŞAMA 9 closes**.

Next numbered step: **10A — Mobil iskelet / proje kurulumu**. 10A will pin concrete libraries, build configuration, module layout in the build system, DI wiring and the CI jobs that run the tiers defined here — and it carries two inherited obligations: the six-item bounded verification list handed over by `AMTS-v0` §9, and the target device, which is still not recorded in the repo and is required for `minSdk` confirmation and T6. It must receive a fresh PRE-STEP and explicit user approval before execution.
