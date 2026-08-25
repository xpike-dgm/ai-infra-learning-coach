# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

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

## 4. Güncel stage mapping
- 1 Product framing ✅
- 2 Learning/mastery ✅
- 3 Adaptive planner ✅
- 4 Assessment system ✅
- 5 Curriculum/knowledge graph backbone — **aktif**
  - 5A ✅ PDM-v0
  - 5B 🟡 Graph / Topic metadata sözleşmesi
  - 5C–5D ⬜
- 6 Granular Capability Map
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
11. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
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

Bu lineer takvim değildir. PDM-v0 high-level boundaries'i, 5B graph contract'ı ve AŞAMA 6 granular Skill prerequisites gerçek executable route'u belirleyecek.

## 8. Tamamlanan çekirdek

### AŞAMA 1 ✅
Product framing tamamlandı.

### AŞAMA 2 ✅
GRE-v0 + RVR-v0 dahil learning/mastery modeli tamamlandı.

### AŞAMA 3 ✅
Adaptive planner tamamlandı: 16/16 scenarios, 20/20 invariants PASS.

### AŞAMA 4 ✅
DMA-v0 + WBA-v0 + MCA-v0 + QAB-v0 + AIV-v0 tamamlandı.

### AŞAMA 5 ilerlemesi
- 5A ✅ `PDM-v0 — Professional Domain Backbone` / D-049
- 5B 🟡 Graph / Topic metadata sözleşmesi

## 9. Güncel çalışma konumu

**Aktif:** **`5B — Graph / Topic metadata sözleşmesi`**  
**5B henüz yürütülmedi.**

5B'de PDM-v0 domain backbone'u gerçek versioned knowledge graph contract'ına çevrilecek: Domain/Module/Topic/Skill/Objective identities, many-to-many placement, Skill prerequisites, evidence/retention/remediation metadata, cross-domain reuse, professional/project attribution, source/freshness ve graph versioning.

5B başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041, D-042, D-044–D-050 kararlarını oku; D-043 geri çekilmiştir. PROJECT_CONTEXT, CURRICULUM_DOMAIN_MAP, HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. Şu an aktif adım 5B — Graph / Topic metadata sözleşmesi; 5B henüz yürütülmedi.`