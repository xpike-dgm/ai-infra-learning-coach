# Skill Analytics Specification — SKAX-v0

**Stage step:** 16A — Skill analytics  
**Status:** ACCEPTED — independent 16A QA PASS  
**Decision:** `D-122`  
**Model:** `SKAX-v0 — Skill state projection, skill_detail and progress_overview`  
**Behaviour it implements:** `SPWX-v0` §§4–5, 7–8, 14–18; `TEPM-v0` §4.1, §§9, 11, 16; `VDSX-v0` (tones); `WFPX-v0` §§5.2–5.3; `PRG-v0` §7; `RVR-v0` §2  
**Code:** `core-model` (`SkillPresentationFacts.kt`, `ProgressFacts.kt`), `core-application` (`SkillStateAssembly.kt`, `ProgressQuery.kt`), `core-presentation` (`ProgressPresentation.kt`), `app-ui` (`ProgressScreen.kt`), `app-wiring`  
**Persistence / ports / schema:** unchanged

## 1. Purpose

8E fixed what Progress may say. 16A makes it say it, from the store, for every published Skill:

> **Gerçekte neyi kanıtladım ve neye dikkat etmem gerekiyor — her Skill için, motorların yazdığından fazlasını iddia etmeden?**

Primary invariant:

> **A Skill's learner-facing state is `SPWX-v0`'s one precedence over the four axes their own engines wrote, decided by one function that both the stored row and the screen use.** Where an axis a rule needs is unwritten, or evidence is newer than the row, the Skill is *recomputing* — never a guessed state and never a ninth one. Progress counts current state as an inventory and never divides it, and only the engines decide what happens next.

---

# 2. What was found

- **The presentation column held the mastery value.** 12A wrote its own mastery axis into `skill_state.primary_presentation_state`, because the other axes had no engine yet. 13C and 13D then carried that value forward. So the column never held one of the eight states. Every writer now derives the value from the axes the row has.
- **One derivation, two layers.** The row assembly in `core-application` writes the state that `core-presentation` shows, and `MSBX-v0` lets neither depend on the other. The eight states and their derivation moved to `core-model`, following the precedent of `WeaknessAxis.of`. One function means one answer for each Skill.
- **A dependent's prerequisite axis goes stale.** A prerequisite Skill that changes does not re-assemble the rows of the Skills that depend on it (reverse invalidation, 18E). The Progress read therefore asks the gate again instead of trusting the stored axis.
- **A new learner sees most of the course waiting, and it is the planner's set.** With all eight packages published and nothing recorded, 4 Skills read *Henüz Kanıt Yok* (the four entry lessons) and 56 read *Ön Koşul Bekliyor*. Those 56 are exactly the Skills the planner holds back: one gate, one answer.
- **`TEPM-v0` §4.1 rule 4 names only fresh and stable.** `RVR-v0` §2 says `at_risk` is a mixed-evidence concern on a Skill that is still mastered. A confirmed Skill that is at risk therefore stays *Doğrulanmış*, with the at-risk qualifier shown beside it (assistant default, §5).

---

# 3. Decisions

User decisions (2026-10-17, the user's explicit approval of 16A):

- **Scope:**
  - the Skill projection: the eight states derived from the four axes and written correctly to `skill_state`, with `at_risk` as a qualifier;
  - `skill_detail`;
  - `progress_overview`: inventory and attention set.
  
  Topic state, the weakness and misconception views and the Technical English profile belong to 16C. History and the longitudinal `assessment_report` belong to 16B.
- **Delivery:** core, plus a real Compose Progress screen and Skill detail that read the store. Today they are honestly empty.
- **An unevaluated axis:** shown as recomputing, never guessed.

Assistant defaults, reviewed: listed in the contract (`arch/16a_skill_analytics/skill_analytics.yaml` `assistant_defaults`).

Declared extensions (`D-122`):

- `SkillPresentationState` and `SkillPresentationProjection` live in `core-model`.
- `AssembleSkillState` runs at the end of `RecomputeSkillState`. It writes `prerequisite_axis_state` (the gate's eligibility for new work on the Skill) and `primary_presentation_state` under the row's own watermark. This closes 16C's `prerequisite_axis_state` loop.
- Every `skill_state` writer derives `primary_presentation_state` from the axes the row now has.

---

# 4. The derivation

`SkillPresentationProjection.derive(reading)` checks the rules in order; the first rule that holds decides.

| # | When | State |
|---|---|---|
| 1 | weakness axis is `remediation_required` | `remediation_required` |
| 2 | mastery is `confirmation_verification_due`, or mastery is confirmed and retention is `verification_due` | `confirmation_verification_due` |
| 3 | mastery is confirmed and retention is `review_due` | `confirmed_review_due` |
| 4 | mastery is confirmed and retention is `fresh`, `stable` or `at_risk` | `confirmed_current` |
| 5 | mastery is `not_yet_evidenced` or `developing_with_support`, and the gate says new work waits | `prerequisite_unresolved` |
| 6 | mastery is `developing_independent` | `developing_independent` |
| 7 | mastery is `developing_with_support` | `developing_with_support` |
| 8 | otherwise | `not_yet_evidenced` |

**Recomputing** is the projection's own status, not a ninth state. A Skill is recomputing when:

- its row is older than its newest evidence;
- evidence exists but the mastery or the weakness axis is unwritten;
- it is confirmed but its retention is `untracked` or `not_yet_evaluated`;
- it is a rule-5 Skill and the gate has not answered.

A rule that needs a missing axis stops the derivation, because the later rules would depend on what that axis says. In the stored column, a recomputing Skill is `not_yet_evaluated`.

A Skill with **no evidence at all** reads as not evidenced, with no weakness. That is a fact about the store, not a guess.

**Qualifiers** are exactly `VDSX-v0`'s four:

- `retention_at_risk`, from the retention axis;
- `requires_independent_recheck`, open until an independent row on the same Objective follows it;
- `contested_evidence_present`;
- `provisional_evaluation_present`.

---

# 5. Evidence by kind

`SPWX-v0` §8 asks for evidence to be distinguished, not merged into one count. Each row gets the first kind that applies:

1. **Sayılmayan:** invalid, judged invalid, contested, or produced while the target's prerequisites were missing (`TEPM-v0` §11).
2. **Kesinleşmemiş:** judged by a provisional evaluator.
3. **Destekli:** assisted, practice-only or awaiting an independent recheck, or made after a solution was shown (`TEPM-v0` §9).
4. **Bağımsız:** everything else.

The order means a row that cannot count is never shown as counting in some weaker way.

---

# 6. Progress overview and `skill_detail`

- **States:** the ten `SPWX-v0` §17 states, with `VDSX-v0`'s tones.
  - Partial is the only pending tone. The two system faults are the only fault tones.
  - `empty_no_evidence_yet` is a legitimate start.
  - `empty_no_attention_needed` says plainly that nothing being due does not mean everything was learned.
  - `offline_local_capable` is never produced, because Progress needs no network. AI being unavailable is a context line, never the main state.
- **Inventory:** a count of Skills per state, as text beside its label. It has no ratio, percentage, bar, gauge or level, and no type holds a total to divide by. Recomputing Skills are counted on a separate line.
- **Attention set:** grouped by reason (`SPWX-v0` §7.2). It is an inventory of current needs and creates no planner priority.
  - `review_due` keeps its neutral tone.
  - `assessment_opportunity` exists in the vocabulary, but nothing produces it until the app composes sessions (16D).
- **Skill list:** grouped by id namespace, for orientation only. A group has no count.
- **`skill_detail`:**
  - the same chip as the overview, plus all four axes;
  - Objective by Objective: whether it has been shown independently, its weakness (a hypothesis is "an open question", never a deficiency), and its evidence by kind;
  - the evidence summary by kind;
  - the prerequisite context, naming each blocker and linking it to that blocker's own `skill_detail`;
  - the Topic placements.
  
  Deferred fields and their owners are listed in the contract.

---

# 7. Verification

- **Suites:**
  - `SkillPresentationProjectionTest`: every rule; every combination of axis values. No combination confirms a Skill its mastery engine did not confirm, `at_risk` is never dropped, and no stored value is a ninth state.
  - `ProgressQueryTest`: a fake store with a prerequisite edge.
  - `ProgressPresentationTest`: no forbidden word or ratio in any string, and no forbidden field in any type.
  - The 16A section of `ShippedCourseTest`: the real SQLite store with all eight packages.
- **Mutation:** Kotlin 30/30. The first run left three mutants alive: the retention and weakness writers not deriving the presentation, which the end-of-chain assembly hid, and the order of the two empty states. Each got a test, and the whole set ran again. The validator's own mutation run (one change per mutant to an input it reads; a crash is never a detection) detects every mutant; the counts are in the contract's `validator_mutation`. Each negative control survived.
- **Validator:** `tools/validate_skill_analytics.py` reads `SPWX-v0`'s and `VDSX-v0`'s accepted contracts and the real Kotlin, and every check passes (`arch/16a_skill_analytics/qa_report.yaml`).
- **Gradle and APKs:** see the contract's `verified_runs`.
- **Not run: T6.** The screen has not been seen on a device; SQLite was read on the JVM only.

---

# 8. Open loops

| Loop | Owner |
|---|---|
| A correction appended after the row was built is not seen as newer evidence | 16D |
| Dependents are not re-assembled when a prerequisite changes (the read asks the gate again) | 18E |
| `assessment_opportunity` has no producer | 16D |
| Topic state, weakness and misconception views, Technical English profile | 16C |
| Learning history, `assessment_report`, `evidence_history_entry` | 16B |
| `skill_detail → planner_explanation` is not an accepted IA edge | 17 |
| Objective labels (no statement in the store) and microcopy | 17 |
| T6 and the screen on a device | 19 |

---

# 9. Anti-patterns explicitly rejected

- A percentage, ratio, bar, gauge, level, rank, score or domain total of competence.
- A ninth Skill state, or `at_risk` as one.
- A state guessed for a Skill whose axes do not determine one, or a stale row shown as current.
- A second derivation of the eight states anywhere.
- Assisted, provisional or contaminated evidence shown as independent.
- Progress deciding what to do next.

---

**Next step:** **16B — Öğrenme geçmişi** (fresh PRE and the user's new explicit approval).
