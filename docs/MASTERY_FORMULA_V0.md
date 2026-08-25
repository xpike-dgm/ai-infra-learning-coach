# Mastery Formula v0 — AI Infra Learning Coach

**Adım:** 2E — Mastery formülü v0  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24  
**Final model:** `GRE-v0 — Gated Recent Evidence`

Bu belge 2A–2D'de tanımlanan öğrenme birimleri, evidence türleri ve AI/ipucu davranışını deterministik, açıklanabilir, cold-start uyumlu ve sonradan kalibre edilebilir bir mastery karar modeline dönüştürür.

Bağlayıcı kaynaklarla birlikte okunur:

- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/2E_RESEARCH_VALIDATION.md`
- `docs/V1_SUCCESS_CRITERIA.md`

Ana ilke:

> **Mastery tek bir yüzde değildir. Son dönemdeki bağımsız ve target-matched direct evidence + Objective'e özgü hard gates + diversity + verification birlikte karar verir.**

İkinci ilke:

> **Assisted performance öğrenme için değerlidir fakat bağımsız mastery'nin yerine geçmez.**

Üçüncü ilke:

> **V0'daki `0.80`, `5 grup` gibi sayılar bilimsel sabit değil, açıkça versionlanan cold-start mühendislik ayarlarıdır.**

---

# 1. Candidate Beta formül neden kaldırıldı?

İlk candidate model şu çekirdeği kullanıyordu:

```text
alpha = 1 + Σ(w_i × q_i)
beta  = 1 + Σ(w_i × (1-q_i))
objective_score = alpha / (alpha + beta)
```

Research AI doğrulaması ve yönetici incelemesi sonrası bu çekirdek finalden çıkarıldı.

Gerekçeler:

- role / assistance / evaluator gibi farklı kavramları tek çarpansal ağırlıkta karıştırıyordu,
- `H1=0.85`, `H2=0.65`, `AI evaluator=0.80` gibi ampirik olarak kalibre edilmemiş katsayılara sahte hassasiyet veriyordu,
- tüm geçmiş sonsuza kadar biriktiği için çok eski evidence yeni evidence'ın etkisini giderek sönümletebiliyordu,
- score yanlışlıkla posterior knowledge probability gibi algılanabilirdi.

Not: Kesirli Beta shape değerleri matematiksel olarak tek başına yasak değildir. Sorun bizim candidate katsayılarımızın kalibre edilmiş bir generative/likelihood modelinden gelmemesi ve score'un psychometric probability olarak savunulamamasıdır.

---

# 2. Final v0 mimarisi — GRE-v0

Canonical pipeline:

```text
Raw Attempt / Artifact
    ↓
Eligibility + prerequisite + contamination validation
    ↓
Assistance / provenance classification
    ↓
Dependency group / testlet aggregation
    ↓
Eligible independent H0 direct evidence groups
    ↓
Recent bounded Objective score
    ↓
Objective hard gates
    ↓
Skill required/critical Objective gates
    ↓
Verification / hysteresis
    ↓
SkillMasteryDecision
    ↓
Prerequisite / Topic state / Planner
```

Invalid evidence hesaplamaya girmez.

---

# 3. Mastery score'a hangi evidence girer?

## 3.1 Eligible mastery evidence

Bir evidence group Objective mastery score'una ancak hepsi sağlanıyorsa girebilir:

1. target `LearningObjective` doğru attribution edilmiş,
2. item/task teknik olarak geçerli ve ambiguous değil,
3. required prerequisite'ler daha önce öğretilmiş/eligible,
4. answer leakage / solution exposure yok,
5. assistance seviyesi **H0**,
6. Objective profile'a göre **direct/primary evidence**,
7. artifact gerekiyorsa provenance target davranışın kullanıcı tarafından üretildiğini gösteriyor,
8. dependency/testlet grouping sonrası bağımsız evidence group olarak kabul edilebilir,
9. evaluator sonucu `verified` durumunda.

## 3.2 Assisted evidence — H1–H4

H1–H4:

- learning history'de tutulur,
- misconception / remediation / hint dependence / task selection için kullanılabilir,
- fresh independent recheck tetikleyebilir,
- fakat **positive independent mastery score'una girmez**.

Bu bir ceza değildir. Yardımın amacı öğrenmeyi ilerletmektir; mastery sorusu ise `yardımsız yapabiliyor mu?` sorusudur.

H3/H4 solution exposure sonrası 2D'deki fresh/unseen independent recheck zorunluluğu korunur.

## 3.3 Corroborating evidence

Corroborating evidence:

- diagnostic confidence,
- remediation seçimi,
- explanation/debugging yönü,
- verification scheduling

için kullanılabilir; ancak direct evidence eksikliğini puan biriktirerek telafi edemez.

Final v0'da `corroborating = 0.50` mastery multiplier'ı yoktur.

## 3.4 Contextual signals

Time, completion, streak, self-confidence, hint count gibi contextual sinyaller mastery score üretmez.

---

# 4. Problem family / Testlet / Local-dependence guard

Aynı exact soru veya çok yakın varyantların independent evidence gibi sayılmasını engellemek için iki metadata seviyesi kullanılır:

```text
variant_family_id
dependency_group_id / testlet_id
```

Kurallar:

1. Aynı `dependency_group_id/testlet_id` içindeki correlated alt item'lar **tek evidence group** üretir.
2. Exact solution-exposed item yeni positive independent group oluşturamaz.
3. Aynı session'da yakın varyantlar bağımsız group sayısını şişiremez.
4. `variant_family_id` diversity gate için kullanılır.
5. Yalnız sayı/değişken adı değiştirilmiş kopya yeni family/context değildir.
6. İleri Topic'te gerçek farklı bağlamda aynı Skill'in doğal kullanımı yeni family/context olabilir.

Bir testlet'in group-level kalite sonucu:

```text
group_quality q_g ∈ [0,1]
```

olarak objective-specific rubric ile hesaplanır.

---

# 5. Recent bounded Objective score

V0, sonsuz tüm-history accumulation kullanmaz.

Her Objective için zaman sırasına göre son en fazla `M` eligible independent H0 direct evidence group tutulur.

```text
RECENT_WINDOW_MAX_GROUPS_V0 = 5
```

Bu sayı **engineering heuristic**'tir ve 18C'de kalibre edilir.

Objective score:

```text
W_o = Objective için son en fazla 5 eligible independent direct evidence group

recent_direct_score(o) = mean(q_g for g in W_o)
```

Equal weighting kullanılır. V0'da:

- assistance multiplier yok,
- evidence-role multiplier yok,
- evaluator multiplier yok,
- difficulty multiplier yok,
- recency multiplier yok.

Recency etkisi bounded window seçimiyle sağlanır. Time-based forgetting/decay 2F'ye aittir.

## Neden basit mean?

Cold-start'ta kalibre edilmemiş katsayılar uydurmak yerine her eligible bağımsız direct group eşit oy hakkına sahiptir. Karmaşıklık hard gates ve Objective profile'da tutulur.

---

# 6. Operational score threshold

```text
OBJECTIVE_MASTERY_THRESHOLD_V0 = 0.80
```

Bu değer:

- V1 cold-start için engineering heuristic,
- BKT `P(L)` değildir,
- IRT ability probability değildir,
- `kullanıcı %80 öğrendi` anlamına gelmez,
- UI'da mastery yüzdesi olarak sunulmaz.

UI ayrık state gösterebilir:

- Geliştiriliyor
- Doğrulama Bekliyor
- Yetkin
- Zayıflıyor / Pekiştirme Gerekli

Threshold 18C pilotunda false-positive / false-negative sonuçlarına göre değişebilir.

---

# 7. Objective profile

Her Objective authoring sırasında en az şu davranış metadata'sına sahip olabilir:

```text
required: true | false
criticality: standard | critical
acceptable_evidence_types
direct_evidence_types
required_direct_type
min_independent_groups
min_variant_families
requires_non_basic_evidence
requires_transfer
requires_user_authored_artifact
allowed_tools_policy
```

Defaults aşağıdadır; Objective gereği farklı değer gerekiyorsa açık authoring gerekçesi tutulur.

---

# 8. Standard required Objective gate

V0 default PASS için hepsi gerekir:

1. `recent_direct_score >= 0.80`
2. en az **2 eligible independent H0 direct evidence group**
3. default en az **2 anlamlı variant family/context**
4. Objective'in `required_direct_type` şartı varsa karşılanmış
5. unresolved `requires_independent_recheck` yok
6. unresolved `verification_due` yok
7. evidence prerequisite-valid
8. evaluator status score'a giren gruplar için `verified`

Çok atomik bir Objective için iki family üretmek anlamsızsa `min_variant_families` authoring sırasında gerekçeli olarak düşürülebilir; bu global varsayılan değildir.

---

# 9. Critical Objective gate

Standard şartlara ek olarak:

1. en az **3 eligible independent H0 direct evidence group**
2. en az **2 farklı problem family/context**
3. yalnız basic/easy evidence'dan oluşmama
4. Objective `coding/production` ise en az bir gerçek **H0 user-authored coding artifact**
5. Objective `debugging/diagnosis` ise en az bir bağımsız H0 diagnosis/fix evidence
6. Objective transfer gerektiriyorsa unseen transfer evidence
7. allowed-tools policy ihlali yok

Critical Objective'in başka recognition/corroborating başarılarla matematiksel olarak telafi edilmesi mümkün değildir.

---

# 10. Coding / production özel kuralı

Objective:

> `Pointer üzerinden bir int değeri değiştiren kısa C kodunu kendisi yazabilir.`

Mastery için:

- doğru kod seçeneğini işaretlemek yeterli değil,
- kod çıktısı tahmini yeterli değil,
- AI'nın yazdığı kodu çalıştırmak yeterli değil,
- explanation tek başına production yerine geçmez.

En az bir H0 user-authored coding artifact şarttır; kritik Objective ise diğer diversity/recent-score gate'leri de geçer.

Compiler/test runner artifact'ı doğrulayabilir; fakat compiler sonucu yalnız hedef davranış gerçekten test ediliyorsa `verified` production group üretir.

---

# 11. Debugging özel kuralı

Debugging direct evidence en az target objective'in rubric'ine göre şu davranışlardan gerekli olanlarını gözlemlemelidir:

- semptomu tanıma,
- nedeni/bug bölgesini izole etme,
- uygun fix üretme,
- gerektiğinde nedeni açıklama.

Yalnız `hangi satır hatalı?` MCQ'su full debugging mastery değildir.

Critical debugging Objective en az bir bağımsız H0 debugging evidence ister.

---

# 12. AI evaluator güvenilirlik modeli

İlk candidate'taki sabit:

```text
AI evaluator high confidence = 0.80
```

**kaldırılmıştır.**

Evaluator sonucu numeric confidence multiplier yerine şu operasyonel statülerden biriyle tutulur:

```text
verified
provisional
invalid
```

## `verified`

Örnekler:

- prevalidated deterministic answer key,
- compiler + objective-specific tests,
- deterministic rubric check,
- ileride benchmark sonucu izin verilen iyi kalibre edilmiş evaluator policy.

## `provisional`

- yalnız LLM rubric değerlendirmesi,
- ambiguity ihtimali,
- partial/open-ended cevapta güvenilir cross-check yok.

`provisional` evidence critical mastery gate'ini tek başına karşılayamaz.

Sistem fresh farklı-modality check, deterministic check veya ileride 14F'de kalibre edilmiş evaluator policy isteyebilir.

## `invalid`

- evaluator/system failure,
- ambiguous item,
- wrong rubric/answer key,
- attribution yapılamıyor.

Score'a girmez.

14F'de gerçek benchmark ile LLM evaluator policy yeniden ele alınacaktır.

---

# 13. Difficulty davranışı

Difficulty numeric score multiplier değildir.

V0 labels:

```text
basic
authentic/application
transfer/integration
```

veya Aşama 4'te kesinleşecek eşdeğer sınıflar.

Difficulty şu işlerde kullanılır:

- item eligibility,
- Objective hard gate,
- critical Objective'in yalnız basic evidence ile geçmesini engelleme,
- planner/question selection,
- transfer diversity.

Gerçek item data oluşursa ileride IRT/Elo/başka calibration ile difficulty estimate eklenebilir.

---

# 14. Skill mastery aggregation

Skill mastery **yüksek average ile kritik eksikleri kapatan compensatory score** kullanmaz.

Canonical karar:

```text
skill_mastered =
    all required Objectives PASS
    AND all critical Objectives PASS
    AND no unresolved critical recheck
```

`skill_recent_score` analytics/UI-internal özet olarak required Objective recent scores'tan derived edilebilir; fakat mastery gate değildir.

Bu sayede bir Objective'teki çok yüksek performans başka required Objective açığını gizleyemez.

---

# 15. Prerequisite-ready gate

2E sonunda başlangıç kuralı:

```text
prerequisite_ready =
    skill_mastered
    AND no_unresolved_critical_recheck
```

2F retention/forgetting geldiğinde buna retention state/risk koşulları eklenecektir.

LLM doğrudan `prerequisite_ready=true` yazamaz.

---

# 16. Negative evidence ve hysteresis

## 16.1 Mastery öncesi

Eligible H0 direct negative/partial evidence recent window'a normal `q_g` değeriyle girer.

Assisted H1–H4 failure mastery score'a doğrudan negative yazılmaz; misconception/remediation sinyali olabilir ve bağımsız recheck tetikleyebilir.

## 16.2 Mastered Objective/Skill sonrası tek contradiction

İlk temiz, prerequisite-valid, bağımsız H0 direct anlamlı failure:

```text
verification_due = true
```

Mastery anında silinmez.

Fresh/unseen H0 doğrulama planlanır.

### Recheck positive

- recent window güncellenir,
- `verification_due` temizlenebilir,
- mastered korunur.

### Recheck de negative

- confirmed weakness vardır,
- current recent window/gates tekrar değerlendirilir,
- sonuç 2B/2F davranışına göre `weakening` veya `remediation_required` üretebilir.

Bu model slip/measurement-noise ile gerçek zayıflığı ayırmaya çalışır.

---

# 17. Support band — istatistiksel confidence değildir

UI/engine için explainable support band tutulabilir.

## LOW

- Objective minimum independent group gate'i eksik veya
- direct evidence eksik veya
- evaluator verification eksik.

## MEDIUM

- standard Objective default gate'leri için yeterli bağımsız direct support var.

## HIGH

- critical default support şartları,
- 3+ independent group,
- 2+ family/context,
- gerekli non-basic/production/debugging/transfer evidence,
- unresolved recheck yok.

Bu `95% confidence` gibi istatistiksel güven aralığı değildir.

---

# 18. False-positive guardrails

## Tek kolay quiz doğru

- direct Objective gate/diversity/min-group yok
- **NOT MASTERED**

## Aynı soru 10 kez doğru

- dependency/family guard
- bağımsız evidence sayısı şişmez
- **NOT MASTERED**

## H2 ile beş doğru

- assisted formative evidence
- mastery recent score'a girmez
- **NOT MASTERED** bağımsız H0 kanıt yoksa

## AI tüm kodu yazdı ve test geçti

- production provenance kullanıcıya ait değil
- coding gate karşılanmaz
- **NOT MASTERED**

## Theory güçlü, coding yok

- required production Objective gate eksik
- **NOT MASTERED**

## Bilinmeyen prerequisite nedeniyle yanlış

- invalid/contaminated for target Objective
- kullanıcı cezalandırılmaz

---

# 19. False-negative guardrails

- Tek clean hata instant mastery reset yapmaz.
- Prerequisite contamination negative evidence olmaz.
- Assisted practice başarısızlığı bağımsız mastery skoruna otomatik ceza değildir.
- Objective-specific authoring, anlamsız family/difficulty gate'lerinin gereksiz uygulanmasını engeller.
- AI evaluator ambiguity durumunda kullanıcı doğrudan başarısız sayılmaz; `provisional` + recheck kullanılır.
- Kritik olmayan Objective'lerde unnecessary over-gating pilotta ölçülür.

---

# 20. Explainability contract

Her karar en az şu trace'i üretmelidir:

```text
MasteryDecisionTrace
- skill_id
- mastery_formula_version
- objective_recent_scores
- window_evidence_group_ids
- dependency/testlet aggregation
- passed_gates
- failed_gates
- excluded_evidence_ids + reasons
- assisted/formative evidence summary
- unresolved_rechecks
- evaluator_status_summary
- decision
- reason_codes
```

Kullanıcıya sade açıklama örneği:

> “Pointer mantığında ilerledin; fakat farklı bir soruda yardım almadan kod yazabildiğine dair yeterli bağımsız kanıt henüz yok.”

---

# 21. Performans dostu implementation contract

D-028 gereği her ekran açılışında tüm Attempt history taranmaz.

Objective için sufficient-state/cache tutulabilir:

```text
recent_eligible_groups[<=5]
recent_direct_score
independent_group_count
variant_family_count
required_direct_flags
verification_due
support_band
formula_version
```

Yeni evidence geldiğinde küçük bounded yapı güncellenir.

Correction/invalidation durumunda güvenli targeted recalculation yolu bulunur.

Kesin DB schema 9C, implementation 12A'da.

---

# 22. Versioned v0 config

```text
mastery_formula_version = "GRE-v0"
objective_mastery_threshold = 0.80
recent_window_max_groups = 5
standard_min_independent_groups = 2
standard_default_min_variant_families = 2
critical_min_independent_groups = 3
critical_min_variant_families = 2
assisted_positive_mastery_score = false
corroborating_can_replace_direct = false
ai_evaluator_fixed_weight = none
single_clean_post_mastery_failure = verification_due
```

Bu parametrelerin `0.80`, `5`, `2`, `3` gibi numeric bölümleri calibration candidate'larıdır.

---

# 23. Pilot calibration — 18C

Loglanacak ve analiz edilecek ana çıktılar:

- false-positive mastery: mastered sonrası fresh H0 transfer/production failure,
- false-negative mastery: kullanıcı tekrar tekrar bağımsız başarılı iken gate nedeniyle gereksiz bekleme,
- average attempts before mastery,
- hint dependence before independent success,
- problem-family diversity etkisi,
- mastered prerequisite sonrası dependent Skill öğrenme performansı,
- retention recheck başarısı,
- remediation frequency,
- LLM evaluator ile deterministic/fresh cross-check uyuşmazlığı,
- over-practice.

`0.80`, window `5` ve min-group/family defaultları bu verilerle değişebilir.

Tek kullanıcı V1'de gerçek population psychometric calibration yapılamayacağı açıkça kabul edilir; amaç güvenli, explainable cold-start motorudur.

---

# 24. 2E'de bilinçli olarak yapılmayanlar

2F veya sonraya bırakılanlar:

- half-life / time decay,
- spaced repetition interval'leri,
- retention risk threshold,
- mastered → weakening time trigger,
- recency dışında explicit time weighting,
- fitted PFA/R-PFA coefficients,
- BKT parameter fitting,
- IRT item calibration,
- Elo difficulty fitting,
- LLM evaluator benchmark/calibration,
- assessment composition,
- exact DB schema.

---

# 25. 2E kabul kriterleri

2E tamamlandı çünkü:

- ayrı Research AI raporu alındı ve otomatik kabul edilmeden değerlendirildi,
- candidate Beta-style accumulator finalden çıkarıldı,
- sabit assistance ve AI evaluator numeric weights kaldırıldı,
- mastery score yalnız eligible H0 direct evidence'a bağlandı,
- same-family/dependency için testlet grouping tanımlandı,
- bounded recent window ile saturation riski sınırlandı,
- v0 recent score formülü tanımlandı,
- `0.80` ve `M=5` açıkça engineering heuristic olarak versionlandı,
- standard/critical Objective hard gates tanımlandı,
- coding/debugging bağımsız artifact şartları korundu,
- Skill aggregation non-compensatory gate olarak sadeleştirildi,
- single-error hysteresis/verification_due korundu,
- AI evaluator numeric trust yerine verified/provisional/invalid modeli getirildi,
- difficulty numeric multiplier olmaktan çıkarılmış halde korundu,
- explainability ve performance-friendly bounded state tanımlandı,
- 2F sınırı açık bırakıldı.

---

# 26. Sıradaki adım

## 2F — Unutma modeli

2F için yeni PRE-STEP GitHub refresh ve ayrı Research AI turu yapılacaktır.

Araştırılacak/tasarlanacak:

- spaced repetition model yaklaşımı,
- review interval başlangıcı ve büyümesi,
- successful/failed delayed retrieval,
- retention risk / time decay,
- `mastered → weakening → mastered/remediation_required`,
- natural reuse'un retention evidence sayılması,
- GRE-v0 recent mastery state ile retention state'in birlikte çalışması.
