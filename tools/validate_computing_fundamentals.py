"""Independent 15A QA — CPFX-v0 Computer / Programming Fundamentals content.

The rules are read from the accepted contracts — FBB-v0 §6.1 and §16 (the subgraph and the authoring roles every
published Objective needs), 6C's decomposition (identity, Objectives, edges), KGC-v0 §27-§28 (lifecycle, graph
invariants), AIV-v0 §2-§3 and §23-§25 (an AI-generated item is never trusted by itself; generator output is not
validation proof), QAB-v0 §15 and §19 (variant families, answer keys), GRE-v0's two-family gate, 3B §3 and §15 (tasks)
— and compared with the content source, the shipped package and the real Kotlin. The package is rebuilt here from
its source and must be byte-identical to what ships, so nothing in the app can differ from what was checked.

What this step exists to prevent must be unrepresentable as a PASS: an item whose key was not checked against what
really happens, an AI-generated item validated without an independent verdict, a hidden prerequisite, a lesson that
teaches a wrong output, a task that serves a need its purpose may not serve or claims more trust than its items, a
Skill or Objective whose identity drifted from 6C, and a draft subgraph left off the learner's route.
"""

from __future__ import annotations

import copy
import importlib.util
from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTENT = ROOT / "curriculum/content/15a_computing_fundamentals"
DECOMP = ROOT / "curriculum/decomposition/6c_foundations"
ASSET = ANDROID / "app-wiring/src/main/assets/curriculum_package.txt"

CONTRACT = ROOT / "arch/15a_computing_fundamentals/computing_fundamentals.yaml"
VERIFICATION = ROOT / "arch/15a_computing_fundamentals/content_verification.yaml"
SPEC = ROOT / "docs/COMPUTING_FUNDAMENTALS_CONTENT_SPEC.md"
RESEARCH = ROOT / "research/15a_computing_fundamentals_research.md"
QA_OUT = ROOT / "arch/15a_computing_fundamentals/qa_report.yaml"
FBB = ROOT / "docs/V1_FOUNDATION_BACKBONE.md"
TAXONOMY = ROOT / "docs/TASK_TAXONOMY_SPEC.md"
DECISIONS = ROOT / "docs/DECISIONS.md"

TASK_KT = ANDROID / "core-model/src/main/kotlin/coach/AuthoredTaskFacts.kt"
FORMAT_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"
SOURCE_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
TASK_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/AuthoredTaskFactsTest.kt"
FORMAT_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/PackageFormatTest.kt"
SHIPPED_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/ShippedPackageTest.kt"
PLAN_TEST = ANDROID / "app-wiring/src/test/kotlin/coach/wiring/FirstPlanTest.kt"

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


def paren_body(source: str, start: str) -> str:
    """The text from [start] to the parenthesis that closes the first one opened after it."""
    i = source.find(start)
    if i < 0:
        return ""
    depth, j, opened = 0, source.find("(", i), False
    while 0 <= j < len(source):
        if source[j] == "(":
            depth, opened = depth + 1, True
        elif source[j] == ")":
            depth -= 1
            if opened and depth == 0:
                return source[i:j + 1]
        j += 1
    return ""


# ---------------------------------------------------------------- inputs
for path in (CONTRACT, VERIFICATION, SPEC, RESEARCH, ASSET, TASK_KT, FORMAT_KT, SOURCE_KT, TASK_TEST, FORMAT_TEST, SHIPPED_TEST,
             PLAN_TEST, CONTENT / "package.yaml", CONTENT / "notation.yaml", CONTENT / "independent_review.yaml",
             ROOT / "tools/build_curriculum_package.py"):
    check(f"E15A-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
pkg = load(CONTENT / "package.yaml") or {}
notation = load(CONTENT / "notation.yaml") or {}
review = load(CONTENT / "independent_review.yaml") or {}
verification = load(VERIFICATION) or {}
skill_docs = {d["skill"]: d for d in (load(p) for p in sorted((CONTENT / "skills").glob("*.yaml"))) if d}
skills6c = {s["skill_id_candidate"]: s for s in (load(DECOMP / "skills.yaml") or [])}
objectives6c = {o["objective_id_candidate"]: o for o in (load(DECOMP / "objectives.yaml") or [])}
edges6c = load(DECOMP / "prerequisite_edges.yaml") or []
task_kt = strip_comments(read(TASK_KT))
format_kt = strip_comments(read(FORMAT_KT))
source_kt = strip_comments(read(SOURCE_KT))
asset = read(ASSET)

check("E15A-01_model", contract.get("model") == "CPFX-v0", str(contract.get("model")))
check("E15A-01_status", contract.get("status") == "accepted_15a", str(contract.get("status")))
check("E15A-01_decision", contract.get("decision") == "D-112", str(contract.get("decision")))
ud = contract.get("user_decisions", {})
check("E15A-01_user_decisions", ud.get("validation") == "executed_key_plus_independent_review" and ud.get("notation") == "readable_python_subset"
      and ud.get("objectives") == "refine_and_record", str(ud))

# ---------------------------------------------------------------- the package that ships is the package that was checked
spec = importlib.util.spec_from_file_location("builder", ROOT / "tools/build_curriculum_package.py")
builder = importlib.util.module_from_spec(spec)
spec.loader.exec_module(builder)
rebuilt, report = builder.build(CONTENT)
check("E15A-02_shipped_is_rebuilt", rebuilt == asset, "the shipped asset differs from a fresh build of its source")
check("E15A-02_generated_marker", asset.splitlines()[1:2] == ["# Generated by tools/build_curriculum_package.py from curriculum/content/15a_computing_fundamentals — do not edit by hand."]
      if asset else False, "")
check("E15A-02_no_failures", report["failures"] == [], f"{len(report['failures'])} failures: {[f['item'] for f in report['failures']][:5]}")
check("E15A-02_report_recorded", verification.get("summary") == report["summary"] and verification.get("items") == report["items"],
      "arch/15a_computing_fundamentals/content_verification.yaml is not the report of this build")

# ---------------------------------------------------------------- graph: exactly FBB-v0 §6.1 as 6C decomposed it
fbb = read(FBB)
fbb_section = fbb[fbb.find("## 6.1 Shared computing / programming skills"):fbb.find("## 6.2 Python skills")]
fbb_skills = re.findall(r"`(skill\.(?:computing|programming)\.[a-z_]+)`", fbb_section)
sixc_shared = sorted(s for s in skills6c if s.startswith(("skill.computing.", "skill.programming.")))
ids = [s["id"] for s in pkg.get("skills", [])]
check("E15A-03_fbb_seeds_covered", set(fbb_skills) <= set(ids) and len(fbb_skills) == 10, f"fbb={fbb_skills}")
check("E15A-03_exactly_6c_shared", sorted(ids) == sixc_shared and len(ids) == 12, f"package={sorted(ids)} 6c={sixc_shared}")
check("E15A-03_content_per_skill", sorted(skill_docs) == sorted(ids), "")
own_objectives = sorted(o["id"] for d in skill_docs.values() for o in d["objectives"])
sixc_objectives = sorted(k for k, o in objectives6c.items() if o["owner_skill_id"] in ids)
check("E15A-03_objective_identity", own_objectives == sixc_objectives and len(own_objectives) == 13, f"{own_objectives}")
internal = [e for e in edges6c if e["target_skill_id"] in ids and e["prerequisite_skill_id"] in ids]
inbound = [e for e in edges6c if e["target_skill_id"] in ids and e["prerequisite_skill_id"] not in ids]
check("E15A-03_closed_subgraph", inbound == [], f"edges from outside: {[(e['prerequisite_skill_id'], e['target_skill_id']) for e in inbound]}")
check("E15A-03_edge_count", len(internal) == 11 and sum(e["edge_kind"] == "hard" for e in internal) == 10, f"{len(internal)}")
pkg_edges = re.findall(r"\[prerequisite_edge\]\nprerequisite=(\S+)@v1\ntarget=(\S+)@v1\nedge_version=1\nedge_kind=(\w+)\n"
                       r"reason_kind=\w+\nstrictness_profile=\w+\nlifecycle_status=(\w+)", asset)
check("E15A-03_edges_ratified", sorted((p, t, k) for p, t, k, _ in pkg_edges) == sorted((e["prerequisite_skill_id"], e["target_skill_id"], e["edge_kind"]) for e in internal)
      and all(l == "published" for *_, l in pkg_edges), f"{len(pkg_edges)} edges in package")
pkg_skill_status = re.findall(r"\[skill\]\nlogical_id=(\S+)\n(?:.*\n){3}lifecycle_status=(\w+)", asset)
check("E15A-03_skills_published", len(pkg_skill_status) == 12 and all(s == "published" for _, s in pkg_skill_status), str(pkg_skill_status[:2]))

hard = {}
for e in internal:
    if e["edge_kind"] == "hard":
        hard.setdefault(e["target_skill_id"], set()).add(e["prerequisite_skill_id"])
seen, stack, cyclic = set(), set(), []


def visit(n):
    if n in stack:
        cyclic.append(n)
        return
    if n in seen:
        return
    seen.add(n)
    stack.add(n)
    for p in hard.get(n, ()):
        visit(p)
    stack.discard(n)


for n in ids:
    visit(n)
check("E15A-03_hard_dag", cyclic == [], str(cyclic))
entry = sorted(s for s in ids if s not in hard)
check("E15A-03_entry_points", entry == contract.get("entry_skills"), f"entry={entry}")

# ---------------------------------------------------------------- Objective reconciliation (user decision: refine and record)
expected_direct = contract.get("required_direct_types", {})
for d in skill_docs.values():
    for o in d["objectives"]:
        oid = o["id"]
        six = objectives6c.get(oid, {})
        ok = all(o.get(k) for k in ("statement", "observable_behavior", "reconciliation"))
        check(f"E15A-04_recorded_{oid.rsplit('.', 2)[-2]}_{oid.rsplit('.', 1)[-1]}", ok and o.get("required_direct_type") == expected_direct.get(oid)
              and o["required_direct_type"] in o["direct_evidence_types"] and set(o["direct_evidence_types"]) <= set(o["acceptable_evidence_types"])
              and set(o["acceptable_evidence_types"]) == set(six.get("evidence_profile", {}).get("acceptable_evidence_types", [])),
              f"{oid}: {o.get('required_direct_type')} vs contract {expected_direct.get(oid)}")
check("E15A-04_criticality_unchanged", all(o.get("criticality", objectives6c[o["id"]]["criticality"]) == objectives6c[o["id"]]["criticality"]
      for d in skill_docs.values() for o in d["objectives"]), "")
statements = [o["statement"] for d in skill_docs.values() for o in d["objectives"]]
check("E15A-04_no_template_statement", len(set(statements)) == len(statements) and not any("Yeni bir bağlamda" in s for s in statements), "")

# ---------------------------------------------------------------- items: executed, reviewed, measurable, fair
items = report["items"]
item_review = (review or {}).get("items", {}) or {}
check("E15A-05_review_method", bool((review or {}).get("method")) and review.get("reviewer") == "independent_15a_content_review", "")
check("E15A-05_every_item_reviewed", sorted(item_review) == sorted(i["item"] for i in items), f"{len(item_review)} reviewed / {len(items)} items")
check("E15A-05_every_item_passed_review", all(v.get("verdict") == "pass" for v in item_review.values()),
      str([k for k, v in item_review.items() if v.get("verdict") != "pass"][:5]))
check("E15A-05_every_item_validated", all(i["status"] == "validated" for i in items), "")
check("E15A-05_no_hidden_prerequisite", all(not i["hidden_prerequisites"] for i in items), "")
modes = {i["verify_mode"] for i in items}
check("E15A-05_known_modes", modes <= {"stdout", "probe", "termination", "behaviour", "functions", "detects", "fault", "script",
                                     "which_input", "fix_terminates", "reference", "rubric"}, str(modes))
executed = [i for i in items if i["verify_mode"] not in ("reference", "rubric")]
references = [i for i in items if i["verify_mode"] == "reference"]
rubrics = [i for i in items if i["verify_mode"] == "rubric"]
check("E15A-05_counts", (len(items), len(executed), len(references), len(rubrics)) == tuple(contract.get("item_counts", {}).get(k) for k in
      ("total", "executed", "reference_grounded", "rubric")), f"{len(items)} {len(executed)} {len(references)} {len(rubrics)}")
check("E15A-05_reference_minority", len(references) * 5 < len(items), "most keys must be executed, not cited")

item_rows = {m.group(1): m.group(0) for m in re.finditer(r"\[item\]\nref=(\S+)@v1\n(?:[^\n]+\n)+", asset + "\n")}
for d in skill_docs.values():
    for o in d["objectives"]:
        oid = o["id"]
        measuring = [r for r in item_rows.values() if f"target_objectives={oid}@v1\n" in r and "deterministic_verification=true" in r
                     and f"evidence_type={o['required_direct_type']}\n" in r]
        families = {re.search(r"variant_family_id=(\S+)", r).group(1) for r in measuring}
        transfer = [r for r in measuring if "difficulty_class=transfer_integration" in r]
        tag = f"{oid.rsplit('.', 2)[-2]}_{oid.rsplit('.', 1)[-1]}"
        check(f"E15A-05_measurable_{tag}", len(families) >= 4 and transfer != [], f"{len(families)} families, {len(transfer)} transfer")
difficulties = set(re.findall(r"difficulty_class=(\w+)", asset))
check("E15A-05_difficulty_vocabulary", difficulties == {"basic", "authentic_application", "transfer_integration"}, str(difficulties))
check("E15A-05_ai_origin_declared", "content_origin=human_authored" not in asset and asset.count("content_origin=ai_generated") > 0,
      "AI-written content is never declared human-authored")
check("E15A-05_ceiling_not_trusted", "status=trusted" not in asset and "lifecycle_status=trusted" not in asset, "")
check("E15A-05_code_reading_not_runnable", all("prohibited_solution_sources=terminal,compiler,debugger,external_ai" in r for r in item_rows.values()), "")

# ---------------------------------------------------------------- lessons
expl_review = (review or {}).get("explanations", {}) or {}
expl_ids = re.findall(r"\[explanation\]\nlogical_id=(\S+)", asset)
check("E15A-06_every_explanation_reviewed", sorted(expl_review) == sorted(expl_ids), f"{len(expl_review)} / {len(expl_ids)}")
check("E15A-06_every_explanation_passed", all(v.get("verdict") == "pass" for v in expl_review.values()),
      str([k for k, v in expl_review.items() if v.get("verdict") != "pass"][:5]))
checks_run = report.get("explanation_checks", [])
check("E15A-06_lesson_claims_executed", len(checks_run) >= contract.get("lesson_checks_min", 1) and all(c["result"] == "PASS" for c in checks_run),
      f"{len(checks_run)} checks")
for d in skill_docs.values():
    for o in d["objectives"]:
        tag = f"{o['id'].rsplit('.', 2)[-2]}_{o['id'].rsplit('.', 1)[-1]}"
        check(f"E15A-06_lesson_{tag}", bool(o["explanations"].get("canonical")) and "worked_example" in o["explanations"]
              and len(o.get("misconceptions", [])) >= 2 and all(m["open_question"].rstrip().endswith("?") for m in o["misconceptions"]), "")
skills_with_prereq = {e["target_skill_id"] for e in internal if e["edge_kind"] == "hard"}
check("E15A-06_prerequisite_refresh", all(any("prerequisite_refresh" in o["explanations"] for o in skill_docs[s]["objectives"]) for s in skills_with_prereq), "")

# ---------------------------------------------------------------- notation: taught before it is read
introducers = {s for c in notation.get("constructs", {}).values() for s in c["introduced_by"]}
check("E15A-07_introducers_in_package", introducers <= set(ids), str(introducers - set(ids)))
never = sorted(c for c, v in notation.get("constructs", {}).items() if not v["introduced_by"])
check("E15A-07_forbidden_constructs", never == sorted(contract.get("never_introduced", [])), str(never))
check("E15A-07_iteration_declares_comparison", skill_docs.get("skill.programming.iteration_reasoning", {}).get("lesson_requires")
      == ["skill.programming.expression_boolean_reasoning"], "")

# ---------------------------------------------------------------- tasks: 3B, served only as declared
serves_kt = paren_body(task_kt, "private val SERVES")
table = {p.lower(): set(t.lower() for t in re.findall(r"NeedTrigger\.([A-Z_]+)", rhs))
         for p, rhs in re.findall(r"TaskPurpose\.([A-Z_]+) to (setOf\([^)]*\)|emptySet\(\))", serves_kt)}
check("E15A-08_serving_table", table == {k: set(v) for k, v in (contract.get("task_serving") or {}).items()}, str(table))
tax = read(TAXONOMY)
activities = re.findall(r'^\s+"([a-z_]+)",$', paren_body(task_kt, "val ACTIVITY_KINDS"), re.M)
tax_block = tax[tax.find("## 3.2 `activity_kind`"):tax.find("### Önemli örnek")]
check("E15A-08_activity_kinds_are_3b", activities == re.findall(r"^([a-z_]+)$", tax_block, re.M) and len(activities) == 12, str(activities))
check("E15A-08_never_widens", "need.trigger !in serves || primarySkill !in need.targetSkills" in task_kt, "")
check("E15A-08_adapter_only_authored", re.search(r"parsed\?\.tasks\.orEmpty\(\)\.mapNotNull \{ it\.candidateFor\(need\) \}", source_kt) is not None, "")
check("E15A-08_task_trust_bounded", "declares ${task.validationStatus.id} over item" in format_kt and "TRUSTING" in format_kt, "")
tasks = re.findall(r"\[task\]\nlogical_id=task\.(\S+)\.(teach|practice|check|review|repair)\n(?:[^\n]+\n)+", asset + "\n")
check("E15A-08_five_per_skill", len(tasks) == 60 and all(sum(1 for t in tasks if t[0] == s.split(".", 1)[1]) == 5 for s in ids), f"{len(tasks)}")
purposes = dict(re.findall(r"logical_id=task\.\S+\.(teach|practice|check|review|repair)\n(?:[^\n]+\n){4}purpose=(\w+)", asset))
check("E15A-08_task_purposes", purposes == {"teach": "teach", "practice": "practice", "check": "assess", "review": "retain", "repair": "remediate"}, str(purposes))
check("E15A-08_tasks_validated", all(t["status"] == "validated" for t in report["tasks"]), "")
minutes = [int(m) for m in re.findall(r"\[task\]\n(?:[^\n]+\n)*?cost_minutes=(\d+)", asset)]
check("E15A-08_minutes_are_estimates", contract.get("task_minutes", {}).get("calibrated") is False and all(0 < m <= 20 for m in minutes), str(minutes[:5]))

# ---------------------------------------------------------------- the format
check("E15A-09_whole_line_comment", 'if (raw.startsWith("#")) "" else raw' in format_kt and "substringBefore('#')" not in format_kt, "")
check("E15A-09_prompt_line_breaks", 'section.values["prompt"].orEmpty().replace("\\\\n", "\\n")' in format_kt, "")
keys = re.findall(r'"([a-z_]+)"', paren_body(format_kt, '"task" to setOf(')[len('"task"'):])
check("E15A-09_task_keys", keys == contract.get("task_section_keys"), str(keys))
check("E15A-09_task_section_known", '"task",' in body(format_kt, "private val KNOWN_SECTIONS"), "")

# ---------------------------------------------------------------- tests
for path, names in ((TASK_TEST, ["a task serves only a need it declares, and only about its own Skill",
                                 "a purpose serves only the needs the accepted contracts give it"]),
                    (FORMAT_TEST, ["only a whole line is a comment, so code and prose may contain a hash",
                                   "a task that presents what the package lacks, or claims more trust than its items, refuses the package"]),
                    (SHIPPED_TEST, ["everything shipped was validated, so nothing is a candidate",
                                    "every Objective is teachable, practisable and measurable by its own direct type"]),
                    (PLAN_TEST, ["a learner who has done nothing is offered the two entry lessons and nothing that waits on them"])):
    text = read(path)
    for name in names:
        check(f"E15A-10_test_{name[:40]}", f"`{name}`" in text, f"{path.name}: {name}")
check("E15A-10_shipped_test_reads_the_asset", 'File("../app-wiring/src/main/assets/curriculum_package.txt")' in read(SHIPPED_TEST), "")
check("E15A-10_plan_test_reads_the_asset", 'File("src/main/assets/curriculum_package.txt")' in read(PLAN_TEST) and "BuildDailyPlan(" in read(PLAN_TEST), "")

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results", {})
mids = [m.get("id") for m in mr.get("mutants", [])]
check("E15A-11_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("E15A-11_compile_failure_not_detection", mr.get("compile_failure_is_detection") is False and mr.get("final_run_is_a_single_clean_run") is True, "")
check("E15A-11_control", mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation", {})
check("E15A-11_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs", [])
check("E15A-11_runs_pass", len(runs) >= 5 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification", {})
check("E15A-11_t6_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("sqlite_publish_of_shipped_package_run") is False, "")
gates = {g["file"]: g["checks"] for g in contract.get("narrowed_gates", [])}
check("E15A-11_narrowed_gates", gates == {"tools/validate_daily_micro_assessment.py": ["E11D-13_no_asset_ships"],
                                          "tools/validate_planner_engine.py": ["E12C-09_adapter_answers_truthfully"]}, str(gates))
for f in gates:
    check(f"E15A-11_narrowing_declared_{Path(f).stem}", "Narrowed by 15A (CPFX-v0 / D-112)" in read(ROOT / f), f)
check("E15A-11_open_loops", set(contract.get("open_loops", {})) == {"multi_package_shipping", "requires_transfer_not_stored",
      "iteration_comparison_prerequisite", "runner_presents_tasks", "estimate_calibration", "t6_and_device_ingestion"}, str(contract.get("open_loops")))

# ---------------------------------------------------------------- spec, research, decision
spec_text = read(SPEC)
check("E15A-12_spec_status", "**Status:** ACCEPTED — independent 15A QA PASS" in spec_text and "`D-112`" in spec_text, "")
check("E15A-12_spec_next", "**15B — Python Foundations**" in spec_text, "")
check("E15A-12_spec_t6", "**Not run: T6.**" in spec_text, "")
check("E15A-12_research", "gcc.gnu.org/onlinedocs/gcc/Overall-Options.html" in read(RESEARCH) and "docs.python.org/3/glossary.html" in read(RESEARCH), "")
check("E15A-12_decision", re.search(r"^#+ .*D-112", read(DECISIONS), re.M) is not None, "D-112 heading in DECISIONS")

passed = sum(1 for r in results if r["result"] == "PASS")
out = {"model": "CPFX-v0", "stage_step": "15A", "decision": "D-112", "result": "PASS" if not failures else "FAIL",
       "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
if "--no-write" not in sys.argv:
    QA_OUT.parent.mkdir(parents=True, exist_ok=True)
    QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"15A QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
for failure in failures:
    print("  FAIL", failure)
sys.exit(0 if not failures else 1)
