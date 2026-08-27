from __future__ import annotations

from collections import defaultdict, deque
from pathlib import Path
import argparse
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
BLUEPRINT = ROOT / "curriculum" / "english" / "7a_entry_diagnostic" / "blueprint.yaml"
REPORT = ROOT / "curriculum" / "english" / "7a_entry_diagnostic" / "qa_report.yaml"
SKILLS = ROOT / "curriculum" / "decomposition" / "6c_foundations" / "skills.yaml"
OBJECTIVES = ROOT / "curriculum" / "decomposition" / "6c_foundations" / "objectives.yaml"
EDGES = ROOT / "curriculum" / "decomposition" / "6c_foundations" / "prerequisite_edges.yaml"
REVIEWS = ROOT / "curriculum" / "decomposition" / "6c_foundations" / "review_queue.yaml"
SPEC = ROOT / "docs" / "ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md"
RESEARCH = ROOT / "research" / "7a_english_entry_diagnostic_research.md"


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def main(write_report: bool) -> int:
    blueprint = load(BLUEPRINT)
    skills = load(SKILLS)
    objectives = load(OBJECTIVES)
    edges = load(EDGES)
    reviews = load(REVIEWS)
    spec_text = SPEC.read_text(encoding="utf-8")

    checks: list[dict] = []
    failures: list[str] = []

    def check(name: str, condition: bool, details: str) -> None:
        checks.append({"check": name, "result": "PASS" if condition else "FAIL", "details": details})
        if not condition:
            failures.append(f"{name}: {details}")

    english_skills = {
        row["skill_id_candidate"]: row
        for row in skills
        if "domain.technical_english" in row.get("shared_placement_domain_ids", [])
    }
    english_ids = set(english_skills)

    claims = blueprint.get("claims", [])
    claim_ids = [row.get("skill_id") for row in claims]
    claim_by_id = {row.get("skill_id"): row for row in claims}

    check("E7A-01_exact_d01_scope_count", len(english_ids) == 15, f"canonical D01 English Skills={len(english_ids)}")
    check("E7A-01_blueprint_scope_exact", set(claim_ids) == english_ids and len(claim_ids) == len(set(claim_ids)), f"blueprint claims={len(claim_ids)} canonical={len(english_ids)}")
    check("E7A-01_expected_count_metadata", blueprint.get("scope", {}).get("expected_skill_count") == len(english_ids), "expected_skill_count matches canonical registry")

    english_objectives = [row for row in objectives if row.get("owner_skill_id") in english_ids]
    owners = {row.get("owner_skill_id") for row in english_objectives}
    check("E7A-04_objective_ownership", owners == english_ids, f"English objective owners cover {len(owners)}/{len(english_ids)} Skills")
    check("E7A-04_diagnostic_eligible", all(english_skills[sid].get("diagnostic_eligibility") == "eligible" for sid in english_ids), "all D01 Skills diagnostic eligible")

    canonical_hard_pairs = {
        (row["prerequisite_skill_id"], row["target_skill_id"])
        for row in edges
        if row.get("edge_kind") == "hard"
        and row.get("prerequisite_skill_id") in english_ids
        and row.get("target_skill_id") in english_ids
    }
    blueprint_hard_pairs = {
        (source, claim["skill_id"])
        for claim in claims
        for source in claim.get("hard_prerequisite_skill_ids", [])
    }
    check("E7A-02_english_hard_dag_match", blueprint_hard_pairs == canonical_hard_pairs, f"blueprint hard pairs={len(blueprint_hard_pairs)} canonical={len(canonical_hard_pairs)}")

    # Derive roots and verify DAG.
    indegree = {sid: 0 for sid in english_ids}
    graph: dict[str, list[str]] = defaultdict(list)
    for source, target in canonical_hard_pairs:
        graph[source].append(target)
        indegree[target] += 1
    roots = sorted(sid for sid, degree in indegree.items() if degree == 0)
    expected_roots = sorted(blueprint.get("routing", {}).get("entry_root_skill_ids", []))
    check("E7A-02_entry_roots_match", roots == expected_roots, f"roots={roots}")
    queue = deque(roots)
    seen = 0
    indegree_work = dict(indegree)
    while queue:
        node = queue.popleft()
        seen += 1
        for nxt in graph[node]:
            indegree_work[nxt] -= 1
            if indegree_work[nxt] == 0:
                queue.append(nxt)
    check("E7A-02_english_graph_dag", seen == len(english_ids), f"visited={seen}/{len(english_ids)}")

    # Existing project invariant: English cannot globally hard-gate technical Skills.
    cross_english_hard = [
        row for row in edges
        if row.get("edge_kind") == "hard"
        and row.get("prerequisite_skill_id") in english_ids
        and row.get("target_skill_id") not in english_ids
    ]
    check("E7A-03_no_technical_global_gate", not cross_english_hard and blueprint.get("invariants", {}).get("technical_curriculum_global_english_gate") is False, "no English→technical hard edge and blueprint global gate=false")

    # Claim evidence must be compatible with canonical Skill evidence.
    evidence_mismatches = []
    for sid, claim in claim_by_id.items():
        if sid not in english_skills:
            continue
        canonical_types = english_skills[sid].get("independent_evidence_path", {}).get("direct_evidence_types", [])
        if claim.get("primary_evidence_type") not in canonical_types:
            evidence_mismatches.append({"skill_id": sid, "declared": claim.get("primary_evidence_type"), "canonical": canonical_types})
    check("E7A-05_evidence_compatibility", not evidence_mismatches, f"mismatches={evidence_mismatches}")

    task_families = blueprint.get("task_families", [])
    task_by_id = {row.get("task_family_id"): row for row in task_families}
    claim_task_ids = [row.get("task_family_id") for row in claims]
    check("E7A-05_one_task_family_per_claim", len(task_families) == len(english_ids) and set(claim_task_ids) == set(task_by_id), f"task families={len(task_families)}")
    check("E7A-05_task_targets_match", all(task_by_id[c["task_family_id"]].get("target_skill_id") == c["skill_id"] for c in claims), "every task family targets its claim Skill")

    inv = blueprint.get("invariants", {})
    check("E7A-06_self_report_non_evidence", inv.get("self_report_is_evidence") is False and "mastery_write" in blueprint.get("self_report_context", {}).get("prohibited_uses", []), "self-report cannot write mastery")
    check("E7A-07_no_easier_threshold", inv.get("diagnostic_pass_percentage") is None and inv.get("fixed_item_count_for_mastery") is None and inv.get("diagnostic_mastery_standard") == "GRE-v0", "no new pass percentage/item count; GRE-v0 preserved")
    check("E7A-08_cefr_boundary", blueprint.get("cefr_alignment_status") == "pending_7B" and blueprint.get("cefr_level") is None and blueprint.get("cefr_handoff", {}).get("may_assign_final_level_in_7A") is False, "7A does not assign CEFR level")

    required_resolutions = {
        "mastery_or_waiver_confirmed",
        "evidence_present_not_yet_sufficient",
        "learning_needed_clean_evidence",
        "prerequisite_unresolved",
        "not_assessed",
        "user_deferred",
        "invalid_or_contaminated",
    }
    check("E7A-09_profile_resolution_vocabulary", set(blueprint.get("profile_resolution_vocabulary", [])) == required_resolutions, "safe diagnostic resolution vocabulary exact")
    check("E7A-09_invalid_no_negative_write", "do_not_write_target_negative_evidence" in blueprint.get("routing", {}).get("invalid_or_contaminated_actions", []), "invalid/contaminated attempt cannot write target negative evidence")
    check("E7A-10_no_downstream_fail_broadcast", inv.get("downstream_failure_broadcast") is False and "mark_hard_dependents_prerequisite_unresolved" in blueprint.get("routing", {}).get("clean_failure_actions", []), "downstream dependents become prerequisite_unresolved")

    production_claims = [row for row in claims if row.get("diagnostic_tier") in {"production", "production_interaction"}]
    check("E7A-11_production_prerequisite_guard", all(row.get("hard_prerequisite_skill_ids") and row.get("default_stage") == "production_probe" for row in production_claims), f"production claims={len(production_claims)}")

    check("E7A-12_resource_trust", inv.get("resource_trust_policy") == "QAB-v0+AIV-v0" and all(row.get("mastery_capable_only_when_trusted_and_GRE_valid") is True for row in task_families), "all mastery-capable families require trusted resource + GRE validity")
    check("E7A-13_pause_safe", {"user_deferred", "session_capacity_exhausted"} <= set(blueprint.get("routing", {}).get("pause_conditions", [])), "pause/resume states represented")
    check("E7A-14_profile_not_broad_score", blueprint.get("scope", {}).get("broad_domain_score_required") is False and "entry_frontier_skill_ids" in spec_text, "granular frontier profile; no mandatory broad score")

    future = blueprint.get("future_stage_boundaries", {})
    check("E7A-15_stage_boundary", all(step in future for step in ["7B", "7C", "7D", "7E", 15, 18]), "future ownership boundaries declared")

    cefr_review = next((row for row in reviews if row.get("review_id") == "review.6c.english.cefr_alignment"), None)
    check("E7A-15_cefr_review_stays_open_for_7B", bool(cefr_review) and cefr_review.get("status") == "open" and cefr_review.get("resolution_owner_step") == "7B", "CEFR alignment review remains owned by 7B")

    check("research_basis_exists", RESEARCH.is_file(), str(RESEARCH.relative_to(ROOT)))
    check("spec_exists", SPEC.is_file() and "EED-v0" in spec_text and "D-063" in spec_text, str(SPEC.relative_to(ROOT)))

    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "7A",
        "model": "EED-v0",
        "candidate_decision": "D-063",
        "result": result,
        "checked_at": "2026-08-27",
        "counts": {
            "canonical_d01_english_skills": len(english_ids),
            "canonical_d01_english_objectives": len(english_objectives),
            "canonical_english_hard_edges": len(canonical_hard_pairs),
            "blueprint_claims": len(claims),
            "task_families": len(task_families),
        },
        "checks": checks,
        "failures": failures,
        "cefr_alignment_review": {
            "review_id": "review.6c.english.cefr_alignment",
            "expected_status": "open",
            "owner_step": "7B",
        },
    }

    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

    print(f"7A_ENGLISH_ENTRY_DIAGNOSTIC_QA={result}")
    print(f"skills={len(english_ids)} objectives={len(english_objectives)} hard_edges={len(canonical_hard_pairs)} task_families={len(task_families)}")
    if failures:
        for failure in failures:
            print("-", failure)
        return 1
    return 0


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--write-report", action="store_true")
    args = parser.parse_args()
    sys.exit(main(args.write_report))
