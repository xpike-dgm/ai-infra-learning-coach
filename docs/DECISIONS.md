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