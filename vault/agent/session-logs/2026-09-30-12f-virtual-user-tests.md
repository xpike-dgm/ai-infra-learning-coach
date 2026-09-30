---
type: session-log
status: completed
stage_step: 12F
model: VUSX-v0
decision: D-097
date: 2026-09-30
---

# 12F — Sanal kullanıcı testleri

12E (#37) main'e merge edildi (d41befb); fresh 12F PRE yapıldı ve beş kanonik kaynak `12E ✅ / 12F active-not-executed` gösterdi. Kullanıcı açık onay verdi ("devam et merge edildi").

## Result
- Canonical: `docs/VIRTUAL_USER_TESTS_SPEC.md`
- Machine-readable: `arch/12f_virtual_user_tests/virtual_users.yaml`
- QA: `arch/12f_virtual_user_tests/qa_report.yaml`
- Stale audit: `arch/12f_virtual_user_tests/stale_reference_audit.yaml`
- Research/synthesis: `research/12f_virtual_user_tests_research.md`
- Final: `VUSX-v0 / D-097`; AŞAMA 12 closed
- Tests: VirtualUserScenariosTest (engine), VirtualUserJourneysTest (application), VirtualUserExplanationsTest (presentation), PlannerExplanationTest (grouping)
- Implementation mutation: 27/27, only the virtual-user suites running
- Independent QA: 148/148 PASS; validator mutation 30/30
- T6: çalıştırılmadı

## Found
- A returning learner's due inventory was listed row by row on the explanation; now one entry per recorded reason.
- 3H's S07 example day is illustrative; with a task for every due Skill, urgency fills the day. No rule changed (18C).
- `VDW-v0` has no implementation and no owner; S06 and invariant 12 re-pointed to 13.
- Two scenario tests were weaker than they looked (F01, F06); the validator crashed instead of failing on a missing key (W02).

## Durable decisions
A virtual user is state, never an answer; a scenario that cannot run against real code is named with its owner and never simulated by hand.

## Next
13A — Haftalık sınav is active-not-executed after POST. Fresh PRE + explicit user approval required.
