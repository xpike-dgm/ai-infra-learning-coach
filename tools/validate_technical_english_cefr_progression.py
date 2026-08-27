from __future__ import annotations

from pathlib import Path
import argparse
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ALIGNMENT = ROOT / "curriculum/english/7b_cefr_progression/alignment.yaml"
REPORT = ROOT / "curriculum/english/7b_cefr_progression/qa_report.yaml"
EED = ROOT / "curriculum/english/7a_entry_diagnostic/blueprint.yaml"
REVIEWS = ROOT / "curriculum/decomposition/6c_foundations/review_queue.yaml"
SPEC = ROOT / "docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md"
RESEARCH = ROOT / "research/7b_technical_english_cefr_research.md"


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def main(write_report: bool) -> int:
    alignment = load(ALIGNMENT)
    eed = load(EED)
    reviews = load(REVIEWS)
    spec_text = SPEC.read_text(encoding="utf-8") if SPEC.is_file() else ""

    checks: list[dict] = []
    failures: list[str] = []

    def check(name: str, condition: bool, details: str) -> None:
        checks.append({"check": name, "result": "PASS" if condition else "FAIL", "details": details})
        if not condition:
            failures.append(f"{name}: {details}")

    claims = eed.get("claims", [])
    canonical_ids = [row.get("skill_id") for row in claims]
    canonical_set = set(canonical_ids)

    rows = alignment.get("skill_alignments", [])
    aligned_ids = [row.get("skill_id") for row in rows]
    aligned_set = set(aligned_ids)
    row_by_id = {row.get("skill_id"): row for row in rows}

    check("E7B-01_exact_d01_scope", aligned_set == canonical_set and len(aligned_ids) == len(set(aligned_ids)) == 15, f"aligned={len(aligned_ids)} canonical={len(canonical_set)}")
    check("E7B-01_expected_count_metadata", alignment.get("scope", {}).get("expected_skill_count") == 15, "expected_skill_count=15")
    check("E7B-01_no_new_skill_identity", all(sid in canonical_set for sid in aligned_ids), "all aligned IDs originate from accepted EED/D01 claims")

    band_order = alignment.get("band_order", {})
    allowed_base_bands = {"A1", "A2", "B1"}
    anchors = {sid: row_by_id[sid].get("anchor_band") for sid in aligned_set}
    check("E7B-02_base_band_vocabulary", set(anchors.values()) <= allowed_base_bands and len(anchors) == 15, f"bands={sorted(set(anchors.values()))}")

    counts = {band: sum(1 for value in anchors.values() if value == band) for band in allowed_base_bands}
    check("E7B-02_balanced_anchor_distribution", counts == {"A1": 5, "A2": 5, "B1": 5}, f"counts={counts}")
    check("E7B-02_no_B2plus_base_reclassification", all(value != "B2+" for value in anchors.values()), "B2+ remains extension-only")

    canonical_hard_pairs = {
        (source, claim.get("skill_id"))
        for claim in claims
        for source in claim.get("hard_prerequisite_skill_ids", [])
    }
    band_violations = []
    for source, target in sorted(canonical_hard_pairs):
        if source not in anchors or target not in anchors:
            band_violations.append({"edge": [source, target], "reason": "missing_alignment"})
            continue
        if band_order.get(anchors[source], 999) > band_order.get(anchors[target], -1):
            band_violations.append({"edge": [source, target], "source_band": anchors[source], "target_band": anchors[target]})
    check("E7B-03_hard_prerequisite_band_monotonicity", not band_violations and len(canonical_hard_pairs) == 16, f"hard_edges={len(canonical_hard_pairs)} violations={band_violations}")

    controlled = set(alignment.get("controlled_cefr_scale_families", []))
    used_families = {family for row in rows for family in row.get("cefr_scale_families", [])}
    missing_family = [sid for sid, row in row_by_id.items() if not row.get("cefr_scale_families")]
    check("E7B-04_controlled_scale_families", bool(controlled) and used_families <= controlled and not missing_family, f"controlled={len(controlled)} used={len(used_families)} missing={missing_family}")

    b2 = alignment.get("b2_plus_contract", {})
    extension_ids = set(b2.get("extension_skill_ids", []))
    no_auto_ids = set(b2.get("explicitly_no_auto_extension_skill_ids", []))
    expected_extension = {
        "skill.english.documentation_navigation",
        "skill.english.read_definition_and_constraint",
        "skill.english.read_procedure_sequence",
        "skill.english.ask_clarifying_technical_question",
    }
    check("E7B-05_b2plus_subset_exact", extension_ids == expected_extension and extension_ids <= canonical_set, f"extension_ids={sorted(extension_ids)}")
    check("E7B-05_b2plus_no_auto_overlap", not (extension_ids & no_auto_ids), f"overlap={sorted(extension_ids & no_auto_ids)}")
    check("E7B-05_b2plus_flags_match", all(bool(row.get("b2_plus_extension_eligible")) == (sid in extension_ids) for sid, row in row_by_id.items()), "row eligibility flags match contract")
    check("E7B-05_b2plus_scope_present", all(row_by_id[sid].get("b2_plus_extension_scope") for sid in extension_ids), "all extension-eligible Skills have bounded scope")
    check("E7B-05_no_synthetic_b2_mastery", b2.get("synthetic_completion_state_in_7b") == "forbidden" and alignment.get("alignment_semantics", {}).get("b2_plus_is_new_mastery_state") is False, "B2+ is evidence-depth extension only")

    semantics = alignment.get("alignment_semantics", {})
    check("E7B-06_cefr_not_mastery", semantics.get("cefr_alignment_is_mastery_state") is False and semantics.get("base_band_summary_is_derived") is True, "CEFR alignment remains derived metadata")
    check("E7B-06_no_certification_overclaim", semantics.get("council_of_europe_certification_claim") is False and semantics.get("overall_general_english_cefr_claim") == "forbidden" and semantics.get("plain_level_label_without_technical_qualifier") == "forbidden", "general/certification overclaim guarded")
    check("E7B-06_no_numeric_cefr_formula", semantics.get("numeric_cefr_conversion_formula") is None, "no invented score-to-CEFR formula")
    check("E7B-06_no_technical_global_gate", semantics.get("english_to_technical_global_hard_gate") is False, "English remains parallel, not global technical gate")

    profile = alignment.get("profile_derivation", {})
    check("E7B-07_profile_source_is_skill_evidence", profile.get("canonical_source_of_truth") == "exact_skill_objective_evidence" and profile.get("single_general_english_level") is None and profile.get("broad_score") is None, "profile does not replace exact Skill evidence")
    check("E7B-07_uneven_profile_preserved", profile.get("uneven_profile_preserved") is True, "uneven CEFR-aligned Technical English profile allowed")
    check("E7B-07_b2plus_display_deferred", profile.get("b2_plus_completion_state_defined_in_7b") is False and b2.get("learner_facing_behavior_owner_step") == "7E", "B2+ learner-facing behavior deferred to 7E")

    bridge = alignment.get("pre_a1_bridge", {})
    check("E7B-08_preA1_bridge_not_gate", bridge.get("supported_as_context") is True and bridge.get("canonical_target_band") is False and bridge.get("technical_global_blocker") is False, "Pre-A1 is zero-entry scaffold context only")

    future = alignment.get("future_stage_boundaries", {})
    check("E7B-09_future_boundaries", all(key in future for key in ["7C", "7D", "7E", "15F", "15G", "18D", 20]), f"future keys={list(future)}")

    review = next((row for row in reviews if row.get("review_id") == "review.6c.english.cefr_alignment"), None)
    accepted = alignment.get("status") == "accepted_7b"
    if accepted:
        check("E7B-10_review_resolved_post", bool(review) and review.get("status") == "resolved" and review.get("resolution_owner_step") == "7B", "CEFR alignment review resolved by 7B")
        check(
            "E7B-10_eed_handoff_preserved_historical",
            eed.get("cefr_alignment_status") == "pending_7B"
            and eed.get("cefr_level") is None
            and eed.get("cefr_handoff", {}).get("owner_step") == "7B",
            "accepted EED-v0 keeps its original 7A handoff marker; current alignment truth lives in TECP-v0",
        )
    else:
        check("E7B-10_review_open_pre", bool(review) and review.get("status") == "open" and review.get("resolution_owner_step") == "7B", "review remains open until 7B POST finalization")
        check("E7B-10_eed_handoff_pending_pre", eed.get("cefr_alignment_status") == "pending_7B" and eed.get("cefr_level") is None, "EED handoff remains pending until accepted 7B")

    check("E7B-11_research_exists", RESEARCH.is_file(), str(RESEARCH.relative_to(ROOT)))
    check("E7B-11_spec_exists", SPEC.is_file() and "TECP-v0" in spec_text and "D-064" in spec_text, str(SPEC.relative_to(ROOT)))

    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "7B",
        "model": "TECP-v0",
        "candidate_decision": "D-064",
        "result": result,
        "checked_at": "2026-08-27",
        "status_observed": alignment.get("status"),
        "counts": {
            "canonical_d01_skills": len(canonical_set),
            "aligned_skills": len(aligned_set),
            "canonical_english_hard_edges": len(canonical_hard_pairs),
            "A1_anchor_skills": counts.get("A1", 0),
            "A2_anchor_skills": counts.get("A2", 0),
            "B1_anchor_skills": counts.get("B1", 0),
            "B2_plus_extension_skills": len(extension_ids),
            "cefr_scale_families_used": len(used_families),
        },
        "checks": checks,
        "failures": failures,
        "review": {
            "review_id": "review.6c.english.cefr_alignment",
            "expected_status": "resolved" if accepted else "open",
            "owner_step": "7B",
        },
    }

    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

    print(f"7B_TECHNICAL_ENGLISH_CEFR_QA={result}")
    print(
        f"skills={len(aligned_set)} hard_edges={len(canonical_hard_pairs)} "
        f"anchors=A1:{counts.get('A1', 0)},A2:{counts.get('A2', 0)},B1:{counts.get('B1', 0)} "
        f"b2_extensions={len(extension_ids)}"
    )
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
