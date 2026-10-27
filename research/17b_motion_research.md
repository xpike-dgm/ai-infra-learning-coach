# 17B — Motion: research synthesis

**Step:** 17B — Motion (`MOTX-v0` / `D-134`)  
**Question:** how does the app move — to orient, keep an object continuous, confirm input and follow a real state change — without pressure, reward or lost information, and with the platform's reduced motion honoured?

## 1. Internal contracts (binding)

- **`VDSX-v0` §10 (8F, `docs/DESIGN_SYSTEM_SPEC.md`):** motion has no persuasive role; four purpose classes; duration bands are declared defaults and calibration inputs; forbidden motion (countdown, pressure pulse, celebration on completion, decay, streak/score, motion as the sole indicator, motion that blocks exit); celebration only for a genuine canonical change and proportionate; reduced motion honoured with nothing lost.
- **`VDSX-v0` §12:** nothing critical is conveyed by motion alone; reduced motion loses no information.
- **`TRUX-v0` (8C):** the focused-flow exit is one deliberate action from every state.
- **`VPLX-v0` (17A):** the components motion is applied to.

## 2. External facts used

- **Material 3 motion tokens:** short durations 50–200 ms, medium 250–400 ms; standard and decelerate easing. The bands take the short and the first medium token.
- **WCAG 2.2 2.3.3 (Animation from Interactions, AAA):** motion animation triggered by interaction can be disabled — met through the platform setting.
- **Android:** "Remove animations" sets `Settings.Global.ANIMATOR_DURATION_SCALE` (and the transition and window scales) to 0; Compose scales its animation durations by the animator scale.

## 3. What the emulator showed

- Motion on: tabs, a Topic, a lesson, a question and its answer; no crash.
- All three scales at 0: every screen complete at once; no crash.
- The emulator's System UI stopped responding once right after boot; it was not the app.

## 4. Not decided here

An in-app preference (17D), calibration (18), the device (19).
