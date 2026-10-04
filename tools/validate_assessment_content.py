"""Independent 15G QA — ACNX-v0 assessment content supplement.

The rules are read from the accepted contracts — `QAB-v0` §§8, 14, 17, 22 (pools, exposure, transfer profiles, roles),
`MCA-v0` §§5–9 (the month's cross-topic transfer, an unseen structure), `AIV-v0` §16 (a transfer claim is checked),
`WAAX-v0` (a misconception label is as strong as its evidence), `LFPS-v0` (a published version is never overwritten),
D-113 (incremental packages) and the user's 15G decisions — and compared with the content source, all seven shipped
packages and the real Kotlin. The seventh package is rebuilt here from its source — every added key run again by the
content directory it belongs to — and must be byte-identical to what ships, as must all six earlier packages.

What this step exists to prevent must be unrepresentable as a PASS: a supplement that overwrites or carries again
anything published, an added item no independent reviewer passed, a choice item stored as authored code, hands-on or
written evidence, a transfer claim with no cross-topic context or a transfer item a task would spend, a wrong option
mapped to another Objective's label or to the right answer, a task republished without growing, an Objective below the
agreed pool that is not recorded as such, and a planner or composer that never asks the transfer owner.
"""

from __future__ import annotations

import copy
import hashlib
import importlib.util
import pickle
from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTENT = ROOT / "curriculum/content/15g_assessment"
SOURCES = ["15a_computing_fundamentals", "15b_python_foundations", "15c_c_foundations", "15d_memory_foundations",
           "15e_linux_git_shell", "15f_english_a1_a2"]
ASSETS = ANDROID / "app-wiring/src/main/assets"
ASSET = ASSETS / "curriculum_package_v7.txt"
EARLIER_ASSETS = [ASSETS / "curriculum_package.txt"] + [ASSETS / f"curriculum_package_v{n}.txt" for n in (2, 3, 4, 5, 6)]

CONTRACT = ROOT / "arch/15g_assessment_content/assessment_content.yaml"
VERIFICATION = ROOT / "arch/15g_assessment_content/content_verification.yaml"
SPEC = ROOT / "docs/ASSESSMENT_CONTENT_SPEC.md"
RESEARCH = ROOT / "research/15g_assessment_content_research.md"
QA_OUT = ROOT / "arch/15g_assessment_content/qa_report.yaml"
DECISIONS = ROOT / "docs/DECISIONS.md"
BUILDER = ROOT / "tools/build_curriculum_package.py"

K = ANDROID
TRANSFER_ENGINE = K / "core-engines/src/main/kotlin/coach/engines/TransferEngine.kt"
MONTHLY = K / "core-engines/src/main/kotlin/coach/engines/MonthlyBlueprintEngine.kt"
WEEKLY = K / "core-engines/src/main/kotlin/coach/engines/WeeklyBlueprintEngine.kt"
PLANNER = K / "core-engines/src/main/kotlin/coach/engines/PlannerEngine.kt"
COMPOSER = K / "core-engines/src/main/kotlin/coach/engines/BlueprintComposer.kt"
TRANSFER_PLANNING = K / "core-application/src/main/kotlin/coach/application/TransferPlanning.kt"
BUILD_PLAN = K / "core-application/src/main/kotlin/coach/application/BuildDailyPlan.kt"
BLUEPRINT_APP = K / "core-application/src/main/kotlin/coach/application/BlueprintAssessment.kt"
PLANNER_FACTS = K / "core-model/src/main/kotlin/coach/PlannerFacts.kt"
REASONS = K / "core-model/src/main/kotlin/coach/ReasonCodes.kt"
OPEN_RESPONSE = K / "core-model/src/main/kotlin/coach/OpenResponseFacts.kt"
COPY = K / "core-presentation/src/main/kotlin/coach/presentation/ExplanationCopy.kt"
FORMAT_KT = K / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"
SOURCE_KT = K / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
SHIPPED_TEST = K / "data-curriculum/src/test/kotlin/coach/curriculum/ShippedAssessmentPackageTest.kt"
COURSE_TEST = K / "app-wiring/src/test/kotlin/coach/wiring/ShippedCourseTest.kt"
ENGINE_TEST = K / "core-engines/src/test/kotlin/coach/engines/TransferEngineTest.kt"
PLANNING_TEST = K / "core-application/src/test/kotlin/coach/application/TransferPlanningTest.kt"

CROSS_TOPIC = {"cross_topic_context", "integrated_system_context"}

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
    out = {"model": "ACNX-v0", "stage_step": "15G", "decision": "D-120", "result": "PASS" if not failures else "FAIL",
           "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
    if "--no-write" not in sys.argv:
        QA_OUT.parent.mkdir(parents=True, exist_ok=True)
        QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
    print(f"15G QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(0 if not failures else 1)


# ---------------------------------------------------------------- inputs
for path in (CONTRACT, VERIFICATION, SPEC, RESEARCH, ASSET, *EARLIER_ASSETS, BUILDER, CONTENT / "package.yaml",
             CONTENT / "independent_review.yaml", TRANSFER_ENGINE, TRANSFER_PLANNING, FORMAT_KT, SOURCE_KT, SHIPPED_TEST,
             COURSE_TEST, ENGINE_TEST, PLANNING_TEST):
    check(f"A15G-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
pkg = load(CONTENT / "package.yaml") or {}
review = load(CONTENT / "independent_review.yaml") or {}
verification = load(VERIFICATION) or {}
asset = read(ASSET)
earlier_text = "\n\n".join(read(a) for a in EARLIER_ASSETS)

check("A15G-01_model", contract.get("model") == "ACNX-v0", str(contract.get("model")))
check("A15G-01_status", contract.get("status") == "accepted_15g", str(contract.get("status")))
check("A15G-01_decision", contract.get("decision") == "D-120", str(contract.get("decision")))
ud = contract.get("user_decisions", {}) or {}
check("A15G-01_user_decisions", ud.get("approval") == "explicit_user_approval_2026-10-04" and ud.get("pool") == "15_to_20_outside_the_lesson"
      and ud.get("transfer") == "content_and_monthly_producer" and ud.get("misconception_keys") == "option_level_deterministic"
      and ud.get("authorship") == "assistant_writes_independent_agents_review" and ud.get("english_pool") == "honest_small_pool_shortfall_recorded",
      str(ud))
check("A15G-01_package", pkg.get("version") == 7 and pkg.get("supplements") == [f"curriculum/content/{s}" for s in SOURCES], "")

# ---------------------------------------------------------------- the package that ships is the package that was checked
spec = importlib.util.spec_from_file_location("builder", BUILDER)
builder = importlib.util.module_from_spec(spec)
spec.loader.exec_module(builder)


def build_inputs_digest() -> str:
    """Every file the supplement build reads: the builder, the 15G content and the six content directories it adds to."""
    h = hashlib.sha256()
    files = [BUILDER, ROOT / "tools/code_test_runner.py", *sorted(p for p in CONTENT.rglob("*") if p.is_file() and "suites" not in p.relative_to(CONTENT).parts)]
    for name in SOURCES:
        files += sorted(p for p in (ROOT / "curriculum/content" / name).rglob("*") if p.is_file() and "suites" not in p.relative_to(ROOT / "curriculum/content" / name).parts)
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
    check("A15G-02_builds", False, f"{type(error).__name__}: {error}"[:300])
    finish()
check("A15G-02_builds", True, "")
suite_files = report.pop("suite_files", {})
check("A15G-02_shipped_is_rebuilt", rebuilt == asset, "the shipped asset differs from a fresh build of its source")
check("A15G-02_generated_marker", asset.splitlines()[1:2] == ["# Generated by tools/build_curriculum_package.py from curriculum/content/15g_assessment — do not edit by hand."], "")
check("A15G-02_no_failures", report["failures"] == [], f"{len(report['failures'])}: {[f['item'] for f in report['failures']][:5]}")
check("A15G-02_report_recorded", verification.get("summary") == report["summary"] and verification.get("items") == report["items"]
      and verification.get("by_source") == report["by_source"], "arch/15g_assessment_content/content_verification.yaml is not the report of this build")
stale_suites = sorted(iid for iid, body in suite_files.items() if read(CONTENT / "suites" / f"{iid}.json") != body)
check("A15G-02_suites_written", suite_files != {} and stale_suites == []
      and sorted(p.stem for p in (CONTENT / "suites").glob("*.json")) == sorted(suite_files), f"stale or extra: {stale_suites[:5]}")
if "--skip-earlier-rebuild" not in sys.argv:
    for name, a in zip(SOURCES, EARLIER_ASSETS):
        try:
            again, earlier_report = builder.build(ROOT / "curriculum/content" / name)
            untouched = again == read(a) and earlier_report["failures"] == []
            why = f"{len(earlier_report['failures'])} build failures" if again == read(a) else "the asset differs"
        except Exception as error:  # noqa: BLE001
            untouched, why = False, f"{type(error).__name__}: {error}"[:200]
        check(f"A15G-02_earlier_untouched_{name}", untouched, f"{name} is no longer a clean, fresh build of its source: {why}")

# ---------------------------------------------------------------- a supplement adds; it never carries again or overwrites
for kind in ("domain", "module", "topic", "skill", "objective", "topic_skill", "prerequisite_edge", "explanation", "misconception"):
    check(f"A15G-03_adds_no_{kind}", f"\n[{kind}]\n" not in "\n" + asset, f"a supplement adds no {kind}")
items = sections(asset, "item")
earlier_items = {s["ref"] for s in sections(earlier_text, "item")}
earlier_objectives = {f"{s['logical_id']}@v{s['version']}" for s in sections(earlier_text, "objective")}
check("A15G-03_nothing_carried_again", items != [] and all(i["ref"] not in earlier_items for i in items), "")
check("A15G-03_targets_published_objectives", all(i["target_objectives"] in earlier_objectives for i in items),
      str([i["ref"] for i in items if i["target_objectives"] not in earlier_objectives][:3]))
validations = {v["resource"]: v["status"] for v in sections(asset, "validation")}
check("A15G-03_every_item_validated", sorted(validations) == sorted(i["ref"] for i in items) and set(validations.values()) == {"validated"}, "")
check("A15G-03_counts", (len(items), len(sections(asset, "task")), len(sections(asset, "answer_misconception")),
                         sum(1 for i in items if "transfer_profile" in i))
      == tuple((contract.get("counts") or {}).get(k) for k in ("items", "task_versions", "answer_misconceptions", "transfer_items")),
      str(contract.get("counts")))

# ---------------------------------------------------------------- independent review
reviewed = review.get("items") or {}
mapped = review.get("answer_misconceptions") or {}
check("A15G-04_reviewer", review.get("reviewer") == "independent_15g_content_review" and bool(review.get("method")), "")
added_ids = sorted(i["item"] for i in report["items"])
check("A15G-04_every_item_reviewed", sorted(reviewed) == added_ids, f"{len(reviewed)} / {len(added_ids)}")
check("A15G-04_every_item_passed", all(v.get("verdict") == "pass" for v in reviewed.values()),
      str([k for k, v in reviewed.items() if v.get("verdict") != "pass"][:5]))
check("A15G-04_every_mapping_passed", all(v.get("verdict") == "pass" for v in mapped.values())
      and sorted(m["mapping"] for m in report["answer_misconceptions"]) == sorted(mapped), f"{len(mapped)} mappings")
check("A15G-04_rounds_recorded", isinstance(review.get("rounds"), int) and review["rounds"] >= 1
      and review.get("rounds") == (contract.get("review") or {}).get("rounds"), "")

# ---------------------------------------------------------------- pools: the agreed size, or an exact, recorded shortfall
added_by_objective: dict[str, list[dict]] = {}
published_by_objective: dict[str, list[dict]] = {}
lexicon_sources: set[str] = set()
for name in SOURCES:
    for path in sorted((CONTENT / "items" / name).glob("*.yaml")):
        for o in (load(path) or {}).get("objectives", []):
            added_by_objective.setdefault(o["id"], []).extend(o.get("items") or [])
    for path in sorted((ROOT / "curriculum/content" / name / "skills").glob("*.yaml")):
        for o in (load(path) or {}).get("objectives", []):
            published_by_objective.setdefault(o["id"], []).extend(o.get("items") or [])
pools = {oid: sum(1 for it in published_by_objective[oid] if not it.get("in_lesson"))
              + sum(1 for it in added_by_objective.get(oid, []) if "transfer_profile" not in it)
         for oid in published_by_objective}
shortfall = {s["objective"]: s["pool"] for s in (contract.get("pool") or {}).get("shortfall", [])}
below = {oid: n for oid, n in pools.items() if n < 15}
check("A15G-05_pool_minimum_or_recorded", below == shortfall, f"below 15: {below}; recorded: {shortfall}")
check("A15G-05_pool_counted", len(pools) == (contract.get("pool") or {}).get("objectives") == 65, str(len(pools)))
check("A15G-05_shortfall_reason", all(s.get("reason") for s in (contract.get("pool") or {}).get("shortfall", [])), "")
english_below = sorted(o for o in shortfall if o.startswith("objective.english."))
check("A15G-05_english_shortfall_is_the_user_decision", len(english_below) == 8
      and all(s.get("decision") == "user_honest_small_pool" for s in contract["pool"]["shortfall"] if s["objective"].startswith("objective.english.")), "")

# ---------------------------------------------------------------- transfer
transfer = [i for i in items if "transfer_profile" in i]
in_tasks = {r for t in sections(earlier_text + "\n\n" + asset, "task") for r in t.get("items", "").split(",") if r}
check("A15G-06_transfer_profile", transfer != [] and all(i["transfer_profile"] in CROSS_TOPIC and i.get("context_family_id", "").startswith("context.")
                                                          for i in transfer), "")
check("A15G-06_transfer_reserved", all(i["blueprint_roles"] == "cross_topic_transfer" and i["scope_eligibility"] == "monthly_capability"
                                       and i["difficulty_class"] == "transfer_integration" for i in transfer), "")
check("A15G-06_transfer_never_in_a_task", all(i["ref"] not in in_tasks for i in transfer), "")
check("A15G-06_other_items_claim_no_transfer", all("cross_topic_transfer" not in i["blueprint_roles"] and "integration_or_transfer" not in i["blueprint_roles"]
                                                   for i in items if "transfer_profile" not in i), "AIV-v0 §16")
check("A15G-06_transfer_requires_another_skill", all(i.get("required_skills") and i["target_skills"] not in i["required_skills"].split(",") for i in transfer), "")

# ---------------------------------------------------------------- tasks: a new version only when the items grew
earlier_tasks = {f"{t['logical_id']}@v{t['version']}": t for t in sections(earlier_text, "task")}
grown = []
for t in sections(asset, "task"):
    v1 = earlier_tasks.get(f"{t['logical_id']}@v1")
    new, old = t.get("items", "").split(","), (v1 or {}).get("items", "").split(",")
    grown.append(t["version"] == "2" and v1 is not None and set(old) <= set(new) and len(new) > len(old)
                 and t["explanations"] == v1["explanations"] and t["serves"] == v1["serves"] and t["purpose"] == v1["purpose"])
check("A15G-07_tasks_grow_as_version_2", grown != [] and all(grown), f"{grown.count(False)} not grown v2")
check("A15G-07_no_teach_task", not any(t["logical_id"].endswith(".teach") for t in sections(asset, "task")), "a supplement writes no lesson")
check("A15G-07_tasks_validated", all(t["lifecycle_status"] == "validated" for t in sections(asset, "task")), "")

# ---------------------------------------------------------------- wrong-option keys
keys = {f"{k['logical_id']}@v{k['version']}": k for k in sections(earlier_text + "\n\n" + asset, "answer_key")}
accepted = {}
for a in sections(earlier_text + "\n\n" + asset, "accepted_answer"):
    accepted.setdefault(a["key"], set()).add(a["text"].strip())
labels = {f"{m['logical_id']}@v{m['version']}": m for m in sections(earlier_text, "misconception")}
bad = []
for m in sections(asset, "answer_misconception"):
    key, label = keys.get(m["key"]), labels.get(m["misconception"])
    if key is None or label is None or label["objective"] != key["objective"] or m["text"].strip() in accepted.get(m["key"], set()) \
            or not re.fullmatch(r"[A-D]", m["text"]):
        bad.append(m["key"] + ":" + m["text"])
check("A15G-08_mappings_own_objective_wrong_option", sections(asset, "answer_misconception") != [] and bad == [], str(bad[:5]))
check("A15G-08_mapping_once", len({(m["key"], m["text"]) for m in sections(asset, "answer_misconception")}) == len(sections(asset, "answer_misconception")), "")

# ---------------------------------------------------------------- the form can produce the evidence it is stored as
objective_docs = {}
for name in SOURCES:
    for path in sorted((ROOT / "curriculum/content" / name / "skills").glob("*.yaml")):
        for o in (load(path) or {}).get("objectives", []):
            objective_docs[o["id"]] = o
form_bad = []
for oid, its in added_by_objective.items():
    o = objective_docs[oid]
    for it in its:
        evidence = it.get("evidence_type") or o.get("required_direct_type")
        if (evidence == "authored_code" and "suite" not in it) or (evidence == "written_or_spoken_production" and it.get("options")) \
                or (evidence == "hands_on_system_task" and "suite" not in it and not it.get("hands_on")) \
                or (it.get("evidence_type") and it["evidence_type"] not in o.get("acceptable_evidence_types", [])):
            form_bad.append(f"{oid}:{it['slug']}")
check("A15G-09_form_matches_evidence", form_bad == [], str(form_bad[:5]))
builder_src = read(BUILDER)
fp = builder_src[builder_src.find("def form_problems("):builder_src.find("def transfer_problems(")]
check("A15G-09_builder_form_rule", all(s in fp for s in ('evidence == "authored_code" and "suite" not in it',
                                                         'evidence == "hands_on_system_task" and "suite" not in it and not it.get("hands_on")',
                                                         'evidence == "written_or_spoken_production" and it.get("options")',
                                                         'not in objective.get("acceptable_evidence_types", [])'))
      and "problems = form_problems(it, o)" in builder_src, "")
tp = builder_src[builder_src.find("def transfer_problems("):builder_src.find("# ------------------------------------------------------------------------------------------------ build")]
check("A15G-09_builder_transfer_rule", all(s in tp for s in ("not in CROSS_TOPIC_PROFILES", 'not it.get("context_family")', "if skill in declared:",
                                                             "topic_of.get(r) != topic_of.get(skill)", '!= "transfer_integration"'))
      and 'CROSS_TOPIC_PROFILES = {"cross_topic_context", "integrated_system_context"}' in builder_src, "")
check("A15G-09_builder_never_overwrites", 'if supplement is not None and plan["items"] == published_plans[kind]["items"]:' in builder_src
      and "task_version = 1 if supplement is None else 2" in builder_src, "")

# ---------------------------------------------------------------- Kotlin: the owner, and everyone who must ask it
te = strip_comments(read(TRANSFER_ENGINE))
check("A15G-10_owner_learned", "state.lifecycleStatus in ON_ROUTE && state.mastery == MasteryAxisState.CONFIRMED_CURRENT" in te, "")
check("A15G-10_owner_transfer_item", all(s in te for s in ("item.transferProfile?.crossTopic == true", "AssessmentScope.MONTHLY_CAPABILITY in item.scopeEligibility",
                                                           "MonthlyRole.CROSS_TOPIC_TRANSFER in item.blueprintRoles")), "")
check("A15G-10_owner_clean_measurement", all(s in te for s in ("row.evaluatorStatus == EvaluatorStatus.VERIFIED", "row.independenceClass == IndependenceClass.INDEPENDENT",
                                                               "!row.contested", "row.prerequisiteValid", "!row.solutionExposed", "row.outcome != EvidenceOutcome.INVALID")), "")
check("A15G-10_owner_no_threshold", not re.search(r"\b0\.\d+|\bthreshold\s*=", te), "nothing here is a threshold")
tpl = strip_comments(read(TRANSFER_PLANNING))
check("A15G-10_facts_store_gate_exposure", all(s in tpl for s in ("StoreTrust.apply(persistence, it)", "TransferEngine.measured(", "persistence.exposuresFor(",
                                                                  "item.ref !in shown", "item.variantFamilyId !in solved", ".eligibility.waits")), "")
check("A15G-10_planner_asks", "TransferPlanning.needs(persistence, content, states)" in strip_comments(read(BUILD_PLAN)), "")
check("A15G-10_composer_asks", "TransferPlanning.needs(persistence, content, states)" in strip_comments(read(BLUEPRINT_APP)), "")
check("A15G-10_monthly_role", "NeedTrigger.TRANSFER_OPPORTUNITY -> Either.Role(MonthlyRole.CROSS_TOPIC_TRANSFER)" in strip_comments(read(MONTHLY)), "")
check("A15G-10_weekly_excluded", "NeedTrigger.TRANSFER_OPPORTUNITY -> Either.Excluded(BlueprintExclusion.TRANSFER_IS_MONTHLY)" in strip_comments(read(WEEKLY)), "")
check("A15G-10_band_with_integration", re.search(r"NeedTrigger\.INTEGRATION_OPPORTUNITY, NeedTrigger\.TRANSFER_OPPORTUNITY ->\s*if \(need\.requiredByCurriculum\) PriorityBand\.P3 else PriorityBand\.P4",
                                                strip_comments(read(PLANNER))) is not None, "")
check("A15G-10_composer_refuses_unsupported", "SlotItemRefusal.TRANSFER_CLAIM_UNSUPPORTED" in strip_comments(read(COMPOSER)), "")
check("A15G-10_trigger_declared", 'TRANSFER_OPPORTUNITY("transfer_opportunity", "need.transfer_opportunity")' in read(PLANNER_FACTS)
      and '"need.transfer_opportunity"' in read(REASONS) and '"need.transfer_opportunity" to' in read(COPY), "")
orf = strip_comments(read(OPEN_RESPONSE))
check("A15G-10_wrong_answer_hypothesis", "if (accepts(response)) return null" in orf and "an accepted answer is never a misconception" in orf
      and "misconceptionHypotheses = listOfNotNull(key.misconceptionFor(response))" in orf, "")
fmt = strip_comments(read(FORMAT_KT))
check("A15G-10_format_supplement", all(s in fmt for s in ('"answer_misconception" to setOf("key", "text", "misconception")', "fun parse(text: String, earlier: List<Parsed> = emptyList())",
                                                          "the misconception belongs to another Objective", "an accepted answer is never a misconception",
                                                          '"transfer_profile", "context_family_id"', "unknown transfer profile")), "")
src = strip_comments(read(SOURCE_KT))
check("A15G-10_highest_task_version", ".groupBy { it.ref.logicalId }.values.map { versions -> versions.maxBy { it.ref.version } }" in src
      and 'once("wrong answer"' in src, "")

# ---------------------------------------------------------------- tests
for path, names in ((SHIPPED_TEST, ["the package is version 7, reads after the six it adds to, and adds no Skill, Objective, edge, Topic or label",
                                    "a task is published again only as version 2 of a published task whose items grew, and only the newest is offered",
                                    "a transfer item names its cross-topic context and is reserved for the month's transfer slot, never spent by a task",
                                    "every wrong-option key names a label of its own key's Objective, and an accepted answer never names one"]),
                    (COURSE_TEST, ["all seven shipped packages are published into the real store, oldest first, and the supplement adds items, not Skills",
                                   "with the supplement, a learner who has done nothing is offered the same entry lessons, and no transfer opens before learning"])):
    text = read(path)
    for name in names:
        check(f"A15G-11_test_{name[:40]}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- verification claims
for key in ("mutation_results_builder", "mutation_results_kotlin"):
    mr = contract.get(key) if isinstance(contract.get(key), dict) else {}
    mids = [m.get("id") for m in mr.get("mutants", [])]
    check(f"A15G-12_{key}_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
          and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
    check(f"A15G-12_{key}_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("A15G-12_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("A15G-12_runs_pass", len(runs) >= 7 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") if isinstance(contract.get("device_verification"), dict) else {}
check("A15G-12_t6_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("sqlite_publish_on_jvm_run") is True
      and dv.get("sqlite_publish_on_device_run") is False, "")

# ---------------------------------------------------------------- spec, research, decision
spec_text = read(SPEC)
check("A15G-13_spec_status", "**Status:** ACCEPTED — independent 15G QA PASS" in spec_text and "`D-120`" in spec_text and "__" not in spec_text, "")
check("A15G-13_spec_next", "**15H — Content QA**" in spec_text, "")
check("A15G-13_spec_t6", "**Not run: T6.**" in spec_text, "")
research = read(RESEARCH)
check("A15G-13_research", all(url in research for url in ("https://en.wikipedia.org/wiki/Transfer_of_learning", "https://en.wikipedia.org/wiki/Multiple_choice",
                                                          "https://en.wikipedia.org/wiki/Concept_inventory")), "every source is named by its URL")
check("A15G-13_decision", re.search(r"^#+ .*D-120", read(DECISIONS), re.M) is not None, "D-120 heading in DECISIONS")

finish()
