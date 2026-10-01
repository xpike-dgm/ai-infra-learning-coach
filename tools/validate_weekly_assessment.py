"""Independent 13A QA — WBAX-v0 Weekly Blueprint Assessment Implementation.

The vocabularies are read out of the accepted contracts' own text — `WBA-v0` §7 roles, §27 statuses and §29
reason codes from `docs/WEEKLY_ASSESSMENT_SPEC.md`, `QAB-v0`'s selection order, `DDM-v0`'s session entity, 10D's
disclosure — and compared with the real Kotlin, never with 13A's own contract alone.

The checks that matter most are structural: a role used as a quota, an item that promotes itself, a seen or
solved item counted as fresh, a weekly queue or minute budget beside the planner's, a missed week turned into
debt, and a result that can hold a score must be **unrepresentable** as a PASS here.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/13a_weekly_assessment/weekly_assessment.yaml"
SPEC = ROOT / "docs/WEEKLY_ASSESSMENT_IMPL_SPEC.md"
RESEARCH = ROOT / "research/13a_weekly_assessment_research.md"
QA_OUT = ROOT / "arch/13a_weekly_assessment/qa_report.yaml"
WBA = ROOT / "docs/WEEKLY_ASSESSMENT_SPEC.md"
QAB = ROOT / "docs/QUESTION_BANK_SPEC.md"
DDM = ROOT / "docs/DOMAIN_DATA_MODEL_SPEC.md"
LDB = ROOT / "docs/LOCAL_DATABASE_SPEC.md"
ASUX_YAML = ROOT / "ux/8d_assessment_session/session.yaml"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/WeeklyAssessmentFacts.kt"
CODEC_KT = ANDROID / "core-model/src/main/kotlin/coach/WeeklyBlueprintCodec.kt"
ITEM_KT = ANDROID / "core-model/src/main/kotlin/coach/AssessmentFacts.kt"
ATTEMPT_KT = ANDROID / "core-model/src/main/kotlin/coach/AttemptFacts.kt"
ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/WeeklyBlueprintEngine.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/WeeklyAssessment.kt"
BUILD_KT = ANDROID / "core-application/src/main/kotlin/coach/application/BuildDailyPlan.kt"
SUBMIT_KT = ANDROID / "core-application/src/main/kotlin/coach/application/SubmitAttempt.kt"
PRES_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/WeeklyAssessmentSession.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
SQL_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
PKG_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/PackageFormat.kt"
SOURCE_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"

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
    """The balanced parenthesised text right after a signature: a `listOf(...)` or a data class's constructor.
    `body()` looks for braces, and a constructor-only data class or a list has none of its own."""
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


for path in (CONTRACT, SPEC, RESEARCH, FACTS_KT, CODEC_KT, ENGINE_KT, APP_KT, PRES_KT):
    check(f"E13A-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

contract = yaml.safe_load(read(CONTRACT)) or {}
wba, qab, ddm, ldb = read(WBA), read(QAB), read(DDM), read(LDB)
facts, codec, item_kt, attempt_kt = strip_comments(read(FACTS_KT)), strip_comments(read(CODEC_KT)), strip_comments(read(ITEM_KT)), strip_comments(read(ATTEMPT_KT))
engine, app, build = strip_comments(read(ENGINE_KT)), strip_comments(read(APP_KT)), strip_comments(read(BUILD_KT))
submit, pres, ports = strip_comments(read(SUBMIT_KT)), strip_comments(read(PRES_KT)), strip_comments(read(PORTS_KT))
schema, migrations, sql = strip_comments(read(SCHEMA_KT)), strip_comments(read(MIGRATIONS_KT)), strip_comments(read(SQL_KT))
pkg, source = strip_comments(read(PKG_KT)), strip_comments(read(SOURCE_KT))
spec_text, research_text = read(SPEC), read(RESEARCH)

# ---------------------------------------------------------------- contract head
check("E13A-01_model", contract.get("model") == "WBAX-v0", str(contract.get("model")))
check("E13A-01_status", contract.get("status") == "accepted_13a", str(contract.get("status")))
check("E13A-01_decision", contract.get("decision") == "D-098", str(contract.get("decision")))
check("E13A-01_semantics", contract.get("weekly_semantics") == "WBA-v0", str(contract.get("weekly_semantics")))
scope = contract.get("scope", {})
for key, expected in {"blueprint_composed_from_state": True, "items_chosen_after_slots": True, "slots_offered_to_planner": True,
                      "schema_changed": True, "migration_added": True, "interfaces_added": False, "boundaries_changed": False,
                      "planner_rule_changed": False, "gate_rule_changed": False, "mastery_rule_changed": False,
                      "monthly_composition": False, "diagnostic_waiver_implemented": False, "app_calls_composition": False}.items():
    check(f"E13A-01_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")

# ---------------------------------------------------------------- roles, read from WBA-v0 §7
wba_roles = code_block_lines(section(wba, "# 7. Blueprint role family", "## `recent_required_progress`"))
kotlin_roles = re.findall(r'^\s+([A-Z_]+)\("([a-z_]+)", "assessment\.weekly\.', facts, re.M)
check("E13A-02_roles_from_wba", len(wba_roles) == 6, f"wba={wba_roles}")
check("E13A-02_roles_equal_wba_in_order", [r[1] for r in kotlin_roles] == wba_roles, f"kotlin={[r[1] for r in kotlin_roles]}")
check("E13A-02_roles_equal_contract", contract.get("roles", {}).get("order") == wba_roles, "contract order")
check("E13A-02_roles_not_quota", "**quota değildir**" in wba and contract.get("roles", {}).get("quota") is False, "roles are quotas")
order_block = parens(facts, "val SELECTION_ORDER: List<BlueprintRole> = listOf(")
selection = re.findall(r"^\s+([A-Z_]+),", order_block, re.M)
by_const = {c: i for c, i in kotlin_roles}
check("E13A-02_selection_order", [by_const.get(c) for c in selection] == contract.get("roles", {}).get("selection_order"),
      f"selection={selection}")
check("E13A-02_selection_starts_with_repair", selection[:1] == ["WEAKNESS_OR_VERIFICATION"] and "P0/P1 integrity/verification/repair needs" in wba,
      "§9 does not start with integrity/verification/repair")
intent_by_role = dict(re.findall(r'\("([a-z_]+)", "assessment\.weekly\.[a-z_]+",\s*AssessmentIntent\.([A-Z_]+), TaskPurpose\.[A-Z]+\)', facts))
purpose_by_role = dict(re.findall(r'\("([a-z_]+)", "assessment\.weekly\.[a-z_]+",\s*AssessmentIntent\.[A-Z_]+, TaskPurpose\.([A-Z]+)\)', facts))
check("E13A-02_intents", {k: v.lower() for k, v in intent_by_role.items()} == contract.get("roles", {}).get("intent"), str(intent_by_role))
check("E13A-02_retention_stays_retain", purpose_by_role.get("retention_due") == "RETAIN" and
      "`retention_review_due` varsayılan olarak `retain` candidate üretir" in read(ROOT / "docs/DAILY_MICRO_ASSESSMENT_SPEC.md"),
      str(purpose_by_role))
check("E13A-02_others_assess", all(v == "ASSESS" for k, v in purpose_by_role.items() if k != "retention_due") and len(purpose_by_role) == 6,
      str(purpose_by_role))

# ---------------------------------------------------------------- reason codes, read from WBA-v0 §29
wba_codes = code_block_lines(section(wba, "# 29. Weekly-specific reason codes", "# 30."))
consts = dict(re.findall(r'const val ([A-Z_]+) = "(assessment\.weekly\.[a-z_]+)"', facts))
catalog_block = parens(facts, "val CATALOG: List<String> = listOf(")
catalog = [consts.get(token, token.strip('"')) for token in re.findall(r'^\s+("assessment\.weekly\.[a-z_]+"|[A-Z_]+),', catalog_block, re.M)]
check("E13A-03_codes_from_wba", len(wba_codes) == 19, f"wba={len(wba_codes)}")
check("E13A-03_catalog_equals_wba_in_order", catalog == wba_codes, f"kotlin={catalog}")
check("E13A-03_catalog_equals_contract", contract.get("reason_codes", {}).get("codes") == wba_codes, "contract codes")
check("E13A-03_role_codes_in_catalog", all(f"assessment.weekly.slot_" in c for c in
      re.findall(r'"(assessment\.weekly\.slot_[a-z_]+)"', facts)) and len(re.findall(r'"(assessment\.weekly\.slot_[a-z_]+)"', facts)) >= 6,
      "role codes")
reason_catalog = read(ANDROID / "core-model/src/main/kotlin/coach/ReasonCodes.kt")
check("E13A-03_not_in_planner_catalog", "assessment.weekly" not in reason_catalog, "weekly codes leaked into the closed planner catalogue")

# ---------------------------------------------------------------- statuses and the result contract (§27)
status_line = re.search(r"session_status: ([a-z |]+)", section(wba, "# 27. Weekly result contract", "# 28."))
wba_statuses = [s.strip() for s in status_line.group(1).split("|")] if status_line else []
kotlin_statuses = re.findall(r'^\s+[A-Z]+\("([a-z]+)"\),', body(facts, "enum class WeeklySessionStatus"), re.M)
check("E13A-04_statuses_equal_wba", kotlin_statuses == wba_statuses == ["complete", "partial", "deferred", "invalidated"], f"{kotlin_statuses} vs {wba_statuses}")
check("E13A-04_no_overall_score_in_wba", "`overall_mastery_score` zorunluluğu taşımaz" in wba, "§27 text moved")
for type_name in ("data class WeeklyAssessmentBlueprint", "data class AssessmentBlueprintSlot", "data class WeeklyAssessmentResult",
                  "data class WeeklyEvidenceFact", "data class WeeklySlotOutcome"):
    fields = re.findall(r"val (\w+):", parens(facts, type_name))
    bad = [f for f in fields if re.search(r"score|grade|percent|pass(?!ed)|threshold|questioncount|deadline|countdown", f, re.I)]
    check(f"E13A-04_no_score_field_{type_name.split()[-1]}", not bad and len(fields) > 0, f"fields={bad or fields[:3]}")
result_body = body(engine, "fun result(")
check("E13A-04_skip_not_completed", "submitted == true" in result_body, "a skip can count as completed")
check("E13A-04_status_from_submission", "completed.isEmpty() -> WeeklySessionStatus.DEFERRED" in result_body
      and "unresolved.isEmpty() -> WeeklySessionStatus.COMPLETE" in result_body, "status not from submission")
check("E13A-04_clean_needs_verified_independent", "e.evaluatorStatus == EvaluatorStatus.VERIFIED && e.independence == IndependenceClass.INDEPENDENT" in result_body,
      "positive/negative not restricted to verified independent evidence")
check("E13A-04_contaminated_unusable", "!e.prerequisiteContaminated" in result_body, "contaminated evidence usable")
check("E13A-04_incomplete_not_failure_code", "WeeklyReasonCodes.INCOMPLETE_NOT_FAILURE" in result_body, "incomplete not said")
check("E13A-04_changes_only_reported", "stateChangeRefs = stateChangeRefs" in result_body, "state changes derived")

# ---------------------------------------------------------------- cycle
cycle = body(facts, "object WeeklyCycle")
check("E13A-05_cycle_iso_week_based_year", "IsoFields.WEEK_BASED_YEAR" in cycle and "IsoFields.WEEK_OF_WEEK_BASED_YEAR" in cycle, "not the ISO week")
check("E13A-05_cycle_from_study_day", "LocalDate.parse(studyDay)" in cycle and "Instant" not in cycle, "cycle not from the study day")
check("E13A-05_cycle_no_default_locale", "format(" not in cycle and "uppercase" not in cycle.lower(), "locale-sensitive formatting")
cyc = contract.get("cycle", {})
check("E13A-05_cycle_contract", cyc.get("definition") == "iso_8601_week_of_recorded_study_day" and cyc.get("product_default") is True
      and cyc.get("user_confirmation") == "confirmed_by_user"
      and cyc.get("scientific_value") is False and cyc.get("stacking_possible") is False, str(cyc))
check("E13A-05_wba_no_debt", "7 gün geçti -> kaçırılan sınav borcu oluştu" in wba and "fresh weekly blueprint" in wba, "§3 text moved")

# ---------------------------------------------------------------- pool
pool = body(engine, "fun targetPool(")
role_of = body(engine, "private fun roleOf(")
check("E13A-06_needs_from_planner", "PlannerEngine.needsFromSkillStates(states) + ownerNeeds" in pool, "the composer opens needs of its own")
check("E13A-06_one_skill_once", "kept.any { it.skill == entry.skill }" in pool and "MEASURED_IN_ANOTHER_ROLE" in pool, "a Skill measured twice")
check("E13A-06_remediation_skill_excluded", "underRepair" in pool and "REMEDIATION_OPEN" in pool, "a Skill under remediation is measured")
check("E13A-06_new_learning_not_measured", "NeedTrigger.NEW_LEARNING -> Either.Excluded(WeeklyExclusion.NOT_TAUGHT_YET)" in role_of,
      "measured before teaching")
check("E13A-06_dma_no_assessment_before_teaching", "Bir Objective henüz anlatılmadıysa normal mastery assessment yapılmaz" in read(ROOT / "docs/DAILY_MICRO_ASSESSMENT_SPEC.md"),
      "DMA-v0 §5 moved")
check("E13A-06_recency", "recentlyEvidenced == null || skill in recentlyEvidenced" in role_of and "NOT_ACTIVE_SINCE_LAST_CYCLE" in role_of,
      "recency not applied")
check("E13A-06_wba_recency_source", "son cycle'dan beri meaningful teaching/practice/evidence ile ilerleyen required Objectives" in wba, "§8 moved")
check("E13A-06_critical_needs_holding_back", "Criticality.CRITICAL_PREREQUISITE && skill in holdingBack" in role_of, "critical confidence without a blocker")
check("E13A-06_english_track_only", "need.track == ENGLISH_TRACK" in role_of and 'ENGLISH_TRACK = "technical_english"' in engine, "parallel role not English-only")
check("E13A-06_diagnostic_not_weekly", "NeedTrigger.DIAGNOSTIC_OPPORTUNITY, NeedTrigger.REINFORCEMENT_OPPORTUNITY" in role_of, "diagnostic measured weekly")
check("E13A-06_band_is_planners", "PlannerEngine.band(need, scope, StarvationBucket.NONE)" in engine, "the week has its own band")
check("E13A-06_no_randomness", not re.search(r"\bRandom\b|shuffle|UUID", engine + app), "randomness in composition")
exclusions = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\),', body(facts, "enum class WeeklyExclusion"), re.M)
check("E13A-06_exclusions_equal_contract", exclusions == contract.get("pool", {}).get("exclusions"), f"{exclusions}")
holding = body(app, "private fun holdingBack(")
check("E13A-06_holding_back_from_trace", "PlanTraceCodec::decode" in holding and "BLOCKED_PREREQUISITE" in holding and "relatedSkills" in holding,
      "holding back not read from the planner's trace")
check("E13A-06_gate_not_rerun_for_pool", "resolve(" not in holding, "the gate is re-run to find blockers")

# ---------------------------------------------------------------- item selection (QAB-v0 §31–§33)
slot_for = body(engine, "private fun slotFor(")
check("E13A-07_qab_order_text", "6. user exposure / solution exposure" in qab and "Selector önce narrow indexed candidate set üretir" in qab, "QAB-v0 §31/§33 moved")
indexed_at = slot_for.find("AssessmentScope.WEEKLY_BLUEPRINT in it.scopeEligibility")
bound_at = slot_for.find("take(MAX_ITEMS_PER_SLOT_V0)")
exposure_at = slot_for.find("exposedFamilies")
check("E13A-07_bound_after_index_before_learner", 0 <= indexed_at < bound_at and bound_at >= 0 and slot_for.find("SOLUTION_EXPOSED") > bound_at,
      "bound not between indexed facets and per-learner filters")
check("E13A-07_bound_value", re.search(r"const val MAX_ITEMS_PER_SLOT_V0 = 5\b", engine) is not None
      and contract.get("item_selection", {}).get("bound_owner") == "18E", "bound changed or unowned")
check("E13A-07_role_declared", "role in it.blueprintRoles" in slot_for and "ROLE_NOT_DECLARED" in slot_for, "undeclared role filled")
check("E13A-07_fit_weekly_intent", "ItemSelection.fit(item, role.intent, AssessmentScope.WEEKLY_BLUEPRINT, profiles, evaluatorAvailable)" in slot_for,
      "fit not checked for the role's intent in weekly scope")
check("E13A-07_gate_fails_closed", "decision == null || decision.eligibility.waits" in slot_for, "an unanswered gate lets the item through")
check("E13A-07_solution_exposed_family", "item.variantFamilyId in exposedFamilies" in slot_for and "item.ref in exposedItems" in slot_for, "solved family fresh")
check("E13A-07_seen_not_fresh", "item.ref in seen" in slot_for and "ALREADY_SEEN" in slot_for, "a seen item is fresh")
check("E13A-07_family_once", "item.variantFamilyId in usedFamilies" in slot_for, "near variants measure two slots")
check("E13A-07_group_once", "it in usedGroups" in slot_for, "a testlet measures two slots")
check("E13A-07_minutes_not_invented", "item.expectedActiveMinutes == null" in slot_for and "EXPECTED_MINUTES_MISSING" in slot_for, "minutes invented")
check("E13A-07_high_stakes_trust", "TRUSTED = setOf(LifecycleStatus.VALIDATED, LifecycleStatus.TRUSTED, LifecycleStatus.DEPRECATED)" in engine,
      "planned slot trust differs from the planner's")
refusals = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\),', body(facts, "enum class WeeklyItemRefusal"), re.M)
check("E13A-07_refusals_equal_contract", refusals == contract.get("item_selection", {}).get("refusals"), str(refusals))
order = parens(engine, "private val itemOrder: Comparator<AssessmentItem> = compareBy<AssessmentItem>(")
check("E13A-07_not_shortest_first", "expectedActiveMinutes" not in order and "LifecycleStatus.TRUSTED" in order, "the shortest item first")
check("E13A-07_closure_p0_p1", "requiredForSessionClosure = entry.band == PriorityBand.P0 || entry.band == PriorityBand.P1" in slot_for,
      "closure not limited to integrity/verification/repair")
check("E13A-07_h0", "val independenceMode: IndependenceMode get() = IndependenceMode.H0_REQUIRED" in facts, "slots not h0_required")
check("E13A-07_wba_h0", "independence_mode = h0_required" in wba, "§19 moved")
check("E13A-07_trust_from_store", "persistence.latestValidation(item.ref)?.status ?: LifecycleStatus.CANDIDATE" in app
      and "persistence.resourceVersion(item.ref) ?: return null" in app, "trust from the item's own claim")
blueprint_init = body(facts, "data class WeeklyAssessmentBlueprint")
check("E13A-07_structural_once", "one Skill is measured once per blueprint" in read(FACTS_KT) and "variant family measures one slot" in read(FACTS_KT),
      "the blueprint type allows repeats")

# ---------------------------------------------------------------- planning
cand = member(engine, "fun slotCandidates(")
check("E13A-08_same_need", "needKey = slot.needKey" in cand, "a slot has a need of its own")
check("E13A-08_purpose_from_role", "purpose = slot.role.purpose" in cand, "purpose not from the role")
check("E13A-08_atomic", "atomicEvidenceBoundary = true" in cand and "splittable = true" not in cand, "a slot can be cut")
check("E13A-08_minutes_from_item", "costMinutes = slot.expectedActiveMinutes!!" in cand, "minutes invented for a slot")
check("E13A-08_trust_from_item", "validationStatus = slot.itemLifecycle!!" in cand, "trust invented for a slot")
check("E13A-08_served_not_again", "filterNot { it.item in servedItems }" in cand, "a served slot is offered again")
weekly_slots = body(app, "fun candidates(persistence: PersistencePort")
check("E13A-08_only_this_week", "blueprint.cycleId != WeeklyCycle.of(studyDay)" in weekly_slots, "an earlier week is offered")
check("E13A-08_only_open_needs", "it.needKey in open" in weekly_slots, "slots offered for needs that are not open")
check("E13A-08_build_daily_plan", "WeeklySlots.candidates(persistence, now.studyDay, needs)" in build, "the planner is not offered the slots")
check("E13A-08_no_own_budget", not re.search(r"weekly\w*(Budget|Minutes)\s*=", build), "a weekly budget beside the planner's")
check("E13A-08_wba_no_extra_time", "daily hard budget'ın dışında ek süre yaratmaz" in wba, "§15 moved")
planning = contract.get("planning", {})
check("E13A-08_contract", planning.get("own_queue") is False and planning.get("own_band") is False and planning.get("own_minutes") is False
      and planning.get("daily_budget_extended") is False, str(planning))

# ---------------------------------------------------------------- composing once, writing honestly
compose = body(app, "fun compose(evaluatorAvailable")
already_at = compose.find("Composed.AlreadyComposed")
nothing_at = compose.find("Composed.NothingToMeasure")
append_at = compose.find("append(blueprint)")
check("E13A-09_watermark_first", 0 <= compose.find("truthWatermark()") < compose.find("publishedSkills()"), "watermark read after state")
check("E13A-09_same_week_writes_nothing", 0 <= already_at < append_at, "the same week is composed twice")
check("E13A-09_nothing_to_measure_writes_nothing", 0 <= nothing_at < append_at and "readySlots.isEmpty()" in compose, "an empty blueprint is written")
check("E13A-09_unreadable_refused", "Composed.Refused(\"the last weekly blueprint cannot be read\")" in compose, "an unreadable blueprint is guessed around")
check("E13A-09_no_debt_code", "WeeklyReasonCodes.NO_EXAM_DEBT" in body(engine, "fun compose(") and "previousCycleId != cycleId" in body(engine, "fun compose("),
      "a missed week is not said to leave no debt")
append = body(app, "private fun append(")
check("E13A-09_one_transaction", "persistence.inTransaction" in append and append.count("appendTruth") == 1, "a blueprint is not one row in one transaction")
check("E13A-09_scope_stored", '"scope" to AssessmentScope.WEEKLY_BLUEPRINT.storedAs' in append and '"blueprint" to WeeklyBlueprintCodec.encode(blueprint)' in append,
      "the session row does not carry its scope and blueprint")
recompose = body(app, "fun recompose(")
check("E13A-09_recompose_this_week_only", "a past week is not recomposed" in recompose, "a past week is recomposed")
check("E13A-09_recompose_appends", "append(recomposed)" in recompose and "UPDATE" not in recompose, "a recomposition edits")
engine_recompose = body(engine, "fun recompose(")
check("E13A-09_recompose_keeps_others", "if (slot.slotId !in slotIds) return@map slot" in engine_recompose, "a kept slot changes")
check("E13A-09_recompose_new_family", "replaced.mapNotNull { it.variantFamilyId }" in engine_recompose and "filterNot { it.ref in retired }" in engine_recompose,
      "a replaced item or family is reused")
check("E13A-09_asux_five_conditions", len(re.findall(r'^\s+[A-Z_]+\("[a-z_]+"\),', body(strip_comments(read(ANDROID / "core-presentation/src/main/kotlin/coach/presentation/AssessmentSession.kt")),
      "enum class RecompositionCondition"), re.M)) == 5, "the interior's five conditions moved")

# ---------------------------------------------------------------- contamination (§25)
contam = body(engine, "fun contaminatedBy(")
failed = body(engine, "fun cleanlyFailedSkills(")
check("E13A-10_requires_failed_skill", "slot.itemRequiredSkills.any { it in cleanlyFailedSkills }" in contam, "contamination not from required Skills")
check("E13A-10_clean_failure_only", "EvaluatorStatus.VERIFIED" in failed and "IndependenceClass.INDEPENDENT" in failed and "!it.prerequisiteContaminated" in failed,
      "an assisted or provisional failure is a root failure")
record = body(app, "fun record(")
check("E13A-10_snapshot_contaminated", "PrerequisiteSnapshot.CONTAMINATED" in record and "RecordEvidence(persistence, clock).record(" in record,
      "contamination not recorded through the evidence pipeline")
check("E13A-10_wba_root_cause", "downstream target'lara kör şekilde üç ayrı negative evidence yazılmaz" in wba, "§25 moved")
check("E13A-10_forward_only_disclosed", contract.get("result", {}).get("contamination") == "forward_only"
      and contract.get("result", {}).get("retroactive_contamination_owner") == "13D", "retroactive limitation not disclosed")

# ---------------------------------------------------------------- storage
check("E13A-11_schema_version_3", "const val VERSION = 3" in schema, "schema version")
v3 = body(schema, "val v3")
check("E13A-11_blueprint_column_check", "ADD COLUMN blueprint TEXT CHECK (scope <> 'weekly' OR blueprint IS NOT NULL)" in v3, "weekly row without blueprint allowed")
check("E13A-11_migration_step", "2 to Schema.v3" in migrations and "1 to Schema.v2" in migrations and "0 to Schema.v1" in migrations, "migration steps")
check("E13A-11_session_append_only", '"assessment_session"' in body(schema, "val truthTables"), "the session is not append-only truth")
check("E13A-11_no_update_path", not re.search(r"UPDATE assessment_session", sql + schema), "an UPDATE path to sessions")
check("E13A-11_ddm_entity", "one session, its blocks and boundaries" in ddm, "DDM-v0 session entity moved")
check("E13A-11_ten_d_disclosure", "`assessment_session.scope` (13)" in ldb, "10D's disclosure moved")
check("E13A-11_scope_values", re.search(r"CHECK \(scope IN \('daily', 'weekly', 'monthly'\)\)", read(SCHEMA_KT)) is not None
      and 'WEEKLY_BLUEPRINT("weekly_blueprint", "weekly")' in item_kt, "stored scope vocabulary drifted")
mig = contract.get("schema_migration", {})
check("E13A-11_migration_owned", mig.get("from") == 2 and mig.get("to") == 3 and mig.get("owner") == "13A" and mig.get("populated_fixture_tested") is True, str(mig))
check("E13A-11_codec_format", 'const val FORMAT = "weekly_blueprint/1"' in codec, "codec format")
check("E13A-11_codec_strict", "runCatching { decodeOrThrow(stored) }.getOrNull()" in codec and "if (key in seen) throw Malformed()" in codec
      and "if (lines.firstOrNull() != FORMAT) throw CodecText.Malformed()" in codec, "the codec guesses")
latest = body(sql, "override fun latestAssessmentSession(")
check("E13A-11_latest_by_sequence", "ORDER BY sequence DESC LIMIT 1" in latest and "scope.storedAs" in latest, "latest session not by sequence")
exposure = body(sql, "override fun exposuresFor(")
check("E13A-11_exposure_pinned", "resource_logical_id = ? AND resource_version = ?" in exposure and "DELETE" not in exposure, "exposure not pinned")
since = body(sql, "override fun skillsEvidencedSince(")
check("E13A-11_recency_by_study_day", "occurred_on_study_day >= ?" in since and "instant" not in since, "recency by instant")

# ---------------------------------------------------------------- ports and boundaries
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
msbx = yaml.safe_load(read(MSBX)) or {}
check("E13A-12_port_count", sorted(interfaces) == sorted(p["id"] for p in msbx["ports"]["set"]), f"interfaces={interfaces}")
for signature in ("fun latestAssessmentSession(scope: AssessmentScope): StoredTruth?",
                  "fun exposuresFor(resources: List<VersionedRef>, variantFamilies: List<String>): List<ExposureFact>",
                  "fun skillsEvidencedSince(studyDay: String): List<VersionedRef>",
                  "fun assessmentItemsFor(skill: VersionedRef): List<AssessmentItem>"):
    check(f"E13A-12_port_{signature[4:30]}", signature in ports, f"missing {signature}")
check("E13A-12_no_platform_types", not re.search(r"\bandroid\.|java\.io|java\.sql|SQLite", ports), "a platform type in a port")
engine_imports = re.findall(r"^import ([\w.]+)", read(ENGINE_KT), re.M)
check("E13A-12_engine_core_only", all(i.startswith("coach.model.") for i in engine_imports), f"imports={[i for i in engine_imports if not i.startswith('coach.model.')]}")
check("E13A-12_attempt_names_session", 'put("assessment_session_id", it.toString())' in submit and "val assessmentSessionId: Long? = null" in attempt_kt,
      "an attempt cannot name its session")

# ---------------------------------------------------------------- authored content
item_keys = body(pkg, "private val KNOWN_KEYS")
check("E13A-13_keys_known", '"expected_active_minutes", "blueprint_roles"' in item_keys, "keys not known")
item_reader = body(pkg, "fun item(section: Section)")
check("E13A-13_keys_optional", 'optional(section, "expected_active_minutes")' in item_reader and 'text(section.values, "expected_active_minutes"' not in item_reader,
      "minutes made required")
check("E13A-13_bad_minutes_refused", "is not a positive number" in item_reader and "unknown blueprint role" in item_reader, "bad values accepted")
check("E13A-13_item_fields", "val expectedActiveMinutes: Int? = null" in item_kt and "val blueprintRoles: Set<BlueprintRole> = emptySet()" in item_kt,
      "item fields default to invented values")
check("E13A-13_qab_fields", "expected_active_minutes" in section(qab, "# 8. Canonical AssessmentResource contract", "# 9.")
      and "blueprint_role_eligibility" in qab, "QAB-v0 fields moved")
check("E13A-13_content_source", "override fun assessmentItemsFor(skill: VersionedRef)" in source and "skill in it.targetSkills" in source, "content source")

# ---------------------------------------------------------------- the one interior
view = body(pres, "fun view(")
check("E13A-14_one_interior", "AssessmentSessionView(" in view and "AssessmentScope.WEEKLY_BLUEPRINT" in view, "a separate weekly interior")
check("E13A-14_tools_intersection", "a intersect b" in view, "the session discloses a tool an item forbids")
check("E13A-14_h0", "IndependenceMode.H0_REQUIRED" in view, "independence not disclosed")
check("E13A-14_results_via_interior", "SessionResults.of(" in body(pres, "fun result("), "results bypass the interior")
check("E13A-14_asux_scope", "weekly_blueprint" in read(ASUX_YAML), "ASUX-v0 weekly scope moved")
copy = body(pres, "object WeeklyCopy")
check("E13A-14_copy_not_verdict", not re.search(r"(?i)başarısız oldun|geçtin|kaldın|puan|%", copy) and "borç" in copy, "the copy judges")

# ---------------------------------------------------------------- tests named
suites = contract.get("suites", {})
named_tests = {
    "model": ["the cycle is the ISO week of the recorded study day", "the codec refuses rather than guesses",
              "nothing in the blueprint or the result can hold a score"],
    "engine": ["the pool comes from state, one role per Skill, in section 9 order", "the gate fails closed and a waiting item never measures",
               "a seen item or a solved family is never a fresh measurement", "near variants and one dependency group never measure two slots",
               "a week that passed leaves nothing behind", "the result is evidence by Objective, and a skip is not incorrect",
               "a root prerequisite shown missing in the session contaminates what depends on it and nothing else"],
    "application": ["a week is composed once, and asking again writes nothing", "a new week composes fresh from current state and carries nothing over",
                    "trust is the store's validation record, not what the item claims",
                    "the planner is offered this week's unserved slots for needs it already opened"],
    "storage": ["migrating a populated schema-2 database completes the session table and preserves every truth row",
                "a weekly session without its blueprint is refused by the engine"],
    "presentation": ["the session never discloses a tool one of its items forbids"],
}
for suite, names in named_tests.items():
    text = read(ROOT / suites.get(suite, {}).get("file", "missing"))
    for name in names:
        check(f"E13A-15_{suite}_{name[:40]}", f"`{name}`" in text, f"missing test: {name}")

# ---------------------------------------------------------------- narrowed living gates
for validator in ("validate_planner_engine.py", "validate_prerequisite_engine.py", "validate_reason_codes.py",
                  "validate_replan.py", "validate_virtual_user_tests.py"):
    text = read(ROOT / "tools" / validator)
    check(f"E13A-16_narrowed_{validator[9:30]}", "schema_versions_owned(" in text and 'VERSION = 2" in' not in text,
          "the gate still pins version 2 or lost its guarantee")
check("E13A-16_watermark_first_kept", 'state_at = max(build.find("readProjection("), build.find("PlanningStates.read("))' in read(ROOT / "tools/validate_planner_engine.py"),
      "watermark check not narrowed")
check("E13A-16_contract_lists_narrowing", len(contract.get("living_gates_narrowed", [])) == 6, "narrowing not recorded")

# ---------------------------------------------------------------- honesty
mutation = contract.get("mutation_results", {})
check("E13A-17_mutation_all_detected", mutation.get("detected") == mutation.get("total") == len(mutation.get("mutants", [])) >= 40,
      f"{mutation.get('detected')}/{mutation.get('total')}")
check("E13A-17_mutation_control", mutation.get("negative_control_result") == "survived_as_expected"
      and mutation.get("compile_failure_is_detection") is False and mutation.get("only_weekly_suites_run") is True, "harness honesty")
vm = contract.get("validator_mutation", {})
check("E13A-17_validator_mutation", vm.get("detected") == vm.get("total") and (vm.get("total") or 0) >= 20
      and vm.get("negative_control_result") == "no_false_positive", str(vm))
runs = {r["id"]: r for r in contract.get("verified_runs", [])}
check("E13A-17_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
device = contract.get("device_verification", {})
check("E13A-17_device_not_claimed", device.get("t6_run") is False and device.get("claimed") is False, "a device result is claimed")
loops = contract.get("open_loops", {})
for loop, owner in (("diagnostic_waiver_and_s06", "13F"), ("retroactive_contamination", "13D"),
                    ("calling_composition_from_the_app", "16D"), ("monthly_composition", "13B")):
    check(f"E13A-17_open_{loop[:30]}", str(loops.get(loop, {}).get("owner")) == owner, f"{loop} not owned")
forbidden = set(contract.get("forbidden_weekly_patterns", []))
for pattern in ("role_as_quota", "fixed_question_count_or_duration", "item_promotes_itself", "seen_or_solved_item_as_fresh",
                "weekly_queue_band_or_budget", "missed_week_as_debt", "skip_as_incorrect", "overall_score_or_pass_mark",
                "state_change_without_engine", "stored_blueprint_edited"):
    check(f"E13A-18_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 13A QA PASS", "**Decision:** `D-098`", "A week is an identity, not a quota and not a deadline",
                 "VDW-v0", "Not run: T6", "Mutation 42/42"]:
    check(f"E13A-19_spec_{fragment[:26]}", fragment in spec_text, f"spec: {fragment!r}")
for fragment in ["No web research pass was needed", "WBA-v0", "QAB-v0", "ISO"]:
    check(f"E13A-20_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E13A-21_no_repeated_context_heading", len(context_headings) == len(set(context_headings)),
      f"repeated={[h for h in set(context_headings) if context_headings.count(h) > 1]}")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E13A-21_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)),
      f"repeated={sorted(h for h in set(plan_steps) if plan_steps.count(h) > 1)}")
combining = chr(0x0307)
check("E13A-21_no_combining_dot", all(combining not in read(p) for p in (SPEC, RESEARCH, CONTRACT)), "U+0307 in 13A text")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "WBAX-v0", "stage_step": "13A", "decision": "D-098",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"13A_WEEKLY_ASSESSMENT_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
