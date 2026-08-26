from __future__ import annotations

from copy import deepcopy
from pathlib import Path
from typing import Any
import yaml

ROOT = Path(__file__).resolve().parents[1]
TODAY = "2026-08-27"
EXT_SOURCE = "source.repo.external_6h_research_qa"
REPORT_REF = "research/6h_external_research_ai_report.md"

PKGS = {
    "6c": ROOT / "curriculum/decomposition/6c_foundations",
    "6d": ROOT / "curriculum/decomposition/6d_systems",
    "6e": ROOT / "curriculum/decomposition/6e_gpu_ml_inference",
    "6f": ROOT / "curriculum/decomposition/6f_professional_engineering",
    "6g": ROOT / "curriculum/decomposition/6g_weakness_remediation",
}


def load(pkg: str, name: str) -> Any:
    return yaml.safe_load((PKGS[pkg] / name).read_text(encoding="utf-8"))


def dump(pkg: str, name: str, data: Any) -> None:
    (PKGS[pkg] / name).write_text(
        yaml.safe_dump(data, sort_keys=False, allow_unicode=True, width=120), encoding="utf-8"
    )


def uniq(values: list[str]) -> list[str]:
    out: list[str] = []
    for value in values:
        if value not in out:
            out.append(value)
    return out


def ensure_external_source(pkg: str) -> None:
    if not (PKGS[pkg] / "sources.yaml").exists():
        return
    sources = load(pkg, "sources.yaml")
    if not any(x.get("source_id") == EXT_SOURCE for x in sources):
        sources.append({
            "source_id": EXT_SOURCE,
            "source_kind": "independent_external_research_qa",
            "title": "6H Multi-Evaluator External Research QA Reconciliation",
            "ref": REPORT_REF,
            "version_or_date": TODAY,
            "authority_class": "independent_external_validation_reconciled",
            "supports": ["external_coverage", "hidden_prerequisites", "freshness", "granularity", "professional_evidence"],
            "freshness_class": "versioned_dataset",
            "checked_at": TODAY,
            "notes": "Three independent reports returned PASS WITH REQUIRED CHANGES; manager reconciliation preserves canonical identity rules.",
        })
        dump(pkg, "sources.yaml", sources)

    manifest_path = PKGS[pkg] / "manifest.yaml"
    if manifest_path.exists():
        manifest = load(pkg, "manifest.yaml")
        refs = manifest.get("source_catalog_refs")
        if isinstance(refs, list):
            manifest["source_catalog_refs"] = uniq(refs + [EXT_SOURCE])
        dump(pkg, "manifest.yaml", manifest)


def find(rows: list[dict[str, Any]], key: str, value: str) -> dict[str, Any]:
    for row in rows:
        if row.get(key) == value:
            return row
    raise KeyError(f"missing {key}={value}")


def resolve_review(pkg: str, review_id: str, resolution: str) -> None:
    reviews = load(pkg, "review_queue.yaml")
    row = find(reviews, "review_id", review_id)
    row["status"] = "resolved"
    row["resolution"] = resolution
    row["resolved_at"] = TODAY
    row["resolution_refs"] = uniq(list(row.get("resolution_refs") or []) + [REPORT_REF, "6H"])
    if "source_refs" in row:
        row["source_refs"] = uniq(list(row.get("source_refs") or []) + [EXT_SOURCE])
    dump(pkg, "review_queue.yaml", reviews)


def objective_ids_for(pkg: str, skill_id: str) -> list[str]:
    return [x["objective_id_candidate"] for x in load(pkg, "objectives.yaml") if x["owner_skill_id"] == skill_id]


def add_objective(
    pkg: str,
    owner_skill_id: str,
    slug: str,
    statement: str,
    *,
    template_owner: str | None = None,
    criticality: str | None = None,
    freshness_class: str | None = None,
    transfer_context: str | None = None,
) -> str:
    objectives = load(pkg, "objectives.yaml")
    oid = f"objective.{owner_skill_id.removeprefix('skill.')}.{slug}"
    if any(x["objective_id_candidate"] == oid for x in objectives):
        return oid
    template_skill = template_owner or owner_skill_id
    template = next(x for x in objectives if x["owner_skill_id"] == template_skill)
    row = deepcopy(template)
    row["objective_id_candidate"] = oid
    row["owner_skill_id"] = owner_skill_id
    row["objective_statement"] = statement
    row["observable_action"] = slug
    row["source_refs"] = uniq(list(row.get("source_refs") or []) + [EXT_SOURCE])
    row["provenance_ref"] = EXT_SOURCE
    row["review_refs"] = []
    if criticality:
        row["criticality"] = criticality
    if freshness_class:
        row["freshness_class"] = freshness_class
    if transfer_context:
        row.setdefault("retention_requirement", {})["context_diversity_requirement"] = transfer_context
    objectives.append(row)
    dump(pkg, "objectives.yaml", objectives)
    return oid


def add_skill(
    pkg: str,
    *,
    skill_id: str,
    canonical_name: str,
    statement: str,
    template_skill_id: str,
    primary_topic: str,
    capability_kind: str,
    freshness_class: str,
    technologies: list[str],
    professional_tags: list[str],
    critical_prerequisite: bool,
    objective_slug: str = "demonstrate_capability",
    objective_statement: str | None = None,
) -> str:
    skills = load(pkg, "skills.yaml")
    if any(x["skill_id_candidate"] == skill_id for x in skills):
        return objective_ids_for(pkg, skill_id)[0]
    template = find(skills, "skill_id_candidate", template_skill_id)
    row = deepcopy(template)
    row["skill_id_candidate"] = skill_id
    row["canonical_name"] = canonical_name
    row["capability_statement"] = statement
    row["capability_kind"] = capability_kind
    row["primary_teaching_topic_id"] = primary_topic
    row["linked_topic_ids"] = []
    row["shared_placement_domain_ids"] = list(template["shared_placement_domain_ids"])
    row["independent_evidence_path"]["example_outcome"] = statement
    row["boundaries"] = {
        "prerequisite_boundary": "6H reconciled hard/soft prerequisites define the fair and interpretable evidence boundary.",
        "remediation_boundary": "Failure is remediated at this exact capability; adjacent tool/version details do not reset the broad Domain.",
        "reuse_boundary": "Stable behavior keeps one canonical Skill; vendor/API variants remain version-scoped examples/objectives.",
    }
    row["critical_prerequisite_candidate"] = critical_prerequisite
    row["professional_capability_tags"] = professional_tags
    row["source_refs"] = uniq(list(row.get("source_refs") or []) + [EXT_SOURCE])
    row["provenance_ref"] = EXT_SOURCE
    row["freshness_class"] = freshness_class
    row["technology_dependency_refs"] = technologies
    row["duplicate_resolution"] = {
        "status": "create_new",
        "compared_skill_ids": [template_skill_id],
        "rationale": "Independent 6H external QA found a separately diagnosable capability boundary not covered by the accepted registry.",
    }
    row["granularity_review_codes"] = uniq(["GRANULARITY_OK"] + list(row.get("granularity_review_codes") or []))
    row["review_refs"] = []
    skills.append(row)
    dump(pkg, "skills.yaml", skills)

    oid = add_objective(
        pkg,
        skill_id,
        objective_slug,
        objective_statement or statement,
        template_owner=template_skill_id,
        criticality="critical" if critical_prerequisite else "standard",
        freshness_class=freshness_class,
        transfer_context="different_hardware_workload_or_shape" if "cuda." in skill_id or "multi_gpu." in skill_id else "fresh_context",
    )

    links = load(pkg, "topic_skill_links.yaml")
    template_link = next(x for x in links if x["skill_id"] == template_skill_id and x.get("is_primary_teaching_context"))
    link = deepcopy(template_link)
    link["topic_id"] = primary_topic
    link["skill_id"] = skill_id
    link["objective_scope_ids"] = [oid]
    link["is_primary_teaching_context"] = True
    link["source_refs"] = uniq(list(link.get("source_refs") or []) + [EXT_SOURCE]) if "source_refs" in link else [EXT_SOURCE]
    link["review_refs"] = []
    links.append(link)
    dump(pkg, "topic_skill_links.yaml", links)

    requirements = load(pkg, "capability_requirements.yaml")
    template_requirements = [x for x in requirements if x.get("capability_id") == template_skill_id]
    for req in template_requirements:
        new_req = deepcopy(req)
        new_req["capability_id"] = skill_id
        new_req["source_refs"] = uniq(list(new_req.get("source_refs") or []) + [EXT_SOURCE])
        new_req["review_refs"] = []
        requirements.append(new_req)
    dump(pkg, "capability_requirements.yaml", requirements)

    professional = load(pkg, "professional_attributions.yaml")
    template_prof = [x for x in professional if x.get("capability_id") == template_skill_id]
    for prof in template_prof:
        new_prof = deepcopy(prof)
        new_prof["capability_id"] = skill_id
        new_prof["source_refs"] = uniq(list(new_prof.get("source_refs") or []) + [EXT_SOURCE])
        new_prof["review_refs"] = []
        professional.append(new_prof)
    dump(pkg, "professional_attributions.yaml", professional)
    return oid


def add_edge(pkg: str, source: str, target: str, kind: str, reason_kind: str, rationale: str) -> None:
    edges = load(pkg, "prerequisite_edges.yaml")
    if any(x["prerequisite_skill_id"] == source and x["target_skill_id"] == target for x in edges):
        return
    local_skills = {x["skill_id_candidate"] for x in load(pkg, "skills.yaml")}
    template = deepcopy(edges[0])
    template["prerequisite_skill_id"] = source
    template["target_skill_id"] = target
    template["edge_kind"] = kind
    template["reason_kind"] = reason_kind
    template["authoring_rationale"] = rationale
    template["contamination_risk_if_missing"] = "high" if kind == "hard" else "medium"
    template["hard_soft_test_result"] = "6H_external_research_reconciled"
    template["task_specific_instead_of_graph_edge"] = False
    template["cross_package_ref"] = source not in local_skills
    template["source_refs"] = uniq(["source.repo.prg_v0", EXT_SOURCE])
    template["provenance_ref"] = EXT_SOURCE
    template["lifecycle_status"] = "draft"
    template["review_status"] = "reviewed"
    template["review_refs"] = []
    edges.append(template)
    dump(pkg, "prerequisite_edges.yaml", edges)


def add_project_attribution(pkg: str, skill_id: str, objective_id: str, evidence_type: str) -> None:
    path = PKGS[pkg] / "project_capstone_attributions.yaml"
    if not path.exists():
        return
    rows = load(pkg, "project_capstone_attributions.yaml")
    if any(x.get("skill_id") == skill_id for x in rows):
        return
    template = deepcopy(rows[0])
    template["project_or_capstone_id"] = "project.gpu_inference.integrated_serving_stack"
    template["skill_id"] = skill_id
    template["objective_id"] = objective_id
    template["role"] = "supporting"
    template["structurally_essential"] = True
    template["separately_observable"] = True
    template["expected_evidence_type"] = evidence_type
    template["rubric_component_ref"] = "rubric.gpu_inference.6h." + skill_id.removeprefix("skill.").replace(".", "_")
    template["source_refs"] = uniq(list(template.get("source_refs") or []) + [EXT_SOURCE])
    template["review_refs"] = []
    rows.append(template)
    dump(pkg, "project_capstone_attributions.yaml", rows)


def refresh_package_status(pkg: str) -> None:
    manifest_path = PKGS[pkg] / "manifest.yaml"
    review_path = PKGS[pkg] / "review_queue.yaml"
    if not manifest_path.exists() or not review_path.exists():
        return
    reviews = load(pkg, "review_queue.yaml")
    open_reviews = [x for x in reviews if x.get("status") == "open"]
    manifest = load(pkg, "manifest.yaml")
    manifest["status"] = "authoring_complete_external_qa" if pkg != "6g" else manifest.get("status")
    if "unresolved_review_count" in manifest:
        manifest["unresolved_review_count"] = len(open_reviews)
    if "blocking_review_count" in manifest:
        manifest["blocking_review_count"] = len([x for x in open_reviews if x.get("severity") == "blocking"])
    if "coverage_declarations" in manifest:
        for row in manifest["coverage_declarations"]:
            row["external_validation"] = "validated_6H"
    if "external_research_qa" in manifest:
        manifest["external_research_qa"] = {"status": "validated_6H", "owner_step": "6H"}
    dump(pkg, "manifest.yaml", manifest)

    qa_path = PKGS[pkg] / "qa_report.yaml"
    if qa_path.exists():
        qa = load(pkg, "qa_report.yaml")
        qa["checked_at"] = TODAY
        qa["result"] = "PASS" if not open_reviews else "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS"
        qa["external_research_qa"] = {"status": "validated_6H", "owner_step": "6H", "report_ref": REPORT_REF}
        counts = qa.get("counts") or {}
        collection_count_map = {
            "skills": "skills.yaml", "objectives": "objectives.yaml", "topic_skill_links": "topic_skill_links.yaml",
            "prerequisite_edges": "prerequisite_edges.yaml", "capability_requirements": "capability_requirements.yaml",
            "professional_attributions": "professional_attributions.yaml", "project_attributions": "project_capstone_attributions.yaml",
        }
        for count_key, filename in collection_count_map.items():
            if count_key in counts and (PKGS[pkg] / filename).exists():
                counts[count_key] = len(load(pkg, filename))
        if "hard_prerequisite_edges" in counts:
            edges = load(pkg, "prerequisite_edges.yaml")
            counts["hard_prerequisite_edges"] = len([x for x in edges if x["edge_kind"] == "hard"])
        if "soft_prerequisite_edges" in counts:
            edges = load(pkg, "prerequisite_edges.yaml")
            counts["soft_prerequisite_edges"] = len([x for x in edges if x["edge_kind"] == "soft"])
        if "open_non_blocking_reviews" in counts:
            counts["open_non_blocking_reviews"] = len([x for x in open_reviews if x.get("severity") != "blocking"])
        if "open_blocking_reviews" in counts:
            counts["open_blocking_reviews"] = len([x for x in open_reviews if x.get("severity") == "blocking"])
        qa["counts"] = counts
        dump(pkg, "qa_report.yaml", qa)


def patch_6c() -> None:
    ensure_external_source("6c")
    skills = load("6c", "skills.yaml")
    row = find(skills, "skill_id_candidate", "skill.python.thread_process_choice")
    row["freshness_class"] = "version_sensitive"
    row["technology_dependency_refs"] = uniq(list(row.get("technology_dependency_refs") or []) + ["technology.python_runtime"])
    row["source_refs"] = uniq(list(row.get("source_refs") or []) + [EXT_SOURCE])
    dump("6c", "skills.yaml", skills)
    add_objective(
        "6c", "skill.python.thread_process_choice", "runtime_mode_tradeoff",
        "Python runtime mode/GIL-free-threaded davranışı dahil workload özelliklerine göre thread, process veya async seçimini gerekçelendirebilmek.",
        freshness_class="version_sensitive", transfer_context="different_runtime_or_workload",
    )
    resolve_review("6c", "review.6c.external_coverage", "Independent 6H reports found no blocking D01-D05 semantic gap; Python runtime concurrency assumptions are version-sensitive and eBPF/io_uring remain optional contextual examples.")
    refresh_package_status("6c")


def patch_6d() -> None:
    ensure_external_source("6d")
    numa_oid = add_skill(
        "6d",
        skill_id="skill.os.numa_locality_affinity",
        canonical_name="NUMA Locality & Affinity",
        statement="Process/thread placement, NUMA-local/remote memory locality ve device affinity ilişkisini topology ve ölçüm verisiyle açıklayıp doğru placement seçebilmek.",
        template_skill_id="skill.os.context_switch_cost",
        primary_topic="topic.os.virtual_memory_paging",
        capability_kind="measurement_reasoning",
        freshness_class="evergreen",
        technologies=[],
        professional_tags=["performance_reasoning", "systems_design", "profiling_benchmarking"],
        critical_prerequisite=True,
    )
    add_edge("6d", "skill.arch.cache_hierarchy_model", "skill.os.numa_locality_affinity", "hard", "performance_reasoning_dependency", "Memory hierarchy/locality is required to interpret local-vs-remote NUMA cost.")
    add_edge("6d", "skill.os.virtual_physical_translation", "skill.os.numa_locality_affinity", "hard", "evidence_interpretability", "NUMA memory placement cannot be interpreted without virtual/physical translation and locality.")
    add_edge("6d", "skill.os.scheduler_model", "skill.os.numa_locality_affinity", "soft", "conceptual_dependency", "CPU/thread affinity informs NUMA placement but basic locality can be taught independently.")

    add_objective(
        "6d", "skill.platform.scheduling_placement_constraints", "accelerator_claim_topology",
        "Resource-claim/device-class tarzı accelerator request modelinde device properties, topology domain ve all-or-nothing workload placement kısıtlarını gerekçelendirebilmek.",
        freshness_class="version_sensitive", transfer_context="different_orchestrator_version_or_cluster",
    )
    # Keep the stable platform Skill; DRA/Kueue API names are implementation examples, not new Skill identity.
    skills = load("6d", "skills.yaml")
    sched = find(skills, "skill_id_candidate", "skill.platform.scheduling_placement_constraints")
    sched["freshness_class"] = "version_sensitive"
    sched["technology_dependency_refs"] = uniq(list(sched.get("technology_dependency_refs") or []) + ["technology.container_orchestrator"])
    sched["source_refs"] = uniq(list(sched.get("source_refs") or []) + [EXT_SOURCE])
    dump("6d", "skills.yaml", skills)

    resolve_review("6d", "review.6d.external_coverage", "Independent 6H QA identified one required hidden prerequisite: explicit NUMA locality/affinity. D06-D07/D09-D13 core coverage is otherwise sufficient for the target route.")
    resolve_review("6d", "review.6d.platform_tool_freshness", "Stable scheduling/placement capability retained; Kubernetes DRA/ResourceClaim and topology/all-or-nothing scheduling are version-scoped objectives/examples with freshness metadata.")
    refresh_package_status("6d")


def patch_6e() -> None:
    ensure_external_source("6e")

    async_oid = add_skill(
        "6e", skill_id="skill.cuda.async_data_movement_pipeline", canonical_name="CUDA Async Data Movement Pipeline",
        statement="Kernel içindeki tiled asynchronous data movement, staged producer/consumer dependency ve copy-compute overlap'i correctness ve ölçülmüş performance ile doğrulayabilmek.",
        template_skill_id="skill.cuda.shared_memory_tiling", primary_topic="topic.cuda.shared_memory_tiling",
        capability_kind="tool_specific_production", freshness_class="version_sensitive", technologies=["technology.cuda_toolkit"],
        professional_tags=["performance_reasoning", "concurrency", "profiling_benchmarking"], critical_prerequisite=True,
    )
    speculative_oid = add_skill(
        "6e", skill_id="skill.optimization.speculative_decoding_tradeoff", canonical_name="Speculative Decoding Trade-off",
        statement="Draft/proposal ve target verification akışını correctness, acceptance behavior, additional compute/memory ve latency/throughput trade-off'larıyla değerlendirebilmek.",
        template_skill_id="skill.optimization.batch_composition_tradeoff", primary_topic="topic.optimization.token_scheduler",
        capability_kind="design_reasoning", freshness_class="evergreen", technologies=[],
        professional_tags=["performance_reasoning", "systems_design"], critical_prerequisite=False,
    )
    disagg_oid = add_skill(
        "6e", skill_id="skill.serving.prefill_decode_disaggregation", canonical_name="Prefill / Decode Disaggregation",
        statement="Prefill ve decode'u ayrı worker/resource pools'a ayırıp KV-state handoff, routing, transfer cost, SLO ve failure semantics açısından doğru deployment tasarlayabilmek.",
        template_skill_id="skill.serving.worker_engine_topology", primary_topic="topic.serving.engine_worker_topology",
        capability_kind="design_reasoning", freshness_class="evergreen", technologies=[],
        professional_tags=["systems_design", "reliability", "performance_reasoning"], critical_prerequisite=True,
    )
    moe_oid = add_skill(
        "6e", skill_id="skill.ml.moe_routing_dataflow", canonical_name="MoE Routing Dataflow",
        statement="Router, top-k expert selection, token dispatch, expert compute ve combine dataflow'unu tensor shape/state ve load distribution ile açıklayabilmek.",
        template_skill_id="skill.ml.transformer_block_dataflow", primary_topic="topic.ml.transformer_block_causal_lm",
        capability_kind="systems_reasoning", freshness_class="evergreen", technologies=[],
        professional_tags=["systems_design", "performance_reasoning"], critical_prerequisite=True,
    )
    ep_oid = add_skill(
        "6e", skill_id="skill.multi_gpu.expert_parallel_sharding", canonical_name="Expert Parallel Sharding",
        statement="Expert weights ve token traffic'i GPU/node'lar arasında shard edip communication, topology, load-imbalance ve failure trade-off'larını değerlendirebilmek.",
        template_skill_id="skill.multi_gpu.tensor_parallel_sharding", primary_topic="topic.multi_gpu.parallelism_sharding",
        capability_kind="design_reasoning", freshness_class="evergreen", technologies=[],
        professional_tags=["systems_design", "performance_reasoning", "distributed_systems"], critical_prerequisite=True,
    )

    # Cross-package hidden prerequisites and new stable-capability edges.
    add_edge("6e", "skill.os.numa_locality_affinity", "skill.multi_gpu.topology_discovery", "hard", "evidence_interpretability", "Topology discovery explicitly requires interpretable NUMA locality/affinity reasoning.")
    add_edge("6e", "skill.os.numa_locality_affinity", "skill.serving.worker_resource_binding", "soft", "performance_reasoning_dependency", "NUMA/device locality improves resource binding decisions without globally blocking basic serving topology.")
    add_edge("6e", "skill.platform.scheduling_placement_constraints", "skill.ai_infra.topology_aware_placement", "hard", "professional_workflow_dependency", "Production cluster placement requires orchestrator-level resource and topology constraint reasoning.")
    add_edge("6e", "skill.cuda.shared_memory_tiling", "skill.cuda.async_data_movement_pipeline", "hard", "procedural_dependency", "Staged in-kernel pipelines require correct shared-memory tiling and buffer reuse.")
    add_edge("6e", "skill.cuda.memory_visibility_ordering", "skill.cuda.async_data_movement_pipeline", "hard", "safety_dependency", "Asynchronous producer/consumer pipelines require correct visibility and ordering semantics.")
    add_edge("6e", "skill.gpu.copy_compute_overlap_model", "skill.cuda.async_data_movement_pipeline", "hard", "performance_reasoning_dependency", "Pipeline performance evidence requires a correct copy/compute overlap model.")
    add_edge("6e", "skill.inference.autoregressive_loop", "skill.optimization.speculative_decoding_tradeoff", "hard", "conceptual_dependency", "Speculation accelerates the autoregressive progression and cannot be interpreted without it.")
    add_edge("6e", "skill.inference.prefill_decode_distinction", "skill.serving.prefill_decode_disaggregation", "hard", "conceptual_dependency", "The two phases must be understood before they can be separated across resources.")
    add_edge("6e", "skill.inference.kv_cache_semantics", "skill.serving.prefill_decode_disaggregation", "hard", "evidence_interpretability", "KV ownership/handoff is central to disaggregated serving correctness.")
    add_edge("6e", "skill.serving.worker_engine_topology", "skill.serving.prefill_decode_disaggregation", "hard", "professional_workflow_dependency", "Disaggregation is a worker/engine topology design behavior.")
    add_edge("6e", "skill.network.latency_bandwidth_budget", "skill.serving.prefill_decode_disaggregation", "hard", "performance_reasoning_dependency", "KV transfer cost cannot be assessed without latency/bandwidth budgeting.")
    add_edge("6e", "skill.distributed.partial_failure_model", "skill.serving.prefill_decode_disaggregation", "soft", "conceptual_dependency", "Distributed failure semantics support production recovery reasoning.")
    add_edge("6e", "skill.multi_gpu.rdma_transport_model", "skill.serving.prefill_decode_disaggregation", "soft", "tool_environment_dependency", "RDMA is a relevant implementation path but is not required by the stable disaggregation concept.")
    add_edge("6e", "skill.ml.transformer_block_dataflow", "skill.ml.moe_routing_dataflow", "hard", "conceptual_dependency", "MoE is a transformer-block variant and requires the dense block/dataflow foundation.")
    add_edge("6e", "skill.ml.moe_routing_dataflow", "skill.multi_gpu.expert_parallel_sharding", "hard", "evidence_interpretability", "Expert-parallel token communication derives from router/expert semantics.")
    add_edge("6e", "skill.multi_gpu.collective_semantics", "skill.multi_gpu.expert_parallel_sharding", "hard", "conceptual_dependency", "Expert parallelism requires multi-device collective/data movement reasoning.")
    add_edge("6e", "skill.multi_gpu.topology_bandwidth_model", "skill.multi_gpu.expert_parallel_sharding", "soft", "performance_reasoning_dependency", "Topology/bandwidth is needed for advanced production EP optimization but not the first semantic model.")

    # CUDA cluster/DSM is retained as a separately observable objective under memory visibility/ordering after GNS-v0 review.
    add_objective(
        "6e", "skill.cuda.memory_visibility_ordering", "cluster_scope_execution_memory",
        "Thread-block cluster synchronization ve distributed-shared-memory scope/lifetime/visibility davranışını architecture/tool version bağlamında doğru reason edebilmek.",
        freshness_class="version_sensitive", transfer_context="different_gpu_architecture_or_toolkit",
    )
    # Modern Triton mechanisms are implementation examples of existing performance/scheduling capability, not new permanent Skills.
    add_objective(
        "6e", "skill.triton.matmul_performance_reasoning", "modern_pipeline_schedule_tradeoff",
        "Persistent execution, asynchronous/software pipelining, TMA-benzeri movement ve warp/work specialization seçeneklerini resource, synchronization ve measured performance trade-off'larıyla değerlendirebilmek.",
        freshness_class="version_sensitive", transfer_context="different_gpu_shape_or_triton_version",
    )
    add_objective(
        "6e", "skill.optimization.kv_capacity_model", "tiered_kv_offload_tradeoff",
        "GPU/CPU/secondary-storage KV offload veya tiering seçimini capacity, transfer cost ve latency etkisiyle değerlendirebilmek.",
        freshness_class="version_sensitive", transfer_context="different_runtime_or_memory_tier",
    )
    add_objective(
        "6e", "skill.optimization.quantization_format_reasoning", "low_precision_format_family_tradeoff",
        "Weight/activation/KV için FP8/FP4-class ve block-scaled format ailelerini memory, compute, scale metadata ve quality etkisiyle karşılaştırabilmek.",
        freshness_class="version_sensitive", transfer_context="different_hardware_or_format_family",
    )
    add_objective(
        "6e", "skill.optimization.token_budget_scheduler", "chunked_prefill_tradeoff",
        "Chunked prefill benzeri scheduling davranışını TTFT/ITL, decode interference, token budget ve memory baskısı üzerinden değerlendirebilmek.",
        freshness_class="version_sensitive", transfer_context="different_runtime_or_workload_mix",
    )
    add_objective(
        "6e", "skill.multi_gpu.rdma_transport_model", "registration_zero_copy_lifecycle",
        "Registered/dma-buf-benzeri zero-copy path ile staged transport arasında buffer registration lifecycle ve portability trade-off'larını ayırabilmek.",
        freshness_class="version_sensitive", transfer_context="different_rdma_stack_or_gpu_runtime",
    )
    add_objective(
        "6e", "skill.multi_gpu.nccl_collective_operation", "topology_current_algorithm_behavior",
        "Current NCCL runtime'da topology, registration ve NVLS-benzeri acceleration seçeneklerinin collective lifecycle/performance üzerindeki etkisini version-scoped kanıtla değerlendirebilmek.",
        freshness_class="version_sensitive", transfer_context="different_nccl_version_or_topology",
    )
    add_objective(
        "6e", "skill.ai_infra.topology_aware_placement", "orchestrated_accelerator_claim_placement",
        "Cluster scheduler/resource-claim modelinde rack/node/NUMA/device topology seviyelerini workload communication ve failure-domain constraints ile eşleştirebilmek.",
        freshness_class="version_sensitive", transfer_context="different_cluster_scheduler_or_topology",
    )

    for sid, oid, ev in [
        ("skill.cuda.async_data_movement_pipeline", async_oid, "authored_code"),
        ("skill.optimization.speculative_decoding_tradeoff", speculative_oid, "design_argument"),
        ("skill.serving.prefill_decode_disaggregation", disagg_oid, "design_argument"),
        ("skill.ml.moe_routing_dataflow", moe_oid, "explanation"),
        ("skill.multi_gpu.expert_parallel_sharding", ep_oid, "design_argument"),
    ]:
        add_project_attribution("6e", sid, oid, ev)

    resolve_review("6e", "review.6e.external_coverage", "Three independent external reports converged on required NUMA-linked GPU topology, CUDA async pipeline, speculative decoding, prefill/decode disaggregation, MoE routing and expert-parallel coverage. Stable capabilities/edges were added without vendor-specific Skill proliferation.")
    resolve_review("6e", "review.6e.tool_runtime_freshness", "Current CUDA/Triton/serving/NCCL/RDMA details are represented as version-sensitive objectives/examples unless they pass the stable capability independence test; tool/API names do not replace stable concept identity.")
    resolve_review("6e", "review.6e.math_numerical_coverage", "Existing tensor shape, matrix multiplication, probability/softmax, floating-point error and mixed-precision capabilities are sufficient for the inference-systems target; no calculus/advanced-math hard route added. Existing quantization hard-edge guard remains enforced.")
    refresh_package_status("6e")


def patch_6f() -> None:
    ensure_external_source("6f")
    # External duplicate candidates were semantically reviewed. Keep them: their lifecycle/evidence boundaries differ.
    skills = load("6f", "skills.yaml")
    comparisons = {
        "skill.professional.oss_patch_scope": ["skill.professional.pr_scope_discipline"],
        "skill.professional.oss_submission_evidence": ["skill.professional.pr_problem_solution_evidence"],
    }
    for sid, compared in comparisons.items():
        row = find(skills, "skill_id_candidate", sid)
        row["duplicate_resolution"] = {
            "status": "create_new",
            "compared_skill_ids": compared,
            "rationale": "6H semantic comparison retained separate identity: OSS issue/patch readiness and generic PR communication/scope occur at different lifecycle/evidence/remediation boundaries.",
        }
        row["source_refs"] = uniq(list(row.get("source_refs") or []) + [EXT_SOURCE])
    maint = find(skills, "skill_id_candidate", "skill.professional.oss_maintainer_feedback_iteration")
    maint["duplicate_resolution"] = {
        "status": "create_new",
        "compared_skill_ids": [],
        "rationale": "No equivalent generic canonical review-iteration Skill exists with the same upstream maintainer/repository-norm workflow boundary.",
    }
    maint["source_refs"] = uniq(list(maint.get("source_refs") or []) + [EXT_SOURCE])
    dump("6f", "skills.yaml", skills)

    add_objective(
        "6f", "skill.professional.dependency_supply_chain_review", "distinguish_sbom_provenance_attestation",
        "SBOM/component inventory, build provenance ve signed/verified attestation kanıtlarının farklı soruları cevapladığını ayırıp release/dependency risk incelemesinde doğru kanıtı kullanabilmek.",
        freshness_class="version_sensitive", transfer_context="different_supply_chain_standard_or_project",
    )
    add_objective(
        "6f", "skill.professional.oss_contribution_policy", "live_policy_discovery",
        "Hedef upstream repository'nin güncel CONTRIBUTING/governance/test/review politikasını canlı authoritative kaynaktan bulup contribution planına uygulayabilmek.",
        freshness_class="version_sensitive", transfer_context="different_upstream_repository",
    )

    # Make professional-readiness evidence diversity explicit as an additional durable artifact; no global capstone pass broadcasting.
    guard_path = PKGS["6f"] / "external_qa_evidence_guards.yaml"
    guards = {
        "model": "6H-professional-evidence-guard-v0",
        "rules": [
            {"guard_id": "guard.capstone_no_global_mastery", "rule": "A global project/capstone pass cannot grant mastery to tagged component Skills/Objectives."},
            {"guard_id": "guard.component_attribution_required", "rule": "Readiness evidence must be separately attributable to structurally essential component Objectives."},
            {"guard_id": "guard.three_evidence_families", "rule": "Final professional readiness requires fresh/direct evidence from at least three materially distinct families: systems/service, GPU/inference, production/professional."},
            {"guard_id": "guard.operations_failure_evidence", "rule": "At least one professional-readiness evidence artifact must involve failure injection, incident reconstruction, or operational diagnosis/recovery."},
        ],
        "source_refs": [EXT_SOURCE, "source.repo.professional_readiness"],
    }
    guard_path.write_text(yaml.safe_dump(guards, sort_keys=False, allow_unicode=True, width=120), encoding="utf-8")

    resolve_review("6f", "review.6f.external_coverage", "Independent 6H reports found D23 breadth strong. Three suspected OSS context clones were semantically rechecked and retained because lifecycle/evidence/remediation boundaries differ; supply-chain evidence distinctions were strengthened.")
    resolve_review("6f", "review.6f.oss_workflow_freshness", "Repository-specific contribution policy is treated as live/versioned source material under a stable policy-discovery capability, not frozen as one platform's workflow.")
    resolve_review("6f", "review.6f.capstone_diversity", "Existing project diversity retained; external QA adds non-compensatory component evidence, >=3 materially distinct evidence families, and operations/failure evidence guards.")
    refresh_package_status("6f")


def patch_6g_if_present() -> None:
    if not (PKGS["6g"] / "manifest.yaml").exists():
        return
    # 6G is regenerated after technical patches so it sees the final Skill/Objective registry.
    reviews = load("6g", "review_queue.yaml")
    row = find(reviews, "review_id", "review.6g.external_behavior_coverage")
    row["status"] = "resolved"
    row["resolution"] = "Independent learning-science QA corroborates WLRM core safety and requires explicit expertise-aware guidance fading; AI assistance remains non-closure evidence. Executable guard scenarios added by 6H."
    row["resolved_at"] = TODAY
    row["resolution_refs"] = [REPORT_REF, "6H"]
    dump("6g", "review_queue.yaml", reviews)

    state = load("6g", "state_contract.yaml")
    invariants = state.setdefault("invariants", [])
    for inv in [
        "guidance_must_fade_as_verified_competence_and_transfer_evidence_accumulate",
        "previously_mastered_target_defaults_to_verification_retrieval_transfer_or_diagnosis_before_full_reteach",
        "ai_assistance_may_scaffold_remediation_but_never_satisfies_independent_closure_evidence",
    ]:
        if inv not in invariants:
            invariants.append(inv)
    state["external_research_qa_ref"] = REPORT_REF
    dump("6g", "state_contract.yaml", state)

    guards = load("6g", "aggregation_guards.yaml")
    existing = {x["guard_id"] for x in guards["rules"]}
    new_guards = [
        {"guard_id": "guard.guidance_fading", "rule": "High guidance is for novice/prerequisite repair; as verified success/transfer accumulate, hints/worked examples fade toward retrieval, transfer and independent problem solving."},
        {"guard_id": "guard.mastered_target_reverification_first", "rule": "A previously mastered target defaults to fresh verification/retrieval/transfer/debug localization before full worked-example reteaching."},
        {"guard_id": "guard.ai_scaffold_not_closure", "rule": "AI assistance may scaffold remediation but cannot satisfy fresh H0 independent closure evidence."},
    ]
    guards["rules"].extend(x for x in new_guards if x["guard_id"] not in existing)
    dump("6g", "aggregation_guards.yaml", guards)

    strategies = load("6g", "remediation_strategies.yaml")
    for s in strategies:
        if s.get("strategy_id") in {"strategy.targeted_reteach", "strategy.worked_example", "strategy.micro_drill"}:
            s["guidance_selection_guard"] = "prefer for novice/prerequisite-repair; fade after verified success/transfer; previously mastered targets require verification-first"
        if s.get("strategy_id") in {"strategy.fresh_independent_recheck", "strategy.transfer_retest", "strategy.retrieval_reinforcement"}:
            s["guidance_selection_guard"] = "preferred as competence/retention evidence rises; H0 remains required where closure evidence is claimed"
    dump("6g", "remediation_strategies.yaml", strategies)

    # Durable executable-style scenario contract for Stage 13/18 implementation tests.
    scenarios = {
        "model": "6H-WLRM-external-behavior-QA-v0",
        "scenarios": [
            {"id": "invalid_attempt_no_target_weakness", "given": "attempt invalid or prerequisite-contaminated", "expect": "no target negative evidence"},
            {"id": "assisted_failure_hypothesis_only", "given": "assisted/provisional failure", "expect": "cannot directly confirm remediation_required"},
            {"id": "first_mastery_contradiction_verification", "given": "first clean contradiction after mastery", "expect": "verification_due before mastery erasure"},
            {"id": "mastered_target_guidance_fades", "given": "prior mastery or repeated transfer evidence", "expect": "verification/retrieval/transfer/debug path before worked-example reteach"},
            {"id": "ai_scaffold_cannot_close", "given": "remediation completed with material AI assistance", "expect": "fresh H0 direct verified evidence still required"},
            {"id": "project_pass_no_broadcast", "given": "integrated project global pass", "expect": "no automatic mastery for all tagged objectives"},
        ],
        "source_refs": [REPORT_REF],
    }
    (PKGS["6g"] / "external_behavior_qa_scenarios.yaml").write_text(
        yaml.safe_dump(scenarios, sort_keys=False, allow_unicode=True, width=120), encoding="utf-8"
    )

    # Recompute manifest/qa count fields produced by the generator after resolving the 6H-owned review.
    manifest = load("6g", "manifest.yaml")
    open_reviews = [x for x in reviews if x.get("status") == "open"]
    manifest["counts"]["open_non_blocking_reviews"] = len([x for x in open_reviews if x.get("severity") != "blocking"])
    manifest["counts"]["open_blocking_reviews"] = len([x for x in open_reviews if x.get("severity") == "blocking"])
    manifest["external_research_qa"] = {"status": "validated_6H", "owner_step": "6H", "report_ref": REPORT_REF}
    dump("6g", "manifest.yaml", manifest)
    qa = load("6g", "qa_report.yaml")
    qa["checked_at"] = TODAY
    qa["counts"]["open_non_blocking_reviews"] = manifest["counts"]["open_non_blocking_reviews"]
    qa["counts"]["open_blocking_reviews"] = manifest["counts"]["open_blocking_reviews"]
    qa["external_research_qa"] = {"status": "validated_6H", "owner_step": "6H", "report_ref": REPORT_REF}
    dump("6g", "qa_report.yaml", qa)


def write_patch_marker() -> None:
    out = ROOT / "curriculum/decomposition/6h_research_qa/external_patch_applied.yaml"
    data = {
        "stage_step": "6H",
        "patch_model": "S6ERQA-v0-candidate",
        "applied_at": TODAY,
        "external_report_ref": REPORT_REF,
        "stable_skill_additions": [
            "skill.os.numa_locality_affinity",
            "skill.cuda.async_data_movement_pipeline",
            "skill.optimization.speculative_decoding_tradeoff",
            "skill.serving.prefill_decode_disaggregation",
            "skill.ml.moe_routing_dataflow",
            "skill.multi_gpu.expert_parallel_sharding",
        ],
        "tool_vendor_details_policy": "version_scoped_objective_or_example_unless_independent_capability_test_passes",
        "raw_external_verdict": "PASS_WITH_REQUIRED_CHANGES",
        "manager_reconciliation": "patch_applied_pending_full_regression_and_post_step",
    }
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(yaml.safe_dump(data, sort_keys=False, allow_unicode=True, width=120), encoding="utf-8")


def main() -> None:
    patch_6c()
    patch_6d()
    patch_6e()
    patch_6f()
    patch_6g_if_present()
    write_patch_marker()
    print("6H_EXTERNAL_QA_PATCH=APPLIED")


if __name__ == "__main__":
    main()
