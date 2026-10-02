# Provider Adapter Implementation Specification — PRVX-v0

**Stage step:** 14G — Provider abstraction/fallback  
**Status:** ACCEPTED — independent 14G QA PASS  
**Decision:** `D-111`  
**Model:** `PRVX-v0 — Provider Adapter`  
**Behaviour it implements:** `AIAX-v0 / D-079` §5 (schema-constrained output), §6 (refusal is not a wrong answer; stop reason before content), §7 (end-to-end budget, bounded retries, uniform degradation), §8 (provider-independent adapter, router, model in configuration, currency verified not asserted), §9 (call discipline), §10 (no developer key in the APK, no proxy, the learner's own key in platform secure storage, never logged/exported/backed up), §11 (minimum content); `LEARNING_BEHAVIOR_RULES` §17–§18; `MSBX-v0` (`ai-adapter` optional, `core-*` free of network and AI); `TVSX-v0` (no check calls a live provider; the null path is proven by building without the adapter); `TUTX-v0`, `CDEX-v0`, `ACCX-v0`, `OREX-v0` (the instructions, messages and schemas the adapter carries)  
**Persistence:** schema unchanged — the key is not learner data and never enters the store  
**Boundaries:** ports unchanged (the four plus `TutorPort`); network code exists only in `ai-adapter`, and the network permission only in the AI build

## 1. Purpose

14A–14F wrote down, in core, everything an AI may be asked and everything core will accept back; the adapter that would actually ask had no call site. 14G is the first real provider call — the one the learner decided at 14A would come here.

It answers one question:

> **AI gerçekten nasıl çağrılır — ve ürünün hiçbir şeyi ona bağlı kalmadan nasıl çalışır?**

Primary invariant:

> **The provider is a replaceable detail behind the ports.** Only the learner's own key, kept encrypted on this device, can make a call; without it nothing leaves the device and nothing in the product stops working. What is sent is exactly what core built — its instructions, its message, its schema — and nothing the provider says becomes an answer unless it is one schema-exact object; a refusal is a refusal, a timeout ends the call, and nothing is retried behind the learner's back.

---

# 2. What was found before writing adapter code

- **The adapter had no call site** (`AiTutor`, `AiEvaluator` returned `unavailable`), as 10A–14F intended.
- **No instructions existed for evaluating code with an AI** (14D left them to 14G), so the code path had no message to send.
- **The app had no network permission and no way to enter a key**, so even a finished adapter could not have called anything.
- **The provider stores responses by default** (`store` defaults to true, kept at least 30 days per the reference) — more than the minimum `AIAX-v0` §11 allows.

---

# 3. User decisions (2026-10-02)

1. **Provider: OpenAI** (Responses API).
2. **Fallback: one provider, the uniform degradation `AIAX-v0` already defines** — a non-answer leaves an evaluation pending and the tutor falls back to written help; bounded retries inside the same budget; no second provider or model.
3. **Key entry: a small settings screen in 14G** on Profile — enter, remove, test the connection.

---

# 4. Scope boundary

## 4.1 14G decides

- the provider client, its request shape, its reading order and its failure mapping,
- the router (task class → model) and the configuration recorded with the build,
- the end-to-end budget and which failures are retried,
- how the tutor's and the evaluators' replies are read,
- the AI instructions for code (closing 14D's open loop),
- where the key lives and how it is entered, checked and removed,
- the network permission, per build.

## 4.2 14G does not decide

- anything an AI may decide (nothing — `AIAX-v0` §4.2 unchanged),
- calling the tutor or evaluators from the task screens (16D),
- calibration of any evaluator (18), usage or cost tracking,
- a backend or a second provider (a later, explicit decision if ever).

---

# 5. The client (`OpenAiResponses`, `ProviderConfig`, `HttpTransport`)

The reference read on 2026-10-02 and recorded with the build: the Responses API reference (`POST /v1/responses`), the Structured Outputs guide and the model guide. One call sends exactly: `model` (from the router), `instructions` (core's text), `input` (core's message), `text.format` = `json_schema`, `strict: true`, core's schema, and **`store: false`**. The key travels only in the `Authorization` header, only over TLS (an `http://` endpoint is refused).

Reading order (`AIAX-v0` §6): status first — `incomplete` with `content_filter` is `refused`, any other incomplete or non-completed status is `invalid_response`; then a `refusal` content item is `refused` and nothing else is read; only then exactly one `output_text` that parses as one JSON object is an answer. Anything else is `invalid_response`.

| Failure | Outcome | Retried |
|---|---|---|
| no key | `unavailable` — no request is made | — |
| 401 / 403 | `unavailable`, key rejected | never |
| timeout, or budget spent | `timed_out` | never |
| network failure, 408, 429, 5xx | `transport_error` after the attempts | while attempts and budget remain |
| other 4xx | `transport_error` | never |
| refusal / content filter | `refused` | never |
| not exactly one JSON object | `invalid_response` | never |

The budget is end to end (default 60 s, 2 attempts — product defaults in `ProviderConfig`, not scientific values): each attempt gets only what is left. Nothing is retried later in the background. HTTP is the platform's own `HttpURLConnection` and JSON a small strict reader/writer in the adapter — no library is added; nothing in the adapter logs.

---

# 6. The router (`TaskClass`)

`tutor_help`, `open_response_evaluation`, `code_evaluation` — each names its model in `ProviderConfig`. Default: `gpt-6-astra` for all three, because `AIAX-v0` §8.1 places explanation/hint/feedback and open-response evaluation in the quality tier and the reference names it the flagship; switching a class to another model touches configuration only. A request carrying a rubric is an open response; otherwise it is code (the only other evaluator caller).

---

# 7. Reading the replies (`AiTutor`, `AiEvaluator`, `ConnectionCheck`)

- **Tutor:** core's `TutorInstructions` text, message and `tutor_reply/2` schema; a reply is exactly its four fields, each from its closed vocabulary; `TutorRef(openai, model, tutor_instructions/3)`. Whether it may be shown is still `TutorRules.accept` — a reply above the ceiling arrives and is not shown.
- **Open response:** `open_response_instructions/1` / `open_response_evaluation/1`; only findings and catalog labels are read; component results are left empty for core to derive (`OpenResponse.acceptAi`).
- **Code:** the new `code_evaluation_instructions/1` / `code_evaluation/1` in core (one component per named objective `id@vN`, four signals, no grade field, style never counts); an objective the request did not name is no answer, never a guess; `CodeEvaluation.acceptAi` still caps it at provisional.
- **Labels:** only ids from the request's catalog; each is proposed for every targeted Objective and the evidence pipeline keeps it only where the catalog declares it (14B).
- **Evaluator ref:** `openai/<model that answered>@<schema id>` on every row.
- **Connection check:** a fixed instruction and the word `ping` — nothing about any learner — through the same client; it reports works / no key / key rejected / unreachable / timed out / unexpected reply.

---

# 8. The key and the settings screen

The key is encrypted with an AES-GCM key held in the Android Keystore (it never leaves it); only the ciphertext is written, to the no-backup directory; `allowBackup` is off; it never enters the store, so it is never in an export. It is never logged and never shown — the field is masked and cleared once saved. Presence is read on the store thread at startup, so the main thread only reads a flag. Removing it deletes the ciphertext and the Keystore entry: AI turns off, nothing else changes. The screen (Profile) says before asking: AI is optional, where the key stays, what leaves the device, and that removing it deletes nothing.

The **no-AI build has no network permission at all**; the AI build's manifest adds only `INTERNET`. Both builds pass.

---

# 9. Changes to accepted code

- `ai-adapter`: `AiTutor`, `AiEvaluator` get their call sites (the no-client constructors stay unavailable); new `Provider.kt`, `OpenAiResponses.kt`, `Json.kt`, `ConnectionCheck.kt`.
- `core-model`: `CodeEvaluationInstructions` (new).
- `core-presentation`: `AiSettingsPresentation`; `app-ui`: `AiSettingsScreen`; `app-wiring`: `AiSettings` seam, `AiWiring` (withAi / withoutAi), Profile wiring, an AI work thread, the per-build manifest.
- Narrowed gates (declared in the contract): 10E's `E10E-12_test` and 14A's `E14A-12_adapter` accept the renamed test that states the same guarantee; 14A's `E14A-08_adapter_unavailable` and `E14A-08_wiring` check that the no-client path is still unavailable, the tutor file opens no network, and the no-AI build keeps `NullTutor`.

---

# 10. Verification

- Suites: `JsonTest`, `OpenAiResponsesTest`, `AiTutorTest`, `AiEvaluatorTest`, `ConnectionCheckTest` (ai-adapter, scripted transport), `CodeEvaluationInstructionsTest` (core-model), `AiSettingsPresentationTest` (core-presentation).
- The merged manifests were read after each build: the no-AI APK has no `INTERNET` permission; the AI APK has it.
- Validator `tools/validate_provider_adapter.py`, reading `AIAX-v0` §5–§11 and the recorded reference against the Kotlin and the manifests, with its own mutation test.
- Runs: T1, T2, T3, T5 and both builds — all PASS.
- **Mutation:** see the contract's `mutation_results`; a compile failure is not a detection.
- **Not run: T6, and no live provider call.** No check calls a live provider (`TVSX-v0`), and Claude does not enter API keys; the first real call is the learner's, from the settings screen ("Bağlantıyı dene") on the device. The Keystore code runs only on a device.

---

# 11. Open loops

| Loop | Owner |
|---|---|
| the first live call and the Keystore path on the device | the learner (T6) |
| calling the tutor and evaluators from the task screens | 16D |
| calibration of evaluators | 18 |
| usage and cost visibility | 16 / later |

---

# 12. Anti-patterns explicitly rejected

- a developer key in the APK, or a proxy holding one,
- the key in plain storage, a log, a backup or an export, or shown back,
- a call without the learner's key, or a network permission in the no-AI build,
- the provider keeping the call (`store` left on),
- anything but core's instructions, message and schema sent,
- a refusal or a filtered reply read as an answer or a wrong answer,
- free text parsed into a verdict, or an extra field kept,
- a retry after a refusal, an invalid reply, a rejected key or a timeout, or any retry in the background,
- a model name in core.

---

# 13. 14G acceptance contract

1. `PRVX-v0` is the accepted provider adapter.
2. OpenAI, single provider with uniform degradation, settings screen in 14G (user decisions).
3. The key is the learner's, encrypted on the device, never logged, shown, exported or backed up.
4. What is sent is core's, unchanged, not stored by the provider; only a schema-exact object is an answer.
5. No schema change; ports unchanged; four gates narrowed for exactly this step.
6. Nothing is claimed that was not run; T6 and a live call were not run.
7. Independent 14G QA passes and the full `validate_*.py` sweep passes.

---

# 14. Handoff after acceptance

If accepted, 14G becomes `PRVX-v0 / D-111` and **AŞAMA 14 is complete**.

Next numbered step: **15A — Computer / Programming Fundamentals** (AŞAMA 15 — İlk 8–12 Haftalık Gerçek Eğitim İçeriği). It must receive a fresh PRE-STEP and explicit user approval before execution.
