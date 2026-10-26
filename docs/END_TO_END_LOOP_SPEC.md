# End-to-End Daily Loop Specification — E2EX-v0

**Stage step:** 16F — Uçtan uca günlük döngü  
**Status:** ACCEPTED — independent 16F QA PASS  
**Decision:** `D-128` (the new step 16G: `D-129`)  
**Model:** `E2EX-v0 — a planned task run from Today to its end: entry confirmed, lesson shown, questions served, answers judged by their key, evidence written, and the plan replanned only when state changed`  
**Behaviour it implements:** `TRUX-v0` §5–§10; `RNRX-v0`; `DMAX-v0` (items and exposure); `OREX-v0` (answer keys); `2D` §6 (independence); `RPLX-v0` (kept work); `PCRX-v0` (the report); `RMDX-v0`'s open loop; D-125  
**Code:** `core-model` (`TaskRunFacts.kt`; `AttemptSubmission.plannedTaskId`; `PlannedTaskFact.started`; `AuthoredTask.refOfCandidate`), `core-ports` (`startedPlannedTasks`, `authoredTask`), `data-persistence` (schema v11 `attempt.planned_task_id`), `data-curriculum` (`authoredTask`), `core-application` (`RunTask.kt` — `OpenTask`, `RunTask`, `StartedTasks`; `BuildDailyPlan.openNeeds`; `ServeDailyMicroItem.usable`; `ReportProgramChanges` and `PlanToday` keep started work; `TodayFactsQuery`; `ReminderCheck`), `core-presentation` (`TaskRunPresentation.kt`, `TaskRunStage`), `app-ui` (`TaskRunnerScreen.kt`), `app-wiring` (`TaskRunSession`, `CoachApplication`, `MainActivity`)  
**Persistence / schema:** schema v11 adds `attempt.planned_task_id`

## 1. Purpose

> **Bugünün planındaki bir görevi başlatıp sonuna kadar yapabilir miyim — girişi gerçekten doğrulanmış, dersi gösterilmiş, soruları sunulmuş, cevapları dersin anahtarıyla değerlendirilmiş, kanıtı yazılmış ve planı yalnız durum değiştiyse, başladığım işi koruyarak yenilenmiş biçimde?**

Primary invariant:

> **A planned task runs only when every entry condition is confirmed** from the store, the gate, the open needs and the authored task. Nothing is assumed, and what cannot be confirmed is named, never blamed.
>
> **The runner stays an execution surface.** It shows the course's own lesson and serves the task's unseen questions. It records each frozen answer, with its help, its provenance and its planned task, as one learner action. It has the answer judged by the course's answer key and hands it to the evidence pipeline.
>
> **An answer is right, partly, not right or not measured.** Seeing the answer is a step the learner chooses.
>
> **At the end, the report says only what canonical state did.** Only a change asks the planner for a new plan, and that plan keeps every task the learner started.

---

# 2. What was found

- **The user split the loop again (D-129).**
  - 16D's `moved_to_16f` held the whole rest of the loop.
  - The user kept the core loop here and moved the rest to a new step, **16G**, added like D-099 with no renumbering: the tutor panel, the alternative explanation, the comprehension check, the wrong-answer analysis, code reports, open-response AI, the fast path, assessment sessions and dispositions.
- **Twenty-four of sixty Skills have only code-test tasks.**
  - 618 of 1112 items have an answer key, 480 have code tests and 14 have a rubric.
  - 36 Skills' tasks hold an answer-key item; 24 (Python and C coding) hold code tests only.
  - Until 16G imports the PC runner's report, those tasks enter with *local capability unavailable*, said in plain words.
- **A teaching task holds one lesson question.** A teach run is the lesson and that question. Its answer is practice by design (`2D` §6), and it covers the Objective (16C).
- **The replan after evidence assumed nothing was started.** 13E's `ReportProgramChanges` and 16D's capacity replan both passed no kept positions. Both now keep the started tasks the store reports.
- **The store could not say which task was started.** `attempt` had no link to `planned_task` (11B §5.3). Schema v11 adds `planned_task_id`.
- **A new learner's first run reports no change, and that is 13E's rule.**
  - A state no engine had written is not a "before": the report claims nothing about it, and with no claimed change nothing is replanned.
  - The run's evidence is recorded, and the recompute writes the Skill's state, so the next run (or the next day's plan) reads it.
- **Two earlier checks misread new code, and the code was changed instead of the checks:**
  - 16D's restore check strips block comments. The restore picker's `"*/*"` opened one that a new comment closed, swallowing `app.confirmRestore`.
  - 11C's session check reads the first `onStart = {`, and a run action of that name came first. It is now `onBegin`.
- **Earlier gates pinned what 16F changes:**
  - 11B's entry from Today alone; 13E's replan without kept work; 16D's capacity replan without kept work.
  - 13F's and 14B's "truth untouched", read to the end of the schema.
  - 16E's schema version, silence order, work-today rule and content signature; 16E's two tests.
  - Each was narrowed and marked "Narrowed at 16F".

---

# 3. Decisions

User decisions (2026-10-26):

- **Scope:** the core loop only — start from Today with real entry checks, the lesson, a question, the answer, answer-key evaluation, evidence, recompute and the change report, a replan that keeps started work, Today refreshed, the reminder's rule updated. The rest goes to 16G (D-129).
- **Feedback:** after submitting, only right or wrong. Seeing the answer is a separate, optional step, recorded as H4.

Assistant defaults:

- **The link:** a nullable `attempt.planned_task_id` (schema v11).
- **Started:** a task with an attempt in today's plan version, or one an earlier version of today kept. Today does not offer it, and every replan keeps it.
- **Local capability:** an `assess` task needs the assessment session (16G), and a task needs an unseen item an answer key can judge.
- **Items:** the CHECKPOINT intent, unseen, in the task's authored order, never repeated. The run ends when none is left.
- **Provenance:** asked once at orientation, with nothing preselected, and recorded on every answer of the run.
- **Lesson help:** seeing the course's own explanation again during a question is H2, recorded with the answer.
- **The answer before answering:** H4 after the consequence is said. No attempt and no evidence; the solution exposure is recorded.
- **The answer after answering:** H4 `after_submit` and a solution exposure. The frozen answer's evidence stays as written (`TRUX-v0` §6.4).
- **Processing:** once, when the run ends — finished, nothing left, or left early. With nothing answered, nothing is recomputed.
- **The reminder:** "every task in today's plan started" replaces "an attempt recorded today" (16E's open loop). Its silences are unchanged.

---

# 4. Entry

| Condition (`TRUX-v0` §6.1) | Confirmed from |
|---|---|
| planned task still selected | today's readable plan holds the planned task, and it is not started |
| hard prerequisites satisfied | the gate (`ResolvePrerequisites`) does not make the task wait |
| learning need still open | the task's need is among `BuildDailyPlan.openNeeds()` |
| content version compatible | the authored task version is readable and its Skill is published |
| required local capability available | not an `assess` task, and an unseen answer-key item is usable |

- **Unmet is not failed.** The runner names what it could not confirm, in plain words, and says it is neither an error nor a failure. When capability is the gap, the screen says why: the assessment session is not here yet; the questions are code or long answers; every judgeable question was already seen.
- **Entry writes no truth.** The program's state is captured at entry, so the end can report what changed.

---

# 5. The run

1. **Orientation:** the title, the purpose in learner words, the plan's estimate as context, the assistance policy, and — for a teaching task — that its answers are practice. Then the provenance question, with nothing chosen.
2. **Lesson:** the course's own explanation of the task's Objectives, when there is one.
3. **Question:**
   - The next unseen item, recorded as seen when shown. A blank answer is a skip: nothing is written.
   - Help is always there: the lesson again (H2), or the answer (H4 after the consequence is said).
4. **Answer:**
   - The attempt, with its artifact, provenance, help and planned task, is one transaction.
   - The answer key judges it, and the evidence pipeline writes one row per Objective with the independence the recorded help and provenance give it.
   - The screen says only: right, partly right, did not match the accepted answer, or not measurable (which is not a wrong answer).
5. **The answer, if asked:** the accepted answer, and that this question is no longer a fresh measurement — a later unseen one will confirm the capability.
6. **Next or finish:** the next unseen item, or the end.

---

# 6. The end

- **What happens:** the touched Skills are recomputed and the program change report (13E) is read.
  - A plan is replanned only when canonical state changed, and the replan keeps every started task.
  - With nothing answered, nothing is recomputed: "Bu görevde bir cevap kaydedilmedi; hiçbir şey değişmedi."
- **What is shown:** only the report's own sentences — what was confirmed, what opened, what the plan added or dropped — or that nothing changed. Never a score, a level or a mastery claim the engines did not write.
- **After the end:** Today is read again, the started task is not offered, and the next task is the planner's current selection.
- **Leaving early** costs nothing. Whatever was recorded is processed once.

---

# 7. The reminder

16E's open loop is closed:

- **Content:** a reminder counts only the tasks in today's plan the learner has not started.
- **Silence:** a day whose tasks are all started is silent (`work_recorded_today`).
- **Read failure:** a read of the started tasks that fails is silent (`day_unreadable`), never "none started".

---

# 8. Not done here

| Loop | Owner |
|---|---|
| Tutor panel, alternative explanation, comprehension check, wrong-answer analysis | 16G |
| Code items (the PC runner's report), open responses through the evaluator, the self-check | 16G |
| The fast path; weekly, monthly and daily assessment sessions from Today; slots' session ids; dispositions; `assessment_opportunity` | 16G |
| Resuming a started task from a checkpoint | 16G |
| Run polish | 17 |
| The run on a device | 19 |

**Not run: T6.** The run has not been seen on a device; its decisions and writes were run on the JVM through the same classes, on the real store with all eight packages.

**Next step:** **16G — Uçtan uca günlük döngü: yardım ve ölçüm**. It needs a fresh PRE and the user's new explicit approval.
