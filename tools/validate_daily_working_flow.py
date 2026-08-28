from __future__ import annotations

import argparse
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
FLOW = ROOT / "ux/8c_daily_working_flow/flow.yaml"
SPEC = ROOT / "docs/DAILY_WORKING_FLOW_SPEC.md"
RESEARCH = ROOT / "research/8c_daily_working_flow_research.md"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
HOME = ROOT / "ux/8b_today_home/home.yaml"
REPORT = ROOT / "ux/8c_daily_working_flow/qa_report.yaml"

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

    for path in [FLOW, SPEC, RESEARCH, IA, HOME]:
        check("E8C-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None)

    flow = load_yaml(FLOW)
    ia = load_yaml(IA)
    home = load_yaml(HOME)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")

    # --- 1. Identity -------------------------------------------------------
    check("E8C-01_identity", flow.get("model") == "TRUX-v0" and flow.get("stage_step") == "8C",
          f"model={flow.get('model')} step={flow.get('stage_step')}")
    check("E8C-01_status", flow.get("status") in {"candidate_8c", "accepted_8c"}, f"status={flow.get('status')}")
    check("E8C-01_decision", (flow.get("candidate_decision") or flow.get("decision")) == "D-070",
          f"decision={flow.get('candidate_decision') or flow.get('decision')}")
    check("E8C-01_parent_ia", flow.get("parent_ia") == "UXIA-v0", f"parent={flow.get('parent_ia')}")
    check("E8C-01_home_contract", flow.get("home_contract") == "THUX-v0", f"home={flow.get('home_contract')}")
    check("E8C-01_owning_surface", flow.get("owning_ia_surface") == "task_runner_flow",
          f"surface={flow.get('owning_ia_surface')}")

    # --- 2. Scope boundary -------------------------------------------------
    scope = flow["scope"]
    check("E8C-02_semantic_only", scope["semantic_flow_only"] is True, "8C remains a semantic flow contract")
    deferred_false = [
        "final_visual_design_locked", "assessment_interior_locked", "skill_progress_visual_semantics_locked",
        "implementation_technology_locked", "physical_data_schema_locked", "code_runner_integration_locked",
        "ai_tutor_prompt_behavior_locked",
    ]
    check("E8C-02_no_premature_lock", all(scope[k] is False for k in deferred_false),
          f"violations={[k for k in deferred_false if scope[k] is not False]}")
    null_scope = ["fixed_step_count", "fixed_session_length_minutes", "fixed_daily_task_count", "fixed_pixel_geometry"]
    check("E8C-02_no_invented_constants", all(scope[k] is None for k in null_scope),
          f"violations={[k for k in null_scope if scope[k] is not None]}")

    # --- 3. Truth ownership ------------------------------------------------
    owners = flow["canonical_truth_owners"]
    expected_owners = {
        "current_plan": "planner", "task_selection": "PBR-v0", "mastery": "GRE-v0", "retention": "RVR-v0",
        "prerequisite": "PRG-v0", "planner_explanation": "PDT-v0", "reentry_recovery": "SRR-v0",
        "capacity": "D-033", "task_taxonomy": "D-034", "assistance_evidence": "AI_assistance_2D_contract",
        "weakness_remediation": "WLRM-v0", "assessment_evidence": "DMA_WBA_MCA_pipeline",
        "english_integration": "TEIP-v0", "assessment_interior": "8D",
    }
    check("E8C-03_truth_owners", owners == expected_owners, f"owners={owners}")
    check("E8C-03_runner_owns_no_engine_truth",
          not any(v in {"task_runner_flow", "runner", "TRUX-v0"} for v in owners.values()),
          "runner must not own any canonical engine truth")

    # --- 4. Invariant guards -----------------------------------------------
    inv = flow["invariants"]
    expected_false = [
        "runner_is_planner", "runner_is_mastery_authority", "runner_is_evidence_evaluator",
        "runner_is_prerequisite_authority", "runner_advances_cached_task_list", "session_is_graded_unit",
        "session_has_required_task_count", "session_has_required_duration", "plan_exhausted_means_day_succeeded",
        "user_stopped_means_day_failed", "task_completion_is_mastery", "lifecycle_state_is_evidence",
        "pause_is_failure", "stop_creates_debt", "stop_creates_streak_loss",
        "incomplete_attempt_is_negative_evidence", "invalidated_resume_is_negative_evidence",
        "resume_can_bypass_prerequisite", "resume_can_bypass_version_validity", "blocked_task_is_startable",
        "assistance_request_is_negative_evidence", "assistance_withheld_to_force_independence",
        "auto_reveal_solution_on_first_error", "same_item_after_exposure_is_mastery_path",
        "runner_schedules_recheck", "post_submit_explanation_contaminates_prior_attempt",
        "provenance_inferred_by_suspicion", "honest_provenance_disclosure_penalised",
        "evidence_written_without_attempt", "evaluation_pending_writes_evidence", "evaluation_pending_is_pass",
        "evaluation_pending_is_fail", "ai_unavailable_fakes_success", "offline_always_blocks_flow",
        "countdown_timer_pressure", "english_quota_or_streak_in_flow", "dual_target_overall_pass_broadcast",
        "english_scaffold_shown_as_penalty", "reason_can_exceed_trace_facts", "duplicate_assessment_interior",
        "in_flight_run_destroyed_by_replan",
    ]
    check("E8C-04_false_guards", all(inv[k] is False for k in expected_false),
          f"violations={[k for k in expected_false if inv.get(k) is not False]}")
    expected_true = [
        "assistance_policy_disclosed_before_independent_work", "consequence_disclosed_before_h3_h4",
        "exit_reachable_in_one_deliberate_action", "next_task_is_recomputed_planner_selection",
    ]
    check("E8C-04_true_guards", all(inv[k] is True for k in expected_true),
          f"violations={[k for k in expected_true if inv.get(k) is not True]}")
    check("E8C-04_guard_count", len(expected_false) >= 40, f"false_guards={len(expected_false)}")

    # --- 5. Session model --------------------------------------------------
    session = flow["session_model"]
    check("E8C-05_session_emergent", session["emergent_not_container"] is True, "session is emergent, not a container")
    check("E8C-05_session_ungraded",
          session["session_level_score"] is False and session["session_level_grade"] is False
          and session["session_completion_percentage"] is False and session["session_identity_is_evidence"] is False,
          f"session grading leak={session}")
    check("E8C-05_entry_sources",
          set(session["entry_sources"]) == {"today_primary_action", "today_queue_row", "entity_context", "resume_entry"},
          f"entry_sources={session['entry_sources']}")
    check("E8C-05_ended_reasons",
          set(session["ended_reasons"]) == {"user_stopped", "plan_exhausted", "capacity_reached", "interrupted", "recovery_required"},
          f"ended_reasons={session['ended_reasons']}")

    # --- 6. Shared focused-flow frame --------------------------------------
    frame = flow["shared_focused_flow_frame"]
    check("E8C-06_frame_owner", frame["owned_by"] == "8C", f"owner={frame['owned_by']}")
    check("E8C-06_frame_inherited_by_both",
          set(frame["inherited_by"]) == {"task_runner_flow", "assessment_session_flow"},
          f"inherited_by={frame['inherited_by']}")
    expected_frame = {
        "entry_revalidation", "safe_pause_and_exit_availability", "resume_revalidation",
        "capacity_and_replan_interaction", "degraded_and_recovery_behavior", "deterministic_semantic_return",
    }
    check("E8C-06_frame_elements", set(frame["frame_elements"]) == expected_frame, f"elements={frame['frame_elements']}")
    check("E8C-06_interior_split",
          frame["interior_owned_by_8c"] == ["task_runner_flow"] and frame["interior_owned_by_8d"] == ["assessment_session_flow"],
          "task runner interior 8C; assessment interior 8D")
    check("E8C-06_assess_delegates", frame["assess_purpose_delegates_interior_to_8d"] is True,
          "assess purpose delegates interior to 8D")

    # --- 7. IA / Home consistency ------------------------------------------
    ia_text = yaml.safe_dump(ia, allow_unicode=True)
    check("E8C-07_ia_surface_exists", "task_runner_flow" in ia_text, "task_runner_flow present in accepted 8A IA")
    check("E8C-07_ia_assessment_flow_exists", "assessment_session_flow" in ia_text,
          "assessment_session_flow present in accepted 8A IA")
    check("E8C-08_home_deferred_8c",
          home["scope"]["task_runner_choreography_locked"] is False
          and home["future_stage_boundaries"]["8C"] == "task_runner_and_daily_session_choreography",
          "8B deferred runner choreography to 8C")
    check("E8C-08_home_precedence_resume",
          "valid_resumable_focused_session" in home["primary_action_precedence"]
          and home["precedence_rules"]["resumable_session_must_revalidate"] is True,
          "8B resume revalidation contract inherited")

    # --- 9. Lifecycle ------------------------------------------------------
    phases = sorted(flow["task_run_lifecycle"]["phases"], key=lambda x: x["order"])
    phase_ids = [p["id"] for p in phases]
    check("E8C-09_lifecycle_exact", phase_ids == ["enter", "orient", "work", "submit", "resolve", "transition"],
          f"phases={phase_ids}")
    check("E8C-09_non_linear",
          set(flow["task_run_lifecycle"]["non_linear_transitions"]) == {"pause", "abandon", "recover"},
          f"non_linear={flow['task_run_lifecycle']['non_linear_transitions']}")

    # --- 10. Entry revalidation --------------------------------------------
    entry = flow["entry_revalidation"]
    expected_entry = {
        "planned_task_still_selected", "hard_prerequisites_satisfied", "learning_need_still_open",
        "content_version_compatible", "required_local_capability_available",
    }
    check("E8C-10_entry_conditions", set(entry["conditions"]) == expected_entry, f"conditions={entry['conditions']}")
    check("E8C-10_entry_failure_safe",
          entry["failure_starts_flow"] is False and entry["failure_returns_to_today_with_trace_reason"] is True
          and entry["failure_is_negative_evidence"] is False and entry["failure_is_user_error_message"] is False,
          f"entry failure semantics={entry}")

    # --- 11. Orientation ---------------------------------------------------
    orient = flow["orientation"]
    check("E8C-11_orientation_discloses_assistance", "assistance_policy_disclosure" in orient["required_semantics"],
          f"required={orient['required_semantics']}")
    check("E8C-11_orientation_explanation_entry", "planner_explanation_entry" in orient["required_semantics"],
          "planner explanation entry available from orientation")
    forbidden_orient = {"answer_preview", "mastery_prediction", "guaranteed_completion_time", "career_completion_percentage"}
    check("E8C-11_orientation_forbidden", forbidden_orient <= set(orient["forbidden_semantics"]),
          f"missing={sorted(forbidden_orient - set(orient['forbidden_semantics']))}")

    # --- 12. Work phase ----------------------------------------------------
    work = flow["work_phase"]
    check("E8C-12_work_segmented",
          work["activity_shaped_not_universal_question_screen"] is True and work["segmented"] is True
          and work["segment_boundaries_are_checkpoint_candidates"] is True, f"work={work}")
    forbidden_work = {"force_linear_completion_of_whole_day_plan_before_exit", "visible_countdown_pressure",
                      "slower_work_treated_as_mastery_signal"}
    check("E8C-12_work_forbidden", forbidden_work <= set(work["forbidden"]),
          f"missing={sorted(forbidden_work - set(work['forbidden']))}")

    # --- 13. Submit --------------------------------------------------------
    submit = flow["submit_phase"]
    check("E8C-13_submit_freeze", submit["submission_freezes_attempt"] is True, "submission freezes the attempt")
    check("E8C-13_no_retroactive_contamination", submit["post_submit_explanation_contaminates_prior_attempt"] is False,
          "post-submit explanation does not contaminate the prior attempt")
    check("E8C-13_same_item_not_independent", submit["same_item_after_exposure_is_independent_evidence"] is False,
          "same item after exposure is not independent evidence")
    check("E8C-13_no_attempt_no_evidence",
          submit["task_may_end_without_attempt"] is True and submit["no_attempt_means_no_evidence"] is True
          and submit["no_attempt_is_missing_result"] is False, f"submit={submit}")

    # --- 14. Resolve -------------------------------------------------------
    resolve = flow["resolve_phase"]
    forbidden_resolve = {"mastery_or_level_change_claim", "score_as_capability_verdict",
                         "skill_state_not_produced_by_canonical_state"}
    check("E8C-14_resolve_forbidden", forbidden_resolve <= set(resolve["forbidden_presentation"]),
          f"missing={sorted(forbidden_resolve - set(resolve['forbidden_presentation']))}")
    required_submission = {
        "planned_task_ref", "target_objective_refs", "artifact_refs", "artifact_origin", "assistance_events",
        "highest_assistance_level", "component_results", "integration_mode", "runner_completion_state",
        "requires_independent_recheck_flag",
    }
    check("E8C-14_submission_semantics", required_submission <= set(resolve["attempt_submission_semantics"]),
          f"missing={sorted(required_submission - set(resolve['attempt_submission_semantics']))}")
    check("E8C-14_completion_state_not_mastery", resolve["runner_completion_state_is_mastery_value"] is False,
          "runner completion state is workflow state only")

    # --- 15. Transition ----------------------------------------------------
    trans = flow["transition_phase"]
    check("E8C-15_next_from_planner", trans["next_task_source"] == "recomputed_planner_selection",
          f"next_task_source={trans['next_task_source']}")
    check("E8C-15_no_cached_advance", trans["cached_local_list_advance"] is False, "no cached local list advance")
    check("E8C-15_deterministic_return", trans["return_target_deterministic"] is True, "deterministic semantic return")
    check("E8C-15_no_stale_expectation",
          trans["stale_expectation_shown_when_replan_removed_task"] is False
          and trans["replan_removal_explained_from_trace"] is True, f"transition={trans}")

    # --- 16. Checkpoint / pause --------------------------------------------
    cp = flow["checkpoint_model"]
    expected_cp = {
        "segment_pedagogically_meaningful_alone", "artifact_and_runner_state_persistable_honestly",
        "no_partially_exposed_solution_state", "no_half_evaluated_independent_attempt",
    }
    check("E8C-16_safe_checkpoint", set(cp["safe_checkpoint_conditions"]) == expected_cp,
          f"conditions={cp['safe_checkpoint_conditions']}")
    pause_ids = [p["id"] for p in cp["pause_classes"]]
    check("E8C-16_pause_classes", set(pause_ids) == {"checkpoint_pause", "mid_segment_pause", "high_stakes_pause"},
          f"pause_classes={pause_ids}")
    by_id = {p["id"]: p for p in cp["pause_classes"]}
    check("E8C-16_checkpoint_durable",
          by_id["checkpoint_pause"]["durable"] is True and by_id["checkpoint_pause"]["produces_resume_context"] is True
          and by_id["checkpoint_pause"]["maps_to"] == "paused_progress", f"checkpoint_pause={by_id['checkpoint_pause']}")
    check("E8C-16_mid_segment_not_durable",
          by_id["mid_segment_pause"]["durable"] is False
          and by_id["mid_segment_pause"]["presented_as_saved_progress"] is False,
          f"mid_segment_pause={by_id['mid_segment_pause']}")
    check("E8C-16_high_stakes_marked",
          by_id["high_stakes_pause"]["marked"] is True
          and by_id["high_stakes_pause"]["silent_independent_continuation_after_long_gap"] is False,
          f"high_stakes_pause={by_id['high_stakes_pause']}")
    expected_resume_ctx = {"learning_need_key", "source_task_id", "checkpoint_id", "completed_segments",
                           "remaining_segments", "artifact_state_ref"}
    check("E8C-16_resume_context", set(cp["resume_context_fields"]) == expected_resume_ctx,
          f"resume_context={cp['resume_context_fields']}")
    check("E8C-16_no_auto_first_task",
          cp["paused_checkpoint_is_automatic_next_day_first_task"] is False
          and cp["paused_checkpoint_reenters_as_continue_learning_need"] is True,
          "paused checkpoint re-ranks as continue_learning need")

    # --- 17. Resume revalidation -------------------------------------------
    resume = flow["resume_revalidation"]
    expected_resume = {
        "content_version_compatible", "prerequisites_still_eligible", "learning_need_still_open",
        "runner_and_artifact_state_intact", "high_stakes_gap_integrity_acceptable",
    }
    check("E8C-17_resume_conditions", set(resume["conditions"]) == expected_resume, f"conditions={resume['conditions']}")
    check("E8C-17_resume_failure_safe",
          resume["failure_offers_fresh_alternative"] is True and resume["failure_is_negative_evidence"] is False
          and resume["failure_is_failure_message"] is False, f"resume={resume}")

    # --- 18. Abandon / stop ------------------------------------------------
    stop = flow["abandon_and_stop"]
    check("E8C-18_exit_available",
          stop["exit_always_available"] is True and stop["exit_reachable_in_one_deliberate_action"] is True,
          f"stop={stop}")
    check("E8C-18_stop_no_penalty",
          stop["planned_but_not_started_is_learning_gap"] is False and stop["started_but_user_stopped_is_history_only"] is True
          and stop["incomplete_attempt_is_wrong_answer"] is False and stop["creates_debt"] is False
          and stop["creates_streak_loss"] is False and stop["creates_catch_up_obligation"] is False,
          f"stop semantics={stop}")
    forbidden_exit = {"you_will_lose_your_progress", "your_streak_will_break",
                      "only_n_more_minutes_to_succeed_today", "guilt_confirmation_dialog"}
    check("E8C-18_forbidden_exit_framing", forbidden_exit <= set(stop["forbidden_exit_framing"]),
          f"missing={sorted(forbidden_exit - set(stop['forbidden_exit_framing']))}")

    # --- 19. Assistance ----------------------------------------------------
    asst = flow["assistance_choreography"]
    check("E8C-19_assistance_available",
          asst["always_requestable_in_teaching_practice"] is True
          and asst["withholding_to_force_independence"] is False, f"assistance={asst}")
    check("E8C-19_escalation_order", asst["escalation_order"] == ["H1", "H2", "H3", "H4"],
          f"escalation={asst['escalation_order']}")
    check("E8C-19_no_unrequested_reveal",
          asst["jump_to_h4_without_request"] is False and asst["auto_reveal_on_first_error"] is False
          and asst["mislabel_h3_scaffold_as_hint"] is False, "no unrequested or mislabelled reveal")
    check("E8C-19_consequence_disclosure",
          asst["consequence_disclosed_before_h3_h4"] is True
          and asst["consequence_framing_allowed"] == "changes_what_this_attempt_can_prove"
          and asst["consequence_framing_forbidden"] == "penalty_language",
          f"consequence={asst['consequence_framing_allowed']}/{asst['consequence_framing_forbidden']}")
    after = asst["after_exposure"]
    check("E8C-20_after_exposure",
          after["same_item_repeat_allowed_for_learning"] is True and after["same_item_repeat_labelled_practice"] is True
          and after["same_item_repeat_is_mastery_path"] is False and after["raises_requires_independent_recheck"] is True,
          f"after_exposure={after}")
    check("E8C-20_recheck_owner",
          after["runner_schedules_recheck"] is False
          and after["recheck_owner"] == "planner_and_remediation_retention_pipelines",
          f"recheck_owner={after['recheck_owner']}")
    expected_event = {"level", "timing", "target_scope", "source", "requested_by_user"}
    check("E8C-21_assistance_event", set(asst["assistance_event_fields"]) == expected_event,
          f"event_fields={asst['assistance_event_fields']}")
    check("E8C-21_assistance_levels", asst["assistance_levels"] == ["H1", "H2", "H3", "H4"],
          f"levels={asst['assistance_levels']}")
    check("E8C-21_assistance_timings",
          set(asst["assistance_timings"]) == {"before_attempt", "during_attempt", "after_submit", "after_failure"},
          f"timings={asst['assistance_timings']}")
    check("E8C-21_target_scope_split",
          set(asst["target_scopes"]) == {"target_objective", "non_target_support"},
          f"target_scopes={asst['target_scopes']}")
    conv = asst["assessment_mode_conversion"]
    check("E8C-22_assessment_conversion",
          conv["revealing_help_converts_item_to_learning"] is True and conv["conversion_flags_fresh_recheck"] is True
          and conv["conversion_is_punitive"] is False and conv["presentation_owner"] == "8D",
          f"conversion={conv}")

    # --- 23. Provenance ----------------------------------------------------
    prov = flow["artifact_provenance"]
    check("E8C-23_asked_not_inferred",
          prov["asked_not_inferred"] is True and prov["disclosure_low_friction"] is True
          and prov["unknown_recorded_not_guessed"] is True, f"provenance={prov}")
    check("E8C-23_non_punitive",
          prov["honest_disclosure_framed_as_confession"] is False and prov["honest_disclosure_penalised"] is False,
          "honest provenance disclosure is non-punitive")
    expected_origin = {"user_authored", "user_authored_with_assistance", "mixed_authorship",
                       "generated_or_copied", "unknown_provenance"}
    check("E8C-23_origin_values", set(prov["origin_values"]) == expected_origin, f"origins={prov['origin_values']}")
    forbidden_prov = {"accuse_user_of_cheating", "silent_state_downgrade_on_suspicion",
                      "honest_answer_is_slow_or_discouraged_path"}
    check("E8C-23_forbidden_policing", forbidden_prov <= set(prov["forbidden"]),
          f"missing={sorted(forbidden_prov - set(prov['forbidden']))}")
    check("E8C-23_copied_retains_learning_value", prov["generated_or_copied_retains_learning_value"] is True,
          "generated/copied artifact keeps learning value")

    # --- 24. Capacity / replan ---------------------------------------------
    cap = flow["capacity_and_replan"]
    check("E8C-24_in_flight_protected",
          cap["in_flight_run_destroyed_by_replan"] is False and cap["replan_preserves_completed_evidence"] is True
          and cap["replan_reresolves_unstarted_only"] is True, f"replan={cap}")
    check("E8C-24_invalid_in_flight_checkpoint", cap["invalid_in_flight_task_drives_to_safe_checkpoint"] is True,
          "invalid in-flight task drives to safe checkpoint")
    dec = cap["remaining_time_decreased"]
    check("E8C-25_time_decrease",
          dec["current_run_may_finish_or_checkpoint_pause"] is True and dec["unstarted_reresolved"] is True
          and dec["removed_unstarted_is_failure"] is False and dec["removed_unstarted_is_debt"] is False,
          f"decrease={dec}")
    inc = cap["remaining_time_increased"]
    check("E8C-25_time_increase",
          inc["fresh_mini_plan_from_current_state"] is True and inc["blind_replay_of_deferred_list"] is False,
          f"increase={inc}")
    check("E8C-25_replan_triggers",
          cap["early_finish_trigger"] == "TASK_FINISHED_EARLY" and cap["overrun_trigger"] == "TASK_OVERRAN_ESTIMATE",
          f"triggers={cap['early_finish_trigger']}/{cap['overrun_trigger']}")
    check("E8C-25_no_force_cut", cap["force_cut_evidence_integral_work"] is False,
          "evidence-integral work is not force-cut")
    check("E8C-26_reason_source",
          cap["in_flow_reason_source"] == "PDT-v0" and cap["in_flow_reason_bounded"] is True
          and cap["full_explanation_route"] == "planner_explanation" and cap["free_form_ai_reason_truth"] is False,
          f"reason={cap['in_flow_reason_source']}")

    # --- 27. Technical English ---------------------------------------------
    eng = flow["technical_english_in_flow"]
    expected_modes = {"technical_only_localized", "technical_with_english_exposure",
                      "dual_target_integrated", "english_primary_technical_context"}
    check("E8C-27_teip_modes", set(eng["supported_integration_modes"]) == expected_modes,
          f"modes={eng['supported_integration_modes']}")
    check("E8C-27_mode_count", len(eng["supported_integration_modes"]) == 4,
          f"count={len(eng['supported_integration_modes'])}")
    check("E8C-27_scaffold_metadata",
          eng["scaffold_mode_is_task_metadata"] is True and eng["runner_improvises_scaffold"] is False,
          "scaffold mode is task metadata, not runner improvisation")
    check("E8C-27_dual_target_separable",
          eng["dual_target_component_results_separable"] is True and eng["dual_target_overall_pass_broadcast"] is False,
          "dual-target results stay component-separable")
    check("E8C-27_english_no_quota",
          eng["english_quota_streak_debt_in_flow"] is False and eng["scaffold_shown_as_penalty_or_level"] is False
          and eng["non_target_gloss_is_support_not_target_assistance"] is True, f"english={eng}")

    # --- 28. Degraded / recovery -------------------------------------------
    deg = flow["degraded_and_recovery"]
    off = deg["offline"]
    check("E8C-28_offline",
          off["locally_runnable_work_remains_runnable"] is True and off["network_dependent_degrades_truthfully"] is True
          and off["fakes_completed_remote_step"] is False, f"offline={off}")
    ai = deg["ai_unavailable"]
    check("E8C-28_ai_degraded_core", ai["deterministic_core_runs"] is True, "deterministic core runs without AI")
    check("E8C-29_evaluation_pending",
          ai["open_ended_becomes_evaluation_pending"] is True and ai["evaluation_pending_writes_evidence"] is False
          and ai["auto_pass"] is False and ai["auto_fail"] is False and ai["evaluation_pending_visible"] is True,
          f"ai_unavailable={ai}")
    crash = deg["interruption_and_crash"]
    check("E8C-29_crash_truthful",
          crash["restores_last_durable_checkpoint_only"] is True and crash["claims_unpersisted_work"] is False
          and crash["silently_discards_persisted_work"] is False, f"crash={crash}")
    rec = deg["data_recovery"]
    check("E8C-29_recovery",
          rec["supersedes_normal_work_when_integrity_uncertain"] is True and rec["silent_progress_reset"] is False,
          f"recovery={rec}")

    # --- 30. Semantic states -----------------------------------------------
    states = flow["semantic_states"]
    expected_states = {
        "entering_revalidating", "blocked_not_startable", "orientation", "active_work", "assistance_open",
        "submitting", "feedback_resolved", "evaluation_pending", "checkpoint_paused", "resume_revalidating",
        "resume_invalidated", "replan_interrupted", "stopped_no_penalty", "offline_local_capable",
        "ai_unavailable_deterministic_core", "error_recoverable", "data_recovery_required",
    }
    check("E8C-30_states_exact", set(states) == expected_states, f"states={states}")
    check("E8C-30_states_unique", len(states) == len(set(states)) == 17, f"count={len(states)}")
    check("E8C-30_states_textual",
          flow["state_distinguishable_in_text"] is True
          and flow["state_distinguishable_by_color_or_motion_only"] is False,
          "states distinguishable in text")

    # --- 31. Accessibility -------------------------------------------------
    acc = flow["accessibility_baseline"]
    expected_acc = [
        "primary_work_action_accessible_name", "exit_pause_accessible_name",
        "exit_pause_reachable_when_shell_suppressed", "assistance_level_and_consequence_in_text",
        "pending_invalidated_recovery_announcable", "segment_progress_semantic_not_decorative_only",
        "duration_not_only_task_identifier",
    ]
    check("E8C-31_accessibility", all(acc[k] is True for k in expected_acc),
          f"violations={[k for k in expected_acc if acc.get(k) is not True]}")

    # --- 32. Forbidden patterns --------------------------------------------
    forbidden = set(flow["forbidden_flow_patterns"])
    expected_forbidden = {
        "guilt_driven_exit_gate", "streak_or_daily_goal_enforcement", "countdown_timer_pressure_surface",
        "second_planner_cached_list_advance", "mastery_or_level_up_announcer", "solution_auto_reveal_on_first_error",
        "same_item_retry_as_independent_proof", "cheating_detection_interrogation", "visible_penalty_for_asking_help",
        "evidence_written_without_attempt", "fake_success_while_evaluator_unavailable", "duplicate_assessment_interior",
        "pause_presented_as_losing_progress",
    }
    check("E8C-32_forbidden_patterns", expected_forbidden <= forbidden,
          f"missing={sorted(expected_forbidden - forbidden)}")
    check("E8C-32_forbidden_count", len(forbidden) == 13, f"count={len(forbidden)}")

    # --- 33. Future boundaries ---------------------------------------------
    # YAML parses bare numeric stage keys (10, 12, ...) as ints; normalise before checking.
    boundaries = {str(k): v for k, v in flow["future_stage_boundaries"].items()}
    for stage in ["8D", "8E", "8F", "8G", "9A", "9C", "9E", "10", "12", "13", "14", "16", "17", "18"]:
        check("E8C-33_boundary_open", stage in boundaries, f"stage={stage}")
    check("E8C-33_8d_owns_assessment", boundaries["8D"] == "assessment_session_interior_and_result_presentation",
          f"8D={boundaries['8D']}")

    # --- 34. Acceptance ----------------------------------------------------
    acceptance = flow["acceptance"]
    required_models = {"UXIA-v0", "THUX-v0", "GRE-v0", "RVR-v0", "PBR-v0", "PRG-v0", "PDT-v0", "SRR-v0",
                       "TEIP-v0", "WLRM-v0"}
    check("E8C-34_required_models", required_models <= set(acceptance["required_previous_models"]),
          f"missing={sorted(required_models - set(acceptance['required_previous_models']))}")
    check("E8C-34_regressions_required",
          acceptance["independent_qa_required"] is True and acceptance["stage6_regression_required"] is True
          and acceptance["stage7_regression_required"] is True and acceptance["stage8a_regression_required"] is True
          and acceptance["stage8b_regression_required"] is True, f"acceptance={acceptance}")

    # --- 35. Spec / research textual contract -------------------------------
    spec_markers = [
        "TRUX-v0 — Task Runner & Daily Working Flow UX",
        "`D-070`",
        "The Task Runner is an execution surface",
        "enter -> orient -> work -> submit -> resolve -> transition",
        "post_submit_explanation_contaminates_prior_attempt = false",
        "ai_evaluator_unavailable -> evaluation_pending",
        "The runner does not schedule the recheck.",
        "provenance is **asked, not inferred**",
        "8D — Sınav UX",
    ]
    check("E8C-35_spec_contract", all(m in spec for m in spec_markers),
          f"missing={[m for m in spec_markers if m not in spec]}")
    check("E8C-35_spec_no_fixed_geometry",
          "No fixed step count, fixed screen sequence, fixed session length, countdown timer or motion specification is canonical in 8C." in spec,
          "spec refuses invented geometry/constants")

    research_markers = [
        "A new separate external Research AI is **not required**",
        "LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent",
        "Continuity without a second planner",
        "Assistance without ambush",
        "Provenance without policing",
        "Degradation without fake evidence",
    ]
    check("E8C-36_research_synthesis", all(m in research for m in research_markers),
          f"missing={[m for m in research_markers if m not in research]}")

    return finish(args.write_report, flow)


def finish(write_report: bool, flow) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "8C",
        "model": "TRUX-v0",
        "candidate_decision": "D-070",
        "result": result,
        "status_observed": flow.get("status") if isinstance(flow, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "lifecycle_phases": len(flow.get("task_run_lifecycle", {}).get("phases", [])) if isinstance(flow, dict) else None,
        "semantic_states": len(flow.get("semantic_states", [])) if isinstance(flow, dict) else None,
        "pause_classes": len(flow.get("checkpoint_model", {}).get("pause_classes", [])) if isinstance(flow, dict) else None,
        "forbidden_flow_patterns": len(flow.get("forbidden_flow_patterns", [])) if isinstance(flow, dict) else None,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"8C_DAILY_WORKING_FLOW_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"phases={report['lifecycle_phases']} states={report['semantic_states']} "
          f"pause_classes={report['pause_classes']} forbidden={report['forbidden_flow_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
