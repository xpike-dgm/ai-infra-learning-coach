"""Independent 13C QA — RVRX-v0 Retention Verification & Risk Implementation.

The axis (§2), the V0 table (§20) and the reason codes (§17) are read out of `docs/RETENTION_FORGETTING_SPEC.md`
itself and compared with the real Kotlin, never with 13C's own contract alone.

The checks that matter most are structural: mastery decaying with time, a due review read as forgetting, a
lock or a risk raised by a day passing, a number not in the accepted spec, a near repeat counted as a strong
review, a recheck that grows the interval, and a `skill_state` row claiming newer truth than its oldest axis
must be **unrepresentable** as a PASS here.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/13c_spaced_repetition/retention.yaml"
SPEC = ROOT / "docs/RETENTION_IMPL_SPEC.md"
RESEARCH = ROOT / "research/13c_spaced_repetition_research.md"
QA_OUT = ROOT / "arch/13c_spaced_repetition/qa_report.yaml"
RVR = ROOT / "docs/RETENTION_FORGETTING_SPEC.md"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/RetentionFacts.kt"
AXIS_KT = ANDROID / "core-model/src/main/kotlin/coach/PrerequisiteFacts.kt"
EVIDENCE_KT = ANDROID / "core-model/src/main/kotlin/coach/EvidenceFacts.kt"
ENGINE_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/RetentionEngine.kt"
PLANNER_KT = ANDROID / "core-engines/src/main/kotlin/coach/engines/PlannerEngine.kt"
APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildRetention.kt"
MASTERY_APP_KT = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildMastery.kt"
BUILD_KT = ANDROID / "core-application/src/main/kotlin/coach/application/BuildDailyPlan.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
SQL_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"

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
    """The balanced parenthesised text that starts at the signature's first `(`."""
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


def section(text: str, start: str, end: str) -> str:
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    return text[a:b] if a >= 0 and b > a else ""


def code_block_lines(text: str) -> list[str]:
    blocks = re.findall(r"```text\n(.*?)```", text, re.S)
    return [line.strip() for line in blocks[0].splitlines() if line.strip()] if blocks else []


for path in (CONTRACT, SPEC, RESEARCH, FACTS_KT, ENGINE_KT, APP_KT):
    check(f"E13C-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

contract = yaml.safe_load(read(CONTRACT)) or {}
rvr = read(RVR)
facts, axis_kt, evidence_kt = strip_comments(read(FACTS_KT)), strip_comments(read(AXIS_KT)), strip_comments(read(EVIDENCE_KT))
engine, planner, app = strip_comments(read(ENGINE_KT)), strip_comments(read(PLANNER_KT)), strip_comments(read(APP_KT))
mastery_app, build, ports = strip_comments(read(MASTERY_APP_KT)), strip_comments(read(BUILD_KT)), strip_comments(read(PORTS_KT))
schema, migrations, sql = strip_comments(read(SCHEMA_KT)), strip_comments(read(MIGRATIONS_KT)), strip_comments(read(SQL_KT))
spec_text, research_text = read(SPEC), read(RESEARCH)

# ---------------------------------------------------------------- contract head
check("E13C-01_model", contract.get("model") == "RVRX-v0", str(contract.get("model")))
check("E13C-01_status", contract.get("status") == "accepted_13c", str(contract.get("status")))
check("E13C-01_decision", contract.get("decision") == "D-101", str(contract.get("decision")))
check("E13C-01_semantics", contract.get("retention_semantics") == "RVR-v0", str(contract.get("retention_semantics")))
scope = contract.get("scope", {})
for key, expected in {"retention_axis_written": True, "rebuilt_from_evidence": True, "day_driven_refresh": True,
                      "planner_refreshes_before_planning": True, "schema_changed": True, "migration_added": True,
                      "interfaces_added": False, "boundaries_changed": False, "planner_rule_changed": False,
                      "gate_rule_changed": False, "mastery_rule_changed": False, "remediation_implemented": False,
                      "topic_weakening_implemented": False, "app_calls_rebuild": False}.items():
    check(f"E13C-01_scope_{key}", scope.get(key) is expected, f"{key}={scope.get(key)}")
check("E13C-01_rvr_principle", "Zamanın geçmesi negatif evidence değildir." in rvr and "`review_due`: review zamanı geldi/geçti, fakat negatif evidence yok. **Unutuldu demek değildir.**" in rvr,
      "RVR-v0 principle moved")

# ---------------------------------------------------------------- the axis, read from RVR-v0 §2
rvr_axis = code_block_lines(section(rvr, "### Retention ekseni", "- `untracked`"))
kotlin_axis = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\),', body(axis_kt, "enum class RetentionAxis"), re.M)
check("E13C-02_axis_from_rvr", rvr_axis == ["untracked", "fresh", "stable", "review_due", "verification_due", "at_risk"], str(rvr_axis))
check("E13C-02_axis_equals_rvr", kotlin_axis[:6] == rvr_axis and kotlin_axis[6:] == ["not_yet_evaluated"], str(kotlin_axis))
check("E13C-02_axis_contract", contract.get("axis", {}).get("values") == rvr_axis and contract.get("axis", {}).get("plus_unwritten") == "not_yet_evaluated", "contract axis")
stored_states = re.search(r'const val RETENTION_STATES = "([^"]+)"', schema)
check("E13C-02_schema_value_set", stored_states is not None and
      [s.strip(" '") for s in stored_states.group(1).split(",")] == rvr_axis + ["not_yet_evaluated"], "stored value set drifted")
check("E13C-02_remediation_not_axis", "remediation_required" not in kotlin_axis and "`remediation_required` retention state değil" in rvr, "remediation is a retention state")

# ---------------------------------------------------------------- V0 numbers, read from RVR-v0 §20
table = dict(re.findall(r"^\| ([a-z ]+) \| ([^|]+) \| engineering heuristic / calibrate \|$", section(rvr, "## 20. V0 config", "## 21."), re.M))
def num(text: str) -> float:
    m = re.search(r"([\d.]+)", text)
    return float(m.group(1)) if m else -1.0
check("E13C-03_table_read", len(table) == 9, f"rows={sorted(table)}")
initial = body(facts, "fun initialReviewDays(")
for name, constant in (("factual initial", "RetentionProfile.FACTUAL -> "), ("standard initial", "RetentionProfile.STANDARD -> "),
                       ("complex initial", "RetentionProfile.COMPLEX -> ")):
    m = re.search(re.escape(constant) + r"(\d+)", initial)
    check(f"E13C-03_{name.replace(' ', '_')}", m is not None and float(m.group(1)) == num(table.get(name, "")), f"{name}: {m and m.group(1)} vs {table.get(name)}")
for name, constant in (("critical initial cap", "CRITICAL_INITIAL_REVIEW_CAP_DAYS"), ("standard growth", "STANDARD_GROWTH_FACTOR"),
                       ("critical growth", "CRITICAL_GROWTH_FACTOR"), ("standard max", "STANDARD_MAX_INTERVAL_DAYS"),
                       ("critical max", "CRITICAL_MAX_INTERVAL_DAYS"), ("verification separation", "VERIFICATION_DELAY_DAYS")):
    m = re.search(r"const val " + constant + r" = ([\d.]+)", facts)
    check(f"E13C-03_{name.replace(' ', '_')}", m is not None and float(m.group(1)) == num(table.get(name, "")), f"{name}: {m and m.group(1)} vs {table.get(name)}")
policy = contract.get("policy_v0", {})
check("E13C-03_contract_numbers", policy.get("initial_review_days") == {"factual": 2, "standard": 4, "complex": 7}
      and policy.get("critical_initial_cap_days") == 3 and policy.get("growth_factor") == {"standard": 2.0, "critical": 1.6}
      and policy.get("max_interval_days") == {"standard": 180, "critical": 90} and policy.get("verification_separation_days") == 1, str(policy))
check("E13C-03_heuristic_labelled", "engineering heuristic that\n * needs calibration" in read(FACTS_KT) or "engineering heuristic" in read(FACTS_KT),
      "V0 numbers not labelled")
check("E13C-03_calibration_owner", policy.get("calibration_owner") == "18C" and policy.get("status") == "engineering_heuristic_needs_calibration"
      and policy.get("probability_or_half_life") is False, str(policy))
check("E13C-03_no_other_numbers", sorted(set(re.findall(r"const val [A-Z_]+ = ([\d.]+)", facts))) == sorted({"3", "2.0", "1.6", "180", "90", "1"}),
      f"{re.findall(r'const val [A-Z_]+ = ([0-9.]+)', facts)}")
grow = body(facts, "fun grownInterval(")
check("E13C-03_rounds_down", "kotlin.math.floor(current * factor)" in grow and "minOf(" in grow and policy.get("rounding") == "down", "rounding")
check("E13C-03_critical_cap_applied", "if (critical) minOf(base, CRITICAL_INITIAL_REVIEW_CAP_DAYS) else base" in facts, "critical cap")
check("E13C-03_no_probability", not re.search(r"(?i)probab|half_?life|decay|forget(ting)?Curve|exp\(", facts + engine), "a probability or decay in retention code")

# ---------------------------------------------------------------- reason codes, read from RVR-v0 §17
rvr_codes = code_block_lines(section(rvr, "## 17. Explainability reason codes", "## 18."))
kotlin_codes = re.findall(r'^\s+[A-Z_]+\("([A-Z_]+)"\),', body(facts, "enum class RetentionReason"), re.M)
check("E13C-04_codes_from_rvr", len(rvr_codes) == 11, str(rvr_codes))
check("E13C-04_codes_equal_rvr_in_order", kotlin_codes == rvr_codes, str(kotlin_codes))
check("E13C-04_codes_contract", contract.get("reason_codes", {}).get("codes") == rvr_codes, "contract codes")
check("E13C-04_not_in_planner_catalog", "RETENTION_FAILURE_FIRST" not in read(ANDROID / "core-model/src/main/kotlin/coach/ReasonCodes.kt"),
      "RVR codes leaked into the closed planner catalogue")

# ---------------------------------------------------------------- time
axis_on = body(facts, "fun axisOn(")
check("E13C-05_only_schedule_moves", "if ((state == RetentionAxis.FRESH || state == RetentionAxis.STABLE) && isDueOn(today)) RetentionAxis.REVIEW_DUE" in axis_on
      and "else state" in axis_on, "time moves more than the schedule")
check("E13C-05_due_inclusive", "!LocalDate.parse(today).isBefore(LocalDate.parse(nextReviewDay))" in body(facts, "fun isDueOn("), "due day exclusive")
check("E13C-05_replay_never_stores_due", "state = RetentionAxis.REVIEW_DUE" not in engine, "the replay stores review_due")
check("E13C-05_engine_has_no_clock", not re.search(r"System\.currentTimeMillis|LocalDate\.now|Instant\.now|ClockPort|Clock\b", engine + facts), "the engine reads a clock")
check("E13C-05_days_not_instants", "LocalDate.parse(studyDay)" in body(facts, "object StudyDays") and "Instant" not in body(facts, "object StudyDays"), "days from instants")
check("E13C-05_rvr_no_decay", "GRE-v0 current mastery score sırf `N gün geçti` diye düşmez" in rvr, "§1 moved")

# ---------------------------------------------------------------- transitions
step = body(engine, "fun step(")
check("E13C-06_mastery_from_events", "if (!event.masteredBefore)" in step and "if (!event.masteredAfter)" in step, "mastery not read from events")
check("E13C-06_lost_is_untracked", "RetentionAxis.UNTRACKED" in body(engine, "if (!event.masteredAfter)"), "lost mastery keeps a schedule")
fresh = body(engine, "private fun startFresh(")
check("E13C-06_fresh_initial", "RetentionPolicyV0.initialInterval(profile, state.critical)" in fresh and "RetentionAxis.FRESH" in fresh, "fresh not from the initial interval")
check("E13C-06_unknown_profile", "RetentionAxis.NOT_YET_EVALUATED" in fresh and "?: return" in fresh, "an unknown profile is scheduled")
sched = body(engine, "private fun scheduled(")
check("E13C-06_first_failure", "cleanNegative(event) -> state.copy(\n                state = RetentionAxis.VERIFICATION_DUE," in sched
      and "RetentionReason.RETENTION_FAILURE_FIRST" in sched, "a first failure is not verification")
check("E13C-06_due_review_grows", "strongPositive(state, event) && due ->" in sched and "RetentionPolicyV0.grownInterval(" in sched, "a due review does not grow")
reuse = sched[sched.find("            strongPositive(state, event) -> state.copy("):sched.find("uncertain(event) && due")]
check("E13C-06_reuse_keeps_clock", "lastNaturalReuseDay" in reuse and "nextReviewDay" not in reuse and "intervalDays" not in reuse, "early reuse moves the clock")
check("E13C-06_risk_only_on_due", "uncertain(event) && due -> state.copy(" in sched, "risk off schedule")
verifying = body(engine, "private fun verifying(")
check("E13C-06_separation", "if (!separated) return state" in verifying and "RetentionPolicyV0.VERIFICATION_DELAY_DAYS" in verifying, "no separation")
check("E13C-06_recheck_fresh", "!event.nearRepeat -> recheckPassed(state, event)" in verifying and "!event.nearRepeat -> recheckPassed(state, event)" in body(engine, "private fun atRisk("),
      "a near repeat resolves a failure")
passed = body(engine, "private fun recheckPassed(") or parens(engine, "private fun recheckPassed(")
recheck_text = engine[engine.find("private fun recheckPassed("):]
check("E13C-06_recheck_no_growth", "grownInterval" not in recheck_text and "state.intervalDays!!" in recheck_text, "a recheck grows the interval")
strong = body(engine, "private fun strongPositive(") or engine[engine.find("private fun strongPositive("):engine.find("private fun uncertain(")]
check("E13C-06_near_repeat_complex_critical", "!(event.nearRepeat && (state.profile == RetentionProfile.COMPLEX || state.critical))" in strong, "near repeat rule")
clean = facts[facts.find("val clean: Boolean"):facts.find("data class RetentionSnapshot")]
for cond in ("!contested", "prerequisiteValid", "!solutionExposed", "direct", "evaluatorStatus == EvaluatorStatus.VERIFIED",
             "independence == IndependenceClass.INDEPENDENT"):
    check(f"E13C-07_clean_{cond[:24]}", cond in clean, f"clean lacks {cond}")
check("E13C-07_rvr_flashcard", "Kritik production Skill'i flashcard ile retention-pass yapılamaz." in rvr and "Automatic cluster/descendant refresh yoktur." in rvr, "§4/§12 moved")

# ---------------------------------------------------------------- application
rebuild = body(app, "fun rebuild(")
check("E13C-08_watermark_first", 0 <= rebuild.find("persistence.truthWatermark()") < rebuild.find("persistence.evidenceFor("), "watermark read after evidence")
check("E13C-08_day_required", "require(rows.all { it.second.studyDay != null })" in rebuild, "a row with no day is placed")
check("E13C-08_no_truth_written", "appendTruth" not in app, "retention writes truth")
events = body(app, "private fun events(")
for needle in ("previouslyMastered = previous in MASTERED,", "unresolvedVerification = previous == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE,",
               "previouslyMastered = previous == MasteryAxisState.CONFIRMED_CURRENT,"):
    check(f"E13C-08_replay_mirrors_{needle[:28]}", needle in events, f"replay differs: {needle}")
check("E13C-08_mastery_rebuild_same_rule", "previouslyMastered = previous == MasteryAxisState.CONFIRMED_CURRENT ||" in mastery_app
      and "unresolvedVerification = previous == MasteryAxisState.CONFIRMATION_VERIFICATION_DUE" in mastery_app, "RebuildMastery's rule moved")
check("E13C-08_near_repeat_computed", "row.resource != null && other.resource == row.resource" in events and "other.variantFamilyId == row.variantFamilyId" in events, "near repeat not computed")
check("E13C-08_direct_computed", "directEvidenceTypes?.contains(row.evidenceType) == true" in events, "directness not computed")
write = body(app, "fun write(")
check("E13C-08_skill_state_min_watermark", "minOf(existing?.truthWatermark ?: truthWatermark, truthWatermark)" in write, "skill_state claims newer truth")
for axis in ("mastery_axis_state", "prerequisite_axis_state", "weakness_axis_state"):
    check(f"E13C-08_carries_{axis}", f'"{axis}" to carried("{axis}")' in write, f"{axis} overwritten")
check("E13C-08_retention_axis_written", '"retention_axis_state" to axis.id' in write, "axis not written")
refresh = body(app, "fun refresh(")
check("E13C-08_refresh_keeps_watermark", "row.truthWatermark" in refresh and "truthWatermark()" not in refresh, "refresh claims new truth")
check("E13C-08_refresh_indexed", "persistence.retentionDueBy(now.studyDay)" in refresh and "publishedSkills" not in refresh, "refresh scans")
check("E13C-08_unreadable_named", "unreadable += skill" in refresh, "unreadable guessed")
check("E13C-08_decode_strict", "RetentionProfile.entries.single { it.id == id }" in app and "RetentionAxis.entries.single" in app, "decode guesses")

# ---------------------------------------------------------------- planner
build_fn = body(build, "fun build(")
check("E13C-09_refresh_before_states", 0 <= build_fn.find("RefreshDueRetention(persistence, clock).refresh()") < build_fn.find("PlanningStates.read("),
      "the planner plans on a stale schedule")
check("E13C-09_watermark_still_first", 0 <= build_fn.find("persistence.truthWatermark()") < build_fn.find("PlanningStates.read("), "watermark order")
check("E13C-09_needs_unchanged", "state.mastery == MasteryAxisState.CONFIRMED_CURRENT && state.retention == RetentionAxis.REVIEW_DUE" in planner
      and "NeedTrigger.RETENTION_REVIEW_DUE, EvidenceSeverity.NO_NEGATIVE_EVIDENCE" in planner, "the retention need rule moved")
check("E13C-09_review_due_not_blocking", "RetentionAxis.REVIEW_DUE -> PrerequisiteReadiness.READY_DUE" in
      strip_comments(read(ANDROID / "core-engines/src/main/kotlin/coach/engines/PrerequisiteEngine.kt")), "review_due blocks")

# ---------------------------------------------------------------- storage
check("E13C-10_schema_version_5", "const val VERSION = 5" in schema, "schema version")
v5 = body(schema, "val v5")
added = re.findall(r"ALTER TABLE retention_state ADD COLUMN (\w+)", v5)
check("E13C-10_columns_equal_contract", added == contract.get("schema_migration", {}).get("columns"), str(added))
check("E13C-10_due_index", "CREATE INDEX IF NOT EXISTS retention_due ON retention_state (next_review_on_study_day)" in v5, "no due index")
check("E13C-10_insert_trigger", "BEFORE INSERT ON retention_state" in v5 and "BEFORE UPDATE ON retention_state" in v5
      and v5.count("WHEN NEW.state NOT IN ($RETENTION_STATES)") == 2, "value set not enforced")
check("E13C-10_migration_step", "4 to Schema.v5" in migrations and "3 to Schema.v4" in migrations, "migration steps")
mig = contract.get("schema_migration", {})
check("E13C-10_migration_owned", mig.get("from") == 4 and mig.get("to") == 5 and mig.get("owner") == "13C" and mig.get("populated_fixture_tested") is True, str(mig))
due_sql = body(sql, "override fun retentionDueBy(")
check("E13C-10_due_sql", "state IN ('fresh', 'stable')" in due_sql and "next_review_on_study_day <= ?" in due_sql and "instant" not in due_sql, "due query")
check("E13C-10_evidence_day", "e.occurred_on_study_day" in body(sql, "override fun evidenceFor(") and "studyDay = statement.getText(15)" in sql, "evidence day not read")
check("E13C-10_rvr_compact_state", "next_review_at" in section(rvr, "## 18.", "## 19.") and "unresolved_verification_id" in rvr, "§18 moved")

# ---------------------------------------------------------------- ports
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
msbx = yaml.safe_load(read(MSBX)) or {}
check("E13C-11_port_count", sorted(interfaces) == sorted(p["id"] for p in msbx["ports"]["set"]) and contract.get("port_count") == 4, str(interfaces))
check("E13C-11_due_port", "fun retentionDueBy(studyDay: String): List<VersionedRef>" in ports, "no due port")
check("E13C-11_evidence_day_field", "val studyDay: String? = null," in evidence_kt, "no evidence day")

# ---------------------------------------------------------------- tests named
suites = contract.get("suites", {})
named_tests = {
    "model": ["the V0 numbers are RVR-v0 section 20's, and nothing else", "time moves a schedule to review_due and nothing else",
              "an unknown profile is not guessed", "the reason codes are RVR-v0 section 17's, in order"],
    "engine": ["a year with no evidence makes a review due and nothing worse", "the first clean contradiction opens verification and erases nothing",
               "a fresh recheck a day later passes, without growing the interval", "a near repeat cannot carry a complex or critical review alone",
               "strong use before the due day is reuse and does not move the clock", "only clean independent verified direct work counts as a review",
               "uncertainty on a due check is a concern, and a fresh success clears it"],
    "application": ["a mastered Skill gets a fresh schedule, and skill_state carries the retention axis",
                    "the replayed mastery timeline agrees with the mastery engine rebuilt after every row",
                    "the day moves a fresh schedule to review_due, and nothing else is touched",
                    "the planner sees a review that came due overnight, and it is not a failure",
                    "a review repeating a family already met, or not direct for its Objective, is not a complex review"],
    "storage": ["migrating a populated schema-4 database completes retention_state and preserves every truth row",
                "retention_state holds only the RVR-v0 axis, on insert and on the upsert's update",
                "the due query returns only fresh and stable schedules whose study day has come"],
}
for suite, names in named_tests.items():
    text = read(ROOT / suites.get(suite, {}).get("file", "missing"))
    for name in names:
        check(f"E13C-12_{suite}_{name[:40]}", f"`{name}`" in text, f"missing test: {name}")

# ---------------------------------------------------------------- narrowed gates
check("E13C-13_13b_gate_narrowed", "Narrowed at 13C" in read(ROOT / "tools/validate_monthly_assessment.py")
      and 'all(v in owned_versions for v in range(4, schema_version + 1))' in read(ROOT / "tools/validate_monthly_assessment.py"), "13B gate not narrowed with ownership")
check("E13C-13_13b_test_narrowed", "assertTrue(Schema.VERSION >= 4)" in read(ANDROID / "data-persistence/src/test/kotlin/coach/persistence/MonthlySessionStorageTest.kt"),
      "13B test not narrowed")
check("E13C-13_recorded", len(contract.get("living_gates_narrowed", [])) == 2, "narrowing not recorded")

# ---------------------------------------------------------------- honesty
mutation = contract.get("mutation_results", {})
check("E13C-14_mutation_all_detected", mutation.get("detected") == mutation.get("total") == len(mutation.get("mutants", [])) >= 40,
      f"{mutation.get('detected')}/{mutation.get('total')}")
check("E13C-14_mutation_control", mutation.get("negative_control_result") == "survived_as_expected" and mutation.get("compile_failure_is_detection") is False
      and mutation.get("only_retention_suites_run") is True and mutation.get("final_run_is_a_single_clean_run") is True, "harness honesty")
vm = contract.get("validator_mutation", {})
check("E13C-14_validator_mutation", vm.get("detected") == vm.get("total") and (vm.get("total") or 0) >= 20 and vm.get("negative_control_result") == "no_false_positive", str(vm))
runs = {r["id"]: r for r in contract.get("verified_runs", [])}
check("E13C-14_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
device = contract.get("device_verification", {})
check("E13C-14_device_not_claimed", device.get("t6_run") is False and device.get("claimed") is False, "a device result is claimed")
loops = contract.get("open_loops", {})
for loop, owner in (("rebuild_after_evidence_from_app", "16D"), ("topic_weakening_and_remediation", "13D"),
                    ("overdue_urgency_buckets_and_success_band", "18C"), ("replay_runtime", "18E")):
    check(f"E13C-14_open_{loop[:30]}", str(loops.get(loop, {}).get("owner")) == owner, f"{loop} not owned")
forbidden = set(contract.get("forbidden_retention_patterns", []))
for pattern in ("mastery_time_decay", "review_due_as_forgetting", "lock_or_risk_from_time", "probability_or_half_life_presented_as_known",
                "schedule_for_unknown_profile", "near_repeat_as_strong_complex_or_critical_review", "seen_item_as_recheck",
                "recheck_pass_grows_interval", "cluster_or_descendant_refresh", "skill_state_claims_newer_truth"):
    check(f"E13C-15_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 13C QA PASS", "**Decision:** `D-101`", "Time passing is not negative evidence",
                 "Not run: T6", "Mutation 47/47", "13D — Remediation Engine"]:
    check(f"E13C-16_spec_{fragment[:26]}", fragment in spec_text, f"spec: {fragment!r}")
for fragment in ["No web research pass was needed", "RVR-v0", "18C", "replay"]:
    check(f"E13C-17_research_{fragment[:24]}", fragment.lower() in research_text.lower(), f"missing={fragment!r}")

# ---------------------------------------------------------------- living memory hygiene
context_headings = re.findall(r"^## (\d+\.\d+) ", read(ROOT / "PROJECT_CONTEXT.md"), re.M)
check("E13C-18_no_repeated_context_heading", len(context_headings) == len(set(context_headings)), "repeated context heading")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E13C-18_no_repeated_master_plan_step", len(plan_steps) == len(set(plan_steps)), "repeated master plan step")
combining = chr(0x0307)
check("E13C-18_no_combining_dot", all(combining not in read(p) for p in (SPEC, RESEARCH, CONTRACT, FACTS_KT)), "U+0307 in 13C text")

passed_count = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "RVRX-v0", "stage_step": "13C", "decision": "D-101",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed_count, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
print(f"13C_RETENTION_QA={report['result']}")
print(f"checks={passed_count}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
