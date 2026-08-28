---
type: session-log
status: completed
stage_step: 8E
model: SPWX-v0
decision: D-072
date: 2026-08-29
---

# 8E — Progress / Skill / Weakness UX

8D merge edildikten sonra fresh 8E PRE main üzerinden yapıldı; beş kanonik kaynak `8D ✅ / 8E active-not-executed` gösterdi ve kullanıcı açık onay verdi.

## Result
- Canonical: `docs/PROGRESS_SKILL_UX_SPEC.md`
- Machine-readable: `ux/8e_progress_skill_weakness/progress.yaml`
- QA: `ux/8e_progress_skill_weakness/qa_report.yaml`
- Stale audit: `ux/8e_progress_skill_weakness/stale_reference_audit.yaml`
- Research/synthesis: `research/8e_progress_skill_weakness_research.md`
- Final: `SPWX-v0 / D-072`
- Independent QA: 128/128 PASS — 6 owned surface / 8 Skill state / 6 Topic state / 10 semantic state / 14 forbidden anti-pattern
- Stage 6 / Stage 7 / 8A / 8B / 8C / 8D / external-memory regressions: PASS

## Durable decisions
Progress canonical evidence state'in projection'ıdır; mastery engine, score, competence yüzdesi, career tracker veya streak dashboard değildir. `TEPM-v0`nin 8 derived presentation state'i ve precedence'ı bütün Skill'lere genelleştirildi; TEPM-v0 değişmedi ve technical Skill'ler için ikinci vokabüler üretilmedi. `at_risk` attention qualifier'dır, dokuzuncu primary state değildir. Multi-axis truth sıralanır fakat çökertilmez; `skill_detail` mastery/retention/prerequisite/weakness eksenlerini ayrı ayrı incelenebilir tutar. 8 Skill ve 6 Topic Türkçe etiketi kilitlendi; internal ID'ler sabit. Topic state derived orchestration'dır; prerequisite iddiası, Skill ortalaması ve yüzde yoktur. Progress sayabilir fakat puanlayamaz. Yalnız `supported`/`confirmed` weakness gösterilir; AI hypothesis confirmed olamaz; localization yayılmaz. `remediation_task_completed != remediation_closed`. Learning history streak calendar değildir; assessment_report longitudinal'dir ve session'ları score'a toplayamaz.

## Key tensions resolved
1. **İkinci vokabüler riski.** 7E English Skill'leri için 8 state tanımlamıştı; bunlar aynı registry'nin sıradan Skill'leri. Technical için ayrı sistem kurmak tek registry'ye iki çelişkili etiket sistemi verirdi. Genelleştirme seçildi.
2. **`at_risk` vs exactly-8 kontratı.** RVR-v0'da `at_risk` var, TEPM-v0 English'i tam 8 state'e sabitliyor. Dokuzuncu state kabul edilmiş kontratı bozar, düşürmek gerçek sinyali atar. Qualifier çözümü ikisini de korudu.
3. **Ölçek duygusu vs yüzde yasağı.** Kullanıcı ilerleme hissi ister; dürüst cevap oran değil envanterdir. Count'lar etiketli inventory olarak serbest, total'e bölme yasak.

## Method note
Validator kasıtlı olarak self-referential olmaktan çıkarıldı: 8 state ve precedence doğrudan `curriculum/english/7e_mastery_profile/policy.yaml`, Topic state'leri `TOPIC_STATE_MACHINE.md`, weakness lifecycle ve closure kuralı `WLRM`, review/at_risk kuralları `RETENTION_FORGETTING_SPEC.md` ve report family'leri 8D `session.yaml` ile çapraz doğrulanıyor. Ayrıca mutation test: 5 kasıtlı ihlal (dokuzuncu state, mastery yüzdesi, task-completion closure, duplicate Topic etiketi, session aggregation) 8 check FAIL verdi; dosya geri alındı.

## Next
8F — Tasarım sistemi is active-not-executed after POST. Fresh PRE + explicit user approval required before execution.
