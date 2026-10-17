# 15H — Content QA — research and synthesis

**Step:** 15H · **Model:** CQAX-v0 · **Decision:** D-121 · **Date:** 2026-10-10

## 1. What had to be found out

1. **What is "content QA" once 15A–15G have shipped?** Each step checked its own content as it was written: every key run, every item reviewed. What no single step could see was left as open loops owned by 15H: needs the graph does not state (15A–15F), suites a fixed output passes (15B, 15C), constructs the notation does not name (15B–15E), `requires_transfer` not stored (15A–15D), generous difficulty labels (15D–15G) and the pool shortfall (15G). 15H audits Stage 15 as a whole for these, and corrects without overwriting anything published.
2. **How is published content corrected?** `LFPS-v0`: a published version is never overwritten; a correction is a new version (`CurriculumPackage`). An item is a resource version with an append-only validation history, and the store reads the newest record. So a flawed item gets its next version — same logical id, same variant family — and the version it replaces is retired by an appended `retired` record. Tasks that presented it get their next version.
3. **What should the graph say?** `PRG-v0` §6: the graph carries the general learning dependency, a task's own `required_skills` what that exact task needs — and is hard even where the graph is silent. §2.2: if a "soft" Skill is really needed, the metadata or graph must say so. The user's rule (2026-10-10): an edge wherever a lesson needs a Skill the graph does not imply — hard only where the lesson and every item outside it need it, soft otherwise.
4. **When is a code item's suite adequate?** A suite must tell a program that does the work from one that does not. The cheapest program that does not is the one that prints the expected output.
5. **Does `requires_transfer` need storing now?** `GRE-v0` §8 (standard gate) has no transfer condition; §9 (critical gate) does. All 65 published Objectives are standard.

## 2. Sources read (2026-10-10)

The sources were read with a plain web fetch. Nothing was sent anywhere and no account was used.

| Source | Used for |
|---|---|
| Wikipedia — Test oracle — https://en.wikipedia.org/wiki/Test_oracle | An oracle supplies the correct output a test checks against; the oracle problem is deciding what that output should be, and partial oracles check important properties only. A code item's expected output is a partial oracle: it says what must be printed, not how it was produced. Where the Objective is the production (a declaration, a computation, a fix in logic), the suite must also observe that — a harness that reads the program's variables, or its source for the asked construct. |
| Wikipedia — Mutation testing — https://en.wikipedia.org/wiki/Mutation_testing | A suite is judged by the deliberate faults it kills; a surviving mutant usually marks a gap, though an equivalent mutant can never be killed. The program that only prints the expected output is treated as a mutant every suite must kill — unless printing that output is the construct (an equivalent mutant for that Objective, recorded as an exemption). The builder, the audit, the Kotlin and the validator were themselves tested by mutation. |

Internal contracts read again: `PRG-v0` §§2, 6, 17 (hard and soft; a task's own requirement; a teach task is not blocked by its own prerequisite's repair); `KGC-v0` §12 (edge fields, `reason_kind` categories); `GRE-v0` §§7–9 (Objective profile, standard and critical gates); `AIV-v0` (validation is checked, never assumed); `QAB-v0` (difficulty classes, roles); `CDEX-v0` (code is judged by what it does on the learner's computer); `LFPS-v0` / `LDBX-v0` (append-only, never overwrite); D-113, D-120 (incremental packages, supplements); the open loops of `CPFX-v0`, `PYFX-v0`, `CFNX-v0`, `MMFX-v0`, `LGSX-v0`, `EAAX-v0`, `ACNX-v0` and their independent reviews.

## 3. Decisions taken (user, 2026-10-10)

- **Graph:** add the edges the content relies on, in a v8 supplement; hard only where the lesson and every item outside it need the prerequisite, soft otherwise.
- **`requires_transfer`:** close it: a standard Objective has no transfer gate (`GRE-v0` §8); the builder refuses a critical Objective until GRE-v0's gate fields are stored.
- **Flawed published items:** publish the next version and retire the old one by an appended record; tasks grow a version.
- **Pool:** technical Objectives to 15; English keeps 15G's honest small pool.
- Approval: the user's explicit approval of 15H ("diğer adıma geçebilirsin"); authorship as in 15G — the assistant writes, separate reviewer agents judge.

## 4. What was found

- **Most lesson needs were already declared, just not in the graph.** 15A–15G declared them at task and item level, so the gate was right; the graph — what explanations, diagnostics and the planner's blockers read — was silent. Of 43 lesson needs not implied by the published graph, all are now edges (15 hard, 28 soft; with four "reads glossed" soft edges, 47 in all).
- **A lesson can show code its teach task does not reach.** Reading the lessons' code lines found 15 Skills whose lesson shows a construct of a Skill the teach task did not require (most often an `if` in a Python or C lesson, the lifetime lesson's pointers). Their teach tasks now require it. Four places where declaring the need would make two lessons wait for each other are recorded instead (the `if` that checks `scanf`'s result, a prototype in the link-error lesson, a `/` in the declaration trace) along with Git's "# hazırlık" device.
- **Most lesson-check findings are verification scaffolds.** A check wraps the lesson's code in a loop over test values or a try/except that prints an error's type; the lesson never shows those lines. The audit verifies that a scaffold's construct is not on a shown line.
- **A fixed output is the construct in some Objectives and a validity hole in others.** For `compile_and_run_small_program` and for a script whose error is in the print statement itself, printing the text is the work. For a declaration, an assignment, a computation or a fix in logic, it is not: 29 items got a next version whose suite also reads the program's variables, or checks that the asked declarations are there, that the computed value is not written in the source, and that no declared variable goes unused.
- **The reference solution, not the item, used untaught constructs.** Five Python references used `in`, `is`, `pass` or `continue` where the lessons never show them; each item is solvable with taught constructs, proven by an alternative that passes the published suite. Nothing shipped changed.
- **Text similarity is not a near-variance test.** A scan of every Objective's items flagged 128 pairs above 0.85 similarity; the ones opened were short "what does this print?" prompts measuring different rules. Near-variance stays a reviewer's judgment.
