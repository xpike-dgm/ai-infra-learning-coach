# Code Evaluation Implementation Specification — CDEX-v0

**Stage step:** 14D — Kod değerlendirme  
**Status:** ACCEPTED — independent 14D QA PASS  
**Decision:** `D-108`  
**Model:** `CDEX-v0 — Code Evaluation`  
**Behaviour it implements:** `AIAX-v0 / D-079` §5 (schema-constrained evaluator, `verified` only from a deterministic path such as compiler + objective-specific tests, an uncalibrated LLM is `provisional`), §6 (a refusal is not a wrong answer), §11 (what may leave the device); `MSS-v0` §5.4 (passing tests are verifying artifacts, not understanding); `2D` "Test runner"; `QAB-v0` §21 (`ArtifactRequirement.test_output_required`), §2 (`coding_task`); `LEARNING_BEHAVIOR_RULES` §1 (the phone is not an IDE; code is run on the computer and brought back); `WAAX-v0 / D-106` (a failing test may name a catalog misconception)  
**Persistence:** schema unchanged — code tests are content, served by the content port; results are recorded by the existing evidence pipeline  
**Boundaries:** `MSBX-v0 / D-078` — one content-port refinement (`codeTestsFor`); ports remain the four plus `TutorPort` (`D-105`)

## 1. Purpose

Until 14D nothing in the app produced an evaluation: the evidence pipeline (12A) could record one, the evaluator port could be asked for one, but no code path judged a learner's code. 14D decides **who may judge code, and what the judgement proves**.

It answers one question:

> **Bir kodun doğru olduğunu ne kanıtlar — ve kim söyleyebilir?**

Primary invariant:

> **Code is judged by running it, or it is only an opinion.** A code task is verified only by the course's own tests, run on the learner's computer and read back strictly, and a test speaks only for the Objective it was written for. A test that did not run measured nothing, and nothing is held against the learner for it. Without tests, an AI may judge the code only where the task allows a provisional result, and never as more than provisional; a task that needs a verified result is never put to an AI. Passing tests show that the code does what was asked — not that the learner can explain why.

---

# 2. What was found before writing evaluation code

- **No evaluation was ever produced.** `RecordEvidence` accepts an `EvaluationResult`, and `EvaluatorPort` exists, but no production code called the port or built a result; the only evaluator implementations were the null evaluator and the unwired AI adapter.
- **The accepted deterministic path for code could not run where the learner is.** `AIAX-v0` §5.2 names "compiler + objective-specific tests"; `LEARNING_BEHAVIOR_RULES` §1 says the phone is not an IDE and the learner runs code on their computer. Nothing connected the two.
- **Nothing kept a test to its Objective.** A multi-Objective coding task had no way to say which test speaks for which Objective, so one failing test would have had to count against every target.
- **An AI could have claimed verification.** The port's result type allows `Verified`; nothing stopped an evaluator port from returning it.

---

# 3. User decisions (2026-10-01)

1. **Tests run on the learner's computer and the report is imported.** The course supplies a small runner; the learner runs it against their own code and pastes the report back. Nothing compiles or runs on the phone.
2. **Without tests, an AI's evaluation is provisional evidence** — only where the task allows a provisional result. A task that needs a verified result is never put to an AI; where tests exist they write the evidence and an AI may only give feedback (`TUTX-v0` `explain_mistake`).

---

# 4. Scope boundary

## 4.1 14D decides

- what the course's tests are, how they are pinned to an item version and to Objectives,
- the report format and how strictly it is read,
- what a report proves for each Objective, including a failed build, a timeout and a test that did not run,
- who may judge a code task (tests, a provisional AI, or nobody yet),
- what core accepts from an evaluator about code,
- what is said after code is evaluated,
- the reference runner the learner runs.

## 4.2 14D does not decide

- the tests themselves (15),
- the adapter's prompt and call site (14G; the AI's request shape is the existing `EvaluationRequest`),
- comprehension of AI-written code (14E) and open-ended evaluation (14F),
- storing the pasted report as an artifact and calling the use case from the app (16D),
- any mastery, weakness, retention or planner rule.

No score, threshold, weight or tolerance is introduced. The time limit and the output comparison are authored per suite.

---

# 5. Tests as content (`CodeTestSuite`, `codeTestsFor`)

A suite is `codetest.<namespace>.<slug>` (`GNS-v0`), pinned to one item version, with one or more tests. Each test (`lower_snake_case` id) names **the one Objective its result speaks for** and, optionally, a catalog misconception its failure points at (`WAAX-v0`). A suite may name a **build Objective**: only then can a failed build count against anything; the build and a test never share an Objective. Authored in strict `[code_test_suite]` / `[code_test]` package sections; a test of an undeclared suite, a suite without tests, or two suites for one item version make the package unreadable. Served by `ContentPort.codeTestsFor(item)` for exactly that item version.

---

# 6. The runner and its report

`tools/code_test_runner.py` (standard library only) runs on the learner's computer: it reads a `code_test_suite/1` JSON (commands, authored `timeout_seconds`, authored `compare` = `exact` | `trim_trailing_whitespace`, the tests' stdin/args/expected output/optional exit code), runs the build and each test without a shell, and prints `code_test_report/1` as bytes identical on every operating system:

```text
code_test_report/1
item: <logical_id>@v<N>
suite: <logical_id>@v<N>
build: ok | failed | not_required | environment_error
test: <id> passed | failed | timed_out | not_run | error
end
```

A command that cannot be started is `environment_error` / `error`: it says nothing about the code. A failed build leaves every test `not_run`. The app reads the report completely or not at all (`CodeTestReports.decode`); surrounding blank lines and CRLF are accepted, anything else out of place is malformed.

The report is the learner's own run, pasted by them. The product is for personal use (`D-080`); the report is not signed and nothing pretends it is tamper-proof.

---

# 7. What a report proves (`CodeEvaluation.evaluate`)

- A report for another item, another suite version, or a different test set; a skipped build where the suite names a build Objective; or a runner environment error — **measures nothing**, and nothing is written.
- **Build:** the build Objective (if named) is met when the code built and not met when it did not. With no build Objective a failed build blames nothing.
- **Tests:** each Objective is judged by its own tests only. Met only when every one ran and passed; all that ran failed → not met; mixed → partially met; passes while another of its tests did not run → **not reliably measured** (never claimed as met). A failed build leaves every tested Objective not reliably measured. A timeout is a failure: the authored limit is part of the test.
- A failing test that names a catalog misconception proposes it for its Objective; the pipeline keeps it only on a row that went wrong and only if the catalog declares it (14B).
- The result is `Verified`, `evaluator = deterministic/code_tests@<suite>@v<N>|code_test_report/1`, recorded by the existing pipeline like any other evaluation; a not-reliably-measured component is an `invalid` row, never a zero.

---

# 8. Who may judge (`CodeEvaluation.route`, `EvaluateCode`)

| Situation | Route | AI asked? |
|---|---|---|
| The course has tests for the item version | `TESTS` | never, whatever the task allows |
| No tests; the task needs `verified`, or requires a deterministic evaluator, or declares deterministic verification | `TESTS_REQUIRED` — nothing measured yet | never |
| No tests; the task allows `provisional` | `AI_PROVISIONAL` | yes — task text and code only |

A tested task without a report, or with a malformed one, measures nothing and **never falls back to an AI**. A report for a task without tests is not used. An empty submission is a skip and sends nothing.

`CodeEvaluation.acceptAi`: an evaluator port that returns `Verified` has answered outside its contract, as has one that judges no Objective, one twice, or one the task does not target — each is `invalid_response`, a non-answer. Every non-answer (`refused`, `timed_out`, `transport_error`, `invalid_response`, `unavailable`) measures nothing and is never a wrong answer.

---

# 9. What is said (`CodeEvaluationPresentation`)

One sentence per Objective, from the result alone ("Bu hedefin kontrolleri geçti / bazıları geçmedi / geçmedi"; an unmeasured Objective "ölçülemedi … Bu bir yanlış sayılmadı"). An AI's judgement is worded as its view ("AI'a göre …") and labelled **"AI değerlendirmesi: doğrulanmamıştır. Bilgi verir ama tek başına bir beceriyi geçirmez."** A tested result carries **"Testlerin geçmesi kodun istenen şekilde çalıştığını gösterir; neden çalıştığını açıklayabilmek ayrıca ölçülür."** Every reason nothing was measured is said plainly, and none caused by a report, a runner or an evaluator is called the learner's mistake. No score, count or percentage.

---

# 10. Verification

- Suites: `CodeEvaluationFactsTest` (model, reading the runner's real reports from `code_test_14d/`), `EvaluateCodeTest` (application, including recording through `RecordEvidence`), `CodeEvaluationPresentationTest`, `PackageFormatTest` (sections and the content port).
- The runner was run on five fixture programs (`tools/fixtures/code_test_14d/`: correct, wrong, does not build, never finishes, missing directory); its outputs are the model tests' resources, and the validator re-runs it and compares byte for byte.
- Validator `tools/validate_code_evaluation.py`, reading `AIAX-v0` §5–§6, `MSS-v0` §5.4, `QAB-v0` §21 and `LEARNING_BEHAVIOR_RULES` §1 against the Kotlin and the runner, with its own mutation test.
- Runs: T1, T2, T3, T5 and both builds — all PASS.
- **Mutation:** see the contract's `mutation_results`; only the 14D suites run; a compile failure is not a detection.
- **Not run: T6.** Nothing in the app evaluates code yet (16D). **Not run: a C build.** No C compiler is installed where this was built; the runner's build step was exercised with Python's `py_compile`, and C suites are exercised the same way once a compiler is present.

---

# 11. Open loops

| Loop | Owner |
|---|---|
| the suites and their runner files for real tasks | 15 |
| storing the pasted report as an artifact; calling `EvaluateCode` from the app; giving the learner the suite file | 16D |
| the AI evaluator's prompt and call site for code | 14G |
| comprehension of AI-written code | 14E |
| open-ended (non-code) evaluation | 14F |
| calibrating an AI code evaluator so it may ever be more than provisional | 18 |

---

# 12. Anti-patterns explicitly rejected

- an AI asked about a task the course has tests for,
- an AI's judgement recorded as verified,
- a task that needs a verified result put to an AI,
- a missing or broken report replaced by an AI's opinion,
- one failing test counted against every Objective,
- a failed build blamed on Objectives it does not speak for,
- a test that did not run counted as a failure, or passes claimed as met while a test did not run,
- a runner or environment error treated as a wrong answer,
- a report for another item or suite version accepted,
- passing tests presented as understanding,
- the answer key, test expectations or learner history sent to a provider.

---

# 13. 14D acceptance contract

1. `CDEX-v0` is the accepted code-evaluation behaviour.
2. Tests run on the learner's computer and the report is imported (user decision); a provisional AI judges only untested tasks that allow it (user decision).
3. A test speaks only for its Objective; a failed build only for the build Objective; what did not run measured nothing.
4. An evaluator port can never verify; every non-answer measures nothing.
5. No schema change; one content-port refinement; results recorded by the existing pipeline.
6. Nothing is claimed that was not run; T6 and a C build were not run.
7. Independent 14D QA passes and the full `validate_*.py` sweep passes.

---

# 14. Handoff after acceptance

If accepted, 14D becomes `CDEX-v0 / D-108`.

Next numbered step: **14E — AI-generated code comprehension check**. It must receive a fresh PRE-STEP and explicit user approval before execution.

---

**14E note (2026-10-02, `D-109`):** passing tests on code the learner did not write is still not their production: such a submission is `requires_independent_recheck` and is followed by an optional comprehension check (`ACCX-v0`). Details: `docs/CODE_COMPREHENSION_IMPL_SPEC.md`.
