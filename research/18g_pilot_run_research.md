# 18G Research — Pilotun yürütülmesi (PLTX-v0)

**Step:** 18G — Pilotun yürütülmesi (`D-141`; added by `D-140`)
**Date:** 2026-10-28

## 1. What the accepted contracts already say

- `V1_SUCCESS_CRITERIA` SC-048 (P0): in a real-use pilot, open core-rule violations — a hard prerequisite bypassed, the learner's progress lost, a critical Skill counted mastered when it is not — are **0**.
- SC-049 (P1): over a real-use window of at least 14 days, on **90% or more** of the study days produced the learner does not have to correct the plan by hand for a structural error — "this task is impossible because of a prerequisite / the time is totally wrong / it leads down the wrong branch". 90% is a starting target, recalibrated with pilot data; a change for personal preference alone is not a structural correction.
- `D-080`: the product is for personal use on one device (Poco M6 Pro, API 36); `D-140` split the pilot's running — the journal, the PC report and the phone install — out of 18A into 18G.
- 16D (`STGX-v0`): the backup is the learner's own export to a place they choose; restore never changes anything without confirmation. That export is the only way data leaves the phone, so the PC report reads it.
- 11C (`SESX-v0`): the working session is not stored. The only times in the store are each row's own instant, so a planned task's real duration is not recorded; the interval between its first and last answer is all there is — it leaves out reading before the first answer and may include time in the background, so it is shown as an interval, not a duration.

## 2. Decisions taken with the user (2026-10-28)

- **Order.** 18B–18F read pilot data, so 18G runs first; the numbering does not change (`D-141`).
- **Done when the pilot starts.** The journal, the PC report, the phone install and the first day's record are ready; the 14 days then run on their own and their data is read in 18B.
- **Journal entry from Today.** One row, "Planla ilgili bir sorun bildir", opening five kinds — SC-049's three structural ones in its own words, a personal preference (recorded, not counted) and a rule violation (SC-048) — and an optional short note.
- **Install over USB.** The assistant installs the AI build with adb once the learner enables USB debugging; the learner approves on the phone.

## 3. Decisions taken from the sources

- **The journal observes and never acts.** A report that replanned or changed a preference would correct the very plan SC-049 counts; an entry writes one row and nothing else.
- **A day is counted once.** SC-049 counts days, so several structural reports on one day are one corrected day; the denominator is the days a plan was made plus any day a structural report names.
- **No report is not proof.** SC-048 is listed from what the learner reported; the report says that no report does not show that no rule was broken.
- **The note is short and the learner's own.** At most 200 characters, trimmed; it is the learner writing about the plan, not about a task's content, so it carries none of the tutor text 16B keeps out of the store.

## 4. External

No web research was needed: the criteria are the repo's own. adb install (`adb install -r`) and USB debugging are the Android platform tools' documented path; nothing is downloaded from outside the SDK already installed.
