# 6H External Research AI — Multi-Evaluator Reconciliation

**Stage:** 6H  
**Date:** 2026-08-27  
**Manager status:** reconciliation in progress; this document does not by itself close 6H.

## Independent reports received

All three evaluators used the supplied Stage 6 snapshot/brief and returned **PASS WITH REQUIRED CHANGES**.

| Evaluator artifact | SHA-256 | Verdict | Manager reliability note |
|---|---|---|---|
| `chatgpt.md` | `52d50c17b71df63d16560cf81bd47aac108fa0f1b9776eea1fd7c1787a97eeb4` | PASS WITH REQUIRED CHANGES | Highest canonical-ID alignment; detailed primary-source and learning-science reconciliation. |
| `perplexity.md` | `86ebef2f6dabfcf10d302e72a5329777b9b3e02e92bf77310e58aaa7cb026568` | PASS WITH REQUIRED CHANGES | Strong independent corroboration; exact row IDs occasionally unavailable from combined-file indexing. |
| `gemini.md` | `829b9bcc4dba5f845704193c3ebfbb5b9a4ba26dd3da4130b9ba2eb42abd3bec` | PASS WITH REQUIRED CHANGES | Useful independent trend corroboration, but its D01–D23 numbering diverges from the canonical repo and several vendor/model-specific features are over-promoted to stable capability status. |

Raw reports were user-supplied in the 6H execution session. Their hashes preserve provenance even though raw conversation attachments are not the durable project source of truth.

## Manager reconciliation rule

External reports are evidence, not automatic authority. A proposed change is accepted only when it:
1. maps to the canonical D01–D23 registry,
2. passes GNS-v0 independent-failure/remediation/reuse boundaries,
3. is supported by current authoritative sources or learning/assessment evidence,
4. does not convert a fast-moving vendor/API detail into unnecessary stable Skill identity,
5. preserves PRG-v0 hard/soft semantics and the global hard DAG.

## High-confidence required stable capability additions

The following gaps are independently supported and absent from the current canonical registry:

1. `skill.os.numa_locality_affinity` — local/remote NUMA memory, CPU/device affinity and justified topology placement.
2. `skill.cuda.async_data_movement_pipeline` — in-kernel staged asynchronous data movement, producer/consumer synchronization and copy/compute overlap.
3. `skill.optimization.speculative_decoding_tradeoff` — proposal/verification, acceptance behavior and latency/throughput/compute trade-offs independent of a specific engine.
4. `skill.serving.prefill_decode_disaggregation` — separate prefill/decode resource pools with KV-state handoff, routing, transfer cost, SLO and failure semantics.
5. `skill.ml.moe_routing_dataflow` — router/top-k expert selection, token dispatch, expert compute and combine semantics.
6. `skill.multi_gpu.expert_parallel_sharding` — expert/token placement across GPUs/nodes with communication, topology and imbalance reasoning.

`skill.cuda.cluster_scope_execution_memory` remains a conditional split: cluster/DSM receives explicit objective/freshness coverage; it becomes a separate Skill only if independent evidence/remediation remains meaningfully distinct after the final granularity test.

## Required hidden-prerequisite corrections

At minimum reconcile/add:
- `skill.arch.cache_hierarchy_model -> skill.os.numa_locality_affinity` hard
- `skill.os.virtual_physical_translation -> skill.os.numa_locality_affinity` hard
- `skill.os.scheduler_model -> skill.os.numa_locality_affinity` soft
- `skill.os.numa_locality_affinity -> skill.multi_gpu.topology_discovery` hard
- `skill.os.numa_locality_affinity -> skill.serving.worker_resource_binding` soft
- `skill.platform.scheduling_placement_constraints -> skill.ai_infra.topology_aware_placement` hard
- `skill.inference.prefill_decode_distinction -> skill.serving.prefill_decode_disaggregation` hard
- `skill.inference.kv_cache_semantics -> skill.serving.prefill_decode_disaggregation` hard
- `skill.serving.worker_engine_topology -> skill.serving.prefill_decode_disaggregation` hard
- `skill.network.latency_bandwidth_budget -> skill.serving.prefill_decode_disaggregation` hard
- `skill.ml.transformer_block_dataflow -> skill.ml.moe_routing_dataflow` hard
- `skill.ml.moe_routing_dataflow -> skill.multi_gpu.expert_parallel_sharding` hard
- `skill.multi_gpu.collective_semantics -> skill.multi_gpu.expert_parallel_sharding` hard
- `skill.inference.autoregressive_loop -> skill.optimization.speculative_decoding_tradeoff` hard
- `skill.cuda.shared_memory_tiling -> skill.cuda.async_data_movement_pipeline` hard
- `skill.cuda.memory_visibility_ordering -> skill.cuda.async_data_movement_pipeline` hard
- `skill.gpu.copy_compute_overlap_model -> skill.cuda.async_data_movement_pipeline` hard

Math/numerical conclusion: **no new broad math/calculus domain**. Existing tensor/linear-algebra/probability/numerical Skills are sufficient for the target role; quantization-related prerequisite edges must be rechecked and added only if absent.

## Required freshness/objective patches — not new stable tool Skills

Version/source-scoped objectives/examples must cover current behavior for:
- Python free-threaded/runtime concurrency assumptions,
- Kubernetes DRA/ResourceClaim/DeviceClass and topology/all-or-nothing workload scheduling,
- CUDA thread-block clusters/DSM, TMA/cp.async-like mechanisms, Graphs/memory-pool examples,
- Triton persistent execution, TMA/software pipelining, warp specialization and current autotuning/numerical-validation practice,
- KV offload, chunked prefill, prefix reuse and FP8/FP4-class quantization variants,
- NCCL/RDMA registration, dma-buf/zero-copy, NVLS/current topology diagnosis and IB/RoCE implementation cases,
- OpenTelemetry Profiles/GenAI convention names,
- SBOM/component inventory vs build provenance vs signed/verified attestation,
- live repository-specific OSS contribution policy.

Model/vendor-specific items such as MLA, MTP, DeepSeekMoE/DualPipe, FlashInfer, vLLM V1 internals, NVFP4/MXFP4, CUTLASS/CuTe and particular scheduler/runtime APIs are valuable **freshness-scoped examples/objectives** unless a GNS-v0 independent capability test later proves a stable learner-state boundary. They are not automatically canonical Skills.

## D23 professional-readiness changes

The existing D23 breadth is retained. Final readiness must formalize a non-compensatory evidence guard:
- no global capstone PASS may grant component mastery,
- component evidence remains separately attributable,
- evidence must be fresh/direct/verified where required,
- readiness requires evidence from at least three materially distinct families: systems/service, GPU/inference, and production/professional,
- at least one operations/failure-injection or incident-reconstruction artifact is required for operational readiness.

Semantic merge review is required for the suspected OSS context-only pairs:
- `oss_patch_scope` vs `pr_scope_discipline`,
- `oss_maintainer_feedback_iteration` vs generic review-feedback iteration,
- `oss_submission_evidence` vs `pr_problem_solution_evidence`.
Merge only when independent failure/remediation/planner behavior is equivalent; preserve OSS context through TopicSkillLink/project attribution.

## WLRM changes

WLRM core safety is externally corroborated and retained: prerequisite contamination protection, no broad reset, assisted/provisional evidence ceilings, first contradiction -> verification, fresh H0 closure and no project evidence broadcast.

Required addition: an explicit expertise-aware **guidance-fading selector**. High guidance is appropriate for novice/prerequisite repair; previously mastered or repeatedly transferred capabilities default to retrieval/verification/transfer/debug localization rather than worked-example reteaching. AI assistance may scaffold remediation but cannot satisfy independent closure evidence.

Add executable/state-transition QA for these guards. Do **not** replace the existing deterministic safety contract with a mandatory BKT probability model; Gemini's BKT proposal is treated as an optional future calibration hypothesis, not a Stage 6 requirement.

## 6H review disposition target

- `review.6c.external_coverage` -> resolve with minor freshness patch; no blocking D01–D05 semantic gap.
- `review.6d.external_coverage` -> resolve after NUMA addition/edges.
- `review.6d.platform_tool_freshness` -> resolve after DRA/topology freshness metadata.
- `review.6e.external_coverage` -> resolve after six stable capability/edge changes and required objective coverage.
- `review.6e.tool_runtime_freshness` -> resolve after version/source-scoped runtime refresh policy.
- `review.6e.math_numerical_coverage` -> resolve with no new math domain; verify required edges.
- `review.6f.external_coverage` -> resolve after D23 semantic merge/evidence audit.
- `review.6f.oss_workflow_freshness` -> resolve as live versioned contributor-policy model.
- `review.6f.capstone_diversity` -> resolve after non-compensatory >=3 evidence-family guard.
- `review.6g.external_behavior_coverage` -> resolve after guidance-fading + executable guard QA.

## Acceptance guard

6H remains incomplete until the accepted patch set is applied deterministically, 6G is regenerated against the final registry, all source-package and combined graph validators pass, the 10 6H-owned reviews are resolved, and D-050 POST-STEP living-memory/stale-reference audit passes.
