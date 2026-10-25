# 16E — Notifications: research synthesis

**Step:** 16E — Bildirimler (`RMDX-v0` / `D-127`)  
**Question:** when may this app speak to the learner from outside itself, what may it say, and how does it stay honest — never stale, never a count of days, never a debt?

## 1. Internal contracts (binding)

- **`UXIA-v0`** (`ux/8a_information_architecture/ia.yaml`, `docs/INFORMATION_ARCHITECTURE_SPEC.md`):
  - Profile owns `reminders`;
  - §15: a notification may target Today, and entry validation happens before any stale or no-longer-eligible action opens;
  - §18 pattern 8 forbids a stale notification that bypasses eligibility or current-state checks;
  - `streak` is a forbidden top-level concept.
- **`V1_SCOPE` §17** names a daily reminder, retention due and weekly/monthly assessment notifications. **SC-043** says weekly/monthly and due-retention notifications are scheduled only when that state really occurs, and that a completed or no-longer-due task must not produce a stale notification.
- **`SRR-v0`** (`docs/MISSED_DAY_RECOVERY_SPEC.md`):
  - absence is not failure, not a mastery penalty and not task debt;
  - yesterday's plan is never replayed;
  - scenario H: with no app opening during an absence, the starvation counter does not move;
  - §20 sent "notification cadence" here.
- **`THUX-v0`** (`ux/8b_today_home/home.yaml`, `docs/TODAY_HOME_SCREEN_SPEC.md`): no streak pressure, no fake urgency score, no streak-loss punishment, no guilt or debt state.
- **`EODX-v0`** (`docs/END_OF_DAY_SPEC.md`):
  - the end of a day is a boundary in time, not a verdict;
  - any end-of-day notification was sent here.
- **`VDSX-v0`** (`docs/DESIGN_SYSTEM_SPEC.md`): no countdown, timer or urgency device, and no streak visual.
- **D-033 / `PLNX-v0` / `RPLX-v0`:**
  - the plan is the planner's selection inside today's hard budget;
  - a need that did not fit is not a debt;
  - a new study day is a re-entry plan.
- **`STGX-v0` (16D):**
  - a preference is appended to the store, so a backup carries it;
  - 16D planned today when the store opened (`openApp`).
- **D-080:** personal use only. There is no store distribution, so a store policy is not a constraint here; the device's own rules are.

## 2. External sources (read 2026-10-25)

1. **Schedule alarms** — https://developer.android.com/develop/background-work/services/alarms (last updated 2026-10-21).
   - Inexact alarms never fire before the trigger time. `setWindow` is delivered within its window when no battery restriction is in effect.
   - For apps targeting Android 12+, windows under ten minutes are typically clipped to ten.
   - Alarms are deferred while the device is in Doze.
   - All alarms are cancelled when the device shuts down. Rescheduling needs `RECEIVE_BOOT_COMPLETED` and a `BOOT_COMPLETED` receiver, which the page advises keeping disabled until it is needed.
   - Exact alarms need `SCHEDULE_EXACT_ALARM` or `USE_EXACT_ALARM`. The page attaches no permission to inexact alarms.
   - Used for: a ten-minute `setWindow`, no exact-alarm permission, a re-arm receiver enabled only while the reminder is on, and silence for a delivery that lands on another date.
2. **`AlarmManager`** — https://developer.android.com/reference/android/app/AlarmManager.
   - "Apps should not pass in a window of less than 10 minutes."
   - Registered alarms are cleared when the device is turned off and rebooted.
   - The page says nothing about app updates or force-stop.
   - Used for: re-arming on every open and on `MY_PACKAGE_REPLACED` rather than trusting an alarm to survive.
3. **`BroadcastReceiver.goAsync`** — https://developer.android.com/reference/android/content/BroadcastReceiver.
   - Receivers may run for up to 10 seconds before the app is considered non-responsive.
   - The limit still applies after `goAsync`, up to `PendingResult.finish()`.
   - Used for: an eight-second budget for waiting on the store and deciding; past it the reminder is silence.
4. **Notification runtime permission** — https://developer.android.com/develop/ui/views/notifications/notification-permission.
   - `POST_NOTIFICATIONS` is a runtime permission from Android 13, and the guidance is to ask in context, on a user action.
   - Notifications are off by default for new installs.
   - `areNotificationsEnabled()` should be checked before posting.
   - Used for: asking only when the learner turns the reminder on, and checking again before every post.
5. **Channels** — https://developer.android.com/develop/ui/views/notifications/channels.
   - A channel is required from API 26.
   - Once created, its behaviour belongs to the user.
   - Used for: one default-importance channel, created when the reminder is turned on.
6. **Create a notification** — https://developer.android.com/develop/ui/views/notifications/build-notification.
   - A tap opens an activity through an immutable `PendingIntent`, with `setAutoCancel`.

Items 4–6 were read through a summary of the page rather than the full page text. The facts used from them are standard, and they match the platform behaviour the code relies on.

## 3. Synthesis

- **One reminder, and only what today's plan holds.**
  - The user chose a single daily reminder over separate retention and assessment notifications.
  - The reminder names a review, or a weekly or monthly assessment, only when the planner put one in today's plan. A review that did not fit today is therefore never announced, and announcing it would turn "did not fit" into a debt.
  - This satisfies SC-043 by construction: the content *is* current state.
- **A reminder must not write.**
  - In 16D, opening the store planned today. Every start of the process opens the store, and an alarm or a reboot signal starts the process.
  - A daily reminder would therefore have written a plan every day, including on days the learner never opened. `SRR-v0` scenario H assumes an absence with no app opening plans nothing (its starvation counter does not move), and a plan on a day nobody opened is a record of a day that did not happen.
  - Planning now happens when the learner opens the app.
  - The reminder reads today's stored plan, or the planner's own composition without the write (`BuildDailyPlan.preview`).
- **Silence over staleness.**
  - Anything the reminder cannot verify is silence, never a guessed or generic note: the day could not be read, the plan could not be read, it arrived on another date, Android blocks it, or work is already recorded today.
  - "Work recorded today" stands in for "today's plan has unstarted tasks" until 16F links attempts to planned tasks.
- **Its own table.**
  - `preference_record`'s keys are a SQLite CHECK, and changing a CHECK rebuilds the table and rewrites every stored row.
  - A new append-only table adds and rewrites nothing.
