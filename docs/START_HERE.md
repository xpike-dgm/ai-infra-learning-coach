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

V1 full 4+ year curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.

## 3. Güncel rota/plan kararları

### D-042 — Python foundation
Python common technical foundation'a resmi olarak eklendi. C/C++ yerine geçmez; automation/testing/benchmark, ML/PyTorch ve infra tooling için tamamlayıcı ana dildir.

### D-043 — geri çekildi
Önceki standalone specialization-stage kararı kullanıcı talebinin yanlış yorumuydu. Canonical değildir.

### D-044 — Granular Capability Map
Yeni **AŞAMA 6**, ana öğrenme rotasındaki her büyük alanı `Domain → Module → Topic → Skill → Learning Objective` seviyesinde kapsamlı alt bölümlere ayıracaktır. Amaç broad `Python zayıf` yerine exact Skill/Objective weakness/remediation üretmektir.

Canonical: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

### D-045 — Weekly Blueprint Assessment
`WBA-v0`: item'dan önce state-temelli blueprint, multi-Skill evidence ama granular attribution, fixed score/time/quota yok, incomplete/missed exam debt değil.

Canonical: `docs/WEEKLY_ASSESSMENT_SPEC.md`.

### D-046 — Monthly Capability Assessment
`MCA-v0`: longitudinal state-based sampling, daha geniş transfer/integration, critical revalidation ve delayed retention; cumulative-everything exam veya ay sonu pass/fail değil. Professional checkpoint final professional-readiness/capstone gate değildir.

Canonical: `docs/MONTHLY_ASSESSMENT_SPEC.md`.

## 4. Güncel stage mapping
- 1 = Product framing
- 2 = Learning/mastery
- 3 = Adaptive planner
- 4 = Assessment design
- 5 = Knowledge graph backbone
- 6 = Granular Capability Map
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

## 6. Ana kariyer/öğrenme yönü

**Technical English (parallel) → Python → C → Linux + Git + Shell → Data Structures & Algorithms foundations → Modern C++ → Computer Architecture → Operating Systems + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases foundations → Containers / Cloud / Observability → Performance Engineering & Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM / SGLang / TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

AŞAMA 6 bu listenin her maddesini detaylı capability haritasına bölecek.

## 7. Tamamlanan çekirdek modeller

### AŞAMA 2 ✅
- GRE-v0 / D-031 — Gated Recent Evidence
- RVR-v0 / D-032 — Retention Verification & Risk

### AŞAMA 3 ✅
- D-033 hard daily capacity
- D-034 LearningNeed/TaskCandidate/Evidence
- PBR-v0 / D-035
- PRG-v0 / D-036
- VDW-v0 / D-037
- SRR-v0 / D-038
- PDT-v0 / D-039
- 3H: 16/16 scenarios, 20/20 invariants PASS

### AŞAMA 4 ilerlemesi
- 4A ✅ DMA-v0 / D-040
- 4B ✅ WBA-v0 / D-045
- 4C ✅ MCA-v0 / D-046
- 4D 🟡 Question Bank
- 4E ⬜ AI-generated item validation

## 8. Güncel çalışma konumu

**Aktif adım: `4D — Soru bankası`.**

4D henüz yürütülmedi. 4C POST-STEP sync tamamlandı.

4D'nin ana sorusu:
> Daily/weekly/monthly blueprint'lerin güvenilir biçimde seçebileceği, uzun curriculum boyunca versionlanıp QA edilebilecek, prerequisite-safe ve same/near-variant kaynaklı sahte evidence'ı engelleyen trusted item/task bank nasıl modellenmeli?

4D'de kilitlenecek ana alanlar:
- stable item ID/version/lifecycle,
- target Skill/Objective + prerequisite metadata,
- evidence/activity type,
- assessment scope + blueprint role eligibility,
- variant/dependency/context/transfer families,
- integrated component attribution,
- rubric/answer key/evaluator,
- allowed tools/artifact requirements,
- trust/validation/content origin,
- solution exposure/reuse/freshness,
- difficulty/complexity,
- duration/atomicity,
- language/scaffold,
- bounded selection/indexing,
- 4E AI-generated candidate validation handoff.

## 9. Yeni sohbet/agent okuma sırası
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
20. `docs/PLANNER_EXPLAINABILITY_SPEC.md`
21. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
22. `docs/WEEKLY_ASSESSMENT_SPEC.md`
23. `docs/MONTHLY_ASSESSMENT_SPEC.md`
24. `docs/ENGLISH_FOUNDATION_RULES.md`
25. `docs/MASTER_PLAN.md`
26. `docs/PROGRESS_LOG.md`

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041 professional target, D-042 Python foundation, D-044 Granular Capability Map, D-045 WBA-v0 ve D-046 MCA-v0 kararlarını oku; D-043 geri çekilmiştir. HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. Şu an aktif adım 4D — Soru bankası; 4D henüz yürütülmedi.`