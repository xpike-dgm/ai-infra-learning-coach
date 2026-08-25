# HANDOFF STATE — Güncel Proje Durumu

**Son güncelleme:** 2026-08-25  
Repo: `xpike-dgm/ai-infra-learning-coach`

## 0. Zorunlu protokol
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`, D-024, D-027.

> Her numaralı adım başlamadan PRE-STEP GitHub refresh; bittikten sonra ana çıktı + `EXECUTION_INDEX`, `STEP_STATUS`, `HANDOFF_STATE`, `PROGRESS_LOG`, `MASTER_PLAN` ve gerekiyorsa `DECISIONS` senkronu zorunludur.

## 1. Ürün ve ana rota
Tek kullanıcı için, sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering yolunda günlük yöneten, uygulama içinde öğreten/uygulatan ve yalnız kanıtlanmış öğrenmeyi ilerleme sayan adaptif Android öğrenme koçu.

Güncel ana rota:
**Technical English (parallel) → Python → C → Linux + Git + Shell → DS&A foundations → Modern C++ → Computer Architecture → OS + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases → Containers / Cloud / Observability → Performance Engineering / Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM/SGLang/TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

## 2. Uzun vadeli hedef — D-041
- Full curriculum 4+ yıl veya daha uzun sürebilir.
- Takvim readiness gate değildir.
- Final hedef professional-readiness seviyesinde verified engineering capability.
- Mastery + retention + debugging + transfer + performance + integrated project/capstone evidence gerekir.
- V1 full curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## 3. Güncel rota/plan kararları

### D-042 — Python resmi foundation
Python common core'un resmi parçasıdır; C/C++ yerine geçmez.

### D-043 — geri çekildi
Standalone specialization-stage yorumu canonical değildir.

### D-044 — AŞAMA 6 Granular Capability Map
Ana rotadaki her büyük alan `Domain → Module → Topic → Skill → Learning Objective` seviyesine ayrılacak. Hedef broad `Python zayıf` yerine exact Skill/Objective weakness/mastery/remediation.

### D-045 — WBA-v0
Weekly assessment item'dan önce blueprint üretir; fixed score/time/quota yoktur; multi-Skill evidence granular attribution ile çalışır.

### D-046 — MCA-v0
Monthly assessment longitudinal state-based sampling, broader transfer/integration ve ihtiyaç-temelli critical revalidation kullanır; cumulative final/pass-score değildir.

### D-047 — QAB-v0
Question Bank yalnız MCQ değil; coding/debugging/system/transfer/integrated task dahil versioned AssessmentResource bank'idir. Logical ID + immutable version, lifecycle/use ceiling, exact Objective/prerequisite/evidence attribution, exposure/family/context/freshness ve bounded selection vardır.

### D-048 — AIV-v0 AI Assessment Resource Validation
Canonical: `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md`.

Ana kararlar:
- AI-generated resource `candidate` başlar ve kendi çıktısı validation proof değildir.
- Minimum safety/correctness validation geçmeden user-facing selection'a çıkamaz.
- Schema, technical correctness, answer/rubric, ambiguity, target/evidence fit, prerequisite/forbidden concept/language leakage, duplicate/family/dependency/context/transfer, evaluator/tool/artifact, freshness ve execution-safety ayrı check'lerdir.
- Validation weighted confidence score değildir; final use ceiling en kısıtlayıcı applicable check ile belirlenir.
- Semantic use ceilings: `practice_only < low_stakes_assessment < standard_mastery_eligible < critical_mastery_eligible`.
- Generator self-review veya AI majority vote truth değildir; deterministic/executable/reference-grounded checks önceliklidir.
- Practice-only yanlış bilgi toleransı değildir; correctness unresolved resource gösterilmez.
- Single uncalibrated LLM standard/critical verified mastery için yeterli değildir.
- Near duplicate yeni independent family sayılmaz; transfer claim ayrıca doğrulanır.
- Hidden technical/English prerequisite learner failure'a dönüştürülemez.
- Trusted-template inheritance yalnız validated invariants korunuyorsa mümkündür.
- Validator disagreement promotion'ı fail-safe biçimde durdurur.
- Generated code/system task execution safety validation ister.
- Version-sensitive content freshness/source audit ister.
- Confirmed content bug invalidation + historical evidence review/repair açabilir; learner cezalandırılmaz.

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

### AŞAMA 4 ✅ — Assessment foundation tamamlandı
- 4A ✅ DMA-v0 / D-040
- 4B ✅ WBA-v0 / D-045
- 4C ✅ MCA-v0 / D-046
- 4D ✅ QAB-v0 / D-047
- 4E ✅ AIV-v0 / D-048

Canonical assessment flow:

```text
Current granular Skill/Objective state
→ Daily / Weekly / Monthly assessment blueprint
→ QAB-v0 trusted/versioned resource selection
→ AIV-v0 validation/trust/use-ceiling policy for AI-generated resources
→ PlannedTask
→ Attempt / Artifact
→ prerequisite + assistance + provenance + evaluator validation
→ Objective-level EvidenceEvent
→ GRE / RVR / PRG / Planner
```

4E'de ayrı Research AI kullanılmadı; validator accuracy yüzdesi, majority-vote optimum'u veya universal threshold uydurulmadı. Empirical calibration AŞAMA 14F/18'e bırakıldı.

## 5. Güncel kesin konum

**AŞAMA 1:** ✅  
**AŞAMA 2:** ✅  
**AŞAMA 3:** ✅  
**AŞAMA 4:** ✅  
**AŞAMA 5:** başladı

- `5A` 🟡 **Ana domain haritası — AKTİF, henüz yürütülmedi**
- `5B–5D` ⬜ bekliyor
- `6–20` ⬜ bekliyor

## 6. 5A'da kesinleştirilecekler
Ana soru:
> 4+ yıllık professional-readiness hedefini kapsayan ana knowledge/curriculum domain envelope nasıl kurulmalı; hangi alanlar common core, parallel track, supporting domain veya advanced specialization katmanı olmalı?

Kesinleştirilecek:
- Technical English paralel track,
- Python + C common foundation,
- Linux/Git/Shell + DS&A foundations,
- Modern C++,
- Computer Architecture,
- OS/Memory,
- Concurrency/Parallelism,
- Networking,
- Distributed Systems + Storage/DB,
- Containers/Cloud/Observability,
- Performance Engineering,
- GPU Architecture + CUDA + Triton,
- ML/Transformer support depth,
- LLM Inference + serving engines,
- KV Cache / batching / scheduling / quantization,
- Multi-GPU / NCCL / RDMA,
- AI/GPU Infrastructure,
- Open Source / engineering practice / projects-capstone layer,
- domain-level prerequisite vs parallel relations,
- AŞAMA 6 granular capability map'e handoff.

5A ayrıntılı Python/CUDA alt konu listesini yazmayacak; bu AŞAMA 6'nın işi.

## 7. 5A için doğrudan okunacaklar
1. `docs/START_HERE.md`
2. `docs/PROJECT_MEMORY_PROTOCOL.md`
3. `docs/HANDOFF_STATE.md`
4. `docs/EXECUTION_INDEX.md`
5. `docs/STEP_STATUS.md`
6. `docs/DECISIONS.md`
7. `docs/PROFESSIONAL_READINESS_TARGET.md`
8. `docs/PRODUCT_REQUIREMENTS.md`
9. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
10. `docs/LEARNING_ENGINE_SPEC.md`
11. `docs/LEARNING_BEHAVIOR_RULES.md`
12. `docs/MASTER_PLAN.md`
13. `docs/PROGRESS_LOG.md`

## 8. Yeni sohbetin ilk işi
Repo üzerinden D-041/D-042/D-044/D-045/D-046/D-047/D-048 ve aktif adımı doğrula. D-043'ü canonical kabul etme. Ardından **5A — Ana domain haritası** için yeni PRE-STEP GitHub refresh yap.
