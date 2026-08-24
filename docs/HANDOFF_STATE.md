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

## Öğrenme davranışında ayrıca kilitlenen kurallar

Ayrıntılı kaynak: `docs/LEARNING_BEHAVIOR_RULES.md` / D-022.

- Uygulama yalnız test etmez; uygulama içinde öğretir, uygulatır, ölçer ve eksikse yeniden öğretir.
- Gerekli temel Learning Objective'ler coverage açısından atlanmaz; ileri detaylar doğru sonraki Topic/Skill'e bırakılır.
- Yanlış cevap ceza değil evidence/remediation sinyalidir.
- Yanlış yapılan sorunun birebir aynısı hemen tekrar edilerek ezber ödüllendirilmez; aynı Skill farklı varyasyonla yeniden ölçülür.
- Henüz öğretilmemiş prerequisite isteyen soru kullanıcıyı başarısız sayamaz.
- Zorluk bilinmeyen kavram gizleyerek değil, öğrenilmiş kavramları daha karmaşık kullanarak artırılır.
- Kritik prerequisite süre doldu diye terk edilmez; yalnız ona bağımlı dal bekler, bağımsız dallar devam eder.
- Remediation mevcut günlük kapasitenin içine yerleştirilir; başarısızlık günü kontrolsüz uzatmaz.
- Mastered Skill'ler haftalar/aylar sonra retention ile yeniden test edilebilir; tek hata tüm mastery'yi sıfırlamaz.
- Bilgi havuzu doğrulanmış çekirdek; soru havuzu doğrulanmış çekirdek + question families/variants + kontrollü AI üretimi olarak tasarlanır.
- AI provider/model henüz kalıcı karar değildir; çekirdek mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.

---

# 3. Tamamlanan Aşamalar / Adımlar

## ✅ AŞAMA 1 — Ürün Çerçevesini Kilitle

- `1A` ✅ Ana ürün amacı — `docs/PRODUCT_REQUIREMENTS.md`
- `1B` ✅ V1 kapsamı — `docs/V1_SCOPE.md`
- `1C` ✅ Başarı kriterleri — `docs/V1_SUCCESS_CRITERIA.md`
- `1D` ✅ Non-goals — `docs/NON_GOALS.md`

## ✅ 2A — Bilgi birimleri

Ana çıktı: `docs/LEARNING_ENGINE_SPEC.md`

Kilitleyen yapı:

`Domain → Module → Topic → Skill → Learning Objective`

- `Domain → Module → Topic` curriculum organizasyon katmanıdır.
- `Skill → Learning Objective` gerçek learning/mastery katmanıdır.
- Canonical mastery'nin ana planner/prerequisite seviyesi Skill'dir.
- Topic/Module/Domain mastery derived edilir.
- Runtime prerequisite ana olarak Skill → Skill çalışır.
- Topic completion mastery değildir.

Kalıcı karar: D-021.

## ✅ 2B — Topic durumları

Ana çıktı: `docs/TOPIC_STATE_MACHINE.md`

Canonical state'ler:

- `locked`
- `available`
- `learning`
- `mastered`
- `weakening`
- `remediation_required`

Bağlayıcı davranış:

- Topic state mastery'nin kendisi değildir; prerequisite/coverage/Skill mastery/retention/remediation girdilerinden derived edilir.
- `locked` yalnız başlanmamış Topic'in hard prerequisite giriş kapısıdır.
- Başlanmış/mastered Topic prerequisite sonradan zayıfladı diye geriye dönük `locked` yapılmaz.
- `mastered`, coverage veya validated diagnostic waiver + required Skill mastery gate gerektirir.
- `weakening` retention riskini, `remediation_required` hedefli onarım gerektiren doğrulanmış Skill eksikliğini ifade eder.
- Remediation geçmiş coverage'ı sıfırlamaz ve bütün curriculum'u durdurmaz.
- Topic state tek başına ileri Topic kilidi değildir; canonical prerequisite yetkisi Skill mastery'dedir.
- State transition'lar deterministik ve reason/history ile açıklanabilir olmalıdır.

Kalıcı karar: D-023.

---

# 4. Şu Anda Bulunulan Kesin Adım

**AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla**

- `2A` ✅ Bilgi birimleri
- `2B` ✅ Topic durumları
- `2C` 🟡 **Mastery sinyalleri — AKTİF**
- `2D` ⬜ AI/ipucu etkisi
- `2E` ⬜ Mastery formülü v0
- `2F` ⬜ Unutma modeli

## Aktif iş: 2C

Teori, coding, debugging, explanation/Feynman, transfer, retention, proje ve süre gibi evidence türlerinin:

- neyi gerçekten kanıtladığı,
- hangi Learning Objective/Skill'e bağlandığı,
- güvenilirlik sınırları,
- tek başına neyi kanıtlayamayacağı,
- yanlış pozitif mastery'yi nasıl engelleyeceği

kesinleştirilecek.

2C'de henüz ağırlık yüzdeleri kilitlenmeyecek; sayısal formül 2E'ye aittir.

---

# 5. Hâlâ Açık Ana Konular

- mastery evidence modeli (`2C`)
- AI-help impact (`2D`)
- mastery formülü / threshold / confidence (`2E`)
- spaced repetition / decay (`2F`)
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
12. `docs/LEARNING_ENGINE_SPEC.md`
13. `docs/LEARNING_BEHAVIOR_RULES.md`
14. **`docs/TOPIC_STATE_MACHINE.md`**
15. `docs/MASTER_PLAN.md`
16. `docs/AI_AGENT_WORKFLOW.md`
17. `docs/PROGRESS_LOG.md`

---

# 7. Yeni Sohbetin Yapacağı İlk İş

Repo hafızasını okuduktan sonra doğrudan:

> **`2C — Mastery sinyalleri`**

adımından devam et.

Aşama 1, 2A, 2B ve `LEARNING_BEHAVIOR_RULES.md` içindeki bağlayıcı kararları kullanıcı açıkça değiştirmedikçe yeniden tartışmaya açma.

---

# 8. Sohbet Aktarım Mesajı

> `GitHub'daki xpike-dgm/ai-infra-learning-coach reposu önceki uzun sohbetimin kalıcı proje hafızasıdır. docs/START_HERE.md dosyasından başlayıp belirtilen sırayı oku. Önceki sohbetin devamı gibi davran. STEP_STATUS.md ve HANDOFF_STATE.md içindeki aktif adım kodundan devam et. Özellikle LEARNING_BEHAVIOR_RULES.md ve TOPIC_STATE_MACHINE.md içindeki bağlayıcı öğrenme/state kurallarını koru. Daha önce alınmış kararları yeniden sordurma. Araştırma/kodlama/test işlerini AI_AGENT_WORKFLOW.md protokolüne göre böl. Yeni kararları ve tamamlanan adımları GitHub'a kaydet.`
