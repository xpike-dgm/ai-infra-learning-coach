# Planner Task Taxonomy & TaskCandidate Contract — 3B

**Adım:** 3B — Görev kategorileri  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24

Bu belge Aşama 3 planner'ının günlük plana koyabileceği işleri hangi kavramlarla temsil edeceğini tanımlar. `docs/ADAPTIVE_PLANNER_SPEC.md` içindeki 3A capacity contract'ı ile birlikte canonical planner girdisidir.

Bağlayıcı kaynaklar:
- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/MASTERY_FORMULA_V0.md`
- `docs/RETENTION_FORGETTING_SPEC.md`
- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/ADAPTIVE_PLANNER_SPEC.md`

Ana ilke:

> **LearningNeed, TaskCandidate ve EvidenceEvent aynı şey değildir. State bir öğrenme ihtiyacı doğurur; planner bu ihtiyacı karşılayabilecek görev adayları üretir; yalnız gerçek kullanıcı Attempt/Artifact'ı sonradan evidence oluşturabilir.**

İkinci ilke:

> **Bir görevin “neden bugün var olduğu”, “kullanıcının ne yaptığı”, “hangi curriculum hattında olduğu” ve “hangi evidence'ı üretebildiği ayrı eksenlerdir.**

Bu ayrım özellikle `coding`, `retention`, `English`, `remediation`, `project` gibi farklı anlamdaki etiketlerin tek karma enum içine atılmasını engeller.

---

# 1. Canonical planner pipeline

```text
Curriculum + Skill/Mastery + Retention + Remediation state
    ↓
LearningNeed
    ↓
0..N eligible TaskCandidate alternative
    ↓
3C Priority + 3D Prerequisite + 3A Capacity
    ↓
Selected PlannedTask
    ↓
User interaction / Attempt / Artifact
    ↓
EvidenceEvent
    ↓
GRE-v0 / RVR-v0 / remediation state update
    ↓
Replan
```

Kritik sonuç:

- `TaskCandidate` seçilmedi diye negative evidence oluşmaz.
- Task tamamlandı diye otomatik mastery oluşmaz.
- Evidence ancak gerçek Attempt/Artifact ve ilgili evidence kurallarıyla oluşur.

---

# 2. LearningNeed — task'tan önce gelen gerçek ihtiyaç

Planner'ın kalıcı olarak koruması gereken şey çoğu zaman eski bir task ID değil, task'ı doğuran **anlamsal ihtiyaçtır**.

Kavramsal contract:

```text
LearningNeed
- need_key
- trigger_kind
- curriculum_track_id / domain_id
- target_skill_ids
- target_objective_ids
- source_state_refs
- criticality / prerequisite relevance
- due_at? / detected_at?
- open_condition
- resolved_condition
```

## 2.1 Canonical trigger kinds

V1 planner şu ihtiyaç kaynaklarını desteklemelidir:

```text
new_learning
continue_learning
weakness_detected
remediation_required
retention_review_due
verification_due
diagnostic_opportunity
reinforcement_opportunity
parallel_track_due
integration_opportunity
```

Bunlar henüz priority sırası değildir. Priority **3C**'de belirlenir.

## 2.2 Need neden task ID değildir?

Örnek:

`pointer_dereference` için retention review due olsun.

Bugün planner 12 dakikalık bir coding retention task'i üretebilir fakat kapasiteye sığmadığı için seçmeyebilir.

Ertesi gün:
- natural reuse retention ihtiyacını kapatmış olabilir,
- farklı bir 7 dakikalık debugging task'i daha uygun olabilir,
- Skill `verification_due` durumuna geçmiş olabilir,
- kullanıcı yalnız 10 dakika ayırmış olabilir.

Bu yüzden:

```text
old_task_candidate != tomorrow_debt
```

ama:

```text
open LearningNeed remains until its resolved_condition is true
```

Bu 3A'daki `overflow = debt değildir` kuralının yapısal karşılığıdır.

---

# 3. Task'ın dört ayrı ana ekseni

Her TaskCandidate en az dört farklı anlamı ayrı tutar.

## 3.1 `primary_purpose` — neden yaptırıyoruz?

Canonical V1 değerleri:

```text
teach
practice
assess
remediate
retain
diagnose
reinforce
```

### `teach`
Henüz kapsanmamış Skill/Objective'i açıklama, örnekleme veya ilk yapılandırılmış öğrenme.

### `practice`
Öğrenilmiş/öğrenilmekte olan davranışı uygulatma. Guided veya independent olabilir.

### `assess`
Mevcut performansı ölçme. Günlük mikro değerlendirme, mastery verification veya ileride weekly/monthly assessment bu purpose altında olabilir; sınav kompozisyonu Aşama 4'tedir.

### `remediate`
Belirli weakness/misconception/failure sonrası hedefli onarım. Aktivitesi yeni anlatım, micro-drill, debugging veya coding olabilir.

### `retain`
Anlamlı gecikmeden sonra RVR-v0 retention doğrulaması/retrieval. Passive restudy tek başına retention PASS değildir.

### `diagnose`
Kullanıcının bir Skill'i önceden bilip bilmediğini veya hangi alt noktada zorlandığını anlamaya yönelik ölçüm. Skip/waiver davranışı 3E'de kesinleşir.

### `reinforce`
Daha önce öğrenilmiş Skill'i farklı/ileri bağlamda tekrar kullandırma ve şemayı güçlendirme. Uygun H0 doğal kullanım daha sonra retention evidence da oluşturabilir; automatic değildir.

Kural: Her TaskCandidate **tek bir primary purpose** taşır. İkincil hedefler metadata olabilir ama 3C'nin temel priority nedeni primary purpose + LearningNeed üzerinden açıklanır.

---

## 3.2 `activity_kind` — kullanıcı ne yapacak?

Canonical baseline:

```text
content_explanation
worked_example
recognition_selection
recall_free_response
code_reading_trace
coding_production
debugging_diagnosis
hands_on_system_task
explanation_justification
transfer_problem
integrated_project_task
language_activity
```

Bu liste runner/UX geliştikçe alt tiplere ayrılabilir fakat anlamları karıştırılmaz.

### Önemli örnek

`coding_production` bir **activity**'dir; şu farklı amaçlarla kullanılabilir:

```text
practice + coding_production
assess + coding_production
remediate + coding_production
retain + coding_production
reinforce + coding_production
```

Aynı şekilde `debugging_diagnosis` bir activity'dir; `remediate` veya `retain` onun amacı olabilir.

`hands_on_system_task`, Linux terminal, config, networking, process/tool kullanımı gibi kod yazmadan gerçek teknik icra gerektiren işleri kapsar.

---

## 3.3 `curriculum_track/domain` — hangi hatta ait?

English bir `primary_purpose` değildir.

Örnek:

```text
curriculum_track = technical_english
primary_purpose = practice
activity_kind = language_activity
```

veya:

```text
curriculum_track = c_foundations
primary_purpose = retain
activity_kind = coding_production
```

Bu sayede English için ayrı ve tutarsız planner motoru gerekmez; 6A–6E aynı TaskCandidate contract'ını kullanabilir.

English task eligibility'de `ENGLISH_FOUNDATION_RULES.md` içindeki öğretilmemiş grammar/vocabulary prerequisite yasağı korunur.

---

## 3.4 `evidence_contract` — task neyi kanıtlayabilir?

Task category evidence type değildir.

Örnek:

```text
primary_purpose = practice
activity_kind = coding_production
expected_evidence_type = coding_production
```

Task practice amaçlı olsa bile kullanıcı H0, valid, direct ve verified artifact üretirse GRE-v0 kurallarına göre evidence olabilir.

Tersi de geçerlidir: `assess` purpose taşıyan bir task teknik olarak hatalı/contaminated ise valid evidence üretmez.

---

# 4. Practice independence ayrı metadata'dır

Practice için ve gerektiğinde diğer purpose'larda:

```text
independence_mode:
- not_applicable
- guided_allowed
- independent_expected
- h0_required
```

Örnekler:
- worked example → `not_applicable`
- early guided drill → `guided_allowed`
- normal independent practice → `independent_expected`
- mastery/retention verification → çoğunlukla `h0_required`

Assistance gerçekleşirse gerçek evidence sınıfını `AI_ASSISTANCE_EVIDENCE_SPEC.md` ve GRE-v0 belirler; task metadata kullanıcının yardım aldığına dair sahte varsayım üretmez.

---

# 5. Evidence contract

Kavramsal yapı:

```text
EvidenceContract
- evidence_expected: none | optional | required
- expected_evidence_types[]
- objective_attributions[]
- independence_mode
- allowed_tools_policy
- evaluator_requirement
- artifact_requirement?
- retention_context: none | delayed | natural_reuse
```

Her objective attribution en az şunları taşıyabilir:

```text
ObjectiveAttribution
- objective_id
- skill_id
- target_role: primary | secondary
- expected_evidence_type
- maximum_evidence_role: direct | corroborating | contextual
- rubric_id?
- structurally_essential: true | false
- separately_observable: true | false
```

`maximum_evidence_role`, task tasarımının bu Objective için en fazla ne iddia edebileceğini belirtir. Gerçek evidence eligibility yine Attempt/Artifact + assistance + evaluator + prerequisite şartlarıyla hesaplanır.

---

# 6. Purpose ile evidence arasında zorunlu eşitlik yok

Örnekler:

| Learning need | Primary purpose | Activity | Muhtemel evidence |
|---|---|---|---|
| Pointer ilk anlatım | `teach` | `content_explanation` | none/contextual; varsa ayrı micro-check |
| Pointer alıştırması | `practice` | `coding_production` | coding/production |
| Pointer mastery ölçümü | `assess` | `coding_production` | coding/production direct |
| Pointer 30 gün sonra | `retain` | `coding_production` | coding + delayed retention |
| Pointer hatası sonrası | `remediate` | `debugging_diagnosis` | formative/debugging; yardım düzeyine bağlı |
| İleri projede pointer kullanımı | `reinforce` | `integrated_project_task` | yalnız ayrı attribution varsa coding/transfer/retention |
| English article çalışması | `practice` | `language_activity` | English Objective'e uygun recall/production |

Bu tablo priority sırası değildir.

---

# 7. Multi-Skill / integrated task attribution

Bir project veya integrated task birden fazla Skill kullanabilir. Global task success bütün Skill'lere otomatik evidence üretmez.

Her component Skill için ayrı sorular:

1. Bu Skill çözüm için gerçekten gerekli miydi?
2. Kullanıcı target davranışı gerçekten kendisi mi üretti?
3. Davranış task output/trace/rubric içinde ayrı gözlenebiliyor mu?
4. Hangi Objective'e bağlanıyor?
5. Direct mi, corroborating mi?
6. Assistance/provenance temiz mi?

Kural:

```text
project_pass != all_component_skills_pass
```

`structurally_essential=false` veya `separately_observable=false` ise o component için global başarıdan direct mastery/retention evidence türetilmez.

Bu RVR-v0 natural reuse kuralını da korur.

---

# 8. Content / task provenance

Her TaskCandidate'ın kaynağı izlenebilmelidir.

```text
content_origin:
- trusted_prevalidated_bank
- validated_parametric_family
- ai_generated_candidate
- remediation_generated
- planner_composed_integrated_task
- user_selected_or_imported

validation_status:
- trusted
- validated
- provisional
- invalid
```

AI tarafından üretilmiş candidate yalnız `AI üretti` diye yasak değildir; fakat özellikle mastery/retention/diagnostic gibi high-stakes evidence üretecekse Aşama 4D–4E validation kurallarını geçmeden trusted item gibi davranamaz.

Planner task content'i ile evidence evaluator provenance ayrı tutulabilir.

---

# 9. Variant / dependency metadata

Assessment/practice task'ları gerektiğinde şu kimlikleri taşır:

```text
item_id / task_template_id
variant_family_id?
dependency_group_id / testlet_id?
```

Bunlar:
- exact/near repeat guard,
- GRE evidence independence,
- retention novelty,
- transfer çeşitliliği

için kullanılır.

Sadece variable adı/sayı değiştirmek otomatik yeni family değildir.

---

# 10. Prerequisite / eligibility contract

TaskCandidate en az:

```text
required_skill_ids[]
required_objective_ids[]?
forbidden_not_yet_concepts[]?
allowed_tools_policy
```

metadata'sını destekler.

3B yalnız contract'ı tanımlar. Hard/soft eligibility ve planner scheduling davranışı **3D**'de kesinleşir.

Bağlayıcı invariant şimdiden geçerlidir:

> Kullanıcı henüz öğretilmemiş prerequisite yüzünden target Skill için başarısız sayılamaz.

---

# 11. Difficulty ve complexity

TaskCandidate şu metadata'yı destekleyebilir:

```text
difficulty: basic | intermediate | advanced
complexity_tags[]
```

Difficulty priority puanı değildir ve GRE-v0'da numeric mastery multiplier değildir.

Kritik Objective'in non-basic gate'i gibi mevcut kurallar korunur.

---

# 12. Capacity contract — 3A ile birleşim

Her TaskCandidate 3A için en az:

```text
base_estimated_minutes
planning_cost_minutes
estimate_confidence: low | medium | high
splittable: true | false
minimum_safe_chunk_minutes?
checkpoint_ids[]?
atomic_evidence_boundary: true | false
```

alanlarını destekler.

`atomic_evidence_boundary=true` ise assessment artifact'ı veya tek parça production evidence rastgele ortadan kesilmez.

Splittable teaching/project task checkpoint'te pause edilebilir.

---

# 13. Paused task ile deferred candidate ayrımı

### `deferred_candidate`
Henüz başlanmamış ve bugünkü capacity/priority nedeniyle seçilmemiş adaydır.

- failure değildir,
- ertesi güne görev borcu olarak taşınmaz,
- candidate ID yeniden kullanılmak zorunda değildir,
- underlying LearningNeed açık ise ertesi gün fresh alternative üretilebilir.

### `paused_progress`
Kullanıcı gerçekten başlamış ve pedagogically safe checkpoint'te durmuş iştir.

Kavramsal olarak:

```text
ResumeContext
- learning_need_key
- source_task_id
- checkpoint_id
- completed_segments
- remaining_segments
- artifact_state_ref?
```

Ertesi gün bile otomatik `ilk görev` değildir. Planner önce LearningNeed'in hâlâ geçerli olduğunu, prerequisite/state değişmediğini ve devam etmenin en iyi seçenek olduğunu yeniden değerlendirir.

Bu sayede hem gerçek ilerleme kaybolmaz hem lineer homework backlog oluşmaz.

---

# 14. Task lifecycle evidence değildir

Kavramsal lifecycle:

```text
candidate
planned
active
paused
completed
deferred
cancelled
```

Bu state'lerin hiçbiri tek başına mastery evidence değildir.

Özellikle:
- `completed` = task workflow tamamlandı; `correct/mastered` anlamına gelmez,
- `deferred` = öğrenme açığı değildir,
- `cancelled` = negative evidence değildir,
- `paused` = failure değildir.

Evidence ayrı Attempt/Artifact pipeline'ından çıkar.

---

# 15. Canonical TaskCandidate v0 contract

Exact DB schema 8C'ye aittir. 3B davranış contract'ı:

```text
TaskCandidate
- id
- learning_need_key

- primary_purpose
- activity_kind
- curriculum_track_id
- domain_id
- module_id?
- topic_id?

- primary_target_skill_id
- primary_target_objective_id?
- secondary_target_skill_ids[]
- objective_attributions[]

- trigger_kind
- state_signal_refs[]
- criticality

- required_skill_ids[]
- required_objective_ids[]?
- forbidden_not_yet_concepts[]?
- allowed_tools_policy

- evidence_contract
- independence_mode
- difficulty
- complexity_tags[]

- content_origin
- validation_status
- task_template_id?
- item_id?
- variant_family_id?
- dependency_group_id?

- base_estimated_minutes
- planning_cost_minutes
- estimate_confidence
- splittable
- minimum_safe_chunk_minutes?
- checkpoint_ids[]?
- atomic_evidence_boundary

- resume_context_ref?
- candidate_generation_version
```

3C bu primitive'e priority score/reason ekleyecektir; 3B priority ağırlığı belirlemez.

---

# 16. 3C'nin kullanacağı priority input sinyalleri

3B, 3C'ye ham sinyalleri verir; sıralamayı yapmaz.

En az:

```text
primary_purpose
trigger_kind
criticality
prerequisite relevance
retention state / due metadata
remediation state
mastery/weakness refs
continuation status
curriculum track/domain
planning_cost_minutes
candidate validation/trust
```

3C bunlardan deterministic priority/selection policy üretecektir.

---

# 17. Bounded candidate generation ve performans

D-028 gereği bir LearningNeed için sınırsız AI candidate üretimi veya her replan'da tüm curriculum'u tarama zorunlu değildir.

V1 implementasyonu:
- açık LearningNeed'leri indeksleyebilir,
- her need için bounded sayıda candidate alternative üretebilir,
- trusted/prevalidated bank'ı öncelikle kullanabilir,
- generated candidates'i cache/version ile yönetebilir.

Exact cap ve index stratejisi 8C/11C'de belirlenir.

---

# 18. Anti-patterns / yasaklar

Aşağıdaki modeller kullanılmaz:

1. Tek karma enum: `coding | retention | English | remediation | project`.
2. `task completed → mastery`.
3. `project passed → tüm component Skills passed`.
4. `AI generated item → otomatik trusted assessment`.
5. `deferred candidate → tomorrow debt`.
6. `aynı LearningNeed → her zaman aynı exact task`.
7. Task category'ye 3B içinde sabit priority ağırlığı vermek.
8. English'i teknik prerequisite'ten bağımsız tek global blocker yapmak.
9. Guided practice sonucunu H0 independent evidence gibi etiketlemek.
10. Task'in öğretilmemiş prerequisite içermesine rağmen target Skill'e negative evidence yazmak.

---

# 19. Örnekler

## Örnek A — yeni pointer öğretimi

```text
need: new_learning(pointer_dereference)
purpose: teach
activity: content_explanation
track: c_foundations
evidence_expected: optional
```

## Örnek B — pointer bağımsız practice

```text
need: continue_learning(pointer_dereference)
purpose: practice
activity: coding_production
independence: independent_expected
expected_evidence: coding_production
```

## Örnek C — retention

```text
need: retention_review_due(pointer_dereference)
purpose: retain
activity: coding_production
independence: h0_required
expected_evidence: coding_production + delayed_retention
```

## Örnek D — misconception remediation

```text
need: remediation_required(address_vs_value)
purpose: remediate
activity: debugging_diagnosis
independence: guided_allowed
```

Bu task öğrenme için çok değerli olabilir; yardım aldıysa independent mastery evidence olmak zorunda değildir.

## Örnek E — English A0

```text
need: parallel_track_due(article_a_an)
purpose: practice
activity: language_activity
track: technical_english
required_english_prerequisites: previously_taught_only
```

## Örnek F — integrated project

```text
need: integration_opportunity(memory_foundations)
purpose: reinforce
activity: integrated_project_task
targets: pointer + struct + allocation
```

Her component için ayrı `ObjectiveAttribution`; global success automatic component mastery değildir.

## Örnek G — Linux hands-on

```text
need: continue_learning(filesystem_navigation)
purpose: practice
activity: hands_on_system_task
expected_evidence: objective-specific direct execution artifact
```

## Örnek H — diagnostic

```text
need: diagnostic_opportunity(pointer_basics)
purpose: diagnose
activity: code_reading_trace + coding_production
```

Diagnostic'in hangi şartta lesson skip/waiver vereceği 3E'de tanımlanır.

---

# 20. 3B acceptance criteria

3B PASS için:

1. LearningNeed, TaskCandidate ve EvidenceEvent ayrıdır.
2. Deferred task ID yerine underlying open need korunur; no-backlog-debt kuralı yapısaldır.
3. Purpose/activity/track/evidence dört ayrı eksendir.
4. Canonical purpose taxonomy tanımlıdır.
5. Coding/debugging/project/English yanlış biçimde aynı purpose enum'una konmaz.
6. Multi-Skill task'ta component attribution ayrı ve gözlenebilir olmak zorundadır.
7. Global project success automatic component mastery/retention üretmez.
8. Provenance + validation status contract'ı vardır.
9. Variant/dependency metadata GRE/RVR ile uyumludur.
10. Prerequisite/allowed-tools contract'ı vardır.
11. 3A duration/splitting/checkpoint contract'ı TaskCandidate'a bağlanmıştır.
12. Paused progress ile deferred candidate ayrılmıştır.
13. Task lifecycle state'leri evidence değildir.
14. 3C'nin kullanacağı priority input primitive'leri hazırdır fakat priority ağırlıkları 3B'de uydurulmamıştır.
15. English A0 prerequisite kuralı korunur.
16. D-028 bounded/deterministic implementasyon yönü korunur.

---

# Sonraki adım

**3C — Öncelik puanı / task selection priority.**

3C şu soruyu cevaplayacaktır:

> Aynı gün capacity'ye sığmayacak kadar çok açık LearningNeed ve TaskCandidate varsa, hangileri bugün seçilir, hangileri ertelenir ve bu karar hangi explainable priority sinyalleriyle verilir?
