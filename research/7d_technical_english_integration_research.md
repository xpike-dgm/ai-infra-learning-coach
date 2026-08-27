# 7D Research — Technical English Integration

**Step:** 7D — Teknik entegrasyon  
**Date:** 2026-08-27  
**Role:** Research input; not product truth by itself

## Research question

How should Technical English be embedded inside Python/C/Linux/Git/systems/GPU/inference tasks without turning English into a global technical gate, contaminating technical evidence, or making bilingual support a mastery shortcut?

## Canonical project constraints reviewed first

- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036
- `docs/TASK_TAXONOMY_SPEC.md` — D-034
- `docs/QUESTION_BANK_SPEC.md` — QAB-v0 / D-047
- `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md` — AIV-v0 / D-048
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0 / D-031
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0 / D-032
- `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` — WLRM-v0 / D-061
- `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md` — EED-v0 / D-063
- `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md` — TECP-v0 / D-064
- `docs/DAILY_ENGLISH_COMPONENT_SPEC.md` — DECP-v0 / D-065

These already establish the non-negotiable product invariant: unknown English cannot become a hidden prerequisite for a task whose construct is technical capability.

## External authoritative sources

### 1. Council of Europe — Purposes of the CEFR
Source: https://www.coe.int/en/web/common-european-framework-reference-languages/uses-and-objectives

Relevant findings:
- CEFR is a descriptive/flexible tool, not a ready-made curriculum.
- Learning/teaching objectives should be adapted to learner needs and specific contexts.
- CEFR explicitly supports plurilingual profiles rather than requiring one-language-only instructional behavior.

Implication for 7D:
- No universal Turkish/English ratio should be invented.
- Bilingual/plurilingual scaffolding can be legitimate when it preserves the intended construct and learner access.

### 2. Council of Europe — Mediation
Source: https://www.coe.int/en/web/common-european-framework-reference-languages/mediation

Relevant findings:
- Mediation includes making meaning and enabling communication across linguistic barriers.
- Mediation may occur across languages or varieties.
- Receptive, productive, interactive and mediation activity can be combined in authentic tasks.

Implication for 7D:
- Cross-language support is not inherently a defect.
- Translation/gloss/explanation can support technical learning, but assistance must be separated from independent English evidence.

### 3. Council of Europe — CEFR Companion Volume / plurilingual expansion
Sources:
- https://www.coe.int/en/web/common-european-framework-reference-languages/cefr-companion-volume-and-its-language-versions
- https://www.coe.int/en/web/common-european-framework-reference-languages/introduction-and-context

Relevant findings:
- The Companion Volume extends descriptors for mediation, online interaction and plurilingual/pluricultural competence.
- CEFR levels exist alongside a richer analysis of communicative contexts, tasks and purposes.

Implication for 7D:
- Integration should remain task/construct-specific; a learner should not be assigned a broad English gate because a technical resource happens to be in English.

### 4. ALTE — Guidelines for Language for Specific Purposes Tests
Sources:
- https://www.alte.org/Materials
- https://www.coe.int/en/web/common-european-framework-reference-languages/developing-tests-examining

Relevant findings:
- ALTE maintains specific guidance for Language for Specific Purposes (LSP) tests in addition to general language-test principles.
- Specific-purpose assessment must be designed around the intended target-use context while preserving normal validity principles.

Implication for 7D:
- Technical-English tasks should declare whether language itself is a target construct, merely a support medium, or part of a dual-target task.
- An authentic technical context does not automatically make every technical outcome English evidence.

### 5. ETS — Guidelines for the Assessment of English Language Learners
Source: https://www.ets.org/pdfs/about/ell-guidelines.pdf

Relevant findings:
- A major validity threat in content-area assessment is construct-irrelevant variance caused by language demands unrelated to the target content construct.
- A learner may possess the target content skill yet fail because they cannot access the English wording.

Implication for 7D:
- For technical-only assessment, required English load must be neutralized through localization/bilingual support or be shown already ready and non-contaminating.
- If undeclared language load plausibly causes a technical failure, that technical negative evidence is invalid/prerequisite-contaminated rather than a clean technical failure.

## Reconciliation with project architecture

External sources do not justify a new mastery model, a fixed English percentage, a fixed scaffold-fading schedule, or a global English prerequisite. The existing project contracts are stronger and more precise:

1. PRG-v0 already provides prerequisite readiness and contamination behavior.
2. QAB-v0 already separates task resource metadata from learner evidence.
3. GRE-v0 already requires objective-matched, prerequisite-valid, direct, verified, independent evidence.
4. DECP-v0 already keeps English inside the common planner/capacity pipeline.
5. TECP-v0 already limits B2+ to bounded extension evidence rather than a new mastery band.

Therefore 7D should add an **integration/attribution overlay**, not new Skill identities or a second planner.

## Recommended 7D model

Use four explicit integration modes:

1. `technical_only_localized`
   - Technical capability is the only target construct.
   - Turkish/bilingual language support is allowed as needed.
   - English cannot be a hidden hard gate and cannot receive mastery evidence from the task.

2. `technical_with_english_exposure`
   - Technical capability remains the target.
   - Authentic English strings/docs/errors may be shown for realistic exposure/reinforcement.
   - Construct-essential English must be either already ready or safely scaffolded; technical evidence remains the primary attribution.

3. `dual_target_integrated`
   - One task intentionally targets at least one technical Objective and one English Objective.
   - Both target sets and both prerequisite sets are explicit.
   - Evidence attribution is component-specific; global task success/failure cannot broadcast to every target.

4. `english_primary_technical_context`
   - English is the target construct and technical context is the scenario.
   - Required specialist technical reasoning must be controlled/known/scaffolded so technical ignorance cannot masquerade as English failure.

## Scaffold policy recommendation

Allow support surfaces such as:
- Turkish instruction,
- bilingual parallel instruction,
- glossary/term gloss,
- non-target translation,
- chunking/simplified instruction wording,
- context explanation,
- non-answer-revealing examples.

But support must obey construct boundaries:
- If technical skill is target, language support may be generous as long as it does not solve the technical problem.
- If English skill is target, support may clarify non-target technical context but may not reveal the target English behavior without lowering evidence independence/use ceiling.
- Scaffold fading should be evidence-driven, not calendar-driven and not a fixed Turkish/English percentage.

## Integrated evidence recommendation

A single task may serve multiple LearningNeeds, but evidence must remain separate.

For each target component require:
- structurally essential behavior,
- separately observable output or action,
- prerequisite-valid interpretation,
- evaluator/rubric sufficient for that Objective,
- assistance/exposure compatible with the intended evidence class.

Examples:
- Correct C code + weak English bug description can yield valid technical positive evidence and English remediation need.
- Misread English instruction that prevents attempting the C behavior cannot be used as clean negative C evidence.
- Correct English reading of a CUDA paragraph cannot prove CUDA conceptual mastery unless the technical behavior itself is separately observed.

## Risks to guard

- **English global-gate creep:** requiring B1/B2 merely because docs are in English.
- **Construct contamination:** technical failure caused by language load.
- **Double counting:** one integrated completion marking both English and technical targets mastered.
- **Scaffold leakage:** translated/glossed answer accidentally becoming independent English evidence.
- **Technical-context contamination:** specialist domain ignorance causing false English failure.
- **Authenticity overreach:** using authentic vendor docs whose technical prerequisites exceed the learner's current state.
- **Fake precision:** fixed language ratio, fixed number of bilingual days, or fixed fading schedule without calibration.

## Research conclusion

**Recommendation: PASS for a 7D integration overlay with strict construct separation and component-level evidence attribution.**

The strongest design principle is:

> Choose language support to preserve the validity of the task's declared construct. Do not remove support merely to force English, and do not count supported language behavior as independent English mastery.

Confidence: high for construct-separation/fairness direction; medium for exact UI/scaffold presentation because production UX/content calibration belongs to later stages.