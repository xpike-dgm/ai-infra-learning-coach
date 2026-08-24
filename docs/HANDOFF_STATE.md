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
- Final hedef yalnız course completion değil, professional-readiness seviyesinde verified engineering capability.
- Final readiness; mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ister.
- V1 full curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.
- Product job offer/salary/seniority veya üniversite/HR filtresi garantisi vermez; gerçek ekip/production deneyimi ayrıca oluşur.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## 3. Güncel rota/plan kararları — D-042 / D-044

### D-042 — Python resmi foundation
- Python C/C++'ın yerine geçmez.
- Automation, testing, benchmark scripting, ML/PyTorch ve infra tooling için common core'a eklendi.
- İleri Python kapsamı typing, testing, async/concurrency, multiprocessing, networking, profiling, packaging ve infra/ML kullanımını kapsayacak.

### D-043 — geri çekildi
Önceki “sona standalone specialization-track aşaması ekleme” yorumu kullanıcının talebini yanlış anlamıştır. Canonical yürütme planından çıkarıldı.

### D-044 — AŞAMA 6 Granular Capability Map
Asıl ihtiyaç, ana rotadaki bütün büyük alanları ayrıntılı öğrenme/ölçüm parçalarına bölmektir.

Canonical yapı:
`Domain → Module → Topic → Skill → Learning Objective`

Amaç:
- `Python zayıf` gibi geniş bir tanı yerine,
- `Python → Control Flow → Loops → while termination` gibi,
- ayrı mastery/evidence/remediation uygulanabilen zayıflık konumu üretmek.

AŞAMA 6:
- Python dahil bütün rotayı alt kavramlara böler,
- prerequisites ve evidence requirement'larını bağlar,
- cross-domain duplicate Skill'leri önler,
- weakness → reteach/practice/retest mapping'i tasarlar,
- kapsam ve hidden prerequisite için bağımsız Research AI QA içerir.

Canonical charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

## 4. Plan reindex sonucu
- AŞAMA 1–5 aynı kaldı.
- Yeni **AŞAMA 6 — Granular Capability Map** eklendi.
- Eski English 6 → yeni 7.
- Eski UX 7 → yeni 8.
- Eski Architecture 8 → yeni 9.
- Eski Skeleton 9 → yeni 10.
- Eski Daily MVP 10 → yeni 11.
- Eski Mastery/Planner implementation 11 → yeni 12.
- Eski Assessment implementation 12 → yeni 13.
- Eski AI Tutor 13 → yeni 14.
- Eski first content 14 → yeni 15.
- Eski analytics 15 → yeni 16.
- Eski polish 16 → yeni 17.
- Eski pilot 17 → yeni 18.
- Eski release 18 → yeni 19.
- Eski long professional curriculum 19 → yeni 20.
- Yanlış eski specialization AŞAMA 20 kaldırıldı.

Tamamlanmış 1–4 kodları değişmedi.

## 5. Bağlayıcı ana kurallar
- Curriculum takvim değil prerequisite graph.
- Canonical mastery/prerequisite seviyesi Skill; evidence Objective'e bağlanabilir.
- Domain/Module/Topic broad progress summary olabilir; gerçek weakness/remediation mümkün olduğunca Skill/Objective seviyesinde lokalize edilir.
- Coverage/time/streak/task completion mastery değildir.
- Öğretilmemiş prerequisite yüzünden kullanıcı başarısız sayılmaz.
- Coding mastery gerçek user-authored artifact ister.
- AI yardımı serbest; assisted performance independent mastery değildir.
- Tek yeni yanlış mastered Skill'i anında silmez.
- English paralel gider; global technical blocker değildir.
- Core mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
- D-028: bounded/incremental hesap, async ağır işler, gerçek cihaz performance QA.
- D-041: professional readiness takvim değil evidence ile belirlenir.

## 6. Tamamlanan aşamalar

### AŞAMA 1 ✅
`1A–1D` tamamlandı.

### AŞAMA 2 ✅ — Öğrenme/Mastery
- GRE-v0 — D-031
- RVR-v0 — D-032
- D-044 granularity clarification 2A ile uyumludur; aşama yeniden açılmadı.

### AŞAMA 3 ✅ — Adaptive Planner
- 3A D-033
- 3B D-034
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
- 4B 🟡 Haftalık sınav — aktif, henüz yürütülmedi

## 7. Güncel kesin konum

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** devam ediyor

- `4A` ✅
- `4B` 🟡 **Haftalık sınav — AKTİF**
- `4C–4E` ⬜ bekliyor
- `5–20` ⬜ bekliyor

D-044 plan correction 4B execution değildir. 4B başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 8. 4B'de kesinleştirilecekler
Ana soru:
> Haftalık sınav daily micro assessment'ın sağlayamadığı hangi daha geniş evidence'ı sağlamalı ve çok sayıda Skill/Objective'i adil bir blueprint ile nasıl ölçmeli?

Kesinleştirilecek:
- weekly purpose/scope,
- DMA-v0'dan farkı,
- required/critical Skill/Objective coverage,
- multi-Skill blueprint,
- modality/family/context diversity,
- weakness + recent progress + prerequisite risk dengesi,
- fixed sahte optimum olmadan composition,
- capacity/pause/incomplete,
- H0/H1–H4 assistance,
- invalid/ambiguous/provisional item safety,
- result → GRE/RVR/remediation/PRG/planner,
- tek sınava aşırı tepki vermeyen hysteresis,
- future AŞAMA 6 granular IDs ile uyum,
- 4C ortak blueprint/result contract.

## 9. İlk okuma sırası
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/PRODUCT_REQUIREMENTS.md`
8. `docs/PROFESSIONAL_READINESS_TARGET.md`
9. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
10. `docs/PROJECT_MASTER_CONTEXT.md`
11. `docs/V1_SCOPE.md`
12. `docs/LEARNING_ENGINE_SPEC.md`
13. `docs/LEARNING_BEHAVIOR_RULES.md`
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
29. `docs/PROGRESS_LOG.md`

## 10. Yeni sohbetin ilk işi
Repo üzerinden D-041/D-042/D-044 ve aktif adımı doğrula. D-043'ü canonical kabul etme; geri çekilmiştir. Ardından **4B — Haftalık sınav** için yeni PRE-STEP GitHub refresh yap.
