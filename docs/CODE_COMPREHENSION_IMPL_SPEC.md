# AI-Generated Code Comprehension Implementation Specification — ACCX-v0

**Stage step:** 14E — AI-generated code comprehension check  
**Status:** ACCEPTED — independent 14E QA PASS  
**Decision:** `D-109`  
**Model:** `ACCX-v0 — AI-Generated Code Comprehension`  
**Behaviour it implements:** `2D` §4 (`generated_or_copied` is never direct production evidence), §6, §8 (scenarios A and E), §9 (comprehension check is not production check; the recheck matches the Objective's behaviour), §17 (false-positive guardrails 1 and 3); `V1_SUCCESS_CRITERIA` SC-011 (AI help is no mastery shortcut) and SC-012 (a comprehension check after AI help); `TRUX-v0` §9 (provenance asked, never inferred); `AIV-v0` (AI-written assessment content is not trusted); `TUTX-v0 / D-105`  
**Persistence:** schema unchanged — written checks are content, served by the content port; answers are recorded by the existing evidence pipeline  
**Boundaries:** `MSBX-v0 / D-078` — one content-port refinement (`comprehensionChecksFor`); ports remain the four plus `TutorPort` (`D-105`)

## 1. Purpose

14D decided what a test report proves about code. It cannot say whether the learner understands code they did not write — and `2D` is explicit that running such code proves nothing about them. 14E decides **how understanding of that code is checked, and what the check may prove**.

It answers one question:

> **AI'ın yazdığı bir kodu öğrenci anlıyor mu — ve bu neyi kanıtlar?**

Primary invariant:

> **Running code someone else wrote proves nothing about the learner; explaining it proves understanding, not production.** When the learner submits code an AI or another source wrote, or that a shown solution largely gave them, a comprehension check is offered right after and may be skipped. Written checks come first and are judged by their answer key; only where none is written does the tutor ask about the learner's own code, and that is practice, never evidence. A correct answer is evidence of the kind its author declared — never of the item's own production — and code the learner did not write opens a fresh independent check of the production Objective.

---

# 2. What was found before writing comprehension code

- **AI-written code opened no recheck.** `AssistanceInterpretation` (14A) turned `generated_or_copied` into `practice_only`, which the mastery engine excludes and forgets; `2D` §8 scenario A says the target Objective gets an independent recheck when the AI wrote the code.
- **No comprehension check existed**, written or asked; SC-012 requires at least one suitable path after AI help.
- **Nothing kept a comprehension answer apart from production.** Nothing stopped a check from declaring the item's own production evidence type (`2D` §17 guardrail 3).

---

# 3. User decisions (2026-10-02)

1. **Written first, otherwise the tutor as practice.** Course-written checks with an answer key; where none is written, the tutor may ask about the learner's own code — practice only, no evidence (AI-written questions are not trusted assessment content).
2. **Right after the submission, optional.** Offered in the same flow; skipping is not a wrong answer.
3. **AI-written code opens an independent recheck.** `generated_or_copied` on a task that measures the learner is `requires_independent_recheck`, not `practice_only`; in a teaching task, which never measured independence, it stays practice.

---

# 4. Scope boundary

## 4.1 14E decides

- when a comprehension check is offered, and from which source,
- the closed kinds of comprehension question,
- written checks as content and how an answer is judged,
- that a comprehension answer is never production evidence,
- the tutor's `check_understanding` request,
- the independence of work the learner says was generated or copied,
- what is said around the check.

## 4.2 14E does not decide

- the checks themselves (15),
- the production recheck itself — the existing mastery engine's unresolved recheck and the planner own it,
- the adapter's prompt and call site (14G), open-ended evaluation (14F),
- rendering the check and calling it from the app (16D),
- any mastery, weakness, retention or planner rule.

No score, threshold, weight or count is introduced.

---

# 5. When it is offered (`Comprehension.isOffered`, `offer`)

Only when the learner did not write the code alone — by their own account or by the help they were shown: provenance `generated_or_copied` or `mixed_authorship`, or `H3`/`H4` help with the target before the answer froze. Not for their own work, help with it (`H1`/`H2`), an unknown origin (it accuses no one), help after the answer froze, or support around the target. Nothing is inferred from how the code looks. Written checks for the item version first; only where none exists, the tutor's practice.

---

# 6. Kinds and written checks (`ComprehensionKind`, `ComprehensionCheck`)

`2D` §9's comprehension questions: `line_purpose` ("Bu satır neden gerekli?"), `removal_effect` ("Bunu kaldırırsak ne olur?"), `state_effect` ("`*p` neyi değiştiriyor?"), `find_the_bug` ("hatayı bul"). "Apply the same logic with other variables" is production, not comprehension — it is the recheck.

A written check is `comprehension.<namespace>.<slug>`, pinned to one item version, speaking for one Objective the item targets, with two to four choices (`a`–`d`), the answer among them, and an authored evidence type that is **never the item's own** (`2D` §8 scenario E). Strict `[comprehension_check]` package section; `ContentPort.comprehensionChecksFor(item)`.

---

# 7. What an answer proves (`Comprehension.answer`)

Judged by the key: the right choice → `met`, another → `not_met`, nothing chosen → skipped and nothing written. Result `Verified`, evaluator `deterministic/comprehension_key@<check>@v<N>`, recorded by the existing pipeline under the check's own evidence type, so the mastery engine counts it only where the Objective lists that type as evidence — never as production.

---

# 8. The tutor's practice (`check_understanding`, `CheckUnderstanding.practice`)

A sixth tutor intent (declared extension of `TUTX-v0`): only after the answer froze and only with the submitted code. Without the learner's answer, the tutor asks exactly one short question about that code; with it (and the question it answers), it says what the answer gets right and misses, then explains — never a grade (rule 16). `<check_question>` and `<learner_answer>` are material like every section. Instructions `tutor_instructions/3`; the reply schema's intent enum grew, so its id is `tutor_reply/2`. 14A's other rules hold: after the answer froze the help is recorded at `H4` and a shown solution is an exposure. It is offered only where nothing is written, and it is never evidence.

---

# 9. Independence of AI-written work (`AssistanceInterpretation`)

| When | Class (14A) | Class (14E) |
|---|---|---|
| H3/H4 with the target before the answer froze | requires_independent_recheck | requires_independent_recheck |
| teaching task | practice_only | practice_only (checked before provenance) |
| provenance `generated_or_copied` | practice_only | **requires_independent_recheck** |
| H1/H2 with the target; assisted or mixed authorship | assisted | assisted |
| otherwise | independent | independent |

The mastery engine already opens an unresolved recheck for such rows and the planner schedules it with a fresh, unseen variant; nothing about that is changed. It is not a penalty and deletes no mastery.

---

# 10. What is said (`ComprehensionPresentation`)

The offer names what happened without accusing anyone and says skipping is fine; an answer is judged plainly with the right choice shown, always with **"Bu bir anlama kontrolü: kodu açıklayabildiğini gösterir, kendin yazabildiğini değil."**; the tutor's question is labelled **"pratik içindir, kanıt sayılmaz"**; when a recheck opens, **"Bu bir ceza değil."** No score, count or percentage.

---

# 11. Changes to accepted code

- `TutorIntent.CHECK_UNDERSTANDING`; `TutorAsk`/`TutorRequest` `checkQuestion`, `learnerAnswer`; `TutorRules.prepare` rules for them; `TutorInstructions` rule 16, `tutor_instructions/3`, `tutor_reply/2`, two sections; `TutorPresentation` label.
- `AssistanceInterpretation`: the teaching-task rule moves before provenance, and `generated_or_copied` is `requires_independent_recheck` (user decision).
- Narrowed gates (declared in the contract): 14A's `E14A-02_intents`, `E14A-05_message_sections`, `E14A-06_version`, `E14A-07_interpretation_order`; 14C's `E14C-07_version_raised`, `E14C-07_reply_schema_unchanged`, `E14C-07_canonical_is_material`. Each reads the extension from 14E's contract, so only exactly this change passes.

---

# 12. Verification

- Suites: `ComprehensionFactsTest`, `AssistanceInterpretationTest`, `TutorInstructionsTest` (model), `CheckUnderstandingTest` (application, incl. recording through `RecordEvidence`), `ComprehensionPresentationTest`, `PackageFormatTest`.
- Validator `tools/validate_code_comprehension.py`, reading `2D` §4/§8/§9/§17 and SC-011/SC-012 against the Kotlin, with its own mutation test.
- Runs: T1, T2, T3, T5 and both builds — all PASS.
- **Mutation:** see the contract's `mutation_results`; only the 14E suites run; a compile failure is not a detection.
- **Not run: T6.** Nothing in the app offers the check yet (16D).

---

# 13. Open loops

| Loop | Owner |
|---|---|
| written comprehension checks for real tasks | 15 |
| rendering the check; calling `CheckUnderstanding` from the app | 16D |
| the adapter's prompt and call site for `check_understanding` | 14G |
| open-ended (free-text) comprehension answers | 14F |
| whether tutor-written questions could ever be trusted (calibration) | 18 |

---

# 14. Anti-patterns explicitly rejected

- running code someone else wrote counted as the learner's production,
- a comprehension answer counted as production evidence,
- a check offered on the learner's own work, or on suspicion,
- a skipped check counted as wrong,
- a tutor-written question recorded as evidence, or offered while a written one exists,
- AI-written code left as practice that measures nothing,
- a recheck presented as a penalty.

---

# 15. 14E acceptance contract

1. `ACCX-v0` is the accepted comprehension-check behaviour.
2. Written first, otherwise tutor practice; right after, optional; AI-written code opens a recheck (user decisions).
3. A comprehension answer is never evidence of the item's own production.
4. The tutor's question is never evidence.
5. No schema change; one content-port refinement; 14A and 14C gates narrowed for exactly the declared extension.
6. Nothing is claimed that was not run; T6 was not run.
7. Independent 14E QA passes and the full `validate_*.py` sweep passes.

---

# 16. Handoff after acceptance

If accepted, 14E becomes `ACCX-v0 / D-109`.

Next numbered step: **14F — Açık uçlu cevap değerlendirme**. It must receive a fresh PRE-STEP and explicit user approval before execution.
