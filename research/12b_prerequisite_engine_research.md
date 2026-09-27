# 12B Prerequisite Engine — Research & Decision Synthesis

**Stage step:** 12B — Prerequisite Engine  
**Purpose:** Turn `PRG-v0` into running code: the readiness gate that decides whether a candidate's target evidence would be interpretable and fair, and the one state family `PRG-v0` owns.

## 1. Research-need decision

**No web research pass was needed.** `PRG-v0` was designed at 3D, its hard/soft semantics were grounded in the prerequisite-graph literature then, and 6H closed the graph itself with an external research QA (549/549 hard DAG). What 12B faced was not a missing idea but the gap between a contract written before any code existed and the store, the graph data and the engines that exist now. The work was to find those gaps and close them without inventing anything the contract did not already say.

## 2. Canonical source set reviewed

- `PRG-v0` (`docs/PREREQUISITE_POLICY_SPEC.md`) — the whole gate: Skill→Skill runtime unit (§1), hard and soft (§2), the four readiness values (§3), the hard eligibility matrix (§4), soft eligibility (§5), task-level requirements (§6), the `PrerequisiteDecision` contract (§7), the planner order in which priority cannot override eligibility (§8), dependent-wait / independent-continue (§9), `review_due` is not `not_ready` (§11), verification and remediation (§12), the contamination guard (§13), the English hidden-prerequisite guard (§16), the teach-task exception (§17), the reason inputs (§20), determinism and bounded work (§21), the anti-patterns (§22) and the worked examples (§23) that became tests.
- `KGC-v0` (`docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`) — the edge fields (§12), `strictness_profile` expresses `PRG-v0` metadata and does not rewrite it (§12.2), and the curriculum lifecycle `draft | published | deprecated | retired | invalidated` (§27).
- `RVR-v0` (`docs/RETENTION_FORGETTING_SPEC.md`) — the six retention values `PRG-v0` reads.
- `VDW-v0` (`docs/DIAGNOSTIC_WAIVER_SPEC.md`) — a coverage waiver is not mastery; current competence always comes from `GRE-v0`/`RVR-v0`.
- `SPWX-v0 / D-072`, `TEPM-v0` — the four axes on `skill_state`, and `prerequisite_unresolved` as a presentation state.
- `DDM-v0 / D-077`, `LDBX-v0 / D-085` — `skill_prerequisite_edge`, `skill.critical_prerequisite`, and the `prerequisite_readiness` projection with its provenance.
- `MSBX-v0 / D-078` — `PRG-v0` owns `prerequisite_readiness` and may read mastery, retention and curriculum.
- `MSTX-v0 / D-092` — the mastery axis this gate reads, and the `prerequisite_snapshot` the mastery engine already reads back as contamination.
- The authored graph itself (`curriculum/decomposition/*/prerequisite_edges.yaml`).

## 3. What was found before any code was written

1. **Every authored edge is still `draft`.** All 950 edges in `curriculum/decomposition` carry `lifecycle_status: draft`, and `KGC-v0` §27 says a draft entity has no runtime selection. A gate that honoured "draft is not in force" by *ignoring* the edge would silently drop 851 hard prerequisites the moment content shipped — exactly the contamination the gate exists to prevent.
2. **`strictness_profile` has one value in the whole graph**: `default_prg_v0`. `KGC-v0` §12.2 says the profile only expresses existing `PRG-v0` metadata, so there is no second profile whose meaning could be implemented.
3. **`contamination_risk_if_missing` is authored but has no column.** `PRG-v0` §4.2 names it as a strictness trigger; `DDM-v0` never gave it a field, and 10D forbids inventing one.
4. **The retention and weakness engines do not exist yet** (13), so the two axes `PRG-v0` reads besides mastery have never been written.
5. **`skill_state` carries four engines' axes under one watermark.** 12A writes its own axis there and carries the other three; if a second engine did the same under its own watermark, the row would claim to be as current as its newest writer while carrying another engine's older axis — a staleness nobody could detect.
6. **The two per-Skill prerequisite facts are different questions.** `prerequisite_readiness` asks what a Skill is worth *as a prerequisite* (`PRG-v0` §3); the `prerequisite_axis_state` that drives `prerequisite_unresolved` asks whether a Skill's *own* prerequisites are resolved. Collapsing them would make either one wrong.
7. **The "contaminated" value lived in the adapter.** 12A's `SqlitePersistence.CONTAMINATED` was the only definition of the value the mastery engine reads, so nothing in core could produce it without repeating a string.

## 4. Positions taken

- **A draft edge is reported, never dropped and never quietly enforced.** It makes the candidate `invalid_prerequisite_metadata`: the candidate waits, the problem is named, and nothing is attributed to the target meanwhile. Publishing content (15) is what promotes the edges.
- **`default_prg_v0` is the only strictness profile**; any other value is a metadata problem, not a guess. Strictness comes from the source Skill's `critical_prerequisite` and the candidate's own request (§4.2).
- **`contamination_risk_if_missing` is recorded as not stored** rather than given an invented column; the owner is authored content (15) and the data model.
- **An axis nobody has evaluated yet is named, never read as bad news.** Retention not yet evaluated leaves a confirmed Skill `ready` — nothing has said it needs review — and the decision carries `retention_not_yet_evaluated`. Missing mastery is the one exception: no confirmed mastery means `not_ready`, which is what `PRG-v0` §3 says.
- **12B writes only `prerequisite_readiness`.** Putting the prerequisite axis onto `skill_state` belongs to whoever assembles that row under one watermark read, which is where recomputation is orchestrated (12D). The finding applies to 12A's carry too; it is harmless today only because no other engine writes the row.
- **The readiness row's watermark is the mastery row's**, or `0` when there is none: a derived projection cannot claim to have seen more truth than its input had.
- **The gate fails closed.** A readiness it was not given is `not_ready`.
- **`PrerequisiteSnapshot.CONTAMINATED` moved into core**, and the adapter now reads core's value instead of owning it.

## 5. Explicitly not decided in 12B

Assembling `skill_state`'s non-mastery axes and the primary presentation state (12D with 13), selecting what to do about a blocked candidate and generating repair needs (12C), the replan triggers of §19 and reverse invalidation of dependents (12D), user-facing text from the reason inputs (12E), retention and weakness values (13), undeclared-prerequisite discovery during evaluation (14), promoting authored edges out of `draft` and storing `contamination_risk_if_missing` (15), and any index on `skill_prerequisite_edge` — the graph is small enough that a bounded scan has not been shown to matter (18). No priority is an input, no readiness is a number, and nothing in the app reaches the gate yet.
