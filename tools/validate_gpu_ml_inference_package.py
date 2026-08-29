from __future__ import annotations

from collections import defaultdict, deque
from pathlib import Path
import re
import sys
import yaml


ROOT = Path(__file__).resolve().parents[1]
PKG = ROOT / "curriculum" / "decomposition" / "6e_gpu_ml_inference"
FDM = ROOT / "curriculum" / "decomposition" / "6c_foundations"
SDM = ROOT / "curriculum" / "decomposition" / "6d_systems"
ROUTE_FAMILIES = {f"D{n:02d}" for n in range(14, 23)}
EXPECTED = [
    "manifest.yaml", "sources.yaml", "organization_entities.yaml", "skills.yaml", "objectives.yaml",
    "topic_skill_links.yaml", "prerequisite_edges.yaml", "capability_requirements.yaml",
    "professional_attributions.yaml", "project_capstone_attributions.yaml", "seed_mappings.yaml",
    "review_queue.yaml", "qa_report.yaml",
]


def load(directory: Path, name: str):
    return yaml.safe_load((directory / name).read_text(encoding="utf-8"))


failures: list[str] = []


def check(condition: bool, message: str) -> None:
    if not condition:
        failures.append(message)


check(all((PKG / name).is_file() for name in EXPECTED), "logical collection file missing")
if failures:
    print("GPU_ML_INFERENCE_PACKAGE_QA=FAIL")
    for failure in failures:
        print(f"- {failure}")
    sys.exit(1)


data = {name: load(PKG, name) for name in EXPECTED}
manifest = data["manifest.yaml"]
sources = data["sources.yaml"]
org = data["organization_entities.yaml"]
skills = data["skills.yaml"]
objectives = data["objectives.yaml"]
links = data["topic_skill_links.yaml"]
edges = data["prerequisite_edges.yaml"]
requirements = data["capability_requirements.yaml"]
professional = data["professional_attributions.yaml"]
projects = data["project_capstone_attributions.yaml"]
mappings = data["seed_mappings.yaml"]
reviews = data["review_queue.yaml"]
qa = data["qa_report.yaml"]

fdm_skills = {x["skill_id_candidate"] for x in load(FDM, "skills.yaml")}
sdm_skills = {x["skill_id_candidate"] for x in load(SDM, "skills.yaml")}
fdm_objectives = {x["objective_id_candidate"] for x in load(FDM, "objectives.yaml")}
sdm_objectives = {x["objective_id_candidate"] for x in load(SDM, "objectives.yaml")}
prior_skills = fdm_skills | sdm_skills
prior_objectives = fdm_objectives | sdm_objectives
prior_edges = [
    (x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"])
    for directory in (FDM, SDM)
    for x in load(directory, "prerequisite_edges.yaml")
]

id_pattern = re.compile(r"^(domain|module|topic|skill|objective)\.[a-z0-9_]+(?:\.[a-z0-9_]+)*$")
unstable_token = re.compile(r"(?:^|\.)(week|day|stage|chapter|part|v\d+|level\d+|beginner|advanced_track)(?:\.|_|$)")
org_ids = [x["logical_id_candidate"] for x in org]
skill_ids = [x["skill_id_candidate"] for x in skills]
objective_ids = [x["objective_id_candidate"] for x in objectives]
org_set, skill_set, objective_set = set(org_ids), set(skill_ids), set(objective_ids)
known_skills = prior_skills | skill_set
domain_set = {x["logical_id_candidate"] for x in org if x["entity_type"] == "domain"}
module_set = {x["logical_id_candidate"] for x in org if x["entity_type"] == "module"}
topic_set = {x["logical_id_candidate"] for x in org if x["entity_type"] == "topic"}

# Structural / partition / identity
check(set(manifest["route_family_ids"]) == ROUTE_FAMILIES, "route partition is not D14-D22")
check({r for x in org if x["entity_type"] == "domain" for r in x["linked_route_family_ids"]} == ROUTE_FAMILIES,
      "domain route family coverage incomplete")
check(len(org_ids) == len(org_set), "duplicate organization ID")
check(len(skill_ids) == len(skill_set), "duplicate Skill ID")
check(len(objective_ids) == len(objective_set), "duplicate Objective ID")
check(not (skill_set & prior_skills), "6E redeclares accepted 6C/6D Skill ID")
check(not (objective_set & prior_objectives), "6E redeclares accepted 6C/6D Objective ID")
check(all(id_pattern.fullmatch(x) for x in org_ids + skill_ids + objective_ids), "invalid logical ID")
check(not any(unstable_token.search(x) for x in org_ids + skill_ids + objective_ids), "unstable logical ID component")

for row in org:
    oid = row["logical_id_candidate"]
    check(bool(row["semantic_statement"]), f"organization entity without semantic statement: {oid}")
    if row["entity_type"] == "domain":
        check(row["parent_organization_id"] is None, f"Domain has parent: {oid}")
    elif row["entity_type"] == "module":
        check(row["parent_organization_id"] in domain_set, f"Module parent missing: {oid}")
    elif row["entity_type"] == "topic":
        check(row["parent_organization_id"] in module_set, f"Topic parent missing: {oid}")
    else:
        check(False, f"unknown entity_type: {oid}")

# Objective ownership/evidence and Skill semantics
owner_counts = defaultdict(int)
for row in objectives:
    oid = row["objective_id_candidate"]
    owner = row["owner_skill_id"]
    check(owner in skill_set, f"Objective owner missing/locality violation: {oid}")
    check(oid.startswith("objective." + owner.removeprefix("skill.") + "."), f"Objective ID does not sit under owner Skill: {oid}")
    owner_counts[owner] += 1
    check(bool(row["observable_action"] and row["success_criteria"]), f"Objective not observable: {oid}")
    profile = row["evidence_profile"]
    check(profile["policy_defaults_ref"] == "GRE-v0", f"Objective bypasses GRE-v0: {oid}")
    check(profile["required_direct_type"] in profile["direct_evidence_types"], f"Objective direct evidence mismatch: {oid}")
    check(profile["evaluator_requirement"] == "verified", f"Objective evaluator requirement weakened: {oid}")
    check(row["retention_requirement"]["delayed_revalidation_required"] is True, f"Objective retention missing: {oid}")
check(all(owner_counts[x] >= 1 for x in skill_set), "Skill without Objective")

recognition_only = {"recognition", "explanation", "code_reading", "artifact_analysis"}
for row in skills:
    sid = row["skill_id_candidate"]
    check(row["primary_teaching_topic_id"] in topic_set, f"Skill Topic missing: {sid}")
    check(row["shared_placement_domain_ids"] and row["shared_placement_domain_ids"][0] in domain_set,
          f"Skill domain placement missing: {sid}")
    check(row["independent_evidence_path"]["observable"] is True, f"Skill evidence not observable: {sid}")
    check(bool(row["independent_evidence_path"]["direct_evidence_types"]), f"Skill direct evidence missing: {sid}")
    check(bool(row["remediation_tags"]), f"Skill remediation missing: {sid}")
    check(row["retention_profile"] in {"factual", "standard", "complex"}, f"invalid retention profile: {sid}")
    check(row["diagnostic_eligibility"] in {"eligible", "restricted", "not_eligible"}, f"invalid diagnostic eligibility: {sid}")
    check(row["freshness_class"] in {"evergreen", "version_sensitive", "fast_moving"}, f"invalid freshness class: {sid}")
    check(row["duplicate_resolution"]["status"] in {"reuse_existing", "create_new", "candidate_split", "candidate_merge", "duplicate_review_required"},
          f"invalid duplicate resolution: {sid}")
    if row["freshness_class"] != "evergreen":
        check(bool(row["technology_dependency_refs"]), f"non-evergreen Skill without technology dependency: {sid}")
    if row["capability_kind"] in {"tool_specific_production", "systems_production", "measurement_production", "professional_workflow"}:
        check(not set(row["independent_evidence_path"]["direct_evidence_types"]) <= recognition_only,
              f"production/workflow Skill proven by recognition/analysis alone: {sid}")

# Stable concept vs tool/runtime-specific explicit separation.
for stable_id in {
    "skill.serving.engine_configuration_reasoning", "skill.optimization.continuous_batching_reasoning",
    "skill.multi_gpu.collective_semantics", "skill.multi_gpu.rdma_transport_model",
}:
    check(stable_id in skill_set, f"stable accelerator/inference concept missing: {stable_id}")
for tool_id in {
    "skill.serving.vllm_runtime_operation", "skill.serving.sglang_runtime_operation",
    "skill.serving.tensorrt_llm_runtime_operation", "skill.multi_gpu.nccl_collective_operation",
    "skill.cuda.kernel_execution_mapping", "skill.triton.program_instance_mapping",
}:
    check(tool_id in skill_set, f"tool-specific professional capability missing: {tool_id}")
    if tool_id in skill_set:
        row = next(x for x in skills if x["skill_id_candidate"] == tool_id)
        check(row["freshness_class"] != "evergreen", f"tool-specific Skill incorrectly evergreen: {tool_id}")

# TopicSkillLinks / explicit reuse
roles = {"teach", "practice", "assess", "reinforce", "transfer", "integrate"}
importances = {"core", "supporting", "incidental"}
primary_by_skill = defaultdict(int)
for row in links:
    check(row["topic_id"] in topic_set, f"dangling TopicSkillLink topic: {row['topic_id']}")
    check(row["skill_id"] in known_skills, f"dangling TopicSkillLink skill: {row['skill_id']}")
    check(row["role"] in roles and row["importance"] in importances, f"invalid TopicSkillLink vocabulary: {row}")
    check(all(x in objective_set for x in row["objective_scope_ids"]), f"dangling current-package objective scope: {row}")
    if row["is_primary_teaching_context"]:
        primary_by_skill[row["skill_id"]] += 1
        check(row["skill_id"] in skill_set, f"prior-package Skill claimed as primary teaching context: {row['skill_id']}")
check(all(primary_by_skill[x] == 1 for x in skill_set), "Skill without exactly one primary teaching context")

# Prerequisites / hard-soft / combined DAG
reason_kinds = {
    "conceptual_dependency", "procedural_dependency", "evidence_interpretability", "safety_dependency",
    "tool_environment_dependency", "language_dependency", "performance_reasoning_dependency",
    "professional_workflow_dependency",
}
pairs = set()
for row in edges:
    source, target, kind = row["prerequisite_skill_id"], row["target_skill_id"], row["edge_kind"]
    check(source in known_skills, f"dangling prerequisite source: {source}")
    check(target in skill_set, f"prerequisite target is not a 6E Skill: {target}")
    check(source != target, f"self prerequisite: {source}")
    check(kind in {"hard", "soft"}, f"invalid edge kind: {kind}")
    check(row["reason_kind"] in reason_kinds, f"invalid reason_kind: {row['reason_kind']}")
    check(row["task_specific_instead_of_graph_edge"] is False, f"task-specific requirement authored as graph edge: {source}->{target}")
    check(bool(row["authoring_rationale"]), f"prerequisite without rationale: {source}->{target}")
    check(row["cross_package_ref"] == (source in prior_skills), f"cross_package_ref mislabeled: {source}->{target}")
    check((source, target, kind) not in pairs, f"duplicate edge: {source}->{target}/{kind}")
    pairs.add((source, target, kind))
check(not any((a, b, "hard") in pairs and (a, b, "soft") in pairs for a, b, _ in pairs), "hard/soft same-pair conflict")

combined = set(prior_edges) | pairs
hard_graph = defaultdict(list)
indegree = {x: 0 for x in known_skills}
for a, b, kind in combined:
    if kind == "hard":
        hard_graph[a].append(b)
        indegree[b] += 1
q = deque(x for x, d in indegree.items() if d == 0)
visited = 0
while q:
    node = q.popleft()
    visited += 1
    for target in hard_graph[node]:
        indegree[target] -= 1
        if indegree[target] == 0:
            q.append(target)
check(visited == len(known_skills), "hard prerequisite cycle across combined 6C+6D+6E graph")
check(not any(a.startswith("skill.english.") and not b.startswith("skill.english.") and k == "hard" for a, b, k in combined),
      "English global hard gate")

# Explicit math/numerical hidden-prerequisite guard.
for sid in {
    "skill.math.tensor_shape_reasoning", "skill.math.broadcasting_reasoning", "skill.math.matrix_multiplication_reasoning",
    "skill.math.dot_product_similarity", "skill.math.probability_normalization", "skill.math.softmax_stability",
    "skill.math.floating_point_error_reasoning", "skill.math.mixed_precision_tradeoff",
}:
    check(sid in skill_set, f"explicit math/numerical capability missing: {sid}")
required_math_edges = {
    ("skill.math.matrix_multiplication_reasoning", "skill.ml.attention_qkv_shapes", "hard"),
    ("skill.math.softmax_stability", "skill.ml.attention_score_mask_softmax", "hard"),
    ("skill.math.tensor_shape_reasoning", "skill.inference.kv_memory_growth", "hard"),
    ("skill.math.mixed_precision_tradeoff", "skill.optimization.quantization_format_reasoning", "hard"),
}
check(required_math_edges <= pairs, "critical math/numerical prerequisites are not explicit hard edges")

# Triton cannot bypass the GPU/CUDA foundation.
triton_sources = {(a, b) for a, b, k in pairs if k == "hard" and b.startswith("skill.triton.")}
check(("skill.cuda.kernel_execution_mapping", "skill.triton.program_instance_mapping") in triton_sources,
      "Triton programming model bypasses CUDA/GPU foundation")

# Requirements / attributions
scope_kinds = {"topic", "module", "domain", "v1_backbone", "professional_route", "project", "capstone", "assessment_blueprint"}
for row in requirements:
    check(row["capability_id"] in skill_set, f"requirement capability missing: {row['capability_id']}")
    check(row["scope_kind"] in scope_kinds, f"invalid requirement scope kind: {row['scope_kind']}")
    if row["scope_kind"] == "domain":
        check(row["scope_id"] in domain_set, f"requirement domain scope missing: {row['scope_id']}")
check(all(x["capability_id"] in skill_set for x in professional), "professional attribution capability missing")
for row in projects:
    check(row["skill_id"] in skill_set and row["objective_id"] in objective_set, f"project attribution missing: {row}")
    check(row["structurally_essential"] and row["separately_observable"], f"non-observable project attribution: {row}")

# Prior-package reuse must be complete and clone-free.
for row in mappings:
    check(row["seed_id"] in prior_skills, f"reuse mapping seed not in prior registry: {row['seed_id']}")
    check(row["result_entity_refs"] == [row["seed_id"]], f"reused prior Skill identity changed: {row['seed_id']}")
    check(row["mapping_class"] == "reused_from_prior_package", f"unexpected mapping class: {row['seed_id']}")
    expected_origin = "6d_systems" if row["seed_id"] in sdm_skills else "6c_foundations"
    check(row["origin_package"] == expected_origin, f"wrong prior origin package: {row['seed_id']}")
declared_reuse = {x["seed_id"] for x in mappings}
actual_reuse = {a for a, _, _ in pairs if a in prior_skills} | {x["skill_id"] for x in links if x["skill_id"] in prior_skills}
check(declared_reuse == actual_reuse, "prior-package reuse is not fully declared in seed_mappings")
check(bool(declared_reuse & sdm_skills), "6E fails to reuse Systems registry")
check(bool(declared_reuse & fdm_skills), "6E fails to reuse Foundations registry")

# D-058 accelerator forward reuse loop must now be durable-resolved.
sdm_reviews = load(SDM, "review_queue.yaml")
forward = next((x for x in sdm_reviews if x["review_id"] == "review.6d.accelerator_forward_reuse"), None)
check(forward is not None and forward["status"] == "resolved", "review.6d.accelerator_forward_reuse was not resolved")
if forward is not None:
    check(bool(forward.get("resolution")) and bool(forward.get("resolution_refs")), "resolved 6D forward review lacks durable resolution refs")
sdm_manifest = load(SDM, "manifest.yaml")
check(sdm_manifest["unresolved_review_count"] == len([x for x in sdm_reviews if x["status"] == "open"]),
      "6D manifest review count drift after forward-review resolution")

# Provenance/review/QA
source_ids = {x["source_id"] for x in sources}
check(len(source_ids) == len(sources), "duplicate source ID")
check(all((ROOT / x["ref"]).is_file() for x in sources), "source path missing")
check(set(manifest["source_catalog_refs"]) == source_ids, "manifest source catalog mismatch")
referenced = {r for row in skills + objectives for r in row["source_refs"]}
referenced |= {row["provenance_ref"] for row in skills}
check(referenced <= source_ids, "entity references source outside catalog")
check(manifest["blocking_review_count"] == 0, "manifest reports blocking review")
check(manifest["unresolved_review_count"] == len([x for x in reviews if x["status"] == "open"]),
      "manifest review count does not match queue")
check(bool(manifest["known_exclusions"]), "manifest declares no explicit exclusions")
check(not any(x["severity"] == "blocking" and x["status"] == "open" for x in reviews), "open blocking review")
check(all(x["severity"] in {"blocking", "non_blocking", "advisory"} for x in reviews), "invalid review severity")
check(qa["result"] in {"PASS", "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS"}
      and (qa["result"] == "PASS") == (qa["counts"]["open_non_blocking_reviews"] == 0),
      "QA result mismatch")
check(qa["external_research_qa"]["status"] in {"pending", "validated_6H"}
      and qa["external_research_qa"]["owner_step"] == "6H",
      "6H external QA guard missing")
check(qa["counts"]["skills"] == len(skill_set) and qa["counts"]["objectives"] == len(objective_set),
      "QA report counts do not match emitted collections")
check(qa["counts"]["reused_prior_package_skills"] == len(declared_reuse), "QA reuse count mismatch")

if failures:
    print("GPU_ML_INFERENCE_PACKAGE_QA=FAIL")
    for failure in failures:
        print(f"- {failure}")
    sys.exit(1)

hard_edges = len([x for x in edges if x["edge_kind"] == "hard"])
print("GPU_ML_INFERENCE_PACKAGE_QA=PASS")
print(f"domains={len(domain_set)} modules={len(module_set)} topics={len(topic_set)}")
print(f"skills={len(skill_set)} objectives={len(objective_set)} topic_skill_links={len(links)}")
print(f"prerequisites={len(edges)} hard={hard_edges} soft={len(edges) - hard_edges}")
print(f"cross_package_edges={len([x for x in edges if x['cross_package_ref']])}")
print(f"reused_prior={len(declared_reuse)} reused_6c={len(declared_reuse & fdm_skills)} reused_6d={len(declared_reuse & sdm_skills)}")
print(f"combined_hard_dag_nodes={visited}/{len(known_skills)} (6C+6D+6E)")
print(f"open_blocking_reviews=0 open_non_blocking_reviews={len([x for x in reviews if x['status'] == 'open'])}")
