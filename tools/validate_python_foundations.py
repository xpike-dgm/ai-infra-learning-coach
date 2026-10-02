"""Independent 15B QA — PYFX-v0 Python Foundations content.

The rules are read from the accepted contracts — FBB-v0 §6.2 and §16 (the Python seeds and the authoring roles every
published Objective needs), 6C's decomposition (seed mappings, identity, Objectives, edges), KGC-v0 §27-§28 (lifecycle,
graph invariants), AIV-v0 §2-§3 and §23-§25 (an AI-generated item is never trusted by itself), QAB-v0 §15, §19 and §21
(variant families, keys, tools), CDEX-v0 (a code item is judged by the course's own tests), GRE-v0's two-family gate,
3B §3 and §15 (tasks), LFPS-v0 (a published version is never overwritten) — and compared with the content source, both
shipped packages, the test suites and the real Kotlin. The second package is rebuilt here from its source and must be
byte-identical to what ships, and so must every test suite, so nothing the learner meets can differ from what was checked.

What this step exists to prevent must be unrepresentable as a PASS: a code item whose tests a plausible wrong solution
passes or whose reference fails them, a key not checked against what really happens, an AI-generated item validated
without an independent verdict, a hidden prerequisite, a lesson that teaches a wrong output, a Python Skill that could
never become ready, a second package that re-carries or overwrites what the first published, and a course whose correct
answers fail on the learner's own computer.
"""

from __future__ import annotations

import importlib.util
import json
from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTENT = ROOT / "curriculum/content/15b_python_foundations"
FIRST_CONTENT = ROOT / "curriculum/content/15a_computing_fundamentals"
DECOMP = ROOT / "curriculum/decomposition/6c_foundations"
ASSET = ANDROID / "app-wiring/src/main/assets/curriculum_package_v2.txt"
FIRST_ASSET = ANDROID / "app-wiring/src/main/assets/curriculum_package.txt"

CONTRACT = ROOT / "arch/15b_python_foundations/python_foundations.yaml"
VERIFICATION = ROOT / "arch/15b_python_foundations/content_verification.yaml"
SPEC = ROOT / "docs/PYTHON_FOUNDATIONS_CONTENT_SPEC.md"
RESEARCH = ROOT / "research/15b_python_foundations_research.md"
QA_OUT = ROOT / "arch/15b_python_foundations/qa_report.yaml"
FBB = ROOT / "docs/V1_FOUNDATION_BACKBONE.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
RUNNER = ROOT / "tools/code_test_runner.py"

STORE_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/CurriculumStore.kt"
SOURCE_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
INGEST_KT = ANDROID / "core-application/src/main/kotlin/coach/application/DailyMicroAssessment.kt"
APP_KT = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
WIRING_GRADLE = ANDROID / "app-wiring/build.gradle.kts"
PUBLISH_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/CurriculumPublishingTest.kt"
FORMAT_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/PackageFormatTest.kt"
SHIPPED_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/ShippedPythonPackageTest.kt"
INGEST_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/DailyMicroAssessmentTest.kt"
COURSE_TEST = ANDROID / "app-wiring/src/test/kotlin/coach/wiring/ShippedCourseTest.kt"

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def load(path: Path):
    return yaml.safe_load(read(path)) if path.is_file() else None


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"(?<![:\"])//[^\n]*", "", source)


def body(source: str, start: str) -> str:
    i = source.find(start)
    if i < 0:
        return ""
    depth, j, opened = 0, source.find("{", i), False
    while 0 <= j < len(source):
        if source[j] == "{":
            depth, opened = depth + 1, True
        elif source[j] == "}":
            depth -= 1
            if opened and depth == 0:
                return source[i:j + 1]
        j += 1
    return ""


def tag(oid: str) -> str:
    return f"{oid.rsplit('.', 2)[-2]}_{oid.rsplit('.', 1)[-1]}"


# ---------------------------------------------------------------- inputs
for path in (CONTRACT, VERIFICATION, SPEC, RESEARCH, ASSET, FIRST_ASSET, STORE_KT, SOURCE_KT, PORTS_KT, INGEST_KT, APP_KT,
             PUBLISH_TEST, FORMAT_TEST, SHIPPED_TEST, INGEST_TEST, COURSE_TEST, RUNNER, CONTENT / "package.yaml",
             CONTENT / "notation.yaml", CONTENT / "independent_review.yaml", ROOT / "tools/build_curriculum_package.py"):
    check(f"E15B-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
pkg = load(CONTENT / "package.yaml") or {}
notation = load(CONTENT / "notation.yaml") or {}
review = load(CONTENT / "independent_review.yaml") or {}
verification = load(VERIFICATION) or {}
skill_docs = {d["skill"]: d for d in (load(p) for p in sorted((CONTENT / "skills").glob("*.yaml"))) if d}
first_ids = [s["id"] for s in (load(FIRST_CONTENT / "package.yaml") or {}).get("skills", [])]
skills6c = {s["skill_id_candidate"]: s for s in (load(DECOMP / "skills.yaml") or [])}
objectives6c = {o["objective_id_candidate"]: o for o in (load(DECOMP / "objectives.yaml") or [])}
edges6c = load(DECOMP / "prerequisite_edges.yaml") or []
seeds6c = load(DECOMP / "seed_mappings.yaml") or []
asset = read(ASSET)
first_asset = read(FIRST_ASSET)

check("E15B-01_model", contract.get("model") == "PYFX-v0", str(contract.get("model")))
check("E15B-01_status", contract.get("status") == "accepted_15b", str(contract.get("status")))
check("E15B-01_decision", contract.get("decision") == "D-113", str(contract.get("decision")))
ud = contract.get("user_decisions", {})
check("E15B-01_user_decisions", ud.get("scope") == "seventeen_seeds_plus_three_prerequisites"
      and ud.get("shipping") == "incremental_packages_entity_semantic_version", str(ud))

# ---------------------------------------------------------------- the package that ships is the package that was checked
spec = importlib.util.spec_from_file_location("builder", ROOT / "tools/build_curriculum_package.py")
builder = importlib.util.module_from_spec(spec)
spec.loader.exec_module(builder)
rebuilt, report = builder.build(CONTENT)
suite_files = report.pop("suite_files", {})
check("E15B-02_shipped_is_rebuilt", rebuilt == asset, "the shipped asset differs from a fresh build of its source")
check("E15B-02_generated_marker", asset.splitlines()[1:2] == ["# Generated by tools/build_curriculum_package.py from curriculum/content/15b_python_foundations — do not edit by hand."]
      if asset else False, "")
check("E15B-02_no_failures", report["failures"] == [], f"{len(report['failures'])} failures: {[f['item'] for f in report['failures']][:5]}")
check("E15B-02_report_recorded", verification.get("summary") == report["summary"] and verification.get("items") == report["items"],
      "arch/15b_python_foundations/content_verification.yaml is not the report of this build")
on_disk = {p.stem: p.read_text(encoding="utf-8") for p in sorted((CONTENT / "suites").glob("*.json"))}
check("E15B-02_suites_are_rebuilt", on_disk == suite_files and len(on_disk) == contract.get("item_counts", {}).get("code"),
      f"{len(on_disk)} on disk, {len(suite_files)} built")
first_rebuilt, _ = builder.build(FIRST_CONTENT)
check("E15B-02_first_package_untouched", first_rebuilt == first_asset, "15A's shipped package is no longer a fresh build of its source")

# ---------------------------------------------------------------- graph: FBB-v0 §6.2 as 6C decomposed it, plus its closure
fbb = read(FBB)
fbb_section = fbb[fbb.find("## 6.2 Python skills"):fbb.find("## 6.3 C / memory skills")]
fbb_seeds = re.findall(r"`(skill\.python\.[a-z_]+)`", fbb_section)
mapped = sorted({r for m in seeds6c if m["seed_kind"] == "skill" and m["seed_id"] in fbb_seeds for r in m["result_entity_refs"]})
ids = [s["id"] for s in pkg.get("skills", [])]
added = sorted(set(ids) - set(mapped))
check("E15B-03_fbb_seeds", len(fbb_seeds) == 12, str(fbb_seeds))
check("E15B-03_seeds_as_6c_decomposed", sorted(contract.get("seed_skills", [])) == mapped and len(mapped) == 17 and set(mapped) <= set(ids), f"{mapped}")
check("E15B-03_added_are_declared", added == sorted(contract.get("added_prerequisite_skills", [])) and len(ids) == 20, f"added={added}")
hard_into = {}
for e in edges6c:
    if e["edge_kind"] == "hard":
        hard_into.setdefault(e["target_skill_id"], set()).add(e["prerequisite_skill_id"])
check("E15B-03_added_are_prerequisites", all(any(a in hard_into.get(s, ()) for s in mapped) for a in added),
      "every added Skill is a hard prerequisite of a seed Skill")
known = set(ids) | set(first_ids)
open_hard = sorted((p, t) for t in ids for p in hard_into.get(t, ()) if p not in known)
check("E15B-03_closed_under_hard_prerequisites", open_hard == [], f"a Skill that could never become ready: {open_hard}")
check("E15B-03_content_per_skill", sorted(skill_docs) == sorted(ids), "")
own_objectives = sorted(o["id"] for d in skill_docs.values() for o in d["objectives"])
sixc_objectives = sorted(k for k, o in objectives6c.items() if o["owner_skill_id"] in ids)
check("E15B-03_objective_identity", own_objectives == sixc_objectives and len(own_objectives) == 21, f"{own_objectives}")
expected_edges = sorted((e["prerequisite_skill_id"], e["target_skill_id"], e["edge_kind"]) for e in edges6c
                        if e["target_skill_id"] in ids and e["prerequisite_skill_id"] in known)
pkg_edges = re.findall(r"\[prerequisite_edge\]\nprerequisite=(\S+)@v1\ntarget=(\S+)@v1\nedge_version=1\nedge_kind=(\w+)\n"
                       r"reason_kind=\w+\nstrictness_profile=\w+\nlifecycle_status=(\w+)", asset)
check("E15B-03_edges_ratified", sorted((p, t, k) for p, t, k, _ in pkg_edges) == expected_edges
      and len(pkg_edges) == contract.get("scope", {}).get("edges") and all(l == "published" for *_, l in pkg_edges), f"{len(pkg_edges)} edges")
check("E15B-03_builds_on_15a", sum(1 for p, *_ in pkg_edges if p in first_ids) == contract.get("scope", {}).get("edges_from_15a"), "")
pkg_skill_status = re.findall(r"\[skill\]\nlogical_id=(\S+)\n(?:.*\n){3}lifecycle_status=(\w+)", asset)
check("E15B-03_skills_published", len(pkg_skill_status) == 20 and all(s == "published" for _, s in pkg_skill_status), str(pkg_skill_status[:2]))
seen, stack, cyclic = set(), set(), []


def visit(n):
    if n in stack:
        cyclic.append(n)
        return
    if n in seen:
        return
    seen.add(n)
    stack.add(n)
    for p in hard_into.get(n, ()):
        if p in known:
            visit(p)
    stack.discard(n)


for n in ids:
    visit(n)
check("E15B-03_hard_dag", cyclic == [], str(cyclic))
waits_only_on_15a = sorted(s for s in ids if not (hard_into.get(s, set()) & set(ids)))
check("E15B-03_python_entry_points", waits_only_on_15a == contract.get("python_entry_skills"), f"{waits_only_on_15a}")

# ---------------------------------------------------------------- Objective reconciliation
expected_direct = contract.get("required_direct_types", {})
for d in skill_docs.values():
    for o in d["objectives"]:
        oid = o["id"]
        six = objectives6c.get(oid, {})
        ok = all(o.get(k) for k in ("statement", "observable_behavior", "reconciliation"))
        check(f"E15B-04_recorded_{tag(oid)}", ok and o.get("required_direct_type") == expected_direct.get(oid)
              == six.get("evidence_profile", {}).get("required_direct_type")
              and set(o["acceptable_evidence_types"]) == set(six.get("evidence_profile", {}).get("acceptable_evidence_types", [])),
              f"{oid}: {o.get('required_direct_type')}")
statements = [o["statement"] for d in skill_docs.values() for o in d["objectives"]]
check("E15B-04_no_template_statement", len(set(statements)) == len(statements) and not any("Yeni bir bağlamda" in s for s in statements), "")

# ---------------------------------------------------------------- items: executed, reviewed, measurable, fair
items = report["items"]
item_review = (review or {}).get("items", {}) or {}
check("E15B-05_review_method", bool((review or {}).get("method")) and review.get("reviewer") == "independent_15b_content_review", "")
check("E15B-05_every_item_reviewed", sorted(item_review) == sorted(i["item"] for i in items), f"{len(item_review)} reviewed / {len(items)} items")
check("E15B-05_every_item_passed_review", all(v.get("verdict") == "pass" for v in item_review.values()),
      str([k for k, v in item_review.items() if v.get("verdict") != "pass"][:5]))
check("E15B-05_every_item_validated", all(i["status"] == "validated" for i in items), "")
check("E15B-05_no_hidden_prerequisite", all(not i["hidden_prerequisites"] for i in items), "")
modes = {i["verify_mode"] for i in items}
check("E15B-05_known_modes", modes <= {"stdout", "probe", "traceback", "suite"}, str(modes))
code = [i for i in items if i["verify_mode"] == "suite"]
reading = [i for i in items if i["verify_mode"] != "suite"]
counts = contract.get("item_counts", {})
check("E15B-05_counts", (len(items), len(code), len(reading)) == (counts.get("total"), counts.get("code"), counts.get("reading")),
      f"{len(items)} {len(code)} {len(reading)}")
check("E15B-05_code_executed_against_wrong", all(i["verification"].startswith("executed with the course runner; the reference passes")
      and re.search(r"each of ([2-9]|\d\d) wrong solutions fails", i["verification"]) for i in code), "")
for iid, text in suite_files.items():
    suite = json.loads(text)
    # A program (or a harness reading one) with a fixed output has one honest test; anything that takes input is tried on
    # several inputs.
    fixed = len(suite["tests"]) == 1 and not suite["tests"][0].get("args") and not suite["tests"][0].get("stdin")
    check(f"E15B-05_suite_{iid.split('.', 2)[2]}", suite["format"] == "code_test_suite/1" and suite["build"]["command"][:3] == ["{python}", "-m", "py_compile"]
          and (len(suite["tests"]) >= 2 or fixed) and suite["timeout_seconds"] == 5,
          f"{len(suite['tests'])} tests")
item_rows = {m.group(1): m.group(0) for m in re.finditer(r"\[item\]\nref=(\S+)@v1\n(?:[^\n]+\n)+", asset + "\n")}
for d in skill_docs.values():
    for o in d["objectives"]:
        oid = o["id"]
        measuring = [r for r in item_rows.values() if f"target_objectives={oid}@v1\n" in r and "deterministic_verification=true" in r
                     and f"evidence_type={o['required_direct_type']}\n" in r]
        families = {re.search(r"variant_family_id=(\S+)", r).group(1) for r in measuring}
        transfer = [r for r in measuring if "difficulty_class=transfer_integration" in r]
        check(f"E15B-05_measurable_{tag(oid)}", len(families) >= 4 and transfer != [], f"{len(families)} families, {len(transfer)} transfer")
check("E15B-05_ai_origin_declared", "content_origin=human_authored" not in asset and asset.count("content_origin=ai_generated") > 0, "")
check("E15B-05_ceiling_not_trusted", "status=trusted" not in asset and "lifecycle_status=trusted" not in asset, "")
code_rows = [r for r in item_rows.values() if "evaluator_policy_version=CDEX-v0/code_tests" in r]
check("E15B-05_code_tools", len(code_rows) == counts.get("code") and all("allowed_tools=terminal,documentation\n" in r
      and "prohibited_solution_sources=external_ai\n" in r and "evidence_type=authored_code\n" in r for r in code_rows), "")
check("E15B-05_reading_not_runnable", all("prohibited_solution_sources=terminal,compiler,debugger,external_ai" in r
      for r in item_rows.values() if r not in code_rows), "")

# ---------------------------------------------------------------- the learner's own computer
runner = read(RUNNER)
check("E15B-06_runner_utf8", '[sys.executable, "-X", "utf8"]' in runner, "the runner does not run Python in UTF-8 mode")
check("E15B-06_file_tests_read_text", all("read_bytes().decode" not in t for t in suite_files.values()),
      "a test reads a written file as bytes, so a correct solution fails on Windows (CRLF)")
# A path result is a name, a suffix, a boolean or an as_posix() string — never the platform's own separator.
path_refs = [it["reference"] for o in skill_docs.get("skill.python.path_handling", {}).get("objectives", []) for it in o["items"]]
check("E15B-06_path_results_portable",
      all("\\" not in test["expected_stdout"] for k, t in suite_files.items() if ".path_handling." in k for test in json.loads(t)["tests"])
      and len(path_refs) == 5 and all(re.search(r"\.(as_posix\(\)|suffix|stem|name)\s*$|\.parent ==", r.strip()) for r in path_refs),
      "a path result depends on the operating system's separator")

# ---------------------------------------------------------------- lessons
expl_review = (review or {}).get("explanations", {}) or {}
expl_ids = re.findall(r"\[explanation\]\nlogical_id=(\S+)", asset)
check("E15B-07_every_explanation_reviewed", sorted(expl_review) == sorted(expl_ids), f"{len(expl_review)} / {len(expl_ids)}")
check("E15B-07_every_explanation_passed", all(v.get("verdict") == "pass" for v in expl_review.values()),
      str([k for k, v in expl_review.items() if v.get("verdict") != "pass"][:5]))
checks_run = report.get("explanation_checks", [])
check("E15B-07_lesson_claims_executed", len(checks_run) >= contract.get("lesson_checks_min", 1) and all(c["result"] == "PASS" for c in checks_run),
      f"{len(checks_run)} checks")
for d in skill_docs.values():
    for o in d["objectives"]:
        check(f"E15B-07_lesson_{tag(o['id'])}", bool(o["explanations"].get("canonical")) and "worked_example" in o["explanations"]
              and "prerequisite_refresh" in o["explanations"]
              and len(o.get("misconceptions", [])) >= 2 and all(m["open_question"].rstrip().endswith("?") for m in o["misconceptions"]), "")
COMBINING_DOT = chr(0x0307)
check("E15B-07_no_combining_dot", COMBINING_DOT not in asset and all(COMBINING_DOT not in t for t in suite_files.values()),
      "a combining dot above (U+0307) reached the content")

# ---------------------------------------------------------------- notation
introducers = {s for c in notation.get("constructs", {}).values() for s in c["introduced_by"]}
check("E15B-08_introducers_known", introducers <= known, str(introducers - known))
never = sorted(c for c, v in notation.get("constructs", {}).items() if not v["introduced_by"])
check("E15B-08_forbidden_constructs", never == sorted(contract.get("never_introduced", [])), str(never))

# ---------------------------------------------------------------- tasks
tasks = re.findall(r"\[task\]\nlogical_id=task\.(\S+)\.(teach|practice|check|review|repair)\n(?:[^\n]+\n)+", asset + "\n")
check("E15B-09_five_per_skill", len(tasks) == 100 and all(sum(1 for t in tasks if t[0] == s.split(".", 1)[1]) == 5 for s in ids), f"{len(tasks)}")
purposes = dict(re.findall(r"logical_id=task\.\S+\.(teach|practice|check|review|repair)\n(?:[^\n]+\n){4}purpose=(\w+)", asset))
check("E15B-09_task_purposes", purposes == {"teach": "teach", "practice": "practice", "check": "assess", "review": "retain", "repair": "remediate"}, str(purposes))
check("E15B-09_tasks_validated", all(t["status"] == "validated" for t in report["tasks"]), "")
minutes = [int(m) for m in re.findall(r"\[task\]\n(?:[^\n]+\n)*?cost_minutes=(\d+)", asset)]
check("E15B-09_minutes_are_estimates", contract.get("task_minutes", {}).get("calibrated") is False and all(0 < m <= 20 for m in minutes), str(minutes[:5]))

# ---------------------------------------------------------------- incremental shipping (user decision)
store_kt = strip_comments(read(STORE_KT))
check("E15B-10_store_refuses_republished", "republishedEntities(curriculum)" in store_kt and "versionMismatches" not in store_kt
      and "is already published and is never overwritten" in store_kt, "")
check("E15B-10_store_never_overwrites", "if (versionExists(curriculum.version)) return PublishOutcome.AlreadyPublished" in store_kt, "")
source_kt = strip_comments(read(SOURCE_KT))
check("E15B-10_source_reads_all_or_none", "(listOf(first) + later()).map(PackageFormat::parse).also(::checkSequence)" in source_kt
      and "onFailure { failure = it }.getOrNull()" in source_kt, "")
seq = body(source_kt, "private fun checkSequence(")
check("E15B-10_versions_rise", "b.curriculum.version <= a.curriculum.version" in seq, "")
check("E15B-10_authored_once", all(f'once("{k}"' in seq for k in ("item", "explanation", "task", "code test suite", "answer key", "rubric")), "")
ports_kt = strip_comments(read(PORTS_KT))
check("E15B-10_port_refinement", "fun curriculumPackages(): List<CurriculumPackage> = listOfNotNull(curriculumPackage())" in ports_kt, "")
ingest = body(strip_comments(read(INGEST_KT)), "fun ingestAll()")
check("E15B-10_ingest_oldest_first_stops", "sortedBy { it.version }" in ingest and "if (outcome is PublishOutcome.Refused) break" in ingest, "")
app_kt = read(APP_KT)
check("E15B-10_app_reads_later_packages", 'Regex("^curriculum_package_v(\\\\d+)\\\\.txt$")' in app_kt and "ingestAll()" in app_kt
      and "FileContentSource(later = ::laterPackages, source = ::authoredPackage)" in app_kt, "")
v1_ids = set(re.findall(r"^logical_id=(\S+)$", first_asset, re.M))
v2_ids = set(re.findall(r"^logical_id=(\S+)$", asset, re.M))
check("E15B-10_nothing_carried_again", v1_ids & v2_ids == set(), str(sorted(v1_ids & v2_ids)[:5]))
check("E15B-10_entities_version_1", re.findall(r"^version=(\d+)$", asset, re.M)[0] == "2"
      and set(re.findall(r"^version=(\d+)$", asset, re.M)[1:]) == {"1"} and "@v2" not in asset, "")
check("E15B-10_jvm_driver_for_tests_only", 'testRuntimeOnly("androidx.sqlite:sqlite-bundled-jvm:' in read(WIRING_GRADLE)
      and 'implementation("androidx.sqlite:sqlite-bundled-jvm' not in read(WIRING_GRADLE), "")

# ---------------------------------------------------------------- tests
for path, names in ((PUBLISH_TEST, ["a later package that carries an entity already published is refused whole and writes nothing",
                                    "a later package brings new entities at their own version 1 and builds on what is published"]),
                    (FORMAT_TEST, ["later packages are served with the first as one course, and published oldest first",
                                   "packages that do not rise in version, or author something twice, are not served at all"]),
                    (INGEST_TEST, ["every package is published once, oldest first, and a refusal stops the ones built on it"]),
                    (SHIPPED_TEST, ["everything shipped was validated, so nothing is a candidate",
                                    "every item is judged by exactly one thing, and every code item by tests the course runs"]),
                    (COURSE_TEST, ["both packages are published into the real store, oldest first, and a second start publishes nothing",
                                   "with the Python package, a learner who has done nothing is still offered only the two entry lessons"])):
    text = read(path)
    for name in names:
        check(f"E15B-11_test_{name[:40]}", f"`{name}`" in text, f"{path.name}: {name}")
check("E15B-11_course_test_reads_assets", 'File("src/main/assets/curriculum_package_v2.txt")' in read(COURSE_TEST)
      and "SqlitePersistence.open(" in read(COURSE_TEST) and "BuildDailyPlan(" in read(COURSE_TEST), "")

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results") if isinstance(contract.get("mutation_results"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("E15B-12_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("E15B-12_compile_failure_not_detection", mr.get("compile_failure_is_detection") is False and mr.get("final_run_is_a_single_clean_run") is True, "")
check("E15B-12_control", mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("E15B-12_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("E15B-12_runs_pass", len(runs) >= 5 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") if isinstance(contract.get("device_verification"), dict) else {}
check("E15B-12_t6_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("sqlite_publish_on_jvm_run") is True
      and dv.get("sqlite_publish_on_device_run") is False, "")
gates = {g["file"]: g["checks"] for g in contract.get("narrowed_gates", [])}
check("E15B-12_narrowed_gates", gates == contract.get("expected_narrowed_gates"), str(gates))
for f in gates:
    check(f"E15B-12_narrowing_declared_{Path(f).stem}", "15B" in read(ROOT / f) and "D-113" in read(ROOT / f), f)
check("E15B-12_open_loops", set(contract.get("open_loops", {})) == {"graph_requirements_declared_per_item", "fixed_output_items", "notation_coverage",
      "path_objective_action_name", "unicode_normal_forms", "requires_transfer_not_stored", "runner_presents_tasks",
      "estimate_calibration", "t6_and_device_ingestion"}, str(sorted(contract.get("open_loops", {}))))

# ---------------------------------------------------------------- spec, research, decision
spec_text = read(SPEC)
check("E15B-13_spec_status", "**Status:** ACCEPTED — independent 15B QA PASS" in spec_text and "`D-113`" in spec_text, "")
check("E15B-13_spec_next", "**15C — C Foundations**" in spec_text, "")
check("E15B-13_spec_t6", "**Not run: T6.**" in spec_text, "")
research = read(RESEARCH)
check("E15B-13_research", all(u in research for u in ("docs.python.org/3/library/stdtypes.html", "docs.python.org/3/library/functions.html",
      "docs.python.org/3/library/pathlib.html", "docs.python.org/3/library/os.html")), "")
check("E15B-13_decision", re.search(r"^#+ .*D-113", read(DECISIONS), re.M) is not None, "D-113 heading in DECISIONS")

passed = sum(1 for r in results if r["result"] == "PASS")
out = {"model": "PYFX-v0", "stage_step": "15B", "decision": "D-113", "result": "PASS" if not failures else "FAIL",
       "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
if "--no-write" not in sys.argv:
    QA_OUT.parent.mkdir(parents=True, exist_ok=True)
    QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"15B QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
for failure in failures:
    print("  FAIL", failure)
sys.exit(0 if not failures else 1)
