# Measurement Sessions from Today Specification — ASRX-v0

**Stage step:** 16H — Uçtan uca günlük döngü: ölçme oturumları  
**Status:** ACCEPTED — independent 16H QA PASS  
**Decision:** `D-132`  
**Model:** `ASRX-v0 — measurement from Today: daily assessment tasks, weekly and monthly slots and fast-path checks run in the one assessment interior; their attempts carry their session; a reported answer is held as contested; the fast path opens from a Topic and replans today`  
**Behaviour it implements:** `ASUX-v0` (8D); `DMAX-v0` (11D); `WBAX-v0` (13A); `MCAX-v0` (13B); `VDWX-v0` (13F); `WLRX-v0` (13D dispositions); `SPWX-v0` §7.2 (`assessment_opportunity`); `LHRX-v0` (16B history); `E2EX-v0` and `HLPX-v0`'s run; D-131  
**Code:** `core-model` (`Measurement`, `MeasurementKind`, `RunnableTask.measurement`, `ReplanTrigger.DIAGNOSTIC_REQUESTED`, `EvidenceRow.disposition`, `ProgressFacts.assessmentOpportunities`), `core-application` (`RunTask.kt` — `OpenTask.openSlot`, `openDiagnostic`, `RunTask.beginSession`, `contest`, `SlotSessions`; `FastPath.kt`; `AssessmentOpportunities`; `ProgressQuery(clock)`; `LearningHistory`), `core-presentation` (`MeasurementCopy`, `MeasurementPresentation`, `FastPathView`, `ProgressPresentation`, `HistoryCopy.disposition`), `app-ui` (`TaskRunnerScreen.kt`, `TopicScreen.kt`), `app-wiring` (`TaskRunFlow`, `TaskRunSession`, `CoachApplication`, `MainActivity`)  
**Persistence / schema:** none — schema v11 unchanged

## 1. Purpose

> **Today'den bir ölçme görevini — günlük ölçmeyi, haftanın ya da ayın bir slotunu, hızlı yolun bir kontrolünü — tek ölçme iç yüzünde yapabilir, bir soruyu boş bırakabilir, hatalı bir soruyu bildirebilir ve bir konuda hızlı yolu isteyip bırakabilir miyim — hiçbir şeyin not, yüzde ya da geçti/kaldı olmadığı, boş bırakmanın yanlış sayılmadığı ve bildirmenin aleyhime işlemediği biçimde?**

Primary invariant:

> **A measurement planned for today runs in the one assessment interior,** whatever composed it: a daily assessment task, a slot of this cycle's weekly or monthly blueprint, or a check of the learner's open diagnostic. Its kind is context, never extra weight.
>
> **Entry confirms, and guesses nothing.** The slot is still in this cycle's stored blueprint; the check still belongs to the open diagnostic; the item is published, judgeable, usable for its intent and unseen.
>
> **Every attempt carries its session.** A daily assessment's session opens when the learner begins. A slot's answer is recorded as that slot's evidence, so a prerequisite the same session showed missing does not count against its target.
>
> **A question may be left unanswered, and that is not incorrect.** An answered one may be reported: its evidence is held as contested by an appended disposition, never edited and never held against the learner.
>
> **The result** is its state, what could not be measured and what the engines reported.
>
> **The fast path** is opened only by the learner, from a Topic. It joins today's plan at once and keeps started work; leaving it owes nothing.

---

# 2. What was found

- **The blueprint was already composed on open, and its slots reached plans unopenable.**
  - `PlanToday.ensure` (16D) composes this week's and this month's blueprint before planning, so the user's "automatic on open" decision was already the behaviour.
  - Slot candidates reached the planner, but `OpenTask` read every planned task as an authored task: a planned slot would have entered with *content not compatible*.
- **An authored task and a slot serve the same need, so the planner picks one of them.** On the real store the composed slot was an alternative to the authored practice task for the same `continue_learning` need, and the planner chose the practice task. One task per need is `PBR-v0`'s rule and stays; a slot runs when the planner selects it.
- **No replan trigger existed for a fast-path request.** `PDT-v0` §15's list had `diagnostic_waiver_granted` but nothing for the request itself. With the user's decision it is a declared extension, `diagnostic_requested`, reasoned with `PDT-v0` §8.7's own fast-path code.
- **The Learn tab has no screen.** The fast path's entry is on the Topic detail (user decision); Learn's own screen is 17's.
- **No entry placement flow exists,** and no diagnostic source for one. "Entry placement as a diagnostic source" is not built here — inventing a placement flow is not 16H's — and goes to 18A.
- **A correction's reason was applied but never carried on the row.** `EvidenceDispositions.effective` made a contested row contested, but the row did not say which correction it was. It now carries the disposition and its reason, so the history can name it; a reinstated row is the row again (13D) and names nothing.
- **Earlier checks pinned what 16H changes:**
  - 16F's entry, serving and answer checks;
  - 16G's two answer checks;
  - 12D's trigger list;
  - one 16F test.

  Each was narrowed and marked "Narrowed at 16H".

---

# 3. Decisions

User decisions (2026-10-26):

- **Scope:** everything in 16G's `moved_to_16h` in one step:
  - sessions from Today and slot session ids;
  - the fast path's entry;
  - dispositions and contest from the app;
  - `assessment_opportunity`.
- **Blueprint composition:** automatic when the learner opens the app in a new week or month. This was already the behaviour since 16D.
- **Contest:** yes — *Bu soruda sorun var* holds the answer as contested; it is not evidence, not wrong, and the session continues.
- **Fast path replan:** immediately, through a new trigger `diagnostic_requested` (`D-132`). Started work is kept.
- **Fast path entry:** on the Topic detail; the Learn tab stays empty until 17.

Assistant defaults:

- **The runner.** The three measurement kinds run in the existing runner, one item at a time; each item is one atomic boundary. Orientation says which session it is, with the independence disclosure, before any work (`ASUX-v0` §6).
- **When a daily session opens.** A daily assessment opens one `daily` `assessment_session` row when the learner begins, not at entry, so entry still writes no truth (16F).
- **Intents:**
  - an assess task's items: verification;
  - a slot's: its role's intent;
  - a diagnostic check's: mastery evidence (13F's route);
  - every other task's: checkpoint.
- **No lesson first.** A measurement is a check, not teaching.
- **Slot evidence.** A slot's evidence goes through `RecordSlotEvidence`, together with what the session's earlier slots produced. That record is read from the attempts that name the session. At the end of a slot run, `ApplyRetroactiveContamination` runs before the recompute.
- **When a report is offered.** Only on an answered question that wrote evidence. Before answering, leaving it unanswered is the way: a report with no evidence would hold nothing, and promising a review nobody records would be untrue.
- **What a report writes.** `evidence_disposition` `contested`, reason `learner_reported_item_problem`, decided by `user_report` (`DDM-v0`'s deciders).
- **The recompute reads the report.** The end-of-run recompute re-reads every answered Skill, so a contested row is read as contested before anything is reported.
- **Which Skills the fast path requests.** Only Skills new learning may start on (`KGC-v0` §27); the others are left out rather than refusing the whole request.
- **Withdrawing.** It does not replan. Today's checks then fail entry by name, and the next plan omits them.
- Working Turkish microcopy; 17 owns the polish.

---

# 4. Entering a measurement (`OpenTask`)

1. **The planned candidate decides the kind:**
   - `weekly:<cycle>:<slot>` / `monthly:<cycle>:<slot>`: a blueprint slot (13A/13B's candidate id);
   - `diagnostic:<session>:<objective>:<item>`: a fast-path check (13F's);
   - an authored task: as before. An `assess` task is a daily assessment.
2. **A slot** must be a ready slot of the stored blueprint of **this** cycle, for this Skill, with its item published. An earlier cycle's slot is not debt, and an unreadable blueprint is not guessed.
3. **A check** must belong to the diagnostic that is open now. A withdrawn or replaced request's checks are not run.
4. **The prerequisites** are the gate's, read from the item's own required Skills.
5. **The capability.** The item must be judgeable here (16G), usable for the measurement's intent, and unseen. Otherwise entry names *no item the app can evaluate* or *no unseen item*.
6. **Entry writes nothing.**

---

# 5. Inside a session (`RunTask`)

- **Begin.** A daily assessment appends its `daily` session row. A slot's and a diagnostic's session already exist, and nothing is opened.
- **Answer.** Every attempt carries `assessment_session_id`, whatever judged it (key, tests or rubric). A slot's evidence is the slot's (`WBA-v0` §25, `MCA-v0` §20).
- **Skip.** *Boş bırak* moves on; nothing is written, and it is not incorrect (`ASUX-v0` §7.3).
- **Report.** *Bu soruda sorun var* on an answered question appends a `contested` disposition to each row it wrote. The row is never edited (13D). It is held, not counted against the learner, and named in the history.
- **Help.** Help stays allowed and non-punitive; H3/H4 converts the item to learning, as 16G's run already says.
- **End.**
  1. For a slot run, retroactive contamination runs first.
  2. The touched Skills are recomputed, which includes 13F's coverage.
  3. 13E's report says what changed, and only a change replans (16F).

---

# 6. The result (`MeasurementPresentation`)

`ASUX-v0` §13: one boundary per question shown. Each boundary is answered (frozen), skipped (unsubmitted), reported (contested) or never answered (open).

The lines say:
- **the state:** *Ölçüm tamamlandı* or *Ölçümün bir kısmı yapıldı…*, with the unanswered part never wrong and never debt;
- **what could not be measured reliably:** unanswered, reported, assisted, a shown solution, a pending evaluation;
- **what the engines reported.**

There is no score, percentage, grade or pass/fail anywhere.

---

# 7. The fast path (`FastPath`, Topic detail)

- **On a Topic**, *Bunu biliyorum, hızlı yolu dene* asks for the fast path over the Topic's Skills that new learning may start on. The request is recorded (13F) and today's plan is replanned with `diagnostic_requested`, keeping started work.
- **While it is open,** the Topic shows 13F's result: which parts are skipped, and which go back to normal learning without being called a failure. *Hızlı yoldan çık* withdraws it; what was waived stays.
- **An open check is an `assessment_opportunity`,** as is a ready, unserved slot of this cycle's blueprint (`SPWX-v0` §7.2).

---

# 8. What moved on

- Entry placement as a diagnostic source (18A).
- The Learn tab's own screen (17).
- Misconception memory in item selection (18D).
- `RebuildMastery` writing its own watermark on `skill_state` (18E).
- The day summary naming a run's real changes (17).
- A blueprint result's `stateChangeRefs`, which nothing stores (18D).

---

# 9. Verification

Recorded in `arch/16h_assessment_sessions/assessment_sessions.yaml`.

- **The course test on the real SQLite store,** with all eight packages and the shipped suites:
  - a learner's blueprint is composed on open, a slot is planned, opened as a measurement of its session and answered as that session's slot evidence, and it is not offered again;
  - a daily assessment opens its own session as the learner begins, and a reported answer is held as contested and named in the history;
  - the fast path joins today's plan at once, keeps started work and opens its check in its own session, and leaving it owes nothing.
- **Mutation:** Kotlin and validator mutation, each with a negative control.
- **Builds and tests:** the full JVM suite, `verifyModuleBoundaries` and both APKs.
- **Not run:** T6. The sessions have not been seen on a device.

---

# 10. Acceptance criteria

1. A planned slot, a fast-path check and an assess task each enter as a measurement, only when entry confirms every condition; entry writes nothing.
2. Every measurement attempt carries its session; a daily session opens when the learner begins; a slot's evidence is the slot's.
3. Skipping writes nothing; a report appends a `contested` disposition and edits nothing.
4. The result names its state and what could not be measured; there is no score, grade or pass/fail.
5. The fast path is opened only by the learner, from a Topic, and replans today keeping started work; withdrawing owes nothing.
6. `assessment_opportunity` names exactly the Skills a session could measure now.
7. Independent 16H QA passes and the full `validate_*.py` sweep passes.

---

# 11. Open loops

- Run polish (17).
- The sessions and the fast path on a device, and T6 (19).

**AŞAMA 16 is complete with 16H.** Next numbered step: `17A — Görsel polish`. It needs a fresh PRE and the user's new explicit approval.
