# Memory Foundations Content Specification — MMFX-v0

**Stage step:** 15D — Memory Foundations  
**Status:** ACCEPTED — independent 15D QA PASS  
**Decision:** `D-116`  
**Model:** `MMFX-v0 — Memory Foundations content`  
**Behaviour it implements:** `FBB-v0` §6.3 (the memory seeds) and §16; `KGC-v0` §8, §25, §27–§28; `LFPS-v0` and `D-113` (incremental packages; a published version is never overwritten); `D-114` (C is judged on Linux; a doubled backslash is one backslash); `QAB-v0` §21–§22; `AIV-v0` §2–§3, §23–§25; `CDEX-v0` (code is judged by the course's own tests, run on the learner's computer); `GRE-v0`; `TASK_TAXONOMY_SPEC` (3B) §3, §10, §15, §19  
**Content:** `curriculum/content/15d_memory_foundations/` → `android/app-wiring/src/main/assets/curriculum_package_v4.txt` and `curriculum/content/15d_memory_foundations/suites/` (both generated; never edit them by hand)  
**Persistence / ports / schema / Kotlin main code:** unchanged

## 1. Purpose

15C judged C where the learner builds it. 15D is about memory, and that adds a new difficulty:

- Reading a local variable after its function has returned is undefined behaviour.
- So is reading heap memory after `free`.
- The program's output proves nothing: it often prints the "right" number.

15D answers:

> **Bir erişimin geçerli olup olmadığı nasıl doğrulanır — tanımsız bir programın çıktısı hiçbir şey kanıtlamazken?**

Primary invariant:

> **A key about memory is what the program really does. A use of memory that no longer exists is judged by what AddressSanitizer reports, never by what an undefined program happens to print.** The learner's build is the checked build: the same sanitizers, the same run options, and a clean exit required. So a solution that reads dead memory fails even when its output was right. Every suite tells a right solution from a plausible wrong one, and no earlier package reads differently.

---

# 2. What was found before and during writing content

- **The output of a lifetime error proves nothing.** A program that returns the address of a local and reads it prints the expected value without a sanitizer. Lifetime keys are therefore checked against the sanitizer's finding.
- **Stack-use-after-return is on by default in gcc 15.2's libasan.** Google's wiki says the opposite. The course sets the option explicitly, so the result does not depend on the runtime's default.
- **A sanitizer report aborts before stdout is flushed.** A solution that reads dead memory therefore already fails on its output. The clean-exit rule is defensive, and the validator checks it statically.
- **The lifetime Skill reads through pointers, but the graph does not say so.** In 6C, `storage_lifetime_intuition` depends only on `functions_basic`. Its work declares `pointer_dereference` instead (3B §10), and the graph question goes to 15H.
- **`NULL` needs a header in a function-only file.** The runner shows only "build: failed", so the pointer lesson now names `<stddef.h>`.
- **The sanitizer does not catch every invalid access.** Reading through an uninitialised pointer ran clean. The lessons say "many, not all".

---

# 3. User decisions

The user approved starting 15D on 2026-10-03, and no new decision was needed:

- The scope is FBB-v0 §6.3's three remaining seeds, as 6C decomposed them.
- Their hard closure is already published by 15C.
- 15C's WSL Ubuntu + gcc already ships the sanitizers.

Earlier decisions carry over:
- keys are executed and reviewed independently;
- Objectives are refined and recorded, with identities unchanged;
- packages are incremental, and every entity is version 1;
- C is checked on Linux with gcc, through the learner's own runner.

---

# 4. Scope

FBB-v0 §6.3's memory seeds as 6C decomposed them: `address_value_distinction`, `pointer_formation`, `pointer_dereference` and `storage_lifetime_intuition`. 6C split the pointer seed in two. That gives **4 Skills, 6 Objectives and 5 edges**, 3 of them from 15C's `declaration_type_model` and `functions_basic`.

Nothing is added and there is no entry point: every memory Skill waits on 15C. Arrays, structs, the string library, the math library and `goto` are still forbidden in every item.

| Skill | Objective | Direct type | Items |
|---|---|---|---|
| address_value_distinction | explain_address_vs_value | explanation | 5 reading + 1 rubric |
| address_value_distinction | trace_simple_memory_example | explanation | 5 reading + 1 rubric |
| pointer_formation | demonstrate_capability | authored_code | 5 code (harnessed) |
| pointer_dereference | read_pointed_value | authored_code | 5 code (harnessed) |
| pointer_dereference | modify_value_via_pointer | authored_code | 5 code (harnessed) |
| storage_lifetime_intuition | identify_simple_lifetime_boundary | explanation | 2 reading + 4 sanitizer + 1 rubric |

---

# 5. How a key is checked (on Linux, with sanitizers)

| Kind | What is run and required |
|---|---|
| `c_stdout` | Built with `gcc -std=c11 -Wall -Wextra` in Linux and run. The printed output is the key. Only programs with defined behaviour are used, and an address is compared, never printed as a key. |
| `c_sanitize` (new) | Built with `-g -fsanitize=address,undefined -fno-omit-frame-pointer -fno-sanitize-recover=all` and run with `ASAN_OPTIONS=detect_stack_use_after_return=1:detect_leaks=0`. The finding — `stack-use-after-return`, `heap-use-after-free`, `undefined-behavior`, `compile` or `ok` — must make the keyed option the only true one. A valid program must also print what the item says. |
| `suite` | The course's runner, inside Linux, builds `cozum.c` with the same sanitizers and runs it with the same options. Every test expects exit code 0. The reference passes every test, and every wrong solution fails at least one. |
| `rubric` | An open response judged by its rubric (OREX-v0). Provisional at most. |

All 15 code items ask for a function. They are built with the course's own `main` (15C's harness), and the sanitizer flags reach both compiles and the link.

Lesson checks:
- A check that names a `finding:` is run through the sanitizer.
- Every other check is built and run as in 15C.

Validation records name `15d/executed_key`, `15d/executed_suite` or `15d/rubric_reviewed`, each `+independent_review`. The validator rebuilds the package and every suite on Linux and compares the bytes.

Result:
- 34 items: 15 code, 12 reading, 4 sanitizer and 3 rubric.
- 33 explanations, 13 misconceptions and 20 tasks.
- 15 lesson claims were executed.
- Every item and explanation was independently reviewed and passed.

---

# 6. Changes to accepted code

- `tools/build_curriculum_package.py`:
  - `c_sanitized` and the `c_sanitize` mode;
  - lesson checks with a `finding:`;
  - `harness_cflags` reaching the harness compiles and the link;
  - `expect_clean_exit`.

  15A, 15B and 15C rebuild byte-identically.
- `app-wiring`: the generated fourth asset, read by the existing `curriculum_package_v<N>.txt` loader.
- Kotlin main code, the runner, the schema, the ports and the store are unchanged. No living gate was narrowed.

---

# 7. Verification

- **Suites:**
  - `ShippedMemoryPackageTest` reads the real fourth asset with the first three and checks that:
    - it is published, closed, and built on 15C;
    - C code is shown with its backslash-n;
    - every Objective is measurable;
    - every item is judged by exactly one thing;
    - the terminal is allowed only at the computer;
    - every Skill is served, and the lifetime work declares `pointer_dereference`;
    - everything is validated.
  - `ShippedCourseTest` publishes all four assets into the real SQLite schema on the JVM. A learner with no history still starts only the three entry Skills, and every memory Skill waits.
- **Mutation:** 9/9 detected. It targets the builder's new checking, using the real build plus four planted-error fixtures. Kotlin main code is unchanged.
- **Validator:** `tools/validate_memory_foundations.py` rebuilds all four packages and every suite on Linux: 132/132. Its own mutation test detected 36/36, and the sweep passed 59/59. 1009 JVM tests passed.
- **Not run: T6.** Nothing ran on the device. SQLite publication ran on the JVM only.

---

# 8. Open loops

| Loop | Owner |
|---|---|
| requirements declared per item (`pointer_dereference` on lifetime work; `expression_evaluation`/`functions_basic` on address and pointer items) | 15H |
| moderate transfer labels (address f05, trace f05, storage f05) | 15H |
| notation coverage; lesson checks not scanned; `requires_transfer` | 15H |
| the runner's pool; moving `cozum.c` and the report between phone and computer | 16D |
| minute calibration | 18B |
| T6 and on-device ingestion | 19 |

---

# 9. Anti-patterns explicitly rejected

- A lifetime key checked by what an undefined program printed.
- A learner build without the sanitizers the check used.
- A suite that a solution reading dead memory or a null pointer passes.
- A lesson that promises the sanitizer catches every invalid access.
- An array, a string function or a struct in a 15D item.
- An item validated without an independent verdict.

---

**Next step:** **15E — Linux / Git / Shell Foundations** (fresh PRE + explicit user approval).
