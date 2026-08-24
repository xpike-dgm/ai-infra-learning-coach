# Adaptive Planner Spec — AŞAMA 3

**Durum:** TAMAMLANDI / CANONICAL CORE SPEC  
**Tarih:** 2026-08-24  
**Tamamlanan kapsam:** `3A–3H`

Bu belge Aşama 3'ün çekirdek capacity contract'ını taşır. Diğer Aşama 3 alt-spec'leri:
- `docs/TASK_TAXONOMY_SPEC.md` — 3B
- `docs/PRIORITY_POLICY_SPEC.md` — 3C
- `docs/PREREQUISITE_POLICY_SPEC.md` — 3D
- `docs/DIAGNOSTIC_WAIVER_SPEC.md` — 3E
- `docs/MISSED_DAY_RECOVERY_SPEC.md` — 3F
- `docs/PLANNER_EXPLAINABILITY_SPEC.md` — 3G
- `docs/PLANNER_SIMULATION_SUITE.md` — 3H

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

Task seçimi 3B–3G contract'larıyla çözülür.

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
3. PBR-v0 priority ile kalan zamanı yeniden paketler,
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
Authoring'deki `base_estimated_minutes` kullanılır.

### Kişisel hız adaptasyonu
Yeterli gerçek kullanım biriktikçe task type/evidence/domain/complexity bazında user pace factor uygulanabilir. Exact formula ve sample-size implementasyon/kalibrasyon aşamasında belirlenir.

---

## 11. Active learning time ile wall-clock ayrımı

İleride telemetry mümkünse:

```text
wall_clock_duration
active_learning_duration
```

ayrılır. Network/AI/compiler bekleme süreleri kullanıcının öğrenme kapasitesini yanlış tüketmiş sayılmamalıdır.

---

## 12. Task budget'a sığmıyorsa

Sıra:
1. güvenli split,
2. aynı LearningNeed için daha küçük eligible alternative,
3. defer,
4. critical olması user izni olmadan hard budget aşımı vermez.

Assessment/coding artifact gibi evidence bütünlüğü gerektiren işler rastgele ortadan kesilmez.

---

## 13. Overflow = debt değildir

Planlanamayan candidate ertesi gün `must_do_tomorrow_debt` olmaz. Need açıksa current state'ten fresh candidate yeniden üretilir.

---

## 14. Capacity nedeniyle hiçbir Skill terk edilmez

Hard prerequisite bugüne sığmadıysa:
- Skill skip edilmiş sayılmaz,
- curriculum'dan düşmez,
- dependent branch gerektiğinde bekler,
- independent branch'ler devam edebilir.

---

## 15. Capacity input/output contract

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

Invariant:
```text
planned_minutes <= planning_budget_minutes
expected_total_day_minutes <= hard_budget_minutes  # explicit extension yoksa
```

---

## 16. Replan triggers — capacity tarafı

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

---

## 17. Explainability requirement

Planner en az capacity source, not-enough-time defer, split, remediation/time-change replan ve no-penalty stop nedenlerini structured reason code ile açıklayabilmelidir. Canonical reason taxonomy `docs/PLANNER_EXPLAINABILITY_SPEC.md` içindedir.

---

## 18. Determinizm

Aynı curriculum state, mastery/retention state, candidate set, duration estimates, capacity input ve planner config version aynı capacity envelope ve semantik seçim sonucunu üretmelidir.

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

Hepsi versioned/configurable ve pilot data ile kalibre edilebilir.

---

## 20. 3A acceptance criteria

3A PASS:
1. explicit süre hard envelope,
2. remediation/retention auto-overrun yok,
3. editable presets,
4. micro-session no-penalty,
5. fixed category percentage yok,
6. duration estimate/personal pace contract,
7. split/smaller/defer,
8. no backlog debt,
9. session time replan,
10. unfinished plan failure değil,
11. active vs wall-clock ayrımı,
12. deterministic primitive,
13. D-028 + GRE/RVR uyumu.

---

# AŞAMA 3 kapanış durumu

Aşama 3 alt-spec'leri tamamlandı ve `docs/PLANNER_SIMULATION_SUITE.md` ile policy-level doğrulandı:

```text
3A ✅ Capacity — D-033
3B ✅ Task taxonomy — D-034
3C ✅ PBR-v0 — D-035
3D ✅ PRG-v0 — D-036
3E ✅ VDW-v0 — D-037
3F ✅ SRR-v0 — D-038
3G ✅ PDT-v0 — D-039
3H ✅ Simulation suite

16 / 16 scenarios PASS
20 / 20 invariants PASS
0 critical cross-spec contradiction
```

Bu PASS production runtime testi değildir. Planner implementation sanal kullanıcı testleri 11F'te; gerçek cihaz/performance doğrulaması 17E'de ayrıca yapılacaktır.

**Sonraki canonical adım:** `4A — Günlük mikro değerlendirme`.