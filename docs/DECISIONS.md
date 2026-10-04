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


## D-072 — Progress / Skill / Weakness UX = SPWX-v0
**Durum:** Kabul edildi — 2026-08-29

- 8E final modeli `SPWX-v0 — Progress, Skill State & Weakness UX` oldu.
- Canonical spec `docs/PROGRESS_SKILL_UX_SPEC.md`; machine-readable contract `ux/8e_progress_skill_weakness/progress.yaml`; research/contract synthesis `research/8e_progress_skill_weakness_research.md`.
- Progress canonical evidence state'in projection'ıdır; mastery engine, score, competence yüzdesi, career-completion tracker veya streak dashboard değildir.
- **Tek Skill presentation vokabüleri:** `TEPM-v0`nin 8 derived state'i ve precedence'ı bütün Skill'lere genelleştirildi. `TEPM-v0` değişmedi ve tek genel kuralın English örneği hâline geldi; technical Skill'ler için ikinci bir etiket sistemi üretilmedi. Dokuzuncu primary state eklenemez.
- Türkçe Skill etiketleri kilitlendi: `Henüz Kanıt Yok`, `Destekle Gelişiyor`, `Bağımsız Gelişiyor`, `Doğrulanmış`, `Doğrulanmış · Tekrar Zamanı`, `Doğrulanmış · Yeniden Kontrol Gerekli`, `Pekiştirme Gerekli`, `Ön Koşul Bekliyor`. Internal ID'ler sabittir; sonraki microcopy çalışması yalnız semantics korunursa sözcük rafine edebilir.
- **Multi-axis truth sıralanır, çökertilmez.** Bir Skill aynı anda mastery, retention, prerequisite ve weakness ekseni taşır. Primary presentation state bu eksenleri sıralar; yerlerini almaz. `skill_detail` her ekseni ayrı ayrı incelenebilir tutmak zorundadır ve headline ile çelişen bir ekseni atamaz.
- **`at_risk` bir attention qualifier'dır**, dokuzuncu primary state değildir ve sessizce düşürülmez. `TEPM-v0`nin exactly-8 kontratı bozulmadan RVR-v0'nun `at_risk` sinyali korunur; mixed/repeated retention signal olarak açıklanır, geçen süre olarak değil.
- **Topic UI etiketleri kilitlendi** (`TSM-v0` §2 bu yetkiyi açıkça 8E'ye devretmişti): `Kilitli`, `Hazır`, `Öğreniliyor`, `Öğrenildi`, `Tekrar Gerekebilir`, `Pekiştirme Gerekli`. Internal state kimlikleri değişmedi. `weakening` bilinçli olarak kayıp değil fırsat dili ile yazıldı. Topic state derived orchestration'dır; prerequisite iddiası değildir, Skill ortalaması değildir ve Topic yüzdesi yoktur.
- `progress_overview` iki yarıdan oluşur: `demonstrated_capability_inventory` ve `attention_set`. Attention altı gerekçeye göre gruplanır, backlog/debt/to-do sayacı değildir ve planner priority üretmez.
- **Progress sayabilir, puanlayamaz.** Count'lar yalnız açıkça inventory olarak etiketlenerek gösterilir; hiçbir count competence ima etmek için total'e bölünmez. Mastery yüzdesi, competence ratio, `confirmed/total`, career bar, broad domain score, level/rank/tier/badge ekonomisi ve internal mastery heuristic değeri yasaktır.
- `skill_detail` tek paylaşılan Skill yüzeyidir; Learn ve Progress'ten aynı truth görünür. Objective-level gap Skill state'i tarafından gizlenemez; evidence summary independent/assisted/provisional/invalid ayrımını korur ve tek sayıya indirgenmez.
- **Weakness sunumu:** yalnız `supported` ve `confirmed` weakness olarak gösterilir. `hypothesis` en fazla açık soru olarak görünebilir, asla eksiklik olarak değil; AI-önerili hypothesis confirmed gibi gösterilemez. Localization korunur; domain'e yukarı veya dependent'lara aşağı yayılım yasaktır.
- **`remediation_task_completed != remediation_closed`.** Remediation ancak canonical closure gerçekleştiğinde — fresh, context-diverse, H0, direct, verified, prerequisite-valid evidence — kapalı gösterilir. Weakness dili located gap'i tarif eder, kullanıcıyı değil.
- `technical_english_profile` `TEPM-v0` semantiğini değiştirmeden sunar: qualified A1/A2/B1 base profile, first-class uneven per-Skill detail, bounded B2+ per-capability extension, historical band provenance. General/official CEFR claim, certification claim, numeric aggregate ve `B2+ complete` iddiası yasaktır; English technical gate olarak sunulamaz.
- `learning_history` anlamlı learning/assessment/review/remediation olaylarının kronolojisidir. Streak calendar, contribution graph veya attendance heatmap değildir; katılım başarı olarak gösterilmez ve timeline boşluğu failure olarak işaretlenmez.
- `assessment_report` 8D'den devralınan longitudinal yüzeydir ve `ASUX-v0` semantic family'lerini aynen taşır. Mastery sahibi değildir; geçmiş session'ları score, grade veya competence trend line hâline getiremez. `not_reliably_measured` first-class kalır ve provisional etiketli kalır.
- `review_due` nötr bir planlı fırsattır: unutulmuş demek değildir, confirmed state'i düşürmez ve decay/error gibi stillendirilemez. `verification_due` dürüst current uncertainty bildirir; confirmed history'yi silmez ve demotion event değildir. Hiçbir görsel severity canonical state'in iddia etmediği bir ağırlığı ima edemez.
- 10 Progress domain semantic state metinle ayırt edilebilir. `empty_no_evidence_yet` meşru bir başlangıçtır ve failure değildir; `empty_no_attention_needed` her şeyin mastered veya professional-ready olduğunu ima etmez.
- Degraded davranış: offline'da locally derived state tam görünür ve remote-bağımlı veri sıfır olarak değil `unavailable` olarak etiketlenir; AI olmadan Progress tamamen kullanılabilir; recomputation sırasında stale projection current gibi sunulamaz; data recovery normal sunumu geçersiz kılar ve sessiz reset yasaktır.
- 8E hiçbir mastery/retention/prerequisite/weakness/assessment algoritmasını değiştirmez ve visual design system, geometry, implementation technology veya threshold calibration kilitlemez; sahipleri 8F–10/16–18'dir.
- Independent 8E QA: **128/128 PASS**; 6 owned surface / 8 Skill presentation state / 6 Topic state / 10 semantic state / 14 forbidden Progress anti-pattern. Validator, 8 state ve precedence'ı doğrudan `curriculum/english/7e_mastery_profile/policy.yaml` ile, Topic state'lerini `TOPIC_STATE_MACHINE.md` ile, weakness lifecycle'ını `WLRM` ile ve report family'lerini 8D `session.yaml` ile çapraz doğrular; ayrıca mutation test ile sınandı. Stage 6, Stage 7, accepted 8A, 8B, 8C, 8D ve external-memory regressions PASS.
- Sonraki numbered step `8F — Tasarım sistemi`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/PROGRESS_SKILL_UX_SPEC.md`.


## D-073 — Visual Design System = VDSX-v0
**Durum:** Kabul edildi — 2026-08-29

- 8F final modeli `VDSX-v0 — Visual Design System` oldu.
- Canonical spec `docs/DESIGN_SYSTEM_SPEC.md`; machine-readable contract `ux/8f_design_system/design_system.yaml`; research/contract synthesis `research/8f_design_system_research.md`.
- Design system bir **expression layer**'dır. Canonical state'in iddia etmediği hiçbir anlamı, severity'yi, aciliyeti, sıralamayı veya hiyerarşiyi ekleyemez: `visual_severity <= canonical_severity`.
- **Severity kuralı kilitlendi.** Tone, bir state'in *ne anlama geldiğinden* atanır; ne kadar alarm verici hissettirdiğinden değil. Her kabul edilmiş state'in tam olarak bir declared tone'u vardır ve hiçbir yüzey farklı bir tone seçemez.
- Tam altı tone: `neutral`, `active`, `positive_confirmed`, `attention`, `pending_unresolved`, `system_fault`. `positive_confirmed` sakin olup kutlayıcı değildir; `attention` "yapılacak bir şey var" demektir, "kötü yaptın" değil; `pending_unresolved` ne pass ne fail'dir.
- **Hiçbir learning state `system_fault` tone'u kullanamaz.** Bu tone yalnız gerçek teknik arızalara ayrılmıştır ve tam olarak iki state'e izinlidir: `error_recoverable` ve `data_recovery_required`. Alarm görünümüne izinli tek tone budur.
- **Attention grubunda görünmek tone'u yükseltmez.** Gruplama organizasyondur, tone valans'tır. Bu sayede `confirmed_review_due` Progress attention set'inde yer alsa bile `neutral` kalır.
- 8A–8E'nin 46 surface state'inin tamamı, 8 Skill presentation state, 6 Topic state ve 4 attention qualifier tone'a eşlendi; eksik veya uydurulmuş state yoktur. `confirmed_review_due` ve Topic `weakening` bilinçle `neutral`'dır; `stopped_no_penalty`, `resume_invalidated`, `slot_recomposed`, `capacity_zero`, `capacity_too_small_no_candidate`, `empty_no_evidence_yet`, `not_yet_evidenced` ve `prerequisite_unresolved` için non-negative tone zorunludur.
- Typography 8 role kullanır ve technical content için `mono` role içerir. Type scale ve spacing rhythm açıkça **product default**'tur, bilimsel değer değildir ve 17–18 için calibration girdisidir. Metin scalable birimlerdedir, sistem font boyutunu onurlandırır ve %200 metin boyutunda kullanılabilir kalır; state label'ı body içerikten önce truncate olamaz — state bilgidir, süs değil.
- **Türkçe casing korundu.** Locale-naive case transform yasaktır: `i → İ` ve `I → ı` garanti edilemiyorsa dönüşüm yapılamaz. Kilitlenmiş SPWX-v0 etiketleri yazıldığı gibi render edilir, hiçbir component all-caps zorunlu kılamaz ve seçilen typeface'ler `ı İ ş Ş ğ Ğ ç Ç ö Ö ü Ü` karakterlerini `mono` role dahil tam desteklemek zorundadır.
- Renk yalnız **semantic role** olarak belirtilir; ürün kodunda ham değer bulunmaz. Kontrast WCAG 2.2'ye çapalandı: gövde/label metni ≥ 4.5:1 (1.4.3), büyük metin ve state indicator/UI component ≥ 3:1 (1.4.3 / 1.4.11); focus göstergesi her yüzeyde görünür. Kontrast **tema başına ölçülür**; dark theme light'ın inversiyonu değildir.
- Renk asla tek taşıyıcı değildir (WCAG 1.4.1): her tone bir metin etiketi ve renk-dışı bir ayırt edici ile birlikte gelir; `review_due` ve `verification_due` hue'ya bağlı olmadan ayırt edilebilir.
- Spacing 4dp temel ritim ve `4/8/12/16/24/32/48` adım seti kullanır. Dokunma hedefi **en az 48dp**'dir; WCAG 2.2 2.5.8'in 24×24 tabanı yerine daha katı Android/Material kuralı benimsenmiştir. Focused flow'da exit/pause, shell görsel olarak bastırılmış olsa bile tam hedef boyutunu korur.
- İkonlar destekleyicidir ve asla tek taşıyıcı değildir; state ikonu daima metin etiketiyle görünür, bir semantic state için tek metafor kullanılır ve anlamlı ikonlar 3:1 kontrast kuralına uyar.
- **Motion'ın ikna edici rolü yoktur.** Dört purpose class (`orientation`, `continuity`, `feedback`, `state_change`) tanımlıdır; süre bantları product default'tur. Yasak: countdown/timer animasyonu, urgency pulse, **task completion'da ödül animasyonu** (çünkü `task_completed != mastery_confirmed`), learning state için decay/düşme animasyonu, streak/combo/score animasyonu, tek state-change göstergesi olarak motion ve focused flow çıkışını engelleyen motion. Kutlama yalnız canonical state gerçekten değiştiğinde ve orantılı biçimde yapılabilir.
- Platform reduced-motion ayarı onurlandırılır ve motion azaltıldığında hiçbir bilgi kaybolmaz.
- 18 component'lik vocabulary tanımlandı; her component kabul edilmiş bir yüzeye ve sahibi bir spec'e eşlenir. **Hiçbir component surface veya state icat edemez.**
- **Progress-bar şekli kısıtlandı:** yalnız bounded ve factual, competence olmayan nicelikler için (assessment session içindeki konum, task segment içindeki konum). Mastery/capability, competence ratio, career/curriculum completion, `confirmed/total`, Topic/Domain yüzdesi ve English level için kullanılamaz. Gauge, dial, level meter, rank badge, tier emblem, streak counter, calendar heatmap, leaderboard ve score trend line component şekli olarak yasaktır.
- Somut hex paleti 8F'de kilitlenmedi; token role'leri, tone eşlemeleri ve kontrast kısıtları kilitlendi. Palet 8G/10'da üretilir ve shipping öncesi tema başına §7'ye karşı ölçülmek zorundadır. Ölçülmemiş renk değerini sabitleyen bir spec, kısıtı sabitleyenden zayıftır.
- 8F hiçbir destination, surface, state, label, davranış veya truth ownership eklemez/değiştirmez; wireframe geometry, implementation technology, component library seçimi, persistence schema ve empirical calibration kilitlenmemiştir. Sahipleri 8G–10/16–18'dir.
- Independent 8F QA: **121/121 PASS**; 6 tone / 46-of-46 surface state eşlemesi / 18 component / 14 forbidden design anti-pattern. Validator tone kapsamını doğrudan 8A–8E yaml kontratlarından hesaplanan state union'ına karşı doğrular (eksik veya uydurulmuş state yakalanır) ve mutation test ile sınandı. Stage 6, Stage 7, accepted 8A–8E ve external-memory regressions PASS.
- Sonraki numbered step `8G — Wireframe/prototip`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/DESIGN_SYSTEM_SPEC.md`.


## D-074 — Wireframe & Prototype Geometry = WFPX-v0
**Durum:** Kabul edildi — 2026-08-29

- 8G final modeli `WFPX-v0 — Wireframe & Prototype Geometry` oldu ve **AŞAMA 8 kapandı**.
- Canonical spec `docs/WIREFRAME_PROTOTYPE_SPEC.md`; machine-readable contract `ux/8g_wireframe_prototype/wireframe.yaml`; görülebilir prototip `ux/8g_wireframe_prototype/prototype.html`; research/contract synthesis `research/8g_wireframe_prototype_research.md`.
- Geometry kabul edilmiş anlamı **yerleştirir**; bir surface'in ne anlama geldiğini, region sırasını, bir şeyin hangi state'te olduğunu veya o state'in tone'unu değiştiremez.
- Üç window class tanımlandı: `compact` (<600dp, bottom navigation), `medium` (600–839dp, navigation rail), `expanded` (≥840dp, rail + opsiyonel detail pane). Dört destination'ın kimliği ve sırası (`Today → Learn → Progress → Profile`) her sınıfta aynıdır; semantic region sırası değişmezdir ve hiçbir sınıf region ekleyemez, çıkaramaz veya yeniden sıralayamaz. `expanded`'daki detail pane aynı surface'i aynı truth ile gösterir, ikinci bir kopya değildir.
- Altı surface için concrete region geometry tanımlandı ve her biri sahibi spec'e karşı doğrulandı: Today (THUX-v0 5 region sırası birebir), Progress overview (SPWX-v0 iki yarı), Skill detail, Topic detail, Task runner flow ve Assessment session flow.
- `skill_detail` primary state chip'i **ve** dört ekseni birlikte gösterir; chip axis block'un yerini alamaz. Evidence summary independent/assisted/provisional/invalid ayrımını korur.
- `progress_overview` yalnız envanter sayıları gösterir; oran, bar, gauge veya yüzde yoktur.
- Focused flow'larda shell görsel olarak bastırılabilir fakat **exit ve pause her window class'ta 48dp tam hedefi ve sabit konumu korur** — shell gizliyken tek çıkış yolu oldukları için bu bir layout tercihi değil güvenlik gereğidir. Focused-flow chrome'unda countdown veya urgency öğesi yoktur; position context yalnız yön bilgisidir.
- **Ölçülmüş palet üretildi.** `VDSX-v0` §3 somut rengi bilinçle kilitlememiş ve paletin burada üretilip tema başına ölçülmesini şart koşmuştu. Light ve dark bağımsız olarak ölçüldü; dark, light'ın inversiyonu değildir. 52 zorunlu çiftin tamamı geçer; gözlenen minimumlar: yüzey üstü metin **6.08** (gereken ≥4.5), non-text ve sınır **3.79** (gereken ≥3.0), tone container üstü metin **6.06** (gereken ≥4.5).
- **Hue politikası bir severity kararıdır, zevk kararı değildir.** `attention` menekşedir (`#6A3FB5` / `#C0A6F5`), kehribar değil; kırmızı yalnız `system_fault` içindir. Attention kehribar, fault kırmızı olsaydı palet yeşil→sarı→kırmızı bir şiddet rampası oluşturur ve tone tablosu ne derse desin her `attention` state'i "başarısızlığa bir adım" gibi okunurdu. `VDSX-v0` §4'ün görsel severity yasağı fiilen burada uygulanır.
- Depolanan kontrast oranları **kanıttır, source of truth değildir**; `tools/validate_wireframe_prototype.py` her çalıştırmada WCAG relative-luminance formülünü uygulayarak hex'ten yeniden hesaplar ve beyan edilen minimumların hesaplananla eşleştiğini doğrular.
- %200 metin boyutuna kadar layout'lar **reflow eder, state'i truncate etmez**. Skill satırı sığmazsa iki satıra sarar; chip'ler Skill adı elenmeden önce sarar; secondary metadata ve duration state bilgisinden **önce** elenir; exit/pause hiçbir koşulda 48dp altına inmez.
- `prototype.html` self-contained tek sayfadır, wireframe'leri ve her iki paleti insan incelemesi için render eder ve **bağlayıcı değildir**: implementasyon değildir, teknoloji seçimi değildir, hiçbir framework'e bağlanmaz ve spec ile çelişirse spec kazanır. 9A/10 teknoloji seçiminde tamamen serbesttir.
- Independent 8G QA: **222/222 PASS**; 3 window class / 6 surface / **52 ölçülen kontrast çifti** / 11 forbidden geometry anti-pattern. Validator region sıralarını doğrudan THUX/SPWX/ASUX/TRUX kontratlarından, tone token'larını VDSX-v0'dan, destination sırasını UXIA-v0'dan çapraz doğrular; kontrastı iddia etmez hesaplar ve hex'lerin hem spec'te hem prototipte göründüğünü kontrol eder. İlk turda beyan edilen bir minimum yanlış çıktı (6.76 yerine gerçek 6.08) ve validator bunu yakaladı; düzeltildi. Ayrıca mutation test uygulandı: 6 kasıtlı ihlal 16 check FAIL verdi.
- Stage 6, Stage 7, accepted 8A–8F ve external-memory regressions PASS.
- **AŞAMA 8 tamamlandı.** Sonraki numbered step `9A — Teknik mimari / platform seçimi`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/WIREFRAME_PROTOTYPE_SPEC.md`.


## D-075 — Mobil teknoloji seçimi = AMTS-v0
**Durum:** Kabul edildi — 2026-08-29

- 9A final modeli `AMTS-v0 — Android Mobile Technology Selection` oldu ve **AŞAMA 9 başladı**.
- Canonical spec `docs/MOBILE_TECHNOLOGY_SPEC.md`; machine-readable contract `arch/9a_mobile_technology/technology.yaml`; research/decision synthesis `research/9a_mobile_technology_research.md`.
- Teknoloji seçimi kabul edilmiş kontratlara **hizmet eder**; hiçbirini zayıflatamaz, yeniden yorumlayamaz veya geçersiz kılamaz. Bir library default'u kabul edilmiş bir kontratla çeliştiğinde **kontrat kazanır**.
- **Araştırma sınırı açıkça çizildi.** `AI_AGENT_WORKFLOW.md` §3 "Hangisini seçmeliyiz?" ve "framework/library güncelliği" sorularını Araştırma AI'a yönlendirir; 9A, 8B–8G'den farklı olarak bunun geçerli olduğu ilk adımdır. Seçim *mantığı* değişmeyecek kabul edilmiş kontratlardan türetilir ve güncel pazar verisine ihtiyaç duymaz; buna karşılık library güncelliği, deprecation ve önerilen default'lar herhangi bir sabit bilgi ufkundan daha hızlı değişir. Bu nedenle 9A somut bir **bounded verification list** taşır ve bu liste 10A'da güncel kaynaklarla çözülür. Güncellik hakkında kesinlik iddia etmek `AGENTS.md`nin yasakladığı doğrulanmamış hassasiyet olurdu.
- **Android native seçildi; V1'de cross-platform UI katmanı yoktur.** Cross-platform araç seti tek kod tabanını birden çok platforma yaymak için vardır; V1 tek platforma çıkar ve iOS/web/desktop'ı açıkça dışlar. Fayda mevcut değildir, maliyet ise derhal ödenir — ve o maliyetin ineceği üç yer tam olarak AŞAMA 8'in kontrata bağladığı üç yerdir: screen-reader state exposure, locale-doğru Türkçe casing ve window-class adaptive navigation.
- **Taşınabilirlik hedge'i UI değil domain core'dur.** "V1'de yalnız Android" ≠ "sonsuza dek Android". Sistemin taşınabilir parçası domain core olursa ileride bir platform eklemek rewrite değil re-skin olur.
- **Kotlin** dil, **Jetpack Compose** UI toolkit olarak seçildi. `VDSX-v0` arayüzü token ve state olarak tanımlar, `SPWX-v0` bir Skill'in görünümünü canonical state'in deterministik projeksiyonu yapar; declarative ve state-driven bir toolkit bunu doğrudan ifade eder. Imperative bir view hiyerarşisi view state'i canonical state ile elle senkronize etmeyi gerektirirdi — bu tam olarak UI'ın state'in iddia etmediği bir şeyi söylemesine yol açan hata sınıfıdır.
- **Component library bir substrate'tir, truth kaynağı değildir.** Material 3 component mekaniği için kullanılabilir fakat `VDSX-v0` token'ları otoriterdir; library default renk ve typography'si ekrana ulaşamaz ve hiçbir component surface spec'inin tanımlamadığı bir state, tone veya affordance ekleyemez.
- **Dynamic colour (Material You) kapatılmak zorundadır.** Paleti kullanıcının duvar kağıdından türetir; bu, ölçülmüş paleti çöpe atar, tema başına kontrast kanıtını geçersiz kılar ve paletin bir şiddet rampası gibi okunmasını engelleyen bilinçli menekşe-değil-kehribar hue politikasını yok eder.
- **Deterministic core dependency-izole edilmiştir.** V1 release kriteri 8, AI Tutor yokken core'un çökmemesini şart koşar; bir konvansiyon bu çizgiyi yıllar boyunca tutamaz, bir dependency sınırı tutar. Domain core — curriculum graph, mastery, retention, prerequisite, planner, evidence — **saf Kotlin'dir ve Android API'ye, UI toolkit'e, networking'e veya herhangi bir AI client'a bağımlı olamaz.** Sonuç: core cihazsız test edilebilir ve ağsız çalıştırılabilir. Exact module/service boundary'leri 9D'ye aittir; 9A kuralı kilitler, yerleşimi değil.
- Üç window class `WFPX-v0` ile **birebir** eşleşir; platform kavramı ile spec inşa gereği örtüşür, tesadüfen değil. Destination kimliği ve sırası sınıflar arasında değişmez.
- Kabul edilmiş her accessibility gereksinimi bir platform mekanizmasına eşlendi: state'in metin olarak screen-reader'a açılması, scalable text units ile %200 metin ve reflow, 48dp minimum hedef, `focus_ring` token'lı focus semantics (≥3:1), platform reduced-motion ayarının onurlandırılması ve rengin asla tek taşıyıcı olmaması.
- **Türkçe casing bir platform-API meselesidir, styling meselesi değil.** Codebase'de default-locale case transform yasaktır; her dönüşüm explicit locale almak zorundadır ve `i ↔ İ` / `ı ↔ I` round-trip etmelidir. Kilitli `SPWX-v0` etiketleri yazıldığı gibi render edilir ve styling için case-transform edilemez. Identifier/teknik içerik karşılaştırmaları locale-sensitive casing kullanamaz — aksi hâlde noktalı/noktasız `i` bir lookup'ı bozar.
- **`minSdk` bir politikadır, sihirli sayı değil**: gereken accessibility, locale, adaptive ve date/time davranışını bunları zayıflatan uyumluluk shim'leri olmadan destekleyen en düşük seviye. Working default **API 26**'dır (modern date/time API'si o seviyeden itibaren natively mevcuttur ve bu ürün scheduling/history ağırlıklıdır); bu bir product default'tur, bilimsel değer değildir ve 10A'da **gerçek hedef cihaza karşı** doğrulanmalıdır. Hedef cihaz henüz repoda kayıtlı değildir.
- Kurulabilir **APK** V1 release kriteri 10 gereği zorunludur ve doğrudan kurulum ile cihaz QA'si için üretilebilir kalmalıdır; app bundle opsiyoneldir. Kritik akışlar V1 kriteri 9 gereği gerçek Android cihazında bağımsız QA'dan geçer.
- 9A depolama motoru, domain veri modeli, module/service sınırları, AI entegrasyonu, test stratejisi, DI/navigation library'si veya build pipeline'ını **kararlaştırmaz**; sahipleri 9B, 9C, 9D, 9E, 9F, 10A ve 19'dur. Bunlar için hiçbir library adı verilmemiştir.
- Independent 9A QA: **100/100 PASS**; validator seçimi opinion'a değil kabul edilmiş kontratlara karşı doğrular — window class'ları `WFPX-v0`dan, dokunma hedefi ve focus kontrastını `VDSX-v0`dan, Türkçe glyph setini `VDSX-v0`dan, kilitli etiket sayılarını `SPWX-v0`dan, platform ve release kısıtlarını `V1_SCOPE.md` metninden ve Research AI yönlendirmesini `AI_AGENT_WORKFLOW.md` metninden çapraz okur. Ayrıca mutation test uygulandı: 6 kasıtlı ihlal 7 check FAIL verdi. Stage 6, Stage 7 ve AŞAMA 8 regressions PASS.
- Sonraki numbered step `9B — Veri saklama / local-first`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/MOBILE_TECHNOLOGY_SPEC.md`.


## D-076 — Local-first persistence = LFPS-v0
**Durum:** Kabul edildi — 2026-08-29

- 9B final modeli `LFPS-v0 — Local-First Persistence Architecture` oldu.
- Canonical spec `docs/LOCAL_FIRST_PERSISTENCE_SPEC.md`; machine-readable contract `arch/9b_local_first_persistence/persistence.yaml`; research/decision synthesis `research/9b_local_first_persistence_research.md`.
- **En temel karar: kanıt source of truth'tur; öğrenci state'i onun yeniden hesaplanabilir projeksiyonudur.** Attempt, artifact, evidence event, assistance metadata, artifact provenance, exposure record, assessment session ve planner decision trace **append-only** truth kayıtlarıdır; yerinde güncellenmez ve silinmez. Mastery, retention, prerequisite readiness, Topic state, weakness/remediation ve Technical English profile ise **cache**'tir ve her an truth kayıtlarından yeniden kurulabilir. Öğrencinin kanıtladığı yetkinliği yalnız yeni kanıt değiştirebilir.
- Gerekçe estetik değil: derived state otoriter olsaydı bir migration veya bug, hiçbir kanıt değişmeden öğrencinin "kanıtladığı" şeyi değiştirebilirdi. Kanıtı truth yapmak bu bozulma sınıfını yapısal olarak ortadan kaldırır ve `SPWX-v0`nin `recomputing_projection` state'ine gerçek bir karşılık verir.
- **Geçersiz kanıt silinmez, işaretlenir.** Disposition kaydın parçasıdır; yeniden hesaplama onu yokluğuyla değil kuralla dışlar. Böylece geçmiş açıklanabilir kalır.
- **Storage engine: embedded transactional relational store — SQLite.** Gerekçe tercihten değil kontratlardan türetildi: versiyonlanmış curriculum graph ile ona işaret eden evidence arasında referential integrity, planner ve evidence pipeline'ının ihtiyaç duyduğu Skill/Objective/zaman/geçerlilik üzerinden cross-entity sorgular, transactional all-or-nothing yazma, yıllarca sürecek schema değişimi için olgun forward migration ve sunucusuz/ağsız tam on-device çalışma. Reddedilen alternatifler ve kayıpları kaydedildi: document/key-value store referential integrity ve planner'ın ihtiyaç duyduğu cross-entity sorguları kaybeder; flat file/serialized snapshot ise transaction ve inandırıcı migration yolunu kaybeder.
- **ORM/mapping library bu adımda seçilmedi**; 10A verification item'ıdır. Bu, `AMTS-v0`nin "library güncelliği iddia edilmez, doğrulanır" emsalini izler.
- **Dependency ters çevrildi.** `AMTS-v0` core'un Android'e bağımlı olmasını yasaklar fakat core okuma/yazma yapmak zorundadır: persistence interface'leri core'a aittir ve core tiplerinde ifade edilir; platform katmanı bunları SQLite üzerinde implemente eder. Core signature'ında SQLite, Android veya file-system tipi görünemez; core cihazsız, fake/in-memory implementasyona karşı çalıştırılabilir ve test edilebilir. Exact module layout 9D'ye aittir.
- **Curriculum ve user state ayrı saklanır ve ayrı versiyonlanır** (V1_SCOPE §16). Published curriculum version'ları saklanır, üzerine yazılmaz (`KGC-v0`); user kayıtları üretildikleri curriculum version'ını **pin**'ler; curriculum güncellemesi yalnız version ekler ve mevcut kanıtı asla yeniden yazmaz, yeniden yorumlamaz veya geçersizleştirmez. Historical evidence, onu üreten graph version'ına karşı yeniden kurulabilir kalır.
- **Curriculum güncellemesi bir state değişikliği değildir.** Yeni içerik kurmak tek başına hiçbir mastery/retention/readiness/weakness/profile sonucunu değiştiremez. Semantic bir değişiklik geçmiş bir sonucu artık geçersiz kılıyorsa bu, normal engine'ler üzerinden **yeni bir need** olarak ifade edilir; geçmişin sessizce yeniden yazılması olarak değil.
- **Exposure kayıtları kalıcı ve first-class veridir.** Solution exposure, görülen item version'ı ve variant-family exposure profil ömrü boyunca saklanır; backup, export ve migration'a kanıtla aynı statüde dahildir. Birinin kaybı **veri kaybıdır**, cache eviction değildir. Gerekçe spesifiktir: bir exposure kaydı kaybolursa solution-exposed bir item daha sonra taze bağımsız kontrol olarak sunulabilir. Hiçbir şey çökmez — ürün yalnızca sahte independent evidence üretmeye başlar. Sistemdeki en sessiz hata biçimidir ve engellenebileceği tek yer storage katmanıdır.
- **Bir öğrenci eylemi bir transaction'dır.** Attempt, artifact, assistance metadata, provenance, evidence event ve sonuçtaki derived-state güncellemesi birlikte commit olur veya hiçbiri olmaz. Kısmi yazma gözlemlenemez; attempt asla assistance metadata veya provenance olmadan kalıcılaştırılamaz; `evaluation_pending` attempt'i yazar fakat **evidence yazmaz** ve bu da atomiktir. Kısmi yazma, arayüzün kanıtın desteklemediği bir şeyi iddia etmeye başlamasının tam olarak yoludur.
- **Migration forward-only ve versiyonludur**; kanıtı, exposure'ı veya provenance'ı asla silmez, yeniden yazmaz veya yeniden yorumlamaz. Derived state'i atıp yeniden kurabilir ve bu veri kaybı değildir. Her migration boş değil **dolu** bir veritabanına karşı test edilir. Downgrade desteklenmez; geri dönüş schema downgrade değil **backup restore**'dur. Tamamlanamayan migration önceki state'i bozmadan bırakır ve `data_recovery_required` yüzeyler.
- **Backup ve export kullanıcı tarafından başlatılır**; V1'de otomatik cloud upload yoktur. Export profili yeniden kurmaya yetecek kadar eksiksizdir: truth kayıtları, exposure kayıtları, provenance, curriculum version pin'leri ve tercihler. Derived state export edilmek zorunda değildir çünkü yeniden kurulabilir, fakat yokluğu bilgi kaybettirmemelidir. Export üretildiği schema ve policy version'ını kaydeder ve kullanıcı taşımadıkça cihazda kalır.
- **Restore atomiktir ve doğrulanır**: yapısal bütünlük ve version uyumluluğu önce kontrol edilir, sonra değiştirir. Eski schema'dan restore forward migration çalıştırır; **daha yeni** schema'dan restore best-effort uygulanmaz, reddedilir. Restore asla sessizce merge etmez; profil değiştirme açıktır.
- **Bütünlük açılışta, migration sonrası ve restore sonrası kontrol edilir.** Tespit edilen bozulma kabul edilmiş `data_recovery_required` state'ini yüzeyler; **sessiz progress reset yasaktır**. Truth kayıtları sağlamken derived state tutarsızsa doğru onarım **reset değil recomputation**'dır ve `recomputing_projection` olarak yüzeyler. Kalıcılaştırılmamış iş asla kaydedilmiş gibi iddia edilmez.
- **V1'de kanıt budanmaz.** Otomatik silme, rolling window ve tidy-up job yoktur; history tasarım gereği birikir çünkü retention, remediation ve verification geriye doğru okur. Derived state ve cache'ler serbestçe atılabilir. Depolama ileride gerçek bir kısıt hâline gelirse budama sonradan, açıkça tasarlanmış ve kullanıcıya görünür bir karar olur — asla örtük değil ve asla mevcut state'in bağımlı olduğu veriye dokunmadan.
- Core okuma/yazma yolları ağsız çalışır; V1'de realtime multi-device cloud sync yoktur; ağ yokluğu yalnız opsiyonel kabiliyeti düşürür, deterministic core'u değil; persistence hiçbir AI client'a bağımlı değildir.
- 9B entity/field/relation ve physical schema (9C), module/service boundary (9D), AI entegrasyonu (9E), test stratejisi (9F), ORM/migration tooling (10A) ile encryption posture ve cloud backup hedefi (16/19) kararlarını **vermez**.
- Independent 9B QA: **100/100 PASS**; validator kararı kontratlara karşı doğrular — curriculum/user ayrımını `V1_SCOPE.md` metninden, published-version değişmezliğini `KGC-v0` metninden, exposure ayrımını `QUESTION_BANK_SPEC.md` metninden, exposed-item yeniden kullanım yasağını `ASUX-v0`dan, `evaluation_pending` evidence yasağını hem `TRUX-v0` hem `ASUX-v0`dan, `data_recovery_required` ve `recomputing_projection` state'lerinin gerçekliğini `SPWX-v0`dan ve core purity'yi `AMTS-v0`dan okur. Ayrıca mutation test uygulandı: 7 kasıtlı ihlal 6 check FAIL verdi. Stage 6, Stage 7, AŞAMA 8 ve 9A regressions PASS.
- Sonraki numbered step `9C — Domain veri modeli`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/LOCAL_FIRST_PERSISTENCE_SPEC.md`.


## D-077 — Domain veri modeli = DDM-v0
**Durum:** Kabul edildi — 2026-08-29

- 9C final modeli `DDM-v0 — Domain Data Model` oldu.
- Canonical spec `docs/DOMAIN_DATA_MODEL_SPEC.md`; machine-readable contract `arch/9c_domain_data_model/data_model.yaml`; research/decision synthesis `research/9c_domain_data_model_research.md`.
- Ana invariant: **schema mimariyi uygular.** `LFPS-v0`nin her garantisi — truth/projection ayrımı, append-only, exposure kalıcılığı, version pinning — burada konvansiyon değil **yapısal**dır.
- Adımın çözdüğü asıl problem: **bir schema, bir mimariyi sessizce yürürlükten kaldırabilir.** `LFPS-v0` truth kayıtlarının append-only olduğunu ve geçersiz kanıtın silinmeyip işaretlendiğini söyleyebilir; fakat schema evidence satırına mutable bir `outcome` kolonu verip disposition tablosu vermezse garanti gitmiştir ve hiçbir test bunu bildirmez.
- **Tek veritabanı, üç mantıksal bölge:** `curriculum_store` (published sonrası değişmez, versiyonlu), `user_truth_store` (append-only), `user_projection_store` (atılabilir ve yeniden kurulabilir). Ayrım schema'da ifade edilir; curriculum store'dan user store'a foreign key yoktur.
- **Versiyonlu kimlik composite'tir:** her published curriculum entity'si ve her assessment resource'u `(logical_id, version)` ile anahtarlanır. Logical ID'ler `GNS-v0` formatını izler ve içlerinde week/stage/release/band/difficulty/role bulunmaz.
- **Pinning yapısaldır.** User store'daki her curriculum/resource referansı hem logical ID hem version taşır. Yalnız logical ID ile referans yasaktır: version anahtarda olmazsa referans, curriculum güncellendiği anda sessizce en yeni version'a kayar — `KGC-v0`nin yasakladığı ve `LFPS-v0`nin önlemek için yazıldığı sessiz state değişikliği tam olarak budur.
- **Dört eksen dört kolon olarak kalır.** Bir evidence satırı dört bağımsız olgu taşır ve her biri kendi kolonudur: `outcome` (positive/negative/partial/invalid), `evaluator_status` (verified/provisional/invalid), `independence_class` (independent/assisted/practice_only/requires_independent_recheck) ve `contested`. Herhangi ikisini birleştirmek bir garantiyi yok eder: `evaluator_status`ü `outcome`a katlamak provisional bir sonucu settled olandan ayırt edilemez yapar; `independence_class`ı katlamak "tek başına yaptı" ile "cevabı gördükten sonra yaptı" farkını siler.
- **Düzeltme bir append'tir.** Truth satırında `UPDATE` yolu yoktur; sonraki bir yargı — invalidated, contested, superseded, reinstated — orijinali referans veren bir `evidence_disposition` satırı olarak yazılır. Yeniden hesaplama dispositionları okur ve **yokluğuyla değil kuralla** dışlar.
- `evidence_event`, `GRE-v0`nin bu adıma açıkça devrettiği alan sözleşmesini karşılar ve skill ile resource version'larını pinler.
- **Zaman tek bir değer değildir.** Her timestamp'li satır üç değer saklar: `occurred_at_instant` (UTC epoch — retention için interval aritmetiği), `occurred_on_study_day` (learner-local tarih — günlük planlama ve history) ve `utc_offset_minutes` (o anda yürürlükte olan offset). Bu ürün learner-local güne göre planlar, geçen süreye göre retention zamanlar ve saat dilimi seyahatiyle DST değişimine dayanmak zorundadır. Yalnız instant saklamak bir attempt'in **hangi çalışma gününe** sayıldığını kaybettirir — DST veya seyahat sonrası kanıt sessizce günler arasında kayar ve günlük plan ile history kullanıcının gerçekte yaptığıyla çelişir. Yalnız local date saklamak retention'ın dayandığı interval aritmetiğini kaybettirir. Yürürlükteki offset saklanan değerden geri kazanılamadığı için ikisi de sonradan diğerinden güvenilir biçimde türetilemez; üçü de yazma anında kaydedilir.
- **Projeksiyonların provenance'ı vardır.** Her projection satırı `policy_version`, `truth_watermark`, `built_at_instant` ve `input_curriculum_version` kaydeder. Yeniden kurulabilir bir projeksiyon ancak bayatlığı **tespit edilebilirse** güvenilirdir; watermark olmadan bayat bir projeksiyon güncel olandan ayırt edilemez ve `recomputing_projection` karşılaştıracak bir şey bulamaz.
- **`SPWX-v0`nin dört Skill ekseni ayrı kolonlarda saklanır** ve derived presentation state onların yerini storage'da da almaz — ekranda almadığı gibi.
- **Exposure sorgulanabilir olmak zorundadır, yalnız saklanabilir değil.** "Solution-exposed bir item'ı asla taze bağımsız kontrol olarak sunma" garantisi item seçiminin sıcak yolundaki bir lookup'tır; bu yüzden exposure, resource logical ID, variant family ve exposure kind üzerinden indekslenir ki kontrol her zaman çalışacak kadar ucuz olsun. Exposure satırları silinmez, arşivlenmez ve truth kayıtlarıyla birlikte export/migrate edilir.
- Physical schema **library-neutral** ifade edilir çünkü ORM 10A kararıdır: entity başına bir tablo, polymorphic catch-all yok, versiyonlu entity'lerde composite primary key, versiyonlu bir entity'ye işaret eden her foreign key version kolonunu taşır, enum benzeri alanlar dokümante edilmiş izinli kümesi olan constrained string'lerdir, truth tabloları projection watermark'ı olarak kullanılan monotonic bir sequence taşır ve projection tabloları truth'a dokunmadan atılıp yeniden kurulabilir. Index intent sorgu şekilleri olarak belirtilir; somut index tanımları ve tuning implementation ile 18E'ye aittir.
- `AMTS-v0` core purity gereği core'un gördüğü modelde platform tipi yoktur: identifier string, instant epoch değeri, study day ISO tarih string'i ve enum benzeri alanlar constrained string'tir; SQLite/Android/ORM/filesystem tipi görünmez.
- Truth tabloları budanmadan büyür; yazmalar append-only olduğu için maliyet history boyutundan bağımsızdır; okuma maliyeti index şekli ve engine'lerdeki bounded query window'ları ile kontrol edilir, history silerek değil. Projection tabloları event başına değil entity başına anahtarlandığı için küçük kalır.
- 9C ORM/mapping library ve annotation'ları (10A), somut migration script'leri (10A/12), module ve service boundary'leri (9D), AI entegrasyonu (9E), append-only'nin nasıl uygulanıp doğrulanacağı dahil test stratejisi (9F) ve encryption at rest (19) kararlarını **vermez**.
- Independent 9C QA: **114/114 PASS**; 11 curriculum entity / 12 truth entity / 8 projection entity / 15 forbidden model anti-pattern. Validator modeli kendine değil kaynak kontratlara karşı doğrular: truth ve projection kapsamını `LFPS-v0` listelerinden, Skill alanlarını ve published-version değişmezliğini `KGC-v0`dan, ID kurallarını `GNS-v0`dan, EvidenceEvent alanlarını `GRE-v0`dan (ve bu sözleşmenin gerçekten 9C'ye devredildiğini metinden), assistance level/timing/scope ile artifact origin değerlerini `TRUX-v0`dan, evaluator status değerlerini `ASUX-v0`dan, dört ekseni ve `recomputing_projection` gerçekliğini `SPWX-v0`dan, exposure kind'larını ve kayıp sınıflandırmasını `LFPS-v0`dan okur. Ayrıca mutation test uygulandı: 7 kasıtlı ihlal 6 check FAIL verdi. Stage 6, Stage 7, AŞAMA 8, 9A ve 9B regressions PASS.
- Sonraki numbered step `9D — Servis sınırları`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/DOMAIN_DATA_MODEL_SPEC.md`.


## D-078 — Servis sınırları = MSBX-v0
**Durum:** Kabul edildi — 2026-08-29

- 9D final modeli `MSBX-v0 — Module & Service Boundaries` oldu.
- Canonical spec `docs/SERVICE_BOUNDARIES_SPEC.md`; machine-readable contract `arch/9d_service_boundaries/boundaries.yaml`; research/decision synthesis `research/9d_service_boundaries_research.md`.
- Ana invariant: **sınırlar garantileri yapısal hâle getirir.** AI ve ağ yokluğunda ayakta kalan deterministic core, hatırlamayla değil dependency kuralıyla korunur.
- Adımın çözdüğü asıl problem: **"core AI olmadan da çalışır" şu ana kadar bir vaatti.** Hiçbir şey yarın bir engine'in AI client import etmesini yapısal olarak engellemiyordu. Yıllarca herkesin hatırlamasına bağlı bir vaat garanti değildir; bir dependency kuralı garantidir.
- **On modül ve katı içe-doğru dependency kuralı:** `core-model`, `core-ports`, `core-engines`, `core-application`, `core-presentation`, `data-persistence`, `data-curriculum`, `ai-adapter`, `app-ui`, `app-wiring`. `core-*` asla `data-*`, `ai-*` veya `app-*`'e bağımlı olamaz; graf asiklikdir; `app-wiring` her implementasyonu bilen tek modüldür ve domain logic içermez. Kural **kontrol edilebilirdir**, temenni değil.
- **Core'un dışarıdan ihtiyaç duyduğu her şey bir port'tur** ve port'lar core'da yaşar, core tipleriyle ifade edilir, hiçbir platform tipi içermez: `PersistencePort`, `ContentPort`, `ClockPort`, `EvaluatorPort`.
- **Saat bir port'tur.** `DDM-v0` her timestamp'li kayıtta instant, learner-local study day ve offset ister; `ADAPTIVE_PLANNER_SPEC` §18 aynı girdilerin aynı sonucu üretmesini ister. Bir engine sistem saatini doğrudan okursa ikisi de bozulur: timezone mantığı test edilemez hâle gelir ve planner çıktısı beyan edilen girdilerinin bir fonksiyonu olmaktan çıkar. Zaman bir **girdidir**, ortam gerçeği değil.
- **Core'da rastgelelik yoktur ve randomness port'u da yoktur.** Beraberlikler `PBR-v0`nin deterministic rank'ıyla tutarlı biçimde **beyan edilmiş total ordering** ile çözülür. Seeded random source reddedildi: determinizmi ihlal edilemez bir özellik olmaktan çıkarıp yanlış ayarlanabilir bir konfigürasyon hâline getirirdi.
- **AI yokluğu yapısaldır.** Hiçbir `core-*` modülü AI client, HTTP client veya network tipine referans veremez; **null evaluator implementasyonu ürünle birlikte sevk edilir** ve test fixture'ı değildir; uygulama `ai-adapter` yokken build edilip çalışabilmelidir. Null evaluator ile open-ended attempt'ler `evaluation_pending` olur ve **evidence yazılmaz** (`TRUX-v0` / `ASUX-v0`), hiçbir deterministic kabiliyet düşmez. Böylece V1 release kriteri 8 — "AI Tutor yokken deterministic local core çökmemeli" — umutla değil **wiring ile** karşılanır ve adaptör olmadan build alarak gösterilebilir.
- **Her engine tam olarak bir state ailesine sahiptir ve başkasınınkini yazmaz.** GRE mastery'yi, RVR retention'ı, PRG readiness'i, TSM Topic state'i, WLRM weakness/remediation'ı, PBR priority/rank'ı, PDT plan/trace'i, TEPM English profile'ı sahiplenir. Planner mastery, retention, readiness veya weakness yazmaz; assessment retention'ı doğrudan yazmaz; cross-engine etkiler `core-application`ın engine'leri beyan edilmiş sırayla çağırmasıyla olur. Bir engine'in başkasının state'ini yazmasına izin vermek, önceki her aşamanın yasakladığı ikinci source of truth'u sessizce geri getirirdi.
- **Transaction sınırı `core-application`dadır.** `LFPS-v0` bir öğrenci eyleminin attempt, artifact, assistance metadata, provenance, evidence ve sonuçtaki derived-state güncellemesi boyunca atomik commit olmasını ister; bu orkestrasyon UI handler'larında veya bir engine içinde değil, adı konmuş bir katmandadır. Engine'ler saf policy kalır: transaction açmazlar ve persistence'ı doğrudan çağırmazlar.
- **Presentation projection core'dadır, UI'da değil.** `SPWX-v0` derived presentation state'i beyan edilmiş precedence'lı deterministik bir projeksiyon yapar. `core-presentation` bunu ve Today/task runner/assessment session/Progress view model'lerini **saf veri** olarak hesaplar; `app-ui` yalnız render eder. Aksi hâlde üründeki en güvenlik-kritik etiketleme — bir chip'in öğrencinin yetkinliği hakkında ne iddia ettiği — yalnız cihazda test edilebilirdi.
- `app-wiring` composition root'tur: her implementasyonu bilen tek modüldür, domain logic/policy/state içermez, gerçek veya null evaluator'ı seçer ve bir implementasyonu değiştirmek yalnız bu modüle dokunur.
- 9D DI library ve wiring söz dizimi (10A), build-system modül tanım söz dizimi (10A), test stratejisi ve dependency kuralının CI'da nasıl zorlanacağı (9F), AI provider/prompt davranışı (9E) ile somut sınıf/fonksiyon/paket adlarını **kararlaştırmaz**.
- Independent 9D QA: **93/93 PASS**; 10 modül / 4 port / 8 engine / 15 forbidden boundary anti-pattern. Validator dependency grafını **iddia etmez, hesaplar**: bilinmeyen bağımlılıkları, cycle'ı ve forbidden layer edge'lerini graf üzerinden bulur; ayrıca kararı upstream kontratlara karşı doğrular — `AMTS-v0` ve `LFPS-v0`nin boundary layout'u gerçekten 9D'ye devrettiğini, `DDM-v0`nin üç zaman değerini gerçekten istediğini, `ADAPTIVE_PLANNER_SPEC`in determinizmi gerçekten şart koştuğunu, `TRUX-v0` ve `ASUX-v0`nin `evaluation_pending` için evidence'ı gerçekten yasakladığını, `SPWX-v0`nin projeksiyonu gerçekten deterministik ilan ettiğini ve V1 kriteri 8'in metinde gerçekten bulunduğunu okur. Mutation test: 6 kasıtlı ihlal (core→ai kenarı, graf cycle'ı, core'da rastgelelik, null evaluator'ın fixture'a indirgenmesi, planner'ın mastery yazması, presentation'ın UI'a taşınması) 8 check FAIL verdi. Stage 6, Stage 7, AŞAMA 8, 9A, 9B ve 9C regressions PASS.
- Sonraki numbered step `9E — AI entegrasyon mimarisi`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/SERVICE_BOUNDARIES_SPEC.md`.


## D-079 — AI entegrasyon mimarisi = AIAX-v0
**Durum:** Kabul edildi — 2026-08-29

- 9E final modeli `AIAX-v0 — AI Integration Architecture` oldu.
- Canonical spec `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md`; machine-readable contract `arch/9e_ai_integration/ai_integration.yaml`; research/decision synthesis `research/9e_ai_integration_research.md`.
- Ana invariant: **AI bir port arkasındaki yardımcıdır.** Mastery, retention, prerequisite, planner veya curriculum truth üzerinde asla otorite kazanmaz ve yokluğu ya da hatası asla negative evidence üretmez. Kural: `AI proposes. Deterministic engines decide.`
- **İki kanonik spec kararı açıkça bu adıma devretmişti** ve ikisi de burada kapatıldı: `LEARNING_BEHAVIOR_RULES` §17 model seçimini, §18 ise final güvenlik/proxy/backend kararını 9E'ye bırakmıştı.
- **AI'ın yapabildikleri korundu** — alternatif anlatım, istenen seviyede ipucu, açık uçlu cevabın değerlendirilmesine yardım, kod feedback'i, kök neden analizi, misconception hipotezi, soru varyantı ve transfer item taslağı. Amaç AI'ı azaltmak değil, yetkisini sınırlamaktır.
- **AI'ın asla yapamayacakları:** mastery/retention state yazmak veya değiştirmek, prerequisite'i karşılamak veya aşmak, planner priority/rank/capacity'yi değiştirmek, assessment quota koymak, "öğrendi" diye karar vermek, kendi ürettiği item'ı doğrulamak, weakness'i tek başına confirmed yapmak ve refusal/timeout/error'ı olumsuz sonuca çevirmek.
- **Evaluator çıktısı schema-constrained structured output'tur**, ayrıştırılacak düz metin değil. Schema'ya uymayan yanıt bir **hata**dır, bir hüküm değil. Serbest metinden hüküm ayrıştırmak yasaklandı çünkü bir misparse hata gibi değil **hüküm gibi görünür** — bu ürünün tolere edemeyeceği sessiz hata sınıfı tam olarak budur.
- **Kalibre edilmemiş LLM değerlendirmesi varsayılan olarak `provisional`dır** (2D ve `AIV-v0` §18). Bilgilendirebilir ve confirmation need açabilir; tek başına critical mastery gate'i geçemez veya ağır remediation'ı sürükleyemez. `verified` deterministik bir yol gerektirir: answer key, objective-specific test, compiler/runtime verifier, deterministic trace checker, validated benchmark criterion veya ileride calibrated policy altındaki prevalidated rubric.
- Evaluator asla mastery hükmü döndürmez; çıktısı evidence pipeline'ına girdidir ve ne anlama geldiğine `GRE-v0` karar verir.
- **Refusal bir yanlış cevap değildir.** Yedi sonuçlu taksonomi tanımlandı ve `refused`, `timed_out`, `transport_error`, `invalid_response`, `unavailable` — hepsi aynı biçimde `evaluation_pending`e düşer, evidence yazmaz ve ne geçti ne kaldı sayılır. Güvenlik sınıflandırıcıları bir isteği reddedip normal bir yanıt döndürebilir; adapter her yanıtsızlığı başarısız değerlendirme sayarsa, **öğrenci kendi kod örneğinde bir filtre tetiklendiği için negative evidence alır.** Bu entegrasyondaki en zarar verici yanlış okuma budur ve implementation takdirine bırakılmadan açıkça yasaklandı. Adapter içeriği okumadan önce stop reason'ı incelemek zorundadır ve boş veya beklenmedik yanıttan hüküm çıkaramaz.
- **Timeout bütçesi uçtan uçadır.** SDK client'ları varsayılan olarak retry yaptığı için wall-clock, yapılandırılmış per-request timeout ile deneme sayısının çarpımına ulaşabilir; bu nedenle per-call timeout kullanıcıya verilen bir garanti değildir. Bütçe tüm denemeleri kapsar, ayarlanabilir bir product default'tur, retry'lar sınırlıdır ve kullanıcının key'ine karşı sessiz arka plan retry'ı yoktur.
- Degradation tek biçimlidir: AI yokken hiçbir deterministic kabiliyet düşmez, öğrenci dürüst bir pending state görür, uydurma sonuç üretilmez ve uygulama AI kapalıyken tamamen kullanılabilir kalır.
- **Model seçimi ile provider bağımsızlığı birlikte sağlandı.** Model adı **konfigürasyonda** yaşar, core'da değil; hiçbir kalıcı öğrenme semantiği model adına bağlı değildir; adapter bir router sunar ve model/provider değiştirmek yalnız adapter ile konfigürasyona dokunur. Task class'a göre varsayılanlar: open-response evaluation ve learner-facing açıklama/ipucu **quality tier** (değerlendirme kalitesi doğrudan öğrenciye söylenen şeyi etkiler ve ekonomi yapılacak en uygunsuz yer burasıdır); generated-item taslağı ve toplu validation **cost tier**, toplu validation ayrıca batch modda (latency'ye duyarsız ve yüksek hacimli). Somut model kimlikleri konfigürasyon değeridir, kalıcı olarak iddia edilmez ve 10A/14'te güncel bir provider referansına karşı yeniden doğrulanmak zorundadır — `AMTS-v0`nin "güncellik iddia edilmez, doğrulanır" emsali.
- **Deterministik iş asla AI çağırmaz** (§17): answer-key kontrolleri, prerequisite/graph değerlendirmesi, planner candidate/priority/capacity mantığı, mastery/retention/readiness hesabı, progress ve history lookup'ları ve projection rebuild'leri. Maliyet duruşu: latency'ye duyarsız toplu iş batch kullanır, stabil prefix'ler caching için yapılandırılır, değişmemiş girdi için değerlendirme tekrarlanmaz ve **maliyet hiçbir zaman bir evidence kuralını zayıflatmanın gerekçesi değildir**.
- **Credential kararı kapatıldı.** APK'ya sabit veya paylaşılan API key **konmaz** — hiçbir koşulda. V1'de **backend proxy yoktur**: V1 sunucusuz ve local-first'tür ve yalnız bir key tutmak için proxy eklemek bunu çelişkiye düşürür ve ürünün başka türlü ihtiyaç duymadığı altyapıyı getirir. **Öğrenci kendi key'ini girer** ve key **platform secure storage**'da tutulur, düz preference veya dosyada değil. Tehdit modeli açıkça yazıldı: kullanıcının kendi cihazındaki kendi key'i sızmış bir sır değildir — sır, cihazı elinde tutan kişiye aittir; sabit bir geliştirici key'i ise her kuruluma tek bir paylaşılan sır gönderir. Key log, crash report, export, backup veya diagnostics'te asla görünmez; `LFPS-v0` export'una dahil edilmez; provider endpoint'i dışında hiçbir yere gönderilmez; key'i kaldırmak veri kaybı olmadan null-evaluator yoluna döner ve uygulama hiç key girilmeden tamamen kullanılabilir olmalıdır. İleride hosted backend gelirse bu, kendi adımı olan açık ve bilinçli bir karardır — bir implementation tercihinin örtük sonucu değil.
- **Gizlilik sınırı çizildi.** Bu ürün başka türlü cihazdan hiç çıkmadığı için bir evaluator çağrısı mimari bir olaydır: yalnız **mevcut attempt'i değerlendirmek için gereken asgari içerik** gönderilebilir; evidence history, mastery state, plan, profile, exposure kayıtları, provenance ve planner trace'leri **asla** gönderilmez; AI kullanımı öğrenciye görünürdür ve kapatılabilir; AI kapalıyken cihazdan hiçbir şey çıkmaz.
- `AIV-v0` entegrasyonu: generated item **untrusted** girer ve doğrulanana kadar high-stakes evidence için kullanılamaz; generator ile validator ayrıdır; generator'ın kendi self-grade'i validation değildir; kalibre edilmemiş tek-LLM skoru tek başına yetersizdir; doğrulanmamış AI item'ı güçlü mastery-changing evidence üretemez ve validation status resource version'ında kaydedilir.
- **Her AI-türevli evidence satırı bir `evaluator_ref` kaydeder**: provider, model ve prompt/schema version — `DDM-v0`nin `evaluator` alanıyla tutarlı. Bu olmadan geçmiş bir değerlendirme açıklanamaz, model değişikliği üzerine akıl yürütülemez ve yeniden değerlendirme kapsamlandırılamaz. Sonraki bir yargı `LFPS-v0` gereği append edilen bir `evidence_disposition`'dır, düzenleme değil.
- 9E prompt metni ve rubric ifadesi (14), AI Tutor konuşma UX'i (14), evaluator'ın insan yargısına karşı kalibrasyonu (18), somut SDK çağrı noktaları ve hata işleme kodu (10A/14) ile test stratejisi (9F) kararlarını **vermez**.
- Independent 9E QA: **86/86 PASS**; 8 AI-may / 9 AI-may-never / 7 outcome / 18 forbidden AI anti-pattern. Validator kararı kaynak kontratlara karşı doğrular: `LEARNING_BEHAVIOR_RULES` metninden AI'ın prerequisite'i aşamayacağını, provider bağımsızlığı hedefini, model seçiminin gerçekten 9E'ye devredildiğini, deterministik iş için AI çağrısı yasağını, hardcoded key reddini ve güvenlik kararının 9E'ye verildiğini; `AIV-v0` metninden kalibre edilmemiş tek-LLM skorunun yetersizliğini ve critical mastery ceiling sınırını; `TRUX-v0`/`ASUX-v0`dan `evaluation_pending` evidence ve auto-fail yasağını; `MSBX-v0`dan null evaluator'ın ürünle sevk edildiğini; `DDM-v0`dan `evaluator`/`evaluator_status` alanlarının ve `resource_validation_record` entity'sinin varlığını; `LFPS-v0`dan export'un preferences taşıdığını fakat key'in taşınmadığını okur. Ayrıca spec'in hiçbir somut model kimliğini kanonik yapmadığı algoritmik olarak kontrol edilir. Mutation test: 7 kasıtlı ihlal (refusal'ın yanlış cevap sayılması, refused'ın evidence yazması, uncalibrated LLM'in verified olması, APK'da hardcoded key, serbest metin ayrıştırma, mastery_state'in gönderilebilir hâle gelmesi, bütçenin uçtan uca olmaması) 7 check FAIL verdi. Stage 6, Stage 7, AŞAMA 8, 9A, 9B, 9C ve 9D regressions PASS.
- Sonraki numbered step `9F — Test stratejisi`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md`.


## D-080 — Dağıtım kapsamı: kişisel kullanım, store dağıtımı yok
**Durum:** Kabul edildi — 2026-08-29 (kullanıcı kararı; numaralı adım değildir)

- Ürün **tek kullanıcı içindir** ve o kullanıcı sahibin kendisidir. **Hiçbir uygulama merkezine yüklenmeyecek ve hiçbir halka açık yerde paylaşılmayacaktır.** Repo private'dır.
- Bu, `V1_SCOPE` §1'deki "tek kullanıcı / kişisel kullanım" ilkesini genişletmez; **dağıtım** boyutunu kilitler. V1_SCOPE tek kullanıcıyı zaten söylüyordu, dağıtımın olmayacağını söylemiyordu.
- **Düşen iş:** store yayın gereksinimleri (data-safety beyanı, privacy policy, store listing), dağıtım için release imzalama seremonisi, güvenlik amaçlı obfuscation/R8 kuralları, certificate pinning, çok kullanıcı/hesap sistemi, uzak crash reporting ve cihaz matrisi. AŞAMA 19 buna göre daralır.
- **`minSdk` tek cihaza sabitlenebilir:** geniş uyumluluk bir gereksinim değildir. `AMTS-v0` §9'un `minSdk` politikası kullanıcının gerçek cihazına göre doğrulanır. **Hedef cihaz hâlâ repoda kayıtlı değildir ve 10A için gereklidir.**
- **Device QA tek cihazdır:** V1 kriteri 9 ("kritik akışlar gerçek Android cihazında bağımsız QA'dan geçmeli") bir cihaz matrisi değil, o tek hedef cihaz anlamına gelir.
- `LFPS-v0` export'u paylaşılabilir bir artifact değil **kişisel yedek** olarak ele alınır; `export_stays_on_device_unless_user_moves_it` zaten bunu söylüyordu.
- **Değişmeyenler ve nedeni — bunlar güvenlik kuralı değildir:** AI'ın yetki sınırları, `refusal != yanlış cevap`, schema-constrained evaluator çıktısı, `evaluation_pending`in evidence yazmaması, append-only truth ve exposure kalıcılığı. Bunlar yabancılardan korunmak için değil, **kullanıcının kendi kanıt kaydını bozmamak** için vardır; dağıtım olmaması bu riskin hiçbirini azaltmaz.
- `AIAX-v0`ın "yalnız mevcut attempt için gereken asgari içerik cihazdan çıkar" sınırı korunur; gerekçesi bu kapsamda güvenlik değil **token maliyeti** ve AI-kapalı yolun çalışır kalmasıdır.
- `AIAX-v0` `key_in_platform_secure_storage: true` invariant'ı **korunur**. Kişisel kullanımda kazanılacak iş küçüktür (platform bunu hazır sağlar) ve gevşetmek spec + yaml + validator üçlüsünde bilinçli bir amendment gerektirir. İleride gevşetilirse bu **açık bir amendment** olarak yapılır, sessizce değil.
- **API key repoya commit edilmez.** Repo private olsa bile git history kalıcıdır ve sızan bir key doğrudan sahibin faturasıdır. Bu bir güvenlik programı değil, tek satırlık bir `.gitignore` kuralıdır ve 10A'da uygulanır.
- Bu karar hiçbir kabul edilmiş modeli geri almaz; kapsamı daraltır. Etkilediği ileri adımlar: **10A** (key saklama sadeleşmesi, `minSdk` sabitleme, gitignore kuralı), **9F** (device QA tek cihaz, store QA yok), **19** (release/dağıtım işinin büyük kısmı düşer).


## D-081 — Test stratejisi = TVSX-v0
**Durum:** Kabul edildi — 2026-08-29

- 9F final modeli `TVSX-v0 — Test & Verification Strategy` oldu ve **AŞAMA 9 kapandı**.
- Canonical spec `docs/TEST_STRATEGY_SPEC.md`; machine-readable contract `arch/9f_test_strategy/test_strategy.yaml`; research/decision synthesis `research/9f_test_strategy_research.md`.
- Ana invariant: **Üzerine hiçbir şeyin düşmediği bir garanti bir tercihtir.** Kabul edilmiş her invariant'ın, ihlal edildiğinde FAIL veren adı konmuş bir sahibi vardır; aksi hâlde invariant yalnız herkes hatırladığı sürece yaşar — ki `MSBX-v0` tam olarak bunu ortadan kaldırmak için yazılmıştı.
- **Beş kanonik spec test stratejisini açıkça bu adıma devretmişti** ve hepsi burada karşılandı: `AMTS-v0` (test stratejisi ve tooling), `LFPS-v0` (test stratejisi), `DDM-v0` (append-only'nin nasıl zorlanıp doğrulanacağı), `MSBX-v0` (dependency kuralının CI'da zorlanması), `AIAX-v0` (null-evaluator yolunun doğrulanması).
- **Altı doğrulama katmanı:** T1 pure domain (cihaz/ağ/DB/saat/AI yok), T2 persistence contract (gerçek storage engine), T3 structural (CI'da statik analiz, build time'da düşer), T4 presentation & accessibility, T5 adapter & integration (yalnız kayıtlı yanıtlar), T6 device smoke (tek hedef cihaz). **Cihaz katmanı en küçük katmandır**: cihaz dışında doğrulanabilen her şey cihaz dışında doğrulanır.
- **Coverage yüzdesi release gate değildir.** Bu proje learner state için proxy sayıları (mastery yüzdesi, gradebook, streak) zaten reddediyor; aynı gerekçe burada da geçerlidir — bir yüzde, önemli invariant'lar kontrolsüz kalırken yükselebilir. Gate **invariant coverage**'dır: sahipsiz bir invariant tek başına release'i bloklar.
- **Negatif doğrulama katman gereksinimidir.** Bir şeyi yasaklayan her kural için, yasaklananı deneyip **başarısız olmasını şart koşan** bir check gerekir. Yalnız izinli yolu çalıştıran bir check yasak hakkında hiçbir şey kanıtlamaz: truth tablosunda UPDATE/DELETE storage katmanı tarafından reddedilmeli, `core-*`in `data-*`/`ai-*`/`app-*`e bağımlılığı ve port imzasındaki platform tipi build'i düşürmeli, yarım kalan migration önceki state'i bozmadan `data_recovery_required` yüzeyletmeli, yeni schema'dan restore best-effort değil reddedilmeli.
- **Append-only schema seviyesinde doğrulanır**, çağıran kodda değil; düzeltme yolu append edilen `evidence_disposition` olarak doğrulanır ve orijinal satırın sonrasında değişmeden durduğu kontrol edilir.
- **Migration'lar dolu fixture'lara karşı doğrulanır** — `LFPS-v0` bunu zaten şart koşuyordu ve bu adıma kadar sahibi yoktu. Her önceki schema version'ı için evidence/exposure/provenance/version pin taşıyan fixture ileri migrate edilir; evidence, exposure ve provenance **birebir** korunur (sayım ve içerik, örnekleme değil); derived state atılıp yeniden kurulabilir ve rebuild'in aynı projeksiyonu ürettiği doğrulanır.
- **Hiçbir check canlı AI provider çağırmaz.** Canlı çağrı deterministik değildir, kullanıcının kendi parasını harcar, key'ini gerektirir ve hataları atfedilemez kılar. Yedi `AIAX-v0` sonucunun tamamı kayıtlı/sentezlenmiş yanıtlarla üretilir ve beş yanıtsızlık `evaluation_pending` üretip evidence yazmaz. Payload assertion'ı evidence history, mastery state, plan, profile, exposure, provenance ve planner trace'lerinin **gitmediğini** doğrular.
- **Null-evaluator yolu `ai-adapter` olmadan build alınarak doğrulanır**, yalnız stub'layarak değil. V1 kriteri 8 böylece umut değil wiring olur ve bu check'in düşmesi release'i bloklar.
- **Determinizm iddia edilmez, egzersiz edilir:** saat `ClockPort` üzerinden sabit instant/study day/offset ile enjekte edilir, hiçbir check sistem saatini okumaz, planner ve projection koşuları tekrarlanıp **byte-identical sıralı çıktı** vermek zorundadır, beraberlikler kasıtlı eşitliklerle sınanır ve gün sınırı/DST/seyahat vakaları üç-değerli zaman üzerinden çalıştırılır. **Ara ara geçen bir check düşmüş bir check'tir**; retry-to-green yasaktır çünkü mimarinin yasakladığı nondeterminizmi tam olarak gizlerdi.
- **Altı severity sınıfı**; `evidence_correctness` her zaman bloklar çünkü öğrencinin ne yapabildiğine dair yanlış bir iddia kozmetik bir kusur değildir. `structural` ve `data_safety` de her zaman bloklar.
- **Release gate 11 koşuldur** ve kısmi geçiş yoktur; içinde coverage yüzdesi **yoktur**. `V1_SCOPE`in on release kriterinin her biri en az bir geçen check'e eşlenir, hiçbir check flaky diye karantinada olamaz ve `tools/validate_*.py` **glob'un tamamı** geçmelidir — elle tutulan bir alt küme değil (9E POST'unda öğrenilen ders).
- **Invariant register machine-readable'dır:** 66 kayıt, her biri `MSBX-v0`, `LFPS-v0`, `DDM-v0`, `AIAX-v0`, `AMTS-v0`, `SPWX-v0`, `VDSX-v0` veya `WFPX-v0` kontratında **gerçekten var olan bir anahtar** ve beyan edilen değeriyle. 9F hiçbir yeni ürün semantiği icat etmez.
- **Mutation disciplini ürün suite'ine de uygulanır:** her invariant sınıfı için kasıtlı bir ihlalin adı konmuş bir check'i düşürdüğü gösterilmelidir. Bozuk bir implementasyona karşı geçen bir check, check değildir.
- **Doğrulamanın yapamadıkları açıkça yazıldı:** geçen bir suite ürünün kabul edilmiş modellerine uygun davrandığını kanıtlar; modellerin doğru olduğunu **kanıtlamaz**. Hiçbir release notu, log veya Progress yüzeyi geçen bir suite'i öğrenme kalitesi kanıtı gibi sunamaz.
- **Tooling kapasite olarak adlandırıldı, library olarak değil**; somut seçim ve version pinning 10A'ya aittir ve güncellik `AMTS-v0` emsaliyle iddia edilmez, doğrulanır.
- `D-080` etkisi: device QA tek hedef cihazdır, store-release doğrulaması kapsam dışıdır ve **hedef cihaz hâlâ repoda kayıtlı değildir**; 10A ve T6 için gereklidir.
- Independent 9F QA: **288/288 PASS**; 66 invariant register kaydı / 6 katman / 9 negatif check / 11 release gate koşulu / 19 forbidden test anti-pattern. Validator kararı kendine değil kaynak kontratlara doğrular: her register invariant'ı upstream yaml'da beyan edilen değeriyle aranır, AI sonuç kümesi `AIAX-v0` taksonomisiyle birebir karşılaştırılır, kontrast çapaları ve 48dp `WFPX-v0`den, migration/restore beklentileri `LFPS-v0`den okunur, beş upstream deferral ilgili spec'lerin ham metninde aranır ve `V1_SCOPE`in on kriteri metinden parse edilir. Ayrıca spec ve contract'ın hiçbir somut test library'si adlandırmadığı algoritmik kontrol edilir. Mutation test: 8 kasıtlı ihlal 8 FAIL verdi. Stage 6, Stage 7, AŞAMA 8 ve 9A–9E regressions PASS (26/26 validator).
- **Validator kendi iki hatasını yakaladı:** `E9F-16` başlangıçta her katmanın en az bir invariant sahiplenmesini şart koşuyordu, oysa T6'nın invariant sahiplenmemesi tasarımın kendisidir (cihaz dışında doğrulanabilen cihaz dışında doğrulanır) — check T6'yı dışlayacak ve onun yerine bir V1 kriteri sahiplenmesini şart koşacak biçimde daraltıldı. Library taramasındaki `truth` terimi ürünün kendi sözlüğüyle ("truth tables", "source of truth") çakışıp false positive verdiği için listeden çıkarıldı.
- Sonraki numbered step `10A — Mobil iskelet / proje kurulumu`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TEST_STRATEGY_SPEC.md`.


## D-082 — Proje kurulumu = MPSX-v0
**Durum:** Kabul edildi — 2026-08-31

- 10A final modeli `MPSX-v0 — Mobile Project Skeleton` oldu ve **repodaki ilk çalıştırılabilir çıktı** üretildi.
- Canonical spec `docs/PROJECT_SETUP_SPEC.md`; machine-readable contract `arch/10a_project_setup/project_setup.yaml`; research/decision synthesis `research/10a_project_setup_research.md`; Gradle projesi `android/`.
- Ana invariant: **Çalıştırılmamış hiçbir şey iddia edilmez.** Her version güncel bir kaynaktan okundu, her yapısal kural gerçek bir build'i düşürüyor ve kaydedilen her sonuç komut çalıştırılarak elde edildi.
- **1A–9F arası her adım birbirine karşı doğrulanan spec üretti; 10A'nın çıktısını bir makine çalıştırıyor** ve bu "doğrulanmış"ın anlamını değiştiriyor: build ya geçer ya geçmez, iç tutarlılık bunun yerine geçmez.
- **Build iki yanlışı hemen yakaladı ve ikisi de gizlenmeden kaydedildi:** AGP 9.3.0 uyumluluk tablosu Gradle minimumu olarak `9.5.0` okunmuştu fakat o dağıtım çözülmüyor ve wrapper 404 verdi — pin, Gradle'ın kendi sürüm servisinin güncel dediği **9.7.1** oldu. Android modülleri başlangıçta `org.jetbrains.kotlin.android` uyguluyordu; **AGP 9.0+ Kotlin desteğini yerleşik sağlıyor ve bu plugin'i açıkça reddediyor**. İkisi de "güncellik hatırlanmaz, doğrulanır" ilkesinin kanıtıdır.
- **Toolchain (2026-08-31'de doğrulandı):** AGP 9.3.0, Gradle 9.7.1, Kotlin 2.4.0, Compose BOM 2026.08.00 (Compose 1.12.0 / Material3 1.4.0), Compose Material 3 Adaptive 1.3.0, androidx.sqlite 2.7.0, JVM toolchain 17. `compileSdk = 37` (Compose 1.12 API 37'ye karşı derleniyor), `targetSdk = 36`, `minSdk = 26`.
- **Hedef cihaz kaydedildi ve `AMTS-v0` §8.1'in açık maddesi kapandı:** **Poco M6 Pro**, model `2312FPCA6G`, Android 16 / **API 36**, build `BP2A.250605.031.A3`, HyperOS `3.0.304.0.WNFMIXM.C10`, Helio G99-Ultra, 12+6 GB. `D-080` gereği bu **tek** hedef cihazdır; `TVSX-v0` T6 ve V1 kriteri 9 bir matris değil bu telefon demektir.
- **`minSdk` yükseltilmedi.** §8.1 "shim gerektirmeden gereken davranışı destekleyen **en düşük** seviye" diyor; cihazın API 36 olması 26'yı yükseltmek için gerekçe değil, tercih olurdu. 26 aynı zamanda `java.time`ın native olduğu seviyedir ve retention/review zamanlaması bu sayede desugaring bağımlılığı taşımaz.
- **`AMTS-v0` §9'un altı maddesi kapandı:** (1) `WindowSizeClass` + `NavigationSuiteScaffold` + pane scaffold'lar; `WFPX-v0` geometrisi kanonik kalır, library yalnız mekanizmayı sağlar, (2) dynamic colour opt-in'dir — `dynamicLightColorScheme`/`dynamicDarkColorScheme` hiç çağrılmaz ve tema `WFPX-v0` ölçülmüş token'larıyla `lightColorScheme`/`darkColorScheme`'den kurulur; bir **yokluk** olduğu için source scan ile denetlenir, (3) `minSdk` 26 cihaza karşı doğrulandı, (4) `Modifier.semantics` ile `stateDescription`/`contentDescription`/`heading`/live region ve `isTraversalGroup` + `traversalIndex`, (5) reduced motion için ayrı bayrak yok; `Settings.Global.ANIMATOR_DURATION_SCALE` animasyonlar kapalıyken `0f` okur, (6) `minSdk` 26'da **hiçbir compatibility library gerekmiyor**.
- **On modül `android/` altında** `MSBX-v0` ile birebir; her modülün beyan ettiği project dependency'ler o kontratın `depends_on` listesine **eşit** ve bu makine tarafından kontrol ediliyor, böylece build ile kabul edilmiş sınır birbirinden ayrışamıyor.
- **Yalnız iki `app-*` modülü Android modülüdür.** `data-*` ve `ai-*`ın Android plugin'i uygulamaması bilinçlidir: `TVSX-v0` T2'nin gerçek storage engine'e karşı ve adaptör doğrulamasının kayıtlı yanıtlara karşı **cihaz dışında** koşmasını sağlayan şey budur.
- **Dependency kuralı build'i düşürüyor.** Root build'deki `verifyModuleBoundaries` katmanı tanınmayan modülü, yasak kenarı (`core` → `data`/`ai`/`app`), yalnız composition root'un her katmana ulaşabilmesini ve **renklendirmeli DFS ile hesaplanan** cycle'ı kontrol eder; `check`e bağlıdır. Üçüncü parti architecture-rule library kullanılmadı: kural Gradle'ın kendi project dependency'leri üzerinde tam olarak ifade edilebiliyor ve bir library boşuna güncellik riski eklerdi. Çalıştırıldı: **10 modül, yasak kenar yok, cycle yok.**
- **Kural mutation-test edildi:** `core-model → data-persistence` beyan edildiğinde build hem yasak kenarı hem yarattığı cycle'ı bildirerek düştü. O çıktıyı okurken check'in kendi **cycle raporlaması** hatalı bulundu — ilk cycle'dan sonra traversal stack artık sadık bir yol değil ve sonraki zincirler yanıltıcıydı; artık yalnız ilk cycle raporlanıyor.
- **AI adaptörü olmadan build gerçekten alınıyor.** Seam bir runtime dalı değil source-set seçimi: `:ai-adapter` `settings.gradle.kts`te koşullu, `src/withAi/kotlin` adaptör destekli evaluator'ı, `src/withoutAi/kotlin` ise `core-application`da sevk edilen `NullEvaluator`ı sağlıyor. `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` **BUILD SUCCESSFUL** ve 10 yerine **9 modül** yapılandırıyor. V1 kriteri 8 böylece tek komutla gösterilebilir hale geldi.
- **Çalıştırılan runlar:** RUN-01 `verifyModuleBoundaries` (T3) PASS; RUN-02 core testleri (T1) PASS; RUN-03 `:app-wiring:assembleDebug` (T5) PASS ve `app-wiring-debug.apk` üretildi (V1 kriteri 10 için APK üretilebilirliği); RUN-04 adaptörsüz build (T5) PASS.
- **Bilinçli yoklar, gerekçeleriyle:** DI framework yok (tek object graph, tek kullanıcı, framework annotation'ı core'a sokmama), ORM yok (`DDM-v0` library-neutral schema ve `TVSX-v0`ın storage seviyesinde zorlanan append-only'si el yazımı SQL/trigger'ı gerektiriyor; `androidx.sqlite` bundled driver T2'yi JVM'de koşturuyor), architecture-rule library yok, HTTP client yok (AIAX çağrı noktaları 14'te), `org.jetbrains.kotlin.android` yok.
- **Saat tam olarak tek yerde okunuyor** — `app-wiring`deki `ClockPort` implementasyonu — ve validator bunu kaynak taramasıyla doğruluyor.
- **Credential hijyeni (`D-080`):** `.gitignore` `android/local.properties`, `android/secrets.properties`, `*.keystore` ve `*.jks` kapsıyor. Gerekçe dar ve somut: repo private olsa bile git history kalıcıdır ve sızan key doğrudan sahibin faturasıdır. `AIAX-v0`ın `key_in_platform_secure_storage` invariant'ına dokunulmadı ve validator dokunulmadığını ayrıca kontrol ediyor.
- **CI** (`.github/workflows/android.yml`) T3 → T1 → adaptörsüz build → adaptörlü build sırasını ve ayrıca `tools/validate_*.py` **glob'unun tamamını** koşuyor. **T6 kasıtlı olarak CI'da yok**: device tier hedef cihazda elle koşuyor ve bir emülatör job'unu device tier gibi sunmak release gate'e kontrol etmediği bir şeyi iddia ettirirdi.
- Independent 10A QA: **127/127 PASS**; validator 10A'nın kendi iddialarını değil **gerçek Gradle dosyalarını ve Kotlin kaynaklarını** okuyor — modül kümesini ve her modülün beyan ettiği bağımlılıkları `boundaries.yaml`a, katman komutlarını `TVSX-v0`a, platform seviyelerini `AMTS-v0` politikasına ve cihazın kendi API seviyesine, credential kurallarını `AIAX-v0` ile `D-080`e karşı doğruluyor. Mutation test: 7 kasıtlı ihlal (yasak modül bağımlılığı, dynamic colour kullanımı, ikinci saat okuması, DI framework beyanı, wrapper version kayması, `minSdk` kayması, adaptörsüz source set'in silinmesi) 7 FAIL verdi. Stage 6, Stage 7, AŞAMA 8 ve AŞAMA 9 regressions PASS (27/27 validator).
- Sonraki numbered step `10B — Navigation`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/PROJECT_SETUP_SPEC.md`.

## D-083 — Navigation = NSHX-v0
**Durum:** Kabul edildi — 2026-09-04

- 10B final modeli `NSHX-v0 — Navigation Shell` oldu.
- Canonical spec `docs/NAVIGATION_SHELL_SPEC.md`; machine-readable contract `arch/10b_navigation/navigation.yaml`; research/decision synthesis `research/10b_navigation_research.md`; kod `android/core-presentation` + `android/app-ui`.
- **10B dış araştırma gerektirmedi.** İhtiyaç duyulabilecek tek ekosistem bilgisi — güncel adaptive navigation API'leri — 10A'da doğrulanıp pinlenmişti. `AMTS-v0`ın "güncellik doğrulanır" disiplininin amacı tam olarak bu: doğrulama toolchain'i pinleyen adımda bir kez yapılır, sonraki adım onu harcar.
- Ana invariant: **navigasyon kuralları `core-presentation`da yaşar, UI toolkit'inde değil.** Destination kümesi, sırası, hangi surface'ların paylaşıldığı, hangi geçişlerin var olduğu ve focused flow'un nereye döndüğü saf fonksiyonlardır; bir ihlal telefonda fark edilen bir şey değil, laptop'ta düşen bir testtir.
- **Neden Compose'da değil:** route string'leriyle dolu bir `NavHost` dört kabul edilmiş kararı UI toolkit'ine sokardı. `MSBX-v0` bunu presentation state için zaten reddetmişti — üründeki en güvenlik-kritik etiketleme yalnız cihazda test edilebilir hâle gelirdi. Navigasyon aynı sınıf karar: `UXIA-v0` "Learn ve Progress altında çelişkili Skill detail sayfaları"nı yasaklıyor ve bu bir **kimlik** iddiasıdır; kimlik routing tablosuna değil modele aittir.
- **Dört destination, kabul edilmiş sırada** (`today → learn → progress → profile`), `today` başlangıç. Kotlin enum'unun bildirim sırası **o sıradır**; senkron tutulacak ikinci bir liste yok, yani yeniden sıralama kazara kayma değil bilinçli bir kaynak değişikliği olmak zorunda. Türkçe etiketler (`Bugün · Öğren · İlerleme · Profil`) çalışma microcopy'si ve sahibi 14; kanonik olan destination **id**'si ve sırası.
- Assessment, Technical English, AI Tutor ve her engine state **destination değil**. `UXIA-v0` on bir yasak top-level id sayıyor ve bir test hiçbirinin destination olmadığını doğruluyor.
- **Kanonik entity başına tek surface objesi.** Skill detail Today, Learn ve Progress'ten erişilebilir ve her seferinde **aynı** objedir. Origin başına bir route tutan bir kayıt defteri, yasaklanan çelişkili-detay ihlalini yazmayı kolaylaştırırdı; bu tasarım onu temsil edilemez yapıyor ve validator bildirim sayısını sayarak öyle kalmasını sağlıyor.
- **Contextual edge'ler sayılı ve kapalı**, kabul edilmiş IA kontratındaki kümenin aynısı. Listelenmemiş bir geçiş navigable değil (`profile → skill_detail` ve `learn → task_runner_flow` reddediliyor, ikisi de test edilmiş). Gerekçe: her surface her şeyi açabilseydi, öğrencinin izlediği yol curriculum yapısı gibi görünmeye başlardı — `UXIA-v0` §10.2'nin açıkça yasakladığı şey. Kod içindeki kenar kümesi validator tarafından `ia.yaml` ile karşılaştırılıyor, yani shell ile kabul edilmiş IA ayrışamaz.
- **Focused flow destination değildir; shell'i askıya alır.** `showsShell` ve `requiresSafeExit` parametre değil surface'tan **türetilir**; böylece tehlikeli durum — gizlenmiş shell ve çıkışsız ekran — inşa edilemez. Çıkışın kendisi `TRUX-v0`a ait ve `WFPX-v0` onu 48dp hedefe sabitliyor; shell'in tek işi üstüne navigasyon çizmemek. Detail pane de focused flow sırasında bastırılıyor: geniş pencere, odaklanılması gereken işin yanında bir gezinme yüzeyini canlı tutmamalı.
- **Dönüş kuralı deterministik:** normal günlük iş → `today`; entity context'inden açılan akış → origin **hâlâ geçerliyse** oraya; bayat ya da geçersizleşmiş origin → `today`. Ortadaki durum bir replan'in öğrenciyi mahsur bırakabileceği yer, bu yüzden geçerlilik varsayım değil açık bir girdi. Focused flow olmayan bir surface için dönüş hedefi istemek yüksek sesle hata veriyor.
- **Window class'lar `WFPX-v0`nin:** compact ≤599 (bottom bar), medium 600–839 (rail), expanded ≥840 (rail + detail pane). Shell `NavigationSuiteScaffold` ile çiziliyor fakat **eşikleri o belirlemiyor**: sınıf `core-presentation`da kabul edilmiş breakpoint'lerden hesaplanıyor, bir library varsayılanını değiştirse bile kabul edilmiş geometri kazanıyor. 599/600/839/840 sınır değerleri test edildi. **Window class yalnız çizimi değiştirir**; küme, sıra ve anlam üç sınıfta da aynı (`UXIA-v0` §16).
- **Erişilebilirlik:** her destination metin etiketi render ediyor ve ikonun content description'ı null — etiket zaten adlandırdığı için ikon hiçbir zaman tek anlam taşıyıcısı değil; seçim `stateDescription` ile metin olarak veriliyor; `traversalIndex` kanonik destination sırasını izliyor, yani screen-reader gezinme sırası layout'un tesadüfünü değil kabul edilmiş sırayı takip ediyor.
- Çalıştırılan runlar: `:core-presentation:test` (T1) PASS, `verifyModuleBoundaries` (T3) PASS, `:app-wiring:assembleDebug` (T5) PASS ve APK üretildi, `-PwithAiAdapter=false` (T5) PASS. Sonuncusu bu adımın ötesinde önemli: shell V1 kriteri 8'i zayıflatmadan indi ve ürün hâlâ AI adaptörü olmadan build alıp çalışıyor.
- Independent 10B QA: **104/104 PASS**. Validator 10B'nin kendi kontratını değil **gerçek Kotlin kaynağını** okuyor ve destination id/sırasını, yasak top-level kümesini, 15 contextual edge'i, 6 shared detail'i ve dönüş semantiğini `UXIA-v0`nin `ia.yaml`ından; window class'ları ve shell eşlemesini `WFPX-v0`nin `wireframe.yaml`ından alıp karşılaştırıyor. Mutation test: 8 kasıtlı ihlal (destination sırası, yasak destination, fazladan edge, ikinci skill_detail surface'ı, breakpoint kayması, türetilmeyen shell suppression, UI'da hardcoded id, metin etiketinin kaldırılması) 8 FAIL verdi. Stage 6, Stage 7, AŞAMA 8, AŞAMA 9 ve 10A regressions PASS (28/28 validator).
- Sonraki numbered step `10C — Design system implementation`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/NAVIGATION_SHELL_SPEC.md`.


## D-084 — Design system implementation = DSIX-v0
**Durum:** Kabul edildi — 2026-09-04

- 10C final modeli `DSIX-v0 — Design System Implementation` oldu.
- Canonical spec `docs/DESIGN_SYSTEM_IMPL_SPEC.md`; machine-readable contract `arch/10c_design_system/design_system_impl.yaml`; research/decision synthesis `research/10c_design_system_research.md`; kod `android/core-presentation` (token + tone) ve `android/app-ui` (tema).
- **10C dış araştırma gerektirmedi.** Renkler zaten **seçilmiş değil ölçülmüş**ti (`WFPX-v0` tema başına kaydetti) ve sabit palet sağlama mekanizması 10A'da doğrulanıp pinlenmişti.
- Ana invariant: **tasarım sistemi, kanonik state'in iddia etmediği severity'yi ekleyemez** — ve bir kural yalnız gözden geçirilmek yerine **temsil edilemez** kılınabiliyorsa öyle yapılır.
- **Token'lar tema dosyasında değil `core-presentation`da.** Hex değerleri yalnız Compose'un içinde yaşasaydı kontrastı doğrulamak UI toolkit'i gerektirirdi ve doğal kısayol, hesaplamak yerine hatırlanan bir oranı iddia etmek olurdu. Bu kısayol bu projede bir kez zaten başarısız oldu: 8G'de elle beyan edilen bir minimum yanlıştı ve yalnız yeniden hesaplama yakaladı. Şimdi token'lar düz veri, WCAG formülleri yanlarında ve ürünün kendi suite'i her oranı cihazsız/Compose'suz bir JVM testinde **yeniden hesaplıyor**.
- **Palet birebir kopyalandı ve burada revize edilmedi**: tema başına 18 token, iki temada aynı token kümesi, dark light'ın tersi değil. Yeşil–amber–kırmızı rampası yok; `attention` menekşe ve kırmızı yalnız `system_fault`ın, çünkü bir trafik ışığı rampası öğrenme state'lerini severity seviyeleri gibi kodlardı. Validator Kotlin kaynağındaki her token'ı kabul edilmiş palet ile bayt bayt karşılaştırıyor, yani bir check'i geçirmek için sessizce yapılan bir ayar düşerdi.
- **Kontrast iddia edilmiyor, yeniden hesaplanıyor** — hem ürün testinde hem repo validator'ında, **iki temada da**: her iki yüzeyde metin ve muted metin ≥ 4.5:1, outline ve focus ring ≥ 3:1, altı tone container'ının her birinde on-tone metin ≥ 4.5:1 ve her tone container'ı yüzeye karşı ≥ 3:1. Validator ayrıca kayıtlı minimumları (6.08 / 3.79 / 6.06) token'lardan **yeniden türetiyor** ve üçü de birebir tuttu.
- **Fault tone bir learning state için temsil edilemez kılındı.** `Tone` altı değerli ve `SYSTEM_FAULT` içeriyor; **`LearningTone` beş değerli ve atanacak bir fault değeri yok**; `SkillPresentationState.tone` `LearningTone` döndürüyor. Gözden geçirilecek bir kod yolu yok, çünkü yazılacak bir ifade yok. Fault tone'u yalnız `SystemFaultState` (`error_recoverable`, `data_recovery_required`) üretiyor ve Material'ın `error` rolü yalnız onu taşıyor — böylece bir bileşen "hata rengi"ne uzanıp öğrencinin state'ine uygulayamıyor.
- Sekiz Skill state'inin her birinin **tam olarak bir** beyan edilmiş tonu var ve kabul edilmiş harita ile eşleşiyor. Üçü bilinçle nötr kalıyor: `not_yet_evidenced`, `confirmed_review_due` ve `prerequisite_unresolved` **bekleme** state'leri, başarısızlık değil. Dört qualifier kendi tonunu taşıyor ve state'inkini değiştirmiyor.
- **Attention grubunda görünmek tonu değiştirmiyor** ve bu kural, state'in kendi tonunu döndüren adı konmuş bir fonksiyon — böylece niyet, kodun yokluğuyla ima edilmek yerine test edilebiliyor. Bir davranış mutation'ı (fonksiyonun tonu yükseltmesi) Kotlin testini düşürdü.
- 48dp hedef tabanı per-component disiplin yerine bir `minimumTouchTarget()` modifier'ı; desteklenen metin ölçekleri %100/150/200; state chip'i etiketini her zaman **metin** olarak render ediyor ve `stateDescription` ile veriyor, yani renk hiçbir zaman tek anlam taşıyıcısı değil; hiçbir yerde locale-naive case transform yok.
- **Dynamic colour kapalı kaldı ve bu bir yokluk olduğu için source scan ile denetleniyor. Scan bu adımın kendi yorumunu yakaladı:** tema dosyasının açıklaması kuralı iki dynamic scheme builder'ının adını yazarak anlatıyordu ve düz metin taraması bir yorumu bir çağrıdan ayıramaz. **Gate gevşetilmedi, yorum yeniden yazıldı** — katı yokluk daha güçlü bir garanti; "geçmesi serbest ama çağırmak yasak" diyen bir kural, şu an ihtiyaç duymadığı bir parser ve bir yargı gerektirirdi.
- Çalıştırılan runlar: `:core-presentation:test` (T1) PASS, `verifyModuleBoundaries` (T3) PASS, `:app-wiring:assembleDebug` (T5) PASS, `-PwithAiAdapter=false` (T5) PASS.
- Independent 10C QA: **146/146 PASS**. Validator 10C'nin kendi kontratını değil kabul edilmiş tasarım sistemini referans alıyor: her token `WFPX-v0` paletiyle, her state→tone ataması `VDSX-v0` haritasıyla karşılaştırılıyor ve her kontrast oranı hex'ten yeniden hesaplanıyor. Mutation test: 7 validator ihlali (token ayarı, `LearningTone`a fault eklenmesi, state tone değişimi, dynamic colour, locale-naive casing, hedef tabanının düşürülmesi, chip'ten state metninin kaldırılması) + 1 davranış ihlali (grouping'in tonu yükseltmesi) = 8/8 yakalandı. Stage 6, Stage 7, AŞAMA 8, AŞAMA 9, 10A ve 10B regressions PASS (29/29 validator).
- Sonraki numbered step `10D — Local database`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/DESIGN_SYSTEM_IMPL_SPEC.md`.


## D-085 — Local database = LDBX-v0
**Durum:** Kabul edildi — 2026-09-14

- 10D final modeli `LDBX-v0 — Local Database` oldu.
- Canonical spec `docs/LOCAL_DATABASE_SPEC.md`; machine-readable contract `arch/10d_local_database/local_database.yaml`; research/decision synthesis `research/10d_local_database_research.md`; kod `android/data-persistence`.
- Ana invariant: **storage engine, mimarinin yasakladığını reddeder.** Append-only truth, değişmez curriculum, version pinning ve kalıcı exposure, çağıran kodun uyacağına güvenilen kurallar değil; SQLite'ın reddettiği ifadeler — ve her ret **denenerek** kanıtlanıyor.
- **İlk taslak yanlıştı ve bu kaydedildi.** Bu adımın ilk şeması kontrattan değil önceki adımların hafızasından yazılmıştı ve kendi testleri de aynı taslağa karşı yazıldığı için geçiyordu. Validator yazılırken taslak `DDM-v0`nin `data_model.yaml`ına karşı okununca sapmalar ortaya çıktı: outcome ekseni `met/partially_met/not_met/not_reliably_measured` idi — kabul edilmiş değerler `positive/negative/partial/invalid`; `evaluator_status`ta `invalid`, `independence_class`ta `practice_only` ve `requires_independent_recheck` eksikti; UTC offset **saniye** tutuluyordu — kabul edilmiş alan `utc_offset_minutes`; evidence tek objective'e bağlıydı — kabul edilen Skill referansı + çoğul pinli objective referansları; 12 truth entity yerine 4 tablo vardı. Outcome hatası öğretici olanı: 10A'nın evaluator **sinyali** enum'u evidence **outcome** eksenine kullanılmıştı — benzer görünen iki farklı kavram. Şema kabulden önce kontrata göre yeniden yazıldı. Kalıcı ders: **bir taslağa karşı yazılmış suite, o taslağın kontratı yanlış okumasını yakalayamaz** — yalnız kontratı okuyan bir check yakalar.
- **Library API'si hatırlamadan değil, çözülmüş jar'dan `javap` ile okundu.** Doküman sayfası render olmadı ve 10A hatırlanan API'lerin bedelini zaten iki kez göstermişti.
- **Aynı şema iki yerde koşuyor:** JVM'de `sqlite-bundled-jvm` ile (T2 cihazsız, gerçek storage engine) ve cihazda `sqlite-bundled-android` ile. İkincisi **APK'nın içinde doğrulandı** — `lib/arm64-v8a/libsqliteJni.so` Poco M6 Pro'nun ABI'si için paketlenmiş — çünkü bir Android uygulamasının tükettiği JVM kütüphanesi yanlış varyant çözülürse başarıyla build alır ve telefonda native kütüphane yokluğundan çöker.
- **Üç store bölgesi `DDM-v0` ile birebir:** curriculum 11 tablo (değişmez: UPDATE ve DELETE yok), user truth 13 tablo (12 DDM truth entity + `evidence_event_objective`; append-only: UPDATE ve DELETE yok), projection 8 tablo (düşürülebilir ve yeniden kurulabilir). **User truth'tan curriculum'a foreign key yok**; bir test her truth tablosunun foreign key'lerini sayarak bunu koruyor.
- `evidence_event_objective`, `evidence_event`in çoğul `objective_logical_ids`/`objective_versions` alanlarının ilişkisel biçimi: hedeflenen her Objective için bir satır, her biri kendi version pin'iyle, böylece `evidence_by_objective` indekslenebiliyor. Yeni bir entity değil.
- **Her truth ve curriculum tablosunda abort eden `BEFORE UPDATE` ve `BEFORE DELETE` trigger'ları** var ve tablo envanterlerinden üretiliyor, yani hiçbir tablo kazara dışarıda kalamıyor. Evidence düzeltmesi append edilen bir `evidence_disposition`; orijinal satırın sonrasında bayt bayt değişmediği kontrol ediliyor.
- **Her `DDM-v0` izinli değer kümesi** — dört evidence ekseni, disposition ve kimin karar verdiği, assistance level/timing/scope/source, provenance origin, exposure kind — validator'ın kontratla karşılaştırdığı aynı Kotlin listesinden üretilen bir `CHECK` constraint.
- **Pinning yapısal:** versiyonlu entity'ler `(logical_id, version)` ile anahtarlanıyor, foreign key'ler version kolonunu taşıyor, evidence'taki resource referansı ya pinli ya yok — asla version'sız değil — ve bir projection yalnız logical id ile adreslenemiyor.
- **Zaman:** her timestamp'li satır `occurred_at_instant`, `occurred_on_study_day`, `utc_offset_minutes` taşıyor; DDM'nin kendi adlarını verdiği yerde onlar (`decided_at_*`, `answered_at_*`). Core zaman tipi offset'i saniye taşıyor, storage dakika; dönüşüm tam bölme ve **tam dakika olmayan bir offset kesilmek yerine reddediliyor**. Her gerçek zone offset'i tam dakika olduğu için ret yalnız bozuk girdide tetikleniyor — sessiz kesmenin en çok zarar vereceği an tam da orası.
- **Tek global truth sequence** her türden her truth satırında ilerliyor ve projection watermark'ı; tablo başına id bir projection'a kendisinden sonra exposure geldiğini söyleyemezdi. Geri alınan bir eylem onu ilerletmiyor.
- Her projection satırı `policy_version`, `truth_watermark`, `built_at_instant` ve `input_curriculum_version` taşıyor; port'taki `ProjectionRecord` son ikisini kazandı, yani provenance payload'a gizlenmek yerine tipin parçası. Bu yeni bir port değil, mevcut port'un DDM'nin istediği alanlarla tamamlanması. `skill_state` dört ekseni ayrı, primary presentation state'i yanlarında saklıyor — yerine değil.
- **Migration ileri-yönlü ve adım başına transaction'lı:** daha yeni bir schema reddediliyor; yarıda düşen bir adım `ROLLBACK` ile önceki durumu bozmadan bırakıyor ve `DataRecoveryRequired` yükseltiyor. Version 2 dokuz `DDM-v0` read-path indeksini ekliyor, böylece migration dolu bir veritabanında gerçekten egzersiz ediliyor. Dolu fixture 25 evidence, 10 exposure ve bir disposition taşıyor ve evidence migration sonrası **içerik olarak satır satır** karşılaştırılıyor.
- **Adapter kendi kolon listesini tutmuyor:** SQLite'a `PRAGMA table_info` ile soruyor ve kendisinin sahip olmadığı her default'suz `NOT NULL` kolonu zorunlu kılıyor. Elle tutulan ikinci bir liste şemadan sessizce kopar; böylece tek kaynak DDL.
- **Modelin adlandırmadığı kolonlar açıklandı:** `DDM-v0`nin alanlarını listelemediği entity'ler için yalnız kimlik, sequence, zaman ve DDM'nin ima ettiği referanslar sabitlendi, artı satırın ihtiyaç duyduğu en küçük içerik kolonu — her biri sahibi olan adımla birlikte (`assessment_session.scope` → 13, `artifact.content_ref` → 11, `planned_task.position` → 12, `planner_decision_trace.trace` → 12, `resume_checkpoint.context` → 11, `plan_version.policy_version` → 12, `domain/module/topic.name` → 11).
- **22 T2 check** JVM'de gerçek SQLite'a karşı koşuyor, fake repository yok; her yasak, yasaklanan ifade çalıştırılıp reddedilmesi şart koşularak kanıtlanıyor.
- **Mutation 9/9 yakalandı** — truth trigger'larının kaldırılması, curriculum trigger'larının kaldırılması, outcome kümesinin denetlenmemesi, version'sız resource referansı, learner eyleminden rollback'in kaldırılması, migration'ın transaction'sız hâle gelmesi, yeni schema'nın tahminle açılması, offset'in sessizce kesilmesi, truth sequence'in dondurulması. **Biri başta yakalanmadı:** migration-failure testi sabote edilmiş migration'ı ilk ifadede, hiçbir şey değişmeden düşürüyordu, yani `ROLLBACK` yerine `COMMIT` yazılsa da geçiyordu. Test, hatayı adım indekslerini oluşturup metadata satırını temizledikten **sonra** enjekte edecek biçimde yeniden yazıldı ve artık mutant'ı yakalıyor. Mutation testing olmasaydı hiçbir şey doğrulamayan bir test atomikliği doğruluyor diye kaydedilecekti.
- Independent 10D QA: **163/163 PASS**; validator 10D'nin kendi kontratını değil **gerçek Kotlin DDL'ini** `DDM-v0` `data_model.yaml`ı ve `LFPS-v0`a karşı okuyor. Validator'ın kendi mutation testi 9/9 — **ilk taslağın üç hatasının üçü de dahil** (yanlış outcome değerleri, saniye offset, eksik independence class). Stage 6, 7, AŞAMA 8, AŞAMA 9 ve 10A–10C regressions PASS (30/30 validator).
- Açık loop'lar: veritabanı `MainActivity`de main thread'de açılıyor ve `DataRecoveryRequired` şu an çökme — ikisi de 10E; backup/export ve atomik doğrulanmış restore `LFPS-v0`de tanımlı ve `TVSX-v0`de doğrulanmış ama burada implemente edilmedi; curriculum içerik yüklemesi 11.
- Sonraki numbered step `10E — Temel uygulama sağlığı`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/LOCAL_DATABASE_SPEC.md`.


## D-086 — Temel uygulama sağlığı = APHX-v0
**Durum:** Kabul edildi — 2026-09-14

- 10E final modeli `APHX-v0 — App Health` oldu ve **AŞAMA 10 kapandı**.
- Canonical spec `docs/APP_HEALTH_SPEC.md`; machine-readable contract `arch/10e_app_health/app_health.yaml`; research/decision synthesis `research/10e_app_health_research.md`; kod `core-model` (`StoreHealth.kt`), `core-application` (`StoreStartup.kt`), `core-presentation` (`AppHealth.kt`), `data-persistence` (`StoreOpener.kt`, `Backup.kt`), `ai-adapter`, `app-ui` (`HealthSurface.kt`), `app-wiring` (`CoachApplication.kt`).
- Ana invariant: **store'un hiçbir arızası çökme değildir ve hiçbir arızası reset değildir.** Açılışın ters gidebileceği her yol kabul edilmiş bir state'e dönüşür; hiçbiri öğrencinin verisini silmez, kesmez, yeniden oluşturmaz veya birleştirmez — ve "hiçbir şey kıpırdamadı" bir status'a güvenerek değil **byte karşılaştırmasıyla** kanıtlanır.
- **Handoff iki sorun söylüyordu; kodu kontratlara karşı okumak iki sorun daha buldu.** (1) veritabanı `MainActivity.onCreate`de senkron açılıyor ve her activity yeniden yaratılışında tekrar açılıyordu, (2) `DataRecoveryRequired` yakalanmamış bir exception'dı — daha yeni şema veya yarım migration uygulamayı çökertiyordu, (3) **açılışta bütünlük hiç kontrol edilmiyordu** — `LFPS-v0` §12 bunu şart koşuyor, (4) **varsayılan build'in `AiEvaluator.evaluate`i `TODO()` idi** ve ilk açık uçlu denemede çökecekti: CI'ın normal build dediği adaptörlü build güvensiz olandı, adaptörsüz build güvenliydi.
- **Store sürecin, activity'nin değil.** `CoachApplication` bir `StoreStartup`ı bir kez başlatır; activity'ler yalnız gözlemler, rotation hiçbir şeyi yeniden açmaz. Orkestrasyon `core-application`da olduğu için garantiler JVM testidir: açılış çağıranın thread'inde koşmaz (latch'te tutulan opener ile — senkron bir `start()` testi `Opening` gözlemlemek yerine asardı), `start()` idempotent, fırlatan opener `RecoverableFailure` olur ve store vermeden `Ready` iddia eden opener'a inanılmaz. Veritabanı yolu da arka planda çözülür; debug build'lerde StrictMode disk erişimini loglar. **Yeni port eklenmedi**; `MSBX-v0`nin dört portu aynı.
- **Sebep tip olarak taşınır, mesaj ayrıştırılmaz.** `StoreStatus` (`Opening/Ready/RecoveryRequired/RecoverableFailure`) ve `RecoveryReason` (`INTEGRITY_CHECK_FAILED/NEWER_SCHEMA/MIGRATION_INCOMPLETE`) `core-model` tipleri; `Migrations.DataRecoveryRequired` artık sebebini taşıyor. Sınır bütünlüğün şüpheye düştüğü yer: dosya hiç açılamazsa hiçbir şey okunmadı ve retry anlamlı (`error_recoverable`); okurken veya kontrol ederken bir şey düşerse bütünlük tam olarak belirsiz olan şeydir (`data_recovery_required`).
- **Yazmadan önce kontrol.** `StoreOpener.open` asla fırlatmaz; sıra: `quick_check` + `foreign_key_check` → ileri migration → migration koştuysa tam `integrity_check`. Bozuk dosya migration ile "onarılmak" yerine bulunduğu gibi raporlanır. **Hiçbir hata yolu dosyayı silmez, kesmez veya yeniden oluşturmaz**: her recovery vakası dosyayı byte byte karşılaştırır ve sidecar oluşmadığını kontrol eder. Test edilen biçimler: veritabanı olmayan dosya, kesilmiş veritabanı, geçerli başlık arkasında bozuk b-tree sayfası, motorun arkasından yazılmış FK ihlali, yalnız tam kontrolün görebildiği index-içerik uyuşmazlığı, yeni şema ve bir şeyi değiştirdikten sonra düşen migration.
- **`quick_check` ile `integrity_check` farkı varsayılmadı, fixture ile kanıtlandı:** tanımı artık girdileriyle eşleşmeyen bir index `quick_check`ten geçer (ön koşul olarak assert ediliyor) ve migration sonrası tam kontrol onu yakalar.
- **Öğrencinin gördüğü:** `core-presentation` `AppHealth.of(storeStatus, evaluatorAvailability)` hesaplar, `app-ui` çizer. Vokabüler `UXIA-v0`nin altı cross-cutting state'i, onun sırasında ve `VDSX-v0` tonlarıyla; fault tonu yalnız `error_recoverable` ve `data_recovery_required`. Öncelik `THUX-v0`: `data_recovery_required` → `error_recoverable` → `loading` → normal kullanım. **Shell yalnız normal kullanım mümkünken çizilir.** `ai_unavailable_core_available` yalnız core gerçekten çalışırken üretilir ve asla bloklamaz. Her state kelimeyle söylenir, polite live region ile duyurulur; recovery ekranı hiçbir verinin silinmediğini, sıfırlanmadığını ya da yeniden oluşturulmadığını söyler. `empty_valid` (11) ve `offline_local_available` (14) beyan edildi ama üretilmiyor.
- **`HealthAction`ın tek değeri `RECHECK`** ve aynı, değiştirmeyen açılışı yeniden koşar. Reset/wipe/delete/recreate değeri **yok**, yani uyumsuz veri üzerinde "baştan başla" sunan bir ekran yazılamaz — sessiz olmayan bir reset düğmesi de resettir.
- **AI yokluğu çökme değil:** `AiEvaluator` 14 çağrı noktalarını verene kadar `evaluation_pending(unavailable)` döndürür ve kendini `UNAVAILABLE` bildirir; iki source set de availability raporlar.
- **Restore: mekanizma 10E'de, kontroller 16D'de — kullanıcının bu adımdaki kararı.** Arşiv ürünün kendi şemasında bir SQLite veritabanı: yapısı gereği tam (truth, exposure, provenance, curriculum pin'leri, schema/policy version store'da) ve şemadan kopabilecek ikinci bir serileştirme yok; hiçbir arşiv kolonunun credential taşıyamayacağı test ediliyor. Export `VACUUM INTO` + restore'un uygulayacağı doğrulamanın aynısı, yani geri yüklenemeyecek bir yedek ihtiyaç günü değil yapıldığı an bulunur; var olan dosyanın üstüne yazmaz. Restore: arşiv **kopyalanır** (kullanıcının yedeği hiç açılmaz), kopya tam integrity + FK kontrolünden geçer, şema versiyonu olmalı (yoksa `NOT_A_PROFILE_ARCHIVE`), yeniyse reddedilir, eskiyse kopya üzerinde ileri migrate edilip yeniden kontrol edilir ve profil şemasının her tablosunu içermelidir; **ancak sonra** canlı dosya **tek atomik rename** ile değiştirilir; eski canlı dosyanın yanındaki journal önce kenara alınır (rename düşerse geri konur) çünkü SQLite onu yeni dosyaya geri oynatırdı; sonuç `StoreOpener` ile yeniden açılır. Reddedilen arşiv **hem canlı profili hem arşivi byte byte değiştirmeden** bırakır. Birleştirme yok.
- **Mutation 16/16 — ama hepsi ilk denemede değil, ve kayıt bunu söylüyor.** **M08 (eski journal taşındı) başta yaşadı:** test çöp bir journal yazıyordu, SQLite geçersiz başlığı asla hot saymaz, set-aside silinse de geçiyordu; test artık transaction ortasında gerçek bir çökme görüntüsü yakalıyor ve journal'ın geri oynatıldığını ön koşul olarak kanıtlıyor. **M09 (eksik profil şeması kabul) başta yaşadı:** tek "profil değil" testinde metadata tablosu yoktu, daha önceki kontrol reddediyordu ve tablo kümesi kontrolü hiç egzersiz edilmiyordu; yeni test metadata'sı ve bütünlüğü sağlam ama `exposure_record` tablosu olmayan bir arşiv — geri yüklemek her exposure kaydını sessizce kaybettirirdi. **M02'nin ilk hali derlenmedi** ve bu yakalama sayılmaz; derlenecek biçimde yeniden yazılıp koşuldu. Mutation öncesi iddiaları gözden geçirmek migration sonrası tam kontrolü kanıtlayan hiçbir şey olmadığını buldu; index-içerik fixture'ı eklendi ve M04'ü o yakalıyor.
- Çalıştırılan runlar: T1 core testleri PASS, `:data-persistence:test` (T2) PASS — 46 test, `verifyModuleBoundaries` (T3) PASS, `:ai-adapter:test` (T5) PASS, `:app-wiring:assembleDebug` (T5) PASS, `-PwithAiAdapter=false` (T5) PASS. CI'a `:ai-adapter:test` adımı eklendi.
- **Çalıştırılmayan: T6.** Poco M6 Pro 10E sırasında bağlı değildi. Uygulamanın cihazda `Ready`e ulaştığı, recovery ekranının orada çizildiği veya TalkBack ile duyurulduğu, StrictMode'un cihaz main thread'inde sessiz kaldığı ya da atomik rename'in cihaz dosya sisteminde doğru davrandığı iddia edilmiyor. Restore ortasında gerçek süreç öldürme test edilmedi; set-aside ve rename sırası akıl yürütmedir, çökme testi değil.
- Independent 10E QA: **152/152 PASS**. Validator 10E'nin kendi kontratını değil **gerçek Kotlin kaynağını** `UXIA-v0` `ia.yaml`, `THUX-v0` `home.yaml`, `VDSX-v0`, `LFPS-v0`, `TVSX-v0`, `AIAX-v0` ve `MSBX-v0`ye karşı okuyor; bazı check'ler varlık değil **dosya içindeki sıra** üzerine (bütünlük migration'dan önce, doğrulama değiştirmeden önce), çünkü varlık kontrolü ikisini yanlış sırada yapan kodu geçirirdi; yorumlar taranmadan önce siliniyor. Validator'ın kendi mutation testi 12/12 ve negatif kontrol (kuralı yalnız yorumda anmak) false positive vermedi. Validator ilk koşuda kendi hatasını da gösterdi: YAML çıplak adım numaralarını (11, 12, 14) integer okuyor; karşılaştırma adım kodu olarak düzeltildi, kontrat değişmedi. Stage 6, 7, AŞAMA 8, AŞAMA 9 ve 10A–10D regressions PASS (31/31 validator).
- Sahipleri kaydedilen sınırlar: export/restore Profile kontrolleri ve değiştirilen profilin saklanıp saklanmayacağı → 16D; tüm profil kaybını tespit (sıfır uzunluklu veya eksik veritabanı ilk açılıştan ayırt edilemez ve korunacak veri taşımaz) → 19B; `empty_valid` → 11; `loading_projection`/`recomputing_projection` → 12; `offline_local_available` ve final microcopy → 14; açılış ve bütünlük kontrolü bütçeleri → 18E.
- **AŞAMA 10 TAMAMLANDI** — MPSX-v0 → NSHX-v0 → DSIX-v0 → LDBX-v0 → APHX-v0.
- Sonraki numbered step `11A — Today ekranı`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/APP_HEALTH_SPEC.md`.


## D-087 — Today ekranı = TDYX-v0
**Durum:** Kabul edildi — 2026-09-17

- 11A final modeli `TDYX-v0 — Today Interior` oldu; **AŞAMA 11 başladı**.
- Canonical spec `docs/TODAY_INTERIOR_SPEC.md`; machine-readable contract `arch/11a_today/today_interior.yaml`; research/decision synthesis `research/11a_today_research.md`; kod `core-model` (`TodayFacts.kt`), `core-presentation` (`TodayPresentation.kt`), `core-application` (`TodayFactsQuery.kt`), `app-ui` (`TodayScreen.kt`), `app-wiring`.
- Ana invariant: **Today kanonik planner/state gerçeğinin bir projeksiyonudur.** Kendisine verilmeyen hiçbir şeyi hesaplamaz, yani ikinci bir planner, mastery engine, prerequisite engine, gradebook ya da İngilizce kota sistemi olamaz. Bu adımda bu bir disiplin sözü değil: planner henüz yok ve Today ekranı doldurmak için plan uydurmak yerine dürüst boş/yükleniyor state'lerini gösteriyor.
- **Kodu kontratlara karşı okumak iki kusur daha buldu.** (1) `FileContentSource.resource` `TODO()` idi — yayımlanmamış içerik isteyen ilk çağıran çökecekti; `ContentPort` zaten "kayıt yok" için `null` diyor. Bu, 10E'nin AI adaptöründe bulduğu kusurun aynısı. (2) `Surface.all` başlatılmış bir `val` idi ve başka bir modül kendi initializer'ından bir surface'a referans verdiğinde kayıt **null** içeriyordu: 11A `TodayView`'dan `Surface.ProgressOverview`ı anar anmaz 10B'nin kendi geçen testi `NullPointerException` ile düştü. Bu, 10D'nin `truthGuards()` ile karşılaştığı Kotlin başlatma-sırası tuzağı; kayıt artık erişimde hesaplanıyor ve yapısal bir test onu koruyor.
- **Vokabülerler sahiplerinden kopyalandı, seçilmedi:** `THUX-v0`nin 12 semantic state'i ve 6 adımlı precedence'ı kendi sıralarında; `TASK_TAXONOMY_SPEC` §3.1'in yedi canonical purpose'ı (İngilizce bir track'tir, purpose değil); 8 reason ve 7 attention family; `WFPX-v0`nin region sırası; `VDSX-v0`nin tonları — fault tonunu yalnız `error_recoverable` ve `data_recovery_required` taşır, boş gün/kapasite sınırı/bekleme nötrdür çünkü hiçbiri başarısızlık değildir.
- **Yazılamayanlar:** reason tipinde serbest metin alanı yok (private constructor + `fromTraceFacts`, yani planner'ın kaydetmediği bir gerekçe inşa edilemez ve AI'ın uyduracağı bir metnin yeri yoktur); satırda mastery/score/yüzde/streak/rank alanı yok; kapasite yalnız izinli beş özet alanını taşır; **kapasite hükmü sunumda türetilmez** — iki tahmini burada karşılaştırmak Today'in planner'ın ne seçebileceğine sessizce karar vermesi olurdu, bu yüzden hüküm bir girdidir.
- **Gösterilmek yerine süzülenler:** başka bir study day'in planı satır olmaz (`SRR-v0` dünkü planı backlog diye oynatmayı yasaklıyor); blocked iş ne başlatılabilir ne listelenir ve hepsi blocked'sa state `empty_no_eligible_task` olur; replanning sırasında hiçbir satır gösterilmez; kurtarma gereken store'da plan satırı çizilmez; revalidate edilmemiş oturum resume için sunulmaz; primary task'ın zaten temsil ettiği attention düşürülür (remediation görevi zaten remediation çağrısıdır).
- **`empty_valid` burada üretiliyor** — 10E'nin bu adıma bıraktığı state. Ayrım dürüst olan kısım: hiçbir şey yayımlanmadıysa `empty_no_open_need` + `empty_valid` context'i ve içerik yüklenmediğini söyleyen metin (mastery ya da hazırlık iddiası yok); yayımlanmış curriculum varsa ama plan yoksa `loading_initial_plan`; kapasite sıfır ya da hiçbir aday sığmıyorsa kapasite state'leri, çünkü bunlar planın yokluğunu kendileri açıklar ve buna "yükleniyor" demek gelmeyecek bir işi vaat etmek olurdu.
- **Read path plan okumuyor ve bu bir eksiklik değil pozisyondur:** bir Today satırı purpose, trace'e dayalı gerekçe ve süre ister; `DDM-v0`nin `planned_task` tablosunda bu kolonlar yok ve 10D bunları planner'ı yazan 12'ye bırakmıştı. Var olan kısmi satırları okuyup gerisini makul varsayılanlarla doldurmak, bir ekranın hiçbir engine'in karar vermediği şeyleri iddia etmeye tam olarak böyle başlamasıdır. Kapasite de okunmuyor: Profile ayarı 16D'nin ve varsayılan bir sayı öğrencinin seçmediği bir sayı olurdu.
- **Read path yalnız okur, store thread'inde koşar ve resume'da yenilenir** — gece yarısını geçen bir süreç yoksa dünün study day'ini taşır ve bayat study day, bayat planın "bugünün planı" olmasının yoludur.
- **Port refinement:** `PersistencePort` `curriculumPublished()` kazandı; beşinci port değil, core tipleriyle ifade edilmiş bir incelme (10D'nin `ProjectionRecord` emsali). Port sayısı dört kaldı ve bir check bunu koruyor.
- **Modül yerleşimi:** `core-application` `core-presentation`a bağlı olamayacağı için iki tarafın da ihtiyaç duyduğu facts `core-model`de; projeksiyon `core-presentation`da; render `app-ui`da; sunum girdisi composition root'ta değil bir `core-presentation` fonksiyonunda birleştiriliyor.
- **Mutation 16/16 — üçü ancak testler güçlendirildikten sonra.** M08 (türetilmiş kapasite hükmü) başta yaşadı çünkü test vakası kapasite dalına hiç ulaşmıyordu. **M09 (attention'ın primary'yi tekrarlaması) zayıf testten fazlasını gösterdi:** kural primary *action kind*'a göre yazılmıştı ve tek test onu tetikleyemeyen recovery dalını kullanıyordu; `THUX-v0`nin gerçek kuralı primary *task*'ın konusuyla ilgili olduğu için kural purpose karşılaştırmasına göre yeniden yazıldı. M16 (eager surface registry) yaşadı çünkü NPE yalnız onu ortaya çıkaran yükleme sırasında tekrarlanıyor; artık yapısal bir test backing field olmadığını doğruluyor. M07'nin ilk hali davranışı değiştirmediği için yakalama sayılmadı, yeniden yazıldı.
- Çalıştırılan runlar: T1 core testleri PASS, `:data-persistence:test` (T2) PASS — 47 test, `verifyModuleBoundaries` (T3) PASS, `:data-curriculum:test` + `:ai-adapter:test` (T5) PASS, adaptörlü ve adaptörsüz `assembleDebug` (T5) PASS. CI'ın adaptör adımı içerik adaptörünü de koşuyor.
- **Çalıştırılmayan: T6.** Telefon yine bağlı değildi; Today'in cihazda nasıl çizildiği, TalkBack sırası, %200 metin ve gece yarısı yenilemesi doğrulanmadı. Gerçek bir store'a karşı boş ve yükleniyor dışında hiçbir Today state'i görülmedi, çünkü henüz plan yazan bir planner yok.
- Independent 11A QA: **150/150 PASS**. Validator 11A'nın kendi kontratını değil **gerçek Kotlin'i** `THUX-v0`, `UXIA-v0`, `VDSX-v0`, `WFPX-v0`, `MSBX-v0`, `APHX-v0` ve `TASK_TAXONOMY_SPEC`e karşı okuyor; bazı check'ler "yazılamaz"ı yapısal olarak kontrol ediyor. Validator'ın kendi mutation testi 16/16 ve yorum-içi negatif kontrol false positive vermedi — **ama biri başta kaçtı:** bayat-plan filtresi kontrolü dosyada "herhangi bir yerde" arıyordu ve aynı ifade `queueOf()` içinde de geçtiği için ana yolu filtresiz bırakan mutant'ı geçiriyordu; check iki fonksiyon gövdesini ayrı ayrı arayacak biçimde daraltıldı. Stage 6, 7, AŞAMA 8, 9, 10 regressions PASS (32/32 validator).
- Sahipleri kaydedilen sınırlar: start action ve Task Runner girişi → 11B; resume checkpoint içeriği ve session state → 11C; curriculum ingestion → 11D; planner/plan/trace/kapasite özetleri → 12; kapasite ayarları ve Profile kontrolleri → 16D; final microcopy ve offline state → 14; cihazda erişilebilirlik cilası → 17.
- Sonraki numbered step `11B — Task runner`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TODAY_INTERIOR_SPEC.md`.


## D-088 — Task runner = RNRX-v0
**Durum:** Kabul edildi — 2026-09-19

- 11B final modeli `RNRX-v0 — Task Runner` oldu.
- Canonical spec `docs/TASK_RUNNER_SPEC.md`; machine-readable contract `arch/11b_task_runner/task_runner.yaml`; research/decision synthesis `research/11b_task_runner_research.md`; kod `core-model` (`AttemptFacts.kt`), `core-presentation` (`TaskRunner.kt`), `core-application` (`SubmitAttempt.kt`), `app-ui` (`TaskRunnerScreen.kt`), `app-wiring`.
- Ana invariant: **Task Runner bir execution surface'tir.** Planner, mastery otoritesi, prerequisite otoritesi ya da evidence evaluator değildir; bu yüzden görev sıralayamaz, state değiştiremez, önbellekteki listeyi ilerletemez ve bir denemeyi geçti/kaldı yapamaz.
- **Runner kodundan önce main'de bir kusur bulundu:** 11A'nın sync betiği Türkçe metni `"İ".lower()` ile kurmuştu; Python'da bu noktalı bir `i` artı **U+0307 COMBINING DOT ABOVE** üretir. Beş kelime (dört "açık", bir "taşımaz") `AGENTS.md`, `PROJECT_CONTEXT.md`, `HANDOFF_STATE.md` ve `START_HERE.md`e girmişti. Düzeltildi ve 11B validator'ı artık repodaki herhangi bir metin dosyasında U+0307 görürse düşüyor. Guard ilk koşuda kendini yakaladı — dosyayı yazan araç kaçış dizisini gerçek karaktere çevirmişti — ve karakter artık `chr(0x0307)` ile üretiliyor.
- **Vokabülerler `TRUX-v0`den sırasıyla:** 17 state, 6 faz, 5 giriş ve 5 resume koşulu, 3 pause sınıfı ve tek bir sonraki-görev kaynağı (yeniden hesaplanmış planner seçimi). Tonlar `VDSX-v0`nin: `blocked_not_startable` `attention`, `evaluation_pending` `pending_unresolved`. **İlk taslak tonları `app-ui` içinde elle seçmişti**; tesadüfen `VDSX-v0` ile birebir aynıydılar ama tesadüf garanti değil ve UI toolkit yanlış modül, harita `core-presentation`a taşındı.
- **Girişte hiçbir şey varsayılmaz:** her koşul doğrulanmalı; doğrulanamayanlar `unmet` diye adlandırılır, `failed` değil — henüz hiçbir şeyin doğrulayamadığı koşul başarısızlık değildir, runner da bir varsayımla başlamaz. Today yalnızca görevinin hâlâ seçili olduğunu doğrulayabilir; prerequisite, açık ihtiyaç, içerik uyumu ve yerel yetenek başka engine'lerin olguları ve hiçbiri henüz yok. **Bu yüzden bugün hiçbir görev başlatılamaz ve dürüst sonuç budur** — kural gevşetilerek değil, engine'ler olgu sağlayarak değişecek.
- **Yardım:** her zaman istenebilir; istenmeden verilmez; H3/H4 sonuç ölçüm diliyle açıklanmadan asla verilmez; ilk hatada çözüm açılmaz. **Kural `TRUX-v0`nin yazdığı gibi, kapsam koşulu olmadan uygulanıyor** — hedef-kapsamlı yardıma daraltmak makul bir okuma olurdu ama kabul edilmiş bir kontratın sessiz yeniden yorumu olurdu; daraltan mutant yakalanıyor.
- **Durmak her zaman `stopped_no_penalty`**, çıkış her state'te ilk öğe, mid-segment pause kaydedilmiş ilerleme gibi gösterilmez, pending değerlendirme ne geçti ne kaldı.
- **Bir deneme bir transaction:** attempt, artifact, öğrencinin provenance cevabı ve her assistance event birlikte commit olur ya da hiçbiri. **Evidence yazılmaz** — runner evidence evaluator değildir (12). Tek zaman damgası; türetilmiş olgular (`highest_assistance_level`, `requires_independent_recheck`) saklanmaz. Her değer kümesi `DDM-v0`, `TRUX-v0` **ve şemanın kendi CHECK listeleriyle** eşit. Kanıt ikiye bölündü: T1 use case'in şeklini, T2 aynı satır dizisinin gerçek SQLite'ta atomik olduğunu kanıtlıyor.
- **Port refinement:** `appendTruth` artık eklenen satırın id'sini döndürüyor; beşinci port değil.
- **Saklanmayanlar, sahipleriyle:** attempt üzerinde `planned_task_ref` ve `runner_completion_state` → 12 (`DDM-v0` attempt alanlarını adlandırmıyor ve 10D alan uydurmayı yasakladı), component results/target objectives → 12, artifact gövdesi depolaması → 11D, resume checkpoint içeriği → 11C.
- **Today'in başlat aksiyonu artık runner'ı açıyor:** giriş core'da doğrulanıyor, runner focused flow olarak kabuğu askıya alıyor, çıkış Today'e dönüyor.
- **Mutation 18/18, hepsi ilk turda.** R16 (`appendTruth` row id yerine truth sequence döndürürse) kaçması beklenen mutanttı çünkü ilk denemede ikisi de 1; yakalandı ama id kontrolüyle değil **foreign key** ile — artifact'in sequence'i (2) hiçbir artifact satırını göstermiyor ve SQLite provenance eklemesini reddediyor. Kayıt gerçekten yakalayan mekanizmayı adlandırıyor.
- Çalıştırılan runlar: T1 PASS, `:data-persistence:test` (T2) PASS — 50 test, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS.
- **Çalıştırılmayan: T6.** Gerçek uygulamada runner şu an yalnız `blocked_not_startable` gösterebiliyor; sonuç açıklamasının ulaşılabilir çağıranı yok; atomik gönderim yalnız T1/T2'de kanıtlı.
- Independent 11B QA: **147/147 PASS**; validator'ın kendi mutation testi 18/18 ve yorum-içi negatif kontrol false positive vermedi. Stage 6–11A regressions PASS (33/33 validator).
- Sonraki numbered step `11C — Session state`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TASK_RUNNER_SPEC.md`.


## D-089 — Session state = SESX-v0
**Durum:** Kabul edildi — 2026-09-19

- 11C final modeli `SESX-v0 — Session State` oldu.
- Canonical spec `docs/SESSION_STATE_SPEC.md`; machine-readable contract `arch/11c_session_state/session_state.yaml`; research/decision synthesis `research/11c_session_state_research.md`; kod `core-model` (`SessionFacts.kt`), `core-presentation` (`SessionState.kt`), `core-application` (`ResumeCheckpoints.kt`), `core-ports` + `data-persistence` (`readTruth`), `app-ui` (`TaskRunnerScreen.kt`), `app-wiring`.
- Ana invariant: **bir duraklatma işin nerede olduğunu saklar, ne kadar sürdüğünü ya da ne kadar iyi gittiğini değil.** Yalnız durable pause yazılır ve yalnız yazılan pause kaydedilmiş gösterilir; resume yalnız saklanan checkpoint'in kanıtladığını doğrular; oturum tarihtir, puan değil.
- **Kod yazmadan önce bulunanlar:** (1) `TRUX-v0` high-stakes pause'un *işaretlenmesini* şart koşuyor ama `ResumeContext` alan listesi pause sınıfını taşımıyor — saklanmayan bir işaret resume'da uygulanamaz; saklanan bağlam durable pause türünü taşıyor ve bu yeni bir anlam alanı değil, `TRUX-v0`nin istediği işaret. (2) `PersistencePort` truth satırını geri okuyamıyordu. (3) Main'de 11A'nın sync'inden (1cbe4aa) kalan iki bozuk Türkçe kelime: dört living dokümanda çift noktasız ı'lı "çalıştırılmadı" ve `STEP_STATUS`ta ş'si düşmüş "doğrulanmamış oturum"; 11B'nin U+0307 guard'ı ikisini de göremiyordu. Düzeltildi ve 11C validator'ı tam bozuk biçimleri yakalıyor.
- **Checkpoint:** `SRR-v0` `ResumeContext` (`learning_need_key`, `source_task_id`, `checkpoint_id`, `completed_segments`, `remaining_segments`, `artifact_state_ref?`) + `kind`. Süre, deneme sayısı, puan, ilerleme oranı, seri saklanmaz. Biçim `resume_context/1`, değerler yalnız kimlik token'ları (kaçış kuralı yok), çözme **katı**: eksik/bilinmeyen/tekrar/sırası bozuk anahtar ya da yanlış sürüm tahminle değil *hiçbir şeyle* sonuçlanır; okunamayan satır "yok" ile karışmaz.
- **Bir pause bir transaction, tek append-only satır;** attempt, evidence, projection yok; tüketildi/son bayrağı yok (truth UPDATE'i ya da türetilebilir bir kopya olurdu). Hangi checkpoint'in kullanılacağı planner'ın `resume_context_ref`'i (12). Şema değişmedi, migration yok.
- **Durable ne zaman:** high-stakes iş her yerde durable ve işaretli; sıradan iş yalnız dört güvenli-checkpoint koşulunun hepsi *doğrulanınca*; aksi `mid_segment_pause`, doğrulanmayanlar `unmet`. Mid-segment pause yazılmaz ve runner state'ini değiştirmez; `CheckpointKind`in iki durable değeri var. Bugün hiçbir sıradan pause durable değil — segment anlamı ve artifact kalıcılığı 11D'nin olguları.
- **Resume:** checkpoint en çok iki koşulu doğrular — çözülen ve artifact göstermeyen bağlam için `runner_and_artifact_state_intact`; yalnız sıradan pause için `high_stakes_gap_integrity_acceptable` (`TRUX-v0` koşulu high-stakes işe kapsamlıyor). **High-stakes gap eşiği uydurulmadı**; politika 13'ün, kalibrasyon 18D'nin. İçerik uyumu, prerequisite ve açık ihtiyaç checkpoint'ten asla doğrulanmaz; bugün hiçbir checkpoint devam ettirilemez ve `resume_invalidated` suçlamadan ve hiçbir şeyin silinmediğini söyleyerek gösterilir. **Today checkpoint sunmaz** — yeniden giriş planner'ın `continue_learning` ihtiyacıdır.
- **Working session:** `TRUX-v0` §4'ün altı alanı; puan, not, yüzde, süre ya da zorunlu sayı tutabilecek alan yok. Başlayan ilk koşuyla başlar (blocked giriş başlatmaz); bir kez biter (öğrenci çıkışı → `user_stopped`, boş seçim + planner'ın kapasite hükmü → `capacity_reached`, boş seçim aksi → `plan_exhausted`, çıkışsız ayrılma → `interrupted`, store kurtarması → `recovery_required`); sebeplerin tonu ya da hükmü yok. **Saklanmaz:** `DDM-v0` adlandırmıyor ve 10D entity uydurmayı yasakladı; kalıcı tarih 16B'nin.
- **Port refinement:** `readTruth(kind, id)`; yazma yolu eklemez; port sayısı dört kaldı.
- **Mutation 20/20, hepsi ilk turda ve hepsi testle** — mekanizmayı kaydetmek için koşu tekrarlandı; hiçbiri yalnız derleyiciyle yakalanmadı.
- **Düzeltme (11D, D-090):** bu sayıyı üreten koşucu Gradle'ı hiç çalıştırmamıştı (`cmd /c gradlew.bat` çalışma dizininden çözülmüyor), yani her mutant yanlış sebeple "yakalandı" sayılmıştı. 11D koşucuyu düzeltti ve bu seti dürüstçe yeniden koştu: **20/20, hepsi düşen bir testle**. Sayı aynı kaldı, artık gerçekten doğrulanmış durumda. Ayrıca 11C'nin POST sync betiği `PROJECT_CONTEXT.md` 12.15 bölümünü dört kez eklemişti; mükerrerler 11D'de temizlendi.
- Çalıştırılan runlar: T1 PASS, `:data-persistence:test` (T2) PASS — 55 test, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS.
- **Çalıştırılmayan: T6.** Gerçek uygulamada hiçbir aktivite aktif işe ulaşmadığı için pause, checkpoint, resume ya da oturum görülmedi.
- Independent 11C QA: **146/146 PASS**; validator'ın kendi mutation testi 20/20 — biri (Today'e checkpoint sızması) başta kaçtı çünkü kontrol çağrı argümanları yerine olmayan bir gövdeyi okuyordu; düzeltildi. Yorum-içi negatif kontrol false positive vermedi. Stage 6–11B regressions PASS.
- Sonraki numbered step `11D — Günlük mikro quiz`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/SESSION_STATE_SPEC.md`.


## D-090 — Günlük mikro quiz = DMAX-v0
**Durum:** Kabul edildi — 2026-09-21

- 11D final modeli `DMAX-v0 — Daily Micro Assessment Implementation` oldu.
- Canonical spec `docs/DAILY_MICRO_ASSESSMENT_IMPL_SPEC.md`; machine-readable contract `arch/11d_daily_micro_assessment/daily_micro.yaml`; research/decision synthesis `research/11d_daily_micro_assessment_research.md`; kod `core-model` (`AssessmentFacts.kt`, `CurriculumPackage.kt`, `ArtifactBody.kt`), `core-presentation` (`AssessmentSession.kt`), `core-application` (`DailyMicroAssessment.kt`), `data-persistence` (`CurriculumStore.kt`), `data-curriculum` (`PackageFormat.kt`, `FileContentSource.kt`), `app-ui` (`AssessmentSessionScreen.kt`), `app-wiring`.
- Ana invariant: **assessment session bir kanıt toplama akışıdır** ve **bir item yalnız validation'ının, evaluator'ının ve Objective'in kendi kanıt profilinin izin verdiği kadarını taşıyabilir.** Ne oturum ne item bir otoritedir: oturum gradebook ya da ikinci state engine olamaz, item kendini yükseltemez.
- **Kod yazmadan önce bulunanlar:** (1) immutable curriculum bölgesinin **hiç yazıcısı yoktu**; kuralları hiç gerçek bir yazmaya karşı denenmemişti. (2) `DDM-v0` `QAB-v0` item'ının yalnız bir kısmını adlandırıyor; kalanı sütun uydurmak yerine authored içerikte kaldı. (3) Artifact gövdesinin yeri yoktu. (4) İlk referans çözücü yalnız pakete bakıyordu, yani sonraki bir sürümün yalnız revalidation taşıması imkânsızdı — bunu T2 kontrolü buldu, inceleme değil. (5) **Mutation koşucusu Gradle'ı hiç çalıştırmamıştı.**
- **Curriculum ingestion:** `publishCurriculum` tek yazma yolu; tek transaction; ret yazmadan önce karar verilir; yayımlanmış sürüm asla üzerine yazılmaz ve düzeltme yeni sürümdür; çözülmeyen referans, paketten başka sürüm taşıyan satır ve tek token olmayan evidence type paketin tamamını reddeder; user bölgelerine hiçbir şey yazılmaz. Authored paket `curriculum_package/1` katı ayrıştırılır ve ayrıştırılamayan paket hiçbir parçasıyla sunulmaz. **İçerik bu adımda yazılmadı** (15).
- **Item güveni:** mağazanın validation kaydı belirler, dokümanın kendi iddiası değil. Etkin tavan uygulanabilir **en kısıtlayıcı** kuraldır: `candidate` → `practice_only`; `ai_generated` ve `trusted` değilse en çok `standard_mastery_eligible`; deterministic doğrulama yoksa provisional evaluator ile en çok `low_stakes_assessment`; deterministic şartsa ve yoksa `practice_only`; evaluator çalışamıyorsa ve deterministic yoksa `practice_only`. Tavan deklare edileni asla aşmaz. **Kanıt uyumuna Objective karar verir** ve mastery ölçümü Objective'in direct tipini ister; bilinmeyen profil uyumlu varsayılmaz. Uygun olmayan item negatif kanıt değildir.
- **Exposure** yalnız item gerçekten sunulduğunda (`item_version_seen`) ve çözüm gösterildiğinde (`solution_exposure`) yazılır; görülmemiş item için hiçbir şey yazılmaz ve kayıtlar kalıcıdır.
- **Tek interior** (`ASUX-v0`) kodda: 19 state ve tonlar sahiplerinden; scope yalnız gösterilen bağlam; atomic evidence boundary gönderim birimi ve testlet bölünmez; gönderilen sınır donar; gönderilmemişler serbestçe gezilir; boş bırakmak yanlış değildir; yardım engellenmez, H1/H2 assisted, H3/H4 practice-only + yeni görülmemiş soru; mod dönüşümü açıktır ve **oturum recheck'i planlamaz**; beş koşullu recomposition donmuş sınıra dokunmaz; itiraz kanıtı contested tutar ve kullanıcının durumunu kötüleştirmez; sonuç semantiktir, `not_reliably_measured` birinci sınıftır, ham sayımlar ayrı tiptedir ve **kanonik değişiklik yoksa değişiklik iddia edilmez** — puan/yüzde/not/eşik alanı yoktur.
- **Artifact gövdesi** kısa yanıt için referansın içinde RFC 2397 `data:` URI olarak taşınır: attempt'in kendi transaction'ında commit olur, 10E'nin doğrulanmış export'una zaten dahildir ve hiçbir tablo, kolon, port ya da kabul edilmiş kontrat değişmedi. 4096 karakterin üstü **reddedilir**, kırpılmaz; gerçek blob store 14/15'in.
- **Port incelmeleri:** `publishCurriculum`, `resourceVersion`, `latestValidation`, `objectiveProfile`, `assessmentItem`, `curriculumPackage`. Yeni interface yok; port sayısı dört.
- **Mutation 27/27** — üçü ilk dürüst koşuda hayatta kaldı ve her biri kuralda değil testlerde gerçek bir boşluk gösterdi; testler güçlendirildi.
- **11C düzeltmesi:** 11C'nin 20/20 mutation kaydı hiç koşmayan bir araçla üretilmişti; koşucu düzeltildi, set dürüstçe yeniden koşuldu ve sonuç **20/20** olarak doğrulandı. Ayrıca 11C'nin POST betiği `PROJECT_CONTEXT.md` 12.15 bölümünü dört kez eklemişti; mükerrerler temizlendi ve validator tekrarlanan bölüm başlığında düşüyor. 11A'nın `ContentDocument? = null` gövdesini pinleyen duran kontrolü 11D'nin gerçek adaptörüyle bayatladı ve sahip olduğu garantiye daraltıldı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS.
- **Çalıştırılmayan: T6.** Gerçek uygulamada hiçbir planner ölçüm seçmiyor ve içerik sevk edilmiyor, bu yüzden hiçbir oturum başlayamaz.
- Independent 11D QA: **188/188 PASS**; validator'ın kendi mutation testi 27/27 ve iki geçersiz mutant (derlenmeyen rename, no-op) geçerlileriyle değiştirildi; yorum-içi negatif kontrol false positive vermedi. Sweep 35/35.
- Sonraki numbered step `11E — Gün sonu`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/DAILY_MICRO_ASSESSMENT_IMPL_SPEC.md`.


## D-091 — Gün sonu = EODX-v0
**Durum:** Kabul edildi — 2026-09-21

- 11E final modeli `EODX-v0 — End of Day` oldu ve **AŞAMA 11 kapandı**.
- Canonical spec `docs/END_OF_DAY_SPEC.md`; machine-readable contract `arch/11e_end_of_day/end_of_day.yaml`; research/decision synthesis `research/11e_end_of_day_research.md`; kod `core-model` (`DayFacts.kt`), `core-presentation` (`EndOfDay.kt`), `core-application` (`DayCloseFacts.kt`), `core-ports` + `data-persistence` (`countTruth`), `app-ui` (`EndOfDayView.kt`, `TodayScreen`), `app-wiring`.
- Ana invariant: **gün sonu bir hüküm değil, zamanda bir sınırdır.** Gün, öğrencinin çalışma günü değiştiği için kapanır — öğrenci bir şeyi bitirdiği ya da bitiremediği için değil — ve kapanınca öğrencinin durumu hakkında hiçbir şey değişmez. Günü kapatan bir öğrenci aksiyonu yoktur.
- **Kabul edilmiş bir gün-sonu spec'i yoktu.** Bu, bu tür ürünlerin seri, halka, günlük hedef ve "bugün 42 dakika çalıştın" için uzandığı tek yer olduğu için önemliydi: kurallar uydurulmadı, parçaların sahiplerinden türetildi — state'ler ve sayma kuralları `SPWX-v0`, yokluk `SRR-v0`, gün `DDM-v0`nin üç değerli zamanı, öncelik `APHX-v0`, tonlar `VDSX-v0`, bölge `THUX-v0`, kapalı surface kümesi `NSHX-v0`.
- **Gün satırın kaydettiği çalışma günüdür.** Sayım satırın kendi `*_study_day` kolonuna karşı yapılır; instant aralığına karşı asla — yeniden hesaplamak, yaz saati değişiminin ya da bir uçuşun işi bir günden diğerine sessizce taşımasının yoludur. Kendi günü olmayan bir tablo (bir başka satırın parçası olanlar) sayılmaz, projection ve curriculum tabloları truth diye sayılamaz.
- **Yeni gün boş başlar.** Envanter, yarım plan ya da yükümlülük sınırı geçmez; gün geriye dönmez; borç taşıyabilecek bir API yoktur. `SRR-v0` zaten gelinmeyen günün borç olmadığını söylüyor.
- **Söylenebilenler:** yazılanların etiketli envanteri (`SPWX-v0` §counting_rules) — toplam, oran, yüzde ya da hedef yok ve sayım ilerleme değil; yalnız kanonik bir engine bildirdiyse bir değişiklik (`learning_history` aileleri); okunamayan sayımın "okunamadı" olarak adlandırılması. Evidence pipeline henüz olmadığı için özet "bugün bir değişiklik olmadı" diyor ve etkinlikten iddia üretmiyor.
- **Söylenemeyenler — alan olmadığı için:** günün başarılı/başarısız olması, seri ya da ardışık gün sayısı, tamamlanma yüzdesi/oranı, "çalışılan dakika", yarına geçen borç, etkinliğin öğrenme sayılması, kimsenin bildirmediği bir değişiklik. Boş gün `empty_no_evidence_yet` ve nötr; günler arası boşluk hiç çizilmez (ızgaradaki boş kare, yokluğun skora dönüşme biçimidir).
- **Render:** Today'in `day_plan_context` bölgesinde. `NSHX-v0`nin surface kümesi kapalı olduğu için gün sonuna yeni bir destination icat edilmedi; günler arası kalıcı geçmiş 16B'nin. Grafik, halka, bar ya da takvim ızgarası yok; her şey metin.
- **Port refinement:** `countTruth(kind, studyDay)`; hiçbir şey yazmaz, yeni interface yok, port sayısı dört.
- **Mutation 20/20.** E20 ilk turda hayatta kaldı: ikinci bir koruma (gün kolonu yok) birincisini (truth tablosu değil) maskeliyordu ve test iki reddi ayırt edemiyordu. T2 kontrolü artık her reddin kendi kuralını adlandırdığını doğruluyor — 11D'nin parser reddi için yaptığı düzeltmenin aynısı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS.
- **Çalıştırılmayan: T6.** Cihazda hiçbir şey doğrulanmadı; gece yarısı dönüşü enjekte saatle JVM testinde kanıtlandı. Gerçek uygulamada gün okunabiliyor ve boş okunuyor.
- Independent 11E QA: **128/128 PASS**; validator'ın kendi mutation testi 26/26 ve yorum-içi negatif kontrol false positive vermedi. Sweep 36/36.
- **AŞAMA 11 TAMAMLANDI** — `TDYX-v0 / D-087` → `RNRX-v0 / D-088` → `SESX-v0 / D-089` → `DMAX-v0 / D-090` → `EODX-v0 / D-091`. Günlük döngünün her yüzeyi kodda; planner ve içerik olmadığı için uygulama dürüstçe boş duruyor ve hiçbiri kendisine verilmeyeni uydurmuyor.
- Sonraki numbered step `12A — Mastery Engine v1`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/END_OF_DAY_SPEC.md`.


## D-092 — Mastery Engine v1 = MSTX-v0
**Durum:** Kabul edildi — 2026-09-21

- 12A final modeli `MSTX-v0 — Mastery Engine v1` oldu ve **AŞAMA 12 başladı**.
- Canonical spec `docs/MASTERY_ENGINE_IMPL_SPEC.md`; machine-readable contract `arch/12a_mastery_engine/mastery_engine.yaml`; research/decision synthesis `research/12a_mastery_engine_research.md`; kod `core-engines` (`MasteryEngine.kt`), `core-model` (`EvidenceFacts.kt`), `core-application` (`EvidencePipeline.kt`, `RebuildMastery.kt`), `core-ports` + `data-persistence` (`evidenceFor`, `truthWatermark`, `latestCurriculumVersion`).
- Ana invariant: **mastery tek bir soru sorar — yardımsız yapabiliyor mu?** Yardımlı iş, görülmüş çözüm, doğrulanmamış değerlendirme, itirazlı soru ve bozuk prerequisite üzerinde yapılmış iş skora girmez. Hiçbiri ceza değildir; başka bir sorunun cevabıdır.
- **Kod yazmadan önce bulunanlar:** (1) 11B ve 11D kasten kanıt yazmamıştı, yani `DDM-v0`nin dört ekseni ürün tarafından hiç yazılmamıştı ve mastery'nin okuyacağı bir şey yoktu. (2) `GRE-v0`ün Objective gate profili (`min_independent_groups`, `min_variant_families`, `requires_*`) `DDM-v0`de kolon değil; 11D'nin item metadata'sı gibi authored içerikte kalıyor ve varsayılanlar `GRE-v0`ün. (3) Grup rubric'i henüz yok; testlet'in `q_g`'si için grup satırlarının ortalaması stand-in olarak kaydedildi — bağımsız grup sayısını asla artıramaz, asıl koruma budur.
- **Uygunluk (§3.1):** dokuz koşul tek bir fonksiyonda ve **ihlal edilen kuralları döndürüyor**, böylece her dışlama açıklanabiliyor. Yardımlı kanıt saklanıyor, recheck tetikleyebiliyor ve remediation'a bilgi veriyor; yalnız "yardımsız yapabiliyor mu" sorusunun cevabı değil.
- **Gruplama ve pencere:** bağımlı grup tek gruptur (aynı soruyu on kez yanıtlamak bağımsız kanıtı şişirmez); pencere son beş gruptur; ortalama eşit ağırlıklıdır ve assistance/evaluator/difficulty/recency çarpanı yoktur — `GRE-v0` cold-start'ta kalibre edilmemiş katsayı uydurmamak için hepsini kaldırmıştı.
- **Kapılar ve toplama:** standart ve kritik kapılar ayrı; kritik Objective yalnız basic kanıtla geçemez; Objective kendi artifact'ini ya da transfer kanıtını isteyebilir. **Skill non-compensatory**: her required ve critical Objective kendi başına geçmeli — ortalama, bir Objective'deki parlak sonucun eksik olanı gizlemesine izin verirdi.
- **Histerezis (§16.2) iki yarısıyla:** ilk temiz, prerequisite-valid, bağımsız çelişki `verification_due` açar ve mastery'yi **korur**; doğrulama zaten açıkken gelen yeni çelişki gürültü değildir, doğrulama kapanır ve kapılar yeniden karar verir. Yalnız birini uygulamak farklı bir ürün üretirdi: biri hiç güncellenmeyen, diğeri tek hatada panikleyen.
- **Kanıt yazımı:** deneme başına tek transaction, hedeflenen Objective başına bir satır, Objective sürümü kendi satırında pinli. **Yanıtsız değerlendirme hiçbir şey yazmaz.** Dört eksen ayrı yazılır. **`not_reliably_measured` `invalid` ve sonuçsuzdur** — sıfır öğrencinin yaptığı bir şeydir, "ölçemedik" değil. Bağımsızlık kaydedilen yardımdan gelir, cevabın görünüşünden çıkarılmaz.
- **Projeksiyon:** yeniden kurulur, düzenlenmez; truth yazmaz; tam provenance taşır; watermark kanıttan önce okunur; yayımlanmış curriculum yoksa hiçbir şey yazılmaz; yalnız mastery ekseni yazılır ve diğerleri taşınır. `primary_presentation_state` şimdilik mastery ekseni olarak raporlanıyor — `SPWX-v0` onu dört eksenden türetir ve tek eksenden uydurmak o kontratın yasakladığı "bütün gerçek" iddiası olurdu.
- **Sabitler:** `5`, `0.80`, `2`, `3` — hepsi `GRE-v0`ün kalibre edilmemiş cold-start sezgisi ve 18C'nin sahipliğinde. Hiçbiri olasılık, güven ya da yetenek tahmini değil ve hiçbiri yüzde olarak gösterilmiyor.
- **Mutation 33/33.** G26 ve G33 ilk dürüst koşuda hayatta kaldı ve ikisi de kuralda değil testlerde aynı tür boşluğu gösterdi: yalnız testlerin hiç kurmadığı bir durumda ortaya çıkan kural. Fixture'ların tamamı v1 olduğu için sürüm pini görünmüyordu ve aralık koşulunu deneyen test yoktu; ikinci bir Objective sürümü ve bir core-model invariant testi bunları kapattı.
- **Validator'ın kendi kusuru:** imza okuyucusu varsayılan parametre değerindeki `=` işaretinde duruyordu, yani dokuz kontrol boş metin okuyup sessizce geçiyordu. 11D'de aynı sınıf hata bulunmuştu; okuyucu artık parametre listesini dengeliyor.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS. `data-persistence` `core-application`a bağlanamadığı için kanıt 11B'deki gibi bölündü: use case şekli T1'de, depolama garantileri T2'de.
- **Çalıştırılmayan: T6.** Ayrıca motor uygulamada erişilebilir değil: hiçbir yol değerlendirme üretmiyor (12C).
- Independent 12A QA: **164/164 PASS**; validator mutation 37/37 ve yorum-içi negatif kontrol false positive vermedi. Sweep 37/37.
- Sonraki numbered step `12B — Prerequisite Engine`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/MASTERY_ENGINE_IMPL_SPEC.md`.

## D-093 — Prerequisite Engine = PRQX-v0
**Durum:** Kabul edildi — 2026-09-27

- 12B final modeli `PRQX-v0 — Prerequisite Engine` oldu.
- Canonical spec `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md`; machine-readable contract `arch/12b_prerequisite_engine/prerequisite_engine.yaml`; research/decision synthesis `research/12b_prerequisite_engine_research.md`; kod `core-engines` (`PrerequisiteEngine.kt`), `core-model` (`PrerequisiteFacts.kt`), `core-application` (`ResolvePrerequisites.kt`), `core-ports` + `data-persistence` (`skill`, `prerequisiteEdgesInto`).
- Ana invariant: **bir eksik prerequisite yalnız gerçekten ona bağlı işi bekletir.** `review_due` unutma değildir ve bloklamaz; soft eksik hiçbir şeyi kilitlemez; priority kapıyı aşamaz; bekleyen aday başarısız bir ihtiyaç değildir.
- **Kod yazmadan önce bulunanlar:** (1) Authored 950 kenarın hepsi `draft` (851'i hard) ve `KGC-v0` §27 draft'a runtime seçimi vermiyor; "draft'ı yok say" okuması içerik sevk edildiği anda her hard prerequisite'i sessizce düşürürdü. (2) Graph'ta tek strictness profili var: `default_prg_v0`. (3) `contamination_risk_if_missing` authored ama DDM'de kolonu yok. (4) Retention ve weakness motorları (13) henüz yazmadı. (5) `skill_state` dört motorun eksenini tek watermark altında taşıyor; her motor kendi eksenini kendi watermark'ıyla yazsaydı başka motorun bayat ekseni güncel görünürdü. (6) `prerequisite_readiness` (Skill'in prerequisite olarak değeri) ile `prerequisite_axis_state` (Skill'in kendi prerequisite'lerinin çözülüp çözülmediği) farklı sorular. (7) `contaminated` değeri adaptördeydi.
- **Readiness (§3):** dört değer, sayı değil. Açık remediation `not_ready`; çelişen mastery `uncertain`; doğrulanmamış mastery `not_ready`; doğrulanmış mastery `review_due` ile `ready_due`, `verification_due`/`at_risk` ile `uncertain`, aksi hâlde `ready`. **Değerlendirilmemiş eksen adlandırılır ve kötü haber sayılmaz**; eksik mastery istisnadır çünkü §3 onu açıkça `not_ready` sayar. Kapanan remediation yeni kanıt olmadan kilidi açmaz.
- **Eligibility (§4, §5):** normal hard + `uncertain` koşullu uygundur; kritik kaynak ya da adayın strict isteğiyle bekler; soft eksik asla bloklamaz; task'in kendi gereksinimi hard'dır ve iki yoldan istenen Skill hard sayılır; eksik readiness kapıyı kapalı tutar; karar sıradan bağımsızdır. Priority girdisi ve başarısızlık alanı yoktur.
- **Graph (`KGC-v0` §12, §27):** `published` ve `deprecated` yürürlükte; `retired` artık kapı değil; `draft` `edge_not_published`, `invalidated` `edge_invalidated`, bilinmeyen lifecycle ayrıca adlandırılır — hiçbiri düşürülmez, hiçbiri sessizce uygulanmaz. En yeni `edge_version` yürürlüktedir. `default_prg_v0` dışındaki strictness profili tahmin değil metadata sorunudur. Döngü `prerequisite_cycle`; yürüyüş her Skill'i bir kez okur. Readiness geçişli hesaplanmaz: bir Skill'in readiness'i kendi doğrulanmış mastery'sidir.
- **Contamination (§13):** bekleyen aday üzerindeki deneme `contaminated` snapshot'ı yazar ve mastery motoru onu `prerequisite_contaminated` olarak dışlar; değer artık `core-model`in ve adaptör onu okuyor.
- **Projeksiyon:** yalnız `PRG-v0`ın sahibi olduğu `prerequisite_readiness` yazılır; watermark mastery satırınınkidir, yoksa `0`; yayımlanmış curriculum yoksa hiçbir şey yazılmaz. `skill_state`in prerequisite ekseni ve primary presentation state tek watermark okumasıyla 12D'de birleştirilecek; bulgu 12A'nın taşımasına da uygulanır.
- **Mutation 44/44.** Harness'in bir hayatta kalanı raporlayabildiği yorum-içi bir no-op mutantla gösterildi.
- **Living memory hijyeni:** 12A senkronunun `MASTER_PLAN`a iki kez yazdığı 12B başlığı, `MASTER_PLAN`da tamamlanmış 9D–9F ve 10A–10E adımlarını hâlâ işaretsiz gösteren eski iskelet başlıkları ve `STEP_STATUS`ta 12A başlığı altında kalan 10E satırları bulundu ve düzeltildi; validator artık tekrarlanan adım başlığını düşürüyor.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS. Kanıt 11B ve 12A'daki gibi bölündü: kararlar T1'de, depolama garantileri T2'de.
- **Çalıştırılmayan: T6.** Ayrıca kapı uygulamada erişilebilir değil: onu soran planner yok (12C).
- Independent 12B QA: **184/184 PASS**; validator mutation 42/42 ve yorum-içi negatif kontrol false positive vermedi. Sweep 38/38.
- Sonraki numbered step `12C — Planner Engine v1`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md`.

## D-094 — Planner Engine v1 = PLNX-v0
**Durum:** Kabul edildi — 2026-09-28

- 12C final modeli `PLNX-v0 — Planner Engine v1` oldu.
- Canonical spec `docs/PLANNER_ENGINE_IMPL_SPEC.md`; machine-readable contract `arch/12c_planner_engine/planner_engine.yaml`; research/decision synthesis `research/12c_planner_engine_research.md`; kod `core-engines` (`PlannerEngine.kt`), `core-model` (`PlannerFacts.kt`, `PlanTraceCodec.kt`), `core-application` (`BuildDailyPlan.kt`), `core-ports` + `data-persistence` + `data-curriculum` (`publishedSkills`, `taskCandidates`).
- Ana invariant: **önce semantik öncelik, sonra fiziksel sığma.** Priority bloklanmış, geçersiz ya da güvenilmeyen görevi kurtaramaz; kapasite önceliği yeniden yazmaz; gün uzatılmaz; hiçbir görevin karşılamadığı ihtiyaç açık kalır, yarının borcu ve başarısızlık değildir.
- **Kod yazmadan önce bulunanlar:** (1) `planned_task` yalnız Skill ve pozisyon taşıyor; amaç, başlık, etkinlik, dakika ve gerekçe için kolon yok. (2) Hiçbir authored görev yok; `curriculum_package/1`in görev bölümü yok. (3) Hiçbir yerde starvation eşiği yok; `PBR-v0` §6.5 18B/18C'ye bırakıyor ve 7C QA'sı eşik uydurmayı açıkça reddetmiş. (4) Retention ve weakness henüz yazılmadı. (5) Authored her Skill `draft`. (6) İhtiyaç başına aday sınırı 3B §17'de açıkça 12C'ye bırakılmış.
- **Kapasite (D-033):** bugünkü değişiklik → günün profili → planlı varsayılan → normal profil; sert bütçe asla aşılmaz; planlama bütçesi `floor(sert × 0,90)`; `10` dakikanın altında rezerv gevşer ve yeni öğretim yapılmaz. `30/60/90` bir ayar ekranının (16D) sunduğu varsayılanlar; planner hiçbir ayarın yerine kendi varsayılanını koymaz.
- **İhtiyaçlar (3B):** remediation weakness ekseninden, verification çelişen mastery ya da retention doğrulamasından, review retention'dan, devam mastery sürerken, yeni öğrenme kanıtsız mastery'den. Yazılmamış eksen, `draft` ya da `retired` Skill hiçbir şey açmaz; `deprecated` Skill yeni başlangıç açmaz.
- **Aday ve sıra (`PDT-v0` §17):** adaylar içeriğin ihtiyaca cevabı; planner görev uydurmaz. İhtiyaç başına en çok `5` aday, kararlı sırada ve `deprecated` en sonda; bu öğrenme anlamı olmayan bir mühendislik sınırı (18E). `assess`/`retain`/`diagnose` doğrulanmış aday ister. Her aday öncelik hesaplanmadan kapıya sorulur; cevapsız aday bloklanmış sayılır.
- **Öncelik (`PBR-v0`):** beş bant ve on alanlı rank vektörü, alan alan karşılaştırılır ve toplanmaz, kararlı tie-break. P0 gerçek bir blocker ister (kapıda bağımlı işi gerçekten bekleten kritik Skill); kritik etiket tek başına P1'dir. `review_due` bakımdır. Starvation planlı ilerlemeyi P2'ye kaldırır, onarımı geçemez; baskı girdidir ve ürün hiçbirini vermez.
- **Seçim:** sığ → güvenli böl → küçük alternatif → ertele; atomik kanıt sınırı bölünmez; bir ihtiyaca tek görev. Sığmayan yüksek öncelikli ihtiyaç zaman yüzünden ertelenir ve iz bunu söyler (`PDT-v0` §18) — 'daha az önemli' diye etiketlenmez.
- **Plan truth'tur:** `plan_version`, `planned_task` ve `planner_decision_trace` tek transaction'da eklenir ve düzenlenmez; watermark durumdan önce okunur; yayımlanmış curriculum yoksa hiçbir şey yazılmaz. `planned_task`ın taşıyamadığı her şey — amaç, başlık, etkinlik, dakika, gerekçe kodları — katı ve sürümlü `planner_trace/1` izinde; kolon uydurulmadı, bozuk iz tahmin edilmez reddedilir.
- **Mutation 55/55.** Yalnız yorumu değiştiren kontrol mutantı hayatta kaldı, yani harness hayatta kalanı raporlayabiliyor.
- **Living memory:** `OPEN_LOOPS`ta 12C'ye bağlanmış iki madde (denemeden sonra recompute eden yol, gün özetine giden değişiklik yolu) 12C'nin kapsamında değildi; uydurulmadan 12D'ye bağlandı.
- **Daraltılan yaşayan kapı:** 11D'nin `E11D-13_content_methods` kontrolü ContentPort'un tam metod listesini sabitliyordu ve `taskCandidates` incelmesiyle düştü. 11D yalnız kendi iki metodunun sahibi; kontrol E11C-10 emsaliyle 11D'nin gerçekten karar verdiği şeye daraltıldı, garanti zayıflamadı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS. Kanıt 11B, 12A ve 12B'deki gibi bölündü: kararlar T1'de, depolama garantileri T2'de.
- **Çalıştırılmayan: T6.** Ayrıca planner uygulamada çağrılmıyor: kapasite ayarı (16D), authored görev (15) ve Today'in izden gerekçe göstermesi (12E) yok.
- Independent 12C QA: **226/226 PASS**; validator mutation 46/46 ve yorum-içi negatif kontrol false positive vermedi. Sweep 39/39.
- Sonraki numbered step `12D — Replan`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/PLANNER_ENGINE_IMPL_SPEC.md`.

## D-095 — Replan = RPLX-v0
**Durum:** Kabul edildi — 2026-09-30

- 12D final modeli `RPLX-v0 — Replan` oldu.
- Canonical spec `docs/REPLAN_SPEC.md`; machine-readable contract `arch/12d_replan/replan.yaml`; research/decision synthesis `research/12d_replan_research.md`; kod `core-engines` (`ReplanEngine.kt`), `core-model` (`PlannerFacts.kt`, `PlanTraceCodec.kt`), `core-application` (`BuildDailyPlan.kt`), `core-ports` + `data-persistence` (`latestPlan`, `resumeCheckpointRows`).
- Ana invariant: **bir plan düzenlenmez, gerekçesi olan yeni bir sürümle değiştirilir.** Başlanan iş korunur, yalnız başlanmamış kalan yeniden çözülür, gün kendiliğinden büyümez; geri dönüş hiçbir şeyi tekrar oynatmaz ve yokluk borç, başarısızlık ya da çürüme değildir.
- **Kod yazmadan önce bulunanlar:** (1) `DDM-v0` `attempt`e `planned_task` bağı vermiyor; depo hangi planlı görevin başladığını söyleyemez. (2) 12C'nin `build()`u aynı gün ikinci kez çağrılınca gerekçesiz yeni bir initial sürüm yazıyordu (`PDT-v0` §15'e aykırı). (3) `planner_trace/1`de korunan görev, replan kaydı ve re-entry bağlamı için yer yoktu. (4) 12D'ye devredilen dört madde bugün kurulamıyordu. (5) `USER_FOCUS_CHANGED`in etki edeceği bir odak tercihi yok.
- **Üretim türü (`PDT-v0` §4):** plan yoksa initial; en yeni plan başka bir çalışma gününe aitse re-entry (olay gelse bile); bugünün planı varsa ve olay geldiyse replan; aynı gün olay yoksa mevcut plan döner ve hiçbir şey yazılmaz.
- **Olaylar:** D-033 §16, `PBR-v0` §17, `PRG-v0` §19; her biri `PDT-v0` §8.10 kodunu taşır; `task_completed`in kodu yok ve uydurulmaz; `user_focus_changed` kabul edilmez (16D).
- **Kalan bütçe (D-033 §8):** bugün için yeni kapasite günü değiştirir; bildirilen kalan süre kalanın kendisidir; diğer her olay günü korur; korunan dakikalar üstten düşer; kalan asla negatif değildir ve günle aynı kurala uyar; replan günü kendiliğinden büyütmez.
- **Korunan iş (`TRUX-v0` §10.1):** çağıran bildirir, her pozisyon önceki plana karşı doğrulanır, bilinmeyen pozisyon reddedilir ve reddedilen replan hiçbir şey yazmaz; korunan görevler önde, değişmeden, işaretli; ihtiyaçları iki kez karşılanmaz; okunamayan önceki plan tahminle değiştirilmez.
- **Re-entry (`SRR-v0`):** dünkü plan oynatılmaz, hiçbir şeyi korunmaz, normal gün bütçesi geçerli, starvation beslenmez; iz §17 bağlamını ve `PDT-v0` §8.8 kodlarını taşır — skor, ceza ya da borç yok.
- **Duraklatılmış iş (`SRR-v0` §5):** en son güvenli duraklatma açık devam ihtiyacını P2 yapar ama seçmez; high-stakes duraklatma bağımsız iş olarak devam ettirilmez; kapanmış ihtiyacın duraklatması hiçbir şey değiştirmez.
- **İz:** `planner_trace/2`; `/1` katı biçimde okunmaya devam eder ve sahip olmadığı alanları iddia edemez.
- **Yeniden bağlananlar:** `skill_state` birleştirmesi → 13; deneme sonrası recompute ve deneme→planlı görev bağı → 15; planner'ı uygulamadan çağırmak → 16D; bağımlıların ters invalidation'ı → 18E.
- **Daraltılan yaşayan kapılar:** 12C'nin `E12C-02` kapasite ve `E12C-07` iz sürümü kontrolleri, kuralın `capacityOf`a taşınması ve izin `/2`ye yükselmesiyle sahip oldukları garantiye daraltıldı; garanti zayıflamadı.
- **Mutation 33/33.** Yalnız yorumu değiştiren kontrol mutantı hayatta kaldı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS.
- **Çalıştırılmayan: T6.** Planner uygulamada çağrılmıyor ve hiçbir görev başlatılamadığı için hiçbir olay üretilmiyor.
- Independent 12D QA: **157/157 PASS**; validator mutation 38/38 ve yorum-içi negatif kontrol false positive vermedi. Sweep 40/40.
- Sonraki numbered step `12E — Explanation / reason codes`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/REPLAN_SPEC.md`.

## D-096 — Explanation / reason codes = RSNX-v0
**Durum:** Kabul edildi — 2026-09-30

- 12E final modeli `RSNX-v0 — Reason Codes & Planner Explanation` oldu.
- Canonical spec `docs/PLANNER_EXPLANATION_IMPL_SPEC.md`; machine-readable contract `arch/12e_reason_codes/reason_codes.yaml`; research/decision synthesis `research/12e_reason_codes_research.md`; kod `core-model` (`ReasonCodes.kt`, `ExplanationFacts.kt`, `PlanTraceCodec.kt`, `PlannerFacts.kt`), `core-engines` (`PlannerEngine.kt`), `core-application` (`PlanReading.kt`, `TodayFactsQuery.kt`, `PlannerExplanationQuery.kt`), `core-presentation` (`PlannerExplanation.kt`, `ExplanationCopy.kt`, `TodayPresentation.kt`), `app-ui` (`PlannerExplanationScreen.kt`), `data-persistence` (`latestPlan`).
- Ana invariant: **açıklama karar izinin projeksiyonudur.** Her cümle izin kaydettiği bir reason code'a ya da izin bir alanındaki olguya dayanır; planner'ın kaydetmediği gerekçe kurulamaz, süreye sığmayan iş daha az önemli diye anlatılmaz, bekleyen iş gerçek Skill blocker'ını adlandırır, `review_due` unutmak değildir, yokluk borç değildir.
- **Kod yazmadan önce bulunanlar:** (1) iz bir blocker'ı adlandıramıyordu (`PDT-v0` §7 `related_refs`, §11, 3H invariant 6). (2) Today planı okuyamıyordu ve `latestPlan()` satır id'si vermiyordu. (3) Planner `PDT-v0` §8'de olmayan `independent_branch_available`ı yazıyor — `PRG-v0` §20'nin girdisi. (4) 11A'nın devam etiketi yeni beceri için 'başlanmış' diyecekti. (5) Replan sonrası korunan iş yeniden başlatılacak iş gibi sunulacaktı.
- **Katalog:** `PDT-v0` §8'in on ailesi + `PRG-v0` §20'nin dokuz girdisi, sırasıyla ve kapalı; dışındaki kod ifadeye dönüşemez.
- **İz:** `planner_trace/3`; her aday izi kapının Skill'ler hakkındaki cevabını planlama anında kaydeder (bloklu: hard blocker'lar ya da güvenini beklediği prerequisite'ler; koşullu: belirsizler; destekli: soft boşluklar; uygun: bloklamayan review-due'lar; cevapsız: hiçbiri). `/2` ve `/1` katı okunur ve bunu iddia edemez. Kapı açıklamak için yeniden koşulmaz.
- **Planı okumak:** plan günü satırın kendisidir; iz yalnız kendi satırlarını anlatıyorsa güvenilir (gün, konumlar, Skill'ler, tekrar yok, seçilen görevin ihtiyaç kararı); değilse okunamaz, Today `error_recoverable` gösterir ve tahmin yapılmaz. `latestPlan()` planın `planned_task` satırlarını konum sırasıyla döndürür — yeni port değil.
- **Today:** her satır kendi `planned_task` id'sine; dakikalar bugünkü planlanan kısım; kapasite planner'ın kaydı; 'hiçbir şey sığmadı' planner'ın kaydedilmiş bulgusu; korunan iş başlatılabilir değil; replan ve bekleyen ihtiyaç açıklamaya bağlanan attention.
- **Satır aileleri (`THUX-v0` §7.1, sözlük değişmedi):** birincil ihtiyacın tetikleyicisinden; devam ettirilen güvenli duraklatma daha özgül olgudur; doğrulanmamış zayıflık doğrulanır, onarılmaz; İngilizce yalnız paralel hattın kendi ritminde sebeptir; en çok bir destekleyici (sığdırma ya da hat). Devam etiketi 'Öğrenme yolunda ilerliyor'.
- **Açıklama:** `Statement` özel kurucu; yalnız kaydedilmiş ve katalogda olan kod ya da altı iz olgusundan kurulur. Neden bugün: önce ihtiyaç, sonra belirleyici öncelik nedeni, uygunluk ve sığdırma; bant kodu asla neden değil; korunan iş korunan olarak anlatılır. Neden bugün değil: süre ertelemesi daha az önemli değil (yalnız kaydedilmiş düşük-öncelik kodu bunu söyleyebilir); bekleyen ihtiyaç Skill'lerini adlandırır; görevi olmayan ihtiyaca kod uydurulmaz. Kodsuz replan yalnız planın değiştiğini söyler. Yeniden değerlendirme asla tarih değildir.
- **Metin:** `ExplanationCopy` `PDT-v0` §3'ün şablon yedeği; her katalog kodunun cümlesi var; `DayCopy` gibi çekirdekte; unutma yalnız inkâr edilirken geçer, yokluk başarısızlık ya da borç değildir, skor/yüzde/seri yok. Sözcükler 14'ün.
- **Daraltılan yaşayan kapılar:** `E11A-07_blocked_filtered`, `E11A-11_no_invented_plan`, `E11A-11_no_invented_capacity`, `E12C-07_unknown_format_refused`, `E12D-08_format_v2`, `E12D-08_only_known_versions`; garanti zayıflamadı.
- **Mutation 52/52.** Koşucu ilk turda `gradlew.bat`ı göreli adla çağırdı ve hiç Gradle çalıştırmadı; reddetme kuralı hepsini 'no verdict' saydı, sayılmadı. Yalnız yorumu değiştiren kontrol mutantı hayatta kaldı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS.
- **Çalıştırılmayan: T6.** Planner uygulamada çağrılmıyor; cihazda açıklanacak bir plan yok.
- Independent 12E QA: **219/219 PASS**; validator mutation 40/40 ve yorum-içi negatif kontrol false positive vermedi. Sweep 41/41.
- Sonraki numbered step `12F — Sanal kullanıcı testleri`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/PLANNER_EXPLANATION_IMPL_SPEC.md`.

## D-097 — Sanal kullanıcı testleri = VUSX-v0
**Durum:** Kabul edildi — 2026-09-30

- 12F final modeli `VUSX-v0 — Virtual User Scenarios` oldu ve **AŞAMA 12 kapandı**.
- Canonical spec `docs/VIRTUAL_USER_TESTS_SPEC.md`; machine-readable contract `arch/12f_virtual_user_tests/virtual_users.yaml`; research/decision synthesis `research/12f_virtual_user_tests_research.md`; kod `core-engines` test fixtures (`virtual/VirtualUsers.kt`) ve üç suite (`VirtualUserScenariosTest`, `VirtualUserJourneysTest`, `VirtualUserExplanationsTest`), `core-presentation` (`PlannerExplanation.kt` gruplama, `ExplanationCopy.shortNames`), `app-ui` (`PlannerExplanationScreen.kt`).
- Ana invariant: **sanal kullanıcı durumdur, cevap değil.** İhtiyaç durumdan (`needsFromSkillStates`), karar gerçek kapıdan (`PrerequisiteEngine.decide`), plan gerçek planner'dan, açıklanan iz planner'ın (ve re-entry için `ReplanEngine`in) yazdığından gelir. Yalnız `PLNX-v0` §5'in sahibinden geldiğini söylediği ihtiyaçlar (paralel hat, entegrasyon, pekiştirme) doğrudan verilir.
- **Tek tanım:** sanal kullanıcılar `core-engines` Gradle test fixture'ı; `core-application` ve `core-presentation` testleri `MSBX-v0`nin zaten izin verdiği kenardan kullanır. Yeni bağımlılık kenarı yok.
- **Kapsam:** 3H'nin 16 senaryosundan 15'i gerçek kodla koşuldu; S06 koşulamıyor — `VDW-v0` tanısal atlamasının uygulaması ve plan içinde sahibi yoktu — ve `PDT-v0` invariant 12 ile birlikte 13'e bağlandı. Invariant 17 yapısal (aday sınırı, sınırlı iz, geçmişle artmayan okuma); runtime bütçesi 18E.
- **Bulgu ve düzeltme:** dönen öğrencinin due envanteri `planner_explanation`da satır satır listeleniyordu (`SRR-v0` §9.1). Aynı tetikleyici, aynı gelmeme nedeni ve aynı yeniden değerlendirmeyle gelmeyen ihtiyaçlar tek girdi; hiçbir ihtiyaç ya da Skill düşmez; farklı blocker'lı bekleyen ihtiyaçlar ayrı kalır; ekran üç Skill'i adlandırıp kalanını etiketli envanter olarak sayar. 12E spec'ine açık, tarihli not düşüldü.
- **Bulgu, kural değişmedi:** 3H S07 örnek günü açıklayıcıdır; her due'nun görevi varsa `PBR-v0` aciliyeti günü tekrarlarla doldurur ve yeni öğrenme süre için bekler (`SRR-v0` §15'e uygun). Koruma starvation/track balance, eşikler 18C.
- **Mutation 27/27**, yalnız sanal kullanıcı testleri koşarken; F01 (kimseyi bekletmeyen kritik doğrulama P1 kalmalı) ve F06 (sığan kısa ders bile küçük blokta öğretilmez) ilk turda yaşadı — test boşlukları; kapatılıp bütün set yeniden koşuldu. Yorum-içi kontrol hayatta kaldı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS.
- **Çalıştırılmayan: T6.** Planner uygulamada çağrılmıyor; sanal kullanıcılar yalnız testlerde yaşadı.
- Independent 12F QA: **148/148 PASS**; validator mutation 30/30 — W02 validator'ın bir anahtar eksikken hata vermek yerine çöktüğünü buldu, düzeltildi; çökme tespit sayılmaz. Sweep 42/42.
- Sonraki numbered step `13A — Haftalık sınav`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/VIRTUAL_USER_TESTS_SPEC.md`.

## D-098 — Haftalık sınav = WBAX-v0
**Durum:** Kabul edildi — 2026-10-01

- 13A final modeli `WBAX-v0 — Weekly Blueprint Assessment Implementation` oldu.
- Canonical spec `docs/WEEKLY_ASSESSMENT_IMPL_SPEC.md`; machine-readable contract `arch/13a_weekly_assessment/weekly_assessment.yaml`; research/decision synthesis `research/13a_weekly_assessment_research.md`; kod `core-model` (`WeeklyAssessmentFacts.kt`, `WeeklyBlueprintCodec.kt`), `core-engines` (`WeeklyBlueprintEngine.kt`), `core-application` (`WeeklyAssessment.kt`, `BuildDailyPlan.kt`, `SubmitAttempt.kt`), `core-presentation` (`WeeklyAssessmentSession.kt`), `core-ports`, `data-persistence` (şema v3), `data-curriculum`.
- Ana invariant: **bir hafta bir kimliktir, kota ya da son tarih değil.** Ölçmeye değer olan durumdan gelir, bir Skill tek kez ölçülür, güvenilir ve taze bir item'ı olmayan slot kapsama değildir, hafta kendi dakikasını ve kuyruğunu eklemez ve sınavı yapılmadan geçen bir hafta geride hiçbir şey bırakmaz.
- **Kod yazmadan önce bulunanlar:** (1) hiçbir ürün kodu `assessment_session` yazmamıştı ve tabloda blueprint için yer yoktu; 10D onu açıkça 13'e bırakmıştı. (2) Item modelinde `QAB-v0`ın `expected_active_minutes`ı ve rol uygunluğu yoktu. (3) Deneme oturumunu adlandıramıyordu; exposure okunamıyordu; "son döngüden beri"nin okuyucusu yoktu. (4) `WBA-v0` haftalık ritmi sabitliyor ama döngü sınırını tanımlamıyor. (5) `VDW-v0` ve 3H S06 12F'de "13"e bağlanmıştı ama 13A–13E'nin hiçbiri kapsamıyordu (D-099).
- **Döngü:** kaydedilmiş çalışma gününün ISO-8601 haftası (`2026-W40`), instant değil; Pazartesi başlangıçlı ürün varsayılanı, bilimsel değer değil, **kullanıcı onayladı**; başlangıç günü ayarı 16D. Hafta bir kez kurulur; aynı hafta yeniden sorulunca hiçbir şey yazılmaz; yeni haftada güncel durumdan taze kurulur ve `assessment.weekly.no_exam_debt` taşır; iki kaçırılan hafta yapısal olarak yığılamaz. Ölçmeye değer bir şey yoksa hiçbir şey yazılmaz.
- **Havuz (`WBA-v0` §7–§9):** composer kendi ihtiyacını açmaz; planner'ın eksenlerden açtığı ihtiyaçları ve yalnız sahiplerinin verdiklerini (paralel hat, entegrasyon) okur. `verification_due`/`weakness_detected` → doğrulama; bağımlı işi gerçekten bekleten kritik Skill'in devamı → kritik ön koşul güveni (planner izinden, kapı yeniden koşulmaz); son blueprint'ten beri kanıt alan devam → son ilerleme; `retention_review_due` → retention (amaç `retain` kalır); İngilizce paralel hat → paralel İngilizce. Yeni öğrenme, tanı/pekiştirme fırsatları ve açık remediation'daki Skill ölçülmez. Bir Skill tek kez, §9 sırasında önce gelen rolle; bant ve rank planner'ınki. Roller kota değildir.
- **Item seçimi (`QAB-v0` §31–§33):** indekslenebilir yüzler (hedef Skill, haftalık kapsam, seçilebilir lifecycle, beyan edilen rol) önce; okuma slot başına 5 ile sınırlı (mühendislik sınırı, 18E); trusted önce, deterministik önce, kararlı id — asla en kısa önce. Her ret bir kural adlandırır: niyete uygun değil (güven mağazanın validation kaydıdır), ön koşul bekliyor (kapı kapalı kalır), çözüm gösterilmiş (aile dahil), zaten görülmüş, varyant ailesi/dependency group kullanımda, beyan edilmemiş süre. Item'ı olmayan slot kapsama değildir ve öğrencinin hatası değildir. Her slot `h0_required`; yalnız P0/P1 slotlar oturum kapanışı için gerekli.
- **Planner:** her hazır slot, planner'ın zaten açtığı ihtiyaç için bir aday olur (amaç rolden, süre item'dan, güven mağazadan, `atomicEvidenceBoundary`); `BuildDailyPlan` bu haftanın sunulmamış slotlarını ekler. Haftanın kendi kuyruğu, bandı ve dakikası yoktur; gün uzatılmaz; geçen haftanın slotları sunulmaz.
- **Oturum ve sonuç:** tek interior (`ASUX-v0`); araç beyanı item'ların hepsinin izin verdiği kesişimdir; deneme oturumunu adlandırır. Sonuç `WBA-v0` §27: gönderilene göre `complete/partial/deferred` (`invalidated` beyanlı, üreticisi yok), doğrulanmış pozitif/negatif/kısmi yalnız doğrulanmış bağımsız kanıttan, geçersiz/contaminated/provisional/yardımlı birinci sınıf; puan alanı yok; değişiklik yalnız motorların bildirdiği. Kök neden (`WBA-v0` §25): bu oturumda temiz biçimde eksik görünen Skill'e dayanan sonraki slot `contaminated` yazılır; ileriye dönük, geriye dönük düzeltme 13D.
- **Depolama:** şema v3 `assessment_session.blueprint TEXT CHECK (scope <> 'weekly' OR blueprint IS NOT NULL)` + iki okuma indeksi; katı `weekly_blueprint/1`; dolu şema-2 veritabanına karşı her truth satırı korunarak test edildi. Yeniden kompozisyon yeni satır ekler, düzenlemez. Dört port incelmesi (`latestAssessmentSession`, `exposuresFor`, `skillsEvidencedSince`, `assessmentItemsFor`); port sayısı dört. Authored `[item]` iki isteğe bağlı anahtar kazandı.
- **Daraltılan yaşayan kapılar:** `E12B-12`, `E12C-08`, `E12D-09`, `E12E-04`, `E12F-07` `schema_version_unchanged` artık 2'yi aşan her şema sürümünün onu ekleyen kabul edilmiş kontratta (`schema_migration`) sahiplenilmesini istiyor; `E12C-08_watermark_first` paylaşılan `PlanningStates.read(`'i tanıyor. Garanti zayıflamadı.
- **Mutation 42/42**, yalnız altı haftalık suite koşarken; ilk turda W26 ve W40 eşdeğer, W27 derlenmez çıktı ve davranışı değiştiren mutantlarla değiştirildi; bütün set tek değişmemiş ağaçtan yeniden koşuldu. Kontrol mutantı hayatta kaldı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS.
- **Çalıştırılmayan: T6.** Uygulamada hiçbir şey hafta kurmuyor ya da planner'ı çağırmıyor (16D) ve hiçbir authored item süre ya da rol beyan etmiyor (15).
- Independent 13A QA: **210/210 PASS**; validator mutation 25/25 (V11 ilk turda kaçtı — okuyucu karşılaştırıcının yalnız ilk lambdasını görüyordu; düzeltildi ve set yeniden koşuldu) ve yorum-içi negatif kontrol false positive vermedi. Sweep 43/43.
- Sonraki numbered step `13B — Aylık sınav`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/WEEKLY_ASSESSMENT_IMPL_SPEC.md`.

## D-099 — 13F eklendi: Tanısal atlama (VDW-v0)
**Durum:** Kabul edildi — 2026-10-01 (kullanıcı kararı; numaralı adım değildir)

- 12F `VDW-v0` tanısal atlamasını ve 3H S06'yı "13"e bağlamıştı, fakat 13A–13E'nin hiçbiri onu kapsamıyordu; sahibi olmayan bir sözleşme sahipsiz invariant demekti (`TVSX-v0`).
- Kullanıcı 13A sırasında **AŞAMA 13'ün sonuna yeni bir adım eklenmesine** karar verdi: **`13F — Tanısal atlama (VDW-v0)`**. D-017 gereği henüz başlanmamış bir aşamaya adım eklemektir; **mevcut hiçbir adım yeniden numaralanmadı** ve tamamlanmış hiçbir adım değişmedi.
- 13F `VDW-v0 / D-037`'yi koda dökecek ve 3H S06 ile `PDT-v0` invariant 12'yi gerçek kodla koşacak. Kapsamı 13F'nin kendi PRE-STEP'inde belirlenir.
- Sıra: 13A ✅ → 13B → 13C → 13D → 13E → 13F.

## D-100 — Aylık sınav = MCAX-v0
**Durum:** Kabul edildi — 2026-10-01

- 13B final modeli `MCAX-v0 — Monthly Capability Assessment Implementation` oldu.
- Canonical spec `docs/MONTHLY_ASSESSMENT_IMPL_SPEC.md`; machine-readable contract `arch/13b_monthly_assessment/monthly_assessment.yaml`; research/decision synthesis `research/13b_monthly_assessment_research.md`; kod `core-model` (`AssessmentBlueprint.kt`, `MonthlyAssessmentFacts.kt`, `BlueprintCodec.kt`, `WeeklyAssessmentFacts.kt`, `AssessmentFacts.kt`), `core-engines` (`BlueprintComposer.kt`, `MonthlyBlueprintEngine.kt`, `WeeklyBlueprintEngine.kt`), `core-application` (`BlueprintAssessment.kt`, `BuildDailyPlan.kt`), `core-presentation` (`BlueprintAssessmentSession.kt`), `data-persistence` (şema v4), `data-curriculum`.
- Ana invariant: **bir ay daha geniş bir penceredir, daha ağır bir sınav değil.** Ölçmeye değer olan yine durumdan gelir, bir Skill tek kez ölçülür, kritik bir Skill yalnız bir nedenle yeniden doğrulanır, aylık etiket kanıta ağırlık eklemez, ay kendi dakikasını ve kuyruğunu eklemez ve sınavı yapılmadan geçen bir ay geride hiçbir şey bırakmaz.
- **Kod yazmadan önce bulunanlar:** (1) `MCA-v0` §4 aylığın ortak kontratın politika uzantısı olduğunu ve ikinci bir mimari olmadığını söylüyor, fakat 13A kontratı haftalık adlarla ve haftalık rollere sabitlenmiş biçimde yazmıştı. (2) v3 CHECK'i yalnız haftalık satırları kapsıyordu ve SQLite bir CHECK'i değiştiremez. (3) Bir item yalnız haftalık rol beyan edebiliyordu. (4) `cross_topic_transfer` ve `professional_evidence_checkpoint` rollerinin durum üreticisi yok. (5) `MCA-v0` ay sınırı tanımlamıyor. (6) `MCA-v0` §27'nin durum tarafı listeleri motorların bildirdiğidir, sonuç hesaplayamaz.
- **Tek kontrat, iki kapsam:** `AssessmentBlueprint` `scope` taşır; slot rolü `SlotRole`'dür (`BlueprintRole` haftalık, `MonthlyRole` aylık); karışık blueprint temsil edilemez; yalnız aylık blueprint önceki oturumu adlandırabilir. Ortak kompozisyon tek, kapsam-parametreli `BlueprintComposer`. **Hiçbir haftalık değer değişmedi** — roller, kodlar, ret ve dışlama id'leri, aday id'leri ve `weekly_blueprint/1` metni; bütün 13A testleri yalnız tip adları güncellenerek geçiyor.
- **Döngü:** kaydedilmiş çalışma gününün takvim ayı (`2026-10`), instant değil; kullanıcının onayladığı haftalık kuralı izleyen ürün varsayılanı, bilimsel değer değil; ayar 16D. Ay bir kez kurulur; yeni ay önceki aylık oturumu `prior_session` olarak adlandırır (borç değil), pencereyi o oturumun gününden ölçer ve `assessment.monthly.no_exam_debt` taşır. Hafta ve ay bağımsız döngülerdir.
- **Havuz (`MCA-v0` §5–§7, §11–§12):** kritik ön koşulda `verification_due`/`weakness_detected`, bağımlı işi bekleten devam ya da vadesi gelmiş tekrar → kritik yetenek yeniden doğrulaması; başka yerde doğrulama/zayıflık → kalıcı endişe (motorun durumundan, tek item'dan değil); pencere içinde kanıt alan required devam → boylamsal; diğer tekrar → gecikmiş retention (`retain` kalır); entegrasyon fırsatı → entegre uygulama; İngilizce hat → paralel teknik İngilizce. Kritik Skill kritik olduğu için ölçülmez; supporting/optional devam `not_required_capability`; açık remediation, yeni öğrenme, tanı/pekiştirme ve İngilizce dışı hat ölçülmez. Bir Skill tek kez, §7 sırasında; bant planner'ınki; `%50 + %30 + %20` yazılamaz.
- **Üreticisi olmayan iki rol:** transfer ve profesyonel kontrol noktası beyanlı, kodlu ve depolanabilir; hiçbir ihtiyaç onları açmıyor ve üretici uydurulmadı (15). Kontrol noktası hazır olma kapısı değildir.
- **Item, planner, oturum:** ortak composer — aylık kapsama uygun, aylık role beyanlı, mağazanın güvendiği, kapıdan geçen, taze item; yalnız haftalık item aylık slota giremez. Slotlar planner'ın açtığı ihtiyaçların adayı (`monthly:<ay>:<slot>`); hafta ve ay aynı ihtiyacı tutarsa iki aday alternatiftir ve planner ihtiyaç başına en çok bir görev seçer; ayın kuyruğu, bandı, dakikası yok, gün uzatılmaz. Tek interior, bloklar §7 sırasında.
- **Sonuç:** ortak sonuç + `MCA-v0` §27'nin dört boylamsal listesi, her biri yalnız kendi rolünün temiz, bağımsız, doğrulanmış kanıtıyla; temiz negatif ya da yardımlı/provisional iş yeniden doğrulama değildir (ilk temiz çelişki `verification_due`, §24). Kanıt satırı "aylık" bilmez; aylık başarı yüzdesi yok.
- **Depolama:** şema v4 `assessment_session_blueprint_format` trigger'ı — haftalık ya da aylık satır kendi biçiminde blueprint taşımalı; dolu şema-3 veritabanına karşı her truth satırı korunarak test edildi. `monthly_blueprint/1` = haftalık biçim + `prior_session`; head alanları tam eşleşmeli. Port değişmedi (dört). `blueprintRoles` artık `Set<SlotRole>`; uygun olmadığı kapsamın rolü paketi reddeder.
- **Daraltılan yaşayan kapılar:** 13A validator'ı taşınan kodu yeni yerinde okuyor; genel biçimler (`scope`, `codes.*`, `BlueprintScopes.cycleOf`) haftalık değerlere bağlandığını ayrıca doğrulayarak; beş haftalık dışlama hâlâ ilk ve değişmemiş; v3 13A'nın ve sonraki her sürüm sahibinin kontratında. Garanti zayıflamadı.
- **Mutation 48/48**, yalnız aylık suite'ler koşarken; ilk turda M10 (aylık bloklar listeleme sırasında) kaçtı — blok testi iki sıranın ayrıştığı bir rol çifti kullanmıyordu; test güçlendirildi ve bütün set tek değişmemiş ağaçtan yeniden koşuldu. Kontrol mutantı hayatta kaldı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS; 622 JVM testi.
- **Çalıştırılmayan: T6.** Uygulamada hiçbir şey ay kurmuyor ya da planner'ı çağırmıyor (16D) ve hiçbir authored item aylık rol beyan etmiyor (15).
- Independent 13B QA: **259/259 PASS**; validator mutation 28/28 ve yorum-içi negatif kontrol false positive vermedi. Sweep 44/44.
- Sonraki numbered step `13C — Spaced repetition`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/MONTHLY_ASSESSMENT_IMPL_SPEC.md`.

## D-101 — Spaced repetition = RVRX-v0
**Durum:** Kabul edildi — 2026-10-01

- 13C final modeli `RVRX-v0 — Retention Verification & Risk Implementation` oldu.
- Canonical spec `docs/RETENTION_IMPL_SPEC.md`; machine-readable contract `arch/13c_spaced_repetition/retention.yaml`; research/decision synthesis `research/13c_spaced_repetition_research.md`; kod `core-model` (`RetentionFacts.kt`, `EvidenceFacts.kt`), `core-engines` (`RetentionEngine.kt`), `core-application` (`RebuildRetention.kt`, `BuildDailyPlan.kt`), `core-ports` (`retentionDueBy`), `data-persistence` (şema v5).
- Ana invariant: **zamanın geçmesi negatif kanıt değildir.** Gün yalnız planlanmış bir kontrolün geldiğini söyler: mastery azalmaz, vadesi gelen tekrar unutma değildir, hiçbir şey kilitlenmez ve bir Skill'i doğrulamaya, riske ya da kararlılığa yalnız temiz, bağımsız bir kontrol taşıyabilir.
- **Kod yazmadan önce bulunanlar:** (1) retention eksenini hiçbir şey yazmıyordu; planner, kapı ve 13A/13B rolleri hep `not_yet_evaluated` okuyordu. (2) `retention_state` yalnız `state` kolonuna sahipti. (3) `EvidenceRow` deponun tuttuğu çalışma gününü düşürüyordu. (4) `RVR-v0` §19'un indeksli vade sorgusu yoktu. (5) Mastery yol bağımlıdır; yeniden kurulabilir bir takvim her satırdan sonraki mastery kararını ister. (6) `skill_state`'in tek yazıcısı vardı ve 13A birleştirmeyi 13C'ye bırakmıştı.
- **Durum makinesi (`RVR-v0` §2–§10):** mastery'yi sağlayan satır → `fresh` (başlangıç aralığı); ilk temiz hata → `verification_due` (silmez; kritikse bağımlı yeni iş bekler); vadesinde ya da sonrasında güçlü kontrol → `stable`, aralık büyür; vadeden önce güçlü kullanım → doğal yeniden kullanım, saat değişmez; vadeli kontrole belirsiz sonuç → `at_risk`; en az bir çalışma günü sonra taze yeniden kontrol → `stable`, **aralık büyümez**; mastery'nin kapıları geçmez olursa → `untracked`. `review_due` replay tarafından hiç saklanmaz, günden türetilir; zaman başka hiçbir durumu değiştirmez ve `at_risk` asla zamandan gelmez. Bilinmeyen profil için aralık uydurulmaz.
- **Temiz/güçlü/belirsiz:** temiz = itirazsız, prerequisite-geçerli, çözüm gösterilmemiş, Objective için doğrudan, doğrulanmış ve bağımsız (H0). Güçlü = karmaşık ya da kritik Skill için yakın tekrar olmayan temiz pozitif; yeniden kontrol her profilde taze olmalı. Belirsiz = doğrulanmış kısmi ya da provisional değerlendirici.
- **V0 sayıları `RVR-v0` §20'ninkidir** ve hiçbiri burada seçilmedi: başlangıç 2/4/7 gün, kritik tavan 3, büyüme ×2.0/×1.6, en çok 180/90, ayrım 1 gün; hepsi mühendislik heuristiği, kalibrasyon 18C. Büyüme aşağı yuvarlanır (daha erken kontrol, asla daha geç) ve ayrım bir kilit değil kredi kuralıdır — beyan edildi.
- **Yeniden kurma ve yenileme:** `RebuildRetention` watermark'ı önce okur, Skill'in kanıtını kayıt sırasıyla oynatır ve mastery motorunu her satırdan sonra `RebuildMastery` gibi yeniden sorar; truth yazmaz. `RefreshDueRetention` zamanın yaptığı tek şeydir: indeksli sorguyla `fresh`/`stable` takvimleri `review_due` yapar, watermark'ı korur, okunamayan satırı adlandırır. `BuildDailyPlan` durumları okumadan önce yeniler; planner kuralı değişmedi.
- **Depolama:** şema v5 `retention_state`'e `RVR-v0` §18'in alanlarını, `retention_due` vade indeksini ve değer kümesi trigger'larını (insert + upsert güncellemesi) ekler; dolu şema-4 veritabanına karşı her truth satırı korunarak test edildi. `skill_state` retention eksenini alır, diğerlerini taşır ve iki watermark'ın eskisini yazar. Port incelmesi `retentionDueBy`; `EvidenceRow.studyDay`; port sayısı dört.
- **Daraltılan yaşayan kapılar:** `E13B-10_schema_version_4` artık 4 ve sonrasını, her sonraki sürüm sahibinin kontratındaysa kabul ediyor; 13B'nin depo testi en az 4 istiyor. 12x ve 13A kapıları değişmedi (v5'in sahibi 13C kontratı).
- **Mutation 47/47**, yalnız retention suite'leri koşarken; ilk turda üç mutant kaçtı ve her biri iddia ettiğinden azını kanıtlayan bir testi gösterdi: R25 ve R26 (yardımlı ve contaminated kanıt temiz sayılıyor) — motor testindeki zayıf olaylar mastery satırından önce oluşturulduğu için ondan önce oynatılıyor ve test boşa geçiyordu; R45 (güncelleme trigger'ı kaldırıldı) — SQLite upsert'te önce insert trigger'ını çalıştırdığı için güncelleme trigger'ını yalnız doğrudan `UPDATE` sınar. Testler düzeltildi ve bütün set tek değişmemiş ağaçtan yeniden koşuldu. Kontrol mutantı hayatta kaldı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS; 654 JVM testi.
- **Çalıştırılmayan: T6.** Uygulamada hiçbir şey kanıttan sonra motorları yeniden kurmuyor ya da planner'ı çağırmıyor (16D).
- Independent 13C QA: **164/164 PASS**; validator mutation 27/27 ve yorum-içi negatif kontrol false positive vermedi. Sweep 45/45.
- Sonraki numbered step `13D — Remediation Engine`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/RETENTION_IMPL_SPEC.md`.

## D-102 — Remediation Engine = WLRX-v0
**Durum:** Kabul edildi — 2026-10-01

- 13D final modeli `WLRX-v0 — Weakness Localization & Remediation Implementation` oldu.
- Canonical spec `docs/WEAKNESS_REMEDIATION_IMPL_SPEC.md`; machine-readable contract `arch/13d_remediation_engine/remediation.yaml`; research/decision synthesis `research/13d_remediation_engine_research.md`; kod `core-model` (`WeaknessFacts.kt`), `core-engines` (`WeaknessEngine.kt`), `core-application` (`RebuildWeakness.kt`, `MasteryTimeline.kt`, `RebuildRetention.kt`, `BuildDailyPlan.kt`, `BlueprintAssessment.kt`), `data-persistence` (şema v6, dispozisyon okuma).
- Ana invariant: **başarısız bir deneme başarısız bir Skill değildir.** Atfedilemeyen deneme öğrencinin değildir, yardım alınmış iş en çok bir hipotezdir, mastery sonrası çelişki hiçbir şey silinmeden doğrulama açar, remediation yalnız mastery motorunun kapıları düştüğünde doğrulanır ve yalnız taze bağımsız kanıt onları yeniden geçirdiğinde kapanır.
- **Kod yazmadan önce bulunanlar:** (1) zayıflık eksenini hiçbir şey yazmıyordu; kapı ve planner okunmayan bir eksenden `remediation_required` okuyordu. (2) `weakness_state` tek kolonluydu. (3) `weakness_detected` 12C'de sahibinin sağladığı ihtiyaç olarak listeleniyordu ama sağlayan yoktu. (4) Dispozisyonlar yazılıyor ama hiçbir motor okumuyordu; 13A/13B geriye dönük contamination'ı buraya bırakmıştı. (5) Retention ve zayıflık aynı satır başı mastery zaman çizgisine ihtiyaç duyuyordu. (6) Topic durumu ve 11C'nin yüksek riskli boşluk politikası hiçbir 13 alt adımı olmadan "13"e bağlıydı.
- **Kullanıcı kararları:** Topic durum makinesi (`TSM-v0`'ın altı durumu, `RVR-v0` §15 `weakening` dahil) → **16C**; yüksek riskli duraklatma boşluk politikası → **18D** (o zamana kadar yüksek riskli duraklatma devam ettirilmez).
- **Atıf (`failure_attribution_rules.yaml`):** on iki kural, öncelik sırasıyla ilk eşleşen karar verir; dördü saklanan bir satırla asla eşleşemez (yapılmamış deneme, ortam hatası, entegre proje sonucu, `review_due`) ve küme kabul edilmiş haliyle kalsın diye tutuldu. Temiz ama doğrudan olmayan hata korroborasyondur (hipotez — beyan edildi). Bir hata sinyali asla düşürmez.
- **Doğrulama ve kapanış:** mastery motorunun kapıları temiz bir hatadan sonra düşerse remediation doğrulanır (hangi item gösterirse göstersin); mastery tutarken taze yeniden kontrol hatası doğrulamayı açık tutar. Taze (aynı item ya da aile değil) ve temiz başarı hipotezi ve desteklenmiş sinyali kapatır; doğrulanmış remediation'ı yalnız kapılar yeniden geçtiğinde kapatır. Yardım, aynı item, tek başarı ve biten görev kapatmaz.
- **Eksen ve ihtiyaçlar:** Skill ekseni Objective'lerin en güçlü açık sinyali (doğrulanmış → `remediation_required`); yayılım yok. Motor `hypothesis`/`supported` için `weakness_detected` sağlar; `remediation_required` ve mastery sonrası doğrulama için sağlamaz (planner zaten açıyor) — bir endişe tek ihtiyaç. `BuildDailyPlan` ve `ComposeAssessmentBlueprint` bunları alır; planner kuralı değişmedi.
- **Dispozisyonlar:** depo her satırı en yeni dispozisyonu uygulanmış olarak döner (`contested` → itirazlı; `invalidated`/`prerequisite_contaminated` → ön koşul bozuk; diğer `invalidated` ve `superseded` → değerlendirme geçersiz; `reinstated` → kaydedildiği gibi); satır düzenlenmez, öğrencinin sonucu yeniden yazılmaz, mastery motoru değişmedi. `RecordDisposition` doğrulayarak ekler. `ApplyRetroactiveContamination` yalnız temiz biçimde eksik görünen Skill'i gerçekten gerektiren slotların kanıtını düzeltir; 13A/13B'nin ileriye dönük sınırını kapatır.
- **Ortak mastery zaman çizgisi:** `MasteryTimeline` retention ve zayıflığın okuduğu tek replay'dir ve `RebuildMastery`'yi birebir izler.
- **Depolama:** şema v6 `weakness_state`'i tamamlar (Skill, son atıf sonucu ve kuralı, açık doğrulama, sinyal kanıtları, ilk/son görülme, çözüm kanıtı), `weakness_by_skill` indeksi ve yaşam döngüsü trigger'ları; dolu şema-5 veritabanına karşı her truth satırı korunarak test edildi. `skill_state` zayıflık eksenini alır, en eski watermark'ı taşır. Port değişmedi (dört).
- **Daraltılan yaşayan kapılar:** `E13C-08` (watermark ve replay ayna kontrolleri `MasteryTimeline`'ı okuyor, retention'ın onu okuduğu ayrıca doğrulanıyor), `E13C-10_schema_version_5` ve 13C depo testi; garanti zayıflamadı.
- **Mutation 44/44**, yalnız zayıflık suite'leri koşarken; ilk turda D02, D31 ve D37 kaçtı (eksen önceliği, varsayılan doğrudanlık, zaman çizgisinin önceki mastery'si — her biri sınanmayan bir durum); testler eklendi/güçlendirildi ve bütün set tek değişmemiş ağaçtan yeniden koşuldu. Kontrol mutantı hayatta kaldı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS; 679 JVM testi.
- **Çalıştırılmayan: T6.** Uygulamada hiçbir şey kanıttan sonra motorları yeniden kurmuyor ya da planner'ı çağırmıyor (16D).
- Independent 13D QA: **165/165 PASS**; validator mutation 28/28 ve yorum-içi negatif kontrol false positive vermedi. Sweep 46/46.
- Sonraki numbered step `13E — Program değişiklik raporu`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/WEAKNESS_REMEDIATION_IMPL_SPEC.md`.

## D-103 — Program değişiklik raporu = PCRX-v0
**Durum:** Kabul edildi — 2026-10-01

- 13E final modeli `PCRX-v0 — Program Change Report Implementation` oldu.
- Canonical spec `docs/PROGRAM_CHANGE_REPORT_IMPL_SPEC.md`; machine-readable contract `arch/13e_program_change_report/program_change.yaml`; research/decision synthesis `research/13e_program_change_report_research.md`; kod `core-model` (`ProgramChangeFacts.kt`), `core-engines` (`ProgramChangeEngine.kt`), `core-application` (`ProgramChanges.kt`: `CaptureProgramSnapshot`, `RecomputeSkillState`, `ReportProgramChanges`), `core-presentation` (`ProgramChangePresentation.kt`), `core-ports` + `data-persistence` (`objectivesOf`).
- Ana invariant: **bir rapor iki okumanın farkıdır; motorların yazdığından fazlasını iddia edemez.** Değişiklik yalnız bir eksen gerçekten hareket ettiyse söylenir, kimsenin yazmadığı durum bir 'önce' değildir, vadesi gelen tekrar gündür, plan değişikliği iki kayıtlı plan sürümü arasındaki farktır ve hiçbir şey değişmediyse sonuç bunu açıkça söyler (`ASUX-v0` §13.4).
- **Kod yazmadan önce bulunanlar:** (1) bir Skill'i kanıttan sonra tek yerde yeniden hesaplayan bir şey yoktu ve hiçbir port bir Skill'in Objective'lerini listeleyemiyordu — her rebuild'in istediği kapı profillerinin kaynağı yoktu. (2) Sonuç aileleri (11D) ve `stateChangeRefs` (13A) vardı ve tasarım gereği boştu. (3) Replan (12D) vardı; olayını bir sınavın ne yaptığından seçen yoktu. (4) İlk okumanın 'önce'si yoktur; saf bir fark, kimsenin değerlendirmediği Skill için "doğrulandı" derdi.
- **Durum değişiklikleri:** on bir tür, her biri `ASUX-v0` §13.1'in bir ailesinde ve planner'ın `ReplanTrigger`'ının zaten kaydettiği bir `replan.*` koduyla. Kimsenin yazmadığı eksen değişiklik iddia etmez (`unknownBefore`); zamanın tek başına yaptığı geçişler (`review_due`, `untracked → fresh`, mastered olmayan durumlar arası gelişme) raporlanmaz; `confirmed_current → confirmation_verification_due` `verification_opened`'dır (düşüş değil) ve bir doğrulama Skill başına bir kez adlandırılır; düzeltme sonucu düşüşler (`remediation_required → supported`, `supported → hypothesis`) yeni bulgu değildir; hipotez `verification_needed` ailesindedir (`SPWX-v0`). Readiness ayrıca farklanmaz: raporlanan eksenlerin fonksiyonudur, bağımlılara etkisi plan değişikliği olarak görünür.
- **Plan değişiklikleri:** iki farklı plan sürümü arasında açılan/kapanan ihtiyaçlar (`needKey`) ve eklenen/çıkan görevler (`candidateId`). Aynı sürüm değişiklik değildir; önceki plan yoksa `firstPlan` ve değişiklik değildir. Nedenler: durum değişikliklerinin kodları + yalnız yeni plan sürümü varsa yeni planın kendi `replan.*` kodları.
- **Yeniden hesaplama:** `RecomputeSkillState` profilleri `objectivesOf` → `ObjectiveGateProfiles` ile yayımlanmış curriculum'dan alır (`critical`/`standard`, başka değer reddedilir; transfer/artifact parametreleri `GRE-v0` varsayılanında — 15) ve `RebuildMastery → RebuildRetention → RebuildWeakness → RebuildReadiness` sırasıyla çağırır; Objective'i yayımlanmamış Skill için hiçbir şey yazılmaz; truth yazılmaz.
- **Replan:** `ReportProgramChanges` durum değişmediyse plan istemez ve hiçbir şey yazmaz; değiştiyse planner'ın kendi replan'ını değişikliğin adlandırdığı olayla çağırır (`remediation_opened` → `new_remediation_created`, sonra `verification_opened` → `new_verification_due_created`, yoksa `new_evidence_recorded`); hiçbiri kalan süreyi değiştirmez, günün bütçesi korunur. Rapor yeni plandan sonra okunur. V1 sürüm tanımının 3. maddesi ("weekly/monthly assessment gelecek planı değiştirmeli") artık çekirdekte uçtan uca test ediliyor.
- **Sunum:** `ProgramChangeResults.canonicalChanges` `SessionResults.of`'a ailelerini verir; her cümle kayıtlı bir değişiklikten gelir; hiçbir şey değişmediyse tek cümle (`NOTHING_CHANGED` / `FIRST_PLAN`). Puan, yüzde, not, suçlama ya da "daha az önemli" yok; bunu bir test ve validator tutuyor. `StateChange.ref` blueprint sonucunun `stateChangeRefs`'i içindir.
- **Port ve depolama:** tek inceltme `PersistencePort.objectivesOf`; port sayısı dört, şema değişmedi (v6).
- **Mutation 42/42**, yalnız program değişikliği suite'leri koşarken; ilk turda kaçan olmadı (iki test mutant listesi yazılırken, hiçbir koşudan önce güçlendirildi). Kontrol mutantı hayatta kaldı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS; 712 JVM testi.
- **Çalıştırılmayan: T6.** Uygulamada gerçek bir oturumdan sonra yeniden hesaplamayı ya da raporu çağıran bir şey henüz yok (16D).
- Independent 13E QA: **162/162 PASS**; validator mutation 29/29 ve yorum-içi negatif kontrol false positive vermedi. Sweep 47/47.
- Açık loop'lar: kalıcı `assessment_report` ve boylamsal geçmiş → 16C; uygulamadan çağırmak ve `stateChangeRefs`'i doldurmak → 16D; transfer/artifact kapı alanları → 15; nihai metin → 14.
- Sonraki numbered step `13F — Tanısal atlama (VDW-v0)`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/PROGRAM_CHANGE_REPORT_IMPL_SPEC.md`.

## D-104 — Tanısal atlama = VDWX-v0
**Durum:** Kabul edildi — 2026-10-01

- 13F final modeli `VDWX-v0 — Validated Diagnostic Waiver Implementation` oldu ve **AŞAMA 13 kapandı**.
- Canonical spec `docs/DIAGNOSTIC_WAIVER_IMPL_SPEC.md`; machine-readable contract `arch/13f_diagnostic_waiver/diagnostic_waiver.yaml`; research/decision synthesis `research/13f_diagnostic_waiver_research.md`; kod `core-model` (`DiagnosticFacts.kt`), `core-engines` (`DiagnosticWaiverEngine.kt`; `PlannerEngine.plan(coverage)`, `WeaknessEngine` daraltması, `ProgramChangeEngine.coverageChange`), `core-application` (`Diagnostics.kt`: `RequestDiagnostic`, `WithdrawDiagnostic`, `RebuildDiagnosticCoverage`, `ReadDiagnosticResult`, `DiagnosticPlanning`), `core-presentation` (`DiagnosticPresentation.kt`, `PlannerExplanation` kapsam cümleleri), `data-persistence` (şema v7, `latestAssessmentSessionIn`, kanıtın oturumu).
- Ana invariant: **tanısal yol mastery'ye giden daha kolay bir yol değildir; aynı kanıtı daha erken toplar.** Waiver yalnız bir Objective'in `GRE-v0` kapıları tanısal kanıtta *ilk kez* geçtiğinde verilir, o kanıtı adlandırır ve kapsamdır — asla mastery ya da retention değildir. "Biliyorum" demek kanıt değildir, tek kolay soru bir Topic'i atlatmaz, yardım ya da temiz bir kaçırma hızlı yolu suçlamadan bitirir ve yalnız gösterilen kısmın dersi atlanır.
- **Kullanıcı kararları (2026-10-01):** (1) öğretilmemiş bir Objective'de tanısal temel hata zayıflık değildir — `WLRM-v0`'ın kuralı yalnız bu durum için daraltıldı, on iki kural aynı; (2) tanısal yolu yalnız öğrenci açar (hızlı yol isteği, önceki deneyim beyanı, dışarıda öğrenmiş olma); planner'ın kendisi 18B'ye, giriş yerleşimi 16D'ye; (3) tanısal denemede yardım alınırsa ya da çözüm görülürse o Objective hızlı yoldan çıkar, ceza yok.
- **Kod yazmadan önce bulunanlar:** kodlar, ihtiyaç ve replan olayı vardı ama üreten/okuyan yoktu; `DDM-v0`'da waiver entity'si, `MSBX-v0`'da sahibi yoktu; kanıt satırı oturumunu taşımıyordu ve ders tamamlama hiçbir yerde kayıtlı değildi; görev hangi Objective'i öğrettiğini söyleyemiyordu; 13E kısmi bir waiver'dan sonra replan istemezdi; `VDW-v0` §12.1 ile `WLRM-v0` çelişiyordu.
- **Test sırasında bulunan:** ilk uygulama, aynı tanısal yolda yardım alındıktan sonraki iki temiz başarıyla yine waiver veriyordu; replay artık hızlı yol bittiği andan sonraki tanısal kanıtı saymıyor. 12E açıklaması atlanan dersi söyleyemiyor ve tanıyı bekleyen ihtiyacı "görevi yok" diye anlatıyordu; ikisi de izdeki kodlardan okunuyor.
- **Depolama:** tanısal yol `daily` bir `assessment_session` satırıdır, içeriği `diagnostic_scope/1` (katı); en yeni tanısal satır karar verir (yeni istek değiştirir, geri çekme bitirir, borç yok). Şema v7 `diagnostic_coverage` projeksiyonunu ekler (Objective başına waiver + açık tanının görünümü, CHECK'ler) ve başka biçimde içerik taşıyan `daily` satırı reddeder; dolu v6 fixture'a karşı test edildi. `DDM-v0` projeksiyon envanteri ve `MSBX-v0` motor sahipliği bu kararla birer kalemle genişletildi (`VDW-v0` → `diagnostic_coverage_state`); kabul edilmiş 9C/9D kontratları düzenlenmedi.
- **Planner:** açık Objective'i olan her Skill için bir P3 `diagnostic_opportunity` (öğrenci istediği için `decisive`); her açık Objective için taze, güvenilir, H0, kritikliğine göre mastery'ye uygun bir item; yalnız eksik kapı istenir. Kapsam tutması yalnız Objective'lerini beyan eden derslere uygulanır: hepsi atlandıysa `resolved_before_selection` (`partial`/`full_coverage_waiver`), biri hâlâ kontrol ediliyorsa `conditional_not_selected` (`user_requested_fast_path`). Önkoşul bekleyen tanı `diagnostic.prerequisite_blocked` yazar. Planlama kanıt okumaz.
- **Rapor:** `coverage_waived` (`confirmed_capabilities`) ve `coverage_waiver_withdrawn` (`not_reliably_measured`) eklendi; waiver replan olayını `diagnostic_waiver_granted` yapar.
- **Port ve model:** tek port inceltmesi `latestAssessmentSessionIn`, dört port; `EvidenceRow.assessmentSessionId`, `TaskCandidate.targetObjectives`, `WeaknessEvent.diagnosticBaseline`.
- **S06** gerçek kodla üç seviyede koşuyor; `PDT-v0` invariant 12 kapsandı; 3H'nin on altı senaryosunun hepsi koşuyor.
- **Daraltılan yaşayan kapılar:** `E10D-02_projection_inventory` (DDM-v0'ın sekizi kalır; fazlası kabul edilmiş sonraki bir kontratın `projection_extension`'ı ile beyan edilmeli), `E13E-02` iki kontrolü ve `E13E-06_triggers` (13E'nin türleri ve sırası değişmeden önde; eklenenler beyan edilmeli), `E13E-09`, `E13D-10` (şema sürümü sahipli), `E13A-07_trust_from_store` (güven okuması değişmeden paylaşılan `StoreTrust`'a taşındı), 13E model testi ve 13D depo testi; hiçbir garanti zayıflamadı.
- **Mutation 69/69**, yalnız 13F suite'leri koşarken; ilk turda V04 (açık bağımsız yeniden kontrol) ve V67 (istek satırında fazla alan) test boşluğuydu, testler eklendi; V29 (gereksiz güven filtresi, kaldırıldı) ve V56 eşdeğerdi, anlamlı mutantlarla değiştirildi; null smart-cast'ı bozan iki mutant yeniden yazıldı; bütün set tek değişmemiş ağaçtan yeniden koşuldu. Kontrol mutantı hayatta kaldı. Koşucunun ilk sürümü hiçbir şey koşmuyordu (Python WSL bash'e ulaşıyordu) ve bunu PASS değil void olarak raporladı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS; 783 JVM testi.
- **Çalıştırılmayan: T6.** Uygulama hızlı yolu henüz sunmuyor ve use case'leri çağırmıyor (16D).
- Independent 13F QA: **220/220 PASS**; validator mutation 29/29 ve yorum-içi negatif kontrol false positive vermedi. Sweep 48/48.
- Açık loop'lar: Topic durumu (16C), uygulamadan çağırma ve giriş yerleşimi (16D), planner kaynaklı tanı (18B), curriculum sürümleri arası waiver ve authored içerik (15), replay maliyeti (18E), metin (14).
- Sonraki numbered step `14A — Tutor davranış sözleşmesi`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/DIAGNOSTIC_WAIVER_IMPL_SPEC.md`.


## D-105 — Tutor davranış sözleşmesi = TUTX-v0
**Durum:** Kabul edildi — 2026-10-01

- 14A final modeli `TUTX-v0 — Tutor Behaviour Contract` oldu ve **AŞAMA 14 başladı**.
- Canonical spec `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md`; machine-readable contract `arch/14a_tutor_contract/tutor_contract.yaml`; research/decision synthesis `research/14a_tutor_contract_research.md`; kod `core-model` (`TutorFacts.kt`, `TutorInstructions.kt`, `AssistanceInterpretation.kt`), `core-ports` (`TutorPort`), `core-application` (`AskTutor.kt`, `NullTutor.kt`), `core-presentation` (`TutorPresentation.kt`), `ai-adapter` (`AiTutor.kt`), `app-wiring` (`TutorProvider`), `data-persistence` (`evidenceFor` gösterilmiş çözümü okuyor).
- Ana invariant: **tutor istek üzerine öğretir ve asla karar vermez.** Yalnız sorulana cevap verir, öğrencinin seçtiğinden fazlasını açmaz, öğrencinin ne yapabildiğini iddia etmez ve gerçekten gösterdiği her yardım olduğu gibi kaydedilir. Göstermediği yardım hiçbir yerde kaydedilmez ve söylediği hiçbir şey kanıt değildir.
- **Kullanıcı kararları (2026-10-01):** (1) **ayrı `TutorPort`** — yardım ile yargının provenance'ı farklıdır (`2D` §16); port, `D-104`'ün projeksiyon envanterini genişletmesi gibi **beyan edilmiş uzantı** (`port_extension`) olarak eklendi, `MSBX-v0`'ın dört portu ve 9D kontratı düzenlenmedi; (2) **serbest soru**, deneme açıkken cevabın en fazla ne kadar açabileceğini (H1–H4) önce öğrenci seçer, H3/H4 önce açıklanır, kaydedilen seviye seçilen seviyedir; deneme dışında seviye sorulmaz; (3) **14A ağ çağrısı yapmaz**; ilk gerçek sağlayıcı çağrısı, router ve model kimliklerinin güncel kaynakla yeniden doğrulanması **14G**'nindir.
- **Kod yazmadan önce bulunanlar:** (1) tutor yoktu — port, istek, yanıt, null uygulama ya da adaptör yoktu; tek AI yüzeyi `EvaluatorPort.evaluate`'ti; (2) kaydedilen yardım kanıta ulaşmıyordu — `RecordEvidence`'ı çağıran herkes bağımsızlık sınıfını kendisi veriyordu, yani bir tutor H4'ü bağımsız iş olarak kaydedilebilirdi; (3) gösterilmiş çözüm motorlara hiç ulaşmıyordu — `evidenceFor` her satırı `solutionExposed = false` döndürüyordu (`RVRX-v0` açık loop'u, → 14); (4) runner'ın H3/H4 açıklaması yalnız cevap açıkken doğruydu, cevap gönderildikten sonra yanlıştı (`2D` §5.3).
- **Beş kapalı istek:** `hint`, `explain_differently`, `question`, `explain_mistake`, `gloss`. **Yardım her zaman istenebilir:** `TutorRules.prepare`'in "yardım yok" sonucu yoktur; uymayan istek anına uyan isteğe **yönlendirilir** ya da **eksik**i söylenir ve tek yönlendirme her zaman yardıma ulaşır. Hedef İngilizce segmentin gloss'u cevabı verir, bu yüzden ipucudur (`TEIP-v0` §5.3).
- **Kayıt asla izin verilenden az yardım iddia etmez:** deneme açıkken öğrencinin tavanı, gönderilmiş cevaptan sonra `H4` (seviye sorulmadı ve açıklama çözümü gösterebilir), gloss `H1` + `non_target_support`, deneme yokken hiçbir şey. Tutor'un kendi seviye beyanı denetlenemez; yalnız **reddetmek** için kullanılır (tavanı aşan ya da tavan varken seviye beyan etmeyen yanıt gösterilmez), kaydı asla düşürmez. H3/H4 açıklaması ana göre konuşur: cevap gönderildikten sonra denemenin değişmediğini söyler.
- **Cihazdan yalnız mevcut görev çıkar:** mesaj core'da kurulur (`TutorInstructions.userMessage`) ve cihaz dışında bayt bayt doğrulanır; geçmiş, mastery, retention, zayıflık, plan, profil, exposure, provenance ve izler için istekte alan yoktur. Referans çözüm yalnız artık hiçbir şey açığa vuramayacağında gider. Malzeme veridir, talimat değildir: yapıştırılan `</task>` bölüm kapatıp uygulama gibi konuşamaz.
- **Yanıt şemaya bağlıdır** (`tutor_reply/1`, `additionalProperties: false`, Kotlin sözlüklerinden kurulur): hüküm, puan, mastery iddiası, plan ya da doğrulanmış misconception için alan yoktur; gösterilemeyen yanıt `invalid_response`'tur, hüküm değil; metin asla hüküm için ayrıştırılmaz.
- **Yanıtsızlık:** reddetme, zaman aşımı, ağ hatası, geçersiz yanıt ve kullanılamazlık **yardım verilmedi** demektir — hiçbir şey gösterilmez, **hiçbir şey kaydedilmez**, öğrenci suçlanmaz, sessiz yeniden deneme yoktur. Aynı istek için tavanın altında **yazılmış yardım** varsa o gösterilir ve kendi bilinen seviyesiyle kaydedilir; yerine hiçbir şey uydurulmaz.
- **Kaydedilen yardım kanıtın bağımsızlığını belirler** (`AssistanceInterpretation`, `2D` §5–§6): cevap donmadan önce hedefe H3/H4 → `requires_independent_recheck`; `generated_or_copied` ya da `teach` görevi → `practice_only`; H1/H2 ya da yardımla/ortak yazım beyanı → `assisted`; aksi `independent`. Gönderimden sonraki yardım geriye uzanmaz, hedef dışı destek sayılmaz, `unknown_provenance` kimseyi suçlamaz.
- **Gösterilen çözüm o andan itibaren exposure'dır** (`AskTutor`, `QAB-v0` §24) ve sonraki kanıt onu, **denemenin kendi sırasına** göre okur; gönderilmiş cevabın açıklaması o cevaba geriye uzanmaz, yalnız görülmüş item gösterilmiş çözüm değildir. Şema değişmedi.
- Ölçen amaçlarda (`assess`, `retain`, `diagnose`) yardım engellenmez; açık bir ölçüm item'ında gösterilen çözüm item'ı öğrenmeye çevirir ve bu söylenir; tanısal yolda cevap açıkken hedefe yardım o Objective'in hızlı yolunu bitirir (`D-104`). **Rehberliğin azalması tutor'un değildir:** tutor esirgeyerek azaltmaz; scaffold planner seçimi ve görevin instruction mode'u üzerinden, kanıtla azalır (`TEIP-v0` §5.4).
- Talimat metni kanonik ve sürümlü (`tutor_instructions/1`); panelin hiçbir durumu fault tonu taşımaz, AI yardımı öğretim metni olarak etiketlenir, her yanıtsızlık hiçbir şeyin kaydedilmediğini söyler.
- **Daraltılan yaşayan kapılar:** port sayısını dörde sabitleyen on sekiz kontrol (`E10E-07`, `E11A-10`, `E11B-08`, `E11C-10`, `E11D-13`, `E11E-09`, `E12A-11`, `E12B-12`, `E12C-09`, `E12D-09`, `E12E-04`, `E12F-07`, `E13A-12`, `E13B-12`, `E13C-11`, `E13D-11`, `E13E-09`, `E13F-11`) artık "`MSBX-v0`'ın dördü + kabul edilmiş, `D-105`'li beyanlı uzantı" okuyor; beyan edilmemiş bir port hâlâ düşürüyor, hiçbir garanti zayıflamadı. İlk taramada grep'in kaçırdığı on kapı da düştü ve aynı biçimde daraltıldı.
- **Tarama bir hata daha yakaladı:** iki yeni test Türkçe metni varsayılan locale ile küçültüyordu (`.lowercase()`); 10C'nin `E10C-15` kapısı (`AMTS-v0`) yakaladı, testler Türkçe/kök locale'e çevrildi ve bu iki testin koruduğu on dört mutant yeniden koşuldu (14/14).
- **Mutation 68/68**, yalnız 14A suite'leri koşarken; ilk turda T02 derlenmedi (derleme hatası tespit sayılmaz), derlenen bir biçimde yeniden yazıldı ve bütün set tek değişmemiş ağaçtan yeniden koşuldu; kontrol mutantı hayatta kaldı. Koşucu önce Gradle'ı gerçekten çalıştırdığını kanıtladı ve bir mutant elle, adı konmuş testin düştüğü görülerek doğrulandı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 adaptör testleri PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS; 835 JVM testi.
- **Çalıştırılmayan: T6.** Uygulama tutor'u henüz çağırmıyor (16D) ve gerçek sağlayıcı bağlı değil (14G).
- Independent 14A QA: **219/219 PASS**; validator mutation 30/30 ve yorum-içi negatif kontrol false positive vermedi. Sweep 49/49.
- Açık loop'lar: yanlış analizi ve misconception hafızası (14B), alternatif anlatım (14C), kod değerlendirme (14D), AI kodunun anlaşılma kontrolü (14E), açık uçlu değerlendirme (14F), adaptör çağrı noktası/router/model güncelliği (14G), yazılmış ipucu basamakları ve görev instruction mode'u (15), paneli çizmek ve `AskTutor`/`independence`'ı uygulamadan çağırmak (16D), konuşma geçmişi (16B), yanıtların beyan ettikleri seviyeye karşı kalibrasyonu (18D).
- Sonraki numbered step `14B — Yanlış analizi`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md`.

## D-106 — Yanlış analizi = WAAX-v0
**Durum:** Kabul edildi — 2026-10-01

- 14B final modeli `WAAX-v0 — Wrong-Answer Analysis & Misconception Memory` oldu.
- Canonical spec `docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md`; machine-readable contract `arch/14b_wrong_answer_analysis/wrong_answer_analysis.yaml`; research/decision synthesis `research/14b_wrong_answer_analysis_research.md`; kod `core-model` (`MisconceptionFacts.kt`, `EvaluationResult.misconceptionHypotheses`), `core-engines` (`MisconceptionEngine.kt`), `core-application` (`AnalyzeWrongAnswer.kt`, `RebuildWeakness` içinde `WeaknessEvents` ve `MisconceptionRows`, `RecordEvidence` etiketleri), `core-presentation` (`WrongAnswerPresentation.kt`), `data-persistence` (şema v8, `misconceptionsOf`, etiket okuma), `data-curriculum` (`[misconception]` bölümü).
- Ana invariant: **yanlış bir cevap bir bilgidir, öğrenci hakkında bir hüküm değil.** Bir misconception etiketi yalnız taşıdığı kanıt ve kaynağı kadar güçlüdür: zayıflık motorunun kendi kuralı Objective'i ne kadar taşıyorsa o kadar ilerler, AI'ın önerisi asla hipotezin üstüne çıkmaz, Objective için beyan edilmemiş etiket hiç saklanmaz ve hipotez öğrenciye yalnız soru olarak sorulur.
- **Kullanıcı kararları (2026-10-01):** (1) **kapalı katalog** — etiketler yayımlanmış curriculum'da Objective sürümü başına beyan edilir (içerik 15'te yazılır); AI yalnız bu listeden seçebilir, liste dışı öneri saklanmaz; (2) **hipotez yalnız açık soru olarak**, yanlış cevaptan hemen sonra bir kez; Progress'te zayıflık olarak görünmez (`SPWX-v0`).
- **Kod yazmadan önce bulunanlar:** `DDM-v0` §7.1'in adlandırdığı `evidence_event.misconception_tags` kolonu 10D'den beri vardı ama hiçbir şey yazmıyor ya da okumuyordu; Kotlin `EvaluationResult` `AIAX-v0` §5.1'in `misconception_hypotheses[]` alanını düşürmüştü; `WLRM-v0`'ın `LearnerMisconceptionSignal`'inin projeksiyonu, sahibi ve yükselme kuralı yoktu; etiketlerin karşılaştırılacağı bir katalog yoktu.
- **Katalog:** `misconception` curriculum tablosu (`misconception.<namespace>.<slug>`, tek Objective sürümüne pinli, açık soru zorunlu ve soru olmalı); sürümüyle tek transaction'da yayımlanır, değişmezdir, yayımlanmamış Objective'e işaret eden ya da başka sürüm taşıyan etiket bütün paketi reddettirir; paket formatına katı `[misconception]` bölümü; `PersistencePort.misconceptionsOf` — inceltme, yeni port değil.
- **Kanıttaki etiketler:** `EvaluationResult.Verified`/`.Provisional` `misconceptionHypotheses` taşır. `RecordEvidence` bir öneriyi yalnız bileşen yanlış gittiyse (`not_met`/`partially_met`), kendi Objective'i içinse ve katalog beyan ediyorsa saklar; deterministik yoldan gelen `deterministic`, kalibre edilmemiş değerlendiriciden gelen `ai_proposed`. Yanıtsızlık hiçbir şey yazmaz. Biçim katı `misconception_tags/1`.
- **Hafıza:** `misconception_state` (`WLRM-v0` ailesi, yeni aile yok). Her satır zayıflık motorunun kendi kuralıyla, Objective'in o anki durumuna karşı atfedilir; etiket kuralın Objective'i taşıyacağı kadar ilerler (yardımlı/provisional/kısmi → hipotez; temiz mastery-öncesi hata ya da ilk temiz çelişki → destekli; taze yeniden kontrol düşer ve kapılar geçmezse → doğrulanmış), kaynak tavanıyla sınırlanır. Geçersiz, itirazlı, ön koşulu bozuk iş ve tanısal temel hata hiçbir etiketi oynatmaz; hiçbir şey açık etiketi düşürmez; taze temiz başarı kapatır, doğrulanmışı yalnız kapılar yeniden geçince; zaman kapatmaz. SQLite yaşam döngüsü dışı durumu, iki kaynak dışını, hipotezin üstündeki `ai_proposed`'ı ve kanıtsız çözümü reddeder.
- **Analiz:** `AnalyzeWrongAnswer` saklanan atfı okur (`RebuildWeakness` ile aynı `WeaknessEvents`), hiçbir şey yazmaz, AI çağırmaz. `WrongAnswerSummary`: her atıf için suçlamasız bir cümle; etiket yalnız hedef hakkında konuşan satırdan sonra anılır; hipotez yalnız kataloğun açık sorusu, destekli/doğrulanmış adlandırılır, çözülmüş anılmaz; puan, yüzde, sayı yok.
- `review.6g.misconception_taxonomy_expansion` kapandı: çalışma zamanı sözleşmesi ve kapalı katalog burada; hangi etiketlerin yazılacağı 15; gerçek öğrenci hatalarıyla genişletme 18.
- `DDM-v0`'ın curriculum envanteri (`misconception`) ve projeksiyon envanteri (`misconception_state`) bu kararla birer kalemle genişletildi; kabul edilmiş 9C kontratı düzenlenmedi.
- **Mutation 51/51**, yalnız 14B suite'leri koşarken; kontrol mutantı hayatta kaldı; derleme hatası tespit sayılmadı. Mutasyondan önce üç kural testte açıktaydı (kural konuşmayan satırın sonucu, başka sürümlü etiket, önceden yayımlanmış Objective'e bağlanan etiket) ve testler eklendi. İlk turda E03 hayatta kaldı: hiçbir test, kapılar hâlâ geçerken düşen taze bir yeniden kontrolü sınamıyordu; durum eklendi ve bütün set tek değişmemiş ağaçtan yeniden koşuldu. Bir mutant (E05) elle koşuldu ve adı konmuş testlerin düştüğü görüldü.
- **Daraltılan yaşayan kapılar:** `E13D-09` (olay kurma `WeaknessEvents.of`'a taşındı; watermark hâlâ kanıttan önce, gün zorunlu, tek zaman çizgisi) ve `E13F-11_version` (13F hâlâ tam 6→7'nin sahibi; v8 `D-106`'nın `schema_migration`'ı); hiçbir garanti zayıflamadı.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS; 864 JVM testi.
- **Çalıştırılmayan: T6.** Uygulama analizi henüz göstermiyor (16D) ve etiket üreten değerlendirici yok (14D/14F/15).
- Independent 14B QA: **154/154 PASS**; validator mutation 28/28 ve yorum-içi negatif kontrol false positive vermedi. Sweep 50/50.
- Açık loop'lar: authored etiketler ve deterministik anahtarlar (15), hafızanın item seçiminde ve karşıt örnek içeriğinde kullanımı (15), Progress görünümü (16C), analizi uygulamadan çağırmak (16D), katalog genişletme ve yükselme kalibrasyonu (18), kod değerlendirme (14D), LLM değerlendirici önerileri (14F).
- Sonraki numbered step `14C — Alternatif anlatım`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md`.

## D-107 — Alternatif anlatım = ALEX-v0
**Durum:** Kabul edildi — 2026-10-01

- 14C final modeli `ALEX-v0 — Alternative Explanation` oldu.
- Canonical spec `docs/ALTERNATIVE_EXPLANATION_IMPL_SPEC.md`; machine-readable contract `arch/14c_alternative_explanation/alternative_explanation.yaml`; research/decision synthesis `research/14c_alternative_explanation_research.md`; kod `core-model` (`AlternativeExplanationFacts.kt`; `TutorAsk.form`, `TutorRequest.form`, `TutorContext.canonicalExplanation`, `TutorRules.authored`, `TutorInstructions` v2), `core-application` (`ExplainDifferently.kt`, `AskTutor.showWritten`), `core-presentation` (`AlternativeExplanationPresentation.kt`), `core-ports` (`ContentPort.explanationsFor`), `data-curriculum` (`[explanation]` bölümü).
- Ana invariant: **anlatım tutmadığında yöntem değişir; kapsam ve doğruluk değişmez.** Biçimi öğrenci seçer; önce doğrulanmış yazılı anlatım gösterilir; yalnız yazılmış olan yoksa tutor yazar — dersin kendi anlatımına dayanarak, onunla çelişmemesi söylenerek, doğrulanmamış diye etiketlenerek ve asıl anlatıma dönüş her zaman bir adım uzakta. Öğrencinin kendi durumunu gerektiren bir biçimi AI asla yazmaz.
- **Kullanıcı kararları (2026-10-01):** (1) **biçimi öğrenci seçer** — kısa bir menüden; hiçbir şey arkasından sıralanmaz ya da seçilmez; bu oturumda gösterilenler işaretlenir; (2) **önce yazılmış, yoksa AI** — biçim için tavana sığan doğrulanmış yazılı anlatım varsa tutor çağrılmaz.
- **Kod yazmadan önce bulunanlar:** `explain_differently` isteği bir biçim taşımıyordu (her alternatif modelin seçimi olurdu); `LEARNING_BEHAVIOR_RULES` §12'nin istediği yazılı alternatif/remediation anlatımlarının içerikte yeri yoktu; tutor isteği dersin kavram anlatımını taşıyamıyordu, yani bir AI alternatifi çelişmemesi gereken metne dayandırılamıyordu; öğrencinin misconception'ıyla karşılaştırma `AIAX-v0` §11'in asla gönderilmeyen öğrenci verisini gerektirirdi.
- **Biçimler (`ExplanationForm`, kapalı):** `plain_reteach`, `different_example`, `worked_example`, `state_trace`, `prerequisite_refresh` (`LEARNING_BEHAVIOR_RULES` §9 ve `WLRM-v0` stratejileri; tutor yazabilir); `misconception_contrast` (yalnız yazılı); `canonical` (dersin kendi anlatımı; alternatif değildir, her alternatifin dayanağı ve dönüş yeridir).
- **Yazılı anlatımlar:** `explanation.<namespace>.<slug>`, tek Objective sürümüne pinli, yazarının beyan ettiği seviye (açık bir cevap varken gösterilirse o seviyede kaydedilir; dersin anlatımının seviyesi yoktur), karşılaştırma yalnız bir katalog misconception'ı adlandırır. Katı `[explanation]` paket bölümü; `ContentPort.explanationsFor` — inceltme, yeni port değil; mağazaya yayımlanmaz; şema değişmedi.
- **Menü ve gösterim:** sözlük sırası, sıralama yok; yazılı varsa yazılı, yoksa tutor yazabiliyorsa AI; karşılaştırma yalnız öğrencinin hafızasının açık tuttuğu (hypothesis/supported/confirmed) bir etiket için yazılı karşılaştırma varsa ve 'sık yapılan bir karışıklık' olarak — hafıza cihazda okunur, gönderilmez. En az açan yazılı anlatım tavana sığarsa tutor çağrılmaz (`AskTutor.showWritten`); AI alternatifi dersin anlatımı `<canonical>` olarak eklenerek istenir; sığan yazılı da AI de yoksa gösterilecek doğru bir şey olmadığı söylenir. 14A'nın tavan, H3/H4 açıklaması, deneme dışında kayıt olmaması ve gösterilen çözümün exposure olması kuralları değişmedi.
- **Tutor talimatları `tutor_instructions/2`:** kural 14 (`<canonical>` dersin doğrulanmış anlatımıdır; çelişme, kapsam ekleme, emin değilsen söyle) ve kural 15 (adı konan biçimde anlat; 'farklı örnek' ve 'çözümlü örnek' asla görevin kendi item'ı değildir). `<canonical>` diğer bölümler gibi materyaldir ve yapıştırılan metinle kapatılamaz. Biçim ve dayanak yokken 14A mesajları bayt bayt aynı.
- **Sunum:** AI alternatifi 'AI tarafından üretildi: dersin doğrulanmış içeriği değildir' diye etiketlenir; her alternatifte 'Asıl anlatıma dön'; puan, sayı, suçlama yok.
- **Daraltılan yaşayan kapı:** `tools/validate_tutor_contract.py` (14A) tam olarak bu dört ek için daraltıldı — altıncı bağlam alanı (`canonicalExplanation`), altıncı bölüm (`canonical`), talimat sürümü (≥1) ve alan adlarının kelime kelime okunması ('explanation' artık 'plan' okunmuyor); başka hiçbir garanti zayıflamadı.
- **Mutation 42/42**, yalnız 14C suite'leri koşarken; kontrol mutantı hayatta kaldı; derleme hatası tespit sayılmadı. İlk turda X08 hayatta kaldı: menü testi dersin kendi anlatımı yazılıyken koşmuyordu; test güçlendirildi ve bütün set tek değişmemiş ağaçtan yeniden koşuldu. Bir mutant (X28, dayanak eklenmiyor) elle koşuldu ve adı konmuş testin düştüğü görüldü.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS; 883 JVM testi.
- **Çalıştırılmayan: T6.** Uygulama menüyü henüz sunmuyor (16D) ve anlatımlar henüz yazılmadı (15).
- Independent 14C QA: **123/123 PASS**; validator mutation 30/30 ve yorum-içi negatif kontrol false positive vermedi. Sweep 51/51.
- Açık loop'lar: yazılı anlatımlar ve karşılaştırmalar (15), menüyü çizmek ve uygulamadan çağırmak (16D), adaptör çağrı noktası (14G), biçim kalibrasyonu (18).
- Sonraki numbered step `14D — Kod değerlendirme`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/ALTERNATIVE_EXPLANATION_IMPL_SPEC.md`.

## D-108 — Kod değerlendirme = CDEX-v0
**Durum:** Kabul edildi — 2026-10-01

- 14D final modeli `CDEX-v0 — Code Evaluation` oldu.
- Canonical spec `docs/CODE_EVALUATION_IMPL_SPEC.md`; machine-readable contract `arch/14d_code_evaluation/code_evaluation.yaml`; research/decision synthesis `research/14d_code_evaluation_research.md`; kod `core-model` (`CodeEvaluationFacts.kt`), `core-application` (`EvaluateCode.kt`), `core-presentation` (`CodeEvaluationPresentation.kt`), `core-ports` (`ContentPort.codeTestsFor`), `data-curriculum` (`[code_test_suite]`/`[code_test]` bölümleri); PC tarafı koşucu `tools/code_test_runner.py` ve fixture'ları `tools/fixtures/code_test_14d/`.
- Ana invariant: **kod çalıştırılarak değerlendirilir, yoksa yalnız bir görüştür.** Bir kod görevi yalnız dersin kendi testleriyle doğrulanır — öğrencinin bilgisayarında koşulur ve raporu katı okunur — ve bir test yalnız yazıldığı Objective için konuşur. Çalışmayan test hiçbir şey ölçmemiştir ve öğrenciye karşı sayılmaz. Test yoksa AI kodu yalnız görev provisional sonuca izin veriyorsa ve en çok provisional olarak değerlendirir; doğrulanmış sonuç isteyen görev AI'a hiç sorulmaz. Testlerin geçmesi kodun istenen şekilde çalıştığını gösterir, öğrencinin nedenini açıklayabildiğini değil.
- **Kullanıcı kararları (2026-10-01):** (1) **testler öğrencinin bilgisayarında koşar ve rapor içe aktarılır** — ders küçük bir koşucu sağlar, öğrenci onu kendi koduyla çalıştırıp raporu yapıştırır; telefonda hiçbir şey derlenmez ya da çalışmaz; (2) **test yoksa AI değerlendirmesi yalnız provisional kanıttır** ve yalnız görev `provisional_allowed` diyorsa; testli görevde kanıtı testler yazar, AI yalnız geri bildirim verir (`TUTX-v0` `explain_mistake`).
- **Kod yazmadan önce bulunanlar:** hiçbir üretim kodu bir değerlendirme üretmiyordu — `RecordEvidence` bir sonuç kaydedebiliyor, `EvaluatorPort` sorulabiliyordu, ama hiçbir yol kodu yargılamıyordu; `AIAX-v0` §5.2'nin kod için kabul ettiği deterministik yol (derleyici + Objective'e özgü testler) kodun bulunduğu bilgisayarda koşamıyordu (`LEARNING_BEHAVIOR_RULES` §1: telefon IDE değildir); çok-Objective'li bir görevde bir testi Objective'ine bağlayan bir şey yoktu; değerlendirici portunun `Verified` döndürmesini engelleyen bir şey yoktu.
- **Testler içeriktir:** `codetest.<namespace>.<slug>`, tek item sürümüne pinli; her test (`lower_snake_case`) tek bir hedef Objective için konuşur ve başarısızlığı bir katalog misconception'ını önerebilir; suite bir derleme Objective'i adlandırabilir ve derleme ile test aynı Objective'i paylaşmaz. Katı paket bölümleri; beyan edilmemiş suite'in testi, testsiz suite ya da bir item sürümü için iki suite paketi okunamaz yapar. `ContentPort.codeTestsFor` — inceltme, yeni port değil; mağazaya yayımlanmaz; şema değişmedi.
- **Koşucu ve rapor:** `tools/code_test_runner.py` yalnız standart kütüphane, kabuksuz, süre sınırı ve çıktı karşılaştırması (`exact` / `trim_trailing_whitespace`) suite'te yazılır; `code_test_report/1`'i her işletim sisteminde aynı baytlarla basar; başlatılamayan komut ortam hatasıdır ve kod hakkında bir şey söylemez. Uygulama raporu tamamen ya da hiç okumaz. Rapor öğrencinin kendi koşusudur; ürün kişisel kullanım içindir (`D-080`), imzalanmaz ve kurcalanamaz diye sunulmaz.
- **Bir rapor neyi kanıtlar:** başka item, başka suite sürümü, farklı test kümesi, derleme Objective'i varken atlanmış derleme ya da ortam hatası hiçbir şey ölçmez. Derleme yalnız derleme Objective'ini taşır; derleme Objective'i yoksa başarısız derleme kimseyi suçlamaz ve testli Objective'ler ölçülmemiş kalır. Bir Objective yalnız bütün testleri koşup geçtiyse karşılanmış, koşanların hepsi düştüyse karşılanmamış, karışıksa kısmen karşılanmıştır; biri koşmadıysa geçenler karşılanmış sayılmaz. Zaman aşımı başarısızlıktır: yazılmış sınır testin parçasıdır. Sonuç `Verified`, değerlendirici `deterministic/code_tests@<suite>@v<N>|code_test_report/1`; mevcut kanıt hattıyla kaydedilir; ölçülmemiş bileşen `invalid` satırdır, sıfır değildir.
- **Kim yargılayabilir:** test varsa testler — görev ne izin verirse versin AI sorulmaz; test yok ve görev doğrulanmış isterse kimse (ölçülmez, AI'a gitmez); test yok ve görev provisional'a izin verirse AI — yalnız görev metni ve kod gider. Eksik ya da bozuk rapor asla AI'a düşmez. Değerlendirici portundan `Verified`, hiç Objective, aynı Objective'e iki kez ya da hedef dışı Objective `invalid_response`'tur; her yanıtsızlık hiçbir şey ölçmez ve yanlış cevap değildir. `EvaluationRequest` değişmedi.
- **Sunum:** Objective başına bir cümle; ölçülmeyen 'ölçülemedi … yanlış sayılmadı'; AI'ın kararı 'AI'a göre' ve 'AI değerlendirmesi: doğrulanmamıştır' etiketiyle; testli sonuçta 'testlerin geçmesi … neden çalıştığını açıklayabilmek ayrıca ölçülür'; rapor, koşucu ya da değerlendiriciden kaynaklanan hiçbir şey öğrencinin hatası denmez; puan, sayı, yüzde yok.
- **Mutation 49/49**, yalnız 14D suite'leri koşarken; kontrol mutantı hayatta kaldı; derleme hatası tespit sayılmadı; bir eşdeğer mutant (D26) gerekçesiyle dışarıda bırakıldı. İlk turda D10 (tek bilinmeyen test durumu) ve D11 (`@v0` referansı) hayatta kaldı; testler güçlendirildi ve bütün set tek değişmemiş ağaçtan yeniden koşuldu. Bir mutant (D20, başarısız derlemenin her Objective'i suçlaması) elle koşuldu ve adı konmuş testin düştüğü görüldü.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS, koşucu beş fixture programında PASS; 905 JVM testi.
- **Çalıştırılmayan: T6** (uygulama kodu henüz değerlendirmiyor, 16D) **ve bir C derlemesi** (bu makinede C derleyicisi yok; derleme adımı Python `py_compile` ile sınandı).
- Independent 14D QA: **122/122 PASS**; validator mutation 30/30 ve yorum-içi negatif kontrol false positive vermedi. Sweep 52/52.
- Açık loop'lar: gerçek görevlerin suite'leri (15), raporu artifact olarak saklamak ve uygulamadan çağırmak (16D), kod için adaptör istemi (14G), AI yazımı kodun kavranması (14E), açık uçlu değerlendirme (14F), AI kod değerlendiricisinin kalibrasyonu (18).
- Sonraki numbered step `14E — AI-generated code comprehension check`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/CODE_EVALUATION_IMPL_SPEC.md`.

## D-109 — AI yazımı kod anlama kontrolü = ACCX-v0
**Durum:** Kabul edildi — 2026-10-02

- 14E final modeli `ACCX-v0 — AI-Generated Code Comprehension` oldu.
- Canonical spec `docs/CODE_COMPREHENSION_IMPL_SPEC.md`; machine-readable contract `arch/14e_code_comprehension/code_comprehension.yaml`; research/decision synthesis `research/14e_code_comprehension_research.md`; kod `core-model` (`ComprehensionFacts.kt`; `AssistanceInterpretation`; `TutorIntent.CHECK_UNDERSTANDING`, `TutorAsk`/`TutorRequest` `checkQuestion`/`learnerAnswer`, `TutorInstructions` v3), `core-application` (`CheckUnderstanding.kt`), `core-presentation` (`ComprehensionPresentation.kt`, `TutorPresentation` etiketi), `core-ports` (`ContentPort.comprehensionChecksFor`), `data-curriculum` (`[comprehension_check]` bölümü).
- Ana invariant: **başkasının yazdığı kodun çalışması öğrenci hakkında hiçbir şey kanıtlamaz; onu açıklayabilmek anlamayı kanıtlar, üretimi değil.** Öğrenci AI'ın ya da başka bir kaynağın yazdığı ya da büyük ölçüde gösterilmiş bir çözümün verdiği kodu gönderdiğinde hemen ardından bir anlama kontrolü sunulur ve geçilebilir; önce yazılmış kontroller gelir ve cevap anahtarıyla değerlendirilir; yalnız yazılmış kontrol yoksa tutor öğrencinin kendi kodu hakkında soru sorar ve bu pratiktir, kanıt değildir. Doğru cevap yazarının beyan ettiği türde kanıttır, item'ın kendi üretimi asla değildir; öğrencinin yazmadığı kod üretim Objective'i için bağımsız yeniden kontrol açar.
- **Kullanıcı kararları (2026-10-02):** (1) **önce yazılmış, yoksa tutor pratiği** — dersin cevap anahtarlı kontrolleri; yoksa tutor soru sorar ama AI'ın yazdığı soru güvenilir değerlendirme içeriği olmadığından kanıt yazmaz; (2) **hemen sonra ve isteğe bağlı** — aynı akışta sunulur, geçmek yanlış sayılmaz; (3) **AI yazımı kod yeniden kontrol açar** — `generated_or_copied` ölçen bir görevde `requires_independent_recheck` olur (`2D` §8 senaryo A); öğretim görevinde pratik kalır.
- **Kod yazmadan önce bulunanlar:** 14A'nın `AssistanceInterpretation`ı `generated_or_copied`'ı `practice_only` yapıyordu; mastery motoru bunu dışarıda bırakıyor ve hiçbir yeniden kontrol açılmıyordu (`2D` §8 senaryo A'ya aykırı); SC-012'nin istediği anlama kontrolü hiç yoktu; bir anlama cevabının item'ın kendi üretim kanıt türünü taşımasını engelleyen bir şey yoktu (`2D` §17 korkuluk 3).
- **Ne zaman sunulur:** yalnız öğrenci kodu tek başına yazmadıysa — kendi beyanıyla (`generated_or_copied`, `mixed_authorship`) ya da cevap donmadan hedefe gösterilen H3/H4 yardımıyla. Kendi işi, H1/H2 yardım, bilinmeyen köken (kimseyi suçlamaz), cevap donduktan sonraki yardım ve hedef çevresindeki destek sunmaz; kodun görünüşünden hiçbir şey çıkarılmaz.
- **Türler ve yazılı kontroller:** `2D` §9'un dört anlama sorusu (`line_purpose`, `removal_effect`, `state_effect`, `find_the_bug`); 'aynı mantığı başka değişkenlerle uygula' üretimdir ve yeniden kontrolün kendisidir. Yazılı kontrol `comprehension.<namespace>.<slug>`, tek item sürümüne pinli, item'ın hedeflediği tek Objective için, iki-dört seçenek ve cevap aralarından; kanıt türü yazarınca beyan edilir ve item'ın kendi türü olamaz. Katı `[comprehension_check]` bölümü; `ContentPort.comprehensionChecksFor` — inceltme; mağazaya yayımlanmaz; şema değişmedi.
- **Bir cevap neyi kanıtlar:** anahtarla; doğru seçenek `met`, başkası `not_met`, seçilmemiş geçilmiştir ve hiçbir şey yazılmaz. `Verified`, değerlendirici `deterministic/comprehension_key@<check>@v<N>`; mevcut kanıt hattıyla kontrolün kendi kanıt türünde kaydedilir, böylece mastery motoru onu yalnız Objective'in o türü kanıt saydığı yerde sayar — üretim olarak asla.
- **Tutor pratiği:** altıncı istek `check_understanding` (`TUTX-v0`'ın beyanlı uzantısı): yalnız cevap donduktan sonra ve gönderilmiş kodla; cevap yokken tek kısa soru sorar ve cevaplamaz, cevapla (ve cevapladığı soruyla) neyin doğru neyin eksik olduğunu söyler ve açıklar, asla notlamaz (kural 16). `<check_question>` ve `<learner_answer>` materyaldir. `tutor_instructions/3`; yanıt şemasının intent listesi büyüdüğü için `tutor_reply/2`. 14A'nın diğer kuralları geçerli. Yalnız yazılmış kontrol yoksa sunulur ve asla kanıt değildir.
- **Bağımsızlık:** öğretim görevi kuralı provenance'tan önceye alındı ve `generated_or_copied` artık `requires_independent_recheck`; mastery motoru zaten bu satırlar için çözülmemiş yeniden kontrol açıyor ve planner yeni, görülmemiş bir varyantla planlıyor — bunların hiçbiri değişmedi; ceza değildir ve mastery'yi silmez.
- **Daraltılan yaşayan kapılar (kontratta beyanlı):** 14A'nın `E14A-02_intents`, `E14A-05_message_sections`, `E14A-06_version`, `E14A-07_interpretation_order` ve 14C'nin `E14C-07_version_raised`, `E14C-07_reply_schema_unchanged`, `E14C-07_canonical_is_material` kapıları; uzantıyı 14E kontratından okurlar, 'önce' listesi 14A'nın kendi listesi olmalıdır — yalnız tam olarak bu değişiklik geçer.
- **Mutation 43/43**, yalnız 14E suite'leri koşarken, ilk ve tek temiz koşuda; kontrol mutantı hayatta kaldı; derleme hatası tespit sayılmadı. Koşudan önce kodu okurken bir boşluk kapatıldı: tutor sorusunun pratik etiketini sınayan test yoktu. Bir mutant (E18, AI yazımı kodun yeniden pratik olması) elle koşuldu ve adı konmuş testin düştüğü görüldü.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS; 922 JVM testi.
- **Çalıştırılmayan: T6.** Uygulama kontrolü henüz sunmuyor (16D) ve kontroller henüz yazılmadı (15).
- Independent 14E QA: **112/112 PASS**; validator mutation 30/30 ve yorum-içi negatif kontrol false positive vermedi. Sweep 53/53.
- Açık loop'lar: yazılı kontroller (15), kontrolü çizmek ve uygulamadan çağırmak (16D), `check_understanding` adaptör istemi (14G), serbest metin anlama cevapları (14F), tutor sorularına güven kalibrasyonu (18).
- Sonraki numbered step `14F — Açık uçlu cevap değerlendirme`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/CODE_COMPREHENSION_IMPL_SPEC.md`.

## D-110 — Açık uçlu cevap değerlendirme = OREX-v0
**Durum:** Kabul edildi — 2026-10-02

- 14F final modeli `OREX-v0 — Open-Response Evaluation` oldu.
- Canonical spec `docs/OPEN_RESPONSE_EVALUATION_IMPL_SPEC.md`; machine-readable contract `arch/14f_open_response_evaluation/open_response_evaluation.yaml`; research/decision synthesis `research/14f_open_response_evaluation_research.md`; kod `core-model` (`OpenResponseFacts.kt`, `OpenResponseInstructions`; `EvaluationResult.Provisional.rubricFindings`), `core-application` (`EvaluateOpenResponse.kt`), `core-presentation` (`OpenResponsePresentation.kt`), `core-ports` (`ContentPort.answerKeyFor`/`rubricFor`; `EvaluationRequest.rubric`/`misconceptionCatalog`), `data-curriculum` (`[answer_key]`, `[accepted_answer]`, `[rubric]`, `[rubric_criterion]`).
- Ana invariant: **serbest metin bir cevap dersin doğru cevabın neyi içerdiğini söylediği şeye göre değerlendirilir, nasıl kulağa geldiğine göre değil.** Kısa cevap dersin kabul edilen cevaplarıyla doğrulanır; uzun cevap rubric'iyle, kriter kriter değerlendirilir. AI yalnız her kriterin karşılanıp karşılanmadığını söyleyebilir — bunun her Objective için ne anlama geldiğine core karar verir — ve AI'ın kararı asla provisional'dan fazlası değildir. Doğrulanmış sonuç isteyen görev AI'a hiç sorulmaz. Hiçbir şey cevap vermezse cevap bekler: hiçbir şey yazılmaz, öğrenci rubric'le kendi cevabını pratik olarak kontrol edebilir ve yalnız öğrenci isterse yeniden değerlendirilir.
- **Kullanıcı kararları (2026-10-02):** (1) **kısa cevaplar kabul edilen cevap listesiyle** — ders cevapları ve eşleştirme kuralını yazar, eşleşme `verified`; eşleşmeyen cevap AI'a düşmez; (2) **AI yoksa bekler, rubric'le öz-kontrol** — kanıt yazılmaz, öz-kontrol pratiktir, yeniden değerlendirme yalnız öğrenci isterse, arka planda kendiliğinden denenmez (`AIAX-v0` §7.1).
- **Kod yazmadan önce bulunanlar:** `EvaluationRequest` görev ve cevabı taşıyordu ama rubric'i taşımıyordu (her AI hükmü kendi iyi cevap fikrine göre olurdu); `EvaluationResult` `AIAX-v0` §5.1'in `rubric_findings[]`'ini düşürmüştü, yani değerlendirici yalnız kararın kendisini döndürebilirdi; `QAB-v0` §19'un normalize cevap anahtarı ve çoklu geçerli cevabı için deterministik yol yoktu.
- **Kısa cevaplar:** `answerkey.<namespace>.<slug>`, tek item sürümüne pinli, item'ın hedeflediği tek Objective için; eşleştirme yalnız uçları kırpar ve satır sonlarını normalleştirir, içerideki hiçbir şeyi değiştirmez; büyük-küçük harf duyarsızsa yalnız ASCII harfler katlanır, Türkçe `ı`/`I` ve `i`/`İ` sessizce birleştirilmez. Eşleşme `met`, gerisi `not_met`, ikisi de `Verified` (`deterministic/answer_key@<key>@v<N>`); boş cevap geçilmiştir.
- **Rubric'ler ve bulgular:** `rubric.<namespace>.<slug>`, her kriter hedeflenen tek Objective için; değerlendirici kriter başına `met`/`not_met`/`unclear` önerir (`rubricFindings`). `OpenResponse.acceptAi` karar verir ve değerlendiricinin kendi Objective hükmünü kullanmaz: hepsi karşılandı → met; değerlendirilenlerin hepsi karşılanmadı → not met; karışık → partially met; karşılananlar varken biri belirsiz → not reliably measured. `Verified` iddiası, eksik, bilinmeyen ya da tekrarlı kriter geçersiz yanıttır; misconception önerileri yalnız hedeflenen Objective'ler için tutulur, kanıt hattı yalnız katalog etiketlerini saklar.
- **Kim yargılar:** anahtar varsa anahtar (AI yok); yalnız rubric varsa ve görev doğrulanmış isterse hiçbir şey ölçülmez (AI yok); provisional'a izin verirse AI kriter kriter; ikisi de yoksa hiçbir şeye göre değerlendirilemez. Cihazdan yalnız hedef Objective'ler, görev metni, rubric, cevap ve o Objective'lerin katalog etiketleri çıkar — cevap dışındakilerin hepsi curriculum metnidir.
- **Talimatlar:** `open_response_instructions/1`, şema `open_response_evaluation/1` (`findings[]`, `misconception_hypotheses[]`, ek alan yok, not alanı yok); kriter başına tek bulgu; uzunluk, üslup, akıcılık, güven ve dil asla sayılmaz; materyal talimat değildir; genel not ya da öğrenci hakkında iddia yok; misconception yalnız listelenen katalogdan; emin değilse `unclear` ve bu öğrenciye karşı sayılmaz. Mesaj core'da kurulur ve cihaz dışında bayt bayt doğrulanır.
- **Bekleme ve öz-kontrol:** yanıtsızlık hiçbir şey ölçmez ve yazmaz; öz-kontrol rubric'i gösterir — rubric doğru cevabın ne içerdiğini söylediği için bu gösterilmiş bir çözümdür ve tutor'unki gibi exposure olarak kaydedilir, öğrenciye önceden söylenir. Core'da hiçbir şey yeniden denemez.
- **Daraltılan yaşayan kapı (kontratta beyanlı):** 14D'nin `E14D-05_request_unchanged` kapısı — ilk üç alan aynı, gerisi tam olarak bu uzantı, ve kod değerlendirmesi hâlâ yalnız üç alanı gönderiyor.
- **Mutation 49/49**, yalnız 14F suite'leri koşarken; kontrol mutantı hayatta kaldı; derleme hatası tespit sayılmadı. İlk turda F22 (hedef dışı bir Objective için kriter) ve F47 (tamamlanmış bir rubric'in yanında yetim kriter) yaşadı — testler bu durumları sınamıyordu — ve F32 derlenmedi (mutant yeniden yazıldı, sayılmadı); testler eklendi ve bütün set tek değişmemiş ağaçtan yeniden koşuldu. Bir mutant (F16, değerlendiricinin kendi hükmünün kullanılması) elle koşuldu ve adı konmuş testin düştüğü görüldü. Ayrıca paket testi yazılırken gerçek bir hata yakaladı: yeni bölümler ayrıştırıcının kararından sonra okunuyor, hataları yok sayılıyordu.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS; 941 JVM testi.
- **Çalıştırılmayan: T6.** Uygulama açık uçlu cevabı henüz değerlendirmiyor (16D) ve anahtarlar/rubric'ler henüz yazılmadı (15).
- Independent 14F QA: **117/117 PASS**; validator mutation 30/30 ve yorum-içi negatif kontrol false positive vermedi. Sweep 54/54.
- Açık loop'lar: anahtarlar ve rubric'ler (15), `open_response_evaluation/1` için adaptör çağrı noktası (14G), uygulama çağrısı ve öz-kontrol ekranı (16D), değerlendirici kalibrasyonu (18).
- Sonraki numbered step `14G — Provider abstraction/fallback`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/OPEN_RESPONSE_EVALUATION_IMPL_SPEC.md`.

## D-111 — Sağlayıcı adaptörü = PRVX-v0 (AŞAMA 14 kapandı)
**Durum:** Kabul edildi — 2026-10-02

- 14G final modeli `PRVX-v0 — Provider Adapter` oldu ve **AŞAMA 14 kapandı** (TUTX-v0 → WAAX-v0 → ALEX-v0 → CDEX-v0 → ACCX-v0 → OREX-v0 → PRVX-v0).
- Canonical spec `docs/PROVIDER_ADAPTER_IMPL_SPEC.md`; machine-readable contract `arch/14g_provider_adapter/provider_adapter.yaml`; research/decision synthesis `research/14g_provider_adapter_research.md`; kod `ai-adapter` (`Provider.kt`, `OpenAiResponses.kt`, `Json.kt`, `AiTutor.kt`, `AiEvaluator.kt`, `ConnectionCheck.kt`), `core-model` (`CodeEvaluationInstructions.kt`), `core-presentation` (`AiSettingsPresentation.kt`), `app-ui` (`AiSettingsScreen.kt`), `app-wiring` (`AiSettings`, `AiWiring` withAi/withoutAi, Profile, AI iş parçacığı, build başına manifest).
- Ana invariant: **sağlayıcı portların arkasında değiştirilebilir bir ayrıntıdır.** Yalnız öğrencinin bu cihazda şifreli saklanan kendi anahtarı bir çağrı yapabilir; anahtar yoksa cihazdan hiçbir şey çıkmaz ve üründe hiçbir şey çalışmayı bırakmaz. Gönderilen tam olarak core'un kurduğudur — talimatları, mesajı, şeması — ve sağlayıcının söylediği hiçbir şey tek bir şemaya tam uyan nesne olmadıkça cevap olmaz; red red'dir, zaman aşımı çağrıyı bitirir ve öğrencinin arkasından hiçbir şey yeniden denenmez.
- **Kullanıcı kararları (2026-10-02):** (1) **sağlayıcı OpenAI** (Responses API); (2) **tek sağlayıcı, AIAX'ın tek tip düşüşü** — yanıtsızlıkta değerlendirme bekler, tutor yazılmış yardıma düşer; aynı bütçe içinde sınırlı yeniden deneme; ikinci sağlayıcı ya da model yok; (3) **anahtar ekranı 14G'de** — Profile'da gir, kaldır, bağlantıyı dene.
- **Araştırma (AIAX-v0 §8.2):** OpenAI Responses API referansı, Structured Outputs rehberi ve model rehberi 2026-10-02'de okundu ve build'e kaydedildi (`ProviderConfig.reference`); sağlayıcıya hiçbir şey gönderilmedi, hesap ya da anahtar kullanılmadı.
- **Kod yazmadan önce bulunanlar:** adaptörün çağrı noktası yoktu (10A–14F bilerek); uygulamanın ağ izni ve anahtar girme yolu yoktu; kod için AI talimatı yoktu (14D açık loop'u); sağlayıcı yanıtları varsayılan olarak en az 30 gün saklıyor (`store` true) — `AIAX-v0` §11'in izin verdiğinden fazla.
- **İstemci:** `POST /v1/responses`; yalnız `model` (router'dan), `instructions` (core'un metni), `input` (core'un mesajı), `text.format` = katı `json_schema` (core'un şeması) ve `store: false`; anahtar yalnız `Authorization` başlığında, yalnız TLS üzerinden. Okuma sırası: önce durum (içerik filtresiyle `incomplete` → `refused`), sonra `refusal` içeriği (→ `refused`), en son tek bir `output_text` tek bir JSON nesnesi olarak; gerisi `invalid_response`. 401/403 → `unavailable` (anahtar reddedildi); zaman aşımı ya da bütçe bitişi → `timed_out`; ağ hatası/408/429/5xx denemeler bitince `transport_error`; diğer 4xx `transport_error`. Uçtan uca bütçe 60 sn, en çok 2 deneme (ürün varsayılanı, `ProviderConfig`'de); yalnız ağ hatası/408/429/5xx yeniden denenir; arka planda hiçbir şey yeniden denenmez. Kütüphane eklenmedi: `HttpURLConnection` ve küçük, katı bir JSON okuyucu/yazıcı; adaptörde hiçbir şey loglanmaz.
- **Router:** `tutor_help`, `open_response_evaluation`, `code_evaluation`; varsayılan model üçü için de `gpt-6-astra` (`AIAX-v0` §8.1 kalite katmanı); değiştirmek yalnız yapılandırmaya dokunur; core hiçbir model ya da sağlayıcı adı bilmez.
- **Yanıtların okunması:** tutor `tutor_reply/2`'nin tam dört alanı; gösterilip gösterilmeyeceği hâlâ `TutorRules.accept`. Açık uçlu cevap yalnız bulgular ve katalog etiketleri (bileşenleri core türetir). Kod için yeni `code_evaluation_instructions/1` / `code_evaluation/1` (adlandırılmış Objective başına bir bileşen, not alanı yok, üslup sayılmaz); adlandırılmamış Objective tahmin edilmez. Etiketler yalnız isteğin katalogundan. Her satırda `openai/<cevap veren model>@<şema>`. Bağlantı testi sabit bir talimat ve `ping` gönderir.
- **Anahtar ve ekran:** Android Keystore'da duran bir AES-GCM anahtarıyla şifrelenir; yalnız şifreli metin yedeklenmeyen dizine yazılır (`allowBackup` kapalı); depoya hiç girmez, dışa aktarımda yoktur; loglanmaz, gösterilmez (alan maskeli, kaydedince temizlenir). Varlığı mağaza iş parçacığında okunur. Kaldırmak şifreli metni ve Keystore girdisini siler: AI kapanır, başka hiçbir şey değişmez. Ekran sormadan önce söyler: AI isteğe bağlı, anahtar nerede durur, cihazdan ne çıkar, kaldırmak hiçbir şeyi silmez. **AI'sız build'de ağ izni hiç yoktur**; AI build'in manifest'i yalnız `INTERNET` ekler — birleştirilmiş manifest'ler build sonrası okundu.
- **Daraltılan yaşayan kapılar (kontratta beyanlı):** 10E'nin `E10E-12_test` ve 14A'nın `E14A-12_adapter` kapıları aynı güvenceyi söyleyen yeniden adlandırılmış testi kabul eder; 14A'nın `E14A-08_adapter_unavailable` ve `E14A-08_wiring` kapıları istemcisiz yolun hâlâ kullanılamaz olduğunu, tutor dosyasının ağ açmadığını ve AI'sız build'in `NullTutor`'u koruduğunu denetler.
- **Mutation 41/41**, yalnız 14G suite'leri koşarken, ilk ve tek temiz koşuda; kontrol mutantı hayatta kaldı; derleme hatası tespit sayılmadı. Bir mutant (G02, sağlayıcının çağrıyı saklaması) elle koşuldu ve adı konmuş testin düştüğü görüldü.
- Çalıştırılan runlar: T1 PASS, T2 PASS, T3 PASS, T5 PASS, adaptörlü ve adaptörsüz `assembleDebug` PASS, birleştirilmiş manifest okuması PASS; 964 JVM testi.
- **Çalıştırılmayan: T6 ve canlı sağlayıcı çağrısı.** Hiçbir kontrol canlı sağlayıcı çağırmaz (`TVSX-v0`) ve Claude API anahtarı girmez; ilk gerçek çağrı ve Keystore yolu öğrencinin, cihazda Profile'dan.
- Independent 14G QA: **129/129 PASS**; validator mutation 30/30 ve yorum-içi negatif kontrol false positive vermedi. Sweep 55/55.
- Açık loop'lar: ilk canlı çağrı ve Keystore cihazda (öğrenci, T6), tutor ve değerlendiricileri görev ekranlarından çağırmak ve kullanım görünürlüğü (16D), kalibrasyon (18).
- Sonraki numbered step `15A — Computer / Programming Fundamentals`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/PROVIDER_ADAPTER_IMPL_SPEC.md`.

## D-112 — Computer / Programming Fundamentals içeriği = CPFX-v0
**Durum:** Kabul edildi — 2026-10-02

- 15A final modeli `CPFX-v0 — Computer / Programming Fundamentals content` oldu; **AŞAMA 15 başladı** ve uygulama ilk kez gerçek içerik sevk ediyor.
- Canonical spec `docs/COMPUTING_FUNDAMENTALS_CONTENT_SPEC.md`; machine-readable contract `arch/15a_computing_fundamentals/computing_fundamentals.yaml`; doğrulama raporu `arch/15a_computing_fundamentals/content_verification.yaml`; research/decision synthesis `research/15a_computing_fundamentals_research.md`; içerik kaynağı `curriculum/content/15a_computing_fundamentals/` (paket, gösterim merdiveni, 12 Skill dosyası, bağımsız inceleme); üretici `tools/build_curriculum_package.py`; sevk edilen paket `android/app-wiring/src/main/assets/curriculum_package.txt` (üretilir, elle düzenlenmez); kod `core-model` (`AuthoredTaskFacts.kt`), `data-curriculum` (`PackageFormat`, `FileContentSource.taskCandidates`).
- Ana invariant: **içerik yalnız onu neyin kontrol ettiği kadar güvenilirdir.** Her cevap anahtarı gerçekte olanla karşılaştırılır — kod çalıştırılır, seçmeli sorunun her seçeneği çalıştırılır, dersin kendi örnek çıktıları çalıştırılır — çalıştırılamayan yerde yetkili bir kaynağa dayanır; ve her item, çalıştırılmış ya da kaynaklı, doğrulanmış sayılmadan önce bağımsız bir incelemeden geçer. AI'ın yazdığı hiçbir şey kendi başına güvenilir sayılmaz, dersin öğretmediği hiçbir gösterim bir item'da okunmaz ve bir görev yalnız planner'ın zaten açtığı, kendi Skill'ine ait ve amacının hizmet edebileceği bir ihtiyaca hizmet eder.
- **Kullanıcı kararları (2026-10-02):** (1) **çalıştırma + bağımsız QA** — anahtar çalıştırılarak ya da kaynakla, her item ve anlatım bağımsız incelemeyle; AI içeriği `ai_generated` kalır, en çok `validated`, yani en çok `standard_mastery_eligible`; (2) **okunur Python alt kümesi** — 15A dersleri küçük bir Python alt kümesini yalnız okumak için öğretir, yazmak 15B'nindir; (3) **netleştir ve kaydet** — Objective kimlikleri 6C'nin; cümle, gözlenebilir davranış ve direct type özgülleştirildi ve her değişiklik gerekçesiyle kaydedildi.
- **İçerik yazmadan önce bulunanlar:** hiçbir format görev taşıyamıyordu, bu yüzden her ihtiyaç adaysızdı; 6C alt-grafının tamamı `draft` idi ve rotada değildi; 6C Objective metadata'sı şablondu (iki iteration Objective'i birebir aynı, hepsinde direct type explanation); `#` her değeri kesiyordu ve item prompt'u satır sonu çözmüyordu; `while` koşulları `expression_boolean_reasoning`'i gerektiriyor ama graph söylemiyor; 6C'nin `requires_transfer` beyanı mağazada taşınmıyor; tek asset ve paket sürümüne bağlı varlıklar.
- **Alt-graf:** FBB-v0 §6.1'in 6C'de ayrıştırılmış paylaşılan computing/programming alt-grafı — 12 Skill, 13 Objective, 11 iç kenar (10 hard, 1 soft), 8 topic — `draft`'tan `published`'a onaylandı; dışarıdan giren kenar yok; kimlik değişmedi; 9 Objective'in direct type'ı code_reading oldu, 4'ü explanation kaldı; criticality değişmedi. Giriş Skill'leri: `program_execution_model`, `state_assignment_model`.
- **İçerik:** her Objective için dersin kendi anlatımı, worked example, gerektiğinde ön koşul hatırlatması ve durum izi, 2–3 katalog misconception ve yazılı karşılaştırmaları, yedi varyant ailesinde yedi item (2 basic, 2 authentic, 3 transfer) ve dört explanation Objective'i için rubric item'ı. Toplam 95 item (83 çalıştırılarak, 8 kaynakla, 4 rubric), 79 anlatım, 34 misconception, 60 görev (Skill başına teach/practice/check/review/repair), 40 ders iddiası çalıştırıldı.
- **Görev:** `AuthoredTask` 3B §15'in içerik tarafıdır; hangi amacın hangi ihtiyaca hizmet edebileceği (`TaskServing`) 3B §3.1 ve §19'dan kapalı; `diagnose` asla yazılmaz. Bir görev yalnız tetikleyiciyi beyan ettiyse ve ihtiyaç kendi Skill'i hakkındaysa aday olur; paket, eksik bir şeyi sunan, başka Skill'in Objective'ini adlandıran ya da item'larından fazla güven iddia eden görevi reddeder. Ders içi item yalnız teach görevindedir.
- **Format:** yalnız `#` ile başlayan satır yorumdur; prompt'ta backslash-n satır sonudur; katı `[task]` bölümü. Şema, portlar ve mağaza değişmedi.
- **Bağımsız inceleme:** ilk geçişte 94 item'ın 62'si ve 79 anlatımın 78'i geçti; 94 anahtarın hepsi doğruydu, düşenler ders içi sızıntıydı (soru dersin kendi anlatımında, worked example'ında ya da karşılaştırmasında zaten çözülmüştü) ve bir anlatımda aritmetik hata vardı. Düşen 32 item yeni senaryolarla yeniden yazıldı, ders içi item'lar sonraki havuzlardan çıkarıldı; ikinci ve üçüncü geçişte hepsi geçti.
- **Daraltılan yaşayan kapılar (kontratta beyanlı):** 11D'nin `E11D-13_no_asset_ships` (yalnız üretilmiş asset sevk edilebilir) ve 12C'nin `E12C-09_adapter_answers_truthfully` (adaptör yalnız yazılmış görevlerle cevap verir).
- **İlk plan:** sevk edilen paket, gerçek kapı ve planner'la, geçmişi olmayan bir öğrenci için iki giriş dersini bütçe içinde planlıyor; on bağımlı Skill bekliyor; hiçbir ihtiyaç adaysız kalmıyor (`FirstPlanTest`).
- **Mutation 22/22**, yalnız 15A suite'leri koşarken, son ve tek temiz koşuda; kontrol mutantı hayatta kaldı; derleme hatası tespit sayılmadı; ilk koşuda F04 (test güçlendirildi) yaşadı ve F10 eşdeğerdi (gereksiz kontrol kaldırıldı). Çalıştırılan runlar: T1, T2, T3, `verifyModuleBoundaries`, adaptörlü ve adaptörsüz `assembleDebug` PASS ve paketin APK içindeki SHA-256'sı kaynakla aynı; 977 JVM testi.
- Independent 15A QA: **131/131 PASS**; validator mutation 30/30 ve iki negatif kontrol false positive vermedi. Sweep 56/56.
- **Çalıştırılmayan: T6 ve sevk edilen paketin SQLite'a ilk yayımlanması** — ilk ingestion öğrencinin, cihazda.
- Açık loop'lar: ikinci paketin nasıl sevk edileceği (15B); `requires_transfer`'ın mağazada taşınmaması ve `iteration_reasoning` ← `expression_boolean_reasoning` kenarı (15H); runner'ın görev havuzundan görülmemiş item seçmesi (16D); dakika kalibrasyonu (18B); T6 ve cihazda ingestion.
- Sonraki numbered step `15B — Python Foundations`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/COMPUTING_FUNDAMENTALS_CONTENT_SPEC.md`.

## D-113 — Python Foundations içeriği = PYFX-v0
**Durum:** Kabul edildi — 2026-10-02

- 15B final modeli `PYFX-v0 — Python Foundations content` oldu; uygulama ikinci içerik paketini sevk ediyor ve öğrenci ilk kez **kod yazıyor**.
- Canonical spec `docs/PYTHON_FOUNDATIONS_CONTENT_SPEC.md`; machine-readable contract `arch/15b_python_foundations/python_foundations.yaml`; doğrulama raporu `arch/15b_python_foundations/content_verification.yaml`; research/decision synthesis `research/15b_python_foundations_research.md`; içerik kaynağı `curriculum/content/15b_python_foundations/` (paket, yazma gösterimi, 20 Skill dosyası, bağımsız inceleme, 85 test suite'i); sevk edilen paket `android/app-wiring/src/main/assets/curriculum_package_v2.txt` (üretilir, elle düzenlenmez); kod `core-ports` (`ContentPort.curriculumPackages`), `core-application` (`IngestCurriculum.ingestAll`), `data-persistence` (`CurriculumStore.republishedEntities`), `data-curriculum` (`FileContentSource`: birden çok paket), `app-wiring` (sonraki asset'ler), `tools/code_test_runner.py` (UTF-8 kipi).
- Ana invariant: **bir program ne yaptığına göre, öğrencinin kendi bilgisayarında, doğru çözümü makul bir yanlış çözümden ayırabilen testlerle değerlendirilir.** Her kod item'ının referans çözümü dersin testlerini öğrencinin kullandığı koşucuyla geçer ve en az iki makul yanlış çözümün her biri en az bir testi düşürür; doğru bir çözümü prompt'un söylemediği bir nedenle (işletim sisteminin satır sonu ya da yol ayracı, konsolun kod sayfası) düşüren test, testin kusurudur. Sonraki bir paket yalnız ekler: yayımlanmış hiçbir şey yeniden taşınmaz ya da üzerine yazılmaz ve bir Skill yalnız hazır olabilmesi için gereken her şeyle yayımlanır.
- **Kullanıcı kararları (2026-10-02):** (1) **kapsam: 6C'nin ayrıştırdığı on yedi tohum + onların dışarıdaki üç hard ön koşulu** — `scope_name_resolution`, `exception_handling`, `string_text_operations`; 20 Skill, 21 Objective; (2) **artımlı paket, varlık sürümü anlamsal revizyondur** — Aşama 15'in her adımı kendi paketini sevk eder, yeni varlıklar sürüm 1'dir; 11D'nin "her varlık paketin sürümünü taşır" kuralı "**yayımlanmış bir sürümün üzerine asla yazılmaz**" olarak daraltıldı: mağaza yayımlanmış bir domain, module, topic, Skill, Objective, misconception ya da kaynağı yeniden taşıyan paketi bütünüyle reddeder.
- **İçerik yazmadan önce bulunanlar:** ikinci paket sevk edilemiyordu (tek asset, paket sürümüne bağlı varlıklar); 14D koşucusu Windows'ta doğru Türkçe çıktılı programları düşürüyordu (konsol kod sayfası); üç tohum Skill hiç hazır olamazdı (hard ön koşulları tohum değil); fonksiyon yazmak gerekiyor ama graph'ta ön koşul değil; metin kipindeki dosyalar platforma göre farklı (CRLF); traceback'ler Python sürümüne göre farklı (`^`/`~` satırları); `path_handling` Objective'inin eylem adı dosya okuma diyor, cümlesi yol işlemleri.
- **Alt-graf:** FBB-v0 §6.2'nin on iki Python tohumu (6C'de on yedi Skill) + üç ön koşul — 20 Skill, 21 Objective, 31 kenar (7'si 15A Skill'lerinden) — `draft`'tan `published`'a; kimlik değişmedi; direct type'lar 6C'nin (17 authored_code, 4 code_reading); geçmişi olmayan öğrenci için hiçbir Python Skill'i başlatılamaz, hepsi 15A'yı bekler.
- **İçerik:** her Objective için dersin anlatımı, ön koşul hatırlatması, worked example, gerektiğinde durum izi, 2–3 katalog misconception ve yazılı karşılaştırmaları, çalıştırılan ders iddiaları; kod Objective'inde beş item (cozum.py, dersin test suite'iyle), okuma Objective'inde altı item. Toplam 109 item (85 kod suite'iyle, 17 stdout, 7 traceback), 116 anlatım, 44 misconception, 100 görev, 59 ders iddiası çalıştırıldı. Kod item'ları yalnız `external_ai`'yi yasaklar; terminal ve belge yazmanın parçasıdır (QAB-v0 §21).
- **Doğrulama:** yeni `traceback` kipi programı `program.py` olarak çalıştırır, gösterilen traceback'in gerçeği olduğunu (sürüme bağlı işaret satırları hariç) ve anahtarın sorulan parça olduğunu doğrular; yeni `suite` kipi 14D koşucusunu — öğrencinin kendisininkini — referans ve en az iki yanlış çözüm üzerinde koşar. Build deterministik; validator iki paketi ve her suite'i bayt bayt yeniden kurar.
- **Öğrencinin bilgisayarı:** koşucu `python -X utf8` ile başlatır (PEP 686, 3.15'ten itibaren varsayılan); dosya testleri kendi geçici dizinlerini kurar ve yazılanı metin kipinde okur; yol sonuçları ad, uzantı, boolean ya da `as_posix()` olarak karşılaştırılır; eksik `encoding=` testle yakalanamaz ve bu sınır kaydedildi.
- **İkinci paketin sevki:** `curriculum_package_v<N>.txt`; içerik kaynağı paketleri birlikte okur ya da hiçbirini sunmaz (sürümler kesin artar, hiçbir şey iki pakette yazılmaz); `ContentPort.curriculumPackages()` varsayılanlı port inceltmesi; `ingestAll` en eskiden başlar, yayımlanmış sürüm `AlreadyPublished`, ilk ret durdurur.
- **Bağımsız inceleme:** ilk geçişte 109 item'ın 98'i ve 116 anlatımın 113'ü geçti; her anahtar, traceback ve ders iddiası doğruydu. Düşenler: 6 ders içi sızıntı (15A'nın kalıbı yeniden), testlerini makul bir yanlış çözümün geçtiği 4 item (yapılmamış takas, kullanılmamış bileşim, iki her-şeyi-yakala), testin istediğini söylemeyen 1 prompt ve 3 anlatım cümlesi. Hepsi yeniden yazıldı; ikinci geçişte bir item (`.get()` kullanan doğru çözümü düşüren test) kaldı, düzeltildi; üçüncü geçişte **109/109, 116/116**. İnceleme ayrıca 85 referansı, 172 yanlış çözümü ve yaklaşık 70 ek doğru/yanlış çözümü koşucudan geçirdi.
- **Daraltılan yaşayan kapılar (kontratta beyanlı):** 11D `E11D-07_version_mismatch_refused` ve `E11D-09_failed_parse_serves_nothing`, 11A `E11A-13_content_port_returns_null`, 14B `E14B-03_version_refused`.
- **Sevk edilen kurs:** iki gerçek paket gerçek içerik kaynağıyla okunup gerçek ingestion ile **gerçek SQLite şemasına** JVM'de yayımlanıyor, ikinci açılış hiçbir şey yazmıyor; gerçek kapı ve planner geçmişi olmayan öğrenciye yine yalnız iki giriş dersini sunuyor, diğer otuz Skill bekliyor (`ShippedCourseTest`; app-wiring unit testleri bundled SQLite'ın JVM sürümünü yalnız test runtime'ında kullanır).
- **Mutation 27/27**, yalnız 15B suite'leri koşarken, son ve tek temiz koşuda; kontrol mutantı hayatta kaldı; derleme hatası tespit sayılmadı; ilk koşuda 8 mutant yaşadı ve dört test boşluğu kapatıldı (tek başına taşınan Objective ve kaynak, iki pakette yazılan anlatım/görev/suite/anahtar, sonraki paketin item'larının sunulması, portun varsayılanı). Çalıştırılan runlar: T1, T2, T3, `verifyModuleBoundaries`, adaptörlü ve adaptörsüz `assembleDebug` PASS ve iki paketin APK içindeki SHA-256'sı kaynakla aynı; 992 JVM testi. Independent 15B QA: **254/254 PASS**; validator mutation 35/35 ve iki negatif kontrol false positive vermedi. Sweep 57/57.
- Çalıştırılmayan: **T6 ve cihazda ilk ingestion** — ilk ingestion öğrencinin, telefonda; SQLite yayımı yalnız JVM'de koştu.
- Açık loop'lar: item başına beyan edilen graph gereksinimleri, sabit çıktılı item'lar, `path_handling` eylem adı, gösterim kapsamı (`in`, `is`, `pass`, `break`, `as`; ders iddiaları taranmıyor) ve `requires_transfer` (15H); Unicode normal biçimleri (20A); runner'ın görev havuzu ve cozum.py/raporun telefon–bilgisayar taşınması (16D); dakika kalibrasyonu (18B); T6 ve cihazda ingestion (19).
- Sonraki numbered step `15C — C Foundations`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/PYTHON_FOUNDATIONS_CONTENT_SPEC.md`.

## D-114 — C Foundations içeriği = CFNX-v0
**Durum:** Kabul edildi — 2026-10-02

- 15C final modeli `CFNX-v0 — C Foundations content` oldu; uygulama üçüncü içerik paketini sevk ediyor ve öğrenci C yazıyor.
- Canonical spec `docs/C_FOUNDATIONS_CONTENT_SPEC.md`; machine-readable contract `arch/15c_c_foundations/c_foundations.yaml`; doğrulama raporu `arch/15c_c_foundations/content_verification.yaml`; research `research/15c_c_foundations_research.md`; içerik kaynağı `curriculum/content/15c_c_foundations/` (9 Skill dosyası, C yazma gösterimi, bağımsız inceleme, 35 test suite'i); sevk edilen paket `android/app-wiring/src/main/assets/curriculum_package_v3.txt`; kod `data-curriculum` (`PackageFormat.unescape`), `tools/build_curriculum_package.py` (Linux'ta doğrulama).
- Ana invariant: **bir C programı öğrencinin onu derleyip çalıştırdığı yerde değerlendirilir: Linux'ta gcc ile, öğrencinin kendi koşucusundan.** Her anahtar gcc'nin ve programın gerçekte yaptığıdır, her suite doğru çözümü makul bir yanlış çözümden ayırır, fonksiyon isteyen item'ı yalnız fonksiyon geçer ve paket C kodunu yazıldığı gibi gösterir — ters bölü-n ters bölü-n kalır — hiçbir önceki paketin okunuşunu değiştirmeden.
- **Kullanıcı kararları (2026-10-02):** (1) **Linux ön koşulu:** `terminal_filesystem_navigation` 15C'de yayımlanır (6C'de ilk C Skill'i ona hard bağlı; kendi ön koşulu yok; 15E kalanını ekler); (2) **ortam:** WSL Ubuntu + gcc, kullanıcı kurdu (`build-essential` gerekti); öğrenci ve doğrulama aynı gcc'yi kullanır; (3) **girdi:** `standard_io_basic` eklendi (programlar scanf ile okur ve birden çok girdiyle denenir).
- **İçerik yazmadan önce bulunanlar:** ilk C Skill'i bir Linux Skill'ini bekliyor; girdi olmadan C programları denenemiyor; yazar makinesinde C derleyicisi yok (VS içinde eski MSVC; dağıtımsız WSL); `gcc` paketi C kütüphane başlıklarını getirmedi; paket bir ters bölü-n'i gösteremiyordu (C kodu yazılamıyordu); fonksiyon item'ı fonksiyonsuz bir programla geçiliyordu; terminal Skill'i üçüncü bir giriş noktası.
- **Alt-graf:** FBB-v0 §6.3'ün dört C tohumu (6C'de yedi Skill) + `standard_io_basic` + `terminal_filesystem_navigation` — 9 Skill, 10 Objective, 15 kenar (7'si 15A'dan) — `published`, kimlik değişmedi; işaretçi, dizi ve bellek 15D'nin ve her 15C item'ında yasak. `compile_link_run_basic`'in iki Objective'i 6C'de aynı şablon cümleyi taşıyordu; eylem adlarına göre ayrıldı.
- **İçerik:** 51 item (35 kod suite'iyle — 5'i dersin kendi main'iyle fonksiyon çağırır —, 6 okuma, 5 aşama, 5 kabuk), 56 anlatım, 21 misconception, 45 görev, 32 ders iddiası Linux'ta çalıştırıldı.
- **Doğrulama Linux'ta:** `c_stdout` (gcc ile derlenip çalıştırılan programın çıktısı), `c_stage` (derleme, bağlama, çalışma ayrı ayrı; ilk başarısız aşama), `shell` (boş dizinde bash); kod item'ları öğrencinin koşucusuyla WSL içinde; fonksiyon item'ları dersin main'iyle bağlanır (`-Dmain=ogrenci_main`). 15A ve 15B bayt bayt aynı yeniden kuruluyor.
- **Biçim inceltmesi:** `PackageFormat.unescape` — çok satırlı bir değerde ters bölü-n satır sonu, çift ters bölü tek ters bölüdür; başka her ters bölü kendisidir. Önceki iki paket çift ters bölü içermediği için aynı okunur. Üretici yalnız `escape_backslash: true` beyan eden pakette kaçırır.
- **Bağımsız inceleme:** Bağımsız inceleme ilk geçişte 51 item'ın 46'sını ve 56 anlatımın 52'sini geçirdi (her anahtar doğru, 70 yanlış çözümün hepsi düştü; düşenler 3 ders içi sızıntı, öğretilmemiş %%, sınırları test etmeyen bir suite ve 4 teknik cümleydi); ikinci geçişte 51/51 ve 56/56.
- **Daraltılan yaşayan kapılar (kontratta beyanlı):** 15A `E15A-09_prompt_line_breaks` ve 14C `E14C-09_line_breaks` (ikisi de eski `.replace` çağrısını arıyordu; şimdi `unescape`).
- **Sevk edilen kurs:** üç paket gerçek SQLite şemasına JVM'de yayımlanıyor; geçmişi olmayan öğrenci yalnız giriş Skill'lerini başlatabiliyor — artık terminal de bunlardan biri — ve her C Skill'i bekliyor (`ShippedCourseTest`).
- **Mutation 8/8** (yalnız `PackageFormat.unescape` ve dört çağrı noktası; 15C başka Kotlin değiştirmedi), ilk ve tek temiz koşuda; kontrol mutantı hayatta kaldı. Validator mutation **34/34**: önceki koşular iki çökme ve bir kaçış gösterdi — kaçışı engelleyen bir derleme hatası artık FAIL'dir ve önceki paketlerin yeniden kurulumunda doğrulama hatası da aranır; son koşu yük altında 5 sn'lik bir Linux zaman aşımını gösterdi, sınır 60 sn yapıldı (öğrencinin test sınırı değişmedi). Çalıştırılan runlar: T1, T2, T3, `verifyModuleBoundaries`, adaptörlü ve adaptörsüz `assembleDebug` PASS, üç paketin APK içindeki SHA-256'sı kaynakla aynı; 1001 JVM testi. Independent 15C QA: **155/155 PASS**; sweep 58/58 (14C'nin bir kapısı da daraltıldı).
- Çalıştırılmayan: **T6 ve cihazda ilk ingestion**; SQLite yayımı yalnız JVM'de koştu.
- Açık loop'lar: item başına beyan edilen gereksinimler, sabit çıktılı item'lar, gösterim kapsamı ve `requires_transfer` (15H); runner'ın havuzu ve cozum.c/raporun telefon–bilgisayar taşınması (C Linux ister) (16D); dakika kalibrasyonu (18B); T6 (19).
- Sonraki numbered step `15D — Memory Foundations`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/C_FOUNDATIONS_CONTENT_SPEC.md`.

## D-115 — Kaynak repo herkese açık (D-080'in "repo private" maddesi kapandı)
**Durum:** Kabul edildi — 2026-10-03 (kullanıcı kararı; numaralı adım değildir)

- `xpike-dgm/ai-infra-learning-coach` **herkese açıktır** (kullanıcı, 2026-10-03). Sebep: hesabın GitHub Actions işleri bir fatura/harcama limiti engeliyle hiç başlamıyordu (15C PR #54); açık repolarda standart runner'lar ücretsizdir. Bu reponun kendi kullanımı Pro'nun aylık kotasının çok altındaydı (ekim ~165 iş-dakikası).
- **Değişmeyen:** D-080'in ürün kararı aynen geçerlidir — uygulama tek kullanıcı içindir; hiçbir uygulama merkezine yüklenmez, APK paylaşılmaz, store/dağıtım işleri kapsam dışıdır. Yalnız D-080'in "Repo private'dır" cümlesi bu kararla kapandı; kanıt ve AI yetki kuralları değişmez.
- **Açmadan önce yapılan tarama:** 667 commit'in bütün geçmişi API anahtarı, token, özel anahtar, `.env`/keystore dosyası ve şifre ataması için tarandı; hiçbiri yok. Kullanıcının e-posta adresi yalnız 50 commit'in yazar/committer bilgisinde vardır, dosya içeriğinde yoktur; kullanıcı geçmişin yeniden yazılmamasına ve adresin görünür kalmasına karar verdi.
- Repo bir kez açıldığı için içeriği (kaynak, araştırma, `vault/` notları) başkalarınca kopyalanmış olabilir; sonradan yeniden gizlemek bu kopyaları kaldırmaz.

## D-116 — Memory Foundations içeriği = MMFX-v0
**Durum:** Kabul edildi — 2026-10-03

- 15D final modeli `MMFX-v0 — Memory Foundations content` oldu; uygulama dördüncü içerik paketini sevk ediyor ve öğrenci işaretçi ve bellek ömrü çalışıyor.
- Canonical spec `docs/MEMORY_FOUNDATIONS_CONTENT_SPEC.md`; machine-readable contract `arch/15d_memory_foundations/memory_foundations.yaml`; doğrulama raporu `arch/15d_memory_foundations/content_verification.yaml`; research `research/15d_memory_foundations_research.md`; içerik kaynağı `curriculum/content/15d_memory_foundations/` (4 Skill dosyası, gösterim, bağımsız inceleme, 15 test suite'i); sevk edilen paket `android/app-wiring/src/main/assets/curriculum_package_v4.txt`; kod `tools/build_curriculum_package.py` (sanitizer ile doğrulama). Kotlin ana kodu, şema, portlar ve depo değişmedi.
- Ana invariant: **bellekle ilgili bir anahtar programın gerçekte yaptığıdır ve artık var olmayan belleğin kullanımı, tanımsız bir programın ne yazdırdığına göre değil AddressSanitizer'ın ne bildirdiğine göre değerlendirilir.** Öğrencinin derlemesi kontrol edilen derlemedir — aynı sanitizer'lar, aynı çalışma seçenekleri, temiz çıkış şartı — bu yüzden ölü belleği okuyan bir çözüm çıktısı doğru olsa bile düşer.
- **Kullanıcı onayı (2026-10-03):** 15D'ye başlamak; yeni bir karar gerekmedi — kapsam FBB-v0 §6.3'ün kalan üç tohumu (6C'nin ayrıştırdığı haliyle), hard kapanışları 15C'de yayımlı ve 15C'nin WSL Ubuntu + gcc'si sanitizer'ları zaten taşıyor.
- **Bulunanlar:** ömür hatasının çıktısı hiçbir şey kanıtlamaz (anahtarlar sanitizer bulgusuyla doğrulanır); gcc 15.2'nin libasan'ında stack-use-after-return varsayılan olarak açık (Google'ın wiki'sinin söylediğinin tersine; seçenek açıkça verilir); sanitizer raporu stdout yazılmadan programı durdurur (temiz çıkış kuralı savunmacıdır ve statik doğrulanır); ömür Skill'i işaretçiden okur ama graph bunu söylemez (işi `pointer_dereference`'ı beyan eder, 15H); yalnız fonksiyon içeren bir dosyada `NULL` başlık ister; sanitizer her geçersiz erişimi yakalamaz (ilk değeri verilmemiş işaretçi) ve dersler bunu söyler.
- **Alt-graf:** FBB-v0 §6.3'ün üç bellek tohumu (6C'de dört Skill: `address_value_distinction`, `pointer_formation`, `pointer_dereference`, `storage_lifetime_intuition`) — 4 Skill, 6 Objective, 5 kenar (3'ü 15C'den) — `published`, kimlik değişmedi; hiçbir Skill eklenmedi ve giriş noktası yok. Dizi, struct, string ve math kütüphanesi ve `goto` her 15D item'ında yasak.
- **İçerik:** 34 item (15 kod suite'iyle — hepsi dersin kendi main'iyle fonksiyon çağırır —, 12 okuma, 4 sanitizer, 3 rubric), 33 anlatım, 13 misconception, 20 görev, 15 ders iddiası Linux'ta çalıştırıldı (2'si sanitizer bulgusuyla).
- **Doğrulama Linux'ta, sanitizer ile:** `c_sanitize` (yeni) programı `-fsanitize=address,undefined -fno-sanitize-recover=all` ile derler ve `ASAN_OPTIONS=detect_stack_use_after_return=1:detect_leaks=0` ile çalıştırır; bulgu (`stack-use-after-return`, `heap-use-after-free`, `ok` …) anahtarlı seçeneği tek doğru yapmalıdır ve geçerli bir program söylediğini yazdırmalıdır. Suite'ler aynı bayraklarla derlenir (harness'ın iki derlemesi ve bağlaması dahil) ve her test çıkış kodu 0 bekler. 15A, 15B ve 15C bayt bayt aynı yeniden kuruluyor.
- **Bağımsız inceleme:** ilk geçişte 34 item'ın 29'unu ve 33 anlatımın 30'unu geçirdi (her anahtar ve ders kontrolü doğru, 30 yanlış çözümün hepsi düştü; düşenler testi geçen iki yanlış çözüm — biri istemde kendi çıktısını gösteren sabit çıktılı item —, üç ders içi sızıntı ve üç cümleydi: `sizeof(char)`, `NULL`'ın başlığı, sanitizer'ın her geçersiz erişimi yakaladığı iddiası); ikinci geçişte 34/34 ve 33/33.
- **Sevk edilen kurs:** dört paket gerçek SQLite şemasına JVM'de yayımlanıyor; geçmişi olmayan öğrenci yine yalnız üç giriş Skill'ini başlatabiliyor ve her bellek Skill'i bekliyor (`ShippedCourseTest`).
- **Mutation 9/9** (üreticinin 15D denetimi: `c_sanitized`, `c_sanitize` kipi, sanitizer'lı ders kontrolleri, harness bayrakları ve bağlama; 15D Kotlin ana kodu değiştirmedi), gerçek derleme ve dört bilerek bozulmuş fikstüre karşı; kontrol mutantı hayatta kaldı. Validator mutation **36/36**. Çalıştırılan runlar: T1, T2, T3, `verifyModuleBoundaries`, adaptörlü ve adaptörsüz `assembleDebug` PASS, dört paketin APK içindeki SHA-256'sı kaynakla aynı; 1009 JVM testi. Independent 15D QA: **132/132 PASS**; sweep 59/59.
- Çalıştırılmayan: **T6 ve cihazda ilk ingestion**; SQLite yayımı yalnız JVM'de koştu.
- Açık loop'lar: item başına beyan edilen gereksinimler ve ömür Skill'inin işaretçi kenarı, ılımlı transfer etiketleri, gösterim kapsamı ve `requires_transfer` (15H); runner'ın havuzu ve cozum.c/raporun telefon–bilgisayar taşınması (16D); dakika kalibrasyonu (18B); T6 (19).
- Sonraki numbered step `15E — Linux / Git / Shell Foundations`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/MEMORY_FOUNDATIONS_CONTENT_SPEC.md`.

## D-117 — Kullanıcının sürekli onayı: adımlar onay beklemeden sürer
**Durum:** Kabul edildi — 2026-10-03 (kullanıcı kararı; numaralı adım değildir)

- Kullanıcı 15D sırasında şunu söyledi: "ben dur diyene kadar mevcut adım bitse de sen merge et, direkt diğer adıma geç; durmanı söyleyene kadar devam et."
- Bu, AGENTS.md ve `PROJECT_MEMORY_PROTOCOL`ün her numaralı adım için istediği **kullanıcı açık onayını** kullanıcı "dur" diyene kadar karşılar. PRE, bağımsız değerlendirme, POST ve stale audit yükümlülükleri aynen sürer; yalnız adım başına onay beklenmez.
- Her adım tam protokolle biter; PR'ı CI yeşil olunca asistan merge eder ve sonraki adımın fresh PRE'si başlar.
- Normalde kullanıcıya sorulacak ürün kararlarında asistan önerdiği seçeneği seçer ve kayıtlara **"asistanın varsayılan seçimi (D-117)"** olarak yazar, "kullanıcı kararı" olarak değil; kullanıcı döndüğünde geri çevirebilir.
- Değişmeyen: şifre/kimlik bilgisi, ödeme, hesap ayarı gibi kullanıcının kendisinin yapması gereken işler, geri alınamaz işlemler ve gerçek bir arıza yine durdurur ve kullanıcıya sorulur.

## D-118 — Linux / Git / Shell Foundations içeriği = LGSX-v0
**Durum:** Kabul edildi — 2026-10-03

- 15E final modeli `LGSX-v0 — Linux / Git / Shell Foundations content` oldu; uygulama beşinci içerik paketini sevk ediyor ve öğrenci süreç çıktılarını, komut kurmayı, boru hattını ve Git'i çalışıyor.
- Canonical spec `docs/LINUX_GIT_SHELL_CONTENT_SPEC.md`; machine-readable contract `arch/15e_linux_git_shell/linux_git_shell.yaml`; doğrulama raporu `arch/15e_linux_git_shell/content_verification.yaml`; research `research/15e_linux_git_shell_research.md`; içerik kaynağı `curriculum/content/15e_linux_git_shell/` (5 Skill dosyası, kabuk gösterimi, bağımsız inceleme); sevk edilen paket `android/app-wiring/src/main/assets/curriculum_package_v5.txt`; kod `tools/build_curriculum_package.py` (gösterilen komutlar + probe; paket başına kabuk prelude'u). Kotlin ana kodu, şema, portlar ve depo değişmedi.
- Ana invariant: **bir komut hakkındaki anahtar, gösterilen komutların ta kendisi Linux'ta boş bir dizinde çalıştırılınca bash'in ve git'in gerçekte yazdığıdır;** probe yalnız sorulanı yazdırır; hiçbir anahtar kontrol eden makinenin Git'inin kendi uydurduğuna (hash, tarih, kimlik, dal adı) dayanmaz ve bir hazırlık satırı item'ının sorduğunu öğretmez.
- **Onay:** kullanıcının sürekli onayı (D-117); kullanıcıya düşen yeni bir ürün kararı çıkmadı. **Asistanın varsayılan seçimleri (D-117):** başlangıç kaydı için "# hazırlık" satırları; kontrol gösterilen komutları + probe'u çalıştırır, ayrı betik çalıştırmaz; Objective başına 8 anahtarlı item (kullanıcının havuz endişesine cevap).
- **Bulunanlar:** terminal Skill'i 15C'de yayımlanmıştı; 15C'de bir kabuk item'ının kontrolü gösterilenden ayrı bir betikti; Git makineden makineye hash, tarih, kimlik ve dal adı uydurur; değişmiş bir dosyayı görmek için kayıt gerekir ama kayıt bir sonraki Skill'dir; "nothing to commit" gibi Git mesajları stdout'a gider; Git işi dosya oluşturur ama graph bunu söylemez (item düzeyinde beyan, 15H).
- **Alt-graf:** FBB-v0 §6.4'ün tohumları (6C'de altı Skill; terminal 15C'de yayımlandı) — 5 Skill, 5 Objective, 6 kenar (3'ü 15A ve 15C'den) — `published`, kimlik değişmedi; hiçbir Skill eklenmedi ve giriş noktası yok. Kontrol akışı, kabuk değişkenleri, rm ve sudo her item'da yasak.
- **İçerik:** 41 item (40 kabuk — 8'i gözlem, 32'si terminalde uygulama —, 1 rubric), 35 anlatım, 15 misconception, 25 görev, 23 ders iddiası Linux'ta çalıştırıldı.
- **Bağımsız inceleme:** Bağımsız inceleme ilk geçişte 41 item'ın 39'unu ve 35 anlatımın 34'ünü geçirdi (40 anahtarın hepsi doğru; düşenler kanonik metnin söylediği bir mkdir kodu, istemin sormadığı bir rubric ölçütü ve git init'in hint satırlarını atlayan bir cümleydi; öğrencinin Git kimliği eksikse kayıtların başarısız olacağı da bulundu ve iki derste tekrarlandı); ikinci geçişte 41/41 ve 35/35.
- **Daraltılan yaşayan kapı (kontratta beyanlı):** 15C `E15C-10_keys_in_linux` üreticinin eski kabuk çağrısını harfi harfine arıyordu; artık prelude biçimini ve 15C'nin prelude'u olmadığını ister (15C'nin kabuk anahtarları yine düz bash ile çalışır).
- **Sevk edilen kurs:** beş paket gerçek SQLite şemasına JVM'de yayımlanıyor; geçmişi olmayan öğrenci yine yalnız üç giriş Skill'ini başlatabiliyor ve her 15E Skill'i bekliyor (`ShippedCourseTest`).
- **Mutation 5/5** (üreticinin 15E denetimi: gösterilen komutlar + probe, kabuk prelude'u; 15E Kotlin ana kodu değiştirmedi), gerçek derleme ve bilerek bozulmuş fikstürlere karşı; kontrol mutantı hayatta kaldı. Validator mutation **36/36**. Çalıştırılan runlar: T1, T2, T3, `verifyModuleBoundaries`, adaptörlü ve adaptörsüz `assembleDebug` PASS, beş paketin APK içindeki SHA-256'sı kaynakla aynı; 1017 JVM testi. Independent 15E QA: **114/114 PASS**; sweep 60/60.
- Çalıştırılmayan: **T6 ve cihazda ilk ingestion**; SQLite yayımı yalnız JVM'de koştu.
- Açık loop'lar: Git işinin dosya oluşturmayı beyan etmesi ve gösterim kapsamı (15H); 15A–15D'nin soru havuzu (kullanıcı kararı bekliyor); cevapların telefon–bilgisayar taşınması (16D); dakika kalibrasyonu (18B); T6 (19).
- Sonraki numbered step `15F — English A0→A1/A2 başlangıç paketi`; fresh PRE ile yürütülür (kullanıcının onayı D-117 ile sürüyor).

Ayrıntı: `docs/LINUX_GIT_SHELL_CONTENT_SPEC.md`.

## D-119 — English A0→A1/A2 başlangıç içeriği = EAAX-v0
**Durum:** Kabul edildi — 2026-10-04

- 15F final modeli `EAAX-v0 — English A0→A1/A2 starter content` oldu; uygulama altıncı içerik paketini sevk ediyor ve öğrenci teknik İngilizceye sıfırdan başlıyor.
- Canonical spec `docs/ENGLISH_A1_A2_CONTENT_SPEC.md`; machine-readable contract `arch/15f_english_a1_a2/english_a1_a2.yaml`; doğrulama raporu `arch/15f_english_a1_a2/content_verification.yaml`; research `research/15f_english_a1_a2_research.md`; içerik kaynağı `curriculum/content/15f_english_a1_a2/` (10 Skill dosyası, ders başına kelime listesi = lexicon, bağımsız inceleme); sevk edilen paket `android/app-wiring/src/main/assets/curriculum_package_v6.txt`; kod `tools/build_curriculum_package.py` (`english_words`, lexicon denetimi). Kotlin ana kodu, şema, portlar ve depo değişmedi.
- Ana invariant: **bir item'ın gösterdiği her İngilizce kelime, item'ın dayanabileceği bir dersin öğrettiği bir kelimedir;** hiçbir dersin öğretmediği kelimeyi derleme reddeder. Anlam anahtarı kaynağını adlandırır; bir aracın mesajı gerçek araçla üretilir ve harfi harfine gösterilir. İngilizce hiçbir teknik şey için kapı değildir.
- **Onay:** kullanıcının sürekli onayı (D-117); kullanıcıya düşen yeni bir ürün kararı çıkmadı. **D-117 15F ile sona erdi:** kullanıcı 2026-10-04'te "devam et mevcut aşamayı bitirince durursun" dedi; 15G kullanıcının yeni açık onayı olmadan başlamaz. **Asistanın varsayılan seçimleri (D-117):** 10 A1/A2 Skill; üretici tarafından uygulanan ders başına kelime listesi; Türkçe istem ve seçenekler, ölçülen İngilizce uyaran; gerçek düzeni koruyan yazılmış yardım parçaları; yalnız kararlı araç mesajları; Objective başına 8 anahtarlı item.
- **Bulunanlar:** kelime için tek dürüst koruma ders başına kelime listesidir; literal'ler (ters tırnak, tırnaklı ad, harf dışı karakterli belirteç, NameError gibi karışık harfli ad) kelime değildir; YAML yes/no/on'u boolean okur; araç mesajları sürüme ve kabuğa göre değişir (uutils/GNU mkdir, git'in bağlama noktası notu, etkileşimli/etkileşimsiz bash "command not found"); gerçek yardım sayfaları A2 öğrencisinin bildiğinden çok kelime içerir.
- **Alt-graf:** 7B'nin D01 A1/A2 çapa Skill'leri (6C ayrıştırması) — 10 Skill, 10 Objective, 9 hard kenar, hepsi İngilizcenin içinde — `published`, kimlik değişmedi; `recognize_core_technical_labels` dördüncü giriş noktası; hiçbir Skill eklenmedi; hiçbir teknik Skill İngilizceyi beklemez.
- **İçerik:** 82 item (72 kaynaklı anlam anahtarı, 8 gerçek araç mesajı, 2 rubric), 50 anlatım, 20 misconception, 50 görev.
- **Bağımsız inceleme:** Bağımsız inceleme ilk geçişte 82 item'ın 71'ini ve 50 anlatımın 46'sını geçirdi (72 anlam anahtarının hepsi doğru; düşenler Cancel'ın da savunulabildiği bir item, belirsiz bir 'It', kabuğa göre değişen bir araç mesajı, anlamı verilmemiş kelimeler, dar kabul listeleri, cevabı gösteren bir görev başlığı ve 'Permission denied dosyanın var olduğunu söyler' yanlışıydı); ikinci geçişte 82/82 ve 50/50.
- **Sevk edilen kurs:** altı paket gerçek SQLite şemasına JVM'de yayımlanıyor; geçmişi olmayan öğrenci dört giriş Skill'ini başlatabiliyor — artık ilk İngilizce ders de bunlardan biri (`ShippedCourseTest`).
- **Mutation 8/8** (üreticinin 15F denetimi: `english_words` ve lexicon; 15F Kotlin ana kodu değiştirmedi), gerçek derleme ve bilerek bozulmuş fikstürlere karşı; kontrol mutantı hayatta kaldı. Validator mutation **36/36**. Çalıştırılan runlar: T1, T2, T3, `verifyModuleBoundaries`, adaptörlü ve adaptörsüz `assembleDebug` PASS, altı paketin APK içindeki SHA-256'sı kaynakla aynı; 1025 JVM testi. Independent 15F QA: **119/119 PASS**; sweep 61/61.
- Çalıştırılmayan: **T6 ve cihazda ilk ingestion**; SQLite yayımı yalnız JVM'de koştu.
- Açık loop'lar: documentation_navigation'ın graph'ı ve cömert zorluk etiketleri (15H); 15A–15D'nin soru havuzu (kullanıcı kararı bekliyor); dinleme, konuşma, B1 ve pre-A1 köprüsü (sonraki adımlar); dakika kalibrasyonu (18B); T6 (19).
- Sonraki numbered step `15G — Assessment content`; fresh PRE ile yürütülür (kullanıcının onayı D-117 ile sürüyor).

Ayrıntı: `docs/ENGLISH_A1_A2_CONTENT_SPEC.md`.

## D-120 — Assessment content = ACNX-v0
**Durum:** Kabul edildi — 2026-10-04

- 15G final modeli `ACNX-v0 — Assessment content supplement` oldu; uygulama yedinci paketi sevk ediyor: yayımlanmış Objective'lere, yayımlanmış hiçbir şeyi değiştirmeden, öğrencinin görmediği yeni item'lar ekleyen bir **ek paket** (supplement).
- Canonical spec `docs/ASSESSMENT_CONTENT_SPEC.md`; machine-readable contract `arch/15g_assessment_content/assessment_content.yaml`; doğrulama raporu `arch/15g_assessment_content/content_verification.yaml`; research `research/15g_assessment_content_research.md`; içerik kaynağı `curriculum/content/15g_assessment/` (`items/<kaynak>/`, `independent_review.yaml`); sevk edilen paket `android/app-wiring/src/main/assets/curriculum_package_v7.txt`; üretici `tools/build_curriculum_package.py` (`build_supplement`, `form_problems`, `transfer_problems`); validator `tools/validate_assessment_content.py`.
- Ana invariant: **eklenen bir item, Objective'ini öğrencinin görmediği bir yapıda, Objective'in istediği kanıtı üretebilen bir biçimde ölçer ve yalnız bağımsız bir inceleyici geçirdikten sonra sevk edilir.** Ek paket item, büyüyen görev sürümü, transfer item'ı ve yanlış seçenek misconception anahtarı ekler; yayımlanmış hiçbir şeyin üzerine yazmaz. Bir transfer iddiası denetlenir, varsayılmaz; transfer item'ı ayın transfer slotuna ayrılır. Bir yanlış seçenek yalnız tam olarak o misconception'dan çıkıyorsa ve anahtarın kendi Objective'inin etiketiyse onu adlandırır.
- **Kullanıcı kararları (2026-10-04, yeni açık onayla):** Objective başına dersin dışında 15–20 item; transfer = içerik + aylık üretici (`TRANSFER_OPPORTUNITY`), `professional_evidence_checkpoint` AŞAMA 20'de; seçenek düzeyinde deterministik misconception anahtarları; item'ları asistan yazar, ayrı inceleyici ajanlar değerlendirir; 15F için **dürüst küçük havuz** — yalnız yapısal olarak farklı item'lar, eksik kayıtlı, dersler ve lexicon değişmez.
- **Beyan edilmiş uzantılar (kabul edilmiş spec'lerin sessiz düzenlemesi değil):** `NeedTrigger.TRANSFER_OPPORTUNITY` / `need.transfer_opportunity` (3B §2.1 / `PDT-v0` §8.1; bant entegrasyonla aynı satır, `MCA-v0` §7); `BlueprintExclusion.TRANSFER_IS_MONTHLY`; `SlotItemRefusal.TRANSFER_CLAIM_UNSUPPORTED` (`AIV-v0` §16); paket biçiminde `[answer_misconception]`, `[item]`'da `transfer_profile` ve `context_family_id`; sonraki bir paketin bir görevin yeni sürümünü yayımlaması ve önceki bir paketin anahtarını eşlemesi (D-113).
- **Kapsam:** 613 item (15A 120, 15B 235, 15C 112, 15D 63, 15E 40, 15F 43), 240 görev v2'si (yalnız öğe listesi büyüyen practice/check/review/repair; hiçbir teach görevi değişmedi), 21 transfer item'ı, 144 yanlış seçenek anahtarı (66'sı eklenen, 78'i yayımlanmış item'larda). Hiçbir Skill, Objective, kenar, Topic, anlatım ya da etiket eklenmedi; 1–6. paketler bayt bayt aynı yeniden üretiliyor.
- **Havuz:** 65 Objective'in 54'ü ≥15. Eksik kayıtlı: `scope_name_resolution`, `c.declaration_type_model`, `git.repository_status_diff` 14 (daha fazla aday kardeş item'ın ya da dersin tekrarıydı); 15F'te `technical_noun_phrase_recognition` 12, `negation_question`, `follow_bilingual_instruction`, `documentation_navigation` 11, `be_and_simple_present`, `imperative`, `preposition`, `write_command_result_note` 10 (kullanıcı kararı; öğretilmiş lexicon daha fazla yapısal fark taşımıyor).
- **Bulunanlar:** eklenen item'ları düşüren şey yanlış anahtar değil, ders sızıntısı ve kardeşine yakınlıktır; bir seçmeli item kod, uygulamalı ya da yazılı kanıt taşıyamaz (üretici artık reddediyor); aynı dersin daha zor bir item'ı transfer değildir; bağlamı adlandırılmamış bir transfer item'ı tüm derlemeyi çökertiyordu (üretici mutation koşusu buldu, düzeltildi).
- **Bağımsız inceleme:** paket başına bir inceleyici ajan her item'ı ve her eşlemeyi anahtar, sızıntı, yakınlık, ipucu, ölçülen yapı, gizli ön koşul ve zorluk etiketine göre değerlendirdi; dört tur. Son verdict turu: 425 (1), 158 (2), 25 (3), 5 (4); 613/613 item ve 144/144 eşleme geçti.
- **Kotlin:** `TransferEngine` (sahip), `TransferPlanning` (depo güveni, kapı, exposure, temiz ölçüm); planner ve aylık composer aynı ihtiyacı okur; haftalık dışlar; composer desteklenmeyen transfer iddiasını reddeder; `AcceptedAnswers.misconceptionFor` yalnız anahtarın kendi Objective'i için hipotez önerir; `PackageFormat.parse(text, earlier)` ve `FileContentSource` en yüksek görev sürümünü sunar ve eşlenmiş yanlış cevapları anahtarlarına bağlar. Şema, portlar ve depo değişmedi.
- **Doğrulama:** üretici mutation **22/22**, Kotlin mutation __KOTLIN_MUTATION__, validator mutation __VALIDATOR_MUTATION__; kontrol mutantları hayatta kaldı. T1, T2, T3, `verifyModuleBoundaries`, adaptörlü/adaptörsüz `assembleDebug` PASS; yedi paketin APK içindeki SHA-256'sı kaynakla aynı; __JVM__ JVM testi. Independent 15G QA: __QA__.
- Çalıştırılmayan: **T6 ve cihazda ilk ingestion**; SQLite yayımı yalnız JVM'de koştu.
- Açık loop'lar: havuz eksikleri (15H; İngilizce için önce daha geniş lexicon); `professional_evidence_checkpoint` (AŞAMA 20); inceleyicilerin cömert etiket notları (15H); dakika kalibrasyonu (18B); T6 (19).
- Sonraki numbered step `15H — Content QA`; fresh PRE ve kullanıcının yeni açık onayı gerekir (D-117 15F ile bitti).

Ayrıntı: `docs/ASSESSMENT_CONTENT_SPEC.md`.
