# 14C Alternative Explanation — Research & Decision Synthesis

**Stage step:** 14C — Alternatif anlatım  
**Purpose:** Decide how "explain it another way" works: which forms exist, where each comes from, and how an AI-written alternative is kept from contradicting the course.

## 1. Research-need decision

**No web research pass was needed.** The forms are fixed by accepted texts — `LEARNING_BEHAVIOR_RULES` §9 lists the ways the method changes when learning does not land (simpler explanation, different example/analogy, worked example, a short return to a prerequisite, a personal AI explanation) and §12 requires a verified written core that AI may vary but never re-scope; `WLRM-v0`'s remediation strategies name the explanation-shaped ones (`targeted_reteach`, `worked_example`, `state_trace_reconstruction`, `prerequisite_refresh`, `misconception_contrast`). `AIAX-v0` §11 decides what may leave the device. Two product questions went to the user (§4).

## 2. Canonical source set reviewed

- `docs/LEARNING_BEHAVIOR_RULES.md` §9, §12, §16.
- `curriculum/decomposition/6g_weakness_remediation/remediation_strategies.yaml`; `research/6h_external_research_ai_report.md` (expertise-aware guidance fading).
- `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md` (14A), `docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md` (14B).
- `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md` §4, §11; `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md` §5.4.

## 3. What was found

1. `explain_differently` carried no form.
2. No place existed for written alternative explanations.
3. An AI alternative could not be grounded in the course's own explanation.
4. A contrast with the learner's own misconception would need learner state to leave the device.

## 4. Positions taken

- **User decisions (2026-10-01):** the learner chooses the form from a menu; written explanations first, the tutor only where none fits.
- **Seven forms, five the tutor may write.** A misconception contrast is written-only (learner state stays on the device); the course's explanation is not an alternative.
- **Grounding is attached by the use case**, not left to the screen: whenever a course explanation is written, an AI alternative is asked with it and told never to contradict it; the instructions version is raised to 2.
- **Written explanations are content**, served by the content port and pinned by version; no schema change.
- **Guidance fading stays outside the tutor** (14A): no automatic form choice; calibration of which forms help is 18's.

## 5. Explicitly not decided in 14C

Written explanation content (15); rendering and calling from the app (16D); the adapter call site (14G); calibration of forms (18). T6 was not run.
