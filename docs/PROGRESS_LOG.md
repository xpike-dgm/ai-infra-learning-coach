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

**PRE-STEP**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `PROJECT_MEMORY_PROTOCOL`, `ADAPTIVE_PLANNER_SPEC`, `TASK_TAXONOMY_SPEC`, `PRIORITY_POLICY_SPEC` ve `RETENTION_FORGETTING_SPEC` yeniden okundu.
- Aktif adımın 3F olduğu, 3A–3E ile GRE/RVR kararlarının current-state/no-debt recovery yönünü zaten bağladığı doğrulandı.
- Ayrı Research AI kullanılmadı; 3F yeni bir bilimsel forgetting threshold'u seçmek yerine mevcut canonical state/priority/capacity modellerini re-entry policy'ye bağlayan ürün/mimari adımıydı.

**Final `SRR-v0 — State-based Re-entry & Recovery`**
- Absence failure, negative mastery evidence, remediation trigger veya task debt değildir.
- Geri dönüşte stale unstarted PlannedTask/TaskCandidate replay edilmez; current state'ten fresh LearningNeed/candidate üretilir.
- Zaman yalnız RVR due/temporal urgency'yi değiştirebilir; review_due sırf uzun ara nedeniyle verification_due/at_risk olmaz.
- Unresolved verification/remediation need'leri absence ile silinmez.
- Safe/version-valid paused checkpoint continuation adayı olabilir fakat PRG/PBR/capacity yeniden değerlendirilir.
- Incomplete high-stakes H0/diagnostic/retention attempt negative evidence değildir; fresh item gerekebilir.
- Due-state inventory DailyPlan değildir; çok sayıda review_due tek güne yığılmaz.
- 1/7/30/60+ gün için ayrı pedagojik threshold yoktur; aynı state-driven resolver çalışır.
- Starvation yalnız eligible need'in aktif planning günlerinde ertelenmesidir; absence günleri starvation artırmaz. Retention overdue age ayrı sinyaldir.
- Integrated recovery task yalnız separately observable/attributable Skill'leri refresh eder; sibling/cluster auto-refresh yoktur.
- Recovery PBR-v0 priority + PRG-v0 eligibility + 3A hard capacity içinde çalışır; user explicit extension olmadan gün uzamaz.
- Long absence safe new learning'i globally dondurmaz.
- Self-report automatic retention refresh değildir; diagnostic/verified artifact normal evidence pipeline'ına girebilir.
- Candidate generation/query bounded/incremental ve D-028 ile uyumludur.

**Çıktılar**
- `docs/MISSED_DAY_RECOVERY_SPEC.md`
- `docs/DECISIONS.md` — D-038
- canonical POST-STEP state dosyaları ve `MASTER_PLAN` senkronlandı.

**Sonraki kesin adım:** `3G — Açıklanabilir planner`.
3G başlamadan yeni PRE-STEP GitHub refresh zorunlu.
