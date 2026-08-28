from __future__ import annotations

import argparse
from collections import deque
from pathlib import Path
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
SPEC = ROOT / "docs/INFORMATION_ARCHITECTURE_SPEC.md"
RESEARCH = ROOT / "research/8a_information_architecture_research.md"
REPORT = ROOT / "ux/8a_information_architecture/qa_report.yaml"

checks: list[dict] = []
failures: list[str] = []


def check(name: str, condition: bool, details: str) -> None:
    result = "PASS" if condition else "FAIL"
    checks.append({"check": name, "result": result, "details": details})
    if not condition:
        failures.append(f"{name}: {details}")


def load_yaml(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write-report", action="store_true")
    args = parser.parse_args()

    for path in [IA, SPEC, RESEARCH]:
        check("E8A-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None)

    ia = load_yaml(IA)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")

    check("E8A-01_identity", ia.get("model") == "UXIA-v0" and ia.get("stage_step") == "8A", f"model={ia.get('model')} step={ia.get('stage_step')}")
    check("E8A-01_status", ia.get("status") in {"candidate_8a", "accepted_8a"}, f"status={ia.get('status')}")
    check("E8A-01_decision", (ia.get("candidate_decision") or ia.get("decision")) == "D-068", f"decision={ia.get('candidate_decision') or ia.get('decision')}")

    primary = ia["primary_destinations"]
    ids = [x["id"] for x in sorted(primary, key=lambda x: x["order"])]
    check("E8A-02_primary_count", len(primary) == 4 and ia["scope"]["top_level_destination_count"] == 4, f"count={len(primary)}")
    check("E8A-02_primary_exact_order", ids == ["today", "learn", "progress", "profile"], f"order={ids}")
    check("E8A-02_start_today", ia["scope"]["normal_start_destination"] == "today", f"start={ia['scope']['normal_start_destination']}")
    check("E8A-02_unique_primary", len(ids) == len(set(ids)), f"ids={ids}")

    forbidden = set(ia["top_level_forbidden"])
    check("E8A-03_forbidden_not_primary", not (forbidden & set(ids)), f"intersection={sorted(forbidden & set(ids))}")
    required_forbidden = {"assessment", "english", "ai_chat", "ai_tutor", "mastery", "retention", "remediation", "weakness", "streak", "leaderboard"}
    check("E8A-03_forbidden_policy_complete", required_forbidden <= forbidden, f"missing={sorted(required_forbidden - forbidden)}")

    roots = ia["surface_families"]["shell_roots"]
    root_ids = [x["id"] for x in roots]
    check("E8A-04_shell_roots", set(root_ids) == {"today_overview", "learn_overview", "progress_overview", "profile_overview"} and len(root_ids) == 4, f"roots={root_ids}")
    root_owner = {x["owner"] for x in roots}
    check("E8A-04_shell_owner_exact", root_owner == set(ids), f"owners={sorted(root_owner)}")

    shared = ia["surface_families"]["shared_details"]
    shared_ids = [x["id"] for x in shared]
    required_shared = {"topic_detail", "skill_detail", "planner_explanation", "assessment_report", "technical_english_profile", "learning_history"}
    check("E8A-05_shared_required", required_shared <= set(shared_ids), f"missing={sorted(required_shared - set(shared_ids))}")
    check("E8A-05_shared_unique", len(shared_ids) == len(set(shared_ids)), f"ids={shared_ids}")
    skill_rows = [x for x in shared if x["id"] == "skill_detail"]
    check("E8A-05_single_skill_detail", len(skill_rows) == 1 and skill_rows[0].get("shared_single_semantic_surface") is True, f"rows={len(skill_rows)}")
    planner = next(x for x in shared if x["id"] == "planner_explanation")
    check("E8A-05_planner_trace_owner", planner.get("source_contract") == "PDT-v0" and planner.get("free_form_ai_truth") is False, f"source={planner.get('source_contract')}")
    english = next(x for x in shared if x["id"] == "technical_english_profile")
    check("E8A-05_english_profile_owner", english.get("source_contract") == "TEPM-v0" and english.get("general_cefr_claim") is False, f"source={english.get('source_contract')}")
    report = next(x for x in shared if x["id"] == "assessment_report")
    check("E8A-05_assessment_not_truth", report.get("direct_mastery_authority") is False, "assessment report cannot own mastery")

    focus = ia["surface_families"]["focused_flows"]
    focus_ids = {x["id"] for x in focus}
    check("E8A-06_focus_flows", focus_ids == {"task_runner_flow", "assessment_session_flow"}, f"flows={sorted(focus_ids)}")
    check("E8A-06_safe_exit", all(x.get("safe_pause_exit_required") is True for x in focus), "all focused flows require safe pause/exit")
    owner_map = {x["id"]: str(x.get("detailed_choreography_owner")) for x in focus}
    check("E8A-06_future_owners", owner_map == {"task_runner_flow": "8C", "assessment_session_flow": "8D"}, f"owners={owner_map}")

    setup = ia["surface_families"]["setup"]
    check("E8A-07_setup_boundary", len(setup) == 1 and setup[0]["id"] == "initial_setup_flow" and setup[0]["mandatory_diagnostic_invented_by_ia"] is False, f"setup={setup}")

    ownership = {x["object"]: x for x in ia["information_ownership"]}
    required_objects = {
        "current_plan", "next_task", "current_capacity", "planner_reason", "curriculum_hierarchy",
        "topic_state", "exact_skill_state", "weakness_remediation", "review_verification_due",
        "assessment_due", "assessment_report_history", "technical_english_profile", "learning_history",
        "daily_capacity_preference", "reminders", "theme_accessibility_preferences", "backup_export_restore",
    }
    check("E8A-08_information_coverage", required_objects <= set(ownership), f"missing={sorted(required_objects - set(ownership))}")
    check("E8A-08_plan_today", ownership["current_plan"]["primary_home"] == "today" and ownership["next_task"]["primary_home"] == "today", "plan/next task owned by Today")
    check("E8A-08_curriculum_learn", ownership["curriculum_hierarchy"]["primary_home"] == "learn", "curriculum owned by Learn")
    check("E8A-08_progress_states", all(ownership[k]["primary_home"] == "progress" for k in ["weakness_remediation", "review_verification_due", "assessment_report_history"]), "state/history owned by Progress")
    check("E8A-08_profile_ops", all(ownership[k]["primary_home"] == "profile" for k in ["daily_capacity_preference", "reminders", "theme_accessibility_preferences", "backup_export_restore"]), "operation settings owned by Profile")

    inv = ia["invariants"]
    expected_false = [
        "ui_is_mastery_source_of_truth", "ui_hierarchy_is_prerequisite_truth", "task_completion_is_mastery",
        "assessment_score_is_direct_mastery", "english_is_global_technical_gate", "general_cefr_claim_allowed",
        "career_completion_percentage_primary", "streak_primary_success_metric", "ai_chat_default_start",
        "ai_required_for_primary_shell", "stale_deep_link_can_bypass_eligibility", "adaptive_layout_changes_semantic_ia",
    ]
    check("E8A-09_invariant_false_guards", all(inv[k] is False for k in expected_false), f"violations={[k for k in expected_false if inv[k] is not False]}")
    check("E8A-09_single_skill_truth", inv["shared_skill_detail_single_semantic_surface"] is True, "shared skill detail invariant")

    progress = ia["progress_guards"]
    forbidden_progress = set(progress["forbidden_primary_summaries"])
    required_progress = {"career_completion_percentage", "elapsed_day_percentage", "streak_success", "task_completion_as_mastery", "numeric_cefr_average", "broad_domain_failure_without_skill_detail"}
    check("E8A-10_progress_guards", required_progress <= forbidden_progress, f"missing={sorted(required_progress - forbidden_progress)}")
    required_states = {"not_started", "developing", "confirmed", "review_due", "verification_due", "remediation_required", "prerequisite_unresolved"}
    check("E8A-10_state_distinctions", required_states <= set(progress["distinct_states_must_remain_representable"]), f"missing={sorted(required_states - set(progress['distinct_states_must_remain_representable']))}")

    assessment = ia["assessment_guards"]
    check("E8A-11_assessment_pipeline", assessment["workflow_pipeline"] == ["assessment_session", "attempt_artifact", "validity_attribution_evidence", "canonical_state_update", "planner_replan", "report_explanation"], f"pipeline={assessment['workflow_pipeline']}")
    check("E8A-11_no_gradebook_shortcut", assessment["score_to_broad_mastery_shortcut"] == "forbidden" and assessment["permanent_top_level_destination"] is False, "assessment remains contextual workflow")

    eg = ia["english_guards"]
    check("E8A-12_english_contexts", set(eg["contexts"]) == {"today", "learn", "progress"}, f"contexts={sorted(eg['contexts'])}")
    check("E8A-12_english_no_silo", eg["permanent_top_level_destination"] is False and eg["global_technical_gate"] is False and eg["official_or_general_cefr_claim"] is False, "English integrated without overclaim")

    state_set = set(ia["cross_cutting_surface_states"])
    required_cross = {"loading", "empty_valid", "error_recoverable", "offline_local_available", "ai_unavailable_core_available", "data_recovery_required"}
    check("E8A-13_cross_cutting_states", state_set == required_cross, f"states={sorted(state_set)}")

    entry = ia["external_entry_points"]
    check("E8A-14_deeplink_revalidation", entry["must_revalidate_current_state_before_action"] is True, "stale external entries are revalidated")

    adaptive = ia["adaptive_navigation"]
    check("E8A-15_adaptive_semantic_order", adaptive["semantic_order"] == ["today", "learn", "progress", "profile"], f"order={adaptive['semantic_order']}")
    check("E8A-15_component_deferred", adaptive["component_choice_deferred"] is True and adaptive["semantic_labels_consistent_across_window_sizes"] is True, "component implementation deferred, semantics stable")

    access = ia["accessibility_semantics"]
    check("E8A-16_accessibility_baseline", all(access.values()), f"values={access}")

    future = ia["future_stage_boundaries"]
    check("E8A-17_8b_8g_boundaries", all(str(k) in {str(x) for x in future.keys()} for k in ["8B", "8C", "8D", "8E", "8F", "8G"]), f"keys={list(future)}")
    check("E8A-17_no_premature_lock", ia["scope"]["final_visual_design_locked"] is False and ia["scope"]["implementation_technology_locked"] is False and ia["scope"]["physical_navigation_component_locked"] is False and ia["scope"]["physical_data_schema_locked"] is False, "later-stage boundaries preserved")

    # Reachability: shell peer pairs are bidirectional; contextual edges are directed.
    graph: dict[str, set[str]] = {}
    def add(a: str, b: str) -> None:
        graph.setdefault(a, set()).add(b)
    for a, b in ia["navigation_edges"]["shell_peer_switching"]:
        add(a, b); add(b, a)
    for a, b in ia["navigation_edges"]["contextual"]:
        add(a, b)
    q = deque(["today"])
    seen = {"today"}
    while q:
        cur = q.popleft()
        for nxt in graph.get(cur, set()):
            if nxt not in seen:
                seen.add(nxt); q.append(nxt)
    required_reachable = set(ids) | required_shared | focus_ids
    check("E8A-18_route_reachability", required_reachable <= seen, f"unreachable={sorted(required_reachable - seen)}")

    # Canonical owner regression anchors.
    owners = ia["canonical_truth_owners"]
    expected_owners = {
        "mastery": "GRE-v0", "retention": "RVR-v0", "prerequisite": "PRG-v0",
        "planner_priority": "PBR-v0", "planner_explanation": "PDT-v0",
        "diagnostic_waiver": "VDW-v0", "weakness_remediation": "WLRM-v0",
        "curriculum_identity": "KGC-v0", "english_progression": "TECP-v0",
        "english_daily": "DECP-v0", "english_integration": "TEIP-v0", "english_profile": "TEPM-v0",
    }
    check("E8A-19_truth_owners", owners == expected_owners, f"owners={owners}")

    spec_markers = [
        "Today → Learn → Progress → Profile",
        "Assessment is a **workflow/evidence source**",
        "No permanent `English` top-level destination",
        "Shared detail routes preserve origin context",
        "Browse hierarchy is not prerequisite hierarchy",
        "8B — Ana ekran",
        "8G — Wireframe/prototip",
    ]
    check("E8A-20_spec_contract", all(m in spec for m in spec_markers), f"missing={[m for m in spec_markers if m not in spec]}")

    research_markers = [
        "developer.android.com/develop/ui/compose/components/navigation-bar",
        "developer.android.com/develop/adaptive-apps/guides/build-adaptive-navigation",
        "w3.org/WAI/WCAG21/Understanding/consistent-navigation",
        "w3.org/WAI/WCAG22/Understanding/consistent-identification",
        "nngroup.com/articles/mobile-sharpens-usability-guidelines",
        "nngroup.com/articles/mini-ia-structuring-information",
    ]
    check("E8A-21_research_authoritative", all(m in research for m in research_markers), f"missing={[m for m in research_markers if m not in research]}")
    check("E8A-21_research_no_visual_overreach", "No external source justifies a fixed card count" in research, "research does not invent visual precision")

    return finish(args.write_report, ia)


def finish(write_report: bool, ia) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "8A",
        "model": "UXIA-v0",
        "candidate_decision": "D-068",
        "result": result,
        "status_observed": ia.get("status") if isinstance(ia, dict) else None,
        "counts": {
            "checks": len(checks),
            "failures": len(failures),
            "top_level_destinations": len(ia.get("primary_destinations", [])) if isinstance(ia, dict) else 0,
            "shared_detail_surfaces": len(ia.get("surface_families", {}).get("shared_details", [])) if isinstance(ia, dict) else 0,
            "focused_flows": len(ia.get("surface_families", {}).get("focused_flows", [])) if isinstance(ia, dict) else 0,
            "information_objects": len(ia.get("information_ownership", [])) if isinstance(ia, dict) else 0,
            "research_sources": len(ia.get("research_sources", [])) if isinstance(ia, dict) else 0,
        },
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"8A_INFORMATION_ARCHITECTURE_QA={result}")
    print(f"checks={len(checks)} failures={len(failures)}")
    if isinstance(ia, dict):
        print(f"top_level={len(ia.get('primary_destinations', []))} shared={len(ia.get('surface_families', {}).get('shared_details', []))} focus={len(ia.get('surface_families', {}).get('focused_flows', []))}")
    for f in failures:
        print("-", f)
    return 0 if result == "PASS" else 1


if __name__ == "__main__":
    sys.exit(main())
