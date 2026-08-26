# Granularity & Naming Standard — GNS-v0

**Adım:** 6A — Granularity + naming standardı  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Final model:** `GNS-v0 — Granularity & Naming Standard`  
**Karar:** D-054

Bu belge AŞAMA 6 boyunca 23 ana route family'nin yüzlerce/binlerce capability node'una ayrılırken **hangi semantic sınırda Domain, Module, Topic, Skill veya Learning Objective yaratılacağını**, canonical logical ID'lerin nasıl adlandırılacağını ve FBB-v0 authoring seed'lerinin nasıl ratify/refactor edileceğini tanımlar.

Bağlayıcı kaynaklar:
- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — KGC-v0 / D-051
- `docs/CURRICULUM_DOMAIN_MAP.md` — PDM-v0 / D-049
- `docs/V1_FOUNDATION_BACKBONE.md` — FBB-v0 / D-052
- `docs/GRAPH_ARCHITECTURE_QA.md` — GQA-v0 / D-053
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — D-044
- `docs/PROJECT_MEMORY_PROTOCOL.md` — D-050

Ana ilke:

> **Granularity takvim, chapter boyu veya keyword sayısına göre değil; bağımsız öğrenilebilirlik, gözlemlenebilir evidence, prerequisite ayrımı, hedefli remediation ve cross-context reuse açısından anlamlı capability sınırına göre belirlenir.**

İkinci ilke:

> **Canonical identity curriculum placement değildir. Aynı semantic capability farklı Topic/Domain bağlamlarında tekrar kullanılıyorsa yeni Skill clone'u yaratılmaz; canonical Skill yeniden linklenir.**

Üçüncü ilke:

> **Logical ID insan arayüzündeki başlık değildir. Display label, açıklama, öğretim sırası veya placement değişebilir; semantic capability identity değişmedikçe logical ID korunur.**

---

# 1. 6A'nın sınırı

6A kesinleştirir:
- Domain / Module / Topic / Skill / Learning Objective semantic sınırlarını,
- under-fragmentation ve over-fragmentation guard'larını,
- Skill split/keep/reuse karar standardını,
- shared vs language/tool/context-specific capability ayrımını,
- Objective atomicity / observable-action standardını,
- canonical logical ID convention'ını,
- display label / alias / localization ile identity ayrımını,
- logical identity vs entity version değişim kurallarını,
- FBB authoring seed ratification/refactor lifecycle'ını,
- 6B decomposition template'inin zorunlu naming/granularity alanlarını.

6A kesinleştirmez:
- 23 route family'nin gerçek tam node listesini → 6C–6F (6B yalnız ortak blueprint'i kilitler),
- Foundations'taki her FBB seed'in final split/merge sonucunu → 6C,
- English CEFR progression/cadence'ini → AŞAMA 7,
- production lesson/task/resource body'lerini → AŞAMA 15,
- physical DB schema'sını → 9C,
- GRE/PRG/RVR algoritmalarını → mevcut canonical specs korunur,
- full curriculum coverage/current-industry doğrulamasını → 6H external Research QA.

---

# 2. Canonical entity-level granularity standardı

## 2.1 Domain

**Domain**, yıllar boyunca anlamlı kalacak geniş professional capability alanıdır.

Yeni Domain ancak aşağıdakiler anlamlı biçimde doğruysa açılır:
- profesyonel coverage açısından ayrı bir büyük alanı temsil eder,
- birden fazla Module/Topic/capability family barındırır,
- curriculum navigation/coverage raporunda bağımsız anlam taşır,
- yalnız tek araç, tek ders veya geçici teknoloji sürümü değildir.

Domain **mastery atomu değildir** ve runtime hard prerequisite değildir.

İyi örnekler:
- Python
- Computer Architecture
- Distributed Systems
- GPU Architecture
- Technical English

Kötü Domain örnekleri:
- `Week 4`
- `for loop`
- `NVIDIA H100 menu options`
- `Chapter 7`

## 2.2 Module

**Module**, tek bir primary Domain içinde birden fazla Topic'i organize eden anlamlı authoring kümesidir.

Module açmak için:
- aynı professional/technical theme altında birden fazla öğretim bağlamını gruplamalı,
- learner navigation ve authoring açısından tek Topic'ten daha geniş anlam taşımalı,
- sırf dosya boyunu bölmek veya haftalara ayırmak için yaratılmamalıdır.

Module'ın sınırı mastery değil organization sınırıdır.

İyi örnek:
```text
Domain: Python
Module: Control Flow
Topics: Conditionals, Iteration, Loop Debugging
```

Kötü örnek:
```text
Module: Week 2 Part A
```

## 2.3 Topic

**Topic**, kullanıcıya gösterilebilen öğretim/çalışma bağlamı ve resource paketleme anchor'ıdır.

Topic:
- bir veya daha fazla canonical Skill'i teach/practice/assess/reinforce/transfer rolüyle bağlayabilir,
- lesson/practice/remediation/resource ailesi için anlamlı bir bağlam sunar,
- learner-facing başlık olarak anlaşılabilir olmalıdır,
- tek başına mastery kaydı değildir.

Bir Topic'i sırf tek keyword için açmak gerekmez. Buna karşılık aynı Topic içinde birbirinden tamamen kopuk capability aileleri toplanıyorsa Topic fazla geniştir ve ayrılmalıdır.

Örnek:
```text
Topic: Python Iteration
  -> skill.programming.iteration_reasoning
  -> skill.python.for_iteration
  -> skill.python.while_termination
```

## 2.4 Skill

**Skill**, planner/prerequisite/mastery/remediation sisteminin ana canonical capability atomudur.

Bir candidate Skill'in iyi sınırda olması için şu özelliklerin tamamı aranır:
- bağımsız biçimde öğretilebilir veya uygulanabilir,
- en az bir doğrudan evidence yolu ile ayrı gözlemlenebilir,
- başarısızlığı başka capability'lerden anlamlı biçimde ayrıştırılabilir,
- zayıflığında hedefli remediation üretilebilir,
- prerequisite olarak başka work için anlamlı bir sınır oluşturabilir veya başka bağlamlarda reuse edilebilir,
- yalnız bir chapter/keyword/display etiketi değildir.

Skill mümkün olduğunca **“kullanıcı ne yapabiliyor?”** şeklinde tanımlanır.

İyi örnekler:
- `while loop için termination state/condition kurabilmek`
- `memory address ile stored value farkını ayırt edebilmek`
- `repository working-tree değişikliklerini status/diff ile okuyabilmek`
- `bir execution trace üzerinden fault location'ı lokalize edebilmek`

Fazla geniş örnekler:
- `Python`
- `Loops`
- `Pointers`
- `Linux`

Fazla mikro örnekler — bağımsız planner/remediation/evidence değeri yoksa:
- yalnız `while` keyword'ünü yazabilmek,
- yalnız `:` karakterini doğru koymak,
- yalnız `git status` komut adını hatırlamak.

## 2.5 Learning Objective

**Learning Objective**, tek canonical Skill altında tek bir ölçülebilir evidence target'ıdır.

Objective şu üç parçayı taşımalıdır:
```text
koşul / bağlam
+ gözlemlenebilir eylem
+ değerlendirilebilir başarı ölçütü
```

Objective:
- exactly one canonical Skill'e bağlıdır,
- `anlar`, `bilir`, `aşina olur` gibi tek başına gözlemlenemeyen fiillerle yetinmez,
- bir assessment/rubric ile doğrudan doğrulanabilir,
- birbirinden bağımsız iki capability'yi tek Objective'e sıkıştırmaz.

Örnek:
> Verilen yeni bir `while` örneğinde termination state'in güncellenmediği non-termination nedenini lokalize eder ve target behavior'ı bozmadan düzeltir.

Bu Objective bir integrated task içinde başka Objective'lerle birlikte ölçülebilir; yine de identity olarak tek Skill'in evidence target'ıdır.

---

# 3. Skill granularity karar testi — Capability Independence Test

Yeni Skill açmadan veya mevcut Skill'i bölmeden önce authoring şu soruları sorar:

### A — Independent evidence
Candidate capability diğer parçalardan ayrı bir task/rubric ile güvenilir biçimde gözlemlenebilir mi?

### B — Independent failure / remediation
Kullanıcı bu capability'de zayıfken komşu capability'de güçlü olabilir mi ve bu fark planner'ın farklı remediation seçmesini gerektirir mi?

### C — Prerequisite boundary
Bu capability'nin eksikliği yalnız belirli downstream work'u bloke etmeli veya support davranışını değiştirmeli mi?

### D — Reuse boundary
Capability farklı Topic/Module/Domain bağlamlarında aynı semantic yetenek olarak tekrar kullanılabiliyor mu?

### E — Evidence/depth boundary
Komşu behavior'lardan anlamlı biçimde farklı evidence modality, transfer, artifact, retention veya professional-depth gereksinimi var mı?

Karar semantik olarak verilir; puan toplamı kullanılmaz.

```text
ayrı learner state / prerequisite / remediation kararı anlamlıysa
→ ayrı Skill güçlü adaydır

ayrım yalnız syntax tokenı, örnek metni veya curriculum sırasıysa
→ ayrı Skill açma
```

Belirsiz candidate:
```text
needs_granularity_review
```
olarak authoring QA'ya taşınır; rastgele split/merge yapılmaz.

---

# 4. Under-fragmentation guard

Aşağıdaki belirtilerden biri güçlü biçimde varsa Skill fazla geniş olabilir:
- Objectives birbirinden bağımsız başarısızlık profilleri gösteriyor,
- prerequisite edge Skill'in yalnız küçük bir kısmı için geçerli,
- bir alt davranış coding artifact isterken başka alt davranış yalnız concept recognition ile ölçülüyor ve planner bunları ayrı ele almalı,
- aynı Skill için remediation tek hedefe indirgenemiyor,
- Skill'in yalnız bir kısmı başka Domain'de reuse ediliyor,
- retention/freshness/tool requirement'ları semantic olarak farklılaşıyor,
- “bu Skill zayıf” sonucu kullanıcının neyi tekrar etmesi gerektiğini hâlâ söylemiyor.

Bu durumda:
```text
broad Skill
→ candidate child Skills
→ Objective ownership yeniden değerlendir
→ prerequisite/evidence/history migration planla
```

6C–6F decomposition sırasında bu guard aktif uygulanır.

Önemli: FBB-v0'daki `values_variables_expressions`, `pointer_declaration_dereference_basic` gibi birleşik authoring seed'ler **6A tarafından otomatik olarak yanlış ilan edilmez**. 6C, bu standarda göre evidence/prerequisite/remediation ayrışmasını inceleyip ratify veya explicit split kararı verir.

---

# 5. Over-fragmentation guard

Aşağıdaki belirtiler yeni Skill yaratmamak için güçlü sinyaldir:
- iki candidate her zaman aynı prerequisite, aynı evidence ve aynı remediation ile birlikte hareket ediyor,
- birinin ayrı learner state'i planner kararını değiştirmiyor,
- ayrım yalnız keyword/operator/syntax punctuation düzeyinde,
- ayrı Skill yalnız bir tek soru veya tek lesson parçasını temsil ediyor,
- farklı isimlere rağmen semantic capability aynı,
- yeni node yalnız curriculum placement farklı olduğu için açılıyor,
- artifact/rubric'te candidate behavior ayrı gözlemlenemiyor.

Bu durumda:
- aynı Skill altında farklı Objective,
- Topic içinde farklı resource,
- veya aynı Skill'e yeni TopicSkillLink

kullanılması tercih edilir.

Amaç node sayısını minimum yapmak değildir; **diagnostic ve planner açısından anlamlı olmayan node çoğalmasını önlemektir.**

---

# 6. Skill mi Objective mi?

Pratik karar:

```text
Aynı kalıcı capability'nin farklı gözlemlenebilir kanıtları
→ aynı Skill altında ayrı Objectives

Bağımsız mastery/prerequisite/remediation state'i gerektiren behavior
→ ayrı Skill
```

Örnek:

```text
Skill: skill.programming.iteration_reasoning

Objectives:
- trace_iteration
- identify_termination_state
- detect_nontermination_cause
```

Ancak Python syntax ile gerçek loop üretme ayrıca language-specific production capability ise:
```text
skill.python.for_iteration
skill.python.while_termination
```
şeklinde ayrı Skill olabilir.

Objective sayısını artırmak Skill'i otomatik parçalamaz. Skill split kararı learner state ve planner davranışı açısından gerekçelendirilmelidir.

---

# 7. Shared vs language/tool/context-specific Skill standardı

## 7.1 Shared Skill

Capability farklı bağlamlarda özünde aynıysa ve bir bağlamdaki valid evidence diğer bağlamdaki capability hakkında anlamlı bilgi taşıyorsa canonical shared Skill tercih edilir.

Örnek:
- `skill.programming.iteration_reasoning`
- `skill.programming.state_assignment_model`
- `skill.programming.debug_localization_basic` authoring seed'inin semantic çekirdeği

Python/C/DS&A Topic'leri bu Skill'i TopicSkillLink ile reuse edebilir.

## 7.2 Language-specific Skill

Dil syntax'ı, runtime behavior'ı, standard library'si veya production procedure'ı capability'nin özünü değiştiriyorsa ayrı Skill gerekir.

Örnek:
- `skill.python.for_iteration`
- C'de language-specific loop production capability
- Python import/module kullanımı
- C pointer declaration/dereference procedure'ı

Python mastery C syntax mastery'yi otomatik vermez.

## 7.3 Tool-specific Skill

Tool kullanımı kendi başına profesyonel capability ise tool-specific Skill olabilir:
- Git repository state inspection,
- compiler/toolchain invocation,
- CUDA kernel launch/debugging gibi platform-specific production behavior.

Fakat vendor/tool adı generic systems concept'in yerine kullanılmaz.

Örnek ayrım:
```text
generic concept: scheduling fairness reasoning
vendor-specific task: belirli serving engine scheduler option'ını yapılandırma
```

İkinci tür Skill gerektiğinde source/freshness metadata ile daha sıkı versionlanır.

## 7.4 Context-only fark Skill yaratmaz

Aynı capability farklı senaryo, veri, proje veya story context'inde uygulanıyorsa yeni Skill yerine:
- yeni assessment variant/context,
- yeni TopicSkillLink,
- transfer Objective/resource

kullanılır.

`same capability + different story != new Skill`

---

# 8. Technical English granularity guard

Technical English paralel Skill graph'ıdır; teknik Domain'lerin global prerequisite'i değildir.

6C English decomposition sırasında:
- grammar function,
- vocabulary recognition/production,
- instruction comprehension,
- error-message reading,
- documentation reading,
- technical writing/speaking

capability'leri gerektiğinde ayrı Skill'lere bölünebilir.

Ancak:
- CEFR level'i learner mastery atomu değildir,
- `A1`, `A2`, `B1` gibi alignment bilgisi tek başına logical ID'nin ana semantic identity'si yapılmaz,
- teknik task English-only değilse bilinmeyen grammar hidden technical prerequisite olamaz.

CEFR/technical alignment metadata ve AŞAMA 7 progression design'ında çözülür.

---

# 9. Canonical logical ID convention

## 9.1 Genel format

```text
<entity_type>.<semantic_namespace>.<semantic_slug>[.<subslug>]
```

Entity prefix:
```text
domain.
module.
topic.
skill.
objective.
```

Baseline kurallar:
- lowercase ASCII,
- segment ayırıcı `.`,
- segment içinde `snake_case`,
- locale-independent semantic slug,
- case-sensitive display text ID'ye kopyalanmaz,
- whitespace ve punctuation kullanılmaz,
- ID içine entity version yazılmaz.

## 9.2 Önerilen kalıplar

```text
domain.python
module.python.control_flow
topic.python.iteration
skill.programming.iteration_reasoning
skill.python.while_termination
objective.programming.iteration_reasoning.trace_iteration
objective.python.while_termination.debug_nontermination
```

Objective ID, mümkün olduğunca owning Skill'in semantic namespace/slugu + Objective action slug'ını izler.

Bu dotted path **runtime tree parent ilişkisi değildir**. Parent/placement KGC relation alanlarından gelir. ID namespace readability ve collision prevention içindir.

## 9.3 ID'ye yazılmaması gerekenler

Varsayılan olarak yasak:
- week/day/calendar sırası,
- AŞAMA numarası,
- `FB0/FB1` progression band'i,
- release adı `v1/v2`,
- entity version,
- geçici lesson sıra numarası,
- kullanıcı locale'i,
- difficulty/mastery state,
- `required/optional/critical` rolü.

Örnek kötü ID:
```text
skill.week3.python.loop_v2
skill.fb2.if_statement
objective.a1.english.task4
```

Bu bilgiler metadata'dır.

## 9.4 `basic`, `advanced`, `intro` gibi level kelimeleri

Bu kelimeler yalnız semantic scope'u gerçekten tanımlıyorsa kullanılabilir; sırf curriculum sırası için kullanılmamalıdır.

Kötü:
```text
skill.python.basic_loops
```
çünkü `basic` capability sınırını açıklamıyor.

Daha iyi:
```text
skill.python.while_termination
skill.python.iterable_for_iteration
```

FBB authoring seed'lerinde bulunan `_basic` / `_intro` benzeri slug'lar **provisional** kabul edilir. 6C semantic scope açıkça aynı kalıyorsa ratify edebilir; aksi halde explicit logical-id normalization/refactor mapping üretir.

## 9.5 Vendor ve version adları

Fast-moving tool/version bilgisi stable generic capability ID'ye gömülmez.

```text
skill.inference.continuous_batching_reasoning
```
gibi stable capability tercih edilir.

Gerçek tool-specific capability gerekiyorsa namespace tool'u açıkça taşıyabilir; fakat:
- generic concept yerine geçmez,
- freshness/source metadata zorunludur,
- tool sürümü logical ID yerine entity/content version metadata'sında tutulur.

---

# 10. Display label, canonical name, alias ve localization

`logical_id` kalıcı machine identity'dir.

İnsan-facing alanlar:
- `display_name`,
- `canonical_name`,
- açıklama,
- Türkçe/İngilizce localization,
- aliases

semantic identity değişmiyorsa editorial olarak geliştirilebilir.

Örnek:
```text
skill.programming.iteration_reasoning
```

Türkçe display:
> Döngü ilerleyişi ve bitiş koşulunu akıl yürüterek izleme

İngilizce display:
> Reason about iteration progress and termination

İki ayrı learner Skill değildir.

Alias:
- arama/migration/authoring yardımcı bilgisidir,
- yeni canonical identity üretmez.

Display rename nedeniyle Skill clone'lamak yasaktır.

---

# 11. Capability statement naming standardı

Skill `capability_statement` mümkün olduğunca:
```text
[context gerektiğinde] + yapılabilir davranış/capability
```
biçimindedir.

Tercih:
- `... ayırt edebilmek`
- `... lokalize edebilmek`
- `... kurabilmek`
- `... açıklayabilmek`
- `... doğrulayabilmek`
- `... uygulayabilmek`

Kaçınılır:
- `X bilgisi`
- `X konusu`
- `X'i anlamak`
- `Chapter X`

Semantic capability statement, logical identity değerlendirmesinde display başlığından daha önemlidir.

---

# 12. Learning Objective naming / atomicity standardı

Objective authoring üç katmanda tutulur:

```text
objective_id       = stable machine identity
objective_statement = insan tarafından okunabilir ölçülebilir hedef
observable_action  = evaluator/rubric'in doğrudan izleyeceği davranış
```

Objective atomicity testi:
- tek primary evidence target var mı?
- PASS/FAIL/provisional kararı tek ana capability'ye bağlanabiliyor mu?
- başarısızlık iki farklı Skill arasında belirsiz mi?
- Objective'i ölçmek için öğretilmemiş hidden prerequisite gerekiyor mu?

Bağımsız iki behavior varsa iki Objective üret.

Tek behavior'ın doğal doğrulama zinciri ise tek Objective olabilir. Örneğin debugging task'ında `cause localization → fix → verification` aynı debugging outcome'un ayrılmaz rubric bileşenleriyse tek Objective olarak kalabilir; fakat her parça ayrı learner state gerektiriyorsa Objective/Skill ayrımı yeniden değerlendirilir.

Objective action slug fiil odaklı ve stable olmalıdır:
```text
trace_iteration
identify_termination_state
debug_nontermination
explain_address_vs_value
verify_repository_state
```

`question_3`, `exercise_b`, `lesson_check` gibi content-instance isimleri Objective ID olamaz.

---

# 13. Duplicate Skill resolver standardı

Yeni Skill yaratmadan önce en az şunlar karşılaştırılır:
- existing `skill_id` / aliases,
- capability statement,
- Objective set,
- prerequisite neighborhood,
- evidence modality/depth,
- remediation intent,
- cross-domain placements,
- professional tags.

Karar:
```text
same semantic capability
→ existing canonical Skill + new TopicSkillLink

meaningful independent capability
→ new Skill

uncertain overlap
→ duplicate_review_required
```

String benzerliği veya LLM önerisi otomatik merge yapamaz.

---

# 14. Logical identity vs entity version

KGC-v0 korunur.

## Aynı logical ID, yeni version
Aşağıdaki değişiklikler semantic identity'yi koruyor fakat published meaning/evaluation'ı anlamlı biçimde değiştiriyorsa yeni entity version gerekir:
- capability statement scope'unun netleştirilmesi,
- Objective başarı koşulunun semantic değişimi,
- evidence/depth requirement değişimi,
- critical prerequisite semantics değişimi,
- freshness/tool behavior değişimi.

## Aynı logical ID, editorial update
Semantic meaning değişmiyorsa display/localization/typo/authoring-note düzeltmesi logical identity değiştirmez. Published immutable storage'da exact implementation yine audit/version policy'ye göre izlenebilir.

## Yeni logical ID
Eski ve yeni capability aynı learner state olarak güvenle yorumlanamıyorsa yeni identity gerekir.

Örnek:
```text
broad pointer capability
→ pointer address/value reasoning
+ pointer dereference production
```

Bu gerçek split ise iki yeni logical identity + migration manifest gerekir.

---

# 15. Split / merge / rename / re-home kuralları

## Rename / display rewrite
Semantic capability aynı:
```text
same logical_id
```

## Curriculum re-home
Skill başka Topic/Module/Domain'de öğretilmeye başlıyor:
```text
same skill_id
new/updated TopicSkillLink
```

## Skill split
Tek broad Skill gerçekte bağımsız mastery states içeriyor:
```text
old_skill -> new_skill_A + new_skill_B
```
Historical evidence yalnız Objective-level attribution güvenliyse taşınır; bedava mastery yoktur.

## Skill merge
İki Skill semantik olarak gereksiz duplicate ise:
```text
old_skill_A + old_skill_B -> canonical_skill
```
Evidence kör average edilmez; compatibility explicit çözülür.

## Objective move
Objective'in owning Skill'i değişiyorsa bu yalnız placement edit değildir. Exactly-one-Skill identity semantiği nedeniyle explicit version/migration review gerekir.

---

# 16. FBB-v0 authoring seed ratification standardı

FBB seed'leri learner-published olmadığı için 6C öncesi aşağıdaki status'lardan biri atanmalıdır:

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

Her seed için en az şu rationale kaydedilir:
- semantic capability sınırı,
- neden ayrı learner Skill state gerekli/gereksiz,
- prerequisite etkisi,
- evidence/remediation etkisi,
- shared-vs-specific kararı,
- old → new ID mapping gerekiyorsa migration sınıfı.

FBB `authoring_seed` olduğu için 6C'de ID normalization mümkündür; fakat sessiz overwrite yine yasaktır. Mapping/provenance korunur.

6A **41 FBB Skill'i tek tek ratify etmez**. Bu standardı 6C'nin Foundations detailed map authoring QA'sına devreder.

---

# 17. Topic / Module naming convention

Topic ve Module logical ID'leri primary organization namespace kullanabilir:
```text
module.python.control_flow
topic.python.iteration
topic.c.pointer_dereference
topic.linux.process_io
```

Kurallar:
- display başlığı değişse de semantic organization package aynıysa ID korunabilir,
- `part1`, `week2`, `lesson5` gibi sıra bağımlı slug'lardan kaçınılır,
- Topic'in ID'si içinde bütün bağlı Skill'leri encode etmeye çalışma,
- cross-domain shared Skill Topic ID nedeniyle clone'lanmaz.

Topic/Module yeniden düzenlemesi learner capability history'yi doğrudan değiştirmez.

---

# 18. Domain naming convention

PDM-v0 route family'ler canonical domain envelope'ıdır.

Domain ID:
```text
domain.<stable_route_slug>
```

Örnek:
```text
domain.python
domain.technical_english
domain.computer_architecture
domain.distributed_systems
domain.gpu_architecture
domain.ai_infrastructure
```

PDM route sıra kodu `D01`, `D02` logical ID'ye gömülmez; sıra değişebilir.

---

# 19. Authoring review reason codes

6B–6F decomposition ve 6H QA aşağıdaki structured reason code'ları kullanabilir:

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

Bunlar learner state değildir; authoring/QA explainability içindir.

---

# 20. 6B decomposition template handoff

6B'nin her candidate entity/capability row'u en az şu soruları cevaplayabilmelidir:

```text
entity_type
logical_id_candidate
display_name
semantic_statement
primary_placement
linked/shared placements
parent organization entity
candidate Skill owner / Objective owner
independent evidence path
prerequisite boundary
remediation boundary
reuse boundary
shared_vs_specific rationale
evidence/depth expectation
retention/freshness note
source/provenance
lifecycle_status
granularity_review_code
seed_mapping_if_any
```

Bu liste physical DB schema değildir; decomposition authoring contract'ıdır. 9C physical modelde normalize edilir.

6B ayrıca yeni Skill yaratmadan duplicate resolver uygulamalı ve `needs_granularity_review` kalan candidate'ları 6C–6H QA'ya taşımayı desteklemelidir.

---

# 21. Örnekler

## 21.1 Python loops

Fazla geniş:
```text
skill.python.loops
```

Daha anlamlı ayrım candidate'ı:
```text
skill.programming.iteration_reasoning
skill.python.for_iteration
skill.python.while_termination
```

Objective örnekleri:
```text
objective.programming.iteration_reasoning.trace_iteration
objective.python.while_termination.write_terminating_loop
objective.python.while_termination.debug_nontermination
```

Bu yalnız standardı gösterir; 6C actual final map'te prerequisite/evidence review ile ratify eder.

## 21.2 C pointers

`Pointers` Topic olabilir; fakat tek Skill olarak fazla geniş olabilir.

Candidate capability family:
- address/value reasoning,
- pointer declaration/address-of procedure,
- dereference read/write behavior,
- null/invalid pointer safety,
- pointer arithmetic,
- lifetime/ownership interactions.

Bunların final Skill sınırı 6C'de independent evidence/prerequisite/remediation testine göre belirlenir. Her operator otomatik ayrı Skill değildir.

## 21.3 Git

`git status`, `git diff`, `git add` komutlarının her biri otomatik ayrı Skill değildir.

Capability örneği:
```text
working tree/index durumunu okuyup değişikliğin hangi state'te olduğunu ayırt edebilmek
```

Ayrı workflow behavior (`stage + commit + history verification`) bağımsız evidence/remediation gerektiriyorsa ayrı Skill olabilir.

## 21.4 English

`the`, `a/an`, `to` gibi her token otomatik Skill değildir. Fakat farklı grammar function'ları learner'da bağımsız hata/remediation pattern'i yaratıyor ve üretim task eligibility'sini ayrı etkiliyorsa granular English Skill olarak modellenebilir.

Technical vocabulary recognition ile free technical sentence production aynı Skill olmak zorunda değildir.

## 21.5 GPU / serving

`batching` tek broad Skill olarak kalmak zorunda değildir. 6E'de örneğin:
- throughput/latency trade-off reasoning,
- static vs dynamic/continuous batching behavior,
- scheduling interaction,
- memory/KV interaction

ayrışması gerekebilir.

Ancak belirli engine UI option'larının her biri stable Skill değildir; tool-specific capability gerekiyorsa freshness/version metadata ile ayrılır.

---

# 22. Anti-patterns

Yasak / kaçınılacak davranışlar:
1. Topic adını otomatik Skill'e kopyalamak.
2. Her syntax keyword/operator için Skill yaratmak.
3. Aynı capability'yi farklı Domain'de clone'lamak.
4. Week/stage/lesson sıra bilgisini logical ID'ye gömmek.
5. Display rename nedeniyle yeni Skill identity yaratmak.
6. `basic/advanced` kelimeleriyle semantic sınırı açıklamadan ID üretmek.
7. Vendor/version adını generic systems concept yerine koymak.
8. Objective'e iki bağımsız capability sıkıştırmak.
9. Integrated task PASS'ini tek broad Objective/Skill'e dönüştürmek.
10. FBB authoring seed'i published canonical identity gibi değişmez kabul etmek.
11. Split/merge sırasında learner evidence'ı kör kopyalamak.
12. Domain/Module/Topic granularity'sini runtime hard prerequisite'e dönüştürmek.
13. Node sayısını kalite metriği yapmak.
14. LLM/string-similarity önerisini otomatik duplicate merge kararı yapmak.

---

# 23. 6A acceptance gate

6A ancak aşağıdakiler sağlandığında tamamlanır:

1. Domain/Module/Topic/Skill/Objective semantic sınırları explicit.
2. Skill için independent evidence + remediation + prerequisite + reuse temelli granularity testi var.
3. Under-fragmentation ve over-fragmentation guard'ları var.
4. Skill vs Objective ayrımı açık.
5. Shared vs language/tool/context-specific split kriteri açık.
6. Technical English global-gate güvenliği korunuyor.
7. Canonical logical ID formatı locale-independent/stable/version-free.
8. Display/localization/alias ile identity ayrımı açık.
9. Week/stage/release/band gibi volatile bilgi ID dışında tutuluyor.
10. Vague `basic/advanced/intro` slug'ları için guard var.
11. Vendor/tool/version naming policy var.
12. Logical ID vs entity version vs placement change ayrımı explicit.
13. Split/merge/re-home/objective-move migration kuralları KGC-v0 ile uyumlu.
14. FBB authoring seed ratification status contract'ı tanımlı.
15. 6B decomposition template handoff'u tanımlı.
16. GRE/RVR/PRG/QAB/AIV davranışları sessizce değiştirilmedi.
17. 6H external Research QA zorunluluğu korunuyor.

---

# 24. Research / Coding / Test AI kararı

6A için ayrı external Research AI kullanılmadı.

Gerekçe:
- 6A current-industry coverage veya hangi teknik konuların eksik olduğu araştırması değildir,
- mevcut accepted Learning Engine + KGC + FBB + GQA + PRG contract'larını tek bir authoring/naming standardına formalize eder,
- external coverage ve hidden-prerequisite validation planlandığı gibi 6H'de bağımsız Research AI ile yapılacaktır.

Coding AI kullanılmadı; physical implementation yoktur.

Ayrı Test AI kullanılmadı; 6A spec contract'ı 6B–6F authoring ve 6H QA sırasında uygulanacak reason-code/acceptance guard'ları tanımlar. Runtime implementation testleri sonraki teknik aşamalardadır.

---

# 25. Final 6A kararı

**Final model:** `GNS-v0 — Granularity & Naming Standard`

Canonical decomposition mantığı:

```text
Domain/Module/Topic = organization / teaching context
Skill               = reusable learner capability state
Objective           = atomic observable evidence target

new Skill?
  ├─ independent evidence/remediation/prerequisite/reuse anlamlı → EVET
  └─ yalnız syntax/context/placement farkı → HAYIR; Objective/resource/link kullan

logical_id = stable semantic identity
version    = published semantic revision
placement  = curriculum organization relation
```

**6A sonrası numaralı adım:** `6B — Full-route decomposition blueprint`.
