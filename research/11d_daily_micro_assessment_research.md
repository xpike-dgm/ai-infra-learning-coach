# 11D Daily Micro Assessment — Research & Decision Synthesis

**Stage step:** 11D — Günlük mikro quiz  
**Purpose:** Make one measurement runnable end to end — authored curriculum published, an item read back pinned and trusted only as far as its validation allows, and `ASUX-v0`'s single session interior as code.

## 1. Research-need decision

**No web research pass was needed.** The session interior, its states, submission, assistance, recomposition, dispute and result semantics are accepted in `ASUX-v0`; the daily scope, item minimum validity contract and evidence fit in `DMA-v0`; lifecycle, use ceiling, content origin, evaluator requirement and exposure in `QAB-v0`; the promotion rules that cap an unvalidated item in `AIV-v0`; the curriculum region and its pins in `DDM-v0` / `LDBX-v0`; module placement in `MSBX-v0`. No dependency was added — the authored package format is parsed by a strict parser written here rather than by pulling in a YAML or JSON library.

## 2. Canonical source set reviewed

- `ASUX-v0 / D-071` — one interior for three scopes; the atomic evidence boundary as the submission unit; frozen submitted boundaries; free navigation before submission; skip ≠ incorrect; help never blocked and its consequence disclosed; explicit mode conversion; five recomposition conditions; dispute → contested; evaluator status; six semantic result families with `not_reliably_measured` first class; no pass/fail, grade, percentage or threshold.
- `DMA-v0` — a daily quiz quota does not exist; question-like UI is not `assess`; four intents; the item's minimum validity contract; evidence type must fit the Objective; high-stakes use needs `trusted`/`validated` or deterministic verification.
- `QAB-v0` — seven lifecycle states; use ceiling ordered and semantic, not numeric; five content origins; evaluator requirement; allowed tools; scope eligibility; exposure state kept separate from bank metadata.
- `AIV-v0` — a generated item starts at `candidate` at best; the final ceiling is the most restrictive check result; practice use is not a licence to teach something wrong.
- `DDM-v0` / `LDBX-v0` — what the curriculum region does and does not name; composite pinning; immutability once published.
- `SESX-v0 / D-089`, `RNRX-v0 / D-088` — the frame the session inherits, and the two open loops handed here: artifact body storage and content compatibility facts.

## 3. Synthesis problems 11D had to solve

1. **The curriculum region had no writer**, so its immutability had never been tested against a real write.
2. **`DDM-v0` names only part of `QAB-v0`'s item.** The rest had to live somewhere without inventing columns.
3. **An item's own document cannot be trusted about its own trust.**
4. **The artifact body had no home**, and both obvious homes — a new table, or files beside the database — would have amended an accepted contract (10D's declared table set) or an accepted mechanism (10E's single-file verified export).
5. **A later curriculum version may carry only a revalidation**, which a package-local reference resolver refuses.
6. **The result must be able to say "nothing changed"** without inventing a state claim, while the engines that would report a change do not exist yet.
7. **The mutation harness had never actually run Gradle**, so its results — including 11C's — were meaningless.

## 4. Positions taken

- **One write path**, one transaction, refusal computed before any write, and `AlreadyPublished` instead of an overwrite.
- **Authored content carries what the schema does not name**, and the strict parser refuses anything it does not understand rather than importing a guess.
- **Trust is the store's validation record**, and the document's claim about itself is overridden; the effective ceiling is the most restrictive applicable rule and can never exceed the declared one.
- **The Objective decides evidence fit**; mastery needs its direct type; an unknown profile is unfit.
- **The artifact body is carried inline as a `data:` URI, with an explicit refusal above 4096 characters** — atomic with the attempt, inside the existing export, and no schema, port or accepted contract touched. A real blob store is 14/15's when larger artifacts exist.
- **References resolve against the package or the store**, both version-pinned — found by a T2 check rather than by review.
- **A result claims a change only when canonical state reported one**, and says plainly that nothing changed otherwise.
- **The harness was fixed and both suites re-run**, and 11C's published mutation number was corrected rather than left standing.

## 5. Explicitly not decided in 11D

Planner selection of assessments and the prerequisite gate facts (12), the evidence pipeline (12), weekly and monthly blueprint composition (13), AI evaluator behaviour and rubric content (14), final microcopy (14), the authored curriculum itself (15), `assessment_report` (16B), evaluator and item calibration (18D). No passing threshold, quota, fixed question count or countdown is claimed.
