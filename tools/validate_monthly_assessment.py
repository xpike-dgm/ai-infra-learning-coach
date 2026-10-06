"""Independent 13B QA — MCAX-v0 Monthly Capability Assessment Implementation.

The vocabularies are read out of the accepted contracts' own text — `MCA-v0` §5 roles, §27 statuses and §29
reason codes from `docs/MONTHLY_ASSESSMENT_SPEC.md` — and compared with the real Kotlin, never with 13B's own
contract alone. 13A's weekly vocabulary is compared with 13A's accepted contract, so a weekly value changed to
make room for the month fails here.

The checks that matter most are structural: a monthly label that adds weight, a second assessment architecture
beside the common contract, a critical Skill tested for being critical, a role used as a quota or a percentage,
a weekly-only item in a monthly slot, a monthly queue or budget beside the planner's, a missed month turned into
debt, a clean negative or assisted work called a revalidation, and a monthly row without its own blueprint must
be **unrepresentable** as a PASS here.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/13b_monthly_assessment/monthly_assessment.yaml"
WEEKLY_CONTRACT = ROOT / "arch/13a_weekly_assessment/weekly_assessment.yaml"
SPEC = ROOT / "docs/MONTHLY_ASSESSMENT_IMPL_SPEC.md"
RESEARCH = ROOT / "research/13b_monthly_assessment_research.md"
QA_OUT = ROOT / "arch/13b_monthly_assessment/qa_report.yaml"
MCA = ROOT / "docs/MONTHLY_ASSESSMENT_SPEC.md"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
WEEKLY_VALIDATOR = ROOT / "tools/validate_weekly_assessment.py"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/MonthlyAssessmentFacts.kt"
WEEKLY_FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/WeeklyAssessmentFacts.kt"
COMMON_KT = ANDROID / "core-model/src/main/kotlin/coach/AssessmentBlueprint.kt"
CODEC_KT = ANDROID / "core-model/src/main/kotlin/coach/BlueprintCodec.kt"
ITEM_KT = ANDROID / "core-model/src/main/kotlin/coach/AssessmentFacts.kt"
ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/MonthlyBlueprintEngine.kt"
WEEKLY_ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/WeeklyBlueprintEngine.kt"
COMPOSER_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/BlueprintComposer.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/BlueprintAssessment.kt"
BUILD_KT = ANDROID / "core-application/src/main/kotlin/coach/application/BuildDailyPlan.kt"
PRES_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/BlueprintAssessmentSession.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
PKG_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"
OLD_FILES = [ANDROID / "core-model/src/main/kotlin/coach/WeeklyBlueprintCodec.kt",
             ANDROID / "core-application/src/main/kotlin/coach/application/WeeklyAssessment.kt",
             ANDROID / "core-presentation/src/main/kotlin/coach/presentation/WeeklyAssessmentSession.kt"]

def declared_port_extensions(root):
    """Ports added after MSBX-v0 by an accepted later contract (14A, D-105), never by an unrecorded edit."""
    import yaml as _yaml
    path = root / "arch/14a_tutor_contract/tutor_contract.yaml"
    tutor = (_yaml.safe_load(path.read_text(encoding="utf-8")) or {}) if path.is_file() else {}
    ext = tutor.get("port_extension") or {}
    return [ext["port"]] if tutor.get("status") == "accepted_14a" and ext.get("decision") == "D-105" else []


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
    """The text of one function or block, from its signature to its balanced closing brace."""
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


def parens(source: str, signature: str) -> str:
    """The balanced parenthesised text that starts at the signature's first `(`: a list or a constructor."""
    at = source.find(signature)
    if at < 0:
        return ""
    open_at = source.find("(", at + len(signature) - 1 if signature.endswith("(") else at)
    depth = 0
    for index in range(open_at, len(source)):
        if source[index] == "(":
            depth += 1
        elif source[index] == ")":
            depth -= 1
            if depth == 0:
                return source[at:index + 1]
    return ""


def member(source: str, signature: str) -> str:
    """An expression-bodied member: from its signature to the next member at the same indentation."""
    at = source.find(signature)
    if at < 0:
        return ""
    indent = source.rfind("\n", 0, at) + 1
    prefix = source[indent:at]
    nxt = re.search(r"\n" + re.escape(prefix) + r"(fun |val |const |private |/\*\*|@)", source[at + len(signature):])
    return source[at:at + len(signature) + nxt.start()] if nxt else source[at:]


def section(text: str, start: str, end: str) -> str:
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    return text[a:b] if a >= 0 and b > a else ""


def code_block_lines(text: str) -> list[str]:
    blocks = re.findall(r"```text\n(.*?)```", text, re.S)
    return [line.strip() for line in blocks[0].splitlines() if line.strip()] if blocks else []


for path in (CONTRACT, SPEC, RESEARCH, FACTS_KT, COMMON_KT, CODEC_KT, ENGINE_KT, COMPOSER_KT, APP_KT, PRES_KT):
    check(f"E13B-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")
for path in OLD_FILES:
    check(f"E13B-00_moved_{path.name}", not path.exists(), f"{rel(path)} still exists beside its generic replacement")

contract = yaml.safe_load(read(CONTRACT)) or {}
weekly_contract = yaml.safe_load(read(WEEKLY_CONTRACT)) or {}
mca = read(MCA)
facts, weekly_facts, common = strip_comments(read(FACTS_KT)), strip_comments(read(WEEKLY_FACTS_KT)), strip_comments(read(COMMON_KT))
codec, item_kt = strip_comments(read(CODEC_KT)), strip_comments(read(ITEM_KT))
engine, weekly_engine, composer = strip_comments(read(ENGINE_KT)), strip_comments(read(WEEKLY_ENGINE_KT)), strip_comments(read(COMPOSER_KT))
app, build, pres, ports = strip_comments(read(APP_KT)), strip_comments(read(BUILD_KT)), strip_comments(read(PRES_KT)), strip_comments(read(PORTS_KT))
schema, migrations, pkg = strip_comments(read(SCHEMA_KT)), strip_comments(read(MIGRATIONS_KT)), strip_comments(read(PKG_KT))
spec_text, research_text = read(SPEC), read(RESEARCH)

# ---------------------------------------------------------------- contract head
check("E13B-01_model", contract.get("model") == "MCAX-v0", str(contract.get("model")))
check("E13B-01_status", contract.get("status") == "accepted_13b", str(contract.get("status")))
check("E13B-01_decision", contract.get("decision") == "D-100", str(contract.get("decision")))
check("E13B-01_semantics", contract.get("monthly_semantics") == "MCA-v0" and contract.get("extends") == "WBAX-v0", "semantics")
scope = contract.get("scope", {})
for key, expected in {"common_contract_generalised": True, "weekly_values_changed": False, "weekly_tests_still_pass": True,
                      "blueprint_composed_from_state": True, "items_chosen_after_slots": True, "slots_offered_to_planner": True,
                      "schema_changed": True, "migration_added": True, "interfaces_added": False, "boundaries_changed": False,
                      "planner_rule_changed": False, "gate_rule_changed": False, "mastery_rule_changed": False,
                      "transfer_producer_invented": False, "professional_checkpoint_producer_invented": False,
                      "diagnostic_waiver_implemented": False, "app_calls_composition": False}.items():
    check(f"E13B-01_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")
check("E13B-01_mca_one_architecture", "Bu ikinci bir assessment mimarisi değildir" in mca and "monthly_scope != stronger_numeric_weight" in mca,
      "MCA-v0 §2/§4 moved")

# ---------------------------------------------------------------- roles, read from MCA-v0 §5
mca_roles = code_block_lines(section(mca, "# 5. Monthly blueprint role family", "## `longitudinal_required_capability`"))
kotlin_roles = re.findall(r'^\s+([A-Z_]+)\("([a-z_]+)", "assessment\.monthly\.', facts, re.M)
roles = contract.get("roles", {})
check("E13B-02_roles_from_mca", len(mca_roles) == 8, f"mca={mca_roles}")
check("E13B-02_roles_equal_mca_in_order", [r[1] for r in kotlin_roles] == mca_roles, f"kotlin={[r[1] for r in kotlin_roles]}")
check("E13B-02_roles_equal_contract", roles.get("order") == mca_roles, "contract order")
check("E13B-02_roles_not_quota", "Bunlar fixed quota değildir." in mca and roles.get("quota") is False and roles.get("percentage_split") is False,
      "roles are quotas")
order_block = parens(facts, "val SELECTION_ORDER: List<MonthlyRole> = listOf(")
selection = re.findall(r"^\s+([A-Z_]+),", order_block, re.M)
by_const = {c: i for c, i in kotlin_roles}
check("E13B-02_selection_order", [by_const.get(c) for c in selection] == roles.get("selection_order") and len(selection) == 8,
      f"selection={selection}")
check("E13B-02_selection_starts_with_integrity", selection[:1] == ["PERSISTENT_WEAKNESS_OR_VERIFICATION"]
      and "1. unresolved integrity/verification needs," in section(mca, "# 7.", "# 8."), "§7 does not start with integrity/verification")
check("E13B-02_no_percentage_split", "%50 yeni konu + %30 eski konu + %20 English" in mca
      and not re.search(r"\d+\s*%|\bpercent|\bratio\b|\bweight", (facts + engine + composer).lower()), "a percentage or weight in monthly code")
role_rows = re.findall(r'\("([a-z_]+)", "assessment\.monthly\.[a-z_]+",\s*AssessmentIntent\.([A-Z_]+), TaskPurpose\.([A-Z]+), (RoleEvidenceKind\.[A-Z_]+|null)\)', facts)
check("E13B-02_role_rows_complete", len(role_rows) == 8, f"rows={len(role_rows)}")
check("E13B-02_intents", {r[0]: r[1].lower() for r in role_rows} == roles.get("intent"), str({r[0]: r[1] for r in role_rows}))
check("E13B-02_purposes", {r[0]: r[2].lower() for r in role_rows} == roles.get("purpose"), str({r[0]: r[2] for r in role_rows}))
check("E13B-02_retention_stays_retain", {r[0]: r[2] for r in role_rows}.get("delayed_retention_sampling") == "RETAIN"
      and "`retention_review_due` varsayılan olarak `retain` candidate üretir" in read(ROOT / "docs/DAILY_MICRO_ASSESSMENT_SPEC.md"), "retention planned as assess")
kinds = {r[0]: r[3].split(".")[-1].lower() for r in role_rows if r[3] != "null"}
check("E13B-02_evidence_kinds", kinds == roles.get("evidence_kind"), str(kinds))
check("E13B-02_weekly_feeds_no_list", "override val evidenceKind: RoleEvidenceKind? get() = null" in weekly_facts, "weekly roles feed longitudinal lists")
weekly_role_ids = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)", "assessment\.weekly\.', weekly_facts, re.M)
check("E13B-02_no_role_id_collision", not set(weekly_role_ids) & set(r[1] for r in kotlin_roles) and roles.get("role_ids_collide_with_weekly") is False,
      "a role id means two things")
check("E13B-02_checkpoint_not_gate", "`professional_evidence_checkpoint` yalnız evidence üretir; final certification değildir." in mca
      and roles.get("professional_checkpoint_is_gate") is False, "checkpoint is a gate")

# ---------------------------------------------------------------- reason codes, read from MCA-v0 §29
mca_codes = code_block_lines(section(mca, "# 29. Monthly-specific reason codes", "# 30."))
consts = dict(re.findall(r'const val ([A-Z_]+) = "(assessment\.monthly\.[a-z_]+)"', facts))
catalog_block = parens(facts, "val CATALOG: List<String> = listOf(")
catalog = [consts.get(token, token.strip('"')) for token in re.findall(r'^\s+("assessment\.monthly\.[a-z_]+"|[A-Z_]+),', catalog_block, re.M)]
check("E13B-03_codes_from_mca", len(mca_codes) == 21, f"mca={len(mca_codes)}")
check("E13B-03_catalog_equals_mca_in_order", catalog == mca_codes, f"kotlin={catalog}")
check("E13B-03_catalog_equals_contract", contract.get("reason_codes", {}).get("codes") == mca_codes, "contract codes")
role_codes = re.findall(r'"(assessment\.monthly\.slot_[a-z_]+)"', facts)
check("E13B-03_role_codes_in_catalog", len(set(role_codes)) == 8 and all(c in mca_codes for c in role_codes), str(role_codes))
check("E13B-03_not_in_planner_catalog", "assessment.monthly" not in read(ANDROID / "core-model/src/main/kotlin/coach/ReasonCodes.kt"),
      "monthly codes leaked into the closed planner catalogue")
overrides = dict(re.findall(r"override val (\w+) get\(\) = ([A-Z_.a-z]+)", body(facts, "object MonthlyReasonCodes")))
interface_members = re.findall(r"^\s+val (\w+): String$", body(common, "interface ScopeReasonCodes"), re.M)
check("E13B-03_every_scope_code_overridden", sorted(k for k in overrides if k != "catalog") == sorted(interface_members) and len(interface_members) == 12,
      f"overrides={sorted(overrides)}")
check("E13B-03_overrides_are_monthly", all(v in consts for k, v in overrides.items() if k != "catalog"),
      f"{ {k: v for k, v in overrides.items() if k != 'catalog' and v not in consts} }")
check("E13B-03_composer_writes_no_weekly_code", "WeeklyReasonCodes" not in composer and "MonthlyReasonCodes" not in composer,
      "the shared composer names a scope's codes directly")
check("E13B-03_scope_codes_bound", "AssessmentScope.MONTHLY_CAPABILITY -> MonthlyReasonCodes" in body(common, "fun codes(")
      and "AssessmentScope.WEEKLY_BLUEPRINT -> WeeklyReasonCodes" in body(common, "fun codes("), "scope codes not bound")

# ---------------------------------------------------------------- the common contract
blueprint_type = parens(common, "data class AssessmentBlueprint(")
check("E13B-04_blueprint_has_scope", "val scope: AssessmentScope" in blueprint_type, "the blueprint does not know its scope")
check("E13B-04_prior_session", "val priorSessionId: Long? = null" in blueprint_type, "no prior_monthly_result_ref")
init = body(common, "data class AssessmentBlueprint(")
check("E13B-04_mixed_unrepresentable", "require(slots.all { it.role in roles })" in init and "BlueprintScopes.roles(scope)" in init,
      "a blueprint can hold another scope's roles")
check("E13B-04_only_composed_scopes", "require(scope in BlueprintScopes.COMPOSED)" in init
      and "setOf(AssessmentScope.WEEKLY_BLUEPRINT, AssessmentScope.MONTHLY_CAPABILITY)" in common, "a daily blueprint")
check("E13B-04_prior_only_monthly", "require(scope == AssessmentScope.MONTHLY_CAPABILITY || priorSessionId == null)" in init, "weekly names a prior session")
check("E13B-04_one_skill_once_structural", "one Skill is measured once per blueprint" in read(COMMON_KT), "the type allows repeats")
for type_name in ("data class AssessmentBlueprint(", "data class AssessmentBlueprintSlot(", "data class AssessmentBlueprintResult(",
                  "data class BlueprintEvidenceFact(", "data class BlueprintSlotOutcome("):
    fields = re.findall(r"val (\w+):", parens(common, type_name))
    bad = [f for f in fields if re.search(r"score|grade|percent|pass(?!ed)|threshold|questioncount|deadline|countdown|weight|ready|readiness|verdict", f, re.I)]
    check(f"E13B-04_no_score_field_{type_name.split()[-1].rstrip('(')}", not bad and len(fields) > 0, f"fields={bad or fields[:3]}")
result_type = parens(common, "data class AssessmentBlueprintResult(")
for field in ("revalidatedCriticalSkills", "revalidatedRetentionSkills", "transferEvidenceObjectives", "integratedEvidenceObjectives"):
    check(f"E13B-04_result_{field}", f"val {field}: List<" in result_type, f"missing {field}")
mca_statuses = re.search(r"session_status: ([a-z |]+)", section(mca, "# 27. Monthly result contract", "# 28."))
statuses = re.findall(r'^\s+[A-Z]+\("([a-z]+)"\),', body(common, "enum class BlueprintSessionStatus"), re.M)
check("E13B-04_statuses_equal_mca", statuses == [s.strip() for s in mca_statuses.group(1).split("|")] if mca_statuses else False, str(statuses))
check("E13B-04_mca_no_overall", "`overall_mastery_score` zorunlu değildir" in mca, "§27 text moved")
scopes_obj = body(common, "object BlueprintScopes")
for line in ("AssessmentScope.MONTHLY_CAPABILITY -> MonthlyRole.SELECTION_ORDER", "AssessmentScope.MONTHLY_CAPABILITY -> MonthlyRole.entries",
             "AssessmentScope.MONTHLY_CAPABILITY -> MonthlyCycle.of(studyDay)", "AssessmentScope.WEEKLY_BLUEPRINT -> BlueprintRole.SELECTION_ORDER",
             "AssessmentScope.WEEKLY_BLUEPRINT -> BlueprintRole.entries", "AssessmentScope.WEEKLY_BLUEPRINT -> WeeklyCycle.of(studyDay)"):
    check(f"E13B-04_scopes_{line[16:60]}", line in scopes_obj, f"missing {line}")
check("E13B-04_blocks_in_selection_order", "BlueprintScopes.selectionOrder(scope).mapNotNull" in common, "blocks not in the scope's order")

# ---------------------------------------------------------------- cycle
cycle = body(facts, "object MonthlyCycle")
check("E13B-05_cycle_from_study_day", "LocalDate.parse(studyDay)" in cycle and "Instant" not in cycle, "cycle not from the study day")
check("E13B-05_cycle_calendar_month", "day.monthValue" in cycle and "padStart(2, '0')" in cycle and "IsoFields" not in cycle, "not a calendar month")
check("E13B-05_cycle_no_default_locale", "format(" not in cycle and "uppercase" not in cycle.lower(), "locale-sensitive formatting")
cyc = contract.get("cycle", {})
check("E13B-05_cycle_contract", cyc.get("definition") == "calendar_month_of_recorded_study_day" and cyc.get("product_default") is True
      and cyc.get("scientific_value") is False and cyc.get("stacking_possible") is False and cyc.get("prior_session_is_debt") is False
      and cyc.get("setting_owner") == "16D", str(cyc))
check("E13B-05_mca_no_debt", "30 gün geçti -> zorunlu eski sınav borcu" in mca and "eski monthly exam'lar üst üste birikmez" in mca, "§3 text moved")

# ---------------------------------------------------------------- pool
pool = body(engine, "fun targetPool(")
role_of = body(engine, "private fun roleOf(")
check("E13B-06_needs_from_planner", "PlannerEngine.needsFromSkillStates(states) + ownerNeeds" in pool, "the composer opens needs of its own")
check("E13B-06_one_skill_once", "BlueprintComposer.onePerSkill(MonthlyRole.SELECTION_ORDER, candidates, exclusions)" in pool
      and "kept.any { it.skill == entry.skill }" in body(composer, "fun onePerSkill("), "a Skill measured twice")
check("E13B-06_remediation_skill_excluded", "underRepair" in pool and "BlueprintExclusion.REMEDIATION_OPEN" in pool, "a Skill under remediation is measured")
check("E13B-06_new_learning_not_measured", "NeedTrigger.NEW_LEARNING -> Either.Excluded(BlueprintExclusion.NOT_TAUGHT_YET)" in role_of, "measured before teaching")
check("E13B-06_diagnostic_not_monthly", "NeedTrigger.DIAGNOSTIC_OPPORTUNITY, NeedTrigger.REINFORCEMENT_OPPORTUNITY ->\n                Either.Excluded(BlueprintExclusion.NOT_A_MONTHLY_MEASUREMENT)" in role_of,
      "diagnostic measured monthly")
check("E13B-06_critical_continue_needs_holding_back", "critical && skill in holdingBack -> Either.Role(MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION)" in role_of,
      "a critical Skill revalidated without a reason")
check("E13B-06_critical_is_prerequisite", "val critical = need.criticality == Criticality.CRITICAL_PREREQUISITE" in role_of, "critical means something else")
critical_uses = role_of.count("MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION")
check("E13B-06_critical_only_for_reasons", critical_uses == 3 and role_of.count("if (critical)") == 2, f"critical revalidation produced {critical_uses} ways")
check("E13B-06_mca_not_every_month", "Her critical Skill her ay otomatik test edilmez." in mca, "§11 moved")
check("E13B-06_persistent_from_state", "NeedTrigger.VERIFICATION_DUE, NeedTrigger.WEAKNESS_DETECTED ->" in role_of
      and "Tek bir zayıf item “persistent” değildir." in mca, "a persistent concern from something other than engine state")
check("E13B-06_longitudinal_required", "need.criticality !in REQUIRED -> Either.Excluded(BlueprintExclusion.NOT_REQUIRED_CAPABILITY)" in role_of
      and "setOf(Criticality.CRITICAL_PREREQUISITE, Criticality.REQUIRED)" in engine, "longitudinal samples non-required Skills")
check("E13B-06_window", "recentlyEvidenced == null || skill in recentlyEvidenced -> Either.Role(MonthlyRole.LONGITUDINAL_REQUIRED_CAPABILITY)" in role_of
      and "NOT_ACTIVE_SINCE_LAST_CYCLE" in role_of, "window not applied")
check("E13B-06_retention", "if (critical) Either.Role(MonthlyRole.CRITICAL_CAPABILITY_REVALIDATION)\n                else Either.Role(MonthlyRole.DELAYED_RETENTION_SAMPLING)" in role_of,
      "retention mapping")
check("E13B-06_english_track_only", "need.track == WeeklyBlueprintEngine.ENGLISH_TRACK" in role_of, "parallel role not English-only")
check("E13B-06_integration", "NeedTrigger.INTEGRATION_OPPORTUNITY -> Either.Role(MonthlyRole.INTEGRATED_APPLICATION)" in role_of, "integration mapping")
# Narrowed at 15G (`ACNX-v0` / `D-120`): the 13B contract left both roles without a producer, owned by 15; 15G built the transfer
# owner (TransferEngine, D-120) and the monthly engine maps its need to CROSS_TOPIC_TRANSFER — and nothing else. The
# checkpoint still has no producer anywhere.
check("E13B-06_no_invented_producer", "PROFESSIONAL_EVIDENCE_CHECKPOINT" not in engine
      and engine.count("CROSS_TOPIC_TRANSFER") == 1
      and "NeedTrigger.TRANSFER_OPPORTUNITY -> Either.Role(MonthlyRole.CROSS_TOPIC_TRANSFER)" in engine
      and set(roles.get("without_producer", {})) == {"cross_topic_transfer", "professional_evidence_checkpoint"}
      and all(v.get("owner") == 15 for v in roles.get("without_producer", {}).values()), "a producer invented or unowned")
# Narrowed at 15G (`ACNX-v0` / `D-120`): the one transfer trigger is 15G's declared extension (`transfer_opportunity`); no other.
_triggers = re.findall(r"^\s+([A-Z_]+)\(", body(read(ANDROID / "core-model/src/main/kotlin/coach/PlannerFacts.kt"), "enum class NeedTrigger"), re.M)
check("E13B-06_trigger_set_unchanged", [t for t in _triggers if "TRANSFER" in t] == ["TRANSFER_OPPORTUNITY"],
      "a transfer trigger invented")
check("E13B-06_band_is_planners", "PlannerEngine.band(need, scope, StarvationBucket.NONE)" in composer and "BlueprintComposer.entry(" in pool, "the month has its own band")
check("E13B-06_no_randomness", not re.search(r"\bRandom\b|shuffle|UUID", engine + composer + app), "randomness in composition")
exclusions = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\),', body(common, "enum class BlueprintExclusion"), re.M)
# Narrowed at 15G (`ACNX-v0` / `D-120`): 15G appended exactly one exclusion, `transfer_is_monthly`.
check("E13B-06_exclusions_equal_contract", exclusions == contract.get("pool", {}).get("exclusions") + ["transfer_is_monthly"], f"{exclusions}")
check("E13B-06_weekly_exclusions_first", exclusions[:5] == weekly_contract.get("pool", {}).get("exclusions"), "a weekly exclusion moved")

# ---------------------------------------------------------------- the shared composer
slot_for = body(composer, "private fun slotFor(")
check("E13B-07_indexed_by_scope", "entry.skill in it.targetSkills && scope in it.scopeEligibility && it.lifecycleStatus.selectable" in slot_for,
      "items indexed by a fixed scope")
check("E13B-07_fit_by_scope", "ItemSelection.fit(item, role.intent, scope, profiles, evaluatorAvailable)" in slot_for, "fit by a fixed scope")
check("E13B-07_role_declared", "role in it.blueprintRoles" in slot_for and "ROLE_NOT_DECLARED" in slot_for, "undeclared role filled")
check("E13B-07_gate_fails_closed", "decision == null || decision.eligibility.waits" in slot_for, "an unanswered gate lets the item through")
check("E13B-07_fresh", "item.variantFamilyId in exposedFamilies" in slot_for and "item.ref in seen" in slot_for, "a seen or solved item is fresh")
check("E13B-07_trust", "TRUSTED = setOf(LifecycleStatus.VALIDATED, LifecycleStatus.TRUSTED, LifecycleStatus.DEPRECATED)" in composer, "trust differs from the planner's")
check("E13B-07_bound", re.search(r"const val MAX_ITEMS_PER_SLOT_V0 = 5\b", composer) is not None
      and "const val MAX_ITEMS_PER_SLOT_V0 = BlueprintComposer.MAX_ITEMS_PER_SLOT_V0" in weekly_engine, "two bounds")
monthly_compose = body(engine, "fun compose(")
check("E13B-07_monthly_scope_passed", "AssessmentScope.MONTHLY_CAPABILITY, POLICY_VERSION" in monthly_compose and 'const val POLICY_VERSION = "MCA-v0"' in engine,
      "the monthly engine composes another scope")
check("E13B-07_weekly_scope_passed", "AssessmentScope.WEEKLY_BLUEPRINT, POLICY_VERSION" in body(weekly_engine, "fun compose(")
      and "priorSessionId = null" in body(weekly_engine, "fun compose("), "the weekly engine composes another scope")
for fn, needle in (("fun compose(", "val codes = BlueprintScopes.codes(scope)"), ("fun recompose(", "val codes = BlueprintScopes.codes(previous.scope)"),
                   ("fun result(", "val codes = BlueprintScopes.codes(blueprint.scope)"), ("private fun slotFor(", "val codes = BlueprintScopes.codes(scope)")):
    check(f"E13B-07_codes_of_scope_{fn[4:16].strip('( ')}", needle in body(composer, fn), f"{fn} does not speak its scope's codes")
cand = member(composer, "fun slotCandidates(")
check("E13B-07_candidate_same_need", "needKey = slot.needKey" in cand and "costMinutes = slot.expectedActiveMinutes!!" in cand
      and "atomicEvidenceBoundary = true" in cand, "a slot has a need, minutes or a cut of its own")
check("E13B-07_candidate_format", "BlueprintCodecs.format(blueprint.scope)" in cand, "generation names a fixed format")
check("E13B-07_candidate_id", 'AssessmentScope.MONTHLY_CAPABILITY -> "monthly:$cycleId:$slotId"' in composer
      and 'AssessmentScope.WEEKLY_BLUEPRINT -> "weekly:$cycleId:$slotId"' in composer, "candidate ids")
check("E13B-07_served_not_again", "filterNot { it.item in servedItems }" in cand, "a served slot is offered again")
check("E13B-07_engine_core_only", all(i.startswith(("coach.model.", "coach.engines.")) for i in re.findall(r"^import ([\w.]+)", read(ENGINE_KT) + read(COMPOSER_KT), re.M)),
      "an engine imports outside core")

# ---------------------------------------------------------------- result
result_body = body(composer, "fun result(")
check("E13B-08_clean_is_verified_independent", "usable(e) && e.evaluatorStatus == EvaluatorStatus.VERIFIED && e.independence == IndependenceClass.INDEPENDENT" in result_body,
      "clean not verified independent")
check("E13B-08_lists_from_clean", "o.evidence.filter { clean(it) && (!positiveOnly || it.outcome == EvidenceOutcome.POSITIVE) }" in result_body,
      "longitudinal lists from unclean evidence")
check("E13B-08_revalidation_positive_only", "cleanOf(RoleEvidenceKind.CRITICAL_REVALIDATION, positiveOnly = true)" in result_body
      and "cleanOf(RoleEvidenceKind.RETENTION_REVALIDATION, positiveOnly = true)" in result_body, "a negative revalidates")
check("E13B-08_lists_by_own_kind", "transferEvidenceObjectives = cleanOf(RoleEvidenceKind.TRANSFER," in result_body
      and "integratedEvidenceObjectives = cleanOf(RoleEvidenceKind.INTEGRATION," in result_body, "a list fed by another role")
check("E13B-08_kind_from_role", "slotOf[it.slotId]?.role?.evidenceKind == kind" in result_body, "kind not from the slot's role")
check("E13B-08_changes_only_reported", "stateChangeRefs = stateChangeRefs" in result_body, "state changes derived")
check("E13B-08_skip_not_completed", "submitted == true" in result_body and "completed.isEmpty() -> BlueprintSessionStatus.DEFERRED" in result_body,
      "a skip can count as completed")
check("E13B-08_policy_from_blueprint", "assessmentPolicyVersion = blueprint.policyVersion" in result_body, "policy not the blueprint's")
record = body(app, "fun record(")
check("E13B-08_no_monthly_weight", "scope" not in record and "monthly" not in record.lower() and "RecordEvidence(persistence, clock).record(" in record,
      "the evidence row learns its scope")
check("E13B-08_mca_hysteresis", "İlk clean monthly contradiction:" in mca and "instant unmastery değildir" in mca, "§24 moved")

# ---------------------------------------------------------------- application
use_case = body(app, "class ComposeAssessmentBlueprint(")
check("E13B-09_one_use_case", "private val scope: AssessmentScope" in use_case and "require(scope in BlueprintScopes.COMPOSED)" in use_case,
      "no scope-generic use case")
check("E13B-09_no_monthly_copy", "class ComposeMonthlyAssessment" not in app and "class ComposeWeeklyAssessment" not in app, "a second use case")
compose = body(app, "fun compose(evaluatorAvailable")
check("E13B-09_reads_own_scope", "persistence.latestAssessmentSession(scope)" in compose and "BlueprintScopes.cycleOf(scope, now.studyDay)" in compose,
      "composition reads another scope's row or cycle")
check("E13B-09_monthly_pool", "AssessmentScope.MONTHLY_CAPABILITY -> MonthlyBlueprintEngine.targetPool(" in compose, "the month reads another pool")
check("E13B-09_prior_session", "priorSessionId = previous?.id" in compose, "the prior month is not named")
check("E13B-09_same_cycle_writes_nothing", 0 <= compose.find("Composed.AlreadyComposed") < compose.find("append(blueprint)"), "the same month composed twice")
check("E13B-09_nothing_writes_nothing", 0 <= compose.find("Composed.NothingToMeasure") < compose.find("append(blueprint)"), "an empty blueprint is written")
check("E13B-09_watermark_first", 0 <= compose.find("truthWatermark()") < compose.find("PlanningStates.read("), "watermark read after state")
append = body(app, "private fun append(")
check("E13B-09_append", "persistence.inTransaction" in append and append.count("appendTruth") == 1 and '"scope" to scope.storedAs' in append
      and '"blueprint" to BlueprintCodecs.encode(blueprint)' in append, "the row does not carry its own scope and format")
recompose = body(app, "fun recompose(")
check("E13B-09_recompose_own_cycle", "blueprint.cycleId != BlueprintScopes.cycleOf(scope, now.studyDay)" in recompose
      and "persistence.latestAssessmentSession(scope)" in recompose and "append(recomposed)" in recompose, "a past or foreign cycle recomposed")
slots = body(app, "internal object BlueprintSlots")
check("E13B-09_slots_both_scopes", "BlueprintScopes.COMPOSED.sortedBy { it.ordinal }.flatMap" in slots, "only one scope's slots offered")
check("E13B-09_slots_this_cycle", "blueprint.cycleId != BlueprintScopes.cycleOf(scope, studyDay)" in slots and "it.needKey in open" in slots,
      "an earlier month or a closed need offered")
check("E13B-09_slots_monthly_engine", "AssessmentScope.MONTHLY_CAPABILITY -> MonthlyBlueprintEngine.slotCandidates(blueprint, served)" in slots, "monthly slots built by another engine")
check("E13B-09_build_daily_plan", "BlueprintSlots.candidates(persistence, now.studyDay, needs)" in build, "the planner is not offered the slots")
check("E13B-09_no_own_budget", not re.search(r"(monthly|weekly)\w*(Budget|Minutes)\s*=", build, re.I), "a monthly budget beside the planner's")
check("E13B-09_mca_capacity", "Monthly assessment daha geniş olabilir fakat **hiçbir günün hard capacity'sini otomatik aşmaz**." in mca, "§15 moved")
planning = contract.get("planning", {})
check("E13B-09_contract_planning", planning.get("own_queue") is False and planning.get("own_band") is False and planning.get("own_minutes") is False
      and planning.get("daily_budget_extended") is False and planning.get("tasks_per_need") == "at_most_one", str(planning))
check("E13B-09_planner_one_per_need", '"one_task_per_need" to (selected.map { it.needKey }.toSet().size == selected.size)' in
      read(ANDROID / "core-engines/src/main/kotlin/coach/engines/PlannerEngine.kt"), "the planner may pick two tasks for one need")

# ---------------------------------------------------------------- storage
# Narrowed at 13C (RVRX-v0 / D-101): version 4 is 13B's, and every later version must be owned by its step's contract.
schema_version = int((re.search(r"const val VERSION = (\d+)", schema) or re.search(r"(0)", "0")).group(1))
owned_versions = {m.get("to") for f in ROOT.glob("arch/*/*.yaml")
                  for m in [(yaml.safe_load(f.read_text(encoding="utf-8")) or {}).get("schema_migration") or {}] if isinstance(m, dict)}
check("E13B-10_schema_version_4", schema_version >= 4 and all(v in owned_versions for v in range(4, schema_version + 1)), "schema version")
v4 = body(schema, "val v4")
check("E13B-10_trigger_weekly", "(NEW.scope = 'weekly' AND (NEW.blueprint IS NULL OR substr(NEW.blueprint, 1, 17) <> 'weekly_blueprint/'))" in v4,
      "a weekly row without its own blueprint allowed")
check("E13B-10_trigger_monthly", "(NEW.scope = 'monthly' AND (NEW.blueprint IS NULL OR substr(NEW.blueprint, 1, 18) <> 'monthly_blueprint/'))" in v4,
      "a monthly row without its own blueprint allowed")
check("E13B-10_prefix_lengths", len("weekly_blueprint/") == 17 and len("monthly_blueprint/") == 18, "prefix length")
check("E13B-10_trigger_aborts", "BEFORE INSERT ON assessment_session" in v4 and "RAISE(ABORT" in v4, "the trigger does not refuse")
check("E13B-10_v3_unchanged", "ADD COLUMN blueprint TEXT CHECK (scope <> 'weekly' OR blueprint IS NOT NULL)" in body(schema, "val v3"), "v3 changed")
check("E13B-10_migration_step", "3 to Schema.v4" in migrations and "2 to Schema.v3" in migrations, "migration steps")
check("E13B-10_no_update_path", not re.search(r"UPDATE assessment_session", schema + strip_comments(read(ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"))),
      "an UPDATE path to sessions")
mig = contract.get("schema_migration", {})
check("E13B-10_migration_owned", mig.get("from") == 3 and mig.get("to") == 4 and mig.get("owner") == "13B" and mig.get("populated_fixture_tested") is True
      and mig.get("earlier_rows_touched") is False, str(mig))

# ---------------------------------------------------------------- codec
check("E13B-11_monthly_format", 'const val FORMAT = "monthly_blueprint/1"' in body(codec, "object MonthlyBlueprintCodec"), "monthly format")
check("E13B-11_weekly_format_unchanged", 'const val FORMAT = "weekly_blueprint/1"' in body(codec, "object WeeklyBlueprintCodec"), "weekly format changed")
check("E13B-11_monthly_with_prior", "BlueprintCodec.decode(stored, FORMAT, AssessmentScope.MONTHLY_CAPABILITY, withPrior = true)" in codec
      and "BlueprintCodec.encode(FORMAT, blueprint, withPrior = true)" in codec, "the monthly codec drops the prior session")
check("E13B-11_weekly_without_prior", "BlueprintCodec.decode(stored, FORMAT, AssessmentScope.WEEKLY_BLUEPRINT, withPrior = false)" in codec, "the weekly head changed")
check("E13B-11_encode_requires_scope", "require(blueprint.scope == AssessmentScope.MONTHLY_CAPABILITY)" in codec
      and "require(blueprint.scope == AssessmentScope.WEEKLY_BLUEPRINT)" in codec, "a codec writes another scope")
check("E13B-11_head_exact", 'if (h.keys != (HEAD + "reasons" + if (withPrior) listOf("prior_session") else emptyList()).toSet()) throw CodecText.Malformed()' in codec,
      "a head field of another format is read past")
check("E13B-11_roles_of_scope", "val roles = BlueprintScopes.roles(scope)" in codec and "roles.single { it.id == v(\"role\") }" in codec, "roles of any scope decoded")
check("E13B-11_strict", "runCatching { decodeOrThrow(stored, format, scope, withPrior) }.getOrNull()" in codec
      and "if (lines.firstOrNull() != format) throw CodecText.Malformed()" in codec, "the codec guesses")
dispatch = body(codec, "object BlueprintCodecs")
check("E13B-11_dispatch", "AssessmentScope.MONTHLY_CAPABILITY -> MonthlyBlueprintCodec.decode(stored)" in dispatch
      and "AssessmentScope.WEEKLY_BLUEPRINT -> WeeklyBlueprintCodec.decode(stored)" in dispatch and "AssessmentScope.DAILY_MICRO -> null" in dispatch,
      "a scope decoded with another codec")

# ---------------------------------------------------------------- ports and boundaries
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
msbx = yaml.safe_load(read(MSBX)) or {}
check("E13B-12_port_count", sorted(interfaces) == sorted([p["id"] for p in msbx["ports"]["set"]] + declared_port_extensions(ROOT)) and contract.get("port_count") == 4, f"interfaces={interfaces}")
check("E13B-12_no_monthly_port", "monthly" not in ports.lower() and contract.get("port_refinements") == [], "a monthly port method")

# ---------------------------------------------------------------- authored content
check("E13B-13_roles_type", "val blueprintRoles: Set<SlotRole> = emptySet()" in item_kt, "an item declares only one scope's roles")
reader = body(pkg, "fun item(section: Section)")
check("E13B-13_roles_any_scope", "BlueprintScopes.COMPOSED.firstOrNull { scope -> BlueprintScopes.roles(scope).any { it.id == raw } }" in reader,
      "monthly roles unknown to the package")
check("E13B-13_role_scope_consistent", 'scope.id !in list(section, "scope_eligibility") ->' in reader and "needs scope" in reader,
      "a role of an ineligible scope accepted")
check("E13B-13_unknown_refused", "unknown blueprint role" in reader, "an unknown role accepted")

# ---------------------------------------------------------------- the one interior
view = body(pres, "fun view(")
check("E13B-14_one_interior", "AssessmentSessionView(" in view and "scope = blueprint.scope" in view and "a intersect b" in view, "a separate monthly interior")
check("E13B-14_status_by_scope", "AssessmentScope.MONTHLY_CAPABILITY -> when (status)" in body(pres, "fun statusText(")
      and "BlueprintSessionStatus.DEFERRED -> MonthlyCopy.DEFERRED" in pres, "a month described as a week")
copy = body(pres, "object MonthlyCopy")
check("E13B-14_copy_not_verdict", not re.search(r"(?i)başarısız oldun|geçtin|kaldın|puan|%|final|hazırsın|yeterlisin", copy) and "borç" in copy,
      "the monthly copy judges")
check("E13B-14_checkpoint_copy", "CHECKPOINT_NOT_A_GATE" in copy and "kararı vermez" in copy, "the checkpoint copy is a verdict")
check("E13B-14_results_via_interior", "SessionResults.of(" in body(pres, "fun result("), "results bypass the interior")
check("E13B-14_asux_scope", "monthly_capability" in read(ROOT / "ux/8d_assessment_session/session.yaml"), "ASUX-v0 monthly scope moved")

# ---------------------------------------------------------------- weekly values unchanged (13A's accepted contract)
weekly_consts = dict(re.findall(r'const val ([A-Z_]+) = "(assessment\.weekly\.[a-z_]+)"', weekly_facts))
weekly_catalog = [weekly_consts.get(t, t.strip('"')) for t in re.findall(r'^\s+("assessment\.weekly\.[a-z_]+"|[A-Z_]+),',
                  parens(weekly_facts, "val CATALOG: List<String> = listOf("), re.M)]
check("E13B-15_weekly_codes_unchanged", weekly_catalog == weekly_contract.get("reason_codes", {}).get("codes"), "a weekly code changed")
check("E13B-15_weekly_roles_unchanged", weekly_role_ids == weekly_contract.get("roles", {}).get("order"), "a weekly role changed")
refusals = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\),', body(common, "enum class SlotItemRefusal"), re.M)
# Narrowed at 15G (`ACNX-v0` / `D-120`): 15G appended exactly one refusal, `transfer_claim_unsupported` (AIV-v0 §16).
check("E13B-15_weekly_refusals_unchanged", refusals == weekly_contract.get("item_selection", {}).get("refusals") + ["transfer_claim_unsupported"], str(refusals))
check("E13B-15_weekly_overrides", "override val evidenceRecorded get() = EVIDENCE_BUNDLE_RECORDED" in weekly_facts
      and "override val noExamDebt get() = NO_EXAM_DEBT" in weekly_facts, "weekly codes rebound")
weekly_validator = read(WEEKLY_VALIDATOR)
for needle in ("Narrowed at 13B", "AssessmentScope.WEEKLY_BLUEPRINT, POLICY_VERSION", "override val incompleteNotFailure get() = INCOMPLETE_NOT_FAILURE",
               "AssessmentScope.WEEKLY_BLUEPRINT -> WeeklyCycle.of(studyDay)", "override val noExamDebt get() = NO_EXAM_DEBT",
               "BlueprintCodec.decode(stored, FORMAT, AssessmentScope.WEEKLY_BLUEPRINT, withPrior = false)"):
    check(f"E13B-15_13a_gate_bound_{needle[:28]}", needle in weekly_validator, f"13A gate lost its weekly binding: {needle!r}")
check("E13B-15_13a_gate_ids_kept", len(set(re.findall(r'check\(f?"(E13A-\d\d_[a-z_0-9]+)', weekly_validator))) >= 120, "13A checks removed")
check("E13B-15_narrowing_recorded", len(contract.get("living_gates_narrowed", [])) == 12, "narrowing not recorded")

# ---------------------------------------------------------------- tests named
suites = contract.get("suites", {})
named_tests = {
    "model": ["the cycle is the calendar month of the recorded study day", "the eight roles are MCA-v0's, in its order, and selection follows section 7",
              "the monthly reason codes are MCA-v0 section 29, twenty-one and in order",
              "a blueprint holds only its own scope's roles, and only a month names a prior session", "a format never reads the other scope's text"],
    "engine": ["the pool comes from state, one role per Skill, in section 7 order", "a critical Skill is revalidated only for a reason, never because it is critical",
               # Narrowed at 15G (`ACNX-v0` / `D-120`): the test now also covers the transfer producer.
               "owner needs bring integration, transfer and the parallel track, the checkpoint has no producer",
               "slots take only items eligible for the monthly scope and declared for the monthly role",
               "revalidation lists name only clean independent positives of their own role",
               "a clean negative on a critical Skill revalidates nothing and is not a verdict"],
    "application": ["a month is composed once, stored in its own format, and asking again writes nothing",
                    "a new month composes fresh, names the prior session and carries no debt",
                    "the week and the month are composed independently and read back by their own scope",
                    "week and month slots for one need are alternatives, and the planner takes at most one",
                    "monthly slot evidence is recorded exactly like any other, contamination included"],
    "storage": ["migrating a populated schema-3 database installs the format rule and preserves every truth row",
                "a monthly session without a monthly blueprint is refused by the engine"],
    "presentation": ["the month runs in the same interior, blocked in its own selection order",
                     "a month's status is described in its own words, never judged"],
    "content": ["a monthly role is declared on an item the monthly scope can use, never on one it cannot"],
}
for suite, names in named_tests.items():
    text = read(ROOT / suites.get(suite, {}).get("file", "missing"))
    for name in names:
        check(f"E13B-16_{suite}_{name[:40]}", f"`{name}`" in text, f"missing test: {name}")

# ---------------------------------------------------------------- honesty
mutation = contract.get("mutation_results", {})
check("E13B-17_mutation_all_detected", mutation.get("detected") == mutation.get("total") == len(mutation.get("mutants", [])) >= 40,
      f"{mutation.get('detected')}/{mutation.get('total')}")
check("E13B-17_mutation_control", mutation.get("negative_control_result") == "survived_as_expected"
      and mutation.get("compile_failure_is_detection") is False and mutation.get("only_monthly_suites_run") is True
      and mutation.get("final_run_is_a_single_clean_run") is True, "harness honesty")
vm = contract.get("validator_mutation", {})
check("E13B-17_validator_mutation", vm.get("detected") == vm.get("total") and (vm.get("total") or 0) >= 20
      and vm.get("negative_control_result") == "no_false_positive", str(vm))
runs = {r["id"]: r for r in contract.get("verified_runs", [])}
check("E13B-17_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
device = contract.get("device_verification", {})
check("E13B-17_device_not_claimed", device.get("t6_run") is False and device.get("claimed") is False, "a device result is claimed")
loops = contract.get("open_loops", {})
for loop, owner in (("transfer_and_checkpoint_producers", "15"), ("monthly_cycle_setting", "16D"), ("distinct_day_longitudinal_evidence", "18D"),
                    ("retroactive_contamination", "13D"), ("diagnostic_waiver_and_s06", "13F"), ("calling_composition_from_the_app", "16D")):
    check(f"E13B-17_open_{loop[:30]}", str(loops.get(loop, {}).get("owner")) == owner, f"{loop} not owned")
forbidden = set(contract.get("forbidden_monthly_patterns", []))
for pattern in ("monthly_label_adds_weight", "overall_monthly_score_or_pass_mark", "readiness_or_domain_verdict", "cumulative_everything_exam",
                "fixed_question_count_duration_or_role_percentage", "critical_tested_for_being_critical", "persistent_concern_from_one_item",
                "checkpoint_as_readiness_gate", "weekly_only_item_in_monthly_slot", "monthly_queue_band_or_budget", "missed_month_as_debt",
                "negative_or_assisted_called_revalidation", "second_assessment_architecture", "weekly_value_changed_for_monthly",
                "stored_blueprint_edited"):
    check(f"E13B-18_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 13B QA PASS", "**Decision:** `D-100`", "A month is a wider window, not a heavier exam",
                 "No weekly value changed", "Not run: T6", "Mutation 48/48", "13C — Spaced repetition"]:
    check(f"E13B-19_spec_{fragment[:26]}", fragment in spec_text, f"spec: {fragment!r}")
for fragment in ["No web research pass was needed", "MCA-v0", "WBA-v0", "QAB-v0", "calendar month"]:
    check(f"E13B-20_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E13B-21_no_repeated_context_heading", len(context_headings) == len(set(context_headings)),
      f"repeated={[h for h in set(context_headings) if context_headings.count(h) > 1]}")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E13B-21_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)),
      f"repeated={sorted(h for h in set(plan_steps) if plan_steps.count(h) > 1)}")
combining = chr(0x0307)
check("E13B-21_no_combining_dot", all(combining not in read(p) for p in (SPEC, RESEARCH, CONTRACT, FACTS_KT, PRES_KT)), "U+0307 in 13B text")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "MCAX-v0", "stage_step": "13B", "decision": "D-100",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"13B_MONTHLY_ASSESSMENT_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
