# Weekly Assessment Implementation Specification — WBAX-v0

**Stage step:** 13A — Haftalık sınav  
**Status:** ACCEPTED — independent 13A QA PASS  
**Decision:** `D-098`  
**Model:** `WBAX-v0 — Weekly Blueprint Assessment Implementation`  
**Weekly semantics:** `WBA-v0 / D-045`  
**Interior:** `ASUX-v0 / D-071`, `DMAX-v0 / D-090`  
**Item bank and validation:** `QAB-v0 / D-047`, `AIV-v0 / D-048`  
**Engines it feeds:** `MSTX-v0 / D-092`, `PRQX-v0 / D-093`, `PLNX-v0 / D-094`  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

13A turns `WBA-v0` into code: a week's measurement is composed from current state before any item is chosen, its slots are measured only with items the store trusts and the learner has not seen, the planner fits them into real days, and the result says which Objectives got which evidence — never a score.

It answers one primary question:

> **Bu hafta neyi ölçmeye değer — ve bunu, güvenilir ve taze bir soruyla, günün gerçek süresini aşmadan ve kaçırılan haftayı borca çevirmeden nasıl ölçeriz?**

Primary invariant:

> **A week is an identity, not a quota and not a deadline.** What is worth measuring comes from state, one Skill is measured once, a slot without a trusted fresh item is not coverage, the week adds no minutes and no queue of its own, and a week that passed without its assessment leaves nothing behind.

---

# 2. What was found before writing weekly code

- **Nothing had ever written an `assessment_session`.** 11D built the one interior but no session row; 10D fixed only its `scope` column and disclosed that 13 owns the rest. `DDM-v0` describes the entity as "one session, its blocks and boundaries" — so the blueprint had nowhere to live.
- **The item model lacked two `QAB-v0` fields a weekly slot needs.** `expected_active_minutes` (§8, §22) — an item that says nothing about its duration cannot be fitted to a time budget — and `blueprint_role_eligibility` (§14).
- **An attempt could not name its session.** `DDM-v0` gives `attempt` an `assessment_session_id`; `SubmitAttempt` never wrote it.
- **Exposure could be written but not read.** `DDM-v0` indexes exposure precisely for the selection-time lookup "has this learner seen it, or its solution?", and no port could ask.
- **"Since the last cycle" had no reader.** `WBA-v0` §8 measures recent progress since the previous blueprint; nothing could say which Skills received evidence after a given study day without reading evidence history into core.
- **`WBA-v0` defines no cycle boundary.** It says "weekly" and forbids debt; it does not say where a week begins. §4 below records the product default.
- **`VDW-v0` (diagnostic waiver) and 3H S06 were re-pointed to "13" by 12F, but none of 13A–13E covers them.** The owner is a plan decision, so it was put to the user rather than assigned here: the user chose a new step, **13F — Tanısal atlama (VDW-v0)**, appended to AŞAMA 13 without renumbering anything (`D-099`).

---

# 3. Scope boundary

## 3.1 13A decides

- the weekly cycle and when a blueprint is composed,
- the target pool, its roles and their order,
- per-slot item selection and its refusals,
- how slots reach the planner,
- the weekly view of the one interior and the result contract,
- the root-cause contamination guard,
- completing `assessment_session` with its blueprint.

## 3.2 13A does not decide

- which slot runs on which day — the planner's (`PBR-v0`, D-033),
- what an answer proves — the evidence pipeline and `GRE-v0`,
- retention scheduling and its needs → 13C; remediation, weakness closure and its needs → 13D,
- monthly composition → 13B,
- final microcopy and evaluator behaviour → 14,
- authored items with minutes and roles → 15,
- when the app composes a week (calling it) → 16D, with the planner,
- item/exposure calibration and any freshness window → 18D; runtime budgets → 18E.

No passing mark, percentage, fixed question count, fixed duration or countdown is introduced.

---

# 4. The cycle

A cycle is the **ISO-8601 week of the learner-local study day** the row recorded (`WeeklyCycle.of`), written `2026-W40`. The study day is `DDM-v0`'s own day column, never an instant, so a DST change or a flight cannot move work from one week into another; the ISO week-based year names weeks across New Year (`2027-01-01` is `2026-W53`).

This is a **product default**, not a scientific value: `WBA-v0` fixes weekly cadence and no boundary. The week starting on Monday matches the learner's locale, **and the user confirmed it**; a setting may move the start day later (16D).

A week is composed **once**. Asked again in the same week, composition returns the stored blueprint and writes nothing (the 12D emsal). In a new week it composes fresh from current state: the only week that can ever be composed is the current one, so two missed weeks cannot stack by construction, and the new blueprint carries `assessment.weekly.no_exam_debt`. An earlier week's unfinished slots are never offered again; if their needs are still open, current state brings them back on its own.

A week where nothing is worth measuring, or nothing trusted can measure it, returns `NothingToMeasure` and **writes nothing** — it is not an exam the learner missed, and a later composition that week can still find one.

---

# 5. The target pool (`WBA-v0` §7–§9)

The composer opens **no needs of its own**. It reads exactly the needs the planner opens from each engine's axis (`PlannerEngine.needsFromSkillStates`) plus the ones only their owners supply (the parallel track's cadence, integration opportunities), and decides for each whether it is a weekly measurement and under which role:

| Need | Weekly role, or why not |
|---|---|
| `verification_due`, `weakness_detected` | `weakness_or_verification` |
| `continue_learning` of a **critical** Skill that **held dependent work back** in the last plan | `critical_prerequisite_confidence` |
| `continue_learning` with evidence since the previous blueprint (or no previous blueprint) | `recent_required_progress` |
| `continue_learning` with no evidence since then | not measured — `not_active_since_last_cycle` |
| `retention_review_due` | `retention_due` (purpose stays `retain`, `DMA-v0` §2) |
| owner `integration_opportunity` | `integration_or_transfer` |
| owner `parallel_track_due` on `technical_english` | `parallel_english` |
| `new_learning` | not measured — `not_taught_yet` (`DMA-v0` §5) |
| `diagnostic_opportunity`, `reinforcement_opportunity` | not measured — `VDW-v0`'s, or not a measurement |
| any need of a Skill under **open remediation** | not measured — `remediation_open` |

"Held dependent work back" is the prerequisite gate's own answer, read from the last plan's `planner_trace/3` (blocked candidates' related Skills); the gate is not re-run.

**One Skill is measured once**, under the role that comes first in §9's order — `weakness_or_verification`, `critical_prerequisite_confidence`, `recent_required_progress`, `retention_due`, `integration_or_transfer`, `parallel_english`. Inside a role, needs keep the planner's own `PBR-v0` band and rank vector. The roles are **not quotas**: a week with no due retention has no retention slot, and a week with nothing due has no blueprint.

---

# 6. Choosing an item for a slot (`QAB-v0` §31–§33)

Per slot, the composer first narrows by the indexed facets — the item targets the Skill, is eligible for `weekly_blueprint`, has a selectable lifecycle and **declares the role** — orders them by `QAB-v0` §32 (trusted before validated, deterministic check before evaluator, then a stable id; never the shortest first), reads at most `MAX_ITEMS_PER_SLOT_V0 = 5` (an engineering bound like the planner's per-need cap, owned by 18E), and takes the first that passes every per-learner check. Each refusal names a rule:

- `not_usable_for_intent` — `ItemSelection.fit` for the role's `DMA-v0` intent (trust from the **store's** validation record, the Objective's evidence profile, the use ceiling), and a planned slot needs a validated item (`PBR-v0` §3),
- `prerequisite_waits` — the gate said wait, or did not answer (it fails closed),
- `solution_exposed` — the item, or any item of its variant family, had its solution shown (`QAB-v0` §24),
- `already_seen` — the exact item version was served before: a weekly slot asks for an independent, fresh measurement,
- `variant_family_in_use`, `dependency_group_in_use` — a near variant or a testlet already measures another slot this week (`QAB-v0` §15–§16),
- `expected_minutes_missing` — the item does not declare its cost, and none is invented.

A slot with no passing item is `no_valid_item`: it is **not coverage**, it is never presented as the learner's failure, and its need stays exactly as open as it was. Every slot is `h0_required`; help is never blocked, it only changes what the attempt proves.

A slot is **required for session closure** only when its band is P0 or P1 — integrity, verification and repair (`WBA-v0` §11); everything else is welcome and optional.

Blocks are the ready slots grouped by role in selection order; each boundary is one atomic slot.

---

# 7. Reaching the planner (`WBA-v0` §4, §15)

Each ready slot becomes a `TaskCandidate` for **the same need the planner already opened** — purpose from the role (`retain` for retention), activity `assessment_session`, the item's minutes as cost, the store's validation as trust, the item's required Skills for the gate, and `atomicEvidenceBoundary = true`. `BuildDailyPlan` adds this week's slot candidates to the authored ones; the planner then does everything it always does: trust, gate, `PBR-v0` band and rank, capacity fit.

So the week adds **no queue, no band and no minutes of its own**. A weekly session that does not fit today continues on another day of the week inside that day's budget; nothing is extended. A slot whose item the learner has since been served is not offered again, and only the current week's blueprint is ever offered.

---

# 8. The session and its result

The weekly session runs in **the one interior** (`ASUX-v0`): `WeeklySessionPresentation.view` maps blocks and slots to its blocks and boundaries, discloses `h0_required`, and discloses as allowed only the tools **every** item allows — a session never claims a tool is allowed that one of its items forbids. Freezing, skipping, help, recomposition and dispute are the interior's existing code, unchanged.

An attempt made in the session **names it** (`AttemptSubmission.assessmentSessionId` → `attempt.assessment_session_id`).

**Root-cause contamination (`WBA-v0` §25).** When a slot's evidence is recorded (`RecordWeeklySlotEvidence`, through 12A's `RecordEvidence`), a Skill this very session has already shown missing — cleanly: verified, independent, negative, not itself contaminated — makes every later slot whose item requires it record the `contaminated` prerequisite snapshot, which the mastery engine already excludes. Independent branches are untouched. The guard is forward-only: a downstream answer submitted *before* its root failed keeps its evidence, because correcting it means an appended `evidence_disposition` that the mastery engine does not yet read (open loop, §13).

**The result (`WBA-v0` §27)** is built from what happened in the session and what the engines reported:

- `complete` when every ready slot was submitted, `partial` when some were, `deferred` when none were — a skip is **not incorrect** and an unfinished session is **not a failure** (`assessment.weekly.incomplete_not_failure`);
- verified positive, negative and partial Objectives come only from verified, independent, usable evidence;
- invalid and contaminated evidence is listed as unusable, provisional evidence as provisional, assisted evidence as needing an independent recheck — all first class in the interior's `not_reliably_measured`;
- state changes are only what the canonical engines reported; none is inferred from answers.

There is no `overall_mastery_score` and no field that could hold one. `invalidated` is declared by `WBA-v0` §27 and has no producer yet.

**Recomposition (`WBA-v0` §17).** The session names the unresolved slots one of the five `ASUX-v0` conditions applies to; composition re-chooses only those, never reusing the replaced item or its variant family, and appends a **new** session row that names the one it supersedes. Nothing is edited; attempts already made stay under the session they were made in.

---

# 9. Storage

**Schema version 3** completes `assessment_session`, forward-only and in one transaction like every step:

- `blueprint TEXT CHECK (scope <> 'weekly' OR blueprint IS NOT NULL)` — the weekly session's blocks and boundaries as strict `weekly_blueprint/1` text; a weekly row without one is refused by SQLite itself, and a daily row still needs none;
- `assessment_sessions_by_scope (scope, sequence)` and `evidence_by_study_day (occurred_on_study_day)` for the two new read paths.

Rows written before v3 are untouched: an added column fills them with nothing, and nothing had ever written a session. The session table stays append-only (its existing abort triggers apply to the new column). Migration was tested against a **populated** schema-2 database: every truth row of every table unchanged, the session row gaining only an empty column.

`weekly_blueprint/1` is strict in both directions like `planner_trace/3`: an unknown version, section, field or value, a repeated field or a rejection naming no slot decodes to `null`, never to a guess. A blueprint that cannot be read refuses composition rather than being guessed around.

`AssessmentScope` now carries the stored value (`daily`/`weekly`/`monthly`) beside the interior id, so the two vocabularies cannot drift.

---

# 10. Port refinements

No new interface; the port count stays four.

- `PersistencePort.latestAssessmentSession(scope)` — the newest session row of a scope, as written.
- `PersistencePort.exposuresFor(resources, variantFamilies)` — exposure by pinned item version or variant family, read-only.
- `PersistencePort.skillsEvidencedSince(studyDay)` — distinct Skills with evidence on or after a study day, by the rows' own day.
- `ContentPort.assessmentItemsFor(skill)` — the authored items targeting a Skill.

The authored `[item]` section gained two **optional** keys, `expected_active_minutes` and `blueprint_roles`; an item that does not declare them simply cannot fill a weekly slot, and a non-positive minute value or an unknown role refuses the package.

---

# 11. Living gates narrowed

Five 12x validators pinned their own "the schema is unchanged" guarantee as `VERSION = 2`, and one found the planner's state read by searching for `readProjection(` in `BuildDailyPlan`:

- `E12B-12`, `E12C-08`, `E12D-09`, `E12E-04`, `E12F-07` `schema_version_unchanged` now require that **every** schema version beyond 2 is declared by the accepted contract that added it (`schema_migration` in its arch yaml) — an unowned move still fails;
- `E12C-08_watermark_first` also recognises `PlanningStates.read(`, the shared state reader both the planner and the composer now use; the watermark must still be read before any state.

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

**Mutation 42/42, and only the six weekly suites ran** — every mutant broke a weekly rule in the real cycle, pool, item selection, planner bridge, result, contamination, storage, codec, interior view or authored format, every mutant had to compile, a run with no Gradle verdict is refused, and a comment-only control survives. **Three first-run mutants were not counted:** W26 and W40 were equivalent (the replaced item's variant family already blocks it; the item model's own `init` already refuses a non-positive duration) and W27 did not compile. Each was replaced by a mutant that changes behaviour — dropping the replaced families from the in-use set, silently dropping an unknown blueprint role, a same-week check that never matches — all three were caught, and the whole set was run again from one unchanged tree. Details in `arch/13a_weekly_assessment/weekly_assessment.yaml` (`mutation_results`).

**Not run: T6.** Nothing in the app composes a week or calls the planner yet (16D), and no authored item declares minutes or roles (15); the weekly session has lived only in tests.

---

# 13. Open, and owned elsewhere

- **`VDW-v0` diagnostic waiver and 3H S06** → **13F** (user decision, `D-099`).
- **Weekly cycle start day as a setting** → 16D; the ISO-week default is confirmed by the user.
- **Retroactive contamination** of a downstream answer submitted before its root failed — needs `evidence_disposition` read by the mastery engine → 13D (weakness/remediation owns correction flows).
- **`skill_state` assembly under one watermark** — 13A writes no axis; the first second writer is 13C.
- **Retention and weakness needs and their `retention.*` codes** → 13C / 13D.
- **Content freshness** (`QAB-v0` §26, `stale` not for high stakes) is not in the item model → 15 / 18D.
- **Per-item tool disclosure** inside one session (today: the intersection) → 14's microcopy.
- **Calling composition from the app** → 16D, with the planner and the capacity setting.

---

# 14. Anti-patterns explicitly rejected

- a role used as a quota, or a fixed question count, duration or percentage,
- a Skill measured under several roles in one week,
- measuring before teaching, or measuring a Skill under open remediation,
- an item promoting itself past the store's validation,
- a seen item, a solved variant family, a near variant or a shared testlet counted as a fresh independent measurement,
- a duration invented for an item that declares none,
- a weekly queue, band or minute budget beside the planner's,
- a missed week turned into two exams, or last week's slots offered this week,
- a skip, a partial session or a deferred week called a failure,
- an overall score, a pass mark or a grade in the result,
- a state change claimed that no engine reported,
- a downstream item blamed for a root prerequisite this session just showed missing,
- editing a stored blueprint instead of appending a recomposition.

---

# 15. 13A acceptance contract

1. `WBAX-v0` is the accepted weekly assessment implementation.
2. Vocabularies equal `WBA-v0` (§7 roles and §29 codes, in order), `ASUX-v0`, `DMA-v0` and `QAB-v0`.
3. The cycle is the ISO week of the recorded study day; a week is composed once, and nothing is written when nothing is worth measuring.
4. The pool comes from the needs the planner opens, one role per Skill in §9's order; roles are not quotas.
5. Every item refusal names a rule; a slot without a trusted, fresh, fitting item is not coverage.
6. Slots reach the planner as candidates for the needs it already opened, with no queue, band or minutes of their own.
7. The session runs in the one interior; attempts name their session.
8. The result has no score; a skip is not incorrect; contaminated, invalid, provisional and assisted evidence is first class.
9. A missed week leaves no debt; recomposition appends and never edits.
10. Schema v3 completes `assessment_session`, tested against a populated database; the port count stays four.
11. Narrowed living gates keep their guarantees.
12. Nothing is claimed that was not run; T6 was not run.
13. Independent 13A QA passes and the full `validate_*.py` sweep passes.

---

# 16. Handoff after acceptance

If accepted, 13A becomes `WBAX-v0 / D-098`.

Next numbered step: **13B — Aylık sınav**. It must receive a fresh PRE-STEP and explicit user approval before execution. `MCA-v0` reuses the generic blueprint/slot/result contract of `WBA-v0` §28; 13A's composer, codec, planner bridge and interior view are its starting point. Open loops carried: the diagnostic waiver and S06 (13F), retroactive contamination (13D), retention and weakness needs (13C/13D), content freshness (15/18D), calling composition from the app (16D) and the T6 device run.
