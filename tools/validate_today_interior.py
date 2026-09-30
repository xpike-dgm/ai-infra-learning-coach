"""Independent 11A QA — TDYX-v0 Today Interior.

The implementation is validated against the accepted contracts, not against its own. The twelve
semantic states, the six-step precedence, the task-row fields, the reason and attention vocabularies
and the empty-state rules are read out of `THUX-v0`'s `home.yaml`; the region order out of
`WFPX-v0`; the tones out of `VDSX-v0`; the cross-cutting states out of `UXIA-v0`; the module
placement out of `MSBX-v0`; the purposes out of `TASK_TAXONOMY_SPEC` — and each is compared with the
**actual Kotlin source**.

Several checks are about what cannot be written rather than what is: no free-text reason field, no
mastery-shaped field on a row, no capacity verdict derived in the presentation. Those are the claims
a screen starts making by accident, so they are checked structurally.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"

CONTRACT = ROOT / "arch/11a_today/today_interior.yaml"
SPEC = ROOT / "docs/TODAY_INTERIOR_SPEC.md"
RESEARCH = ROOT / "research/11a_today_research.md"
QA_OUT = ROOT / "arch/11a_today/qa_report.yaml"

HOME = ROOT / "ux/8b_today_home/home.yaml"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
VDSX = ROOT / "ux/8f_design_system/design_system.yaml"
WFPX = ROOT / "ux/8g_wireframe_prototype/wireframe.yaml"
MSBX = ROOT / "arch/9d_service_boundaries/boundaries.yaml"
APHX = ROOT / "arch/10e_app_health/app_health.yaml"
TAXONOMY = ROOT / "docs/TASK_TAXONOMY_SPEC.md"

FACTS_KT = ANDROID / "core-model/src/main/kotlin/coach/TodayFacts.kt"
TODAY_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/TodayPresentation.kt"
NAV_KT = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/Navigation.kt"
QUERY_KT = ANDROID / "core-application/src/main/kotlin/coach/application/TodayFactsQuery.kt"
SCREEN_KT = ANDROID / "app-ui/src/main/kotlin/coach/ui/TodayScreen.kt"
CONTENT_KT = ANDROID / "data-curriculum/src/main/kotlin/coach/curriculum/FileContentSource.kt"
PORTS_KT = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
ACTIVITY_KT = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
APPLICATION_KT = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
WORKFLOW = ROOT / ".github/workflows/android.yml"

TODAY_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/TodayPresentationTest.kt"
NAV_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/NavigationTest.kt"
QUERY_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/TodayFactsQueryTest.kt"
CONTENT_TEST = ANDROID / "data-curriculum/src/test/kotlin/coach/curriculum/FileContentSourceTest.kt"
T2_TESTS = ANDROID / "data-persistence/src/test/kotlin/coach/persistence"

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
    """Judges code, not prose describing the rule."""
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"//[^\n]*", "", source)


def enum_ids(source: str, name: str) -> list[str]:
    match = re.search(rf"enum class {name}\b[^{{]*\{{(.*?)\n\}}", source, re.S)
    return re.findall(r'\(\s*"([a-z_]+)"', match.group(1)) if match else []


def data_class_fields(source: str, name: str) -> list[str]:
    match = re.search(rf"(?:data )?class {name}\s*(?:private )?\(([^)]*)\)", source, re.S)
    return re.findall(r"val (\w+):", match.group(1)) if match else []


def ordered(source: str, *needles: str) -> bool:
    positions = [source.find(n) for n in needles]
    return all(p >= 0 for p in positions) and positions == sorted(positions)


contract = load(CONTRACT)
home = load(HOME)
ia = load(IA)
vdsx = load(VDSX)
wfpx = load(WFPX)
msbx = load(MSBX)
aphx = load(APHX)

for path in (FACTS_KT, TODAY_KT, QUERY_KT, SCREEN_KT, CONTENT_KT, TODAY_TEST, QUERY_TEST, CONTENT_TEST, SPEC, RESEARCH):
    check(f"E11A-00_exists_{path.name}", path.is_file(), f"missing {path}")

facts = strip_comments(read(FACTS_KT))
today = strip_comments(read(TODAY_KT))
navigation = strip_comments(read(NAV_KT))
query = strip_comments(read(QUERY_KT))
screen = read(SCREEN_KT)
screen_code = strip_comments(screen)
content = strip_comments(read(CONTENT_KT))
ports = strip_comments(read(PORTS_KT))
activity = strip_comments(read(ACTIVITY_KT))
application = strip_comments(read(APPLICATION_KT))
workflow = read(WORKFLOW)
today_test = read(TODAY_TEST)
nav_test = read(NAV_TEST)
query_test = read(QUERY_TEST)
content_test = read(CONTENT_TEST)
t2_tests = "\n".join(read(p) for p in sorted(T2_TESTS.glob("*.kt")))
spec_text = read(SPEC)
research_text = read(RESEARCH)

# ---------------------------------------------------------------- identity and scope
check("E11A-01_model", contract.get("model") == "TDYX-v0", str(contract.get("model")))
check("E11A-01_status", contract.get("status") == "accepted_11a", str(contract.get("status")))
check("E11A-01_decision", contract.get("decision") == "D-087", str(contract.get("decision")))
for key in ("planner_implemented", "mastery_engine_implemented", "surface_semantics_changed",
            "boundaries_changed", "ports_added", "schema_changed"):
    check(f"E11A-01_scope_{key}", contract.get("scope", {}).get(key) is False,
          f"{key}={contract.get('scope', {}).get(key)}")
for key in ("task_row_count_claim", "pixel_geometry_claim", "startup_time_claim"):
    check(f"E11A-01_noclaim_{key}", contract.get("scope", {}).get(key, "missing") is None,
          f"{key}={contract.get('scope', {}).get(key)}")

# ---------------------------------------------------------------- vocabularies match THUX-v0
accepted_states = home["semantic_states"]
kotlin_states = enum_ids(today, "TodayState")
check("E11A-02_states_are_thux", kotlin_states == accepted_states,
      f"kotlin={kotlin_states} thux={accepted_states}")
accepted_precedence = home["primary_action_precedence"]
kotlin_precedence = enum_ids(today, "PrimaryActionKind")
check("E11A-02_precedence_is_thux", kotlin_precedence == accepted_precedence,
      f"kotlin={kotlin_precedence} thux={accepted_precedence}")
accepted_attention = home["attention_context"]["allowed_families"]
check("E11A-02_attention_is_thux", enum_ids(today, "AttentionFamily") == accepted_attention,
      f"kotlin={enum_ids(today, 'AttentionFamily')} thux={accepted_attention}")
check("E11A-02_dispositions_are_thux",
      enum_ids(today, "TaskDisposition") == home["planned_task_row"]["allowed_dispositions"],
      f"kotlin={enum_ids(today, 'TaskDisposition')}")

# purposes come from the taxonomy spec's own list
taxonomy = read(TAXONOMY)
purpose_block = re.search(r"Canonical V1 değerleri:\s*```text\n(.*?)```", taxonomy, re.S)
accepted_purposes = purpose_block.group(1).split() if purpose_block else []
check("E11A-03_purposes_are_canonical", enum_ids(facts, "TaskPurpose") == accepted_purposes,
      f"kotlin={enum_ids(facts, 'TaskPurpose')} spec={accepted_purposes}")
check("E11A-03_english_is_not_a_purpose", "english" not in " ".join(enum_ids(facts, "TaskPurpose")),
      "English is a track, never a purpose (TEIP-v0)")

# ---------------------------------------------------------------- reasons cannot be invented
reason_ids = enum_ids(facts, "ReasonFamily")
check("E11A-04_reason_families_closed", len(reason_ids) == contract["vocabularies"]["reason_families"]["count"],
      f"families={reason_ids}")
check("E11A-04_reason_has_no_free_text",
      not re.search(r"class ReasonSummary[^}]*?val \w+: String", today, re.S),
      "a String on the reason type is where a free-form AI justification would live")
check("E11A-04_reason_is_trace_derived",
      "class ReasonSummary private constructor" in today and "fun fromTraceFacts" in today,
      "a reason must only be constructible from the planner's trace facts")
check("E11A-04_one_primary_one_supporting",
      home["reason_presentation"]["overview_primary_reason_count"] == 1
      and home["reason_presentation"]["overview_supporting_reason_max"] == 1
      and re.search(r"val primary: ReasonFamily,\s*val supporting: ReasonFamily\?", today) is not None,
      "exactly one primary and at most one supporting reason")
check("E11A-04_upstream_subset_rule",
      home["reason_presentation"]["subset_of_trace_facts_required"] is True
      and home["reason_presentation"]["free_form_ai_truth"] is False,
      "THUX-v0 requires reasons to be a subset of trace facts")

# ---------------------------------------------------------------- the task row
row_fields = data_class_fields(today, "TodayTaskRow")
for field in ("plannedTaskRef", "displayTitle", "primaryPurpose", "targetSkillRefs", "disposition", "reason"):
    check(f"E11A-05_row_field_{field}", field in row_fields, f"row fields={row_fields}")
forbidden_row = re.compile(r"mastery|score|percent|streak|grade|rank", re.I)
check("E11A-05_row_claims_nothing",
      not [f for f in row_fields if forbidden_row.search(f)],
      f"row fields={row_fields}")
check("E11A-05_purpose_activity_track_separate",
      home["planned_task_row"]["purpose_activity_track_separate"] is True
      and all(f in row_fields for f in ("primaryPurpose", "activityKind", "curriculumTrack")),
      "purpose, activity and track must stay three fields")

# ---------------------------------------------------------------- capacity
capacity_fields = data_class_fields(facts, "CapacityContext")
allowed_capacity = {
    "resolvedDailyMinutes", "currentDayOverride", "estimatedTotalPlannedMinutes",
    "estimatedRemainingPlannedMinutes", "planRecalculated", "tooSmallForAnyCandidate",
}
check("E11A-06_capacity_fields_allowed", set(capacity_fields) <= allowed_capacity,
      f"unexpected={sorted(set(capacity_fields) - allowed_capacity)}")
forbidden_capacity = re.compile(r"percent|progress|mastery|required|minimum|countdown", re.I)
check("E11A-06_capacity_claims_nothing", not [f for f in capacity_fields if forbidden_capacity.search(f)],
      f"capacity fields={capacity_fields}")
check("E11A-06_verdict_not_derived",
      "tooSmallForAnyCandidate" in facts
      and not re.search(r"estimatedTotalPlannedMinutes\s*[!><]", today),
      "the presentation must not compare estimates to decide a capacity verdict")
check("E11A-06_upstream_forbids_interpretation",
      "progress_percentage" in home["capacity_context"]["forbidden_interpretations"],
      "D-033/THUX-v0 forbid reading capacity as progress")

# ---------------------------------------------------------------- truthfulness in the projection
# The filter appears in the projection *and* in the queue builder, and both matter: a check that
# only proved "somewhere in this file" would pass an implementation whose primary path ignores the
# study day, which is exactly the mutant that slipped through the first time.
of_body = today[today.find("fun of("):today.find("private fun blocked")]
queue_body = today[today.find("private fun queueOf"):today.find("private fun row(")]
check("E11A-07_stale_plan_filtered",
      "input.plan?.takeIf { it.studyDay == input.studyDay }" in of_body
      and "input.plan?.takeIf { it.studyDay == input.studyDay }" in queue_body,
      "a plan from another study day must never become today's action or today's queue")
check("E11A-07_upstream_no_replay",
      home["missed_day"]["replay_yesterday_plan_as_backlog"] is False
      and home["missed_day"]["absence_is_debt"] is False,
      "SRR-v0/THUX-v0 forbid replaying an old plan")
# 12E moved the filter into one named rule that also keeps kept work (started earlier, 12D) from being
# offered again; the guarantee — blocked work is neither actionable nor listed — is what is checked.
check("E11A-07_blocked_filtered",
      ("filterNot { it.blocked }" in today
       or ("private fun startable(task: PlannedTaskFact): Boolean = !task.blocked" in today
           and today.count("filter(::startable)") >= 2))
      and home["remaining_plan"]["blocked_dependent_work_actionable"] is False,
      "blocked work must not be actionable or listed")
check("E11A-07_session_revalidated",
      "input.resumable?.takeIf { it.revalidated }" in today
      and home["precedence_rules"]["resumable_session_must_revalidate"] is True,
      "an unrevalidated session must not be offered")
check("E11A-07_ai_never_supersedes",
      home["precedence_rules"]["ai_unavailable_supersedes_valid_local_plan"] is False
      and "contexts = contexts" in today,
      "AI absence is context, never a blocking state")
check("E11A-07_queue_is_current_selection",
      home["remaining_plan"]["contains_old_daily_backlog"] is False
      and home["remaining_plan"]["includes_only_current_selected_tasks"] is True
      and "private fun queueOf" in today,
      "the queue is the current selection only")

# ---------------------------------------------------------------- tones from VDSX-v0
tones = vdsx["surface_state_tones"]
tone_block = re.search(r"val TodayState\.tone: Tone(.*?)\n\}", today, re.S)
tone_source = tone_block.group(1) if tone_block else ""
for state in accepted_states:
    expected = tones.get(state)
    name = state.upper()
    if expected == "system_fault":
        ok = re.search(rf"TodayState\.{name} -> Tone\.SYSTEM_FAULT", tone_source) is not None
    elif expected == "active":
        ok = re.search(rf"TodayState\.{name} -> Tone\.ACTIVE", tone_source) is not None
    else:
        # A neutral state is one the branch names and never maps to a louder tone. The last entry of
        # the neutral branch is followed by `-> Tone.NEUTRAL`, so presence plus absence is the test.
        ok = re.search(rf"TodayState\.{name}\b", tone_source) is not None and \
             re.search(rf"TodayState\.{name} -> Tone\.(SYSTEM_FAULT|ACTIVE)", tone_source) is None
    check(f"E11A-08_tone_{state}", ok, f"vdsx={expected}")
check("E11A-08_only_two_fault_states",
      sorted(re.findall(r"TodayState\.(\w+) -> Tone\.SYSTEM_FAULT", tone_source)) ==
      ["DATA_RECOVERY_REQUIRED", "ERROR_RECOVERABLE"],
      "only the two system conditions may wear the fault tone")

# ---------------------------------------------------------------- geometry and navigation
check("E11A-09_region_order",
      wfpx["surface_geometry"]["today_overview"]["region_order"] == contract["vocabularies"]["region_order"]["value"]
      and re.search(r'regionOrder: List<String> = listOf\(\s*"primary_action", "day_plan_context", "remaining_plan", "attention_context", "supporting_navigation",', today) is not None,
      "the region order is WFPX-v0's")
check("E11A-09_supporting_routes",
      "Surface.PlannerExplanation" in today and "Surface.SkillDetail" in today
      and "Surface.TopicDetail" in today and "Surface.ProgressOverview" in today
      and "Surface.ProfileOverview" in today,
      "THUX-v0's supporting routes must be reachable")
check("E11A-09_registry_computed_on_access",
      re.search(r"val all: List<Surface>\s*\n\s*get\(\) =", navigation) is not None,
      "the surface registry must be computed on access, not held as an initialised field")

# ---------------------------------------------------------------- module placement (MSBX-v0)
check("E11A-10_facts_in_core_model", "package coach.model" in facts, "the facts must live in core-model")
check("E11A-10_projection_in_core_presentation",
      "package coach.presentation" in today and msbx["invariants"]["presentation_state_computed_in_ui_toolkit"] is False,
      "the projection must live in core-presentation")
check("E11A-10_query_is_core_application",
      "package coach.application" in query and not re.search(r"import android|import androidx", query),
      "the read path must live in core-application and stay free of Android")
check("E11A-10_ui_renders_only",
      "TodayPresentation.of(" not in screen_code and "coach.persistence" not in screen,
      "app-ui must not compute the projection or reach persistence")
check("E11A-10_ports_unchanged",
      len(re.findall(r"^interface \w+Port\b", ports, re.M)) == 4 and len(msbx["ports"]["set"]) == 4,
      "no fifth port may be added")
check("E11A-10_port_refinement_recorded",
      contract["port_refinement"]["new_port"] is False and "curriculumPublished" in ports,
      "the port refinement must be recorded")

# ---------------------------------------------------------------- the read path tells the truth
check("E11A-11_query_reads_only",
      "appendTruth" not in query and "writeProjection" not in query and "inTransaction" not in query,
      "the Today read path must not write")
# 11A held the plan and capacity at `null` because no planner wrote plans yet. 12E reads them, so the
# gate was narrowed to its guarantee: neither is invented — both come only from the stored plan through
# PlanReading, and with nothing stored there is neither.
check("E11A-11_no_invented_plan",
      re.search(r"plan = null", query) is not None
      or ("persistence.latestPlan() ?: return TodayFacts(studyDay, curriculumLoaded = curriculumLoaded)" in query
          and "plan = PlanReading.snapshot(read)" in query and "PlanReading.read(stored, studyDay)" in query),
      "no plan may be invented; it comes only from what the planner stored")
check("E11A-11_no_invented_capacity",
      re.search(r"capacity = null", query) is not None
      or (query.count("capacity =") == 1 and "capacity = PlanReading.capacity(read.trace)" in query),
      "no capacity may be invented; it comes only from the planner's record of today's plan")
check("E11A-11_study_day_from_clock", "clock.now().studyDay" in query,
      "the study day comes from ClockPort")
check("E11A-11_off_main_thread",
      "TodayFactsQuery" in application and "storeThread.execute" in application
      and "TodayFactsQuery" not in activity,
      "reading Today must happen on the store thread, never in the activity")
check("E11A-11_refreshed_on_resume",
      "override fun onStart()" in activity and "refreshToday" in activity,
      "a process kept open past midnight must re-read the study day")

# ---------------------------------------------------------------- empty_valid handed over by 10E
declared = {d["state"]: d for d in aphx["cross_cutting_states"]["declared_not_produced"]}
check("E11A-12_empty_valid_was_deferred",
      declared.get("empty_valid", {}).get("owner") == 11,
      f"APHX-v0 deferred: {sorted(declared)}")
check("E11A-12_empty_valid_now_produced",
      "CrossCuttingState.EMPTY_VALID" in today and contract["empty_valid"]["produced_here"] is True,
      "11A must produce the empty_valid it was handed")
check("E11A-12_empty_valid_is_uxia", "empty_valid" in ia["cross_cutting_surface_states"],
      "empty_valid is UXIA-v0's")
check("E11A-12_empty_is_not_mastery",
      home["empty_state_distinctions"]["no_task_implies_all_mastered"] is False
      and home["empty_state_distinctions"]["no_task_implies_professional_ready"] is False,
      "an empty day claims nothing about mastery")

# ---------------------------------------------------------------- content port no longer throws
# 11A owns the guarantee, not the implementation: asking for content that is not there answers
# "no such resource" instead of crashing. 11D gave the adapter a real package to serve, so pinning
# the literal `= null` body had become a stale gate; the guarantee is what is checked.
check("E11A-13_content_port_returns_null",
      "override fun resource(ref: VersionedRef): ContentDocument?" in content
      and "TODO(" not in content and "throw" not in content,
      "asking for unpublished content is a missing resource, not a crash")

# ---------------------------------------------------------------- the screen says things in words
for state in accepted_states:
    name = state.upper()
    check(f"E11A-14_label_{state}", f"TodayState.{name} to" in screen, f"no Turkish label for {state}")
check("E11A-14_state_is_text", "StateChip(" in screen and "liveRegion = LiveRegionMode.Polite" in screen,
      "the state must be text and announced")
check("E11A-14_touch_target", "minimumTouchTarget()" in screen, "actions keep the 48dp floor")
check("E11A-14_no_locale_naive_case", not re.search(r"\.(uppercase|lowercase|capitalize)\(\s*\)", screen),
      "no locale-naive case transform")
check("E11A-14_primary_action_first",
      ordered(screen_code, "PrimaryAction(view", "view.capacity?.let", "view.remainingPlan.isNotEmpty()",
              "view.attention.forEach", "view.supportingNavigation.forEach"),
      "the regions must be rendered in the accepted order")

# ---------------------------------------------------------------- the checks exist
for name in [
    "the twelve semantic states are THUX-v0's, in its order",
    "yesterday's plan is never shown as today's",
    "a blocked task is never actionable and never fills the queue",
    "a plan whose every task is blocked is an empty state, not a blocked task list",
    "replanning never exposes the plan it is replacing",
    "a reason cannot be invented, only taken from the planner's trace facts",
    "no reason carries free text that an AI could fill",
    "capacity too small for any candidate is reported by the planner, never derived here",
    "a fresh install says nothing is published yet and claims no mastery",
    "AI being unavailable is context and never supersedes a valid local plan",
    "attention does not duplicate what the primary task already represents",
    "every Today state wears the tone VDSX-v0 assigned it",
    "nothing on a task row can claim mastery, a score or a streak",
]:
    check(f"E11A-15_test_{name[:44]}", f"`{name}`" in today_test, f"missing test: {name}")
check("E11A-15_registry_guard_test",
      "`the surface registry is computed on access, not held as an initialised field`" in nav_test,
      "the initialisation-order fix must have a check")
check("E11A-15_query_tests",
      "`no plan and no capacity are invented while the planner and settings do not exist`" in query_test
      and "`reading Today writes nothing`" in query_test,
      "the read path's claims must be tested")
check("E11A-15_t2_curriculum_published",
      "`an empty curriculum store and a published one are told apart`" in t2_tests,
      "the curriculum-published read must be tested against a real store")
check("E11A-15_content_test",
      "`an unpublished resource is absent rather than an exception`" in content_test,
      "the content port must be tested for not throwing")

# ---------------------------------------------------------------- CI and honesty
check("E11A-16_ci_runs_curriculum_tests", ":data-curriculum:test" in workflow,
      "CI must run the data-curriculum tests")
device = contract["device_verification"]
check("E11A-17_device_not_claimed", device["t6_run"] is False and device["claimed"] is False,
      "no device result may be claimed when the device was not connected")
mutation = contract["mutation_results"]
check("E11A-17_mutation_all_detected", mutation["detected"] == mutation["total"] >= 12,
      str({k: mutation[k] for k in ("total", "detected")}))
check("E11A-17_survivors_recorded", len(mutation.get("survived_at_first", [])) >= 3,
      "mutants that survived at first must be recorded with the fix")
check("E11A-17_found_before_recorded", len(contract.get("found_before_11a", [])) >= 2,
      "defects found by reading the code against the contracts must be recorded")
check("E11A-17_not_verified_recorded", len(contract.get("not_verified", [])) >= 3,
      "what this step does not verify must be written down")
runs = {r["id"]: r for r in contract["verified_runs"]}
check("E11A-17_runs_pass", len(runs) >= 5 and all(r["result"] == "PASS" for r in runs.values()),
      str({k: v["result"] for k, v in runs.items()}))
boundary_owners = {str(k) for k in contract.get("future_stage_boundaries", {})}
for owner in ("11B", "11C", "11D", "12", "16D"):
    check(f"E11A-17_boundary_{owner}", owner in boundary_owners, f"missing {owner}")

forbidden = set(contract.get("forbidden_today_patterns", []))
for pattern in ["rendering_a_plan_from_another_study_day", "offering_a_blocked_task_as_startable",
                "showing_the_previous_plan_while_replanning", "showing_a_reason_the_planner_never_recorded",
                "free_text_reason_field", "deriving_a_capacity_verdict_in_the_presentation",
                "letting_ai_absence_supersede_a_valid_local_plan",
                "implying_mastery_or_readiness_from_an_empty_day",
                "inventing_a_plan_the_planner_never_produced"]:
    check(f"E11A-18_forbidden_{pattern[:40]}", pattern in forbidden, f"missing={pattern}")
for fragment in ["**Status:** ACCEPTED — independent 11A QA PASS", "**Decision:** `D-087`",
                 "empty_valid", "study day", "12"]:
    check(f"E11A-19_spec_{fragment[:26]}", fragment in spec_text, f"missing={fragment!r}")
for fragment in ["TODO()", "planned_task", "empty_valid"]:
    check(f"E11A-20_research_{fragment}", fragment in research_text, f"missing={fragment!r}")

acceptance = contract.get("acceptance", {})
for key in ("independent_qa_required", "build_must_be_run_not_asserted",
            "truthfulness_rules_must_be_mutation_tested", "stage10_regression_required"):
    check(f"E11A-21_{key}", acceptance.get(key) is True, f"{key}={acceptance.get(key)}")

# ------------------------------------------------------------------ report
passed = sum(1 for r in results if r["result"] == "PASS")
report = {
    "model": "TDYX-v0", "stage_step": "11A", "decision": "D-087",
    "result": "PASS" if not failures else "FAIL",
    "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures),
    "semantic_states": len(kotlin_states), "reason_families": len(reason_ids),
    "checks": results, "failures": failures,
}
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"11A_TODAY_INTERIOR_QA={report['result']}")
print(f"checks={passed}/{len(results)} states={len(kotlin_states)} reasons={len(reason_ids)}")
for failure in failures:
    print("-", failure.encode("ascii", "backslashreplace").decode("ascii"))
sys.exit(0 if not failures else 1)
