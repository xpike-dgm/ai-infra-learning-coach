---
type: session-log
status: completed
stage_step: 14B
model: WAAX-v0
decision: D-106
date: 2026-10-01
---

# 14B — Yanlış analizi

14A (#45) main'e merge edilmişti (0b4f276). Fresh 14B PRE yapıldı; beş kanonik kaynak `14A ✅ / 14B active-not-executed` gösterdi. Kullanıcı açık onay verdi ("merge edildi devam edebilirsin") ve iki ürün sorusunu önerilen seçeneklerle cevapladı.

## Result
- Canonical: `docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md`
- Machine-readable: `arch/14b_wrong_answer_analysis/wrong_answer_analysis.yaml`
- QA: `arch/14b_wrong_answer_analysis/qa_report.yaml`
- Stale audit: `arch/14b_wrong_answer_analysis/stale_reference_audit.yaml`
- Research/synthesis: `research/14b_wrong_answer_analysis_research.md`
- Final: `WAAX-v0 / D-106`
- Code: MisconceptionFacts, EvaluationResult.misconceptionHypotheses (core-model); MisconceptionEngine (core-engines); AnalyzeWrongAnswer, WeaknessEvents, MisconceptionRows, RecordEvidence tags (core-application); WrongAnswerPresentation (core-presentation); schema v8, misconceptionsOf, tag reading (data-persistence); [misconception] section (data-curriculum)
- Implementation mutation: 51/51, only the 14B suites running
- T6: çalıştırılmadı

## User decisions
- A closed, curriculum-authored misconception catalog; a label outside it is never stored, whoever proposed it.
- A hypothesis is shown only as an open question, right after the wrong answer.

## Found
- `evidence_event.misconception_tags` existed since 10D and nothing wrote or read it.
- The Kotlin evaluation result had dropped AIAX-v0's `misconception_hypotheses[]`.
- There was no misconception memory, owner, escalation rule or catalog.
- Before mutation, three rules had no test (a row no rule speaks for, a label of another version, a label pinned to an earlier published Objective); tests were added first.
- In the first mutation run E03 survived: no test had a fresh recheck failing while the gates still pass; the case was added and the whole set re-run (51/51).
- The sweep found the schema-version gates and two 13D/13F structural gates; v8 was declared as an owned `schema_migration` and the two gates narrowed without weakening them.

## Durable decisions
A wrong answer is information, not a verdict. A label is never stronger than the evidence it rides on or its source; an AI proposal never rises above a hypothesis.

## Next
14C — Alternatif anlatım is active-not-executed after POST. Fresh PRE + explicit user approval required.
