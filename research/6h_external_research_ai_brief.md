# 6H External Research AI Brief — Independent Stage 6 Coverage / Prerequisite QA

## Role

You are the **independent external Research AI/evaluator** for step 6H of `xpike-dgm/ai-infra-learning-coach`.

You are not the author/manager of the curriculum. Do not assume the existing map is correct. Challenge it against current authoritative sources and professional practice.

Your output is required by repository decision D-016. The manager's own reasoning, code validation and web research **cannot substitute** for your independent report.

## Evaluation target

Stage 6 currently contains:
- D01–D23 route families,
- 543 accepted canonical Skills,
- 590 Learning Objectives,
- 930 prerequisite edges (836 hard / 94 soft),
- a combined hard DAG over 543/543 Skills,
- WLRM weakness/remediation coverage for all 543 Skills / 590 Objectives.

Canonical inputs to inspect:
- `docs/CURRICULUM_DOMAIN_MAP.md`
- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`
- `docs/GRANULARITY_NAMING_STANDARD.md`
- `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md`
- `docs/FOUNDATIONS_DETAILED_MAP.md`
- `docs/SYSTEMS_DETAILED_MAP.md`
- `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`
- `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`
- `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`
- all files under:
  - `curriculum/decomposition/6c_foundations/`
  - `curriculum/decomposition/6d_systems/`
  - `curriculum/decomposition/6e_gpu_ml_inference/`
  - `curriculum/decomposition/6f_professional_engineering/`
  - `curriculum/decomposition/6g_weakness_remediation/`
- `curriculum/decomposition/6h_research_qa/structural_report.yaml`

The manager-side source audit in `research/6h_current_source_audit.md` is **non-independent input only**. You may use it to understand hypotheses, but you must perform your own coverage search and may reject all of its hypotheses.

## Mandatory source policy

Use current authoritative/primary sources wherever possible:
- official language/tool/runtime documentation,
- Linux kernel docs,
- Kubernetes official docs/KEPs/release material,
- NVIDIA CUDA/NCCL/TensorRT-LLM documentation,
- Triton official docs,
- vLLM/SGLang official docs,
- standards/specifications (OCI, OpenTelemetry, SLSA, etc.),
- peer-reviewed/meta-analytic learning-science sources for WLRM behavior.

For each material finding provide:
1. source title,
2. URL,
3. publication/version/date if available,
4. which exact Skill/Objective/edge/review it affects,
5. why existing coverage is sufficient or insufficient.

Do not use generic SEO articles as the only support for a curriculum change.

## Required independent questions

### A. Full professional coverage D01–D23

For every route family D01–D23, answer:
- Are the accepted Skills sufficient for a 4+ year professional-readiness route toward systems/distributed/GPU/LLM-inference/AI-infrastructure engineering?
- Is a critical capability missing?
- Is anything clearly over-fragmented or duplicated?
- Is any capability obsolete or incorrectly elevated from tool detail into stable Skill identity?

Return an explicit D01–D23 matrix.

### B. Hidden prerequisite audit

Find capabilities that are assumed by later Skills but not represented as explicit hard/soft prerequisites.

Pay special attention to:
- math/numerical prerequisites for tensor/attention/quantization/performance reasoning,
- OS/memory/concurrency prerequisites for GPU/runtime behavior,
- networking/distributed prerequisites for NCCL/RDMA/disaggregated serving,
- Linux/platform prerequisites for production AI infrastructure,
- Technical English being accidentally used as an undeclared hard gate.

For every proposed prerequisite edge, explain why failure without the source Skill would make target teaching/evidence uninterpretable (`hard`) or merely harder (`soft`).

### C. Duplicate / granularity audit

Use semantic identity, not string similarity.

Check:
- duplicate Skills across languages/tools/Topics,
- context-only variants that should be reuse rather than clones,
- under-fragmented Skills where failure/remediation would still be too broad,
- micro-Skills whose independent learner state would never change a planner/remediation decision.

The structural validator currently reports same-name candidates:
- `skill.c.conditionals` vs `skill.python.conditionals`
- `skill.c.expression_evaluation` vs `skill.python.expression_evaluation`
- `skill.c.for_iteration` vs `skill.python.for_iteration`

Do not call these duplicates merely because names match; decide whether the language/runtime behavior changes the capability boundary.

### D. Current-industry / tool freshness audit

Independently inspect 2025–2026 current practice. At minimum evaluate whether the map correctly handles or omits:
- Kubernetes Dynamic Resource Allocation / accelerator resource claims,
- workload-aware / gang / topology-aware scheduling,
- CUDA 13.x-era thread-block clusters / distributed shared memory and relevant asynchronous execution evolution,
- Triton persistent/TMA/warp-specialized kernel practice,
- vLLM/SGLang/TensorRT-LLM modern serving behavior,
- speculative decoding,
- prefill/decode disaggregation and KV transfer,
- MoE / expert parallel deployment,
- KV cache paging/prefix reuse/offload/quantization,
- FP8/FP4-class low-precision formats without format-per-Skill overfitting,
- NCCL/RDMA/GPUDirect/NVLS/buffer-registration evolution,
- Linux eBPF/io_uring relevance,
- OpenTelemetry current traces/metrics/logs/profiles/GenAI conventions,
- container/image/software supply-chain provenance, attestations and SBOM practices.

Important: a current feature does **not** automatically deserve a new Skill. Classify tool/version details as freshness-scoped examples when stable capability identity already exists.

### E. Professional engineering / OSS / capstone diversity

Evaluate whether D23 has enough independent evidence for:
- code review and change isolation,
- testing/CI/release engineering,
- debugging and regression localization,
- profiling/benchmark experiment design,
- design/RFC/tradeoff reasoning,
- operations/reliability/runbooks/postmortems,
- supply-chain/security hygiene,
- upstream OSS contribution workflow,
- professional technical communication,
- multi-domain integrated capstones.

Assess whether the current capstone/project families are diverse enough to prevent one narrow project from masquerading as professional readiness.

### F. Weakness localization/remediation safety

Audit WLRM-v0 against learning science and evidence integrity.

Check especially:
- invalid/ambiguous/prerequisite-contaminated attempts not causing target negative evidence,
- assisted/provisional failures staying hypothesis-only,
- first post-mastery contradiction producing verification rather than instant mastery deletion,
- remediation closure requiring fresh independent evidence,
- no broad Topic/Domain reset from a local failure,
- integrated project outcome not broadcasting evidence to every component Skill,
- retrieval/spacing/feedback/worked-example/error-correction behavior,
- whether guidance fading / expertise reversal needs an explicit selection guard.

## Finding taxonomy

Every finding must use one of these classifications:
- `BLOCKING_MISSING_CAPABILITY`
- `BLOCKING_HIDDEN_PREREQUISITE`
- `BLOCKING_GRAPH_OR_EVIDENCE_SAFETY`
- `DUPLICATE_OR_OVERFRAGMENTED`
- `UNDERFRAGMENTED_CAPABILITY`
- `FRESHNESS_UPDATE`
- `NON_BLOCKING_OPTIONAL`
- `FALSE_POSITIVE_NO_CHANGE`

Each finding must include:
- finding ID,
- route family/domain,
- exact affected current IDs if any,
- proposed semantic change,
- hard/soft prerequisite implications,
- source evidence,
- confidence (`high | medium | low`),
- whether Stage 6 must change before acceptance.

## Required output structure

Return one self-contained report with:

1. `EXECUTIVE_VERDICT`
   - one of `PASS`, `PASS_WITH_REQUIRED_CHANGES`, `FAIL`, `BLOCKED`

2. `SOURCE_QUALITY_SUMMARY`
   - authoritative source count/type,
   - freshness window,
   - any unavailable/uncertain areas.

3. `D01_D23_COVERAGE_MATRIX`
   - one row per route family,
   - `adequate | change_required | optional_gap | uncertain`,
   - concise justification and sources.

4. `FINDINGS`
   - all findings using the taxonomy above.

5. `PREREQUISITE_AUDIT`
   - proposed add/remove/downgrade/upgrade edges with hard/soft justification.

6. `DUPLICATE_GRANULARITY_AUDIT`

7. `FRESHNESS_AUDIT`

8. `PROFESSIONAL_CAPSTONE_AUDIT`

9. `WLRM_BEHAVIOR_AUDIT`

10. `REVIEW_RESOLUTION_RECOMMENDATIONS`
    - explicitly address each of these review IDs:
      - `review.6c.external_coverage`
      - `review.6d.external_coverage`
      - `review.6d.platform_tool_freshness`
      - `review.6e.external_coverage`
      - `review.6e.tool_runtime_freshness`
      - `review.6e.math_numerical_coverage`
      - `review.6f.external_coverage`
      - `review.6f.oss_workflow_freshness`
      - `review.6f.capstone_diversity`
      - `review.6g.external_behavior_coverage`

11. `FINAL_REQUIRED_PATCH_SET`
    - exact minimum changes required before Stage 6 may close.

## Independence declaration

End with:

`INDEPENDENCE_DECLARATION`
- identify the external Research AI/service/model that produced the report,
- state that it was asked to independently challenge the existing map,
- state whether it relied on the manager's conclusions or independently verified them.

A report without this declaration or without current source evidence does not satisfy 6H.
