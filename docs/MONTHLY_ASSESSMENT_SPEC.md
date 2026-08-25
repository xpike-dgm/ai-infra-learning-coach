# Monthly Assessment Specification — MCA-v0

**Adım:** 4C — Aylık yeterlilik sınavı  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Final model:** `MCA-v0 — Monthly Capability Assessment`

Bu belge aylık değerlendirmenin günlük DMA-v0 ve haftalık WBA-v0'dan daha geniş bir zaman ufkunda, özellikle transfer, entegrasyon, kritik capability yeniden doğrulaması ve uzunlamasına evidence görünümü için nasıl çalışacağını tanımlar.

Bağlayıcı kaynaklar:
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` — DMA-v0
- `docs/WEEKLY_ASSESSMENT_SPEC.md` — WBA-v0
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0
- `docs/ADAPTIVE_PLANNER_SPEC.md`
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/PRIORITY_POLICY_SPEC.md` — PBR-v0
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0
- `docs/PLANNER_EXPLAINABILITY_SPEC.md` — PDT-v0
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/V1_SUCCESS_CRITERIA.md`
- `docs/PROFESSIONAL_READINESS_TARGET.md`
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — D-044

Ana ilke:

> **Aylık assessment bir “ay sonu notu” değildir. Daha geniş bir öğrenme penceresinde farklı zamanlarda ve bağlamlarda gelişen önemli Skill/Objective'lerin bağımsız transfer, entegrasyon, kritik prerequisite güveni ve delayed retention açısından yeniden örneklenmesini sağlayan blueprint-temelli bir capability assessment'tır.**

İkinci ilke:

> **Monthly sonucu ayrı bir mastery sistemi kurmaz. Her item/attempt normal validity, prerequisite, assistance, provenance, evaluator ve attribution kontrollerinden geçer; yalnız oluşan geçerli Objective-level EvidenceEvent'ler GRE-v0/RVR-v0 ve planner state'lerini etkiler.**

Üçüncü ilke:

> **Aylık değerlendirme professional-readiness'e giden daha geniş evidence üretebilir fakat tek başına professional-readiness, capstone completion, domain pass/fail veya kariyer yüzdesi yazamaz.**

---

# 1. MCA-v0 neyi çözer?

Daily assessment lokal ve karar-anı odaklıdır. Weekly assessment birden fazla Skill/Objective için kısa dönem blueprint oluşturur. Monthly assessment ise daha uzunlamasına şu soruları hedefler:

- Son dönemde öğrenilen required capability'ler farklı bir bağlamda gerçekten transfer edilebiliyor mu?
- Birkaç hafta boyunca ayrı ayrı kullanılan Skill'ler daha büyük bir problem içinde birlikte çalıştırılabiliyor mu?
- Gelecek curriculum dalları için kritik prerequisite'lerin güveni hâlâ yeterli mi?
- Haftalık ölçümlerde tekrarlayan veya belirsiz kalan weakness'lar daha güvenilir biçimde lokalize edilebiliyor mu?
- Daha eski mastered Skill'lerden hangileri delayed retrieval / natural integration içinde yeniden doğrulanmalı?
- Kullanıcının gelişimi yalnız yakın-context başarısından mı geliyor, yoksa tutorial-dışı problem yapısına transfer var mı?
- Technical English paralel hattında daha geniş bir teknik kullanım capability'si gerçekten ölçülmeye hazır mı?
- Aylık evidence hangi LearningNeed, remediation, verification veya next-path kararlarını değiştirmeli?

MCA-v0 bütün geçmişi tekrar test etmez. Amaç **yüksek bilgi değerli capability sampling** yapmaktır.

---

# 2. Daily / Weekly / Monthly ayrımı

## DMA-v0 — Daily Micro Assessment
- current/local LearningNeed,
- kısa checkpoint veya missing evidence,
- tek Objective veya küçük integration,
- aynı gün teaching/practice akışına yakın.

## WBA-v0 — Weekly Blueprint Assessment
- haftalık multi-Skill blueprint,
- recent progress + weakness + critical prerequisite + retention + integration,
- günlük akıştan daha bağımsız ve daha çeşitli evidence.

## MCA-v0 — Monthly Capability Assessment
- daha uzun longitudinal window,
- recent + older capability sampling,
- daha güçlü cross-topic/cross-module transfer,
- daha büyük fakat attributable integration task'ları,
- persistent weakness/uncertainty'nin yeniden kontrolü,
- kritik prerequisite/capability revalidation,
- professional evidence katmanlarına doğru daha anlamlı uygulama/entegrasyon kanıtı.

Kritik invariants:

```text
monthly_scope != stronger_numeric_weight
monthly_exam != final_professional_gate
monthly_result != domain_pass_fail
```

Bir evidence sırf monthly session'dan geldi diye GRE-v0'da ekstra katsayı almaz. Gücü task/evidence kalitesinden gelir.

---

# 3. “Monthly” katı 30 günlük sınav borcu değildir

V1 product requirement olarak aylık assessment cadence bulunur. Ancak:

```text
30 gün geçti -> zorunlu eski sınav borcu
```

canonical değildir.

Davranış:
- `monthly_assessment_cycle` uygun dönemde assessment need açabilir,
- planner uygun gün/kapasiteye yerleştirir,
- kullanıcı ilgili cycle'da assessment'ı tamamlamazsa failure yazılmaz,
- eski monthly exam'lar üst üste birikmez,
- sonraki uygun cycle'da current state'ten fresh blueprint oluşturulur,
- güvenli biçimde devam edilebilen partial session resume edilebilir,
- stale/unexposed slot'lar gerektiğinde fresh item/task ile yeniden compose edilir.

Aylık cadence scheduling semantics'tir; mastery semantiği değildir.

---

# 4. Common AssessmentBlueprint contract korunur

4B'de kilitlenen generic abstraction MCA-v0 tarafından yeniden kullanılır:

```text
AssessmentBlueprint
AssessmentBlueprintSlot
AssessmentSessionResult
```

Monthly specialization ek metadata taşıyabilir:

```text
MonthlyAssessmentBlueprintExtension
- assessment_scope: monthly
- cycle_id
- longitudinal_window_ref
- prior_monthly_result_ref?
- sampling_basis_refs[]
- integration_candidate_refs[]
- critical_revalidation_refs[]
- delayed_retention_candidate_refs[]
- monthly_policy_version
```

Bu ikinci bir assessment mimarisi değildir; WBA-v0 ortak contract'ının monthly policy extension'ıdır.

---

# 5. Monthly blueprint role family'leri

MCA-v0 aşağıdaki slot role family'lerini destekler:

```text
longitudinal_required_capability
persistent_weakness_or_verification
critical_capability_revalidation
delayed_retention_sampling
cross_topic_transfer
integrated_application
parallel_technical_english
professional_evidence_checkpoint
```

Bunlar fixed quota değildir.

## `longitudinal_required_capability`
Son dönemde farklı gün/haftalarda ilerleyen required Skill/Objective'lerde yakın-context dışı bağımsız kanıt ihtiyacını temsil eder.

## `persistent_weakness_or_verification`
Birden fazla güvenilir state/evidence olayı boyunca çözülemeyen weakness, `verification_due`, remediation sonrası fresh recheck veya weekly'de provisional kalan önemli concern için kullanılır.

Tek bir zayıf item “persistent” değildir.

## `critical_capability_revalidation`
Yakın gelecek curriculum'un önemli bir kısmının dayandığı critical prerequisite/capability için gerçekten yeniden güven kontrolü gerekiyorsa kullanılır.

Her critical Skill her ay otomatik test edilmez.

## `delayed_retention_sampling`
RVR-v0 açısından due/overdue veya anlamlı delayed retrieval fırsatı bulunan mastered Skill'i daha eski bağlamdan örnekler.

## `cross_topic_transfer`
Aynı domain veya yakın domain'lerde farklı Topic'lerden daha önce öğrenilmiş Skill'lerin yeni problem yapısına transferini ölçer.

## `integrated_application`
Birden fazla Skill'in gerçekçi bir uygulama/system task içinde birlikte kullanılmasını ölçer. Her target component ayrı observable ve attributable olmak zorundadır.

## `parallel_technical_english`
Technical English hattında geniş teknik kullanım assessment'ı gerçekten due/eligible ise eklenebilir. Sabit quota değildir.

## `professional_evidence_checkpoint`
Aşama 6/15/20 capability metadata'sı izin verdiğinde applied/integrated engineering davranışını daha gerçekçi task ile örnekleyebilir. Bu slot **professional-readiness gate'i değildir**; yalnız uygun evidence üretir.

---

# 6. Target pool — yalnız “bu ay işlenenler” değildir

Monthly target pool şunlardan türetilebilir:

1. longitudinal window içinde meaningful progress gösteren required Objectives,
2. unresolved verification/remediation veya birden fazla valid concern,
3. future graph açısından yüksek etkili critical prerequisites,
4. RVR review/delayed retrieval ihtiyaçları,
5. cross-topic/cross-module transfer opportunities,
6. integrated task için birlikte kullanılmaya hazır Skill kümeleri,
7. due Technical English capability'leri,
8. professional evidence profile'ında eksik ama bu seviyede ölçülebilir applied/integrated evidence gaps.

Bir Skill yalnız “30 gün içinde görüldü” diye target olmaz.

Bir Skill:
- fresh ve yeterli bağımsız evidence taşıyorsa,
- repeated measurement düşük information value taşıyorsa,
- prerequisite-ready değilse,
- uygun validated item/task yoksa,
- daha sonra doğal project evidence ile daha iyi ölçülecekse

blueprint'ten çıkarılabilir.

---

# 7. Recent progress ile older capability dengesi

Monthly assessment yalnız en yeni konulara veya yalnız eski retention'a yığılmaz.

Selection semantics:

1. unresolved integrity/verification needs,
2. high-impact critical prerequisite confidence,
3. required capability'de decision-changing evidence gaps,
4. meaningful delayed retention,
5. transfer/integration information value,
6. parallel English / professional checkpoint ihtiyaçları,
7. capacity fit.

Bu bir weighted percentage değildir.

Yasak örnek:

```text
%50 yeni konu + %30 eski konu + %20 English
```

Bu oranlar empirical validation olmadan canonical yapılmaz.

---

# 8. Aylık kapsam “cumulative final” değildir

MCA-v0 bütün eski curriculum'u baştan sona yeniden sınamaz.

Neden:
- gereksiz test yükü,
- recognition/rote bias,
- current state kararlarına düşük bilgi değeri,
- gerçek coding/integration için zamanı azaltma,
- uzun 4+ yıllık rotada ölçeklenememe.

Bunun yerine state-temelli bounded sampling yapılır.

```text
monthly assessment = selective longitudinal sampling
not cumulative everything exam
```

---

# 9. Transfer standardı weekly'den nasıl genişler?

Monthly transfer slot'u mümkün olduğunda:
- exact lesson example'dan yüzeysel olarak farklı,
- farklı problem structure/context,
- yalnız daha önce öğretilmiş prerequisites,
- target Skill'in gerçekten gerekli olduğu,
- ezberlenmiş template ile doğrudan çözülemeyen,
- mümkünse birkaç haftalık aralıkta öğrenilmiş capability'leri birleştiren

bir task kullanır.

Sadece variable isimlerini veya sayıları değiştirmek daha güçlü monthly transfer değildir.

Transfer task bilinmeyen prerequisite ekleyerek zorlaştırılamaz.

---

# 10. Integrated application standardı

Monthly assessment weekly'den daha büyük integration block kullanabilir; ancak global project success component mastery değildir.

Her target Objective için:

```text
structurally_essential
AND separately_observable
AND prerequisite_valid
AND user_behavior_attributable
AND evaluator_sufficient
```

şartları gerekir.

Örnek gelecekte:
- Python CLI + file handling + error handling,
- C + memory + debugging,
- Linux process/tooling + shell,
- networking + concurrency,
- CUDA kernel + profiling,
- inference serving + latency/throughput measurement.

Bu örnekler yalnız ilgili Skills öğretildikten sonra eligible olur.

---

# 11. Critical capability revalidation

Critical Skill'in yalnız `critical=true` olması her ay slot üretmez.

Revalidation için güçlü nedenler:
- next path'in büyük kısmı bu capability'ye dayanıyor,
- unresolved `verification_due`,
- retention review meaningful biçimde due,
- recent clean contradiction,
- critical evidence profile'ında eksik required direct type,
- uzun süre doğal kullanım/verification görülmemiş ve current decision gerçekten buna bağlı,
- integrated task içinde prerequisite güveni olmadan downstream evidence yorumlanamayacak.

`review_due` tek başına “unutuldu” anlamına gelmez; RVR-v0 korunur.

---

# 12. Persistent weakness ne demektir?

MCA-v0 bir tek yanlış item'ı “kalıcı eksik” diye etiketlemez.

Persistent concern ancak örneğin:
- farklı bağımsız attempt'lerde tekrarlanan valid negative/partial evidence,
- remediation sonrası fresh recheck'in hâlâ zayıf olması,
- weekly + natural usage gibi farklı context'lerde aynı Objective gap'inin görünmesi,
- GRE/RVR state'inde unresolved verification/remediation bulunması

gibi state tarafından destekleniyorsa blueprint'te güçlü role alır.

MCA-v0 yeni bir `persistent_score` formülü icat etmez; mevcut evidence/state'i kullanır.

---

# 13. Evidence modality ve complexity

Monthly scope, yalnız MCQ/recognition ile doldurulamaz.

Objective'e göre:
- concept → recall/explanation/code reading,
- production → user-authored code/system artifact,
- debugging → fresh diagnosis/fix,
- transfer → unseen structure,
- performance → benchmark/profiler evidence,
- integration → component-attributable larger task

seçilebilir.

Universal quota yoktur:

```text
her ay 2 coding + 2 debugging + 20 MCQ
```

canonical değildir.

Difficulty/complexity mastery multiplier'ı değildir.

---

# 14. Professional-readiness ile ilişki

MCA-v0, professional-readiness evidence katmanlarına katkı sağlayabilir:

- Foundation evidence,
- Applied engineering evidence,
- uygun seviyede Integrated systems evidence,
- ileride GPU/inference applied evidence.

Fakat monthly session tek başına:
- professional-ready,
- job-ready,
- domain complete,
- capstone passed

kararı yazamaz.

Professional-readiness final gate'i `docs/PROFESSIONAL_READINESS_TARGET.md` içindeki required domain + retention + debugging + transfer + performance + integrated project/capstone evidence kurallarına bağlı kalır.

`professional_evidence_checkpoint` yalnız evidence üretir; final certification değildir.

---

# 15. Aylık süre ve günlük hard capacity

Monthly assessment daha geniş olabilir fakat **hiçbir günün hard capacity'sini otomatik aşmaz**.

Bir monthly session:
- birkaç atomic block'tan oluşabilir,
- gerektiğinde birden fazla güne yayılabilir,
- her gün yalnız remaining daily hard budget'a sığan block'lar planlanır,
- user açıkça ek süre vermezse günlük budget aşılmaz,
- uzun coding/integration block yalnız güvenli checkpoint varsa split edilir.

Universal:

```text
monthly exam = 2 saat
monthly exam = 100 soru
```

kuralı yoktur.

---

# 16. Split / pause / resume

Monthly session safe boundaries arasında bölünebilir:

```text
MonthlyAssessmentSession
→ Block A
→ checkpoint
→ Block B
→ checkpoint
→ Integration Block C
```

Kurallar:
- item/testlet atomic evidence boundary ortasında kesilmez,
- completed attempts/evidence incremental persist edilir,
- pause failure/assistance/mastery signal değildir,
- resume aynı item freshness ve prerequisite validity korunuyorsa mümkündür,
- exposure/version/prerequisite değişimi varsa unresolved slot fresh task ile recomposed edilir.

---

# 17. Incomplete / missed monthly assessment

Incomplete session:
- submitted valid attempts normal evidence üretir,
- unsubmitted item incorrect değildir,
- unresolved slot mastery penalty değildir,
- session `partial` olabilir,
- unresolved semantic needs current state'te kalır,
- later planner fresh slot/task oluşturabilir.

Missed monthly cycle:
- failure değildir,
- curriculum progress otomatik düşmez,
- “iki aylık sınav borcu” oluşmaz,
- current state'ten fresh monthly blueprint oluşturulur.

---

# 18. Assistance — H0 / H1–H4

Independent mastery/verification/transfer iddiası için varsayılan:

```text
independence_mode = h0_required
```

H1/H2:
- öğrenmeye devam edilebilir,
- positive independent mastery evidence değildir,
- fresh H0 recheck ihtiyacı kalabilir.

H3/H4:
- practice-only / solution-exposed,
- aynı item immediate independent recheck değildir,
- fresh/unseen task gerekir.

Allowed-tools policy Objective'e özgüdür. Gerçek profiler/debugger/terminal kullanımını ölçen task'ta izin verilen tool kullanımı H0'ı otomatik bozmaz.

---

# 19. Invalid / ambiguous / prerequisite-contaminated / provisional safety

Monthly kapsam daha yüksek-stakes görünse de validity kuralları aynıdır.

Aşağıdakiler mastery-changing positive/negative evidence üretemez:
- ambiguous prompt,
- wrong answer key/rubric,
- undeclared prerequisite,
- corrupted item/content version,
- evaluator failure,
- target attribution yapılamaması,
- high-impact kullanım için yeterli validation olmayan AI-generated item.

Provisional evaluator:
- feedback verebilir,
- confirmation need açabilir,
- critical transition veya heavy remediation'ı tek başına belirleyemez.

Invalid item slot'u kapatmış sayılmaz.

---

# 20. Root-cause contamination ve integrated task güvenliği

Bir root prerequisite gap'i birden fazla downstream component'i bozuyorsa downstream target'lara kör negative evidence yazılmaz.

Resolver:
- root Skill'e valid evidence yazabilir,
- contaminated targets `invalid_prerequisite_contamination` / unscored olabilir,
- bağımsız observable component'leri ayrı değerlendirebilir,
- next planner root remediation/verification'a öncelik verebilir.

Monthly integration task'in büyüklüğü attribution standardını düşürmez.

---

# 21. Technical English monthly behavior

Technical English paralel track olmaya devam eder.

Monthly blueprint:
- due/eligible olduğunda daha geniş technical reading/writing/comprehension/application slot'u ekleyebilir,
- sabit English quota kullanmaz.

English Objective ölçülüyorsa yalnız öğretilmiş grammar/vocabulary prerequisite'leri kullanılabilir.

Teknik Objective ölçülüyorsa bilinmeyen English grammar hidden prerequisite olamaz; gerektiğinde Turkish/bilingual instruction/scaffold kullanılır.

---

# 22. Raw monthly score canonical state değildir

MCA-v0:

```text
82/100 = mastered
60 altı = failed month
```

kuralı kullanmaz.

UI isterse session summary gösterebilir; canonical state Objective/Skill evidence üzerinden oluşur.

Daha yararlı semantic summary:

```text
confirmed_capabilities
verification_needed
persistent_targeted_gaps
retention_revalidated
not_reliably_measured
plan_changes
```

Bu summary yeni bir mastery database'i değildir; canonical state'in derived raporudur.

---

# 23. Positive monthly result

Valid H0 direct verified evidence:
1. Objective EvidenceEvent üretir,
2. GRE-v0 bounded recent evidence/gates normal biçimde yeniden hesaplanır,
3. RVR delayed evidence ise retention state güncellenebilir,
4. PRG readiness değişebilir,
5. weakness/remediation/verification need kapanabilir,
6. Topic/Module/Domain derived summary değişebilir,
7. planner sonraki çalışma dönemini replan eder.

Monthly label extra weight üretmez.

---

# 24. Negative monthly result ve hysteresis

## Henüz mastered olmayan Skill
Valid H0 direct negative/partial evidence normal GRE/weakness/remediation pipeline'ına girer.

## Daha önce mastered Skill
İlk clean monthly contradiction:

```text
verification_due
```

üretebilir; instant unmastery değildir.

Repeated fresh valid evidence GRE gates'i gerçekten düşürürse remediation oluşabilir.

Bir monthly session'daki tek kötü gün bütün ayı veya Domain'i “başarısız” yapmaz.

---

# 25. Aylık sonuç curriculum/planner'ı gerçekten değiştirmeli

V1 `SC-016` gereği monthly result yalnız rapor olamaz.

Geçerli persistent/critical gap oluştuğunda:
- ilgili Skill/Objective LearningNeed'i açılır/güncellenir,
- PBR priority değişebilir,
- PRG dependent branch bekleyebilir,
- targeted remediation/verification task'ları seçilebilir,
- lower-priority new learning ertelenebilir,
- bağımsız branches devam edebilir.

Güçlü ve stable alan sırf monthly exam'da bulundu diye gereksiz ağır tekrar almaz.

---

# 26. D-044 granular localization

Monthly assessment broad state yazmaz:

```text
Python failed
Linux passed
CUDA 74%
```

canonical değildir.

Doğru model:

```text
EvidenceEvent
→ Objective
→ Skill state
→ derived Topic/Module/Domain summary
```

Örneğin gelecekte:

```text
Python
  Functions: stable
  Exceptions: mastered
  Async task cancellation: verification_due
  Multiprocessing process lifecycle: learning
```

MCA-v0 remediation'ı mümkün olduğunca exact Skill/Objective'e yönlendirir.

---

# 27. Monthly result contract

Generic `AssessmentSessionResult` üzerine monthly summary extension:

```text
MonthlyAssessmentResult
- assessment_session_id
- cycle_id
- blueprint_ref
- longitudinal_window_ref
- session_status: complete | partial | deferred | invalidated
- completed_slot_refs[]
- unresolved_slot_refs[]
- attempt_result_refs[]
- evidence_event_refs[]
- verified_positive_objective_ids[]
- verified_negative_objective_ids[]
- partial_objective_ids[]
- invalid_or_unusable_attempt_ids[]
- provisional_attempt_ids[]
- assistance_recheck_objective_ids[]
- revalidated_critical_skill_ids[]
- revalidated_retention_skill_ids[]
- transfer_evidence_objective_ids[]
- integrated_evidence_objective_ids[]
- newly_opened_verification_ids[]
- newly_opened_weakness_or_remediation_refs[]
- resolved_need_refs[]
- resulting_mastery_state_refs[]
- resulting_retention_state_refs[]
- resulting_prerequisite_state_refs[]
- derived_capability_summary_ref?
- replan_event_ref?
- assessment_policy_version
```

`overall_mastery_score` zorunlu değildir ve canonical mastery source değildir.

---

# 28. 4D Question Bank handoff

4D item/task bank schema, DMA/WBA/MCA blueprint'lerini desteklemek için en az şu kavramları taşıyabilmelidir:

```text
Item / Task metadata
- stable item_id + version
- assessment_scope_eligibility: daily | weekly | monthly | multiple
- blueprint_role_eligibility[]
- target_skill_ids[]
- target_objective_ids[]
- required_skill_ids[]
- forbidden_not_yet_concepts[]?
- evidence_type
- activity_kind
- direct/corroborating capability
- difficulty / complexity class
- variant_family_id
- dependency_group_id / testlet_id?
- context_family / transfer_structure tags
- integrated_component_attributions[]
- expected_answer / rubric / test reference
- evaluator_requirement
- allowed_tools_policy
- artifact_requirement?
- validation_status / trust level
- content_origin
- exposure / solution-exposure compatibility
- estimated_active_minutes
- splittable / atomic boundary
- language / scaffold metadata
- freshness / content version metadata
```

4D exact production schema'yı, lifecycle'ı, validation state'lerini ve bank selection contract'ını kesinleştirecektir.

---

# 29. Monthly-specific reason codes

PDT-v0 `assessment.*` namespace extension:

```text
assessment.monthly.due
assessment.monthly.blueprint_generated
assessment.monthly.slot_longitudinal_required
assessment.monthly.slot_persistent_weakness
assessment.monthly.slot_critical_revalidation
assessment.monthly.slot_delayed_retention
assessment.monthly.slot_cross_topic_transfer
assessment.monthly.slot_integrated_application
assessment.monthly.slot_parallel_english
assessment.monthly.slot_professional_checkpoint
assessment.monthly.no_eligible_target
assessment.monthly.no_valid_item
assessment.monthly.capacity_split
assessment.monthly.partial_session
assessment.monthly.incomplete_not_failure
assessment.monthly.no_exam_debt
assessment.monthly.prerequisite_contaminated
assessment.monthly.assistance_recheck_required
assessment.monthly.invalid_item_replaced
assessment.monthly.capability_evidence_recorded
assessment.monthly.replan_after_result
```

User-facing explanation trace'te olmayan nedeni icat edemez.

---

# 30. User-facing monthly summary

Önerilen semantic sections:

```text
Bu dönemde doğrulanan beceriler
Transfer edebildiğin beceriler
Yeniden doğrulanacak kritik noktalar
Hedefli geliştirme gereken alt beceriler
Retention açısından yeniden doğrulananlar
Bu oturumda güvenilir ölçülemeyenler
Programında değişenler
```

Tek overall “aylık başarı yüzdesi” ana mesaj değildir.

---

# 31. Performance / bounded behavior

D-028 korunur.

Monthly blueprint generation:
- bütün yıllık history'yi UI thread'de full-scan etmez,
- compact Skill/Objective mastery, evidence-gap, RVR, PRG, weakness ve recent-window summaries kullanır,
- prior monthly summary/ref üzerinden incremental longitudinal context tüketebilir,
- bounded target pool ve bounded candidate alternatives üretir,
- heavy item selection/evaluation async çalışır,
- partial session/evidence incremental persist edilir.

Exact DB/index/cache/query AŞAMA 9 ve implementation aşamalarında kesinleşir.

---

# 32. Research / calibration sınırı

4C'de ayrı Research AI kullanılmadı.

MCA-v0:
- scientifically optimal soru sayısı,
- ideal monthly exam süresi,
- fixed transfer oranı,
- psychometric pass score,
- universal critical revalidation cadence

iddiası üretmez.

Bunların empirical UX/false-positive/false-negative/calibration tarafı AŞAMA 18 pilotuna bırakılır. 4C'nin görevi mevcut evidence/prerequisite/mastery kurallarını daha geniş monthly orchestration'a bağlamaktır.

---

# 33. MCA-v0 invariants

1. Monthly assessment tek overall mastery score/pass-fail değildir.
2. Monthly evidence GRE/RVR'ı bypass etmez veya ekstra weight almaz.
3. Fixed soru sayısı, fixed süre, fixed role yüzdesi yoktur.
4. Monthly assessment bütün geçmiş curriculum'u cumulative olarak tekrar test etmez.
5. Blueprint item'lardan önce oluşturulur.
6. Target'lar current canonical Skill/Objective state'inden türetilir.
7. Recent + older sampling state-temellidir; fixed yüzde değildir.
8. Critical Skill sırf critical olduğu için her ay otomatik test edilmez.
9. Transfer bilinmeyen prerequisite ekleyerek zorlaştırılmaz.
10. Integrated task global pass'i component evidence'a yayamaz.
11. Persistent weakness tek item'dan türetilmez.
12. Validation/trust ve PRG eligibility selection'dan önce gelir.
13. H1–H4 positive independent mastery değildir.
14. H3/H4 sonrası fresh/unseen recheck gerekir.
15. Invalid/ambiguous/prerequisite-contaminated item credit/penalty üretmez.
16. Provisional evaluator critical transition'ı tek başına belirleyemez.
17. Daily hard capacity otomatik aşılmaz; monthly session güvenli biçimde bölünebilir.
18. Incomplete/missed monthly assessment failure/debt değildir.
19. First clean post-mastery contradiction instant unmastery değildir.
20. Broad Domain pass/fail raw monthly result'tan yazılmaz.
21. D-044 granular weakness localization korunur.
22. Technical English hidden prerequisite olamaz.
23. Monthly professional checkpoint final professional-readiness gate değildir.
24. Monthly result V1 SC-016 gereği canonical state/planner'ı gerektiğinde gerçekten değiştirebilir.
25. Result yalnız evidence → GRE/RVR → PRG/weakness/remediation → planner pipeline'ı üzerinden etkiler.

---

# 34. 4C acceptance criteria

4C PASS için:

1. DMA/WBA/MCA scope farkları açık.
2. WBA generic blueprint/result contract monthly scope için yeniden kullanılıyor.
3. Longitudinal required, persistent weakness, critical revalidation, delayed retention, transfer, integration, English ve professional checkpoint role'ları tanımlı ama quota yapılmamış.
4. Cumulative-everything exam anti-pattern'i engellenmiş.
5. Fixed scientifically optimal soru/süre/puan uydurulmamış.
6. Transfer ve integration weekly'den daha geniş ama prerequisite-safe tanımlanmış.
7. Critical capability revalidation trigger'ları tanımlı; automatic every-month retest yok.
8. Recent/older balance fixed yüzde olmadan state-temelli.
9. Professional-readiness ile monthly evidence arasındaki sınır net.
10. Capacity/split/pause/resume/incomplete/missed davranışı tanımlı.
11. H0/H1–H4 assistance davranışı korunuyor.
12. Invalid/ambiguous/provisional/prerequisite contamination safety korunuyor.
13. Negative result GRE/RVR hysteresis ile uyumlu.
14. D-044 granular localization korunuyor.
15. Monthly result V1 SC-016 uyarınca planner/curriculum priority'yi değiştirebiliyor.
16. 4D Question Bank için gerekli item/blueprint metadata handoff'u tanımlı.
17. D-028 bounded/performance yaklaşımı korunuyor.

---

# 35. Final 4C kararı

**Final model:** `MCA-v0 — Monthly Capability Assessment`

Özet:

```text
monthly assessment cycle
    ↓
current Skill/Objectives + longitudinal summaries + evidence gaps + RVR + PRG + PBR
    ↓
monthly capability blueprint
    ↓
bounded longitudinal / critical / retention / transfer / integration slots
    ↓
validated + prerequisite-valid item/task selection
    ↓
capacity-aware multi-block session
    ↓
H0 attempt/artifact where independent evidence is required
    ↓
validity + assistance + provenance + evaluator
    ↓
Objective-level EvidenceEvents
    ↓
GRE / RVR / verification / weakness / remediation
    ↓
PRG + derived curriculum state
    ↓
next-period planner/replan
```

MCA-v0'nun amacı ay sonunda kullanıcıya tek not vermek değil; **haftalık ölçümün göremediği daha geniş transfer ve entegrasyonu örneklemek, kritik capability güvenini gerektiğinde yeniden doğrulamak ve ortaya çıkan gerçek granular evidence ile sonraki öğrenme planını değiştirmektir.**