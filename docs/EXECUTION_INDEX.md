# AI Infra Learning Coach — Numaralı Yürütme İndeksi

Bu belge projenin sabit adım kodlarının canonical indeksidir. Ayrıntılı checklist `docs/MASTER_PLAN.md`, anlık durum `docs/STEP_STATUS.md` içindedir.

## Kullanım kuralı
- Ana aşamalar **1–20**.
- Alt adımlar `1A`, `2E`, `12F` biçiminde kullanılır.
- Tamamlanan `[x]`, bekleyen `[ ]`.
- Her adım öncesi/sonrası `docs/PROJECT_MEMORY_PROTOCOL.md` uygulanır.
- `MASTER_PLAN`, `STEP_STATUS`, `HANDOFF_STATE` ve `PROGRESS_LOG` canonical durumla senkron tutulur.
- D-041: full curriculum 4+ yıllık professional-readiness horizon'ına genişletildi.
- D-042: Python ana technical foundation rotasına resmi olarak eklendi.
- D-043: yanlış yorum nedeniyle geri çekildi; standalone specialization stage canonical değildir.
- D-044: **AŞAMA 6 — Granular Capability Map** planlama aşamalarının arasına eklendi; henüz başlanmamış future stages yeniden indekslendi.
- D-045: 4B final weekly model `WBA-v0 — Weekly Blueprint Assessment`.
- D-046: 4C final monthly model `MCA-v0 — Monthly Capability Assessment`.

> Renumber kuralı: tamamlanmış `1–4` kodları değişmez. D-044 yalnız henüz başlanmamış future stages'i yeniden indekslemiştir.

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

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla
- [x] **4A — Günlük mikro değerlendirme** — `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` — DMA-v0 / D-040
- [x] **4B — Haftalık sınav** — `docs/WEEKLY_ASSESSMENT_SPEC.md` — WBA-v0 / D-045
  - blueprint-before-items,
  - multi-Skill/Objectives with granular attribution,
  - no fixed question/time/category quota,
  - capacity-aware split/pause/resume,
  - incomplete/missed exam != failure/debt,
  - GRE/RVR/PRG/PBR integration.
- [x] **4C — Aylık yeterlilik sınavı** — `docs/MONTHLY_ASSESSMENT_SPEC.md` — MCA-v0 / D-046
  - longitudinal capability blueprint,
  - broader transfer/integration + critical revalidation,
  - recent/older state-based sampling; not cumulative-everything,
  - no fixed score/time/quota,
  - professional checkpoint != professional-readiness gate,
  - granular evidence → planner/curriculum priority,
  - Question Bank metadata handoff.
- [ ] **4D — Soru bankası** **AKTİF**
- [ ] **4E — AI-generated soru doğrulaması**

---

# AŞAMA 5 — Curriculum ve Knowledge Graph İskeleti
- [ ] **5A — Ana domain haritası** — 4+ year professional domain envelope; Python dahil common foundation
- [ ] **5B — Graph / Topic metadata sözleşmesi** — prerequisite, evidence, retention, criticality, versioning
- [ ] **5C — İlk 8–12 haftalık curriculum backbone** — V1 başlangıç alt grafiğinin iskeleti
- [ ] **5D — Graph architecture QA** — cycle/dead-end/hidden prerequisite ve genişleme kontrolü

> AŞAMA 5 bütün ayrıntılı konu listesini yazmaz; graph'ın iskeletini kurar. Ayrıntılı decomposition AŞAMA 6'dadır.

---

# AŞAMA 6 — Granular Capability Map / Öğrenme Rotasını Alt Becerilere Böl
Ana charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`

- [ ] **6A — Granularity + naming standardı** — Domain/Module/Topic/Skill/Objective sınırları, canonical ID, over-fragmentation guard
- [ ] **6B — Full-route decomposition blueprint** — bütün ana teknik/English rotası için ortak decomposition şablonu
- [ ] **6C — Foundations detailed map** — Technical English, Python, C, Linux/Git/Shell, DS&A
- [ ] **6D — Systems detailed map** — Modern C++, Architecture, OS/Memory, Concurrency, Networking, Distributed, Storage, Cloud/Observability, Performance
- [ ] **6E — GPU / ML / Inference detailed map** — GPU, CUDA, Triton, Transformer, inference internals, serving engines, KV/batching/scheduling/quantization, Multi-GPU/NCCL/RDMA, AI Infra
- [ ] **6F — Professional engineering / project map** — testing/build/debug/profiling, OSS workflow, large projects, capstone capability decomposition
- [ ] **6G — Weakness localization + remediation mapping** — zayıflığın Skill/Objective düzeyinde ayrı tutulması
- [ ] **6H — Coverage / prerequisite / Research QA** — eksik/duplicate/hidden prerequisite audit + bağımsız Research AI doğrulaması

> Örnek hedef: `Python zayıf` yerine `Python → Control Flow → Loops → while termination` gibi hedefli tanı ve remediation.

---

# AŞAMA 7 — İngilizce Paralel Hattı
- [ ] **7A — Başlangıç ölçümü**
- [ ] **7B — A1/A2/B1/B2+ teknik hedefleri**
- [ ] **7C — Günlük English bileşeni**
- [ ] **7D — Teknik entegrasyon**
- [ ] **7E — English mastery**

---

# AŞAMA 8 — UX ve Ekranlar
- [ ] **8A — Bilgi mimarisi**
- [ ] **8B — Ana ekran**
- [ ] **8C — Günlük çalışma akışı**
- [ ] **8D — Sınav UX**
- [ ] **8E — Skill/progress/weakness UX**
- [ ] **8F — Tasarım sistemi**
- [ ] **8G — Wireframe/prototip**

---

# AŞAMA 9 — Teknik Mimari ve Veri Modeli
- [ ] **9A — Mobil teknoloji seçimi**
- [ ] **9B — Veri saklama / local-first**
- [ ] **9C — Domain veri modeli** — granular Skill/Objective state, years-long history, curriculum versioning
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

> AŞAMA 15, AŞAMA 6 capability map'ini kullanarak yalnız ilk production-quality 8–12 haftalık paketi üretir; full 4+ year curriculum değildir.

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
- [ ] **18D — Assessment kalibrasyonu**
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

> AŞAMA 20 yeni konu haritasını sıfırdan icat etmez; AŞAMA 6'da üretilen granular capability map'i yıllar boyunca gerçek içerik/evidence/project paketlerine dönüştürür.

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4C`  
**Aktif:** **`4D — Soru bankası`**

**Long-term target:** D-041 / `docs/PROFESSIONAL_READINESS_TARGET.md`.  
**Route update:** D-042 Python foundation.  
**Plan correction:** D-043 withdrawn; D-044 granular capability map / AŞAMA 6.  
**Assessment:** D-045 WBA-v0; D-046 MCA-v0.

4D başlamadan yeni PRE-STEP GitHub refresh zorunludur.