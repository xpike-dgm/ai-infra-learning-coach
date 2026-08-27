# 7E Research — Technical English Mastery / Profile Reporting

**Step:** 7E — English mastery  
**Role:** Research input; not automatically canonical product truth  
**Date:** 2026-08-27

## Research question

How should AI Infra Learning Coach present learner-facing Technical English mastery and CEFR-aligned progress without inventing a second mastery algorithm, overstating a general-English CEFR level, hiding uneven capability profiles, or allowing assisted / contaminated evidence to masquerade as independent proficiency?

## Authoritative sources reviewed

### Council of Europe — CEFR framework / levels / descriptors

1. CEFR Tests and Examinations  
   https://www.coe.int/en/web/common-european-framework-reference-languages/tests-and-examinations

   Relevant direction: CEFR can support reporting **profiles across aspects of language proficiency** through common reference levels and descriptors.

2. CEFR Levels  
   https://www.coe.int/en/web/common-european-framework-reference-languages/level-descriptions

   Relevant direction: the global scale is intentionally a compact orientation device; the more detailed self-assessment grid exists to profile main language skills for practical purposes.

3. CEFR Companion Volume 2020  
   https://www.coe.int/en/web/common-european-framework-reference-languages/cefr-companion-volume-and-its-language-versions
   PDF: https://rm.coe.int/cefr-companion-volume-with-new-descriptors-2020/16809ea0d4

   Relevant direction: CEFR explicitly supports proficiency **profiles**, including uneven profiles across communicative activities. The Companion Volume also includes plus levels and broadened activity categories such as mediation and online interaction.

4. Purposes / contextual use of the CEFR  
   https://www.coe.int/en/web/common-european-framework-reference-languages/uses-and-objectives

   Relevant direction: CEFR does not provide a ready-made solution for every use; it must be interpreted and adapted to the target context and learner group.

5. Relating examinations to the CEFR  
   https://www.coe.int/en/web/common-european-framework-reference-languages/relating-examinations-to-the-cefr

   Relevant direction: the Council of Europe does **not** validate an implementer's claimed CEFR linkage. Users of the framework are responsible for coherent, realistic and evidence-supported interpretation.

6. CEFR descriptors  
   https://www.coe.int/en/web/common-european-framework-reference-languages/cefr-descriptors

   Relevant direction: CEFR reference levels are expressed through structured can-do descriptors for distinct categories rather than through a single undifferentiated language score.

### ALTE — language assessment and specific-purpose assessment

7. ALTE Guides and Reference Materials  
   https://www.alte.org/Materials

   Relevant direction: ALTE publishes dedicated Language for Specific Purposes guidance and a current language-test-development manual. This supports keeping a technical-purpose construct explicit rather than silently generalising a narrow technical profile into a broad language qualification.

### ETS — score interpretation / profile validity

8. ETS research on analytic rating profiles  
   https://www.ets.org/research/policy_research_reports/publications/article/2007/hnkq.html

   Relevant direction: score-profile interpretation is itself a construct-validity question. A composite can coexist with analytic dimensions, but claims need evidence for the interpretation intended.

9. ETS TOEIC validity and fairness  
   https://www.ets.org/toeic/research/validity-fairness.html

   Relevant direction: score interpretations should mean what they claim to mean and reflect the real-world abilities intended by the assessment.

## Reconciliation with existing canonical project contracts

The external sources do **not** justify a new English-only probability score, fixed pass percentage, or official CEFR certification claim.

Existing project contracts are already stronger than a generic language score for mastery truth:

- `GRE-v0` owns Skill/Objective mastery.
- `RVR-v0` owns retention / review / verification state.
- `VDW-v0` may accept validated prior knowledge without lowering the mastery standard.
- `WLRM-v0` owns localized weakness/remediation.
- `TECP-v0` owns CEFR-aligned Technical English progression metadata.
- `TEIP-v0` owns construct-valid technical/English integration and contamination guards.

Therefore 7E should be a **derived learner-facing profile contract**, not another mastery engine.

## Research-backed design implications

### 1. Prefer a profile over one broad label

The learner may be strong at documentation navigation while still weak at basic bug-description production. A single broad score would hide that useful instructional difference.

Canonical recommendation:
- exact Skill/Objective state remains visible/traceable,
- A1/A2/B1 are qualified Technical English profile summaries,
- uneven profiles are first-class, not treated as an error.

### 2. A global band is only a derived summary

The existing TECP-v0 `highest_complete_base_band` rule is compatible with the CEFR's use as an orientation framework if the claim is explicitly scoped.

Allowed style:
- `Technical English — A2 base profile confirmed`
- `Technical English — B1 base profile verification due`

Forbidden style:
- `Your English level is B1`
- `Official CEFR B1`
- `CEFR-certified B1`

### 3. Review due is not skill loss

RVR-v0 already defines `review_due` as time-based verification need without negative evidence. 7E should preserve the mastered/base-profile claim and add a review signal rather than downgrade the learner.

### 4. Verification due should add uncertainty without instant erasure

RVR/GRE hysteresis says the first clean contradiction creates `verification_due`; it does not instantly delete mastery. Learner-facing output should therefore preserve historical confirmation while clearly marking that the current claim requires re-checking.

A second clean failure that actually causes GRE gates to fail can move the affected Skill into remediation and then change the current complete-base-band summary.

### 5. Assisted performance must not be labelled independent mastery

H1–H4, answer-revealing scaffold, model exposure, or provisional evaluator results are useful for learning but cannot become independent English mastery. 7E should distinguish support-dependent progress from independently confirmed capability.

### 6. Technical task language cannot silently create English mastery

TEIP-v0 remains binding:
- technical-only localized tasks create no English mastery attribution,
- authentic English exposure is not automatically evidence,
- integrated English evidence requires explicit target + separate observability + prerequisite validity + normal GRE/QAB/AIV gates.

### 7. B2+ remains evidence depth, not a completed level

Current D01 has only four B2+ extension-eligible capabilities. Therefore 7E should report named professional-extension evidence, not `B2+ complete`.

Example:
- `Professional extension evidence: documentation navigation, definition/constraint reading`

Not allowed:
- `Technical English B2+ achieved`

### 8. Profile claims need provenance/explainability

Every learner-facing summary should be explainable from:
- exact Skill/Objective states,
- mastery source (GRE or validated diagnostic pathway),
- retention state,
- assistance ceiling,
- last qualifying evidence / verification reason,
- CEFR anchor metadata.

The UI does not need to expose raw internal formulas, but it should be possible to explain why a claim exists.

## Rejected alternatives

### One numeric English percentage
Rejected because it would be compensatory and would obscure required Skill gaps. It would also duplicate GRE semantics.

### Average CEFR band
Rejected because averaging A1/A2/B1/B2+ into a number invents an unsupported psychometric scale and can misrepresent uneven profiles.

### Review-due downgrade
Rejected because time is not negative evidence under RVR-v0.

### Instant mastery deletion on first contradiction
Rejected because it violates GRE/RVR hysteresis.

### Overall B2+ completion badge
Rejected because current B2+ is a bounded extension on only four Skills, not a complete broad B2 construct.

### Calendar-based scaffold independence badge
Rejected because TEIP-v0 makes scaffold reduction evidence/task-validity driven and reversible.

## Recommended 7E acceptance direction

1. No new Skill, Objective, prerequisite edge, mastery score, threshold or CEFR certification claim.
2. Exact D01 15-Skill state remains the canonical English mastery truth.
3. Learner-facing Skill presentation is derived from GRE + RVR + prerequisite/remediation state.
4. Base-band summary is qualified Technical English only and preserves uneven detail.
5. `review_due` preserves mastery; `verification_due` exposes uncertainty without immediate deletion.
6. Confirmed remediation can suppress a currently-complete band while preserving historical achievement provenance.
7. Assisted/provisional/contaminated evidence cannot create independent-confirmed status.
8. B2+ is per-capability professional-extension evidence only; no aggregate completion state.
9. Technical-only task success does not update English mastery.
10. Profile claims must be traceable to exact underlying evidence/state and use non-overclaiming wording.

## Confidence / uncertainty

**Confidence: high** for the architectural direction because it follows both CEFR profile/context guidance and already accepted GRE/RVR/TECP/TEIP contracts.

**Deferred empirical questions:** whether learners understand specific labels, whether the profile presentation improves decisions, and any calibration of score-report wording or thresholds belong to later UX/pilot stages (8/16/18), not 7E.
