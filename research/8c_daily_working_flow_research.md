# 8C Daily Working Flow / Task Runner — Research & Contract Synthesis

**Stage step:** 8C — Günlük çalışma akışı  
**Purpose:** Turn accepted planner / evidence / assistance / IA / Home contracts into a focused daily working-flow choreography without creating a second planner, a second mastery authority or a second evidence pipeline.

## 1. Research-need decision

8C introduces no new learning-science claim, no new threshold, no new scheduling algorithm and no platform technology choice. Its problem is interaction choreography over already-accepted semantics:

```text
UXIA-v0 task_runner_flow ownership
+ THUX-v0 primary action / resume / return contract
+ 3A capacity + 3B task taxonomy + 3F re-entry + 3G explainability
+ 2D assistance/evidence contract
+ TEIP-v0 integration modes
→ focused daily working-flow choreography
```

A new separate external Research AI is **not required** for acceptance. The external UX provenance already accepted in 8A (action-first orientation, progressive disclosure, truthful state) remains valid and is not reinterpreted here to justify fixed screens, step counts, timers or motion.

Independent QA is still required, because a task runner is the single place where the product is most likely to accidentally manufacture false mastery: it touches attempts, assistance, artifacts and completion at the same moment.

## 2. Canonical source set reviewed

### IA and Home
- `docs/INFORMATION_ARCHITECTURE_SPEC.md`, `ux/8a_information_architecture/ia.yaml`
- `docs/TODAY_HOME_SCREEN_SPEC.md`, `ux/8b_today_home/home.yaml`

Key constraints: `task_runner_flow` is a focused flow entered mainly from Today or an entity context; the shell may be suppressed but a safe pause/exit/resume path must survive; the return target must be deterministic; `assessment_session_flow` is a separate focused flow owned by 8D.

### Planner and capacity
- `docs/ADAPTIVE_PLANNER_SPEC.md`
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/PLANNER_EXPLAINABILITY_SPEC.md`
- `docs/MISSED_DAY_RECOVERY_SPEC.md`

Key constraints: the canonical pipeline is `LearningNeed → TaskCandidate → PlannedTask → Attempt/Artifact → EvidenceEvent → state update → replan`. Lifecycle states are not evidence. `paused_progress` is distinct from `deferred_candidate` and carries a `ResumeContext`. Replan preserves completed evidence and re-resolves only unstarted work. Stopping early is explicitly no-penalty.

### Assistance and evidence
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/MASTERY_SIGNALS_SPEC.md`, `docs/MASTERY_FORMULA_V0.md`
- `docs/RETENTION_FORGETTING_SPEC.md`
- `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`

Key constraints: assistance is classified H0–H4 by content, separately from artifact origin and separately from timing. Post-submit explanation does not retroactively contaminate a finished attempt. Solution exposure requires a fresh unseen variant for independent recheck, never the same item. Asking for help is not negative evidence, and suspicion is not evidence of cheating.

### Technical English
- `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`
- `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`

Key constraints: exactly four integration modes; scaffold modes are semantic support, not levels; dual-target tasks must keep component-level results separable; no overall pass broadcast.

## 3. Synthesis problems 8C actually has to solve

1. **What a "daily session" is.** The product promises a daily working flow, but nothing in the accepted contracts defines a fixed daily container. A session must be an emergent sequence of task runs, not a graded unit with a required length or task count.
2. **Continuity without a second planner.** A user finishing one task expects to keep going. If the runner caches "the rest of today's list" and advances through it locally, it becomes a second planner and defeats replan. The next task must be the planner's recomputed current selection after post-attempt processing.
3. **Where the assessment interior lives.** Both `task_runner_flow` and `assessment_session_flow` need entry revalidation, pause, safe exit, return and degraded behavior. Duplicating that in 8C and 8D would create two contradictory truths. The resolution is a shared focused-flow frame owned by 8C, with the assessment interior owned by 8D.
4. **Assistance without ambush.** Evidence class changes when help reveals the solution. If the user only learns this afterwards, the system punishes by surprise and teaches the user to avoid help — the opposite of the product goal. Consequence must be disclosed before escalation, and framed as measurement, not penalty.
5. **Provenance without policing.** The system cannot reliably detect pasted or externally generated code. If honest disclosure is expensive or punitive, the user is incentivised to lie and evidence quality collapses. Provenance capture must be cheap, honest-by-default and non-punitive.
6. **Pause that is not failure.** A checkpoint pause must preserve real progress, but a stale high-stakes attempt must not silently continue as independent evidence after a long gap.
7. **Degradation without fake evidence.** When the AI evaluator is unavailable, an open-ended attempt can be neither auto-passed nor auto-failed. It must park as unevaluated and write no evidence.

## 4. Positions taken

- The runner is an execution surface. It produces `Attempt`, `Artifact`, assistance metadata and provenance; it interprets none of them.
- A working session is emergent and ungraded. `plan_exhausted` is not "day succeeded"; `user_stopped` is not "day failed".
- Continuity is allowed, but the next task is always the planner's recomputed current selection.
- 8C owns the shared focused-flow frame for both focused flows and the full interior only for `task_runner_flow`.
- Assistance escalates H1 → H2 → H3 → H4 on request; the runner never auto-reveals a solution on first error, and discloses evidence consequence before H3/H4.
- After solution exposure the same item may be repeated for learning only, never presented as a mastery path; the runner raises `requires_independent_recheck` and the planner owns scheduling.
- Provenance is asked, not inferred. Honest disclosure never produces a penalty framing.
- An in-flight run survives replan; only unstarted work is re-resolved.
- Unevaluated is a real, visible, truthful state.

## 5. Explicitly not decided in 8C

Assessment question/session/result interior (8D), Skill/progress/weakness labels and visualisation (8E), typography/color/spacing/motion/components (8F), wireframe geometry (8G), navigation framework and UI technology (9A/10), physical persistence schema (9C), code runner / compiler / provider integration (9E, 14D), AI tutor prompt behavior (14), notification design (16), empirical usability and accessibility calibration (17–18).

No external source in this synthesis justifies a fixed step count, a fixed screen sequence, a countdown timer, a required daily task quota or a motivational streak device.
