"""Independent 9F QA — TVSX-v0 Test & Verification Strategy.

The strategy is validated against the contracts it claims to verify, not against
itself: every registered invariant must exist in the upstream accepted contract
with the declared value, the AI outcome set must match `AIAX-v0`, the contrast
anchors and target size must match `WFPX-v0`, the persistence expectations must
match `LFPS-v0`, and the five upstream deferrals to 9F must be present in the
raw spec text of the specs that made them.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]

CONTRACT = ROOT / "arch/9f_test_strategy/test_strategy.yaml"
SPEC = ROOT / "docs/TEST_STRATEGY_SPEC.md"
RESEARCH = ROOT / "research/9f_test_strategy_research.md"
QA_OUT = ROOT / "arch/9f_test_strategy/qa_report.yaml"

SOURCE_CONTRACTS = {
    "MSBX-v0": "arch/9d_service_boundaries/boundaries.yaml",
    "LFPS-v0": "arch/9b_local_first_persistence/persistence.yaml",
    "DDM-v0": "arch/9c_domain_data_model/data_model.yaml",
    "AIAX-v0": "arch/9e_ai_integration/ai_integration.yaml",
    "AMTS-v0": "arch/9a_mobile_technology/technology.yaml",
    "SPWX-v0": "ux/8e_progress_skill_weakness/progress.yaml",
    "VDSX-v0": "ux/8f_design_system/design_system.yaml",
    "WFPX-v0": "ux/8g_wireframe_prototype/wireframe.yaml",
}

# Upstream specs that explicitly handed test strategy to this step.
DEFERRAL_SOURCES = {
    "docs/MOBILE_TECHNOLOGY_SPEC.md": "test strategy and tooling",
    "docs/LOCAL_FIRST_PERSISTENCE_SPEC.md": "test strategy",
    "docs/DOMAIN_DATA_MODEL_SPEC.md": "test strategy, including how append-only is enforced and verified",
    "docs/SERVICE_BOUNDARIES_SPEC.md": "test strategy and CI enforcement of the dependency rule",
    "docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md": "test strategy, including verification of the null-evaluator path",
}

# A test strategy may not pin a concrete library; 10A owns that, with currency verified.
# "truth" is deliberately absent: it collides with this product's own vocabulary
# ("truth tables", "source of truth") and would report a false positive.
CONCRETE_LIBRARIES = [
    "junit", "espresso", "robolectric", "mockk", "mockito", "assertj",
    "turbine", "kotest", "sqldelight", "detekt", "konsist", "archunit",
    "jacoco", "kover", "paparazzi", "roborazzi", "uiautomator", "gradle",
]

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def load(rel: str):
    return yaml.safe_load((ROOT / rel).read_text(encoding="utf-8"))


def find_values(node, key: str) -> list:
    """Every value stored under `key` anywhere in an upstream contract."""
    found: list = []
    if isinstance(node, dict):
        for k, v in node.items():
            if k == key:
                found.append(v)
            found.extend(find_values(v, key))
    elif isinstance(node, list):
        for item in node:
            found.extend(find_values(item, key))
    return found


contract = yaml.safe_load(CONTRACT.read_text(encoding="utf-8"))
spec_text = SPEC.read_text(encoding="utf-8")
research_text = RESEARCH.read_text(encoding="utf-8")
sources = {name: load(rel) for name, rel in SOURCE_CONTRACTS.items()}

# ---------------------------------------------------------------- identity
check("E9F-01_model", contract.get("model") == "TVSX-v0", str(contract.get("model")))
check("E9F-01_status", contract.get("status") == "accepted_9f", str(contract.get("status")))
check("E9F-01_decision", contract.get("decision") == "D-081", str(contract.get("decision")))
check("E9F-01_boundaries", contract.get("boundaries") == "MSBX-v0", str(contract.get("boundaries")))
check("E9F-01_distribution_scope", contract.get("distribution_scope") == "D-080",
      str(contract.get("distribution_scope")))
check("E9F-01_closes_stage_9", contract.get("closes_stage_9") is True, str(contract.get("closes_stage_9")))

scope = contract.get("scope", {})
for key, expected in [
    ("verification_strategy_only", True), ("test_libraries_locked", False), ("ci_platform_locked", False),
    ("build_files_locked", False), ("new_product_semantics_introduced", False),
    ("stage8_semantics_changed", False), ("persistence_rules_changed", False),
    ("data_model_changed", False), ("boundaries_changed", False), ("ai_integration_changed", False),
]:
    check(f"E9F-02_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")
for key in ("defect_detection_rate_claim", "coverage_number_claim", "suite_runtime_budget",
            "tooling_currency_claim"):
    check(f"E9F-02_noclaim_{key}", scope.get(key, "missing") is None, f"{key}={scope.get(key)}")

# ------------------------------------------------------------- invariants
inv = contract.get("invariants", {})
for key, expected in [
    ("guarantee_without_a_failing_check_is_a_preference", True),
    ("every_accepted_invariant_has_an_owning_check", True),
    ("unowned_invariant_blocks_release", True),
    ("coverage_percentage_is_a_release_gate", False),
    ("negative_verification_required_for_prohibitions", True),
    ("append_only_verified_by_attempted_violation", True),
    ("migration_verified_against_populated_fixtures", True),
    ("migration_verified_only_against_empty_database", False),
    ("null_evaluator_verified_by_building_without_adapter", True),
    ("live_ai_provider_called_in_checks", False),
    ("all_seven_ai_outcomes_covered", True),
    ("refusal_fixture_treated_as_wrong_answer", False),
    ("end_to_end_timeout_budget_verified", True),
    ("per_call_timeout_asserted_as_end_to_end", False),
    ("determinism_exercised_with_injected_clock", True),
    ("system_clock_read_in_checks", False),
    ("repeated_runs_must_be_byte_identical", True),
    ("flaky_check_is_a_failing_check", True),
    ("retry_to_green_allowed", False),
    ("failing_check_disabled_to_unblock_release", False),
    ("fake_may_implement_guarantee_under_test", False),
    ("contrast_recomputed_not_asserted", True),
    ("hand_computed_value_asserted_where_computable", False),
    ("mutation_discipline_applies_to_product_suite", True),
    ("product_suite_replaces_spec_validators", False),
    ("spec_validator_sweep_is_full_glob", True),
    ("passing_suite_presented_as_learning_quality_evidence", False),
    ("new_product_semantics_introduced_by_test_strategy", False),
]:
    check(f"E9F-03_inv_{key}", inv.get(key) is expected, f"{key}={inv.get(key)}")

# ------------------------------------------------------------------ tiers
tiers = {t["id"]: t for t in contract.get("tiers", [])}
check("E9F-04_tier_count", len(tiers) == 6, f"tiers={sorted(tiers)}")
check("E9F-04_tier_ids", sorted(tiers) == ["T1", "T2", "T3", "T4", "T5", "T6"], f"{sorted(tiers)}")
check("E9F-04_t1_no_device", tiers.get("T1", {}).get("device") is False
      and tiers.get("T1", {}).get("network") is False
      and tiers.get("T1", {}).get("database") is False, str(tiers.get("T1")))
check("E9F-04_t1_clock_injected", tiers.get("T1", {}).get("clock") == "injected", str(tiers.get("T1", {}).get("clock")))
check("E9F-04_t2_fake_not_substitute",
      tiers.get("T2", {}).get("fake_repository_substitutes_for_this_tier") is False,
      str(tiers.get("T2", {}).get("fake_repository_substitutes_for_this_tier")))
check("E9F-04_t3_build_time", tiers.get("T3", {}).get("fails_at") == "build_time",
      str(tiers.get("T3", {}).get("fails_at")))
check("E9F-04_t4_contrast_recomputed",
      tiers.get("T4", {}).get("contrast_source") == "recomputed_from_declared_hex",
      str(tiers.get("T4", {}).get("contrast_source")))
check("E9F-04_t5_no_live_provider", tiers.get("T5", {}).get("live_provider") is False,
      str(tiers.get("T5", {}).get("live_provider")))
check("E9F-04_t6_single_device", tiers.get("T6", {}).get("runtime") == "single_target_device"
      and tiers.get("T6", {}).get("smallest_tier") is True, str(tiers.get("T6")))
check("E9F-04_t6_criterion_9", tiers.get("T6", {}).get("satisfies_v1_criterion") == 9,
      str(tiers.get("T6", {}).get("satisfies_v1_criterion")))

# -------------------------------------------------------- negative checks
negatives = {n["id"]: n for n in contract.get("negative_checks_required", [])}
check("E9F-05_negative_count", len(negatives) >= 9, f"count={len(negatives)}")
required_negatives = {
    "update_on_truth_table", "delete_on_truth_table", "core_module_depends_on_data_ai_or_app",
    "platform_type_in_port_signature", "observable_partial_write_after_failed_transaction",
    "partial_migration_applied", "restore_from_newer_schema",
    "schema_invalid_evaluator_response_treated_as_verdict", "refusal_producing_negative_evidence",
}
declared_negatives = {n.get("forbidden") for n in negatives.values()}
check("E9F-05_negative_coverage", required_negatives <= declared_negatives,
      f"missing={sorted(required_negatives - declared_negatives)}")
check("E9F-05_negative_rejectors", all(n.get("must_be_rejected_by") for n in negatives.values()),
      "every negative check names the layer that must reject it")
mig_negative = next((n for n in negatives.values() if n.get("forbidden") == "partial_migration_applied"), {})
check("E9F-05_migration_surfaces", mig_negative.get("must_surface") == "data_recovery_required",
      str(mig_negative.get("must_surface")))

# ------------------------------------------------------------ determinism
det = contract.get("determinism_verification", {})
check("E9F-06_clock_injected", det.get("clock_is_injected") is True, str(det.get("clock_is_injected")))
check("E9F-06_no_system_clock", det.get("system_clock_read_in_checks") is False,
      str(det.get("system_clock_read_in_checks")))
check("E9F-06_repeated_runs", det.get("repeated_runs_required") is True, str(det.get("repeated_runs_required")))
check("E9F-06_no_retry_to_green", det.get("retry_to_green") is False, str(det.get("retry_to_green")))
check("E9F-06_time_cases", {"day_boundary", "dst_transition", "travel_offset_change"}
      <= set(det.get("time_cases_exercised", [])), str(det.get("time_cases_exercised")))
# cross-read: MSBX must actually forbid randomness and require same-inputs-same-result
msbx_det = sources["MSBX-v0"].get("determinism", {})
check("E9F-06_upstream_no_randomness", msbx_det.get("randomness_in_core") is False,
      f"MSBX randomness_in_core={msbx_det.get('randomness_in_core')}")
check("E9F-06_upstream_same_inputs", msbx_det.get("same_inputs_same_result") is True,
      f"MSBX same_inputs_same_result={msbx_det.get('same_inputs_same_result')}")
check("E9F-06_no_seed_available", det.get("randomness_available_to_seed") is False
      and msbx_det.get("seeded_random_port") is False, "no seeded random exists upstream to seed")
check("E9F-06_clock_port_name", det.get("clock_port") == "ClockPort"
      and "ClockPort" in {p["id"] for p in sources["MSBX-v0"]["ports"]["set"]},
      "clock port name matches the accepted port set")

# ------------------------------------------------------------ persistence
pv = contract.get("persistence_verification", {})
lfps = sources["LFPS-v0"]
check("E9F-07_append_schema_level", pv.get("append_only", {}).get("verified_at") == "schema_level",
      str(pv.get("append_only", {}).get("verified_at")))
check("E9F-07_append_attempted_violation",
      pv.get("append_only", {}).get("method") == "attempted_update_and_delete_must_be_rejected",
      str(pv.get("append_only", {}).get("method")))
check("E9F-07_upstream_no_update", lfps["invariants"].get("truth_record_updated_in_place") is False,
      "LFPS forbids in-place truth update")
check("E9F-07_tx_stages", {"attempt", "artifact", "assistance_metadata", "provenance", "evidence",
                           "derived_state_update"} <= set(pv.get("transactions", {}).get("stages", [])),
      str(pv.get("transactions", {}).get("stages")))
check("E9F-07_tx_no_partial", pv.get("transactions", {}).get("partial_state_observable") is False,
      str(pv.get("transactions", {}).get("partial_state_observable")))
mig = pv.get("migration", {})
check("E9F-07_mig_populated", mig.get("populated_fixtures_required") is True,
      str(mig.get("populated_fixtures_required")))
check("E9F-07_mig_upstream_requires_populated",
      lfps["migration"].get("tested_against_populated_database") is True
      and lfps["migration"].get("tested_only_against_empty_database") is False,
      "LFPS requires migration tested against a populated database")
check("E9F-07_mig_preserved", set(mig.get("preserved_exactly", [])) == {"evidence", "exposure", "provenance"},
      str(mig.get("preserved_exactly")))
check("E9F-07_mig_comparison", mig.get("comparison") == "counts_and_content_not_sampled",
      str(mig.get("comparison")))
check("E9F-07_mig_interrupted", mig.get("interrupted_migration_surfaces") == "data_recovery_required"
      and lfps["migration"].get("incomplete_migration_surfaces") == "data_recovery_required",
      "interrupted migration surfaces the upstream recovery state")
check("E9F-07_mig_downgrade", mig.get("downgrade_verified_unsupported") is True
      and lfps["migration"].get("downgrade_supported") is False, "downgrade stays unsupported")
ber = pv.get("backup_export_restore", {})
check("E9F-07_export_no_key", ber.get("export_contains_api_key") is False,
      str(ber.get("export_contains_api_key")))
check("E9F-07_upstream_export_no_key", sources["AIAX-v0"]["invariants"].get("key_included_in_export") is False,
      "AIAX forbids the key in the export")
check("E9F-07_restore_atomic", ber.get("restore_atomic") is True
      and lfps["backup_export_restore"].get("restore_atomic") is True, "restore atomic upstream and here")
check("E9F-07_restore_verified_first", ber.get("restore_verified_before_replacing") is True
      and lfps["backup_export_restore"].get("restore_verified_before_replacing") is True,
      "restore verified before replacing")
check("E9F-07_exposure_blocking", pv.get("exposure", {}).get("release_blocking") is True,
      str(pv.get("exposure", {}).get("release_blocking")))
check("E9F-07_upstream_exposure_permanent", lfps["invariants"].get("exposure_record_droppable") is False,
      "LFPS forbids dropping exposure records")

# --------------------------------------------------------- AI verification
aiv = contract.get("ai_verification", {})
aiax = sources["AIAX-v0"]
upstream_outcomes = {o["id"]: o for o in aiax["outcome_taxonomy"]["outcomes"]}
declared_outcomes = {o["id"]: o for o in aiv.get("outcomes_covered", [])}
check("E9F-08_live_provider", aiv.get("live_provider_called") is False, str(aiv.get("live_provider_called")))
check("E9F-08_outcome_set_matches_aiax", set(declared_outcomes) == set(upstream_outcomes),
      f"declared={sorted(declared_outcomes)} upstream={sorted(upstream_outcomes)}")
check("E9F-08_seven_outcomes", len(declared_outcomes) == 7, f"count={len(declared_outcomes)}")
for oid, upstream in upstream_outcomes.items():
    declared = declared_outcomes.get(oid, {})
    check(f"E9F-08_outcome_{oid}",
          declared.get("writes_evidence") == upstream.get("writes_evidence"),
          f"declared={declared.get('writes_evidence')} upstream={upstream.get('writes_evidence')}")
non_answers = [oid for oid, o in upstream_outcomes.items() if o.get("degrades_to") == "evaluation_pending"]
check("E9F-08_non_answer_count", len(non_answers) == 5, f"non_answers={sorted(non_answers)}")
check("E9F-08_non_answer_no_evidence",
      all(declared_outcomes.get(oid, {}).get("writes_evidence") is False for oid in non_answers),
      "every non-answer outcome writes no evidence")
check("E9F-08_refusal_not_wrong_answer",
      declared_outcomes.get("refused", {}).get("treated_as_wrong_answer") is False
      and aiax["outcome_taxonomy"].get("refusal_is_wrong_answer") is False,
      "refusal is not a wrong answer here or upstream")
check("E9F-08_no_free_text_fallback", aiv.get("free_text_fallback_path_exists") is False,
      str(aiv.get("free_text_fallback_path_exists")))
check("E9F-08_payload_never_sent",
      set(aiv.get("payload_assertions", {}).get("must_not_contain", [])) == set(aiax["privacy"]["never_sent"]),
      f"declared={sorted(aiv.get('payload_assertions', {}).get('must_not_contain', []))} "
      f"upstream={sorted(aiax['privacy']['never_sent'])}")
check("E9F-08_deterministic_zero_calls", aiv.get("deterministic_operations_evaluator_calls") == 0,
      str(aiv.get("deterministic_operations_evaluator_calls")))
check("E9F-08_upstream_deterministic_no_ai", aiax["invariants"].get("deterministic_work_calls_ai") is False,
      "AIAX forbids AI calls for deterministic work")

# ---------------------------------------------------- null-evaluator path
nev = contract.get("null_evaluator_verification", {})
msbx_ai = sources["MSBX-v0"]["ai_absence"]
check("E9F-09_method_build", nev.get("method") == "build_product_without_ai_adapter", str(nev.get("method")))
check("E9F-09_stub_insufficient", nev.get("stub_alone_is_sufficient") is False,
      str(nev.get("stub_alone_is_sufficient")))
check("E9F-09_upstream_ships", msbx_ai.get("null_evaluator_ships_with_product") is True
      and msbx_ai.get("null_evaluator_is_test_fixture") is False,
      "MSBX ships the null evaluator and refuses to call it a fixture")
check("E9F-09_upstream_builds", msbx_ai.get("app_builds_without_ai_adapter") is True,
      str(msbx_ai.get("app_builds_without_ai_adapter")))
check("E9F-09_pending", nev.get("open_ended_attempt_becomes") == "evaluation_pending"
      and msbx_ai.get("with_null_evaluator_open_ended_becomes") == "evaluation_pending",
      "open-ended attempt becomes evaluation_pending here and upstream")
check("E9F-09_no_evidence", nev.get("evidence_written") is False
      and msbx_ai.get("with_null_evaluator_evidence_written") is False, "no evidence on the null path")
check("E9F-09_no_degradation", nev.get("deterministic_capability_degrades") is False,
      str(nev.get("deterministic_capability_degrades")))
check("E9F-09_criterion_8", nev.get("satisfies_v1_criterion") == 8
      and msbx_ai.get("satisfies_v1_criterion") == 8, "both target V1 criterion 8")
check("E9F-09_release_blocking", nev.get("release_blocking") is True, str(nev.get("release_blocking")))

# ------------------------------------------------------------ presentation
pres = contract.get("presentation_verification", {})
wfpx = sources["WFPX-v0"]
anchors = pres.get("contrast_anchors", {})
check("E9F-10_contrast_text_anchor",
      anchors.get("text") == wfpx["palette"]["contrast_requirements"]["text_on_surface_min"],
      f"declared={anchors.get('text')} upstream={wfpx['palette']['contrast_requirements']['text_on_surface_min']}")
check("E9F-10_contrast_nontext_anchor",
      anchors.get("large_text_and_non_text")
      == wfpx["palette"]["contrast_requirements"]["non_text_and_boundary_min"],
      f"declared={anchors.get('large_text_and_non_text')} "
      f"upstream={wfpx['palette']['contrast_requirements']['non_text_and_boundary_min']}")
check("E9F-10_recomputed", pres.get("contrast_recomputed_from_hex") is True
      and wfpx["acceptance"].get("contrast_must_be_computed_not_asserted") is True,
      "contrast recomputed here and required upstream")
check("E9F-10_target_dp",
      pres.get("minimum_target_dp") == wfpx["surface_geometry"]["task_runner_flow"]["exit_min_target_dp"],
      f"declared={pres.get('minimum_target_dp')}")
check("E9F-10_text_scales", 200 in pres.get("text_scales_checked", [])
      and wfpx["text_scaling"]["max_supported_percent"] == 200, "200% text checked and supported upstream")
check("E9F-10_themes", set(pres.get("themes_checked", [])) == {"light", "dark"}, str(pres.get("themes_checked")))
check("E9F-10_skill_states", pres.get("skill_states_verified") == 8, str(pres.get("skill_states_verified")))
check("E9F-10_forbidden_presentations",
      {"pass_fail_banner", "grade", "threshold", "mastery_percentage", "streak", "competence_progress_bar"}
      <= set(pres.get("forbidden_presentations_verified_absent", [])),
      str(pres.get("forbidden_presentations_verified_absent")))
check("E9F-10_no_fault_tone_on_learning_state", pres.get("learning_state_may_carry_fault_tone") is False
      and sources["VDSX-v0"]["invariants"].get("learning_state_uses_fault_tone") is False,
      "no learning state carries the fault tone here or upstream")

# ------------------------------------------------------------- severities
severities = {s["id"]: s for s in contract.get("severity_classes", [])}
check("E9F-11_severity_count", len(severities) == 6, f"{sorted(severities)}")
check("E9F-11_evidence_always_blocking",
      severities.get("evidence_correctness", {}).get("release_effect") == "always_blocking",
      str(severities.get("evidence_correctness", {}).get("release_effect")))
for sid in ("structural", "data_safety"):
    check(f"E9F-11_{sid}_blocking", severities.get(sid, {}).get("release_effect") == "always_blocking",
          str(severities.get(sid, {}).get("release_effect")))
check("E9F-11_advisory_non_blocking",
      severities.get("advisory", {}).get("release_effect") == "non_blocking_recorded",
      str(severities.get("advisory", {}).get("release_effect")))

# ----------------------------------------------------------- release gate
gate = contract.get("release_gate", {})
conditions = {c["id"]: c["condition"] for c in gate.get("conditions", [])}
check("E9F-12_no_partial_pass", gate.get("partial_pass_allowed") is False, str(gate.get("partial_pass_allowed")))
check("E9F-12_no_coverage_gate", gate.get("coverage_percentage_gate") is False,
      str(gate.get("coverage_percentage_gate")))
check("E9F-12_gate_is_invariant_coverage", gate.get("gate_is") == "invariant_coverage", str(gate.get("gate_is")))
check("E9F-12_condition_count", len(conditions) == 11, f"count={len(conditions)}")
check("E9F-12_condition_ids_unique", len(conditions) == len(gate.get("conditions", [])), "release gate ids unique")
check("E9F-12_full_glob_condition",
      any("validate_glob" in c or "validate_glob_passes" in c for c in conditions.values()),
      "release gate requires the full validator glob")
check("E9F-12_device_condition", any("single_target_device" in c for c in conditions.values()),
      "release gate names the single target device")

# ------------------------------------------------- V1 criteria cross-read
v1_text = (ROOT / "docs/V1_SCOPE.md").read_text(encoding="utf-8")
release_block = v1_text.split("# V1 RELEASE TANIMI", 1)[1] if "# V1 RELEASE TANIMI" in v1_text else ""
upstream_criteria = re.findall(r"^(\d{1,2})\.\s+\S", release_block[:1200], flags=re.M)
check("E9F-13_v1_has_ten_criteria", [int(x) for x in upstream_criteria[:10]] == list(range(1, 11)),
      f"parsed={upstream_criteria[:12]}")
crit_map = {int(k): v for k, v in contract.get("v1_criteria_map", {}).items()}
check("E9F-13_criteria_mapped", sorted(crit_map) == list(range(1, 11)), f"mapped={sorted(crit_map)}")
check("E9F-13_criteria_nonempty", all(isinstance(v, list) and v for v in crit_map.values()),
      "every criterion maps to at least one check")
check("E9F-13_criterion_8_null_eval", any("NULLEVAL" in c for c in crit_map.get(8, [])),
      f"criterion 8 -> {crit_map.get(8)}")
check("E9F-13_criterion_9_device", any(c.startswith("T6-") for c in crit_map.get(9, [])),
      f"criterion 9 -> {crit_map.get(9)}")
check("E9F-13_criterion_7_persistence",
      all(c.startswith("T2-") for c in crit_map.get(7, [])), f"criterion 7 -> {crit_map.get(7)}")

# ------------------------------------------------------ invariant register
register = contract.get("invariant_register", [])
check("E9F-14_register_nonempty", len(register) >= 40, f"entries={len(register)}")
seen_invariants = set()
for entry in register:
    name = entry.get("invariant")
    model = entry.get("source_model")
    upstream_values = find_values(sources.get(model, {}), name)
    check(f"E9F-14_upstream_{model}_{name}",
          entry.get("expected") in upstream_values,
          f"expected={entry.get('expected')} upstream={upstream_values[:3]}")
    seen_invariants.add((model, name))
check("E9F-14_no_duplicate_entries", len(seen_invariants) == len(register), "register has no duplicate rows")
check("E9F-15_owner_present", all(e.get("owner_check") for e in register),
      "every registered invariant has an owning check")
check("E9F-15_owner_tier_prefix",
      all(e.get("owner_check", "").split("-")[0] == e.get("tier") for e in register),
      "owner check id prefix matches its tier")
check("E9F-15_tier_valid", all(e.get("tier") in tiers for e in register), "every register tier exists")
check("E9F-15_severity_valid", all(e.get("severity") in severities for e in register),
      "every register severity is a declared class")
covered_tiers = {e["tier"] for e in register}
# T6 is excluded by design: the strategy requires anything verifiable off-device to be
# verified off-device, so the device tier owns V1 criteria (flows) rather than invariants.
off_device_tiers = set(tiers) - {"T6"}
check("E9F-16_offdevice_tiers_own_something", off_device_tiers <= covered_tiers,
      f"tiers without a registered invariant={sorted(off_device_tiers - covered_tiers)}")
check("E9F-16_device_tier_owns_no_invariant", "T6" not in covered_tiers,
      "the device tier verifies flows, not invariants that could be checked off-device")
check("E9F-16_device_tier_owns_criteria",
      any(any(c.startswith("T6-") for c in checks) for checks in crit_map.values()),
      "the device tier owns at least one V1 criterion")
covered_models = {e["source_model"] for e in register}
check("E9F-17_all_source_models_present", covered_models == set(SOURCE_CONTRACTS),
      f"missing={sorted(set(SOURCE_CONTRACTS) - covered_models)}")

# ---------------------------------------------------------------- meta
meta = contract.get("meta_rules", {})
check("E9F-18_mutation_required", meta.get("mutation_discipline_required") is True,
      str(meta.get("mutation_discipline_required")))
check("E9F-18_fake_rule", meta.get("fake_implements_guarantee_under_test") is False,
      str(meta.get("fake_implements_guarantee_under_test")))
check("E9F-18_fixed_clock_allowed", meta.get("fixed_clock_fake_allowed") is True,
      "a fixed clock is an input, not the guarantee under test")
check("E9F-18_no_disable", meta.get("failing_check_disabled_to_unblock_release") is False,
      str(meta.get("failing_check_disabled_to_unblock_release")))
check("E9F-18_separate_systems", meta.get("spec_validators_and_product_suite_are_separate_systems") is True,
      str(meta.get("spec_validators_and_product_suite_are_separate_systems")))
check("E9F-18_full_glob", meta.get("sweep_is_full_validate_glob") is True,
      str(meta.get("sweep_is_full_validate_glob")))
check("E9F-18_recomputed_values", {"contrast", "counts", "orderings", "hashes"}
      <= set(meta.get("values_recomputed_not_hand_asserted", [])),
      str(meta.get("values_recomputed_not_hand_asserted")))

# --------------------------------------------------------------- tooling
check("E9F-19_capabilities_listed", len(contract.get("tooling_capabilities_required", [])) >= 8,
      f"count={len(contract.get('tooling_capabilities_required', []))}")
check("E9F-19_no_concrete_libraries", contract.get("concrete_libraries_named") is False,
      str(contract.get("concrete_libraries_named")))
check("E9F-19_library_owner", contract.get("library_selection_owner") == "10A",
      str(contract.get("library_selection_owner")))
check("E9F-19_currency_reverified", contract.get("currency_reverification_required") is True,
      str(contract.get("currency_reverification_required")))
lowered_spec = spec_text.lower()
named = [lib for lib in CONCRETE_LIBRARIES if re.search(rf"\b{lib}\b", lowered_spec)]
check("E9F-19_spec_names_no_library", not named, f"named={named}")
contract_lowered = CONTRACT.read_text(encoding="utf-8").lower()
named_contract = [lib for lib in CONCRETE_LIBRARIES if re.search(rf"\b{lib}\b", contract_lowered)]
check("E9F-19_contract_names_no_library", not named_contract, f"named={named_contract}")
check("E9F-19_no_adapter_build_capability",
      any("without_ai_adapter" in c for c in contract.get("tooling_capabilities_required", [])),
      "tooling must be able to build the product without the AI adapter")

# ---------------------------------------------------- forbidden patterns
forbidden = set(contract.get("forbidden_test_patterns", []))
for pattern in [
    "coverage_percentage_as_release_gate",
    "invariant_with_no_owning_check",
    "verifying_only_the_allowed_path_for_a_prohibition",
    "append_only_check_that_never_attempts_update_or_delete",
    "migrating_only_an_empty_database",
    "fake_implementing_the_guarantee_under_test",
    "calling_a_live_ai_provider_from_a_check",
    "treating_a_refusal_fixture_as_a_wrong_answer",
    "reading_the_system_clock_inside_a_check",
    "retrying_a_flaky_check_until_green",
    "disabling_or_quarantining_a_failing_check_to_unblock_a_release",
    "presenting_a_passing_suite_as_learning_quality_evidence",
    "running_a_hand_maintained_subset_of_validate_scripts_as_the_sweep",
    "device_only_verification_of_something_verifiable_off_device",
    "introducing_new_product_semantics_in_the_test_strategy",
]:
    check(f"E9F-20_forbidden_{pattern[:38]}", pattern in forbidden, f"missing={pattern}")
check("E9F-20_forbidden_count", len(forbidden) >= 18, f"count={len(forbidden)}")

# --------------------------------------------------------- spec contract
for fragment in [
    "**Status:** ACCEPTED — independent 9F QA PASS",
    "**Decision:** `D-081`",
    "A guarantee that nothing fails on is a preference",
    "Coverage percentage is not a gate",
    "A check that only passes sometimes is a failing check",
    "No check calls a live AI provider",
    "building the product without `ai-adapter`",
    "populated fixtures",
    "recomputed from the declared hex values",
    "A green suite proves the product behaves as its accepted models say",
    "invariant coverage",
]:
    check(f"E9F-21_spec_{fragment[:40]}", fragment in spec_text, f"missing={fragment!r}")
check("E9F-21_spec_closes_stage9", "AŞAMA 9 closes" in spec_text, "spec states Stage 9 closes")
check("E9F-21_spec_next_step", "10A" in spec_text and "target device" in spec_text,
      "spec hands over to 10A and names the unrecorded target device")
check("E9F-21_spec_no_coverage_number", not re.search(r"\b\d{2,3}\s*%\s*coverage", lowered_spec),
      "spec claims no coverage percentage")

# ------------------------------------------- upstream deferrals to 9F
for rel, fragment in DEFERRAL_SOURCES.items():
    text = (ROOT / rel).read_text(encoding="utf-8")
    check(f"E9F-22_deferral_{Path(rel).stem[:34]}", f"{fragment} → 9F" in text,
          f"missing deferral in {rel}: {fragment!r}")

# --------------------------------------------------- research synthesis
for fragment in [
    "A guarantee that nothing fails on is a preference",
    "Coverage percentage is the wrong gate",
    "Tests must never call a live AI provider",
    "Migrations are only meaningful against populated data",
    "Flaky tests are worse than failing tests here",
]:
    check(f"E9F-23_research_{fragment[:36]}", fragment in research_text, f"missing={fragment!r}")

# ------------------------------------------------ distribution scope D-080
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
check("E9F-24_d080_recorded", "## D-080 — Dağıtım kapsamı" in decisions, "D-080 recorded in DECISIONS")
check("E9F-24_d080_in_v1_scope", "**D-080:**" in v1_text, "D-080 reflected in V1_SCOPE")
dse = contract.get("distribution_scope_effects", {})
check("E9F-24_scope_decision_ref", dse.get("decision") == "D-080", str(dse.get("decision")))
check("E9F-24_single_device", dse.get("device_matrix") == "single_target_device", str(dse.get("device_matrix")))
check("E9F-24_target_device_open", dse.get("target_device_recorded_in_repo") is False,
      "the target device is still not recorded and 9F says so")
check("E9F-24_store_out_of_scope", dse.get("store_release_verification") == "out_of_scope",
      str(dse.get("store_release_verification")))
check("E9F-24_key_invariant_untouched",
      sources["AIAX-v0"]["invariants"].get("key_in_platform_secure_storage") is True,
      "D-080 did not silently relax the AIAX key-storage invariant")

# -------------------------------------------------------------- acceptance
acceptance = contract.get("acceptance", {})
check("E9F-25_required_models", {"MSBX-v0", "LFPS-v0", "DDM-v0", "AIAX-v0", "AMTS-v0", "SPWX-v0",
                                 "VDSX-v0", "WFPX-v0", "PBR-v0"}
      <= set(acceptance.get("required_previous_models", [])),
      str(acceptance.get("required_previous_models")))
for key in ("independent_qa_required", "stage6_regression_required", "stage7_regression_required",
            "stage8_regression_required", "stage9a_regression_required", "stage9b_regression_required",
            "stage9c_regression_required", "stage9d_regression_required", "stage9e_regression_required"):
    check(f"E9F-25_{key}", acceptance.get(key) is True, f"{key}={acceptance.get(key)}")

# ------------------------------------------------------------------ report
passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "TVSX-v0",
    "stage_step": "9F",
    "decision": "D-081",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results),
    "checks_passed": passed,
    "checks_failed": len(failures),
    "register_entries": len(register),
    "tiers": len(tiers),
    "release_gate_conditions": len(conditions),
    "checks": results,
    "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"9F_TEST_STRATEGY_QA={report['result']}")
print(f"checks={passed}/{len(results)} register={len(register)} tiers={len(tiers)} "
      f"gate_conditions={len(conditions)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not failures else 1)
