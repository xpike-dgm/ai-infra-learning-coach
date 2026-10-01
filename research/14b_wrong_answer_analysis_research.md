# 14B Wrong-Answer Analysis — Research & Decision Synthesis

**Stage step:** 14B — Yanlış analizi  
**Purpose:** Say truthfully what a wrong answer shows — which Objective, and what kind of mistake it may have been — without overstating either, and remember misconceptions only as far as the evidence allows.

## 1. Research-need decision

**No web research pass was needed.** How strong a misconception may become is fixed by accepted contracts: `WLRM-v0`'s misconception contract (an LLM may propose a hypothesis but cannot publish a confirmed misconception; memory guides remediation and item choice and sets no mastery; resolution needs evidence, not time) and its state contract and twelve attribution rules (`WLRX-v0` in code); `AIAX-v0` §5.1 (`misconception_hypotheses[]`, hypotheses never confirmed weakness); `SPWX-v0` (a hypothesis is never a deficiency); `LEARNING_BEHAVIOR_RULES` §5 and §14. The open input of `review.6g.misconception_taxonomy_expansion` — real learner errors — cannot be researched before the pilot; the review was therefore resolved by contract and owners, not by inventing labels. Two product questions went to the user (§4).

## 2. Canonical source set reviewed

- `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` §4–§8; `curriculum/decomposition/6g_weakness_remediation/misconception_contract.yaml`, `state_contract.yaml`, `remediation_strategies.yaml` (`strategy.misconception_contrast`), `review_queue.yaml`.
- `docs/WEAKNESS_REMEDIATION_IMPL_SPEC.md` and `WeaknessEngine` (13D).
- `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md` §4–§6; `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md` (14A).
- `docs/MASTERY_SIGNALS_SPEC.md` §10–§11; `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` §22–§32.
- `docs/PROGRESS_SKILL_UX_SPEC.md` (hypothesis presentation); `docs/LEARNING_BEHAVIOR_RULES.md` §5, §14.
- `docs/DOMAIN_DATA_MODEL_SPEC.md` §7.1 (`misconception/error tags`); `docs/GRANULARITY_NAMING_STANDARD.md` (canonical ids).

## 3. What was found

1. `evidence_event.misconception_tags` existed since 10D and nothing wrote it.
2. The Kotlin evaluation result had dropped `AIAX-v0`'s `misconception_hypotheses[]`.
3. No misconception memory, owner or escalation rule existed in code.
4. No catalog existed to name a label against; free LLM labels would have entered permanent memory under many names.

## 4. Positions taken

- **User decisions (2026-10-01):** a closed, curriculum-authored catalog (labels outside it are not stored); a hypothesis is shown only as an open question right after the wrong answer.
- **A label rides on the Objective's own attribution:** it moves exactly as far as the weakness engine's rule moves the Objective, capped by its source (`ai_proposed` stops at a hypothesis); no new threshold, count or weight.
- **Memory belongs to `WLRM-v0`'s family** — the misconception contract is its own — so no new engine family.
- **The analysis reads the stored attribution**, through one shared event builder, so what the learner is told cannot drift from what the engines decided.
- **`review.6g.misconception_taxonomy_expansion` resolved:** contract and catalog mechanism here; labels → 15; expansion from real errors → 18.

## 5. Explicitly not decided in 14B

Authored labels and deterministic answer-to-label keys (15); item choice and contrast content from the memory (15); the Progress view (16C); calling the analysis from the app (16D); escalation calibration (18C); code evaluation (14D); LLM evaluator prompts (14F). T6 was not run.
