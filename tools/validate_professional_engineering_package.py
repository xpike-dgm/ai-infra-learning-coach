from __future__ import annotations

from collections import defaultdict, deque
from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
PKG = ROOT / "curriculum" / "decomposition" / "6f_professional_engineering"
PRIOR = [
    ROOT / "curriculum" / "decomposition" / "6c_foundations",
    ROOT / "curriculum" / "decomposition" / "6d_systems",
    ROOT / "curriculum" / "decomposition" / "6e_gpu_ml_inference",
]
EXPECTED = ["manifest.yaml", "sources.yaml", "organization_entities.yaml", "skills.yaml", "objectives.yaml", "topic_skill_links.yaml", "prerequisite_edges.yaml", "capability_requirements.yaml", "professional_attributions.yaml", "project_capstone_attributions.yaml", "seed_mappings.yaml", "review_queue.yaml", "qa_report.yaml"]


def load(directory: Path, name: str):
    return yaml.safe_load((directory / name).read_text(encoding="utf-8"))

failures = []
def check(condition, message):
    if not condition: failures.append(message)

check(all((PKG / name).is_file() for name in EXPECTED), "logical collection file missing")
if failures:
    print("PROFESSIONAL_ENGINEERING_PACKAGE_QA=FAIL")
    print("\n".join("- " + x for x in failures)); sys.exit(1)

data = {name: load(PKG, name) for name in EXPECTED}
manifest, org, skills, objectives = data["manifest.yaml"], data["organization_entities.yaml"], data["skills.yaml"], data["objectives.yaml"]
links, edges, requirements = data["topic_skill_links.yaml"], data["prerequisite_edges.yaml"], data["capability_requirements.yaml"]
professional, projects, mappings, reviews, qa = data["professional_attributions.yaml"], data["project_capstone_attributions.yaml"], data["seed_mappings.yaml"], data["review_queue.yaml"], data["qa_report.yaml"]

prior_skill_sets = [{x["skill_id_candidate"] for x in load(d, "skills.yaml")} for d in PRIOR]
prior_obj_sets = [{x["objective_id_candidate"] for x in load(d, "objectives.yaml")} for d in PRIOR]
prior_skills = set().union(*prior_skill_sets); prior_objectives = set().union(*prior_obj_sets)
prior_edges = [(x["prerequisite_skill_id"], x["target_skill_id"], x["edge_kind"]) for d in PRIOR for x in load(d, "prerequisite_edges.yaml")]

org_ids = [x["logical_id_candidate"] for x in org]; skill_ids = [x["skill_id_candidate"] for x in skills]; objective_ids = [x["objective_id_candidate"] for x in objectives]
org_set, skill_set, objective_set = set(org_ids), set(skill_ids), set(objective_ids)
known_skills = prior_skills | skill_set; known_objectives = prior_objectives | objective_set
id_pattern = re.compile(r"^(domain|module|topic|skill|objective)\.[a-z0-9_]+(?:\.[a-z0-9_]+)*$")
domains = {x["logical_id_candidate"] for x in org if x["entity_type"] == "domain"}; modules = {x["logical_id_candidate"] for x in org if x["entity_type"] == "module"}; topics = {x["logical_id_candidate"] for x in org if x["entity_type"] == "topic"}

check(manifest["route_family_ids"] == ["D23"], "route partition is not D23 exactly")
check(domains == {"domain.professional_engineering"}, "unexpected D23 domain set")
check(len(modules) == 9, "expected 9 D23 modules")
check(len(topics) == 27, "expected 27 D23 topics")
check(len(org_ids) == len(org_set) and len(skill_ids) == len(skill_set) and len(objective_ids) == len(objective_set), "duplicate local IDs")
check(not (skill_set & prior_skills), "6F redeclares prior Skill")
check(not (objective_set & prior_objectives), "6F redeclares prior Objective")
check(all(id_pattern.fullmatch(x) for x in org_ids + skill_ids + objective_ids), "invalid logical ID")
for row in org:
    if row["entity_type"] == "domain": check(row["parent_organization_id"] is None, "domain has parent")
    elif row["entity_type"] == "module": check(row["parent_organization_id"] in domains, f"module parent missing {row['logical_id_candidate']}")
    elif row["entity_type"] == "topic": check(row["parent_organization_id"] in modules, f"topic parent missing {row['logical_id_candidate']}")

owner_counts = defaultdict(int)
for row in objectives:
    oid, owner = row["objective_id_candidate"], row["owner_skill_id"]
    check(owner in skill_set, f"objective owner not local {oid}")
    check(oid.startswith("objective." + owner.removeprefix("skill.") + "."), f"objective ID/owner mismatch {oid}")
    owner_counts[owner] += 1
    p = row["evidence_profile"]
    check(p["policy_defaults_ref"] == "GRE-v0" and p["evaluator_requirement"] == "verified", f"objective evidence policy weak {oid}")
    check(p["required_direct_type"] in p["direct_evidence_types"], f"objective direct evidence mismatch {oid}")
    check(row["retention_requirement"]["delayed_revalidation_required"] is True, f"objective retention missing {oid}")
check(all(owner_counts[x] >= 1 for x in skill_set), "local Skill without Objective")

for row in skills:
    sid = row["skill_id_candidate"]
    check(sid.startswith("skill.professional."), f"unexpected local skill namespace {sid}")
    check(row["primary_teaching_topic_id"] in topics, f"skill topic missing {sid}")
    check(row["shared_placement_domain_ids"] == ["domain.professional_engineering"], f"skill domain mismatch {sid}")
    check(row["independent_evidence_path"]["observable"] is True and row["independent_evidence_path"]["direct_evidence_types"], f"skill evidence missing {sid}")
    check("production_context" in row["evidence_depth_expectations"], f"professional Skill lacks production context {sid}")
    check(row["freshness_class"] == "evergreen", f"D23 stable workflow unexpectedly version scoped {sid}")

primary = defaultdict(int)
for row in links:
    check(row["topic_id"] in topics and row["skill_id"] in known_skills, f"dangling TopicSkillLink {row}")
    if row["is_primary_teaching_context"]:
        primary[row["skill_id"]] += 1
        check(row["skill_id"] in skill_set, f"prior Skill claimed as D23 primary {row['skill_id']}")
check(all(primary[x] == 1 for x in skill_set), "local Skill without exactly one primary teaching context")

pairs = set()
allowed_reason = {"conceptual_dependency", "procedural_dependency", "evidence_interpretability", "safety_dependency", "professional_workflow_dependency"}
for row in edges:
    a, b, k = row["prerequisite_skill_id"], row["target_skill_id"], row["edge_kind"]
    check(a in known_skills and b in skill_set and a != b, f"bad prerequisite {a}->{b}")
    check(k in {"hard", "soft"} and row["reason_kind"] in allowed_reason, f"bad edge vocabulary {a}->{b}")
    check(row["cross_package_ref"] == (a in prior_skills), f"cross_package_ref mismatch {a}->{b}")
    check((a,b,k) not in pairs, f"duplicate edge {a}->{b}/{k}"); pairs.add((a,b,k))
check(not any((a,b,"hard") in pairs and (a,b,"soft") in pairs for a,b,_ in pairs), "hard/soft conflict")

combined = set(prior_edges) | pairs
hard_graph = defaultdict(list); indegree = {x: 0 for x in known_skills}
for a,b,k in combined:
    if k == "hard": hard_graph[a].append(b); indegree[b] += 1
q = deque(x for x,d in indegree.items() if d == 0); visited = 0
while q:
    n = q.popleft(); visited += 1
    for t in hard_graph[n]:
        indegree[t] -= 1
        if indegree[t] == 0: q.append(t)
check(visited == len(known_skills), "combined 6C+6D+6E+6F hard graph cycle")
check(not any(a.startswith("skill.english.") and not b.startswith("skill.english.") and k == "hard" for a,b,k in combined), "English global hard gate")

for row in requirements:
    check(row["capability_id"] in skill_set, f"requirement points outside local D23 skill {row['capability_id']}")
check(all(x["capability_id"] in skill_set for x in professional), "professional attribution points outside local D23 skill")

expected_projects = {"project.foundation.reproducible_cli_tool", "project.systems.observable_networked_service", "project.gpu_inference.integrated_serving_stack", "project.professional.open_source_contribution", "capstone.professional.ai_infrastructure_system"}
check(expected_projects <= {x["project_or_capstone_id"] for x in projects}, "required project/capstone overlay missing")
for row in projects:
    check(row["skill_id"] in known_skills and row["objective_id"] in known_objectives, f"project attribution dangling {row}")
    check(row["structurally_essential"] and row["separately_observable"], f"project attribution not separately observable {row}")

reuse = {x["seed_id"] for x in mappings if x["seed_kind"] == "accepted_prior_skill"}
check(reuse <= prior_skills, "reuse mapping includes non-prior Skill")
check(all(x["mapping_status"] == "reuse_existing" for x in mappings if x["seed_kind"] == "accepted_prior_skill"), "prior Skill mapping not reuse_existing")
for sid in {"skill.engineering.reproducible_run_notes", "skill.git.stage_commit_history_basic", "skill.performance.benchmark_reproducibility", "skill.observability.structured_logging", "skill.reliability.slo_definition", "skill.serving.latency_throughput_slo_analysis", "skill.ai_infra.model_rollout_validation"}:
    check(sid in reuse, f"required cross-package professional reuse missing {sid}")

sdm_reviews = load(PRIOR[1], "review_queue.yaml"); gim_reviews = load(PRIOR[2], "review_queue.yaml")
check(next(x for x in sdm_reviews if x["review_id"] == "review.6d.professional_overlay_reconciliation")["status"] == "resolved", "6D professional overlay review not resolved")
check(next(x for x in gim_reviews if x["review_id"] == "review.6e.professional_overlay_reconciliation")["status"] == "resolved", "6E professional overlay review not resolved")
open_reviews = [x for x in reviews if x["status"] == "open"]
check(all(x["severity"] == "non_blocking" and x["status"] in {"open", "resolved"}
          and x["resolution_owner_step"] == "6H" for x in reviews), "6F open review ownership/severity invalid")
check(qa["external_research_qa"]["status"] in {"pending", "validated_6H"}
      and qa["external_research_qa"]["owner_step"] == "6H", "6H external QA guard missing")

counts = qa["counts"]
for key, actual in {"domains":len(domains), "modules":len(modules), "topics":len(topics), "skills":len(skill_set), "objectives":len(objective_set), "topic_skill_links":len(links), "prerequisite_edges":len(edges), "professional_attributions":len(professional), "project_attributions":len(projects), "open_non_blocking_reviews":len(open_reviews)}.items():
    check(counts[key] == actual, f"qa count mismatch {key}: {counts[key]} != {actual}")
check(counts["open_blocking_reviews"] == 0 and manifest["blocking_review_count"] == 0, "blocking review count nonzero")
check(manifest["unresolved_review_count"] == len(open_reviews), "manifest review count mismatch")

if failures:
    print("PROFESSIONAL_ENGINEERING_PACKAGE_QA=FAIL")
    for failure in failures: print("- " + failure)
    sys.exit(1)

hard = sum(x["edge_kind"] == "hard" for x in edges)
print("PROFESSIONAL_ENGINEERING_PACKAGE_QA=PASS")
print(f"domains={len(domains)} modules={len(modules)} topics={len(topics)}")
print(f"skills={len(skill_set)} objectives={len(objective_set)} topic_skill_links={len(links)}")
print(f"prerequisites={len(edges)} hard={hard} soft={len(edges)-hard}")
print(f"cross_package_edges={sum(x['cross_package_ref'] for x in edges)} reused_prior={len(reuse)}")
print(f"combined_hard_dag_nodes={visited}/{len(known_skills)} (6C+6D+6E+6F)")
print(f"projects={len({x['project_or_capstone_id'] for x in projects})} project_attributions={len(projects)}")
print(f"open_blocking_reviews=0 open_non_blocking_reviews={len(open_reviews)}")
