# 12F Virtual User Tests — Research & Decision Synthesis

**Stage step:** 12F — Sanal kullanıcı testleri  
**Purpose:** Run the 3H virtual users against the gate, planner, replan, store, Today and explanation that now exist, and say plainly which scenarios cannot run yet.

## 1. Research-need decision

**No web research pass was needed.** The scenarios, their expected outcomes and the twenty invariants are 3H's (`docs/PLANNER_SIMULATION_SUITE.md`), which ran them at policy level and explicitly deferred the real run to 12F. The contracts they test are already accepted (`PDT-v0`, `PBR-v0`, `PRG-v0`, `SRR-v0`, D-033) and implemented (12B–12E). The work was to turn each virtual user into state the engines read — not answers they are handed — and to see what the real code does with it.

## 2. Canonical source set reviewed

- `docs/PLANNER_SIMULATION_SUITE.md` (3H) — §3 the eight profiles, §4 S01–S16, §5 the twenty `PDT-v0` invariants, §1 "this PASS does not replace 12F", §5 `PASS*` for invariant 17 pending a real benchmark.
- `docs/PLANNER_EXPLAINABILITY_SPEC.md` (`PDT-v0`) — §23 invariants; §21 the not-today detail.
- `docs/MISSED_DAY_RECOVERY_SPEC.md` (`SRR-v0`) — §9.1 `due state inventory != DailyPlan`; §14 recovery may take several days; §15 new learning on a return day "if capacity remains after PBR ordering".
- `docs/PRIORITY_POLICY_SPEC.md` (`PBR-v0`) — temporal urgency inside a band; starvation and track balance as the guards.
- `docs/DIAGNOSTIC_WAIVER_SPEC.md` (`VDW-v0`) — what S06 needs.
- `arch/9d_service_boundaries/boundaries.yaml` (`MSBX-v0`) — which modules may use the fixtures.
- The 12B–12E implementations and their specs.

## 3. What was found

1. **A returning learner's due inventory was listed row by row on `planner_explanation`** — seventy-eight "not today" entries, the backlog `SRR-v0` §9.1 forbids.
2. **3H's S07 example day depends on how many due reviews have authored tasks.** With a task for every due Skill, `PBR-v0` urgency fills the rest of the day with reviews and new learning waits for time, which `SRR-v0` §15 allows.
3. **`VDW-v0` has no implementation and no owner in the plan**, so S06 and invariant 12 cannot run.
4. **Two scenario tests were weaker than they looked** (F01, F06).

## 4. Positions taken

- **A virtual user is state.** Needs come from `PlannerEngine.needsFromSkillStates` except the ones `PLNX-v0` §5 says come from their owners; every gate decision is `PrerequisiteEngine.decide`; every explained trace is one the planner wrote.
- **Defined once, as test fixtures of `core-engines`**, and used on edges `MSBX-v0` already allows.
- **The explanation groups needs that did not come for the same recorded reason** into one entry that drops nothing; 12E's spec carries an explicit amendment.
- **No rule changed to make an illustrative example come true.** S07 is tested both ways; the guard is 18C's thresholds.
- **S06 is named, not simulated**, and re-pointed to 13.
- **Mutation runs only the virtual-user suites**, so every catch is theirs.

## 5. Explicitly not decided in 12F

The diagnostic waiver (13); retention and weakness needs, whose states the users supply (13); starvation and track-balance thresholds (18C); runtime budgets (18E); calling the planner from the app (16D). T6 was not run.
