# 7B Research — Technical English CEFR Alignment

**Adım:** 7B — A1/A2/B1/B2+ teknik hedefleri  
**Tarih:** 2026-08-27  
**Rol:** Research input; canonical product behavior değildir. Manager reconciliation sonrası 7B spec belirleyicidir.

## Research question

Stage 6 D01 Technical English graph'ındaki 15 canonical Skill, CEFR 2020 Companion Volume ve language-for-specific-purposes assessment ilkeleriyle nasıl hizalanmalı; bunu yaparken mastery state, technical prerequisite ve certification iddiaları nasıl korunmalı?

## Primary / authoritative sources

1. Council of Europe — CEFR Companion Volume (2020)  
   https://rm.coe.int/common-european-framework-of-reference-for-languages-learning-teaching/16809ea0d4

2. Council of Europe — CEFR Descriptors  
   https://www.coe.int/en/web/common-european-framework-reference-languages/cefr-descriptors

3. Council of Europe — Purposes / uses of the CEFR  
   https://www.coe.int/en/web/common-european-framework-reference-languages/uses-and-objectives

4. Council of Europe — Tests and examinations  
   https://www.coe.int/en/web/common-european-framework-reference-languages/tests-and-examinations

5. Council of Europe — Online interaction  
   https://www.coe.int/en/web/common-european-framework-reference-languages/online-interaction

6. Council of Europe — CEFR levels / profile orientation  
   https://www.coe.int/en/web/common-european-framework-reference-languages/level-descriptions

7. ALTE — Guides and reference materials / Guidelines for the Development of Language for Specific Purposes Tests  
   https://www.alte.org/Materials

## Findings

### R7B-01 — CEFR is a reference framework, not a ready-made technical-English curriculum

Council of Europe explicitly frames CEFR as a common reference basis and states that it must be interpreted/adapted for specific contexts. Therefore AI Infra Learning Coach should not import a generic exam syllabus and call it the Technical English curriculum.

**Design consequence:** D01 canonical Skills remain the capability identity. CEFR becomes alignment/progression metadata around those Skills.

### R7B-02 — A language profile can be uneven across activities

CEFR levels are expressed through descriptor scales across reception, production, interaction, mediation and communicative competences. Council of Europe materials also present profile-oriented uses rather than requiring a single uniform level for every activity.

**Design consequence:** learner source of truth remains Skill/Objective evidence. A single broad `English B1` state is not canonical.

### R7B-03 — Pre-A1 and plus levels exist, but 7B target bands do not need to turn them into new learner states

The 2020 Companion Volume includes Pre-A1 on many scales and supports finer plus-level distinctions such as B2+. Pre-A1 is useful for the zero-entry bridge; B2+ is useful as a professional target reference.

**Design consequence:**
- Pre-A1 may appear as entry/bridge context but 7B's named target bands stay A1/A2/B1/B2+.
- B2+ is not a new mastery algorithm or a synthetic score.

### R7B-04 — Technical English is language for a specific purpose

ALTE's LSP guidance treats specific-purpose testing as normal language-test development plus explicit attention to the target-use domain and its tasks. Technical knowledge must not accidentally become the construct unless intentionally part of the claim.

**Design consequence:** EED-v0's construct-contamination guard is retained. Programming/CUDA/Linux knowledge cannot become hidden evidence for English level.

### R7B-05 — Online interaction matters for professional engineering language use

The CEFR Companion Volume adds online interaction descriptors, including collaborative/transactional exchanges, asking for clarification and working toward shared tasks. This fits issue/PR/chat/documentation workflows better than a school-only grammar ladder.

**Design consequence:** `ask_clarifying_technical_question` and selected professional writing/navigation capabilities may link to online interaction scales.

### R7B-06 — A CEFR link must not be overstated as certification

Council of Europe provides guidance/tools but does not certify the quality of a product's claimed link to CEFR. Examination quality and fairness remain the responsibility of the test provider / implementing system.

**Design consequence:** current product wording must be `CEFR-aligned Technical English profile/target`, not `CEFR-certified` and not an official CEFR examination result.

### R7B-07 — Current D01 semantics are strongest for A1→B1 text-first capability

The 15 accepted D01 Skills are primarily label/phrase/sentence comprehension, instructions/errors/documentation, short technical notes, basic bug description, simple process explanation and clarification. Their literal semantic scope is naturally foundation-to-independent technical usage. Several names explicitly contain `simple` or `basic`.

**Manager interpretation to validate in 7B:**
- A1: core receptive building blocks,
- A2: straightforward technical operation/navigation + short note,
- B1: independent definitions/procedures/basic issue communication/process explanation/clarification,
- B2+: professional evidence-depth extension only where the existing Skill semantics can honestly support it.

B2+ must not silently turn `read_simple_terminal_error_fragments`, `write_basic_bug_description` or `explain_simple_technical_process` into broader advanced capabilities.

## Recommended 7B invariants

1. `cefr_alignment != mastery_state`.
2. `overall_general_english_cefr_claim = forbidden` for the current text-first D01 graph.
3. Every 15 D01 Skill receives exactly one base technical anchor band among A1/A2/B1.
4. Hard-prerequisite band ordering is monotonic: target cannot be anchored below a hard prerequisite.
5. B2+ is an explicit professional extension envelope, not a hidden Skill rewrite.
6. A B2+ task variant may target an existing Skill only if it stays inside that Skill's semantic capability boundary.
7. Plain `English B1/B2` certification-style labels are forbidden; learner-facing text must qualify the result as Technical English / contextual alignment.
8. CEFR alignment must not create English→technical global hard gates.
9. No new numeric threshold, weighting or CEFR conversion formula is invented.
10. Future content authoring that needs genuinely broader language capability must trigger normal GNS/KGC capability review rather than overload an existing Skill.

## Research-role conclusion

The evidence supports a **context-adapted, profile-based, text-first Technical English progression overlay**. It does not support treating the 15 D01 Skills as a full general-English CEFR certification battery. The safest 7B model is therefore a cumulative A1→A2→B1 base map plus a bounded B2+ professional evidence-depth extension, while preserving EED/GRE/PRG/WLRM behavior unchanged.
