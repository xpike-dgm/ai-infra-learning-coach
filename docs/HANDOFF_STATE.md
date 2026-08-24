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

- İngilizce A0'dan teknik eğitimle paralel ilerler; önce bitirilmesi gereken ayrı ön koşul değildir.
- `Gün X / 1095` ve sahte kariyer yüzde ilerlemesi ana metrik olmayacaktır.
- Curriculum sabit takvim değil prerequisite ilişkili knowledge graph olacaktır.
- Eksik bir konu yalnız kendisine bağlı dalları bekletir; bağımsız dallar devam eder.
- Daily planner mastery, retention, assessment, prerequisite ve günlük kapasiteye göre plan üretir.
- Günlük mikro assessment + haftalık sınav + aylık yeterlilik sınavı gelecekteki programı değiştirir.
- AI yardımı yasak değildir; ancak AI ile tamamlanan iş gerçek anlama yerine geçmez.
- Uygulama kişisel kullanım içindir; auth, ödeme, abonelik, sosyal sistem, admin paneli ve multi-tenant SaaS kapsam dışıdır.
- Kritik geliştirme akışı: **Yönetici → gerekirse Araştırma AI → Spec → Kodlama AI → bağımsız Test/QA AI → PASS/FAIL → GitHub kaydı**.
- Proje Aşama 1–19 ve `1A / 1B / ...` sabit kodlarıyla yürütülür.

---

# 3. Tamamlanan Ürün Adımları

## ✅ 1A — Ana ürün amacı

Çıktı: `docs/PRODUCT_REQUIREMENTS.md`

Ürünün amacı, günlük değer önerisi, kanıtlanmış öğrenme ilkesi, adaptif davranış ve kariyer rotası kilitlendi.

## ✅ 1B — V1 kapsamı

Çıktı: `docs/V1_SCOPE.md`

V1 Android odaklı günlük kullanım release'i olarak sınırlandı. Daily planner, knowledge graph/prerequisite, mastery, assessments, retention, remediation, AI Tutor, parallel English, ilk 8–12 haftalık curriculum, local-first persistence, polished UI ve backup/restore V1 kapsamındadır.

## ✅ 1C — Başarı kriterleri

Çıktı: `docs/V1_SUCCESS_CRITERIA.md`

V1 için 49 acceptance kriteri tanımlandı ve P0/P1/P2 olarak sınıflandırıldı.

Kritik release kuralları:

- tüm P0 kriterleri PASS,
- kritik P1 fonksiyon hatası yok,
- hard prerequisite bypass yok,
- progress data loss yok,
- task completion/tek quiz ile yanlış mastery yok,
- weekly/monthly sınav sonuçları planner'ı gerçekten değiştiriyor,
- missed-day replan backlog yığmıyor,
- AI çekirdek mastery/planner/prerequisite kurallarını keyfi aşamıyor,
- backup/restore/migration güvenilir,
- final kritik akışlar bağımsız QA tarafından doğrulanıyor,
- gerçek Android cihaz ve pilot testleri geçiliyor.

Mastery threshold, assessment ağırlıkları, spaced repetition interval'leri ve planner oranları 1C'de rastgele sabitlenmedi; ilgili sonraki aşamalarda araştırma/simülasyon/pilot ile belirlenecek.

---

# 4. Şu Anda Bulunulan Kesin Adım

**AŞAMA 1 — Ürün Çerçevesini Kilitle**

- `1A` ✅ Ana ürün amacı
- `1B` ✅ V1 kapsamı
- `1C` ✅ Başarı kriterleri
- `1D` 🟡 **Non-goals — AKTİF**

## Aktif iş: 1D

V1 ve projenin özellikle ne olmaya çalışmadığını tek bir kalıcı listede konsolide etmek. Amaç scope creep'i önlemektir.

1D tamamlandığında **Aşama 1 tamamen kapanacak** ve sonraki aktif adım:

> **`2A — Bilgi birimleri`**

olacaktır.

---

# 5. Hâlâ Açık Ana Konular

- consolidated non-goals (`1D`)
- Domain → Module → Topic → Skill → Learning Objective modeli
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
8. `docs/MASTER_PLAN.md`
9. `docs/AI_AGENT_WORKFLOW.md`
10. `docs/PROGRESS_LOG.md`
11. İlgili spec dosyaları (`PRODUCT_REQUIREMENTS.md`, `V1_SCOPE.md`, `V1_SUCCESS_CRITERIA.md`, vb.)

---

# 7. Yeni Sohbetin Yapacağı İlk İş

Repo hafızasını okuduktan sonra doğrudan:

> **`1D — Non-goals`**

adımından devam et.

Daha önce kilitlenen `1A`, `1B`, `1C` kararlarını kullanıcı açıkça değiştirmedikçe yeniden tartışmaya açma.

---

# 8. Sohbet Aktarım Mesajı

> `GitHub'daki xpike-dgm/ai-infra-learning-coach reposu önceki uzun sohbetimin kalıcı proje hafızasıdır. docs/START_HERE.md dosyasından başlayıp belirtilen sırayı oku. Önceki sohbetin devamı gibi davran. STEP_STATUS.md ve HANDOFF_STATE.md içindeki aktif adım kodundan devam et. Daha önce alınmış kararları yeniden sordurma. Araştırma/kodlama/test işlerini AI_AGENT_WORKFLOW.md protokolüne göre böl. Yeni kararları ve tamamlanan adımları GitHub'a kaydet.`

---

# 9. Güncelleme Kuralı

Bu dosya aktif adım değiştiğinde, ana karar alındığında, milestone tamamlandığında veya sohbet devredilmeden önce güncellenir.
