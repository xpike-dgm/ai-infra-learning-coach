# 14D Code Evaluation — Research & Decision Synthesis

**Stage step:** 14D — Kod değerlendirme  
**Purpose:** Decide who may judge a learner's code, where the deterministic path runs, and what a result proves for each Objective.

## 1. Research-need decision

**No web research pass was needed.** The evidence rules are fixed by accepted texts: `AIAX-v0` §5.2 (`verified` only from a deterministic path — "compiler + objective-specific tests"; an uncalibrated LLM is `provisional`) and §6 (a refusal is not a wrong answer); `MSS-v0` §5.4 and `2D` "Test runner" (tests verify correctness, not understanding); `QAB-v0` §2 (`coding_task`) and §21 (`test_output_required`); `LEARNING_BEHAVIOR_RULES` §1 (the phone is not an IDE). Where the tests run and whether an AI may write evidence for code were product questions and went to the user (§4).

## 2. Canonical source set reviewed

- `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md` §4–§6, §11.
- `docs/MASTERY_SIGNALS_SPEC.md` §5.4; `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md` (compiler feedback, test runner); `docs/MASTERY_FORMULA_V0.md` (`verified` examples).
- `docs/QUESTION_BANK_SPEC.md` §2, §20, §21; `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` §26.
- `docs/LEARNING_BEHAVIOR_RULES.md` §1, §16.
- `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md` (14A), `docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md` (14B).
- The code: `EvidencePipeline.kt`, `EvaluatorPort`, `NullEvaluator`, `AiEvaluator`, `AssessmentItem`.

## 3. What was found

1. No production code produced an evaluation; the evidence pipeline had nothing to record.
2. The accepted deterministic path for code (compiler + tests) had nowhere to run: the learner's code lives on their computer.
3. Nothing kept a test to its Objective.
4. Nothing stopped an evaluator port from returning `Verified`.

## 4. Positions taken

- **User decisions (2026-10-01):** tests run on the learner's computer and the report is imported; without tests, an AI may write provisional evidence only where the task allows it.
- **A reference runner ships in the repo** (`tools/code_test_runner.py`, standard library only), so the report format is produced by real code, not described; its output on fixtures is the Kotlin tests' input and the validator compares them byte for byte.
- **What did not run measured nothing** (12A's "an unmeasurable answer is not zero"): a failed build blames only an authored build Objective; a test that did not run leaves its Objective unmeasured; a timeout is a failure because the authored limit is part of the test.
- **No fallback from tests to AI:** a missing or malformed report never becomes an AI judgement.
- **The report is the learner's own run**; personal use (`D-080`) — not signed, and not presented as tamper-proof.

## 5. Explicitly not decided in 14D

The tests themselves (15); the adapter prompt and call site (14G); comprehension of AI-written code (14E); open-ended evaluation (14F); storing the report and calling from the app (16D); calibrating an AI code evaluator (18). T6 and a C build were not run.
