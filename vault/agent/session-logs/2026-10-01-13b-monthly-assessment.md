---
type: session-log
status: completed
stage_step: 13B
model: MCAX-v0
decision: D-100
date: 2026-10-01
---

# 13B — Aylık sınav

13A (#39) main'e merge edildi (2d6ea44); fresh 13B PRE yapıldı ve beş kanonik kaynak `13A ✅ / 13B active-not-executed` gösterdi. Kullanıcı açık onay verdi ("sıradki başla").

## Result
- Canonical: `docs/MONTHLY_ASSESSMENT_IMPL_SPEC.md`
- Machine-readable: `arch/13b_monthly_assessment/monthly_assessment.yaml`
- QA: `arch/13b_monthly_assessment/qa_report.yaml`
- Stale audit: `arch/13b_monthly_assessment/stale_reference_audit.yaml`
- Research/synthesis: `research/13b_monthly_assessment_research.md`
- Final: `MCAX-v0 / D-100`
- Code: AssessmentBlueprint (common contract), MonthlyAssessmentFacts, BlueprintCodec (core-model); BlueprintComposer, MonthlyBlueprintEngine, WeeklyBlueprintEngine (core-engines); BlueprintAssessment + BuildDailyPlan (core-application); BlueprintAssessmentSession (core-presentation); schema v4 (data-persistence); role parsing (data-curriculum)
- Implementation mutation: 48/48, only the monthly suites running
- Independent QA: 259/259 PASS; validator mutation 28/28; sweep 44/44
- 622 JVM tests; every 13A test still passes
- T6: çalıştırılmadı

## Found
- The common blueprint contract existed only under weekly names with weekly roles hard-wired; generalised into one contract and one composer, no weekly value changed.
- Schema v3's CHECK covered weekly rows only; schema v4 adds a trigger requiring a blueprint in the row's own scope format, tested against a populated v3 database.
- `cross_topic_transfer` and `professional_evidence_checkpoint` have no state producer; not invented, owner 15.
- First mutation run: M10 survived (block test used two roles whose orders agree); test strengthened and the full set re-run.

## Defaults (not separately asked)
- Monthly cycle = calendar month of the recorded study day — follows the user-confirmed weekly rule; setting 16D.
- Longitudinal window = evidence since the previous monthly blueprint; distinct-day counting left to 18D.

## Durable decisions
A month is a wider window, not a heavier exam: a critical Skill is revalidated only for a reason, a monthly label adds no evidence weight, the month adds no minutes or queue, and a missed month leaves nothing behind.

## Next
13C — Spaced repetition is active-not-executed after POST. Fresh PRE + explicit user approval required.
