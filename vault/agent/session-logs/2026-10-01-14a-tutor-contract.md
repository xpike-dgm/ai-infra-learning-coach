---
type: session-log
status: completed
stage_step: 14A
model: TUTX-v0
decision: D-105
date: 2026-10-01
---

# 14A — Tutor davranış sözleşmesi

13F (#44) main'e merge edilmişti (e332c9a). Takeover okuması ve fresh 14A PRE yapıldı; beş kanonik kaynak `13F ✅ / 14A active-not-executed` gösterdi. Kullanıcı açık onay verdi ("14A ile devam et") ve üç ürün sorusunu önerilen seçeneklerle cevapladı.

## Result
- Canonical: `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md`
- Machine-readable: `arch/14a_tutor_contract/tutor_contract.yaml`
- QA: `arch/14a_tutor_contract/qa_report.yaml`
- Stale audit: `arch/14a_tutor_contract/stale_reference_audit.yaml`
- Research/synthesis: `research/14a_tutor_contract_research.md`
- Final: `TUTX-v0 / D-105` — **AŞAMA 14 başladı**
- Code: TutorFacts, TutorInstructions, AssistanceInterpretation (core-model); TutorPort (core-ports); AskTutor, NullTutor (core-application); TutorPresentation (core-presentation); AiTutor (ai-adapter, unavailable until 14G); TutorProvider (app-wiring); solution exposure read in `evidenceFor` (data-persistence)
- Implementation mutation: 68/68, only the 14A suites running
- T6: çalıştırılmadı

## User decisions
- A separate `TutorPort`, declared as an extension under D-105; the accepted 9D contract is not edited.
- Free questions; while an answer is open the learner chooses how much the reply may reveal.
- No network in 14A; the first real provider call, router and model-currency check are 14G's.

## Found
- There was no tutor in code at all.
- Recorded help never reached the evidence: every caller chose the independence class itself.
- `evidenceFor` never returned a shown solution, so the engines that honour it never saw one.
- The runner's H3/H4 disclosure was untrue after an answer is frozen.
- The first mutation run classified mutants as detected after ~7 seconds each; one was re-run by hand and a named test was seen failing before any result was trusted. One mutant (T02) did not compile; it was rewritten and the whole set re-run from one unchanged tree.
- The sweep found ten more port-count gates than the first grep did (eighteen in all, narrowed to MSBX's four plus the declared extension) and two new tests lowercasing Turkish copy with the default locale (10C's gate); both fixed, the affected mutants re-run.

## Durable decisions
The tutor teaches on request and never decides. Every piece of help it actually shows is recorded for what it is — never less than the learner allowed — and help it did not show is recorded nowhere.

## Next
14B — Yanlış analizi is active-not-executed after POST. Fresh PRE + explicit user approval required.
