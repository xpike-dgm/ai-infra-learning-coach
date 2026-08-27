# 6H Manager Current-Source Audit — NOT Independent External Research QA

**Step:** 6H — Coverage / prerequisite / external Research QA  
**Date:** 2026-08-27  
**Status:** Manager-side current-source audit complete enough to seed independent review; **this document does not satisfy D-016 / 6H external Research AI independence.**

## 1. Purpose and guard

This audit compares the accepted Stage 6 registry against current authoritative/public technical sources. It is deliberately a **hypothesis generator**, not the final evaluator.

The following are prohibited:
- closing 6H from this manager-side web research alone,
- resolving 6C–6G reviews owned by 6H without independent Research AI evidence,
- adding/removing canonical Skills merely because a current tool exposes a feature,
- treating one vendor/tool feature as a stable capability without a semantic granularity check.

Final 6H acceptance still requires a separate external Research AI/evaluator to challenge these hypotheses and the whole route independently.

## 2. Internal baseline already verified

`curriculum/decomposition/6h_research_qa/structural_report.yaml` establishes:
- D01–D23 route envelope: 23/23,
- 543 globally unique accepted Skills,
- 590 globally unique Objectives,
- 930 prerequisite edges: 836 hard / 94 soft,
- combined hard graph DAG: 543/543,
- no dangling prerequisite endpoint,
- no self prerequisite,
- no conflicting hard/soft pair,
- every Skill has at least one Objective,
- WLRM exact coverage: 543 Skills / 590 Objectives,
- 10 open reviews are explicitly owned by 6H,
- 3 same-name duplicate candidates are language-specific (`C` vs `Python`) and have different capability statements; they remain review candidates, not proven duplicates.

## 3. Current-source evidence and candidate coverage questions

### 3.1 Kubernetes accelerator allocation — DRA

Current authoritative source:
- Kubernetes Dynamic Resource Allocation: https://kubernetes.io/docs/concepts/resource-management/dynamic-resource-allocation/
- Current docs mark DRA **stable in Kubernetes v1.35** and describe device classes/claims for attached devices such as accelerators.

Registry comparison:
- Existing `skill.ai_infra.gpu_capability_inventory` covers accelerator inventory.
- Existing `skill.ai_infra.topology_aware_placement` covers topology/failure-domain placement reasoning.
- Exact `ResourceClaim` / DRA capability is not present in the accepted Skill registry.

**External-review hypothesis H-K8S-01:** Determine whether dynamic accelerator/device claim semantics now constitute a distinct version-sensitive production Skill, or whether existing inventory + placement Skills plus later content are sufficient.

### 3.2 Kubernetes workload-aware / gang scheduling

Current authoritative sources:
- Kubernetes v1.36 workload-aware scheduling: https://kubernetes.io/blog/2026/05/13/kubernetes-v1-36-advancing-workload-aware-scheduling/
- Kubernetes v1.36 release notes/blog: https://kubernetes.io/blog/2026/04/22/kubernetes-v1-36-release/

Current v1.36 work includes Workload/PodGroup APIs, gang scheduling, topology-aware scheduling and workload-aware preemption for AI/ML and batch workloads; the v1.36 set is still alpha.

Registry comparison:
- Generic scheduling, fairness/preemption and topology-aware placement concepts exist.
- Exact gang/workload-group scheduling semantics are not represented as a named accepted Skill.

**H-K8S-02:** Decide whether a stable conceptual `co-scheduling / gang scheduling reasoning` capability is missing, while keeping v1.36 API details version-sensitive rather than turning alpha API names into evergreen curriculum identity.

### 3.3 LLM serving — disaggregated prefill/decode and KV transfer

Current authoritative sources:
- vLLM disaggregated prefilling: https://docs.vllm.ai/en/latest/features/disagg_prefill/
- TensorRT-LLM disaggregated serving: https://nvidia.github.io/TensorRT-LLM/features/disagg-serving.html

Both current serving stacks explicitly model prefill/context and decode/generation on separate instances/GPUs and require KV-state exchange/connectors.

Registry comparison:
- `skill.inference.prefill_decode_distinction` exists.
- `skill.inference.prefill_decode_profile` exists.
- No explicit accepted Skill was found for disaggregated prefill/decode serving architecture or KV transfer between phases.

**H-INF-01:** Decide whether `disaggregated prefill/decode architecture + KV transfer boundary` is independently observable/remediable enough for a new Skill, versus being a transfer context for existing prefill/decode + distributed transport capabilities.

### 3.4 LLM serving — speculative decoding

Current authoritative source:
- vLLM speculative decoding: https://docs.vllm.ai/en/latest/features/speculative_decoding/

Current vLLM documents several speculation methods and positions speculative decoding as a latency optimization for suitable workloads.

Registry comparison:
- No `speculative` Skill/topic string was found in the accepted 6E registry.

**H-INF-02:** Validate whether speculative decoding is now professional-core enough for D18–D20, and if so whether the capability boundary should be stable (`speculative decoding acceptance/performance reasoning`) rather than vendor-method-specific.

### 3.5 MoE / expert parallel serving

Current authoritative source:
- vLLM Expert Parallel Deployment: https://docs.vllm.ai/en/latest/serving/expert_parallel_deployment/

The current serving documentation treats expert parallelism as an operational deployment mode for MoE models and integrates it with TP/DP and disaggregated serving.

Registry comparison:
- Tensor parallel sharding exists.
- No explicit `MoE`, `mixture of experts`, or `expert parallel` Skill was found.

**H-INF-03:** Determine whether MoE routing/expert-parallel serving is a critical missing professional capability for the target 4+ year AI-infrastructure route, or an optional later specialization.

### 3.6 Quantization — current low-precision formats

Current authoritative sources:
- TensorRT-LLM quantization: https://nvidia.github.io/TensorRT-LLM/1.1.0/features/quantization.html
- TensorRT-LLM current/RC quantization including NVFP4/MXFP4/FP8 KV cache: https://nvidia.github.io/TensorRT-LLM/1.3.0rc7/features/quantization.html
- TensorRT-LLM FP8 tuning: https://nvidia.github.io/TensorRT-LLM/performance/performance-tuning-guide/fp8-quantization.html

Registry comparison:
- `skill.optimization.quantization_format_reasoning`, quality validation, performance validation and quantized memory estimation already exist.

**H-INF-04:** Default assumption is **no new format-per-Skill explosion**. External evaluator should confirm that FP8/FP4/NVFP4/MXFP4 belong as freshness-scoped examples/technology metadata unless their operational evidence/remediation boundary truly differs from existing generic quantization Skills.

### 3.7 Triton / modern accelerator-kernel practice

Current authoritative sources:
- Triton Persistent Matmul: https://triton-lang.org/main/getting-started/tutorials/09-persistent-matmul.html
- Triton Warp Specialization: https://triton-lang.org/main/getting-started/tutorials/gluon/warp-specialization.html

Current tutorials exercise persistent kernels, tensor descriptors/TMA, warp specialization, profiling/autotuning and newer GPU-specific execution techniques.

Registry comparison:
- Core Triton kernel authoring, profiling/autotune and CUDA comparison exist.
- Exact `persistent`, `TMA`, and `warp specialization` capabilities were not found in the accepted Skill names/statements.

**H-GPU-01:** Determine whether these are current advanced examples under existing kernel/performance Skills, or whether at least one stable asynchronous-pipeline/persistent-kernel reasoning capability is missing. Do not create Hopper/Blackwell-only identity unless justified.

### 3.8 CUDA architecture evolution

Current authoritative source:
- CUDA Programming Guide 13.2: https://docs.nvidia.com/cuda/cuda-programming-guide/pdf/cuda-programming-guide.pdf

Current CUDA documents thread-block clusters and distributed shared memory for newer compute capabilities.

Registry comparison:
- Existing GPU/CUDA map covers warp execution, occupancy, memory, synchronization, launch configuration and performance reasoning.

**H-GPU-02:** Validate whether thread-block-cluster / distributed-shared-memory reasoning is an advanced required capability, version-sensitive enrichment, or unnecessary specialization for the target professional envelope.

### 3.9 NCCL communication evolution

Current authoritative sources:
- NCCL 2.31.2 environment/config documentation: https://docs.nvidia.com/deeplearning/nccl/user-guide/docs/env.html
- Current user-buffer/window registration documentation: https://docs.nvidia.com/deeplearning/nccl/user-guide/docs/usage/bufferreg.html

Modern NCCL exposes buffer registration, CUDA-graph registration, NVLS and window registration/zero-copy related behavior.

Registry comparison:
- Current map already includes collectives, topology/bandwidth, communication profiling, NCCL/runtime operation and GPUDirect/RDMA validation.

**H-NCCL-01:** Confirm whether buffer/window registration and NVLS are examples beneath existing performance/transport Skills or require a separate version-sensitive operational capability. Avoid mapping every NCCL knob into a Skill.

### 3.10 Linux production observability — eBPF and io_uring

Current authoritative sources:
- Linux kernel BPF docs: https://docs.kernel.org/bpf/
- Linux kernel current documentation includes modern io_uring networking/zero-copy facilities under networking docs.

Registry comparison:
- Existing systems map contains syscall tracing, kernel event observation, profiling, async/event-loop and I/O reasoning.
- Exact `eBPF/BPF` and `io_uring` Skills are absent.

**H-SYS-01:** Decide whether eBPF observability is now a required professional tooling Skill for systems/performance/AI-infra work or a version-sensitive tool specialization beneath kernel-event/profiling capabilities.

**H-SYS-02:** Decide whether io_uring needs a distinct capability or should remain a concrete Linux mechanism used to exercise existing async-I/O/event-loop/system-call reasoning.

### 3.11 OpenTelemetry current observability

Current authoritative sources:
- OpenTelemetry semantic conventions: https://opentelemetry.io/docs/specs/otel/semantic-conventions/
- Semantic conventions 1.44.0: https://opentelemetry.io/docs/specs/semconv/
- Profiles public alpha announcement: https://opentelemetry.io/blog/2026/profiles-alpha/
- GenAI observability: https://opentelemetry.io/blog/2026/genai-observability/

Registry comparison:
- Generic logs/metrics/traces and distributed tracing are represented.
- `OpenTelemetry` itself is not a canonical Skill identity, which is desirable for vendor/tool neutrality.

**H-OBS-01:** Verify that current semantic-convention/profiles/GenAI observability changes require only freshness-scoped content examples rather than new stable Skills; if a missing capability exists, prefer a stable `telemetry semantic normalization/correlation` behavior over an OpenTelemetry-name Skill.

### 3.12 Professional engineering — software supply-chain provenance/SBOM

Current authoritative sources:
- SLSA v1.2: https://slsa.dev/spec/v1.2/
- GitHub artifact attestations/provenance: https://docs.github.com/en/actions/how-tos/secure-your-work/use-artifact-attestations/use-artifact-attestations
- CNCF software supply-chain practices: https://contribute.cncf.io/projects/best-practices/security/supply-chain/supply-chain-best-practices/

Registry comparison:
- `skill.professional.artifact_integrity_verification` exists.
- `skill.professional.dependency_supply_chain_review` exists.
- Platform image supply-chain safety is also reused/compared.
- Explicit `SBOM` name is absent.

**H-PRO-01:** Likely no SBOM-only Skill should be created. Validate whether provenance/attestation/SBOM generation-verification can be expressed as evidence/tasks under existing artifact-integrity and dependency-supply-chain Skills, or whether build provenance attestation is independently observable/remediable enough to justify a split.

### 3.13 Weakness/remediation behavior — learning science

Current research sources:
- Rowland 2014 testing-effect meta-analysis: https://pubmed.ncbi.nlm.nih.gov/25150680/
- Mawson & Kang 2025 distributed-practice classroom meta-analysis: https://pubmed.ncbi.nlm.nih.gov/40564553/
- Metcalfe 2017 learning-from-errors review: https://pubmed.ncbi.nlm.nih.gov/27648988/
- van Gog, Paas & Sweller 2010 worked-example review: https://link.springer.com/article/10.1007/s10648-010-9145-4
- 2025 systematic review of learning from erroneous examples: https://link.springer.com/article/10.1007/s10648-025-10071-x

Registry/WLRM comparison:
- retrieval reinforcement exists,
- fresh independent recheck exists,
- worked-example reconstruction exists,
- misconception contrast exists,
- targeted reteach and micro-drill exist,
- retention is separately handled by RVR-v0,
- failure localization protects against contaminated evidence.

**H-REM-01:** Validate whether WLRM needs an explicit corrective-feedback/error-explanation strategy or whether current misconception contrast + targeted reteach + fresh recheck already represent the behavior without unnecessary strategy proliferation.

**H-REM-02:** Validate guidance fading / expertise reversal: worked examples are useful for novices but should not become a fixed remediation default for advanced learners. Determine whether strategy selection metadata needs a learner-readiness/guidance-fading guard.

## 4. Manager-side preliminary classification

These are **not final changes**.

### Strong external-review candidates
1. speculative decoding,
2. disaggregated prefill/decode + KV-transfer architecture,
3. MoE/expert-parallel serving,
4. Kubernetes accelerator DRA/resource claims,
5. workload/gang scheduling as a stable concept,
6. Triton modern asynchronous/persistent/warp-specialized kernel practice,
7. corrective-feedback / guidance-fading behavior in remediation.

### Likely freshness/content updates rather than new Skill identities
- concrete FP8/FP4/NVFP4/MXFP4 format matrix,
- specific NCCL registration knobs/NVLS/window APIs,
- OpenTelemetry-specific convention names,
- exact Kubernetes v1.36 alpha APIs,
- exact CUDA architecture/API names where existing stable execution-model Skills already cover the capability,
- SBOM as a named tool/artifact if existing provenance/supply-chain Skills can observe the behavior.

### Needs explicit independent judgment
- eBPF as required professional Skill vs tool-specific enrichment,
- io_uring as distinct Skill vs concrete async-I/O mechanism,
- thread-block clusters/DSM as required vs advanced specialization,
- capstone family diversity against actual professional AI-infrastructure expectations.

## 5. External evaluator must not anchor on this list

The independent evaluator must perform a **full D01–D23 scan**, not merely accept/reject the hypotheses above. It must be allowed to discover omissions that this manager-side audit missed and to reject false-positive gap candidates.
