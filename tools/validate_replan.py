"""Independent 12D QA — RPLX-v0 Replan.

The implementation is validated against the accepted contracts, not against its own. The generation
kinds are read out of `PDT-v0`'s text, the triggers out of D-033 §16, `PBR-v0` §17 and `PRG-v0` §19, the
reason codes out of `PDT-v0` §8.8 and §8.10, and the re-entry context out of `SRR-v0` §17 — each
compared with the Kotlin.

The checks that matter most are structural: an edited plan, an unexplained new version, a replayed old
plan, absence turned into pressure, and guessed kept work must all be **unrepresentable**.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/12d_replan/replan.yaml"
SPEC = ROOT / "docs/REPLAN_SPEC.md"
RESEARCH = ROOT / "research/12d_replan_research.md"
QA_OUT = ROOT / "arch/12d_replan/qa_report.yaml"

PDT = ROOT / "docs/PLANNER_EXPLAINABILITY_SPEC.md"
CAPACITY = ROOT / "docs/ADAPTIVE_PLANNER_SPEC.md"
PBR = ROOT / "docs/PRIORITY_POLICY_SPEC.md"
PRG = ROOT / "docs/PREREQUISITE_POLICY_SPEC.md"
SRR = ROOT / "docs/MISSED_DAY_RECOVERY_SPEC.md"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/ReplanEngine.kt"
PLANNER_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/PlannerEngine.kt"
FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/PlannerFacts.kt"
CODEC_KT = ANDROID / "core-model/src/main/kotlin/coach/PlanTraceCodec.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/BuildDailyPlan.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
ADAPTER_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"

FACTS_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/PlannerFactsTest.kt"
ENGINE_TEST = ANDROID / "core-engines/src/test/kotlin/coach/engines/ReplanEngineTest.kt"
APP_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/ReplanTest.kt"
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


def enum_block(source: str, name: str) -> str:
    match = re.search(rf"enum class {name}\b[^{{]*\{{(.*?)\n\}}", source, re.S)
    return match.group(1) if match else ""


def enum_ids(source: str, name: str) -> list[str]:
    return re.findall(r'\(\s*"([A-Za-z0-9_]+)"', enum_block(source, name))


def enum_names(source: str, name: str) -> list[str]:
    return re.findall(r"^\s+([A-Z][A-Z0-9_]+)\(", enum_block(source, name), re.M)


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


def constructor_params(source: str, cls: str) -> list[str]:
    match = re.search(rf"class {cls}\b[^(]*\((.*?)\n\)", source, re.S)
    return re.findall(r"val (\w+)\s*:", match.group(1)) if match else []


def text_block_after(text: str, anchor: str) -> list[str]:
    at = text.find(anchor)
    if at < 0:
        return []
    block = re.search(r"```text\n(.*?)```", text[at:], re.S)
    return [line.strip() for line in block.group(1).splitlines() if line.strip()] if block else []


def camel(snake: str) -> str:
    head, *rest = snake.split("_")
    return head + "".join(w.capitalize() for w in rest)


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


contract = load(CONTRACT)
msbx = load(MSBX)
pdt = read(PDT)

for path in (ENGINE_KT, FACTS_KT, CODEC_KT, APP_KT, FACTS_TEST, ENGINE_TEST, APP_TEST, T2_TEST, SPEC, RESEARCH):
    check(f"E12D-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

engine = strip_comments(read(ENGINE_KT))
planner = strip_comments(read(PLANNER_KT))
facts = strip_comments(read(FACTS_KT))
codec = strip_comments(read(CODEC_KT))
app = strip_comments(read(APP_KT))
ports = strip_comments(read(PORTS_KT))
adapter = strip_comments(read(ADAPTER_KT))
schema = read(SCHEMA_KT)
facts_test, engine_test, app_test, t2_test = read(FACTS_TEST), read(ENGINE_TEST), read(APP_TEST), read(T2_TEST)
spec_text, research_text = read(SPEC), read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E12D-01_model", contract.get("model") == "RPLX-v0", str(contract.get("model")))
check("E12D-01_status", contract.get("status") == "accepted_12d", str(contract.get("status")))
check("E12D-01_decision", contract.get("decision") == "D-095", str(contract.get("decision")))
for key in ("replan_implemented", "reentry_implemented", "paused_continuation_implemented"):
    check(f"E12D-01_scope_{key}", contract["scope"].get(key) is True, f"{key}={contract['scope'].get(key)}")
for key in ("skill_state_assembled", "recompute_after_attempt_implemented", "planner_called_from_app",
            "reverse_invalidation_implemented", "focus_preference_implemented", "reason_text_implemented",
            "schema_changed", "migration_added", "interfaces_added", "boundaries_changed"):
    check(f"E12D-01_scope_{key}", contract["scope"].get(key) is False, f"{key}={contract['scope'].get(key)}")
for key in ("absence_penalty_claim", "backlog_claim"):
    check(f"E12D-01_no_{key}", contract["scope"].get(key) is None, f"{key} was claimed")
check("E12D-01_model_constant", 'const val REPLAN_MODEL = "RPLX-v0"' in engine, "the replan does not name RPLX-v0")

# ---------------------------------------------------------------- generation kinds, read out of PDT-v0 §4
kinds_line = re.search(r"- generation_kind: ([a-z_ |]+)", pdt)
pdt_kinds = [k.strip() for k in kinds_line.group(1).split("|")] if kinds_line else []
check("E12D-02_kinds_read_from_pdt", pdt_kinds == ["initial", "replan", "reentry"], f"pdt={pdt_kinds}")
check("E12D-02_kinds_equal_pdt", enum_ids(facts, "GenerationKind") == pdt_kinds, f"kotlin={enum_ids(facts, 'GenerationKind')}")
classify = body(engine, "fun classify(")
check("E12D-02_classify_body_read", len(classify) > 100, "the classify reader returned nothing")
order = [classify.find(f) for f in ("previousStudyDay == null -> GenerationKind.INITIAL",
                                    "previousStudyDay != today -> GenerationKind.REENTRY",
                                    "else -> GenerationKind.REPLAN")]
check("E12D-02_classification", all(i >= 0 for i in order) and order == sorted(order), f"positions={order}")

# ---------------------------------------------------------------- triggers, read out of the three contracts
capacity_triggers = text_block_after(read(CAPACITY), "## 16. Replan triggers")
pbr_triggers = text_block_after(read(PBR), "# 17. Replan triggers")
prg_triggers = text_block_after(read(PRG), "# 19. Replan triggers")
check("E12D-03_triggers_read", len(capacity_triggers) == 8 and len(pbr_triggers) == 11 and len(prg_triggers) == 9,
      f"d033={len(capacity_triggers)} pbr={len(pbr_triggers)} prg={len(prg_triggers)}")
declared = set(capacity_triggers) | set(pbr_triggers) | set(prg_triggers)
kotlin_triggers = set(enum_names(facts, "ReplanTrigger"))
not_accepted = {t.upper() for t in contract["triggers"]["not_accepted"]}
check("E12D-03_triggers_are_the_contracts", kotlin_triggers == declared - not_accepted,
      f"missing={sorted(declared - not_accepted - kotlin_triggers)} extra={sorted(kotlin_triggers - declared)}")
check("E12D-03_focus_not_accepted", "USER_FOCUS_CHANGED" not in kotlin_triggers and not_accepted == {"USER_FOCUS_CHANGED"},
      "the focus event is accepted with nothing to act on")
check("E12D-03_trigger_ids_are_snake_names", all(i == n.lower() for n, i in zip(enum_names(facts, "ReplanTrigger"), enum_ids(facts, "ReplanTrigger"))),
      "a trigger id is not its own name")
pdt_replan_codes = text_block_after(pdt, "## 8.10 Replan")
trigger_codes = re.findall(r'\(\s*"[a-z_]+",\s*(?:"([a-z_.]+)"|null)\)', enum_block(facts, "ReplanTrigger"))
check("E12D-03_codes_read_from_pdt", len(pdt_replan_codes) == 13, f"pdt={pdt_replan_codes}")
check("E12D-03_every_code_is_pdt", {c for c in trigger_codes if c} <= set(pdt_replan_codes),
      f"not in PDT-v0: {sorted({c for c in trigger_codes if c} - set(pdt_replan_codes))}")
uncoded = [n.lower() for n, c in zip(enum_names(facts, "ReplanTrigger"), trigger_codes) if not c]
check("E12D-03_uncoded_events_named", uncoded == contract["triggers"]["uncoded_events"], f"uncoded={uncoded}")
sets_remaining = re.search(r"val setsRemainingTime: Boolean\s*get\(\) = ([^\n]+)", facts)
check("E12D-03_sets_remaining_time", sets_remaining is not None
      and sorted(re.findall(r"this == (\w+)", sets_remaining.group(1))) == sorted(t.upper() for t in contract["triggers"]["sets_remaining_time"]),
      str(sets_remaining and sets_remaining.group(1)))

# ---------------------------------------------------------------- the remaining budget (D-033 §8)
remainder = body(engine, "fun remainderMinutes(")
check("E12D-04_remainder_body_read", len(remainder) > 200, "the remainder reader returned nothing")
for label, fragment in (
    ("declared_remaining_is_the_remainder", "trigger.setsRemainingTime -> requireNotNull(declaredRemainingMinutes)"),
    ("capacity_change_replaces_the_day", "trigger == ReplanTrigger.TODAY_CAPACITY_CHANGED ->"),
    ("capacity_change_minus_kept", '"a capacity change names the new capacity" } - preservedMinutes).coerceAtLeast(0)'),
    ("other_events_keep_the_day", "else -> (dayHardBudget(previous) - preservedMinutes).coerceAtLeast(0)"),
    ("never_negative", 'require(it >= 0)'),
):
    check(f"E12D-04_{label}", fragment in remainder, f"missing {fragment}")
check("E12D-04_day_carried_across_replans",
      "trace.capacity.hardBudgetMinutes + (trace.replan?.preservedMinutes ?: 0)" in body(engine, "fun dayHardBudget("),
      "a chain of replans forgets the day's budget")
check("E12D-04_same_rule_as_a_day", "PlannerEngine.capacityOf(" in body(engine, "fun remainderCapacity(")
      and "return capacityOf(source, minutes)" in body(planner, "fun resolveCapacity("),
      "the remainder does not use the day's capacity rule")

# ---------------------------------------------------------------- kept work (TRUX-v0 §10.1)
compose = body(engine, "fun composeReplan(")
check("E12D-05_compose_body_read", len(compose) > 500, "the replan composer returned nothing")
for label, fragment in (
    ("kept_first_and_marked", "entry.copy(position = i, preserved = true)"),
    ("rest_after_kept", "it.copy(position = it.position + kept.size, preserved = false)"),
    ("unstarted_invalidated", "previous.selected.map { it.position }.filterNot { it in preservedPositions }"),
    ("trigger_code_recorded", "planReasonCodes = listOfNotNull(trigger.reasonCode) + fresh.planReasonCodes,"),
    ("day_budget_checked", '"day_within_hard_budget" to (keptMinutes + fresh.selected.sumOf { it.plannedMinutes } <= dayHardMinutes),'),
):
    check(f"E12D-05_{label}", fragment in compose, f"missing {fragment}")
check("E12D-05_kept_need_not_served_twice", "return needs.filterNot { it.needKey in served }" in body(engine, "fun unservedNeeds("),
      "a need a kept task serves is served again")
build = body(app, "fun build(")
check("E12D-05_build_body_read", len(build) > 2000, "the build reader returned nothing")
for label, fragment in (
    ("same_day_no_event_writes_nothing", "if (replan == null) return Built.AlreadyPlanned(previous!!.planVersionId)"),
    ("unreadable_previous_refused", 'if (previousTrace == null) return Built.Refused('),
    ("unknown_kept_refused", "if (!known.containsAll(replan.keptPositions)) {"),
    ("missing_remaining_refused", "if (replan.trigger.setsRemainingTime && replan.remainingMinutes == null) {"),
    ("kept_only_on_a_replan", "val kept = if (kind == GenerationKind.REPLAN) {"),
):
    check(f"E12D-05_{label}", fragment in build, f"missing {fragment}")
refusals = [build.find(f) for f in ("Built.AlreadyPlanned(", "Built.Refused(")]
check("E12D-05_refusals_before_writing", all(0 <= i < build.find("inTransaction") for i in refusals), f"positions={refusals}")
check("E12D-05_one_transaction", build.count("inTransaction") == 1, "a plan version is not written in one transaction")

# ---------------------------------------------------------------- re-entry (SRR-v0)
reentry = body(engine, "fun composeReentry(")
check("E12D-06_reentry_body_read", len(reentry) > 500, "the re-entry composer returned nothing")
pdt_reentry_codes = text_block_after(pdt, "## 8.8 Re-entry")
declared_reentry = re.findall(r'const val REENTRY_\w+ = "([a-z_.]+)"', engine) + re.findall(r'const val REPLAN_RETURN = "([a-z_.]+)"', engine)
check("E12D-06_codes_read_from_pdt", len(pdt_reentry_codes) == 8, f"pdt={pdt_reentry_codes}")
check("E12D-06_codes_are_pdt", set(declared_reentry) <= set(pdt_reentry_codes) | set(pdt_replan_codes)
      and set(declared_reentry) == set(contract["reentry"]["codes"]), f"kotlin={declared_reentry}")
for code in ("REENTRY_NOT_FAILURE", "REENTRY_NOT_DEBT", "REENTRY_STALE_NOT_REPLAYED", "REENTRY_REGENERATED"):
    check(f"E12D-06_always_says_{code.lower()}", f"            add({code})\n" in reentry, f"{code} is not always recorded")
srr_fields = text_block_after(read(SRR), "# 17. Recovery event")
check("E12D-06_context_read_from_srr", len(srr_fields) >= 10, f"srr={srr_fields}")
context_params = constructor_params(facts, "ReentryContext")
check("E12D-06_context_equals_contract", [camel(f) for f in contract["reentry"]["context_fields"]] == context_params,
      f"kotlin={context_params}")
for field in ("stale_planned_task_count", "open_need_count_by_trigger", "p0_p1_need_count", "resolved_daily_capacity_minutes"):
    check(f"E12D-06_srr_field_{field}", any(field in line for line in srr_fields) and camel(field) in context_params,
          f"{field} is not carried")
FORBIDDEN = ("score", "penalty", "debt", "streak", "fail", "missed", "decay", "percent")
for cls in ("ReentryContext", "ReplanRecord"):
    fields = constructor_params(facts, cls)
    check(f"E12D-06_no_verdict_field_{cls}", fields and not [f for f in fields if any(w in f.lower() for w in FORBIDDEN)],
          f"{cls} fields={fields}")
check("E12D-06_absence_feeds_nothing", "starvation" not in engine.lower() and "starvation" not in app.lower(),
      "absence reaches starvation")
check("E12D-06_absence_days_only_recorded", engine.count("absenceStudyDays(") == 2, "absence days are used for something")

# ---------------------------------------------------------------- paused work (SRR-v0 §5)
paused = body(engine, "fun withPausedWork(")
check("E12D-07_paused_body_read", len(paused) > 300, "the paused-work reader returned nothing")
for label, fragment in (
    ("latest_pause_wins", ".mapValues { it.value.last() }"),
    ("high_stakes_not_continued", "pause.kind == CheckpointKind.HIGH_STAKES_PAUSE -> { highStakes += 1; need }"),
    ("only_continuation_becomes_paused", "need.trigger == NeedTrigger.CONTINUE_LEARNING ->"),
    ("paused_is_a_signal", "need.copy(continuation = ContinuationValue.PAUSED_SAFE_CHECKPOINT)"),
):
    check(f"E12D-07_{label}", fragment in paused, f"missing {fragment}")
check("E12D-07_pause_never_opens_a_need", "val updated = needs.map { need ->" in paused,
      "a pause adds a need of its own instead of marking an open one")
check("E12D-07_undecodable_pause_ignored", "mapNotNull { row -> row.record.payload[\"context\"]?.let(ResumeContextCodec::decode) }" in build,
      "a pause that does not decode is treated as one")

# ---------------------------------------------------------------- the trace
# 12D owns /2 and what it carries; 12E moved the written format to /3 (candidates' related Skills). The
# check was narrowed from "the format is /2" to what 12D decided: the written format is /2 or later, the
# planner and the codec name the same one, and /2 and /1 both still read.
written_format = re.search(r'const val FORMAT = "planner_trace/(\d+)"', codec)
check("E12D-08_format_v2", written_format is not None and int(written_format.group(1)) >= 2
      and 'const val FORMAT_V1 = "planner_trace/1"' in codec
      and (written_format.group(1) == "2" or 'const val FORMAT_V2 = "planner_trace/2"' in codec)
      and f'const val TRACE_SCHEMA = "planner_trace/{written_format.group(1)}"' in planner,
      "the trace format is not planner_trace/2 or later reading /2 and /1")
decode = body(codec, "private fun decodeOrThrow(")
check("E12D-08_decode_body_read", len(decode) > 1500, "the decode reader returned nothing")
known_formats = re.search(r"private val KNOWN_FORMATS = setOf\(([^)]*)\)", codec)
check("E12D-08_only_known_versions",
      "if (version != FORMAT && version != FORMAT_V1) throw Malformed()" in decode
      or ("if (version !in KNOWN_FORMATS) throw Malformed()" in decode and known_formats is not None
          and sorted(x.strip() for x in known_formats.group(1).split(",")) == ["FORMAT", "FORMAT_V1", "FORMAT_V2"]),
      "a format nobody wrote is accepted")
for label, fragment in (
    ("v1_cannot_claim_kept", 'else if ("preserved" in f) throw Malformed() else false'),
    ("v1_cannot_claim_replan", "if (!v2 || replan != null) throw Malformed()"),
    ("v1_cannot_claim_reentry", "if (!v2 || reentry != null) throw Malformed()"),
):
    check(f"E12D-08_{label}", fragment in decode, f"missing {fragment}")
check("E12D-08_kept_flag_written", '"preserved" to it.preserved.toString()' in codec, "the kept flag is not written")

# ---------------------------------------------------------------- ports and storage
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
check("E12D-09_port_count", sorted(interfaces) == sorted(p["id"] for p in msbx["ports"]["set"]), f"interfaces={interfaces}")
check("E12D-09_latest_plan", "fun latestPlan(): StoredPlan?" in ports, "latestPlan is missing")
check("E12D-09_checkpoint_rows", "fun resumeCheckpointRows(): List<StoredTruth>" in ports, "resumeCheckpointRows is missing")
check("E12D-09_raw_trace_returned", "val traceText: String?" in facts and "PlanTraceCodec" not in adapter,
      "the adapter interprets the trace")
latest = body(adapter, "override fun latestPlan(")
check("E12D-09_latest_body_read", len(latest) > 300, "the latestPlan reader returned nothing")
# The trace query also orders by sequence, so the check names the plan query itself (V31 found the
# looser form passing while the plan query was reversed).
check("E12D-09_newest_by_sequence", '"SELECT id FROM plan_version ORDER BY sequence DESC LIMIT 1"' in latest,
      "the newest plan is not the newest")
check("E12D-09_count_is_the_plans_own", '"SELECT COUNT(*) FROM planned_task WHERE plan_version_id = ?"' in latest,
      "the task count is not the plan's own")
check("E12D-09_checkpoints_oldest_first", '"SELECT id FROM resume_checkpoint ORDER BY sequence"' in adapter,
      "pauses do not come back oldest first")
check("E12D-09_reads_only", not re.search(r"\b(INSERT|UPDATE|DELETE)\b", latest), "reading the newest plan writes")
check("E12D-09_schema_version_unchanged", "const val VERSION = 2" in schema, "the schema version moved")

# ---------------------------------------------------------------- tests
for name in ["a replan and a re-entry read back exactly",
             "a trace 12C wrote still reads, and cannot claim what its format never had",
             "re-entry records nothing that is a score, a penalty or a debt"]:
    check(f"E12D-10_facts_test_{name[:42]}", f"`{name}`" in facts_test, f"missing test: {name}")
for name in ["a plan from another study day is never today's plan",
             "only the remainder is solved again, and replanning never grows the day by itself",
             "a safe pause makes continuation paused work, and a high-stakes pause is not resumed",
             "a replan keeps started work first and solves only the rest",
             "an event PDT-v0 has no code for gets none",
             "re-entry does not replay the old plan and records what the return looked like",
             "absence is only recorded, never turned into pressure"]:
    check(f"E12D-10_engine_test_{name[:42]}", f"`{name}`" in engine_test, f"missing test: {name}")
for name in ["asked again on the same day with no event, the planner keeps today's plan",
             "a replan keeps what was started and solves only the rest",
             "a smaller capacity today never undoes work already done",
             "a replan that cannot be made honestly is refused and writes nothing",
             "a new study day is re-entry, and yesterday's plan is not replayed",
             "a safe pause makes its need paused work, and a high-stakes pause is not resumed"]:
    check(f"E12D-10_app_test_{name[:42]}", f"`{name}`" in app_test, f"missing test: {name}")
for name in ["the newest plan comes back with its own day, its task count and its trace",
             "stored pauses come back oldest first, exactly as written, and reading them writes nothing"]:
    check(f"E12D-10_t2_test_{name[:42]}", f"`{name}`" in t2_test, f"missing T2 test: {name}")

# ---------------------------------------------------------------- honesty
device = contract["device_verification"]
check("E12D-11_device_not_claimed", device["t6_run"] is False and device["claimed"] is False, "a device result is claimed")
mutation = contract["mutation_results"]
check("E12D-11_mutation_all_detected", mutation["detected"] == mutation["total"] == len(mutation.get("mutants", [])) >= 30,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E12D-11_mutation_ran_gradle", mutation.get("harness_verified_to_run_gradle") is True, "the harness was not verified")
check("E12D-11_mutation_negative_control", mutation.get("negative_control_result") == "survived_as_expected",
      "the harness was never shown able to report a survivor")
vm = contract["validator_mutation"]
check("E12D-11_validator_mutation", vm["detected"] == vm["total"] >= 25 and vm.get("negative_control_result") == "no_false_positive",
      str(vm))
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E12D-11_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
check("E12D-11_not_reachable_in_app", contract["wiring"]["reachable_in_app_today"] is False, "the replan is claimed reachable")
for item, owner in (("skill_state_assembly_under_one_watermark", 13), ("recompute_chain_after_an_attempt", 15),
                    ("calling_the_planner_from_the_app", "16D"), ("reverse_invalidation_of_dependents", "18E"),
                    ("attempt_to_planned_task_link", 15)):
    check(f"E12D-11_re_pointed_{item[:30]}", str(contract["re_pointed"].get(item, {}).get("to")) == str(owner)
          and contract["re_pointed"][item].get("why"), f"{item} is not re-pointed with a reason")
gates = {g["check"] for g in contract.get("living_gates_narrowed", [])}
check("E12D-11_narrowed_gates_recorded", any("E12C-07_trace_format" in g for g in gates) and any("E12C-02" in g for g in gates)
      and all(g.get("guarantee_weakened") is False for g in contract["living_gates_narrowed"]), str(gates))
forbidden = set(contract.get("forbidden_replan_patterns", []))
for pattern in ("plan_edited_in_place", "new_version_without_a_reason", "old_plan_replayed_after_absence",
                "absence_as_debt_or_failure", "absence_feeding_starvation", "in_flight_work_destroyed_by_replan",
                "paused_work_selected_automatically", "invented_reason_code_for_an_event", "unreported_kept_work_guessed"):
    check(f"E12D-12_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 12D QA PASS", "**Decision:** `D-095`",
                 "A plan is replaced by a new version with a reason, never edited", "T6", "planner_trace/2", "MUTATION"]:
    present = fragment in spec_text if fragment != "MUTATION" else "MUTATION_SUMMARY_PLACEHOLDER" not in spec_text
    check(f"E12D-13_spec_{fragment[:26]}", present, f"spec: {fragment!r}")
for fragment in ["No web research pass was needed", "SRR-v0", "reported", "re-pointed"]:
    check(f"E12D-14_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E12D-15_no_repeated_context_heading", len(context_headings) == len(set(context_headings)),
      f"repeated={[h for h in set(context_headings) if context_headings.count(h) > 1]}")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E12D-15_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)),
      f"repeated={sorted(h for h in set(plan_steps) if plan_steps.count(h) > 1)}")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "RPLX-v0", "stage_step": "12D", "decision": "D-095",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"12D_REPLAN_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
