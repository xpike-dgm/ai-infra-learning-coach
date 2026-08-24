# AI Infra Learning Coach — Master Geliştirme Planı

**Durum:** AKTİF / CANONICAL DETAYLI PLAN  
**Son senkron:** 2026-08-24

Sabit adım kimliklerinin canonical kaynağı `docs/EXECUTION_INDEX.md`; bu dosya ayrıntılı checklist ve completion notlarıdır.

Ana ilke:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

## Zorunlu yürütme

`PRE-STEP GitHub refresh → gerekiyorsa Research/Coding/QA → spec/çıktı → kabul kontrolü → POST-STEP GitHub sync → MASTER_PLAN checkbox/completion note → sonraki adım`

Tamamlanan `[x]`, bekleyen `[ ]`, aktif adım **AKTİF**.

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle ✅

### [x] 1A — Ana ürün amacı
Ürün amacı, günlük değer, course/todo farkı, kanıtlanmış öğrenme, kariyer rotası.  
**Çıktı:** `docs/PRODUCT_REQUIREMENTS.md`

### [x] 1B — V1 kapsamı
Android kişisel kullanım, V1 içi/dışı, auth/payment/social/admin sınırı, ilk 8–12 haftalık production curriculum.  
**Çıktı:** `docs/V1_SCOPE.md`

### [x] 1C — Başarı kriterleri
P0/P1/P2 acceptance; planner/mastery/prerequisite/retention/replan/data reliability; bağımsız QA.  
**Çıktı:** `docs/V1_SUCCESS_CRITERIA.md`

### [x] 1D — Non-goals
Sabit takvim, streak/time mastery, full mobile IDE identity, SaaS/social/payment scope creep, sahte bilimsel kesinlik yok.  
**Çıktı:** `docs/NON_GOALS.md`

> **Tamamlanma notu — 2026-08-24:** Aşama 1 kapandı.

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla

## Amaç
`Dersi tamamladı = öğrendi` hatasını kaldıran açıklanabilir, deterministik ve retention-aware öğrenme motoru tasarlamak.

### [x] 2A — Bilgi birimleri
- `Domain → Module → Topic → Skill → Learning Objective`
- Domain/Module/Topic organizasyon; Skill/Objective measurement.
- Skill canonical mastery/prerequisite.
- Topic/Module/Domain derived.
- Skill→Skill runtime prerequisite.
- Topic↔Skill many-to-many.

**Çıktı:** `docs/LEARNING_ENGINE_SPEC.md`  
**Karar:** D-021.

### [x] 2B — Topic durumları
- `locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`.
- Coverage/mastery ayrımı.
- Started Topic prerequisite regression yüzünden geriye dönük locked olmaz.
- Deterministic reason/history.

**Çıktı:** `docs/TOPIC_STATE_MACHINE.md`  
**Karar:** D-023.

### [x] 2C — Mastery sinyalleri
- concept recognition/recall.
- code reading/output.
- coding/production.
- debugging/diagnosis.
- explanation/justification.
- transfer.
- retention.
- integrated project.
- contextual time/completion/streak/self-confidence mastery değil.
- prerequisite validity, dedup, evidence independence/diversity.

**Çıktı:** `docs/MASTERY_SIGNALS_SPEC.md`  
**Karar:** D-025.

### [x] 2D — AI / ipucu etkisi
- H0–H4.
- assistance timing.
- artifact origin/provenance.
- independent / assisted / practice-only / requires-recheck.
- generated/copied production mastery değil.
- H3/H4 sonrası fresh/unseen independent recheck.
- compiler/test/docs/autocomplete objective-specific tool policy.

**Çıktı:** `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`  
**Karar:** D-026.

### [x] 2E — Mastery formülü v0

**Research:** Mastery Learning, BKT ve IRT karşılaştırıldı. BKT threshold'larının bağlama bağlı olduğu; item difficulty'nin gerçek veride kalibre edilmesi gerektiği nedeniyle V1'de sahte precision'dan kaçınıldı.

**Canonical score:**

```text
alpha = 1 + Σ(w_i × q_i)
beta  = 1 + Σ(w_i × (1-q_i))
objective_score = alpha / (alpha + beta)
```

- `q_i`: rubric quality `[0,1]`.
- `w_i = role × assistance × provenance`.
- direct `1.00`, corroborating `0.50`, contextual `0`.
- H0/H1/H2/H3/H4: `1.00 / 0.85 / 0.65 / 0.35-or-0 / 0`.
- operational threshold `0.80`; probability veya `% learned` değildir.
- difficulty numeric multiplier değil; critical gate / item eligibility input'u.
- same-item/family inflation engellenir.
- standard/critical Objective hard gate'leri.
- critical production: en az bir H0 user-authored direct artifact.
- all required/critical Objective gates before Skill mastered.
- single post-mastery negative → `verification_due`, instant reset yok.
- confidence bands + decision trace + versioning.
- D-028'e uygun incremental aggregate yönü.
- v0 constants 17C pilotta kalibre edilebilir.

**Çıktı:** `docs/MASTERY_FORMULA_V0.md`  
**Karar:** D-029.

> **Tamamlanma notu — 2026-08-24:** 2E research + deterministic formula + gates tamamlandı. 2F retention/time modeline geçildi.

### [ ] 2F — Unutma modeli — **AKTİF**

2F başlamadan PRE-STEP refresh + Research AI/dış research.

Tasarlanacak:
- spaced repetition model seçimi.
- ilk review interval'leri.
- successful delayed retrieval sonrası interval growth.
- failed delayed retrieval sonrası interval/remediation.
- time-based retention risk/decay.
- `mastered → weakening → mastered/remediation_required` exact trigger.
- natural reuse'un retention evidence sayılması.
- one-error-no-reset kuralı.
- 2E score + retention state birlikte kullanım.

**Beklenen çıktı:** `docs/RETENTION_FORGETTING_SPEC.md` veya eşdeğer canonical spec.

### Aşama 2 kapanış kapısı
Aynı evidence/retention geçmişi verildiğinde Skill state deterministik ve açıklanabilir hesaplanmalı; assistance, confidence ve forgetting ayrı ama entegre modellenmiş olmalı.

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla

### [ ] 3A — Günlük kapasite
Kısa/normal/yoğun gün, hedef süre, minimum block, overflow.

### [ ] 3B — Görev kategorileri
Yeni konu, remediation, retention, coding, debugging, English, project, micro assessment.

### [ ] 3C — Öncelik puanı
Critical prerequisite, due retention, weak Skill, next eligible Topic, English, diversity.

### [ ] 3D — Prerequisite davranışı
Hard/soft; dependent waits, independent continues.

### [ ] 3E — Hızlı öğrenme
Diagnostic/skip, no single-easy-quiz skip, validated waiver, retention.

### [ ] 3F — Kaçırılan günler
1 gün / birkaç gün / 1 hafta+; backlog dump yok; current-state replan.

### [ ] 3G — Açıklanabilir planner
Reason codes + “neden bugün?” açıklaması.

### [ ] 3H — Planner simülasyonu
Hızlı öğrenen, tek temelde takılan, English/technical asimetri, sık ara, yoğun AI yardımı dahil sanal profiller.

**Çıktı:** `docs/ADAPTIVE_PLANNER_SPEC.md`, decision table, pseudocode, simulation suite.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla

### [ ] 4A — Günlük mikro değerlendirme
Quiz, short answer, code reading, coding, debugging, explanation, retrieval.

### [ ] 4B — Haftalık sınav
Yeni/eski balance; coding/debugging/English; result→replan.

### [ ] 4C — Aylık yeterlilik sınavı
Comprehensive Skill evidence, retention, program change.

### [ ] 4D — Soru bankası
Target Skill/Objective, prerequisites, difficulty/type, variant family, rubric, tools.

### [ ] 4E — AI-generated soru doğrulaması
Correctness, ambiguity, prereq eligibility, target fit, validator/rubric, trust.

**Çıktı:** `docs/ASSESSMENT_SYSTEM_SPEC.md`, schema, templates, rubrics.

---

# AŞAMA 5 — Curriculum ve Knowledge Graph

### [ ] 5A — Ana domain haritası
Technical English, Computer Fundamentals, C, Linux, DSA, Modern C++, OS/Memory, Concurrency, Networking, Distributed Systems, GPU, CUDA, Triton, ML/LLM Systems, Inference, Multi-GPU, AI Infrastructure, Open Source/Career.

### [ ] 5B — Topic metadata
Objectives, Skill links, prerequisites, effort, criticality, evidence profile, remediation, career relevance.

### [ ] 5C — İlk 8–12 haftalık graph
Computer Fundamentals, C, Memory, Linux, first DSA, parallel English, DAG.

### [ ] 5D — Curriculum QA
Missing prereq, cycle, difficulty placement, ROI, career relevance.

**Çıktı:** `docs/CURRICULUM_GRAPH_SPEC.md`, dataset, QA.

---

# AŞAMA 6 — İngilizce Paralel Hat

Bağlayıcı ön kural: `docs/ENGLISH_FOUNDATION_RULES.md`.

### [ ] 6A — Başlangıç ölçümü
### [ ] 6B — A1/A2/B1/B2 teknik hedefleri
### [ ] 6C — Günlük English bileşeni
### [ ] 6D — Teknik entegrasyon
### [ ] 6E — English mastery

**Çıktı:** `docs/ENGLISH_TRACK_SPEC.md`, first 12-week English curriculum, level criteria.

---

# AŞAMA 7 — Ekranlar ve UX

### [ ] 7A — Bilgi mimarisi
### [ ] 7B — Ana ekran
### [ ] 7C — Günlük çalışma akışı
### [ ] 7D — Sınav UX
### [ ] 7E — Skill/progress UX
### [ ] 7F — Tasarım sistemi
### [ ] 7G — Wireframe/prototip

**Çıktı:** `docs/UX_SPEC.md`, screen inventory, navigation, wireframes, design system.

---

# AŞAMA 8 — Teknik Mimari ve Veri Modeli

### [ ] 8A — Mobil teknoloji seçimi
### [ ] 8B — Veri saklama / local-first
### [ ] 8C — Domain veri modeli
### [ ] 8D — Servis sınırları
### [ ] 8E — AI entegrasyon + model/router + API key/proxy + code execution/VDS kararı
### [ ] 8F — Test ve performance stratejisi

D-028 gereği startup/jank/memory/battery/DB/network performansı first-class acceptance alanıdır.

**Çıktı:** `docs/TECH_ARCHITECTURE.md`, `docs/DATA_MODEL.md`, ADR/diagram.

---

# AŞAMA 9 — Mobil Proje İskeleti

> **Ana production kodlamasının başladığı aşama.**

### [ ] 9A — Proje kurulumu
### [ ] 9B — Navigation
### [ ] 9C — Design system implementation
### [ ] 9D — Local database
### [ ] 9E — Temel uygulama sağlığı

---

# AŞAMA 10 — Günlük Öğrenme MVP

### [ ] 10A — Today
### [ ] 10B — Task runner
### [ ] 10C — Session state
### [ ] 10D — Mikro quiz
### [ ] 10E — Gün sonu

---

# AŞAMA 11 — Mastery ve Planner Implementasyonu

### [ ] 11A — Mastery Engine v1
### [ ] 11B — Prerequisite Engine
### [ ] 11C — Planner Engine v1
### [ ] 11D — Replan
### [ ] 11E — Explanation/reason codes
### [ ] 11F — Sanal kullanıcı testleri

---

# AŞAMA 12 — Exam / Retention / Remediation Implementasyonu

### [ ] 12A — Haftalık sınav
### [ ] 12B — Aylık sınav
### [ ] 12C — Spaced repetition
### [ ] 12D — Remediation Engine
### [ ] 12E — Program değişiklik raporu

---

# AŞAMA 13 — AI Tutor

### [ ] 13A — Tutor davranış sözleşmesi
### [ ] 13B — Yanlış analizi
### [ ] 13C — Alternatif anlatım
### [ ] 13D — Kod değerlendirme
### [ ] 13E — AI-code comprehension
### [ ] 13F — Açık uçlu evaluator
### [ ] 13G — Provider abstraction/fallback

---

# AŞAMA 14 — İlk Production Curriculum

### [ ] 14A — Computer Fundamentals
### [ ] 14B — C Foundations
### [ ] 14C — Memory Foundations
### [ ] 14D — Linux Foundations
### [ ] 14E — English A0→A1/A2
### [ ] 14F — Assessment content
### [ ] 14G — Content QA

---

# AŞAMA 15 — Analytics / Settings / Notifications

### [ ] 15A — Skill analytics
### [ ] 15B — Learning history
### [ ] 15C — Progress semantics
### [ ] 15D — Settings
### [ ] 15E — Notifications

---

# AŞAMA 16 — UI/UX Polish

### [ ] 16A — Görsel polish
### [ ] 16B — Motion
### [ ] 16C — Usability
### [ ] 16D — Accessibility

---

# AŞAMA 17 — Pilot / Kalibrasyon / QA

### [ ] 17A — Pilot başlangıcı
### [ ] 17B — Planner gözlemi
### [ ] 17C — Mastery Formula v0 calibration
### [ ] 17D — Assessment calibration
### [ ] 17E — Teknik/performance QA
### [ ] 17F — Düzeltme döngüsü

---

# AŞAMA 18 — Release APK

### [ ] 18A — Release hazırlığı
### [ ] 18B — Veri güvenilirliği
### [ ] 18C — Final regression
### [ ] 18D — APK / gerçek cihaz
### [ ] 18E — Release docs

---

# AŞAMA 19 — Uzun Vadeli Curriculum / Career

### [ ] 19A — Modern C++
### [ ] 19B — Systems
### [ ] 19C — Distributed Systems
### [ ] 19D — GPU/CUDA
### [ ] 19E — Triton/Inference
### [ ] 19F — Multi-GPU/AI Infrastructure
### [ ] 19G — Open source
### [ ] 19H — Career readiness
### [ ] 19I — Sürekli curriculum QA

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2E`  
**Aktif:** **`2F — Unutma modeli`**

## Sonraki yürütme

1. 2F PRE-STEP GitHub refresh.
2. Retention/spaced-repetition Research AI / dış research.
3. 2E score + time/retention state sentezi.
4. Forgetting/retention canonical spec.
5. POST-STEP GitHub + MASTER_PLAN sync.