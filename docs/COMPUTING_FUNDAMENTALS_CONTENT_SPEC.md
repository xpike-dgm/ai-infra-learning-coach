# Computer / Programming Fundamentals Content Specification — CPFX-v0

**Stage step:** 15A — Computer / Programming Fundamentals  
**Status:** ACCEPTED — independent 15A QA PASS  
**Decision:** `D-112`  
**Model:** `CPFX-v0 — Computer / Programming Fundamentals content`  
**Behaviour it implements:** `FBB-v0` §5–§6.1 (the zero-entry bridge is placement, not a new domain; the shared computing/programming Skill seeds) and §16 (every published required Objective has teach, guided and independent practice, an H0 evidence path, remediation and retention); `KGC-v0` §8, §25, §27–§28 (Objective contract, versioning, lifecycle, graph invariants); `QAB-v0` §7, §15, §19, §22 (origin, variant families, answer keys, durations); `AIV-v0` §2–§3, §23–§25 (an AI-generated item is never trusted by itself; generator output is not validation proof; what standard mastery needs); `GRE-v0` (two variant families); `TASK_TAXONOMY_SPEC` (3B) §3, §10, §15, §19  
**Content:** `curriculum/content/15a_computing_fundamentals/` → `android/app-wiring/src/main/assets/curriculum_package.txt` (generated, never hand-edited)  
**Persistence / ports / schema:** unchanged — a new authored section (`[task]`) is content served by the existing `ContentPort.taskCandidates`; two format refinements

## 1. Purpose

Up to 14G every engine, contract and evaluator existed, but the app taught nothing: no package shipped, and no format could carry a task, so the planner recorded every need as having no valid candidate. 15A writes the first real content — the zero-entry computing/programming bridge — and makes it reachable. It answers one question:

> **Sıfırdan başlayan bir öğrenci için ilk gerçek içerik nasıl yazılır — ve yapay zekâ tarafından yazılmış bir sorunun doğru olduğuna neye dayanarak güvenilir?**

Primary invariant:

> **Content is only as trustworthy as what checked it.** Every answer key is checked against what really happens — the code is run, every option of a choice is run, a lesson's own example outputs are run — or, where nothing can be run, against a cited authoritative source; and every item, run or cited, needs an independent reviewer's pass before it is validated. Nothing AI-written is ever trusted by itself, nothing a lesson has not taught is read in an item, and a task serves only a need the planner already opened, about its own Skill, for a purpose that may serve it.

---

# 2. What was found before writing content

- **No task could exist.** `curriculum_package/1` had no task section and `FileContentSource.taskCandidates` returned an empty list, so even with content the plan would have stayed empty.
- **The subgraph was off the route.** Every 6C Skill, Objective and edge is `draft`; the planner opens `new_learning` only for `published` Skills (`KGC-v0` §27).
- **6C's Objective metadata was a template.** The two iteration Objectives had identical statements, success criteria were generic, and every Objective required `explanation` as its direct type — including ones whose observable behaviour is reading code.
- **The format cut content.** Everything after a `#` on any line was a comment, so a Python comment or a heading inside a value was silently truncated; an item prompt did not decode line breaks, so code could not span lines.
- **A prerequisite the graph does not state.** Every `while` condition is a comparison, taught by `expression_boolean_reasoning`; 6C makes that neither a hard nor a soft prerequisite of `iteration_reasoning`.
- **A gate the store cannot apply.** 6C declares `requires_transfer` on every Objective, but the store's Objective row has no such column, so `GRE-v0`'s transfer gate is never applied.
- **One asset, version-bound entities.** The store requires every entity of a package to carry the package's own version, and the app reads one asset.

---

# 3. User decisions (2026-10-02)

1. **Validation: executed keys plus an independent review.** Every key is checked by running the code where possible, otherwise by a cited authoritative source, and every item and explanation needs an independent reviewer's pass. AI content stays `ai_generated`, is at most `validated`, and so at most `standard_mastery_eligible`.
2. **Notation: a readable Python subset.** These Skills are language-neutral reasoning, but reasoning needs a notation; 15A's lessons teach a small Python subset **for reading**. Writing Python remains 15B's.
3. **Objectives: refine and record.** Each Objective keeps its 6C identity; its statement, observable behaviour and direct evidence type are made specific, and each change is recorded with its reason.

---

# 4. Scope boundary

## 4.1 15A decides

- which Skills and Objectives the first package publishes, and their final metadata,
- the reading notation and which lesson teaches each construct,
- the lessons, written alternatives, misconception catalog, items, answer keys, rubrics and tasks of those Skills,
- the `[task]` section and how a task serves a need,
- how an AI-written item becomes validated.

## 4.2 15A does not decide

- Python as a language (15B), C (15C), memory (15D), Linux/Git/Shell (15E), English (15F), cross-skill assessment content (15G), content QA across stage 15 (15H),
- how the runner presents a task's pool and picks unseen items (16D),
- calibration of minutes, thresholds or evaluators (18),
- any engine, schema or port rule.

No score, weight, threshold or similarity measure is introduced.

---

# 5. The subgraph (ratified `draft` → `published`)

Exactly `FBB-v0` §6.1's shared computing/programming Skills as 6C decomposed them — 12 Skills, 13 Objectives, 11 internal edges (10 hard, 1 soft), 8 primary teaching topics. No edge enters the subgraph from outside, so it is published on its own; edges *out* of it are published by the packages of their targets. Identities are 6C's. Entry points (no hard prerequisite): `skill.computing.program_execution_model`, `skill.programming.state_assignment_model`.

| Skill | Objective(s) | Direct type (6C → 15A) |
|---|---|---|
| program_execution_model | explain_pipeline | explanation → explanation |
| source_runtime_artifact_distinction | classify_artifacts | explanation → explanation |
| state_assignment_model | trace_state | explanation → **code_reading** |
| expression_boolean_reasoning | evaluate_condition | explanation → **code_reading** |
| branching_reasoning | predict_branch | explanation → **code_reading** |
| iteration_reasoning | trace_iteration, detect_nontermination | explanation → **code_reading** (both; statements separated) |
| function_decomposition | define_io_responsibility | explanation → explanation |
| trace_execution_basic | produce_trace | explanation → **code_reading** |
| debug_localization_basic | locate_cause | explanation → **code_reading** |
| test_case_basic | design_expected_case | explanation → explanation |
| code_reading_basic | demonstrate_capability | explanation → **code_reading** |
| input_validation_reasoning | demonstrate_capability | explanation → **code_reading** |

Acceptable evidence types are unchanged; `explanation` stays a direct type wherever it was one. Criticality is unchanged (`standard` everywhere). Every change is recorded per Objective in its Skill file's `reconciliation`.

---

# 6. The reading notation

`notation.yaml` names each construct, the pattern that recognises it in code, and the lesson(s) that introduce it: `print` and string literals (program execution, state), assignment, augmented assignment, `+ - *` and comments (state), `//`, `%`, comparisons and `and`/`or`/`not` (expressions), `if`/`elif`/`else` (branching), `while` and `for … range` (iteration), `def`/`return` (functions). Six construct families are never introduced and are forbidden in every 15A item: true division, `input()`, collections, imports, conversion built-ins and method calls.

An item may use a construct only if its own Skill, a hard prerequisite of it, or a Skill it declares in `required_skills` introduces it; otherwise the build refuses to validate it. The item's `forbidden_not_yet_concepts` lists what remains unreachable for it. `iteration_reasoning` declares `expression_boolean_reasoning` for its lesson and its `while` items (`lesson_requires`, §2).

---

# 7. Content per Objective

Every Objective has:

- **the course's own explanation** (`canonical`), and a **worked example** (H3);
- a **prerequisite refresher** (H1) wherever the Skill has a hard prerequisite, and a **state trace** (H2) where tracing is the behaviour;
- **two or three catalog misconceptions** (`WAAX-v0`), each with a neutral open question and a written contrast (H2);
- **seven items** in seven variant families (two basic, two authentic application, three transfer), plus a rubric item (`r1`) for four explanation Objectives;
- **lesson checks**: every code claim a lesson makes is executed.

Items are short-answer or choice; each has an answer key (`OREX-v0`, verified, ASCII-only case fold) or, for `r1`, a rubric (provisional at most, low-stakes ceiling). Each item is `h0_required` except the two basic ones (`guided_allowed`), scoped for daily, weekly and monthly use with roles declared by difficulty and criticality, and prohibits `terminal`, `compiler`, `debugger` and `external_ai` — the point is to read the code, not run it.

---

# 8. How an AI-written item becomes validated

`tools/build_curriculum_package.py` builds the package from the content source and checks every item:

| Mode | What is run and required |
|---|---|
| `stdout`, `probe` | the printed output (or the state after line *k*) is the key; for a choice, exactly one option |
| `termination` | the loop ends within the limit exactly when the key says it does |
| `behaviour` | each option's description, as a function, is compared with the program on several inputs; only the key agrees on all |
| `functions` | each option's definition is run against the specification; only the key passes |
| `detects` | each option's input separates the faulty function from the correct one only for the key |
| `fault` | the stated symptom is real, and replacing the keyed line alone gives the intended output |
| `which_input`, `fix_terminates` | only the keyed starting value / replacement line produces the target / ends the loop |
| `script` | a verification script reproduces the pipeline or artifact fact (files written, bytecode cached, output produced) |
| `reference` | no execution possible here (C has no compiler on the authoring machine; definitions): an authoritative citation and the claim it supports |
| `rubric` | an open response; its criteria are reviewed |

An item is written `validated` only if its check passes, it uses no hidden construct, **and** the independent review (`independent_review.yaml`) gave it a pass; otherwise it is `candidate` — practice at best. A task is `validated` only if every item it presents is. Validation records name the method (`15a/executed_key+independent_review`, `15a/reference_grounded+independent_review`, `15a/rubric_reviewed+independent_review`), origin `ai_generated`, instant 2026-10-02T12:00Z. The build is deterministic; the validator rebuilds the package and requires the shipped asset to be byte-identical.

Result: 95 items — 83 executed, 8 reference-grounded, 4 rubric; 79 explanations; 34 misconceptions; 60 tasks; every item and explanation independently reviewed and passed.

---

# 9. Tasks (`AuthoredTask`, `TaskServing`, `[task]`)

A task is the content side of 3B §15's `TaskCandidate`: `task.<namespace>.<slug>`, one primary Skill and its Objectives, one purpose, a 3B §3.2 activity kind, the needs it serves, an authoring estimate of minutes (uncalibrated; 18B), the explanations and items it presents, required Skills, and a validation status. It carries no priority and nothing about the learner.

Which needs a purpose may serve is closed and taken from 3B §19's examples, `PBR-v0`'s verification work and `WLRM-v0`'s repair:

| Purpose | Serves |
|---|---|
| teach | new_learning |
| practice | continue_learning, parallel_track_due |
| assess | verification_due |
| retain | retention_review_due |
| remediate | remediation_required, weakness_detected |
| reinforce | reinforcement_opportunity, integration_opportunity |
| diagnose | — (composed from the open diagnostic, 13F) |

A task serves a need only if it declares the need's trigger **and** the need is about its own Skill; one task offered to two needs is two candidates (`authored:<task>@v1:<needKey>`). The parser refuses a task that presents anything the package lacks, names another Skill's Objective, or declares itself `validated`/`trusted` over an item the package does not validate. Each Skill has five: `teach` (canonical + worked example + the in-lesson item), `practice` (basic and authentic items, the rubric item), `check` (authentic and transfer, atomic), `review` (all keyed items, atomic), `repair` (refresher, trace and contrasts + all keyed items). Which unseen item of a pool is shown is the runner's (16D).

---

# 10. The format refinements

- **Whole-line comments only:** a line starting with `#` is a comment; a `#` inside a value is content.
- **Prompt line breaks:** a backslash-n in an item prompt is a line break, as in an explanation.
- **`[task]`:** the strict section above; unknown keys, triggers, purposes and activity kinds refuse the package.

---

# 11. Changes to accepted code

- `core-model`: `AuthoredTaskFacts.kt` (`AuthoredTask`, `TaskServing`).
- `data-curriculum`: `PackageFormat` (the refinements; `Parsed.tasks`), `FileContentSource.taskCandidates`.
- `app-wiring`: the generated asset; its first JVM test; `kotlin-test-junit5` named for it.
- Narrowed gates (declared in the contract): 11D's `E11D-13_no_asset_ships` (an asset may ship, but only the generated one) and 12C's `E12C-09_adapter_answers_truthfully` (the adapter answers with authored tasks only).

---

# 12. Verification

- Suites: `AuthoredTaskFactsTest` (model), `PackageFormatTest` (format), `ShippedPackageTest` (the real asset through the real parser and adapter: published and closed, keys unique, every Objective measurable by two families of its direct type, every item keyed or rubric'd, every misconception contrasted, every Skill served for each need, everything validated), `FirstPlanTest` (the real asset through the real gate and planner for a learner with no history: the two entry lessons are planned within the budget, the ten dependent Skills wait, no need is left without a candidate, the plan is written as truth once).
- Validator `tools/validate_computing_fundamentals.py`, reading FBB-v0 §6.1, 6C, 3B §3.2 and the Kotlin, rebuilding the package byte-for-byte, with its own mutation test.
- **Mutation:** see the contract's `mutation_results`; a compile failure is not a detection.
- **Not run: T6.** Nothing ran on the device, and the first ingestion of the shipped package into SQLite is the learner's, on the phone.

---

# 13. Open loops

| Loop | Owner |
|---|---|
| how a second package ships (one asset; version-bound entities) | 15B |
| `requires_transfer` declared by 6C but not stored, so the transfer gate never applies | 15H |
| whether `expression_boolean_reasoning` belongs in the graph as a prerequisite of `iteration_reasoning` | 15H |
| the runner presenting a task's explanations and choosing unseen items from its pool | 16D |
| calibrating task and item minutes | 18B |
| T6 and on-device ingestion | 19 |

---

# 14. Anti-patterns explicitly rejected

- an AI-written key published without being run or cited, or without an independent verdict,
- an item validated because the model that wrote it said so,
- a construct read in an item before any prerequisite lesson taught it,
- a lesson whose example output nobody ran,
- a lesson that already contains the problem it will later ask,
- a task that serves a need it was not written for, about another Skill, or claims more trust than its items,
- a draft subgraph left off the route, or an identity changed while "refining",
- a minute estimate presented as calibrated.

---

**Next step:** **15B — Python Foundations** (fresh PRE + explicit user approval).
