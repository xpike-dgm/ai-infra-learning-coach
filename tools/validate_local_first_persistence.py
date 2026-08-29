from __future__ import annotations

import argparse
from pathlib import Path
import yaml

ROOT = Path(__file__).resolve().parents[1]
PERSIST = ROOT / "arch/9b_local_first_persistence/persistence.yaml"
SPEC = ROOT / "docs/LOCAL_FIRST_PERSISTENCE_SPEC.md"
RESEARCH = ROOT / "research/9b_local_first_persistence_research.md"
TECH = ROOT / "arch/9a_mobile_technology/technology.yaml"
PROGRESS = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
FLOW = ROOT / "ux/8c_daily_working_flow/flow.yaml"
SESSION = ROOT / "ux/8d_assessment_session/session.yaml"
V1 = ROOT / "docs/V1_SCOPE.md"
KGC = ROOT / "docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md"
QAB = ROOT / "docs/QUESTION_BANK_SPEC.md"
REPORT = ROOT / "arch/9b_local_first_persistence/qa_report.yaml"

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

    for path in [PERSIST, SPEC, RESEARCH, TECH, PROGRESS, FLOW, SESSION, V1, KGC, QAB]:
        check("E9B-00_required_file", path.exists(), str(path.relative_to(ROOT)))
    if failures:
        return finish(args.write_report, None)

    p = load_yaml(PERSIST)
    tech = load_yaml(TECH)
    progress = load_yaml(PROGRESS)
    flow = load_yaml(FLOW)
    session = load_yaml(SESSION)
    spec = SPEC.read_text(encoding="utf-8")
    research = RESEARCH.read_text(encoding="utf-8")
    v1 = V1.read_text(encoding="utf-8")
    kgc = KGC.read_text(encoding="utf-8")
    qab = QAB.read_text(encoding="utf-8")

    # --- 1. Identity -------------------------------------------------------
    check("E9B-01_identity", p.get("model") == "LFPS-v0" and p.get("stage_step") == "9B",
          f"model={p.get('model')} step={p.get('stage_step')}")
    check("E9B-01_status", p.get("status") in {"candidate_9b", "accepted_9b"}, f"status={p.get('status')}")
    check("E9B-01_decision", (p.get("candidate_decision") or p.get("decision")) == "D-076",
          f"decision={p.get('candidate_decision') or p.get('decision')}")
    check("E9B-01_platform", p.get("platform") == "AMTS-v0", f"platform={p.get('platform')}")

    # --- 2. Scope ----------------------------------------------------------
    scope = p["scope"]
    check("E9B-02_architecture_only", scope["persistence_architecture_only"] is True, "9B is architecture only")
    deferred = ["physical_schema_locked", "entities_and_relations_locked", "service_boundaries_locked",
                "ai_integration_locked", "test_strategy_locked", "orm_library_locked",
                "encryption_posture_locked"]
    check("E9B-02_deferred_open", all(scope[k] is False for k in deferred),
          f"violations={[k for k in deferred if scope[k] is not False]}")
    check("E9B-02_stage8_untouched", scope["stage8_semantics_changed"] is False, "AŞAMA 8 semantics untouched")
    nulls = ["storage_size_budget", "query_performance_number", "retention_cutoff", "library_version_locked"]
    check("E9B-02_no_invented_numbers", all(scope[k] is None for k in nulls),
          f"violations={[k for k in nulls if scope[k] is not None]}")
    check("E9B-02_schema_owner", p["canonical_truth_owners"]["physical_schema"] == "9C",
          f"schema owner={p['canonical_truth_owners']['physical_schema']}")

    # --- 3. Invariants -----------------------------------------------------
    inv = p["invariants"]
    expected_false = [
        "derived_state_is_source_of_truth", "truth_record_updated_in_place", "truth_record_deleted",
        "invalid_evidence_deleted_instead_of_marked", "curriculum_update_changes_learner_state",
        "published_curriculum_version_overwritten", "curriculum_update_reinterprets_past_evidence",
        "exposure_record_droppable", "exposure_loss_is_cache_eviction",
        "attempt_persisted_without_assistance_metadata", "attempt_persisted_without_provenance",
        "evidence_written_for_evaluation_pending", "partial_write_observable", "partial_migration_applied",
        "partial_restore_applied", "schema_downgrade_supported", "restore_from_newer_schema_best_effort",
        "restore_silently_merges", "silent_progress_reset", "reset_preferred_over_recomputation",
        "evidence_pruned_in_v1", "implicit_pruning_allowed", "core_signature_exposes_storage_type",
        "core_depends_on_android_for_persistence", "persistence_requires_network",
        "persistence_requires_ai_client", "realtime_cloud_sync_in_v1", "automatic_cloud_upload_in_v1",
        "orm_library_named_here", "schema_defined_here",
    ]
    check("E9B-03_false_guards", all(inv[k] is False for k in expected_false),
          f"violations={[k for k in expected_false if inv.get(k) is not False]}")
    expected_true = ["evidence_is_source_of_truth", "truth_records_append_only", "derived_state_rebuildable",
                     "one_action_one_transaction", "migration_forward_only", "restore_is_atomic_and_verified",
                     "curriculum_and_user_state_separately_versioned"]
    check("E9B-03_true_guards", all(inv[k] is True for k in expected_true),
          f"violations={[k for k in expected_true if inv.get(k) is not True]}")
    check("E9B-03_guard_count", len(expected_false) >= 28, f"false_guards={len(expected_false)}")

    # --- 4. Source of truth -----------------------------------------------
    sot = p["source_of_truth"]
    required_truth = {"attempt", "artifact", "evidence_event", "assistance_metadata", "artifact_provenance",
                      "exposure_record"}
    check("E9B-04_truth_records", required_truth <= set(sot["truth_records"]),
          f"missing={sorted(required_truth - set(sot['truth_records']))}")
    required_derived = {"skill_and_objective_mastery", "retention_state", "prerequisite_readiness",
                        "topic_state", "weakness_and_remediation_state", "technical_english_profile"}
    check("E9B-04_derived_projections", required_derived <= set(sot["derived_projections"]),
          f"missing={sorted(required_derived - set(sot['derived_projections']))}")
    check("E9B-04_no_overlap", not (set(sot["truth_records"]) & set(sot["derived_projections"])),
          f"overlap={sorted(set(sot['truth_records']) & set(sot['derived_projections']))}")
    check("E9B-05_append_only",
          sot["append_only"] is True and sot["updated_in_place"] is False and sot["deleted"] is False,
          f"append-only={sot['append_only']}")
    check("E9B-05_projection_is_cache",
          sot["projection_is_cache"] is True and sot["projection_rebuildable_at_any_time"] is True
          and sot["rebuild_is_deterministic_for_same_inputs_and_policy_version"] is True,
          "projection is a deterministic rebuildable cache")
    check("E9B-05_only_evidence_changes_capability",
          sot["only_new_evidence_changes_demonstrated_capability"] is True,
          "only new evidence changes demonstrated capability")
    check("E9B-06_mark_not_erase",
          sot["invalid_evidence_marked_not_erased"] is True and sot["disposition_is_part_of_record"] is True
          and sot["recomputation_excludes_by_rule_not_by_absence"] is True,
          "invalid evidence is marked, not erased")

    # --- 7. Storage engine -------------------------------------------------
    se = p["storage_engine"]
    check("E9B-07_engine_class", se["class"] == "embedded_transactional_relational" and se["choice"] == "sqlite",
          f"engine={se['class']}/{se['choice']}")
    check("E9B-07_justified_from_contracts", se["justified_from_contracts_not_preference"] is True,
          "engine justified from contracts")
    check("E9B-07_required_properties", len(se["required_properties"]) >= 5,
          f"properties={len(se['required_properties'])}")
    rejected = {r["id"] for r in se["rejected_alternatives"]}
    check("E9B-07_alternatives_recorded",
          {"document_or_key_value_store", "flat_files_or_serialized_snapshots"} <= rejected,
          f"rejected={sorted(rejected)}")
    check("E9B-07_alternatives_losses",
          all(r.get("loses") for r in se["rejected_alternatives"]), "each rejected alternative records its loss")
    check("E9B-08_orm_deferred",
          se["orm_or_mapping_library_chosen"] is False and se["orm_library_owner_step"] == "10A",
          f"orm={se['orm_or_mapping_library_chosen']} owner={se['orm_library_owner_step']}")
    # 9A must actually have deferred library currency for this precedent to hold.
    check("E9B-08_9a_precedent_real",
          tech["verification_list"]["owner_step"] == "10A"
          and tech["invariants"]["currency_claim_asserted_without_source"] is False,
          "AMTS-v0 really defers library currency to 10A")

    # --- 9. Dependency direction cross-checked against AMTS ---------------
    dd = p["dependency_direction"]
    check("E9B-09_core_owns_interfaces",
          dd["core_declares_persistence_interfaces"] is True and dd["platform_implements_over_sqlite"] is True
          and dd["interfaces_expressed_in_core_types"] is True, f"dependency={dd}")
    check("E9B-09_no_leak",
          dd["storage_type_in_core_signature"] is False and dd["android_type_in_core_signature"] is False
          and dd["filesystem_type_in_core_signature"] is False, "no storage/android/fs type in core signature")
    check("E9B-09_core_testable",
          dd["core_runnable_against_fake_or_in_memory_impl"] is True and dd["core_testable_without_device"] is True,
          "core testable without a device")
    check("E9B-09_amts_core_purity",
          set(tech["deterministic_core"]["forbidden_dependencies"]) == {"android_api", "ui_toolkit", "networking", "ai_client"},
          f"AMTS core forbidden={tech['deterministic_core']['forbidden_dependencies']}")
    check("E9B-09_boundary_owner", dd["module_layout_owner"] == "9D", f"module owner={dd['module_layout_owner']}")

    # --- 10. Curriculum vs user state cross-checked against V1 and KGC ----
    cu = p["curriculum_and_user_state"]
    check("E9B-10_separation",
          cu["separately_stored"] is True and cu["separately_versioned"] is True
          and cu["user_records_pin_curriculum_version"] is True, f"separation={cu}")
    check("E9B-10_v1_requires_separation", "curriculum data ile user state ayrılır" in v1,
          "V1_SCOPE requires curriculum/user state separation")
    check("E9B-10_kgc_immutability",
          "Published graph state sessizce değişmez" in kgc,
          "KGC-v0 forbids silent change to published graph state")
    check("E9B-11_versions_retained",
          cu["published_versions_retained"] is True and cu["published_version_overwritten"] is False
          and cu["curriculum_update_adds_versions_only"] is True, "published versions retained")
    check("E9B-11_update_is_not_state_change",
          cu["curriculum_update_is_a_state_change"] is False
          and cu["curriculum_update_rewrites_evidence"] is False
          and cu["curriculum_update_invalidates_evidence"] is False
          and cu["semantic_change_expressed_as_new_need_through_engines"] is True,
          "a curriculum update cannot change learner state by itself")
    check("E9B-11_reconstructable",
          cu["historical_evidence_reconstructable_against_producing_version"] is True,
          "historical evidence stays reconstructable")

    # --- 12. Exposure records ---------------------------------------------
    ex = p["exposure_records"]
    check("E9B-12_permanence", ex["permanence"] == "life_of_profile" and ex["first_class"] is True,
          f"exposure={ex['permanence']}")
    check("E9B-12_kinds",
          {"solution_exposure", "item_version_seen", "variant_family_exposure"} <= set(ex["retained_kinds"]),
          f"kinds={ex['retained_kinds']}")
    check("E9B-12_included_everywhere",
          ex["included_in_backup"] is True and ex["included_in_export"] is True
          and ex["included_in_migration"] is True, "exposure included in backup/export/migration")
    check("E9B-12_loss_is_data_loss",
          ex["loss_classified_as"] == "data_loss" and ex["loss_classified_as_cache_eviction"] is False,
          f"loss={ex['loss_classified_as']}")
    check("E9B-12_qab_separates_exposure",
          "kullanıcı exposure geçmişi ayrı tutulur" in qab,
          "QAB-v0 keeps user exposure history separate")
    check("E9B-12_asux_forbids_reuse",
          session["invariants"]["same_item_after_exposure_is_independent_recheck"] is False,
          "ASUX-v0 forbids reusing an exposed item as an independent recheck")

    # --- 13. Transactional boundaries -------------------------------------
    tb = p["transactional_boundaries"]
    required_tx = {"attempt", "artifact", "assistance_metadata", "artifact_provenance", "evidence_event",
                   "resulting_derived_state_update"}
    check("E9B-13_tx_unit", tb["unit"] == "one_learner_action_is_one_transaction", f"unit={tb['unit']}")
    check("E9B-13_committed_together", required_tx <= set(tb["committed_together"]),
          f"missing={sorted(required_tx - set(tb['committed_together']))}")
    check("E9B-13_no_partial",
          tb["partial_write_observable"] is False and tb["failed_write_leaves_previous_state_intact"] is True,
          "no observable partial write")
    check("E9B-13_metadata_always",
          tb["attempt_without_assistance_metadata"] is False and tb["attempt_without_provenance"] is False,
          "attempts always carry assistance metadata and provenance")
    check("E9B-14_evaluation_pending",
          tb["evaluation_pending_writes_evidence"] is False
          and tb["evaluation_pending_attempt_write_is_atomic"] is True,
          "evaluation_pending writes no evidence, atomically")
    check("E9B-14_trux_pending_real",
          flow["degraded_and_recovery"]["ai_unavailable"]["evaluation_pending_writes_evidence"] is False,
          "TRUX-v0 really forbids evidence for evaluation_pending")
    check("E9B-14_asux_pending_real",
          session["degraded_and_recovery"]["ai_evaluator_unavailable"]["evidence_written"] is False,
          "ASUX-v0 really forbids evidence for evaluation_pending")

    # --- 15. Migration -----------------------------------------------------
    mig = p["migration"]
    check("E9B-15_forward_only",
          mig["forward_only"] is True and mig["versioned"] is True and mig["downgrade_supported"] is False
          and mig["revert_mechanism"] == "restore_from_backup", f"migration={mig['forward_only']}")
    check("E9B-15_never_destroys",
          mig["deletes_evidence"] is False and mig["rewrites_evidence"] is False
          and mig["reinterprets_evidence"] is False and mig["deletes_exposure_or_provenance"] is False,
          "migration never destroys evidence/exposure/provenance")
    check("E9B-15_derived_rebuild_ok",
          mig["may_discard_and_rebuild_derived_state"] is True
          and mig["discarding_derived_state_is_data_loss"] is False,
          "discarding derived state is not data loss")
    check("E9B-16_migration_tested",
          mig["tested_against_populated_database"] is True and mig["tested_only_against_empty_database"] is False,
          "migration tested against populated data")
    check("E9B-16_fails_intact",
          mig["incomplete_migration_leaves_previous_state_intact"] is True
          and mig["incomplete_migration_surfaces"] == "data_recovery_required",
          "incomplete migration fails intact and surfaces recovery")

    # --- 17. Backup / export / restore ------------------------------------
    ber = p["backup_export_restore"]
    check("E9B-17_user_initiated",
          ber["user_initiated"] is True and ber["automatic_cloud_upload_in_v1"] is False,
          "backup/export user initiated, no auto cloud upload")
    required_export = {"truth_records", "exposure_records", "provenance", "curriculum_version_pins", "preferences"}
    check("E9B-17_export_complete",
          ber["export_complete_enough_to_reconstruct_profile"] is True
          and required_export <= set(ber["export_includes"]),
          f"missing={sorted(required_export - set(ber['export_includes']))}")
    check("E9B-17_export_versions",
          ber["export_records_schema_version"] is True and ber["export_records_policy_version"] is True,
          "export records schema and policy versions")
    check("E9B-17_export_no_loss",
          ber["export_requires_derived_state"] is False and ber["export_omission_loses_information"] is False,
          "omitting derived state loses no information")
    check("E9B-18_restore_atomic",
          ber["restore_atomic"] is True and ber["restore_verified_before_replacing"] is True
          and len(ber["restore_verification"]) >= 2, f"restore={ber['restore_atomic']}")
    check("E9B-18_restore_versions",
          ber["restore_from_older_schema_runs_forward_migration"] is True
          and ber["restore_from_newer_schema"] == "refused", "newer-schema restore refused")
    check("E9B-18_no_silent_merge",
          ber["restore_silently_merges"] is False and ber["profile_replacement_is_explicit"] is True,
          "restore never silently merges")
    check("E9B-18_v1_requires_backup", "Backup / export / restore" in v1,
          "V1_SCOPE requires backup/export/restore")

    # --- 19. Integrity and recovery ---------------------------------------
    ir = p["integrity_and_recovery"]
    check("E9B-19_integrity_checks",
          ir["integrity_checked_on_open"] is True and ir["integrity_checked_after_migration"] is True
          and ir["integrity_checked_after_restore"] is True, "integrity checked at all three points")
    check("E9B-19_corruption_state",
          ir["corruption_surfaces_state"] == "data_recovery_required" and ir["silent_progress_reset"] is False,
          f"corruption={ir['corruption_surfaces_state']}")
    check("E9B-19_state_is_accepted",
          "data_recovery_required" in progress["semantic_states"]
          and "recomputing_projection" in progress["semantic_states"],
          "both surfaced states are accepted SPWX-v0 states")
    check("E9B-20_recompute_not_reset",
          ir["inconsistent_projection_repair"] == "recomputation"
          and ir["inconsistent_projection_reset"] is False
          and ir["recomputation_surfaces_state"] == "recomputing_projection",
          "recomputation preferred over reset")
    check("E9B-20_no_false_claims",
          ir["unpersisted_work_claimed_as_saved"] is False
          and ir["recovery_restores_last_durable_checkpoint"] is True,
          "unpersisted work never claimed as saved")
    check("E9B-20_trux_recovery_real",
          flow["degraded_and_recovery"]["interruption_and_crash"]["claims_unpersisted_work"] is False,
          "TRUX-v0 really forbids claiming unpersisted work")

    # --- 21. History growth -----------------------------------------------
    hg = p["history_growth"]
    check("E9B-21_no_pruning",
          hg["evidence_pruned_in_v1"] is False and hg["automatic_deletion"] is False
          and hg["rolling_window"] is False and hg["tidy_up_job"] is False, f"pruning={hg}")
    check("E9B-21_future_pruning_explicit",
          hg["future_pruning_must_be_explicit_and_user_visible"] is True
          and hg["future_pruning_may_touch_data_current_state_depends_on"] is False,
          "future pruning is explicit and cannot touch depended-upon data")
    check("E9B-21_reads_backwards",
          set(hg["reads_backwards_for"]) == {"retention", "remediation", "verification"},
          f"reads_backwards_for={hg['reads_backwards_for']}")

    # --- 22. Local-first boundary -----------------------------------------
    lf = p["local_first_boundary"]
    check("E9B-22_offline_core",
          lf["core_read_write_works_offline"] is True
          and lf["network_absence_degrades_deterministic_core"] is False
          and lf["persistence_depends_on_ai_client"] is False, f"local_first={lf}")
    check("E9B-22_no_cloud_sync", lf["realtime_multi_device_cloud_sync_in_v1"] is False,
          "no realtime cloud sync in V1")
    check("E9B-22_v1_local_first", "V1 local-first'tür" in v1, "V1_SCOPE states local-first")

    # --- 23. Forbidden / boundaries / acceptance --------------------------
    forbidden = set(p["forbidden_persistence_patterns"])
    expected_forbidden = {
        "derived_state_as_source_of_truth", "truth_record_updated_or_deleted_in_place",
        "deleting_invalid_evidence_instead_of_marking_disposition",
        "curriculum_update_rewriting_or_invalidating_evidence",
        "overwriting_published_curriculum_version",
        "dropping_exposure_records_in_migration_export_or_cleanup",
        "persisting_attempt_without_assistance_metadata_or_provenance",
        "writing_evidence_for_evaluation_pending_attempt",
        "partially_applied_write_migration_or_restore",
        "schema_downgrade_instead_of_backup_restore",
        "best_effort_restore_from_newer_schema", "silent_progress_reset_on_corruption",
        "resetting_when_recomputation_would_repair", "implicit_evidence_pruning",
        "storage_or_android_type_in_core_signature",
        "naming_orm_library_or_asserting_currency_here",
        "deciding_entities_relations_or_physical_schema_here",
    }
    check("E9B-23_forbidden_patterns", expected_forbidden <= forbidden,
          f"missing={sorted(expected_forbidden - forbidden)}")
    check("E9B-23_forbidden_count", len(forbidden) == 17, f"count={len(forbidden)}")

    boundaries = {str(k): v for k, v in p["future_stage_boundaries"].items()}
    for stage in ["9C", "9D", "9E", "9F", "10A", "16", "19"]:
        check("E9B-24_boundary_open", stage in boundaries, f"stage={stage}")
    check("E9B-24_9c_owns_schema",
          boundaries["9C"] == "domain_data_model_entities_relations_and_physical_schema",
          f"9C={boundaries['9C']}")

    acc = p["acceptance"]
    required_models = {"AMTS-v0", "KGC-v0", "QAB-v0", "AIV-v0", "GRE-v0", "RVR-v0", "PRG-v0", "TSM-v0",
                       "WLRM-v0", "TEPM-v0", "TRUX-v0", "ASUX-v0", "SPWX-v0"}
    check("E9B-25_required_models", required_models <= set(acc["required_previous_models"]),
          f"missing={sorted(required_models - set(acc['required_previous_models']))}")
    check("E9B-25_regressions_required",
          all(acc[k] is True for k in ["independent_qa_required", "stage6_regression_required",
                                       "stage7_regression_required", "stage8_regression_required",
                                       "stage9a_regression_required"]), f"acceptance={acc}")

    # --- 26. Spec / research textual contract ------------------------------
    spec_markers = [
        "LFPS-v0 — Local-First Persistence Architecture",
        "`D-076`",
        "Evidence is the source of truth",
        "**Decision: an embedded transactional relational store — SQLite.**",
        "Exposure records are **permanent, first-class data**",
        "One learner action is one transaction.",
        "**Evidence is not pruned in V1.**",
        "9C — Domain veri modeli",
    ]
    check("E9B-26_spec_contract", all(m in spec for m in spec_markers),
          f"missing={[m for m in spec_markers if m not in spec]}")
    check("E9B-26_spec_no_invented_numbers",
          "No storage size budget, query performance number, retention cut-off or library version is canonical in 9B." in spec,
          "spec refuses invented numbers")

    research_markers = [
        "Exposure records are load-bearing forever",
        "What is the source of truth",
        "Partial writes are how a UI starts lying",
        "Core purity versus a platform database",
    ]
    check("E9B-27_research_synthesis", all(m in research for m in research_markers),
          f"missing={[m for m in research_markers if m not in research]}")

    return finish(args.write_report, p)


def finish(write_report: bool, p) -> int:
    result = "PASS" if not failures else "FAIL"
    report = {
        "stage_step": "9B",
        "model": "LFPS-v0",
        "candidate_decision": "D-076",
        "result": result,
        "status_observed": p.get("status") if isinstance(p, dict) else None,
        "checks_total": len(checks),
        "checks_passed": sum(1 for x in checks if x["result"] == "PASS"),
        "checks_failed": len(failures),
        "storage_engine": p.get("storage_engine", {}).get("choice") if isinstance(p, dict) else None,
        "truth_records": len(p.get("source_of_truth", {}).get("truth_records", [])) if isinstance(p, dict) else None,
        "derived_projections": len(p.get("source_of_truth", {}).get("derived_projections", [])) if isinstance(p, dict) else None,
        "forbidden_persistence_patterns": len(p.get("forbidden_persistence_patterns", [])) if isinstance(p, dict) else None,
        "checks": checks,
        "failures": failures,
    }
    if write_report:
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    print(f"9B_LOCAL_FIRST_PERSISTENCE_QA={result}")
    print(f"checks={report['checks_total']} failures={report['checks_failed']}")
    print(f"engine={report['storage_engine']} truth={report['truth_records']} "
          f"derived={report['derived_projections']} forbidden={report['forbidden_persistence_patterns']}")
    if failures:
        for failure in failures:
            print(f"- {failure}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
