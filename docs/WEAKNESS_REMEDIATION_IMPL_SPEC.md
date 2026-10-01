# Weakness & Remediation Implementation Specification — WLRX-v0

**Stage step:** 13D — Remediation Engine  
**Status:** ACCEPTED — independent 13D QA PASS  
**Decision:** `D-102`  
**Model:** `WLRX-v0 — Weakness Localization & Remediation Implementation`  
**Weakness semantics:** `WLRM-v0 / D-061` (dataset `curriculum/decomposition/6g_weakness_remediation/`)  
**Engines it reads and feeds:** `MSTX-v0 / D-092` (mastery decisions), `PRQX-v0 / D-093` (readiness), `PLNX-v0 / D-094` (needs), `RVRX-v0 / D-101` (shared mastery timeline), `WBAX-v0 / D-098`, `MCAX-v0 / D-100` (pools, retroactive contamination)  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

13D turns `WLRM-v0` into code: each evidence row is attributed to its Objective by `WLRM-v0`'s failure-attribution rules, an Objective carries a localized weakness signal, a Skill's weakness is only what its Objectives show, and a remediation is closed by new evidence — never by finishing a task.

It answers one primary question:

> **Bu kanıt gerçekte hangi Objective hakkında bir şey söylüyor, ne kadar emin — ve bir zayıflık ne zaman gerçekten kapanır?**

Primary invariant:

> **A failed attempt is not a failed Skill.** An attempt nobody can attribute is not the learner's, help taken is a hypothesis at most, a contradiction after mastery opens verification before anything is erased, a remediation is confirmed only when the mastery engine's gates stop passing, and it is closed only when fresh independent evidence makes them pass again.

---

# 2. What was found before writing weakness code

- **Nothing had ever written the weakness axis.** `skill_state.weakness_axis_state` was `not_yet_evaluated` everywhere; the prerequisite gate and the planner both read `remediation_required` from an axis no engine produced.
- **`weakness_state` existed with one column.** `DDM-v0` names it "per Objective"; 10D gave it only `state`.
- **`weakness_detected` had no supplier.** 12C lists it as **owner-supplied**, and 13A/13B's pools route it to `weakness_or_verification` / `persistent_weakness_or_verification` — but nothing supplied it.
- **Dispositions were written but never read.** `evidence_disposition` is how an append-only row is corrected (`LFPS-v0`), and 13A/13B left retroactive root-cause contamination here because no engine read a correction.
- **Retention already replayed the mastery decision after every row.** Weakness needs the same timeline; two copies of a subtle replay would drift.
- **Two loops pointed at "13" with no sub-step to own them.** The user decided: Topic state (`TSM-v0`'s six states, `RVR-v0` §15 `weakening` included) → **16C**; the high-stakes pause gap policy (11C) → **18D**.

---

# 3. Scope boundary

## 3.1 13D decides

- each evidence row's attribution, by `WLRM-v0`'s twelve rules in priority order,
- the Objective weakness lifecycle and remediation closure,
- the Skill's weakness axis and the `weakness_detected` needs its owner supplies,
- how a disposition is read, and retroactive root-cause contamination inside a blueprint session,
- `weakness_state` storage and its value set.

## 3.2 13D does not decide

- whether a Skill is mastered — the mastery engine; confirmation reads its gates, it never sets them,
- Topic state and `weakening` → 16C (user decision),
- remediation **content** — the per-Objective strategy routes are authored data (`remediation_routes.yaml`); turning them into tasks is 15/20 (`WLRM-v0` §11 `content_realization`),
- misconception memory and its taxonomy → 14B (`WLRM-v0` §11),
- guidance fading inside a remediation task → 14 (tutor behaviour),
- calibration of anything → 18C.

No score, threshold, decay or reset is introduced.

---

# 4. Attribution (`failure_attribution_rules.yaml`)

`WeaknessEngine.rule` returns the first matching rule in priority order:

| Priority | Rule | Matches | Effect |
|---:|---|---|---|
| 10 | `invalid_or_ambiguous` | invalid outcome, invalid evaluator, contested | not attributable |
| 20 | `prerequisite_contamination` | prerequisite not valid | prerequisite signal; target untouched |
| 30 | `unattempted_or_deferred` | — | never a row (nothing is written for an unattempted task) |
| 40 | `environment_outside_target` | — | never a row (an unmeasurable result is already `invalid`) |
| 50 | `assisted_h1_h4` | failure with help or a seen solution | hypothesis |
| 60 | `provisional_or_partial` | failure that is provisional, partial or not direct for the Objective | hypothesis |
| 70 | `clean_premastery_h0_direct` | clean failure, Skill not mastered | supported |
| 80 | `first_clean_postmastery_contradiction` | clean failure, mastered, no verification open, mastery holds | supported + verification open |
| 90 | `fresh_recheck_fail` | clean failure, mastered, and the gates fail — or a fresh recheck fails while verification is open | confirmed when the gates fail; otherwise verification stays open |
| 100 | `integrated_global_outcome_guard` | — | never a row (evidence is written per Objective component) |
| 110 | `review_due_without_negative_evidence` | — | never applies (this engine does not read retention) |
| 120 | `fresh_recovery_success` | clean, fresh success | resolves what it can (§6) |

**Clean** is `WLRM-v0` §4: H0, direct for the Objective, verified, prerequisite-valid, uncontested, no solution exposure. A failure that is clean but **not direct** for its Objective is corroboration, so it is a hypothesis (declared: `WLRM-v0` rule 70 requires direct). A failure never lowers a signal: weaker evidence adds to it, stronger evidence escalates it.

**Confirmation follows the gates.** `failed_fresh_recheck_or_GRE_gate_failure: confirmed_remediation_required` — when the mastery engine's gates no longer pass after a clean failure, the remediation is confirmed whatever item showed it; a fresh recheck that fails while mastery still holds keeps verification open.

---

# 5. The lifecycle and the axis (`WLRM-v0` §5)

An Objective's signal is `none → hypothesis → supported → confirmed → resolved`; a new failure after `resolved` starts a new signal from that day. Every open signal names the evidence that showed it and the day it was first seen; a resolved one names the evidence that resolved it. Those are invariants of the type: a weakness without evidence, or a resolution without evidence, cannot be constructed.

A Skill's axis is the strongest open signal of its Objectives: any confirmed → `remediation_required` (the value the gate and the planner already read), else `supported`, else `hypothesis`, else `resolved`, else `none`. Nothing reaches a Topic, a Domain or another Skill (`aggregation_guards.yaml`).

---

# 6. Closure (`WLRM-v0` §7)

A **fresh** check — not an item or a variant family that showed the open signal — that is clean:

- resolves a `hypothesis`,
- resolves a `supported` signal (before mastery, or a post-mastery verification: the recheck passed),
- resolves a `confirmed` remediation **only when the mastery engine's gates pass again**; one success is not enough, exactly as a finished remediation task is not.

Help taken never closes anything (`guard.ai_scaffold_not_closure`); a completed task writes no evidence, so it cannot close anything by construction.

---

# 7. Needs (`learning_need_mappings.yaml`)

`WeaknessEngine.needs` supplies `weakness_detected` for an on-route Skill whose axis is `hypothesis` (severity `partial_or_uncertain_concern`) or `supported` (`clean_contradiction_or_verification_due`). A confirmed remediation is not supplied again — the planner already opens `remediation_required` from the axis — and neither is a post-mastery verification, which the planner opens from the mastery axis: **one concern is one need**.

`BuildDailyPlan` adds the supplied needs to the planner's own; `ComposeAssessmentBlueprint` adds them to the caller's owner needs, so the weekly and monthly pools see them as 13A/13B already route them. The planner's band for `weakness_detected` (P1) and every other rule are unchanged.

---

# 8. Dispositions and retroactive contamination

**A disposition is read, never written over the row.** The store returns each evidence row with its **newest** disposition applied (`EvidenceDispositions.effective`), so mastery, retention and weakness all see the corrected row without a second exclusion rule:

- `contested` → the row is contested,
- `invalidated` with reason `prerequisite_contaminated` → the row is prerequisite-contaminated,
- any other `invalidated`, or `superseded` → the row's evaluation no longer stands (evaluator `invalid`),
- `reinstated` → the row as recorded.

The learner's outcome is never rewritten. `RecordDisposition` appends one in a transaction, refusing an unknown disposition or decider, a blank reason, and evidence that does not exist.

**Retroactive root-cause contamination** (`WBA-v0` §25, `MCA-v0` §20). `ApplyRetroactiveContamination` takes a blueprint and the session's outcomes: for every slot whose item really requires a Skill the session showed cleanly missing — and whose target is not that Skill — each of its evidence rows not already contaminated gets `invalidated` / `prerequisite_contaminated` / `deterministic_rule`. Independent branches are never touched; asked again, it has nothing more to say. This closes 13A/13B's forward-only limitation.

---

# 9. Shared mastery timeline

`MasteryTimeline` (core-application) is the one replay of the mastery engine after every row, mirroring `RebuildMastery`; retention (13C) now reads it too, so both engines see the same answer to "was this Skill mastered before and after this row?".

---

# 10. Storage

**Schema version 6** completes `weakness_state`, forward-only and in one transaction: `skill_logical_id`, `skill_version`, `last_attribution_outcome`, `last_failure_rule`, `verification_open`, `signal_evidence_ids`, `first_seen_on_study_day`, `last_seen_on_study_day`, `resolution_evidence_id`, `as_of_study_day`; the `weakness_by_skill` index; and two triggers refusing a signal outside `WLRM-v0`'s lifecycle (the insert guard also stops an upsert; the update guard stops a direct `UPDATE`). Migration was tested against a **populated** schema-5 database: every truth row unchanged.

`skill_state` gets the weakness axis, carries the other three, and keeps the **older** watermark. The store's `evidenceFor` reads each row's newest disposition through the existing `dispositions_by_evidence_event` index. No port changed; the port count stays four.

---

# 11. Living gates narrowed

- `E13C-08_watermark_first` and the three `E13C-08_replay_mirrors_*` checks read the Skill's rows and the per-row replay where they now live (`MasteryTimeline`), plus a new check that retention reads the shared timeline.
- `E13C-10_schema_version_5` now requires version 5 or later with every later version owned by its contract; 13C's storage test asserts at least 5.
- The 12x and 13A/13B schema gates needed no change: they already require an owning contract, and the 13D contract owns v6.

No guarantee was weakened.

---

# 12. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

JVM tests: 679, all passing.

**Mutation 44/44, and only the weakness suites ran** — every mutant broke a weakness rule in the axis, freshness, cleanliness, dispositions, attribution, confirmation, closure, the supplied needs, the `skill_state` watermark, disposition recording, retroactive contamination, the shared timeline, the planner and composer bridges, the lifecycle triggers, the migration or the store's disposition read; every mutant had to compile, a run with no Gradle verdict is refused, and a comment-only control survives. **Three first-run mutants survived, each a test that proved less than it claimed:** D02 (a hypothesis outranking a supported weakness on the axis) — the axis test never paired the two; D31 (directness assumed when rebuilding) — every application row was direct; D37 (the shared timeline forgetting the previous mastery) — no weakness test held a contradiction after mastery. Three tests were added or strengthened, all three mutants are caught, and the whole set was run again from one unchanged tree. Details in `arch/13d_remediation_engine/remediation.yaml` (`mutation_results`).

**Not run: T6.** Nothing in the app rebuilds engines after evidence or calls the planner yet (16D).

---

# 13. Open, and owned elsewhere

- **Topic state machine and `weakening`** → 16C (user decision; `RVR-v0` §15 included).
- **High-stakes pause gap policy** (11C) → 18D (user decision); until then a high-stakes pause is not resumed.
- **Remediation content from the strategy routes** → 15/20; **misconception memory** → 14B; **guidance fading** → 14.
- **Rebuilding engines after evidence from the app**, and calling `ApplyRetroactiveContamination` when a session closes → 16D.
- **Disposition reasons in mastery traces**: an invalidated row is excluded as "evaluator not verified"; naming the disposition in the trace → 16C (evidence history presentation).
- **Calibration** → 18C.

---

# 14. Anti-patterns explicitly rejected

- one failed attempt failing a Skill, a Topic or a Domain,
- an unattributable, contaminated, deferred or environment-blocked attempt blaming its target,
- help taken, a provisional evaluation or a partial result confirming a remediation,
- a contradiction after mastery erasing it before verification,
- a remediation confirmed by anything but the mastery engine's gates,
- a remediation closed by a finished task, by help, by the same item, or by one success,
- a manual mastery override,
- a weakness spread to linked Skills, dependents, a Topic or a project's components,
- a correction written over a stored row,
- a contaminated downstream answer counted against its target,
- `review_due` read as a weakness.

---

# 15. 13D acceptance contract

1. `WLRX-v0` is the accepted weakness and remediation implementation.
2. The lifecycle, attribution outcomes and twelve rules equal `WLRM-v0`'s, in order.
3. Attribution is Objective-local; the Skill axis is derived; nothing broadcasts.
4. Confirmation and closure follow the mastery engine's gates; closure needs fresh clean evidence.
5. The engine supplies `weakness_detected`; one concern is one need; no planner rule changed.
6. Dispositions are read, newest first, and never written over a row; retroactive contamination touches only dependent slots.
7. Schema v6 completes `weakness_state`, tested against a populated database; the port count stays four.
8. Narrowed living gates keep their guarantees.
9. Nothing is claimed that was not run; T6 was not run.
10. Independent 13D QA passes and the full `validate_*.py` sweep passes.

---

# 16. Handoff after acceptance

If accepted, 13D becomes `WLRX-v0 / D-102`.

Next numbered step: **13E — Program değişiklik raporu**. It must receive a fresh PRE-STEP and explicit user approval before execution. Mastery, retention, readiness and weakness now all write state from evidence; 13E reports what changed in the program and why, from the engines' own traces.

---

**13F note (2026-10-01, `D-104`, user decision):** a clean miss inside a diagnostic, on an Objective with no evidence from ordinary learning before it and a Skill not mastered (`WeaknessEvent.diagnosticBaseline`), opens no weakness signal — `VDW-v0` §12.1: not knowing something never taught is not a weakness. `WeaknessEngine.rule` returns no rule for that case only; the twelve rules and every other attribution are unchanged.

---

**14A note (2026-10-01, `D-105`):** guidance fading is not the tutor's — the tutor never fades by withholding; scaffold decreases through what the planner selects and the instruction mode a task declares, driven by evidence (`TEIP-v0` §5.4). Misconception memory remains 14B's. Details: `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md` §13.

---

**14B note (2026-10-01, `D-106`):** misconception memory is now in code and stays in the `WLRM-v0` family: `MisconceptionEngine` replays each catalog label by `WeaknessEngine.rule` unchanged, capped by its source (an `ai_proposed` label never rises above a hypothesis). `WeaknessEvent` gained `misconceptionTags`; no attribution rule changed. One-Skill event building moved to `WeaknessEvents`, shared with the wrong-answer analysis. Details: `docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md`.
