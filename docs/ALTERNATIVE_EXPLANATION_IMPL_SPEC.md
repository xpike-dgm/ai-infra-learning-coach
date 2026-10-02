# Alternative Explanation Implementation Specification — ALEX-v0

**Stage step:** 14C — Alternatif anlatım  
**Status:** ACCEPTED — independent 14C QA PASS  
**Decision:** `D-107`  
**Model:** `ALEX-v0 — Alternative Explanation`  
**Behaviour it implements:** `LEARNING_BEHAVIOR_RULES` §9 (when learning does not land, the method changes) and §12 (a verified written core; AI may explain differently but never changes scope), `WLRM-v0` remediation strategies (`targeted_reteach`, `worked_example`, `state_trace_reconstruction`, `prerequisite_refresh`, `misconception_contrast`), `TUTX-v0 / D-105` (`explain_differently`), `WAAX-v0 / D-106` (misconception memory), `AIAX-v0 / D-079` §11 (what may leave the device)  
**Persistence:** schema unchanged — written explanations are content, served by the content port  
**Boundaries:** `MSBX-v0 / D-078` — one content-port refinement (`explanationsFor`); ports remain the four plus `TutorPort` (`D-105`)

## 1. Purpose

14A let the learner ask for an explanation again; it did not say *how*. 14C decides what "explain it another way" offers, where each way comes from, and how an AI-written alternative is kept honest.

It answers one question:

> **Anlatım tutmadığında başka nasıl anlatılır — ve bu anlatım neye dayanır?**

Primary invariant:

> **When an explanation does not land, the method changes — the scope and the truth do not.** The learner chooses the form; a verified, written explanation is shown first; only where none exists does the tutor write one — grounded in the course's own explanation, told never to contradict it, labelled as unverified, and never the last word: the course's explanation is always one action away. A form that would need the learner's own state is never written by the AI.

---

# 2. What was found before writing explanation code

- **`explain_differently` had no form.** A request could say "again" but not how; every alternative would have been the model's choice.
- **There was nowhere for written alternatives to live.** `LEARNING_BEHAVIOR_RULES` §12 wants a verified core — short and detailed explanations, worked examples, common mistakes, alternative/remediation explanations — and the content format had none of it.
- **An AI alternative could not be grounded.** The tutor request could carry the reference *solution* only once it gave nothing away; the course's explanation of the concept itself — the text an alternative must not contradict — had no field.
- **A contrast with the learner's own misconception would have to send learner state.** 14B's memory is learner data (`AIAX-v0` §11 never-sent).

---

# 3. User decisions (2026-10-01)

1. **The learner chooses the form** from a short menu; nothing is ranked or chosen behind their back; forms already shown this session are marked.
2. **Written first, otherwise AI:** a verified written explanation for the chosen form is shown when one fits; only when none does is the tutor asked.

---

# 4. Scope boundary

## 4.1 14C decides

- the closed vocabulary of explanation forms and where each comes from,
- where written explanations live and how they are read,
- the menu, the order of sources, and what fits the learner's ceiling,
- grounding an AI alternative in the course's own explanation,
- what is said beside an alternative.

## 4.2 14C does not decide

- the written explanations themselves (15),
- choosing a form automatically, or fading guidance by expertise (the planner and task metadata, `TEIP-v0` §5.4; calibration 18),
- the adapter's call site (14G),
- rendering the menu and calling the use case from the app (16D),
- any mastery, weakness or planner rule.

No score, threshold, ratio or ordering by "what works best" is introduced.

---

# 5. Forms (`ExplanationForm`)

| Form | Source | Tutor may write |
|---|---|---|
| `plain_reteach` | §9 "daha sade anlatım"; `strategy.targeted_reteach` | yes |
| `different_example` | §9 "farklı örnek/analogy" | yes |
| `worked_example` | §9 "worked example"; `strategy.worked_example` | yes |
| `state_trace` | `strategy.state_trace_reconstruction` | yes |
| `prerequisite_refresh` | §9 "prerequisite'e kısa geri dönüş"; `strategy.prerequisite_refresh` | yes |
| `misconception_contrast` | `strategy.misconception_contrast` | **no** — chosen because of this learner's memory, which never leaves the device |
| `canonical` | §12 the verified core | no — it is not an alternative; every alternative is grounded in it and returns to it |

---

# 6. Written explanations (`ExplanationVariant`, `explanationsFor`)

One written explanation of one Objective version: `explanation.<namespace>.<slug>` (`GNS-v0`), its form, **the level it reaches** (declared by its author, so it is recorded at that level if shown while an answer is open; the course's own explanation has none), its text and — for a contrast only — the catalog misconception it contrasts. Authored in a strict `[explanation]` package section (a backslash-n is a line break), served by `ContentPort.explanationsFor(objective)` (a refinement), pinned by version. Never published into the store: it is content, like an item's prompt.

---

# 7. The menu (`ExplanationMenu`, `ExplainDifferently.options`)

The offered forms, **in the vocabulary's order** — the learner chooses:

- a form with a written explanation is offered as **written**,
- otherwise, if the tutor may write it, as **AI**,
- a contrast only if a written contrast exists for a catalog label **this learner's memory holds open** (hypothesis, supported or confirmed) — read on the device, offered as "a common mix-up", never "your mistake",
- each option says whether it was already shown this session (held in memory only; nothing is stored).

---

# 8. Showing a form (`ExplainDifferently.prepare` / `explain`)

- `prepare` builds the learner's ask as `explain_differently`, names the form only where the tutor may write it, and **attaches the course's own explanation** whenever one is written — no AI alternative is asked without the text it must not contradict, when that text exists.
- `explain` shows the **least revealing written** explanation of the form that fits the learner's ceiling, without calling the tutor (`AskTutor.showWritten`, `TutorRules.authored`); if none fits and the tutor may write the form, the tutor is asked; otherwise there is nothing true to show, and that is said.
- 14A's rules hold unchanged: the ceiling while an answer is open, disclosure for H3/H4, nothing recorded outside an attempt, a shown solution is an exposure — written or AI.

---

# 9. The tutor's instructions (`tutor_instructions/2`)

Two rules are added; the version is raised because a rule changed:

- **14.** `<canonical>` is the course's verified explanation: explain the same content another way; never contradict it, never add scope it does not have, and say you are unsure rather than "correcting" it.
- **15.** A named form is explained as that form (simpler; a new example or analogy, never the task's own item; a different small worked problem, never the task's own; the state step by step; a brief recap of what must be understood first).

`<canonical>` is material like every other section: pasted text cannot close it. The tutor is never told about a form only written content may give.

---

# 10. What is said (`AlternativeExplanationPresentation`)

Menu lines name the form, mark "AI ile" for a tutor-written option and "bu oturumda gösterildi" for one already seen. Beside every alternative: a tutor-written one is labelled **"AI tarafından üretildi: dersin doğrulanmış içeriği değildir… asıl anlatım geçerlidir"**, a written one as verified; **"Asıl anlatıma dön"** is always offered. No score, number or blame.

---

# 11. Changes to accepted code

- `TutorAsk.form`, `TutorRequest.form`, `TutorContext.canonicalExplanation`; `TutorRules.prepare` refuses a form on another intent or a form the tutor may not write; `TutorRules.authored`.
- `TutorInstructions.VERSION` → `tutor_instructions/2`; `form:` line and `<canonical>` section; both omitted when absent, so 14A's messages are unchanged byte for byte.
- `AskTutor.showWritten` (14A's `ask` unchanged).
- 14A's validator narrowed for exactly these: the sixth context field, the sixth section, the instructions version, and field names read word by word (so "explanation" is not read as "plan"); every other guarantee stands.

---

# 12. Verification

- Suites: `AlternativeExplanationFactsTest`, `TutorInstructionsTest` (model), `ExplainDifferentlyTest` (application), `AlternativeExplanationPresentationTest` (presentation), `PackageFormatTest` (the strict section and the content port).
- Validator `tools/validate_alternative_explanation.py`, reading `LEARNING_BEHAVIOR_RULES` §9/§12, the `WLRM-v0` strategies and `AIAX-v0` §11 against the Kotlin, with its own mutation test.
- Runs: T1, T2, T3, T5 and both builds — all PASS.
- **Mutation:** see the contract's `mutation_results`; only the 14C suites run; a compile failure is not a detection.
- **Not run: T6.** Nothing in the app offers the menu yet (16D).

---

# 13. Open loops

| Loop | Owner |
|---|---|
| written explanations (canonical and alternatives, contrasts) | 15 |
| rendering the menu; calling `ExplainDifferently` from the app | 16D |
| the adapter call site | 14G |
| whether one form helps more than another for a learner (calibration) | 18 |

---

# 14. Anti-patterns explicitly rejected

- a form chosen for the learner, or a hidden ranking,
- an AI alternative shown while a fitting written one exists,
- an AI alternative asked without the course's explanation when one is written,
- an AI alternative presented as verified, or without the way back,
- the learner's misconception memory sent to a provider,
- a written explanation shown above the learner's ceiling,
- the task's own item used as the "different example",
- a rule changed without raising the instructions version.

---

# 15. 14C acceptance contract

1. `ALEX-v0` is the accepted alternative-explanation behaviour.
2. A closed form vocabulary, each form traced to an accepted source; the tutor never writes a form that needs learner state.
3. The learner chooses (user decision); written first, otherwise AI (user decision).
4. An AI alternative is grounded in the course's own explanation when one exists, labelled unverified, and the way back is always offered.
5. 14A's rules hold unchanged; its validator is narrowed for exactly the four additions.
6. No schema change; one content-port refinement.
7. Nothing is claimed that was not run; T6 was not run.
8. Independent 14C QA passes and the full `validate_*.py` sweep passes.

---

# 16. Handoff after acceptance

If accepted, 14C becomes `ALEX-v0 / D-107`.

Next numbered step: **14D — Kod değerlendirme**. It must receive a fresh PRE-STEP and explicit user approval before execution.

---

**15C note (2026-10-03, `D-114`):** an explanation's text is read by `PackageFormat.unescape` (a backslash-n is a line break; a doubled backslash is one backslash), so a written lesson can show C code. `E14C-09_line_breaks` was narrowed to the new reader. Details: `docs/C_FOUNDATIONS_CONTENT_SPEC.md`.
