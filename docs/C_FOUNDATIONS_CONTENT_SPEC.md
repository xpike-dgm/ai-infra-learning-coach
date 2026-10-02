# C Foundations Content Specification — CFNX-v0

**Stage step:** 15C — C Foundations  
**Status:** ACCEPTED — independent 15C QA PASS  
**Decision:** `D-114`  
**Model:** `CFNX-v0 — C Foundations content`  
**Behaviour it implements:** `FBB-v0` §6.3 (the C seeds; pointers and memory are 15D's) and §16; `KGC-v0` §8, §25, §27–§28; `LFPS-v0` and `D-113` (incremental packages; a published version is never overwritten); `QAB-v0` §21–§22; `AIV-v0` §2–§3, §23–§25; `CDEX-v0` (code is judged by the course's own tests, run on the learner's computer); `GRE-v0`; `TASK_TAXONOMY_SPEC` (3B) §3, §10, §15, §19  
**Content:** `curriculum/content/15c_c_foundations/` → `android/app-wiring/src/main/assets/curriculum_package_v3.txt` and `curriculum/content/15c_c_foundations/suites/` (generated, never hand-edited)  
**Persistence / ports / schema:** unchanged; one format refinement (a doubled backslash is one backslash)

## 1. Purpose

15B judged Python on whatever computer the learner had. C is built before it runs, and a C program's behaviour depends on the compiler and the system it is built on. 15C answers:

> **Bir C programı öğrencinin onu derleyip çalıştırdığı yerde nasıl doğrulanır — ve paket C kodunu kaybetmeden nasıl gösterir?**

Primary invariant:

> **A C program is judged where the learner builds and runs it: with gcc on Linux, through the learner's own runner.** Every key is what gcc and the program really do, every suite tells a right solution from a plausible wrong one, an item that asks for a function is passed only by the function, and the package shows C code exactly as written — a backslash-n stays a backslash-n — without changing how any earlier package reads.

---

# 2. What was found before writing content

- **The first C Skill waits on a Linux Skill.** In 6C `compile_link_run_basic` hard-depends on `skill.linux.terminal_filesystem_navigation` (an FBB-v0 §6.4 seed, 15E), which has no prerequisite of its own. Without it no C Skill could ever become ready.
- **C programs need input to be tried.** Without `scanf` every program has one fixed output, and a program that prints the expected text passes; `standard_io_basic` is neither a seed nor a prerequisite of the seeds.
- **No C compiler on the authoring machine.** Only an old MSVC embedded in Visual Studio; WSL without a distribution. After the user installed Ubuntu, `apt install gcc` alone left out the C library headers; `build-essential` completed it.
- **The package could not show C.** A backslash-n in a prompt or lesson was always a line break, and a backslash had no escape, so `printf("%d\n", x)` could not be written.
- **A function item was passed without the function.** A suite that runs the learner's whole program cannot tell a function from code inside `main`.

---

# 3. User decisions (2026-10-02)

1. **Linux prerequisite:** publish `terminal_filesystem_navigation` in 15C (15B's closure rule); 15E adds the rest of Linux/Git/Shell.
2. **Environment:** WSL Ubuntu + gcc, installed by the user; the learner and the build use the same gcc.
3. **Input:** add `standard_io_basic`.

Earlier decisions carry over: executed keys plus an independent review; Objectives refined and recorded, identities unchanged; incremental packages, entity version 1.

---

# 4. Scope

FBB-v0 §6.3's four C seeds as 6C decomposed them — `compile_link_run_basic`, `declaration_type_model`, `expression_evaluation`, `conditionals`, `for_iteration`, `while_iteration`, `functions_basic` — plus `standard_io_basic` and `terminal_filesystem_navigation`: **9 Skills, 10 Objectives, 15 edges** (7 from 15A's reasoning Skills). Every hard prerequisite of every published Skill is published. Pointers, arrays, memory, structs and the string library are forbidden in every item (15D and later).

| Skill | Objective | Direct type | Items |
|---|---|---|---|
| terminal_filesystem_navigation | navigate_relative_absolute | hands_on_system_task | 5 shell |
| compile_link_run_basic | compile_and_run_small_program | hands_on_system_task | 5 code |
| compile_link_run_basic | classify_compile_vs_runtime_failure | hands_on_system_task | 5 stage |
| declaration_type_model | write_basic_declarations | authored_code | 5 code |
| expression_evaluation | demonstrate_capability | code_reading | 6 reading |
| standard_io_basic | demonstrate_capability | authored_code | 5 code |
| conditionals | write_control_flow | authored_code | 5 code |
| for_iteration | demonstrate_capability | authored_code | 5 code |
| while_iteration | demonstrate_capability | authored_code | 5 code |
| functions_basic | write_and_call_function | authored_code | 5 code (harnessed) |

The two `compile_link_run_basic` Objectives carried the same template statement in 6C; each is now specific to its action name. `terminal_filesystem_navigation` has no prerequisite, so it is a **third entry point**: a learner with no history may start it on day one.

---

# 5. How a key is checked (on Linux)

| Kind | What is run and required |
|---|---|
| `c_stdout` | built with `gcc -std=c11 -Wall -Wextra` in Linux and run (with the item's input); the printed output is the key |
| `c_stage` | compiled (`-c`), linked and run separately; the first failing stage (compile, link, run) or `ok` is the only keyed option |
| `shell` | the commands run in bash in an empty directory in Linux; their output is the key |
| `suite` | the course's runner, inside Linux, builds `cozum.c` and runs `./cozum`; the reference passes every test and at least two plausible wrong solutions each fail one |

An item that asks for a function (`c_harness`) is built with the course's own `main` in `ders_test.c`, the learner's `main` renamed (`-Dmain=ogrenci_main`); a program without the function fails at link. Hands-on items allow the terminal and documentation; reading items do not. Lesson checks are C programs (or shell) built and run the same way. Validation records name `15c/executed_key+independent_review` or `15c/executed_suite+independent_review`; the validator rebuilds the package and every suite on Linux and compares bytes.

Result: 51 items — 35 code (5 harnessed), 6 reading, 5 stage, 5 shell; 56 explanations; 21 misconceptions; 45 tasks; 32 lesson claims executed; every item and explanation independently reviewed and passed.

---

# 6. The format refinement (`D-114`)

`PackageFormat.unescape` reads every multi-line value (item prompt, explanation, comprehension prompt and choices): a backslash-n is a line break (15A) and **a doubled backslash is one backslash**; any other backslash is itself. The builder doubles backslashes only for a package that declares `escape_backslash: true` (15C) and refuses a backslash in a single-line value there. Neither earlier package contains a doubled backslash, so both read byte-for-byte as before; their assets are unchanged.

---

# 7. Changes to accepted code

- `data-curriculum`: `PackageFormat.unescape` and its four call sites.
- `tools/build_curriculum_package.py`: per-package code configuration (Python stays the default), Linux execution through WSL, the `c_stdout`/`c_stage`/`shell` checks, C lesson checks, the C harness, `hands_on` items, notation `strip` patterns, opt-in backslash escaping. 15A and 15B rebuild byte-identically.
- `app-wiring`: the generated third asset (read by the existing `curriculum_package_v<N>.txt` loader).
- The runner is unchanged: its commands were already arbitrary.
- Narrowed gates (declared in the contract): 15A `E15A-09_prompt_line_breaks` and 14C `E14C-09_line_breaks` — both read the old `.replace` call; both now require `unescape`.

---

# 8. Verification

- Suites: `PackageFormatTest` (the escape rule), `ShippedCPackageTest` (the real third asset with the first two: published, closed, C code shown with its backslash-n, every Objective measurable, every item judged by exactly one thing, terminal only at the computer, every Skill served, everything validated), `ShippedCourseTest` (all three assets published into the real SQLite schema on the JVM; a learner with no history starts only entry Skills — now including the terminal — and every C Skill waits).
- Validator `tools/validate_c_foundations.py`, rebuilding all three packages and every suite (on Linux), with its own mutation test.
- **Not run: T6.** Nothing ran on the device; SQLite publication ran on the JVM only.

---

# 9. Open loops

| Loop | Owner |
|---|---|
| requirements declared per item (`standard_io_basic` on control flow, `functions_basic` on a prototype) | 15H |
| fixed-output items in `compile_link_run_basic` and `declaration_type_model` | 15H |
| notation coverage; lesson checks not scanned; `requires_transfer` | 15H |
| the runner's pool; moving `cozum.c` and the report between phone and computer (C needs Linux) | 16D |
| minute calibration | 18B |
| T6 and on-device ingestion | 19 |

---

# 10. Anti-patterns explicitly rejected

- a C key checked with a different compiler or system than the learner's,
- a function item that a program without the function passes,
- C code shown without its backslash-n, or a format change that reads an earlier package differently,
- a C Skill published while its Linux prerequisite is not,
- a pointer or an array in a 15C item,
- an item validated without an independent verdict.

---

**Next step:** **15D — Memory Foundations** (fresh PRE + explicit user approval).
