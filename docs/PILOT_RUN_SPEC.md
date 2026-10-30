# Pilot Run Specification — PLTX-v0

**Stage step:** 18G — Pilotun yürütülmesi  
**Status:** ACCEPTED — independent 18G QA PASS  
**Decision:** `D-141` (the step was added by `D-140`; it runs before 18B–18F with no renumbering)  
**Model:** `PLTX-v0 — the learner reports a plan problem from Today in SC-049's own words; the journal records one row and never acts; a PC report reads an exported backup read-only for SC-048, SC-049 and the planner observation 18B reads`  
**Behaviour it implements:** `V1_SUCCESS_CRITERIA` SC-048 and SC-049, `STGX-v0` (16D backup), `SESX-v0` (11C: the working session is not stored), `PDT-v0` (the planner trace), `THUX-v0` (Today), `A11X-v0` (17D)  
**Code:** `core-model` (`PilotJournalFacts.kt`), `core-ports` (`pilotJournalEntries` — a refinement), `core-application` (`RecordPilotJournal`), `core-presentation` (`PilotJournalPresentation`), `app-ui` (`PilotJournalDialog.kt`, the Today row), `app-wiring` (`MainActivity` dialog state, `CoachApplication.recordPilotJournal`), `data-persistence` (schema v14, `pilotJournalEntries`, TEXT columns bound as text), `tools/pilot_report.py`, `tools/audit_a11y_emulator.py` (reads the dialog)  
**Persistence / schema:** v14 — `pilot_journal_entry` (append-only; nothing stored is rewritten)

## 1. Purpose

> **Öğrenci planla ilgili bir sorunu, planı değiştirmeden ve kendisi hakkında hiçbir şey söylemeden kaydedebiliyor mu; ve pilotun sayıları yalnız saklanan satırlardan, PC'de okunabiliyor mu?**

Primary invariant:

> **A journal entry is the learner's own report about the plan they were looking at:** one of SC-049's three structural corrections in its own words (a prerequisite makes it impossible, the time is totally wrong, it leads down the wrong branch), a personal preference (recorded, never counted) or a rule violation (SC-048), with an optional short note (trimmed, at most 200 characters).
>
> **It is appended in one row and nothing else changes:** no plan is written or replanned, no preference changes, nothing is evidence. A report that changed the plan would correct the very plan SC-049 counts.
>
> **The PC report opens an exported backup read-only and says only what the rows say.** SC-049 counts days, a day with any structural report once, on the day of the plan the report names; SC-048 lists what was reported and says that no report is not proof; the planner observation sets each planned task's estimate beside the interval between its first and last answer (reading before the first answer is not stored and time in the background may be inside it; a one-answer task has no interval; a task with no answer is not listed) and names the needs the planner kept passing over. A trace the app's own codec would not decode is named as unreadable, never guessed at.

---

# 2. The journal

- **Where:** one row on Today, "Planla ilgili bir sorun bildir", last — after everything about doing the work.
- **The dialog:** five radio rows (the row carries the role and the state), an optional note with its own count as supporting text, "Vazgeç" and "Bildirimi kaydet". Saving needs a kind; the note may be empty. Choosing the personal preference says it is not counted as a structural correction.
- **After saving:** "Bildirimin kaydedildi. Plan değiştirilmedi.", announced politely. Nothing is refreshed, because nothing changed.
- **What is stored:** the kind, the latest plan version (or none when there was no plan), the note, and the row's own time; append-only like every user table since 16D, so a backup carries it.

# 3. The PC report

`python -X utf8 tools/pilot_report.py <backup.coachdb> [--out report.md]` — standard library only.

| Section | What it says |
|---|---|
| Window | the study days a plan was made on (first – last), the days with an answer, plan versions and how many traces were unreadable; a window shorter than SC-049's 14 days is said |
| SC-049 | counted days = planned days ∪ days a structural report names; corrected days counted once; the share without a correction against the 90% starting target, never claimed met early; the three kinds counted; preferences listed as not counted |
| SC-048 | every rule-violation report with its day, plan and note, or "no report — which is not proof" |
| All reports | a table of every entry |
| Planner observation | estimate (the trace's `selected` at the task's position in its own plan) beside the first-to-last answer interval (— for one answer); needs eligible but not selected on each day's last plan, with the longest run of planned days; replan triggers |

# 4. Order, install and the first day

- 18B–18F read pilot data, so 18G runs first (`D-141`); the numbering does not change.
- 18G is complete when the pilot starts: the journal and the report are in the build, the AI build is installed on the Poco M6 Pro over USB (the learner enables USB debugging and approves on the phone), and the first pilot day is recorded. The 14 days then run on their own; 18B reads their export.

# 5. What was found

See `arch/18g_pilot_run/pilot_run.yaml` `found` for the full list.

# 6. Verification

`tools/validate_pilot_run.py` (the PC report is run against a database built from the real `Schema.kt` text), the Kotlin tests listed in the contract, Kotlin and validator mutation, and the accessibility audit on the emulator at 100% and 200% text with the journal dialog as its own screen. T6 was not run.
