# Domain Data Model Specification — DDM-v0

**Stage step:** 9C — Domain veri modeli  
**Status:** ACCEPTED — independent 9C QA PASS  
**Decision:** `D-077`  
**Model:** `DDM-v0 — Domain Data Model`  
**Persistence:** `LFPS-v0 / D-076`  
**Platform:** `AMTS-v0 / D-075`

## 1. Purpose

9C makes `LFPS-v0`'s persistence architecture concrete at **entity, field, relation and physical-schema level**.

It answers one primary question:

> **Kabul edilmiş kontratlar tabloya, alana ve ilişkiye nasıl döner — hiçbir garanti kaybolmadan?**

Primary invariant:

> **The schema enforces the architecture. Every `LFPS-v0` guarantee — truth versus projection, append-only, exposure permanence, version pinning — is structural here, not a convention.**

---

## 2. Binding inputs

- `LFPS-v0 / D-076` — the truth/projection split and its exact record lists, append-only truth, marked-not-erased invalid evidence, exposure permanence, curriculum version pinning, atomic writes, forward-only migration.
- `KGC-v0` — `Skill`, `Objective`, `TopicSkillLink`, `SkillPrerequisiteEdge` contracts; `entity_version` on every published entity; published versions never overwritten. It routes the physical schema **to this step**.
- `GNS-v0` — logical ID format: lowercase ASCII dotted namespace, locale/order/version independent; week, stage, release, band, difficulty and role may not appear inside an ID.
- `QAB-v0` / `AIV-v0` — logical item identity, content version and user exposure history are separate; validation status; solution exposure affects that user's evidence freshness.
- `GRE-v0` — the `EvidenceEvent` field contract, explicitly handed to 9C.
- `RVR-v0` / `PRG-v0` / `TSM-v0` / `WLRM-v0` / `TEPM-v0` — retention, readiness, Topic, weakness and English profile vocabularies; all are projections.
- `D-034` — `LearningNeed`, `TaskCandidate`, `PlannedTask`, `Attempt`, `Artifact`, `Evidence` are distinct; lifecycle state is not evidence.
- `PDT-v0` / `SRR-v0` / `TRUX-v0` / `ASUX-v0` / `SPWX-v0` — traces, resume context, assistance and provenance, assessment sessions, and the four Skill state axes.
- `AMTS-v0` — core purity constrains the types the core-visible model may use.

9C changes no accepted semantic, state, label, tone, geometry or persistence rule.

---

# 3. Scope boundary

## 3.1 9C decides

- entity families and their store placement,
- entity fields and relations,
- identity and versioning keys,
- how append-only, disposition and pinning are expressed structurally,
- time representation,
- projection provenance,
- exposure indexing,
- library-neutral physical schema: tables, keys, constraints, index intent,
- history growth shape.

## 3.2 9C does not decide

- ORM/mapping library and its annotations → 10A,
- concrete migration scripts → 10A/12,
- module and service boundaries → 9D,
- AI integration → 9E,
- test strategy, including how append-only is enforced and verified → 9F,
- encryption at rest → 19,
- any accepted AŞAMA 8 semantic, state, label, tone or geometry.

No row-count budget, query latency target, storage size estimate or specific index implementation is canonical in 9C.

---

# 4. Store layout

One database, three logical regions, and the split is **in the schema**, not in convention.

```text
curriculum_store    versioned, immutable once published
user_truth_store    append-only, never updated or deleted
user_projection_store   rebuildable cache, freely discardable
```

Binding rules:

- a curriculum row is never overwritten once published,
- a truth row has no in-place mutation path,
- a projection row may be deleted and rebuilt at any time,
- no foreign key points from the curriculum store into a user store.

---

# 5. Identity and versioning

## 5.1 Logical IDs

Logical IDs follow `GNS-v0`: lowercase ASCII, dotted namespace, `snake_case` segments, independent of locale, ordering and version. Week, stage, release, band, difficulty and role never appear inside an ID.

## 5.2 Versioned identity

Every published curriculum entity and every assessment resource is keyed by:

```text
PRIMARY KEY (logical_id, version)
```

## 5.3 Pinning is structural

Every user-store reference to curriculum or to an assessment resource carries **both** the logical ID and the version.

```text
evidence_event.skill_logical_id    +  evidence_event.skill_version
attempt.resource_logical_id        +  attempt.resource_version
```

A reference by logical ID alone is forbidden. Without the version in the key, a reference silently re-points at the newest version the moment curriculum is updated — which is exactly the silent state change `KGC-v0` forbids and `LFPS-v0` was written to prevent.

---

# 6. Curriculum store

| Entity | Key | Notes |
|---|---|---|
| `curriculum_version` | `version` | manifest: published_at, source refs, provenance |
| `domain` | `(logical_id, version)` | organization layer |
| `module` | `(logical_id, version)` | organization layer |
| `topic` | `(logical_id, version)` | organization layer |
| `skill` | `(logical_id, version)` | capability identity per `KGC-v0` |
| `objective` | `(logical_id, version)` | exactly one parent Skill |
| `topic_skill_link` | `(topic_logical_id, topic_version, skill_logical_id, skill_version)` | many-to-many reuse |
| `skill_prerequisite_edge` | `(prerequisite_*, target_*, edge_version)` | `edge_kind: hard \| soft` |
| `assessment_resource` | `logical_id` | stable item identity |
| `assessment_resource_version` | `(logical_id, version)` | content, rubric ref, evidence type, allowed tools |
| `resource_validation_record` | `(logical_id, version, validated_at)` | `validation_status`, validator, origin |

`skill` carries the `KGC-v0` field set: canonical name, capability statement, lifecycle status, capability kind, retention profile, critical-prerequisite flag, remediation/professional/project tags, source refs, provenance, freshness policy ref and aliases.

`objective` carries its evidence profile: required flag, criticality, acceptable and direct evidence types, required direct type.

---

# 7. User truth store — append-only

Truth entities, matching `LFPS-v0` exactly:

| Entity | Purpose |
|---|---|
| `attempt` | one learner attempt at a boundary |
| `artifact` | produced output, with origin |
| `evidence_event` | interpreted evidence for target Objectives |
| `assistance_event` | one granted assistance, with level/timing/scope/source |
| `artifact_provenance` | asked-not-inferred origin answer |
| `exposure_record` | what this learner has seen |
| `assessment_session` | one session, its blocks and boundaries |
| `planner_decision_trace` | why the planner chose what it chose |
| `plan_version` / `planned_task` | what was planned at a point in time |
| `resume_checkpoint` | `SRR-v0` `ResumeContext` |
| `evidence_disposition` | a later statement about an earlier evidence row |

## 7.1 `evidence_event`

Fields follow the `GRE-v0` contract handed to this step: id, timestamps, skill and objective references with versions, evidence type, source task/resource/artifact references, variant family, outcome, correctness/rubric result, difficulty, novelty/familiarity, assistance context, duration, prerequisite snapshot, delay since last exposure, evaluator and provenance, misconception/error tags, artifact reference.

## 7.2 Four axes stay four columns

An evidence row carries four **independent** facts, and each is its own column:

```text
outcome              positive | negative | partial | invalid
evaluator_status     verified | provisional | invalid
independence_class   independent | assisted | practice_only | requires_independent_recheck
contested            true | false
```

Collapsing any pair destroys a guarantee. Folding `evaluator_status` into `outcome` makes a provisional result indistinguishable from a settled one; folding `independence_class` into `outcome` erases the difference between "did it alone" and "did it after seeing the answer".

## 7.3 Correction is an append

There is no `UPDATE` path on a truth row. A later judgement — invalidation, contest, supersession — is written as an `evidence_disposition` row referencing the original.

```text
evidence_disposition
- evidence_event_id
- disposition: invalidated | contested | superseded | reinstated
- reason_code
- decided_at_instant / decided_on_study_day
- decided_by: deterministic_rule | validator | user_report
```

Recomputation reads dispositions and excludes by rule, never by absence.

---

# 8. Exposure

```text
exposure_record
- id
- resource_logical_id + resource_version
- variant_family_id
- exposure_kind: solution_exposure | item_version_seen | variant_family_exposure
- max_exposure_level
- occurred_at_instant / occurred_on_study_day
- source_attempt_id?
```

The guarantee "never serve a solution-exposed item as a fresh independent check" is a **lookup on the hot path of item selection**, not merely a retention promise. The model therefore indexes exposure by resource logical ID, by variant family and by exposure kind so that check is cheap enough to always run.

Exposure rows are never deleted, and are exported and migrated with truth records.

---

# 9. Time

Every timestamped row stores **three** values:

```text
occurred_at_instant     UTC epoch — for interval arithmetic
occurred_on_study_day   learner-local date — for daily planning and history
utc_offset_minutes      the offset in effect at that moment
```

This product plans by learner-local day, schedules retention by elapsed time, and must survive timezone travel and daylight-saving changes.

- Storing only the instant loses **which study day** an attempt counted as; after a DST change or travel, evidence silently moves between days and the daily plan and history disagree with what the learner actually did.
- Storing only the local date loses interval arithmetic, which retention depends on.
- Neither reliably derives from the other after the fact, because the offset in effect is not recoverable from the stored value alone.

All three are recorded at write time.

---

# 10. Projection store — rebuildable

| Projection | Grain |
|---|---|
| `skill_state` | per Skill: the four `SPWX-v0` axes plus derived presentation state |
| `objective_state` | per Objective |
| `retention_state` | per Skill |
| `prerequisite_readiness` | per Skill |
| `topic_state` | per Topic |
| `weakness_state` | per Objective |
| `english_profile` | per D01 Skill plus qualified band summary |
| `planner_summary` | derived planner-visible summaries |

## 10.1 The four axes are stored separately

`SPWX-v0` requires `skill_detail` to keep the mastery, retention, prerequisite and weakness axes individually inspectable. `skill_state` therefore stores each axis as its own column alongside the derived presentation state — the presentation state never replaces them in storage any more than it does on screen.

## 10.2 Projection provenance

Every projection row records what built it:

```text
- policy_version
- truth_watermark          highest truth-record sequence included
- built_at_instant
- input_curriculum_version
```

A rebuildable projection is only trustworthy if staleness is **detectable**. Without a watermark, a stale projection is indistinguishable from a current one and `recomputing_projection` has nothing to compare against.

---

# 11. Physical schema shape

Expressed library-neutrally, because the ORM is a 10A decision.

- one table per entity; no polymorphic catch-all table,
- composite primary keys on versioned entities,
- foreign keys carry the version column wherever they reference a versioned entity,
- enum-like fields are constrained string columns with a documented allowed set,
- truth tables carry a monotonic sequence column used as the projection watermark,
- projection tables are droppable and rebuildable without touching truth tables,
- schema version and policy version are stored in a metadata table.

Index intent, stated as the query shapes that must stay cheap:

- evidence by Skill, by Objective, and by time range,
- exposure by resource logical ID, by variant family, by exposure kind,
- attempts by assessment session,
- planned tasks by plan version,
- dispositions by evidence event.

Concrete index definitions and their tuning belong to implementation and 18E.

---

# 12. History growth

- Truth tables grow without pruning, per `LFPS-v0`.
- Growth is append-only, so writes stay cheap regardless of history size.
- Read cost is controlled by index shape and by bounded query windows in the engines, not by deleting history.
- Projection tables stay small: they are keyed by entity, not by event.

---

# 13. Core-visible types

Per `AMTS-v0`, no platform type appears in the model the core sees.

- identifiers are strings,
- instants are epoch values,
- study days are ISO date strings,
- enum-like fields are constrained strings,
- no SQLite, Android, ORM or filesystem type appears in a core-visible entity.

---

# 14. Anti-patterns explicitly rejected

- a mutable `outcome` column with no disposition table,
- any `UPDATE` or `DELETE` path on a truth table,
- soft-delete flags on truth rows instead of disposition records,
- a curriculum reference without its version column,
- a foreign key from the curriculum store into a user store,
- collapsing outcome, evaluator status, independence class or contested into fewer columns,
- storing only a UTC instant, or only a local date, on a timestamped row,
- a projection row without policy version and truth watermark,
- deleting or archiving exposure rows,
- an exposure design that makes the selection-time lookup expensive,
- a polymorphic catch-all table for heterogeneous entities,
- ORM annotations or dialect-specific DDL locked in this step,
- a platform type in a core-visible entity,
- overwriting a published curriculum or resource version,
- deciding boundaries, AI integration, test strategy or encryption here.

---

# 15. 9C acceptance contract

9C can be accepted only if independent QA verifies at minimum:

1. The schema expresses the three store regions and no curriculum→user foreign key exists.
2. Every `LFPS-v0` truth record has a corresponding truth entity, and every declared projection has a projection entity.
3. Truth entities have no in-place mutation path; correction is an appended disposition record.
4. Versioned entities are keyed by `(logical_id, version)` and every user reference to curriculum or a resource carries the version.
5. Logical IDs follow `GNS-v0` format rules.
6. `evidence_event` covers the `GRE-v0` field contract.
7. Outcome, evaluator status, independence class and contested are four separate fields.
8. The four `SPWX-v0` Skill axes are stored separately from the derived presentation state.
9. Every timestamped record stores instant, study day and UTC offset.
10. Every projection row records policy version, truth watermark, build time and input curriculum version.
11. Exposure records cover all `LFPS-v0` exposure kinds, are never deleted, and are indexed for the selection-time lookup.
12. Assistance events carry level, timing, target scope, source and whether the user requested them.
13. Artifact provenance uses the accepted origin values.
14. Truth tables carry a monotonic sequence used as the projection watermark.
15. Enum-like fields are constrained strings with documented allowed sets.
16. No ORM annotation, dialect-specific DDL or platform type appears in the model.
17. No row-count, latency or storage number is asserted.
18. 9D/9E/9F/10A/19 boundaries remain open.
19. Stage 6, Stage 7, AŞAMA 8, 9A and 9B accepted contracts still validate.

---

# 16. Handoff after acceptance

If accepted, 9C becomes `DDM-v0 / D-077`.

Next numbered step:

**9D — Servis sınırları**

9D will define module and service boundaries over this model: where the pure-Kotlin core ends, how persistence, assessment, planner, AI and UI layers are separated, and which direction every dependency points. It must receive a fresh PRE-STEP and explicit user approval before execution.

---

**13F note (2026-10-01, `D-104`):** the projection inventory is extended by one entry, `diagnostic_coverage` (per Objective, owned by `VDW-v0`): the coverage waiver and the open diagnostic's progress (schema v7). `VDW-v0` had no implementation step when this model was accepted; the eight entities above are unchanged. Details: `docs/DIAGNOSTIC_WAIVER_IMPL_SPEC.md`.
