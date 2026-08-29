# 9B Local-First Persistence — Research & Decision Synthesis

**Stage step:** 9B — Veri saklama / local-first  
**Purpose:** Decide how this product's data is stored, versioned, migrated, backed up and recovered, so that years of evidence survive without ever silently changing what the learner has demonstrated.

## 1. Research-need decision

9B chooses a storage engine, which `AI_AGENT_WORKFLOW.md` §3 would normally route to Research AI as a "which should we choose?" question. In practice the accepted contracts narrow it to a single viable class before preference enters:

```text
relational integrity across a versioned curriculum graph
+ append-only evidence queried by Skill, Objective, time and validity
+ transactional all-or-nothing writes across attempt, evidence and derived state
+ years of history with migration and no data loss
+ fully on-device, no server, no network in the read/write path
→ an embedded transactional relational store
```

The **engine class** is therefore decidable here. What is *not* decidable here is the mapping/ORM library on top of it, its current API surface and its migration tooling — that is library currency, which 9A already established belongs to a bounded verification list rather than to an assertion. 9B therefore names the engine and hands the library question to 10A.

Independent QA remains required. Persistence is where a mistake is least visible and most permanent: a lost exposure record or a silently rewritten curriculum version does not crash anything, it just makes the product quietly start lying about what the learner can do.

## 2. Canonical source set reviewed

- `docs/V1_SCOPE.md` §16, §19 — progress preserved across restart and update; curriculum data separated from user state; migration supported; backup, export, restore and migration data protection required; V1 is local-first with no realtime multi-device cloud sync.
- `AMTS-v0 / D-075` — the domain core is pure Kotlin with no Android, UI, network or AI dependency. Any persistence the core uses must therefore be expressed as core-owned interfaces, with the platform implementing them.
- `KGC-v0` — published graph state does not change silently; semantic changes are versioned; learner evidence and history must remain reconstructable against the graph version they were produced under. It also routes the physical schema to 9C.
- `QAB-v0` / `AIV-v0` — logical item identity, content version and user exposure history are kept separate; a published version is never silently altered; an old attempt stays traceable to the exact version it used. Solution exposure affects that user's evidence freshness permanently.
- `GRE-v0` / `RVR-v0` / `PRG-v0` / `WLRM-v0` / `TEPM-v0` — mastery, retention, readiness, weakness and the English profile are all *derived* from evidence; `verification_due` does not delete history; remediation recomputes the current picture from clean evidence; historical confirmed bands are retained.
- `TRUX-v0` / `ASUX-v0` — attempts carry assistance metadata and artifact provenance; `evaluation_pending` writes no evidence; recovery restores the last durable checkpoint and never claims unpersisted work.
- `SPWX-v0` — `recomputing_projection` and `data_recovery_required` are already accepted surface states, so both must correspond to something real in the storage layer.

## 3. Synthesis problems 9B actually has to solve

1. **What is the source of truth.** Mastery, retention, readiness and weakness are all derived. If they are stored as authoritative rows, a migration or a bug can change what the learner has "demonstrated" without any evidence changing. If evidence is the truth and derived state is a projection, that class of corruption becomes impossible by construction.
2. **Curriculum updates must not touch evidence.** The curriculum will change for years. An update that rewrote or reinterpreted past evidence would violate `KGC-v0` directly, and the learner would silently lose or gain capability they never demonstrated.
3. **Exposure records are load-bearing forever.** If a solution-exposed item's exposure record is lost — dropped in a migration, omitted from an export — that item can later be served as a fresh independent check. Nothing crashes; the product just starts producing false independent evidence. This is the quietest possible failure in the whole system.
4. **Partial writes are how a UI starts lying.** An attempt saved without its evidence, or evidence saved without its assistance metadata, produces exactly the state the product spent eight stages forbidding: a claim the evidence does not support.
5. **Core purity versus a platform database.** The core must not depend on Android, but it needs to read and write. The dependency has to be inverted rather than compromised.
6. **Restore must be all-or-nothing.** A half-applied restore is worse than no restore, because the resulting state looks valid.
7. **Years of growth.** History accumulates by design and is never pruned for tidiness, because retention, remediation and verification all read backwards.

## 4. Positions taken

- **Evidence is the source of truth; derived state is a recomputable projection.** Attempts, artifacts, evidence events, assistance metadata, provenance and exposure are **append-only**. Mastery, retention, readiness, Topic state, weakness and the English profile are caches that can be rebuilt.
- **An embedded transactional relational store (SQLite)** is the engine. A document or key-value store would give up the referential integrity and cross-entity queries the planner and evidence pipeline need; flat files would give up transactions and migration entirely.
- **The mapping/ORM library is not chosen here.** It is a 10A verification item, consistent with the 9A precedent on library currency.
- **The core owns persistence interfaces; the platform implements them.** Dependency is inverted so `AMTS-v0` core purity is structural.
- **Curriculum content and user state are separately stored and separately versioned.** User records pin the curriculum version they were produced under; published curriculum versions are retained, not overwritten.
- **Exposure records are permanent and are first-class in backup, export and migration.** Losing one is treated as data loss, not as cache eviction.
- **Writes that belong to one attempt commit atomically or not at all.**
- **Migration is forward-only, versioned, and may never delete or reinterpret evidence.** Rollback is a restore from backup, not a downgrade.
- **Restore is atomic and verified**; a failed restore leaves the previous state intact.
- **Corruption surfaces as `data_recovery_required`**, never as a silent reset.
- **No pruning of evidence in V1.** If retention of storage ever becomes a real constraint, it is a later, explicitly-designed decision — never an implicit one.

## 5. Explicitly not decided in 9B

Entities, fields, relations and the physical schema (9C), module and service boundaries (9D), AI integration (9E), test strategy (9F), the mapping/ORM and migration-tooling libraries (10A), encryption-at-rest posture and any cloud backup target (16/19), and any accepted AŞAMA 8 semantic, state, label, tone or geometry.

No external source in this synthesis justifies a storage size budget, a query performance number, a retention cut-off, or a library version claim.
