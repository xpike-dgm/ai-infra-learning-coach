# Planner Priority & Selection Policy — PBR-v0

**Adım:** 3C — Öncelik puanı  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24  
**Final model:** `PBR-v0 — Priority Bands & Rank Vector`

Bu belge, açık LearningNeed'ler ve bunlardan üretilen TaskCandidate'lar günlük kapasiteye birlikte sığmadığında **hangi ihtiyaçların bugün seçileceğini** tanımlar.

Bağlayıcı kaynaklar:
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/MASTERY_FORMULA_V0.md`
- `docs/RETENTION_FORGETTING_SPEC.md`
- `docs/ADAPTIVE_PLANNER_SPEC.md`
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/ENGLISH_FOUNDATION_RULES.md`

Ana ilke:

> **Planner eski görevleri sıraya koymaz; açık LearningNeed'leri önem, risk, güncellik ve capacity bağlamında yeniden değerlendirir.**

İkinci ilke:

> **Öncelik tek bir sahte-hassas puanın toplamı değildir. Önce semantik bir priority band seçilir, sonra aynı band içindeki ihtiyaçlar deterministik rank vektörüyle sıralanır.**

Üçüncü ilke:

> **Bir ihtiyaç bugün seçilmedi diye kaybolmaz ve failure olmaz. Açık kaldığı sürece ileriki planlarda yeniden adaydır; starvation guard uzun süre sürekli ertelenmesini engeller.**

---

# 1. 3C neyi çözer?

Örnek:

```text
Açık ihtiyaçların toplam planning cost'u = 80 dk
Bugünkü planning budget = 50 dk
```

3C şu soruyu cevaplar:

```text
Hangi 50 dk seçilecek?
```

3C şunları yapmaz:
- prerequisite eligibility'nin bütün ayrıntısını tanımlamaz → 3D,
- skip/diagnostic waiver mantığını tamamlamaz → 3E,
- uzun ara recovery algoritmasını tamamlamaz → 3F,
- kullanıcıya gösterilecek nihai reason-code metnini tasarlamaz → 3G,
- gerçek planner doğruluğunu simülasyonla kabul etmez → 3H.

---

# 2. Priority, task değil LearningNeed seviyesinde başlar

Önce LearningNeed sıralanır. Aynı need için birden fazla TaskCandidate varsa capacity ve evidence gereksinimine en uygun candidate seçilir.

```text
LearningNeed priority
    ↓
need için best eligible candidate alternative
    ↓
capacity'ye yerleştirme
```

Bu nedenle 5 dakikalık düşük değerli bir task, yalnız kısa olduğu için 20 dakikalık kritik doğrulamanın önüne geçemez.

Yasak anti-pattern:

```text
priority_per_minute = score / duration
```

Bu yaklaşım kolay/kısa görev bias'ı yaratabileceği için canonical değildir.

---

# 3. Eligibility priority'den önce gelir

Bir task ne kadar yüksek priority taşısa da kullanıcı için geçerli değilse seçilemez.

3C invariant:

```text
ineligible candidate cannot be rescued by priority
```

3D hard/soft prerequisite davranışını kesinleştirecektir. 3C, 3D'nin sağlayacağı eligibility/prerequisite-relevance sinyalini tüketmeye hazırdır.

Validation/trust açısından high-stakes assessment/retention task'i trusted/validated değilse aynı şekilde priority ile güvenilir hale gelmez.

---

# 4. PBR-v0 priority bands

Band numarası küçüldükçe ihtiyaç daha önce ele alınır.

## P0 — `integrity_blocker`

Mevcut öğrenme zincirinin güvenilirliğini çözmeden ilerlemenin yanlış olabileceği durumlar.

Örnekler:
- critical prerequisite Skill'de unresolved `verification_due` ve ona bağlı yeni work bekliyor,
- critical prerequisite için `remediation_required` ve dependent branch gerçekten bloke,
- mevcut state çelişkisini çözmeden planner'ın doğru branch seçemediği zorunlu doğrulama.

P0 yalnız `critical Skill` olduğu için verilmez. **Gerçek blocking/integrity koşulu** gerekir.

`review_due` tek başına P0 değildir.

## P1 — `repair_or_verify`

Yeni negatif/çelişkili evidence sonrası hedefli onarım veya doğrulama.

Örnekler:
- non-blocking `verification_due`,
- `remediation_required`,
- actionable weakness_detected,
- `at_risk` + gerçek negatif/partial evidence nedeniyle fresh verification ihtiyacı.

Criticality, aynı band içinde sıralamayı yükseltir.

## P2 — `maintain_or_continue`

Bilgiyi kaybetmeden veya mevcut öğrenme bağlamını gereksiz kesmeden sürdürmeye yönelik ihtiyaçlar.

Örnekler:
- critical prerequisite `review_due`,
- anlamlı biçimde overdue retention review,
- `paused_progress` içeren devam görevi,
- aktif Topic/Skill üzerinde `continue_learning`,
- çözülmezse yakında P1/P0'a dönüşme riski olan bakım ihtiyacı.

`review_due` forgetting değildir; bu band yalnız scheduling önemini ifade eder.

## P3 — `planned_progress`

Normal ileri öğrenme ve paralel gelişim.

Örnekler:
- normal `new_learning`,
- normal `continue_learning`,
- standard `retention_review_due`,
- `parallel_track_due` (ör. Technical English),
- bir sonraki öğrenme kararını verimli kılacak uygun `diagnostic_opportunity`.

Bu band içindeki task dağılımı sabit yüzdeyle yapılmaz.

## P4 — `reinforce_or_optimize`

Faydalı fakat bugün yapılmaması temel ilerlemeyi/ölçüm bütünlüğünü bozmayacak fırsatlar.

Örnekler:
- `reinforcement_opportunity`,
- optional integration task,
- ek transfer/practice,
- zorunlu olmayan project/integration opportunity,
- düşük ROI destekleyici practice.

---

# 5. Band tek başına yeterli değildir — rank vector

Aynı band içindeki LearningNeed'ler şu deterministik vektörle sıralanır:

```text
PriorityRankVector
1. integrity_or_blocking_scope
2. criticality
3. evidence_severity
4. temporal_urgency
5. starvation_pressure
6. continuation_value
7. decision_value
8. track_balance_pressure
9. duration_fit_class
10. stable_tie_break_key
```

Bu bir ağırlıklı toplam değildir. Alanlar sırayla karşılaştırılır.

---

# 6. Rank alanları

## 6.1 `integrity_or_blocking_scope`

Öncelik:

```text
blocks_current_required_path
blocks_next_ready_dependency
non_blocking
```

Exact hard/soft dependency 3D'de kesinleşir.

## 6.2 `criticality`

Semantik sıra:

```text
critical_prerequisite
required
supporting
optional
```

Criticality tek başına P0 yaratmaz; band içi önem sinyalidir.

## 6.3 `evidence_severity`

Örnek sıra:

```text
confirmed_repeated_failure
clean_contradiction_or_verification_due
partial_or_uncertain_concern
no_negative_evidence
```

`review_due` burada negative evidence değildir.

## 6.4 `temporal_urgency`

Retention/due gibi zaman hassas ihtiyaçlarda kullanılır.

Kavramsal sıra:

```text
overdue_high
just_due
due_soon
not_time_sensitive
```

Exact overdue bucket eşikleri RVR/pilot config ile versioned olabilir; 3C bunları bilimsel forgetting probability diye yorumlamaz.

## 6.5 `starvation_pressure`

Bir LearningNeed **eligible olduğu halde capacity/priority nedeniyle tekrar tekrar seçilemiyorsa** starvation basıncı artar.

Önemli ayrım:
- ineligible olduğu günler starvation sayılmaz,
- kullanıcının hiç çalışmadığı gün doğrudan negative evidence değildir,
- task ID değil need bazında izlenir.

V0 için config'lenebilir conceptual buckets:

```text
none
watch
promote
```

`promote` olan soft need aynı band içindeki daha yeni need'lerin önüne çıkar; gerektiğinde P3 → P2 seviyesine **starvation promotion** alabilir.

Starvation promotion:
- P0/P1 integrity/repair işlerini geçemez,
- optional P4'ü zorunlu P2 yapmaz,
- fixed takvim borcu üretmez.

Exact eligible-deferral threshold 18B/18C pilotunda kalibre edilir.

## 6.6 `continuation_value`

Gerçek bir `paused_progress` veya aynı öğrenme bağlamında devam etmenin switching cost'u düşükse kullanılır.

Sıra:

```text
paused_safe_checkpoint
active_learning_context
fresh_new_context
```

Continuation bonus P0/P1'i geçemez. Kullanıcı dünkü task'a başlamış diye bugün sonsuza kadar onu sürdürmek zorunda değildir.

## 6.7 `decision_value`

Kısa bir diagnostic/verification task'i bir sonraki büyük plan kararını belirleyecekse değerlidir.

Örnek:
- “Bu Skill zaten biliniyor mu?” doğrulaması 25 dakikalık gereksiz teach task'ını engelleyebilir.

3E skip/waiver kurallarını kesinleştirecek; 3C yalnız bu bilgi-değer sinyalini taşır.

## 6.8 `track_balance_pressure`

Technical English gibi paralel track'lerin normal progress tarafından sonsuza kadar itilmesini engelleyen soft sinyal.

Track cadence/frequency 7C'de tanımlanır. 3C yalnız şu contract'ı sağlar:

```text
parallel track due + repeated eligible deferral
→ track_balance_pressure yükselir
```

Bu sabit `%20 English` anlamına gelmez.

## 6.9 `duration_fit_class`

Priority semantiği çözüldükten sonra capacity fit için kullanılır.

```text
fits_remaining
fits_via_safe_split
fits_via_smaller_alternative
cannot_fit_today
```

Daha kısa olmak tek başına daha önemli olmak değildir.

## 6.10 `stable_tie_break_key`

Diğer bütün alanlar eşitse deterministik sonuç için:

```text
need_key + candidate_generation_version + canonical candidate id
```

veya eşdeğer stabil sıralama kullanılır.

Random tie-break V1 canonical planner'da kullanılmaz.

---

# 7. Priority band türetme — default decision table

| Need / state | Varsayılan band | Not |
|---|---:|---|
| Critical prerequisite verification/remediation gerçekten branch bloke ediyor | P0 | integrity blocker |
| `verification_due` | P1 | criticality band içinde yükseltir |
| `remediation_required` | P1 | blocking ise P0 olabilir |
| actionable `weakness_detected` | P1 | gerçek evidence gerekir |
| `at_risk` + negative/partial evidence | P1/P2 | severity'ye göre |
| critical `review_due` | P2 | forgetting değildir |
| strongly overdue standard retention | P2 | exact threshold calibration |
| `paused_progress` / meaningful continue_learning | P2/P3 | state'e göre |
| normal `retention_review_due` | P3 | overdue/critical overlay olabilir |
| normal `continue_learning` | P3 | continuity modifier |
| `new_learning` | P3 | prerequisite-valid olmalı |
| `parallel_track_due` | P3 | starvation/track-balance guard |
| useful diagnostic opportunity | P3 | decision_value yüksek olabilir |
| `reinforcement_opportunity` | P4 | natural reuse yine evidence üretebilir |
| optional integration/project | P4 | required curriculum task ise P3 olabilir |

Bu tablo fixed category quota değildir.

---

# 8. Capacity-aware selection algorithm — davranış contract'ı

Planner her replan'da kavramsal olarak:

```text
1. Open LearningNeed'leri al.
2. Eligibility/trust açısından kullanılamayan candidate'ları çıkar.
3. Her need için priority band + rank vector üret.
4. Need'leri band/rank ile sırala.
5. En yüksek need için remaining capacity'ye uyan en iyi task alternative'ını seç.
6. Task sığmıyorsa:
   a. pedagogically safe split,
   b. aynı need'i karşılayan smaller eligible alternative,
   c. bugün defer.
7. Seçilen task'ın cost'unu remaining planning budget'tan düş.
8. Aynı need'in gereksiz duplicate candidate'larını bu plan turunda bastır.
9. State/evidence değişirse fresh replan yap.
10. Capacity dolunca kalan need'leri açık bırak; task debt üretme.
```

Priority yüksek task bugün fiziksel olarak sığmıyorsa planner bütün günü boş bırakmaz; sonraki en yüksek fit task'a geçebilir. Ancak kritik bloklayıcı need unresolved olarak kalır ve dependent work 3D kuralına göre bekleyebilir.

---

# 9. Knapsack / dakika verimliliği anti-pattern'i

Planner'ın amacı “50 dakikaya en çok task sayısını doldurmak” değildir.

Yanlış:

```text
5 tane 10 dk düşük-value task > 1 tane 30 dk critical task
```

yalnız task sayısı daha fazla olduğu için seçilemez.

Aynı şekilde planner toplam numeric utility maksimize eden opak bir knapsack modelini V1'de canonical karar mekanizması yapmaz.

Önce semantic priority, sonra capacity fit gelir.

---

# 10. Aynı need için duplicate çalışma engeli

Bir LearningNeed aynı gün için çok sayıda alternatif üretebilir.

Varsayılan:
- bir candidate seçildiğinde aynı need'in diğer alternatifleri o planning round'da bastırılır,
- task sonucu yeni evidence/remediation doğurursa need state'i yeniden hesaplanır,
- gerekirse fresh follow-up need/candidate oluşur.

Bu, planner'ın tek bir zayıflığa aynı anda 4 benzer task koymasını engeller.

Exception:
- pedagojik olarak açıkça tanımlanmış teach → practice → assess mini-chain.

Bu chain ayrı child-step/sequence contract ile temsil edilmeli; rastgele duplicate değildir.

---

# 11. Balance sabit yüzdeyle değil need-state ile sağlanır

Canonical olarak yok:

```text
40% C
20% English
20% retention
20% remediation
```

Balance şu sinyallerle oluşur:
- gerçek open needs,
- criticality,
- due/overdue,
- verification/remediation,
- continuation,
- starvation pressure,
- parallel-track due,
- capacity.

Böylece sakin bir günde new learning ağırlıklı plan olabilir; başka bir günde remediation/verification ağırlıklı olabilir.

---

# 12. English / paralel track davranışı

English global technical blocker değildir.

3C:
- `parallel_track_due` need'ini P3 progress olarak değerlendirir,
- repeated eligible deferral → starvation/track-balance pressure artırır,
- English'i sırf “parallel” olduğu için her gün zorunlu sabit dakika yapmaz,
- kritik technical integrity P0/P1'i geçirmez,
- ama normal new-learning tarafından süresiz aç bırakılmasını da önler.

Exact English cadence 7C'de tanımlanır.

---

# 13. Retention priority davranışı

RVR-v0 korunur:
- `review_due` = forgetting değildir,
- standard review_due varsayılan P3,
- critical review_due veya yüksek overdue P2 olabilir,
- actual clean failure → `verification_due` → P1,
- critical blocking verification → P0 olabilir.

Bu ayrım time-only sinyali gerçek negative evidence ile karıştırmaz.

---

# 14. Remediation priority davranışı

Remediation genellikle P1'dir çünkü gerçek weakness evidence'ına dayanır.

Ama:
- bütün remediation otomatik P0 değildir,
- düşük ROI/supporting weakness bütün günü yiyemez,
- critical path'i gerçekten bloke eden remediation P0'a çıkar,
- daily hard capacity D-033 gereği yine aşılamaz.

Bir gün çok sayıda P1 need varsa rank vector + starvation/criticality + capacity fit ile subset seçilir; kalanlar açık kalır.

---

# 15. User preference / focus override

Kullanıcı örneğin “bugün Linux çalışmak istiyorum” diyebilir.

Bu V1'de soft preference sinyali olabilir fakat:
- P0 integrity blocker'ı yok sayamaz,
- prerequisite eligibility'yi bypass edemez,
- mastery/evidence'i sahte şekilde değiştiremez,
- remaining normal-progress task'lar arasında tercih etkisi yaratabilir.

Exact UX ve ayar davranışı 7A–7G'de kesinleşir.

---

# 16. 80 dk ihtiyaç / 50 dk capacity örneği

Açık ihtiyaçlar:

```text
A — 15 dk critical pointer verification; dependent C work bekliyor → P0
B — 15 dk address/value remediation → P1
C — 20 dk current C lesson continuation → P2
D — 10 dk Linux retention review_due → P3
E — 10 dk Technical English parallel-track practice → P3
F — 10 dk optional reinforcement → P4
```

Toplam:

```text
80 dk
```

Bugünkü planning budget örnek olarak 50 dk ise:

```text
A 15 + B 15 + C 20 = 50 dk
```

D/E/F failure sayılmaz ve “yarın 30 dk borç” oluşturmaz.

Ertesi plan turunda:
- A/B çözülmüşse artık aday olmayabilir,
- C tamamlanmış olabilir,
- D/E open need olarak yeniden gelir,
- E daha önce birkaç eligible plan turunda ertelendiyse starvation/track-balance baskısı artabilir,
- yeni daha kritik need oluşmuşsa sıralama tekrar değişebilir.

Örneğin English sürekli ertelenmişse E normal P3 içindeki yeni C task'larından öne çıkabilir veya config'e göre starvation promotion ile P2'ye yükselebilir.

---

# 17. Replan triggers

Priority yeniden hesaplanmalıdır:

```text
TASK_COMPLETED
NEW_EVIDENCE_RECORDED
NEW_REMEDIATION_CREATED
NEW_VERIFICATION_DUE_CREATED
RETENTION_STATE_CHANGED
PREREQUISITE_STATE_CHANGED
TODAY_CAPACITY_CHANGED
SESSION_REMAINING_TIME_CHANGED
TASK_OVERRAN_ESTIMATE
USER_REQUESTED_EXTRA_TIME
USER_FOCUS_CHANGED
```

Replan completed evidence'ı silmez; yalnız remaining plan değişir.

---

# 18. PriorityDecisionTrace

3G user-facing explanation'ı tasarlayacaktır. 3C şimdiden machine-readable trace üretmeyi zorunlu kılar:

```text
PriorityDecisionTrace
- learning_need_key
- priority_policy_version
- priority_band
- rank_vector
- source_state_refs
- selected_candidate_id?
- fit_result
- selected: true | false
- defer_reason?
- competing_need_keys[]?
```

Böylece “neden bunu seçtin?” sorusu sonradan reconstruct edilebilir.

---

# 19. Determinizm

Aynı:
- open LearningNeed seti,
- candidate alternatives,
- Skill/mastery/retention/remediation state,
- prerequisite/criticality metadata,
- capacity state,
- starvation/track balance state,
- priority policy version

verildiğinde aynı priority sırası ve aynı selected set çıkmalıdır.

LLM priority band'ını keyfi biçimde değiştiremez.

---

# 20. Performans

D-028 gereği:
- priority hesaplama bounded open-need seti üzerinde çalışır,
- her replan'da tüm tarihi baştan taramak zorunda değildir,
- due/critical/remediation/open-need alanları indekslenebilir,
- rank vector basit/incremental state'ten türetilir,
- candidate alternatives bounded'dır.

Exact DB/index implementasyonu 9C/12C'ye aittir.

---

# 21. V0 calibration / heuristic alanları

Aşağıdakiler engineering policy'dir, bilimsel sabit değildir:
- P0–P4 band sınırlarının bazı edge-case mapping'leri,
- starvation promotion threshold,
- overdue urgency bucket'ları,
- track balance promotion davranışı,
- user-focus soft preference strength,
- continuation tie-break gücü.

Bunlar 3H simulation + 18B/18C pilot ile kalibre edilebilir.

---

# 22. Anti-patterns / yasaklar

1. `remediation=100, retention=70, English=30` gibi açıklamasız sahte-hassas additive score.
2. Task sayısını veya dakika başına task sayısını maksimize etmek.
3. `review_due`yu forgetting/negative evidence saymak.
4. Critical etiketi var diye her task'ı P0 yapmak.
5. Deferred task'ı next-day debt yapmak.
6. Eligible soft need'i sonsuza kadar aç bırakmak.
7. English'i sabit yüzde veya global blocker yapmak.
8. Continuation bonus ile gerçek verification/remediation blocker'ını bastırmak.
9. Priority ile prerequisite eligibility'yi bypass etmek.
10. Random tie-break nedeniyle aynı state'te farklı plan üretmek.

---

# 23. 3C acceptance criteria

3C PASS için:

1. 80/50 capacity problemi deterministic biçimde çözülebiliyor.
2. Critical blocking verification/repair normal progress'ten ayrılıyor.
3. `review_due` ile negative evidence ayrımı korunuyor.
4. Fixed category yüzdeleri yok.
5. Priority tek opak additive score değil; semantic band + rank vector.
6. Starvation guard açık LearningNeed'i süresiz ertelenmekten koruyor.
7. Parallel track starvation önlenebiliyor fakat technical integrity'yi bypass etmiyor.
8. Duration fit priority'den sonra geliyor; short-task bias yok.
9. Safe split / smaller alternative / defer 3A ile uyumlu.
10. Deferred need failure/debt değil.
11. Multi-candidate same-need duplicate baskılanıyor.
12. Replan eventleri tanımlı.
13. Priority trace reconstruct edilebilir.
14. Aynı input → aynı output.
15. D-028 bounded/incremental performans yönü korunuyor.
16. 3D/3E/3F/3G kapsamlarına gereksiz taşma yapılmıyor.
