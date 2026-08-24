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
- split/defer,
- deferred task debt değildir.

### 3B ✅ Task taxonomy — D-034
- `LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent`,
- purpose/activity/track/evidence ayrı,
- unresolved need kalıcı; old task ID debt değildir.

### 3C ✅ Priority — PBR-v0 / D-035
- eligibility priority'den önce,
- P0–P4 semantic bands,
- deterministic rank vector,
- starvation/track-balance guard,
- duration semantic priority'den sonra.

### 3D ✅ Prerequisite — PRG-v0 / D-036
- runtime prerequisite `Skill → Skill`,
- `hard | soft`,
- readiness `ready | ready_due | uncertain | not_ready`,
- `review_due` hard lock değildir,
- yalnız affected dependent branch bekler,
- prerequisite contamination target negative evidence değildir.

### 3E ✅ Hızlı öğrenme — VDW-v0 / D-037
- diagnostic GRE-v0'dan daha gevşek mastery yolu değildir,
- Objective-level validated coverage waiver + partial waiver,
- critical H0/evidence/prerequisite guard korunur,
- waiver mastery/retention değildir.

### 3F ✅ Kaçırılan günler — SRR-v0 / D-038
Ana çıktı: `docs/MISSED_DAY_RECOVERY_SPEC.md`.

Canonical davranış:
- absence failure, mastery decay veya task debt değildir,
- stale PlannedTask/TaskCandidate replay edilmez,
- current state'ten fresh LearningNeed/candidate üretilir,
- RVR time yalnız due/urgency'yi etkiler; review_due tek başına forgetting değildir,
- unresolved verification/remediation absence ile silinmez,
- safe paused checkpoint continuation adayı olabilir ama otomatik seçilmez,
- incomplete high-stakes H0 attempt negative evidence değildir; gerekiyorsa fresh candidate gelir,
- due-state inventory DailyPlan değildir; yüzlerce review tek güne yığılmaz,
- 1/7/30/60+ gün için ayrı pedagojik threshold yoktur,
- absence günleri starvation sayılmaz; overdue ayrı zaman sinyalidir,
- integrated recovery evidence ayrı observability/attribution ister; cluster refresh yoktur,
- recovery PBR-v0 + PRG-v0 + 3A hard capacity ile çalışır,
- long absence new learning'i globally dondurmaz,
- self-report automatic retention refresh değildir,
- deterministic/bounded implementation beklenir.

## 5. Güncel kesin konum

**AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla**

- `3A` ✅
- `3B` ✅
- `3C` ✅
- `3D` ✅
- `3E` ✅
- `3F` ✅
- `3G` 🟡 **Açıklanabilir planner — AKTİF**
- `3H` ⬜ Bekliyor

## 6. 3G'de kesinleştirilecekler
- machine-readable reason codes,
- selected/skipped/blocked/deferred decision trace,
- PBR/PRG/RVR/capacity/diagnostic/re-entry girdilerinin tek açıklanabilir modelde birleşmesi,
- user-facing kısa açıklamalar ile internal audit trace ayrımı,
- `neden bugün bu görev?`, `neden diğer görev gelmedi?`, `neden branch bekliyor?` cevapları,
- replan reason chain,
- deterministic end-to-end planner pseudocode,
- 3H simulation'ın doğrulayacağı trace/invariant beklentileri.

3G başlamadan yeni PRE-STEP GitHub refresh zorunlu.

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
20. `docs/ENGLISH_FOUNDATION_RULES.md`
21. `docs/MASTER_PLAN.md`
22. `docs/PROGRESS_LOG.md`

## 8. Yeni sohbetin ilk işi
Repo üzerinden aktif adımı doğrula ve **3G — Açıklanabilir planner** için yeni PRE-STEP GitHub refresh yap. 3A–3F kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.
