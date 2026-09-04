# 10C Design System Implementation — Research & Decision Synthesis

**Stage step:** 10C — Design system implementation  
**Purpose:** Turn `VDSX-v0`'s expression rules and `WFPX-v0`'s measured palette into a Compose theme that cannot break them.

## 1. Research-need decision

**No external research pass was required.** The colours are already **measured, not chosen** — `WFPX-v0` recorded them per theme with their contrast ratios — and the mechanism for supplying a fixed palette instead of a wallpaper-derived one was verified and pinned at 10A. Nothing here depends on current ecosystem data.

What this step needed instead was a decision about *where* the tokens live, and that is an architecture question the accepted contracts already answer.

## 2. Canonical source set reviewed

- `VDSX-v0 / D-073` — the design system is an expression layer and `visual_severity <= canonical_severity`; exactly six tones; one declared tone per state; no learning state may use the fault tone; attention-group membership never changes a tone; the fault tone belongs to `error_recoverable` and `data_recovery_required` alone; the 48dp target floor adopted over WCAG's 24px; state may never be carried by colour alone.
- `WFPX-v0 / D-074` — the measured palette per theme, its contrast requirements and recorded minima, the tone→token map, the rule that dark is not an inversion of light, the absence of a traffic-light ramp (attention is violet; red is the fault tone alone), 200% text support, and the standing instruction that the validator recomputes contrast from hex.
- `SPWX-v0 / D-072` — the eight Skill presentation states and the qualifiers that ride alongside them.
- `AMTS-v0 / D-075` — dynamic colour disabled; no default-locale case transform; locked labels rendered as authored.
- `MSBX-v0 / D-078` — presentation is computed in core as pure data; `app-ui` renders.
- `MPSX-v0 / D-082` — the source scan that enforces the absence of the dynamic scheme builders.

## 3. Synthesis problems 10C actually has to solve

1. **Where do the tokens live?** If the hex values only exist inside the Compose theme file, then verifying their contrast needs the UI toolkit, and the natural shortcut is to assert a remembered ratio instead. That shortcut is exactly what failed at 8G, where a hand-declared minimum turned out to be wrong and only the recomputation caught it.
2. **A prohibition enforced by review is not enforced.** "No learning state may wear the fault tone" is easy to state and easy to violate months later. The question is whether the type system can make the assignment impossible rather than merely wrong.
3. **Material's semantic roles are a trap.** Mapping the six accepted tones onto Material's `error`, `warning`-ish and `primary` roles would let any component reach for "the error colour" and apply it to a learning state — precisely the severity inflation `VDSX-v0` forbids.
4. **Grouping is not severity.** A state that appears in an attention group is still the same state. If the tone were computed from where something is rendered, the design system would be adding severity the canonical state never claimed.
5. **Absence is hard to prove.** Disabling dynamic colour means certain functions are never called, and an absence has to be checked by scanning rather than by a unit test.

## 4. Positions taken

- **The tokens live in `core-presentation` as plain data**, and the contrast formulas live beside them, so the product's own suite recomputes every ratio in an ordinary JVM test with no device and no Compose. The theme file only converts data into Compose types.
- **A separate `LearningTone` type with five values.** There is no `SYSTEM_FAULT` to assign, so the forbidden mapping cannot be written — the prohibition is **structurally** enforced rather than reviewed.
- **The six tones are exposed on their own**, not folded into Material's colour roles, and Material's `error` role carries the system fault tone and nothing else.
- **Attention grouping is a named function that returns the state's own tone**, so "grouping does not upgrade severity" is a testable claim rather than the absence of code.
- **Contrast is recomputed in both the product test and the repository validator**, and the recorded minima are re-derived from the tokens rather than copied.
- **The palette is not revised here.** If a check failed, the fix would be the code, never the measured token.
- **Every interactive target uses a `minimumTouchTarget` modifier** rather than per-component discipline.

## 5. What the checks caught

The 10A source scan for the dynamic scheme builders failed on this step's own **prose**: the theme file's comment explained the rule by naming the two functions. The scan is a plain text scan and cannot tell a comment from a call.

The comment was reworded rather than the gate loosened. Strict absence is the stronger guarantee, and a rule that tolerates "mentions but not calls" needs a parser and a judgement call where it currently needs neither.

## 6. Explicitly not decided in 10C

Real screen components and the interiors of the four destinations (11 and later), typography scale application beyond the theme, motion implementation, persistence and content loading (10D), app health and diagnostics (10E), final microcopy (14), and on-device accessibility verification (17).

No claim is made here about aesthetic quality or rendering performance.
