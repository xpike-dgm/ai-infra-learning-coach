"""Independent 17E QA — RUXX-v0, the remaining UX parts.

The rules are read from `EODX-v0` (the day summary says only what an engine changed), `LHRX-v0` (changes are replayed
from evidence), `PDT-v0`/`RSNX-v0` (an explanation is the trace's projection), `UXIA-v0` (contextual edges are
enumerated; two were added by the user's decision, D-138), `AIAX-v0` §11 (only the minimum leaves the device, nothing
it carried is kept), `VDSX-v0`/`WFPX-v0` (measured tokens, 48dp) and `MOTX-v0` (reduced motion), and compared with the
real Kotlin, the schema, the emulator audit's reports, the tests, the spec, the contract and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a day summary that invents a change, says "nothing
changed" when it could not read the changes, or stays stale after a run; a Skill's plan that adds a reason the trace
did not record; an edge the IA contract does not list; an AI count that stores what a call carried, counts a call that
never left the device, writes zero for an unreported token count or puts a price on it; a motion switch that can bring
back motion Android removed; and a focus ring that is invisible, animated or not on every target.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
UI = ANDROID / "app-ui/src/main/kotlin/coach/ui"
PRES = ANDROID / "core-presentation/src/main/kotlin/coach/presentation"
APP = ANDROID / "core-application/src/main/kotlin/coach/application"
MODEL = ANDROID / "core-model/src/main/kotlin/coach"
WIRING = ANDROID / "app-wiring/src"
CONTRACT = ROOT / "arch/17e_remaining_ux/remaining_ux.yaml"
QA_OUT = ROOT / "arch/17e_remaining_ux/qa_report.yaml"
REPORT_100 = ROOT / "arch/17e_remaining_ux/a11y_emulator_report.yaml"
REPORT_200 = ROOT / "arch/17e_remaining_ux/a11y_emulator_report_text_200.yaml"
SPEC = ROOT / "docs/REMAINING_UX_SPEC.md"
RESEARCH = ROOT / "research/17e_remaining_ux_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
EXECUTION_INDEX = ROOT / "docs/EXECUTION_INDEX.md"
MASTER_PLAN = ROOT / "docs/MASTER_PLAN.md"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
SCHEMA = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
SQLITE = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
CLIENT = ANDROID / "ai-adapter/src/main/kotlin/coach/ai/OpenAiResponses.kt"
CONNECTION = ANDROID / "ai-adapter/src/main/kotlin/coach/ai/ConnectionCheck.kt"
ACTIVITY = WIRING / "main/kotlin/coach/wiring/MainActivity.kt"
APPLICATION = WIRING / "main/kotlin/coach/wiring/CoachApplication.kt"
AI_WIRING = WIRING / "withAi/kotlin/coach/wiring/AiWiring.kt"
NO_AI_WIRING = WIRING / "withoutAi/kotlin/coach/wiring/AiWiring.kt"
TESTS = {
    "RemainingUxTest": (ANDROID / "core-presentation/src/test/kotlin/coach/presentation/RemainingUxTest.kt", 9),
    "AiCallCountingTest": (ANDROID / "ai-adapter/src/test/kotlin/coach/ai/AiCallCountingTest.kt", 8),
    "DisplayAndUsageStorageTest": (ANDROID / "data-persistence/src/test/kotlin/coach/persistence/DisplayAndUsageStorageTest.kt", 7),
}
NEW_SCREENS = ["skill_detail", "skill_plan_explanation"]

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def code_only(src: str) -> str:
    src = re.sub(r"/\*.*?\*/", "", src, flags=re.S)
    return re.sub(r"(?m)^\s*//[^\n]*|\s//\s[^\n]*", "", src)


def body(src: str, start: str) -> str:
    """The braces-balanced block that follows the first `start`, or '' when it is absent."""
    at = src.find(start)
    if at < 0:
        return ""
    open_at = src.find("{", at)
    depth = 0
    for j in range(open_at, len(src)):
        if src[j] == "{":
            depth += 1
        elif src[j] == "}":
            depth -= 1
            if depth == 0:
                return src[at:j + 1]
    return src[at:]


def segment(src: str, start: str, end: str) -> str:
    """The text from `start` to the next `end` (for expression bodies and signatures that hold braces)."""
    at = src.find(start)
    if at < 0:
        return ""
    stop = src.find(end, at + len(start))
    return src[at:stop if stop > 0 else len(src)]


def load_yaml(path: Path):
    try:
        return yaml.safe_load(read(path)) or {}
    except yaml.YAMLError:
        return {}


contract = load_yaml(CONTRACT)
end_of_day = read(PRES / "EndOfDay.kt")
eod_view = read(UI / "EndOfDayView.kt")
day_close = read(APP / "DayCloseFacts.kt")
history = read(APP / "LearningHistory.kt")
explanation = read(PRES / "PlannerExplanation.kt")
explanation_copy = read(PRES / "ExplanationCopy.kt")
explanation_screen = read(UI / "PlannerExplanationScreen.kt")
navigation = read(PRES / "Navigation.kt")
progress_screen = read(UI / "ProgressScreen.kt")
english_screen = code_only(read(UI / "EnglishProfileScreen.kt"))
activity = read(ACTIVITY)
application = read(APPLICATION)
schema = read(SCHEMA)
client = read(CLIENT)
usage_app = read(APP / "AiUsage.kt")
usage_model = read(MODEL / "AiUsageFacts.kt")
usage_pres = read(PRES / "AiUsagePresentation.kt")
display_model = read(MODEL / "DisplayFacts.kt")
appearance = read(PRES / "AppearancePresentation.kt")
motion_core = read(PRES / "Motion.kt")
motion_ui = read(UI / "Motion.kt")
theme = read(UI / "CoachTheme.kt")
components = read(UI / "Components.kt")
profile = read(UI / "ProfileScreen.kt")
tokens = read(PRES / "DesignTokens.kt")

# ---------------------------------------------------------------- 1. identity
check("A17E-01_contract", contract.get("model") == "RUXX-v0" and contract.get("decision") == "D-138"
      and contract.get("stage_step") == "17E" and contract.get("added_by") == "D-136", str({k: contract.get(k) for k in ("model", "decision")}))
check("A17E-01_spec_accepted", "RUXX-v0" in read(SPEC) and "ACCEPTED" in read(SPEC) and "D-138" in read(SPEC), "")
check("A17E-01_research", "input_tokens" in read(RESEARCH) and "2.4.13" in read(RESEARCH), "")
decisions = (contract.get("user_decisions") or {})
check("A17E-01_user_decisions", "no money" in str(decisions.get("ai_usage", "")) and "focus ring" in str(decisions.get("scope", "")), "")

# ---------------------------------------------------------------- 2. the day summary says only what an engine changed
changes_of = segment(end_of_day, "fun of(facts: HistoryFacts", "fun input(")
check("A17E-02_change_words_are_13e", "ProgramChangeCopy.stateTemplates.getValue(change.kind)" in changes_of
      and "entry.changes.map" in changes_of, "a day change not in 13E's sentence, or not from an engine's change")
check("A17E-02_that_day_only", "it.studyDay == studyDay" in changes_of, "a change from another day")
check("A17E-02_named_by_skill", "CurriculumNames.skill(change.skill" in changes_of, "")
input_fn = segment(end_of_day, "fun input(", "private fun familyOf(")
check("A17E-02_unread_is_named", "if (changes == null) unreadKinds + UNREAD_KIND else unreadKinds" in input_fn, "unread changes are not named")
check("A17E-02_unread_is_not_nothing_changed", "if (DayChanges.UNREAD_KIND !in view.unreadKinds) Text(DayCopy.NOTHING_CHANGED" in eod_view,
      "the summary says nothing changed when it could not read the changes")
check("A17E-02_unread_has_words", '(DayChanges.UNREAD_KIND to "durum değişiklikleri")' in eod_view, "")
check("A17E-02_day_close_invents_none", "DayChange(" not in code_only(day_close)
      and "changes = runCatching { LearningHistoryQuery(persistence, content).day(studyDay) }.getOrNull()" in day_close,
      "the day's facts build a change of their own, or do not read the history's replay")
day_fn = body(history, "fun day(studyDay: String)")
check("A17E-02_replay_is_the_history", "HistoryReplay.steps(persistence, it.ref, persistence.objectivesOf(it.ref))" in day_fn
      and ".filter { it.row.studyDay == studyDay }" in day_fn and "it.studyDay == studyDay" in day_fn, "the day is not the history's replay for that day")
check("A17E-02_reads_only", not re.search(r"appendTruth\(|inTransaction\s*\{", code_only(day_fn)), "reading a day writes")
check("A17E-02_refreshed_after_a_run", "{ lines, facts, day ->" in activity and "onDayFacts(day)" in activity
      and "onDayFacts = { dayFacts.value = it }" in activity, "the day summary stays stale after a run")
check("A17E-02_summary_built_in_core", "EndOfDay.of(DayChanges.input(it.record, it.unreadKinds, it.changes, health))" in activity, "")

# ---------------------------------------------------------------- 3. the two edges
edges_block = body(navigation, "val contextualEdges")
check("A17E-03_edges_in_graph", '"skill_detail" to "planner_explanation",' in edges_block
      and '"technical_english_profile" to "skill_detail",' in edges_block, "")
check("A17E-03_one_way_only", '"planner_explanation" to "skill_detail"' not in edges_block
      and '"skill_detail" to "technical_english_profile"' not in edges_block, "")
ia = load_yaml(IA)
ia_edges = [tuple(e) for e in (ia.get("navigation_edges") or {}).get("contextual", [])]
check("A17E-03_edges_in_ia_contract", ("skill_detail", "planner_explanation") in ia_edges
      and ("technical_english_profile", "skill_detail") in ia_edges and "17E (D-138" in read(IA), str(ia_edges[-3:]))
code_edges = re.findall(r'"(\w+)" to "(\w+)",', edges_block)
check("A17E-03_graph_equals_contract", sorted(code_edges) == sorted(ia_edges), f"code={len(code_edges)} ia={len(ia_edges)}")
amend = contract.get("amendments") or []
check("A17E-03_amendment_recorded", any("UXIA-v0" in str(a.get("contract")) and "technical_english_profile -> skill_detail" in str(a.get("what"))
                                        and "skill_detail -> planner_explanation" in str(a.get("what")) for a in amend), "")
check("A17E-03_wired_through_the_graph", "NavigationGraph.canOpen(Surface.SkillDetail.id, Surface.PlannerExplanation.id)" in activity
      and "NavigationGraph.canOpen(Surface.TechnicalEnglishProfile.id, Surface.SkillDetail.id)" in activity, "")
for_skill = body(explanation, "fun forSkill(")
explain = body(explanation, "private fun explain(")
check("A17E-03_narrowed_not_new", "explain(facts.trace, facts.skillNames, only = skill)" in for_skill
      and ".filter { only == null || it.primarySkill == only || needs[it.needKey]?.targetSkills?.contains(only) == true }" in explain
      and "(only == null || only in it.targetSkills)" in explain and "val plan = if (only != null) emptyList()" in explain,
      "a Skill's plan is built some other way than the whole plan narrowed")
check("A17E-03_no_new_sentence", "Statement.ofCode(" not in for_skill and "Statement.ofFact(" not in for_skill, "forSkill adds a statement")
check("A17E-03_nothing_recorded_said", "SkillPlanCopy.NOT_MENTIONED" in explanation_screen
      and "explanation.state == ExplanationState.EXPLAINED && explanation.today.isEmpty() && explanation.notToday.isEmpty()" in explanation, "")
check("A17E-03_back_says_where", 'if (focus == null) "Bugün\'e dön" else SkillPlanCopy.BACK' in explanation_screen, "")
check("A17E-03_entry_row", "onOpenPlan?.let { open -> NavRow(SkillPlanCopy.ENTRY, open) }" in progress_screen
      and "const val ENTRY = " in explanation_copy, "")
check("A17E-03_english_rows_open_skill", "NavRow(chip.name, onClick = { onOpenSkill(chip.skill) }" in english_screen, "")

# ---------------------------------------------------------------- 4. AI calls counted, nothing carried, no money
ddl = re.search(r"CREATE TABLE IF NOT EXISTS ai_call_record \((.*?)\n\s*\)\n", schema, re.S)
columns = re.findall(r"^\s*(\w+)\s+(?:INTEGER|TEXT)", ddl.group(1), re.M) if ddl else []
time_cols = ["called_at_instant", "called_on_study_day", "utc_offset_minutes"] if ddl and 'time("called_at", "called_on")' in ddl.group(1) else []
check("A17E-04_counts_only_columns", columns == ["id", "call_use", "model", "outcome", "attempts", "input_usage", "output_usage"]
      and time_cols != [] and "$SEQUENCE" in ddl.group(1), f"columns={columns}")
check("A17E-04_no_content_or_credential_column", not [c for c in columns if re.search(r"text|message|content|prompt|reply|instruction|key|secret|token|password|price|cost", c)], str(columns))
check("A17E-04_tokens_nullable_never_negative", "input_usage            INTEGER," in schema and "CHECK (input_usage IS NULL OR input_usage >= 0)" in schema
      and "CHECK (output_usage IS NULL OR output_usage >= 0)" in schema and "CHECK (attempts >= 1)" in schema, "")
call_fn = body(client, "    ): ProviderOutcome {")
check("A17E-04_counted_once_when_it_left", "if (sent > 0) {" in call_fn and call_fn.count("onCall(") == 1
      and "outcomeOf(outcome), sent, tokens.first" in call_fn, "a call that never left is counted, or counted twice")
# Review (17E): a connection that never opened sent nothing, and the transport says so apart from any other failure.
provider = read(ANDROID / "ai-adapter/src/main/kotlin/coach/ai/Provider.kt")
not_sent = call_fn[call_fn.find("catch (notSent: NotSentException)"):call_fn.find("catch (timeout: SocketTimeoutException)")]
check("A17E-04_not_sent_is_not_a_call", "catch (notSent: NotSentException)" in call_fn and "sent++" not in not_sent
      and call_fn.find("catch (notSent: NotSentException)") < call_fn.find("catch (timeout: SocketTimeoutException)")
      and "throw NotSentException(timedOut = failure is java.net.SocketTimeoutException, cause = failure)" in provider
      and provider.find("connection.connect()") < provider.find("connection.outputStream.use"), "")
check("A17E-04_no_key_not_counted", "?: return ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE)" in call_fn
      and call_fn.find("?: return ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE)") < call_fn.find("fun ended("), "")
returns = re.findall(r"\breturn (?!ended\()(\S+)", code_only(call_fn))
check("A17E-04_every_end_counted", returns == ["ProviderOutcome.NotDelivered(PendingReason.UNAVAILABLE)", "outcome"], f"returns={returns}")
check("A17E-04_listener_cannot_fail_the_call", "runCatching { onCall(AiCall(" in call_fn, "")
usage_fn = body(client, "private fun usageOf(")
check("A17E-04_tokens_as_reported", '"usage"' in usage_fn and 'count("input_tokens") to count("output_tokens")' in usage_fn
      and "toLongOrNull()?.takeIf { it >= 0 }" in usage_fn and "?: return null to null" in usage_fn, "")
check("A17E-04_call_carries_nothing", re.search(r"data class AiCall\(([^)]*)\)", usage_model) is not None
      and not re.search(r"instruction|message|text|reply|schema|key", re.search(r"data class AiCall\(([^)]*)\)", usage_model).group(1)), "")
record_fn = body(usage_app, "fun record(")
check("A17E-04_unreported_is_not_zero", 'call.inputTokens?.let { put("input_usage"' in record_fn and 'call.outputTokens?.let { put("output_usage"' in record_fn, "")
check("A17E-04_written_after_the_job", "listenForAiCalls { call ->\n            storeThread.execute {" in application
      and "runCatching { AiUsage(opened.graph.persistence, opened.graph.clock).record(call) }" in application, "")
check("A17E-04_no_ai_build_counts_nothing", "internal fun listenForAiCalls(" in read(NO_AI_WIRING) and "= Unit" in read(NO_AI_WIRING).split("listenForAiCalls", 1)[1][:120]
      and "AiWiring.onCall = listener" in read(AI_WIRING), "")
check("A17E-04_connection_check_is_its_own_use", "AiCallUse.CONNECTION_CHECK)" in read(CONNECTION), "")
view_fields = " ".join(re.findall(r"data class (?:AiUsageLine|AiUsagePeriod|AiUsageView)\(([^)]*)\)", usage_pres))
check("A17E-04_no_money_field", view_fields and not re.search(r"price|cost|amount|money|budget|limit|ücret|tutar", view_fields, re.I), view_fields)
code_strings = re.findall(r'"([^"]*)"', code_only(usage_pres).replace(re.search(r"const val NO_PRICE =\s*\"[^\"]*\"", usage_pres).group(0), "") if "const val NO_PRICE" in usage_pres else "")
# Kotlin's own templates (${...}, $name) are not a currency sign; they are removed before the strings are read.
shown_strings = [re.sub(r"\$\{(?:[^{}]|\{[^{}]*\})*\}|\$\w+", "", s) for s in code_strings]
check("A17E-04_no_currency_on_screen", not [s for s in shown_strings if re.search(r"[$€₺]|\bTL\b|USD|dolar|lira|tutar", s)], "")
check("A17E-04_price_not_invented_said", "Uygulama bir ücret hesaplamaz" in usage_pres, "")
check("A17E-04_inventory_by_study_day", "it.studyDay.take(MONTH_PREFIX) == month" in usage_pres, "")

# ---------------------------------------------------------------- 5. theme and motion
check("A17E-05_theme_values", 'val themeValues = listOf("system", "light", "dark")' in schema and "CHECK (reduce_motion IN (0, 1))" in schema, "")
check("A17E-05_motion_only_adds", "fun of(animatorDurationScale: Float, inApp: Boolean = false): Boolean = inApp || animatorDurationScale <= 0f" in motion_core, "")
check("A17E-05_theme_applied", "CoachTheme(dark = dark, reduceMotionInApp = display.reduceMotion)" in activity
      and "val dark = display.dark(isSystemInDarkTheme())" in activity and "LocalReducedMotion provides platformReducedMotion(reduceMotionInApp)" in theme, "")
check("A17E-05_system_until_read", "displayState.value ?: DisplayPreference.SYSTEM_DEFAULT" in activity and "ThemeChoice.SYSTEM -> systemDark" in display_model, "")
check("A17E-05_bars_follow_app_theme", "LaunchedEffect(dark)" in activity and "enableEdgeToEdge(statusBarStyle = bars, navigationBarStyle = bars)" in activity, "")
appearance_ui = body(profile, "private fun AppearanceSection(")
check("A17E-05_choice_is_one_group", "Modifier.selectableGroup()" in appearance_ui and "role = Role.RadioButton" in appearance_ui
      and "role = Role.Switch" in appearance_ui and appearance_ui.count("minimumTouchTarget()") == 2, "")
check("A17E-05_note_says_which_setting", "systemReducedMotion -> AppearanceCopy.MOTION_SYSTEM" in appearance, "")
check("A17E-05_replans_nothing", "PlanToday" not in body(application, "fun setDisplay(") and "BuildDailyPlan" not in body(application, "fun setDisplay("), "")

# ---------------------------------------------------------------- 6. the focus ring
ring = body(theme, "fun Modifier.focusRing()")
check("A17E-06_ring_on_every_target", "this.focusRing().sizeIn(" in body(theme, "fun Modifier.minimumTouchTarget()"), "")
check("A17E-06_measured_token", 'LocalFocusRing provides tokenColor(theme, "focus_ring")' in theme
      and "const val FOCUS_RING_WIDTH_DP = 2" in tokens and "2.4.13" in tokens, "")
check("A17E-06_only_while_focused", "onFocusChanged { focused = it.hasFocus }" in ring and "if (focused && ring != Color.Unspecified)" in ring, "")
check("A17E-06_outside_the_target", "topLeft = Offset(-outset, -outset)" in ring and "drawContent()" in ring, "")
check("A17E-06_not_animated", not re.search(r"animate|Animatable|tween|spring", ring), "")
# Review (17E): the shell's destinations are not 48dp-modifier targets, so they carry the ring themselves.
check("A17E-06_tabs_carry_the_ring", "modifier = Modifier.focusRing().semantics {" in read(UI / "AppShell.kt"), "")
check("A17E-06_navrow_ring_sees_focus", re.search(r"\.minimumTouchTarget\(\)[\s\S]{0,160}?\.clickable\(", segment(components, "fun NavRow(", "fun BackButton(")) is not None, "")

# ---------------------------------------------------------------- 7. schema v12
# Narrowed at 18A (`D-139`): version 13 adds the setup record after v12; v12 itself is what this checks.
_v12_end = schema.find("val v13: List<String>") if "val v13: List<String>" in schema else schema.find("const val MISCONCEPTION_SOURCES")
v12 = schema[schema.find("val v12: List<String>"):_v12_end]
_version = re.search(r"const val VERSION = (\d+)", schema)
check("A17E-07_version", _version is not None and int(_version.group(1)) >= 12 and "11 to Schema.v12," in code_only(read(MIGRATIONS)), "")
check("A17E-07_only_adds", len(re.findall(r"CREATE TABLE IF NOT EXISTS", v12)) == 2 and not re.search(r"\b(ALTER|DROP|UPDATE|DELETE)\b", code_only(v12).replace("appendOnlyGuards", "")), "")
check("A17E-07_guarded_append_only", "(displayPreferenceTables + aiUsageTables).flatMap(::appendOnlyGuards)" in v12
      and "+ displayPreferenceTables + aiUsageTables" in schema, "")
mig = contract.get("schema_migration") or {}
check("A17E-07_owned", mig.get("from") == 11 and mig.get("to") == 12 and mig.get("truth_altered") is False, str(mig))
check("A17E-07_reads_known_values", "requireNotNull(ThemeChoice.of(" in read(SQLITE) and "requireNotNull(AiCallUse.of(" in read(SQLITE), "")

# ---------------------------------------------------------------- 8. tests
for name, (path, least) in TESTS.items():
    src = read(path)
    check(f"A17E-08_tests_{name}", src.count("@Test") >= least, f"{src.count('@Test')} < {least}")
lh = read(ANDROID / "core-application/src/test/kotlin/coach/application/LearningHistoryTest.kt")
check("A17E-08_tests_day_replay", "LearningHistoryQuery(store).day(" in lh and "DayCloseFacts(store, clock).load(" in lh, "")
ru = read(TESTS["RemainingUxTest"][0])
check("A17E-08_tests_cover_the_rules", all(s in ru for s in ("DayChanges.UNREAD_KIND", "forSkill(", "notMentioned", "ReducedMotion.of(0f, inApp = false)",
                                                                "focus_ring", "AiUsageCopy.NO_PRICE")), "")

# ---------------------------------------------------------------- 9. the real app, audited again
for label, path, scale in (("100", REPORT_100, 1.0), ("200", REPORT_200, 2.0)):
    rep = load_yaml(path)
    screens = rep.get("screens") or []
    names = [s.get("screen") for s in screens]
    check(f"A17E-09_report_{label}_passed", rep.get("result") == "PASS" and rep.get("findings_total") == 0 and rep.get("step") == "17E", str(rep.get("result")))
    check(f"A17E-09_report_{label}_font_scale", rep.get("font_scale") == scale, str(rep.get("font_scale")))
    check(f"A17E-09_report_{label}_new_screens", all(n in names for n in NEW_SCREENS) and "profile_overview" in names and len(names) >= 16, str(names))
    check(f"A17E-09_report_{label}_reached_and_whole", screens and all(s.get("marker_found") and s.get("read_to_end") and not s.get("clipped_never_judged")
                                                                          and s.get("unanchored_readings", 0) == 0 for s in screens), "")
tool = read(ROOT / "tools/audit_a11y_emulator.py")
check("A17E-09_tool_visits_new_screens", 'visit("skill_detail"' in tool and 'visit("skill_plan_explanation"' in tool and "def tap_row_scrolling(" in tool, "")

# ---------------------------------------------------------------- 10. the contract says what was not done
ver = contract.get("verification") or {}
check("A17E-10_not_claimed", ver.get("t6_run") is False and ver.get("live_provider_call") is False, "")
found = {f.get("id") for f in (contract.get("found") or [])}
check("A17E-10_found_recorded", {"day_summary_never_refreshed_after_a_run", "first_run_report_and_day_summary_disagree",
                                 "system_bars_followed_the_system", "token_column_looks_like_a_credential"} <= found, str(sorted(found)))
check("A17E-10_open_loop_named", any("13E report or the history's replay" in str(m) for m in contract.get("moved_on") or []), "")
for key in ("mutation_results_kotlin", "mutation_results_validator"):
    m = contract.get(key) or {}
    check(f"A17E-11_{key}", isinstance(m.get("total"), int) and m.get("total") > 0 and m.get("detected") == m.get("total")
          and m.get("negative_control_result") == "survived_as_expected", str({k: m.get(k) for k in ("total", "detected")}))
check("A17E-11_independent_review", "found" in str(ver.get("independent_review", "")) and "pending" not in str(ver.get("independent_review", "")), "")

# ---------------------------------------------------------------- 11. the living record
dec = read(DECISIONS)
check("A17E-11_decision_recorded", "## D-138" in dec and "RUXX-v0" in dec, "")
check("A17E-11_execution_index", re.search(r"\[x\] \*\*17E", read(EXECUTION_INDEX)) is not None, "")
check("A17E-11_master_plan", "### [x] 17E" in read(MASTER_PLAN), "")

passed = sum(r["result"] == "PASS" for r in results)
QA_OUT.parent.mkdir(parents=True, exist_ok=True)
QA_OUT.write_text(yaml.safe_dump({"step": "17E", "model": "RUXX-v0", "checks": len(results), "passed": passed, "results": results},
                                 allow_unicode=True, sort_keys=False, width=200), encoding="utf-8", newline="\n")
print(f"17E QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
for f in failures:
    print("FAIL", f)
sys.exit(0 if not failures else 1)
