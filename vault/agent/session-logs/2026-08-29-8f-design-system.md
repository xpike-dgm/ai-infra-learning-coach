---
type: session-log
status: completed
stage_step: 8F
model: VDSX-v0
decision: D-073
date: 2026-08-29
---

# 8F — Visual Design System

8E merge edildikten sonra fresh 8F PRE main üzerinden yapıldı; beş kanonik kaynak `8E ✅ / 8F active-not-executed` gösterdi ve kullanıcı açık onay verdi.

## Result
- Canonical: `docs/DESIGN_SYSTEM_SPEC.md`
- Machine-readable: `ux/8f_design_system/design_system.yaml`
- QA: `ux/8f_design_system/qa_report.yaml`
- Stale audit: `ux/8f_design_system/stale_reference_audit.yaml`
- Research/synthesis: `research/8f_design_system_research.md`
- Final: `VDSX-v0 / D-073`
- Independent QA: 121/121 PASS — 6 tone / 46-of-46 surface state / 18 component / 14 forbidden anti-pattern
- Stage 6 / Stage 7 / 8A–8E / external-memory regressions: PASS

## Durable decisions
Design system bir expression layer'dır ve canonical state'in iddia etmediği anlam, severity, aciliyet, sıralama veya hiyerarşi ekleyemez (`visual_severity <= canonical_severity`). Tam altı tone vardır ve tone anlamdan atanır, histen değil. 8A–8E'nin 46 surface state'i, 8 Skill state'i, 6 Topic state'i ve 4 qualifier'ı eksiksiz eşlendi. `system_fault` yalnız `error_recoverable` ve `data_recovery_required`'a izinlidir; hiçbir learning state alarm tonu alamaz. Attention grubunda görünmek tone yükseltmez. Kontrast WCAG 1.4.3/1.4.11'e çapalanır ve tema başına ölçülür; renk asla tek taşıyıcı değildir; dokunma hedefi en az 48dp, içerik %200 metinde kullanılabilir. Türkçe casing korunur ve locale-naive case transform yasaktır. Motion'ın ikna edici rolü yoktur; task completion'da ödül animasyonu yasaktır. Progress-bar yalnız bounded factual konum için kullanılabilir. Somut hex kilitlenmedi; token role, tone eşlemesi ve kontrast kısıtı kilitlendi.

## Key tensions resolved
1. **Severity görsel serbest değişkendi.** `review_due` unutma değil, `remediation_required` failure değil — ama hiçbir şey bunların alarm kırmızısıyla gösterilmesini engellemiyordu ve ekrandaki en güçlü sinyal etiketi yalanlardı. Tone anlamdan atanacak biçimde kurala bağlandı.
2. **Error tonunun sınırı.** Learning state'ler error stilini ödünç alabilseydi, ürünün beş adımda sildiği "geride kaldın" ifadesi palet üzerinden geri gelirdi. Fault tonu iki gerçek arıza state'ine kilitlendi.
3. **Kutlama ve completion.** `task_completed != mastery_confirmed` kurucu invariant; completion'da başarı animasyonu tam da ürünün reddettiği şeyi iddia eder. Yasaklandı; kutlama yalnız gerçek canonical değişimde ve orantılı.
4. **Türkçe casing.** Locale-naive uppercase `i`yi `İ` yerine `I` yapar; "uppercase butonlar" diyen bir design system ürünün kendi kilitli etiketlerini bozardı.

## Method note
Validator tone kapsamını kendi dosyasından değil, 8A–8E yaml kontratlarından hesaplanan state union'ından doğrular — hem eksik hem uydurulmuş state yakalanır. Mutation test: 6 kasıtlı ihlal (learning state'e fault tonu, `stopped_no_penalty`'ye negatif ton, `weakening`'e attention, competence progress-bar, task-completion ödül animasyonu, eşlemeden state düşürme) 11 check FAIL verdi; dosya geri alındı.

## Next
8G — Wireframe/prototip is active-not-executed after POST. Fresh PRE + explicit user approval required before execution. 8G ayrıca somut paleti üretir ve VDSX-v0 §7 kontrast kurallarına karşı tema başına doğrular.
