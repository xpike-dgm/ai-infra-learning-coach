# AI Infra Learning Coach — Master Geliştirme Planı

**Durum:** AKTİF / CANONICAL DETAYLI PLAN  
**Son senkron:** 2026-08-25

Sabit adım kimliklerinin canonical kaynağı `docs/EXECUTION_INDEX.md` dosyasıdır. Bu dosya ayrıntılı checklist ve completion notlarını onunla senkron tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

## Uzun vadeli hedef — D-041
> **Gerektiğinde 4+ yıl veya daha uzun sürebilecek mastery-gated bir rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability oluşturmak.**

`4+ yıl` countdown değildir. Final readiness; required Skill mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ile verilir.

V1 full professional curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality içerik ile release edilir.

## 2026-08-25 rota/plan kararları
- **D-042:** Python common foundation'ın resmi parçasıdır; C/C++ yerine geçmez.
- **D-043:** standalone specialization-stage yorumu geri çekildi.
- **D-044:** AŞAMA 6 Granular Capability Map eklendi; broad domain'ler `Module → Topic → Skill → Objective` seviyesinde weakness-addressable hale getirilecek.
- **D-045:** 4B final weekly model WBA-v0.
- **D-046:** 4C final monthly model MCA-v0.

Canonical yürütme:
`PRE-STEP GitHub refresh → gerekiyorsa Research/Coding/QA → spec/çıktı → değerlendirme → POST-STEP GitHub sync → sonraki adım`

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle ✅
### [x] 1A — Ana ürün amacı — `docs/PRODUCT_REQUIREMENTS.md`
### [x] 1B — V1 kapsamı — `docs/V1_SCOPE.md`
### [x] 1C — Başarı kriterleri — `docs/V1_SUCCESS_CRITERIA.md`
### [x] 1D — Non-goals — `docs/NON_GOALS.md`

> AŞAMA 1 tamamlandı.

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla ✅
### [x] 2A — Bilgi birimleri — `docs/LEARNING_ENGINE_SPEC.md` — D-021
### [x] 2B — Topic durumları — `docs/TOPIC_STATE_MACHINE.md` — D-023
### [x] 2C — Mastery sinyalleri — `docs/MASTERY_SIGNALS_SPEC.md` — D-025
### [x] 2D — AI / ipucu etkisi — `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md` — D-026
### [x] 2E — Mastery formülü v0 — GRE-v0 — D-031
### [x] 2F — Unutma modeli — RVR-v0 — D-032

**D-044 clarification:** broad Domain/Topic diagnosis atomu değildir; weakness/remediation mümkün olduğunca Skill/Objective seviyesinde lokalize edilir.

> AŞAMA 2 tamamlandı.

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla ✅
### [x] 3A — Günlük kapasite — D-033
### [x] 3B — Görev kategorileri — D-034
### [x] 3C — Öncelik — PBR-v0 / D-035
### [x] 3D — Prerequisite — PRG-v0 / D-036
### [x] 3E — Hızlı öğrenme — VDW-v0 / D-037
### [x] 3F — Kaçırılan günler — SRR-v0 / D-038
### [x] 3G — Açıklanabilir planner — PDT-v0 / D-039
### [x] 3H — Planner simülasyonu — `docs/PLANNER_SIMULATION_SUITE.md`

```text
16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical cross-spec contradiction
```

> AŞAMA 3 tamamlandı.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla

### [x] 4A — Günlük mikro değerlendirme — DMA-v0 / D-040
**Final:** `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
- daily assessment quota değildir,
- Objective-matched evidence,
- H0/assistance/provenance,
- prerequisite fairness,
- invalid/provisional safety,
- evidence→GRE/RVR→replan.

### [x] 4B — Haftalık sınav — WBA-v0 / D-045
**Final:** `docs/WEEKLY_ASSESSMENT_SPEC.md`
- weekly exam tek overall score/pass-fail değildir,
- item'lardan önce blueprint,
- recent progress + weakness/verification + critical prerequisite + retention + integration/transfer + gerektiğinde English,
- fixed quota/soru/süre yok,
- evidence weekly olduğu için extra weight almaz,
- PRG fairness + family diversity + Objective-specific modality,
- split/pause/resume; incomplete/missed exam debt değildir,
- broad Domain state yazılmaz,
- common `AssessmentBlueprint / Slot / SessionResult` abstraction kilitlendi.

### [x] 4C — Aylık yeterlilik sınavı — MCA-v0 / D-046
**Final:** `docs/MONTHLY_ASSESSMENT_SPEC.md`
- monthly exam ay sonu notu/domain pass-fail değildir,
- WBA common blueprint/result contract'ı monthly extension ile kullanılır,
- longitudinal required capability,
- persistent weakness/verification,
- critical capability revalidation,
- delayed retention,
- cross-topic transfer,
- integrated application,
- gerektiğinde Technical English ve professional evidence checkpoint,
- role family'leri fixed quota değildir,
- cumulative-everything exam yok; state-based bounded longitudinal sampling,
- recent/older balance fixed yüzde değil,
- critical Skill automatic monthly retest değil,
- transfer/integration prerequisite-safe ve component-attributable,
- professional checkpoint final professional-readiness/capstone gate değil,
- fixed soru sayısı/süre/pass score yok,
- daily hard budget korunur; safe multi-block split/pause/resume,
- incomplete/missed monthly exam failure/debt değil,
- H0/H1–H4 + invalid/provisional/root-contamination + GRE/RVR hysteresis korunur,
- D-044 granular localization korunur,
- V1 SC-016: güvenilir persistent/critical gap planner/curriculum priority'yi gerçekten değiştirir,
- 4D için item/task bank metadata handoff tanımlandı.

**PRE/POST notu:** 4C fresh GitHub PRE-STEP refresh ile yürütüldü. Ayrı Research AI kullanılmadı; psychometric optimum veya universal cadence uydurulmadı. Empirical calibration AŞAMA 18'e bırakıldı.

### [ ] 4D — Soru bankası — **AKTİF**
Kesinleştirilecek:
- trusted item/task production schema,
- stable ID + version + lifecycle,
- target Skill/Objective + required prerequisites,
- `forbidden_not_yet_concepts` / eligibility,
- evidence/activity kind,
- daily/weekly/monthly scope eligibility,
- blueprint-role eligibility,
- difficulty vs complexity semantics,
- variant family + dependency/testlet group,
- context/transfer structure,
- integrated component attribution,
- answer key/rubric/test reference,
- evaluator requirement,
- allowed tools / artifact requirement,
- validation/trust/content origin,
- exposure/solution leakage/reuse,
- estimated duration + atomic/splittable behavior,
- language/scaffold/freshness metadata,
- indexing/query/bounded selection,
- 4E AI-generated item validation handoff.

### [ ] 4E — AI-generated soru doğrulaması
- generated candidate trusted bank'e otomatik girmez,
- correctness/ambiguity/target-fit/prerequisite/duplicate/rubric/evaluator checks,
- risk-based use eligibility,
- invalidation/version lifecycle.

---

# AŞAMA 5 — Curriculum ve Knowledge Graph İskeleti
### [ ] 5A — Ana domain haritası
- 4+ year professional envelope,
- Technical English paralel,
- Python + C foundation,
- systems → distributed → performance → GPU → inference → AI infra,
- OSS/projects/capstone layer.

### [ ] 5B — Graph / metadata sözleşmesi
- Domain/Module/Topic/Skill/Objective relations,
- prerequisite,
- required/criticality,
- evidence contracts,
- retention/remediation/diagnostic,
- project/capstone attribution,
- version/freshness.

### [ ] 5C — İlk 8–12 haftalık curriculum backbone
### [ ] 5D — Graph architecture QA
- cycle/dead-end,
- hidden prerequisite,
- duplicate canonical Skill,
- scalability/versioning.

> AŞAMA 5 schema/backbone; detailed capability decomposition AŞAMA 6.

---

# AŞAMA 6 — Granular Capability Map / Öğrenme Rotasını Alt Becerilere Böl
Canonical charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

### [ ] 6A — Granularity + naming standardı
### [ ] 6B — Full-route decomposition blueprint
### [ ] 6C — Foundations detailed map
- Technical English,
- Python,
- C,
- Linux/Git/Shell,
- DS&A.

### [ ] 6D — Systems detailed map
- Modern C++, Architecture, OS/Memory, Concurrency, Networking, Distributed, Storage/DB, Cloud/Observability, Performance.

### [ ] 6E — GPU / ML / Inference detailed map
- GPU Architecture, CUDA, Triton, ML/Transformer, inference internals, serving, KV/batching/scheduling/quantization, Multi-GPU/NCCL/RDMA, AI Infra.

### [ ] 6F — Professional engineering / project map
- Git/code review, testing/build/debug/profiling, docs/benchmarks, OSS, integrated projects, capstones.

### [ ] 6G — Weakness localization + remediation mapping
### [ ] 6H — Coverage + prerequisite + external Research QA

**Acceptance:** full route sufficiently granular Skill/Objective map; targeted diagnosis/remediation and prerequisite/evidence mapping mümkün.

---

# AŞAMA 7 — İngilizce Paralel Hattı
### [ ] 7A — Başlangıç ölçümü
### [ ] 7B — A1/A2/B1/B2+ teknik hedefleri
### [ ] 7C — Günlük English bileşeni
### [ ] 7D — Teknik entegrasyon
### [ ] 7E — English mastery

---

# AŞAMA 8 — UX ve Ekranlar
### [ ] 8A — Bilgi mimarisi
### [ ] 8B — Ana ekran
### [ ] 8C — Günlük çalışma akışı
### [ ] 8D — Sınav UX
### [ ] 8E — Skill/progress/weakness UX
### [ ] 8F — Tasarım sistemi
### [ ] 8G — Wireframe/prototip

---

# AŞAMA 9 — Teknik Mimari ve Veri Modeli
### [ ] 9A — Mobil teknoloji seçimi
### [ ] 9B — Veri saklama/local-first
### [ ] 9C — Domain veri modeli
### [ ] 9D — Servis sınırları
### [ ] 9E — AI entegrasyon mimarisi
### [ ] 9F — Test stratejisi/performance budgets

---

# AŞAMA 10 — Mobil Proje İskeleti
### [ ] 10A — Proje kurulumu
### [ ] 10B — Navigation
### [ ] 10C — Design system implementation
### [ ] 10D — Local database
### [ ] 10E — Temel uygulama sağlığı

---

# AŞAMA 11 — Günlük Öğrenme MVP
### [ ] 11A — Today
### [ ] 11B — Task runner
### [ ] 11C — Session state
### [ ] 11D — Günlük mikro quiz
### [ ] 11E — Gün sonu

---

# AŞAMA 12 — Mastery + Planner Implementasyonu
### [ ] 12A — Mastery Engine v1
### [ ] 12B — Prerequisite Engine
### [ ] 12C — Planner Engine v1
### [ ] 12D — Replan
### [ ] 12E — Reason codes
### [ ] 12F — Sanal kullanıcı testleri

---

# AŞAMA 13 — Assessment + Retention + Remediation Implementasyonu
### [ ] 13A — Haftalık sınav
### [ ] 13B — Aylık sınav
### [ ] 13C — Spaced repetition
### [ ] 13D — Remediation Engine
### [ ] 13E — Program değişiklik raporu

---

# AŞAMA 14 — AI Tutor ve Akıllı Değerlendirme
### [ ] 14A — Tutor davranış sözleşmesi
### [ ] 14B — Yanlış analizi
### [ ] 14C — Alternatif anlatım
### [ ] 14D — Kod değerlendirme
### [ ] 14E — AI-generated code comprehension check
### [ ] 14F — Açık uçlu cevap değerlendirme
### [ ] 14G — Provider abstraction/fallback

---

# AŞAMA 15 — İlk 8–12 Haftalık Gerçek Eğitim İçeriği
### [ ] 15A — Computer / Programming Fundamentals
### [ ] 15B — Python Foundations
### [ ] 15C — C Foundations
### [ ] 15D — Memory Foundations
### [ ] 15E — Linux / Git / Shell Foundations
### [ ] 15F — English A0→A1/A2 başlangıç paketi
### [ ] 15G — Assessment content
### [ ] 15H — Content QA

> AŞAMA 15 full 4+ year curriculum değildir; AŞAMA 6 map'inin başlangıç bölümünü production-quality content'e dönüştürür.

---

# AŞAMA 16 — İlerleme / Analitik / Ayarlar
### [ ] 16A — Skill analytics
### [ ] 16B — Öğrenme geçmişi
### [ ] 16C — Progress / weakness kuralları
### [ ] 16D — Ayarlar
### [ ] 16E — Bildirimler

---

# AŞAMA 17 — UI/UX Polish
### [ ] 17A — Görsel polish
### [ ] 17B — Motion
### [ ] 17C — Kullanılabilirlik
### [ ] 17D — Accessibility

---

# AŞAMA 18 — Pilot / Kalibrasyon / QA
### [ ] 18A — Pilot başlangıcı
### [ ] 18B — Planner gözlemi
### [ ] 18C — Mastery kalibrasyonu
### [ ] 18D — Assessment kalibrasyonu
- DMA/WBA/MCA duration/UX,
- false-positive/false-negative,
- slot selection pressure,
- revalidation/retention behavior.
### [ ] 18E — Teknik / performance QA
### [ ] 18F — Düzeltme döngüsü

---

# AŞAMA 19 — Release APK
### [ ] 19A — Release hazırlığı
### [ ] 19B — Veri güvenilirliği
### [ ] 19C — Final regression
### [ ] 19D — APK / gerçek cihaz
### [ ] 19E — Release dokümantasyonu

**AŞAMA 19 V1 release = professional curriculum completion değildir.**

---

# AŞAMA 20 — Uzun Vadeli Professional Curriculum ve Kariyer Katmanı
### [ ] 20A — Modern C++ + Advanced Python + Professional Tooling
### [ ] 20B — Systems + Architecture + Performance
### [ ] 20C — Networking + Distributed Systems + Storage
### [ ] 20D — GPU Architecture + CUDA
### [ ] 20E — Triton + ML/Transformer + LLM Inference
### [ ] 20F — Serving Engines + KV Cache / Batching / Scheduling / Quantization
### [ ] 20G — Multi-GPU / NCCL / RDMA / AI Infrastructure
### [ ] 20H — Open Source + Engineering Practice + Career Readiness
### [ ] 20I — Sürekli Curriculum QA + Büyük Entegre Projeler + Professional Capstones

> Full curriculum completion takvimle değil professional-readiness evidence ile tanımlanır.

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4C`  
**Aktif:** **`4D — Soru bankası`**

**Bağlayıcı:** D-041 professional target; D-042 Python foundation; D-044 granular capability map; D-045 WBA-v0; D-046 MCA-v0.  
**Geri çekilen:** D-043 specialization-stage yorumu.

Bir sonraki yürütme: **4D başlamadan yeni PRE-STEP GitHub refresh → 4D trusted Question Bank policy/schema → POST-STEP sync.**