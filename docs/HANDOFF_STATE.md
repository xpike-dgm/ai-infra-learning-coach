# HANDOFF STATE — Güncel Proje Durumu ve Sohbet Aktarım Özeti

Bu dosya yeni bir ChatGPT sohbetine veya başka bir agent'a geçerken mevcut çalışma durumunu hızlıca devretmek için tutulur.

**Son güncelleme:** 2026-08-24

Repo: `xpike-dgm/ai-infra-learning-coach`

---

# 0. Zorunlu çalışma protokolü

Bağlayıcı kaynak: `docs/PROJECT_MEMORY_PROTOCOL.md` / D-024.

> **Her numaralı proje adımı başlamadan GitHub PRE-STEP beyin tazelemesi yapılır; adım bittikten sonra gerekli canonical hafıza dosyaları güncellenmeden adım tamamlanmış sayılmaz.**

Minimum PRE-STEP:

- `docs/HANDOFF_STATE.md`
- `docs/EXECUTION_INDEX.md`
- `docs/STEP_STATUS.md`
- `docs/DECISIONS.md`
- başlanacak adımla ilgili en güncel spec/davranış dosyaları

POST-STEP'te ana çıktı ile birlikte `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG` kontrol edilir/güncellenir; yeni kalıcı karar varsa `DECISIONS.md` de güncellenir.

Aynı sohbet içinde bir sonraki numaralı adıma geçerken bile PRE-STEP refresh yeniden yapılır.

---

# 1. Ana ürün

> Sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering kariyer rotasında günlük olarak yöneten; her gün ne çalışacağını mevcut bilgi durumuna göre belirleyen; yalnız kanıtlanmış öğrenmeyi ilerleme kabul eden; mastery, retention, assessment ve prerequisite sonuçlarına göre gelecekteki programı yeniden düzenleyen kişisel adaptif Android öğrenme koçu.

Ana ilke:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Ana teknik yön:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

---

# 2. Kesinleşen büyük kurallar

- İngilizce A0'dan teknik eğitimle paralel ilerler.
- `Gün X / 1095`, streak ve task completion mastery değildir.
- Curriculum sabit takvim değil prerequisite ilişkili knowledge graph'tır.
- Eksik konu yalnız bağımlı dalları bekletir; bağımsız dallar devam eder.
- Planner mastery, retention, assessment, prerequisite ve günlük kapasiteye göre plan üretir.
- Günlük mikro assessment + haftalık + aylık sınav gelecekteki programı değiştirir.
- Uygulama yalnız test etmez; öğretir → uygulatır → ölçer → remediation/retest yapar.
- AI yardımı mümkündür; fakat AI ile tamamlanan iş gerçek kullanıcı anlayışı yerine geçmez.
- Çekirdek mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
- Kritik prerequisite süre dolduğu için atlanmaz; yöntem değişir.
- Remediation günlük kapasitenin üstüne kontrolsüz eklenmez; planner daha düşük öncelikli işi kaydırır.
- Henüz öğretilmemiş prerequisite isteyen soru kullanıcıyı başarısız sayamaz.
- Yanlış yapılan exact soru hemen tekrar edilerek ezber ödüllendirilmez; aynı Skill farklı varyasyon/bağlamla yeniden ölçülür.
- Mastered Skill'ler haftalar/aylar sonra retention ile tekrar doğrulanabilir.
- Bilgi havuzu doğrulanmış çekirdek; soru havuzu doğrulanmış çekirdek + question family/variants + kontrollü AI üretimi olarak tasarlanır.
- Uygulama kişisel kullanım içindir; auth/payment/social/admin/multi-tenant SaaS varsayılan kapsam dışıdır.
- Kritik geliştirme akışı: **GitHub PRE-STEP refresh → Yönetici → gerekirse Araştırma AI → Spec → Kodlama AI → bağımsız Test/QA AI → GitHub POST-STEP sync**.

---

# 3. Tamamlanan Aşama 1

## ✅ AŞAMA 1 — Ürün Çerçevesini Kilitle

- `1A` ✅ Ana ürün amacı — `docs/PRODUCT_REQUIREMENTS.md`
- `1B` ✅ V1 kapsamı — `docs/V1_SCOPE.md`
- `1C` ✅ Başarı kriterleri — `docs/V1_SUCCESS_CRITERIA.md`
- `1D` ✅ Non-goals — `docs/NON_GOALS.md`

---

# 4. Aşama 2 tamamlanan adımlar

## ✅ 2A — Bilgi birimleri

Ana çıktı: `docs/LEARNING_ENGINE_SPEC.md`

Kilitleyen model:

`Domain → Module → Topic → Skill → Learning Objective`

- `Domain → Module → Topic` curriculum organizasyon katmanıdır.
- `Skill → Learning Objective` gerçek learning/mastery katmanıdır.
- Canonical mastery'nin ana planner/prerequisite seviyesi Skill'dir.
- Topic/Module/Domain mastery Skill verilerinden derived edilir.
- Runtime prerequisite ana olarak Skill → Skill çalışır.
- Task completion mastery değildir.

Kalıcı karar: D-021.

## ✅ 2B — Topic durumları

Ana çıktı: `docs/TOPIC_STATE_MACHINE.md`

Canonical Topic state'leri:

- `locked`
- `available`
- `learning`
- `mastered`
- `weakening`
- `remediation_required`

Önemli davranış:

- Topic state mastery'nin kendisi değildir; prerequisite/coverage/Skill mastery/retention/remediation verilerinden derived edilir.
- `locked` esas olarak başlanmamış Topic'in hard-prerequisite giriş kapısıdır.
- Başlanmış/mastered Topic prerequisite sonradan zayıfladı diye geriye dönük `locked` yapılmaz.
- `mastered` coverage/validated waiver + required Skill mastery gate gerektirir.
- `weakening` retention riskini, `remediation_required` hedefli onarımı ifade eder.
- Topic state tek başına ileri Topic kilidi değildir; canonical prerequisite Skill mastery'dedir.

Kalıcı karar: D-023.

## ✅ 2C — Mastery sinyalleri

Ana çıktı: `docs/MASTERY_SIGNALS_SPEC.md`

Kalıcı karar: D-025.

Ana evidence modeli:

- Evidence atomik olarak Learning Objective'e, oradan canonical Skill'e bağlanır.
- Evidence rolleri: `direct/primary`, `corroborating`, `contextual`.
- Ana evidence türleri:
  - concept recognition,
  - concept recall / short open response,
  - code reading / output prediction,
  - coding / production,
  - debugging / diagnosis,
  - explanation / justification,
  - transfer / novel application,
  - retention / delayed retrieval,
  - integrated project/task.
- Coding evidence gerçek kullanıcı kod artifact'ı gerektirir; doğru kod seçeneğini işaretlemek coding evidence değildir.
- Transfer yalnız daha önce öğrenilmiş prerequisite'lerle geçerli evidence sayılır.
- Retention immediate başarıdan ayrıdır; ileri Topic'teki doğal yeniden kullanım da evidence olabilir.
- Project completion içindeki tüm Skill'leri otomatik mastered yapmaz; evidence objective bazında ayrıştırılır.
- Time, lesson/task completion, streak ve self-confidence mastery değildir; contextual sinyaldir.
- Aynı exact soru veya çok yakın familya tekrarları bağımsız evidence gibi mastery'yi şişiremez.
- Hatalı/ambiguous item, bilinmeyen prerequisite, evaluator/system problemi veya answer leakage evidence'ı invalid yapabilir.
- Assistance context evidence ile saklanır; exact AI/hint etkisi 2D'ye bırakıldı.
- Exact weight, threshold, minimum evidence/çeşitlilik ve confidence 2E'ye bırakıldı.

2C için kısa research doğrulaması retrieval practice, delayed retention ve transfer literatürüyle yapıldı; bu araştırma sayısal weight/threshold belirlemek için kullanılmadı.

---

# 5. English foundation bağlayıcı kuralı

Ana çıktı: `docs/ENGLISH_FOUNDATION_RULES.md`

- A0 kullanıcıdan henüz öğretilmemiş `a/an`, `the`, `to`, temel cümle dizilimi, `be`, pronoun, preposition vb. yapılarını kullanarak serbest İngilizce üretmesi beklenmez.
- English progression varsayılan olarak `recognition → controlled production → free production → technical use → transfer/retention` yönündedir.
- Teknik task C/Linux bilgisini ölçüyorsa bilinmeyen English grammar gizli prerequisite olamaz; gerekirse Türkçe/bilingual scaffold sağlanır.
- English grammar/vocabulary kendi prerequisite graph'ına sahip olacaktır.
- Kesin grammar sırası ve CEFR hedefleri 6A–6E'de tasarlanacaktır.

---

# 6. Şu anda bulunulan kesin adım

**AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla**

- `2A` ✅ Bilgi birimleri
- `2B` ✅ Topic durumları
- `2C` ✅ Mastery sinyalleri
- `2D` 🟡 **AI/ipucu etkisi — AKTİF**
- `2E` ⬜ Mastery formülü v0
- `2F` ⬜ Unutma modeli

## Aktif iş: 2D

Kesinleştirilecek konular:

- hint level taxonomy,
- kullanıcı kendi çözmeden önce/sonra alınan yardım,
- AI explanation vs AI-generated answer/code,
- copy/paste ve assisted coding,
- yardım sonrası comprehension/transfer recheck,
- assisted evidence'ın güvenilirlik rolü,
- hangi yardım türünün mastery evidence'ını kirletip hangisinin öğrenme scaffold'u sayılacağı.

2D'de mümkün olduğunca exact mastery yüzdeleri kilitlenmeyecek; sayısal aggregation 2E'ye bırakılacak.

**2D tasarımına başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP GitHub refresh zorunludur.**

---

# 7. Hâlâ açık ana konular

- AI/hint impact (`2D`)
- mastery formula / threshold / confidence (`2E`)
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

# 8. İlk okuma sırası

1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/PROJECT_MASTER_CONTEXT.md`
4. `docs/HANDOFF_STATE.md`
5. `docs/EXECUTION_INDEX.md`
6. `docs/STEP_STATUS.md`
7. `PROJECT_CONTEXT.md`
8. `docs/DECISIONS.md`
9. `docs/PRODUCT_REQUIREMENTS.md`
10. `docs/V1_SCOPE.md`
11. `docs/V1_SUCCESS_CRITERIA.md`
12. `docs/NON_GOALS.md`
13. `docs/LEARNING_ENGINE_SPEC.md`
14. `docs/LEARNING_BEHAVIOR_RULES.md`
15. `docs/TOPIC_STATE_MACHINE.md`
16. `docs/MASTERY_SIGNALS_SPEC.md`
17. `docs/ENGLISH_FOUNDATION_RULES.md`
18. `docs/MASTER_PLAN.md`
19. `docs/AI_AGENT_WORKFLOW.md`
20. `docs/PROGRESS_LOG.md`

---

# 9. Yeni sohbetin yapacağı ilk iş

Repo hafızasını okuduktan sonra aktif adımı doğrula ve doğrudan:

> **`2D — AI / ipucu etkisi`**

adımından devam et.

Ancak tasarıma başlamadan D-024 / `PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh yap.

Aşama 1, 2A, 2B, 2C, `LEARNING_BEHAVIOR_RULES.md` ve `ENGLISH_FOUNDATION_RULES.md` içindeki bağlayıcı kararları kullanıcı açıkça değiştirmedikçe yeniden tartışmaya açma.

---

# 10. Sohbet aktarım mesajı

> `GitHub'daki xpike-dgm/ai-infra-learning-coach reposu önceki uzun sohbetimin kalıcı proje hafızasıdır. docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md dosyalarından başlayarak belirtilen sırayı oku. Önceki sohbetin devamı gibi davran. Her numaralı adım başlamadan PRE-STEP GitHub beyin tazelemesi, bittikten sonra POST-STEP GitHub sync yap. STEP_STATUS.md, HANDOFF_STATE.md ve EXECUTION_INDEX.md içindeki aktif adım kodundan devam et. Özellikle LEARNING_BEHAVIOR_RULES.md, TOPIC_STATE_MACHINE.md, MASTERY_SIGNALS_SPEC.md ve ENGLISH_FOUNDATION_RULES.md içindeki bağlayıcı kuralları koru. Daha önce alınmış kararları yeniden sordurma. Araştırma/kodlama/test işlerini AI_AGENT_WORKFLOW.md protokolüne göre böl. Yeni kararları ve tamamlanan adımları GitHub'a kaydet.`