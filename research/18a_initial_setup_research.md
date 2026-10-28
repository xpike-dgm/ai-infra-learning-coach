# 18A Research — Pilot başlangıcı: ilk açılış kurulumu (ISTX-v0)

**Step:** 18A — Pilot başlangıcı (`D-139`; `D-140` adds 18G)
**Date:** 2026-10-28

## 1. What the accepted contracts already say

- `UXIA-v0` §7.4 / `ia.yaml`: `initial_setup_flow` is pre-shell, "the minimum local setup, then Today", for "initial daily capacity/preferences and introduction to diagnostic opportunities"; `mandatory_diagnostic_invented_by_ia: false`. No screen, no code existed for it.
- `VDW-v0` §3 lists the sources a diagnostic may come from — `user_requested_fast_path`, `planner_diagnostic_opportunity`, `prior_experience_claim`, `curriculum_entry_placement`, `resume_after_external_learning` — and says self-report only selects scope; a diagnostic is never mandatory. 13F made the learner the only one who opens one; `curriculum_entry_placement` waited for "an entry flow" and 16H named it for 18A.
- 16D (`STGX-v0`): the daily length is a preference, a ceiling, never a target; presets 30/60/90 until the learner sets them.
- `V1_SUCCESS_CRITERIA` SC-048/049 and §14.7: a real-use pilot of at least 14 days with no hard-rule violation, and most plans without a structural manual correction. The user split the pilot's running (the journal and the phone install) into a new step, 18G (`D-140`).

## 2. Decisions taken from the sources

- **Areas, not Topics, for "I know this".** The curriculum's own grouping (`topic.<area>.<name>`, Learn's order) gives six rows instead of 27; a diagnostic still checks at Objective level (`VDW-v0` §4), so a coarse row never waives a coarse unit.
- **One diagnostic over the ticked areas' startable Skills.** `RequestDiagnostic` already takes many Skills in one request and skips what is shown or waived; a Skill waiting on a prerequisite is held by 13F's rules, not refused.
- **A used store is not asked.** A store with a plan has been opened by a learner (16E: a plan is written only when the learner opens the app), so a store from before 18A or a restored backup with work in it skips the setup.
- **The setup plans nothing.** Today is planned after it, so the learner's length and choices are recorded before the planner reads them.

## 3. The run report's "before" (user decision)

- 13E (`PCRX-v0`): a state no engine had written is not a "before", so a new learner's first run reported nothing. 16B (`LHRX-v0`, D-123) replays the engines from no evidence, so the history and 17E's day summary named the change (a question about a difficulty) — seen on the emulator in 17E.
- The user chose that both say the change. The run's report now fills only the unwritten axes of the touched Skills with the replay over exactly the evidence its snapshot saw; a written axis is never replaced, and the engine's own rule is unchanged.

## 4. External

No web research was needed: every rule comes from the accepted contracts above. Accessibility follows 17D's rules and audit (checkbox rows with their role and state, the field's error as its own supporting text, 48dp).
