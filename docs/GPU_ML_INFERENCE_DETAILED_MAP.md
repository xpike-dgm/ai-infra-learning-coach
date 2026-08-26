# GIM-v0 — GPU / ML / Inference Detailed Map

**Adım:** 6E  
**Karar:** D-059  
**Durum:** Internal authoring + deterministic QA complete; external Research QA pending 6H  
**Canonical dataset:** `curriculum/decomposition/6e_gpu_ml_inference/`

## Kapsam
D14–D22 route family'leri ayrıntılandırıldı: GPU Architecture, CUDA, Triton, ML + Transformer Foundations, LLM Inference Internals, Serving Systems, KV Cache / Batching / Scheduling / Quantization, Multi-GPU + NCCL + RDMA ve AI/GPU Infrastructure.

## Final sayılar
- 9 Domain / 27 Module / 70 Topic
- 143 Skill / 159 Objective / 230 TopicSkillLink
- 279 prerequisite edge: 247 hard / 32 soft
- 89 cross-package prerequisite edge
- 59 prior Skill reuse: 9 Foundation + 50 Systems
- 286 capability requirement
- 216 professional attribution
- 16 separately-observable project/capstone attribution

## Ana kararlar
- Existing 6C/6D capabilities clone edilmedi; canonical Skill ID reuse edildi.
- Hidden math/numerical prerequisites explicit hale getirildi: tensor shape/broadcast, matrix multiplication, probability/softmax, floating-point error ve mixed precision reasoning.
- Stable accelerator/inference concepts generic tutuldu; CUDA, Triton, GPU profiler, serving runtimes, NCCL/RDMA gibi tool/runtime-specific capabilities freshness + technology dependency metadata'sı taşır.
- Triton core, GPU/CUDA foundation'ını bypass etmez.
- English teknik route için global hard gate değildir.
- FRDB Pass-B audit sonrası 32 scaffold ilişki soft'a indirildi, 2 gerçek prerequisite olmayan ilişki kaldırıldı.

## QA
- Prerequisite registry preflight: PASS
- FRDB hard/soft Pass-B edge audit: PASS
- 6D regression validator: PASS
- Independent 6E package validator: PASS
- 6C+6D+6E combined hard graph: DAG 467/467
- Package result: `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`
- Open reviews: 0 blocking / 3 non-blocking

## Review handoff
- `review.6d.accelerator_forward_reuse` 6E tarafından resolved edildi.
- 6D'nin external coverage/tool freshness review'ları 6H'ye, professional overlay reconciliation 6F'ye açık kalır.
- `review.6e.professional_overlay_reconciliation` PEM-v0 / D-060 ile resolved edildi; D23 registry 6E professional/project attributions'ını canonical ID reuse ile reconcile eder.
- 6E external coverage, tool/runtime freshness ve math/numerical coverage audit'leri independent external Research QA için 6H'ye pending kalır.
- Package 6H tamamlanana kadar external-validation/learner-publication açısından final sayılmaz.
