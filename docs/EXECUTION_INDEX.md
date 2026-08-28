# AI Infra Learning Coach — Numaralı Yürütme İndeksi

Bu belge projenin sabit adım kodlarının canonical indeksidir. Ayrıntılı checklist `docs/MASTER_PLAN.md`, anlık durum `docs/STEP_STATUS.md` içindedir.

## Kullanım kuralı
- Ana aşamalar **1–20**.
- Alt adımlar `1A`, `2E`, `12F` biçiminde kullanılır.
- Tamamlanan `[x]`, bekleyen `[ ]`.
- Her adım öncesi/sonrası `docs/PROJECT_MEMORY_PROTOCOL.md` uygulanır.
- D-050 sonrası `PROJECT_CONTEXT`, `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG`, `DECISIONS` her step kapanışında istisnasız kontrol edilir ve repo-wide stale-reference scan yapılır.
- D-041: full curriculum 4+ yıllık professional-readiness horizon'ına genişletildi.
- D-042: Python ana technical foundation rotasına resmi olarak eklendi.
- D-043: yanlış yorum nedeniyle geri çekildi; standalone specialization stage canonical değildir.
- D-044: **AŞAMA 6 — Granular Capability Map** planlama aşamalarının arasına eklendi.
- D-045: 4B final weekly model `WBA-v0`.
- D-046: 4C final monthly model `MCA-v0`.
- D-047: 4D final assessment resource bank modeli `QAB-v0`.
- D-048: 4E final AI-generated assessment validation modeli `AIV-v0`.
- D-049: 5A final domain backbone modeli `PDM-v0`.
- D-050: living-memory sync + repo-wide stale-reference audit zorunlu.
- D-051: 5B final knowledge-graph contract `KGC-v0`.
- D-052: 5C final V1 foundation backbone `FBB-v0`.
- D-053: 5D final foundation graph architecture QA `GQA-v0`; corrective seed patch PASS.
- D-054: 6A final granularity/naming contract `GNS-v0`; semantic entity boundaries + stable logical ID rules.
- D-055: local çalışan agent main manager rolünü devralabilir; workflow contracts değişmez; transition kendi başına 6B execution değildi.
- D-056: 6B final full-route decomposition blueprint `FRDB-v0`; 6C–6F için ortak machine-readable authoring package + QA contract.
- D-057: 6C final Foundations detailed map `FDM-v0`; D01–D05 package + FBB 41/47 seed ratification + internal graph QA.
- D-058: 6D final Systems detailed map `SDM-v0`; D06–D13 package + 6C registry cross-package reuse + combined hard-graph QA.
- D-059: 6E final GPU / ML / Inference detailed map `GIM-v0`; D14–D22 package + 6C/6D reuse + hard/soft Pass-B + combined hard-graph QA.

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle ✅
- [x] **1A — Ana ürün amacı** — `docs/PRODUCT_REQUIREMENTS.md`
- [x] **1B — V1 kapsamı** — `docs/V1_SCOPE.md`
- [x] **1C — Başarı kriterleri** — `docs/V1_SUCCESS_CRITERIA.md`
- [x] **1D — Non-goals** — `docs/NON_GOALS.md`

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla ✅
- [x] **2A — Bilgi birimleri** — `docs/LEARNING_ENGINE_SPEC.md`
- [x] **2B — Topic durumları** — `docs/TOPIC_STATE_MACHINE.md`
- [x] **2C — Mastery sinyalleri** — `docs/MASTERY_SIGNALS_SPEC.md`
- [x] **2D — AI/ipucu etkisi** — `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- [x] **2E — Mastery formülü v0** — `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md` — GRE-v0 / D-031
- [x] **2F — Unutma modeli** — `docs/RETENTION_FORGETTING_SPEC.md`, `docs/2F_RESEARCH_VALIDATION.md` — RVR-v0 / D-032

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla ✅
- [x] **3A — Günlük kapasite** — `docs/ADAPTIVE_PLANNER_SPEC.md` — D-033
- [x] **3B — Görev kategorileri** — `docs/TASK_TAXONOMY_SPEC.md` — D-034
- [x] **3C — Öncelik puanı** — `docs/PRIORITY_POLICY_SPEC.md` — PBR-v0 / D-035
- [x] **3D — Prerequisite davranışı** — `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036
- [x] **3E — Hızlı öğrenme** — `docs/DIAGNOSTIC_WAIVER_SPEC.md` — VDW-v0 / D-037
- [x] **3F — Kaçırılan günler** — `docs/MISSED_DAY_RECOVERY_SPEC.md` — SRR-v0 / D-038
- [x] **3G — Açıklanabilir planner** — `docs/PLANNER_EXPLAINABILITY_SPEC.md` — PDT-v0 / D-039
- [x] **3H — Planner simülasyonu** — `docs/PLANNER_SIMULATION_SUITE.md`
  - 16/16 scenarios PASS, 20/20 invariants PASS, 0 critical contradiction.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla ✅
- [x] **4A — Günlük mikro değerlendirme** — `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` — DMA-v0 / D-040
- [x] **4B — Haftalık sınav** — `docs/WEEKLY_ASSESSMENT_SPEC.md` — WBA-v0 / D-045
- [x] **4C — Aylık yeterlilik sınavı** — `docs/MONTHLY_ASSESSMENT_SPEC.md` — MCA-v0 / D-046
- [x] **4D — Soru / assessment resource bank** — `docs/QUESTION_BANK_SPEC.md` — QAB-v0 / D-047
- [x] **4E — AI-generated soru doğrulaması** — `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md` — AIV-v0 / D-048

> **AŞAMA 4 tamamlandı.**

---

# AŞAMA 5 — Curriculum ve Knowledge Graph İskeleti
- [x] **5A — Ana domain haritası** — `docs/CURRICULUM_DOMAIN_MAP.md` — PDM-v0 / D-049
  - 23 route family canonical high-level envelope,
  - Technical English parallel,
  - Python/C/Linux early complementary foundations,
  - systems → distributed/platform → performance → GPU → inference → AI infra,
  - ML/Transformer supporting domain,
  - professional/OSS/project/capstone cross-cutting evidence layer,
  - domain-level authoring relations != runtime Skill prerequisite.
- [x] **5B — Graph / Topic metadata sözleşmesi** — `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — KGC-v0 / D-051
- [x] **5C — İlk 8–12 haftalık curriculum backbone** — `docs/V1_FOUNDATION_BACKBONE.md` — FBB-v0 / D-052
- [x] **5D — Graph architecture QA** — `docs/GRAPH_ARCHITECTURE_QA.md` — GQA-v0 / D-053

> AŞAMA 5 bütün ayrıntılı konu listesini yazmaz; graph'ın iskeletini kurar. Ayrıntılı decomposition AŞAMA 6'dadır.

---

# AŞAMA 6 — Granular Capability Map / Öğrenme Rotasını Alt Becerilere Böl
Ana charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`

- [x] **6A — Granularity + naming standardı** — `docs/GRANULARITY_NAMING_STANDARD.md` — GNS-v0 / D-054
- [x] **6B — Full-route decomposition blueprint** — `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md` — FRDB-v0 / D-056
- [x] **6C — Foundations detailed map** — `docs/FOUNDATIONS_DETAILED_MAP.md`, `curriculum/decomposition/6c_foundations/` — FDM-v0 / D-057
- [x] **6D — Systems detailed map** — `docs/SYSTEMS_DETAILED_MAP.md`, `curriculum/decomposition/6d_systems/` — SDM-v0 / D-058
- [x] **6E — GPU / ML / Inference detailed map** — `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`, `curriculum/decomposition/6e_gpu_ml_inference/` — GIM-v0 / D-059
- [x] **6F — Professional engineering / project map** — `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`, `curriculum/decomposition/6f_professional_engineering/` — PEM-v0 / D-060
- [x] **6G — Weakness localization + remediation mapping** — `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`, `curriculum/decomposition/6g_weakness_remediation/` — WLRM-v0 / D-061
- [x] **6H — Coverage / prerequisite / Research QA** — `docs/STAGE6_EXTERNAL_RESEARCH_QA.md` — S6ERQA-v0 / D-062; 549 Skill / 608 Objective / 950 edge; 549/549 DAG; 10/10 review resolved

---

# AŞAMA 7 — İngilizce Paralel Hattı
- [x] **7A — Başlangıç ölçümü** — `EED-v0 / D-063`
- [x] **7B — A1/A2/B1/B2+ teknik hedefleri** — `TECP-v0 / D-064`
- [x] **7C — Günlük English bileşeni** — `DECP-v0 / D-065`
- [x] **7D — Teknik entegrasyon** — `TEIP-v0 / D-066`
- [x] **7E — English mastery** — `TEPM-v0 / D-067`

---

# AŞAMA 8 — UX ve Ekranlar
- [x] **8A — Bilgi mimarisi** — `UXIA-v0 / D-068`
- [x] **8B — Ana ekran** — `THUX-v0 / D-069`
- [x] **8C — Günlük çalışma akışı** — `TRUX-v0 / D-070`
- [x] **8D — Sınav UX** — `ASUX-v0 / D-071`
- [x] **8E — Skill/progress/weakness UX** — `SPWX-v0 / D-072`
- [ ] **8F — Tasarım sistemi** **AKTİF**
- [ ] **8G — Wireframe/prototip**

---

# AŞAMA 9 — Teknik Mimari ve Veri Modeli
- [ ] **9A — Mobil teknoloji seçimi**
- [ ] **9B — Veri saklama / local-first**
- [ ] **9C — Domain veri modeli** — granular Skill/Objective state, assessment-resource versions/exposure/validation records, years-long history, curriculum versioning
- [ ] **9D — Servis sınırları**
- [ ] **9E — AI entegrasyon mimarisi**
- [ ] **9F — Test stratejisi**

---

# AŞAMA 10 — Mobil Proje İskeleti ve Tasarım Sistemini Kur
- [ ] **10A — Proje kurulumu**
- [ ] **10B — Navigation**
- [ ] **10C — Design system implementation**
- [ ] **10D — Local database**
- [ ] **10E — Temel uygulama sağlığı**

---

# AŞAMA 11 — Çekirdek Günlük Öğrenme Akışı MVP’sini Geliştir
- [ ] **11A — Today ekranı**
- [ ] **11B — Task runner**
- [ ] **11C — Session state**
- [ ] **11D — Günlük mikro quiz**
- [ ] **11E — Gün sonu**

---

# AŞAMA 12 — Mastery ve Adaptif Planner’ı Koda Dök
- [ ] **12A — Mastery Engine v1**
- [ ] **12B — Prerequisite Engine**
- [ ] **12C — Planner Engine v1**
- [ ] **12D — Replan**
- [ ] **12E — Explanation / reason codes**
- [ ] **12F — Sanal kullanıcı testleri**

---

# AŞAMA 13 — Haftalık/Aylık Sınav, Retention ve Remediation’ı Geliştir
- [ ] **13A — Haftalık sınav**
- [ ] **13B — Aylık sınav**
- [ ] **13C — Spaced repetition**
- [ ] **13D — Remediation Engine**
- [ ] **13E — Program değişiklik raporu**

---

# AŞAMA 14 — AI Tutor ve Akıllı Değerlendirme Katmanını Geliştir
- [ ] **14A — Tutor davranış sözleşmesi**
- [ ] **14B — Yanlış analizi**
- [ ] **14C — Alternatif anlatım**
- [ ] **14D — Kod değerlendirme**
- [ ] **14E — AI-generated code comprehension check**
- [ ] **14F — Açık uçlu cevap değerlendirme**
- [ ] **14G — Provider abstraction / fallback**

---

# AŞAMA 15 — İlk 8–12 Haftalık Gerçek Eğitim İçeriğini Üret ve QA Et
- [ ] **15A — Computer / Programming Fundamentals**
- [ ] **15B — Python Foundations**
- [ ] **15C — C Foundations**
- [ ] **15D — Memory Foundations**
- [ ] **15E — Linux / Git / Shell Foundations**
- [ ] **15F — English A0→A1/A2 başlangıç paketi**
- [ ] **15G — Assessment content**
- [ ] **15H — Content QA**

---

# AŞAMA 16 — İlerleme, Analitik, Ayarlar ve Günlük Kullanım Araçları
- [ ] **16A — Skill analytics**
- [ ] **16B — Öğrenme geçmişi**
- [ ] **16C — Progress / weakness gösterim kuralları**
- [ ] **16D — Ayarlar**
- [ ] **16E — Bildirimler**

---

# AŞAMA 17 — UI/UX Polish ve Erişilebilirlik
- [ ] **17A — Görsel polish**
- [ ] **17B — Motion**
- [ ] **17C — Kullanılabilirlik**
- [ ] **17D — Accessibility**

---

# AŞAMA 18 — Gerçek Kullanım Pilotu, Kalibrasyon ve QA
- [ ] **18A — Pilot başlangıcı**
- [ ] **18B — Planner gözlemi**
- [ ] **18C — Mastery kalibrasyonu**
- [ ] **18D — Assessment/item/validator kalibrasyonu**
- [ ] **18E — Teknik / performance QA**
- [ ] **18F — Düzeltme döngüsü**

---

# AŞAMA 19 — Release APK ve Kullanıma Hazır Sürüm
- [ ] **19A — Release hazırlığı**
- [ ] **19B — Veri güvenilirliği**
- [ ] **19C — Final regression**
- [ ] **19D — APK / gerçek cihaz testleri**
- [ ] **19E — Release dokümantasyonu**

> V1 release ≠ full professional curriculum completion.

---

# AŞAMA 20 — Uzun Vadeli Professional Curriculum ve Kariyer Katmanı
- [ ] **20A — Modern C++ + Advanced Python + Professional Tooling paketi**
- [ ] **20B — Systems + Architecture + Performance paketi**
- [ ] **20C — Networking + Distributed Systems + Storage paketi**
- [ ] **20D — GPU Architecture + CUDA paketi**
- [ ] **20E — Triton + ML/Transformer + LLM Inference paketi**
- [ ] **20F — Serving Engines + KV Cache / Batching / Scheduling / Quantization paketi**
- [ ] **20G — Multi-GPU / NCCL / RDMA / AI Infrastructure paketi**
- [ ] **20H — Open Source + Engineering Practice + Career Readiness**
- [ ] **20I — Sürekli Curriculum QA + Büyük Entegre Projeler + Professional Capstones**

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`, `8A–8E`  
**Son tamamlanan:** **`8E — SPWX-v0 / D-072`**  
**Aktif:** **`8F — Tasarım sistemi`** — active-not-executed

8E `SPWX-v0` ile Progress domain kilitlendi: TEPM-v0'dan genelleştirilen tek 8-state Skill vokabüleri, qualifier olarak `at_risk`, sıralanan fakat çökertilmeyen multi-axis truth, kilitlenmiş Topic etiketleri, inventory-only counting, hypothesis != deficiency, `remediation_task_completed != remediation_closed`, streak olmayan learning history ve gradebook olmayan longitudinal assessment report. Mastery yüzdesi, competence ratio, career bar, level/rank ve streak calendar yasaktır.

8F başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.
