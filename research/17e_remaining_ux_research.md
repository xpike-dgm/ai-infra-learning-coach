# 17E Research — Kalan UX parçaları (RUXX-v0)

**Step:** 17E — Kalan UX parçaları (`D-136`, `D-138`)
**Date:** 2026-10-27

## 1. The day summary's real changes

- `EODX-v0` (11E) left `DayRecord.changes` for "a canonical engine" to fill; until 17E `DayCloseFacts` never filled it, so the day summary always said "Kanonik durumda bugün bir değişiklik olmadı".
- The learning history (16B, `LHRX-v0`, user decision D-123) already says, per evidence row, what the engines changed — by replaying mastery, retention and weakness over the evidence up to that row on its own study day. The day's changes are those rows' changes for one study day; no new rule is needed.
- Only Skills with evidence recorded on the day can have changed on it (a day passing is not evidence: `RVR-v0`), so only those are replayed, each from its first row.
- 13E's run report and 16B's replay start from different "befores": 13E never counts a state no engine had written; the replay starts from what the engines say about no evidence. On a new learner's first wrong answer the two disagree (seen on the emulator). Not resolved here; named as an open loop.

## 2. The two edges

- `UXIA-v0` §7.2 already describes `planner_explanation` as "Why this task? / Why not today? / Why blocked?" — a Skill's plan is the same question asked about one Skill. `PDT-v0` §2.2 (`user_facing_explanation ⊆ facts_in_decision_trace`) holds by construction when the view is the whole explanation filtered.
- `TEPM-v0` §8 makes the uneven per-Skill profile first-class; `SPWX-v0` keeps one Skill truth — so the profile links to the one shared `skill_detail` rather than repeating it.
- Both are additions to an accepted contract (`ux/8a_information_architecture/ia.yaml`), made by the user's decision and recorded as an amendment (`D-138`); the code graph and the contract are compared by 10B's validator.

## 3. AI usage

- Provider reply shape, read from the official openai-python SDK types (the platform reference returned HTTP 403 to automated fetches), 2026-10-27:
  - `Response.usage: Optional[ResponseUsage]`;
  - `ResponseUsage`: `input_tokens: int`, `output_tokens: int`, `total_tokens: int`, `input_tokens_details` (`cached_tokens`, `cache_write_tokens`), `output_tokens_details` (`reasoning_tokens`);
  - no price, cost or billing amount anywhere in `Response`.
  - Source: `https://raw.githubusercontent.com/openai/openai-python/main/src/openai/types/responses/response_usage.py`, `.../response.py`.
- Consequences: tokens are read as reported and are unknown when `usage` is absent (it is optional); no money is computed (user decision, and the reply carries none); the SDK does not say whether `usage` is present on an incomplete or refused reply, so it is read whenever present.
- `AIAX-v0` §11: only the minimum leaves the device; keeping what a call carried would be a second copy of learner content. Only counts are kept.
- `BackupTest`'s credential scan treats a column named `*token*` as one that could hold a credential; the counts are `input_usage` / `output_usage`.

## 4. Focus ring, theme, reduced motion

- WCAG 2.2 2.4.7 (focus visible, AA), 2.4.11 (focus not obscured, AA), 2.4.13 (focus appearance, AAA: an outline at least 2 CSS px thick, 3:1 against unfocused). The `focus_ring` token was measured in 8G at 6.17:1 / 5.55:1 (light) against surface / surface_variant; 17E's test recomputes it in both themes.
- Compose moves focus to a clickable only in non-touch input; a tap draws no ring.
- `enableEdgeToEdge()` styles the system bars from the system's dark mode; with an app theme chosen apart from the system the bars must be styled from the app's theme (seen on the emulator: dark icons on the dark theme).
- `MOTX-v0`: reduced motion follows Android's animator scale; an in-app switch may only add to it (it cannot bring back motion the learner removed system-wide).
