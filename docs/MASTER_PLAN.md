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

### [x] 1A — Ana ürün amacı
Çıktı: `docs/PRODUCT_REQUIREMENTS.md`

### [x] 1B — V1 kapsamı
Çıktı: `docs/V1_SCOPE.md`

### [x] 1C — Başarı kriterleri
Çıktı: `docs/V1_SUCCESS_CRITERIA.md`

### [x] 1D — Non-goals
Çıktı: `docs/NON_GOALS.md`

> **Tamamlandı — 2026-08-24:** ürün amacı, V1 sınırı, acceptance ve non-goals kilitlendi.

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla ✅

### [x] 2A — Bilgi birimleri
`Domain → Module → Topic → Skill → Learning Objective`; Skill canonical mastery/prerequisite seviyesi.  
Çıktı: `docs/LEARNING_ENGINE_SPEC.md` — D-021.

### [x] 2B — Topic durumları
`locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`.  
Çıktı: `docs/TOPIC_STATE_MACHINE.md` — D-023.

### [x] 2C — Mastery sinyalleri
Çıktı: `docs/MASTERY_SIGNALS_SPEC.md` — D-025.

### [x] 2D — AI / ipucu etkisi
Çıktı: `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md` — D-026.

### [x] 2E — Mastery formülü v0
Final: `GRE-v0 — Gated Recent Evidence`.  
Çıktılar: `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md` — D-031.

### [x] 2F — Unutma modeli
Final: `RVR-v0 — Retention Verification & Risk`.  
Çıktılar: `docs/RETENTION_FORGETTING_SPEC.md`, `docs/2F_RESEARCH_VALIDATION.md` — D-032.

> **AŞAMA 2 tamamlandı — 2026-08-24:** GRE-v0 mastery + RVR-v0 retention canonical.

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla

### [x] 3A — Günlük kapasite
- explicit daily time = hard budget,
- editable short/normal/intensive profiles,
- reserve/min block heuristic,
- no auto-overrun,
- dynamic remaining-time replan,
- split/defer,
- no backlog debt,
- duration/pacing contract.

Çıktı: `docs/ADAPTIVE_PLANNER_SPEC.md`.  
Karar: D-033.

### [x] 3B — Görev kategorileri
- `LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`,
- purpose/activity/track/evidence ayrı,
- unresolved LearningNeed kalıcı; old task debt değil,
- multi-Skill attribution/provenance/variant/prerequisite/duration contract.

Çıktı: `docs/TASK_TAXONOMY_SPEC.md`.  
Karar: D-034.

### [x] 3C — Öncelik puanı

**Final: `PBR-v0 — Priority Bands & Rank Vector`**
- priority açık LearningNeed seviyesinde,
- eligibility priority'den önce,
- P0 `integrity_blocker`, P1 `repair_or_verify`, P2 `maintain_or_continue`, P3 `planned_progress`, P4 `reinforce_or_optimize`,
- critical etiketi tek başına P0 değil; gerçek blocking gerekir,
- `review_due` forgetting/negative evidence değildir,
- aynı band içinde lexicographic rank vector: blocking → criticality → evidence severity → temporal urgency → starvation → continuation → decision value → track balance → duration fit → stable tie-break,
- additive sahte-hassas score ve score/minute yok,
- starvation guard + parallel-track balance,
- safe split/smaller alternative/defer 3A ile uyumlu,
- kalan LearningNeed açık kalır; task debt yok,
- `PriorityDecisionTrace` açıklanabilirlik için tutulur.

Çıktı: `docs/PRIORITY_POLICY_SPEC.md`.  
Karar: D-035.

> **Tamamlandı — 2026-08-24:** 80 dk ihtiyaç / 50 dk capacity problemi semantic priority + deterministic rank + capacity fit ile çözülebilir hale geldi.

### [ ] 3D — Prerequisite davranışı — **AKTİF**
Kesinleştirilecek:
- hard/soft prerequisite edge semantics,
- task eligibility,
- critical unresolved verification/remediation nedeniyle dependent wait,
- `review_due` tek başına hard lock olmaması,
- bağımsız branch'lerin devam etmesi,
- prerequisite contamination guard,
- prerequisite state değişince replan,
- 3D eligibility filter ile 3C priority sırası.

### [ ] 3E — Hızlı öğrenme
Diagnostic/skip/validated waiver; no single-easy-quiz skip.

### [ ] 3F — Kaçırılan günler
Long absence sonrası backlog dump yok; current-state recovery.

### [ ] 3G — Açıklanabilir planner
Reason codes + deterministic selection pseudocode.

### [ ] 3H — Planner simülasyonu
Sanal kullanıcı profilleri ve scenario suite.

**Aşama 3 çıktıları:** `docs/ADAPTIVE_PLANNER_SPEC.md`, `docs/TASK_TAXONOMY_SPEC.md`, `docs/PRIORITY_POLICY_SPEC.md`, prerequisite/decision policy, pseudocode, simulation suite.

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
Bağlayıcı ön kural: `docs/ENGLISH_FOUNDATION_RULES.md`.
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
> **Ana production uygulama kodlamasının başladığı aşama.**
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

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3C`  
**Aktif:** **`3D — Prerequisite davranışı`**

Bir sonraki yürütme: yeni PRE-STEP GitHub refresh → 3D prerequisite/eligibility policy → POST-STEP sync.
