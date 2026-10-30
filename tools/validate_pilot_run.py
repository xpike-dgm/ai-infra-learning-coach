"""18G validator (PLTX-v0 / D-141): the pilot journal records and never acts, and the PC report says only what rows say.

Checks the journal's closed kinds across model, schema and copy; schema v14 adds one guarded table and alters nothing;
recording writes one row and never plans; the Today row and its dialog; the TEXT binding fix; the tests; the PC report
run against a database built from the real `Schema.kt` text; the contract; and the accessibility audits.
"""
from __future__ import annotations

import hashlib
import importlib.util
import re
import sqlite3
import sys
import tempfile
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parent.parent
A = ROOT / "android"
SCHEMA = A / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS = A / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
ADAPTER = A / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
FACTS = A / "core-model/src/main/kotlin/coach/PilotJournalFacts.kt"
RECORD = A / "core-application/src/main/kotlin/coach/application/RecordPilotJournal.kt"
COPY = A / "core-presentation/src/main/kotlin/coach/presentation/PilotJournalPresentation.kt"
DIALOG = A / "app-ui/src/main/kotlin/coach/ui/PilotJournalDialog.kt"
TODAY = A / "app-ui/src/main/kotlin/coach/ui/TodayScreen.kt"
MAIN = A / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
APP = A / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
TOOL = ROOT / "tools/pilot_report.py"
CONTRACT = ROOT / "arch/18g_pilot_run/pilot_run.yaml"
OUT = ROOT / "arch/18g_pilot_run/qa_report.yaml"
TESTS = {
    "PilotJournalCourseTest": A / "app-wiring/src/test/kotlin/coach/wiring/PilotJournalCourseTest.kt",
    "PilotJournalStorageTest": A / "data-persistence/src/test/kotlin/coach/persistence/PilotJournalStorageTest.kt",
    "PilotJournalPresentationTest": A / "core-presentation/src/test/kotlin/coach/presentation/PilotJournalPresentationTest.kt",
    "PilotJournalFactsTest": A / "core-model/src/test/kotlin/coach/model/PilotJournalFactsTest.kt",
}
KINDS = ["prerequisite_impossible", "time_wrong", "wrong_branch", "personal_preference", "rule_violation"]

results: list[dict] = []


def check(cid: str, ok: bool, detail: str = "") -> None:
    results.append({"id": cid, "result": "PASS" if ok else "FAIL", **({"detail": detail} if detail and not ok else {})})


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.exists() else ""


def code_only(text: str) -> str:
    """Kotlin without line comments, KDoc or block comments (string contents are kept)."""
    text = re.sub(r"/\*.*?\*/", "", text, flags=re.S)
    return "\n".join(re.sub(r"(^|\s)//.*$", "", line) for line in text.splitlines())


def segment(text: str, start: str, end: str) -> str:
    i = text.find(start)
    if i < 0:
        return ""
    j = text.find(end, i + len(start))
    return text[i:j if j > 0 else len(text)]


def body(text: str, signature: str) -> str:
    """The braces-balanced body that follows [signature]."""
    i = text.find(signature)
    if i < 0:
        return ""
    i = text.find("{", i)
    depth = 0
    for j in range(i, len(text)):
        depth += {"{": 1, "}": -1}.get(text[j], 0)
        if depth == 0:
            return text[i:j + 1]
    return text[i:]


schema = read(SCHEMA)
facts, record, copy, dialog = read(FACTS), read(RECORD), read(COPY), read(DIALOG)
today, main, app, adapter = read(TODAY), read(MAIN), read(APP), read(ADAPTER)

# ---------------------------------------------------------------- 1. kinds, closed and the same everywhere
model_kinds = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)", structural', facts, flags=re.M)
schema_kinds = re.findall(r'"([a-z_]+)"', segment(schema, "val pilotJournalKindValues", "\n"))
check("A18G-01_kinds_closed", model_kinds == KINDS and schema_kinds == KINDS, f"{model_kinds} / {schema_kinds}")
structural = re.findall(r'\("([a-z_]+)", structural = true', facts)
violation = re.findall(r'\("([a-z_]+)", structural = false, ruleViolation = true', facts)
check("A18G-01_sc049_three_structural", structural == KINDS[:3], str(structural))
check("A18G-01_sc048_one_violation_kind", violation == ["rule_violation"], str(violation))
labels = re.findall(r"PilotJournalKind\.([A-Z_]+) to \"([^\"]+)\"", copy)
check("A18G-01_every_kind_labelled_once", len(labels) == 5 and len({l for _, l in labels}) == 5, str(labels))
check("A18G-01_sc049_words", {"Ön koşul yüzünden imkânsız", "Süre tamamen yanlış", "Yanlış dala götürüyor"} <= {l for _, l in labels})

# ---------------------------------------------------------------- 2. schema v14
v14 = segment(schema, "val v14: List<String>", "const val MISCONCEPTION_SOURCES")
check("A18G-02_version", "const val VERSION = 14" in schema and "13 to Schema.v14," in code_only(read(MIGRATIONS)))
check("A18G-02_only_adds", len(re.findall(r"CREATE TABLE IF NOT EXISTS", v14)) == 1
      and not re.search(r"\b(ALTER|DROP|UPDATE|DELETE)\b", code_only(v14)))
check("A18G-02_guarded", "pilotJournalTables.flatMap(::appendOnlyGuards)" in v14 and "pilotJournalTables" in segment(schema, "val appendOnlyTables", "\n\n"))
check("A18G-02_note_short_and_trimmed", "CHECK (length(note) <= 200 AND note = trim(note))" in v14 and "const val NOTE_LIMIT = 200" in facts)
check("A18G-02_names_the_plan", "FOREIGN KEY (plan_version_id) REFERENCES plan_version (id)" in v14 and re.search(r"plan_version_id\s+INTEGER,", v14) is not None)
check("A18G-02_no_credential_like_column", not re.search(r"\b\w*(token|secret|key|password)\w*\s+(TEXT|INTEGER)", v14, re.I))

# ---------------------------------------------------------------- 3. recording observes and never acts
rec = code_only(record)
check("A18G-03_one_row", rec.count("appendTruth(") == 1 and 'const val KIND = "pilot_journal_entry"' in rec)
check("A18G-03_never_plans", not re.search(r"BuildDailyPlan|PlanToday|Replan|CapacitySettings|setToday|RequestDiagnostic|EvidencePipeline", rec))
check("A18G-03_one_transaction", "persistence.inTransaction { persistence.appendTruth(" in rec)
check("A18G-03_names_latest_plan", "persistence.latestPlan()?.planVersionId" in rec)
check("A18G-03_too_long_writes_nothing", rec.find("return Result.NoteTooLong") < rec.find("appendTruth(") and rec.find("return Result.NoteTooLong") > 0)
rec_app = code_only(body(app, "fun recordPilotJournal("))
check("A18G-03_app_does_not_replan", "RecordPilotJournal(" in rec_app and not re.search(r"PlanToday|BuildDailyPlan|TodayFactsQuery|refresh", rec_app), rec_app[:200])
check("A18G-03_app_off_main_thread", "storeThread.execute" in rec_app and "main.post" in rec_app)

# ---------------------------------------------------------------- 4. Today row and dialog
t = code_only(today)
row_at = t.find("NavRow(PilotJournalCopy.ROW")
nav_at = t.find("view.supportingNavigation")
check("A18G-04_row_after_navigation", row_at > nav_at > 0, f"{row_at} {nav_at}")
check("A18G-04_saved_is_said", "PilotJournalCopy.SAVED" in t and "PilotJournalCopy.NOT_SAVED" in t
      and "LiveRegionMode.Polite" in segment(t, "PilotJournalCopy.SAVED", "\n    }"))
m = code_only(main)
check("A18G-04_wired", "onReportPlanProblem = {" in m and "PilotJournalDialog(" in m and "app.recordPilotJournal(kind, note) { journalSaved = it }" in m)
check("A18G-04_notice_cleared_on_leaving", "selected = destination\n        journalSaved = null" in m)
check("A18G-04_failed_save_is_said", "?: return onDone(false)" in rec_app and "main.post { onDone(saved) }" in rec_app)
d = code_only(dialog)
check("A18G-04_radio_rows", "selectableGroup()" in d and "role = Role.RadioButton" in d and "RadioButton(selected = kind == option, onClick = null)" in d)
check("A18G-04_targets", "Modifier.fillMaxWidth().minimumTouchTarget()\n                                .selectable(" in d
      and segment(d, "confirmButton", "dismissButton").count("minimumTouchTarget()") == 1
      and segment(d, "dismissButton", "\n    )").count("minimumTouchTarget()") == 1)
check("A18G-04_no_fault_colour", "isError" not in d and "colorScheme.error" not in d)
check("A18G-04_save_gate_in_core", "PilotJournalPresentation.canSave(kind, typed)" in d)
check("A18G-04_dialog_decides_nothing", not re.search(r"\.trim\(\)\.length\s*<=|200", d.replace("PilotJournal.NOTE_LIMIT", "")))
check("A18G-04_inputs_survive_rotation", d.count("rememberSaveable") >= 2)

# ---------------------------------------------------------------- 5. words
words = " ".join(l for _, l in labels) + " " + " ".join(re.findall(r'const val [A-Z_]+ = "([^"]+)"', copy))
check("A18G-05_reporting_changes_nothing", "planı değiştirmez" in copy and "Plan değiştirilmedi" in copy)
check("A18G-05_preference_not_counted", "sayılmaz" in segment(copy, "PREFERENCE_NOTE", "\n"))
check("A18G-05_no_blame_or_score", not re.search(r"başarısız|puan|seri|streak|hata yaptın|suç", words, re.I), words)

# ---------------------------------------------------------------- 6. TEXT binding
ad = code_only(adapter)
check("A18G-06_text_bound_as_text", 'text = s.getText(2).equals("TEXT", ignoreCase = true)' in ad
      and "if (column in textColumns) statement.bindText(index++, value)" in ad)
check("A18G-06_projection_text_bound_as_text",
      "if (it in textColumns) statement.bindText(index++, value)" in segment(ad, "fun writeProjection", "fun curriculumPublished"))
check("A18G-06_reads_journal", "FROM pilot_journal_entry ORDER BY sequence" in ad)

# ---------------------------------------------------------------- 7. tests
for name, path in TESTS.items():
    check(f"A18G-07_{name}", path.exists() and "@Test" in read(path))
course = read(TESTS["PilotJournalCourseTest"])
check("A18G-07_course_no_replan", "watermark + 1" in course and "store.planVersions()" in course and '"007"' in course)
storage = read(TESTS["PilotJournalStorageTest"])
check("A18G-07_storage_refusals", all(s in storage for s in ['"too_hard"', 'repeat(201)', '" untrimmed"', "plan = 999", "UPDATE pilot_journal_entry", "DELETE FROM pilot_journal_entry", "version = 13"]))
check("A18G-07_narrowed_18a", "Narrowed at 18G" in read(A / "data-persistence/src/test/kotlin/coach/persistence/SetupStorageTest.kt"))

# ---------------------------------------------------------------- 8. the PC report, against the real schema text
LISTS = {m.group(1): re.findall(r'"([^"]+)"', m.group(2)) for m in re.finditer(r"val (\w+Values) = listOf\(([^)]*)\)", schema)}


def expand(sql: str) -> str:
    sql = sql.replace("$SEQUENCE", "sequence INTEGER NOT NULL UNIQUE")

    def time(match: re.Match) -> str:
        args = re.findall(r'"([^"]+)"', match.group(1))
        prefix = args[0] if args else "occurred_at"
        day = args[1] if len(args) > 1 else "occurred_on"
        return f"\n{prefix}_instant INTEGER NOT NULL,\n{day}_study_day TEXT NOT NULL,\nutc_offset_minutes INTEGER NOT NULL"

    sql = re.sub(r"\$\{time\(([^)]*)\)\}", time, sql)
    sql = re.sub(r"\$\{oneOf\(\"(\w+)\", (\w+)\)\}",
                 lambda m: f"CHECK ({m.group(1)} IN ({', '.join(repr(v) for v in LISTS[m.group(2)])}))", sql)
    return sql


def create(table: str) -> str:
    m = re.search(r"(CREATE TABLE IF NOT EXISTS %s \(.*?\n\s*\))\s*\n" % table, schema, re.S)
    return expand(m.group(1)) if m else ""


# A trace the app itself wrote: the emulator's first plan on 2026-10-28, encoded by `PlanTraceCodec` (planner_trace/3).
TRACE = read(ROOT / "arch/18g_pilot_run/fixture_planner_trace.txt")
PLANNER_FACTS = read(A / "core-model/src/main/kotlin/coach/PlannerFacts.kt") + read(A / "core-model/src/main/kotlin/coach/TodayFacts.kt")


def kotlin_ids(enum: str) -> list[str]:
    m = re.search(r"enum class %s\b.*?\{(.*?)\n\}" % enum, PLANNER_FACTS, re.S)
    return re.findall(r'^\s+[A-Z_0-9]+\("([a-z_0-9]+)"', m.group(1), re.M) if m else []


INSERT = {
    "plan_version": "INSERT INTO plan_version (id, sequence, policy_version, occurred_at_instant, occurred_on_study_day, utc_offset_minutes) VALUES (?,?,?,?,?,?)",
    "trace": "INSERT INTO planner_decision_trace (sequence, plan_version_id, trace, occurred_at_instant, occurred_on_study_day, utc_offset_minutes) VALUES (?,?,?,?,?,?)",
    "planned_task": "INSERT INTO planned_task (id, sequence, plan_version_id, skill_logical_id, skill_version, position, occurred_at_instant, occurred_on_study_day, utc_offset_minutes) VALUES (?,?,?,?,?,?,?,?,?)",
    "attempt": "INSERT INTO attempt (sequence, resource_logical_id, resource_version, occurred_at_instant, occurred_on_study_day, utc_offset_minutes, planned_task_id) VALUES (?,?,?,?,?,?,?)",
    "journal": "INSERT INTO pilot_journal_entry (sequence, entry_kind, plan_version_id, note, recorded_at_instant, recorded_on_study_day, utc_offset_minutes) VALUES (?,?,?,?,?,?,?)",
}


def build(path: Path, version: int, journal: bool, planned_link: bool, rows: bool) -> list[bool]:
    """A backup made from the real `Schema.kt` text, as a store of [version] would hold it."""
    db = sqlite3.connect(path)
    try:
        return _fill(db, version, journal, planned_link, rows)
    finally:
        db.close()


def _fill(db: sqlite3.Connection, version: int, journal: bool, planned_link: bool, rows: bool) -> list[bool]:
    made = []
    tables = ["schema_metadata", "assessment_session", "plan_version", "planned_task", "planner_decision_trace", "attempt"]
    for table in tables + (["pilot_journal_entry"] if journal else []):
        sql = create(table)
        made.append(bool(sql))
        if sql:
            db.execute(sql)
    if planned_link:
        db.execute("ALTER TABLE attempt ADD COLUMN planned_task_id INTEGER REFERENCES planned_task (id)")
    db.execute("INSERT INTO schema_metadata VALUES (?, 'x')", (version,))
    if rows:
        seq = iter(range(1, 1000))
        days = ["2026-10-28", "2026-10-29", "2026-10-30"]
        # The third plan's trace has a known header and one malformed escape: the codec would not decode it.
        bad = TRACE.replace("%", "%G", 1)
        for i, day in enumerate(days, start=1):
            db.execute(INSERT["plan_version"], (i, next(seq), "pdt-v0", 1_000 * i, day, 180))
            db.execute(INSERT["trace"], (next(seq), i, TRACE if i < 3 else bad, 1_000 * i, day, 180))
            db.execute(INSERT["planned_task"], (i, next(seq), i, "skill.programming.state_assignment_model", 1, 0, 1_000 * i, day, 180))
        # Day one: two answers 25 minutes apart; day two: one answer, which has no interval.
        for instant, task, day in ((0, 1, days[0]), (25 * 60_000, 1, days[0]), (0, 2, days[1])):
            db.execute(INSERT["attempt"], (next(seq), "item.x", 1, 10_000_000 + instant, day, 180, task))
        for kind, note, plan, day in [("time_wrong", "40 dk sürdü", 1, days[0]), ("wrong_branch", "", 1, days[0]),
                                      ("personal_preference", "kısa gün", 2, days[1]), ("rule_violation", "ön koşul atlandı", 2, days[1]),
                                      # Saved after the day rolled over: it is about plan 1, so it is day one's correction.
                                      ("prerequisite_impossible", "gece yarısından sonra", 1, days[2])]:
            db.execute(INSERT["journal"], (next(seq), kind, plan, note, 5, day, 180))
    db.commit()
    return made


spec = importlib.util.spec_from_file_location("pilot_report", TOOL)
if spec is not None and TOOL.exists() and TRACE:
    pilot_report = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(pilot_report)
    made, before, after, text, text13, text10, error = [], "", "x", "", "", "", ""
    with tempfile.TemporaryDirectory(ignore_cleanup_errors=True) as tmp:
        try:
            # A "#" and a "%" in the name: the tool's file URI must keep them as part of the path.
            db_path = Path(tmp) / "pilot #1 %20.coachdb"
            made = build(db_path, 14, journal=True, planned_link=True, rows=True)
            before = hashlib.sha256(db_path.read_bytes()).hexdigest()
            text = pilot_report.report(db_path)
            after = hashlib.sha256(db_path.read_bytes()).hexdigest()
            old13, old10 = Path(tmp) / "v13.coachdb", Path(tmp) / "v10.coachdb"
            build(old13, 13, journal=False, planned_link=True, rows=False)
            build(old10, 10, journal=False, planned_link=False, rows=False)
            text13, text10 = pilot_report.report(old13), pilot_report.report(old10)
        except Exception as e:  # a fixture the schema refuses, or a tool that raises, is a failed check, not a crash
            error = f"{type(e).__name__}: {e}"
    check("A18G-08_fixture_and_report_ran", not error, error)
    check("A18G-08_schema_tables_found", bool(made) and all(made), str(made))
    check("A18G-08_read_only", before == after and '"?mode=ro"' in read(TOOL) and ".as_uri()" in read(TOOL))
    check("A18G-08_window", "Plan yapılan çalışma günleri: **3** (2026-10-28 – 2026-10-30)" in text and "izi okunamayan: 1" in text, text[:600])
    check("A18G-08_sc049_day_counted_once", "Yapısal düzeltme bildirilen günler: **1**" in text and "**2 / 3 (%67)**" in text, text)
    check("A18G-08_report_counted_on_its_plans_day", "  - Ön koşul yüzünden imkânsız: 1" in text
          and "| 2026-10-28 | Ön koşul yüzünden imkânsız | 1 | gece yarısından sonra |" in text, text)
    check("A18G-08_preference_not_counted", "Kişisel tercih bildirimleri (sayılmaz): 1" in text)
    check("A18G-08_short_window_said", "pencere henüz 3 gün" in text)
    check("A18G-08_violation_listed", "**1 bildirim.**" in text and "ön koşul atlandı" in text)
    title = "Değişken ve atama: durumu adım adım izlemek"
    check("A18G-08_estimate_beside_interval",
          f"| 2026-10-28 | skill.programming.state_assignment_model | {title} | 14 | 25.0 | 2 |" in text
          and f"| 2026-10-29 | skill.programming.state_assignment_model | {title} | 14 | — | 1 |" in text
          and "arka planda kaldığı süreyi içerebilir" in text and "Cevabı olmayan görevler" in text, text)
    check("A18G-08_passed_over", "| new_learning:skill.linux.terminal_filesystem_navigation@v1 | new_learning | 2 | 2 |" in text, text)
    check("A18G-08_no_report_is_not_proof", "ihlal olmadığının kanıtı değildir" in read(TOOL))
    check("A18G-08_older_backups", "Bu yedekte pilot günlüğü yok" in text13 and "Bu yedekte pilot günlüğü yok" in text10
          and "Bu yedekte cevaplar planlı göreve bağlı değil" in text10 and "bağlı değil" not in text13, text10)
    # The trace is read as strictly as the app's own codec reads it.
    def parse(trace: str):
        try:
            return pilot_report.parse_trace(trace)
        except Exception:  # raising is not refusing: the report would stop instead of naming the plan unread
            return "raised"

    check("A18G-08_trace_strict", isinstance(parse(TRACE), dict)
          and parse(TRACE.replace("planner_trace/3", "planner_trace/9", 1)) is None
          and parse(TRACE + "\nbogus\tx=1") is None
          and parse(TRACE.replace("trigger=new_learning", "trigger=review_due", 1)) is None
          and parse(TRACE.replace("disposition=selected", "disposition=chosen", 1)) is None
          and parse(TRACE.replace("\testimated=14", "", 1)) is None
          and parse(TRACE.replace("%", "%G", 1)) is None
          and parse(TRACE.replace("planner_trace/3", "planner_trace/2", 1)) is None)
    check("A18G-08_closed_lists_are_the_codecs",
          list(pilot_report.TASK_PURPOSES) == kotlin_ids("TaskPurpose") and list(pilot_report.NEED_TRIGGERS) == kotlin_ids("NeedTrigger")
          and list(pilot_report.NEED_DISPOSITIONS) == kotlin_ids("NeedDisposition")
          and list(pilot_report.REPLAN_TRIGGERS) == kotlin_ids("ReplanTrigger"))
    check("A18G-08_stdlib_only", not re.search(r"^\s*(import|from)\s+(?!argparse|sqlite3|sys|collections|pathlib|urllib|__future__)", read(TOOL), re.M))
else:
    check("A18G-08_tool_and_fixture_exist", False)

# ---------------------------------------------------------------- 9. contract and audits
contract = yaml.safe_load(read(CONTRACT)) if CONTRACT.exists() else {}
check("A18G-09_contract", contract.get("model") == "PLTX-v0" and contract.get("decision") == "D-141"
      and contract.get("schema_migration") == {"from": 13, "to": 14, "owner": "18G", "tables": ["pilot_journal_entry"], "truth_altered": False, "decision": "D-141"})
check("A18G-09_order_recorded", "18B" in str(contract.get("user_decisions", {}).get("order", "")))
ver = contract.get("verification", {})
check("A18G-07_t6_not_claimed", ver.get("t6_run") is False)
for name in ["a11y_emulator_report.yaml", "a11y_emulator_report_text_200.yaml"]:
    path = ROOT / "arch/18g_pilot_run" / name
    report = yaml.safe_load(read(path)) if path.exists() else {}
    screens = [s.get("screen") for s in report.get("screens", [])] if isinstance(report.get("screens"), list) else []
    check(f"A18G-09_{name}", report.get("result") == "PASS" and "pilot_journal_dialog" in screens and not report.get("screens_missing"),
          f"{report.get('result')} {report.get('screens_missing')}")
# 18G is done when the pilot starts (user decision): installed on the phone and the first day's plan written there.
pilot = contract.get("pilot_start", {})
check("A18G-09_pilot_started", bool(pilot.get("installed_on")) and bool(re.fullmatch(r"\d{4}-\d{2}-\d{2}", str(pilot.get("first_study_day", ""))))
      and pilot.get("first_plan_written") is True and pilot.get("build") == "ai", str(pilot))
for k in ("mutation_results_kotlin", "mutation_results_validator"):
    m_ = contract.get(k, {})
    check(f"A18G-09_{k}", bool(m_) and m_.get("total") == m_.get("detected") and m_.get("total", 0) > 0)

# ---------------------------------------------------------------- report
failed = [r for r in results if r["result"] == "FAIL"]
OUT.parent.mkdir(parents=True, exist_ok=True)
OUT.write_text(yaml.safe_dump({"model": "PLTX-v0", "stage_step": "18G", "checks": len(results), "passed": len(results) - len(failed),
                               "result": "PASS" if not failed else "FAIL", "results": results}, allow_unicode=True, sort_keys=False), encoding="utf-8")
for r in failed:
    print(f"FAIL {r['id']} {r.get('detail', '')[:300]}")
print(f"{len(results) - len(failed)}/{len(results)} PASS")
sys.exit(1 if failed else 0)
