---
type: session-log
status: completed
stage_step: 15D
model: MMFX-v0
decision: D-116
date: 2026-10-03
---

# 15D — Memory Foundations — a use of memory is judged by what the sanitizer reports

15C (#54) was merged into main (1c60571). The repository was made public by the user (D-115) because GitHub Actions jobs were blocked by billing. A fresh 15D PRE showed `15C ✅ / 15D active-not-executed` in all five canonical sources. The user approved explicitly ("onay veriyorum"; "Sen merge et, 15D'ye başla"). No new product decision was needed.

## Result
- Canonical: `docs/MEMORY_FOUNDATIONS_CONTENT_SPEC.md`
- Machine-readable: `arch/15d_memory_foundations/memory_foundations.yaml`
- Content verification: `arch/15d_memory_foundations/content_verification.yaml`
- QA: `arch/15d_memory_foundations/qa_report.yaml`
- Stale audit: `arch/15d_memory_foundations/stale_reference_audit.yaml`
- Research/synthesis: `research/15d_memory_foundations_research.md`
- Content pipeline: `curriculum/content/15d_memory_foundations/` → `tools/build_curriculum_package.py` (Linux through WSL, with AddressSanitizer + UBSan) → `android/app-wiring/src/main/assets/curriculum_package_v4.txt` and `suites/`
- Code: builder only — `c_sanitized`, the `c_sanitize` mode, lesson checks with a `finding:`, `harness_cflags`, `expect_clean_exit`. Kotlin main code, schema, ports and store are unchanged.
- Final: `MMFX-v0 / D-116`
- Not run: T6 and on-device ingestion. SQLite publication ran on the JVM only.

## Scope
- FBB-v0 §6.3's three memory seeds, as 6C decomposed them: `address_value_distinction`, `pointer_formation`, `pointer_dereference` and `storage_lifetime_intuition`.
- 4 Skills, 6 Objectives and 5 edges, 3 of the edges from 15C. Nothing was added and there is no entry point.
- 34 items (15 code, 12 reading, 4 sanitizer, 3 rubric), 33 explanations, 13 misconceptions and 20 tasks.

## Found
- The output of a lifetime error proves nothing, so lifetime keys are checked against the sanitizer's finding.
- gcc 15.2's libasan detects stack-use-after-return by default, unlike what Google's wiki says. The option is still set explicitly.
- A sanitizer report aborts the program before stdout is flushed. The clean-exit rule is therefore defensive and is checked statically.
- The lifetime Skill reads through pointers, but the graph does not say so. Its work declares `pointer_dereference`; the graph question goes to 15H.
- `NULL` needs a header in a file that contains only a function.
- The sanitizer does not catch every invalid access: an uninitialised pointer ran clean.

## Independent review
- **First pass:** 29/34 items and 30/33 explanations passed. Every key and lesson check was correct, and all 30 wrong solutions failed. The failures were:
  - 2 suites a wrong solution passed (one fixed-output item showed its own output in the prompt)
  - 3 items with lesson leakage
  - 3 wrong sentences: `sizeof(char)`, the header for `NULL`, and the claim that the sanitizer catches every invalid access
- **Second pass:** 34/34 items, 33/33 explanations. A one-sentence prompt clarification the reviewer suggested was confirmed afterwards.

## Verification
- Builder mutation: 9/9, against the real build and four planted-error fixtures. The control survived.
  - Two earlier runs were discarded: the fixture copy lay outside the repo, and then a one-file fixture always failed.
  - A test was strengthened: read f01 got a wrong solution that only the sanitizer catches.
- Validator: 132/132. Its own mutation test: 36/36. Sweep: 59/59.
- JVM tests: 1009. Both APK builds passed, and all four packages in the APK match their source by SHA-256.

## Durable decisions
A use of memory that no longer exists is judged by what AddressSanitizer reports, never by what an undefined program printed. The learner's build is the checked build.

## Durable decisions recorded in this session
- **D-117 — standing approval.** The user said to merge each finished step and go straight on to the next until they say stop. Product decisions that would have gone to the user are recorded as "asistanın varsayılan seçimi (D-117)".
- **Open concern from the user:** question pools are small (~5 items per Objective). A struggling learner can run out of fresh items, and the planner then stalls on `no_valid_candidate`. A pool-expansion step is recommended; the user has not decided yet (OPEN_LOOPS).

## Next
After POST, 15E — Linux / Git / Shell Foundations is active-not-executed. It needs a fresh PRE; the user's approval is standing (D-117).
