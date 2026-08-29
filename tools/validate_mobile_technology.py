from __future__ import annotations

import argparse
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
TECH = ROOT / "arch/9a_mobile_technology/technology.yaml"
SPEC = ROOT / "docs/MOBILE_TECHNOLOGY_SPEC.md"
RESEARCH = ROOT / "research/9a_mobile_technology_research.md"
WF = ROOT / "ux/8g_wireframe_prototype/wireframe.yaml"
DS = ROOT / "ux/8f_design_system/design_system.yaml"
PROGRESS = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
V1 = ROOT / "docs/V1_SCOPE.md"
WORKFLOW = ROOT / "docs/AI_AGENT_WORKFLOW.md"
REPORT = ROOT / "arch/9a_mobile_technology/qa_report.yaml"

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

    for path in [TECH, SPEC, RESEARCH, WF, DS, PROGRESS, IA, V1, WORKFLOW]:
        check("E9A-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None)

    t = load_yaml(TECH)
    wf = load_yaml(WF)
    ds = load_yaml(DS)
    progress = load_yaml(PROGRESS)
    ia = load_yaml(IA)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")
    v1 = V1.read_text(encoding="utf-8")
    workflow = WORKFLOW.read_text(encoding="utf-8")

    # --- 1. Identity -------------------------------------------------------
    check("E9A-01_identity", t.get("model") == "AMTS-v0" and t.get("stage_step") == "9A",
          f"model={t.get('model')} step={t.get('stage_step')}")
    check("E9A-01_status", t.get("status") in {"candidate_9a", "accepted_9a"}, f"status={t.get('status')}")
    check("E9A-01_decision", (t.get("candidate_decision") or t.get("decision")) == "D-075",
          f"decision={t.get('candidate_decision') or t.get('decision')}")
    expected_contracts = {"UXIA-v0", "THUX-v0", "TRUX-v0", "ASUX-v0", "SPWX-v0", "VDSX-v0", "WFPX-v0"}
    check("E9A-01_implements_stage8", set(t["implements_contracts"]) == expected_contracts,
          f"contracts={t['implements_contracts']}")

    # --- 2. Scope ----------------------------------------------------------
    scope = t["scope"]
    check("E9A-02_selection_only", scope["selection_only"] is True, "9A is selection only")
    deferred = ["storage_engine_locked", "domain_data_model_locked", "service_boundaries_locked",
                "ai_integration_locked", "test_strategy_locked", "di_and_navigation_library_locked",
                "build_pipeline_locked"]
    check("E9A-02_deferred_open", all(scope[k] is False for k in deferred),
          f"violations={[k for k in deferred if scope[k] is not False]}")
    unchanged = ["stage8_semantics_changed", "states_or_labels_changed", "tones_or_geometry_changed"]
    check("E9A-02_stage8_untouched", all(scope[k] is False for k in unchanged),
          f"violations={[k for k in unchanged if scope[k] is not False]}")
    check("E9A-02_no_unverified_claims",
          scope["library_version_locked"] is None and scope["benchmark_number"] is None
          and scope["currency_claim"] is None, "no version/benchmark/currency asserted")

    # --- 3. Invariants -----------------------------------------------------
    inv = t["invariants"]
    expected_false = [
        "technology_overrides_contract", "library_default_overrides_token", "dynamic_color_enabled",
        "component_library_is_source_of_truth", "component_introduces_undefined_state_or_tone",
        "core_depends_on_android_api", "core_depends_on_ui_toolkit", "core_depends_on_network",
        "core_depends_on_ai_client", "cross_platform_ui_layer_in_v1",
        "default_locale_case_transform_allowed", "locked_labels_case_transformed_for_styling",
        "accessibility_reimplemented_instead_of_platform", "min_sdk_is_fixed_magic_number",
        "currency_claim_asserted_without_source", "storage_or_data_model_decided_here",
    ]
    check("E9A-03_false_guards", all(inv[k] is False for k in expected_false),
          f"violations={[k for k in expected_false if inv.get(k) is not False]}")
    expected_true = ["contract_wins_on_conflict", "core_is_pure_kotlin", "window_classes_map_one_to_one",
                     "installable_apk_required", "real_device_qa_required"]
    check("E9A-03_true_guards", all(inv[k] is True for k in expected_true),
          f"violations={[k for k in expected_true if inv.get(k) is not True]}")

    # --- 4. Platform selection cross-checked against V1_SCOPE --------------
    plat = t["platform"]
    check("E9A-04_android_native", plat["target"] == "android_native", f"target={plat['target']}")
    check("E9A-04_no_cross_platform", plat["cross_platform_ui_framework"] == "none",
          f"cross_platform={plat['cross_platform_ui_framework']}")
    check("E9A-04_excluded_platforms", set(plat["v1_excluded_platforms"]) == {"ios", "web", "desktop"},
          f"excluded={plat['v1_excluded_platforms']}")
    check("E9A-04_v1_says_android_only", "V1 Android odaklıdır" in v1,
          "V1_SCOPE states Android focus")
    check("E9A-04_v1_excludes_others", "iOS/web/desktop istemcileri" in v1,
          "V1_SCOPE excludes iOS/web/desktop clients")
    check("E9A-04_portability_hedge", "domain_core" in plat["portability_hedge"],
          f"hedge={plat['portability_hedge']}")
    check("E9A-05_language", t["language"] == "kotlin", f"language={t['language']}")
    ui = t["ui_toolkit"]
    check("E9A-05_ui_toolkit",
          ui["choice"] == "jetpack_compose" and ui["paradigm"] == "declarative_state_driven",
          f"ui={ui}")

    # --- 6. Component library must not override the design system ----------
    cl = t["component_library"]
    check("E9A-06_substrate_only",
          cl["role"] == "substrate_only" and cl["is_source_of_colour_truth"] is False
          and cl["tokens_authoritative"] == "VDSX-v0" and cl["conflict_resolution"] == "contract_wins",
          f"component_library={cl}")
    check("E9A-06_dynamic_color_off", cl["dynamic_color_enabled"] is False,
          "dynamic colour disabled")
    check("E9A-06_dynamic_color_rationale",
          "measured_palette" in cl["dynamic_color_rationale"] and "hue_policy" in cl["dynamic_color_rationale"],
          f"rationale={cl['dynamic_color_rationale']}")
    check("E9A-06_no_default_leak",
          cl["default_colours_reach_screen"] is False and cl["default_typography_reaches_screen"] is False
          and cl["may_introduce_undefined_state_or_tone"] is False, "no library defaults on screen")
    # The design system must actually forbid what we are protecting.
    check("E9A-06_vdsx_forbids_default_override",
          ds["invariants"]["design_system_adds_meaning"] is False
          and ds["color"]["raw_values_in_product_code"] is False,
          "VDSX-v0 keeps colour semantic and adds no meaning")
    check("E9A-06_palette_is_measured", wf["palette"]["measured_per_theme"] is True,
          "WFPX-v0 palette is measured per theme — dynamic colour would discard it")
    check("E9A-06_hue_policy_exists", wf["palette"]["hue_policy"]["traffic_light_ramp"] is False,
          "WFPX-v0 hue policy exists and would be destroyed by dynamic colour")

    # --- 7. Deterministic core --------------------------------------------
    core = t["deterministic_core"]
    expected_forbidden = {"android_api", "ui_toolkit", "networking", "ai_client"}
    check("E9A-07_core_forbidden_deps", set(core["forbidden_dependencies"]) == expected_forbidden,
          f"forbidden={core['forbidden_dependencies']}")
    check("E9A-07_core_purity", core["purity"] == "no_android_no_ui_no_network_no_ai",
          f"purity={core['purity']}")
    expected_owns = {"curriculum_graph", "mastery", "retention", "prerequisite", "planner", "evidence"}
    check("E9A-07_core_owns", set(core["owns"]) == expected_owns, f"owns={core['owns']}")
    check("E9A-07_core_runnable",
          core["testable_without_device"] is True and core["runnable_without_network"] is True,
          "core testable without device and runnable without network")
    check("E9A-07_boundary_deferred", core["boundary_layout_owner"] == "9D",
          f"boundary owner={core['boundary_layout_owner']}")
    check("E9A-07_v1_requires_degraded_core", "AI Tutor yokken deterministic local core çökmemeli" in v1,
          "V1_SCOPE requires the core to survive AI absence")

    # --- 8. Window classes cross-checked against WFPX-v0 -------------------
    wc = {c["id"]: c for c in t["window_classes"]["classes"]}
    wf_wc = {c["id"]: c for c in wf["window_classes"]}
    check("E9A-08_class_ids_match", set(wc) == set(wf_wc), f"9A={sorted(wc)} vs WFPX={sorted(wf_wc)}")
    for cid in wf_wc:
        for key in ("min_width_dp", "max_width_dp", "shell"):
            check("E9A-08_class_field_matches", wc[cid].get(key) == wf_wc[cid].get(key),
                  f"{cid}.{key}: 9A={wc[cid].get(key)} vs WFPX={wf_wc[cid].get(key)}")
    check("E9A-08_one_to_one", t["window_classes"]["mapping_is_one_to_one"] is True
          and t["window_classes"]["source_contract"] == "WFPX-v0", "one-to-one mapping declared")
    check("E9A-09_invariance",
          t["window_classes"]["destination_identity_invariant"] is True
          and t["window_classes"]["destination_order_invariant"] is True
          and t["window_classes"]["region_added_or_removed_by_class"] is False,
          "destination identity/order invariant, no region drift")
    check("E9A-09_ia_has_four_destinations", len(ia["primary_destinations"]) == 4,
          f"destinations={len(ia['primary_destinations'])}")

    # --- 10. Accessibility mapping cross-checked against VDSX -------------
    am = t["accessibility_mapping"]
    check("E9A-10_target_matches_vdsx",
          am["minimum_target_dp"] == ds["spacing_and_targets"]["minimum_touch_target_dp"],
          f"9A={am['minimum_target_dp']} vs VDSX={ds['spacing_and_targets']['minimum_touch_target_dp']}")
    check("E9A-10_focus_contrast_matches_vdsx",
          am["focus_indicator_contrast_min"] == ds["accessibility_rules"]["focus_indicator_contrast_min"],
          f"9A={am['focus_indicator_contrast_min']} vs VDSX={ds['accessibility_rules']['focus_indicator_contrast_min']}")
    for key in ["state_exposed_as_text", "text_scaling_200_percent", "focus_indicator",
                "reduced_motion", "colour_never_sole_carrier"]:
        check("E9A-10_mapping_present", bool(am.get(key)), f"missing mapping: {key}")
    check("E9A-10_contractual", am["requirements_are_contractual_not_aspirational"] is True,
          "accessibility treated as contractual")
    check("E9A-10_vdsx_requires_200",
          ds["typography"]["usable_at_200_percent"] is True, "VDSX-v0 requires 200% usability")

    # --- 11. Locale cross-checked against VDSX ----------------------------
    loc = t["locale"]
    check("E9A-11_no_default_locale_casing",
          loc["default_locale_case_transform_allowed"] is False
          and loc["explicit_locale_required_for_case_conversion"] is True
          and loc["turkish_casing_round_trips"] is True, f"locale casing={loc}")
    check("E9A-11_casing_pairs", loc["casing_pairs"] == [["i", "İ"], ["ı", "I"]],
          f"casing_pairs={loc['casing_pairs']}")
    ds_glyphs = set(ds["typography"]["turkish_safe"]["required_glyphs"])
    check("E9A-11_glyphs_match_vdsx", set(loc["required_glyphs"]) == ds_glyphs,
          f"9A={sorted(set(loc['required_glyphs']))} vs VDSX={sorted(ds_glyphs)}")
    check("E9A-11_mono_required",
          loc["required_in_mono_role"] is True
          and ds["typography"]["turkish_safe"]["required_in_mono_role"] is True,
          "mono role must cover Turkish glyphs in both specs")
    check("E9A-12_locked_labels_safe",
          loc["locked_labels_rendered_as_authored"] is True
          and loc["locked_labels_case_transformed_for_styling"] is False,
          "locked labels rendered as authored")
    check("E9A-12_spwx_labels_exist",
          len(progress["skill_presentation"]["labels_tr"]) == 8
          and len(progress["topic_presentation"]["labels_tr"]) == 6,
          "SPWX-v0 locked labels present")
    check("E9A-12_identifier_casing_safe", loc["identifier_comparison_uses_locale_sensitive_casing"] is False,
          "identifier comparison avoids locale-sensitive casing")

    # --- 13. minSdk policy -------------------------------------------------
    ms = t["min_sdk"]
    check("E9A-13_policy_not_number",
          ms["is_policy_not_magic_number"] is True and ms["is_product_default_not_scientific"] is True,
          f"min_sdk={ms}")
    check("E9A-13_working_default", isinstance(ms["working_default_api"], int) and ms["working_default_api"] > 0,
          f"working_default_api={ms['working_default_api']}")
    check("E9A-13_rationale_recorded", bool(ms["working_default_rationale"]), "rationale recorded")
    check("E9A-13_device_verification",
          ms["target_device_recorded_in_repo"] is False
          and ms["must_be_confirmed_at_10a_against_real_device"] is True,
          "device verification deferred to 10A and flagged as unrecorded")

    # --- 14. Release -------------------------------------------------------
    rel = t["release"]
    check("E9A-14_apk_required",
          rel["installable_apk_required"] is True and rel["app_bundle_required_in_v1"] is False,
          f"release={rel}")
    check("E9A-14_device_qa", rel["real_device_qa_required"] is True, "real-device QA required")
    check("E9A-14_v1_requires_apk", "release APK kurulabilir olmalı" in v1,
          "V1_SCOPE requires an installable release APK")
    check("E9A-14_v1_requires_device_qa", "gerçek Android cihazında bağımsız QA" in v1,
          "V1_SCOPE requires real-device independent QA")

    # --- 15. Verification list --------------------------------------------
    vl = t["verification_list"]
    check("E9A-15_owner", vl["owner_step"] == "10A", f"owner={vl['owner_step']}")
    check("E9A-15_non_empty", isinstance(vl["items"], list) and len(vl["items"]) >= 5,
          f"items={len(vl.get('items', []))}")
    check("E9A-15_cannot_change_selection", vl["cannot_change_selection"] is True,
          "verification cannot change the selection")
    check("E9A-15_research_routing_real",
          vl["research_ai_routed"] is True and "framework/library güncelliği" in workflow
          and "Hangisini seçmeliyiz?" in workflow,
          "AI_AGENT_WORKFLOW really routes these to Research AI")

    # --- 16. Forbidden / boundaries / acceptance --------------------------
    forbidden = set(t["forbidden_selection_patterns"])
    expected_forbidden = {
        "cross_platform_layer_for_platforms_v1_excludes",
        "dynamic_colour_or_library_default_palette_on_screen",
        "component_library_introducing_undefined_state_or_tone",
        "core_depending_on_android_ui_network_or_ai",
        "default_locale_case_transform",
        "case_transforming_locked_labels_for_styling",
        "reimplementing_platform_accessibility_behaviour",
        "asserting_library_version_or_currency_without_source",
        "treating_min_sdk_as_fixed_number_without_device_verification",
        "deciding_storage_data_model_boundaries_ai_or_test_strategy_here",
    }
    check("E9A-16_forbidden_patterns", expected_forbidden <= forbidden,
          f"missing={sorted(expected_forbidden - forbidden)}")
    check("E9A-16_forbidden_count", len(forbidden) == 10, f"count={len(forbidden)}")

    boundaries = {str(k): v for k, v in t["future_stage_boundaries"].items()}
    for stage in ["9B", "9C", "9D", "9E", "9F", "10A", "12", "13", "14", "19"]:
        check("E9A-17_boundary_open", stage in boundaries, f"stage={stage}")
    check("E9A-17_9b_owns_storage", boundaries["9B"] == "local_first_persistence", f"9B={boundaries['9B']}")

    acc = t["acceptance"]
    check("E9A-18_required_models", expected_contracts <= set(acc["required_previous_models"]),
          f"missing={sorted(expected_contracts - set(acc['required_previous_models']))}")
    check("E9A-18_regressions_required",
          all(acc[k] is True for k in ["independent_qa_required", "verification_list_required_non_empty",
                                       "stage6_regression_required", "stage7_regression_required",
                                       "stage8_regression_required"]), f"acceptance={acc}")

    # --- 19. Spec / research textual contract ------------------------------
    spec_markers = [
        "AMTS-v0 — Android Mobile Technology Selection",
        "`D-075`",
        "The technology choice serves the accepted contracts",
        "**Decision:** Android native. No cross-platform UI framework in V1.",
        "**Dynamic colour (Material You) must be disabled.**",
        "**Default-locale case transforms are forbidden in the codebase.**",
        "`minSdk` is a policy",
        "9B — Veri saklama / local-first",
    ]
    check("E9A-19_spec_contract", all(m in spec for m in spec_markers),
          f"missing={[m for m in spec_markers if m not in spec]}")
    check("E9A-19_spec_has_verification_list",
          "Bounded verification list — resolve at 10A" in spec, "spec carries the verification list")
    check("E9A-19_spec_no_currency_claim",
          "No library version, benchmark number or \"currently best framework\" claim is canonical in 9A." in spec,
          "spec refuses currency claims")

    research_markers = [
        "**9A is not.**",
        "Whether a cross-platform layer earns its cost",
        "A design system library will fight the design system",
        "Turkish casing is a platform-API question",
        "bounded verification list",
    ]
    check("E9A-20_research_synthesis", all(m in research for m in research_markers),
          f"missing={[m for m in research_markers if m not in research]}")

    return finish(args.write_report, t)


def finish(write_report: bool, t) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "9A",
        "model": "AMTS-v0",
        "candidate_decision": "D-075",
        "result": result,
        "status_observed": t.get("status") if isinstance(t, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "platform": t.get("platform", {}).get("target") if isinstance(t, dict) else None,
        "ui_toolkit": t.get("ui_toolkit", {}).get("choice") if isinstance(t, dict) else None,
        "window_classes": len(t.get("window_classes", {}).get("classes", [])) if isinstance(t, dict) else None,
        "verification_items": len(t.get("verification_list", {}).get("items", [])) if isinstance(t, dict) else None,
        "forbidden_selection_patterns": len(t.get("forbidden_selection_patterns", [])) if isinstance(t, dict) else None,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"9A_MOBILE_TECHNOLOGY_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"platform={report['platform']} ui={report['ui_toolkit']} "
          f"window_classes={report['window_classes']} verification_items={report['verification_items']} "
          f"forbidden={report['forbidden_selection_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
