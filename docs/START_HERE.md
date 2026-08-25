# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

## 1. Bu repo ne için var?
Tek kullanıcı için geliştirilecek kişisel adaptif mobil öğrenme uygulamasının ürün hafızasını, kararlarını, curriculum yönünü ve geliştirme planını kalıcı tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Sistem sabit kurs takvimi değil; gerçek Skill state, prerequisite, retention, evidence ve günlük capacity'ye göre plan üretir.

## 2. Güncel uzun vadeli hedef — D-041

> **Sıfırdan başlayan kullanıcıyı, gerektiğinde 4+ yıl veya daha uzun sürebilecek mastery-gated bir rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability seviyesine taşımak.**

4+ yıl countdown değildir. Professional readiness; mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ile belirlenir.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

V1 ayrımı korunur: full 4+ year curriculum V1 ön koşulu değildir. V1 learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.

## 3. Güncel rota/plan kararları

### D-042 — Python foundation
Python common technical foundation'a resmi olarak eklendi. C/C++ yerine geçmez; automation/testing/benchmark, ML/PyTorch ve infra tooling için tamamlayıcı ana dildir.

### D-043 — geri çekildi
Önceki standalone specialization-stage kararı kullanıcı talebinin yanlış yorumuydu. Canonical değildir.

### D-044 — Granular Capability Map
Yeni **AŞAMA 6**, ana öğrenme rotasındaki her büyük alanı:

`Domain → Module → Topic → Skill → Learning Objective`

seviyesinde kapsamlı alt bölümlere ayıracaktır.

Amaç `Python zayıf` gibi kaba bir sonuç yerine örneğin `Python → Control Flow → Loops → while termination` seviyesinde zayıflığı bulmak ve yalnız ilgili capability için reteach/practice/retest üretmektir.

Canonical charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

### D-045 — Weekly Blueprint Assessment
4B final model:

> **`WBA-v0 — Weekly Blueprint Assessment`**

Weekly assessment:
- tek overall score/pass-fail değildir,
- item seçilmeden önce state-temelli blueprint üretir,
- recent progress, weakness/verification, critical prerequisite, retention, integration/transfer ve gerektiğinde English role'larını kullanır; fixed quota değildir,
- fixed soru sayısı/süre kullanmaz,
- daily hard budget dışında otomatik süre yaratmaz,
- split/pause/resume olabilir; incomplete/missed exam failure/debt değildir,
- Objective-level evidence'ı GRE/RVR/PRG/planner'a bağlar,
- broad `Python failed` gibi state yazmaz; D-044 granular weakness localization'ı korur.

Canonical: `docs/WEEKLY_ASSESSMENT_SPEC.md`.

## 4. Güncel stage mapping
- 1–5 değişmedi.
- **6 = Granular Capability Map**
- 7 = English parallel line
- 8 = UX
- 9 = Architecture/data model
- 10 = Mobile skeleton
- 11 = Daily learning MVP
- 12 = Mastery/planner implementation
- 13 = Assessment/retention/remediation implementation
- 14 = AI Tutor/evaluation
- 15 = First 8–12 week production content
- 16 = Analytics/settings
- 17 = Polish/accessibility
- 18 = Pilot/calibration/QA
- 19 = Release APK
- 20 = Full professional curriculum/career/capstones

Tamamlanmış 1–4 kodları değişmedi. Old mistaken specialization AŞAMA 20 kaldırıldı.

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
10. `docs/PROJECT_MASTER_CONTEXT.md`
11. `docs/V1_SCOPE.md`
12. `docs/V1_SUCCESS_CRITERIA.md`
13. `docs/NON_GOALS.md`
14. `docs/LEARNING_ENGINE_SPEC.md`
15. `docs/LEARNING_BEHAVIOR_RULES.md`
16. `docs/TOPIC_STATE_MACHINE.md`
17. `docs/MASTERY_SIGNALS_SPEC.md`
18. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
19. `docs/MASTERY_FORMULA_V0.md`
20. `docs/RETENTION_FORGETTING_SPEC.md`
21. `docs/ADAPTIVE_PLANNER_SPEC.md`
22. `docs/TASK_TAXONOMY_SPEC.md`
23. `docs/PRIORITY_POLICY_SPEC.md`
24. `docs/PREREQUISITE_POLICY_SPEC.md`
25. `docs/DIAGNOSTIC_WAIVER_SPEC.md`
26. `docs/MISSED_DAY_RECOVERY_SPEC.md`
27. `docs/PLANNER_EXPLAINABILITY_SPEC.md`
28. `docs/PLANNER_SIMULATION_SUITE.md`
29. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
30. `docs/WEEKLY_ASSESSMENT_SPEC.md`
31. `docs/ENGLISH_FOUNDATION_RULES.md`
32. `docs/MASTER_PLAN.md`
33. `docs/AI_AGENT_WORKFLOW.md`
34. `docs/PROGRESS_LOG.md`

## 7. Ana kariyer/öğrenme yönü

**Technical English (parallel) → Python → C → Linux + Git + Shell → Data Structures & Algorithms foundations → Modern C++ → Computer Architecture → Operating Systems + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases foundations → Containers / Cloud / Observability → Performance Engineering & Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM / SGLang / TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

AŞAMA 6 bu listenin her maddesini detaylı capability haritasına bölecek.

## 8. Tamamlanan öğrenme/mastery omurgası — AŞAMA 2 ✅

### GRE-v0 — D-031
- Canonical mastery Skill seviyesinde.
- Yalnız valid + prerequisite-valid + H0 + direct + verified + independent evidence mastery'ye girer.

### RVR-v0 — D-032
- Mastery/retention ayrı.
- Time-based mastery decay yok.
- `review_due` forgetting değildir.
- First clean contradiction → verification; no backlog dump.

D-044 bu modeli değiştirmez; yalnız gerçek curriculum'un Skill/Objective granularity'sini kapsamlı hale getirir.

## 9. Adaptive Planner — AŞAMA 3 ✅
- 3A D-033 — hard daily capacity / no task debt.
- 3B D-034 — LearningNeed / TaskCandidate / Evidence ayrımı.
- 3C PBR-v0 / D-035 — semantic priority bands + deterministic rank.
- 3D PRG-v0 / D-036 — hard/soft Skill prerequisites; branch-local blocking.
- 3E VDW-v0 / D-037 — validated Objective-level diagnostic waiver.
- 3F SRR-v0 / D-038 — current-state re-entry; no absence debt.
- 3G PDT-v0 / D-039 — structured planner decision trace.
- 3H planner simulation PASS: 16/16 scenarios, 20/20 invariants, 0 critical contradiction.

## 10. Assessment — AŞAMA 4 ilerlemesi

### 4A ✅ DMA-v0 — Daily Micro Assessment — D-040
Ana çıktı: `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`.

### 4B ✅ WBA-v0 — Weekly Blueprint Assessment — D-045
Ana çıktı: `docs/WEEKLY_ASSESSMENT_SPEC.md`.

## 11. Güncel çalışma konumu

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** devam ediyor

- `4A` ✅
- `4B` ✅
- `4C` 🟡 **Aylık yeterlilik sınavı — AKTİF**
- `4D–4E` ⬜ bekliyor
- `5–20` ⬜ bekliyor

## 12. 4C'de yapılacaklar
Ana soru:
> Aylık assessment, WBA-v0'ın sağlayamadığı daha geniş transfer/integration ve critical revalidation evidence'ını nasıl toplamalı; bunu tek final score'a dönüştürmeden uzun dönem capability state'ine nasıl bağlamalı?

Kesinleştirilecek:
- monthly purpose/scope ve daily/weekly farkı,
- WBA-v0 common `AssessmentBlueprint / AssessmentBlueprintSlot / AssessmentSessionResult` contract'ının monthly specialization'ı,
- daha geniş transfer/integration,
- critical prerequisite/capability revalidation,
- older/retention + recent progress dengesi,
- professional-readiness'e doğru evidence aggregation ama final readiness ile karıştırmama,
- fixed score ile mastery vermeme,
- capacity / split / pause / incomplete,
- H0/H1–H4 assistance,
- invalid/ambiguous/provisional item güvenliği,
- D-044 granular Skill/Objective localization,
- result → GRE/RVR/remediation/PRG/planner,
- 4D Question Bank handoff.

4C başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 13. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041 professional target, D-042 Python foundation, D-044 Granular Capability Map ve D-045 WBA-v0 kararlarını oku; D-043 geri çekilmiştir. HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. Şu an aktif adım 4C — Aylık yeterlilik sınavı; 4C henüz yürütülmedi.`
