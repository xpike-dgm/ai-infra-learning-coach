---
type: session-log
status: completed
stage_step: 15B
model: PYFX-v0
decision: D-113
date: 2026-10-02
---

# 15B — Python Foundations — the learner writes code

15A (#52) had been merged into main (6e889e6). A fresh 15B PRE was done, and the five canonical sources showed `15A ✅ / 15B active-not-executed`. The user gave explicit approval ("merge edildi devam et") and answered two product questions.

## Result
- Canonical: `docs/PYTHON_FOUNDATIONS_CONTENT_SPEC.md`
- Machine-readable: `arch/15b_python_foundations/python_foundations.yaml`
- Content verification: `arch/15b_python_foundations/content_verification.yaml`
- QA: `arch/15b_python_foundations/qa_report.yaml`
- Stale audit: `arch/15b_python_foundations/stale_reference_audit.yaml`
- Research/synthesis: `research/15b_python_foundations_research.md`
- Content source: `curriculum/content/15b_python_foundations/` → `tools/build_curriculum_package.py` → `android/app-wiring/src/main/assets/curriculum_package_v2.txt` and `curriculum/content/15b_python_foundations/suites/`
- Code:
  - core-ports: `ContentPort.curriculumPackages`, with a default
  - core-application: `IngestCurriculum.ingestAll`
  - data-persistence: `CurriculumStore.republishedEntities`
  - data-curriculum: `FileContentSource` reads several packages, all or none
  - app-wiring: later assets are read and ingested; `ShippedCourseTest`; the JVM SQLite driver is used in unit tests only
  - tools: `code_test_runner.py` runs in UTF-8 mode
- Final: `PYFX-v0 / D-113`
- T6 and on-device ingestion: not run. The SQLite publish of both shipped packages ran on the JVM only.

## User decisions
- Scope: the seventeen decomposed seeds plus their three outside hard prerequisites, 20 Skills in total.
- Shipping: incremental packages, where an entity's version is its semantic revision. 11D's version-equality rule was narrowed to "a published version is never overwritten".

## Found
- A second package could not ship. There was one asset, and entities were tied to the package's version.
- The 14D runner failed correct Turkish output on Windows because of the cp1254 console.
- Three seed Skills could never become ready, because their hard prerequisites were not seeds.
- Writing a function is needed, but the graph does not make it a prerequisite.
- Text-mode files (CRLF) and tracebacks (caret lines) differ by platform and by Python version.

## Independent review
- **First pass:** every key, traceback and lesson claim was correct. 98 of 109 items and 113 of 116 explanations passed. The failures were:
  - 6 items with lesson leakage
  - 4 items whose tests a plausible wrong solution passed
  - 1 prompt that did not state what its test required
  - 3 explanation sentences
- **Second pass:** one item still failed, because its test rejected a correct solution that uses `.get()`.
- **Third pass:** 109/109 items, 116/116 explanations.

## Verification
- Mutation: 27/27 detected. The first run detected 19; the survivors exposed four test gaps, each closed. The control mutant survived.
- Validator: 254/254; its own mutation test: 35/35; sweep 57/57; 992 JVM tests.

## Durable decisions
A program is judged by what it does, on the learner's own computer, against tests that can tell a right solution from a plausible wrong one. A later package only adds to what is published.

## Next
After POST, 15C — C Foundations is active-not-executed. It needs a fresh PRE and explicit user approval.
