---
type: session-log
status: completed
stage_step: 8D
model: ASUX-v0
decision: D-071
date: 2026-08-29
---

# 8D — Assessment Session UX

8C merge edildikten sonra fresh 8D PRE main üzerinden yapıldı; beş kanonik kaynak `8C ✅ / 8D active-not-executed` gösterdi ve kullanıcı açık onay verdi.

## Result
- Canonical: `docs/ASSESSMENT_SESSION_UX_SPEC.md`
- Machine-readable: `ux/8d_assessment_session/session.yaml`
- QA: `ux/8d_assessment_session/qa_report.yaml`
- Stale audit: `ux/8d_assessment_session/stale_reference_audit.yaml`
- Research/synthesis: `research/8d_assessment_session_research.md`
- Final: `ASUX-v0 / D-071`
- Independent QA: 107/107 PASS — 3 assessment scope / 6 result family / 5 recomposition condition / 19 semantic state / 15 forbidden anti-pattern
- Stage 6 / Stage 7 / 8A / 8B / 8C / external-memory regressions: PASS

## Durable decisions
Assessment session bir evidence-collection workflow'udur; gradebook, score-based mastery authority, pass/fail verdict veya ikinci state engine değildir. Üç scope (daily/weekly/monthly) tek interior kullanır ve scope yalnız gösterilen context'tir. Submission birimi atomic evidence boundary'dir; bölünmez ve kısmen puanlanmaz. Submit edilen boundary donar; açık blok içinde submit edilmemiş boundary'ler serbestçe gezilebilir. Skip meşrudur ve incorrect sayılmaz. `h0_required` ve allowed-tools policy cevap öncesi açıklanır; objective-appropriate tool kullanımı H0'ı bozmaz. Yardım engellenmez ve cezalandırılmaz; H3/H4 fresh unseen item gerektirir; recheck planner-owned kalır. Resume'da beş koşullu slot recomposition uygulanır ve completed valid evidence silinmez. Incomplete session partial olabilir; exam debt yoktur. Item dispute evidence'ı contested tutar fakat auto-invalidate etmez ve undo button değildir. Provisional her yerde etiketlidir; invalid ne kredi ne ceza verir. Result altı semantic family kullanır; pass/fail banner, grade, threshold ve broad score yasaktır. `not_reliably_measured` first-class'tır. Diagnostics `task_runner_flow`'da kalır.

## Method note
Validator ilk turda 107/107 geçti. Kendi yazdığım kontratı kendi yazdığım validator ile doğrulamanın zayıflığına karşı ayrıca mutation test uygulandı: 4 kasıtlı ihlal (pass/fail banner, revisitable submitted boundary, `not_reliably_measured` incorrect'e katlama, diagnostics ownership kayması) enjekte edildi ve 5 check FAIL verdi. Dosya geri alındı ve final rapor temiz state üzerinden yazıldı.

## Cross-step finding — tooling lifecycle
POST audit sırasında `tools/audit_*_post_step_stale.py` script'lerinin tasarımı gereği bir sonraki adım tamamlanınca FAIL verdiği netleşti (7E/8A/8B/8C closure audit'leri şu an FAIL). Bu regresyon değildir: closure gate'leri kendi kapanış anının kanıtını dondurur. Ayrım `docs/PROJECT_MEMORY_PROTOCOL.md` §4.4 olarak kalıcılaştırıldı — standing regression sweep yalnız `validate_*.py`; closure audit'leri yalnız kendi POST-STEP'inde çalışır.

## Next
8E — Skill/progress/weakness UX is active-not-executed after POST. Fresh PRE + explicit user approval required before execution.
