# 7A Research — English Entry Diagnostic

**Step:** 7A — Başlangıç ölçümü  
**Role:** Research input; manager reconciliation is the product decision.  
**Date:** 2026-08-27

## Research question

How should AI Infra Learning Coach determine a learner's starting English capability profile without turning the diagnostic into an easier mastery path, a single broad CEFR score, or a hidden technical-knowledge test?

## Primary / authoritative sources

1. **Council of Europe — CEFR Descriptors / Companion Volume (2020)**  
   https://www.coe.int/en/web/common-european-framework-reference-languages/cefr-descriptors  
   Relevant signal: CEFR proficiency is described through structured `can-do` descriptors across multiple categories rather than only one undifferentiated score.

2. **Council of Europe — Tests and Examinations**  
   https://www.coe.int/en/web/common-european-framework-reference-languages/tests-and-examinations  
   Relevant signal: assessment should support transparent/coherent interpretation and can report profiles across aspects of proficiency.

3. **Council of Europe / ALTE — Developing tests and examining**  
   https://www.coe.int/en/web/common-european-framework-reference-languages/developing-tests-examining  
   Relevant signal: test development should specify content, target relevant proficiency, and interpret performance in relation to real-world language use; the page also points to ALTE guidance for Language for Specific Purposes tests.

4. **Council of Europe — Uses and objectives of the CEFR**  
   https://www.coe.int/en/web/common-european-framework-reference-languages/uses-and-objectives  
   Relevant signal: CEFR is not a ready-made solution; it must be adapted to the target context and learner group.

5. **ETS — Evidence-Centered Design: The TOEIC Speaking and Writing Tests (2010)**  
   https://www.ets.org/research/policy_research_reports/publications/report/2010/itjx.html  
   Relevant signal: explicitly define what the assessment claims to measure and design task specifications/evidence that support those interpretations.

6. **ETS — TOEFL iBT Test Framework and Test Development (2020)**  
   https://www.ets.org/research/policy_research_reports/publications/periodical/2020/kgqf.html  
   Relevant signal: ECD requires explicit measurement claims and examination of the strength of evidence supporting those claims.

## Manager synthesis for this project

The project already has a granular D01 Technical English Skill graph. Therefore 7A should diagnose **that graph**, not invent a separate placement taxonomy.

The diagnostic should use an Evidence-Centered Design shape:

```text
claim about exact English Skill / Objective
→ evidence needed to support that claim
→ task family capable of eliciting that evidence
→ prerequisite / contamination validation
→ project GRE / VDW / WLRM evidence pipeline
```

### Accepted research implications

- Produce a **granular capability profile**, not a single English score.
- Keep final A1/A2/B1/B2+ alignment owned by **7B**. 7A may collect evidence that 7B later maps; it must not claim validated CEFR equivalence itself.
- Adapt the assessment to the project's real target domain: documentation, instructions, terminal/error fragments, short technical writing and clarification.
- Avoid construct-irrelevant difficulty: unknown programming/system knowledge must not be required merely to measure English.
- Use prerequisite-aware adaptive probing so downstream production is not treated as failed when its English prerequisites are unresolved.
- Keep self-report, prior certificates and confidence as contextual/routing information only.
- Positive diagnostic evidence may create mastery/coverage waiver only through existing **GRE-v0 + VDW-v0** gates; diagnostic is not a lower-standard shortcut.
- Negative evidence must pass the existing WLRM attribution/contamination guards before it becomes a confirmed learning need.
- Integrated tasks may support multiple claims only when each component is separately observable and attributable.
- No arbitrary new pass percentage, fixed item count or psychometric ability estimate is introduced in 7A. Calibration belongs to later pilot/calibration work.

## Project constraints preserved

- `docs/ENGLISH_FOUNDATION_RULES.md`: unknown grammar/vocabulary cannot be a hidden prerequisite; bilingual scaffold is allowed when the target remains observable.
- `VDW-v0`: diagnostic uses the same mastery gates as normal evidence.
- `PRG-v0`: hard prerequisite absence blocks only the dependent evidence path.
- `GRE-v0`: H0/direct/verified/prerequisite-valid evidence is required for independent mastery.
- `QAB-v0` / `AIV-v0`: unvalidated generated items/evaluations cannot become trusted mastery evidence.
- Stage 6 D01 canonical Skill identities are reused; 7A does not silently add or clone English Skills.

## Explicit handoff to 7B

7B must decide:

- CEFR descriptor alignment for the existing Technical English capability identities,
- whether the professional target requires any controlled graph migration/addition after CEFR activity coverage review,
- A1/A2/B1/B2+ technical target metadata,
- user-facing level/band reporting rules.

7A only produces the evidence/profile contract that makes that later alignment possible.
