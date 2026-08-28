---
type: session-log
status: completed
stage_step: 8C
model: TRUX-v0
decision: D-070
date: 2026-08-29
---

# 8C — Daily Working Flow / Task Runner

Oturum stale bir premise ile açıldı: yerel repo `main`'in 204 commit gerisindeydi ve talep "aktif adım 6D" diyordu. Fresh PRE-STEP refresh 6D'nin `SDM-v0 / D-058` ile çoktan kapandığını, gerçek aktif adımın 8C olduğunu beş kanonik kaynakta (EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN) doğruladı. Tamamlanmış canonical paket yeniden yazılmadı; kullanıcı 8C için açık onay verdi.

## Result
- Canonical: `docs/DAILY_WORKING_FLOW_SPEC.md`
- Machine-readable: `ux/8c_daily_working_flow/flow.yaml`
- QA: `ux/8c_daily_working_flow/qa_report.yaml`
- Stale audit: `ux/8c_daily_working_flow/stale_reference_audit.yaml`
- Research/synthesis: `research/8c_daily_working_flow_research.md`
- Final: `TRUX-v0 / D-070`
- Independent QA: 123/123 PASS — 6 lifecycle phase / 17 semantic state / 3 pause class / 13 forbidden anti-pattern
- Stage 6 / Stage 7 / 8A / 8B / external-memory regressions: PASS

## Durable decisions
Task Runner bir execution surface'tir ve planner, mastery engine, prerequisite engine, evidence evaluator veya scoring device olamaz. Working session emergent ve ungraded'dır; required count/duration/percentage yoktur. Tek bir shared focused-flow frame hem `task_runner_flow` hem `assessment_session_flow` tarafından devralınır; assessment interior 8D'ye aittir. Lifecycle `enter → orient → work → submit → resolve → transition`; entry/resume revalidation deterministiktir ve prerequisite/content-version bypass edilemez. Assistance daima talep edilebilir ve yalnız talep üzerine H1→H4 yükselir; H3/H4 öncesi consequence ölçüm dilinde açıklanır; solution exposure sonrası same-item mastery path yoktur ve recheck scheduling planner-owned kalır. Provenance sorulur, çıkarsanmaz ve dürüst beyan cezasızdır. In-flight run replan'dan korunur; continuity recomputed planner selection kullanır. AI evaluator yoksa attempt `evaluation_pending` olur ve evidence yazılmaz.

## Cross-step finding
POST repo-wide audit, 8C dışı bir stale living gate buldu: `tools/validate_english_entry_diagnostic.py` içindeki `E7A-15` check'i, 7B'nin (`TECP-v0 / D-064`) resolved ettiği `review.6c.english.cefr_alignment` review'ının hâlâ `open` olduğunu iddia ediyordu ve 7A regression'ını FAIL yapıyordu. Check adı ve 7A'nın saklı `qa_report.yaml` kaydı korunarak assertion ownership-handoff'a daraltıldı. 7A canonical spec'i ve kararı değiştirilmedi.

## Next
8D — Sınav UX is active-not-executed after POST. Fresh PRE + explicit user approval required before execution.
