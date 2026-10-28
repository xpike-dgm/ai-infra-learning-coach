# Initial Setup Specification — ISTX-v0

**Stage step:** 18A — Pilot başlangıcı  
**Status:** ACCEPTED — independent 18A QA PASS  
**Decision:** `D-139` (and `D-140`: 18G added)  
**Model:** `ISTX-v0 — the first-run setup: shown once, pre-shell, before any plan, on a store no one has used; it records the normal day as a preference and, only for the areas the learner ticks, one diagnostic whose source is the entry placement — never evidence, never mandatory; skipping records only that it was done; and a run's report reads an unwritten "before" the way the history does`  
**Behaviour it implements:** `UXIA-v0` §7.4, `VDW-v0` §3 (amended: `curriculum_entry_placement`), `STGX-v0` (16D), `PCRX-v0` (13E, amended), `LHRX-v0` (16B), `RUXX-v0` (17E)  
**Code:** `core-model` (`SetupFacts.kt`, `DiagnosticSource.CURRICULUM_ENTRY_PLACEMENT`), `core-ports` (`setupRecords` — a refinement), `core-application` (`InitialSetup`, `ReportProgramChanges` + `HistoryReplay.axesAt`), `core-presentation` (`SetupPresentation`, `Surface.InitialSetupFlow`), `app-ui` (`SetupScreen.kt`), `app-wiring` (`MainActivity` setup state, `CoachApplication.checkSetup/finishSetup`), `tools/audit_a11y_emulator.py` (reads the setup)  
**Persistence / schema:** v13 — `setup_record` (append-only; nothing stored is rewritten)

## 1. Purpose

> **İlk açılışta öğrenci kendi süresini ve bildiğini düşündüğü alanları, hiçbir şeyi kanıt saymadan ve zorunlu kılmadan söyleyebiliyor mu; ve bir koşunun sonu, geçmişin ve gün özetinin söylediği değişikliği söylüyor mu?**

Primary invariant:

> **The setup is shown once, before Today and before any plan,** on a store no one has used (a curriculum is published, no setup was recorded, no plan was ever made). A store from before 18A or a restored backup with work in it is not asked.
>
> **It asks two things and decides neither:** the normal day's minutes (a 16D preference, a ceiling) and, optionally, the areas the learner already knows something of. Nothing is pre-ticked. Ticking areas opens **one** diagnostic over their Skills on which new learning may start, with the source `curriculum_entry_placement`: "I know this" names the scope — it is not evidence, nothing is waived by it, and no diagnostic is mandatory.
>
> **Skipping is the safe exit** and is offered first; it records only that the setup was done — the presets stand and learning starts at the beginning.
>
> **It plans nothing.** Today is planned after it, by the planner, from what it recorded; a start of the activity does not plan while the setup is open.
>
> **A run's report says the change the history says** (user decision): an axis no engine had written before the run is read as the engines replayed over the evidence the snapshot saw; a written axis is never replaced.

---

# 2. What was found

- **There was no setup.** `UXIA-v0` §7.4 and `VDW-v0`'s `curriculum_entry_placement` existed only in specs; a fresh install went straight to a plan made with the presets.
- **A plan could have been made during the setup:** the activity refreshed Today on every start. It now waits until the setup is settled.
- **The run report named Skills in English** once a first run reported a change ("State Assignment Model", emulator) — against 17A's rule. It names them through `CurriculumNames.skill`.
- **The independent review found two blocking problems.** A setup interrupted after its diagnostic was written but before its row is offered again, and a skip then left that diagnostic open, holding the ticked areas' lessons; `finish()` now first withdraws a leftover placement diagnostic. And the copy promised short checks in today's plan, but a check joins the plan only when its Skill may start (on a fresh store 56 of 60 Skills wait); the copy now says the checks come as each Skill becomes startable. Also taken: the setup ends once on a double tap, the inputs survive a rotation, the skip note sits under the skip, and the placement source has one call site.
- **A first wrong answer now replans today:** it is a change in the run's report (a weakness question), and 13E replans on a change. The report is the net change of the run; on a single-answer run it is the history's change, while a run that moves a Skill and back may say less than the history lists.
- **The accessibility audit waited for the shell forever,** because it clears the app's data and now meets the setup first. It reads the setup as its own screen and leaves it by its safe exit.

---

# 3. Decisions

User decisions (2026-10-28):

- **Scope:** the first-run setup only (`initial_setup_flow` and the `curriculum_entry_placement` source).
- **The run report's "before":** the run's end report replays an unwritten "before" like the history — both say the change (amends `PCRX-v0`).
- **A new step, 18G — Pilotun yürütülmesi (`D-140`),** at the end of AŞAMA 18 with no renumbering: the pilot journal (SC-048/049 records and a PC pilot report) and installing on the phone to start the 14-day pilot.

Assistant defaults: see the contract's `assistant_defaults` (`arch/18a_initial_setup/initial_setup.yaml`) — areas rather than Topics as the unit of "I know this", only the normal day asked, a store with a plan treated as used, the setup row appended last, Turkish names in the run report.

---

# 4. What moved on

- The pilot journal and starting the 14-day pilot on the phone (18G, `D-140`).
- A calibrated `planner_diagnostic_opportunity` (18B).
- TalkBack and T6 (19).

---

# 5. Verification

- Kotlin: `InitialSetupCourseTest` (the eight shipped packages, the real store and planner), `SetupStorageTest`; narrowed `ProgramChangesTest`, `DiagnosticFactsTest`, `DisplayAndUsageStorageTest`.
- Narrowed validators ("Narrowed at 18A"): `validate_diagnostic_waiver.py` E13F-02, `validate_learning_history.py` A16B-05 (two), `validate_remaining_ux.py` A17E-07, `validate_program_change_report.py` E13E-06 and its application test list.
- Emulator (Pixel 7 AVD, API 34; data on drive D; not T6): the setup on a fresh install; 45 minutes and Linux gave a 45-minute day with a fast-path check; not shown again; the skip path; a first wrong answer's report naming the change in Turkish; the accessibility audit with the setup at 100% and 200% text.
- Validator: `tools/validate_initial_setup.py`. Mutation, review and counts: the contract.

---

# 6. Acceptance criteria

17A–17E's gates hold, and independent QA verifies at minimum:

1. The setup is shown once, pre-shell, only on an unused store, and plans nothing; Today is planned after it.
2. Only ticked areas open one diagnostic with the placement source; nothing is evidence, waived or mandatory; a skip records only the setup row.
3. The words say "not evidence", "not mandatory", "not a failure", "a ceiling, not a target".
4. The screen pre-ticks nothing, offers the skip first, uses checkbox rows and the field's own error.
5. Schema v13 only adds one guarded table.
6. The run's report fills only unwritten axes with the history's replay and agrees with the history.
7. The audit passes with the setup at 100% and 200%.

---

# 7. Open loops

See §4. **18A is complete.** Next numbered step: `18B — Planner gözlemi`. It needs a fresh PRE and the user's new explicit approval.
