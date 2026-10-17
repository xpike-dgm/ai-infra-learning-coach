# 16A — Skill analytics: research synthesis

**Step:** 16A — Skill analytics (`SKAX-v0` / `D-122`)  
**Question:** how should a learner-facing Skill view show what was demonstrated and what needs attention, without becoming a score?

## 1. Internal contracts (binding)

- `SPWX-v0 / D-072` (`docs/PROGRESS_SKILL_UX_SPEC.md`):
  - one eight-state vocabulary with a declared precedence (§4);
  - the axes are ordered, not collapsed (§5);
  - `at_risk` is a qualifier (§5.1);
  - the overview has two halves, an inventory and an attention set (§7);
  - the `skill_detail` content contract (§8);
  - "Progress may count. It may not score." (§14);
  - degraded modes never show a stale projection as current (§16);
  - ten semantic states (§17) and an accessibility baseline (§18).
- `TEPM-v0 / D-067` (`docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`):
  - the derivation precedence (§4.1);
  - assisted progress is distinct from independent mastery (§9);
  - invalid, provisional and contaminated evidence produces no clean claim (§11);
  - no numeric aggregate (§16);
  - detailed progress analytics are deferred to 16A/16C (§18).
- `RVR-v0` (`docs/RETENTION_FORGETTING_SPEC.md`) §2:
  - `review_due` does not mean forgotten;
  - `at_risk` is a mixed-evidence concern on a Skill that is still mastered, and never comes from elapsed time alone.
- `PRG-v0`, as implemented in `PRQX-v0`:
  - readiness (what a Skill is worth as a prerequisite) is not the same thing as the Skill's own prerequisite resolution;
  - the latter is the gate's eligibility for new work on the Skill, and it drives `prerequisite_unresolved`.
- `VDSX-v0 / D-073` (`ux/8f_design_system/design_system.yaml`):
  - one tone per state;
  - `partial_projection_available` is pending;
  - only `error_recoverable` and `data_recovery_required` take the fault tone;
  - membership in an attention group does not change a tone.
- `WFPX-v0 / D-074` §§5.2–5.3:
  - the overview keeps its two halves, with no ratio, bar, gauge or percentage;
  - `skill_detail` shows the chip *and* all four axes.
- `MSBX-v0 / D-078`:
  - `core-application` and `core-presentation` may not depend on each other;
  - the presentation projection stays in core.

## 2. External sources

- **Goodhart's law.** Wikipedia, https://en.wikipedia.org/wiki/Goodhart%27s_law, read 2026-10-17.
  - Once a statistic is made a target, people steer toward the number, and it stops reflecting what it measured. Strathern's 1997 wording: "When a measure becomes a target, it ceases to be a good measure."
  - Her education example: an expected grade stops distinguishing individual performances.
  - The article's related entries include surrogation (the measure replacing the thing measured) and Campbell's law.
  - **Used for:**
    - the rule that Progress shows an inventory of current state, never a percentage or score the learner could optimise;
    - the rule that Progress gives the planner no priority.
- **Learning analytics.** Wikipedia, https://en.wikipedia.org/wiki/Learning_analytics, read 2026-10-17.
  - The article lists dashboards neutrally, as a visualisation of learning data.
  - The learner-facing use it names is reflecting on one's achievements and behaviour patterns *compared with other learners*.
  - It notes that what serves the learner can conflict with what serves an institution.
  - Its ethics section stresses explaining what the analytics covers, human feedback and error correction, and trust.
  - **Used for:** the product's single-learner, non-comparative framing — the app has no other learners and shows no comparison — and for explaining every state in words.
  - **Not used for:** claims about reductive metrics or misreading numbers. The article does not make them.

## 3. Synthesis

- **One derivation.**
  - The eight states and their precedence are one function in `core-model`. The stored column and the screen both call it, so a Skill opened from any surface carries the same truth (`SPWX-v0` §8).
  - Recomputing is the honest answer when an axis is missing or the row is stale (user decision). It is not a ninth state.
- **Counting, not scoring.**
  - Counts are inventory lines beside their labels. No projected type holds a total, ratio or floating-point value, so a percentage cannot be built from them.
  - Groups exist only for orientation and carry no count, so no domain score can form.
- **Evidence kinds stay apart.** The first kind that applies wins: not counted, then provisional, then assisted, then independent. A row that cannot count is never shown as weaker evidence, and a provisional judgement is never shown as an independent one.
- **The gate is asked again.** A dependent's stored prerequisite axis can be stale until reverse invalidation exists (18E). The read asks the gate, so the overview's waiting set is the planner's waiting set.
