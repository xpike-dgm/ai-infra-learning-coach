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

## D-050 — Living project memory sync ve repo-wide stale-reference audit zorunlu
**Durum:** Kabul edildi — 2026-08-25

- Her numaralı adım sonunda `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN`, `PROJECT_CONTEXT`, `START_HERE` ve `DECISIONS` istisnasız kontrol edilir; durum/karar değişikliğinden etkilenenler aynı POST-STEP içinde güncellenir.
- `PROJECT_CONTEXT.md` kısa yaşayan snapshot'tır ve eski aktif adımda bırakılamaz.
- `PROJECT_MASTER_CONTEXT.md` uzun/stabil bağlamdır; volatile aktif adımı tekrar etmez.
- `README.md` insan için repo girişidir; volatile aktif step kopyalamak yerine current state kaynaklarına yönlendirir.
- Her adım kapanışında değişen step/model/dosya adları için repo-wide stale-reference taraması yapılır.
- Future-stage reindex yapılırsa bütün repo içindeki future-reference'lar aynı senkron turunda taranıp düzeltilir; historical completion metni geçmiş bağlam olarak korunabilir.
- Aynı role sahip duplicate yaşayan source of truth tutulmaz; tamamen superseded ve benzersiz provenance değeri olmayan taslak silinir, faydalı eski seed notları açıkça `NON-CANONICAL/HISTORICAL` etiketlenir.
- Stable tamamlanmış spec'ler sırf active step değişti diye yeniden yazılmaz; ancak stale cross-reference veya superseded contract içeriyorsa düzeltilir.
- Ayrıntılı dosya rol matrisi ve kapanış checklist'i `docs/PROJECT_MEMORY_PROTOCOL.md` içinde canonicaldır.

## D-051 — Curriculum knowledge graph contract = KGC-v0
**Durum:** Kabul edildi — 2026-08-25

- 5B final modeli `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` oldu.
- Curriculum iki ayrı katmanda modellenir: `Domain → Module → Topic` organization; `Skill → Learning Objective` capability/evidence.
- Skill canonical reusable capability identity'dir; farklı Topic/Domain placements yeni learner mastery kaydı yaratmaz.
- Topic↔Skill many-to-many `TopicSkillLink` ile çözülür; Objective exactly one canonical Skill'e bağlıdır.
- Runtime prerequisite canonical olarak versioned `SkillPrerequisiteEdge` (`hard | soft`) kullanır; Domain/Module/Topic relations yalnız authoring guidance'dır.
- `required / critical / optional` geniş curriculum scope'larında scope-relative capability semantics'tir; global broad-domain boolean ile bütün rota kilitlenmez.
- Objective evidence profile GRE-v0 gate alanlarını taşır; QAB resource link'i mastery evidence'ın kendisi değildir.
- Skill retention profile RVR-v0 ile; diagnostic/remediation metadata runtime learner state'ten ayrı şekilde bağlanır.
- Technical English global technical hard gate değildir; language dependency yalnız gerçekten gerekli capability/task'ta explicit modellenir.
- Professional/project/capstone attribution granular Skill/Objective seviyesinde tutulur; project PASS bütün tagged capability'lere otomatik evidence vermez.
- Published entity/edge/graph semantic state immutable versionlanır; split/merge/refactor learner'a bedava mastery veremez ve historical evidence'ı sessizce silemez.
- Graph migration `fully_compatible | compatible_with_reverification | not_automatically_transferable` evidence compatibility semantiğini explicit taşır.
- Provenance/freshness ile stable systems concept ve fast-moving tool/vendor content ayrılır.
- D-028 gereği runtime full-graph scan'e dayanmaz; adjacency/reverse-dependency/index/cache contract'ı zorunludur, exact DB/index budgets 9C/9F/18E'ye bırakılır.
- 5C ilk 8–12 haftalık V1 alt graph'ını KGC-v0 ile kuracak; AŞAMA 6 full granular decomposition'u aynı contract üzerinde yapacaktır.

Ayrıntı: `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.

## D-052 — V1 foundation backbone = FBB-v0
**Durum:** Kabul edildi — 2026-08-25

- 5C final modeli `FBB-v0 — V1 Foundation Backbone` oldu.
- “İlk 8–12 hafta” calendar unlock değildir; V1 başlangıç content hacmini/scope'unu ifade eder. Runtime progression mastery + prerequisite + retention + daily capacity ile belirlenir.
- Canonical çıktı `docs/V1_FOUNDATION_BACKBONE.md`.
- V1 foundation subgraph; Computer/Programming zero-entry bridge, Python, C, Linux/Git/Shell, early DS&A ve day-one parallel Technical English'i KGC-v0 üzerinde bağlar.
- 5C Skill/Objective ID'leri `authoring_seed / not_learner_published` lifecycle'ındadır; 6A/6C global naming/granularity standardı ile ratify veya explicit KGC migration üzerinden refine edilir.
- Tek global `foundation_passed` gate yoktur. Technical, English ve professional-workflow scope'ları ayrıdır; English global technical hard prerequisite değildir.
- Shared programming mental-model Skills ile language-specific production Skills ayrıdır; Python mastery C syntax mastery'yi bedava vermez.
- Initial runtime dependencies yalnız Skill→Skill hard/soft PRG-v0 edge'leridir; Domain/Module/Topic placement hard lock üretmez.
- Evidence templates GRE/QAB/RVR'ı değiştirmez; yeni numeric mastery threshold/count icat edilmez.
- Diagnostic, retention ve remediation anchor'ları exact Skill/Objective attribution'a bağlıdır; broad Domain reset yasaktır.
- AŞAMA 15 production content, 6A/6C/6H sonrası ratified/published graph ID'lerine bağlanır.
- 5D FBB-v0 subgraph'ı cycle, dead-end, hidden prerequisite, duplicate Skill/reuse ve reachability açısından QA edecektir.
- Ayrı external Research AI 5C'de kullanılmadı; full coverage/current-industry/prerequisite bağımsız Research QA 6H'de zorunlu kalır.

Ayrıntı: `docs/V1_FOUNDATION_BACKBONE.md`.

## D-053 — Foundation graph architecture QA = GQA-v0
**Durum:** Kabul edildi — 2026-08-25

- 5D final modeli `GQA-v0 — Foundation Graph Architecture QA` oldu.
- Canonical QA dosyası `docs/GRAPH_ARCHITECTURE_QA.md`.
- İlk FBB-v0 audit'inde explicit TopicSkillLink matrix eksikliği ve KGC controlled vocabulary dışı `reason_kind=supporting` kullanımı blocking structural bulgu olarak saptandı ve düzeltildi.
- Hidden-prerequisite audit'i Python I/O/collections/mapping/files, Python exception/module context, trace→debug chain, debug-fix explanation ve C storage/lifetime context için minimal required hard edge'leri ekledi.
- Corrective patch sonrası hard graph DAG; self/dangling/conflicting edge yok; combined relations cycle üretmiyor.
- Shared capability reuse canonical Skill + TopicSkillLink ile çözülür; clone learner state yasaktır.
- English global technical hard gate değildir; branch isolation korunur.
- `review_due` PRG/RVR gereği hard prerequisite'i otomatik `not_ready` yapmaz.
- FBB entity'leri hâlâ `authoring_seed / not_learner_published`; 6A/6C ratification ve 6H external Research QA öncesi production publish yapılmaz.
- 5D PASS full professional curriculum coverage doğrulaması değildir; 6H independent Research AI zorunluluğu korunur.

Ayrıntı: `docs/GRAPH_ARCHITECTURE_QA.md`.

## D-054 — Granularity & Naming Standard = GNS-v0
**Durum:** Kabul edildi — 2026-08-25

- 6A final modeli `GNS-v0 — Granularity & Naming Standard` oldu.
- Canonical çıktı `docs/GRANULARITY_NAMING_STANDARD.md`.
- Domain/Module/Topic organization granularity'si ile Skill/Objective capability/evidence granularity'si ayrı semantic testlerle tanımlandı.
- Yeni Skill kararı takvim veya keyword'e değil independent evidence, independent remediation, prerequisite boundary ve cross-context reuse değerine bağlandı.
- Under-fragmentation ve over-fragmentation guard'ları zorunlu authoring QA oldu.
- Aynı semantic capability farklı Topic/Domain placements'ta clone'lanmaz; canonical Skill + TopicSkillLink reuse edilir.
- Shared mental-model capability ile language/tool-specific production capability ayrı Skill olabilmesi için explicit split kriteri tanımlandı.
- Learning Objective exactly-one-Skill altında atomic observable evidence target olarak tutulur; content-instance adı Objective identity olamaz.
- Logical ID convention lowercase ASCII dotted namespace + snake_case segment; locale/order/version bağımsızdır.
- Week/day/stage/FB band/release/version/difficulty/requirement role logical ID'ye gömülmez.
- `basic/advanced/intro` yalnız gerçek semantic scope ifade ediyorsa kullanılabilir; FBB seed'deki vague level slug'ları 6C ratification'da review edilir.
- Display/localization/alias değişimi logical identity değişimi değildir.
- Split/merge/re-home/objective-move KGC-v0 conservative migration semantics ile çözülür; bedava mastery yoktur.
- FBB authoring seed'leri 6C'de `ratify_as_is | ratify_with_display_edit | normalize_logical_id | split_required | merge_with_existing | rehome_placement_only | deprecate_seed | needs_granularity_review` status'larından biriyle değerlendirilir.
- 6B ortak decomposition template'i GNS-v0 reason-code ve authoring alanlarını tüketmek zorundadır.
- 6A external Research AI kullanmadı; full coverage/current-industry/prerequisite independent Research AI doğrulaması 6H'de zorunlu kalır.

Ayrıntı: `docs/GRANULARITY_NAMING_STANDARD.md`.

## D-055 — Ana yöneticilik rolü local çalışan agent'a devredilebilir
**Durum:** Kabul edildi — 2026-08-26

- Kullanıcı ana manager/koordinatör rolünü local çalışan agent'a devretme kararı verdi.
- Manager sorumlulukları değişmez: PRE-STEP refresh, state consistency, Research/Coding/Test delegation, spec/decision ownership, independent acceptance, POST-STEP living-memory sync ve repo-wide stale-reference audit.
- GitHub/repo durable source of truth olmaya devam eder; local scratchpad veya sohbet hafızası canonical kararların yerine geçmez.
- Local takeover bootstrap: root `AGENTS.md`, `docs/LOCAL_MANAGER_HANDOFF.md`, `docs/START_HERE.md`, `docs/PROJECT_MEMORY_PROTOCOL.md`, ardından repo içindeki tüm Markdown dosyalarının tam okunması.
- `LOCAL_MANAGER_HANDOFF.md` canonical specs/DECISIONS yerine geçmez; kayıpsız bootstrap ve navigation belgesidir.
- D-016 Research/Coding/Test bağımsızlığı korunur; explicit independent Research AI zorunluluğu manager'ın kendi araştırmasıyla ikame edilemez.
- Bu transition numbered curriculum/architecture adımı değildir; **6B'yi yürütmez veya tamamlamaz**.
- Transition state: `6A ✅ GNS-v0 / D-054`, `6B 🟡 active / not executed`.

Canonical takeover bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`.

## D-056 — Full-route decomposition blueprint = FRDB-v0
**Durum:** Kabul edildi — 2026-08-26

- 6B final modeli `FRDB-v0 — Full-Route Decomposition Blueprint` oldu.
- Canonical çıktı `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md`.
- 23 route family 6C Foundations, 6D Systems, 6E GPU/ML/Inference ve 6F Professional Engineering package sınırlarına eksiksiz atandı.
- 6C–6F için canonical interchange, logical collection'ları standardize edilmiş UTF-8 YAML authoring package olarak tanımlandı; Markdown yalnız açıklama/QA eşlikçisidir.
- Manifest, source catalog, organization entities, Skills, Objectives, TopicSkillLinks, prerequisite edges, scope-relative requirements, professional/project attributions, seed mappings, review queue ve QA report contract'ları kilitlendi.
- Skill candidate authoring independent evidence, remediation, prerequisite, reuse ve shared-vs-specific sınırlarını explicit taşımak zorundadır.
- Objective exactly-one-Skill altında observable action, success criteria ve GRE-compatible evidence profile taşır.
- Cross-package duplicate resolver accepted graph + prior packages + current candidates + FBB mappings üzerinde çalışır; aynı semantic Skill yeni placement için clone'lanmaz.
- Hard/soft prerequisite authoring PRG-v0 contamination testini kullanır; task-specific requirement graph edge'e çevrilmez, Domain/Topic order hard gate olmaz.
- FBB seed ratification/refactor mapping'i 6C için zorunlu hale getirildi.
- GNS-v0 reason codes ve blocking/non-blocking unresolved review queue canonical authoring QA parçasıdır.
- Retention, diagnostic, remediation, professional attribution, provenance ve freshness metadata'sı authoring contract'a bağlandı; yeni mastery algoritması veya sahte numeric threshold üretilmedi.
- Technical English global technical gate değildir; stable concept ile tool/vendor-specific fast-moving capability ayrımı korunur.
- 6G weakness/remediation mapping ve 6H independent external Research QA handoff'u explicit tutuldu.
- FRDB-v0 physical DB schema, production lesson/task content'i veya gerçek full-route node listesi değildir.

Ayrıntı: `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md`.

## D-057 — Foundations detailed map = FDM-v0
**Durum:** Kabul edildi — 2026-08-26

- 6C final modeli `FDM-v0 — Foundations Detailed Map` oldu.
- Canonical summary `docs/FOUNDATIONS_DETAILED_MAP.md`; canonical machine-readable dataset `curriculum/decomposition/6c_foundations/` içindedir.
- D01–D05; 5 Domain, 14 Module, 46 Topic, 132 canonical Skill candidate ve 137 exactly-one-Skill Objective ile ayrıntılandırıldı.
- 145 TopicSkillLink aynı semantic shared capability'nin Python/C/DS&A placement'larında clone'lanmasını önler.
- 200 hard/soft prerequisite edge PRG-v0 semantics ile tanımlandı; hard graph DAG, self/dangling/conflicting edge yoktur.
- FBB-v0 41 Skill ve 47 Objective seed'inin tamamı explicit mapping aldı; 33 Skill ratify edildi, 8 broad Skill split edildi; 38 Objective ratify edildi, 9 Objective owner/ID normalization aldı.
- FBB learner-published olmadığı için seed refactor learner evidence migration veya bedava mastery üretmez.
- Technical English granular capability graph'ıdır fakat unrelated technical Skills için global hard gate değildir; CEFR progression/cadence AŞAMA 7'de kalır.
- Python map core language, data/functions, software engineering, systems/data/performance reuse capability'lerini ayrı learner states olarak kapsar.
- C map toolchain, types/control/functions, pointer/memory/data, modular build/debug/safety capability'lerini ayrıştırır.
- Linux/Shell/Git ve DS&A map'leri komut/başlık ezberi yerine observable workflow, state, correctness ve complexity capability'leri kullanır.
- Internal deterministic QA sonucu `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`; 0 blocking review vardır.
- External coverage/current-industry/hidden-prerequisite validation 6H independent Research AI'ye kadar pending; package learner-published değildir.

Ayrıntı: `docs/FOUNDATIONS_DETAILED_MAP.md`.

## D-058 — Systems detailed map = SDM-v0
**Durum:** Kabul edildi — 2026-08-26

- 6D final modeli `SDM-v0 — Systems Detailed Map` oldu.
- Canonical summary `docs/SYSTEMS_DETAILED_MAP.md`; canonical machine-readable dataset `curriculum/decomposition/6d_systems/` içindedir.
- D06–D13; 8 Domain, 21 Module, 64 Topic, 192 canonical Skill candidate ve 207 exactly-one-Skill Objective ile ayrıntılandırıldı.
- 224 TopicSkillLink ile shared capability'ler Systems bağlamlarında clone'lanmadan yeniden kullanıldı.
- 313 hard/soft prerequisite edge PRG-v0 semantics ile tanımlandı; 6C + 6D birleşik hard graph DAG'dır (324/324 node), self/dangling/duplicate/conflicting edge yoktur.
- FRDB-v0 §19 Pass B hard/soft testi uygulandı: yalnız scaffold sağlayan 52 declared source soft'a indirildi ve `hard_soft_test_result` alanıyla kayıtlandı. Final dağılım 259 hard / 54 soft.
- 43 accepted 6C Skill cross-package prerequisite kaynağı veya `reinforce` TopicSkillLink olarak reuse edildi; reuse `seed_mappings.yaml` içinde `mapping_class: reused_from_prior_package` ile explicit beyan edilir ve canonical ID değişmez.
- 6D hiçbir 6C Skill/Objective ID'sini yeniden tanımlamaz; prerequisite edge target'ı daima bir 6D Skill'idir, yani 6C graph'ı geriye dönük değiştirilmez.
- Modern C++ map'i dil feature listesi yerine ownership/lifetime/move/exception-safety/build/test capability sınırlarını kullanır; pointer ve memory temel capability'leri C'den reuse edilir.
- Architecture map'i chip-design specialization'a genişlemez; execution, cache/locality ve latency/throughput reasoning'i performance ile bağlar.
- OS/Memory map'i teoriyi Linux gözlemi, syscall tracing ve ölçümle bağlar.
- Concurrency map'i correctness ile scaling capability'lerini ayrı learner states olarak tutar; API kullanımı tek başına capability değildir.
- Networking map'i certification müfredatı değildir; protokol semantiği, socket/multiplexing production ve katmanlı arıza tanısını kapsar.
- Distributed + Storage map'i partial failure, replication/partitioning, consistency, consensus, durability/WAL, transaction/isolation, index/query cost ve delivery semantics'i ayrı capability'lere böler.
- Containers/Cloud/Observability map'i tool/vendor adını stable concept'in yerine koymaz; 13 fast-moving ve 21 version-sensitive Skill freshness + technology dependency metadata'sı taşır, incident response ve postmortem gibi practice-shaped capability'ler evergreen kalır.
- Performance map'i ölçüm geçerliliğini optimizasyondan önce zorunlu tutar; benchmark, varyans, tail latency, profiling, bottleneck attribution ve kapasite/maliyet reasoning'i ayrı capability'lerdir.
- 15 Skill için ikinci Objective yazıldı; bunlar aynı capability'nin ayrı gözlemlenebilir kanıtıdır, Skill split gerekçesi değildir.
- `project.systems.observable_networked_service` candidate attribution'ı yalnız structurally essential ve separately observable 12 component için yazıldı; tek project PASS toplu evidence üretmez.
- Technical English global technical hard gate değildir; English→unrelated technical hard edge yoktur.
- Deterministik authoring generator (`tools/generate_systems_package.py`) ve bağımsız package validator (`tools/validate_systems_package.py`) üretildi; her ikisi de PASS verir.
- Internal deterministic QA sonucu `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`; 0 blocking, 4 açık non-blocking review vardır (6H external coverage, 6H platform tool freshness, 6F professional overlay reconciliation, 6E accelerator forward reuse).
- External coverage/current-industry/hidden-prerequisite validation 6H independent Research AI'ye kadar pending; package learner-published değildir.

Ayrıntı: `docs/SYSTEMS_DETAILED_MAP.md`.

## D-059 — GPU / ML / Inference detailed map = GIM-v0
**Durum:** Kabul edildi — 2026-08-26

- 6E final modeli `GIM-v0 — GPU / ML / Inference Detailed Map` oldu.
- Canonical summary `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`; machine-readable dataset `curriculum/decomposition/6e_gpu_ml_inference/` içindedir.
- D14–D22 = 9 Domain / 27 Module / 70 Topic / 143 Skill / 159 Objective / 230 TopicSkillLink.
- 279 prerequisite edge FRDB Pass-B sonrası 247 hard / 32 soft olarak kabul edildi; 2 gerçek dependency olmayan edge kaldırıldı.
- 59 prior Skill (9 Foundation + 50 Systems) clone edilmeden canonical ID ile reuse edildi.
- Hidden math/numerical prerequisites explicit Skill/edge olarak modellendi.
- Stable accelerator/inference concepts ile version/tool-specific CUDA/Triton/serving/NCCL/RDMA capabilities ayrıldı.
- Combined 6C+6D+6E hard graph DAG 467/467; English global hard gate yok.
- 6D `review.6d.accelerator_forward_reuse` resolved edildi ve 6D regression QA tekrar PASS verdi.
- Internal package QA `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`; 0 blocking / 4 non-blocking review.
- Independent external coverage/current-industry/source-quality Research QA 6H'de zorunlu ve pending; package learner-published değildir.

## D-060 — Professional engineering / projects detailed map = PEM-v0
**Durum:** Kabul edildi — 2026-08-27

- 6F final modeli `PEM-v0 — Professional Engineering / Projects Detailed Map` oldu.
- Canonical summary `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`; dataset `curriculum/decomposition/6f_professional_engineering/`.
- D23 = 1 Domain / 9 Module / 27 Topic / 76 Skill / 87 Objective / 110 TopicSkillLink.
- 138 prerequisite edge = 137 hard / 1 soft; 36 cross-package edge.
- 25 prior Skill clone'lanmadan reuse edildi: 8 6C + 12 6D + 5 6E.
- Existing foundation/systems/GPU-inference projects D23 professional workflow evidence ile augment edildi; yeni OSS contribution project ve integrated AI-infrastructure capstone familyası tanımlandı.
- `review.6d.professional_overlay_reconciliation` ve `review.6e.professional_overlay_reconciliation` resolved edildi.
- Internal QA `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`; 0 blocking / 3 open 6F non-blocking review. Independent external Research QA 6H'ye pending; learner publication yapılmadı.

## D-061 — Weakness localization/remediation overlay = WLRM-v0
**Durum:** Kabul edildi — 2026-08-27

- 6G final modeli `WLRM-v0 — Weakness Localization & Remediation Map` oldu.
- Canonical summary `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`; dataset `curriculum/decomposition/6g_weakness_remediation/`.
- Accepted 6C–6F registry'sindeki 543 Skill ve 590 Objective exact covered; 590 Objective-specific remediation route üretildi.
- 6G yeni Skill/Objective identity veya prerequisite edge eklemez; learner weakness/remediation overlay'idir.
- 12 deterministic failure-attribution rule, 15 remediation strategy family ve 6 LearningNeed mapping kabul edildi.
- Invalid/ambiguous/prerequisite-contaminated attempt target negative evidence yazamaz; assisted/provisional failure yalnız hypothesis olabilir.
- First clean post-mastery contradiction `verification_due`; confirmed fresh recheck failure GRE/RVR gate recompute sonrası `remediation_required` açabilir.
- `review_due` weakness değildir; broad Topic/Domain reset ve integrated-project broadcast yasaktır.
- Remediation closure fresh H0 + direct + verified + prerequisite-valid evidence ister; task completion veya manual mastery override closure değildir.
- Learner misconception state curriculum identity'den ayrıdır ve LLM tek başına confirmed state/mastery yazamaz.
- Internal QA `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`; 0 blocking / 4 non-blocking review. Independent external Research QA 6H'ye pending ve zorunludur.

## D-062 — Stage 6 External Research QA = S6ERQA-v0
**Durum:** Kabul edildi — 2026-08-27

- 6H final modeli `S6ERQA-v0 — Stage 6 External Research QA` oldu.
- Canonical çıktı `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`; external evaluator reconciliation `research/6h_external_research_ai_report.md`.
- Üç bağımsız evaluator Stage 6 başlangıç snapshot'ına `PASS WITH REQUIRED CHANGES` verdi; öneriler GNS-v0/KGC-v0/PRG-v0 ile manager tarafından bağımsız reconcile edildi.
- Stable capability independence testini geçen 6 yeni Skill eklendi: NUMA locality/affinity, CUDA async data movement pipeline, speculative decoding trade-off, prefill/decode disaggregation, MoE routing dataflow, expert-parallel sharding.
- Tool/vendor/model özel fast-moving ayrıntılar (ör. DRA API isimleri, CUDA/Triton current mechanisms, MLA/MTP/DualPipe/FlashInfer/NVFP4/CUTLASS/CuTe) otomatik canonical Skill yapılmaz; ayrı learner-state testi geçmiyorsa version-scoped Objective/example/freshness metadata olarak tutulur.
- Final Stage 6 registry: 23 route family / 549 Skill / 608 Objective / 950 prerequisite edge; combined hard graph 549/549 DAG.
- WLRM final registry'nin 549 Skill / 608 Objective'ini exact kapsar; guidance fading + mastered-target reverification-first + AI-scaffold-not-closure guard'ları eklenmiştir.
- D23 final readiness global capstone pass ile component mastery vermez; ayrı attributable component evidence, en az üç materially distinct evidence family ve operations/failure evidence ister.
- 10/10 6H-owned review resolved; `tools/validate_6h_external_reconciliation.py` final PASS.
- AŞAMA 6 tamamlandı. Sonraki numbered step `7A — İngilizce başlangıç ölçümü`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.


## D-063 — English Entry Diagnostic = EED-v0
**Durum:** Kabul edildi — 2026-08-27

- 7A final modeli `EED-v0 — English Entry Diagnostic` oldu.
- Canonical spec `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`; machine-readable blueprint `curriculum/english/7a_entry_diagnostic/blueprint.yaml`; research basis `research/7a_english_entry_diagnostic_research.md`.
- Diagnostic final Stage 6 D01 registry'sindeki 15 Technical English Skill + 15 Objective'i exact profile olarak ölçer; broad `English weak` veya tek broad score canonical learner state değildir.
- Canonical English hard prerequisite DAG 16 edge ile aynen tüketilir; root `skill.english.recognize_core_technical_labels`.
- Diagnostic mastery için daha düşük standart kullanmaz: positive mastery/waiver yalnız GRE-v0 + VDW-v0 normal gate'leriyle oluşur.
- Self-report, certificate claim ve confidence routing/context sinyalidir; mastery/evidence değildir.
- Unknown grammar/vocabulary veya specialist technical knowledge target English failure'ına gizli prerequisite olamaz.
- Invalid, ambiguous, technical-context-contaminated veya prerequisite-unresolved attempt target negative evidence yazamaz; downstream dependent Skills topluca failed yapılmaz.
- 15 diagnostic claim için 15 task family tanımlandı; resource/evaluator trust QAB-v0 + AIV-v0 + GRE-v0 zincirine bağlıdır.
- 7A final CEFR level atamaz; `cefr_alignment_status=pending_7B`, `cefr_level=null`. `review.6c.english.cefr_alignment` açık kalır ve 7B'nin ownership'indedir.
- Independent deterministic 7A validator PASS: 15/15 Skills, 15/15 Objectives, 16/16 English hard edge, 15/15 task family; final Stage 6 regression PASS.
- Sonraki numbered step `7B — A1/A2/B1/B2+ teknik hedefleri`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`.

## D-064 — Technical English CEFR Progression = TECP-v0
**Durum:** Kabul edildi — 2026-08-27

- 7B final modeli `TECP-v0 — Technical English CEFR Progression` oldu.
- Canonical spec `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`; machine-readable alignment `curriculum/english/7b_cefr_progression/alignment.yaml`; research basis `research/7b_technical_english_cefr_research.md`.
- Accepted D01 15 Skill identity'si değişmeden exactly-one base CEFR-aligned Technical English anchor aldı: 5 A1 / 5 A2 / 5 B1.
- Canonical 16 English hard prerequisite edge'in tamamında band monotonicity PASS; CEFR metadata yeni prerequisite edge üretmez.
- CEFR alignment learner mastery state, raw score, Council of Europe certification veya general-English level claim değildir. Canonical source exact Skill/Objective evidence + GRE/VDW state'tir.
- Current D01 text-first profile olduğu için `English B1/B2`, `CEFR-certified` veya `official level` gibi unqualified claims yasaktır; yalnız qualified Technical English profile wording kullanılabilir.
- Pre-A1 zero-entry scaffold context'i olabilir fakat ayrı target gate/failure label/technical blocker değildir.
- B2+ yeni mastery bandı veya silent Skill expansion değildir; yalnız `documentation_navigation`, `read_definition_and_constraint`, `read_procedure_sequence`, `ask_clarifying_technical_question` için semantic boundary içinde bounded professional evidence-depth extension'dır.
- `simple/basic/bilingual` semantics taşıyan mevcut Skills B2+ görünümü için otomatik genişletilemez; daha geniş capability gerekirse GNS-v0/KGC-v0 normal capability review gerekir.
- 10 controlled CEFR scale-family ref authoring/provenance metadata'sı olarak kullanılır; descriptor family refs Skill identity/evidence rule değildir.
- `review.6c.english.cefr_alignment` 7B tarafından `CONTEXT_ONLY_NO_SPLIT` olarak resolved edildi; yeni Skill/Objective/prerequisite gerekmedi.
- 7A EED-v0 artifact'ındaki `pending_7B` alanı tarihsel handoff kontratı olarak korunur; current CEFR alignment source TECP-v0'dır.
- Final Stage 6 regression, 7A EED regression ve independent 7B validator PASS.
- Sonraki numbered step `7C — Günlük English bileşeni`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`.

## D-065 — Daily English Component Policy = DECP-v0
**Durum:** Kabul edildi — 2026-08-27

- 7C final modeli `DECP-v0 — Daily English Component Policy` oldu.
- Canonical spec `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`; machine-readable policy `curriculum/english/7c_daily_component/policy.yaml`; research basis `research/7c_daily_english_component_research.md`.
- Technical English ayrı daily budget/planner değildir; existing `LearningNeed → TaskCandidate → PBR-v0 → PRG-v0 → common capacity → Attempt/Artifact → Evidence` pipeline'ını kullanır.
- Fixed daily English minute, percentage/share, completion quota, streak gate, missed-day failure veya debt yoktur.
- Active study day'de open + eligible + interpretable Technical English need ve safe candidate varsa en az bir English TaskCandidate üretilir; candidate generation selection/mastery garantisi değildir.
- Normal parallel-track progress P3'tür. P0/P1 integrity/repair work dar capacity'de English'i o gün dışarıda bırakabilir.
- Repeated eligible omission yeni sabit gün eşiği yaratmadan existing PBR `track_balance_pressure` + `starvation_pressure` (`none | watch | promote`) semantics'iyle scheduling pressure üretir. Promotion eligibility/capacity/evidence guard'larını aşamaz.
- Daily task mix sabit category yüzdeleriyle değil exact Skill/Objective state'inden seçilir: new/continue learning, retention, remediation/verification, production ve reinforcement/B2+.
- Distributed/retrieval-practice research yönü kullanılır fakat 7C universal optimal interval, daily minute, percentage veya fixed checkpoint count iddia etmez. Spacing ownership RVR-v0'da kalır; empirical calibration AŞAMA 18'e aittir.
- Unknown grammar/vocabulary clean production failure üretemez; remediation exact Skill/Objective seviyesinde kalır; same failed item memorization closure değildir.
- Corrective feedback learning için kullanılabilir fakat feedback-assisted revision independent mastery evidence değildir; gerekli closure fresh H0/direct/verified attempt ister.
- B2+ yalnız TECP-v0 bounded evidence-depth extension'dır; synthetic B2+ completion veya silent Skill expansion yoktur.
- 7C yalnız English-track scaffold/cadence/task-mix behavior'ını tanımlar. Technical curriculum içindeki bilingual/English integration 7D'ye; learner-facing English mastery/CEFR behavior 7E'ye bırakılmıştır.
- Stage 6 regression + accepted 7B regression + independent 7C validator PASS.
- D-050 POST living-memory, external-memory ve repo-wide stale-reference audit ile kapanış zorunludur.

Ayrıntı: `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`.

## D-066 — Technical English Integration Policy = TEIP-v0
**Durum:** Kabul edildi — 2026-08-27

- 7D final modeli `TEIP-v0 — Technical English Integration Policy` oldu.
- Canonical spec `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`; machine-readable policy `curriculum/english/7d_technical_integration/policy.yaml`; research basis `research/7d_technical_english_integration_research.md`.
- Task/resource/prompt language target construct ile aynı şey değildir; technical ve English construct'leri ayrı attribution taşır.
- Exactly four integration mode kabul edildi: `technical_only_localized`, `technical_with_english_exposure`, `dual_target_integrated`, `english_primary_technical_context`.
- Technical-only task'lerde non-target English Turkish/bilingual/gloss support ile neutralize edilebilir; English global hard gate veya English mastery attribution oluşmaz.
- Authentic English exposure tek başına English mastery değildir; construct-essential language ya ready olmalı ya construct-valid support ile neutralize edilmelidir.
- Dual-target task her iki target/prerequisite family'yi explicit taşır ve component-level rubric/evidence ister; global task PASS/FAIL component'lere broadcast edilemez.
- English-primary technical-context task'ta specialist technical ignorance English failure'a dönüşemez; context ready/controlled/scaffolded olmalıdır.
- 7 contamination reason-code ve bidirectional contamination guard kabul edildi: unknown English technical negative evidence'ı, hidden specialist technical context English negative evidence'ı geçersiz kılar.
- Scaffold evidence + task validity driven ve reversible'dır; fixed Turkish/English ratio, fixed fading day count veya fixed integration quota yoktur.
- One integrated task multiple LearningNeed'e hizmet edebilir; duration bir kez sayılır, priority track sayısıyla çarpılmaz, completion bütün need/evidence'ı otomatik kapatmaz.
- Authentic/translated/glossed/AI-generated support QAB/AIV/prerequisite/freshness ve semantic-integrity guard'larını aşamaz.
- D01 canonical 15 Skill identity'si ve Stage 6 graph değişmedi; 7D yeni Skill/Objective/prerequisite edge üretmedi.
- Independent 7D QA: 49/49 check PASS; 15 canonical English Skill / 4 mode / 15 safety fixture / 7 contamination code.
- Sonraki numbered step `7E — English mastery`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`.

## D-067 — Technical English Mastery Profile = TEPM-v0
**Durum:** Kabul edildi — 2026-08-27

- 7E final modeli `TEPM-v0 — Technical English Mastery Profile` oldu.
- Canonical spec `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`; machine-readable policy `curriculum/english/7e_mastery_profile/policy.yaml`; research basis `research/7e_english_mastery_profile_research.md`.
- 7E yeni mastery engine değildir: exact English Skill/Objective mastery GRE-v0, retention RVR-v0, prerequisite PRG-v0, prior-knowledge validation VDW-v0, remediation WLRM-v0 tarafından sahiplenilmeye devam eder.
- D01 exact 15 Skill identity'si değişmedi; yeni Skill/Objective/prerequisite edge veya numeric English mastery formula üretilmedi.
- 8 learner-facing derived Skill presentation state kabul edildi: not-yet-evidenced, developing-with-support, developing-independent, confirmed-current, confirmed-review-due, confirmation-verification-due, remediation-required, prerequisite-unresolved.
- `review_due` mastery veya base-band demotion değildir; `verification_due` ilk contradiction sonrası current uncertainty gösterir fakat mastery'yi anında silmez.
- Current complete base profile yalnız qualified Technical English A1/A2/B1 summary'dir; exact uneven Skill detail'i her zaman source of truth'a trace edilebilir.
- General-English `You are B1`, official CEFR/certification claim, numeric CEFR average veya compensatory English percentage yasaktır.
- Confirmed remediation current complete band'ı real current evidence'dan yeniden türetebilir; historical higher-band confirmation provenance olarak korunur.
- B2+ aggregate mastery/completion değildir; TECP-v0'nun exact 4 extension-eligible Skill'i için named professional extension evidence olarak gösterilir.
- Assisted, answer-revealed, provisional veya contaminated performance independent-confirmed English mastery üretmez; later assisted practice daha önceki clean mastery'yi tek başına silmez.
- TEIP-v0 integration semantics korunur: technical-only/exposure task English mastery broadcast yapmaz; dual-target evidence component-specific; hidden specialist technical context English failure'a dönüşemez.
- Independent 7E QA: 54/54 check PASS; 15 Skill / 8 presentation state / 3 base band / 4 B2+ extension Skill / 18 safety fixture / 17 reason-code.
- AŞAMA 7 tamamlandı. Sonraki numbered step `8A — Bilgi mimarisi`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`.


## D-068 — UX Information Architecture = UXIA-v0
**Durum:** Kabul edildi — 2026-08-27

- 8A final modeli `UXIA-v0 — Adaptive Learning Information Architecture` oldu.
- Primary semantic shell exactly four destination kullanır ve sıra sabittir: `Today → Learn → Progress → Profile`; normal start `Today`'dir.
- Assessment, Technical English, AI Tutor/chat, mastery/retention/remediation/weakness ayrı top-level destination değildir; kendi workflow/context/home'larında görünür.
- Shared semantic detail surfaces: Topic, Skill, planner explanation, assessment report, Technical English profile, learning history. Aynı Skill için Learn/Progress altında farklı truth kopyaları üretilmez.
- Task Runner ve assessment session focused flow'dur; shell'den ayrılabilir fakat safe pause/exit ve deterministic semantic return gerekir.
- UI hierarchy prerequisite truth değildir; planner reason PDT-v0 trace'ten, English profile TEPM-v0'dan, mastery GRE/RVR pipeline'ından türetilir.
- Progress career %, elapsed time, streak, task completion veya numeric/general CEFR'i primary mastery truth olarak kullanamaz.
- Compact/expanded navigation component'i 8A'da kilitlenmedi; semantic destination identity/order window size ile değişmez.
- Final visual hierarchy 8B–8G'ye, implementation 9–10'a bırakıldı.
- Independent validator: 49/49 PASS; 4 primary destination / 6 shared detail / 2 focused flow / 17 information object / 7 research source.

Ayrıntı: `docs/INFORMATION_ARCHITECTURE_SPEC.md`.


## D-069 — Today Home UX = THUX-v0
**Durum:** Kabul edildi — 2026-08-28

- 8B final modeli `THUX-v0 — Today Home UX` oldu.
- Canonical spec `docs/TODAY_HOME_SCREEN_SPEC.md`; machine-readable contract `ux/8b_today_home/home.yaml`; research/contract synthesis `research/8b_today_home_research.md`.
- Today ana soruyu action-first biçimde cevaplar: `Bugün şimdi ne yapmalıyım?`; Today canonical planner/state truth'un projection'ıdır, ikinci planner/mastery engine değildir.
- Semantic content hierarchy `primary_action → day_plan_context → remaining_plan → conditional attention_context → supporting_navigation` olarak kilitlendi; bu sıra fixed card/pixel geometry değildir.
- Dominant action precedence data-recovery safety → valid revalidated resume → selected next PlannedTask → loading/replanning → valid empty/capacity-limited → recoverable error şeklindedir.
- Today kuyruğu yalnız current selected `PlannedTask`'ları temsil eder; bütün LearningNeed/TaskCandidate evreni, eski gün backlog'u veya debt listesi değildir. Blocked dependent work startable gösterilemez.
- Daily capacity hard time budget context'idir, progress/mastery değildir. Today current-day override sağlayabilir ve override replan tetikler; persistent capacity preference Profile-owned kalır.
- Task purpose/activity/track ayrımı korunur; duration estimate'tir ve mastery signal değildir; task completion mastery/failure çıkarımı yapamaz.
- Planner reason snippet'i yalnız PDT-v0 trace facts'ten türetilir: overview'da bir primary + en fazla bir materially useful supporting reason; full explanation shared `planner_explanation` surface'indedir.
- Assessment Today'de contextual PlannedTask/attention olarak görünür; daily quota/permanent gradebook yoktur ve assessment score broad mastery truth değildir.
- Technical English common capacity içinde contextual track'tir; separate budget/fixed quota/streak/debt/general-CEFR claim yoktur.
- SRR-v0 korunur: missed day backlog/debt/failure üretmez; Today current state'ten fresh plan gösterir.
- Empty/loading/replanning/offline/AI-degraded/recovery states semantically ayrılır; `no task today` all-mastered/professional-ready anlamına gelmez.
- 8B final visual design, fixed card count/pixels, task-runner choreography, assessment interaction, Skill/progress visualization ve implementation technology'yi kilitlemez; sahipleri 8C–10/17–18'dir.
- Independent 8B QA: **90/90 PASS**; 5 semantic content region / 12 semantic state / 11 forbidden Home anti-pattern. Stage 6, Stage 7, accepted 8A ve external-memory regressions PASS.
- Sonraki numbered step `8C — Günlük çalışma akışı`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TODAY_HOME_SCREEN_SPEC.md`.


## D-070 — Daily Working Flow / Task Runner UX = TRUX-v0
**Durum:** Kabul edildi — 2026-08-29

- 8C final modeli `TRUX-v0 — Task Runner & Daily Working Flow UX` oldu.
- Canonical spec `docs/DAILY_WORKING_FLOW_SPEC.md`; machine-readable contract `ux/8c_daily_working_flow/flow.yaml`; research/contract synthesis `research/8c_daily_working_flow_research.md`.
- Task Runner bir execution surface'tir: `Attempt`, `Artifact`, assistance metadata ve provenance üretir; planner, mastery engine, prerequisite engine, evidence evaluator veya motivational scoring device değildir.
- Daily working session emergent ve ungraded'dır: required task count, required duration, completion percentage, session score/grade yoktur. `plan_exhausted` gün başarılı, `user_stopped` gün başarısız anlamına gelmez.
- 8C shared focused-flow frame'i (`entry_revalidation`, `safe_pause_and_exit_availability`, `resume_revalidation`, `capacity_and_replan_interaction`, `degraded_and_recovery_behavior`, `deterministic_semantic_return`) tek yerde tanımlar; frame hem `task_runner_flow` hem `assessment_session_flow` tarafından devralınır. Interior choreography yalnız `task_runner_flow` için 8C'ye, assessment interior 8D'ye aittir.
- Task-run lifecycle `enter → orient → work → submit → resolve → transition` olarak kilitlendi; `pause | abandon | recover` non-linear geçişlerdir.
- Entry ve resume revalidation deterministiktir; prerequisite veya content-version geçerliliği bypass edilemez. Başarısız entry/resume negative evidence değildir ve failure mesajı olarak sunulmaz.
- Orientation, bağımsız çalışmadan önce task'in assistance policy'sini açıklamak zorundadır; kullanıcı aldığı yardımın evidence yorumunu nasıl değiştirdiğini sonradan öğrenmez.
- Assistance teaching/practice işinde daima talep edilebilir; bağımsızlığı zorlamak için yardım kısıtlanamaz. Escalation yalnız talep üzerine `H1 → H2 → H3 → H4` ilerler; ilk yanlışta otomatik solution reveal ve H3 scaffold'ı hint diye etiketleme yasaktır. H3/H4 öncesi consequence, ceza dili yerine ölçüm dili ile açıklanır.
- Solution exposure sonrası aynı item yalnız practice olarak tekrarlanabilir; mastery path olarak sunulamaz. Runner `requires_independent_recheck` bayrağını yükseltir fakat recheck'i **zamanlamaz**; sahibi planner + remediation/retention pipeline'larıdır.
- Submit attempt'i dondurur; submit sonrası açıklama önceki tamamlanmış attempt'i geriye dönük kirletmez. Attempt olmayan task evidence üretmez ve bu eksik sonuç değildir.
- Artifact provenance **sorulur, çıkarsanmaz**. Dürüst beyan ucuz ve cezasızdır; şüpheye dayalı sessiz state düşürme, cheating suçlaması ve dürüst cevabı zorlaştırma yasaktır. Bilinmeyen köken tahmin edilmez, `unknown_provenance` olarak kaydedilir.
- Pause üç sınıfa ayrılır: durable `checkpoint_pause` (ResumeContext üretir), transient `mid_segment_pause` (saved progress olarak sunulamaz) ve marked `high_stakes_pause` (uzun aradan sonra sessizce independent evidence olarak sürdürülemez).
- Stop her zaman ve tek deliberate action ile erişilebilirdir; debt, streak loss veya catch-up yükümlülüğü üretmez. Guilt framing ve streak-kaybı uyarısı yasaktır.
- In-flight run replan tarafından yok edilmez; replan completed evidence'ı korur ve yalnız unstarted işi yeniden çözer. Continuity izinlidir fakat sonraki task daima planner'ın recomputed current selection'ıdır; cached local list üzerinden ilerleme yasaktır.
- AI evaluator yoksa open-ended attempt `evaluation_pending` olur: evidence yazılmaz, otomatik pass/fail verilmez ve durum görünür kalır. Offline'da locally runnable iş tam çalışır; sahte tamamlanmış remote step gösterilmez.
- Dört `TEIP-v0` integration mode frame değiştirmeden desteklenir; `dual_target_integrated` component-separable kalır, overall pass broadcast yasaktır; flow içinde English quota/streak/debt yoktur.
- 17 runner semantic state metinle ayırt edilebilir; renk veya motion tek başına state taşıyamaz.
- 8C final visual design, fixed step count/session length/daily task count/pixel geometry, assessment interior, Skill/progress visualization, code-runner entegrasyonu ve implementation technology'yi kilitlemez; sahipleri 8D–10/14/16–18'dir.
- Independent 8C QA: **123/123 PASS**; 6 lifecycle phase / 17 semantic state / 3 pause class / 13 forbidden flow anti-pattern. Stage 6, Stage 7, accepted 8A, accepted 8B ve external-memory regressions PASS.
- Sonraki numbered step `8D — Sınav UX`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/DAILY_WORKING_FLOW_SPEC.md`.


## D-071 — Assessment Session UX = ASUX-v0
**Durum:** Kabul edildi — 2026-08-29

- 8D final modeli `ASUX-v0 — Assessment Session & Result UX` oldu.
- Canonical spec `docs/ASSESSMENT_SESSION_UX_SPEC.md`; machine-readable contract `ux/8d_assessment_session/session.yaml`; research/contract synthesis `research/8d_assessment_session_research.md`.
- Assessment session bir evidence-collection workflow'udur; gradebook, score-based mastery authority, pass/fail verdict veya ikinci state engine değildir.
- **Tek bir assessment session interior** üç scope'un (`daily_micro` / `weekly_blueprint` / `monthly_capability`) tamamına hizmet eder. `assessment_scope` yalnız gösterilen context'tir; ayrı ekran ailesi veya ayrı kural seti değildir ve weekly/monthly etiketi evidence'a ek ağırlık kazandırmaz.
- Submission birimi **atomic evidence boundary**'dir (item veya testlet); boundary asla bölünmez, ortadan kesilmez ve kısmen puanlanmaz. `dependency_group` tek evidence group gibi davranır ve üyeleri birbirini doğrulamaz.
- Submit edilen boundary **dondurulur**: revisit, edit veya resubmit edilemez; submit sonrası feedback önceki attempt'i geriye dönük kirletmez. Açık blok içindeki submit edilmemiş boundary'ler serbestçe gezilebilir ve serbest sırayla cevaplanabilir.
- Skip meşrudur: `unsubmitted_boundary != incorrect`. Skip negative evidence, penalty veya failure değildir; ilgili measurement need current state'te açık kalır.
- `independence_mode` (mastery/verification için varsayılan `h0_required`) ve allowed-tools policy **cevap vermeden önce** açıklanır. Objective gerçekten terminal/debugger/profiler kullanımını ölçüyorsa izin verilen tool kullanımı H0'ı bozmaz. Undisclosed independence rule yasaktır.
- Assessment sırasında yardım engellenmez ve yardım istemek negative evidence değildir. Consequence ölçüm dilinde açıklanır: H1/H2 assisted olur ve independent mastery evidence üretmez; H3/H4 practice-only / solution-exposed olur ve fresh unseen item gerektirir. Mode conversion açık yapılır, ihlal olarak çerçevelenmez ve session öğretime devam edebilir. Session `requires_independent_recheck` yükseltir fakat recheck'i zamanlamaz.
- Pause failure, assistance veya mastery signal değildir. Resume'da unresolved slot tam beş koşuldan biri gerçekleşirse fresh item ile recompose edilir: solution/explanation exposure, item version/validation değişimi, anlamlı prerequisite değişimi, uzun ara nedeniyle freshness güvenilirliğinin kaybı, kullanıcı reset/alternative talebi. Recomposition completed valid evidence'ı silmez ve recomposed slot failure retry'ı olarak sunulmaz.
- Incomplete session meşrudur: submitted valid attempt'ler normal evidence üretir, unsubmitted item incorrect sayılmaz, unresolved slot mastery penalty vermez, session `partial` olabilir ve exam debt oluşmaz. Missed cycle failure değildir.
- Item dispute ucuz ve cezasızdır: user report tek başına `item_invalid` yapmaz, ilgili evidence contested tutulur, critical transition yalnız contested item'a dayandırılmaz, validator/answer-key/rubric yeniden kontrol edilir ve item QA'ya flag'lenir. Dispute bir undo button değildir ve kullanıcı state'ine zarar vermez.
- Evaluator status `verified | provisional | invalid` olarak sunulur. Provisional her yerde provisional etiketlenir; bilgilendirebilir ve confirmation need açabilir fakat settled verdict gibi gösterilemez ve tek başına critical transition'ı belirleyemez. Invalid item ne kredi ne ceza üretir ve slot'u kapatmış sayılmaz.
- Result surface **semantic**'tir ve `what just changed` sorusunu cevaplar; altı family kullanır: `confirmed_capabilities`, `verification_needed`, `persistent_targeted_gaps`, `retention_revalidated`, `not_reliably_measured`, `plan_changes`. Pass/fail banner, yüzde/harf notu, geçme eşiği, `8/10 = mastered` kuralı, broad domain score, career percentage ve başkalarıyla karşılaştırma yasaktır.
- Raw doğru/yanlış sayısı gösterilebilir fakat açıkça informational'dır ve state'ten ayrılır; verdict değildir.
- `not_reliably_measured` **first-class** bir sonuçtur ve boş değilse daima gösterilir. Kaynakları: invalid item, provisional evaluation, assisted attempt, solution-exposed attempt, contested item, unsubmitted slot. Bu family gizlenemez ve incorrect kategorisine katlanamaz.
- State-change iddiası yalnız canonical state gerçekten değiştiyse yapılır. Mastered bir Skill'deki ilk clean contradiction `verification_due` açar; instant unmastery veya demotion event değildir. Hiçbir şey değişmediyse bu düz biçimde söylenir; motivasyon için sahte ilerleme iddiası üretilmez.
- In-session result view (`what just changed, right now`) ile Progress-owned `assessment_report` (`what happened across sessions over time`) rolleri ayrıdır; ikisi de mastery sahibi değildir ve çelişkili truth sunamaz.
- `TRUX-v0` shared focused-flow frame devralınır, yeniden tanımlanmaz. Diagnostics `task_runner_flow` işidir; 8D diagnostic execution'ı sahiplenmez.
- AI evaluator yoksa attempt korunur ve `evaluation_pending` işaretlenir; evidence yazılmaz, otomatik pass/fail verilmez. Offline'da deterministik değerlendirilebilir item'lar tam çalışır; remote evaluation gerektirenler fail değil pending olur.
- 19 assessment session semantic state metinle ayırt edilebilir; renk veya motion tek başına anlam taşıyamaz.
- 8D Stage 4 blueprint policy'sini değiştirmez; passing threshold, percentage grade, fixed question count, countdown clock veya fixed geometry kilitlemez. Sahipleri 8E–10/13–18'dir.
- Independent 8D QA: **107/107 PASS**; 3 assessment scope / 6 result family / 5 recomposition condition / 19 semantic state / 15 forbidden session anti-pattern. Validator ayrıca mutation test ile doğrulandı. Stage 6, Stage 7, accepted 8A, 8B, 8C ve external-memory regressions PASS.
- Sonraki numbered step `8E — Skill/progress/weakness UX`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/ASSESSMENT_SESSION_UX_SPEC.md`.
