# AI Infra Learning Coach — Master Geliştirme Planı

**Durum:** AKTİF / CANONICAL DETAYLI PLAN  
**Son senkron:** 2026-08-24

Bu belge projenin ayrıntılı yürütme planıdır. Sabit adım kimliklerinin canonical kaynağı `docs/EXECUTION_INDEX.md` dosyasıdır; bu dosyadaki checklist ve current-state onunla senkron tutulur.

Ana ürün ilkesi:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

## Zorunlu yürütme kuralı

Her numaralı adım:

`PRE-STEP GitHub refresh → gerekiyorsa Research/Coding/QA → spec/çıktı → kabul kontrolü → POST-STEP GitHub sync → MASTER_PLAN checkbox/completion note → sonraki adım`

Tamamlanan adım `[x]`, bekleyen `[ ]`, aktif adım açıklamada **AKTİF** olarak tutulur.

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle ✅

## Amaç

Kod başlamadan ürünün ne yaptığı, ne yapmadığı ve V1'in ne zaman başarılı sayılacağı yoruma kapatılır.

### [x] 1A — Ana ürün amacı

- Tek cümlelik ürün amacı.
- Günlük kullanıcı değeri.
- Klasik course/todo uygulamasından fark.
- “Kanıtlanmış öğrenme” ilkesi.
- Ana kariyer rotasıyla ilişki.

**Çıktı:** `docs/PRODUCT_REQUIREMENTS.md`

### [x] 1B — V1 kapsamı

- Android kişisel kullanım release kapsamı.
- V1 içi / V1 dışı özellikler.
- 3 yıllık curriculum'un V1 ön koşulu olmaması.
- Auth/payment/social/admin/multi-tenant sınırı.

**Çıktı:** `docs/V1_SCOPE.md`

### [x] 1C — Başarı kriterleri

- Ölçülebilir P0/P1/P2 acceptance kriterleri.
- Planner, mastery, prerequisite, retention, replan ve data reliability kapıları.
- Kritik final akışlarında bağımsız QA.

**Çıktı:** `docs/V1_SUCCESS_CRITERIA.md`

### [x] 1D — Non-goals

- Sabit takvimli kurs olmayacak.
- Streak/time/task-completion mastery olmayacak.
- Full mobile IDE / SaaS / social / payment scope creep olmayacak.
- Kariyer garantisi ve sahte bilimsel kesinlik kullanılmayacak.

**Çıktı:** `docs/NON_GOALS.md`

> **Tamamlanma notu — 2026-08-24:** 1A–1D tamamlandı. Ürün amacı, V1 sınırı, acceptance ve non-goals kilitlendi. Aşama 1 kapandı.

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla

## Amaç

`Dersi tamamladı = öğrendi` hatasını ortadan kaldıran, açıklanabilir ve ileride deterministik olarak hesaplanabilir öğrenme modelini kurmak.

### [x] 2A — Bilgi birimleri

- `Domain → Module → Topic → Skill → Learning Objective`.
- `Domain/Module/Topic` organizasyon; `Skill/Objective` ölçüm katmanı.
- Skill canonical mastery/prerequisite seviyesi.
- Topic/Module/Domain mastery derived.
- Skill → Skill runtime prerequisite.
- Topic ↔ Skill many-to-many.
- English aynı Skill modelinde, ancak gereksiz global hard-lock değil.

**Çıktı:** `docs/LEARNING_ENGINE_SPEC.md`

> **Tamamlanma notu — 2026-08-24:** D-021 ile canonical learning unit modeli kilitlendi.

### [x] 2B — Topic durumları

- `locked`
- `available`
- `learning`
- `mastered`
- `weakening`
- `remediation_required`
- coverage ve mastery ayrımı.
- Başlanmış Topic prerequisite regression yüzünden geriye dönük locked olmaz.
- State reason/history açıklanabilir olmalı.

**Çıktı:** `docs/TOPIC_STATE_MACHINE.md`

> **Tamamlanma notu — 2026-08-24:** D-023 ile Topic state machine Skill verilerinden derived orchestration modeli olarak kilitlendi.

### [x] 2C — Mastery sinyalleri

- concept recognition.
- concept recall.
- code reading/output prediction.
- coding/production.
- debugging/diagnosis.
- explanation/justification.
- transfer/novel application.
- retention/delayed retrieval.
- integrated project/task.
- time/completion/streak/self-confidence contextual; mastery değil.
- invalid/contaminated evidence kuralları.
- evidence independence/diversity.
- objective-specific evidence profile.

**Çıktı:** `docs/MASTERY_SIGNALS_SPEC.md`

> **Tamamlanma notu — 2026-08-24:** D-025 ile çok kaynaklı evidence modeli ve false-positive guardrail'leri kilitlendi.

### [x] 2D — AI / ipucu etkisi

- H0–H4 assistance content taxonomy.
- yardım timing'i.
- artifact authorship/provenance.
- independent / assisted / practice-only / requires-recheck yorumları.
- AI-generated code'un coding mastery sayılmaması.
- full/partial solution sonrası fresh/unseen recheck.
- comprehension ile production ayrımı.
- compiler/test/docs/autocomplete için objective-specific allowed-tools policy.
- external AI için surveillance yerine provenance + fresh recheck.
- AI helper ve evaluator provenance ayrımı.

**Çıktı:** `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`

> **Tamamlanma notu — 2026-08-24:** D-026 ile yardım kullanımı yasaklanmadan independent mastery ile assisted performance ayrıldı. Exact sayısal etki 2E'ye bırakıldı.

### [ ] 2E — Mastery formülü v0 — **AKTİF**

Bu adım başlamadan yeni PRE-STEP GitHub refresh yapılır ve **Research AI** kullanılır.

Araştırılacak/tasarlanacak:

- evidence aggregation yaklaşımı.
- Objective → Skill aggregation.
- direct/corroborating evidence etkisi.
- difficulty / novelty / independence.
- H0–H4 assistance'ın kurallı/sayısal etkisi.
- positive / partial / negative evidence.
- minimum independent/direct/diverse evidence gate.
- mastery threshold.
- prerequisite-ready koşulu.
- confidence gerekip gerekmediği ve nasıl hesaplanacağı.
- tek quiz / tek sınav / aynı familya false-positive önleme.
- deterministic, explainable Mastery Formula v0.

**Beklenen çıktı:** `docs/MASTERY_FORMULA_V0.md` veya eşdeğer canonical spec.

### [ ] 2F — Unutma modeli

- spaced repetition yaklaşımı.
- review scheduling.
- başarılı/başarısız delayed retrieval etkisi.
- decay / retention risk.
- `mastered → weakening` davranışının exact trigger'ları.
- doğal yeniden kullanımın retention evidence etkisi.

**Beklenen çıktı:** forgetting/retention spec.

### Aşama 2 kapanış kapısı

Aynı evidence geçmişi verildiğinde sistem neden o Skill state'ini verdiğini deterministik ve açıklanabilir biçimde hesaplayabilmeli; yardım ve retention ayrı modellenmiş olmalı.

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla

## Amaç

Uygulamanın her gün `Bugün ne çalışmalıyım?` kararını current state'e göre vermesi.

### [ ] 3A — Günlük kapasite
- kısa/normal/yoğun gün.
- hedef süre ve dinamik değişiklik.
- minimum task block.
- overflow davranışı.

### [ ] 3B — Görev kategorileri
- yeni konu.
- remediation.
- retention.
- coding.
- debugging.
- English.
- project.
- micro assessment.

### [ ] 3C — Öncelik puanı
- kritik prerequisite.
- due retention.
- weak/remediation Skill.
- sıradaki uygun yeni Topic.
- paralel English.
- görev çeşitliliği.

### [ ] 3D — Prerequisite davranışı
- hard/soft prerequisite.
- dependent branch wait.
- independent branch continue.

### [ ] 3E — Hızlı öğrenme
- diagnostic/skip.
- tek kolay quiz ile skip yok.
- validated coverage waiver.
- skip sonrası retention.

### [ ] 3F — Kaçırılan günler
- 1 gün / birkaç gün / 1 hafta+.
- backlog dumping yok.
- current state'ten replan.

### [ ] 3G — Açıklanabilir planner
- reason code.
- kullanıcıya `neden bugün?` açıklaması.

### [ ] 3H — Planner simülasyonu
- hızlı öğrenen.
- tek temelde takılan.
- English/technical hız farkı.
- sık gün kaçıran.
- yoğun AI yardımı kullanan.
- yeterli sanal kullanıcı senaryosu.

**Çıktı:** `docs/ADAPTIVE_PLANNER_SPEC.md`, decision table, pseudocode, simulation suite.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla

### [ ] 4A — Günlük mikro değerlendirme
- quiz, short answer, code reading, coding, debugging, explanation, retrieval.

### [ ] 4B — Haftalık sınav
- yeni/eski skill dengesi.
- coding/debugging/English.
- sonuç → next-week replan.

### [ ] 4C — Aylık yeterlilik sınavı
- comprehensive Skill evidence.
- retention.
- coding/debugging/explanation.
- program değişikliği.

### [ ] 4D — Soru bankası
- metadata.
- target Skill/Objective.
- required prerequisites.
- difficulty/type.
- variant family.
- rubric/expected answer.
- allowed tools/assistance policy.

### [ ] 4E — AI-generated soru doğrulaması
- technical correctness.
- ambiguity.
- prerequisite eligibility.
- target-objective fit.
- validator/rubric.
- trusted vs practice-only AI item.

**Çıktı:** `docs/ASSESSMENT_SYSTEM_SPEC.md`, question schema, exam templates, scoring rubrics.

---

# AŞAMA 5 — Curriculum ve Knowledge Graph Mimarisini Tasarla

### [ ] 5A — Ana domain haritası

Technical English, Computer Fundamentals, C, Linux, DSA, Modern C++, OS/Memory, Concurrency, Networking, Distributed Systems, GPU Architecture, CUDA, Triton, ML/LLM Systems, Inference, Multi-GPU, AI Infrastructure, Open Source/Career.

### [ ] 5B — Topic metadata
- objectives.
- Skill links.
- prerequisites.
- effort.
- criticality.
- assessment/evidence profile.
- remediation.
- career relevance.

### [ ] 5C — İlk 8–12 haftalık curriculum graph
- Computer Fundamentals.
- C Foundations.
- Memory Foundations.
- Linux Foundations.
- ilk DSA.
- paralel English.
- DAG links.

### [ ] 5D — Curriculum QA
- missing prerequisite.
- circular dependency.
- aşırı/erken zorluk.
- düşük ROI içerik.
- career relevance.

**Çıktı:** `docs/CURRICULUM_GRAPH_SPEC.md`, initial curriculum dataset, QA report.

---

# AŞAMA 6 — İngilizce Paralel Hattını Tasarla

Bağlayıcı ön kural: `docs/ENGLISH_FOUNDATION_RULES.md`.

### [ ] 6A — Başlangıç ölçümü
- A0 diagnostic.
- reading/vocabulary/listening/basic production.

### [ ] 6B — A1/A2/B1/B2 teknik hedefleri
- CEFR + technical capability mapping.

### [ ] 6C — Günlük English bileşeni
- grammar.
- high-frequency vocabulary.
- technical vocabulary.
- reading/writing/listening/speaking.

### [ ] 6D — Teknik entegrasyon
- o günkü teknik içerikle bağlantı.
- bilingual scaffold.
- mastery'ye göre Türkçe desteğin azalması.

### [ ] 6E — English mastery
- reading.
- writing.
- listening.
- speaking.
- technical vocabulary/explanation.

**Çıktı:** `docs/ENGLISH_TRACK_SPEC.md`, first 12-week English curriculum, level criteria.

---

# AŞAMA 7 — Ürün Gereksinimleri, Ekranlar ve UX'i Kilitle

### [ ] 7A — Bilgi mimarisi
### [ ] 7B — Ana ekran
### [ ] 7C — Günlük çalışma akışı
### [ ] 7D — Sınav UX
### [ ] 7E — Skill/progress UX
### [ ] 7F — Tasarım sistemi
### [ ] 7G — Wireframe/prototip

Ana ekranın cevabı: `Bugün ne yapmalıyım?`

Ana navigation adayları: Today | Roadmap/Graph | Progress | Exams | Settings.

**Çıktı:** `docs/UX_SPEC.md`, screen inventory, navigation map, wireframes, design system.

---

# AŞAMA 8 — Teknik Mimari ve Veri Modelini Kesinleştir

### [ ] 8A — Mobil teknoloji seçimi
- Kotlin/Compose, Flutter, RN vb. araştırma/karar.

### [ ] 8B — Veri saklama / local-first
- database seçimi.
- migrations.
- curriculum vs user state.
- backup/restore temeli.

### [ ] 8C — Domain veri modeli
- Domain/Module/Topic/Skill/Objective.
- PrerequisiteEdge.
- Task/AssessmentItem.
- Attempt/EvidenceEvent/AssistanceContext.
- MasteryState/ReviewSchedule.
- DailyPlan/StudySession.
- exams/results/AI interactions.

### [ ] 8D — Servis sınırları
- curriculum.
- mastery.
- prerequisite.
- planner.
- assessment.
- retention/remediation.
- AI tutor.
- analytics.

### [ ] 8E — AI entegrasyon mimarisi
- provider-independent adapter/router.
- model selection.
- prompts/versioning.
- response validation.
- API key/security/proxy kararı.
- cost/log/fallback.
- AI kapalıyken core app davranışı.

### [ ] 8F — Test stratejisi
- unit.
- simulation.
- migration.
- UI smoke.
- regression.

**Çıktı:** `docs/TECH_ARCHITECTURE.md`, `docs/DATA_MODEL.md`, ADR/architecture diagram.

---

# AŞAMA 9 — Mobil Proje İskeleti ve Tasarım Sistemini Kur

> **Ana production uygulama kodlamasının başladığı aşama.**

### [ ] 9A — Proje kurulumu
### [ ] 9B — Navigation
### [ ] 9C — Design system implementation
### [ ] 9D — Local database
### [ ] 9E — Temel uygulama sağlığı

**Çıktı:** çalışan mobil skeleton/debug build.

---

# AŞAMA 10 — Çekirdek Günlük Öğrenme Akışı MVP'sini Geliştir

### [ ] 10A — Today ekranı
### [ ] 10B — Task runner
### [ ] 10C — Session state
### [ ] 10D — Günlük mikro quiz
### [ ] 10E — Gün sonu

**Çıktı:** Daily learning MVP / Task Engine v1 / Quiz v1.

---

# AŞAMA 11 — Mastery ve Adaptif Planner'ı Koda Dök

### [ ] 11A — Mastery Engine v1
### [ ] 11B — Prerequisite Engine
### [ ] 11C — Planner Engine v1
### [ ] 11D — Replan
### [ ] 11E — Explanation / reason codes
### [ ] 11F — Sanal kullanıcı testleri

**Çıktı:** Mastery + Prerequisite + Adaptive Planner v1.

---

# AŞAMA 12 — Haftalık/Aylık Sınav, Retention ve Remediation'ı Geliştir

### [ ] 12A — Haftalık sınav
### [ ] 12B — Aylık sınav
### [ ] 12C — Spaced repetition
### [ ] 12D — Remediation Engine
### [ ] 12E — Program değişiklik raporu

**Çıktı:** exam engines + retention + remediation.

---

# AŞAMA 13 — AI Tutor ve Akıllı Değerlendirme Katmanını Geliştir

### [ ] 13A — Tutor davranış sözleşmesi
### [ ] 13B — Yanlış analizi
### [ ] 13C — Alternatif anlatım
### [ ] 13D — Kod değerlendirme
### [ ] 13E — AI-generated code comprehension check
### [ ] 13F — Açık uçlu cevap değerlendirme
### [ ] 13G — Provider abstraction / fallback

**Çıktı:** AI Tutor v1 + evaluators.

---

# AŞAMA 14 — İlk 8–12 Haftalık Gerçek Eğitim İçeriğini Üret ve QA Et

### [ ] 14A — Computer Fundamentals
### [ ] 14B — C Foundations
### [ ] 14C — Memory Foundations
### [ ] 14D — Linux Foundations
### [ ] 14E — English A0→A1/A2
### [ ] 14F — Assessment content
### [ ] 14G — Content QA

**Çıktı:** production curriculum v1 + Assessment Bank v1.

---

# AŞAMA 15 — İlerleme, Analitik, Ayarlar ve Günlük Kullanım Araçları

### [ ] 15A — Skill analytics
### [ ] 15B — Öğrenme geçmişi
### [ ] 15C — Progress gösterim kuralları
### [ ] 15D — Ayarlar
### [ ] 15E — Bildirimler

---

# AŞAMA 16 — UI/UX Polish ve Erişilebilirlik

### [ ] 16A — Görsel polish
### [ ] 16B — Motion
### [ ] 16C — Kullanılabilirlik
### [ ] 16D — Accessibility

---

# AŞAMA 17 — Gerçek Kullanım Pilotu, Kalibrasyon ve QA

### [ ] 17A — Pilot başlangıcı
### [ ] 17B — Planner gözlemi
### [ ] 17C — Mastery kalibrasyonu
### [ ] 17D — Assessment kalibrasyonu
### [ ] 17E — Teknik QA
### [ ] 17F — Düzeltme döngüsü

Amaç: birkaç haftalık gerçek kullanımda planner/mastery/assessment'ın sürekli elle müdahale gerektirmeden faydalı çalışması.

---

# AŞAMA 18 — Release APK ve Kullanıma Hazır Sürüm

### [ ] 18A — Release hazırlığı
### [ ] 18B — Veri güvenilirliği
### [ ] 18C — Final regression
### [ ] 18D — APK / gerçek cihaz testleri
### [ ] 18E — Release dokümantasyonu

**Çıktı:** Release APK — temel ürün günlük kullanıma hazır.

---

# AŞAMA 19 — Uzun Vadeli Curriculum ve Kariyer Katmanı

### [ ] 19A — Modern C++ paketi
### [ ] 19B — Systems paketi
### [ ] 19C — Distributed Systems paketi
### [ ] 19D — GPU/CUDA paketi
### [ ] 19E — Triton/Inference paketi
### [ ] 19F — Multi-GPU / AI Infrastructure
### [ ] 19G — Open source
### [ ] 19H — Career readiness
### [ ] 19I — Sürekli curriculum QA

Bu aşama iteratiftir; V1 release'in ön koşulu değildir.

---

# Güncel Konum

**Aktif aşama:** AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla  
**Tamamlanan:** `1A–1D`, `2A`, `2B`, `2C`, `2D`  
**Aktif:** **`2E — Mastery formülü v0`**

## Bir sonraki yürütme

1. 2E PRE-STEP GitHub refresh.
2. Research AI'a mastery formula / learning-science araştırma görevi.
3. Araştırma çıktısını mevcut `MASTERY_SIGNALS_SPEC.md` + `AI_ASSISTANCE_EVIDENCE_SPEC.md` ile sentezleme.
4. Deterministik Mastery Formula v0.
5. POST-STEP GitHub + MASTER_PLAN sync.
