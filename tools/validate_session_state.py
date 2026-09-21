"""Independent 11C QA — SESX-v0 Session State.

The implementation is validated against the accepted contracts, not against its own. The
safe-checkpoint conditions, pause classes, `ResumeContext` fields, entry sources and ended reasons
are read out of `TRUX-v0`'s `flow.yaml`; the truth entities out of `DDM-v0`; the column owner out of
`LDBX-v0`; the port set out of `MSBX-v0` — each compared with the actual Kotlin source.

Several checks are structural: what must be unrepresentable (a stored mid-segment pause, a session
score, a consumed flag, an invented gap threshold) is looked for in the code with comments stripped.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/11c_session_state/session_state.yaml"
SPEC = ROOT / "docs/SESSION_STATE_SPEC.md"
RESEARCH = ROOT / "research/11c_session_state_research.md"
QA_OUT = ROOT / "arch/11c_session_state/qa_report.yaml"

FLOW = ROOT / "ux/8c_daily_working_flow/flow.yaml"
DDM = ROOT / "arch/9c_domain_data_model/data_model.yaml"
LDBX = ROOT / "arch/10d_local_database/local_database.yaml"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/SessionFacts.kt"
STATE_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/SessionState.kt"
RUNNER_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TaskRunner.kt"
TODAY_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TodayPresentation.kt"
CKPT_KT = ANDROID / "core-application/src/main/kotlin/coach/application/ResumeCheckpoints.kt"
TODAY_QUERY_KT = ANDROID / "core-application/src/main/kotlin/coach/application/TodayFactsQuery.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
ADAPTER_KT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
SCREEN_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/TaskRunnerScreen.kt"
ACTIVITY_KT = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"

FACTS_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/ResumeContextTest.kt"
STATE_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/SessionStateTest.kt"
CKPT_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/ResumeCheckpointsTest.kt"
T2_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/TransactionAndMigrationTest.kt"
T2_DIR = ANDROID / "data-persistence/src/test/kotlin"

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


def body(source: str, signature: str) -> str:
    """The brace-balanced body that follows the first occurrence of `signature`."""
    start = source.find(signature)
    if start < 0:
        return ""
    open_at = source.find("{", start + len(signature))
    if open_at < 0:
        return ""
    depth = 0
    for i in range(open_at, len(source)):
        depth += {"{": 1, "}": -1}.get(source[i], 0)
        if depth == 0:
            return source[open_at + 1:i]
    return ""


def call_args(source: str, call: str) -> str:
    """The parenthesis-balanced argument text of the first call to `call`."""
    start = source.find(call + "(")
    if start < 0:
        return ""
    depth = 0
    for i in range(start + len(call), len(source)):
        depth += {"(": 1, ")": -1}.get(source[i], 0)
        if depth == 0:
            return source[start + len(call) + 1:i]
    return ""


def constructor_params(source: str, cls: str) -> list[str]:
    match = re.search(rf"class {cls}\b[^(]*\((.*?)\n\)", source, re.S)
    return re.findall(r"val (\w+)\s*:", match.group(1)) if match else []


def snake(name: str) -> str:
    return re.sub(r"([A-Z])", r"_\1", name).lower()


def rel(path: Path) -> str:
    return str(path.relative_to(ROOT)).replace("\\", "/")


contract = load(CONTRACT)
flow = load(FLOW)
ddm = load(DDM)
ldbx = load(LDBX)
msbx = load(MSBX)

for path in (FACTS_KT, STATE_KT, CKPT_KT, FACTS_TEST, STATE_TEST, CKPT_TEST, SPEC, RESEARCH):
    check(f"E11C-00_exists_{path.name}", path.is_file(), f"missing {rel(path)}")

facts = strip_comments(read(FACTS_KT))
state = strip_comments(read(STATE_KT))
runner = strip_comments(read(RUNNER_KT))
today = strip_comments(read(TODAY_KT))
ckpt = strip_comments(read(CKPT_KT))
today_query = strip_comments(read(TODAY_QUERY_KT))
ports = strip_comments(read(PORTS_KT))
schema = read(SCHEMA_KT)
adapter = strip_comments(read(ADAPTER_KT))
screen = strip_comments(read(SCREEN_KT))
activity = strip_comments(read(ACTIVITY_KT))
facts_test = read(FACTS_TEST)
state_test = read(STATE_TEST)
ckpt_test = read(CKPT_TEST)
t2_test = read(T2_TEST)
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E11C-01_model", contract.get("model") == "SESX-v0", str(contract.get("model")))
check("E11C-01_status", contract.get("status") == "accepted_11c", str(contract.get("status")))
check("E11C-01_decision", contract.get("decision") == "D-089", str(contract.get("decision")))
for key in ("evidence_written", "planner_implemented", "content_implemented", "artifact_storage_implemented",
            "working_session_persisted", "resume_offered_on_today", "surface_semantics_changed",
            "boundaries_changed", "ports_added", "schema_changed", "migration_added"):
    check(f"E11C-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")
check("E11C-01_no_gap_threshold_claimed", contract["scope"].get("gap_threshold_claim") is None, "a gap threshold was claimed")

# ---------------------------------------------------------------- vocabularies from TRUX-v0
cm = flow["checkpoint_model"]
sm = flow["session_model"]
check("E11C-02_safe_checkpoint_conditions", enum_ids(state, "SafeCheckpointCondition") == cm["safe_checkpoint_conditions"],
      f"kotlin={enum_ids(state, 'SafeCheckpointCondition')}")
durable = [p["id"] for p in cm["pause_classes"] if p.get("durable")]
check("E11C-02_durable_kinds_only", enum_ids(facts, "CheckpointKind") == durable,
      f"kotlin={enum_ids(facts, 'CheckpointKind')} durable={durable}")
check("E11C-02_mid_segment_unstorable", "mid_segment_pause" not in enum_ids(facts, "CheckpointKind"),
      "a mid-segment pause has a stored representation")
check("E11C-02_entry_sources", enum_ids(state, "SessionEntrySource") == sm["entry_sources"],
      f"kotlin={enum_ids(state, 'SessionEntrySource')}")
check("E11C-02_ended_reasons", enum_ids(state, "SessionEndedReason") == sm["ended_reasons"],
      f"kotlin={enum_ids(state, 'SessionEndedReason')}")
keys = re.search(r"private val KEYS = listOf\((.*?)\)", facts, re.S)
key_list = re.findall(r'"([a-z_]+)"', keys.group(1)) if keys else []
check("E11C-02_context_fields", key_list == ["kind"] + cm["resume_context_fields"],
      f"stored={key_list} trux={cm['resume_context_fields']}")
session_fields = [snake(p) for p in constructor_params(state, "WorkingSession")]
check("E11C-02_session_fields", session_fields == sm["semantic_fields"], f"kotlin={session_fields}")
check("E11C-02_resume_conditions_are_11b", enum_ids(runner, "ResumeCondition") == flow["resume_revalidation"]["conditions"],
      "RNRX-v0 resume conditions drifted")

# ---------------------------------------------------------------- what a checkpoint stores
FORBIDDEN_WORDS = ("score", "grade", "percent", "duration", "elapsed", "minutes", "streak", "success", "count", "required")
context_fields = constructor_params(facts, "ResumeContext")
check("E11C-03_context_carries_no_time_or_result",
      not [f for f in context_fields if any(w in f.lower() for w in FORBIDDEN_WORDS)], f"fields={context_fields}")
check("E11C-03_format_versioned", re.search(r'const val FORMAT = "resume_context/\d+"', facts) is not None, "no versioned format")
decode = body(facts, "fun decode(")
check("E11C-03_decode_exact_version", "!= FORMAT) return null" in decode, "decode does not require the exact format")
check("E11C-03_decode_exact_keys", "!= KEYS) return null" in decode, "decode does not require the exact key list")
check("E11C-03_decode_refuses_rather_than_guesses", "getOrNull()" in decode and "?: return null" in decode,
      "decode must return null on anything it cannot read exactly")
check("E11C-03_values_are_tokens", "TOKEN.matches" in facts, "values are not restricted to identity tokens")
init = body(facts, "class ResumeContext(")
check("E11C-03_completed_nonempty", "completedSegments.isNotEmpty()" in init, "a checkpoint may follow no completed work")
check("E11C-03_remaining_nonempty", "remainingSegments.isNotEmpty()" in init, "a finished task may be stored as paused")
check("E11C-03_disjoint_segments", "intersect" in init, "a segment may be both completed and remaining")
check("E11C-03_undecodable_distinct_from_missing", "val context: ResumeContext?" in facts
      and "?: return null" in body(ckpt, "fun read(") and "context = row.payload" in ckpt,
      "an undecodable checkpoint must stay a row with a null context")

# ---------------------------------------------------------------- one pause, one row
record = body(ckpt, "fun record(")
check("E11C-04_one_transaction", record.count("inTransaction") == 1, "the pause is not exactly one transaction")
check("E11C-04_one_row", record.count("appendTruth") == 1, f"appendTruth count={record.count('appendTruth')}")
check("E11C-04_kind_is_resume_checkpoint", 'const val KIND = "resume_checkpoint"' in ckpt, "wrong truth table")
check("E11C-04_single_timestamp", record.count("clock.now()") == 1, "more than one timestamp per action")
for word in ("evidence", '"attempt"', "writeProjection", "exposure"):
    check(f"E11C-04_pause_writes_no_{word.strip(chr(34))}", word not in ckpt, f"{word} in ResumeCheckpoints")
check("E11C-04_no_consumed_flag", not re.search(r"consumed|superseded|latest|isActive", ckpt + facts, re.I),
      "a checkpoint carries a consumed/latest flag")
truth_tables = re.search(r"val truthTables[^=]*=\s*listOf\((.*?)\)", schema, re.S)
check("E11C-04_resume_checkpoint_is_truth", truth_tables is not None and '"resume_checkpoint"' in truth_tables.group(1),
      "resume_checkpoint is not a truth table")
ddl = re.search(r"CREATE TABLE IF NOT EXISTS resume_checkpoint \((.*?)\n\s*\)", schema, re.S)
ddl_text = ddl.group(1) if ddl else ""
check("E11C-05_schema_unchanged", "context TEXT    NOT NULL" in ddl_text
      and not re.search(r"\b(kind|segments|consumed|learning_need_key)\b", ddl_text), f"ddl={ddl_text.strip()[:120]}")
ldbx_text = read(LDBX)
check("E11C-05_context_owned_by_11", "{table: resume_checkpoint, column: context, owner: 11}" in ldbx_text,
      "LDBX-v0 does not leave resume_checkpoint.context to 11")
check("E11C-05_ddm_maps_resume_context", any(e["id"] == "resume_checkpoint" for e in ddm["truth_entities"]),
      "DDM-v0 has no resume_checkpoint")

# ---------------------------------------------------------------- pause policy
classify = body(state, "fun classify(")
check("E11C-06_high_stakes_marked", "if (highStakesWork) return PauseDecision.Durable(CheckpointKind.HIGH_STAKES_PAUSE)" in classify,
      "high-stakes work is not paused durably and marked")
check("E11C-06_all_four_required", "filterNot { it in confirmed }" in classify and "unmet.isEmpty()" in classify,
      "an ordinary pause is durable without every condition confirmed")
check("E11C-06_unmet_not_failed", "failed" not in state.lower(), "an unconfirmed condition is called failed")
state_after = body(state, "fun stateAfter(")
check("E11C-06_mid_segment_keeps_state", "is PauseDecision.NotDurable -> current" in state_after,
      "a mid-segment pause changes the runner state")
check("E11C-06_only_durable_is_saved", state_after.count("CHECKPOINT_PAUSED") == 1
      and "is PauseDecision.Durable -> RunnerState.CHECKPOINT_PAUSED" in state_after, "saved state reachable otherwise")
check("E11C-06_mid_segment_class", "PauseClass.MID_SEGMENT_PAUSE" in body(state, "data class NotDurable("),
      "NotDurable is not the mid-segment class")

# ---------------------------------------------------------------- resume
confirm = body(state, "fun fromCheckpoint(")
check("E11C-07_decoded_required", "stored?.context ?: return emptySet()" in confirm, "an undecodable checkpoint confirms something")
check("E11C-07_artifact_not_assumed", "if (context.artifactStateRef == null) add(ResumeCondition.RUNNER_AND_ARTIFACT_STATE_INTACT)" in confirm,
      "referenced artifact state assumed intact")
check("E11C-07_gap_only_for_ordinary_pause",
      "if (context.kind == CheckpointKind.CHECKPOINT_PAUSE) add(ResumeCondition.HIGH_STAKES_GAP_INTEGRITY_ACCEPTABLE)" in confirm,
      "a high-stakes gap is confirmed without an accepted policy")
for condition in ("CONTENT_VERSION_COMPATIBLE", "PREREQUISITES_STILL_ELIGIBLE", "LEARNING_NEED_STILL_OPEN"):
    check(f"E11C-07_never_{condition.lower()}", condition not in confirm, f"a checkpoint confirms {condition}")
check("E11C-07_no_invented_threshold", not re.search(r"\b\d{2,}\b", state) and "Duration" not in state
      and "hours" not in state.lower(), "a numeric threshold appears in session state")

def tone_of(source: str, state_name: str) -> str:
    """Reads the tone arm of `RunnerState.tone`'s `when` that names the given state."""
    for arm in re.findall(r"([A-Z_.,\sa-zA-Z]+?)->\s*Tone\.([A-Z_]+)", body(source, "val RunnerState.tone")):
        names, tone = arm
        if f"RunnerState.{state_name}" in re.sub(r"\s", "", names).split(","):
            return tone
    return ""


check("E11C-07_resume_invalidated_not_fault", tone_of(runner, "RESUME_INVALIDATED") == "NEUTRAL"
      and tone_of(runner, "CHECKPOINT_PAUSED") == "NEUTRAL",
      f"resume_invalidated={tone_of(runner, 'RESUME_INVALIDATED')} checkpoint_paused={tone_of(runner, 'CHECKPOINT_PAUSED')}")
check("E11C-07_today_offers_no_checkpoint", "ResumeCheckpoints" not in today + today_query + activity
      and call_args(activity, "todayInput") != "" and "resumable" not in call_args(activity, "todayInput"),
      "a checkpoint reaches Today past the planner")

# ---------------------------------------------------------------- the working session
check("E11C-08_session_fields_clean",
      not [f for f in constructor_params(state, "WorkingSession") + constructor_params(state, "TaskRun")
           if any(w in f.lower() for w in FORBIDDEN_WORDS)], "a session field could hold a score or a time")
check("E11C-08_constructor_internal", "class WorkingSession internal constructor(" in state,
      "a session can be built outside its rules")
start = body(state, "fun startIfRunStarted(")
check("E11C-08_blocked_entry_starts_none", "is Revalidation.NotStartable -> null" in start, "a blocked entry starts a session")
check("E11C-08_ends_once", "if (session.ended) return session" in body(state, "fun on("), "an ended session can be rewritten")
check("E11C-08_ended_does_not_grow", "require(!session.ended)" in body(state, "fun withRun("), "an ended session grows")
reason = body(state, "fun endedReason(")
expected = [("SessionEvent.LearnerExited", "USER_STOPPED"), ("event.capacityReached", "CAPACITY_REACHED"),
            ("else", "PLAN_EXHAUSTED"), ("SessionEvent.FlowLeftWithoutExit", "INTERRUPTED"),
            ("SessionEvent.StoreNeedsRecovery", "RECOVERY_REQUIRED")]
for event, value in expected:
    check(f"E11C-08_reason_{value.lower()}", re.search(rf"{re.escape(event)} -> SessionEndedReason\.{value}", reason) is not None,
          f"{event} does not end as {value}")
check("E11C-08_non_empty_selection_continues", "!event.selectionEmpty -> null" in reason, "a non-empty selection ends the session")
check("E11C-08_reason_has_no_tone", "Tone" not in state, "a session reason carries a tone")
check("E11C-08_capacity_not_derived", "capacityReached" in body(state, "data class SelectionRecomputed(")
      and not re.search(r"capacity\w*\s*[<>]=?", state), "a capacity verdict is derived in session state")
check("E11C-09_not_persisted", "appendTruth" not in state and "working_session" not in schema
      and all(e["id"] != "working_session" for e in ddm["truth_entities"]), "the working session is persisted")

# ---------------------------------------------------------------- port refinement
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
msbx_ports = [p["id"] for p in msbx["ports"]["set"]]
check("E11C-10_port_count_four", sorted(interfaces) == sorted(msbx_ports), f"interfaces={interfaces} msbx={msbx_ports}")
persistence_methods = re.findall(r"fun (?:<T> )?(\w+)\(", body(ports, "interface PersistencePort"))
# 11C owns `readTruth` and the fact that reading truth added no mutation path. It does not own the
# rest of the port: later steps refine the same interface (11D publishes curriculum), so this check
# was narrowed from an exact list to what 11C actually decided.
check("E11C-10_persistence_methods",
      persistence_methods[:3] == ["inTransaction", "appendTruth", "readTruth"]
      and {"readProjection", "writeProjection", "curriculumPublished"} <= set(persistence_methods),
      f"methods={persistence_methods}")
read_truth = body(adapter, "override fun readTruth(")
check("E11C-10_read_truth_reads_only", "SELECT" in read_truth and not re.search(r"\b(INSERT|UPDATE|DELETE)\b", read_truth),
      "readTruth writes")
check("E11C-10_read_truth_truth_only", "require(kind in Schema.truthTables)" in read_truth, "readTruth reads projections")
check("E11C-10_read_truth_offset_minutes", "* 60" in read_truth, "the stored offset unit is lost on read")

# ---------------------------------------------------------------- wiring and screen
on_start = body(activity, "onStart = {")
check("E11C-11_session_starts_from_revalidated_entry", "WorkingSessions.startIfRunStarted(" in on_start
      and "entry = entry" in on_start and "SessionEntrySource.TODAY_PRIMARY_ACTION" in on_start,
      "the session start is not derived from the revalidated entry")
check("E11C-11_exit_ends_user_stopped", "SessionEvent.LearnerExited" in body(activity, "onExit = {"), "the exit does not end the session")
check("E11C-11_saved_copy_only_for_durable", screen.count("RunnerCopy.CHECKPOINT_SAVED") == 1
      and "RunnerState.CHECKPOINT_PAUSED -> Text(RunnerCopy.CHECKPOINT_SAVED" in screen, "saved text reachable elsewhere")
check("E11C-11_invalidated_copy", "RunnerState.RESUME_INVALIDATED ->" in screen and "RunnerCopy.RESUME_INVALIDATED" in screen,
      "resume_invalidated has no explanation")
invalidated = re.search(r'const val RESUME_INVALIDATED =(.*?)\n\n', read(RUNNER_KT), re.S)
invalidated_text = invalidated.group(1) if invalidated else ""
check("E11C-11_invalidated_not_blame", "hata ya da başarısızlık değil" in invalidated_text and "silinmedi" in invalidated_text,
      "resume_invalidated reads as blame or loss")
for condition in ("prerequisites_still_eligible", "runner_and_artifact_state_intact", "high_stakes_gap_integrity_acceptable"):
    upper = condition.upper()
    check(f"E11C-11_label_{condition}", f"ResumeCondition.{upper}.id to" in screen, f"no label for {condition}")
check("E11C-11_ui_decides_no_tone", "Tone." not in screen, "app-ui names a tone")

# ---------------------------------------------------------------- tests exist and count honestly
for name in ["a context round-trips exactly", "anything that does not decode exactly decodes to nothing rather than a guess",
             "a value that would need escaping is refused rather than escaped"]:
    check(f"E11C-12_t1_model_{name[:40]}", f"`{name}`" in facts_test, f"missing test: {name}")
for name in ["only a durable pause is shown as saved", "a high-stakes gap is never confirmed acceptable without an accepted gap policy",
             "no checkpoint is resumable today and an invalidated resume is not a failure", "a blocked entry starts no session",
             "a session ends once and its history cannot be rewritten or extended",
             "a session can hold no score, grade, percentage, duration or required count"]:
    check(f"E11C-12_t1_state_{name[:40]}", f"`{name}`" in state_test, f"missing test: {name}")
for name in ["a pause is one transaction writing one checkpoint row and nothing else",
             "an unreadable checkpoint is kept distinct from a missing one"]:
    check(f"E11C-12_t1_app_{name[:40]}", f"`{name}`" in ckpt_test, f"missing test: {name}")
for name in ["a checkpoint reads back from SQLite exactly as it was saved", "a pause writes nothing but its checkpoint",
             "a saved checkpoint is never rewritten, and a later pause appends beside it",
             "a pause that fails leaves no checkpoint claiming it was saved"]:
    check(f"E11C-12_t2_{name[:40]}", f"`{name}`" in t2_test, f"missing T2 test: {name}")
t2_count = sum(read(p).count("@Test") for p in T2_DIR.rglob("*.kt"))
run02 = next((r for r in contract["verified_runs"] if r["id"] == "RUN-02"), {})
# 11C claimed the count at its own acceptance; later steps add T2 checks of their own, so the
# living form of this check is that 11C did not claim more than exists, and that its own checks do.
check("E11C-12_t2_count_not_inflated", run02.get("tests", 0) <= t2_count,
      f"claimed={run02.get('tests')} sources={t2_count}")

# ---------------------------------------------------------------- repository hygiene (carried from 11B)
COMBINING_DOT_ABOVE = chr(0x0307)
contaminated = []
for path in ROOT.rglob("*"):
    if not path.is_file() or {".git", "build", ".gradle", ".kotlin"} & set(path.parts):
        continue
    if path.suffix not in {".md", ".yaml", ".yml", ".kt", ".py", ".kts"}:
        continue
    try:
        if COMBINING_DOT_ABOVE in path.read_text(encoding="utf-8"):
            contaminated.append(rel(path))
    except UnicodeDecodeError:
        continue
check("E11C-13_no_combining_dot", not contaminated, f"U+0307 found in {contaminated}")
LIVING = ["AGENTS.md", "PROJECT_CONTEXT.md", "docs/START_HERE.md", "docs/HANDOFF_STATE.md", "docs/STEP_STATUS.md",
          "docs/EXECUTION_INDEX.md", "docs/MASTER_PLAN.md", "docs/DECISIONS.md", "docs/PROGRESS_LOG.md",
          "docs/LOCAL_MANAGER_HANDOFF.md", "vault/agent/CURRENT_CONTEXT.md", "vault/agent/OPEN_LOOPS.md"]
adjacent = []
for living in LIVING:
    lines = read(ROOT / living).splitlines()
    adjacent += [f"{living}:{n + 1}" for n in range(1, len(lines))
                 if len(lines[n].strip()) > 25 and lines[n] == lines[n - 1]]
check("E11C-13_no_adjacent_duplicate_lines", not adjacent, f"adjacent duplicates at {adjacent}")
# 11A's sync left two broken Turkish words on main that 11B's U+0307 guard could not see: a doubled
# dotless i and a dropped ş. They are fixed; these exact forms must not come back.
BROKEN_WORDS = ["çal" + "ıı" + "ştırılmadı", "doğrulanmam" + "ı oturum"]
broken = [f"{living}: {w}" for living in LIVING for w in BROKEN_WORDS if w in read(ROOT / living)]
check("E11C-13_no_broken_turkish_words", not broken, f"found {broken}")
headings = re.findall(r"^### \[.\] (11C[^\n]*)", read(ROOT / "docs/MASTER_PLAN.md"), re.M)
check("E11C-13_one_11c_heading", len(headings) == 1, f"11C headings={headings}")

# ---------------------------------------------------------------- honesty
device = contract["device_verification"]
check("E11C-14_device_not_claimed", device["t6_run"] is False and device["claimed"] is False, "no device result claimed")
mutation = contract["mutation_results"]
check("E11C-14_mutation_all_detected", mutation["detected"] == mutation["total"] == len(mutation["mutants"]) >= 16,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E11C-14_no_compile_only_catch", mutation.get("caught_by_compilation_only") == [], "a mutant was caught only by the compiler")
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E11C-14_runs_pass", len(runs) >= 6 and all(r["result"] == "PASS" for r in runs.values()), str(list(runs)))
check("E11C-14_not_verified_named", len(contract.get("not_verified", [])) >= 3, "what was not verified must be named")
boundaries = {str(k) for k in contract.get("future_stage_boundaries", {})}
for owner in ("11D", "12", "13", "16B", "18D"):
    check(f"E11C-14_boundary_{owner}", owner in boundaries, f"missing {owner}")
forbidden = set(contract.get("forbidden_session_patterns", []))
for pattern in ("mid_segment_pause_written_or_shown_as_saved", "checkpoint_storing_elapsed_time_score_or_count",
                "checkpoint_decoded_by_best_guess", "checkpoint_updated_or_flagged_consumed",
                "high_stakes_gap_threshold_invented", "checkpoint_offered_on_today_bypassing_planner",
                "session_scored_graded_or_timed", "session_persisted_as_invented_entity"):
    check(f"E11C-14_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 11C QA PASS", "**Decision:** `D-089`", "unmet",
                 "resume_context/1", "readTruth", "16B", "T6"]:
    check(f"E11C-15_spec_{fragment[:26]}", fragment in spec_text, f"missing={fragment!r}")
for fragment in ["high-stakes", "readTruth", "continue_learning", "No web research pass was needed"]:
    check(f"E11C-16_research_{fragment[:20]}", fragment in research_text, f"missing={fragment!r}")

passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "SESX-v0", "stage_step": "11C", "decision": "D-089",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
print(f"11C_SESSION_STATE_QA={report['result']}")
print(f"checks={passed}/{len(results)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
