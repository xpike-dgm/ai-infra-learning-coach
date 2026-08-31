"""Independent 10A QA — MPSX-v0 Mobile Project Skeleton.

This validator reads the **actual Gradle files and Kotlin sources** and compares them to the
accepted contracts, not to the 10A contract's own claims: the module set and every declared
project dependency are checked against `MSBX-v0`'s boundaries.yaml, the tier commands against
`TVSX-v0`, the platform levels against `AMTS-v0`'s recorded policy and the target device's own
API level, and the credential rules against `AIAX-v0` and `D-080`.

A skeleton that merely says it obeys the dependency rule proves nothing; the point here is that
the build files themselves are the evidence.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]

CONTRACT = ROOT / "arch/10a_project_setup/project_setup.yaml"
SPEC = ROOT / "docs/PROJECT_SETUP_SPEC.md"
RESEARCH = ROOT / "research/10a_project_setup_research.md"
QA_OUT = ROOT / "arch/10a_project_setup/qa_report.yaml"

ANDROID = ROOT / "android"
SETTINGS = ANDROID / "settings.gradle.kts"
ROOT_BUILD = ANDROID / "build.gradle.kts"
CATALOG = ANDROID / "gradle/libs.versions.toml"
WRAPPER = ANDROID / "gradle/wrapper/gradle-wrapper.properties"
GRADLE_PROPS = ANDROID / "gradle.properties"
WORKFLOW = ROOT / ".github/workflows/android.yml"
GITIGNORE = ROOT / ".gitignore"

BOUNDARIES = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
TEST_STRATEGY = ROOT / "arch/9f_test_strategy/test_strategy.yaml"
TECHNOLOGY = ROOT / "arch/9a_mobile_technology/technology.yaml"
AI_INTEGRATION = ROOT / "arch/9e_ai_integration/ai_integration.yaml"

# Libraries whose absence is a decision, not an oversight (see the 10A research synthesis).
FORBIDDEN_LIBRARY_MARKERS = [
    "dagger", "hilt", "koin", "kodein",              # no DI framework
    "room", "sqldelight", "objectbox", "realm",       # no ORM
    "retrofit", "okhttp", "ktor",                     # no HTTP client yet
    "konsist", "archunit",                            # no architecture-rule library
    "org.jetbrains.kotlin.android",                   # rejected by AGP 9+
]

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


contract = load(CONTRACT)
boundaries = load(BOUNDARIES)
strategy = load(TEST_STRATEGY)
technology = load(TECHNOLOGY)
ai_integration = load(AI_INTEGRATION)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity
check("E10A-01_model", contract.get("model") == "MPSX-v0", str(contract.get("model")))
check("E10A-01_status", contract.get("status") == "accepted_10a", str(contract.get("status")))
check("E10A-01_decision", contract.get("decision") == "D-082", str(contract.get("decision")))
check("E10A-01_boundaries_ref", contract.get("boundaries") == "MSBX-v0", str(contract.get("boundaries")))
check("E10A-01_strategy_ref", contract.get("verification_strategy") == "TVSX-v0",
      str(contract.get("verification_strategy")))
for key in ("build_performance_claim", "apk_size_claim", "newest_version_claim"):
    check(f"E10A-01_noclaim_{key}", contract.get("scope", {}).get(key, "missing") is None,
          f"{key}={contract.get('scope', {}).get(key)}")
for key in ("boundaries_changed", "persistence_rules_changed", "data_model_changed",
            "ai_integration_changed", "verification_strategy_changed",
            "new_product_semantics_introduced"):
    check(f"E10A-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")

# ------------------------------------------------- the project actually exists
for path in (SETTINGS, ROOT_BUILD, CATALOG, WRAPPER, GRADLE_PROPS):
    check(f"E10A-02_exists_{path.name}", path.is_file(), f"missing {path}")
check("E10A-02_wrapper_jar", (ANDROID / "gradle/wrapper/gradle-wrapper.jar").is_file(),
      "the wrapper jar must be committed or nobody else can build")
check("E10A-02_gradlew", (ANDROID / "gradlew").is_file(), "missing gradlew")

settings_text = read(SETTINGS)
catalog_text = read(CATALOG)
root_build_text = read(ROOT_BUILD)

# ------------------------------------- module set matches the accepted boundaries
declared_modules = set(re.findall(r'include\(":([a-z-]+)"\)', settings_text))
canonical_modules = {m["id"] for m in boundaries["modules"]}
check("E10A-03_module_set_matches_msbx", declared_modules == canonical_modules,
      f"missing={sorted(canonical_modules - declared_modules)} extra={sorted(declared_modules - canonical_modules)}")
check("E10A-03_module_count", len(canonical_modules) == 10, f"count={len(canonical_modules)}")
check("E10A-03_adapter_is_conditional",
      re.search(r'withAiAdapter.*\n.*include\(":ai-adapter"\)', settings_text, re.S) is not None
      or 'if (withAiAdapter.toBoolean())' in settings_text,
      "ai-adapter must be conditionally included so the product can build without it")

# ----------------------- every module's declared dependencies match boundaries.yaml
canonical_deps = {m["id"]: set(m.get("depends_on", [])) for m in boundaries["modules"]}
for module, expected in canonical_deps.items():
    build_file = ANDROID / module / "build.gradle.kts"
    if not build_file.is_file():
        check(f"E10A-04_buildfile_{module}", False, f"missing {build_file}")
        continue
    text = read(build_file)
    actual = set(re.findall(r'project\(":([a-z-]+)"\)', text))
    check(f"E10A-04_deps_{module}", actual == expected,
          f"declared={sorted(actual)} canonical={sorted(expected)}")

# ---------------------------------------- the dependency rule fails the build
check("E10A-05_task_registered", 'tasks.register("verifyModuleBoundaries")' in root_build_text,
      "verifyModuleBoundaries task must exist")
check("E10A-05_forbidden_edges_mirrored",
      all(f'"core" to "{layer}"' in root_build_text for layer in ("data", "ai", "app")),
      "the task must encode every forbidden edge from boundaries.yaml")
canonical_forbidden = {(e["from_layer"], e["to_layer"]) for e in boundaries["dependency_rule"]["forbidden_edges"]}
check("E10A-05_forbidden_edges_match_contract",
      canonical_forbidden == {("core", "data"), ("core", "ai"), ("core", "app")},
      f"upstream forbidden edges changed: {sorted(canonical_forbidden)}")
check("E10A-05_throws_on_violation", "throw GradleException" in root_build_text,
      "a violation must fail the build, not print a warning")
check("E10A-05_wired_into_check", 'it.name == "check"' in root_build_text,
      "the rule must run with the rest of verification")
check("E10A-05_cycle_computed", "colour" in root_build_text and "dependency cycle" in root_build_text,
      "the cycle must be computed, not assumed")
check("E10A-05_upstream_requires_checkable",
      boundaries["dependency_rule"].get("rule_is_checkable_not_aspirational") is True,
      "MSBX-v0 declares the rule checkable, which is what 10A had to deliver")
enforcement = contract.get("dependency_rule_enforcement", {})
check("E10A-05_fails_at_build_time", enforcement.get("fails_at") == "build_time",
      str(enforcement.get("fails_at")))
check("E10A-05_no_third_party_analyser", enforcement.get("third_party_analyser") is False,
      str(enforcement.get("third_party_analyser")))
check("E10A-05_mutation_tested", enforcement.get("mutation_tested") is True
      and enforcement.get("mutation_detected") is True, "the rule itself was mutation-tested")
check("E10A-05_tier_matches_strategy",
      any(t["id"] == "T3" and t.get("fails_at") == "build_time" for t in strategy["tiers"]),
      "TVSX-v0 T3 requires build-time failure")

# ------------------------------------------------ core stays pure Kotlin
for module in sorted(m for m in canonical_modules if m.startswith(("core-", "data-", "ai-"))):
    build_file = ANDROID / module / "build.gradle.kts"
    if build_file.is_file():
        text = read(build_file)
        check(f"E10A-06_no_android_plugin_{module}",
              "com.android." not in text and "android.library" not in text
              and "android.application" not in text,
              f"{module} must not apply an Android plugin")

# ----------------------------------------------- platform levels and the device
versions = dict(re.findall(r'^(minSdk|targetSdk|compileSdk)\s*=\s*"(\d+)"', catalog_text, re.M))
levels = contract["toolchain"]["platform_levels"]
check("E10A-07_min_sdk_matches", int(versions.get("minSdk", -1)) == levels["min_sdk"],
      f"catalog={versions.get('minSdk')} contract={levels['min_sdk']}")
check("E10A-07_target_sdk_matches", int(versions.get("targetSdk", -1)) == levels["target_sdk"],
      f"catalog={versions.get('targetSdk')} contract={levels['target_sdk']}")
check("E10A-07_compile_sdk_matches", int(versions.get("compileSdk", -1)) == levels["compile_sdk"],
      f"catalog={versions.get('compileSdk')} contract={levels['compile_sdk']}")
device = contract["target_device"]
check("E10A-07_device_recorded", device.get("recorded_here") is True and bool(device.get("model")),
      "the target device must be recorded here; AMTS-v0 left it open")
check("E10A-07_device_satisfies_min_sdk", device["api_level"] >= levels["min_sdk"],
      f"device api={device['api_level']} minSdk={levels['min_sdk']}")
check("E10A-07_target_sdk_matches_device", levels["target_sdk"] == device["api_level"],
      f"targetSdk={levels['target_sdk']} device={device['api_level']}")
check("E10A-07_min_sdk_is_amts_working_default", levels["min_sdk"] == 26,
      "AMTS-v0 §8.1 asks for the lowest level needing no weakening shim; java.time is native from 26")
check("E10A-07_amts_left_device_open",
      "not yet recorded in this repo" in read(ROOT / "docs/MOBILE_TECHNOLOGY_SPEC.md"),
      "AMTS-v0 is the spec that left the device open; 10A is where it closes")
check("E10A-07_single_device_scope", device.get("scope_source") == "D-080"
      and device.get("is_single_target_device") is True, "D-080 makes this a single target device")

# --------------------------------------------- the six-item AMTS verification list
items = contract["amts_verification_list"]["items"]
check("E10A-08_six_items", len(items) == 6 and {i["id"] for i in items} == set(range(1, 7)),
      f"ids={[i['id'] for i in items]}")
check("E10A-08_all_answered", all(str(i.get("answer", "")).strip() for i in items),
      "every item must carry an answer")
check("E10A-08_no_compat_library",
      next(i for i in items if i["id"] == 6).get("compatibility_library_required") is False,
      "item 6 must state whether a compatibility library is needed")
check("E10A-08_upstream_deferred_to_10a",
      technology.get("future_stage_boundaries", {}).get("10A") is not None
      or "resolve at 10A" in read(ROOT / "docs/MOBILE_TECHNOLOGY_SPEC.md"),
      "AMTS-v0 must actually have deferred this list to 10A")

# -------------------------------------------- dynamic colour is off, structurally
kotlin_sources = list(ANDROID.rglob("*.kt"))
check("E10A-09_sources_exist", len(kotlin_sources) >= 10, f"kotlin files={len(kotlin_sources)}")
dynamic_hits = [
    str(p.relative_to(ROOT))
    for p in kotlin_sources
    if re.search(r"dynamic(Light|Dark)ColorScheme", read(p))
]
check("E10A-09_no_dynamic_colour", not dynamic_hits, f"dynamic colour used in {dynamic_hits}")
check("E10A-09_upstream_forbids_dynamic_colour",
      technology["invariants"].get("dynamic_color_enabled") is False,
      "AMTS-v0 forbids dynamic colour")

# --------------------------------------------- the clock is read in exactly one place
clock_hits = [
    str(p.relative_to(ROOT))
    for p in kotlin_sources
    if re.search(r"Instant\.now\(\)|System\.currentTimeMillis\(\)|LocalDate\.now\(\)", read(p))
]
check("E10A-10_clock_read_once", len(clock_hits) == 1, f"system clock read in {clock_hits}")
check("E10A-10_clock_read_in_wiring", clock_hits and "app-wiring" in clock_hits[0],
      f"the only clock read must be the ClockPort implementation, found in {clock_hits}")
check("E10A-10_clock_is_a_port", boundaries["clock_port"].get("is_a_port") is True,
      "MSBX-v0 makes the clock a port")

# ------------------------------------------------- the null-evaluator seam
ai_absence = contract["ai_absence"]
with_ai = ANDROID / "app-wiring/src/withAi/kotlin/coach/wiring/EvaluatorProvider.kt"
without_ai = ANDROID / "app-wiring/src/withoutAi/kotlin/coach/wiring/EvaluatorProvider.kt"
check("E10A-11_both_source_sets_exist", with_ai.is_file() and without_ai.is_file(),
      "both evaluator source sets must exist")
check("E10A-11_without_ai_uses_null_evaluator", "NullEvaluator" in read(without_ai),
      "the no-adapter build must select the shipped null evaluator")
check("E10A-11_without_ai_does_not_import_adapter", "coach.ai." not in read(without_ai),
      "the no-adapter source set must not reference the adapter package")
check("E10A-11_with_ai_uses_adapter", "AiEvaluator" in read(with_ai), "the adapter build selects the adapter")
null_evaluator = ANDROID / "core-application/src/main/kotlin/coach/application/NullEvaluator.kt"
check("E10A-11_null_evaluator_ships", null_evaluator.is_file(),
      "the null evaluator must live in a shipped core module, not in a test source set")
check("E10A-11_null_evaluator_pending", "EvaluationPending" in read(null_evaluator),
      "the null evaluator must yield evaluation_pending")
check("E10A-11_upstream_ships_null_evaluator",
      boundaries["ai_absence"].get("null_evaluator_ships_with_product") is True
      and boundaries["ai_absence"].get("null_evaluator_is_test_fixture") is False,
      "MSBX-v0 ships it and refuses to call it a fixture")
check("E10A-11_criterion_8", ai_absence.get("satisfies_v1_criterion") == 8
      and ai_absence.get("build_without_adapter_succeeds") is True,
      "V1 criterion 8 must be satisfied by a build that actually ran")
check("E10A-11_adapter_absent_count", ai_absence.get("adapter_absent_module_count") == 9,
      str(ai_absence.get("adapter_absent_module_count")))
check("E10A-11_strategy_requires_build_not_stub",
      strategy["null_evaluator_verification"].get("stub_alone_is_sufficient") is False,
      "TVSX-v0 refuses a stub as sufficient")

# ------------------------------------------------------- no undeclared dependencies
# Comment lines explain why a library is absent, so only non-comment lines count as declarations.
declared_lines = [line for line in catalog_text.splitlines()
                  if line.strip() and not line.strip().startswith("#")]
declared_blob = "\n".join(declared_lines).lower()
actually_declared = [m for m in FORBIDDEN_LIBRARY_MARKERS if m in declared_blob]
check("E10A-12_no_forbidden_libraries", not actually_declared, f"declared={actually_declared}")
check("E10A-12_absences_are_reasoned",
      len(contract.get("dependencies_deliberately_absent", [])) >= 4
      and all(d.get("reason") for d in contract["dependencies_deliberately_absent"]),
      "each absent dependency must carry a reason")

# ------------------------------------------------------------- toolchain pins
pins = contract["toolchain"]["pins"]
for key, pattern in [("agp", r'agp\s*=\s*"([\d.]+)"'), ("kotlin", r'kotlin\s*=\s*"([\d.]+)"'),
                     ("compose_bom", r'composeBom\s*=\s*"([\d.]+)"'),
                     ("compose_adaptive", r'composeAdaptive\s*=\s*"([\d.]+)"'),
                     ("androidx_sqlite", r'sqlite\s*=\s*"([\d.]+)"')]:
    found = re.search(pattern, catalog_text)
    check(f"E10A-13_pin_{key}", found is not None and found.group(1) == str(pins[key]),
          f"catalog={found.group(1) if found else None} contract={pins[key]}")
wrapper_text = read(WRAPPER)
check("E10A-13_pin_gradle", f"gradle-{pins['gradle']}-bin.zip" in wrapper_text,
      f"wrapper does not pin {pins['gradle']}: {wrapper_text.strip().splitlines()[2] if len(wrapper_text.splitlines()) > 2 else wrapper_text}")
check("E10A-13_currency_verified", contract["toolchain"].get("currency_verified") is True
      and contract["toolchain"].get("currency_asserted") is False,
      "currency is verified, not asserted (AMTS-v0 precedent)")
check("E10A-13_verification_date", bool(str(contract["toolchain"].get("verified_on", "")).strip()),
      "the verification date must be recorded")
check("E10A-13_build_corrections_recorded",
      len(contract["toolchain"].get("build_corrections", [])) >= 2
      and all(c.get("found_by") == "running the build"
              for c in contract["toolchain"]["build_corrections"]),
      "corrections the build produced must be recorded rather than quietly fixed")

# ------------------------------------------------------- runs were actually run
runs = {r["id"]: r for r in contract.get("verified_runs", [])}
check("E10A-14_runs_recorded", len(runs) >= 4, f"count={len(runs)}")
check("E10A-14_all_pass", all(r["result"] == "PASS" for r in runs.values()),
      str({k: v['result'] for k, v in runs.items()}))
check("E10A-14_boundary_run", any(r["command"].endswith("verifyModuleBoundaries") for r in runs.values()),
      "the boundary task run must be recorded")
check("E10A-14_no_adapter_run",
      any("-PwithAiAdapter=false" in r["command"] for r in runs.values()),
      "the no-adapter build must be recorded")
check("E10A-14_apk_produced",
      any(r.get("artifact", "").endswith(".apk") for r in runs.values()),
      "V1 criterion 10 needs an installable APK to remain producible")
check("E10A-14_build_not_asserted", contract["acceptance"].get("build_must_be_run_not_asserted") is True,
      "10A must not claim a build result it did not run")

# ------------------------------------------------------------ credentials
gitignore_text = read(GITIGNORE)
for entry in ("android/local.properties", "android/secrets.properties", "*.keystore", "*.jks"):
    check(f"E10A-15_gitignore_{entry}", entry in gitignore_text, f"missing gitignore entry {entry}")
check("E10A-15_no_key_in_catalog",
      not re.search(r"(api[_-]?key|anthropic[_-]?key|sk-ant)", catalog_text, re.I),
      "no key material in the version catalog")
check("E10A-15_key_storage_owner",
      ai_integration["credentials"].get("storage") == "platform_secure_storage"
      and ai_integration["invariants"].get("key_in_platform_secure_storage") is True,
      "AIAX-v0 still owns key storage and 10A did not relax it")
check("E10A-15_no_hardcoded_key_upstream",
      ai_integration["invariants"].get("hardcoded_or_shared_key_in_apk") is False,
      "AIAX-v0 forbids a key in the APK")

# ------------------------------------------------------------------- CI
workflow_text = read(WORKFLOW)
check("E10A-16_workflow_exists", WORKFLOW.is_file(), "missing android workflow")
check("E10A-16_runs_boundaries", "verifyModuleBoundaries" in workflow_text, "CI must run T3")
check("E10A-16_runs_no_adapter_build", "-PwithAiAdapter=false" in workflow_text,
      "CI must build without the adapter")
check("E10A-16_runs_full_validator_glob", "tools/validate_*.py" in workflow_text,
      "the sweep must be the full glob, never a subset (TVSX-v0 RG-11)")
check("E10A-16_no_emulator_as_device_tier",
      contract["ci"].get("runs_device_tier") is False,
      "an emulator job must not be presented as the device tier")

# --------------------------------------------------------- spec and synthesis
for fragment in [
    "**Status:** ACCEPTED — independent 10A QA PASS",
    "**Decision:** `D-082`",
    "Poco M6 Pro",
    "verifyModuleBoundaries",
    "-PwithAiAdapter=false",
    "currency is verified",
]:
    check(f"E10A-17_spec_{fragment[:34]}", fragment in spec_text, f"missing={fragment!r}")
check("E10A-17_spec_records_corrections",
      "9.5" in spec_text and "kotlin.android" in spec_text,
      "the spec must record what the build corrected rather than presenting a clean story")
for fragment in [
    "Poco M6 Pro",
    "currency is **verified, not asserted**",
    "No DI framework",
]:
    check(f"E10A-18_research_{fragment[:30]}", fragment in research_text, f"missing={fragment!r}")

# -------------------------------------------------------------- acceptance
acceptance = contract.get("acceptance", {})
check("E10A-19_required_models",
      {"MSBX-v0", "TVSX-v0", "AMTS-v0", "LFPS-v0", "DDM-v0", "AIAX-v0"}
      <= set(acceptance.get("required_previous_models", [])),
      str(acceptance.get("required_previous_models")))
for key in ("independent_qa_required", "stage6_regression_required", "stage7_regression_required",
            "stage8_regression_required", "stage9_regression_required"):
    check(f"E10A-19_{key}", acceptance.get(key) is True, f"{key}={acceptance.get(key)}")

# ------------------------------------------------------------------ report
passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "MPSX-v0",
    "stage_step": "10A",
    "decision": "D-082",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results),
    "checks_passed": passed,
    "checks_failed": len(failures),
    "modules_verified": len(canonical_modules),
    "kotlin_sources": len(kotlin_sources),
    "checks": results,
    "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"10A_PROJECT_SKELETON_QA={report['result']}")
print(f"checks={passed}/{len(results)} modules={len(canonical_modules)} kotlin_sources={len(kotlin_sources)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not failures else 1)
