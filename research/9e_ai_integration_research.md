# 9E AI Integration Architecture — Research & Decision Synthesis

**Stage step:** 9E — AI entegrasyon mimarisi  
**Purpose:** Decide what the AI adapter behind `EvaluatorPort` may do, what it may never do, how it fails, which model it defaults to, and how its credential is handled.

## 1. Research-need decision

Two canonical specs explicitly deferred decisions to this step:

- `LEARNING_BEHAVIOR_RULES.md` §17 — "Model seçimi, maliyet, kalite, latency ve değerlendirme başarısı **9E — AI entegrasyon mimarisi** sırasında kesinleştirilecektir."
- `LEARNING_BEHAVIOR_RULES.md` §18 — "Final güvenlik/proxy/backend kararı **9E**'de teknik olarak kesinleştirilecektir."

So unlike 9B–9D, 9E is required to make a **concrete model selection** and a **concrete credential decision** — while §17 simultaneously requires a provider-independent adapter and forbids binding the product's durable learning behaviour to any model name. Those are compatible: the architecture is provider-neutral, and the selection is a swappable default.

Model and API facts here were taken from the bundled `claude-api` skill reference rather than from recall, because model identifiers, pricing and request shapes drift faster than any fixed knowledge horizon. That reference is itself cached, so re-verification at implementation is a recorded requirement rather than an assumption.

Independent QA is required because this is the one step where an external, non-deterministic system touches a product whose entire premise is that only verified evidence counts.

## 2. Canonical source set reviewed

- `MSBX-v0 / D-078` — `EvaluatorPort` is core-owned; `ai-adapter` is optional; a null evaluator ships; no `core-*` module may reference an AI, HTTP or network type.
- `LEARNING_BEHAVIOR_RULES.md` §16–§18 — the deterministic core list, AI's genuine support areas, "AI cannot bypass a hard prerequisite by saying *I think they learned it*", the provider-independent adapter goal, the rule that simple deterministic work must not make AI calls, and the deferred credential decision.
- `AIV-v0` §3, §18 — generator and validator must be separate; a generator's self-grade and an uncalibrated single-LLM score are **insufficient alone**; open-ended responses evaluable only by an uncalibrated LLM can be structurally valid but cannot reach a critical mastery ceiling.
- 2D assistance/evidence contract — an uncalibrated LLM open-response evaluation is `provisional`; provisional may inform and open a confirmation need but cannot alone pass a critical mastery gate or drive heavy remediation.
- `DMA-v0` §32 — the LLM sets no quota, writes no mastery, bypasses no invalid prerequisite, and does not change planner priority.
- `TRUX-v0` / `ASUX-v0` — when the evaluator is unavailable the attempt becomes `evaluation_pending` and writes **no** evidence; it is neither pass nor fail.
- `DDM-v0` — `evidence_event` carries `evaluator` and `evaluator_status`; provenance must stay reconstructable.
- `LFPS-v0` — evidence is append-only; a later judgement is an appended disposition.
- `V1_SCOPE.md` — local-first, no backend, no realtime cloud sync; the deterministic core must survive the AI Tutor's absence.

## 3. Synthesis problems 9E actually has to solve

1. **A refusal is not a wrong answer.** Safety classifiers can decline a request and return a normal response carrying a refusal stop reason. If the adapter treats any non-answer as a failed evaluation, a learner gets negative evidence because a filter fired on their code sample. That is the single most damaging misread available in this integration.
2. **Free-text parsing is a silent correctness hazard.** An evaluator that returns prose to be regex-parsed will eventually be misparsed, and the misparse looks like a verdict rather than an error. The response must be schema-constrained and rejected when it does not validate.
3. **Timeout budgets are not what they look like.** SDK clients retry by default, so wall-clock can reach the configured timeout multiplied by attempts. A "30 second" timeout can block a learner for minutes unless the budget is defined end-to-end.
4. **Provider independence versus a concrete choice.** §17 demands both. Resolved by putting the model name in configuration and keeping every durable learning semantic independent of it.
5. **The credential question has a real threat model.** Hardcoding a developer key in an APK ships a shared secret to every install. A user-supplied key in platform secure storage is a different situation: the secret belongs to the person holding the device.
6. **Local-first meets a remote API.** This product otherwise never leaves the device. Any evaluator call sends learner-authored content to a third party, so what may leave, and whether the user knows, are architectural decisions rather than implementation details.
7. **AI is genuinely useful and must not be over-restricted.** The point is not to minimise AI; it is to bound its authority. Explanation, hints, misconception hypotheses, variant generation and open-response assistance are real value the product wants.

## 4. Positions taken

- **AI is an assistant behind a port and never an authority.** It cannot write mastery, change retention, bypass a prerequisite, alter planner priority or set a quota.
- **An uncalibrated LLM evaluation is `provisional` by default**, per 2D and `AIV-v0` §18. It may inform and open a confirmation need; it cannot alone pass a critical mastery gate.
- **Evaluator responses are schema-constrained**, and a response that does not validate is an error, not a verdict.
- **Refusal is a first-class outcome distinct from failure**, and maps to `evaluation_pending` — never to negative evidence.
- **Every non-answer degrades identically**: timeout, transport error, refusal, invalid schema and absent adapter all produce `evaluation_pending` with no evidence written.
- **The timeout budget is end-to-end**, covering retries, and is a bounded product default rather than a per-call library setting.
- **The model is named in configuration, not in the core**, with a recorded default and an explicit re-verification requirement.
- **Deterministic work never calls AI**, per §17: answer-key checks, graph locks, planner rules and progress lookups stay local.
- **No developer key ships in the APK and no backend proxy exists in V1.** The learner supplies their own key, stored in platform secure storage, and it never appears in logs, exports or backups.
- **Every AI-derived evidence row records an evaluator reference** — provider, model and prompt/schema version — so a past evaluation stays explicable and re-evaluation is possible.
- **The learner can see and disable AI**, and the product remains fully usable with it off.

## 5. Explicitly not decided in 9E

Prompt text and evaluation rubric wording (14), the AI Tutor's conversational UX (14), calibration of evaluator agreement against human judgement (18), concrete SDK call sites and error-handling code (10A/14), test strategy including how the null-evaluator path is verified (9F), and any accepted semantic, persistence rule, data model or boundary.

No external source in this synthesis justifies an accuracy claim for any model, a latency guarantee, a monthly cost estimate, or a promise that a given model will remain available.
