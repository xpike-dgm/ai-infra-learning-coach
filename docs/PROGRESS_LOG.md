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
- Final `PDT-v0 — Planner Decision Trace`.
- Structured reason codes, internal/user explanation ayrımı, versioned replan chain ve deterministic planner pseudocode kilitlendi.
- Çıktı: `docs/PLANNER_EXPLAINABILITY_SPEC.md`.
- Karar: D-039.

---

### 2026-08-24 — 3H Planner simülasyonu tamamlandı / AŞAMA 3 kapatıldı
- 8 sanal kullanıcı profil sınıfı ve 16 zorlayıcı scenario çalıştırıldı.
- 20/20 PDT-v0 invariant kontrol edildi.

**Sonuç**
```text
16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical cross-spec contradiction
```

- Bu spec/policy-level PASS'tir; runtime testleri 11F, gerçek cihaz/performance 17E'de ayrıca zorunlu.
- Çıktı: `docs/PLANNER_SIMULATION_SUITE.md`.
- **AŞAMA 3 tamamlandı.**

---

### 2026-08-24 — 4A Günlük mikro değerlendirme tamamlandı

**PRE-STEP**
- `PROJECT_MEMORY_PROTOCOL`, `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `LEARNING_BEHAVIOR_RULES`, `MASTERY_SIGNALS_SPEC`, `AI_ASSISTANCE_EVIDENCE_SPEC`, `MASTERY_FORMULA_V0`, `RETENTION_FORGETTING_SPEC`, `TASK_TAXONOMY_SPEC`, `PREREQUISITE_POLICY_SPEC`, `PLANNER_EXPLAINABILITY_SPEC` ve `ENGLISH_FOUNDATION_RULES` yeniden okundu.
- Aktif adımın 4A olduğu, Aşama 3'ün 3H PASS ile kapalı olduğu doğrulandı.
- Ayrı Research AI kullanılmadı; 4A bilimsel sabit soru/dakika optimumu seçmek yerine mevcut evidence/mastery/planner contract'larını günlük assessment davranışına bağlayan ürün/policy adımıydı.

**Final `DMA-v0 — Daily Micro Assessment`**
- Daily micro assessment zorunlu günlük quiz/kota değildir.
- Fixed soru sayısı, fixed assessment süresi veya günlük yüzde yoktur.
- `practice`, `assess`, `retain`, `diagnose` purpose'ları ayrı tutulur.
- Assessment existing LearningNeed + Objective evidence-gap bağlamından üretilir; ayrı assessment backlog/debt yoktur.
- Assessment intent'leri: `checkpoint`, `mastery_evidence`, `verification`, `integration_check`.
- Objective-matched evidence modality seçilir; düşük capacity evidence standardını düşürmez.
- Mastery/verification için H0 independent measurement varsayılandır.
- H1–H4 yardım öğrenmeye izin verir fakat positive independent mastery değildir; yardım istemek negative H0 evidence değildir.
- Submit sonrası feedback önceki H0 attempt'i geriye dönük contaminate etmez; solution exposure sonrası fresh/unseen recheck gerekir.
- PRG prerequisite fairness assessment öncesi zorunludur; prerequisite contamination target negative evidence değildir.
- Invalid/ambiguous/evaluator-invalid item mastery credit veya penalty üretmez.
- Provisional evaluator critical mastery/remediation kararını tek başına belirleyemez.
- Tek doğru item automatic mastery değildir; tek clean post-mastery failure instant unmastery değildir.
- Coding/debugging/transfer Objective evidence standardı kısa görev uğruna MCQ/recognition'a düşürülemez.
- Multi-Skill task yalnız separately observable/attributable component'lere evidence verir.
- Assessment sonucu canonical `Attempt/Artifact → EvidenceEvent → GRE/RVR → weakness/verification/remediation → PRG/Topic → remaining-plan replan` zincirini kullanır.
- New remediation günü otomatik uzatmaz.
- Technical assessment'ta bilinmeyen English grammar/vocabulary gizli prerequisite olamaz.
- 4B–4E için minimum assessment item/result contract ve `assessment.*` reason-code namespace'i tanımlandı.

**Çıktılar**
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
- `docs/DECISIONS.md` — D-040
- canonical POST-STEP state dosyaları + `MASTER_PLAN` senkronlandı.

**Sonraki kesin adım:** `4B — Haftalık sınav`.
4B başlamadan yeni PRE-STEP GitHub refresh zorunlu.
