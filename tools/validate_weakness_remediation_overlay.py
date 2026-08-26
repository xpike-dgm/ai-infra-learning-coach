from __future__ import annotations

from collections import defaultdict
from pathlib import Path
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
PKG = ROOT / "curriculum/decomposition/6g_weakness_remediation"
SOURCE_PACKAGES = [
    ROOT / "curriculum/decomposition/6c_foundations",
    ROOT / "curriculum/decomposition/6d_systems",
    ROOT / "curriculum/decomposition/6e_gpu_ml_inference",
    ROOT / "curriculum/decomposition/6f_professional_engineering",
]
EXPECTED = [
    "manifest.yaml", "sources.yaml", "state_contract.yaml", "skill_weakness_profiles.yaml",
    "objective_weakness_profiles.yaml", "failure_attribution_rules.yaml", "remediation_strategies.yaml",
    "remediation_routes.yaml", "learning_need_mappings.yaml", "aggregation_guards.yaml",
    "misconception_contract.yaml", "tag_catalog.yaml", "review_queue.yaml", "qa_report.yaml",
]


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


failures: list[str] = []

def check(cond: bool, msg: str) -> None:
    if not cond:
        failures.append(msg)


check(all((PKG / name).is_file() for name in EXPECTED), "logical overlay collection missing")
if failures:
    print("WEAKNESS_REMEDIATION_OVERLAY_QA=FAIL")
    for x in failures:
        print("- " + x)
    raise SystemExit(1)

manifest = load(PKG / "manifest.yaml")
state_contract = load(PKG / "state_contract.yaml")
skill_profiles = load(PKG / "skill_weakness_profiles.yaml")
objective_profiles = load(PKG / "objective_weakness_profiles.yaml")
failure_rules = load(PKG / "failure_attribution_rules.yaml")
strategies = load(PKG / "remediation_strategies.yaml")
routes = load(PKG / "remediation_routes.yaml")
need_mappings = load(PKG / "learning_need_mappings.yaml")
guards = load(PKG / "aggregation_guards.yaml")
misconception = load(PKG / "misconception_contract.yaml")
tag_catalog = load(PKG / "tag_catalog.yaml")
reviews = load(PKG / "review_queue.yaml")
qa = load(PKG / "qa_report.yaml")

accepted_skills = {}
accepted_objectives = {}
for directory in SOURCE_PACKAGES:
    for row in load(directory / "skills.yaml"):
        sid = row["skill_id_candidate"]
        check(sid not in accepted_skills, f"source registry duplicate Skill: {sid}")
        accepted_skills[sid] = row
    for row in load(directory / "objectives.yaml"):
        oid = row["objective_id_candidate"]
        check(oid not in accepted_objectives, f"source registry duplicate Objective: {oid}")
        accepted_objectives[oid] = row

skill_profile_by_id = {x["skill_id"]: x for x in skill_profiles}
objective_profile_by_id = {x["objective_id"]: x for x in objective_profiles}
route_by_objective = {x["objective_id"]: x for x in routes}
strategy_ids = {x["strategy_id"] for x in strategies}
need_mapping_ids = {x["mapping_id"] for x in need_mappings}

# Exact coverage / no new identity.
check(len(skill_profile_by_id) == len(skill_profiles), "duplicate Skill profile")
check(len(objective_profile_by_id) == len(objective_profiles), "duplicate Objective profile")
check(set(skill_profile_by_id) == set(accepted_skills), "Skill overlay coverage is not exact accepted 6C-6F registry")
check(set(objective_profile_by_id) == set(accepted_objectives), "Objective overlay coverage is not exact accepted 6C-6F registry")
check(manifest.get("adds_new_curriculum_skills") is False, "6G must not add curriculum Skills")
check(manifest.get("adds_new_curriculum_objectives") is False, "6G must not add curriculum Objectives")
check(manifest.get("model_id") == "WLRM-v0", "unexpected 6G model id")

# Skill profile semantics.
owners = defaultdict(list)
for oid, obj in accepted_objectives.items():
    owners[obj["owner_skill_id"]].append(oid)
for sid, row in skill_profile_by_id.items():
    check(set(row["objective_ids"]) == set(owners[sid]), f"Skill objective coverage mismatch: {sid}")
    check(row["localization_unit"] == "objective_first_then_skill_gate", f"Skill bypasses objective-first localization: {sid}")
    check(row["broad_parent_reset_allowed"] is False, f"broad reset enabled: {sid}")
    check(bool(row["resolution_rule"]), f"Skill resolution rule missing: {sid}")

# Objective profile + route contract.
for oid, row in objective_profile_by_id.items():
    source = accepted_objectives[oid]
    check(row["owner_skill_id"] == source["owner_skill_id"], f"Objective owner mismatch: {oid}")
    check(row["required_direct_type"] == source["evidence_profile"]["required_direct_type"], f"direct type drift: {oid}")
    check(row["localization_unit"] == "objective", f"Objective not localization unit: {oid}")
    check(row["broad_parent_reset_allowed"] is False, f"Objective broad reset enabled: {oid}")
    fc = row["failure_confirmation"]
    check(fc["assisted_or_provisional"] == "hypothesis_only", f"assisted/provisional can confirm weakness: {oid}")
    check(fc["first_clean_postmastery_contradiction"] == "verification_due", f"post-mastery hysteresis bypass: {oid}")
    rc = row["recheck_contract"]
    check(all(rc[x] is True for x in ["fresh_variant_or_context_required", "H0_required_for_closure", "prerequisite_valid_required", "direct_evidence_required", "evaluator_verified_required"]), f"unsafe recheck contract: {oid}")
    check(row["candidate_strategy_ids"], f"Objective has no remediation strategy: {oid}")
    check(set(row["candidate_strategy_ids"]) <= strategy_ids, f"Objective references unknown strategy: {oid}")
    check(oid in route_by_objective, f"Objective route missing: {oid}")

check(len(route_by_objective) == len(routes) == len(accepted_objectives), "route count/objective uniqueness mismatch")
for oid, route in route_by_objective.items():
    check(route["skill_id"] == accepted_objectives[oid]["owner_skill_id"], f"route owner mismatch: {oid}")
    check(set(route["candidate_strategy_ids"]) <= strategy_ids, f"route unknown strategy: {oid}")
    check(set(route["planner_mapping_ids"]) <= need_mapping_ids, f"route unknown LearningNeed mapping: {oid}")
    forbidden = set(route["forbidden_shortcuts"])
    check("task_completion_closes_remediation" in forbidden, f"task completion shortcut not forbidden: {oid}")
    check("broad_topic_or_domain_reset" in forbidden, f"broad reset shortcut not forbidden: {oid}")
    check("manual_mastery_score_override" in forbidden, f"manual mastery override not forbidden: {oid}")

# Required failure attribution safety rules.
rules = {x["rule_id"]: x for x in failure_rules}
required_rule_ids = {
    "failure.invalid_or_ambiguous",
    "failure.prerequisite_contamination",
    "failure.unattempted_or_deferred",
    "failure.environment_outside_target",
    "failure.assisted_h1_h4",
    "failure.provisional_or_partial",
    "failure.clean_premastery_h0_direct",
    "failure.first_clean_postmastery_contradiction",
    "failure.fresh_recheck_fail",
    "failure.integrated_global_outcome_guard",
    "failure.review_due_without_negative_evidence",
    "failure.fresh_recovery_success",
}
check(required_rule_ids <= set(rules), "required failure attribution rule missing")
if required_rule_ids <= set(rules):
    check(rules["failure.prerequisite_contamination"]["gre_effect"] == "no_target_negative_evidence", "prerequisite contamination writes target negative evidence")
    check(rules["failure.first_clean_postmastery_contradiction"]["target_effect"] == "verification_due", "first contradiction does not map to verification_due")
    check(rules["failure.fresh_recheck_fail"]["target_effect"] == "remediation_required", "failed fresh recheck does not map to remediation")
    check(rules["failure.review_due_without_negative_evidence"]["target_effect"] == "no_weakness", "review_due incorrectly becomes weakness")
    check(rules["failure.integrated_global_outcome_guard"]["gre_effect"] == "no_broadcast_to_tagged_objectives", "integrated outcome broadcast guard missing")

# Planner mapping safety.
need_by_id = {x["mapping_id"]: x for x in need_mappings}
check("need.map.verification_due" in need_by_id, "verification LearningNeed mapping missing")
check("need.map.confirmed_remediation" in need_by_id, "remediation LearningNeed mapping missing")
check("need.map.prerequisite_gap" in need_by_id, "prerequisite repair mapping missing")
check("need.map.retention_review_only" in need_by_id, "retention review mapping missing")
if "need.map.confirmed_remediation" in need_by_id:
    check(need_by_id["need.map.confirmed_remediation"]["default_priority_band"] == "P1", "confirmed remediation must default P1")
    check("P0 only" in need_by_id["need.map.confirmed_remediation"].get("priority_override", ""), "P0 integrity-blocker restriction missing")
if "need.map.retention_review_only" in need_by_id:
    check("remediation" not in need_by_id["need.map.retention_review_only"]["trigger_kind"], "review_due incorrectly mapped to remediation")

# Strategy / tag bridge coverage.
check(len(strategy_ids) == len(strategies), "duplicate remediation strategy id")
check("strategy.fresh_independent_recheck" in strategy_ids, "fresh independent recheck strategy missing")
check("strategy.prerequisite_refresh" in strategy_ids, "prerequisite refresh strategy missing")
source_tags = {tag for s in accepted_skills.values() for tag in (s.get("remediation_tags") or [])}
source_tags |= {tag for o in accepted_objectives.values() for tag in (o.get("remediation_tags") or [])}
catalog_tags = {x["source_remediation_tag"] for x in tag_catalog}
check(catalog_tags == source_tags, "source remediation tag catalog incomplete or has extras")
for row in tag_catalog:
    check(bool(row["mapped_strategy_ids"]), f"tag has no strategy bridge: {row['source_remediation_tag']}")
    check(set(row["mapped_strategy_ids"]) <= strategy_ids, f"tag maps to unknown strategy: {row['source_remediation_tag']}")

# Aggregation guards / misconception runtime contract.
guard_ids = {x["guard_id"] for x in guards["rules"]}
for gid in {
    "guard.no_domain_reset", "guard.no_topic_broadcast", "guard.no_project_broadcast",
    "guard.branch_local_prerequisite_effect", "guard.review_due_not_weakness", "guard.english_not_global_failure",
}:
    check(gid in guard_ids, f"aggregation guard missing: {gid}")
check(misconception["identity_scope"] == "learner_specific_runtime_state_not_curriculum_skill_identity", "misconception identity incorrectly becomes curriculum identity")
check(set(misconception["states"]) == {"hypothesis", "supported", "confirmed", "resolved"}, "misconception states unexpected")

# State contract must preserve upstream models rather than invent a new mastery formula.
invariants = set(state_contract["invariants"])
for inv in {
    "attempt_failure_is_not_automatically_skill_failure",
    "invalid_or_prerequisite_contaminated_attempt_never_writes_target_negative_evidence",
    "first_clean_post_mastery_contradiction_opens_verification_due_before_mastery_erasure",
    "remediation_closure_requires_new_evidence_not_task_completion",
    "topic_domain_states_are_derived_and_never_the_primary_remediation_target",
    "review_due_without_negative_evidence_is_not_weakness",
}:
    check(inv in invariants, f"state invariant missing: {inv}")

# Review / QA handoff.
check(all(x["severity"] == "non_blocking" for x in reviews), "blocking review unexpectedly present")
check(any(x["resolution_owner_step"] == "6H" and x["status"] == "open" for x in reviews), "6H external review handoff missing")
check(qa["result"] == "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS", "QA result unexpected")
check(qa["counts"]["skills_covered"] == len(accepted_skills), "QA Skill count mismatch")
check(qa["counts"]["objectives_covered"] == len(accepted_objectives), "QA Objective count mismatch")
check(qa["counts"]["remediation_routes"] == len(accepted_objectives), "QA route count mismatch")
check(qa["counts"]["open_blocking_reviews"] == 0, "QA reports blocking review")
check(qa["external_research_qa"]["status"] == "pending" and qa["external_research_qa"]["owner_step"] == "6H", "external Research QA handoff wrong")

if failures:
    print("WEAKNESS_REMEDIATION_OVERLAY_QA=FAIL")
    for failure in failures:
        print("- " + failure)
    raise SystemExit(1)

print("WEAKNESS_REMEDIATION_OVERLAY_QA=PASS")
print(f"skills_covered={len(accepted_skills)} objectives_covered={len(accepted_objectives)} routes={len(routes)}")
print(f"failure_rules={len(failure_rules)} strategies={len(strategies)} source_tags={len(tag_catalog)}")
print(f"open_blocking_reviews=0 open_non_blocking_reviews={len([x for x in reviews if x['status'] == 'open'])}")
