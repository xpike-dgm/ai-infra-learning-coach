# HANDOFF STATE — Güncel Proje Durumu

**Son güncelleme:** 2026-08-25  
Repo: `xpike-dgm/ai-infra-learning-coach`

## 0. Zorunlu protokol
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`, D-024, D-027.

> Her numaralı adım başlamadan PRE-STEP GitHub refresh; bittikten sonra ana çıktı + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN` ve gerekiyorsa `DECISIONS` senkronu zorunludur.

## 1. Ürün
Tek kullanıcı için, sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering yolunda günlük yöneten, uygulama içinde öğreten/uygulatan ve yalnız kanıtlanmış öğrenmeyi ilerleme sayan adaptif Android öğrenme koçu.

Güncel ana rota:
**Technical English (parallel) → Python → C → Linux + Git + Shell → DS&A foundations → Modern C++ → Computer Architecture → OS + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases → Containers / Cloud / Observability → Performance Engineering / Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM/SGLang/TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

## 2. Uzun vadeli hedef — D-041
- Full curriculum **4+ yıl veya daha uzun** sürebilir.
- 4+ yıl countdown/mezuniyet garantisi değildir.
- Final hedef professional-readiness seviyesinde verified engineering capability.
- Final readiness; mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ister.
- V1 full curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.
- Product job offer/salary/seniority veya üniversite/HR filtresi garantisi vermez.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## 3. Güncel rota/plan kararları

### D-042 — Python resmi foundation
Python common core'a resmi olarak eklendi; C/C++ yerine geçmez.

### D-043 — geri çekildi
Standalone specialization-stage yorumu kullanıcı talebini yanlış anlamıştır; canonical değildir.

### D-044 — AŞAMA 6 Granular Capability Map
Ana rotadaki her büyük alan `Domain → Module → Topic → Skill → Learning Objective` seviyesine ayrılacak.
Amaç `Python zayıf` yerine `Python → Control Flow → Loops → while termination` gibi hedefli weakness/mastery/remediation.
Canonical charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

### D-045 — WBA-v0 Weekly Blueprint Assessment
Haftalık assessment:
- tek overall score/pass-fail değildir,
- item'lardan önce state-temelli blueprint üretir,
- recent progress + weakness/verification + critical prerequisite + retention + integration/transfer + gerektiğinde English role'larını kullanır; fixed quota değildir,
- fixed soru sayısı/süre kullanmaz,
- daily hard capacity dışına otomatik taşmaz; safe split/pause/resume mümkündür,
- incomplete/missed exam failure/debt/stack değildir,
- H0/assistance/provenance/prerequisite/evaluator safety kurallarını DMA-v0'dan miras alır,
- raw exam sonucu değil Objective-level EvidenceEvent'ler GRE/RVR/PRG/planner'ı değiştirir,
- broad Domain fail/pass yazmaz; D-044 granular localization'ı korur.

Canonical: `docs/WEEKLY_ASSESSMENT_SPEC.md`.

## 4. Tamamlanan aşamalar

### AŞAMA 1 ✅
`1A–1D` tamamlandı.

### AŞAMA 2 ✅ — Öğrenme/Mastery
- GRE-v0 — D-031
- RVR-v0 — D-032
- D-044 granularity clarification uyumlu; aşama yeniden açılmadı.

### AŞAMA 3 ✅ — Adaptive Planner
- 3A D-033 — hard daily capacity / no task debt
- 3B D-034 — LearningNeed / TaskCandidate / Evidence
- 3C PBR-v0 / D-035
- 3D PRG-v0 / D-036
- 3E VDW-v0 / D-037
- 3F SRR-v0 / D-038
- 3G PDT-v0 / D-039
- 3H planner simulation PASS

```text
16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical cross-spec contradiction
```

### AŞAMA 4 ilerlemesi
- 4A ✅ DMA-v0 / D-040
- 4B ✅ WBA-v0 / D-045
- 4C 🟡 Aylık yeterlilik sınavı — aktif, henüz yürütülmedi
- 4D–4E ⬜ bekliyor

## 5. 4B final özeti

WBA-v0 weekly session bir **blueprint-based evidence bundle**'dır.

Canonical flow:

```text
weekly cycle + current state
→ weekly blueprint
→ bounded slots
→ validated/prerequisite-valid items
→ capacity-aware blocks
→ Attempt/Artifact
→ EvidenceEvent
→ GRE/RVR/verification/weakness/remediation
→ PRG/Topic
→ replan
```

Critical guards:
- weekly scope evidence'a ekstra ağırlık vermez,
- one score mastery yazmaz,
- same/near variant diversity şişiremez,
- integrated task global pass'i sibling Skill'lere yayamaz,
- first clean post-mastery failure instant unmastery değildir,
- invalid/prerequisite-contaminated/provisional item güvenli biçimde sınırlandırılır,
- H1–H4 positive independent mastery değildir,
- missed/incomplete weekly exam failure/debt değildir.

4B'de ayrı Research AI kullanılmadı; calibrated psychometric optimum uydurulmadı. Empirik assessment süre/UX ve false-positive/false-negative calibration AŞAMA 18 pilotuna bırakıldı.

## 6. Güncel kesin konum

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** devam ediyor

- `4A` ✅
- `4B` ✅
- `4C` 🟡 **Aylık yeterlilik sınavı — AKTİF**
- `4D–4E` ⬜ bekliyor
- `5–20` ⬜ bekliyor

## 7. 4C'de kesinleştirilecekler
Ana soru:
> Aylık assessment, WBA-v0'ın sağlayamadığı daha geniş transfer/integration ve critical revalidation evidence'ını nasıl toplamalı; bunu tek final score'a dönüştürmeden uzun dönem capability state'ine nasıl bağlamalı?

Kesinleştirilecek:
- monthly purpose/scope ve daily/weekly farkı,
- WBA-v0 common `AssessmentBlueprint / Slot / SessionResult` contract'ının monthly specialization'ı,
- daha geniş transfer/integration,
- critical prerequisite/capability revalidation,
- older/retention evidence ile recent progress dengesi,
- professional-readiness'e doğru evidence aggregation ama final readiness ile karıştırmama,
- capacity/split/pause/incomplete,
- H0/H1–H4,
- invalid/ambiguous/provisional item safety,
- D-044 granular Skill/Objective localization,
- result → GRE/RVR/remediation/PRG/planner,
- 4D Question Bank schema için handoff.

## 8. İlk okuma sırası
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/PRODUCT_REQUIREMENTS.md`
8. `docs/PROFESSIONAL_READINESS_TARGET.md`
9. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
10. `docs/LEARNING_ENGINE_SPEC.md`
11. `docs/LEARNING_BEHAVIOR_RULES.md`
12. `docs/MASTERY_SIGNALS_SPEC.md`
13. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
14. `docs/MASTERY_FORMULA_V0.md`
15. `docs/RETENTION_FORGETTING_SPEC.md`
16. `docs/ADAPTIVE_PLANNER_SPEC.md`
17. `docs/TASK_TAXONOMY_SPEC.md`
18. `docs/PRIORITY_POLICY_SPEC.md`
19. `docs/PREREQUISITE_POLICY_SPEC.md`
20. `docs/PLANNER_EXPLAINABILITY_SPEC.md`
21. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
22. `docs/WEEKLY_ASSESSMENT_SPEC.md`
23. `docs/ENGLISH_FOUNDATION_RULES.md`
24. `docs/MASTER_PLAN.md`
25. `docs/PROGRESS_LOG.md`

## 9. Yeni sohbetin ilk işi
Repo üzerinden D-041/D-042/D-044/D-045 ve aktif adımı doğrula. D-043'ü canonical kabul etme. Ardından **4C — Aylık yeterlilik sınavı** için yeni PRE-STEP GitHub refresh yap.
