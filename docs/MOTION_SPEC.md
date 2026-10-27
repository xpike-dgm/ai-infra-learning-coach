# Motion Specification — MOTX-v0

**Stage step:** 17B — Motion  
**Status:** ACCEPTED — independent 17B QA PASS  
**Decision:** `D-134`  
**Model:** `MOTX-v0 — motion by purpose: orientation, continuity, feedback and state change, each a short declared band; nothing repeats, pulses, counts down or celebrates; motion is never the only sign of a change and never stands before the exit; with the platform's animations removed every change is immediate`  
**Behaviour it implements:** `VDSX-v0` §10 (purpose classes, duration bands, forbidden motion, reduced motion) and §12 (nothing conveyed by motion alone); `TRUX-v0` (the focused-flow exit); `VPLX-v0` (17A's components)  
**Code:** `core-presentation` (`Motion.kt` — `MotionPurpose`, `MotionSpec`, `MotionEasing`, `MotionPolicy`, `ForbiddenMotion`, `ReducedMotion`), `app-ui` (`Motion.kt` — `LocalReducedMotion`, `platformReducedMotion`, `motionSpec`, `ArriveOnChange`, `continuousSize`; `CoachTheme.kt`, `AppShell.kt`, `Components.kt`, `TaskRunnerScreen.kt`)  
**Persistence / schema:** none — schema v11 unchanged

## 1. Purpose

> **Uygulamadaki hareket bana yalnız nereye geldiğimi, aynı şeyin büyüdüğünü, girdimin alındığını ve durumun gerçekten değiştiğini mi söylüyor — beni acele ettirmeden, ödüllendirmeden, hiçbir şeyi yalnız hareketle söylemeden ve hareketi kapattığımda hiçbir bilgiyi kaybetmeden?**

Primary invariant:

> **Motion has a purpose, and only four:** orientation (a destination or a detail arriving), continuity (the help panel or a grouping block growing), feedback (an answer's result arriving) and state change (a chip following its new state, for the same Skill or Topic).
>
> **Each purpose is a short declared band,** and nothing is long enough to delay the next action.
>
> **Nothing repeats, pulses, counts down or celebrates** — not even when canonical state genuinely changed.
>
> **Motion is never the only sign of a change:** the text beside it already says so, and it is announced from outside the fading layer. A focused flow is never faded in or out, so its exit is visible and acts from the first frame; the destination it returns to fades in (orientation).
>
> **Reduced motion follows the platform:** with Android's animations removed, every change is immediate and nothing is lost.

---

# 2. What was found

- **No animation existed in the app.** Only Material components' own defaults moved (ripple, the navigation indicator, dialogs). 17B is the first motion the app itself makes.
- **A fade can be keyed without retaining the old screen.** The shell's content is one lambda that reads the current state, so a cross-fade would draw the new screen twice. An arriving surface fades in on its key instead, and nothing animates its leaving — which is also what keeps the exit free (`VDSX-v0` §10.3).
- **Compose already scales its durations by the platform's animator duration scale,** and at 0 it ends them at once. 17B makes the rule explicit (`ReducedMotion`, `snap()`), so it holds by construction, not by a framework default.
- **The independent review found two blocking problems in the first version, both fixed:**
  - the focused flow was faded in as a whole (continuity), so its exit started invisible though clickable — the flow is no longer faded;
  - with motion reduced every arrival drew one blank frame before its content — an arrival is now visible from its first frame.
- **It also found a chip animating when it was reused for another Skill, a stage swap animated as if it were one object, and the result's announcement on a transparent node.** A chip now restarts for a new subject, only the help panel grows and shrinks inside a run, and the live region sits outside the fade. The validator's gaps it named (generic specs, more animation APIs, frame loops, the wiring's Compose code) were closed.
- **The emulator's own System UI stopped responding once after boot** (an "isn't responding" dialog). It was the emulator, not the app; the app ran on.

---

# 3. Decisions

User decisions (2026-10-27):

- **Scope:** the motion policy in core (§10's four purposes, bands, forbidden motion, reduced motion) and its application: destinations and details arriving, the focused flow entered, panels opening and closing, an answer's result arriving, a chip following its state.
- **Celebration:** none at all, even when canonical state changes; the change is said in text.
- **Reduced motion:** the platform setting (animator duration scale 0, "remove animations"); an in-app preference stays 17D's.

Assistant defaults:

- the bands follow Material 3's duration tokens: feedback 100 ms, orientation and state change 200 ms, continuity 250 ms; the ceiling is 300 ms. They are declared defaults and calibration inputs for 18;
- easing: decelerate for something arriving (orientation), standard otherwise;
- arrival is a fade only — no scale, rotation or travel, which could read as falling or rising;
- a chip animates its colour only; its label and its announced state change at once. During the 200 ms the colour passes through intermediate colours that were not measured — transient, and the label beside it is the measured pair;
- the platform setting is read when the activity starts; Compose scales its own durations by the same setting at once, so a change while the app runs takes full effect on the next start.

---

# 4. The policy (`core-presentation/Motion.kt`)

- `MotionPurpose`: `orientation`, `continuity`, `feedback`, `state_change` — `VDSX-v0` §10.2's list, in its order.
- `MotionPolicy.bands`: one `MotionSpec` per purpose; `LONGEST_MS = 300`.
- `MotionPolicy.spec(purpose, reduced)`: the band, or a zero duration when motion is reduced.
- `MotionPolicy.CELEBRATION_ALLOWED = false`.
- `ForbiddenMotion`: §10.3's seven, named so a reader and a validator can find them.
- `ReducedMotion.of(scale)`: reduced exactly when the platform's animator duration scale is 0.

---

# 5. In the app (`app-ui/Motion.kt` and its uses)

- `CoachTheme` reads the platform's animator duration scale once and provides `LocalReducedMotion`.
- `motionSpec(purpose)` is the only place an animation spec is made: a `tween` of the band, or `snap()`.
- `ArriveOnChange(key, purpose)`: content fades in when its key changes.
  - destinations and details: orientation (`AppShell`); a focused flow is never faded;
  - an answer's result: feedback (`TaskRunnerScreen`), announced by a live region outside the fading layer.
  - with motion reduced the content is visible from its first frame.
- `Modifier.continuousSize()`: continuity for a group that grows or shrinks (`SectionBlock`, the run's help panel).
- `StateChip(subject = …)`: its colour follows a new state of the same subject; a new subject starts a new chip.

---

# 6. What moved on

- An in-app reduced-motion preference and theme preferences (17D).
- Motion durations calibrated with real use (18).
- The focused-flow exit staying in view on a long lesson (17C).
- Motion on the Poco M6 Pro (19).

---

# 7. Verification

- Kotlin: `MotionTest` (4).
- Independent review: a separate agent read the code against `VDSX-v0` §§10, 12; its two blocking findings and four suggestions were fixed.
- Validator: `tools/validate_motion.py`, 36/36 PASS.
- Mutation: Kotlin 10/10 and validator 18/18, each a single clean run; the controls survived.
- Emulator (Pixel 7 AVD, API 34; not T6): Today, Learn, a Topic, a lesson, a question and its answer with motion on; the same with the animator, transition and window scales at 0. Every screen was complete, and nothing crashed.
- 1291 JVM tests, `verifyModuleBoundaries`, `assembleDebug` with and without the AI adapter.
- The standing sweep: every validator passes; `validate_content_qa.py` was not run (disk space; the packages are unchanged) and the slow content validators were not re-run (the packages are unchanged since their 17A run).

---

# 8. Acceptance criteria

17B is accepted only if independent QA verifies at minimum:

1. The purposes are `VDSX-v0` §10.2's four, each with one band.
2. Every band is short, feedback is the briefest, and nothing exceeds the ceiling.
3. With the platform's animations removed every change is immediate.
4. Animation specs are made in one place, from a purpose.
5. Nothing repeats, pulses, counts down, scales, rotates, travels or celebrates.
6. Motion is never the only sign of a change, and the focused-flow exit is never behind motion.
7. Earlier gates still pass.

---

# 9. Open loops

See §6. **17B is complete.** Next numbered step: `17C — Kullanılabilirlik`. It needs a fresh PRE and the user's new explicit approval.
