# 13B Monthly Assessment — Research & Decision Synthesis

**Stage step:** 13B — Aylık sınav  
**Purpose:** Turn `MCA-v0`'s monthly capability assessment into running code — selective longitudinal sampling composed from state, extending the common blueprint contract 13A built rather than building a second assessment architecture.

## 1. Research-need decision

**No web research pass was needed.** Every rule 13B implements is already accepted: the eight roles, their selection semantics, the 21 reason codes, the result contract and the "no stronger numeric weight" invariant are `MCA-v0`'s (4C); the common blueprint/slot/result contract is `WBA-v0` §28's and already exists as code (13A); item trust, selection and exposure are `QAB-v0`'s and `AIV-v0`'s; the interior is `ASUX-v0`'s; needs, gate and priority are 12A–12D's. `MCA-v0` §32 itself defers the empirical questions (optimal question count, duration, transfer ratio, critical revalidation cadence) to AŞAMA 18, so nothing here needed a new external source.

## 2. Canonical source set reviewed

- `docs/MONTHLY_ASSESSMENT_SPEC.md` (`MCA-v0`) — §2 scope differences and `monthly_scope != stronger_numeric_weight`, §3 no debt, §4 common contract and its monthly extension, §5 roles, §6 target pool, §7 selection semantics and the forbidden percentage split, §8 not cumulative, §9–§10 transfer and integration, §11 critical revalidation triggers, §12 persistent concern, §14 professional checkpoint is not a gate, §15–§17 capacity, split, incomplete, §18 H0, §19–§20 validity and root cause, §24 negative result hysteresis, §27 result, §29 reason codes, §30 summary, §32 calibration boundary, §33 invariants.
- `docs/WEEKLY_ASSESSMENT_SPEC.md` (`WBA-v0`) §28 and `docs/WEEKLY_ASSESSMENT_IMPL_SPEC.md` (`WBAX-v0`) — the contract and composer being extended.
- `docs/QUESTION_BANK_SPEC.md` (`QAB-v0`) §13–§14 scope and role eligibility, §31–§33 selection.
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` (`DMA-v0`) §2, §5, §7; `docs/ASSESSMENT_SESSION_UX_SPEC.md` (`ASUX-v0`) §4.
- `docs/PRIORITY_POLICY_SPEC.md`, `docs/PLANNER_ENGINE_IMPL_SPEC.md`, `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md` — needs, bands, the gate, one task per need.
- `docs/LOCAL_DATABASE_SPEC.md`, `docs/DOMAIN_DATA_MODEL_SPEC.md` — `assessment_session` and the append-only rules.

## 3. What was found

1. The common contract existed only under weekly names, with the weekly roles hard-wired into the slot, the codec and the item model.
2. Schema v3's CHECK covered weekly rows only, and SQLite cannot alter a column CHECK.
3. An item could declare only weekly roles.
4. `cross_topic_transfer` and `professional_evidence_checkpoint` have no state producer: no need trigger and no capability metadata exist yet.
5. `MCA-v0` fixes a monthly cadence but no month boundary.
6. `MCA-v0` §27's state-side lists are what the engines report, not something the result can compute.

## 4. Positions taken

- **One contract, two scopes.** The weekly contract is generalised (`AssessmentBlueprint` with a `scope`, roles behind `SlotRole`), the shared composition is one parametric `BlueprintComposer`, and a mixed blueprint is unrepresentable. No weekly value changed; every 13A test still passes.
- **The cycle is the calendar month of the recorded study day** — a product default following the user-confirmed weekly rule, never a scientific value; composed once per month; the previous monthly session is named, never owed.
- **The pool is the planner's needs**, one role per Skill in §7's order. A critical Skill is revalidated only for §11's reasons (open verification or contradiction, a due review, or holding dependent work back) — never for being critical. Supporting/optional Skills are not longitudinal samples. Remediation, untaught Skills, diagnostics and non-English tracks are not monthly measurements.
- **Transfer and the professional checkpoint are declared, not invented**: roles, codes and storage exist; no producer is fabricated; owner 15.
- **Longitudinal window = evidence since the previous monthly blueprint**; distinct-day counting would be an invented threshold and is left to 18D.
- **The result adds §27's four longitudinal lists**, each fed only by clean independent verified evidence of its own role; a clean negative revalidates nothing.
- **Schema v4 is a trigger**: a weekly or monthly row must carry a blueprint in its own scope's format; tested against a populated v3 database.
- **Living gates narrowed, not loosened**: 13A's validator follows the moved code and gains checks that the generic forms are bound to weekly values.

## 5. Explicitly not decided in 13B

transfer and professional-evidence producers (15, with 6/20 metadata); the monthly cycle as a setting (16D); distinct-day longitudinal evidence (18D); retention scheduling (13C); remediation closure and retroactive contamination (13D); the diagnostic waiver (13F); monthly summary microcopy (14); calling composition from the app (16D); calibration (18). T6 was not run.
