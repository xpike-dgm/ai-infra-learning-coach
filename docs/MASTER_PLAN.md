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

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla

## Amaç

`Dersi tamamladı = öğrendi` hatasını ortadan kaldıran, Skill/Objective seviyesinde açıklanabilir ve deterministik öğrenme modeli.

### [x] 2A — Bilgi birimleri

- `Domain → Module → Topic → Skill → Learning Objective`
- Skill canonical mastery/prerequisite seviyesi
- Topic/Module/Domain derived
- Skill → Skill prerequisite
- Topic ↔ Skill many-to-many

Çıktı: `docs/LEARNING_ENGINE_SPEC.md` — D-021.

### [x] 2B — Topic durumları

`locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`.

Çıktı: `docs/TOPIC_STATE_MACHINE.md` — D-023.

### [x] 2C — Mastery sinyalleri

Recognition, recall, code reading, coding, debugging, explanation, transfer, retention, project; direct/corroborating/contextual ayrımı; prerequisite contamination ve same-family guard.

Çıktı: `docs/MASTERY_SIGNALS_SPEC.md` — D-025.

### [x] 2D — AI / ipucu etkisi

H0–H4, timing, artifact provenance, independent/assisted/practice-only/recheck, generated code guardrail, fresh recheck, objective-specific tools.

Çıktı: `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md` — D-026.

### [x] 2E — Mastery formülü v0

**Research süreci:**
- İlk Beta-style candidate ana yönetici tarafından tasarlandı.
- Workflow hatası fark edilince 2E yeniden açıldı.
- Ayrı Research AI raporu alındı.
- Rapor otomatik kabul edilmedi; kritik iddialar 2A–2D ve seçili akademik kaynaklarla karşılaştırıldı.
- Candidate Beta-style weighted accumulator ve sabit assistance/AI evaluator katsayıları finalden çıkarıldı.

**Final model: `GRE-v0 — Gated Recent Evidence`**

- mastery score yalnız valid + prerequisite-valid + H0 + direct + verified + independent evidence group'lardan oluşur,
- H1–H4 formative/remediation/recheck sinyalidir; positive independent mastery score'a girmez,
- corroborating evidence direct gate'i ikame etmez,
- same-family/dependent items `testlet/dependency_group` olarak gruplanır,
- Objective score = son en fazla `5` eligible independent direct group'un `q_g` ortalaması,
- `0.80` threshold ve window `5` versioned engineering heuristic,
- standard default: 2 independent group + default 2 family/context,
- critical default: 3 independent group + 2 family/context + non-basic/objective-specific gate,
- coding critical: H0 user-authored artifact,
- debugging critical: H0 diagnosis/fix evidence,
- Skill mastered yalnız tüm required/critical Objective gates PASS ise,
- tek clean post-mastery negative → `verification_due`, instant reset yok,
- difficulty multiplier değil gate,
- AI evaluator fixed numeric weight yok; `verified | provisional | invalid`,
- bounded/incremental sufficient-state ile D-028 performans gereksinimi korunur.

Çıktılar:
- `docs/MASTERY_FORMULA_V0.md`
- `docs/2E_RESEARCH_VALIDATION.md`
- D-031; D-029 candidate'ın yerine geçti.

> **Tamamlanma notu — 2026-08-24:** 2E bağımsız Research AI doğrulaması sonrası GRE-v0 ile final kapandı.

### [ ] 2F — Unutma modeli — **AKTİF**

Başlamadan yeni PRE-STEP + ayrı Research AI turu.

Tasarlanacak:
- spaced repetition model seçimi,
- review interval başlangıcı/büyümesi,
- successful/failed delayed retrieval,
- time-based retention risk/decay,
- `mastered → weakening → mastered/remediation_required`,
- natural reuse'un retention evidence etkisi,
- GRE-v0 mastery state ile retention state entegrasyonu.

Beklenen çıktı: `docs/RETENTION_FORGETTING_SPEC.md`.

### Aşama 2 kapanış kapısı

Aynı current evidence + retention history ile Skill durumu deterministik/açıklanabilir hesaplanmalı; assistance, independent mastery ve forgetting ayrı ama entegre olmalı.

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla

### [ ] 3A — Günlük kapasite
Kısa/normal/yoğun gün, hedef süre, overflow.

### [ ] 3B — Görev kategorileri
Yeni konu, remediation, retention, coding, debugging, English, project, micro assessment.

### [ ] 3C — Öncelik puanı
Critical prerequisite, due retention, weak Skill, next eligible Topic, English, diversity.

### [ ] 3D — Prerequisite davranışı
Hard/soft, dependent wait, independent continue.

### [ ] 3E — Hızlı öğrenme
Diagnostic/skip, validated waiver, no single-easy-quiz skip.

### [ ] 3F — Kaçırılan günler
Backlog dump yok; current state'ten replan.

### [ ] 3G — Açıklanabilir planner
Reason codes.

### [ ] 3H — Planner simülasyonu
Sanal kullanıcı profilleri.

Çıktı: `docs/ADAPTIVE_PLANNER_SPEC.md` + simulation suite.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla

### [ ] 4A — Günlük mikro değerlendirme
### [ ] 4B — Haftalık sınav
### [ ] 4C — Aylık yeterlilik sınavı
### [ ] 4D — Soru bankası
### [ ] 4E — AI-generated soru doğrulaması

Çıktı: `docs/ASSESSMENT_SYSTEM_SPEC.md`.

---

# AŞAMA 5 — Curriculum ve Knowledge Graph

### [ ] 5A — Ana domain haritası
### [ ] 5B — Topic metadata
### [ ] 5C — İlk 8–12 haftalık curriculum graph
### [ ] 5D — Curriculum QA

Çıktı: `docs/CURRICULUM_GRAPH_SPEC.md` + dataset/QA.

---

# AŞAMA 6 — İngilizce Paralel Hattı

Bağlayıcı ön kural: `docs/ENGLISH_FOUNDATION_RULES.md`.

### [ ] 6A — Başlangıç ölçümü
### [ ] 6B — A1/A2/B1/B2 teknik hedefleri
### [ ] 6C — Günlük English bileşeni
### [ ] 6D — Teknik entegrasyon
### [ ] 6E — English mastery

Çıktı: `docs/ENGLISH_TRACK_SPEC.md`.

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

Çıktı: `docs/TECH_ARCHITECTURE.md`, `docs/DATA_MODEL.md`, ADR.

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

**Tamamlanan:** `1A–1D`, `2A–2E`  
**Aktif:** **`2F — Unutma modeli`**

Bir sonraki yürütme: yeni PRE-STEP GitHub refresh → retention/spaced-repetition Research AI → 2F spec → POST-STEP sync.
