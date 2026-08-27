# Technical English Integration Spec — TEIP-v0

**Adım:** 7D — Teknik entegrasyon  
**Durum:** CANDIDATE — QA + POST-STEP kapanışı bekliyor  
**Tarih:** 2026-08-27  
**Candidate model:** `TEIP-v0 — Technical English Integration Policy`  
**Candidate decision:** `D-066`

Bu belge Technical English'in Python, C, Linux/Git/Shell, systems, networking, distributed systems, GPU/CUDA, inference ve professional workflow task'larının içine **construct-valid** biçimde nasıl yerleştirileceğini tanımlar.

Ana ilke:

> **Bir task'ın dili ile ölçtüğü capability aynı şey değildir. Technical construct ölçülüyorsa English erişim bariyeri neutralize edilir; English construct ölçülüyorsa language target ve prerequisite'leri açıkça declared olur.**

İkinci ilke:

> **Integrated task tek bir global PASS/FAIL üretip bütün English + technical hedeflere broadcast edemez. Her Objective attribution separately observable, prerequisite-valid ve evaluator-sufficient olmalıdır.**

Üçüncü ilke:

> **Scaffold azaltma takvime veya sabit Türkçe/İngilizce oranına göre değil, target construct + learner evidence + task validity'ye göre yapılır.**

---

# 1. Bağlayıcı girdiler

TEIP-v0 aşağıdaki accepted contract'ları tüketir ve onları değiştirmez:

- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036
- `docs/TASK_TAXONOMY_SPEC.md` — D-034
- `docs/PRIORITY_POLICY_SPEC.md` — PBR-v0 / D-035
- `docs/QUESTION_BANK_SPEC.md` — QAB-v0 / D-047
- `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md` — AIV-v0 / D-048
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0 / D-031
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0 / D-032
- `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` — WLRM-v0 / D-061
- `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md` — EED-v0 / D-063
- `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md` — TECP-v0 / D-064
- `docs/DAILY_ENGLISH_COMPONENT_SPEC.md` — DECP-v0 / D-065
- `research/7d_technical_english_integration_research.md`

7D:
- yeni English veya technical Skill/Objective identity üretmez,
- Stage 6 prerequisite graph'ını değiştirmez,
- English'i technical route için global hard gate yapmaz,
- CEFR bandını technical eligibility gate yapmaz,
- GRE/RVR/PRG threshold veya state modelini değiştirmez,
- fixed Turkish/English ratio, fixed fading day count veya fixed integrated-task quota yaratmaz,
- learner-facing final English mastery/CEFR display behavior'ını tanımlamaz → 7E.

---

# 2. Construct separation

Her integrated/technical-context task önce şu soruya cevap verir:

```text
Bu task gerçekte neyi ölçüyor/öğretiyor?
```

TEIP-v0 iki construct family'yi ayrı tutar:

```text
technical_construct
english_construct
```

Bir task ikisini birden hedefleyebilir; fakat bu durumda iki target seti ve iki evidence attribution ayrı olmak zorundadır.

## 2.1 Medium != target

Aşağıdakiler canonical olarak farklıdır:

```text
prompt language
resource language
instruction language
scaffold language
target construct
```

Bir Linux task'ının man page'i English olduğu için task otomatik English assessment değildir.
Bir English reading task'ında pointer örneği geçtiği için task otomatik C mastery assessment değildir.

---

# 3. Canonical integration modes

Machine-readable source:

`curriculum/english/7d_technical_integration/policy.yaml`

7D exactly dört mode kullanır.

## 3.1 `technical_only_localized`

Amaç: yalnız technical capability öğretmek/ölçmek.

Kurallar:
- `technical_target_objective_ids` non-empty.
- `english_target_objective_ids` empty.
- English mastery target değildir.
- Prompt/instruction Turkish veya bilingual olabilir.
- Authentic English token/string gösterilebilir fakat target çözüm için gereken language demand ya already-ready ya da scaffolded/translated olmalıdır.
- Task-level English hard gate oluşturulmaz.
- English performansı bu task'tan positive/negative mastery evidence alamaz.

Örnek:
- C pointer dereference coding task'i Türkçe yönergeyle verilir.
- Compiler error text English olabilir; task'ın amacı pointer fix ise error wording'in gerekli anlamı bilingual support ile erişilebilir yapılır.

## 3.2 `technical_with_english_exposure`

Amaç: technical capability primary kalırken learner'ı gerçek English teknik materyalle kontrollü biçimde karşılaştırmak.

Kurallar:
- technical target non-empty.
- English target optional reinforcement refs olabilir fakat task'ın technical eligibility'sini yükselten global gate değildir.
- Authentic docs/error/README/man-page fragment kullanılabilir.
- Construct-essential English load için iki yol vardır:
  1. required English Skill açıkça ready'dir, veya
  2. language demand support ile neutralize edilir.
- English exposure tek başına English mastery evidence değildir.
- Eğer separately observable, target-declared English Objective yoksa English state güncellenmez.

Örnek:
- Linux `grep` task'ında kısa English man-page option fragment'i gösterilir; glossary ile option meaning erişilebilir tutulur.
- CUDA API docs'tan kısa signature/parameter snippet'i gösterilir; technical task API kullanımını ölçüyorsa ağır prose English'i glossary/translation ile neutralize edilebilir.

## 3.3 `dual_target_integrated`

Amaç: aynı task içinde hem technical hem English Objective'i bilinçli biçimde hedeflemek.

Zorunlu contract:
- en az bir technical target Objective,
- en az bir English target Objective,
- her iki family için explicit required Skill refs,
- component-specific rubric/evaluator path,
- component-level assistance ceiling,
- component-level evidence result.

Global task result:

```text
overall_task_completion != automatic_technical_evidence
overall_task_completion != automatic_english_evidence
```

Örnek:
- Kullanıcı bir C bug'ını düzeltir ve sonra kısa English bug description yazar.
- Correct code technical positive evidence olabilir.
- Bug description independently English Objective'e göre değerlendirilebilir.
- English description zayıf diye doğru C code negative technical evidence olmaz.
- C bug'ını anlayamadığı için description yanlışsa English negative evidence ancak English construct ayrıca yorumlanabilir kalıyorsa kullanılabilir.

## 3.4 `english_primary_technical_context`

Amaç: English Skill'i gerçek teknik context içinde öğretmek/ölçmek.

Kurallar:
- English target Objective non-empty.
- Technical context target değilse specialist technical reasoning required edilmez.
- Context için gereken technical knowledge ya learner'da ready olmalı ya da answer-revealing olmayan context scaffold ile neutralize edilmelidir.
- Technical ignorance English failure'a masquerade edemez.
- Technical mastery evidence varsayılan olarak none'dır.

Örnek:
- Basit terminal error fragment'inden `file not found` anlamını çıkarmak.
- Task filesystem diagnosis uzmanlığı istemez; language signal'i ölçer.

---

# 4. Task integration metadata contract

TEIP-v0 physical DB schema değildir; 9C implementation'a semantic contract sağlar.

Bir integration-aware TaskCandidate en az şu semantics'i temsil edebilmelidir:

```text
TechnicalEnglishIntegrationContract
- integration_mode
- technical_target_skill_ids[]
- technical_target_objective_ids[]
- english_target_skill_ids[]
- english_target_objective_ids[]
- technical_required_skill_ids[]
- english_required_skill_ids[]
- language_load_role
- authentic_resource_refs[]
- instruction_language_mode
- scaffold_surfaces[]
- construct_essential_language_segments[]
- component_attribution[]
- assistance_ceiling_by_component
- contamination_reason_codes[]
- integration_policy_version
```

## 4.1 `language_load_role`

Controlled vocabulary:

```text
incidental
supporting
construct_relevant
```

- `incidental`: English text exists but target behavior does not depend on understanding it.
- `supporting`: some language meaning is needed, but may be scaffolded without changing the technical construct.
- `construct_relevant`: English behavior is itself an explicit target component.

`construct_relevant` does not automatically mean dual-target; `english_primary_technical_context` also uses it.

---

# 5. Instruction/scaffold modes

Allowed semantic modes:

```text
turkish_primary
bilingual_parallel
english_with_targeted_gloss
english_primary_with_non_target_support
english_unscaffolded
```

These are not CEFR levels and not a fixed progression calendar.

## 5.1 Scaffold surfaces

Possible support:
- Turkish instruction,
- bilingual parallel instruction,
- glossary/term gloss,
- translation of non-target segments,
- instruction chunking,
- syntactic simplification,
- context explanation,
- non-answer-revealing worked context,
- hover/tap definition or equivalent UX later.

## 5.2 Technical target rule

If the target is technical:
- language support may be generous enough to remove construct-irrelevant language load,
- support must not solve the technical reasoning/coding/debugging target,
- system must not withhold necessary translation merely to “force English”.

## 5.3 English target rule

If the target is English:
- technical context may be scaffolded,
- target English phrase/grammar/meaning cannot be answer-revealed before independent response if strong evidence is intended,
- answer-revealing language support lowers assistance/evidence ceiling under existing AI-assistance/GRE rules.

## 5.4 Fading

Canonical:

```text
scaffold_fading_driver = evidence_and_task_validity
fixed_day_schedule = null
fixed_language_percentage = null
```

Support may decrease when:
- relevant English prerequisites are ready,
- prior attempts show the learner can access the material independently,
- removing support does not create construct contamination,
- target task/evidence purpose benefits from independent language use.

Support may increase again after regression/remediation or when a new task introduces genuinely new language load.

This is not failure or backsliding.

---

# 6. Prerequisite semantics

## 6.1 No global English gate

Forbidden examples:

```text
all_python_requires_B1_english
cuda_requires_B2_english
linux_requires_documentation_navigation_mastered
```

CEFR anchor metadata is never a technical hard gate.

## 6.2 Technical-only task

If an exact technical task happens to require an English behavior that is not part of the target construct:
- prefer localization/scaffold or an equivalent lower-language-load candidate,
- do not add English hard prerequisite merely because the source resource is English.

An English `required_skill_id` is justified only when:
- understanding that English behavior is structurally necessary for this exact task,
- the task intentionally preserves that language demand,
- the purpose/integration mode makes that demand construct-valid.

For pure technical assessment, if preserving the language demand would contaminate the construct, the candidate is invalid for that learner rather than a reason to fail the technical Skill.

## 6.3 Dual-target task

Both technical and English required Skill sets are explicit.

Eligibility is component-aware:
- missing technical hard prerequisite may invalidate English attribution if specialist reasoning is needed to produce the language response,
- missing English hard prerequisite may invalidate technical negative attribution if language access is necessary to understand the technical task,
- independent component evidence can still survive if its behavior was separately observed and remains interpretable.

---

# 7. Evidence attribution matrix

## 7.1 Technical-only localized

Technical evidence:
- allowed if normal GRE/PRG/QAB conditions pass.

English evidence:
- none by default.

## 7.2 Technical with English exposure

Technical evidence:
- allowed if language load was ready or neutralized and technical target is separately observable.

English evidence:
- exposure/practice only unless an explicit English Objective was separately declared and observed; otherwise no mastery attribution.

## 7.3 Dual-target

Technical and English each receive their own component result:

```text
component_result:
- positive_eligible
- negative_eligible
- practice_only
- provisional
- invalid_prerequisite_contamination
- invalid_scaffold_leakage
- not_observed
```

No broad combined pass.

## 7.4 English-primary technical context

English evidence:
- allowed if technical context prerequisite is ready/controlled and English target remains independently observable.

Technical evidence:
- none unless an explicit technical Objective is also declared, in which case mode should normally become `dual_target_integrated`.

---

# 8. Failure attribution / contamination rules

Canonical reason codes:

```text
language_access_contamination
technical_context_contamination
undeclared_english_prerequisite
undeclared_technical_prerequisite
scaffold_answer_leakage
component_not_separately_observable
overall_outcome_broadcast_forbidden
```

## 8.1 Language caused technical failure

If learner cannot access the task because undeclared/unready English is required:

```text
technical_negative_evidence = invalid_prerequisite_contamination
```

System may:
- preserve Attempt history,
- create/refresh relevant English LearningNeed if appropriate,
- flag resource/task metadata for QA,
- choose localized/bilingual alternative on replan.

It must not lower target technical mastery from that contaminated failure.

## 8.2 Technical ignorance caused English failure

If English task requires specialist technical reasoning not ready for the learner:

```text
english_negative_evidence = invalid_prerequisite_contamination
```

Use a simpler/known technical context or scaffold non-target domain facts.

## 8.3 Positive evidence under partial contamination

A contamination concern does not automatically delete separately observed positive evidence.

Example:
- learner misunderstood one English sentence but still produced a correct independently verified C artifact from explicit executable requirements.
- technical positive evidence may remain interpretable if evaluator can show the target behavior was independently demonstrated.

Conservative rule:

> Preserve only the component whose claim→evidence chain remains independently interpretable.

---

# 9. Natural integration and dual-purpose efficiency

One task may serve more than one open LearningNeed; this can reduce redundant practice.

But:
- multi-need linkage does not multiply priority score,
- one completion does not automatically resolve all linked needs,
- estimated duration is counted once against common capacity,
- each evidence claim is attributed separately,
- optional integration cannot jump ahead of P0/P1 merely because it covers two tracks.

`integration_opportunity` remains an existing LearningNeed trigger; TEIP-v0 does not create a new priority band.

## 9.1 Natural English reinforcement in technical work

Authentic technical work may reinforce English:
- docs navigation,
- instruction reading,
- error-fragment reading,
- clarification question,
- definition/constraint reading.

For strong English evidence, normal requirements still apply:
- explicit target Objective,
- structurally essential language behavior,
- separately observable response/action,
- prerequisites ready,
- H0/direct/verified/independent as required,
- no answer-revealing scaffold.

Otherwise treat as practice/reinforcement/exposure, not mastery closure.

---

# 10. Authentic technical resource policy

Potential contexts:
- compiler/runtime errors,
- terminal output,
- man pages,
- README/docs,
- GitHub issue/PR text,
- configuration docs,
- API reference,
- CUDA/Triton/runtime docs,
- benchmark/profiling output,
- incident/runbook fragments.

Authenticity does not override prerequisite validity.

A resource can be authentic yet inappropriate because:
- language load exceeds learner state,
- technical concepts exceed target scope,
- vendor/version detail is stale,
- too much domain knowledge is required for an English construct,
- translation/gloss changes technical meaning.

For assessment/mastery-changing use, QAB-v0/AIV-v0 lifecycle/use-ceiling/freshness rules remain authoritative.

## 10.1 Translation/gloss integrity

A translated or bilingual support artifact must preserve:
- technical meaning,
- constraints/warnings,
- negation,
- parameter names/identifiers where relevant,
- code/command literal integrity.

If support changes semantic meaning, resource is invalid for strong evidence.

AI-generated translation/gloss is not self-validating.

---

# 11. Integration patterns by route family

These are authoring patterns, not production content.

## Python / C

Technical-only:
- Turkish/bilingual instruction + English identifier/error snippet.

Dual-target:
- implement/fix code + write a short English result/bug note when both Objectives are ready.

Guard:
- language failure cannot erase independently verified code correctness.

## Linux / Git / Shell

Technical-with-exposure:
- command task + short authentic man/help fragment with targeted gloss.

Dual-target:
- execute operation + ask/write a clarifying technical question or short result note.

Guard:
- unknown prose in docs does not make filesystem/process Git capability fail if task can be localized.

## Systems / Networking / Distributed

English-primary technical context:
- read a known/simple definition/constraint and extract condition/limitation.

Dual-target later:
- explain simple observed behavior in English only when underlying technical behavior is already ready enough to keep language attribution interpretable.

Guard:
- advanced consensus/network diagnosis cannot be a hidden prerequisite for B1 reading evidence.

## GPU / CUDA / Triton / inference

Technical-with-exposure:
- short official-doc snippet around already-taught API/concept.

English-primary:
- documentation navigation or constraint extraction in a context whose technical concept is controlled.

Guard:
- model/vendor-specific complexity must not become fake English difficulty.

## Professional workflow

Possible dual-target contexts:
- basic bug report,
- command/result note,
- clarification question,
- docs navigation during issue resolution.

Professional artifact success still does not broadcast to every tagged technical/English Objective.

---

# 12. Planner/explainability semantics

A cross-track candidate may expose an integration trace:

```text
TechnicalEnglishIntegrationTrace
- integration_mode
- linked_learning_need_keys[]
- technical_target_refs[]
- english_target_refs[]
- technical_prerequisite_decision
- english_prerequisite_decision
- instruction_language_mode
- scaffold_surfaces[]
- language_load_role
- component_evidence_goals[]
- component_assistance_ceilings[]
- selected_reason
- omitted_reason?
```

Useful reason concepts:

```text
localized_to_protect_technical_construct
bilingual_support_due_to_unready_non_target_english
english_exposure_safe_for_current_profile
dual_target_both_components_ready
integration_rejected_language_contamination
integration_rejected_technical_context_contamination
scaffold_reduced_after_independent_evidence
scaffold_restored_for_new_language_load
```

These are explainability reasons, not learner mastery states.

---

# 13. Assessment and feedback

## 13.1 Before response

If independent evidence is intended:
- do not reveal target English answer,
- do not reveal target technical solution,
- non-target context support is allowed within the declared assistance ceiling.

## 13.2 After response

Corrective feedback may address:
- technical error,
- language error,
- both, separately.

Feedback output should localize:

```text
technical_issue_refs[]
english_issue_refs[]
```

Not:

```text
"You failed the integrated task, so both are weak."
```

## 13.3 Assisted revision

Assisted revision remains learning/practice evidence; independent mastery closure still requires fresh normal evidence under GRE/WLRM.

---

# 14. 7D safety fixtures

A validator/QA suite should explicitly cover at least:

1. C technical task + unknown English wording → localized support, no C negative contamination.
2. Linux task + authentic English man page → technical evidence valid only if construct-essential language is ready/scaffolded.
3. English error-fragment task + unknown Linux diagnosis concept → English negative invalid if diagnosis was secretly required.
4. Dual-target code + bug note → code positive can coexist with English negative/remediation.
5. Dual-target task overall PASS → no automatic component mastery broadcast.
6. English target with answer-revealing translation → English independent evidence ceiling lowered/invalid.
7. Technical target with bilingual instruction → bilingual support alone does not lower technical evidence if technical answer is not revealed.
8. B2+ docs navigation extension → remains inside TECP-v0 semantic boundary.
9. Authentic CUDA docs with new vendor jargon → use controlled context/gloss or reject; do not create global English gate.
10. Existing English mastered Skill used naturally in technical task → reinforcement allowed; retention/mastery evidence only with normal attribution requirements.
11. English `review_due` → technical task not globally blocked.
12. English `remediation_required` → only genuinely language-dependent integrated candidate may be blocked; unrelated technical branch continues.
13. Technical context failure during English-primary task → English negative contamination guard.
14. Integrated task linking two LearningNeeds → duration counted once, evidence separately.
15. AI-generated bilingual scaffold changes negation/constraint → resource invalid for strong evidence.

---

# 15. Acceptance requirements

7D may close only if:

1. 7A/7B/7C canonical D01 15-Skill set remains unchanged.
2. Exactly four integration modes are defined with non-overlapping target semantics.
3. `technical_only_localized` forbids English mastery attribution and English global hard gate.
4. `english_primary_technical_context` protects against specialist-technical contamination.
5. `dual_target_integrated` requires separately observable component attribution and forbids global pass broadcast.
6. Task metadata separates technical targets, English targets, technical prerequisites and English prerequisites.
7. Language load role is explicit (`incidental | supporting | construct_relevant`).
8. Scaffold modes are semantic support modes, not CEFR levels or calendar phases.
9. No fixed Turkish/English percentage, fixed fading day count or fixed integrated-task quota is introduced.
10. Scaffold fading is evidence/task-validity driven and can reverse when new load/remediation requires support.
11. Technical negative evidence is invalid when undeclared/unready English plausibly caused the failure.
12. English negative evidence is invalid when undeclared/unready specialist technical knowledge plausibly caused the failure.
13. Positive component evidence may survive only if its claim→evidence chain remains independently interpretable.
14. Natural integrated reuse cannot manufacture strong English or technical evidence without normal GRE/PRG/QAB attribution.
15. Authentic resource usage remains subject to prerequisite, lifecycle/use-ceiling and freshness rules.
16. Translation/gloss integrity guard preserves technical semantics and AI-generated support is not self-validating.
17. 7D does not define learner-facing final English mastery/CEFR behavior; 7E remains owner.
18. Final Stage 6 reconciliation regression + accepted 7B + accepted 7C + independent 7D validator PASS.
19. Persistent external-memory validator PASS.
20. D-050 living-memory sync + repo-wide stale-reference audit PASS.

---

# 16. Future-stage boundaries

7D does **not** implement:

- final English mastery/profile/display semantics → **7E**,
- production UI/widget design for scaffold controls → **8 / 11**,
- physical DB schema → **9C**,
- planner engine code → **12C/12D**,
- Tutor-generated translations/explanations runtime → **14**,
- real Python/C/Linux/English lesson/item bodies → **15**,
- empirical scaffold effectiveness/calibration → **18**,
- expanded long-term professional communication capabilities → **20** if GNS/KGC review proves new identities are needed.

---

# 17. Candidate decision

`TEIP-v0 / D-066` is accepted only after independent deterministic QA + D-050 POST.

Candidate summary:

> Technical English integration is construct-aware and component-attributed. Technical-only tasks localize or scaffold non-target English rather than gate technical progress. Dual-target tasks declare both target/prerequisite families and can yield different component outcomes. English-primary tasks control technical context so domain ignorance cannot masquerade as language weakness. Scaffold is evidence-driven and never a mastery shortcut.