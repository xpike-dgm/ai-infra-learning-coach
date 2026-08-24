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
Ana çıktı: `docs/PREREQUISITE_POLICY_SPEC.md`.

Canonical davranış:
- runtime prerequisite `Skill → Skill`,
- edge `hard | soft`,
- readiness `ready | ready_due | uncertain | not_ready`,
- `review_due` = `ready_due`, hard lock değildir,
- hard `not_ready` dependent candidate'ı bloke eder,
- critical/strict `verification_due` dependent yeni work'u verification çözülene kadar bekletebilir,
- normal uncertain dependency conditional eligibility olabilir; tüm curriculum durmaz,
- task-level `required_skill_ids` exact candidate hard requirement'tır,
- priority prerequisite'i bypass edemez,
- yalnız affected branch bekler; independent branches devam eder,
- started/mastered Topic prerequisite regression ile `locked` olmaz,
- prerequisite contamination target negative evidence değildir,
- missing prerequisite repair/review/verification need olarak planner'a geri beslenir,
- Technical English gerçek dependency değilse global technical blocker değildir,
- deterministic/bounded `PrerequisiteDecision` output'u vardır.

## 5. Güncel kesin konum

**AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla**

- `3A` ✅
- `3B` ✅
- `3C` ✅
- `3D` ✅
- `3E` 🟡 **Hızlı öğrenme — AKTİF**
- `3F–3H` ⬜ Bekliyor

## 6. 3E'de kesinleştirilecekler
- kullanıcı bir Topic/Skill'i zaten biliyorsa bunu nasıl güvenilir diagnostic ile gösterecek,
- `available → mastered` validated diagnostic yolu,
- coverage waiver / skip semantics,
- tek kolay quiz ile skip yasağı,
- partial diagnostic sonucu ve yalnız bilinen Objective'lerin atlanması,
- critical Skill için güçlü independent evidence,
- diagnostic assistance/provenance,
- false-positive skip guard,
- diagnostic sonucu GRE-v0 + PRG-v0 + planner replan entegrasyonu.

3E için araştırma gereksinimi PRE-STEP sonrası değerlendirilmeli; özellikle diagnostic/placement mastery konusunda dış learning-science evidence gerekiyorsa Research AI kullanılabilir.

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
16. `docs/PREREQUISITE_POLICY_SPEC.md`
17. `docs/ENGLISH_FOUNDATION_RULES.md`
18. `docs/MASTER_PLAN.md`
19. `docs/PROGRESS_LOG.md`

## 8. Yeni sohbetin ilk işi
Repo üzerinden aktif adımı doğrula ve **3E — Hızlı öğrenme** için yeni PRE-STEP GitHub refresh yap. 3A D-033, 3B D-034, 3C D-035, 3D D-036 ve Aşama 2 GRE/RVR kararlarını kullanıcı açıkça değiştirmedikçe yeniden açma.
