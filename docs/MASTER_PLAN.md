# AI Infra Learning Coach — Master Geliştirme Planı

**Durum:** AKTİF / CANONICAL DETAYLI PLAN  
**Son senkron:** 2026-08-25

Sabit adım kimliklerinin canonical kaynağı `docs/EXECUTION_INDEX.md` dosyasıdır. Bu dosya ayrıntılı checklist ve completion notlarını onunla senkron tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

## Uzun vadeli hedef — D-041
Gerektiğinde 4+ yıl veya daha uzun sürebilecek mastery-gated rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability oluşturmak. Takvim readiness gate değildir; final readiness mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ister.

V1 full curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality içerikle release edilebilir.

## Güncel rota/plan kararları
- D-042: Python common foundation'ın resmi parçasıdır; C/C++ yerine geçmez.
- D-043: standalone specialization-stage yorumu geri çekilmiştir.
- D-044: **AŞAMA 6 — Granular Capability Map**, bütün rotayı `Domain → Module → Topic → Skill → Learning Objective` seviyesinde ayrıntılandıracaktır.
- D-045: weekly assessment = WBA-v0.
- D-046: monthly assessment = MCA-v0.
- D-047: assessment resource bank = QAB-v0.

## Zorunlu yürütme
`PRE-STEP GitHub refresh → gerekiyorsa Research/Coding/QA → spec/çıktı → değerlendirme → POST-STEP GitHub sync → sonraki adım`

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle ✅
### [x] 1A — Ana ürün amacı — `docs/PRODUCT_REQUIREMENTS.md`
### [x] 1B — V1 kapsamı — `docs/V1_SCOPE.md`
### [x] 1C — Başarı kriterleri — `docs/V1_SUCCESS_CRITERIA.md`
### [x] 1D — Non-goals — `docs/NON_GOALS.md`

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla ✅
### [x] 2A — Bilgi birimleri — D-021
### [x] 2B — Topic durumları — D-023
### [x] 2C — Mastery sinyalleri — D-025
### [x] 2D — AI / ipucu etkisi — D-026
### [x] 2E — Mastery formülü v0 — GRE-v0 / D-031
### [x] 2F — Unutma modeli — RVR-v0 / D-032

D-044 clarification: broad Domain/Topic tanı atomu değildir; weakness/remediation mümkün olduğunca Skill/Objective seviyesinde lokalize edilir.

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

**Sonuç:** 16/16 scenarios PASS, 20/20 invariants PASS, 0 critical contradiction.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla

### [x] 4A — Günlük mikro değerlendirme — DMA-v0 / D-040
**Final:** `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
- daily quota değildir,
- Objective-matched evidence,
- H0/assistance/provenance,
- prerequisite fairness,
- invalid/provisional safety,
- evidence → GRE/RVR → planner.

### [x] 4B — Haftalık sınav — WBA-v0 / D-045
**Final:** `docs/WEEKLY_ASSESSMENT_SPEC.md`
- blueprint-before-items,
- recent progress + weakness/verification + critical prerequisite + retention + integration/transfer + gerektiğinde English,
- no fixed score/question/time quota,
- family/context diversity,
- split/pause/resume,
- missed/incomplete exam debt değildir,
- common `AssessmentBlueprint / Slot / SessionResult` abstraction.

### [x] 4C — Aylık yeterlilik sınavı — MCA-v0 / D-046
**Final:** `docs/MONTHLY_ASSESSMENT_SPEC.md`
- longitudinal state-based sampling,
- persistent concern + delayed retention,
- cross-topic transfer + integrated application,
- need-based critical capability revalidation,
- no cumulative-everything/pass-score,
- professional checkpoint != final readiness,
- result granular evidence/state üzerinden planner priority'yi değiştirir.

### [x] 4D — Soru / assessment resource bank — QAB-v0 / D-047
**Final:** `docs/QUESTION_BANK_SPEC.md`

Final davranış:
- Question Bank yalnız MCQ değil, `AssessmentResource` bank'idir: recognition/recall/code reading/coding/debugging/hands-on system/explanation/transfer/integrated/language/testlet/template.
- `resource_id` logical identity; published `resource_version` immutable'dır; Attempt exact version'a bağlanır.
- Lifecycle: `draft | candidate | validated | trusted | deprecated | invalidated | retired`.
- Lifecycle ile `use_ceiling` ayrıdır; bank'te bulunmak otomatik high-stakes eligibility değildir.
- Exact target Skill/Objective, prerequisites, forbidden concepts, language prerequisites, activity/evidence, scope/blueprint roles, evaluator/tools/artifact/duration metadata vardır.
- Variant family, dependency/testlet group, context family ve transfer profile ayrı semantics taşır.
- Integrated task global PASS'i component Objectives'e yayamaz.
- Difficulty fake numeric mastery multiplier değildir; semantic difficulty + complexity profile kullanılır.
- User exposure/solution exposure global bank content'inden ayrıdır; fixed universal cooldown yoktur.
- Learner freshness ile technology/content freshness ayrıdır; stale resource strong assessment için ineligible olur.
- Deprecated ≠ invalidated; invalidated version historical evidence audit/repair akışına girebilir.
- Selector bounded/indexed çalışır; full-bank scan hedeflenmez.
- Parameterized template instances yeni independent family sayılmaz.
- AI-generated resource varsayılan `candidate` başlar; 4E validation olmadan trusted/mastery-changing use'a yükselmez.

**PRE/POST notu:** 4D fresh GitHub PRE-STEP refresh ile yürütüldü. Ayrı Research AI kullanılmadı; psychometric calibration veya validator accuracy eşiği uydurulmadı. Empirical item calibration AŞAMA 18'e; AI validation policy 4E'ye bırakıldı.

### [ ] 4E — AI-generated soru doğrulaması — **AKTİF**
Kesinleştirilecek:
- generated candidate lifecycle entry,
- schema completeness,
- technical correctness,
- expected answer/rubric correctness,
- ambiguity / multiple-valid-answer detection,
- target Objective ve evidence-modality fit,
- prerequisite completeness / forbidden-concept leakage,
- duplicate / near-duplicate / variant-family classification,
- dependency/testlet/context/transfer validation,
- evaluator/tool/artifact compatibility,
- technology/source freshness,
- automated/deterministic/review boundaries,
- risk-based `use_ceiling` promotion,
- trusted-template inheritance limits,
- revalidation/invalidation,
- uncertain validator fail-safe behavior.

**4E çıkışı:** AI-generated resource'ın hangi koşulda practice-only kalacağı, validated olacağı veya high-stakes trusted use'a yükselebileceği deterministic/auditable policy.

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

> AŞAMA 5 schema/backbone; detailed decomposition AŞAMA 6.

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
- Modern C++, Architecture, OS/Memory, Concurrency, Networking, Distributed Systems, Storage/DB, Containers/Cloud/Observability, Performance.

### [ ] 6E — GPU / ML / Inference detailed map
- GPU Architecture, CUDA, Triton, ML/Transformer, inference internals, serving engines, KV/batching/scheduling/quantization, Multi-GPU/NCCL/RDMA, AI Infra.

### [ ] 6F — Professional engineering / project map
- Git/code review, testing/build/debug/profiling, design docs, benchmarks, OSS workflow, integrated projects, capstone.

### [ ] 6G — Weakness localization + remediation mapping
### [ ] 6H — Coverage / prerequisite / external Research QA

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
- granular Skill/Objective state,
- assessment resource identity/version/lifecycle,
- per-user exposure,
- years-long curriculum/user history,
- curriculum versions/migrations.
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
### [ ] 18D — Assessment/item/exposure kalibrasyonu
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

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4D`  
**Aktif:** **`4E — AI-generated soru doğrulaması`**

Bir sonraki yürütme: **4E başlamadan yeni PRE-STEP GitHub refresh → AI-generated item validation policy → POST-STEP sync.**
