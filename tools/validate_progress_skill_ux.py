from __future__ import annotations

import argparse
from pathlib import Path
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]
PROGRESS = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
SPEC = ROOT / "docs/PROGRESS_SKILL_UX_SPEC.md"
RESEARCH = ROOT / "research/8e_progress_skill_weakness_research.md"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
SESSION = ROOT / "ux/8d_assessment_session/session.yaml"
TEPM_POLICY = ROOT / "curriculum/english/7e_mastery_profile/policy.yaml"
TSM = ROOT / "docs/TOPIC_STATE_MACHINE.md"
WLRM = ROOT / "docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md"
RVR = ROOT / "docs/RETENTION_FORGETTING_SPEC.md"
REPORT = ROOT / "ux/8e_progress_skill_weakness/qa_report.yaml"

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

    for path in [PROGRESS, SPEC, RESEARCH, IA, SESSION, TEPM_POLICY, TSM, WLRM, RVR]:
        check("E8E-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None)

    p = load_yaml(PROGRESS)
    ia = load_yaml(IA)
    session = load_yaml(SESSION)
    tepm = load_yaml(TEPM_POLICY)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")
    tsm_text = TSM.read_text(encoding="utf-8")
    wlrm_text = WLRM.read_text(encoding="utf-8")
    rvr_text = RVR.read_text(encoding="utf-8")

    # --- 1. Identity -------------------------------------------------------
    check("E8E-01_identity", p.get("model") == "SPWX-v0" and p.get("stage_step") == "8E",
          f"model={p.get('model')} step={p.get('stage_step')}")
    check("E8E-01_status", p.get("status") in {"candidate_8e", "accepted_8e"}, f"status={p.get('status')}")
    check("E8E-01_decision", (p.get("candidate_decision") or p.get("decision")) == "D-072",
          f"decision={p.get('candidate_decision') or p.get('decision')}")
    check("E8E-01_parent_ia", p.get("parent_ia") == "UXIA-v0", f"parent={p.get('parent_ia')}")
    check("E8E-01_destination", p.get("owning_ia_destination") == "progress",
          f"destination={p.get('owning_ia_destination')}")
    expected_surfaces = {"progress_overview", "skill_detail", "topic_detail",
                         "technical_english_profile", "learning_history", "assessment_report"}
    check("E8E-01_owned_surfaces", set(p["owned_surfaces"]) == expected_surfaces,
          f"surfaces={p['owned_surfaces']}")

    # --- 2. Scope ----------------------------------------------------------
    scope = p["scope"]
    check("E8E-02_semantic_only", scope["semantic_presentation_only"] is True,
          "8E remains a semantic presentation contract")
    unchanged = ["mastery_algorithm_changed", "retention_algorithm_changed", "prerequisite_rule_changed",
                 "weakness_rule_changed", "assessment_policy_changed", "tepm_contract_changed",
                 "topic_internal_state_ids_changed"]
    check("E8E-02_upstream_untouched", all(scope[k] is False for k in unchanged),
          f"violations={[k for k in unchanged if scope[k] is not False]}")
    deferred_false = ["final_visual_design_locked", "implementation_technology_locked", "physical_data_schema_locked"]
    check("E8E-02_no_premature_lock", all(scope[k] is False for k in deferred_false),
          f"violations={[k for k in deferred_false if scope[k] is not False]}")
    null_scope = ["mastery_percentage", "competence_ratio", "career_completion_bar", "streak_calendar",
                  "fixed_pixel_geometry"]
    check("E8E-02_no_invented_metrics", all(scope[k] is None for k in null_scope),
          f"violations={[k for k in null_scope if scope[k] is not None]}")

    # --- 3. Truth ownership ------------------------------------------------
    owners = p["canonical_truth_owners"]
    expected_owners = {
        "mastery": "GRE-v0", "retention": "RVR-v0", "prerequisite": "PRG-v0", "topic_orchestration": "TSM-v0",
        "weakness_remediation": "WLRM-v0", "english_profile_semantics": "TEPM-v0",
        "assessment_evidence": "DMA_WBA_MCA_pipeline", "assessment_session_interior": "ASUX-v0",
        "planner_explanation": "PDT-v0", "planner_priority": "PBR-v0", "focused_flow_frame": "TRUX-v0",
    }
    check("E8E-03_truth_owners", owners == expected_owners, f"owners={owners}")
    check("E8E-03_progress_owns_no_engine_truth",
          not any(v in {"SPWX-v0", "progress", "progress_overview"} for v in owners.values()),
          "Progress must not own any canonical engine truth")

    # --- 4. Invariants -----------------------------------------------------
    inv = p["invariants"]
    expected_false = [
        "progress_is_mastery_engine", "progress_is_second_planner", "progress_is_gradebook",
        "progress_creates_planner_priority", "mastery_percentage_shown", "competence_ratio_shown",
        "confirmed_over_total_ratio_shown", "career_completion_percentage_shown", "broad_domain_score_shown",
        "mastery_gauge_level_or_rank_shown", "internal_mastery_heuristic_value_exposed", "streak_calendar_shown",
        "attendance_rendered_as_achievement", "contribution_graph_shown", "timeline_gap_marked_as_failure",
        "ninth_primary_presentation_state_added", "at_risk_is_primary_state", "at_risk_dropped",
        "primary_state_replaces_axes", "contradicting_axis_discarded", "topic_state_is_prerequisite_claim",
        "topic_percentage_shown", "topic_state_is_average_of_skills", "browse_placement_implies_prerequisite",
        "hypothesis_shown_as_deficiency", "ai_hypothesis_shown_as_confirmed", "weakness_broadcast_upward",
        "weakness_broadcast_downward", "remediation_task_completion_closes_remediation",
        "review_due_styled_as_decay", "review_due_demotes_confirmed_state", "verification_due_deletes_history",
        "verification_due_is_demotion_event", "at_risk_derived_from_elapsed_time_alone",
        "assessment_report_owns_mastery", "assessment_report_aggregates_sessions_into_score",
        "assessment_report_shows_competence_trend_line", "not_reliably_measured_folded_into_failure",
        "provisional_presented_as_settled", "general_or_official_cefr_claim", "numeric_english_aggregate",
        "aggregate_b2_plus_complete_claim", "english_presented_as_technical_gate",
        "duplicate_skill_truth_contradicting_learn", "empty_state_implies_all_mastered",
        "empty_state_implies_professional_ready", "stale_projection_presented_as_current",
        "ai_required_to_render_state", "offline_shows_missing_remote_data_as_zero", "silent_progress_reset",
    ]
    check("E8E-04_false_guards", all(inv[k] is False for k in expected_false),
          f"violations={[k for k in expected_false if inv.get(k) is not False]}")
    expected_true = ["exactly_eight_presentation_states", "presentation_projection_deterministic",
                     "axes_individually_inspectable_in_skill_detail", "counts_allowed_as_labelled_inventory",
                     "same_skill_truth_from_learn_and_progress"]
    check("E8E-04_true_guards", all(inv[k] is True for k in expected_true),
          f"violations={[k for k in expected_true if inv.get(k) is not True]}")
    check("E8E-04_guard_count", len(expected_false) >= 45, f"false_guards={len(expected_false)}")

    # --- 5. TEPM-v0 generalization (cross-checked against accepted 7E) -------
    sp = p["skill_presentation"]
    tepm_states = tepm["skill_presentation_states"]
    tepm_precedence = tepm["skill_presentation_precedence"]
    check("E8E-05_states_match_tepm", sp["states"] == tepm_states,
          f"8E={sp['states']} vs TEPM={tepm_states}")
    check("E8E-05_precedence_matches_tepm", sp["precedence"] == tepm_precedence,
          f"8E={sp['precedence']} vs TEPM={tepm_precedence}")
    check("E8E-05_exactly_eight", len(sp["states"]) == len(set(sp["states"])) == 8,
          f"count={len(sp['states'])}")
    check("E8E-05_precedence_covers_states", set(sp["precedence"]) == set(sp["states"]),
          "precedence covers exactly the declared states")
    check("E8E-05_generalized", sp["generalized_from"] == "TEPM-v0" and sp["applies_to_all_skills"] is True
          and sp["second_vocabulary_for_technical_skills"] is False,
          f"generalization={sp['generalized_from']}")
    check("E8E-06_labels_complete", set(sp["labels_tr"]) == set(sp["states"]),
          f"missing labels={sorted(set(sp['states']) - set(sp['labels_tr']))}")
    check("E8E-06_labels_unique", len(set(sp["labels_tr"].values())) == 8,
          f"labels={sorted(set(sp['labels_tr'].values()))}")
    check("E8E-06_internal_ids_fixed", sp["internal_ids_fixed"] is True, "internal state ids stay fixed")

    # --- 7. Multi-axis -----------------------------------------------------
    ma = p["multi_axis_presentation"]
    required_projection = {"primary_presentation_state", "mastery_axis_state", "retention_axis_state",
                           "prerequisite_axis_state", "weakness_axis_state", "attention_qualifiers"}
    check("E8E-07_projection_fields", required_projection <= set(ma["projection_fields"]),
          f"missing={sorted(required_projection - set(ma['projection_fields']))}")
    check("E8E-07_orders_not_replaces",
          ma["primary_state_orders_axes"] is True and ma["primary_state_replaces_axes"] is False
          and ma["primary_state_claimed_as_whole_truth"] is False, f"multi_axis={ma}")
    ar = ma["at_risk_handling"]
    check("E8E-08_at_risk_qualifier",
          ar["is_primary_state"] is False and ar["is_attention_qualifier"] is True
          and ar["silently_dropped"] is False and ar["explained_as_mixed_or_repeated_signal"] is True
          and ar["explained_as_elapsed_time"] is False, f"at_risk={ar}")
    check("E8E-08_at_risk_in_qualifiers", "retention_at_risk" in ma["attention_qualifiers"],
          f"qualifiers={ma['attention_qualifiers']}")
    check("E8E-08_at_risk_not_a_state", "at_risk" not in sp["states"] and "retention_at_risk" not in sp["states"],
          "at_risk is not a primary presentation state")
    # RVR canonical vocabulary must contain at_risk for this guard to be meaningful.
    check("E8E-08_rvr_defines_at_risk", "at_risk" in rvr_text, "RVR-v0 defines at_risk")
    check("E8E-08_rvr_at_risk_not_time_only", "sırf overdue süre yüzünden oluşmaz" in rvr_text,
          "RVR-v0 states at_risk is not caused by overdue duration alone")

    # --- 9. Topic ----------------------------------------------------------
    tp = p["topic_presentation"]
    expected_topic = ["locked", "available", "learning", "mastered", "weakening", "remediation_required"]
    check("E8E-09_topic_states", tp["internal_states"] == expected_topic, f"states={tp['internal_states']}")
    for st in expected_topic:
        check("E8E-09_topic_state_in_tsm", f"`{st}`" in tsm_text, f"state={st}")
    check("E8E-09_topic_labels_complete", set(tp["labels_tr"]) == set(expected_topic),
          f"missing={sorted(set(expected_topic) - set(tp['labels_tr']))}")
    check("E8E-09_topic_labels_unique", len(set(tp["labels_tr"].values())) == 6,
          f"labels={sorted(set(tp['labels_tr'].values()))}")
    check("E8E-09_topic_ids_unchanged", tp["internal_ids_changed"] is False and tp["source_contract"] == "TSM-v0",
          "topic internal ids unchanged")
    check("E8E-10_topic_semantics",
          tp["state_is_derived_orchestration"] is True and tp["state_is_prerequisite_claim"] is False
          and tp["state_is_average_of_skills"] is False and tp["percentage_shown"] is False,
          f"topic semantics={tp}")
    check("E8E-10_locked_reason",
          tp["locked_reason_reachable"] is True and tp["locked_is_permanent_verdict"] is False,
          "locked reason reachable and not permanent")
    check("E8E-10_tsm_delegates_wording", "8E" in tsm_text and "internal state kimlikleri sabit tutulmalıdır" in tsm_text,
          "TSM-v0 delegates UI wording to 8E while fixing internal ids")

    # --- 11. Progress overview ---------------------------------------------
    ov = p["progress_overview"]
    check("E8E-11_halves", ov["halves"] == ["demonstrated_capability_inventory", "attention_set"],
          f"halves={ov['halves']}")
    check("E8E-11_inventory_not_ratio",
          ov["inventory_counts_allowed"] is True and ov["inventory_counts_labelled_as_inventory"] is True
          and ov["inventory_ratio_or_percentage"] is False, f"inventory={ov}")
    expected_attention = {"remediation_open", "verification_due", "review_due", "retention_at_risk",
                          "prerequisite_blocked", "assessment_opportunity"}
    check("E8E-11_attention_reasons", set(ov["attention_reasons"]) == expected_attention,
          f"reasons={ov['attention_reasons']}")
    check("E8E-12_attention_not_backlog",
          ov["attention_is_backlog_or_debt"] is False and ov["attention_is_todo_count"] is False
          and ov["creates_planner_priority"] is False, f"attention semantics={ov}")
    forbidden_ov = {"career_completion_percentage", "broad_domain_score", "mastery_gauge_level_or_rank",
                    "streak_or_attendance_dashboard", "second_planner_start_now_surface", "assessment_gradebook"}
    check("E8E-12_forbidden_overview", forbidden_ov <= set(ov["forbidden_overview_shapes"]),
          f"missing={sorted(forbidden_ov - set(ov['forbidden_overview_shapes']))}")

    # --- 13. Skill detail --------------------------------------------------
    sd = p["skill_detail"]
    check("E8E-13_shared",
          sd["shared_surface"] is True and set(sd["reachable_from"]) == {"today", "learn", "progress"},
          f"reachable={sd['reachable_from']}")
    required_sd = {"skill_ref", "primary_presentation_state", "attention_qualifiers", "mastery_axis_state",
                   "retention_axis_state", "prerequisite_axis_state", "weakness_axis_state",
                   "objective_level_breakdown", "evidence_summary", "prerequisite_context",
                   "planner_explanation_entry"}
    check("E8E-13_fields", required_sd <= set(sd["semantic_fields"]),
          f"missing={sorted(required_sd - set(sd['semantic_fields']))}")
    check("E8E-13_no_hidden_gap", sd["objective_level_gap_hidden_by_skill_state"] is False,
          "Skill state never hides an Objective-level gap")
    check("E8E-14_evidence_kinds",
          set(sd["evidence_summary_distinguishes"]) == {"independent", "assisted", "provisional", "invalid"}
          and sd["evidence_kinds_merged_into_one_count"] is False,
          f"evidence kinds={sd['evidence_summary_distinguishes']}")
    check("E8E-14_shared_truth",
          sd["same_truth_from_learn_and_progress"] is True
          and sd["exposes_mastery_percentage_or_confidence_number"] is False,
          "shared truth, no numeric exposure")
    check("E8E-14_prereq_links", sd["prerequisite_context_links_blocking_skill"] is True,
          "prerequisite context links the blocking Skill")

    # --- 15. Topic detail --------------------------------------------------
    td = p["topic_detail"]
    check("E8E-15_topic_detail",
          td["shows_topic_state_and_skill_structure"] is True and td["creates_mastery_truth"] is False
          and td["creates_prerequisite_truth"] is False and td["creates_evidence_truth"] is False
          and td["skills_link_to_shared_skill_detail"] is True
          and td["structural_placement_rendered_as_dependency_chain"] is False
          and td["completion_percentage"] is False, f"topic_detail={td}")

    # --- 16. Weakness ------------------------------------------------------
    w = p["weakness_presentation"]
    expected_lifecycle = ["none", "hypothesis", "supported", "confirmed", "resolved"]
    check("E8E-16_lifecycle", w["lifecycle"] == expected_lifecycle, f"lifecycle={w['lifecycle']}")
    check("E8E-16_lifecycle_matches_wlrm",
          "none → hypothesis → supported → confirmed → resolved" in wlrm_text,
          "lifecycle matches WLRM-v0 canonical text")
    check("E8E-16_display_rules",
          w["display_rules"]["hypothesis"] == "open_question_at_most"
          and w["display_rules"]["supported"] == "localized_weakness_signal"
          and w["display_rules"]["confirmed"] == "localized_confirmed_weakness"
          and w["display_rules"]["resolved"] == "history_not_current_problem",
          f"display_rules={w['display_rules']}")
    check("E8E-17_hypothesis_guard",
          w["hypothesis_shown_as_deficiency"] is False and w["ai_hypothesis_shown_as_confirmed"] is False,
          "hypothesis is never a deficiency or a confirmed claim")
    check("E8E-17_localization",
          w["localization_preserved"] is True and w["broadcast_upward_to_domain"] is False
          and w["broadcast_downward_to_dependents"] is False, f"localization={w}")
    expected_closure = {"fresh", "context_diverse", "h0_independent", "direct", "verified", "prerequisite_valid"}
    check("E8E-18_closure_evidence",
          w["closure_requires_canonical_evidence"] is True
          and set(w["closure_evidence_properties"]) == expected_closure
          and w["task_completion_closes_remediation"] is False,
          f"closure={w['closure_evidence_properties']}")
    check("E8E-18_wlrm_closure_rule",
          "Remediation task'ının tamamlanması remediation'ı kapatmaz" in wlrm_text,
          "WLRM-v0 states task completion does not close remediation")
    check("E8E-18_non_punitive",
          w["framing_describes_located_gap_not_the_user"] is True and w["uses_failure_language"] is False
          and w["ranks_the_user"] is False, "weakness framing is non-punitive")

    # --- 19. English profile -----------------------------------------------
    ep = p["technical_english_profile"]
    check("E8E-19_bands", ep["base_bands"] == ["A1", "A2", "B1"], f"bands={ep['base_bands']}")
    check("E8E-19_uneven_first_class",
          ep["uneven_per_skill_detail_first_class"] is True and ep["flattened_single_level"] is False,
          "uneven per-Skill detail is first-class")
    check("E8E-19_same_states",
          ep["uses_same_eight_presentation_states"] is True and ep["second_state_model_for_english"] is False,
          "English uses the same eight presentation states")
    check("E8E-20_no_overclaim",
          ep["general_or_official_cefr_claim"] is False and ep["certification_claim"] is False
          and ep["numeric_aggregate_or_average"] is False and ep["aggregate_b2_plus_complete_claim"] is False
          and ep["presented_as_technical_gate"] is False, f"english overclaim guards={ep}")
    check("E8E-20_b2_bounded", ep["b2_plus_bounded_per_capability_extension"] is True,
          "B2+ stays a bounded per-capability extension")

    # --- 21. Learning history ----------------------------------------------
    lh = p["learning_history"]
    check("E8E-21_history_families",
          set(lh["event_families"]) == {"learning", "assessment", "review", "remediation"},
          f"families={lh['event_families']}")
    check("E8E-21_not_streak",
          lh["is_streak_calendar"] is False and lh["is_contribution_graph"] is False
          and lh["is_attendance_heatmap"] is False and lh["attendance_rendered_as_achievement"] is False
          and lh["consecutive_day_count_as_success_metric"] is False
          and lh["gap_marked_as_failure_or_missed_obligation"] is False, f"history={lh}")
    check("E8E-21_history_links",
          lh["records_meaningful_events"] is True and lh["records_what_changed"] is True
          and lh["entries_link_to_subject"] is True, "history records events and links subjects")

    # --- 22. Assessment report ---------------------------------------------
    ar_ = p["assessment_report"]
    session_families = session["result_presentation"]["semantic_families"]
    check("E8E-22_families_match_8d", ar_["semantic_families"] == session_families,
          f"8E={ar_['semantic_families']} vs 8D={session_families}")
    check("E8E-22_handoff",
          ar_["handed_over_by"] == "ASUX-v0" and ar_["in_session_view_owner"] == "8D"
          and ar_["scope"] == "longitudinal_across_sessions", f"handoff={ar_}")
    check("E8E-23_no_gradebook",
          ar_["owns_mastery"] is False and ar_["aggregates_sessions_into_score"] is False
          and ar_["aggregates_sessions_into_grade"] is False
          and ar_["shows_competence_trend_line"] is False, f"report guards={ar_}")
    check("E8E-23_nrm_and_provisional",
          ar_["not_reliably_measured_first_class"] is True
          and ar_["not_reliably_measured_folded_into_failure"] is False
          and ar_["provisional_labelled"] is True and ar_["contradicts_in_session_view"] is False,
          "not_reliably_measured stays first-class; provisional labelled")
    check("E8E-23_8d_deferred_report",
          {str(k): v for k, v in session["future_stage_boundaries"].items()}["8E"]
          == "skill_progress_weakness_labels_and_assessment_report",
          "8D deferred assessment_report to 8E")

    # --- 24. Counting ------------------------------------------------------
    cr = p["counting_rules"]
    check("E8E-24_counting",
          cr["counts_allowed"] is True and cr["counts_must_be_labelled_inventory"] is True
          and cr["count_divided_by_total_to_imply_competence"] is False
          and cr["progress_bar_toward_mastery_or_career"] is False, f"counting={cr}")
    forbidden_counts = {"percent_complete", "domain_percentage_score", "numeric_level",
                        "confirmed_over_total_ratio"}
    check("E8E-24_forbidden_counts", forbidden_counts <= set(cr["forbidden_examples"]),
          f"missing={sorted(forbidden_counts - set(cr['forbidden_examples']))}")

    # --- 25. Review / verification framing -----------------------------------
    rf = p["review_verification_framing"]
    check("E8E-25_review_neutral",
          rf["review_due_is_neutral_scheduled_opportunity"] is True and rf["review_due_means_forgotten"] is False
          and rf["review_due_demotes_confirmed_state"] is False
          and rf["review_due_styled_as_decay_or_error"] is False, f"review framing={rf}")
    check("E8E-25_rvr_review_rule", "Unutuldu demek değildir" in rvr_text,
          "RVR-v0 states review_due does not mean forgotten")
    check("E8E-26_verification_framing",
          rf["verification_due_states_current_uncertainty"] is True
          and rf["verification_due_deletes_confirmed_history"] is False
          and rf["verification_due_is_demotion_event"] is False, f"verification framing={rf}")
    check("E8E-26_no_excess_severity", rf["visual_severity_beyond_canonical_state"] is False,
          "no visual severity beyond canonical state")

    # --- 27. Degraded ------------------------------------------------------
    dg = p["degraded_and_recovery"]
    check("E8E-27_offline",
          dg["offline"]["locally_derived_state_viewable"] is True
          and dg["offline"]["remote_dependent_labelled_unavailable"] is True
          and dg["offline"]["remote_dependent_shown_as_zero_or_empty"] is False, f"offline={dg['offline']}")
    check("E8E-27_ai_unavailable",
          dg["ai_unavailable"]["progress_fully_usable"] is True
          and dg["ai_unavailable"]["ai_required_to_render_state"] is False
          and dg["ai_unavailable"]["unevaluated_attempts_shown_pending"] is True
          and dg["ai_unavailable"]["unevaluated_attempts_contribute_state"] is False,
          f"ai_unavailable={dg['ai_unavailable']}")
    check("E8E-28_recomputing",
          dg["recomputing"]["stale_projection_presented_as_current"] is False
          and dg["recomputing"]["recomputation_state_visible"] is True, f"recomputing={dg['recomputing']}")
    check("E8E-28_recovery",
          dg["data_recovery"]["supersedes_normal_presentation"] is True
          and dg["data_recovery"]["silent_progress_reset"] is False, f"recovery={dg['data_recovery']}")

    # --- 29. Semantic states -----------------------------------------------
    states = p["semantic_states"]
    expected_states = {
        "loading_projection", "recomputing_projection", "ready_with_evidence", "empty_no_evidence_yet",
        "empty_no_attention_needed", "partial_projection_available", "offline_local_capable",
        "ai_unavailable_full_state_available", "error_recoverable", "data_recovery_required",
    }
    check("E8E-29_states_exact", set(states) == expected_states, f"states={states}")
    check("E8E-29_states_unique", len(states) == len(set(states)) == 10, f"count={len(states)}")
    es = p["empty_state_semantics"]
    check("E8E-29_empty_semantics",
          es["empty_no_evidence_yet_is_failure"] is False
          and es["empty_no_attention_needed_implies_all_mastered"] is False
          and es["empty_no_attention_needed_implies_professional_ready"] is False, f"empty={es}")
    check("E8E-29_states_textual",
          p["state_distinguishable_in_text"] is True
          and p["state_distinguishable_by_color_or_motion_only"] is False, "states distinguishable in text")

    # --- 30. Accessibility -------------------------------------------------
    acc = p["accessibility_baseline"]
    expected_acc_true = ["every_presentation_state_has_accessible_name", "attention_qualifiers_in_text",
                         "inventory_counts_readable_as_text", "objective_breakdown_reachable_by_linear_traversal",
                         "history_readable_as_list", "review_and_verification_distinguishable_without_hue"]
    check("E8E-30_accessibility", all(acc[k] is True for k in expected_acc_true),
          f"violations={[k for k in expected_acc_true if acc.get(k) is not True]}")
    check("E8E-30_no_color_only", acc["state_conveyed_by_color_alone"] is False,
          "state never conveyed by color alone")

    # --- 31. Forbidden patterns --------------------------------------------
    forbidden = set(p["forbidden_progress_patterns"])
    expected_forbidden = {
        "mastery_percentage_or_competence_ratio", "career_completion_or_readiness_bar", "broad_domain_score",
        "level_rank_tier_or_badge_economy", "streak_calendar_or_contribution_graph",
        "assessment_gradebook_or_score_trend_line", "second_planner",
        "single_badge_replacing_contradicting_axes", "ai_hypothesis_shown_as_confirmed_weakness",
        "remediation_task_completion_read_as_fixed", "review_due_styled_as_decay_or_failure",
        "topic_label_implying_hard_prerequisite", "general_or_official_cefr_claim",
        "duplicate_skill_truth_contradicting_learn",
    }
    check("E8E-31_forbidden_patterns", expected_forbidden <= forbidden,
          f"missing={sorted(expected_forbidden - forbidden)}")
    check("E8E-31_forbidden_count", len(forbidden) == 14, f"count={len(forbidden)}")

    # --- 32. IA consistency ------------------------------------------------
    ia_text = yaml.safe_dump(ia, allow_unicode=True)
    for surface in ["progress", "skill_detail", "topic_detail", "technical_english_profile",
                    "learning_history", "assessment_report"]:
        check("E8E-32_ia_surface_exists", surface in ia_text, f"surface={surface}")

    # --- 33. Future boundaries / acceptance --------------------------------
    boundaries = {str(k): v for k, v in p["future_stage_boundaries"].items()}
    for stage in ["8F", "8G", "9A", "9C", "10", "12", "13", "14", "16", "17", "18"]:
        check("E8E-33_boundary_open", stage in boundaries, f"stage={stage}")
    check("E8E-33_8f_owns_visual", boundaries["8F"] == "visual_design_system", f"8F={boundaries['8F']}")

    acceptance = p["acceptance"]
    required_models = {"UXIA-v0", "THUX-v0", "TRUX-v0", "ASUX-v0", "GRE-v0", "RVR-v0", "PRG-v0", "TSM-v0",
                       "WLRM-v0", "TEPM-v0", "PDT-v0"}
    check("E8E-34_required_models", required_models <= set(acceptance["required_previous_models"]),
          f"missing={sorted(required_models - set(acceptance['required_previous_models']))}")
    check("E8E-34_regressions_required",
          all(acceptance[k] is True for k in
              ["independent_qa_required", "stage6_regression_required", "stage7_regression_required",
               "stage8a_regression_required", "stage8b_regression_required", "stage8c_regression_required",
               "stage8d_regression_required"]), f"acceptance={acceptance}")

    # --- 35. Spec / research textual contract -------------------------------
    spec_markers = [
        "SPWX-v0 — Progress, Skill State & Weakness UX",
        "`D-072`",
        "Progress is a projection of canonical evidence state",
        "The `TEPM-v0` presentation states and precedence are generalized to every Skill",
        "The primary presentation state orders these axes; it does not replace them",
        "remediation_task_completed != remediation_closed",
        "Progress may count. It may not score.",
        "8F — Tasarım sistemi",
    ]
    check("E8E-35_spec_contract", all(m in spec for m in spec_markers),
          f"missing={[m for m in spec_markers if m not in spec]}")
    check("E8E-35_spec_no_invented_metrics",
          "No mastery percentage, competence ratio, career bar, streak calendar or fixed geometry is canonical in 8E." in spec,
          "spec refuses invented metrics")
    # Every declared label must actually appear in the spec tables.
    missing_labels = [v for v in list(sp["labels_tr"].values()) + list(tp["labels_tr"].values()) if v not in spec]
    check("E8E-35_labels_documented", not missing_labels, f"missing_from_spec={missing_labels}")

    research_markers = [
        "A new separate external Research AI is **not required**",
        "Five axes, one label",
        "Avoiding a second vocabulary",
        "Where `at_risk` goes",
        "Not reinventing the percentage",
        "Weakness without accusation",
        "History that is not a streak calendar",
    ]
    check("E8E-36_research_synthesis", all(m in research for m in research_markers),
          f"missing={[m for m in research_markers if m not in research]}")

    # --- 37. Spec must not contain a percentage-style progress claim --------
    bad_numeric = re.findall(r"(?<!`)%\s?\d{1,3}(?!\S)", spec)
    allowed_context = spec.count("%64 tamamlandı") + spec.count("Python 72%")
    check("E8E-37_no_stray_percentage", len(bad_numeric) <= allowed_context,
          f"stray_percentages={bad_numeric}")

    return finish(args.write_report, p)


def finish(write_report: bool, p) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "8E",
        "model": "SPWX-v0",
        "candidate_decision": "D-072",
        "result": result,
        "status_observed": p.get("status") if isinstance(p, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "owned_surfaces": len(p.get("owned_surfaces", [])) if isinstance(p, dict) else None,
        "skill_presentation_states": len(p.get("skill_presentation", {}).get("states", [])) if isinstance(p, dict) else None,
        "topic_states": len(p.get("topic_presentation", {}).get("internal_states", [])) if isinstance(p, dict) else None,
        "semantic_states": len(p.get("semantic_states", [])) if isinstance(p, dict) else None,
        "forbidden_progress_patterns": len(p.get("forbidden_progress_patterns", [])) if isinstance(p, dict) else None,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"8E_PROGRESS_SKILL_UX_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"surfaces={report['owned_surfaces']} skill_states={report['skill_presentation_states']} "
          f"topic_states={report['topic_states']} semantic_states={report['semantic_states']} "
          f"forbidden={report['forbidden_progress_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
