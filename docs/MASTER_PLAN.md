# AI Infra Learning Coach — Master Geliştirme Planı

**Durum:** AKTİF / CANONICAL DETAYLI PLAN  
**Son senkron:** 2026-08-25

Sabit adım kimliklerinin canonical kaynağı `docs/EXECUTION_INDEX.md` dosyasıdır. Bu dosya ayrıntılı checklist ve completion notlarını onunla senkron tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

## Uzun vadeli hedef — D-041

> **Gerektiğinde 4+ yıl veya daha uzun sürebilecek mastery-gated bir rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability oluşturmak.**

`4+ yıl` countdown değildir. Final readiness; required Skill mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ile verilir.

V1 ayrımı korunur: full professional curriculum bitmeden, learning engine + ilk 8–12 haftalık production-quality içerik ile release edilir.

## 2026-08-25 rota/plan güncellemeleri — D-042 / D-044

- **D-042:** Python common foundation'ın resmi parçasıdır; C/C++ yerine geçmez.
- **D-043:** önceki “sona specialization stage ekle” yorumu kullanıcı talebini yanlış anlamıştır ve geri çekilmiştir.
- **D-044:** ana rotadaki her büyük domain uygulamanın ayrı öğretebildiği/ölçebildiği `Module → Topic → Skill → Objective` seviyesine kapsamlı biçimde bölünecektir.
- Bu nedenle **AŞAMA 6 — Granular Capability Map** AŞAMA 5'ten sonra planlama aşamalarının arasına eklendi; henüz başlanmamış future stages yeniden indekslendi.

Canonical charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

## 2026-08-25 assessment güncellemesi — D-045

- 4B final model: **`WBA-v0 — Weekly Blueprint Assessment`**.
- Weekly exam tek score/pass-fail sistemi değildir; item'lardan önce state-temelli blueprint üretir.
- Evidence DMA/GRE/RVR/PRG/PBR contract'larını bypass etmez.
- Fixed soru sayısı/süre/kategori yüzdesi yoktur.
- Incomplete/missed weekly exam failure/debt değildir.
- D-044 granular Skill/Objective localization korunur.

Canonical: `docs/WEEKLY_ASSESSMENT_SPEC.md`.

## Zorunlu yürütme
`PRE-STEP GitHub refresh → gerekiyorsa Research/Coding/QA → spec/çıktı → değerlendirme → POST-STEP GitHub sync → checklist/completion note → sonraki adım`

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle ✅
### [x] 1A — Ana ürün amacı — `docs/PRODUCT_REQUIREMENTS.md`
### [x] 1B — V1 kapsamı — `docs/V1_SCOPE.md`
### [x] 1C — Başarı kriterleri — `docs/V1_SUCCESS_CRITERIA.md`
### [x] 1D — Non-goals — `docs/NON_GOALS.md`

> **AŞAMA 1 tamamlandı.** D-041 uzun vadeli çıkış hedefini genişletti; V1 ayrımı değişmedi.

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla ✅
### [x] 2A — Bilgi birimleri — `docs/LEARNING_ENGINE_SPEC.md` — D-021
### [x] 2B — Topic durumları — `docs/TOPIC_STATE_MACHINE.md` — D-023
### [x] 2C — Mastery sinyalleri — `docs/MASTERY_SIGNALS_SPEC.md` — D-025
### [x] 2D — AI / ipucu etkisi — `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md` — D-026
### [x] 2E — Mastery formülü v0 — GRE-v0 — D-031
### [x] 2F — Unutma modeli — RVR-v0 — D-032

**D-044 clarification:** geniş Domain/Topic başlıkları weakness/mastery atomu değildir. Zayıflık/remediation mümkün olduğunca canonical Skill / Objective seviyesinde lokalize edilir.

> **AŞAMA 2 tamamlandı — yeniden açılmadı.**

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla ✅

### [x] 3A — Günlük kapasite — D-033
- explicit daily time = hard budget,
- no auto-overrun,
- split / smaller alternative / defer,
- no task/backlog debt.

### [x] 3B — Görev kategorileri — D-034
- `State → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`,
- purpose/activity/track/evidence ayrı,
- unresolved LearningNeed kalıcı; old task debt değil.

### [x] 3C — Öncelik puanı — PBR-v0 / D-035
- eligibility priority'den önce,
- P0–P4 semantic bands,
- deterministic rank vector.

### [x] 3D — Prerequisite davranışı — PRG-v0 / D-036
- runtime `Skill → Skill`,
- hard/soft edges,
- branch-local blocking,
- contamination guard.

### [x] 3E — Hızlı öğrenme — VDW-v0 / D-037
- Objective-level validated coverage waiver,
- diagnostic mastery'nin kolay alternatifi değildir.

### [x] 3F — Kaçırılan günler — SRR-v0 / D-038
- absence failure/debt değildir,
- current-state fresh replan.

### [x] 3G — Açıklanabilir planner — PDT-v0 / D-039
- structured reason trace,
- deterministic/explainable core.

### [x] 3H — Planner simülasyonu
**Final:** `docs/PLANNER_SIMULATION_SUITE.md`

```text
16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical cross-spec contradiction
```

> **AŞAMA 3 tamamlandı.**

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla

### [x] 4A — Günlük mikro değerlendirme — DMA-v0 / D-040
**Final:** `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
- daily assessment quota değildir,
- Objective-matched evidence,
- H0/assistance/provenance guards,
- prerequisite fairness,
- invalid/provisional item safety,
- evidence→GRE/RVR→replan integration.

### [x] 4B — Haftalık sınav — WBA-v0 / D-045
**Final:** `docs/WEEKLY_ASSESSMENT_SPEC.md`

- weekly assessment tek overall score/pass-fail değildir,
- item seçilmeden önce `WeeklyAssessmentBlueprint` oluşturulur,
- blueprint role family'leri: recent required progress, weakness/verification, critical prerequisite confidence, retention due, integration/transfer, gerektiğinde parallel English,
- role family'leri fixed quota değildir,
- fixed soru sayısı / fixed süre / fixed kategori yüzdesi yoktur,
- weekly evidence sırf weekly olduğu için ekstra weight almaz; GRE-v0/RVR-v0 normal pipeline'ına girer,
- PRG prerequisite fairness + root-cause contamination guard,
- variant/dependency family diversity,
- Objective-specific evidence modality,
- H0 independent measurement; H1–H4 positive independent mastery değildir,
- invalid/ambiguous/prerequisite-contaminated/provisional item safety,
- safe split/pause/resume; incomplete session failure değildir,
- missed weekly exam backlog/debt/stack değildir,
- first clean contradiction instant unmastery değildir,
- raw score broad `Python failed` gibi coarse state yazamaz; D-044 granular weakness localization korunur,
- common `AssessmentBlueprint / AssessmentBlueprintSlot / AssessmentSessionResult` abstraction 4C için kilitlendi.

**PRE/POST notu:** 4B fresh GitHub PRE-STEP refresh ile yürütüldü. Ayrı Research AI kullanılmadı; calibrated soru sayısı/puan/süre iddiası üretilmedi. Empirik calibration AŞAMA 18'e bırakıldı.

### [ ] 4C — Aylık yeterlilik sınavı — **AKTİF**
Kesinleştirilecek:
- monthly purpose/scope ve DMA/WBA farkı,
- WBA common blueprint/result contract'ının monthly specialization'ı,
- daha geniş transfer/integration,
- critical prerequisite/capability revalidation,
- older/retention + recent progress dengesi,
- professional-readiness'e doğru daha geniş evidence aggregation ama final readiness ile karıştırmama,
- fixed total score ile mastery vermeme,
- capacity / safe split / pause / incomplete,
- H0/H1–H4,
- invalid/ambiguous/provisional item safety,
- D-044 granular Skill/Objective localization,
- result → GRE/RVR/remediation/PRG/planner,
- 4D Question Bank schema handoff.

### [ ] 4D — Soru bankası
- trusted item metadata,
- variant/dependency group,
- Objective attribution,
- difficulty/complexity,
- validation/versioning,
- assessment blueprint slot matching,
- long curriculum'da scalable item lifecycle.

### [ ] 4E — AI-generated soru doğrulaması
- AI candidate trusted bank'e otomatik giriş değildir,
- correctness/ambiguity/prerequisite/duplicate/target-fit/rubric-evaluator validator.

---

# AŞAMA 5 — Curriculum ve Knowledge Graph İskeleti

AŞAMA 5 özellikle **schema/backbone** aşamasıdır; full ayrıntılı konu listesi burada yazılmaz.

### [ ] 5A — Ana domain haritası
- 4+ year professional domain envelope,
- Technical English paralel hat,
- Python + C foundation,
- systems → distributed → performance → GPU → inference → AI infra omurgası,
- open source / projects / capstone professional layer.

### [ ] 5B — Graph / metadata sözleşmesi
- Domain / Module / Topic / Skill / Objective relations,
- prerequisite,
- required/criticality,
- evidence contracts,
- retention,
- remediation / diagnostic tags,
- project/capstone attribution,
- curriculum versioning / freshness.

### [ ] 5C — İlk 8–12 haftalık curriculum backbone
- V1 production subgraph iskeleti,
- canonical IDs,
- full route'a sonradan genişleyebilir yapı.

### [ ] 5D — Graph architecture QA
- cycle/dead-end,
- hidden prerequisite,
- duplicate canonical Skill,
- scalability / versioning kontrolü.

**Çıkış:** AŞAMA 6 detailed capability taxonomy'sini taşıyacak graph sözleşmesi.

---

# AŞAMA 6 — Granular Capability Map / Öğrenme Rotasını Alt Becerilere Böl

Canonical charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

Amaç: uygulamanın `Python zayıf` demesi yerine örneğin `Python → Control Flow → Loops → while termination` düzeyinde zayıflığı görebilmesi ve yalnız gerekli parçaya reteach/practice/retest uygulayabilmesi.

### [ ] 6A — Granularity + naming standardı
- Domain/Module/Topic/Skill/Objective sınırları,
- canonical ID convention,
- over-fragmentation ve too-broad Skill guard.

### [ ] 6B — Full-route decomposition blueprint
Şu ana rotanın tamamı için ortak decomposition şablonu:

`Technical English (parallel) → Python → C → Linux + Git + Shell → DS&A → Modern C++ → Computer Architecture → OS + Memory → Concurrency / Parallel → Networking → Distributed + Storage/DB → Containers / Cloud / Observability → Performance / Profiling → GPU Architecture → CUDA → Triton → ML + Transformer → LLM Inference Internals → serving systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → OSS + large projects + capstone`

### [ ] 6C — Foundations detailed map
- Technical English,
- Python,
- C,
- Linux / Git / Shell,
- DS&A foundations.

**Python örnek decomposition beklentisi:** values/types, variables, operators, I/O, conditionals, loops, strings, collections, functions, scope, modules/imports, files, exceptions, debugging, iteration/comprehensions, classes where needed, typing, testing, environments/dependencies, packaging, CLI/automation, subprocess/OS, networking, async, multiprocessing, profiling, data/NumPy/tensor-facing Python, benchmark/infra scripting.

### [ ] 6D — Systems detailed map
- Modern C++,
- Computer Architecture,
- OS / Memory,
- Concurrency / Parallelism,
- Networking,
- Distributed Systems,
- Storage / Databases,
- Containers / Cloud / Observability,
- Performance / Profiling.

### [ ] 6E — GPU / ML / Inference detailed map
- GPU Architecture,
- CUDA,
- Triton,
- ML / Transformer foundations,
- LLM Inference Internals,
- vLLM/SGLang/TensorRT-LLM-style serving systems,
- KV Cache / batching / scheduling / quantization,
- Multi-GPU / NCCL / RDMA,
- AI Infrastructure / GPU Infrastructure.

### [ ] 6F — Professional engineering / project map
- Git workflow / code review,
- testing/build/debug/profiling,
- documentation/design docs,
- reproducible benchmarks,
- open-source contribution workflow,
- integrated projects,
- capstone capability decomposition.

### [ ] 6G — Weakness localization + remediation mapping
- error/evidence → exact Skill/Objective attribution,
- broad-domain overreaction guard,
- targeted reteach / practice / verification,
- derived Domain/Topic summary ayrı, canonical weakness ayrı.

### [ ] 6H — Coverage + prerequisite + external Research QA
- missing-domain audit,
- hidden prerequisite audit,
- duplicate/cycle/dead-end audit,
- technology freshness audit,
- bağımsız Research AI coverage validation.

**Acceptance:** bütün rota sufficiently granular Skill/Objective haritasına ayrılmış, weakness localization ve targeted remediation mümkün, sonraki UX/data/content aşamaları bu ID'leri tüketebilir durumda.

---

# AŞAMA 7 — İngilizce Paralel Hattı
Bağlayıcı: `docs/ENGLISH_FOUNDATION_RULES.md` + AŞAMA 6 Technical English capability map.

### [ ] 7A — Başlangıç ölçümü
### [ ] 7B — A1/A2/B1/B2+ teknik hedefleri
### [ ] 7C — Günlük English bileşeni
### [ ] 7D — Teknik entegrasyon
### [ ] 7E — English mastery

> AŞAMA 6 English'in neye bölündüğünü; AŞAMA 7 ise English'in nasıl öğretileceği/ölçüleceği davranışını tasarlar.

---

# AŞAMA 8 — UX ve Ekranlar
### [ ] 8A — Bilgi mimarisi
### [ ] 8B — Ana ekran
### [ ] 8C — Günlük çalışma akışı
### [ ] 8D — Sınav UX
### [ ] 8E — Skill/progress/weakness UX
- broad domain summary + granular weakness drill-down,
- kullanıcıya aşırı node karmaşası göstermeden doğru derinliği açma.
### [ ] 8F — Tasarım sistemi
### [ ] 8G — Wireframe/prototip

---

# AŞAMA 9 — Teknik Mimari ve Veri Modeli
### [ ] 9A — Mobil teknoloji seçimi
### [ ] 9B — Veri saklama/local-first
### [ ] 9C — Domain veri modeli
- canonical Skill/Objective state,
- granular weakness/remediation state,
- assessment blueprint/session/result references,
- years-long curriculum/user history,
- curriculum versions/migrations,
- project/capstone evidence references.
### [ ] 9D — Servis sınırları
### [ ] 9E — AI entegrasyon mimarisi
### [ ] 9F — Test stratejisi / performance budgets

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

> Runtime granular weakness davranışı AŞAMA 6 IDs üzerinde test edilir.

---

# AŞAMA 13 — Assessment + Retention + Remediation Implementasyonu
### [ ] 13A — Haftalık sınav — WBA-v0 implementation
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

AŞAMA 15 **tam 4+ yıllık curriculum değildir**; AŞAMA 6 map'inin başlangıç bölümünü production-quality içeriğe dönüştürür.

### [ ] 15A — Computer / Programming Fundamentals
### [ ] 15B — Python Foundations
### [ ] 15C — C Foundations
### [ ] 15D — Memory Foundations
### [ ] 15E — Linux / Git / Shell Foundations
### [ ] 15F — English A0→A1/A2 başlangıç paketi
### [ ] 15G — Assessment content
### [ ] 15H — Content QA

Her paket mümkün olduğunca `concept → guided → independent → debugging → explanation → transfer → retention → project` derinlik modelini destekler.

---

# AŞAMA 16 — İlerleme / Analitik / Ayarlar
### [ ] 16A — Skill analytics
### [ ] 16B — Öğrenme geçmişi
### [ ] 16C — Progress / weakness kuralları
- gün/year countdown yerine verified capability,
- `Python overall` gibi derived summary + alt Skill detayları,
- zayıflık hangi alt capability'de açıkça görülebilir.
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
- WBA-v0 session duration/user burden,
- blueprint target balance,
- false-positive/false-negative behavior,
- invalid/provisional item rate,
- split/pause/incomplete UX.
### [ ] 18E — Teknik / performance QA
### [ ] 18F — Düzeltme döngüsü

Pilot granular Skill diagnosis'ın doğru remediation üretip üretmediğini de test eder.

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

AŞAMA 20'nin amacı AŞAMA 6'da haritalanan full professional route'u modül modül gerçek content/evidence/project paketlerine dönüştürmek ve sürekli QA etmektir.

### [ ] 20A — Modern C++ + Advanced Python + Professional Tooling
### [ ] 20B — Systems + Architecture + Performance
### [ ] 20C — Networking + Distributed Systems + Storage
### [ ] 20D — GPU Architecture + CUDA
### [ ] 20E — Triton + ML/Transformer + LLM Inference
### [ ] 20F — Serving Engines + KV Cache / Batching / Scheduling / Quantization
### [ ] 20G — Multi-GPU / NCCL / RDMA / AI Infrastructure
### [ ] 20H — Open Source + Engineering Practice + Career Readiness
### [ ] 20I — Sürekli Curriculum QA + Büyük Entegre Projeler + Professional Capstones

- repository/source-tree reading,
- issue reproduction,
- accepted OSS contribution hazırlığı,
- test/benchmark contributions,
- design docs / technical writing,
- realistic failure/debugging exercises,
- final capstone families,
- professional readiness evidence mapping.

> Full curriculum completion takvimle değil professional-readiness evidence ile tanımlanır.

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4B`  
**Aktif:** **`4C — Aylık yeterlilik sınavı`**

**Bağlayıcı:** D-041 professional target; D-042 Python foundation; D-044 granular capability map; D-045 WBA-v0.  
**Geri çekilen:** D-043 specialization-stage yorumu.

Bir sonraki yürütme: **4C başlamadan yeni PRE-STEP GitHub refresh → 4C monthly assessment policy → POST-STEP sync.**
