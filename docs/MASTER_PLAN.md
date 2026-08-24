# AI Infra Learning Coach — Master Geliştirme Planı

**Durum:** AKTİF / CANONICAL DETAYLI PLAN  
**Son senkron:** 2026-08-24

Sabit adım kimliklerinin canonical kaynağı `docs/EXECUTION_INDEX.md` dosyasıdır. Bu dosya ayrıntılı checklist ve completion notlarını onunla senkron tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

## Zorunlu yürütme
`PRE-STEP GitHub refresh → gerekiyorsa Research/Coding/QA → spec/çıktı → değerlendirme → POST-STEP GitHub sync → checklist/completion note → sonraki adım`

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle ✅
### [x] 1A — Ana ürün amacı — `docs/PRODUCT_REQUIREMENTS.md`
### [x] 1B — V1 kapsamı — `docs/V1_SCOPE.md`
### [x] 1C — Başarı kriterleri — `docs/V1_SUCCESS_CRITERIA.md`
### [x] 1D — Non-goals — `docs/NON_GOALS.md`

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla ✅
### [x] 2A — Bilgi birimleri — `docs/LEARNING_ENGINE_SPEC.md` — D-021
### [x] 2B — Topic durumları — `docs/TOPIC_STATE_MACHINE.md` — D-023
### [x] 2C — Mastery sinyalleri — `docs/MASTERY_SIGNALS_SPEC.md` — D-025
### [x] 2D — AI / ipucu etkisi — `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md` — D-026
### [x] 2E — Mastery formülü v0 — `GRE-v0` — D-031
### [x] 2F — Unutma modeli — `RVR-v0` — D-032

> **AŞAMA 2 tamamlandı — 2026-08-24.**

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla

### [x] 3A — Günlük kapasite
- explicit daily time = hard budget,
- no auto-overrun,
- split/defer,
- no backlog debt.

Çıktı: `docs/ADAPTIVE_PLANNER_SPEC.md`.  
Karar: D-033.

### [x] 3B — Görev kategorileri
- `LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`,
- purpose/activity/track/evidence ayrı,
- unresolved LearningNeed kalıcı; old task debt değil,
- multi-Skill attribution/provenance/prerequisite/duration contract.

Çıktı: `docs/TASK_TAXONOMY_SPEC.md`.  
Karar: D-034.

### [x] 3C — Öncelik puanı
**Final: `PBR-v0 — Priority Bands & Rank Vector`**
- eligibility priority'den önce,
- P0–P4 semantic bands,
- deterministic lexicographic rank vector,
- starvation/track-balance guard,
- duration semantic priority'den sonra,
- no task debt.

Çıktı: `docs/PRIORITY_POLICY_SPEC.md`.  
Karar: D-035.

### [x] 3D — Prerequisite davranışı
**Final: `PRG-v0 — Prerequisite Readiness Gate`**
- runtime prerequisite `Skill → Skill`,
- `hard | soft` edge semantics,
- readiness: `ready | ready_due | uncertain | not_ready`,
- `review_due` hard lock değildir,
- hard `not_ready` dependent candidate'ı bloke eder,
- critical/strict `verification_due` dependent yeni work'u bekletebilir,
- task-level `required_skill_ids` exact task eligibility'yi belirler,
- yalnız affected branch bekler; independent branches devam eder,
- started Topic regression ile `locked` olmaz,
- prerequisite contamination target negative evidence değildir,
- priority prerequisite'i bypass edemez,
- deterministic/bounded resolver.

Çıktı: `docs/PREREQUISITE_POLICY_SPEC.md`.  
Karar: D-036.

### [x] 3E — Hızlı öğrenme
**Final: `VDW-v0 — Validated Diagnostic Waiver`**
- diagnostic GRE-v0'dan daha kolay ayrı mastery standardı değildir,
- self-report yalnız diagnostic trigger/scope,
- tek easy quiz / recognition-only whole-topic skip yok,
- Objective-level `DiagnosticCoverageWaiver`,
- partial diagnostic yalnız kanıtlanan Objective'leri waive eder,
- `available → mastered` yalnız coverage + GRE required/critical gates birlikte geçince,
- critical coding/debugging/transfer/diversity gate'leri diagnostic'te düşürülemez,
- H0/provenance/evaluator/prerequisite guard,
- integrated diagnostic component evidence için ayrı attribution,
- diagnostic fail prior-knowledge yolunda otomatik remediation değildir,
- waiver mastery/retention state değildir ve curriculum version'a bağlıdır,
- GRE → waiver → PRG → Topic → Planner replan entegrasyonu,
- deterministic/bounded.

Çıktı: `docs/DIAGNOSTIC_WAIVER_SPEC.md`.  
Karar: D-037.

> **Tamamlandı — 2026-08-24:** hızlı öğrenme, güvenilir evidence standardını düşürmeden Objective-level diagnostic waiver olarak kilitlendi.

### [ ] 3F — Kaçırılan günler — **AKTİF**
Kesinleştirilecek:
- kısa/orta/uzun absence sonrası current-state recovery,
- eski PlannedTask/TaskCandidate backlog'unu taşımama,
- overdue retention/remediation/verification ihtiyaçlarını yeniden üretme,
- review/task yığılması yerine bounded yeniden giriş planı,
- critical P0/P1 işlerin recovery önceliği,
- starvation ile absence ayrımı,
- daily capacity içinde recovery,
- `borç` hissi yaratmayan re-entry davranışı,
- 3A–3E ile deterministic entegrasyon.

### [ ] 3G — Açıklanabilir planner
Reason codes + deterministic selection pseudocode.

### [ ] 3H — Planner simülasyonu
Sanal kullanıcı profilleri ve scenario suite.

**Aşama 3 çıktıları:** `docs/ADAPTIVE_PLANNER_SPEC.md`, `docs/TASK_TAXONOMY_SPEC.md`, `docs/PRIORITY_POLICY_SPEC.md`, `docs/PREREQUISITE_POLICY_SPEC.md`, `docs/DIAGNOSTIC_WAIVER_SPEC.md`, missed-day/decision policy, pseudocode, simulation suite.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla
### [ ] 4A — Günlük mikro değerlendirme
### [ ] 4B — Haftalık sınav
### [ ] 4C — Aylık yeterlilik sınavı
### [ ] 4D — Soru bankası
### [ ] 4E — AI-generated soru doğrulaması

---

# AŞAMA 5 — Curriculum ve Knowledge Graph
### [ ] 5A — Ana domain haritası
### [ ] 5B — Topic metadata
### [ ] 5C — İlk 8–12 haftalık curriculum graph
### [ ] 5D — Curriculum QA

---

# AŞAMA 6 — İngilizce Paralel Hattı
### [ ] 6A — Başlangıç ölçümü
### [ ] 6B — A1/A2/B1/B2 teknik hedefleri
### [ ] 6C — Günlük English bileşeni
### [ ] 6D — Teknik entegrasyon
### [ ] 6E — English mastery

---

# AŞAMA 7 — UX ve Ekranlar
### [ ] 7A — Bilgi mimarisi
### [ ] 7B — Ana ekran
### [ ] 7C — Günlük çalışma akışı
### [ ] 7D — Sınav UX
### [ ] 7E — Skill/progress UX
### [ ] 7F — Tasarım sistemi
### [ ] 7G — Wireframe/prototip

---

# AŞAMA 8 — Teknik Mimari ve Veri Modeli
### [ ] 8A — Mobil teknoloji seçimi
### [ ] 8B — Veri saklama/local-first
### [ ] 8C — Domain veri modeli
### [ ] 8D — Servis sınırları
### [ ] 8E — AI entegrasyon mimarisi
### [ ] 8F — Test stratejisi

---

# AŞAMA 9 — Mobil Proje İskeleti
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
### [ ] 10D — Günlük mikro quiz
### [ ] 10E — Gün sonu

---

# AŞAMA 11 — Mastery + Planner Implementasyonu
### [ ] 11A — Mastery Engine v1
### [ ] 11B — Prerequisite Engine
### [ ] 11C — Planner Engine v1
### [ ] 11D — Replan
### [ ] 11E — Reason codes
### [ ] 11F — Sanal kullanıcı testleri

---

# AŞAMA 12 — Assessment + Retention + Remediation Implementasyonu
### [ ] 12A — Haftalık sınav
### [ ] 12B — Aylık sınav
### [ ] 12C — Spaced repetition
### [ ] 12D — Remediation Engine
### [ ] 12E — Program değişiklik raporu

---

# AŞAMA 13 — AI Tutor ve Akıllı Değerlendirme
### [ ] 13A — Tutor davranış sözleşmesi
### [ ] 13B — Yanlış analizi
### [ ] 13C — Alternatif anlatım
### [ ] 13D — Kod değerlendirme
### [ ] 13E — AI-generated code comprehension check
### [ ] 13F — Açık uçlu cevap değerlendirme
### [ ] 13G — Provider abstraction/fallback

---

# AŞAMA 14 — İlk Gerçek Eğitim İçeriği
### [ ] 14A — Computer Fundamentals
### [ ] 14B — C Foundations
### [ ] 14C — Memory Foundations
### [ ] 14D — Linux Foundations
### [ ] 14E — English A0→A1/A2
### [ ] 14F — Assessment content
### [ ] 14G — Content QA

---

# AŞAMA 15 — İlerleme / Analitik / Ayarlar
### [ ] 15A — Skill analytics
### [ ] 15B — Öğrenme geçmişi
### [ ] 15C — Progress kuralları
### [ ] 15D — Ayarlar
### [ ] 15E — Bildirimler

---

# AŞAMA 16 — UI/UX Polish
### [ ] 16A — Görsel polish
### [ ] 16B — Motion
### [ ] 16C — Kullanılabilirlik
### [ ] 16D — Accessibility

---

# AŞAMA 17 — Pilot / Kalibrasyon / QA
### [ ] 17A — Pilot başlangıcı
### [ ] 17B — Planner gözlemi
### [ ] 17C — Mastery kalibrasyonu
### [ ] 17D — Assessment kalibrasyonu
### [ ] 17E — Teknik QA
### [ ] 17F — Düzeltme döngüsü

---

# AŞAMA 18 — Release APK
### [ ] 18A — Release hazırlığı
### [ ] 18B — Veri güvenilirliği
### [ ] 18C — Final regression
### [ ] 18D — APK / gerçek cihaz
### [ ] 18E — Release dokümantasyonu

---

# AŞAMA 19 — Uzun Vadeli Curriculum ve Kariyer Katmanı
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

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3E`  
**Aktif:** **`3F — Kaçırılan günler`**

Bir sonraki yürütme: yeni PRE-STEP GitHub refresh → 3F missed-days/current-state recovery policy → POST-STEP sync.
