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
19. `docs/MASTER_PLAN.md`
20. `docs/AI_AGENT_WORKFLOW.md`
21. `docs/PROGRESS_LOG.md`

## 4. Ana kariyer/öğrenme yönü
**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

English teknik eğitimle paralel ilerler; doğrudan CUDA ile başlanmaz.

## 5. Tamamlanan öğrenme/mastery omurgası — AŞAMA 2 ✅

### GRE-v0 — Gated Recent Evidence
- Canonical mastery Skill seviyesinde; evidence Learning Objective'e bağlanır.
- Yalnız valid + prerequisite-valid + H0 + direct + verified + independent evidence mastery score'a girer.
- Hard gates, diversity, H0 coding/debugging, verification_due.
- Bounded recent evidence; threshold/window heuristics kalibre edilir.

Ayrıntı: `docs/MASTERY_FORMULA_V0.md` — D-031.

### RVR-v0 — Retention Verification & Risk
- Mastery ve retention ayrı eksen.
- Time-based mastery decay yok.
- `review_due` forgetting değildir.
- Skill-type appropriate H0 delayed verification.
- First failure → verification; strict natural reuse; no auto cluster refresh; no backlog dump.

Ayrıntı: `docs/RETENTION_FORGETTING_SPEC.md` — D-032.

## 6. Adaptive Planner ilerlemesi

### 3A ✅ Günlük kapasite
`docs/ADAPTIVE_PLANNER_SPEC.md` — D-033.

- Explicit daily minutes = hard budget.
- Today override en yüksek source priority.
- Editable short/normal/intensive V0 presetleri `30/60/90 dk`.
- `10%` planning reserve + `10 dk` minimum plannable block V0 heuristic.
- Fixed category yüzdeleri yok.
- Remediation/retention planı otomatik uzatmaz; remaining plan replan edilir.
- Session time override desteklenir.
- Unfinished plan failure değildir.
- Safe split / smaller alternative / defer.
- Deferred task debt/backlog değildir.
- Duration estimates user pace'e adapte olabilir.
- Capacity deterministic/versioned; LLM süreyi keyfi değiştiremez.

## 7. Güncel çalışma konumu

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** devam ediyor

- `3A` ✅ Günlük kapasite
- `3B` 🟡 **Görev kategorileri — AKTİF**
- `3C–3H` ⬜ bekliyor

## 8. 3B'de yapılacaklar
3B başlamadan yeni PRE-STEP GitHub refresh zorunlu.

Kesinleştirilecek:
- canonical task taxonomy,
- teaching / practice / assessment / coding / debugging / retention / remediation / English / project ilişkisi,
- task category ile evidence type'ın ayrı olması,
- `TaskCandidate` contract,
- target Skill/Objective, prerequisites, duration, splittable, provenance, reason metadata,
- multi-Skill/integrated task attribution,
- 3C priority motorunun tüketebileceği deterministic task primitive.

## 9. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md ile başla. HANDOFF_STATE.md, EXECUTION_INDEX.md, STEP_STATUS.md ve MASTER_PLAN.md üzerinden aktif adımı doğrula. Her numaralı adımda PRE-STEP GitHub refresh ve POST-STEP GitHub + MASTER_PLAN sync yap. Aşama 2 GRE-v0/RVR-v0 ve Aşama 3A D-033 capacity contract kararlarını koru. Şu an aktif adım 3B — Görev kategorileri.`
