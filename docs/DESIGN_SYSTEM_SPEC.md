# Visual Design System Specification — VDSX-v0

**Stage step:** 8F — Tasarım sistemi  
**Status:** ACCEPTED — independent 8F QA PASS  
**Decision:** `D-073`  
**Model:** `VDSX-v0 — Visual Design System`  
**Parent IA:** `UXIA-v0 / D-068`

## 1. Purpose

8F defines the **visual and interaction expression layer**: typography, colour, spacing, iconography, motion and the component vocabulary that render the semantics locked in 8A–8E.

It answers one primary question:

> **Kabul edilmiş anlamı, ona hiçbir şey eklemeden nasıl gösteririz?**

Primary invariant:

> **The design system is an expression layer. It may never add meaning, severity, urgency, ranking or hierarchy that the canonical state does not assert.**

---

## 2. Binding inputs

8F consumes and preserves:

- `UXIA-v0 / D-068` — 4 destinations, 6 shared detail surfaces, 2 focused flows, 6 cross-cutting surface states. §17 assigns exact touch targets, contrast, screen-reader implementation and motion preferences to 8F/17.
- `THUX-v0 / D-069` — 5 Today content regions, 12 semantic states, semantic responsive hierarchy.
- `TRUX-v0 / D-070` — 6 lifecycle phases, 17 semantic states, 3 pause classes; countdown pressure forbidden.
- `ASUX-v0 / D-071` — 19 semantic states, 6 result families; no pass/fail banner, grade or countdown.
- `SPWX-v0 / D-072` — 8 Skill presentation states and 6 Topic states with locked Turkish labels, 10 semantic states, 4 attention qualifiers; no mastery percentage, competence ratio, career bar, level/rank or streak calendar; no visual severity beyond canonical state.
- `GRE-v0` / `RVR-v0` — task completion is not mastery; `review_due` is not forgetting and does not demote.
- `docs/V1_SCOPE.md` — Android-focused V1 requiring consistent typography/spacing, dark/light, loading/empty/error states, accessibility and appropriate motion.
- Named external standards: WCAG 2.2 **1.4.1**, **1.4.3**, **1.4.4**, **1.4.11**, **2.5.8**; Android/Material 48dp minimum touch target and the platform reduced-motion setting.

8F introduces no new destination, surface, state, label, behavior or truth ownership.

---

# 3. Scope boundary

## 3.1 8F decides

- the semantic tone vocabulary and its exhaustive mapping to every accepted state,
- typography roles, scale structure and Turkish-safe text rules,
- colour token roles, tone-to-token mapping and contrast requirements,
- theming rules for light and dark,
- spacing rhythm and touch-target rules,
- iconography rules,
- motion purpose classes, duration bands and forbidden motion,
- the component vocabulary and its ownership mapping,
- restricted component shapes,
- design-system accessibility rules,
- explicit handoff boundaries to 8G and implementation stages.

## 3.2 8F does not decide

- screen layouts, wireframes and prototype geometry → 8G,
- navigation framework and UI technology, including whether a specific component library is used at implementation time → 9A/10,
- physical persistence schema → 9C,
- analytics and notification design → 16,
- empirical accessibility and usability calibration, including final contrast and motion tuning → 17–18,
- production microcopy beyond the labels already locked in 8E,
- any change to accepted semantics, states, labels or truth ownership.

Concrete hex values are **not** locked here. 8F locks token roles, tone mappings and the contrast constraints they must satisfy; the concrete palette is produced during 8G/10 and must be measured against §7 per theme before shipping. A specification that hardcodes unmeasured colour values is weaker than one that fixes the constraint.

---

# 4. The severity rule

Colour, weight and motion are free variables. Meaning is not. The single most likely way this design system can break the product is by making a state look like something the canonical state does not assert.

Binding rules:

```text
visual_severity <= canonical_severity
```

1. Tone is assigned **from what a state means**, never from how alarming it feels.
2. Every accepted state has exactly one declared tone. No surface may choose a different one.
3. **No learning state may use the `system_fault` tone.** That tone is reserved for genuine technical faults.
4. Appearing in an attention group does **not** upgrade a state's tone. Grouping is organisation; tone is valence.
5. No visual treatment may imply loss, decay, failure, punishment or ranking where the state asserts none.

---

# 5. Semantic tone vocabulary

Exactly six tones:

| Tone | Meaning | Typical use |
|---|---|---|
| `neutral` | informational, no valence | loading, empty, paused, stopped, offline, most states |
| `active` | this is the live thing right now | current work, in-progress, submitting |
| `positive_confirmed` | canonical confirmation actually occurred | confirmed capability, mastered Topic |
| `attention` | an action or check is needed; **not** failure | verification due, remediation, blocked, at risk |
| `pending_unresolved` | truthfully incomplete; neither pass nor fail | evaluation pending, contested, partial |
| `system_fault` | genuine technical error or integrity risk | recoverable error, data recovery |

`positive_confirmed` is calm, not celebratory. `attention` reads as "there is something to do", never as "you did badly". `system_fault` is the only tone permitted to look alarming, and only two states may use it.

## 5.1 Tone assignment table — surface states

All 46 accepted surface states are mapped. Notable assignments and why:

- `stopped_no_penalty` → `neutral`. Stopping is explicitly not failure.
- `resume_invalidated` → `neutral`. Explicitly not negative evidence.
- `slot_recomposed` → `neutral`. Explicitly not a retry of a failure.
- `capacity_zero`, `capacity_too_small_no_candidate` → `neutral`. Capacity is context, not performance.
- `empty_no_evidence_yet` → `neutral`. A legitimate starting state.
- `blocked_not_startable` → `attention`. Something must be resolved, but nothing was done wrong.
- `evaluation_pending`, `item_contested`, `result_partial`, `partial_projection_available` → `pending_unresolved`.
- All offline and AI-unavailable states → `neutral`. Degradation is not a fault when the local core works.
- `error_recoverable`, `data_recovery_required` → `system_fault`. Only these two.

The complete machine-readable mapping is `ux/8f_design_system/design_system.yaml`.

## 5.2 Tone assignment — Skill presentation states

| Skill state | Tone |
|---|---|
| `not_yet_evidenced` | `neutral` |
| `developing_with_support` | `active` |
| `developing_independent` | `active` |
| `confirmed_current` | `positive_confirmed` |
| `confirmed_review_due` | `neutral` |
| `confirmation_verification_due` | `attention` |
| `remediation_required` | `attention` |
| `prerequisite_unresolved` | `neutral` |

`confirmed_review_due` is `neutral` deliberately. `RVR-v0` states that review due is not forgetting and does not demote a confirmed state. It appears in the Progress attention set for organisation, and per §4 rule 4 that grouping does not raise its tone.

## 5.3 Tone assignment — Topic states

| Topic state | Tone |
|---|---|
| `locked` | `neutral` |
| `available` | `neutral` |
| `learning` | `active` |
| `mastered` | `positive_confirmed` |
| `weakening` | `neutral` |
| `remediation_required` | `attention` |

`weakening` carries the locked label `Tekrar Gerekebilir`. Rendering it as decay would contradict the label the product already committed to.

## 5.4 Tone assignment — attention qualifiers

| Qualifier | Tone |
|---|---|
| `retention_at_risk` | `attention` |
| `requires_independent_recheck` | `attention` |
| `contested_evidence_present` | `pending_unresolved` |
| `provisional_evaluation_present` | `pending_unresolved` |

---

# 6. Typography

## 6.1 Roles

```text
display
title_large
title
body_large
body
label
caption
mono
```

`mono` exists for code, terminal output, identifiers and technical fragments, which are first-class content in this product.

## 6.2 Scale

The scale is a **declared product default**, expressed in scalable text units, not a scientific value. It is a calibration input for 17–18.

Rules:

- text uses scalable units and honours the user's system font size,
- content remains usable at **200% text size** (WCAG 1.4.4),
- no accepted state label may truncate before body content does; state is information, not decoration,
- line length and leading follow the rhythm in §8 rather than fixed pixel values.

## 6.3 Turkish-safe text rules

The locked labels are Turkish, and Turkish casing is a real correctness problem.

Binding rules:

- **No locale-naive case transforms.** `i` must uppercase to `İ` and `I` must lowercase to `ı`; any transform that cannot guarantee this is forbidden.
- Locked labels from `SPWX-v0` are rendered as authored; the design system may not restyle them into a different case.
- Chosen typefaces must fully support `ı İ ş Ş ğ Ğ ç Ç ö Ö ü Ü`, including in the `mono` role.
- No component specifies all-caps styling as a requirement.

---

# 7. Colour and contrast

## 7.1 Token roles

Colour is specified as **semantic roles**, never as raw values in product code:

```text
surface
surface_variant
on_surface
on_surface_muted
outline
tone_neutral / on_tone_neutral
tone_active / on_tone_active
tone_positive / on_tone_positive
tone_attention / on_tone_attention
tone_pending / on_tone_pending
tone_fault / on_tone_fault
focus_ring
```

Each tone in §5 maps to exactly one `tone_*` pair.

## 7.2 Contrast requirements

Anchored to WCAG 2.2:

- body and label text against its background: **≥ 4.5:1** (1.4.3),
- large text (≥18pt, or ≥14pt bold): **≥ 3:1** (1.4.3),
- state indicators, icons carrying meaning, focus rings and component boundaries: **≥ 3:1** (1.4.11),
- the focus indicator must be visible against every surface it can appear on.

Concrete values must be measured against these thresholds **per theme** before shipping. Dark theme is not an inversion of light; a pair that passes in one theme may fail in the other and must be verified independently.

## 7.3 Colour is never alone

Per WCAG 1.4.1 and the baselines already committed in 8A/8B/8E:

- every tone is accompanied by a text label,
- every tone is accompanied by a non-colour differentiator such as an icon or shape,
- `review_due` and `verification_due` must be distinguishable without relying on hue,
- no result meaning, Skill state or Topic state is carried by colour alone.

## 7.4 Theming

Light and dark are both first-class. Theme selection is a Profile preference per `UXIA-v0`. Tone identity, meaning and ordering are identical across themes; only the rendered values differ.

---

# 8. Spacing, rhythm and targets

- Spacing follows a **4dp base rhythm** with a declared step set of `4, 8, 12, 16, 24, 32, 48`. This is a product default, not a scientific value.
- Interactive targets are **at least 48dp**, adopting the stricter Android/Material rule over the WCAG 2.2 2.5.8 floor of 24×24.
- Exit and pause affordances in focused flows keep full target size even when the shell is visually suppressed.
- Related information is grouped by spacing and structure rather than by boxes alone.
- Fixed pixel geometry, fixed card counts and screen layouts remain 8G's.

---

# 9. Iconography

- Icons are **supportive**, never the sole carrier of meaning.
- Every icon that indicates state appears with its text label.
- One semantic state uses one icon metaphor consistently across every surface.
- Icons carrying meaning satisfy the 3:1 non-text contrast rule.
- No icon may encode a state that has no text label anywhere in the interface.
- Decorative icons are marked as decorative for assistive technology.

---

# 10. Motion

## 10.1 Motion has no persuasive role

Motion may orient, maintain continuity and confirm that something happened. It may not persuade, pressure or reward.

## 10.2 Purpose classes

```text
orientation   # where did this come from, where did it go
continuity    # this is the same object, moved or expanded
feedback      # your input was received
state_change  # canonical state actually changed
```

Duration bands are declared product defaults and calibration inputs for 17–18: micro feedback is brief, orientation and continuity are short, and nothing in normal use is long enough to delay the next action.

## 10.3 Forbidden motion

- countdown, timer or urgency animation of any kind,
- pulsing or attention-grabbing motion used to create pressure,
- **celebratory or reward animation on task completion**, because `task_completed != mastery_confirmed`,
- animation that implies decay, loss or falling for `review_due`, `weakening` or any learning state,
- streak, combo or score animation,
- motion that is the sole indication that state changed,
- motion that blocks the user from exiting a focused flow.

Celebration is permitted only when canonical state genuinely changed — and even then it stays proportionate and never overstates what the evidence supports.

## 10.4 Reduced motion

The platform reduced-motion setting is honoured. With motion reduced, every state change remains fully conveyed through text and layout, and no information is lost.

---

# 11. Component vocabulary

Components exist to render accepted surfaces. **No component may invent a surface, a state or a meaning.**

| Component family | Renders | Semantics owned by |
|---|---|---|
| `shell_navigation` | 4 primary destinations | `UXIA-v0` |
| `primary_action_block` | Today dominant action | `THUX-v0` |
| `planned_task_row` | selected PlannedTask projection | `THUX-v0` |
| `capacity_context_block` | daily capacity context | `THUX-v0` |
| `attention_item` | conditional attention entry | `THUX-v0` / `SPWX-v0` |
| `reason_snippet` | bounded PDT-v0 reason | `PDT-v0` |
| `focused_flow_frame` | entry, pause, exit, return chrome | `TRUX-v0` |
| `task_segment_block` | task-run segment | `TRUX-v0` |
| `assistance_panel` | assistance request and consequence | `TRUX-v0` / `ASUX-v0` |
| `provenance_prompt` | artifact origin question | `TRUX-v0` |
| `assessment_item_view` | one atomic evidence boundary | `ASUX-v0` |
| `result_family_block` | one semantic result family | `ASUX-v0` |
| `state_chip` | one presentation state + qualifiers | `SPWX-v0` |
| `skill_detail_block` | Skill axes and evidence | `SPWX-v0` |
| `inventory_count` | labelled inventory count | `SPWX-v0` |
| `history_entry` | one learning-history event | `SPWX-v0` |
| `empty_state_block` | a truthful empty state | owning surface spec |
| `degraded_state_banner` | offline / AI-unavailable / recovery | owning surface spec |

## 11.1 Restricted shapes

**Progress-bar-shaped components** are restricted. They may depict only bounded, factual, non-competence quantities — for example position within an assessment session or within a task's segments.

They may never depict:
- mastery, capability or competence,
- career or curriculum completion,
- a `confirmed / total` ratio,
- a Topic or Domain percentage,
- an English level.

Equally forbidden as component shapes: gauges, dials, level meters, rank badges, tier emblems, streak counters, calendar heatmaps, leaderboards and score trend lines.

---

# 12. Design-system accessibility rules

- Every interactive component has a stable, meaningful accessible name matching its semantic label.
- Focus order follows the semantic reading order declared by the owning surface spec.
- Focus indication is always visible and meets 3:1 contrast.
- State is exposed to assistive technology as text, never only as a visual attribute.
- Targets meet the 48dp rule.
- Content remains usable at 200% text size without losing state information.
- Reduced motion loses no information.
- Nothing critical is conveyed by colour, icon or motion alone.

---

# 13. Anti-patterns explicitly rejected

The design system must not introduce:

- a red or alarm-styled learning state,
- the `system_fault` tone on anything other than a genuine technical fault,
- a reward or celebration animation on task completion,
- a countdown, timer or urgency device,
- a competence progress bar, gauge, dial or level meter,
- a rank, tier, badge or streak visual,
- a calendar heatmap or contribution graph,
- a score trend line,
- colour, icon or motion as the sole carrier of meaning,
- locale-naive case transforms that corrupt Turkish labels,
- a component that invents a surface or state,
- a visual hierarchy that contradicts the owning surface spec,
- a dark theme derived by inverting light without re-measuring contrast,
- motion that blocks exit from a focused flow.

---

# 14. 8F acceptance contract

8F can be accepted only if independent QA verifies at minimum:

1. The design system remains an expression layer and adds no meaning, severity or hierarchy.
2. Exactly six tones exist and every tone is defined by meaning.
3. Every accepted surface state from 8A–8E has exactly one declared tone, with no state unmapped and none invented.
4. Every Skill presentation state, Topic state and attention qualifier has exactly one declared tone.
5. Only `error_recoverable` and `data_recovery_required` carry `system_fault`; no learning state does.
6. `confirmed_review_due` and Topic `weakening` are `neutral`, and attention grouping does not upgrade a tone.
7. `stopped_no_penalty`, `resume_invalidated`, `slot_recomposed`, `capacity_zero` and `empty_no_evidence_yet` are non-negative in tone.
8. Contrast rules cite WCAG 1.4.3 and 1.4.11 thresholds and require per-theme measurement.
9. Colour is never the sole carrier; every tone pairs with text and a non-colour differentiator.
10. Touch targets are at least 48dp and content is usable at 200% text size.
11. Turkish casing is protected and locked labels are rendered as authored.
12. Motion has purpose classes, honours reduced motion, and forbids countdown, reward-on-completion, decay and streak animation.
13. Motion is never the sole indication of a state change and never blocks exit.
14. Every component maps to an accepted surface and an owning spec, and none invents a surface or state.
15. Progress-bar shapes are restricted to bounded factual quantities and never depict competence.
16. Gauges, levels, ranks, badges, streaks, heatmaps and trend lines are excluded.
17. No concrete hex value is locked without the measurement requirement.
18. 8G and implementation boundaries remain open.
19. Stage 6, Stage 7, 8A, 8B, 8C, 8D and 8E accepted contracts still validate.

---

# 15. Handoff after acceptance

If accepted, 8F becomes `VDSX-v0 / D-073`.

Next numbered step:

**8G — Wireframe/prototip**

8G will produce concrete wireframes and prototype geometry using this design system and the accepted 8A–8E semantics, without altering any state, label, tone or truth ownership. It must receive a fresh PRE-STEP and explicit user approval before execution.
