# Question / Assessment Resource Bank Specification — QAB-v0

**Adım:** 4D — Soru bankası  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Final model:** `QAB-v0 — Trusted Assessment Resource Bank`

Bu belge günlük, haftalık ve aylık assessment blueprint'lerinin güvenilir biçimde seçebileceği; 4+ yıllık curriculum boyunca ölçeklenebilecek; aynı/near item'larla sahte evidence üretmeyecek; coding/debugging/system/transfer/integration gibi gerçek teknik görevleri de taşıyabilecek production-grade assessment resource bank davranışını tanımlar.

Bağlayıcı kaynaklar:
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` — DMA-v0 / D-040
- `docs/WEEKLY_ASSESSMENT_SPEC.md` — WBA-v0 / D-045
- `docs/MONTHLY_ASSESSMENT_SPEC.md` — MCA-v0 / D-046
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — D-044

Ana ilke:

> **Question Bank yalnız çoktan seçmeli soru deposu değildir. Ölçülmek istenen Objective davranışına uygun prompt, coding task, debugging task, system task, transfer task, testlet ve integrated task'ların versioned, prerequisite-aware, exposure-aware ve doğrulanabilir bir assessment resource bank'idir.**

İkinci ilke:

> **Bir resource bank'te bulunuyor diye otomatik güçlü evidence üretmez. Gerçek Attempt/Artifact yine prerequisite, assistance, provenance, evaluator, attribution ve GRE/RVR kurallarından geçer.**

Üçüncü ilke:

> **Logical item kimliği, content version'ı ve kullanıcı exposure geçmişi ayrı tutulur. Published bir version sessizce değiştirilmez; eski attempt hangi version ile yapıldıysa sonsuza kadar izlenebilir kalır.**

---

# 1. 4D neyi çözer?

QAB-v0 şu problemleri çözer:

- Hangi Objective'i gerçekten ölçtüğü belli olmayan rastgele soru havuzunu önlemek.
- Öğretilmemiş prerequisite yüzünden kullanıcıyı yanlış değerlendirmemek.
- Aynı sorunun veya yüzeysel varyantlarının independent evidence sayısını şişirmesini engellemek.
- Daily/weekly/monthly blueprint slot'larını doğru task/resource ile eşleştirmek.
- Coding/debugging/transfer gibi davranışları MCQ'ya indirgememek.
- Answer key/rubric/test/evaluator olmadan high-stakes item kullanılmasını engellemek.
- AI-generated içeriğin trusted bank'e otomatik girmesini engellemek.
- Technology/version değişimlerinde stale content'i güvenli biçimde yönetmek.
- 4+ yıllık curriculum'da selection query'lerinin full-bank scan'e dönüşmesini engellemek.

QAB-v0 şunları yapmaz:
- mastery hesabını yeniden tanımlamaz → GRE-v0,
- assessment cadence/composition'ı yeniden tanımlamaz → DMA/WBA/MCA,
- prerequisite readiness'i yeniden tanımlamaz → PRG-v0,
- AI-generated item validator'ın exact pipeline'ını tamamlamaz → 4E.

---

# 2. “Question Bank” yerine Assessment Resource Bank

Canonical physical resource tek tip `Question` değildir.

Baseline resource kinds:

```text
recognition_item
recall_item
code_reading_item
coding_task
debugging_task
hands_on_system_task
explanation_task
transfer_task
integrated_task
language_task
testlet
parameterized_template
```

`resource_kind` kullanıcı ne yapacağını tamamen belirlemez. Canonical `activity_kind` yine `TASK_TAXONOMY_SPEC.md` ile hizalıdır.

Örnek:

```text
resource_kind = coding_task
activity_kind = coding_production
expected_evidence_type = coding_production
```

Question Bank adı ürün içinde kullanılabilir; data model semantiği daha geniş `AssessmentResource`'tır.

---

# 3. Logical identity ve immutable version

Her resource iki seviyeli kimlik kullanır:

```text
resource_id       = logical stable identity
resource_version  = immutable published content version
```

Örnek:

```text
resource_id = py.control_flow.while_termination.debug.001
resource_version = 3
```

Attempt her zaman exact version'a bağlanır:

```text
attempt.resource_id
attempt.resource_version
```

## Version oluşturmayı gerektiren değişiklikler

Aşağıdakiler published resource üzerinde sessiz edit yapılmaz; yeni version gerekir:
- prompt semantics değişimi,
- expected answer/rubric değişimi,
- target Objective/Skill değişimi,
- prerequisite değişimi,
- evaluator/test değişimi,
- allowed tools/artifact requirement değişimi,
- difficulty/complexityyi anlamlı etkileyen değişiklik,
- answer leakage/ambiguity fix'i,
- technology/runtime dependency değişimi.

Yalnız kullanıcıya anlamı değiştirmeyen typo/format düzeltmesi için implementation ileride same-version display patch destekleyebilir; ancak audit açısından simplest safe default yeni version'dır. Exact authoring ergonomics 9C/15H'de netleşebilir.

---

# 4. Published version immutable'dır

Bir published version:
- attempt history'yi korumak için immutable kabul edilir,
- sonradan yanlış olduğu anlaşılırsa overwrite edilmez,
- `invalidated` veya `deprecated` lifecycle state'ine geçirilir,
- gerekiyorsa corrected new version oluşturulur.

Bu sayede:

```text
aynı resource_version + aynı response + aynı evaluator version
→ aynı semantic evaluation yeniden kurulabilir
```

olmalıdır.

---

# 5. Lifecycle state

Baseline lifecycle:

```text
draft
candidate
validated
trusted
deprecated
invalidated
retired
```

## `draft`
Authoring aşamasında; selection için uygun değil.

## `candidate`
Şema olarak oluşmuş fakat içerik/trust doğrulaması tamamlanmamış. AI-generated resource varsayılan olarak en fazla burada başlar.

## `validated`
Declared target, prerequisites, answer/rubric/evaluator ve ambiguity/correctness kontrolleri ilgili validation policy'yi geçmiştir. Kullanım alanı ayrıca `use_ceiling` tarafından sınırlanır.

## `trusted`
Belirtilen high-stakes use policy için önceden onaylanmış ve gerekli deterministic/prevalidated güven koşullarını taşıyan resource.

`trusted` bütün Objective'ler ve bütün assessment scope'ları için evrensel güven demek değildir; declared target/use profile'a göre anlamlıdır.

## `deprecated`
Yeni seçim için tercih edilmez; geçmiş attempts geçerliliğini otomatik kaybetmez.

## `invalidated`
Resource version'da correctness, ambiguity, prerequisite, rubric/evaluator veya başka integrity problemi doğrulanmıştır. Yeni selection yasaktır; affected past evidence review/invalidation workflow'a girebilir.

## `retired`
Artık active curriculum/bank selection'da kullanılmaz; audit/history için tutulur.

---

# 6. Trust ile lifecycle aynı şey değildir

Selection yalnız `status = validated` kontrolü yapmaz.

Resource ayrıca kullanım tavanı taşır:

```text
use_ceiling:
  practice_only
  low_stakes_assessment
  standard_mastery_eligible
  critical_mastery_eligible
```

Örnek:
- open response için yalnız uncalibrated LLM evaluator varsa item structurally validated olabilir fakat `critical_mastery_eligible` olamaz,
- deterministic hidden tests ile doğrulanan küçük coding task trusted olabilir.

Effective eligibility:

```text
lifecycle/trust
AND assessment use ceiling
AND exact Objective evidence requirement
AND evaluator availability
AND prerequisite eligibility
AND user exposure constraints
```

ile belirlenir.

---

# 7. Content origin / provenance

Resource en az şu origin metadata'sını taşır:

```text
content_origin:
  human_authored
  parameterized_from_trusted_template
  ai_generated
  mixed_authorship
  imported_reference_based
```

Ek alanlar:

```text
authoring_source_refs[]
generator_model_ref?
generation_prompt_version?
created_at
created_by
```

`content_origin = ai_generated` kendi başına invalid demek değildir; ancak **trusted/mastery-changing use için 4E validation yapılmadan yükseltilemez**.

---

# 8. Canonical AssessmentResource contract

```text
AssessmentResource
- resource_id
- resource_version
- resource_kind
- lifecycle_status
- use_ceiling
- content_origin
- created_at
- published_at?
- deprecated_at?

- prompt_payload_ref
- display_assets_refs[]?
- instruction_language_profile

- target_skill_ids[]
- target_objective_ids[]
- objective_attributions[]
- required_skill_ids[]
- forbidden_not_yet_concepts[]?
- language_prerequisite_skill_ids[]?

- primary_activity_kind
- expected_evidence_types[]
- maximum_evidence_role
- independence_mode
- allowed_tools_policy
- artifact_requirement?

- assessment_scope_eligibility[]
- blueprint_role_eligibility[]
- assessment_intent_eligibility[]

- difficulty_class
- complexity_profile
- variant_family_id
- dependency_group_id?
- testlet_id?
- context_family_id?
- transfer_profile?

- answer_key_ref?
- rubric_ref?
- deterministic_test_ref?
- evaluator_requirement
- evaluator_policy_ref?

- atomic_evidence_boundary
- splittable
- minimum_safe_chunk_minutes?
- expected_active_minutes
- estimate_confidence

- technology_dependencies[]?
- content_freshness_status
- freshness_review_ref?

- validation_record_refs[]
- source_reference_refs[]
- tags[]
- index_facets[]
```

Schema fiziksel DB implementasyonu değildir; 9C bunu normalize edecektir.

---

# 9. Objective attribution zorunluluğu

Her assessment resource mümkün olduğunca exact Objective'e bağlanır.

Multi-objective resource için:

```text
ResourceObjectiveAttribution
- objective_id
- skill_id
- target_role: primary | secondary
- structurally_essential
- separately_observable
- expected_evidence_type
- maximum_evidence_role
- rubric_component_ref?
- artifact_component_ref?
- contamination_dependency_refs[]?
```

Kritik invariant:

```text
global task pass != all tagged objectives pass
```

Integrated task'ta Objective ayrı gözlenemiyorsa o Objective için direct evidence iddia edilemez.

---

# 10. Prerequisite metadata

Resource iki prerequisite katmanı taşır:

```text
required_skill_ids[]
forbidden_not_yet_concepts[]?
```

`required_skill_ids` exact task'i adil çözmek için gereken gerçek prerequisite'lerdir ve PRG-v0'a gider.

`forbidden_not_yet_concepts`, özellikle başlangıç curriculum'unda prompt/solution'ın henüz öğretilmemiş concept'e gizlice dayanmasını önleyen authoring/validation guard'dır.

Graph'ta target Skill'in genel prerequisite'i bulunmaması exact item requirement'ını yok saydırmaz.

---

# 11. English / language prerequisite güvenliği

Technical Objective ölçen resource:
- bilinmeyen English grammar/vocabulary'yi gizli prerequisite yapamaz,
- gerekirse Turkish/bilingual instruction variant kullanabilir.

English Objective ölçen resource:
- yalnız öğretilmiş grammar/vocabulary prerequisites'i kullanır.

Metadata:

```text
InstructionLanguageProfile
- instruction_locales[]
- primary_locale
- bilingual_scaffold_available
- target_language_is_evidence: true | false
- language_prerequisite_skill_ids[]
- translation_equivalence_group_id?
```

Aynı sorunun Türkçe/İngilizce çevirileri farklı independent problem family sayılmaz.

---

# 12. Evidence/activity fit

Resource kolay üretildiği için yanlış modality'ye dönüşemez.

Örnekler:
- coding Objective → user-authored coding artifact,
- debugging Objective → fresh diagnosis/fix,
- terminal/system Objective → hands-on system task,
- transfer Objective → unseen structure/context,
- concept recall → free response uygun olabilir.

`expected_evidence_types` ve `maximum_evidence_role`, selector'ın MCQ'yu production mastery yerine kullanmasını engeller.

---

# 13. Assessment scope eligibility

Resource declared scope eligibility taşır:

```text
assessment_scope_eligibility:
  daily_micro?
  weekly?
  monthly?
  retention?
  diagnostic?
  practice?
```

Bu sadece resource'un teknik olarak o scope'ta kullanılabileceğini söyler; blueprint'e mutlaka gireceğini söylemez.

Örnek:
- 4 dakikalık recall item daily+weekly olabilir,
- 35 dakikalık integrated system task monthly olabilir fakat günlük micro slot'a uygun olmayabilir,
- diagnostic item normal mastery assessment için uygun olmayabilir.

---

# 14. Blueprint role / intent eligibility

WBA/MCA blueprint matching için resource hangi role'lara uygun olduğunu deklaratif taşır.

Örnek:

```text
blueprint_role_eligibility:
  weakness_or_verification
  retention_due
  integration_or_transfer
  critical_capability_revalidation
  cross_topic_transfer
  integrated_application
```

Daily için:

```text
assessment_intent_eligibility:
  checkpoint
  mastery_evidence
  verification
  integration_check
```

Role tag alone selection priority değildir; actual current state/need role'u üretir.

---

# 15. Variant family

`variant_family_id` aynı temel çözüm yapısına sahip yakın varyantları gruplar.

Aynı family örnekleri:
- yalnız sayılar değişiyor,
- variable isimleri değişiyor,
- aynı bug pattern aynı fix ile çözülüyor,
- aynı algoritmik şablon farklı yüzey metniyle sunuluyor.

Kural:

> **Near variant yeni resource_id olsa bile otomatik independent evidence diversity değildir.**

Family granularity authoring/4E QA ile kontrol edilir.

---

# 16. Dependency group / testlet

`dependency_group_id` veya `testlet_id`, item'ların ortak stem/context/önceki cevaba bağlı olması nedeniyle local dependence taşıdığını gösterir.

Kurallar:
- aynı dependency group içindeki correlated alt item'lar GRE-v0 independent group sayısını şişiremez,
- bir root prerequisite/stem failure downstream item'ları contaminate ediyorsa kör negative yazılmaz,
- selector aynı dependency group'u diversity diye saymaz.

Variant family ve dependency group farklı kavramlardır:

```text
variant_family = benzer çözüm yapısı
local_dependency_group = aynı context/stem nedeniyle istatistiksel/anlamsal bağımlılık
```

---

# 17. Context family ve transfer profile

Gerçek transferi yalnız variable değişiminden ayırmak için:

```text
context_family_id
transfer_profile:
  same_context
  near_context
  cross_topic_context
  novel_application
  integrated_system_context
```

Bu değerler numeric mastery multiplier değildir.

Monthly/transfer blueprint, Objective gerçekten transfer istiyorsa farklı context family ve uygun transfer profile isteyebilir.

---

# 18. Difficulty ve complexity ayrıdır

QAB-v0 tek bir sahte hassas `difficulty_score = 7.4` zorunlu kılmaz.

Baseline difficulty:

```text
introductory
standard_application
advanced_application
transfer_or_integration
```

Ayrı complexity profile örnek alanları:

```text
ComplexityProfile
- reasoning_breadth
- integration_breadth
- environment_complexity
- artifact_size_class
- open_endedness
- debugging_depth
- performance_analysis_required
```

Bunlar önce semantic class'tır; numeric psychometric calibration AŞAMA 18 verisi olmadan icat edilmez.

Difficulty, GRE-v0 score multiplier değildir.

---

# 19. Answer key / rubric / deterministic verification

Resource, evidence tipine uygun değerlendirme yolu tanımlamalıdır.

Possible evaluator artifacts:

```text
normalized_answer_key
multiple_valid_answers_ref
rubric_ref
compiler_test_ref
unit_test_ref
hidden_test_suite_ref
trace_checker_ref
benchmark_acceptance_ref
manual_review_rubric_ref
llm_evaluator_policy_ref
composite_evaluator_ref
```

Kritik:
- exact answer item'da answer key versioned,
- coding'de testler target behavior'ı gerçekten doğrulamalı,
- debugging'de yalnız programın çalışması root-cause diagnosis Objective'ini kanıtlamayabilir,
- open response'ta tek uncalibrated LLM `verified` critical evidence üretmez.

---

# 20. Evaluator requirement

Baseline:

```text
EvaluatorRequirement
- required_status: verified | provisional_allowed
- allowed_evaluator_kinds[]
- deterministic_required: true | false
- rubric_ref?
- evaluator_policy_version
```

Resource valid olsa bile required evaluator kullanılamıyorsa strong assessment slot için ineligible olabilir.

Evaluation failure kullanıcı failure değildir:

```text
evaluator unavailable/invalid -> evidence invalid/provisional, not learner negative
```

---

# 21. Allowed tools ve artifact requirement

Gerçek engineering task'larında tool kullanımı objective'e göre normal olabilir.

Metadata:

```text
AllowedToolsPolicy
- compiler
- debugger
- terminal
- documentation
- profiler
- IDE autocomplete
- internet_docs?
- external_ai
- prohibited_solution_sources[]
```

`external_ai` mastery/verification için çoğunlukla H0'ı bozar; ancak gerçek professional tool-use Objective'i ileride farklı policy tanımlayabilir.

Artifact requirement:

```text
ArtifactRequirement
- artifact_kind
- user_authored_required
- execution_output_required
- test_output_required
- profiler_output_required?
- explanation_required?
```

---

# 22. Atomicity / duration

Her resource expected active cost taşır:

```text
expected_active_minutes
estimate_confidence
splittable
atomic_evidence_boundary
minimum_safe_chunk_minutes?
```

Selector kısa diye yanlış evidence modality seçmez.

- MCQ'nin 2 dk olması onu 20 dk coding verification yerine koymaz.
- testlet ortadan kesilirse correlation/evidence semantics bozuluyorsa atomic tutulur.
- büyük integrated task yalnız safe checkpoint'lerde split edilir.

Daily hard capacity daima korunur.

---

# 23. User exposure state bank metadata'sından ayrıdır

Resource global content'tir. Kullanıcının onu görüp görmediği ayrı state'tir:

```text
UserResourceExposure
- user_id
- resource_id
- resource_version
- first_seen_at?
- last_seen_at?
- attempt_count
- last_attempt_ref?
- solution_exposed_at?
- max_exposure_level
- family_recent_exposure_refs[]
- dependency_group_recent_refs[]
```

Bank content'i `seen=true` diye mutate edilmez.

---

# 24. Solution exposure

Kullanıcı H3/H4, worked solution veya post-submit full solution gördüyse exact resource version `solution_exposed` olur.

Sonuç:
- aynı item immediate fresh independent evidence olamaz,
- near variant/family de selector tarafından contamination riskiyle değerlendirilir,
- yeni/unseen family/context gerekebilir.

Solution exposure resource'u global invalid yapmaz; yalnız o kullanıcı için evidence freshness'ını etkiler.

---

# 25. Exposure cooldown için sahte sabit yok

QAB-v0:

```text
aynı soru 14 gün sonra yine independent olur
```

gibi universal sayı kilitlemez.

Freshness şu sinyallere bağlıdır:
- exact solution exposure,
- family yakınlığı,
- dependency overlap,
- intervening contexts,
- assessment intent,
- Objective için gereken diversity.

Empirik reuse/cooldown calibration AŞAMA 18'e bırakılır.

---

# 26. Content freshness ile learner retention farklıdır

İki “freshness” karıştırılmaz.

## Learner/item exposure freshness
Kullanıcı soruyu/çözümü daha önce gördü mü?

## Content freshness
Resource teknik olarak hâlâ güncel/doğru mu?

Content metadata:

```text
content_freshness_status:
  current
  review_due
  stale
  retired
```

Örnek dependencies:
- Python runtime/version,
- GCC/Clang behavior,
- CUDA toolkit/architecture,
- vLLM/SGLang/TensorRT-LLM version,
- API/CLI semantics.

`stale` high-stakes strong evidence için seçilemez.

---

# 27. Technology dependency metadata

Gereken resource'larda:

```text
TechnologyDependency
- technology_id
- version_constraint?
- behavior_dependency_note
- source_ref?
- verified_at
```

Evergreen concept item gereksiz version constraint taşımak zorunda değildir.

Tool/library-specific item zamanla review_due olabilir. Bu user mastery decay değildir; content QA problemidir.

---

# 28. Validation record

Her validated/trusted version için audit trail:

```text
ValidationRecord
- validation_id
- resource_id
- resource_version
- validator_kind
- validator_ref
- validation_policy_version
- checks_run[]
- result
- findings[]
- validated_at
```

4E validator bu contract'a kayıt üretecek.

Published trust kararının yalnız serbest metin `looks_good` kaydı olması yeterli değildir.

---

# 29. Invalidation ve geçmiş evidence

Bir resource sonradan invalidated olursa:
- yeni selection anında durur,
- affected attempts/evidence exact resource version üzerinden bulunabilir,
- ilgili EvidenceEvent'ler `invalid/unusable` veya review-required yapılabilir,
- GRE/RVR state versioned recalculation/event ile düzeltilir,
- kullanıcı hatalı item nedeniyle cezalandırılmaz.

Past evidence sessizce silinmez; audit trail korunur.

Full historical recompute UI thread'de yapılmaz; bounded/background repair job mimarisi 9C/12A'da tasarlanır.

---

# 30. Deprecated vs invalidated

`deprecated`:
- resource yanlış olmak zorunda değildir,
- yeni/daha iyi replacement bulunduğu için selection dışına çıkabilir,
- geçmiş evidence geçerli kalabilir.

`invalidated`:
- integrity problemi vardır,
- geçmiş evidence'ın güvenilirliği etkilenebilir.

Bu ayrım önemlidir.

---

# 31. Selection pipeline

Assessment blueprint slot resource seçerken canonical sıra:

```text
1. slot target Objective/Skill + evidence requirement
2. assessment scope / role / intent eligibility
3. lifecycle + use ceiling + content freshness
4. exact prerequisite / language eligibility
5. evaluator/tool/environment availability
6. user exposure / solution exposure
7. variant-family / dependency-group / context diversity
8. duration / atomicity fit
9. bounded candidate ranking
10. selected resource version
```

Priority/blueprint ihtiyacı resource bank tarafından icat edilmez. Bank yalnız uygun candidate sağlar.

---

# 32. Candidate ranking

QAB-v0 yeni global weighted score icat etmez.

Aynı slot için eligible resources arasında semantic sıra kullanılabilir:
- required evidence type'ı exact karşılayan,
- daha güvenilir validation/evaluator,
- gerekli fresh family/context sağlayan,
- exposure contamination'ı düşük,
- intended complexity'ye uygun,
- capacity'ye anlamlı şekilde sığan,
- stable tie-break.

Sırf en kısa olduğu için düşük-değerli item seçilmez.

---

# 33. Indexing / bounded performance

4+ yıllık bank full scan ile sorgulanmamalıdır.

Implementation en az şu indeks/facet'leri desteklemeye hazırlanmalıdır:
- target_objective_id,
- target_skill_id,
- lifecycle/use_ceiling,
- scope eligibility,
- blueprint role / daily intent,
- evidence/activity kind,
- required prerequisite signature veya lookup,
- variant_family_id,
- dependency_group_id,
- context_family_id,
- difficulty/complexity class,
- duration bucket,
- locale/language profile,
- content freshness.

Selector önce narrow indexed candidate set üretir, sonra per-user exposure/prerequisite filtrelerini uygular.

Exact candidate count limit implementation config'dir; 4D bilimsel bir `top 20` sabiti ilan etmez.

---

# 34. Parameterized template

Trusted template çok sayıda numeric/input variation üretebilir.

Template contract:

```text
ParameterizedTemplate
- template_id
- template_version
- parameter_schema
- generation_constraints
- expected_answer_generator_ref
- invariant_validation_ref
- variant_family_id
- dependency_policy
```

Her generated instance yeni problem family sayılmaz.

Instance correctness deterministic generator/checker ile doğrulanabiliyorsa use policy buna izin verebilir; fakat family diversity hâlâ template semantics'e göre hesaplanır.

---

# 35. AI-generated resource 4E handoff

AI-generated candidate:

```text
content_origin = ai_generated
lifecycle_status = candidate
use_ceiling <= practice_only / policy-defined low-risk ceiling
```

olarak başlar.

4E en az şunları kesinleştirecek:
- schema completeness,
- technical correctness,
- answer/rubric correctness,
- ambiguity,
- target Objective fit,
- prerequisite completeness,
- forbidden concept leakage,
- evidence modality fit,
- duplicate/near-duplicate/family detection,
- dependency/testlet correctness,
- transfer claim validity,
- evaluator suitability,
- difficulty/complexity sanity,
- source/technology freshness,
- risk-based promotion policy.

AI output **kendisi için validation proof değildir**.

---

# 36. Trusted template'ten AI varyant üretimi

Trusted template olması AI'nın ürettiği her exact varyantın otomatik trusted olduğu anlamına gelmez.

Safe inheritance yalnız declarative olarak doğrulanmış invariant'lar için mümkündür.

Örnek:
- template parameter ranges güvenli,
- answer generator deterministic,
- prerequisites değişmiyor,
- generated prompt constraint validator'ı geçiyor.

Bunların dışında semantic AI rewrite yeni validation gerektirir.

Exact policy 4E'de kilitlenecek.

---

# 37. Diagnostic / practice / high-stakes ayrımı

Aynı bank resource'ları farklı use case taşıyabilir fakat trust requirement aynı olmak zorunda değildir.

Kavramsal risk sırası:

```text
practice/remediation
< low-stakes checkpoint
< standard mastery/retention verification
< critical mastery / high-impact weekly/monthly revalidation
```

Risk yükseldikçe correctness, prerequisite, evaluator ve trust requirement gevşemez.

QAB-v0 exact automation/human-review threshold'unu 4E'ye bırakır.

---

# 38. Resource sonucu doğrudan mastery değildir

Bank kaydı:

```text
resource.answer_key = correct_answer
```

olabilir; fakat user result canonical pipeline'dan geçer:

```text
Resource
→ PlannedTask
→ Attempt/Artifact
→ evaluate
→ prerequisite/assistance/provenance/attribution
→ EvidenceEvent
→ GRE/RVR
→ planner
```

Resource hiçbir zaman `mastery_delta = +20` içermez.

---

# 39. Reason codes

Assessment namespace'e bank-specific reason codes eklenebilir:

```text
assessment.bank.resource_selected
assessment.bank.no_resource_for_objective
assessment.bank.scope_ineligible
assessment.bank.use_ceiling_insufficient
assessment.bank.prerequisite_ineligible
assessment.bank.language_prerequisite_ineligible
assessment.bank.solution_exposed
assessment.bank.variant_family_too_recent
assessment.bank.dependency_conflict
assessment.bank.context_diversity_needed
assessment.bank.evaluator_unavailable
assessment.bank.content_review_due
assessment.bank.content_stale
assessment.bank.resource_invalidated
assessment.bank.replacement_version_selected
assessment.bank.duration_not_fit
assessment.bank.generated_candidate_not_trusted
```

PDT user-facing explanation trace'te olmayan neden icat etmez.

---

# 40. User-facing semantics

Kullanıcıya bank/lifecycle teknik ayrıntısının tamamı gösterilmek zorunda değildir.

Gerektiğinde anlaşılır mesajlar:
- `Bu soru daha önce çözümüyle görüldüğü için bağımsız ölçümde kullanmadık.`
- `Bu görev için gerekli ön bilgiyi henüz işlemediğin için başka bir görev seçildi.`
- `Bu soru güvenilir biçimde değerlendirilemedi; sonucunu ilerlemene karşı kullanmadık.`
- `Bu teknik görev güncelliğini yitirdiği için yeni sürüm kullanıldı.`

---

# 41. QAB-v0 invariants

1. Bank yalnız MCQ deposu değildir.
2. Resource logical ID ve immutable published version ayrıdır.
3. Attempt exact resource version'a bağlanır.
4. Published version sessizce overwrite edilmez.
5. AI-generated candidate otomatik trusted değildir.
6. Bank'te bulunmak otomatik mastery eligibility değildir.
7. Exact prerequisites selection öncesi kontrol edilir.
8. Bilinmeyen English gizli teknik prerequisite olamaz.
9. Evidence modality Objective davranışıyla eşleşir.
10. Variant family bağımsız evidence diversity'yi şişiremez.
11. Dependency/testlet local dependence ayrı modellenir.
12. Translation aynı problem family'yi yeni family yapmaz.
13. Integrated task global pass'i bütün Objectives'e yayamaz.
14. Difficulty numeric mastery multiplier değildir.
15. Invalid evaluator learner failure değildir.
16. Solution exposure per-user state'tir; global resource invalidity değildir.
17. Content freshness ve learner retention farklı kavramlardır.
18. Stale resource strong evidence için kullanılmaz.
19. Deprecated ve invalidated farklıdır.
20. Invalidated version'ın past evidence'ı audit edilebilir.
21. Selection full-bank scan olmadan bounded/indexed tasarlanır.
22. Short task sırf kısa olduğu için daha uygun modality'nin önüne geçmez.
23. Resource bank assessment need/priority icat etmez; blueprint/planner state'ini tüketir.
24. Resource `mastery_delta` veya doğrudan topic pass/fail taşımaz.
25. 4E validation promotion lifecycle üzerinde çalışır.

---

# 42. 4D acceptance criteria

4D PASS için:

1. Question Bank'in assessment resource bank olduğu açık.
2. Stable ID + immutable version contract var.
3. Lifecycle/trust/use ceiling ayrımı tanımlı.
4. Target Skill/Objective attribution var.
5. Required prerequisite + forbidden-not-yet guard var.
6. English/language prerequisite güvenliği var.
7. Evidence/activity/scope/blueprint-role metadata var.
8. Variant/dependency/context/transfer semantics ayrılmış.
9. Integrated component attribution tanımlı.
10. Answer key/rubric/test/evaluator contract var.
11. Allowed tools/artifact requirement var.
12. Difficulty/complexity fake precision olmadan ayrılmış.
13. Duration/atomicity/capacity metadata var.
14. User exposure/solution leakage global content'ten ayrılmış.
15. Content freshness/technology dependency var.
16. Invalidation/deprecation/version history davranışı var.
17. Bounded/indexed selection pipeline tanımlı.
18. Parameterized template davranışı tanımlı.
19. AI-generated candidate trusted bank'e otomatik giremiyor.
20. 4E validator handoff açık.
21. DMA/WBA/MCA/PRG/GRE/RVR invariants korunuyor.
22. D-044 granular Skill/Objective IDs ile uyumlu.

---

# 43. Final 4D kararı

**Final model:** `QAB-v0 — Trusted Assessment Resource Bank`

Canonical akış:

```text
Granular curriculum Skill/Objectives
        ↓
versioned AssessmentResources
        ↓
lifecycle + trust + use ceiling
        ↓
blueprint slot requirement
        ↓
scope/role/evidence/prerequisite filter
        ↓
exposure + variant/dependency/context diversity filter
        ↓
evaluator/tool/duration eligibility
        ↓
bounded resource selection
        ↓
PlannedTask
        ↓
Attempt / Artifact
        ↓
validity + assistance + provenance + evaluator
        ↓
Objective-level EvidenceEvent
        ↓
GRE / RVR / PRG / Planner
```

QAB-v0 böylece assessment sisteminin `hangi soruyu/görevi güvenle kullanabiliriz?` katmanını çözer; **kullanıcının gerçekten ne bildiğine ilişkin karar yine evidence pipeline'ının işidir.**
