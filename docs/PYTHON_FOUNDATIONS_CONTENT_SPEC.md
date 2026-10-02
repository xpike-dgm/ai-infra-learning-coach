# Python Foundations Content Specification — PYFX-v0

**Stage step:** 15B — Python Foundations  
**Status:** ACCEPTED — independent 15B QA PASS  
**Decision:** `D-113`  
**Model:** `PYFX-v0 — Python Foundations content`  
**Behaviour it implements:** `FBB-v0` §6.2 and §16 (the Python seeds; every published required Objective has teach, guided and independent practice, an H0 evidence path, remediation and retention); `KGC-v0` §8, §25, §27–§28 (Objective contract, an entity's version is its semantic revision, lifecycle, graph invariants); `LFPS-v0` (a published version is never overwritten); `QAB-v0` §7, §15, §19, §21, §22; `AIV-v0` §2–§3, §23–§25; `CDEX-v0` (code is judged by the course's own tests, run on the learner's computer); `GRE-v0` (two variant families); `TASK_TAXONOMY_SPEC` (3B) §3, §10, §15, §19  
**Content:** `curriculum/content/15b_python_foundations/` → `android/app-wiring/src/main/assets/curriculum_package_v2.txt` and `curriculum/content/15b_python_foundations/suites/` (generated, never hand-edited)  
**Persistence / ports / schema:** schema unchanged; one store rule narrowed (user decision); one port refinement (`ContentPort.curriculumPackages`, with a default)

## 1. Purpose

15A taught a learner to read programs. 15B teaches them to write Python — and is the first step whose evidence is the learner's own code, judged on their own computer. It answers one question:

> **Bir öğrencinin yazdığı kod neye dayanarak doğru sayılır — ve ikinci bir içerik paketi, birincinin yayımladığı hiçbir şeye dokunmadan nasıl gelir?**

Primary invariant:

> **A program is judged by what it does, on the learner's own computer, against tests that can tell a right solution from a plausible wrong one.** Every code item's reference solution passes the course's own tests through the runner the learner uses, and at least two plausible wrong solutions each fail one; a test that a correct solution could fail for reasons the prompt does not state — the operating system's line endings or path separator, the console's code page — is a defect of the test. A later package only adds: what is published is never carried again or overwritten, and a Skill is published only with everything it needs to become ready.

---

# 2. What was found before writing content

- **A second package could not ship.** The app read one asset, and the store refused any entity whose version was not the package's own (11D) — so a Skill first published in curriculum version 2 would have had to be "version 2" of something that never had a version 1, and every 15A entity would have had to be re-carried.
- **The runner failed correct Turkish programs on Windows.** 14D's runner started the learner's program with the platform's console encoding (cp1254), so a program printing `ç`, `ş`, `ı` produced bytes that did not match the expected UTF-8 output.
- **Three seed Skills could never become ready.** In 6C, `modules_imports_basic` needs `scope_name_resolution`, `path_handling` needs `string_text_operations`, and `files_paths_basic` needs `exception_handling` — and none of those three is an FBB-v0 §6.2 seed. Publishing only the seventeen decomposed seeds would publish Skills whose hard prerequisites are not on the route.
- **Writing a function is needed but not a prerequisite.** Most production Skills are naturally tested as functions, but 6C does not make them depend on `functions_parameters_return` or `list_operations`.
- **Text-mode files differ by platform.** Writing `'\n'` in text mode produces `os.linesep` (CRLF on Windows), so a test that reads the written bytes fails every correct solution on Windows.
- **Tracebacks differ by Python version.** Python 3.11+ draws `^`/`~` caret lines under the failing expression; a traceback copied into an item must not depend on them.
- **6C naming.** `objective.python.path_handling.read_write_small_text_file` states path operations; file reading is `files_paths_basic`'s.

---

# 3. User decisions (2026-10-02)

1. **Scope: the seventeen decomposed seeds plus the three hard prerequisites outside them — twenty Skills, twenty-one Objectives.** Added: `scope_name_resolution`, `exception_handling`, `string_text_operations`.
2. **Shipping: incremental packages; an entity's version is its semantic revision.** Each step of Stage 15 ships its own package (`curriculum_package_v<N>.txt` after the first); new entities are version 1; references to what earlier packages published are pinned to those versions. 11D's "every entity carries the package's version" is narrowed to "**a published version is never overwritten**": the store refuses a package that carries any already-published domain, module, topic, Skill, Objective, misconception or resource.

15A's decisions carry over unchanged: executed keys plus an independent review; Objectives refined and recorded, identity unchanged.

---

# 4. Scope boundary

## 4.1 15B decides

- which Python Skills and Objectives the second package publishes, and their final metadata,
- the Python notation each lesson introduces for writing,
- lessons, written alternatives, misconception catalog, items, test suites, keys and tasks of those Skills,
- how a second package ships, is read and is published,
- how the learner's code is run fairly on their computer.

## 4.2 15B does not decide

- C (15C), memory (15D), Linux/Git/Shell (15E), English (15F), cross-skill assessment content (15G), content QA across stage 15 (15H), later Python (comprehensions, lambdas, classes, generators; 20A),
- how the runner presents a task's pool or moves files between phone and computer (16D),
- calibration of minutes, thresholds or evaluators (18),
- any engine or schema rule.

No score, weight, threshold or similarity measure is introduced.

---

# 5. The subgraph

FBB-v0 §6.2's twelve seeds as 6C decomposed them (seventeen Skills: `values_variables_expressions` → value types, assignment, expressions; `sequence_collections_basic` → list, tuple, set; `files_paths_basic` → path handling, files) plus the three added — **20 Skills, 21 Objectives, 31 edges**, of which 7 come from 15A Skills. Every hard prerequisite of every published Skill is published (here or by 15A). Identities are 6C's; lifecycle `draft` → `published`.

| Skill | Objective | Direct type | Items |
|---|---|---|---|
| run_repl_script | execute_and_classify_result | authored_code | 5 code |
| value_type_behavior | demonstrate_capability | code_reading | 6 reading |
| assignment_binding | write_state_change | authored_code | 5 code |
| expression_evaluation | demonstrate_capability | code_reading | 6 reading |
| input_output_basic | build_small_io_flow | authored_code | 5 code |
| conditionals | write_branch_logic | authored_code | 5 code |
| for_iteration | iterate_sequence | authored_code | 5 code |
| while_termination | debug_infinite_loop, write_terminating_loop | authored_code | 5 + 5 code |
| functions_parameters_return | write_small_function | authored_code | 5 code |
| list_operations | select_and_use_sequence | authored_code | 5 code |
| tuple_immutable_sequence | demonstrate_capability | authored_code | 5 code |
| set_operations | demonstrate_capability | authored_code | 5 code |
| mapping_collections_basic | select_and_use_mapping | authored_code | 5 code |
| exceptions_read_basic | extract_error_type_location | code_reading | 6 reading |
| scope_name_resolution | demonstrate_capability | code_reading | 6 reading |
| modules_imports_basic | import_and_use_module_member | authored_code | 5 code |
| exception_handling | demonstrate_capability | authored_code | 5 code |
| string_text_operations | demonstrate_capability | authored_code | 5 code |
| path_handling | read_write_small_text_file | authored_code | 5 code |
| files_paths_basic | demonstrate_capability | authored_code | 5 code |

Direct types and acceptable types are 6C's, unchanged; criticality is unchanged. Every Objective's statement and observable behaviour is made specific and each change is recorded in its Skill file's `reconciliation`. For a learner with no history nothing in this package is startable: every Python Skill waits, through the gate, on 15A Skills.

---

# 6. The writing notation

`notation.yaml` extends 15A's reading constructs with those 15B introduces for writing, each owned by the lesson that teaches it: true division and `**` (expressions), conversions and `None` (value types), `input()` (I/O), `len`, aggregate built-ins, brackets, braces and method calls (the collection and text Skills), f-strings (text), `import` and `__name__` (modules), `try`/`except`/`finally`/`raise` (exception handling), `open`/`with` (files), `global` (scope). Comprehensions, lambdas, classes and generators are never introduced and are forbidden in every 15B item. An item may use a construct — in the code it shows, its options and **its reference solution** — only if its own Skill, the closure of its hard prerequisites (15A's included) or a Skill it declares in `required_skills` introduces it. The course's test harness is not the learner's code and is not scanned.

---

# 7. Content per Objective

Every Objective has the course's explanation, a prerequisite refresher (H1), a worked example (H3), a state trace (H2) where tracing is the behaviour, two or three catalog misconceptions with neutral open questions and written contrasts, and executed lesson checks. A **code Objective** has five items in five variant families (two basic, two authentic, one transfer); a **reading Objective** has six (two of each). The first basic item is the in-lesson one, presented only in the teach task.

A code item asks for `cozum.py`. Its suite (`code_test_suite/1`) compiles it (`python -m py_compile`), then runs it — as a program (stdin/arguments → stdout) or, for a function, through the course's harness that imports it — with a 5-second limit and trailing whitespace trimmed. Items prohibit only `external_ai`: the terminal and documentation are part of writing code (QAB-v0 §21). Reading items prohibit terminal, compiler, debugger and AI, as in 15A.

---

# 8. How an AI-written item becomes validated

`tools/build_curriculum_package.py` builds the package and every suite and checks every item:

| Kind | What is run and required |
|---|---|
| `stdout` | the printed output is the key; for a choice, exactly one option |
| `traceback` (new) | the program, run as `program.py`, fails; the traceback shown in the prompt is the real one without its version-dependent caret lines; the key is the part asked for (type, line, function and line, module line, message) |
| `suite` (new) | the course's runner (14D) — the learner's own — runs the reference solution, which must pass every test, and at least two plausible wrong solutions, each of which must fail at least one |

An item is `validated` only if its check passes, it uses no hidden construct **and** the independent review passed it; validation records name `15b/executed_key+independent_review` or `15b/executed_suite+independent_review`, origin `ai_generated`, instant 2026-10-02T12:00Z. The build is deterministic; the validator rebuilds the package and every suite byte-for-byte, and rebuilds 15A's package to show it is untouched.

The suite check paid for itself while writing: it refused a median item whose inputs a mean also passed, a same-folder item a parent-name comparison also passed, and — before any learner saw them — two file items whose tests failed every correct solution on Windows.

Result: 109 items — 85 code (`suite`), 24 reading (17 `stdout`, 7 `traceback`); 116 explanations; 44 misconceptions; 100 tasks; every item and explanation independently reviewed and passed.

---

# 9. Shipping a later package (`D-113`)

- **Assets:** the first package stays `curriculum_package.txt`; later ones are `curriculum_package_v<N>.txt`, read in the order of N.
- **Reading (`FileContentSource`):** all packages are parsed together; versions must rise strictly; no item, explanation, task, test suite, comprehension check, answer key or rubric may be authored in two packages. If any package fails, **none** is served — a later package builds on the earlier ones, and half a course is not a course. Lookups serve the merged content; `curriculumPackage()` stays the first package (11D) and `curriculumPackages()` is all of them.
- **Port refinement:** `ContentPort.curriculumPackages()`, defaulting to the single package, so every existing adapter is unchanged.
- **Publishing (`IngestCurriculum.ingestAll`):** oldest first; an already-published version answers `AlreadyPublished` and writes nothing; the first refusal stops the run.
- **Store (`CurriculumStore`):** a package's own new entities are written at their own versions; carrying an already-published entity refuses the whole package before anything is written; references resolve against the package and the store, as before.

---

# 10. The learner's own computer

- **UTF-8 mode:** the runner starts `{python}` as `python -X utf8`, so standard streams and `open()` default to UTF-8 on every platform (PEP 686 makes this the default from Python 3.15). 14D's suites still pass (122/122).
- **Files:** every file test creates and removes its own temporary directory and reads what the learner wrote in text mode (universal newlines).
- **Paths:** a path result is compared as a name, a suffix, a boolean or `as_posix()`, never with the platform's separator.
- **Limit recorded:** because of UTF-8 mode, a test cannot detect a missing `encoding="utf-8"`; the lessons teach it and the review checks it.

---

# 11. Changes to accepted code

- `core-ports`: `ContentPort.curriculumPackages()` (default).
- `core-application`: `IngestCurriculum.ingestAll()`.
- `data-persistence`: `CurriculumStore` — `republishedEntities` replaces the version-equality rule (user decision).
- `data-curriculum`: `FileContentSource` — several packages, sequence check, merge.
- `app-wiring`: later assets read and ingested; the generated second asset; unit tests run the JVM flavour of the bundled SQLite driver (test runtime only).
- `tools`: `code_test_runner.py` (UTF-8 mode); `build_curriculum_package.py` (`builds_on`, `traceback` and `suite` checks, suites written beside the content).
- Narrowed gates (declared in the contract): 11D `E11D-07_version_mismatch_refused` and `E11D-09_failed_parse_serves_nothing`, 11A `E11A-13_content_port_returns_null`, 14B `E14B-03_version_refused`.

---

# 12. Verification

- Suites: `CurriculumPublishingTest`, `MisconceptionStorageTest` (store), `PackageFormatTest` (several packages read together or not at all), `DailyMicroAssessmentTest` (`ingestAll`), `ShippedPythonPackageTest` (the real second asset with the first: published, closed against 15A, every Objective measurable by two families of its direct type, every item judged by exactly one key/rubric/suite, every Skill served for each need, everything validated), `ShippedCourseTest` (both real assets **published into the real SQLite schema** by the real ingestion, a second start publishing nothing, and the real gate and planner offering a learner with no history only the two 15A entry lessons while all thirty other Skills wait).
- Validator `tools/validate_python_foundations.py`, reading FBB-v0 §6.2, 6C (seed mappings, Objectives, edges) and the Kotlin, rebuilding both packages and every suite byte-for-byte, with its own mutation test.
- **Mutation:** see the contract's `mutation_results`; a compile failure is not a detection.
- **Not run: T6.** Nothing ran on the device; the first on-device ingestion is the learner's. The SQLite publication of the shipped course ran on the JVM only.

---

# 13. Open loops

| Loop | Owner |
|---|---|
| requirements the graph does not state are declared per item: writing functions, brackets in set/tuple/mapping items, conditions in `for_iteration`, input in `conditionals`, `import` in every `path_handling` item | 15H |
| fixed-output items (`run_repl_script`, most of `assignment_binding`) pass a program that prints the expected text directly; `assignment_binding.f02` shows a harness can read the module's variables instead | 15H |
| the notation does not cover `in`, `is`, `pass`, `break`, `as`, and lesson checks are not scanned | 15H |
| `path_handling`'s Objective action name says file reading; its statement says paths | 15H |
| Unicode normal forms (NFC/NFD) are not taught | 20A |
| `requires_transfer` declared by 6C but not stored | 15H |
| the runner presenting a task's pool, and moving `cozum.py` / the report between phone and computer | 16D |
| calibrating task and item minutes | 18B |
| T6 and on-device ingestion | 19 |

---

# 14. Anti-patterns explicitly rejected

- a code item whose tests a plausible wrong solution passes, or whose reference was never run through the learner's runner,
- a test that fails a correct solution on Windows (or on anything else) for a reason the prompt does not state,
- a traceback in an item that the program does not really print,
- a later package that re-carries, re-versions or overwrites what an earlier one published,
- half a course served because one package did not read,
- a Skill published while a hard prerequisite of it is not,
- an item validated because the model that wrote it said so, or a construct used before any lesson taught it,
- a minute estimate presented as calibrated.

---

**Next step:** **15C — C Foundations** (fresh PRE + explicit user approval).
