# HANDOFF STATE — Güncel Proje Durumu ve Sohbet Aktarım Özeti

Bu dosya yeni bir ChatGPT sohbetine veya başka bir agent'a geçerken mevcut çalışma durumunu hızlıca devretmek için tutulur.

**Son güncelleme:** 2026-08-24

Repo: `xpike-dgm/ai-infra-learning-coach`

---

# 1. Ana Ürün

> Sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering kariyer rotasında günlük olarak yöneten; her gün ne çalışacağını mevcut bilgi durumuna göre belirleyen; yalnız kanıtlanmış öğrenmeyi ilerleme kabul eden; mastery, retention, assessment ve prerequisite sonuçlarına göre gelecekteki programı yeniden düzenleyen kişisel adaptif Android öğrenme koçu.

Ana ilke:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Ana teknik yön:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

---

# 2. Kesinleşen Büyük Kurallar

- İngilizce A0'dan teknik eğitimle paralel ilerler.
- `Gün X / 1095` ve sahte kariyer yüzde ilerlemesi ana metrik değildir.
- Curriculum sabit takvim değil prerequisite ilişkili knowledge graph'tır.
- Eksik konu yalnız bağımlı dalları bekletir; bağımsız dallar devam eder.
- Planner mastery, retention, assessment, prerequisite ve günlük kapasiteye göre plan üretir.
- Günlük mikro assessment + haftalık + aylık sınav gelecekteki programı değiştirir.
- AI yardımı mümkündür; fakat AI ile tamamlanan iş gerçek anlama yerine geçmez.
- Uygulama kişisel kullanım içindir; auth/payment/social/admin/multi-tenant SaaS varsayılan kapsam dışıdır.
- Kritik geliştirme akışı: **Yönetici → gerekirse Araştırma AI → Spec → Kodlama AI → bağımsız Test/QA AI → PASS/FAIL → GitHub kaydı**.
- Proje Aşama 1–19 ve `1A / 1B / ...` sabit kodlarıyla yürütülür.
- Non-goals kapsam değişikliği sessizce yapılamaz; yeni decision kaydı gerekir.

---

# 3. Tamamlanan Ürün Aşaması

## ✅ AŞAMA 1 — Ürün Çerçevesini Kilitle

### ✅ 1A — Ana ürün amacı
Çıktı: `docs/PRODUCT_REQUIREMENTS.md`

### ✅ 1B — V1 kapsamı
Çıktı: `docs/V1_SCOPE.md`

### ✅ 1C — Başarı kriterleri
Çıktı: `docs/V1_SUCCESS_CRITERIA.md`

49 acceptance kriteri P0/P1/P2 olarak tanımlandı. Tüm P0 kriterleri PASS olmadan release yoktur.

### ✅ 1D — Non-goals
Çıktı: `docs/NON_GOALS.md`

Ürün seviyesi non-goals ile yalnız V1'e ertelenen özellikler ayrıldı. Sabit kurs, time/streak progress, tamamen LLM kontrollü curriculum, SaaS/social/payment scope creep, tam IDE kimliği ve sahte bilimsel kesinlik reddedildi. Tam curriculum, diğer platformlar, live cloud sync, full voice tutor, full sandbox ve career-market engine V1 sonrasına bırakıldı.

**Aşama 1 sonucu:** Ne inşa ettiğimiz, V1'de ne olduğu, ne zaman başarılı sayıldığı ve ne yapmayacağımız artık kilitlidir.

---

# 4. Şu Anda Bulunulan Kesin Adım

**AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla**

- `2A` 🟡 **Bilgi birimleri — AKTİF**
- `2B` ⬜ Topic durumları
- `2C` ⬜ Mastery sinyalleri
- `2D` ⬜ AI/ipucu etkisi
- `2E` ⬜ Mastery formülü v0
- `2F` ⬜ Unutma modeli

## Aktif iş: 2A

`Domain → Module → Topic → Skill → Learning Objective` modelini kesinleştirmek.

2A'da netleşmesi gerekenler:

- her katmanın kesin anlamı,
- hangi katmanın curriculum organizasyonu için olduğu,
- mastery'nin hangi seviyede tutulacağı,
- prerequisite edge'in hangi birimler arasında kurulabileceği,
- bir topic ile skill arasındaki fark,
- learning objective'in nasıl ölçülebilir yazılacağı,
- aynı skill'in birden fazla topic/module ile ilişkisi gerekiyorsa nasıl temsil edileceği,
- English ve teknik domainlerin aynı modele nasıl oturacağı.

---

# 5. Hâlâ Açık Ana Konular

- bilgi birimi modeli (`2A`)
- topic state machine
- mastery formula / threshold / evidence weights
- AI-help impact
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

# 6. İlk Okuma Sırası

1. `docs/START_HERE.md`
2. `docs/PROJECT_MASTER_CONTEXT.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `PROJECT_CONTEXT.md`
7. `docs/DECISIONS.md`
8. `docs/PRODUCT_REQUIREMENTS.md`
9. `docs/V1_SCOPE.md`
10. `docs/V1_SUCCESS_CRITERIA.md`
11. `docs/NON_GOALS.md`
12. `docs/MASTER_PLAN.md`
13. `docs/AI_AGENT_WORKFLOW.md`
14. `docs/PROGRESS_LOG.md`

---

# 7. Yeni Sohbetin Yapacağı İlk İş

Repo hafızasını okuduktan sonra doğrudan:

> **`2A — Bilgi birimleri`**

adımından devam et.

Aşama 1 kararlarını kullanıcı açıkça değiştirmedikçe yeniden tartışmaya açma.

---

# 8. Sohbet Aktarım Mesajı

> `GitHub'daki xpike-dgm/ai-infra-learning-coach reposu önceki uzun sohbetimin kalıcı proje hafızasıdır. docs/START_HERE.md dosyasından başlayıp belirtilen sırayı oku. Önceki sohbetin devamı gibi davran. STEP_STATUS.md ve HANDOFF_STATE.md içindeki aktif adım kodundan devam et. Daha önce alınmış kararları yeniden sordurma. Araştırma/kodlama/test işlerini AI_AGENT_WORKFLOW.md protokolüne göre böl. Yeni kararları ve tamamlanan adımları GitHub'a kaydet.`
