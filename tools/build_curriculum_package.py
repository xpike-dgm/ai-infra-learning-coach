#!/usr/bin/env python3
"""Builds an authored curriculum package (`curriculum_package/1`) from a content directory — 15A, CPFX-v0 / D-112.

The content directory holds the human-readable source: `package.yaml` (graph ratification and refinements over the
6C draft), `notation.yaml` (which reading constructs each Skill's lesson introduces) and one YAML file per Skill
(lesson text, alternatives, misconception catalog, items, tasks).

Nothing is trusted because it was written down. Every item's answer key is checked before it may be published as
validated (AIV-v0 §3-§8, §23-§25):

* an item with code is **executed** (CPython, isolated, with a timeout) and the key must be what the code really does;
* a choice item must have exactly one option that is true, checked by running each option where it is code;
* an item that cannot be executed must cite an authoritative reference that the independent review confirmed;
* and every item, executed or not, must carry a `pass` verdict from the independent review file.

An item failing any of these is written as `candidate` (practice at best, never mastery evidence), and a task that
presents it is written as `candidate` too. The build is deterministic: the same content gives the same bytes.

Usage:
  python tools/build_curriculum_package.py curriculum/content/15a_computing_fundamentals \
      --out android/app-wiring/src/main/assets/curriculum_package.txt \
      --report arch/15a_computing_fundamentals/content_verification.yaml
"""
from __future__ import annotations

import argparse
import os
import re
import subprocess
import sys
import tempfile
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
DECOMPOSITION = ROOT / "curriculum/decomposition/6c_foundations"
PYTHON = sys.executable
TIMEOUT_SECONDS = 5
NONTERMINATION_SECONDS = 2
# 2026-10-02T12:00:00Z — the day the independent review of 15A content was recorded.
VALIDATED_AT = 1_790_942_400_000
CHOICE_KEYS = ["A", "B", "C", "D", "E"]


class BuildError(Exception):
    pass


def load(path: Path):
    with open(path, encoding="utf-8") as f:
        return yaml.safe_load(f)


# ------------------------------------------------------------------------------------------------ execution

def run_python(code: str, cwd: str | None = None, timeout: float = TIMEOUT_SECONDS, stdin: str = "") -> tuple[str, str, str]:
    """Runs [code] in an isolated CPython (`-I`: no user site, no environment). Returns (status, stdout, stderr)."""
    try:
        done = subprocess.run([PYTHON, "-I", "-X", "utf8", "-c", code], capture_output=True, text=True, timeout=timeout,
                              cwd=cwd, input=stdin, encoding="utf-8", env={"PYTHONIOENCODING": "utf-8", "SYSTEMROOT": os.environ.get("SYSTEMROOT", "")})
    except subprocess.TimeoutExpired:
        return "timeout", "", ""
    return ("ok" if done.returncode == 0 else "error"), done.stdout, done.stderr


def norm(text: str) -> str:
    return "\n".join(line.rstrip() for line in text.strip().splitlines())


def verify_item(item: dict) -> tuple[bool, str]:
    """Checks the item's key against what really happens. Returns (verified, how)."""
    v = item.get("verify") or {}
    mode = v.get("mode")
    key = str(item["answer"]).strip()
    options = {k: str(t) for k, t in (item.get("options") or {}).items()}

    # An option may combine an outcome with its reason; `outcomes` then names the outcome each option claims, and
    # it is the outcome that is checked against what really happens.
    claims = {k: str(t) for k, t in (v.get("outcomes") or options).items()}

    def keyed_option_matches(actual: str) -> tuple[bool, str]:
        hits = [k for k, text in claims.items() if norm(text) == norm(actual)]
        if hits != [key]:
            return False, f"true value {actual!r} matches options {hits}, key is {key}"
        return True, f"executed; true value {actual!r} is option {key} and no other"

    if mode == "stdout":
        status, out, err = run_python(item["code"])
        # `expect_error`: the program is meant to fail, and what it printed before failing is the answer.
        if status != ("error" if v.get("expect_error") else "ok"):
            return False, f"code did not run as declared: {status} {err.strip()[-200:]}"
        if v.get("last_line"):
            out = (out.strip().splitlines() or [""])[-1]
        if options:
            return keyed_option_matches(out)
        if norm(out) != norm(key):
            return False, f"code prints {norm(out)!r}, key is {key!r}"
        return True, "executed; the key is exactly what the code prints"

    if mode == "probe":
        lines = item["code"].rstrip("\n").split("\n")
        upto = v.get("upto")
        body = "\n".join(lines[:upto] if upto else lines)
        status, out, err = run_python(body + "\n" + v["probe"])
        if status != "ok":
            return False, f"probe did not run: {status} {err.strip()[-200:]}"
        return keyed_option_matches(out) if options else ((norm(out) == norm(key)), f"probe prints {norm(out)!r}")

    if mode == "termination":
        status, out, err = run_python(item["code"], timeout=NONTERMINATION_SECONDS)
        terminates = status == "ok"
        if status == "error":
            return False, f"code raised: {err.strip()[-200:]}"
        claim = v["keyed_terminates"]
        if claim != terminates:
            return False, f"key claims terminates={claim}, execution says terminates={terminates}"
        if "iterations_probe" in v and terminates:
            s2, o2, e2 = run_python(item["code"] + "\n" + v["iterations_probe"])
            if norm(o2) != norm(str(v["iterations"])):
                return False, f"iteration probe prints {o2!r}, expected {v['iterations']}"
        return True, f"executed with a {NONTERMINATION_SECONDS}s limit; terminates={terminates} as keyed"

    if mode == "behaviour":
        # The program, as a template over inputs, is run for every case; exactly the keyed option's reference agrees.
        cases = v["cases"]
        refs = v["refs"]
        actual = []
        for case in cases:
            code = v["template"].format(**case)
            status, out, err = run_python(code)
            if status != "ok":
                return False, f"template failed on {case}: {err.strip()[-200:]}"
            actual.append(norm(out))
        agree = []
        for k, ref in refs.items():
            expected = []
            for case in cases:
                status, out, err = run_python(f"f = {ref}\nprint(f(**{case!r}))")
                if status != "ok":
                    return False, f"reference {k} failed: {err.strip()[-200:]}"
                expected.append(norm(out))
            if expected == actual:
                agree.append(k)
        if agree != [key]:
            return False, f"options agreeing with the program on all {len(cases)} cases: {agree}; key {key}"
        return True, f"executed on {len(cases)} cases; only option {key} describes the program"

    if mode == "functions":
        # Each option is a function; exactly the keyed one meets the specification on every case.
        spec = v["spec"]
        cases = v["cases"]
        name = v["name"]
        passing = []
        for k, src in (v.get("sources") or options).items():
            ok = True
            for case in cases:
                prog = f"{src}\n{spec}\nimport sys\nr = {name}(*{case!r})\ne = spec(*{case!r})\nprint(r == e and type(r) == type(e))"
                status, out, err = run_python(prog)
                if status != "ok" or out.strip() != "True":
                    ok = False
                    break
            if ok:
                passing.append(k)
        if passing != [key]:
            return False, f"options meeting the specification: {passing}; key {key}"
        return True, f"executed on {len(cases)} cases; only option {key} meets the specification"

    if mode == "detects":
        # Each option is a test input; exactly the keyed one tells the faulty function from the correct one.
        detect = []
        for k, args in v["inputs"].items():
            prog = (f"{v['correct']}\ncorrect = {v['name']}\n{v['faulty']}\nfaulty = {v['name']}\n"
                    f"print(correct(*{args!r}) != faulty(*{args!r}))")
            status, out, err = run_python(prog)
            if status != "ok":
                return False, f"input {k} did not run: {err.strip()[-200:]}"
            if out.strip() == "True":
                detect.append(k)
        if detect != [key]:
            return False, f"inputs that detect the fault: {detect}; key {key}"
        return True, f"executed; only input {key} separates the faulty function from the correct one"

    if mode == "fault":
        # The program misbehaves; replacing exactly the keyed line with the fix makes it behave, and the symptom
        # stated in the prompt is what the faulty program really prints.
        status, out, err = run_python(item["code"])
        if status != "ok" or norm(out) != norm(str(v["observed"])):
            return False, f"faulty code prints {norm(out)!r} ({status}), prompt says {v['observed']!r}"
        lines = item["code"].rstrip("\n").split("\n")
        line_no = v["line_options"][key]
        fixed = list(lines)
        indent = re.match(r"\s*", fixed[line_no - 1]).group(0)
        fixed[line_no - 1] = indent + v["fix"].strip()
        status, out2, err = run_python("\n".join(fixed))
        if status != "ok" or norm(out2) != norm(str(v["intended"])):
            return False, f"fixed code prints {norm(out2)!r}, intended {v['intended']!r}"
        if norm(out) == norm(str(v["intended"])):
            return False, "the faulty code already behaves as intended"
        return True, f"executed; the symptom is real and fixing line {line_no} alone gives the intended output"

    if mode == "script":
        # A verification script (run in an empty temporary directory) prints the true answer.
        with tempfile.TemporaryDirectory() as d:
            status, out, err = run_python(v["script"], cwd=d)
        if status != "ok":
            return False, f"script failed: {err.strip()[-300:]}"
        return keyed_option_matches(out) if options else ((norm(out) == norm(key)), f"script prints {norm(out)!r}")

    if mode == "fix_terminates":
        # Each option replaces one line of a non-terminating loop; exactly the keyed replacement makes it terminate.
        status, out, err = run_python(item["code"], timeout=NONTERMINATION_SECONDS)
        if status != "timeout":
            return False, f"the program shown is meant not to terminate, but it ended ({status})"
        lines = item["code"].rstrip("\n").split("\n")
        indent = re.match(r"\s*", lines[v["line"] - 1]).group(0)
        ends = []
        for k, replacement in v["replacements"].items():
            changed = list(lines)
            changed[v["line"] - 1] = indent + replacement.strip()
            status, out, err = run_python("\n".join(changed), timeout=NONTERMINATION_SECONDS)
            if status == "error":
                return False, f"option {k} raised: {err.strip()[-200:]}"
            if status == "ok":
                ends.append(k)
        if ends != [key]:
            return False, f"replacements that terminate: {ends}; key {key}"
        return True, f"executed with a {NONTERMINATION_SECONDS}s limit; only replacement {key} terminates"

    if mode == "which_input":
        # Each option is a starting assignment; exactly the keyed one makes the program print the target.
        hits = []
        for k, assignment in v["assignments"].items():
            status, out, err = run_python(assignment + "\n" + item["code"])
            if status != "ok":
                return False, f"option {k} did not run: {err.strip()[-200:]}"
            if norm(out) == norm(str(v["target"])):
                hits.append(k)
        if hits != [key]:
            return False, f"starting values that print {v['target']!r}: {hits}; key {key}"
        return True, f"executed for every option; only option {key} prints {v['target']!r}"

    if mode == "reference":
        if not v.get("reference") or not v.get("claim"):
            return False, "a reference item names its source and the claim it checks"
        return True, f"reference-grounded: {v['reference']}"

    if mode == "rubric":
        return True, "open response; judged by its rubric (OREX-v0), provisional at most"

    return False, f"unknown verify mode {mode!r}"


# ------------------------------------------------------------------------------------------------ notation

def constructs_used(code: str, notation: dict) -> set[str]:
    used = set()
    for construct, spec in notation["constructs"].items():
        if re.search(spec["pattern"], code, re.M):
            used.add(construct)
    return used


# ------------------------------------------------------------------------------------------------ package text

def esc(text: str) -> str:
    """One package line per value: a line break becomes a backslash-n (PackageFormat reads it back)."""
    text = text.strip("\n")
    if "\\n" in text:
        raise BuildError(f"content contains a literal backslash-n, which the format reads as a line break: {text[:60]!r}")
    return text.replace("\r", "").replace("\n", "\\n")


def section(name: str, values: list[tuple[str, object]]) -> str:
    lines = [f"[{name}]"]
    for k, v in values:
        if isinstance(v, bool):
            v = "true" if v else "false"
        elif isinstance(v, (list, tuple)):
            v = ",".join(str(x) for x in v)
        elif v is None:
            v = ""
        lines.append(f"{k}={v}")
    return "\n".join(lines)


def pin(ref: str) -> str:
    return f"{ref}@v1"


# ------------------------------------------------------------------------------------------------ build

def build(content_dir: Path, partial: bool = False) -> tuple[str, dict]:
    pkg = load(content_dir / "package.yaml")
    notation = load(content_dir / "notation.yaml")
    review_path = content_dir / "independent_review.yaml"
    review = load(review_path) if review_path.exists() else {}
    reviewed_items = (review or {}).get("items", {}) or {}
    reviewed_text = (review or {}).get("explanations", {}) or {}

    skills6c = {s["skill_id_candidate"]: s for s in load(DECOMPOSITION / "skills.yaml")}
    objectives6c = {o["objective_id_candidate"]: o for o in load(DECOMPOSITION / "objectives.yaml")}
    edges6c = load(DECOMPOSITION / "prerequisite_edges.yaml")
    links6c = load(DECOMPOSITION / "topic_skill_links.yaml")
    org6c = {o["logical_id_candidate"]: o for o in load(DECOMPOSITION / "organization_entities.yaml")}

    skill_ids = [s["id"] for s in pkg["skills"]]
    skill_files = {}
    for path in sorted((content_dir / "skills").glob("*.yaml")):
        doc = load(path)
        skill_files[doc["skill"]] = (path, doc)
    missing = [s for s in skill_ids if s not in skill_files]
    if missing and not partial:
        raise BuildError(f"skills without content: {missing}")
    if partial:
        # Authoring aid only: build what exists. A partial package is never shipped (`--out` is refused).
        pkg["skills"] = [s for s in pkg["skills"] if s["id"] in skill_files]
        skill_ids = [s["id"] for s in pkg["skills"]]

    hard_prereqs: dict[str, set[str]] = {s: set() for s in skill_ids}
    for e in edges6c:
        if e["target_skill_id"] in skill_ids and e["prerequisite_skill_id"] in skill_ids and e["edge_kind"] == "hard":
            hard_prereqs[e["target_skill_id"]].add(e["prerequisite_skill_id"])

    def closure(skill: str) -> set[str]:
        seen, todo = set(), [skill]
        while todo:
            s = todo.pop()
            for p in hard_prereqs.get(s, ()):
                if p not in seen:
                    seen.add(p)
                    todo.append(p)
        return seen

    introduced: dict[str, list[str]] = {c: list(spec["introduced_by"]) for c, spec in notation["constructs"].items()}

    def teachable(skill: str, declared: set[str]) -> set[str]:
        # The constructs taught by the Skill itself, its hard prerequisites, or what the item declares it requires.
        sources = {skill} | closure(skill) | declared | {p for d in declared for p in closure(d)}
        return {c for c, by in introduced.items() if sources & set(by)}

    out: list[str] = ["\n".join([
        "curriculum_package/1",
        "# Generated by tools/build_curriculum_package.py from " + content_dir.relative_to(ROOT).as_posix() + " — do not edit by hand.",
        f"version={pkg['version']}",
        f"source_refs={pkg['source_refs']}",
        f"provenance={pkg['provenance']}",
    ])]
    report: dict = {"package_version": pkg["version"], "items": [], "explanations": 0, "tasks": [], "failures": []}

    # -------------------------------------------------------------- graph: topics, skills, objectives, links, edges
    topics = sorted({s6["primary_teaching_topic_id"] for s6 in (skills6c[s] for s in skill_ids)})
    for t in topics:
        out.append(section("topic", [("logical_id", t), ("version", 1), ("name", org6c[t]["display_name"])]))

    for s in pkg["skills"]:
        s6 = skills6c[s["id"]]
        out.append(section("skill", [
            ("logical_id", s["id"]), ("version", 1),
            ("canonical_name", s6["canonical_name"]),
            ("capability_statement", s.get("capability_statement") or s6["capability_statement"]),
            ("lifecycle_status", "published"),
            ("capability_kind", s6["capability_kind"]),
            ("retention_profile", s6["retention_profile"]),
            ("critical_prerequisite", bool(s6["critical_prerequisite_candidate"])),
            ("source_refs", "source.repo.fbb_v0;source.repo.internal_6c_authoring;" + pkg["content_source_ref"]),
            ("provenance", pkg["provenance"]),
        ]))

    objective_parent = {}
    objective_profile = {}
    for s in pkg["skills"]:
        _, doc = skill_files[s["id"]]
        for o in doc["objectives"]:
            o6 = objectives6c[o["id"]]
            if o6["owner_skill_id"] != s["id"]:
                raise BuildError(f"{o['id']} belongs to {o6['owner_skill_id']} in 6C")
            objective_parent[o["id"]] = s["id"]
            objective_profile[o["id"]] = o
            out.append(section("objective", [
                ("logical_id", o["id"]), ("version", 1), ("parent_skill", pin(s["id"])),
                ("required", o6["requirement_role"] == "required"),
                ("criticality", o.get("criticality", o6["criticality"])),
                ("acceptable_evidence_types", o["acceptable_evidence_types"]),
                ("direct_evidence_types", o["direct_evidence_types"]),
                ("required_direct_type", o.get("required_direct_type")),
            ]))
        sixc_objectives = sorted(k for k, v in objectives6c.items() if v["owner_skill_id"] == s["id"])
        if sorted(objective_parent_k for objective_parent_k, p in objective_parent.items() if p == s["id"]) != sixc_objectives:
            raise BuildError(f"{s['id']}: Objectives differ from 6C {sixc_objectives}")

    for link in links6c:
        if link["skill_id"] in skill_ids and link["topic_id"] in topics:
            out.append(section("topic_skill", [("topic", pin(link["topic_id"])), ("skill", pin(link["skill_id"]))]))

    for e in edges6c:
        if e["target_skill_id"] in skill_ids and e["prerequisite_skill_id"] in skill_ids:
            out.append(section("prerequisite_edge", [
                ("prerequisite", pin(e["prerequisite_skill_id"])), ("target", pin(e["target_skill_id"])),
                ("edge_version", 1), ("edge_kind", e["edge_kind"]), ("reason_kind", e["reason_kind"]),
                ("strictness_profile", e["strictness_profile_ref"]), ("lifecycle_status", "published"),
                ("provenance", pkg["provenance"]),
            ]))

    # -------------------------------------------------------------- per Skill content
    resources, validations, items_out, keys_out, rubrics_out, explanations_out, misconceptions_out, tasks_out = ([] for _ in range(8))
    item_status: dict[str, str] = {}

    for s in pkg["skills"]:
        sid = s["id"]
        path, doc = skill_files[sid]
        ns = sid.split(".", 1)[1]  # e.g. programming.state_assignment_model
        explanation_refs: dict[str, list[str]] = {}
        item_refs: dict[str, list[str]] = {}

        for o in doc["objectives"]:
            oid = o["id"]
            action = oid.rsplit(".", 1)[1]
            base = f"{ns}.{action}"
            ex = o["explanations"]
            refs = []
            # The course's own explanation (canonical) and the written alternatives (ALEX-v0).
            for form, spec in [("canonical", {"text": ex["canonical"]})] + [(k, v) for k, v in ex.items() if k != "canonical"]:
                eid = f"explanation.{base}.{form}"
                values = [("logical_id", eid), ("version", 1), ("objective", pin(oid)), ("form", form)]
                if form != "canonical":
                    values.append(("level", spec["level"]))
                values.append(("text", esc(spec["text"])))
                explanations_out.append(section("explanation", values))
                refs.append(eid)
                report["explanations"] += 1
            for m in o.get("misconceptions", []):
                mid = f"misconception.{ns}.{m['slug']}"
                misconceptions_out.append(section("misconception", [
                    ("logical_id", mid), ("version", 1), ("objective", pin(oid)),
                    ("name", esc(m["name"])), ("open_question", esc(m["open_question"])),
                ]))
                eid = f"explanation.{base}.contrast_{m['slug']}"
                explanations_out.append(section("explanation", [
                    ("logical_id", eid), ("version", 1), ("objective", pin(oid)), ("form", "misconception_contrast"),
                    ("level", m["contrast"]["level"]), ("misconception", pin(mid)), ("text", esc(m["contrast"]["text"])),
                ]))
                refs.append(eid)
                report["explanations"] += 1
            explanation_refs[oid] = refs
            # Every code claim a lesson makes is executed too: teaching a wrong output is worse than asking about one.
            for n, check in enumerate(o.get("checks", []), 1):
                status, printed, err = run_python(check["code"])
                ok_check = status == "ok" and norm(printed) == norm(str(check["prints"]))
                report.setdefault("explanation_checks", []).append(
                    {"objective": oid, "check": n, "result": "PASS" if ok_check else "FAIL", "printed": norm(printed)})
                if not ok_check:
                    report["failures"].append({"item": f"{oid} explanation check {n}",
                                               "problems": [f"lesson claims {check['prints']!r}, code prints {norm(printed)!r} ({status})"]})

            item_refs[oid] = []
            for it in o["items"]:
                iid = f"item.{base}.{it['slug']}"
                family = f"family.{base}.{it['slug']}"
                code = it.get("code")
                rubric = it.get("rubric")
                # Code may stand in the code block or in the options (a choice between definitions); both are read.
                used = constructs_used((code or "") + "\n" + "\n".join(str(t) for t in (it.get("options") or {}).values()), notation)
                declared = set(it.get("required_skills", []))
                reachable = teachable(sid, declared)
                hidden = sorted(used - reachable)
                ok, how = verify_item(it)
                reviewed = reviewed_items.get(iid, {}).get("verdict") == "pass"
                problems = []
                if not ok:
                    problems.append(how)
                if hidden:
                    problems.append(f"uses constructs no prerequisite teaches: {hidden}")
                if not reviewed:
                    problems.append("no pass verdict from the independent review")
                status = "validated" if not problems else "candidate"
                item_status[iid] = status
                mode = (it.get("verify") or {}).get("mode")
                report["items"].append({"item": iid, "status": status, "verify_mode": mode, "verification": how,
                                        "constructs": sorted(used), "hidden_prerequisites": hidden, "reviewed": reviewed})
                if problems:
                    report["failures"].append({"item": iid, "problems": problems})

                prompt = it["prompt"].strip("\n")
                if code:
                    prompt += "\n\n" + code.rstrip("\n")
                options = it.get("options") or {}
                if options:
                    prompt += "\n\n" + "\n".join(f"{k}) {options[k]}" for k in CHOICE_KEYS if k in options)
                evidence_type = it.get("evidence_type") or o["required_direct_type"] or o["direct_evidence_types"][0]
                is_rubric = rubric is not None
                validator = {
                    "reference": "15a/reference_grounded+independent_review",
                    "rubric": "15a/rubric_reviewed+independent_review",
                }.get(mode, "15a/executed_key+independent_review")

                resources.append(section("resource", [
                    ("logical_id", iid), ("version", 1), ("content_ref", f"item://{iid}@v1"),
                    ("rubric_ref", f"rubric.{base}.{it['slug']}@v1" if is_rubric else None),
                    ("evidence_type", evidence_type), ("allowed_tools_policy", "none"),
                    ("variant_family_id", family), ("content_origin", "ai_generated"),
                ]))
                validations.append(section("validation", [
                    ("resource", f"{iid}@v1"), ("validated_at_instant", VALIDATED_AT), ("status", status),
                    ("validator", validator), ("origin", "ai_generated"),
                ]))
                difficulty = it["difficulty"]
                roles = list(notation["roles"]["all"])
                if difficulty == "transfer_integration":
                    roles += notation["roles"]["transfer"]
                if s.get("critical"):
                    roles += notation["roles"]["critical"]
                independence = "h0_required" if it.get("independence", "h0") == "h0" else "guided_allowed"
                items_out.append(section("item", [
                    ("ref", f"{iid}@v1"), ("prompt", esc(prompt)),
                    ("target_objectives", pin(oid)), ("target_skills", pin(sid)),
                    ("required_skills", [pin(r) for r in sorted(declared)]),
                    ("evidence_type", evidence_type),
                    ("expected_answer_or_rubric_ref", f"rubric.{base}.{it['slug']}@v1" if is_rubric else f"answerkey.{base}.{it['slug']}@v1"),
                    ("evaluator_required_status", "provisional_allowed" if is_rubric else "verified"),
                    ("evaluator_deterministic_required", not is_rubric),
                    ("evaluator_policy_version", "OREX-v0/rubric" if is_rubric else "OREX-v0/answer_key"),
                    ("deterministic_verification", not is_rubric),
                    ("allowed_tools", []), ("prohibited_solution_sources", ["terminal", "compiler", "debugger", "external_ai"]),
                    ("independence_mode", independence), ("difficulty_class", difficulty),
                    ("lifecycle_status", status), ("content_origin", "ai_generated"),
                    ("declared_use_ceiling", "low_stakes_assessment" if is_rubric else "standard_mastery_eligible"),
                    ("scope_eligibility", ["daily_micro", "weekly_blueprint", "monthly_capability"]),
                    ("variant_family_id", family), ("forbidden_not_yet_concepts", sorted(set(introduced) - reachable)),
                    ("expected_active_minutes", it.get("minutes", notation["minutes"][difficulty])),
                    ("blueprint_roles", sorted(set(roles))),
                ]))
                if is_rubric:
                    rubrics_out.append(section("rubric", [("logical_id", f"rubric.{base}.{it['slug']}"), ("version", 1), ("item", f"{iid}@v1")]))
                    for c in rubric:
                        rubrics_out.append(section("rubric_criterion", [
                            ("rubric", f"rubric.{base}.{it['slug']}@v1"), ("id", c["id"]), ("objective", pin(oid)), ("statement", esc(c["statement"])),
                        ]))
                else:
                    keys_out.append(section("answer_key", [
                        ("logical_id", f"answerkey.{base}.{it['slug']}"), ("version", 1), ("item", f"{iid}@v1"),
                        ("objective", pin(oid)), ("case_sensitive", False),
                    ]))
                    accepted = [str(it["answer"])] + [str(a) for a in it.get("also_accept", [])]
                    for a in accepted:
                        keys_out.append(section("accepted_answer", [("key", f"answerkey.{base}.{it['slug']}@v1"), ("text", esc(a))]))
                item_refs[oid].append(iid)

        # -------------------------------------------------------------- tasks
        objectives = [o["id"] for o in doc["objectives"]]
        def items_where(pred):
            # An item shown inside the lesson belongs to the lesson only: offered again later it would measure recall
            # of the lesson, not the capability (independent review, 15A).
            return [i for o in doc["objectives"] for it in o["items"] for i in [f"item.{ns}.{o['id'].rsplit('.', 1)[1]}.{it['slug']}"]
                    if pred(it) and (it.get("in_lesson") is True) == (pred is lesson_items)]

        def lesson_items(it):
            return bool(it.get("in_lesson"))
        def exps(forms):
            return [e for o in objectives for e in explanation_refs[o] if any(e.endswith("." + f) or ("." + f + "_") in e for f in forms)]
        plans = {
            "teach": dict(purpose="teach", activity="content_explanation", serves=["new_learning"],
                          explanations=exps(["canonical", "worked_example"]),
                          items=items_where(lesson_items)),
            "practice": dict(purpose="practice", activity=doc["activity"], serves=["continue_learning"], explanations=[],
                             items=items_where(lambda it: it["difficulty"] in ("basic", "authentic_application") and "rubric" not in it)
                             + items_where(lambda it: "rubric" in it)),
            "check": dict(purpose="assess", activity=doc["activity"], serves=["verification_due"], explanations=[],
                          items=items_where(lambda it: it["difficulty"] != "basic" and "rubric" not in it), atomic=True),
            "review": dict(purpose="retain", activity=doc["activity"], serves=["retention_review_due"], explanations=[],
                           items=items_where(lambda it: "rubric" not in it), atomic=True),
            "repair": dict(purpose="remediate", activity=doc["activity"], serves=["remediation_required", "weakness_detected"],
                           explanations=exps(["prerequisite_refresh", "state_trace", "plain_reteach", "different_example", "contrast"]),
                           items=items_where(lambda it: "rubric" not in it)),
        }
        for kind, plan in plans.items():
            spec = doc["tasks"][kind]
            tid = f"task.{ns}.{kind}"
            status = "validated" if all(item_status[i] == "validated" for i in plan["items"]) else "candidate"
            # A task needs what its items need, and what its lesson text needs when the graph does not already say so
            # (3B §10: a task's own requirement is hard even where the graph is silent).
            required = sorted({r for o in doc["objectives"] for it in o["items"] for r in it.get("required_skills", [])
                               if f"item.{ns}.{o['id'].rsplit('.', 1)[1]}.{it['slug']}" in plan["items"]}
                              | set(doc.get("lesson_requires", [])))
            tasks_out.append(section("task", [
                ("logical_id", tid), ("version", 1), ("title", esc(spec["title"])), ("primary_skill", pin(sid)),
                ("target_objectives", [pin(o) for o in objectives]), ("purpose", plan["purpose"]),
                ("activity_kind", plan["activity"]), ("serves", plan["serves"]), ("cost_minutes", spec["minutes"]),
                ("required_skills", [pin(r) for r in required]),
                ("explanations", [pin(e) for e in plan["explanations"]]), ("items", [pin(i) for i in plan["items"]]),
                ("lifecycle_status", status), ("content_origin", "ai_generated"),
                ("atomic_evidence_boundary", bool(plan.get("atomic"))),
            ]))
            report["tasks"].append({"task": tid, "status": status, "items": len(plan["items"]), "explanations": len(plan["explanations"])})

    for block in (misconceptions_out, resources, validations, explanations_out, items_out, keys_out, rubrics_out, tasks_out):
        out.extend(block)

    text = "\n\n".join(out) + "\n"
    report["summary"] = {
        "skills": len(skill_ids), "objectives": len(objective_parent), "topics": len(topics),
        "items": len(report["items"]), "validated_items": sum(1 for i in report["items"] if i["status"] == "validated"),
        "explanations": report["explanations"], "misconceptions": len(misconceptions_out), "tasks": len(tasks_out),
        "failures": len(report["failures"]),
    }
    return text, report


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("content_dir")
    ap.add_argument("--out")
    ap.add_argument("--report")
    ap.add_argument("--strict", action="store_true", help="fail if any item is not validated")
    ap.add_argument("--partial", action="store_true", help="authoring aid: build only the Skills that have content")
    args = ap.parse_args()
    if args.partial and args.out:
        raise SystemExit("a partial build is never written as the shipped package")
    content_dir = (ROOT / args.content_dir).resolve()
    text, report = build(content_dir, partial=args.partial)
    if args.out:
        Path(ROOT / args.out).parent.mkdir(parents=True, exist_ok=True)
        with open(ROOT / args.out, "w", encoding="utf-8", newline="\n") as f:
            f.write(text)
    if args.report:
        Path(ROOT / args.report).parent.mkdir(parents=True, exist_ok=True)
        with open(ROOT / args.report, "w", encoding="utf-8", newline="\n") as f:
            yaml.safe_dump(report, f, allow_unicode=True, sort_keys=False, width=120)
    print(yaml.safe_dump(report["summary"], allow_unicode=True, sort_keys=False).strip())
    for failure in report["failures"]:
        print("FAIL", failure["item"], "::", "; ".join(failure["problems"]))
    if args.strict and report["failures"]:
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
