from __future__ import annotations

from pathlib import Path
import argparse
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
POLICY = ROOT / "curriculum/english/7e_mastery_profile/policy.yaml"
ALIGNMENT = ROOT / "curriculum/english/7b_cefr_progression/alignment.yaml"
ENTRY = ROOT / "curriculum/english/7a_entry_diagnostic/blueprint.yaml"
DAILY = ROOT / "curriculum/english/7c_daily_component/policy.yaml"
INTEGRATION = ROOT / "curriculum/english/7d_technical_integration/policy.yaml"
SPEC = ROOT / "docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md"
RESEARCH = ROOT / "research/7e_english_mastery_profile_research.md"
REPORT = ROOT / "curriculum/english/7e_mastery_profile/qa_report.yaml"


def load_yaml(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write-report", action="store_true")
    args = parser.parse_args()

    checks: list[dict] = []
    failures: list[str] = []

    def check(code: str, ok: bool, details: str):
        checks.append({"check": code, "result": "PASS" if ok else "FAIL", "details": details})
        if not ok:
            failures.append(f"{code}: {details}")

    for path in [POLICY, ALIGNMENT, ENTRY, DAILY, INTEGRATION, SPEC, RESEARCH]:
        check("E7E-00_required_file", path.exists(), str(path.relative_to(ROOT)))

    if failures:
        print("7E_TECHNICAL_ENGLISH_MASTERY_PROFILE_QA=FAIL")
        for f in failures:
            print("-", f)
        return 1

    policy = load_yaml(POLICY)
    alignment = load_yaml(ALIGNMENT)
    entry = load_yaml(ENTRY)
    daily = load_yaml(DAILY)
    integration = load_yaml(INTEGRATION)
    spec_text = SPEC.read_text(encoding="utf-8")
    research_text = RESEARCH.read_text(encoding="utf-8")

    check("E7E-01_identity", policy.get("model") == "TEPM-v0" and policy.get("stage_step") == "7E", f"model={policy.get('model')} step={policy.get('stage_step')}")
    check("E7E-01_status", policy.get("status") in {"candidate_7e", "accepted_7e"}, f"status={policy.get('status')}")
    check("E7E-01_decision", policy.get("candidate_decision") == "D-067", f"decision={policy.get('candidate_decision')}")

    alignment_ids = [x["skill_id"] for x in alignment["skill_alignments"]]
    entry_ids = [x["skill_id"] for x in entry["claims"]]
    check("E7E-02_alignment_scope", len(alignment_ids) == 15 and len(set(alignment_ids)) == 15, f"TECP skills={len(alignment_ids)}")
    check("E7E-02_entry_scope", set(entry_ids) == set(alignment_ids), f"EED={len(entry_ids)} TECP={len(alignment_ids)}")
    check("E7E-02_policy_scope_count", policy["scope"]["canonical_skill_count"] == 15, f"policy count={policy['scope']['canonical_skill_count']}")
    check("E7E-02_no_graph_mutation", all(policy["scope"][k] == "forbidden" for k in ["new_skill_creation", "new_objective_creation", "new_prerequisite_edge_creation", "new_mastery_formula"]), "7E is derived profile only")

    expected_states = {
        "not_yet_evidenced",
        "developing_with_support",
        "developing_independent",
        "confirmed_current",
        "confirmed_review_due",
        "confirmation_verification_due",
        "remediation_required",
        "prerequisite_unresolved",
    }
    states = set(policy["skill_presentation_states"])
    check("E7E-03_skill_state_vocabulary", states == expected_states, f"states={sorted(states)}")
    check("E7E-03_precedence_complete", set(policy["skill_presentation_precedence"]) == expected_states and len(policy["skill_presentation_precedence"]) == 8, "all 8 states have deterministic precedence")

    inv = policy["invariants"]
    check("E7E-04_mastery_ownership", inv["canonical_mastery_owner"] == "GRE-v0" and inv["canonical_retention_owner"] == "RVR-v0" and inv["canonical_prerequisite_owner"] == "PRG-v0" and inv["canonical_remediation_owner"] == "WLRM-v0", "existing canonical engines retained")
    check("E7E-04_profile_non_authoritative", inv["derived_profile_can_write_mastery"] is False, "derived profile cannot write mastery")
    check("E7E-04_no_general_cefr_overclaim", inv["general_english_level_claim"] == "forbidden" and inv["official_cefr_certification_claim"] == "forbidden", "general/official CEFR claims forbidden")
    check("E7E-04_no_numeric_score", inv["numeric_english_mastery_percentage"] is None and inv["numeric_cefr_average"] is None, "no numeric English/CEFR aggregate")
    check("E7E-04_no_b2_completion", inv["aggregate_b2_plus_completion"] == "forbidden", "no aggregate B2+ completion")
    check("E7E-04_retention_hysteresis", inv["review_due_is_unmastered"] is False and inv["verification_due_instantly_deletes_mastery"] is False, "RVR/GRE hysteresis preserved")
    check("E7E-04_assistance_guard", inv["assisted_success_is_independent_mastery"] is False and inv["provisional_evaluator_is_independent_mastery"] is False, "assisted/provisional is not independent mastery")
    check("E7E-04_contamination_guard", inv["contaminated_attempt_changes_clean_profile_state"] is False, "contaminated attempt cannot change clean profile")

    base = policy["base_profile"]
    check("E7E-05_base_bands", base["allowed_bands"] == ["A1", "A2", "B1"], f"bands={base['allowed_bands']}")
    expected_base_status = {"not_yet_complete", "complete_current", "complete_review_due", "complete_verification_due"}
    check("E7E-05_base_status", set(base["profile_status_values"]) == expected_base_status, f"statuses={base['profile_status_values']}")
    check("E7E-05_review_no_demotion", base["review_due_demotes_band"] is False and policy["review_semantics"]["review_due"]["preserve_base_band_completion"] is True, "review_due preserves band")
    check("E7E-05_verification_uncertainty", base["verification_due_adds_uncertainty_qualifier"] is True and policy["review_semantics"]["verification_due"]["instant_unmastery"] is False, "verification adds uncertainty without instant deletion")
    check("E7E-05_remediation_recompute", base["confirmed_remediation_can_reduce_current_complete_band"] is True and base["preserve_historical_highest_confirmed_band"] is True, "current recompute + history preservation")
    check("E7E-05_qualified_scope", base["qualification_scope"] == "technical_english_text_first", f"scope={base['qualification_scope']}")

    uneven = policy["uneven_profile"]
    check("E7E-06_uneven_first_class", uneven["first_class"] is True and uneven["exact_skill_detail_must_remain_traceable"] is True, "uneven exact-Skill profile preserved")
    check("E7E-06_no_compensation", uneven["compensatory_skill_average"] is False and uneven["numeric_cefr_average"] is False and uneven["broad_band_failure_broadcast"] is False, "no compensatory average or broad failure")

    b2 = policy["b2_plus_extension"]
    expected_b2 = set(alignment["b2_plus_contract"]["extension_skill_ids"])
    check("E7E-07_b2_exact_subset", set(b2["eligible_skill_ids"]) == expected_b2 and len(expected_b2) == 4, f"B2+ eligible={len(expected_b2)}")
    check("E7E-07_b2_no_completion", b2["aggregate_completion_state"] == "forbidden", "B2+ is per-capability evidence only")
    check("E7E-07_b2_strong_evidence", set(b2["strong_evidence_requires"]) >= {"explicit_canonical_skill_target", "H0_direct_verified_prerequisite_valid", "no_answer_revealing_scaffold"}, "B2+ strong evidence keeps normal gates")

    assistance = policy["assistance_presentation"]
    check("E7E-08_assisted_development_only", assistance["assisted_or_answer_revealed_success_can_show_development"] is True and assistance["assisted_or_answer_revealed_success_can_confirm_mastery"] is False, "assisted success may develop but not confirm")
    check("E7E-08_prior_mastery_not_punished", assistance["prior_confirmed_mastery_erased_by_later_assisted_practice"] is False, "later support does not erase prior clean mastery")
    check("E7E-08_no_fake_fading", assistance["fixed_support_fading_days"] is None and assistance["fixed_turkish_english_ratio"] is None, "no fixed scaffold schedule/ratio")

    effects = policy["technical_integration_effects"]
    expected_modes = {x["mode"] for x in integration["integration_modes"]}
    check("E7E-09_mode_regression", set(effects) == expected_modes, f"modes={sorted(effects)}")
    check("E7E-09_technical_only_no_english", effects["technical_only_localized"]["english_profile_update"] == "none", "technical-only cannot update English profile")
    check("E7E-09_exposure_not_mastery", effects["technical_with_english_exposure"]["strong_mastery_without_explicit_target"] == "forbidden", "exposure alone is not mastery")
    check("E7E-09_dual_separation", effects["dual_target_integrated"]["component_specific_evidence"] is True and effects["dual_target_integrated"]["global_task_pass_broadcast"] == "forbidden", "dual-target component attribution preserved")
    check("E7E-09_technical_contamination", effects["english_primary_technical_context"]["technical_ignorance_can_create_clean_english_negative"] is False, "technical ignorance cannot create clean English negative")

    fixtures = policy["safety_fixtures"]
    expected_fixture_ids = {f"E7E-F{i:02d}" for i in range(1, 19)}
    fixture_ids = {x["id"] for x in fixtures}
    check("E7E-10_fixture_count", len(fixtures) == 18, f"fixtures={len(fixtures)}")
    check("E7E-10_fixture_ids", fixture_ids == expected_fixture_ids, f"fixture ids={sorted(fixture_ids)}")

    check("E7E-11_7b_regression_anchor", alignment.get("status") == "accepted_7b" and alignment.get("decision") == "D-064", "TECP-v0 accepted")
    check("E7E-11_7c_regression_anchor", daily.get("status") == "accepted_7c" and daily.get("decision") == "D-065", "DECP-v0 accepted")
    check("E7E-11_7d_regression_anchor", integration.get("status") == "accepted_7d" and integration.get("decision") == "D-066", "TEIP-v0 accepted")

    check("E7E-12_spec_identity", "TEPM-v0 — Technical English Mastery Profile" in spec_text and "D-067" in spec_text, "spec identity present")
    check("E7E-12_spec_review_guard", "review_due != forgotten" in spec_text and "verification_due" in spec_text, "review/verification semantics documented")
    check("E7E-12_spec_b2_guard", "B2+ complete" in spec_text and "aggregate `B2+ complete`" in spec_text, "B2+ overclaim guard documented")
    check("E7E-12_spec_no_numeric", "english_percentage" in spec_text and "cefr_numeric_average" in spec_text, "numeric aggregate guards documented")

    research_required = ["Council of Europe", "ALTE", "ETS", "profile", "general-English", "B2+"]
    check("E7E-13_research_sources", all(x in research_text for x in research_required), "authoritative profile/validity research present")
    check("E7E-13_research_no_new_mastery", "derived learner-facing profile contract" in research_text and "not another mastery engine" in research_text, "research reconciled to existing mastery ownership")

    report = {
        "stage_step": "7E",
        "model": "TEPM-v0",
        "candidate_decision": "D-067",
        "result": "PASS" if not failures else "FAIL",
        "checked_at": "2026-08-27",
        "status_observed": policy.get("status"),
        "counts": {
            "canonical_english_skills": len(alignment_ids),
            "skill_presentation_states": len(states),
            "base_bands": len(base["allowed_bands"]),
            "b2_plus_extension_skills": len(expected_b2),
            "safety_fixtures": len(fixtures),
            "reason_codes": len(policy["reason_codes"]),
        },
        "checks": checks,
        "failures": failures,
    }

    if args.write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

    print(f"7E_TECHNICAL_ENGLISH_MASTERY_PROFILE_QA={'PASS' if not failures else 'FAIL'}")
    print(f"checks={len(checks)} failures={len(failures)}")
    if failures:
        for f in failures:
            print("-", f)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
