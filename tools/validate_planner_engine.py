"""Independent 12C QA — PLNX-v0 Planner Engine v1.

The implementation is validated against the Stage 3 contracts, not against its own. The trigger kinds
are read out of 3B's own text (`TASK_TAXONOMY_SPEC.md`), the capacity defaults out of D-033's
(`ADAPTIVE_PLANNER_SPEC.md`), the bands and rank vector out of `PBR-v0`'s, the dispositions and reason
codes out of `PDT-v0`'s, the ownership and ports out of `MSBX-v0` — each compared with the Kotlin.

The checks that matter most are structural: a priority score, a knapsack, an invented threshold, an
invented reason code and a plan that can be edited must all be **unrepresentable**, not merely absent.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/12c_planner_engine/planner_engine.yaml"
SPEC = ROOT / "docs/PLANNER_ENGINE_IMPL_SPEC.md"
RESEARCH = ROOT / "research/12c_planner_engine_research.md"
QA_OUT = ROOT / "arch/12c_planner_engine/qa_report.yaml"

CAPACITY_SPEC = ROOT / "docs/ADAPTIVE_PLANNER_SPEC.md"
TAXONOMY = ROOT / "docs/TASK_TAXONOMY_SPEC.md"
PBR = ROOT / "docs/PRIORITY_POLICY_SPEC.md"
PDT = ROOT / "docs/PLANNER_EXPLAINABILITY_SPEC.md"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
ENGLISH_QA = ROOT / "curriculum/english/7c_daily_component/qa_report.yaml"

ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/PlannerEngine.kt"
FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/PlannerFacts.kt"
CODEC_KT = ANDROID / "core-model/src/main/kotlin/coach/PlanTraceCodec.kt"
TODAY_KT = ANDROID / "core-model/src/main/kotlin/coach/TodayFacts.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/BuildDailyPlan.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
STORE_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/CurriculumStore.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
CONTENT_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"

FACTS_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/PlannerFactsTest.kt"
ENGINE_TEST = ANDROID / "core-engines/src/test/kotlin/coach/engines/PlannerEngineTest.kt"
APP_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/BuildDailyPlanTest.kt"
T2_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/PlanStorageTest.kt"

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"//[^\n]*", "", source)


def enum_ids(source: str, name: str) -> list[str]:
    match = re.search(rf"enum class {name}\b[^{{]*\{{(.*?)\n\}}", source, re.S)
    return re.findall(r'\(\s*"([A-Za-z0-9_]+)"', match.group(1)) if match else []


def enum_codes(source: str, name: str) -> list[str]:
    match = re.search(rf"enum class {name}\b[^{{]*\{{(.*?)\n\}}", source, re.S)
    return re.findall(r'\(\s*"[A-Za-z0-9_]+",\s*"([a-z0-9_.]+)"', match.group(1)) if match else []


def body(source: str, signature: str) -> str:
    """A declaration's own text. The parameter list is balanced first (11D and 12A each found a reader
    that stopped at a default value's `=`), and every body read is itself checked to be non-empty."""
    start = source.find(signature)
    if start < 0:
        return ""
    cursor = source.find("(", start)
    if cursor < 0:
        return ""
    depth = 0
    for i in range(cursor, len(source)):
        depth += {"(": 1, ")": -1}.get(source[i], 0)
        if depth == 0:
            cursor = i + 1
            break
    rest = source[cursor:]
    brace = rest.find("{")
    equals = rest.find("=")
    if brace >= 0 and (equals < 0 or brace < equals):
        depth = 0
        for i in range(cursor + brace, len(source)):
            depth += {"{": 1, "}": -1}.get(source[i], 0)
            if depth == 0:
                return source[cursor + brace + 1:i]
        return ""
    end = source.find("\n\n", cursor)
    return source[start:end if end > 0 else len(source)]


def params(source: str, signature: str) -> list[str]:
    start = source.find(signature)
    if start < 0:
        return []
    cursor = source.find("(", start)
    depth, end = 0, cursor
    for i in range(cursor, len(source)):
        depth += {"(": 1, ")": -1}.get(source[i], 0)
        if depth == 0:
            end = i
            break
    return re.findall(r"(?:^|,|\()\s*(?:val\s+)?(\w+)\s*:", source[cursor:end + 1])


def constructor_params(source: str, cls: str) -> list[str]:
    match = re.search(rf"class {cls}\b[^(]*\((.*?)\n\)", source, re.S)
    return re.findall(r"val (\w+)\s*:", match.group(1)) if match else []


def text_block_after(text: str, anchor: str) -> list[str]:
    at = text.find(anchor)
    if at < 0:
        return []
    block = re.search(r"```text\n(.*?)```", text[at:], re.S)
    return [line.strip() for line in block.group(1).splitlines() if line.strip()] if block else []


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


contract = load(CONTRACT)
msbx = load(MSBX)
capacity_text = read(CAPACITY_SPEC)
taxonomy = read(TAXONOMY)
pbr = read(PBR)
pdt = read(PDT)

for path in (ENGINE_KT, FACTS_KT, CODEC_KT, APP_KT, FACTS_TEST, ENGINE_TEST, APP_TEST, T2_TEST, SPEC, RESEARCH):
    check(f"E12C-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

engine = strip_comments(read(ENGINE_KT))
facts = strip_comments(read(FACTS_KT))
codec = strip_comments(read(CODEC_KT))
today = strip_comments(read(TODAY_KT))
app = strip_comments(read(APP_KT))
ports = strip_comments(read(PORTS_KT))
store = strip_comments(read(STORE_KT))
schema = read(SCHEMA_KT)
content = strip_comments(read(CONTENT_KT))
facts_test = read(FACTS_TEST)
engine_test = read(ENGINE_TEST)
app_test = read(APP_TEST)
t2_test = read(T2_TEST)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E12C-01_model", contract.get("model") == "PLNX-v0", str(contract.get("model")))
check("E12C-01_status", contract.get("status") == "accepted_12c", str(contract.get("status")))
check("E12C-01_decision", contract.get("decision") == "D-094", str(contract.get("decision")))
for key in ("replan_implemented", "reentry_implemented", "paused_continuation_implemented", "reason_text_implemented",
            "today_reads_plan", "capacity_setting_stored", "authored_tasks_shipped", "starvation_threshold_calibrated",
            "schema_changed", "migration_added", "interfaces_added", "boundaries_changed", "surface_semantics_changed"):
    check(f"E12C-01_scope_{key}", contract.get("scope", {}).get(key) is False, f"{key}={contract['scope'].get(key)}")
for key in ("priority_score_claim", "utility_maximisation_claim"):
    check(f"E12C-01_no_{key}", contract["scope"].get(key) is None, f"{key} was claimed")
check("E12C-01_model_constant", 'const val PLANNER_MODEL = "PLNX-v0"' in engine, "the planner does not stamp PLNX-v0")

# ---------------------------------------------------------------- capacity, read out of D-033
declared = {name: re.search(rf"{name} = ([\d.]+)", capacity_text) for name in (
    "short_profile_minutes_v0", "normal_profile_minutes_v0", "intensive_profile_minutes_v0",
    "planning_reserve_ratio_v0", "minimum_plannable_block_minutes_v0")}
for name, match in declared.items():
    check(f"E12C-02_d033_states_{name}", match is not None, f"D-033 does not state {name}")
for name, kotlin in (("short_profile_minutes_v0", "SHORT_PROFILE_MINUTES_V0"),
                     ("normal_profile_minutes_v0", "NORMAL_PROFILE_MINUTES_V0"),
                     ("intensive_profile_minutes_v0", "INTENSIVE_PROFILE_MINUTES_V0"),
                     ("minimum_plannable_block_minutes_v0", "MINIMUM_PLANNABLE_BLOCK_MINUTES_V0")):
    in_kotlin = re.search(rf"const val {kotlin} = (\d+)", engine)
    check(f"E12C-02_constant_{kotlin}", in_kotlin is not None and declared[name] is not None
          and int(in_kotlin.group(1)) == int(float(declared[name].group(1)))
          and contract["declared_constants"][name] == int(float(declared[name].group(1))),
          f"d033={declared[name] and declared[name].group(1)} kotlin={in_kotlin and in_kotlin.group(1)}")
reserve = re.search(r"const val PLANNING_RESERVE_PERCENT_V0 = (\d+)", engine)
check("E12C-02_reserve_is_d033", reserve is not None and declared["planning_reserve_ratio_v0"] is not None
      and int(reserve.group(1)) / 100 == float(declared["planning_reserve_ratio_v0"].group(1))
      and contract["declared_constants"]["planning_reserve_ratio_v0"] == float(declared["planning_reserve_ratio_v0"].group(1)),
      f"kotlin={reserve and reserve.group(1)}")
resolve = body(engine, "fun resolveCapacity(")
check("E12C-02_resolve_body_read", len(resolve) > 200, "the capacity reader returned nothing")
order = [resolve.find(f) for f in ("CapacitySource.TODAY_OVERRIDE to", "CapacitySource.SELECTED_SHORT to",
                                   "CapacitySource.SCHEDULED_DEFAULT to", "CapacitySource.NORMAL_PROFILE to")]
check("E12C-02_resolution_order", all(i >= 0 for i in order) and order == sorted(order), f"positions={order}")
# 12D moved the D-033 §4/§5 rule into `capacityOf` so a replan's remainder uses the very same rule; the
# check reads it where it now lives. The guarantee — floor(hard * 0.90), relaxed only below the block,
# and resolveCapacity applying it — is unchanged.
capacity_rule = body(engine, "fun capacityOf(")
check("E12C-02_planning_budget_exact", "minutes * (100 - PLANNING_RESERVE_PERCENT_V0) / 100" in capacity_rule
      and "return capacityOf(source, minutes)" in resolve,
      "the planning budget is not floor(hard * 0.90)")
check("E12C-02_relaxed_only_below_block", "val planning = if (below) minutes else" in capacity_rule,
      "the reserve relaxes outside the minimum block")
check("E12C-02_no_teaching_below_block",
      "!(capacity.belowMinimumBlock && a.candidate.purpose == TaskPurpose.TEACH)" in engine,
      "new teaching is offered below the minimum block")
capacity_source_codes = [c for c in text_block_after(pdt, "## 8.6 Capacity / fit") if c.startswith("capacity.source_")]
check("E12C-02_source_codes_are_pdt", set(enum_codes(facts, "CapacitySource")) <= set(capacity_source_codes)
      and len(capacity_source_codes) == 5, f"pdt={capacity_source_codes}")
check("E12C-02_capacity_input_has_no_default",
      not re.search(r"normalProfileMinutes:\s*Int\s*=", facts), "the capacity input substitutes a default for a setting")

# ---------------------------------------------------------------- needs, read out of 3B
triggers = text_block_after(taxonomy, "## 2.1 Canonical trigger kinds")
check("E12C-03_triggers_read_from_3b", len(triggers) == 10, f"3b={triggers}")
check("E12C-03_triggers_equal_3b", enum_ids(facts, "NeedTrigger") == triggers, f"kotlin={enum_ids(facts, 'NeedTrigger')}")
need_codes = text_block_after(pdt, "## 8.1 LearningNeed / state")
check("E12C-03_need_codes_equal_pdt", enum_codes(facts, "NeedTrigger") == need_codes, f"pdt={need_codes}")
purposes = text_block_after(taxonomy, "## 3.1 `primary_purpose`")
check("E12C-03_purposes_equal_3b", enum_ids(today, "TaskPurpose") == purposes, f"3b={purposes}")
needs = body(engine, "fun needsFromSkillStates(")
check("E12C-03_needs_body_read", len(needs) > 500, "the needs reader returned nothing")
for label, fragment in (
    ("remediation_from_weakness", "if (state.weaknessAxis == REMEDIATION_REQUIRED) {"),
    ("verification_from_mastery", "if (state.mastery == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE ||"),
    ("review_due_is_not_negative", "add(need(NeedTrigger.RETENTION_REVIEW_DUE, EvidenceSeverity.NO_NEGATIVE_EVIDENCE,"),
    ("review_due_is_just_due", "TemporalUrgency.JUST_DUE, ContinuationValue.FRESH_NEW_CONTEXT))"),
    ("new_learning_only_when_unevidenced", "(state.mastery == null || state.mastery == MasteryAxisState.NOT_YET_EVIDENCED) &&"),
    ("new_learning_only_when_published", "state.lifecycleStatus == SKILL_PUBLISHED"),
    ("off_route_opens_nothing", "if (!onRoute(state.lifecycleStatus)) return@flatMap emptyList()"),
):
    check(f"E12C-03_{label}", fragment in needs, f"missing {fragment}")
on_route = re.search(r"SKILL_ON_ROUTE = setOf\(([^)]*)\)", engine)
check("E12C-03_route_lifecycles", on_route is not None
      and sorted(re.findall(r'"(\w+)"', on_route.group(1))) == ["deprecated", "published"],
      f"kotlin={on_route and on_route.group(1)}")
check("E12C-03_no_overdue_invented", "OVERDUE_HIGH" not in needs, "the planner invents an overdue bucket")

# ---------------------------------------------------------------- priority, read out of PBR-v0
bands = [re.sub(r"^P\d — `?|`$", "", h) for h in re.findall(r"^## (P\d — `\w+`)", pbr, re.M)]
check("E12C-04_bands_read_from_pbr", len(bands) == 5, f"pbr={bands}")
check("E12C-04_bands_equal_pbr", enum_ids(facts, "PriorityBand") == bands, f"kotlin={enum_ids(facts, 'PriorityBand')}")
band_codes = [c for c in text_block_after(pdt, "## 8.5 Priority / PBR-v0") if re.match(r"priority\.p\d_", c)]
check("E12C-04_band_codes_equal_pdt", enum_codes(facts, "PriorityBand") == band_codes, f"pdt={band_codes}")
# The anchor is the section heading: the name `PriorityRankVector` is itself inside the block.
rank_fields = [re.sub(r"^\d+\.\s*", "", line) for line in text_block_after(pbr, "# 5. Band tek başına yeterli değildir")[1:]]
check("E12C-04_rank_fields_read_from_pbr", len(rank_fields) == 10, f"pbr={rank_fields}")
kotlin_rank = constructor_params(facts, "RankVector")
check("E12C-04_rank_vector_has_ten_fields", len(kotlin_rank) == 10, f"kotlin={kotlin_rank}")
compare = body(facts, "override fun compareTo(")
compared = re.findall(r"\{ it\.(\w+) \}", compare)
check("E12C-04_rank_compared_in_declared_order", compared == kotlin_rank, f"compared={compared}")
check("E12C-04_rank_not_summed", not re.search(r"\.sum\(|\+\s*it\.|ordinal\s*\*", compare), "the rank vector is summed")
for enum_name, anchor in (("BlockingScope", "## 6.1"), ("Criticality", "## 6.2"), ("EvidenceSeverity", "## 6.3"),
                          ("TemporalUrgency", "## 6.4"), ("ContinuationValue", "## 6.6"), ("DurationFit", "## 6.9")):
    declared_order = text_block_after(pbr, anchor)
    check(f"E12C-04_{enum_name}_order_is_pbr", enum_ids(facts, enum_name) == declared_order,
          f"pbr={declared_order} kotlin={enum_ids(facts, enum_name)}")
starvation_order = text_block_after(pbr, "## 6.5")
check("E12C-04_starvation_values_are_pbr", sorted(enum_ids(facts, "StarvationBucket")) == sorted(starvation_order),
      f"pbr={starvation_order}")
band = body(engine, "fun band(")
check("E12C-04_band_body_read", len(band) > 300, "the band reader returned nothing")
check("E12C-04_p0_needs_real_blocker", "if (blocking && critical) PriorityBand.P0 else PriorityBand.P1" in band,
      "P0 does not need both a critical Skill and real blocking")
check("E12C-04_review_due_maintenance",
      "if (critical || need.temporalUrgency == TemporalUrgency.OVERDUE_HIGH) PriorityBand.P2 else PriorityBand.P3" in band,
      "review due is not P2/P3")
check("E12C-04_paused_is_p2", "if (need.continuation == ContinuationValue.PAUSED_SAFE_CHECKPOINT) PriorityBand.P2 else PriorityBand.P3" in band,
      "paused work is not P2")
check("E12C-04_reinforcement_p4", "NeedTrigger.REINFORCEMENT_OPPORTUNITY -> PriorityBand.P4" in band, "reinforcement is not P4")
check("E12C-04_starvation_limited",
      "return if (base == PriorityBand.P3 && starvation == StarvationBucket.PROMOTE) PriorityBand.P2 else base" in band,
      "starvation promotion is not limited to planned progress")
plan = body(engine, "fun plan(")
check("E12C-04_plan_body_read", len(plan) > 3000, "the plan reader returned nothing")
check("E12C-04_band_before_rank", ".sortedWith(compareBy({ it.band }, { it.rank }))" in plan,
      "needs are not ordered by band, then rank")
check("E12C-04_no_random", not re.search(r"\b(Random|random\(|shuffled\(|nextInt)", engine), "randomness in the planner")
check("E12C-04_no_score", not re.search(r"\b(score|utility|weight|perMinute|per_minute)\b", engine, re.I),
      "a score or utility appears in the planner")

# ---------------------------------------------------------------- the gate order and trust
assess_at = plan.find("val assessed = bounded.map")
rank_at = plan.find("val ranked = needs.map")
select_at = plan.find("var remaining = budget")
check("E12C-05_gate_order", 0 <= assess_at < rank_at < select_at, f"positions={[assess_at, rank_at, select_at]}")
check("E12C-05_untrusted_high_stakes", "candidate.purpose in HIGH_STAKES && candidate.validationStatus !in TRUSTED_FOR_HIGH_STAKES ->" in plan,
      "an unvalidated candidate can serve a high-stakes purpose")
high = re.search(r"HIGH_STAKES = setOf\(([^)]*)\)", engine)
check("E12C-05_high_stakes_set", high is not None and sorted(re.findall(r"TaskPurpose\.(\w+)", high.group(1)))
      == ["ASSESS", "DIAGNOSE", "RETAIN"], f"kotlin={high and high.group(1)}")
check("E12C-05_unselectable_invalid", "!candidate.validationStatus.selectable ->" in plan, "a draft candidate is usable")
check("E12C-05_gate_fails_closed", "Assessed(candidate, PrerequisiteEligibility.BLOCKED, false, CandidateDisposition.BLOCKED_PREREQUISITE," in plan,
      "a candidate the gate never answered for is usable")
check("E12C-05_blocked_unusable", "decision.eligibility == PrerequisiteEligibility.BLOCKED ->" in plan, "a blocked candidate is usable")
check("E12C-05_invalid_unusable", "decision.eligibility == PrerequisiteEligibility.INVALID_PREREQUISITE_METADATA ->" in plan,
      "invalid prerequisite metadata is usable")
cap = re.search(r"const val MAX_CANDIDATES_PER_NEED_V0 = (\d+)", engine)
check("E12C-05_cap_declared", cap is not None and int(cap.group(1)) == contract["engineering_bounds"]["max_candidates_per_need_v0"]
      and contract["engineering_bounds"]["learning_meaning"] is False, f"kotlin={cap and cap.group(1)}")
check("E12C-05_cap_applied", "ordered.take(MAX_CANDIDATES_PER_NEED_V0)" in plan, "the candidate set is unbounded")
check("E12C-05_deprecated_last", "compareBy({ it.validationStatus == LifecycleStatus.DEPRECATED }, { it.id })" in plan,
      "deprecated candidates are not ordered last")
check("E12C-05_holds_back_from_gate", "(decision.hardBlockerSkills + decision.uncertainSkills).forEach { skill ->" in plan,
      "blocking scope is not taken from the gate's answers")
plan_params = params(engine, "fun plan(")
check("E12C-05_no_priority_override_input", plan_params and not [p for p in plan_params if re.search(r"priority|score|focus|weight", p, re.I)],
      f"params={plan_params}")

# ---------------------------------------------------------------- selection
for label, fragment in (
    ("fits", "choice.candidate.costMinutes <= remaining ->"),
    ("safe_split", "choice.candidate.splittable && choice.candidate.minimumSafeChunkMinutes!! <= remaining ->"),
    ("split_plans_what_fits", "take(choice, remaining, split = true)"),
    ("smaller_alternative", "val smaller = offered.drop(1).firstOrNull { it.candidate.costMinutes <= remaining }"),
    ("budget_reduced", "remaining -= minutes"),
    ("same_need_superseded", "fun supersedeAllBut(kept: Assessed) = offered.filter { it !== kept }.forEach {"),
    ("deferred_for_time", 'listOf("selection.not_selected_capacity", "capacity.deferred_not_enough_time"))'),
    ("no_reason_invented", "else -> finish(NeedDisposition.NO_VALID_CANDIDATE, null, emptyList())"),
    ("closed_need_not_planned", "if (needKey !in openNeeds) {"),
):
    check(f"E12C-06_{label}", fragment in plan, f"missing {fragment}")
order = [plan.find(f) for f in ("choice.candidate.costMinutes <= remaining ->", "choice.candidate.splittable &&",
                                "val smaller = offered.drop(1)", 'listOf("selection.not_selected_capacity"')]
check("E12C-06_fit_split_smaller_defer", all(i >= 0 for i in order) and order == sorted(order), f"positions={order}")
check("E12C-06_atomic_never_split", "require(!(splittable && atomicEvidenceBoundary))" in facts,
      "an atomic evidence boundary can be split")
check("E12C-06_no_knapsack", not re.search(r"knapsack|sortedBy\s*\{\s*it\.candidate\.costMinutes", engine, re.I),
      "selection orders by cost")
check("E12C-06_starvation_not_invented", 'starvation: Map<String, StarvationBucket> = emptyMap()' in engine
      and "starvation" not in app, "a starvation pressure is supplied without a calibrated threshold")
english_qa = read(ENGLISH_QA)
check("E12C-06_english_qa_refused_threshold", "no invented N-day starvation threshold" in english_qa,
      "7C's QA no longer records the refusal the planner relies on")
prg_reasons = text_block_after(read(ROOT / "docs/PREREQUISITE_POLICY_SPEC.md"), "3D en az şu reason input'larını üretir")
handed = load(ROOT / "arch/12b_prerequisite_engine/prerequisite_engine.yaml")["reason_inputs"]["owned_elsewhere"]
check("E12C-06_independent_branch_is_prg", "independent_branch_available" in prg_reasons
      and handed.get("independent_branch_available") == "12C"
      and 'const val INDEPENDENT_BRANCH_AVAILABLE = "independent_branch_available"' in engine,
      "the reason input 12B handed to the planner is not produced")
check("E12C-06_independent_branch_only_when_true",
      "if (selected.isNotEmpty() && needTraces.any { it.disposition == NeedDisposition.BLOCKED }) {" in plan,
      "the independent-branch reason is not tied to a blocked need and a selected task")
invariant_block = plan[plan.find("val invariants = linkedMapOf("):plan.find("return PlanTrace(")]
invariants = re.findall(r'"(\w+)" to', invariant_block)
for name in ("planned_within_planning_budget", "hard_budget_not_exceeded", "no_blocked_or_invalid_candidate_selected",
             "one_task_per_need", "no_teaching_below_minimum_block"):
    check(f"E12C-06_invariant_{name}", name in invariants, f"invariants={invariants}")

# ---------------------------------------------------------------- the trace, read out of PDT-v0
need_dispositions = text_block_after(pdt, "`final_disposition` baseline")
check("E12C-07_need_dispositions_equal_pdt", enum_ids(facts, "NeedDisposition") == need_dispositions, f"pdt={need_dispositions}")
candidate_dispositions = text_block_after(pdt, "Candidate disposition baseline")
check("E12C-07_candidate_dispositions_equal_pdt", enum_ids(facts, "CandidateDisposition") == candidate_dispositions,
      f"pdt={candidate_dispositions}")
pdt_codes = set(re.findall(r"^([a-z]+\.[a-z0-9_]+)$", pdt, re.M))
used_codes = set(re.findall(r'"((?:need|candidate|eligibility|priority|capacity|selection|replan)\.[a-z0-9_]+)"', engine + facts))
check("E12C-07_every_code_is_pdt", used_codes and used_codes <= pdt_codes, f"not in PDT-v0: {sorted(used_codes - pdt_codes)}")
# 12C owns planner_trace/1 and that it still decodes; 12D moved the written format to /2. The check was
# narrowed from "the format is /1" to what 12C decided: a versioned planner_trace format whose /1 still
# reads, with the planner and the codec naming the same version.
written = re.search(r'const val FORMAT = "(planner_trace/\d+)"', codec)
check("E12C-07_trace_format", written is not None and 'const val FORMAT_V1 = "planner_trace/1"' in codec
      and f'const val TRACE_SCHEMA = "{written.group(1)}"' in engine,
      "the trace format is not a versioned planner_trace format that still reads /1")
decode = body(codec, "private fun decodeOrThrow(")
check("E12C-07_decode_body_read", len(decode) > 1000, "the decode reader returned nothing")
check("E12C-07_unknown_format_refused", "if (lines.firstOrNull() != FORMAT) throw Malformed()" in decode
      or "if (version != FORMAT && version != FORMAT_V1) throw Malformed()" in decode, "another format is accepted")
check("E12C-07_unknown_section_refused", "else -> throw Malformed()" in decode, "an unknown section is accepted")
check("E12C-07_decode_never_guesses", "fun decode(stored: String): PlanTrace? = runCatching { decodeOrThrow(stored) }.getOrNull()" in codec,
      "decoding can return a partial trace")
check("E12C-07_title_escaped", "if (byte >= 0 && char in UNRESERVED) append(char)" in codec, "a title is stored unescaped")
trace_fields = constructor_params(facts, "PlanTrace")
for field in ("capacity", "needs", "candidates", "selected", "planReasonCodes", "invariantChecks", "policyVersions",
              "truthWatermark", "curriculumVersion", "skillsNotOnRoute"):
    check(f"E12C-07_trace_{field}", field in trace_fields, f"the trace does not carry {field}")
FORBIDDEN = ("score", "percent", "debt", "streak", "fail", "grade", "utility", "rationale")
for cls in ("PlannedEntry", "NeedTrace", "CandidateTrace", "PlanTrace", "LearningNeed", "DailyCapacity", "TaskCandidate"):
    fields = constructor_params(facts, cls)
    check(f"E12C-07_no_verdict_field_{cls}", fields and not [f for f in fields if any(w in f.lower() for w in FORBIDDEN)],
          f"{cls} fields={fields}")

# ---------------------------------------------------------------- the plan as truth
build = body(app, "fun build(")
check("E12C-08_build_body_read", len(build) > 1500, "the build reader returned nothing")
check("E12C-08_one_transaction", build.count("inTransaction") == 1, "the plan is not written in one transaction")
watermark_at = build.find("persistence.truthWatermark()")
check("E12C-08_watermark_first", 0 <= watermark_at < build.find("persistence.publishedSkills()") < build.find("readProjection("),
      "the watermark is not read before the state")
check("E12C-08_nothing_published_writes_nothing", "?: return Built.NothingPublished" in build
      and build.find("?: return Built.NothingPublished") < build.find("inTransaction"), "a plan is written with nothing published")
for kind in ("plan_version", "planned_task", "planner_decision_trace"):
    check(f"E12C-08_appends_{kind}", f'"{kind}"' in build, f"{kind} is not appended")
check("E12C-08_trace_stored", '"trace" to PlanTraceCodec.encode(trace)' in build, "the trace is not stored")
check("E12C-08_no_projection", "writeProjection" not in app and "evidenceFor" not in app, "planning writes a projection or reads evidence")
check("E12C-08_gate_asked", "gate.resolve(" in build and "requiredSkills = candidate.requiredSkills," in build
      and "requiresStrictPrerequisiteConfidence = candidate.requiresStrictPrerequisiteConfidence," in build,
      "a candidate's own requirements do not reach the gate")
truth_tables = re.search(r"val truthTables = listOf\((.*?)\)", schema, re.S)
check("E12C-08_plan_tables_are_truth", truth_tables is not None and all(f'"{t}"' in truth_tables.group(1)
      for t in ("plan_version", "planned_task", "planner_decision_trace")), "the plan tables are not truth")
planned_task_ddl = re.search(r"CREATE TABLE IF NOT EXISTS planned_task \((.*?)\n\s*\)\n", schema, re.S)
columns = re.findall(r"^\s*(\w+)\s+(?:INTEGER|TEXT)", planned_task_ddl.group(1), re.M) if planned_task_ddl else []
check("E12C-08_planned_task_columns_unchanged",
      columns == ["id", "plan_version_id", "skill_logical_id", "skill_version", "position"], f"columns={columns}")
check("E12C-08_schema_version_unchanged", "const val VERSION = 2" in schema, "the schema version moved")

# ---------------------------------------------------------------- ports and adapters
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
check("E12C-09_port_count", sorted(interfaces) == sorted(p["id"] for p in msbx["ports"]["set"]), f"interfaces={interfaces}")
check("E12C-09_published_skills", "fun publishedSkills(): List<SkillRow>" in ports, "publishedSkills is missing")
check("E12C-09_task_candidates", "fun taskCandidates(need: LearningNeed): List<TaskCandidate>" in ports, "taskCandidates is missing")
skills_read = body(store, "fun publishedSkills(")
check("E12C-09_skills_body_read", len(skills_read) > 100, "the skills reader returned nothing")
check("E12C-09_newest_version", "WHERE s.version = (SELECT MAX(n.version) FROM skill n WHERE n.logical_id = s.logical_id)" in skills_read,
      "every version of a Skill is planned from")
check("E12C-09_lifecycle_unfiltered", "lifecycle_status" not in skills_read, "the store filters lifecycles")
check("E12C-09_skills_read_only", not re.search(r"\b(INSERT|UPDATE|DELETE)\b", skills_read), "reading Skills writes")
check("E12C-09_adapter_answers_truthfully", "override fun taskCandidates(need: LearningNeed): List<TaskCandidate> = emptyList()" in content,
      "the file adapter claims tasks its format cannot carry")

# ---------------------------------------------------------------- tests
for name in ["the rank vector is compared field by field, never summed",
             "the stored trace reads back exactly, whatever the title contains",
             "a trace in another format or with an unknown part is not guessed at",
             "nothing in the plan is a score, a percentage or a debt"]:
    check(f"E12C-10_facts_test_{name[:42]}", f"`{name}`" in facts_test, f"missing test: {name}")
for name in ["eighty minutes of needs in a fifty minute budget is PBR-v0's worked example",
             "a critical label alone does not make an integrity blocker",
             "review due is maintenance, not forgetting, and never an integrity blocker",
             "priority never rescues a blocked or untrusted candidate",
             "a short easy task never jumps a long important one",
             "a higher priority task that does not fit is deferred for time, not called less important",
             "a safe split plans the part that fits, and an atomic evidence boundary is never cut",
             "starvation lifts planned progress one band and never past repair work",
             "no pressure is invented when none is supplied",
             "the same state gives the same plan whatever order it arrives in",
             "a need nothing authored serves says so without inventing a reason",
             "needs come from the axis each engine owns, and from nothing else"]:
    check(f"E12C-10_engine_test_{name[:42]}", f"`{name}`" in engine_test, f"missing test: {name}")
for name in ["with nothing published nothing is planned and nothing is written",
             "the plan, its tasks and its trace are appended together in one transaction",
             "the watermark is read before any state, so the plan never claims to have seen more",
             "a candidate is asked about at the prerequisite gate before it is ranked"]:
    check(f"E12C-10_app_test_{name[:42]}", f"`{name}`" in app_test, f"missing test: {name}")
for name in ["a plan, its tasks and its trace append as truth and read back exactly",
             "a plan is never edited in place",
             "planning reads the newest version of every Skill, lifecycle included, in a stable order"]:
    check(f"E12C-10_t2_test_{name[:42]}", f"`{name}`" in t2_test, f"missing T2 test: {name}")

# ---------------------------------------------------------------- honesty
device = contract["device_verification"]
check("E12C-11_device_not_claimed", device["t6_run"] is False and device["claimed"] is False, "a device result is claimed")
mutation = contract["mutation_results"]
check("E12C-11_mutation_all_detected", mutation["detected"] == mutation["total"] == len(mutation.get("mutants", [])) >= 40,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E12C-11_mutation_ran_gradle", mutation.get("harness_verified_to_run_gradle") is True,
      "the mutation harness was not verified to have run anything")
check("E12C-11_mutation_negative_control", mutation.get("negative_control_result") == "survived_as_expected",
      "the harness was never shown able to report a survivor")
vm = contract["validator_mutation"]
check("E12C-11_validator_mutation", vm["detected"] == vm["total"] >= 30 and vm.get("negative_control_result") == "no_false_positive",
      str(vm))
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E12C-11_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
check("E12C-11_not_verified_named", len(contract.get("not_verified", [])) >= 3, "what was not verified must be named")
check("E12C-11_not_reachable_in_app", contract["wiring"]["reachable_in_app_today"] is False,
      "the contract claims the planner is reachable in the app")
boundaries = {str(k) for k in contract.get("future_stage_boundaries", {})}
for owner in ("12D", "12E", "12F", "13", "15", "16D", "18C"):
    check(f"E12C-11_boundary_{owner}", owner in boundaries, f"missing {owner}")
forbidden = set(contract.get("forbidden_planner_patterns", []))
for pattern in ("additive_priority_score", "tasks_per_minute_or_knapsack", "short_task_bias", "review_due_as_forgetting",
                "critical_label_as_p0", "deferred_task_as_next_day_debt", "fixed_category_percentages",
                "priority_bypassing_the_gate", "random_tie_break", "invented_starvation_threshold",
                "invented_reason_for_an_unserved_need", "plan_edited_in_place"):
    check(f"E12C-12_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 12C QA PASS", "**Decision:** `D-094`",
                 "Semantic priority first, physical fit second", "T6", "planner_trace/1", "MUTATION"]:
    present = fragment in spec_text if fragment != "MUTATION" else "MUTATION_SUMMARY_PLACEHOLDER" not in spec_text
    check(f"E12C-13_spec_{fragment[:26]}", present, f"spec: {fragment!r}")
for fragment in ["No web research pass was needed", "PBR-v0", "no invented", "planner_trace/1"]:
    check(f"E12C-14_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E12C-15_no_repeated_context_heading", len(context_headings) == len(set(context_headings)),
      f"repeated={[h for h in set(context_headings) if context_headings.count(h) > 1]}")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E12C-15_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)),
      f"repeated={sorted(h for h in set(plan_steps) if plan_steps.count(h) > 1)}")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "PLNX-v0", "stage_step": "12C", "decision": "D-094",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"12C_PLANNER_ENGINE_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
