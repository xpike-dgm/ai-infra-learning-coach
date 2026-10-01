"""Independent 13E QA — PCRX-v0 Program Change Report Implementation.

The result families are read out of `ASUX-v0` §13.1 itself, the replan reasons out of the planner's own
`ReplanTrigger`, the criticality set out of `KGC-v0` and the authored Objectives, and all of them are compared
with the real Kotlin — never with 13E's own contract alone.

The checks that matter most are structural: a change claimed from a state nobody had written, a review coming
due reported as a change, a contradiction reported as lost mastery, a hypothesis filed as a gap, a first plan
reported as a plan change, a replan with nothing changed, and a report that reads answers instead of state
must be **unrepresentable** as a PASS here.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/13e_program_change_report/program_change.yaml"
SPEC = ROOT / "docs/PROGRAM_CHANGE_REPORT_IMPL_SPEC.md"
RESEARCH = ROOT / "research/13e_program_change_report_research.md"
QA_OUT = ROOT / "arch/13e_program_change_report/qa_report.yaml"
ASUX = ROOT / "docs/ASSESSMENT_SESSION_UX_SPEC.md"
KGC = ROOT / "docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md"
V1_SCOPE = ROOT / "docs/V1_SCOPE.md"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/ProgramChangeFacts.kt"
PLANNER_FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/PlannerFacts.kt"
ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/ProgramChangeEngine.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/ProgramChanges.kt"
PREREQ_APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/ResolvePrerequisites.kt"
PRES_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/ProgramChangePresentation.kt"
SESSION_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/AssessmentSession.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
STORE_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/CurriculumStore.kt"
SQL_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"

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


def body(source: str, signature: str) -> str:
    at = source.find(signature)
    if at < 0:
        return ""
    open_at = source.find("{", at)
    depth = 0
    for index in range(open_at, len(source)):
        if source[index] == "{":
            depth += 1
        elif source[index] == "}":
            depth -= 1
            if depth == 0:
                return source[at:index + 1]
    return ""


for path in (CONTRACT, SPEC, RESEARCH, FACTS_KT, ENGINE_KT, APP_KT, PRES_KT):
    check(f"E13E-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

contract = yaml.safe_load(read(CONTRACT)) or {}
facts, planner_facts, engine = strip_comments(read(FACTS_KT)), strip_comments(read(PLANNER_FACTS_KT)), strip_comments(read(ENGINE_KT))
app, prereq_app, pres = strip_comments(read(APP_KT)), strip_comments(read(PREREQ_APP_KT)), strip_comments(read(PRES_KT))
session, ports = strip_comments(read(SESSION_KT)), strip_comments(read(PORTS_KT))
store, sql, schema = strip_comments(read(STORE_KT)), strip_comments(read(SQL_KT)), strip_comments(read(SCHEMA_KT))
pres_raw = read(PRES_KT)
spec_text, research_text, asux, kgc, v1 = read(SPEC), read(RESEARCH), read(ASUX), read(KGC), read(V1_SCOPE)

# ---------------------------------------------------------------- contract head and the accepted texts it rests on
check("E13E-01_model", contract.get("model") == "PCRX-v0", str(contract.get("model")))
check("E13E-01_status", contract.get("status") == "accepted_13e", str(contract.get("status")))
check("E13E-01_decision", contract.get("decision") == "D-103", str(contract.get("decision")))
check("E13E-01_semantics", contract.get("result_semantics") == "ASUX-v0", str(contract.get("result_semantics")))
scope = contract.get("scope", {})
for key, expected in {"state_changes_reported": True, "plan_changes_reported": True, "recompute_orchestrated": True,
                      "replan_only_on_state_change": True, "result_families_filled": True, "schema_changed": False,
                      "migration_added": False, "interfaces_added": False, "boundaries_changed": False,
                      "planner_rule_changed": False, "mastery_rule_changed": False, "retention_rule_changed": False,
                      "weakness_rule_changed": False, "gate_rule_changed": False, "durable_assessment_report": False,
                      "app_calls_report": False}.items():
    check(f"E13E-01_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")
check("E13E-01_asux_truthful", "The result may state a Skill state change **only if canonical state actually changed**." in asux
      and "If nothing changed, the result says so plainly rather than manufacturing a progress claim." in asux, "ASUX-v0 §13.4 moved")
check("E13E-01_asux_no_demotion", "never as instant unmastery, and never as a demotion event" in asux, "ASUX-v0 §13.4 moved")
check("E13E-01_v1_criterion", "3. weekly/monthly assessment gelecek planı değiştirmeli," in v1, "V1 release definition moved")

# ---------------------------------------------------------------- vocabularies, read from ASUX-v0 and the planner
families_block = re.search(r"## 13\.1 The result is semantic, not a grade.*?```text\n(.*?)```", asux, re.S)
asux_families = families_block.group(1).split() if families_block else []
check("E13E-02_asux_families", asux_families == ["confirmed_capabilities", "verification_needed", "persistent_targeted_gaps",
                                                  "retention_revalidated", "not_reliably_measured", "plan_changes"], str(asux_families))
result_family_ids = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\)', body(session, "enum class ResultFamily"), re.M)
check("E13E-02_result_family_enum", result_family_ids == asux_families, str(result_family_ids))
kinds = re.findall(r'^\s+([A-Z_]+)\("([a-z_]+)", "([a-z_]+)", "([a-z_.]+)"\)', body(facts, "enum class StateChangeKind"), re.M)
# Narrowed at 13F: 13E's eleven kinds come first and unchanged; any kind after them must be declared, with its family,
# by the accepted later contract that added it (`state_change_extension` / `state_change_families`), and its family
# must still be one of ASUX-v0 §13.1's.
extension, extension_families = [], {}
for _path in sorted(ROOT.glob("arch/*/*.yaml")):
    try:
        _doc = yaml.safe_load(_path.read_text(encoding="utf-8")) or {}
    except Exception:
        continue
    if isinstance(_doc, dict) and str(_doc.get("status", "")).startswith("accepted_") and _doc.get("state_change_extension"):
        extension += list(_doc["state_change_extension"])
        extension_families.update(_doc.get("state_change_families") or {})
own_kinds = contract.get("state_changes", {}).get("kinds") or []
check("E13E-02_kinds_equal_contract", [k[1] for k in kinds] == own_kinds + extension, str([k[1] for k in kinds]))
state_families = set(asux_families) - {"not_reliably_measured", "plan_changes"}
check("E13E-02_every_kind_in_a_state_family", bool(kinds) and all(
    (k[2] in state_families) if k[1] in own_kinds else (k[2] == extension_families.get(k[1]) and k[2] in asux_families) for k in kinds),
    str([k[2] for k in kinds]))
replan_codes = set(re.findall(r'^\s+[A-Z_]+\("[a-z_]+", "(replan\.[a-z_]+)"\)', body(planner_facts, "enum class ReplanTrigger"), re.M))
check("E13E-02_reasons_are_the_planners", bool(kinds) and all(k[3] in replan_codes for k in kinds), f"{sorted({k[3] for k in kinds} - replan_codes)}")
by_id = {k[1]: k for k in kinds}
check("E13E-02_hypothesis_is_a_question", by_id.get("weakness_question_opened", ("", "", ""))[2] == "verification_needed", "a hypothesis is filed as a gap")
check("E13E-02_contradiction_is_verification", by_id.get("verification_opened", ("", "", ""))[2] == "verification_needed"
      and by_id.get("verification_opened", ("", "", "", ""))[3] == "replan.new_verification_created", "a contradiction is not a verification")
check("E13E-02_remediation_reason", by_id.get("remediation_opened", ("", "", "", ""))[3] == "replan.new_remediation_created", "remediation reason")
plan_kinds = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\)', body(facts, "enum class PlanChangeKind"), re.M)
check("E13E-02_plan_kinds", plan_kinds == ["need_opened", "need_closed", "task_added", "task_removed"], str(plan_kinds))

# ---------------------------------------------------------------- the report is a diff that cannot overclaim
report_cls = body(facts, "data class ProgramChangeReport(")
check("E13E-03_reads_forward", "require(toWatermark >= fromWatermark)" in report_cls, "a report may read backwards")
check("E13E-03_nothing_changed", "stateChanges.isEmpty() && planChanges.isEmpty()" in report_cls, "nothingChanged ignores a section")
check("E13E-03_no_score_field", not re.search(r"(?i)\b(score|percent|grade|pass(ed)?|fail(ed)?|threshold)\b", facts + engine), "a verdict field")
check("E13E-03_no_numeric_state", not re.search(r"\b(Double|Float)\b", facts + engine), "a numeric state")

state_fn = body(engine, "fun stateChanges(")
unwritten = re.search(r'private const val UNWRITTEN = "([a-z_]+)"', engine)
nye = re.search(r'const val NOT_YET_EVALUATED = "([a-z_]+)"', prereq_app)
check("E13E-04_unwritten_is_the_stores", unwritten is not None and nye is not None and unwritten.group(1) == nye.group(1), "unwritten drifted")
for axis in ("m", "r", "w"):
    check(f"E13E-04_unwritten_guard_{axis}", f"if ({axis}0 == UNWRITTEN) {{\n            if ({axis}1 != UNWRITTEN) unknown += skill\n        }} else if ({axis}0 != {axis}1) {{" in state_fn,
          f"axis {axis} claims a change from a state nobody had written")
check("E13E-04_one_name_per_change", "linkedMapOf<StateChangeKind, StateChange>()" in state_fn and "out.putIfAbsent(kind," in state_fn, "a change named twice")
check("E13E-04_capability_only_at_confirmation", "m0 !in MASTERED && m1 == MasteryAxisState.CONFIRMED_CURRENT.id -> add(StateChangeKind.CAPABILITY_CONFIRMED" in state_fn,
      "development reported as a capability")
check("E13E-04_contradiction_opens_verification",
      "m0 == MasteryAxisState.CONFIRMED_CURRENT.id && m1 == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE.id ->\n                    add(StateChangeKind.VERIFICATION_OPENED, m0, m1)" in state_fn,
      "a contradiction is reported as lost mastery")
check("E13E-04_contradiction_before_loss", 0 <= state_fn.find("add(StateChangeKind.VERIFICATION_OPENED, m0, m1)") < state_fn.find("add(StateChangeKind.MASTERY_NO_LONGER_CONFIRMED"),
      "loss checked before the contradiction")
check("E13E-04_mastered_set", "setOf(MasteryAxisState.CONFIRMED_CURRENT.id, MasteryAxisState.CONFIRMATION_VERIFICATION_DUE.id)" in engine, "mastered set")
check("E13E-04_review_due_is_the_day", not re.search(r"r1 == RetentionAxis\.(REVIEW_DUE|FRESH)\b", state_fn) and "REVIEW_DUE.id ->" not in state_fn,
      "a review coming due is reported")
revalidated = re.search(r"REVALIDATED_FROM = setOf\(([^)]*)\)", engine)
check("E13E-04_revalidated_from_checks", revalidated is not None and "UNTRACKED" not in revalidated.group(1) and "STABLE" not in revalidated.group(1)
      and "REVIEW_DUE" in revalidated.group(1), "revalidated from a state with nothing to revalidate")
check("E13E-04_downgrade_not_supported", "w1 == WeaknessAxis.SUPPORTED.id && w0 != WeaknessAxis.REMEDIATION_REQUIRED.id ->" in state_fn, "a downgrade is a new weakness")
check("E13E-04_question_only_from_closed", "w1 == WeaknessAxis.HYPOTHESIS.id && w0 in CLOSED ->" in state_fn, "a downgrade is a new question")
check("E13E-04_remediation_closes_to_closed", "w0 == WeaknessAxis.REMEDIATION_REQUIRED.id && w1 in CLOSED ->" in state_fn, "remediation closed by a downgrade")
check("E13E-04_resolved_only_if_open", "w0 in OPEN_WEAKNESS && w1 == WeaknessAxis.RESOLVED.id ->" in state_fn, "a weakness never open reported resolved")
check("E13E-04_reads_no_answers", not re.search(r"\b(EvidenceRow|EvidenceOutcome|quality|outcome|Attempt)\b", engine), "the report reads answers")
check("E13E-04_pure", "import coach.ports" not in engine and "persistence" not in engine.lower(), "the engine reads a store")

report_fn = body(engine, "fun report(")
check("E13E-05_first_plan_marked", "val firstPlan = before.plan == null && after.plan != null" in report_fn, "a first plan unmarked")
check("E13E-05_same_version_no_diff", "if (plan0 == null || plan1 == null || before.planVersionId == after.planVersionId) emptyList()" in report_fn,
      "the same plan version is diffed, or a first plan is a change")
check("E13E-05_replan_reasons_only", '.filter { it.startsWith("replan.") }' in report_fn, "non-replan plan codes leak")
check("E13E-05_reasons_need_a_new_plan", "if (plans.isNotEmpty() || (before.planVersionId != null && before.planVersionId != after.planVersionId)) {" in report_fn,
      "replan reasons claimed with no new plan")
check("E13E-05_stable_order", "after.skills.values.sortedBy { it.skill.toString() }" in report_fn, "unstable order")
plan_fn = body(engine, "fun planChanges(")
for part in ("(needs1.keys - needs0.keys)", "(needs0.keys - needs1.keys)", "(tasks1.keys - tasks0.keys)", "(tasks0.keys - tasks1.keys)"):
    check(f"E13E-05_plan_diff_{part[1:13]}", part in plan_fn, f"missing {part}")
check("E13E-05_plan_keys", "associateBy { it.needKey }" in plan_fn and "associateBy { it.candidateId }" in plan_fn, "plan diff keys")

# ---------------------------------------------------------------- recompute, snapshot and replan
recompute = body(app, "fun recompute(")
order = [recompute.find(f"{name}(persistence, clock).rebuild(") for name in ("RebuildMastery", "RebuildRetention", "RebuildWeakness", "RebuildReadiness")]
check("E13E-06_recompute_order", all(p >= 0 for p in order) and order == sorted(order), f"{order}")
check("E13E-06_profiles_from_curriculum", "persistence.objectivesOf(skill).map(ObjectiveGateProfiles::of)" in recompute, "profiles not from the curriculum")
check("E13E-06_nothing_to_recompute", "if (profiles.isEmpty()) return false" in recompute, "an unpublished Skill is recomputed")
check("E13E-06_no_truth", "appendTruth" not in app and "writeProjection" not in app, "the report writes state itself")
capture = body(app, "fun capture(")
check("E13E-06_watermark_first", 0 <= capture.find("persistence.truthWatermark()") < capture.find("persistence.publishedSkills()") < capture.find("persistence.latestPlan()"),
      "the watermark is read after state")
check("E13E-06_unwritten_axis", "axes[column]?.ifEmpty { null } ?: ResolvePrerequisites.NOT_YET_EVALUATED" in capture, "an empty axis reads as written")
report_app = body(app, "fun report(before: ProgramSnapshot")
check("E13E-06_replan_only_on_change", "val built = trigger?.let {" in report_app and "kinds.isEmpty() -> null" in app, "a replan with nothing changed")
check("E13E-06_planner_own_replan", "BuildDailyPlan(persistence, content, clock).replan(BuildDailyPlan.ReplanRequest(trigger = it, capacity = capacity))" in report_app,
      "the plan is changed outside the planner")
check("E13E-06_report_after_plan", "return Reported(ProgramChangeEngine.report(before, capture.capture()), built)" in report_app, "the report is read before the new plan")
triggers = re.findall(r"-> ReplanTrigger\.([A-Z_]+)", body(app, "fun triggerFor("))
getter = re.search(r"val setsRemainingTime: Boolean\s+get\(\) = ([^\n]+)", planner_facts)
setters = set(re.findall(r"this == ([A-Z_]+)", getter.group(1))) if getter else set()
# Narrowed at 13F: 13E's order holds — remediation, then verification, and new evidence last; an event between them must
# be one a later accepted contract declares (`replan_trigger_for_waiver`) and one 12D's `ReplanTrigger` already has.
declared_triggers = set()
for _path in sorted(ROOT.glob("arch/*/*.yaml")):
    try:
        _doc = yaml.safe_load(_path.read_text(encoding="utf-8")) or {}
    except Exception:
        continue
    if isinstance(_doc, dict) and str(_doc.get("status", "")).startswith("accepted_") and _doc.get("replan_trigger_for_waiver"):
        declared_triggers.add(str(_doc["replan_trigger_for_waiver"]).upper())
check("E13E-06_triggers", triggers[:2] == ["NEW_REMEDIATION_CREATED", "NEW_VERIFICATION_DUE_CREATED"] and triggers[-1:] == ["NEW_EVIDENCE_RECORDED"]
      and set(triggers[2:-1]) <= declared_triggers, str(triggers))
check("E13E-06_budget_kept", bool(setters) and not (set(triggers) & setters), f"{set(triggers) & setters}")

# ---------------------------------------------------------------- gate profiles from the published curriculum
profiles = body(facts, "object ObjectiveGateProfiles")
check("E13E-07_kgc_criticality", "- criticality: standard | critical" in kgc, "KGC-v0 criticality set moved")
authored = set()
for path in sorted((ROOT / "curriculum/decomposition").glob("*/objectives.yaml")):
    authored |= set(re.findall(r"^\s+criticality: (\w+)", read(path), re.M))
check("E13E-07_authored_criticality", authored == {"critical", "standard"}, str(sorted(authored)))
check("E13E-07_profile_mapping", '"critical" -> true' in profiles and '"standard" -> false' in profiles
      and "else -> throw IllegalArgumentException" in profiles, "an unknown criticality is read as standard")
check("E13E-07_profile_fields", all(f in profiles for f in ("required = row.required,", "acceptableEvidenceTypes = row.acceptableEvidenceTypes,",
                                                              "directEvidenceTypes = row.directEvidenceTypes,", "requiredDirectType = row.requiredDirectType,")),
      "a profile field not read from the curriculum")
check("E13E-07_no_invented_gate", not re.search(r"min(IndependentGroups|VariantFamilies)\s*=|requires(Transfer|UserAuthoredArtifact|NonBasicEvidence)\s*=", profiles),
      "a gate parameter invented")

# ---------------------------------------------------------------- presentation
canonical = body(pres, "fun canonicalChanges(")
check("E13E-08_family_from_kind", "ResultFamily.entries.single { it.id == change.kind.family }" in canonical, "a change lands in the wrong family")
check("E13E-08_plan_section", "families[ResultFamily.PLAN_CHANGES]" in canonical, "plan changes not their own section")
summary = body(pres, "fun summary(")
check("E13E-08_summary_only_when_nothing", "!report.nothingChanged -> null" in summary and "report.firstPlan -> ProgramChangeCopy.FIRST_PLAN" in summary,
      "the nothing-changed sentence over real changes")
copy_strings = re.findall(r'"([^"]*[a-zçğıöşü][^"]*)"', body(pres_raw, "object ProgramChangeCopy"))
lowered = [s.lower() for s in copy_strings]
for word in ("%", "puan", "başarısız", "geçti", "kaldın", "borç", "daha az önemli", "geride", "unuttun", "seri", "not:"):
    check(f"E13E-08_copy_no_{word.strip()[:12]}", all(word not in s for s in lowered), f"{word!r} in copy")
check("E13E-08_hypothesis_not_deficiency", any("henüz bir eksik değil" in s for s in lowered), "a hypothesis worded as a deficiency")
check("E13E-08_templates_cover_kinds", all(f"StateChangeKind.{k[0]} to" in pres for k in kinds), "a change with no sentence")
check("E13E-08_no_combining_dot", chr(0x0307) not in pres_raw, "U+0307 in copy")

# ---------------------------------------------------------------- port and storage
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
msbx = yaml.safe_load(read(MSBX)) or {}
check("E13E-09_port_count", sorted(interfaces) == sorted(p["id"] for p in msbx["ports"]["set"]) and contract.get("port_count") == 4, str(interfaces))
check("E13E-09_refinement_declared", contract.get("port_refinements") == [{"port": "PersistencePort", "method": "objectivesOf"}], str(contract.get("port_refinements")))
check("E13E-09_port_method", "fun objectivesOf(skill: VersionedRef): List<ObjectiveRow>" in ports, "port method")
objectives_of = body(store, "fun objectivesOf(")
check("E13E-09_store_pins_version", "WHERE parent_skill_logical_id = ? AND parent_skill_version = ? ORDER BY logical_id, version" in objectives_of, "version not pinned or order unstable")
check("E13E-09_store_reads_criticality", "criticality = statement.getText(3)," in objectives_of and "required = statement.getLong(2) == 1L," in objectives_of, "fields")
check("E13E-09_adapter_delegates", "override fun objectivesOf(skill: VersionedRef): List<ObjectiveRow> = curriculumStore.objectivesOf(skill)" in sql, "adapter")
# Narrowed at 13F: 13E itself changed no schema; a later version must be owned by the accepted contract that added it.
_owned = {(int(m["from"]), int(m["to"])) for f in ROOT.glob("arch/*/*.yaml")
          for m in [(yaml.safe_load(f.read_text(encoding="utf-8")) or {}).get("schema_migration") or {}]
          if isinstance(m, dict) and m.get("from") is not None and m.get("to") is not None}
_version = int((re.search(r"const val VERSION = (\d+)", schema) or re.search(r"(0)", "0")).group(1))
check("E13E-09_schema_unchanged", _version >= 6 and all((v - 1, v) in _owned for v in range(7, _version + 1)) and "schema_migration" not in contract,
      "schema changed without an owning contract")

# ---------------------------------------------------------------- tests named
suites = contract.get("suites", {})
named_tests = {
    "model": ["a gate profile is the curriculum's own required flag, criticality and evidence types",
              "a criticality the curriculum does not define is refused, not read as standard",
              "every state change belongs to one of the result's families and records a replan reason",
              "a report reads forward and says nothing changed only when nothing did"],
    "engine": ["a contradiction opens a verification and does not take mastery away", "a review coming due is the day, not a change",
               "a hypothesis is an open question, support is not yet a deficiency", "a state nobody had written is not a before",
               "the plan diff is needs opened and closed and tasks added and removed",
               "the same plan version is not a plan change, and a first plan is not one either"],
    "application": ["a contradiction opens a verification and the next plan carries it",
                    "a failed re-check confirms a remediation and the next plan repairs it",
                    "evidence that changes no state changes no plan, and the report says nothing changed",
                    "the first reading of a Skill claims no change from a state nobody had written",
                    "recomputing writes every axis from the published Objectives"],
    "presentation": ["each change lands in its own family and nowhere else", "a weakness question is never filed or worded as a gap",
                     "plan changes are their own section and a first plan is not a change",
                     "no sentence scores, grades, blames or calls dropped work less important"],
    "storage": ["a Skill version's Objectives come back as published, in a stable order", "a new Skill version reads only its own Objectives"],
}
for suite, names in named_tests.items():
    text = read(ROOT / suites.get(suite, {}).get("file", "missing"))
    for name in names:
        check(f"E13E-10_{suite}_{name[:40]}", f"`{name}`" in text, f"missing test: {name}")

# ---------------------------------------------------------------- honesty
mutation = contract.get("mutation_results", {})
check("E13E-11_mutation_all_detected", mutation.get("detected") == mutation.get("total") == len(mutation.get("mutants", [])) >= 40,
      f"{mutation.get('detected')}/{mutation.get('total')}")
check("E13E-11_mutation_control", mutation.get("negative_control_result") == "survived_as_expected" and mutation.get("compile_failure_is_detection") is False
      and mutation.get("only_program_change_suites_run") is True and mutation.get("final_run_is_a_single_clean_run") is True, "harness honesty")
vm = contract.get("validator_mutation", {})
check("E13E-11_validator_mutation", vm.get("detected") == vm.get("total") and (vm.get("total") or 0) >= 20 and vm.get("negative_control_result") == "no_false_positive", str(vm))
runs = {r["id"]: r for r in contract.get("verified_runs", [])}
check("E13E-11_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
device = contract.get("device_verification", {})
check("E13E-11_device_not_claimed", device.get("t6_run") is False and device.get("claimed") is False, "a device result is claimed")
loops = contract.get("open_loops", {})
for loop, owner in (("durable_assessment_report", "16C"), ("app_calls_report", "16D"), ("transfer_artifact_gate_fields", "15"), ("result_wording", "14")):
    check(f"E13E-11_open_{loop[:30]}", str(loops.get(loop, {}).get("owner")) == owner, f"{loop} not owned")
forbidden = set(contract.get("forbidden_report_patterns", []))
for pattern in ("change_claimed_from_unwritten_state", "review_due_reported_as_change", "contradiction_reported_as_demotion",
                "hypothesis_reported_as_gap", "first_plan_reported_as_change", "replan_without_state_change",
                "report_reads_answers", "score_or_percentage_in_result", "dropped_work_called_less_important"):
    check(f"E13E-12_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 13E QA PASS", "**Decision:** `D-103`", "A report is a diff of two readings",
                 "Not run: T6", "Mutation 42/42", "13F — Tanısal atlama (VDW-v0)"]:
    check(f"E13E-13_spec_{fragment[:26]}", fragment in spec_text, f"spec: {fragment!r}")
for fragment in ["No web research pass was needed", "ASUX-v0", "16C", "16D"]:
    check(f"E13E-13_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E13E-14_no_repeated_context_heading", len(context_headings) == len(set(context_headings)), "repeated context heading")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E13E-14_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)), "repeated master plan step")
combining = chr(0x0307)
check("E13E-14_no_combining_dot", all(combining not in read(p) for p in (SPEC, RESEARCH, CONTRACT, FACTS_KT)), "U+0307 in 13E text")

passed_count = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "PCRX-v0", "stage_step": "13E", "decision": "D-103",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed_count, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"13E_PROGRAM_CHANGE_QA={report['result']}")
print(f"checks={passed_count}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
