# 15C — C Foundations — research and synthesis

**Step:** 15C · **Model:** CFNX-v0 · **Decision:** D-114 · **Date:** 2026-10-02

## 1. What had to be found out

1. **What is 15C's scope?** FBB-v0 §6.3 lists seven C/memory seeds. The four C seeds (compile_link_run_basic, declarations_types_expressions, conditionals_loops_basic, functions_basic) are 15C's; pointers and memory (address_value_distinction, pointer_declaration_dereference_basic, storage_lifetime_intuition) are 15D's. 6C decomposed the four C seeds into seven Skills.
2. **Can those Skills ever become ready?** Not on their own. In 6C the first one, `compile_link_run_basic`, has a hard prerequisite outside 15A/15B and outside C: the Linux Skill `terminal_filesystem_navigation` (FBB-v0 §6.4, a 15E seed), which itself has no prerequisite. The user chose to publish it in 15C (15B's closure rule). A second gap is not in the graph: a C program can only be tried on several inputs if it reads input (`scanf`), which is `standard_io_basic` — not a seed, not a prerequisite of the seeds. The user chose to add it. Nine Skills, ten Objectives, 15 edges (7 from 15A).
3. **Where is C built?** The authoring machine had no C compiler on PATH; Visual Studio held only an old embedded MSVC (19.12, VS2017 era); WSL was installed without a distribution. The user chose WSL Ubuntu + gcc and installed it. `apt install gcc` alone did not bring the C library headers (`stdio.h: No such file or directory`); `build-essential` did (gcc 15.2, Python 3.14 in Ubuntu). Every key, lesson claim and suite of 15C is checked there, through the same runner the learner runs.
4. **Can the package show C at all?** No. `curriculum_package/1` read a backslash-n in a prompt or lesson as a line break and had no way to write a literal backslash, so `printf("%d\n", x)` could not be shown. 15C adds one rule: a doubled backslash is one backslash. Neither earlier package contains a doubled backslash, so both read exactly as before.

## 2. Sources read (2026-10-02)

Read with a plain web fetch; nothing was sent anywhere and no account was used.

| Source | Used for |
|---|---|
| GCC 14 porting notes — https://gcc.gnu.org/gcc-14/porting_to.html | Implicit function declarations are errors (`-Werror=implicit-function-declaration` by default): "It is no longer possible to call a function that has not been declared." Grounds the functions lesson's "a prototype or definition before use" and the link-vs-compile items: a call to an undeclared function fails at compile, a declared but undefined one at link. |
| cppreference — `scanf`/`fscanf` — https://en.cppreference.com/w/c/io/fscanf | scanf returns the number of receiving arguments assigned, or EOF; `%lf` is `double*` (unlike printf, where `%f` serves double); `%d` skips leading whitespace, `%c` does not, and a space in the format consumes whitespace. Grounds the standard_io lesson, its misconceptions and items f02, f03, f05. |
| cppreference — arithmetic operators — https://en.cppreference.com/w/c/language/operator_arithmetic | Integer division truncates toward zero since C99 and `(a/b)*b + a%b == a`; unsigned arithmetic is modulo 2^n; signed overflow is undefined. Grounds the expression lesson (negative division and remainder, the unsigned wrap claim, the UB statements) and its misconception against Python's floor division. |
| Microsoft Learn — WSL file systems — https://learn.microsoft.com/en-us/windows/wsl/filesystems | `C:\` is mounted at `/mnt/c` in WSL, and project files are best kept in the Linux file system. Grounds the terminal lesson's WSL paragraph. |

## 3. What was decided with the user

1. **Linux prerequisite:** add `terminal_filesystem_navigation` to 15C (15E adds the rest of Linux/Git/Shell).
2. **Environment:** WSL Ubuntu + gcc, installed by the user; the learner and the build use the same gcc.
3. **Input:** add `standard_io_basic`, so programs read input and are tried on several inputs.

15A's and 15B's decisions carry over: executed keys plus an independent review; Objectives refined and recorded, identities unchanged; incremental packages with entity version 1.

## 4. How the keys are checked

- `c_stdout`: the program is built with `gcc -std=c11 -Wall -Wextra` in Linux and run (with the item's input); what it prints is the key.
- `c_stage`: the program is compiled (`-c`), linked and run separately; the first stage that fails (compile, link, run) or `ok` must be the only keyed option.
- `shell`: the commands run in bash in an empty directory in Linux; their output is the key.
- Code items: the course's runner, inside Linux, builds `cozum.c` with gcc and runs `./cozum`; the reference passes every test and at least two plausible wrong solutions each fail one. An item that asks for a function is built with the course's own `main` (the learner's `main` renamed with `-Dmain=ogrenci_main`), so a program that computes the answer without the function fails at link.

The build refused several of the author's own first drafts: a "wrong" solution that was in fact correct (it passed every test), a `/` in a shell option read as C division, and hidden prerequisites (a function prototype in a compile-link item, a division in a functions item).

## 5. Findings for later steps

- **A third entry point.** `terminal_filesystem_navigation` has no prerequisite, so a learner with no history may now start the terminal lesson on day one, next to 15A's two entry lessons. This is the graph's own statement (6C), not a 15C choice.
- **Fixed outputs remain** for `compile_link_run_basic` and `declaration_type_model` items (no input before `standard_io_basic`), as in 15B.
- **Item-level requirements the graph does not state**, as in 15B: control-flow and function items declare `standard_io_basic`; a compile-link item declares `functions_basic` for its prototype.
- **The learner's runner needs Linux** for C (gcc, `./cozum`, `bash`); moving `cozum.c` and the report between the phone and the computer stays 16D's.
