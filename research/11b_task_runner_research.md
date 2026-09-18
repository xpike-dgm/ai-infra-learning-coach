# 11B Task Runner — Research & Decision Synthesis

**Stage step:** 11B — Task runner  
**Purpose:** Turn `TRUX-v0`'s focused flow into running code, write the product's first learner record, and decide what the runner may truthfully do while no task can yet be confirmed startable.

## 1. Research-need decision

**No web research pass was needed.** The flow, its states, the assistance choreography, provenance and pause semantics are accepted in `TRUX-v0`; the attempt, artifact, assistance and provenance tables and their value sets in `DDM-v0` and `LDBX-v0`; one-action-one-transaction in `LFPS-v0`; module placement in `MSBX-v0`; tones in `VDSX-v0`. No dependency was added.

## 2. Canonical source set reviewed

- `TRUX-v0 / D-070` — the runner is an execution surface, not planner, mastery, prerequisite or evidence authority; seventeen states; six phases; five entry and five resume conditions whose failure is not negative evidence; help always requestable, escalated only on request, consequence disclosed before H3/H4 in measurement terms, no auto-reveal; provenance asked not inferred with `unknown_provenance` as an honest answer; three pause classes; stop costs nothing; `evaluation_pending` is neither pass nor fail; the next task is a recomputed planner selection.
- `DDM-v0 / D-077` — `assistance_event` and `artifact_provenance` fields and value sets; `attempt` and `artifact` named without fields.
- `LDBX-v0 / D-085` — the physical tables; CHECK constraints built from the same Kotlin lists; forbidding "inventing entity fields the data model does not name".
- `LFPS-v0 / D-076` — one learner action is one transaction; an attempt may not be persisted without its assistance metadata or provenance; `evaluation_pending` writes no evidence.
- `TDYX-v0 / D-087` — Today's start action, and the fact that Today can confirm only that its task is still selected.

## 3. What was found before writing runner code

The previous step's own sync script had put **U+0307 COMBINING DOT ABOVE** into five Turkish words on `main`, because `"İ".lower()` in Python is a dotted `i` plus the combining character, not `i`. Nothing had noticed: the words render almost identically. They were fixed, and the new validator scans every text file for the character. Its first run caught the validator itself, because the writing tool had turned the escape sequence into the literal character — so the guard now builds it with `chr(0x0307)`.

## 4. Synthesis problems 11B had to solve

1. **Nothing can be started yet.** Prerequisites, the open need, content compatibility and local capability are other engines' facts, and none exists.
2. **The schema does not hold everything `TRUX-v0` lists for a submission.** `planned_task_ref`, `runner_completion_state`, component results and target objectives have no columns, and `DDM-v0` does not name `attempt`'s fields.
3. **Rows of one action must reference each other inside one transaction**, but `appendTruth` returned nothing.
4. **The proof of atomicity needs the real engine**, but `data-persistence` may not depend on `core-application`.
5. **A rule can be quietly narrowed by a reasonable reading.** "Before granting H3 or H4" has no scope condition; target-scoped help is where the disclosure's content is literally true.
6. **Values chosen by hand can coincide with the contract** and still be the wrong kind of evidence.

## 5. Positions taken

- **Entry needs confirmation, and unconfirmed is `unmet`, not `failed`.** No task is startable today; that changes by engines supplying facts, not by loosening the rule.
- **The runner writes attempt, artifact, provenance and assistance — never evidence.** Everything `TRUX-v0` lists that has no column is either derivable (and deliberately not stored) or owned elsewhere: `planned_task_ref` and `runner_completion_state` → 12, component results and target objectives → 12, artifact bodies → 11D, checkpoint content → 11C. Nothing was invented.
- **`appendTruth` returns the row id**, a recorded port refinement.
- **The proof is split:** T1 proves the use case's shape; T2 proves the same row sequence atomic on real SQLite.
- **The disclosure rule is applied as written**, without a scope condition, and a mutant that narrows it is caught.
- **Tones moved to core** after the first draft chose them in the UI — correctly, by coincidence.

## 6. Explicitly not decided in 11B

The evidence pipeline and the facts that confirm entry (12), content (15), checkpoint persistence and session state (11C), artifact storage and the first runnable activity (11D), the assessment interior (13), AI assistance content and final microcopy (14). No session length, step count or timing is claimed.
