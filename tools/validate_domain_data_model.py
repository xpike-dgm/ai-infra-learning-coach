from __future__ import annotations

import argparse
import re
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
DM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
SPEC = ROOT / "docs/DOMAIN_DATA_MODEL_SPEC.md"
RESEARCH = ROOT / "research/9c_domain_data_model_research.md"
PERSIST = ROOT / "arch/9b_local_first_persistence/persistence.yaml"
TECH = ROOT / "arch/9a_mobile_technology/technology.yaml"
PROGRESS = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
FLOW = ROOT / "ux/8c_daily_working_flow/flow.yaml"
SESSION = ROOT / "ux/8d_assessment_session/session.yaml"
GRE = ROOT / "docs/MASTERY_SIGNALS_SPEC.md"
KGC = ROOT / "docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md"
GNS = ROOT / "docs/GRANULARITY_NAMING_STANDARD.md"
REPORT = ROOT / "arch/9c_domain_data_model/qa_report.yaml"

checks: list[dict] = []
failures: list[str] = []

ID_RE = re.compile(r"^[a-z0-9]+(?:_[a-z0-9]+)*$")


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

    for path in [DM, SPEC, RESEARCH, PERSIST, TECH, PROGRESS, FLOW, SESSION, GRE, KGC, GNS]:
        check("E9C-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None)

    m = load_yaml(DM)
    persist = load_yaml(PERSIST)
    tech = load_yaml(TECH)
    progress = load_yaml(PROGRESS)
    flow = load_yaml(FLOW)
    session = load_yaml(SESSION)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")
    gre = GRE.read_text(encoding="utf-8")
    kgc = KGC.read_text(encoding="utf-8")

    # --- 1. Identity -------------------------------------------------------
    check("E9C-01_identity", m.get("model") == "DDM-v0" and m.get("stage_step") == "9C",
          f"model={m.get('model')} step={m.get('stage_step')}")
    check("E9C-01_status", m.get("status") in {"candidate_9c", "accepted_9c"}, f"status={m.get('status')}")
    check("E9C-01_decision", (m.get("candidate_decision") or m.get("decision")) == "D-077",
          f"decision={m.get('candidate_decision') or m.get('decision')}")
    check("E9C-01_lineage", m.get("persistence") == "LFPS-v0" and m.get("platform") == "AMTS-v0",
          f"persistence={m.get('persistence')} platform={m.get('platform')}")

    # --- 2. Scope ----------------------------------------------------------
    scope = m["scope"]
    check("E9C-02_model_only", scope["data_model_only"] is True, "9C is the data model only")
    deferred = ["orm_library_locked", "migration_scripts_locked", "service_boundaries_locked",
                "ai_integration_locked", "test_strategy_locked", "encryption_at_rest_locked"]
    check("E9C-02_deferred_open", all(scope[k] is False for k in deferred),
          f"violations={[k for k in deferred if scope[k] is not False]}")
    check("E9C-02_upstream_untouched",
          scope["stage8_semantics_changed"] is False and scope["persistence_rules_changed"] is False,
          "AŞAMA 8 semantics and LFPS rules untouched")
    nulls = ["row_count_budget", "query_latency_target", "storage_size_estimate", "index_implementation"]
    check("E9C-02_no_invented_numbers", all(scope[k] is None for k in nulls),
          f"violations={[k for k in nulls if scope[k] is not None]}")

    # --- 3. Invariants -----------------------------------------------------
    inv = m["invariants"]
    expected_false = [
        "truth_table_has_update_path", "truth_table_has_delete_path", "soft_delete_flag_on_truth_row",
        "curriculum_row_overwritten_when_published", "resource_version_overwritten",
        "reference_without_version_allowed", "curriculum_to_user_foreign_key",
        "outcome_and_evaluator_status_collapsed", "outcome_and_independence_collapsed",
        "contested_collapsed_into_outcome", "presentation_state_replaces_axes_in_storage",
        "timestamp_stores_instant_only", "timestamp_stores_local_date_only",
        "projection_without_provenance", "exposure_row_deleted",
        "exposure_lookup_expensive_at_selection_time", "polymorphic_catch_all_table",
        "orm_annotation_in_model", "dialect_specific_ddl_in_model",
        "platform_type_in_core_visible_entity", "row_count_or_latency_asserted",
        "boundaries_ai_tests_or_encryption_decided_here",
    ]
    check("E9C-03_false_guards", all(inv[k] is False for k in expected_false),
          f"violations={[k for k in expected_false if inv.get(k) is not False]}")
    expected_true = ["schema_enforces_architecture", "correction_is_appended_disposition",
                     "versioned_identity_is_composite", "pinning_is_structural",
                     "projections_droppable_and_rebuildable", "truth_tables_carry_monotonic_sequence"]
    check("E9C-03_true_guards", all(inv[k] is True for k in expected_true),
          f"violations={[k for k in expected_true if inv.get(k) is not True]}")

    # --- 4. Store regions cross-checked against LFPS ----------------------
    regions = {r["id"]: r for r in m["store_regions"]}
    check("E9C-04_regions", set(regions) == {"curriculum_store", "user_truth_store", "user_projection_store"},
          f"regions={sorted(regions)}")
    check("E9C-04_mutability",
          regions["curriculum_store"]["mutability"] == "immutable_once_published"
          and regions["user_truth_store"]["mutability"] == "append_only"
          and regions["user_projection_store"]["mutability"] == "droppable_and_rebuildable",
          "store mutability matches LFPS-v0")
    check("E9C-04_no_cross_fk",
          m["curriculum_to_user_foreign_key"] is False and m["projection_rebuild_touches_truth_tables"] is False,
          "no curriculum→user FK; rebuild does not touch truth")
    check("E9C-04_lfps_append_only",
          persist["source_of_truth"]["append_only"] is True and persist["invariants"]["truth_record_deleted"] is False,
          "LFPS-v0 really requires append-only truth")

    # --- 5. Truth entity coverage against LFPS ----------------------------
    lfps_truth = set(persist["source_of_truth"]["truth_records"])
    mapped = {e["maps_to_lfps"] for e in m["truth_entities"] if "maps_to_lfps" in e}
    check("E9C-05_truth_coverage", lfps_truth <= mapped,
          f"unmapped LFPS truth records={sorted(lfps_truth - mapped)}")
    truth_ids = {e["id"] for e in m["truth_entities"]}
    check("E9C-05_disposition_entity", "evidence_disposition" in truth_ids,
          "evidence_disposition entity exists")
    lfps_derived = set(persist["source_of_truth"]["derived_projections"])
    proj_ids = {e["id"] for e in m["projection_entities"]}
    check("E9C-06_projection_coverage", len(proj_ids) >= len(lfps_derived),
          f"projections={len(proj_ids)} vs LFPS derived={len(lfps_derived)}")
    check("E9C-06_no_truth_projection_overlap", not (truth_ids & proj_ids),
          f"overlap={sorted(truth_ids & proj_ids)}")

    # --- 7. Identity and pinning ------------------------------------------
    ident = m["identity"]
    check("E9C-07_id_contract", ident["id_format_contract"] == "GNS-v0"
          and ident["id_case"] == "lowercase_ascii", f"id={ident['id_format_contract']}")
    check("E9C-07_id_independence",
          set(ident["id_independent_of"]) == {"locale", "ordering", "version"},
          f"independent_of={ident['id_independent_of']}")
    check("E9C-07_id_forbidden",
          {"week", "stage", "release", "band", "difficulty", "role"} <= set(ident["forbidden_inside_id"]),
          f"forbidden={ident['forbidden_inside_id']}")
    check("E9C-07_gns_real", "week/stage/release/band/difficulty/role bilgisinin logical ID'ye gömülmesini yasaklar" in
          (ROOT / "docs/HANDOFF_STATE.md").read_text(encoding="utf-8"),
          "GNS-v0 really forbids those inside logical IDs")
    check("E9C-08_composite_key", ident["versioned_entity_primary_key"] == ["logical_id", "version"],
          f"pk={ident['versioned_entity_primary_key']}")
    check("E9C-08_pinning",
          ident["user_reference_carries_version"] is True
          and ident["reference_by_logical_id_alone"] is False
          and bool(ident["pinning_rationale"]), "pinning is structural with recorded rationale")
    check("E9C-08_lfps_pinning_real",
          persist["curriculum_and_user_state"]["user_records_pin_curriculum_version"] is True,
          "LFPS-v0 really requires version pinning")
    check("E9C-08_kgc_immutability", "Published graph state sessizce değişmez" in kgc,
          "KGC-v0 forbids silent published-state change")

    # --- 9. Curriculum entities -------------------------------------------
    cur = {e["id"]: e for e in m["curriculum_entities"]}
    required_cur = {"curriculum_version", "domain", "module", "topic", "skill", "objective",
                    "topic_skill_link", "skill_prerequisite_edge", "assessment_resource",
                    "assessment_resource_version", "resource_validation_record"}
    check("E9C-09_curriculum_entities", required_cur <= set(cur),
          f"missing={sorted(required_cur - set(cur))}")
    for eid in ["domain", "module", "topic", "skill", "objective", "assessment_resource_version"]:
        check("E9C-09_versioned_key", cur[eid]["key"] == ["logical_id", "version"],
              f"{eid}.key={cur[eid]['key']}")
    kgc_skill_fields = {"canonical_name", "capability_statement", "lifecycle_status", "capability_kind",
                        "retention_profile", "critical_prerequisite", "remediation_tags",
                        "professional_capability_tags", "project_capability_tags", "source_refs",
                        "provenance", "aliases"}
    check("E9C-10_skill_fields_cover_kgc", kgc_skill_fields <= set(cur["skill"]["fields"]),
          f"missing={sorted(kgc_skill_fields - set(cur['skill']['fields']))}")
    check("E9C-10_objective_pins_parent",
          {"parent_skill_logical_id", "parent_skill_version"} <= set(cur["objective"]["fields"]),
          f"objective fields={cur['objective']['fields']}")
    check("E9C-10_link_key_has_versions",
          cur["topic_skill_link"]["key"].count("topic_version") == 1
          and cur["topic_skill_link"]["key"].count("skill_version") == 1,
          f"link key={cur['topic_skill_link']['key']}")
    check("E9C-10_edge_fields", {"edge_kind", "reason_kind"} <= set(cur["skill_prerequisite_edge"]["fields"]),
          f"edge fields={cur['skill_prerequisite_edge']['fields']}")

    # --- 11. EvidenceEvent covers the GRE contract ------------------------
    ee = m["evidence_event"]
    check("E9C-11_covers_gre", ee["covers_contract"] == "GRE-v0", f"covers={ee['covers_contract']}")
    check("E9C-11_gre_hands_to_9c", "9C için davranış sözleşmesidir" in gre,
          "GRE-v0 really hands the EvidenceEvent contract to 9C")
    gre_required = {"evidence_type", "variant_family_id", "outcome", "difficulty",
                    "assistance_context", "duration_ms", "prerequisite_snapshot",
                    "delay_since_last_exposure_ms", "evaluator", "provenance", "misconception_tags"}
    check("E9C-11_gre_fields", gre_required <= set(ee["fields"]),
          f"missing={sorted(gre_required - set(ee['fields']))}")
    check("E9C-11_pins_versions",
          {"skill_logical_id", "skill_version", "resource_logical_id", "resource_version"} <= set(ee["fields"]),
          "evidence pins skill and resource versions")

    # --- 12. Four independent axes ----------------------------------------
    axes = ee["independent_axes"]
    check("E9C-12_axis_count", len(axes) == 4, f"axes={sorted(axes)}")
    check("E9C-12_outcome_values", set(axes["outcome"]) == {"positive", "negative", "partial", "invalid"},
          f"outcome={axes['outcome']}")
    check("E9C-12_evaluator_values", set(axes["evaluator_status"]) == {"verified", "provisional", "invalid"},
          f"evaluator={axes['evaluator_status']}")
    check("E9C-12_independence_values",
          set(axes["independence_class"]) == {"independent", "assisted", "practice_only",
                                              "requires_independent_recheck"},
          f"independence={axes['independence_class']}")
    check("E9C-12_axes_are_columns",
          {"outcome", "evaluator_status", "independence_class", "contested"} <= set(ee["fields"]),
          "all four axes are fields")
    check("E9C-12_not_collapsed", ee["axes_collapsed"] is False and bool(ee["collapse_rationale"]),
          "axes not collapsed, rationale recorded")
    check("E9C-12_evaluator_matches_asux",
          set(session["evaluator_status"]["values"]) == set(axes["evaluator_status"]),
          f"9C={sorted(axes['evaluator_status'])} vs ASUX={sorted(session['evaluator_status']['values'])}")

    # --- 13. Disposition ---------------------------------------------------
    disp = m["evidence_disposition"]
    check("E9C-13_appended", disp["appended_not_updated"] is True, "disposition is appended")
    check("E9C-13_values",
          set(disp["disposition_values"]) == {"invalidated", "contested", "superseded", "reinstated"},
          f"values={disp['disposition_values']}")
    check("E9C-13_decided_by",
          set(disp["decided_by_values"]) == {"deterministic_rule", "validator", "user_report"},
          f"decided_by={disp['decided_by_values']}")
    check("E9C-13_rule_not_absence", disp["recomputation_excludes_by_rule_not_by_absence"] is True,
          "recomputation excludes by rule")
    check("E9C-13_lfps_mark_not_erase",
          persist["source_of_truth"]["invalid_evidence_marked_not_erased"] is True,
          "LFPS-v0 requires marking rather than erasing")

    # --- 14. Assistance and provenance cross-checked against TRUX ---------
    ae = m["assistance_event"]
    trux_a = flow["assistance_choreography"]
    check("E9C-14_assistance_fields",
          {"level", "timing", "target_scope", "source", "requested_by_user"} <= set(ae["fields"]),
          f"fields={ae['fields']}")
    check("E9C-14_levels_match_trux", ae["level_values"] == trux_a["assistance_levels"],
          f"9C={ae['level_values']} vs TRUX={trux_a['assistance_levels']}")
    check("E9C-14_timings_match_trux", set(ae["timing_values"]) == set(trux_a["assistance_timings"]),
          f"9C={sorted(ae['timing_values'])} vs TRUX={sorted(trux_a['assistance_timings'])}")
    check("E9C-14_scopes_match_trux", set(ae["target_scope_values"]) == set(trux_a["target_scopes"]),
          f"9C={sorted(ae['target_scope_values'])} vs TRUX={sorted(trux_a['target_scopes'])}")
    ap = m["artifact_provenance"]
    trux_p = flow["artifact_provenance"]
    check("E9C-15_provenance_asked", ap["asked_not_inferred"] is True and trux_p["asked_not_inferred"] is True,
          "provenance asked, not inferred, in both specs")
    check("E9C-15_origin_values_match_trux", set(ap["origin_values"]) == set(trux_p["origin_values"]),
          f"9C={sorted(ap['origin_values'])} vs TRUX={sorted(trux_p['origin_values'])}")

    # --- 16. Exposure ------------------------------------------------------
    ex = m["exposure_record"]
    lfps_kinds = set(persist["exposure_records"]["retained_kinds"])
    check("E9C-16_exposure_kinds", set(ex["exposure_kinds"]) == lfps_kinds,
          f"9C={sorted(ex['exposure_kinds'])} vs LFPS={sorted(lfps_kinds)}")
    check("E9C-16_never_removed",
          ex["deleted"] is False and ex["archived"] is False
          and ex["exported_with_truth_records"] is True and ex["migrated_with_truth_records"] is True,
          f"exposure lifecycle={ex['deleted']}/{ex['archived']}")
    check("E9C-16_indexed",
          ex["indexed_for_selection_lookup"] is True
          and {"resource_logical_id", "variant_family_id", "exposure_kind"} <= set(ex["indexed_by"]),
          f"indexed_by={ex['indexed_by']}")
    check("E9C-16_pins_version", {"resource_logical_id", "resource_version"} <= set(ex["fields"]),
          "exposure pins the resource version")
    check("E9C-16_lfps_data_loss",
          persist["exposure_records"]["loss_classified_as"] == "data_loss",
          "LFPS-v0 classifies exposure loss as data loss")

    # --- 17. Time ----------------------------------------------------------
    tr = m["time_representation"]
    check("E9C-17_three_fields",
          set(tr["fields_on_every_timestamped_row"]) ==
          {"occurred_at_instant", "occurred_on_study_day", "utc_offset_minutes"},
          f"fields={tr['fields_on_every_timestamped_row']}")
    check("E9C-17_not_one_sided",
          tr["instant_only"] is False and tr["local_date_only"] is False, "neither representation alone")
    check("E9C-17_purposes",
          bool(tr["instant_purpose"]) and bool(tr["study_day_purpose"]) and bool(tr["offset_purpose"])
          and bool(tr["rationale"]), "each value has a recorded purpose")
    check("E9C-17_write_time", tr["recorded_at_write_time"] is True, "recorded at write time")
    for entity_key in ["evidence_event", "exposure_record"]:
        fields = set(m[entity_key]["fields"])
        check("E9C-17_applied_to_entity",
              {"occurred_at_instant", "occurred_on_study_day"} <= fields, f"{entity_key} timestamp fields")

    # --- 18. Projections ---------------------------------------------------
    skill_state = next(e for e in m["projection_entities"] if e["id"] == "skill_state")
    spwx_axes = {"mastery_axis_state", "retention_axis_state", "prerequisite_axis_state", "weakness_axis_state"}
    check("E9C-18_axes_stored_separately",
          skill_state["stores_axes_separately"] is True and set(skill_state["axes"]) == spwx_axes
          and skill_state["presentation_state_replaces_axes"] is False,
          f"skill_state={skill_state}")
    check("E9C-18_axes_match_spwx",
          set(progress["multi_axis_presentation"]["projection_fields"]) >= spwx_axes,
          "SPWX-v0 really declares these four axes")
    pp = m["projection_provenance"]
    check("E9C-19_projection_provenance",
          pp["required_on_every_projection_row"] is True
          and {"policy_version", "truth_watermark", "built_at_instant", "input_curriculum_version"} <= set(pp["fields"])
          and pp["enables_state"] == "recomputing_projection" and bool(pp["rationale"]),
          f"provenance={pp['fields']}")
    check("E9C-19_state_is_accepted", "recomputing_projection" in progress["semantic_states"],
          "recomputing_projection is an accepted SPWX-v0 state")

    # --- 20. Physical schema -----------------------------------------------
    ps = m["physical_schema"]
    check("E9C-20_library_neutral",
          ps["library_neutral"] is True and ps["orm_annotations"] is False
          and ps["dialect_specific_ddl"] is False, "schema is library-neutral")
    check("E9C-20_table_shape",
          ps["one_table_per_entity"] is True and ps["polymorphic_catch_all_table"] is False
          and ps["composite_primary_keys_on_versioned_entities"] is True
          and ps["foreign_keys_carry_version_column"] is True, "table and key shape")
    check("E9C-20_enums",
          ps["enum_like_fields_are_constrained_strings"] is True
          and ps["enum_allowed_sets_documented"] is True, "enum-like fields constrained and documented")
    check("E9C-21_watermark",
          ps["truth_tables_carry_monotonic_sequence"] is True
          and ps["sequence_used_as_projection_watermark"] is True
          and ps["projection_tables_droppable_without_touching_truth"] is True,
          "monotonic sequence drives the watermark")
    check("E9C-21_metadata", {"schema_version", "policy_version"} <= set(ps["metadata_table_stores"]),
          f"metadata={ps['metadata_table_stores']}")
    required_index_intent = {"evidence_by_skill", "evidence_by_objective", "evidence_by_time_range",
                             "exposure_by_resource_logical_id", "exposure_by_variant_family",
                             "exposure_by_exposure_kind", "dispositions_by_evidence_event"}
    check("E9C-21_index_intent", required_index_intent <= set(ps["index_intent"]),
          f"missing={sorted(required_index_intent - set(ps['index_intent']))}")
    check("E9C-21_index_tuning_deferred", "18E" in str(ps["concrete_index_definitions_owner"]),
          f"index owner={ps['concrete_index_definitions_owner']}")

    # --- 22. Core-visible types cross-checked against AMTS ----------------
    cv = m["core_visible_types"]
    check("E9C-22_core_types",
          cv["identifier"] == "string" and cv["instant"] == "epoch_value"
          and cv["study_day"] == "iso_date_string" and cv["enum_like"] == "constrained_string",
          f"core types={cv}")
    check("E9C-22_no_platform_type",
          cv["platform_type_present"] is False
          and {"sqlite_type", "android_type", "orm_type", "filesystem_type"} <= set(cv["forbidden_types"]),
          f"forbidden={cv['forbidden_types']}")
    check("E9C-22_amts_core_purity",
          tech["invariants"]["core_signature_exposes_storage_type"] is False
          if "core_signature_exposes_storage_type" in tech["invariants"]
          else persist["dependency_direction"]["storage_type_in_core_signature"] is False,
          "core purity really required upstream")

    # --- 23. History growth ------------------------------------------------
    hg = m["history_growth"]
    check("E9C-23_growth",
          hg["truth_tables_grow_without_pruning"] is True
          and hg["writes_are_append_only_so_cost_is_independent_of_history_size"] is True
          and hg["read_cost_controlled_by_deleting_history"] is False
          and hg["projection_tables_keyed_by_entity_not_by_event"] is True, f"growth={hg}")
    check("E9C-23_lfps_no_pruning", persist["history_growth"]["evidence_pruned_in_v1"] is False,
          "LFPS-v0 forbids pruning in V1")

    # --- 24. Forbidden / boundaries / acceptance --------------------------
    forbidden = set(m["forbidden_model_patterns"])
    expected_forbidden = {
        "mutable_outcome_column_without_disposition_table",
        "update_or_delete_path_on_truth_table",
        "soft_delete_flag_instead_of_disposition_record",
        "curriculum_reference_without_version_column",
        "foreign_key_from_curriculum_store_into_user_store",
        "collapsing_outcome_evaluator_independence_or_contested",
        "timestamp_with_instant_only_or_local_date_only",
        "projection_row_without_policy_version_and_watermark",
        "deleting_or_archiving_exposure_rows",
        "exposure_design_making_selection_lookup_expensive",
        "polymorphic_catch_all_table",
        "orm_annotations_or_dialect_specific_ddl_here",
        "platform_type_in_core_visible_entity",
        "overwriting_published_curriculum_or_resource_version",
        "deciding_boundaries_ai_tests_or_encryption_here",
    }
    check("E9C-24_forbidden_patterns", expected_forbidden <= forbidden,
          f"missing={sorted(expected_forbidden - forbidden)}")
    check("E9C-24_forbidden_count", len(forbidden) == 15, f"count={len(forbidden)}")

    boundaries = {str(k): v for k, v in m["future_stage_boundaries"].items()}
    for stage in ["9D", "9E", "9F", "10A", "12", "18", "19"]:
        check("E9C-25_boundary_open", stage in boundaries, f"stage={stage}")
    check("E9C-25_9d_owns_boundaries", boundaries["9D"] == "module_and_service_boundaries",
          f"9D={boundaries['9D']}")

    acc = m["acceptance"]
    required_models = {"LFPS-v0", "AMTS-v0", "KGC-v0", "GNS-v0", "QAB-v0", "AIV-v0", "GRE-v0", "RVR-v0",
                       "PRG-v0", "TSM-v0", "WLRM-v0", "TEPM-v0", "SPWX-v0", "TRUX-v0", "ASUX-v0"}
    check("E9C-26_required_models", required_models <= set(acc["required_previous_models"]),
          f"missing={sorted(required_models - set(acc['required_previous_models']))}")
    check("E9C-26_regressions_required",
          all(acc[k] is True for k in ["independent_qa_required", "stage6_regression_required",
                                       "stage7_regression_required", "stage8_regression_required",
                                       "stage9a_regression_required", "stage9b_regression_required"]),
          f"acceptance={acc}")

    # --- 27. Entity id hygiene --------------------------------------------
    all_entity_ids = ([e["id"] for e in m["curriculum_entities"]]
                      + [e["id"] for e in m["truth_entities"]]
                      + [e["id"] for e in m["projection_entities"]])
    bad_ids = [e for e in all_entity_ids if not ID_RE.match(e)]
    check("E9C-27_entity_id_format", not bad_ids, f"bad_ids={bad_ids}")
    check("E9C-27_entity_ids_unique", len(all_entity_ids) == len(set(all_entity_ids)),
          f"duplicates={len(all_entity_ids) - len(set(all_entity_ids))}")

    # --- 28. Spec / research textual contract ------------------------------
    spec_markers = [
        "DDM-v0 — Domain Data Model",
        "`D-077`",
        "The schema enforces the architecture",
        "PRIMARY KEY (logical_id, version)",
        "A reference by logical ID alone is forbidden.",
        "Four axes stay four columns",
        "There is no `UPDATE` path on a truth row.",
        "Every timestamped row stores **three** values",
        "9D — Servis sınırları",
    ]
    check("E9C-28_spec_contract", all(m_ in spec for m_ in spec_markers),
          f"missing={[m_ for m_ in spec_markers if m_ not in spec]}")
    check("E9C-28_spec_no_numbers",
          "No row-count budget, query latency target, storage size estimate or specific index implementation is canonical in 9C." in spec,
          "spec refuses invented numbers")

    research_markers = [
        "A schema can silently repeal an architecture",
        "Version pinning has to be structural",
        "Several independent axes must not be collapsed",
        "Time is not one value",
        "Projections need provenance",
        "Exposure must be queryable, not merely stored",
    ]
    check("E9C-29_research_synthesis", all(m_ in research for m_ in research_markers),
          f"missing={[m_ for m_ in research_markers if m_ not in research]}")

    return finish(args.write_report, m)


def finish(write_report: bool, m) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "9C",
        "model": "DDM-v0",
        "candidate_decision": "D-077",
        "result": result,
        "status_observed": m.get("status") if isinstance(m, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "curriculum_entities": len(m.get("curriculum_entities", [])) if isinstance(m, dict) else None,
        "truth_entities": len(m.get("truth_entities", [])) if isinstance(m, dict) else None,
        "projection_entities": len(m.get("projection_entities", [])) if isinstance(m, dict) else None,
        "forbidden_model_patterns": len(m.get("forbidden_model_patterns", [])) if isinstance(m, dict) else None,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"9C_DOMAIN_DATA_MODEL_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"curriculum={report['curriculum_entities']} truth={report['truth_entities']} "
          f"projection={report['projection_entities']} forbidden={report['forbidden_model_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
