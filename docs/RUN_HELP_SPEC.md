# Help and Judged Answers in a Run Specification — HLPX-v0

**Stage step:** 16G — Uçtan uca günlük döngü: yardım ve ölçüm  
**Status:** ACCEPTED — independent 16G QA PASS  
**Decision:** `D-130` (the new step 16H: `D-131`)  
**Model:** `HLPX-v0 — help and judged answers inside a planned task run: code judged by the course's tests from a pasted report, open answers by their rubric, the tutor, another explanation, what a wrong answer said, the comprehension check and a durable pause`  
**Behaviour it implements:** `TRUX-v0` §7–§8, §12; `TUTX-v0` (14A); `WAAX-v0` (14B); `ALEX-v0` (14C); `CDEX-v0` (14D); `ACCX-v0` (14E); `OREX-v0` (14F); `SESX-v0` (11C); `E2EX-v0`'s `moved_to_16g`; D-129  
**Code:** `core-ports` (`codeSuiteText`, `codeRunnerText`), `data-curriculum` (`FileContentSource` suite and runner readers), `core-model` (`RunItemKind`, `RunAnswer`, `AnsweredItem.pending`), `core-application` (`RunTask.kt` — `RunItems`, `answerCode`, `answerOpen`, `reevaluate`, `selfCheck`; `RunHelp.kt`), `core-presentation` (`TaskRunPresentation.kt`, `RunHelpPresentation.kt`), `app-ui` (`TaskRunnerScreen.kt`), `app-wiring` (`TaskRunFlow`, `TaskRunSession`, `CoachApplication`, `MainActivity`; assets `code_suites/`, `code_test_runner.py`), `tools/ship_code_suites.py`  
**Persistence / schema:** none — schema v11 unchanged

## 1. Purpose

> **Bir görevin içinde kodumu dersin testleriyle, açık cevabımı rubric'iyle değerlendirtebilir, takıldığımda yardım isteyebilir, yanlış cevabımın ne söylediğini görebilir, yazmadığım kodu anladığımı gösterebilir ve güvenli bir noktada duraklatabilir miyim — hiçbir şeyin benim hakkımda karar vermediği, gösterilmeyen hiçbir yardımın kaydedilmediği ve ölçmeyen hiçbir raporun yazılmadığı biçimde?**

Primary invariant:

> **Inside a run, every judgement stays the course's and every rule stays an earlier step's.**
>
> **Code is judged only by the course's own tests.** The learner runs them on their computer from the suite and the runner the app gives them to copy. A report that cannot be read, is for another item or suite, or reports an environment error measures nothing, and nothing is written.
>
> **An open answer freezes first and is judged by its rubric**, criterion by criterion and provisional at most. With no evaluator it waits, is never wrong, and is judged again only when the learner asks.
>
> **Help is always one action away.** Nothing is sent until the rules say the ask is ready. Help shown while the answer is open goes with the attempt; help shown after it froze is recorded on that attempt; help never shown is recorded nowhere.
>
> **A wrong answer is read from the attribution the engines already made.** Code the learner did not write alone is followed by the comprehension check. A pause is saved only at a boundary the pause policy confirms, as one checkpoint, and a high-stakes pause is marked.

---

# 2. What was found

- **The user split the rest of the loop again (D-131).**
  - 16F's `moved_to_16g` held both help inside a run and measurement from Today.
  - The user kept everything inside the run here and moved measurement to a new step, **16H**, added like D-099 with no renumbering: daily, weekly and monthly sessions from Today, slots carrying the session id, the fast path, dispositions and `assessment_opportunity`.
- **Every Skill now has a task the app can run.**
  - With answer keys alone (16F), 36 of 60 Skills had a runnable task.
  - With the shipped suites, all 60 do. The 24 that were left out are the Python and C coding Skills.
  - Of the 1112 shipped items: 618 are judged by an answer key, 480 by the course's tests and 14 by a rubric. None is left unjudged.
- **No shipped package authors a written comprehension check.**
  - 14E built the written path, and no package uses it yet.
  - After code the learner did not write alone, what follows is the tutor's practice question, which is never evidence.
  - The written path is exercised by a test with one authored check, as a later package would add it.
- **All fourteen rubric items allow a provisional result.** No shipped open item needs a verified result, so every one can be judged by the evaluator. One that did would not be served (tested with a changed reading).
- **A pause resumes only where the need is `continue_learning`.**
  - `ReplanEngine.withPausedWork` (12D) makes an open continuation need paused work (P2).
  - A teaching task serves `new_learning`, so its checkpoint records where the learner stopped, but it does not raise the need. The task is offered again because the need is still open.
  - Nothing here loosens that rule.
- **The 16F contract had a list item that parsed as a mapping.** `code items: …` in `moved_to_16g` was read as a one-key map. It was reworded, and every item is a string.
- **Earlier checks pinned what 16G changes.** 16F's `A16F-03_answer_key_only` and `A16F-04_judged_by_the_key_only`, and 15B's `E15B-10_app_reads_later_packages` (it pinned the content source call that now also takes the suite readers; found by the sweep), were narrowed and marked "Narrowed at 16G".

---

# 3. Decisions

User decisions (2026-10-26):

- **Scope:** everything inside the task run:
  - code report import;
  - open-response AI evaluation and the self-check;
  - the tutor panel and the alternative explanation;
  - the comprehension check and the wrong-answer analysis;
  - resuming a started task.

  Measurement from Today goes to a new step **16H** (D-131).
- **Code transport:** copy-paste. The app gives the suite text and the runner with Copy buttons; the learner runs them on the PC and pastes the report back. No network or file permission is added.

Assistant defaults:

- **The suites ship as authored.**
  - `tools/ship_code_suites.py` copies the 480 `code_test_suite/1` files byte for byte, named `<item logical id>-v<N>.json`, and the 14D runner as `code_test_runner.py`.
  - `--check` holds the shipped set equal to the sources.
- **A suite is served only for exactly its item version and suite version, in its format.** A code item without its shipped suite is not served at all.
- **An item is servable when the app can judge it:** an answer key, the course's tests with a shipped suite, or a rubric whose task allows a provisional result. It must still be usable by 11D's rule.
- **The pasted report is read and not stored.** The code is the attempt's artifact, and the evidence names the suite version.
- **An open answer freezes first, then the evaluator is asked.**
  - No answer leaves the attempt `evaluation_pending` with no evidence.
  - Asking again judges the same attempt with the independence recorded with it.
- **Help runs on the store thread.** What the tutor showed and what the evaluator said are written in the same job, and the store has one writer.
- **The asks offered:**
  - while the answer is open: a hint, the concept another way, a question;
  - once it froze: explain my answer, the concept another way, a question.
  - A gloss is not offered. Which segment is the learning target is task metadata (`TEIP-v0` §4), no task declares it, and guessing it is forbidden.
- **The instruction mode is `turkish_primary`.** Every shipped task is Turkish-primary and none declares another; the tutor never picks a language.
- **After the answer froze:** the timing is `after_failure` when the answer did not fully match and `after_submit` otherwise. No level is asked (14A).
- **Help shown after the answer froze** is appended as an `assistance_event` on that attempt, in the shape `SubmitAttempt` writes, and changes nothing the frozen answer proved (`TRUX-v0` §8.5).
- **The comprehension check.** A written check answered alone is independent, except in a teaching task, which is practice by design (`AssistanceInterpretation`).
- **The wrong-answer summary** appears after a not-right or partly-right answer that wrote evidence. It is read from the stored attribution and writes nothing.
- **A pause is offered between questions only.**
  - The boundary confirms the four safe-checkpoint conditions, unless a help reply is still on its way or an evaluation is pending.
  - A measuring purpose (`assess`, `retain`, `diagnose`) pauses as `high_stakes_pause`.
  - The checkpoint names the shown and remaining items, and the run then ends the way every run ends.
- Working Turkish microcopy; 17 owns the polish.

---

# 4. Code: the course's tests, run on the learner's computer (`RunItems`, `answerCode`)

1. The question shows how to run the tests: open a folder, save the runner once, save this item's suite under its own name, write the code under the file name the task gives, run `python code_test_runner.py <suite>`, and paste the code and the whole report.
2. **Copy buttons** put the runner and the suite on the system clipboard. Nothing else leaves the app, and no permission is added.
3. `answerCode`:
   - a blank code field is a skip;
   - a body too long to carry inline is refused;
   - `EvaluateCode` (14D) reads the report strictly.
4. **A report that measures nothing writes nothing:** missing, malformed, for another item, for another suite, not matching the suite, an environment error. The learner is told why, in 14D's own words, and can send again.
5. A report the suite can judge freezes the attempt — the code is its artifact — and its result goes to the evidence pipeline as `verified`, each test for its own Objective.
6. The result is said per Objective, with 14D's sentence that passing tests is not understanding. There is no accepted answer to reveal.

---

# 5. Open answers: the rubric, provisional at most (`answerOpen`, `reevaluate`, `selfCheck`)

1. Only items whose task allows a provisional result are served, and only when an evaluator is available. No AI is asked for a task that needs a verified result.
2. The answer freezes first (one action). Then the evaluator judges it criterion by criterion (14F); core decides what that means for each Objective.
3. **No evaluator answer:** the attempt stays `evaluation_pending` with no evidence. The runner shows the pending state, 14F's sentence that nothing retries on its own, and *Yeniden değerlendirilsin*.
4. **Asking again** judges the same attempt. Evidence is written for that attempt, with the independence its help and provenance gave it.
5. **The self-check** shows the rubric as practice, never evidence, and records it as the solution exposure it is.

---

# 6. Help inside the run (`RunHelp`, `RunHelpPresentation`)

1. **One action away.** *Yardım iste* opens the asks that fit the moment.
2. **The rules decide when an ask is ready.** `TutorRules.prepare` (14A) runs first, so nothing is sent while something is missing:
   - a level to choose while the answer is open;
   - the consequence of H3/H4 said first;
   - the learner's question;
   - a redirect to the ask that fits.
3. **What may leave the device** is only `TutorContext` (14A): the question, its Objectives, the frozen answer once it froze, and the course's own explanation of those Objectives.
4. **Another explanation** is 14C's menu. The learner chooses a form; written comes first, and the tutor writes only where it may. Nothing true to show is said plainly.
5. **What came back** is shown with where it came from and what it changed. A tutor that cannot answer is said plainly, is never a fault, and records nothing.
6. **Where help is recorded:**
   - Help shown while the answer is open travels with the attempt when it is submitted.
   - After the answer froze, it is appended on that attempt.

---

# 7. What a wrong answer said (`RunHelp.wrongAnswer`)

After a not-right or partly-right answer that wrote evidence, 14B's `AnalyzeWrongAnswer` reads the attribution the engines already made, and 14B's summary is shown:

- a hypothesis only as the catalog's own question;
- a misconception named only when the engine supports it.

It writes nothing.

---

# 8. The comprehension check (`Comprehension.offer`, `RunHelp.comprehensionAnswer`, `RunHelp.practice`)

1. After a code answer the learner did not write alone, by their provenance answer or by the help they were shown (14E), the check is offered and may be skipped.
2. **Written checks come first,** one at a time:
   - a right choice is evidence of the kind the check declares, for its own Objective, on the code's attempt;
   - a skip writes nothing.
3. **With none written** — as with every shipped package today — the tutor may ask one question about the learner's code and respond to their answer. That is practice, labelled so, and never evidence.

---

# 9. A durable pause (`PausePolicy`, `RunHelp.pause`)

1. *Duraklat* is offered between questions only.
2. **The boundary confirms** the four safe-checkpoint conditions (11C): the question is complete on its own, every artifact is frozen in the store, nothing is half shown, and no attempt waits for its evaluation.
   - A help reply still on its way, or a pending evaluation, leaves the pause not durable: nothing is written, and the learner is told.
3. **A measuring purpose pauses marked** (`high_stakes_pause`): it is not continued as the same independent attempt.
4. **A durable pause writes one checkpoint** naming the shown and remaining items, then ends the run the way every run ends (16F): what was recorded is processed once.
5. The runner shows 11C's saved state.
6. **Coming back is the planner's.** The checkpoint makes an open `continue_learning` need paused work at the next plan (12D). Today offers no checkpoint (11C).

---

# 10. What moved to 16H (D-131)

- Daily, weekly and monthly assessment sessions started from Today, and slots carrying the session id.
- The fast path's entry and its use cases; entry placement as a diagnostic source.
- Dispositions recorded from the app, and the disposition reason in traces.
- `assessment_opportunity`.

---

# 11. Verification

Recorded in `arch/16g_run_help/run_help.yaml`. Highlights:

- **The real store and course:**
  - the course test runs every 16G path on the real SQLite store with all eight packages and the shipped suites;
  - every shipped suite is served for exactly its item version, and every Skill has a runnable task;
  - a code answer is judged from the report, and a report that measures nothing writes nothing;
  - an open answer waits and is judged again on request;
  - help after an answer froze is recorded on that attempt, and help never shown nowhere;
  - a pause writes one checkpoint.
- **Mutation:** Kotlin and validator mutation, each with a negative control.
- **Builds and tests:** the full JVM suite, `verifyModuleBoundaries`, both APKs, and the shipped assets read back from the APK.
- **Not run:**
  - T6 (the device tier);
  - a live provider call.

---

# 12. Acceptance criteria

1. A code item is served only with its shipped suite. A report that measures nothing writes nothing; a judged report freezes the attempt with the code as its artifact and writes verified evidence.
2. An open answer freezes first. No evaluator answer leaves it pending with no evidence; asking again judges the same attempt, provisional at most.
3. Nothing is sent to the tutor before `TutorRules.prepare` is ready. Help shown is recorded once, where `TRUX-v0` §8.5 says; help not shown is recorded nowhere.
4. The wrong-answer summary and the comprehension check are 14B's and 14E's, and write only what those steps write.
5. A pause is durable only when `PausePolicy` confirms it, writes one checkpoint, and marks high-stakes work.
6. No network or file permission is added; the shipped suites and runner equal their sources.
7. Independent 16G QA passes and the full `validate_*.py` sweep passes.

---

# 13. Open loops

- Measurement from Today (16H, D-131).
- Written comprehension checks in a package (20I, continuous curriculum QA).
- Run polish (17).
- The run, the Copy buttons and the tutor on a device, and T6 (19).

Next numbered step: `16H — Uçtan uca günlük döngü: ölçme oturumları`. It needs a fresh PRE and the user's new explicit approval.
