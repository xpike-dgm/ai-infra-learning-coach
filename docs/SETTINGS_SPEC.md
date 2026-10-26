# Settings Specification — STGX-v0

**Stage step:** 16D — Ayarlar  
**Status:** ACCEPTED — independent 16D QA PASS  
**Decision:** `D-126` (the new step 16F: `D-125`)  
**Model:** `STGX-v0 — the learner's own settings, capacity and backup, and today's plan made by the app from them`  
**Behaviour it implements:** `UXIA-v0` Profile (capacity preferences, backup/export/restore); `THUX-v0` capacity context and `change_today_capacity`; D-033 (capacity is a hard budget); `ADAPTIVE_PLANNER_SPEC` §3 (profiles and v0 presets); `LFPS-v0` §11 (export and restore); `SRR-v0` (no replay of yesterday's plan)  
**Code:** `core-model` (`PreferenceFacts.kt`), `core-ports` (`preferenceRecords`), `data-persistence` (schema v9 `preference_record`, `ProfileReplacement`), `core-application` (`Settings.kt` — `CapacitySettings`, `PlanToday`; `StoreStartup.replace`), `core-presentation` (`SettingsPresentation.kt`), `app-ui` (`ProfileScreen.kt`, `TodayScreen` action), `app-wiring`  
**Persistence / schema:** schema v9 adds `preference_record`

## 1. Purpose

> **Uygulama kendi kişisel kullanımımda nasıl çalışsın — günlük sürem, yedeğim — ve bugünün planı bu süreden gerçekten kurulsun mu, hiçbir süreyi hedef, hiçbir geri yüklemeyi sessiz bir kayıp yapmadan?**

Primary invariant:

> **A preference is the learner's decision, appended and never edited.** It lives in the store, so a backup carries it.
>
> **Today's plan is made by the planner from those preferences.** The normal profile applies unless today has its own choice. A choice for today applies to today only and leaves no debt. A change replans today only if today's budget changed.
>
> **Time is a ceiling, never a target or a measure.**
>
> **A backup goes only where the learner puts it.** A restore replaces nothing until the learner confirms it, and the replaced profile is always kept.

---

# 2. What was found

- **Every step since 11 sent its app call here.**
  - Steps 11–16C each left "call it from the app" to 16D: the planner, the runner's items, attempts and evaluation, the recompute and reports, the tutor and the fast path.
  - The user decided (2026-10-25) to split this: 16D keeps the settings and today's plan, and a new step, **16F — Uçtan uca günlük döngü**, takes the rest.
  - 16F is added like D-099 did for 13F, with no renumbering (`D-125`).
- **A column named `preference_key` reads as a credential column.** 10E's credential scan refuses any column that could hold a key. The column is `preference_name`.
- **Two study-day columns break the adapter's time rule.** The adapter finds a row's study day by its suffix, so a second `…_study_day` column (`applies_to_study_day`) made the row's own day ambiguous. The column is `applies_to_day`.
- **A restore closes the store, and the shell forgets its state while it does.** The backup controls' state lives above the shell, so the outcome is still there afterwards.
- **Earlier gates pin the schema.**
  - The 10D, 11C and 11E checks read `truthTables`, `truthGuards()` and the read paths literally. The schema keeps `DDM-v0`'s thirteen tables as `truthTables` and adds `appendOnlyTables` beside it, so those checks hold as written.
  - Six 14x checks pinned schema version 8. They were narrowed, each marked "Narrowed at 16D": version 9 is accepted because this contract's `schema_migration` owns it.
- **A 14F check could never fail.** `E14F-07_not_published` held a literal backspace where a word boundary was meant (a heredoc wrote it in 14F). It was repaired and marked "Repaired at 16D".

---

# 3. Decisions

User decisions (2026-10-25):

- **Scope:** settings, plus today's plan made by the app, so that Today shows real tasks. The rest of the end-to-end wiring moves to 16F.
- **Capacity:** short, normal and intensive days with `ADAPTIVE_PLANNER_SPEC` §3's v0 presets of 30, 60 and 90 minutes. Each length can be changed, and today can have its own choice.
- **Backup controls are in 16D.** A restore needs explicit confirmation, and the replaced profile is kept aside.
- **Cycles stay fixed:** the ISO week starting on Monday, and the calendar month. There is no setting.
- **The focus preference goes to 18B.**

Assistant defaults:

- **Storage:** preferences are an append-only table in the store (schema v9), not platform settings.
- **Ranges:** a length and today's minutes are whole minutes from 0 to 1440. An unreadable stored row is skipped, never guessed.
- **Today's choice:** the newest choice made for today wins, and clearing it is a row too.
- **When the app plans:** on open and when a new study day is seen. This week's and this month's blueprints are composed first, once per cycle.
- **Replanning:** a capacity change replans with `today_capacity_changed` only when today's hard budget differs from the plan's. Nothing is reported as already started, because nothing can be started from Today yet (16F).
- **Keeping the replaced profile:** a verified copy of the live profile is written before anything is replaced. If it cannot be made, nothing is replaced.

---

# 4. Capacity

| Step (D-033 §2) | Source |
|---|---|
| 1 | today's minutes |
| 2 | today's profile (short / normal / intensive) |
| 3 | the normal profile |

- **Presets:** short 30, normal 60, intensive 90. These are product defaults the learner changes, not scientific values.
- **Range:** 0–1440 minutes.
- **Today only:** a choice for today applies to today's study day only. Tomorrow it is gone, nothing carries over, and yesterday's plan is never replayed.
- **The budget is a ceiling:** the plan fits inside it and never exceeds it. A smaller day is not a failure.
- **Profile** shows each length, whether it is the preset or the learner's own, and today's choice.
- **Today** has one action, "Bugünkü süreyi değiştir": a profile, a number of minutes, or clearing today's choice.

---

# 5. Preferences in the store

| Key | Day | Value |
|---|---|---|
| `capacity.short_minutes` | none | digits |
| `capacity.normal_minutes` | none | digits |
| `capacity.intensive_minutes` | none | digits |
| `capacity.today` | the study day | `none`, a profile id, or digits |

- **Append-only:** `preference_record` is guarded like every user truth table.
- **The schema refuses:**
  - an unknown key;
  - an empty value;
  - a today choice without its day;
  - a standing preference with a day.
- **Migration:** v9 is a forward migration, tested against a populated v8 database. Every truth row stays as it was.

---

# 6. Backup, export, restore

- **Export:** the learner chooses where the file goes, through the system picker. The archive is made in the cache, verified as a restore would verify it, then copied out. Nothing is uploaded.
- **Restore:**
  1. The chosen file is copied into the cache.
  2. The learner confirms the replacement.
  3. The store is closed.
  4. A verified copy of the live profile is kept in `replaced-profiles/`.
  5. The backup is verified and swapped in atomically.
  6. The store is reopened.
- **Refusals:** the four reasons are integrity, not a profile, a newer schema and an incomplete migration. Each says that nothing was changed, and a refusal leaves no copy behind.

---

# 7. Not done here

| Loop | Owner |
|---|---|
| The end-to-end daily loop (runner, answers, evaluation, recompute, tutor, fast path, code and open responses, slots' session ids, dispositions) | 16F |
| Reminders | 16E |
| Theme and accessibility preferences | 17D |
| Focus preference | 18B |
| The file pickers and the restore on a device | 19 |

**Not run: T6.** The screens and the system file pickers have not been seen on a device.

**Next step:** **16E — Bildirimler**. It needs a fresh PRE and the user's new explicit approval.

---

**16F note (2026-10-26, `D-128`):** a capacity change's replan now keeps the tasks the learner started (`StartedTasks.today`); §3's "nothing is reported as already started" held only while nothing could be started from Today. Details: `docs/END_TO_END_LOOP_SPEC.md`.
