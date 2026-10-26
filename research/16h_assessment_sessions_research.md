# 16H — Measurement sessions from Today: research synthesis

**Step:** 16H — Uçtan uca günlük döngü: ölçme oturumları (`ASRX-v0` / `D-132`)  
**Question:** how does a measurement the planner chose for today — a daily assessment, a blueprint slot, a fast-path check — run in the one assessment interior, carry its session, let the learner skip or report a question, and open the fast path, without a score, a verdict or a second planner?

## 1. Internal contracts (binding)

- **`ASUX-v0` (8D, `docs/ASSESSMENT_SESSION_UX_SPEC.md`):**
  - one interior for every scope;
  - the atomic evidence boundary is the unit of submission;
  - submission freezes;
  - skipping is not incorrect (§7.3);
  - a contested item's evidence is held, never counted against the learner (§10);
  - help is non-punitive, and H3/H4 converts to learning (§8);
  - the result is six semantic families with `not_reliably_measured` first class, and no score, grade or pass/fail (§13);
  - independence and allowed tools are said before any work (§6).
- **`DMAX-v0` (11D):**
  - the store decides an item's trust;
  - the effective use ceiling is the most restrictive rule;
  - exposure is recorded only when an item is shown;
  - a daily session is one `assessment_session` row of scope `daily`.
- **`WBAX-v0` / `MCAX-v0` (13A, 13B):**
  - a cycle's blueprint is composed once;
  - slots are the planner's candidates for needs it already opened;
  - one task per need;
  - a slot's evidence is recorded with what the session already showed (`RecordSlotEvidence`, `WBA-v0` §25, `MCA-v0` §20);
  - an earlier cycle's slot is not debt.
- **`VDWX-v0` (13F):**
  - only the learner opens a diagnostic, and "I know this" only names the scope;
  - one diagnostic is open at a time;
  - withdrawing owes nothing and waivers stay;
  - checks are the planner's candidates `diagnostic:<session>:<objective>:<item>`;
  - the recompute rebuilds coverage.
- **`WLRX-v0` (13D):** a correction is an appended `evidence_disposition`, never an edit, and the store applies the newest one for every engine. `DDM-v0`'s deciders include `user_report`.
- **`SPWX-v0` §7.2:** `assessment_opportunity` is one of the attention groups — a current need, never a priority.
- **`PDT-v0` §15 / `RPLX-v0` (12D):**
  - the replan triggers are a closed list;
  - a replan keeps started work and re-resolves only the rest;
  - the trigger's reason code is one of `PDT-v0` §8's.
- **`E2EX-v0` / `HLPX-v0` (16F, 16G):** the run's entry, serving, submission, evidence, help and end.

## 2. External grounding

- **Leaving a question unanswered is not an error.** Measurement practice separates *omitted* from *incorrect* responses (e.g., the *Standards for Educational and Psychological Testing*, AERA/APA/NCME 2014, on scoring rules). Here that is structural: a skip writes nothing.
- **Item reports from test takers are part of quality control.** Large-scale testing programmes keep a channel for flagging flawed items and re-scoring after review (ITC Guidelines on Quality Control in Scoring, Test Analysis and Reporting, 2014). Here the report holds the evidence as contested; the review is 18D's calibration.
- **Formative assessment feedback is task-focused.** Black & Wiliam (1998, *Assessment in Education* 5(1)) and Shute (2008) favour information about the work over grades. The result lists what changed and what could not be measured; it never gives a mark.

## 3. Synthesis

1. **One interior, three origins.** The kind of measurement is read from the planned candidate. Entry confirms what that origin's store says — the stored blueprint, the open diagnostic, the authored task — and guesses nothing.
2. **The session is a fact on every attempt.** It is what lets 13A's contamination, 13F's waiver and 16B's report read the session later, without keeping it in memory.
3. **A report is a correction, not a verdict.** The row stays; a disposition says it is held.
4. **The fast path is the learner's own event.** Its replan is a declared trigger reasoned with `PDT-v0`'s own fast-path code, so the plan can explain itself.

## 4. Verification plan

- Course tests on the real store:
  - slot entry, session id and slot evidence;
  - a daily session opened at begin, and a report held as contested and named in the history;
  - the fast path replanning today, keeping started work, its check in its own session, and a withdrawal.
- Unit tests for the measurement model, the result, the orientation and the Progress opportunity.
- Kotlin and validator mutation with negative controls; the full sweep; both APKs.
