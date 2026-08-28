# Progress / Skill / Weakness UX Specification — SPWX-v0

**Stage step:** 8E — Skill/progress/weakness UX  
**Status:** ACCEPTED — independent 8E QA PASS  
**Decision:** `D-072`  
**Model:** `SPWX-v0 — Progress, Skill State & Weakness UX`  
**Parent IA:** `UXIA-v0 / D-068`

## 1. Purpose

8E defines **how demonstrated capability, attention needs and learning history are labelled, grouped and presented** across the Progress information domain.

It answers one primary question:

> **Gerçekte neyi kanıtladım ve neye dikkat etmem gerekiyor?**

Primary invariant:

> **Progress is a projection of canonical evidence state. It never becomes a mastery engine, a score, a percentage of competence, a career-completion tracker or a streak dashboard.**

---

## 2. Binding inputs

8E consumes and preserves:

- `UXIA-v0 / D-068` — `progress` owns evidence-backed capability summaries, weakness/review/verification/remediation views, history, assessment reports and the Technical English profile. `skill_detail` and `topic_detail` are shared surfaces; the same Skill must not carry contradictory Learn and Progress truths. Browse hierarchy never substitutes prerequisite truth. Career/streak/time/course-completion percentage is rejected as the main progress concept.
- `THUX-v0 / D-069` — Today shows bounded attention summaries; exact state semantics live here.
- `TRUX-v0 / D-070` — task completion is not mastery; `requires_independent_recheck` and provenance semantics originate in the runner.
- `ASUX-v0 / D-071` — the in-session result view answers "what just changed"; `assessment_report` is the longitudinal surface and is handed to this step. Its semantic families are inherited.
- `GRE-v0` — the internal mastery value is an engineering heuristic, is not `P(L)` or an IRT ability, and is **not presentable as a percentage**. Discrete states are the sanctioned presentation.
- `RVR-v0` — retention states `fresh | stable | review_due | verification_due | at_risk`; `review_due` does not mean forgotten and `at_risk` never arises from overdue duration alone.
- `PRG-v0` — readiness states `ready | ready_due | uncertain | not_ready`.
- `TSM-v0` (`docs/TOPIC_STATE_MACHINE.md`) — six canonical Topic states, with user-facing wording explicitly delegated to 8E and internal identities required to stay fixed.
- `WLRM-v0` — weakness lifecycle `none → hypothesis → supported → confirmed → resolved`, Objective-localized, with closure requiring fresh H0 direct verified prerequisite-valid evidence.
- `TEPM-v0` — exactly 8 derived Skill presentation states with a declared precedence, qualified A1/A2/B1 Technical English base profile, bounded B2+ per-capability extension, no general/official CEFR claim and no numeric aggregate.
- `PDT-v0` — planner explanations remain trace-derived and externally owned.
- V1 local-first Android scope and the AI-degraded deterministic-core requirement.

8E introduces no new mastery threshold, retention interval, prerequisite edge, weakness rule, assessment policy or numeric scale.

---

# 3. Scope boundary

## 3.1 8E decides

- Progress overview semantics and grouping,
- the single Skill presentation-state vocabulary and its precedence,
- multi-axis state presentation rules,
- Topic user-facing state labels,
- `skill_detail` and `topic_detail` content contracts,
- weakness and remediation presentation semantics,
- the Technical English profile surface presentation,
- `learning_history` semantics,
- longitudinal `assessment_report` semantics,
- what may and may not be counted or aggregated,
- Progress-domain semantic states including empty and degraded,
- Progress accessibility baseline,
- explicit handoff boundaries to 8F–8G and implementation stages.

## 3.2 8E does not decide

- mastery, retention, prerequisite, weakness or assessment algorithms → Stage 2/3/4/6, unchanged,
- assessment session execution → 8D,
- typography, color, spacing, iconography, motion and component library → 8F,
- final wireframes and prototype geometry → 8G,
- navigation framework and UI technology → 9A/10,
- physical persistence schema → 9C,
- analytics and settings detail → 16,
- final microcopy polish and accessibility calibration → 17–18,
- empirical threshold calibration → 18C.

No mastery percentage, competence ratio, career bar, streak calendar or fixed geometry is canonical in 8E.

---

# 4. One Skill presentation vocabulary

`TEPM-v0` already defined 8 derived presentation states for the D01 English Skills. Those are ordinary Skills in the same registry. Defining a second vocabulary for technical Skills would give one registry two contradictory label systems.

Binding rule:

> **The `TEPM-v0` presentation states and precedence are generalized to every Skill. `TEPM-v0` is unchanged and becomes the English instance of one general rule.**

## 4.1 The eight states

```text
not_yet_evidenced
developing_with_support
developing_independent
confirmed_current
confirmed_review_due
confirmation_verification_due
remediation_required
prerequisite_unresolved
```

There are exactly eight. No ninth primary state may be added by any surface.

## 4.2 Precedence

Identical to `TEPM-v0`:

```text
1. remediation_required
2. confirmation_verification_due
3. confirmed_review_due
4. confirmed_current
5. prerequisite_unresolved
6. developing_independent
7. developing_with_support
8. not_yet_evidenced
```

The projection is deterministic. The same canonical state must always produce the same presentation state.

## 4.3 Turkish labels

| Internal state | UI label |
|---|---|
| `not_yet_evidenced` | Henüz Kanıt Yok |
| `developing_with_support` | Destekle Gelişiyor |
| `developing_independent` | Bağımsız Gelişiyor |
| `confirmed_current` | Doğrulanmış |
| `confirmed_review_due` | Doğrulanmış · Tekrar Zamanı |
| `confirmation_verification_due` | Doğrulanmış · Yeniden Kontrol Gerekli |
| `remediation_required` | Pekiştirme Gerekli |
| `prerequisite_unresolved` | Ön Koşul Bekliyor |

Internal identities are fixed. Later microcopy work may refine wording only if the semantics above are preserved exactly.

---

# 5. Multi-axis state is ordered, not collapsed

A Skill simultaneously carries a mastery axis, a retention axis, a prerequisite-readiness axis and possibly a weakness axis.

Binding rule:

> **The primary presentation state orders these axes; it does not replace them. `skill_detail` must keep each contributing axis individually inspectable.**

```text
SkillStateProjection
- primary_presentation_state
- mastery_axis_state
- retention_axis_state
- prerequisite_axis_state
- weakness_axis_state
- attention_qualifiers[]
- contributing_evidence_summary_ref
```

A surface may show only the primary state in a list. It may not claim the primary state is the whole truth, and it may not discard an axis that contradicts the headline.

## 5.1 `at_risk` is a qualifier, not a state

`RVR-v0` defines `at_risk`. `TEPM-v0` fixes English Skills at exactly eight presentation states.

Resolution:

```text
at_risk -> attention_qualifier attached to the primary state
at_risk -> never a ninth primary presentation state
at_risk -> never silently dropped
```

`at_risk` is displayed as a retention-attention qualifier alongside the primary label, and is explained as mixed or repeated retention signal — never as overdue time alone.

---

# 6. Topic state labels

`TSM-v0` delegates user-facing wording to 8E and requires internal identities to stay fixed.

| Internal state | UI label |
|---|---|
| `locked` | Kilitli |
| `available` | Hazır |
| `learning` | Öğreniliyor |
| `mastered` | Öğrenildi |
| `weakening` | Tekrar Gerekebilir |
| `remediation_required` | Pekiştirme Gerekli |

`weakening` is deliberately worded as an opportunity rather than as loss. The product's canonical position is that review need is not forgetting, and the label must not imply decay the state does not assert.

Binding rules:

- Topic state is **derived orchestration**, never a prerequisite claim.
- Topic state is **never an average** of Skill values, and no Topic percentage exists.
- `locked` reflects orchestration availability, not a permanent verdict, and its reason must be reachable.
- Topic placement in the browse hierarchy never implies a hard prerequisite.

---

# 7. `progress_overview`

Answers: *what have I demonstrated, and what needs attention?*

Two semantic halves:

```text
demonstrated_capability_inventory
attention_set
```

## 7.1 Demonstrated capability inventory

An inventory of what has actually been evidenced, grouped by curriculum organization for orientation.

Permitted: counts of Skills in each presentation state, presented explicitly as inventory.

Forbidden: any ratio, percentage, score, gauge, level, rank or bar presented as competence — including `confirmed / total`.

## 7.2 Attention set

Grouped by the reason attention exists:

```text
remediation_open
verification_due
review_due
retention_at_risk
prerequisite_blocked
assessment_opportunity
```

Attention is a set of real current needs, not a backlog, a debt list or a to-do count. It creates no planner priority; the planner remains the owner of what happens next.

## 7.3 What Progress overview must not become

- a career-completion or readiness percentage,
- a broad domain score such as `Python 72%`,
- a mastery gauge, level or rank,
- a streak or attendance dashboard,
- a second planner offering "start this now" as its primary purpose,
- an assessment gradebook.

---

# 8. `skill_detail`

The single canonical Skill surface, shared by Today, Learn and Progress.

Content contract:

```text
SkillDetailView
- skill_ref
- display_name
- primary_presentation_state
- attention_qualifiers[]
- mastery_axis_state
- retention_axis_state
- prerequisite_axis_state
- weakness_axis_state
- objective_level_breakdown[]
- evidence_summary
- evidence_history_entry
- prerequisite_context
- remediation_context?
- related_topic_placements[]
- planner_explanation_entry
- integration_or_english_context?
```

Binding rules:

- Objective-level detail is available; Skill state never hides an Objective-level gap.
- Evidence summary distinguishes independent, assisted, provisional and invalid evidence rather than merging them into one count.
- Prerequisite context explains blocking honestly and links to the blocking Skill.
- The same Skill opened from Learn and from Progress shows the same state and the same evidence.
- No mastery percentage, confidence number or internal heuristic value is exposed.

---

# 9. `topic_detail`

Shows derived Topic orchestration state and its Skill structure.

Binding rules:

- it presents Topic state and the Skills placed under it,
- it creates no mastery, prerequisite or evidence truth,
- Skill entries link to the shared `skill_detail`,
- structural placement is not rendered as a dependency chain,
- no Topic completion percentage exists.

---

# 10. Weakness and remediation presentation

## 10.1 Only supported and confirmed weakness is shown as weakness

`WLRM-v0` lifecycle: `none → hypothesis → supported → confirmed → resolved`.

```text
hypothesis  -> may appear at most as an open question, never as a deficiency
supported   -> may be shown as a localized weakness signal
confirmed   -> shown as a localized confirmed weakness
resolved    -> shown as history, not as a current problem
```

An AI-proposed hypothesis must never be rendered as a confirmed weakness or as a statement about the user.

## 10.2 Localization is preserved

Weakness is presented at the Objective/Skill level where it was localized. A weakness never broadcasts upward into "you are weak at Python" or downward into unrelated dependent Skills.

## 10.3 Remediation closure is evidence, not task completion

```text
remediation_task_completed != remediation_closed
```

Remediation is shown as closed only when canonical closure occurred: fresh, context-diverse, H0, direct, verified, prerequisite-valid evidence through the GRE/RVR pipeline. Until then it remains open, and the surface says so plainly.

## 10.4 Non-punitive framing

Weakness presentation describes a located gap and its route forward. It does not describe the user, does not rank the user, and does not use failure language.

---

# 11. `technical_english_profile`

Presents `TEPM-v0` semantics unchanged:

- qualified A1/A2/B1 Technical English base profile,
- first-class uneven per-Skill detail rather than a single flattened level,
- bounded B2+ per-capability extension evidence on exactly the TECP-v0 extension-eligible Skills,
- historical confirmed-band provenance retained.

Forbidden:

- a general or official CEFR claim,
- a certification claim,
- a numeric English aggregate, average or percentage,
- an aggregate `B2+ complete` claim,
- presenting English as a gate on the technical route.

English Skills use the same eight presentation states as every other Skill; this surface adds the qualified band framing, not a second state model.

---

# 12. `learning_history`

A chronology of meaningful learning, assessment, review and remediation events.

Binding rules:

- it records what happened and what it changed,
- it is **not** a streak calendar, contribution graph or attendance heatmap,
- attendance is never rendered as achievement,
- consecutive-day counts are not shown as a success metric,
- a gap in the timeline is not marked as failure or missed obligation,
- entries link to the Skill, Topic or assessment they concern.

---

# 13. `assessment_report`

The longitudinal assessment surface handed over by `ASUX-v0`.

It inherits the semantic families:

```text
confirmed_capabilities
verification_needed
persistent_targeted_gaps
retention_revalidated
not_reliably_measured
plan_changes
```

Binding rules:

- it reports across sessions, where the in-session result view reported one moment,
- it owns no mastery,
- it may not aggregate past sessions into a score, average, grade or trend line presented as competence,
- `not_reliably_measured` remains first-class and is never folded into failure,
- provisional results remain labelled provisional,
- a session and this report must never present contradictory truths about the same attempt.

---

# 14. Counting rules

Progress may count. It may not score.

```text
allowed:    "8 Skill doğrulanmış"           # inventory
allowed:    "3 Objective pekiştirme bekliyor"
forbidden:  "%64 tamamlandı"
forbidden:  "Python 72%"
forbidden:  "Seviye 4"
forbidden:  "confirmed / total"
```

Any count must be labelled as an inventory of current state. A count is never divided by a total to imply competence, and never rendered as a progress bar toward mastery or a career.

---

# 15. Review and verification framing

- `review_due` is a scheduled opportunity, presented neutrally. It does not mean forgotten, does not demote a confirmed state and is never styled as decay or error.
- `verification_due` states honest current uncertainty. It does not delete confirmed history, is not a demotion event, and explains that a fresh independent check is needed.
- `at_risk` is explained as mixed or repeated retention signal, never as elapsed time.
- No visual treatment may assert a severity the canonical state does not assert.

---

# 16. Degraded and recovery behavior

- **Offline** — locally derived state remains fully viewable; anything requiring remote data is labelled unavailable rather than shown as zero or empty.
- **AI unavailable** — Progress is fully usable; AI is not required to render any state. Unevaluated attempts appear as pending and contribute no state.
- **Recomputation in progress** — a stale projection is never presented as current; the surface says it is recomputing.
- **Data recovery required** — supersedes normal presentation; silent progress reset is forbidden.

---

# 17. Progress domain semantic states

```text
loading_projection
recomputing_projection
ready_with_evidence
empty_no_evidence_yet
empty_no_attention_needed
partial_projection_available
offline_local_capable
ai_unavailable_full_state_available
error_recoverable
data_recovery_required
```

`empty_no_evidence_yet` is a legitimate starting state and is never framed as failure. `empty_no_attention_needed` never implies everything is mastered or that the user is professionally ready.

Each state must be distinguishable in text, not by color or motion alone.

---

# 18. Accessibility baseline

- Every presentation state has a textual accessible name; state is never conveyed by color alone.
- Attention qualifiers are conveyed in text alongside the primary state.
- Inventory counts are readable as text, not only inside a chart.
- Objective-level breakdown is reachable by linear traversal.
- History entries are readable as a list, not only as a graphic timeline.
- `review_due` and `verification_due` are distinguishable without relying on hue.

---

# 19. Anti-patterns explicitly rejected

The Progress domain must not become:

- a mastery percentage or competence ratio,
- a career-completion or readiness bar,
- a broad domain score,
- a level, rank, tier or badge economy,
- a streak calendar, contribution graph or attendance heatmap,
- an assessment gradebook or score trend line,
- a second planner,
- a place where one badge replaces contradicting state axes,
- a place where an AI hypothesis is shown as confirmed weakness,
- a place where completing a remediation task reads as the weakness being fixed,
- a place where `review_due` is styled as decay or failure,
- a place where a Topic label implies a hard prerequisite,
- a general or official CEFR claim,
- a duplicate Skill truth that contradicts `Learn`.

---

# 20. 8E acceptance contract

8E can be accepted only if independent QA verifies at minimum:

1. Progress remains a projection of canonical evidence state and owns no engine truth.
2. Exactly eight Skill presentation states exist, generalized from `TEPM-v0`, with its precedence unchanged.
3. `TEPM-v0`'s accepted English contract is preserved without modification.
4. `at_risk` is a qualifier, is never a ninth primary state, and is never dropped.
5. The primary presentation state orders the axes and never replaces them; `skill_detail` keeps each axis inspectable.
6. The six Topic UI labels are locked and internal Topic state identities are unchanged.
7. Topic state is derived orchestration, is never a prerequisite claim and has no percentage.
8. Progress overview has a demonstrated-capability inventory and an attention set, and creates no planner priority.
9. No mastery percentage, competence ratio, career bar, domain score, level or rank exists anywhere.
10. Counts are permitted only as labelled inventory and are never divided by a total to imply competence.
11. `review_due` is neutral and non-demoting; `verification_due` states uncertainty without deleting history.
12. Only `supported` and `confirmed` weakness is shown as weakness; a hypothesis is never a deficiency.
13. Weakness localization is preserved with no upward or downward broadcast.
14. Remediation is shown closed only on canonical closure evidence, never on task completion.
15. `learning_history` is not a streak calendar and attendance is never achievement.
16. `assessment_report` is longitudinal, owns no mastery, and does not aggregate sessions into a score or competence trend.
17. `not_reliably_measured` remains first-class and provisional stays labelled.
18. The same Skill shows the same truth from Learn and from Progress.
19. Empty states are legitimate and never imply mastery or readiness.
20. Degraded modes never present a stale projection as current.
21. 8F/8G/9/10 implementation boundaries remain open.
22. Stage 2/3/4, Stage 6, Stage 7, 8A, 8B, 8C and 8D accepted contracts still validate.

---

# 21. Handoff after acceptance

If accepted, 8E becomes `SPWX-v0 / D-072`.

Next numbered step:

**8F — Tasarım sistemi**

8F will define the visual design system — typography, color, spacing, iconography, motion and the component library — implementing the semantic labels and states locked in 8A–8E consistently, without altering any IA truth ownership or state semantics. It must receive a fresh PRE-STEP and explicit user approval before execution.
