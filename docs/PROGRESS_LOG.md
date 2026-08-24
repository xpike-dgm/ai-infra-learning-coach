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

- Bu spec/policy-level PASS'tir; runtime testleri sonraki implementation QA aşamasında, gerçek cihaz/performance ayrıca pilot QA'da zorunlu.
- Çıktı: `docs/PLANNER_SIMULATION_SUITE.md`.
- **AŞAMA 3 tamamlandı.**

---

### 2026-08-24 — 4A Günlük mikro değerlendirme tamamlandı

**PRE-STEP**
- `PROJECT_MEMORY_PROTOCOL`, `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, ilgili mastery/planner/English spec'leri yeniden okundu.
- Aktif adımın 4A olduğu, Aşama 3'ün 3H PASS ile kapalı olduğu doğrulandı.

**Final `DMA-v0 — Daily Micro Assessment`**
- Daily micro assessment zorunlu günlük quiz/kota değildir.
- Fixed soru sayısı, fixed assessment süresi veya günlük yüzde yoktur.
- `practice`, `assess`, `retain`, `diagnose` purpose'ları ayrı tutulur.
- Assessment existing LearningNeed + Objective evidence-gap bağlamından üretilir.
- H0 independent measurement mastery/verification için varsayılandır.
- Assistance/provenance/prerequisite/invalid-item safety korunur.
- Coding/debugging/transfer evidence standardı düşük capacity yüzünden düşürülmez.
- Assessment sonucu canonical evidence→state→replan zincirini kullanır.

**Çıktılar**
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
- `docs/DECISIONS.md` — D-040

**Sonraki kesin adım:** `4B — Haftalık sınav`.

---

### 2026-08-24 — Uzun vadeli curriculum kapsamı professional-readiness hedefiyle genişletildi

**D-041**
- Yaklaşık üç yıllık curriculum horizon'ı kaldırıldı; rota gerektiğinde **4+ yıl veya daha uzun** sürebilir.
- Süre progress/readiness gate'i değildir.
- Final hedef course completion değil verified professional capability.
- Öğretim depth modeli genişletildi.
- V1 full 4+ year content'i beklemeyecek.
- Ürün iş teklifi, seniority, maaş veya diploma/HR filtresi garantisi veremez.

---

### 2026-08-25 — Python foundation eklendi

**D-042**
- Python common programming foundation'a resmi olarak eklendi.
- C/C++ yerine geçmez; automation, testing, benchmark scripting, ML/PyTorch ve infra tooling için tamamlayıcı ana dildir.

---

### 2026-08-25 — D-043 specialization yorumu oluşturuldu

Önceki kullanıcı mesajı yanlış yorumlanarak sona standalone specialization-track aşaması eklenmişti. Bu kayıt tarihsel olarak burada korunur ancak **D-044 ile bu karar geri çekilmiştir**; current canonical plan olarak kullanılmamalıdır.

---

### 2026-08-25 — D-044 Granular Capability Map düzeltmesi / future stage reindex

Kullanıcı asıl isteğinin mesleği uzmanlık dallarına ayırmak değil, **öğrenme rotasındaki her büyük alanı ayrıntılı öğrenme alt bölümlerine parçalayacak ayrı bir planlama aşaması** olduğunu açıkladı.

**PRE-CHANGE GitHub refresh**
- `HANDOFF_STATE`, `EXECUTION_INDEX`, `STEP_STATUS`, `DECISIONS`, `MASTER_PLAN`, `PROJECT_MEMORY_PROTOCOL`, `LEARNING_ENGINE_SPEC` ve `PROFESSIONAL_READINESS_TARGET` yeniden okundu.
- Mevcut learning modelinin zaten `Domain → Module → Topic → Skill → Learning Objective` yapısını ve Skill-level mastery/prerequisite'i desteklediği doğrulandı.
- Aktif yürütme adımının hâlâ `4B — Haftalık sınav` olduğu ve 4B'nin henüz yürütülmediği doğrulandı.

**Plan correction — D-044**
- D-043 standalone specialization stage geri çekildi.
- **AŞAMA 6 — Granular Capability Map / Öğrenme Rotasını Alt Becerilere Böl** eklendi.
- AŞAMA 5 yalnız graph/schema/backbone; AŞAMA 6 full detailed taxonomy olacak.
- Technical English'ten Python, C, systems, distributed, GPU/CUDA/Triton, inference/serving, multi-GPU/AI infra ve open-source/capstone'a kadar bütün rota `Module → Topic → Skill → Objective` seviyesinde haritalanacak.
- Örnek hedef: `Python zayıf` yerine `Python → Control Flow → Loops → while termination` weakness localization.
- Python içinde conditionals, loops, functions, collections, errors, files, testing, typing, packaging, async, multiprocessing, networking, profiling, automation ve ML/infra-facing kullanım gibi family'ler ayrı capability map'e dönüştürülecek; final kapsam Research QA ile doğrulanacak.
- Prerequisite, criticality, evidence type, diagnostic/remediation, retention, cross-domain reuse, project/capstone ve freshness metadata bağlanacak.
- AŞAMA 6 external Research AI coverage/prerequisite validation içerecek.

**Reindex**
- Tamamlanmış AŞAMA 1–4 değişmedi.
- AŞAMA 5 aynı kaldı.
- Yeni AŞAMA 6 eklendi.
- Old future 6–19 birer sıra kaydı: English 7, UX 8, Architecture 9, Skeleton 10, Daily MVP 11, Mastery/Planner implementation 12, Assessment implementation 13, AI Tutor 14, first content 15, analytics 16, polish 17, pilot 18, release 19, full professional curriculum 20.
- Old mistaken specialization AŞAMA 20 kaldırıldı.
- Toplam stage sayısı yine 20.

**Yeni canonical charter**
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`

**POST-SYNC**
- `EXECUTION_INDEX`, `MASTER_PLAN`, `STEP_STATUS`, `HANDOFF_STATE`, `DECISIONS`, `START_HERE`, `PROFESSIONAL_READINESS_TARGET` ve `PROGRESS_LOG` D-044 ile senkronlandı.

**Execution durumu değişmedi:** `4B — Haftalık sınav` aktif ve henüz yürütülmedi. 4B başlamadan fresh PRE-STEP GitHub refresh yine zorunludur.
