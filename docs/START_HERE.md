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
22. `docs/ENGLISH_FOUNDATION_RULES.md`
23. `docs/MASTER_PLAN.md`
24. `docs/AI_AGENT_WORKFLOW.md`
25. `docs/PROGRESS_LOG.md`

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
- Runtime prerequisite `Skill → Skill`.
- Hard/soft edge ayrımı.
- Readiness `ready | ready_due | uncertain | not_ready`.
- `review_due` hard lock değildir.
- Hard `not_ready` dependent task'i bloklar.
- Critical/strict `verification_due` dependent yeni work'u bekletebilir.
- Yalnız affected branch bekler; independent branches devam eder.
- Started Topic prerequisite regression ile `locked` olmaz.
- Prerequisite contamination target negative evidence değildir.
- Priority prerequisite'i bypass edemez.

Ana çıktı: `docs/PREREQUISITE_POLICY_SPEC.md`.

## 7. Güncel çalışma konumu

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** devam ediyor

- `3A` ✅
- `3B` ✅
- `3C` ✅
- `3D` ✅
- `3E` 🟡 **Hızlı öğrenme — AKTİF**
- `3F–3H` ⬜ bekliyor

## 8. 3E'de yapılacaklar

Ana soru:
> Kullanıcı bir Skill/Topic'i zaten biliyorsa gereksiz dersi tekrar etmeden bunu nasıl güvenilir biçimde kanıtlayıp geçebilir?

Kesinleştirilecek:
- diagnostic/placement task yapısı,
- validated coverage waiver / skip semantics,
- `available → mastered` güvenilir diagnostic yolu,
- tek kolay quiz ile skip yasağı,
- partial diagnostic: yalnız kanıtlanan Objective/Skill kısımlarının waive edilmesi,
- critical Skill diagnostic için stronger evidence,
- diagnostic assistance/provenance ve H0 gereksinimi,
- false-positive skip guard,
- diagnostic sonucu GRE-v0/PRG-v0/planner replan entegrasyonu.

3E başlamadan yeni PRE-STEP GitHub refresh zorunlu. Diagnostic/placement konusunda dış pedagojik kanıt gerekiyorsa Research AI kullanımı PRE-STEP sonrası değerlendirilecek.

## 9. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda docs/START_HERE.md ve docs/PROJECT_MEMORY_PROTOCOL.md ile başla. HANDOFF_STATE.md, EXECUTION_INDEX.md, STEP_STATUS.md ve MASTER_PLAN.md üzerinden aktif adımı doğrula. Her numaralı adımda PRE-STEP GitHub refresh ve POST-STEP GitHub + MASTER_PLAN sync yap. Aşama 2 GRE-v0/RVR-v0 ile 3A D-033, 3B D-034, 3C D-035 ve 3D PRG-v0/D-036 kararlarını koru. Şu an aktif adım 3E — Hızlı öğrenme.`
