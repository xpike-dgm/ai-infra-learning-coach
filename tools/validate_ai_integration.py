from __future__ import annotations

import argparse
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
AI = ROOT / "arch/9e_ai_integration/ai_integration.yaml"
SPEC = ROOT / "docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md"
RESEARCH = ROOT / "research/9e_ai_integration_research.md"
BOUND = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
DM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
PERSIST = ROOT / "arch/9b_local_first_persistence/persistence.yaml"
FLOW = ROOT / "ux/8c_daily_working_flow/flow.yaml"
SESSION = ROOT / "ux/8d_assessment_session/session.yaml"
RULES = ROOT / "docs/LEARNING_BEHAVIOR_RULES.md"
AIV = ROOT / "docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md"
V1 = ROOT / "docs/V1_SCOPE.md"
REPORT = ROOT / "arch/9e_ai_integration/qa_report.yaml"

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

    for path in [AI, SPEC, RESEARCH, BOUND, DM, PERSIST, FLOW, SESSION, RULES, AIV, V1]:
        check("E9E-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None)

    a = load_yaml(AI)
    bound = load_yaml(BOUND)
    dm = load_yaml(DM)
    persist = load_yaml(PERSIST)
    flow = load_yaml(FLOW)
    session = load_yaml(SESSION)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")
    rules = RULES.read_text(encoding="utf-8")
    aiv = AIV.read_text(encoding="utf-8")
    v1 = V1.read_text(encoding="utf-8")

    # --- 1. Identity -------------------------------------------------------
    check("E9E-01_identity", a.get("model") == "AIAX-v0" and a.get("stage_step") == "9E",
          f"model={a.get('model')} step={a.get('stage_step')}")
    check("E9E-01_status", a.get("status") in {"candidate_9e", "accepted_9e"}, f"status={a.get('status')}")
    check("E9E-01_decision", (a.get("candidate_decision") or a.get("decision")) == "D-079",
          f"decision={a.get('candidate_decision') or a.get('decision')}")
    check("E9E-01_boundaries", a.get("boundaries") == "MSBX-v0", f"boundaries={a.get('boundaries')}")

    # --- 2. Scope ----------------------------------------------------------
    scope = a["scope"]
    check("E9E-02_architecture_only", scope["integration_architecture_only"] is True, "9E is architecture only")
    deferred = ["prompt_text_locked", "tutor_ux_locked", "evaluator_calibration_locked",
                "sdk_call_sites_locked", "test_strategy_locked"]
    check("E9E-02_deferred_open", all(scope[k] is False for k in deferred),
          f"violations={[k for k in deferred if scope[k] is not False]}")
    upstream = ["stage8_semantics_changed", "persistence_rules_changed", "data_model_changed",
                "boundaries_changed"]
    check("E9E-02_upstream_untouched", all(scope[k] is False for k in upstream),
          f"violations={[k for k in upstream if scope[k] is not False]}")
    nulls = ["accuracy_claim", "latency_guarantee", "monthly_cost_estimate", "availability_promise"]
    check("E9E-02_no_claims", all(scope[k] is None for k in nulls),
          f"violations={[k for k in nulls if scope[k] is not None]}")

    # --- 3. Invariants -----------------------------------------------------
    inv = a["invariants"]
    expected_false = [
        "ai_writes_mastery", "ai_writes_retention", "ai_satisfies_or_bypasses_prerequisite",
        "ai_changes_planner_priority", "ai_sets_assessment_quota", "ai_decides_learner_has_learned",
        "ai_validates_its_own_generated_item", "ai_confirms_weakness_alone",
        "uncalibrated_llm_evaluation_is_verified", "evaluator_returns_mastery_verdict",
        "free_text_parsing_of_verdict", "schema_invalid_response_treated_as_verdict",
        "refusal_treated_as_wrong_answer", "refusal_produces_negative_evidence",
        "timeout_produces_negative_evidence", "transport_error_produces_negative_evidence",
        "unbounded_retries", "silent_background_retry_against_user_key",
        "per_call_timeout_presented_as_end_to_end_guarantee", "deterministic_work_calls_ai",
        "hardcoded_or_shared_key_in_apk", "backend_proxy_in_v1",
        "key_in_logs_exports_backups_or_diagnostics", "key_included_in_export",
        "evidence_history_sent_to_provider", "mastery_state_sent_to_provider",
        "plan_or_profile_sent_to_provider", "durable_semantic_bound_to_model_name",
        "model_name_in_core", "currency_asserted_without_source",
        "unvalidated_item_produces_strong_evidence", "deterministic_capability_degrades_without_ai",
    ]
    check("E9E-03_false_guards", all(inv[k] is False for k in expected_false),
          f"violations={[k for k in expected_false if inv.get(k) is not False]}")
    expected_true = ["ai_proposes_engines_decide", "evaluator_output_schema_constrained",
                     "uncalibrated_llm_evaluation_is_provisional", "non_answer_yields_evaluation_pending",
                     "timeout_budget_is_end_to_end", "model_name_in_configuration",
                     "learner_supplies_own_key", "key_in_platform_secure_storage",
                     "app_usable_with_ai_disabled", "evaluator_ref_recorded_on_evidence"]
    check("E9E-03_true_guards", all(inv[k] is True for k in expected_true),
          f"violations={[k for k in expected_true if inv.get(k) is not True]}")
    check("E9E-03_guard_count", len(expected_false) >= 30, f"false_guards={len(expected_false)}")

    # --- 4. AI may / may never --------------------------------------------
    may = set(a["ai_may"])
    never = set(a["ai_may_never"])
    required_may = {"alternative_explanation", "hint_at_requested_assistance_level",
                    "help_evaluate_open_ended_response", "code_feedback", "root_cause_analysis",
                    "misconception_hypothesis", "candidate_question_variant_generation"}
    check("E9E-04_ai_may", required_may <= may, f"missing={sorted(required_may - may)}")
    required_never = {"write_or_change_mastery_state", "change_retention_or_review_scheduling",
                      "satisfy_or_bypass_prerequisite", "change_planner_priority_rank_or_capacity",
                      "set_assessment_quota", "decide_learner_has_learned",
                      "validate_its_own_generated_item", "confirm_weakness_on_its_own_judgement",
                      "turn_refusal_timeout_or_error_into_negative_result"}
    check("E9E-04_ai_may_never", required_never <= never, f"missing={sorted(required_never - never)}")
    check("E9E-04_no_overlap", not (may & never), f"overlap={sorted(may & never)}")
    check("E9E-04_authority_rule", a["authority_rule"] == "ai_proposes_deterministic_engines_decide",
          f"authority={a['authority_rule']}")
    # The rules doc must really say AI cannot bypass a hard prerequisite.
    check("E9E-04_rules_forbid_bypass",
          "AI `Bence öğrendi` diyerek hard prerequisite'i keyfi aşamaz." in rules,
          "LEARNING_BEHAVIOR_RULES really forbids AI bypassing a prerequisite")

    # --- 5. Evaluator contract --------------------------------------------
    ec = a["evaluator_contract"]
    check("E9E-05_schema_constrained",
          ec["output_is_schema_constrained"] is True and ec["free_text_parsing_allowed"] is False
          and ec["schema_invalid_response_is"] == "error"
          and ec["schema_invalid_response_is_verdict"] is False, f"evaluator output={ec['schema_invalid_response_is']}")
    check("E9E-05_free_text_rationale", bool(ec["free_text_rationale"]), "free-text rationale recorded")
    required_fields = {"target_objective_refs", "component_results", "outcome_signal",
                       "rubric_findings", "misconception_hypotheses", "evaluator_ref"}
    check("E9E-05_result_fields", required_fields <= set(ec["result_fields"]),
          f"missing={sorted(required_fields - set(ec['result_fields']))}")
    check("E9E-05_no_verdict",
          ec["outcome_signal_is_mastery_verdict"] is False and ec["evaluator_confidence_is_a_gate"] is False
          and ec["misconception_hypotheses_are_confirmed"] is False, "evaluator returns no verdict")
    check("E9E-06_provisional",
          ec["uncalibrated_llm_status"] == "provisional" and ec["provisional_may_inform"] is True
          and ec["provisional_may_open_confirmation_need"] is True
          and ec["provisional_passes_critical_gate_alone"] is False
          and ec["provisional_drives_heavy_remediation_alone"] is False, f"provisional={ec['uncalibrated_llm_status']}")
    check("E9E-06_verified_deterministic",
          ec["verified_requires_deterministic_path"] is True and len(ec["verified_paths"]) >= 5,
          f"verified_paths={len(ec['verified_paths'])}")
    check("E9E-06_aiv_agrees",
          "uncalibrated single-LLM score" in aiv or "uncalibrated single-LLM" in aiv,
          "AIV-v0 really names uncalibrated single-LLM scoring as insufficient")
    check("E9E-06_aiv_ceiling",
          "critical mastery ceiling'e çıkamaz" in aiv,
          "AIV-v0 really caps uncalibrated-LLM-only resources below the critical mastery ceiling")

    # --- 7. Outcome taxonomy: refusal is not failure ----------------------
    ot = a["outcome_taxonomy"]
    outcomes = {o["id"]: o for o in ot["outcomes"]}
    expected_outcomes = {"evaluated_verified", "evaluated_provisional", "refused", "timed_out",
                         "transport_error", "invalid_response", "unavailable"}
    check("E9E-07_outcome_set", set(outcomes) == expected_outcomes, f"outcomes={sorted(outcomes)}")
    non_answer = ["refused", "timed_out", "transport_error", "invalid_response", "unavailable"]
    check("E9E-07_non_answers_pending",
          all(outcomes[o]["writes_evidence"] is False and outcomes[o]["degrades_to"] == "evaluation_pending"
              for o in non_answer),
          f"violations={[o for o in non_answer if outcomes[o].get('writes_evidence') is not False or outcomes[o].get('degrades_to') != 'evaluation_pending']}")
    check("E9E-08_refusal_distinct",
          ot["refusal_is_distinct_from_failure"] is True and ot["refusal_is_wrong_answer"] is False
          and ot["stop_reason_inspected_before_reading_content"] is True
          and ot["verdict_inferred_from_empty_or_unexpected_response"] is False,
          f"refusal handling={ot['refusal_is_wrong_answer']}")
    check("E9E-08_refusal_rationale", bool(ot["refusal_rationale"]), "refusal rationale recorded")
    check("E9E-08_trux_pending",
          flow["degraded_and_recovery"]["ai_unavailable"]["evaluation_pending_writes_evidence"] is False,
          "TRUX-v0 really forbids evidence for evaluation_pending")
    check("E9E-08_asux_pending",
          session["degraded_and_recovery"]["ai_evaluator_unavailable"]["evidence_written"] is False
          and session["degraded_and_recovery"]["ai_evaluator_unavailable"]["auto_fail"] is False,
          "ASUX-v0 really forbids evidence and auto-fail for evaluation_pending")

    # --- 9. Failure and timeout -------------------------------------------
    ft = a["failure_and_timeout"]
    check("E9E-09_budget_end_to_end",
          ft["budget_is_end_to_end"] is True and ft["budget_covers_all_attempts"] is True
          and ft["per_call_timeout_is_user_facing_guarantee"] is False
          and ft["budget_is_product_default_not_scientific"] is True, f"budget={ft['budget_is_end_to_end']}")
    check("E9E-09_budget_rationale", "retry" in ft["rationale"], f"rationale={ft['rationale']}")
    check("E9E-09_retries_bounded",
          ft["retries_bounded"] is True and ft["retries_unthrottled"] is False
          and ft["silent_background_retry"] is False, "retries bounded and not silent")
    check("E9E-10_degradation",
          ft["degradation_uniform"] is True and ft["deterministic_capability_degrades"] is False
          and ft["learner_sees_truthful_pending_state"] is True
          and ft["fabricated_result_on_failure"] is False and ft["app_usable_with_ai_disabled"] is True,
          f"degradation={ft}")
    check("E9E-10_msbx_null_evaluator",
          bound["ai_absence"]["null_evaluator_ships_with_product"] is True
          and bound["ai_absence"]["deterministic_capability_degrades"] is False,
          "MSBX-v0 really ships a null evaluator with no degradation")

    # --- 11. Model selection ----------------------------------------------
    ms = a["model_selection"]
    check("E9E-11_provider_independent",
          ms["provider_independent_adapter"] is True and ms["router_present"] is True
          and ms["model_name_location"] == "configuration" and ms["model_name_in_core"] is False
          and ms["durable_semantic_depends_on_model_name"] is False, f"model_selection={ms['model_name_location']}")
    check("E9E-11_rules_require_independence",
          "provider-independent AI adapter/model router yaklaşımı hedeflenmektedir" in rules,
          "LEARNING_BEHAVIOR_RULES really requires a provider-independent adapter")
    check("E9E-11_rules_assign_to_9e",
          "9E — AI entegrasyon mimarisi** sırasında kesinleştirilecektir" in rules,
          "LEARNING_BEHAVIOR_RULES really assigns model selection to 9E")
    classes = {d["task_class"]: d for d in ms["defaults_by_task_class"]}
    check("E9E-12_task_classes",
          {"open_response_evaluation", "explanation_hint_feedback", "generated_item_drafting",
           "bulk_validation_of_generated_items"} <= set(classes),
          f"classes={sorted(classes)}")
    check("E9E-12_quality_for_evaluation",
          classes["open_response_evaluation"]["tier"] == "quality"
          and classes["explanation_hint_feedback"]["tier"] == "quality",
          "learner-facing evaluation uses the quality tier")
    check("E9E-12_all_have_rationale", all(bool(d.get("rationale")) for d in ms["defaults_by_task_class"]),
          "every default records a rationale")
    check("E9E-12_currency_deferred",
          ms["concrete_identifiers_asserted_as_permanent"] is False
          and ms["currency_reverification_required"] is True
          and ms["reference_recorded_with_build"] is True, f"currency={ms['currency_reverification_required']}")

    # --- 13. Call discipline ----------------------------------------------
    cd = a["call_discipline"]
    required_never_ai = {"deterministic_answer_key_checks", "prerequisite_and_graph_evaluation",
                         "planner_candidate_priority_and_capacity_logic",
                         "mastery_retention_and_readiness_computation",
                         "progress_and_history_lookups", "projection_rebuilds"}
    check("E9E-13_never_calls_ai", required_never_ai <= set(cd["never_calls_ai"]),
          f"missing={sorted(required_never_ai - set(cd['never_calls_ai']))}")
    check("E9E-13_cost_discipline",
          cd["bulk_non_latency_sensitive_uses_batch"] is True
          and cd["repeats_evaluation_for_unchanged_input"] is False
          and cd["cost_is_reason_to_weaken_evidence_rule"] is False, f"call_discipline={cd}")
    check("E9E-13_rules_require_local",
          "Basit/deterministik işler gereksiz AI API çağrısı yapmamalıdır" in rules,
          "LEARNING_BEHAVIOR_RULES really forbids AI calls for deterministic work")

    # --- 14. Credentials ---------------------------------------------------
    cr = a["credentials"]
    check("E9E-14_decided_here",
          cr["decided_here"] is True and cr["deferred_by"] == "LEARNING_BEHAVIOR_RULES_section_18",
          "credential decision settled here as deferred")
    check("E9E-14_no_shipped_key",
          cr["hardcoded_or_shared_key_in_apk"] is False and cr["backend_proxy_in_v1"] is False
          and bool(cr["backend_proxy_rationale"]), "no shipped key, no V1 backend proxy")
    check("E9E-14_user_key",
          cr["learner_supplies_own_key"] is True and cr["storage"] == "platform_secure_storage"
          and cr["stored_in_plain_preferences_or_files"] is False, f"storage={cr['storage']}")
    check("E9E-14_threat_model", bool(cr["threat_model"]), "threat model stated")
    hr = cr["handling_rules"]
    check("E9E-15_handling_rules",
          all(hr[k] is False for k in ["key_in_logs", "key_in_crash_reports", "key_in_exports",
                                       "key_in_backups", "key_in_diagnostics",
                                       "key_sent_anywhere_but_provider_endpoint",
                                       "removing_key_causes_data_loss"]),
          f"violations={[k for k, v in hr.items() if k != 'removing_key_returns_to_null_evaluator' and k != 'app_usable_without_key_ever_entered' and v is not False]}")
    check("E9E-15_no_key_no_loss",
          hr["removing_key_returns_to_null_evaluator"] is True
          and hr["app_usable_without_key_ever_entered"] is True, "no key means null evaluator, not data loss")
    check("E9E-15_rules_forbid_hardcode",
          "Mobil APK içine sabit/hardcoded gizli API key koymak varsayılan final tasarım değildir" in rules,
          "LEARNING_BEHAVIOR_RULES really rejects a hardcoded key as the default design")
    check("E9E-15_rules_assign_security_to_9e",
          "Final güvenlik/proxy/backend kararı 9E'de teknik olarak kesinleştirilecektir" in rules,
          "LEARNING_BEHAVIOR_RULES really assigns the security decision to 9E")
    check("E9E-15_v1_local_first", "V1 local-first'tür" in v1, "V1_SCOPE really states local-first")
    check("E9E-15_key_not_in_lfps_export",
          "preferences" in persist["backup_export_restore"]["export_includes"]
          and inv["key_included_in_export"] is False,
          "the LFPS export carries preferences but never the key")

    # --- 16. Privacy -------------------------------------------------------
    pv = a["privacy"]
    never_sent = set(pv["never_sent"])
    required_never_sent = {"evidence_history", "mastery_state", "plan", "profile",
                           "exposure_records", "provenance", "planner_traces"}
    check("E9E-16_never_sent", required_never_sent <= never_sent,
          f"missing={sorted(required_never_sent - never_sent)}")
    check("E9E-16_minimum_content",
          pv["may_send"] == "minimum_content_needed_to_evaluate_current_attempt"
          and pv["product_otherwise_never_leaves_device"] is True, f"may_send={pv['may_send']}")
    check("E9E-16_visible_and_optional",
          pv["ai_use_visible_to_learner"] is True and pv["ai_can_be_disabled"] is True
          and pv["with_ai_disabled_nothing_leaves_device"] is True, f"privacy={pv}")

    # --- 17. Generated resources ------------------------------------------
    gr = a["generated_resource_integration"]
    check("E9E-17_untrusted",
          gr["generated_item_enters_untrusted"] is True
          and gr["usable_for_high_stakes_before_validation"] is False
          and gr["generator_and_validator_separate"] is True
          and gr["generator_self_grade_is_validation"] is False
          and gr["uncalibrated_single_llm_score_sufficient_alone"] is False
          and gr["unvalidated_item_produces_strong_mastery_changing_evidence"] is False,
          f"generated resources={gr}")
    check("E9E-17_status_recorded",
          gr["validation_status_recorded_on_resource_version"] is True
          and any(e["id"] == "resource_validation_record" for e in dm["curriculum_entities"]),
          "DDM-v0 really has a resource_validation_record entity")

    # --- 18. Evaluator provenance -----------------------------------------
    ep = a["evaluator_provenance"]
    check("E9E-18_provenance",
          ep["recorded_on_every_ai_derived_evidence_row"] is True
          and set(ep["fields"]) == {"provider", "model", "prompt_or_schema_version"}
          and ep["maps_to_ddm_field"] == "evaluator", f"provenance={ep['fields']}")
    check("E9E-18_ddm_has_evaluator",
          "evaluator" in dm["evidence_event"]["fields"] and "evaluator_status" in dm["evidence_event"]["fields"],
          "DDM-v0 evidence_event really carries evaluator and evaluator_status")
    check("E9E-18_disposition_not_edit",
          ep["later_judgement_is"] == "appended_evidence_disposition"
          and ep["later_judgement_is_edit"] is False
          and persist["source_of_truth"]["invalid_evidence_marked_not_erased"] is True,
          "later judgement is an appended disposition, per LFPS-v0")

    # --- 19. Forbidden / boundaries / acceptance --------------------------
    forbidden = set(a["forbidden_ai_patterns"])
    expected_forbidden = {
        "ai_writing_mastery_retention_readiness_weakness_or_planner_state",
        "ai_satisfying_or_bypassing_prerequisite", "ai_setting_assessment_quota",
        "treating_uncalibrated_llm_evaluation_as_verified", "generator_validating_its_own_output",
        "unvalidated_ai_item_producing_strong_mastery_changing_evidence",
        "parsing_an_evaluation_verdict_out_of_free_text",
        "treating_schema_invalid_response_as_verdict", "treating_refusal_as_wrong_answer",
        "turning_timeout_or_transport_error_into_negative_evidence",
        "unbounded_or_unthrottled_retries_against_learner_key",
        "per_call_timeout_presented_as_end_to_end_guarantee", "calling_ai_for_deterministic_work",
        "hardcoded_or_shared_api_key_in_apk", "key_in_logs_exports_backups_or_diagnostics",
        "sending_evidence_history_mastery_plan_or_profile_to_provider",
        "binding_durable_learning_semantic_to_model_name",
        "asserting_model_currency_accuracy_latency_or_cost_without_source",
    }
    check("E9E-19_forbidden_patterns", expected_forbidden <= forbidden,
          f"missing={sorted(expected_forbidden - forbidden)}")
    check("E9E-19_forbidden_count", len(forbidden) == 18, f"count={len(forbidden)}")

    boundaries_map = {str(k): v for k, v in a["future_stage_boundaries"].items()}
    for stage in ["9F", "10A", "14", "18"]:
        check("E9E-20_boundary_open", stage in boundaries_map, f"stage={stage}")

    acc = a["acceptance"]
    required_models = {"MSBX-v0", "AIV-v0", "GRE-v0", "RVR-v0", "PRG-v0", "PBR-v0", "WLRM-v0",
                       "DDM-v0", "LFPS-v0", "TRUX-v0", "ASUX-v0"}
    check("E9E-20_required_models", required_models <= set(acc["required_previous_models"]),
          f"missing={sorted(required_models - set(acc['required_previous_models']))}")
    check("E9E-20_regressions_required",
          all(acc[k] is True for k in ["independent_qa_required", "stage6_regression_required",
                                       "stage7_regression_required", "stage8_regression_required",
                                       "stage9a_regression_required", "stage9b_regression_required",
                                       "stage9c_regression_required", "stage9d_regression_required"]),
          f"acceptance={acc}")

    # --- 21. Spec / research textual contract ------------------------------
    spec_markers = [
        "AIAX-v0 — AI Integration Architecture",
        "`D-079`",
        "AI is an assistant behind a port",
        "AI proposes. Deterministic engines decide.",
        "**A refusal is not a wrong answer.**",
        "The budget is end-to-end",
        "**No developer or shared API key ships in the APK.** Ever.",
        "9F — Test stratejisi",
    ]
    check("E9E-21_spec_contract", all(mk in spec for mk in spec_markers),
          f"missing={[mk for mk in spec_markers if mk not in spec]}")
    check("E9E-21_spec_no_claims",
          "No accuracy claim, latency guarantee, monthly cost estimate or availability promise is canonical in 9E." in spec,
          "spec refuses claims")
    check("E9E-21_spec_names_no_model",
          "claude-" not in spec.lower() and "gpt-" not in spec.lower(),
          "spec names no concrete model identifier as canonical")

    research_markers = [
        "A refusal is not a wrong answer",
        "Free-text parsing is a silent correctness hazard",
        "Timeout budgets are not what they look like",
        "The credential question has a real threat model",
        "AI is genuinely useful and must not be over-restricted",
    ]
    check("E9E-22_research_synthesis", all(mk in research for mk in research_markers),
          f"missing={[mk for mk in research_markers if mk not in research]}")

    return finish(args.write_report, a)


def finish(write_report: bool, a) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "9E",
        "model": "AIAX-v0",
        "candidate_decision": "D-079",
        "result": result,
        "status_observed": a.get("status") if isinstance(a, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "ai_may": len(a.get("ai_may", [])) if isinstance(a, dict) else None,
        "ai_may_never": len(a.get("ai_may_never", [])) if isinstance(a, dict) else None,
        "outcomes": len(a.get("outcome_taxonomy", {}).get("outcomes", [])) if isinstance(a, dict) else None,
        "forbidden_ai_patterns": len(a.get("forbidden_ai_patterns", [])) if isinstance(a, dict) else None,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"9E_AI_INTEGRATION_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"ai_may={report['ai_may']} ai_may_never={report['ai_may_never']} "
          f"outcomes={report['outcomes']} forbidden={report['forbidden_ai_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
