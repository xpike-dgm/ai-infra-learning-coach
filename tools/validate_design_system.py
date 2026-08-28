from __future__ import annotations

import argparse
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
DS = ROOT / "ux/8f_design_system/design_system.yaml"
SPEC = ROOT / "docs/DESIGN_SYSTEM_SPEC.md"
RESEARCH = ROOT / "research/8f_design_system_research.md"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
HOME = ROOT / "ux/8b_today_home/home.yaml"
FLOW = ROOT / "ux/8c_daily_working_flow/flow.yaml"
SESSION = ROOT / "ux/8d_assessment_session/session.yaml"
PROGRESS = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
REPORT = ROOT / "ux/8f_design_system/qa_report.yaml"

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

    for path in [DS, SPEC, RESEARCH, IA, HOME, FLOW, SESSION, PROGRESS]:
        check("E8F-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None, 0)

    d = load_yaml(DS)
    ia = load_yaml(IA)
    home = load_yaml(HOME)
    flow = load_yaml(FLOW)
    session = load_yaml(SESSION)
    progress = load_yaml(PROGRESS)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")

    # --- 1. Identity -------------------------------------------------------
    check("E8F-01_identity", d.get("model") == "VDSX-v0" and d.get("stage_step") == "8F",
          f"model={d.get('model')} step={d.get('stage_step')}")
    check("E8F-01_status", d.get("status") in {"candidate_8f", "accepted_8f"}, f"status={d.get('status')}")
    check("E8F-01_decision", (d.get("candidate_decision") or d.get("decision")) == "D-073",
          f"decision={d.get('candidate_decision') or d.get('decision')}")
    check("E8F-01_parent_ia", d.get("parent_ia") == "UXIA-v0", f"parent={d.get('parent_ia')}")

    # --- 2. Scope ----------------------------------------------------------
    scope = d["scope"]
    check("E8F-02_expression_only", scope["expression_layer_only"] is True, "8F is an expression layer")
    unchanged = ["semantics_changed", "states_added_or_removed", "labels_changed", "truth_ownership_changed"]
    check("E8F-02_semantics_untouched", all(scope[k] is False for k in unchanged),
          f"violations={[k for k in unchanged if scope[k] is not False]}")
    deferred_false = ["wireframe_geometry_locked", "implementation_technology_locked", "physical_data_schema_locked",
                      "component_library_choice_locked", "concrete_hex_palette_locked"]
    check("E8F-02_no_premature_lock", all(scope[k] is False for k in deferred_false),
          f"violations={[k for k in deferred_false if scope[k] is not False]}")
    null_scope = ["fixed_pixel_geometry", "fixed_screen_layout", "passing_threshold"]
    check("E8F-02_no_geometry", all(scope[k] is None for k in null_scope),
          f"violations={[k for k in null_scope if scope[k] is not None]}")

    # --- 3. Truth ownership ------------------------------------------------
    owners = d["canonical_truth_owners"]
    expected_owners = {
        "destinations_and_surfaces": "UXIA-v0", "today_semantics": "THUX-v0",
        "task_runner_semantics": "TRUX-v0", "assessment_session_semantics": "ASUX-v0",
        "progress_state_semantics": "SPWX-v0", "mastery": "GRE-v0", "retention": "RVR-v0",
        "prerequisite": "PRG-v0", "planner_explanation": "PDT-v0",
    }
    check("E8F-03_truth_owners", owners == expected_owners, f"owners={owners}")
    check("E8F-03_ds_owns_nothing",
          not any(v in {"VDSX-v0", "design_system"} for v in owners.values()),
          "design system owns no canonical truth")

    # --- 4. Invariants -----------------------------------------------------
    inv = d["invariants"]
    expected_false = [
        "design_system_adds_meaning", "design_system_adds_severity",
        "design_system_adds_hierarchy_contradicting_spec", "component_invents_surface", "component_invents_state",
        "learning_state_uses_fault_tone", "attention_grouping_upgrades_tone", "visual_severity_exceeds_canonical",
        "color_is_sole_carrier", "icon_is_sole_carrier", "motion_is_sole_carrier_of_state_change",
        "motion_blocks_focused_flow_exit", "countdown_or_urgency_motion", "reward_animation_on_task_completion",
        "decay_or_loss_animation_for_learning_state", "streak_or_combo_animation", "competence_progress_bar",
        "gauge_dial_or_level_meter", "rank_tier_or_badge_visual", "streak_counter_visual",
        "calendar_heatmap_or_contribution_graph", "leaderboard_visual", "score_trend_line_visual",
        "locale_naive_case_transform", "all_caps_required_by_any_component",
        "locked_labels_restyled_to_other_case", "dark_theme_is_inversion_without_remeasurement",
        "concrete_hex_locked_without_measurement", "state_information_truncates_before_body_content",
        "tone_count_other_than_six", "celebration_without_canonical_state_change",
    ]
    check("E8F-04_false_guards", all(inv[k] is False for k in expected_false),
          f"violations={[k for k in expected_false if inv.get(k) is not False]}")
    expected_true = ["color_never_sole_carrier", "every_state_has_exactly_one_tone", "contrast_measured_per_theme",
                     "reduced_motion_loses_no_information", "state_exposed_as_text_to_assistive_tech"]
    check("E8F-04_true_guards", all(inv[k] is True for k in expected_true),
          f"violations={[k for k in expected_true if inv.get(k) is not True]}")
    check("E8F-04_guard_count", len(expected_false) >= 28, f"false_guards={len(expected_false)}")

    # --- 5. Tone vocabulary ------------------------------------------------
    tones = {t["id"]: t for t in d["tones"]}
    expected_tones = {"neutral", "active", "positive_confirmed", "attention", "pending_unresolved", "system_fault"}
    check("E8F-05_tone_set", set(tones) == expected_tones, f"tones={sorted(tones)}")
    check("E8F-05_exactly_six", len(d["tones"]) == 6, f"count={len(d['tones'])}")
    check("E8F-05_only_fault_alarming",
          [t for t in tones if tones[t].get("may_look_alarming") is True] == ["system_fault"],
          f"alarming={[t for t in tones if tones[t].get('may_look_alarming') is True]}")
    check("E8F-05_positive_not_celebratory", tones["positive_confirmed"].get("celebratory") is False,
          "positive_confirmed is calm, not celebratory")
    check("E8F-05_attention_not_failure",
          tones["attention"].get("implies_failure") is False
          and tones["pending_unresolved"].get("implies_failure") is False,
          "attention and pending do not imply failure")
    check("E8F-05_fault_not_for_learning", tones["system_fault"].get("allowed_for_learning_state") is False,
          "fault tone not allowed for learning state")

    # --- 6. Exhaustive surface-state tone coverage (cross-checked) ----------
    union: list[str] = []
    for src in (home, flow, session, progress):
        for s in src["semantic_states"]:
            if s not in union:
                union.append(s)
    for s in ia["cross_cutting_surface_states"]:
        if s not in union:
            union.append(s)
    mapped = d["surface_state_tones"]
    check("E8F-06_no_unmapped_state", set(union) <= set(mapped),
          f"unmapped={sorted(set(union) - set(mapped))}")
    check("E8F-06_no_invented_state", set(mapped) <= set(union),
          f"invented={sorted(set(mapped) - set(union))}")
    check("E8F-06_coverage_exact", set(mapped) == set(union), f"mapped={len(mapped)} union={len(union)}")
    check("E8F-06_union_size", len(union) == 46, f"union={len(union)}")
    check("E8F-07_all_tones_valid", all(v in expected_tones for v in mapped.values()),
          f"invalid={[k for k, v in mapped.items() if v not in expected_tones]}")

    # --- 8. Fault tone restriction -----------------------------------------
    fault_states = sorted(k for k, v in mapped.items() if v == "system_fault")
    check("E8F-08_fault_only_two", fault_states == ["data_recovery_required", "error_recoverable"],
          f"fault_states={fault_states}")
    check("E8F-08_fault_allowlist_matches", sorted(d["fault_tone_allowed_states"]) == fault_states,
          f"allowlist={sorted(d['fault_tone_allowed_states'])}")

    # --- 9. Non-negative tone requirements ---------------------------------
    non_negative = {"neutral", "active", "positive_confirmed"}
    sev = d["severity_rule"]
    skill_tones = d["skill_state_tones"]
    topic_tones = d["topic_state_tones"]
    all_tone_maps = {**mapped, **skill_tones}
    for state in sev["non_negative_tone_required_for"]:
        check("E8F-09_non_negative_tone", all_tone_maps.get(state) in non_negative,
              f"{state}={all_tone_maps.get(state)}")
    check("E8F-09_review_due_neutral", skill_tones["confirmed_review_due"] == "neutral",
          f"confirmed_review_due={skill_tones['confirmed_review_due']}")
    check("E8F-09_weakening_neutral", topic_tones["weakening"] == "neutral",
          f"weakening={topic_tones['weakening']}")
    check("E8F-09_stopped_neutral", mapped["stopped_no_penalty"] == "neutral",
          f"stopped_no_penalty={mapped['stopped_no_penalty']}")

    # --- 10. Severity rule -------------------------------------------------
    check("E8F-10_severity_rule",
          sev["tone_assigned_from_meaning_not_feel"] is True and sev["one_declared_tone_per_state"] is True
          and sev["surface_may_choose_different_tone"] is False
          and sev["learning_state_may_use_fault_tone"] is False
          and sev["attention_group_membership_changes_tone"] is False
          and sev["implies_loss_decay_failure_or_ranking_without_canonical_basis"] is False,
          f"severity_rule={sev}")

    # --- 11. Skill / Topic / qualifier coverage (cross-checked) -------------
    sp_states = progress["skill_presentation"]["states"]
    check("E8F-11_skill_coverage", set(skill_tones) == set(sp_states),
          f"missing={sorted(set(sp_states) - set(skill_tones))} extra={sorted(set(skill_tones) - set(sp_states))}")
    check("E8F-11_skill_tones_valid", all(v in expected_tones for v in skill_tones.values()),
          f"invalid={[k for k, v in skill_tones.items() if v not in expected_tones]}")
    check("E8F-11_no_skill_fault", not any(v == "system_fault" for v in skill_tones.values()),
          "no Skill state uses the fault tone")
    tp_states = progress["topic_presentation"]["internal_states"]
    check("E8F-12_topic_coverage", set(topic_tones) == set(tp_states),
          f"missing={sorted(set(tp_states) - set(topic_tones))} extra={sorted(set(topic_tones) - set(tp_states))}")
    check("E8F-12_topic_tones_valid", all(v in expected_tones for v in topic_tones.values()),
          f"invalid={[k for k, v in topic_tones.items() if v not in expected_tones]}")
    check("E8F-12_no_topic_fault", not any(v == "system_fault" for v in topic_tones.values()),
          "no Topic state uses the fault tone")
    qual = progress["multi_axis_presentation"]["attention_qualifiers"]
    q_tones = d["qualifier_tones"]
    check("E8F-13_qualifier_coverage", set(q_tones) == set(qual),
          f"missing={sorted(set(qual) - set(q_tones))} extra={sorted(set(q_tones) - set(qual))}")
    check("E8F-13_qualifier_tones_valid", all(v in expected_tones for v in q_tones.values()),
          f"invalid={[k for k, v in q_tones.items() if v not in expected_tones]}")
    check("E8F-13_no_qualifier_fault", not any(v == "system_fault" for v in q_tones.values()),
          "no qualifier uses the fault tone")

    # --- 14. External standards --------------------------------------------
    std_ids = {s["id"] for s in d["external_standards"]}
    expected_std = {"wcag_1_4_1_use_of_color", "wcag_1_4_3_contrast_minimum", "wcag_1_4_4_resize_text",
                    "wcag_1_4_11_non_text_contrast", "wcag_2_5_8_target_size_minimum",
                    "android_material_touch_target", "android_reduced_motion_setting"}
    check("E8F-14_standards", expected_std <= std_ids, f"missing={sorted(expected_std - std_ids)}")

    # --- 15. Typography ----------------------------------------------------
    ty = d["typography"]
    expected_roles = {"display", "title_large", "title", "body_large", "body", "label", "caption", "mono"}
    check("E8F-15_type_roles", set(ty["roles"]) == expected_roles, f"roles={ty['roles']}")
    check("E8F-15_mono_present", ty["mono_role_for_technical_content"] is True, "mono role for technical content")
    check("E8F-15_scale_is_default", ty["scale_is_product_default_not_scientific"] is True,
          "type scale declared as product default")
    check("E8F-15_resize",
          ty["scalable_units_required"] is True and ty["honours_system_font_size"] is True
          and ty["usable_at_200_percent"] is True
          and ty["state_label_truncates_before_body_content"] is False, f"typography={ty}")
    tr = ty["turkish_safe"]
    check("E8F-16_turkish_case",
          tr["locale_naive_case_transform_allowed"] is False and tr["dotted_dotless_i_preserved"] is True
          and tr["locked_labels_rendered_as_authored"] is True
          and tr["all_caps_required_by_component"] is False, f"turkish_safe={tr}")
    required_glyphs = {"ı", "İ", "ş", "Ş", "ğ", "Ğ", "ç", "Ç", "ö", "Ö", "ü", "Ü"}
    check("E8F-16_turkish_glyphs", required_glyphs <= set(tr["required_glyphs"]),
          f"missing={sorted(required_glyphs - set(tr['required_glyphs']))}")
    check("E8F-16_turkish_mono", tr["required_in_mono_role"] is True, "Turkish glyphs required in mono role")

    # --- 17. Colour --------------------------------------------------------
    col = d["color"]
    check("E8F-17_semantic_roles",
          col["specified_as_semantic_roles"] is True and col["raw_values_in_product_code"] is False,
          "colour specified as semantic roles")
    for tone in expected_tones:
        suffix = {"neutral": "neutral", "active": "active", "positive_confirmed": "positive",
                  "attention": "attention", "pending_unresolved": "pending", "system_fault": "fault"}[tone]
        check("E8F-17_tone_token_pair",
              f"tone_{suffix}" in col["token_roles"] and f"on_tone_{suffix}" in col["token_roles"],
              f"tone={tone}")
    check("E8F-17_one_pair_per_tone", col["each_tone_maps_to_one_token_pair"] is True, "one token pair per tone")
    ct = col["contrast"]
    check("E8F-18_contrast_thresholds",
          ct["body_and_label_text_min"] == 4.5 and ct["large_text_min"] == 3.0
          and ct["state_indicator_and_ui_component_min"] == 3.0, f"contrast={ct}")
    check("E8F-18_per_theme",
          ct["measured_per_theme"] is True and ct["dark_derived_by_inversion_without_measurement"] is False
          and ct["focus_indicator_visible_on_all_surfaces"] is True, f"contrast theming={ct}")
    ca = col["color_alone"]
    check("E8F-18_color_alone",
          ca["sole_carrier_allowed"] is False and ca["text_label_required"] is True
          and ca["non_color_differentiator_required"] is True
          and ca["review_and_verification_distinguishable_without_hue"] is True, f"color_alone={ca}")
    th = col["theming"]
    check("E8F-19_theming",
          th["light_first_class"] is True and th["dark_first_class"] is True
          and th["theme_preference_owner"] == "profile"
          and th["tone_identity_changes_with_theme"] is False, f"theming={th}")

    # --- 20. Spacing / targets ---------------------------------------------
    sptg = d["spacing_and_targets"]
    check("E8F-20_rhythm", sptg["base_rhythm_dp"] == 4 and sptg["step_scale_dp"] == [4, 8, 12, 16, 24, 32, 48],
          f"spacing={sptg['step_scale_dp']}")
    check("E8F-20_scale_is_default", sptg["scale_is_product_default_not_scientific"] is True,
          "spacing scale declared as product default")
    check("E8F-20_touch_target",
          sptg["minimum_touch_target_dp"] == 48 and sptg["wcag_target_floor_px"] == 24
          and sptg["adopts_stricter_platform_rule"] is True, f"targets={sptg}")
    check("E8F-20_exit_target", sptg["focused_flow_exit_keeps_full_target"] is True,
          "focused-flow exit keeps full target size")
    check("E8F-20_no_layout", sptg["fixed_screen_layout_defined"] is False, "no screen layout defined in 8F")

    # --- 21. Iconography ---------------------------------------------------
    ic = d["iconography"]
    check("E8F-21_icons",
          ic["supportive_only"] is True and ic["sole_carrier_allowed"] is False
          and ic["state_icon_appears_with_text_label"] is True and ic["one_metaphor_per_semantic_state"] is True
          and ic["meaningful_icon_contrast_min"] == 3.0
          and ic["icon_encodes_state_without_text_label_anywhere"] is False
          and ic["decorative_icons_marked_decorative"] is True, f"iconography={ic}")

    # --- 22. Motion --------------------------------------------------------
    mo = d["motion"]
    check("E8F-22_no_persuasion", mo["has_persuasive_role"] is False, "motion has no persuasive role")
    check("E8F-22_purpose_classes",
          set(mo["purpose_classes"]) == {"orientation", "continuity", "feedback", "state_change"},
          f"purpose_classes={mo['purpose_classes']}")
    check("E8F-22_durations_default",
          mo["duration_bands_are_product_defaults"] is True and mo["duration_bands_are_scientific"] is False
          and mo["delays_next_action_in_normal_use"] is False, f"durations={mo}")
    forbidden_motion = {"countdown_or_timer_animation", "urgency_pulse_or_pressure_motion",
                        "reward_animation_on_task_completion", "decay_or_falling_animation_for_learning_state",
                        "streak_or_combo_or_score_animation", "motion_as_sole_state_change_indicator",
                        "motion_blocking_focused_flow_exit"}
    check("E8F-23_forbidden_motion", forbidden_motion <= set(mo["forbidden"]),
          f"missing={sorted(forbidden_motion - set(mo['forbidden']))}")
    cel = mo["celebration"]
    check("E8F-23_celebration",
          cel["allowed_only_on_canonical_state_change"] is True and cel["proportionate_required"] is True
          and cel["overstates_evidence"] is False, f"celebration={cel}")
    rm = mo["reduced_motion"]
    check("E8F-23_reduced_motion",
          rm["platform_setting_honoured"] is True and rm["information_lost_when_reduced"] is False,
          f"reduced_motion={rm}")

    # --- 24. Components ----------------------------------------------------
    cv = d["component_vocabulary"]
    check("E8F-24_no_invention",
          cv["component_may_invent_surface"] is False and cv["component_may_invent_state"] is False,
          "components invent nothing")
    valid_owners = {"UXIA-v0", "THUX-v0", "TRUX-v0", "ASUX-v0", "SPWX-v0", "PDT-v0", "owning_surface_spec"}
    bad_owner = [c["id"] for c in cv["components"] if c["semantics_owner"] not in valid_owners]
    check("E8F-24_component_owners", not bad_owner, f"bad_owner={bad_owner}")
    check("E8F-24_component_count", len(cv["components"]) >= 18, f"count={len(cv['components'])}")
    ids = [c["id"] for c in cv["components"]]
    check("E8F-24_component_ids_unique", len(ids) == len(set(ids)), f"duplicates={len(ids) - len(set(ids))}")

    # --- 25. Restricted shapes ---------------------------------------------
    rs = d["restricted_shapes"]
    pb = rs["progress_bar"]
    check("E8F-25_progress_bar_allowed",
          set(pb["allowed_for"]) == {"position_within_assessment_session", "position_within_task_segments"},
          f"allowed_for={pb['allowed_for']}")
    forbidden_pb = {"mastery_or_capability", "competence_ratio", "career_or_curriculum_completion",
                    "confirmed_over_total_ratio", "topic_or_domain_percentage", "english_level"}
    check("E8F-25_progress_bar_forbidden", forbidden_pb <= set(pb["forbidden_for"]),
          f"missing={sorted(forbidden_pb - set(pb['forbidden_for']))}")
    forbidden_shapes = {"gauge", "dial", "level_meter", "rank_badge", "tier_emblem", "streak_counter",
                        "calendar_heatmap", "leaderboard", "score_trend_line"}
    check("E8F-26_forbidden_shapes", forbidden_shapes <= set(rs["forbidden_component_shapes"]),
          f"missing={sorted(forbidden_shapes - set(rs['forbidden_component_shapes']))}")

    # --- 27. Accessibility -------------------------------------------------
    ac = d["accessibility_rules"]
    expected_ac_true = ["stable_meaningful_accessible_name", "accessible_name_matches_semantic_label",
                        "focus_order_follows_owning_spec_reading_order", "focus_indicator_always_visible",
                        "state_exposed_as_text", "usable_at_200_percent_without_losing_state",
                        "reduced_motion_loses_no_information"]
    check("E8F-27_accessibility", all(ac[k] is True for k in expected_ac_true),
          f"violations={[k for k in expected_ac_true if ac.get(k) is not True]}")
    check("E8F-27_targets_and_focus",
          ac["minimum_target_dp"] == 48 and ac["focus_indicator_contrast_min"] == 3.0
          and ac["critical_meaning_by_color_icon_or_motion_alone"] is False, f"accessibility={ac}")

    # --- 28. Forbidden patterns / boundaries -------------------------------
    forbidden = set(d["forbidden_design_patterns"])
    expected_forbidden = {
        "red_or_alarm_styled_learning_state", "fault_tone_on_non_fault",
        "reward_animation_on_task_completion", "countdown_or_urgency_device",
        "competence_progress_bar_gauge_or_level_meter", "rank_tier_badge_or_streak_visual",
        "calendar_heatmap_or_contribution_graph", "score_trend_line",
        "color_icon_or_motion_as_sole_carrier", "locale_naive_case_transform",
        "component_inventing_surface_or_state", "visual_hierarchy_contradicting_owning_spec",
        "dark_theme_inverted_without_remeasurement", "motion_blocking_focused_flow_exit",
    }
    check("E8F-28_forbidden_patterns", expected_forbidden <= forbidden,
          f"missing={sorted(expected_forbidden - forbidden)}")
    check("E8F-28_forbidden_count", len(forbidden) == 14, f"count={len(forbidden)}")

    boundaries = {str(k): v for k, v in d["future_stage_boundaries"].items()}
    for stage in ["8G", "9A", "9C", "10", "12", "13", "14", "16", "17", "18"]:
        check("E8F-29_boundary_open", stage in boundaries, f"stage={stage}")
    check("E8F-29_8g_owns_geometry", boundaries["8G"] == "wireframe_and_prototype_geometry",
          f"8G={boundaries['8G']}")

    acceptance = d["acceptance"]
    required_models = {"UXIA-v0", "THUX-v0", "TRUX-v0", "ASUX-v0", "SPWX-v0", "GRE-v0", "RVR-v0", "PDT-v0"}
    check("E8F-30_required_models", required_models <= set(acceptance["required_previous_models"]),
          f"missing={sorted(required_models - set(acceptance['required_previous_models']))}")
    check("E8F-30_regressions_required",
          all(acceptance[k] is True for k in
              ["independent_qa_required", "stage6_regression_required", "stage7_regression_required",
               "stage8a_regression_required", "stage8b_regression_required", "stage8c_regression_required",
               "stage8d_regression_required", "stage8e_regression_required"]), f"acceptance={acceptance}")

    # --- 31. Spec / research textual contract -------------------------------
    spec_markers = [
        "VDSX-v0 — Visual Design System",
        "`D-073`",
        "The design system is an expression layer",
        "visual_severity <= canonical_severity",
        "**No learning state may use the `system_fault` tone.**",
        "Appearing in an attention group does **not** upgrade a state's tone",
        "No locale-naive case transforms.",
        "8G — Wireframe/prototip",
    ]
    check("E8F-31_spec_contract", all(m in spec for m in spec_markers),
          f"missing={[m for m in spec_markers if m not in spec]}")
    check("E8F-31_spec_defers_hex",
          "Concrete hex values are **not** locked here." in spec, "spec defers concrete hex with measurement rule")
    for t in expected_tones:
        check("E8F-31_tone_documented", f"`{t}`" in spec, f"tone={t}")

    research_markers = [
        "A new separate external Research AI is **not required**",
        "Severity is a visual free variable, and it must not be",
        "Celebration and completion",
        "Turkish typography and casing",
        "Dark theme is not an inversion",
        "Tokens without geometry",
    ]
    check("E8F-32_research_synthesis", all(m in research for m in research_markers),
          f"missing={[m for m in research_markers if m not in research]}")

    return finish(args.write_report, d, len(union))


def finish(write_report: bool, d, union_size: int) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "8F",
        "model": "VDSX-v0",
        "candidate_decision": "D-073",
        "result": result,
        "status_observed": d.get("status") if isinstance(d, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "tones": len(d.get("tones", [])) if isinstance(d, dict) else None,
        "surface_states_mapped": len(d.get("surface_state_tones", {})) if isinstance(d, dict) else None,
        "upstream_state_union": union_size,
        "components": len(d.get("component_vocabulary", {}).get("components", [])) if isinstance(d, dict) else None,
        "forbidden_design_patterns": len(d.get("forbidden_design_patterns", [])) if isinstance(d, dict) else None,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"8F_DESIGN_SYSTEM_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"tones={report['tones']} states_mapped={report['surface_states_mapped']}/{union_size} "
          f"components={report['components']} forbidden={report['forbidden_design_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
