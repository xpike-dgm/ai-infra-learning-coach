# Notifications Specification — RMDX-v0

**Stage step:** 16E — Bildirimler  
**Status:** ACCEPTED — independent 16E QA PASS  
**Decision:** `D-127`  
**Model:** `RMDX-v0 — one quiet daily reminder at the learner's time that says only what today's plan holds`  
**Behaviour it implements:** `UXIA-v0` Profile `reminders` and §15 (entry points outside the shell); `V1_SCOPE` §17 and SC-043; `SRR-v0` (absence is not debt); `THUX-v0` (no streak pressure or fake urgency); `EODX-v0` (the end of a day is not a verdict); D-033 (the plan fits today's time)  
**Code:** `core-model` (`ReminderFacts.kt`), `core-ports` (`reminderPreferences`), `core-engines` (`BlueprintComposer.scopeOfCandidate`), `data-persistence` (schema v10 `reminder_preference`), `core-application` (`Reminders.kt` — `ReminderSettings`, `ReminderCheck`; `BuildDailyPlan.preview`), `core-presentation` (`ReminderPresentation.kt`), `app-ui` (`ProfileScreen.kt`), `app-wiring` (`Reminders.kt`, `CoachApplication`, `MainActivity`, both manifests)  
**Persistence / schema:** schema v10 adds `reminder_preference`

## 1. Purpose

> **Seçtiğim saatte, bugünün planında gerçekten yeni bir iş varsa, bana tek ve sessiz bir hatırlatma gelebilir mi — hiçbir günü saymadan, hiçbir seriyi tutmadan, hiçbir yokluğu borç saymadan ve hiçbir şey yazmadan?**

Primary invariant:

> **A reminder is one quiet note a day at most, at the time the learner chose. It says only what today's plan holds.** That is the plan the planner selected inside today's time, read from the store or composed without being written.
>
> **It is off until the learner turns it on.** The decision is appended to the store like every preference.
>
> **It writes no truth.**
>
> **Anything that could make it stale or unfounded is silence.** It never counts days, keeps a series, names an absence, raises its voice after one, or says what the learner did not do.

---

# 2. What was found

- **Every start of the process planned today.**
  - In 16D, opening the store also planned today (`openApp`).
  - An alarm, or a reboot signal, starts the process, so a daily reminder would have written a plan every day, including on days the learner never opened.
  - Opening the store now plans nothing. Today is planned when the learner opens the app: the activity asks `refreshToday` once the store is ready.
- **The reminder needs a plan on a day the learner has not opened.**
  - A reminder is due before the learner opens the app, so today usually has no stored plan yet.
  - `BuildDailyPlan.preview` returns the very plan `build` would write, one line before the write transaction. Nothing is written.
  - When the learner opens the app, the planner writes exactly that plan. The course test proves this on the shipped curriculum, after days away too.
- **The preference keys cannot grow without a table rebuild.**
  - `preference_record`'s closed key list is a SQLite CHECK, and SQLite changes a CHECK only by rebuilding the table, which rewrites every stored row.
  - The reminder therefore has its own append-only table, which adds and rewrites nothing.
- **Android does not document what happens to an alarm after an app update or a force-stop.** It documents only that a reboot clears alarms. The reminder is armed again on every open, on `MY_PACKAGE_REPLACED`, on clock and time-zone changes, after each alarm and after a restore.
- **Earlier gates pinned the old shape.**
  - 16D's `A16D-04_schema_v9` pinned version 9, and `A16D-05_app_plans_before_today_is_read` pinned the plan inside `openApp`. Both were narrowed, each marked "Narrowed at 16E".
  - 16D's storage test pinned version 9 and was narrowed the same way.
  - A first draft split `BuildDailyPlan.build` into compose and write. Five earlier validators (12C, 12D, 13C, 13D, 13F) read `build`'s body as written, so the split was withdrawn: `build` keeps its body, and the preview stops one line before the write.

---

# 3. Decisions

User decisions (2026-10-25):

- **Scope:** one daily reminder at the learner's chosen time, at most one a day. Its content is built when it is due, from current state: today's plan, a review in it, a weekly or monthly assessment in it. With nothing real to say, nothing is sent.
- **Default:** off until the learner turns it on in Profile. Android's notification permission (13+) is asked only then.
- **Timing:** approximate, with an inexact window and no exact-alarm permission.

Assistant defaults:

- **Content source:** today's stored plan when the learner already opened the app today, otherwise the preview. A review or an assessment that did not fit today is not named, because not fitting is not a debt.
- **Work recorded today:** an attempt recorded on today's study day silences the day's reminder, until 16F links attempts to planned tasks.
- **Suggested time:** 19:00, where the field starts. Nothing is scheduled until the reminder is turned on.
- **Window:** ten minutes, Android's shortest inexact window. The alarm never fires early.
- **Its own day:** a reminder delivered on another local date (Doze, a late wake) is silent.
- **One a day:** the study day of the last reminder shown is kept on the device. It is delivery bookkeeping, not a learner decision.
- **A tap** opens the app the way its icon does. Today is the start destination, and the notification carries no task.
- **Storage:** its own append-only table (schema v10).
- **Budget:** the receiver waits at most eight seconds for the store and the decision.
- **No end-of-day and no absence notification.**

---

# 4. When a reminder is shown

`ReminderCheck.decide` answers in this order. The first rule that holds is the answer.

| # | Rule | Answer |
|---|---|---|
| 1 | The reminder is off | silent `disabled` |
| 2 | Android blocks the app's notifications | silent `notifications_blocked` |
| 3 | It was scheduled for another local date | silent `delivered_on_another_day` |
| 4 | Today's reminder was already shown | silent `already_shown_today` |
| 5 | No curriculum is published | silent `no_curriculum` |
| 6 | Today's attempts cannot be counted | silent `day_unreadable` |
| 7 | An attempt is recorded today | silent `work_recorded_today` |
| 8 | Today's stored plan cannot be read | silent `plan_unreadable` |
| 9 | Today's plan has no new task | silent `nothing_planned` |
| 10 | Otherwise | **show** what today's plan holds |

- **The store not ready within the budget** is silent `store_not_ready`.
- **Silence is never a message about the learner.** A silent reason is only logged.
- **The plan:** the stored plan for today when there is one, read only when its trace describes its rows (12E). Otherwise `BuildDailyPlan.preview`, the re-entry plan after days away included. Yesterday's plan is never named.

---

# 5. What a reminder says

- **Title, every day:** "Bugünün planı hazır".
- **Body:** only what today's plan holds:
  - how many new tasks (never one kept because it was already started);
  - the plan's own estimate of their minutes, when it has one;
  - whether a review (`retain`) is among them;
  - whether a weekly or monthly assessment slot is among them.
- **Example:** "3 görev, yaklaşık 45 dk · içinde tekrar ve haftalık değerlendirme var".
- **Never:** a count of days, a series, an absence, a debt, urgency, a verdict, a percentage or a goal. No function can produce one, and the test scans every combination for such words.
- **A tap** opens the app the way its icon does. Nothing is opened past the checks every entry makes (`UXIA-v0` §15).

---

# 6. The setting

| Field | Value |
|---|---|
| On / off | off by default; `reminder_enabled` 0 or 1 |
| Time | a local minute of the day, 0–1439; typed as `SS:DD`; suggestion 19:00 |

- **Append-only:** `reminder_preference` is guarded like every user truth table. One row is the whole setting, and the newest readable row is the setting.
- **The schema refuses:** a minute outside the day; an on/off value that is neither; a row without both values.
- **Migration:** v10 is a forward migration, tested against a populated v9 database. Every truth row stays as it was.
- **Backup:** a backup carries it, like every preference.
- **Profile** shows one of four states:
  - off, with the time it would come;
  - on, with its time;
  - on but blocked by Android, with a button to Android's notification settings;
  - "Bildirim izni verilmedi; hatırlatma kapalı kaldı" right after a refusal, also with that button. Android stops showing its prompt after repeated refusals.
- **The note:** "Günde en çok bir kez gelir … Hatırlatma bir yükümlülük değildir; gün saymaz, seri tutmaz, ara vermeyi borç saymaz."

---

# 7. Delivery on Android

- **Alarm:** `setWindow(RTC_WAKEUP, …, 10 min)` with an immutable `PendingIntent` to the app's own, non-exported receiver. Arming again replaces the alarm.
- **Re-arm:** on every store open, after each alarm, and on reboot, app update, time and time-zone changes.
  - The receiver for the system signals is disabled in the manifest. It is enabled only while the reminder is on, so with the reminder off nothing wakes the app.
- **Receiver:** uses `goAsync`, waits at most eight seconds for the store and the decision, and posts only what core decided. It checks `areNotificationsEnabled` and the channel before posting.
- **Channel:** "Günlük hatırlatma", default importance, created when the reminder is turned on.
- **Permissions:** `POST_NOTIFICATIONS` and `RECEIVE_BOOT_COMPLETED`, in both builds. The build without the AI adapter still has no network permission.

---

# 8. Not done here

| Loop | Owner |
|---|---|
| The end-to-end daily loop; "work recorded today" can become "unstarted tasks in today's plan" once attempts link to planned tasks | 16F |
| Notification polish | 17 |
| Theme and accessibility preferences | 17D |
| Focus preference | 18B |
| The alarm, the receivers, the permission prompt and the notification on a device | 19 |

**Not run: T6.** The alarm, the receivers, the permission prompt and the notification have not been seen on a device; the decisions were run on the JVM through the same classes.

**Next step:** **16F — Uçtan uca günlük döngü**. It needs a fresh PRE and the user's new explicit approval.

---

**16F note (2026-10-26, `D-128`):** the open loop of §8 is closed. Attempts now name their planned task, so rules 6–7 read which of today's tasks were started: the content counts only unstarted tasks, a day whose tasks are all started is silent (`work_recorded_today`), and a read that fails is silent (`day_unreadable`). The silences are unchanged. Details: `docs/END_TO_END_LOOP_SPEC.md`.
