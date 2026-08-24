# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

## 1. Bu repo ne için var?
Tek kullanıcı için geliştirilecek kişisel adaptif mobil öğrenme uygulamasının ürün hafızasını, kararlarını, curriculum yönünü ve geliştirme planını kalıcı tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Sistem sabit kurs takvimi değil; gerçek Skill state, prerequisite, retention, evidence ve günlük capacity'ye göre plan üretir.

## 2. Zorunlu GitHub beyin tazeleme protokolü
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

## 3. Yeni sohbet/agent okuma sırası
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/PRODUCT_REQUIREMENTS.md`
8. `docs/V1_SCOPE.md`
9. `docs/V1_SUCCESS_CRITERIA.md`
10. `docs/NON_GOALS.md`
11. `docs/LEARNING_ENGINE_SPEC.md`
12. `docs/LEARNING_BEHAVIOR_RULES.md`
13. `docs/TOPIC_STATE_MACHINE.md`
14. `docs/MASTERY_SIGNALS_SPEC.md`
15. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
16. `docs/MASTERY_FORMULA_V0.md`
17. `docs/RETENTION_FORGETTING_SPEC.md`
18. `docs/ADAPTIVE_PLANNER_SPEC.md`
19. `docs/TASK_TAXONOMY_SPEC.md`
20. `docs/PRIORITY_POLICY_SPEC.md`
21. `docs/PREREQUISITE_POLICY_SPEC.md`
22. `docs/DIAGNOSTIC_WAIVER_SPEC.md`
23. `docs/MISSED_DAY_RECOVERY_SPEC.md`
24. `docs/PLANNER_EXPLAINABILITY_SPEC.md`
25. `docs/PLANNER_SIMULATION_SUITE.md`
26. `docs/ENGLISH_FOUNDATION_RULES.md`
27. `docs/MASTER_PLAN.md`
28. `docs/AI_AGENT_WORKFLOW.md`
29. `docs/PROGRESS_LOG.md`

## 4. Ana kariyer/öğrenme yönü
**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

English teknik eğitimle paralel ilerler; doğrudan CUDA ile başlanmaz.

## 5. Tamamlanan öğrenme/mastery omurgası — AŞAMA 2 ✅

### GRE-v0 — D-031
- Canonical mastery Skill seviyesinde.
- Yalnız valid + prerequisite-valid + H0 + direct + verified + independent evidence mastery'ye girer.

### RVR-v0 — D-032
- Mastery/retention ayrı.
- Time-based mastery decay yok.
- `review_due` forgetting değildir.
- First clean contradiction → verification; no backlog dump.

## 6. Adaptive Planner — AŞAMA 3 ✅

### 3A — D-033
Hard daily capacity; no auto-overrun; split/defer; task debt yok.

### 3B — D-034
`LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`.

### 3C — PBR-v0 / D-035
P0–P4 semantic bands + deterministic rank; eligibility priority'den önce; duration priority'den sonra.

### 3D — PRG-v0 / D-036
Hard/soft Skill prerequisite; `ready | ready_due | uncertain | not_ready`; branch-local blocking; review_due no-lock.

### 3E — VDW-v0 / D-037
Diagnostic GRE-v0'dan daha kolay değildir; Objective-level validated partial/full waiver.

### 3F — SRR-v0 / D-038
Absence failure/debt değildir; stale plan replay edilmez; current-state re-entry; due inventory ≠ DailyPlan.

### 3G — PDT-v0 / D-039
Structured PlannerDecisionTrace; user-facing reason internal trace'ten türetilir; deterministic pseudocode; LLM source of truth değildir.

### 3H — Planner simulation ✅
Ana çıktı: `docs/PLANNER_SIMULATION_SUITE.md`.

```text
16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical cross-spec contradiction
```

Bu policy/spec-level PASS'tir. Production runtime testleri 11F ve gerçek cihaz/performance 17E'de ayrıca yapılacaktır.

## 7. Güncel çalışma konumu

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** devam ediyor

- `4A` 🟡 **Günlük mikro değerlendirme — AKTİF**
- `4B–4E` ⬜ bekliyor

## 8. 4A'da yapılacaklar

Ana soru:
> Günlük öğrenme akışında kullanıcıyı gereksiz sınava boğmadan, hangi Skill/Objective'leri ne zaman ve nasıl güvenilir biçimde ölçeceğiz?

Kesinleştirilecek:
- micro-assessment purpose/scope,
- teach/practice/assessment ayrımı,
- target Skill/Objective seçimi,
- capacity-aware kompozisyon,
- sabit bilimsel soru sayısı/dakika optimumu uydurmama,
- GRE-v0 evidence gates,
- H0/H1–H4 assistance/provenance,
- PRG prerequisite/contamination guard,
- retention/remediation/replan bağlantısı,
- low-capacity gün davranışı,
- invalid/ambiguous item güvenliği,
- result contract'ın 4B–4E'ye taşınması.

4A başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 9. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md ile başla. HANDOFF_STATE.md, EXECUTION_INDEX.md, STEP_STATUS.md ve MASTER_PLAN.md üzerinden aktif adımı doğrula. Her numaralı adımda PRE-STEP GitHub refresh ve POST-STEP GitHub + MASTER_PLAN sync yap. Aşama 2 GRE-v0/RVR-v0 ve Aşama 3 D-033–D-039 + PLANNER_SIMULATION_SUITE PASS kararlarını koru. Şu an aktif adım 4A — Günlük mikro değerlendirme.`