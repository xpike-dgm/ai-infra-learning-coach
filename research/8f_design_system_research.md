# 8F Visual Design System — Research & Contract Synthesis

**Stage step:** 8F — Tasarım sistemi  
**Purpose:** Give the accepted 8A–8E semantics a consistent visual expression that adds no meaning of its own.

## 1. Research-need decision

8F introduces no new destination, surface, state, label or behavior. Everything it renders is already locked. Its problem is expression:

```text
UXIA-v0 destinations and surfaces
+ THUX-v0 / TRUX-v0 / ASUX-v0 / SPWX-v0 semantic states and labels
+ declared accessibility baselines from every prior UX step
+ named external accessibility and Android platform standards
→ typography, color, spacing, iconography, motion and component vocabulary
```

A new separate external Research AI is **not required**. 8F does, however, deliberately **anchor to named external standards rather than inventing values**, because the one place a design system must not improvise is accessibility. The external provenance already accepted in 8A (`developer.android.com`, `w3.org/WAI`, NN/g) is extended here with the specific WCAG success criteria that govern contrast, resize and target size.

Independent QA remains required, because a design system is the last place where forbidden meaning can re-enter the product silently — as a red badge, a celebratory animation or a progress bar.

## 2. Canonical source set reviewed

### Semantics to be rendered
- `docs/INFORMATION_ARCHITECTURE_SPEC.md` — 4 destinations, 6 shared detail surfaces, 2 focused flows, 6 cross-cutting surface states. §17 states plainly that exact touch targets, contrast, screen-reader implementation and motion preferences **belong to 8F/17**.
- `docs/TODAY_HOME_SCREEN_SPEC.md` — 5 content regions, 12 semantic states.
- `docs/DAILY_WORKING_FLOW_SPEC.md` — 6 lifecycle phases, 17 semantic states, 3 pause classes.
- `docs/ASSESSMENT_SESSION_UX_SPEC.md` — 19 semantic states, 6 result families.
- `docs/PROGRESS_SKILL_UX_SPEC.md` — 8 Skill presentation states with locked Turkish labels, 6 Topic states with locked Turkish labels, 10 semantic states, attention qualifiers.

### Product constraints
- `docs/V1_SCOPE.md` — Android-focused personal app; V1 UI requires consistent typography/spacing, dark/light, loading/empty/error states, accessibility and appropriate motion.
- `docs/PRODUCT_REQUIREMENTS.md` — modern, simple UI; time, streak and completion are never progress.

### External standards anchored
- WCAG 2.2 **1.4.3 Contrast (Minimum)** — 4.5:1 for normal text, 3:1 for large text (≥18pt, or ≥14pt bold).
- WCAG 2.2 **1.4.11 Non-text Contrast** — 3:1 for UI components and meaningful graphical objects, which covers state indicators.
- WCAG 2.2 **1.4.1 Use of Color** — color must not be the only visual means of conveying information.
- WCAG 2.2 **1.4.4 Resize Text** — content must remain usable at 200% text size.
- WCAG 2.2 **2.5.8 Target Size (Minimum)** — 24×24 CSS px floor at AA.
- Android/Material guidance — 48dp minimum touch target, which is stricter than the WCAG floor and is adopted as the product rule.
- Android animator-duration / "remove animations" accessibility setting — the platform mechanism for reduced motion.

## 3. Synthesis problems 8F actually has to solve

1. **Severity is a visual free variable, and it must not be.** `review_due` is canonically *not* forgetting. `remediation_required` is canonically *not* failure. Nothing stops a designer from rendering either in alarm red, and the label would then be contradicted by the strongest signal on the screen. Colour has to be assigned from meaning, not from feel.
2. **Where the error tone is allowed at all.** If learning states may borrow error styling, every "you are behind" affordance the product spent five steps deleting comes back through the palette.
3. **Celebration and completion.** `task_completed != mastery_confirmed` is a founding invariant. A success animation on task completion asserts exactly the thing the product denies.
4. **Progress-bar-shaped components.** 8E forbids a competence ratio. A progress bar is a ratio rendered as a picture; it needs an explicit restriction rather than an implicit one.
5. **Turkish typography and casing.** The locked labels are Turkish. Locale-naive uppercase transforms break Turkish: `i` uppercases to `I` instead of `İ`, and `ı` is mishandled. A design system that specifies "uppercase buttons" without a locale rule will silently corrupt the product's own labels.
6. **Text scaling versus dense state.** Skill rows carry a primary state plus qualifiers. At 200% text these must remain readable and must not truncate the state — the state is the information.
7. **Dark theme is not an inversion.** Contrast ratios must be recomputed per theme; a tone that passes in light can fail in dark.
8. **Tokens without geometry.** 8G owns wireframes and layout geometry. 8F must define scales and rhythm without specifying screens.

## 4. Positions taken

- The design system is an **expression layer**. It may not add meaning, severity, urgency, ranking or hierarchy that the canonical state does not assert.
- A **semantic tone vocabulary** is defined and every accepted semantic state is mapped to a tone by meaning. The mapping is exhaustive and declared, not left to visual judgement.
- **No learning state may use the error/danger tone.** That tone is reserved for genuine system faults — recoverable errors and data-recovery states.
- `review_due` and `confirmed_review_due` take a **neutral informational** tone; `verification_due` and `remediation_required` take an **attention** tone that means "needs action", never "you failed".
- **Colour is never the sole carrier**: every tone pairs with a text label and a non-colour differentiator.
- Contrast, resize and target size are anchored to the named WCAG criteria; the touch-target rule adopts Android's stricter 48dp.
- **Motion has no persuasive role.** No countdown, no urgency pulse, no reward animation for task completion. Motion may acknowledge a real canonical state change, is always optional, respects the platform reduced-motion setting, and is never the sole carrier of a state change.
- **Progress-bar-shaped components are restricted** to bounded factual quantities such as position within a session; they may never depict capability, mastery, career or a competence ratio.
- **No locale-naive case transforms.** Turkish casing is explicitly protected, and locked labels are rendered as authored.
- Type scale, spacing rhythm and motion durations are **declared product defaults**, explicitly not scientific values, and are calibration inputs for 17–18.
- Dark and light are both first-class; tone contrast is validated per theme rather than inverted.

## 5. Explicitly not decided in 8F

Screen layouts, wireframes and prototype geometry (8G), navigation framework and UI technology (9A/10) — including whether Material components are used at implementation time, physical persistence schema (9C), analytics and notification design (16), empirical accessibility and usability calibration including final contrast/motion tuning (17–18), and final production microcopy beyond the labels already locked in 8E.

No external source in this synthesis justifies a reward animation, a streak visual, a competence progress bar, a red learning state, a countdown clock or a leaderboard aesthetic.
