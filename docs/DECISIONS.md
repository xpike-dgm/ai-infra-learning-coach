# Product Decisions Log

Bu dosya kalıcı ürün kararlarını kaydeder. Ayrıntılı teknik davranış ilgili canonical spec dosyalarındadır.

## D-001 — 3 yıllık gün sayacı gösterilmeyecek
**Durum:** Kabul edildi  
`Gün X / 1095` ana ilerleme metriği değildir.

## D-002 — İlerleme mastery tabanlı olacak
**Durum:** Kabul edildi  
Ders/task completion tek başına öğrenme değildir.

## D-003 — Knowledge graph takvimden öncelikli
**Durum:** Kabul edildi  
Takvim kapasiteyi, prerequisite/mastery konu uygunluğunu belirler.

## D-004 — Eksik konu tüm programı dondurmaz
**Durum:** Kabul edildi  
Yalnız bağımlı dallar bekler.

## D-005 — Haftalık/aylık sınavlar programı değiştirecek
**Durum:** Kabul edildi

## D-006 — İngilizce paralel ilerleyecek
**Durum:** Kabul edildi

## D-007 — Ana kariyer rotası systems → GPU → AI infrastructure
**Durum:** Kabul edildi  
C → Linux → C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure.

## D-008 — Uygulama kişisel kullanım için
**Durum:** Kabul edildi  
Auth/payment/social/admin/multi-tenant SaaS varsayılan kapsam dışıdır.

## D-009 — Modern ve sade mobil UI
**Durum:** Kabul edildi  
Ana ekran `Bugün ne yapmalıyım?` sorusunu cevaplar.

## D-010 — Streak ana başarı metriği olmayacak
**Durum:** Kabul edildi

## D-011 — AI kullanımı yasaklanmayacak
**Durum:** Kabul edildi  
AI yardımı sonrası gerektiğinde independent comprehension/transfer/production doğrulaması gerekir.

## D-012 — 3 yıllık curriculum V1 ön koşulu değil
**Durum:** Kabul edildi  
İlk 8–12 haftalık production-quality paket + öğrenme motoru önce gelir.

## D-013 — Süreler adaptif, konu bağımlılıkları daha kalıcı
**Durum:** Kabul edildi

## D-014 — İlk iş doğrudan CUDA olmak zorunda değil
**Durum:** Kabul edildi  
C++ Systems / Systems Software / Linux Infrastructure / Distributed Systems / Performance / uygun SRE-Cloud rolleri köprü olabilir.

## D-015 — Geliştirme master plan üzerinden aşamalı yürütülecek
**Durum:** Kabul edildi — 2026-08-24

## D-016 — Araştırma, kodlama ve test ayrı AI rolleridir
**Durum:** Kabul edildi — 2026-08-24  
Research AI dış araştırma; Coding AI implementasyon; Test/QA AI bağımsız doğrulama; ana yönetici karar/spec/GitHub hafızasıdır. Ayrıntı: `docs/AI_AGENT_WORKFLOW.md`.

## D-017 — Sabit `1A / 1B / ...` adım kodları kullanılacak
**Durum:** Kabul edildi — 2026-08-24  
Canonical indeks: `docs/EXECUTION_INDEX.md`.

## D-018 — V1 adaptif öğrenme döngüsünü gerçek Android release olarak çalıştıracak
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/V1_SCOPE.md`.

## D-019 — V1 release acceptance kriterleri ve bağımsız QA'ya bağlı
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/V1_SUCCESS_CRITERIA.md`.

## D-020 — Ürün/V1 non-goals kilitlendi
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/NON_GOALS.md`.

## D-021 — Öğrenme birimleri organizasyon ve mastery katmanı olarak ayrılacak
**Durum:** Kabul edildi — 2026-08-24  
`Domain → Module → Topic → Skill → Learning Objective`; canonical mastery/prerequisite ana seviyesi Skill. Ayrıntı: `docs/LEARNING_ENGINE_SPEC.md`.

## D-022 — Öğretme, yanlış cevap, remediation, retention ve replan davranışları bağlayıcıdır
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/LEARNING_BEHAVIOR_RULES.md`.

## D-023 — Topic state Skill verilerinden derived, explainable work state olacak
**Durum:** Kabul edildi — 2026-08-24  
`locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`. Ayrıntı: `docs/TOPIC_STATE_MACHINE.md`.

## D-024 — Her numaralı adım öncesi/sonrası GitHub beyin tazelemesi zorunlu
**Durum:** Kabul edildi — 2026-08-24  
Akış: `PRE-STEP GitHub refresh → çalışma → gerekirse Research/Coding/QA → POST-STEP GitHub sync`. Ayrıntı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

## D-025 — Mastery çok kaynaklı ve Objective'e uygun evidence ile kanıtlanacak
**Durum:** Kabul edildi — 2026-08-24  
Recognition, recall, code reading, coding, debugging, explanation, transfer, retention, project ayrıdır; time/completion/streak/self-confidence mastery değildir. Coding mastery gerçek user artifact ister. Ayrıntı: `docs/MASTERY_SIGNALS_SPEC.md`.

## D-026 — AI/ipucu yardımı öğrenmeyi destekler fakat independent mastery ile eşit değildir
**Durum:** Kabul edildi — 2026-08-24  
H0–H4, timing, artifact provenance ve fresh recheck davranışı. Ayrıntı: `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`.

## D-027 — MASTER_PLAN canonical yürütme durumuyla senkron tutulacak
**Durum:** Kabul edildi — 2026-08-24

## D-028 — Mobil performans/akıcılık first-class requirement
**Durum:** Kabul edildi — 2026-08-24  
UI thread ağır mastery/planner/DB/network/AI/code-execution ile bloke edilmez; incremental/local-first ve gerçek cihaz performance QA uygulanır.

## D-029 — İlk Beta-style Mastery Formula candidate'ı
**Durum:** **YERİNE D-031 GEÇTİ — 2026-08-24**

İlk candidate:
```text
alpha = 1 + Σ(wq)
beta  = 1 + Σ(w(1-q))
score = alpha/(alpha+beta)
```
ve direct/corroborating, H0–H4, AI-evaluator numeric multiplier'ları önerilmişti. Bu model ayrı Research AI doğrulamasından sonra finalden çıkarıldı. Tarihsel candidate ayrıntısı Git history ve `docs/2E_RESEARCH_VALIDATION.md` içinde açıklanır.

## D-030 — 2E ayrı Research AI raporu alınmadan kapatılamaz
**Durum:** UYGULANDI / TAMAMLANDI — 2026-08-24  
Kullanıcı bağımsız Research AI raporunu sağladı; ana yönetici raporu otomatik kabul etmeyip mevcut 2A–2D kararları ve seçili akademik kaynaklarla değerlendirdi.

## D-031 — Final Mastery Formula v0 = Gated Recent Evidence (GRE-v0)
**Durum:** Kabul edildi — 2026-08-24

- Mastery score'a yalnız eligible, prerequisite-valid, H0, direct/primary, verified, bağımsız evidence group girer.
- H1–H4 positive independent mastery score'a girmez.
- Same/near items dependency/testlet grouping ile kontrol edilir.
- Objective score son en fazla 5 eligible H0 direct group'un ortalaması; threshold 0.80, window 5 heuristic/calibration değeridir.
- Required/critical Objective hard gates; critical coding H0 user-authored artifact, debugging H0 diagnosis/fix ister.
- İlk clean post-mastery failure instant reset değil `verification_due` üretir.
- Difficulty multiplier değildir; fixed AI-evaluator trust multiplier yoktur.
- Bounded/incremental implementation D-028'e uygundur.

Ayrıntı: `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md`.

## D-032 — Final Retention/Forgetting modeli = RVR-v0
**Durum:** Kabul edildi — 2026-08-24

`RVR-v0 — Retention Verification & Risk` 2F final modelidir.

- GRE-v0 mastery ve retention scheduling ayrı eksenlerdir.
- Zaman geçişi GRE/mastery score'u otomatik düşürmez; `review_due` üretir.
- Retention states: `untracked`, `fresh`, `stable`, `review_due`, `verification_due`, `at_risk`.
- `review_due` unutma değildir; Topic'i otomatik `weakening` yapmaz ve prerequisite'i tek başına hard-block etmez.
- Retention verification target Skill'e uygun active H0 evidence ister; coding/debugging/transfer flashcard ile ikame edilmez.
- İlk clean delayed failure → `verification_due`; fresh/unseen recheck. Recheck failure sonrası yeni evidence GRE-v0'a girer ve gates doğal yeniden hesaplanır; score elle `0.50` gibi değere atanmaz.
- Natural reuse ancak target Skill structurally essential + H0 + separately verified + context-diverse ise planned retention evidence sayılabilir.
- Global project/task success bütün component Skills'i refresh etmez; automatic cluster/descendant refresh yoktur.
- Critical prerequisite `review_due` iken hard lock yok; actual negative evidence nedeniyle `verification_due` varsa unresolved critical recheck boyunca dependent yeni work bekleyebilir.
- Long absence sonrası backlog dump yok; representative/integrated verification yalnız ayrı Skill attribution mümkünse kullanılır. Daily capacity 3A–3F'de belirlenir.
- V0 interval defaults (`2/4/7 gün`, critical cap 3, growth `2.0/1.6`, max `180/90`) ve 1 günlük verification separation bilimsel sabit değil versioned engineering heuristic + pilot calibration ayarıdır.
- Model bounded/incremental/local uygulanabilir; ağır history scan veya population-trained model V1 şartı değildir.

Ayrıntı: `docs/RETENTION_FORGETTING_SPEC.md`, `docs/2F_RESEARCH_VALIDATION.md`.

## D-033 — Günlük kapasite planner için hard zaman bütçesidir; ihtiyaç oluştu diye gün otomatik uzamaz
**Durum:** Kabul edildi — 2026-08-24

3A final capacity contract:

- Günlük `available_minutes` planner'ın hard envelope'udur; explicit today override en yüksek önceliğe sahiptir.
- Short/normal/intensive yalnız editable UI presetleridir. V0 önerileri `30/60/90 dk`; bilimsel optimum değildir.
- Planner süre tahmin hatası için V0'da `10%` reserve bırakır; bu da versioned engineering heuristic'tir.
- `minimum_plannable_block_minutes_v0 = 10`; daha az süre failure/ceza değildir, yalnız uygun micro-task varsa planlanır.
- Capacity sabit kategori yüzdelerine bölünmez; new learning / remediation / retention / English payı gerçek state ve 3C priority ile belirlenir.
- Yeni remediation veya retention işi mevcut günün üstüne eklenip süreyi otomatik büyütmez; remaining capacity yeniden paketlenir.
- Kullanıcı session sırasında süreyi azaltır/artırırsa yalnız kalan plan current state'ten replan edilir; completed evidence korunur.
- Başlanmamış veya yarım bırakılmış task otomatik negative mastery evidence değildir.
- Task sığmıyorsa pedagogically safe split → smaller eligible alternative → defer sırası uygulanır; critical task bile kullanıcı izni olmadan hard budget'ı aşmaz.
- Deferred işler ertesi güne “borç kuyruğu” olarak taşınmaz; current-state candidate generation yapılır.
- Task duration metadata ve gelecekte user pace adaptation desteklenir; wall-clock ile active-learning duration ayrılabilir.
- Capacity resolution ve output deterministic/versioned olmalı; LLM günlük süreyi keyfi değiştiremez.

Ayrıntı: `docs/ADAPTIVE_PLANNER_SPEC.md` — 3A bölümü.
