# 13A Weekly Assessment — Research & Decision Synthesis

**Stage step:** 13A — Haftalık sınav  
**Purpose:** Turn `WBA-v0`'s weekly blueprint into running code — a week's measurement composed from state before any item is chosen, fitted into real days by the planner, and reported without a score.

## 1. Research-need decision

**No web research pass was needed.** Every rule 13A implements is already accepted: the roles, their order, the reason codes and the result contract are `WBA-v0`'s (4B); item trust, selection order, exposure and duration are `QAB-v0`'s and `AIV-v0`'s; the interior is `ASUX-v0`'s and already exists as code (11D); the needs, gate and priority are 12A–12D's. The work was to compose these into a weekly blueprint without inventing a quota, a duration, a threshold or a queue, and to find what the code was missing.

## 2. Canonical source set reviewed

- `docs/WEEKLY_ASSESSMENT_SPEC.md` (`WBA-v0`) — §3 no debt and fresh blueprint, §4 no separate queue, §5–§7 blueprint, slot and roles, §8 target pool, §9 selection order, §10 fairness, §11 required coverage, §13 integrated attribution, §15 capacity, §16–§18 blocks, pause, incomplete, §19 H0, §20 invalid items, §25 root-cause contamination, §27 result, §28 generic contract, §29 reason codes.
- `docs/QUESTION_BANK_SPEC.md` (`QAB-v0`) — §8 resource fields (`expected_active_minutes`), §14 blueprint role eligibility, §15–§16 variant family and dependency group, §22 duration, §24 solution exposure, §31–§33 selection order and bounded read.
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` (`DMA-v0`) — §2 purpose vs UI, §3 intents, §5 no assessment before teaching, §7 retention stays `retain`.
- `docs/ASSESSMENT_SESSION_UX_SPEC.md` (`ASUX-v0`) and `docs/DAILY_MICRO_ASSESSMENT_IMPL_SPEC.md` (`DMAX-v0`) — the one interior.
- `docs/PRIORITY_POLICY_SPEC.md`, `docs/PLANNER_ENGINE_IMPL_SPEC.md`, `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md` — needs, bands, the gate, high-stakes trust.
- `docs/DOMAIN_DATA_MODEL_SPEC.md` and `docs/LOCAL_DATABASE_SPEC.md` — `assessment_session` as "one session, its blocks and boundaries", and 10D's disclosure that 13 owns completing it.

## 3. What was found

1. Nothing had ever written an `assessment_session`, and the table had no column for a session's blocks and boundaries.
2. The item model lacked `QAB-v0`'s `expected_active_minutes` and blueprint role eligibility.
3. An attempt could not name its session; exposure could not be read; "since the last cycle" had no reader.
4. `WBA-v0` fixes weekly cadence but no cycle boundary.
5. `VDW-v0` and 3H S06 were re-pointed to "13" by 12F but no 13 sub-step covers them.
6. Five 12x validators pinned "the schema is unchanged" as `VERSION = 2`, which any later owned migration breaks.

## 4. Positions taken

- **The cycle is the ISO week of the recorded study day** — a product default the user confirmed (the week starts Monday), never a scientific value, composed once per week; a missed week leaves nothing behind by construction.
- **The pool is the planner's needs**, one role per Skill in §9's order; open remediation, untaught Skills and diagnostics are not weekly measurements; roles are not quotas.
- **A slot gets the first item the store trusts, the gate allows and the learner has not seen**, never a near variant or a shared testlet of another slot, never an item without declared minutes. The read is bounded after the indexed facets (an engineering bound, 18E).
- **Slots reach the planner as candidates for the needs it already opened**: no weekly queue, band or minutes.
- **`assessment_session` is completed by a forward migration** with the blueprint as strict `weekly_blueprint/1` text and a CHECK that a weekly row carries one.
- **Root-cause contamination is forward-only** inside a session; retroactive correction needs dispositions the mastery engine does not read yet (13D).
- **Living gates narrowed, not loosened**: a schema version beyond 2 must be owned by an accepted contract.

## 5. Explicitly not decided in 13A

the diagnostic waiver, now 13F by the user's decision (`D-099`); monthly composition (13B); retention and weakness needs (13C/13D); retroactive contamination (13D); content freshness (15/18D); per-item tool microcopy (14); calling composition from the app (16D); calibration (18D/18E). T6 was not run.
