# START HERE — Yeni Sohbet / Yeni Agent İçin Başlangıç Noktası

Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.

## 1. Bu repo ne için var?
Tek kullanıcı için geliştirilecek kişisel adaptif mobil öğrenme uygulamasının ürün hafızasını, kararlarını, curriculum yönünü ve geliştirme planını kalıcı tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Sistem sabit kurs takvimi değil; gerçek Skill state, prerequisite, retention, evidence ve günlük capacity'ye göre plan üretir.

## 2. Güncel uzun vadeli hedef — D-041

2026-08-24 kapsam genişletmesiyle ürünün nihai curriculum hedefi büyütüldü.

> **Sıfırdan başlayan kullanıcıyı, gerektiğinde 4+ yıl veya daha uzun sürebilecek mastery-gated bir rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability seviyesine taşımak.**

4+ yıl bir countdown değildir. Professional readiness; mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ile belirlenir.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

V1 ayrımı korunur: full 4+ year curriculum V1 ön koşulu değildir. V1 learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.

## 3. Zorunlu GitHub beyin tazeleme protokolü
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

## 4. Yeni sohbet/agent okuma sırası
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/PRODUCT_REQUIREMENTS.md`
8. `docs/PROFESSIONAL_READINESS_TARGET.md`
9. `docs/PROJECT_MASTER_CONTEXT.md`
10. `docs/V1_SCOPE.md`
11. `docs/V1_SUCCESS_CRITERIA.md`
12. `docs/NON_GOALS.md`
13. `docs/LEARNING_ENGINE_SPEC.md`
14. `docs/LEARNING_BEHAVIOR_RULES.md`
15. `docs/TOPIC_STATE_MACHINE.md`
16. `docs/MASTERY_SIGNALS_SPEC.md`
17. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
18. `docs/MASTERY_FORMULA_V0.md`
19. `docs/RETENTION_FORGETTING_SPEC.md`
20. `docs/ADAPTIVE_PLANNER_SPEC.md`
21. `docs/TASK_TAXONOMY_SPEC.md`
22. `docs/PRIORITY_POLICY_SPEC.md`
23. `docs/PREREQUISITE_POLICY_SPEC.md`
24. `docs/DIAGNOSTIC_WAIVER_SPEC.md`
25. `docs/MISSED_DAY_RECOVERY_SPEC.md`
26. `docs/PLANNER_EXPLAINABILITY_SPEC.md`
27. `docs/PLANNER_SIMULATION_SUITE.md`
28. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
29. `docs/ENGLISH_FOUNDATION_RULES.md`
30. `docs/MASTER_PLAN.md`
31. `docs/AI_AGENT_WORKFLOW.md`
32. `docs/PROGRESS_LOG.md`

## 5. Ana kariyer/öğrenme yönü
**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

D-041 ile rota derinliği ayrıca computer architecture, infra-relevant DS&A/storage/cloud/observability, performance engineering, transformer/inference fundamentals, serving engine internals, multi-GPU, professional tooling, open source ve capstone katmanlarını kapsayacak şekilde genişletildi.

English teknik eğitimle paralel ilerler; doğrudan CUDA ile başlanmaz.

## 6. Tamamlanan öğrenme/mastery omurgası — AŞAMA 2 ✅

### GRE-v0 — D-031
- Canonical mastery Skill seviyesinde.
- Yalnız valid + prerequisite-valid + H0 + direct + verified + independent evidence mastery'ye girer.

### RVR-v0 — D-032
- Mastery/retention ayrı.
- Time-based mastery decay yok.
- `review_due` forgetting değildir.
- First clean contradiction → verification; no backlog dump.

## 7. Adaptive Planner — AŞAMA 3 ✅

- 3A D-033 — hard daily capacity / no task debt.
- 3B D-034 — LearningNeed / TaskCandidate / Evidence ayrımı.
- 3C PBR-v0 / D-035 — semantic priority bands + deterministic rank.
- 3D PRG-v0 / D-036 — hard/soft Skill prerequisites; branch-local blocking.
- 3E VDW-v0 / D-037 — validated Objective-level diagnostic waiver.
- 3F SRR-v0 / D-038 — current-state re-entry; no absence debt.
- 3G PDT-v0 / D-039 — structured planner decision trace.
- 3H planner simulation PASS: 16/16 scenarios, 20/20 invariants, 0 critical contradiction.

## 8. Assessment — AŞAMA 4 ilerlemesi

### 4A ✅ DMA-v0 — Daily Micro Assessment — D-040
Ana çıktı: `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`.

- Günlük assessment zorunlu quiz/kota değildir.
- Fixed soru sayısı/süre/yüzde yok.
- `practice / assess / retain / diagnose` purpose'ları ayrı.
- Assessment existing LearningNeed/evidence gap'ten doğar.
- H0 independent measurement mastery/verification için varsayılandır.
- Assistance/provenance/prerequisite/invalid item guards korunur.
- Coding/debugging/transfer evidence standardı kısa süre nedeniyle düşmez.
- Assessment sonucu canonical evidence/state/replan pipeline'ına girer.

## 9. Güncel çalışma konumu

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** devam ediyor

- `4A` ✅
- `4B` 🟡 **Haftalık sınav — AKTİF**
- `4C–4E` ⬜ bekliyor

D-041 scope sync tamamlandı; **4B henüz yürütülmedi**.

## 10. 4B'de yapılacaklar

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
- D-041 professional-readiness hedefiyle ileride integrated/capstone evidence'a uyum,
- 4C monthly assessment için ortak blueprint/result contract.

4B başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 11. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda START_HERE ve PROJECT_MEMORY_PROTOCOL ile başla. D-041 / PROFESSIONAL_READINESS_TARGET.md dahil current scope'u oku. HANDOFF_STATE, EXECUTION_INDEX, STEP_STATUS ve MASTER_PLAN üzerinden aktif adımı doğrula. Her numaralı adımda PRE-STEP GitHub refresh ve POST-STEP GitHub + MASTER_PLAN sync yap. Şu an aktif adım 4B — Haftalık sınav; 4B henüz yürütülmedi.`
