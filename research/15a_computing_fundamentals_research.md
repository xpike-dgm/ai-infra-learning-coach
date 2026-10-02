# 15A — Computer / Programming Fundamentals — research and synthesis

**Step:** 15A · **Model:** CPFX-v0 · **Decision:** D-112 · **Date:** 2026-10-02

## 1. What had to be found out

15A is the first content step. Before writing a lesson it had to answer three questions the accepted contracts left open:

1. **What exactly is 15A's scope?** FBB-v0 §5 says the zero-entry bridge is not a new "Computer Fundamentals" domain; §6.1 names ten shared computing/programming Skills, and 6C decomposed them into twelve (it added `code_reading_basic` and `input_validation_reasoning`) with thirteen Objectives. A scan of every 6C/6D/6E/6F/6G edge showed that **no edge enters this subgraph from outside**: it is a closed set of entry points, so it can be published on its own.
2. **Can the app plan anything once content exists?** No. `FileContentSource.taskCandidates` returned an empty list because `curriculum_package/1` had no task section; the planner therefore recorded every need as having no valid candidate. A content step that adds lessons without tasks would have changed nothing a learner sees.
3. **Can AI-written items carry mastery evidence?** Only through AIV-v0's pipeline: an AI-generated item never starts trusted (§2), its generator's say-so is not validation (§3), and standard mastery needs "strong independent / deterministic / reference-backed validation" (§25). `ItemTrust` caps every non-trusted AI item at `standard_mastery_eligible`.

## 2. Sources read (2026-10-02)

Read with a plain web fetch; nothing was sent anywhere and no account was used.

| Source | Used for |
|---|---|
| GCC Manual §3.2 *Options Controlling the Kind of Output* — https://gcc.gnu.org/onlinedocs/gcc/Overall-Options.html | "Compilation can involve up to four stages: preprocessing, compilation proper, assembly and linking, always in that order"; `.c` is "C source code that must be preprocessed"; `-c` produces "an object file for each source file"; the default linked output is an executable (`a.out`). Grounds the pipeline and artifact items that cannot be executed here (no C compiler on the authoring machine). |
| GCC Manual §3.16 *Options for Linking* — https://gcc.gnu.org/onlinedocs/gcc/Link-Options.html | The section number of the linking chapter (the first draft cited §3.15; the page says §3.16 and the citation was corrected). |
| Python Glossary — *bytecode*, *interpreted* — https://docs.python.org/3/glossary.html | Python compiles source to bytecode and may cache it in `.pyc` files; source files "can be run directly without explicitly creating an executable". The first draft of the pipeline lesson said the translation happens from source on *every* run; the glossary's caching note made the sentence precise: a directly-run file is translated from source each time, cached bytecode of other files may be reused. |
| Python Library Reference — `unittest` — https://docs.python.org/3/library/unittest.html | "A test case is the individual unit of testing. It checks for a specific response to a particular set of inputs." Grounds the test-case definition item. |

## 3. What was decided with the user

1. **Validation:** every key executed where it can be, otherwise reference-grounded; plus an independent reviewer's verdict on every item and explanation. AI content stays `ai_generated` and is at most `validated`.
2. **Notation:** a readable Python subset, taught for reading in 15A's own lessons; writing Python stays 15B's.
3. **Objectives:** refine 6C's templated metadata and record every change with its reason; identities unchanged.

## 4. How the keys are checked

`tools/build_curriculum_package.py` runs CPython isolated (`-I`) with a timeout for every executable item, in one of eleven modes: the printed output (`stdout`), the state after a given line (`probe`), termination within a limit (`termination`), every option's description against the program on several inputs (`behaviour`), every option's function against the specification (`functions`), every option's test input against a faulty and a correct function (`detects`), the symptom being real and the single keyed line fixing it (`fault`), the keyed starting value being the only one that produces the target output (`which_input`), the keyed replacement being the only one that ends a non-terminating loop (`fix_terminates`), and a verification script for pipeline and artifact facts (`script`). The lessons' own code claims are run the same way. A choice is verified only if **exactly one** option is true.

The check paid for itself while writing: the lesson checks caught three wrong expected outputs in the author's own check list, the edge-count test caught a miscount (eleven, not twelve), and a review of the debugging lesson found that its examples were the very items it would later ask — they were rewritten.

## 5. Findings for later steps

- `iteration_reasoning`'s while conditions need comparisons, which `expression_boolean_reasoning` teaches; 6C makes that Skill neither a hard nor a soft prerequisite of iteration. 15A declares it on the lesson and the items (3B §10); whether the graph should say so is 15H's.
- 6C declares `requires_transfer` on every Objective, but the store's Objective row has no such column, so GRE-v0's transfer gate is never applied. The content carries transfer items regardless; whether the schema should carry the flag is 15H's.
- The store requires every entity of a package to carry the package's own version, and the app ships one asset. How a second package (15B) ships is 15B's.
