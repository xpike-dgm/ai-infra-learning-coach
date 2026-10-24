"""Independent 16B QA — LHRX-v0 learning history and longitudinal assessment report (with the D-123 mastery correction).

The rules are read from the accepted contracts — `SPWX-v0` (`ux/8e_progress_skill_weakness/progress.yaml`: the history's
event families and prohibitions, the report's families), `ASUX-v0` (the six families and the not-reliably-measured
reasons, as `core-presentation` already declares them), `PCRX-v0` (the change vocabulary and sentences), `GRE-v0` §16.2
(`docs/MASTERY_FORMULA_V0.md`) and `MSBX-v0` — and compared with the real Kotlin, the contract, the tests, the spec,
the research and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a history that writes truth or projections, a change
no engine made, a streak, attendance count or a day drawn as missed, a grade or trend across sessions, a plan claimed
unchanged that nobody found, stored conversation text, and a mastery rebuild that gives two answers for the same evidence
— a verification closed by the row that opened it, or a passed recheck that takes mastery away.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/16b_learning_history/learning_history.yaml"
QA_OUT = ROOT / "arch/16b_learning_history/qa_report.yaml"
SPEC = ROOT / "docs/LEARNING_HISTORY_SPEC.md"
RESEARCH = ROOT / "research/16b_learning_history_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
SPWX = ROOT / "ux/8e_progress_skill_weakness/progress.yaml"
GRE = ROOT / "docs/MASTERY_FORMULA_V0.md"

ENGINE = ANDROID / "core-engines/src/main/kotlin/coach/engines/MasteryEngine.kt"
REBUILD = ANDROID / "core-application/src/main/kotlin/coach/application/RebuildMastery.kt"
TIMELINE = ANDROID / "core-application/src/main/kotlin/coach/application/MasteryTimeline.kt"
HISTORY = ANDROID / "core-application/src/main/kotlin/coach/application/LearningHistory.kt"
FACTS = ANDROID / "core-model/src/main/kotlin/coach/HistoryFacts.kt"
EVIDENCE_FACTS = ANDROID / "core-model/src/main/kotlin/coach/EvidenceFacts.kt"
PORTS = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
ADAPTER = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
SCHEMA = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/HistoryPresentation.kt"
SESSION_PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/AssessmentSession.kt"
CHANGE_FACTS = ANDROID / "core-model/src/main/kotlin/coach/ProgramChangeFacts.kt"
SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/HistoryScreen.kt"
ACTIVITY = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
APPLICATION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
ENGINE_TEST = ANDROID / "core-engines/src/test/kotlin/coach/engines/MasteryEngineTest.kt"
HISTORY_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/LearningHistoryTest.kt"
PRESENTATION_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/HistoryPresentationTest.kt"
STORAGE_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/HistoryStorageTest.kt"
COURSE_TEST = ANDROID / "app-wiring/src/test/kotlin/coach/wiring/ShippedCourseTest.kt"

results: list[dict] = []
failures: list[str] = []


def check(check_id: str, condition: bool, details: str = "") -> None:
    results.append({"check": check_id, "result": "PASS" if condition else "FAIL", "details": details})
    if not condition:
        failures.append(f"{check_id}: {details}")


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.is_file() else ""


def load(path: Path):
    return yaml.safe_load(read(path)) if path.is_file() else None


def strip_comments(source: str) -> str:
    source = re.sub(r"/\*.*?\*/", "", source, flags=re.S)
    return re.sub(r"(?<![:\"])//[^\n]*", "", source)


def body(source: str, signature: str) -> str:
    start = source.find(signature)
    if start < 0:
        return ""
    open_at = source.find("{", start)
    depth = 0
    for i in range(open_at, len(source)):
        depth += {"{": 1, "}": -1}.get(source[i], 0)
        if depth == 0:
            return source[start:i + 1]
    return source[start:]


def enum_ids(source: str, name: str) -> list[str]:
    return re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"', body(source, f"enum class {name}("), re.M)


def finish() -> None:
    passed = sum(1 for r in results if r["result"] == "PASS")
    out = {"model": "LHRX-v0", "stage_step": "16B", "decision": "D-123", "result": "PASS" if not failures else "FAIL",
           "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
    if "--no-write" not in sys.argv:
        QA_OUT.parent.mkdir(parents=True, exist_ok=True)
        QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
    print(f"16B QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(0 if not failures else 1)


for path in (CONTRACT, SPEC, RESEARCH, SPWX, GRE, ENGINE, REBUILD, TIMELINE, HISTORY, FACTS, EVIDENCE_FACTS, PORTS, ADAPTER,
             SCHEMA, PRESENTATION, SESSION_PRESENTATION, CHANGE_FACTS, SCREEN, ACTIVITY, APPLICATION, ENGINE_TEST,
             HISTORY_TEST, PRESENTATION_TEST, STORAGE_TEST, COURSE_TEST):
    check(f"A16B-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
spwx = load(SPWX) or {}
engine = strip_comments(read(ENGINE))
rebuild = strip_comments(read(REBUILD))
timeline = strip_comments(read(TIMELINE))
history = strip_comments(read(HISTORY))
facts = strip_comments(read(FACTS))
presentation_raw = read(PRESENTATION)
presentation = strip_comments(presentation_raw)
screen = strip_comments(read(SCREEN))
activity = strip_comments(read(ACTIVITY))

# ---------------------------------------------------------------- contract
check("A16B-01_model", contract.get("model") == "LHRX-v0" and contract.get("stage_step") == "16B"
      and contract.get("status") == "accepted_16b" and contract.get("decision") == "D-123", "")
ud = contract.get("user_decisions") or {}
check("A16B-01_user_decisions", ud.get("approval") == "explicit_user_approval_2026-10-17"
      and ud.get("what_changed_source") == "replay_engines_over_stored_evidence_no_new_table"
      and ud.get("scope") == "learning_history_and_longitudinal_assessment_report"
      and ud.get("stored_text") == "no_tutor_or_learner_text_assistance_levels_only_working_session_unstored"
      and ud.get("delivery") == "core_and_real_compose_screen"
      and ud.get("engine_fix") == "fix_mastery_recheck_inside_16b_declared_2026-10-18", str(ud))
scope = contract.get("scope") or {}
check("A16B-01_scope", all(scope.get(k) is False for k in ("schema_changed", "migration_added", "new_truth_table", "boundaries_changed",
      "retention_rule_changed", "weakness_rule_changed", "planner_rule_changed", "conversation_text_stored", "working_session_stored"))
      and scope.get("mastery_rule_changed") == "corrected_to_gre_v0_16_2", str(scope))

# ---------------------------------------------------------------- the mastery correction (GRE-v0 §16.2)
gre = read(GRE)
check("A16B-02_gre_says_positive_recheck_keeps", "### Recheck positive" in gre and "mastered korunur" in gre and "### Recheck de negative" in gre, "")
decide = body(engine, "fun decide(")
check("A16B-02_state_carried", "data class VerificationState(" in engine and "val openedAt: Long? = null" in engine
      and "val handled: Set<Long> = emptySet()" in engine and "val forgiven: Set<Long> = emptySet()" in engine
      and "verification: VerificationState = VerificationState.NONE" in decide, "")
# The boolean parameter, not retention's own `unresolvedVerificationEvidenceId` (13C), which names its evidence.
check("A16B-02_no_open_flag_left", not re.search(r"\bunresolvedVerification\b", "".join(strip_comments(read(p)) for p in ANDROID.rglob("*.kt")
      if "/src/main/" in p.as_posix() and "/build/" not in p.as_posix())), "the boolean that could not tell a fresh recheck is back")
check("A16B-02_only_later_rows_are_a_recheck", "val fresh = eligible.filter { it.sequence > verification.openedAt!! }" in decide, "")
check("A16B-02_no_fresh_check_holds", "else -> true to verification" in decide, "")
check("A16B-02_failed_recheck_resolves", "freshContradictions.isNotEmpty() -> objectivePassed to" in decide, "")
check("A16B-02_positive_recheck_keeps", "fresh.any { it.outcome == EvidenceOutcome.POSITIVE } -> true to" in decide
      and "forgiven = verification.forgiven + verification.openedBy" in decide, "")
check("A16B-02_forgiven_leave_the_window", "val scored = eligible.filter { it.id !in verification.forgiven }" in decide
      and "val window = recentWindow(groupsOf(scored))" in decide, "")
check("A16B-02_opening_row_handled", "handled = verification.handled + unhandled.map { it.id }," in decide, "")
check("A16B-02_only_eligible_rows", "val fresh = eligible." in decide and "val unhandled = eligible.filter" in decide,
      "assisted or provisional work could act as a recheck")
replay = body(engine, "fun replay(")
check("A16B-03_replay_from_evidence", "decisions.forEach { states[it.objective] = it.verification }" in replay
      and ".also { mastered = it.mastered }" in replay and "listOf(Long.MIN_VALUE)" in replay, "")
check("A16B-03_rebuild_uses_replay", "MasteryEngine.replay(skill, profiles, rows).last()" in rebuild
      and "previousAxis" not in rebuild and "MasteryEngine.decide(" not in rebuild, "")
check("A16B-03_timeline_uses_replay", "MasteryEngine.replay(skill, profiles, rows)" in body(timeline, "fun of(")
      and "MasteryEngine.decide(" not in timeline, "")

# ---------------------------------------------------------------- the history
families = enum_ids(facts, "HistoryFamily")
check("A16B-04_families_are_spwx", sorted(families) == sorted((spwx.get("learning_history") or {}).get("event_families", [])), str(families))
check("A16B-04_family_precedence", families == ["assessment", "remediation", "review", "learning"]
      and (contract.get("history") or {}).get("families") == families, "")
prefix = body(history, "internal class PrefixPersistence(")
check("A16B-05_replay_writes_nothing", 'override fun appendTruth(record: TruthRecord): Long = error("a replay writes no truth")' in prefix
      and "projections[record.key] = record" in prefix and "base.writeProjection" not in history and "base.appendTruth" not in history, "")
check("A16B-05_replay_cuts_evidence", "evidence[objective].orEmpty().filter { it.sequence <= cutoff }" in prefix
      and "override fun truthWatermark(): Long = cutoff" in prefix, "")
steps = body(history, "fun steps(")
check("A16B-05_replay_same_rebuilds", all(s in steps for s in ("RebuildMastery(overlay, clock).rebuild(skill, profiles)",
      "RebuildRetention(overlay, clock).rebuild(skill, profiles)", "RebuildWeakness(overlay, clock).rebuild(skill, profiles)")), "")
check("A16B-05_known_start_and_own_day", "read(rows.first().sequence - 1" in steps and "StudyTimestamp(0L, day, 0)" in steps
      and "read(row.sequence" in steps, "")
check("A16B-05_changes_are_13e", "ProgramChangeEngine.stateChanges(previous, after)" in steps, "")
query = body(history, "class LearningHistoryQuery(")
check("A16B-05_query_only_reads", not re.search(r"appendTruth|writeProjection|inTransaction", query), "")
reports = body(history, "private fun reports(")
check("A16B-06_session_changes_its_own", "mine.flatMap { it.changes }.distinctBy { it.skill to it.kind }" in reports, "")
check("A16B-06_plan_found_or_not_claimed", "trace.generationKind == GenerationKind.REPLAN.id" in reports and "trace.truthWatermark < next" in reports
      and ".takeIf { it > 0 }" in reports and "replanFound = replan != null," in reports, "")
check("A16B-06_not_measured", "NotMeasured(mineEntries.filter { it.kind != EvidenceKind.INDEPENDENT }, unanswered)" in reports, "")
check("A16B-06_slot_answered_by_session_evidence", "item == null || item !in answered" in reports, "")

# ---------------------------------------------------------------- what is read and stored
ports = read(PORTS)
check("A16B-07_port_refinements_defaulted", "fun planVersions(): List<StoredPlan> = listOfNotNull(latestPlan())" in ports
      and "fun assessmentSessions(): List<StoredTruth> = AssessmentScope.entries.mapNotNull { latestAssessmentSession(it) }" in ports, "")
interfaces = re.findall(r"^interface (\w+)", ports, re.M)
check("A16B-07_port_count_unchanged", sorted(interfaces) == sorted(["ClockPort", "PersistencePort", "ContentPort", "EvaluatorPort", "TutorPort"]), str(interfaces))
adapter = read(ADAPTER)
check("A16B-07_assistance_levels_read", "SELECT group_concat(level) FROM (SELECT DISTINCT s.level FROM assistance_event s" in adapter
      and "WHERE s.attempt_id = e.source_attempt_id ORDER BY s.level" in adapter, "")
check("A16B-07_all_versions_in_order", 'query("SELECT id FROM plan_version ORDER BY sequence") { it.getLong(0) }.mapNotNull(::planOf)' in adapter
      and 'query("SELECT id FROM assessment_session ORDER BY sequence")' in adapter, "")
schema = read(SCHEMA)
check("A16B-07_no_new_table", not re.search(r"CREATE TABLE IF NOT EXISTS (?:\w*history\w*|\w*conversation\w*|tutor_message\w*)", schema), "")
check("A16B-07_no_text_on_evidence", "val assistanceLevels: List<AssistanceLevel> = emptyList()" in read(EVIDENCE_FACTS)
      and not re.search(r"val (?:message|transcript|conversation|tutorText)", read(EVIDENCE_FACTS)), "")

# ---------------------------------------------------------------- the surface
check("A16B-08_changes_in_13e_words", "ProgramChangeCopy.stateTemplates.getValue(it.kind)" in presentation
      and "ProgramChangeCopy.stateTemplates.getValue(change.kind)" in presentation and ".ifEmpty { listOf(HistoryCopy.NO_CHANGE) }" in presentation, "")
check("A16B-08_report_families_are_asux", enum_ids(read(SESSION_PRESENTATION), "ResultFamily") == (spwx.get("assessment_report") or {}).get("semantic_families")
      and "ResultFamily.entries.mapNotNull" in presentation, "")
check("A16B-08_change_families_are_asux", set(re.findall(r'"[a-z_]+", "([a-z_]+)", "replan', read(CHANGE_FACTS))) <= set((spwx.get("assessment_report") or {}).get("semantic_families", [])), "")
check("A16B-08_reasons_are_asux", enum_ids(read(SESSION_PRESENTATION), "NotReliablyMeasured") == (contract.get("assessment_report") or {}).get("not_reliably_measured_reasons"), "")
check("A16B-08_unfound_plan_not_claimed", "!report.replanFound -> HistoryCopy.STATE_UNCHANGED" in presentation, "")
check("A16B-08_days_only_with_entries", "facts.entries.sortedByDescending { it.sequence }.groupBy { it.studyDay.orEmpty() }" in presentation
      and "LocalDate" not in presentation and "plusDays" not in presentation, "a day without an entry could be drawn")
literals = re.findall(r'"((?:\\.|[^"\\])*)"', presentation_raw) + re.findall(r'"((?:\\.|[^"\\])*)"', read(SCREEN))
forbidden = ["%", "puan", "skor", "seri", "streak", "kaçırdın", "başarısız", "yanlış", "geride", "borç", "üst üste"]
bad = [s for s in literals if any(w in s.lower() for w in forbidden) or re.search(r"\d+\s*/\s*\d+", s)]
check("A16B-08_no_score_or_blame", bad == [], str(bad[:3]))
check("A16B-08_no_aggregate_field", not re.search(r"val (?:score|average|trend|streak|grade|total)\w*\s*:", presentation + facts, re.I), "")
check("A16B-09_screen_decides_nothing", "coach.application" not in screen and not re.search(r"LazyVerticalGrid|Calendar|ProgressIndicator|heatmap", screen), "")
check("A16B-09_wired_on_accepted_edges", all(s in activity for s in (
    "NavigationGraph.canOpen(Destination.PROGRESS.id, Surface.LearningHistory.id)",
    "NavigationGraph.canOpen(Destination.PROGRESS.id, Surface.AssessmentReport.id)",
    "NavigationGraph.canOpen(Surface.SkillDetail.id, Surface.LearningHistory.id)",
    "NavigationGraph.canOpen(Surface.AssessmentReport.id, Surface.LearningHistory.id)")) and "LearningHistoryQuery(opened.graph.persistence)" in read(APPLICATION), "")

# ---------------------------------------------------------------- tests name what they guard
for path, names in ((ENGINE_TEST, ["the row that opened a verification is never its own recheck, however often it is decided",
                                   "a clean positive recheck closes the verification and keeps mastery",
                                   "a failed recheck resolves the verification and lets the gates decide again",
                                   "replaying the same evidence always gives the same decisions"]),
                    (HISTORY_TEST, ["recomputing again changes nothing, and a passed recheck keeps mastery (D-123)",
                                    "replaying writes nothing and ends where the live recompute ended",
                                    "a session's report says what its own program change report said, plan changes included",
                                    "without a plan written for the session, no plan change is claimed"]),
                    (PRESENTATION_TEST, ["days appear only when something happened, newest first, and nothing marks a gap",
                                         "nothing changed is said plainly, and an unfound plan is not claimed unchanged"]),
                    (STORAGE_TEST, ["an evidence row carries its attempt and the distinct assistance levels recorded on it, in order"]),
                    (COURSE_TEST, ["with all eight packages, a learner who has done nothing has no history, and recorded work is replayed as the engines decided"])):
    text = read(path)
    for name in names:
        check(f"A16B-10_test_{re.sub(r'[^a-z0-9]+', '_', name[:40].lower()).strip('_')}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- narrowed gates are declared where they are
for gate in (contract.get("narrowed_gates") or {}).get("gates", []):
    text = read(ROOT / gate["file"])
    check(f"A16B-11_narrowed_{Path(gate['file']).stem}", "Narrowed at 16B (`LHRX-v0` / `D-123`)" in text, gate["file"])

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results_kotlin") if isinstance(contract.get("mutation_results_kotlin"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("A16B-12_kotlin_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("A16B-12_kotlin_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("A16B-12_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("A16B-12_runs_pass", len(runs) >= 6 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") or {}
check("A16B-12_device_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("screen_seen_on_device") is False
      and dv.get("sqlite_read_on_jvm_run") is True, "")

# ---------------------------------------------------------------- spec, research, decision
spec = read(SPEC)
check("A16B-13_spec_status", "**Status:** ACCEPTED — independent 16B QA PASS" in spec and "`D-123`" in spec
      and not re.search(r"\b(?:MK|MV|QA)_RESULT\b|__[A-Z][A-Z_]*__", spec), "no result placeholder is left")
check("A16B-13_spec_next", "**16C — Progress / weakness gösterim kuralları**" in spec and "**Not run: T6.**" in spec, "")
research = read(RESEARCH)
check("A16B-13_research", all(r in research for r in ("SPWX-v0", "ASUX-v0", "PCRX-v0", "GRE-v0", "LFPS-v0",
                                                      "https://en.wikipedia.org/wiki/Goodhart%27s_law")), "")
check("A16B-13_decision", re.search(r"^#+ .*D-123", read(DECISIONS), re.M) is not None, "D-123 heading in DECISIONS")

finish()
