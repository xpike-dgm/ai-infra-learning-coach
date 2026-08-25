# Curriculum Knowledge Graph Contract — KGC-v0

**Adım:** 5B — Graph / Topic metadata sözleşmesi  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Final model:** `KGC-v0 — Versioned Curriculum Knowledge Graph Contract`  
**Karar:** D-051

Bu belge PDM-v0 domain backbone'unu, AŞAMA 6'nın yüzlerce/binlerce granular capability node'una güvenli biçimde genişleyebilecek, mastery/prerequisite/assessment/retention/remediation sistemleriyle uyumlu ve yıllar boyunca versionlanabilir bir knowledge-graph sözleşmesine dönüştürür.

Bağlayıcı kaynaklar:
- `docs/CURRICULUM_DOMAIN_MAP.md` — PDM-v0 / D-049
- `docs/LEARNING_ENGINE_SPEC.md` — canonical `Domain → Module → Topic → Skill → Learning Objective`
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0 / D-031
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0 / D-032
- `docs/QUESTION_BANK_SPEC.md` — QAB-v0 / D-047
- `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md` — AIV-v0 / D-048
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — D-044
- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/PROJECT_MEMORY_PROTOCOL.md` — D-050

Ana ilke:

> **Curriculum placement ile gerçek capability identity aynı şey değildir. Domain/Module/Topic öğretim ve navigasyon yapısını; Skill/Learning Objective ise ölçülebilir capability gerçeğini temsil eder.**

İkinci ilke:

> **Mastery, weakness, prerequisite ve remediation davranışı broad Topic/Domain completion'a değil canonical Skill/Objective kimliklerine bağlanır.**

Üçüncü ilke:

> **Published graph state sessizce değişmez. Semantic değişiklikler versionlanır; learner evidence/history eski graph sürümüyle yeniden kurulabilir kalır.**

---

# 1. 5B'nin sınırı

5B şunları kesinleştirir:
- entity ve relation contract'ları,
- canonical identity / curriculum placement ayrımı,
- prerequisite ve requirement semantiği,
- evidence/retention/remediation/diagnostic metadata bağları,
- English prerequisite güvenliği,
- professional/project/capstone attribution,
- provenance/freshness/versioning,
- graph migration ve performance invariants.

5B şunları yapmaz:
- bütün route'u ayrıntılı Skill/Objective listesine bölmez → AŞAMA 6,
- ilk 8–12 haftalık gerçek lesson/task content'ini yazmaz → AŞAMA 15,
- fiziksel SQLite/Room/DB şemasını kesinleştirmez → 9C,
- runtime planner/prerequisite algoritmasını yeniden tanımlamaz → PRG/PBR/GRE/RVR canonical kalır,
- external curriculum coverage araştırmasını tamamlamaz → 6H Research QA.

---

# 2. Graph'ın iki ayrı katmanı

## 2.1 Curriculum organization layer

```text
Domain → Module → Topic
```

Amaç:
- curriculum authoring,
- navigation,
- lesson/material packaging,
- broad progress/analytics summary,
- professional coverage görünümü.

Bu katmandaki node completion/mastery, learner capability'nin bağımsız source of truth'u değildir.

## 2.2 Capability / evidence layer

```text
Skill → Learning Objective
```

Amaç:
- gerçek mastery,
- prerequisite readiness,
- weakness localization,
- remediation,
- retention,
- assessment attribution,
- professional capability evidence.

Kritik invariant:

```text
Topic completed != Skill mastered
Domain progress != professional readiness
```

---

# 3. Canonical identity modeli

Her logical entity'nin insan tarafından değişebilir display adı ile kalıcı canonical identity'si ayrılır.

Baseline:

```text
logical_id
entity_version
slug/display_name
lifecycle_status
```

`logical_id` semantic capability'nin kalıcı kimliğidir. Display adı, açıklama veya curriculum placement değişti diye yeni logical identity yaratılmaz.

Published semantic content değişmişse aynı published version overwrite edilmez; yeni `entity_version` oluşturulur.

## 3.1 ID ilkeleri

ID:
- locale-independent,
- display title'dan bağımsız,
- deterministic/stable,
- case-normalized,
- semantic olarak yeniden kullanılmayan

olmalıdır.

Örnek formatlar yalnız authoring convention'dır:

```text
domain.python
module.python.control_flow
topic.python.loops
skill.programming.while_termination
objective.programming.while_termination.identify_state_change
```

AŞAMA 6A exact naming convention'ı finalize eder; 5B invariant'ı `stable logical identity != display label` ayrımıdır.

---

# 4. Domain contract

```text
Domain
- domain_id
- domain_version
- display_name
- description
- domain_roles[]
- lifecycle_status
- sort/navigation metadata
- source_refs[]
- provenance
- freshness_policy_ref?
- tags[]
```

`domain_roles[]` PDM-v0 controlled vocabulary'sini kullanır:

```text
parallel_track
common_foundation
systems_core
distributed_platform_core
performance_core
accelerator_core
supporting_domain
inference_systems_core
target_infrastructure
professional_evidence_layer
```

Domain:
- direct mastery puanı taşımaz,
- runtime hard-lock üretmez,
- Skill state'lerinden derived summary üretilebilir.

---

# 5. Module contract

```text
Module
- module_id
- module_version
- primary_domain_id
- display_name
- description
- lifecycle_status
- navigation_order?
- authoring_tags[]
- source_refs[]
- provenance
- freshness_policy_ref?
```

Module büyük Domain'i öğretilebilir/authorable kümelere ayırır.

Default modelde Module'ın tek `primary_domain_id`'si vardır. Cross-domain capability reuse Module kopyalayarak değil canonical Skill link'leriyle çözülür.

Module→Module relation yalnız authoring/navigation guidance olabilir; runtime hard prerequisite değildir.

---

# 6. Topic contract

```text
Topic
- topic_id
- topic_version
- primary_module_id
- display_name
- description
- lifecycle_status
- topic_kind?
- teaching_intent_tags[]
- estimated_scope_class?
- source_refs[]
- provenance
- freshness_policy_ref?
```

Topic:
- kullanıcının anlayacağı öğretim/çalışma bağlamıdır,
- lesson/practice/assessment/remediation resources için anchor olabilir,
- kendi başına mastery atomu değildir.

Topic state learner runtime'da Skill/Objective state'lerinden derived edilir; curriculum graph'ta static `mastered` alanı bulunmaz.

---

# 7. Skill contract — canonical capability identity

```text
Skill
- skill_id
- skill_version
- canonical_name
- capability_statement
- lifecycle_status
- capability_kind
- retention_profile
- critical_prerequisite
- diagnostic_policy_ref?
- remediation_tags[]
- professional_capability_tags[]
- project_capability_tags[]
- source_refs[]
- provenance
- freshness_policy_ref?
- aliases[]?
```

Skill:
- ders başlığı değil ölçülebilir tekrar kullanılabilir capability'dir,
- birden fazla Topic/Module/Domain bağlamında kullanılabilir,
- learner için **tek canonical mastery/retention state** üretir,
- cross-domain reuse nedeniyle kopyalanmaz.

Kritik invariant:

```text
same semantic Skill in 3 Topics
→ 1 canonical skill_id
→ 1 learner Skill state
```

`critical_prerequisite` GRE critical Objective ile aynı kavram değildir. Bu alan PRG-v0 strict prerequisite behavior için overlay'dir.

---

# 8. Learning Objective contract

Learning Objective tek bir canonical Skill altında atomik, gözlemlenebilir evidence target'ıdır.

```text
LearningObjective
- objective_id
- objective_version
- skill_id
- objective_statement
- observable_action
- lifecycle_status
- requirement_role
- criticality
- evidence_profile
- diagnostic_eligibility
- remediation_tags[]
- professional_capability_tags[]
- project_capability_tags[]
- source_refs[]
- provenance
- freshness_policy_ref?
```

## 8.1 Cardinality

V0 invariant:

```text
Learning Objective -> exactly 1 canonical Skill
Skill -> 1..N Learning Objectives
```

Bir Objective gerçekte iki bağımsız capability ölçüyorsa parçalanmalıdır. Integrated task birden fazla Objective'i aynı task'ta ölçebilir; Objective identity yine tek Skill'e bağlı kalır.

## 8.2 `requirement_role`

Skill içindeki Objective gate semantiği:

```text
required
optional
```

`criticality` ayrı eksendir:

```text
standard
critical
```

Böylece GRE-v0:

```text
all required Objectives PASS
AND all critical Objectives PASS
```

kuralını uygulayabilir.

`optional` bir Objective `critical` olamaz şeklinde evrensel yasak koyulmaz; fakat böyle bir kombinasyon authoring QA'da explicit justification ister, çünkü semantik olarak çoğu durumda çelişkilidir.

---

# 9. Objective evidence profile

5B, GRE-v0'daki Objective profile alanlarını graph contract'a bağlar.

```text
ObjectiveEvidenceProfile
- acceptable_evidence_types[]
- direct_evidence_types[]
- required_direct_type?
- min_independent_groups
- min_variant_families
- requires_non_basic_evidence
- requires_transfer
- requires_user_authored_artifact
- allowed_tools_policy
- evaluator_requirement?
```

Bu alanlar numeric mastery multiplier değildir; gate/eligibility semantics'tir.

AŞAMA 6 authoring reusable evidence-profile templates kullanabilir. Ancak published graph version'da effective/resolved profile izlenebilir olmalıdır; mutable template değişikliği geçmiş Objective semantics'ini sessizce değiştiremez.

GRE-v0 cold-start default sayıları aynı kalır; 5B yeni scientific optimum icat etmez.

---

# 10. Topic ↔ Skill many-to-many relation

Canonical relation:

```text
TopicSkillLink
- topic_id
- skill_id
- link_version
- role
- importance
- is_primary_teaching_context
- objective_scope_ids[]?
- authoring_notes?
```

`role`:

```text
teach
practice
assess
reinforce
transfer
integrate
```

Bir Topic aynı Skill'i birden fazla rolle kullanabilir; physical schema 9C'de normalize edilebilir.

`importance` semantic enum olmalıdır; false precision numeric weight değildir:

```text
core
supporting
incidental
```

`incidental` link otomatik mastery evidence hedefi değildir.

---

# 11. Curriculum placement canonical Skill identity'den ayrıdır

Bir Skill'in curriculum'da nerede ilk kez öğretildiği ile Skill'in kimliği ayrılır.

Opsiyonel metadata:

```text
SkillPlacementProfile
- skill_id
- primary_teaching_topic_id?
- introduction_topic_ids[]
- practice_topic_ids[]
- transfer_topic_ids[]
```

Bu profile navigation/authoring kolaylığı sağlar; learner state placement başına çoğalmaz.

Bir Skill başka Domain'de yeniden kullanıldığında:

```text
new TopicSkillLink
```

oluşturulur; yeni Skill clone'u oluşturulmaz.

---

# 12. Skill → Skill prerequisite edge

Canonical runtime prerequisite edge PRG-v0 ile aynıdır.

```text
SkillPrerequisiteEdge
- prerequisite_skill_id
- target_skill_id
- edge_version
- edge_kind: hard | soft
- reason_kind
- strictness_profile
- authoring_rationale
- source_refs[]
- provenance
- lifecycle_status
```

Direction her zaman:

```text
prerequisite source -> dependent target
```

## 12.1 `reason_kind`

Controlled semantic categories:

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

`reason_kind` yeni gate algoritması değildir; authoring/explainability/QA bilgisidir.

## 12.2 Strictness

`hard|soft` canonical davranışı PRG-v0'dadır.

`strictness_profile` ancak existing PRG metadata'yı ifade eder; örneğin critical prerequisite veya strict target assessment'ın `uncertain` state'e nasıl davranacağını belirtir. 5B PRG-v0'ı yeniden yazmaz.

## 12.3 Yasaklar

- Domain→Domain relation doğrudan hard runtime gate'e çevrilmez.
- Topic completion prerequisite-ready kabul edilmez.
- Soft edge eksikliği hard block üretmez.
- `review_due` edge'i otomatik not-ready yapmaz.

---

# 13. Domain / Module / Topic authoring relations

PDM-v0'daki yüksek seviye ilişkiler korunur fakat runtime Skill gate'inden ayrılır.

```text
AuthoringRelation
- source_entity_id
- target_entity_id
- relation_kind
- rationale
- source_refs[]
```

Örnek `relation_kind`:

```text
recommended_before
can_run_parallel
builds_toward
reuses_capabilities_from
integration_of
professional_evidence_overlay
```

Bu relation'lar curriculum map tasarlayan AŞAMA 6'ya rehberlik eder. Planner bunları tek başına `blocked` üretmek için kullanamaz.

---

# 14. Required / critical / optional capability semantics

`required | critical | optional` tek global Skill boolean'ı olarak modellenmez; **scope-relative** olmalıdır.

Aynı Skill:
- V1 başlangıç curriculum'unda optional,
- professional route'ta required,
- belirli capstone'da critical

olabilir.

Canonical contract:

```text
CapabilityRequirement
- scope_kind
- scope_id
- capability_kind: skill | objective
- capability_id
- requirement_role: required | optional
- criticality: standard | critical
- rationale
- source_refs[]
```

`scope_kind` örnekleri:

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

Kritik invariant:

> **Global `skill.required=true` ile bütün rotayı gereksiz kilitlemek yasaktır. Requirement her zaman hangi scope için geçerli olduğunu söylemelidir.**

Objective'in kendi Skill mastery gate'i için `LearningObjective.requirement_role/criticality` ayrı ve daha lokal contract'tır.

---

# 15. Assessment / QAB bağlantısı

Knowledge graph Question Bank içeriğini kopyalamaz.

QAB resource target'ları canonical graph ID'lerine bağlanır:

```text
AssessmentResource.target_skill_ids[]
AssessmentResource.target_objective_ids[]
AssessmentResource.required_skill_ids[]
```

Graph tarafında authoring kolaylığı için opsiyonel reverse refs/index facets bulunabilir:

```text
AssessmentBinding
- objective_id
- resource_id
- minimum_resource_version?
- declared_role?
```

Fakat source of truth assessment semantics QAB resource version'ıdır.

Kural:

```text
resource link exists != mastery evidence exists
```

Gerçek Attempt/Artifact yine eligibility → prerequisite → assistance/provenance → evaluator → attribution → GRE/RVR pipeline'ından geçer.

---

# 16. Retention metadata

Skill authoring metadata RVR-v0 ile hizalıdır:

```text
retention_profile:
  factual
  standard
  complex

critical_prerequisite: true | false
```

Review interval değerleri graph node'a bilimsel gerçek gibi gömülmez; policy/config version tarafından yönetilir.

Objective-level özel retention requirement gerekiyorsa:

```text
ObjectiveRetentionRequirement
- objective_id
- retention_evidence_kind?
- delayed_revalidation_required
- context_diversity_requirement?
```

kullanılabilir; ancak RVR-v0 state machine değişmez.

---

# 17. Diagnostic metadata

VDW-v0 ile uyum için Skill/Objective şu metadata'yı taşıyabilir:

```text
diagnostic_eligibility: eligible | restricted | not_eligible
diagnostic_profile_ref?
```

Diagnostic:
- mastery'nin kolay alternatifi değildir,
- required prerequisite'i bypass edemez,
- waiver yalnız exact validated Objective/Skill scope için geçerlidir.

Graph yalnız eligibility ve authoring metadata sağlar; actual waiver decision VDW-v0 runtime behavior'dır.

---

# 18. Remediation metadata

Curriculum graph learner-specific weakness state taşımaz. Static remediation metadata yalnız targeted response üretmeye yardım eder.

```text
remediation_tags[]
misconception_family_refs[]?
repair_prerequisite_skill_ids[]?
recommended_evidence_modalities[]?
```

Learner state ayrı runtime storage'dadır.

Kritik invariant:

```text
static curriculum metadata != user weakness state
```

Bir Objective failure'ı bütün Topic/Domain'e broad remediation açmaz. Attribution exact Skill/Objective'e lokal kalır; Topic/Domain weakness summary derived olabilir.

---

# 19. Technical English prerequisite contract

D-006 / `ENGLISH_FOUNDATION_RULES` korunur.

Cross-domain English Skill edge desteklenir; fakat yalnız gerçekten target behavior için zorunluysa `hard` olabilir.

Teknik capability ölçen content için:
- bilinmeyen grammar/vocabulary hidden prerequisite olamaz,
- Turkish/bilingual scaffold capability target'ı bozmuyorsa kullanılabilir,
- English eksikliği technical Skill'e negative evidence yazdıramaz.

English-specific capability için:
- öğretilmiş grammar/vocabulary Skill'leri normal Skill prerequisite edge olarak modellenebilir.

Authoring metadata:

```text
LanguageSupportProfile
- target_language_is_evidence
- language_prerequisite_skill_ids[]
- bilingual_scaffold_allowed
- scaffold_reduction_policy_ref?
```

Task/resource seviyesindeki exact language prerequisite yine QAB/Task metadata tarafından deklaratif taşınır.

---

# 20. Professional capability attribution

Professional readiness broad course completion'dan türetilmez. Skill/Objectives professional capability family'lerine explicit attribution taşıyabilir.

```text
ProfessionalCapabilityAttribution
- capability_id
- professional_family_id
- attribution_role: supporting | direct | critical
- context_requirements[]?
- evidence_expectation_ref?
```

Örnek family'ler:
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
- open-source workflow.

Bu attribution readiness kararı değildir; AŞAMA 20 ve capstone layer'ın hangi capability evidence'larını toplaması gerektiğini tanımlar.

---

# 21. Project / capstone attribution

Skill/Objective bir project/capstone'a `covered` diye etiketlendiğinde global project PASS otomatik mastery kanıtı değildir.

```text
ProjectCapabilityAttribution
- project_or_capstone_id
- skill_id
- objective_id?
- role: supporting | required | critical
- structurally_essential
- separately_observable
- expected_evidence_type
- rubric_component_ref?
```

Kritik invariant QAB/PRG ile aynıdır:

```text
project success != all tagged Objectives PASS
```

Direct evidence için target behavior ayrı gözlenebilir ve prerequisite-valid olmalıdır.

---

# 22. Provenance ve source contract

Her semantic graph entity/edge'in değişiklik kaynağı izlenebilir olmalıdır.

```text
Provenance
- authored_by
- authored_at
- source_refs[]
- research_ref?
- decision_refs[]
- review_refs[]
- change_reason
```

`source_refs[]` yalnız URL olmak zorunda değildir; canonical repo research/spec/reference kimliği de olabilir.

AŞAMA 6H external Research QA bulguları source/review refs olarak graph authoring history'sine bağlanabilir.

---

# 23. Technology/content freshness

Stable systems concept ile version-sensitive tooling ayrılır.

```text
FreshnessPolicy
- freshness_class:
    evergreen
    version_sensitive
    fast_moving
- technology_dependency_refs[]?
- review_trigger_policy
```

Actual mutable audit sonucu ayrı kayıttır:

```text
FreshnessRecord
- entity_id
- entity_version
- status: current | review_due | stale | disputed
- checked_at
- source_refs[]
- checked_by
```

Böylece sırf `review_due` oldu diye semantic entity version değiştirilmez; fakat stale technology-dependent node/resource high-stakes selection için bloke edilebilir.

Vendor/tool isimleri stable Skill identity'yi gereksiz yere parçalamamalıdır.

Örnek:
- stable Skill: dynamic batching trade-off'larını ölçüp açıklamak,
- tool-specific Topic/resource: belirli vLLM sürümünde ilgili config davranışı.

---

# 24. Curriculum graph version

Published graph immutable snapshot gibi davranır.

```text
CurriculumGraphVersion
- graph_version_id
- parent_graph_version_id?
- schema_contract_version
- published_at
- entity_version_refs[]
- relation_version_refs[]
- content_hash?
- migration_manifest_ref?
- release_notes_ref?
```

Runtime session/Attempt mümkün olduğunda exact graph version'ı kaydeder.

Kural:

```text
same graph version + same curriculum state snapshot + same policy version
→ same semantic graph interpretation
```

---

# 25. Entity versioning

Yeni version gerektiren semantic değişiklik örnekleri:
- Skill capability meaning değişti,
- Objective observable outcome değişti,
- required/critical gate değişti,
- prerequisite edge hard↔soft değişti,
- target Skill/Objective mapping değişti,
- evidence profile değişti,
- professional/capstone critical attribution anlamlı değişti.

Basit typo/display düzeltmesi için implementation daha hafif patch destekleyebilir; audit açısından safe default yeni version'dır.

Published semantic version overwrite edilmez.

---

# 26. Migration contract

Graph version değişimi learner history'yi silmez ve mastery'yi kör kopyalamaz.

```text
GraphMigrationEntry
- old_entity_id
- old_entity_version
- action
- new_entity_refs[]
- evidence_compatibility
- migration_reason
- review_required
```

`action`:

```text
unchanged
metadata_only
placement_moved
superseded_by
split_into
merged_into
retired
```

`evidence_compatibility`:

```text
fully_compatible
compatible_with_reverification
not_automatically_transferable
```

## 26.1 Rename / placement move

Semantic capability değişmiyorsa learner mastery/history korunabilir.

## 26.2 Skill split

Bir broad Skill iki yeni Skill'e ayrıldıysa eski mastery iki yeni Skill'e otomatik kopyalanmaz.

Migration:
- historical evidence retained,
- Objective-level evidence hangi yeni capability'ye güvenle map edilebiliyorsa taşınabilir,
- belirsiz kısım fresh verification ister.

## 26.3 Merge

İki Skill tek yeni Skill'e merge edildiğinde yüksek eski score'lar kör average yapılmaz. Yeni Skill'in required/critical Objective gate'leri yeniden değerlendirilir.

## 26.4 Semantic Objective change

Objective meaning değiştiyse eski evidence yeni Objective'e yalnız explicit compatibility varsa katkı verir.

Kritik invariant:

> **Curriculum refactor learner'a bedava mastery veremez ve geçerli historical evidence'ı sessizce silemez.**

---

# 27. Lifecycle

Curriculum entity baseline lifecycle:

```text
draft
published
deprecated
retired
invalidated
```

- `draft`: runtime selection yok.
- `published`: graph version'da aktif kullanılabilir.
- `deprecated`: yeni authoring/selection'da tercih edilmez; history korunur.
- `retired`: active route'tan çıkarılmıştır; audit/history korunur.
- `invalidated`: semantic integrity problemi vardır; affected learner evidence review gerekebilir.

`deprecated != wrong`.

---

# 28. Graph validation invariants

Published graph en az şu structural kontrolleri geçmelidir:

1. bütün referenced IDs/versions mevcut,
2. logical ID uniqueness korunuyor,
3. Objective exactly one Skill'e bağlı,
4. TopicSkillLink dangling değil,
5. hard Skill prerequisite subgraph cycle içermiyor,
6. self prerequisite yok,
7. hard/soft edge aynı source-target için çelişkili duplicate değil,
8. required/critical Objective'lerin evidence profile'ı ölçülebilir,
9. coding/debugging/transfer Objective uygun direct modality gerektiriyor,
10. critical production Objective artifact gate'i taşıyor,
11. English hidden-prerequisite guard ihlal edilmiyor,
12. project/capstone attribution direct evidence'ı global PASS'ten türetmiyor,
13. stale/invalidated node active high-stakes route'a sessizce girmiyor,
14. migration manifest semantic node değişikliklerini kapsıyor.

5D ayrıca:
- dead-end,
- unreachable capability,
- duplicate semantic Skill,
- hidden prerequisite,
- over-fragmentation,
- under-fragmentation,
- cross-domain coverage

QA'sı yapacaktır.

---

# 29. Duplicate Skill guard

AŞAMA 6 decomposition sırasında aynı capability farklı domain ekipleri tarafından tekrar üretilebilir.

Yeni Skill açılmadan önce authoring resolver:

```text
canonical_name/aliases
capability statement
objective set
prerequisite neighborhood
professional tags
```

üzerinden candidate duplicate araması yapmalıdır.

Semantic olarak aynıysa yeni Skill yerine mevcut Skill'e yeni TopicSkillLink eklenir.

Fuzzy/LLM duplicate suggestion yalnız yardımcı sinyaldir; otomatik merge kararı değildir.

---

# 30. Index / traversal / performance contract — D-028

4+ yıllık graph yüzlerce/binlerce node'a büyüyebilir. Runtime her planner kararında full graph taramamalıdır.

Minimum logical indexes:

```text
skill -> objectives
skill -> prerequisite incoming/outgoing edges
skill -> reverse dependents
topic -> linked skills
skill -> linked topics
objective -> assessment-resource facets
professional tag -> skills/objectives
project/capstone -> capability attributions
freshness status -> affected entities
```

Runtime invariants:
- adjacency lookup bounded/indexed,
- state change affected downstream set üzerinden invalidate edilebilir,
- graph cache `graph_version_id` ile key'lenebilir,
- large reverse traversal background/async olabilir,
- UI thread'de full graph recomputation yasaktır,
- search/filter indexes denormalized cache olabilir fakat canonical identity'yi değiştirmez.

5B universal sabit `max_depth=5` veya `max_nodes=500` icat etmez. Exact budgets 9F/18E gerçek cihaz benchmark'larıyla kalibre edilir.

---

# 31. Determinism

Aynı:
- graph version,
- entity/relation versions,
- policy versions,
- learner mastery/retention/remediation snapshot,
- assessment/task metadata

ile graph resolver aynı semantic capability/prerequisite/requirement yorumunu üretmelidir.

LLM:
- missing edge önerebilir,
- duplicate candidate işaretleyebilir,
- authoring açıklaması üretebilir,

ama runtime canonical edge/state'i kendiliğinden değiştiremez.

---

# 32. Minimal published Skill örneği

```yaml
skill_id: skill.programming.while_termination
skill_version: 1
canonical_name: While termination condition kurabilmek
capability_kind: programming_reasoning
retention_profile: standard
critical_prerequisite: false
lifecycle_status: published
remediation_tags:
  - infinite_loop
professional_capability_tags:
  - debugging_foundation
```

Objectives:

```yaml
- objective_id: objective.programming.while_termination.identify_state_change
  objective_version: 1
  skill_id: skill.programming.while_termination
  requirement_role: required
  criticality: standard
  objective_statement: >
    Verilen basit bir while döngüsünde termination condition'ın hangi state
    değişimine bağlı olduğunu belirler ve neden sona ereceğini açıklar.
  evidence_profile:
    acceptable_evidence_types: [concept_recall, code_reading, explanation]
    direct_evidence_types: [code_reading, explanation]
    min_independent_groups: 2
    min_variant_families: 2
    requires_non_basic_evidence: false
    requires_transfer: false
    requires_user_authored_artifact: false

- objective_id: objective.programming.while_termination.debug_infinite_loop
  objective_version: 1
  skill_id: skill.programming.while_termination
  requirement_role: required
  criticality: critical
  objective_statement: >
    Yeni bir while-loop örneğinde termination state'in güncellenmediği infinite-loop
    nedenini bağımsız olarak izole eder ve doğru fix'i üretir.
  evidence_profile:
    acceptable_evidence_types: [debugging]
    direct_evidence_types: [debugging]
    required_direct_type: debugging
    min_independent_groups: 3
    min_variant_families: 2
    requires_non_basic_evidence: true
    requires_transfer: true
    requires_user_authored_artifact: false
```

Placements:

```yaml
- topic_id: topic.python.loops
  skill_id: skill.programming.while_termination
  role: teach
  importance: core

- topic_id: topic.c.control_flow.loops
  skill_id: skill.programming.while_termination
  role: reinforce
  importance: core
```

Bu örnek iki dil bağlamında aynı mental capability'nin gerçekten aynı Skill olduğu varsayımını göstermek içindir. AŞAMA 6A semantic granularity QA sırasında dil-spesifik davranışlar ayrı Skill gerektiriyorsa ayrıştırılabilir; identity kararı isim benzerliğine göre otomatik verilmez.

---

# 33. 5C handoff — V1 backbone

5C ilk 8–12 haftalık başlangıç alt grafını bu contract ile üretir.

5C minimum:
- kullanılan Domain/Module/Topic entity'lerini,
- canonical Skill/Objective IDs'lerini,
- TopicSkillLink'leri,
- hard/soft prerequisite edge'lerini,
- Skill içi required/critical Objective gate'lerini,
- evidence profiles,
- retention profiles,
- English safety metadata'sını,
- source/version metadata'sını

tanımlayabilmelidir.

5C production lesson body yazmak zorunda değildir; AŞAMA 15 bunu doldurur.

---

# 34. AŞAMA 6 handoff

6A–6H bu contract'ı kullanacaktır.

- 6A exact naming/granularity standardını finalize eder.
- 6B decomposition template'i bu entity/relation contract'a map eder.
- 6C–6F yüzlerce/binlerce capability node'unu üretir.
- 6G weakness/remediation mapping'i Objective/Skill identity üzerinde kurar.
- 6H external Research QA coverage/prerequisite/freshness/duplicate bulgularını provenance + graph QA'ya bağlar.

AŞAMA 6, 5B contract'ı sessizce değiştiremez. Schema yetersiz kalırsa explicit yeni contract version + decision gerekir.

---

# 35. 9C implementation handoff

Bu belge conceptual/logical contract'tır.

9C şu physical soruları çözecektir:
- Room/SQLite table normalization,
- composite keys/indexler,
- immutable entity/resource versions storage,
- graph-version snapshot/delta representation,
- migration execution tables,
- learner state ile curriculum state foreign-key strategy,
- years-long attempt/evidence history,
- cache invalidation ve query plans.

5B physical DB schema dayatmaz.

---

# 36. Anti-patterns

Yasaklar:

1. Aynı Skill'i her Topic altında clone etmek.
2. Topic completion'ı Skill mastery yapmak.
3. Domain/Module relation'ı doğrudan hard runtime prerequisite yapmak.
4. Global `Python required=true` gibi scope'suz requirement kullanmak.
5. Criticality'yi numeric weight'e çevirmek.
6. Assessment resource var diye mastery yazmak.
7. Project PASS'i bütün tagged Skills/Objectives'e otomatik evidence saymak.
8. English'i global technical hard gate yapmak.
9. Vendor/tool adını stable capability identity yerine geçirmek.
10. Published semantic entity/version'ı sessiz overwrite etmek.
11. Skill split/merge sırasında mastery'yi kör kopyalamak/average etmek.
12. `review_due` veya freshness `review_due` durumunu negative learner evidence yapmak.
13. User-specific weakness'i static curriculum node içine yazmak.
14. Runtime planner için full graph scan'e güvenmek.
15. LLM'nin canonical graph edge/state'i doğrudan mutate etmesine izin vermek.

---

# 37. Acceptance gate — 5B

5B ancak aşağıdakiler sağlandığında tamamlanır:

- Domain/Module/Topic/Skill/Objective contract açık,
- organization vs capability identity ayrımı açık,
- Topic↔Skill many-to-many destekli,
- Objective exactly-one-Skill invariant açık,
- Skill→Skill hard/soft PRG-v0 edge contract açık,
- scope-relative required/critical/optional semantics açık,
- GRE Objective evidence profile graph'a bağlandı,
- QAB assessment refs source-of-truth ayrımı korundu,
- RVR retention profile bağlandı,
- diagnostic/remediation metadata learner state'ten ayrıldı,
- English hidden-prerequisite guard bağlandı,
- professional/project/capstone attribution granular ve non-compensatory,
- provenance/freshness tanımlı,
- graph/entity versioning + conservative migration tanımlı,
- cycle/dangling/duplicate/hidden prerequisite QA hooks tanımlı,
- bounded indexing/traversal/performance invariants tanımlı,
- 5C ve AŞAMA 6 handoff'u açık,
- mevcut GRE/RVR/PRG/QAB/AIV davranışları sessizce değiştirilmedi.

**Sonuç:** `KGC-v0` 5B için kabul edilebilir canonical knowledge-graph contract'ıdır.
