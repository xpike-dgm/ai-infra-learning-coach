# 8D Assessment Session UX — Research & Contract Synthesis

**Stage step:** 8D — Sınav UX  
**Purpose:** Turn the accepted assessment evidence contracts into one assessment session interior and one truthful result surface, without creating a gradebook, a second mastery authority or three divergent exam screens.

## 1. Research-need decision

8D introduces no new assessment algorithm, no new blueprint policy, no new scoring rule, no new item-validation standard and no platform technology choice. Stage 4 already decided what gets measured, when it may be measured, what makes an item trustworthy and how evidence is interpreted. 8D decides only how that execution is presented and navigated:

```text
UXIA-v0 assessment_session_flow + assessment_report ownership
+ TRUX-v0 shared focused-flow frame
+ DMA-v0 / WBA-v0 / MCA-v0 evidence, assistance, validity and result contracts
+ 2D assistance/evidence classes
+ GRE-v0 / RVR-v0 / PRG-v0 / PBR-v0 / PDT-v0 state ownership
→ assessment session interior and result presentation
```

A new separate external Research AI is **not required** for acceptance. Independent QA remains required, because an exam screen is the single place where a learning product most naturally drifts into a gradebook and starts treating a score as a capability verdict.

## 2. Canonical source set reviewed

### IA and focused-flow frame
- `docs/INFORMATION_ARCHITECTURE_SPEC.md`, `ux/8a_information_architecture/ia.yaml`
- `docs/DAILY_WORKING_FLOW_SPEC.md`, `ux/8c_daily_working_flow/flow.yaml`

Key constraints: `assessment_session_flow` is a focused flow for daily/weekly/monthly assessment execution; `assessment_report` is a Progress-owned historical surface that "cannot behave as an independent score-based mastery authority". 8C defined one shared focused-flow frame — entry revalidation, safe pause/exit, resume revalidation, capacity/replan interaction, degraded/recovery behavior, deterministic return — that 8D inherits rather than redefines. `task_runner_flow`, not `assessment_session_flow`, owns learning, practice, remediation, retention **and diagnostic** work.

### Assessment scopes
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` (DMA-v0)
- `docs/WEEKLY_ASSESSMENT_SPEC.md` (WBA-v0)
- `docs/MONTHLY_ASSESSMENT_SPEC.md` (MCA-v0)

Key constraints: all three share the same evidence validity, assistance, prerequisite and item-trust rules. All three reject `score → mastered` rules. All three allow block-wise split at safe checkpoints, forbid cutting an atomic evidence boundary, treat pause as neither failure nor assistance, and treat unsubmitted items as not-incorrect. They differ in blueprint composition and target pool, not in interaction.

### Item trust and evaluation
- `docs/QUESTION_BANK_SPEC.md` (QAB-v0), `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md` (AIV-v0)
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`

Key constraints: evaluator status is `verified | provisional | invalid`. A provisional evaluator may give feedback and open a confirmation need but cannot alone decide a critical mastery transition. Invalid items produce neither credit nor penalty. Unvalidated AI-generated items cannot produce strong mastery-changing evidence. Requesting help is never negative evidence; H1/H2 downgrades an independence claim, H3/H4 makes the attempt practice-only and requires a fresh unseen recheck.

### State ownership
- `docs/MASTERY_SIGNALS_SPEC.md`, `docs/MASTERY_FORMULA_V0.md`, `docs/RETENTION_FORGETTING_SPEC.md`, `docs/PREREQUISITE_POLICY_SPEC.md`, `docs/PLANNER_EXPLAINABILITY_SPEC.md`

Key constraints: GRE-v0 owns mastery, RVR-v0 retention, PRG-v0 prerequisite readiness, PBR-v0 priority, PDT-v0 user-facing reasons. A first clean contradiction on an already-mastered Skill opens `verification_due`, not instant unmastery.

## 3. Synthesis problems 8D actually has to solve

1. **Three scopes, one interior.** Daily, weekly and monthly assessments share every interaction-relevant rule. Designing three exam screens would create three places for the gradebook to creep back in. The scope must be context metadata over one interior.
2. **Where the session may be cut.** Blocks may be paused; an atomic evidence boundary may not be cut. The interior therefore needs an explicit unit of submission that is the evidence boundary itself, not "a page".
3. **Navigation after submission.** 8C froze the attempt at submission. An exam interior that lets a user revisit and change a submitted answer would silently reopen a frozen attempt. But navigation *within* an unsubmitted boundary is legitimate and expected.
4. **Showing results without grading.** The user genuinely wants to know how it went. The honest answer is not a percentage — it is which capabilities were confirmed, which need verification, which gaps persist and which things could not be measured reliably. Raw correct-counts may inform but must never be the verdict.
5. **Making "not reliably measured" first-class.** Invalid items, provisional evaluations, assisted attempts and unfinished blocks all produce non-verdict outcomes. If the UI hides them, the user reads silence as failure; if it counts them as wrong, the system lies.
6. **Letting the user contest an item without letting them delete evidence.** A learner who hit an ambiguous prompt must be able to say so cheaply. But a report is not proof, and self-reporting must not become an undo button for inconvenient results.
7. **Assistance inside a measurement.** Blocking help would punish curiosity and corrupt the product's teaching-first stance. Allowing it silently would let assisted attempts masquerade as independent evidence. The conversion must be permitted, visible and non-punitive.

## 4. Positions taken

- One assessment session interior serves all three scopes; scope is displayed context, never a different screen family or a different rule set.
- The submission unit is the declared atomic evidence boundary — an item or a testlet — and it is never split.
- Submitted boundaries are frozen and cannot be revisited or edited; unsubmitted boundaries within an open block remain freely navigable.
- The result surface is semantic. It reports confirmed capabilities, verification needs, persistent gaps, revalidated retention, what could not be reliably measured, and what the plan did in response. There is no pass/fail banner and no grade.
- `not_reliably_measured` is a visible first-class outcome, never rounded down into failure.
- Provisional evaluation is labelled provisional wherever it appears and cannot be presented as a settled verdict.
- Item disputes are cheap and non-punitive; they hold the affected evidence as contested and trigger revalidation rather than auto-invalidating it.
- Assistance is available during assessment; the evidence consequence is disclosed in measurement language and the session may continue in learning mode.
- Diagnostics remain `task_runner_flow` work; 8D does not claim them.
- The in-session result view answers "what just changed"; long-term history and cross-session reporting stay in the Progress-owned `assessment_report`.

## 5. Explicitly not decided in 8D

Blueprint composition and target-pool policy (already Stage 4; unchanged here), question-bank schema and authoring (4D/15), item validation pipeline internals (4E/14), Skill/progress/weakness labels and visualisation (8E), typography/color/spacing/motion/components (8F), wireframe geometry (8G), navigation framework and UI technology (9A/10), physical persistence schema (9C), evaluator and code-runner integration (9E/13/14), notification design (16), empirical usability and accessibility calibration (17–18).

No external source in this synthesis justifies a passing threshold, a percentage grade, a countdown clock, a leaderboard, a per-exam streak or a fixed question count.
