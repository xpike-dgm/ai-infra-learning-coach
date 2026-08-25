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
Weekly assessment item'dan önce blueprint üretir; multi-Skill evidence Objective-level attribution ile çalışır; fixed score/time/quota yoktur; incomplete/missed exam debt değildir.
Canonical: `docs/WEEKLY_ASSESSMENT_SPEC.md`.

### D-046 — MCA-v0 Monthly Capability Assessment
Monthly assessment:
- tek ay sonu notu/domain pass-fail değildir,
- WBA common blueprint/result abstraction'ını kullanır,
- longitudinal required capability, persistent weakness/verification, critical revalidation, delayed retention, cross-topic transfer, integrated application ve gerektiğinde Technical English/professional checkpoint role'larını kullanır; fixed quota değildir,
- cumulative-everything exam değildir; state-temelli bounded longitudinal sampling yapar,
- recent/older balance fixed yüzde değildir,
- critical Skill sırf critical diye her ay otomatik retest edilmez,
- transfer/integration yalnız öğretilmiş prerequisites ve component-level attribution ile çalışır,
- professional checkpoint final professional-readiness/capstone gate değildir,
- fixed soru sayısı/süre/pass score yoktur,
- daily hard capacity korunur; safe split/pause/resume mümkündür,
- incomplete/missed monthly exam failure/debt değildir,
- H0/assistance/provenance/prerequisite/evaluator safety + GRE/RVR hysteresis aynen korunur,
- raw broad domain pass/fail yazmaz; D-044 granular localization'ı korur,
- güvenilir persistent/critical gap planner/curriculum priority'yi gerçekten değiştirebilir.

Canonical: `docs/MONTHLY_ASSESSMENT_SPEC.md`.

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
- 4C ✅ MCA-v0 / D-046
- 4D 🟡 Soru bankası — aktif, henüz yürütülmedi
- 4E ⬜ bekliyor

## 5. 4C final özeti

Canonical flow:

```text
monthly cycle
→ current granular state + longitudinal summaries
→ monthly capability blueprint
→ bounded critical/retention/transfer/integration slots
→ validated + prerequisite-valid items/tasks
→ capacity-aware multi-block session
→ Attempt/Artifact
→ Objective-level EvidenceEvent
→ GRE/RVR/verification/weakness/remediation
→ PRG/derived curriculum state
→ next-period planner/replan
```

Critical guards:
- monthly label evidence'a extra weight vermez,
- one score/domain pass-fail yoktur,
- cumulative full-history exam yoktur,
- persistent weakness tek item'dan türetilmez,
- critical revalidation automatic monthly quota değildir,
- unknown prerequisite transfer difficulty olarak kullanılamaz,
- integrated task global pass'i component Skills'e yayamaz,
- invalid/provisional/contaminated item kullanıcıyı etkilemez,
- H1–H4 positive independent mastery değildir,
- first clean post-mastery failure instant unmastery değildir,
- missed/incomplete monthly exam failure/debt değildir,
- professional checkpoint final readiness değildir.

4C'de ayrı Research AI kullanılmadı; calibrated psychometric optimum uydurulmadı. Empirik assessment calibration AŞAMA 18'e bırakıldı.

## 6. Güncel kesin konum

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** devam ediyor

- `4A` ✅
- `4B` ✅
- `4C` ✅
- `4D` 🟡 **Soru bankası — AKTİF**
- `4E` ⬜ bekliyor
- `5–20` ⬜ bekliyor

## 7. 4D'de kesinleştirilecekler
Ana soru:
> Daily/weekly/monthly blueprint'lerin güvenilir biçimde seçebileceği, uzun 4+ yıllık curriculum boyunca ölçeklenebilecek ve aynı/near sorularla sahte evidence üretmeyecek trusted item/task bank nasıl modellenmeli?

Kesinleştirilecek:
- stable item/task ID + version/lifecycle,
- target Skill/Objective attribution,
- required prerequisites + forbidden not-yet concepts,
- evidence/activity type,
- assessment scope eligibility,
- blueprint-role eligibility,
- difficulty/complexity semantics,
- variant family + dependency/testlet group,
- transfer/context structure,
- integrated component attribution,
- answer key/rubric/test reference,
- evaluator requirements,
- allowed tools + artifact requirements,
- validation/trust/content origin,
- solution exposure / reuse / freshness,
- estimated duration / atomic boundaries,
- language/scaffold metadata,
- selection/indexing/bounded-performance contract,
- 4E AI-generated candidate validation/lifecycle handoff.

## 8. 4D için doğrudan okunacaklar
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
8. `docs/WEEKLY_ASSESSMENT_SPEC.md`
9. `docs/MONTHLY_ASSESSMENT_SPEC.md`
10. `docs/MASTERY_SIGNALS_SPEC.md`
11. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
12. `docs/MASTERY_FORMULA_V0.md`
13. `docs/PREREQUISITE_POLICY_SPEC.md`
14. `docs/TASK_TAXONOMY_SPEC.md`
15. `docs/LEARNING_BEHAVIOR_RULES.md`
16. `docs/ENGLISH_FOUNDATION_RULES.md`
17. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
18. `docs/MASTER_PLAN.md`
19. `docs/PROGRESS_LOG.md`

## 9. Yeni sohbetin ilk işi
Repo üzerinden D-041/D-042/D-044/D-045/D-046 ve aktif adımı doğrula. D-043'ü canonical kabul etme. Ardından **4D — Soru bankası** için yeni PRE-STEP GitHub refresh yap.