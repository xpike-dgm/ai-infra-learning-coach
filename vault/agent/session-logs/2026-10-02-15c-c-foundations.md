---
type: session-log
status: completed
stage_step: 15C
model: CFNX-v0
decision: D-114
date: 2026-10-02
---

# 15C — C Foundations — the learner writes C

15B (#53) was merged into main (f9b42c6). A fresh 15C PRE showed `15B ✅ / 15C active-not-executed` in all five canonical sources. The user approved explicitly ("merge edildi devam et"), answered three product questions, and installed WSL Ubuntu with gcc and build-essential themselves. The assistant did not enter the sudo password the user offered; the user ran the command.

## Result
- Canonical: `docs/C_FOUNDATIONS_CONTENT_SPEC.md`
- Machine-readable: `arch/15c_c_foundations/c_foundations.yaml`
- Content verification: `arch/15c_c_foundations/content_verification.yaml`
- QA: `arch/15c_c_foundations/qa_report.yaml`
- Stale audit: `arch/15c_c_foundations/stale_reference_audit.yaml`
- Research/synthesis: `research/15c_c_foundations_research.md`
- Content pipeline: `curriculum/content/15c_c_foundations/` → `tools/build_curriculum_package.py` (Linux through WSL) → `android/app-wiring/src/main/assets/curriculum_package_v3.txt` and `suites/`
- Code:
  - data-curriculum: `PackageFormat.unescape`
  - builder: per-package code config, `c_stdout`/`c_stage`/`shell` checks, C harness, `hands_on` items, opt-in backslash escaping
- Final: `CFNX-v0 / D-114`
- Not run: T6 and on-device ingestion. SQLite publication ran on the JVM only.

## User decisions
- The Linux terminal Skill is published in 15C, because the first C Skill depends on it.
- The environment is WSL Ubuntu + gcc, installed by the user.
- `standard_io_basic` is added, so programs read input with scanf.

## Found
- The first C Skill waits on a Linux Skill (15E), and that Skill has no prerequisite of its own.
- C programs need input before they can be tried on more than one case.
- The authoring machine had no C compiler, and installing the `gcc` package alone did not bring the C headers.
- The package format could not show a backslash-n, so C code could not be written.
- A function item passed even when the function was missing.
- The terminal Skill becomes a third entry point.

## Independent review
- **First pass:** 46/51 items and 52/56 explanations passed. Every key was correct and every wrong solution failed. The failures were:
  - 3 items with lesson leakage
  - `%%` was never taught
  - a suite that missed boundaries
  - 4 technically wrong sentences: a header gives the declaration, not the definition; a misspelled call is a compile error in gcc 15; a do-while input loop runs forever on EOF; signed overflow is undefined behaviour
- **Second pass:** 51/51 items, 56/56 explanations.

## Verification
- Kotlin mutation: 8/8, all detected in the first run. The control mutant survived.
- Validator mutation: 34/34.
  - Earlier runs exposed two crashes and one survivor, and the validator was fixed for all three.
  - The final run surfaced a flaky 5 s Linux timeout, now raised to 60 s.
- Validator: 155/155. 15A's (131/131) and 14C's (123/123) validators each had one line-break gate narrowed; 15B's is at 254/254, unchanged.
- Sweep: 58/58.
- JVM tests: 1001.

## Durable decisions
A C program is judged where the learner builds it: on Linux, with gcc, through the learner's own runner. The package shows C exactly as written.

## Next
After POST, 15D — Memory Foundations is active-not-executed. It needs a fresh PRE and explicit user approval.
