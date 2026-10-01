---
type: session-log
status: completed
stage_step: 14C
model: ALEX-v0
decision: D-107
date: 2026-10-01
---

# 14C — Alternatif anlatım

14B (#46) main'e merge edilmişti (54e31a8). Fresh 14C PRE yapıldı; beş kanonik kaynak `14B ✅ / 14C active-not-executed` gösterdi. Kullanıcı açık onay verdi ("14C ile devam et") ve iki ürün sorusunu önerilen seçeneklerle cevapladı.

## Result
- Canonical: `docs/ALTERNATIVE_EXPLANATION_IMPL_SPEC.md`
- Machine-readable: `arch/14c_alternative_explanation/alternative_explanation.yaml`
- QA: `arch/14c_alternative_explanation/qa_report.yaml`
- Stale audit: `arch/14c_alternative_explanation/stale_reference_audit.yaml`
- Research/synthesis: `research/14c_alternative_explanation_research.md`
- Final: `ALEX-v0 / D-107`
- Code: AlternativeExplanationFacts, TutorAsk/TutorRequest form, TutorContext canonicalExplanation, TutorRules.authored, tutor_instructions/2 (core-model); ExplainDifferently, AskTutor.showWritten (core-application); AlternativeExplanationPresentation (core-presentation); ContentPort.explanationsFor (core-ports); [explanation] section (data-curriculum)
- Implementation mutation: 42/42, only the 14C suites running
- T6: çalıştırılmadı

## User decisions
- The learner chooses the explanation form from a menu; nothing is ranked or chosen for them.
- A written explanation first; the tutor only where none fits.

## Found
- `explain_differently` carried no form.
- Written alternative explanations had nowhere to live.
- An AI alternative could not be grounded in the course's own explanation.
- A contrast with the learner's own misconception would have needed learner state to leave the device; it is written-only.
- First mutation run: X08 survived (no menu test with the course's own explanation written); the test was strengthened and the whole set re-run.

## Durable decisions
When an explanation does not land, the method changes — the scope and the truth do not. An AI alternative is grounded, labelled unverified, and never the last word.

## Next
14D — Kod değerlendirme is active-not-executed after POST. Fresh PRE + explicit user approval required.
