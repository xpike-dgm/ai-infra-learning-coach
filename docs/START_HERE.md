# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

**Local manager takeover — D-055:** Repo yerel çalışan ana yönetici agent'a devrediliyorsa root `AGENTS.md` ve `docs/LOCAL_MANAGER_HANDOFF.md` bu dosyayla birlikte ilk bootstrap setidir. Local takeover sırasında repo içindeki tüm Markdown dosyaları ayrıca tamamen okunmalıdır.

## 1. Bu repo ne için var?
Tek kullanıcı için geliştirilecek kişisel adaptif mobil öğrenme uygulamasının ürün hafızasını, kararlarını, curriculum yönünü ve geliştirme planını kalıcı tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Sistem sabit kurs takvimi değil; gerçek Skill state, prerequisite, retention, evidence ve günlük capacity'ye göre plan üretir.

## 2. Güncel uzun vadeli hedef — D-041
> **Sıfırdan başlayan kullanıcıyı, gerektiğinde 4+ yıl veya daha uzun sürebilecek mastery-gated bir rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability seviyesine taşımak.**

4+ yıl countdown değildir. Professional readiness mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ile belirlenir.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

V1 full 4+ year curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.

## 3. Güncel rota/plan kararları

### D-042 — Python foundation
Python common technical foundation'ın resmi parçasıdır; C/C++ yerine geçmez.

### D-043 — geri çekildi
Standalone specialization-stage yorumu canonical değildir.

### D-044 — Granular Capability Map
AŞAMA 6 ana öğrenme rotasının her büyük alanını `Domain → Module → Topic → Skill → Learning Objective` seviyesinde ayrıntılandıracak. Amaç broad weakness yerine exact capability localization/remediation.

Canonical: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

### D-045 — WBA-v0
Weekly assessment blueprint-before-items; no fixed score/time/quota; granular evidence.

### D-046 — MCA-v0
Monthly assessment longitudinal state-based capability sampling; broader transfer/integration; no cumulative final/pass score.

### D-047 — QAB-v0
Question Bank yalnız MCQ değil, versioned AssessmentResource bank'idir. Stable logical ID + immutable version, lifecycle/use ceiling, exact target/prerequisite/evidence metadata, family/dependency/context/freshness ve bounded selection vardır.

### D-048 — AIV-v0
AI-generated assessment resource candidate olarak başlar; correctness/ambiguity/prerequisite/evaluator/freshness/safety validation olmadan trust/use-ceiling promotion yoktur.

### D-049 — PDM-v0 Professional Domain Backbone
Canonical: `docs/CURRICULUM_DOMAIN_MAP.md`.

- 23 ana route family high-level professional envelope olarak kilitlendi.
- Technical English parallel track.
- Python + C + Linux/Git/Shell complementary early foundations; DS&A supporting foundation.
- Systems core → distributed/platform → performance → GPU/accelerator → inference → multi-GPU → AI/GPU Infrastructure convergence yapısı.
- Performance route boyunca cross-cutting capability.
- ML/Transformer inference için supporting domain; generic ML research specialization değil.
- Open Source / engineering practice / projects / capstones route boyunca artan professional evidence layer.
- Security/reliability ve gerekli math/numerical capability hidden prerequisite bırakılamaz.
- Tool/vendor adı stable systems concept'in yerine geçmez.
- Domain-level ilişkiler authoring guidance; runtime hard prerequisite Skill→Skill PRG-v0.

### D-050 — Living memory sync + stale-reference audit
Canonical: `docs/PROJECT_MEMORY_PROTOCOL.md`.

- Her numaralı adım sonunda yaşayan state dosyaları istisnasız kontrol edilir.
- `PROJECT_CONTEXT.md` kısa current snapshot olarak eski step'te bırakılamaz.
- `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG` ve `DECISIONS` mandatory POST-check setindedir.
- Her step kapanışında stale active-step, old stage number, deleted/renamed file ve superseded decision/model referansları repo-wide taranır.
- Stable specs active step'i kopyalamaz; yalnız davranış/cross-reference değişirse güncellenir.

### D-051 — KGC-v0
5B final graph contract `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` içinde versioned curriculum identity, Topic↔Skill placement, Skill prerequisite, Objective evidence profile, scope-relative requirement, retention/remediation/English/professional attribution ve conservative graph migration semantics'ini kilitledi.

### D-052 — FBB-v0
5C final V1 foundation backbone `docs/V1_FOUNDATION_BACKBONE.md` içinde zero-entry bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English seed subgraph'ını tanımladı. 8–12 hafta calendar gate değildir; seed IDs 6A/6C ratification öncesi learner-published değildir.

### D-053 — GQA-v0
5D final graph architecture QA `docs/GRAPH_ARCHITECTURE_QA.md` içinde FBB seed graph'ı cycle/dead-end/hidden prerequisite/duplicate/reuse/English-global-gate/reachability açısından doğruladı; blocking structural sorunları corrective patch ile düzeltti ve AŞAMA 5'i kapattı.

### D-054 — GNS-v0
6A final `docs/GRANULARITY_NAMING_STANDARD.md` standardı Domain/Module/Topic/Skill/Objective semantic sınırlarını, Skill atomization testini, under/over-fragmentation guard'larını, shared-vs-specific capability split'ini, stable logical ID convention'ını ve FBB seed ratification/refactor lifecycle'ını kilitledi.

### D-055 — Local manager takeover
Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. GitHub durable source of truth, D-024/D-027/D-050 PRE/POST protokolü ve Research/Coding/Test bağımsızlığı değişmez. Canonical bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`. Bu transition kendi başına 6B'yi yürütmedi; sonraki kullanıcı onaylı numbered work normal protokolle ilerledi.

### D-056 — FRDB-v0
6B final `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md` contract'ı 23 route family'yi 6C–6F package'larına atadı; ortak machine-readable authoring collections, duplicate/reuse, prerequisite, FBB mapping, source/freshness, review queue ve QA sözleşmesini kilitledi.

### D-057 — FDM-v0
6C final `docs/FOUNDATIONS_DETAILED_MAP.md` + `curriculum/decomposition/6c_foundations/` package'ı D01–D05'i 132 Skill / 137 Objective seviyesine ayırdı; FBB 41/47 mapping complete, hard graph DAG ve internal QA PASS.

### D-058 — SDM-v0
6D final `docs/SYSTEMS_DETAILED_MAP.md` + `curriculum/decomposition/6d_systems/` package'ı D06–D13'ü 192 Skill / 207 Objective seviyesine ayırdı; 43 accepted 6C Skill clone'lanmadan reuse edildi, 6C+6D birleşik hard graph DAG 324/324 ve internal QA PASS.

### D-059 — GIM-v0
6E final `docs/GPU_ML_INFERENCE_DETAILED_MAP.md` + `curriculum/decomposition/6e_gpu_ml_inference/` package'ı D14–D22'yi 143 Skill / 159 Objective seviyesine ayırdı; 59 prior Skill clone'lanmadan reuse edildi, FRDB hard/soft Pass-B audit PASS ve 6C+6D+6E combined hard graph DAG 467/467.

### D-060 — PEM-v0
6F final `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md` + `curriculum/decomposition/6f_professional_engineering/` package'ı D23 professional engineering/OSS/project-capstone layer'ını 76 Skill / 87 Objective seviyesine ayırdı; prior technical capability'ler clone edilmeden reuse edildi ve 6D/6E professional overlay review'ları kapatıldı.

## 4. Güncel stage mapping
- 1 Product framing ✅
- 2 Learning/mastery ✅
- 3 Adaptive planner ✅
- 4 Assessment system ✅
- 5 Curriculum/knowledge graph backbone ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- 6 Granular Capability Map — **aktif**
  - 6A ✅ GNS-v0 / D-054
  - 6B ✅ FRDB-v0 / D-056
  - 6C ✅ FDM-v0 / D-057
  - 6D ✅ SDM-v0 / D-058
  - 6E ✅ GIM-v0 / D-059
  - 6F ✅ PEM-v0 / D-060
  - 6G ✅ WLRM-v0 / D-061
  - 6H 🟡 Coverage / prerequisite / Research QA
- 7 English parallel line
- 8 UX
- 9 Architecture/data model
- 10 Mobile skeleton
- 11 Daily learning MVP
- 12 Mastery/planner implementation
- 13 Assessment/retention/remediation implementation
- 14 AI Tutor/evaluation
- 15 First 8–12 week production content
- 16 Analytics/settings
- 17 Polish/accessibility
- 18 Pilot/calibration/QA
- 19 Release APK
- 20 Full professional curriculum/career/capstones

## 5. Zorunlu GitHub beyin tazeleme protokolü
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

> Hiçbir numaralı adım PRE-STEP GitHub refresh yapılmadan başlatılmaz; hiçbir adım D-050 living-memory kontrolü + repo-wide stale-reference scan tamamlanmadan kapanmış sayılmaz.

Minimum PRE-STEP:
1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. `PROJECT_CONTEXT.md`
7. başlanacak adımla ilgili en güncel spec/davranış dosyaları

POST-STEP ALWAYS-CHECK:
1. ana spec/çıktı
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/HANDOFF_STATE.md`
5. `docs/PROGRESS_LOG.md`
6. `docs/MASTER_PLAN.md`
7. `PROJECT_CONTEXT.md`
8. `docs/START_HERE.md`
9. `docs/DECISIONS.md`
10. repo-wide stale-reference scan

README ve `PROJECT_MASTER_CONTEXT` volatile aktif step taşımaz; yalnız kendi rolü gerçekten etkilenirse güncellenir.

## 6. Yeni sohbet/agent okuma sırası
> D-044 öncesi bir stable/historical belgede eski future-stage numarası görülürse, current execution'ı değiştirmeden önce `docs/STAGE_REINDEX_MAP.md` ile karşılığı doğrulanır.

0. `AGENTS.md`
0a. `docs/LOCAL_MANAGER_HANDOFF.md`
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `PROJECT_CONTEXT.md`
4. `docs/HANDOFF_STATE.md`
5. `docs/EXECUTION_INDEX.md`
6. `docs/STEP_STATUS.md`
7. `docs/DECISIONS.md`
8. `docs/PRODUCT_REQUIREMENTS.md`
9. `docs/PROFESSIONAL_READINESS_TARGET.md`
10. `docs/CURRICULUM_DOMAIN_MAP.md`
11. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`
11a. `docs/V1_FOUNDATION_BACKBONE.md`
11b. `docs/GRAPH_ARCHITECTURE_QA.md`
11c. `docs/GRANULARITY_NAMING_STANDARD.md`
11d. `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md`
11e. `docs/FOUNDATIONS_DETAILED_MAP.md`
11f. `curriculum/decomposition/6c_foundations/manifest.yaml`
11g. `docs/SYSTEMS_DETAILED_MAP.md`
11h. `curriculum/decomposition/6d_systems/manifest.yaml`
11i. `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`
11j. `curriculum/decomposition/6e_gpu_ml_inference/manifest.yaml`
11k. `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`
11l. `curriculum/decomposition/6f_professional_engineering/manifest.yaml`
11m. `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`
11n. `curriculum/decomposition/6g_weakness_remediation/manifest.yaml`
11o. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
12. `docs/LEARNING_ENGINE_SPEC.md`
13. `docs/LEARNING_BEHAVIOR_RULES.md`
14. `docs/MASTERY_SIGNALS_SPEC.md`
15. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
16. `docs/MASTERY_FORMULA_V0.md`
17. `docs/RETENTION_FORGETTING_SPEC.md`
18. `docs/ADAPTIVE_PLANNER_SPEC.md`
19. `docs/TASK_TAXONOMY_SPEC.md`
20. `docs/PRIORITY_POLICY_SPEC.md`
21. `docs/PREREQUISITE_POLICY_SPEC.md`
22. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
23. `docs/WEEKLY_ASSESSMENT_SPEC.md`
24. `docs/MONTHLY_ASSESSMENT_SPEC.md`
25. `docs/QUESTION_BANK_SPEC.md`
26. `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md`
27. `docs/ENGLISH_FOUNDATION_RULES.md`
28. `docs/MASTER_PLAN.md`
29. `docs/PROGRESS_LOG.md`

`docs/LEARNING_ENGINE.md` yalnız historical/superseded pointer'dır; canonical learning-engine kaynağı değildir. `docs/ENGLISH_TRACK.md` yalnız non-canonical seed notes'tur.

## 7. Ana kariyer/öğrenme yönü
**Technical English (parallel) → Python → C → Linux + Git + Shell → Data Structures & Algorithms foundations → Modern C++ → Computer Architecture → Operating Systems + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases foundations → Containers / Cloud / Observability → Performance Engineering & Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM / SGLang / TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

Bu lineer takvim değildir. PDM-v0 high-level boundaries'i, KGC-v0 graph contract'ı ve AŞAMA 6 granular Skill prerequisites gerçek executable route'u belirleyecek.

## 8. Tamamlanan çekirdek

### AŞAMA 1 ✅
Product framing tamamlandı.

### AŞAMA 2 ✅
GRE-v0 + RVR-v0 dahil learning/mastery modeli tamamlandı.

### AŞAMA 3 ✅
Adaptive planner tamamlandı: 16/16 scenarios, 20/20 invariants PASS.

### AŞAMA 4 ✅
DMA-v0 + WBA-v0 + MCA-v0 + QAB-v0 + AIV-v0 tamamlandı.

### AŞAMA 5 ✅
- 5A ✅ `PDM-v0 — Professional Domain Backbone` / D-049
- 5B ✅ `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` / D-051
- 5C ✅ `FBB-v0 — V1 Foundation Backbone` / D-052
- 5D ✅ `GQA-v0 — Foundation Graph Architecture QA` / D-053

### AŞAMA 6 ilerlemesi
- 6A ✅ `GNS-v0 — Granularity & Naming Standard` / D-054
- 6B ✅ `FRDB-v0 — Full-Route Decomposition Blueprint` / D-056
- 6C ✅ `FDM-v0 — Foundations Detailed Map` / D-057
- 6D ✅ `SDM-v0 — Systems Detailed Map` / D-058
- 6E ✅ `GIM-v0 — GPU / ML / Inference Detailed Map` / D-059
- 6F ✅ `PEM-v0 — Professional Engineering / Projects Detailed Map` / D-060
- 6G ✅ `WLRM-v0 — Weakness Localization & Remediation Map` / D-061
- 6H 🟡 Coverage / prerequisite / Research QA — aktif, henüz yürütülmedi

## 9. Güncel çalışma konumu

**Aktif:** **`6H — Coverage / prerequisite / Research QA`**
**6H henüz yürütülmedi. Independent external Research AI zorunludur.**

6F PEM-v0 / D-060 ile D23 professional engineering / OSS / project-capstone detailed map'ini tamamladı; D01–D22 accepted Skills canonical ID ile reuse edildi ve 6D/6E professional-overlay review'ları resolved edildi.

6H başlamadan fresh PRE-STEP GitHub refresh ve bağımsız Research AI workflow'u zorunludur.

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041, D-042, D-044–D-061 kararlarını oku; D-043 geri çekilmiştir. PROJECT_CONTEXT, CURRICULUM_DOMAIN_MAP, CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT, GRANULARITY_NAMING_STANDARD, FULL_ROUTE_DECOMPOSITION_BLUEPRINT, FOUNDATIONS_DETAILED_MAP, SYSTEMS_DETAILED_MAP, GPU_ML_INFERENCE_DETAILED_MAP, PROFESSIONAL_ENGINEERING_DETAILED_MAP ve 6c–6f decomposition manifest/skills/prerequisites, HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. Şu an aktif adım 6H — Coverage / prerequisite / Research QA; 6H henüz yürütülmedi ve independent external Research AI zorunludur.`
