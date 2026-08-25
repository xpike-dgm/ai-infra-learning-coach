# Weekly Assessment Specification — WBA-v0

**Adım:** 4B — Haftalık sınav  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Final model:** `WBA-v0 — Weekly Blueprint Assessment`

Bu belge haftalık değerlendirmenin, günlük mikro değerlendirmeden daha geniş fakat hâlâ adil, prerequisite-aware ve evidence-temelli biçimde nasıl çalışacağını tanımlar.

Bağlayıcı kaynaklar:
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` — DMA-v0
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
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — D-044

Ana ilke:

> **Haftalık sınav tek bir not üretmek için değil, bir haftalık öğrenme akışındaki birden fazla önemli Skill/Objective hakkında daha geniş ve çeşitli evidence toplamak için blueprint ile oluşturulan bir assessment oturumudur.**

İkinci ilke:

> **Weekly sonucu GRE-v0/RVR-v0'ı bypass eden ayrı bir mastery sistemi değildir. Her item/attempt önce aynı validity, prerequisite, assistance, provenance, evaluator ve attribution kurallarından geçer.**

Üçüncü ilke:

> **Haftalık sınavın kapsamı geniş olabilir; fakat bütün öğrenilen Skill'leri her hafta test etmek zorunda değildir. Fixed soru sayısı, fixed süre, fixed kategori yüzdesi veya tek bir “geçti/kaldı” puanı canonical değildir.**

---

# 1. WBA-v0 neyi çözer?

DMA-v0 günlük akış içinde lokal ve gerektiğinde evidence toplar. WBA-v0 ise daha geniş bir zaman penceresinde şu sorulara cevap vermeye çalışır:

- Bu hafta ilerleyen önemli Skill'ler gerçekten bağımsız biçimde gösterilebiliyor mu?
- Günlük görevlerde saklanabilecek weakness veya misconception var mı?
- Bir sonraki öğrenme dalını etkileyen critical prerequisite'ler güvenilir mi?
- Daha önce mastered olup RVR açısından yeniden doğrulanması anlamlı olan Skill var mı?
- Birkaç ayrı Skill yeni bir context içinde birlikte kullanılabiliyor mu?
- Technical English paralel hattında ölçülmesi gereken gerçek bir Objective var mı?
- Hangi evidence gap'leri gelecek haftanın planını değiştirmeli?

WBA-v0 bunları rastgele soru seçerek değil, **önce blueprint oluşturup sonra item/task seçerek** çözer.

---

# 2. Daily micro assessment ile weekly assessment farkı

## DMA-v0
- local/current LearningNeed odaklı,
- aynı gün teaching/practice sonrası kısa checkpoint olabilir,
- tek Objective veya küçük integration check olabilir,
- günlük kapasite içinde fırsatçı ve düşük sürtünmeli çalışır.

## WBA-v0
- session-level bir **coverage blueprint** oluşturur,
- birden fazla Skill/Objective arasında bilinçli seçim yapar,
- recent progress + weakness/verification + prerequisite risk + retention + integration diversity'yi birlikte düşünebilir,
- günlük akıştan daha az yakın bağlamda independent recall/application ölçebilir,
- daha geniş coding/debugging/transfer item'larına yer verebilir,
- sonuçları tek score'a sıkıştırmadan evidence bundle olarak işler.

Kritik:

```text
weekly_scope != stronger_weight
```

Bir evidence yalnız weekly sınavdan geldi diye GRE-v0'da ekstra ağırlık kazanmaz. Gücü, Objective'e uygunluğu ve evidence kalitesinden gelir.

---

# 3. “Weekly” takvim borcu değildir

V1 product requirement olarak haftalık assessment cadence bulunur. Ancak bu:

```text
7 gün geçti -> kaçırılan sınav borcu oluştu
```

anlamına gelmez.

Canonical davranış:

- bir `weekly_assessment_cycle` assessment ihtiyacı açabilir,
- uygun gün/kapasitede planner bunu planlar,
- kullanıcı o hafta yapamadıysa failure oluşmaz,
- sonraki dönemde iki veya üç eski weekly exam üst üste yığılmaz,
- current state üzerinden **fresh weekly blueprint** oluşturulur,
- eski blueprint yalnız devam etmeye teknik olarak güvenliyse resume edilir; aksi halde unresolved semantic needs yeniden compose edilir.

Bu SRR-v0 / no-task-debt yaklaşımıyla uyumludur.

---

# 4. Weekly assessment ayrı mastery queue kurmaz

Weekly orchestration, mevcut state ve LearningNeed'leri kullanır.

Yeni periodic trigger extension:

```text
periodic_assessment_due
```

Bu trigger task'ın neden weekly assessment context'ine girdiğini belirtir; altında gerçek target selection yine Skill/Objective state'inden gelir.

Canonical zincir:

```text
Current state + open LearningNeeds + weekly cycle
→ Weekly Blueprint
→ Blueprint Slots
→ validated/prerequisite-valid TaskCandidates
→ PBR + capacity
→ Assessment Session / Blocks
→ Attempt / Artifact
→ EvidenceEvent
→ GRE / RVR / weakness / verification
→ PRG / Topic state
→ replan
```

Weekly session kendi başına mastery state yazmaz.

---

# 5. Weekly Blueprint

Item seçilmeden önce `WeeklyAssessmentBlueprint` üretilir.

```text
WeeklyAssessmentBlueprint
- blueprint_id
- assessment_scope: weekly
- cycle_id
- generated_at
- state_snapshot_ref
- curriculum_version
- assessment_policy_version
- blueprint_slots[]
- expected_active_minutes
- splittable
- session_window_state
- blueprint_reason_codes[]
```

Blueprint'in amacı bütün curriculum'u test etmek değil, o anki state'te haftalık değerlendirme için **en anlamlı ve güvenilir measurement questions** setini oluşturmaktır.

---

# 6. Blueprint Slot

Her ölçüm ihtiyacı bir slot ile temsil edilir.

```text
AssessmentBlueprintSlot
- slot_id
- role
- target_skill_ids[]
- target_objective_ids[]
- source_state_refs[]
- required_evidence_types[]
- required_skill_ids[]
- criticality
- independence_mode
- allowed_tools_policy
- evaluator_requirement
- variant_family_constraints[]
- context_diversity_requirement?
- expected_active_minutes
- required_for_session_closure: true | false
- slot_status
- reason_codes[]
```

`required_for_session_closure` bütün açık LearningNeed'lerin zorunlu slot olduğu anlamına gelmez. Blueprint generation önce bounded bir ölçüm seti seçer; yalnız o seçilmiş set içindeki kritik slotlar session closure için required olabilir.

---

# 7. Blueprint role family'leri

WBA-v0 aşağıdaki slot role'larını destekler:

```text
recent_required_progress
weakness_or_verification
critical_prerequisite_confidence
retention_due
integration_or_transfer
parallel_english
```

## `recent_required_progress`
Bu hafta yeni ilerleyen required Skill/Objective'lerde günlük evidence'ın ötesinde bağımsız veya farklı-family evidence gerekiyorsa kullanılır.

## `weakness_or_verification`
Current `weakness_detected`, `verification_due` veya remediation sonrası fresh verification ihtiyacını temsil eder.

## `critical_prerequisite_confidence`
Yakın future branch'i etkileyen critical prerequisite için gerçekten ek confidence/evidence ihtiyacı varsa kullanılır. `review_due` tek başına failure değildir.

## `retention_due`
RVR-v0 `review_due` veya uygun delayed-retrieval ihtiyacını weekly session içinde karşılayabilir.

## `integration_or_transfer`
Daha önce öğretilmiş birkaç Skill'i farklı context/problem yapısında birlikte ölçmek için kullanılır. Her component ayrı observable olmalıdır.

## `parallel_english`
Technical English hattında gerçekten assessment due ise eklenir. Her weekly exam'a sabit English sorusu koymak zorunlu değildir.

Kritik:

> Bu role family'leri **quota değildir**. Her hafta her family'den soru bulunmak zorunda değildir.

---

# 8. Target pool oluşturma

Weekly blueprint target pool şu kaynaklardan türetilir:

1. son cycle'dan beri meaningful teaching/practice/evidence ile ilerleyen required Objectives,
2. unresolved verification veya weakness,
3. next-path açısından önemli critical prerequisites,
4. RVR review/retention needs,
5. uygun integration/transfer opportunities,
6. due Technical English assessment needs.

Bir Skill sırf “bu hafta işlendi” diye otomatik sınava girmez. Eğer:
- sufficient fresh independent evidence zaten varsa,
- repeated item düşük information value taşıyorsa,
- Objective daha sonra daha doğru modality ile ölçülecekse,
- prerequisite/validation uygun değilse

blueprint'e alınmayabilir.

---

# 9. Blueprint selection sırası

WBA-v0 yeni weighted score icat etmez. Mevcut PBR/PRG semantiğini kullanır.

Selection mantığı:

1. validation/trust ve prerequisite eligibility,
2. P0/P1 integrity/verification/repair needs,
3. critical/required evidence gaps,
4. recent progress için decision-changing evidence,
5. due retention,
6. integration/transfer diversity,
7. parallel track balance,
8. capacity fit ve bounded session composition.

Sadece kısa olduğu için düşük değerli MCQ, daha anlamlı coding/debugging/verification slot'unun önüne geçirilmez.

---

# 10. Coverage fairness

Weekly exam adil olmak için:

- yalnız kolay/recognition item'larından oluşmamalı,
- yalnız o haftanın son dersinden oluşmamalı,
- yalnız zayıf Skill'leri bombardıman etmemeli,
- sırf broad domain temsili olsun diye irrelevant Skill seçmemeli,
- öğrenciye öğretilmemiş prerequisite yüklememeli,
- aynı variant/dependency family ile coverage şişirmemeli.

Blueprint composition, **state'te ölçülmeye değer olan şeyleri** temsil eder.

`Python`, `Linux`, `CUDA` gibi broad Domain adları doğrudan assessment atomu değildir. D-044 sonrası gerçek blueprint slot'ları canonical Skill/Objective IDs kullanacaktır.

---

# 11. Required / critical coverage

WBA-v0 “her required Skill her hafta ölçülür” demez.

Ancak aşağıdaki durumlar blueprint selection'da güçlü öncelik taşır:
- next learning path'i bloke eden unresolved critical verification,
- critical Objective için eksik required evidence type,
- remediation sonrası bağımsız fresh recheck,
- direct evidence eksik olduğu için mastery decision'ın güvenilir olmadığı required Objective.

Critical Objective'in evidence standardı weekly süre baskısı nedeniyle düşürülemez.

---

# 12. Evidence modality / family / context diversity

Bir weekly session birden fazla target içeriyorsa selector mümkün olduğunda:
- recognition'a aşırı yığılmayı önler,
- recall / code-reading / coding / debugging / explanation / transfer gibi uygun family'leri kullanır,
- GRE-v0'daki eksik required evidence type'ı hedefler,
- yakın varyantların bağımsız evidence sayısını şişirmesini önler,
- meaningful context diversity üretir.

Ancak universal:

```text
her hafta en az X coding + Y debugging + Z MCQ
```

kuralı yoktur.

Objective ne gerektiriyorsa modality onu takip eder.

---

# 13. Multi-Skill / integrated item

Weekly assessment günlük micro check'e göre daha geniş integration task'ları kullanabilir.

Bir integrated item'in her target Objective için evidence üretebilmesi için:

```text
structurally_essential
AND separately_observable
AND prerequisite_valid
AND provenance_valid
AND evaluator_sufficient
```

olmalıdır.

Global `task_passed=true` bütün tagged Skill'lere positive evidence yayamaz.

Tek root prerequisite failure diğer downstream component'leri adil biçimde ölçmeyi engelliyorsa target evidence:
- invalid prerequisite contamination,
- unscored,
- veya yalnız gerçekten observable component'lerle sınırlı

olur.

---

# 14. Weekly assessment için difficulty

Difficulty puan multiplier'ı değildir.

Weekly session:
- recent beginner Objective için basic/medium item,
- required application Objective için authentic application,
- critical coding Objective için production artifact,
- transfer gerektiren Objective için unseen structure

seçebilir.

Zorluk bilinmeyen prerequisite ekleyerek artırılmaz.

---

# 15. Haftalık süre / daily capacity ilişkisi

Weekly assessment **daily hard budget'ın dışında ek süre yaratmaz**.

Session planning:
- weekly blueprint `expected_active_minutes` tahmini üretir,
- planner günün remaining hard budget'ına göre assessment block'ları seçer,
- gerekirse session safe boundaries arasında birden fazla güne bölünebilir,
- kullanıcı açıkça ek süre vermedikçe günlük budget aşılmaz.

Universal fixed:

```text
haftalık sınav = 60 dakika
haftalık sınav = 30 soru
```

kuralı yoktur.

---

# 16. Atomic block / split davranışı

Weekly exam tek dev unsplittable task olmak zorunda değildir.

```text
WeeklyAssessmentSession
→ Block A
→ safe checkpoint
→ Block B
→ safe checkpoint
→ Block C
```

Kurallar:
- individual item/testlet atomic evidence boundary ortasında kesilmez,
- coding/project artifact güvenli checkpoint'i yoksa rastgele bölünmez,
- blocks arasında pause/resume olabilir,
- completed attempts/evidence korunur,
- unfinished block negative evidence değildir.

---

# 17. Pause / resume

Pause:
- failure değildir,
- assistance değildir,
- mastery signal değildir.

Resume güvenliyse aynı session devam eder.

Aşağıdaki durumlarda unresolved slot fresh item ile recomposed edilebilir:
- solution/explanation exposure oldu,
- item version/validation değişti,
- prerequisite state meaningful biçimde değişti,
- çok uzun ara nedeniyle exact item freshness güvenilir değil,
- user requested reset/alternative.

Completed valid evidence silinmez.

---

# 18. Incomplete session

Weekly session incomplete ise:
- submitted valid attempts normal evidence üretir,
- unsubmitted item'lar `incorrect` sayılmaz,
- incomplete slot mastery penalty üretmez,
- session `partial` olabilir,
- unresolved semantic measurement need current state'te kalabilir,
- sonraki plan fresh candidate/slot oluşturabilir.

İki haftalık incomplete exam iki ayrı borç exam'a dönüşmez.

---

# 19. Assistance — H0 / H1–H4

Mastery/verification iddiası taşıyan weekly slot varsayılan olarak:

```text
independence_mode = h0_required
```

Kullanıcı hint/AI yardımı isterse yardım engellenmez.

### H1/H2
- attempt `assisted_learning_only` olabilir,
- positive independent GRE evidence üretmez,
- slot bağımsız evidence ihtiyacını çözmemiş sayılabilir,
- daha sonra fresh H0 recheck gerekir.

### H3/H4
- `practice_only_solution_exposed`,
- same item/family immediate independent recheck olamaz,
- fresh/unseen item gerekir.

Yardım istemek negative evidence değildir.

Allowed developer tools policy ayrıca tanımlıdır. Örneğin Objective gerçek terminal/debugger kullanımını ölçüyorsa izin verilen terminal/debugger kullanımı H0'ı bozmaz.

---

# 20. Invalid / ambiguous / provisional item güvenliği

Weekly exam daha geniş olduğu için item validity standardı düşmez; tersine strong mastery-changing evidence için high trust gerekir.

Aşağıdakiler positive veya negative mastery evidence üretemez:
- ambiguous prompt,
- wrong answer key,
- undeclared prerequisite,
- corrupted content/version,
- evaluator failure,
- target attribution yapılamaması,
- high-stakes kullanım için yeterli validation olmayan AI-generated item.

Provisional evaluator:
- feedback verebilir,
- confirmation slot açabilir,
- critical mastery/remediation kararını tek başına belirleyemez.

Invalid slot blueprint coverage'ı tamamlamış sayılmaz; capacity uygunsa fresh valid replacement seçilebilir.

---

# 21. Raw exam score mastery değildir

WBA-v0 `8/10 = mastered` veya `60 altı = failed` kuralı kullanmaz.

UI isterse doğru/yanlış sayısını bilgilendirici olarak gösterebilir; fakat canonical learning state bunu kullanmaz.

Weekly result'ın asıl çıktısı:
- hangi Objectives için valid positive evidence oluştu,
- hangi Objectives için valid negative/partial evidence oluştu,
- hangi evidence invalid/provisional/assisted kaldı,
- hangi verification/remediation needs açıldı,
- planner'ın neyi değiştirdiği.

---

# 22. Positive weekly result

Her valid H0 direct verified evidence event:
1. Objective'e yazılır,
2. GRE-v0 recent bounded window'a uygun şekilde girer,
3. hard gates yeniden değerlendirilir,
4. RVR state gerekirse güncellenir,
5. PRG readiness değişebilir,
6. Topic derived state değişebilir,
7. LearningNeed kapanabilir/değişebilir,
8. planner replan yapabilir.

Weekly exam bütün Skill'i tek hareketle “passed” yapmaz.

---

# 23. Negative weekly result ve hysteresis

## Henüz mastered olmayan Skill
Valid H0 direct negative/partial evidence normal GRE pipeline'ına girer ve targeted weakness/remediation ihtiyacı oluşturabilir.

## Daha önce mastered Skill
İlk clean contradiction:

```text
verification_due
```

üretir.

Tek weekly item hatası instant unmastery değildir.

Fresh independent recheck FAIL gibi tekrar eden güvenilir evidence sonrası GRE-v0 doğal biçimde yeniden hesaplanır.

---

# 24. Broad-domain overreaction yasaktır

Bir weekly exam'da:
- bir Python loop item'ı yanlış,
- bir pointer item'ı doğru,
- bir English item'ı provisional

oldu diye sistem:

```text
Python failed
C passed
English failed
```

şeklinde coarse state yazmaz.

Evidence ilgili Objective/Skill'e gider. Domain/Module/Topic summary daha sonra derived edilir.

D-044 ile bu özellikle bağlayıcıdır.

---

# 25. Root-cause / prerequisite contamination

Aynı prerequisite açığı birkaç weekly item'ı etkileyebilir.

Örnek:
- downstream 3 item aynı memory-address prerequisite'ini gerektiriyor,
- kullanıcı root prerequisite'te başarısız.

Bu durumda downstream target'lara kör şekilde üç ayrı negative evidence yazılmaz.

Resolver:
- root prerequisite weakness/verification oluşturabilir,
- contaminated downstream item'ları invalid/unscored yapabilir,
- bağımsız branch'leri normal değerlendirmeye devam eder.

---

# 26. Technical English weekly behavior

Technical English:
- paralel track'tir,
- weekly blueprint'e gerçek assessment due olduğunda slot olarak girebilir,
- sabit “her sınavda X English sorusu” quota'sı yoktur.

English Objective ölçülüyorsa yalnız öğretilmiş grammar/vocabulary prerequisites kullanılır.

Teknik Objective ölçülüyorsa bilinmeyen English grammar target failure'a dönüştürülemez; gerekirse Turkish/bilingual scaffold kullanılır.

---

# 27. Weekly result contract

```text
WeeklyAssessmentResult
- assessment_session_id
- cycle_id
- blueprint_ref
- blueprint_version
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
- newly_opened_verification_ids[]
- newly_opened_weakness_or_remediation_refs[]
- resolved_need_refs[]
- resulting_mastery_state_refs[]
- resulting_retention_state_refs[]
- resulting_prerequisite_state_refs[]
- replan_event_ref?
- assessment_policy_version
```

Bu result bir `overall_mastery_score` zorunluluğu taşımaz.

---

# 28. 4C için ortak generic blueprint contract

4B ile şu abstraction kilitlenir:

```text
AssessmentBlueprint
- assessment_scope: daily_micro | weekly | monthly
- blueprint_id
- generated_at
- state_snapshot_ref
- target_window_ref?
- slot_refs[]
- policy_version
- curriculum_version
- expected_active_minutes

AssessmentBlueprintSlot
- slot_id
- role
- target_skill_ids[]
- target_objective_ids[]
- required_evidence_types[]
- required_skill_ids[]
- criticality
- independence_mode
- allowed_tools_policy
- evaluator_requirement
- diversity_constraints[]
- expected_active_minutes
- required_for_session_closure

AssessmentSessionResult
- assessment_scope
- blueprint_ref
- session_status
- attempt_result_refs[]
- evidence_event_refs[]
- invalid/provisional/assisted refs[]
- state_change_refs[]
- replan_event_ref?
```

4C bu contract'ı kullanacak; yalnız monthly scope daha geniş transfer/integration ve critical revalidation policy'si ekleyecek.

---

# 29. Weekly-specific reason codes

PDT-v0 `assessment.*` namespace'i genişletilir:

```text
assessment.weekly.due
assessment.weekly.blueprint_generated
assessment.weekly.slot_recent_progress
assessment.weekly.slot_weakness_or_verification
assessment.weekly.slot_critical_prerequisite
assessment.weekly.slot_retention_due
assessment.weekly.slot_integration_transfer
assessment.weekly.slot_parallel_english
assessment.weekly.no_eligible_target
assessment.weekly.no_valid_item
assessment.weekly.capacity_split
assessment.weekly.partial_session
assessment.weekly.incomplete_not_failure
assessment.weekly.assistance_recheck_required
assessment.weekly.invalid_item_replaced
assessment.weekly.prerequisite_contaminated
assessment.weekly.evidence_bundle_recorded
assessment.weekly.replan_after_result
assessment.weekly.no_exam_debt
```

User-facing explanation yalnız trace'teki gerçek reason'ları paraphrase eder.

---

# 30. User-facing weekly summary

Weekly sonuç ekranı tek nota mahkûm değildir.

Önerilen semantic sections:

```text
Kanıtlanan beceriler
Yeniden doğrulanacak beceriler
Hedefli çalışılması gereken noktalar
Bu sınavda güvenilir ölçülemeyen noktalar
Gelecek planında değişenler
```

Örnek:

> `while termination` için bağımsız debugging kanıtı zayıf olduğu için gelecek planda hedefli tekrar açıldı.

> `pointer dereference` güçlü kaldı; ek ağır tekrar eklenmedi.

> Bir item prerequisite uyumsuzluğu nedeniyle geçersiz sayıldı ve ilerlemeni etkilemedi.

---

# 31. Performance / bounded behavior

D-028 korunur.

Weekly blueprint generation:
- bütün tarihçeyi UI thread'de taramaz,
- compact current Skill/Objectives/evidence-gap/RVR/PRG summaries kullanır,
- bounded target pool ve bounded candidate alternatives üretir,
- item selection/evaluation ağırsa async çalışır,
- session partial results incremental persist edilir.

Exact DB/index/cache 9C/9F ve implementation stages içinde kesinleşir.

---

# 32. WBA-v0 invariants

1. Weekly assessment tek overall mastery score değildir.
2. Weekly evidence GRE/RVR'ı bypass etmez.
3. Fixed soru sayısı/süre/kategori yüzdesi yoktur.
4. Her learned Skill her hafta test edilmek zorunda değildir.
5. Blueprint item'lardan önce üretilir.
6. Blueprint target'ları canonical Skill/Objective state'inden gelir.
7. Validation/trust ve prerequisite eligibility selection'dan önce gelir.
8. Critical evidence standardı süre baskısıyla düşmez.
9. Same/near family coverage'ı sahte biçimde büyütemez.
10. Integrated task global pass'i sibling Skills'e otomatik yayılmaz.
11. H1–H4 positive independent mastery evidence değildir.
12. H3/H4 sonrası fresh/unseen recheck gerekir.
13. Invalid/ambiguous/prerequisite-contaminated item credit/penalty üretmez.
14. Provisional evaluator critical transition'ı tek başına belirleyemez.
15. Incomplete session failure değildir.
16. Missed weekly exam debt/stack oluşturmaz.
17. Daily hard budget otomatik aşılmaz.
18. First clean contradiction instant unmastery değildir.
19. Broad Domain failure/pass state'i raw weekly score'dan yazılmaz.
20. D-044 granular weakness localization korunur.
21. Technical English gizli prerequisite olamaz.
22. Result yalnız canonical evidence/state/replan pipeline üzerinden programı değiştirir.

---

# 33. 4B acceptance criteria

4B PASS için:

1. DMA-v0 ile weekly farkı açık.
2. Weekly blueprint-before-items contract tanımlı.
3. Recent progress / weakness / prerequisite / retention / integration / English role'ları tanımlı ama quota yapılmamış.
4. Fixed scientifically optimal question count/time uydurulmamış.
5. Required/critical Objective evidence gates korunmuş.
6. Evidence modality/family/context diversity davranışı tanımlı.
7. PRG prerequisite fairness ve root contamination guard var.
8. H0/H1–H4 assistance davranışı tanımlı.
9. Pause/resume/incomplete/split davranışı capacity ile uyumlu.
10. Invalid/ambiguous/provisional item safety var.
11. Positive/negative weekly result GRE/RVR hysteresis ile uyumlu.
12. Broad-domain overreaction önlenmiş.
13. Missed weekly exam debt üretmiyor.
14. Weekly result → planner/replan contract tanımlı.
15. 4C için ortak generic blueprint/result contract hazır.
16. D-044 granular capability IDs ile uyumlu.
17. D-028 bounded/performance yaklaşımı korunuyor.

---

# 34. Final 4B kararı

**Final model:** `WBA-v0 — Weekly Blueprint Assessment`

Özet:

```text
weekly assessment cycle
    ↓
current Skill/Objectives + evidence gaps + RVR + PRG + PBR
    ↓
weekly blueprint
    ↓
bounded blueprint slots
    ↓
validated + prerequisite-valid item/task selection
    ↓
capacity-aware session blocks
    ↓
H0 attempt / artifact where independent evidence is required
    ↓
validity + assistance + provenance + evaluator
    ↓
Objective-level EvidenceEvents
    ↓
GRE / RVR / verification / weakness / remediation
    ↓
PRG + Topic derived state
    ↓
next plan / replan
```

WBA-v0, kullanıcının haftalık öğrenmesini tek bir notla yargılayan klasik sınav değil; **daha geniş coverage'ı blueprint ile planlayan, yalnız güvenilir ve objective-matched evidence'ı state'e yazan ve gelecek çalışma planını granular weakness'lara göre değiştiren assessment katmanıdır.**
