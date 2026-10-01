# 13C Spaced Repetition — Research & Decision Synthesis

**Stage step:** 13C — Spaced repetition  
**Purpose:** Turn `RVR-v0`'s retention verification model into running code — a schedule for proven Skills, honest re-verification, and a review the planner sees the day it comes due.

## 1. Research-need decision

**No web research pass was needed.** `RVR-v0` (2F) was itself built on a research validation (`docs/2F_RESEARCH_VALIDATION.md`) and already fixes the axis, the transitions, the V0 numbers and their status as engineering heuristics, and defers every empirical question — interval fit, success band, FSRS/HLR-style models — to the AŞAMA 18 pilot (§21, §22). The work was to implement it without inventing a probability, a decay or a threshold, and to find what the code was missing.

## 2. Canonical source set reviewed

- `docs/RETENTION_FORGETTING_SPEC.md` (`RVR-v0`) — §1 no time decay, §2 axis, §3 strong evidence, §5–§6 profile, initial interval and growth, §7 `review_due`, §8 successful review, §9 hysteresis and separation, §10 recheck results, §11 natural reuse, §12 no cluster refresh, §13 near repeats, §14 critical prerequisites, §15 Topic weakening, §16 missed days, §17 reason codes, §18 compact state, §19 performance, §20 V0 table.
- `docs/MASTERY_ENGINE_IMPL_SPEC.md` (`MSTX-v0`) — how a contradiction opens verification and how a rebuild reads the previous axis.
- `docs/PRIORITY_POLICY_SPEC.md` (`PBR-v0`) §6.4, §13 — retention priority and the uncalibrated overdue buckets.
- `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md` (`PRQX-v0`) — `review_due` is `ready_due`; critical `verification_due` blocks dependent new work.
- `docs/DOMAIN_DATA_MODEL_SPEC.md`, `docs/LOCAL_DATABASE_SPEC.md` — `retention_state` per Skill, projection provenance.
- `docs/WEEKLY_ASSESSMENT_IMPL_SPEC.md`, `docs/MONTHLY_ASSESSMENT_IMPL_SPEC.md` — the retention roles that read the planner's needs.

## 3. What was found

1. Nothing wrote the retention axis; every reader saw `not_yet_evaluated`.
2. `retention_state` had only `state`; §18's compact state had no columns.
3. `EvidenceRow` dropped the study day the store kept.
4. No indexed due query existed (§19).
5. Mastery is path-dependent, so a rebuildable schedule needs the mastery decision after every row.
6. `skill_state` had one writer, and 13A had left its assembly to 13C.

## 4. Positions taken

- **Retention is replayed from evidence**, with the mastery engine re-decided after every row exactly as `RebuildMastery` would; the projection is rebuildable and writes no truth.
- **`review_due` is derived from the day, never stored by a replay**; a daily refresh through the indexed due query moves `fresh`/`stable` schedules and keeps their watermark. The planner calls it before reading states.
- **The V0 numbers are `RVR-v0` §20's**, labelled heuristic, owner 18C. Growth rounds down (an earlier check, never a later one); the one-day separation is a rule about credit, not a lock.
- **Clean, strong and uncertain** follow §3, §10 and §13; a recheck must be fresh for every profile; `at_risk` never comes from time or from an off-schedule partial.
- **Schema v5** adds §18's columns, the due index and value-set triggers; tested against a populated v4 database.
- **`skill_state`** gets the retention axis with the older watermark of the two, so it never claims more truth than its oldest axis saw.

## 5. Explicitly not decided in 13C

remediation and Topic `weakening` (13D); overdue urgency buckets and the success band (18C); representative checks after absence (18B); rebuilding engines after evidence from the app (16D); joining prior exposure records into evidence (14/18D); any model beyond V0 (after 18). T6 was not run.
