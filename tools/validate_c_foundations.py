"""Independent 15C QA — CFNX-v0 C Foundations content.

The rules are read from the accepted contracts — FBB-v0 §6.3 (the C seeds), 6C's decomposition (seed mappings, identity,
Objectives, edges), KGC-v0 §27-§28, AIV-v0 §2-§3 and §23-§25 (an AI-generated item is never trusted by itself), QAB-v0 §21
(tools), CDEX-v0 (code is judged by the course's own tests), GRE-v0's two-family gate, 3B §3 and §15, D-113 (incremental
packages; a published version is never overwritten) — and compared with the content source, all three shipped packages,
the test suites and the real Kotlin. The third package is rebuilt here from its source — every key, lesson claim and
suite checked again with gcc on Linux, through the learner's own runner — and must be byte-identical to what ships, as
must every suite and both earlier packages.

What this step exists to prevent must be unrepresentable as a PASS: a C program judged anywhere but where the learner
builds it, a suite a plausible wrong solution passes, a function item a program without the function passes, a key not
checked against what gcc and the program really do, C code the package cannot show (a lost backslash-n), an AI-generated
item validated without an independent verdict, a hidden prerequisite (a pointer, an array), a C Skill that could never
become ready, and a package that changes how the earlier ones read.
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
CONTENT = ROOT / "curriculum/content/15c_c_foundations"
EARLIER = [ROOT / "curriculum/content/15a_computing_fundamentals", ROOT / "curriculum/content/15b_python_foundations"]
DECOMP = ROOT / "curriculum/decomposition/6c_foundations"
ASSET = ANDROID / "app-wiring/src/main/assets/curriculum_package_v3.txt"
EARLIER_ASSETS = [ANDROID / "app-wiring/src/main/assets/curriculum_package.txt", ANDROID / "app-wiring/src/main/assets/curriculum_package_v2.txt"]

CONTRACT = ROOT / "arch/15c_c_foundations/c_foundations.yaml"
VERIFICATION = ROOT / "arch/15c_c_foundations/content_verification.yaml"
SPEC = ROOT / "docs/C_FOUNDATIONS_CONTENT_SPEC.md"
RESEARCH = ROOT / "research/15c_c_foundations_research.md"
QA_OUT = ROOT / "arch/15c_c_foundations/qa_report.yaml"
FBB = ROOT / "docs/V1_FOUNDATION_BACKBONE.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
BUILDER = ROOT / "tools/build_curriculum_package.py"

FORMAT_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"
FORMAT_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/PackageFormatTest.kt"
SHIPPED_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/ShippedCPackageTest.kt"
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
for path in (CONTRACT, VERIFICATION, SPEC, RESEARCH, ASSET, *EARLIER_ASSETS, FORMAT_KT, FORMAT_TEST, SHIPPED_TEST, COURSE_TEST,
             CONTENT / "package.yaml", CONTENT / "notation.yaml", CONTENT / "independent_review.yaml", BUILDER):
    check(f"E15C-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
pkg = load(CONTENT / "package.yaml") or {}
notation = load(CONTENT / "notation.yaml") or {}
review = load(CONTENT / "independent_review.yaml") or {}
verification = load(VERIFICATION) or {}
skill_docs = {d["skill"]: d for d in (load(p) for p in sorted((CONTENT / "skills").glob("*.yaml"))) if d}
earlier_ids = [s["id"] for d in EARLIER for s in (load(d / "package.yaml") or {}).get("skills", [])]
objectives6c = {o["objective_id_candidate"]: o for o in (load(DECOMP / "objectives.yaml") or [])}
edges6c = load(DECOMP / "prerequisite_edges.yaml") or []
seeds6c = load(DECOMP / "seed_mappings.yaml") or []
asset = read(ASSET)

check("E15C-01_model", contract.get("model") == "CFNX-v0", str(contract.get("model")))
check("E15C-01_status", contract.get("status") == "accepted_15c", str(contract.get("status")))
check("E15C-01_decision", contract.get("decision") == "D-114", str(contract.get("decision")))
ud = contract.get("user_decisions", {})
check("E15C-01_user_decisions", ud.get("linux_prerequisite") == "add_terminal_navigation_to_15c"
      and ud.get("environment") == "wsl_ubuntu_gcc" and ud.get("input") == "add_standard_io_basic", str(ud))

# ---------------------------------------------------------------- the package that ships is the package that was checked (on Linux)
spec = importlib.util.spec_from_file_location("builder", BUILDER)
builder = importlib.util.module_from_spec(spec)
spec.loader.exec_module(builder)
# A build that cannot be made at all (a refused value, no Linux to build in) is a FAIL, never a crash.
try:
    rebuilt, report = builder.build(CONTENT)
except Exception as error:  # noqa: BLE001 — any failure to build is reported, not raised
    check("E15C-02_builds", False, f"{type(error).__name__}: {error}"[:300])
    print(f"15C QA: {sum(1 for r in results if r['result'] == 'PASS')}/{len(results)} FAIL")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(1)
check("E15C-02_builds", True, "")
suite_files = report.pop("suite_files", {})
check("E15C-02_shipped_is_rebuilt", rebuilt == asset, "the shipped asset differs from a fresh build of its source")
check("E15C-02_generated_marker", asset.splitlines()[1:2] == ["# Generated by tools/build_curriculum_package.py from curriculum/content/15c_c_foundations — do not edit by hand."]
      if asset else False, "")
check("E15C-02_no_failures", report["failures"] == [], f"{len(report['failures'])} failures: {[f['item'] for f in report['failures']][:5]}")
check("E15C-02_report_recorded", verification.get("summary") == report["summary"] and verification.get("items") == report["items"],
      "arch/15c_c_foundations/content_verification.yaml is not the report of this build")
on_disk = {p.stem: p.read_text(encoding="utf-8") for p in sorted((CONTENT / "suites").glob("*.json"))}
check("E15C-02_suites_are_rebuilt", on_disk == suite_files and len(on_disk) == contract.get("item_counts", {}).get("code"),
      f"{len(on_disk)} on disk, {len(suite_files)} built")
# `--skip-earlier-rebuild` exists only for this validator's own mutation run (each rebuild of 15B runs 85 suites); every
# sweep and every POST run rebuilds both earlier packages.
if "--skip-earlier-rebuild" not in sys.argv:
    for d, a in zip(EARLIER, EARLIER_ASSETS):
        # The text alone is not enough: a lesson check that no longer holds does not change the package's text.
        try:
            again, earlier_report = builder.build(d)
            untouched = again == read(a) and earlier_report["failures"] == []
            why = f"{len(earlier_report['failures'])} build failures" if again == read(a) else "the asset differs"
        except Exception as error:  # noqa: BLE001
            untouched, why = False, f"{type(error).__name__}: {error}"[:200]
        check(f"E15C-02_earlier_untouched_{d.name}", untouched, f"{d.name} is no longer a clean, fresh build of its source: {why}")

# ---------------------------------------------------------------- graph: FBB-v0 §6.3's C seeds as 6C decomposed them, plus their closure
fbb = read(FBB)
fbb_section = fbb[fbb.find("## 6.3 C / memory skills"):fbb.find("## 6.4")]
c_seeds = [s for s in re.findall(r"`(skill\.c\.[a-z_]+)`", fbb_section) if "pointer" not in s]
mapped = sorted({r for m in seeds6c if m["seed_kind"] == "skill" and m["seed_id"] in c_seeds for r in m["result_entity_refs"]})
ids = [s["id"] for s in pkg.get("skills", [])]
added = sorted(set(ids) - set(mapped))
check("E15C-03_c_seeds", sorted(c_seeds) == sorted(["skill.c.compile_link_run_basic", "skill.c.declarations_types_expressions",
      "skill.c.conditionals_loops_basic", "skill.c.functions_basic"]), str(c_seeds))
check("E15C-03_seeds_as_6c_decomposed", sorted(contract.get("seed_skills", [])) == mapped and len(mapped) == 7 and set(mapped) <= set(ids), str(mapped))
check("E15C-03_added_are_declared", added == sorted(contract.get("added_skills", [])) and len(ids) == 9, str(added))
check("E15C-03_pointers_and_memory_are_15d", not any(s.startswith("skill.memory.") or "pointer" in s for s in ids), str(ids))
hard_into: dict[str, set[str]] = {}
for e in edges6c:
    if e["edge_kind"] == "hard":
        hard_into.setdefault(e["target_skill_id"], set()).add(e["prerequisite_skill_id"])
known = set(ids) | set(earlier_ids)
open_hard = sorted((p, t) for t in ids for p in hard_into.get(t, ()) if p not in known)
check("E15C-03_closed_under_hard_prerequisites", open_hard == [], f"a Skill that could never become ready: {open_hard}")
check("E15C-03_terminal_is_why", "skill.linux.terminal_filesystem_navigation" in hard_into.get("skill.c.compile_link_run_basic", ())
      and not hard_into.get("skill.linux.terminal_filesystem_navigation"), "the Linux Skill is added because the first C Skill needs it")
check("E15C-03_content_per_skill", sorted(skill_docs) == sorted(ids), "")
own_objectives = sorted(o["id"] for d in skill_docs.values() for o in d["objectives"])
sixc_objectives = sorted(k for k, o in objectives6c.items() if o["owner_skill_id"] in ids)
check("E15C-03_objective_identity", own_objectives == sixc_objectives and len(own_objectives) == 10, str(own_objectives))
expected_edges = sorted((e["prerequisite_skill_id"], e["target_skill_id"], e["edge_kind"]) for e in edges6c
                        if e["target_skill_id"] in ids and e["prerequisite_skill_id"] in known)
pkg_edges = re.findall(r"\[prerequisite_edge\]\nprerequisite=(\S+)@v1\ntarget=(\S+)@v1\nedge_version=1\nedge_kind=(\w+)\n"
                       r"reason_kind=\w+\nstrictness_profile=\w+\nlifecycle_status=(\w+)", asset)
check("E15C-03_edges_ratified", sorted((p, t, k) for p, t, k, _ in pkg_edges) == expected_edges
      and len(pkg_edges) == contract.get("scope", {}).get("edges") and all(l == "published" for *_, l in pkg_edges), f"{len(pkg_edges)} edges")
check("E15C-03_builds_on_earlier", sum(1 for p, *_ in pkg_edges if p in earlier_ids) == contract.get("scope", {}).get("edges_from_earlier"), "")
entry = sorted(s for s in ids if not (hard_into.get(s, set()) & known))
check("E15C-03_entry_points", entry == contract.get("entry_skills"), str(entry))

# ---------------------------------------------------------------- Objective reconciliation
for d in skill_docs.values():
    for o in d["objectives"]:
        six = objectives6c.get(o["id"], {})
        check(f"E15C-04_recorded_{tag(o['id'])}", all(o.get(k) for k in ("statement", "observable_behavior", "reconciliation"))
              and o.get("required_direct_type") == six.get("evidence_profile", {}).get("required_direct_type")
              and set(o["acceptable_evidence_types"]) == set(six.get("evidence_profile", {}).get("acceptable_evidence_types", [])), o["id"])
statements = [o["statement"] for d in skill_docs.values() for o in d["objectives"]]
check("E15C-04_no_template_statement", len(set(statements)) == len(statements) and not any("Yeni bir bağlamda" in s for s in statements), "")

# ---------------------------------------------------------------- items
items = report["items"]
item_review = (review or {}).get("items", {}) or {}
check("E15C-05_review_method", bool((review or {}).get("method")) and review.get("reviewer") == "independent_15c_content_review", "")
check("E15C-05_every_item_reviewed", sorted(item_review) == sorted(i["item"] for i in items), f"{len(item_review)} / {len(items)}")
check("E15C-05_every_item_passed_review", all(v.get("verdict") == "pass" for v in item_review.values()),
      str([k for k, v in item_review.items() if v.get("verdict") != "pass"][:5]))
check("E15C-05_every_item_validated", all(i["status"] == "validated" for i in items), "")
check("E15C-05_no_hidden_prerequisite", all(not i["hidden_prerequisites"] for i in items), "")
modes = {i["verify_mode"] for i in items}
check("E15C-05_linux_modes_only", modes == {"suite", "c_stdout", "c_stage", "shell"}, str(modes))
counts = contract.get("item_counts", {})
by_mode = {m: sum(1 for i in items if i["verify_mode"] == m) for m in ("suite", "c_stdout", "c_stage", "shell")}
check("E15C-05_counts", (len(items), by_mode["suite"], by_mode["c_stdout"], by_mode["c_stage"], by_mode["shell"]) ==
      (counts.get("total"), counts.get("code"), counts.get("c_stdout"), counts.get("c_stage"), counts.get("shell")), f"{len(items)} {by_mode}")
check("E15C-05_code_executed_against_wrong", all(i["verification"].startswith("executed with the course runner; the reference passes")
      and re.search(r"each of ([2-9]|\d\d) wrong solutions fails", i["verification"]) for i in items if i["verify_mode"] == "suite"), "")
check("E15C-05_keys_built_on_linux", all(i["verification"].startswith(("built with gcc and run in Linux", "run in bash in an empty directory in Linux",
      "executed; true value")) for i in items if i["verify_mode"] != "suite"), "")
code_cfg = pkg.get("code", {})
check("E15C-05_code_environment", code_cfg.get("environment") == "linux" and code_cfg.get("file") == "cozum.c"
      and code_cfg.get("build") == ["gcc", "-std=c11", "-Wall", "-Wextra", "-o", "cozum", "cozum.c"] and code_cfg.get("run") == ["./cozum"], str(code_cfg))
for iid, text in suite_files.items():
    suite = json.loads(text)
    fixed = len(suite["tests"]) == 1 and not suite["tests"][0].get("args") and not suite["tests"][0].get("stdin")
    build = suite["build"]["command"]
    harnessed = build[:2] == ["bash", "-c"]
    ok_build = build == code_cfg.get("build") or (harnessed and "-Dmain=ogrenci_main -c cozum.c" in build[2]
                                                     and "gcc cozum.o ders_test.o -o cozum" in build[2])
    check(f"E15C-05_suite_{iid.split('.', 2)[2]}", suite["format"] == "code_test_suite/1" and ok_build and suite["run"]["command"] == ["./cozum"]
          and (len(suite["tests"]) >= 2 or fixed) and suite["timeout_seconds"] == 5, f"{len(suite['tests'])} tests")
function_items = [it for d in skill_docs.values() for o in d["objectives"] for it in o["items"] if it.get("c_harness")]
check("E15C-05_function_items_harnessed", len(function_items) == counts.get("function_harnessed")
      and all(it in function_items for it in skill_docs["skill.c.functions_basic"]["objectives"][0]["items"]),
      "every functions_basic item is called by the course's own main")
item_rows = {m.group(1): m.group(0) for m in re.finditer(r"\[item\]\nref=(\S+)@v1\n(?:[^\n]+\n)+", asset + "\n")}
for d in skill_docs.values():
    for o in d["objectives"]:
        oid = o["id"]
        measuring = [r for r in item_rows.values() if f"target_objectives={oid}@v1\n" in r and "deterministic_verification=true" in r
                     and f"evidence_type={o['required_direct_type']}\n" in r]
        families = {re.search(r"variant_family_id=(\S+)", r).group(1) for r in measuring}
        transfer = [r for r in measuring if "difficulty_class=transfer_integration" in r]
        check(f"E15C-05_measurable_{tag(oid)}", len(families) >= 4 and transfer != [], f"{len(families)} families")
check("E15C-05_ai_origin_declared", "content_origin=human_authored" not in asset and asset.count("content_origin=ai_generated") > 0, "")
at_computer = [r for r in item_rows.values() if re.search(r"evidence_type=(authored_code|hands_on_system_task)\n", r)]
check("E15C-05_terminal_at_the_computer", len(at_computer) == counts.get("total") - counts.get("c_stdout")
      and all("allowed_tools=terminal,documentation\n" in r and "prohibited_solution_sources=external_ai\n" in r for r in at_computer), "")
check("E15C-05_reading_not_runnable", all("prohibited_solution_sources=terminal,compiler,debugger,external_ai" in r
      for r in item_rows.values() if r not in at_computer), "")

# ---------------------------------------------------------------- C code is shown exactly (D-114 format refinement)
format_kt = strip_comments(read(FORMAT_KT))
unescape = body(format_kt, "fun unescape(")
check("E15C-06_unescape_rule", "next == 'n' || next == '\\\\'" in unescape and "append(if (next == 'n') '\\n' else '\\\\')" in unescape, "")
check("E15C-06_multiline_values_unescaped", format_kt.count("unescape(") >= 5 and '.replace("\\\\n", "\\n")' not in format_kt, "")
check("E15C-06_package_escapes", pkg.get("escape_backslash") is True, "")
# The new rule reads a package differently only where it holds a doubled backslash; neither earlier package does.
check("E15C-06_earlier_read_unchanged", all("\\\\" not in read(a) for a in EARLIER_ASSETS),
      "an earlier package holds a doubled backslash, so the new rule would read it differently")
check("E15C-06_c_newline_shipped", "\\\\n" in asset, "C code in the package carries an escaped backslash-n")

# ---------------------------------------------------------------- lessons
expl_review = (review or {}).get("explanations", {}) or {}
expl_ids = re.findall(r"\[explanation\]\nlogical_id=(\S+)", asset)
check("E15C-07_every_explanation_reviewed", sorted(expl_review) == sorted(expl_ids), f"{len(expl_review)} / {len(expl_ids)}")
check("E15C-07_every_explanation_passed", all(v.get("verdict") == "pass" for v in expl_review.values()),
      str([k for k, v in expl_review.items() if v.get("verdict") != "pass"][:5]))
checks_run = report.get("explanation_checks", [])
check("E15C-07_lesson_claims_executed", len(checks_run) >= contract.get("lesson_checks_min", 1) and all(c["result"] == "PASS" for c in checks_run),
      f"{len(checks_run)} checks")
for d in skill_docs.values():
    for o in d["objectives"]:
        check(f"E15C-07_lesson_{tag(o['id'])}", bool(o["explanations"].get("canonical")) and "worked_example" in o["explanations"]
              and "prerequisite_refresh" in o["explanations"] and len(o.get("misconceptions", [])) >= 2
              and all(m["open_question"].rstrip().endswith("?") for m in o["misconceptions"]), "")
COMBINING_DOT = chr(0x0307)
check("E15C-07_no_combining_dot", COMBINING_DOT not in asset and all(COMBINING_DOT not in t for t in suite_files.values()), "")

# ---------------------------------------------------------------- notation
introducers = {s for c in notation.get("constructs", {}).values() for s in c["introduced_by"]}
check("E15C-08_introducers_known", introducers <= known, str(introducers - known))
never = sorted(c for c, v in notation.get("constructs", {}).items() if not v["introduced_by"])
check("E15C-08_forbidden_constructs", never == sorted(contract.get("never_introduced", [])), str(never))
check("E15C-08_not_code_is_stripped", len(notation.get("strip", [])) == 4, "")

# ---------------------------------------------------------------- tasks
tasks = re.findall(r"\[task\]\nlogical_id=task\.(\S+)\.(teach|practice|check|review|repair)\n(?:[^\n]+\n)+", asset + "\n")
check("E15C-09_five_per_skill", len(tasks) == 45 and all(sum(1 for t in tasks if t[0] == s.split(".", 1)[1]) == 5 for s in ids), f"{len(tasks)}")
check("E15C-09_tasks_validated", all(t["status"] == "validated" for t in report["tasks"]), "")
minutes = [int(m) for m in re.findall(r"\[task\]\n(?:[^\n]+\n)*?cost_minutes=(\d+)", asset)]
check("E15C-09_minutes_are_estimates", contract.get("task_minutes", {}).get("calibrated") is False and all(0 < m <= 20 for m in minutes), "")

# ---------------------------------------------------------------- the builder judges C where the learner builds it
builder_src = read(BUILDER)
check("E15C-10_runner_in_linux", '["python3", wsl_path(RUNNER) if os.name == "nt" else str(RUNNER), "suite.json", "--dir", "."]' in builder_src, "")
check("E15C-10_keys_in_linux", 'in_linux(GCC + ["-c", "program.c", "-o", "program.o"], d)' in builder_src
      and 'in_linux(["gcc", "program.o", "-o", "program"], d)' in builder_src and 'in_linux(["bash", "-c", script], d)' in builder_src, "")
check("E15C-10_earlier_packages_keep_python", 'PYTHON_CODE = {"file": CODE_FILE, "build": CODE_BUILD, "run": CODE_RUN, "environment": "host"}' in builder_src
      and 'ESCAPE_BACKSLASH = bool(pkg.get("escape_backslash", False))' in builder_src, "")

# ---------------------------------------------------------------- tests
for path, names in ((FORMAT_TEST, ["a doubled backslash is one backslash, so C code can show a backslash-n, and every other backslash is itself"]),
                    (SHIPPED_TEST, ["everything shipped was validated, so nothing is a candidate",
                                    "C code is shown exactly as written, with its backslash-n, and the lines of the program are lines"]),
                    (COURSE_TEST, ["all three shipped packages are published into the real store, oldest first, and C waits on the terminal",
                                   "with the C package, a learner who has done nothing may also start the terminal lesson, and no C Skill starts"])):
    text = read(path)
    for name in names:
        check(f"E15C-11_test_{name[:40]}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results") if isinstance(contract.get("mutation_results"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("E15C-12_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("E15C-12_compile_failure_not_detection", mr.get("compile_failure_is_detection") is False and mr.get("final_run_is_a_single_clean_run") is True, "")
check("E15C-12_control", mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("E15C-12_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("E15C-12_runs_pass", len(runs) >= 5 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") if isinstance(contract.get("device_verification"), dict) else {}
check("E15C-12_t6_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("sqlite_publish_on_jvm_run") is True
      and dv.get("sqlite_publish_on_device_run") is False, "")
gates = {g["file"]: g["checks"] for g in contract.get("narrowed_gates", [])}
check("E15C-12_narrowed_gates", gates == contract.get("expected_narrowed_gates"), str(gates))
for f in gates:
    check(f"E15C-12_narrowing_declared_{Path(f).stem}", "15C" in read(ROOT / f) and "D-114" in read(ROOT / f), f)

# ---------------------------------------------------------------- spec, research, decision
spec_text = read(SPEC)
check("E15C-13_spec_status", "**Status:** ACCEPTED — independent 15C QA PASS" in spec_text and "`D-114`" in spec_text, "")
check("E15C-13_spec_next", "**15D — Memory Foundations**" in spec_text, "")
check("E15C-13_spec_t6", "**Not run: T6.**" in spec_text, "")
check("E15C-13_research", "gcc.gnu.org" in read(RESEARCH) and "learn.microsoft.com" in read(RESEARCH), "")
check("E15C-13_decision", re.search(r"^#+ .*D-114", read(DECISIONS), re.M) is not None, "D-114 heading in DECISIONS")

passed = sum(1 for r in results if r["result"] == "PASS")
out = {"model": "CFNX-v0", "stage_step": "15C", "decision": "D-114", "result": "PASS" if not failures else "FAIL",
       "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
if "--no-write" not in sys.argv:
    QA_OUT.parent.mkdir(parents=True, exist_ok=True)
    QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"15C QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
for failure in failures:
    print("  FAIL", failure)
sys.exit(0 if not failures else 1)
