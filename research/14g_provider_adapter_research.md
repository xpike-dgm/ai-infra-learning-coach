# 14G Provider Adapter — Research & Decision Synthesis

**Stage step:** 14G — Provider abstraction/fallback  
**Purpose:** Make the first real provider call behind the ports, verified against a current provider reference, without anything in the product depending on it.

## 1. Research pass

`AIAX-v0` §8.2 requires the request shape and model identifiers to be confirmed against a current provider reference at the call site and recorded with the build. A web reading pass was made on **2026-10-02** in the built-in browser:

- **Responses API reference** (`developers.openai.com/api/reference/resources/responses/methods/create`): `POST /v1/responses`; `model`, `instructions`, `input`, `text`, `store` (defaults to **true**, kept at least 30 days), `status` / `incomplete_details` in the reply, output messages with `output_text` content.
- **Structured Outputs guide** (`developers.openai.com/api/docs/guides/structured-outputs`): `text.format = {type: "json_schema", name, strict: true, schema}`; every field required, `additionalProperties: false` on every object; a refusal arrives as a separate `refusal` content item that does not follow the schema.
- **Model guide** (`developers.openai.com/api/docs/models`): `gpt-6-astra` (flagship), `gpt-6.1-sol` (near-flagship at lower cost), `gpt-6-luna` (cost tier).

Nothing was sent to the provider; no account or key was used.

## 2. Canonical source set reviewed

- `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md` §4–§11; `docs/LEARNING_BEHAVIOR_RULES.md` §17–§18.
- `docs/SERVICE_BOUNDARIES_SPEC.md` / `arch/9d_service_boundaries/boundaries.yaml` (`ai-adapter` optional); `docs/PROJECT_SETUP_SPEC.md` (no HTTP client until a call site exists).
- `docs/TEST_STRATEGY_SPEC.md` (no live provider in any check; the null path proven by the no-adapter build).
- 14A–14F specs: the instructions, messages and schemas the adapter carries.

## 3. What was found

1. The adapter had no call site; the app had no network permission and no way to enter a key.
2. No AI instructions existed for code (14D's open loop).
3. The provider stores responses by default.

## 4. Positions taken

- **User decisions (2026-10-02):** OpenAI; one provider with `AIAX-v0`'s uniform degradation; a small settings screen in 14G.
- **`store: false`** on every call — the minimum, kept no longer than the call.
- **Default model `gpt-6-astra` for all three task classes**, because `AIAX-v0` §8.1 puts them in the quality tier; changing it touches configuration only.
- **No library added**: `HttpURLConnection` and a small strict JSON reader/writer.
- **The key in the Android Keystore**, ciphertext in the no-backup directory; network permission only in the AI build.
- **Retries:** only network failure, 408, 429, 5xx, inside one end-to-end budget (60 s, 2 attempts — product defaults).

## 5. Explicitly not decided in 14G

Calling the AI from task screens (16D); calibration (18); cost/usage visibility; a backend or a second provider. T6 and the first live call are the learner's, on the device.
