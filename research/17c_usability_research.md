# 17C — Usability: research synthesis

**Step:** 17C — Kullanılabilirlik (`USBX-v0` / `D-135`)  
**Question:** what makes a task run easier to use without changing what is recorded, judged or claimed?

## 1. Internal contracts (binding)

- **`TRUX-v0` (8C):** the safe exit is one deliberate action from every state, with no guilt gate.
- **`VDSX-v0` §11.1:** progress-shaped components only for bounded factual quantities, such as the position within a session or a task's segments; never competence. §9: one icon metaphor per meaning.
- **14F (`OREX-v0`):** a short answer is matched against the accepted answers as typed; only the ends are trimmed, and case is folded only where the key says.
- **16B (`LHRX-v0`):** the history records the assistance levels that were shown.
- **16E (`RMDX-v0`):** one quiet reminder a day, never pressure.

## 2. External facts used

- **Android and Compose:** Compose's `KeyboardOptions` default capitalisation is none. `autoCorrectEnabled = false` only drops `TYPE_TEXT_FLAG_AUTO_CORRECT`, which keyboards ignore on a multi-line field (AOSP LatinIME turns autocorrect off for `NO_SUGGESTIONS`, or for no auto-correct on a single-line field); the independent review found this in the compiled Compose 1.12. `TYPE_TEXT_FLAG_NO_SUGGESTIONS` is honoured, and Compose's `InterceptPlatformTextInput` lets the app add it.
- **Android windows:** `windowSoftInputMode="adjustResize"` resizes the window for the keyboard instead of panning it.
- **Android notifications:** the small icon is drawn from its alpha channel only (white on transparent); `Notification.CATEGORY_REMINDER` marks a user-scheduled reminder; `setColor` tints the icon.

## 3. What the emulator showed

- After scrolling a long lesson, the exit stayed at the top.
- The question header read "Soru 1" for a task with one servable question.

## 4. Not decided here

The day summary's real changes, two IA edges and AI usage/cost visibility (not chosen); TalkBack (17D); the device (19).
