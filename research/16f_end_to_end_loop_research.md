# 16F — End-to-end daily loop: research synthesis

**Step:** 16F — Uçtan uca günlük döngü (`E2EX-v0` / `D-128`; the new step 16G: `D-129`)  
**Question:** how does a planned task go from Today's start action to its end — entered honestly, taught, asked, answered, judged, recorded and reflected in the plan — without the runner becoming a planner, an evaluator or a judge of the learner?

## 1. Internal contracts (binding)

- **`TRUX-v0` (8C, `docs/DAILY_WORKING_FLOW_SPEC.md`):**
  - **§6.1, entry:** five conditions are revalidated before anything starts — still selected, hard prerequisites, the need still open, content version, local capability. A failed entry is never negative evidence.
  - **§6.2, orientation:** what, why, how long as planning context, and the assistance policy before any independent work.
  - **§6.4, submission:** submitting freezes the attempt. Post-submit explanation does not contaminate it, and a same-item retry after exposure is not independent evidence.
  - **§6.5, resolve:** correctness feedback is allowed, a mastery or level claim is not. The runner hands off `planned_task_ref`.
  - **§6.6, transition:** the next task is the planner's recomputed selection, never a cached list.
  - **§8, assistance:** help is always requestable and escalates on request. H3/H4 come only after the consequence is disclosed. No auto-reveal on a first error.
  - **§9, provenance:** asked, not inferred; cheap; never framed as a confession.
  - **§10.1:** an in-flight run survives a replan.
  - **§5:** an `assess` task's interior is the assessment session's.
- **`RNRX-v0` (11B):**
  - entry must be confirmed from engine facts, never loosened;
  - `SubmitAttempt` is one learner action in one transaction, and writes no evidence;
  - §5.3 left `planned_task_ref` on the attempt to the step that runs a planned task.
- **`DMAX-v0` (11D):**
  - the store decides an item's trust;
  - the effective use ceiling is the most restrictive rule;
  - exposure is recorded only when an item is shown;
  - a short response lives inline as a `data:` URI of at most 4096 characters.
- **`OREX-v0` (14F):** a short answer is judged by the course's accepted answers. The result is verified, for the key's own Objective only.
- **`2D` §6 / `AssistanceInterpretation` (14A):**
  - independence is derived from the recorded help and the provenance answer;
  - a teaching task is practice by design;
  - help after the answer froze does not reach back into it.
- **`RPLX-v0` (12D):**
  - a replan keeps the positions the caller reports as started, and checks them against the previous plan;
  - the store could not tell which tasks were started.
- **`PCRX-v0` (13E):**
  - the touched Skills are recomputed;
  - a change is reported only when an axis really moved;
  - only a change asks for a replan.
  - Its replan assumed nothing was started.
- **`RMDX-v0` (16E):** its open loop says that once attempts link to planned tasks, "work recorded today" can become "every task started".
- **D-125:** 16F receives 16D's `moved_to_16f`.

## 2. External sources

None needed. 16F wires accepted core parts together; no new platform behaviour or third-party fact is relied on.

## 3. Synthesis

- **The link has to be stored.**
  - A replan must know which tasks were started, and the store could not tell. Today would also offer started work again.
  - `TRUX-v0` §6.5 already names the handoff field, and 11B named this step as its owner.
  - A nullable column on `attempt` adds and rewrites nothing.
- **Local capability is the honest place for what the app cannot run yet.**
  - Code items need the PC runner's report, rubric items need an evaluator, and an `assess` task needs the assessment session; all three are 16G.
  - Naming that at entry keeps the planner unchanged (its rule is not the app's capability) and blames no one.
- **A run is a sequence of unseen questions, not a quota.** The task's authored pool, served in order and never repeated, ends when nothing fresh is left. No step count, duration or score.
- **The reveal follows `TRUX-v0` as written.**
  - **After submission:** the frozen answer keeps its evidence (§6.4); the exposure makes the item and its family stale for later measurement (`LFPS-v0`).
  - **Before an answer:** the question becomes learning with nothing measured (§8.6).
- **Processing once, at the end.** Evidence is written as each answer is recorded, so nothing is lost. The recompute, the report and the replan run once, when the run ends — finished, exhausted or left — so the plan changes at most once per run.
