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
26. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
27. `docs/ENGLISH_FOUNDATION_RULES.md`
28. `docs/MASTER_PLAN.md`
29. `docs/AI_AGENT_WORKFLOW.md`
30. `docs/PROGRESS_LOG.md`

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

- 3A D-033 — hard daily capacity / no task debt.
- 3B D-034 — LearningNeed / TaskCandidate / Evidence ayrımı.
- 3C PBR-v0 / D-035 — semantic priority bands + deterministic rank.
- 3D PRG-v0 / D-036 — hard/soft Skill prerequisites; branch-local blocking.
- 3E VDW-v0 / D-037 — validated Objective-level diagnostic waiver.
- 3F SRR-v0 / D-038 — current-state re-entry; no absence debt.
- 3G PDT-v0 / D-039 — structured planner decision trace.
- 3H planner simulation PASS: 16/16 scenarios, 20/20 invariants, 0 critical contradiction.

Bu 3H sonucu spec/policy-level PASS'tir. Runtime tests 11F ve gerçek cihaz/performance 17E'de ayrıca zorunludur.

## 7. Assessment — AŞAMA 4 ilerlemesi

### 4A ✅ DMA-v0 — Daily Micro Assessment — D-040
Ana çıktı: `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`.

- Günlük assessment zorunlu quiz/kota değildir.
- Fixed soru sayısı/süre/yüzde yok; state + PBR + hard capacity belirler.
- `practice / assess / retain / diagnose` purpose'ları ayrı.
- Assessment existing LearningNeed/evidence gap'ten doğar; assessment backlog/debt yok.
- H0 independent measurement mastery/verification için varsayılandır.
- H1–H4 yardım learning'e izin verir ama positive independent mastery değildir; yardım istemek negative evidence değildir.
- Submit sonrası feedback önceki H0 attempt'i geriye dönük kirletmez.
- Invalid/ambiguous/prerequisite-contaminated item mastery credit/penalty üretmez.
- Provisional evaluator critical mastery/remediation kararını tek başına belirleyemez.
- Tek doğru item mastery değildir; first clean post-mastery failure instant unmastery değildir.
- Coding/debugging/transfer evidence standardı kısa süre nedeniyle düşmez.
- Assessment sonucu canonical evidence/state/replan pipeline'ına girer.
- 4B–4E için minimum item/result contract + `assessment.*` reason codes vardır.

## 8. Güncel çalışma konumu

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** devam ediyor

- `4A` ✅
- `4B` 🟡 **Haftalık sınav — AKTİF**
- `4C–4E` ⬜ bekliyor

## 9. 4B'de yapılacaklar

Ana soru:
> Haftalık sınav, daily micro assessment'ın sağlayamadığı hangi daha geniş evidence'ı sağlamalı ve çok sayıda Skill/Objective'i adil bir blueprint ile nasıl ölçmeli?

Kesinleştirilecek:
- weekly assessment purpose/scope,
- DMA-v0'dan farkı,
- required/critical Skill/Objective coverage,
- multi-Skill blueprint,
- evidence modality/family/context diversity,
- current weakness + recent progress + prerequisite risk dengesi,
- fixed bilimsel soru sayısı/puan uydurmama,
- sınav süresi ve capacity ilişkisi,
- bölünebilirlik / pause / incomplete davranışı,
- H0/H1–H4 assistance ve solution exposure,
- invalid/ambiguous/provisional item güvenliği,
- weekly result → GRE/RVR/remediation/PRG/planner,
- tek sınav sonucuna aşırı tepki vermeyen hysteresis,
- 4C monthly assessment için ortak blueprint/result contract.

4B başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md ile başla. HANDOFF_STATE.md, EXECUTION_INDEX.md, STEP_STATUS.md ve MASTER_PLAN.md üzerinden aktif adımı doğrula. Her numaralı adımda PRE-STEP GitHub refresh ve POST-STEP GitHub + MASTER_PLAN sync yap. Aşama 2 GRE-v0/RVR-v0, Aşama 3 D-033–D-039 + PLANNER_SIMULATION_SUITE PASS ve 4A DMA-v0/D-040 kararlarını koru. Şu an aktif adım 4B — Haftalık sınav.`