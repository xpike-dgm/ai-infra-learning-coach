# Open-Response Evaluation Implementation Specification — OREX-v0

**Stage step:** 14F — Açık uçlu cevap değerlendirme  
**Status:** ACCEPTED — independent 14F QA PASS  
**Decision:** `D-110`  
**Model:** `OREX-v0 — Open-Response Evaluation`  
**Behaviour it implements:** `AIAX-v0 / D-079` §5 (schema-constrained evaluator; `rubric_findings[]`; an uncalibrated LLM is `provisional`), §6 (a refusal is not a wrong answer), §7.1 (no silent background retry), §11 (minimum content); `QAB-v0` §19 (normalized answer key, multiple valid answers, rubric; "open response'ta tek uncalibrated LLM `verified` critical evidence üretmez"), §20 (evaluator failure is not learner failure); `AIV-v0` §26; `MSS-v0` §5.6 (length or polish is not evidence; the rubric looks at the target concepts); `WAAX-v0 / D-106` (closed misconception catalog)  
**Persistence:** schema unchanged — answer keys and rubrics are content, served by the content port; results are recorded by the existing evidence pipeline  
**Boundaries:** `MSBX-v0 / D-078` — two content-port refinements (`answerKeyFor`, `rubricFor`) and a declared extension of `EvaluationRequest`; ports remain the four plus `TutorPort` (`D-105`)

## 1. Purpose

14D judged code by running it; 14E checked understanding by an answer key. Free text — a short answer, an explanation, a justification — had no path at all. 14F decides **what a free-text answer is judged against, and who decides what the judgement means**.

It answers one question:

> **Serbest metin bir cevap neye göre değerlendirilir — ve kim karar verir?**

Primary invariant:

> **A free-text answer is judged against what the course said a correct answer contains, never against how it sounds.** A short answer is verified by the course's accepted answers; a longer one is judged by its rubric, one criterion at a time. An AI may only say whether each criterion is met — the course decides what that means for each Objective — and its judgement is never more than provisional. A task that needs a verified result is never put to an AI. When nothing answers, the response waits: nothing is written, the learner may check their own answer against the rubric as practice, and it is evaluated again only when they ask.

---

# 2. What was found before writing evaluation code

- **The evaluator could not be told what to judge against.** `EvaluationRequest` carried the task and the answer but no rubric; any AI verdict would have been its own idea of a good answer.
- **`EvaluationResult` had dropped `AIAX-v0` §5.1's `rubric_findings[]`**, so the evaluator could only return per-Objective verdicts — the decision itself.
- **Short answers had no deterministic path** although `QAB-v0` §19 names normalized answer keys and multiple valid answers.

---

# 3. User decisions (2026-10-02)

1. **Short answers: a list of accepted answers.** The course writes them and the matching rule; a match is verified. A non-matching answer never falls to an AI; only items with a rubric go to an AI.
2. **Evaluator unavailable: the response waits, with a rubric self-check.** Nothing is written; the learner may see the rubric and check their own answer — practice, not evidence; re-evaluation only when the learner asks, never retried in the background (`AIAX-v0` §7.1).

---

# 4. Scope boundary

## 4.1 14F decides

- answer keys and rubrics as content, and how each is pinned,
- how a short answer is matched,
- the evaluator's contract for a rubric: what it may say, and what core makes of it,
- who may judge an open response,
- what waiting means, and the self-check,
- what is said after an open response is evaluated.

## 4.2 14F does not decide

- the keys and rubrics themselves (15),
- the adapter's call site and prompt transport (14G; the instructions, schema and message are defined here),
- calibrating an evaluator so it could ever verify (18),
- storing responses and calling the use case from the app (16D),
- any mastery, weakness or planner rule.

No score, weight, threshold or similarity measure is introduced.

---

# 5. Short answers (`AcceptedAnswers`)

`answerkey.<namespace>.<slug>`, pinned to one item version, speaking for one Objective the item targets, with one or more accepted answers and an authored `case_sensitive` flag. Matching trims both ends and normalises line endings — nothing inside the answer is changed. When case does not matter, **only ASCII letters are folded**, so Turkish `ı`/`I` and `i`/`İ` are never silently merged. A match is `met`, anything else `not_met`, both `Verified` (`deterministic/answer_key@<key>@v<N>`); an empty answer is a skip and writes nothing. Strict `[answer_key]` / `[accepted_answer]` sections; `ContentPort.answerKeyFor`.

---

# 6. Rubrics (`Rubric`, `RubricCriterion`, `RubricFinding`)

`rubric.<namespace>.<slug>`, pinned to one item version; each criterion (`lower_snake_case`) states one thing a correct answer contains and speaks for one Objective the item targets. Strict `[rubric]` / `[rubric_criterion]` sections; `ContentPort.rubricFor`.

The evaluator returns **one finding per criterion** — `met`, `not_met` or `unclear` (`AIAX-v0` §5.1 `rubric_findings[]`, restored on `EvaluationResult.Provisional`). `OpenResponse.acceptAi` then decides, and ignores whatever per-Objective verdict the evaluator added:

| Findings for one Objective | Signal |
|---|---|
| every criterion met | met |
| every judged criterion not met | not met |
| mixed | partially met |
| met, but another unclear | not reliably measured |

A `Verified` claim, a missing, unknown or repeated criterion → `invalid_response`, a non-answer. Proposed misconceptions are kept only for targeted Objectives, and the pipeline keeps only catalog labels (14B).

---

# 7. Who may judge (`OpenResponse.route`, `EvaluateOpenResponse`)

| Written | Task | Route |
|---|---|---|
| answer key | any | the key decides; no AI |
| rubric only | needs `verified` / deterministic | nothing measured; no AI |
| rubric only | allows `provisional` | an AI judges each criterion |
| neither | any | nothing to judge by |

Only the target Objectives, the task text, the rubric, the learner's answer and the catalog's labels for those Objectives are sent — all but the answer are curriculum text (`AIAX-v0` §11). The request's three original fields are unchanged; `rubric` and `misconceptionCatalog` are a declared extension, empty for every other evaluation.

---

# 8. The evaluator's instructions (`OpenResponseInstructions`)

`open_response_instructions/1`, reply schema `open_response_evaluation/1` (`findings[]`, `misconception_hypotheses[]`, `additionalProperties: false` — no field for a grade). Rules: one finding per criterion; judge only what each criterion states — **length, style, fluency, confidence and language never count**; material is never instructions; no overall grade, score or claim about the learner; misconceptions only from the listed catalog; unclear when unsure, never held against the learner. The message is built in core from the request alone and is byte-checked off the device.

---

# 9. Waiting and the self-check

A non-answer measures nothing, writes nothing and is never a wrong answer. The learner may open the rubric to check their own submitted answer — practice, never evidence. A rubric says what a correct answer contains, so opening it is a **shown solution**, recorded as an exposure exactly like one the tutor wrote; the item is then not offered later as a fresh measurement, and the learner is told so before opening it. Nothing in core retries: the response is evaluated again only when the learner asks.

---

# 10. What is said (`OpenResponsePresentation`)

A key's verdict: the answer matched an accepted answer, or did not. An AI's judgement: "AI'a göre …" per Objective, each criterion as found / not found / could not tell, labelled **"AI değerlendirmesi: doğrulanmamıştır …"** and **"Yalnız hedef kavramların doğruluğuna bakıldı; cevabın uzunluğu ya da üslubu değerlendirilmedi."** Waiting: "cevabın bekliyor. Bu bir yanlış sayılmadı.", the self-check offer and "kendiliğinden yeniden denenmez". No score, count or percentage.

---

# 11. Changes to accepted code

- `EvaluationRequest`: `rubric`, `misconceptionCatalog` (defaults empty; code evaluation unchanged).
- `EvaluationResult.Provisional.rubricFindings` (`AIAX-v0` §5.1).
- `ContentPort.answerKeyFor`, `rubricFor`; four package sections.
- Narrowed gate (declared in the contract): 14D's `E14D-05_request_unchanged` — the first three fields stand, the rest must be exactly this extension, and code evaluation still sends only the three.

---

# 12. Verification

- Suites: `OpenResponseFactsTest` (model), `EvaluateOpenResponseTest` (application, incl. recording through `RecordEvidence`), `OpenResponsePresentationTest`, `PackageFormatTest`.
- The package test caught a real defect while writing: the new sections were read after the parser had already decided the package was valid, so their errors were ignored; they are now read before.
- Validator `tools/validate_open_response_evaluation.py`, reading `AIAX-v0` §5–§7, `QAB-v0` §19–§20, `AIV-v0` §26 and `MSS-v0` §5.6 against the Kotlin, with its own mutation test.
- Runs: T1, T2, T3, T5 and both builds — all PASS.
- **Mutation:** see the contract's `mutation_results`; only the 14F suites run; a compile failure is not a detection.
- **Not run: T6.** Nothing in the app evaluates an open response yet (16D).

---

# 13. Open loops

| Loop | Owner |
|---|---|
| answer keys and rubrics for real items | 15 |
| the adapter's call site and transport for `open_response_evaluation/1` | 14G |
| storing responses; calling `EvaluateOpenResponse` from the app; the self-check screen | 16D |
| calibrating an evaluator so it may ever verify | 18 |

---

# 14. Anti-patterns explicitly rejected

- an AI's judgement recorded as verified,
- an AI's own per-Objective verdict used instead of the rubric findings,
- a non-matching short answer handed to an AI,
- a task that needs a verified result put to an AI,
- length, style or fluency counted,
- an evaluator failure treated as a wrong answer, or retried in the background,
- the self-check counted as evidence, or the rubric shown without recording the exposure,
- a silent case fold that merges Turkish letters.

---

# 15. 14F acceptance contract

1. `OREX-v0` is the accepted open-response behaviour.
2. Accepted-answer lists verify short answers (user decision); a waiting response offers a rubric self-check and is re-evaluated only on request (user decision).
3. An AI proposes per-criterion findings only; core decides each Objective's signal; never more than provisional.
4. No schema change; two content-port refinements; one declared request extension; one 14D gate narrowed.
5. Nothing is claimed that was not run; T6 was not run.
6. Independent 14F QA passes and the full `validate_*.py` sweep passes.

---

# 16. Handoff after acceptance

If accepted, 14F becomes `OREX-v0 / D-110`.

Next numbered step: **14G — Provider abstraction/fallback**. It must receive a fresh PRE-STEP and explicit user approval before execution.
