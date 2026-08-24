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

POST-STEP: ana spec + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN`; yeni karar varsa `DECISIONS`.

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
25. `docs/ENGLISH_FOUNDATION_RULES.md`
26. `docs/MASTER_PLAN.md`
27. `docs/AI_AGENT_WORKFLOW.md`
28. `docs/PROGRESS_LOG.md`

## 4. Ana kariyer/öğrenme yönü
**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

English teknik eğitimle paralel ilerler; doğrudan CUDA ile başlanmaz.

## 5. Tamamlanan öğrenme/mastery omurgası — AŞAMA 2 ✅

### GRE-v0 — Gated Recent Evidence — D-031
- Canonical mastery Skill seviyesinde.
- Yalnız valid + prerequisite-valid + H0 + direct + verified + independent evidence mastery'ye girer.

### RVR-v0 — Retention Verification & Risk — D-032
- Mastery/retention ayrı.
- Time-based mastery decay yok.
- `review_due` forgetting değildir.
- First failure → verification; strict natural reuse; no backlog dump.

## 6. Adaptive Planner ilerlemesi

### 3A ✅ Günlük kapasite — D-033
Explicit günlük süre hard budget; no auto-overrun; split/defer; task debt yok.

### 3B ✅ Task taxonomy — D-034
`State → LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`; purpose/activity/track/evidence ayrı.

### 3C ✅ Priority — PBR-v0 / D-035
Eligibility priority'den önce; P0–P4 semantic bands; deterministic rank vector; starvation/track balance; duration priority'den sonra.

### 3D ✅ Prerequisite — PRG-v0 / D-036
Skill-level hard/soft prerequisite, review_due no-lock, branch-local blocking ve contamination guard.

### 3E ✅ Hızlı öğrenme — VDW-v0 / D-037
Diagnostic GRE-v0'dan daha kolay değildir; Objective-level validated partial/full coverage waiver ve false-skip guards.

### 3F ✅ Kaçırılan günler — SRR-v0 / D-038
Absence failure/debt değildir; stale plan replay edilmez; current-state re-entry, due inventory ≠ DailyPlan, starvation ≠ absence.

### 3G ✅ Açıklanabilir planner — PDT-v0 / D-039
- Planner karar sırasında structured `PlannerDecisionTrace` üretir.
- Need-level ve candidate-level decision trace ayrıdır.
- Selected/blocked/invalid/deferred/split/smaller-alternative/superseded disposition'ları explicit'tir.
- User-facing açıklama internal trace'teki gerçek nedenlerden türetilir.
- Private chain-of-thought saklanmaz/gösterilmez.
- LLM yalnız trace'i paraphrase edebilir; template fallback vardır.
- PRG eligibility → PBR priority → capacity fit sırası korunur.
- Review_due forgetting değildir; absence debt/failure değildir.
- Replan version/event chain completed evidence'ı korur.
- 3A–3G deterministic end-to-end pseudocode ve 3H invariant set'i vardır.

Ana çıktı: `docs/PLANNER_EXPLAINABILITY_SPEC.md`.

## 7. Güncel çalışma konumu

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** devam ediyor

- `3A` ✅
- `3B` ✅
- `3C` ✅
- `3D` ✅
- `3E` ✅
- `3F` ✅
- `3G` ✅
- `3H` 🟡 **Planner simülasyonu — AKTİF**

## 8. 3H'de yapılacaklar

Ana soru:
> 3A–3G'de tasarlanan planner gerçek ve zor kullanıcı senaryolarında kendi kurallarını bozmadan çalışıyor mu?

Doğrulanacak:
- normal yeni öğrenme,
- remediation ve verification,
- critical prerequisite blocking,
- retention review_due,
- düşük/yüksek günlük kapasite,
- higher-priority task capacity'ye sığmama,
- diagnostic partial skip,
- uzun absence/re-entry,
- paused continuation,
- session ortasında süre değişmesi,
- yeni evidence sonrası replan,
- same input determinism,
- user-facing reason ile internal trace tutarlılığı,
- D-028 bounded/performance expectation,
- 3A–3G invariant suite.

3H başlamadan yeni PRE-STEP GitHub refresh zorunlu. 3H sonucunda tüm kritik senaryolar PASS ise **AŞAMA 3 kapatılır ve 4A aktif edilir**; FAIL varsa ilgili planner spec'i düzeltilip senaryo yeniden çalıştırılır.

## 9. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md ile başla. HANDOFF_STATE.md, EXECUTION_INDEX.md, STEP_STATUS.md ve MASTER_PLAN.md üzerinden aktif adımı doğrula. Her numaralı adımda PRE-STEP GitHub refresh ve POST-STEP GitHub + MASTER_PLAN sync yap. Aşama 2 GRE-v0/RVR-v0 ve 3A–3G D-033–D-039 kararlarını koru. Şu an aktif adım 3H — Planner simülasyonu.`
