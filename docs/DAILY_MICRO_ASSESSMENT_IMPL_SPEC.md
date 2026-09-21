# Daily Micro Assessment Implementation Specification — DMAX-v0

**Stage step:** 11D — Günlük mikro quiz  
**Status:** ACCEPTED — independent 11D QA PASS  
**Decision:** `D-090`  
**Model:** `DMAX-v0 — Daily Micro Assessment Implementation`  
**Interior semantics:** `ASUX-v0 / D-071`  
**Daily scope:** `DMA-v0`  
**Item bank and validation:** `QAB-v0`, `AIV-v0`  
**Frame:** `TRUX-v0 / D-070`, `RNRX-v0 / D-088`, `SESX-v0 / D-089`  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

11D makes one honest measurement runnable end to end: authored curriculum can be published into the immutable store, an item can be read back with its pins intact, what that item may be used for is decided by rules rather than by its own claim, and the single assessment session interior exists as code.

It answers one primary question:

> **Bir ölçüm baştan sona nasıl dürüst yürür — ve bir soru aslında ne için kullanılabilir?**

Primary invariant:

> **An assessment session is an evidence-collection workflow**, and **an item may only carry what its validation, its evaluator and the Objective's own evidence profile allow.** Neither the session nor the item is an authority: the session never becomes a gradebook or a second state engine, and an item cannot promote itself.

---

# 2. What was found before writing any assessment code

- **The immutable curriculum region had no writer at all.** The store could report whether a curriculum was published and read rows back, but nothing could publish one; its rules had never been exercised by a real write. `publishCurriculum` is now the single write path.
- **`DDM-v0` names only part of `QAB-v0`'s item.** `assessment_resource_version` carries `content_ref`, `evidence_type`, `allowed_tools_policy`, `variant_family_id`, `dependency_group_id` and `content_origin`. An item's targets, use ceiling, scope eligibility, evaluator requirement, difficulty and independence mode have no columns — so they stay in the authored content document. Inventing columns is what 10D forbade.
- **The artifact body had nowhere to live.** See §7.
- **A published-version reference could not be revalidated.** The first resolver only looked inside the package being published, so a later version carrying only a revalidation of an already-published resource was refused. The T2 publishing check found it, not review; references now resolve against the package **or** the store, both version-pinned.
- **The mutation runner had never run anything.** See §9 — this invalidated 11C's recorded mutation result, which is corrected in the same change.

---

# 3. Scope boundary

## 3.1 11D decides

- curriculum ingestion: the authored package format, its strict parser, and publishing into the immutable region,
- what an item may be used for: the effective use ceiling and evidence fit,
- exposure recording when an item is served and when a solution is revealed,
- the single assessment session interior (`ASUX-v0`) as code, used first by `daily_micro`,
- where a short artifact body lives.

## 3.2 11D does not decide

- which measurement runs today, and the prerequisite gate that precedes it → the planner and evidence pipeline, 12,
- weekly and monthly blueprint composition → 13 (`WBA-v0`, `MCA-v0`),
- AI evaluator behaviour, rubric content and final microcopy → 14,
- the authored curriculum and item content itself → 15,
- `assessment_report` and longitudinal history → 16B,
- evaluator and item calibration → 18D.

No passing threshold, percentage grade, fixed question count or countdown is introduced.

---

# 4. Ingesting authored curriculum

`publishCurriculum` is the only way into the curriculum region, which is what makes its rules enforceable rather than aspirational:

- **one transaction** — a package lands whole or not at all, so an Objective whose Skill is missing cannot be observed;
- **a published version is never overwritten** — re-publishing the same version answers `AlreadyPublished`, and a correction is a new version;
- **refusal is decided before anything is written**, so a refused package is not a rollback story: an unresolved reference, a row carrying another version than the package, or an evidence type that is not a single token all refuse the whole package;
- **nothing is written into the user regions** — publishing advances no truth sequence.

The authored package is `curriculum_package/1`: `[section]` blocks of `key=value` lines. The parser is **strict** — an unknown section, an unknown or repeated key, a missing required key, an unpinned reference, an unknown enum value or a line that is neither refuses the package and names the line. A package that fails to parse serves *nothing*; the app then behaves exactly as it does with no content rather than serving fragments.

**No content ships today.** Authoring the first curriculum is 15's, so ingestion runs at startup, finds no package, publishes nothing and says so.

---

# 5. What an item may be used for

Trust is the **store's** answer. The published validation record overrides whatever lifecycle status the authored document claims for itself, because an item that could promote itself would make `AIV-v0`'s pipeline decorative.

The effective use ceiling is the **most restrictive** applicable rule (`QAB-v0` §6, `AIV-v0` §22):

| Situation | Ceiling |
|---|---|
| `draft`, `invalidated`, `retired` | not selectable at all |
| validated only as `candidate` | `practice_only` |
| `ai_generated` and not `trusted` | at most `standard_mastery_eligible` |
| no deterministic verification, provisional evaluator allowed | at most `low_stakes_assessment` |
| deterministic verification required but absent | `practice_only` |
| evaluator unavailable and no deterministic check | `practice_only` |

The ceiling never rises above what the item declared. `checkpoint` and `integration_check` need `low_stakes_assessment`; `mastery_evidence` and `verification` need `standard_mastery_eligible`.

**Evidence fit is the Objective's decision, not the item's** (`DMA-v0` §12): the item declares what it produces, and the Objective's profile says what it accepts. A mastery-bearing measurement must produce the Objective's own direct type, so a convenient recall item cannot become production evidence by claiming to be one. An unknown Objective profile is unfit, never assumed compatible.

An unusable item is a fact about the item and the store. It is never negative evidence and never presented as the learner's error.

---

# 6. Exposure

Exposure is recorded when the item is actually **served** (`item_version_seen`) and when a solution is **revealed** (`solution_exposure`, carrying the level and the attempt it came from). An item that was found unusable records nothing, because the learner never saw it. Exposure rows are permanent (`LFPS-v0`): forgetting them is how a product starts re-measuring with an answer the learner has already seen.

---

# 7. Where a short response lives

`DDM-v0` gives an artifact a `content_ref` and does not say what it points at. For a daily micro response the body is carried **inside the reference**, as an RFC 2397 `data:` URI.

That keeps three guarantees a side file would have cost: the body commits in the very same transaction as its attempt, so the half-record `LFPS-v0` forbids cannot exist; it lives inside the database, so 10E's verified export already contains it and no second restore path appears; and no table, column or port is invented for it.

Its limit is explicit: beyond 4096 characters the body is **refused**, never truncated and never silently written elsewhere. Code files, projects and execution output are larger artifacts whose store belongs to the steps that produce them (14, 15).

---

# 8. The one session interior

`AssessmentSession.kt` in `core-presentation` holds `ASUX-v0`'s nineteen states in its order, with `VDSX-v0`'s tones — only the two system conditions wear the fault tone. One interior serves all three scopes; `assessment_scope` is displayed context, so a "monthly" label cannot raise what an item proves.

- **The atomic evidence boundary is the submission unit.** An item boundary carries exactly one item and a testlet is never split.
- **Submission freezes.** A frozen boundary cannot be revisited, edited or resubmitted — there is no code path that accepts it.
- **Unsubmitted boundaries stay navigable**, in any order, because nothing is frozen until it is submitted.
- **Skipping is not incorrect.** It leaves the measurement need unresolved and lands in `not_reliably_measured`.
- **Help is never blocked.** H1/H2 make the attempt assisted; H3/H4 make it practice-only and solution-exposed and require a fresh unseen item. No assistance level produces independent mastery evidence, and the conversion of a measuring item into learning is explicit. The session raises `requires_independent_recheck`; **it never schedules it.**
- **Recomposition** replaces unresolved slots under the five declared conditions and never touches a frozen boundary; a recomposed slot is a fresh measurement, not a retry of a failure.
- **A dispute** holds the evidence as contested without invalidating the item and without harming the learner's state.
- **The result is semantic.** Six families, `not_reliably_measured` first class and always shown when non-empty, raw counts kept in a separate type so they cannot be read as the outcome, and **a state change claimed only when canonical state actually changed** — with no evidence pipeline yet, a session says plainly that nothing changed rather than manufacturing a claim. There is no score, percentage, grade or threshold field anywhere in the result.

Independence mode and the allowed-tools policy are stated in text **before** the response affordance.

---

# 9. Mutation testing, and a tool that had never run

Writing 11D's mutation suite exposed a defect in the harness both 11C and 11D used: it invoked `cmd /c gradlew.bat` with a working directory, which Windows does not resolve, so **Gradle never started**. Every mutant returned a non-zero exit code for the wrong reason and was recorded as caught.

- The runner now invokes the wrapper by absolute path and **refuses to classify a run whose output contains no Gradle build result at all**.
- 11D's suite was re-run against the fixed harness.
- **11C's suite was re-run too, and `docs/SESSION_STATE_SPEC.md`, `arch/11c_session_state/session_state.yaml` and `D-089` were corrected to the real result.** A number that was produced by a tool that never ran is not a finding; leaving it in place would have been the exact opposite of what this repository is for.

---

# 10. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :core-model:test :core-ports:test :core-engines:test :core-application:test :core-presentation:test` | T1 | PASS |
| RUN-02 | `./gradlew :data-persistence:test` | T2 | PASS |
| RUN-03 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-04 | `./gradlew :data-curriculum:test :ai-adapter:test` | T5 | PASS |
| RUN-05 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS |
| RUN-06 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

**Not run: T6.** The phone was not connected. In the real app no planner selects an assessment and no content ships, so no session can start, no item can be served and the session screen was never drawn on a device.

---

# 11. Anti-patterns explicitly rejected

- an item promoting itself past its validation, or an unvalidated AI item carrying mastery evidence,
- an item declaring its own evidence fit, or a mastery claim from something that is not the Objective's direct type,
- exposure recorded for an item nobody saw,
- overwriting a published curriculum version, or publishing half a package,
- serving a half-loaded authored package,
- truncating an artifact body to make it fit,
- editing or resubmitting a frozen boundary,
- treating a skip as incorrect,
- blocking assistance, or converting a measuring item silently,
- a session that schedules its own recheck,
- a dispute used as an undo button,
- a result with a score, grade, percentage or threshold,
- hiding `not_reliably_measured` or folding it into "incorrect",
- claiming a state change no engine reported.

---

# 12. 11D acceptance contract

1. `DMAX-v0` is the accepted daily micro assessment implementation.
2. Vocabularies equal `ASUX-v0`, `DMA-v0`, `QAB-v0` and `AIV-v0`, in their order.
3. Publishing is the single write path, one transaction, and never overwrites a published version.
4. The authored package parses strictly or not at all, and a failed parse serves nothing.
5. Trust comes from the store's validation record, never from the item's own claim.
6. The effective ceiling is the most restrictive applicable rule and never exceeds the declared one.
7. The Objective's profile decides evidence fit; a mastery measurement needs its direct type.
8. Exposure is recorded when an item is served and when a solution is revealed, never otherwise.
9. One interior serves all scopes; submitted boundaries freeze; skipping is not incorrect.
10. Assistance is never blocked, its consequence is disclosed, and conversion is explicit.
11. The result is semantic; `not_reliably_measured` is first class; no change is claimed without a canonical change.
12. An artifact body is carried inline or refused, never truncated.
13. The schema is unchanged, no migration was added, and the port count stays four.
14. Nothing is claimed that was not run; T6 was not run.
15. Independent 11D QA passes, Stage 6–11C regressions pass, and 11C's mutation record is corrected.

---

# 13. Handoff after acceptance

If accepted, 11D becomes `DMAX-v0 / D-090`.

Next numbered step: **11E — Gün sonu**. It must receive a fresh PRE-STEP and explicit user approval before execution. Open loops carried: planner selection and the evidence pipeline (12), weekly and monthly composition (13), evaluator behaviour and microcopy (14), authored content (15), `assessment_report` (16B), calibration (18D) and the T6 device run.
