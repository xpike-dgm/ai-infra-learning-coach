# 14F Open-Response Evaluation — Research & Decision Synthesis

**Stage step:** 14F — Açık uçlu cevap değerlendirme  
**Purpose:** Decide what a free-text answer is judged against, what an AI may say about it, and what happens when no evaluator answers.

## 1. Research-need decision

**No web research pass was needed.** The rules are fixed by accepted texts: `AIAX-v0` §5 (schema-constrained output, `rubric_findings[]`, uncalibrated LLM → `provisional`), §6 (refusal is not a wrong answer), §7.1 (no silent background retry), §11 (minimum content); `QAB-v0` §19 (normalized answer key, multiple valid answers, rubric; a single uncalibrated LLM never yields `verified` critical evidence) and §20 (evaluator failure is not learner failure); `AIV-v0` §26; `MSS-v0` §5.6 (polish is not evidence). Two product questions went to the user (§4).

## 2. Canonical source set reviewed

- `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md` §4–§8, §11.
- `docs/QUESTION_BANK_SPEC.md` §19, §20; `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md` §8, §26.
- `docs/MASTERY_SIGNALS_SPEC.md` §5.6, §7.1; `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` §26.
- `docs/CODE_EVALUATION_IMPL_SPEC.md` (14D), `docs/CODE_COMPREHENSION_IMPL_SPEC.md` (14E), `docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md` (14B).
- The code: `EvaluationRequest`, `EvaluationResult`, `EvidencePipeline`, `AiEvaluator`.

## 3. What was found

1. `EvaluationRequest` had no rubric, so an AI could only judge by its own idea of a good answer.
2. `EvaluationResult` had dropped `rubric_findings[]`, so an evaluator could only return the decision itself.
3. Short answers had no deterministic path.

## 4. Positions taken

- **User decisions (2026-10-02):** short answers by an authored accepted-answer list (verified; a mismatch never falls to an AI); a waiting response offers a rubric self-check as practice and is re-evaluated only on request.
- **The AI proposes per-criterion findings only; core derives each Objective's signal** with the same "what could not be judged measured nothing" rule as 14D and 14E. Its own per-Objective verdict is ignored.
- **Matching normalises only the ends and line endings**, and a case-insensitive key folds ASCII letters only — no silent merge of Turkish `ı`/`I`/`i`/`İ`.
- **Opening the rubric is a shown solution** and is recorded as an exposure, so the item is not reused as a fresh measurement.
- **The catalog's labels travel with the request**, so the evaluator can only propose labels the course declared (14B).

## 5. Explicitly not decided in 14F

Keys and rubrics (15); the adapter call site (14G); calibration (18); storing responses and the app screens (16D). T6 was not run.
