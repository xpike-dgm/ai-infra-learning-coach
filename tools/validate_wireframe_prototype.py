from __future__ import annotations

import argparse
import re
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
WF = ROOT / "ux/8g_wireframe_prototype/wireframe.yaml"
SPEC = ROOT / "docs/WIREFRAME_PROTOTYPE_SPEC.md"
RESEARCH = ROOT / "research/8g_wireframe_prototype_research.md"
PROTOTYPE = ROOT / "ux/8g_wireframe_prototype/prototype.html"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
HOME = ROOT / "ux/8b_today_home/home.yaml"
FLOW = ROOT / "ux/8c_daily_working_flow/flow.yaml"
SESSION = ROOT / "ux/8d_assessment_session/session.yaml"
PROGRESS = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
DS = ROOT / "ux/8f_design_system/design_system.yaml"
REPORT = ROOT / "ux/8g_wireframe_prototype/qa_report.yaml"

checks: list[dict] = []
failures: list[str] = []
measured: list[dict] = []


def check(name: str, condition: bool, details: str) -> None:
    result = "PASS" if condition else "FAIL"
    checks.append({"check": name, "result": result, "details": details})
    if not condition:
        failures.append(f"{name}: {details}")


def load_yaml(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


# --- WCAG 2.2 relative luminance and contrast ratio ------------------------
HEX_RE = re.compile(r"^#[0-9A-Fa-f]{6}$")


def _channel(value: int) -> float:
    c = value / 255.0
    return c / 12.92 if c <= 0.03928 else ((c + 0.055) / 1.055) ** 2.4


def luminance(hex_color: str) -> float:
    h = hex_color.lstrip("#")
    r, g, b = int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)
    return 0.2126 * _channel(r) + 0.7152 * _channel(g) + 0.0722 * _channel(b)


def contrast(a: str, b: str) -> float:
    la, lb = luminance(a), luminance(b)
    hi, lo = max(la, lb), min(la, lb)
    return (hi + 0.05) / (lo + 0.05)


def hue_is_warm_amber(hex_color: str) -> bool:
    """True when the colour reads as amber/orange: red dominant, green mid, blue low."""
    h = hex_color.lstrip("#")
    r, g, b = int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)
    return r > b + 40 and g > b + 20 and r >= g


def hue_is_red(hex_color: str) -> bool:
    h = hex_color.lstrip("#")
    r, g, b = int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)
    return r > g + 40 and r > b + 40


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write-report", action="store_true")
    args = parser.parse_args()

    for path in [WF, SPEC, RESEARCH, PROTOTYPE, IA, HOME, FLOW, SESSION, PROGRESS, DS]:
        check("E8G-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None)

    w = load_yaml(WF)
    home = load_yaml(HOME)
    flow = load_yaml(FLOW)
    session = load_yaml(SESSION)
    progress = load_yaml(PROGRESS)
    ds = load_yaml(DS)
    ia = load_yaml(IA)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")
    proto = PROTOTYPE.read_text(encoding="utf-8")

    # --- 1. Identity -------------------------------------------------------
    check("E8G-01_identity", w.get("model") == "WFPX-v0" and w.get("stage_step") == "8G",
          f"model={w.get('model')} step={w.get('stage_step')}")
    check("E8G-01_status", w.get("status") in {"candidate_8g", "accepted_8g"}, f"status={w.get('status')}")
    check("E8G-01_decision", (w.get("candidate_decision") or w.get("decision")) == "D-074",
          f"decision={w.get('candidate_decision') or w.get('decision')}")
    check("E8G-01_parent", w.get("parent_ia") == "UXIA-v0" and w.get("design_system") == "VDSX-v0",
          f"parent={w.get('parent_ia')} ds={w.get('design_system')}")

    # --- 2. Scope ----------------------------------------------------------
    scope = w["scope"]
    check("E8G-02_geometry_only", scope["geometry_and_palette_only"] is True, "8G is geometry + palette")
    unchanged = ["semantics_changed", "states_added_or_removed", "labels_changed", "tones_changed",
                 "truth_ownership_changed"]
    check("E8G-02_semantics_untouched", all(scope[k] is False for k in unchanged),
          f"violations={[k for k in unchanged if scope[k] is not False]}")
    check("E8G-02_prototype_not_impl",
          scope["prototype_is_implementation"] is False and scope["prototype_is_technology_choice"] is False
          and scope["implementation_technology_locked"] is False, "prototype commits to nothing")
    check("E8G-02_no_fixed_cards", scope["fixed_card_count"] is None and scope["passing_threshold"] is None,
          "no invented constants")

    # --- 3. Invariants -----------------------------------------------------
    inv = w["invariants"]
    expected_false = [
        "geometry_changes_meaning", "region_order_differs_from_owning_spec",
        "destination_identity_changes_by_window_class", "destination_order_changes_by_window_class",
        "region_added_or_removed_by_window_class", "second_copy_of_shared_surface_with_own_truth",
        "countdown_in_focused_flow_chrome", "urgency_element_in_focused_flow_chrome",
        "exit_below_min_target", "exit_hidden_behind_menu", "ratio_or_percentage_in_progress_overview",
        "bar_or_gauge_in_progress_overview", "primary_chip_replaces_axis_block",
        "state_truncated_at_large_text", "traffic_light_severity_ramp", "attention_is_amber_or_orange",
        "non_fault_tone_is_red", "dark_derived_by_inverting_light", "contrast_asserted_without_computation",
        "prototype_presented_as_implementation", "fixed_card_count_imposed",
    ]
    check("E8G-03_false_guards", all(inv[k] is False for k in expected_false),
          f"violations={[k for k in expected_false if inv.get(k) is not False]}")
    expected_true = ["semantic_order_invariant_across_classes", "palette_measured_per_theme",
                     "layouts_reflow_at_200_percent"]
    check("E8G-03_true_guards", all(inv[k] is True for k in expected_true),
          f"violations={[k for k in expected_true if inv.get(k) is not True]}")

    # --- 4. Window classes -------------------------------------------------
    wc = {c["id"]: c for c in w["window_classes"]}
    check("E8G-04_window_classes", set(wc) == {"compact", "medium", "expanded"}, f"classes={sorted(wc)}")
    check("E8G-04_breakpoints_ordered",
          wc["compact"]["max_width_dp"] < wc["medium"]["min_width_dp"]
          <= wc["medium"]["max_width_dp"] < wc["expanded"]["min_width_dp"],
          f"breakpoints={[wc['compact']['max_width_dp'], wc['medium']['min_width_dp'], wc['medium']['max_width_dp'], wc['expanded']['min_width_dp']]}")
    ia_dest = [d["id"] for d in sorted(ia["primary_destinations"], key=lambda x: x["order"])]
    check("E8G-05_destination_order_matches_ia", w["destination_order"] == ia_dest,
          f"8G={w['destination_order']} vs IA={ia_dest}")
    check("E8G-05_order_invariant",
          w["destination_order_invariant"] is True and w["detail_pane_shows_same_surface_same_truth"] is True,
          "destination order invariant; detail pane shares truth")

    # --- 6. Region order cross-checked against owning specs ----------------
    sg = w["surface_geometry"]
    home_regions = [r["id"] for r in sorted(home["content_hierarchy"], key=lambda x: x["order"])]
    check("E8G-06_today_order_matches_thux", sg["today_overview"]["region_order"] == home_regions,
          f"8G={sg['today_overview']['region_order']} vs THUX={home_regions}")
    check("E8G-06_today_owner", sg["today_overview"]["owning_spec"] == "THUX-v0", "Today owned by THUX-v0")
    check("E8G-06_today_dominant_and_uncounted",
          sg["today_overview"]["primary_action_dominant_in_all_classes"] is True
          and sg["today_overview"]["fixed_card_count"] is None, "primary dominant, no fixed card count")

    progress_halves = progress["progress_overview"]["halves"]
    check("E8G-07_progress_halves_match_spwx",
          sg["progress_overview"]["region_order"] == progress_halves,
          f"8G={sg['progress_overview']['region_order']} vs SPWX={progress_halves}")
    check("E8G-07_progress_no_ratio",
          sg["progress_overview"]["counts_rendered_as_inventory"] is True
          and sg["progress_overview"]["ratio_bar_gauge_or_percentage_present"] is False,
          "inventory only, no ratio/bar/gauge")

    sd = sg["skill_detail"]
    spwx_axes = {"mastery_axis_state", "retention_axis_state", "prerequisite_axis_state", "weakness_axis_state"}
    check("E8G-08_axes_match_spwx", set(sd["axes_shown"]) == spwx_axes, f"axes={sd['axes_shown']}")
    check("E8G-08_axis_block_kept",
          sd["axis_block_always_present"] is True and sd["primary_chip_replaces_axis_block"] is False,
          "chip never replaces the axis block")
    spwx_ev = set(progress["skill_detail"]["evidence_summary_distinguishes"])
    check("E8G-08_evidence_kinds_match_spwx", set(sd["evidence_kinds_shown_separately"]) == spwx_ev,
          f"8G={sd['evidence_kinds_shown_separately']} vs SPWX={sorted(spwx_ev)}")
    check("E8G-09_topic_no_percentage", sg["topic_detail"]["completion_percentage_present"] is False,
          "no Topic completion percentage")

    tr = sg["task_runner_flow"]
    check("E8G-10_runner_exit",
          tr["shell_may_be_suppressed"] is True and tr["exit_and_pause_always_present"] is True
          and tr["exit_min_target_dp"] == 48 and tr["exit_position_fixed_in_all_classes"] is True
          and tr["countdown_present"] is False, f"runner chrome={tr}")
    asf = sg["assessment_session_flow"]
    check("E8G-10_assessment_chrome",
          asf["position_context_is_orientation_only"] is True and asf["countdown_present"] is False
          and asf["exit_min_target_dp"] == 48, f"assessment chrome={asf}")
    ds_target = ds["spacing_and_targets"]["minimum_touch_target_dp"]
    check("E8G-10_target_matches_vdsx",
          tr["exit_min_target_dp"] == ds_target and asf["exit_min_target_dp"] == ds_target,
          f"8G={tr['exit_min_target_dp']} vs VDSX={ds_target}")

    # --- 11. Tone token map cross-checked against VDSX ---------------------
    ds_tones = {t["id"] for t in ds["tones"]}
    tmap = w["tone_token_map"]
    check("E8G-11_tone_map_covers_vdsx", set(tmap) == ds_tones,
          f"missing={sorted(ds_tones - set(tmap))} extra={sorted(set(tmap) - ds_tones)}")
    ds_roles = set(ds["color"]["token_roles"])
    flat_tokens = [tok for pair in tmap.values() for tok in pair]
    check("E8G-11_tokens_declared_in_vdsx", set(flat_tokens) <= ds_roles,
          f"undeclared={sorted(set(flat_tokens) - ds_roles)}")

    # --- 12. PALETTE: contrast is COMPUTED, not asserted -------------------
    pal = w["palette"]
    check("E8G-12_measured_per_theme",
          pal["measured_per_theme"] is True and pal["dark_is_inversion_of_light"] is False
          and pal["validator_recomputes_from_hex"] is True
          and pal["ratios_are_evidence_not_source_of_truth"] is True, f"palette policy={pal['measured_per_theme']}")
    req = pal["contrast_requirements"]
    check("E8G-12_thresholds_match_vdsx",
          req["text_on_surface_min"] == ds["color"]["contrast"]["body_and_label_text_min"]
          and req["non_text_and_boundary_min"] == ds["color"]["contrast"]["state_indicator_and_ui_component_min"],
          f"8G={req} vs VDSX={ds['color']['contrast']}")

    themes = pal["themes"]
    check("E8G-13_two_themes", set(themes) == {"light", "dark"}, f"themes={sorted(themes)}")

    all_min_text = []
    all_min_nontext = []
    all_min_tone = []
    for theme_name, t in themes.items():
        for token, value in t.items():
            check("E8G-13_valid_hex", bool(HEX_RE.match(str(value))), f"{theme_name}.{token}={value}")
        surface, variant = t["surface"], t["surface_variant"]
        # text on surfaces
        for token in ["on_surface", "on_surface_muted"]:
            for bg_name, bg in [("surface", surface), ("surface_variant", variant)]:
                r = contrast(t[token], bg)
                measured.append({"theme": theme_name, "pair": f"{token}/{bg_name}", "ratio": round(r, 2),
                                 "required": req["text_on_surface_min"]})
                all_min_text.append(r)
                check("E8G-14_text_contrast", r >= req["text_on_surface_min"],
                      f"{theme_name} {token}/{bg_name}={r:.2f} < {req['text_on_surface_min']}")
        # non-text: outline and focus ring
        for token in ["outline", "focus_ring"]:
            for bg_name, bg in [("surface", surface), ("surface_variant", variant)]:
                r = contrast(t[token], bg)
                measured.append({"theme": theme_name, "pair": f"{token}/{bg_name}", "ratio": round(r, 2),
                                 "required": req["non_text_and_boundary_min"]})
                all_min_nontext.append(r)
                check("E8G-15_non_text_contrast", r >= req["non_text_and_boundary_min"],
                      f"{theme_name} {token}/{bg_name}={r:.2f} < {req['non_text_and_boundary_min']}")
        # tone pairs
        for tone, (bg_tok, fg_tok) in tmap.items():
            bg, fg = t[bg_tok], t[fg_tok]
            r_text = contrast(fg, bg)
            measured.append({"theme": theme_name, "pair": f"{fg_tok}/{bg_tok}", "ratio": round(r_text, 2),
                             "required": req["text_on_tone_min"]})
            all_min_tone.append(r_text)
            check("E8G-16_tone_text_contrast", r_text >= req["text_on_tone_min"],
                  f"{theme_name} {fg_tok}/{bg_tok}={r_text:.2f} < {req['text_on_tone_min']}")
            for bg_name, page_bg in [("surface", surface), ("surface_variant", variant)]:
                r_ind = contrast(bg, page_bg)
                measured.append({"theme": theme_name, "pair": f"{bg_tok}/{bg_name}", "ratio": round(r_ind, 2),
                                 "required": req["non_text_and_boundary_min"]})
                all_min_nontext.append(r_ind)
                check("E8G-17_tone_indicator_contrast", r_ind >= req["non_text_and_boundary_min"],
                      f"{theme_name} {bg_tok}/{bg_name}={r_ind:.2f} < {req['non_text_and_boundary_min']}")

    # Declared minima must match what we just computed.
    if all_min_text and all_min_nontext and all_min_tone:
        dm = pal["measured_minima"]
        check("E8G-18_declared_min_text", abs(min(all_min_text) - dm["text_on_surface"]) < 0.01,
              f"computed={min(all_min_text):.2f} declared={dm['text_on_surface']}")
        check("E8G-18_declared_min_nontext", abs(min(all_min_nontext) - dm["non_text_and_boundary"]) < 0.01,
              f"computed={min(all_min_nontext):.2f} declared={dm['non_text_and_boundary']}")
        check("E8G-18_declared_min_tone", abs(min(all_min_tone) - dm["text_on_tone"]) < 0.01,
              f"computed={min(all_min_tone):.2f} declared={dm['text_on_tone']}")

    # --- 19. Hue policy: no traffic-light ramp -----------------------------
    hp = pal["hue_policy"]
    check("E8G-19_hue_policy_declared",
          hp["attention_is_amber_or_orange"] is False and hp["fault_is_only_red"] is True
          and hp["traffic_light_ramp"] is False, f"hue_policy={hp}")
    for theme_name, t in themes.items():
        att = t[tmap["attention"][0]]
        check("E8G-20_attention_not_amber", not hue_is_warm_amber(att),
              f"{theme_name} tone_attention={att} reads as amber/orange")
        for tone, (bg_tok, _) in tmap.items():
            if tone == "system_fault":
                continue
            check("E8G-20_only_fault_is_red", not hue_is_red(t[bg_tok]),
                  f"{theme_name} {bg_tok}={t[bg_tok]} reads as red but tone is {tone}")
        check("E8G-20_fault_is_red", hue_is_red(t[tmap["system_fault"][0]]),
              f"{theme_name} tone_fault={t[tmap['system_fault'][0]]}")

    # Light and dark must not be trivial inversions of each other.
    inverted = 0
    for token in themes["light"]:
        lh = themes["light"][token].lstrip("#")
        dh = themes["dark"].get(token, "").lstrip("#")
        if dh and all(abs((255 - int(lh[i:i+2], 16)) - int(dh[i:i+2], 16)) <= 8 for i in (0, 2, 4)):
            inverted += 1
    check("E8G-21_not_an_inversion", inverted == 0, f"inverted_tokens={inverted}")

    # --- 22. Text scaling --------------------------------------------------
    ts = w["text_scaling"]
    check("E8G-22_text_scaling",
          ts["max_supported_percent"] == 200 and ts["layouts_reflow"] is True
          and ts["state_truncated"] is False and ts["state_information_elided"] is False
          and ts["exit_and_pause_shrink_below_min_target"] is False, f"text_scaling={ts}")
    check("E8G-22_elision_order", ts["elision_order"] == ["secondary_metadata", "duration"],
          f"elision_order={ts['elision_order']}")

    # --- 23. Prototype -----------------------------------------------------
    pr = w["prototype"]
    check("E8G-23_prototype_declared",
          pr["file"] == "ux/8g_wireframe_prototype/prototype.html" and pr["self_contained"] is True
          and pr["binding"] is False and pr["is_implementation"] is False
          and pr["is_technology_choice"] is False and pr["commits_to_framework"] is False
          and pr["spec_wins_on_disagreement"] is True, f"prototype={pr}")
    check("E8G-23_prototype_no_remote",
          "http://" not in proto and "https://" not in proto, "prototype is self-contained (no remote refs)")
    check("E8G-23_prototype_marks_non_binding",
          "non-binding" in proto.lower() or "bağlayıcı değildir" in proto.lower(),
          "prototype states it is non-binding")
    for theme_name, t in themes.items():
        for token, value in t.items():
            if token.startswith("tone_") or token in {"surface", "on_surface"}:
                check("E8G-24_prototype_uses_palette", value.lower() in proto.lower(),
                      f"{theme_name}.{token}={value} missing from prototype")

    # --- 25. Forbidden / boundaries / acceptance ---------------------------
    forbidden = set(w["forbidden_geometry_patterns"])
    expected_forbidden = {
        "region_order_differing_from_owning_spec",
        "destination_identity_or_order_changing_by_window_class",
        "second_copy_of_shared_surface_with_own_truth",
        "countdown_or_urgency_in_focused_flow_chrome",
        "exit_below_min_target_or_hidden_in_menu",
        "ratio_bar_gauge_or_percentage_in_progress_overview",
        "primary_chip_replacing_axis_block", "state_truncation_at_large_text",
        "green_amber_red_severity_ramp", "dark_palette_inverted_without_measurement",
        "prototype_presented_as_implementation",
    }
    check("E8G-25_forbidden_patterns", expected_forbidden <= forbidden,
          f"missing={sorted(expected_forbidden - forbidden)}")
    check("E8G-25_forbidden_count", len(forbidden) == 11, f"count={len(forbidden)}")

    boundaries = {str(k): v for k, v in w["future_stage_boundaries"].items()}
    for stage in ["9A", "9C", "10", "12", "13", "14", "16", "17", "18"]:
        check("E8G-26_boundary_open", stage in boundaries, f"stage={stage}")
    check("E8G-26_stage8_closes", w["stage_8_closes_on_acceptance"] is True, "Stage 8 closes on acceptance")

    acc = w["acceptance"]
    required_models = {"UXIA-v0", "THUX-v0", "TRUX-v0", "ASUX-v0", "SPWX-v0", "VDSX-v0"}
    check("E8G-27_required_models", required_models <= set(acc["required_previous_models"]),
          f"missing={sorted(required_models - set(acc['required_previous_models']))}")
    check("E8G-27_contrast_computed_required", acc["contrast_must_be_computed_not_asserted"] is True,
          "acceptance requires computed contrast")
    check("E8G-27_regressions_required",
          all(acc[k] is True for k in
              ["independent_qa_required", "stage6_regression_required", "stage7_regression_required",
               "stage8a_regression_required", "stage8b_regression_required", "stage8c_regression_required",
               "stage8d_regression_required", "stage8e_regression_required", "stage8f_regression_required"]),
          f"acceptance={acc}")

    # --- 28. Spec / research textual contract -------------------------------
    spec_markers = [
        "WFPX-v0 — Wireframe & Prototype Geometry",
        "`D-074`",
        "Geometry arranges accepted meaning",
        "`attention` is **violet**, not amber",
        "Dark is **not** an inversion of light",
        "The prototype is a **visualisation, not an implementation**",
        "9A — Mobil teknoloji seçimi",
    ]
    check("E8G-28_spec_contract", all(m in spec for m in spec_markers),
          f"missing={[m for m in spec_markers if m not in spec]}")
    for theme_name, t in themes.items():
        for token, value in t.items():
            check("E8G-28_spec_documents_hex", value in spec, f"{theme_name}.{token}={value} missing from spec")

    research_markers = [
        "A new separate external Research AI is **not required**",
        "A palette that is measured, not asserted",
        "Avoiding the traffic-light reading",
        "Geometry without pixel mockups",
        "A prototype that is not a technology commitment",
    ]
    check("E8G-29_research_synthesis", all(m in research for m in research_markers),
          f"missing={[m for m in research_markers if m not in research]}")

    return finish(args.write_report, w)


def finish(write_report: bool, w) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "8G",
        "model": "WFPX-v0",
        "candidate_decision": "D-074",
        "result": result,
        "status_observed": w.get("status") if isinstance(w, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "window_classes": len(w.get("window_classes", [])) if isinstance(w, dict) else None,
        "surfaces_with_geometry": len(w.get("surface_geometry", {})) if isinstance(w, dict) else None,
        "contrast_pairs_measured": len(measured),
        "contrast_min_observed": round(min((m["ratio"] for m in measured), default=0), 2),
        "forbidden_geometry_patterns": len(w.get("forbidden_geometry_patterns", [])) if isinstance(w, dict) else None,
        "measured_contrast": measured,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"8G_WIREFRAME_PROTOTYPE_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"window_classes={report['window_classes']} surfaces={report['surfaces_with_geometry']} "
          f"contrast_pairs={report['contrast_pairs_measured']} min_ratio={report['contrast_min_observed']} "
          f"forbidden={report['forbidden_geometry_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
