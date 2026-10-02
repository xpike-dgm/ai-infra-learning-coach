# Wrong-Answer Analysis Implementation Specification — WAAX-v0

**Stage step:** 14B — Yanlış analizi  
**Status:** ACCEPTED — independent 14B QA PASS  
**Decision:** `D-106`  
**Model:** `WAAX-v0 — Wrong-Answer Analysis & Misconception Memory`  
**Behaviour it implements:** `WLRM-v0 / D-061` misconception contract and `review.6g.misconception_taxonomy_expansion`, `LEARNING_BEHAVIOR_RULES` §5 and §14, `MASTERY_SIGNALS_SPEC` §10, `AIAX-v0 / D-079` §5.1 (`misconception_hypotheses[]`), `SPWX-v0 / D-072` (a hypothesis is never a deficiency)  
**Attribution it reuses:** `WLRX-v0 / D-102` (the weakness engine's twelve rules, unchanged)  
**Persistence:** `LFPS-v0`, `DDM-v0`, `LDBX-v0` — schema v8 (one curriculum table, one projection; `evidence_event.misconception_tags` written for the first time)  
**Boundaries:** `MSBX-v0 / D-078` — one port refinement (`misconceptionsOf`), still the four ports plus `TutorPort` (`D-105`); no new engine family (the misconception contract is `WLRM-v0`'s)

## 1. Purpose

After a wrong answer the product has to say two things truthfully: **which Objective it says something about**, and **what kind of mistake it may have been** — and it must not overstate either. The first is the weakness engine's decision (13D). 14B adds the second: a misconception label, its memory over time, and the short analysis the learner reads right after the answer.

It answers one question:

> **Bu yanlış neyi gösteriyor — ve bunu ne kadar kesin söyleyebiliriz?**

Primary invariant:

> **A wrong answer is information, not a verdict about the learner.** A misconception label is only ever as strong as the evidence it rides on and its source allows: it moves exactly as far as the weakness engine's own rule moves its Objective, an AI's proposal never rises above a hypothesis, a label nobody declared for the Objective is never stored, and a hypothesis is put to the learner only as a question.

---

# 2. What was found before writing analysis code

- **Nothing wrote misconceptions.** `DDM-v0` §7.1 names `misconception_tags` on `evidence_event` and 10D created the column; nothing ever wrote or read it.
- **The evaluator contract had lost its hypotheses.** `AIAX-v0` §5.1 lists `misconception_hypotheses[]` in the minimum evaluation result; the Kotlin `EvaluationResult` carried only component results and the evaluator reference.
- **There was no misconception memory.** `WLRM-v0`'s `LearnerMisconceptionSignal` (hypothesis → supported → confirmed → resolved) had no projection, no owner in code and no rule for how a label rises.
- **There was no catalog to name a label against**, and `review.6g.misconception_taxonomy_expansion` (owner 14B) asks which labels should be production-authored — with "real learner errors" as a required input, which do not exist before the pilot.

---

# 3. User decisions (2026-10-01)

1. **A closed catalog.** Labels are declared in the published curriculum, one Objective version each, authored with the content (15). A label outside the catalog is not stored, whoever proposed it. 14B builds the mechanism; the catalog is empty until 15, and grows from pilot evidence in 18.
2. **A hypothesis is shown only as an open question,** once, right after the wrong answer — never as a weakness in Progress, where only supported and confirmed signals appear (`SPWX-v0`).

---

# 4. Scope boundary

## 4.1 14B decides

- the closed catalog: its shape, where it lives, how it is published and read,
- how an evaluation's proposals become labels on the evidence row,
- how a label rises, holds and resolves (the misconception memory),
- the analysis of one wrong answer and what the learner is told,
- the resolution of `review.6g.misconception_taxonomy_expansion`.

## 4.2 14B does not decide

- any weakness, mastery, retention or planner rule — the twelve `WLRM-v0` rules are asked as they are,
- the labels themselves (15) or their expansion from real errors (18),
- using the memory to choose items or `misconception_contrast` remediation content (15, with authored contrast items),
- the Progress view of supported/confirmed misconceptions (16C),
- calling the analysis from the app after a run (16D),
- the deterministic evaluator that maps an answer to a label (14D for code, 15 for authored keys) and the LLM evaluator prompts (14F).

No score, threshold, ratio, weight or count is introduced.

---

# 5. The closed catalog (`misconception`, schema v8)

A `MisconceptionRow`: `misconception.<namespace>.<slug>` (`GNS-v0`), its version, the **one Objective version** it belongs to, a name, and the **open question** it is put to the learner with (it must be a question). It is curriculum: published in the same transaction as its version, pinned, immutable (the same abort triggers as every curriculum table), refused with the whole package if it points at an Objective nobody published or carries another version. The authored package format gains a strict `[misconception]` section. `PersistencePort.misconceptionsOf(objective)` reads it — a refinement, not a new port.

`D-106` extends `DDM-v0`'s curriculum inventory by this one entity; the accepted 9C contract is not edited.

---

# 6. Labels on evidence

`EvaluationResult.Verified` and `.Provisional` carry `misconceptionHypotheses` (Objective + label id), restoring `AIAX-v0` §5.1. `RecordEvidence` keeps a proposal only if:

- the component **went wrong** (`not_met` or `partially_met`) — a right answer carries no misconception,
- it is about **that** component's Objective,
- the **catalog declares** the label for that Objective.

A proposal from a deterministic path (`Verified`) is recorded as `deterministic`; from an uncalibrated evaluator (`Provisional`), as `ai_proposed`. A non-answer writes nothing, so it can carry nothing. The labels are written to `evidence_event.misconception_tags` as strict `misconception_tags/1` text and read back on the row (`EvidenceRow.misconceptionTags`).

---

# 7. The memory (`MisconceptionEngine`, `misconception_state`)

Replayed from one Objective's evidence, in recording order, for each of its catalog labels:

- every row is attributed by **the weakness engine's own rule** against the Objective's state at that moment;
- a label the row carries moves as far as that rule would move the Objective: **hypothesis** on assisted, provisional or partial work; **supported** on a clean failure before mastery or a first clean contradiction after it; **confirmed** only when a fresh recheck fails and the mastery engine's gates no longer pass;
- **the source's ceiling caps it:** an `ai_proposed` label never rises above a hypothesis (`WLRM-v0`);
- invalid, contested or prerequisite-contaminated work, and a diagnostic baseline miss, **move no label**;
- nothing lowers an open label; a fresh, clean success on the Objective **resolves** an open hypothesis or support, and a confirmed label only when the gates pass again; time alone resolves nothing;
- a label the catalog does not declare for this Objective names nothing.

`RebuildWeakness` writes every catalog label's row (`none` if nothing carried it) with `DDM-v0` provenance, in the same transaction as the weakness rows. SQLite refuses a state outside `WLRM-v0`'s lifecycle, a source outside the two, an `ai_proposed` label above a hypothesis, and a resolution without its evidence. Owned by the `WLRM-v0` family (no new family); `D-106` extends `DDM-v0`'s projection inventory by this one table.

---

# 8. The analysis (`AnalyzeWrongAnswer`, `WrongAnswerSummary`)

`AnalyzeWrongAnswer.analyze(skill, profiles, evidenceIds)` returns, for each evidence row of the attempt, **the stored attribution** — the same events the memory is rebuilt from, attributed by the same engine — and each label's state after the row. It writes nothing, asks no AI and sends nothing anywhere.

`WrongAnswerSummary` turns it into what the learner reads:

| Attribution | What is said |
|---|---|
| not attributable / content or environment | it was not written against the learner |
| prerequisite signal | the target is not blamed; something needed first is not ready |
| hypothesis | a question mark; no conclusion was drawn |
| supported | a little more work on this Objective will help; one answer says nothing about the whole topic |
| verification due | it conflicts with something shown before; nothing was erased; it will be rechecked with an unseen item |
| remediation required | marked for targeted work; the plan decides when |

Labels are mentioned **only** on a row that said something about the target. A hypothesis appears only as its catalog question; a supported or confirmed label is named; a resolved one is not. There is no score, percentage, number or grade, and no word that blames.

---

# 9. `review.6g.misconception_taxonomy_expansion` — resolved

The 6G question was which domain-specific labels should be production-authored. 14B resolves the part it owns: **the runtime contract and a closed, curriculum-authored catalog**, with an AI allowed to choose only from it. Which labels to author is content (15), and expanding the catalog from real learner errors is calibration (18). The review is closed with those owners named.

---

# 10. Port and model refinements

- `PersistencePort.misconceptionsOf(objective)` — ports remain `MSBX-v0`'s four plus `TutorPort` (`D-105`).
- `EvaluationResult.*.misconceptionHypotheses`, `EvidenceRow.misconceptionTags`, `WeaknessEvent.misconceptionTags`, `CurriculumPackage.misconceptions`, `RebuildWeakness.Rebuilt.misconceptions`.
- `WeaknessEvents` is the one place one Skill's evidence becomes weakness events; `RebuildWeakness` and `AnalyzeWrongAnswer` both read it, so the analysis cannot drift from the stored state.

---

# 11. Verification

- Suites: `MisconceptionFactsTest` (model), `MisconceptionEngineTest` (engine), `WrongAnswerAnalysisTest` (application), `WrongAnswerPresentationTest` (presentation), `MisconceptionStorageTest` (real SQLite, v7 → v8 on a populated fixture), `PackageFormatTest` (the strict section).
- Validator `tools/validate_wrong_answer_analysis.py`, reading `WLRM-v0`'s misconception contract and state contract, `AIAX-v0` §5.1, `SPWX-v0` and the 6G review queue against the Kotlin, with its own mutation test.
- Runs: T1, T2, T3, T5 and both builds — all PASS; 864 JVM tests.
- **Mutation 51/51**, only the 14B suites running; the comment-only control survived; a compile failure is not a detection. Before the harness ran, three rules had no test (a row no rule speaks for, a label of another version, a label pinned to an Objective an earlier version published) and tests were added. In the first run E03 survived — no test had a fresh recheck failing while the gates still pass — so that case was added and the whole set re-run from one unchanged tree. One mutant (E05) was re-run by hand and named tests were seen failing.
- Two living gates were narrowed without weakening them: `E13D-09` (event building moved into `WeaknessEvents.of`; the watermark is still read before any evidence, a row without a day is still refused, and there is still one timeline) and `E13F-11_version` (13F still owns exactly 6 → 7; v8 is owned by this step's `schema_migration`).
- Sweep 50/50; independent QA 154/154; validator mutation 28/28 with no false positive from forbidden fragments inside comments.
- **Not run: T6.** Nothing in the app shows the analysis yet (16D), and no evaluator produces labels yet (14D/14F/15).

---

# 12. Open loops

| Loop | Owner |
|---|---|
| authored labels in the catalog; deterministic answer-to-label keys | 15 |
| using the memory for item choice and `misconception_contrast` content | 15 |
| the Progress view of supported/confirmed misconceptions | 16C |
| calling the analysis after a run | 16D |
| expanding the catalog from real learner errors; supported/confirmed escalation calibration | 18 (`review.6g.calibration`, 18C) |
| code evaluation producing labels | 14D |
| LLM evaluator prompts producing proposals | 14F |

---

# 13. Anti-patterns explicitly rejected

- a label nobody declared, stored because a model proposed it,
- an AI proposal raised above a hypothesis,
- a label rising faster than its Objective's own attribution,
- a label moved by invalid, contested or prerequisite-contaminated work, or by a diagnostic baseline miss,
- a misconception on a right answer,
- a label resolved by time, or by a success on the same item or family,
- a hypothesis stated as a finding or shown in Progress,
- a label mentioned after a row that blamed nothing,
- a wrong answer read as a verdict about the learner, the Topic or the Skill,
- an analysis that recomputes its own attribution instead of reading the engine's.

---

# 14. 14B acceptance contract

1. `WAAX-v0` is the accepted wrong-answer analysis and misconception memory.
2. A closed catalog (user decision), curriculum-authored, pinned, immutable; undeclared labels are never stored.
3. Labels are recorded only on rows that went wrong, from a deterministic path as `deterministic` and from an uncalibrated evaluator as `ai_proposed`.
4. A label moves exactly as the weakness engine's rule moves its Objective, capped by its source; nothing unattributable moves it; it resolves only on fresh clean evidence.
5. The analysis is the stored attribution; a hypothesis is shown only as a question (user decision).
6. Schema v8, one curriculum table and one projection under `D-106`; one port refinement; no new engine family.
7. `review.6g.misconception_taxonomy_expansion` is resolved with its owners.
8. Nothing is claimed that was not run; T6 was not run.
9. Independent 14B QA passes and the full `validate_*.py` sweep passes.

---

# 15. Handoff after acceptance

If accepted, 14B becomes `WAAX-v0 / D-106`.

Next numbered step: **14C — Alternatif anlatım**. It must receive a fresh PRE-STEP and explicit user approval before execution.

---

**14C note (2026-10-01, `D-107`):** the misconception memory now decides, on the device, which written `misconception_contrast` explanations may be offered (a label held open: hypothesis, supported or confirmed); it is offered as a common mix-up and never sent to a provider. Writing the contrasts stays with 15. Details: `docs/ALTERNATIVE_EXPLANATION_IMPL_SPEC.md`.

---

**15B note (2026-10-02, `D-113`, user decision):** a label's version is its own, not the package's. A later package may pin a new version-1 label to an Objective an earlier package published; carrying an already-published label refuses the package. `E14B-03_version_refused` was narrowed accordingly. Details: `docs/PYTHON_FOUNDATIONS_CONTENT_SPEC.md`.
