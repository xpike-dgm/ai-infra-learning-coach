# AI Infra Learning Coach — Numaralı Yürütme İndeksi

Bu belge projenin konuşmada ve görev devrinde kullanılan **sabit adım kodlarını** tanımlar. Ayrıntılı checklist ve teknik açıklamalar `docs/MASTER_PLAN.md` içinde tutulur.

## Kullanım kuralı

- Ana aşamalar **1–19**.
- Alt adımlar `1A`, `1B`, `2A`, `3C` biçiminde sabit kimliğe sahiptir.
- Bir kodun anlamı sonradan mümkün olduğunca değiştirilmez.
- Tamamlanan adım `[x]`, bekleyen `[ ]` olarak işaretlenir.
- Aktif adım ayrıca `docs/STEP_STATUS.md` ve `docs/HANDOFF_STATE.md` içinde gösterilir.
- Her tamamlanma için ilgili spec + tarihli completion note + `PROGRESS_LOG.md` kaydı tutulur.

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle

- [x] **1A — Ana ürün amacı:** Tek cümlelik amaç, günlük değer, klasik kurs/todo farkı, kanıtlanmış öğrenme ilkesi ve kariyer rotası ilişkisi.
  - Çıktı: `docs/PRODUCT_REQUIREMENTS.md`
- [x] **1B — V1 kapsamı:** V1’de olacaklar/olmayacaklar, kişisel kullanım sınırı ve ilk gerçek release kapsamı.
  - Çıktı: `docs/V1_SCOPE.md`
- [x] **1C — Başarı kriterleri:** Daily planner, mastery, prerequisite, assessment, replan, retention, AI Tutor, English, persistence ve release için ölçülebilir acceptance testleri.
  - Çıktı: `docs/V1_SUCCESS_CRITERIA.md`
- [ ] **1D — Non-goals:** Scope creep’i önlemek için ürünün özellikle ne olmayacağını tek listede kilitle.

**Aşama 1 çıkışı:** Product requirements + V1 scope + success criteria + non-goals.

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla

- [ ] **2A — Bilgi birimleri:** Domain → Module → Topic → Skill → Learning Objective.
- [ ] **2B — Topic durumları:** locked, available, learning, mastered, weakening, remediation_required.
- [ ] **2C — Mastery sinyalleri:** teori, coding, debugging, açıklama, transfer, retention, proje, süre.
- [ ] **2D — AI/ipucu etkisi:** Hint seviyeleri, AI-assisted task ve comprehension check.
- [ ] **2E — Mastery formülü v0:** Ağırlıklar, threshold, minimum evidence, yanlış pozitif önleme, confidence.
- [ ] **2F — Unutma modeli:** Spaced repetition, interval değişimi ve mastery decay.

**Çıktı:** `LEARNING_ENGINE_SPEC.md`, mastery formula v0, topic state machine.

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla

- [ ] **3A — Günlük kapasite:** Kısa/normal/yoğun gün, bloklar, mola, gün içi süre değişikliği.
- [ ] **3B — Görev kategorileri:** Yeni konu, remediation, retention, coding, debugging, English, proje, mikro değerlendirme.
- [ ] **3C — Öncelik puanı:** Zayıf prerequisite, due review, yeni konu, ihmal edilen alan, English payı, çeşitlilik.
- [ ] **3D — Prerequisite davranışı:** Hard/soft prerequisite, kilitler, bağımsız dallar.
- [ ] **3E — Hızlı öğrenme:** Diagnostic, skip doğrulaması, retention.
- [ ] **3F — Kaçırılan günler:** 1 gün / birkaç gün / 1 hafta+ replan.
- [ ] **3G — Açıklanabilir planner:** Reason code ve kullanıcı açıklaması.
- [ ] **3H — Planner simülasyonu:** En az 20 sanal öğrenci akışı.

**Çıktı:** `ADAPTIVE_PLANNER_SPEC.md`, decision table, pseudocode, simulation suite.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla

- [ ] **4A — Günlük mikro değerlendirme**
- [ ] **4B — Haftalık sınav**
- [ ] **4C — Aylık yeterlilik sınavı**
- [ ] **4D — Soru bankası**
- [ ] **4E — AI-generated soru doğrulaması**

**Çıktı:** `ASSESSMENT_SYSTEM_SPEC.md`, exam templates, scoring rubrics.

---

# AŞAMA 5 — Curriculum ve Knowledge Graph Mimarisini Tasarla

- [ ] **5A — Ana domain haritası**
- [ ] **5B — Topic metadata**
- [ ] **5C — İlk 8–12 haftalık curriculum graph**
- [ ] **5D — Curriculum QA**

**Çıktı:** `CURRICULUM_GRAPH_SPEC.md`, initial curriculum dataset, graph QA report.

---

# AŞAMA 6 — İngilizce Paralel Hattını Tasarla

- [ ] **6A — Başlangıç ölçümü**
- [ ] **6B — A1/A2/B1/B2 teknik hedefleri**
- [ ] **6C — Günlük English bileşeni**
- [ ] **6D — Teknik entegrasyon**
- [ ] **6E — English mastery**

**Çıktı:** `ENGLISH_TRACK_SPEC.md`, first 12-week English curriculum, level criteria.

---

# AŞAMA 7 — Ürün Gereksinimleri, Ekranlar ve UX’i Kilitle

- [ ] **7A — Bilgi mimarisi**
- [ ] **7B — Ana ekran**
- [ ] **7C — Günlük çalışma akışı**
- [ ] **7D — Sınav UX**
- [ ] **7E — Skill/progress UX**
- [ ] **7F — Tasarım sistemi**
- [ ] **7G — Wireframe/prototip**

**Çıktı:** `UX_SPEC.md`, screen inventory, navigation map, wireframes, design system.

---

# AŞAMA 8 — Teknik Mimari ve Veri Modelini Kesinleştir

- [ ] **8A — Mobil teknoloji seçimi**
- [ ] **8B — Veri saklama / local-first**
- [ ] **8C — Domain veri modeli**
- [ ] **8D — Servis sınırları**
- [ ] **8E — AI entegrasyon mimarisi**
- [ ] **8F — Test stratejisi**

**Çıktı:** `TECH_ARCHITECTURE.md`, `DATA_MODEL.md`, architecture diagram, ADR.

---

# AŞAMA 9 — Mobil Proje İskeleti ve Tasarım Sistemini Kur

- [ ] **9A — Proje kurulumu**
- [ ] **9B — Navigation**
- [ ] **9C — Design system implementation**
- [ ] **9D — Local database**
- [ ] **9E — Temel uygulama sağlığı**

**Çıktı:** İlk çalışan mobil iskelet / debug build.

---

# AŞAMA 10 — Çekirdek Günlük Öğrenme Akışı MVP’sini Geliştir

- [ ] **10A — Today ekranı**
- [ ] **10B — Task runner**
- [ ] **10C — Session state**
- [ ] **10D — Günlük mikro quiz**
- [ ] **10E — Gün sonu**

**Çıktı:** Daily learning MVP, Task Engine v1, Quiz v1.

---

# AŞAMA 11 — Mastery ve Adaptif Planner’ı Koda Dök

- [ ] **11A — Mastery Engine v1**
- [ ] **11B — Prerequisite Engine**
- [ ] **11C — Planner Engine v1**
- [ ] **11D — Replan**
- [ ] **11E — Explanation / reason codes**
- [ ] **11F — Sanal kullanıcı testleri**

**Çıktı:** Mastery + Prerequisite + Adaptive Planner v1.

---

# AŞAMA 12 — Haftalık/Aylık Sınav, Retention ve Remediation’ı Geliştir

- [ ] **12A — Haftalık sınav**
- [ ] **12B — Aylık sınav**
- [ ] **12C — Spaced repetition**
- [ ] **12D — Remediation Engine**
- [ ] **12E — Program değişiklik raporu**

**Çıktı:** Weekly/Monthly Exam v1, Retention Engine, Remediation Engine.

---

# AŞAMA 13 — AI Tutor ve Akıllı Değerlendirme Katmanını Geliştir

- [ ] **13A — Tutor davranış sözleşmesi**
- [ ] **13B — Yanlış analizi**
- [ ] **13C — Alternatif anlatım**
- [ ] **13D — Kod değerlendirme**
- [ ] **13E — AI-generated code comprehension check**
- [ ] **13F — Açık uçlu cevap değerlendirme**
- [ ] **13G — Provider abstraction / fallback**

**Çıktı:** AI Tutor v1 + evaluators.

---

# AŞAMA 14 — İlk 8–12 Haftalık Gerçek Eğitim İçeriğini Üret ve QA Et

- [ ] **14A — Computer Fundamentals**
- [ ] **14B — C Foundations**
- [ ] **14C — Memory Foundations**
- [ ] **14D — Linux Foundations**
- [ ] **14E — English A0→A1/A2**
- [ ] **14F — Assessment content**
- [ ] **14G — Content QA**

**Çıktı:** Production curriculum v1 + Assessment Bank v1.

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

**Çıktı:** Release APK — temel ürün kullanıma hazır.

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

**Aktif aşama:** AŞAMA 1 — Ürün Çerçevesini Kilitle  
**Tamamlanan:** `1A`, `1B`, `1C`  
**Aktif:** **`1D — Non-goals`**

Aşama 1 tamamlandıktan sonra `2A — Bilgi birimleri` ile öğrenme motoru tasarımına geçilecektir.
