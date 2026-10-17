#!/usr/bin/env python3
"""Stage 15 content QA — 15H, CQAX-v0 / D-121.

Audits what Stage 15 published (the six content directories, 15G's supplement and the seven shipped packages) for what
the per-step builds could not see, and checks that every finding is resolved by 15H's supplement
(`curriculum/content/15h_content_qa`) or recorded with its reason. It writes `arch/15h_content_qa/content_qa_audit.yaml`
and exits non-zero when any finding is unresolved.

  fixed_output   a code item whose suite the program that only prints the expected output passes — resolved by a
                 revision (a new version whose suite reads how the output was produced) or a recorded exemption where
                 producing that output is the construct (fixed_output.yaml);
  notation       a Python item using a construct no lesson shows (in, is, pass, break / continue, as — notation_overlay.yaml)
                 — resolved by a revision, or, where only the never-shipped reference used it, by an alternative reference
                 (alt_references.yaml) that uses taught constructs only and passes the item's published suite;
  lesson_checks  a lesson's code claim using a construct its teach task cannot reach — resolved by declaring the lesson's
                 need (graph.yaml teach_requires) or classified in lesson_findings.yaml (a verification scaffold the lesson
                 does not show, a construct the notation attributes elsewhere, the course's "# hazırlık" device, or a
                 recorded cycle);
  lesson_text    the same for the code lines a lesson's texts show (canonical, alternatives, contrasts);
  graph          a lesson need the published graph does not imply — resolved by an edge in graph.yaml;
  labels         a review note calling a difficulty label generous, not earned, without context, modest or borderline — decided in
                 label_decisions.yaml (a label revision, or kept in the reviewer's own words);
  pools          items outside the lesson per Objective after 15H — technical Objectives at 15 or more, English recorded;
  criticality    no published Objective is critical (GRE-v0's critical gate fields are not stored; the builder refuses one).

Usage:  python tools/content_qa.py [--skip-runs]
  --skip-runs  does not run suites (constant programs, alternative references); a run-less audit is never recorded.
"""
from __future__ import annotations

import copy
import json
import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "tools"))
import build_curriculum_package as b  # noqa: E402

DIRS = ["15a_computing_fundamentals", "15b_python_foundations", "15c_c_foundations", "15d_memory_foundations",
        "15e_linux_git_shell", "15f_english_a1_a2"]
CONTENT = ROOT / "curriculum/content"
SUPPLEMENT_15G = CONTENT / "15g_assessment"
QA = CONTENT / "15h_content_qa"
ASSETS = ROOT / "android/app-wiring/src/main/assets"
OUT = ROOT / "arch/15h_content_qa/content_qa_audit.yaml"


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8")) if path.exists() else {}


# ------------------------------------------------------------------------------------------------ the published content

def skill_docs() -> dict[str, dict]:
    """Every Skill file of the six directories, with 15G's items attached to their Objectives as `_15g`."""
    docs = {}
    for d in DIRS:
        for path in sorted((CONTENT / d / "skills").glob("*.yaml")):
            doc = load(path)
            doc["_dir"], doc["_file"] = d, path.name
            docs[doc["skill"]] = doc
        for path in sorted((SUPPLEMENT_15G / "items" / d).glob("*.yaml")):
            for o in (load(path) or {}).get("objectives", []):
                target = next(oo for s in docs.values() for oo in s["objectives"] if oo["id"] == o["id"])
                target.setdefault("_15g", []).extend(o.get("items", []))
    return docs


def items_of(doc: dict):
    """(objective, item, source, logical id) for every published item of a Skill."""
    for o in doc["objectives"]:
        base = o["id"].split(".", 1)[1]
        for source, items in (("own", o["items"]), ("15g", o.get("_15g", []))):
            for it in items:
                yield o, it, source, f"item.{base}.{it['slug']}"


def qa_items() -> dict[str, dict]:
    """15H's own files per Objective: new items and revisions."""
    out: dict[str, dict] = {}
    for path in sorted((QA / "items").rglob("*.yaml")):
        for o in (load(path) or {}).get("objectives", []):
            entry = out.setdefault(o["id"], {"items": [], "revisions": []})
            entry["items"] += o.get("items", [])
            entry["revisions"] += o.get("revisions", [])
    return out


def revisions_by_item() -> dict[str, list[str]]:
    out = {}
    for oid, entry in qa_items().items():
        for r in entry["revisions"]:
            out[f"item.{oid.split('.', 1)[1]}.{r['revises']}"] = str(r["reason"]).split("+")
    return out


def shipped_tasks() -> dict[str, dict]:
    tasks: dict[str, dict] = {}
    for f in sorted(ASSETS.glob("curriculum_package*.txt")):
        for s in f.read_text(encoding="utf-8").split("\n["):
            if s.startswith("task]"):
                d = dict(line.split("=", 1) for line in s.split("\n")[1:] if "=" in line)
                if d["logical_id"] not in tasks or int(tasks[d["logical_id"]]["version"]) < int(d["version"]):
                    tasks[d["logical_id"]] = d
    return tasks


def teach_requirements(docs: dict[str, dict], with_15h: bool) -> dict[str, set[str]]:
    tasks = shipped_tasks()
    graph = load(QA / "graph.yaml") or {}
    out = {}
    for sid in docs:
        task = tasks[f"task.{sid.split('.', 1)[1]}.teach"]
        out[sid] = {r.split("@")[0] for r in task["required_skills"].split(",") if r}
        if with_15h:
            out[sid] |= set((graph.get("teach_requires") or {}).get(sid, {}).get("requires", []))
    return out


def code_config(d: str) -> dict:
    return {**b.PYTHON_CODE, **(load(CONTENT / d / "package.yaml").get("code") or {})}


def suite_file(source: str, d: str, iid: str) -> Path:
    return (SUPPLEMENT_15G if source == "15g" else CONTENT / d) / "suites" / f"{iid}.json"


# ------------------------------------------------------------------------------------------------ audits

def audit_fixed_output(docs, run: bool) -> list[dict]:
    exempt = load(QA / "fixed_output.yaml") or {}
    revised = revisions_by_item()
    rows = []
    for sid, doc in docs.items():
        for o, it, source, iid in items_of(doc):
            if "suite" not in it:
                continue
            if len({t.get("expected_stdout") for t in it["suite"]}) > 1:
                continue  # the constant prints the first test's output, which another test does not expect
            row = {"item": iid, "source": source}
            if run:
                suite = json.loads(suite_file(source, doc["_dir"], iid).read_text(encoding="utf-8"))
                cfg = code_config(doc["_dir"])
                got = b.run_suite(suite, b.constant_program(cfg["file"], it["suite"][0].get("expected_stdout", "")), cfg)
                row["constant_passes"] = got.get("__build__") == "ok" and all(got.get(t["id"]) == "passed" for t in it["suite"])
                if not row["constant_passes"]:
                    continue
            if not run:
                row["resolution"] = "not_run"  # a run-less audit cannot tell; it is never recorded
            elif "fixed_output" in revised.get(iid, []):
                row["resolution"] = "revised"
            elif o["id"] in (exempt.get("objectives") or {}):
                row["resolution"] = "exempt_objective"
            elif iid in (exempt.get("items") or {}):
                row["resolution"] = "exempt_item"
            else:
                row["resolution"] = None
            rows.append(row)
    return rows


def overlaid_notation(d: str) -> dict:
    notation = copy.deepcopy(load(CONTENT / d / "notation.yaml"))
    for construct, spec in ((load(QA / "notation_overlay.yaml") or {}).get(d) or {}).items():
        if construct in notation["constructs"]:
            notation["constructs"][construct]["introduced_by"] = sorted(
                set(notation["constructs"][construct]["introduced_by"]) | set(spec["introduced_by"]))
        else:
            notation["constructs"][construct] = spec
    return notation


def reach(sid: str, declared: set[str], notation: dict, hard: dict[str, set[str]]) -> set[str]:
    sources = {sid} | b.closure_of(sid, hard) | declared | {p for x in declared for p in b.closure_of(x, hard)}
    return {c for c, spec in notation["constructs"].items() if sources & set(spec["introduced_by"])}


def audit_notation(docs, run: bool) -> list[dict]:
    _, _, hard = b.published_graph()
    alt = load(QA / "alt_references.yaml") or {}
    revised = revisions_by_item()
    rows = []
    for sid, doc in docs.items():
        overlay = (load(QA / "notation_overlay.yaml") or {}).get(doc["_dir"])
        if not overlay:
            continue
        notation = overlaid_notation(doc["_dir"])
        added = set(overlay)
        for o, it, source, iid in items_of(doc):
            scanned = ((it.get("code") or "") + "\n" + "\n".join(str(t) for t in (it.get("options") or {}).values()
                                                                if it.get("scan_options", True)) + "\n" + (it.get("reference") or ""))
            declared = set(it.get("required_skills", []))
            hidden = sorted((b.constructs_used(scanned, notation) & added) - reach(sid, declared, notation, hard))
            if not hidden:
                continue
            row = {"item": iid, "source": source, "constructs": hidden}
            if "notation" in revised.get(iid, []):
                row["resolution"] = "revised"
            elif iid in alt:
                ref = alt[iid]["reference"]
                still = sorted(b.constructs_used(ref, notation) - reach(sid, declared, notation, hard))
                row["alternative_hidden"] = still
                if run:
                    suite = json.loads(suite_file(source, doc["_dir"], iid).read_text(encoding="utf-8"))
                    got = b.run_suite(suite, ref, code_config(doc["_dir"]))
                    row["alternative_passes"] = got.get("__build__") == "ok" and all(got.get(t["id"]) == "passed" for t in it["suite"])
                ok = not still and (row.get("alternative_passes") is True or not run)
                row["resolution"] = "alternative_reference" if ok else None
            else:
                row["resolution"] = None
            rows.append(row)
    return rows


def hard_after_15h() -> dict[str, set[str]]:
    """The published hard graph with 15H's hard edges added: the graph a lesson is planned against once 15H ships."""
    _, _, hard = b.published_graph()
    hard = {k: set(v) for k, v in hard.items()}
    for e in (load(QA / "graph.yaml") or {}).get("edges", []):
        if e["kind"] == "hard":
            hard.setdefault(e["target"], set()).add(e["prerequisite"])
    return hard


def shown_lines(o: dict) -> set[str]:
    texts = [e if isinstance(e, str) else e["text"] for e in o["explanations"].values()]
    texts += [m["contrast"]["text"] for m in o.get("misconceptions", [])]
    return {re.sub(r"\s+", " ", line.strip()) for t in texts for line in t.splitlines() if line.strip()}


def audit_lesson_checks(docs) -> list[dict]:
    """A lesson check is code the build runs to verify what the lesson claims. A construct it uses that the teach task
    cannot reach is a finding; whether the lesson *shows* it (a line of the check appears in the lesson) decides how it
    may be resolved: a shown construct needs the lesson's need declared (teach_requires) or a recorded reason."""
    hard = hard_after_15h()  # a lesson is read against the graph as it ships after 15H
    teach = teach_requirements(docs, with_15h=True)
    findings = load(QA / "lesson_findings.yaml") or {}
    rows = []
    for sid, doc in docs.items():
        notation = overlaid_notation(doc["_dir"])
        if not notation.get("constructs"):
            continue
        lang = (load(CONTENT / doc["_dir"] / "package.yaml") or {}).get("lesson_checks", "python")
        reachable = reach(sid, teach[sid], notation, hard)
        for o in doc["objectives"]:
            shown = shown_lines(o)
            # Every construct any code line of the lesson's texts uses: a construct shown inline elsewhere is shown too
            # (independent review, 15H), so a scaffold cannot hide it.
            texts = [e if isinstance(e, str) else e["text"] for e in o["explanations"].values()]
            texts += [m["contrast"]["text"] for m in o.get("misconceptions", [])]
            in_text = set().union(*[b.constructs_used(line, notation) for text in texts
                                    for line in code_lines(text, LESSON_LANG.get(doc["_dir"], "python"))]) if texts else set()
            for n, check in enumerate(o.get("checks", []), 1):
                if check.get("lang", lang) == "shell" and doc["_dir"] != "15e_linux_git_shell":
                    continue  # a shell check in a C package is read with the C notation; 15C's terminal Skill is 15E's notation
                for construct in sorted(b.constructs_used(check["code"], notation) - reachable):
                    shown_use = construct in in_text or any(construct in b.constructs_used(line, notation)
                                    for line in check["code"].splitlines() if re.sub(r"\s+", " ", line.strip()) in shown)
                    key = f"{sid}|{o['id'].rsplit('.', 1)[1]}|check{n}|{construct}"
                    decision = findings.get(key) or {}
                    kind = decision.get("kind")
                    ok = kind in {"notation_gap", "accepted_device", "recorded"} or (kind == "verification_scaffold" and not shown_use)
                    rows.append({"finding": key, "shown_in_lesson": shown_use, "resolution": kind if ok else None,
                                 **({"note": decision["note"]} if decision.get("note") else {})})
    return rows


LESSON_LANG = {"15a_computing_fundamentals": "python", "15b_python_foundations": "python", "15c_c_foundations": "c",
               "15d_memory_foundations": "c", "15e_linux_git_shell": "shell"}


def code_lines(text: str, lang: str) -> list[str]:
    """The lines of a lesson text that are code: a Python line that compiles (and is more than a name), a C line that ends
    a statement or opens or closes a block, a shell line after the "$ " prompt. Prose is never read as code; an
    annotation after the code ("   (açıklama)", "→ sonuç") is cut off first."""
    out = []
    for line in text.splitlines():
        # A numbered listing ("1  def kare_kok(n):", "3. x = 1") shows code after its line number.
        s = re.sub(r"^\d+(?:\.|\)|\s{2,})\s*", "", line.strip())
        s = re.split(r"\s{2,}\(|\s+→|\s{3,}", s)[0].strip()
        if not s:
            continue
        if lang == "python":
            # A statement that does not compile alone (return, a bare keyword) still shows its construct.
            if re.match(r"(return|import|from|pass|break|continue|global|nonlocal|raise|yield)\b", s) and not re.search(r"[çğıöşüÇĞİÖŞÜ]", s):
                out.append(s)
                continue
            probe = s + ("\n    pass" if s.endswith(":") else "")
            for candidate in (probe, re.sub(r"^(elif|else|except|finally)\b", "if True", probe)):
                try:
                    compile(candidate, "<lesson>", "exec")
                except SyntaxError:
                    continue
                if re.search(r"[=()\[\]:+\-*/%<>]", s):
                    out.append(s)
                break
        elif lang == "c":
            if re.search(r";\s*$|\{\s*$|^\}|^#include|\bint main\b", s) and not re.search(r"[çğıöşüÇĞİÖŞÜ]", re.sub(r'"[^"]*"', "", s)):
                out.append(s)
        elif s.startswith("$ "):
            out.append(s[2:])
    return out


def audit_lesson_text(docs) -> list[dict]:
    """The code a lesson shows — its canonical, alternatives and contrasts — against what its teach task reaches. A shown
    construct the teach task cannot reach needs the lesson's need declared (teach_requires) or a recorded reason. English
    lessons show no code; their words are glossed where shown (15F) and are the lexicon's, not the notation's."""
    hard = hard_after_15h()  # a lesson is read against the graph as it ships after 15H
    teach = teach_requirements(docs, with_15h=True)
    findings = load(QA / "lesson_findings.yaml") or {}
    rows = []
    for sid, doc in docs.items():
        lang = LESSON_LANG.get(doc["_dir"])
        if lang is None or sid == "skill.linux.terminal_filesystem_navigation":
            continue  # the terminal Skill is 15C's, its commands are 15E's notation; its lesson checks run in Linux
        notation = overlaid_notation(doc["_dir"])
        reachable = reach(sid, teach[sid], notation, hard)
        for o in doc["objectives"]:
            texts = [(k, e if isinstance(e, str) else e["text"]) for k, e in o["explanations"].items()]
            texts += [("contrast_" + m["slug"], m["contrast"]["text"]) for m in o.get("misconceptions", [])]
            for form, text in texts:
                for line in code_lines(text, lang):
                    for construct in sorted(b.constructs_used(line, notation) - reachable):
                        key = f"{sid}|{o['id'].rsplit('.', 1)[1]}|{form}|{construct}"
                        decision = findings.get(key) or {}
                        kind = decision.get("kind")
                        rows.append({"finding": key, "line": line,
                                     "resolution": kind if kind in {"notation_gap", "accepted_device", "recorded"} else None,
                                     **({"note": decision["note"]} if decision.get("note") else {})})
    return rows


def audit_graph(docs) -> list[dict]:
    skills, published, hard = b.published_graph()
    teach = teach_requirements(docs, with_15h=True)
    graph = load(QA / "graph.yaml") or {}
    added = {(e["prerequisite"], e["target"]): e for e in graph.get("edges", [])}
    rows = []
    for t in skills:
        for p in sorted(teach[t] - b.closure_of(t, hard)):
            if (p, t) in published:
                continue
            e = added.get((p, t))
            new_hard = {k: set(v) for k, v in hard.items()}
            for x in graph.get("edges", []):
                if x["kind"] == "hard":
                    new_hard.setdefault(x["target"], set()).add(x["prerequisite"])
            implied = p in b.closure_of(t, new_hard)
            rows.append({"need": f"{p}->{t}", "resolution": f"{e['kind']}_edge" if e else ("implied_by_15h_hard_edges" if implied else None)})
    return rows


def audit_labels() -> list[dict]:
    decisions = load(QA / "label_decisions.yaml") or {}
    revised = revisions_by_item()
    rows = []
    for d in DIRS + ["15g_assessment"]:
        for iid, v in ((load(CONTENT / d / "independent_review.yaml") or {}).get("items") or {}).items():
            if not re.search(r"generous|not earned|no context|modest|borderline", str(v.get("note", "")), re.I):
                continue
            if iid in (decisions.get("revise") or {}):
                resolution = "revised" if "label" in revised.get(iid, []) else None
            elif iid in (decisions.get("keep") or {}):
                resolution = "kept" if "label" not in revised.get(iid, []) else None
            else:
                resolution = None
            rows.append({"item": iid, "review": d, "resolution": resolution})
    for iid in (decisions.get("revise") or {}):
        if not any(r["item"] == iid for r in rows):
            rows.append({"item": iid, "review": "decision without a flagged note",
                         "resolution": "revised" if "label" in revised.get(iid, []) else None})
    return rows


def audit_pools(docs) -> list[dict]:
    new = qa_items()
    rows = []
    for sid, doc in docs.items():
        for o in doc["objectives"]:
            count = sum(1 for it in o["items"] + o.get("_15g", []) + new.get(o["id"], {}).get("items", [])
                        if not it.get("in_lesson") and not it.get("transfer_profile"))
            english = sid.startswith("skill.english.")
            rows.append({"objective": o["id"], "outside_lesson": count, "english": english,
                         "resolution": "at_least_15" if count >= 15 else ("recorded_small_pool" if english else None)})
    return rows


def audit_criticality() -> list[dict]:
    rows = []
    for f in sorted(ASSETS.glob("curriculum_package*.txt")):
        for s in f.read_text(encoding="utf-8").split("\n["):
            if s.startswith("objective]"):
                d = dict(line.split("=", 1) for line in s.split("\n")[1:] if "=" in line)
                rows.append({"objective": d["logical_id"], "criticality": d["criticality"],
                             "resolution": "standard" if d["criticality"] == "standard" else None})
    return rows


def main() -> int:
    run = "--skip-runs" not in sys.argv
    docs = skill_docs()
    audits = {
        "fixed_output": audit_fixed_output(docs, run),
        "notation": audit_notation(docs, run),
        "lesson_checks": audit_lesson_checks(docs),
        "lesson_text": audit_lesson_text(docs),
        "graph": audit_graph(docs),
        "labels": audit_labels(),
        "pools": audit_pools(docs),
        "criticality": audit_criticality(),
    }
    unresolved = {k: [r for r in v if r.get("resolution") is None] for k, v in audits.items()}
    summary = {k: {"findings": len(v), "unresolved": len(unresolved[k])} for k, v in audits.items()}
    report = {"audit": "15h_content_qa", "model": "CQAX-v0", "decision": "D-121", "runs": run, "summary": summary,
              "result": "PASS" if not any(unresolved.values()) else "FAIL", **audits}
    if run:
        OUT.parent.mkdir(parents=True, exist_ok=True)
        OUT.write_text(yaml.safe_dump(report, allow_unicode=True, sort_keys=False, width=120), encoding="utf-8", newline="\n")
    print(yaml.safe_dump(summary, sort_keys=False).strip())
    for k, rows in unresolved.items():
        for r in rows:
            print("UNRESOLVED", k, "::", {x: y for x, y in r.items() if x != "resolution"})
    return 0 if report["result"] == "PASS" else 1


if __name__ == "__main__":
    sys.exit(main())
