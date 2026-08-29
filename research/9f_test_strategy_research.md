# 9F Test Strategy — Research & Decision Synthesis

**Stage step:** 9F — Test stratejisi  
**Purpose:** Decide how the guarantees accepted in Stage 9 are actually verified, which failures are release-blocking, and what it means for a build to be releasable.

## 1. Research-need decision

9F selects no library, framework or external option whose currency could drift, so it does not require a separate Research AI pass. Every decision here is derived from constraints already accepted: `MSBX-v0`'s dependency rule and ports, `LFPS-v0`'s append-only and migration guarantees, `DDM-v0`'s schema-level enforcement, `AIAX-v0`'s outcome taxonomy, `ADAPTIVE_PLANNER_SPEC` §18 determinism, `WFPX-v0`'s measured palette, `SPWX-v0`'s presentation projection and `V1_SCOPE`'s ten release criteria.

Four canonical specs explicitly deferred to this step:

- `AMTS-v0` §5 — "test strategy and tooling → 9F"
- `LFPS-v0` §5 — "test strategy → 9F"
- `DDM-v0` §5 — "test strategy, including how append-only is enforced and verified → 9F"
- `MSBX-v0` §5 — "test strategy and CI enforcement of the dependency rule → 9F"
- `AIAX-v0` §5 — "test strategy, including verification of the null-evaluator path → 9F"

Independent QA is required because this is the step that decides which failures are allowed to reach the learner.

## 2. Canonical source set reviewed

- `MSBX-v0 / D-078` — ten modules, inward-only dependency rule, four ports, clock as a port, no randomness in core, null evaluator shipped with the product, one state family per engine, transaction boundary in `core-application`, presentation projection in core.
- `LFPS-v0 / D-076` — evidence is truth and derived state is a rebuildable projection; truth records are append-only; one action is one transaction; migration is forward-only, may never rewrite evidence and **is testable against a populated database, not only an empty one**; restore is atomic and verified before replacing; an incomplete migration leaves the previous state intact and surfaces `data_recovery_required`; silent reset is forbidden.
- `DDM-v0 / D-077` — truth tables have no UPDATE/DELETE path; `(logical_id, version)` composite identity; four independent evidence axes as four columns; instant + study day + UTC offset on every timestamped row; projection provenance with policy version and truth watermark.
- `AIAX-v0 / D-079` — seven-outcome taxonomy; refusal is not a wrong answer; every non-answer degrades to `evaluation_pending` writing no evidence; timeout budget is end-to-end; deterministic work never calls AI.
- `ADAPTIVE_PLANNER_SPEC` §18 + `PBR-v0` — same declared inputs produce the same result; ties resolved by a declared total ordering.
- `SPWX-v0 / D-072`, `VDSX-v0 / D-073`, `WFPX-v0 / D-074` — derived presentation state is a deterministic projection with declared precedence; `visual_severity <= canonical_severity`; the palette is measured per theme and contrast is recomputed from hex; 48dp minimum target; 200% text must not break layout; Turkish casing must not use locale-naive transforms.
- `V1_SCOPE` — the ten V1 release criteria, including criterion 8 (the deterministic local core must survive the AI Tutor's absence) and criterion 9 (critical flows pass independent QA on a real Android device).
- `D-080` — personal use, no store distribution: device QA means the single target device, and store-release verification is out of scope.

## 3. Synthesis problems 9F actually has to solve

1. **A guarantee that nothing fails on is a preference.** Stage 9 accepted a set of structural claims. If no named check fails when one is violated, the claim survives only as long as everyone remembers it — which is exactly the failure mode `MSBX-v0` was written to remove. Every accepted invariant needs an owner.
2. **Coverage percentage is the wrong gate.** A percentage is a proxy, and this project has refused proxy numbers everywhere else (no mastery percentage, no gradebook, no streak). A suite can reach a high percentage while asserting nothing about the invariants that matter. The gate must be **invariant coverage**: every listed invariant has a named guarding check.
3. **Green tests can assert nothing.** The repo already learned this at the spec level — validators are mutation-tested, and the 8G validator caught an arithmetic error its author had asserted by hand. The same discipline has to apply to product tests, or the suite becomes decoration.
4. **The most dangerous tests are the negative ones.** Verifying that the right thing works is easy; verifying that the wrong thing is *impossible* is what append-only, the dependency rule and the refusal semantics actually require. An append-only test that only checks "insert works" proves nothing.
5. **Tests must never call a live AI provider.** A live call is nondeterministic, costs the learner's own money, needs their key, and would make the suite fail for reasons unrelated to the code. Every AI outcome — including refusal, timeout, transport error and schema-invalid response — has to be reachable from recorded or synthesized responses.
6. **Migrations are only meaningful against populated data.** `LFPS-v0` already says this. An empty-database migration test passes on a migration that silently drops every evidence row.
7. **Determinism cannot be tested by inspection.** It has to be exercised: fixed clock inputs, repeated runs, identical output including ordering.
8. **Flaky tests are worse than failing tests here.** A product whose planner is required to be deterministic cannot normalise "run it again and it passes". Retry-to-green would hide precisely the class of defect the architecture forbids.
9. **A passing suite is not evidence of pedagogical correctness.** Tests can prove the engine did what the model says. They cannot prove the model teaches well. That is what real evidence from real usage is for, and 9F must say so rather than let a green suite imply more than it means.

## 4. Positions taken

- **Every accepted invariant is owned by a named check**, and the release gate is invariant coverage, not a coverage percentage.
- **Six verification tiers**: pure domain (no device, no network, no database), persistence contract (real storage engine), structural/architecture (dependency rule and platform-type checks, enforced in CI), presentation projection (pure, off-device), UI and accessibility, and device smoke on the single target device.
- **Negative verification is a first-class tier requirement**: forbidden operations must be proven impossible at the layer that forbids them, not merely absent from calling code.
- **The AI adapter is verified against recorded and synthesized responses only.** No test calls a live provider.
- **The null-evaluator path is verified by building the product without `ai-adapter`**, not only by stubbing it — this is what turns V1 criterion 8 from hope into wiring.
- **Migrations are verified against populated fixtures** at each prior schema version, with evidence, exposure and provenance preserved exactly and derived state allowed to be rebuilt.
- **Determinism is exercised, not asserted**: injected clock, repeated runs, identical ordered output.
- **A flaky check is a failing check.** Retry-to-green is forbidden.
- **Evidence-correctness failures are the highest severity class** and are always release-blocking, because a wrong claim about what the learner can do is not a cosmetic defect.
- **Test strategy does not decide learning semantics**, and a green suite is never presented as proof of teaching quality.
- **Product tests and the repo's `tools/validate_*.py` spec gates are separate systems** and neither substitutes for the other.

## 5. Explicitly not decided in 9F

Concrete test-library selection and versions, build/CI platform configuration and DI wiring (10A), individual test case bodies for features that do not exist yet (11–18), evaluator calibration against human judgement (18), performance budget calibration on the real device (18E), and any change to an accepted semantic, persistence rule, data model, boundary or AI integration decision.

No source in this synthesis justifies a claimed defect-detection rate, a claimed coverage number, a runtime budget for the suite, or a promise that any listed tooling is current — currency is verified at 10A, not asserted here.
