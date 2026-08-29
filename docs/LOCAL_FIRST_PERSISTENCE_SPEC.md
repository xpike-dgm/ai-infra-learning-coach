# Local-First Persistence Specification — LFPS-v0

**Stage step:** 9B — Veri saklama / local-first  
**Status:** ACCEPTED — independent 9B QA PASS  
**Decision:** `D-076`  
**Model:** `LFPS-v0 — Local-First Persistence Architecture`  
**Platform:** `AMTS-v0 / D-075`

## 1. Purpose

9B decides **how this product's data is stored, versioned, migrated, backed up and recovered** so that years of evidence survive without ever silently changing what the learner has demonstrated.

It answers one primary question:

> **Kanıt yıllar boyunca nasıl korunur ve hiçbir zaman sessizce değişmez?**

Primary invariant:

> **Evidence is the source of truth. Mastery, retention, readiness, weakness and the English profile are recomputable projections of it. Nothing in the storage layer may change what the learner has demonstrated except new evidence.**

---

## 2. Binding inputs

- `docs/V1_SCOPE.md` §16, §19 — progress preserved across restart and update; **curriculum data separated from user state**; migration supported; backup, export, restore and migration data protection required; local-first with no realtime multi-device cloud sync.
- `AMTS-v0 / D-075` — the domain core is pure Kotlin with **no** Android, UI, network or AI dependency.
- `KGC-v0` — published graph state never changes silently; semantic changes are versioned; learner evidence must stay reconstructable against the graph version it was produced under. Physical schema belongs to 9C.
- `QAB-v0` / `AIV-v0` — logical item identity, content version and user exposure history are separate; a published version is never silently altered; an old attempt stays traceable to the exact version it used; solution exposure permanently affects that user's evidence freshness.
- `GRE-v0` / `RVR-v0` / `PRG-v0` / `TSM-v0` / `WLRM-v0` / `TEPM-v0` — all learner state is derived from evidence; `verification_due` does not delete history; remediation recomputes from clean evidence; historical confirmed bands are retained.
- `TRUX-v0` / `ASUX-v0` — attempts carry assistance metadata and artifact provenance; `evaluation_pending` writes no evidence; recovery restores the last durable checkpoint and never claims unpersisted work.
- `SPWX-v0` — `recomputing_projection` and `data_recovery_required` are accepted surface states and must correspond to real storage-layer conditions.

9B changes no accepted semantic, state, label, tone or geometry, and defines no entity schema.

---

# 3. Scope boundary

## 3.1 9B decides

- what is source of truth and what is projection,
- the storage engine class,
- the dependency direction between core and persistence,
- curriculum/user-state separation and versioning strategy,
- append-only and immutability rules,
- exposure-record durability,
- transactional write boundaries,
- migration policy,
- backup, export and restore behavior,
- integrity, corruption detection and recovery,
- history growth and pruning policy.

## 3.2 9B does not decide

- entities, fields, relations and physical schema → 9C,
- module and service boundaries → 9D,
- AI integration → 9E,
- test strategy → 9F,
- mapping/ORM and migration-tooling libraries → 10A,
- encryption-at-rest posture and any cloud backup target → 16/19,
- any accepted AŞAMA 8 semantic, state, label, tone or geometry.

No storage size budget, query performance number, retention cut-off or library version is canonical in 9B.

---

# 4. Source of truth

## 4.1 Evidence is truth; state is projection

```text
append-only truth        derived projection (rebuildable)
─────────────────        ────────────────────────────────
Attempt                  Skill / Objective mastery
Artifact                 retention state
EvidenceEvent            prerequisite readiness
assistance metadata      Topic state
artifact provenance      weakness / remediation state
exposure record          Technical English profile
assessment session       planner-visible derived summaries
planner decision trace
```

Binding rules:

- Truth records are **append-only**. They are never updated in place and never deleted.
- Derived state is a **cache**. It may be discarded and rebuilt from truth records at any time.
- A rebuild must produce the same result as the original computation for the same inputs and policy version.
- Nothing may change a learner's demonstrated capability except new evidence.

The reason is not elegance. If derived state were authoritative, a migration or a bug could change what the learner has "demonstrated" without any evidence changing. Making evidence the truth removes that class of corruption by construction, and gives `SPWX-v0`'s `recomputing_projection` state something real to describe.

## 4.2 Correction without deletion

Evidence found to be invalid, contested or contaminated is **marked**, not erased. Its disposition is part of the record. Recomputation then excludes it by rule rather than by absence, which keeps the history explicable.

---

# 5. Storage engine

**Decision: an embedded transactional relational store — SQLite.**

Rationale from the contracts rather than preference:

- referential integrity across a versioned curriculum graph and evidence that points into it,
- cross-entity queries by Skill, Objective, time and validity, which the planner and evidence pipeline require,
- transactional all-or-nothing writes,
- a mature forward migration story for years of schema change,
- fully on-device with no server and no network in the read/write path.

Rejected alternatives:

- a document or key-value store — gives up referential integrity and the cross-entity queries the planner needs,
- flat files or serialized snapshots — gives up transactions and any credible migration path.

**The mapping/ORM library is not chosen here.** It is a 10A verification item, consistent with the `AMTS-v0` precedent that library currency is verified with current sources rather than asserted.

---

# 6. Dependency direction

`AMTS-v0` forbids the core from depending on Android. The core still needs to read and write, so the dependency is **inverted**:

```text
core (pure Kotlin)          →  declares persistence interfaces
platform layer (Android)    →  implements them over SQLite
```

Binding rules:

- persistence interfaces are owned by the core and expressed in core types,
- no SQLite, Android or file-system type appears in a core signature,
- the core is runnable and testable against an in-memory or fake implementation with no device,
- exact module layout is 9D's; 9B locks the direction only.

---

# 7. Curriculum and user state

## 7.1 Separated and separately versioned

Curriculum content and user state are stored separately and versioned separately, per V1_SCOPE §16.

- Published curriculum versions are **retained, never overwritten**, per `KGC-v0`.
- User records **pin the curriculum version** they were produced under.
- A curriculum update adds versions; it never rewrites, reinterprets or invalidates existing evidence.
- Historical evidence stays reconstructable against the graph version that produced it.

## 7.2 A curriculum update is not a state change

Installing new or revised curriculum content must never, by itself, change a mastery, retention, readiness, weakness or profile outcome. If a semantic change means a past result no longer applies, that is expressed as a new need through the normal engines — never as a silent rewrite of history.

---

# 8. Exposure records

Exposure records are **permanent, first-class data**.

- Solution exposure, item version seen, and variant-family exposure are retained for the life of the profile.
- They are included in backup, export and migration on the same footing as evidence.
- Losing one is treated as **data loss**, not cache eviction.

The reason is specific: if an exposure record disappears, a solution-exposed item can later be served as a fresh independent check. Nothing crashes — the product simply starts producing false independent evidence. This is the quietest failure mode in the system, and the storage layer is the only place it can be prevented.

---

# 9. Transactional boundaries

One learner action is one transaction.

```text
attempt + artifact + assistance metadata + provenance
+ evidence event + resulting derived-state update
→ commit together, or not at all
```

Binding rules:

- a partial write is never observable,
- an attempt is never persisted without its assistance metadata and provenance,
- `evaluation_pending` writes the attempt but **no** evidence, and that must be atomic too,
- a failed write leaves the previous consistent state intact.

A partial write is precisely how the interface starts claiming something the evidence does not support.

---

# 10. Migration

- Migration is **forward-only and versioned**.
- A migration may **never delete, rewrite or reinterpret** evidence, exposure or provenance.
- Derived state may be discarded and rebuilt by a migration; that is not data loss.
- Every migration is testable against a populated database, not only an empty one.
- Downgrade is not supported. Reverting is a **restore from backup**, not a schema downgrade.
- A migration that cannot complete leaves the previous state intact and surfaces `data_recovery_required` rather than proceeding partially.

---

# 11. Backup, export and restore

## 11.1 Backup and export

- Backup and export are **user-initiated**; V1 performs no automatic cloud upload.
- An export is **complete enough to reconstruct the profile**: truth records, exposure records, provenance, curriculum version pins and preferences.
- Derived state need not be exported, because it is rebuildable — but its absence must not lose information.
- The export records the schema version and policy version it was produced under.
- Export content is the learner's personal data and stays on-device unless the learner moves it.

## 11.2 Restore

- Restore is **atomic**: it fully succeeds or leaves the previous state untouched.
- Restore is **verified** before it replaces anything: structural integrity and version compatibility are checked first.
- A restore from an older schema version runs forward migration; a restore from a *newer* version is refused rather than partially applied.
- Restore never silently merges. Replacing a profile is explicit.

---

# 12. Integrity, corruption and recovery

- Integrity is checked on open, and after migration and restore.
- Detected corruption surfaces the accepted `data_recovery_required` state.
- **Silent progress reset is forbidden.** The system never quietly starts from zero.
- Recovery restores the last durable checkpoint, and offers backup restore when that is insufficient.
- Unpersisted work is never claimed as saved, per `TRUX-v0` and `ASUX-v0`.
- When derived state is inconsistent but truth records are intact, the correct repair is **recomputation**, not reset — surfaced as `recomputing_projection`.

---

# 13. History growth

- History accumulates by design; retention, remediation and verification all read backwards.
- **Evidence is not pruned in V1.** There is no automatic deletion, no rolling window and no tidy-up job.
- Derived state and caches may be discarded freely.
- If storage ever becomes a genuine constraint, pruning is a later, explicitly designed and user-visible decision — never an implicit one, and never for data any current state depends on.

---

# 14. Local-first boundary

- All core read and write paths work with no network.
- No realtime multi-device cloud sync exists in V1.
- Network absence degrades optional capability only, never the deterministic core.
- Persistence never depends on an AI client.

---

# 15. Anti-patterns explicitly rejected

- treating mastery, retention, readiness, weakness or the English profile as source of truth,
- updating or deleting a truth record in place,
- deleting invalid evidence instead of marking its disposition,
- letting a curriculum update rewrite, reinterpret or invalidate past evidence,
- overwriting a published curriculum version,
- dropping exposure records in migration, export or cleanup,
- persisting an attempt without its assistance metadata or provenance,
- writing evidence for an `evaluation_pending` attempt,
- a partially applied write, migration or restore,
- a schema downgrade instead of a backup restore,
- restoring from a newer schema version by best effort,
- silent progress reset on corruption,
- resetting when recomputation would repair the projection,
- pruning evidence implicitly,
- a SQLite, Android or file-system type in a core signature,
- naming a mapping/ORM library or asserting its currency in this step,
- deciding entities, fields, relations or physical schema here.

---

# 16. 9B acceptance contract

9B can be accepted only if independent QA verifies at minimum:

1. Evidence is declared the source of truth and all learner state is a recomputable projection.
2. Truth records are append-only; nothing changes demonstrated capability except new evidence.
3. Invalid evidence is marked rather than deleted.
4. The storage engine is an embedded transactional relational store, justified from contracts rather than preference, with alternatives and their losses recorded.
5. No mapping/ORM library is chosen, and library currency is handed to 10A.
6. Persistence interfaces are core-owned and no SQLite/Android/file-system type appears in a core signature.
7. Curriculum and user state are separately stored and separately versioned, with user records pinning the curriculum version.
8. Published curriculum versions are retained and a curriculum update cannot change learner state by itself.
9. Exposure records are permanent, first-class in backup/export/migration, and their loss is classified as data loss.
10. One learner action is one atomic transaction; attempts always carry assistance metadata and provenance; `evaluation_pending` writes no evidence.
11. Migration is forward-only, never destroys evidence, is tested against populated data, and fails intact rather than partially.
12. Downgrade is refused; reverting is a backup restore.
13. Export is complete enough to reconstruct the profile and records its schema and policy versions.
14. Restore is atomic and verified, refuses newer-schema sources, and never silently merges.
15. Corruption surfaces `data_recovery_required`; silent reset is forbidden; recomputation is preferred to reset when truth records are intact.
16. Evidence is not pruned in V1 and any future pruning is explicit and user-visible.
17. Core read/write works with no network and no AI client.
18. 9C/9D/9E/9F/10A boundaries remain open and no schema is defined here.
19. Stage 6, Stage 7, AŞAMA 8 and 9A accepted contracts still validate.

---

# 17. Handoff after acceptance

If accepted, 9B becomes `LFPS-v0 / D-076`.

Next numbered step:

**9C — Domain veri modeli**

9C will define the domain data model on top of this persistence architecture: granular Skill/Objective state, assessment-resource versions, exposure and validation records, years-long history and curriculum versioning — as entities, fields and relations. It must receive a fresh PRE-STEP and explicit user approval before execution.
