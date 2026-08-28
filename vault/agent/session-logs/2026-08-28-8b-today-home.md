---
type: session-log
status: completed
stage_step: 8B
model: THUX-v0
decision: D-069
date: 2026-08-28
---

# 8B — Today / Home UX

Fresh PRE main üzerinde 8A complete / 8B active-not-executed state'ini doğruladı. Kullanıcı 8B execution için açık onay verdi.

## Result
- Canonical: `docs/TODAY_HOME_SCREEN_SPEC.md`
- Machine-readable: `ux/8b_today_home/home.yaml`
- QA: `ux/8b_today_home/qa_report.yaml`
- Research/synthesis: `research/8b_today_home_research.md`
- Final: `THUX-v0 / D-069`
- Independent QA: 90/90 PASS
- Stage 6 / Stage 7 / 8A / external-memory regressions: PASS

## Durable decisions
Today is action-first planner/state projection; queue only selected PlannedTasks; capacity is hard time budget; reasons are PDT-v0 trace-derived; task completion is not mastery; missed day is not debt; assessment and Technical English are contextual; offline/AI-degraded deterministic core remains usable where local capability exists.

## Next
8C — Günlük çalışma akışı is active-not-executed after POST. Fresh PRE + explicit user approval required before execution.
