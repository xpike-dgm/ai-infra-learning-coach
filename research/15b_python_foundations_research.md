# 15B — Python Foundations — research and synthesis

**Step:** 15B · **Model:** PYFX-v0 · **Decision:** D-113 · **Date:** 2026-10-02

## 1. What had to be found out

15B is the second content step and the first that asks the learner to **write** code. Before writing a lesson it had to answer three questions:

1. **What exactly is 15B's scope?** `FBB-v0` §6.2 names twelve Python seeds; 6C decomposed them into seventeen Skills with eighteen Objectives. Three of them have a hard prerequisite outside that set — `modules_imports_basic` needs `scope_name_resolution`, `path_handling` needs `string_text_operations`, `files_paths_basic` needs `exception_handling`. Publishing the seventeen alone would leave three Skills that can never become ready. The user chose to add the three (twenty Skills, twenty-one Objectives).
2. **Can a second package ship at all?** No. The app read one asset, and the store refused any entity whose version was not the package's own (11D), so a second package would have had to re-carry every 15A entity at version 2 — which is "a published version is never overwritten" read backwards. The user chose incremental packages with entity versions as semantic revisions: each step ships its own package; a later package only adds; what is published is never carried again.
3. **Can a learner's code be judged on their own computer?** 14D's runner (`tools/code_test_runner.py`) is what the learner runs. Running a correct Turkish-printing program through it on Windows failed: the child process wrote its output in the console code page (cp1254), so `ç`, `ş`, `ı` did not round-trip. A course whose correct answers fail on the learner's computer measures the console, not the learner.

## 2. Sources read (2026-10-02)

Read with a plain web fetch; nothing was sent anywhere and no account was used.

| Source | Used for |
|---|---|
| Python Library Reference — Built-in Types, `str.lower`, `str.split` — https://docs.python.org/3/library/stdtypes.html | `lower()` uses the Unicode Standard's §3.13 default case mapping, which is language-independent: it is why `"I".lower()` is `i` and `"İ".lower()` is `i` + U+0307. Grounds the Turkish casing lesson, its misconception, and `string_text_operations.f05`. `split()` with no separator treats runs of whitespace as one and drops leading/trailing whitespace — grounds the `split()` vs `split(" ")` distinction several items test. |
| Python Library Reference — `open()` — https://docs.python.org/3/library/functions.html | With `newline=None` a written `'\n'` becomes `os.linesep` (CRLF on Windows) and reading translates any line ending to `'\n'`; the default encoding is the locale's. The first file-test harnesses read the written file as bytes and failed every correct solution on Windows; they now read in text mode (universal newlines). The lesson's "always write `encoding="utf-8"`" rests on the default being platform-dependent. |
| Python Library Reference — Python UTF-8 Mode — https://docs.python.org/3/library/os.html | `-X utf8` makes the standard streams and `open()`'s default encoding UTF-8; it becomes the default in Python 3.15 (PEP 686). This is the 14D runner fix, and also why a test cannot catch a missing `encoding=` argument — recorded as a limit in `files_paths_basic`'s reconciliation. |
| Python Library Reference — `pathlib` — https://docs.python.org/3/library/pathlib.html | `suffix` is the last dot-separated portion (`'.gz'` for `library.tar.gz`), `stem` drops only it, `with_suffix` replaces it, `as_posix` gives forward slashes, `/` joins, spurious slashes collapse. Grounds every `path_handling` item and its two misconceptions; `as_posix` is why path results compare the same on every operating system. |
| Python FAQ — *Why am I getting an UnboundLocalError…* — https://docs.python.org/3/faq/programming.html | "when you make an assignment to a variable in a scope, that variable becomes local to that scope". Grounds the scope lesson's rule 3 and `scope_name_resolution.f04`. |
| Python Tutorial — Errors and Exceptions — https://docs.python.org/3/tutorial/errors.html | The last line of the error message names the exception type; `finally` runs whether or not an exception occurred and an unhandled exception is re-raised after it. Grounds the traceback-reading and exception-handling lessons. |

## 3. What was decided with the user

1. **Scope:** the seventeen 6C Python Skills plus the three hard prerequisites outside them — twenty Skills.
2. **Shipping:** incremental packages; every entity keeps its own semantic version (all new ones are version 1); 11D's "an entity's version is the package's" is narrowed to "a published version is never overwritten", declared.

The 15A decisions (executed keys plus an independent review; Objectives refined and recorded, identity unchanged) carry over unchanged.

## 4. How the keys are checked

Reading items are checked as in 15A, plus one new mode: **`traceback`** runs the program as `program.py`, requires it to fail, compares the traceback shown in the prompt with the real one (the `^`/`~` caret lines differ between Python versions and are left out), and checks the key against the part asked for (type, line, function and line, the module-level line, or the message).

A code item is checked by the course's own runner, exactly as the learner will run it: the reference solution must pass every test, and every one of at least two plausible wrong solutions must fail at least one. A test the reference passes but no wrong solution fails tests nothing; a wrong solution that passes everything means the suite cannot tell — the build refused several of the author's first drafts for exactly that (a median item whose inputs a mean also passed; a same-folder item a parent-name comparison also passed).

## 5. Findings for later steps

- **No input-teaching prerequisite.** Writing a function or a list is needed by many production Skills that 6C does not make depend on `functions_parameters_return` or `list_operations`. Items declare these as `required_skills` (3B §10), which the gate applies per task; whether the graph should say so is 15H's.
- **Fixed-output programs.** `run_repl_script` and `assignment_binding` items ask for a program with a fixed output; a program that prints the expected text directly passes. The independent review and the H0 policy carry these items; an input-driven variant needs Skills those two do not depend on.
- **6C naming.** `objective.python.path_handling.read_write_small_text_file` describes path operations, not file reading; the identity is kept and the 6C statement is what is measured.
- **"Normalization-aware".** 6C's `string_text_operations` asks for normalization-aware operations; 15B measures Turkish case mapping (language-independent `lower`/`upper`). Unicode normal forms (NFC/NFD) are not taught.
- **The independent review** checked every key, traceback and lesson claim and ran about forty-five further correct and wrong solutions through the runner. It failed eleven items in the first pass: six for leakage (the problem was already solved in the lesson's own text or checks — the 15A pattern again), four whose tests a plausible wrong solution passed (a swap never made, a composition never used, two catch-alls), and one whose prompt did not say what a test required. It also failed three explanation sentences (what the runner ignores, `bool()` on empty collections, which standard module a local file can shadow on Windows). All were rewritten and passed a second pass.
- **Python 3.15** makes UTF-8 mode the default, so "the default encoding is the platform's" becomes version-dependent; the advice to write `encoding="utf-8"` stays right.
- **First SQLite publication of the shipped course off-device.** The app-wiring unit tests now run the JVM flavour of the bundled SQLite driver, so both shipped packages are published into the real schema by the real ingestion in a JVM test; on-device ingestion is still 19's.
