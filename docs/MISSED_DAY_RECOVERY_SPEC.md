# Missed-Day / Re-entry Recovery Policy — SRR-v0

**Adım:** 3F — Kaçırılan günler  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24  
**Final model:** `SRR-v0 — State-based Re-entry & Recovery`

Bu belge kullanıcının bir veya daha fazla gün uygulamaya dönmemesi halinde planner'ın nasıl geri döneceğini tanımlar. Amaç eski günlük planları borç gibi biriktirmeden, güncel mastery / retention / prerequisite / remediation durumundan güvenilir ve kapasiteye sığan yeni bir plan üretmektir.

Bağlayıcı kaynaklar:
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/MASTERY_FORMULA_V0.md`
- `docs/RETENTION_FORGETTING_SPEC.md`
- `docs/ADAPTIVE_PLANNER_SPEC.md`
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/PRIORITY_POLICY_SPEC.md`
- `docs/PREREQUISITE_POLICY_SPEC.md`
- `docs/DIAGNOSTIC_WAIVER_SPEC.md`

Ana ilke:

> **Uygulamaya gelinmeyen günler öğrenme borcu değildir. Geri dönüşte eski plan kuyruğu yürütülmez; current state yeniden çözülür ve bugünkü kapasiteye göre fresh plan üretilir.**

İkinci ilke:

> **Absence negatif evidence değildir. Zaman yalnız RVR-v0 retention due/urgency sinyallerini değiştirebilir; GRE-v0 mastery'yi kendi başına düşürmez.**

Üçüncü ilke:

> **Geri dönüşün amacı önce tüm geçmişi “temizlemek” değil, bugün hangi öğrenme/verification/retention işinin en değerli olduğunu güvenilir biçimde seçmektir.**

---

# 1. 3F neyi çözer?

Örnek:

```text
Kullanıcı 30 gün uygulamaya girmedi.
Eski takvimde 25+ task geçmiş görünüyor.
Birçok Skill retention review_due olmuş olabilir.
Bugünkü kapasite = 50 dk.
```

Yanlış davranış:

```text
Dünden kalan 25 task + bugünün task'ları
→ 300+ dk backlog
→ “önce bütün borcu bitir”
```

Canonical davranış:

```text
current time/state refresh
→ stale plan/candidate'ları bırak
→ open LearningNeed'leri current state'ten yeniden üret
→ GRE/RVR/PRG/PBR ile filtrele/sırala
→ bugünkü 50 dk kapasiteye sığan fresh planı üret
```

---

# 2. Absence ne değildir?

Kullanıcının uygulamaya gelmemesi:
- failure değildir,
- negative mastery evidence değildir,
- remediation sebebi değildir,
- streak cezasıyla mastery değiştirmez,
- Topic'i otomatik `weakening` yapmaz,
- Skill'i otomatik `not_mastered` yapmaz,
- geçmiş gün başına yeni task/debt üretmez.

Canonical invariant:

```text
absence_days != missed_task_debt
absence_days != mastery_penalty
absence_days != failure_count
```

---

# 3. Zaman geçince gerçekten ne değişebilir?

Absence boyunca gerçek yeni Attempt/Artifact yoksa GRE-v0 evidence değişmez.

Ancak `now` ilerlediği için RVR-v0 tarafında:
- `fresh/stable → review_due` olabilir,
- mevcut `review_due` daha overdue hale gelebilir,
- `temporal_urgency` planner sinyali güçlenebilir.

Ama yalnız zaman geçti diye:
- `review_due → verification_due` olmaz,
- `review_due → at_risk` olmaz,
- mastered → unmastered olmaz.

`verification_due`, `at_risk`, remediation gibi daha güçlü durumlar yeni/önceden var olan evidence gerektirir.

---

# 4. Eski plan/candidate yaşam döngüsü

## 4.1 Stale `PlannedTask`

Geri dönüşte geçmiş günlere ait başlanmamış `PlannedTask` örnekleri current plan'a taşınmaz.

```text
old planned task != re-entry debt
```

Task history audit için saklanabilir ama günlük planner onu “tamamlanması gereken eski ödev” olarak tekrar kullanmaz.

## 4.2 Eski `TaskCandidate`

TaskCandidate ephemeral seçim alternatifidir. Geri dönüşte current state'ten fresh candidate üretilir.

Aynı LearningNeed hâlâ açıksa:
- aynı türde yeni candidate gelebilir,
- daha kısa/farklı activity gelebilir,
- natural reuse nedeniyle need kapanmış olabilir,
- priority/eligibility değişmiş olabilir.

## 4.3 `LearningNeed`

Kalıcı anlamsal katman budur.

Need hâlâ `open_condition=true` ise current-state regeneration sırasında tekrar görünür. Need çözülmüşse eski task geçmişte kalır.

---

# 5. Paused progress geri dönüşü

Her yarım iş aynı şekilde ele alınmaz.

## 5.1 Resume edilebilir öğrenme/project checkpoint'i

Gerçek `paused_progress` şu koşullarda resume adayı olabilir:
- checkpoint pedagogically safe,
- curriculum/content version hâlâ uyumlu,
- required prerequisites PRG-v0 ile hâlâ eligible,
- task runner state'i bozulmamış,
- continuation gerçek değer taşıyor.

Ama eski checkpoint otomatik seçilmez. `continue_learning` LearningNeed olarak PBR-v0 ile yeniden sıralanır.

## 5.2 Kesilmiş high-stakes/independent attempt

Assessment, diagnostic, retention verification veya H0 mastery attempt'i uzun arayla yarım kaldıysa eski attempt'in devamı bağımsız evidence olarak güvenilir olmayabilir.

Varsayılan:
- incomplete attempt negative evidence değildir,
- old incomplete attempt mastery score'a girmez,
- gerekiyorsa fresh/unseen candidate üretilir,
- solution exposure olduysa 2D same-item/recheck kuralları uygulanır.

---

# 6. Canonical re-entry pipeline

Kullanıcı geri döndüğünde planner şu sırayı izler:

```text
1. now / last_active metadata refresh
2. completed evidence ve gerçek checkpoints'i koru
3. stale PlannedTask / ephemeral TaskCandidate'ları current plan'dan çıkar
4. GRE-v0 mastery state'i mevcut evidence'dan oku
5. RVR-v0 time-based due state'lerini güncelle
6. remediation / verification flags'i koru ve güncelle
7. PRG-v0 prerequisite readiness'i yeniden çöz
8. Topic derived state'lerini yeniden hesapla
9. current open LearningNeed set'ini yeniden üret / dedupe et
10. candidate validation/trust
11. PRG-v0 eligibility
12. PBR-v0 priority/rank
13. 3A daily capacity fit
14. fresh DailyPlan üret
```

Bu işlem eski günlerin planlarını tek tek replay etmez.

---

# 7. Re-entry'de hangi ihtiyaçlar korunur / yeniden doğar?

## 7.1 `verification_due`

Absence öncesi unresolved ise kaybolmaz. Fresh verification ihtiyacı open kalır.

Critical prerequisite'i gerçekten bloke ediyorsa PBR-v0 P0 davranışı uygulanabilir.

## 7.2 `remediation_required`

Absence remediation'yı “tamamlanmış” yapmaz. Need açık kalır ve current state'e göre fresh remediation candidate üretilir.

## 7.3 `retention_review_due`

Her due Skill ayrı eski task borcu yaratmaz. RVR state üzerinden current retention need yeniden türetilir.

## 7.4 `continue_learning`

Kullanıcının daha önce başladığı Topic/Skill hâlâ uygun ise continuation value taşıyabilir. Ama prerequisite veya curriculum state değişmişse planner buna göre yeniden karar verir.

## 7.5 `new_learning`

Uzun absence var diye new learning globally kapatılmaz. P0/P1 bütünlüğü çözülmüş ve relevant prerequisites uygunsa capacity'nin kalanında yeni öğrenme devam edebilir.

## 7.6 Parallel track / English

Absence Technical English'i teknik rotaya global blocker yapmaz. English need'leri PBR starvation/track-balance kurallarıyla normal aday havuzunda yaşar.

---

# 8. “1 / 7 / 30 / 60+ gün” için davranış

SRR-v0 pedagojik kararı sabit gün eşikleriyle vermez. Aynı state-driven algoritma tüm aralarda çalışır.

`absence_duration`:
- audit/UX için kaydedilebilir,
- RVR `now - next_review_at` üzerinden temporal urgency'yi etkileyebilir,
- fakat tek başına mastery veya priority band üretmez.

İllüstratif davranış:

### ~1 gün ara
Çoğu state değişmemiş olabilir. Planner normal current-state replan yapar; dünkü unfinished task borç değildir.

### ~7 gün ara
Bazı retention need'leri due olmuş olabilir. PBR bunları urgency/criticality ile sıralar; günlük capacity aşılmaz.

### ~30 gün ara
Daha fazla Skill review_due olabilir. Eski planların hiçbiri replay edilmez. P0/P1 + kritik bakım + seçilmiş due ihtiyaçlar current capacity içine alınır; kalan need'ler açık kalır.

### ~60+ gün ara
Mastery otomatik silinmez ve bütün curriculum yeniden başlatılmaz. Critical prerequisites, unresolved verification/remediation ve en değerli retention checks öne çıkabilir. Başarılı fresh evidence geldikçe state hızla normalize edilir; new learning güvenli dallarda devam edebilir.

Bu gün sayıları **davranış threshold'u değil açıklama örnekleridir.**

---

# 9. Backlog catastrophe önleme

## 9.1 Due item listesi kullanıcı planı değildir

Sistemde 80 Skill `review_due` olabilir; bu, Today ekranında 80 review göstermek anlamına gelmez.

```text
due state inventory != DailyPlan
```

DailyPlan her zaman PBR + PRG + capacity sonucu seçilmiş bounded alt kümedir.

## 9.2 Aynı Skill için duplicate due task yok

Bir Skill için aynı planning round'da aynı anlamsal retention need'i tekrar tekrar üretilemez.

```text
1 open semantic retention need / Skill / relevant Objective scope
```

Candidate alternatifleri olabilir; yalnız biri seçilir.

## 9.3 Integrated verification kullanılabilir ama otomatik yayılım yok

Tek bir iyi tasarlanmış integrated task birkaç Skill'i aynı anda ölçebilir **yalnız** her Skill için:
- structurally essential kullanım,
- ayrı observability/attribution,
- prerequisite-valid,
- H0/provenance uygunluğu,
- verified rubric/test

sağlanıyorsa.

Bir integrated task başarılı oldu diye gözlenmeyen sibling/descendant Skill'lerin due state'i otomatik temizlenmez.

## 9.4 Candidate generation bounded olmalı

Implementation bütün due history'yi UI thread'de taramamalıdır.

V1 tasarım beklentisi:
- indexed current-state query,
- Skill/need başına bounded candidate alternatives,
- duplicate suppression,
- PBR için gereken top candidate set'ini incremental üretme.

Exact query/candidate limitleri 8C/11C/17E performans aşamalarında kalibre edilir; 3F bilimsel olmayan sabit `10 review` limiti koymaz.

---

# 10. Recovery priority — yeni bir gizli puan sistemi yok

3F ayrı `absence_priority_score` üretmez.

Canonical PBR-v0 bandları korunur:
- P0: gerçek integrity blocker,
- P1: repair / verification,
- P2: critical/overdue maintenance + valuable continuation,
- P3: normal progress / standard retention / parallel track,
- P4: optional reinforcement.

Absence yalnız mevcut sinyallerin current değerlerini değiştirir.

Özellikle:
- critical `verification_due` ve dependent path → P0 olabilir,
- remediation/verification → P1 olabilir,
- critical `review_due` → P2 maintenance olabilir,
- standard `review_due` → P3 veya mevcut PBR kurallarına göre daha yüksek urgency,
- new learning → prerequisites uygunsa P3,
- optional practice → P4.

`review_due` yalnız uzun absence nedeniyle P0/P1'e dönüşmez.

---

# 11. Starvation ile absence kesin ayrıdır

PBR starvation şu durumda büyür:

> Need eligible olduğu aktif planning günlerinde capacity/priority nedeniyle tekrar tekrar seçilemedi.

Kullanıcının uygulamaya hiç gelmediği günler:

```text
starvation_increment = 0
```

Absence öncesinde oluşmuş starvation state korunabilir ama ara verilen gün sayısı ona eklenmez.

Retention overdue age ise RVR temporal urgency olarak doğal biçimde ilerleyebilir. Bunlar iki farklı sinyaldir:

```text
starvation = planner repeatedly deferred an eligible need
retention overdue = time since review due
```

---

# 12. Geri dönüşte kullanıcı “bu arada dışarıda kullandım” derse

Self-report yararlı bağlamdır fakat doğrudan mastery/retention evidence değildir.

Örnek:
> “Son iki ay işte C kullandım.”

Sistem:
- ilgili diagnostic/retention scope'u azaltmak için bunu context olarak kullanabilir,
- fresh H0 diagnostic/transfer verification önerebilir,
- kullanıcı verified artifact getirirse normal evidence pipeline'ına sokabilir,
- yalnız self-report ile bütün due Skill'leri refresh etmez.

Bu VDW-v0 self-report ve GRE/RVR evidence ilkeleriyle uyumludur.

---

# 13. Recovery day capacity

Geri dönüş günü de normal 3A hard budget'a tabidir.

Örnek:

```text
open current needs = ~140 dk
user capacity = 40 dk
planning budget ≈ 36 dk (V0 reserve uygulanıyorsa)
```

Planner:
- en önemli eligible ~36 dk'yı seçer,
- user explicit extension istemedikçe 140 dk plan üretmez,
- kalan LearningNeed'leri failure saymaz,
- ertesi gün tekrar current state'ten replan eder.

Geri dönüş gününe özel zorunlu “minimum recovery dakikası” yoktur.

---

# 14. Recovery birden fazla güne yayılabilir fakat “borç programı” değildir

Uzun absence sonrası bütün due state'lerin tek günde çözülmesi gerekmeyebilir.

Sonraki günlerde:
- önceki gün elde edilen yeni evidence state'i değiştirir,
- kapanan need'ler kaybolur,
- açık need'ler fresh candidate üretebilir,
- PBR priority yeniden hesaplanır,
- PRG branch eligibility yeniden hesaplanır,
- capacity yeniden çözülür.

Bu nedenle recovery bir süreç olabilir fakat lineer `Day -30 backlog → Day -29 backlog...` replay edilmez.

---

# 15. Return plan new learning'i tamamen dondurmaz

Kullanıcı uzun aradan döndüğünde “önce bütün eski review'ları bitir” zorunluluğu yoktur.

New learning şu koşullarda aynı gün planlanabilir:
- ilgili hard prerequisites PRG-v0 ile eligible,
- unresolved critical blocker yok,
- PBR-v0 sıralamasında capacity kalıyor,
- task contamination riski yok.

Bu, geri dönüşü yalnız ceza/review oturumuna dönüştürmemek için önemlidir.

---

# 16. Topic state davranışı

Absence tek başına Topic'i:
- `learning → remediation_required`,
- `mastered → weakening`,
- `mastered → locked`

yapmaz.

Topic state yalnız canonical Skill/mastery/retention/remediation girdilerinden yeniden türetilir.

Başlanmış Topic prerequisite regression ile `locked` yapılmaz; 3D branch-local gating uygulanır.

---

# 17. Recovery event / trace contract

Geri dönüşte planner machine-readable context üretebilir:

```text
ReentryContext
- last_active_at
- returned_at
- absence_duration_days   # informational
- stale_planned_task_count
- preserved_checkpoint_ids[]
- invalidated_incomplete_attempt_ids[]
- open_need_count_by_trigger
- due_skill_count_by_retention_state
- p0_p1_need_count
- resolved_daily_capacity_minutes
- policy_version
```

Bu alanların hiçbiri mastery score değildir.

3G explainability için planner ayrıca hangi stale task'in neden taşınmadığını ve bugünkü görevlerin hangi current need nedeniyle seçildiğini reason inputs olarak tutabilir.

---

# 18. Deterministik resolver özeti

Aynı:
- curriculum/config version,
- evidence state,
- retention state,
- current time,
- daily capacity,
- checkpoint state

girdileri için aynı current need set'i ve aynı selection order elde edilmelidir.

LLM:
- absence nedeniyle mastery silemez,
- eski task backlog'u keyfi geri getiremez,
- hard budget'ı uzatamaz,
- prerequisite gate'i bypass edemez,
- due Skill'i evidence olmadan fresh/stable yapamaz.

---

# 19. 3F acceptance senaryoları

## Senaryo A — 1 gün ara
- eski unstarted PlannedTask replay edilmez,
- current need hâlâ açıksa fresh candidate üretilebilir,
- absence penalty yok.

## Senaryo B — 7 gün ara + 4 review_due
- dört due state inventory'de bulunur,
- Today plan yalnız capacity/PBR'ın seçtiği kadarını içerir,
- geri kalan failure/debt değildir.

## Senaryo C — 30 gün ara + critical review_due
- critical review_due hard lock değildir,
- P2 bakım önceliği alabilir,
- dependent new work automatic lock olmaz.

## Senaryo D — 30 gün ara + critical verification_due
- verification unresolved ise dependent branch PRG/PBR ile bekleyebilir,
- independent branch devam eder.

## Senaryo E — 60+ gün ara + 30 review_due
- mastery otomatik düşmez,
- 30 task planlanmaz,
- bounded current-state candidate set + capacity planı oluşur,
- integrated verification yalnız separately attributable Skills'i kapatır.

## Senaryo F — 60 gün ara + paused project
- safe/version-valid checkpoint continuation adayıdır,
- otomatik seçilmez,
- prerequisite/priority/capacity yeniden değerlendirilir.

## Senaryo G — user app dışında çalışmış
- self-report automatic refresh değildir,
- diagnostic/verified artifact normal evidence pipeline'ına girebilir.

## Senaryo H — absence boyunca hiçbir app açılışı yok
- starvation counter artmaz,
- retention overdue age ilerleyebilir.

---

# 20. 3F'de bilinçli olarak ertelenenler

3F şunları finalleştirmez:
- kullanıcıya gösterilecek exact dönüş metinleri / reason codes → 3G / 7C,
- full planner pseudocode → 3G,
- scenario simulation acceptance → 3H,
- DB/index/query implementation → 8C / 11C,
- notification cadence → 15E,
- exact performance/candidate scan limits → 17E,
- retention interval calibration → 17C.

---

# 21. Final invariant set

```text
absence != failure
absence != mastery decay
absence != task debt
old PlannedTask != tomorrow plan
current state -> fresh LearningNeed/candidate generation
review_due != forgetting
starvation != absence duration
recovery obeys daily hard capacity
critical blocker affects only dependent branch
integrated recovery evidence requires separate attribution
new learning may continue when safe
same state + config + time + capacity -> deterministic plan inputs
```

Bu belge 3F için canonical missed-day/re-entry policy'dir.
