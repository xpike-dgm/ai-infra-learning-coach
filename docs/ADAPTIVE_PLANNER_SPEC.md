# Adaptive Planner Spec — AŞAMA 3

**Durum:** AKTİF GELİŞEN CANONICAL SPEC  
**Tarih:** 2026-08-24  
**Tamamlanan alt adım:** `3A — Günlük kapasite`

Bu belge Aşama 3 boyunca `3A–3H` adımları ilerledikçe genişletilecek canonical Adaptive Planner spesifikasyonudur.

Bağlayıcı kaynaklarla birlikte okunur:
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/MASTERY_FORMULA_V0.md`
- `docs/RETENTION_FORGETTING_SPEC.md`
- `docs/V1_SCOPE.md`
- `docs/V1_SUCCESS_CRITERIA.md`
- `docs/DECISIONS.md`

Ana ilke:

> **Planner kullanıcının ayırdığı zamanı bir hard bütçe olarak kabul eder. Öğrenme ihtiyacı büyüdü diye günü otomatik uzatmaz; mevcut bütçe içinde yeniden paketler.**

İkinci ilke:

> **Tamamlanmamış veya ertelenmiş görev “borç” değildir. Ertesi plan, eski takvim kuyruğunu sürüklemek yerine güncel Skill/mastery/retention/prerequisite state'inden yeniden üretilir.**

---

# 3A — Günlük Kapasite

## 1. Capacity neyi temsil eder?

`daily_capacity_minutes`, kullanıcının **bugün öğrenmeye ayırmayı gerçekten kabul ettiği toplam zaman üst sınırıdır**.

Bu değer:
- kariyer hedefi için “gereken süre” tahmini değildir,
- kullanıcıyı suçlayan minimum çalışma zorunluluğu değildir,
- planner'ın tüm ihtiyaçları sığdırmak zorunda olduğu sihirli süre değildir,
- mastery veya ilerleme puanı değildir.

Capacity yalnızca şunu söyler:

> `Bugünkü plan bu zaman zarfına sığmalıdır.`

---

## 2. Capacity kaynak önceliği

Planner kesin dakika değerini şu öncelikle çözer:

```text
1. today_explicit_override_minutes
2. selected_day_profile_minutes
3. scheduled_default_for_day
4. normal_profile_minutes
```

En üstte bulunan geçerli değer kullanılır.

Örnek:
- normal preset 60 dk,
- bugün kullanıcı `20 dk vaktim var` dedi,
- planner 60 değil **20 dakika** üzerinden plan üretir.

Kullanıcının bugünkü explicit override'ı mevcut planı anında yeniden hesaplatır.

---

## 3. Short / Normal / Intensive profilleri

V1 kullanıcıya üç kolay preset sunabilir:

```text
short
normal
intensive
```

Bunlar bilimsel “ideal öğrenme süreleri” değildir; kullanıcının günlük hayatına göre düzenlenebilir zaman kısayollarıdır.

### V0 öneri presetleri — engineering defaults

```text
short_profile_minutes_v0 = 30
normal_profile_minutes_v0 = 60
intensive_profile_minutes_v0 = 90
```

Bu değerler:
- editable,
- kullanıcıya özel,
- bilimsel sabit değil,
- pilot/günlük kullanımda değiştirilebilir.

Kullanıcı isterse doğrudan dakika da girebilir; preset seçmek zorunlu değildir.

`intensive`, planner'a kapasiteyi aşma yetkisi vermez. Yalnız daha büyük bir explicit budget'tır.

---

## 4. Hard budget ve planning budget

### Hard budget

```text
hard_budget_minutes = resolved_daily_capacity_minutes
```

Planner kullanıcı açıkça `devam et` / `ek süre ekle` demeden bu üst sınırı otomatik geçmez.

### Planning reserve

Task süre tahminleri kusursuz olmadığı için cold-start V0 küçük bir planlama rezervi bırakır:

```text
planning_reserve_ratio_v0 = 0.10
planning_budget_minutes = floor(hard_budget_minutes * 0.90)
```

Örnek:
- kullanıcı 60 dk ayırdı,
- planner yaklaşık 54 dk task planlar,
- kalan yaklaşık 6 dk süre tahmin hatası/geçiş payıdır.

Bu `10%` **engineering heuristic** olup gerçek usage süreleriyle kalibre edilir.

Çok küçük capacity'de reserve task seçimini anlamsız hale getiriyorsa planner en az bir uygun micro-task çalıştırabilmek için reserve'i esnetebilir; hard budget yine aşılmaz.

---

## 5. Minimum viable study block

V1:

```text
minimum_plannable_block_minutes_v0 = 10
```

Bu da engineering heuristic'tir.

### Kullanıcının 10 dakikadan az zamanı varsa

Sistem:
- günü “başarısız” saymaz,
- streak/mastery cezası vermez,
- heavy new Topic başlatmaya çalışmaz,
- varsa gerçekten 3–8 dakikaya sığabilen micro retrieval / tiny remediation / English micro-task önerebilir,
- hiçbir uygun task yoksa `bugün plan üretmek için yeterli blok yok` diyebilir.

Kullanıcı isterse yine manuel olarak çalışma açabilir.

### 10+ dakika

Planner normal capacity contract'ını kullanabilir.

---

## 6. Capacity sabit kategori yüzdelerine bölünmez

3A aşamasında şu tip katı kurallar **yoktur**:

```text
%40 new learning
%20 retention
%20 remediation
%20 English
```

Çünkü gerçek ihtiyaç state'e bağlıdır.

Örneğin:
- bugün kritik `verification_due` varsa daha çok doğrulama/remediation gerekebilir,
- başka gün hiçbir urgent weakness yoksa daha çok new learning gelebilir,
- bazı günler retention ağırlıklı olabilir.

3A yalnız ortak zaman havuzunu tanımlar.

Hangi task'ın ne kadar öncelik alacağı:
- `3B — Task categories`,
- `3C — Priority`,
- `3D — Prerequisite`,
- `3G — Explainable planner`

adımlarında kesinleşir.

---

## 7. Remediation veya retention planı otomatik büyütmez

Bağlayıcı davranış:

```text
existing_plan + newly_discovered_remediation != automatically_longer_day
```

Örnek:
- hard budget = 60 dk,
- 25 dk çalışıldı,
- yanlış evidence nedeniyle 15 dk remediation oluştu,
- kalan budget yaklaşık 35 dk.

Planner:
1. kalan task'ları yeniden değerlendirir,
2. remediation'ı aday havuzuna ekler,
3. 3C priority kurallarıyla kalan 35 dakikayı yeniden paketler,
4. gerekirse daha düşük öncelikli new-learning task'ını erteler,
5. günü otomatik 75 dakikaya çıkarmaz.

Kullanıcı isterse açıkça ek süre ekleyebilir.

---

## 8. Replan yalnız kalan bütçeyi değiştirir

Session sırasında capacity değişebilir.

### Kullanıcı `daha az vaktim kaldı` derse

```text
new_remaining_hard_budget = user_declared_remaining_minutes
```

- tamamlanmış task/evidence korunur,
- devam eden task güvenli checkpoint varsa tamamlanabilir veya pause edilir,
- henüz başlamamış task'lar yeniden seçilir,
- başlamamış task'ların silinmesi failure değildir.

### Kullanıcı `daha fazla vaktim var` derse

Planner:
- mevcut completed evidence/state'i yeniden okur,
- yeni ek süre için fresh mini-plan üretir,
- eski ertelenmiş listeyi kör biçimde sıraya eklemez.

---

## 9. Kullanıcı erken bırakırsa ne olur?

Kullanıcı planın ortasında çalışmayı bırakabilir.

Kurallar:
- tamamlanmamış task otomatik wrong/failure evidence değildir,
- yalnız gerçek Attempt/Artifact varsa evidence oluşur,
- `planned_but_not_started` öğrenme açığı değildir,
- `started_but_user_stopped` yalnız session history olayıdır; objective yanlışlığına eşit değildir,
- sonraki gün planner current state'ten yeniden planlar.

Bu davranış missed-day backlog kuralıyla uyumludur.

---

## 10. Task süre maliyeti

Her planner task candidate ileride en az şu süre metadata'sına sahip olmalıdır:

```text
base_estimated_minutes
splittable: true | false
minimum_safe_chunk_minutes? 
estimate_confidence: low | medium | high
```

Planner task seçerken `planning_cost_minutes` kullanır.

### Cold-start

History yoksa authoring'deki `base_estimated_minutes` kullanılır.

### Kişisel hız adaptasyonu

Yeterli gerçek kullanım biriktikçe:
- task type,
- evidence type,
- domain,
- task complexity

bazında kullanıcının gerçek süreleri izlenebilir ve base estimate'e bir `user_pace_factor` uygulanabilir.

Exact pace formula ve minimum sample size implementasyon/kalibrasyon aşamasında belirlenir; 3A yalnız bu capability contract'ını tanımlar.

---

## 11. Active learning time ile wall-clock ayrımı

İleride telemetry mümkünse iki süre ayrılmalıdır:

```text
wall_clock_duration
active_learning_duration
```

Network/AI response bekleme, compiler remote execution bekleme veya app freeze gibi süreler kullanıcının “öğrenme kapasitesini tüketti” diye yanlış yorumlanmamalıdır.

Planner tahmini esas olarak **aktif kullanıcı çalışma süresini** hedefler.

Bu D-028 performans gereksinimiyle de uyumludur.

---

## 12. Task budget'a sığmıyorsa

Bir task kalan capacity'den büyükse planner şu sırayla davranır:

1. Pedagojik olarak güvenli şekilde `splittable` ise uygun checkpoint'e böl.
2. Aynı learning reason'ı karşılayan daha küçük eligible task varsa onu seç.
3. Task bölünemez ve sığmıyorsa bugünkü plandan defer et.
4. Critical task olması bile kullanıcı izni olmadan hard budget'ı aşma yetkisi vermez.
5. Critical dependency nedeniyle ileri new-learning bloke olabilir; ancak kullanıcıya `bugün süre yetmediği için doğrulama tamamlanamadı` diye reason code üretilir.

Assessment/coding artifact gibi evidence bütünlüğü gerektiren işler rastgele ortadan kesilmez.

---

## 13. Overflow = debt değildir

Planlanamayan işler:

```text
deferred_candidate
```

olarak düşünülebilir, fakat `must_do_tomorrow_debt` değildir.

Ertesi gün:
- mastery değişmiş olabilir,
- natural reuse review ihtiyacını kapatmış olabilir,
- başka critical weakness oluşmuş olabilir,
- kullanıcı daha az/çok zaman ayırmış olabilir.

Bu yüzden planner yeniden candidate generation + priority yapar.

Eski task ID'lerini lineer backlog olarak taşımaz.

---

## 14. Capacity nedeniyle hiçbir Skill terk edilmez

Hard prerequisite bir Skill bugüne sığmadıysa:
- Skill `skip edilmiş` sayılmaz,
- curriculum'dan düşmez,
- yalnız bugün için planlanmamış olabilir,
- ona bağımlı branch gerektiğinde bekler,
- bağımsız branch'ler devam edebilir.

Bu, `süre doldu diye kritik temeli atlama` yasağını korur.

---

## 15. Capacity input/output contract

### Input

```text
DailyCapacityInput
- date
- normal_profile_minutes
- short_profile_minutes
- intensive_profile_minutes
- selected_profile?
- scheduled_default_minutes?
- today_explicit_override_minutes?
- session_remaining_override_minutes?
```

### Derived

```text
DailyCapacityState
- source
- hard_budget_minutes
- planning_budget_minutes
- planned_minutes
- completed_active_minutes
- estimated_remaining_minutes
- hard_remaining_minutes
- plan_version
```

### Planner invariant

```text
planned_minutes <= planning_budget_minutes
```

ve user explicit extension yoksa:

```text
expected_total_day_minutes <= hard_budget_minutes
```

Task execution gerçekte tahminden uzun sürebilir; uygulama kullanıcıyı zorla durdurmaz, ancak yeni task başlatmadan önce kalan budget yeniden kontrol edilir.

---

## 16. Replan triggers — capacity tarafı

3A'nın tanımladığı capacity replan eventleri:

```text
TODAY_CAPACITY_CHANGED
SESSION_REMAINING_TIME_CHANGED
TASK_FINISHED_EARLY
TASK_OVERRAN_ESTIMATE
NEW_REMEDIATION_CREATED
NEW_VERIFICATION_DUE_CREATED
USER_STOPPED_SESSION
USER_REQUESTED_EXTRA_TIME
```

Bunların task priority etkisi 3C/3G'de tanımlanacaktır.

---

## 17. Explainability requirement

Planner en az şu capacity reason'larını açıklayabilmelidir:

```text
capacity_source_today_override
capacity_source_short_profile
capacity_source_normal_profile
capacity_source_intensive_profile
task_deferred_not_enough_time
task_split_to_fit_capacity
plan_repacked_after_remediation
plan_repacked_after_time_change
no_penalty_user_stopped
```

UI metni 7A–7G'de tasarlanır.

---

## 18. Determinizm

Aynı:
- curriculum state,
- mastery/retention state,
- candidate task set,
- duration estimates,
- capacity input,
- planner config version

verildiğinde capacity envelope aynı sonucu üretmelidir.

LLM daily capacity miktarını keyfi biçimde değiştiremez.

---

## 19. V0 parametre sınıflandırması

Aşağıdakiler ürün/engineering default'larıdır; bilimsel gerçek değildir:

```text
short_profile_minutes_v0 = 30
normal_profile_minutes_v0 = 60
intensive_profile_minutes_v0 = 90
planning_reserve_ratio_v0 = 0.10
minimum_plannable_block_minutes_v0 = 10
```

Hepsi:
- versioned config,
- kullanıcı ayarı veya pilot data ile değiştirilebilir,
- UI'da bilimsel optimum gibi sunulmaz.

---

## 20. 3A acceptance criteria

3A PASS sayılması için:

1. Kullanıcının explicit günlük süresi planner için hard envelope'dur.
2. Remediation/retention day length'i otomatik uzatamaz.
3. Short/normal/intensive presetler editable config'tir.
4. Minimum micro-session davranışı vardır ve düşük süre ceza değildir.
5. Fixed kategori yüzdesi yoktur.
6. Task estimates ve future personal pace adaptation contract'ı vardır.
7. Task sığmama/split/defer davranışı tanımlıdır.
8. Overflow backlog debt değildir.
9. Kullanıcı süreyi session sırasında artırıp azaltabilir ve remaining plan replan edilir.
10. Unfinished plan mastery failure değildir.
11. Active vs wall-clock süre ayrımı desteklenir.
12. Output 3B–3G için deterministik capacity primitive sağlar.
13. D-028 performance ve Aşama 2 GRE/RVR kararlarıyla çelişmez.

---

# Sonraki alt adımlar

## 3B — Görev kategorileri
Task taxonomy ve task contract kesinleştirilecek.

## 3C — Öncelik puanı
Capacity havuzuna hangi candidate'ın önce gireceği tasarlanacak.

## 3D — Prerequisite davranışı
Hard/soft dependency scheduling kesinleşecek.

## 3E — Hızlı öğrenme
Diagnostic/skip/validated waiver planlama davranışı.

## 3F — Kaçırılan günler
Uzun ara sonrası current-state recovery.

## 3G — Açıklanabilir planner
Reason codes + deterministic selection pseudocode.

## 3H — Planner simülasyonu
Sanal kullanıcılarla scenario suite.
