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
- explicit daily time hard budget,
- no auto-overrun,
- split/smaller alternative/defer,
- deferred task debt değildir.

### 3B ✅ Task taxonomy — D-034
- `LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`,
- purpose/activity/track/evidence ayrı,
- unresolved need kalıcı; old task ID debt değildir.

### 3C ✅ Öncelik — PBR-v0 / D-035
Ana çıktı: `docs/PRIORITY_POLICY_SPEC.md`.

Canonical davranış:
- priority açık LearningNeed seviyesinde başlar,
- eligibility/trust priority'den önce gelir,
- P0 `integrity_blocker`, P1 `repair_or_verify`, P2 `maintain_or_continue`, P3 `planned_progress`, P4 `reinforce_or_optimize`,
- critical etiketi tek başına P0 yapmaz; gerçek dependency blocking gerekir,
- `review_due` forgetting değildir,
- aynı band içi lexicographic rank: blocking → criticality → evidence severity → temporal urgency → starvation → continuation → decision value → track balance → duration fit → stable tie-break,
- additive sahte-hassas puan ve `score/minute` yok,
- starvation guard eligible need'in süresiz ertelenmesini engeller,
- English/paralel track fixed yüzdeyle değil due + starvation/track balance ile korunur,
- capacity dolunca kalan need açık kalır; next-day homework debt oluşmaz,
- `PriorityDecisionTrace` reconstruct edilebilir.

## 5. Güncel kesin konum

**AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla**

- `3A` ✅
- `3B` ✅
- `3C` ✅
- `3D` 🟡 **Prerequisite davranışı — AKTİF**
- `3E–3H` ⬜ Bekliyor

## 6. 3D'de kesinleştirilecekler
- hard vs soft prerequisite edge semantics,
- candidate eligibility'nin kesin kuralı,
- critical unresolved verification/remediation nedeniyle dependent branch wait,
- `review_due` tek başına hard lock olmaması,
- yalnız bağımlı dalın beklemesi; independent branches'in devamı,
- öğretilmemiş prerequisite contamination guard,
- prerequisite state değişince replan,
- 3D eligibility filter ile 3C priority'nin kesin yürütme sırası,
- user-facing explanation girdileri; final reason code metinleri 3G.

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
11. `docs/MASTERY_FORMULA_V0.md`
12. `docs/RETENTION_FORGETTING_SPEC.md`
13. `docs/ADAPTIVE_PLANNER_SPEC.md`
14. `docs/TASK_TAXONOMY_SPEC.md`
15. `docs/PRIORITY_POLICY_SPEC.md`
16. `docs/ENGLISH_FOUNDATION_RULES.md`
17. `docs/MASTER_PLAN.md`
18. `docs/PROGRESS_LOG.md`

## 8. Yeni sohbetin ilk işi
Repo üzerinden aktif adımı doğrula ve **3D — Prerequisite davranışı** için yeni PRE-STEP GitHub refresh yap. 3A D-033, 3B D-034, 3C D-035 ve Aşama 2 GRE/RVR kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.
