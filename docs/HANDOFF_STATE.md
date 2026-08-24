# HANDOFF STATE — Güncel Proje Durumu ve Sohbet Aktarım Özeti

Bu dosya yeni bir ChatGPT sohbetine veya başka bir agent'a geçerken mevcut çalışma durumunu hızlıca devretmek için tutulur.

**Son güncelleme:** 2026-08-24

Repo: `xpike-dgm/ai-infra-learning-coach`

---

# 0. Zorunlu çalışma protokolü

Bağlayıcı kaynak: `docs/PROJECT_MEMORY_PROTOCOL.md` / D-024 / D-027.

> **Her numaralı proje adımı başlamadan GitHub PRE-STEP beyin tazelemesi yapılır; adım bittikten sonra gerekli canonical hafıza dosyaları ve MASTER_PLAN senkronize edilmeden adım tamamlanmış sayılmaz.**

Minimum PRE-STEP:

- `docs/HANDOFF_STATE.md`
- `docs/EXECUTION_INDEX.md`
- `docs/STEP_STATUS.md`
- `docs/DECISIONS.md`
- başlanacak adımla ilgili en güncel spec/davranış dosyaları

POST-STEP'te ana çıktı ile birlikte en az `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG` ve `MASTER_PLAN` kontrol edilir/güncellenir; yeni kalıcı karar varsa `DECISIONS.md` de güncellenir.

Aynı sohbet içinde bir sonraki numaralı adıma geçerken bile PRE-STEP refresh yeniden yapılır.

---

# 1. Ana ürün

> Sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering kariyer rotasında günlük olarak yöneten; yalnız kanıtlanmış öğrenmeyi ilerleme kabul eden; mastery, retention, assessment ve prerequisite sonuçlarına göre gelecekteki programı yeniden düzenleyen kişisel adaptif Android öğrenme koçu.

Ana ilke:

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Ana teknik yön:

**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

---

# 2. Kesinleşen büyük kurallar

- İngilizce A0'dan teknik eğitimle paralel ilerler.
- `Gün X / 1095`, streak ve task completion mastery değildir.
- Curriculum sabit takvim değil prerequisite ilişkili knowledge graph'tır.
- Canonical mastery/planner prerequisite seviyesi Skill'dir; evidence Learning Objective'e bağlanabilir.
- Eksik Skill yalnız bağımlı dalları bekletir; bağımsız dallar devam eder.
- Uygulama öğretir → uygulatır → ölçer → remediation/retest yapar.
- Yanlış cevap ceza değil evidence/remediation sinyalidir.
- Henüz öğretilmemiş prerequisite isteyen soru kullanıcıyı başarısız sayamaz.
- Aynı exact soru hemen tekrar edilerek ezber mastery sayılmaz.
- Mastered Skill'ler retention ile tekrar doğrulanabilir.
- AI yardımcı öğretmen/evaluator'dır; çekirdek mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
- AI yardımı yasak değildir; fakat assisted performans independent mastery evidence ile aynı değildir.
- Uygulama kişisel kullanım içindir; auth/payment/social/admin/multi-tenant SaaS varsayılan kapsam dışıdır.

---

# 3. Tamamlanan adımlar

## ✅ AŞAMA 1 — Ürün Çerçevesi

- `1A` ✅ Ana ürün amacı
- `1B` ✅ V1 kapsamı
- `1C` ✅ Başarı kriterleri
- `1D` ✅ Non-goals

## ✅ 2A — Bilgi birimleri

Ana çıktı: `docs/LEARNING_ENGINE_SPEC.md`  
Kalıcı karar: D-021.

Model:
`Domain → Module → Topic → Skill → Learning Objective`

## ✅ 2B — Topic state machine

Ana çıktı: `docs/TOPIC_STATE_MACHINE.md`  
Kalıcı karar: D-023.

State'ler:
`locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`.

## ✅ 2C — Mastery sinyalleri

Ana çıktı: `docs/MASTERY_SIGNALS_SPEC.md`  
Kalıcı karar: D-025.

Evidence türleri:
- concept recognition/recall,
- code reading,
- coding/production,
- debugging,
- explanation,
- transfer,
- retention,
- integrated project.

Time/completion/streak/self-confidence mastery değildir. Coding mastery gerçek kullanıcı artifact'ı gerektirir. Project completion bütün alt Skill'leri otomatik mastered yapmaz.

## ✅ 2D — AI / ipucu etkisi

Ana çıktı: `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`  
Kalıcı karar: D-026.

Bağlayıcı model:

- Hint seviyeleri: `H0 none`, `H1 orientation`, `H2 targeted conceptual hint`, `H3 partial solution/scaffold`, `H4 full solution/answer exposure`.
- Timing ayrı tutulur: `before_attempt`, `during_attempt`, `after_submit`, `after_failure`.
- Artifact origin: `user_authored`, `user_authored_with_assistance`, `mixed_authorship`, `generated_or_copied`, `unknown_provenance`.
- Evidence assistance yorumu: `independent_evidence`, `assisted_evidence`, `practice_only`, `requires_independent_recheck`.
- AI'nın tam kodu üretmesi ve kodun çalışması kullanıcı için direct coding mastery değildir.
- H3/H4 çözüm ifşası sonrası kritik Objective fresh/unseen bağımsız variant ile tekrar doğrulanır.
- Aynı çözümü hemen tekrar etmek güçlü evidence değildir.
- Submit sonrası AI feedback önceki attempt'i geriye dönük kirletmez.
- Explanation/comprehension production objective'inin yerine geçmez.
- Compiler/test/docs/autocomplete otomatik penalty değildir; objective-specific tool policy uygulanır.
- Hint istemek tek başına negative mastery değildir.
- External AI için surveillance/cheat-detection yapılmaz; provenance bilinmiyorsa bilinmiyor olarak tutulur.
- AI helper ve AI evaluator provenance ayrıdır.
- Exact sayısal assistance weight/penalty 2E'ye bırakıldı.

---

# 4. English foundation bağlayıcı kuralı

Ana çıktı: `docs/ENGLISH_FOUNDATION_RULES.md`.

A0 kullanıcıdan henüz öğretilmemiş grammar/function-word kullanarak serbest üretim beklenmez. Teknik assessment bilinmeyen English grammar'ı gizli prerequisite yapmaz.

---

# 5. Şu anda bulunulan kesin adım

**AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla**

- `2A` ✅ Bilgi birimleri
- `2B` ✅ Topic durumları
- `2C` ✅ Mastery sinyalleri
- `2D` ✅ AI/ipucu etkisi
- `2E` 🟡 **Mastery formülü v0 — AKTİF**
- `2F` ⬜ Unutma modeli

## Aktif iş: 2E

Kesinleştirilecek konular:

- Objective-level evidence aggregation,
- Objective → Skill mastery aggregation,
- evidence type / difficulty / novelty / independence etkisi,
- minimum direct + independent + diverse evidence gate'leri,
- H0–H4 assistance'ın formüldeki karşılığı,
- positive/negative/partial evidence etkisi,
- mastery threshold / prerequisite-ready gate,
- confidence kavramı,
- tek sınav / tek kolay quiz / aynı familya ile false-positive mastery önleme,
- explainable deterministic formula.

**2E için Research AI kullanılmalıdır.** Research çıktısı otomatik ürün kararı değildir; ana yönetici tarafından repo kararlarıyla birleştirilip Mastery Formula v0'a dönüştürülür.

2E başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP refresh zorunludur.

---

# 6. Açık ana konular

- mastery formula / threshold / confidence (`2E`)
- spaced repetition / decay (`2F`)
- adaptive planner
- assessment composition
- curriculum graph
- English mastery
- UX/wireframe
- mobil teknoloji seçimi
- local database
- AI provider architecture

---

# 7. İlk okuma sırası

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
17. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
18. `docs/ENGLISH_FOUNDATION_RULES.md`
19. `docs/MASTER_PLAN.md`
20. `docs/AI_AGENT_WORKFLOW.md`
21. `docs/PROGRESS_LOG.md`

---

# 8. Yeni sohbetin yapacağı ilk iş

Repo hafızasını okuduktan sonra aktif adımı doğrula ve:

> **`2E — Mastery formülü v0`**

adımından devam et.

Önce PRE-STEP refresh yap; ardından Research AI için mastery/learning-science araştırma görevi hazırla. Önceki 2A–2D kararlarını kullanıcı açıkça değiştirmedikçe yeniden tartışmaya açma.
