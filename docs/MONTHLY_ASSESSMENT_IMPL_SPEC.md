# Monthly Assessment Implementation Specification — MCAX-v0

**Stage step:** 13B — Aylık sınav  
**Status:** ACCEPTED — independent 13B QA PASS  
**Decision:** `D-100`  
**Model:** `MCAX-v0 — Monthly Capability Assessment Implementation`  
**Monthly semantics:** `MCA-v0 / D-046`  
**Common contract it extends:** `WBA-v0 / D-045` §28, implemented by `WBAX-v0 / D-098`  
**Interior:** `ASUX-v0 / D-071`, `DMAX-v0 / D-090`  
**Item bank and validation:** `QAB-v0 / D-047`, `AIV-v0 / D-048`  
**Engines it feeds:** `MSTX-v0 / D-092`, `PRQX-v0 / D-093`, `PLNX-v0 / D-094`  
**Persistence:** `LFPS-v0 / D-076`, `DDM-v0 / D-077`, `LDBX-v0 / D-085`  
**Boundaries:** `MSBX-v0 / D-078`

## 1. Purpose

13B turns `MCA-v0` into code: a month's measurement is **selective longitudinal sampling, not a cumulative everything exam**. It is composed from current state before any item is chosen, measured only with items the store trusts and the learner has not seen, fitted into real days by the planner, and reported as which Objectives got which evidence — never a monthly score, a readiness verdict or a domain pass/fail.

It answers one primary question:

> **Bu ay, haftalık ölçümün göremediği neyi — kalıcı bir endişeyi, kritik bir yeteneğin güvenini, gecikmiş hatırlamayı, entegrasyonu — yeniden ölçmeye değer; ve bunu aylık etiketi kanıta ağırlık eklemeden, günü uzatmadan ve kaçırılan ayı borca çevirmeden nasıl yaparız?**

Primary invariant:

> **A month is a wider window, not a heavier exam.** What is worth measuring still comes from state, one Skill is measured once, a critical Skill is revalidated only for a reason, a monthly label adds no evidence weight, the month adds no minutes and no queue of its own, and a month that passed without its assessment leaves nothing behind.

---

# 2. What was found before writing monthly code

- **The common contract existed only under weekly names.** `MCA-v0` §4 says monthly is a policy extension of `WBA-v0` §28's contract, *not a second assessment architecture* — but 13A had typed the contract as `WeeklyAssessmentBlueprint`, `WeeklySessionStatus`, `WeeklyItemRefusal` and so on, with the six weekly roles hard-wired into the slot, the codec and the item model. A monthly blueprint could not be represented without either copying all of it or generalising it.
- **The v3 rule covered weekly rows only.** `CHECK (scope <> 'weekly' OR blueprint IS NOT NULL)` would accept a monthly row with no blueprint, and SQLite cannot alter a column's CHECK.
- **An item could declare only weekly roles.** `blueprintRoles` was `Set<BlueprintRole>` and the package parser knew six role ids.
- **Two `MCA-v0` roles have no state producer.** `cross_topic_transfer` needs transfer-opportunity metadata and `professional_evidence_checkpoint` needs a professional evidence profile (`MCA-v0` §6.5, §6.8, "Aşama 6/15/20 capability metadata'sı izin verdiğinde"). No need trigger opens either today.
- **`MCA-v0` defines no month boundary.** It fixes a monthly cadence and forbids "30 gün geçti → zorunlu eski sınav borcu"; it does not say where a month begins. §4 below records the product default.
- **`MCA-v0` §27's state-side lists are the engines' to fill.** `newly_opened_verification_ids`, `resolved_need_refs` and `resulting_*_state_refs` are what the canonical engines report after evidence; the result carries them as reported state changes and never derives them from answers.

---

# 3. Scope boundary

## 3.1 13B decides

- generalising 13A's weekly contract into the common one, **without changing any weekly value**,
- the monthly cycle and when a monthly blueprint is composed,
- the monthly target pool, its roles and their order,
- the monthly result's longitudinal lists,
- monthly storage (format, refusal of a row without its own blueprint),
- the monthly view of the one interior and its working copy.

## 3.2 13B does not decide

- which slot runs on which day — the planner's (`PBR-v0`, D-033),
- what an answer proves — the evidence pipeline and `GRE-v0`; the first clean post-mastery contradiction stays `verification_due` (`MCA-v0` §24),
- transfer-opportunity and professional-evidence metadata → 15 (and 6/20 for the capability profile),
- retention scheduling and its needs → 13C; remediation, weakness closure and its needs → 13D,
- the diagnostic waiver → 13F,
- final microcopy → 14; authored items with minutes and roles → 15; calling composition from the app → 16D,
- calibration of anything (question count, duration, cadence) → 18.

No passing mark, percentage, fixed question count, fixed duration, role ratio or countdown is introduced.

---

# 4. One contract, two scopes

`AssessmentBlueprint`, `AssessmentBlueprintSlot`, `BlueprintBlock`, `BlueprintSessionStatus`, `SlotItemRefusal`, `BlueprintExclusion`, `BlueprintEvidenceFact`, `BlueprintSlotOutcome` and `AssessmentBlueprintResult` are the common contract (`AssessmentBlueprint.kt`). A blueprint carries its `scope`; a slot carries a `SlotRole`, which each scope's own role enum implements (`BlueprintRole` for weekly, `MonthlyRole` for monthly). `BlueprintScopes` says, per scope, which roles exist, their selection order, the cycle and the reason-code namespace.

The blueprint's own `init` makes a mixed blueprint unrepresentable: a slot's role must belong to the blueprint's scope, only `weekly` and `monthly` compose a blueprint, and only a monthly blueprint can name a prior session.

Composition that is the same for both scopes — a pool entry's planner band, one Skill once, item selection, the planner bridge, recomposition, the root-cause guard and the result — is **one** implementation, `BlueprintComposer`, with the scope as a parameter. The scope decides which items are eligible (`QAB-v0` §13) and which namespace is written; it never decides what a refusal, a freshness rule, a trust ceiling or a result means. `WeeklyBlueprintEngine` keeps the weekly pool and delegates the rest; `MonthlyBlueprintEngine` adds the monthly pool.

**No weekly value changed.** The weekly roles, codes, exclusion ids, refusal ids, candidate ids and the `weekly_blueprint/1` text are byte-for-byte what 13A wrote, and every 13A test still runs (only type names were updated).

---

# 5. The cycle

A monthly cycle is the **calendar month of the learner-local study day** the row recorded (`MonthlyCycle.of`), written `2026-10` — the same rule as the week: the study day, never an instant.

This is a **product default**, not a scientific value: `MCA-v0` fixes monthly cadence and no boundary. It follows the week the user confirmed (the recorded study day, a calendar boundary, no rolling window); a setting may move it later (16D).

A month is composed **once**; asked again in the same month, composition returns the stored blueprint and writes nothing. A new month composes fresh from current state, names the previous monthly session as `prior_session` (`MCA-v0` §4 `prior_monthly_result_ref`), measures recent progress from that session's study day (`longitudinal_window_ref`), and carries `assessment.monthly.no_exam_debt`. Only the current month can ever be composed, so two missed months cannot stack. A month where nothing is worth measuring writes nothing.

The week and the month are **independent cycles**: composing one never marks the other composed, each reads only its own scope's rows, and each blueprint is stored in its own format.

---

# 6. The monthly target pool (`MCA-v0` §5–§7, §11–§12)

The composer opens no needs of its own. It reads the needs the planner already opens from each engine's axis, plus the owner-supplied ones, and decides for each whether it is a monthly measurement:

| Need | Monthly role | Why |
|---|---|---|
| `verification_due`, `weakness_detected` on a critical prerequisite | `critical_capability_revalidation` | §11: unresolved verification or a recent clean contradiction on a critical capability |
| `verification_due`, `weakness_detected` otherwise | `persistent_weakness_or_verification` | §12: a state-supported concern, raised by an engine, never from one wrong item |
| `continue_learning` on a critical prerequisite that held dependent work back | `critical_capability_revalidation` | §11: the next path really depends on it (the planner's trace, not a re-run gate) |
| `continue_learning` on a required capability with evidence since the previous monthly blueprint | `longitudinal_required_capability` | §5/§6.1: inside the longitudinal window |
| `retention_review_due` on a critical prerequisite | `critical_capability_revalidation` | §11: a meaningfully due review of a critical capability |
| `retention_review_due` otherwise | `delayed_retention_sampling` | §5; the slot stays `retain` (`DMA-v0` §2) and `review_due` is not forgetting |
| `integration_opportunity` | `integrated_application` | §5/§10 |
| `parallel_track_due` on the Technical English track | `parallel_technical_english` | §5/§21 |

Excluded, each by a named rule: open remediation (the whole Skill — it is repaired first; its fresh recheck is remediation's closure, 13D), new learning (`not_taught_yet`), a diagnostic or reinforcement opportunity or a non-English track (`not_a_monthly_measurement`), a Skill in progress with no evidence since the previous monthly blueprint (`not_active_since_last_cycle`), and a supporting or optional Skill in progress (`not_required_capability`, §5 samples **required** capabilities). A **critical Skill is never in the month just for being critical** (§11, invariant 8).

**One Skill is measured once**, under the role that comes first in §7's order — `persistent_weakness_or_verification`, `critical_capability_revalidation`, `longitudinal_required_capability`, `delayed_retention_sampling`, `cross_topic_transfer`, `integrated_application`, `parallel_technical_english`, `professional_evidence_checkpoint`. Inside a role, needs keep the planner's own band and rank vector. The order is an order, not a weight: §7's forbidden `%50 + %30 + %20` cannot be written.

**Transfer and the professional checkpoint have no producer** (§2). Their roles, codes and codec values exist, no need opens them, and nothing here invents one; their owner is 15. A professional checkpoint, when it exists, produces evidence and is never a readiness gate (`MCA-v0` §14); the working copy already says so.

**Longitudinal window, declared simplification.** §5 describes progress "farklı gün/haftalarda". 13B uses the window "evidence since the previous monthly blueprint", the reader 13A already has; whether progress spread over several distinct days adds information is a calibration question (18D), and no distinct-day count is invented.

---

# 7. Items, the planner, the session

Everything here is the common composer, unchanged in meaning from 13A:

- a slot takes the first item that targets the Skill, is **eligible for the monthly scope**, is declared for the **monthly role**, is trusted by the store's validation record, passes the prerequisite gate (fail-closed), is unseen and unsolved, and repeats no variant family or dependency group — in `QAB-v0` §31's order with the engineering read bound of 5 (18E). A weekly-only item never fills a monthly slot;
- each ready slot is offered to the planner as a candidate **for the need it already opened** (`monthly:<cycle>:<slot>`, `monthly_blueprint/1:<cycle>`), with the item's minutes and trust; the month adds no queue, band or minutes and never extends a day (`MCA-v0` §15). When a week and a month both hold a slot for the same need, both are offered as alternatives of that one need and the planner selects at most one task per need;
- the session runs in the **one** interior, blocked in §7's order; tools disclosed are the intersection of the items'; every slot is `h0_required`;
- recomposition (`MCA-v0` §16) appends a new row naming the one it supersedes and keeps `prior_session`; a past month is never recomposed;
- the root-cause guard (`MCA-v0` §20) is the same forward-only guard: a later slot resting on a Skill this session just showed cleanly missing records `contaminated`.

---

# 8. The result (`MCA-v0` §27, §30)

The result is the common one — status by what was submitted, clean positives/negatives/partials only from verified independent evidence, invalid/contaminated/provisional/assisted first class, no score field, changes only as the engines reported them — plus `MCA-v0` §27's four longitudinal lists, each fed only by its own role (`SlotRole.evidenceKind`):

- `revalidated_critical_skill_ids` — a **clean independent verified positive** in a critical revalidation slot;
- `revalidated_retention_skill_ids` — the same in a delayed retention slot;
- `transfer_evidence_objective_ids` / `integrated_evidence_objective_ids` — Objectives with clean evidence from a transfer / integration slot.

A clean negative revalidates nothing and is not a verdict: it goes through the evidence pipeline like any other, where a first clean post-mastery contradiction is `verification_due`, not instant unmastery (§24). Assisted or provisional work revalidates nothing. A monthly evidence row carries nothing that says "monthly": the scope adds no weight (`MCA-v0` §2, `ASUX-v0` §4).

The view shows no overall monthly success percentage and cannot (§30); its families are only what the engines reported.

---

# 9. Storage

**Schema version 4** adds one trigger, forward-only and in one transaction like every step:

```sql
CREATE TRIGGER assessment_session_blueprint_format BEFORE INSERT ON assessment_session
WHEN (NEW.scope = 'weekly'  AND (NEW.blueprint IS NULL OR substr(NEW.blueprint, 1, 17) <> 'weekly_blueprint/'))
  OR (NEW.scope = 'monthly' AND (NEW.blueprint IS NULL OR substr(NEW.blueprint, 1, 18) <> 'monthly_blueprint/'))
BEGIN SELECT RAISE(ABORT, ...); END
```

A weekly or monthly row must carry a blueprint **in its own scope's format**; a monthly row without one, or with a weekly one, is refused by SQLite itself; a daily row still needs none. Rows written before v4 are untouched (a trigger guards inserts; nothing before 13B wrote a monthly row). Migration was tested against a **populated** schema-3 database holding a weekly session: every truth row of every table unchanged.

`monthly_blueprint/1` is the weekly format plus one head field, `prior_session`. Both formats are strict: the head must have **exactly** its format's fields, so a weekly text with a `prior_session` field, a monthly text without one, or a role of the other scope decodes to `null`, never to a guess. 13A's codec file moved to `BlueprintCodec.kt` with the shared reader; `weekly_blueprint/1` is unchanged.

---

# 10. Ports and authored content

No port changed; the port count stays four. 13A's `latestAssessmentSession(scope)`, `exposuresFor`, `skillsEvidencedSince` and `assessmentItemsFor` serve the month unchanged.

The item model's `blueprintRoles` is now `Set<SlotRole>`. Weekly and monthly role ids never collide, so one `blueprint_roles` key serves both; a role of a scope the item is not eligible for (`scope_eligibility`) is a contradiction in the package and refuses it, rather than being silently ignored.

---

# 11. Living gates narrowed

13A's validator read code that 13B moved without changing its meaning. **Every 13A check still runs against the code that does the weekly work**; it now reads it where it lives:

- file paths: the codec (`BlueprintCodec.kt`), the use case (`BlueprintAssessment.kt`), the presentation (`BlueprintAssessmentSession.kt`), the common contract (`AssessmentBlueprint.kt`) and the shared composer (`BlueprintComposer.kt`);
- type names: `BlueprintSessionStatus`, `BlueprintExclusion`, `SlotItemRefusal`, the common data classes, `Set<SlotRole>`;
- generic forms: `scope in it.scopeEligibility` and `ItemSelection.fit(..., scope, ...)` **plus** a check that the weekly engine passes `AssessmentScope.WEEKLY_BLUEPRINT`; `codes.incompleteNotFailure`/`codes.noExamDebt` **plus** a check that the weekly codes map them to `INCOMPLETE_NOT_FAILURE`/`NO_EXAM_DEBT`; `BlueprintScopes.cycleOf(scope, ...)` **plus** a check that weekly maps to `WeeklyCycle.of`;
- `E13A-06_exclusions_equal_contract`: the five weekly exclusions are still first and unchanged; 13B's two follow them;
- `E13A-11_schema_version_3`: version 3 is still 13A's, and every later version must be owned by its step's contract;
- `E13A-12_engine_core_only`: the weekly engine may import its own module's composer, still nothing outside core.

The five 12x `schema_version_unchanged` gates needed no change: 13A already made them require an owning contract, and 13B's contract owns v4.

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

622 JVM tests, all passing; every 13A test still passes against the generalised code.

**Mutation 48/48, and only the monthly suites ran** — every mutant broke a monthly rule in the real cycle, roles, catalogue, common contract, codec, pool, composer, planner bridge, result, application, storage, migration, interior or authored format; every mutant had to compile, a run with no Gradle verdict is refused, and a comment-only control survives. **One first-run mutant survived:** M10 put the monthly blocks in `MCA-v0` §5's listing order instead of §7's selection order, and the model test still passed because the two roles it blocked agree in both orders. The test now also blocks a longitudinal and a persistent slot, whose orders disagree; M10 is caught, and the whole set was run again from one unchanged tree. Details in `arch/13b_monthly_assessment/monthly_assessment.yaml` (`mutation_results`).

**Not run: T6.** Nothing in the app composes a month or calls the planner yet (16D), and no authored item declares monthly roles (15).

---

# 13. Open, and owned elsewhere

- **Transfer-opportunity and professional-evidence producers** → 15 (with 6/20 capability metadata); until then no slot is composed under those two roles.
- **Monthly cycle as a setting** → 16D; the calendar-month default follows the user-confirmed weekly rule.
- **Distinct-day longitudinal evidence** → 18D calibration, if ever.
- **Retroactive contamination** → 13D. **Retention and weakness needs** → 13C / 13D.
- **The diagnostic waiver and S06** → 13F.
- **Monthly summary microcopy** (§30's sections) → 14.
- **Calling composition from the app** → 16D.

---

# 14. Anti-patterns explicitly rejected

- a monthly label adding evidence weight, or a monthly score, pass mark, readiness verdict or domain pass/fail,
- a cumulative everything exam, or a fixed question count, duration or role percentage,
- a critical Skill tested every month just for being critical,
- a persistent concern inferred from one wrong item,
- a professional checkpoint used as a readiness gate,
- a Skill measured under several roles, before teaching, or under open remediation,
- a weekly-only item, or a role the item was not declared for, filling a monthly slot,
- a monthly queue, band or minute budget beside the planner's, or a day extended for the month,
- a missed month turned into two exams, or last month's slots offered this month,
- a clean negative or assisted work called a revalidation,
- a second, monthly-only assessment architecture beside the common contract,
- a weekly value changed to make room for the month,
- editing a stored blueprint instead of appending a recomposition.

---

# 15. 13B acceptance contract

1. `MCAX-v0` is the accepted monthly assessment implementation.
2. The weekly contract is generalised into one common contract; no weekly value changed and every 13A test passes.
3. Vocabularies equal `MCA-v0` (§5 roles in order, §7 selection, §29's 21 codes in order).
4. The cycle is the calendar month of the recorded study day; a month is composed once; nothing is written when nothing is worth measuring; a new month names the prior session and carries no debt.
5. The pool comes from the needs the planner opens, one role per Skill in §7's order; a critical Skill is revalidated only for a reason; transfer and the checkpoint have no invented producer.
6. Items are monthly-eligible, role-declared, store-trusted, gate-passing and fresh; slots reach the planner as alternatives for needs it already opened.
7. The result has no score; its longitudinal lists hold only clean evidence of their own role; a clean negative revalidates nothing.
8. Schema v4 refuses a weekly or monthly row without a blueprint in its own format, tested against a populated v3 database; the port count stays four.
9. Narrowed living gates keep their guarantees.
10. Nothing is claimed that was not run; T6 was not run.
11. Independent 13B QA passes and the full `validate_*.py` sweep passes.

---

# 16. Handoff after acceptance

If accepted, 13B becomes `MCAX-v0 / D-100`.

Next numbered step: **13C — Spaced repetition**. It must receive a fresh PRE-STEP and explicit user approval before execution. 13C owns retention scheduling and its needs; the monthly `delayed_retention_sampling` and critical revalidation roles already read `retention_review_due` as the planner opens it. Open loops carried: transfer and professional-evidence producers (15), the diagnostic waiver and S06 (13F), retroactive contamination (13D), calling composition from the app (16D) and the T6 device run.
