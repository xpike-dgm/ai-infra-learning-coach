# Planner Explanation Implementation Specification — RSNX-v0

**Stage step:** 12E — Explanation / reason codes  
**Status:** ACCEPTED — independent 12E QA PASS  
**Decision:** `D-096`  
**Model:** `RSNX-v0 — Reason Codes & Planner Explanation`  
**Explainability:** `PDT-v0` (`docs/PLANNER_EXPLAINABILITY_SPEC.md`)  
**Prerequisite inputs:** `PRG-v0` §20 (`docs/PREREQUISITE_POLICY_SPEC.md`)  
**Today:** `THUX-v0 / D-069`, `TDYX-v0 / D-087`  
**Surface:** `UXIA-v0 / D-068` (`planner_explanation`), `NSHX-v0 / D-083`  
**Planner:** `PLNX-v0 / D-094`, `RPLX-v0 / D-095`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

12E turns the planner's decision trace into what the learner reads, and lets Today read the plan at all.

It answers one primary question:

> **Bu görev neden bugün, diğeri neden gelmedi, bu neden bekliyor — ve plan neden değişti?**

Primary invariant:

> **An explanation is a projection of the decision trace.** Every sentence stands for a reason code the trace recorded or a fact the trace holds in a field. A reason the planner never recorded cannot be constructed, deferred work is never called less important, a waiting task names its real Skill blocker, `review_due` is never forgetting, and absence is never debt.

---

# 2. What was found before writing any code

- **The trace could not name a blocker.** `PDT-v0` §11 and 3H invariant 6 require a waiting task to name its real Skill blocker (`related_refs`, §7). `CandidateTrace` had no field for it; the gate's answer was used to decide and then dropped. Re-running the gate later would explain today's plan with a decision the planner never made, because state may have moved since.
- **Today could not read a plan.** `TodayFactsQuery` held `plan = null` because `planned_task` carries no title, purpose, minutes or reason (11A). 12C put all of that in the trace, but nothing read it back, and `latestPlan()` returned only a count — so a Today row had no stored `planned_task` id to point at.
- **The planner writes a code `PDT-v0` §8 does not list.** `independent_branch_available` is one of `PRG-v0` §20's explainability inputs, un-namespaced. It is legitimate — §20 exists "for 3G's final texts" — but no catalogue said so.
- **11A's reason label would lie once plans reached Today.** `CONTINUE_CURRENT_LEARNING` was worded "Başlanan öğrenmeyi sürdürüyor", but `THUX-v0` §7.1 has no separate family for a brand-new Skill; new learning is planned progress on the route (`PBR-v0` P3), and a row about it must not claim the learner already started.
- **Kept work would be offered again.** After a replan (12D) the plan's first tasks are the ones already started; Today would have shown them as the next thing to start.

---

# 3. Scope boundary

## 3.1 12E decides

- the closed reason-code catalogue and its sources,
- what the trace records so an explanation can name a blocker (`planner_trace/3`),
- how a stored plan is read back and when its trace is trusted,
- which `THUX-v0` §7.1 families a Today row shows, from which trace facts,
- the `planner_explanation` surface's content: why today, why not today, why waiting, why the plan changed,
- the template fallback (`PDT-v0` §3) and what its sentences may claim.

## 3.2 12E does not decide

- the 3H virtual-user scenarios against this code → 12F,
- retention and weakness needs, and the codes only they would write → 13,
- final microcopy and any LLM paraphrase → 14,
- authored tasks → 15,
- calling the planner from the app, and the capacity setting → 16D,
- any accepted planner, priority, prerequisite or capacity rule.

---

# 4. The catalogue

`ReasonCatalog` holds every code in `PDT-v0` §8's ten families, in its order, and `PRG-v0` §20's nine inputs. A code outside it has no family and cannot become a statement. Every code the planner's vocabulary names (need triggers, bands, blocking scopes, capacity sources, replan triggers) is in it, and every code the planner and the replan write in a real plan, replan and re-entry is in it.

---

# 5. The trace names what the gate answered — `planner_trace/3`

`CandidateTrace.relatedSkills` is `PDT-v0` §7's `related_refs`, recorded at planning time:

| Gate answer | Related Skills |
|---|---|
| blocked | the hard blockers, or — when none is missing outright — the prerequisites whose confidence it waits on |
| conditional | the uncertain prerequisites it went ahead with |
| with support | the soft gaps |
| eligible | the prerequisites due for review that did not block |
| never answered | none: the gate fails closed and nothing is invented |

The format is `planner_trace/3`. `/2` and `/1` still decode, strictly: an older trace cannot claim related Skills, and `/1` still cannot claim a kept task, a replan or a re-entry.

---

# 6. Reading the plan back

`PlanReading.read` is the only way Today and the explanation learn what the planner decided.

- **The day is the row's own.** A plan whose `plan_version` belongs to another study day is not today's plan, even if its trace does not decode; only its day is passed on, and Today's existing rule refuses it.
- **The trace is trusted only when it describes these rows:** same study day, same positions, same Skill at each position, no position twice, and every task this version chose has the need decision that chose it. Anything else is unreadable, and nothing is filled in.
- **Each row points at its own stored `planned_task` id.** `latestPlan()` now returns the plan's rows in position order — a refinement of the existing method, not a new port.
- **Minutes are what is planned today**, which is less than the whole estimate only when the task was split.
- **Capacity is the planner's record**: the day's budget as 12D carries it across replans, whether it was today's override, the planned and remaining minutes, whether the plan was recalculated, and "nothing fits" only when the trace records every eligible need deferred for time with nothing new selected — never a comparison made by the reader.
- **An unreadable plan for today is `error_recoverable`**, a system condition wearing the fault tone, with no rows and no capacity. It is not an empty day.

---

# 7. What a Today row says

One primary family and at most one supporting family (`THUX-v0` §7.1), each from a trace fact:

- **primary** — the need's trigger: remediation → repair a confirmed weakness; verification and an unconfirmed weakness → verify an uncertain state (`SPWX-v0`: hypothesis != deficiency); review due → review due knowledge; diagnostic → collect missing evidence; new learning, continuation, reinforcement and integration → continuing on the route; a paused safe checkpoint the planner continued → resume paused work; the parallel track's own cadence on the English track → parallel Technical English;
- **supporting** — the fit the planner had to make (split or smaller alternative), or else the English track as context.

English is a track and never the reason a task exists, except when the need *is* the parallel track's cadence. The label for continuing on the route now says "Öğrenme yolunda ilerliyor", which is true for new and continued learning alike.

**Kept work is part of the plan and is not offered as new work**: it was already started, and continuing a run is the resumable session's path, which revalidates first (11C). A replan the trace records becomes `plan_changed` attention and a waiting need becomes `prerequisite_blocker` attention, both linking to the explanation; neither lists or ranks the waiting work.

---

# 8. The explanation

`PlannerExplanationPresentation.of` builds four parts, each a list of `Statement`s. A `Statement` has a private constructor: it is built only from a code the trace recorded **and** the catalogue defines, or from one of six trace facts that are fields rather than codes (a kept task, a need nothing serves, a need not taken with no recorded cause, a need waiting with no recorded gate answer, a replan whose event has no code, and kept work now over the day's budget).

- **Why today** (`PDT-v0` §9, §21): the need first; then what moved or fitted it — the decisive priority reason, the eligibility it went ahead with and its related Skills, the split or smaller alternative. A band code is rank bookkeeping and is never shown as the reason. A kept task is explained as kept, not justified again.
- **Why not today** (§10, §11): the need, why it did not come, and when it is looked at again. A need deferred for time says so and is never called less important — only a recorded lower-priority code may say that (§18). A waiting need names the Skills its gate answer named. A need nothing serves says exactly that, with no code invented.
- **Why the plan changed** (§14, §15): the replan's trigger code, or only that the plan changed when the event has none; re-entry's codes — absence is not failure and not debt, and yesterday's plan was not replayed; the capacity source; a short-day relaxation; and that independent work went ahead while a branch waited.
- **When it is looked at again** (§21): next plan, when the prerequisite is ready, or when a task is available — never a date.

With no plan, another day's plan or an unreadable one, nothing is explained, and the state says which.

> **12F amendment (`VUSX-v0 / D-097`, 2026-09-30).** Running the 3H virtual users showed a returning learner's seventy-eight due Skills listed as seventy-eight "not today" entries — the backlog `SRR-v0` §9.1 forbids. Needs that did not come for the same recorded reason (same trigger, same why-not, same reconsideration) are now **one entry** that keeps every need key and every Skill; a waiting need whose blockers differ stays apart. The screen names three Skills and counts the rest as labelled inventory. No statement's source changed.

---

# 9. The words

`ExplanationCopy` is `PDT-v0` §3's template fallback: one Turkish sentence for every catalogue code, sentences naming related Skills where §11 asks for them, and words for every trace fact, reconsideration and state. It lives in core for the reason `DayCopy` does (11E): what an explanation claims is the safety-critical part, so it is plain JVM code a check reads.

No sentence says forgetting except to deny it, calls absence failure or debt, words deferral for time as lower importance, promises a date, or carries a score, a percentage or a streak. The wording itself is working microcopy owned by 14; an LLM may later paraphrase it, never replace the code it comes from.

---

# 10. The surface

`planner_explanation` opens from Today through the accepted contextual edge. Its facts are read on the store thread, exactly as Today's are; `app-ui` renders the view and decides nothing. There is no score, no percentage and no ranked list of what waits.

---

# 11. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

**Mutation 52/52, from a single clean run against one source tree** (run in five chunks under the tool's time limit). Every mutant had to compile and be caught by a failing test; a run with no Gradle verdict is refused, and a comment-only control mutant survives. **The harness's first run never ran Gradle** — it called `gradlew.bat` by a relative name `cmd` did not resolve — and its refusal rule counted none of the 52 instead of reporting them as caught. **X46 survived the first real run**: a `/9` header over `/3` content was refused only because `/3` lines carry `related_skills`, which masked the format check. A test now puts `/2` content under `/9`, and the whole set was run again.

**Not run: T6.** Nothing in the app calls the planner yet (16D), so no plan exists on a device to read or explain; the surface has been seen only in tests.

---

# 12. Anti-patterns explicitly rejected

- an explanation reason the trace never recorded, or a free-text reason,
- re-running the gate to explain a plan the planner already decided,
- a band shown as the reason, or a `critical` label as a blocker,
- deferral for time worded as lower importance, or as debt or failure,
- a waiting task explained as "locked" without its Skill blocker when the trace has one,
- `review_due` as forgetting, or absence as failure, debt or falling behind,
- English as the reason a task exists,
- a row guessed around a trace that does not describe the stored plan,
- work already started offered to start again,
- a date promised for a deferred need.

---

# 13. 12E acceptance contract

1. `RSNX-v0` is the accepted explanation model.
2. The catalogue equals `PDT-v0` §8 and `PRG-v0` §20, in their order, and every code written in a real plan, replan and re-entry is in it.
3. The trace records each candidate's related Skills at planning time; `planner_trace/3` reads back exactly and `/2` and `/1` cannot claim what they never had.
4. A stored plan is read only when its trace describes its own rows; otherwise it is unreadable and nothing is guessed.
5. Today reads the plan, each row points at its stored task, capacity is the planner's record, kept work is not offered, and an unreadable plan is a recoverable fault.
6. A row's families are trace facts; English is never the primary reason except for the parallel track's own cadence.
7. Every explanation statement comes from a recorded code or a trace fact; none can be constructed otherwise.
8. Deferral for time is never lower importance; a waiting need names its blocker; `review_due` is never forgetting; absence is never debt.
9. Every catalogue code has a template, and no template breaks §9's rules.
10. The schema is unchanged, no migration was added, and the port count stays four.
11. Nothing is claimed that was not run; T6 was not run.
12. Independent 12E QA passes and Stage 6–12D regressions pass.

---

# 14. Handoff after acceptance

If accepted, 12E becomes `RSNX-v0 / D-096`.

Next numbered step: **12F — Virtual-user tests**. It must receive a fresh PRE-STEP and explicit user approval before execution. Open loops carried: the 3H scenarios run against this code, including explanations (12F); retention and weakness needs and their codes (13); final microcopy and LLM paraphrase (14); authored tasks (15); calling the planner from the app and the capacity setting (16D); starvation thresholds (18C); reverse invalidation and candidate caps (18E); and the T6 device run.

---

**13F note (2026-10-01, `D-104`):** a served need's explanation also says what a diagnostic did to its lessons (`diagnostic.partial_coverage_waiver` / `full_coverage_waiver` / `user_requested_fast_path`, from the trace's own codes), and a need whose lessons all wait for the learner's diagnostic is explained by that code with `next_plan`, never as "no task" (`PDT-v0` invariant 12, S06).
