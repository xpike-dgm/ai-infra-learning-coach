# AI Infra Learning Coach — Numaralı Yürütme İndeksi

Bu belge, `docs/MASTER_PLAN.md` içindeki ayrıntılı geliştirme planına **sabit ve konuşmada kolay kullanılabilir adım kodları** verir.

## Kullanım kuralı

- Ana aşamalar **1–19** olarak adlandırılır.
- Her aşamanın ana alt adımları `1A`, `1B`, `1C` ... biçimindedir.
- `MASTER_PLAN.md` içindeki daha küçük checkbox maddeleri, ilgili kodun alt görevleri / kabul kriterleridir.
- Bundan sonra proje konuşmalarında yalnızca “Aşama 3” demek yerine mümkün olduğunca **`3C`** gibi tam adım kodu kullanılır.
- Bir adım tamamlandığında bu dosyada `[x]` yapılır; `MASTER_PLAN.md` içindeki ilgili checklist de tamamlanır ve tarihli tamamlanma notu eklenir.
- Bir adımın kodu sonradan değiştirilmez. Yeni adım gerekiyorsa mevcut kodların anlamı korunarak yeni harf eklenir.

> Örnek: `1B` denildiğinde herkes bunun **V1 kapsamını kesinleştirme** adımı olduğunu bilmelidir.

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle

**Eski MASTER_PLAN karşılığı:** Aşama 0

- [ ] **1A — Ana ürün amacı:** Uygulamanın tek cümlelik amacı, günlük temel değeri, klasik kurs/todo uygulamasından farkı, “kanıtlanmış öğrenme” ilkesi ve kariyer rotasıyla ilişkisi kesinleştirilecek.
- [ ] **1B — V1 kapsamı:** V1’de kesin olacaklar, olmayacaklar, kişisel kullanım sınırı ve 3 yıllık hedefin yalnız planlama ufku olması kesinleştirilecek.
- [ ] **1C — Başarı kriterleri:** Günlük plan, mastery, haftalık/aylık sınav etkisi ve kaçırılan gün sonrası replan için ölçülebilir acceptance kriterleri yazılacak.
- [ ] **1D — Non-goals:** Gamification, streak/gün sayısı, tüm 3 yıllık içeriği V1’e doldurma ve benzeri kapsam dışı hedefler kilitlenecek.

**Aşama 1 çıkışı:** `PRODUCT_REQUIREMENTS.md`, V1 scope, non-goals ve success criteria.

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla

**Eski MASTER_PLAN karşılığı:** Aşama 1

- [ ] **2A — Bilgi birimleri:** Domain → Module → Topic → Skill → Learning Objective hiyerarşisi ve ölçülebilir öğrenme hedefleri.
- [ ] **2B — Topic durumları:** locked, available, learning, mastered, weakening ve remediation_required durumları.
- [ ] **2C — Mastery sinyalleri:** teori, coding, debugging, Feynman açıklaması, transfer, retention, proje ve süre sinyalleri.
- [ ] **2D — AI/ipucu etkisi:** İpucu seviyeleri, AI yardımı sonrası comprehension check ve mastery etkisi.
- [ ] **2E — Mastery formülü v0:** Ağırlıklar, threshold, zorunlu alt koşullar, yanlış pozitif önleme ve confidence kararı.
- [ ] **2F — Unutma modeli:** Spaced repetition aralıkları, başarı/başarısızlık sonrası interval ve mastery decay.

**Aşama 2 çıkışı:** `LEARNING_ENGINE_SPEC.md`, mastery formula v0 ve topic state machine.

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla

**Eski MASTER_PLAN karşılığı:** Aşama 2

- [ ] **3A — Günlük kapasite:** Kısa/normal/yoğun gün, görev blokları, mola ve gün içi süre değişiklikleri.
- [ ] **3B — Görev kategorileri:** Yeni konu, remediation, retention, coding, debugging, English, proje ve mikro değerlendirme.
- [ ] **3C — Öncelik puanı:** Zayıf prerequisite, due review, yeni konu, ihmal edilen alan, English payı ve görev çeşitliliği.
- [ ] **3D — Prerequisite davranışı:** Hard/soft prerequisite, kilitler ve bağımsız dalların devamı.
- [ ] **3E — Hızlı öğrenme:** Diagnostic, içerik atlama doğrulaması ve retention kontrolü.
- [ ] **3F — Kaçırılan günler:** 1 gün, birkaç gün ve 1 hafta+ ara sonrası sağlıklı yeniden planlama.
- [ ] **3G — Açıklanabilir planner:** Reason code ve “neden bugün bunu çalışıyorum?” açıklaması.
- [ ] **3H — Planner simülasyonu:** Farklı öğrenci profilleriyle en az 20 sanal akış testi.

**Aşama 3 çıkışı:** `ADAPTIVE_PLANNER_SPEC.md`, decision table, pseudocode ve simülasyon senaryoları.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla

**Eski MASTER_PLAN karşılığı:** Aşama 3

- [ ] **4A — Günlük mikro değerlendirme:** Çoktan seçmeli, kısa cevap, kod çıktısı, kod tamamlama, coding, debugging, açıklama ve retrieval.
- [ ] **4B — Haftalık sınav:** Yeni/eski konu dengesi, coding/debugging/English ağırlıkları ve remediation etkisi.
- [ ] **4C — Aylık yeterlilik sınavı:** Teori, coding, debugging, Feynman, English, retention ve mini proje kararı.
- [ ] **4D — Soru bankası:** Metadata, skill/topic etiketi, zorluk, rubric ve varyasyon sistemi.
- [ ] **4E — AI-generated soru doğrulaması:** Validator, rubric ve şüpheli soruları mastery’den çıkarma.

**Aşama 4 çıkışı:** `ASSESSMENT_SYSTEM_SPEC.md`, sınav şablonları ve scoring rubric’leri.

---

# AŞAMA 5 — Curriculum ve Knowledge Graph Mimarisini Tasarla

**Eski MASTER_PLAN karşılığı:** Aşama 4

- [ ] **5A — Ana domain haritası:** English’den AI Infrastructure’a kadar tüm ana öğrenme domainlerini kilitle.
- [ ] **5B — Topic metadata:** Açıklama, objective, prerequisite, effort, criticality, assessment, remediation, kaynak ve career relevance.
- [ ] **5C — İlk 8–12 haftalık curriculum graph:** Computer Fundamentals, C, Memory, Linux, ilk data structures ve paralel English.
- [ ] **5D — Curriculum QA:** Eksik/circular prerequisite, gereksiz dolgu, kariyer ilişkisi, düşük ROI ve seviye sırası kontrolü.

**Aşama 5 çıkışı:** `CURRICULUM_GRAPH_SPEC.md`, ilk curriculum dataset ve graph QA raporu.

---

# AŞAMA 6 — İngilizce Paralel Hattını Tasarla

**Eski MASTER_PLAN karşılığı:** Aşama 5

- [ ] **6A — Başlangıç ölçümü:** A0, reading, vocabulary, listening, speaking/writing diagnostic.
- [ ] **6B — Seviye hedefleri:** A1, A2, B1 ve B2 için teknik kullanım hedefleri.
- [ ] **6C — Günlük English bileşeni:** Grammar, genel/teknik vocabulary, error/docs reading, GitHub writing, listening ve speaking.
- [ ] **6D — Teknik entegrasyon:** O günkü teknik içerikle İngilizceyi bağla, Türkçe oranını zamanla azalt ve gereksiz prerequisite kilitlerini önle.
- [ ] **6E — English mastery:** Reading, writing, listening, speaking, technical vocabulary ve technical explanation mastery.

**Aşama 6 çıkışı:** `ENGLISH_TRACK_SPEC.md`, ilk 12 haftalık English curriculum ve geçiş kriterleri.

---

# AŞAMA 7 — Ürün Gereksinimleri, Ekranlar ve UX’i Kilitle

**Eski MASTER_PLAN karşılığı:** Aşama 6

- [ ] **7A — Bilgi mimarisi:** Navigation ve ana uygulama bölümleri.
- [ ] **7B — Ana ekran:** Current topic/mastery, günlük süre/görevler, yaklaşan değerlendirme ve ana CTA.
- [ ] **7C — Günlük çalışma akışı:** Görev açılışı → anlatım → örnek → uygulama → ölçüm → sonraki görev → gün özeti.
- [ ] **7D — Sınav UX:** Günlük/haftalık/aylık assessment ve skill breakdown + program değişikliği görünümü.
- [ ] **7E — Skill/progress UX:** Domain/topic mastery, zayıf alan, retention riski ve başlamadı/başarısız ayrımı.
- [ ] **7F — Tasarım sistemi:** Moodboard, renk, tema, tipografi, spacing, component ve motion ilkeleri.
- [ ] **7G — Wireframe/prototip:** Kritik ekranların wireframe’i ve kullanıcı akışı doğrulaması.

**Aşama 7 çıkışı:** `UX_SPEC.md`, screen inventory, navigation map, wireframes ve design system spec.

---

# AŞAMA 8 — Teknik Mimari ve Veri Modelini Kesinleştir

**Eski MASTER_PLAN karşılığı:** Aşama 7

- [ ] **8A — Mobil teknoloji seçimi:** Flutter vs Kotlin/Compose vs React Native karşılaştırması ve tek seçim.
- [ ] **8B — Veri saklama:** Local-first DB, içerik/ilerleme ayrımı, migration ve backup/export temeli.
- [ ] **8C — Domain veri modeli:** Domain’den AIInteraction’a kadar temel entity ve ilişkiler.
- [ ] **8D — Servis sınırları:** Curriculum, mastery, planner, assessment, retention, AI tutor ve analytics servisleri.
- [ ] **8E — AI entegrasyon mimarisi:** Provider-independent adapter, offline fallback, prompt versioning ve validation.
- [ ] **8F — Test stratejisi:** Unit, simulation, migration, UI smoke ve gerekiyorsa golden/snapshot test.

**Aşama 8 çıkışı:** `TECH_ARCHITECTURE.md`, `DATA_MODEL.md`, architecture diagram ve technology decision record.

---

# AŞAMA 9 — Mobil Proje İskeleti ve Tasarım Sistemini Kur

**Eski MASTER_PLAN karşılığı:** Aşama 8

- [ ] **9A — Proje kurulumu:** Mobil proje, klasör mimarisi, lint/format ve build config.
- [ ] **9B — Navigation:** Ana route’lar ve seçilen navigasyon modelinin uygulanması.
- [ ] **9C — Design system:** Renk, tipografi, spacing, temel componentler ve light/dark tema.
- [ ] **9D — Local database:** DB, migration, repository layer, seed data ve persistence testi.
- [ ] **9E — Temel uygulama sağlığı:** Açılış, navigation, tema, restart persistence ve smoke test.

**Aşama 9 çıkışı:** İlk çalışan mobil iskelet ve debug APK/emulator sürümü.

---

# AŞAMA 10 — Çekirdek Günlük Öğrenme Akışı MVP’sini Geliştir

**Eski MASTER_PLAN karşılığı:** Aşama 9

- [ ] **10A — Today ekranı:** Günlük görevler, süre, current topic/mastery ve Start/Continue CTA.
- [ ] **10B — Task runner:** Lesson, reading, practice, quiz, coding/debugging, English ve retention task türleri.
- [ ] **10C — Session state:** Start, pause, resume, skip request, complete ve app restart restore.
- [ ] **10D — Günlük mikro quiz:** Soru/cevap, feedback, attempt ve skill bağlantısı.
- [ ] **10E — Gün sonu:** Tamamlananlar, zorlanılanlar, mastery değişiklikleri ve yarın planner tetikleme.

**Aşama 10 çıkışı:** Günlük çalışma MVP’si, Task Engine v1 ve Quiz v1.

---

# AŞAMA 11 — Mastery ve Adaptif Planner’ı Koda Dök

**Eski MASTER_PLAN karşılığı:** Aşama 10

- [ ] **11A — Mastery Engine v1:** Attempt’lerden mastery, ağırlık, AI etkisi, threshold ve history.
- [ ] **11B — Prerequisite Engine:** Hard lock, soft warning, prerequisite mastery ve bağımsız dallar.
- [ ] **11C — Planner Engine v1:** Kapasite, candidate task, priority, süre, çeşitlilik ve English payı.
- [ ] **11D — Replan:** Başarısız görev, kaçırılan gün, kapasite değişimi ve yeni topic açılması.
- [ ] **11E — Explanation:** Planner reason code ve kullanıcıya sade açıklama.
- [ ] **11F — Test:** Sanal kullanıcılar, pointer’da zorlanma, hızlı öğrenme ve 1 hafta ara senaryoları.

**Aşama 11 çıkışı:** Mastery Engine v1, Adaptive Planner v1 ve Prerequisite Engine v1.

---

# AŞAMA 12 — Haftalık/Aylık Sınav, Retention ve Remediation’ı Geliştir

**Eski MASTER_PLAN karşılığı:** Aşama 11

- [ ] **12A — Haftalık sınav:** Otomatik composition, coverage, yeni/eski denge, scoring, skill breakdown ve sonraki hafta etkisi.
- [ ] **12B — Aylık sınav:** Comprehensive assessment, coding/debugging, retention, English ve curriculum priority update.
- [ ] **12C — Spaced repetition:** Review schedule, due review, interval büyütme/küçültme ve mastery etkisi.
- [ ] **12D — Remediation Engine:** Kök skill, alternatif anlatım, kolay örnek, debugging, micro drill ve prerequisite geri dönüşü.
- [ ] **12E — Program değişiklik raporu:** Neden değişti, ertelenen topic, remediation ve geri gelen eski topic.

**Aşama 12 çıkışı:** Weekly/Monthly Exam v1, Retention Engine v1 ve Remediation Engine v1.

---

# AŞAMA 13 — AI Tutor ve Akıllı Değerlendirme Katmanını Geliştir

**Eski MASTER_PLAN karşılığı:** Aşama 12

- [ ] **13A — Tutor davranış sözleşmesi:** Cevap verme sınırları, Socratic hints, seviye ve dil oranı.
- [ ] **13B — Yanlış analizi:** Kök neden, kavram/dikkatsizlik ayrımı, prerequisite açığı ve planner sinyali.
- [ ] **13C — Alternatif anlatım:** Basitleştirme, analogy, memory diagram, code walkthrough ve counterexample.
- [ ] **13D — Kod değerlendirme:** Çalışma, kalite, hata açıklaması ve comprehension doğrulaması.
- [ ] **13E — AI-generated code check:** Satır açıklama, değişiklik etkisi, farklı input ve transfer task.
- [ ] **13F — Açık uçlu cevaplar:** Rubric, confidence ve spesifik feedback.
- [ ] **13G — Provider abstraction:** Sağlayıcı değişimi ve non-AI fallback.

**Aşama 13 çıkışı:** AI Tutor v1, open-ended evaluator ve code comprehension checker.

---

# AŞAMA 14 — İlk 8–12 Haftalık Gerçek Eğitim İçeriğini Üret ve QA Et

**Eski MASTER_PLAN karşılığı:** Aşama 13

- [ ] **14A — Computer Fundamentals içeriği:** CPU/RAM/storage, binary, program/process, compile/run.
- [ ] **14B — C Foundations içeriği:** Variables’dan struct başlangıcına kadar temel C.
- [ ] **14C — Memory Foundations içeriği:** Address, pointers, stack/heap, lifetime, malloc/free.
- [ ] **14D — Linux Foundations içeriği:** Filesystem, terminal, permissions, process, compiler ve Git.
- [ ] **14E — English A0→A1/A2 içeriği:** Grammar, technical vocabulary, error reading ve basit README/commit.
- [ ] **14F — Assessment content:** Mikro sorular, coding/debugging/transfer, weekly pools ve ilk monthly exam.
- [ ] **14G — Content QA:** Teknik doğruluk, sıfır seviye anlaşılabilirlik, prerequisite, jargon, soru-cevap ve difficulty calibration.

**Aşama 14 çıkışı:** İlk production curriculum, Assessment Bank v1 ve Content QA report.

---

# AŞAMA 15 — İlerleme, Analitik, Ayarlar ve Günlük Kullanım Araçlarını Tamamla

**Eski MASTER_PLAN karşılığı:** Aşama 14

- [ ] **15A — Skill analytics:** Domain/topic mastery, confidence/retention, zayıf ve güçlenen alanlar.
- [ ] **15B — Öğrenme geçmişi:** Süre, assessment, mastery değişimi ve remediation geçmişi.
- [ ] **15C — Progress gösterim kuralları:** Mastery ana metrik; streak/gün sayısı ikincil; başlamadı ve başarısız ayrımı.
- [ ] **15D — Ayarlar:** Günlük süre, gün yoğunluğu, tema, bildirim ve gerekli tutor tercihleri.
- [ ] **15E — Bildirimler:** Günlük plan, retention, weekly/monthly exam ve suçlayıcı olmayan dönüş bildirimi.

**Aşama 15 çıkışı:** Progress dashboard, weakness view, settings ve notifications.

---

# AŞAMA 16 — UI/UX Polish ve Erişilebilirlik

**Eski MASTER_PLAN karşılığı:** Aşama 15

- [ ] **16A — Görsel polish:** Spacing, typography, kart yoğunluğu, CTA ve tema tutarlılığı.
- [ ] **16B — Motion:** Task/mastery/exam animasyonları ve screen transition; gereksiz motion yok.
- [ ] **16C — Kullanılabilirlik:** Touch target, keyboard, scroll, error, loading ve empty state.
- [ ] **16D — Accessibility:** Kontrast, font scaling, screen reader ve renk dışı durum göstergeleri.

**Aşama 16 çıkışı:** Polished UI, accessibility checklist ve UX QA report.

---

# AŞAMA 17 — Gerçek Kullanım Pilotu, Kalibrasyon ve QA

**Eski MASTER_PLAN karşılığı:** Aşama 16

- [ ] **17A — Pilot başlangıcı:** Temiz veri, gerçek günlük süre ve en az 2–4 haftalık kullanım.
- [ ] **17B — Planner gözlemi:** Tekrar miktarı, yeni konu hızı, günlük süre, English oranı ve zayıf konu toparlanması.
- [ ] **17C — Mastery kalibrasyonu:** Mastery kolaylığı/sertliği, retention ve AI-help etkisi.
- [ ] **17D — Assessment kalibrasyonu:** Süre, zorluk, ezber riski ve transfer soruları.
- [ ] **17E — Teknik QA:** Crash, restart recovery, DB integrity/migration, offline, notification ve performance.
- [ ] **17F — Düzeltme döngüsü:** Bulgular → issue → düzeltme → parametre kalibrasyonu → ikinci pilot.

**Aşama 17 çıkışı:** Pilot report, calibration changes ve QA checklist.

---

# AŞAMA 18 — Release APK ve Kullanıma Hazır Sürüm

**Eski MASTER_PLAN karşılığı:** Aşama 17

- [ ] **18A — Release hazırlığı:** Versioning, release config, icon/name/splash, debug cleanup ve release notes.
- [ ] **18B — Veri güvenilirliği:** Backup/export, restore, migration ve update sonrası progress koruma.
- [ ] **18C — Final regression:** Today, lesson, quiz, mastery, planner, exams, retention, tutor, English, notifications ve theme.
- [ ] **18D — APK:** Release APK, gerçek cihaz kurulumu, fresh/update install ve smoke test.
- [ ] **18E — Dokümantasyon:** README, kullanım, limitler ve backup yöntemi.

**Aşama 18 çıkışı:** **Release APK — uygulama kullanıma hazırdır.**

---

# AŞAMA 19 — Uzun Vadeli Curriculum ve Kariyer Katmanını Genişlet

**Eski MASTER_PLAN karşılığı:** Aşama 18

- [ ] **19A — Modern C++ paketi:** RAII, smart pointers, STL, move semantics, templates ve profiling.
- [ ] **19B — Systems paketi:** OS internals, virtual memory, process/thread, concurrency, networking ve async I/O.
- [ ] **19C — Distributed Systems paketi:** RPC, replication, consistency, consensus/Raft, sharding ve failures.
- [ ] **19D — GPU/CUDA paketi:** Architecture, CUDA, memory hierarchy, coalescing, shared memory, warp, profiling ve kernels.
- [ ] **19E — Triton/Inference paketi:** Triton kernels, PyTorch internals, KV cache, batching, quantization ve vLLM/SGLang.
- [ ] **19F — Multi-GPU / AI Infrastructure:** NCCL, parallelism, RDMA/RoCE/InfiniBand, scheduling ve inference performance.
- [ ] **19G — Open source:** Issue, good first issue, PR, code review English ve merged PR tracking.
- [ ] **19H — Career readiness:** Job skill matching, readiness analizi, curriculum gaps, CV evidence ve mock interviews.
- [ ] **19I — Sürekli curriculum QA:** Yeni teknoloji sinyalleri, gerçek iş ilanları, düşük ROI temizliği ve rota ağırlıkları.

**Aşama 19 çıkışı:** Uzun vadeli curriculum paketleri, open-source tracking ve career-readiness engine.

---

# Güncel Konum

**Aktif aşama:** AŞAMA 1 — Ürün Çerçevesini Kilitle

**Aktif ilk adım:** **1A — Ana ürün amacı**

Henüz hiçbir ürün geliştirme adımı tamamlandı olarak işaretlenmemiştir.

## Bundan sonra konuşma biçimi

Örnekler:

- “1A’yı yapalım.”
- “1A tamam mı?”
- “3C için araştırma AI’a prompt hazırla.”
- “8A kararı neydi?”
- “11F test AI’a gitsin.”

Bu kodlar proje boyunca sabit referans olarak kullanılacaktır.
