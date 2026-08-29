# 8G Wireframe & Prototype — Research & Contract Synthesis

**Stage step:** 8G — Wireframe/prototip  
**Purpose:** Turn six steps of accepted semantics and one accepted design system into concrete layout geometry and a measured palette, closing Stage 8.

## 1. Research-need decision

8G introduces no destination, surface, state, label, tone or behavior. Everything it arranges is already locked. Its problem is concretisation:

```text
UXIA-v0 destinations, shared surfaces and focused flows
+ THUX-v0 / TRUX-v0 / ASUX-v0 / SPWX-v0 content regions and states
+ VDSX-v0 tones, typography roles, spacing rhythm, targets and motion rules
→ concrete layout geometry, breakpoint behavior and a measured colour palette
```

A new separate external Research AI is **not required**. 8G does carry one genuinely new technical obligation that no prior step had: **VDSX-v0 deliberately did not lock concrete colour**, and required that the palette be produced here and measured against its §7 contrast rules per theme. That is arithmetic against a named standard, not research.

Independent QA remains required, and for the first time the validator must **compute** rather than assert: a palette that claims WCAG compliance without a contrast calculation is exactly the kind of unverified claim this repo's protocol exists to prevent.

## 2. Canonical source set reviewed

- `docs/INFORMATION_ARCHITECTURE_SPEC.md` — 4 destinations, 6 shared detail surfaces, 2 focused flows; window adaptation must not change destination meaning or order.
- `docs/TODAY_HOME_SCREEN_SPEC.md` — 5 content regions in fixed semantic order; semantic responsive hierarchy; no fixed card count or pixel geometry was locked there.
- `docs/DAILY_WORKING_FLOW_SPEC.md` — 6 lifecycle phases; shell may be visually suppressed but exit/pause must stay reachable at full target size.
- `docs/ASSESSMENT_SESSION_UX_SPEC.md` — blocks and atomic evidence boundaries; position context is orientation, never a countdown.
- `docs/PROGRESS_SKILL_UX_SPEC.md` — two-half overview, 8 Skill states, 6 Topic states, locked Turkish labels, inventory-not-ratio counting.
- `docs/DESIGN_SYSTEM_SPEC.md` — 6 tones and their exhaustive state mapping, token roles, §7 contrast rules with per-theme measurement, 48dp targets, 200% text, Turkish casing, motion rules, restricted shapes. §3.2 assigns screen layouts and geometry to 8G.

## 3. Synthesis problems 8G actually has to solve

1. **A palette that is measured, not asserted.** VDSX-v0 fixed the constraint and left the values. Producing hex without computing ratios would quietly undo the point of deferring them.
2. **Avoiding the traffic-light reading.** If `attention` is amber and `system_fault` is red, the palette itself becomes a severity ramp and re-imports the failure semantics that VDSX-v0 §4 forbids — regardless of what the tone table says. Hue choice is a severity decision, not a taste decision.
3. **Geometry without pixel mockups.** A repo of markdown, YAML and Python cannot usefully hold a pixel comp, and a pixel comp cannot be regression-checked. Geometry has to be expressed as declared structure that a validator can read.
4. **Responsive adaptation that cannot change meaning.** Region order and destination identity are semantic and locked; only spatial arrangement may vary by window class.
5. **A prototype that is not a technology commitment.** 9A owns the technology choice. A viewable prototype is valuable, but must be explicitly non-binding.
6. **Focused flows suppress the shell.** Exit and pause then become the only way out, so their geometry is a safety requirement rather than a layout preference.
7. **200% text against dense state rows.** A Skill row carries a primary state plus qualifiers; at double text size the layout must reflow rather than truncate the state.

## 4. Positions taken

- The palette is **computed and stored with its measured ratios**, and the validator recomputes them from the hex values on every run.
- `attention` is **violet, not amber**, and `system_fault` is the only red. This deliberately breaks the green/amber/red ramp so the palette cannot be read as a severity scale.
- Geometry is expressed as **declared region structure, ordering and breakpoint behavior** in YAML, with human-readable ASCII wireframes in the spec. No pixel comps.
- Three window classes are defined by width breakpoints; **semantic order is invariant across all three** and only arrangement changes.
- The focused-flow exit affordance keeps full 48dp target and a fixed position in every window class.
- A single self-contained `prototype.html` renders the wireframes and both palettes for human review. It is explicitly **non-binding** and is not an implementation or a technology choice.
- Every layout is required to reflow rather than truncate at 200% text, and state information is the last thing allowed to be elided.

## 5. Explicitly not decided in 8G

Navigation framework and UI technology (9A/10) — the prototype implies none, physical persistence schema (9C), runtime planner/mastery/assessment implementation (12–14), analytics and notification design (16), final microcopy and accessibility calibration (17–18), and empirical contrast/motion tuning on real devices (18E). No accepted state, label, tone, surface or truth ownership is altered.

No external source in this synthesis justifies a fixed card count as a semantic rule, a competence visualisation, a streak surface, or a colour ramp that encodes learning states as severity levels.
