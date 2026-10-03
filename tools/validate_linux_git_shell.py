"""Independent 15E QA — LGSX-v0 Linux / Git / Shell Foundations content.

The rules are read from the accepted contracts — FBB-v0 §6.4 (the Linux, shell and Git seeds), 6C's decomposition
(seed mappings, identity, Objectives, edges), KGC-v0 §27-§28, AIV-v0 §2-§3 and §23-§25 (an AI-generated item is never
trusted by itself), QAB-v0 §21 (tools), GRE-v0's two-family gate, 3B §3, §10 and §15, D-113 (incremental packages; a
published version is never overwritten), D-114 (Linux keys are checked in Linux), D-117 (the user's standing approval) —
and compared with the content source, all five shipped packages and the real Kotlin. The fifth package is rebuilt here
from its source — every key and lesson claim run again with bash and git in Linux — and must be byte-identical to what
ships, as must all four earlier packages.

What this step exists to prevent must be unrepresentable as a PASS: a key about a command that was not run, a check
that runs something other than the commands the learner is shown, a key that depends on the checking machine's own Git
configuration or identity (a hash, a date, a branch name), a setup line that teaches something the item then asks
about, an AI-generated item validated without an independent verdict, a hidden prerequisite (a pipe before pipes, a
commit before commits), a Skill that could never become ready, and a package that changes how the earlier ones read.
"""

from __future__ import annotations

import importlib.util
from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTENT = ROOT / "curriculum/content/15e_linux_git_shell"
EARLIER = [ROOT / "curriculum/content/15a_computing_fundamentals", ROOT / "curriculum/content/15b_python_foundations",
           ROOT / "curriculum/content/15c_c_foundations", ROOT / "curriculum/content/15d_memory_foundations"]
DECOMP = ROOT / "curriculum/decomposition/6c_foundations"
ASSETS = ANDROID / "app-wiring/src/main/assets"
ASSET = ASSETS / "curriculum_package_v5.txt"
EARLIER_ASSETS = [ASSETS / "curriculum_package.txt"] + [ASSETS / f"curriculum_package_v{n}.txt" for n in (2, 3, 4)]

CONTRACT = ROOT / "arch/15e_linux_git_shell/linux_git_shell.yaml"
VERIFICATION = ROOT / "arch/15e_linux_git_shell/content_verification.yaml"
SPEC = ROOT / "docs/LINUX_GIT_SHELL_CONTENT_SPEC.md"
RESEARCH = ROOT / "research/15e_linux_git_shell_research.md"
QA_OUT = ROOT / "arch/15e_linux_git_shell/qa_report.yaml"
FBB = ROOT / "docs/V1_FOUNDATION_BACKBONE.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
BUILDER = ROOT / "tools/build_curriculum_package.py"

FORMAT_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"
SHIPPED_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/ShippedShellGitPackageTest.kt"
COURSE_TEST = ANDROID / "app-wiring/src/test/kotlin/coach/wiring/ShippedCourseTest.kt"

PRELUDE = ("export GIT_CONFIG_GLOBAL=/dev/null GIT_CONFIG_NOSYSTEM=1\n"
           "export GIT_AUTHOR_NAME=Ogrenci GIT_AUTHOR_EMAIL=ogrenci@example.com GIT_COMMITTER_NAME=Ogrenci GIT_COMMITTER_EMAIL=ogrenci@example.com\n"
           "export LC_ALL=C.UTF-8\n")
SETUP_MARK = re.compile(r"#\s*hazırlık\s*$")

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
for path in (CONTRACT, VERIFICATION, SPEC, RESEARCH, ASSET, *EARLIER_ASSETS, FORMAT_KT, SHIPPED_TEST, COURSE_TEST,
             CONTENT / "package.yaml", CONTENT / "notation.yaml", CONTENT / "independent_review.yaml", BUILDER):
    check(f"E15E-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

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

check("E15E-01_model", contract.get("model") == "LGSX-v0", str(contract.get("model")))
check("E15E-01_status", contract.get("status") == "accepted_15e", str(contract.get("status")))
check("E15E-01_decision", contract.get("decision") == "D-118", str(contract.get("decision")))
ud = contract.get("user_decisions", {}) or {}
check("E15E-01_standing_approval", ud.get("approval") == "standing_D-117" and ud.get("new_user_decisions") is False
      and isinstance(ud.get("assistant_defaults_under_D-117"), list) and len(ud["assistant_defaults_under_D-117"]) > 0, str(ud))

# ---------------------------------------------------------------- the package that ships is the package that was checked (in Linux)
spec = importlib.util.spec_from_file_location("builder", BUILDER)
builder = importlib.util.module_from_spec(spec)
spec.loader.exec_module(builder)
# A build that cannot be made at all (a refused value, no Linux to build in) is a FAIL, never a crash.
try:
    rebuilt, report = builder.build(CONTENT)
except Exception as error:  # noqa: BLE001 — any failure to build is reported, not raised
    check("E15E-02_builds", False, f"{type(error).__name__}: {error}"[:300])
    print(f"15E QA: {sum(1 for r in results if r['result'] == 'PASS')}/{len(results)} FAIL")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(1)
check("E15E-02_builds", True, "")
suite_files = report.pop("suite_files", {})
check("E15E-02_shipped_is_rebuilt", rebuilt == asset, "the shipped asset differs from a fresh build of its source")
check("E15E-02_generated_marker", asset.splitlines()[1:2] == ["# Generated by tools/build_curriculum_package.py from curriculum/content/15e_linux_git_shell — do not edit by hand."]
      if asset else False, "")
check("E15E-02_no_failures", report["failures"] == [], f"{len(report['failures'])} failures: {[f['item'] for f in report['failures']][:5]}")
check("E15E-02_report_recorded", verification.get("summary") == report["summary"] and verification.get("items") == report["items"],
      "arch/15e_linux_git_shell/content_verification.yaml is not the report of this build")
check("E15E-02_no_code_suites", suite_files == {} and not (CONTENT / "suites").exists(), "15E has no program to test: every key is a command's output")
# `--skip-earlier-rebuild` exists only for this validator's own mutation run (rebuilding 15B–15D runs 135 suites); every
# sweep and every POST run rebuilds all four earlier packages.
if "--skip-earlier-rebuild" not in sys.argv:
    for d, a in zip(EARLIER, EARLIER_ASSETS):
        # The text alone is not enough: a lesson check that no longer holds does not change the package's text.
        try:
            again, earlier_report = builder.build(d)
            untouched = again == read(a) and earlier_report["failures"] == []
            why = f"{len(earlier_report['failures'])} build failures" if again == read(a) else "the asset differs"
        except Exception as error:  # noqa: BLE001
            untouched, why = False, f"{type(error).__name__}: {error}"[:200]
        check(f"E15E-02_earlier_untouched_{d.name}", untouched, f"{d.name} is no longer a clean, fresh build of its source: {why}")

# ---------------------------------------------------------------- graph: FBB-v0 §6.4's seeds as 6C decomposed them
fbb = read(FBB)
fbb_section = fbb[fbb.find("## 6.4 Linux / shell / Git skills"):fbb.find("## 6.5")]
seeds = re.findall(r"`(skill\.[a-z]+\.[a-z_]+)`", fbb_section)
mapped = sorted({r for m in seeds6c if m["seed_kind"] == "skill" and m["seed_id"] in seeds for r in m["result_entity_refs"]})
ids = [s["id"] for s in pkg.get("skills", [])]
check("E15E-03_linux_seeds", sorted(seeds) == sorted(["skill.linux.terminal_filesystem_navigation", "skill.linux.process_exit_stdout_stderr_basic",
      "skill.shell.command_options_redirection_basic", "skill.git.repository_status_diff", "skill.git.stage_commit_history_basic"]), str(seeds))
check("E15E-03_terminal_was_15c", "skill.linux.terminal_filesystem_navigation" in earlier_ids
      and "skill.linux.terminal_filesystem_navigation" not in ids, "published by 15C (D-114), never carried again")
check("E15E-03_seeds_as_6c_decomposed", sorted(contract.get("seed_skills", [])) == sorted(ids)
      == sorted(set(mapped) - {"skill.linux.terminal_filesystem_navigation"}) and len(ids) == 5, str(mapped))
check("E15E-03_nothing_added", contract.get("added_skills") == [], str(contract.get("added_skills")))
hard_into: dict[str, set[str]] = {}
for e in edges6c:
    if e["edge_kind"] == "hard":
        hard_into.setdefault(e["target_skill_id"], set()).add(e["prerequisite_skill_id"])
known = set(ids) | set(earlier_ids)
open_hard = sorted((p, t) for t in ids for p in hard_into.get(t, ()) if p not in known)
check("E15E-03_closed_under_hard_prerequisites", open_hard == [], f"a Skill that could never become ready: {open_hard}")
check("E15E-03_content_per_skill", sorted(skill_docs) == sorted(ids), "")
own_objectives = sorted(o["id"] for d in skill_docs.values() for o in d["objectives"])
sixc_objectives = sorted(k for k, o in objectives6c.items() if o["owner_skill_id"] in ids)
check("E15E-03_objective_identity", own_objectives == sixc_objectives and len(own_objectives) == 5, str(own_objectives))
expected_edges = sorted((e["prerequisite_skill_id"], e["target_skill_id"], e["edge_kind"]) for e in edges6c
                        if e["target_skill_id"] in ids and e["prerequisite_skill_id"] in known)
pkg_edges = re.findall(r"\[prerequisite_edge\]\nprerequisite=(\S+)@v1\ntarget=(\S+)@v1\nedge_version=1\nedge_kind=(\w+)\n"
                       r"reason_kind=\w+\nstrictness_profile=\w+\nlifecycle_status=(\w+)", asset)
check("E15E-03_edges_ratified", sorted((p, t, k) for p, t, k, _ in pkg_edges) == expected_edges
      and len(pkg_edges) == contract.get("scope", {}).get("edges") and all(l == "published" for *_, l in pkg_edges), f"{len(pkg_edges)} edges")
check("E15E-03_builds_on_earlier", sum(1 for p, *_ in pkg_edges if p in earlier_ids) == contract.get("scope", {}).get("edges_from_earlier"), "")
entry = sorted(s for s in ids if not (hard_into.get(s, set()) & known))
check("E15E-03_no_entry_point", entry == [] == contract.get("entry_skills"), str(entry))

# ---------------------------------------------------------------- Objective reconciliation
for d in skill_docs.values():
    for o in d["objectives"]:
        six = objectives6c.get(o["id"], {})
        check(f"E15E-04_recorded_{tag(o['id'])}", all(o.get(k) for k in ("statement", "observable_behavior", "reconciliation"))
              and o.get("required_direct_type") == six.get("evidence_profile", {}).get("required_direct_type")
              and set(o["acceptable_evidence_types"]) == set(six.get("evidence_profile", {}).get("acceptable_evidence_types", [])), o["id"])
statements = [o["statement"] for d in skill_docs.values() for o in d["objectives"]]
check("E15E-04_no_template_statement", len(set(statements)) == len(statements) and not any("Yeni bir bağlamda" in s for s in statements), "")

# ---------------------------------------------------------------- items
items = report["items"]
item_review = (review or {}).get("items", {}) or {}
check("E15E-05_review_method", bool((review or {}).get("method")) and review.get("reviewer") == "independent_15e_content_review", "")
check("E15E-05_every_item_reviewed", sorted(item_review) == sorted(i["item"] for i in items), f"{len(item_review)} / {len(items)}")
check("E15E-05_every_item_passed_review", all(v.get("verdict") == "pass" for v in item_review.values()),
      str([k for k, v in item_review.items() if v.get("verdict") != "pass"][:5]))
check("E15E-05_every_item_validated", all(i["status"] == "validated" for i in items), "")
check("E15E-05_no_hidden_prerequisite", all(not i["hidden_prerequisites"] for i in items), "")
modes = {i["verify_mode"] for i in items}
check("E15E-05_modes", modes == {"shell", "rubric"}, str(modes))
counts = contract.get("item_counts", {})
by_mode = {m: sum(1 for i in items if i["verify_mode"] == m) for m in ("shell", "rubric")}
check("E15E-05_counts", (len(items), by_mode["shell"], by_mode["rubric"]) == (counts.get("total"), counts.get("shell"), counts.get("rubric")),
      f"{len(items)} {by_mode}")
check("E15E-05_keys_run_in_linux", all(i["verification"].startswith(("run in bash in an empty directory in Linux", "executed; true value"))
      for i in items if i["verify_mode"] == "shell"), "")
check("E15E-05_rubrics_provisional", all(i["verification"] == "open response; judged by its rubric (OREX-v0), provisional at most"
      for i in items if i["verify_mode"] == "rubric"), "")
all_items = [it for d in skill_docs.values() for o in d["objectives"] for it in o["items"]]
shell_items = [it for it in all_items if (it.get("verify") or {}).get("mode") == "shell"]
# The learner is shown exactly what runs: no hidden script; a probe only prints what the question asks about.
check("E15E-05_shown_is_run", shell_items != [] and all(it.get("code") and "script" not in it["verify"] for it in shell_items),
      str([it["slug"] for it in shell_items if not it.get("code") or "script" in it["verify"]]))
check("E15E-05_shell_prelude", pkg.get("shell_prelude") == PRELUDE, "the fixed Git identity, no user or system Git configuration, the C.UTF-8 locale")
# A setup line ("# hazırlık") only records a starting commit, only where commits are not yet taught, and is shown.
setup_lines = [(d["skill"], line.strip()) for d in skill_docs.values() for o in d["objectives"] for it in o["items"]
               for line in (it.get("code") or "").splitlines() if SETUP_MARK.search(line)]
check("E15E-05_setup_lines_only_record", setup_lines != [] and all(re.match(r"git (add|commit -q -m) ", line) for _, line in setup_lines)
      and {s for s, _ in setup_lines} == {"skill.git.repository_status_diff"}, str(sorted({s for s, _ in setup_lines})))
check("E15E-05_setup_lines_shown", all(line in asset for _, line in setup_lines), "the learner sees every setup line it runs")
check("E15E-05_setup_explained", "# hazırlık" in skill_docs.get("skill.git.repository_status_diff", {}).get("objectives", [{}])[0]
      .get("explanations", {}).get("canonical", ""), "the lesson says what a setup line is")
# No key may depend on what the checking machine's Git makes up for itself.
volatile = re.compile(r"\b[0-9a-f]{7,40}\b|master|main\b|Ogrenci|example\.com|\b20\d\d-\d\d-\d\d")
keys = [str(it.get("answer", "")) for it in shell_items] + [str(v) for it in shell_items for v in (it["verify"].get("outcomes") or {}).values()]
check("E15E-05_no_machine_dependent_key", not any(volatile.search(k) for k in keys), str([k for k in keys if volatile.search(k)][:3]))
item_rows = {m.group(1): m.group(0) for m in re.finditer(r"\[item\]\nref=(\S+)@v1\n(?:[^\n]+\n)+", asset + "\n")}
for d in skill_docs.values():
    for o in d["objectives"]:
        oid = o["id"]
        measuring = [r for r in item_rows.values() if f"target_objectives={oid}@v1\n" in r and "deterministic_verification=true" in r
                     and f"evidence_type={o['required_direct_type']}\n" in r]
        families = {re.search(r"variant_family_id=(\S+)", r).group(1) for r in measuring}
        transfer = [r for r in measuring if "difficulty_class=transfer_integration" in r]
        check(f"E15E-05_measurable_{tag(oid)}", len(families) >= 6 and transfer != [], f"{len(families)} families")
check("E15E-05_ai_origin_declared", "content_origin=human_authored" not in asset and asset.count("content_origin=ai_generated") > 0, "")
at_computer = [r for r in item_rows.values() if re.search(r"evidence_type=hands_on_system_task\n", r)]
check("E15E-05_terminal_at_the_computer", len(at_computer) == counts.get("hands_on")
      and all("allowed_tools=terminal,documentation\n" in r and "prohibited_solution_sources=external_ai\n" in r for r in at_computer), "")
check("E15E-05_observation_not_runnable", all("prohibited_solution_sources=terminal,compiler,debugger,external_ai" in r
      for r in item_rows.values() if r not in at_computer), "predicting what a process leaves behind: the terminal would answer it")

# ---------------------------------------------------------------- shown exactly (D-114, unchanged)
format_kt = strip_comments(read(FORMAT_KT))
unescape = body(format_kt, "fun unescape(")
check("E15E-06_unescape_rule_unchanged", "next == 'n' || next == '\\\\'" in unescape and "append(if (next == 'n') '\\n' else '\\\\')" in unescape, "")
check("E15E-06_package_escapes", pkg.get("escape_backslash") is True, "")
check("E15E-06_kotlin_unchanged", contract.get("scope", {}).get("kotlin_main_changed") is False, "")

# ---------------------------------------------------------------- lessons
expl_review = (review or {}).get("explanations", {}) or {}
expl_ids = re.findall(r"\[explanation\]\nlogical_id=(\S+)", asset)
check("E15E-07_every_explanation_reviewed", sorted(expl_review) == sorted(expl_ids), f"{len(expl_review)} / {len(expl_ids)}")
check("E15E-07_every_explanation_passed", all(v.get("verdict") == "pass" for v in expl_review.values()),
      str([k for k, v in expl_review.items() if v.get("verdict") != "pass"][:5]))
checks_run = report.get("explanation_checks", [])
check("E15E-07_lesson_claims_executed", len(checks_run) >= contract.get("lesson_checks_min", 1) and all(c["result"] == "PASS" for c in checks_run),
      f"{len(checks_run)} checks")
check("E15E-07_lesson_checks_are_shell", pkg.get("lesson_checks") == "shell"
      and all(c.get("lang", "shell") == "shell" for d in skill_docs.values() for o in d["objectives"] for c in o.get("checks", [])), "")
for d in skill_docs.values():
    for o in d["objectives"]:
        check(f"E15E-07_lesson_{tag(o['id'])}", bool(o["explanations"].get("canonical")) and "worked_example" in o["explanations"]
              and "prerequisite_refresh" in o["explanations"] and len(o.get("misconceptions", [])) >= 2
              and all(m["open_question"].rstrip().endswith("?") for m in o["misconceptions"]), "")
COMBINING_DOT = chr(0x0307)
check("E15E-07_no_combining_dot", COMBINING_DOT not in asset, "")

# ---------------------------------------------------------------- notation
constructs = notation.get("constructs", {})
introducers = {s for c in constructs.values() for s in c["introduced_by"]}
check("E15E-08_introducers_known", introducers <= known, str(introducers - known))
never = sorted(c for c, v in constructs.items() if not v["introduced_by"])
check("E15E-08_forbidden_constructs", never == sorted(contract.get("never_introduced", [])), str(never))
for construct, owner in (("pipe", ["skill.shell.pipeline_redirection"]), ("redirect_stderr", ["skill.shell.pipeline_redirection"]),
                         ("exit_status", ["skill.linux.process_exit_stdout_stderr_basic"]),
                         ("redirect_stdout", ["skill.shell.command_options_redirection_basic"]),
                         ("git_inspect", ["skill.git.repository_status_diff"]), ("git_record", ["skill.git.stage_commit_history_basic"])):
    check(f"E15E-08_{construct}_owned", constructs.get(construct, {}).get("introduced_by") == owner, str(constructs.get(construct)))
check("E15E-08_setup_lines_stripped", r"^.*#\s*hazırlık\s*$" in notation.get("strip", []), "a setup line is copied, never scanned")

# ---------------------------------------------------------------- tasks
tasks = re.findall(r"\[task\]\nlogical_id=task\.(\S+)\.(teach|practice|check|review|repair)\n(?:[^\n]+\n)+", asset + "\n")
check("E15E-09_five_per_skill", len(tasks) == 25 and all(sum(1 for t in tasks if t[0] == s.split(".", 1)[1]) == 5 for s in ids), f"{len(tasks)}")
check("E15E-09_tasks_validated", all(t["status"] == "validated" for t in report["tasks"]), "")
minutes = [int(m) for m in re.findall(r"\[task\]\n(?:[^\n]+\n)*?cost_minutes=(\d+)", asset)]
check("E15E-09_minutes_are_estimates", contract.get("task_minutes", {}).get("calibrated") is False and all(0 < m <= 20 for m in minutes), "")
status_tasks = re.findall(r"\[task\]\nlogical_id=task\.git\.repository_status_diff\.(?:practice|check|review|repair)\n(?:[^\n]+\n)+", asset + "\n")
check("E15E-09_git_work_declares_files", status_tasks != [] and all("skill.shell.command_options_redirection_basic@v1" in t for t in status_tasks),
      "making a file to inspect is not in 6C's graph for Git; the work declares it (3B §10)")

# ---------------------------------------------------------------- the builder runs what is shown, the same way everywhere
builder_src = read(BUILDER)
shell_mode = builder_src[builder_src.find('    if mode == "shell":'):builder_src.find('    if mode == "reference":')]
check("E15E-10_shown_code_runs", 'script = item["code"].rstrip("\\n") + "\\n" + v.get("probe", "")' in shell_mode, "")
check("E15E-10_prelude_everywhere", 'in_linux(["bash", "-c", SHELL_PRELUDE + script], d)' in builder_src
      and 'SHELL_PRELUDE = pkg.get("shell_prelude", "")' in builder_src, "every item and lesson check gets the same first lines")
check("E15E-10_earlier_have_no_prelude", all("shell_prelude" not in read(d / "package.yaml") for d in EARLIER), "")

# ---------------------------------------------------------------- tests
for path, names in ((SHIPPED_TEST, ["everything shipped was validated, so nothing is a candidate",
                                    "the commands are shown exactly as the check runs them, setup lines included, one command per line",
                                    "every item is judged by exactly one thing, and work at the terminal allows it but never an AI"]),
                    (COURSE_TEST, ["all five shipped packages are published into the real store, oldest first, and Git waits on the terminal",
                                   "with the Linux, shell and Git package, a learner who has done nothing still starts only the three entry lessons"])):
    text = read(path)
    for name in names:
        check(f"E15E-11_test_{name[:40]}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results") if isinstance(contract.get("mutation_results"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("E15E-12_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("E15E-12_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("final_run_is_a_single_clean_run") is True, "")
check("E15E-12_control", mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("E15E-12_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("E15E-12_runs_pass", len(runs) >= 5 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") if isinstance(contract.get("device_verification"), dict) else {}
check("E15E-12_t6_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("sqlite_publish_on_jvm_run") is True
      and dv.get("sqlite_publish_on_device_run") is False, "")

# ---------------------------------------------------------------- spec, research, decision
spec_text = read(SPEC)
check("E15E-13_spec_status", "**Status:** ACCEPTED — independent 15E QA PASS" in spec_text and "`D-118`" in spec_text, "")
check("E15E-13_spec_next", "**15F — English A0→A1/A2 başlangıç paketi**" in spec_text, "")
check("E15E-13_spec_t6", "**Not run: T6.**" in spec_text, "")
research = read(RESEARCH)
check("E15E-13_research", all(url in research for url in (
    "https://www.gnu.org/software/bash/manual/bash.html",
    "https://git-scm.com/docs/git-status",
    "https://git-scm.com/docs/git-commit")), "every source the lessons rest on is named by its URL")
check("E15E-13_decision", re.search(r"^#+ .*D-118", read(DECISIONS), re.M) is not None, "D-118 heading in DECISIONS")

passed = sum(1 for r in results if r["result"] == "PASS")
out = {"model": "LGSX-v0", "stage_step": "15E", "decision": "D-118", "result": "PASS" if not failures else "FAIL",
       "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
if "--no-write" not in sys.argv:
    QA_OUT.parent.mkdir(parents=True, exist_ok=True)
    QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"15E QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
for failure in failures:
    print("  FAIL", failure)
sys.exit(0 if not failures else 1)
