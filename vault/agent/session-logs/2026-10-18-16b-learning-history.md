---
type: session-log
status: completed
stage_step: 16B
model: LHRX-v0
decision: D-123
date: 2026-10-18
---

# 16B — Öğrenme geçmişi — history replayed from evidence, and the mastery recheck corrected

On 2026-10-17 the user said "merge et devam et". 16A (#60) was squash-merged after CI went green (534cb53). The fresh 16B PRE showed `16A ✅ / 16B active-not-executed`. That message was also the approval for 16B.

## Result
- Canonical: `docs/LEARNING_HISTORY_SPEC.md`
- Machine-readable: `arch/16b_learning_history/learning_history.yaml`
- QA: `arch/16b_learning_history/qa_report.yaml`
- Stale audit: `arch/16b_learning_history/stale_reference_audit.yaml`
- Research/synthesis: `research/16b_learning_history_research.md`
- Final: `LHRX-v0 / D-123`
- Not run: T6. The screens have not been seen on a device.

## User decisions
- What changed: replay the engines over the stored evidence. No new table.
- Scope: learning history plus the longitudinal assessment report.
- No tutor or learner text is stored. The history shows assistance levels. Working sessions stay unstored.
- Delivery: core plus screens.
- 2026-10-18: fix the mastery recheck bug inside 16B, as a declared correction.

## Found
- 13E's session report was never stored.
- Replaying the evidence row by row disagreed with the batch recompute. The cause was in 12A's mastery engine.
  - While a verification was open, every rebuild treated the contradiction that had opened it as a failed recheck.
  - So two recomputes of the same evidence disagreed.
  - A passed fresh recheck also took mastery away, which contradicts `GRE-v0` §16.2.
- The 12A test that pinned this behaviour had no fresh recheck in its input.
- A passed recheck also revalidates retention and resolves an open weakness question.
- First Kotlin mutation run: four mutants survived.
  - H05 survived because nothing tested that the forgiven contradiction leaves the score window, which only shows on the row after the recheck. A test now covers it.
  - H06 survived because nothing tested that an adjudicated contradiction never reopens a verification. A test now covers it.
  - H10 survived because nothing tested that a session's own rows are in the assessment family. A test now covers it.
  - H12 was equivalent, because the row that closes a remediation is always made while it is open. The redundant condition was removed from the code.
- One new test failed at first because it created rows out of order; a row's sequence is the order it is created in.

## Next
`16C — Progress / weakness gösterim kuralları` needs a fresh PRE and the user's new explicit approval.
