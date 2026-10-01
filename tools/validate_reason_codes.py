"""Independent 12E QA — RSNX-v0 Reason Codes & Planner Explanation.

The implementation is validated against the accepted contracts, not against its own. The catalogue is
read out of `PDT-v0` §8's text and `PRG-v0` §20's, the Today families out of the contract and the Kotlin
`when`, and every Turkish sentence is read out of the template file and held to `PDT-v0` §9–§14.

The checks that matter most are structural: a reason the trace never recorded, a free-text reason, a band
shown as the reason, deferral worded as lower importance, a waiting task without its blocker, a row guessed
around an unreadable trace and started work offered again must all be **unrepresentable**.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

def schema_versions_owned(schema_text: str) -> bool:
    """Narrowed at 13A: this step added no migration. Any schema version beyond 2 must be declared by the
    accepted later contract that added it (`schema_migration` in its arch yaml), so an unowned move still fails."""
    import glob as _glob
    import yaml as _yaml
    match = re.search(r"const val VERSION = (\d+)", schema_text)
    if not match:
        return False
    version = int(match.group(1))
    owned = set()
    for path in _glob.glob(str(ROOT / "arch" / "*" / "*.yaml")):
        try:
            doc = _yaml.safe_load(open(path, encoding="utf-8"))
        except Exception:
            continue
        migration = doc.get("schema_migration") if isinstance(doc, dict) else None
        if isinstance(migration, dict) and migration.get("from") is not None and migration.get("to") is not None:
            owned.add((int(migration["from"]), int(migration["to"])))
    return all((v - 1, v) in owned for v in range(3, version + 1))


ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/12e_reason_codes/reason_codes.yaml"
SPEC = ROOT / "docs/PLANNER_EXPLANATION_IMPL_SPEC.md"
RESEARCH = ROOT / "research/12e_reason_codes_research.md"
QA_OUT = ROOT / "arch/12e_reason_codes/qa_report.yaml"

PDT = ROOT / "docs/PLANNER_EXPLAINABILITY_SPEC.md"
PRG = ROOT / "docs/PREREQUISITE_POLICY_SPEC.md"
THUX = ROOT / "docs/TODAY_HOME_SCREEN_SPEC.md"
UXIA = ROOT / "docs/INFORMATION_ARCHITECTURE_SPEC.md"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

CATALOGUE_KT = ANDROID / "core-model/src/main/kotlin/coach/ReasonCodes.kt"
FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/PlannerFacts.kt"
TODAY_FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/TodayFacts.kt"
EXPLANATION_FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/ExplanationFacts.kt"
CODEC_KT = ANDROID / "core-model/src/main/kotlin/coach/PlanTraceCodec.kt"
PLANNER_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/PlannerEngine.kt"
REPLAN_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/ReplanEngine.kt"
READING_KT = ANDROID / "core-application/src/main/kotlin/coach/application/PlanReading.kt"
QUERY_KT = ANDROID / "core-application/src/main/kotlin/coach/application/TodayFactsQuery.kt"
EXPLAIN_QUERY_KT = ANDROID / "core-application/src/main/kotlin/coach/application/PlannerExplanationQuery.kt"
TODAY_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TodayPresentation.kt"
EXPLAIN_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/PlannerExplanation.kt"
COPY_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/ExplanationCopy.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
ADAPTER_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
SCREEN_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/PlannerExplanationScreen.kt"
TODAY_SCREEN_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/TodayScreen.kt"
ACTIVITY_KT = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
APPLICATION_KT = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"

CATALOGUE_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/ReasonCodesTest.kt"
FACTS_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/PlannerFactsTest.kt"
PLANNER_TEST = ANDROID / "core-engines/src/test/kotlin/coach/engines/PlannerEngineTest.kt"
READING_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/PlanReadingTest.kt"
TODAY_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/TodayPresentationTest.kt"
EXPLAIN_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/PlannerExplanationTest.kt"
COPY_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/ExplanationCopyTest.kt"
T2_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/PlanStorageTest.kt"

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


def load(path: Path):
    return yaml.safe_load(path.read_text(encoding="utf-8"))


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"(?<![:\"])//[^\n]*", "", source)


def enum_block(source: str, name: str) -> str:
    match = re.search(rf"enum class {name}\b[^{{]*\{{(.*?)\n\}}", source, re.S)
    return match.group(1) if match else ""


def enum_ids(source: str, name: str) -> list[str]:
    return re.findall(r'\(\s*"([A-Za-z0-9_]+)"', enum_block(source, name))


def body(source: str, signature: str) -> str:
    """A declaration's own text; the parameter list is balanced first, and every body read is itself
    checked to be non-empty, so a reader failure is a FAIL rather than a silent PASS."""
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


def text_block_after(text: str, anchor: str) -> list[str]:
    at = text.find(anchor)
    if at < 0:
        return []
    block = re.search(r"```text\n(.*?)```", text[at:], re.S)
    return [line.strip() for line in block.group(1).splitlines() if line.strip()] if block else []


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


def kotlin_strings(source: str) -> list[str]:
    return re.findall(r'"((?:[^"\\]|\\.)*)"', source)


contract = load(CONTRACT)
msbx = load(MSBX)
pdt, prg, thux, uxia = read(PDT), read(PRG), read(THUX), read(UXIA)

for path in (CATALOGUE_KT, FACTS_KT, TODAY_FACTS_KT, EXPLANATION_FACTS_KT, CODEC_KT, PLANNER_KT, READING_KT, QUERY_KT,
             EXPLAIN_QUERY_KT, TODAY_KT, EXPLAIN_KT, COPY_KT, SCREEN_KT, CATALOGUE_TEST, READING_TEST, EXPLAIN_TEST,
             COPY_TEST, SPEC, RESEARCH):
    check(f"E12E-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

catalogue_raw = read(CATALOGUE_KT)
catalogue = strip_comments(catalogue_raw)
facts = strip_comments(read(FACTS_KT))
today_facts = strip_comments(read(TODAY_FACTS_KT))
explanation_facts = strip_comments(read(EXPLANATION_FACTS_KT))
codec = strip_comments(read(CODEC_KT))
planner = strip_comments(read(PLANNER_KT))
replan = strip_comments(read(REPLAN_KT))
reading = strip_comments(read(READING_KT))
query = strip_comments(read(QUERY_KT))
explain_query = strip_comments(read(EXPLAIN_QUERY_KT))
today = strip_comments(read(TODAY_KT))
explain = strip_comments(read(EXPLAIN_KT))
copy_raw = read(COPY_KT)
copy = strip_comments(copy_raw)
ports = strip_comments(read(PORTS_KT))
adapter = strip_comments(read(ADAPTER_KT))
schema = read(SCHEMA_KT)
screen = strip_comments(read(SCREEN_KT))
today_screen = read(TODAY_SCREEN_KT)
activity = strip_comments(read(ACTIVITY_KT))
application = strip_comments(read(APPLICATION_KT))
spec_text, research_text = read(SPEC), read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E12E-01_model", contract.get("model") == "RSNX-v0", str(contract.get("model")))
check("E12E-01_status", contract.get("status") == "accepted_12e", str(contract.get("status")))
check("E12E-01_decision", contract.get("decision") == "D-096", str(contract.get("decision")))
for key in ("reason_catalogue_implemented", "related_refs_recorded", "today_reads_the_plan",
            "planner_explanation_surface_implemented", "template_fallback_implemented"):
    check(f"E12E-01_scope_{key}", contract["scope"].get(key) is True, f"{key}={contract['scope'].get(key)}")
for key in ("llm_paraphrase_implemented", "virtual_user_scenarios_run", "planner_called_from_app",
            "retention_and_weakness_codes_written", "schema_changed", "migration_added", "interfaces_added", "boundaries_changed"):
    check(f"E12E-01_scope_{key}", contract["scope"].get(key) is False, f"{key}={contract['scope'].get(key)}")
for key in ("free_text_reason", "score_claim"):
    check(f"E12E-01_no_{key}", contract["scope"].get(key) is None, f"{key} was claimed")

# ---------------------------------------------------------------- the catalogue, read out of PDT-v0 §8 and PRG-v0 §20
pdt_sections = re.findall(r"^## (8\.\d+) ", pdt, re.M)
pdt_codes: list[list[str]] = [text_block_after(pdt, f"## {s} ") for s in pdt_sections]
prg_inputs = text_block_after(prg, "# 20. Explainability inputs")
check("E12E-02_pdt_read", len(pdt_sections) == 10 and all(pdt_codes), f"sections={pdt_sections}")
check("E12E-02_prg_read", len(prg_inputs) == 9, f"prg={prg_inputs}")
kotlin_families = re.findall(r"ReasonCodeFamily\.(\w+) to listOf\((.*?)\n        \)", catalogue, re.S)
kotlin_codes = [re.findall(r'"([a-z0-9_.]+)"', block) for _, block in kotlin_families]
check("E12E-02_family_order", [f for f, _ in kotlin_families] == [n for n in re.findall(r"^    (\w+)\(", enum_block(catalogue, "ReasonCodeFamily"), re.M)],
      f"families={[f for f, _ in kotlin_families]}")
check("E12E-02_pdt_families_equal", kotlin_codes[:10] == pdt_codes,
      f"differs at {[pdt_sections[i] for i in range(min(10, len(kotlin_codes))) if i < len(pdt_codes) and kotlin_codes[i] != pdt_codes[i]]}")
check("E12E-02_prg_inputs_equal", len(kotlin_codes) == 11 and kotlin_codes[10] == prg_inputs, f"kotlin={kotlin_codes[10:] }")
check("E12E-02_counts_match_contract", [len(c) for c in kotlin_codes] == contract["catalogue"]["counts"],
      f"counts={[len(c) for c in kotlin_codes]}")
check("E12E-02_family_ids_match_contract", enum_ids(catalogue, "ReasonCodeFamily") == contract["catalogue"]["families"],
      f"ids={enum_ids(catalogue, 'ReasonCodeFamily')}")
all_codes = {c for block in kotlin_codes for c in block}
written = set(re.findall(r'"((?:need|candidate|eligibility|retention|priority|capacity|diagnostic|reentry|selection|replan)\.[a-z0-9_]+)"',
                         planner + replan + facts))
written |= {c for c in re.findall(r'"([a-z_]+)"', planner + replan) if c in prg_inputs}
check("E12E-02_written_codes_in_catalogue", written and written <= all_codes, f"outside: {sorted(written - all_codes)}")
check("E12E-02_independent_branch_is_prg", "independent_branch_available" in prg_inputs
      and 'INDEPENDENT_BRANCH_AVAILABLE = "independent_branch_available"' in planner, "the un-namespaced code has no source")
recorded = body(catalogue, "fun PlanTrace.recordedReasonCodes(")
check("E12E-02_recorded_body_read", len(recorded) > 150, "the recordedReasonCodes reader returned nothing")
for fragment in ("addAll(planReasonCodes)", "add(need.trigger.reasonCode)", "addAll(need.priorityReasonCodes)",
                 "addAll(need.finalReasonCodes)", "candidates.forEach { addAll(it.reasonCodes) }",
                 "replan?.trigger?.reasonCode?.let(::add)"):
    check(f"E12E-02_recorded_{fragment[:32]}", fragment in recorded, f"missing {fragment}")

# ---------------------------------------------------------------- the trace: planner_trace/3 and related_refs
check("E12E-03_format", 'const val FORMAT = "planner_trace/3"' in codec and 'const val FORMAT_V2 = "planner_trace/2"' in codec
      and 'const val FORMAT_V1 = "planner_trace/1"' in codec and 'const val TRACE_SCHEMA = "planner_trace/3"' in planner,
      "the trace is not planner_trace/3 reading /2 and /1")
check("E12E-03_related_refs_field", re.search(r"val relatedSkills: List<VersionedRef>", facts) is not None,
      "CandidateTrace has no related_refs")
check("E12E-03_related_written", '"related_skills" to list(it.relatedSkills.map(VersionedRef::toString))' in codec,
      "related Skills are not written")
decode = body(codec, "private fun decodeOrThrow(")
check("E12E-03_decode_body_read", len(decode) > 1500, "the decode reader returned nothing")
check("E12E-03_older_cannot_claim_related",
      'relatedSkills = if (v3) items(v("related_skills")).map(::ref)' in decode
      and 'else if ("related_skills" in f) throw Malformed() else emptyList(),' in decode,
      "an older format may claim related Skills, or /3 does not require them")
check("E12E-03_known_formats_closed", "private val KNOWN_FORMATS = setOf(FORMAT, FORMAT_V2, FORMAT_V1)" in codec
      and "if (version !in KNOWN_FORMATS) throw Malformed()" in decode, "a format nobody wrote may be read")
related = body(planner, "private fun relatedSkills(")
check("E12E-03_related_body_read", len(related) > 150, "the relatedSkills reader returned nothing")
gate_rules = contract["trace"]["related_skills_by_gate_answer"]
for label, fragment in (
    ("blocked", "decision.eligibility == PrerequisiteEligibility.BLOCKED ->\n            decision.hardBlockerSkills.ifEmpty { decision.uncertainSkills }"),
    ("conditional_eligible", "decision.eligibility == PrerequisiteEligibility.CONDITIONAL_ELIGIBLE -> decision.uncertainSkills"),
    ("eligible_with_support", "decision.eligibility == PrerequisiteEligibility.ELIGIBLE_WITH_SUPPORT -> decision.softGapSkills"),
    ("eligible", "else -> decision.reviewDueSkills"),
):
    check(f"E12E-03_gate_answer_{label}", label in gate_rules and fragment in related, f"missing {label}")
plan_body = body(planner, "fun plan(")
check("E12E-03_plan_body_read", len(plan_body) > 3000, "the plan reader returned nothing")
check("E12E-03_every_trace_carries_related",
      plan_body.count("it.related)") + plan_body.count("choice.related)") + plan_body.count("smaller.related)") >= 7
      and "CandidateTrace(a.id, a.needKey, a.validationStatus, eligibility, a.costMinutes, disposition, reasons, related)" in plan_body,
      "a candidate trace drops the gate's related Skills")
check("E12E-03_unanswered_names_nothing",
      "decision == null ->\n                    Assessed(candidate, PrerequisiteEligibility.BLOCKED, false, CandidateDisposition.BLOCKED_PREREQUISITE,\n                        listOf(\"eligibility.blocked_hard_prerequisite\"))" in plan_body,
      "a candidate the gate never answered for names Skills it cannot know")
check("E12E-03_no_gate_re_run", "ResolvePrerequisites" not in explain_query + reading and "PrerequisiteEngine" not in explain_query + reading,
      "the explanation re-runs the gate")

# ---------------------------------------------------------------- reading the plan back
read_body = body(reading, "fun read(")
check("E12E-04_read_body_read", len(read_body) > 800, "the read reader returned nothing")
for label, fragment in (
    ("day_is_the_row_s", "val planDay = stored.recordedAt.studyDay\n        if (planDay != studyDay) return Read.FromAnotherDay(planDay)"),
    ("trace_present", 'val text = stored.traceText ?: return Read.Unreadable('),
    ("trace_decodes", "PlanTraceCodec.decode(text) ?: return Read.Unreadable("),
    ("same_study_day", "if (trace.studyDay != planDay) return Read.Unreadable("),
    ("same_positions_and_skills", "if (traced != rows) return Read.Unreadable("),
    ("no_position_twice", "if (traced.map { it.first }.toSet().size != traced.size) return Read.Unreadable("),
    ("need_decision", '?: return Read.Unreadable("a selected task has no need decision")'),
    ("chosen_by_its_need", "if (need.selectedCandidateId != entry.candidateId ||"),
):
    check(f"E12E-04_{label}", fragment in read_body, f"missing {fragment[:60]}")
check("E12E-04_checks_equal_contract", len(contract["plan_reading"]["checks"]) == 6, str(contract["plan_reading"]["checks"]))
check("E12E-04_trusted_read_only_by_read", "class Today internal constructor(val stored: StoredPlan, val trace: PlanTrace) : Read" in reading,
      "a trusted read can be constructed without the checks")
snapshot = body(reading, "fun snapshot(")
check("E12E-04_snapshot_body_read", len(snapshot) > 500, "the snapshot reader returned nothing")
for label, fragment in (
    ("row_points_at_stored_task", "plannedTaskId = ids.getValue(entry.position),"),
    ("planned_minutes", "estimatedMinutes = entry.plannedMinutes,"),
    ("kept_marked", "kept = entry.preserved,"),
    ("kept_not_justified", "traceFacts = if (entry.preserved) emptyList() else reasonFamilies("),
):
    check(f"E12E-04_{label}", fragment in snapshot, f"missing {fragment}")
capacity = body(reading, "fun capacity(")
check("E12E-04_capacity_body_read", len(capacity) > 300, "the capacity reader returned nothing")
for label, fragment in (
    ("day_budget_carried", "resolvedDailyMinutes = ReplanEngine.dayHardBudget(trace),"),
    ("override_said", "currentDayOverride = trace.capacity.source == CapacitySource.TODAY_OVERRIDE,"),
    ("remaining_excludes_kept", "estimatedRemainingPlannedMinutes = fresh.sumOf { it.plannedMinutes },"),
    ("recalculated_said", "planRecalculated = trace.replan != null,"),
    ("too_small_is_recorded", "tooSmallForAnyCandidate = fresh.isEmpty() && trace.needs.any {"),
):
    check(f"E12E-04_{label}", fragment in capacity, f"missing {fragment}")
check("E12E-04_too_small_reads_disposition", "it.disposition == NeedDisposition.ELIGIBLE_NOT_SELECTED && CAPACITY_DEFERRED in it.finalReasonCodes" in capacity,
      "'nothing fits' is derived rather than read from the planner's record")
load_body = body(query, "fun load(")
check("E12E-04_query_body_read", len(load_body) > 500, "the Today query reader returned nothing")
check("E12E-04_query_reads_through_reading", "PlanReading.read(stored, studyDay)" in load_body
      and "plan = PlanReading.snapshot(read)" in load_body and "capacity = PlanReading.capacity(read.trace)" in load_body,
      "Today does not read the plan through PlanReading")
check("E12E-04_unreadable_flag", "is PlanReading.Read.Unreadable -> TodayFacts(studyDay, curriculumLoaded = curriculumLoaded, planUnreadable = true)" in load_body,
      "an unreadable plan is not said to be unreadable")
check("E12E-04_stale_passes_only_the_day", "tasks = emptyList()" in load_body and "studyDay = read.planStudyDay" in load_body,
      "a plan from another day passes more than its day")
check("E12E-04_query_reads_only", not re.search(r"appendTruth|writeProjection|inTransaction", query + explain_query),
      "reading the plan writes")
latest = body(adapter, "override fun latestPlan(")
check("E12E-04_latest_body_read", len(latest) > 500, "the latestPlan reader returned nothing")
check("E12E-04_rows_in_position_order",
      '"SELECT id, position, skill_logical_id, skill_version FROM planned_task WHERE plan_version_id = ? ORDER BY position, id"' in latest,
      "the plan's rows do not come back in position order with their ids")
check("E12E-04_stored_plan_rows", "val plannedTasks: List<StoredPlannedTask>," in facts, "StoredPlan carries no rows")
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
check("E12E-04_port_count", sorted(interfaces) == sorted([p["id"] for p in msbx["ports"]["set"]] + declared_port_extensions(ROOT)), f"interfaces={interfaces}")
check("E12E-04_schema_version_unchanged", schema_versions_owned(schema), "the schema version moved without an owning contract")

# ---------------------------------------------------------------- Today's families, from trace facts
families = body(reading, "fun reasonFamilies(")
check("E12E-05_families_body_read", len(families) > 800, "the reasonFamilies reader returned nothing")
mapped: dict[str, str] = {}
for triggers, family in re.findall(r"((?:NeedTrigger\.\w+,?\s*)+)->\s*ReasonFamily\.(\w+)", families):
    for trigger in re.findall(r"NeedTrigger\.(\w+)", triggers):
        mapped[trigger.lower()] = family.lower()
expected = {k: v for k, v in contract["today_row_families"]["primary_by_trigger"].items() if k != "parallel_track_due"}
check("E12E-05_primary_by_trigger", {k: mapped.get(k) for k in expected} == expected,
      f"kotlin={mapped}")
check("E12E-05_parallel_track", "NeedTrigger.PARALLEL_TRACK_DUE ->\n                    if (english) ReasonFamily.PARALLEL_TECHNICAL_ENGLISH else ReasonFamily.CONTINUE_CURRENT_LEARNING" in families,
      "the parallel track's cadence is not the only way English becomes the reason")
check("E12E-05_paused_first", families.find("ContinuationValue.PAUSED_SAFE_CHECKPOINT -> ReasonFamily.RESUME_VALID_PAUSED_WORK")
      < families.find("when (need.trigger)") and "ContinuationValue.PAUSED_SAFE_CHECKPOINT -> ReasonFamily.RESUME_VALID_PAUSED_WORK" in families,
      "a continued safe pause is not the more specific fact")
check("E12E-05_weakness_is_verified", mapped.get("weakness_detected") == "verify_uncertain_state", "an unconfirmed weakness is called confirmed")
check("E12E-05_at_most_one_supporting", "return listOfNotNull(primary, supporting)" in families
      and contract["today_row_families"]["at_most_supporting"] == 1, "more than one supporting family can be shown")
check("E12E-05_fit_is_recorded", 'SPLIT_TO_FIT in need.finalReasonCodes || SMALLER_TO_FIT in need.finalReasonCodes' in families,
      "the fit is not read from the trace")
thux_families = [line.strip("- ,.").strip() for line in re.search(r"Examples of semantic reason families:\n(.*?)\n\n", thux, re.S).group(1).splitlines()]
check("E12E-05_thux_families_unchanged", len(thux_families) == 8 and len(enum_ids(today_facts, "ReasonFamily")) == 8,
      f"thux={thux_families} kotlin={enum_ids(today_facts, 'ReasonFamily')}")
label = re.search(r'ReasonFamily\.CONTINUE_CURRENT_LEARNING to "([^"]+)"', today_screen)
check("E12E-05_continuation_label_claims_no_start", label is not None and "Başlan" not in label.group(1) and "başlan" not in label.group(1),
      f"label={label and label.group(1)}")
check("E12E-05_kept_not_startable", "private fun startable(task: PlannedTaskFact): Boolean = !task.blocked && !task.kept" in today
      and today.count("filter(::startable)") >= 2, "started work is offered to start again")
of_body = body(today, "fun of(input: TodayInput)")
check("E12E-05_unreadable_is_a_fault", "if (input.planUnreadable) {\n            return TodayView(\n                state = TodayState.ERROR_RECOVERABLE," in of_body
      and of_body.find("if (input.planUnreadable)") < of_body.find("val plan = input.plan?.takeIf"),
      "an unreadable plan is rendered as something else")
check("E12E-05_unreadable_after_health_and_resume", 0 < of_body.find("input.health.blocking") < of_body.find("input.resumable?.takeIf")
      < of_body.find("if (input.planUnreadable)"), "an unreadable plan outranks recovery or a revalidated session")
attention = body(today, "private fun traceAttention(")
check("E12E-05_attention_body_read", len(attention) > 100, "the traceAttention reader returned nothing")
check("E12E-05_attention_from_trace",
      "if (facts.planReplaced) add(AttentionItem(AttentionFamily.PLAN_CHANGED, Surface.PlannerExplanation))" in attention
      and "if (facts.prerequisiteWaiting) add(AttentionItem(AttentionFamily.PREREQUISITE_BLOCKER, Surface.PlannerExplanation))" in attention
      and "attention = attention + traceAttention(facts)," in today,
      "trace-backed attention is missing or does not link to the explanation")

# ---------------------------------------------------------------- the explanation
check("E12E-06_statement_private", "class Statement private constructor(" in explain, "a statement can be built freely")
of_code = body(explain, "internal fun ofCode(")
check("E12E-06_statement_checks_trace_and_catalogue",
      "if (code in recorded && ReasonCatalog.isKnown(code)) Statement(code, null, skills) else null" in of_code,
      "a statement can be built for a code the trace did not record or no contract defines")
check("E12E-06_statement_no_free_text", not re.search(r"val (text|message|reason|note|label)\b", body(explain, "class Statement private constructor(") + explain[explain.find("class Statement"):explain.find("companion object")]),
      "a statement carries free text")
check("E12E-06_trace_facts", enum_ids(explain, "TraceFact") == contract["explanation"]["trace_facts"], f"kotlin={enum_ids(explain, 'TraceFact')}")
check("E12E-06_reconsideration", enum_ids(explain, "Reconsideration") == contract["explanation"]["reconsideration"],
      f"kotlin={enum_ids(explain, 'Reconsideration')}")
check("E12E-06_states", enum_ids(explain, "ExplanationState") == contract["explanation"]["states"], f"kotlin={enum_ids(explain, 'ExplanationState')}")
for name, key in (("SHOWN_PRIORITY", "shown_priority_codes"), ("SHOWN_ELIGIBILITY", "shown_eligibility_codes"), ("SHOWN_FIT", "shown_fit_codes")):
    block = re.search(rf"private val {name} = listOf\((.*?)\)", explain, re.S)
    # Digits included: every band code has one (`priority.p1_...`), and V24 found a reader without them
    # blind to exactly the codes this check forbids.
    shown = re.findall(r'"([a-z0-9_.]+)"', block.group(1)) if block else []
    check(f"E12E-06_{key}", shown == contract["explanation"][key] and set(shown) <= all_codes, f"kotlin={shown}")
    check(f"E12E-06_{key}_no_band", not any(re.match(r"priority\.p\d_", c) for c in shown), f"a band is shown: {shown}")
explain_body = body(explain, "private fun explain(")
check("E12E-06_explain_body_read", len(explain_body) > 1500, "the explain reader returned nothing")
check("E12E-06_kept_explained_as_kept", "if (entry.preserved) {" in explain_body
      and "why = Statement.ofFact(TraceFact.KEPT_FROM_EARLIER_VERSION), supporting = emptyList())" in explain_body,
      "kept work is justified again")
check("E12E-06_uncoded_replan_invents_nothing", "add(replan.trigger.reasonCode?.let { code(it) } ?: Statement.ofFact(TraceFact.PLAN_REPLACED))" in explain_body,
      "an event with no code gets one")
check("E12E-06_over_budget_said", 'if (trace.invariantChecks["day_within_hard_budget"] == false) add(Statement.ofFact(TraceFact.DAY_OVER_BUDGET_AFTER_KEEPING))' in explain_body,
      "kept work now over the day is not said")
check("E12E-06_reentry_explained", "ReasonCatalog.familyOf(it) == ReasonCodeFamily.REENTRY" in explain_body, "re-entry is not explained")
why_not = body(explain, "private fun whyNot(")
check("E12E-06_why_not_body_read", len(why_not) > 1200, "the whyNot reader returned nothing")
check("E12E-06_capacity_before_lower_priority_only_if_recorded",
      "listOf(LOWER_PRIORITY, CAPACITY_DEFERRED, NOT_SELECTED_CAPACITY)\n                    .firstOrNull { it in need.finalReasonCodes }?.let { code(it) }" in why_not,
      "deferral is called lower priority without a recorded code")
check("E12E-06_waiting_names_skills", "val skills = blocked.flatMap { it.relatedSkills }.distinct()" in why_not
      and 'c.reasonCodes.firstOrNull { it.startsWith("eligibility.blocked_") }' in why_not, "a waiting need does not name its blocker")
check("E12E-06_no_task_invents_no_code", "(said ?: Statement.ofFact(TraceFact.NO_TASK_FOR_NEED)) to Reconsideration.WHEN_A_TASK_IS_AVAILABLE" in why_not,
      "a need nothing serves gets an invented code")
check("E12E-06_uxia_questions", all(q in uxia for q in ("Why this task?", "Why not today?", "Why blocked?", "Why did the plan change?")),
      "UXIA-v0's planner_explanation questions were not read")

# ---------------------------------------------------------------- the words
template_block = re.search(r"val codeTemplates: Map<String, String> = linkedMapOf\((.*?)\n    \)", copy, re.S)
template_keys = re.findall(r'^\s+"([a-z0-9_.]+)" to "', template_block.group(1), re.M) if template_block else []
pdt_all = [c for block in pdt_codes for c in block] + prg_inputs
check("E12E-07_every_code_has_a_template", template_keys == pdt_all, f"missing={sorted(set(pdt_all) - set(template_keys))} extra={sorted(set(template_keys) - set(pdt_all))}")
sentences = [s for s in kotlin_strings(copy) if " " in s]
check("E12E-07_sentences_read", len(sentences) >= len(pdt_all), f"sentences={len(sentences)}")
FORBIDDEN_SUBSTRINGS = ("unuttun", "unutmuş", "borcun", "borçlu", "geride kaldın", "geri kaldın", "kaçırdın", "telafi",
                        "başarısız oldun", "tembel", "%")
FORBIDDEN_WORDS = {"skor", "puan", "yüzde", "streak", "seri", "yarın"}
bad = [(s, w) for s in sentences for w in FORBIDDEN_SUBSTRINGS if w in s]
bad += [(s, w) for s in sentences for w in re.findall(r"\w+", s) if w in FORBIDDEN_WORDS]
check("E12E-07_no_forbidden_claim", not bad, f"{bad[:3]}")
forgetting = [s for s in sentences if "unut" in s]
check("E12E-07_forgetting_only_denied", len(forgetting) == 1 and "unuttuğun anlamına gelmez" in forgetting[0], f"{forgetting}")
review = re.search(r'"need\.retention_review_due" to "([^"]+)"', copy)
check("E12E-07_review_due_is_pdt_12", review is not None and "unuttuğun anlamına gelmez" in review.group(1), "review due is not said to be not forgetting")
capacity_sentences = re.findall(r'"(?:capacity\.[a-z_]+|selection\.not_selected_capacity)" to "([^"]+)"', copy)
check("E12E-07_time_is_not_importance", capacity_sentences and not any("öncelik" in s or "önemsiz" in s for s in capacity_sentences),
      "deferral for time is worded as importance")
deferred = re.search(r'"capacity\.deferred_not_enough_time" to "([^"]+)"', copy)
check("E12E-07_deferred_not_debt", deferred is not None and "borç veya başarısızlık değildir" in deferred.group(1), "deferral is not said to be no debt")
for code, fragment in (("reentry.absence_not_failure", "başarısızlık değildir"), ("reentry.absence_not_task_debt", "borç olarak taşınmadı"),
                       ("reentry.stale_plan_not_replayed", "oynatılmadı")):
    said = re.search(rf'"{re.escape(code)}" to "([^"]+)"', copy)
    check(f"E12E-07_{code}", said is not None and fragment in said.group(1), f"{code}: {said and said.group(1)}")
check("E12E-07_no_date_promised", not any(re.search(r"\d", s) for s in sentences) and "tarih sözü değil" in copy, "a date is promised")
skill_block = re.search(r"val skillTemplates: Map<String, String> = linkedMapOf\((.*?)\n    \)", copy, re.S)
skill_keys = re.findall(r'"([a-z0-9_.]+)" to "[^"]*\{skills\}', skill_block.group(1)) if skill_block else []
check("E12E-07_blockers_named", {"eligibility.blocked_hard_prerequisite", "eligibility.blocked_critical_verification",
                                 "eligibility.blocked_strict_prerequisite_confidence"} <= set(skill_keys), f"skill templates={skill_keys}")
check("E12E-07_copy_in_core", "package coach.presentation" in copy_raw and "androidx" not in copy_raw, "the copy is not plain core code")
check("E12E-07_no_locale_naive_case", not re.search(r"\.(uppercase|lowercase|capitalize)\(\s*\)", copy + screen), "a locale-naive case transform")

# ---------------------------------------------------------------- the surface
check("E12E-08_edge_accepted", "NavigationGraph.canOpen(Destination.TODAY.id, surface.id)" in activity
      and 'Surface.PlannerExplanation' in activity, "the explanation opens outside the accepted edge")
check("E12E-08_projected_in_core", "PlannerExplanationPresentation.of(facts)" in activity
      and "PlannerExplanationPresentation.of(" not in screen, "the screen computes the explanation")
check("E12E-08_off_main_thread", "PlannerExplanationQuery" in application and "storeThread.execute" in body(application, "fun loadExplanation(")
      and "PlannerExplanationQuery" not in activity, "the explanation is read on the main thread")
check("E12E-08_screen_renders_only", "coach.persistence" not in screen and "coach.application" not in screen
      and "ExplanationCopy.text(" in screen and "minimumTouchTarget()" in screen, "the screen reaches the store or decides wording")
check("E12E-08_no_ranking_on_screen", not re.search(r"score|percent|rank|sortedBy|sortedWith", screen, re.I), "the screen ranks or scores")

# ---------------------------------------------------------------- tests
tests = {
    CATALOGUE_TEST: ["the catalogue is PDT-v0's ten families and PRG-v0's inputs, in their order",
                     "every code the planner's vocabulary names is in the catalogue",
                     "a trace's recorded codes are exactly the codes it recorded"],
    FACTS_TEST: ["a candidate's related Skills read back exactly",
                 "a trace 12D wrote still reads, and cannot claim related Skills its format never had"],
    PLANNER_TEST: ["a waiting candidate names the Skill it waits on, and one that went ahead names what it went ahead with",
                   "every code the planner writes is a contract code"],
    READING_TEST: ["Today reads today's plan from its trace, each row pointing at its own stored task",
                   "a plan from another study day is passed on only with its own day",
                   "kept work is part of today's plan but is not new work, and the replan is said",
                   "a trace that does not describe the stored rows is unreadable, and nothing is guessed",
                   "a split task shows the part planned for today, and its fit is the supporting reason",
                   "an initial plan, a replan and a re-entry record only contract codes",
                   "reading today's plan and its explanation writes nothing"],
    TODAY_TEST: ["kept work is not offered as new work and never fills the queue",
                 "an unreadable plan for today is a recoverable fault and renders nothing from it",
                 "a replan and a waiting prerequisite the trace records become attention that links to the explanation"],
    EXPLAIN_TEST: ["every statement comes from a code the trace recorded or from a trace fact",
                   "why today is the need first, then what moved or fitted it, and the band is never the reason",
                   "a need deferred for time is deferred for time, never called less important",
                   "a waiting need names the real Skill it waits on",
                   "a need nothing serves says so without inventing a code",
                   "review due is never forgetting",
                   "re-entry says absence is not failure or debt, and replays nothing"],
    COPY_TEST: ["every catalogue code has a sentence, in the catalogue's order, and nothing else does",
                "no sentence claims forgetting, debt, falling behind, a score or a percentage",
                "work deferred for time is never worded as less important"],
    T2_TEST: ["the newest plan's own tasks come back in position order, with their ids and Skills, and reading writes nothing"],
}
for path, names in tests.items():
    text = read(path)
    for name in names:
        check(f"E12E-09_{path.stem}_{name[:40]}", f"`{name}`" in text, f"missing test in {path.name}: {name}")

# ---------------------------------------------------------------- honesty
device = contract["device_verification"]
check("E12E-10_device_not_claimed", device["t6_run"] is False and device["claimed"] is False, "a device result is claimed")
mutation = contract["mutation_results"]
check("E12E-10_mutation_all_detected", mutation["detected"] == mutation["total"] == len(mutation.get("mutants", [])) >= 40,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E12E-10_mutation_ran_gradle", mutation.get("harness_verified_to_run_gradle") is True, "the harness was not verified")
check("E12E-10_mutation_negative_control", mutation.get("negative_control_result") == "survived_as_expected",
      "the harness was never shown able to report a survivor")
vm = contract["validator_mutation"]
check("E12E-10_validator_mutation", vm["detected"] == vm["total"] >= 25 and vm.get("negative_control_result") == "no_false_positive", str(vm))
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E12E-10_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
check("E12E-10_no_plan_in_app_claimed", contract["wiring"]["plan_exists_in_app_today"] is False, "a plan in the app is claimed")
for item, owner in (("virtual_user_scenarios_with_explanations", "12F"), ("retention_and_weakness_codes", 13),
                    ("final_microcopy_and_llm_paraphrase", 14), ("calling_the_planner_from_the_app", "16D")):
    check(f"E12E-10_re_pointed_{item[:30]}", str(contract["re_pointed"].get(item, {}).get("to")) == str(owner)
          and contract["re_pointed"][item].get("why"), f"{item} is not re-pointed with a reason")
narrowed = contract.get("living_gates_narrowed", [])
check("E12E-10_narrowed_gates_recorded",
      {g["check"] for g in narrowed} >= {"E11A-07_blocked_filtered", "E11A-11_no_invented_plan", "E11A-11_no_invented_capacity",
                                         "E12C-07_unknown_format_refused", "E12D-08_format_v2", "E12D-08_only_known_versions"}
      and all(g.get("guarantee_weakened") is False and g.get("why") for g in narrowed), str([g["check"] for g in narrowed]))
forbidden = set(contract.get("forbidden_explanation_patterns", []))
for pattern in ("reason_not_in_the_trace", "free_text_reason", "gate_re_run_to_explain", "band_shown_as_the_reason",
                "deferral_for_time_as_lower_priority", "waiting_without_its_skill_blocker", "review_due_as_forgetting",
                "absence_as_failure_or_debt", "english_as_the_reason_a_task_exists", "row_guessed_around_an_unreadable_trace",
                "started_work_offered_again"):
    check(f"E12E-11_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 12E QA PASS", "**Decision:** `D-096`",
                 "An explanation is a projection of the decision trace", "planner_trace/3", "T6", "MUTATION"]:
    present = fragment in spec_text if fragment != "MUTATION" else "MUTATION_SUMMARY_PLACEHOLDER" not in spec_text and "Mutation" in spec_text
    check(f"E12E-12_spec_{fragment[:26]}", present, f"spec: {fragment!r}")
for fragment in ["No web research pass was needed", "PDT-v0", "PRG-v0", "related_refs"]:
    check(f"E12E-13_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E12E-14_no_repeated_context_heading", len(context_headings) == len(set(context_headings)),
      f"repeated={[h for h in set(context_headings) if context_headings.count(h) > 1]}")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E12E-14_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)),
      f"repeated={sorted(h for h in set(plan_steps) if plan_steps.count(h) > 1)}")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "RSNX-v0", "stage_step": "12E", "decision": "D-096",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"12E_REASON_CODES_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
