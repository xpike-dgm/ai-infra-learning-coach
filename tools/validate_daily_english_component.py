from __future__ import annotations

from pathlib import Path
import argparse
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
POLICY = ROOT / "curriculum" / "english" / "7c_daily_component" / "policy.yaml"
REPORT = ROOT / "curriculum" / "english" / "7c_daily_component" / "qa_report.yaml"
EED = ROOT / "curriculum" / "english" / "7a_entry_diagnostic" / "blueprint.yaml"
TECP = ROOT / "curriculum" / "english" / "7b_cefr_progression" / "alignment.yaml"
SPEC = ROOT / "docs" / "DAILY_ENGLISH_COMPONENT_SPEC.md"
RESEARCH = ROOT / "research" / "7c_daily_english_component_research.md"
PLANNER = ROOT / "docs" / "ADAPTIVE_PLANNER_SPEC.md"
PBR = ROOT / "docs" / "PRIORITY_POLICY_SPEC.md"
TASKS = ROOT / "docs" / "TASK_TAXONOMY_SPEC.md"


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def main(write_report: bool) -> int:
    policy = load(POLICY)
    eed = load(EED)
    tecp = load(TECP)
    spec_text = SPEC.read_text(encoding="utf-8")
    planner_text = PLANNER.read_text(encoding="utf-8")
    pbr_text = PBR.read_text(encoding="utf-8")
    task_text = TASKS.read_text(encoding="utf-8")

    checks: list[dict] = []
    failures: list[str] = []

    def check(name: str, condition: bool, details: str) -> None:
        checks.append({"check": name, "result": "PASS" if condition else "FAIL", "details": details})
        if not condition:
            failures.append(f"{name}: {details}")

    eed_ids = {row["skill_id"] for row in eed.get("claims", [])}
    tecp_ids = {row["skill_id"] for row in tecp.get("skill_alignments", [])}
    check("E7C-01_canonical_d01_scope", len(eed_ids) == 15 and eed_ids == tecp_ids, f"EED={len(eed_ids)} TECP={len(tecp_ids)}")
    check("E7C-01_expected_count_metadata", policy.get("scope", {}).get("expected_skill_count") == 15, "policy expected_skill_count=15")
    bilingual_id = policy.get("scaffold_boundary", {}).get("bilingual_scope_skill_id")
    check("E7C-01_policy_skill_refs_canonical", bilingual_id in eed_ids, f"bilingual scope Skill={bilingual_id}")

    planner = policy.get("planner_contract", {})
    check(
        "E7C-02_common_capacity_only",
        planner.get("common_daily_capacity_pool") is True
        and planner.get("separate_english_budget") is False
        and policy.get("capacity_behavior", {}).get("may_extend_hard_budget_for_english") is False
        and "daily_capacity_minutes" in planner_text,
        "English uses existing common hard budget",
    )
    check(
        "E7C-02_no_fixed_quota",
        planner.get("fixed_daily_english_minutes") is None
        and planner.get("fixed_daily_english_percentage") is None
        and policy.get("capacity_behavior", {}).get("may_reserve_fixed_english_share") is False,
        "no fixed English minutes/percentage/share",
    )
    check(
        "E7C-02_no_daily_completion_or_streak_gate",
        planner.get("mandatory_daily_completion") is False
        and planner.get("english_streak_gate") is False
        and planner.get("missed_english_is_failure") is False
        and planner.get("missed_english_is_debt") is False,
        "daily opportunity is not obligation/failure/debt",
    )

    daily = policy.get("daily_candidate_generation", {})
    check(
        "E7C-03_daily_candidate_invariant",
        daily.get("invariant") == "generate_at_least_one_candidate_when_open_eligible_safe_english_need_exists"
        and set(daily.get("preconditions", [])) == {
            "active_study_day",
            "open_technical_english_learning_need",
            "prerequisite_interpretable_or_ready",
            "safe_task_candidate_available",
        },
        "eligible/open/safe English need produces at least one candidate",
    )
    check(
        "E7C-03_candidate_not_evidence",
        daily.get("candidate_generated_is_selection") is False
        and daily.get("selection_is_attempt") is False
        and daily.get("attempt_is_mastery") is False,
        "candidate/selection/attempt/mastery remain distinct",
    )

    allowed_triggers = {
        "new_learning", "continue_learning", "weakness_detected", "remediation_required",
        "retention_review_due", "verification_due", "diagnostic_opportunity",
        "reinforcement_opportunity", "parallel_track_due",
    }
    declared_triggers = set(policy.get("learning_need_triggers", {}).get("allowed", []))
    check("E7C-04_existing_learning_need_vocabulary", declared_triggers == allowed_triggers, f"triggers={sorted(declared_triggers)}")
    check("E7C-04_task_taxonomy_anchor", all(trigger in task_text for trigger in allowed_triggers), "all 7C trigger kinds already exist in TASK_TAXONOMY_SPEC")
    check("E7C-04_parallel_default_P3", policy.get("parallel_track_due", {}).get("default_priority_band") == "P3_planned_progress" and "parallel_track_due" in pbr_text, "normal English parallel need remains P3")

    balance = policy.get("track_balance", {})
    check(
        "E7C-05_balance_reuses_pbr",
        balance.get("reuse_existing_pbr_track_balance_and_starvation") is True
        and balance.get("pressure_buckets") == ["none", "watch", "promote"]
        and all(token in pbr_text for token in ["track_balance_pressure", "starvation_pressure", "none", "watch", "promote"]),
        "PBR-v0 balance/starvation vocabulary reused",
    )
    check(
        "E7C-05_no_fixed_omission_threshold",
        balance.get("fixed_days_to_watch") is None and balance.get("fixed_days_to_promote") is None,
        "no invented N-day starvation threshold",
    )
    promote = balance.get("promote_behavior", {})
    check(
        "E7C-05_promotion_safety",
        promote.get("may_promote_parallel_need_from_P3_to_P2") is True
        and promote.get("may_overtake_P0_or_P1") is False
        and promote.get("may_make_ineligible_task_eligible") is False
        and promote.get("may_exceed_hard_budget") is False
        and promote.get("creates_mastery_or_evidence") is False,
        "balance promotion cannot bypass integrity, eligibility, capacity or evidence rules",
    )

    mix = policy.get("task_mix", {})
    check("E7C-06_state_driven_mix", mix.get("fixed_category_percentages") is None and mix.get("state_driven") is True, "task mix is state-driven")
    check(
        "E7C-06_new_learning_retrieval_checkpoint",
        "retrieval_or_application_checkpoint" in mix.get("new_learning", {}).get("preferred_sequence", [])
        and mix.get("new_learning", {}).get("fixed_checkpoint_count") is None,
        "new learning can intersperse retrieval without fixed item count",
    )
    retention = mix.get("retention", {})
    check(
        "E7C-07_rvr_owns_spacing",
        retention.get("scheduler_owner") == "RVR-v0"
        and retention.get("introduce_new_spacing_formula") is False
        and retention.get("pre_expose_answer_before_retrieval") is False,
        "7C does not invent spacing and protects retrieval validity",
    )
    remediation = mix.get("remediation_verification", {})
    check(
        "E7C-07_localized_remediation",
        remediation.get("localization") == "exact_skill_or_objective"
        and remediation.get("broad_english_reset") is False
        and remediation.get("same_failed_item_memorization_can_close") is False
        and remediation.get("fresh_variant_after_feedback") is True,
        "remediation remains localized and fresh-variant based",
    )
    production = mix.get("production", {})
    check(
        "E7C-08_production_prerequisite_guard",
        production.get("require_english_hard_prerequisites") is True
        and production.get("unknown_grammar_or_vocabulary_can_create_clean_target_failure") is False,
        "unknown English prerequisites cannot contaminate production failure",
    )
    b2 = mix.get("reinforcement_b2_plus", {})
    check(
        "E7C-08_b2plus_boundary",
        b2.get("source") == "TECP-v0"
        and b2.get("extension_only") is True
        and b2.get("synthetic_completion_state") is False
        and b2.get("may_silently_expand_skill_semantics") is False,
        "B2+ remains bounded TECP-v0 extension",
    )

    rotation = policy.get("activity_rotation", {})
    check(
        "E7C-09_evidence_rotation",
        rotation.get("fixed_round_robin") is False
        and rotation.get("repeat_exact_prompt_by_default") is False
        and rotation.get("prefer_independent_dependency_or_testlet_family") is True
        and rotation.get("prefer_fresh_context_for_retention_or_transfer") is True
        and rotation.get("model_exposed_production_is_H0_independent_evidence") is False,
        "rotation protects evidence independence without fixed round-robin",
    )

    feedback = policy.get("feedback", {})
    check(
        "E7C-10_feedback_evidence_safety",
        feedback.get("answer_revealing_feedback_before_assess_retain_diagnose_capture") is False
        and feedback.get("feedback_after_attempt_allowed") is True
        and feedback.get("feedback_assisted_revision_is_independent_mastery_evidence") is False
        and feedback.get("fresh_H0_attempt_required_for_independent_closure_when_applicable") is True,
        "feedback supports learning but cannot manufacture independent mastery evidence",
    )

    scaffold = policy.get("scaffold_boundary", {})
    check(
        "E7C-11_stage_boundary_scaffold",
        scaffold.get("english_track_scaffold_only_in_7c") is True
        and scaffold.get("reduction_basis") == "evidence_not_calendar"
        and scaffold.get("technical_task_bilingual_integration_owner_step") == "7D",
        "7C stays inside English-track scaffold scope; technical integration is 7D",
    )
    future = policy.get("future_stage_boundaries", {})
    check(
        "E7C-11_future_boundaries",
        all(key in future for key in ["7D", "7E", "15F", "15G", 18, 20]),
        f"future boundaries={list(future)}",
    )

    research = policy.get("research_guards", {})
    check(
        "E7C-12_research_guard_no_fake_precision",
        research.get("distributed_practice_supported") is True
        and research.get("retrieval_practice_supported") is True
        and research.get("universal_optimal_interval_claim") is False
        and research.get("universal_daily_minutes_claim") is False
        and research.get("universal_task_percentage_claim") is False,
        "research supports direction, not fake universal precision",
    )

    check("E7C-13_spec_exists", SPEC.is_file() and "DECP-v0" in spec_text and "D-065" in spec_text, str(SPEC.relative_to(ROOT)))
    check("E7C-13_research_exists", RESEARCH.is_file(), str(RESEARCH.relative_to(ROOT)))

    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "7C",
        "model": "DECP-v0",
        "candidate_decision": "D-065",
        "result": result,
        "checked_at": "2026-08-27",
        "status_observed": policy.get("status"),
        "counts": {
            "canonical_d01_skills": len(eed_ids),
            "tecp_aligned_skills": len(tecp_ids),
            "allowed_learning_need_triggers": len(declared_triggers),
            "selection_result_values": len(policy.get("trace_contract", {}).get("selection_result_vocabulary", [])),
        },
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

    print(f"7C_DAILY_ENGLISH_COMPONENT_QA={result}")
    print(f"skills={len(eed_ids)} triggers={len(declared_triggers)}")
    if failures:
        for failure in failures:
            print("-", failure)
        return 1
    return 0


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--write-report", action="store_true")
    args = parser.parse_args()
    sys.exit(main(args.write_report))
