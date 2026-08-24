# Prerequisite Readiness & Eligibility Policy — PRG-v0

**Adım:** 3D — Prerequisite davranışı  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24  
**Final model:** `PRG-v0 — Prerequisite Readiness Gate`

Bu belge planner'ın bir LearningNeed / TaskCandidate için gerekli prerequisite Skill'lerin yeterince hazır olup olmadığını, hangi eksiklerin gerçekten dependent work'u bekleteceğini ve hangi eksiklerin yalnız destek/öncelik sinyali olacağını tanımlar.

Bağlayıcı kaynaklar:
- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/MASTERY_FORMULA_V0.md`
- `docs/RETENTION_FORGETTING_SPEC.md`
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/PRIORITY_POLICY_SPEC.md`
- `docs/ENGLISH_FOUNDATION_RULES.md`

Ana ilke:

> **Prerequisite gate'in amacı curriculum'u gereksiz kilitlemek değil, target öğrenme/evidence'ın yorumlanabilir ve adil olmasını sağlamaktır.**

İkinci ilke:

> **Bir prerequisite eksikliği yalnız gerçekten ona bağımlı branch/task'i bekletir. Bağımsız dallar çalışmaya devam eder.**

Üçüncü ilke:

> **`review_due` unutma değildir. Takvimsel review ihtiyacı tek başına hard prerequisite readiness'i bozmaz.**

---

# 1. Canonical prerequisite seviyesi

Runtime prerequisite'in ana birimi:

```text
Skill → Skill
```

Topic/Module/Domain seviyesindeki sıra bilgileri authoring/navigation için kullanılabilir; runtime hard gate mümkün olduğunca canonical Skill readiness üzerinden çözülür.

Bu nedenle:

```text
Topic A completed -> Topic B unlocked
```

tek başına canonical kural değildir.

Doğru mantık:

```text
Target Skill / Task
    ↓
required prerequisite Skills
    ↓
current Skill readiness
    ↓
eligibility
```

---

# 2. Hard ve soft prerequisite anlamı

## 2.1 `hard`

Bir prerequisite `hard` ise, source Skill hedef Skill/task için şu nedenlerden en az biriyle zorunludur:

- target davranışı anlamlı biçimde öğretmek için gerekir,
- task'i adil biçimde çözmek için gerekir,
- target evidence'ı yorumlayabilmek için gerekir,
- source Skill olmadan failure'ın hangi Skill'den kaynaklandığı ayrılamaz,
- source eksikliği hedef task'i pedagojik olarak geçersiz hale getirir.

Örnek:

```text
understand_memory_address --hard--> dereference_pointer
```

Hard prerequisite hazır değilse dependent **new learning / independent practice / assessment / retention / transfer** task'i varsayılan olarak eligible değildir.

## 2.2 `soft`

Soft prerequisite:

- işi kolaylaştırır,
- bağlam/akıcılık sağlar,
- daha ileri bir candidate seçmeye yardım eder,
- fakat eksikliği target davranışı öğrenmeyi veya adil biçimde ölçmeyi imkânsız kılmaz.

Soft prerequisite eksikliği tek başına task'i kilitlemez.

Planner:
- daha scaffolded candidate seçebilir,
- destek/reinforcement need oluşturabilir,
- aynı band içinde priority sinyali kullanabilir,
- fakat target Skill'i otomatik `ineligible` yapmaz.

Kritik authoring kuralı:

> Eğer bir "soft" Skill olmadan task gerçekte çözülemiyorsa veya failure target Skill'e adil biçimde bağlanamıyorsa o dependency soft değildir; task metadata/graph düzeltilmelidir.

---

# 3. Skill prerequisite readiness state

PRG-v0 mastery ve retention'ı tek puana dönüştürmez. Her prerequisite için semantik readiness çıkarır:

```text
ready
ready_due
uncertain
not_ready
```

## `ready`

Prerequisite:
- GRE-v0 `mastered`,
- unresolved contradiction yok,
- retention `fresh | stable` veya eşdeğer sağlıklı state.

## `ready_due`

Prerequisite:
- GRE-v0 `mastered`,
- retention `review_due`,
- yeni negatif evidence yok.

**`ready_due` hard gate'i bozmaz.** Review planner priority'sini artırabilir fakat dependent branch otomatik kilitlenmez.

## `uncertain`

Prerequisite historical/current mastery taşıyor olabilir fakat güvenilirliği yeniden doğrulanmalıdır.

Örnekler:
- `verification_due`,
- `at_risk` + unresolved concern,
- evaluator/provenance belirsizliği nedeniyle prerequisite readiness'i yeniden kontrol edilmesi gereken durum.

`uncertain` davranışı task/edge kritikliğine göre aşağıdaki strictness kurallarıyla çözülür.

## `not_ready`

Örnekler:
- prerequisite Skill henüz mastered değil,
- GRE-v0 required gate karşılanmıyor,
- confirmed weakness/remediation nedeniyle prerequisite kullanılabilir seviyede değil,
- prerequisite hiç öğretilmemiş/diagnostic waiver almamış.

Hard prerequisite için `not_ready` dependent candidate'ı bloke eder.

---

# 4. Hard prerequisite eligibility matrix

V1 canonical davranış:

| Prerequisite readiness | Normal hard edge | Critical prerequisite / strict task |
|---|---|---|
| `ready` | eligible | eligible |
| `ready_due` | eligible | eligible; review priority artabilir |
| `uncertain` | conditional eligible | blocked until verification by default |
| `not_ready` | blocked | blocked |

## 4.1 Normal hard edge + `uncertain`

Her `verification_due` bütün curriculum'u kilitlemez.

Normal hard edge'de planner:
- prerequisite verification need'ini P1/P2'ye yükseltebilir,
- target candidate'ı `conditional_eligible` tutabilir,
- fakat high-stakes target assessment/transfer evidence için strict prerequisite confidence isteyebilir.

Bu davranış kullanıcıyı tek bir şüpheli sinyal yüzünden gereksiz yere durdurmayı önler.

## 4.2 Critical prerequisite / strict task + `uncertain`

Aşağıdakilerden biri varsa strict gate uygulanır:
- source Skill `critical_prerequisite=true`,
- dependent candidate açıkça `requires_strict_prerequisite_confidence=true`,
- source'taki hata target evidence'ı ciddi biçimde contaminate edecek,
- RVR-v0 `verification_due` source Skill için unresolved contradiction taşıyor ve dependent yeni work gerçekten ona dayanıyor.

Bu durumda dependent **yeni** work bekler; önce fresh prerequisite verification/remediation çözülür.

Bu D-032/D-035 ile uyumludur: critical `review_due` hard lock değildir, critical unresolved contradiction olabilir.

---

# 5. Soft prerequisite eligibility

Soft edge readiness hiçbir durumda tek başına hard block üretmez.

```text
soft + ready        -> eligible
soft + ready_due    -> eligible
soft + uncertain    -> eligible_with_support
soft + not_ready    -> eligible_with_support
```

Planner candidate seçiminde:
- daha düşük complexity,
- daha fazla önceden öğretilmiş scaffold,
- prerequisite olmayan yardımcı açıklama,
- alternatif task family

seçebilir.

Ancak support, target Objective'in ölçmek istediği davranışı kullanıcı adına yapamaz.

---

# 6. Task-level required Skills graph'tan daha spesifik olabilir

`TaskCandidate.required_skill_ids[]` actual task execution için zorunlu prerequisite'leri belirtir.

Bu liste runtime'da hard eligibility gereksinimidir.

Örnek:

Bir generic `pointer_dereference` Skill'i `malloc` gerektirmeyebilir. Fakat belirli bir pointer assessment item'ı çözmek için `malloc/free` gerekiyorsa:

```text
required_skill_ids += dynamic_memory_basic
```

ve kullanıcı bu Skill'i henüz bilmiyorsa candidate **ineligible** olur.

Kural:

> Graph genel öğrenme bağımlılığını, TaskCandidate metadata ise o exact task'in gerçek prerequisite'lerini temsil eder. Exact task requirement graph'ta görünmüyor diye göz ardı edilmez.

---

# 7. `PrerequisiteDecision` contract

Her candidate için resolver şu tür bir sonuç üretebilir:

```text
PrerequisiteDecision
- candidate_id
- eligibility:
    eligible
    conditional_eligible
    eligible_with_support
    blocked
    invalid_prerequisite_metadata
- hard_blocker_skill_ids[]
- uncertain_skill_ids[]
- soft_gap_skill_ids[]
- readiness_snapshot_refs[]
- requires_strict_prerequisite_confidence
- prerequisite_policy_version
- reason_inputs[]
```

Bu çıktı:
- 3C priority'ye,
- 3G explainability'ye,
- ileride 11B Prerequisite Engine'e

girdi olur.

---

# 8. Exact planner execution order

3D sonrası canonical planlama sırası:

```text
1. Current curriculum/mastery/retention/remediation state'i oku
2. Open LearningNeed'leri türet
3. Bounded TaskCandidate alternatives üret
4. Validation/trust filtresi
5. PRG-v0 prerequisite readiness + eligibility çöz
6. Blocked candidate/need'leri eligible set'ten çıkar
7. Gerekirse blocker prerequisite için repair/review/verification LearningNeed oluştur/güncelle
8. Remaining eligible LearningNeed'leri PBR-v0 ile sırala
9. Aynı need için best eligible candidate seç
10. 3A capacity fit: full -> safe split -> smaller alternative -> defer
11. PlannedTask üret
```

Invariant:

```text
priority cannot override prerequisite eligibility
```

ve:

```text
blocked dependent need != failed need
```

---

# 9. Dependent wait, independent continue

Bir hard prerequisite bloke olduğunda yalnız **transitive olarak gerçekten bağımlı yeni work** bekler.

Örnek:

```text
pointer_dereference NOT_READY
    -> linked_list_pointer_operations bekleyebilir
    -> dynamic_memory_pointer_task bekleyebilir

filesystem_navigation READY
    -> Linux practice devam edebilir

Technical English independent need
    -> devam edebilir
```

Planner bütün Domain/Module'u topluca kilitlemez.

Bu davranış D-004 ve `LEARNING_BEHAVIOR_RULES.md` ile bağlayıcıdır.

---

# 10. Started Topic prerequisite regression

Daha önce başlatılmış veya mastered Topic sonradan prerequisite regression yüzünden geriye dönüp `locked` yapılmaz.

Bunun yerine:
- affected Skill-dependent new task'lar blocked/conditional olabilir,
- Topic gerekiyorsa `weakening` veya `remediation_required` derived state alabilir,
- prerequisite repair/reverification planlanabilir,
- Topic içindeki bağımsız objective/task'lar devam edebilir.

Bu `TOPIC_STATE_MACHINE.md` invariant'ını korur.

---

# 11. `review_due` prerequisite davranışı

Bağlayıcı kural:

```text
review_due != not_ready
```

Bir hard prerequisite `review_due` olduğunda:
- current mastery korunur,
- dependent new learning otomatik bloklanmaz,
- criticality/overdue 3C priority'yi artırabilir,
- uygun natural reuse aynı dependent task içinde strict RVR attribution koşulları sağlanıyorsa retention evidence oluşturabilir.

Ancak task sırf review'u kapatmak için component success varsayamaz; natural reuse ayrı verified attribution ister.

---

# 12. Verification/remediation davranışı

## `verification_due`

- source Skill için gerçek contradiction vardır,
- historical mastery otomatik silinmemiştir,
- prerequisite readiness `uncertain` olur.

Critical/strict hard dependency varsa dependent new work bekler.
Normal non-strict dependency'de planner önce verification'ı yükseltir fakat tüm bağımsız work'u durdurmaz.

## `remediation_required` / confirmed not-ready

Hard prerequisite source gerçekten kullanılabilir gate'in altındaysa:
- dependent new work blocked,
- prerequisite remediation need P0/P1 olabilir,
- bağımsız branch devam eder,
- remediation tamamlandı diye otomatik unblocking olmaz; gerekli yeni evidence/readiness gerekir.

---

# 13. Prerequisite contamination guard

Bir target task kullanıcıda bulunmayan hard prerequisite'i gerektiriyorsa task baştan ineligible olmalıdır.

Eğer metadata hatası nedeniyle task yine çalıştırıldıysa ve kullanıcı başarısız olduysa:

```text
target negative evidence = invalid/unusable
```

çünkü failure target Skill'e güvenilir biçimde atfedilemez.

Sistem:
1. Attempt'i history'de tutabilir,
2. `prerequisite_contamination` işaretler,
3. target GRE-v0'a negative evidence yazmaz,
4. missing prerequisite için need oluşturabilir,
5. item/task metadata'sını QA için flag'ler.

Sonradan “kullanıcı bilmiyordu ama yanlış yaptı” diye target mastery düşürülemez.

---

# 14. Undeclared prerequisite discovery

Task execution/evaluation sırasında beklenmeyen bir prerequisite ortaya çıkarsa:

```text
TASK_PREREQUISITE_METADATA_INVALID
```

olayı üretilebilir.

Bu durumda:
- task'ın target evidence iddiası yeniden değerlendirilir,
- gerekiyorsa evidence invalid edilir,
- content QA için metadata correction gerekir,
- planner aynı flawed candidate'ı tekrar seçmemelidir.

High-stakes assessment'ta undeclared prerequisite özellikle kritik validation hatasıdır.

---

# 15. Multi-Skill / integrated task prerequisite kuralı

Integrated task'ta her component Objective attribution için:
- target davranış structurally essential olmalı,
- target ayrı observable olmalı,
- target dışı hard prerequisite'ler hazır olmalı.

Bir component'in prerequisite'i hazır değilse global project success/failure o component için direct evidence'a çevrilmez.

```text
project outcome != automatic component evidence
```

3B/RVR-v0 natural-reuse kuralları korunur.

---

# 16. Technical English özel kuralı

Technical English Skill'leri gerçek task requirement olabilir; fakat global technical progress gate değildir.

Teknik task'in amacı C/Linux/C++ bilgisini ölçmekse:
- bilinmeyen English grammar/vocabulary gizli hard prerequisite olamaz,
- Türkçe/bilingual scaffold kullanılabilir,
- English gap yüzünden technical target'a negative evidence yazılmaz.

English Skill'in kendisini ölçen task'ta ise yalnız daha önce öğretilmiş English prerequisites zorunlu tutulabilir.

Cross-domain prerequisite yalnız gerçekten gerekli ise graph/task metadata'da açıkça modellenir.

---

# 17. Teach/remediate task istisnası

Bir Skill `not_ready` diye onun **kendi prerequisite repair/teaching task'i** bloke edilmez.

Resolver target zinciri doğru yönde değerlendirir:

- `dereference_pointer` için new-learning task memory-address hard prereq'i ister,
- ama `understand_memory_address` remediation task'i `dereference_pointer` mastery istemez.

Hard gap bulunduğunda planner dependent target'i öğretmeye çalışmak yerine mümkünse eksik prerequisite'in uygun teach/practice/remediation task'ini üretir.

---

# 18. Safe compound chain

Planner tek oturumda şu tür güvenli zincir kurabilir:

```text
prerequisite teach/remediate
    -> prerequisite practice/verification
    -> state update
    -> replan
    -> target task (yalnız artık eligible ise)
```

Önemli:
- target task baştan guaranteed planned sayılmaz,
- prerequisite evidence başarısızsa target otomatik açılmaz,
- capacity hedef task'i sonradan sığdırmaya yetmeyebilir,
- gün bu nedenle hard budget'ın üstüne çıkmaz.

---

# 19. Replan triggers

3D prerequisite kaynaklı replan eventleri:

```text
PREREQUISITE_MASTERY_CHANGED
PREREQUISITE_RETENTION_STATE_CHANGED
PREREQUISITE_VERIFICATION_DUE_CREATED
PREREQUISITE_VERIFICATION_RESOLVED
PREREQUISITE_REMEDIATION_OPENED
PREREQUISITE_REMEDIATION_RESOLVED
DIAGNOSTIC_WAIVER_GRANTED
CURRICULUM_PREREQUISITE_EDGE_CHANGED
TASK_PREREQUISITE_METADATA_INVALID
```

`DIAGNOSTIC_WAIVER_GRANTED` exact davranışı 3E'de kesinleşir.

Replan completed evidence'ı silmez; remaining plan current state'ten yeniden çözülür.

---

# 20. Explainability inputs

3G'nin kullanıcıya göstereceği nihai metinler için 3D en az şu reason input'larını üretir:

```text
blocked_missing_hard_prerequisite
blocked_critical_prerequisite_verification_due
blocked_prerequisite_remediation_required
eligible_prerequisite_review_due_not_blocking
eligible_with_soft_prerequisite_gap
conditional_prerequisite_uncertain
independent_branch_available
prerequisite_repaired_replan
invalid_due_to_prerequisite_contamination
```

---

# 21. Determinism ve performance

Aynı:
- prerequisite graph version,
- Skill mastery/retention/remediation snapshot,
- TaskCandidate required Skills,
- strictness flags,
- planner config version

ile PRG-v0 aynı `PrerequisiteDecision` üretmelidir.

D-028 gereği:
- full graph her candidate için baştan taranmak zorunda değildir,
- reverse dependency index/cache kullanılabilir,
- state change yalnız affected downstream candidates/needs'i invalidate edebilir,
- cycle V1 curriculum QA'da invalid graph olarak reddedilmelidir.

Exact DB/index implementasyonu 8C/11B'ye aittir.

---

# 22. Anti-patterns

Yasaklar:

1. Topic completion'ı hard prerequisite mastery yerine kullanmak.
2. Module/Domain eksik diye bütün domain'i kilitlemek.
3. `review_due` diye mastered Skill'i otomatik not-ready yapmak.
4. Soft prerequisite eksik diye task'i hard-lock etmek.
5. Priority P0 olduğu için prerequisite gate'i bypass etmek.
6. Öğretilmemiş prerequisite içeren task failure'ını target Skill'e negative evidence yazmak.
7. Started/mastered Topic'i prerequisite regression yüzünden geriye `locked` yapmak.
8. Critical bir Skill'in sadece etiketi yüzünden tüm curriculum'u dondurmak.
9. English eksikliğini gerçek technical dependency olmadan global technical blocker yapmak.
10. Remediation tamamlandı eventini yeni evidence olmadan prerequisite-ready saymak.
11. Dependent candidate blocked diye LearningNeed'i failure/closed saymak.
12. Bir task'ta undeclared prerequisite bulunduğu halde item'ı trusted tutmaya devam etmek.

---

# 23. Örnekler

## Örnek A — Hard prerequisite eksik

```text
Target: linked_list_insert
Required: pointer_dereference [hard]
pointer_dereference = not_ready

Result:
linked_list_insert candidate = blocked
pointer_dereference learning/remediation = eligible
Linux independent candidate = eligible
English parallel candidate = eligible
```

## Örnek B — Critical prerequisite yalnız review_due

```text
pointer_dereference:
mastery = mastered
retention = review_due
critical_prerequisite = true
```

Result:
- readiness = ready_due
- linked-list new work otomatik kilitlenmez
- pointer retention need priority kazanabilir
- dependent task içinde strict natural reuse mümkünse review ayrıca kapanabilir

## Örnek C — Critical verification_due

```text
pointer_dereference:
mastery historical/current = mastered
retention = verification_due
critical_prerequisite = true
```

Result:
- readiness = uncertain
- dependent new pointer-heavy work blocked
- fresh pointer verification yüksek priority
- filesystem/English bağımsız işleri çalışabilir

## Örnek D — Soft prerequisite gap

```text
Target: basic Linux process inspection
Soft support: advanced shell shortcuts
advanced_shell_shortcuts = not_ready
```

Result:
- target eligible
- planner basic commands kullanan candidate seçebilir
- advanced shortcut bilmemesi target failure değildir

## Örnek E — Prerequisite contamination

Pointer assessment item'ı yanlışlıkla henüz öğretilmemiş `malloc` bilgisini zorunlu kılıyor.

Result:
- candidate aslında ineligible olmalıydı,
- attempt oluştuysa pointer negative evidence invalid,
- content metadata QA flag,
- uygun pointer-only fresh candidate gerekir.

---

# 24. 3D acceptance criteria

3D PASS için:

1. Runtime prerequisite canonical olarak Skill→Skill.
2. Hard/soft semantics açık ve authoring açısından test edilebilir.
3. `ready / ready_due / uncertain / not_ready` readiness ayrımı var.
4. `review_due` hard lock değil.
5. Hard `not_ready` dependent task'i bloke ediyor.
6. Critical/strict `verification_due` dependent new work'u bekletebiliyor.
7. Soft gap task'i hard-lock etmiyor.
8. Task-level required Skills exact candidate eligibility'yi kontrol ediyor.
9. Priority prerequisite'i bypass edemiyor.
10. Yalnız affected branch bekliyor; independent branches devam ediyor.
11. Started Topic prerequisite regression ile `locked` olmuyor.
12. Prerequisite contamination target negative evidence üretmiyor.
13. Missing prerequisite repair need planner'a geri besleniyor.
14. Replan triggers tanımlı.
15. English hidden-prerequisite guard korunuyor.
16. Deterministic/bounded implementation contract var.
17. D-031 GRE-v0, D-032 RVR-v0, D-033 capacity, D-034 TaskCandidate ve D-035 PBR-v0 ile çelişmiyor.

---

# Sonraki alt adım

`3E — Hızlı öğrenme`: Kullanıcı bir prerequisite/Topic'i zaten biliyorsa bunu tek kolay quiz'e güvenmeden nasıl diagnostic ile doğrulayacağımız, coverage waiver/skip davranışı ve false-skip guard tasarlanacaktır.
