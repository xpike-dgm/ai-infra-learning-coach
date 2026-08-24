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

> **AŞAMA 1 tamamlandı.**

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

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla ✅

### [x] 3A — Günlük kapasite — D-033
- explicit daily time = hard budget,
- no auto-overrun,
- split / smaller alternative / defer,
- no task/backlog debt,
- editable short/normal/intensive presets,
- replan only remaining capacity.

Çıktı: `docs/ADAPTIVE_PLANNER_SPEC.md`.

### [x] 3B — Görev kategorileri — D-034
- `State → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`,
- purpose/activity/track/evidence ayrı,
- unresolved LearningNeed kalıcı; old task debt değil,
- multi-Skill attribution/provenance/prerequisite/duration contract.

Çıktı: `docs/TASK_TAXONOMY_SPEC.md`.

### [x] 3C — Öncelik puanı — PBR-v0 / D-035
- eligibility priority'den önce,
- P0–P4 semantic bands,
- deterministic lexicographic rank vector,
- starvation/track-balance guard,
- duration semantic priority'den sonra,
- no fake weighted score / no priority-per-minute.

Çıktı: `docs/PRIORITY_POLICY_SPEC.md`.

### [x] 3D — Prerequisite davranışı — PRG-v0 / D-036
- runtime `Skill → Skill`,
- hard/soft edges,
- readiness `ready | ready_due | uncertain | not_ready`,
- `review_due` hard lock değildir,
- exact dependent branch blocking,
- contamination guard,
- priority prerequisite'i bypass edemez.

Çıktı: `docs/PREREQUISITE_POLICY_SPEC.md`.

### [x] 3E — Hızlı öğrenme — VDW-v0 / D-037
- diagnostic GRE-v0'dan daha kolay değildir,
- Objective-level validated coverage waiver,
- partial diagnostic canonical,
- critical H0/provenance/evaluator/prerequisite guards,
- GRE → waiver → PRG → Topic → replan entegrasyonu.

Çıktı: `docs/DIAGNOSTIC_WAIVER_SPEC.md`.

### [x] 3F — Kaçırılan günler — SRR-v0 / D-038
- absence failure/mastery decay/task debt değildir,
- stale plan replay edilmez,
- current-state re-entry,
- due inventory ≠ DailyPlan,
- starvation ≠ absence,
- recovery hard capacity + PRG + PBR ile çalışır,
- safe branch'lerde new learning globally dondurulmaz.

Çıktı: `docs/MISSED_DAY_RECOVERY_SPEC.md`.

### [x] 3G — Açıklanabilir planner — PDT-v0 / D-039
- structured reason code + decision trace,
- private chain-of-thought değil canonical state/policy/disposition kaydı,
- internal audit vs user-facing explanation,
- need/candidate dispositions,
- PRG → PBR → capacity sırası korunur,
- versioned replan chain,
- LLM yalnız paraphrase; template fallback,
- deterministic end-to-end planner pseudocode.

Çıktı: `docs/PLANNER_EXPLAINABILITY_SPEC.md`.

### [x] 3H — Planner simülasyonu
**Final:** `docs/PLANNER_SIMULATION_SUITE.md`
- 8 sanal profil sınıfı,
- 16 zorlayıcı policy senaryosu,
- 20/20 PDT-v0 invariant coverage,
- critical prerequisite block,
- `review_due` no-lock semantics,
- high-priority-not-fit capacity açıklaması,
- partial diagnostic,
- 30 günlük absence + büyük due inventory,
- paused checkpoint,
- mid-session capacity change,
- new remediation replan,
- invalid candidate,
- duplicate semantic need,
- critical-label-no-P0,
- same-input determinism,
- explanation trace integrity.

**3H sonucu:**
```text
16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical cross-spec contradiction
```

Not: Bu spec-level simulation PASS'tir. Production planner runtime/sanal kullanıcı testleri 11F'te, gerçek cihaz/performance doğrulaması 17E'de ayrıca yapılacaktır.

> **AŞAMA 3 tamamlandı — 2026-08-24.** Planner design contract implementation'a taşınabilecek seviyede tanımlandı.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla

### [ ] 4A — Günlük mikro değerlendirme — **AKTİF**
Kesinleştirilecek:
- günlük mikro assessment'ın amacı ve sınırı,
- teach/practice/assessment ayrımı,
- günlük ölçülecek Skill/Objective seçimi,
- capacity-aware assessment composition,
- soru sayısı/süre için bilimsel sabit uydurmama,
- GRE-v0 evidence compatibility,
- H0/H1–H4 ve AI assistance davranışı,
- PRG prerequisite validation,
- retention/remediation/replan entegrasyonu,
- low-capacity day davranışı,
- invalid/ambiguous item güvenliği,
- result contract'ın 4B–4E'ye taşınması.

### [ ] 4B — Haftalık sınav
- multi-Skill coverage,
- independent evidence diversity,
- current weaknesses + progress balance,
- programı gerçekten değiştiren sonuçlar.

### [ ] 4C — Aylık yeterlilik sınavı
- daha geniş transfer/integration,
- critical prerequisite revalidation,
- false-positive mastery riskini azaltma.

### [ ] 4D — Soru bankası
- trusted item metadata,
- variant family / dependency group,
- objective attribution,
- difficulty/complexity,
- validation/versioning.

### [ ] 4E — AI-generated soru doğrulaması
- AI candidate generation trusted bank'e otomatik giriş değildir,
- correctness/ambiguity/prerequisite/duplicate/target-fit validator.

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

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`  
**Aktif:** **`4A — Günlük mikro değerlendirme`**

Bir sonraki yürütme: yeni PRE-STEP GitHub refresh → 4A assessment policy → POST-STEP sync.