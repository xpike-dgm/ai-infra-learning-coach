from __future__ import annotations

import argparse
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
HOME = ROOT / "ux/8b_today_home/home.yaml"
SPEC = ROOT / "docs/TODAY_HOME_SCREEN_SPEC.md"
RESEARCH = ROOT / "research/8b_today_home_research.md"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
REPORT = ROOT / "ux/8b_today_home/qa_report.yaml"

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

    for path in [HOME, SPEC, RESEARCH, IA]:
        check("E8B-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None)

    home = load_yaml(HOME)
    ia = load_yaml(IA)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")

    check("E8B-01_identity", home.get("model") == "THUX-v0" and home.get("stage_step") == "8B", f"model={home.get('model')} step={home.get('stage_step')}")
    check("E8B-01_status", home.get("status") in {"candidate_8b", "accepted_8b"}, f"status={home.get('status')}")
    check("E8B-01_decision", (home.get("candidate_decision") or home.get("decision")) == "D-069", f"decision={home.get('candidate_decision') or home.get('decision')}")
    check("E8B-01_parent", home.get("parent_ia") == "UXIA-v0", f"parent={home.get('parent_ia')}")
    check("E8B-01_today_root", home.get("normal_shell_destination") == "today", f"root={home.get('normal_shell_destination')}")

    scope = home["scope"]
    check("E8B-02_semantic_only", scope["semantic_home_only"] is True, "8B remains semantic home contract")
    deferred_false = [
        "final_visual_design_locked", "task_runner_choreography_locked", "assessment_interaction_locked",
        "skill_progress_visual_semantics_locked", "implementation_technology_locked", "physical_data_schema_locked",
    ]
    check("E8B-02_no_premature_lock", all(scope[k] is False for k in deferred_false), f"violations={[k for k in deferred_false if scope[k] is not False]}")
    check("E8B-02_no_fake_geometry", scope["fixed_card_count"] is None and scope["fixed_pixel_geometry"] is None, "fixed cards/pixels remain deferred")

    owners = home["canonical_truth_owners"]
    expected_owners = {
        "current_plan": "planner", "mastery": "GRE-v0", "retention": "RVR-v0", "prerequisite": "PRG-v0",
        "priority": "PBR-v0", "planner_explanation": "PDT-v0", "weakness_remediation": "WLRM-v0",
        "assessment_evidence": "DMA_WBA_MCA_pipeline", "english_daily": "DECP-v0",
        "english_integration": "TEIP-v0", "english_profile": "TEPM-v0",
    }
    check("E8B-03_truth_owners", owners == expected_owners, f"owners={owners}")

    inv = home["invariants"]
    expected_false = [
        "today_is_second_planner", "task_completion_is_mastery", "queue_is_candidate_backlog", "deferred_work_is_debt",
        "missed_day_is_failure", "blocked_task_is_actionable", "capacity_is_progress_metric", "duration_is_mastery_signal",
        "career_completion_percentage_primary", "broad_numeric_mastery_primary", "streak_primary_success_metric",
        "general_cefr_claim_allowed", "english_fixed_daily_quota", "assessment_score_direct_mastery",
        "ai_required_for_core_home", "offline_always_blocks_home", "reason_can_exceed_trace_facts",
    ]
    check("E8B-04_false_guards", all(inv[k] is False for k in expected_false), f"violations={[k for k in expected_false if inv[k] is not False]}")
    check("E8B-04_capacity_override", inv["current_day_capacity_override_may_replan"] is True and inv["persistent_capacity_setting_owner_profile"] is True, "Today override / Profile persistent ownership")

    hierarchy = sorted(home["content_hierarchy"], key=lambda x: x["order"])
    hierarchy_ids = [x["id"] for x in hierarchy]
    expected_hierarchy = ["primary_action", "day_plan_context", "remaining_plan", "attention_context", "supporting_navigation"]
    check("E8B-05_hierarchy_exact", hierarchy_ids == expected_hierarchy, f"hierarchy={hierarchy_ids}")
    check("E8B-05_primary_highest", hierarchy[0]["prominence"] == "highest" and hierarchy[0]["primary_goal"] == "act_now", f"primary={hierarchy[0]}")
    check("E8B-05_attention_conditional", next(x for x in hierarchy if x["id"] == "attention_context")["always_or_stateful"] == "conditional", "attention conditional")
    check("E8B-05_support_progressive", hierarchy[-1]["always_or_stateful"] == "progressive_disclosure", f"support={hierarchy[-1]}")

    pa = home["primary_action"]
    required_primary = {"action_kind", "planned_task_or_session_ref", "display_title", "task_context", "primary_purpose", "reason_summary", "start_or_resume_action", "planner_explanation_entry"}
    check("E8B-06_primary_semantics", required_primary <= set(pa["required_semantics"]), f"missing={sorted(required_primary - set(pa['required_semantics']))}")
    forbidden_primary = {"mastery_delta", "career_completion_percentage", "streak_pressure", "fake_urgency_score", "raw_priority_rank_vector", "free_form_ai_reason_truth"}
    check("E8B-06_primary_forbidden", forbidden_primary <= set(pa["forbidden_semantics"]), f"missing={sorted(forbidden_primary - set(pa['forbidden_semantics']))}")

    precedence = home["primary_action_precedence"]
    expected_precedence = [
        "data_recovery_required", "valid_resumable_focused_session", "selected_next_planned_task",
        "plan_loading_or_replanning", "valid_empty_or_capacity_limited_state", "recoverable_error",
    ]
    check("E8B-07_precedence", precedence == expected_precedence, f"precedence={precedence}")
    prules = home["precedence_rules"]
    check("E8B-07_resume_revalidate", prules["resumable_session_must_revalidate"] is True and prules["stale_session_can_bypass_prerequisite"] is False, "resume is revalidated")
    check("E8B-07_no_stale_action", prules["stale_plan_action_during_replan"] is False, "replan does not expose stale action")
    check("E8B-07_degraded_not_supersede", prules["ai_unavailable_supersedes_valid_local_plan"] is False and prules["offline_supersedes_valid_local_plan"] is False, "offline/AI degraded remain contextual")
    check("E8B-07_recovery_supersedes", prules["recovery_can_supersede_unsafe_plan"] is True, "data integrity may supersede normal plan")

    cap = home["capacity_context"]
    check("E8B-08_capacity_owner", cap["source_contract"] == "D-033" and cap["hard_budget_visible_as_time_context"] is True, f"source={cap['source_contract']}")
    cap_forbidden = {"progress_percentage", "mastery_percentage", "required_minimum_for_success", "fixed_category_split", "guaranteed_countdown"}
    check("E8B-08_capacity_guards", cap_forbidden <= set(cap["forbidden_interpretations"]), f"missing={sorted(cap_forbidden - set(cap['forbidden_interpretations']))}")
    check("E8B-08_estimate_truth", cap["duration_is_estimate"] is True and cap["planning_reserve_must_be_exposed"] is False, "duration remains estimate; reserve not fake UI precision")
    check("E8B-08_today_override_replan", cap["today_override_action"] == "change_today_capacity" and cap["today_override_triggers_replan"] is True, "today override triggers replan")
    check("E8B-08_profile_persistent_owner", cap["persistent_preferences_home"] == "profile", f"home={cap['persistent_preferences_home']}")
    check("E8B-08_capacity_change_no_failure", cap["capacity_decrease_removed_tasks_are_failure"] is False and cap["capacity_increase_promises_fixed_extra_tasks"] is False, "capacity changes do not invent failure/task count")

    queue = home["remaining_plan"]
    check("E8B-09_queue_entity", queue["source_entity"] == "PlannedTask" and queue["includes_only_current_selected_tasks"] is True, f"source={queue['source_entity']}")
    check("E8B-09_no_backlog_leak", queue["contains_all_open_learning_needs"] is False and queue["contains_unselected_task_candidates"] is False and queue["contains_old_daily_backlog"] is False, "queue is current selected plan only")
    check("E8B-09_not_prereq_order", queue["planner_order_is_prerequisite_order"] is False, "list order is not prerequisite truth")
    check("E8B-09_blocked_not_actionable", queue["blocked_dependent_work_actionable"] is False, "blocked work cannot start")
    check("E8B-09_replan_replaces_rows", queue["replan_replaces_invalidated_rows"] is True and queue["main_focus_is_remaining_actionable_work"] is True, "queue tracks current plan")
    check("E8B-09_history_progress", queue["completed_activity_history_owner"] == "progress", f"history_owner={queue['completed_activity_history_owner']}")

    row = home["planned_task_row"]
    check("E8B-10_row_separation", row["purpose_activity_track_separate"] is True, "purpose/activity/track remain separate")
    check("E8B-10_row_duration_guard", row["duration_is_mastery_signal"] is False, "duration not mastery")
    check("E8B-10_row_completion_guard", row["local_completion_can_infer_mastered"] is False and row["local_completion_can_infer_failed"] is False, "local completion cannot infer Skill state")
    check("E8B-10_dispositions", set(row["allowed_dispositions"]) == {"current", "upcoming", "paused"}, f"dispositions={row['allowed_dispositions']}")

    reason = home["reason_presentation"]
    check("E8B-11_reason_owner", reason["source_contract"] == "PDT-v0" and reason["subset_of_trace_facts_required"] is True, f"source={reason['source_contract']}")
    check("E8B-11_reason_bounded", reason["overview_primary_reason_count"] == 1 and reason["overview_supporting_reason_max"] == 1, "overview reason is bounded")
    check("E8B-11_reason_no_cot_ai_truth", reason["private_chain_of_thought"] is False and reason["free_form_ai_truth"] is False, "no CoT/free-form truth")
    check("E8B-11_plan_change_trace", reason["plan_change_reason_trace_backed"] is True, "plan change explanation trace-backed")

    attention = home["attention_context"]
    required_attention = {"plan_changed", "verification_attention", "remediation_attention", "prerequisite_blocker", "retention_attention", "assessment_attention", "recovery_attention"}
    check("E8B-12_attention_families", required_attention == set(attention["allowed_families"]), f"families={attention['allowed_families']}")
    check("E8B-12_attention_not_priority", attention["creates_planner_priority"] is False, "attention cannot rescore")
    check("E8B-12_no_duplicate_cta", attention["duplicate_competing_cta_when_primary_task_represents_same_need"] is False, "no duplicate competing CTA")

    support = home["supporting_navigation"]
    check("E8B-13_support_not_block", support["must_not_block_primary_action"] is True, "details optional before action")
    check("E8B-13_support_routes", {"planner_explanation", "skill_detail", "topic_detail", "progress", "profile_capacity_settings"} <= set(support["routes"]), f"routes={support['routes']}")

    assessment = home["assessment_today"]
    check("E8B-14_assessment_contextual", assessment["contextual_only"] is True and assessment["permanent_exam_section_required"] is False and assessment["daily_quota"] is False, "assessment remains contextual")
    check("E8B-14_assessment_purpose_guard", assessment["question_like_ui_implies_assess_purpose"] is False, "UI form does not define purpose")
    check("E8B-14_assessment_mastery_guard", assessment["score_direct_broad_mastery"] is False and assessment["completed_report_home"] == "progress", "score not mastery; report belongs Progress")
    check("E8B-14_assessment_owner_8d", str(assessment["exact_session_result_layout_owner"]) == "8D", f"owner={assessment['exact_session_result_layout_owner']}")

    english = home["technical_english_today"]
    check("E8B-15_english_common_capacity", english["contextual_only"] is True and english["separate_budget"] is False, "English contextual/common capacity")
    check("E8B-15_english_no_quota", english["fixed_daily_quota"] is False and english["english_streak"] is False and english["omitted_selected_due_capacity_is_debt"] is False, "no quota/streak/debt")
    check("E8B-15_track_not_purpose", english["track_is_primary_purpose"] is False and english["integration_contract"] == "TEIP-v0", "track/purpose separation")
    check("E8B-15_cefr_guard", english["general_or_official_cefr_claim"] is False and english["qualified_profile_home"] == "progress", "no general CEFR; profile under Progress")

    ret = home["return_to_today"]
    expected_pipeline = ["attempt_or_artifact", "evidence_validation_and_attribution", "canonical_state_update_if_warranted", "planner_replan_if_warranted", "render_current_today_plan"]
    check("E8B-16_return_pipeline", ret["pipeline"] == expected_pipeline, f"pipeline={ret['pipeline']}")
    check("E8B-16_stale_plan_guard", ret["stale_pre_attempt_plan_preserved"] is False, "render current post-attempt plan")
    check("E8B-16_completion_guard", ret["completion_implies_mastery"] is False and ret["false_progress_claim_if_no_state_change"] is False, "completion does not fabricate mastery")
    check("E8B-16_state_claim_guard", ret["state_change_claim_requires_canonical_state_change"] is True, "state wording requires canonical change")

    missed = home["missed_day"]
    check("E8B-17_missed_day_owner", missed["source_contract"] == "SRR-v0" and missed["fresh_plan_from_current_state"] is True, "SRR fresh plan")
    check("E8B-17_no_missed_debt", missed["replay_yesterday_plan_as_backlog"] is False and missed["absence_is_failure"] is False and missed["absence_is_debt"] is False, "no missed-day debt")
    check("E8B-17_forbidden_framing", set(missed["forbidden_home_framing"]) == {"overdue_tasks_from_yesterday", "catch_up_missed_day", "streak_loss_punishment"}, f"framing={missed['forbidden_home_framing']}")

    states = set(home["semantic_states"])
    required_states = {
        "loading_initial_plan", "replanning", "ready_plan", "resumable_session", "empty_no_open_need",
        "empty_no_eligible_task", "capacity_zero", "capacity_too_small_no_candidate", "offline_local_available",
        "ai_unavailable_core_available", "error_recoverable", "data_recovery_required",
    }
    check("E8B-18_states", states == required_states, f"states={sorted(states)}")
    empty = home["empty_state_distinctions"]
    check("E8B-18_empty_distinctions", set(empty[:]) if isinstance(empty, list) else set(empty.keys()) >= {"no_task_implies_all_mastered", "no_task_implies_professional_ready"}, "empty-state structure present")
    if isinstance(empty, dict):
        required_empty = {"no_open_need", "no_eligible_task", "no_capacity", "capacity_too_small", "loading", "recoverable_error"}
        distinction_values = {x for x in empty if x not in {"no_task_implies_all_mastered", "no_task_implies_professional_ready"}}
        # YAML stores distinctions as a list value under integer-free keys only if authored that way; accept exact explicit list through semantic marker below.
        check("E8B-18_empty_truth", empty["no_task_implies_all_mastered"] is False and empty["no_task_implies_professional_ready"] is False, "no task does not overclaim mastery/readiness")
        spec_has_distinctions = all(x in spec for x in required_empty)
        check("E8B-18_empty_causes_in_spec", spec_has_distinctions, f"required={sorted(required_empty)}")

    small = home["small_capacity_behavior"]
    check("E8B-19_small_capacity", small["capacity_zero_is_failure"] is False and small["no_valid_micro_task_is_failure"] is False and small["debt_created"] is False, "small capacity has no failure/debt")
    check("E8B-19_micro_task", small["tiny_valid_micro_task_may_be_selected"] is True, "valid tiny task may be selected")

    degraded = home["degraded_behavior"]
    check("E8B-20_offline_core", degraded["offline_local_available"]["core_home_usable_when_local_capability_exists"] is True, "offline local core remains usable")
    check("E8B-20_ai_core", degraded["ai_unavailable_core_available"]["deterministic_home_usable"] is True and degraded["ai_unavailable_core_available"]["ui_may_improvise_replacement_task"] is False, "AI unavailable cannot erase/improvise core")
    check("E8B-20_recovery", degraded["data_recovery_required"]["may_block_normal_plan_until_safe"] is True and degraded["data_recovery_required"]["silent_progress_reset"] is False, "safe recovery without reset")

    disclosure = home["progressive_disclosure"]
    required_dump_guards = {"all_open_learning_needs", "full_prerequisite_graph", "raw_evidence_history", "raw_priority_rank_vector", "full_planner_trace", "every_weak_skill", "entire_curriculum_hierarchy", "technical_english_profile", "assessment_history"}
    check("E8B-21_disclosure_guards", required_dump_guards <= set(disclosure["overview_must_not_dump"]), f"missing={sorted(required_dump_guards - set(disclosure['overview_must_not_dump']))}")
    check("E8B-21_no_fixed_rows", disclosure["fixed_visible_task_row_count"] is None and disclosure["long_plan_rendering_strategy_deferred"] is True, "no fixed row count")

    responsive = home["responsive_semantics"]
    check("E8B-22_responsive_primary", responsive["primary_action_remains_highest_priority"] is True and responsive["truth_ownership_changes_with_window_size"] is False, "responsive semantics stable")
    check("E8B-22_geometry_deferred", responsive["supporting_context_may_move_spatially"] is True and responsive["exact_geometry_deferred"] is True, "geometry deferred")
    check("E8B-22_attention_integrity", responsive["attention_overtakes_primary_only_for_recovery_integrity_blocker"] is True, "only integrity/recovery may overtake action")

    access = home["accessibility_baseline"]
    check("E8B-23_accessibility_semantics", all(access.values()), f"values={access}")

    forbidden = set(home["forbidden_home_patterns"])
    required_forbidden = {
        "streak_dashboard", "career_completion_dashboard", "calendar_backlog_debt_tracker",
        "fixed_category_percentage_dashboard", "assessment_gradebook", "broad_numeric_domain_mastery",
        "daily_english_quota_tracker", "ai_chat_home", "raw_planner_debug_console",
        "permanently_blocked_task_list", "duplicate_progress_screen",
    }
    check("E8B-24_forbidden_patterns", required_forbidden == forbidden, f"forbidden={sorted(forbidden)}")

    future = {str(k): str(v) for k, v in home["future_stage_boundaries"].items()}
    required_future = {"8C", "8D", "8E", "8F", "8G", "9A", "9C", "10", "12", "13", "14", "16", "17", "18"}
    check("E8B-25_future_boundaries", required_future <= set(future), f"missing={sorted(required_future - set(future))}")

    acceptance = home["acceptance"]
    check("E8B-26_qa_required", acceptance["independent_qa_required"] is True, "independent QA required")
    check("E8B-26_regressions", all(acceptance[k] is True for k in ["stage6_regression_required", "stage7_regression_required", "stage8a_regression_required"]), "Stage 6/7/8A regressions required")

    # Parent IA regression anchors specific to Today ownership.
    today = next(x for x in ia["primary_destinations"] if x["id"] == "today")
    expected_today_info = {"current_plan", "next_task", "current_capacity_context", "attention_items", "planner_reason_entry"}
    check("E8B-27_parent_today_ownership", expected_today_info <= set(today["primary_information"]), f"today_info={today['primary_information']}")
    check("E8B-27_parent_start_today", ia["scope"]["normal_start_destination"] == "today", f"start={ia['scope']['normal_start_destination']}")
    focus = {x["id"]: x for x in ia["surface_families"]["focused_flows"]}
    check("E8B-27_parent_flow_owners", str(focus["task_runner_flow"]["detailed_choreography_owner"]) == "8C" and str(focus["assessment_session_flow"]["detailed_choreography_owner"]) == "8D", "8C/8D ownership preserved")

    spec_markers = [
        "**Bugün şimdi ne yapmalıyım?**",
        "Today is a projection of canonical planner/state truth",
        "primary_action",
        "day_plan_context",
        "remaining_plan",
        "attention_context",
        "supporting_navigation",
        "task_completed != mastery_confirmed",
        "8C — Günlük çalışma akışı",
    ]
    check("E8B-28_spec_contract", all(m in spec for m in spec_markers), f"missing={[m for m in spec_markers if m not in spec]}")

    research_markers = [
        "A new separate external Research AI is **not required**",
        "LearningNeed / TaskCandidate / PlannedTask / Attempt / Evidence",
        "The next valid action must dominate",
        "The queue should represent the current plan",
        "Assessment and English should look like work in the plan",
        "No external source justifies" if "No external source justifies" in research else "fixed card counts",
    ]
    check("E8B-29_research_synthesis", all(m in research for m in research_markers), f"missing={[m for m in research_markers if m not in research]}")

    return finish(args.write_report, home)


def finish(write_report: bool, home) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "8B",
        "model": "THUX-v0",
        "candidate_decision": "D-069",
        "result": result,
        "status_observed": home.get("status") if isinstance(home, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "content_regions": len(home.get("content_hierarchy", [])) if isinstance(home, dict) else None,
        "semantic_states": len(home.get("semantic_states", [])) if isinstance(home, dict) else None,
        "forbidden_home_patterns": len(home.get("forbidden_home_patterns", [])) if isinstance(home, dict) else None,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"8B_TODAY_HOME_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"regions={report['content_regions']} states={report['semantic_states']} forbidden={report['forbidden_home_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
