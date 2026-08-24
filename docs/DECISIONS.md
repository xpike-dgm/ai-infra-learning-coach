# Product Decisions Log

Bu dosya kalıcı ürün kararlarını kaydeder. Ayrıntılı teknik davranış ilgili canonical spec dosyalarındadır.

## D-001 — 3 yıllık gün sayacı gösterilmeyecek
**Durum:** Kabul edildi / süre ufku D-041 ile genişletildi  
`Gün X / 1095` ana ilerleme metriği değildir. D-041 sonrası horizon 4+ yıla açılmıştır; no-countdown ilkesi aynen korunur.

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
**Durum:** Kabul edildi / D-042 ile Python foundation eklendi  
Computer/Programming Foundations → Python + C → Linux/Tooling → Modern C++ → DS&A → Computer Architecture → OS/Memory → Concurrency/Parallelism → Networking → Distributed Systems/Storage → Cloud/Observability → Performance → GPU Architecture → CUDA → Triton → ML/Transformer fundamentals → LLM Inference → Multi-GPU → AI Infrastructure.

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
**Durum:** İlke korunuyor / kapsam D-041 ile genişletildi  
İlk 8–12 haftalık production-quality paket + öğrenme motoru önce gelir. D-041 sonrası tam **4+ yıllık professional curriculum** da V1 ön koşulu değildir.

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
Canonical indeks: `docs/EXECUTION_INDEX.md`. D-043 mevcut kodları renumber etmeden AŞAMA 20'yi sona ekler.

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
ve direct/corroborating, H0–H4, AI-evaluator numeric multiplier'ları önerilmişti. Bu model ayrı Research AI doğrulamasından sonra finalden çıkarıldı.

## D-030 — 2E ayrı Research AI raporu alınmadan kapatılamaz
**Durum:** UYGULANDI / TAMAMLANDI — 2026-08-24

## D-031 — Final Mastery Formula v0 = Gated Recent Evidence (GRE-v0)
**Durum:** Kabul edildi — 2026-08-24
- Yalnız eligible, prerequisite-valid, H0, direct/primary, verified, independent evidence groups score'a girer.
- H1–H4 independent positive mastery değildir.
- Same/near item dependency/testlet grouping uygulanır.
- Required/critical Objective hard gates vardır.
- İlk clean post-mastery failure instant reset değil `verification_due` üretir.
- Bounded/incremental uygulanır.

Ayrıntı: `docs/MASTERY_FORMULA_V0.md`, `docs/2E_RESEARCH_VALIDATION.md`.

## D-032 — Final Retention/Forgetting modeli = RVR-v0
**Durum:** Kabul edildi — 2026-08-24
- Mastery ve retention ayrı eksenlerdir.
- Zaman GRE/mastery score'u otomatik düşürmez; `review_due` üretir.
- `review_due` forgetting/hard lock değildir.
- First clean delayed failure → `verification_due`; recheck sonrası GRE doğal yeniden hesaplanır.
- Natural reuse strict attribution ile planned retention evidence olabilir.
- Critical unresolved verification dependent new work'u bekletebilir.
- Missed-day backlog dump yoktur.

Ayrıntı: `docs/RETENTION_FORGETTING_SPEC.md`, `docs/2F_RESEARCH_VALIDATION.md`.

## D-033 — Günlük kapasite planner için hard zaman bütçesidir; ihtiyaç oluştu diye gün otomatik uzamaz
**Durum:** Kabul edildi — 2026-08-24
- Explicit günlük süre hard envelope'dur.
- Fixed kategori yüzdesi yoktur.
- Remediation/retention day length'i otomatik büyütmez.
- Safe split → smaller alternative → defer.
- Deferred task ertesi gün borç değildir.
- Capacity deterministic/versioned'dır.

Ayrıntı: `docs/ADAPTIVE_PLANNER_SPEC.md`.

## D-034 — Planner'da kalıcı olan eski task değil açık LearningNeed'dir; task purpose/activity/track/evidence ayrı eksenlerdir
**Durum:** Kabul edildi — 2026-08-24
- Canonical akış `state → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`.
- Deferred TaskCandidate debt değildir; unresolved LearningNeed fresh candidate üretebilir.
- Purpose/activity/track/evidence ayrı eksenlerdir.
- Multi-Skill component evidence ayrı attribution ister.
- Provenance/validation + prerequisite + duration metadata TaskCandidate contract'ındadır.

Ayrıntı: `docs/TASK_TAXONOMY_SPEC.md`.

## D-035 — Planner priority modeli PBR-v0: semantic priority bands + deterministic rank vector
**Durum:** Kabul edildi — 2026-08-24
- Eligibility/trust priority'den önce gelir.
- P0 integrity blocker, P1 repair/verify, P2 maintain/continue, P3 planned progress, P4 reinforce/optimize.
- `review_due` negative evidence/P0 değildir.
- Same-band sorting lexicographic rank vector ile yapılır.
- Starvation guard ve track balance vardır.
- Duration semantic priority'den sonra gelir.
- Priority prerequisite'i bypass edemez.

Ayrıntı: `docs/PRIORITY_POLICY_SPEC.md`.

## D-036 — Final prerequisite modeli = PRG-v0 Prerequisite Readiness Gate
**Durum:** Kabul edildi — 2026-08-24
- Runtime prerequisite canonical olarak `Skill → Skill` düzeyindedir.
- Edge semantics `hard | soft` olarak ayrılır.
- Prerequisite readiness: `ready | ready_due | uncertain | not_ready`.
- `review_due = ready_due`; hard lock üretmez.
- Hard `not_ready` dependent candidate'ı bloke eder.
- Critical/strict `verification_due` dependent yeni work'u bekletebilir.
- Task-level `required_skill_ids` exact task eligibility için hard requirement'tır.
- Priority prerequisite gate'i bypass edemez.
- Yalnız affected dependent branch bekler; bağımsız branches devam eder.
- Prerequisite contamination target negative evidence değildir.
- Technical English gerçek dependency değilse global technical hard blocker değildir.

Ayrıntı: `docs/PREREQUISITE_POLICY_SPEC.md`.

## D-037 — Final hızlı öğrenme modeli = VDW-v0 Validated Diagnostic Waiver
**Durum:** Kabul edildi — 2026-08-24
- Diagnostic GRE-v0'dan daha kolay ikinci mastery standardı değildir.
- Self-report yalnız diagnostic trigger/scope'tur.
- Objective-level validated coverage waiver + partial waiver vardır.
- Critical H0/evidence/prerequisite false-skip guards korunur.
- Diagnostic fail otomatik remediation değildir.
- Waiver mastery/retention state değildir.

Ayrıntı: `docs/DIAGNOSTIC_WAIVER_SPEC.md`.

## D-038 — Final missed-day / re-entry modeli = SRR-v0 State-based Re-entry & Recovery
**Durum:** Kabul edildi — 2026-08-24
- Absence failure, mastery decay, remediation trigger veya task debt değildir.
- Stale unstarted plan/candidate replay edilmez; current state'ten fresh LearningNeed/candidate üretilir.
- Time yalnız RVR due/urgency sinyalini değiştirebilir.
- Unresolved verification/remediation korunur.
- Due inventory DailyPlan değildir.
- 1/7/30/60+ gün için ayrı pedagojik threshold yoktur.
- Starvation ile absence ayrıdır.
- Recovery PBR + PRG + hard capacity ile çalışır.
- Integrated recovery evidence ayrı attribution ister.

Ayrıntı: `docs/MISSED_DAY_RECOVERY_SPEC.md`.

## D-039 — Final planner explainability modeli = PDT-v0 Planner Decision Trace
**Durum:** Kabul edildi — 2026-08-24
- Planner açıklaması sonradan uydurulmaz; karar sırasında structured reason code + decision trace üretilir.
- Private chain-of-thought değil canonical state refs, policy sonuçları, disposition ve decisive reason code'lar tutulur.
- User-facing factual nedenler internal trace'in alt kümesidir.
- Need-level ve candidate-level izler ayrıdır.
- PRG eligibility → PBR priority → capacity fit sırası korunur.
- `review_due` forgetting/failure; absence debt/failure/starvation diye açıklanamaz.
- Replan versioned event üretir ve completed evidence'ı korur.
- LLM yalnız trace'i paraphrase edebilir; source of truth değildir.

Ayrıntı: `docs/PLANNER_EXPLAINABILITY_SPEC.md`.

## D-040 — Final günlük mikro değerlendirme modeli = DMA-v0 Daily Micro Assessment
**Durum:** Kabul edildi — 2026-08-24
- Daily micro assessment zorunlu günlük quiz/kota değildir; yalnız state'te gerçek measurement ihtiyacı varsa planner candidate'ı olur.
- Fixed soru sayısı, fixed assessment süresi veya günlük yüzde yoktur; assessment 3A hard capacity + PBR priority içinde yaşar.
- `practice`, `assess`, `retain`, `diagnose` purpose'ları canonical olarak ayrıdır; soru-benzeri UI purpose'ı belirlemez.
- Assessment mevcut LearningNeed/evidence gap bağlamından türetilir; ayrı assessment backlog/debt yoktur.
- Target Objective coverage ve PRG prerequisite açısından adil olmalıdır; undeclared/unknown prerequisite target negative evidence üretemez.
- Mastery/verification iddiası için varsayılan H0 independent attempt'tir. H1–H4 yardım öğrenmeye izin verir fakat positive independent mastery evidence değildir; yardım istemek negative H0 evidence sayılmaz.
- Submit sonrası feedback önceki H0 attempt'i geriye dönük contaminate etmez; solution exposure sonrası fresh/unseen recheck gerekir.
- Invalid/ambiguous/prerequisite-contaminated/evaluator-invalid item mastery credit veya penalty üretemez.
- Provisional evaluator critical mastery/remediation kararını tek başına belirleyemez.
- Tek doğru micro item automatic mastery değildir; tek clean post-mastery failure instant unmastery değildir, verification hysteresis korunur.
- Coding/debugging/transfer Objective evidence standardı düşük capacity nedeniyle recognition/MCQ'ya düşürülemez.
- Multi-Skill assessment yalnız separately observable/attributable component'lere evidence verir.
- Assessment sonucu `Attempt/Artifact → EvidenceEvent → GRE/RVR → weakness/verification/remediation → PRG/Topic → remaining-plan replan` zincirini kullanır; yeni remediation günü otomatik uzatmaz.
- Technical assessment'ta bilinmeyen English grammar/vocabulary gizli prerequisite olamaz.
- 4B–4E için minimum assessment item/result contract ve canonical `assessment.*` reason-code namespace'i tanımlanmıştır.

Ayrıntı: `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`.

## D-041 — Uzun vadeli ürün hedefi 4+ yıllık professional-readiness curriculum'a genişletildi
**Durum:** Kabul edildi — 2026-08-24

- Nihai öğrenme rotası artık yaklaşık üç yıllık horizon ile sınırlı değildir; gereken derinlik için **4+ yıl veya daha uzun** sürebilir.
- `4+ yıl` takvimsel mezuniyet, garanti süre veya progress metriği değildir; mastery/evidence yine canonical gate'tir.
- Final ürün hedefi yalnız course completion değil, **AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability** oluşturmaktır.
- Professional readiness; C/C++/Linux, OS/memory, concurrency, networking, distributed systems, performance, GPU/CUDA/Triton, LLM inference, serving ve AI Infrastructure alanlarında required capability'lerin kanıtlanmasını ister.
- Öğretim derinliği `concept → guided → independent → debugging → explanation → transfer → retention → integrated project → performance/production context` yönünde genişletilir.
- Final readiness yalnız küçük quizlerle verilemez; integrated systems, debugging, performance ve professional capstone evidence gerekir.
- V1 release bu genişleme nedeniyle 4+ yıl beklemez; ilk 8–12 haftalık production curriculum + gerçek learning engine ile daha erken release edilir.
- Aşama 5 curriculum graph yeni uzun horizon'ı destekleyecek extensible yapı kurar; Aşama 14 ilk production package'tır; Aşama 19 full professional curriculum expansion + open source + career readiness katmanıdır.
- Product teknik yeterliliği geliştirebilir fakat job offer, maaş, seniority veya üniversite/HR filtrelerini garanti edemez; gerçek ekip/production deneyimi ayrıca oluşur.
- Güçlü GitHub projects, reproducible benchmarks, capstones, open-source readiness/contributions ve technical communication uzun vadeli evidence/portfolio hedeflerine dahil edilir.
- D-001'in no-countdown ilkesi ve D-012'nin V1/full-curriculum ayrımı korunur; yalnız eski yaklaşık üç yıllık süre ufku D-041 ile genişletilmiştir.

Ayrıntı: `docs/PROFESSIONAL_READINESS_TARGET.md`, `docs/PRODUCT_REQUIREMENTS.md`, `docs/PROJECT_MASTER_CONTEXT.md`, `docs/V1_SCOPE.md`.

## D-042 — Python ana öğrenme rotasının resmi foundation dilidir
**Durum:** Kabul edildi — 2026-08-25

- Python, C/C++'ın yerine geçmez; systems/AI infrastructure rotasında tamamlayıcı ana dildir.
- Temel programlama, otomasyon, test/benchmark scripting, veri işleme, ML/PyTorch ekosistemi ve infrastructure tooling için curriculum'a resmi olarak eklenir.
- Python yalnız syntax seviyesinde bırakılmaz; ilerleyen curriculum'da typing, testing, async/concurrency, multiprocessing, networking, profiling, packaging ve infra/ML bağlamında gerçek kullanım içerir.
- Common core Python + C ile başlar; düşük seviye sistem derinliği Modern C++/C ve daha sonra CUDA/Triton ile devam eder.

## D-043 — Professional rota ortak çekirdekten sonra uzmanlık dallarına ayrılacak
**Durum:** Kabul edildi — 2026-08-25

- Mevcut AŞAMA 1–19 kodları renumber edilmez; sona **AŞAMA 20 — Uzmanlık Dallarına Ayrılma ve Track Sistemi** eklenir.
- Kullanıcı önce ortak systems/distributed/GPU/inference çekirdeğinde gerekli capability gates'i karşılar; sonra tek bir uzmanlık derinliğine mahkûm olmayan track sistemi kullanılır.
- İlk candidate track aileleri: GPU Kernel & Performance; LLM Inference/Serving; Distributed AI Infrastructure/Cluster; High-Speed Networking & Multi-GPU; ML Compilers/Runtime; AI Platform/Reliability & Capacity.
- Track seçimi yalnız LLM önerisi, maaş veya hype ile otomatik yapılmaz; kullanıcı tercihi + verified capability + prerequisite readiness + gerçek kariyer kısıtları birlikte değerlendirilir.
- Ortak core korunur; track değiştirmek sıfırdan başlatmaz. Yalnız yeni dalın eksik prerequisite/required evidence'ı açılır.
- Her track kendi critical Skill, project, benchmark/profiling, debugging, transfer ve specialization capstone evidence contract'ına sahip olacaktır.
- Track isimleri zamanla değişebilir; canonical değer framework adlarından çok kalıcı systems capability'lerinde tutulur.

Ayrıntı: `docs/EXECUTION_INDEX.md`, `docs/MASTER_PLAN.md`.
