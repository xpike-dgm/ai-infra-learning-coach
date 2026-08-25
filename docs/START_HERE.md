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

Canonical: `docs/WEEKLY_ASSESSMENT_SPEC.md`.

### D-046 — MCA-v0
Monthly assessment longitudinal state-based capability sampling; broader transfer/integration; no cumulative final/pass score.

Canonical: `docs/MONTHLY_ASSESSMENT_SPEC.md`.

### D-047 — QAB-v0
Question Bank yalnız MCQ değil, versioned AssessmentResource bank'idir. Stable logical ID + immutable version, lifecycle/use ceiling, exact target/prerequisite/evidence metadata, family/dependency/context/freshness ve bounded selection vardır.

Canonical: `docs/QUESTION_BANK_SPEC.md`.

### D-048 — AIV-v0
Final 4E model:

> **`AIV-v0 — AI Assessment Resource Validation`**

- AI-generated resource `candidate` başlar; generator output validation proof değildir.
- Minimum correctness/safety validation geçmeden user-facing selection'a çıkmaz.
- Schema, technical correctness, answer/rubric, ambiguity, target/evidence fit, prerequisites, English leakage, duplicate/family/dependency/context/transfer, evaluator/tool/artifact, freshness ve execution-safety ayrı validate edilir.
- Weighted confidence/majority-vote pass yoktur; final use ceiling en kısıtlayıcı check'e bağlıdır.
- Use ceilings: `practice_only < low_stakes_assessment < standard_mastery_eligible < critical_mastery_eligible`.
- Tek uncalibrated LLM critical verified evidence için yeterli değildir.
- Near duplicate yeni independent family sayılmaz.
- Trusted-template inheritance yalnız validated invariants korunuyorsa mümkündür.
- Validator disagreement fail-safe olarak promotion'ı durdurur.
- Confirmed content bug historical evidence review/repair açabilir; learner cezalandırılmaz.

Canonical: `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md`.

## 4. Güncel stage mapping
- 1 Product framing
- 2 Learning/mastery
- 3 Adaptive planner
- 4 Assessment system ✅ tamamlandı
- 5 Curriculum/knowledge graph backbone — **aktif**
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

> Hiçbir numaralı adım PRE-STEP GitHub refresh yapılmadan başlatılmaz; hiçbir adım ana çıktı ve canonical state dosyaları + `MASTER_PLAN.md` senkronize edilmeden tamamlanmış sayılmaz.

Minimum PRE-STEP:
1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. başlanacak adımla ilgili en güncel spec/davranış dosyaları

POST-STEP: ana spec + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN`; yeni kalıcı karar varsa `DECISIONS`.

## 6. Yeni sohbet/agent okuma sırası
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/PRODUCT_REQUIREMENTS.md`
8. `docs/PROFESSIONAL_READINESS_TARGET.md`
9. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
10. `docs/LEARNING_ENGINE_SPEC.md`
11. `docs/LEARNING_BEHAVIOR_RULES.md`
12. `docs/MASTERY_SIGNALS_SPEC.md`
13. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
14. `docs/MASTERY_FORMULA_V0.md`
15. `docs/RETENTION_FORGETTING_SPEC.md`
16. `docs/ADAPTIVE_PLANNER_SPEC.md`
17. `docs/TASK_TAXONOMY_SPEC.md`
18. `docs/PRIORITY_POLICY_SPEC.md`
19. `docs/PREREQUISITE_POLICY_SPEC.md`
20. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
21. `docs/WEEKLY_ASSESSMENT_SPEC.md`
22. `docs/MONTHLY_ASSESSMENT_SPEC.md`
23. `docs/QUESTION_BANK_SPEC.md`
24. `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md`
25. `docs/ENGLISH_FOUNDATION_RULES.md`
26. `docs/MASTER_PLAN.md`
27. `docs/PROGRESS_LOG.md`

## 7. Ana kariyer/öğrenme yönü
**Technical English (parallel) → Python → C → Linux + Git + Shell → Data Structures & Algorithms foundations → Modern C++ → Computer Architecture → Operating Systems + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases foundations → Containers / Cloud / Observability → Performance Engineering & Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM / SGLang / TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

AŞAMA 5 bu rotanın domain/backbone sınırlarını, AŞAMA 6 ise her domain'in granular capability haritasını kuracak.

## 8. Tamamlanan çekirdek

### AŞAMA 1 ✅
Product framing tamamlandı.

### AŞAMA 2 ✅
GRE-v0 + RVR-v0 dahil learning/mastery modeli tamamlandı.

### AŞAMA 3 ✅
Adaptive planner tamamlandı: 16/16 scenarios, 20/20 invariants PASS.

### AŞAMA 4 ✅
- 4A DMA-v0
- 4B WBA-v0
- 4C MCA-v0
- 4D QAB-v0
- 4E AIV-v0

Assessment foundation tamamlandı: blueprint-based assessment + trusted resource bank + AI-generated content validation + canonical evidence pipeline.

## 9. Güncel çalışma konumu

**Aktif:** **`5A — Ana domain haritası`**  
**5A henüz yürütülmedi.**

5A'da 4+ yıllık professional-readiness rotasının domain-level knowledge/curriculum backbone'u kurulacak; common foundation, parallel Technical English, systems/distributed/performance/GPU/inference/AI infrastructure ve professional engineering/project katmanlarının sınırları ile ana prerequisite ilişkileri belirlenecek.

5A ayrıntılı Python `for/while` veya CUDA alt kavramlarını yazmayacak; bu AŞAMA 6'nın granular decomposition işidir.

5A başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041, D-042, D-044, D-045, D-046, D-047 ve D-048 kararlarını oku; D-043 geri çekilmiştir. HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. AŞAMA 4 tamamlandı. Şu an aktif adım 5A — Ana domain haritası; 5A henüz yürütülmedi.`
