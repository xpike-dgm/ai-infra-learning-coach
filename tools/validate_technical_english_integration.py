from __future__ import annotations

import argparse
from pathlib import Path
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
POLICY_PATH = ROOT / "curriculum/english/7d_technical_integration/policy.yaml"
REPORT_PATH = ROOT / "curriculum/english/7d_technical_integration/qa_report.yaml"
EED_PATH = ROOT / "curriculum/english/7a_entry_diagnostic/blueprint.yaml"
TECP_PATH = ROOT / "curriculum/english/7b_cefr_progression/alignment.yaml"
DECP_PATH = ROOT / "curriculum/english/7c_daily_component/policy.yaml"
SPEC_PATH = ROOT / "docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md"
RESEARCH_PATH = ROOT / "research/7d_technical_english_integration_research.md"


def load_yaml(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write-report", action="store_true")
    args = parser.parse_args()

    failures: list[str] = []
    checks: list[dict] = []

    def check(code: str, condition: bool, details: str) -> None:
        checks.append({"check": code, "result": "PASS" if condition else "FAIL", "details": details})
        if not condition:
            failures.append(f"{code}: {details}")

    for path in (POLICY_PATH, EED_PATH, TECP_PATH, DECP_PATH, SPEC_PATH, RESEARCH_PATH):
        check("E7D-00_required_file", path.is_file(), str(path.relative_to(ROOT)))
    if failures:
        policy = {}
        eed = {}
        tecp = {}
        decp = {}
    else:
        policy = load_yaml(POLICY_PATH)
        eed = load_yaml(EED_PATH)
        tecp = load_yaml(TECP_PATH)
        decp = load_yaml(DECP_PATH)

    if policy:
        check("E7D-01_identity", policy.get("model") == "TEIP-v0" and policy.get("stage_step") == "7D", f"model={policy.get('model')} step={policy.get('stage_step')}")
        check("E7D-01_status", policy.get("status") in {"candidate_7d", "accepted_7d"}, f"status={policy.get('status')}")
        decision = policy.get("decision") or policy.get("candidate_decision")
        check("E7D-01_decision", decision == "D-066", f"decision={decision}")

        eed_skills = {row["skill_id"] for row in eed.get("claims", [])}
        tecp_skills = {row["skill_id"] for row in tecp.get("skill_alignments", [])}
        policy_skills = set(policy.get("canonical_english_skill_ids", []))
        check("E7D-02_eed_scope", len(eed_skills) == 15, f"EED skills={len(eed_skills)}")
        check("E7D-02_tecp_scope", len(tecp_skills) == 15, f"TECP skills={len(tecp_skills)}")
        check("E7D-02_exact_skill_scope", policy_skills == eed_skills == tecp_skills, f"policy={len(policy_skills)} EED={len(eed_skills)} TECP={len(tecp_skills)}")
        check("E7D-02_expected_count", policy.get("scope", {}).get("expected_canonical_english_skill_count") == 15, "policy expected count=15")
        check("E7D-02_no_new_graph_entities", all(policy.get("scope", {}).get(k) == "forbidden" for k in ("new_skill_creation", "new_objective_creation", "new_prerequisite_edge_creation")), "7D is overlay only")
        check("E7D-02_decp_regression_anchor", decp.get("status") == "accepted_7c" and decp.get("decision") == "D-065", f"DECP status={decp.get('status')} decision={decp.get('decision')}")

        modes = {row["mode"]: row for row in policy.get("integration_modes", [])}
        expected_modes = {
            "technical_only_localized",
            "technical_with_english_exposure",
            "dual_target_integrated",
            "english_primary_technical_context",
        }
        check("E7D-03_exact_modes", set(modes) == expected_modes, f"modes={sorted(modes)}")
        tech_only = modes.get("technical_only_localized", {})
        check("E7D-03_technical_only_guard", tech_only.get("english_target_allowed") is False and tech_only.get("english_mastery_attribution") == "forbidden" and tech_only.get("english_hard_gate") == "forbidden", "technical-only cannot target/gate/attribute English")
        exposure = modes.get("technical_with_english_exposure", {})
        check("E7D-03_exposure_not_mastery", exposure.get("english_mastery_from_exposure_alone") == "forbidden" and exposure.get("construct_essential_english_must_be") == "ready_or_neutralized", "exposure does not auto-create English mastery")
        dual = modes.get("dual_target_integrated", {})
        check("E7D-03_dual_component_attribution", dual.get("technical_target_required") is True and dual.get("english_target_required") is True and dual.get("component_rubrics_required") is True and dual.get("overall_pass_broadcast") == "forbidden", "dual target must be component-attributed")
        eng_primary = modes.get("english_primary_technical_context", {})
        check("E7D-03_english_primary_contamination_guard", eng_primary.get("specialist_technical_reasoning_as_hidden_requirement") == "forbidden" and eng_primary.get("technical_context_must_be") == "ready_controlled_or_scaffolded", "technical context cannot contaminate English target")

        inv = policy.get("invariants", {})
        check("E7D-04_no_global_gate", inv.get("english_global_technical_hard_gate") is False and inv.get("cefr_band_as_technical_gate") is False, "English/CEFR cannot globally gate technical route")
        check("E7D-04_medium_not_construct", inv.get("prompt_language_equals_target_construct") is False and inv.get("resource_language_equals_target_construct") is False, "prompt/resource language != target construct")
        check("E7D-04_no_fake_precision", inv.get("fixed_turkish_english_ratio") is None and inv.get("fixed_scaffold_fading_days") is None and inv.get("fixed_integrated_task_quota") is None, "no fixed ratio/day/quota")
        check("E7D-04_fading_semantics", inv.get("scaffold_fading_driver") == "evidence_and_task_validity" and inv.get("scaffold_fading_reversible") is True, "fading is evidence/validity driven and reversible")

        check("E7D-05_language_load_roles", set(policy.get("language_load_roles", [])) == {"incidental", "supporting", "construct_relevant"}, f"roles={policy.get('language_load_roles')}")
        expected_instruction = {"turkish_primary", "bilingual_parallel", "english_with_targeted_gloss", "english_primary_with_non_target_support", "english_unscaffolded"}
        check("E7D-05_instruction_modes", set(policy.get("instruction_language_modes", [])) == expected_instruction, "five semantic instruction modes")
        scaffold = policy.get("scaffold_rules", {})
        check("E7D-05_technical_support", scaffold.get("technical_target", {}).get("support_can_neutralize_language_load") is True and scaffold.get("technical_target", {}).get("withhold_support_to_force_english") == "forbidden", "technical construct can receive language support")
        check("E7D-05_english_target_support_ceiling", scaffold.get("english_target", {}).get("may_reveal_target_english_before_independent_response") is False and scaffold.get("english_target", {}).get("answer_revealing_support_lowers_evidence_ceiling") is True, "English target cannot receive answer-revealing support as independent evidence")
        check("E7D-05_fading_not_calendar", scaffold.get("fading", {}).get("calendar_driven") is False and scaffold.get("fading", {}).get("fixed_percentage_driven") is False and scaffold.get("fading", {}).get("evidence_driven") is True, "fading has no calendar/percentage schedule")

        required_fields = {
            "integration_mode", "technical_target_skill_ids", "technical_target_objective_ids", "english_target_skill_ids", "english_target_objective_ids",
            "technical_required_skill_ids", "english_required_skill_ids", "language_load_role", "authentic_resource_refs", "instruction_language_mode",
            "scaffold_surfaces", "construct_essential_language_segments", "component_attribution", "assistance_ceiling_by_component", "contamination_reason_codes", "integration_policy_version",
        }
        contract = policy.get("task_integration_contract", {})
        check("E7D-06_task_contract", set(contract.get("required_semantic_fields", [])) == required_fields, f"fields={len(contract.get('required_semantic_fields', []))}")
        check("E7D-06_target_prereq_separation", contract.get("target_and_prerequisite_families_separate") is True and contract.get("technical_and_english_attribution_separate") is True, "target/prerequisite and technical/English attribution separated")

        expected_results = {"positive_eligible", "negative_eligible", "practice_only", "provisional", "invalid_prerequisite_contamination", "invalid_scaffold_leakage", "not_observed"}
        check("E7D-07_component_results", set(policy.get("component_result_values", [])) == expected_results, "component result vocabulary exact")
        expected_contam = {"language_access_contamination", "technical_context_contamination", "undeclared_english_prerequisite", "undeclared_technical_prerequisite", "scaffold_answer_leakage", "component_not_separately_observable", "overall_outcome_broadcast_forbidden"}
        check("E7D-07_contamination_codes", set(policy.get("contamination_reason_codes", [])) == expected_contam, "contamination vocabulary exact")
        contam = policy.get("contamination_policy", {})
        c1 = contam.get("unknown_english_causes_technical_failure", {})
        c2 = contam.get("unknown_specialist_technical_context_causes_english_failure", {})
        check("E7D-07_language_to_technical_guard", c1.get("technical_negative_result") == "invalid_prerequisite_contamination" and c1.get("lower_technical_mastery") is False, "English-caused technical failure cannot lower technical mastery")
        check("E7D-07_technical_to_english_guard", c2.get("english_negative_result") == "invalid_prerequisite_contamination" and c2.get("lower_english_mastery") is False, "technical-context-caused English failure cannot lower English mastery")
        check("E7D-07_positive_survival_conservative", contam.get("positive_component_survival", {}).get("allowed") is True and "separately_observable" in contam.get("positive_component_survival", {}).get("condition", ""), "positive component survives only when interpretable")

        natural = policy.get("natural_integration", {})
        check("E7D-08_multi_need_efficiency", natural.get("one_task_may_link_multiple_learning_needs") is True and natural.get("duration_counted_once") is True and natural.get("multiply_priority_for_multiple_tracks") is False, "one integrated task can serve multiple needs without priority inflation")
        check("E7D-08_no_completion_broadcast", natural.get("resolve_all_linked_needs_on_completion") is False and natural.get("component_evidence_separate") is True, "completion does not resolve all needs")
        strong_req = set(natural.get("english_strong_evidence_requires", []))
        check("E7D-08_strong_evidence_guard", {"explicit_english_target_objective", "structurally_essential_language_behavior", "separately_observable", "prerequisite_valid", "normal_GRE_PRG_QAB_requirements", "no_answer_revealing_scaffold"}.issubset(strong_req), "natural English strong evidence keeps normal gates")

        authentic = policy.get("authentic_resource_policy", {})
        check("E7D-09_authenticity_not_validity", authentic.get("authenticity_is_automatic_validity") is False and authentic.get("assessment_use_obeys_qab_aiv") is True, "authentic docs still obey validation")
        integrity = policy.get("translation_gloss_integrity", {})
        check("E7D-09_translation_integrity", integrity.get("semantic_change_invalidates_strong_evidence") is True and integrity.get("ai_generated_support_self_validating") is False and "negation" in integrity.get("preserve", []), "translation/gloss preserves technical semantics")

        fixtures = policy.get("safety_fixtures", [])
        ids = [row.get("id") for row in fixtures]
        expected_ids = [f"E7D-F{i:02d}" for i in range(1, 16)]
        check("E7D-10_fixture_count", len(fixtures) == 15, f"fixtures={len(fixtures)}")
        check("E7D-10_fixture_ids", ids == expected_ids, f"fixture ids={ids}")

        future = policy.get("future_stage_boundaries", {})
        check("E7D-11_7e_boundary", future.get("7E") == "learner_facing_english_mastery_and_cefr_profile_behavior", f"7E={future.get('7E')}")
        check("E7D-11_no_runtime_implementation", future.get("9C") == "physical_data_model" and future.get("12") == "planner_and_prerequisite_runtime_implementation", "7D is policy not runtime implementation")

        if SPEC_PATH.is_file():
            spec = SPEC_PATH.read_text(encoding="utf-8")
            check("E7D-12_spec_identity", "TEIP-v0" in spec and "D-066" in spec and "7D" in spec, "spec identity present")
            check("E7D-12_spec_global_gate_guard", "global hard gate" in spec and "dual_target_integrated" in spec and "language_access_contamination" in spec, "core safeguards documented")
        if RESEARCH_PATH.is_file():
            research = RESEARCH_PATH.read_text(encoding="utf-8")
            check("E7D-12_research_sources", "Council of Europe" in research and "ALTE" in research and "ETS" in research, "authoritative research provenance present")
            check("E7D-12_research_no_fake_ratio", "No universal Turkish/English ratio" in research, "research explicitly rejects universal ratio")

    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "7D",
        "model": "TEIP-v0",
        "candidate_decision": "D-066",
        "result": result,
        "checked_at": "2026-08-27",
        "status_observed": policy.get("status") if policy else None,
        "counts": {
            "canonical_english_skills": len(set(policy.get("canonical_english_skill_ids", []))) if policy else 0,
            "integration_modes": len(policy.get("integration_modes", [])) if policy else 0,
            "safety_fixtures": len(policy.get("safety_fixtures", [])) if policy else 0,
            "contamination_reason_codes": len(policy.get("contamination_reason_codes", [])) if policy else 0,
        },
        "checks": checks,
        "failures": failures,
    }

    if args.write_report:
        REPORT_PATH.parent.mkdir(parents=True, exist_ok=True)
        REPORT_PATH.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=160), encoding="utf-8")

    print(f"7D_TECHNICAL_ENGLISH_INTEGRATION_QA={result}")
    print(f"checks={len(checks)} failures={len(failures)}")
    for failure in failures:
        print("-", failure)
    return 0 if result == "PASS" else 1


if __name__ == "__main__":
    raise SystemExit(main())
