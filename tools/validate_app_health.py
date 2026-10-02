"""Independent 10E QA — APHX-v0 App Health.

The implementation is validated against the accepted contracts, not against its own. The six
cross-cutting states are read out of `UXIA-v0`'s `ia.yaml`, their tones and the fault allow-list out
of `VDSX-v0`, the degraded-state behaviour out of `THUX-v0`, integrity and restore rules out of
`LFPS-v0`, the negative checks out of `TVSX-v0`, the AI outcome taxonomy out of `AIAX-v0` and the
port set out of `MSBX-v0` — and each is compared with the **actual Kotlin source**.

Some checks are about order in a file rather than presence: integrity must be checked before a
migration can write, and a restore candidate must be verified before the live file is replaced. A
presence check would pass code that does both, in the wrong order.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/10e_app_health/app_health.yaml"
SPEC = ROOT / "docs/APP_HEALTH_SPEC.md"
RESEARCH = ROOT / "research/10e_app_health_research.md"
QA_OUT = ROOT / "arch/10e_app_health/qa_report.yaml"

IA = ROOT / "ux/8a_information_architecture/ia.yaml"
HOME = ROOT / "ux/8b_today_home/home.yaml"
VDSX = ROOT / "ux/8f_design_system/design_system.yaml"
LFPS = ROOT / "arch/9b_local_first_persistence/persistence.yaml"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
AIAX = ROOT / "arch/9e_ai_integration/ai_integration.yaml"
TVSX = ROOT / "arch/9f_test_strategy/test_strategy.yaml"

STORE_HEALTH_KT = ANDROID / "core-model/src/main/kotlin/coach/StoreHealth.kt"
STARTUP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/StoreStartup.kt"
APP_HEALTH_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/AppHealth.kt"
OPENER_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/StoreOpener.kt"
BACKUP_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Backup.kt"
MIGRATIONS_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
AI_EVALUATOR_KT = ANDROID / "ai-adapter/src/main/kotlin/coach/ai/AiEvaluator.kt"
HEALTH_UI_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/HealthSurface.kt"
WIRING = ANDROID / "app-wiring/src/main/kotlin/coach/wiring"
ACTIVITY_KT = WIRING / "MainActivity.kt"
APPLICATION_KT = WIRING / "CoachApplication.kt"
GRAPH_KT = WIRING / "AppGraph.kt"
MANIFEST = ANDROID / "app-wiring/src/main/AndroidManifest.xml"
WITH_AI = ANDROID / "app-wiring/src/withAi/kotlin/coach/wiring/EvaluatorProvider.kt"
WITHOUT_AI = ANDROID / "app-wiring/src/withoutAi/kotlin/coach/wiring/EvaluatorProvider.kt"
WORKFLOW = ROOT / ".github/workflows/android.yml"

T2_TESTS = ANDROID / "data-persistence/src/test/kotlin/coach/persistence"
STARTUP_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/StoreStartupTest.kt"
HEALTH_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/AppHealthTest.kt"
AI_TEST = ANDROID / "ai-adapter/src/test/kotlin/coach/ai/AiEvaluatorTest.kt"

def declared_port_extensions(root):
    """Ports added after MSBX-v0 by an accepted later contract (14A, D-105), never by an unrecorded edit."""
    import yaml as _yaml
    path = root / "arch/14a_tutor_contract/tutor_contract.yaml"
    tutor = (_yaml.safe_load(path.read_text(encoding="utf-8")) or {}) if path.is_file() else {}
    ext = tutor.get("port_extension") or {}
    return [ext["port"]] if tutor.get("status") == "accepted_14a" and ext.get("decision") == "D-105" else []


results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def strip_comments(source: str) -> str:
    """Removes Kotlin comments so a scan judges code, not prose that describes the rule."""
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"//[^\n]*", "", source)


def enum_body(source: str, name: str) -> str:
    match = re.search(rf"enum class {name}\b[^{{]*\{{(.*?)\n\}}", source, re.S)
    return match.group(1) if match else ""


def ordered(source: str, *needles: str) -> bool:
    positions = [source.find(n) for n in needles]
    return all(p >= 0 for p in positions) and positions == sorted(positions)


contract = load(CONTRACT)
ia = load(IA)
home = load(HOME)
vdsx = load(VDSX)
lfps = load(LFPS)
msbx = load(MSBX)
aiax = load(AIAX)
tvsx = load(TVSX)

for path in (STORE_HEALTH_KT, STARTUP_KT, APP_HEALTH_KT, OPENER_KT, BACKUP_KT, MIGRATIONS_KT, AI_EVALUATOR_KT,
             HEALTH_UI_KT, ACTIVITY_KT, APPLICATION_KT, GRAPH_KT, STARTUP_TEST, HEALTH_TEST, AI_TEST, SPEC, RESEARCH):
    check(f"E10E-00_exists_{path.name}", path.is_file(), f"missing {path}")

store_health = strip_comments(read(STORE_HEALTH_KT))
startup = strip_comments(read(STARTUP_KT))
app_health = strip_comments(read(APP_HEALTH_KT))
opener = strip_comments(read(OPENER_KT))
backup = strip_comments(read(BACKUP_KT))
migrations = strip_comments(read(MIGRATIONS_KT))
ports = strip_comments(read(PORTS_KT))
ai_evaluator = strip_comments(read(AI_EVALUATOR_KT))
health_ui = read(HEALTH_UI_KT)
activity = strip_comments(read(ACTIVITY_KT))
application = strip_comments(read(APPLICATION_KT))
graph = strip_comments(read(GRAPH_KT))
manifest = read(MANIFEST)
with_ai = strip_comments(read(WITH_AI))
without_ai = strip_comments(read(WITHOUT_AI))
workflow = read(WORKFLOW)
t2_tests = "\n".join(read(p) for p in sorted(T2_TESTS.glob("*.kt")))
app_tests = read(STARTUP_TEST) + read(HEALTH_TEST) + read(AI_TEST)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E10E-01_model", contract.get("model") == "APHX-v0", str(contract.get("model")))
check("E10E-01_status", contract.get("status") == "accepted_10e", str(contract.get("status")))
check("E10E-01_decision", contract.get("decision") == "D-086", str(contract.get("decision")))
for key in ("persistence_rules_changed", "surface_semantics_changed", "boundaries_changed", "ports_added",
            "schema_changed", "restore_ui_implemented"):
    check(f"E10E-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")
for key in ("startup_time_claim", "integrity_check_time_claim", "database_size_claim"):
    check(f"E10E-01_noclaim_{key}", contract.get("scope", {}).get(key, "missing") is None,
          f"{key}={contract.get('scope', {}).get(key)}")

# ---------------------------------------------------- cross-cutting states match UXIA-v0
accepted_states = ia["cross_cutting_surface_states"]
state_body = enum_body(app_health, "CrossCuttingState")
kotlin_states = re.findall(r'\(\s*"([a-z_]+)"', state_body)
check("E10E-02_states_are_uxia", kotlin_states == accepted_states, f"kotlin={kotlin_states} ia={accepted_states}")
check("E10E-02_contract_states", contract["cross_cutting_states"]["ids"] == accepted_states,
      f"contract={contract['cross_cutting_states']['ids']}")

# --------------------------------------------------------- tones match VDSX-v0
tones = vdsx["surface_state_tones"]
fault_allowed = set(vdsx["fault_tone_allowed_states"])
for state in accepted_states:
    entry = re.search(rf'"{state}",\s*([^)]*\))', state_body)
    kotlin_tone = entry.group(1) if entry else ""
    is_fault = "SystemFaultState" in kotlin_tone or "SYSTEM_FAULT" in kotlin_tone
    expected_fault = tones.get(state) == "system_fault"
    check(f"E10E-03_tone_{state}", entry is not None and is_fault == expected_fault
          and (is_fault or f"Tone.{tones.get(state, '').upper()}" in kotlin_tone),
          f"kotlin={kotlin_tone!r} vdsx={tones.get(state)}")
check("E10E-03_fault_allowlist", fault_allowed == {"error_recoverable", "data_recovery_required"},
      f"vdsx={sorted(fault_allowed)}")

# ------------------------------------------------- precedence and degraded behaviour (THUX-v0)
of_body = app_health[app_health.find("fun of("):]
check("E10E-04_recovery_first_in_thux", home["primary_action_precedence"][0] == "data_recovery_required",
      str(home["primary_action_precedence"][:2]))
check("E10E-04_recovery_blocks",
      home["degraded_behavior"]["data_recovery_required"]["may_block_normal_plan_until_safe"] is True
      and re.search(r"RecoveryRequired -> AppHealth\(\s*blocking = CrossCuttingState\.DATA_RECOVERY_REQUIRED", of_body) is not None,
      "a recovery state must block normal use")
check("E10E-04_error_offers_retry",
      home["degraded_behavior"]["error_recoverable"]["safe_retry_or_recovery_required"] is True
      and re.search(r"RecoverableFailure -> AppHealth\(\s*blocking = CrossCuttingState\.ERROR_RECOVERABLE,.*?actions = setOf\(HealthAction\.RECHECK\)", of_body, re.S) is not None,
      "error_recoverable must offer a safe retry")
check("E10E-04_ai_unavailable_not_blocking",
      home["degraded_behavior"]["ai_unavailable_core_available"]["deterministic_home_usable"] is True
      and re.search(r"StoreStatus\.Ready -> AppHealth\(\s*blocking = null", of_body) is not None
      and of_body.count("AI_UNAVAILABLE_CORE_AVAILABLE") == 1,
      "AI unavailability must never block, and must be claimed only for a ready store")
check("E10E-04_shell_follows_normal_use", "val showsShell: Boolean get() = normalUseAvailable" in app_health,
      "the shell must be shown exactly when normal use is available")

# ------------------------------------------------------ no reset is representable
actions = re.findall(r"^\s*([A-Z_]+),?\s*$", enum_body(app_health, "HealthAction"), re.M)
check("E10E-05_single_action", actions == ["RECHECK"], f"actions={actions}")
destructive = re.compile(r"reset|wipe|delete|clear|recreate|fresh|erase|drop", re.I)
check("E10E-05_no_destructive_action", not any(destructive.search(a) for a in actions), f"actions={actions}")
check("E10E-05_upstream_no_silent_reset",
      lfps["integrity_and_recovery"]["silent_progress_reset"] is False
      and home["degraded_behavior"]["data_recovery_required"]["silent_progress_reset"] is False,
      "LFPS-v0 and THUX-v0 forbid silent reset")
check("E10E-05_opener_never_deletes",
      not re.search(r"\.delete\(|deleteRecursively|DROP TABLE|setLength", opener),
      "the opener must not delete, drop or truncate anything")

# ------------------------------------------------------ off the main thread, once per process
check("E10E-06_activity_does_not_open",
      not re.search(r"StoreOpener\.open|SqlitePersistence\.open|AppGraph\.open|getDatabasePath", activity),
      "MainActivity must not open, or resolve the path of, the store")
check("E10E-06_graph_has_no_open", not re.search(r"fun open\(", graph), "a synchronous AppGraph.open must not exist")
check("E10E-06_wiring_no_throwing_open",
      all("SqlitePersistence.open(" not in strip_comments(read(p)) for p in WIRING.glob("*.kt")),
      "the product must use StoreOpener, never the throwing open")
check("E10E-06_open_runs_in_background",
      "background.execute {" in startup and ordered(startup, "background.execute {", "open()"),
      "the opener must be invoked inside the background executor")
check("E10E-06_application_owns_startup",
      "StoreStartup(" in application and "startup.start()" in application
      and re.search(r"override fun onCreate\(\)", application) is not None
      and 'android:name=".CoachApplication"' in manifest,
      "the process, not the activity, must own the store startup")
# The opener's name is not the guarantee; where the work happens is. 11A renamed it when the
# background job also began reading Today's facts, so this asks for the private opener the startup
# is given, whatever it is called, and requires the path resolution to happen inside it.
opener_fun = re.search(r"private fun (\w+)\(\): StoreOpenOutcome", application)
opener_body = application[application.find(f"private fun {opener_fun.group(1)}("):] if opener_fun else ""
check("E10E-06_path_resolved_off_main",
      opener_fun is not None
      and f"::{opener_fun.group(1)}" in application
      and ordered(opener_body, "getDatabasePath", "StoreOpener.open"),
      "the database path must be resolved inside the background opener")
check("E10E-06_activity_observes", "startup.observe(listener)" in activity and "startup.stopObserving(listener)" in activity,
      "the activity observes and stops observing")
check("E10E-06_debug_strictmode", "detectDiskReads()" in application and "BuildConfig.DEBUG" in application,
      "debug builds must detect disk access on the main thread")
check("E10E-06_opener_defect_contained", re.search(r"catch \(defect: Throwable\)", startup) is not None,
      "an opener defect must become a status, not a crash")

# ------------------------------------------------------ boundaries (MSBX-v0)
check("E10E-07_ports_unchanged", sorted(re.findall(r"^interface (\w+Port)\b", ports, re.M)) == sorted([p["id"] for p in msbx["ports"]["set"]] + declared_port_extensions(ROOT))
      and len(msbx["ports"]["set"]) == 4,
      f"interfaces={re.findall(r'^interface (\w+Port)', ports, re.M)}")
check("E10E-07_ui_no_persistence", "coach.persistence" not in health_ui and "coach.application.StoreStartup" not in health_ui,
      "app-ui must not reach persistence or the startup")
check("E10E-07_ui_does_not_decide", "AppHealth.of(" not in health_ui and msbx["invariants"]["presentation_state_computed_in_ui_toolkit"] is False,
      "app-ui must render the health core computed")
check("E10E-07_status_is_core_type",
      "sealed interface StoreStatus" in store_health and "enum class RecoveryReason" in store_health
      and "package coach.model" in store_health,
      "the status and its reason must be core-model types")
check("E10E-07_core_application_platform_free",
      not re.search(r"import android|import androidx", startup),
      "core-application must stay free of Android")

# ------------------------------------------------------ integrity order (LFPS-v0 §12)
ir = lfps["integrity_and_recovery"]
check("E10E-08_upstream_integrity_points",
      ir["integrity_checked_on_open"] is True and ir["integrity_checked_after_migration"] is True
      and ir["integrity_checked_after_restore"] is True and ir["corruption_surfaces_state"] == "data_recovery_required",
      str(ir))
check("E10E-08_check_before_migration",
      ordered(opener, "Integrity.quickProblems(connection)", "Migrations.migrate(connection)", "Integrity.fullProblems(connection)"),
      "quick check, then migration, then full check — in that order")
check("E10E-08_foreign_keys_checked", "PRAGMA foreign_key_check" in opener, "foreign-key consistency must be checked")
check("E10E-08_full_check_is_integrity_check", "PRAGMA integrity_check" in opener and "PRAGMA quick_check" in opener,
      "both SQLite checks must be used where the contract says")
check("E10E-08_reason_travels_as_type",
      "val reason: RecoveryReason" in migrations and "refused.reason" in opener,
      "the recovery reason must travel as a type, not a parsed message")
check("E10E-08_unopenable_is_recoverable",
      re.search(r"driver\.open\(path\)\s*\}\s*catch \(failure: Throwable\)\s*\{\s*return Result\.NotOpened\(StoreStatus\.RecoverableFailure", opener) is not None,
      "a file that cannot be opened at all is error_recoverable")
check("E10E-08_unreadable_is_recovery",
      "recovery(connection, RecoveryReason.INTEGRITY_CHECK_FAILED, unreadable)" in opener,
      "a file that fails while reading is data_recovery_required")

# ------------------------------------------------------ backup / export / restore (LFPS-v0 §11)
ber = lfps["backup_export_restore"]
tv_ber = tvsx["persistence_verification"]["backup_export_restore"]
check("E10E-09_upstream_restore_rules",
      ber["restore_atomic"] is True and ber["restore_verified_before_replacing"] is True
      and ber["restore_from_newer_schema"] == "refused" and ber["restore_silently_merges"] is False
      and ber["export_records_schema_version"] is True and ber["user_initiated"] is True,
      str(ber))
check("E10E-09_upstream_tvsx",
      tv_ber["corrupted_or_newer_archive_leaves_profile_untouched"] is True and tv_ber["export_contains_api_key"] is False,
      str(tv_ber))
restore_body = backup[backup.find("fun restore("):backup.find("private fun verifyCandidate")]
check("E10E-09_verify_before_replace", ordered(restore_body, "verifyCandidate(staging", "replaceAtomically(staging, live)"),
      "the candidate must be verified before the live file is replaced")
check("E10E-09_works_on_a_copy", ordered(restore_body, "Files.copy(archive.toPath(), staging.toPath()", "verifyCandidate(staging")
      and "verifyCandidate(archive" not in backup,
      "the archive itself must never be opened or migrated")
check("E10E-09_atomic_rename", "StandardCopyOption.ATOMIC_MOVE" in backup, "the live file must be replaced by one atomic rename")
check("E10E-09_newer_refused", "if (version > Schema.VERSION) return Refusal.NEWER_SCHEMA" in backup, "a newer archive must be refused")
check("E10E-09_not_a_profile_refused", "return Refusal.NOT_A_PROFILE_ARCHIVE" in backup and "containsAll(expected)" in backup,
      "a database that is not a profile must be refused")
check("E10E-09_no_merge", not re.search(r"\bATTACH\b|INSERT INTO", backup), "restore must replace, never merge")
check("E10E-09_reopened_after_restore", ordered(restore_body, "replaceAtomically", "StoreOpener.open(live.absolutePath"),
      "integrity is checked after restore by reopening through the opener")
check("E10E-09_export_verified", ordered(backup, "store.vacuumInto(", "verifyCandidate(destination"),
      "an export must be verified when it is made")
check("E10E-09_old_journal_set_aside", '"-journal"' in backup and ".superseded" in backup,
      "a journal beside the old live file must not be replayed into the restored one")
check("E10E-09_controls_in_profile_owned_by_16D",
      contract["restore"]["controls_owner"] == "16D"
      and any(o.get("object") == "backup_export_restore" and o.get("primary_home") == "profile"
              for o in ia["information_ownership"] if isinstance(o, dict)),
      "UXIA-v0 homes backup/export/restore in Profile; its controls are 16D's")

# ------------------------------------------------------ AI absence is not a crash (AIAX-v0)
unavailable = next((o for o in aiax["outcome_taxonomy"]["outcomes"] if o["id"] == "unavailable"), {})
check("E10E-10_upstream_unavailable_pending",
      unavailable.get("degrades_to") == "evaluation_pending" and unavailable.get("writes_evidence") is False,
      str(unavailable))
check("E10E-10_adapter_does_not_throw", not re.search(r"\bTODO\(|throw ", ai_evaluator),
      "the adapter must not throw where AIAX-v0 says unavailable")
check("E10E-10_adapter_pending_unavailable", "EvaluationResult.EvaluationPending(PendingReason.UNAVAILABLE)" in ai_evaluator,
      "an adapter without call sites is evaluation_pending(unavailable)")
check("E10E-10_both_builds_report_availability",
      "fun evaluatorAvailability()" in with_ai and "fun evaluatorAvailability()" in without_ai
      and "EvaluatorAvailability.UNAVAILABLE" in without_ai,
      "both evaluator source sets must report availability")

# ------------------------------------------------------ the checks exist and attempt the forbidden
for name in [
    "a file that is not a database surfaces recovery and is left untouched",
    "a truncated database surfaces recovery and is left untouched",
    "a corrupted evidence page behind a valid header surfaces recovery and is left untouched",
    "a foreign key violation written behind the engine's back surfaces recovery",
    "damage only the full check can see is caught after a migration",
    "a newer schema surfaces recovery and is left untouched",
    "a migration that cannot complete surfaces recovery and leaves the older store as it was",
    "an opener never throws whatever it finds",
    "backup then restore onto a clean install preserves truth row for row",
    "restore replaces the live profile entirely and never merges",
    "an older archive is migrated forward on a copy and restored with its truth intact",
    "a corrupted archive is refused and the live profile is untouched",
    "an archive from a newer schema is refused rather than restored best effort",
    "a database that is not a profile is refused",
    "no column in an archive can hold a credential",
]:
    check(f"E10E-11_t2_{name[:44]}", f"`{name}`" in t2_tests, f"missing T2 test: {name}")
check("E10E-11_bytes_compared", "assertContentEquals(before, file.readBytes()" in t2_tests
      and "assertContentEquals(liveBefore, live.readBytes()" in t2_tests,
      "no-reset and untouched-profile claims must compare bytes")
check("E10E-11_quick_check_precondition", "the fixture must pass quick_check" in t2_tests,
      "the full-check fixture must prove quick_check passes first")
for name in [
    "start returns at once and the open runs on another thread",
    "starting twice opens once, so recreating the activity never reopens the database",
    "an opener that throws becomes a recoverable failure instead of a crash",
    "AI being unavailable never blocks and is only claimed alongside a working core",
    "no health action can reset delete or recreate the learner's data",
    "an adapter without call sites degrades to evaluation pending instead of throwing",
]:
    # Narrowed at 14G (`D-111`): the adapter now has a call site, so its "no call sites" test was renamed to say the same
    # guarantee about a missing client or key. The successor name is read from 14G's contract; nothing else is accepted.
    renamed = {r["before"]: r["after"] for r in load(ROOT / "arch/14g_provider_adapter/provider_adapter.yaml").get("renamed_tests", [])}
    check(f"E10E-12_test_{name[:44]}", f"`{name}`" in app_tests or f"`{renamed.get(name, '?')}`" in app_tests, f"missing test: {name}")
check("E10E-12_latch_proves_async", "CountDownLatch" in app_tests and "assertNotEquals(Thread.currentThread()" in app_tests,
      "the off-thread claim must be proven with a blocked opener")
neg = {n["id"]: n for n in tvsx["negative_checks_required"]}
check("E10E-12_upstream_negatives",
      neg["NEG-06"]["must_surface"] == "data_recovery_required" and neg["NEG-07"]["forbidden"] == "restore_from_newer_schema",
      "TVSX-v0 NEG-06 and NEG-07")

# ------------------------------------------------------ every state is said in words
labels = re.search(r"stateLabelsTr[^=]*=\s*mapOf\((.*?)\n\)", health_ui, re.S)
labelled = re.findall(r"CrossCuttingState\.([A-Z_]+) to", labels.group(1) if labels else "")
check("E10E-13_every_state_labelled", len(labelled) == len(accepted_states) == len(set(labelled)),
      f"labelled={labelled}")
reasons = re.findall(r"^\s*([A-Z_]+),\s*$", enum_body(store_health, "RecoveryReason"), re.M)
explained = re.findall(r"RecoveryReason\.([A-Z_]+) to", health_ui)
check("E10E-13_every_reason_explained", reasons and sorted(reasons) == sorted(explained),
      f"reasons={reasons} explained={explained}")
check("E10E-13_nothing_reset_said", "NOTHING_RESET_TR" in health_ui and "sıfırlanmadı" in health_ui,
      "the recovery screen must say nothing was reset")
check("E10E-13_announced", "LiveRegionMode.Polite" in health_ui, "a state change must be announced")
check("E10E-13_chip_renders_text", "StateChip(" in health_ui, "the state is rendered as text through the chip")
check("E10E-13_touch_target", "minimumTouchTarget()" in health_ui, "actions keep the 48dp floor")

# ------------------------------------------------------ CI runs what was added
check("E10E-14_ci_core_application", ":core-application:test" in workflow and ":core-presentation:test" in workflow,
      "T1 must run the new core tests")
check("E10E-14_ci_adapter_test", ":ai-adapter:test" in workflow, "CI must run the adapter test")
check("E10E-14_ci_t2", ":data-persistence:test" in workflow, "CI must run T2")

# ------------------------------------------------------ honesty of the record
device = contract["device_verification"]
check("E10E-15_device_not_claimed", device["t6_run"] is False and device["claimed"] is False,
      "no device result may be claimed when the device was not connected")
mutation = contract["mutation_results"]
check("E10E-15_mutation_all_detected", mutation["detected"] == mutation["total"] >= 12, str({k: mutation[k] for k in ("total", "detected")}))
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E10E-15_runs_pass", len(runs) >= 5 and all(r["result"] == "PASS" for r in runs.values()),
      str({k: v["result"] for k, v in runs.items()}))
check("E10E-15_undetected_gaps_recorded", len(contract.get("not_verified", [])) >= 3,
      "what this step does not verify must be written down")
# YAML reads bare step numbers (11, 12, 14) as integers; compare as the step codes they are.
boundary_owners = {str(k) for k in contract.get("future_stage_boundaries", {})}
for owner in ("16D", "19B", "14", "12", "11", "18E"):
    check(f"E10E-15_boundary_{owner}", owner in boundary_owners, f"missing {owner}")

forbidden = set(contract.get("forbidden_app_health_patterns", []))
for pattern in ["opening_the_store_on_the_main_thread", "crashing_on_data_recovery_required",
                "a_reset_or_start_fresh_action_on_a_recovery_screen", "migrating_before_checking_integrity",
                "classifying_a_failure_by_parsing_its_message", "restoring_by_merging",
                "verifying_or_migrating_the_archive_in_place", "claiming_ai_unavailable_while_the_core_is_not_available",
                "claiming_a_device_result_that_was_not_run"]:
    check(f"E10E-16_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 10E QA PASS", "**Decision:** `D-086`",
                 "data_recovery_required", "quick_check", "integrity_check", "16D", "byte for byte"]:
    check(f"E10E-17_spec_{fragment[:28]}", fragment in spec_text, f"missing={fragment!r}")
for fragment in ["TODO()", "quick_check", "16D", "19B"]:
    check(f"E10E-18_research_{fragment}", fragment in research_text, f"missing={fragment!r}")

acceptance = contract.get("acceptance", {})
for key in ("independent_qa_required", "build_must_be_run_not_asserted", "no_reset_proven_by_bytes",
            "stage9_regression_required", "stage10abcd_regression_required"):
    check(f"E10E-19_{key}", acceptance.get(key) is True, f"{key}={acceptance.get(key)}")

# ------------------------------------------------------------------ report
passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "APHX-v0", "stage_step": "10E", "decision": "D-086",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "cross_cutting_states": len(kotlin_states), "health_actions": len(actions),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"10E_APP_HEALTH_QA={report['result']}")
print(f"checks={passed}/{len(results)} states={len(kotlin_states)} actions={len(actions)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
