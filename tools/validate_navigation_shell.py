"""Independent 10B QA — NSHX-v0 Navigation Shell.

The shell is validated against the accepted information architecture, not against its own
contract: the destination ids and order, the forbidden top-level set, every contextual edge, the
shared-detail set and the focus-flow return semantics are read out of `UXIA-v0`'s `ia.yaml`, the
window classes and their shells out of `WFPX-v0`'s `wireframe.yaml`, and both are compared to the
**actual Kotlin source**.

If the shell and the accepted IA ever disagree, this fails.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]

CONTRACT = ROOT / "arch/10b_navigation/navigation.yaml"
SPEC = ROOT / "docs/NAVIGATION_SHELL_SPEC.md"
RESEARCH = ROOT / "research/10b_navigation_research.md"
QA_OUT = ROOT / "arch/10b_navigation/qa_report.yaml"

IA = ROOT / "ux/8a_information_architecture/ia.yaml"
WIREFRAME = ROOT / "ux/8g_wireframe_prototype/wireframe.yaml"
BOUNDARIES = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

NAV_SOURCE = ROOT / "android/core-presentation/src/main/kotlin/coach/presentation/Navigation.kt"
NAV_TEST = ROOT / "android/core-presentation/src/test/kotlin/coach/presentation/NavigationTest.kt"
SHELL_UI = ROOT / "android/app-ui/src/main/kotlin/coach/ui/AppShell.kt"
ACTIVITY = ROOT / "android/app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


contract = load(CONTRACT)
ia = load(IA)
wireframe = load(WIREFRAME)
boundaries = load(BOUNDARIES)

for path in (NAV_SOURCE, NAV_TEST, SHELL_UI, ACTIVITY, SPEC, RESEARCH):
    check(f"E10B-00_exists_{path.name}", path.is_file(), f"missing {path}")

nav_src = read(NAV_SOURCE)
nav_test = read(NAV_TEST)
shell_ui = read(SHELL_UI)
activity = read(ACTIVITY)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity
check("E10B-01_model", contract.get("model") == "NSHX-v0", str(contract.get("model")))
check("E10B-01_status", contract.get("status") == "accepted_10b", str(contract.get("status")))
check("E10B-01_decision", contract.get("decision") == "D-083", str(contract.get("decision")))
check("E10B-01_ia_ref", contract.get("information_architecture") == "UXIA-v0",
      str(contract.get("information_architecture")))
for key in ("ia_changed", "geometry_changed", "boundaries_changed", "new_ia_introduced"):
    check(f"E10B-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")
for key in ("animation_claim", "navigation_performance_claim"):
    check(f"E10B-01_noclaim_{key}", contract.get("scope", {}).get(key, "missing") is None,
          f"{key}={contract.get('scope', {}).get(key)}")

# ------------------------------------------- destinations match the accepted IA
ia_destinations = sorted(ia["primary_destinations"], key=lambda d: d["order"])
ia_ids = [d["id"] for d in ia_destinations]
kotlin_enum = re.search(r"enum class Destination\(val id: String\) \{(.*?)\n\}", nav_src, re.S)
check("E10B-02_enum_found", kotlin_enum is not None, "Destination enum not found")
kotlin_ids = re.findall(r'^\s{4}[A-Z_]+\("([a-z_]+)"\),', kotlin_enum.group(1), re.M) if kotlin_enum else []
check("E10B-02_ids_and_order_match_ia", kotlin_ids == ia_ids,
      f"kotlin={kotlin_ids} ia={ia_ids}")
check("E10B-02_exactly_four", len(ia_ids) == 4 and len(kotlin_ids) == 4,
      f"ia={len(ia_ids)} kotlin={len(kotlin_ids)}")
check("E10B-02_start_is_today",
      ia_ids[0] == "today" and "val start: Destination = TODAY" in nav_src,
      "today must be the start destination")
check("E10B-02_enum_order_is_canonical", contract["destinations"].get("enum_order_is_canonical") is True
      and contract["destinations"].get("second_list_to_keep_in_sync") is False,
      "the enum must be the only ordered list")

# -------------------------------------- forbidden top-level destinations absent
forbidden_top_level = set(ia["top_level_forbidden"])
check("E10B-03_no_forbidden_destination", not (forbidden_top_level & set(kotlin_ids)),
      f"forbidden ids present: {sorted(forbidden_top_level & set(kotlin_ids))}")
check("E10B-03_forbidden_set_nonempty", len(forbidden_top_level) >= 10,
      f"ia lists {len(forbidden_top_level)} forbidden top-level ids")
check("E10B-03_tested", all(f'"{fid}"' in nav_test for fid in ("assessment", "english", "ai_chat")),
      "a test must assert the forbidden ids are not destinations")

# ------------------------------------------------ contextual edges match the IA
ia_edges = {(a, b) for a, b in ia["navigation_edges"]["contextual"]}
kotlin_edge_block = re.search(r"contextualEdges: Set<Pair<String, String>> = setOf\((.*?)\n    \)", nav_src, re.S)
check("E10B-04_edge_block_found", kotlin_edge_block is not None, "contextualEdges not found")
kotlin_edges = set(re.findall(r'"([a-z_]+)" to "([a-z_]+)"', kotlin_edge_block.group(1))) if kotlin_edge_block else set()
check("E10B-04_edges_match_ia", kotlin_edges == ia_edges,
      f"missing={sorted(ia_edges - kotlin_edges)} extra={sorted(kotlin_edges - ia_edges)}")
check("E10B-04_edges_closed", contract.get("unlisted_transition_navigable") is False
      and "an unlisted contextual edge is not navigable" in nav_test,
      "an unlisted transition must be non-navigable and tested")
check("E10B-04_peer_switching_total",
      len(ia["navigation_edges"]["shell_peer_switching"]) == 6 and "fun canSwitch" in nav_src,
      "all four destinations must be mutual peers")

# ----------------------------------------------- shared details are single surfaces
ia_shared = {d["id"] for d in ia["surface_families"]["shared_details"]}
kotlin_surface_ids = set(re.findall(r'override val id = "([a-z_]+)"', nav_src))
check("E10B-05_shared_details_present", ia_shared <= kotlin_surface_ids,
      f"missing={sorted(ia_shared - kotlin_surface_ids)}")
for sid in sorted(ia_shared):
    occurrences = len(re.findall(rf'override val id = "{sid}"', nav_src))
    check(f"E10B-05_single_surface_{sid}", occurrences == 1,
          f"{sid} declared {occurrences} times; a shared surface must be one object")
skill = next(d for d in ia["surface_families"]["shared_details"] if d["id"] == "skill_detail")
check("E10B-05_skill_detail_shared_flag", skill.get("shared_single_semantic_surface") is True,
      "UXIA-v0 declares skill_detail a single shared semantic surface")
check("E10B-05_skill_detail_reachable_from_three",
      set(skill["reachable_from"]) == {"today", "learn", "progress"},
      f"reachable_from={skill['reachable_from']}")

# ------------------------------------------------ shell roots match the IA
ia_roots = {r["id"]: r["owner"] for r in ia["surface_families"]["shell_roots"]}
for root_id, owner in ia_roots.items():
    check(f"E10B-06_root_{root_id}", f'override val id = "{root_id}"' in nav_src,
          f"missing shell root {root_id}")
check("E10B-06_root_count", len(ia_roots) == 4, f"count={len(ia_roots)}")
check("E10B-06_root_per_destination", "every destination has exactly one shell root" in nav_test,
      "one shell root per destination must be tested")

# ------------------------------------------------------- focused flows
check("E10B-07_flows_not_destinations", not ({"task_runner_flow", "assessment_session_flow"} & set(kotlin_ids)),
      "a focused flow must not be a destination")
check("E10B-07_suppresses_shell", "val showsShell: Boolean = !surface.isFocusedFlow" in nav_src,
      "showsShell must be derived from the surface")
check("E10B-07_exit_derived", "val requiresSafeExit: Boolean = surface.isFocusedFlow" in nav_src,
      "requiresSafeExit must be derived, so 'hidden shell with no exit' is unrepresentable")
check("E10B-07_contract_agrees", contract["focused_flow"].get("suppression_is_derived_not_set") is True
      and contract["focused_flow"].get("unsafe_state_representable") is False,
      "the contract must record the derived-state decision")
check("E10B-07_upstream_requires_exit",
      ia["accessibility_semantics"].get("focused_flow_safe_exit") is True,
      "UXIA-v0 requires a safe exit from a focused flow")
check("E10B-07_ui_hides_shell_for_flow", "if (!state.showsShell)" in shell_ui,
      "app-ui must honour shell suppression rather than deciding it")

# ------------------------------------------------------- return semantics
ia_return = ia["focus_flow_return_semantics"]
check("E10B-08_normal_returns_today", ia_return["task_runner_normal"] == "today"
      and "normal daily work returns to today" in nav_test,
      "the normal return target must be today and tested")
check("E10B-08_entity_context_rule",
      ia_return["entity_context_flow"] == "return_to_origin_if_still_valid_else_today"
      and contract["return_rule"]["entity_context_flow"] == ia_return["entity_context_flow"],
      f"contract={contract['return_rule']['entity_context_flow']} ia={ia_return['entity_context_flow']}")
check("E10B-08_stale_origin_rule",
      ia_return["stale_or_invalid_origin"] == "today"
      and "originStillValid" in nav_src,
      "a stale origin must fall back to today")
check("E10B-08_return_only_for_flows",
      'require(flow.isFocusedFlow)' in nav_src and "only a focused flow has a return target" in nav_test,
      "asking for the return target of a non-flow must fail")

# ------------------------------------------------ window classes match WFPX-v0
wfpx_classes = {w["id"]: w for w in wireframe["window_classes"]}
check("E10B-09_class_count", len(wfpx_classes) == 3, f"{sorted(wfpx_classes)}")
check("E10B-09_medium_breakpoint",
      wfpx_classes["medium"]["min_width_dp"] == 600
      and "MEDIUM_MIN_WIDTH_DP = 600" in nav_src,
      f"wfpx={wfpx_classes['medium']['min_width_dp']}")
check("E10B-09_expanded_breakpoint",
      wfpx_classes["expanded"]["min_width_dp"] == 840
      and "EXPANDED_MIN_WIDTH_DP = 840" in nav_src,
      f"wfpx={wfpx_classes['expanded']['min_width_dp']}")
check("E10B-09_compact_upper_bound", wfpx_classes["compact"]["max_width_dp"] == 599,
      f"wfpx compact max={wfpx_classes['compact']['max_width_dp']}")
check("E10B-09_shell_mapping",
      wfpx_classes["compact"]["shell"] == "bottom_navigation_bar"
      and wfpx_classes["medium"]["shell"] == "navigation_rail"
      and wfpx_classes["expanded"]["shell"].startswith("navigation_rail"),
      "the WFPX shell mapping must be the one implemented")
check("E10B-09_breakpoints_tested", "window classes use the accepted breakpoints" in nav_test,
      "the boundary widths must be tested")
check("E10B-09_computed_in_core",
      "fun ofWidthDp" in nav_src
      and not re.search(r"\b(600|840)\b", shell_ui),
      "breakpoints must be computed in core, not in app-ui")
check("E10B-09_order_invariant_upstream",
      wireframe.get("destination_order_invariant") is True
      and wireframe.get("destination_order") == ia_ids,
      f"wfpx order={wireframe.get('destination_order')}")
check("E10B-09_detail_pane_same_truth",
      wireframe.get("detail_pane_shows_same_surface_same_truth") is True
      and "showsDetailPane" in nav_src,
      "the detail pane must show the same surface and truth")
check("E10B-09_detail_pane_hidden_in_flow",
      "&& !surface.isFocusedFlow" in nav_src,
      "the detail pane must not appear during a focused flow")

# --------------------------------------------------- app-ui only renders
check("E10B-10_ui_iterates_enum", "Destination.entries.forEach" in shell_ui,
      "app-ui must iterate the canonical enum rather than a local list")
ui_literal_ids = set(re.findall(r'"(today|learn|progress|profile)"', shell_ui))
check("E10B-10_no_id_literals_in_ui", not ui_literal_ids,
      f"app-ui hardcodes destination ids: {sorted(ui_literal_ids)}")
check("E10B-10_no_presentation_logic_in_ui",
      "ShellPresentation.of" not in shell_ui and "WindowClass.ofWidthDp" not in shell_ui,
      "app-ui must not compute the presentation; core does")
check("E10B-10_activity_uses_core_window_class", "WindowClass.ofWidthDp" in activity,
      "the window class must come from core")
check("E10B-10_upstream_ui_renders_only",
      boundaries["presentation"].get("ui_module_responsibility") == "rendering_only"
      and boundaries["presentation"].get("computed_in_ui_toolkit") is False,
      f"MSBX-v0 presentation={boundaries['presentation'].get('ui_module_responsibility')}")

# ------------------------------------------------------------ accessibility
check("E10B-11_text_label_present", "label = { Text(label) }" in shell_ui,
      "every destination must render a text label")
check("E10B-11_icon_not_sole_meaning", "contentDescription = null" in shell_ui,
      "the icon is decorative because the label already names the destination")
check("E10B-11_state_description", "stateDescription" in shell_ui,
      "selection state must be exposed as text")
check("E10B-11_traversal_index", "traversalIndex" in shell_ui,
      "traversal order must follow the destination order")
check("E10B-11_upstream_requires_text",
      ia["accessibility_semantics"].get("critical_state_not_color_only") is True
      and ia["accessibility_semantics"].get("critical_state_not_icon_only") is True,
      "UXIA-v0 forbids colour-only and icon-only meaning")
check("E10B-11_labels_not_canonical",
      contract["labels"].get("destination_labels_are_canonical") is False
      and contract["labels"].get("label_owner") == 14,
      "the Turkish labels are working microcopy owned by 14")

# ------------------------------------------------------------ runs and QA
runs = {r["id"]: r for r in contract.get("verified_runs", [])}
check("E10B-12_runs_recorded", len(runs) >= 4, f"count={len(runs)}")
check("E10B-12_all_pass", all(r["result"] == "PASS" for r in runs.values()),
      str({k: v["result"] for k, v in runs.items()}))
check("E10B-12_no_adapter_still_builds",
      any("-PwithAiAdapter=false" in r["command"] for r in runs.values()),
      "the no-adapter build must still be verified after the shell landed")
check("E10B-12_build_not_asserted",
      contract["acceptance"].get("build_must_be_run_not_asserted") is True,
      "10B must not claim a build result it did not run")

# ------------------------------------------------------ forbidden patterns
forbidden = set(contract.get("forbidden_navigation_patterns", []))
for pattern in [
    "deciding_the_destination_set_inside_compose",
    "a_second_destination_list_that_can_drift_from_the_enum",
    "a_separate_skill_detail_surface_per_origin",
    "allowing_an_unlisted_contextual_transition",
    "a_top_level_destination_for_assessment_english_ai_or_an_engine",
    "a_focused_flow_that_can_hide_the_shell_without_an_exit",
    "changing_the_destination_set_or_order_with_the_window_class",
    "computing_window_breakpoints_in_the_ui_toolkit",
    "an_icon_as_the_only_carrier_of_a_destination_meaning",
    "stranding_the_learner_on_a_stale_origin_after_a_replan",
    "treating_browse_placement_as_prerequisite_truth",
]:
    check(f"E10B-13_forbidden_{pattern[:36]}", pattern in forbidden, f"missing={pattern}")

# --------------------------------------------------------- spec and synthesis
for fragment in [
    "**Status:** ACCEPTED — independent 10B QA PASS",
    "**Decision:** `D-083`",
    "core-presentation",
    "NavigationSuiteScaffold",
    "safe exit",
    "unlisted",
]:
    check(f"E10B-14_spec_{fragment[:30]}", fragment in spec_text, f"missing={fragment!r}")
for fragment in [
    "No external research pass was required",
    "one surface object per canonical entity",
]:
    check(f"E10B-15_research_{fragment[:32]}", fragment.lower() in research_text.lower(),
          f"missing={fragment!r}")

# -------------------------------------------------------------- acceptance
acceptance = contract.get("acceptance", {})
check("E10B-16_required_models",
      {"UXIA-v0", "WFPX-v0", "TRUX-v0", "SPWX-v0", "MSBX-v0", "MPSX-v0"}
      <= set(acceptance.get("required_previous_models", [])),
      str(acceptance.get("required_previous_models")))
for key in ("independent_qa_required", "stage6_regression_required", "stage7_regression_required",
            "stage8_regression_required", "stage9_regression_required", "stage10a_regression_required"):
    check(f"E10B-16_{key}", acceptance.get(key) is True, f"{key}={acceptance.get(key)}")

# ------------------------------------------------------------------ report
passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "NSHX-v0",
    "stage_step": "10B",
    "decision": "D-083",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results),
    "checks_passed": passed,
    "checks_failed": len(failures),
    "destinations": len(ia_ids),
    "contextual_edges": len(ia_edges),
    "shared_details": len(ia_shared),
    "checks": results,
    "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"10B_NAVIGATION_SHELL_QA={report['result']}")
print(f"checks={passed}/{len(results)} destinations={len(ia_ids)} edges={len(ia_edges)} "
      f"shared_details={len(ia_shared)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not failures else 1)
