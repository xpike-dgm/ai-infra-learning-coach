"""Independent 12F QA — VUSX-v0 Virtual User Scenarios.

The scenario list and the twenty invariants are read out of 3H's own text (`docs/PLANNER_SIMULATION_SUITE.md`),
not out of the contract, and every test the contract names is looked up in the Kotlin suite that should hold it.

The checks that matter most are structural: a virtual user that hands the planner its answer — a gate decision,
an explained trace, a need current state would not open — or a scenario simulated by hand because its code does
not exist, must be **unrepresentable** as a PASS here.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

def schema_versions_owned(schema_text: str) -> bool:
    """Narrowed at 13A: this step added no migration. Any schema version beyond 2 must be declared by the
    accepted later contract that added it (`schema_migration` in its arch yaml), so an unowned move still fails."""
    import glob as _glob
    import yaml as _yaml
    match = re.search(r"const val VERSION = (\d+)", schema_text)
    if not match:
        return False
    version = int(match.group(1))
    owned = set()
    for path in _glob.glob(str(ROOT / "arch" / "*" / "*.yaml")):
        try:
            doc = _yaml.safe_load(open(path, encoding="utf-8"))
        except Exception:
            continue
        migration = doc.get("schema_migration") if isinstance(doc, dict) else None
        if isinstance(migration, dict) and migration.get("from") is not None and migration.get("to") is not None:
            owned.add((int(migration["from"]), int(migration["to"])))
    return all((v - 1, v) in owned for v in range(3, version + 1))


ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/12f_virtual_user_tests/virtual_users.yaml"
SPEC = ROOT / "docs/VIRTUAL_USER_TESTS_SPEC.md"
RESEARCH = ROOT / "research/12f_virtual_user_tests_research.md"
QA_OUT = ROOT / "arch/12f_virtual_user_tests/qa_report.yaml"
SUITE_3H = ROOT / "docs/PLANNER_SIMULATION_SUITE.md"
EXPLANATION_SPEC = ROOT / "docs/PLANNER_EXPLANATION_IMPL_SPEC.md"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

FIXTURE_KT = ANDROID / "core-engines/src/testFixtures/kotlin/coach/engines/virtual/VirtualUsers.kt"
EXPLAIN_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/PlannerExplanation.kt"
COPY_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/ExplanationCopy.kt"
SCREEN_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/PlannerExplanationScreen.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
BUILD = {m: ANDROID / m / "build.gradle.kts" for m in ("core-engines", "core-application", "core-presentation")}

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"(?<![:\"])//[^\n]*", "", source)


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


def test_names(source: str) -> set[str]:
    """Test names declared with @Test, read from backticked function names."""
    return set(re.findall(r"@Test\s+fun `([^`]+)`\(\)", source))


contract = yaml.safe_load(read(CONTRACT))
msbx = yaml.safe_load(read(MSBX))
suite_3h = read(SUITE_3H)
suites = {name: ANDROID.parent / s["file"] for name, s in contract["suites"].items()}

for path in [CONTRACT, SPEC, RESEARCH, FIXTURE_KT, *suites.values()]:
    check(f"E12F-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

fixture = strip_comments(read(FIXTURE_KT))
suite_text = {name: read(path) for name, path in suites.items()}
suite_code = {name: strip_comments(text) for name, text in suite_text.items()}
declared_tests = {name: test_names(text) for name, text in suite_text.items()}
spec_text, research_text = read(SPEC), read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E12F-01_model", contract.get("model") == "VUSX-v0", str(contract.get("model")))
check("E12F-01_status", contract.get("status") == "accepted_12f", str(contract.get("status")))
check("E12F-01_decision", contract.get("decision") == "D-097", str(contract.get("decision")))
for key in ("scenarios_run_against_real_code", "virtual_users_defined_once", "explanation_grouping_changed"):
    check(f"E12F-01_scope_{key}", contract["scope"].get(key) is True, f"{key}={contract['scope'].get(key)}")
for key in ("planner_rule_changed", "gate_rule_changed", "replan_rule_changed", "diagnostic_waiver_implemented",
            "runtime_benchmark_run", "schema_changed", "migration_added", "interfaces_added", "boundaries_changed"):
    check(f"E12F-01_scope_{key}", contract["scope"].get(key) is False, f"{key}={contract['scope'].get(key)}")
check("E12F-01_no_hand_simulation", contract["scope"].get("hand_simulated_scenario") is None, "a scenario was simulated by hand")
check("E12F-01_stage_12_closed", contract.get("stage_12_closed") is True, "AŞAMA 12 is not closed")

# ---------------------------------------------------------------- the scenarios, read out of 3H
scenario_ids = re.findall(r"^## (S\d\d) — ", suite_3h, re.M)
check("E12F-02_3h_read", scenario_ids == [f"S{i:02d}" for i in range(1, 17)], f"3h={scenario_ids}")
check("E12F-02_every_3h_scenario_in_contract", list(contract["scenarios"]) == scenario_ids,
      f"contract={list(contract['scenarios'])}")
not_run = [sid for sid, s in contract["scenarios"].items() if not s.get("run")]
check("E12F-02_only_s06_not_runnable", not_run == ["S06"], f"not run={not_run}")
for sid, scenario in contract["scenarios"].items():
    if scenario.get("run"):
        tests = scenario.get("tests") or {}
        check(f"E12F-02_{sid}_has_tests", bool(tests) and all(tests.values()), f"{sid}: {tests}")
        for suite, names in tests.items():
            for name in names:
                check(f"E12F-02_{sid}_{suite}_{name[:30]}", name in declared_tests.get(suite, set()),
                      f"{sid}: missing test in {suite}: {name}")
    else:
        check(f"E12F-02_{sid}_owned", scenario.get("owner") is not None and bool(scenario.get("why")), f"{sid}: {scenario}")
check("E12F-02_s06_is_vdw", "VDW-v0" in contract["scenarios"]["S06"].get("why", "") and "Partial diagnostic" in suite_3h,
      "S06 is not the diagnostic waiver scenario")

# ---------------------------------------------------------------- the invariants, read out of 3H §5
invariant_rows = re.findall(r"^\| (\d+) \| ([^|]+) \| ([^|]+) \| (PASS\*?) \|", suite_3h, re.M)
check("E12F-03_3h_invariants_read", [int(r[0]) for r in invariant_rows] == list(range(1, 21)), f"rows={len(invariant_rows)}")
cross = contract["cross_checks"]
for number in range(1, 21):
    covered = contract["invariants"].get(number)
    if isinstance(covered, list):
        resolvable = [c for c in covered if (c in contract["scenarios"] and contract["scenarios"][c].get("run")) or c in cross]
        check(f"E12F-03_invariant_{number:02d}_covered", covered and resolvable == covered, f"{number}: {covered}")
    elif number == 12:
        check("E12F-03_invariant_12_named", covered.get("not_runnable") is True and covered.get("with") == "S06"
              and covered.get("owner") is not None and covered.get("owner") == contract["scenarios"]["S06"].get("owner"), str(covered))
    elif number == 17:
        check("E12F-03_invariant_17_structural", set(covered.get("structural", [])) <= set(cross)
              and covered.get("runtime_owner") == "18E" and "PASS*" in dict((r[0], r[3]) for r in invariant_rows)["17"], str(covered))
    else:
        check(f"E12F-03_invariant_{number:02d}_covered", False, f"{number}: {covered}")
for key, c in cross.items():
    check(f"E12F-03_cross_{key}", c["test"] in declared_tests.get(c["suite"], set()), f"missing: {c}")

# ---------------------------------------------------------------- a virtual user is state, never an answer
decisions = re.search(r"val decisions: Map<String, PrerequisiteDecision>\s*get\(\) \{(.*?)\n            \}", fixture, re.S)
check("E12F-04_decisions_from_the_gate", decisions is not None and "PrerequisiteEngine.decide(" in decisions.group(1)
      and "PrerequisiteEngine.requirements(" in decisions.group(1) and "PrerequisiteEngine.readiness(" in decisions.group(1),
      "gate decisions do not come from the real gate")
check("E12F-04_no_hand_made_decision", "PrerequisiteDecision(" not in fixture
      and all("PrerequisiteDecision(" not in code for code in suite_code.values()), "a gate decision is written by hand")
check("E12F-04_needs_from_state", "PlannerEngine.needsFromSkillStates(learners.map { it.planning }) + ownerNeeds" in fixture,
      "needs do not come from state")
owner_triggers = set(re.findall(r"ownerNeed\(NeedTrigger\.(\w+)", fixture))
allowed = {t.upper() for t in contract["fixtures"]["owner_supplied_needs_allowed"]}
check("E12F-04_owner_needs_allowed_only", owner_triggers and owner_triggers <= allowed, f"owner needs={sorted(owner_triggers)}")
check("E12F-04_plan_from_the_planner", "fun plan(): PlanTrace = PlannerEngine.plan(capacity, needs, candidates, decisions," in fixture,
      "the plan does not come from the planner")
check("E12F-04_no_hand_made_trace_explained", "PlanTrace(" not in suite_code["explanation"]
      and "VirtualUsers.s0" in suite_code["explanation"] and "ReplanEngine.composeReentry(" in suite_code["explanation"],
      "an explained trace is written by hand")
check("E12F-04_journeys_through_use_cases", "BuildDailyPlan(" in suite_code["journey"] and "TodayFactsQuery(" in suite_code["journey"]
      and "PlannerEngine.plan(" not in suite_code["journey"], "journeys bypass the use cases")
check("E12F-04_journeys_read_no_history",
      'override fun evidenceFor(objective: VersionedRef): List<EvidenceRow> = error(' in suite_code["journey"],
      "a journey store lets planning read evidence")
check("E12F-04_no_learner_state_written", 'override fun writeProjection(record: ProjectionRecord) = error(' in suite_code["journey"],
      "a journey store lets planning write learner state")

# ---------------------------------------------------------------- fixtures on an allowed edge
engines_build = read(BUILD["core-engines"])
check("E12F-05_fixtures_plugin", "`java-test-fixtures`" in engines_build, "core-engines has no test fixtures")
for module in ("core-application", "core-presentation"):
    text = read(BUILD[module])
    check(f"E12F-05_fixtures_used_by_{module}", 'testImplementation(testFixtures(project(":core-engines")))' in text,
          f"{module} does not use the virtual users")
    allowed_deps = next(m["depends_on"] for m in msbx["modules"] if m["id"] == module)
    check(f"E12F-05_edge_allowed_{module}", "core-engines" in allowed_deps, f"{module} may not depend on core-engines")
check("E12F-05_no_edge_added", contract["fixtures"]["dependency_edges_added"] == [], str(contract["fixtures"]["dependency_edges_added"]))
root_build = strip_comments(read(ANDROID / "build.gradle.kts"))
graph_reader = root_build[root_build.find("val graph: Map<String, List<String>>"):root_build.find("doLast {")]
check("E12F-05_boundary_check_drops_only_self", ".filter { it != sub.path }" in graph_reader
      and graph_reader.count(".filter {") == 2 and "dependency cycle" in root_build,
      "the boundary check drops more than a module's reference to itself")
refined = contract.get("boundary_check_refined", {})
check("E12F-05_boundary_refinement_recorded", refined.get("real_cycle_still_fails") is True
      and refined.get("edges_to_other_modules_dropped") is False and bool(refined.get("negative_verification")), str(refined))

# ---------------------------------------------------------------- what running them found
explain = strip_comments(read(EXPLAIN_KT))
check("E12F-06_notToday_grouped", "}.let { grouped(it, recorded) }" in explain, "not-today needs are not grouped")
grouped_body = explain[explain.find("private fun grouped("):explain.find("/** Why the need exists at all")]
check("E12F-06_group_key", "listOf(it.need.code, it.need.fact, it.whyNot.code, it.whyNot.fact, it.whyNot.skills, it.reconsideration)" in grouped_body,
      "the grouping key is not the recorded reason")
check("E12F-06_nothing_dropped", "needKeys = group.flatMap { it.needKeys }" in grouped_body and "group.flatMap { it.skills }.distinct()" in grouped_body,
      "grouping drops a need or a Skill")
check("E12F-06_need_keys_list", "val needKeys: List<String>," in explain, "an entry cannot hold several needs")
check("E12F-06_screen_short_names", "ExplanationCopy.shortNames(item.skills)" in read(SCREEN_KT)
      and "fun shortNames(skills: List<SkillMention>, shown: Int = 3)" in read(COPY_KT), "the screen lists every Skill")
check("E12F-06_12e_amended_explicitly", "12F amendment (`VUSX-v0 / D-097`" in read(EXPLANATION_SPEC), "12E's spec changed silently")
found = {f["id"] for f in contract["found_in_12f"]}
check("E12F-06_findings_recorded", {"due_inventory_listed_row_by_row", "s07_example_day_is_illustrative", "test_fixtures_read_as_a_self_cycle",
                                    "diagnostic_waiver_unowned", "scenario_tests_weaker_than_they_looked"} <= found, str(found))
check("E12F-06_s07_both_days", sum(1 for n in contract["scenarios"]["S07"]["tests"]["engine"] if "S07" in n) == 2
      and "fun s07(otherDueReviews: Int = 78, reviewsHaveTasks: Boolean = false)" in fixture, "S07 is tested one way only")

# ---------------------------------------------------------------- ports and storage unchanged
ports = strip_comments(read(PORTS_KT))
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
check("E12F-07_port_count", sorted(interfaces) == sorted(p["id"] for p in msbx["ports"]["set"]), f"interfaces={interfaces}")
check("E12F-07_schema_version_unchanged", schema_versions_owned(read(SCHEMA_KT)), "the schema version moved without an owning contract")

# ---------------------------------------------------------------- honesty
device = contract["device_verification"]
check("E12F-08_device_not_claimed", device["t6_run"] is False and device["claimed"] is False, "a device result is claimed")
mutation = contract["mutation_results"]
check("E12F-08_mutation_all_detected", mutation["detected"] == mutation["total"] == len(mutation.get("mutants", [])) >= 25,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E12F-08_mutation_suites_only", mutation.get("only_virtual_user_suites_run") is True, "mutants were caught by other tests")
check("E12F-08_mutation_ran_gradle", mutation.get("harness_verified_to_run_gradle") is True, "the harness was not verified")
check("E12F-08_mutation_negative_control", mutation.get("negative_control_result") == "survived_as_expected", "no control")
vm = contract["validator_mutation"]
check("E12F-08_validator_mutation", vm["detected"] == vm["total"] >= 20 and vm.get("negative_control_result") == "no_false_positive", str(vm))
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E12F-08_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
for item, owner in (("diagnostic_waiver_and_s06", 13), ("starvation_and_track_balance_thresholds", "18C"),
                    ("runtime_performance_budgets", "18E"), ("calling_the_planner_from_the_app", "16D")):
    check(f"E12F-08_re_pointed_{item[:30]}", str(contract["re_pointed"].get(item, {}).get("to")) == str(owner)
          and contract["re_pointed"][item].get("why"), f"{item} is not re-pointed with a reason")
forbidden = set(contract.get("forbidden_virtual_user_patterns", []))
for pattern in ("fixture_writes_the_answer", "hand_made_gate_decision", "hand_made_explained_trace", "hand_simulated_scenario",
                "due_inventory_shown_as_a_list", "runtime_claim_without_device", "rule_changed_to_fit_an_illustrative_example"):
    check(f"E12F-09_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 12F QA PASS", "**Decision:** `D-097`",
                 "A virtual user is state, never an answer", "S06", "T6", "MUTATION"]:
    present = fragment in spec_text if fragment != "MUTATION" else "MUTATION_SUMMARY_PLACEHOLDER" not in spec_text and "Mutation 27/27" in spec_text
    check(f"E12F-10_spec_{fragment[:26]}", present, f"spec: {fragment!r}")
for fragment in ["No web research pass was needed", "3H", "SRR-v0", "VDW-v0"]:
    check(f"E12F-11_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E12F-12_no_repeated_context_heading", len(context_headings) == len(set(context_headings)),
      f"repeated={[h for h in set(context_headings) if context_headings.count(h) > 1]}")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E12F-12_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)),
      f"repeated={sorted(h for h in set(plan_steps) if plan_steps.count(h) > 1)}")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "VUSX-v0", "stage_step": "12F", "decision": "D-097",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"12F_VIRTUAL_USER_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
