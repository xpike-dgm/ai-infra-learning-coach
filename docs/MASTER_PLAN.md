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
- D-048: AI-generated assessment validation = AIV-v0.

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

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla ✅

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
- AssessmentResource yalnız MCQ değildir; coding/debugging/system/transfer/integrated/language/testlet/template kaynakları içerir.
- Stable logical ID + immutable version.
- Lifecycle/trust/use ceiling ayrımı.
- Exact Skill/Objective/prerequisite/evidence/scope/role/evaluator/tool/artifact/duration metadata.
- Variant family / dependency-testlet / context family / transfer profile ayrımı.
- Per-user solution exposure global content'ten ayrı.
- Technology/content freshness ayrı.
- Bounded/indexed selection.

### [x] 4E — AI-generated soru doğrulaması — AIV-v0 / D-048
**Final:** `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md`

Final davranış:
- AI-generated resource `candidate` başlar; generator output kendi validation proof'u değildir.
- Minimum validation geçmeden user-facing selection'a çıkamaz.
- Schema/reference, technical correctness, answer/rubric, ambiguity, Objective/evidence fit, prerequisite/forbidden concept/language leakage, duplicate/family/dependency/context/transfer, evaluator/tool/artifact, technology freshness ve execution-safety ayrı validate edilir.
- Validation weighted confidence score değildir; final `use_ceiling` applicable check'lerin en kısıtlayıcısıdır.
- Semantic ceiling: `practice_only < low_stakes_assessment < standard_mastery_eligible < critical_mastery_eligible`.
- Practice-only yanlış bilgi toleransı değildir; correctness unresolved candidate blocked kalır.
- Generator self-review veya model majority vote high-stakes trust değildir; deterministic/executable/reference-grounded validation önceliklidir.
- Standard/critical mastery için strong independent validation + verified evaluator gerekir; tek uncalibrated LLM critical verified evidence üretemez.
- Hidden prerequisite/unknown English learner failure'a dönüştürülemez.
- Near duplicate yeni independent family değildir; uncertain family classification diversity credit artırmaz.
- Transfer/integration claim ve component attribution ayrıca validate edilir.
- Trusted-template inheritance yalnız validated invariants korunuyorsa mümkündür; semantic AI rewrite revalidation ister.
- Validator disagreement fail-safe olarak promotion'ı durdurur.
- Confirmed content bug invalidation + historical evidence review/repair açabilir; learner cezalandırılmaz.
- Heavy validation async/bounded çalışır; empirical validator accuracy calibration AŞAMA 14F/18'e bırakılır.

**PRE/POST notu:** 4E fresh GitHub PRE-STEP refresh ile yürütüldü. Ayrı Research AI kullanılmadı; fake confidence/majority/accuracy threshold uydurulmadı.

> **AŞAMA 4 tamamlandı: DMA-v0 + WBA-v0 + MCA-v0 + QAB-v0 + AIV-v0.**

---

# AŞAMA 5 — Curriculum ve Knowledge Graph İskeleti
### [ ] 5A — Ana domain haritası — **AKTİF**
- 4+ year professional envelope,
- Technical English paralel,
- Python + C foundation,
- systems → distributed → performance → GPU → inference → AI infra,
- OSS/projects/capstone layer,
- domain-level prerequisite/parallel relations,
- AŞAMA 6 granular decomposition için sınırlar.

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
- AI validation records/use ceilings,
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
### [ ] 18D — Assessment/item/exposure/validator kalibrasyonu
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

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`  
**AŞAMA 4:** ✅ TAMAMLANDI  
**Aktif:** **`5A — Ana domain haritası`**

**Bağlayıcı:** D-041 professional target; D-042 Python; D-044 granular map; D-045 WBA; D-046 MCA; D-047 QAB; D-048 AIV.  
**Geri çekilen:** D-043.

Bir sonraki yürütme: **5A başlamadan yeni PRE-STEP GitHub refresh → ana domain map/backbone → POST-STEP sync.**
