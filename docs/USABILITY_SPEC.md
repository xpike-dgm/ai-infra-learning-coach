# Usability Specification — USBX-v0

**Stage step:** 17C — Kullanılabilirlik  
**Status:** ACCEPTED — independent 17C QA PASS  
**Decision:** `D-135`  
**Model:** `USBX-v0 — run usability: the focused-flow exit stays in view above the scrolling run, the learner's position is said as a bounded fact ("Soru 2 / 5"), answers, code and reports keep exactly what was typed, the history names help in words, and the reminder is the app's own`  
**Behaviour it implements:** `TRUX-v0` (8C: the safe exit, one action from every state); `VDSX-v0` §11.1 (a position, never competence) and §9 (one icon metaphor per meaning); 14F (a short answer is compared as typed); `LHRX-v0` (16B history); `RMDX-v0` (16E reminder)  
**Code:** `app-ui` (`ExactTextInput.kt`), `app-wiring` (`AndroidManifest.xml` — `adjustResize`), `core-presentation` (`RunUsability.kt` — `RunPosition`, `AnswerField`, `AnswerInput`, `AssistanceWords`; `TaskRunStage.Question.total`; `HistoryPresentation`), `app-ui` (`TaskRunnerScreen.kt`, `Components.kt` — `ScreenColumn(insets)`), `app-wiring` (`TaskRunSession`, `Reminders.kt`, `res/drawable/ic_stat_today.xml`)  
**Persistence / schema:** none — schema v11 unchanged

## 1. Purpose

> **Bir görevi yaparken çıkışı her zaman görebiliyor, kaçıncı soruda olduğumu bir puan gibi değil bir konum olarak görüyor, cevabımı klavye bozmadan yazabiliyor, geçmişte hangi yardımı aldığımı kelimelerle okuyor ve hatırlatmayı uygulamanın kendisi olarak tanıyabiliyor muyum?**

Primary invariant:

> **The exit stays in view.** It sits above the scrolling run, so a long lesson never hides it.
>
> **The position is a fact, not a score:** "Soru 2 / 5" counts this run's questions and says where the learner is, in text.
>
> **What is compared as typed is kept as typed:** a short answer, code and a pasted test report ask the keyboard for no suggestions at all (`TYPE_TEXT_FLAG_NO_SUGGESTIONS`) and no automatic capital. An open answer gets sentence capitals and the keyboard's help.
>
> **Help is named in words** in the history, never as H1–H4.
>
> **The reminder is the app's own:** its own monochrome mark, the measured accent and the reminder category.

---

# 2. What was found

- **The exit scrolled away.** It was the first item of a scrolling column, so in a long lesson it left the screen (17A's emulator run).
- **A keyboard could make a right answer wrong.** Short answers are compared with the answer key as typed (14F), and code and reports are run or parsed as written; a keyboard's autocorrect can rewrite `print(x)` or "4 4". Capitalisation was already off (Compose's default).
- **Turning autocorrect off is not enough on a multi-line field.** The first version set Compose's `autoCorrectEnabled = false`. The independent review read the compiled Compose 1.12 and AOSP's LatinIME and showed that this only drops the auto-correct flag, which keyboards ignore on a multi-line field. The fix adds `TYPE_TEXT_FLAG_NO_SUGGESTIONS` to the editor info (`ExactTextInput`). On the emulator the answer field's editor reports `inputType=0xa0001` (text, multi-line, no suggestions), and Gboard shows no suggestion strip.
- **The window did not resize for the keyboard** (no `windowSoftInputMode`), so a panned window could push the fixed exit off screen while typing. The activity now resizes (`adjustResize`), and on the emulator the exit stays visible with the keyboard open. The AI build — the default — uses its own full copy of the manifest, so the attribute is in both copies; 14G's manifest check caught the copies differing.
- **The history showed codes** ("Açılan yardım: H1, H3").
- **The reminder borrowed a system icon** (`android.R.drawable.ic_popup_reminder`) and had no category.
- **One earlier test pinned the codes;** it was narrowed ("Narrowed at 17C").

---

# 3. Decisions

User decision (2026-10-27): **run usability only.** The day summary naming a run's real changes, the two IA edges (`skill_detail -> planner_explanation`, `technical_english_profile -> skill_detail`) and AI usage/cost visibility were not chosen for 17C; they stay open, and where they go is the user's call at a later PRE.

Assistant defaults:

- the exit is a fixed bar above the scrolling run; the run has one scroll;
- the total is the run's servable questions (`RunnableTask.items`); one question, or a total smaller than the number, says only "Soru n";
- an open answer (rubric) gets sentence capitals and keeps the keyboard's suggestions; a short answer, code and a report get none;
- the help question and the tutor practice answer are prose sent to the tutor, never matched against a key, so they keep the keyboard's help;
- a screen reader hears the position as words ("Soru 2, toplam 5 soru"), never as a fraction;
- the history's words: yön ipucu, kavram yardımı, kısmi çözüm, tam çözüm;
- the reminder's icon is Today's metaphor (a day of the calendar), solid white for the status bar. The accent is the light theme's `tone_active` design token, measured against the app's surfaces and not the notification shade, which Android adjusts itself. The reminder category may let it through Do Not Disturb when the learner allows reminders there.

---

# 4. What moved on

- The day summary naming a run's real changes, the two IA edges and AI usage/cost visibility (not chosen; a later PRE decides).
- TalkBack and accessibility preferences (17D).
- The reminder seen on the device (19).
- `AssessmentSessionScreen` (an 11D component no screen calls) still has the old exit, field and H-codes; if it is ever wired, it must take 17C's runner patterns.
- One source for the help levels' two wordings (the tutor's menu and the history).

---

# 5. Verification

- Kotlin: `RunUsabilityTest` (3); one history test narrowed.
- Validator: `tools/validate_usability.py`, 31/31 PASS.
- Mutation: Kotlin 10/10 and validator 20/20, each a single clean run; the controls survived.
- Independent review: a separate agent found one blocking problem (autocorrect not actually off). It was fixed and verified on the emulator, and its suggestions were taken: resize for the keyboard, the spoken position, wording, validator gaps.
- Emulator (Pixel 7 AVD, API 34; not T6): the exit stays at the top after scrolling a long lesson and with the keyboard open; the question header shows the position; the answer field's editor is `inputType=0xa0001` and Gboard shows no suggestions. The notification was not observed.
- 1294 JVM tests, `verifyModuleBoundaries`, `assembleDebug` with and without the AI adapter.
- The fast standing sweep passes; the content validators were not re-run (the packages are unchanged) and `validate_content_qa.py` was not run (disk space).

---

# 6. Acceptance criteria

17C is accepted only if independent QA verifies at minimum:

1. The exit is above the scrolling run and the run has one scroll.
2. The position is said as text, only as a bounded fact, and never as a bar.
3. Short answers, code and reports ask the keyboard for no suggestions and no capitals; an open answer keeps the keyboard's help.
4. The history names help in words.
5. The reminder uses the app's own monochrome icon, the measured accent and the reminder category.
6. The unchosen items are named as open.
7. Earlier gates still pass.

---

# 7. Open loops

See §4. **17C is complete.** Next numbered step: `17D — Accessibility`. It needs a fresh PRE and the user's new explicit approval.
