# Remaining UX Specification — RUXX-v0

**Stage step:** 17E — Kalan UX parçaları  
**Status:** ACCEPTED — independent 17E QA PASS  
**Decision:** `D-138` (step added by `D-136`)  
**Model:** `RUXX-v0 — the day summary says what the engines changed that day; skill_detail -> planner_explanation and technical_english_profile -> skill_detail; AI calls counted with the provider's tokens and no money; a visible focus ring; a theme choice; an in-app reduced-motion switch that only adds`  
**Behaviour it implements:** `EODX-v0` (11E), `LHRX-v0` (16B), `PCRX-v0` (13E), `RSNX-v0`/`PDT-v0` (12E), `UXIA-v0` (amended), `PWPX-v0` (16C), `PRVX-v0`/`AIAX-v0` (14G, 9E), `VDSX-v0`/`WFPX-v0`, `MOTX-v0` (17B), `A11X-v0` (17D)  
**Code:** `core-model` (`AiUsageFacts.kt`, `DisplayFacts.kt`), `core-ports` (`displayPreferences`, `aiCalls` — refinements), `core-application` (`DayCloseFacts`, `LearningHistoryQuery.day`, `DisplaySettings`, `AiUsage`), `core-presentation` (`DayChanges`, `PlannerExplanationPresentation.forSkill`, `SkillPlanCopy`, `AiUsagePresentation`, `AppearancePresentation`, `ReducedMotion`, `DesignTokens.FOCUS_RING_*`, `NavigationGraph`), `ai-adapter` (`OpenAiResponses` counts each call), `app-ui`, `app-wiring`, `ux/8a_information_architecture/ia.yaml` (amended)  
**Persistence / schema:** v12 — `display_preference`, `ai_call_record` (both append-only; nothing stored is rewritten)

## 1. Purpose

> **Gün özeti o gün gerçekten neyin değiştiğini söylüyor mu; bir beceriden bugünün planına ve İngilizce profilinden bir beceriye gidilebiliyor mu; AI'ın ne kadar kullanıldığı uydurulmadan görülebiliyor mu; ve uygulama klavyeyle, seçilen temayla ve hareket kapalıyken kullanılabiliyor mu?**

Primary invariant:

> **The day summary says only what an engine changed that day.** Each change is replayed from the evidence recorded on that study day exactly as the learning history replays it (16B), said in 13E's sentence and named by its Skill. Activity without a change is still a day where nothing changed; changes that could not be read are named as unread, never reported as "nothing changed". The summary is read again when a run ends.
>
> **A Skill's plan is the plan's own trace narrowed to that Skill** — its needs and the tasks planned for it, in the whole explanation's sentences, with nothing added and the planner not asked again. A plan that recorded nothing about the Skill says exactly that. The English profile's Skills open the one shared Skill detail. Both edges are in the IA contract, added by the user's decision.
>
> **Every AI call that left the device is counted once** — what it was for, the model, how it ended, its attempts and the tokens the provider's reply reported. A token count the reply did not report is unknown, never zero. Nothing the call carried is kept, and there is no price, cost or budget: the reply carries tokens, not money.
>
> **The theme and the motion switch change how the app looks and moves, never what anyone has learned**, and replan nothing. The switch only adds to Android's own setting. The system bars follow the app's theme.
>
> **Keyboard focus is always visible**: every target that keeps the 48dp floor draws a 2dp ring in the measured `focus_ring` colour, 2dp outside itself, only while it holds focus, without animation.

---

# 2. What was found

- **The day summary never named a change.** `DayCloseFacts` left `DayRecord.changes` empty since 11E, waiting for an engine; the history (16B) already replays every row's change. The day's changes are now the history's own for that day.
- **The day summary did not refresh after a run** (seen on the emulator). Since 16F the end of a run read the day's facts and dropped them, so Today showed "Bugün kayıt yok" after a recorded attempt until the app was opened again. The run's end now hands the day's record back.
- **The run's report and the day summary can disagree on a first run** (seen on the emulator). After a new learner's first wrong answer the run's end said the session changed nothing, while the day summary said a question about a difficulty opened: 13E never counts a state no engine had written as a "before", the history replays from no evidence. Recorded, not changed — the day summary follows the history the user chose (D-123); which a learner should see is an open loop.
- **The status bar was dark on dark** when the dark theme was chosen on a light system (emulator): `enableEdgeToEdge()` takes the bars' style from the system. The bars now follow the app's theme.
- **The first focus ring sat on the target's edge**, so a row's text touched it (emulator). It is drawn 2dp outside.
- **A token column looked like a credential**: `BackupTest`'s scan caught `input_tokens`/`output_tokens`. Renamed `input_usage`/`output_usage`; the scan was not loosened.
- **A constructor parameter moved a trailing lambda**: adding `onCall` after the clock bound every test's trailing-lambda clock to it; `OpenAiResponsesTest` caught it. `onCall` sits before the clock.
- **The independent review found two blocking problems.** The shell's four destinations drew no focus ring (the ring came only through the 48dp modifier, which the navigation items do not use), and an attempt whose connection never opened — offline, no host, a connect timeout — was counted as a call although Profile says only calls that left the device are counted. Each tab now carries the ring; the transport connects apart from sending, and only requests really sent are attempts. Of its eleven suggestions nine were taken: the theme is read before Today on a cold start, Today no longer waits for the day's replay, a second quick change builds on the first, "Açık tema"/"Koyu tema" are not confused with the motion note, a task belongs to the Skill its need targets, every task class maps to a counted use, the light bar keeps legible icons on the oldest API, the UXIA spec text is amended, and a reply is "yanıt geldi" rather than "cevap döndü" (the tutor or evaluator may still reject it).
- **17D's audit never opened `skill_detail`.** It now opens it from the English profile and the Skill's plan from it.
- **The provider's reply carries no price** (openai-python SDK types; the platform reference refused automated reads): `usage` is optional and holds `input_tokens`/`output_tokens`.

---

# 3. Decisions

User decisions (2026-10-27):

- **Scope:** all three parts left from 17C — the day summary's real changes, the two navigation edges, AI usage visibility — and all three preferences left from 17D — a visible focus ring, a theme choice, an in-app reduced-motion switch.
- **AI usage:** calls and the provider's input/output tokens per use, this month and in all; no money amount.
- **UXIA-v0 amended:** `skill_detail -> planner_explanation` and `technical_english_profile -> skill_detail` (the objects' contextual lists name them); nothing else changed.

Assistant defaults: see the contract's `assistant_defaults` (`arch/17e_remaining_ux/remaining_ux.yaml`) — among them the model id, AIAX-v0's outcomes as the count's outcomes, the connection check as its own use, the focus ring's size from WCAG 2.2 2.4.13 (no accepted spec sized it), digits grouped without a locale, and a motion note that says which setting is in force.

---

# 4. What moved on

- Which "what changed" a learner sees after a first run — the run's 13E report or the history's replay (open loop).
- The first live AI call and its counted tokens, on the device (T6, 19).
- TalkBack, and the focus ring with a real keyboard or switch, on the device (19).
- One source for the help levels' two wordings; `AssessmentSessionScreen` (no screen calls it) — still open.
- On the right-most tab the focus ring is cut by the screen's edge (19, with a real keyboard).
- The day's record replays each Skill from its first row; its cost grows with a Skill's history (18E).

---

# 5. Verification

- Kotlin: `RemainingUxTest`, `AiCallCountingTest`, `DisplayAndUsageStorageTest`, two `LearningHistoryTest` cases; `TaskLinkStorageTest` narrowed.
- Narrowed validators ("Narrowed at 17E"): `validate_accessibility.py` A17D-05 (NavRow order), `validate_end_to_end_loop.py` A16F-02 and A16F-06, `validate_motion.py` A17B-04 (two), `validate_progress_weakness.py` A16C-09 (two), `validate_provider_adapter.py` E14G-03 and the failure-mapping block.
- Emulator (Pixel 7 AVD, API 34; not T6): Profile's appearance and AI usage sections; the dark theme and its system bars; the focus ring under Tab; English profile → Skill → the Skill's plan; a run's change named in the day summary; 17D's audit, extended to `skill_detail` and `skill_plan_explanation`, at 100% and 200% text.
- Validator: `tools/validate_remaining_ux.py`. Mutation, review and counts: the contract.

---

# 6. Acceptance criteria

17E is accepted only if independent QA verifies at minimum:

1. The day summary's changes come only from the history's replay for that day, in 13E's sentences; unread changes are named; it is refreshed after a run.
2. A Skill's plan is the whole explanation narrowed, with nothing added; an unmentioned Skill says so.
3. Both edges are in the code graph and the IA contract, one way only, and wired through the graph.
4. An AI call is counted once when it left the device, with reported tokens or none, and nothing it carried; no money is shown.
5. The theme and motion choices are stored append-only, the switch only adds, and the bars follow the theme.
6. The focus ring is on every 48dp target, measured, outside the target, unanimated.
7. Schema v12 only adds two guarded tables; earlier gates still pass; the audit passes at 100% and 200%.

---

# 7. Open loops

See §4. **17E is complete. AŞAMA 17 is complete.** Next numbered step: `18A — Pilot başlangıcı`. It needs a fresh PRE and the user's new explicit approval.
