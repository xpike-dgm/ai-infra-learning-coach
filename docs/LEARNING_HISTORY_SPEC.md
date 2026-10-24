# Learning History Specification — LHRX-v0

**Stage step:** 16B — Öğrenme geçmişi  
**Status:** ACCEPTED — independent 16B QA PASS  
**Decision:** `D-123`  
**Model:** `LHRX-v0 — learning history and longitudinal assessment report, replayed from evidence`  
**Behaviour it implements:** `SPWX-v0` §§12–13, 16–18; `ASUX-v0` §§13–14; `GRE-v0` §16.2 (corrected in code); `LFPS-v0` (state is a projection of evidence)  
**Code:** `core-engines` (`MasteryEngine.VerificationState`, `MasteryEngine.replay`), `core-model` (`HistoryFacts.kt`, `EvidenceRow` fields), `core-ports` (two refinements), `data-persistence`, `core-application` (`LearningHistory.kt`, `RebuildMastery`, `MasteryTimeline`), `core-presentation` (`HistoryPresentation.kt`), `app-ui` (`HistoryScreen.kt`), `app-wiring`  
**Persistence / schema:** unchanged; no new table

## 1. Purpose

> **Zaman içinde ne oldu ve neyi değiştirdi — saklanan kanıttan, motorların kendi kararlarıyla, hiçbir günü kaçırılmış ya da hiçbir oturumu not almış saymadan?**

Primary invariant:

> **The learning history is the stored evidence in recording order.** Each row carries what the canonical engines say it changed. The engines are replayed over exactly the evidence up to that row, on the row's own study day, and the change is named in the program change report's own words.
>
> **The assessment report gives each session's `ASUX-v0` families:** the changes the session's own rows caused, what it could not measure, and the plan version its recompute wrote.
>
> Nothing is written and nothing is scored. A day with no entry is not drawn, and no change can be stated that an engine did not make. Rebuilding mastery from the same evidence always gives the same answer.

---

# 2. What was found

- **The session report was never stored.** 13E's "what changed" was computed at the end of a session and then discarded. The user decided that history replays the engines instead of adding a table.
- **Two recomputes of the same evidence gave different answers.** 12A's rebuild continued from the stored mastery axis. When a verification was open, it treated the very contradiction that opened it as a failed recheck. So a second rebuild of the same evidence took mastery away, and so did a *passed* fresh recheck. `GRE-v0` §16.2 says a positive recheck keeps mastery.
  - The 16B replay found this: a session's row-by-row replay disagreed with the batch recompute.
  - The 12A test that pinned the old behaviour had no fresh recheck in its input.
  - The user decided to fix it inside 16B (2026-10-18).
- **What a passed recheck changes.** It also revalidates retention and resolves an open weakness question. The history now shows all three.

---

# 3. Decisions

User decisions (2026-10-17 and 2026-10-18):

- "What changed" comes from replaying the engines over stored evidence. No new table is added.
- 16B covers the learning history and the longitudinal assessment report.
- No tutor or learner text is stored. The history shows the recorded assistance levels and the attempts. The working session stays unstored, as `TRUX-v0` defines it.
- Core plus a real Compose screen.
- The mastery recheck bug is fixed inside 16B, as a declared correction.

The assistant defaults are listed in the contract (`arch/16b_learning_history/learning_history.yaml`).

---

# 4. The mastery correction (`GRE-v0` §16.2)

Each Objective now carries a `VerificationState`:

- the contradictions that opened the verification, and the newest of them;
- contradictions already handled;
- contradictions forgiven by a positive recheck.

The rule:

- Only evidence recorded *after* the opening contradiction counts as a fresh recheck.
- **No fresh check:** mastery is held and the verification stays open, however often the Skill is rebuilt.
- **A fresh, clean contradiction:** the verification resolves negatively, and the gates decide on the evidence as it stands.
- **A fresh, clean positive:** the verification closes, the opening contradiction leaves the score window as noise, and mastery is kept.
- Assisted, provisional, contested, prerequisite-contaminated or solution-exposed work is never a recheck.

`MasteryEngine.replay` decides a Skill from its evidence in recording order: first with no evidence, then after each row. `RebuildMastery` writes its last decision. `MasteryTimeline`, which retention and weakness read, uses the same steps. The two can no longer drift apart, and no stored axis is read.

---

# 5. History and report

- **Entry:** one evidence row. It shows:
  - its family — assessment, remediation, review or learning; the first that applies wins;
  - its evidence kind, from 16A's four;
  - its outcome, worded as what happened;
  - the assistance levels recorded on its attempt;
  - its changes, using 13E's `StateChangeKind` and its sentences, or "Durum değişmedi."
- **Replay:**
  - The engines are rebuilt over a prefix of the evidence, inside an in-memory copy of the store. No truth or projection reaches the real store.
  - The state before the first row is what the engines say about no evidence, so the first changes are known rather than unknown.
- **Days:** an entry is grouped under its own study day, newest first. Only days that have entries appear, so nothing can be drawn as a gap, a miss or a streak.
- **Session report:** for each session it shows:
  - the state changes its own rows caused, each named once;
  - what it could not measure, with `ASUX-v0` §13.3's reasons, including ready blueprint slots that no evidence from the session answered;
  - the plan changes of the replan written for it.
  
  If nothing changed, the report says so plainly. If no plan version written for the session is found, the plan is not claimed to be unchanged.
- **Screens:**
  - The Progress overview opens the history and the report.
  - `skill_detail` opens that Skill's own history.
  - The report opens the history.
  
  All of these are accepted `UXIA-v0` edges.

---

# 6. Verification

- **Suites:**
  - `MasteryEngineTest`: the corrected §16.2 cases, and replay idempotence.
  - `LearningHistoryTest`:
    - each row's changes match the engines;
    - the families;
    - the replay writes nothing and ends where the live recompute ended;
    - a session's report says what its own 13E report said, plan changes included;
    - a passed recheck keeps mastery, and recomputing again changes nothing.
  - `HistoryPresentationTest`.
  - `HistoryStorageTest`: real SQLite.
  - The 16B section of `ShippedCourseTest`: all eight packages and recorded work on the real store.
- **Mutation:** Kotlin 24/24. The first run left three mutants alive and found one equivalent:
  - H05: the forgiven contradiction kept in the score window. A test was added.
  - H06: the opening contradiction not marked as handled. A test was added.
  - H10: a session's rows not put in the assessment family. A test was added.
  - H12 was equivalent: a redundant condition, so the condition was removed from the code.
  
  The whole set then ran again. The validator's own mutation run detects every mutant; the counts are in the contract's `validator_mutation`. Each negative control survived.
- **Validator:** `tools/validate_learning_history.py` reads `SPWX-v0`, `ASUX-v0`, `PCRX-v0` and `GRE-v0` and the real Kotlin, and every check passes (`arch/16b_learning_history/qa_report.yaml`).
- **Narrowed gates:** five earlier validators. The list is in the contract.
- **Not run: T6.** The screens have not been seen on a device.

---

# 7. Open loops

| Loop | Owner |
|---|---|
| Replay cost (quadratic in a Skill's rows) | 18E |
| A contradiction from before mastery that is still in the window may be named among the ones that opened a later verification | 18C |
| Weekly slots must pass the session's id to count as answered | 16D |
| The history is read under today's rules and today's dispositions | recorded |
| Topic, weakness and English views | 16C |
| T6 and the screens on a device | 19 |

---

# 8. Anti-patterns explicitly rejected

- A streak, an attendance count, a calendar grid, or a gap drawn as a miss.
- A grade, score, average or trend line across sessions.
- A change claimed that no engine made, or a plan claimed unchanged that nobody found.
- Stored conversation text.
- A history that writes anything.
- A mastery rebuild that gives two answers for the same evidence.

---

**Next step:** **16C — Progress / weakness gösterim kuralları** (fresh PRE and the user's new explicit approval).
