# 8E Progress / Skill / Weakness UX — Research & Contract Synthesis

**Stage step:** 8E — Skill/progress/weakness UX  
**Purpose:** Give the Progress information domain one honest, non-numeric presentation vocabulary over five separate canonical state axes, without inventing a second mastery model.

## 1. Research-need decision

8E introduces no new mastery formula, retention interval, prerequisite rule, weakness lifecycle or assessment policy. Every state it presents already exists and is already owned. Its problem is projection and wording:

```text
UXIA-v0 progress / skill_detail / topic_detail /
        technical_english_profile / learning_history / assessment_report ownership
+ GRE-v0 mastery + RVR-v0 retention + PRG-v0 readiness
+ TSM-v0 Topic orchestration + WLRM-v0 weakness lifecycle
+ TEPM-v0 derived English presentation states
+ ASUX-v0 result families handed off from 8D
→ Progress domain labels, grouping and visual state semantics
```

A new separate external Research AI is **not required** for acceptance. Independent QA remains required, because Progress is the single place where a learning product most naturally reinvents the percentage — a career bar, a domain score, a mastery gauge — and thereby contradicts the product's founding invariant.

`docs/TOPIC_STATE_MACHINE.md` §2 explicitly delegates the user-facing Topic wording to this step while requiring the internal state identities to stay fixed. 8E therefore has a direct mandate to lock labels.

## 2. Canonical source set reviewed

### IA and upstream UX
- `docs/INFORMATION_ARCHITECTURE_SPEC.md`, `ux/8a_information_architecture/ia.yaml`
- `docs/TODAY_HOME_SCREEN_SPEC.md`, `docs/DAILY_WORKING_FLOW_SPEC.md`, `docs/ASSESSMENT_SESSION_UX_SPEC.md`

Key constraints: `progress` answers "What have I actually demonstrated and what needs attention?" and owns evidence-backed capability summaries, weakness/review/verification/remediation views, history, assessment reports and the Technical English profile. `skill_detail` is the single shared Skill surface — the same Skill must not have contradictory "Learn" and "Progress" truths. Browse hierarchy never substitutes prerequisite truth. Career/streak/time/course-completion percentage is explicitly rejected as the main progress concept. 8D handed `assessment_report` to this step.

### State owners
- `docs/MASTERY_SIGNALS_SPEC.md`, `docs/MASTERY_FORMULA_V0.md` — GRE-v0. The internal mastery value is an engineering heuristic, is not `P(L)` or an IRT ability, and **must not be presented as a mastery percentage**; discrete states are the sanctioned presentation.
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0: `fresh`, `stable`, `review_due`, `verification_due`, `at_risk`. `review_due` explicitly does not mean forgotten; `at_risk` never arises from overdue duration alone.
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0: `ready`, `ready_due`, `uncertain`, `not_ready`, projecting to eligible / conditional / blocked / eligible-with-support.
- `docs/TOPIC_STATE_MACHINE.md` — TSM-v0: `locked`, `available`, `learning`, `mastered`, `weakening`, `remediation_required`.
- `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` — WLRM-v0: `none → hypothesis → supported → confirmed → resolved`, Objective-localized, with remediation closure requiring fresh H0 direct verified evidence rather than task completion.
- `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`, `curriculum/english/7e_mastery_profile/policy.yaml` — TEPM-v0: exactly 8 derived Skill presentation states with a declared precedence, qualified A1/A2/B1 base profile, bounded B2+ per-capability extension, and no general/official CEFR or numeric aggregate.

## 3. Synthesis problems 8E actually has to solve

1. **Five axes, one label.** A Skill simultaneously carries a mastery state, a retention state, a prerequisite readiness state and possibly a weakness state. One badge loses truth; four badges are noise. The projection needs a declared precedence *and* a rule that the primary label orders the axes rather than replacing them.
2. **Avoiding a second vocabulary.** 7E already defined 8 derived presentation states for English Skills. English Skills are ordinary Skills in D01. Inventing a separate technical vocabulary would give the same registry two contradictory label systems.
3. **Where `at_risk` goes.** RVR-v0 has `at_risk`; TEPM-v0's accepted contract fixes English Skills at *exactly* 8 presentation states. Adding a ninth primary state would break an accepted contract; dropping `at_risk` would silently discard a real retention signal.
4. **Making `review_due` not read as failure.** The canonical rule is explicit that review due is not forgetting. Any visual treatment that renders it as decay, loss or a red warning contradicts the spec regardless of what the label says.
5. **Not reinventing the percentage.** The user genuinely wants a sense of scale. The honest answer is an inventory — how many capabilities are confirmed, what needs attention — not a ratio presented as competence.
6. **Weakness without accusation.** WLRM-v0's lifecycle starts at `hypothesis`, which may be AI-proposed. Rendering a hypothesis as a deficiency would let an unverified LLM guess become the user's self-image.
7. **History that is not a streak calendar.** Attendance is not achievement, and a contribution-graph aesthetic silently reintroduces the streak the product rejects.
8. **A longitudinal report that does not become a gradebook.** 8D forbade a per-session grade; aggregating those sessions over time is the obvious back door to the same thing.

## 4. Positions taken

- The 8 derived presentation states and precedence defined by TEPM-v0 are **generalized to every Skill**, technical and English alike. TEPM-v0 is unchanged and becomes the English instance of one general rule.
- `at_risk` is presented as a **secondary attention qualifier attached to a primary state**, never as a ninth primary state and never silently dropped.
- The primary presentation state is a deterministic precedence projection; mastery, retention, readiness and weakness axes remain individually inspectable in `skill_detail`. The label orders the axes; it does not replace them.
- The six Topic UI labels are locked here, with internal state identities untouched, per the explicit TSM-v0 delegation.
- Topic state is derived orchestration. It is never a prerequisite claim and never an average of Skill values.
- No mastery percentage, domain score, career percentage, numeric CEFR aggregate or streak appears anywhere in Progress. Counts are permitted only as inventory, explicitly labelled as inventory, never as a ratio of competence.
- `review_due` is presented as a scheduled opportunity, not decay; `verification_due` states honest current uncertainty without deleting confirmed history.
- Only `supported` and `confirmed` weakness states are shown as weakness; `hypothesis` may appear at most as an open question, never as a deficiency, and never sourced to an unverified AI judgement.
- Remediation is presented as closed only when canonical closure occurred — fresh, H0, direct, verified, prerequisite-valid evidence — never because a remediation task was completed.
- `learning_history` is a chronology of meaningful learning, assessment, review and remediation events. It is not a calendar heatmap and attendance is never rendered as achievement.
- `assessment_report` is longitudinal and inherits `ASUX-v0`'s semantic families. It owns no mastery and may not aggregate past sessions into a score.

## 5. Explicitly not decided in 8E

Mastery, retention, prerequisite and weakness algorithms (Stage 2/3/6, unchanged), assessment execution (8D), typography/color/spacing/iconography/motion/component library (8F), wireframe geometry (8G), navigation framework and UI technology (9A/10), physical persistence schema (9C), analytics and settings detail (16), final microcopy and accessibility calibration (17–18), and empirical threshold calibration (18C).

No external source in this synthesis justifies a mastery percentage, a competence ratio, a career-completion bar, a streak calendar, a leaderboard or a numeric English level.
