# Daily Micro Assessment Specification — DMA-v0

**Adım:** 4A — Günlük mikro değerlendirme  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24  
**Final model:** `DMA-v0 — Daily Micro Assessment`

Bu belge günlük öğrenme akışı içinde kullanıcının hangi Skill/Learning Objective'lerde gerçekten ne gösterebildiğini düşük sürtünmeyle ama güvenilir biçimde ölçme davranışını tanımlar.

Bağlayıcı kaynaklar:
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0
- `docs/ADAPTIVE_PLANNER_SPEC.md`
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/PRIORITY_POLICY_SPEC.md`
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0
- `docs/PLANNER_EXPLAINABILITY_SPEC.md` — PDT-v0
- `docs/ENGLISH_FOUNDATION_RULES.md`

Ana ilke:

> **Günlük mikro değerlendirme bir günlük quiz kotası değildir. Planner yalnız gerçekten ölçülmeye değer bir Skill/Objective olduğunda, uygun ve güvenilir assessment task'ını mevcut günlük kapasitenin içine yerleştirir.**

İkinci ilke:

> **Assessment sonucu mastery'yi doğrudan yazmaz. Attempt/Artifact önce validity, prerequisite, assistance, provenance, evaluator ve attribution kontrollerinden geçer; yalnız oluşan geçerli EvidenceEvent'leri GRE-v0/RVR-v0 ve ilgili state motorları yorumlar.**

Üçüncü ilke:

> **Kullanıcının henüz öğretilmemiş prerequisite yüzünden yanlış yaptığı, ambiguous/invalid olduğu veya çözümü yardım ile elde ettiği bir item independent negative/positive mastery kanıtı gibi kullanılamaz.**

---

# 1. “Daily” ne demektir?

`daily_micro_assessment`, her takvim gününde zorunlu olarak çalışan ayrı bir sınav değildir.

Doğru anlam:

```text
assessment task günlük planner'ın seçebileceği task türlerinden biridir
```

Bir gün:
- hiç assessment olmayabilir,
- tek kısa independent check olabilir,
- birkaç küçük Objective check olabilir,
- bir küçük coding/debugging artifact bütün micro-assessment ihtiyacını karşılayabilir.

Sabit davranış **yoktur**:

```text
her gün 5 soru
her gün 10 dakika quiz
her gün planın %20'si assessment
```

Bunlar DMA-v0'da canonical değildir.

Assessment ihtiyacı varsa ama bugünkü kapasiteye sığmıyorsa:
- user failure oluşmaz,
- assessment debt oluşmaz,
- unresolved learning/evidence need current state'te kalır,
- sonraki planner run current state'ten yeniden karar verir.

---

# 2. Practice, assessment, retention ve diagnostic birbirinden ayrıdır

## `practice`
Ana amaç öğrenmek/uygulamaktır. Guided olabilir. İpucu ve teaching feedback normaldir.

Practice attempt'i uygun H0 + valid + direct + verified evidence üretirse GRE-v0'a katkı verebilir; fakat practice task olması otomatik mastery evidence değildir.

## `assess`
Ana amaç **mevcut independent performansı ölçmek**tir.

Mastery/verification iddiası için varsayılan:

```text
primary_purpose = assess
independence_mode = h0_required
```

Assessment sırasında solution-revealing yardım alınırsa task öğrenmeye devam edebilir fakat o attempt'in independent assessment iddiası değişir.

## `retain`
Ana neden RVR-v0 delayed retention ihtiyacıdır. UI'da soru gibi görünse bile canonical purpose `retain` olarak kalır.

## `diagnose`
Ana neden prior knowledge / fast-path / placement'tır. VDW-v0 kuralları geçerlidir; `assess` diye yeniden etiketlenmez.

Kritik invariant:

```text
question-like UI != primary_purpose assess
```

Purpose, görevin **neden** bugün verildiğini temsil eder.

---

# 3. Daily micro assessment'ın amaçları

DMA-v0 dört assessment intent'i destekler:

```text
checkpoint
mastery_evidence
verification
integration_check
```

## 3.1 `checkpoint`
Yeni öğrenilen/uygulanan Objective'in temel mental modelinin bağımsız kontrolü.

Amaç:
- teaching/practice sonrası kör ilerlemeyi önlemek,
- gerekirse kısa feedback/remediation açmak,
- tek başına bütün Skill'i mastered ilan etmemek.

## 3.2 `mastery_evidence`
GRE-v0 Objective gate'inde eksik olan gerçek evidence türünü toplamak.

Örnek:
- Objective'in code-reading evidence'ı var ama production gate'i eksik,
- yeni candidate recognition değil `coding_production` seçmelidir.

## 3.3 `verification`
Özellikle `verification_due`, contradiction veya bağımsız yeniden kontrol gerektiren durumda fresh/unseen H0 measurement.

Mevcut PBR/PRG kuralları priority ve blocker davranışını belirler.

## 3.4 `integration_check`
Birden fazla daha önce öğretilmiş Skill'in tek küçük gerçekçi görevde ayrı ayrı gözlenebildiği kontrol.

Global task pass bütün component Skill'lere otomatik evidence vermez. Her component için 3B attribution kuralları geçerlidir.

---

# 4. Normal retention ve diagnostic 4A tarafından yutulmaz

Aynı physical item formatı farklı amaçlarla kullanılabilir.

Örnek:

```text
free-response pointer sorusu
```

şu amaçlardan biriyle gelebilir:
- `assess + checkpoint`,
- `assess + mastery_evidence`,
- `retain`,
- `diagnose`.

Evidence pipeline ortak olabilir; scheduling semantics farklıdır.

Bu ayrım:
- RVR retention state'lerinin yanlışlıkla normal daily quiz'e dönüşmesini,
- diagnostic waiver'ın normal assessment gibi yorumlanmasını,
- planner priority nedenlerinin kaybolmasını

engeller.

---

# 5. Assessment ne zaman aday olabilir?

Bir Objective/Skill için daily micro assessment candidate ancak state açısından anlamlıysa üretilir.

Tipik koşullar:

1. İlgili Objective için gerekli teaching/coverage prerequisite'i karşılanmış veya valid diagnostic waiver vardır.
2. Exact assessment item'ın hard prerequisites'i PRG-v0'a göre uygundur.
3. Current state'te gerçekten information/evidence ihtiyacı vardır.
4. Aynı evidence ihtiyacı bugün zaten yeterli fresh evidence ile kapanmamıştır.
5. Uygun item/task trusted/validated veya hedef evidence için izin verilen güven düzeyindedir.
6. Görev Objective'in gerçekten ölçmek istediği behavior ile eşleşir.
7. Planner bunu PBR-v0 + capacity üzerinden seçebilir.

Bir Objective henüz anlatılmadıysa normal mastery assessment yapılmaz.

İstisna:
- explicit prior-knowledge/placement yolu `diagnose` purpose ile VDW-v0'a gider; bu normal daily assessment değildir.

---

# 6. Assessment için yeni “takvim kuyruğu” yoktur

DMA-v0 ayrı bir günlük assessment backlog sistemi kurmaz.

Assessment task normal canonical zinciri kullanır:

```text
Current State
→ LearningNeed
→ TaskCandidate(primary_purpose=assess)
→ PRG eligibility
→ PBR priority
→ 3A capacity fit
→ PlannedTask
→ Attempt/Artifact
→ EvidenceEvent
→ GRE/RVR/remediation update
→ replan
```

Assessment candidate'ı bugüne seçilmediyse eski task ID yarına taşınmaz.

Açık olan semantic need hâlâ geçerliyse sonraki plan fresh candidate üretebilir.

---

# 7. Assessment ihtiyacı mevcut LearningNeed'lerden türetilir

DMA-v0, 3B taxonomy'yi yeni bir paralel queue ile değiştirmez.

Assessment candidate şu mevcut need bağlamlarından türeyebilir:

- `continue_learning` → checkpoint / mastery evidence,
- `weakness_detected` → bağımsız doğrulama gerekirse,
- `verification_due` → fresh verification,
- `integration_opportunity` → integrated check,
- uygun `new_learning` flow içinde coverage/practice sonrası evidence checkpoint.

`retention_review_due` varsayılan olarak `retain` candidate üretir.  
`diagnostic_opportunity` varsayılan olarak `diagnose` candidate üretir.

Böylece trigger ile task purpose karıştırılmaz.

---

# 8. Hangi Objective bugün ölçülür?

Planner bütün açık Objective'leri her gün test etmez.

Assessment target seçiminde sırasıyla şunlara bakılır:

1. **Geçerli state ihtiyacı:** gerçekten measurement gereken Objective var mı?
2. **Progress relevance:** current öğrenme akışında bu evidence bir sonraki kararı değiştiriyor mu?
3. **Critical/prerequisite relevance:** eksik evidence dependent path açısından anlamlı mı?
4. **Evidence gap:** GRE-v0 gate'inde hangi direct modality/family/context eksik?
5. **Freshness/dependency:** aynı/near item veya aynı dependency group ile evidence şişiriliyor mu?
6. **Prerequisite fairness:** item yalnız öğrenilmiş prerequisites kullanıyor mu?
7. **Validation/trust:** item high-stakes kullanıma uygun mu?
8. **Capacity:** bugün anlamlı atomic assessment sığıyor mu?

DMA-v0 yeni bir weighted priority score icat etmez. Need priority için PBR-v0 kullanılır.

---

# 9. “En fazla bilgi / en az soru” ilkesi

Amaç kullanıcıyı sürekli quiz'e sokmak değil, gereken state kararını mümkün olan en az gereksiz ölçümle desteklemektir.

Planner/item selector:
- aynı sonucu tekrar tekrar kanıtlayan düşük değerli item'ları azaltır,
- Objective'in eksik evidence türünü tercih eder,
- gerektiğinde bir integrated item ile birkaç ayrı observable Objective'i ölçebilir,
- bir Objective için yeterli evidence zaten varsa sırf günlük quota doldurmak için yeni soru eklemez.

Kritik:

```text
minimum_questions != fixed number
```

ve:

```text
one integrated task != automatic multi-skill pass
```

---

# 10. Assessment composition capacity-aware'dır

Daily micro assessment ayrı bir zaman bütçesi almaz.

3A hard budget içindeki normal TaskCandidate'dır.

Örnek:

```text
bugünkü hard budget = 50 dk

20 dk continue learning
15 dk critical verification
10 dk English practice
5 dk checkpoint assessment
```

uygun olabilir.

Başka gün:

```text
bugünkü hard budget = 20 dk
15 dk critical remediation
```

varsa kalan 5 dakikaya güvenilir assessment sığmıyorsa assessment gelmeyebilir.

Sistem günü otomatik uzatmaz.

---

# 11. Düşük kapasite günü

3A micro-session kuralları korunur.

Kullanıcının çok az zamanı varsa:
- assessment zorunlu kota değildir,
- gerçek 3–8 dakikalık atomic free-recall/recognition/short-check uygunsa seçilebilir,
- critical production Objective'i 3 dakikalık MCQ ile sahte biçimde ölçülmüş sayılmaz,
- güvenilir evidence için gereken task sığmıyorsa defer edilir,
- hiçbir assessment gelmemesi failure değildir.

Kural:

> **Zaman azlığı evidence standardını düşürmez; yalnız bugün hangi evidence'ın toplanabileceğini sınırlar.**

---

# 12. Evidence türü Objective'e uymalıdır

Item selection mevcut GRE-v0 Objective profile'ını okur.

Örnekler:

### Concept Objective
Uygun olabilir:
- free recall,
- short open response,
- code reading,
- gerektiğinde recognition corroboration.

### Coding / production Objective
Strong direct evidence için:
- gerçek user-authored code production,
- objective-specific compiler/test doğrulaması.

MCQ veya code-selection coding mastery yerine geçmez.

### Debugging Objective
Strong direct evidence için:
- fresh bug/context,
- cause isolation,
- fix,
- gerekiyorsa explanation.

### Transfer Objective
- unseen problem structure,
- yalnız bilinen prerequisites,
- farklı family/context.

DMA-v0 `kolay ve kısa olduğu için` yanlış evidence modality seçmez.

---

# 13. Difficulty davranışı

DMA-v0 difficulty'yi puan multiplier'ı yapmaz.

Item selection:
- early checkpoint için basic item seçebilir,
- GRE-v0 `requires_non_basic_evidence` gate'i varsa authentic/application item seçmelidir,
- transfer gerekiyorsa transfer/integration item gerekir,
- kullanıcı basic'te zorlanıyorsa bilinmeyen prerequisite ekleyerek difficulty artırmaz.

Critical Objective yalnız easy/recognition micro-check'lerle mastered olamaz.

---

# 14. Assessment task contract

3B `TaskCandidate` üzerine 4A için aşağıdaki assessment metadata'sı eklenebilir:

```text
AssessmentTaskMetadata
- assessment_scope: daily_micro
- assessment_intent:
    checkpoint
    mastery_evidence
    verification
    integration_check
- target_objective_ids[]
- target_skill_ids[]
- required_evidence_types[]
- required_skill_ids[]
- item_or_template_ref
- item_version
- variant_family_id?
- dependency_group_id?
- difficulty_class
- independence_mode
- allowed_tools_policy
- evaluator_requirement
- atomic_evidence_boundary
- expected_active_minutes
```

Bu ayrı bir ikinci TaskCandidate modeli değildir; canonical TaskCandidate'ın assessment extension'ıdır.

---

# 15. Assessment item minimum validity contract

4D'de full Question Bank schema kesinleşene kadar daily assessment item en az şunları tanımlayabilmelidir:

```text
AssessmentItemMinimumContract
- item_id
- version
- target_objective_ids[]
- target_skill_ids[]
- required_skill_ids[]
- forbidden_not_yet_concepts[]?
- evidence_type
- objective_attributions[]
- expected_answer_or_rubric_ref
- evaluator_requirement
- allowed_tools_policy
- difficulty_class
- variant_family_id
- dependency_group_id?
- validation_status
- content_origin
```

High-stakes mastery/verification use için item `trusted | validated` olmalı veya objective-specific deterministic verification sağlayabilmelidir.

Unvalidated AI-generated item strong mastery-changing evidence üretmez.

---

# 16. Assessment öncesi prerequisite gate zorunludur

Assessment candidate scoring'den **önce** PRG-v0 uygulanır.

Hard prerequisite `not_ready` ise candidate çalıştırılmamalıdır.

Metadata hatası nedeniyle çalıştırıldıysa:

```text
assessment outcome = invalid_prerequisite_contamination
```

ve:
- target Skill'e negative evidence yazılmaz,
- missing prerequisite için need oluşabilir,
- item content QA için flag'lenir.

Bu özellikle mixed C/Linux/English item'larda zorunludur.

---

# 17. English assessment özel güvenliği

English Objective ölçülüyorsa yalnız daha önce öğretilmiş grammar/vocabulary prerequisites kullanılabilir.

Teknik Objective ölçülüyorsa English gizli prerequisite olamaz.

Örnek:
- target = C pointer reasoning,
- kullanıcı ilgili English grammar'ı bilmiyor,
- item yalnız İngilizce karmaşık yönerge ile çözülebiliyorsa

item target C evidence'ı için adil değildir.

Gerekirse Türkçe/bilingual yönerge kullanılır.

---

# 18. Assessment sırasında yardım davranışı

Mastery/verification amaçlı daily assessment varsayılan olarak H0 independent measurement ister.

## Kullanıcı assessment sırasında hint isterse

Sistem hint'i yasaklamaz.

Ama assistance provenance kaydedilir:
- H1/H2 → assisted evidence; positive independent mastery score'una girmez,
- H3/H4 → practice_only / solution-exposed; fresh independent recheck gerekir.

Task isterse aynı ekran içinde öğrenme moduna devam edebilir.

Canonical sonuç:

```text
assessment task selected
→ hint requested
→ learning can continue
→ independent evidence claim downgraded
```

Kullanıcı yardım istedi diye negative H0 evidence yazılmaz.

---

# 19. Submit sonrası feedback evidence'ı geriye dönük kirletmez

Kullanıcı H0 cevabını submit ettikten sonra:
- doğru/yanlış feedback,
- açıklama,
- worked solution,
- AI Tutor explanation

verilebilir.

Submit öncesi attempt evidence niteliğini korur.

Ancak solution gösterildikten sonra aynı item'ın tekrar çözülmesi yeni independent evidence değildir.

Fresh recheck gerektiğinde unseen/different candidate seçilir.

---

# 20. Assessment outcome ile mastery aynı şey değildir

Raw item outcome:

```text
correct
incorrect
partial
incomplete
```

Mastery decision değildir.

Assessment processing daha zengin sonuç üretir:

```text
AssessmentAttemptResult
- attempt_id
- planned_task_ref
- assessment_intent
- item_ref
- item_version
- target_objective_ids[]
- target_skill_ids[]
- raw_outcome
- assistance_level
- artifact_origin
- prerequisite_validity
- evaluator_status
- evidence_disposition
- evidence_event_refs[]
- feedback_exposure_level
- state_change_refs[]
- replan_trigger?
```

`evidence_disposition` baseline:

```text
eligible_independent
assisted_learning_only
practice_only_solution_exposed
provisional_needs_confirmation
invalid_item
invalid_prerequisite_contamination
invalid_evaluator
incomplete_unscored
```

---

# 21. Valid positive assessment sonucu

H0 + direct + prerequisite-valid + verified + independent-group eligible positive result:

1. Objective EvidenceEvent üretir.
2. GRE-v0 recent bounded window/gates yeniden hesaplanır.
3. Skill mastery derived edilir.
4. prerequisite readiness değişebilir.
5. Topic state değişebilir.
6. open LearningNeed kapanabilir/değişebilir.
7. planner remaining planı gerekirse replan eder.

Tek doğru micro item:

```text
!= automatic Skill mastered
```

GRE-v0 required group/family/direct-type/critical gates aynen korunur.

---

# 22. Valid negative assessment sonucu

## Skill henüz mastered değilse

Eligible H0 direct negative/partial evidence GRE-v0'a normal şekilde girer.

Sistem:
- evidence'ı Objective'e bağlar,
- weakness/misconception sinyali oluşturabilir,
- gerekirse daha uygun teaching/practice/remediation candidate'ı üretebilir,
- fakat tek bir yanlış yüzünden bütün Topic'i sıfırlamaz.

`remediation_required` seviyesi mevcut evidence/state kurallarına göre oluşur; her yanlış otomatik heavy remediation değildir.

## Skill daha önce mastered ise

İlk clean meaningful H0 contradiction:

```text
verification_due
```

üretir; mastery anında silinmez.

Fresh/unseen verification planlanır. RVR/GRE hysteresis korunur.

---

# 23. Partial result

Partial result:
- Objective rubric'e göre partial evidence olabilir,
- hangi component'in doğru/yanlış olduğu ayrı observable ise ayrı attribution yapılabilir,
- global task `partial` diye bütün target Skill'lere aynı sonucu yazmaz.

Evaluator güvenilir değilse `provisional_needs_confirmation` kullanılır.

---

# 24. Invalid / ambiguous item güvenliği

Aşağıdakiler mastery-changing positive veya negative evidence üretemez:
- wrong answer key,
- ambiguous prompt,
- evaluator failure,
- target attribution yapılamaması,
- undeclared hard prerequisite,
- corrupted task/content version,
- high-stakes kullanım için yeterli validation olmayan item.

Sonuç:

```text
invalid item -> no mastery penalty, no mastery credit
```

Sistem:
1. attempt history'yi koruyabilir,
2. evidence'ı `invalid/unusable` yapar,
3. item/template'i QA flag'ler,
4. capacity uygunsa fresh valid alternative seçebilir,
5. kullanıcıyı `yanlış yaptın` diye cezalandırmaz.

---

# 25. Kullanıcı item'ın hatalı/ambiguous olduğunu bildirirse

User report tek başına otomatik `item_invalid=true` demek değildir.

Ancak high-stakes result contested hale getirilebilir:
- ilgili evidence provisional/held yapılır,
- deterministic validator/answer key/rubric yeniden kontrol edilir,
- doğrulanana kadar critical mastery transition yalnız contested item'a dayandırılmaz.

Exact content-review workflow 4D/4E'de kesinleşir.

---

# 26. Evaluator status

GRE-v0 statüleri korunur:

```text
verified
provisional
invalid
```

## Verified
Deterministic/prevalidated answer key, objective-specific tests veya izin verilen güvenilir evaluator policy.

## Provisional
Örneğin tek başına uncalibrated LLM open-response evaluation.

Provisional:
- learning feedback verebilir,
- confirmation candidate tetikleyebilir,
- critical mastery gate'ini tek başına geçemez,
- tek başına ağır negative remediation kararı vermemelidir.

## Invalid
Evidence score'a girmez.

---

# 27. Coding daily assessment

Coding Objective için daily micro assessment kısa olabilir ama gerçek davranışı korumalıdır.

Örnek:
- 6–12 dakikalık küçük function/body yazma,
- pointer ile value mutate etme,
- küçük bug fix,
- terminal/system command task.

Kritik production Objective için:
- user-authored artifact,
- H0,
- objective-specific verification

gerekir.

Telefon üzerinde uygun değilse PC task'i olarak planlanabilir veya capacity/ortam uygun başka zamana defer edilir.

Sırf mobilde kolay diye production gate MCQ'ya düşürülmez.

---

# 28. Integrated daily assessment

Tek micro project/task birkaç Objective'i ölçebilir.

Her Objective için:

```text
structurally_essential
AND separately_observable
AND prerequisite_valid
AND provenance_valid
AND evaluator_sufficient
```

gerekir.

Global task PASS sibling Skills'i otomatik mastered/retained yapmaz.

---

# 29. Aynı item / yakın varyant guard

Independent evidence için:
- exact solution-exposed repeat kullanılmaz,
- aynı dependency group tek evidence group gibi davranır,
- near variant'lar family diversity'yi sahte biçimde artırmaz,
- fresh verification mümkünse farklı family/context kullanır.

DMA-v0 kullanıcıyı aynı cevabı ezberleyerek mastery kazanmaya teşvik etmez.

---

# 30. Feedback akışı

Assessment sonrası feedback'in amacı yalnız skor göstermek değildir.

Baseline:

```text
submit
→ validate/evaluate
→ kısa doğru/yanlış/partial feedback
→ gerekiyorsa misconception/root-cause hint
→ gerekirse teaching/remediation
→ fresh recheck daha sonra / farklı item
→ state update + replan
```

Yanlış sonrası full solution gösterilebilir; öğrenme amacıyla yasak değildir. Ama H4 exposure olarak işaretlenir ve aynı item independent recheck olmaz.

---

# 31. Planner ile entegrasyon

Assessment sonucu state değiştirdiğinde canonical replan chain:

```text
Assessment Attempt
→ Evidence validation
→ GRE-v0 / RVR-v0 update
→ weakness/remediation/verification update
→ PRG readiness
→ Topic derived state
→ open LearningNeeds
→ PBR-v0
→ remaining 3A capacity
→ new plan version
→ PDT-v0 trace
```

Yeni remediation oluşması günü otomatik uzatmaz.

Completed task/evidence replan sırasında korunur.

---

# 32. Assessment kararını hangi engine verir?

- LLM assessment quota belirlemez.
- LLM mastery yazmaz.
- LLM invalid prerequisite'i bypass etmez.
- LLM planner priority'yi keyfi değiştirmez.

Deterministik/local core:
- target eligibility,
- item metadata validation sonucu tüketimi,
- assistance/provenance classification input'ları,
- evidence disposition,
- GRE/RVR/PRG state transition,
- planner capacity/priority.

LLM ileride:
- open response feedback,
- misconception hypothesis,
- explanation,
- candidate item generation

yapabilir; güven düzeyi ve validator sınırları 4E/14F'te kesinleşir.

---

# 33. Assessment-specific reason codes

PDT-v0'a eklenebilecek V1 semantic reason codes:

```text
assessment.checkpoint_ready
assessment.mastery_evidence_needed
assessment.required_evidence_type_missing
assessment.verification_required
assessment.integration_check_useful
assessment.already_satisfied_by_fresh_evidence
assessment.no_valid_item
assessment.not_selected_capacity
assessment.prerequisite_blocked
assessment.item_invalid
assessment.prerequisite_contaminated
assessment.assistance_downgraded_evidence
assessment.solution_exposed
assessment.provisional_evaluation
assessment.incomplete_unscored
assessment.positive_evidence_recorded
assessment.negative_evidence_recorded
assessment.partial_evidence_recorded
assessment.replan_after_state_change
assessment.no_daily_quota
```

User-facing text yine internal trace/evidence facts dışına çıkamaz.

---

# 34. Kullanıcıya gösterilebilecek açıklama örnekleri

### Assessment neden geldi?
> `Bu kısa kontrol, Pointer Dereference için bağımsız uygulama kanıtı eksik olduğu için bugün plana eklendi.`

### Neden assessment yok?
> `Bugünkü plan için ek bir ölçüm gerekmiyor; mevcut çalışma ve kanıtlar üzerinden devam ediyoruz.`

veya capacity nedeniyle:
> `Bu kontrol bugün süreye sığmadı. Başarısızlık veya borç sayılmayacak; ihtiyaç devam ederse sonraki planda yeniden değerlendirilecek.`

### Hint kullanıldı
> `İpucu kullandığın için bu denemeyi öğrenme olarak kaydettik; bağımsız mastery kanıtı için farklı bir görevle yeniden kontrol edeceğiz.`

### Item invalid
> `Bu soru güvenilir biçimde değerlendirilemedi; sonucunu ilerlemene karşı kullanmadık.`

---

# 35. Daily micro assessment result contract

4B–4E ve ileride implementation'ın tüketeceği ortak minimum çıktı:

```text
DailyAssessmentResult
- assessment_session_id?
- planned_task_refs[]
- attempt_result_refs[]
- evidence_event_refs[]
- verified_positive_objective_ids[]
- verified_negative_objective_ids[]
- partial_objective_ids[]
- invalid_or_unusable_attempt_ids[]
- provisional_attempt_ids[]
- assistance_recheck_objective_ids[]
- newly_opened_verification_ids[]
- newly_opened_weakness_or_remediation_refs[]
- resolved_need_refs[]
- resulting_mastery_state_refs[]
- resulting_retention_state_refs[]
- resulting_prerequisite_state_refs[]
- replan_event_ref?
- assessment_policy_version
```

Bu result doğrudan `score = 8/10 → mastered` kuralı taşımaz.

---

# 36. 4B / 4C / 4D / 4E handoff

## 4B — Haftalık sınav
DMA-v0'daki evidence validity, assistance, prerequisite ve item trust kurallarını kullanacak; multi-Skill composition ve weekly coverage ayrıca tasarlanacak.

## 4C — Aylık yeterlilik sınavı
Aynı evidence contract üzerinde daha geniş transfer/integration ve critical prerequisite revalidation tasarlanacak.

## 4D — Soru bankası
`AssessmentItemMinimumContract` production schema'ya dönüşecek:
- trusted item lifecycle,
- variant/dependency family,
- rubric/answer key,
- difficulty,
- versioning,
- usage history / exposure.

## 4E — AI-generated soru doğrulaması
AI-generated candidate'ın:
- correctness,
- ambiguity,
- target fit,
- prerequisite validity,
- duplicate/near-duplicate,
- rubric/evaluator suitability

kontrolleri kesinleşecek.

---

# 37. Performance / bounded behavior

D-028 korunur.

Daily planner path:
- full assessment history'yi taramamalı,
- compact Objective mastery/evidence-gap state kullanmalı,
- recent evidence refs/dependency/family summary'sini tüketmeli,
- bounded candidate alternatives üretmeli,
- item selection/evaluation ağırsa UI thread dışında çalışmalı.

Exact DB index/cache/query yapısı 9C/9F/12A–12C'de kesinleşir.

---

# 38. DMA-v0 invariants

1. Daily assessment zorunlu calendar quota değildir.
2. Assessment ayrı günlük backlog/debt üretmez.
3. Practice/assess/retain/diagnose purpose'ları karıştırılmaz.
4. Assessment target yalnız coverage/prerequisite açısından adil olduğunda ölçülür.
5. Item prerequisite gate evaluation'dan önce çalışır.
6. Invalid/ambiguous item positive veya negative mastery evidence üretmez.
7. H1–H4 assisted performance positive independent mastery score'una girmez.
8. H3/H4 sonrası fresh/unseen independent recheck gerekir.
9. Submit sonrası feedback önceki H0 attempt'i geriye dönük kirletmez.
10. Tek doğru item automatic mastery değildir.
11. Tek clean post-mastery failure instant unmastery değildir; verification_due davranışı korunur.
12. Objective evidence modality gate'i kısa süre uğruna düşürülmez.
13. Critical coding mastery MCQ ile geçilemez.
14. Multi-Skill task global pass'i component evidence'a otomatik yayılmaz.
15. Same/near item bağımsız evidence sayısını şişiremez.
16. Low-capacity day evidence standardını düşürmez.
17. New remediation günü otomatik uzatmaz.
18. Provisional evaluator critical mastery/remediation kararını tek başına belirleyemez.
19. Technical assessment'ta bilinmeyen English grammar gizli prerequisite olamaz.
20. Assessment sonucu yalnız canonical evidence/state pipeline üzerinden planner'ı değiştirir.

---

# 39. 4A acceptance criteria

4A PASS için:

1. Daily micro assessment'ın “zorunlu günlük quiz” olmadığı açık.
2. Practice / assess / retain / diagnose ayrımı korunuyor.
3. Assessment intent'leri ve target selection logic tanımlı.
4. Fixed question/time quota yok; composition capacity-aware.
5. GRE-v0 Objective-specific evidence gates korunuyor.
6. H0/H1–H4 assistance davranışı bağlanmış.
7. PRG prerequisite + contamination guard assessment'ta zorunlu.
8. Invalid/ambiguous/provisional evaluator güvenliği tanımlı.
9. Coding/debugging/transfer evidence biçimleri target behavior ile eşleşiyor.
10. Positive/negative/partial/incomplete result doğrudan mastery state değildir.
11. Post-mastery first failure hysteresis korunuyor.
12. Low-capacity day behavior tanımlı.
13. Assessment → evidence → state → replan akışı tanımlı.
14. Assessment-specific reason codes var.
15. 4B–4E için minimum common result/item contract var.
16. D-028 bounded/performance yaklaşımı korunuyor.

---

# 40. Final 4A kararı

**Final model:** `DMA-v0 — Daily Micro Assessment`

Özet:

```text
Current learning/evidence state
    ↓
Gerçek measurement need var mı?
    ↓
Objective-matched assessment candidate
    ↓
validation/trust
    ↓
PRG prerequisite fairness
    ↓
PBR priority
    ↓
3A capacity fit
    ↓
H0 independent attempt (mastery iddiası için)
    ↓
validity + assistance + provenance + evaluator
    ↓
EvidenceEvent
    ↓
GRE / RVR / weakness / verification
    ↓
PRG + Topic state
    ↓
remaining-plan replan
```

DMA-v0 kullanıcıyı her gün sınava sokan bir quiz motoru değil; **yalnız gerekli olduğunda güvenilir evidence toplayan, yanlış/yardımlı/invalid sonuçları doğru sınıflandıran ve günlük planı güncel state'e göre değiştiren ölçüm katmanıdır.**
