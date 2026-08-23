# HANDOFF STATE — Güncel Proje Durumu ve Sohbet Aktarım Özeti

Bu dosya yeni bir ChatGPT sohbetine veya başka bir agent'a geçerken mevcut çalışma durumunu hızlıca devretmek için tutulur. Büyük ürün bağlamı için `PROJECT_MASTER_CONTEXT.md`, sabit çalışma kodları için `EXECUTION_INDEX.md`, ayrıntılı plan için `MASTER_PLAN.md`, kalıcı kararlar için `DECISIONS.md` okunmalıdır.

**Son güncelleme:** 2026-08-24

---

# 1. Projenin Şu Anki Durumu

Repo: `xpike-dgm/ai-infra-learning-coach`

Proje henüz kodlama aşamasında değildir. Ürün çerçevesi kilitlenmektedir.

Ana ürün:

> Sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering kariyer rotasında günlük olarak yöneten; her gün ne çalışacağını mevcut bilgi durumuna göre belirleyen; yalnız kanıtlanmış öğrenmeyi ilerleme kabul eden; mastery, retention, assessment ve prerequisite sonuçlarına göre gelecekteki programı yeniden düzenleyen kişisel adaptif Android öğrenme koçu.

Ana ilke:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

---

# 2. Kesinleşen Büyük Kararlar

## Kariyer yönü

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

Doğrudan CUDA ile başlanmayacak.

## İngilizce

Başlangıç A0 kabul edilir. İngilizce teknik eğitimin ön koşulu değildir; teknik programla paralel A0→A1→A2→B1→B2 ilerler.

## İlerleme

Ders izlemek, görev kartını tamamlamak veya gün geçirmek tek başına mastery vermez. Mastery; teori, coding, debugging, açıklama, transfer ve retention gibi kanıtlarla ölçülür.

## 1095 gün

`Gün X / 1095` gösterilmeyecek. 3 yıl yalnız yaklaşık planlama/curriculum ufkudur.

## Knowledge graph

Curriculum sabit günlük liste değildir. Prerequisite ilişkili graph kullanılır; eksik konu yalnız bağımlı dalları bekletir.

## Adaptif planner

Mastery, retention, assessment, prerequisite ve günlük kapasiteye göre görevler seçilir. Kaçırılan günler ceza değil replan tetikleyicisidir.

## Assessment

Günlük mikro değerlendirme + haftalık sınav + aylık yeterlilik sınavı bulunur. Sonuçlar gelecekteki programı gerçekten değiştirir.

## AI Tutor

AI yardımına izin vardır; fakat AI ile tamamlanan iş gerçek anlama yerine geçmez. Comprehension/transfer doğrulaması gerekir.

## Kişisel kullanım

Auth, ödeme, abonelik, sosyal sistem, admin paneli ve multi-tenant SaaS yoktur.

## Multi-agent geliştirme

- Araştırma AI
- Kodlama AI
- bağımsız Test/QA AI
- ana yönetici/koordinatör

Kritik akış: **Yönetici → gerekirse Araştırma → Spec → Kodlama → QA → PASS/FAIL → GitHub kayıtları**.

## Sabit yürütme kodları

Aşamalar 1–19; alt adımlar `1A`, `1B`, `2A`, `3C` vb. sabit kodlarla takip edilir.

---

# 3. Tamamlanan Ürün Adımları

## ✅ 1A — Ana ürün amacı

Ana çıktı: `docs/PRODUCT_REQUIREMENTS.md`

Ürünün tek cümlelik amacı, günlük değer önerisi, klasik kurs/todo uygulamasından farkı, kanıtlanmış öğrenme ilkesi, adaptif davranış ve kariyer rotası kilitlendi.

## ✅ 1B — V1 kapsamı

Ana çıktı: `docs/V1_SCOPE.md`

V1, Android odaklı günlük kullanılabilir kişisel release olarak sınırlandı.

V1'de kesin olacak ana yetenekler:

- Today/daily plan,
- knowledge graph + prerequisite,
- adaptive planner/replan,
- task runner,
- mastery engine,
- daily micro assessment,
- weekly/monthly exams,
- retention/spaced repetition,
- remediation,
- AI Tutor v1,
- parallel technical English,
- ilk 8–12 haftalık production curriculum,
- progress/weakness,
- local-first persistence,
- notifications,
- polished UI,
- backup/export/restore.

V1 dışında bırakılan başlıca alanlar:

- 3 yıllık curriculum'un tamamı,
- sosyal/ticari özellikler,
- cloud multi-device live sync,
- iOS/web/desktop,
- gelişmiş career-market engine,
- tam voice tutor,
- gömülü tam IDE/compiler/sandbox,
- aşırı gamification.

---

# 4. Şu Anda Bulunulan Kesin Adım

**AŞAMA 1 — Ürün Çerçevesini Kilitle**

- `1A` ✅ Ana ürün amacı
- `1B` ✅ V1 kapsamı
- `1C` 🟡 **Başarı kriterleri — AKTİF**
- `1D` ⬜ Non-goals

## Aktif iş: 1C

V1 kapsamındaki özellikleri ölçülebilir acceptance kriterlerine dönüştürmek.

Özellikle:

- daily planner doğru mu,
- mastery yanlış pozitif üretmiyor mu,
- prerequisite doğru kilitliyor mu,
- weekly/monthly assessment gerçekten planner'ı değiştiriyor mu,
- retention ve remediation çalışıyor mu,
- kaçırılan gün sağlıklı replan oluyor mu,
- AI Tutor ana sistemi bozuyor mu,
- English track kayboluyor mu,
- local data restart/update sonrası korunuyor mu,
- release APK gerçek cihazda stabil mi

soruları test edilebilir kriterlere çevrilecek.

---

# 5. Hâlâ Açık Ana Konular

- V1 success/acceptance criteria (`1C`)
- consolidated non-goals (`1D`)
- mastery formülü ve threshold'lar
- topic state machine
- spaced repetition algoritması
- adaptive planner decision table
- assessment composition
- curriculum graph
- English mastery
- UX/wireframe
- mobil teknoloji seçimi
- local database
- AI provider architecture

---

# 6. Temel Doküman Okuma Sırası

1. `docs/START_HERE.md`
2. `docs/PROJECT_MASTER_CONTEXT.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `PROJECT_CONTEXT.md`
7. `docs/DECISIONS.md`
8. `docs/MASTER_PLAN.md`
9. `docs/AI_AGENT_WORKFLOW.md`
10. `docs/PROGRESS_LOG.md`
11. İlgili ürün/spec dosyaları

---

# 7. Yeni Sohbetin Yapacağı İlk İş

Repo hafızasını okuduktan sonra doğrudan:

> **`1C — Başarı kriterleri`**

adımından devam et.

Daha önce kilitlenen `1A` ve `1B` kararlarını kullanıcı açıkça değiştirmedikçe yeniden tartışmaya açma.

---

# 8. Sohbet Aktarım Mesajı

> `GitHub'daki xpike-dgm/ai-infra-learning-coach reposu önceki uzun sohbetimin kalıcı proje hafızasıdır. docs/START_HERE.md dosyasından başlayıp belirtilen sırayı oku. Önceki sohbetin devamı gibi davran. STEP_STATUS.md ve HANDOFF_STATE.md içindeki aktif adım kodundan devam et. Daha önce alınmış kararları yeniden sordurma. Araştırma/kodlama/test işlerini AI_AGENT_WORKFLOW.md protokolüne göre böl. Yeni kararları ve tamamlanan adımları GitHub'a kaydet.`

---

# 9. Güncelleme Kuralı

Bu dosya aktif adım değiştiğinde, ana karar alındığında, milestone tamamlandığında veya sohbet devredilmeden önce güncellenir.
