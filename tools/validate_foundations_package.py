from __future__ import annotations

from collections import defaultdict, deque
from pathlib import Path
import re
import sys
import yaml


ROOT = Path(__file__).resolve().parents[1]
PKG = ROOT / "curriculum" / "decomposition" / "6c_foundations"
EXPECTED = [
    "manifest.yaml", "sources.yaml", "organization_entities.yaml", "skills.yaml", "objectives.yaml",
    "topic_skill_links.yaml", "prerequisite_edges.yaml", "capability_requirements.yaml",
    "professional_attributions.yaml", "project_capstone_attributions.yaml", "seed_mappings.yaml",
    "review_queue.yaml", "qa_report.yaml",
]


def load(name: str):
    return yaml.safe_load((PKG / name).read_text(encoding="utf-8"))


failures: list[str] = []


def check(condition: bool, message: str) -> None:
    if not condition:
        failures.append(message)


check(all((PKG / name).is_file() for name in EXPECTED), "logical collection file missing")
data = {name: load(name) for name in EXPECTED}
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

id_pattern = re.compile(r"^(domain|module|topic|skill|objective)\.[a-z0-9_]+(?:\.[a-z0-9_]+)*$")
org_ids = [x["logical_id_candidate"] for x in org]
skill_ids = [x["skill_id_candidate"] for x in skills]
objective_ids = [x["objective_id_candidate"] for x in objectives]
org_set, skill_set, objective_set = set(org_ids), set(skill_ids), set(objective_ids)
topic_set = {x["logical_id_candidate"] for x in org if x["entity_type"] == "topic"}
module_set = {x["logical_id_candidate"] for x in org if x["entity_type"] == "module"}
domain_set = {x["logical_id_candidate"] for x in org if x["entity_type"] == "domain"}

check(set(manifest["route_family_ids"]) == {"D01", "D02", "D03", "D04", "D05"}, "route partition is not D01-D05")
check(len(org_ids) == len(org_set), "duplicate organization ID")
check(len(skill_ids) == len(skill_set), "duplicate Skill ID")
check(len(objective_ids) == len(objective_set), "duplicate Objective ID")
check(all(id_pattern.fullmatch(x) for x in org_ids + skill_ids + objective_ids), "unstable or invalid logical ID")

for row in org:
    if row["entity_type"] == "domain":
        check(row["parent_organization_id"] is None, f"Domain has parent: {row['logical_id_candidate']}")
    elif row["entity_type"] == "module":
        check(row["parent_organization_id"] in domain_set, f"Module parent missing: {row['logical_id_candidate']}")
    elif row["entity_type"] == "topic":
        check(row["parent_organization_id"] in module_set, f"Topic parent missing: {row['logical_id_candidate']}")

owner_counts = defaultdict(int)
for row in objectives:
    check(row["owner_skill_id"] in skill_set, f"Objective owner missing: {row['objective_id_candidate']}")
    owner_counts[row["owner_skill_id"]] += 1
    check(bool(row["observable_action"] and row["success_criteria"]), f"Objective not observable: {row['objective_id_candidate']}")
check(all(owner_counts[x] >= 1 for x in skill_set), "Skill without Objective")

for row in skills:
    sid = row["skill_id_candidate"]
    check(row["primary_teaching_topic_id"] in topic_set, f"Skill Topic missing: {sid}")
    check(row["independent_evidence_path"]["observable"] is True, f"Skill evidence not observable: {sid}")
    check(bool(row["remediation_tags"]), f"Skill remediation missing: {sid}")
    check(bool(row["granularity_review_codes"]), f"Skill granularity review missing: {sid}")

for row in links:
    check(row["topic_id"] in topic_set and row["skill_id"] in skill_set, f"dangling TopicSkillLink: {row}")
    check(all(x in objective_set for x in row["objective_scope_ids"]), f"dangling objective scope: {row}")

reason_kinds = {
    "conceptual_dependency", "procedural_dependency", "evidence_interpretability", "safety_dependency",
    "tool_environment_dependency", "language_dependency", "performance_reasoning_dependency",
    "professional_workflow_dependency",
}
pairs = set()
hard_graph = defaultdict(list)
indegree = {x: 0 for x in skill_set}
for row in edges:
    source, target, kind = row["prerequisite_skill_id"], row["target_skill_id"], row["edge_kind"]
    check(source in skill_set and target in skill_set, f"dangling prerequisite: {source}->{target}")
    check(source != target, f"self prerequisite: {source}")
    check(row["reason_kind"] in reason_kinds, f"invalid reason_kind: {row['reason_kind']}")
    check((source, target, kind) not in pairs, f"duplicate edge: {source}->{target}/{kind}")
    pairs.add((source, target, kind))
    if kind == "hard":
        hard_graph[source].append(target)
        indegree[target] += 1
check(not any((a, b, "hard") in pairs and (a, b, "soft") in pairs for a, b, _ in pairs), "hard/soft pair conflict")
queue = deque(x for x, degree in indegree.items() if degree == 0)
visited = 0
while queue:
    node = queue.popleft()
    visited += 1
    for target in hard_graph[node]:
        indegree[target] -= 1
        if indegree[target] == 0:
            queue.append(target)
check(visited == len(skill_set), "hard prerequisite cycle")
check(not any(a.startswith("skill.english.") and not b.startswith("skill.english.") and k == "hard" for a, b, k in pairs), "English global hard gate")

for row in requirements:
    check(row["capability_id"] in skill_set, f"requirement capability missing: {row['capability_id']}")
    check(row["scope_id"] in domain_set, f"requirement scope missing: {row['scope_id']}")
for row in professional:
    check(row["capability_id"] in skill_set | objective_set, f"professional attribution missing: {row['capability_id']}")
for row in projects:
    check(row["skill_id"] in skill_set and row["objective_id"] in objective_set, f"project attribution missing: {row}")
    check(row["structurally_essential"] and row["separately_observable"], f"non-observable project attribution: {row}")

fbb = (ROOT / "docs" / "V1_FOUNDATION_BACKBONE.md").read_text(encoding="utf-8")
expected_skill_seeds = set(re.findall(r"skill\.[a-z0-9_.]+", fbb))
expected_objective_seeds = set(re.findall(r"objective\.[a-z0-9_.]+", fbb))
mapped_skill_seeds = {x["seed_id"] for x in mappings if x["seed_kind"] == "skill"}
mapped_objective_seeds = {x["seed_id"] for x in mappings if x["seed_kind"] == "objective"}
check(mapped_skill_seeds == expected_skill_seeds and len(mapped_skill_seeds) == 41, "FBB Skill mapping is not exact 41/41")
check(mapped_objective_seeds == expected_objective_seeds and len(mapped_objective_seeds) == 47, "FBB Objective mapping is not exact 47/47")
check(all(ref in skill_set | objective_set for row in mappings for ref in row["result_entity_refs"]), "seed result ref missing")

source_ids = {x["source_id"] for x in sources}
check(len(source_ids) == len(sources), "duplicate source ID")
check(all((ROOT / x["ref"]).is_file() for x in sources), "source path missing")
check(manifest["blocking_review_count"] == 0, "manifest reports blocking review")
check(not any(x["severity"] == "blocking" and x["status"] == "open" for x in reviews), "open blocking review")
check(qa["result"] in {"PASS", "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS"}
      and (qa["result"] == "PASS") == (qa["counts"]["open_non_blocking_reviews"] == 0),
      "QA result mismatch")
check(qa["external_research_qa"]["status"] in {"pending", "validated_6H"}
      and qa["external_research_qa"]["owner_step"] == "6H", "6H external QA guard missing")

if failures:
    print("FOUNDATIONS_PACKAGE_QA=FAIL")
    for failure in failures:
        print(f"- {failure}")
    sys.exit(1)

print("FOUNDATIONS_PACKAGE_QA=PASS")
print(f"domains={len(domain_set)} modules={len(module_set)} topics={len(topic_set)}")
print(f"skills={len(skill_set)} objectives={len(objective_set)} topic_skill_links={len(links)}")
print(f"prerequisites={len(edges)} hard_dag_nodes={visited}/{len(skill_set)}")
print(f"fbb_seed_mappings={len(mapped_skill_seeds)}/41 skills + {len(mapped_objective_seeds)}/47 objectives")
print(f"open_blocking_reviews=0 open_non_blocking_reviews={len([x for x in reviews if x['status'] == 'open'])}")
