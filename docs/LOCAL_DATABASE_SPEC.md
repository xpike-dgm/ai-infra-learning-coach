# Local Database Specification — LDBX-v0

**Stage step:** 10D — Local database  
**Status:** ACCEPTED — independent 10D QA PASS  
**Decision:** `D-085`  
**Model:** `LDBX-v0 — Local Database`  
**Data model:** `DDM-v0 / D-077`  
**Persistence:** `LFPS-v0 / D-076`  
**Verification strategy:** `TVSX-v0 / D-081`

## 1. Purpose

10D builds the product's store: `DDM-v0`'s physical schema as a real SQLite database.

It answers one primary question:

> **Hangi fiziksel şema, `DDM-v0`nin garantilerini atlatılamaz kılar?**

Primary invariant:

> **The storage engine refuses what the architecture forbids.** Append-only truth, immutable curriculum, version pinning and permanent exposure are not rules calling code is trusted to follow; they are statements SQLite rejects — and each rejection is proven by attempting it.

---

# 2. What the first draft got wrong

This step's first schema was written from memory of earlier steps, not from the contract, and its own tests were written against that same draft — so they passed. Cross-reading the draft against `DDM-v0`'s `data_model.yaml` while writing the validator showed it was wrong in ways that mattered:

| Draft | Accepted in `DDM-v0` |
|---|---|
| outcome `met / partially_met / not_met / not_reliably_measured` | `positive / negative / partial / invalid` |
| `evaluator_status` without `invalid` | `verified / provisional / invalid` |
| `independence_class` = `independent / assisted` | adds `practice_only`, `requires_independent_recheck` |
| UTC offset in **seconds** | `utc_offset_minutes` |
| evidence keyed to one objective | Skill reference + plural pinned objective references |
| 4 truth tables | 12 truth entities |

The outcome mistake is the instructive one: it reused the evaluator **signal** enum from 10A for the evidence **outcome** axis, two different concepts that happened to look alike. The schema was rewritten to the contract before acceptance. The divergences are recorded rather than erased, because the durable lesson is that **a suite written against a draft cannot catch that draft's misreading of the contract** — only a check reading the contract can.

---

# 3. Scope boundary

## 3.1 10D decides

- the physical tables for every `DDM-v0` entity across the three store regions,
- how append-only truth and immutable curriculum are enforced,
- constrained value sets, three-value time and the offset unit,
- the global truth sequence and projection watermark,
- projection provenance and the metadata table,
- the `DDM-v0` index set,
- forward-only transactional migration,
- how the adapter derives column requirements,
- the T2 suite and its mutation results.

## 3.2 10D does not decide

- engines that compute projections → 12,
- curriculum content loading → 11,
- backup, export and atomic verified restore implementation → mechanism 10E (`APHX-v0`), Profile controls 16D,
- moving the database open off the main thread and surfacing `data_recovery_required` as a state → 10E,
- assessment-session fields → 13,
- index tuning and query budgets → 18E,
- any accepted semantic, persistence rule, data model or boundary.

No row-count, latency or database-size claim is canonical in 10D.

---

# 4. Engine

`androidx.sqlite` 2.7.0 with the bundled driver and no ORM. Its API was **read from the resolved jar with `javap`**, not written from memory — the documentation page did not render, and 10A had already shown twice what remembered APIs cost.

The same schema runs in two places:

- on the **JVM**, through `sqlite-bundled-jvm`, which is what lets `TVSX-v0` tier T2 use a real storage engine with no device,
- on the **device**, through `sqlite-bundled-android`. This was verified inside the built APK — `lib/arm64-v8a/libsqliteJni.so` is packaged for the Poco M6 Pro's ABI — because a JVM library consumed by an Android app can build successfully and still crash on the phone if the wrong variant is resolved.

---

# 5. Store regions

| Region | Tables | Mutability |
|---|---|---|
| curriculum | 11 — every `DDM-v0` curriculum entity | immutable: no UPDATE, no DELETE |
| user truth | 13 — the 12 `DDM-v0` truth entities + `evidence_event_objective` | append-only: no UPDATE, no DELETE |
| user projection | 8 — every `DDM-v0` projection entity | droppable and rebuildable |

**No foreign key crosses from user truth into curriculum.** A curriculum update must never reach what a learner has demonstrated. A test enumerates every truth table's foreign keys to keep it that way.

`evidence_event_objective` is the relational form of `evidence_event`'s plural `objective_logical_ids` / `objective_versions`: one row per targeted Objective, each with its own version pin, so `evidence_by_objective` can be indexed. It is not a new entity.

---

# 6. Enforcement

**Every truth table and every curriculum table carries a `BEFORE UPDATE` and a `BEFORE DELETE` trigger that aborts.** The triggers are generated from the table inventories, so no table can be left out by accident.

A correction to evidence is an appended `evidence_disposition`; the original row is checked to be byte-for-byte unchanged afterwards. A change to curriculum is a new version.

**Constrained values.** Every `DDM-v0` allowed set — the four evidence axes, dispositions and who decided them, assistance level/timing/scope/source, provenance origin, exposure kind — is a `CHECK` constraint built from the same Kotlin list the validator compares with the contract.

**Pinning.** Versioned entities key on `(logical_id, version)`; foreign keys carry the version column; a resource reference on evidence is pinned or absent, never version-free; a projection cannot be addressed by logical id alone.

---

# 7. Time

Every timestamped row stores `occurred_at_instant`, `occurred_on_study_day` and `utc_offset_minutes` — with `DDM-v0`'s own names where it gives them (`decided_at_*` on dispositions, `answered_at_*` on provenance).

The core time type carries the offset in seconds; storage carries minutes. Conversion is exact division, and **an offset that is not a whole number of minutes is refused rather than truncated**. Every real zone offset is a whole number of minutes, so the refusal only ever fires on corrupt input — which is exactly when a silent truncation would do the most damage.

---

# 8. Sequence, projections and metadata

A **single global truth sequence** advances for every truth row of every kind and is the projection watermark. A per-table id could not tell a projection that an exposure arrived after it was built; this can. A rolled-back action does not advance it.

Every projection row carries `policy_version`, `truth_watermark`, `built_at_instant` and `input_curriculum_version`. The port's `ProjectionRecord` gained the last two, so the provenance is part of the type rather than smuggled through a payload. `skill_state` stores the four axes separately and the primary presentation state beside them, never instead of them.

`schema_metadata` stores the schema version and the policy version.

---

# 9. Migration

- **Forward only.** A database from a newer schema is refused, never opened on a guess.
- **All or nothing.** Each step runs in one transaction; a failure part way through triggers `ROLLBACK`, leaves the previous state intact and raises `DataRecoveryRequired`.
- **A real forward step exists.** Version 2 adds the nine `DDM-v0` read-path indexes, so migration is exercised on a populated database rather than described.

The populated fixture holds 25 evidence events, 10 exposure records and a disposition. After migration, evidence is compared **by content, row for row**, not sampled.

---

# 10. The adapter reads the schema

The adapter does not keep a list of required columns. It asks SQLite (`PRAGMA table_info`) for each table's columns and requires every `NOT NULL` column without a default that it does not own itself. A second hand-maintained list is exactly the thing that drifts from a schema quietly; this way the DDL is the only source.

It owns the id, the sequence and the time columns. Everything else must arrive in the payload, and a failed action rolls back and rethrows.

---

# 11. Columns the data model does not name

`DDM-v0` names some entities without listing their fields. For those, only identity, sequence, time and DDM-implied references are fixed, plus the smallest content column a row needs — each disclosed with the step that owns completing it: `assessment_session.scope` (13), `artifact.content_ref` (11), `planned_task.position` (12), `planner_decision_trace.trace` (12), `resume_checkpoint.context` (11), `plan_version.policy_version` (12), and `name` on `domain`/`module`/`topic` (11). Nothing here claims more about those entities than the model does.

---

# 12. T2 and mutation

22 T2 checks run on the JVM against real SQLite — no fake repository. Every prohibition is proven by executing the forbidden statement and requiring the refusal.

Nine deliberate mutations were applied to the implementation, and **all nine were caught**: truth triggers removed, curriculum triggers removed, the outcome value set unchecked, a version-free resource reference allowed, rollback removed from a learner action, migration made non-transactional, a newer schema opened on a guess, the offset silently truncated, and the truth sequence frozen.

One of them was not caught at first. The migration-failure test originally failed its sabotaged migration on the very first statement, before anything had changed — so replacing `ROLLBACK` with `COMMIT` still passed. The test was rewritten to inject the failure **after** the step has created its indexes and cleared the metadata row, and it now catches the mutant. Without mutation testing, a test that verified nothing would have been recorded as verifying atomicity.

---

# 13. What was actually run

| Run | Command | Tier | Result |
|---|---|---|---|
| RUN-01 | `./gradlew :data-persistence:test` | T2 | PASS — 22 tests |
| RUN-02 | `./gradlew verifyModuleBoundaries` | T3 | PASS |
| RUN-03 | `./gradlew :app-wiring:assembleDebug` | T5 | PASS — Android variant + arm64-v8a native library verified in the APK |
| RUN-04 | `./gradlew :app-wiring:assembleDebug -PwithAiAdapter=false` | T5 | PASS |

---

# 14. Anti-patterns explicitly rejected

- an update or delete path on a truth table,
- a correction by edit instead of an appended disposition,
- overwriting or deleting a curriculum row,
- a foreign key from user truth into curriculum,
- a reference by logical id alone,
- collapsing the evidence axes into one column,
- storing only the instant or only the local date,
- silently truncating a UTC offset,
- a projection row without provenance,
- a migration that can apply partially,
- opening a newer schema on a guess,
- migrating only an empty database,
- a hand-maintained column list beside the schema,
- guessing a library API instead of reading it,
- inventing entity fields the data model does not name.

---

# 15. 10D acceptance contract

1. `LDBX-v0` is the accepted local database.
2. Every `DDM-v0` entity has a table in its region; the one extra table is the relational form of plural references, not a new entity.
3. Truth and curriculum tables refuse UPDATE and DELETE at the storage layer, proven by attempting both.
4. Every allowed value set equals `DDM-v0`'s and is enforced by a constraint.
5. Versioned identity is composite, references carry versions, and no user→curriculum foreign key exists.
6. Timestamped rows carry instant, study day and offset in minutes; a non-whole-minute offset is refused.
7. One global monotonic truth sequence is the projection watermark.
8. Every projection row carries full provenance, and the port type carries it too.
9. Migration is forward-only and transactional, and is tested against a populated database by content.
10. The adapter derives column requirements from the database, not from its own list.
11. The same schema runs on the JVM for T2 and on the device, verified in the APK.
12. Columns the model does not name are disclosed with their owners.
13. 22 T2 checks pass and 9/9 mutations are caught.
14. The first draft's divergences from the contract are recorded.
15. Nothing is claimed that was not run.
16. Independent 10D QA must pass, and Stage 6, 7, 8, 9 and 10A–10C regressions must pass.

---

# 16. Handoff after acceptance

If accepted, 10D becomes `LDBX-v0 / D-085`.

Next numbered step: **10E — Temel uygulama sağlığı**. 10E moves the database open off the main thread, turns `DataRecoveryRequired` from a crash into the `data_recovery_required` state `SPWX-v0` already defines, gives the app a truthful startup and degraded-state path, and closes AŞAMA 10. It must receive a fresh PRE-STEP and explicit user approval before execution.
