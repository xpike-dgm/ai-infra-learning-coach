"""Independent 15H QA — CQAX-v0 content QA supplement.

The rules are read from the accepted contracts — `PRG-v0` §§2, 6, 17 (hard and soft, a task's own requirement),
`KGC-v0` §12 (edges), `GRE-v0` §§8–9 (the critical gate), `AIV-v0` (a validated item is checked, not assumed), `CDEX-v0`
(code is judged by what it does), `QAB-v0` (difficulty labels), `LFPS-v0` (a published version is never overwritten),
D-113 / D-120 (incremental packages, supplements) and the user's 15H decisions — and compared with the content source,
all eight shipped packages and the real Kotlin. The eighth package is rebuilt here from its source — every revised and
added key run again by the content directory it belongs to — and must be byte-identical to what ships, as must the seven
earlier packages; the content audit (tools/content_qa.py) is run again and must find nothing unresolved.

What this step exists to prevent must be unrepresentable as a PASS: a supplement that overwrites or carries again anything
published, a revised item whose old version stays selectable or whose task still presents it, a suite the constant-output
program passes without a recorded reason, a revision that changes more than it says, an edge whose rule does not hold or
that makes the graph cyclic, a lesson need the graph does not say, a lesson-code finding with no reason, a published item
using a construct no lesson teaches with no taught alternative, a generous label left undecided, a technical Objective
below 15, a critical Objective published without its gate fields, and anything shipped that no independent reviewer passed.
"""

from __future__ import annotations

import copy
import hashlib
import importlib.util
import pickle
import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTENT = ROOT / "curriculum/content/15h_content_qa"
SOURCES = ["15a_computing_fundamentals", "15b_python_foundations", "15c_c_foundations", "15d_memory_foundations",
           "15e_linux_git_shell", "15f_english_a1_a2"]
ASSETS = ANDROID / "app-wiring/src/main/assets"
ASSET = ASSETS / "curriculum_package_v8.txt"
EARLIER_ASSETS = [ASSETS / "curriculum_package.txt"] + [ASSETS / f"curriculum_package_v{n}.txt" for n in range(2, 8)]

CONTRACT = ROOT / "arch/15h_content_qa/content_qa.yaml"
VERIFICATION = ROOT / "arch/15h_content_qa/content_verification.yaml"
AUDIT = ROOT / "arch/15h_content_qa/content_qa_audit.yaml"
SPEC = ROOT / "docs/CONTENT_QA_SPEC.md"
RESEARCH = ROOT / "research/15h_content_qa_research.md"
QA_OUT = ROOT / "arch/15h_content_qa/qa_report.yaml"
DECISIONS = ROOT / "docs/DECISIONS.md"
BUILDER = ROOT / "tools/build_curriculum_package.py"
AUDITOR = ROOT / "tools/content_qa.py"

STORE = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/CurriculumStore.kt"
STORE_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/CurriculumPublishingTest.kt"
SHIPPED_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/ShippedContentQaPackageTest.kt"
COURSE_TEST = ANDROID / "app-wiring/src/test/kotlin/coach/wiring/ShippedCourseTest.kt"

RANK = {"basic": 0, "authentic_application": 1, "transfer_integration": 2}

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


def sections(text: str, name: str) -> list[dict]:
    out = []
    for block in text.split("\n\n"):
        if block.startswith(f"[{name}]\n"):
            out.append(dict(line.split("=", 1) for line in block.split("\n")[1:] if "=" in line))
    return out


def finish() -> None:
    passed = sum(1 for r in results if r["result"] == "PASS")
    out = {"model": "CQAX-v0", "stage_step": "15H", "decision": "D-121", "result": "PASS" if not failures else "FAIL",
           "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
    if "--no-write" not in sys.argv:
        QA_OUT.parent.mkdir(parents=True, exist_ok=True)
        QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
    print(f"15H QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(0 if not failures else 1)


def module(path: Path, name: str):
    spec = importlib.util.spec_from_file_location(name, path)
    mod = importlib.util.module_from_spec(spec)
    sys.modules[name] = mod
    spec.loader.exec_module(mod)
    return mod


# ---------------------------------------------------------------- inputs
for path in (CONTRACT, VERIFICATION, AUDIT, SPEC, RESEARCH, ASSET, *EARLIER_ASSETS, BUILDER, AUDITOR, CONTENT / "package.yaml",
             CONTENT / "graph.yaml", CONTENT / "notation_overlay.yaml", CONTENT / "fixed_output.yaml", CONTENT / "alt_references.yaml",
             CONTENT / "lesson_findings.yaml", CONTENT / "label_decisions.yaml", CONTENT / "independent_review.yaml",
             STORE, STORE_TEST, SHIPPED_TEST, COURSE_TEST):
    check(f"A15H-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
pkg = load(CONTENT / "package.yaml") or {}
graph = load(CONTENT / "graph.yaml") or {}
review = load(CONTENT / "independent_review.yaml") or {}
verification = load(VERIFICATION) or {}
asset = read(ASSET)
earlier_text = "\n\n".join(read(a) for a in EARLIER_ASSETS)

check("A15H-01_model", contract.get("model") == "CQAX-v0", str(contract.get("model")))
check("A15H-01_status", contract.get("status") == "accepted_15h", str(contract.get("status")))
check("A15H-01_decision", contract.get("decision") == "D-121", str(contract.get("decision")))
ud = contract.get("user_decisions", {}) or {}
check("A15H-01_user_decisions", ud.get("approval") == "explicit_user_approval_2026-10-10"
      and ud.get("graph") == "add_content_edges_hard_only_where_lesson_and_every_item_else_soft"
      and ud.get("requires_transfer") == "close_no_gate_on_standard_refuse_critical_until_stored"
      and ud.get("flawed_items") == "publish_next_version_retire_old_by_appended_record"
      and ud.get("pool") == "technical_to_15_english_keeps_small_pool", str(ud))
check("A15H-01_package", pkg.get("version") == 8 and pkg.get("step") == "15h" and pkg.get("constant_check") is True
      and pkg.get("supplements") == [f"curriculum/content/{s}" for s in SOURCES]
      and pkg.get("earlier_supplements") == ["curriculum/content/15g_assessment"], "")

# ---------------------------------------------------------------- the package that ships is the package that was checked
builder = module(BUILDER, "build_curriculum_package")
auditor = module(AUDITOR, "content_qa")


def build_inputs_digest() -> str:
    """Every file the supplement build reads: the builder, the runner, the 15H and 15G content and the six directories."""
    h = hashlib.sha256()
    files = [BUILDER, ROOT / "tools/code_test_runner.py"]
    for base in [CONTENT, ROOT / "curriculum/content/15g_assessment"] + [ROOT / "curriculum/content" / s for s in SOURCES]:
        files += sorted(p for p in base.rglob("*") if p.is_file() and "suites" not in p.relative_to(base).parts)
    files += sorted(p for p in (ROOT / "curriculum/decomposition/6c_foundations").rglob("*") if p.is_file())
    for p in files:
        h.update(str(p.relative_to(ROOT)).encode() + b"\0" + p.read_bytes() + b"\0")
    return h.hexdigest()


try:
    # `--build-cache FILE` exists only for this validator's own mutation run: a fresh build is reused only while every
    # build input is byte-identical to the one it was made from; any change to the builder or the content rebuilds.
    cache = Path(sys.argv[sys.argv.index("--build-cache") + 1]) if "--build-cache" in sys.argv else None
    digest = build_inputs_digest() if cache else None
    cached = pickle.loads(cache.read_bytes()) if cache and cache.is_file() else None
    if cached and cached["digest"] == digest:
        rebuilt, report = cached["rebuilt"], copy.deepcopy(cached["report"])
    else:
        rebuilt, report = builder.build_supplement(CONTENT)
        if cache and not cache.is_file():
            cache.write_bytes(pickle.dumps({"digest": digest, "rebuilt": rebuilt, "report": copy.deepcopy(report)}))
except Exception as error:  # noqa: BLE001 — any failure to build is reported, not raised
    check("A15H-02_builds", False, f"{type(error).__name__}: {error}"[:300])
    finish()
check("A15H-02_builds", True, "")
suite_files = report.pop("suite_files", {})
check("A15H-02_shipped_is_rebuilt", rebuilt == asset, "the shipped asset differs from a fresh build of its source")
check("A15H-02_generated_marker", asset.splitlines()[1:2] == ["# Generated by tools/build_curriculum_package.py from curriculum/content/15h_content_qa — do not edit by hand."], "")
check("A15H-02_no_failures", report["failures"] == [], f"{len(report['failures'])}: {[f['item'] for f in report['failures']][:5]}")
check("A15H-02_report_recorded", verification.get("summary") == report["summary"] and verification.get("items") == report["items"]
      and verification.get("by_source") == report["by_source"] and verification.get("edges") == report.get("edges"),
      "arch/15h_content_qa/content_verification.yaml is not the report of this build")
stale_suites = sorted(iid for iid, body in suite_files.items() if read(CONTENT / "suites" / f"{iid}.json") != body)
check("A15H-02_suites_written", suite_files != {} and stale_suites == []
      and sorted(p.stem for p in (CONTENT / "suites").glob("*.json")) == sorted(suite_files), f"stale or extra: {stale_suites[:5]}")
if "--skip-earlier-rebuild" not in sys.argv:
    for name, a in zip(SOURCES, EARLIER_ASSETS):
        try:
            again, earlier_report = builder.build(ROOT / "curriculum/content" / name)
            untouched = again == read(a) and earlier_report["failures"] == []
            why = f"{len(earlier_report['failures'])} build failures" if again == read(a) else "the asset differs"
        except Exception as error:  # noqa: BLE001
            untouched, why = False, f"{type(error).__name__}: {error}"[:200]
        check(f"A15H-02_earlier_untouched_{name}", untouched, f"{name} is no longer a clean, fresh build of its source: {why}")
    try:
        again, g_report = builder.build_supplement(ROOT / "curriculum/content/15g_assessment")
        check("A15H-02_earlier_untouched_15g_assessment", again == read(EARLIER_ASSETS[6]) and g_report["failures"] == [], "")
    except Exception as error:  # noqa: BLE001
        check("A15H-02_earlier_untouched_15g_assessment", False, f"{type(error).__name__}: {error}"[:200])

# ---------------------------------------------------------------- a supplement corrects by appending; it never overwrites
for kind in ("domain", "module", "topic", "skill", "objective", "topic_skill", "explanation", "misconception"):
    check(f"A15H-03_adds_no_{kind}", f"\n[{kind}]\n" not in "\n" + asset, f"content QA adds no {kind}")
items = sections(asset, "item")
earlier_items = {s["ref"]: s for s in sections(earlier_text, "item")}
check("A15H-03_nothing_carried_again", items != [] and all(i["ref"] not in earlier_items for i in items), "")
revised = [i for i in items if not i["ref"].endswith("@v1")]
added = [i for i in items if i["ref"].endswith("@v1")]
check("A15H-03_revisions_are_next_versions", all(i["ref"].endswith("@v2") and i["ref"][:-1] + "1" in earlier_items for i in revised),
      str([i["ref"] for i in revised if i["ref"][:-1] + "1" not in earlier_items][:3]))
check("A15H-03_added_are_new", all(i["ref"].split("@")[0] not in {r.split("@")[0] for r in earlier_items} for i in added), "")
same_identity = all(earlier_items[i["ref"][:-1] + "1"][k] == i[k] for i in revised for k in ("target_objectives", "target_skills", "variant_family_id"))
check("A15H-03_revision_keeps_identity", same_identity, "a revision measures the same Objective, in the same variant family")
records = sections(asset, "validation")
instants = {r["validated_at_instant"] for r in records}
retired = {r["resource"] for r in records if r["status"] == "retired"}
check("A15H-03_old_version_retired", retired == {i["ref"][:-1] + "1" for i in revised}, f"{len(retired)} retired, {len(revised)} revised")
check("A15H-03_every_new_version_validated", {r["resource"] for r in records if r["status"] == "validated"} == {i["ref"] for i in items}
      and {r["status"] for r in records} == {"retired", "validated"}, "")
check("A15H-03_records_appended_after", instants == {str(pkg.get("validated_at_instant"))}
      and all(int(r["validated_at_instant"]) < int(pkg.get("validated_at_instant", 0)) for r in sections(earlier_text, "validation")), "")
check("A15H-03_counts", (len(items), len(revised), len(sections(asset, "task")), len(sections(asset, "prerequisite_edge")),
                         len(sections(asset, "answer_misconception")))
      == tuple((contract.get("counts") or {}).get(k) for k in ("items", "revised_items", "task_versions", "edges", "answer_misconceptions")),
      str(contract.get("counts")))

# A revision changes what it says it changes, and nothing else.
qa_items = auditor.qa_items()
docs = auditor.skill_docs()
published = {f"item.{o['id'].split('.', 1)[1]}.{it['slug']}": it for d in docs.values() for o in d["objectives"]
             for it in o["items"] + o.get("_15g", [])}
allowed = {"label": {"difficulty"}, "fixed_output": {"prompt", "source_checks", "suite"}, "review_fix": {"prompt", "also_accept", "verify"},
           "notation": {"prompt", "harness", "suite", "reference", "wrong"}}
decisions = load(CONTENT / "label_decisions.yaml") or {}
scope_bad = []
for oid, entry in qa_items.items():
    for rev in entry["revisions"]:
        iid = f"item.{oid.split('.', 1)[1]}.{rev['revises']}"
        reasons = str(rev["reason"]).split("+")
        changed = set(rev) - {"revises", "reason", "relabel_from"}
        may = set().union(*(allowed.get(r, set()) for r in reasons))
        base = published.get(iid, {})
        label_ok = "label" not in reasons or (rev.get("relabel_from") == base.get("difficulty")
                                             and RANK.get(rev.get("difficulty"), 9) < RANK.get(base.get("difficulty"), -1)
                                             and (decisions.get("revise") or {}).get(iid, {}).get("to") == rev.get("difficulty"))
        if not set(reasons) <= set(allowed) or not changed <= may or not label_ok:
            scope_bad.append(iid)
check("A15H-03_revision_changes_only_its_reason", qa_items != {} and scope_bad == [], str(scope_bad[:5]))

# ---------------------------------------------------------------- tasks: the next version, never presenting a retired item
tasks = sections(asset, "task")
earlier_tasks = sections(earlier_text, "task")
newest = {}
for t in earlier_tasks:
    newest[t["logical_id"]] = max(newest.get(t["logical_id"], 0), int(t["version"]))
check("A15H-04_task_next_version", all(int(t["version"]) == newest.get(t["logical_id"], 0) + 1 for t in tasks), "")
check("A15H-04_task_presents_no_retired", all(not (set(t["items"].split(",")) & retired) for t in tasks), "")
offered = {}
for t in earlier_tasks + tasks:
    if int(t["version"]) >= int(offered.get(t["logical_id"], {"version": 0})["version"]):
        offered[t["logical_id"]] = t
check("A15H-04_no_offered_task_presents_retired", all(not (set(t["items"].split(",")) & retired) for t in offered.values()),
      str([k for k, t in offered.items() if set(t["items"].split(",")) & retired][:3]))
for skill, spec in (graph.get("teach_requires") or {}).items():
    teach = offered[f"task.{skill.split('.', 1)[1]}.teach"]
    check(f"A15H-04_teach_requires_{skill.split('.')[-1]}", all(f"{r}@v1" in teach["required_skills"].split(",") for r in spec["requires"])
          and spec.get("why"), f"{teach['logical_id']}@v{teach['version']} requires {teach['required_skills']}")

# ---------------------------------------------------------------- the graph says what the lessons need
edges = sections(asset, "prerequisite_edge")
declared = graph.get("edges") or []
check("A15H-05_edges_shipped_as_declared", [(e["prerequisite"], e["target"], e["edge_kind"], e["reason_kind"]) for e in edges]
      == [(f"{d['prerequisite']}@v1", f"{d['target']}@v1", d["kind"], d["reason_kind"]) for d in declared] and edges != [], "")
check("A15H-05_edge_rules_hold", report.get("edges") and all(r["result"] == "PASS" for r in report["edges"])
      and all(builder.EDGE_RULES.get(d["rule"]) == d["kind"] for d in declared), "")
check("A15H-05_edge_fields", all(e["edge_version"] == "1" and e["lifecycle_status"] == "published" and e["strictness_profile"] == "default_prg_v0"
                                 and e["reason_kind"] in {"evidence_interpretability", "conceptual_dependency", "language_dependency"} for e in edges), "")
earlier_pairs = {(e["prerequisite"], e["target"]) for e in sections(earlier_text, "prerequisite_edge")}
check("A15H-05_edges_new", all((e["prerequisite"], e["target"]) not in earlier_pairs for e in edges), "")
hard: dict[str, set[str]] = {}
for e in sections(earlier_text, "prerequisite_edge") + edges:
    if e["edge_kind"] == "hard":
        hard.setdefault(e["target"], set()).add(e["prerequisite"])
cyclic = [s for s in hard if s in builder.closure_of(s, hard)]
check("A15H-05_acyclic", cyclic == [], str(cyclic[:3]))
check("A15H-05_builder_rules", all(s in read(BUILDER) for s in ('"lesson_and_every_item": "hard"', '"lesson": "soft"', '"reads_glossed": "soft"',
                                                                 "the lesson requires it and the graph does not say so",
                                                                 "hard graph with lesson requirements")), "")

# ---------------------------------------------------------------- the audit of what Stage 15 published
run_audit = "--skip-audit-runs" not in sys.argv
audit = {
    "fixed_output": auditor.audit_fixed_output(docs, run_audit),
    "notation": auditor.audit_notation(docs, run_audit),
    "lesson_checks": auditor.audit_lesson_checks(docs),
    "lesson_text": auditor.audit_lesson_text(docs),
    "graph": auditor.audit_graph(docs),
    "labels": auditor.audit_labels(),
    "pools": auditor.audit_pools(docs),
    "criticality": auditor.audit_criticality(),
}
recorded = load(AUDIT) or {}
for name, rows in audit.items():
    check(f"A15H-06_audit_{name}_resolved", rows != [] and all(r.get("resolution") is not None for r in rows),
          str([r for r in rows if r.get("resolution") is None][:2]))
    if run_audit:
        check(f"A15H-06_audit_{name}_recorded", recorded.get(name) == rows, "arch/15h_content_qa/content_qa_audit.yaml is not this audit")
check("A15H-06_audit_result", recorded.get("result") == "PASS" and recorded.get("runs") is True, "")
fixed = load(CONTENT / "fixed_output.yaml") or {}
found = {r["item"] for r in audit["fixed_output"]}
check("A15H-06_exemptions_used_and_reasoned", not fixed.get("objectives") and fixed.get("items")
      and all(v and len(str(v)) > 40 for v in fixed["items"].values()) and set(fixed["items"]) <= found,
      "every exemption is one item's, names a finding and says why (an Objective-wide exemption hid two computations)")
findings = load(CONTENT / "lesson_findings.yaml") or {}
keys = {r["finding"] for r in audit["lesson_checks"] + audit["lesson_text"]}
check("A15H-06_lesson_findings_not_stale", set(findings) == keys and all(v.get("note") for v in findings.values()),
      f"stale: {sorted(set(findings) - keys)[:3]} missing: {sorted(keys - set(findings))[:3]}")
alt = load(CONTENT / "alt_references.yaml") or {}
check("A15H-06_alternatives_not_stale", set(alt) == {r["item"] for r in audit["notation"] if r["resolution"] == "alternative_reference"}, "")
small = sorted(r["objective"] for r in audit["pools"] if r["resolution"] == "recorded_small_pool")
check("A15H-06_small_pools_recorded", small == sorted(contract.get("recorded_small_pools") or []), str(small))
check("A15H-06_technical_pools", all(r["outside_lesson"] >= 15 for r in audit["pools"] if not r["english"]), "")

# ---------------------------------------------------------------- requires_transfer: closed, not invented
src = read(BUILDER)
check("A15H-07_critical_refused", "Objective needs GRE-v0's gate \"\n" in src and "\"fields stored, and the store has none (D-121)\")" in src
      and 'if o.get("criticality", o6["criticality"]) != "standard":' in src, "")
check("A15H-07_no_critical_published", all(s["criticality"] == "standard" for s in sections(earlier_text + "\n\n" + asset, "objective"))
      and len(sections(earlier_text, "objective")) == 65, "")

# ---------------------------------------------------------------- the review
review_items = review.get("items") or {}
labels = [i["ref"].split("@")[0] if i["ref"].endswith("@v1") else i["ref"] for i in items]
check("A15H-08_every_item_reviewed", all(review_items.get(k, {}).get("verdict") == "pass" for k in labels) and len(labels) == len(items), "")
check("A15H-08_every_edge_reviewed", all((review.get("edges") or {}).get(f"{d['prerequisite']}->{d['target']}", {}).get("verdict") == "pass" for d in declared), "")
check("A15H-08_every_lesson_need_reviewed", all((review.get("teach_requires") or {}).get(s, {}).get("verdict") == "pass" for s in (graph.get("teach_requires") or {})), "")
check("A15H-08_review_notes", all(len(str(v.get("note", ""))) > 20 for v in review_items.values()), "every verdict says what was checked")
check("A15H-08_rounds_recorded", isinstance(contract.get("independent_review"), dict) and contract["independent_review"].get("final") == "all_pass", "")

# ---------------------------------------------------------------- Kotlin: the store refuses to carry an edge or a record twice
store = strip_comments(read(STORE))
check("A15H-09_store_edge_refusal", "skill_prerequisite_edge ${it.prerequisite} -> ${it.target} v${it.edgeVersion} is already published and is never overwritten" in store
      and "private fun edgeExists(edge: PrerequisiteEdge)" in store
      and "curriculum.prerequisiteEdges.filter(::edgeExists)" in store, "")
check("A15H-09_store_record_refusal", "is already recorded and is never overwritten" in store and "curriculum.validationRecords.filter(::recordExists)" in store, "")
for path, names in ((STORE_TEST, ["a later package retires an earlier item version by an appended record and adds an edge, and neither is carried twice"]),
                    (SHIPPED_TEST, ["the package is version 8, reads after the seven it corrects, and adds no Skill, Objective, Topic, lesson or label",
                                    "every edge joins two published Skills, is new, and the whole graph stays acyclic",
                                    "a revised item is the next version of a published one, same family, and the version it replaces is retired",
                                    "a task is published again only as its next version, presents no retired item, and only the newest is offered",
                                    "no Objective of any shipped package is critical, because the store keeps no critical gate fields"]),
                    (COURSE_TEST, ["all eight shipped packages are published into the real store, and a revised item's old version is retired, not overwritten",
                                   "with content QA, a learner who has done nothing starts the same entry lessons, and a lesson waits for what it shows"])):
    text = read(path)
    for name in names:
        check(f"A15H-10_test_{re.sub(r'[^a-z0-9]+', '_', name[:40].lower()).strip('_')}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- verification claims
for key in ("mutation_results_builder", "mutation_results_audit", "mutation_results_kotlin"):
    mr = contract.get(key) if isinstance(contract.get(key), dict) else {}
    mids = [m.get("id") for m in mr.get("mutants", [])]
    check(f"A15H-11_{key}_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
          and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
    check(f"A15H-11_{key}_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("A15H-11_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("A15H-11_runs_pass", len(runs) >= 7 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") if isinstance(contract.get("device_verification"), dict) else {}
check("A15H-11_t6_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("sqlite_publish_on_jvm_run") is True
      and dv.get("sqlite_publish_on_device_run") is False, "")

# ---------------------------------------------------------------- spec, research, decision
spec_text = read(SPEC)
check("A15H-12_spec_status", "**Status:** ACCEPTED — independent 15H QA PASS" in spec_text and "`D-121`" in spec_text
      and not re.search(r"\b(?:MB|MA|MV)_RESULT\b|__[A-Z][A-Z_]*__", spec_text), "no result placeholder is left")
check("A15H-12_spec_next", "**16A — Skill analytics**" in spec_text, "")
check("A15H-12_spec_t6", "**Not run: T6.**" in spec_text, "")
research = read(RESEARCH)
check("A15H-12_research", all(ref in research for ref in ("PRG-v0", "KGC-v0", "GRE-v0", "CDEX-v0", "https://en.wikipedia.org/wiki/Mutation_testing",
                                                         "https://en.wikipedia.org/wiki/Test_oracle")), "every source is named")
check("A15H-12_decision", re.search(r"^#+ .*D-121", read(DECISIONS), re.M) is not None, "D-121 heading in DECISIONS")

finish()
