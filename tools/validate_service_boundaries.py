from __future__ import annotations

import argparse
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
BOUND = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
SPEC = ROOT / "docs/SERVICE_BOUNDARIES_SPEC.md"
RESEARCH = ROOT / "research/9d_service_boundaries_research.md"
TECH = ROOT / "arch/9a_mobile_technology/technology.yaml"
PERSIST = ROOT / "arch/9b_local_first_persistence/persistence.yaml"
DM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
FLOW = ROOT / "ux/8c_daily_working_flow/flow.yaml"
SESSION = ROOT / "ux/8d_assessment_session/session.yaml"
PROGRESS = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
PLANNER = ROOT / "docs/ADAPTIVE_PLANNER_SPEC.md"
V1 = ROOT / "docs/V1_SCOPE.md"
REPORT = ROOT / "arch/9d_service_boundaries/qa_report.yaml"

checks: list[dict] = []
failures: list[str] = []


def check(name: str, condition: bool, details: str) -> None:
    result = "PASS" if condition else "FAIL"
    checks.append({"check": name, "result": result, "details": details})
    if not condition:
        failures.append(f"{name}: {details}")


def load_yaml(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def has_cycle(graph: dict[str, list[str]]) -> list[str]:
    """Return a cycle path if one exists, else []."""
    WHITE, GREY, BLACK = 0, 1, 2
    color = {n: WHITE for n in graph}
    stack: list[str] = []

    def visit(node: str) -> list[str]:
        color[node] = GREY
        stack.append(node)
        for nxt in graph.get(node, []):
            if nxt not in color:
                continue
            if color[nxt] == GREY:
                return stack[stack.index(nxt):] + [nxt]
            if color[nxt] == WHITE:
                found = visit(nxt)
                if found:
                    return found
        stack.pop()
        color[node] = BLACK
        return []

    for n in graph:
        if color[n] == WHITE:
            found = visit(n)
            if found:
                return found
    return []


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write-report", action="store_true")
    args = parser.parse_args()

    for path in [BOUND, SPEC, RESEARCH, TECH, PERSIST, DM, FLOW, SESSION, PROGRESS, PLANNER, V1]:
        check("E9D-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None, [])

    b = load_yaml(BOUND)
    tech = load_yaml(TECH)
    persist = load_yaml(PERSIST)
    dm = load_yaml(DM)
    flow = load_yaml(FLOW)
    session = load_yaml(SESSION)
    progress = load_yaml(PROGRESS)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")
    planner = PLANNER.read_text(encoding="utf-8")
    v1 = V1.read_text(encoding="utf-8")

    # --- 1. Identity -------------------------------------------------------
    check("E9D-01_identity", b.get("model") == "MSBX-v0" and b.get("stage_step") == "9D",
          f"model={b.get('model')} step={b.get('stage_step')}")
    check("E9D-01_status", b.get("status") in {"candidate_9d", "accepted_9d"}, f"status={b.get('status')}")
    check("E9D-01_decision", (b.get("candidate_decision") or b.get("decision")) == "D-078",
          f"decision={b.get('candidate_decision') or b.get('decision')}")
    check("E9D-01_lineage",
          b.get("platform") == "AMTS-v0" and b.get("persistence") == "LFPS-v0" and b.get("data_model") == "DDM-v0",
          f"lineage={b.get('platform')}/{b.get('persistence')}/{b.get('data_model')}")

    # --- 2. Scope ----------------------------------------------------------
    scope = b["scope"]
    check("E9D-02_boundaries_only", scope["boundaries_only"] is True, "9D is boundaries only")
    deferred = ["di_library_locked", "build_syntax_locked", "test_strategy_locked",
                "ai_provider_and_prompts_locked", "concrete_names_locked"]
    check("E9D-02_deferred_open", all(scope[k] is False for k in deferred),
          f"violations={[k for k in deferred if scope[k] is not False]}")
    check("E9D-02_upstream_untouched",
          scope["stage8_semantics_changed"] is False and scope["persistence_rules_changed"] is False
          and scope["data_model_changed"] is False, "upstream contracts untouched")
    nulls = ["module_count_target", "layer_depth_rule", "package_naming_convention", "performance_claim"]
    check("E9D-02_no_invented_rules", all(scope[k] is None for k in nulls),
          f"violations={[k for k in nulls if scope[k] is not None]}")

    # --- 3. Invariants -----------------------------------------------------
    inv = b["invariants"]
    expected_false = [
        "core_depends_on_data", "core_depends_on_ai", "core_depends_on_app", "dependency_cycle_present",
        "ai_client_referenced_from_core", "http_or_network_type_in_core",
        "core_reads_system_clock_directly", "randomness_in_core", "seeded_random_in_core",
        "engine_writes_another_engines_state", "planner_writes_learner_state",
        "transaction_opened_inside_engine", "persistence_called_from_ui",
        "presentation_state_computed_in_ui_toolkit", "platform_type_in_port_signature",
        "domain_logic_in_wiring", "null_evaluator_is_test_only_fixture", "ships_without_null_evaluator",
        "deterministic_capability_degrades_without_evaluator", "library_named_here",
    ]
    check("E9D-03_false_guards", all(inv[k] is False for k in expected_false),
          f"violations={[k for k in expected_false if inv.get(k) is not False]}")
    expected_true = ["dependency_rule_is_checkable", "null_evaluator_ships_with_product",
                     "app_builds_without_ai_adapter", "clock_is_a_port",
                     "ties_broken_by_declared_total_ordering", "transaction_boundary_in_core_application",
                     "presentation_projection_in_core"]
    check("E9D-03_true_guards", all(inv[k] is True for k in expected_true),
          f"violations={[k for k in expected_true if inv.get(k) is not True]}")

    # --- 4. Module graph: computed, not asserted --------------------------
    modules = {m["id"]: m for m in b["modules"]}
    expected_modules = {"core-model", "core-ports", "core-engines", "core-application", "core-presentation",
                        "data-persistence", "data-curriculum", "ai-adapter", "app-ui", "app-wiring"}
    check("E9D-04_module_set", set(modules) == expected_modules, f"modules={sorted(modules)}")
    graph = {mid: m.get("depends_on", []) for mid, m in modules.items()}

    unknown = {(mid, dep) for mid, deps in graph.items() for dep in deps if dep not in modules}
    check("E9D-04_no_unknown_dep", not unknown, f"unknown_deps={sorted(unknown)}")

    cycle = has_cycle(graph)
    check("E9D-05_acyclic", not cycle, f"cycle={cycle}")

    def layer(mid: str) -> str:
        return modules[mid]["layer"]

    forbidden = {(f["from_layer"], f["to_layer"]) for f in b["dependency_rule"]["forbidden_edges"]}
    violations = [(mid, dep) for mid, deps in graph.items() for dep in deps
                  if (layer(mid), layer(dep)) in forbidden]
    check("E9D-06_no_forbidden_edge", not violations, f"violations={violations}")
    check("E9D-06_forbidden_set", forbidden == {("core", "data"), ("core", "ai"), ("core", "app")},
          f"forbidden={sorted(forbidden)}")

    core_modules = [mid for mid in modules if layer(mid) == "core"]
    check("E9D-07_core_isolated",
          all(layer(dep) == "core" for mid in core_modules for dep in graph[mid]),
          f"core edges={[(m, d) for m in core_modules for d in graph[m] if layer(d) != 'core']}")
    check("E9D-07_core_model_leaf", graph["core-model"] == [], f"core-model deps={graph['core-model']}")

    non_root_all = [mid for mid in modules
                    if mid != "app-wiring" and set(graph[mid]) >= (set(modules) - {mid})]
    check("E9D-08_only_root_knows_all", not non_root_all, f"non_root_knowing_all={non_root_all}")
    check("E9D-08_root_is_root",
          modules["app-wiring"].get("is_composition_root") is True
          and modules["app-wiring"].get("contains_domain_logic") is False
          and b["dependency_rule"]["only_composition_root_may_depend_on_all"] is True,
          "app-wiring is the composition root without domain logic")
    check("E9D-08_root_depends_on_all",
          set(graph["app-wiring"]) == set(modules) - {"app-wiring"},
          f"root deps={sorted(graph['app-wiring'])}")
    check("E9D-08_rule_checkable",
          b["dependency_rule"]["rule_is_checkable_not_aspirational"] is True
          and b["dependency_rule"]["graph_must_be_acyclic"] is True
          and bool(b["dependency_rule"]["rationale"]), "dependency rule is checkable with rationale")

    # --- 9. Ports ----------------------------------------------------------
    ports = {p["id"]: p for p in b["ports"]["set"]}
    check("E9D-09_port_set",
          set(ports) == {"PersistencePort", "ContentPort", "ClockPort", "EvaluatorPort"},
          f"ports={sorted(ports)}")
    check("E9D-09_ports_in_core",
          b["ports"]["live_in"] == "core-ports" and b["ports"]["expressed_in_core_types"] is True
          and b["ports"]["platform_type_in_signature"] is False, f"ports={b['ports']['live_in']}")
    check("E9D-09_persistence_impl", ports["PersistencePort"]["implemented_by"] == "data-persistence",
          f"persistence impl={ports['PersistencePort']['implemented_by']}")
    check("E9D-09_lfps_core_owned",
          persist["dependency_direction"]["core_declares_persistence_interfaces"] is True
          and persist["dependency_direction"]["module_layout_owner"] == "9D",
          "LFPS-v0 really assigns module layout to 9D")
    check("E9D-09_amts_defers_boundary",
          tech["deterministic_core"]["boundary_layout_owner"] == "9D",
          "AMTS-v0 really assigns boundary layout to 9D")

    # --- 10. Clock port ----------------------------------------------------
    cp = b["clock_port"]
    check("E9D-10_clock_port",
          cp["is_a_port"] is True and cp["core_reads_system_clock_directly"] is False
          and cp["time_is_ambient_fact"] is False and cp["time_is_input"] is True, f"clock={cp['is_a_port']}")
    check("E9D-10_clock_provides",
          set(cp["provides"]) == {"instant", "learner_local_study_day", "utc_offset"},
          f"provides={cp['provides']}")
    check("E9D-10_clock_matches_ddm",
          set(dm["time_representation"]["fields_on_every_timestamped_row"]) ==
          {"occurred_at_instant", "occurred_on_study_day", "utc_offset_minutes"},
          "DDM-v0 really requires all three time values")
    check("E9D-10_clock_rationale",
          bool(cp["rationale_determinism"]) and bool(cp["rationale_timezone"]),
          "clock port rationale recorded")

    # --- 11. Determinism ---------------------------------------------------
    det = b["determinism"]
    check("E9D-11_no_randomness",
          det["randomness_in_core"] is False and det["seeded_random_port"] is False
          and bool(det["seeded_random_rejected_because"]), f"determinism={det['randomness_in_core']}")
    check("E9D-11_tie_breaking",
          det["tie_breaking"] == "declared_total_ordering" and det["tie_breaking_contract"] == "PBR-v0"
          and det["same_inputs_same_result"] is True, f"tie_breaking={det['tie_breaking']}")
    check("E9D-11_planner_requires_determinism",
          "aynı capacity envelope ve semantik seçim sonucunu üretmelidir" in planner,
          "ADAPTIVE_PLANNER_SPEC really requires determinism")

    # --- 12. AI absence is structural -------------------------------------
    ai = b["ai_absence"]
    check("E9D-12_core_clean",
          ai["core_references_ai_client"] is False and ai["core_references_http_client"] is False
          and ai["core_references_network_type"] is False, "core references no AI/HTTP/network type")
    check("E9D-12_null_evaluator",
          ai["evaluator_is_a_port"] is True and ai["null_evaluator_ships_with_product"] is True
          and ai["null_evaluator_is_test_fixture"] is False, "null evaluator ships with the product")
    check("E9D-12_builds_without_adapter",
          ai["app_builds_without_ai_adapter"] is True and ai["app_runs_without_ai_adapter"] is True,
          "app builds and runs without the AI adapter")
    check("E9D-13_pending_semantics",
          ai["with_null_evaluator_open_ended_becomes"] == "evaluation_pending"
          and ai["with_null_evaluator_evidence_written"] is False
          and ai["deterministic_capability_degrades"] is False, f"pending={ai}")
    check("E9D-13_trux_agrees",
          flow["degraded_and_recovery"]["ai_unavailable"]["evaluation_pending_writes_evidence"] is False,
          "TRUX-v0 really forbids evidence for evaluation_pending")
    check("E9D-13_asux_agrees",
          session["degraded_and_recovery"]["ai_evaluator_unavailable"]["evidence_written"] is False,
          "ASUX-v0 really forbids evidence for evaluation_pending")
    check("E9D-13_v1_criterion",
          ai["satisfies_v1_criterion"] == 8 and ai["satisfied_by"] == "wiring_not_hope"
          and "AI Tutor yokken deterministic local core çökmemeli" in v1,
          "V1 criterion 8 is real and satisfied structurally")
    check("E9D-13_ai_adapter_optional", modules["ai-adapter"].get("optional") is True,
          "ai-adapter is declared optional")

    # --- 14. Engine ownership ---------------------------------------------
    engines = {e["engine"]: e for e in b["engine_ownership"]}
    expected_engines = {"GRE-v0", "RVR-v0", "PRG-v0", "TSM-v0", "WLRM-v0", "PBR-v0", "PDT-v0", "TEPM-v0"}
    check("E9D-14_engine_set", set(engines) == expected_engines, f"engines={sorted(engines)}")
    owned = [e["owns"] for e in b["engine_ownership"]]
    check("E9D-14_one_owner_each", len(owned) == len(set(owned)), f"duplicate owners={len(owned) - len(set(owned))}")
    er = b["engine_rules"]
    check("E9D-15_engine_rules",
          er["one_state_family_per_engine"] is True and er["engine_writes_another_engines_state"] is False
          and er["planner_writes_mastery"] is False and er["planner_writes_retention"] is False
          and er["planner_writes_readiness"] is False and er["planner_writes_weakness"] is False
          and er["assessment_writes_retention_directly"] is False, f"engine rules={er}")
    check("E9D-15_cross_engine_via_application",
          er["cross_engine_effects_via"] == "core_application_calling_engines_in_declared_order"
          and bool(er["rationale"]), "cross-engine effects go through the application layer")
    check("E9D-15_gre_owns_mastery", engines["GRE-v0"]["owns"] == "mastery_state",
          f"GRE owns={engines['GRE-v0']['owns']}")
    check("E9D-15_planner_owns_plans",
          engines["PDT-v0"]["owns"] == "plan_versions_planned_tasks_and_decision_traces",
          f"PDT owns={engines['PDT-v0']['owns']}")

    # --- 16. Transaction boundary -----------------------------------------
    tb = b["transaction_boundary"]
    check("E9D-16_owner", tb["owner_module"] == "core-application", f"owner={tb['owner_module']}")
    check("E9D-16_not_elsewhere",
          tb["lives_in_ui_handlers"] is False and tb["lives_in_engine"] is False
          and tb["engine_opens_transaction"] is False and tb["engine_calls_persistence_directly"] is False
          and tb["engines_are_pure_policy"] is True, f"transaction={tb}")
    expected_shape = ["validate_against_current_state", "append_truth_records",
                      "run_engines_in_declared_order", "write_updated_projections",
                      "commit_as_one_transaction"]
    check("E9D-16_use_case_shape", tb["use_case_shape"] == expected_shape, f"shape={tb['use_case_shape']}")
    check("E9D-16_lfps_atomic",
          persist["transactional_boundaries"]["unit"] == "one_learner_action_is_one_transaction",
          "LFPS-v0 really requires one action per transaction")

    # --- 17. Presentation --------------------------------------------------
    pr = b["presentation"]
    check("E9D-17_projection_module", pr["projection_module"] == "core-presentation",
          f"module={pr['projection_module']}")
    check("E9D-17_not_in_ui",
          pr["computed_in_ui_toolkit"] is False and pr["outputs_pure_data"] is True
          and pr["ui_module_responsibility"] == "rendering_only"
          and pr["ui_carries_meaning_beyond_rendering"] is False, f"presentation={pr}")
    check("E9D-17_covers_surfaces",
          {"spwx_presentation_state", "today_view_model", "task_runner_view_model",
           "assessment_session_view_model", "progress_view_model"} <= set(pr["covers"]),
          f"covers={pr['covers']}")
    check("E9D-17_spwx_deterministic",
          progress["invariants"]["presentation_projection_deterministic"] is True,
          "SPWX-v0 really declares the projection deterministic")
    check("E9D-17_rationale", bool(pr["rationale"]), "presentation placement rationale recorded")

    # --- 18. Composition root ---------------------------------------------
    cr = b["composition_root"]
    check("E9D-18_root",
          cr["module"] == "app-wiring" and cr["only_module_knowing_every_implementation"] is True
          and cr["contains_domain_logic"] is False and cr["contains_policy"] is False
          and cr["contains_state"] is False and cr["chooses_real_or_null_evaluator"] is True
          and cr["swapping_implementation_touches_only_this_module"] is True, f"root={cr}")

    # --- 19. Forbidden / boundaries / acceptance --------------------------
    forbidden_patterns = set(b["forbidden_boundary_patterns"])
    expected_forbidden = {
        "core_module_depending_on_data_ai_or_app", "cycle_in_module_graph",
        "ai_http_or_network_type_referenced_from_core", "core_reading_system_clock_directly",
        "randomness_in_core_seeded_or_otherwise", "engine_writing_another_engines_state",
        "planner_writing_mastery_retention_readiness_or_weakness",
        "transaction_opened_inside_engine", "persistence_called_directly_from_ui",
        "presentation_state_computation_inside_compose", "platform_type_in_port_signature",
        "domain_logic_in_wiring", "shipping_without_null_evaluator",
        "treating_null_evaluator_as_test_only_fixture", "naming_di_or_build_library_here",
    }
    check("E9D-19_forbidden_patterns", expected_forbidden <= forbidden_patterns,
          f"missing={sorted(expected_forbidden - forbidden_patterns)}")
    check("E9D-19_forbidden_count", len(forbidden_patterns) == 15, f"count={len(forbidden_patterns)}")

    boundaries_map = {str(k): v for k, v in b["future_stage_boundaries"].items()}
    for stage in ["9E", "9F", "10A", "12", "13", "14"]:
        check("E9D-20_boundary_open", stage in boundaries_map, f"stage={stage}")
    check("E9D-20_9e_owns_ai",
          boundaries_map["9E"] == "ai_integration_architecture_behind_evaluator_port",
          f"9E={boundaries_map['9E']}")

    acc = b["acceptance"]
    required_models = {"AMTS-v0", "LFPS-v0", "DDM-v0", "GRE-v0", "RVR-v0", "PRG-v0", "TSM-v0", "WLRM-v0",
                       "PBR-v0", "PDT-v0", "TEPM-v0", "SPWX-v0", "TRUX-v0", "ASUX-v0"}
    check("E9D-21_required_models", required_models <= set(acc["required_previous_models"]),
          f"missing={sorted(required_models - set(acc['required_previous_models']))}")
    check("E9D-21_graph_verification_required", acc["dependency_graph_must_be_verified"] is True,
          "acceptance requires graph verification")
    check("E9D-21_regressions_required",
          all(acc[k] is True for k in ["independent_qa_required", "stage6_regression_required",
                                       "stage7_regression_required", "stage8_regression_required",
                                       "stage9a_regression_required", "stage9b_regression_required",
                                       "stage9c_regression_required"]), f"acceptance={acc}")

    # --- 22. Spec / research textual contract ------------------------------
    spec_markers = [
        "MSBX-v0 — Module & Service Boundaries",
        "`D-078`",
        "The boundaries make the guarantees structural",
        "core-* may never depend on data-*, ai-* or app-*",
        "## 5.1 The clock is a port",
        "## 5.2 There is no randomness port",
        "null evaluator implementation is part of the shipped product",
        "9E — AI entegrasyon mimarisi",
    ]
    check("E9D-22_spec_contract", all(mk in spec for mk in spec_markers),
          f"missing={[mk for mk in spec_markers if mk not in spec]}")
    check("E9D-22_spec_no_invented_rules",
          "No module-count target, layer-depth rule, package-naming convention or performance claim is canonical in 9D." in spec,
          "spec refuses invented rules")
    for mid in expected_modules:
        check("E9D-22_spec_lists_module", mid in spec, f"module={mid}")

    research_markers = [
        "is currently a promise",
        "Determinism dies quietly",
        "Time is an input, not an ambient fact",
        "Presentation logic must not live in the UI toolkit",
        "Engines must not write each other's state",
    ]
    check("E9D-23_research_synthesis", all(mk in research for mk in research_markers),
          f"missing={[mk for mk in research_markers if mk not in research]}")

    return finish(args.write_report, b, sorted(modules))


def finish(write_report: bool, b, module_ids) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "9D",
        "model": "MSBX-v0",
        "candidate_decision": "D-078",
        "result": result,
        "status_observed": b.get("status") if isinstance(b, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "modules": len(module_ids),
        "module_ids": module_ids,
        "ports": len(b.get("ports", {}).get("set", [])) if isinstance(b, dict) else None,
        "engines": len(b.get("engine_ownership", [])) if isinstance(b, dict) else None,
        "forbidden_boundary_patterns": len(b.get("forbidden_boundary_patterns", [])) if isinstance(b, dict) else None,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"9D_SERVICE_BOUNDARIES_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"modules={report['modules']} ports={report['ports']} engines={report['engines']} "
          f"forbidden={report['forbidden_boundary_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
