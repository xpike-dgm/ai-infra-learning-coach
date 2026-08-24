# HANDOFF STATE — Güncel Proje Durumu

**Son güncelleme:** 2026-08-24  
Repo: `xpike-dgm/ai-infra-learning-coach`

## 0. Zorunlu protokol
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`, D-024, D-027.

> Her numaralı adım başlamadan PRE-STEP GitHub refresh; bittikten sonra ana çıktı + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN` ve gerekiyorsa `DECISIONS` senkronu zorunludur.

## 1. Ürün
Sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering yolunda günlük yöneten, uygulama içinde öğreten/uygulatan, yalnız kanıtlanmış öğrenmeyi ilerleme sayan adaptif Android öğrenme koçu.

Ana rota:
**Technical English + Computer Fundamentals → C → Linux → Modern C++ → OS/Memory → Concurrency → Networking → Distributed Systems → GPU Architecture → CUDA → Triton → LLM Inference → AI Infrastructure**

## 2. Bağlayıcı ana kurallar
- Curriculum takvim değil prerequisite graph.
- Canonical mastery/prerequisite seviyesi Skill; evidence Objective'e bağlanabilir.
- Coverage/time/streak/task completion mastery değildir.
- Öğretilmemiş prerequisite yüzünden kullanıcı başarısız sayılmaz.
- Coding mastery gerçek user artifact ister.
- AI yardımı serbest; assisted performance independent mastery değildir.
- Tek yeni yanlış mastered Skill'i anında silmez.
- English paralel gider; global technical blocker değildir.
- Core mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
- D-028: bounded/incremental hesap ve async ağır işler.

## 3. Tamamlanan ana aşamalar
- **AŞAMA 1** `1A–1D` ✅
- **AŞAMA 2** `2A–2F` ✅

Aşama 2 canonical omurgası:
- `GRE-v0 — Gated Recent Evidence` — D-031.
- `RVR-v0 — Retention Verification & Risk` — D-032.

## 4. AŞAMA 3 ilerlemesi

### 3A ✅ Günlük kapasite — D-033
Explicit daily time hard budget; no auto-overrun; split/defer; no task debt.

### 3B ✅ Task taxonomy — D-034
`LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`; purpose/activity/track/evidence ayrı.

### 3C ✅ Priority — PBR-v0 / D-035
P0–P4 semantic bands + deterministic rank vector; eligibility priority'den önce; starvation/track balance; duration priority'den sonra.

### 3D ✅ Prerequisite — PRG-v0 / D-036
Skill-level hard/soft edges, `ready | ready_due | uncertain | not_ready`, branch-local blocking, review_due no-lock, contamination guard.

### 3E ✅ Hızlı öğrenme — VDW-v0 / D-037
Diagnostic GRE-v0'dan daha kolay değildir; Objective-level validated coverage waiver + partial waiver; critical false-skip guards.

### 3F ✅ Kaçırılan günler — SRR-v0 / D-038
Absence failure/mastery decay/debt değildir; stale plan replay yok; current-state re-entry; due inventory ≠ DailyPlan; starvation ≠ absence.

### 3G ✅ Açıklanabilir planner — PDT-v0 / D-039
Ana çıktı: `docs/PLANNER_EXPLAINABILITY_SPEC.md`.

Canonical davranış:
- planner açıklaması sonradan serbest metinle uydurulmaz,
- `PlannerDecisionTrace` plan generation/replan sırasında structured reason code üretir,
- private chain-of-thought tutulmaz/gösterilmez; canonical state refs + policy outputs + disposition kaydedilir,
- need-level ve candidate-level trace ayrıdır,
- selected/blocked/invalid/lower-priority/capacity-deferred/split/smaller-alternative/superseded sonuçları explicit'tir,
- reason code namespaces: need, validation, eligibility, retention, priority, capacity, diagnostic, re-entry, selection, replan,
- user-facing açıklama trace'in alt kümesidir; trace'te olmayan factual neden ekleyemez,
- PRG eligibility → PBR priority → capacity fit sırası korunur,
- `review_due` forgetting/failure değildir; absence debt/failure/starvation değildir,
- higher-priority task fit olmadığı için lower-priority task seçildiyse gerçek fit nedeni açıklanır,
- replan yeni plan version/event üretir; completed evidence korunur,
- LLM yalnız reason trace'i paraphrase edebilir; template fallback zorunludur,
- 3A–3G deterministic end-to-end pseudocode tanımlıdır,
- 3H için 20 temel simulation invariant'ı hazırlanmıştır,
- trace bounded/ref-based ve D-028 ile uyumludur.

## 5. Güncel kesin konum

**AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla**

- `3A` ✅
- `3B` ✅
- `3C` ✅
- `3D` ✅
- `3E` ✅
- `3F` ✅
- `3G` ✅
- `3H` 🟡 **Planner simülasyonu — AKTİF**

## 6. 3H'de kesinleştirilecek/doğrulanacaklar
- sanal kullanıcı profilleri,
- normal progress,
- remediation ve fresh verification,
- critical prerequisite block,
- review_due/retention,
- low/high capacity,
- higher-priority task fit etmeyince lower-priority task seçimi,
- partial diagnostic fast path,
- long absence/re-entry,
- paused continuation,
- mid-session capacity change,
- new remediation/verification sonrası replan,
- same-input determinism,
- decision trace ve user explanation tutarlılığı,
- 3A–3G acceptance/invariant suite PASS/FAIL,
- gerekiyorsa spec düzeltme döngüsü,
- bütün senaryolar geçerse **AŞAMA 3 kapanışı** ve 4A aktivasyonu.

3H yeni bir pedagojik model icat etme adımı değildir; mevcut planner contract'larını simulation ile kırmaya/validate etmeye odaklanır.

## 7. İlk okuma sırası
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/LEARNING_ENGINE_SPEC.md`
8. `docs/LEARNING_BEHAVIOR_RULES.md`
9. `docs/TOPIC_STATE_MACHINE.md`
10. `docs/MASTERY_SIGNALS_SPEC.md`
11. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
12. `docs/MASTERY_FORMULA_V0.md`
13. `docs/RETENTION_FORGETTING_SPEC.md`
14. `docs/ADAPTIVE_PLANNER_SPEC.md`
15. `docs/TASK_TAXONOMY_SPEC.md`
16. `docs/PRIORITY_POLICY_SPEC.md`
17. `docs/PREREQUISITE_POLICY_SPEC.md`
18. `docs/DIAGNOSTIC_WAIVER_SPEC.md`
19. `docs/MISSED_DAY_RECOVERY_SPEC.md`
20. `docs/PLANNER_EXPLAINABILITY_SPEC.md`
21. `docs/ENGLISH_FOUNDATION_RULES.md`
22. `docs/MASTER_PLAN.md`
23. `docs/PROGRESS_LOG.md`

## 8. Yeni sohbetin ilk işi
Repo üzerinden aktif adımı doğrula ve **3H — Planner simülasyonu** için yeni PRE-STEP GitHub refresh yap. 3A–3G D-033–D-039 kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.
