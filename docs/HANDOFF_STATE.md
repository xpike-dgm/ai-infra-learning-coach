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
- Final readiness mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ister.
- V1 full curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.
- Product job offer/salary/seniority veya üniversite/HR filtresi garantisi vermez.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## 3. Güncel rota/plan kararları

### D-042 — Python resmi foundation
Python common core'a resmi olarak eklendi; C/C++ yerine geçmez.

### D-043 — geri çekildi
Standalone specialization-stage yorumu canonical değildir.

### D-044 — AŞAMA 6 Granular Capability Map
Ana rotadaki her büyük alan `Domain → Module → Topic → Skill → Learning Objective` seviyesine ayrılacak. Hedef broad `Python zayıf` yerine exact Skill/Objective weakness/mastery/remediation.

### D-045 — WBA-v0
Weekly assessment item'dan önce blueprint üretir; fixed score/time/quota yoktur; multi-Skill evidence granular attribution ile çalışır.

### D-046 — MCA-v0
Monthly assessment longitudinal state-based sampling, broader transfer/integration ve ihtiyaç-temelli critical revalidation kullanır; cumulative final/pass-score değildir.

### D-047 — QAB-v0 Trusted Assessment Resource Bank
Canonical: `docs/QUESTION_BANK_SPEC.md`.

Ana kararlar:
- Bank yalnız soru/MCQ deposu değildir; coding/debugging/system/transfer/integrated task dahil versioned AssessmentResource'lar taşır.
- `resource_id` logical identity; `resource_version` immutable published content version'dır.
- Attempt exact version'a bağlanır; published version sessizce overwrite edilmez.
- Lifecycle: `draft | candidate | validated | trusted | deprecated | invalidated | retired`.
- Lifecycle ile `use_ceiling` ayrıdır; bank'te bulunmak automatic strong evidence değildir.
- Resource exact Objective/Skill, prerequisite, language prerequisite, evidence/activity, assessment scope, blueprint role/intent, evaluator/tool/artifact ve duration metadata'sı taşır.
- Variant family, dependency/testlet, context family ve transfer profile ayrıdır; same/near resource'lar evidence diversity'yi şişiremez.
- Integrated resource global PASS'i sibling Objectives'e yayamaz; component attribution gerekir.
- Difficulty fake precision/numeric mastery multiplier değildir.
- User solution exposure global content'ten ayrıdır; fixed universal reuse cooldown yoktur.
- Learner exposure freshness ile technology/content freshness ayrıdır.
- Deprecated ≠ invalidated; invalidated version historical evidence audit/repair akışına girebilir.
- Selector bounded/indexed çalışır; full-bank scan hedeflenmez.
- AI-generated resource varsayılan `candidate` başlar ve 4E validation olmadan trusted/mastery-changing use'a yükselmez.

## 4. Tamamlanan aşamalar

### AŞAMA 1 ✅
`1A–1D` tamamlandı.

### AŞAMA 2 ✅
`2A–2F` tamamlandı: GRE-v0 / RVR-v0 canonical.

### AŞAMA 3 ✅
`3A–3H` tamamlandı.

```text
16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical contradiction
```

### AŞAMA 4 ilerlemesi
- 4A ✅ DMA-v0 / D-040
- 4B ✅ WBA-v0 / D-045
- 4C ✅ MCA-v0 / D-046
- 4D ✅ QAB-v0 / D-047
- 4E 🟡 AI-generated soru doğrulaması — aktif, henüz yürütülmedi

## 5. 4D final akış

```text
Granular Skill/Objectives
→ versioned AssessmentResources
→ lifecycle + trust + use ceiling
→ blueprint slot requirement
→ scope/role/evidence/prerequisite filter
→ exposure + variant/dependency/context filter
→ evaluator/tool/duration eligibility
→ bounded selection
→ PlannedTask
→ Attempt/Artifact
→ validity + assistance + provenance + evaluator
→ Objective-level EvidenceEvent
→ GRE/RVR/PRG/Planner
```

4D'de ayrı Research AI kullanılmadı; psychometric calibration veya AI-validator accuracy threshold uydurulmadı. Empirical item/exposure calibration AŞAMA 18'e, AI validation/promotion policy 4E'ye bırakıldı.

## 6. Güncel kesin konum

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** devam ediyor

- `4A–4D` ✅
- `4E` 🟡 **AI-generated soru doğrulaması — AKTİF**
- `5–20` ⬜ bekliyor

## 7. 4E'de kesinleştirilecekler
Ana soru:
> AI-generated assessment resource'ın teknik olarak doğru, adil, target-matched ve yeterince güvenilir olduğunu nasıl doğrulayacağız; hangi koşulda practice-only kalacak, validated olacak veya trusted/high-stakes use'a yükselebilecek?

Kesinleştirilecek:
- generated candidate lifecycle entry,
- schema completeness,
- technical correctness,
- answer/rubric correctness,
- ambiguity/multiple-valid-answer detection,
- target Objective/evidence modality fit,
- prerequisite completeness / forbidden-concept leakage,
- duplicate/near-duplicate / variant-family classification,
- dependency/testlet/context/transfer validation,
- evaluator/tool/artifact compatibility,
- source/technology freshness,
- deterministic checks vs AI cross-check vs human/manager review sınırları,
- risk-based `use_ceiling` promotion,
- trusted-template inheritance,
- validator uncertainty/fail-safe,
- invalidation/revalidation lifecycle.

## 8. 4E için doğrudan okunacaklar
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/QUESTION_BANK_SPEC.md`
8. `docs/DAILY_MICRO_ASSESSMENT_SPEC.md`
9. `docs/WEEKLY_ASSESSMENT_SPEC.md`
10. `docs/MONTHLY_ASSESSMENT_SPEC.md`
11. `docs/MASTERY_SIGNALS_SPEC.md`
12. `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
13. `docs/MASTERY_FORMULA_V0.md`
14. `docs/PREREQUISITE_POLICY_SPEC.md`
15. `docs/TASK_TAXONOMY_SPEC.md`
16. `docs/LEARNING_BEHAVIOR_RULES.md`
17. `docs/ENGLISH_FOUNDATION_RULES.md`
18. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
19. `docs/MASTER_PLAN.md`
20. `docs/PROGRESS_LOG.md`

## 9. Yeni sohbetin ilk işi
Repo üzerinden D-041/D-042/D-044/D-045/D-046/D-047 ve aktif adımı doğrula. D-043'ü canonical kabul etme. Ardından **4E — AI-generated soru doğrulaması** için yeni PRE-STEP GitHub refresh yap.
