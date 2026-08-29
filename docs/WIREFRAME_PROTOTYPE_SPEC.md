# Wireframe & Prototype Geometry Specification — WFPX-v0

**Stage step:** 8G — Wireframe/prototip  
**Status:** ACCEPTED — independent 8G QA PASS  
**Decision:** `D-074`  
**Model:** `WFPX-v0 — Wireframe & Prototype Geometry`  
**Parent IA:** `UXIA-v0 / D-068`  
**Design system:** `VDSX-v0 / D-073`

## 1. Purpose

8G turns the accepted semantics of 8A–8E and the design system of 8F into **concrete layout geometry and a measured colour palette**, closing AŞAMA 8.

It answers one primary question:

> **Bunlar ekranda tam olarak nasıl yerleşir ve renkler gerçekten okunabilir mi?**

Primary invariant:

> **Geometry arranges accepted meaning. It never changes what a surface means, what order its regions carry, which state a thing is in, or what tone that state has.**

---

## 2. Binding inputs

- `UXIA-v0 / D-068` — 4 destinations in fixed order, 6 shared detail surfaces, 2 focused flows; window adaptation must not change destination meaning or order.
- `THUX-v0 / D-069` — 5 Today regions in fixed semantic order; no fixed card count.
- `TRUX-v0 / D-070` — 6 lifecycle phases; the shell may be visually suppressed but exit/pause must stay reachable in one deliberate action.
- `ASUX-v0 / D-071` — blocks and atomic evidence boundaries; position context is orientation, never a countdown.
- `SPWX-v0 / D-072` — two-half Progress overview, 8 Skill states, 6 Topic states, locked Turkish labels, inventory-not-ratio counting.
- `VDSX-v0 / D-073` — 6 tones and their exhaustive state mapping, token roles, §7 contrast rules **measured per theme**, 48dp targets, 200% text, Turkish casing, motion rules, restricted shapes. §3.2 assigns screen layouts and geometry to this step, and §3 defers the concrete palette to 8G/10 with a measurement requirement.

8G introduces no destination, surface, state, label, tone, behavior or truth ownership.

---

# 3. Scope boundary

## 3.1 8G decides

- window classes and their breakpoints,
- concrete region geometry for every accepted surface,
- responsive arrangement rules that preserve semantic order,
- focused-flow chrome geometry and exit placement,
- the concrete measured colour palette for light and dark,
- text-scaling reflow rules,
- the non-binding viewable prototype,
- explicit handoff boundaries to AŞAMA 9 and beyond.

## 3.2 8G does not decide

- navigation framework or UI technology → 9A/10,
- physical persistence schema → 9C,
- runtime planner/mastery/assessment implementation → 12–14,
- analytics and notification design → 16,
- final microcopy and accessibility calibration → 17–18,
- empirical contrast/motion tuning on real devices → 18E,
- any accepted semantic, state, label, tone or ownership.

The prototype is a **visualisation, not an implementation**. It commits to no framework.

---

# 4. Window classes

Three classes, by available width:

| Class | Width | Shell rendering |
|---|---|---|
| `compact` | < 600dp | bottom navigation bar |
| `medium` | 600–839dp | navigation rail |
| `expanded` | ≥ 840dp | navigation rail + optional detail pane |

Binding rules:

- The four destinations keep **identical identity and order** in every class: `Today → Learn → Progress → Profile`.
- Semantic region order is **invariant**; only spatial arrangement changes.
- No class introduces, removes or reorders a region.
- `expanded` may place a shared detail surface in a side pane, but that pane shows the *same* surface with the *same* truth — never a second copy.

---

# 5. Surface geometry

Geometry is declared structure, not a pixel comp. Machine-readable form: `ux/8g_wireframe_prototype/wireframe.yaml`.

## 5.1 `today_overview`

```text
compact                          expanded
┌──────────────────────────┐     ┌─────────┬──────────────────────────┐
│ PRIMARY ACTION           │     │  Today  │ PRIMARY ACTION           │
│  purpose · duration      │     │  Learn  │  purpose · duration      │
│  [ Başla ]               │     │  Progr. │  [ Başla ]               │
│  reason snippet →        │     │  Profil │  reason snippet →        │
├──────────────────────────┤     │         ├──────────────────────────┤
│ day plan context         │     │         │ day plan context         │
├──────────────────────────┤     │         ├──────────────────────────┤
│ remaining plan           │     │         │ remaining plan           │
│  ▸ task row              │     │         │  ▸ task row              │
│  ▸ task row              │     │         │  ▸ task row              │
├──────────────────────────┤     │         ├──────────────────────────┤
│ attention (conditional)  │     │         │ attention (conditional)  │
├──────────────────────────┤     │         ├──────────────────────────┤
│ supporting navigation    │     │         │ supporting navigation    │
└──────────────────────────┘     └─────────┴──────────────────────────┘
│ Today Learn Progr. Profil│
└──────────────────────────┘
```

Region order is fixed by `THUX-v0` and identical in all classes. The primary action is visually dominant in every class. No fixed card count is imposed.

## 5.2 `progress_overview`

```text
┌──────────────────────────────────┐
│ Kanıtlanmış yetkinlik            │  ← inventory, counts only
│  Doğrulanmış            12       │
│  Bağımsız Gelişiyor      5       │
│  Pekiştirme Gerekli      3       │
├──────────────────────────────────┤
│ Dikkat gerektirenler             │  ← attention set, grouped by reason
│  ▸ Pekiştirme            3       │
│  ▸ Yeniden kontrol       1       │
│  ▸ Tekrar zamanı         4       │
└──────────────────────────────────┘
```

Counts are rendered as inventory. No ratio, bar, gauge or percentage appears in this surface.

## 5.3 `skill_detail`

```text
┌──────────────────────────────────┐
│ Skill adı                        │
│ [Doğrulanmış · Tekrar Zamanı]    │  ← primary state chip
│ ⚠ retention izlemede             │  ← attention qualifiers, as text
├──────────────────────────────────┤
│ Mastery      · Doğrulanmış       │  ← the four axes stay
│ Retention    · Tekrar zamanı     │     individually inspectable
│ Ön koşul     · Hazır             │
│ Zayıflık     · Yok               │
├──────────────────────────────────┤
│ Objective kırılımı               │
├──────────────────────────────────┤
│ Kanıt özeti                      │  ← independent / assisted /
│                                  │     provisional / invalid ayrı
├──────────────────────────────────┤
│ Ön koşul bağlamı · Neden? →      │
└──────────────────────────────────┘
```

The primary chip never replaces the axis block; both are always present.

## 5.4 `task_runner_flow` and `assessment_session_flow`

```text
┌──────────────────────────────────┐
│ [Duraklat]            [Çık]      │  ← always present, 48dp, fixed top
├──────────────────────────────────┤
│ orientation / item content       │
│                                  │
│                                  │
├──────────────────────────────────┤
│ [Yardım]              [Gönder]   │
├──────────────────────────────────┤
│ Blok 2 / 4                       │  ← orientation only, no countdown
└──────────────────────────────────┘
```

Binding rules:

- The shell may be suppressed, but **exit and pause keep full 48dp targets and a fixed position in every window class**. When the shell is hidden, they are the only way out, so their geometry is a safety requirement.
- Position context is textual orientation. No countdown, timer or urgency element occupies this geometry.

---

# 6. The measured palette

`VDSX-v0` §3 deliberately did not lock colour and required the palette to be produced here and **measured per theme**. It is measured, and the validator recomputes every ratio from the hex values on each run.

## 6.1 Hue policy — breaking the traffic light

`attention` is **violet**, not amber. `system_fault` is the only red.

If attention were amber and fault were red, the palette would form a green→amber→red severity ramp, and every `attention` state would read as "one step from failure" no matter what the tone table said. `VDSX-v0` §4 forbids visual severity beyond canonical severity; hue choice is where that rule is actually enforced.

## 6.2 Light theme

| Token | Value |
|---|---|
| `surface` | `#FFFFFF` |
| `surface_variant` | `#F1F3F5` |
| `on_surface` | `#16181B` |
| `on_surface_muted` | `#565C63` |
| `outline` | `#767C84` |
| `focus_ring` | `#0B5FBF` |
| `tone_neutral` / `on_tone_neutral` | `#4A5058` / `#FFFFFF` |
| `tone_active` / `on_tone_active` | `#0B5FBF` / `#FFFFFF` |
| `tone_positive` / `on_tone_positive` | `#1B6E3C` / `#FFFFFF` |
| `tone_attention` / `on_tone_attention` | `#6A3FB5` / `#FFFFFF` |
| `tone_pending` / `on_tone_pending` | `#2F6B72` / `#FFFFFF` |
| `tone_fault` / `on_tone_fault` | `#B3261E` / `#FFFFFF` |

## 6.3 Dark theme

| Token | Value |
|---|---|
| `surface` | `#121417` |
| `surface_variant` | `#1D2126` |
| `on_surface` | `#E6E9ED` |
| `on_surface_muted` | `#A8B0B8` |
| `outline` | `#7A828B` |
| `focus_ring` | `#7FB0F5` |
| `tone_neutral` / `on_tone_neutral` | `#A9B1BA` / `#16181B` |
| `tone_active` / `on_tone_active` | `#7FB0F5` / `#0A1B2E` |
| `tone_positive` / `on_tone_positive` | `#79D3A0` / `#0A1F13` |
| `tone_attention` / `on_tone_attention` | `#C0A6F5` / `#1E1030` |
| `tone_pending` / `on_tone_pending` | `#8FCBD3` / `#0B2124` |
| `tone_fault` / `on_tone_fault` | `#F2B8B5` / `#3A0B08` |

Dark is **not** an inversion of light; it was measured independently.

## 6.4 Measured minima

All 52 required pairs pass. Lowest observed values:

- text on surface: **6.08** (light `on_surface_muted` on `surface_variant`), required ≥ 4.5,
- non-text and boundaries: **3.79** (light `outline` vs `surface_variant`), required ≥ 3.0,
- text on tone containers: **6.06** (light `on_tone_pending`), required ≥ 4.5.

Every value is recomputed by `tools/validate_wireframe_prototype.py` from the stored hex; the stored ratios are evidence, not the source of truth.

---

# 7. Text scaling

At up to 200% system text size:

- layouts **reflow**; they do not truncate state,
- a Skill row that cannot fit its primary state and qualifiers on one line wraps to two,
- state chips wrap before the Skill name is elided,
- duration and secondary metadata are elided **before** any state information,
- the focused-flow exit and pause affordances never shrink below 48dp.

---

# 8. The prototype

`ux/8g_wireframe_prototype/prototype.html` is a single self-contained page that renders the wireframes and both measured palettes for human review.

It is **non-binding**:

- it commits to no framework, library or platform API,
- it is not an implementation and is not a technology choice,
- 9A/10 remain free to choose any technology,
- if it ever disagrees with this specification, the specification wins.

---

# 9. Anti-patterns explicitly rejected

Geometry must not introduce:

- a region order that differs from the owning surface spec,
- a destination whose identity or order changes by window class,
- a second copy of a shared surface with its own truth,
- a countdown, timer or urgency element in focused-flow chrome,
- an exit or pause affordance below 48dp or hidden behind a menu,
- a ratio, bar, gauge or percentage in `progress_overview`,
- a primary state chip that replaces the axis block in `skill_detail`,
- truncation of state information at large text sizes,
- a green→amber→red severity ramp across learning tones,
- a dark palette derived by inverting light without measurement,
- a prototype presented as an implementation or technology decision.

---

# 10. 8G acceptance contract

8G can be accepted only if independent QA verifies at minimum:

1. Geometry arranges accepted meaning and changes no semantic, state, label, tone or ownership.
2. Three window classes are defined and destination identity and order are invariant across all of them.
3. Semantic region order matches the owning surface spec exactly for every surface.
4. Today's five regions appear in the `THUX-v0` order with the primary action dominant in every class.
5. `progress_overview` keeps the two-half structure with inventory counts and no ratio, bar, gauge or percentage.
6. `skill_detail` shows the primary chip **and** all four axes; the chip never replaces them.
7. Focused-flow exit and pause keep 48dp targets and a fixed position in every window class.
8. No countdown or urgency element exists in focused-flow chrome.
9. The palette declares light and dark independently, with no inversion.
10. **Every declared contrast ratio is recomputed from the hex values by the validator**, not read from the file.
11. Text on surfaces meets ≥ 4.5:1, non-text and boundaries ≥ 3.0:1, text on tone containers ≥ 4.5:1, in both themes.
12. Tone token pairs exist for all six `VDSX-v0` tones and for no others.
13. `attention` is not amber/orange and `system_fault` is the only red, so no traffic-light ramp exists.
14. Layouts reflow at 200% text and state information is never elided first.
15. The prototype is declared non-binding and commits to no technology.
16. AŞAMA 9+ boundaries remain open.
17. Stage 6, Stage 7, 8A, 8B, 8C, 8D, 8E and 8F accepted contracts still validate.

---

# 11. Handoff after acceptance

If accepted, 8G becomes `WFPX-v0 / D-074` and **AŞAMA 8 closes**.

Next numbered step:

**9A — Mobil teknoloji seçimi**

AŞAMA 9 will choose the mobile technology and then define the local-first storage, domain data model, service boundaries, AI integration architecture and test strategy that implement the semantics locked in AŞAMA 8. It must receive a fresh PRE-STEP and explicit user approval before execution.
