# Diagnostic Waiver Implementation Specification — VDWX-v0

**Stage step:** 13F — Tanısal atlama (VDW-v0)  
**Status:** ACCEPTED — independent 13F QA PASS  
**Decision:** `D-104`  
**Model:** `VDWX-v0 — Validated Diagnostic Waiver Implementation`  
**Behaviour:** `VDW-v0 / D-037` (`docs/DIAGNOSTIC_WAIVER_SPEC.md`), 3H S06 and `PDT-v0` §23 invariant 12, `PDT-v0` §8.7 (diagnostic reason codes)  
**Gates it asks:** `MSTX-v0 / D-092` (`GRE-v0`'s own Objective gates, unchanged)  
**Engines it touches:** `PLNX-v0 / D-094` (coverage holds), `WLRX-v0 / D-102` (one narrowed rule), `PCRX-v0 / D-103` (two change kinds), `RSNX-v0 / D-096` (explanation of what was skipped)  
**Persistence:** `LFPS-v0`, `DDM-v0`, `LDBX-v0` — schema v7 (`diagnostic_coverage`, one projection added)  
**Boundaries:** `MSBX-v0 / D-078` — one port refinement, still four ports; one engine state family added (`VDW-v0`)  
**Added by:** `D-099` (user decision during 13A)

## 1. Purpose

A learner who already knows part of a Topic should not have to sit through its starting lessons again — and must not be allowed to skip a part they cannot show. 13F makes `VDW-v0` run: the learner asks for the fast path, a diagnostic gathers **the same `GRE-v0` evidence through the same pipeline**, and only an Objective whose gates first passed on that evidence has its starting lesson waived.

It answers one question:

> **Hangi başlangıç anlatımlarını atlayabilirsin — çünkü o Objective'i, mastery'nin kendi kapılarıyla, yardımsız gösterdin?**

Primary invariant:

> **A diagnostic is not an easier road to mastery; it gathers the same evidence sooner.** A waiver is granted only where an Objective's gates first pass on diagnostic evidence, it names that evidence, and it is coverage — never mastery, never retention. Saying "I know this" is never evidence, one easy item never waives a Topic, help taken or a clean miss ends the fast path without blame, and a lesson is skipped only for what was shown.

---

# 2. What was found before writing diagnostic code

- **Nothing ran `VDW-v0`.** The reason codes (`PDT-v0` §8.7), the `diagnostic_opportunity` need and the `diagnostic_waiver_granted` replan event existed; nothing produced or read them. 12F could not run S06, and `PDT-v0` invariant 12 had no test (`D-099`).
- **A waiver had nowhere to live.** `DDM-v0` names no waiver entity and `MSBX-v0` no owner: when 9C/9D were accepted, `VDW-v0` had no implementation step.
- **No row could say it came from a diagnostic.** `EvidenceRow` carried no session, and lesson completion is recorded nowhere, so "the gates passed before any teaching" could not be told from "the gates passed after it".
- **A planner task could not say what it teaches.** `TaskCandidate` named only its Skill; "a waived Objective's lesson is not taught again" (`VDW-v0` §17) had nothing to compare.
- **A partial waiver would not have replanned.** 13E reports only axis changes, and development between non-mastered states is not one; a diagnostic that waived two of four Objectives would have changed nothing the planner was told about.
- **`VDW-v0` §12.1 and `WLRM-v0` disagreed.** A clean pre-mastery miss is a `supported` weakness (`P1 weakness_detected`) under 13D's rule; `VDW-v0` says a failed "did you already know it?" check is not a penalty and the Objective returns to normal learning.

---

# 3. User decisions (2026-10-01)

1. **A diagnostic baseline miss is not a weakness.** A clean miss inside a diagnostic, on an Objective with no evidence from ordinary learning before it and a Skill not yet mastered, opens no weakness signal. The evidence is kept and enters the mastery window by `GRE-v0`'s own rules; the Objective returns to normal learning. `WLRM-v0`'s rule is narrowed for exactly this case and no other.
2. **Only the learner opens a diagnostic.** The fast-path request, a prior-experience claim and resuming after external learning are recorded as truth; the claim only names the scope. `planner_diagnostic_opportunity` needs a calibrated decision-value rule (18B) and `curriculum_entry_placement` an entry flow (16D); neither is representable here.
3. **Help taken ends the fast path for that Objective.** An assisted answer or a seen solution in the diagnostic ends it: no waiver from this diagnostic, normal learning continues, no penalty, and the learner may ask again later with fresh items.

---

# 4. Scope boundary

## 4.1 13F decides

- what a diagnostic is in storage, and which one is open,
- the waiver rule, its evidence and its withdrawal,
- where each Objective of the open diagnostic stands and what is checked next,
- the diagnostic's need and candidates, and how a lesson is held by coverage,
- the outcome (full / partial / no waiver) and its reasons,
- the result and explanation sentences (working microcopy).

## 4.2 13F does not decide

- any gate, threshold or mastery state — `GRE-v0`'s gates are asked as they are,
- the Topic state (`available → mastered`) — 16C, from its Objectives' waivers (`D-102` user decision),
- the app's fast-path entry and calling the use cases after a session (16D),
- carrying a waiver across curriculum versions — no `KGC-v0` §26 migration record exists yet (15),
- a planner-initiated diagnostic (18B), and entry placement (16D),
- final wording (14).

No score, threshold, ratio or weight is introduced.

---

# 5. The diagnostic in storage

A diagnostic is a `daily` assessment session whose content is the learner's request in `diagnostic_scope/1` — strict, versioned text that decodes or is refused:

- `request`: source, study day, curriculum version; then one `target` per Objective (pinned Objective, its Skill, critical flag).
- `withdrawal`: the session withdrawn and the study day.

The **newest diagnostic row** decides (`PersistencePort.latestAssessmentSessionIn(scope, format)`): a request is the open diagnostic; a newer request replaces it — nothing is owed; a withdrawal ends it. Schema v7 refuses a `daily` row whose content is in any other format, so nothing can pass itself off as the learner's request. The scope is Objectives and Skills, never a Topic (`VDW-v0` §4).

**Request (`RequestDiagnostic`).** The required and critical Objectives of the named published Skills, minus those already shown or already waived (§17: not tested again). A Skill that is not `published` cannot have its starting lessons skipped. Nothing checkable → nothing written.

**Withdrawal (`WithdrawDiagnostic`).** Appended; waivers already granted stay; open Objectives return to normal learning (`VDW-v0` §3: never mandatory).

Attempts are made with the session's id (`SubmitAttempt`) and recorded by `RecordEvidence` — the ordinary pipeline (`VDW-v0` §2). The store returns each evidence row with the session its attempt was made in.

---

# 6. The waiver (`DiagnosticWaiverEngine.waiver`)

1. A row is **diagnostic evidence** for an Objective only if it was gathered in a diagnostic whose scope holds that Objective, and the fast path had not already ended there (§7).
2. Replay the Objective's evidence in recording order and ask `GRE-v0`'s gates — the mastery engine's own `decide`, with nothing held over — after each row.
3. If the gates **first** pass on a diagnostic row, the Objective is waived: the waiver names that diagnostic, the evidence ids of the window that passed, and the row's sequence and study day. If they first pass on ordinary learning, it was covered by learning: no waiver.

Consequences, each a test: one item never waives (two groups, two families for standard; three and non-basic for critical); same-family repetition and a dependency group count once; help, a seen solution, an unverified or contested evaluation, a missing prerequisite or an indirect type never count; evidence the learner already had counts and the diagnostic gathers only what is missing (§17); a correction that removes the source evidence removes the waiver.

A waiver is **coverage, not competence**: a later miss does not take it back (current competence is `GRE-v0`/`RVR-v0`'s). Its stored values are `active` and `none`; "superseded" is structural (it is pinned to its Objective version, and a new version has its own row) and "invalidated" is the rebuild no longer granting it, reported as a withdrawal (§11).

---

# 7. One Objective of the open diagnostic (`diagnose`)

The learner's answers in this diagnostic are read in order; the first that ends the fast path decides:

| What happened | State | Code |
|---|---|---|
| help taken or a solution seen | `assistance_ended_fast_path` | `diagnostic.h0_required_for_waiver` |
| a clean, independent, verified, direct miss (negative or partial) | `not_demonstrated` | `diagnostic.no_waiver` |
| work on a missing prerequisite, an unmeasurable or contested answer | — ends nothing | — |

Otherwise: waived → `waived`; gates already pass without a waiver → `already_demonstrated` (not tested again); nothing usable known yet → `probe_needed` / `probe`; else `confirm_needed` with `critical_confirm` for a critical Objective, `transfer_confirm` when only transfer is missing, `confirm` otherwise. An answer that could not be settled (provisional, indirect) is a confirm, not a fresh probe.

A clean miss is not a weakness here (§3.1): `WeaknessEvent.diagnosticBaseline`, and `WeaknessEngine.rule` returns no rule for it while the Skill is not mastered. Once ordinary learning reaches the Objective, or after mastery, `WLRM-v0` applies unchanged.

---

# 8. The outcome

`VDW-v0` §10–§11: **full** only when every Objective in scope is waived or already shown, at least one was waived, and every Skill in scope is mastered by the mastery engine; **partial** when some are waived; **no waiver** otherwise — not a penalty. While any Objective is open the result is in progress and "no waiver" is not said. Reasons: `diagnostic.user_requested_fast_path`, then the outcome's code.

---

# 9. The planner

- **Need.** One `diagnostic_opportunity` per Skill with an open Objective: P3 planned progress, never past repair or verification, `decision_value = decisive` because the learner asked (`VDW-v0` §19).
- **Candidates.** For each open Objective, the next item: targets it, daily-eligible, trusted by the store, `h0_required`, mastery-eligible at its criticality (critical needs `critical_mastery_eligible`), declares its minutes, never seen, no family whose solution was shown, not used for another Objective; a confirm asks only for what the gates miss — a new dependency group, a new family, the required direct type, non-basic or transfer evidence. It is an atomic `diagnose` candidate declaring its one Objective. No item → no candidate; the Objective stays open and it is not the learner's failure.
- **Coverage holds.** A lesson (`teach`) that declares its Objectives, all of them covered: all waived → `resolved_before_selection` with `diagnostic.partial_coverage_waiver` (`full_` when every required Objective of its Skill is waived); any still being checked → `conditional_not_selected` with `diagnostic.user_requested_fast_path` — teaching first would make "already knew it" untrue. A lesson that also teaches an Objective not shown is taught; practice is never held; a lesson that declares nothing is never covered. A need whose every lesson is held is `resolved_before_selection` (all waived) or `no_valid_candidate` with the hold's code.
- A diagnostic that waits on a prerequisite records `diagnostic.prerequisite_blocked` beside the gate's code and names the blocker (`VDW-v0` §13).
- **Planning reads no evidence.** Everything above comes from the `diagnostic_coverage` projection, curriculum, content and exposure (12F invariant 17 holds).

---

# 10. The projection (`diagnostic_coverage`, schema v7)

Per Objective, owned by the new `VDW-v0` state family: the waiver (`waiver`, session, source evidence ids, sequence, study day) and the open diagnostic's view (session, state, stage, failed gates, window families and dependency groups, reason codes), with `DDM-v0` provenance. Rebuilt by `RebuildDiagnosticCoverage` — in `RecomputeSkillState` after mastery, retention and weakness, and by a request for its own targets. SQLite refuses a waiver value outside `{none, active}`, an active waiver without its evidence and session, and a state or stage outside the contract. Truth is untouched.

`D-104` extends `DDM-v0`'s projection inventory and `MSBX-v0`'s engine-ownership map by one entry each, owned here; the accepted 9C/9D contracts are not edited.

---

# 11. The program change report

Two kinds join `PCRX-v0`'s eleven: `coverage_waived` (`confirmed_capabilities`, `replan.prerequisite_state_changed`) and `coverage_waiver_withdrawn` (`not_reliably_measured`, `replan.evidence_state_changed`), each naming its Objective. A snapshot reads the coverage of the open diagnostic's Objectives (the only place a waiver can be granted); a row nobody had written is not a before. A waiver makes the replan event `diagnostic_waiver_granted` (12D's own) after remediation and verification. The result names the Objective, never the Skill as learned.

---

# 12. Result and explanation

`DiagnosticResults.summary` — a headline by outcome and one line per Objective, each from its recorded state: only a waived Objective is said to be skipped, a miss returns to normal learning, help is never a penalty, nothing is called learned; no score, percentage or number. `PlannerExplanation` now says what a diagnostic did to a need's lessons, from the trace's own codes, and a need whose lessons wait is not called "no task" (S06, invariant 12).

---

# 13. Port and model refinements

- `PersistencePort.latestAssessmentSessionIn(scope, format)` — the newest session of a scope whose content is in a format; four ports remain.
- `EvidenceRow.assessmentSessionId` (read through the attempt), `TaskCandidate.targetObjectives` (3B §15), `WeaknessEvent.diagnosticBaseline`, `PlannerEngine.plan(coverage)`.

---

# 14. Verification

- Suites: `DiagnosticFactsTest` (model), `DiagnosticWaiverEngineTest` and the S06 case of `VirtualUserScenariosTest` (engine), `DiagnosticsTest` (application, end to end, S06 journey), `DiagnosticPresentationTest` (presentation, S06 explanation), `DiagnosticStorageTest` (real SQLite, v6→v7 on a populated fixture).
- 3H S06 runs against real code at three levels; `PDT-v0` invariant 12 is covered. All sixteen 3H scenarios now run.
- Implementation mutation with only the 13F suites running; validator `tools/validate_diagnostic_waiver.py` with its own mutation test.
- Six runs (T1, T2, T3, T5 ×3), all PASS; 783 JVM tests.
- **Mutation 69/69**, only the 13F suites running; the comment-only control survived. In the first run four mutants survived — two were test gaps (an independent re-check still owed; an extra field on the request line), closed with tests; two were equivalent (a trust filter the ceiling already enforced, now removed; a guard the transitions already implied), replaced by mutants on what matters — and two mutants that broke a null smart cast were rewritten. The whole set was then run again from one unchanged tree. The harness itself was first found running nothing (Python reached WSL bash) and reported the run void rather than passing.
- **Not run: T6.** Nothing in the app offers the fast path or calls the use cases yet (16D).

---

# 15. Open loops

| Loop | Owner |
|---|---|
| Topic `available → mastered` from its Objectives' waivers | 16C |
| fast-path entry in Learn, calling request/withdraw/result and recompute after a session | 16D |
| curriculum entry placement as a diagnostic source | 16D |
| a planner-initiated diagnostic (calibrated decision value) | 18B |
| carrying a waiver across curriculum versions (`KGC-v0` §26 migration records) | 15 |
| authored diagnostic items and lessons declaring their Objectives | 15 |
| waiver replay cost (quadratic in one Objective's rows, only on rebuild) | 18E |
| final wording | 14 |

---

# 16. Anti-patterns explicitly rejected

- "I know this" as evidence, or as a waiver,
- a lower bar for a diagnostic, or one easy item for a Topic,
- a waiver from evidence gathered after the fast path ended,
- a waiver where the gates first passed on ordinary learning,
- a waiver read as mastery or retention, or a Topic called mastered on it,
- an untaught diagnostic miss as a weakness or remediation,
- help in a diagnostic as a penalty,
- teaching an Objective first while the learner's diagnostic is checking it,
- skipping a lesson that also teaches what was not shown,
- a diagnostic past repair or verification, or outside the day's capacity,
- planning that reads evidence history.

---

# 17. 13F acceptance contract

1. `VDWX-v0` is the accepted diagnostic waiver implementation.
2. A waiver is granted only where `GRE-v0`'s own gates first pass on diagnostic evidence, and names it.
3. Self-report never waives; one item never does; help or a clean miss ends the fast path; a missing prerequisite or an unmeasurable answer ends nothing.
4. A partial waiver skips only what was shown; full requires every Skill mastered by the mastery engine.
5. A baseline miss is not a weakness (user decision); `WLRM-v0` is otherwise unchanged.
6. The planner gets one decisive P3 need per Skill, fresh trusted H0 items, and coverage holds for lessons only; planning reads no evidence.
7. Schema v7, one projection and one state family added under `D-104`; one port refinement, four ports.
8. S06 and invariant 12 run against real code.
9. Nothing is claimed that was not run; T6 was not run.
10. Independent 13F QA passes and the full `validate_*.py` sweep passes.

---

# 18. Handoff after acceptance

If accepted, 13F becomes `VDWX-v0 / D-104` and **AŞAMA 13 is complete**.

Next numbered step: **14A — Tutor davranış sözleşmesi**. It must receive a fresh PRE-STEP and explicit user approval before execution.
