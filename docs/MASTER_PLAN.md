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
- D-049: curriculum domain backbone = PDM-v0.
- D-050: living-memory sync + repo-wide stale-reference audit her numaralı step kapanışında zorunludur.

## Zorunlu yürütme — D-024 / D-027 / D-050
`PRE-STEP GitHub refresh → gerekiyorsa Research/Coding/QA → spec/çıktı → değerlendirme → ALWAYS-CHECK living-memory sync → repo-wide stale-reference scan → sonraki adım`

ALWAYS-CHECK seti ve dosya rolleri: `docs/PROJECT_MEMORY_PROTOCOL.md`.

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
### [x] 4B — Haftalık sınav — WBA-v0 / D-045
### [x] 4C — Aylık yeterlilik sınavı — MCA-v0 / D-046
### [x] 4D — Soru / assessment resource bank — QAB-v0 / D-047
### [x] 4E — AI-generated soru doğrulaması — AIV-v0 / D-048

> **AŞAMA 4 tamamlandı: DMA-v0 + WBA-v0 + MCA-v0 + QAB-v0 + AIV-v0.**

---

# AŞAMA 5 — Curriculum ve Knowledge Graph İskeleti

### [x] 5A — Ana domain haritası — PDM-v0 / D-049
**Final:** `docs/CURRICULUM_DOMAIN_MAP.md`

Final davranış:
- 23 ana route family high-level professional envelope olarak korunur.
- Technical English parallel track.
- Python + C + Linux/Git/Shell complementary early foundations; DS&A supporting common foundation.
- Systems core: Modern C++, Architecture, OS/Memory, Concurrency, Networking.
- Distributed/platform core: Distributed Systems + Storage/DB + Containers/Cloud/Observability.
- Performance route boyunca cross-cutting core'dur; yalnız final optimization bölümü değildir.
- Accelerator core: GPU Architecture → CUDA/Triton.
- ML/Transformer inference için supporting domain; generic ML-research specialization değildir.
- LLM Inference → Serving Systems → KV/Batching/Scheduling/Quantization ayrı bağlı family'lerdir.
- Multi-GPU/NCCL/RDMA networking + distributed + GPU convergence katmanıdır.
- AI/GPU Infrastructure target integration domainidir.
- Open Source/engineering practice/projects/capstones route boyunca büyüyen professional evidence layer'dır.
- Security/reliability/math/numerical ihtiyaçları hidden prerequisite olarak bırakılmaz.
- Tool/vendor isimleri stable systems concept yerine geçmez.
- Domain-level authoring relations runtime hard-lock değildir; Skill→Skill PRG-v0 korunur.

**PRE/POST notu:** 5A fresh GitHub PRE-STEP refresh ile yürütüldü. Ayrı Research AI kullanılmadı; full external coverage/current-industry Research QA AŞAMA 6H'de zorunlu planlandı.

### [ ] 5B — Graph / Topic metadata sözleşmesi — **AKTİF**
Kesinleştirilecek:
- Domain/Module/Topic/Skill/Learning Objective entity contract,
- placement vs canonical Skill identity,
- Topic↔Skill many-to-many,
- Skill→Skill hard/soft prerequisite,
- 5A domain authoring relations,
- required/critical/optional semantics,
- evidence/assessment refs,
- retention/remediation/diagnostic metadata,
- Technical English prerequisite metadata,
- cross-domain Skill reuse,
- professional/project/capstone attribution,
- source/version/freshness,
- graph version/migration,
- indexing/bounded traversal/performance contract.

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
- curriculum graph identities/versions,
- per-user exposure,
- years-long curriculum/user history,
- migrations.
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

# Repository Hygiene Maintenance — D-050

Bu bakım numaralı bir stage değildir ve 5B'yi ilerletmez.

- Repo dosya envanteri audit edildi.
- `PROJECT_CONTEXT.md` living snapshot olarak mandatory sync kapsamına alındı ve 5B current state'e getirildi.
- `PROJECT_MASTER_CONTEXT` / README volatile active-step duplication'dan arındırıldı.
- Obsolete `docs/TODO.md` silindi.
- Legacy `docs/LEARNING_ENGINE.md` explicit historical/superseded pointer'a dönüştürüldü.
- `docs/ENGLISH_TRACK.md` non-canonical seed olarak etiketlendi.
- `PROJECT_MEMORY_PROTOCOL` + `AI_AGENT_WORKFLOW` repo-wide stale-reference scan ve ALWAYS-CHECK setiyle güçlendirildi.

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A`  
**Aktif:** **`5B — Graph / Topic metadata sözleşmesi`**

Bir sonraki yürütme: **5B başlamadan yeni PRE-STEP GitHub refresh → graph/metadata contract → POST-STEP D-050 sync + stale-reference audit.**