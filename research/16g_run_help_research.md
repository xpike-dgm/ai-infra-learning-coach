# 16G — Help and judged answers in a run: research synthesis

**Step:** 16G — Uçtan uca günlük döngü: yardım ve ölçüm (`HLPX-v0` / `D-130`; the new step 16H: `D-131`)  
**Question:** how does a run judge code and open answers, offer help, say what a wrong answer said, check understanding of code the learner did not write, and pause — calling only what 11B–14G already decided, with no new rule about the learner?

## 1. Internal contracts (binding)

- **`TRUX-v0` (8C, `docs/DAILY_WORKING_FLOW_SPEC.md`):**
  - **§7.1:** a safe checkpoint needs four conditions — a segment meaningful alone, artifact and runner state persistable honestly, no partly exposed solution, and no half-evaluated independent attempt.
  - **§7.2:** high-stakes work pauses marked and is not continued as independent work.
  - **§7.3:** a paused checkpoint re-enters as a `continue_learning` need.
  - **§8.1:** help is always requestable and escalates on request.
  - **§8.3:** H3/H4 come only after the consequence is disclosed.
  - **§8.5:** help travels with the attempt; post-submit explanation does not contaminate the frozen attempt.
  - **§12.2:** an evaluator that does not answer leaves the attempt `evaluation_pending`, neither passed nor failed.
- **`TUTX-v0` (14A, `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md`):**
  - `TutorRules.prepare` turns an ask into a request, or says what is missing — in the learner's order: what to ask, what is missing, how much it may reveal, what that changes.
  - `TutorContext` is the only content that may leave the device.
  - Help not shown is recorded nowhere.
  - After the answer froze, no level is asked and the record claims H4.
- **`WAAX-v0` (14B):**
  - `AnalyzeWrongAnswer` reads the stored attribution and writes nothing.
  - A hypothesis is only the catalog's question.
- **`ALEX-v0` (14C):**
  - the learner chooses the form, and written explanations come first;
  - the tutor writes only forms it may, grounded in the course's own explanation and labelled unverified;
  - the way back is always one step.
- **`CDEX-v0` (14D):**
  - code is judged only by the course's tests, run on the learner's computer;
  - the report is read strictly;
  - an environment error measures nothing;
  - the evaluator port never returns `verified`.
- **`ACCX-v0` (14E):**
  - after code the learner did not write alone, a comprehension check is offered and may be skipped;
  - written checks first; the tutor's question is practice;
  - a right choice is evidence of the kind its author declared.
- **`OREX-v0` (14F):**
  - a rubric is judged criterion by criterion, provisional at most;
  - a task needing a verified result is never sent to an AI;
  - with no evaluator the answer waits, and is judged again only on request;
  - the self-check is practice.
- **`SESX-v0` (11C):**
  - `PausePolicy.classify` decides durability from the confirmed conditions;
  - only a durable pause shows the saved state;
  - Today offers no checkpoint.
- **`RPLX-v0` (12D):** `ReplanEngine.withPausedWork` makes an open continuation need paused work (P2), never selected automatically.
- **`2D` §6 / `AssistanceInterpretation`:** independence follows from recorded help and provenance; a teaching task is practice by design.
- **`E2EX-v0` (16F):** the run's entry, serving, submission, evidence and end; `moved_to_16g`.

## 2. External grounding

- **Running the learner's code where they write it.** The phone is not an IDE (`LEARNING_BEHAVIOR_RULES` §1). The 14D runner is standard-library Python that prints a strict, byte-identical report on every operating system.
  - Copy-paste moves text with the platform clipboard, so no file or network permission is needed.
  - The Android clipboard (`ClipboardManager` / Compose `Clipboard` with `ClipEntry`) needs no permission to write.
- **Feedback that informs rather than judges.** Formative feedback research (Shute 2008, *Review of Educational Research* 78(1)) favours specific, task-focused feedback over ability-focused verdicts. That is why each Objective's line uses the test's or the rubric's own words, and nothing names a level.
- **Help on demand.** Help-seeking research in intelligent tutors (Aleven et al. 2003, *Review of Educational Research* 73(3)) finds that help must be easy to reach, and that what it reveals must be accounted for. Here help is one action away, and what was shown is recorded where `TRUX-v0` §8.5 says.
- **Explaining code is not writing it.** Lister et al. (2004, ITiCSE working group) separate reading and tracing from writing. That is 14E's rule: comprehension is evidence of its own kind, never of production.

## 3. Synthesis

1. **Every judgement stays the course's.** The run adds no evaluator: an answer key (14F), the tests (14D) or the rubric (14F) decides, and the evidence pipeline records.
2. **A report that measures nothing writes nothing.** This is stricter than an attempt with no evidence: the learner can fix the report and send again, and no attempt claims to have been judged.
3. **An open answer freezes first.** The learner's work is safe even when no evaluator answers, and a later judgement attaches to the same attempt.
4. **Help is ready only when the rules say so.** The screen gathers the level, the acknowledgement and the question, but `TutorRules.prepare` decides when to send.
5. **A pause is a fact about the boundary, not about the learner.** `PausePolicy` decides; the run only says what the boundary confirms.

## 4. Verification plan

- Course tests on the real store, with all eight packages and the shipped suites:
  - suites served exactly;
  - every Skill runnable;
  - code judged from a report, and nothing written for a report that measures nothing;
  - an open answer pending, then judged on request;
  - after-answer help recorded on its attempt;
  - a pause checkpoint.
- Unit tests for the ask builder, the help panels, the answered and paused stages, and the suite reader's guards.
- Kotlin and validator mutation with negative controls; the full sweep; both APKs and their assets.
