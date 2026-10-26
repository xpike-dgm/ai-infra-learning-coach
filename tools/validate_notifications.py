"""Independent 16E QA — RMDX-v0, the daily reminder.

The rules are read from the accepted contracts — `UXIA-v0` (`ux/8a_information_architecture/ia.yaml`: Profile owns
`reminders`; §15/§18: a notification never bypasses current-state checks), `V1_SCOPE` §17 and SC-043 (a reminder only
when the state really occurs, never stale), `SRR-v0` (absence is not debt), `THUX-v0` (no streak pressure, no fake
urgency) — and compared with the real Kotlin, the schema, both manifests, the contract, the tests, the spec, the research
and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a reminder that names work the planner did not put
in today's plan; one that is stale (another date, already shown, work already recorded) or more than one a day; a
reminder or a process start that writes a plan on a day the learner did not open; a reminder that is on by default or
asks Android's permission before the learner turns it on; an exact alarm; language that counts days, keeps a series,
names an absence or presses; a tap that opens a task past the entry checks; and a reminder setting kept outside the store
or edited in place.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/16e_notifications/notifications.yaml"
QA_OUT = ROOT / "arch/16e_notifications/qa_report.yaml"
SPEC = ROOT / "docs/NOTIFICATIONS_SPEC.md"
RESEARCH = ROOT / "research/16e_notifications_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
V1_SCOPE = ROOT / "docs/V1_SCOPE.md"
CRITERIA = ROOT / "docs/V1_SUCCESS_CRITERIA.md"

FACTS = ANDROID / "core-model/src/main/kotlin/coach/ReminderFacts.kt"
PORTS = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
COMPOSER = ANDROID / "core-engines/src/main/kotlin/coach/engines/BlueprintComposer.kt"
SCHEMA = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
ADAPTER = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
USE_CASES = ANDROID / "core-application/src/main/kotlin/coach/application/Reminders.kt"
BUILD = ANDROID / "core-application/src/main/kotlin/coach/application/BuildDailyPlan.kt"
PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/ReminderPresentation.kt"
PROFILE_SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/ProfileScreen.kt"
WIRING = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/Reminders.kt"
APPLICATION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
ACTIVITY = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
MANIFEST = ANDROID / "app-wiring/src/main/AndroidManifest.xml"
MANIFEST_AI = ANDROID / "app-wiring/src/withAi/AndroidManifest.xml"
MODEL_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/ReminderFactsTest.kt"
PLAN_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/BuildDailyPlanTest.kt"
STORAGE_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/ReminderStorageTest.kt"
PRESENTATION_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/ReminderPresentationTest.kt"
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


def strip_xml_comments(source: str) -> str:
    return re.sub(r"<!--.*?-->", "", source, flags=re.S)


def body(source: str, signature: str) -> str:
    start = source.find(signature)
    if start < 0:
        return ""
    depth, opened = 0, False
    for i in range(start, len(source)):
        if source[i] == "{":
            depth, opened = depth + 1, True
        elif source[i] == "}":
            depth -= 1
            if opened and depth == 0:
                return source[start:i + 1]
    return source[start:]


def expression(source: str, signature: str) -> str:
    """An expression-bodied function, from its signature to the first blank line."""
    start = source.find(signature)
    if start < 0:
        return ""
    end = source.find("\n\n", start)
    return source[start:end if end > 0 else len(source)]


def strings(source: str) -> list[str]:
    return re.findall(r'"((?:[^"\\]|\\.)*)"', source)


def finish() -> None:
    passed = sum(1 for r in results if r["result"] == "PASS")
    out = {"model": "RMDX-v0", "stage_step": "16E", "decision": "D-127", "result": "PASS" if not failures else "FAIL",
           "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
    if "--no-write" not in sys.argv:
        QA_OUT.parent.mkdir(parents=True, exist_ok=True)
        QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
    print(f"16E QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(0 if not failures else 1)


for path in (CONTRACT, SPEC, RESEARCH, IA, V1_SCOPE, CRITERIA, FACTS, PORTS, COMPOSER, SCHEMA, MIGRATIONS, ADAPTER, USE_CASES, BUILD,
             PRESENTATION, PROFILE_SCREEN, WIRING, APPLICATION, ACTIVITY, MANIFEST, MANIFEST_AI, MODEL_TEST, PLAN_TEST,
             STORAGE_TEST, PRESENTATION_TEST, COURSE_TEST):
    check(f"A16E-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
ia = load(IA) or {}
facts = strip_comments(read(FACTS))
schema = strip_comments(read(SCHEMA))
use_cases = strip_comments(read(USE_CASES))
build = strip_comments(read(BUILD))
presentation = strip_comments(read(PRESENTATION))
wiring = strip_comments(read(WIRING))
application = strip_comments(read(APPLICATION))
activity = strip_comments(read(ACTIVITY))
manifests = [strip_xml_comments(read(MANIFEST)), strip_xml_comments(read(MANIFEST_AI))]
reminder = contract.get("reminder") or {}

# ---------------------------------------------------------------- contract and its sources
check("A16E-01_model", contract.get("model") == "RMDX-v0" and contract.get("stage_step") == "16E" and contract.get("decision") == "D-127", "")
ud = contract.get("user_decisions") or {}
check("A16E-01_user_decisions", ud == {
    "approval": "explicit_user_approval_2026-10-25",
    "scope": "one_daily_reminder_at_the_learner_s_time_at_most_one_a_day_content_built_from_current_state_silence_when_nothing_real_to_say",
    "default": "off_until_turned_on_in_profile_notification_permission_asked_only_then",
    "timing": "approximate_inexact_window_no_exact_alarm_permission",
}, str(ud))
scope = contract.get("scope") or {}
check("A16E-01_scope", all(scope.get(k) is False for k in ("boundaries_changed", "planner_rule_changed", "capacity_rule_changed",
      "mastery_rule_changed", "reminder_writes_truth", "app_plans_on_process_start", "exact_alarm_permission",
      "end_of_day_notification", "absence_notification"))
      and scope.get("schema_version") == 10 and scope.get("migration_tested_against_populated_previous_version") is True
      and scope.get("ports_changed") == "refinements_only", "")
profile = next((d for d in ia.get("destinations", []) if isinstance(d, dict) and d.get("id") == "profile"), {}) or {}
check("A16E-01_profile_owns_reminders", "reminders" in (profile.get("primary_information") or [])
      and any(isinstance(o, dict) and o.get("object") == "reminders" and o.get("primary_home") == "profile" for o in ia.get("object_homes", []) or [])
      or "- object: reminders\n    primary_home: profile" in read(IA), "UXIA-v0")
check("A16E-01_v1_scope_and_sc043", "Daily reminder" in read(V1_SCOPE) and "SC-043" in read(CRITERIA)
      and "stale bildirim üretmemelidir" in read(CRITERIA), "")

# ---------------------------------------------------------------- 1. the setting (off by default, newest row wins)
check("A16E-02_off_by_default", "val OFF = ReminderPreference(enabled = false, minuteOfDay = SUGGESTED_MINUTE)" in facts
      and "?: OFF" in expression(facts, "fun of(records: List<ReminderPreferenceRecord>)") and reminder.get("default_enabled") is False, "")
check("A16E-02_suggestion_is_only_a_suggestion", "const val SUGGESTED_MINUTE = 19 * 60" in facts and reminder.get("suggested_minute") == 1140, "")
check("A16E-02_minutes_within_a_day", "const val MINUTES_PER_DAY = 1440" in facts and "val MINUTE_RANGE = 0 until MINUTES_PER_DAY" in facts
      and reminder.get("minute_range") == [0, 1439], "")
of = expression(facts, "fun of(records: List<ReminderPreferenceRecord>)")
check("A16E-02_newest_readable_wins", "records.sortedBy { it.sequence }.lastOrNull { it.minuteOfDay in MINUTE_RANGE }" in of, "")
check("A16E-02_time_read_strictly", 'Regex("""^([01]?\\d|2[0-3]):([0-5]\\d)$""")' in read(FACTS), "")
nxt = body(facts, "fun nextTrigger(")
check("A16E-03_never_the_moment_itself", "if (candidate.isAfter(now)) candidate else" in nxt and "today.plusDays(1)" in nxt, "")
check("A16E-03_its_own_day", "fun sameDay(" in facts and "dateOf(scheduled, zone) == dateOf(now, zone)" in facts, "")

# ---------------------------------------------------------------- 2. storage (LFPS-v0 §11, DDM-v0)
# Narrowed at 16F (`E2EX-v0` / `D-128`): this step's schema was version 10. A later version is accepted only when every
# version after 10 is owned by an accepted later contract's `schema_migration` (16F: 10 -> 11, `attempt.planned_task_id`).
def _schema_10_or_owned_later(text: str) -> bool:
    found = re.search(r"const val VERSION = (\d+)", text)
    if not found:
        return False
    version = int(found.group(1))
    owned = set()
    for path in sorted((ROOT / "arch").glob("*/*.yaml")):
        data = yaml.safe_load(path.read_text(encoding="utf-8")) or {}
        migration = data.get("schema_migration") if isinstance(data, dict) else None
        if isinstance(migration, dict) and isinstance(migration.get("to"), int):
            owned.add(migration["to"])
    return version >= 10 and all(v in owned for v in range(11, version + 1))


check("A16E-04_schema_v10", _schema_10_or_owned_later(schema) and "9 to Schema.v10," in strip_comments(read(MIGRATIONS)), "")
check("A16E-04_append_only_and_guarded", 'val reminderPreferenceTables = listOf("reminder_preference")' in schema
      and "val appendOnlyTables = truthTables + userPreferenceTables + reminderPreferenceTables" in schema
      and ") + reminderPreferenceTables.flatMap(::appendOnlyGuards)" in schema
      and "require(record.kind in Schema.appendOnlyTables)" in read(ADAPTER), "")
v10 = schema[schema.find("val v10: List<String>"):schema.find("val v10: List<String>") + 1200]
check("A16E-04_schema_refuses", "CHECK (reminder_enabled IN (0, 1))" in v10 and "CHECK (minute_of_day BETWEEN 0 AND 1439)" in v10
      and "reminder_enabled       INTEGER NOT NULL" in v10 and "minute_of_day          INTEGER NOT NULL" in v10, "")
check("A16E-04_no_credential_like_column", not re.search(r"\b\w*(?:key|token|secret|password)\w*\s+(?:TEXT|INTEGER)", v10, re.I), "10E's credential guard")
check("A16E-04_preference_table_not_rebuilt", not re.search(r"(DROP|ALTER)\s+TABLE\s+preference_record", schema, re.I)
      and re.findall(r'"([^"]+)"', schema[schema.find("val preferenceKeys"):schema.find(")", schema.find("val preferenceKeys"))]) ==
      ["capacity.short_minutes", "capacity.normal_minutes", "capacity.intensive_minutes", "capacity.today"], "16D's rows are not rewritten")
migration = contract.get("schema_migration") or {}
check("A16E-04_migration_owned", migration == {"from": 9, "to": 10, "owner": "16E", "tables": ["reminder_preference"], "truth_altered": False,
      "decision": "D-127"}, str(migration))
ports = read(PORTS)
check("A16E-04_port_defaulted", "fun reminderPreferences(): List<ReminderPreferenceRecord> = emptyList()" in ports
      and sorted(re.findall(r"^interface (\w+)", ports, re.M)) == sorted(["ClockPort", "PersistencePort", "ContentPort", "EvaluatorPort", "TutorPort"]), "")
check("A16E-04_adapter_reads_in_order", "FROM reminder_preference ORDER BY sequence" in read(ADAPTER), "")
settings_cls = body(use_cases, "class ReminderSettings(")
check("A16E-04_written_by_append_only", "persistence.appendTruth(" in settings_cls and "persistence.inTransaction {" in settings_cls
      and not re.search(r"UPDATE|DELETE|writeProjection", settings_cls), "")

# ---------------------------------------------------------------- 3. when a reminder is shown (SC-043)
decide = body(use_cases, "fun decide(")
# Narrowed at 16F (`E2EX-v0` / `D-128`): attempts now name their planned task, so "every task started" is read after the
# plan (the plan says which tasks there are); the five silences before the plan keep their order.
order = ["ReminderSilence.DISABLED", "ReminderSilence.NOTIFICATIONS_BLOCKED", "ReminderSilence.DELIVERED_ON_ANOTHER_DAY",
         "ReminderSilence.ALREADY_SHOWN_TODAY", "ReminderSilence.NO_CURRICULUM", "ReminderSilence.PLAN_UNREADABLE",
         "ReminderSilence.NOTHING_PLANNED", "ReminderSilence.DAY_UNREADABLE", "ReminderSilence.WORK_RECORDED_TODAY"]
positions = [decide.find(s) for s in order]
check("A16E-05_silence_order", all(p >= 0 for p in positions) and positions == sorted(positions), str(positions))
check("A16E-05_off_first", "if (!ReminderSettings(persistence, clock).read().enabled) return silent(ReminderSilence.DISABLED)" in decide, "")
check("A16E-05_its_own_date_and_once_a_day", "if (scheduledStudyDay != today) return silent(ReminderSilence.DELIVERED_ON_ANOTHER_DAY)" in decide
      and "if (lastShownStudyDay == today) return silent(ReminderSilence.ALREADY_SHOWN_TODAY)" in decide
      and reminder.get("per_study_day_at_most") == 1, "")
# Narrowed at 16F (`E2EX-v0` / `D-128`): "work recorded today" became "every task in today's plan started" — the 16E open
# loop — read from the attempts' planned tasks; a read that fails is still silence, never "none started".
check("A16E-05_work_today_silences", "StartedTasks.today(persistence, clock)" in decide and "?: return silent(ReminderSilence.DAY_UNREADABLE)" in decide
      and "contentOf(trace, started)" in decide and "fresh.all { it.position in started }" in decide
      and "ReminderSilence.WORK_RECORDED_TODAY" in decide, "")
enum_ids = re.findall(r'^\s+[A-Z_]+\("([a-z_]+)"\),', body(facts, "enum class ReminderSilence"), re.M)
check("A16E-05_silences_closed", sorted(enum_ids) == sorted(reminder.get("silences") or []) and len(set(enum_ids)) == len(enum_ids) == 10, str(enum_ids))

# ---------------------------------------------------------------- 4. a reminder writes nothing
check("A16E-06_decision_writes_no_truth", not re.search(r"appendTruth|inTransaction|writeProjection|\.build\(|PlanToday\(|\.replan\(",
      body(use_cases, "class ReminderCheck(")), "")
todays = body(use_cases, "private fun todaysPlan(")
check("A16E-06_stored_plan_then_preview", "stored.recordedAt.studyDay == today" in todays and "PlanReading.read(stored, today)" in todays
      and ".preview(capacity)" in todays and todays.find("PlanReading.read") < todays.find(".preview("), "")
build_fn = body(build, "fun build(")
check("A16E-06_preview_stops_before_the_write", 0 <= build_fn.find("if (!writes) return Built.Previewed(trace)") < build_fn.find("persistence.inTransaction {"), "")
check("A16E-06_builders_write_by_default", "private val writes: Boolean = true," in build
      and "BuildDailyPlan(persistence, content, clock, evaluatorAvailable, writes = false).build(capacity)" in body(build, "fun preview(")
      and len(re.findall(r"writes = false", strip_comments(read(BUILD)))) == 1, "only the preview turns writing off")
open_app = body(application, "private fun openApp()")
check("A16E-06_store_open_plans_nothing", "planToday(" not in open_app and "Reminders.applySetting(" in open_app and "ingestAll()" in open_app, "")
check("A16E-06_learner_open_plans", "app.refreshToday" in activity[activity.find("private val listener"):activity.find("private val app:")]
      and 0 <= body(application, "fun refreshToday(").find("planToday(opened.graph)"), "")
check("A16E-06_receivers_never_plan", not re.search(r"PlanToday\(|BuildDailyPlan\(|refreshToday|appendTruth|inTransaction", wiring), "")

# ---------------------------------------------------------------- 5. what it says
# Narrowed at 16F (`E2EX-v0` / `D-128`): the content also leaves out the tasks started today.
content_of = body(use_cases, "fun contentOf(trace: PlanTrace")
check("A16E-07_only_today_s_new_tasks", "trace.selected.filterNot { it.preserved || it.position in started }" in content_of and "if (fresh.isEmpty()) return null" in content_of
      and "fresh.sumOf { it.plannedMinutes }" in content_of, "")
check("A16E-07_review_and_cycles_from_the_plan", "it.purpose == TaskPurpose.RETAIN" in content_of and "BlueprintComposer.scopeOfCandidate(it.candidateId)" in content_of, "")
composer = strip_comments(read(COMPOSER))
check("A16E-07_scope_inverse_of_candidate_id", '"weekly:$cycleId:$slotId"' in composer and '"monthly:$cycleId:$slotId"' in composer
      and 'candidateId.startsWith("weekly:") -> AssessmentScope.WEEKLY_BLUEPRINT' in composer
      and 'candidateId.startsWith("monthly:") -> AssessmentScope.MONTHLY_CAPABILITY' in composer, "")
copy = strings(body(presentation, "object ReminderCopy")) + strings(body(presentation, "fun notification("))
words = (r"\bseri", r"streak", r"kaçır", r"kaybet", r"borç", r"gündür", r"özledik", r"hâlâ", r"son şans", r"\bhemen\b", r"acele",
         r"%", r"\bhedef", r"\bpuan", r"başarısız", r"unut")
notification_copy = strings(body(presentation, "fun notification(")) + [s for s in strings(body(presentation, "object ReminderCopy"))
                                                                          if s in ("Bugünün planı hazır",) or s.startswith("Günlük") or s.startswith("Seçtiğin")]
bad = [s for s in notification_copy for w in words if re.search(w, s, re.I)]
check("A16E-08_notification_never_presses", bad == [], str(bad[:3]))
check("A16E-08_title_fixed", 'const val TITLE = "Bugünün planı hazır"' in presentation and "ReminderNotificationText(ReminderCopy.TITLE, body)" in presentation, "")
check("A16E-08_note_says_no_obligation", "yükümlülük değildir" in presentation and "gün saymaz, seri tutmaz, ara vermeyi borç saymaz" in presentation
      and "Günde en çok bir kez" in presentation, "")
screen = strip_comments(read(PROFILE_SCREEN))
check("A16E-08_screen_decides_nothing", "coach.application" not in screen and not re.search(r"Slider|ProgressIndicator|\d+\s*%|Calendar|streak", screen)
      and "ReminderPresentation.parseTime(typed)" in screen, "")

# ---------------------------------------------------------------- 6. delivery on Android
check("A16E-09_inexact_ten_minutes", "alarms.setWindow(AlarmManager.RTC_WAKEUP," in wiring and "private const val WINDOW_MILLIS = 10 * 60 * 1000L" in wiring
      and reminder.get("window_minutes") == 10 and reminder.get("alarm_type") == "RTC_WAKEUP", "")
exact = [p for p in (WIRING, APPLICATION, ACTIVITY, MANIFEST, MANIFEST_AI)
         if re.search(r"setExact|setAlarmClock|SCHEDULE_EXACT_ALARM|USE_EXACT_ALARM", strip_xml_comments(strip_comments(read(p))))]
check("A16E-09_no_exact_alarm", exact == [], str(exact))
check("A16E-09_immutable_intents", wiring.count("PendingIntent.FLAG_IMMUTABLE") >= 2 and "FLAG_MUTABLE" not in wiring, "")
rearm = re.search(r'<receiver\s+android:name="\.ReminderRearmReceiver"(.*?)</receiver>', manifests[0], re.S)
rearm_ai = re.search(r'<receiver\s+android:name="\.ReminderRearmReceiver"(.*?)</receiver>', manifests[1], re.S)
actions = ["android.intent.action.BOOT_COMPLETED", "android.intent.action.MY_PACKAGE_REPLACED", "android.intent.action.TIME_SET",
           "android.intent.action.TIMEZONE_CHANGED"]
check("A16E-09_rearm_receiver_off_until_on", all(r is not None and 'android:enabled="false"' in r.group(1)
      and re.findall(r'<action android:name="([^"]+)"', r.group(1)) == actions for r in (rearm, rearm_ai))
      and "COMPONENT_ENABLED_STATE_DISABLED" in wiring and reminder.get("rearm_receiver_enabled_only_while_on") is True, "")
check("A16E-09_alarm_receiver_private", all(re.search(r'<receiver\s+android:name="\.ReminderAlarmReceiver"\s+android:exported="false"\s*/>', m) for m in manifests), "")
check("A16E-09_permissions_both_builds", all('<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />' in m
      and '<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />' in m for m in manifests)
      and "android.permission.INTERNET" not in manifests[0], "the build without AI still has no network permission")
check("A16E-09_budget_inside_ten_seconds", "const val DECISION_BUDGET_MILLIS = 8_000L" in wiring and reminder.get("receiver_budget_millis") == 8000 < 10000, "")
on_alarm = body(wiring, "fun onAlarm(")
check("A16E-09_posts_only_what_core_decided", on_alarm.count("post(context, decision)") == 1 and wiring.count("post(context,") == 1
      and on_alarm.find("is ReminderDecision.Show ->") < on_alarm.find("post(context, decision)") < on_alarm.find("is ReminderDecision.Silent ->")
      and "if (!finished.get())" in on_alarm and "access(context) == NotificationAccess.ALLOWED" in on_alarm, "")
check("A16E-09_one_notification_one_channel", wiring.count("manager.notify(") == 1 and len(re.findall(r"const val CHANNEL_ID", wiring)) == 1
      and "createNotificationChannel(" in wiring and "NotificationManager.IMPORTANCE_DEFAULT" in wiring, "")
check("A16E-09_checks_android_before_posting", "manager.areNotificationsEnabled()" in body(wiring, "fun access(")
      and "Manifest.permission.POST_NOTIFICATIONS" in body(wiring, "fun access("), "")
check("A16E-09_the_app_s_one_clock", "Instant.now()" not in wiring and "System.currentTimeMillis()" not in wiring
      and "opened.graph.clock.now().instantEpochMillis" in wiring, "")
on_turn_on = activity[activity.find("onReminderOn = {"):activity.find("onReminderOff = {")]
check("A16E-10_permission_asked_only_on_turn_on", activity.count("askNotificationPermission.launch(") == 1
      and "askNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)" in on_turn_on
      and "Build.VERSION_CODES.TIRAMISU" in on_turn_on, "")
post = body(wiring, "private fun post(")
check("A16E-11_tap_opens_the_app_as_its_icon_does", "Intent(context, MainActivity::class.java)" in post and ".setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)" in post
      and "putExtra" not in post and reminder.get("tap_opens") == "app_launch_today_start_destination", "the notification carries no task")

# ---------------------------------------------------------------- tests name what they guard
for path, names in ((MODEL_TEST, ["with no stored row the reminder is off and the time is only the suggestion",
                                  "the next reminder is today's time while it is still ahead, otherwise tomorrow's, never the moment itself",
                                  "a time a DST change skips moves forward by the gap, and a time that occurs twice is the first"]),
                    (PLAN_TEST, ["a preview is the very plan build would write, and it writes nothing"]),
                    (STORAGE_TEST, ["schema v10 adds the reminder to a populated v9 database and leaves every truth row as it was",
                                    "the schema refuses a minute outside the day and an on or off that is neither",
                                    "a backup carries the reminder setting with the profile"]),
                    (PRESENTATION_TEST, ["no reminder text can count days, keep a series, name an absence or press",
                                         "Profile says off, on, or that Android blocks it, and offers Android's settings only then"]),
                    (COURSE_TEST, ["with all eight packages, a new learner's reminder names the plan the planner would write, and writes nothing",
                                   "the reminder is silence when off, blocked by Android, late, already shown today, or when work is recorded today",
                                   "after days away the reminder names today's fresh plan, never yesterday's, and still writes nothing",
                                   "a day or a plan that cannot be read is silence, never a zero or a guess",
                                   "a task kept because it was started is not new, and a review or an assessment is named only when the plan holds one"])):
    text = read(path)
    for name in names:
        check(f"A16E-12_test_{re.sub(r'[^a-z0-9]+', '_', name[:40].lower()).strip('_')}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- narrowed gates
gates = (contract.get("narrowed_gates") or {}).get("gates") or []
check("A16E-13_narrowed_gates_marked", len(gates) >= 2 and all(
    read(ROOT / g.get("file", "")).count("Narrowed at 16E (`RMDX-v0` / `D-127`)") >= max(1, len(g.get("checks") or [])) for g in gates),
    "one marker per narrowed check: " + str([g.get("file") for g in gates]))

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results_kotlin") if isinstance(contract.get("mutation_results_kotlin"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("A16E-14_kotlin_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("A16E-14_kotlin_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("A16E-14_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("A16E-14_runs_pass", len(runs) >= 6 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") or {}
check("A16E-14_device_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("notification_seen_on_device") is False, "")

# ---------------------------------------------------------------- spec, research, decision
spec = read(SPEC)
check("A16E-15_spec_status", "**Status:** ACCEPTED — independent 16E QA PASS" in spec and "`D-127`" in spec
      and not re.search(r"__[A-Z][A-Z_]*__", spec), "no result placeholder is left")
check("A16E-15_spec_next", "**16F — Uçtan uca günlük döngü**" in spec and "**Not run: T6.**" in spec, "")
research = read(RESEARCH)
check("A16E-15_research", all(r in research for r in ("UXIA-v0", "SC-043", "SRR-v0", "THUX-v0", "EODX-v0",
      "https://developer.android.com/develop/background-work/services/alarms",
      "https://developer.android.com/reference/android/app/AlarmManager",
      "https://developer.android.com/reference/android/content/BroadcastReceiver",
      "https://developer.android.com/develop/ui/views/notifications/notification-permission")), "")
check("A16E-15_decision", re.search(r"^## D-127 .*RMDX-v0", read(DECISIONS), re.M) is not None, "")

finish()
