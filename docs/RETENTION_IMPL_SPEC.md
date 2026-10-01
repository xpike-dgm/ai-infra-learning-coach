# Retention Implementation Specification — RVRX-v0

**Stage step:** 13C — Spaced repetition  
**Status:** ACCEPTED — independent 13C QA PASS  
**Decision:** `D-101`  
**Model:** `RVRX-v0 — Retention Verification & Risk Implementation`  
**Retention semantics:** `RVR-v0 / D-032`  
**Engines it reads and feeds:** `MSTX-v0 / D-092` (mastery decisions), `PRQX-v0 / D-093` (readiness), `PLNX-v0 / D-094` (needs), `WBAX-v0 / D-098`, `MCAX-v0 / D-100` (retention roles)  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

13C turns `RVR-v0` into code: a Skill the mastery engine has confirmed gets a review schedule, a delayed check that was really independent re-verifies it and lengthens the interval, a clean contradiction opens verification without erasing anything, and the planner sees a review the day it comes due.

It answers one primary question:

> **Kanıtlanmış bir beceriyi ne zaman yeniden kontrol etmek gerekir, ve o kontrol neyi gösterdi — zamanın geçmesini bir eksiklik saymadan?**

Primary invariant:

> **Time passing is not negative evidence.** The day only says that a scheduled check has come: mastery does not decay, a due review is not forgetting, nothing locks, and only a clean independent check can move a Skill toward verification, risk or stability.

---

# 2. What was found before writing retention code

- **Nothing had ever written the retention axis.** `skill_state.retention_axis_state` was `not_yet_evaluated` everywhere; the planner, the prerequisite gate and 13A/13B's retention roles all read an axis no engine produced.
- **`retention_state` existed with one column.** `DDM-v0` names the projection "per Skill" and 10D gave it only `state`; `RVR-v0` §18's compact state (interval, next review, last strong evidence, open verification, risk reasons) had nowhere to live.
- **An evidence row did not carry its day.** `evidence_event.occurred_on_study_day` was stored but `EvidenceRow` dropped it, and a schedule cannot be built without the day of each check.
- **No due query existed.** `RVR-v0` §19 asks for an indexed `next_review_at` query so planning never scans retention history.
- **Mastery is path-dependent.** 12A's rebuild reads the previous axis (a contradiction opens verification only after mastery). A rebuildable retention schedule therefore needs the mastery decision **after each row**, not only the current one.
- **`skill_state` had one writer.** 13A left "assembling the row under one watermark" to 13C, because retention is the first engine besides mastery to write it.

---

# 3. Scope boundary

## 3.1 13C decides

- the retention state machine and its transitions, from evidence,
- the schedule (initial interval, growth, cap) with `RVR-v0` §20's V0 values,
- the day-driven `review_due` refresh and where it runs,
- `retention_state` storage, its value set and the due index,
- the retention axis on `skill_state`.

## 3.2 13C does not decide

- whether a Skill is mastered — the mastery engine (12A); a second clean failure ends retention tracking only because the mastery engine's gates stop passing,
- remediation and Topic `weakening` (`RVR-v0` §15) → 13D,
- which review runs on which day, and overdue urgency buckets (`PBR-v0` §6.4 thresholds) → the planner and 18C,
- representative checks after a long absence (`RVR-v0` §16 `OVERDUE_REPRESENTATIVE_CHECK`) → the planner, 18B,
- when the app rebuilds engines after evidence → 16D,
- calibration of every number → 18C; any model beyond V0 (FSRS-like, HLR) → after 18 (`RVR-v0` §22).

No probability, half-life, forgetting curve, success-rate target or decay is introduced.

---

# 4. The state machine (`RVR-v0` §2–§10)

The engine (`RetentionEngine.replay`) is a pure function of a Skill's evidence in recording order. For every row it is told whether the mastery engine considered the Skill mastered before and after that row (`CONFIRMED_CURRENT` or `CONFIRMATION_VERIFICATION_DUE` — historical mastery is kept while a contradiction is verified).

| From | Event | To |
|---|---|---|
| not mastered | the row that makes it mastered | `fresh`, initial interval from that day |
| `fresh`/`stable` | clean failure | `verification_due` (`RETENTION_FAILURE_FIRST`; critical also `DEPENDENT_PREREQ_VERIFICATION_REQUIRED`) |
| `fresh`/`stable` | strong check on or after the due day | `stable`, interval grows, next review from that day |
| `fresh`/`stable` | strong check before the due day | unchanged clock, `NATURAL_REUSE_VERIFIED` (§11) |
| `fresh`/`stable` | uncertain result to a due check | `at_risk` (§10) |
| `verification_due` | fresh strong check ≥ 1 study day later | `stable`, **interval not grown**, next from that day (`RETENTION_RECHECK_PASS`) |
| `verification_due` | another clean failure, mastery still holds | `verification_due` (`RETENTION_RECHECK_FAIL`) |
| `verification_due` | uncertain result ≥ 1 study day later | `at_risk` |
| `at_risk` | fresh strong check | `stable` (`RETENTION_RECHECK_PASS`) |
| `at_risk` | clean failure | `verification_due` |
| any mastered | the mastery engine's gates stop passing | `untracked` (`REMEDIATION_AFTER_RETENTION_FAILURE`, after a recheck also `RETENTION_RECHECK_FAIL`) |

**Clean** is `RVR-v0` §3 without the delay: not contested, prerequisite-valid, no solution exposure, direct for its Objective, verified, independent (H0). **Strong** is a clean positive that is not a near repeat for a complex or critical Skill (§13). A **near repeat** is a row whose item or variant family this Skill already met. A recheck after a failure must not be a near repeat for any profile: `RVR-v0` §9 asks for a fresh, unseen recheck.

**Uncertain** is an attributable independent result that is either `partial` and verified, or from a provisional evaluator. It raises a concern only as the answer to a due check or a pending verification; a partial result long before any review is due is ordinary mastery evidence, not a retention concern, and `at_risk` never comes from time.

`review_due` is **never stored by a replay**. `RetentionSnapshot.axisOn(day)` derives it: a `fresh` or `stable` Skill whose next review day has come is `review_due`. `verification_due` and `at_risk` are not changed by time, and nothing is ever downgraded because a day passed.

An unknown authored profile schedules nothing: the Skill's axis stays `not_yet_evaluated`, which the prerequisite gate already names and does not read as bad news.

---

# 5. The schedule (`RVR-v0` §5, §6, §9, §20)

The numbers are the accepted spec's own V0 values, labelled **engineering heuristic / needs calibration** in `RetentionPolicyV0`; none was chosen here.

| Parameter | V0 |
|---|---:|
| initial review: factual / standard / complex | 2 / 4 / 7 study days |
| critical initial cap | 3 study days |
| growth: standard / critical | ×2.0 / ×1.6 |
| maximum interval: standard / critical | 180 / 90 study days |
| verification separation | 1 study day |

Two implementation choices are declared, not hidden:

- **Growth rounds down to whole study days.** A shorter interval asks for an earlier check, never a later one; with every initial interval at least two days, the interval still grows at each step (2 → 3 at ×1.6).
- **The verification separation is a rule about credit, not a lock.** The planner may offer a recheck whenever it fits; only a recheck at least one study day after the failure resolves it (`RVR-v0` §9: "kullanıcı katı 24 saat kilidine alınmaz").

All days are the learner-local study days the rows recorded (`DDM-v0`), never computed from an instant.

---

# 6. Rebuilding and refreshing

**`RebuildRetention`** (core-application) reads the watermark first, then the Skill's evidence across its Objectives, and replays the mastery engine after every row exactly as `RebuildMastery` would have decided it: the previous axis feeds the next decision. The resulting events go to the retention engine, and the snapshot is written as of today. A row without a study day is refused rather than placed at a guessed time; with nothing published nothing is written.

**`RefreshDueRetention`** is the only thing time does. It asks the store for `fresh`/`stable` schedules whose review day has come (the indexed due query), moves each to `review_due` with the **same watermark** it had — no truth was read, only the day — and names any stored row it cannot decode instead of guessing it into a state.

**The planner** calls the refresh at the start of `BuildDailyPlan`, before it reads Skill states, so a plan is never made on yesterday's schedule. This refreshes an input; it changes no planner rule. The planner already opens `retention_review_due` from a confirmed Skill whose axis is `review_due` (P3, critical P2 by `PBR-v0`), and `verification_due` from either axis; the prerequisite gate already reads `review_due` as `ready_due` (never blocking) and a critical `verification_due` as blocking dependent new work (`RVR-v0` §14, D-031). 13A's `retention_due` and 13B's `delayed_retention_sampling` / critical revalidation roles read the same needs.

---

# 7. Storage (`RVR-v0` §18, §19)

**Schema version 5** completes `retention_state`, forward-only and in one transaction:

- the columns are `RVR-v0` §18's fields: `retention_profile`, `critical_prerequisite`, `current_interval_days`, `next_review_on_study_day`, `last_strong_retention_on_study_day`, `last_retention_evidence_id`, `successful_delayed_review_count`, `unresolved_verification_evidence_id`, `verification_failure_on_study_day`, `at_risk_reason_codes`, `last_natural_reuse_on_study_day`, `reason_codes`, `as_of_study_day`;
- `retention_due (next_review_on_study_day)` is §19's due index;
- two triggers refuse a `state` outside `RVR-v0`'s axis plus `not_yet_evaluated`: the insert guard also stops an upsert (SQLite runs it before the conflict resolves), and the update guard stops any direct `UPDATE`.

Rows written before v5 gain empty columns; nothing had written one. Migration was tested against a **populated** schema-4 database: every truth row of every table unchanged.

**`skill_state`.** Retention writes its own axis and carries the other three exactly as their engines left them. The row is stamped with the **older** of its existing watermark and retention's, so it never claims to have seen more truth than any of its axes did; its policy version stays the mastery engine's. (12A's `RebuildMastery` still stamps its own watermark when it carries the other axes; that is unchanged and recorded as an open loop.)

---

# 8. Ports

No new interface; the port count stays four.

- `PersistencePort.retentionDueBy(studyDay)` — the `fresh`/`stable` schedules whose review day has come, by the index.
- `EvidenceRow.studyDay` — the row's own recorded study day, read from the column the store already kept.

---

# 9. Living gates narrowed

- `E13B-10_schema_version_4` (13B validator) pinned `VERSION = 4`; it now requires version 4 or later with every later version owned by its step's contract — the same narrowing 13A applied to the 12x gates.
- 13B's storage test `MonthlySessionStorageTest` asserted `Schema.VERSION == 4`; it now asserts at least 4.
- The five 12x `schema_version_unchanged` gates and `E13A-11_schema_version_3` needed no change: they already require an owning contract, and the 13C contract owns v5.

No guarantee was weakened.

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

JVM tests: 654, all passing.

**Mutation 47/47, and only the retention suites ran** — every mutant broke a retention rule in the V0 numbers, the due derivation, the transitions, cleanliness, near repeats, recheck rules, the rebuild's mastery replay, the `skill_state` watermark, the refresh, the planner bridge, the due query, the value-set trigger, the migration or the evidence day; every mutant had to compile, a run with no Gradle verdict is refused, and a comment-only control survives. **Three first-run mutants survived, and each exposed a test that proved less than it claimed:** R25 and R26 (assisted and contaminated evidence counted as clean) survived because the engine test built its weak checks before the mastery row, so the replay ordered them before mastery and ignored them for that reason alone; the test now builds each weak check after its mastery row and shows the same check, clean, does count. R45 (the update guard removed) survived because SQLite runs the insert guard before an upsert's conflict resolves; the storage test now also issues a direct `UPDATE`, the one path the update guard alone protects. All three are caught, and the whole set was run again from one unchanged tree. Details in `arch/13c_spaced_repetition/retention.yaml` (`mutation_results`).

**Not run: T6.** Nothing in the app rebuilds engines after evidence or calls the planner yet (16D).

---

# 11. Open, and owned elsewhere

- **Rebuilding engines after evidence** (mastery → retention → readiness) from the app → 16D.
- **Topic `weakening`** from retention concerns (`RVR-v0` §15) and remediation → 13D.
- **`RebuildMastery` stamps its own watermark** when carrying other axes on `skill_state` → 16D, with the orchestration that rebuilds them together; the prerequisite axis on `skill_state` (presentation only; the gate computes readiness live) → 16C.
- **Overdue urgency buckets** and the success band → 18C; **representative checks after absence** → 18B.
- **Solution exposure from prior exposure records** is not yet joined into evidence rows; today it is covered by the independence class and by item selection refusing seen items → 14 (evaluation) / 18D.
- **Runtime of the per-row mastery replay** (quadratic in a Skill's rows, rebuild only) → 18E.

---

# 12. Anti-patterns explicitly rejected

- mastery that decays with time, or a due review treated as forgetting,
- a lock, a block or a risk raised because a day passed,
- a probability, half-life or forgetting curve presented as known,
- a schedule invented for an unknown profile,
- a near variant or a seen item counted as a strong complex or critical review, or as a recheck,
- one failure erasing mastery, or a recheck pass growing the interval,
- a cluster or descendant refresh from one task (each Skill is replayed from its own evidence),
- a backlog of old reviews turned into debt,
- a `skill_state` row claiming more truth than its oldest axis saw,
- an unreadable stored schedule guessed into a state.

---

# 13. 13C acceptance contract

1. `RVRX-v0` is the accepted retention implementation.
2. The axis, transitions, V0 numbers and reason codes equal `RVR-v0` (§2, §5–§10, §17, §20).
3. Time only derives `review_due`; nothing else changes with time.
4. Only clean independent checks move retention; near repeats and seen items cannot carry complex/critical reviews or rechecks.
5. Retention is rebuilt from evidence with the mastery engine's per-row decisions; the rebuild writes no truth.
6. The planner refreshes due reviews before planning and opens the existing needs; no planner rule changed.
7. Schema v5 completes `retention_state`, tested against a populated database; the port count stays four.
8. Narrowed living gates keep their guarantees.
9. Nothing is claimed that was not run; T6 was not run.
10. Independent 13C QA passes and the full `validate_*.py` sweep passes.

---

# 14. Handoff after acceptance

If accepted, 13C becomes `RVRX-v0 / D-101`.

Next numbered step: **13D — Remediation Engine**. It must receive a fresh PRE-STEP and explicit user approval before execution. 13D owns the weakness axis, remediation closure, Topic `weakening` and retroactive contamination; retention now hands it `verification_due`, `at_risk` and the `REMEDIATION_AFTER_RETENTION_FAILURE` signal.
