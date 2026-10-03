"""Independent 15F QA — EAAX-v0 English A0→A1/A2 starter package.

The rules are read from the accepted contracts — 6C's decomposition of the Technical English route (D01: identity,
Objectives, edges), 7B TECP-v0 (the A1/A2 anchor Skills and band monotonicity), EED-v0 and ENGLISH_FOUNDATION_RULES
(an item never asks for an untaught word or structure), TEIP-v0 (English is never a global gate; translation/gloss
integrity), KGC-v0 §27-§28, AIV-v0 (an AI-generated item is never trusted by itself), GRE-v0's two-family gate, D-113
(incremental packages), D-117 (the user's standing approval) — and compared with the content source, all six shipped
packages and the real Kotlin. The sixth package is rebuilt here from its source — every word of every English stimulus
checked against the lesson lexicons, every tool message produced again in Linux — and must be byte-identical to what
ships, as must all five earlier packages.

What this step exists to prevent must be unrepresentable as a PASS: an English word in an item that no lesson the
item may rely on teaches, a lexicon word its lesson never teaches, a Turkish option scanned as English (or an English
option left unscanned), a tool message shown that the tool does not print, a meaning key with no source, English made a
gate for anything technical, an AI-generated item validated without an independent verdict, and a package that
changes how the earlier ones read.
"""

from __future__ import annotations

import importlib.util
from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTENT = ROOT / "curriculum/content/15f_english_a1_a2"
EARLIER = [ROOT / f"curriculum/content/{d}" for d in ("15a_computing_fundamentals", "15b_python_foundations", "15c_c_foundations",
                                                      "15d_memory_foundations", "15e_linux_git_shell")]
DECOMP = ROOT / "curriculum/decomposition/6c_foundations"
ALIGNMENT = ROOT / "curriculum/english/7b_cefr_progression/alignment.yaml"
ASSETS = ANDROID / "app-wiring/src/main/assets"
ASSET = ASSETS / "curriculum_package_v6.txt"
EARLIER_ASSETS = [ASSETS / "curriculum_package.txt"] + [ASSETS / f"curriculum_package_v{n}.txt" for n in (2, 3, 4, 5)]

CONTRACT = ROOT / "arch/15f_english_a1_a2/english_a1_a2.yaml"
VERIFICATION = ROOT / "arch/15f_english_a1_a2/content_verification.yaml"
SPEC = ROOT / "docs/ENGLISH_A1_A2_CONTENT_SPEC.md"
RESEARCH = ROOT / "research/15f_english_a1_a2_research.md"
QA_OUT = ROOT / "arch/15f_english_a1_a2/qa_report.yaml"
DECISIONS = ROOT / "docs/DECISIONS.md"
BUILDER = ROOT / "tools/build_curriculum_package.py"

FORMAT_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"
SHIPPED_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/ShippedEnglishPackageTest.kt"
COURSE_TEST = ANDROID / "app-wiring/src/test/kotlin/coach/wiring/ShippedCourseTest.kt"

TURKISH = re.compile(r"[çğıöşüÇĞİÖŞÜ]")

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
for path in (CONTRACT, VERIFICATION, SPEC, RESEARCH, ASSET, *EARLIER_ASSETS, FORMAT_KT, SHIPPED_TEST, COURSE_TEST, ALIGNMENT,
             CONTENT / "package.yaml", CONTENT / "notation.yaml", CONTENT / "independent_review.yaml", BUILDER):
    check(f"E15F-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
pkg = load(CONTENT / "package.yaml") or {}
notation = load(CONTENT / "notation.yaml") or {}
review = load(CONTENT / "independent_review.yaml") or {}
verification = load(VERIFICATION) or {}
alignment = load(ALIGNMENT) or {}
skill_docs = {d["skill"]: d for d in (load(p) for p in sorted((CONTENT / "skills").glob("*.yaml"))) if d}
earlier_ids = [s["id"] for d in EARLIER for s in (load(d / "package.yaml") or {}).get("skills", [])]
objectives6c = {o["objective_id_candidate"]: o for o in (load(DECOMP / "objectives.yaml") or [])}
edges6c = load(DECOMP / "prerequisite_edges.yaml") or []
asset = read(ASSET)

check("E15F-01_model", contract.get("model") == "EAAX-v0", str(contract.get("model")))
check("E15F-01_status", contract.get("status") == "accepted_15f", str(contract.get("status")))
check("E15F-01_decision", contract.get("decision") == "D-119", str(contract.get("decision")))
ud = contract.get("user_decisions", {}) or {}
check("E15F-01_standing_approval", ud.get("approval") == "standing_D-117" and ud.get("new_user_decisions") is False
      and isinstance(ud.get("assistant_defaults_under_D-117"), list) and len(ud["assistant_defaults_under_D-117"]) > 0, str(ud))

# ---------------------------------------------------------------- the package that ships is the package that was checked
spec = importlib.util.spec_from_file_location("builder", BUILDER)
builder = importlib.util.module_from_spec(spec)
spec.loader.exec_module(builder)
# A build that cannot be made at all is a FAIL, never a crash.
try:
    rebuilt, report = builder.build(CONTENT)
except Exception as error:  # noqa: BLE001 — any failure to build is reported, not raised
    check("E15F-02_builds", False, f"{type(error).__name__}: {error}"[:300])
    print(f"15F QA: {sum(1 for r in results if r['result'] == 'PASS')}/{len(results)} FAIL")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(1)
check("E15F-02_builds", True, "")
suite_files = report.pop("suite_files", {})
check("E15F-02_shipped_is_rebuilt", rebuilt == asset, "the shipped asset differs from a fresh build of its source")
check("E15F-02_generated_marker", asset.splitlines()[1:2] == ["# Generated by tools/build_curriculum_package.py from curriculum/content/15f_english_a1_a2 — do not edit by hand."]
      if asset else False, "")
check("E15F-02_no_failures", report["failures"] == [], f"{len(report['failures'])} failures: {[f['item'] for f in report['failures']][:5]}")
check("E15F-02_report_recorded", verification.get("summary") == report["summary"] and verification.get("items") == report["items"],
      "arch/15f_english_a1_a2/content_verification.yaml is not the report of this build")
check("E15F-02_no_code_suites", suite_files == {} and not (CONTENT / "suites").exists(), "15F has no program to test")
# `--skip-earlier-rebuild` exists only for this validator's own mutation run; every sweep and every POST run rebuilds
# all five earlier packages.
if "--skip-earlier-rebuild" not in sys.argv:
    for d, a in zip(EARLIER, EARLIER_ASSETS):
        try:
            again, earlier_report = builder.build(d)
            untouched = again == read(a) and earlier_report["failures"] == []
            why = f"{len(earlier_report['failures'])} build failures" if again == read(a) else "the asset differs"
        except Exception as error:  # noqa: BLE001
            untouched, why = False, f"{type(error).__name__}: {error}"[:200]
        check(f"E15F-02_earlier_untouched_{d.name}", untouched, f"{d.name} is no longer a clean, fresh build of its source: {why}")

# ---------------------------------------------------------------- graph: 7B's A1 and A2 anchor Skills as 6C decomposed them
bands = {a["skill_id"]: a["anchor_band"] for a in alignment.get("skill_alignments", [])}
a1_a2 = sorted(s for s, b in bands.items() if b in ("A1", "A2"))
ids = [s["id"] for s in pkg.get("skills", [])]
check("E15F-03_a1_a2_anchor_skills", sorted(ids) == a1_a2 and len(ids) == 10 and sorted(contract.get("seed_skills", [])) == a1_a2, str(ids))
check("E15F-03_nothing_added", contract.get("added_skills") == [], str(contract.get("added_skills")))
hard_into: dict[str, set[str]] = {}
for e in edges6c:
    if e["edge_kind"] == "hard":
        hard_into.setdefault(e["target_skill_id"], set()).add(e["prerequisite_skill_id"])
known = set(ids) | set(earlier_ids)
open_hard = sorted((p, t) for t in ids for p in hard_into.get(t, ()) if p not in known)
check("E15F-03_closed_under_hard_prerequisites", open_hard == [], f"a Skill that could never become ready: {open_hard}")
check("E15F-03_band_monotonic", all(bands.get(p, "Z") <= bands[t] for t in ids for p in hard_into.get(t, ())), "TECP-v0 §7")
check("E15F-03_content_per_skill", sorted(skill_docs) == sorted(ids), "")
own_objectives = sorted(o["id"] for d in skill_docs.values() for o in d["objectives"])
sixc_objectives = sorted(k for k, o in objectives6c.items() if o["owner_skill_id"] in ids)
check("E15F-03_objective_identity", own_objectives == sixc_objectives and len(own_objectives) == 10, str(own_objectives))
expected_edges = sorted((e["prerequisite_skill_id"], e["target_skill_id"], e["edge_kind"]) for e in edges6c
                        if e["target_skill_id"] in ids and e["prerequisite_skill_id"] in known)
pkg_edges = re.findall(r"\[prerequisite_edge\]\nprerequisite=(\S+)@v1\ntarget=(\S+)@v1\nedge_version=1\nedge_kind=(\w+)\n"
                       r"reason_kind=\w+\nstrictness_profile=\w+\nlifecycle_status=(\w+)", asset)
check("E15F-03_edges_ratified", sorted((p, t, k) for p, t, k, _ in pkg_edges) == expected_edges
      and len(pkg_edges) == contract.get("scope", {}).get("edges") and all(l == "published" for *_, l in pkg_edges), f"{len(pkg_edges)} edges")
check("E15F-03_english_never_a_technical_gate", all(p.startswith("skill.english.") and t.startswith("skill.english.") for p, t, *_ in pkg_edges)
      and not any(e["prerequisite_skill_id"].startswith("skill.english.") and e["edge_kind"] == "hard"
                  and not e["target_skill_id"].startswith("skill.english.") and e["target_skill_id"] in known for e in edges6c), "TEIP-v0")
entry = sorted(s for s in ids if not (hard_into.get(s, set()) & known))
check("E15F-03_entry_point", entry == ["skill.english.recognize_core_technical_labels"] == contract.get("entry_skills"), str(entry))

# ---------------------------------------------------------------- Objective reconciliation
for d in skill_docs.values():
    for o in d["objectives"]:
        six = objectives6c.get(o["id"], {})
        check(f"E15F-04_recorded_{tag(o['id'])}", all(o.get(k) for k in ("statement", "observable_behavior", "reconciliation"))
              and o.get("required_direct_type") == six.get("evidence_profile", {}).get("required_direct_type")
              and set(o["acceptable_evidence_types"]) == set(six.get("evidence_profile", {}).get("acceptable_evidence_types", [])), o["id"])
statements = [o["statement"] for d in skill_docs.values() for o in d["objectives"]]
check("E15F-04_no_template_statement", len(set(statements)) == len(statements) and not any("Yeni bir bağlamda" in s for s in statements), "")

# ---------------------------------------------------------------- items
items = report["items"]
item_review = (review or {}).get("items", {}) or {}
check("E15F-05_review_method", bool((review or {}).get("method")) and review.get("reviewer") == "independent_15f_content_review", "")
check("E15F-05_every_item_reviewed", sorted(item_review) == sorted(i["item"] for i in items), f"{len(item_review)} / {len(items)}")
check("E15F-05_every_item_passed_review", all(v.get("verdict") == "pass" for v in item_review.values()),
      str([k for k, v in item_review.items() if v.get("verdict") != "pass"][:5]))
check("E15F-05_every_item_validated", all(i["status"] == "validated" for i in items), "")
check("E15F-05_no_hidden_prerequisite", all(not i["hidden_prerequisites"] for i in items), "no untaught word, no untaught construct")
modes = {i["verify_mode"] for i in items}
check("E15F-05_modes", modes == {"reference", "shell", "rubric"}, str(modes))
counts = contract.get("item_counts", {})
by_mode = {m: sum(1 for i in items if i["verify_mode"] == m) for m in ("reference", "shell", "rubric")}
check("E15F-05_counts", (len(items), by_mode["reference"], by_mode["shell"], by_mode["rubric"])
      == (counts.get("total"), counts.get("reference"), counts.get("shell"), counts.get("rubric")), f"{len(items)} {by_mode}")
all_items = [(d["skill"], it) for d in skill_docs.values() for o in d["objectives"] for it in o["items"]]
refs = [it for _, it in all_items if (it.get("verify") or {}).get("mode") == "reference"]
check("E15F-05_meaning_keys_sourced", refs != [] and all(it["verify"].get("reference") and it["verify"].get("claim") for it in refs), "")
shells = [(s, it) for s, it in all_items if (it.get("verify") or {}).get("mode") == "shell"]
# A tool's message is shown exactly as the tool prints it: the check produces it and looks for the shown line verbatim.
check("E15F-05_tool_messages_verbatim", shells != [] and all(s == "skill.english.read_simple_terminal_error_fragments"
      and it["code"].strip() in it["verify"]["script"] and "grep -q" in it["verify"]["script"] for s, it in shells),
      str([it["slug"] for s, it in shells if it["code"].strip() not in it["verify"].get("script", "")]))
check("E15F-05_shell_prelude", pkg.get("shell_prelude") == "export LC_ALL=C.UTF-8\n", "English tool messages whatever the locale")
mixed = [it["slug"] for _, it in all_items if it.get("options")
         and any(TURKISH.search(str(v)) for v in it["options"].values()) and it.get("scan_options", True)]
check("E15F-05_turkish_options_not_scanned", mixed == [], str(mixed))
# Options are English when every word in them is an English word some lesson teaches (Turkish words are in no lexicon).
lexicon_words = {str(w).lower() for ws in (notation.get("lexicon") or {}).values() for w in ws}
english_unscanned = [it["slug"] for _, it in all_items if it.get("options") and it.get("scan_options", True) is False
                     and not any(TURKISH.search(str(v)) for v in it["options"].values())
                     and all(builder.english_words(str(v)) and builder.english_words(str(v)) <= lexicon_words for v in it["options"].values())]
check("E15F-05_english_options_scanned", english_unscanned == [], str(english_unscanned))
item_rows = {m.group(1): m.group(0) for m in re.finditer(r"\[item\]\nref=(\S+)@v1\n(?:[^\n]+\n)+", asset + "\n")}
for d in skill_docs.values():
    for o in d["objectives"]:
        oid = o["id"]
        measuring = [r for r in item_rows.values() if f"target_objectives={oid}@v1\n" in r and "deterministic_verification=true" in r
                     and f"evidence_type={o['required_direct_type']}\n" in r]
        families = {re.search(r"variant_family_id=(\S+)", r).group(1) for r in measuring}
        transfer = [r for r in measuring if "difficulty_class=transfer_integration" in r]
        check(f"E15F-05_measurable_{tag(oid)}", len(families) >= 6 and transfer != [], f"{len(families)} families")
check("E15F-05_ai_origin_declared", "content_origin=human_authored" not in asset and asset.count("content_origin=ai_generated") > 0, "")
check("E15F-05_no_terminal", all("terminal" not in re.search(r"allowed_tools=([^\n]*)", r).group(1) for r in item_rows.values()
                                 if re.search(r"allowed_tools=", r)), "reading and writing English is the construct")

# ---------------------------------------------------------------- the lexicon: every word an item shows is a word a lesson teaches
lexicon = notation.get("lexicon") or {}
check("E15F-06_lexicon_per_skill", sorted(lexicon) == sorted(ids), str(sorted(set(ids) - set(lexicon))))
check("E15F-06_lexicon_strings", all(isinstance(w, str) for ws in lexicon.values() for w in ws),
      "a plain yes / no / on in YAML is a boolean, not a word")
untaught = []
for sid, words in lexicon.items():
    canonical = skill_docs.get(sid, {}).get("objectives", [{}])[0].get("explanations", {}).get("canonical", "").lower()
    untaught += [f"{sid.rsplit('.', 1)[1]}:{w}" for w in words
                 if isinstance(w, str) and not re.search(rf"(?<![a-z']){re.escape(w.lower())}(?![a-z'])", canonical)]
check("E15F-06_lexicon_is_taught", untaught == [], str(untaught[:8]))
builder_src = read(BUILDER)
ew = builder_src[builder_src.find("def english_words("):builder_src.find("def constructs_used(")]
check("E15F-06_literals_are_not_words", 're.sub(r"`[^`\\n]*`", " ", text)' in ew and "quoted = raw[:1] in" in ew
      and 're.search(r"[a-z][A-Z]", token)' in ew, "backticks, quoted names, non-letters and mixed-case identifiers are skipped")
check("E15F-06_builder_refuses_unknown_words", 'hidden += sorted(f"unknown_word:{w}" for w in words if w not in lexicon)' in builder_src
      and 'hidden += sorted(f"word:{w}" for w in words if w in lexicon and not (lexicon[w] & allowed))' in builder_src, "")
check("E15F-06_earlier_have_no_lexicon", all("lexicon" not in (load(d / "notation.yaml") or {}) for d in EARLIER), "")

# ---------------------------------------------------------------- shown exactly (D-114, unchanged)
format_kt = strip_comments(read(FORMAT_KT))
unescape = body(format_kt, "fun unescape(")
check("E15F-07_unescape_rule_unchanged", "next == 'n' || next == '\\\\'" in unescape and "append(if (next == 'n') '\\n' else '\\\\')" in unescape, "")
check("E15F-07_kotlin_unchanged", contract.get("scope", {}).get("kotlin_main_changed") is False, "")

# ---------------------------------------------------------------- lessons
expl_review = (review or {}).get("explanations", {}) or {}
expl_ids = re.findall(r"\[explanation\]\nlogical_id=(\S+)", asset)
check("E15F-08_every_explanation_reviewed", sorted(expl_review) == sorted(expl_ids), f"{len(expl_review)} / {len(expl_ids)}")
check("E15F-08_every_explanation_passed", all(v.get("verdict") == "pass" for v in expl_review.values()),
      str([k for k, v in expl_review.items() if v.get("verdict") != "pass"][:5]))
checks_run = report.get("explanation_checks", [])
check("E15F-08_lesson_claims_executed", len(checks_run) >= contract.get("lesson_checks_min", 1) and all(c["result"] == "PASS" for c in checks_run),
      f"{len(checks_run)} checks")
for d in skill_docs.values():
    for o in d["objectives"]:
        check(f"E15F-08_lesson_{tag(o['id'])}", bool(o["explanations"].get("canonical")) and "worked_example" in o["explanations"]
              and "prerequisite_refresh" in o["explanations"] and len(o.get("misconceptions", [])) >= 2
              and all(m["open_question"].rstrip().endswith("?") for m in o["misconceptions"]), "")
COMBINING_DOT = chr(0x0307)
check("E15F-08_no_combining_dot", COMBINING_DOT not in asset, "")
check("E15F-08_question_owned", (notation.get("constructs") or {}).get("question", {}).get("introduced_by")
      == ["skill.english.negation_question_comprehension"], "")

# ---------------------------------------------------------------- tasks
tasks = re.findall(r"\[task\]\nlogical_id=task\.(\S+)\.(teach|practice|check|review|repair)\n(?:[^\n]+\n)+", asset + "\n")
check("E15F-09_five_per_skill", len(tasks) == 50 and all(sum(1 for t in tasks if t[0] == s.split(".", 1)[1]) == 5 for s in ids), f"{len(tasks)}")
check("E15F-09_tasks_validated", all(t["status"] == "validated" for t in report["tasks"]), "")
minutes = [int(m) for m in re.findall(r"\[task\]\n(?:[^\n]+\n)*?cost_minutes=(\d+)", asset)]
check("E15F-09_minutes_are_estimates", contract.get("task_minutes", {}).get("calibrated") is False and all(0 < m <= 20 for m in minutes), "")

# ---------------------------------------------------------------- tests
for path, names in ((SHIPPED_TEST, ["everything shipped was validated, so nothing is a candidate",
                                    "the English a learner reads is shown exactly, with a tool's own message word for word",
                                    "the package is version 6 of the ten A1 and A2 Technical English Skills, published, and closed on its own"]),
                    (COURSE_TEST, ["all six shipped packages are published into the real store, oldest first, and English waits only on English",
                                   "with the English package, a learner who has done nothing may also start the first English lesson, and nothing technical waits on English"])):
    text = read(path)
    for name in names:
        check(f"E15F-11_test_{name[:40]}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results") if isinstance(contract.get("mutation_results"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("E15F-12_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("E15F-12_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("final_run_is_a_single_clean_run") is True, "")
check("E15F-12_control", mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("E15F-12_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("E15F-12_runs_pass", len(runs) >= 5 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") if isinstance(contract.get("device_verification"), dict) else {}
check("E15F-12_t6_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("sqlite_publish_on_jvm_run") is True
      and dv.get("sqlite_publish_on_device_run") is False, "")

# ---------------------------------------------------------------- spec, research, decision
spec_text = read(SPEC)
check("E15F-13_spec_status", "**Status:** ACCEPTED — independent 15F QA PASS" in spec_text and "`D-119`" in spec_text, "")
check("E15F-13_spec_next", "**15G — Assessment content**" in spec_text, "")
check("E15F-13_spec_t6", "**Not run: T6.**" in spec_text, "")
research = read(RESEARCH)
check("E15F-13_research", all(url in research for url in (
    "https://en.wikipedia.org/wiki/Imperative_mood", "https://en.wikipedia.org/wiki/Do-support",
    "https://en.wikipedia.org/wiki/Simple_present", "https://en.wikipedia.org/wiki/English_prepositions",
    "https://man7.org/linux/man-pages/man7/man-pages.7.html")), "every source the lessons rest on is named by its URL")
check("E15F-13_decision", re.search(r"^#+ .*D-119", read(DECISIONS), re.M) is not None, "D-119 heading in DECISIONS")

passed = sum(1 for r in results if r["result"] == "PASS")
out = {"model": "EAAX-v0", "stage_step": "15F", "decision": "D-119", "result": "PASS" if not failures else "FAIL",
       "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
if "--no-write" not in sys.argv:
    QA_OUT.parent.mkdir(parents=True, exist_ok=True)
    QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"15F QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
for failure in failures:
    print("  FAIL", failure)
sys.exit(0 if not failures else 1)
