# Product Decisions Log

Bu dosya kalıcı ürün kararlarının canonical özetidir. Ayrıntılı davranış ilgili spec dosyalarındadır.

## D-001 — Takvim/gün sayacı ana ilerleme metriği olmayacak
**Durum:** Kabul edildi / D-041 ile süre ufku genişletildi  
Zaman geçirmek professional progress değildir; `Gün X / toplam gün` readiness gate'i olamaz.

## D-002 — İlerleme mastery tabanlı olacak
**Durum:** Kabul edildi  
Task/lesson completion tek başına öğrenme değildir.

## D-003 — Knowledge graph takvimden öncelikli
**Durum:** Kabul edildi  
Takvim kapasiteyi; prerequisite/mastery konu uygunluğunu belirler.

## D-004 — Eksik konu tüm programı dondurmaz
**Durum:** Kabul edildi  
Yalnız gerçekten bağımlı branch bekler.

## D-005 — Haftalık/aylık assessment programı değiştirecek
**Durum:** Kabul edildi  
Sonuç yalnız rapor/not değildir; canonical evidence/state pipeline üzerinden planner'ı etkiler.

## D-006 — İngilizce paralel ilerleyecek
**Durum:** Kabul edildi

## D-007 — Ana kariyer rotası systems → GPU → AI infrastructure
**Durum:** Kabul edildi / D-042 ile Python foundation eklendi  
Technical English paralel → Python → C → Linux/Git/Shell → DS&A → Modern C++ → Architecture → OS/Memory → Concurrency → Networking → Distributed/Storage → Cloud/Observability → Performance → GPU → CUDA → Triton → ML/Transformer → Inference/Serving → Multi-GPU → AI Infrastructure → Open Source/projects/capstone.

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
AI yardımı öğrenmeyi destekler; independent mastery yerine geçmez.

## D-012 — Full curriculum V1 ön koşulu değil
**Durum:** Kabul edildi / D-041 ile kapsam genişletildi  
V1 learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilebilir.

## D-013 — Süreler adaptif, prerequisite/mastery daha kalıcı
**Durum:** Kabul edildi

## D-014 — İlk iş doğrudan CUDA olmak zorunda değil
**Durum:** Kabul edildi  
C++ Systems, Systems Software, Linux/Infrastructure, Distributed Systems, Performance, uygun SRE/Cloud vb. bridge rol olabilir.

## D-015 — Geliştirme master plan üzerinden aşamalı yürütülecek
**Durum:** Kabul edildi — 2026-08-24

## D-016 — Research / Coding / Test ayrı AI rolleridir
**Durum:** Kabul edildi — 2026-08-24  
Ana yönetici karar/spec/GitHub hafızasından sorumludur. Ayrıntı: `docs/AI_AGENT_WORKFLOW.md`.

## D-017 — Adım kodları canonical indeks üzerinden yürütülecek
**Durum:** Kabul edildi / D-044 future-stage reindex istisnası  
Tamamlanmış adımlar sessizce renumber edilmez; açık plan değişikliğinde henüz başlanmamış future stages yeniden indekslenebilir.

## D-018 — V1 gerçek Android adaptive learning loop'u olacak
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/V1_SCOPE.md`.

## D-019 — V1 release acceptance + bağımsız QA'ya bağlı
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/V1_SUCCESS_CRITERIA.md`.

## D-020 — Product/V1 non-goals kilitlendi
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/NON_GOALS.md`.

## D-021 — Learning unit organizasyon ve mastery katmanları ayrıdır
**Durum:** Kabul edildi — 2026-08-24  
`Domain → Module → Topic → Skill → Learning Objective`; canonical mastery/prerequisite ana seviyesi Skill, atomik evidence Objective'tir. Ayrıntı: `docs/LEARNING_ENGINE_SPEC.md`.

## D-022 — Öğretme/yanlış/remediation/retention/replan davranışları bağlayıcı
**Durum:** Kabul edildi — 2026-08-24  
Ayrıntı: `docs/LEARNING_BEHAVIOR_RULES.md`.

## D-023 — Topic state Skill verilerinden derived work state olacak
**Durum:** Kabul edildi — 2026-08-24  
`locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`. Ayrıntı: `docs/TOPIC_STATE_MACHINE.md`.

## D-024 — Her numaralı adım öncesi/sonrası GitHub beyin tazelemesi zorunlu
**Durum:** Kabul edildi — 2026-08-24  
`PRE-STEP GitHub refresh → çalışma → POST-STEP GitHub sync`. Ayrıntı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

## D-025 — Mastery çok kaynaklı, Objective-matched evidence ister
**Durum:** Kabul edildi — 2026-08-24  
Recognition, recall, code reading, coding, debugging, explanation, transfer, retention, project ayrıdır; time/completion/streak/self-confidence mastery değildir. Ayrıntı: `docs/MASTERY_SIGNALS_SPEC.md`.

## D-026 — AI/ipucu yardımı independent mastery ile eşit değildir
**Durum:** Kabul edildi — 2026-08-24  
H0–H4, timing, provenance ve fresh recheck davranışı. Ayrıntı: `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`.

## D-027 — MASTER_PLAN canonical execution state ile senkron tutulacak
**Durum:** Kabul edildi — 2026-08-24

## D-028 — Mobil performans/akıcılık first-class requirement
**Durum:** Kabul edildi — 2026-08-24  
Bounded/incremental hesap, async ağır işler ve gerçek cihaz QA zorunlu.

## D-029 — İlk Beta-style mastery candidate'ı
**Durum:** YERİNE D-031 GEÇTİ — 2026-08-24  
Kalibre edilmemiş çarpanlara sahte hassasiyet verdiği için final modelden çıkarıldı.

## D-030 — 2E ayrı Research AI raporu olmadan kapatılamaz
**Durum:** UYGULANDI / TAMAMLANDI — 2026-08-24

## D-031 — Final Mastery Formula v0 = GRE-v0
**Durum:** Kabul edildi — 2026-08-24  
Yalnız eligible + prerequisite-valid + H0 + direct + verified + independent evidence groups; bounded recent evidence, required/critical gates ve verification hysteresis. Ayrıntı: `docs/MASTERY_FORMULA_V0.md`.

## D-032 — Final Retention/Forgetting = RVR-v0
**Durum:** Kabul edildi — 2026-08-24  
Time mastery'yi düşürmez; `review_due` forgetting değildir; ilk clean contradiction `verification_due` üretir. Ayrıntı: `docs/RETENTION_FORGETTING_SPEC.md`.

## D-033 — Daily capacity hard budget'tır
**Durum:** Kabul edildi — 2026-08-24  
No auto-overrun; safe split → smaller alternative → defer; deferred task debt değildir. Ayrıntı: `docs/ADAPTIVE_PLANNER_SPEC.md`.

## D-034 — Kalıcı olan eski task değil LearningNeed'dir
**Durum:** Kabul edildi — 2026-08-24  
`State → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`; purpose/activity/track/evidence ayrı eksenlerdir. Ayrıntı: `docs/TASK_TAXONOMY_SPEC.md`.

## D-035 — Planner priority = PBR-v0
**Durum:** Kabul edildi — 2026-08-24  
Eligibility önce, P0–P4 semantic bands + deterministic rank vector; starvation/track balance; duration semantic priority'den sonra. Ayrıntı: `docs/PRIORITY_POLICY_SPEC.md`.

## D-036 — Prerequisite = PRG-v0
**Durum:** Kabul edildi — 2026-08-24  
Skill→Skill hard/soft edge; `ready | ready_due | uncertain | not_ready`; branch-local blocking ve contamination guard. Ayrıntı: `docs/PREREQUISITE_POLICY_SPEC.md`.

## D-037 — Hızlı öğrenme = VDW-v0
**Durum:** Kabul edildi — 2026-08-24  
Validated Objective-level diagnostic waiver; diagnostic mastery'nin kolay alternatifi değildir. Ayrıntı: `docs/DIAGNOSTIC_WAIVER_SPEC.md`.

## D-038 — Missed-day/re-entry = SRR-v0
**Durum:** Kabul edildi — 2026-08-24  
Absence failure/debt değildir; stale plan replay edilmez; current-state replan yapılır. Ayrıntı: `docs/MISSED_DAY_RECOVERY_SPEC.md`.

## D-039 — Planner explainability = PDT-v0
**Durum:** Kabul edildi — 2026-08-24  
Structured reason codes + decision trace; user explanation trace'teki factual nedenlerden türetilir. Ayrıntı: `docs/PLANNER_EXPLAINABILITY_SPEC.md`.

## D-040 — Daily micro assessment = DMA-v0
**Durum:** Kabul edildi — 2026-08-24  
Zorunlu günlük quiz/kota değildir; Objective-matched evidence, H0/assistance/provenance, prerequisite fairness, invalid/provisional safety ve evidence→state→replan pipeline kullanır. Ayrıntı: `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`.

## D-041 — Uzun vadeli hedef 4+ yıl professional-readiness curriculum
**Durum:** Kabul edildi — 2026-08-24  
4+ yıl countdown değildir; final hedef verified engineering capability + integrated/capstone evidence. V1 full curriculum'u beklemez. Ayrıntı: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## D-042 — Python ana rotanın resmi foundation dilidir
**Durum:** Kabul edildi — 2026-08-25  
Python C/C++'ın yerine geçmez; automation/testing/benchmark, data/ML, infra tooling; ileri Python typing/testing/async/multiprocessing/networking/profiling/packaging içerir.

## D-043 — Standalone specialization-track aşaması
**Durum:** GERİ ÇEKİLDİ / YANLIŞ YORUM — 2026-08-25  
Kullanıcı talebi yanlış yorumlandığı için canonical değildir.

## D-044 — Full rota granular capability map'e ayrılacak; AŞAMA 6 eklendi
**Durum:** Kabul edildi — 2026-08-25  
Her büyük alan `Domain → Module → Topic → Skill → Learning Objective` seviyesine ayrılır; weakness/mastery/remediation mümkün olduğunca Skill/Objective düzeyinde lokalize edilir. Ayrıntı: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

## D-045 — Weekly assessment = WBA-v0 Weekly Blueprint Assessment
**Durum:** Kabul edildi — 2026-08-25  
Item'dan önce state-temelli blueprint; multi-Skill coverage granular attribution; no fixed score/time/quota; PRG/family-diversity/H0/invalid-item/hysteresis guards; split/pause/resume; missed/incomplete exam debt değildir. Ayrıntı: `docs/WEEKLY_ASSESSMENT_SPEC.md`.

## D-046 — Monthly assessment = MCA-v0 Monthly Capability Assessment
**Durum:** Kabul edildi — 2026-08-25  
Longitudinal state-based sampling; broader transfer/integration; critical revalidation yalnız gerçek ihtiyaçta; no cumulative-everything/pass-score; professional checkpoint final readiness değildir; H0/invalid/root-contamination/hysteresis guards; persistent gap planner'ı etkiler. Ayrıntı: `docs/MONTHLY_ASSESSMENT_SPEC.md`.

## D-047 — Assessment bank = QAB-v0 Trusted Assessment Resource Bank
**Durum:** Kabul edildi — 2026-08-25

- Question Bank yalnız MCQ deposu değildir; recognition/recall/code-reading/coding/debugging/hands-on system/explanation/transfer/integrated/language/testlet/template resource'larını taşır.
- Logical `resource_id` ile immutable published `resource_version` ayrıdır; Attempt exact version'a bağlanır.
- Published version sessizce overwrite edilmez; yeni semantic değişiklik yeni version üretir.
- Lifecycle `draft | candidate | validated | trusted | deprecated | invalidated | retired` olarak modellenir; lifecycle ile `use_ceiling` ayrı tutulur.
- `use_ceiling`: practice-only, low-stakes, standard mastery, critical mastery gibi declared kullanım tavanını belirler; bank'te bulunmak otomatik strong-evidence eligibility değildir.
- Resource exact target Skill/Objective, prerequisite, forbidden-not-yet concept, language prerequisite, activity/evidence, assessment scope, blueprint-role/intent, evaluator/tool/artifact ve duration metadata'sı taşır.
- `variant_family`, `dependency_group/testlet`, `context_family` ve transfer profile farklı kavramlardır; near/same items independent evidence diversity'yi şişiremez.
- Integrated task global PASS'i bütün tagged Objectives'e yayamaz; component attribution `structurally_essential + separately_observable + prerequisite-valid` olmak zorundadır.
- Difficulty sahte hassas numeric mastery multiplier değildir; semantic difficulty + complexity profile kullanılır, empirical calibration AŞAMA 18'e bırakılır.
- User solution exposure global content lifecycle'dan ayrıdır; exact/near item reuse fresh evidence koşullarına tabidir, fixed universal cooldown yoktur.
- Learner exposure freshness ile content/technology freshness ayrıdır; stale technical content strong assessment'ta kullanılamaz.
- Deprecated resource yanlış olmak zorunda değildir; invalidated resource integrity problemi taşır ve affected historical evidence audit/review akışına girebilir.
- Selection `scope/role/evidence → lifecycle/use ceiling/freshness → prerequisite/language → evaluator/tool → exposure/family/context → duration → bounded rank` sırasıyla çalışır; full-bank scan hedeflenmez.
- Parameterized trusted template generated instances'ı yeni independent family yapmaz.
- AI-generated resource varsayılan olarak `candidate` başlar ve 4E validation olmadan trusted/mastery-changing bank'e otomatik yükselmez.
- Resource hiçbir zaman doğrudan `mastery_delta` veya broad domain pass/fail taşımaz; gerçek Attempt/Artifact normal GRE/RVR/PRG pipeline'ından geçer.

Ayrıntı: `docs/QUESTION_BANK_SPEC.md`.

## D-048 — AI-generated assessment validation = AIV-v0
**Durum:** Kabul edildi — 2026-08-25

- AI output kendi validation proof'u değildir; generated resource `candidate` başlar ve minimum validation geçmeden user-facing selection'a çıkmaz.
- Validation schema/reference, technical correctness, answer/rubric, ambiguity, Objective/evidence fit, prerequisite/forbidden concept/language leakage, duplicate/family/dependency/context/transfer, evaluator/tool/artifact, freshness ve execution-safety boyutlarını ayrı kontrol eder.
- Tek confidence yüzdesi veya weighted validator score yoktur; her check use ceiling'i sınırlar ve final ceiling en kısıtlayıcı applicable sonuçtur.
- Generator self-review veya model majority vote tek başına trust değildir; deterministic/executable/reference-grounded validation önceliklidir.
- `practice_only < low_stakes_assessment < standard_mastery_eligible < critical_mastery_eligible` semantic use-ceiling sırası korunur.
- Practice-only yanlış bilgi toleransı değildir; correctness unresolved ise resource gösterilmez.
- Standard/critical mastery promotion için strong independent/deterministic/reference-backed correctness ve verified evaluator gerekir; tek uncalibrated LLM critical verified evidence üretemez.
- Hidden prerequisite veya bilinmeyen English learner failure'a dönüştürülemez.
- Near duplicate yeni independent family sayılmaz; family classification belirsizse diversity credit artırılmaz.
- Transfer/integration iddiası semantic olarak doğrulanmalı ve component attribution ayrı gözlenebilir olmalıdır.
- Trusted-template inheritance yalnız validated invariants korunuyorsa mümkündür; semantic AI rewrite normal revalidation ister.
- Validator disagreement fail-safe olarak promotion'ı durdurur; majority vote pass değildir.
- Generated code/system tasks için execution/environment safety validation zorunludur.
- Version-sensitive content source/technology freshness audit ister.
- Confirmed content bug resource'u invalidated yapabilir ve exact version'a bağlı historical evidence review/repair akışı açabilir; learner cezalandırılmaz.
- Heavy validation async/bounded çalışır; live session'da validator unavailable diye evidence standardı düşmez.
- Empirical validator/evaluator accuracy thresholds AŞAMA 14F/18 calibration'a bırakılmıştır; 4E sahte scientific optimum uydurmaz.

Ayrıntı: `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md`.

## D-049 — Professional curriculum domain backbone = PDM-v0
**Durum:** Kabul edildi — 2026-08-25

- AŞAMA 5A'nın canonical çıktısı `PDM-v0 — Professional Domain Backbone` oldu.
- Ana route 23 broad family halinde korunur; bu family'ler takvim veya mastery atomu değildir.
- Domain rollerinde `parallel_track`, `common_foundation`, `systems_core`, `distributed_platform_core`, `performance_core`, `accelerator_core`, `supporting_domain`, `inference_systems_core`, `target_infrastructure`, `professional_evidence_layer` semantiği kullanılır.
- Technical English bütün rota boyunca paraleldir; teknik progression'ın global hard prerequisite'i değildir.
- Python + C + Linux/Git/Shell complementary early foundations'tır; katı seri kurs gibi çalışmaz.
- Systems core: Modern C++, Architecture, OS/Memory, Concurrency ve Networking üzerinden distributed/performance katmanına ilerler.
- Performance Engineering sona bırakılan tek optimization bölümü değildir; measurement/benchmark/profiling habits route boyunca büyür.
- GPU Architecture → CUDA/Triton accelerator katmanı systems/performance foundations üzerine oturur; Triton GPU/CUDA mental modelini bypass etmez.
- ML/Transformer ayrı research specialization değil, inference sistemlerini anlayacak supporting domain'dir; gerekli math/numerical capability hidden prerequisite bırakılmaz.
- LLM Inference → serving systems → KV/batching/scheduling/quantization ayrı fakat bağlı domain family'leridir.
- Multi-GPU/NCCL/RDMA networking + distributed + GPU foundations'in advanced convergence katmanıdır.
- AI/GPU Infrastructure systems + distributed + cloud/observability + performance + inference + multi-GPU capability'lerinin target integration domain'idir.
- Open Source/engineering practice/projects/capstones yalnız finalde başlamaz; küçük artifacts erken, integrated projects orta, professional capstones ileri aşamada gelir.
- Security/reliability/observability ayrı cybersecurity specialization'a çevrilmeden cross-cutting professional capability olarak ilgili domainlere dağılır.
- Tool/vendor isimleri stable system concept'in yerine geçmez; version/freshness metadata ile ayrılır.
- Domain-level ilişkiler authoring guidance'dır; runtime hard prerequisite canonical olarak Skill→Skill PRG-v0 ile çözülür.
- 5B graph/metadata contract'ı bu domain backbone'u formalize edecek; AŞAMA 6 bütün family'leri Module/Topic/Skill/Objective seviyesine parçalayacaktır.

Ayrıntı: `docs/CURRICULUM_DOMAIN_MAP.md`.
