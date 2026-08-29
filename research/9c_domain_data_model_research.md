# 9C Domain Data Model — Research & Decision Synthesis

**Stage step:** 9C — Domain veri modeli  
**Purpose:** Make `LFPS-v0`'s persistence architecture concrete at entity, field, relation and physical-schema level, without weakening a single guarantee it established.

## 1. Research-need decision

9C introduces no new algorithm, policy, threshold or external dependency. Every entity it defines already exists as a behavioural contract somewhere in the repo, and several specs explicitly hand their contract to this step:

- `KGC-v0` §1 — "fiziksel SQLite/Room/DB şemasını kesinleştirmez → 9C".
- `MASTERY_SIGNALS_SPEC.md` closes its `EvidenceEvent` block with "Bu bir database migration değildir; **9C için davranış sözleşmesidir**."
- `EXECUTION_INDEX.md` names 9C's scope directly: granular Skill/Objective state, assessment-resource versions/exposure/validation records, years-long history, curriculum versioning.

A separate external Research AI is **not required**: this is transcription and reconciliation of accepted contracts into a model, not a choice between external options. The one genuinely new engineering decision — how time is stored — is settled from a correctness argument, not from preference.

Independent QA is required, because a data model is where guarantees quietly disappear. `LFPS-v0` can declare evidence append-only, but if the schema gives an evidence row a mutable outcome column and no disposition column, the guarantee is gone and nothing will report it.

## 2. Canonical source set reviewed

- `LFPS-v0 / D-076` — the truth/projection split, the exact truth-record and projection lists, append-only, exposure permanence, version pinning, atomic writes, forward-only migration.
- `KGC-v0` — `Skill`, `Objective`, `TopicSkillLink`, `SkillPrerequisiteEdge` field contracts, `entity_version` on every published entity, and the rule that a published version is never overwritten.
- `GNS-v0` — logical ID format: lowercase ASCII dotted namespace, locale/order/version independent, with week/stage/release/band/difficulty/role forbidden inside the ID.
- `QAB-v0` / `AIV-v0` — logical item identity, content version and user exposure history kept separate; validation status; solution exposure affecting that user's evidence freshness.
- `GRE-v0` — the `EvidenceEvent` field contract, explicitly handed to 9C.
- `RVR-v0` / `PRG-v0` / `TSM-v0` / `WLRM-v0` / `TEPM-v0` — retention, readiness, Topic, weakness and English profile state vocabularies, all of which are projections.
- `D-034` task taxonomy — `LearningNeed`, `TaskCandidate`, `PlannedTask`, `Attempt`, `Artifact`, `Evidence` are distinct; lifecycle state is not evidence.
- `PDT-v0` — `PlannerDecisionTrace`, `PlannerReplanEvent`, plan versioning.
- `SRR-v0` — `ResumeContext` fields.
- `TRUX-v0` / `ASUX-v0` — `AttemptSubmission`, `AssistanceEvent`, artifact origin values, assessment session and atomic evidence boundary.
- `SPWX-v0` — the four Skill state axes that `skill_detail` must keep individually inspectable.
- `AMTS-v0` — core purity, which constrains what types may appear in the model the core sees.

## 3. Synthesis problems 9C actually has to solve

1. **A schema can silently repeal an architecture.** `LFPS-v0` says truth records are append-only and invalid evidence is marked rather than erased. A schema with a mutable `outcome` column and no disposition column satisfies neither, and no test would notice.
2. **Version pinning has to be structural.** "User records pin the curriculum version" is only real if the foreign key carries the version. A reference to a logical ID alone silently re-points at the newest version the moment curriculum is updated — exactly the silent state change `KGC-v0` forbids.
3. **Several independent axes must not be collapsed.** An evidence row carries an *outcome*, an *evaluator status*, an *assistance/independence class*, and a *contested* flag. Collapsing any pair into one column destroys a guarantee: for example, folding evaluator status into outcome makes a provisional result indistinguishable from a settled one.
4. **Time is not one value.** This product plans by learner-local day, schedules retention by elapsed time, and must survive timezone travel and DST. Storing only a UTC instant loses which study day an attempt counted as; storing only a local date loses interval arithmetic. Both are needed, and neither derives reliably from the other after the fact.
5. **Projections need provenance.** A rebuildable projection is only trustworthy if it records which policy version and which input range produced it. Otherwise a stale projection is indistinguishable from a current one and `recomputing_projection` has nothing to compare against.
6. **Exposure must be queryable, not merely stored.** The guarantee is "never serve a solution-exposed item as a fresh independent check", which is a lookup on the hot path of item selection. Retention alone is not enough; the model has to make that lookup cheap.
7. **Years of history versus a phone.** History grows without pruning by design, and the planner reads it constantly. Index shape is therefore part of the correctness story, not an optimisation afterthought.

## 4. Positions taken

- **Two stores, one database**: a curriculum store of versioned, immutable-once-published content, and a user store split into append-only truth and rebuildable projections. The split is expressed in the schema, not in convention.
- **Versioned curriculum entities are keyed by `(logical_id, version)`**, and every user reference to curriculum carries both. Pinning is structural.
- **Truth tables have no in-place mutation path.** Correction happens by appending a disposition record, never by updating a row.
- **Outcome, evaluator status, assistance class and contested status are four separate columns**, because they are four separate facts.
- **Every timestamped record stores both a UTC instant and the learner-local study day** it counted as, plus the timezone offset in effect. The study day is what the daily plan, capacity and streak-free history reason about; the instant is what retention intervals reason about.
- **Every projection row records the policy version and the truth-record watermark it was built from**, so staleness is detectable rather than assumed.
- **Exposure is indexed for the selection hot path** by user, logical item, variant family and exposure level.
- **Physical schema is expressed library-neutrally** — tables, keys, constraints and index intent — because the ORM is a 10A decision. No ORM annotation or dialect-specific DDL is locked here.
- **No entity in the core-visible model uses a platform type.** IDs are strings, instants are epoch values, enums are constrained strings.

## 5. Explicitly not decided in 9C

The ORM/mapping library and its annotations (10A), concrete migration scripts (10A/12), module and service boundaries (9D), AI integration (9E), test strategy including how append-only is enforced and verified (9F), encryption at rest (19), and any accepted AŞAMA 8 semantic, state, label, tone or geometry.

No external source in this synthesis justifies a row-count budget, a query latency target, a storage size estimate, or a specific index implementation.
