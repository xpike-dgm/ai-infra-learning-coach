from __future__ import annotations

import argparse
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
SESSION = ROOT / "ux/8d_assessment_session/session.yaml"
SPEC = ROOT / "docs/ASSESSMENT_SESSION_UX_SPEC.md"
RESEARCH = ROOT / "research/8d_assessment_session_research.md"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
FLOW = ROOT / "ux/8c_daily_working_flow/flow.yaml"
REPORT = ROOT / "ux/8d_assessment_session/qa_report.yaml"

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

    for path in [SESSION, SPEC, RESEARCH, IA, FLOW]:
        check("E8D-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None)

    s = load_yaml(SESSION)
    ia = load_yaml(IA)
    flow = load_yaml(FLOW)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")

    # --- 1. Identity -------------------------------------------------------
    check("E8D-01_identity", s.get("model") == "ASUX-v0" and s.get("stage_step") == "8D",
          f"model={s.get('model')} step={s.get('stage_step')}")
    check("E8D-01_status", s.get("status") in {"candidate_8d", "accepted_8d"}, f"status={s.get('status')}")
    check("E8D-01_decision", (s.get("candidate_decision") or s.get("decision")) == "D-071",
          f"decision={s.get('candidate_decision') or s.get('decision')}")
    check("E8D-01_parent_ia", s.get("parent_ia") == "UXIA-v0", f"parent={s.get('parent_ia')}")
    check("E8D-01_frame", s.get("focused_flow_frame") == "TRUX-v0", f"frame={s.get('focused_flow_frame')}")
    check("E8D-01_owning_surface", s.get("owning_ia_surface") == "assessment_session_flow",
          f"surface={s.get('owning_ia_surface')}")

    # --- 2. Scope boundary -------------------------------------------------
    scope = s["scope"]
    check("E8D-02_semantic_only", scope["semantic_session_only"] is True, "8D remains a semantic session contract")
    deferred_false = [
        "final_visual_design_locked", "skill_progress_visual_semantics_locked", "implementation_technology_locked",
        "physical_data_schema_locked", "question_bank_schema_locked", "evaluator_integration_locked",
    ]
    check("E8D-02_no_premature_lock", all(scope[k] is False for k in deferred_false),
          f"violations={[k for k in deferred_false if scope[k] is not False]}")
    check("E8D-02_stage4_untouched", scope["blueprint_policy_changed"] is False,
          "8D does not change Stage 4 blueprint policy")
    null_scope = ["passing_threshold", "percentage_grade", "fixed_question_count", "countdown_clock",
                  "fixed_pixel_geometry"]
    check("E8D-02_no_invented_constants", all(scope[k] is None for k in null_scope),
          f"violations={[k for k in null_scope if scope[k] is not None]}")

    # --- 3. Truth ownership ------------------------------------------------
    owners = s["canonical_truth_owners"]
    expected_owners = {
        "what_is_measured": "stage4_blueprint_policy", "daily_scope": "DMA-v0", "weekly_scope": "WBA-v0",
        "monthly_scope": "MCA-v0", "item_trust": "QAB-v0+AIV-v0",
        "assistance_evidence": "AI_assistance_2D_contract", "mastery": "GRE-v0", "retention": "RVR-v0",
        "prerequisite": "PRG-v0", "priority": "PBR-v0", "planner_explanation": "PDT-v0",
        "focused_flow_frame": "TRUX-v0", "english_integration": "TEIP-v0", "english_profile": "TEPM-v0",
        "longitudinal_reporting": "assessment_report", "diagnostic_execution": "task_runner_flow",
    }
    check("E8D-03_truth_owners", owners == expected_owners, f"owners={owners}")
    check("E8D-03_session_owns_no_engine_truth",
          not any(v in {"assessment_session_flow", "ASUX-v0", "session"} for v in owners.values()),
          "session must not own any canonical engine truth")
    check("E8D-03_diagnostics_not_owned", owners["diagnostic_execution"] == "task_runner_flow",
          "diagnostics remain task_runner_flow work")

    # --- 4. Invariant guards -----------------------------------------------
    inv = s["invariants"]
    expected_false = [
        "session_is_gradebook", "session_is_mastery_authority", "session_is_retention_authority",
        "session_is_second_state_engine", "session_redefines_focused_flow_frame", "session_owns_diagnostics",
        "session_duplicates_assessment_report", "scope_changes_rules", "scope_label_adds_evidence_weight",
        "atomic_boundary_split", "boundary_partially_scored", "submitted_boundary_revisitable",
        "submitted_boundary_editable", "post_submit_feedback_contaminates_prior_attempt",
        "unsubmitted_boundary_is_incorrect", "skipping_is_negative_evidence", "skipping_is_penalty",
        "assistance_blocked_during_assessment", "assistance_request_is_negative_evidence",
        "assistance_conversion_silent", "session_schedules_recheck",
        "same_item_after_exposure_is_independent_recheck", "pause_is_failure", "pause_is_assistance",
        "pause_is_mastery_signal", "recomposition_deletes_valid_evidence", "incomplete_session_is_penalty",
        "incomplete_session_creates_exam_debt", "missed_cycle_is_failure", "user_report_auto_invalidates_item",
        "dispute_harms_user_state", "dispute_is_undo_button", "provisional_presented_as_settled",
        "provisional_alone_settles_critical_transition", "invalid_item_gives_credit", "invalid_item_gives_penalty",
        "invalid_slot_counts_as_covered", "pass_fail_banner", "percentage_or_letter_grade",
        "passing_threshold_rule", "broad_numeric_domain_mastery", "raw_count_is_verdict",
        "not_reliably_measured_hidden", "not_reliably_measured_counted_as_incorrect",
        "state_change_claimed_without_canonical_change", "first_contradiction_causes_instant_unmastery",
        "reason_can_exceed_trace_facts", "countdown_pressure_device", "leaderboard_or_social_comparison",
        "exam_streak_or_quota", "dual_target_overall_pass_broadcast", "general_or_official_cefr_claim",
        "ai_unavailable_fakes_result", "offline_always_blocks_session",
        "objective_appropriate_tool_use_breaks_h0",
    ]
    check("E8D-04_false_guards", all(inv[k] is False for k in expected_false),
          f"violations={[k for k in expected_false if inv.get(k) is not False]}")
    expected_true = [
        "single_interior_for_all_scopes", "independence_mode_disclosed_before_response",
        "allowed_tools_disclosed_before_response", "unsubmitted_boundary_navigable",
        "exit_reachable_in_one_deliberate_action", "not_reliably_measured_is_first_class",
    ]
    check("E8D-04_true_guards", all(inv[k] is True for k in expected_true),
          f"violations={[k for k in expected_true if inv.get(k) is not True]}")
    check("E8D-04_guard_count", len(expected_false) >= 50, f"false_guards={len(expected_false)}")

    # --- 5. One interior, three scopes -------------------------------------
    sm = s["session_model"]
    check("E8D-05_scopes",
          set(sm["assessment_scopes"]) == {"daily_micro", "weekly_blueprint", "monthly_capability"},
          f"scopes={sm['assessment_scopes']}")
    check("E8D-05_scope_context_only",
          sm["scope_is_displayed_context_only"] is True and sm["scope_is_separate_screen_family"] is False,
          "scope is displayed context, not a screen family")
    check("E8D-05_intents",
          set(sm["assessment_intents"]) == {"checkpoint", "mastery_evidence", "verification", "integration_check"},
          f"intents={sm['assessment_intents']}")
    required_session_fields = {"session_id", "assessment_scope", "assessment_intent", "planned_task_ref",
                               "blocks", "independence_mode", "allowed_tools_policy", "session_state"}
    check("E8D-05_session_fields", required_session_fields <= set(sm["semantic_fields"]),
          f"missing={sorted(required_session_fields - set(sm['semantic_fields']))}")

    # --- 6. Block structure ------------------------------------------------
    bs = s["block_structure"]
    check("E8D-06_submission_unit", bs["submission_unit"] == "atomic_evidence_boundary",
          f"submission_unit={bs['submission_unit']}")
    check("E8D-06_boundary_kinds", set(bs["boundary_kinds"]) == {"item", "testlet"},
          f"boundary_kinds={bs['boundary_kinds']}")
    check("E8D-06_boundary_integrity",
          bs["boundary_never_split"] is True and bs["boundary_never_partially_scored"] is True,
          "atomic evidence boundary is never split or partially scored")
    check("E8D-06_checkpoint_inherited",
          bs["blocks_separated_by_safe_checkpoints"] is True and bs["checkpoint_contract_inherited_from"] == "TRUX-v0",
          f"checkpoint contract={bs['checkpoint_contract_inherited_from']}")
    check("E8D-06_artifact_not_split", bs["artifact_work_without_safe_checkpoint_split"] is False,
          "artifact work without a safe checkpoint is not arbitrarily split")
    check("E8D-06_dependency_group",
          bs["dependency_group_behaves_as_one_evidence_group"] is True
          and bs["dependency_group_members_confirm_each_other"] is False,
          "dependency group is one evidence group")

    # --- 7. Item view ------------------------------------------------------
    iv = s["item_view"]
    required_item = {"item_ref", "item_version", "boundary_ref", "prompt_content", "response_affordance",
                     "independence_mode", "allowed_tools_policy", "assistance_entry", "dispute_entry"}
    check("E8D-07_item_fields", required_item <= set(iv["semantic_fields"]),
          f"missing={sorted(required_item - set(iv['semantic_fields']))}")
    forbidden_item = {"target_objective_mastery_state", "running_score_or_percentage", "countdown_clock",
                      "difficulty_as_user_level", "other_user_comparison_or_rank",
                      "answer_or_solution_before_attempt", "streak_quota_or_completion_percentage"}
    check("E8D-07_item_forbidden", forbidden_item <= set(iv["forbidden_fields"]),
          f"missing={sorted(forbidden_item - set(iv['forbidden_fields']))}")
    check("E8D-07_position_orientation", iv["position_context_is_orientation_not_performance"] is True,
          "position context is orientation, not performance")

    # --- 8. Independence disclosure ----------------------------------------
    idis = s["independence_disclosure"]
    check("E8D-08_h0_default", idis["default_mode_for_mastery_or_verification"] == "h0_required",
          f"default={idis['default_mode_for_mastery_or_verification']}")
    check("E8D-08_disclosed",
          idis["disclosed_before_response"] is True and idis["allowed_tools_disclosed_before_response"] is True
          and idis["undisclosed_independence_rules"] is False, f"disclosure={idis}")
    check("E8D-08_tool_use_ok", idis["objective_appropriate_tool_use_breaks_h0"] is False,
          "objective-appropriate tool use does not break H0")

    # --- 9. Navigation and submission --------------------------------------
    nav = s["navigation_and_submission"]
    check("E8D-09_frozen",
          nav["submitted_boundary_is_frozen"] is True and nav["submitted_boundary_revisitable"] is False
          and nav["submitted_boundary_editable"] is False and nav["submitted_boundary_resubmittable"] is False,
          f"submission freeze={nav}")
    check("E8D-09_pre_submit_navigable",
          nav["unsubmitted_boundary_navigable_within_open_block"] is True and nav["free_order_answering_allowed"] is True,
          "unsubmitted boundaries stay navigable")
    check("E8D-10_skipping",
          nav["skipping_allowed"] is True and nav["unsubmitted_is_incorrect"] is False
          and nav["skipping_leaves_need_unresolved"] is True, f"skipping={nav}")
    check("E8D-10_exit",
          nav["exit_always_available"] is True and nav["exit_framed_as_quitting_or_waste"] is False,
          "exit available and never framed as waste")

    # --- 11. Assistance ----------------------------------------------------
    a = s["assistance_in_session"]
    check("E8D-11_not_blocked",
          a["blocked"] is False and a["request_is_negative_evidence"] is False, f"assistance={a['blocked']}")
    check("E8D-11_consequence",
          a["consequence_disclosed_before_revealing_help"] is True
          and a["consequence_framing_allowed"] == "what_this_attempt_can_still_prove"
          and a["consequence_framing_forbidden"] == "penalty_language",
          f"framing={a['consequence_framing_allowed']}/{a['consequence_framing_forbidden']}")
    levels = {lv["id"]: lv for lv in a["levels"]}
    check("E8D-11_levels", set(levels) == {"H1", "H2", "H3", "H4"}, f"levels={sorted(levels)}")
    check("E8D-12_no_independent_from_assistance",
          all(lv["independent_mastery_evidence"] is False for lv in levels.values()),
          "no assistance level yields independent mastery evidence")
    check("E8D-12_h1_h2_assisted",
          levels["H1"]["class"] == "assisted" and levels["H2"]["class"] == "assisted",
          f"H1/H2 class={levels['H1']['class']}/{levels['H2']['class']}")
    check("E8D-12_h3_h4_exposed",
          levels["H3"]["class"] == "practice_only_solution_exposed"
          and levels["H4"]["class"] == "practice_only_solution_exposed"
          and levels["H3"]["requires_fresh_unseen_item"] is True
          and levels["H4"]["requires_fresh_unseen_item"] is True,
          "H3/H4 are solution-exposed and require a fresh unseen item")
    check("E8D-13_conversion",
          a["mode_conversion_explicit"] is True and a["mode_conversion_silent"] is False
          and a["mode_conversion_framed_as_violation"] is False and a["session_may_continue_teaching"] is True,
          f"conversion={a}")
    check("E8D-13_recheck_owner",
          a["raises_requires_independent_recheck"] is True and a["session_schedules_recheck"] is False
          and a["recheck_owner"] == "planner_and_remediation_retention_pipelines",
          f"recheck_owner={a['recheck_owner']}")

    # --- 14. Pause / resume / recomposition --------------------------------
    pr = s["pause_resume_recomposition"]
    check("E8D-14_frame_owner", pr["frame_owner"] == "TRUX-v0", f"frame_owner={pr['frame_owner']}")
    check("E8D-14_pause_neutral",
          pr["pause_is_failure"] is False and pr["pause_is_assistance"] is False
          and pr["pause_is_mastery_signal"] is False, f"pause={pr}")
    expected_recomp = {
        "solution_or_explanation_exposed", "item_version_or_validation_changed",
        "prerequisite_state_changed_meaningfully", "freshness_no_longer_trustworthy_after_long_gap",
        "user_requested_reset_or_alternative",
    }
    check("E8D-15_recomposition_conditions", set(pr["recomposition_conditions"]) == expected_recomp,
          f"conditions={pr['recomposition_conditions']}")
    check("E8D-15_recomposition_count", len(pr["recomposition_conditions"]) == 5,
          f"count={len(pr['recomposition_conditions'])}")
    check("E8D-15_recomposition_safe",
          pr["recomposition_deletes_completed_valid_evidence"] is False
          and pr["recomposed_slot_presented_as_retry_of_failure"] is False
          and pr["recomposed_slot_presented_as_fresh_measurement"] is True,
          f"recomposition semantics={pr}")

    # --- 16. Incomplete session --------------------------------------------
    inc = s["incomplete_session"]
    check("E8D-16_incomplete",
          inc["submitted_valid_attempts_produce_evidence"] is True and inc["unsubmitted_counted_incorrect"] is False
          and inc["unresolved_slot_mastery_penalty"] is False and inc["session_may_be_partial"] is True
          and inc["unresolved_needs_remain_in_current_state"] is True and inc["creates_exam_debt"] is False
          and inc["missed_cycle_is_failure"] is False, f"incomplete={inc}")

    # --- 17. Item dispute --------------------------------------------------
    d = s["item_dispute"]
    check("E8D-17_dispute_semantics",
          d["reporting_low_friction"] is True and d["auto_sets_item_invalid"] is False
          and d["evidence_held_as_contested"] is True
          and d["critical_transition_decided_on_contested_item_alone"] is False
          and d["triggers_validator_answer_key_rubric_recheck"] is True
          and d["flags_item_or_template_for_qa"] is True, f"dispute={d}")
    check("E8D-17_dispute_non_punitive",
          d["treated_as_excuse_or_complaint"] is False and d["is_undo_button"] is False
          and d["harms_user_state"] is False, "dispute is non-punitive and not an undo button")

    # --- 18. Evaluator status ----------------------------------------------
    ev = s["evaluator_status"]
    check("E8D-18_values", set(ev["values"]) == {"verified", "provisional", "invalid"}, f"values={ev['values']}")
    check("E8D-18_provisional",
          ev["provisional_labelled_everywhere"] is True and ev["provisional_may_inform"] is True
          and ev["provisional_may_open_confirmation_need"] is True
          and ev["provisional_presented_as_verdict"] is False
          and ev["provisional_alone_settles_critical_transition"] is False, f"provisional={ev}")
    check("E8D-18_invalid",
          ev["invalid_gives_credit"] is False and ev["invalid_gives_penalty"] is False
          and ev["invalid_slot_counts_as_covered"] is False and ev["invalid_explained_plainly_to_user"] is True,
          f"invalid={ev}")

    # --- 19. Result --------------------------------------------------------
    r = s["result_presentation"]
    check("E8D-19_result_question", r["result_question"] == "what_just_changed",
          f"result_question={r['result_question']}")
    expected_families = ["confirmed_capabilities", "verification_needed", "persistent_targeted_gaps",
                         "retention_revalidated", "not_reliably_measured", "plan_changes"]
    check("E8D-19_families", r["semantic_families"] == expected_families, f"families={r['semantic_families']}")
    forbidden_verdicts = {"pass_fail_banner", "percentage_or_letter_grade", "passing_threshold",
                          "score_equals_mastered_rule", "broad_domain_score", "career_completion_percentage",
                          "comparison_to_other_people_or_target_curve"}
    check("E8D-19_forbidden_verdicts", forbidden_verdicts <= set(r["forbidden_verdicts"]),
          f"missing={sorted(forbidden_verdicts - set(r['forbidden_verdicts']))}")
    check("E8D-20_raw_counts",
          r["raw_counts_allowed"] is True and r["raw_counts_are_informational_only"] is True
          and r["raw_counts_separated_from_state"] is True, f"raw_counts={r}")
    expected_nrm = {"invalid_item", "provisional_evaluation", "assisted_attempt", "solution_exposed_attempt",
                    "contested_item", "unsubmitted_slot"}
    check("E8D-20_not_reliably_measured",
          r["not_reliably_measured_first_class"] is True
          and r["not_reliably_measured_always_shown_when_non_empty"] is True
          and set(r["not_reliably_measured_sources"]) == expected_nrm
          and r["not_reliably_measured_folded_into_incorrect"] is False,
          f"nrm={r['not_reliably_measured_sources']}")
    check("E8D-21_state_claim",
          r["state_change_claim_requires_canonical_state_change"] is True
          and r["first_contradiction_on_mastered_skill"] == "verification_due"
          and r["first_contradiction_causes_instant_unmastery"] is False
          and r["no_change_stated_plainly"] is True and r["manufactured_progress_claim"] is False,
          f"state claim={r['first_contradiction_on_mastered_skill']}")
    check("E8D-21_reason_source",
          r["reason_source"] == "PDT-v0" and r["reason_bounded"] is True
          and r["full_explanation_route"] == "planner_explanation", f"reason={r['reason_source']}")

    # --- 22. State/replan chain --------------------------------------------
    chain = s["state_and_replan_chain"]
    expected_chain = ["attempt", "evidence_validation", "gre_rvr_update",
                      "weakness_remediation_verification_update", "prg_readiness", "topic_derived_state",
                      "open_learning_needs", "pbr_priority", "remaining_capacity", "new_plan_version", "pdt_trace"]
    check("E8D-22_replan_chain", chain == expected_chain, f"chain={chain}")

    # --- 23. Surface boundary ----------------------------------------------
    sb = s["surface_boundary"]
    check("E8D-23_surface_split",
          sb["in_session_result_view"]["owner"] == "8D"
          and sb["assessment_report"]["owner"] == "progress_8E"
          and sb["either_owns_mastery"] is False and sb["contradictory_truths_allowed"] is False,
          f"surface_boundary={sb}")

    # --- 24. Technical English ---------------------------------------------
    te = s["technical_english_in_session"]
    check("E8D-24_english_safety",
          te["task_language_and_target_construct_separate"] is True
          and te["scaffold_mode_is_item_metadata"] is True
          and te["non_target_language_demand_scaffolded_or_neutralised"] is True
          and te["dual_target_component_results_separable"] is True
          and te["dual_target_overall_pass_broadcast"] is False
          and te["technical_ignorance_masquerades_as_english_failure"] is False
          and te["weak_english_produces_negative_technical_evidence"] is False
          and te["general_or_official_cefr_claim"] is False
          and te["qualified_profile_owner"] == "TEPM-v0", f"english={te}")

    # --- 25. Degraded ------------------------------------------------------
    deg = s["degraded_and_recovery"]
    check("E8D-25_frame_owner", deg["frame_owner"] == "TRUX-v0", f"frame_owner={deg['frame_owner']}")
    ai = deg["ai_evaluator_unavailable"]
    check("E8D-25_evaluation_pending",
          ai["attempt_preserved"] is True and ai["marked_evaluation_pending"] is True
          and ai["evidence_written"] is False and ai["auto_pass"] is False and ai["auto_fail"] is False,
          f"ai_unavailable={ai}")
    off = deg["offline"]
    check("E8D-25_offline",
          off["deterministically_evaluable_items_runnable"] is True
          and off["remote_evaluation_items_park_as_pending"] is True
          and off["remote_evaluation_items_fail"] is False, f"offline={off}")
    crash = deg["interruption_and_crash"]
    check("E8D-26_crash",
          crash["restores_last_durable_checkpoint_and_frozen_boundaries"] is True
          and crash["claims_unpersisted_work"] is False, f"crash={crash}")
    rec = deg["data_recovery"]
    check("E8D-26_recovery",
          rec["supersedes_normal_session_work"] is True and rec["silent_progress_reset"] is False,
          f"recovery={rec}")

    # --- 27. Semantic states -----------------------------------------------
    states = s["semantic_states"]
    expected_states = {
        "entering_revalidating", "blocked_not_startable", "session_orientation", "item_active", "assistance_open",
        "submitting_boundary", "boundary_frozen", "block_checkpoint", "session_paused", "resume_revalidating",
        "slot_recomposed", "evaluation_pending", "result_ready", "result_partial", "item_contested",
        "offline_local_capable", "ai_unavailable_deterministic_core", "error_recoverable", "data_recovery_required",
    }
    check("E8D-27_states_exact", set(states) == expected_states, f"states={states}")
    check("E8D-27_states_unique", len(states) == len(set(states)) == 19, f"count={len(states)}")
    check("E8D-27_states_textual",
          s["state_distinguishable_in_text"] is True
          and s["state_distinguishable_by_color_or_motion_only"] is False, "states distinguishable in text")

    # --- 28. Accessibility -------------------------------------------------
    acc = s["accessibility_baseline"]
    expected_acc_true = [
        "response_affordance_accessible_name", "submit_action_accessible_name", "exit_pause_accessible_name",
        "independence_and_tools_in_text", "assistance_level_and_consequence_in_text",
        "pending_contested_recomposed_recovery_announcable", "block_and_boundary_position_semantic",
        "result_families_readable_as_text",
    ]
    check("E8D-28_accessibility", all(acc[k] is True for k in expected_acc_true),
          f"violations={[k for k in expected_acc_true if acc.get(k) is not True]}")
    check("E8D-28_no_color_only_result", acc["result_meaning_by_color_alone"] is False,
          "result meaning is never color-only")

    # --- 29. Forbidden patterns --------------------------------------------
    forbidden = set(s["forbidden_session_patterns"])
    expected_forbidden = {
        "gradebook", "pass_fail_verdict_screen", "percentage_or_letter_grade_report",
        "broad_numeric_domain_mastery_dashboard", "countdown_timer_pressure_surface",
        "leaderboard_or_social_comparison", "exam_streak_or_quota_tracker", "visible_penalty_for_asking_help",
        "skipping_treated_as_wrong_answer", "provisional_presented_as_settled",
        "hiding_what_could_not_be_measured", "undo_button_for_inconvenient_results",
        "second_mastery_or_retention_engine", "duplicate_assessment_report",
        "diagnostic_runner_competing_with_task_runner_flow",
    }
    check("E8D-29_forbidden_patterns", expected_forbidden <= forbidden,
          f"missing={sorted(expected_forbidden - forbidden)}")
    check("E8D-29_forbidden_count", len(forbidden) == 15, f"count={len(forbidden)}")

    # --- 30. Upstream consistency ------------------------------------------
    ia_text = yaml.safe_dump(ia, allow_unicode=True)
    check("E8D-30_ia_surface_exists", "assessment_session_flow" in ia_text,
          "assessment_session_flow present in accepted 8A IA")
    check("E8D-30_ia_report_exists", "assessment_report" in ia_text,
          "assessment_report present in accepted 8A IA")
    frame = flow["shared_focused_flow_frame"]
    check("E8D-31_frame_inherited",
          "assessment_session_flow" in frame["inherited_by"]
          and frame["interior_owned_by_8d"] == ["assessment_session_flow"],
          f"8C frame handoff={frame}")
    check("E8D-31_8c_deferred_assessment",
          flow["scope"]["assessment_interior_locked"] is False
          and {str(k): v for k, v in flow["future_stage_boundaries"].items()}["8D"]
          == "assessment_session_interior_and_result_presentation",
          "8C deferred the assessment interior to 8D")
    check("E8D-31_diagnostics_stay_with_runner",
          flow["shared_focused_flow_frame"]["interior_owned_by_8c"] == ["task_runner_flow"],
          "task runner interior remains 8C's, including diagnostics")

    # --- 32. Future boundaries / acceptance --------------------------------
    boundaries = {str(k): v for k, v in s["future_stage_boundaries"].items()}
    for stage in ["8E", "8F", "8G", "9A", "9C", "9E", "10", "12", "13", "14", "15", "16", "17", "18"]:
        check("E8D-32_boundary_open", stage in boundaries, f"stage={stage}")
    check("E8D-32_8e_owns_report", boundaries["8E"] == "skill_progress_weakness_labels_and_assessment_report",
          f"8E={boundaries['8E']}")

    acceptance = s["acceptance"]
    required_models = {"UXIA-v0", "THUX-v0", "TRUX-v0", "DMA-v0", "WBA-v0", "MCA-v0", "QAB-v0", "AIV-v0",
                       "GRE-v0", "RVR-v0", "PRG-v0", "PBR-v0", "PDT-v0", "TEIP-v0", "TEPM-v0"}
    check("E8D-33_required_models", required_models <= set(acceptance["required_previous_models"]),
          f"missing={sorted(required_models - set(acceptance['required_previous_models']))}")
    check("E8D-33_regressions_required",
          acceptance["independent_qa_required"] is True and acceptance["stage6_regression_required"] is True
          and acceptance["stage7_regression_required"] is True and acceptance["stage8a_regression_required"] is True
          and acceptance["stage8b_regression_required"] is True and acceptance["stage8c_regression_required"] is True,
          f"acceptance={acceptance}")

    # --- 34. Spec / research textual contract -------------------------------
    spec_markers = [
        "ASUX-v0 — Assessment Session & Result UX",
        "`D-071`",
        "An assessment session is an evidence-collection workflow",
        "There is exactly one assessment session interior",
        "submitted_boundary_is_frozen = true",
        "unsubmitted_boundary != incorrect",
        "invalid item -> no mastery penalty, no mastery credit",
        "`not_reliably_measured` is first-class",
        "8E — Skill/progress/weakness UX",
    ]
    check("E8D-34_spec_contract", all(m in spec for m in spec_markers),
          f"missing={[m for m in spec_markers if m not in spec]}")
    check("E8D-34_spec_no_invented_constants",
          "No passing threshold, percentage grade, fixed question count, countdown clock or fixed geometry is canonical in 8D." in spec,
          "spec refuses invented thresholds/geometry")

    research_markers = [
        "A new separate external Research AI is **not required**",
        "Three scopes, one interior",
        "Showing results without grading",
        "Making \"not reliably measured\" first-class",
        "Assistance inside a measurement",
        "Diagnostics remain `task_runner_flow` work",
    ]
    check("E8D-35_research_synthesis", all(m in research for m in research_markers),
          f"missing={[m for m in research_markers if m not in research]}")

    return finish(args.write_report, s)


def finish(write_report: bool, s) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "8D",
        "model": "ASUX-v0",
        "candidate_decision": "D-071",
        "result": result,
        "status_observed": s.get("status") if isinstance(s, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "assessment_scopes": len(s.get("session_model", {}).get("assessment_scopes", [])) if isinstance(s, dict) else None,
        "result_families": len(s.get("result_presentation", {}).get("semantic_families", [])) if isinstance(s, dict) else None,
        "recomposition_conditions": len(s.get("pause_resume_recomposition", {}).get("recomposition_conditions", [])) if isinstance(s, dict) else None,
        "semantic_states": len(s.get("semantic_states", [])) if isinstance(s, dict) else None,
        "forbidden_session_patterns": len(s.get("forbidden_session_patterns", [])) if isinstance(s, dict) else None,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"8D_ASSESSMENT_SESSION_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"scopes={report['assessment_scopes']} result_families={report['result_families']} "
          f"recomposition={report['recomposition_conditions']} states={report['semantic_states']} "
          f"forbidden={report['forbidden_session_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
