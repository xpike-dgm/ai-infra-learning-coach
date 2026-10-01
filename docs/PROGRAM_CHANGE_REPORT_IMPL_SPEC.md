# Program Change Report Implementation Specification — PCRX-v0

**Stage step:** 13E — Program değişiklik raporu  
**Status:** ACCEPTED — independent 13E QA PASS  
**Decision:** `D-103`  
**Model:** `PCRX-v0 — Program Change Report Implementation`  
**Result semantics:** `ASUX-v0 / D-071` §13 (result families, truthful state and plan consequences), `WBA-v0` / `MCA-v0` §30 ("what changed in the plan"), `V1_SCOPE` V1 release tanımı 3 ("weekly/monthly assessment gelecek planı değiştirmeli")  
**Engines it reads:** `MSTX-v0 / D-092`, `RVRX-v0 / D-101`, `WLRX-v0 / D-102` (axes), `PRQX-v0 / D-093` (readiness rebuild), `PLNX-v0 / D-094` + `RPLX-v0 / D-095` (plan versions and replan), `RSNX-v0 / D-096` (trace)  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085` — schema unchanged (v6)  
**Boundaries:** `MSBX-v0 / D-078` — one port refinement, still four ports

## 1. Purpose

After 13A–13D every engine writes its own state from evidence, and the planner replaces a plan with a reasoned new version. 13E makes the consequence of an assessment **visible and honest**: after evidence is recorded, the touched Skills are recomputed by their own engines, the plan is replaced only if canonical state actually changed, and the result names exactly what changed — from what the engines wrote, never from the answers.

It answers one primary question:

> **Bu oturumdan sonra kanonik durumda ve planda gerçekte ne değişti — ve neden?**

Primary invariant:

> **A report is a diff of two readings; it can never claim more than the engines wrote.** A change is stated only where an axis actually moved, a state nobody had written is not a "before", a review coming due is the day and not a change, a plan change is the difference between two recorded plan versions, and when nothing changed the result says so plainly.

---

# 2. What was found before writing report code

- **Nothing recomputed a Skill after evidence in one place.** Mastery, retention, weakness and readiness each had a rebuild, but each took Objective gate profiles from its caller, and no port could say which Objectives a Skill has. `objectiveProfile(ref)` reads one known Objective; nothing listed a Skill's.
- **The result already had its six families** (`SessionResults.of(canonicalChanges = …)`, 11D) and a `stateChangeRefs` list on the blueprint result (13A) — both empty by design until an engine reported a change.
- **The replan already existed** (12D) with `PDT-v0` §8.10 events, including `new_remediation_created` and `new_verification_due_created`; nothing chose the event from what an assessment did.
- **A first reading has no before.** A Skill whose axes were never written cannot be said to have changed; claiming "confirmed" for it would be manufacturing progress.

---

# 3. Scope boundary

## 3.1 13E decides

- which axis transitions are reportable state changes and which result family each belongs to,
- the plan diff between two plan versions (needs opened/closed, tasks added/removed),
- the order in which a touched Skill's engines are recomputed, with profiles from the published curriculum,
- whether a replan is asked for and which `PDT-v0` §8.10 event labels it,
- the result-family statements and the "nothing changed" sentence.

## 3.2 13E does not decide

- any state — each engine still decides its own axis; the report only reads,
- a durable `assessment_report` row or longitudinal report (`SPWX-v0`, 16C),
- calling recompute/report from the app after a real session (16D),
- transfer/artifact gate parameters the curriculum package does not carry yet (15) — profiles leave them at `GRE-v0`'s defaults,
- wording beyond working microcopy (14).

No score, threshold, ratio or weight is introduced.

---

# 4. Recomputing a touched Skill (`RecomputeSkillState`)

1. Profiles: `persistence.objectivesOf(skill)` → `ObjectiveGateProfiles.of` — `KGC-v0`'s `required`, `criticality` (`critical` / `standard`; anything else is **refused**, not read as standard) and evidence types; every other gate parameter keeps `GRE-v0`'s default.
2. A Skill with no published Objective has nothing to recompute; nothing is written.
3. Order: `RebuildMastery` → `RebuildRetention` → `RebuildWeakness` → `RebuildReadiness`. Retention and weakness read the mastery timeline; readiness reads the axes they wrote. Each writes only its own family; recomputing writes no truth.

---

# 5. Snapshots (`CaptureProgramSnapshot`)

A snapshot reads the truth watermark **first**, then every published Skill's `skill_state` axes as stored (an empty or missing axis reads `not_yet_evaluated`), and the newest plan version with its decoded trace (`null` when there is none or it does not decode). The caller captures `before` before recording evidence.

---

# 6. State changes (`ProgramChangeEngine.stateChanges`)

| Axis | From → To | Change | Family | Reason code |
|---|---|---|---|---|
| mastery | not mastered → `confirmed_current` | `capability_confirmed` | confirmed_capabilities | `replan.evidence_state_changed` |
| mastery | `confirmed_current` → `confirmation_verification_due` | `verification_opened` | verification_needed | `replan.new_verification_created` |
| mastery | `confirmation_verification_due` → `confirmed_current` | `verification_resolved` | confirmed_capabilities | `replan.evidence_state_changed` |
| mastery | mastered → not mastered | `mastery_no_longer_confirmed` | persistent_targeted_gaps | `replan.evidence_state_changed` |
| retention | any → `verification_due` | `verification_opened` | verification_needed | `replan.new_verification_created` |
| retention | any → `at_risk` | `retention_at_risk` | verification_needed | `replan.evidence_state_changed` |
| retention | `fresh` / `review_due` / `verification_due` / `at_risk` → `stable` | `retention_revalidated` | retention_revalidated | `replan.evidence_state_changed` |
| weakness | any → `remediation_required` | `remediation_opened` | persistent_targeted_gaps | `replan.new_remediation_created` |
| weakness | `remediation_required` → `resolved` / `none` | `remediation_closed` | confirmed_capabilities | `replan.evidence_state_changed` |
| weakness | not remediation → `supported` | `weakness_supported` | persistent_targeted_gaps | `replan.evidence_state_changed` |
| weakness | `none` / `resolved` → `hypothesis` | `weakness_question_opened` | verification_needed | `replan.evidence_state_changed` |
| weakness | `hypothesis` / `supported` → `resolved` | `weakness_resolved` | confirmed_capabilities | `replan.evidence_state_changed` |

Rules:

- **A state nobody had written is not a before.** If an axis was `not_yet_evaluated` before, no change is claimed on it; the Skill is listed in `unknownBefore`.
- **Time alone reports nothing.** `fresh`/`stable` → `review_due`, `untracked` → `fresh`, and development between non-mastered states are not changes an assessment caused.
- **A contradiction is a verification, never a demotion** (`ASUX-v0` §13.4): `confirmed_current` → `confirmation_verification_due` is `verification_opened`.
- **One verification is named once**, whichever axis opened it.
- **A correction downgrade is not a new finding:** `remediation_required` → `supported` and `supported` → `hypothesis` report nothing.
- **A hypothesis is a question, never a gap** (`SPWX-v0`): it is filed under `verification_needed`.

Readiness is not diffed separately: it is a function of these axes (`RebuildReadiness` reads them), and its effect on other Skills appears where it matters — as plan changes.

---

# 7. Plan changes (`ProgramChangeEngine.planChanges`)

Between two **different** plan versions: needs opened and closed (by `needKey`), tasks added and removed (by `candidateId`), each with its trigger and Skills. The same plan version is no plan change. No previous plan → `firstPlan`, and a first plan is **not** a change. The report's reason codes are the state changes' codes plus the new plan's own `replan.*` codes — only when a new plan version exists.

---

# 8. Replanning (`ReportProgramChanges`)

1. Recompute every touched Skill (§4).
2. Diff `before` against the recomputed state. **No state change → no replan, nothing written**, and the report says nothing changed.
3. Otherwise ask the planner's own replan (12D) with the event the change names: `remediation_opened` → `new_remediation_created`; else `verification_opened` → `new_verification_due_created`; else `new_evidence_recorded`. None of these sets remaining time, so the day's budget is kept; the label only names the plan version — the report lists every change's own reason. On another study day the planner's own rules make it a re-entry.
4. Capture again and report against `before`: state changes, plan changes and reasons together.

---

# 9. Result families (`ProgramChangeResults`)

`canonicalChanges(report, label)` is what `SessionResults.of` takes: one statement per recorded change, in its family, and a `plan_changes` section from the plan diff. `summary(report)` is the one sentence shown when nothing changed (`NOTHING_CHANGED`, or `FIRST_PLAN` for a first plan). Sentences never score, grade, blame, call dropped work less important, or name a hypothesis a deficiency; a check holds them to it. `StateChange.ref` (`skill_state:<skill>#<change>`) is the reference a blueprint result carries in `stateChangeRefs`.

---

# 10. Port refinement

`PersistencePort.objectivesOf(skill): List<ObjectiveRow>` — the published Objectives whose parent is this Skill version, ordered by `logical_id, version`; an unpublished version has none. Four ports remain; no schema change (v6).

---

# 11. Verification

- Suites: `ProgramChangeFactsTest` (model), `ProgramChangeEngineTest` (engine), `ProgramChangesTest` (application, end to end: evidence → recompute → replan → report), `ProgramChangePresentationTest` (presentation), `ProgramChangeStorageTest` (real SQLite).
- Implementation mutation with only these suites running; validator `tools/validate_program_change_report.py` with its own mutation test.
- Six runs (T1, T2, T3, T5 ×3), all PASS.
- **Mutation 42/42**, only the program change suites running; the comment-only control survived. Two tests were strengthened while the mutant list was written, before any run (support corrected down to a hypothesis reports nothing; the retention axis written by recompute is the retention engine's own); nothing survived the first run.
- **Not run: T6.** Nothing in the app calls recompute or the report after a real session yet (16D).

---

# 12. Open loops

| Loop | Owner |
|---|---|
| durable `assessment_report` and longitudinal history | 16C |
| app calls recompute/report after a session; fills `stateChangeRefs` | 16D |
| transfer/artifact gate parameters in the curriculum package | 15 |
| final wording of result statements | 14 |

---

# 13. Anti-patterns explicitly rejected

- a change claimed from a state nobody had written,
- a review coming due, or development between non-mastered states, reported as a change,
- a contradiction after mastery reported as a demotion,
- a hypothesis filed or worded as a gap,
- a first plan, or the same plan version, reported as a plan change,
- a replan when no canonical state changed,
- a report built from answers instead of state,
- a score, percentage, grade or pass/fail in the result,
- work that did not fit called less important, owed or failed.

---

# 14. 13E acceptance contract

1. `PCRX-v0` is the accepted program change report implementation.
2. State-change families are `ASUX-v0` §13.1's; every reason code is one the planner's `ReplanTrigger` already records.
3. A state nobody had written claims nothing; time alone reports nothing; a contradiction is a verification.
4. Plan changes are diffs between two different plan versions; a first plan is not a change.
5. A touched Skill is recomputed by its own engines, in order, with profiles from the published curriculum.
6. A replan is asked for only when state changed, through the planner's own replan, keeping the day's budget.
7. One port refinement (`objectivesOf`), four ports, schema unchanged.
8. Nothing is claimed that was not run; T6 was not run.
9. Independent 13E QA passes and the full `validate_*.py` sweep passes.

---

# 15. Handoff after acceptance

If accepted, 13E becomes `PCRX-v0 / D-103`.

Next numbered step: **13F — Tanısal atlama (VDW-v0)** (added by `D-099`). It must receive a fresh PRE-STEP and explicit user approval before execution. It is also the owner of 12F's S06 scenario, which could not run without a diagnostic waiver.
