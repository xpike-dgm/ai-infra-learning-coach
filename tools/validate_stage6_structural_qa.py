from __future__ import annotations

from collections import Counter, defaultdict, deque
from pathlib import Path
import re
import unicodedata
import yaml

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "curriculum/decomposition/6h_research_qa/structural_report.yaml"
PACKAGES = {
    "6C": ROOT / "curriculum/decomposition/6c_foundations",
    "6D": ROOT / "curriculum/decomposition/6d_systems",
    "6E": ROOT / "curriculum/decomposition/6e_gpu_ml_inference",
    "6F": ROOT / "curriculum/decomposition/6f_professional_engineering",
}
WLRM = ROOT / "curriculum/decomposition/6g_weakness_remediation"


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def norm(text: str) -> str:
    text = unicodedata.normalize("NFKD", text or "").encode("ascii", "ignore").decode("ascii")
    return re.sub(r"[^a-z0-9]+", " ", text.lower()).strip()


def duplicate_groups(rows, key_fn):
    buckets = defaultdict(list)
    for row in rows:
        k = key_fn(row)
        if k:
            buckets[k].append(row)
    return {k: v for k, v in buckets.items() if len(v) > 1}


skills_by_step = {}
objectives_by_step = {}
edges_by_step = {}
manifests = {}
reviews_by_step = {}
all_skills = []
all_objectives = []
all_edges = []
route_ids = []

for step, pkg in PACKAGES.items():
    manifests[step] = load(pkg / "manifest.yaml") or {}
    skills = load(pkg / "skills.yaml") or []
    objectives = load(pkg / "objectives.yaml") or []
    edges = load(pkg / "prerequisite_edges.yaml") or []
    reviews = load(pkg / "review_queue.yaml") or []
    skills_by_step[step] = skills
    objectives_by_step[step] = objectives
    edges_by_step[step] = edges
    reviews_by_step[step] = reviews
    all_skills.extend(skills)
    all_objectives.extend(objectives)
    all_edges.extend(edges)
    route_ids.extend(manifests[step].get("route_family_ids", []))

skill_ids = [r.get("skill_id_candidate") for r in all_skills]
objective_ids = [r.get("objective_id_candidate") for r in all_objectives]
skill_set = set(skill_ids)
objective_set = set(objective_ids)

blocking = []
warnings = []

# Identity uniqueness.
skill_id_dupes = [k for k, n in Counter(skill_ids).items() if k and n > 1]
objective_id_dupes = [k for k, n in Counter(objective_ids).items() if k and n > 1]
if skill_id_dupes:
    blocking.append({"code": "DUPLICATE_SKILL_ID", "items": sorted(skill_id_dupes)})
if objective_id_dupes:
    blocking.append({"code": "DUPLICATE_OBJECTIVE_ID", "items": sorted(objective_id_dupes)})

# Exact/normalized semantic duplicate candidates. These are review candidates, not automatic blockers.
name_groups = duplicate_groups(all_skills, lambda r: norm(r.get("canonical_name", "")))
statement_groups = duplicate_groups(all_skills, lambda r: norm(r.get("capability_statement", "")))
name_candidates = []
for k, rows in sorted(name_groups.items()):
    ids = sorted({r.get("skill_id_candidate") for r in rows})
    if len(ids) > 1:
        name_candidates.append({"normalized_name": k, "skill_ids": ids})
statement_candidates = []
for k, rows in sorted(statement_groups.items()):
    ids = sorted({r.get("skill_id_candidate") for r in rows})
    if len(ids) > 1:
        statement_candidates.append({"normalized_statement": k, "skill_ids": ids})

# Objective ownership.
dangling_objective_owners = sorted({r.get("owner_skill_id") for r in all_objectives if r.get("owner_skill_id") not in skill_set})
if dangling_objective_owners:
    blocking.append({"code": "DANGLING_OBJECTIVE_OWNER", "items": dangling_objective_owners})
owned_counts = Counter(r.get("owner_skill_id") for r in all_objectives)
skills_without_objective = sorted(s for s in skill_set if owned_counts[s] == 0)
if skills_without_objective:
    blocking.append({"code": "SKILL_WITHOUT_OBJECTIVE", "items": skills_without_objective})

# Prerequisite graph integrity.
dangling_edge_endpoints = []
self_edges = []
pair_kinds = defaultdict(set)
hard_adj = defaultdict(list)
hard_indeg = {s: 0 for s in skill_set}
for edge in all_edges:
    src = edge.get("prerequisite_skill_id")
    dst = edge.get("target_skill_id")
    kind = edge.get("edge_kind")
    if src not in skill_set or dst not in skill_set:
        dangling_edge_endpoints.append({"source": src, "target": dst, "kind": kind})
        continue
    if src == dst:
        self_edges.append({"skill_id": src, "kind": kind})
    pair_kinds[(src, dst)].add(kind)
    if kind == "hard":
        hard_adj[src].append(dst)
        hard_indeg[dst] += 1

if dangling_edge_endpoints:
    blocking.append({"code": "DANGLING_PREREQUISITE_ENDPOINT", "items": dangling_edge_endpoints})
if self_edges:
    blocking.append({"code": "SELF_PREREQUISITE", "items": self_edges})
conflicting_edges = [
    {"source": a, "target": b, "kinds": sorted(kinds)}
    for (a, b), kinds in pair_kinds.items() if len(kinds) > 1
]
if conflicting_edges:
    blocking.append({"code": "CONFLICTING_HARD_SOFT_EDGE", "items": conflicting_edges})

# Combined hard DAG.
q = deque(sorted(s for s, d in hard_indeg.items() if d == 0))
visited = []
indeg = dict(hard_indeg)
while q:
    node = q.popleft()
    visited.append(node)
    for nxt in hard_adj[node]:
        indeg[nxt] -= 1
        if indeg[nxt] == 0:
            q.append(nxt)
if len(visited) != len(skill_set):
    cycle_nodes = sorted(s for s, d in indeg.items() if d > 0)
    blocking.append({"code": "HARD_GRAPH_CYCLE", "items": cycle_nodes})

hard_in = Counter()
hard_out = Counter()
for src, targets in hard_adj.items():
    hard_out[src] += len(targets)
    for dst in targets:
        hard_in[dst] += 1
hard_roots = sorted(s for s in skill_set if hard_in[s] == 0)
hard_sinks = sorted(s for s in skill_set if hard_out[s] == 0)

# Route-family envelope should be exact D01..D23 once.
expected_routes = {f"D{i:02d}" for i in range(1, 24)}
route_counter = Counter(route_ids)
missing_routes = sorted(expected_routes - set(route_ids))
duplicate_routes = sorted(r for r, n in route_counter.items() if n > 1)
unexpected_routes = sorted(set(route_ids) - expected_routes)
if missing_routes or duplicate_routes or unexpected_routes:
    blocking.append({
        "code": "ROUTE_FAMILY_ENVELOPE_MISMATCH",
        "missing": missing_routes,
        "duplicates": duplicate_routes,
        "unexpected": unexpected_routes,
    })

# Freshness hygiene: report tool/runtime-specific capabilities that look evergreen/unscoped.
freshness_counts = Counter(r.get("freshness_class", "missing") for r in all_skills)
tool_scoped_candidates = []
for row in all_skills:
    kind = row.get("capability_kind", "")
    cls = row.get("shared_vs_specific", {}).get("classification", "")
    toolish = "tool_specific" in kind or cls in {"domain_or_tool_specific", "tool_specific"}
    if toolish and row.get("freshness_class") == "evergreen" and not row.get("technology_dependency_refs"):
        tool_scoped_candidates.append(row.get("skill_id_candidate"))
if tool_scoped_candidates:
    warnings.append({
        "code": "TOOL_SPECIFIC_EVERGREEN_REVIEW",
        "count": len(tool_scoped_candidates),
        "items": sorted(tool_scoped_candidates),
    })

# Open reviews assigned to 6H.
reviews_6h = []
for step, rows in reviews_by_step.items():
    for row in rows:
        if row.get("status") == "open" and str(row.get("resolution_owner_step", "")) == "6H":
            reviews_6h.append({
                "source_step": step,
                "review_id": row.get("review_id"),
                "severity": row.get("severity"),
                "question": row.get("question"),
            })
# 6G reviews are not in PACKAGES but one belongs to 6H.
for row in load(WLRM / "review_queue.yaml") or []:
    if row.get("status") == "open" and str(row.get("resolution_owner_step", "")) == "6H":
        reviews_6h.append({
            "source_step": "6G",
            "review_id": row.get("review_id"),
            "severity": row.get("severity"),
            "question": row.get("question"),
        })

wlrm_qa = load(WLRM / "qa_report.yaml") or {}
if wlrm_qa.get("counts", {}).get("skills_covered") != len(skill_set):
    blocking.append({"code": "WLRM_SKILL_COVERAGE_MISMATCH"})
if wlrm_qa.get("counts", {}).get("objectives_covered") != len(objective_set):
    blocking.append({"code": "WLRM_OBJECTIVE_COVERAGE_MISMATCH"})

report = {
    "stage_step": "6H",
    "report_kind": "internal_structural_pre_external_research_qa",
    "status": "PASS_INTERNAL_STRUCTURE_EXTERNAL_RESEARCH_PENDING" if not blocking else "FAIL_INTERNAL_STRUCTURE",
    "completion_guard": {
        "stage6_complete": False,
        "reason": "Independent external Research AI validation is mandatory before 6H can close.",
    },
    "counts": {
        "source_packages": len(PACKAGES),
        "route_families": len(set(route_ids)),
        "skills": len(skill_set),
        "objectives": len(objective_set),
        "prerequisite_edges": len(all_edges),
        "hard_edges": sum(1 for e in all_edges if e.get("edge_kind") == "hard"),
        "soft_edges": sum(1 for e in all_edges if e.get("edge_kind") == "soft"),
        "hard_dag_nodes": len(visited),
        "hard_roots": len(hard_roots),
        "hard_sinks": len(hard_sinks),
        "open_6h_reviews": len(reviews_6h),
        "exact_name_duplicate_candidates": len(name_candidates),
        "exact_statement_duplicate_candidates": len(statement_candidates),
    },
    "package_counts": {
        step: {
            "routes": len(manifests[step].get("route_family_ids", [])),
            "skills": len(skills_by_step[step]),
            "objectives": len(objectives_by_step[step]),
            "prerequisite_edges": len(edges_by_step[step]),
        }
        for step in PACKAGES
    },
    "checks": [
        {"check": "route_family_envelope_D01_D23", "result": "PASS" if not (missing_routes or duplicate_routes or unexpected_routes) else "FAIL"},
        {"check": "global_skill_id_uniqueness", "result": "PASS" if not skill_id_dupes else "FAIL"},
        {"check": "global_objective_id_uniqueness", "result": "PASS" if not objective_id_dupes else "FAIL"},
        {"check": "objective_owner_integrity", "result": "PASS" if not dangling_objective_owners else "FAIL"},
        {"check": "every_skill_has_objective", "result": "PASS" if not skills_without_objective else "FAIL"},
        {"check": "prerequisite_endpoint_integrity", "result": "PASS" if not dangling_edge_endpoints else "FAIL"},
        {"check": "no_self_prerequisite", "result": "PASS" if not self_edges else "FAIL"},
        {"check": "no_conflicting_hard_soft_pair", "result": "PASS" if not conflicting_edges else "FAIL"},
        {"check": "combined_hard_graph_dag", "result": "PASS" if len(visited) == len(skill_set) else "FAIL", "details": f"{len(visited)}/{len(skill_set)} nodes"},
        {"check": "wlrm_exact_registry_coverage", "result": "PASS" if wlrm_qa.get("counts", {}).get("skills_covered") == len(skill_set) and wlrm_qa.get("counts", {}).get("objectives_covered") == len(objective_set) else "FAIL"},
    ],
    "semantic_duplicate_review_candidates": {
        "same_normalized_canonical_name": name_candidates,
        "same_normalized_capability_statement": statement_candidates,
    },
    "freshness_hygiene": {
        "freshness_class_counts": dict(sorted(freshness_counts.items())),
        "tool_specific_evergreen_review_candidates": sorted(tool_scoped_candidates),
    },
    "graph_shape_information": {
        "hard_root_skill_ids": hard_roots,
        "hard_sink_skill_ids": hard_sinks,
        "note": "Roots/sinks are informational; a professional capability graph legitimately has entry and terminal Skills. External QA decides whether any are pedagogical dead ends.",
    },
    "open_reviews_owned_by_6H": reviews_6h,
    "blocking_findings": blocking,
    "warnings": warnings,
    "external_research_qa": {
        "status": "required_pending",
        "independence_requirement": "Must be produced by a separate external Research AI/evaluator; manager web research cannot substitute.",
    },
}

OUT.parent.mkdir(parents=True, exist_ok=True)
OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=120), encoding="utf-8")

print("STAGE6_STRUCTURAL_QA=" + ("PASS_INTERNAL_STRUCTURE_EXTERNAL_RESEARCH_PENDING" if not blocking else "FAIL_INTERNAL_STRUCTURE"))
print(f"routes={len(set(route_ids))}/23 skills={len(skill_set)} objectives={len(objective_set)} edges={len(all_edges)}")
print(f"hard_dag_nodes={len(visited)}/{len(skill_set)} open_6h_reviews={len(reviews_6h)}")
print(f"duplicate_name_candidates={len(name_candidates)} duplicate_statement_candidates={len(statement_candidates)}")
if warnings:
    print(f"warnings={len(warnings)}")
if blocking:
    for item in blocking:
        print("BLOCKING:", item.get("code"))
    raise SystemExit(1)
