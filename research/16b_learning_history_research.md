# 16B — Learning history: research synthesis

**Step:** 16B — Öğrenme geçmişi (`LHRX-v0` / `D-123`)  
**Question:** how should a learner see what happened and what it changed over time — without the history becoming a streak, a gradebook, or a second source of truth?

## 1. Internal contracts (binding)

- **`SPWX-v0 / D-072` §12, `learning_history`** (`ux/8e_progress_skill_weakness/progress.yaml`):
  - event families: learning, assessment, review, remediation;
  - it records what happened and what changed;
  - it is never a streak calendar, a contribution graph or an attendance heatmap;
  - no consecutive-day success metric;
  - a gap is never shown as a failure or a missed obligation;
  - entries link to their subject.
- **`SPWX-v0` §13, `assessment_report`:**
  - longitudinal, and owns no mastery;
  - never aggregated into a score, grade or trend line;
  - `not_reliably_measured` is first-class;
  - provisional results stay labelled;
  - it never contradicts the in-session view.
- **`ASUX-v0 / D-071`:**
  - §13.1 lists the six families;
  - §13.2 says raw counts are informational only;
  - §13.3 lists the reasons something counts as not reliably measured;
  - §13.4 says a change may be stated only if canonical state changed, and "nothing changed" is said plainly;
  - §14 separates the session view from `assessment_report`.
- **`PCRX-v0 / D-103` (13E):**
  - `StateChangeKind` and its families;
  - `ProgramChangeEngine.stateChanges`, a diff of two readings;
  - `ProgramChangeCopy`, the sentences;
  - a value no engine had written is not treated as a "before".
- **`GRE-v0` §16.2:**
  - the first clean contradiction opens `verification_due` and keeps mastery;
  - a fresh or unseen H0 recheck follows;
  - a positive recheck keeps mastery and clears the verification;
  - a negative recheck lets the gates decide again.
- **`LFPS-v0` / `DDM-v0`:**
  - evidence is the source of truth;
  - state is a recomputable projection of it;
  - truth is append-only;
  - no tutor or learner text is stored (user decision).
- **`TRUX-v0` / 11C:** the working session is emergent and ungraded, and it is not stored.
- **`UXIA-v0`:** the accepted edges are `progress → learning_history`, `progress → assessment_report`, `skill_detail → learning_history` and `assessment_report → learning_history`.

## 2. External sources

- **Goodhart's law.** https://en.wikipedia.org/wiki/Goodhart%27s_law, read 2026-10-17 (16A).
  - Once a measure becomes a target, people optimise the number instead of what it stood for.
  - Used for: the history has no streak or consecutive-day count, and the report has no grade or trend a learner could chase.
- **Learning analytics.** https://en.wikipedia.org/wiki/Learning_analytics, read 2026-10-17 (16A).
  - The ethics section stresses explaining what the analytics covers, human feedback and error correction, and trust.
  - Used for: every entry says in words what happened and what the engines said it changed, and nothing is stored that the learner did not produce as evidence.

## 3. Synthesis

- **Replay, not a log.** A history of "what changed" kept as its own table would be a second source of truth, and it would disagree with the engines whenever a rule or a disposition changes. The history therefore replays the canonical engines over the stored evidence.
- **Replaying exposed the engine's path dependence.** Mastery continued from the stored axis, so a rebuild was not a pure function of evidence. The fix decides mastery from evidence in order and tracks which contradictions are handled, which makes `GRE-v0` §16.2 hold exactly: one answer per body of evidence.
- **Only days with entries.** A history that draws the days on which something happened cannot draw a missed day; there is nothing to render.
- **A session is its families.** The report never totals sessions. It states each session's changes in 13E's words, names what could not be measured, and claims a plan change only when the plan version written for that session is found.
