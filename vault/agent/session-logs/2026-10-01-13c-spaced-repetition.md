---
type: session-log
status: completed
stage_step: 13C
model: RVRX-v0
decision: D-101
date: 2026-10-01
---

# 13C — Spaced repetition

13B (#40) main'e merge edildi (3788521); fresh 13C PRE yapıldı ve beş kanonik kaynak `13B ✅ / 13C active-not-executed` gösterdi. Kullanıcı açık onay verdi ("merge edildi devam edebilirsin").

## Result
- Canonical: `docs/RETENTION_IMPL_SPEC.md`
- Machine-readable: `arch/13c_spaced_repetition/retention.yaml`
- QA: `arch/13c_spaced_repetition/qa_report.yaml`
- Stale audit: `arch/13c_spaced_repetition/stale_reference_audit.yaml`
- Research/synthesis: `research/13c_spaced_repetition_research.md`
- Final: `RVRX-v0 / D-101`
- Code: RetentionFacts, EvidenceFacts.studyDay (core-model); RetentionEngine (core-engines); RebuildRetention, RefreshDueRetention, BuildDailyPlan (core-application); retentionDueBy (core-ports); schema v5 (data-persistence)
- Implementation mutation: 47/47, only the retention suites running
- T6: çalıştırılmadı

## Found
- Nothing wrote the retention axis; `retention_state` had one column; `EvidenceRow` dropped the study day; no due query existed.
- Mastery is path-dependent, so retention replays the mastery engine after every row exactly as `RebuildMastery` would; tested against `RebuildMastery` itself.
- First mutation run: R25/R26 survived (an engine test built its weak checks before the mastery row, so it proved nothing); R45 survived (SQLite runs the insert guard before an upsert's update). Both tests strengthened; full set re-run.

## Positions (declared, not asked)
- V0 numbers are `RVR-v0` §20's own (heuristic, 18C); growth rounds down; the 1-day separation is a credit rule, not a lock.
- `review_due` is derived from the day; the planner refreshes due schedules before planning.

## Durable decisions
Time passing is not negative evidence: the day only says a scheduled check has come; mastery does not decay, nothing locks, and only a clean independent check moves retention.

## Next
13D — Remediation Engine is active-not-executed after POST. Fresh PRE + explicit user approval required.
