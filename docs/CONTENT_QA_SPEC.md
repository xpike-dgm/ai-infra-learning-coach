# Content QA Specification — CQAX-v0

**Stage step:** 15H — Content QA  
**Status:** ACCEPTED — independent 15H QA PASS  
**Decision:** `D-121`  
**Model:** `CQAX-v0 — Stage 15 content QA supplement`  
**Behaviour it implements:** `PRG-v0` §§2, 6, 17; `KGC-v0` §12; `GRE-v0` §§7–9; `AIV-v0`; `QAB-v0` (difficulty classes); `CDEX-v0`; `LFPS-v0` (a published version is never overwritten); `D-113` and `D-120` (incremental packages, supplements)  
**Content:** `curriculum/content/15h_content_qa/` → `android/app-wiring/src/main/assets/curriculum_package_v8.txt` (generated; never edit it by hand)  
**Audit:** `tools/content_qa.py` → `arch/15h_content_qa/content_qa_audit.yaml`  
**Persistence / ports / schema:** unchanged; the store refuses one more kind of overwrite

## 1. Purpose

Each step of Stage 15 checked its own content as it was written: every key run, every item reviewed. What no single step could see was left to 15H. 15H answers:

> **Aşama 15'in yayımladığı içerik, her adımın tek başına göremediği yerlerde de söylediğini taşıyor mu — ve taşımıyorsa, yayımlanmış hiçbir şeyin üzerine yazmadan nasıl düzeltilir?**

Primary invariant:

> **Content says only what its graph, its checks and its labels can stand behind.** The graph names what every lesson needs. A code item's suite can tell the work from a program that only prints the answer, unless printing that answer is the work. Every construct an item uses is taught by a lesson it may rely on. A difficulty label is the level its review says. A correction is the next version — the version it replaces is retired by an appended record, never edited — and ships only after an independent reviewer passed it.

---

# 2. What was found

- **The gate was right; the graph was silent.** 15A–15G declared every need at task and item level, so the prerequisite gate already held lessons back. The published graph — what explanations, diagnostics and blockers read — said little of it: 50 lesson needs were not implied by it.
- **Lessons show code their teach task does not reach.** Read line by line, 18 lessons show a construct of a Skill their teach task did not require, most often an `if` in a Python or C lesson, a list in a Python one, or the lifetime lesson's pointers. Four would make two lessons wait for each other and are recorded instead, with Git's "# hazırlık" device.
- **Most lesson-check findings are scaffolds.** A check wraps the lesson's code in a loop over test values or a `try/except` that prints an error's type; the lesson never shows those lines.
- **A fixed output is the construct in some items and a validity hole in others.** For printing a text with escapes, or fixing the print statement itself, printing the text is the work. For a declaration, an assignment, a computation or a fix in logic, it is not: 33 items got a next version whose suite also sees how the output was produced.
- **An Objective-wide exemption hides items.** `compile_and_run_small_program` was first exempted as a whole; two of its items ask for a computation. Exemptions are per item.
- **The reference solution, not the item, used untaught constructs** in three Python items (`in`, `is`, `continue`); an alternative with taught constructs passes the published suite, so nothing shipped changes. Two more items could not be solved without `pass` or `continue`; they were revised so the skip does visible work.
- **One level down is not always the right level.** Labels go to the level the review says the item is; 49 labels were lowered, 5 kept with the reviewer's reason.
- **A harness must run the program as a program.** The first harness imported `cozum.py`, so a file guarded by `if __name__ == "__main__":` — taught in modules_imports_basic — printed nothing. It now runs the file as `__main__` into a namespace it keeps.
- **Text similarity is not a near-variance test.** A scan flagged 128 similar pairs; the ones opened measured different rules.

---

# 3. Decisions

User decisions (2026-10-10, the user's explicit approval of 15H):

- **Graph:** add the edges the content relies on, in a v8 supplement; hard only where the lesson and every item outside it need the prerequisite, soft otherwise.
- **`requires_transfer`:** closed. A standard Objective has no transfer gate (`GRE-v0` §8); the builder refuses a critical Objective until `GRE-v0`'s gate fields are stored. All 65 published Objectives are standard.
- **Flawed published items:** publish the next version and retire the old one by an appended record; tasks grow a version.
- **Pool:** technical Objectives to 15; English keeps 15G's honest small pool.

Assistant defaults, reviewed: the three edge rules below; code-line reading of lessons; per-item exemptions; a label goes to the level its review states.

Declared extensions (`D-121`, not silent edits of accepted specs):

- A later supplement may build on an earlier one (`earlier_supplements`), revise its items or the directories' own (item `version: 2`, same logical id and variant family), and append a `retired` validation record for the version it replaces.
- A supplement may publish `[prerequisite_edge]` sections between published Skills, and a task's next version may carry a lesson need (`teach_requires`).
- Code items may declare `source_checks` (Python: `not_in_source`, `required`, `variables`, `updated`, `bound_from`; C: `required`, `not_in_source`, unused variables); the builder writes the harness from them.
- The store refuses an edge version or a validation record carried a second time, by name, before writing.

---

# 4. Scope

**Supplement, version 8.** 87 items: 81 next versions (81 retired versions) and 6 new; 138 task versions; 49 edges; 12 carried wrong-option keys (on relabelled choice items, options unchanged). No Skill, Objective, Topic, lesson or label is added. Packages 1–7 rebuild byte-identically.

| Source | Revised | New | Task versions |
|---|---|---|---|
| 15A computing | 2 (labels) | — | 9 |
| 15B Python | 23 | 2 (scope_name_resolution) | 49 |
| 15C C | 19 | 2 (declaration_type_model) | 20 |
| 15D memory | 3 (labels) | — | 12 |
| 15E Linux/Git | 8 | 2 (repository_status_diff) | 20 |
| 15F English | 26 | — | 28 |

Revisions by reason: 30 fixed output, 3 fixed output and label, 42 label, 4 label with a review fix (a scaffold that gave the answer away, a cueing prompt, a claim, an accept list), 2 notation.

**Graph.** 49 edges, 12 hard (`lesson_and_every_item`) and 37 soft (33 `lesson`, 4 `reads_glossed`); 18 lessons declare a need their teach task did not. The 50 lesson needs: 12 hard edges, 33 soft edges, 5 implied by 15H's own hard edges. The hard graph, alone and with every lesson's requirement, stays acyclic.

**Pools.** 57 of 65 Objectives have 15 or more items outside the lesson; the three technical Objectives at 14 now have 16. The eight English Objectives at 10–12 are recorded (user decision).

---

# 5. Edge rules

PRG-v0 §6: the graph carries the general learning dependency; a task's required Skills carry what that exact task needs, and are hard even where the graph is silent.

- `lesson_and_every_item` → **hard**: the teach task requires it and every item outside the lesson declares it.
- `lesson` → **soft**: the teach task requires it, some items do not.
- `reads_glossed` → **soft**: the lesson reads structures the prerequisite teaches and glosses them where it shows them.

The builder recomputes every rule from the content, refuses an edge the hard graph after the supplement already implies, refuses a lesson need the graph does not say, and keeps the hard graph acyclic, alone and with the lessons' requirements.

---

# 6. How content is checked

- **Builder (every revised and new item):** the key or suite is run by its content directory's runner; the constant program (prints only the first test's expected output) must fail unless the item is exempted with a reason; untaught constructs are refused with the directory's notation plus `notation_overlay.yaml`; the review verdict is per version (`item…@v2`).
- **Audit (everything published):** `tools/content_qa.py` runs eight audits — fixed output, notation, lesson checks, lesson text, graph, labels, pools, criticality — and every finding must have a resolution: a revision, a per-item exemption, an alternative reference that passes, a declared lesson need or edge, a classified lesson finding (scaffold, notation gap, accepted device, recorded), a label decision, or a recorded small pool. The real run (`--skip-runs` is never recorded) found 52 fixed-output, 5 notation, 24 lesson-check, 6 lesson-text, 50 graph, 54 label, 65 pool and 65 criticality rows, all resolved.
- **Independent review.** Four reviewer agents: Python; C and memory; Linux, Git, English and 15A labels; the graph and the audit's decisions. They ran every code item with the course's runner, writing correct variants and cheats. Rounds: Python 3, C 3, Linux/English 3, graph 2. The first round failed 16 Python, 10 C, 7 Linux/English and 11 graph entries; all are fixed and the final verdicts pass (`curriculum/content/15h_content_qa/independent_review.yaml`).

---

# 7. Changes to accepted code

- **`tools/build_curriculum_package.py`:** supplement on supplement; revisions and retirement records; versioned refs everywhere an item is named; task versions from three stages; `teach_requires`; `notation_overlay`; `source_check_harness`; `constant_program` and the constant check; `supplement_edges` (rules, completeness, redundancy, acyclicity); the critical-Objective refusal.
- **New:** `tools/content_qa.py` (audit), `tools/validate_content_qa.py` (validator).
- **data-persistence:** `CurriculumStore` refuses an edge version or a validation record already in the store.
- **Tests:** `ShippedContentQaPackageTest`, the 15H section of `ShippedCourseTest`, `CurriculumPublishingTest`.

---

# 8. Verification

- **Suites:** `ShippedContentQaPackageTest` reads the real eighth asset after the seven; `ShippedCourseTest` publishes all eight into the real SQLite schema on the JVM: every revised item's old version is retired, not overwritten, the graph has 77 + 49 edges, a learner with no history starts the same entry lessons, and the lifetime lesson now waits for the pointer Skill.
- **Mutation:** builder 29/29 (the first full run left the self-update and redundant-edge checks alive, because no planted wrong solution or variant needed them alone; both were added and the whole set ran again); audit 10/10 (a fault planted in a copy of the content must leave an unresolved finding; the first runs planted nothing, because the audit reads a lesson's requirement from the shipped task); Kotlin 2/2. Each negative control survived.
- **Validator:** `tools/validate_content_qa.py` rebuilds all eight packages and re-runs the audit with real runs; every check passes, and its own mutation run (one change per mutant to an input it reads; a crash is never a detection) detects every mutant — the counts are in the contract's `validator_mutation`.
- **Gradle and APKs:** `verifyModuleBoundaries`, every module's JVM tests (1068) and `assembleDebug` with and without the AI adapter passed. The eight packages inside each APK have the same SHA-256 as the source; the APK without the adapter has no network permission.
- **Toolchain:** Python 3.14 and gcc 15.2 in WSL Ubuntu (the learner's generation, `D-120`).
- **Not run: T6.** Nothing ran on the device. SQLite publication ran on the JVM only.

---

# 9. Open loops

| Loop | Owner |
|---|---|
| English pools at 10–12 (a wider taught lexicon first) | a later English content step |
| `imperative_instruction_comprehension` has no transfer_integration item and `negation_question_comprehension` no authentic_application item after relabelling | a later English content step |
| git f07 does not accept the bare `ikinci`; the label lesson does not gloss the noun "copy" (imperative g07) | a later content revision |
| label calibration beyond reviewed notes (siblings of relabelled items, one-level scale) | 18C |
| contrived cheats the source checks accept (a value passed as a numeric printf argument while every variable is still read) | recorded limit |
| notation granularity (brackets credited to several Skills); code-line reading of lessons is heuristic; four teach requirements rest on a reviewer's reading, not an audit finding | recorded limit |
| `professional_evidence_checkpoint` | AŞAMA 20 |
| minute calibration | 18B |
| T6 and on-device ingestion | 19 |

---

# 10. Anti-patterns explicitly rejected

- Editing a published item, task or record instead of publishing its next version.
- A suite a program that only prints the answer passes, where printing the answer is not the work; an exemption for a whole Objective.
- A graph edge the content does not need, a hard edge where some item does not need it, or an edge already implied.
- A lesson-code finding called a scaffold when the lesson shows the construct.
- A label lowered "one step" when the review says what the item is.
- A harness that imports the learner's file instead of running it.

---

**Next step:** **16A — Skill analytics** (fresh PRE and the user's new explicit approval).
