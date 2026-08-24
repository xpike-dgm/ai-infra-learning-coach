# AI Infra Learning Coach — Numaralı Yürütme İndeksi

Bu belge projenin sabit adım kodlarının canonical indeksidir. Ayrıntılı checklist `docs/MASTER_PLAN.md`, anlık durum `docs/STEP_STATUS.md` içindedir.

## Kullanım kuralı
- Ana aşamalar **1–19**.
- Alt adımlar `1A`, `2E`, `11F` biçiminde sabittir.
- Tamamlanan `[x]`, bekleyen `[ ]`.
- Her adım öncesi/sonrası `docs/PROJECT_MEMORY_PROTOCOL.md` uygulanır.
- `MASTER_PLAN`, `STEP_STATUS`, `HANDOFF_STATE` ve `PROGRESS_LOG` canonical durumla senkron tutulur.

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
  - **Tamamlanma notu — 2026-08-24:** 16/16 policy scenarios PASS, 20/20 PDT-v0 invariant PASS, 0 critical cross-spec contradiction. Bu sonuç production runtime testi değildir; 11F/17E doğrulaması ayrıca zorunlu.

**AŞAMA 3 sonucu:** ✅ PASS / TAMAMLANDI.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla
- [ ] **4A — Günlük mikro değerlendirme** **AKTİF**
- [ ] **4B — Haftalık sınav**
- [ ] **4C — Aylık yeterlilik sınavı**
- [ ] **4D — Soru bankası**
- [ ] **4E — AI-generated soru doğrulaması**

---

# AŞAMA 5 — Curriculum ve Knowledge Graph
- [ ] **5A — Ana domain haritası**
- [ ] **5B — Topic metadata**
- [ ] **5C — İlk 8–12 haftalık curriculum graph**
- [ ] **5D — Curriculum QA**

---

# AŞAMA 6 — İngilizce Paralel Hattı
- [ ] **6A — Başlangıç ölçümü**
- [ ] **6B — A1/A2/B1/B2 teknik hedefleri**
- [ ] **6C — Günlük English bileşeni**
- [ ] **6D — Teknik entegrasyon**
- [ ] **6E — English mastery**

---

# AŞAMA 7 — UX ve Ekranlar
- [ ] **7A — Bilgi mimarisi**
- [ ] **7B — Ana ekran**
- [ ] **7C — Günlük çalışma akışı**
- [ ] **7D — Sınav UX**
- [ ] **7E — Skill/progress UX**
- [ ] **7F — Tasarım sistemi**
- [ ] **7G — Wireframe/prototip**

---

# AŞAMA 8 — Teknik Mimari ve Veri Modeli
- [ ] **8A — Mobil teknoloji seçimi**
- [ ] **8B — Veri saklama / local-first**
- [ ] **8C — Domain veri modeli**
- [ ] **8D — Servis sınırları**
- [ ] **8E — AI entegrasyon mimarisi**
- [ ] **8F — Test stratejisi**

---

# AŞAMA 9 — Mobil Proje İskeleti ve Tasarım Sistemini Kur
- [ ] **9A — Proje kurulumu**
- [ ] **9B — Navigation**
- [ ] **9C — Design system implementation**
- [ ] **9D — Local database**
- [ ] **9E — Temel uygulama sağlığı**

---

# AŞAMA 10 — Çekirdek Günlük Öğrenme Akışı MVP’sini Geliştir
- [ ] **10A — Today ekranı**
- [ ] **10B — Task runner**
- [ ] **10C — Session state**
- [ ] **10D — Günlük mikro quiz**
- [ ] **10E — Gün sonu**

---

# AŞAMA 11 — Mastery ve Adaptif Planner’ı Koda Dök
- [ ] **11A — Mastery Engine v1**
- [ ] **11B — Prerequisite Engine**
- [ ] **11C — Planner Engine v1**
- [ ] **11D — Replan**
- [ ] **11E — Explanation / reason codes**
- [ ] **11F — Sanal kullanıcı testleri**

---

# AŞAMA 12 — Haftalık/Aylık Sınav, Retention ve Remediation’ı Geliştir
- [ ] **12A — Haftalık sınav**
- [ ] **12B — Aylık sınav**
- [ ] **12C — Spaced repetition**
- [ ] **12D — Remediation Engine**
- [ ] **12E — Program değişiklik raporu**

---

# AŞAMA 13 — AI Tutor ve Akıllı Değerlendirme Katmanını Geliştir
- [ ] **13A — Tutor davranış sözleşmesi**
- [ ] **13B — Yanlış analizi**
- [ ] **13C — Alternatif anlatım**
- [ ] **13D — Kod değerlendirme**
- [ ] **13E — AI-generated code comprehension check**
- [ ] **13F — Açık uçlu cevap değerlendirme**
- [ ] **13G — Provider abstraction / fallback**

---

# AŞAMA 14 — İlk 8–12 Haftalık Gerçek Eğitim İçeriğini Üret ve QA Et
- [ ] **14A — Computer Fundamentals**
- [ ] **14B — C Foundations**
- [ ] **14C — Memory Foundations**
- [ ] **14D — Linux Foundations**
- [ ] **14E — English A0→A1/A2**
- [ ] **14F — Assessment content**
- [ ] **14G — Content QA**

---

# AŞAMA 15 — İlerleme, Analitik, Ayarlar ve Günlük Kullanım Araçları
- [ ] **15A — Skill analytics**
- [ ] **15B — Öğrenme geçmişi**
- [ ] **15C — Progress gösterim kuralları**
- [ ] **15D — Ayarlar**
- [ ] **15E — Bildirimler**

---

# AŞAMA 16 — UI/UX Polish ve Erişilebilirlik
- [ ] **16A — Görsel polish**
- [ ] **16B — Motion**
- [ ] **16C — Kullanılabilirlik**
- [ ] **16D — Accessibility**

---

# AŞAMA 17 — Gerçek Kullanım Pilotu, Kalibrasyon ve QA
- [ ] **17A — Pilot başlangıcı**
- [ ] **17B — Planner gözlemi**
- [ ] **17C — Mastery kalibrasyonu**
- [ ] **17D — Assessment kalibrasyonu**
- [ ] **17E — Teknik QA**
- [ ] **17F — Düzeltme döngüsü**

---

# AŞAMA 18 — Release APK ve Kullanıma Hazır Sürüm
- [ ] **18A — Release hazırlığı**
- [ ] **18B — Veri güvenilirliği**
- [ ] **18C — Final regression**
- [ ] **18D — APK / gerçek cihaz testleri**
- [ ] **18E — Release dokümantasyonu**

---

# AŞAMA 19 — Uzun Vadeli Curriculum ve Kariyer Katmanı
- [ ] **19A — Modern C++ paketi**
- [ ] **19B — Systems paketi**
- [ ] **19C — Distributed Systems paketi**
- [ ] **19D — GPU/CUDA paketi**
- [ ] **19E — Triton/Inference paketi**
- [ ] **19F — Multi-GPU / AI Infrastructure**
- [ ] **19G — Open source**
- [ ] **19H — Career readiness**
- [ ] **19I — Sürekli curriculum QA**

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`  
**Aktif:** **`4A — Günlük mikro değerlendirme`**

4A başlamadan yeni PRE-STEP GitHub refresh zorunludur.