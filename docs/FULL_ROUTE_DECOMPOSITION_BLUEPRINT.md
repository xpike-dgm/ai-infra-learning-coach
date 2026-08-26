# Full-Route Decomposition Blueprint — FRDB-v0

**Adım:** 6B — Full-route decomposition blueprint  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-26  
**Final model:** `FRDB-v0 — Full-Route Decomposition Blueprint`  
**Karar:** D-056

Bu belge AŞAMA 6C–6F boyunca 23 route family için üretilecek ayrıntılı `Domain → Module → Topic → Skill → Learning Objective` haritalarının ortak authoring yöntemini, kayıt alanlarını, paket biçimini ve QA handoff'unu tanımlar.

Bağlayıcı kaynaklar:
- `docs/GRANULARITY_NAMING_STANDARD.md` — GNS-v0 / D-054
- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — KGC-v0 / D-051
- `docs/CURRICULUM_DOMAIN_MAP.md` — PDM-v0 / D-049
- `docs/V1_FOUNDATION_BACKBONE.md` — FBB-v0 / D-052
- `docs/GRAPH_ARCHITECTURE_QA.md` — GQA-v0 / D-053
- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — D-044
- `docs/PROJECT_MEMORY_PROTOCOL.md` — D-050

Ana ilke:

> **6C–6F farklı teknik alanları yazsa da aynı capability kimliği, granularity, evidence, prerequisite, reuse, provenance ve QA sözleşmesini kullanmak zorundadır.**

İkinci ilke:

> **Blueprint node sayısını veya takvim sırasını optimize etmez. Ayrı learner state, evidence, remediation, prerequisite veya reuse değeri olan capability'leri güvenilir biçimde ayırır; aynı semantic capability'yi farklı placement'larda clone'lamaz.**

Üçüncü ilke:

> **Decomposition paketi production lesson bank'i veya fiziksel DB şeması değildir. Machine-readable authoring dataset'idir; learner-facing publish ancak ilgili detailed-map QA ve 6H bağımsız Research QA sonrasında yapılabilir.**

---

# 1. 6B'nin sınırı

6B kesinleştirir:
- 6C–6F paket sınırlarını,
- ortak machine-readable authoring package yapısını,
- Domain/Module/Topic candidate kayıtlarını,
- Skill/Objective candidate kayıtlarını,
- TopicSkillLink, prerequisite, requirement ve attribution kayıtlarını,
- GNS-v0 granularity review alanlarını,
- duplicate resolver ve cross-domain Skill reuse workflow'unu,
- evidence/depth, remediation, retention ve diagnostic metadata authoring biçimini,
- provenance/freshness/source capture'ını,
- FBB seed ratification/migration mapping'ini,
- unresolved review queue ve package-level QA raporunu,
- 6G ve 6H handoff contract'ını.

6B kesinleştirmez:
- 23 family'nin gerçek tam node listesini → 6C–6F,
- FBB seed'lerinin tek tek final ratification sonucunu → 6C,
- weakness state runtime algoritmasını → 6G + sonraki implementation,
- full coverage/current-industry doğrulamasını → 6H independent Research QA,
- English CEFR progression/cadence'ini → AŞAMA 7,
- lesson/task/resource body'lerini → AŞAMA 15/20,
- SQLite/Room tablo yapısını → 9C,
- GRE/RVR/PRG/PBR davranışlarını → mevcut canonical specs.

---

# 2. Değiştirilemez authoring invariants

1. `Domain/Module/Topic` organization; `Skill/Objective` capability/evidence katmanıdır.
2. Topic/Domain completion mastery değildir.
3. Objective exactly one canonical Skill'e bağlıdır.
4. Aynı semantic Skill farklı Topic/Domain'lerde clone'lanmaz.
5. Runtime prerequisite ana birimi Skill→Skill'dir.
6. Domain/Module/Topic order hard runtime gate değildir.
7. Hard prerequisite yalnız target öğretimi/evidence attribution'ı gerçekten source'a bağlıysa yazılır.
8. Soft gap hard block üretmez.
9. Technical English global technical gate değildir.
10. Bilinmeyen grammar veya undeclared capability hidden prerequisite bırakılamaz.
11. Integrated task/project PASS bütün tagged Objectives'e otomatik evidence vermez.
12. AI/LLM canonical identity, edge veya merge kararını kendiliğinden publish edemez.
13. Tool/vendor adı stable systems concept'in yerine geçmez.
14. Published semantic identity sessiz overwrite edilmez.
15. Split/merge learner'a bedava mastery üretmez.
16. Numeric threshold, node count veya difficulty puanı scientific optimum gibi uydurulmaz.
17. Unresolved semantic belirsizlik açık review kaydı olur; sessiz varsayımla kapatılmaz.
18. 6H external Research QA tamamlanmadan full-route coverage externally validated sayılmaz.

---

# 3. 6C–6F package partition

## 3.1 6C — Foundations

Route family kapsamı:
- D01 Technical English,
- D02 Python,
- D03 C,
- D04 Linux + Git + Shell,
- D05 DS&A Foundations.

Ek zorunluluk:
- bütün FBB-v0 seed'leri GNS-v0 status contract'ı ile tek tek ratify/refactor edilir,
- GQA-v0 corrective edges ve TopicSkillLink'leri başlangıç guard'ı olarak korunur,
- zero-entry bridge organization placement'ları değerlendirilir.

## 3.2 6D — Systems

Route family kapsamı:
- D06 Modern C++,
- D07 Computer Architecture,
- D08 Operating Systems + Memory,
- D09 Concurrency / Parallel Programming,
- D10 Networking,
- D11 Distributed Systems + Storage/Databases,
- D12 Containers / Cloud / Observability,
- D13 Performance Engineering & Profiling.

## 3.3 6E — GPU / ML / Inference

Route family kapsamı:
- D14 GPU Architecture,
- D15 CUDA,
- D16 Triton,
- D17 ML + Transformer Foundations,
- D18 LLM Inference Internals,
- D19 Serving Systems,
- D20 KV Cache / Batching / Scheduling / Quantization,
- D21 Multi-GPU + NCCL + RDMA,
- D22 AI Infrastructure / GPU Infrastructure.

## 3.4 6F — Professional engineering / projects

Route family kapsamı:
- D23 Open Source Contributions + Real Large Projects + Professional Capstones.

Ek cross-cutting kapsam:
- testing,
- build/tooling,
- debugging,
- profiling/benchmarking,
- Git/branch/PR/code review,
- source-tree reading,
- design docs/RFC reasoning,
- reproducibility,
- logging/metrics/tracing,
- reliability/SLO/incident/postmortem,
- security/operational safety,
- technical communication,
- project/capstone integration.

Bu overlay'ler D23 içinde clone capability üretmez; D01–D22 Topic'lerine shared Skill link/attribution ile yeniden yerleştirilebilir.

---

# 4. Canonical authoring package biçimi

6C–6F'nin canonical interchange formatı **UTF-8 YAML authoring package**'dır. Markdown açıklama/QA raporu pakete eşlik eder; yalnız Markdown tablo veya prose canonical dataset yerine geçmez.

Her package logical olarak şu koleksiyonları taşır:

```text
manifest.yaml
sources.yaml
organization_entities.yaml
skills.yaml
objectives.yaml
topic_skill_links.yaml
prerequisite_edges.yaml
capability_requirements.yaml
professional_attributions.yaml
project_capstone_attributions.yaml
seed_mappings.yaml
review_queue.yaml
qa_report.yaml
```

Physical dosya bölme stratejisi dataset büyüdüğünde route family bazında değişebilir. Ancak logical collection adları ve row contract'ları korunur. JSON/DB export türetilebilir; semantic source authoring package'tır.

Canonical future package root önerisi:

```text
curriculum/decomposition/
  6c_foundations/
  6d_systems/
  6e_gpu_ml_inference/
  6f_professional_engineering/
```

6B bu dizinleri boş placeholder olarak üretmez. İlk gerçek package 6C'de oluşturulur.

---

# 5. Package manifest contract

```yaml
package_id: decomposition.6c_foundations
package_version: 1
stage_step: 6C
status: authoring_draft
blueprint_version: FRDB-v0
graph_contract_version: KGC-v0
granularity_standard_version: GNS-v0
base_graph_refs: []
route_family_ids: []
included_collections: []
source_catalog_refs: []
coverage_declarations: []
known_exclusions: []
unresolved_review_count: 0
blocking_review_count: 0
generated_at: null
authored_by: null
review_refs: []
content_hash: null
```

Kurallar:
- `route_family_ids` package partition ile tam eşleşir.
- `known_exclusions` sessiz coverage boşluğu bırakmaz; sonraki stage'e bilinçli bırakılan şeyi açıklar.
- `unresolved_review_count` review queue ile tutarlı olmalıdır.
- `status=published` 6C–6F authoring completion ile otomatik oluşmaz; 6H QA ve graph publication lifecycle ayrıca gerekir.
- `content_hash` implementation detayı olabilir; yokluğu semantic QA'yı engellemez.

---

# 6. Source catalog / provenance contract

Her external veya repository source bir kez catalog'a alınır:

```yaml
source_id: source.repo.gns_v0
source_kind: canonical_spec
title: Granularity & Naming Standard
ref: docs/GRANULARITY_NAMING_STANDARD.md
version_or_date: 2026-08-25
authority_class: accepted_project_contract
supports: [granularity, logical_identity]
freshness_class: evergreen
checked_at: 2026-08-26
notes: null
```

`source_kind` baseline:

```text
canonical_spec
accepted_decision
authoritative_external_reference
research_report
standards_document
tool_vendor_documentation
source_code_reference
internal_design_analysis
```

Kurallar:
- `source_refs[]` yalnız serbest URL listesi değildir; catalog ID kullanır.
- External current-industry/source-quality validation 6H'de güçlendirilir.
- `internal_design_analysis` external doğrulama yerine geçmez.
- Version-sensitive tool claim source/version/check date taşır.
- Source olmayan pedagogical veya technical kesinlik uydurulmaz.

---

# 7. Organization entity row

Domain, Module ve Topic aynı logical collection'da `entity_type` ile ayrılabilir:

```yaml
entity_type: topic
logical_id_candidate: topic.python.iteration
display_name: Python Iteration
semantic_statement: Python iteration davranışlarının öğretim ve uygulama bağlamı.
parent_organization_id: module.python.control_flow
primary_domain_id: domain.python
organization_role: teaching_context
teaching_intent_tags: [teach, practice, debug]
linked_route_family_ids: [D02]
lifecycle_status: draft
authoring_status: candidate
source_refs: []
provenance_ref: null
freshness_class: evergreen
granularity_review_codes: [GRANULARITY_OK]
review_refs: []
notes: null
```

Zorunlu semantik alanlar:
- `entity_type`,
- `logical_id_candidate`,
- `display_name`,
- `semantic_statement`,
- parent/primary placement,
- lifecycle + authoring status,
- source/provenance,
- freshness,
- granularity review sonucu.

Domain parent taşımaz. Module tek primary Domain taşır. Topic tek primary Module taşır. Cross-domain capability reuse organization entity clone'layarak çözülmez.

---

# 8. Skill candidate row

```yaml
skill_id_candidate: skill.python.while_termination
canonical_name: While termination kurabilmek
capability_statement: Python while döngüsünde state update ve termination condition kurabilmek.
capability_kind: language_specific_production
lifecycle_status: draft
authoring_status: candidate

primary_teaching_topic_id: topic.python.iteration
linked_topic_ids: []
shared_placement_domain_ids: [domain.python]

independent_evidence_path:
  observable: true
  direct_evidence_types: [authored_code, debugging]
  example_outcome: Sonlanan döngü üretir veya non-termination nedenini lokalize eder.

boundaries:
  prerequisite_boundary: State/boolean reasoning ayrı source capability olabilir.
  remediation_boundary: Termination hatası for-iteration syntax remediation'ından ayrıdır.
  reuse_boundary: Python runtime/syntax behavior'ı nedeniyle language-specific kalır.

shared_vs_specific:
  classification: language_specific
  rationale: Python syntax/runtime production davranışı shared iteration reasoning'den ayrıdır.

evidence_depth_expectations: [independent_application, debugging, transfer]
retention_profile: complex
diagnostic_eligibility: eligible
critical_prerequisite_candidate: false
remediation_tags: [state_trace, debug_localization, fresh_variant]
professional_capability_tags: [debugging_foundation]
project_capability_tags: []

source_refs: []
provenance_ref: null
freshness_class: evergreen
technology_dependency_refs: []

duplicate_resolution:
  status: no_duplicate_found
  compared_skill_ids: []
  rationale: null

granularity_review_codes: [GRANULARITY_OK, LANGUAGE_SPECIFIC_SPLIT]
seed_mapping_refs: []
review_refs: []
```

Skill candidate en az şu GNS sorularını cevaplar:
- ayrı direct evidence var mı,
- ayrı failure/remediation meaningful mi,
- prerequisite sınırı var mı,
- cross-context reuse var mı,
- evidence/depth/retention farkı var mı,
- shared mi language/tool-specific mi,
- duplicate resolver sonucu ne,
- source ve freshness durumu ne.

`capability_kind` implementation enum'u 9C'de normalize edilebilir; 6B'de anlamı açık semantic label olmalıdır.

---

# 9. Learning Objective candidate row

```yaml
objective_id_candidate: objective.python.while_termination.debug_nontermination
owner_skill_id: skill.python.while_termination
objective_statement: >
  Verilen yeni bir while örneğinde termination state'in güncellenmediği nedeni
  lokalize eder, target davranışı bozmadan düzeltir ve sonucu doğrular.
observable_action: localize_fix_verify_nontermination
success_criteria: Root cause doğru; fix target behavior'ı koruyor; fresh run sonlanıyor.
lifecycle_status: draft
authoring_status: candidate
requirement_role: required
criticality: standard

evidence_profile:
  acceptable_evidence_types: [debugging]
  direct_evidence_types: [debugging]
  required_direct_type: debugging
  requires_user_authored_artifact: true
  requires_transfer: true
  evaluator_requirement: verified
  policy_defaults_ref: GRE-v0

task_environment_expectation: Python execution environment
allowed_tools_policy_ref: h0_debugging_tools_allowed
diagnostic_eligibility: eligible
retention_requirement:
  delayed_revalidation_required: true
  context_diversity_requirement: different_loop_structure

remediation_tags: [state_trace, debug_localization, fresh_variant]
source_refs: []
provenance_ref: null
freshness_class: evergreen
granularity_review_codes: [GRANULARITY_OK]
review_refs: []
```

Kurallar:
- `owner_skill_id` exactly one Skill'dir.
- `objective_statement`, `observable_action` ve `success_criteria` ayrı yazılır.
- Objective content instance değildir; `question_3`/`lesson_check` identity olamaz.
- Evidence profile GRE-v0'ı değiştirmez; policy defaults/ref + gerekli semantic overrides kullanır.
- Yeni numeric group/threshold yalnız kabul edilmiş policy veya source/calibration kararıyla eklenebilir.
- İki bağımsız capability aynı Objective'e sıkıştırılmaz.

---

# 10. TopicSkillLink row

```yaml
topic_id: topic.python.iteration
skill_id: skill.programming.iteration_reasoning
role: reinforce
importance: core
is_primary_teaching_context: false
objective_scope_ids:
  - objective.programming.iteration_reasoning.trace_iteration
authoring_rationale: Shared reasoning capability Python bağlamında yeniden kullanılır.
source_refs: []
lifecycle_status: draft
review_refs: []
```

Controlled values KGC-v0'ı kullanır:
- `role`: `teach | practice | assess | reinforce | transfer | integrate`,
- `importance`: `core | supporting | incidental`.

Bir Topic–Skill çifti birden fazla role sahipse serialization tek row içinde `roles[]` kullanabilir veya logical relation rows'a ayırabilir; semantic duplicate oluşturulamaz.

---

# 11. Skill prerequisite candidate row

```yaml
prerequisite_skill_id: skill.programming.iteration_reasoning
target_skill_id: skill.python.while_termination
edge_kind: hard
reason_kind: conceptual_dependency
strictness_profile_ref: default_prg_v0
authoring_rationale: >
  Iteration state ve termination modelini izleyemeyen kullanıcıda target production
  failure'ı Python syntax ile reasoning arasında güvenilir ayrıştırılamaz.
contamination_risk_if_missing: high
task_specific_instead_of_graph_edge: false
source_refs: []
provenance_ref: null
lifecycle_status: draft
review_status: reviewed
review_refs: []
```

KGC controlled `reason_kind` değerleri:

```text
conceptual_dependency
procedural_dependency
evidence_interpretability
safety_dependency
tool_environment_dependency
language_dependency
performance_reasoning_dependency
professional_workflow_dependency
```

Her hard edge şu soruyu açıkça cevaplar:

> Source yokken target öğretilebilir ve target evidence adil biçimde yorumlanabilir mi?

- Cevap hayırsa hard edge adayıdır.
- Source yalnız scaffold/akıcılık sağlıyorsa soft'tur.
- Gereksinim yalnız belirli task/resource için geçerliyse graph edge yerine task/QAB `required_skill_ids[]` kullanılır.
- Takvim, chapter veya broad Domain sırası rationale olamaz.

---

# 12. Capability requirement row

```yaml
scope_kind: professional_route
scope_id: scope.professional_route.ai_infrastructure
capability_kind: skill
capability_id: skill.python.while_termination
requirement_role: required
criticality: standard
rationale: Foundation programming reliability için gerekli.
source_refs: []
review_refs: []
```

Requirement scope-relative'dir. Global `skill.required=true` ile bütün rota kilitlenmez.

Allowed scope family'leri KGC-v0'dan gelir:

```text
topic
module
domain
v1_backbone
professional_route
project
capstone
assessment_blueprint
```

---

# 13. Professional attribution row

```yaml
capability_id: skill.python.while_termination
professional_family_id: professional.debugging
attribution_role: supporting
context_requirements: [fresh_bug_context]
evidence_expectation_ref: evidence.debugging.h0
rationale: Non-termination debugging temel professional debugging davranışını destekler.
source_refs: []
review_refs: []
```

Baseline professional family registry en az şu aileleri destekler:
- debugging,
- testing,
- build/tooling,
- profiling/benchmarking,
- systems design,
- distributed failure reasoning,
- performance reasoning,
- technical communication,
- reproducibility,
- reliability/observability,
- operational security/safety,
- open-source workflow.

Attribution readiness veya mastery kararı değildir.

---

# 14. Project / capstone attribution row

```yaml
project_or_capstone_id: project.foundation.python_cli
skill_id: skill.python.while_termination
objective_id: objective.python.while_termination.debug_nontermination
role: supporting
structurally_essential: true
separately_observable: true
expected_evidence_type: debugging
rubric_component_ref: rubric.python_cli.loop_recovery
source_refs: []
review_refs: []
```

`structurally_essential=false` veya `separately_observable=false` ise direct component evidence iddiası yapılamaz. Global project PASS otomatik evidence değildir.

6F gerçek project/capstone decomposition'ında bu kayıtları genişletir; 6C–6E yalnız açık ve anlamlı project bağları varsa candidate attribution yazabilir.

---

# 15. FBB seed mapping row

6C için zorunludur; diğer package'larda legacy/seed mapping varsa kullanılabilir.

```yaml
seed_id: skill.python.values_variables_expressions
seed_kind: skill
disposition: needs_granularity_review
result_entity_refs: []
evidence_compatibility: not_applicable_not_published
rationale:
  semantic_boundary: Values, assignment ve expression davranışlarının ayrı learner state gerektirip gerektirmediği incelenecek.
  prerequisite_effect: Bazı downstream edges yalnız assignment/state parçasına bağlı olabilir.
  evidence_effect: Production ve reasoning kanıtları ayrışabilir.
  remediation_effect: Type/value hatası ile assignment-state hatası farklı müdahale isteyebilir.
  reuse_effect: Shared state reasoning Python-specific production'dan ayrılabilir.
mapping_class: pending
source_refs: [source.repo.fbb_v0, source.repo.gns_v0]
review_refs: []
```

Allowed `disposition` GNS-v0 ile aynıdır:

```text
ratify_as_is
ratify_with_display_edit
normalize_logical_id
split_required
merge_with_existing
rehome_placement_only
deprecate_seed
needs_granularity_review
```

FBB learner-published olmadığı için `evidence_compatibility=not_applicable_not_published` kullanılabilir. Buna rağmen old→new mapping/provenance sessizce kaybedilemez.

---

# 16. Review queue row

Belirsizlik prose içinde saklanmaz:

```yaml
review_id: review.6c.python.values_assignment_split
subject_refs: [skill.python.values_variables_expressions]
review_codes: [UNDER_FRAGMENTED, NEEDS_GRANULARITY_REVIEW]
severity: blocking
question: Values, assignment-state ve expression production ayrı Skills olmalı mı?
decision_inputs_required:
  - independent evidence comparison
  - prerequisite neighborhood comparison
  - remediation path comparison
resolution_owner_step: 6C
status: open
resolution: null
source_refs: []
created_at: null
resolved_at: null
```

`severity`:

```text
blocking
non_blocking
advisory
```

Blocking review unresolved ise ilgili entity/relation published sayılamaz. Package authoring completion, açık review'ların açıkça listelenmesine izin verebilir; fakat 6H final coverage gate'i blocking unresolved kayıtları ya çözmeli ya da açık blocker olarak raporlamalıdır.

---

# 17. Granularity review workflow

Her candidate Skill için sıra:

```text
1. capability statement yaz
2. independent evidence yolunu göster
3. independent failure/remediation ayrımını göster
4. prerequisite boundary'yi göster
5. reuse/shared-vs-specific sınırını göster
6. evidence/depth/retention farkını göster
7. existing Skill registry'de duplicate ara
8. GNS-v0 review code ata
9. candidate / reuse / split / merge / review disposition üret
```

Karar puan toplamı değildir.

```text
ayrı learner state planner kararını değiştiriyor
→ ayrı Skill adayı

aynı state + aynı evidence + aynı remediation + yalnız context/syntax-token farkı
→ Skill açma; Objective/resource/TopicSkillLink kullan
```

Allowed review codes GNS-v0'dan gelir:

```text
GRANULARITY_OK
UNDER_FRAGMENTED
OVER_FRAGMENTED
DUPLICATE_CAPABILITY
SHARED_SKILL_REUSE
LANGUAGE_SPECIFIC_SPLIT
TOOL_SPECIFIC_SPLIT
CONTEXT_ONLY_NO_SPLIT
OBJECTIVE_NOT_OBSERVABLE
OBJECTIVE_MULTI_CAPABILITY
HIDDEN_PREREQUISITE_RISK
UNSTABLE_ID_COMPONENT
PLACEMENT_ONLY_CHANGE
SEMANTIC_VERSION_CHANGE
LOGICAL_IDENTITY_CHANGE
NEEDS_GRANULARITY_REVIEW
```

FRDB-v0 yeni overlapping granularity code vocabulary icat etmez.

---

# 18. Duplicate resolver ve cross-package Skill registry

6C–6F ayrı package olsa da duplicate araması yalnız current package içinde yapılmaz.

Candidate oluşturulmadan önce karşılaştırılır:
- accepted graph Skill registry,
- daha önce tamamlanan 6C–6F package Skills,
- current package candidate Skills,
- FBB seed aliases/mappings,
- capability statement,
- Objective set,
- prerequisite neighborhood,
- evidence/depth profile,
- remediation intent,
- placements,
- professional/project tags.

Resolver outcomes:

```text
reuse_existing
create_new
candidate_split
candidate_merge
duplicate_review_required
```

## 18.1 `reuse_existing`

- Existing canonical Skill korunur.
- Yeni TopicSkillLink/attribution eklenir.
- Gerekirse yeni Objective yalnız aynı capability altında gerçekten yeni atomic evidence target ise eklenir.
- Başka Domain placement'ı learner state'i çoğaltmaz.

## 18.2 `create_new`

- Independent capability gerekçesi açık olmalıdır.
- ID registry collision kontrolü yapılır.
- Shared-vs-specific rationale zorunludur.

## 18.3 Split / merge

- Existing published entity varsa KGC migration manifest gerekir.
- FBB authoring seed ise seed mapping gerekir.
- Evidence kör kopyalanmaz veya average edilmez.

String similarity, embedding veya LLM yalnız candidate sinyalidir; canonical merge kararı değildir.

---

# 19. Prerequisite authoring workflow

## 19.1 Pass A — Candidate declaration

Her Skill author yalnız gerçekten gerekli source Skills'i candidate olarak yazar; broad route order kopyalanmaz.

## 19.2 Pass B — Hard/soft test

Hard edge için en az bir gerekçe:
- target teaching anlamlı değil,
- target task adil çözülemez,
- target evidence yorumlanamaz,
- source eksikliği failure attribution'ı contaminate eder,
- safety/correctness nedeniyle source zorunludur.

Yalnız kolaylaştırma/scaffold ise soft'tur.

## 19.3 Pass C — Task-specific ayrımı

Dependency bütün target capability için değil yalnız belirli item/project context için geçerliyse graph edge yaratılmaz; QAB/Task metadata'ya bırakılır.

## 19.4 Pass D — Graph QA

- edge direction source→target,
- self-edge yok,
- hard/soft same-pair conflict yok,
- hard graph DAG,
- dangling ref yok,
- reason_kind controlled,
- branch isolation korunuyor,
- `review_due` not-ready olarak author edilmemiş,
- English global gate yok.

## 19.5 Pass E — Reverse-dependency impact

Hard edge'in hangi branch'i bloke edeceği gözden geçirilir. Gereksiz geniş reverse-dependent neighborhood edge'in yanlış granularity veya yanlış hard classification sinyali olabilir.

---

# 20. Evidence ve depth authoring

FRDB-v0 depth'i tek sayı veya mastery multiplier'ı yapmaz.

Semantic depth vocabulary:

```text
recognition
recall_explanation
guided_application
independent_application
debugging
transfer
delayed_retention
integration
performance_measurement
production_context
```

Skill `evidence_depth_expectations[]` hangi behavior family'lerinin capability kapsamına ait olduğunu belirtir. Objective `evidence_profile` ise exact observable target için gate bilgisini taşır.

Kurallar:
- coding production Skill yalnız recognition ile kanıtlanamaz,
- debugging Skill fresh diagnosis/fix evidence ister,
- transfer yalnız yüzeysel near-variant değildir,
- performance capability ölçüm/benchmark/profiler artifact'ı isteyebilir,
- integrated evidence component-attributable olmalıdır,
- existing GRE-v0 defaults sessizce değiştirilmez,
- package author bilimsel olmayan yeni sayı uydurmaz.

---

# 21. Remediation ve 6G weakness handoff'u

6C–6F learner-specific weakness state üretmez. Her Skill/Objective yalnız static remediation affordance taşır:

```text
remediation_tags[]
repair_prerequisite_skill_ids[]
recommended_evidence_modalities[]
misconception_family_refs[]
```

Her candidate şu soruyu cevaplamalıdır:

> Bu capability zayıf olduğunda bütün Topic/Domain'i tekrar ettirmeden hangi hedefli müdahale seçilebilir?

6G bütün package'ları tüketerek:
- evidence/failure → exact Objective/Skill attribution,
- contamination guard,
- misconception/root-cause family,
- targeted reteach/practice/retest,
- broad-domain overreaction guard

mapping'ini tamamlar.

6C–6F'de remediation boundary açıklanamıyorsa Skill granularity yeniden incelenir.

---

# 22. Retention, diagnostic ve freshness authoring

## 22.1 Learner retention

Skill:

```text
retention_profile = factual | standard | complex
```

Objective gerektiğinde:

```text
delayed_revalidation_required
context_diversity_requirement
```

taşır. Interval sayıları RVR policy/config'e aittir; graph'a scientific truth gibi gömülmez.

## 22.2 Diagnostic

```text
diagnostic_eligibility = eligible | restricted | not_eligible
```

Diagnostic mastery'nin düşük standardı değildir; VDW-v0 korunur.

## 22.3 Content/technology freshness

```text
freshness_class = evergreen | version_sensitive | fast_moving
technology_dependency_refs[]
review_trigger_policy_ref
```

Learner retention ile content freshness karıştırılmaz.

---

# 23. Technical English safety contract

6C English package'ı:
- grammar function,
- vocabulary recognition/production,
- instruction comprehension,
- terminal/error reading,
- documentation reading,
- technical writing/speaking

capability'lerini GNS-v0 ile parçalayabilir.

Kurallar:
- CEFR level mastery atomu değildir,
- `A1/A2/B1` logical ID'nin semantic çekirdeği yapılmaz,
- English production Objective yalnız öğretilmiş English prerequisites ister,
- technical Objective English'i target etmiyorsa bilinmeyen English hidden prerequisite olamaz,
- bilingual scaffold target technical evidence'ı koruyorsa kullanılabilir,
- translation yeni independent problem family veya yeni Skill değildir,
- AŞAMA 7 progression/cadence kararları 6C taxonomy'sinden ayrıdır.

---

# 24. Stable concept / tool-specific capability contract

Her fast-moving alan iki soruyla author edilir:

1. Tool/vendor değişse de kalan stable systems capability nedir?
2. Tool'un kendisini kullanmak ayrı observable professional capability mi?

Sonuç:

```text
stable concept
→ generic canonical Skill

tool-specific independent production behavior
→ tool-specific Skill olabilir
→ source/version/freshness zorunlu

yalnız UI option veya geçici config adı
→ Skill değil; Topic/resource metadata
```

Örnek:

```text
skill.inference.continuous_batching_reasoning
```

stable capability olabilir. Belirli bir serving engine sürümündeki option adı aynı Skill'in yerine geçmez.

---

# 25. Professional/project coverage workflow

Her route family authoring sonunda şu review yapılır:
- hangi Skills debugging/testing/build/profiling davranışı taşıyor,
- hangi Skills reproducible artifact üretebilir,
- hangi Skills technical communication ister,
- hangi Skills reliability/security/observability bağlamında tekrar kullanılır,
- hangi Objectives gerçek project/capstone rubric'inde ayrı gözlenebilir.

6F:
- ortak professional Skill registry'yi reconcile eder,
- teknik package'larda clone edilen overlay candidate'larını merge/reuse review'una alır,
- küçük→integrated→professional capstone evidence progression'ını organization/attribution olarak kurar,
- tek project PASS'ten toplu mastery üretmez.

---

# 26. Cross-package reconciliation

Her package kapanmadan önce:

```text
1. accepted Skill registry refresh
2. logical ID collision scan
3. duplicate/reuse scan
4. cross-package prerequisite refs validation
5. shared professional overlay reconciliation
6. source/freshness consistency
7. unresolved review queue merge
8. deterministic sort/export
```

Cross-package forward ref mümkündür fakat açık olmalıdır:

```yaml
target_ref_status: declared_future_package
expected_owner_step: 6E
blocking_for_current_package_publish: true
```

Var olmayan ID'yi sessiz placeholder Skill gibi kullanmak yasaktır.

---

# 27. Package QA contract

Her package `qa_report.yaml` içinde en az aşağıdaki check family'lerini raporlar.

## 27.1 Structural checks

- unique logical IDs,
- valid parent refs,
- Objective exactly-one-Skill,
- every Skill has at least one observable Objective candidate,
- no dangling TopicSkillLink,
- no dangling prerequisite/requirement/attribution refs,
- controlled enums/reason codes,
- hard graph DAG,
- no self-edge,
- no hard/soft same-pair conflict.

## 27.2 Granularity checks

- under-fragmentation review,
- over-fragmentation review,
- Objective observability,
- Objective multi-capability guard,
- shared-vs-specific rationale,
- duplicate resolver coverage,
- unstable ID component scan.

## 27.3 Learning/evidence checks

- direct evidence path exists,
- evidence modality fits target behavior,
- required/critical profile interpretable,
- remediation path targetable,
- retention profile present,
- diagnostic eligibility explicit,
- integrated attribution separately observable.

## 27.4 Prerequisite checks

- hidden prerequisite scan,
- task-specific-vs-graph distinction,
- branch isolation,
- English global-gate guard,
- source→target direction,
- zero-entry/reachability where relevant,
- no Domain/Topic completion hard gate.

## 27.5 Coverage/provenance checks

- every assigned route family has coverage declaration,
- intentional exclusions explicit,
- source refs resolvable,
- fast-moving claims freshness metadata carry,
- professional overlays considered,
- open/blocking reviews counted.

QA result:

```text
PASS
PASS_WITH_OPEN_NON_BLOCKING_REVIEWS
FAIL
BLOCKED
```

6H bağımsız Research QA bu internal package QA'yı tekrarlar/genişletir; onun yerine geçmez.

---

# 28. Non-canonical illustrative row chain

Aşağıdaki örnek yalnız contract'ın birlikte çalışmasını gösterir; 6C ratification kararı değildir.

```text
domain.python
  -> module.python.control_flow
  -> topic.python.iteration

topic.python.iteration
  -> teach skill.python.while_termination
  -> reinforce skill.programming.iteration_reasoning

skill.programming.iteration_reasoning
  --hard/conceptual_dependency-->
skill.python.while_termination

skill.python.while_termination
  -> objective.python.while_termination.write_terminating_loop
  -> objective.python.while_termination.debug_nontermination
```

6C:
- shared-vs-language-specific sınırı,
- FBB seed mapping'i,
- exact prerequisite hardness,
- Objective evidence profile'ı

GNS/KGC/PRG altında yeniden değerlendirerek gerçek kararı verir.

---

# 29. 6C–6H handoff

## 29.1 6C

- İlk gerçek FRDB-v0 package'ını oluşturur.
- FBB 41 Skill / 47 Objective seed'ini tek tek map eder.
- Foundation organization/capability/prerequisite graph'ını genişletir.
- GQA corrective guards'i korur veya explicit migration ile değiştirir.

## 29.2 6D

- 6C accepted registry'yi duplicate resolver input'u olarak kullanır.
- Systems family'lerini aynı row/QA contract'ıyla üretir.
- C/Linux/DS&A shared prerequisites'i clone'lamaz.

## 29.3 6E

- Systems/performance registry'yi tüketir.
- Stable GPU/inference concepts ile tool/version-specific capability'leri ayırır.
- Math/numerical hidden prerequisites'i explicit candidate Skills/edges olarak yakalar.

## 29.4 6F

- D23 ve cross-cutting professional overlay'i tamamlar.
- 6C–6E'deki professional tags/attributions'ı reconcile eder.
- Project/capstone component observability contract'ını uygular.

## 29.5 6G

- Dört package'ın exact Skill/Objective identities'ini weakness/remediation mapping'e bağlar.
- Broad-domain reset/overreaction guard'ını test eder.

## 29.6 6H

- Independent Research AI ile coverage, current relevance, hidden prerequisite, source quality ve missing-domain audit'i yapar.
- Structural duplicate/cycle/dead-end/freshness checks'i yeniden çalıştırır.
- Bulguları source/review refs ve gerekiyorsa explicit corrections/migration entries ile package'lara bağlar.
- External QA olmadan full professional capability map'i final validated ilan etmez.

---

# 30. Determinism, scale ve physical-schema sınırı

Machine-readable packages:
- stable logical IDs ile deterministik sıralanabilir,
- same inputs + accepted decisions ile semantic olarak aynı rows üretmelidir,
- full graph scan'i runtime zorunluluğu yapmaz,
- future index/adjacency generation'a uygun relation collections taşır,
- authoring QA'nın exact subject/ref üzerinden çalışmasına izin verir.

6B şunları belirlemez:
- Room/SQLite tables,
- composite primary keys,
- cache representation,
- exact file shard size,
- runtime query budget,
- UI serialization.

Bunlar 9C/9F/18E'ye aittir. Authoring YAML physical DB dayatmaz.

---

# 31. Research / Coding / Test kararı

6B için ayrı external Research AI kullanılmadı.

Gerekçe:
- 6B hangi teknik capability'lerin eksik olduğunu veya current industry coverage'ını doğrulayan adım değildir,
- accepted GNS/KGC/PDM/FBB/GQA/PRG contract'larını ortak authoring/output blueprint'ine formalize eder,
- bağımsız external coverage/current-industry/source-quality doğrulaması planlandığı gibi 6H'de zorunlu kalır.

Physical runtime implementation olmadığı için Coding AI kullanılmadı.

QA, bu belgedeki acceptance matrix'in canonical specs'e karşı statik contract review'u olarak yapılır. Actual package/schema validator implementasyonu sonraki authoring/architecture aşamalarında üretilebilir.

---

# 32. 6B acceptance gate

6B ancak aşağıdakilerin tamamı sağlandığında kapanır:

1. 23 route family 6C–6F package'larına eksiksiz atanmış.
2. Ortak machine-readable authoring package biçimi tanımlı.
3. Manifest ve source catalog contract'ı tanımlı.
4. Domain/Module/Topic row contract'ı tanımlı.
5. Skill candidate row independent evidence/remediation/prerequisite/reuse sınırlarını taşıyor.
6. Objective row exactly-one-Skill + observable action + success criteria + evidence profile taşıyor.
7. TopicSkillLink many-to-many reuse contract'ı korunuyor.
8. Hard/soft prerequisite candidate declaration ve reason vocabulary tanımlı.
9. Task-specific requirement ile graph edge ayrımı korunuyor.
10. Scope-relative capability requirement row tanımlı.
11. Professional ve project/capstone attribution ayrı ve non-compensatory.
12. FBB seed mapping/ratification contract'ı tanımlı.
13. GNS-v0 review codes ve unresolved review queue tanımlı.
14. Duplicate resolver current + prior package registry üzerinde çalışıyor.
15. Shared/language/tool/context-specific capability kuralları korunuyor.
16. Evidence/depth yeni mastery sistemi veya sahte numeric threshold üretmiyor.
17. Remediation/retention/diagnostic/freshness metadata handoff'u tanımlı.
18. Technical English global-gate guard korunuyor.
19. Stable concept/tool-specific freshness ayrımı korunuyor.
20. Package structural/granularity/evidence/prerequisite/coverage QA contract'ı tanımlı.
21. 6G weakness/remediation ve 6H external Research QA handoff'u açık.
22. Physical DB, production content veya gerçek full node listesi yanlışlıkla 6B kapsamına taşınmamış.
23. KGC/GNS/FBB/GQA/GRE/RVR/PRG davranışları sessizce değiştirilmemiş.

---

# 33. Final 6B kararı

**Final model:** `FRDB-v0 — Full-Route Decomposition Blueprint`

Canonical authoring akışı:

```text
PDM-v0 route family envelope
        ↓
FRDB-v0 package manifest + source catalog
        ↓
Domain / Module / Topic candidate rows
        ↓
Skill candidate + GNS Capability Independence Test
        ↓
cross-package duplicate resolver / shared Skill reuse
        ↓
atomic Objective + evidence/depth profile
        ↓
TopicSkillLink + Skill prerequisite + scope requirement
        ↓
retention / diagnostic / remediation / professional metadata
        ↓
FBB seed mapping + unresolved review queue
        ↓
package QA
        ↓
6C–6F detailed maps
        ↓
6G weakness/remediation mapping
        ↓
6H independent coverage/prerequisite/Research QA
```

**6B sonrası numaralı adım:** `6C — Foundations detailed map`.
