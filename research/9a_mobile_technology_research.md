# 9A Mobile Technology Selection — Research & Decision Synthesis

**Stage step:** 9A — Mobil teknoloji seçimi  
**Purpose:** Choose the platform and UI technology that can implement the AŞAMA 8 contracts, and be explicit about which parts of that choice are durable and which need current-source verification.

## 1. Research-need decision — this step is different

Every step from 8B to 8G recorded that a separate external Research AI was not required, because each was internal contract synthesis. **9A is not.**

`docs/AI_AGENT_WORKFLOW.md` §3 routes two things explicitly to Research AI:

- `Hangisini seçmeliyiz?`
- `framework/library güncelliği`

9A is literally both. The honest position is therefore split:

**Durable and decidable here.** The selection *logic* follows from contracts that are already accepted and are not going to change:

```text
V1 targets Android only (no iOS/web/desktop)
+ V1 is local-first with no realtime multi-device sync
+ the deterministic core must survive AI and network absence
+ accessibility: 200% text, 48dp targets, screen-reader state as text
+ Turkish locale-correct casing is a correctness requirement
+ three window classes with adaptive navigation
+ years of granular evidence history with migration
→ platform and UI technology
```

That set of constraints determines the answer without needing current market data.

**Not verifiable here.** Library currency, deprecations and recommended defaults move faster than any fixed knowledge horizon. This specification therefore ships a **bounded verification list** (§9) that must be confirmed against current sources at 10A rather than being asserted now. Claiming certainty about ecosystem currency would be exactly the kind of unverified precision `AGENTS.md` forbids.

Independent QA remains required, and the validator checks the selection against the accepted contracts rather than against opinion.

## 2. Canonical source set reviewed

- `docs/V1_SCOPE.md` — Android-focused personal app; local-first; no realtime multi-device cloud sync; no iOS/web/desktop clients in V1; restart/update must preserve progress with migration; backup/export/restore required; release APK must be installable; critical flows must pass independent QA on a real Android device; the deterministic local core must not collapse when the AI Tutor is absent.
- `docs/WIREFRAME_PROTOTYPE_SPEC.md` — three window classes (`compact` <600dp, `medium` 600–839dp, `expanded` ≥840dp) with invariant destination identity and order; 48dp focused-flow exit targets; 200% text reflow.
- `docs/DESIGN_SYSTEM_SPEC.md` — measured token palette per theme, 48dp targets, no locale-naive case transforms, Turkish glyph coverage including the mono role, motion honouring the platform reduced-motion setting, and the rule that no visual treatment may add severity.
- `docs/INFORMATION_ARCHITECTURE_SPEC.md` — four peer destinations rendered as a bottom bar on compact and a rail on larger windows.
- `docs/PROGRESS_SKILL_UX_SPEC.md`, `docs/DAILY_WORKING_FLOW_SPEC.md`, `docs/ASSESSMENT_SESSION_UX_SPEC.md` — state must be exposed to assistive technology as text; AI absence must degrade to visible pending states rather than fake results.
- `docs/AI_AGENT_WORKFLOW.md` — the Research/Coding/Test separation and the independence rule.

## 3. Synthesis problems 9A actually has to solve

1. **Whether a cross-platform layer earns its cost.** Cross-platform toolkits exist to amortise one codebase across several platforms. V1 explicitly ships to one platform and explicitly excludes the others. The benefit is therefore unavailable in V1 while the costs — an extra abstraction between the app and the platform accessibility, locale and adaptive APIs — are paid immediately.
2. **Keeping the door open anyway.** "Android only in V1" is not "Android forever". If the portable part of the system is the domain core rather than the UI, a later platform addition is a re-skin rather than a rewrite. That argues for a hard dependency rule, not for a cross-platform toolkit today.
3. **A design system library will fight the design system.** Material 3's dynamic colour derives the palette from the user's wallpaper. That would silently discard the measured palette, the per-theme contrast evidence and the deliberate violet-not-amber hue policy of `WFPX-v0`. Any component library is a substrate, never the source of colour truth.
4. **Turkish casing is a platform-API question, not a styling question.** `VDSX-v0` forbids locale-naive case transforms because `i` must uppercase to `İ`. Whatever technology is chosen must expose explicit locale-aware casing, and the codebase must never call a default-locale transform.
5. **The deterministic core must be structurally unable to depend on AI or network.** Success criterion 8 says the core must not collapse when the AI Tutor is absent. A convention will not hold that line for years; a dependency boundary will.
6. **Accessibility is contractual, not aspirational.** 200% text, 48dp targets and text-exposed state are already locked. The chosen technology must provide these as first-class platform behaviour rather than as something to be re-implemented.

## 4. Positions taken

- **Android native, with no cross-platform UI layer in V1.** The cross-platform benefit is unavailable under V1 scope; its cost is not.
- **Kotlin**, as the platform's first-class language.
- **Jetpack Compose** as the UI toolkit: a declarative, state-driven toolkit matches a design system defined as tokens and states far better than an imperative view hierarchy.
- **Material 3 is a substrate; `VDSX-v0` tokens are authoritative.** **Dynamic colour must be disabled**, and library default colours and typography must not reach the screen.
- **The domain core is pure Kotlin with no Android, UI, network or AI dependency.** This enforces the deterministic-core requirement structurally and makes the core the portable asset if another platform is ever added.
- **Window size classes map one-to-one onto the three `WFPX-v0` classes**; the platform concept and the specification agree by construction rather than by coincidence.
- **Casing is always explicit-locale.** Default-locale transforms are forbidden in the codebase, not merely discouraged.
- **`minSdk` is a policy, not a magic number**: the lowest level that supports the required accessibility, locale, adaptive and date/time behaviour without compatibility shims that weaken them, confirmed against the actual target device at 10A.
- **What is deferred is deferred.** Storage engine is 9B, domain data model 9C, service boundaries 9D, AI integration 9E, test strategy 9F. 9A names no library for those.

## 5. Explicitly not decided in 9A

Storage engine and local-first persistence mechanics (9B), domain data model (9C), module and service boundaries (9D), AI integration architecture and provider (9E), test strategy and tooling (9F), dependency-injection and navigation libraries (10A), build pipeline detail (19), and any accepted AŞAMA 8 semantic, state, label, tone or geometry.

No external source in this synthesis justifies a specific library version, a claim about which framework is "currently best", or a performance number. Those belong to the §9 verification list, to be resolved with current sources at 10A.
