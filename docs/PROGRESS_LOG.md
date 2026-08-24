# Project Progress Log

Bu dosya projenin oturumlar arası kalıcı ilerleme günlüğüdür. Ayrıntılı plan `MASTER_PLAN.md` / `EXECUTION_INDEX.md`; bu dosya ise ne zaman ne yapıldığını ve neden yapıldığını kronolojik olarak kaydeder.

---

### 2026-08-24 — Proje hafızası ve yürütme sistemi kuruldu
- Proje amacı, kariyer rotası, mastery ilkesi, knowledge graph, assessment, retention ve parallel English yönü kalıcı hale getirildi.
- GitHub kalıcı proje hafızası olarak belirlendi.

---

### 2026-08-24 — Master plan 19 aşamalı yürütme planına dönüştürüldü
- Proje 19 ana aşamaya ve acceptance kapılarına ayrıldı.
- Release APK'nin temel ürünün hazır olduğu nokta olduğu netleştirildi.

---

### 2026-08-24 — Aşama 1 tamamlandı
- `1A–1D` ürün amacı, V1 scope, success criteria ve non-goals tamamlandı.

---

### 2026-08-24 — 2A–2D öğrenme/mastery davranışı tamamlandı
- Learning-unit hiyerarşisi, Topic state machine, mastery evidence taxonomy ve AI/hint provenance kuralları kilitlendi.
- Kararlar: D-021, D-023, D-025, D-026.

---

### 2026-08-24 — Zorunlu GitHub beyin tazeleme protokolü kilitlendi
- Her adım PRE-STEP refresh + POST-STEP sync ile yürütülüyor.
- Kararlar: D-024, D-027.

---

### 2026-08-24 — Mobil performans/akıcılık first-class requirement oldu
- Local/incremental/async tasarım ve gerçek cihaz QA yönü bağlayıcı.
- Karar: D-028.

---

### 2026-08-24 — 2E Research AI validation sonrası GRE-v0 finalleştirildi
- Final `GRE-v0 — Gated Recent Evidence`.
- Karar: D-031.

---

### 2026-08-24 — 2F Research AI validation sonrası RVR-v0 finalleştirildi
- Final `RVR-v0 — Retention Verification & Risk`.
- Karar: D-032.
- **AŞAMA 2 tamamlandı.**

---

### 2026-08-24 — 3A Günlük kapasite tamamlandı
- Explicit günlük süre hard budget.
- Remediation/retention günü otomatik uzatmaz.
- Safe split → smaller alternative → defer; deferred task next-day debt değildir.
- Çıktı: `docs/ADAPTIVE_PLANNER_SPEC.md`.
- Karar: D-033.

---

### 2026-08-24 — 3B Görev kategorileri / TaskCandidate contract tamamlandı
- `State → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent` canonical oldu.
- Purpose/activity/track/evidence eksenleri ayrıldı.
- Unresolved LearningNeed kalıcı; old task ID homework debt değildir.
- Çıktı: `docs/TASK_TAXONOMY_SPEC.md`.
- Karar: D-034.

---

### 2026-08-24 — 3C Priority / Selection Policy tamamlandı
- Final `PBR-v0 — Priority Bands & Rank Vector`.
- Eligibility priority'den önce; P0–P4 semantic bands + deterministic rank vector.
- Starvation/track-balance guard; duration semantic priority'den sonra.
- Çıktı: `docs/PRIORITY_POLICY_SPEC.md`.
- Karar: D-035.

---

### 2026-08-24 — 3D Prerequisite davranışı tamamlandı
- Final `PRG-v0 — Prerequisite Readiness Gate`.
- Skill-level hard/soft edge, ready/ready_due/uncertain/not_ready, branch-local gating ve contamination guard kilitlendi.
- Çıktı: `docs/PREREQUISITE_POLICY_SPEC.md`.
- Karar: D-036.

---

### 2026-08-24 — 3E Hızlı öğrenme / validated diagnostic waiver tamamlandı
- Final `VDW-v0 — Validated Diagnostic Waiver`.
- Diagnostic GRE-v0'dan daha kolay ikinci mastery sistemi yapılmadı.
- Objective-level partial coverage waiver, H0/provenance/prerequisite false-skip guard ve diagnostic→replan entegrasyonu kilitlendi.
- Çıktı: `docs/DIAGNOSTIC_WAIVER_SPEC.md`.
- Karar: D-037.

---

### 2026-08-24 — 3F Kaçırılan günler / state-based re-entry tamamlandı
- Final `SRR-v0 — State-based Re-entry & Recovery`.
- Absence negative evidence/debt değildir; stale plan replay edilmez.
- Current-state fresh need/candidate üretimi, due inventory ≠ DailyPlan, starvation≠absence ve bounded capacity recovery kilitlendi.
- Çıktı: `docs/MISSED_DAY_RECOVERY_SPEC.md`.
- Karar: D-038.

---

### 2026-08-24 — 3G Açıklanabilir planner / decision trace tamamlandı

**PRE-STEP**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `ADAPTIVE_PLANNER_SPEC`, `TASK_TAXONOMY_SPEC`, `PRIORITY_POLICY_SPEC`, `PREREQUISITE_POLICY_SPEC`, `DIAGNOSTIC_WAIVER_SPEC` ve `MISSED_DAY_RECOVERY_SPEC` yeniden okundu.
- Aktif adımın 3G olduğu ve 3A–3F'nin canonical olarak kapalı olduğu doğrulandı.
- Ayrı Research AI kullanılmadı; 3G yeni pedagojik threshold seçmek yerine mevcut deterministik planner kararlarını explainability/audit contract'ına bağlayan ürün/mimari adımıydı.

**Final `PDT-v0 — Planner Decision Trace`**
- Planner açıklaması sonradan freeform AI rationale olarak uydurulmaz; karar sırasında structured reason code + decision trace üretilir.
- Explainability private chain-of-thought değildir; yalnız canonical state refs, policy outputs, selection disposition ve decisive reason'lar tutulur.
- Internal audit trace ile user-facing kısa explanation ayrıldı.
- Need-level ve Candidate-level trace contract'ları tanımlandı.
- Selected / blocked / invalid / lower-priority / capacity-deferred / split / smaller alternative / same-need superseded durumları explicit hale geldi.
- Reason code family'leri need, validation, eligibility, retention, priority, capacity, diagnostic, re-entry, selection ve replan namespace'lerine ayrıldı.
- User-facing her factual explanation internal trace'te bulunmak zorundadır.
- PRG eligibility → PBR priority → 3A capacity fit karar sırası korunur; priority blocked candidate'ı kurtaramaz, capacity semantic priority'yi yeniden yazmaz.
- `review_due` forgetting/failure diye; absence debt/failure/starvation diye açıklanamaz.
- Higher-priority task kalan capacity'ye sığmadığı için lower-priority task seçilirse trace gerçek fit nedenini korur.
- `PlannerReplanEvent` + plan versioning tanımlandı; completed evidence korunarak yalnız remaining plan yeniden çözülür.
- LLM yalnız structured trace'i paraphrase edebilir; template fallback zorunlu ve canonical source trace'tir.
- 3A–3G için deterministic end-to-end planner pseudocode yazıldı.
- 3H'nin doğrulayacağı 20 temel invariant tanımlandı.
- Trace bounded/ref-based ve D-028 performans şartıyla uyumlu tutuldu.

**Çıktılar**
- `docs/PLANNER_EXPLAINABILITY_SPEC.md`
- `docs/DECISIONS.md` — D-039
- canonical POST-STEP state dosyaları ve `MASTER_PLAN` senkronlandı.

**Sonraki kesin adım:** `3H — Planner simülasyonu`.
3H başlamadan yeni PRE-STEP GitHub refresh zorunlu.
