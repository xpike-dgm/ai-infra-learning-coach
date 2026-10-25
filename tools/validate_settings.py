"""Independent 16D QA — STGX-v0 settings (capacity and backup) and today's plan made by the app.

The rules are read from the accepted contracts — D-033 / `docs/ADAPTIVE_PLANNER_SPEC.md` (the resolution order and
§3's v0 presets), `LFPS-v0` (`arch/9b_local_first_persistence/persistence.yaml`: export and restore), `UXIA-v0` and
`THUX-v0` (Profile's contents, Today's capacity action) — and compared with the real Kotlin, the schema, the contract,
the tests, the spec, the research and DECISIONS.

What this step exists to prevent must be unrepresentable as a PASS: a preference kept outside the store (and so lost
by a restore) or edited in place; a time budget shown or used as a target or a measure; today's choice carried into
another day; a replan for a change that left the budget as it was; a plan the app composes itself instead of asking
the planner; a restore that replaces without confirmation, merges, or loses the profile it replaced; a refusal that
claims to have changed something; and the end-to-end loop silently claimed instead of moved to 16F.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
CONTRACT = ROOT / "arch/16d_settings/settings.yaml"
QA_OUT = ROOT / "arch/16d_settings/qa_report.yaml"
SPEC = ROOT / "docs/SETTINGS_SPEC.md"
RESEARCH = ROOT / "research/16d_settings_research.md"
DECISIONS = ROOT / "docs/DECISIONS.md"
PLANNER_SPEC = ROOT / "docs/ADAPTIVE_PLANNER_SPEC.md"
LFPS = ROOT / "arch/9b_local_first_persistence/persistence.yaml"
IA = ROOT / "ux/8a_information_architecture/ia.yaml"
HOME = ROOT / "ux/8b_today_home/home.yaml"
EXECUTION_INDEX = ROOT / "docs/EXECUTION_INDEX.md"
MASTER_PLAN = ROOT / "docs/MASTER_PLAN.md"

FACTS = ANDROID / "core-model/src/main/kotlin/coach/PreferenceFacts.kt"
PLANNER_ENGINE = ANDROID / "core-engines/src/main/kotlin/coach/engines/PlannerEngine.kt"
PORTS = ANDROID / "core-ports/src/main/kotlin/coach/ports/Ports.kt"
SCHEMA = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Schema.kt"
MIGRATIONS = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/Migrations.kt"
ADAPTER = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/SqlitePersistence.kt"
REPLACEMENT = ANDROID / "data-persistence/src/main/kotlin/coach/persistence/ProfileReplacement.kt"
SETTINGS = ANDROID / "core-application/src/main/kotlin/coach/application/Settings.kt"
STARTUP = ANDROID / "core-application/src/main/kotlin/coach/application/StoreStartup.kt"
PRESENTATION = ANDROID / "core-presentation/src/main/kotlin/coach/presentation/SettingsPresentation.kt"
PROFILE_SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/ProfileScreen.kt"
TODAY_SCREEN = ANDROID / "app-ui/src/main/kotlin/coach/ui/TodayScreen.kt"
APPLICATION = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/CoachApplication.kt"
ACTIVITY = ANDROID / "app-wiring/src/main/kotlin/coach/wiring/MainActivity.kt"
MODEL_TEST = ANDROID / "core-model/src/test/kotlin/coach/model/CapacityPreferencesTest.kt"
STARTUP_TEST = ANDROID / "core-application/src/test/kotlin/coach/application/StoreStartupTest.kt"
STORAGE_TEST = ANDROID / "data-persistence/src/test/kotlin/coach/persistence/PreferenceStorageTest.kt"
PRESENTATION_TEST = ANDROID / "core-presentation/src/test/kotlin/coach/presentation/SettingsPresentationTest.kt"
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
    out = {"model": "STGX-v0", "stage_step": "16D", "decision": "D-126", "result": "PASS" if not failures else "FAIL",
           "checks_total": len(results), "checks_passed": passed, "checks_failed": len(failures), "checks": results}
    if "--no-write" not in sys.argv:
        QA_OUT.parent.mkdir(parents=True, exist_ok=True)
        QA_OUT.write_text(yaml.safe_dump(out, sort_keys=False, allow_unicode=True), encoding="utf-8", newline="\n")
    print(f"16D QA: {passed}/{len(results)} {'PASS' if not failures else 'FAIL'}")
    for failure in failures:
        print("  FAIL", failure)
    sys.exit(0 if not failures else 1)


for path in (CONTRACT, SPEC, RESEARCH, PLANNER_SPEC, LFPS, IA, HOME, FACTS, PLANNER_ENGINE, PORTS, SCHEMA, MIGRATIONS, ADAPTER,
             REPLACEMENT, SETTINGS, STARTUP, PRESENTATION, PROFILE_SCREEN, TODAY_SCREEN, APPLICATION, ACTIVITY, MODEL_TEST,
             STARTUP_TEST, STORAGE_TEST, PRESENTATION_TEST, COURSE_TEST):
    check(f"A16D-00_exists_{path.name}", path.is_file(), f"missing {path.relative_to(ROOT)}")

contract = load(CONTRACT) or {}
lfps = load(LFPS) or {}
ia = load(IA) or {}
home = load(HOME) or {}
facts = strip_comments(read(FACTS))
schema = strip_comments(read(SCHEMA))
settings = strip_comments(read(SETTINGS))
startup = strip_comments(read(STARTUP))
replacement = strip_comments(read(REPLACEMENT))
presentation = strip_comments(read(PRESENTATION))
application = strip_comments(read(APPLICATION))
activity = strip_comments(read(ACTIVITY))

# ---------------------------------------------------------------- contract
check("A16D-01_model", contract.get("model") == "STGX-v0" and contract.get("stage_step") == "16D" and contract.get("decision") == "D-126", "")
ud = contract.get("user_decisions") or {}
check("A16D-01_user_decisions", ud.get("approval") == "explicit_user_approval_2026-10-25"
      and ud.get("new_step") == "16F_uctan_uca_gunluk_dongu_added_like_d_099_no_renumbering"
      and ud.get("backup_controls") == "in_16d_restore_confirmed_explicitly_replaced_profile_kept_aside"
      and ud.get("cycles") == "fixed_iso_week_from_monday_and_calendar_month_no_setting" and ud.get("focus_preference") == "18B", str(ud))
scope = contract.get("scope") or {}
check("A16D-01_scope", all(scope.get(k) is False for k in ("boundaries_changed", "planner_rule_changed", "capacity_rule_changed",
      "mastery_rule_changed", "assessment_rule_changed", "cycles_changed", "focus_preference", "app_runs_tasks"))
      and scope.get("schema_version") == 9 and scope.get("migration_tested_against_populated_previous_version") is True
      and scope.get("app_plans_today") is True and scope.get("ports_changed") == "refinements_only", "")

# ---------------------------------------------------------------- 1. capacity (D-033, ADAPTIVE_PLANNER_SPEC §3)
spec_presets = {k: int(v) for k, v in re.findall(r"^(short|normal|intensive)_profile_minutes_v0 = (\d+)$", read(PLANNER_SPEC), re.M)}
code_presets = {k.lower(): int(v) for k, v in re.findall(r"const val (SHORT|NORMAL|INTENSIVE)_PRESET_V0 = (\d+)", facts)}
check("A16D-02_presets_are_the_accepted_v0", spec_presets == code_presets == (contract.get("capacity") or {}).get("presets_v0")
      == {"short": 30, "normal": 60, "intensive": 90}, f"{spec_presets} {code_presets}")
check("A16D-02_minutes_within_a_day", "const val MAX_MINUTES = 1440" in facts and "val MINUTES_RANGE = 0..MAX_MINUTES" in facts
      and (contract.get("capacity") or {}).get("minutes_range") == [0, 1440], "")
resolve = body(strip_comments(read(PLANNER_ENGINE)), "fun resolveCapacity(input: DailyCapacityInput)")
order = [resolve.find(s) for s in ("override != null ->", "CapacityProfile.SHORT ->", "scheduled != null ->", "else -> CapacitySource.NORMAL_PROFILE")]
check("A16D-02_planner_resolution_unchanged", all(i >= 0 for i in order) and order == sorted(order), str(order))
inp = expression(facts, "    fun input(): DailyCapacityInput")
check("A16D-02_input_is_d033", "selectedProfile = (today as? TodayCapacityChoice.Profile)?.profile," in inp
      and "todayOverrideMinutes = (today as? TodayCapacityChoice.Minutes)?.minutes," in inp and "scheduledDefaultMinutes" not in inp, "")
of = body(facts, "fun of(records: List<PreferenceRecord>, studyDay: String)")
check("A16D-03_newest_readable_wins", "records.sortedBy { it.sequence }" in of and "ordered.lastOrNull { it.key == key && it.appliesToStudyDay == null && minutesOf(it.value) != null }" in of, "")
check("A16D-03_today_is_today_only", "it.appliesToStudyDay == studyDay && decodeToday(it.value) != null" in of
      and "?: TodayCapacityChoice.None" in of and (contract.get("capacity") or {}).get("today_choice_carries_over") is False, "")
check("A16D-03_presets_where_nothing_stored", all(f"?: {p}_PRESET_V0" in of for p in ("SHORT", "NORMAL", "INTENSIVE")), "")
minutes_of = expression(facts, "        fun minutesOf(value: String)")
check("A16D-03_strict_reading", "it.all(Char::isDigit)" in minutes_of and "takeIf { it in MINUTES_RANGE }" in minutes_of, "")

# ---------------------------------------------------------------- 2. storage (LFPS-v0 §11, DDM-v0)
check("A16D-04_preferences_exported", "preferences" in ((lfps.get("backup_export_restore") or {}).get("export_includes") or []), "LFPS-v0 exports preferences")
# Narrowed at 16E (`RMDX-v0` / `D-127`): this step's schema was version 9. A later version is accepted only when every
# version after 9 is owned by an accepted later contract's `schema_migration` (16E: 9 -> 10, `reminder_preference`), so an
# unowned move still fails.
def _schema_9_or_owned_later(text: str) -> bool:
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
    return version >= 9 and all(v in owned for v in range(10, version + 1))


check("A16D-04_schema_v9", _schema_9_or_owned_later(schema) and "8 to Schema.v9," in strip_comments(read(MIGRATIONS)), "")
check("A16D-04_append_only_and_guarded", "val userPreferenceTables = listOf(\"preference_record\")" in schema
      and "val appendOnlyTables = truthTables + userPreferenceTables" in schema and ") + userPreferenceTables.flatMap(::appendOnlyGuards)" in schema
      and "truth + truthGuards()" in schema[schema.find("val v1: List<String>"):schema.find("val v1: List<String>") + 200]
      and "require(record.kind in Schema.appendOnlyTables)" in read(ADAPTER), "DDM-v0's thirteen stay the truth list; preferences are guarded alike")
migration = contract.get("schema_migration") or {}
check("A16D-04_migration_owned", migration.get("from") == 8 and migration.get("to") == 9 and migration.get("owner") == "16D"
      and migration.get("tables") == ["preference_record"] and migration.get("truth_altered") is False, str(migration))
key_block = schema[schema.find("val preferenceKeys"):]
keys = re.findall(r'"([^"]+)"', key_block[:key_block.find(")")])
check("A16D-04_keys_closed", keys == (contract.get("preference_storage") or {}).get("keys") and len(keys) == 4, str(keys))
v9 = schema[schema.find("val v9: List<String>"):schema.find("val v9: List<String>") + 1600]
check("A16D-04_day_rule_in_schema", "CHECK ((preference_name = 'capacity.today') = (applies_to_day IS NOT NULL))" in v9
      and "CHECK (length(trim(preference_value)) > 0)" in v9, "")
check("A16D-04_no_credential_like_column", not re.search(r"\b\w*(?:key|token|secret|password)\w*\s+TEXT", v9, re.I), "10E's credential guard")
check("A16D-04_port_defaulted", "fun preferenceRecords(): List<PreferenceRecord> = emptyList()" in read(PORTS)
      and sorted(re.findall(r"^interface (\w+)", read(PORTS), re.M)) == sorted(["ClockPort", "PersistencePort", "ContentPort", "EvaluatorPort", "TutorPort"]), "")
check("A16D-04_adapter_reads_in_order", "FROM preference_record ORDER BY sequence" in read(ADAPTER), "")
check("A16D-04_written_by_append_only", "persistence.appendTruth(" in settings and not re.search(r"UPDATE|DELETE|writeProjection", settings), "")

# ---------------------------------------------------------------- 3. today's plan (D-033 §16, 12C/12D)
ensure = body(settings, "fun ensure(): BuildDailyPlan.Built")
after = body(settings, "fun afterCapacityChange(): BuildDailyPlan.Built")
check("A16D-05_plan_asked_of_the_planner", "compose()" in ensure and "builder().build(CapacitySettings(persistence, clock).read().input())" in ensure, "")
check("A16D-05_replan_only_on_a_new_budget", "if (built !is BuildDailyPlan.Built.AlreadyPlanned) return built" in after
      and "if (trace.capacity.hardBudgetMinutes == PlannerEngine.resolveCapacity(input).hardBudgetMinutes) return built" in after
      and "BuildDailyPlan.ReplanRequest(ReplanTrigger.TODAY_CAPACITY_CHANGED, input)" in after and "keptPositions" not in after, "")
check("A16D-05_cycles_composed_once", "BlueprintScopes.COMPOSED.forEach" in settings and "ComposeAssessmentBlueprint(scope, persistence, content, clock).compose(evaluatorAvailable)" in settings, "")
open_app = body(application, "private fun openApp()")
refresh = body(application, "fun refreshToday(")
# Narrowed at 16E (`RMDX-v0` / `D-127`): 16D planned today when the store opened. Every start of the process opens the
# store — the reminder's alarm and the reboot signal too — so 16E plans only when the learner opens the app: opening the
# store plans nothing, the activity asks refreshToday once the store is ready, and refreshToday still plans before Today
# is read. A plan written from openApp again, or Today read before planning, still fails.
check("A16D-05_app_plans_before_today_is_read", "planToday(" not in open_app and open_app.find("ingestAll()") > 0
      and 0 <= refresh.find("planToday(opened.graph)") < refresh.find("TodayFactsQuery")
      and "app.refreshToday" in activity[activity.find("private val listener"):activity.find("private val app:")], "")
check("A16D-05_app_never_builds_a_plan_itself", "BuildDailyPlan(" not in application and "BuildDailyPlan(" not in activity, "")
check("A16D-05_capacity_change_replans", "PlanToday(opened.graph.persistence, opened.graph.content, opened.graph.clock, evaluatorAvailable()).afterCapacityChange()" in application, "")
check("A16D-05_today_action_is_thux", (home.get("capacity_context") or {}).get("today_override_action") == "change_today_capacity"
      and "onChangeTodayCapacity" in strip_comments(read(TODAY_SCREEN)) and "TodayCapacityDialog(" in activity, "")

# ---------------------------------------------------------------- 4. backup (LFPS-v0 §11)
restore_rules = (lfps.get("backup_export_restore") or {})
check("A16D-06_lfps_restore_rules", restore_rules.get("restore_atomic") is True and restore_rules.get("restore_verified_before_replacing") is True
      and restore_rules.get("user_initiated") is True, "")
rep = body(replacement, "fun replace(")
check("A16D-06_kept_before_replaced", 0 <= rep.find("Backup.export(liveStore, kept, driver)") < rep.find("Backup.restore(archive, liveFile, driver)")
      and "if (copy !is Backup.ExportResult.Exported) {" in rep and "return Outcome.NotKeptNotReplaced(" in rep and rep.find("return Outcome.NotKeptNotReplaced(") < rep.find("liveStore.close()"), "")
check("A16D-06_refusal_leaves_no_copy", "is Backup.RestoreResult.Refused -> {\n                kept.delete()" in rep, "")
check("A16D-06_never_overwrites_a_kept_profile", 'require(!kept.exists())' in rep, "")
confirm = body(application, "fun confirmRestore(")
check("A16D-06_restore_through_startup_replace", "startup.replace(" in confirm and "ProfileReplacement.replace(" in confirm
      and 'File(filesDir, "replaced-profiles")' in confirm, "")
check("A16D-06_confirmation_required", "BackupState.ConfirmRestore" in body(application, "fun prepareRestore(")
      and "app.confirmRestore" in activity and "onConfirmRestore" in strip_comments(read(PROFILE_SCREEN)), "")
check("A16D-06_learner_chooses_the_file", "ActivityResultContracts.CreateDocument(" in activity and "ActivityResultContracts.OpenDocument()" in activity, "")
check("A16D-06_state_survives_the_reopen", "private val backupState = mutableStateOf<BackupState>(BackupState.Idle)" in activity, "")
rp = body(startup, "fun <R> replace(")
check("A16D-06_store_unreadable_while_replaced", "if (status !is StoreStatus.Ready) return" in rp and "status = StoreStatus.Opening" in rp
      and "store = null" in rp and rp.find("runCatching { work(current) }") < rp.find("openNow()"), "")

# ---------------------------------------------------------------- 5. copy (D-033, THUX-v0)
copy = strings(body(presentation, "object SettingsCopy")) + strings(body(presentation, "fun refusalText(")) + strings(body(presentation, "private fun statusOf("))
bad = [s for s in copy for w in (r"%", r"\bpuan", r"\bseri\b", r"başarısız oldun", r"kaçırdın", r"borcun", r"hedefin", r"en az \d+ dk")
       if re.search(w, s, re.I)]
check("A16D-07_time_is_never_a_target", bad == [] and "ilerleme ya da hedef değildir" in presentation and "asla aşmaz" in presentation, str(bad[:3]))
check("A16D-07_smaller_day_not_a_failure", "başarısızlık değildir ve borç bırakmaz" in presentation, "")
status = body(presentation, "private fun statusOf(")
check("A16D-07_refusal_changes_nothing", status.count("Hiçbir şey değiştirilmedi.") >= 2 and "Hiçbir şey değiştirilmedi" in status.split("NotKeptNotReplaced")[1], "")
check("A16D-07_confirm_says_kept", "Mevcut profil silinmez; cihazda ayrıca saklanır." in presentation, "")
for path in (PROFILE_SCREEN,):
    s = strip_comments(read(path))
    check(f"A16D-08_screen_decides_nothing_{path.stem}", "coach.application" not in s and not re.search(r"Slider|ProgressIndicator|\d+\s*%", s), "")
check("A16D-08_profile_holds_its_ia_items", all(x in ((next((d for d in ia.get("destinations", []) if d.get("id") == "profile"), {}) or {}).get("primary_information") or
      re.findall(r"- (\w+)", read(IA)[read(IA).find("- id: profile"):read(IA).find("- id: profile") + 600]))
      for x in ("capacity_preferences", "backup_export_restore")), "")

# ---------------------------------------------------------------- 6. the new step 16F (D-125)
moved = contract.get("moved_to_16f") or []
check("A16D-09_end_to_end_moved_not_claimed", len(moved) >= 6 and scope.get("app_runs_tasks") is False, str(len(moved)))
check("A16D-09_16f_in_the_plan", "- [ ] **16F — Uçtan uca günlük döngü** — `D-125`" in read(EXECUTION_INDEX)
      and "### [ ] 16F — Uçtan uca günlük döngü" in read(MASTER_PLAN), "the step's own checklist lines, not a mention")
decisions = read(DECISIONS)
check("A16D-09_decisions", re.search(r"^## D-125 .*16F", decisions, re.M) is not None and re.search(r"^## D-126 .*STGX-v0", decisions, re.M) is not None, "")

# ---------------------------------------------------------------- tests name what they guard
for path, names in ((MODEL_TEST, ["with nothing stored the accepted presets hold and nothing is chosen for today",
                                  "today's choice applies to its own day only and carries no debt into tomorrow",
                                  "the newest row of a key wins, and an unreadable row is skipped rather than guessed"]),
                    (STARTUP_TEST, ["replacing the profile closes the open store, runs the work off the caller's thread, and reopens"]),
                    (STORAGE_TEST, ["schema v9 adds preferences to a populated v8 database and leaves every truth row as it was",
                                    "restoring a backup keeps the replaced profile aside, preferences included, and the backup becomes the profile",
                                    "a refused backup replaces nothing and keeps no copy, because nothing was lost"]),
                    (PRESENTATION_TEST, ["every refusal says nothing was changed, the confirmation says the old profile is kept, and nothing blames"]),
                    (COURSE_TEST, ["with all eight packages and no preference, the app plans today inside the normal preset, once",
                                   "a shorter day replans today inside it, the same choice again writes nothing, and tomorrow it is gone",
                                   "a new learner's Today now shows a real plan, and its first task is one of the four starting lessons"])):
    text = read(path)
    for name in names:
        check(f"A16D-10_test_{re.sub(r'[^a-z0-9]+', '_', name[:40].lower()).strip('_')}", f"`{name}`" in text, f"{path.name}: {name}")

# ---------------------------------------------------------------- verification claims
mr = contract.get("mutation_results_kotlin") if isinstance(contract.get("mutation_results_kotlin"), dict) else {}
mids = [m.get("id") for m in mr.get("mutants", [])]
check("A16D-11_kotlin_mutation_all_detected", mr.get("total") == mr.get("detected") == len(mids) and len(set(mids)) == len(mids) and len(mids) > 0
      and all(m.get("result") == "detected" for m in mr.get("mutants", [])), f"{mr.get('detected')}/{mr.get('total')}")
check("A16D-11_kotlin_crash_not_detection", mr.get("crash_is_detection") is False and mr.get("negative_control_result") == "survived_as_expected", "")
vm = contract.get("validator_mutation") if isinstance(contract.get("validator_mutation"), dict) else {}
check("A16D-11_validator_mutation", isinstance(vm.get("total"), int) and vm.get("total") == vm.get("detected") and vm.get("total", 0) > 0, str(vm))
runs = contract.get("verified_runs") if isinstance(contract.get("verified_runs"), list) else []
check("A16D-11_runs_pass", len(runs) >= 6 and all(r.get("result") == "PASS" for r in runs), "")
dv = contract.get("device_verification") or {}
check("A16D-11_device_not_claimed", dv.get("t6_run") is False and dv.get("claimed") is False and dv.get("screen_seen_on_device") is False, "")

# ---------------------------------------------------------------- spec, research, decision
spec = read(SPEC)
check("A16D-12_spec_status", "**Status:** ACCEPTED — independent 16D QA PASS" in spec and "`D-126`" in spec
      and not re.search(r"__[A-Z][A-Z_]*__", spec), "no result placeholder is left")
check("A16D-12_spec_next", "**16E — Bildirimler**" in spec and "**Not run: T6.**" in spec, "")
research = read(RESEARCH)
check("A16D-12_research", all(r in research for r in ("UXIA-v0", "THUX-v0", "D-033", "LFPS-v0", "SRR-v0",
      "https://developer.android.com/training/data-storage/shared/documents-files")), "")

finish()
