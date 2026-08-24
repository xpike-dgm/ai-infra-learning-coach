# HANDOFF STATE — Güncel Proje Durumu

**Son güncelleme:** 2026-08-25  
Repo: `xpike-dgm/ai-infra-learning-coach`

## 0. Zorunlu protokol
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`, D-024, D-027.

> Her numaralı adım başlamadan PRE-STEP GitHub refresh; bittikten sonra ana çıktı + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN` ve gerekiyorsa `DECISIONS` senkronu zorunludur.

## 1. Ürün
Tek kullanıcı için, sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering yolunda günlük yöneten, uygulama içinde öğreten/uygulatan ve yalnız kanıtlanmış öğrenmeyi ilerleme sayan adaptif Android öğrenme koçu.

Güncel ana rota:
**Technical English + Computer Fundamentals → Python + C → Linux/Tooling → Modern C++ → DS&A → Computer Architecture → OS/Memory → Concurrency/Parallelism → Networking → Distributed Systems/Storage → Cloud/Observability → Performance Engineering → GPU Architecture → CUDA → Triton → ML/Transformer foundations → LLM Inference → Multi-GPU → AI Infrastructure / ML Systems / GPU Systems → specialization tracks**

## 2. Uzun vadeli hedef — D-041

- Nihai curriculum yaklaşık 3 yıllık horizon ile sınırlı değil; **4+ yıl veya daha uzun** sürebilir.
- 4+ yıl countdown/mezuniyet garantisi değildir.
- Final hedef yalnız course completion değil, **professional-readiness seviyesinde verified engineering capability**.
- Final readiness; required Skill mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ister.
- V1 4+ yıl beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.
- Product job offer/salary/seniority veya üniversite/HR filtresi garantisi vermez; gerçek ekip/production deneyimi ayrıca oluşur.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`, D-041.

## 3. Yeni rota kararları — D-042 / D-043

### D-042 — Python resmi foundation
- Python C/C++'ın yerine geçmez.
- Automation, testing, benchmark scripting, data/ML ekosistemi ve infra tooling için common core'a eklendi.
- İleri Python kapsamı typing, testing, async/concurrency, multiprocessing, networking, profiling, packaging ve ML/infra kullanımını kapsayacak.

### D-043 — AŞAMA 20 / specialization tracks
Mevcut 1–19 aşamaları bozulmadan sona yeni aşama eklendi:

**AŞAMA 20 — Uzmanlık Dallarına Ayrılma ve Track Sistemi**

İlk candidate track aileleri:
- GPU Kernel & Performance Engineering
- LLM Inference / Serving Systems
- Distributed AI Infrastructure / Cluster & Scheduling
- High-Speed Networking & Multi-GPU Systems
- ML Compilers / Runtime Systems
- AI Platform / Reliability / Capacity Engineering

Track seçimi kullanıcı tercihi + verified capability + prerequisite readiness + gerçek kariyer kısıtları ile yapılacak; LLM/maaş/hype tek başına seçim yapmayacak. Track değişimi geçmiş valid mastery/evidence'ı silmeyecek.

## 4. Bağlayıcı ana kurallar
- Curriculum takvim değil prerequisite graph.
- Canonical mastery/prerequisite seviyesi Skill; evidence Objective'e bağlanabilir.
- Coverage/time/streak/task completion mastery değildir.
- Öğretilmemiş prerequisite yüzünden kullanıcı başarısız sayılmaz.
- Coding mastery gerçek user-authored artifact ister.
- AI yardımı serbest; assisted performance independent mastery değildir.
- Tek yeni yanlış mastered Skill'i anında silmez.
- English paralel gider; global technical blocker değildir.
- Core mastery/prerequisite/planner LLM'nin keyfi kontrolünde değildir.
- D-028: bounded/incremental hesap, async ağır işler, gerçek cihaz performance QA.
- D-041: professional readiness takvim değil evidence ile belirlenir.
- D-043: specialization common core'u silmez; branch-local prerequisite/evidence ile çalışır.

## 5. Tamamlanan aşamalar

### AŞAMA 1 ✅
`1A–1D` tamamlandı.

### AŞAMA 2 ✅ — Öğrenme/Mastery
- `GRE-v0 — Gated Recent Evidence` — D-031
- `RVR-v0 — Retention Verification & Risk` — D-032

### AŞAMA 3 ✅ — Adaptive Planner
- `3A` D-033 — hard daily capacity / no task debt
- `3B` D-034 — LearningNeed / TaskCandidate / Evidence ayrımı
- `3C` PBR-v0 / D-035 — semantic bands + deterministic rank
- `3D` PRG-v0 / D-036 — hard/soft prerequisites, branch-local blocking
- `3E` VDW-v0 / D-037 — validated diagnostic waiver
- `3F` SRR-v0 / D-038 — current-state re-entry / no absence debt
- `3G` PDT-v0 / D-039 — structured decision trace
- `3H` planner simulation PASS

3H sonucu:
```text
16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical cross-spec contradiction
```

### AŞAMA 4 ilerlemesi

#### 4A ✅ Günlük mikro değerlendirme — DMA-v0 / D-040
Ana çıktı: `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`.

## 6. Güncel kesin konum

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** devam ediyor

- `4A` ✅
- `4B` 🟡 **Haftalık sınav — AKTİF**
- `4C–4E` ⬜ bekliyor
- `5–19` ⬜ bekliyor
- `20A–20G` ⬜ future specialization

**Önemli:** D-041/D-042/D-043 sync tamamlandı; 4B henüz yürütülmedi. 4B başlamadan yeni PRE-STEP GitHub refresh zorunlu.

## 7. 4B'de kesinleştirilecekler

Ana soru:
> Haftalık sınav daily micro assessment'ın sağlayamadığı hangi daha geniş evidence'ı sağlamalı ve çok sayıda Skill/Objective'i adil bir blueprint ile nasıl ölçmeli?

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
- tek sınava aşırı tepki vermeyen hysteresis,
- D-041 professional-readiness ve D-043 future track evidence ile uyum,
- 4C monthly assessment için ortak blueprint/result contract.

## 8. İlk okuma sırası
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
11. `docs/LEARNING_ENGINE_SPEC.md`
12. `docs/LEARNING_BEHAVIOR_RULES.md`
13. `docs/MASTERY_SIGNALS_SPEC.md`
14. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
15. `docs/MASTERY_FORMULA_V0.md`
16. `docs/RETENTION_FORGETTING_SPEC.md`
17. `docs/ADAPTIVE_PLANNER_SPEC.md`
18. `docs/TASK_TAXONOMY_SPEC.md`
19. `docs/PRIORITY_POLICY_SPEC.md`
20. `docs/PREREQUISITE_POLICY_SPEC.md`
21. `docs/DIAGNOSTIC_WAIVER_SPEC.md`
22. `docs/MISSED_DAY_RECOVERY_SPEC.md`
23. `docs/PLANNER_EXPLAINABILITY_SPEC.md`
24. `docs/PLANNER_SIMULATION_SUITE.md`
25. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
26. `docs/ENGLISH_FOUNDATION_RULES.md`
27. `docs/MASTER_PLAN.md`
28. `docs/PROGRESS_LOG.md`

## 9. Yeni sohbetin ilk işi
Repo üzerinden D-041/D-042/D-043 kapsam ve rota kararlarını ve aktif adımı doğrula. Ardından **4B — Haftalık sınav** için yeni PRE-STEP GitHub refresh yap. Aşama 2 GRE/RVR, Aşama 3 D-033–D-039 + 3H PASS ve 4A DMA-v0/D-040 kullanıcı açıkça değiştirmedikçe yeniden açılmamalıdır.
