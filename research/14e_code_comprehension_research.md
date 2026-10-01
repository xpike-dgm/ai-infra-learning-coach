# 14E AI-Generated Code Comprehension — Research & Decision Synthesis

**Stage step:** 14E — AI-generated code comprehension check  
**Purpose:** Decide how understanding of code the learner did not write is checked, and what such a check may prove.

## 1. Research-need decision

**No web research pass was needed.** The behaviour is fixed by accepted texts: `2D` §4 (`generated_or_copied` is never direct production evidence), §8 scenario A (the target Objective gets an independent recheck when AI wrote the code) and scenario E (explaining AI code is comprehension evidence, never production), §9 (the comprehension questions; the recheck matches the Objective's behaviour), §17 (guardrails 1 and 3); `V1_SUCCESS_CRITERIA` SC-011 and SC-012; `TRUX-v0` §9; `AIV-v0` (AI-written assessment content is not trusted). Three product questions went to the user (§4).

## 2. Canonical source set reviewed

- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md` §4, §6–§9, §14–§17.
- `docs/V1_SUCCESS_CRITERIA.md` SC-011, SC-012; `docs/MASTERY_SIGNALS_SPEC.md` §5.4, §12.
- `docs/DAILY_WORKING_FLOW_SPEC.md` §9 (provenance asked, never inferred).
- `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md` (14A), `docs/CODE_EVALUATION_IMPL_SPEC.md` (14D).
- The code: `AssistanceInterpretation.kt`, `MasteryEngine` (unresolved recheck), `TutorFacts.kt`, `TutorInstructions.kt`.

## 3. What was found

1. `generated_or_copied` became `practice_only` and opened no recheck, against `2D` §8 scenario A.
2. No comprehension check existed (SC-012).
3. Nothing kept a comprehension answer apart from production evidence.

## 4. Positions taken

- **User decisions (2026-10-02):** written checks first, otherwise the tutor as practice with no evidence; offered right after the submission and skippable; AI-written code opens an independent recheck.
- **The four kinds are `2D` §9's comprehension questions**; "apply the same logic again" is production and is the recheck itself, owned by the mastery engine and the planner.
- **Written checks have an answer key** (two to four choices), so they are judged deterministically; free-text answers wait for 14F.
- **A check's evidence type is authored and never the item's own**; the mastery engine's existing direct-evidence profile then decides what it counts for.
- **The tutor's question is a sixth, declared intent** (`check_understanding`), only after the answer froze; the reply schema id was raised because its intent enum grew.
- **The teaching-task rule moved before provenance**, so a teaching task stays practice while measured work the learner did not write opens a recheck.

## 5. Explicitly not decided in 14E

The checks themselves (15); rendering and calling from the app (16D); the adapter prompt (14G); free-text answers (14F); trusting tutor-written questions (18). T6 was not run.
