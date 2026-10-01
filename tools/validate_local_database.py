"""Independent 10D QA — LDBX-v0 Local Database.

The schema is validated against the accepted data model, not against its own contract. Entity
inventories, every allowed value set, the time columns, projection provenance, metadata and index
intent are read out of `DDM-v0`'s `data_model.yaml` and compared with the **actual Kotlin DDL**;
the migration rules are read out of `LFPS-v0`; the negative checks `TVSX-v0` requires are looked
for in the real T2 suite.

This is the check that caught the first draft of this very step using the wrong outcome values and
storing the UTC offset in seconds. If the schema and the accepted model ever drift, it fails.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]

CONTRACT = ROOT / "arch/10d_local_database/local_database.yaml"
SPEC = ROOT / "docs/LOCAL_DATABASE_SPEC.md"
RESEARCH = ROOT / "research/10d_local_database_research.md"
QA_OUT = ROOT / "arch/10d_local_database/qa_report.yaml"

DDM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
LFPS = ROOT / "arch/9b_local_first_persistence/persistence.yaml"
TVSX = ROOT / "arch/9f_test_strategy/test_strategy.yaml"

PKG = ROOT / "android/data-persistence/src/main/kotlin/coach/persistence"
SCHEMA_KT = PKG / "Schema.kt"
MIGRATIONS_KT = PKG / "Migrations.kt"
ADAPTER_KT = PKG / "SqlitePersistence.kt"
TESTS = ROOT / "android/data-persistence/src/test/kotlin/coach/persistence"
PORTS_KT = ROOT / "android/core-ports/src/main/kotlin/coach/ports/Ports.kt"

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


def kotlin_list(source: str, name: str) -> list[str]:
    """Reads `val name = listOf("a", "b", ...)` out of Kotlin source."""
    match = re.search(rf"val {name}\s*=\s*\n?\s*listOf\((.*?)\)", source, re.S)
    return re.findall(r'"([^"]+)"', match.group(1)) if match else []


contract = load(CONTRACT)
ddm = load(DDM)
lfps = load(LFPS)
tvsx = load(TVSX)

for path in (SCHEMA_KT, MIGRATIONS_KT, ADAPTER_KT, PORTS_KT, SPEC, RESEARCH):
    check(f"E10D-00_exists_{path.name}", path.is_file(), f"missing {path}")

schema = read(SCHEMA_KT)
migrations = read(MIGRATIONS_KT)
adapter = read(ADAPTER_KT)
ports = read(PORTS_KT)
tests = "\n".join(read(p) for p in sorted(TESTS.glob("*.kt")))
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity
check("E10D-01_model", contract.get("model") == "LDBX-v0", str(contract.get("model")))
check("E10D-01_status", contract.get("status") == "accepted_10d", str(contract.get("status")))
check("E10D-01_decision", contract.get("decision") == "D-085", str(contract.get("decision")))
for key in ("data_model_changed", "persistence_rules_changed", "boundaries_changed", "new_entities_invented"):
    check(f"E10D-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")
for key in ("row_count_claim", "query_latency_claim", "database_size_claim"):
    check(f"E10D-01_noclaim_{key}", contract.get("scope", {}).get(key, "missing") is None,
          f"{key}={contract.get('scope', {}).get(key)}")

# ------------------------------------------------- entity inventories match DDM-v0
ddm_curriculum = [e["id"] for e in ddm["curriculum_entities"]]
ddm_truth = [e["id"] for e in ddm["truth_entities"]]
ddm_projection = [e["id"] for e in ddm["projection_entities"]]

kotlin_curriculum = kotlin_list(schema, "curriculumTables")
kotlin_truth = kotlin_list(schema, "truthTables")
kotlin_projection = kotlin_list(schema, "projectionTables")

# DDM names the skill/objective/topic entities without a `curriculum_` prefix except the version
# manifest; the table names are the entity ids exactly.
check("E10D-02_curriculum_inventory", set(kotlin_curriculum) == set(ddm_curriculum),
      f"missing={sorted(set(ddm_curriculum) - set(kotlin_curriculum))} extra={sorted(set(kotlin_curriculum) - set(ddm_curriculum))}")
extra_truth = set(kotlin_truth) - set(ddm_truth)
check("E10D-02_truth_covers_ddm", set(ddm_truth) <= set(kotlin_truth),
      f"missing={sorted(set(ddm_truth) - set(kotlin_truth))}")
check("E10D-02_truth_extra_is_documented",
      extra_truth == {"evidence_event_objective"}
      and contract["store_regions"]["user_truth_store"]["extra_relational_table"] == "evidence_event_objective"
      and contract["store_regions"]["user_truth_store"]["extra_table_is_new_entity"] is False,
      f"undocumented extra truth tables: {sorted(extra_truth)}")
# Narrowed at 13F: DDM-v0's eight projections must all be there, and any further projection must be declared by the
# accepted later contract that added it (`projection_extension` in its arch yaml, under its own decision) — so an
# undeclared table still fails, and the accepted DDM-v0 contract itself is never edited.
declared_projection = set()
for _path in sorted(ROOT.glob("arch/*/*.yaml")):
    try:
        _doc = yaml.safe_load(_path.read_text(encoding="utf-8")) or {}
    except Exception:
        continue
    _ext = _doc.get("projection_extension") if isinstance(_doc, dict) else None
    if isinstance(_ext, dict) and _ext.get("table") and _ext.get("decision") and str(_doc.get("status", "")).startswith("accepted_"):
        declared_projection.add(_ext["table"])
check("E10D-02_projection_inventory", set(ddm_projection) <= set(kotlin_projection)
      and set(kotlin_projection) - set(ddm_projection) <= declared_projection,
      f"missing={sorted(set(ddm_projection) - set(kotlin_projection))} undeclared extra={sorted(set(kotlin_projection) - set(ddm_projection) - declared_projection)}")
for table in ddm_curriculum + ddm_truth + ddm_projection:
    check(f"E10D-03_ddl_{table}", re.search(rf"CREATE TABLE IF NOT EXISTS {table} \(", schema) is not None,
          f"no CREATE TABLE for {table}")

# ------------------------------------------------ allowed value sets match DDM-v0
axes = ddm["evidence_event"]["independent_axes"]
value_sets = {
    "outcomeValues": axes["outcome"],
    "evaluatorStatusValues": axes["evaluator_status"],
    "independenceClassValues": axes["independence_class"],
    "dispositionValues": ddm["evidence_disposition"]["disposition_values"],
    "decidedByValues": ddm["evidence_disposition"]["decided_by_values"],
    "assistanceLevelValues": ddm["assistance_event"]["level_values"],
    "assistanceTimingValues": ddm["assistance_event"]["timing_values"],
    "assistanceScopeValues": ddm["assistance_event"]["target_scope_values"],
    "assistanceSourceValues": ddm["assistance_event"]["source_values"],
    "provenanceOriginValues": ddm["artifact_provenance"]["origin_values"],
    "exposureKindValues": ddm["exposure_record"]["exposure_kinds"],
}
for kotlin_name, accepted in value_sets.items():
    found = kotlin_list(schema, kotlin_name)
    check(f"E10D-04_values_{kotlin_name}", found == list(accepted),
          f"kotlin={found} ddm={accepted}")
check("E10D-04_axes_not_collapsed", ddm["evidence_event"].get("axes_collapsed") is False
      and all(f"{axis}" in schema for axis in ("outcome", "evaluator_status", "independence_class", "contested")),
      "the four axes must be four columns")
check("E10D-04_checks_generated",
      all(f'oneOf("{c}", {v})' in schema for c, v in [
          ("outcome", "outcomeValues"), ("evaluator_status", "evaluatorStatusValues"),
          ("independence_class", "independenceClassValues"), ("disposition", "dispositionValues"),
          ("exposure_kind", "exposureKindValues"), ("origin", "provenanceOriginValues")]),
      "each value set must be enforced by a CHECK constraint built from the same list")
check("E10D-04_contested_boolean", "CHECK (contested IN (0, 1))" in schema, "contested must be 0/1")

# -------------------------------------------------------------- time columns
time_fields = ddm["time_representation"]["fields_on_every_timestamped_row"]
check("E10D-05_time_fields_are_ddm", time_fields == ["occurred_at_instant", "occurred_on_study_day", "utc_offset_minutes"],
      f"ddm={time_fields}")
check("E10D-05_offset_in_minutes", "utc_offset_minutes" in schema, "utc_offset_minutes missing")
check("E10D-05_no_seconds_offset_column", not re.search(r"utc_offset_seconds", schema),
      "an offset column in seconds contradicts DDM-v0")
check("E10D-05_instant_and_day_generated",
      '${prefix}_instant' in schema and '${dayPrefix}_study_day' in schema
      and 'fun time(prefix: String = "occurred_at", dayPrefix: String = "occurred_on")' in schema,
      "timestamped rows must carry DDM's instant and study-day columns")
check("E10D-05_disposition_decided_names",
      'time("decided_at", "decided_on")' in schema
      and "decided_at_instant" in ddm["evidence_disposition"]["fields"],
      "evidence_disposition must use DDM's decided_at naming")
check("E10D-05_provenance_answered_names",
      'time("answered_at", "answered_on")' in schema
      and "answered_at_instant" in ddm["artifact_provenance"]["fields"],
      "artifact_provenance must use DDM's answered_at naming")
check("E10D-05_offset_refused_not_truncated",
      "offsetSeconds % 60 == 0" in adapter and contract["time"]["non_whole_minute_offset"] == "refused"
      and contract["time"]["truncated"] is False,
      "a non-whole-minute offset must be refused, never truncated")
check("E10D-05_not_instant_only",
      ddm["invariants"].get("timestamp_stores_instant_only") is False
      and ddm["invariants"].get("timestamp_stores_local_date_only") is False,
      "DDM-v0 forbids instant-only and date-only timestamps")

# ------------------------------------------------------- append-only enforcement
check("E10D-06_truth_guards_cover_all",
      "private fun truthGuards(): List<String> = truthTables.flatMap" in schema,
      "update/delete guards must be generated for every truth table, not a subset")
check("E10D-06_update_guard", "BEFORE UPDATE ON $table" in schema and "RAISE(ABORT" in schema,
      "an UPDATE on a truth table must abort")
check("E10D-06_delete_guard", "BEFORE DELETE ON $table" in schema,
      "a DELETE on a truth table must abort")
check("E10D-06_guards_installed", "truth + truthGuards()" in schema,
      "the guards must be part of the installed schema")
check("E10D-06_upstream_no_update_path",
      ddm["invariants"].get("truth_table_has_update_path") is False
      and ddm["invariants"].get("truth_table_has_delete_path") is False
      and ddm["invariants"].get("soft_delete_flag_on_truth_row") is False,
      "DDM-v0 forbids update/delete paths and soft-delete flags")
check("E10D-06_no_soft_delete_column", not re.search(r"\b(deleted|is_deleted|archived)\b\s+INTEGER", schema),
      "a soft-delete flag is an update path in disguise")
check("E10D-06_curriculum_guards",
      "private fun curriculumGuards(): List<String> = curriculumTables.flatMap" in schema
      and "curriculum + curriculumGuards()" in schema,
      "curriculum rows must be immutable")
check("E10D-06_correction_is_disposition", ddm["evidence_disposition"].get("appended_not_updated") is True
      and "correct by appending a disposition" in schema,
      "a correction is an appended disposition")

# ---------------------------------------------------------------- identity / pinning
check("E10D-07_composite_keys", schema.count("PRIMARY KEY (logical_id, version)") >= 6,
      f"composite keys found: {schema.count('PRIMARY KEY (logical_id, version)')}")
check("E10D-07_fk_carries_version",
      "FOREIGN KEY (parent_skill_logical_id, parent_skill_version)" in schema
      and ddm["physical_schema"].get("foreign_keys_carry_version_column") is True,
      "foreign keys must carry the version column")
truth_block = schema[schema.index("private val truth = listOf("):schema.index("private val indexes")]
crossing = [t for t in ddm_curriculum if re.search(rf"REFERENCES {t} \(", truth_block)]
check("E10D-07_no_user_to_curriculum_fk", not crossing and ddm.get("curriculum_to_user_foreign_key") is False,
      f"truth tables reference curriculum: {crossing}")
check("E10D-07_resource_pinned_or_absent",
      "CHECK ((resource_logical_id IS NULL) = (resource_version IS NULL))" in schema,
      "a resource reference must never be version-free")
check("E10D-07_projection_key_pinned",
      "must be pinned as 'logical_id@vN'" in adapter,
      "a projection must not be addressable by logical id alone")

# ------------------------------------------------------ sequence / watermark
check("E10D-08_sequence_on_truth", "sequence INTEGER NOT NULL UNIQUE" in schema
      and ddm["physical_schema"].get("truth_tables_carry_monotonic_sequence") is True,
      "truth tables must carry a monotonic sequence")
check("E10D-08_sequence_is_watermark", ddm["physical_schema"].get("sequence_used_as_projection_watermark") is True
      and "fun truthWatermark()" in adapter and "truth_sequence" in adapter,
      "the sequence must be the projection watermark")

# ------------------------------------------------------ projections
provenance_fields = ddm["projection_provenance"]["fields"]
for field in provenance_fields:
    check(f"E10D-09_projection_provenance_{field}", field in schema, f"missing {field}")
check("E10D-09_port_carries_provenance",
      "val builtAtInstant: Long" in ports and "val inputCurriculumVersion: Int" in ports,
      "ProjectionRecord must carry every DDM provenance field")
skill_state = next(e for e in ddm["projection_entities"] if e["id"] == "skill_state")
for axis in skill_state["axes"]:
    check(f"E10D-09_skill_state_axis_{axis}", axis in schema, f"skill_state missing {axis}")
check("E10D-09_presentation_not_replacing_axes",
      skill_state.get("presentation_state_replaces_axes") is False and "primary_presentation_state" in schema,
      "the presentation state is derived and stored beside the axes")

# ------------------------------------------------------ metadata and indexes
for field in ddm["physical_schema"]["metadata_table_stores"]:
    check(f"E10D-10_metadata_{field}", field in schema, f"metadata missing {field}")
for index in ddm["physical_schema"]["index_intent"]:
    check(f"E10D-11_index_{index}", f"CREATE INDEX IF NOT EXISTS {index} ON" in schema, f"missing index {index}")

# ------------------------------------------------------ migration (LFPS-v0)
mig = lfps["migration"]
check("E10D-12_forward_only", mig.get("forward_only") is True and "downgrade is not supported" in migrations,
      "a newer schema must be refused")
check("E10D-12_transactional", 'connection.execSQL("BEGIN")' in migrations
      and 'connection.execSQL("ROLLBACK")' in migrations and 'connection.execSQL("COMMIT")' in migrations,
      "each migration step must be all-or-nothing")
check("E10D-12_surfaces_recovery", mig.get("incomplete_migration_surfaces") == "data_recovery_required"
      and "class DataRecoveryRequired" in migrations,
      "a failed migration must surface data_recovery_required")
check("E10D-12_populated_required", mig.get("tested_against_populated_database") is True
      and contract["migration"]["tested_against_populated_fixture"] is True,
      "migrations must be tested against populated data")
check("E10D-12_real_forward_step", "0 to Schema.v1" in migrations and "1 to Schema.v2" in migrations,
      "a real forward step must exist so a populated migration is exercised")

# ------------------------------------------------------ adapter reads the schema
check("E10D-13_columns_from_database", "PRAGMA table_info" in adapter
      and contract["adapter"]["second_hand_maintained_column_list"] is False,
      "the adapter must read columns from SQLite, not keep its own list")
check("E10D-13_transaction_rollback", 'runCatching { connection.execSQL("ROLLBACK") }' in adapter
      and "throw error" in adapter,
      "a failed action must roll back and rethrow")
check("E10D-13_bundled_driver", "BundledSQLiteDriver" in adapter and contract["engine"]["orm"] is False,
      "the bundled driver without an ORM")
check("E10D-13_api_read_not_guessed", contract["engine"].get("api_guessed") is False,
      "the library API must be read from the resolved jar")

# ------------------------------------------------------ the negative T2 checks exist
for name in [
    "every truth table refuses update and delete at the storage layer",
    "every curriculum table refuses update and delete",
    "a correction is an appended disposition and the original row is unchanged",
    "exposure records are permanent",
    "a resource reference is pinned or absent never version free",
    "an offset that is not a whole number of minutes is refused rather than truncated",
    "failure injected at each stage of an action always rolls back completely",
    "migrating a populated database preserves evidence exposure and provenance exactly",
    "a migration that fails part way leaves the previous state intact and surfaces data recovery",
    "a database from a newer schema is refused rather than opened on a guess",
    "no foreign key crosses from the user store into curriculum",
]:
    check(f"E10D-14_test_{name[:40]}", f"`{name}`" in tests, f"missing T2 test: {name}")
check("E10D-14_attempts_forbidden_update", 'db.execute("UPDATE $table' in tests,
      "the suite must actually execute the forbidden UPDATE")
check("E10D-14_attempts_forbidden_delete", 'db.execute("DELETE FROM $table")' in tests,
      "the suite must actually execute the forbidden DELETE")
check("E10D-14_partial_failure_sabotage", "injected failure after partial migration" in tests,
      "the migration failure must be injected after a partial change")
check("E10D-14_upstream_requires_negative",
      tvsx["invariants"].get("append_only_verified_by_attempted_violation") is True
      and tvsx["invariants"].get("migration_verified_against_populated_fixtures") is True,
      "TVSX-v0 requires attempted violation and populated fixtures")

# ------------------------------------------------------ honesty of the record
check("E10D-15_divergences_recorded",
      contract["first_draft_divergences_found"]["recorded_rather_than_hidden"] is True
      and len(contract["first_draft_divergences_found"]["items"]) >= 5,
      "the first draft's divergences from DDM-v0 must be recorded")
check("E10D-15_mutation_results", contract["mutation_results"]["detected"] == contract["mutation_results"]["total"] >= 9,
      str(contract["mutation_results"]))
check("E10D-15_strengthened_test_recorded",
      contract["mutation_results"]["strengthened_during_step"]["found_by"] == "mutation_testing",
      "the test mutation testing strengthened must be recorded")
check("E10D-15_minimal_columns_disclosed", len(contract["minimal_columns_pending_owner"]["columns"]) >= 5,
      "columns DDM-v0 does not name must be disclosed with their owner")
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E10D-15_runs_pass", len(runs) >= 4 and all(r["result"] == "PASS" for r in runs.values()),
      str({k: v["result"] for k, v in runs.items()}))
check("E10D-15_device_variant_verified",
      contract["engine"]["native_library_packaged_for_target_abi"] == "arm64-v8a",
      "the Android variant and target-ABI native library must be verified, not assumed")

# ------------------------------------------------------ forbidden patterns, spec, research
forbidden = set(contract.get("forbidden_database_patterns", []))
for pattern in ["an_update_or_delete_path_on_a_truth_table", "a_foreign_key_from_user_truth_into_curriculum",
                "a_reference_by_logical_id_alone", "collapsing_the_evidence_axes_into_one_column",
                "silently_truncating_a_utc_offset", "a_migration_that_can_apply_partially",
                "migrating_only_an_empty_database", "a_hand_maintained_column_list_beside_the_schema",
                "guessing_a_library_api_instead_of_reading_it", "inventing_entity_fields_the_data_model_does_not_name"]:
    check(f"E10D-16_forbidden_{pattern[:34]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 10D QA PASS", "**Decision:** `D-085`",
                 "positive", "utc_offset_minutes", "arm64-v8a", "ROLLBACK"]:
    check(f"E10D-17_spec_{fragment[:28]}", fragment in spec_text, f"missing={fragment!r}")
for fragment in ["javap", "first draft"]:
    check(f"E10D-18_research_{fragment}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

acceptance = contract.get("acceptance", {})
for key in ("independent_qa_required", "build_must_be_run_not_asserted",
            "negative_checks_must_attempt_the_forbidden_operation",
            "migration_must_be_tested_against_populated_data", "stage9_regression_required",
            "stage10abc_regression_required"):
    check(f"E10D-19_{key}", acceptance.get(key) is True, f"{key}={acceptance.get(key)}")

# ------------------------------------------------------------------ report
passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "LDBX-v0", "stage_step": "10D", "decision": "D-085",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "curriculum_tables": len(kotlin_curriculum), "truth_tables": len(kotlin_truth),
    "projection_tables": len(kotlin_projection),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"10D_LOCAL_DATABASE_QA={report['result']}")
print(f"checks={passed}/{len(results)} curriculum={len(kotlin_curriculum)} truth={len(kotlin_truth)} "
      f"projection={len(kotlin_projection)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
