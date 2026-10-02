# AI Integration Architecture Specification — AIAX-v0

**Stage step:** 9E — AI entegrasyon mimarisi  
**Status:** ACCEPTED — independent 9E QA PASS  
**Decision:** `D-079`  
**Model:** `AIAX-v0 — AI Integration Architecture`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

9E defines **what the AI adapter behind `EvaluatorPort` may do, what it may never do, how it fails, which model it defaults to, and how its credential is handled.**

It answers one primary question:

> **AI nerede gerçekten yardımcı olur ve hangi yetkiyi asla kazanamaz?**

Primary invariant:

> **AI is an assistant behind a port. It never becomes an authority over mastery, retention, prerequisite, planner or curriculum truth, and its absence or failure never produces negative evidence.**

---

## 2. Binding inputs

- `MSBX-v0 / D-078` — `EvaluatorPort` is core-owned, `ai-adapter` is optional, a null evaluator ships, and no `core-*` module may reference an AI, HTTP or network type.
- `LEARNING_BEHAVIOR_RULES.md` §16 — curriculum graph, prerequisite rules, mastery computation, retention, planner candidate/priority logic and progress history stay deterministic; AI's real strengths are alternative explanation, personal hints, help with open-response evaluation, code feedback, root-cause analysis, question variants and transfer/comprehension items; **AI cannot bypass a hard prerequisite by asserting the learner has learned something**.
- `LEARNING_BEHAVIOR_RULES.md` §17 — a provider-independent adapter/router is the goal; durable learning behaviour is not bound to a model name; **model selection is settled in 9E**; deterministic work must not make AI calls.
- `LEARNING_BEHAVIOR_RULES.md` §18 — a hardcoded secret in the APK is not the default design; **the final security/proxy/backend decision is settled in 9E**.
- `AIV-v0` §3, §18 — generator and validator are separate; a generator self-grade and an uncalibrated single-LLM score are insufficient alone; open-ended work evaluable only by an uncalibrated LLM cannot reach a critical mastery ceiling.
- 2D — an uncalibrated LLM open-response evaluation is `provisional`; provisional may inform and open a confirmation need but cannot alone pass a critical mastery gate or drive heavy remediation.
- `DMA-v0` §32 — the LLM sets no quota, writes no mastery, bypasses no prerequisite and does not change planner priority.
- `TRUX-v0` / `ASUX-v0` — an unavailable evaluator yields `evaluation_pending` with no evidence, neither pass nor fail.
- `DDM-v0` / `LFPS-v0` — evidence carries `evaluator` and `evaluator_status`; evidence is append-only and later judgements are appended dispositions.
- `V1_SCOPE.md` — local-first, no backend, no realtime cloud sync; the deterministic core survives the AI Tutor's absence.

---

# 3. Scope boundary

## 3.1 9E decides

- what AI may and may not decide,
- the evaluator contract behind `EvaluatorPort`,
- the outcome taxonomy including refusal,
- failure, timeout and degradation semantics,
- model selection and the router rule,
- call discipline and cost posture,
- credential handling and the backend question,
- what may leave the device,
- `AIV-v0` integration for generated resources,
- evaluator provenance recorded on evidence.

## 3.2 9E does not decide

- prompt text and rubric wording → 14,
- AI Tutor conversational UX → 14,
- evaluator calibration against human judgement → 18,
- concrete SDK call sites and error-handling code → 10A/14,
- test strategy, including verification of the null-evaluator path → 9F,
- any accepted semantic, persistence rule, data model or boundary.

No accuracy claim, latency guarantee, monthly cost estimate or availability promise is canonical in 9E.

---

# 4. What AI may and may not decide

## 4.1 AI may

- produce an alternative explanation of a concept,
- give a hint at a requested assistance level,
- **help evaluate** an open-ended response,
- give code feedback and root-cause analysis,
- propose a misconception hypothesis,
- generate candidate question variants and transfer items.

## 4.2 AI may never

- write or change mastery state,
- change retention state or review scheduling,
- satisfy or bypass a prerequisite,
- change planner priority, rank or capacity,
- set an assessment quota,
- decide that a learner "has learned" something,
- mark its own generated item as validated,
- confirm a weakness as confirmed on its own judgement,
- turn a refusal, timeout or error into a negative result.

```text
AI proposes. Deterministic engines decide.
```

---

# 5. The evaluator contract

## 5.1 Schema-constrained output

An evaluator response is **schema-constrained structured output**, not prose to be parsed.

- The adapter requests a constrained response format and validates the result against the schema.
- A response that does not validate is an **error**, not a verdict.
- Free-text parsing of an evaluation result is forbidden. A misparse looks like a verdict rather than a failure, which is exactly the class of silent error this product cannot tolerate.

Minimum evaluation result shape:

```text
EvaluationResult
- target_objective_refs[]
- component_results[]          # dual-target work stays separable
- outcome_signal               # advisory, never a mastery verdict
- rubric_findings[]
- misconception_hypotheses[]   # hypotheses, never confirmed weakness
- evaluator_confidence?        # advisory only, never a gate
- evaluator_ref                # provider + model + prompt/schema version
```

## 5.2 An uncalibrated LLM evaluation is `provisional`

Per 2D and `AIV-v0` §18:

```text
uncalibrated single-LLM evaluation -> evaluator_status = provisional
```

Provisional evidence may inform the learner, may open a confirmation need, and may not alone pass a critical mastery gate or drive heavy remediation.

`verified` requires a deterministic path — an answer key, objective-specific tests, a compiler or runtime verifier, a deterministic trace checker, a validated benchmark criterion, or a prevalidated rubric under a later calibrated evaluator policy.

## 5.3 The evaluator never returns a mastery verdict

Its output is an input to the evidence pipeline. `GRE-v0` decides what the evidence means.

---

# 6. Outcome taxonomy — refusal is not failure

```text
evaluated_verified      deterministic path succeeded
evaluated_provisional   LLM evaluation succeeded, uncalibrated
refused                 the provider declined to answer
timed_out               end-to-end budget exhausted
transport_error         network or service failure
invalid_response        response failed schema validation
unavailable             no adapter configured, or AI disabled by the learner
```

Binding rule:

```text
refused | timed_out | transport_error | invalid_response | unavailable
    -> evaluation_pending
    -> no evidence written
    -> neither pass nor fail
```

**A refusal is not a wrong answer.** Safety classifiers may decline a request and return a normal response carrying a refusal stop reason. If the adapter treats every non-answer as a failed evaluation, a learner receives negative evidence because a filter fired on their code sample. That is the most damaging misread available in this integration, and it is forbidden explicitly rather than left to implementation judgement.

The adapter must therefore inspect the stop reason before reading content, and must never infer a verdict from an empty or unexpected response.

---

# 7. Failure, timeout and degradation

## 7.1 The budget is end-to-end

SDK clients retry by default, so wall-clock can reach the configured per-request timeout multiplied by the number of attempts. A per-call timeout is therefore not a user-facing guarantee.

Binding rules:

- the adapter declares an **end-to-end budget** covering all attempts,
- the budget is a bounded product default, not a scientific value, and is tunable in configuration,
- when the budget is exhausted the outcome is `timed_out` → `evaluation_pending`,
- retries are bounded and never unbounded or unthrottled,
- a repeated failure does not silently retry in the background against the learner's key.

## 7.2 Degradation is uniform and truthful

- every non-answer degrades the same way: pending, no evidence,
- no deterministic capability degrades when AI is unavailable,
- the learner sees a truthful pending state, never a fabricated result,
- the app remains fully usable with AI disabled.

---

# 8. Model selection and the router

`LEARNING_BEHAVIOR_RULES` §17 requires both a provider-independent adapter and a settled model choice. Both hold:

- the **model name lives in configuration**, never in the core,
- no durable learning semantic depends on a model name,
- the adapter exposes a **router** so different task classes may use different models,
- swapping a model or provider touches the adapter and configuration only.

## 8.1 Default selection

| Task class | Default | Why |
|---|---|---|
| open-response evaluation | quality-tier model | evaluation quality directly affects what the learner is told; this is the least appropriate place to economise |
| explanation, hint, feedback | quality-tier model | learner-facing correctness matters |
| generated-item drafting | cost-tier model | output is untrusted until validated anyway |
| bulk validation of generated items | cost-tier model, batch mode | latency-insensitive and high volume |

The concrete model identifiers are configuration values recorded with the build, taken from a current provider reference at implementation time — not asserted here as permanent.

## 8.2 Currency is verified, not asserted

Model identifiers, pricing, request shapes and capability tiers drift. 9E therefore records a **re-verification requirement**: the concrete identifiers and request shape must be confirmed against a current provider reference at 10A/14, and the reference used must be recorded with the build.

This follows the `AMTS-v0` precedent that library and platform currency is verified rather than asserted.

---

# 9. Call discipline

Per `LEARNING_BEHAVIOR_RULES` §17, simple deterministic work must not make AI calls.

Never calls AI:

- deterministic answer-key checks,
- prerequisite and graph evaluation,
- planner candidate, priority and capacity logic,
- mastery, retention and readiness computation,
- progress and history lookups,
- projection rebuilds.

Cost posture:

- non-latency-sensitive bulk work uses batch processing where the provider offers it,
- stable instruction prefixes are structured for prompt caching,
- an evaluation is not repeated for an unchanged input,
- cost is a configuration and monitoring concern, never a reason to weaken an evidence rule.

---

# 10. Credentials, security and the backend question

`LEARNING_BEHAVIOR_RULES` §18 deferred this here. It is now settled.

## 10.1 Decision

- **No developer or shared API key ships in the APK.** Ever.
- **No backend proxy exists in V1.** V1 is local-first with no server; introducing one solely to hold a key would contradict that and add infrastructure the product otherwise does not need.
- **The learner supplies their own key**, entered in the app's settings.
- The key is stored in **platform secure storage** and never in plain application preferences or files.

## 10.2 The threat model, stated plainly

A user-supplied key on the user's own device is not a leaked secret: the secret belongs to the person holding the device. A hardcoded developer key would be, because it ships one shared secret to every install.

## 10.3 Handling rules

- the key never appears in logs, crash reports, exports, backups or diagnostics,
- the key is never included in the `LFPS-v0` export,
- the key is never sent anywhere except the provider endpoint,
- removing the key returns the app to the null-evaluator path with no data loss,
- the app must be fully usable without a key ever being entered.

If a hosted backend is ever introduced, it is a later, explicitly designed decision with its own step — never an implicit consequence of an implementation choice.

---

# 11. Privacy — what may leave the device

This product otherwise never leaves the device, so an evaluator call is an architectural event.

- only the **minimum content needed to evaluate the current attempt** may be sent,
- learner evidence history, mastery state, the plan and the profile are **never** sent,
- exposure records, provenance and traces are never sent,
- AI use is visible to the learner and can be disabled,
- with AI disabled, nothing leaves the device.

---

# 12. `AIV-v0` integration for generated resources

- a generated item enters **untrusted** and is not usable for high-stakes evidence until validated,
- the **generator and validator are separate**, per `AIV-v0` §3,
- a generator's self-grade is never validation,
- an uncalibrated single-LLM score is never sufficient alone,
- an unvalidated AI-generated item cannot produce strong mastery-changing evidence,
- validation status is recorded on the resource version, per `DDM-v0`.

---

# 13. Evaluator provenance on evidence

Every AI-derived evidence row records an `evaluator_ref` capturing **provider, model and prompt/schema version**, consistent with `DDM-v0`'s `evaluator` field.

Without it, a past evaluation cannot be explained, a model change cannot be reasoned about, and re-evaluation cannot be scoped. With it, a later judgement is an appended `evidence_disposition`, per `LFPS-v0` — never an edit.

---

# 14. Anti-patterns explicitly rejected

- AI writing mastery, retention, readiness, weakness or planner state,
- AI satisfying or bypassing a prerequisite,
- AI setting an assessment quota,
- treating an uncalibrated LLM evaluation as `verified`,
- letting a generator validate its own output,
- an unvalidated AI item producing strong mastery-changing evidence,
- parsing an evaluation verdict out of free text,
- treating a schema-invalid response as a verdict,
- **treating a refusal as a wrong answer**,
- turning a timeout or transport error into negative evidence,
- unbounded or unthrottled retries against the learner's key,
- a per-call timeout presented as an end-to-end guarantee,
- calling AI for deterministic work,
- a hardcoded or shared API key in the APK,
- a key in logs, exports, backups or diagnostics,
- sending evidence history, mastery state, the plan or the profile to a provider,
- binding a durable learning semantic to a model name,
- asserting model currency, accuracy, latency or cost without a current source.

---

# 15. 9E acceptance contract

9E can be accepted only if independent QA verifies at minimum:

1. AI is bounded to assistance and holds no authority over mastery, retention, prerequisite, planner, quota or curriculum truth.
2. The listed AI-may capabilities are preserved, so the integration bounds authority rather than minimising usefulness.
3. Evaluator output is schema-constrained and a schema-invalid response is an error, not a verdict.
4. An uncalibrated LLM evaluation is `provisional`, and `verified` requires a deterministic path.
5. The evaluator returns no mastery verdict.
6. The outcome taxonomy separates refusal from failure, and **refusal never produces negative evidence**.
7. Every non-answer — refusal, timeout, transport error, invalid response, unavailable — yields `evaluation_pending` with no evidence written.
8. The timeout budget is end-to-end across retries and declared as a tunable product default.
9. Retries are bounded and no silent background retry runs against the learner's key.
10. No deterministic capability degrades when AI is unavailable, and the app is usable with AI disabled.
11. The model name lives in configuration, a router exists, and no durable learning semantic depends on a model name.
12. A default selection is recorded per task class with reasoning, plus an explicit currency re-verification requirement.
13. Deterministic work never calls AI.
14. No developer or shared key ships in the APK and no backend proxy exists in V1.
15. The learner supplies the key, it lives in platform secure storage, and it never appears in logs, exports, backups or diagnostics.
16. Only the minimum content needed for the current attempt may leave the device; history, state, plan and profile never do.
17. Generated items enter untrusted, generator and validator are separate, and an unvalidated item cannot produce strong mastery-changing evidence.
18. Every AI-derived evidence row records provider, model and prompt/schema version.
19. No accuracy, latency, cost or availability claim is asserted.
20. 9F/10A/14/18 boundaries remain open.
21. Stage 6, Stage 7, AŞAMA 8, 9A, 9B, 9C and 9D accepted contracts still validate.

---

# 16. Handoff after acceptance

If accepted, 9E becomes `AIAX-v0 / D-079`.

Next numbered step:

**9F — Test stratejisi**

9F will define how these guarantees are actually verified: how the dependency rule is enforced, how append-only and the null-evaluator path are tested, how planner determinism is exercised, how migrations are tested against populated data, and what must pass before a build is considered releasable. It must receive a fresh PRE-STEP and explicit user approval before execution.

---

**14A note (2026-10-01, `D-105`):** the tutor behind its own `TutorPort` follows this contract unchanged — non-answers record nothing, a refusal is never the learner's fault, the reply is schema-constrained (`tutor_reply/1`) and only the current task leaves the device (the message is built in core). The concrete call site, the router and the re-verification of model identifiers (§8.2) are 14G's (user decision). Details: `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md`.

---

**14B note (2026-10-01, `D-106`):** `EvaluationResult` now carries §5.1's `misconception_hypotheses[]` (`misconceptionHypotheses`). They remain proposals: only catalog labels are stored, and a proposal from an uncalibrated evaluator is recorded as `ai_proposed` and never rises above a hypothesis. Details: `docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md`.

---

**14D note (2026-10-01, `D-108`):** code is the first thing evaluated in code. §5.2's deterministic path for code is the course's tests, run on the learner's computer by `tools/code_test_runner.py` and read back as `code_test_report/1`; a test speaks only for its own Objective. Core accepts at most `Provisional` from an evaluator port about code (`CodeEvaluation.acceptAi`): a port that returns `Verified`, or judges an Objective the task does not target, has given an invalid response. An evaluator is asked about code only when the course has no tests for the task and the task allows a provisional result. Details: `docs/CODE_EVALUATION_IMPL_SPEC.md`.

---

**14F note (2026-10-02, `D-110`):** §5.1's `rubric_findings[]` is restored (`EvaluationResult.Provisional.rubricFindings`). For an open response the evaluator proposes one finding per rubric criterion and nothing else is taken from it: core derives each Objective's signal (`OpenResponse.acceptAi`). `EvaluationRequest` gained the rubric and the catalog's labels — curriculum text, never learner data. The evaluator's instructions and schema (`open_response_instructions/1`, `open_response_evaluation/1`) live in core; the adapter (14G) adds transport only. Details: `docs/OPEN_RESPONSE_EVALUATION_IMPL_SPEC.md`.

---

**14G note (2026-10-02, `D-111`):** the adapter has its call sites. Provider: OpenAI Responses API (user decision); request shape and model ids confirmed against the current reference on 2026-10-02 and recorded in `ProviderConfig` (§8.2). Every call sends core's instructions, message and schema with `strict: true` and `store: false`; the stop reason is read before the content; the budget is end to end (60 s, 2 attempts — product defaults); only network failure, 408, 429 and 5xx are retried; nothing is retried in the background. The learner's key is kept encrypted with an Android Keystore key, in the no-backup directory, never shown or logged; the no-AI build has no network permission. Details: `docs/PROVIDER_ADAPTER_IMPL_SPEC.md`.
