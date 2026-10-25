---
type: session-log
status: completed
stage_step: 16E
model: RMDX-v0
decision: D-127
date: 2026-10-25
---

# 16E — Bildirimler — one quiet daily reminder that says only what today's plan holds

On 2026-10-25 the user said "16E ye başla". 16D (#63) had already been merged (de92b7c). The fresh 16E PRE showed `16D ✅ / 16E active-not-executed`, and the message was the approval for 16E.

## Result
- Canonical: `docs/NOTIFICATIONS_SPEC.md`
- Machine-readable: `arch/16e_notifications/notifications.yaml`
- QA: `arch/16e_notifications/qa_report.yaml`
- Stale audit: `arch/16e_notifications/stale_reference_audit.yaml`
- Research/synthesis: `research/16e_notifications_research.md`
- Final: `RMDX-v0 / D-127`
- Not run: T6. The alarm, the receivers, the permission prompt and the notification have not been seen on a device.

## User decisions
- Scope: one daily reminder at the learner's time, at most one a day. Its content comes from current state; with nothing real to say, nothing is sent.
- Default: off until turned on in Profile. Android's permission is asked only then.
- Timing: approximate (an inexact window), with no exact-alarm permission.

## Found
- Every start of the process planned today: in 16D, opening the store planned. An alarm or a reboot starts the process, so a daily reminder would have written plans on days the learner never opened. Opening the store now plans nothing, and the learner opening the app plans.
- The reminder usually speaks before today has a stored plan. `BuildDailyPlan.preview` returns the very plan `build` would write, one line before the write, and writes nothing.
- `preference_record`'s key list is a SQLite CHECK, and it cannot grow without rebuilding the table, so the reminder has its own append-only table (schema v10).
- Android does not document alarms after an update or force-stop, so the alarm is re-armed on every open.
- A first split of `BuildDailyPlan.build` broke five earlier validators that read its body as written; it was withdrawn for the one-line stop.
- A heredoc again turned an escape into a real newline (in the validator); it was fixed with the Edit tool.
- Mutant R18 first did not compile (`if (false)` broke a smart cast); it was rewritten and run alone, and detected.

## Verification
- Kotlin mutation 29/29 (R18 run again alone after its compile crash; negative control survived).
- Validator mutation 47/47 (single clean run after V47 tightened a check; negative control survived).
- 16E QA 103/103 PASS.
- Sweep 68/68 (58 light + 10 heavy).
- 1219 JVM tests, `verifyModuleBoundaries`, and both APKs. aapt2 shows both reminder permissions, the re-arm receiver disabled, no exact-alarm permission, and no INTERNET permission without the adapter. The eight packages are identical to the source assets.

## Next
`16F — Uçtan uca günlük döngü`. It needs a fresh PRE and the user's new explicit approval.
