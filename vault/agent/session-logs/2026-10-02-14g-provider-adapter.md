---
type: session-log
status: completed
stage_step: 14G
model: PRVX-v0
decision: D-111
date: 2026-10-02
---

# 14G — Provider abstraction/fallback — AŞAMA 14 kapandı

14F (#50) main'e merge edilmişti (ed95916). Fresh 14G PRE yapıldı; beş kanonik kaynak `14F ✅ / 14G active-not-executed` gösterdi. Kullanıcı açık onay verdi ("merge edildi devam et") ve üç ürün sorusunu cevapladı.

## Result
- Canonical: `docs/PROVIDER_ADAPTER_IMPL_SPEC.md`
- Machine-readable: `arch/14g_provider_adapter/provider_adapter.yaml`
- QA: `arch/14g_provider_adapter/qa_report.yaml`
- Stale audit: `arch/14g_provider_adapter/stale_reference_audit.yaml`
- Research/synthesis: `research/14g_provider_adapter_research.md`
- Final: `PRVX-v0 / D-111`; AŞAMA 14: TUTX-v0 → WAAX-v0 → ALEX-v0 → CDEX-v0 → ACCX-v0 → OREX-v0 → PRVX-v0
- Code: ai-adapter (Provider, OpenAiResponses, Json, AiTutor, AiEvaluator, ConnectionCheck); CodeEvaluationInstructions (core-model); AiSettingsPresentation (core-presentation); AiSettingsScreen (app-ui); AiSettings, AiWiring, Profile, manifests (app-wiring)
- Implementation mutation: 41/41, only the 14G suites running
- T6 and a live provider call: not run (the learner's, on the device)

## User decisions
- Provider: OpenAI.
- One provider; AIAX-v0's uniform degradation; no second provider or model.
- A small key settings screen in 14G, on Profile.

## Found
- No call site, no network permission, no key entry; no AI instructions for code; the provider stores responses by default.

## Research
- OpenAI Responses API reference, Structured Outputs guide and model guide read 2026-10-02 in the built-in browser; nothing sent, no account or key used.

## Durable decisions
The provider is a replaceable detail behind the ports. Only the learner's own key, encrypted on the device, can make a call; without it nothing leaves the device.

## Next
15A — Computer / Programming Fundamentals is active-not-executed after POST. Fresh PRE + explicit user approval required.
