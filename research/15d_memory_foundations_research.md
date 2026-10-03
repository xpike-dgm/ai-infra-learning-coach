# 15D — Memory Foundations — research and synthesis

**Step:** 15D · **Model:** MMFX-v0 · **Decision:** D-116 · **Date:** 2026-10-03

## 1. What had to be found out

1. **What is 15D's scope?** FBB-v0 §6.3 lists seven C/memory seeds. 15C published the four C seeds. The other three are 15D's: `skill.memory.address_value_distinction`, `skill.c.pointer_declaration_dereference_basic` and `skill.memory.storage_lifetime_intuition`. 6C decomposed them into four Skills, because the pointer seed was split into `pointer_formation` and `pointer_dereference`. The result is 6 Objectives and 5 hard edges.
2. **Can those Skills ever become ready?** Yes, with nothing added. Every hard prerequisite is either one of the four Skills or already published by 15C:
   - `declaration_type_model` is a prerequisite of both the address Skill and pointer formation.
   - `functions_basic` is a prerequisite of the lifetime Skill.

   So no closure question arose for the user, unlike 15B and 15C. 15D therefore adds no entry point. Every memory Skill waits on 15C.
3. **How is a key about lifetime checked?** A key that says "this read is invalid" cannot be checked by running the program and looking at its output:
   - Reading a local variable after its function has returned is undefined behaviour.
   - So is reading heap memory after `free`.
   - The output of such a program proves nothing: it often prints the "right" number.

   AddressSanitizer reports these errors deterministically, so a lifetime key is checked against the **kind of report** (`stack-use-after-return`, `heap-use-after-free`) or against `ok`. The same build is used for every suite. A solution that returns the address of a local, or writes through a null pointer, therefore fails even when everything it printed was right.
4. **Is stack-use-after-return on by default?** The sources and the toolchain disagree:
   - Google's sanitizer documentation says detection needs the run-time option `ASAN_OPTIONS=detect_stack_use_after_return=1`.
   - The libasan that ships with gcc 15.2 in Ubuntu reports `detect_stack_use_after_return` as `true` by default (`ASAN_OPTIONS=help=1`), and detects the error without the option. With the option set to 0, the same program prints the "right" value and exits 0.

   The course's `run` command sets the option explicitly, so the result does not depend on the runtime's default. Leak detection is switched off (`detect_leaks=0`) for two reasons:
   - Leaks are not part of 15D's Objectives.
   - A leak report would fail a basic program that is correct but does not call `free`.

## 2. Sources read (2026-10-03)

The sources were read with a plain web fetch. Nothing was sent anywhere and no account was used.

| Source | Used for |
|---|---|
| GCC — Program Instrumentation Options — https://gcc.gnu.org/onlinedocs/gcc/Instrumentation-Options.html | `-fsanitize=address` instruments memory accesses "to detect out-of-bounds and use-after-free bugs". `-fsanitize=undefined` detects undefined behaviour at run time. With `-fno-sanitize-recover`, only the first error is reported and the program exits with a non-zero code. Grounds the build flags and the suites' `expected_exit_code: 0`. |
| google/sanitizers — AddressSanitizerUseAfterReturn — https://github.com/google/sanitizers/wiki/AddressSanitizerUseAfterReturn | Stack-use-after-return is enabled with `ASAN_OPTIONS=detect_stack_use_after_return=1`, and the report names the error `stack-use-after-return`. The page says it is off by default; gcc 15.2's libasan has it on (see §1.4). Grounds the explicit option in the run command and the lifetime keys. |
| cppreference — storage-class specifiers — https://en.cppreference.com/w/c/language/storage_class_specifiers | Three storage durations: automatic (ends when the block is exited), static (the whole program, initialised once before `main`) and allocated (`malloc` to `free`). Grounds the lifetime lesson and its three cases. |
| cppreference — pointer declaration — https://en.cppreference.com/w/c/language/pointer | `&` forms a pointer and `*` gives the pointed-to object as an lvalue (`*p = 7` stores in `n`). A null pointer points to no object, and dereferencing it is undefined behaviour. Grounds the formation and dereference lessons and the NULL-check items. |
| cppreference — `printf` — https://en.cppreference.com/w/c/io/fprintf | `%p` takes a `void*`, which is why the lesson casts with `(void *)`. `%zu` is for `size_t`, which is why `sizeof` is printed with `%zu`. |

## 3. What was decided with the user

No new decision was needed:

- The scope is FBB-v0 §6.3's three remaining seeds, as 6C decomposed them.
- The closure is already published.
- The environment is 15C's WSL Ubuntu with gcc 15.2. The sanitizers ship with gcc, so nothing more had to be installed.

The user approved starting 15D on 2026-10-03. The decisions of 15A–15C carry over:
- keys are executed and reviewed independently;
- Objectives are refined and recorded, with identities unchanged;
- packages are incremental and every entity is version 1;
- C is checked on Linux with gcc, through the learner's own runner.

## 4. How the keys are checked

- **`c_stdout`:** as in 15C, the program is built with `gcc -std=c11 -Wall -Wextra` in Linux and run. What it prints is the key. Only programs with defined behaviour are used: an address is compared, never printed as a key.
- **`c_sanitize` (new):** the program is built with `-g -fsanitize=address,undefined -fno-omit-frame-pointer -fno-sanitize-recover=all`. It is run with `ASAN_OPTIONS=detect_stack_use_after_return=1:detect_leaks=0`.
  - The finding is the sanitizer's error kind, or `ok`.
  - Each option names an outcome: a valid read is `ok`, and a valid read that prints something else is `ok-different`.
  - The keyed option must be the only true one.
  - A valid program must also print what the item says.
- **Code items:** the course's runner builds `cozum.c` inside Linux with the same sanitizer flags and runs it with the same options. Every test expects exit code 0.
  - The reference passes every test, and at least two plausible wrong solutions each fail one.
  - An item that asks for a function is built with the course's own `main`, as in 15C. The sanitizer flags reach both compiles and the link.
- **Lesson checks:** a check that names a `finding:` is run through the sanitizer; every other check is built and run as in 15C.

## 5. Findings for later steps

- **Reading through a pointer is not a graph prerequisite of lifetime.** In 6C, `storage_lifetime_intuition` depends only on `functions_basic`. Yet a lifetime boundary can only be shown by reading through a pointer that outlives its object. Its items and tasks declare `pointer_dereference` (3B §10), as 15C's items declared `standard_io_basic`. Whether this belongs in the graph is 15H's to decide.
- **Arrays, strings and structs are still forbidden.** Those Skills are later steps. Every 15D item works with single objects.
- **The sanitizer is part of the learner's environment.** The runner's build command carries the flags, so the learner needs nothing beyond `build-essential`. Moving `cozum.c` and the report between the phone and the computer is still 16D's.
