from __future__ import annotations

from collections import defaultdict, deque
from pathlib import Path
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
PKGS = {
    "6c": ROOT / "curriculum/decomposition/6c_foundations",
    "6d": ROOT / "curriculum/decomposition/6d_systems",
    "6e": ROOT / "curriculum/decomposition/6e_gpu_ml_inference",
    "6f": ROOT / "curriculum/decomposition/6f_professional_engineering",
    "6g": ROOT / "curriculum/decomposition/6g_weakness_remediation",
}
REPORT = ROOT / "research/6h_external_research_ai_report.md"
PATCH_MARKER = ROOT / "curriculum/decomposition/6h_research_qa/external_patch_applied.yaml"


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


failures: list[str] = []


def check(condition: bool, message: str) -> None:
    if not condition:
        failures.append(message)


check(REPORT.is_file(), "reconciled external Research AI report missing")
check(PATCH_MARKER.is_file(), "6H external patch marker missing")
if REPORT.is_file():
    report_text = REPORT.read_text(encoding="utf-8")
    for digest in {
        "52d50c17b71df63d16560cf81bd47aac108fa0f1b9776eea1fd7c1787a97eeb4",
        "86ebef2f6dabfcf10d302e72a5329777b9b3e02e92bf77310e58aaa7cb026568",
        "829b9bcc4dba5f845704193c3ebfbb5b9a4ba26dd3da4130b9ba2eb42abd3bec",
    }:
        check(digest in report_text, f"external evaluator provenance digest missing: {digest}")
    check("PASS WITH REQUIRED CHANGES" in report_text, "external verdict missing")

skills_by_pkg: dict[str, list[dict]] = {}
objs_by_pkg: dict[str, list[dict]] = {}
edges_by_pkg: dict[str, list[dict]] = {}
all_skills: set[str] = set()
all_objectives: set[str] = set()
all_edges: list[tuple[str, str, str]] = []

for pkg in ("6c", "6d", "6e", "6f"):
    skills_by_pkg[pkg] = load(PKGS[pkg] / "skills.yaml")
    objs_by_pkg[pkg] = load(PKGS[pkg] / "objectives.yaml")
    edges_by_pkg[pkg] = load(PKGS[pkg] / "prerequisite_edges.yaml")
    for row in skills_by_pkg[pkg]:
        sid = row["skill_id_candidate"]
        check(sid not in all_skills, f"duplicate Skill across final registry: {sid}")
        all_skills.add(sid)
    for row in objs_by_pkg[pkg]:
        oid = row["objective_id_candidate"]
        check(oid not in all_objectives, f"duplicate Objective across final registry: {oid}")
        all_objectives.add(oid)
    all_edges.extend((x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"]) for x in edges_by_pkg[pkg])

required_skills = {
    "skill.os.numa_locality_affinity",
    "skill.cuda.async_data_movement_pipeline",
    "skill.optimization.speculative_decoding_tradeoff",
    "skill.serving.prefill_decode_disaggregation",
    "skill.ml.moe_routing_dataflow",
    "skill.multi_gpu.expert_parallel_sharding",
}
check(required_skills <= all_skills, f"required stable 6H Skill additions missing: {sorted(required_skills-all_skills)}")

owner_by_objective = {x["objective_id_candidate"]: x["owner_skill_id"] for rows in objs_by_pkg.values() for x in rows}
check(set(owner_by_objective.values()) <= all_skills, "final Objective owner outside Skill registry")
for sid in required_skills:
    check(any(owner == sid for owner in owner_by_objective.values()), f"6H Skill without Objective: {sid}")

required_edges = {
    ("skill.arch.cache_hierarchy_model", "skill.os.numa_locality_affinity", "hard"),
    ("skill.os.virtual_physical_translation", "skill.os.numa_locality_affinity", "hard"),
    ("skill.os.scheduler_model", "skill.os.numa_locality_affinity", "soft"),
    ("skill.os.numa_locality_affinity", "skill.multi_gpu.topology_discovery", "hard"),
    ("skill.platform.scheduling_placement_constraints", "skill.ai_infra.topology_aware_placement", "hard"),
    ("skill.cuda.shared_memory_tiling", "skill.cuda.async_data_movement_pipeline", "hard"),
    ("skill.cuda.memory_visibility_ordering", "skill.cuda.async_data_movement_pipeline", "hard"),
    ("skill.gpu.copy_compute_overlap_model", "skill.cuda.async_data_movement_pipeline", "hard"),
    ("skill.inference.autoregressive_loop", "skill.optimization.speculative_decoding_tradeoff", "hard"),
    ("skill.inference.prefill_decode_distinction", "skill.serving.prefill_decode_disaggregation", "hard"),
    ("skill.inference.kv_cache_semantics", "skill.serving.prefill_decode_disaggregation", "hard"),
    ("skill.serving.worker_engine_topology", "skill.serving.prefill_decode_disaggregation", "hard"),
    ("skill.network.latency_bandwidth_budget", "skill.serving.prefill_decode_disaggregation", "hard"),
    ("skill.ml.transformer_block_dataflow", "skill.ml.moe_routing_dataflow", "hard"),
    ("skill.ml.moe_routing_dataflow", "skill.multi_gpu.expert_parallel_sharding", "hard"),
    ("skill.multi_gpu.collective_semantics", "skill.multi_gpu.expert_parallel_sharding", "hard"),
}
edge_set = set(all_edges)
check(required_edges <= edge_set, f"required 6H prerequisite edges missing: {sorted(required_edges-edge_set)}")
check(not any((a, b, "hard") in edge_set and (a, b, "soft") in edge_set for a, b, _ in edge_set), "hard/soft same-pair conflict after 6H patch")

# Final hard graph must remain acyclic across all canonical Skills.
adj: dict[str, list[str]] = defaultdict(list)
indegree = {sid: 0 for sid in all_skills}
for source, target, kind in all_edges:
    check(source in all_skills and target in all_skills, f"dangling final prerequisite: {source}->{target}")
    if kind == "hard" and source in all_skills and target in all_skills:
        adj[source].append(target)
        indegree[target] += 1
queue = deque(s for s, degree in indegree.items() if degree == 0)
visited = 0
while queue:
    source = queue.popleft()
    visited += 1
    for target in adj[source]:
        indegree[target] -= 1
        if indegree[target] == 0:
            queue.append(target)
check(visited == len(all_skills), f"final hard graph cycle: visited {visited}/{len(all_skills)}")

# 6H-owned reviews must be resolved; future-owner reviews remain legitimate.
owned_reviews: list[dict] = []
for pkg in ("6c", "6d", "6e", "6f", "6g"):
    for row in load(PKGS[pkg] / "review_queue.yaml"):
        if row.get("resolution_owner_step") == "6H":
            owned_reviews.append(row)
check(len(owned_reviews) == 10, f"expected 10 6H-owned reviews, found {len(owned_reviews)}")
check(all(x.get("status") == "resolved" for x in owned_reviews), f"unresolved 6H reviews: {[(x.get('review_id'),x.get('status')) for x in owned_reviews if x.get('status')!='resolved']}")

# External source/provenance must be present in accepted packages.
for pkg in ("6c", "6d", "6e", "6f"):
    sources = load(PKGS[pkg] / "sources.yaml")
    source_ids = {x["source_id"] for x in sources}
    check("source.repo.external_6h_research_qa" in source_ids, f"{pkg} missing external 6H source catalog entry")
    manifest = load(PKGS[pkg] / "manifest.yaml")
    if "coverage_declarations" in manifest:
        check(all(x.get("external_validation") == "validated_6H" for x in manifest["coverage_declarations"]), f"{pkg} route coverage not marked externally validated")

# Freshness/objective patches must exist without vendor/API-per-Skill inflation.
required_objectives = {
    "objective.python.thread_process_choice.runtime_mode_tradeoff",
    "objective.platform.scheduling_placement_constraints.accelerator_claim_topology",
    "objective.cuda.memory_visibility_ordering.cluster_scope_execution_memory",
    "objective.triton.matmul_performance_reasoning.modern_pipeline_schedule_tradeoff",
    "objective.optimization.kv_capacity_model.tiered_kv_offload_tradeoff",
    "objective.optimization.quantization_format_reasoning.low_precision_format_family_tradeoff",
    "objective.optimization.token_budget_scheduler.chunked_prefill_tradeoff",
    "objective.multi_gpu.rdma_transport_model.registration_zero_copy_lifecycle",
    "objective.multi_gpu.nccl_collective_operation.topology_current_algorithm_behavior",
    "objective.ai_infra.topology_aware_placement.orchestrated_accelerator_claim_placement",
    "objective.professional.dependency_supply_chain_review.distinguish_sbom_provenance_attestation",
    "objective.professional.oss_contribution_policy.live_policy_discovery",
}
check(required_objectives <= all_objectives, f"required 6H freshness/objective patches missing: {sorted(required_objectives-all_objectives)}")
for forbidden in {
    "skill.kubernetes.dra", "skill.vllm.speculative_decoding", "skill.sglang.prefill_decode_disaggregation",
    "skill.nvidia.nvfp4", "skill.deepseek.mla", "skill.deepseek.dualpipe", "skill.flashinfer.backend_operation",
}:
    check(forbidden not in all_skills, f"vendor/model-specific detail incorrectly promoted to stable Skill: {forbidden}")

# D23 professional evidence guards.
professional_guard_path = PKGS["6f"] / "external_qa_evidence_guards.yaml"
check(professional_guard_path.is_file(), "D23 external evidence guard file missing")
if professional_guard_path.is_file():
    guards = {x["guard_id"] for x in load(professional_guard_path)["rules"]}
    check({
        "guard.capstone_no_global_mastery", "guard.component_attribution_required",
        "guard.three_evidence_families", "guard.operations_failure_evidence",
    } <= guards, "D23 non-compensatory evidence guards incomplete")

# WLRM must exactly cover the patched final registry and external behavior guards.
g_manifest = load(PKGS["6g"] / "manifest.yaml")
g_counts = g_manifest["counts"]
check(g_counts["skills_covered"] == len(all_skills), f"WLRM Skill coverage drift: {g_counts['skills_covered']} != {len(all_skills)}")
check(g_counts["objectives_covered"] == len(all_objectives), f"WLRM Objective coverage drift: {g_counts['objectives_covered']} != {len(all_objectives)}")
check(len(load(PKGS["6g"] / "skill_weakness_profiles.yaml")) == len(all_skills), "WLRM Skill profile count mismatch")
check(len(load(PKGS["6g"] / "objective_weakness_profiles.yaml")) == len(all_objectives), "WLRM Objective profile count mismatch")
check(len(load(PKGS["6g"] / "remediation_routes.yaml")) == len(all_objectives), "WLRM remediation route count mismatch")
agg_guards = {x["guard_id"] for x in load(PKGS["6g"] / "aggregation_guards.yaml")["rules"]}
check({"guard.guidance_fading", "guard.mastered_target_reverification_first", "guard.ai_scaffold_not_closure"} <= agg_guards, "WLRM 6H guidance/AI guards missing")
scenario_path = PKGS["6g"] / "external_behavior_qa_scenarios.yaml"
check(scenario_path.is_file(), "WLRM external behavior QA scenarios missing")
if scenario_path.is_file():
    scenario_ids = {x["id"] for x in load(scenario_path)["scenarios"]}
    check({"invalid_attempt_no_target_weakness", "first_mastery_contradiction_verification", "mastered_target_guidance_fades", "ai_scaffold_cannot_close", "project_pass_no_broadcast"} <= scenario_ids, "WLRM executable external behavior scenarios incomplete")

if failures:
    print("6H_EXTERNAL_RECONCILIATION_QA=FAIL")
    for failure in failures:
        print(f"- {failure}")
    sys.exit(1)

print("6H_EXTERNAL_RECONCILIATION_QA=PASS")
print(f"skills={len(all_skills)} objectives={len(all_objectives)} edges={len(all_edges)} hard_dag={visited}/{len(all_skills)}")
print(f"resolved_6h_reviews={len(owned_reviews)}/10")
