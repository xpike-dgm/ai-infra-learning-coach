# AI Infra Learning Coach — Master Geliştirme Planı

**Durum:** AKTİF / CANONICAL DETAYLI PLAN  
**Son senkron:** 2026-08-25

Sabit adım kimliklerinin canonical kaynağı `docs/EXECUTION_INDEX.md` dosyasıdır. Bu dosya ayrıntılı checklist ve completion notlarını onunla senkron tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

## 2026-08-24 kapsam genişletmesi — D-041

Uzun vadeli curriculum artık yaklaşık üç yıllık bir horizon ile sınırlı değildir. Nihai hedef:

> **Gerektiğinde 4+ yıl veya daha uzun sürebilecek mastery-gated bir rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability oluşturmak.**

`4+ yıl` countdown değildir. Final readiness; required Skill mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ile verilir.

Bağlayıcı: `docs/PROFESSIONAL_READINESS_TARGET.md`.

V1 ayrımı korunur: ilk production-quality 8–12 haftalık curriculum + gerçek learning engine ile release edilir; full professional curriculum V1 ön koşulu değildir.

## 2026-08-25 rota güncellemeleri — D-042 / D-043

- **Python** common foundation rotasına resmi olarak eklendi; C/C++ yerine değil, automation/testing/benchmark/ML-infra tooling tarafında tamamlayıcı ana dil olarak kullanılacak.
- Proje yürütme planı **1–20** ana aşamaya genişletildi; mevcut 1–19 kodları korunarak sona **AŞAMA 20 — Uzmanlık Dallarına Ayrılma ve Track Sistemi** eklendi.
- Uzun rota tek düz çizgi olarak bitmeyecek: ortak systems/distributed/GPU/inference çekirdeğinden sonra specialization tracks açılacak.

## Zorunlu yürütme
`PRE-STEP GitHub refresh → gerekiyorsa Research/Coding/QA → spec/çıktı → değerlendirme → POST-STEP GitHub sync → checklist/completion note → sonraki adım`

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle ✅
### [x] 1A — Ana ürün amacı — `docs/PRODUCT_REQUIREMENTS.md`
### [x] 1B — V1 kapsamı — `docs/V1_SCOPE.md`
### [x] 1C — Başarı kriterleri — `docs/V1_SUCCESS_CRITERIA.md`
### [x] 1D — Non-goals — `docs/NON_GOALS.md`

> **AŞAMA 1 tamamlandı.** D-041, ürünün uzun vadeli çıkış hedefini genişletmiştir; Aşama 1'in V1/product-core kararlarını iptal etmez.

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
- duration semantic priority'den sonra.

### [x] 3D — Prerequisite davranışı — PRG-v0 / D-036
- runtime `Skill → Skill`,
- hard/soft edges,
- readiness `ready | ready_due | uncertain | not_ready`,
- review_due no-lock,
- branch-local blocking,
- contamination guard.

### [x] 3E — Hızlı öğrenme — VDW-v0 / D-037
- validated Objective-level diagnostic waiver,
- partial waiver,
- GRE/prerequisite false-skip guards.

### [x] 3F — Kaçırılan günler — SRR-v0 / D-038
- absence failure/debt değildir,
- stale plan replay edilmez,
- current-state re-entry.

### [x] 3G — Açıklanabilir planner — PDT-v0 / D-039
- structured decision trace,
- internal/user-facing explanation ayrımı,
- deterministic pseudocode,
- LLM source of truth değildir.

### [x] 3H — Planner simülasyonu
**Final:** `docs/PLANNER_SIMULATION_SUITE.md`

```text
16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical cross-spec contradiction
```

> **AŞAMA 3 tamamlandı — 2026-08-24.**

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

> **4A tamamlandı — 2026-08-24.**

### [ ] 4B — Haftalık sınav — **AKTİF**
Kesinleştirilecek:
- weekly assessment amacı ve DMA-v0'dan farkı,
- Skill/Objective blueprint,
- required/critical coverage,
- modality/family/context diversity,
- weakness + recent progress + prerequisite risk dengesi,
- fixed sahte optimum olmadan composition,
- capacity / pause / incomplete,
- H0/H1–H4,
- invalid/provisional item safety,
- weekly result → GRE/RVR/remediation/PRG/planner,
- 4C monthly assessment ortak contract.

### [ ] 4C — Aylık yeterlilik sınavı
- daha geniş transfer/integration,
- critical prerequisite revalidation,
- professional readiness'e doğru daha geniş evidence aggregation,
- tek final puanla mastery vermeme.

### [ ] 4D — Soru bankası
- trusted item metadata,
- variant family / dependency group,
- Objective attribution,
- difficulty/complexity,
- validation/versioning,
- uzun curriculum'da scalable bank/authoring contract.

### [ ] 4E — AI-generated soru doğrulaması
- AI candidate trusted bank'e otomatik giriş değildir,
- correctness/ambiguity/prerequisite/duplicate/target-fit validator.

---

# AŞAMA 5 — Curriculum ve Knowledge Graph

D-041 sonrası Aşama 5'in ek görevi: **4+ yıllık professional curriculum'u taşıyabilecek extensible graph/metadata tasarlamak.** İlk etapta tüm node'lar yazılmayacak; yapı buna hazır olacak. D-042 Python'ı common foundation içine, D-043 ise future specialization track metadata'sını graph tasarımına dahil eder.

### [ ] 5A — Ana domain haritası
- full professional domain envelope,
- foundation → systems → GPU → inference → AI Infrastructure,
- Python + C common programming foundations,
- infra-relevant DS&A, architecture, storage, cloud/observability/performance,
- professional engineering/tooling tracks,
- future specialization branch points.

### [ ] 5B — Topic metadata
- prerequisite,
- required/criticality,
- evidence contracts,
- retention profile,
- professional capability tags,
- specialization-track applicability,
- project/capstone attribution,
- curriculum versioning.

### [ ] 5C — İlk 8–12 haftalık curriculum graph
- V1 production package,
- full route'un başlangıç alt grafiği,
- ileride genişlemeyi/branching'i engellemeyen canonical IDs.

### [ ] 5D — Curriculum QA
- prerequisite integrity,
- missing foundations,
- hidden knowledge,
- professional-target coverage mapping,
- branch reachability / dead-end kontrolü.

---

# AŞAMA 6 — İngilizce Paralel Hattı
Bağlayıcı ön kural: `docs/ENGLISH_FOUNDATION_RULES.md`.
### [ ] 6A — Başlangıç ölçümü
### [ ] 6B — A1/A2/B1/B2+ teknik hedefleri
- exact CEFR çıkış gate research/curriculum design ile belirlenir,
- final hedef technical docs/papers/design review/interview/global team work.
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
- years-long curriculum/user history scalability,
- curriculum versions/migrations,
- capstone/project evidence references,
- common-core + specialization-track state.
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

D-041 sonrası Aşama 14 **tam 4+ yıllık curriculum değildir**; V1 için ilk production package'tır.

### [ ] 14A — Computer Fundamentals
### [ ] 14B — C Foundations
### [ ] 14C — Memory Foundations
### [ ] 14D — Linux Foundations
### [ ] 14E — English A0→A1/A2 başlangıç paketi
### [ ] 14F — Assessment content
### [ ] 14G — Content QA
### [ ] 14H — Python Foundations integration
- temel syntax ezberiyle sınırlı değil,
- automation/testing/benchmark scripting ve ileride ML/infra tooling'e köprü,
- V1 haftalarına sığan kapsam 5C sırasında kesinleştirilir; full Python depth daha sonra genişler.

Her paket mümkün olduğunca `concept → guided → independent → debugging → explanation → transfer → retention → project` derinlik modelini desteklemelidir.

---

# AŞAMA 15 — İlerleme / Analitik / Ayarlar
### [ ] 15A — Skill analytics
### [ ] 15B — Öğrenme geçmişi
### [ ] 15C — Progress kuralları
- gün/year countdown yerine verified capability,
- professional-readiness dimensions ileride desteklenebilir,
- specialization track ilerlemesi common-core ilerlemesinden ayrı gösterilebilir.
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

Pilot yalnız app UX'ini değil, ilk curriculum'un gerçek öğrenme/evidence davranışını da kalibre eder. Bu motor doğrulanmadan full 4+ year content'e kör üretim yapılmaz.

---

# AŞAMA 18 — Release APK
### [ ] 18A — Release hazırlığı
### [ ] 18B — Veri güvenilirliği
### [ ] 18C — Final regression
### [ ] 18D — APK / gerçek cihaz
### [ ] 18E — Release dokümantasyonu

**AŞAMA 18 V1 release = professional curriculum completion değildir.**

---

# AŞAMA 19 — Uzun Vadeli Professional Curriculum ve Kariyer Katmanı

D-041 sonrası Aşama 19'un amacı full professional route'un **ortak profesyonel omurgasını** modül modül üretmek, QA etmek ve professional readiness evidence'ına bağlamaktır. D-043 sonrası her ileri domain'de aynı derinlik zorunlu değildir; specialization depth AŞAMA 20'de dallanır.

### [ ] 19A — Modern C++ + Python + Professional Tooling paketi
- modern C++ language depth,
- Python: typing, testing, async/concurrency, multiprocessing, networking, profiling, packaging ve infra/ML automation,
- ownership/RAII/templates where relevant,
- build/test/debug/profiling,
- production-quality code habits.

### [ ] 19B — Systems + Architecture + Performance paketi
- computer architecture,
- OS/memory,
- concurrency/parallelism,
- Linux internals/tooling,
- performance engineering.

### [ ] 19C — Networking + Distributed Systems + Storage paketi
- networking,
- distributed coordination/failure thinking,
- storage/database fundamentals where infra-relevant,
- observability/reliability foundations.

### [ ] 19D — GPU Architecture + CUDA paketi
- execution/memory model,
- kernels,
- profiling,
- bandwidth/latency/occupancy reasoning,
- correctness/performance exercises.

### [ ] 19E — Triton + ML/Transformer + LLM Inference paketi
- transformer/inference fundamentals,
- Triton kernels,
- quantization,
- KV cache,
- batching/scheduling,
- serving engine internals.

### [ ] 19F — Multi-GPU / AI Infrastructure paketi
- NCCL/RDMA concepts,
- tensor/pipeline/data/expert parallel concepts as relevant,
- distributed inference,
- GPU scheduling/capacity,
- serving reliability/observability,
- vLLM/SGLang/TensorRT-LLM-style systems.

### [ ] 19G — Open Source + Engineering Practice
- repository/source-tree reading,
- issue reproduction,
- tests/benchmark contribution,
- PR/code-review workflow,
- technical writing/design docs.

### [ ] 19H — Career + Professional Readiness
- technical interview,
- portfolio/case-study packaging,
- job-skill mapping,
- degree/experience filters hakkında gerçekçi strategy,
- bridge-role readiness,
- professional capability gaps.

### [ ] 19I — Sürekli Curriculum QA + Professional Capstones
- technology freshness,
- prerequisite/evidence QA,
- integrated common-core capstone families,
- design + implementation + test + debugging + profiling + documentation + postmortem evidence,
- final professional-readiness gate'in gelecekte ayrı deterministic spec'e bağlanması.

> **Final curriculum completion takvimle değil professional-readiness evidence ile tanımlanacaktır.**

---

# AŞAMA 20 — Uzmanlık Dallarına Ayrılma ve Track Sistemi

D-043 sonrası amaç, ortak systems/distributed/GPU/inference temelini korurken kullanıcıyı tek düz rotada sonsuza kadar ilerletmek yerine profesyonel uzmanlık derinliğine dallandırmaktır.

### [ ] 20A — Ortak çekirdek çıkış kapısı
- specialization başlamadan önce hangi common Skills kesin required?
- systems, Linux, architecture, concurrency, networking, distributed fundamentals, performance, GPU/inference literacy için minimum verified gates,
- unresolved critical verification varsa branch depth'e geçiş yok.

### [ ] 20B — Uzmanlık dal haritası
İlk candidate family'ler:
1. **GPU Kernel & Performance Engineering**
2. **LLM Inference / Serving Systems**
3. **Distributed AI Infrastructure / Cluster & Scheduling**
4. **High-Speed Networking & Multi-GPU Systems**
5. **ML Compilers / Runtime Systems**
6. **AI Platform / Reliability / Capacity Engineering**

Bu liste final değildir; Research AI + gerçek iş rolü incelemesiyle değişebilir. Track adları framework-hype yerine kalıcı capability kümelerine dayanmalıdır.

### [ ] 20C — Track seçim politikası
- kullanıcı ilgisi/tercihi,
- verified strengths/weaknesses,
- prerequisite readiness,
- evidence quality,
- erişilebilir bridge roles / gerçek kariyer kısıtları,
- gerektiğinde güncel job-market research.

LLM, maaş veya popülerlik tek başına otomatik track seçemez. Final seçim kullanıcı tarafından onaylanır.

### [ ] 20D — Dal-specific curriculum/evidence contracts
Her track için:
- Skill/Objective graph,
- required/critical capability set,
- production-style projects,
- debugging/transfer requirements,
- profiling/benchmark requirements,
- open-source contribution targets,
- track-specific assessment/evidence.

### [ ] 20E — Secondary track / track değiştirme
- common core kaybolmaz,
- önceki valid mastery/evidence korunur,
- yeni track'e geçince yalnız eksik prerequisite ve required evidence açılır,
- primary + secondary specialization mümkün olabilir,
- track switching ceza/debt değildir.

### [ ] 20F — Uzmanlık capstone ve readiness gate
Selected track professional readiness en az:
- bağımsız design,
- implementation,
- testing,
- debugging,
- profiling/benchmark,
- trade-off explanation,
- documentation/design doc,
- postmortem/decision explanation

evidence'ı ister. Tek sınav veya tutorial project yeterli değildir.

### [ ] 20G — Track freshness / market QA
- framework/tool değişimleri düzenli izlenir,
- curriculum rol isimlerine kör bağlanmaz,
- değişmeyen systems capability'leri canonical tutulur,
- önemli market/technology shift'lerinde track map versionlanır.

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A`  
**Aktif:** **`4B — Haftalık sınav`**

**Bağlayıcı long-term target:** D-041 / `docs/PROFESSIONAL_READINESS_TARGET.md`.  
**Yeni route kararları:** D-042 Python foundation; D-043 AŞAMA 20 specialization tracks.

Bir sonraki yürütme: **4B başlamadan yeni PRE-STEP GitHub refresh → 4B weekly assessment policy → POST-STEP sync.**
