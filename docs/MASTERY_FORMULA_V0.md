# Mastery Formula v0 — AI Infra Learning Coach

**Adım:** 2E — Mastery formülü v0  
**Durum:** CANDIDATE / RESEARCH AI DOĞRULAMASI BEKLİYOR  
**Tarih:** 2026-08-24

> **Düzeltme notu — 2026-08-24:** Bu dosyanın ilk sürümü ana yöneticinin kendi web/dış araştırmasıyla hazırlanmıştır. Proje iş akışında 2E için ayrıca Research AI kullanılması kararlaştırılmış olmasına rağmen ayrı Research AI turu yapılmadan adım yanlışlıkla tamamlandı olarak işaretlenmiştir. Bu nedenle 2E yeniden açılmıştır. Aşağıdaki model candidate v0'dır; Research AI raporu değerlendirilip gerekli revizyonlar yapılmadan bağlayıcı final 2E kararı sayılmaz.

Bu belge 2A–2D'de tanımlanan öğrenme birimleri, evidence türleri ve AI/ipucu davranışını açıklanabilir, deterministik ve ileride kalibre edilebilir bir mastery karar modeline dönüştürmek için hazırlanmış candidate tasarımdır.

Bu belge şu kaynaklarla birlikte değerlendirilmelidir:

- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/V1_SUCCESS_CRITERIA.md`

Ana ilke:

> **Mastery tek bir yüzde değildir. Bir `MasteryEvidenceScore` + zorunlu evidence gate'leri + confidence/verification kuralları birlikte karar vermelidir.**

İkinci ilke:

> **v0 sayıları bilimsel sabit değil, açıkça versionlanan başlangıç kalibrasyon adaylarıdır. Research AI doğrulaması ve daha sonra pilot verisi olmadan kalıcı gerçek kabul edilmez.**

---

# 1. Neden yalnız weighted average kullanmıyoruz?

Yalnız `doğru sayısı / soru sayısı` veya tek weighted average şu hatalara açıktır:

- 10 kolay recognition sorusu gerçek coding mastery gibi görünebilir,
- aynı soru familyası tekrar tekrar çözülerek puan şişebilir,
- AI tarafından üretilmiş kod doğru çalıştığı için kullanıcı mastered sanılabilir,
- bir production objective yalnız explanation ile geçebilir,
- bir tek çok yüksek puan kritik prerequisite'i açabilir.

Bu nedenle candidate 2E iki ayrı katman kullanır:

1. **Score katmanı:** evidence'ın genel yönünü ve gücünü toplar.
2. **Gate katmanı:** objective'in gerçekten gerekli davranışlarla, bağımsız ve çeşitli kanıtla doğrulandığını kontrol eder.

Score yüksek olsa bile gate eksikse mastery verilmez.

---

# 2. İlk yönetici araştırması ve candidate model seçimi

Ana yöneticinin yaptığı ilk dış/web araştırmasında üç ana yaklaşım karşılaştırıldı:

## 2.1 Mastery Learning

Bloom'un mastery yaklaşımı açık hedefler, formative assessment, feedback/corrective ve yeniden doğrulamayı öne çıkarır. Farklı uygulamalarda farklı criterion değerleri kullanılmıştır; tek evrensel mastery yüzdesi yoktur.

## 2.2 Bayesian Knowledge Tracing (BKT)

BKT, kullanıcı Skill bilgisini latent bir learned/unlearned state olarak takip eden açıklanabilir klasik bir knowledge tracing yaklaşımıdır. Uygulamalarda örneğin `0.95` gibi mastery probability threshold'ları kullanılabilir; ancak threshold bağlama/model kalibrasyonuna bağlıdır.

Bu ürünün şu anki durumunda BKT'yi doğrudan canonical motor yapmak için yeterli kalibre edilmiş öğrenci/item datası yoktur. Ayrıca bizim evidence modelimiz binary quiz'den daha geniştir: coding, debugging, explanation, transfer, AI assistance, provenance ve project evidence birlikte kullanılır.

## 2.3 Item Response Theory (IRT)

IRT item difficulty/discrimination gibi özellikleri veriyle modelleyebilir. Bu nedenle `hard soru = otomatik 1.3x puan` gibi keyfi bir multiplier psychometric calibration varmış gibi sahte hassasiyet yaratabilir.

### Candidate 2E kararı

V1 için araştırma doğrulamasına sunulan aday yaklaşım:

- açıklanabilir,
- local/deterministic,
- az veriyle çalışabilir,
- farklı evidence türlerini destekleyebilir,
- daha sonra BKT/IRT veya başka modelle karşılaştırılabilir

bir **Beta-style weighted evidence accumulator + hard mastery gates** modelidir.

Bu score **kalibre edilmiş “öğrenmiş olma olasılığı” değildir**. UI'da `84% ihtimalle biliyorsun` gibi sunulmaz.

Bu bölümdeki yaklaşım ayrı Research AI tarafından kaynaklı biçimde doğrulanacaktır.

---

# 3. Mastery hesaplama pipeline'ı

Candidate canonical akış:

```text
Raw Attempt / Artifact
    ↓
Eligibility & contamination validation
    ↓
Evidence classification / deduplication
    ↓
Outcome quality q_i ∈ [0,1]
    ↓
Effective evidence weight w_i
    ↓
Objective MasteryEvidenceScore
    ↓
Objective mastery gates
    ↓
Skill aggregation + Skill gates
    ↓
SkillMasteryDecision
    ↓
Prerequisite / Topic state / Planner
```

Invalid evidence score hesabına girmez.

Contextual signal score üretmez.

---

# 4. Evidence eligibility — formülden önce

Bir event formüle girmeden önce:

- target Skill/Objective doğru mu,
- required prerequisite'ler kullanıcı tarafından daha önce öğrenilmiş mi,
- item teknik olarak geçerli/ambiguous değil mi,
- answer leakage/solution exposure var mı,
- evaluator/provenance kabul edilebilir mi,
- artifact origin target Objective için anlamlı mı,
- aynı evidence'ın duplicate/correlated kopyası mı

kontrol edilir.

`invalid` veya target Skill'e güvenilir biçimde attribution yapılamayan evidence mastery score'a eklenmez.

---

# 5. Evidence outcome değeri `q_i`

Her kullanılabilir evidence için hedef Objective'e yönelik başarı kalitesi:

```text
q_i ∈ [0,1]
```

olarak normalize edilir.

- tamamen doğru / rubric tam karşılandı → `1.0`
- tamamen yanlış → `0.0`
- partial/mixed → rubric'in gerçekten karşılanan oranı

Partial için kör biçimde her zaman `0.5` kullanılmaz. Mümkün olduğunda objective-specific rubric sonucu kullanılır.

---

# 6. Effective evidence weight `w_i`

Candidate v0'da difficulty'ye numeric multiplier verilmez. Weight evidence'ın **target Objective'e ne kadar doğrudan bağlı olduğu**, **yardım seviyesi** ve **evaluator güveni** üzerinden oluşturulur.

```text
w_i = role_weight × assistance_weight × provenance_weight
```

`w_i` maksimum `1.0` olarak tutulur.

## 6.1 Evidence role weight — candidate

| Evidence role | Weight |
|---|---:|
| `direct / primary` | `1.00` |
| `corroborating` | `0.50` |
| `contextual` | `0.00` |

## 6.2 Assistance weight — candidate

| Yardım | Candidate v0 weight | Not |
|---|---:|---|
| `H0` | `1.00` | bağımsız |
| `H1` | `0.85` | hafif orientation |
| `H2` | `0.65` | targeted conceptual hint |
| `H3` | `0.35` veya `0` | target davranış kullanıcıda kalmışsa; aksi halde practice-only |
| `H4` | `0.00` | full solution exposure |

Ek kurallar:

- `practice_only` → positive mastery weight `0`.
- `requires_independent_recheck` → recheck tamamlanana kadar target mastery gate'ini karşılayamaz.
- `generated_or_copied` artifact, production/coding Objective'i için direct positive weight `0`.
- `mixed_authorship` target davranışın önemli bölümünü AI yaptıysa direct production gate'ini karşılayamaz.

**Bu katsayılar özellikle Research AI tarafından sorgulanacak candidate değerlerdir.**

## 6.3 Evaluator / provenance weight — candidate

| Kaynak | Candidate v0 weight |
|---|---:|
| deterministic compiler/test + doğru target attribution | `1.00` |
| prevalidated item + deterministic answer key | `1.00` |
| açık rubric ile güvenilir değerlendirme | `1.00` |
| AI evaluator + açık rubric + high confidence | `0.80` |
| AI evaluator low confidence / ambiguous | `0.00` ve recheck |
| invalid/hatalı item | `0.00` |

Bu değerler de 4E/13F yanında Research AI değerlendirmesine tabidir.

---

# 7. Difficulty score multiplier değildir — candidate

Candidate v0'da `easy/basic`, `medium`, `hard/transfer` etiketleri score'u doğrudan çarpmaz.

Difficulty şu işlerde kullanılır:

- item eligibility,
- critical mastery gate,
- soru seçimi,
- transfer/diversity doğrulaması.

Kritik Skill'in bütün evidence'ı yalnız basic item'lardan oluşamaz.

---

# 8. Duplicate / same-family inflation guard

1. Aynı `evidence_group_id` içindeki correlated kayıtlar tek bağımsız kanıt olarak görülür.
2. Exact aynı item solution-exposed ise yeni positive mastery evidence olarak kullanılmaz.
3. Aynı assessment/session içinde aynı `variant_family` tekrarları independent group sayısını artırmaz.
4. Farklı session'da aynı family tekrar kullanılabilir; diversity gate için hâlâ aynı family sayılır.
5. Yalnız sayı/değişken adı değişen kopyalar gerçek transfer sayılmaz.

---

# 9. Objective MasteryEvidenceScore — candidate

```text
alpha = 1 + Σ(w_i × q_i)
beta  = 1 + Σ(w_i × (1 - q_i))
objective_score = alpha / (alpha + beta)
```

Bu internal score probability of knowledge olarak yorumlanmaz.

---

# 10. Candidate mastery threshold

```text
MASTERY_SCORE_THRESHOLD_V0 = 0.80
```

Bu değer:

- yalnız başlangıç karar sınırı adayıdır,
- bilimsel sabit değildir,
- `kullanıcı %80 öğrendi` anlamına gelmez,
- tek başına mastery kararı vermez,
- Research AI raporu sonrasında değişebilir,
- pilot 17C'de tekrar kalibre edilebilir.

---

# 11. Confidence — candidate support bands

## LOW
- effective evidence mass `< 2.0`, veya
- iki independent evidence group yok, veya
- gerekli direct evidence yok.

## MEDIUM
- effective mass `>= 2.0`,
- en az 2 independent group,
- required direct evidence mevcut.

## HIGH
- effective mass `>= 3.0`,
- en az 3 independent group,
- en az 2 anlamlı variant/problem family veya context,
- required direct independent evidence mevcut,
- unresolved solution-exposure recheck yok.

Bu eşikler de candidate kalibrasyondur.

---

# 12. Objective mastery gate — candidate

Her Objective authoring profile'ında en az:

```text
required: true | false
criticality: standard | critical
acceptable_evidence_types
direct_evidence_types
required_direct_type (varsa)
requires_independent_evidence
requires_non_basic_evidence
requires_transfer (varsa)
```

## Standard required Objective candidate gate

1. `objective_score >= 0.80`
2. en az bir target-matched direct independent evidence
3. en az 2 independent evidence group
4. gerekli evidence type varsa karşılanmış
5. unresolved `requires_independent_recheck` yok
6. evidence prerequisite-valid

## Critical Objective candidate gate

Standard gate'lere ek olarak:

1. confidence `HIGH`
2. en az 3 independent evidence group
3. en az 2 problem/variant family veya farklı context
4. yalnız basic/easy evidence'dan oluşmama
5. production/coding ise en az bir gerçek `H0 user_authored` target production artifact
6. debugging ise en az bir bağımsız debugging/diagnosis evidence
7. transfer gerekiyorsa unseen transfer evidence

---

# 13. Objective archetype örnekleri

Concept/recall objective için recall/explanation direct; recognition/code reading corroborating olabilir.

Coding/production objective için coding direct olmalı ve candidate critical gate en az bir H0 user-authored artifact ister.

Debugging objective için diagnosis + fix direct evidence olabilir.

---

# 14. Skill aggregation — candidate

```text
skill_score = arithmetic_mean(required_objective_scores)
```

Skill mastered candidate şartları:

1. tüm `required` Objective'ler PASS,
2. tüm `critical` Objective'ler critical gate PASS,
3. `skill_score >= 0.80`,
4. unresolved independent recheck yok,
5. invalid/contaminated evidence üzerine kurulmuş gate yok.

Candidate sonuç:

```text
SkillMasteryDecision = mastered | not_mastered | verification_due
```

---

# 15. Prerequisite-ready gate — candidate

```text
prerequisite_ready = skill_mastered AND no_unresolved_critical_recheck
```

2F retention modeli sonrası güncellenecektir.

---

# 16. Negative evidence / hysteresis — candidate

Daha önce mastered Skill tek bir yeni yanlış yüzünden anında unmastered olmaz.

İlk clean independent direct negative evidence candidate davranışı:

```text
mastery_state = mastered
verification_due = true
```

Fresh doğrulama da negative ise weakness/remediation değerlendirilir.

Bu davranış da Research AI tarafından false-negative/false-positive riski açısından değerlendirilecektir.

---

# 17. Explainability contract

```text
MasteryDecisionTrace
- skill_id
- formula_version
- skill_score
- objective_scores
- confidence_band(s)
- passed_gates
- failed_gates
- effective_evidence_ids
- excluded_evidence_ids + reasons
- unresolved_rechecks
- decision
- reason_codes
```

---

# 18. Incremental / performans dostu hesaplama

D-028 gereği Objective bazında versioned aggregate/sufficient-state tutulabilmelidir:

```text
alpha
beta
effective_mass
independent_group_count
variant_family_count
required_direct_flags
verification_due
formula_version
```

Kesin DB/schema 8C, implementation 11A'dadır.

---

# 19. Versioning ve calibration

Candidate config:

```text
mastery_formula_version = "v0-candidate"
score_threshold = 0.80
role_weight_direct = 1.00
role_weight_corroborating = 0.50
assistance_H0 = 1.00
assistance_H1 = 0.85
assistance_H2 = 0.65
assistance_H3 = 0.35
assistance_H4 = 0.00
ai_evaluator_high_confidence = 0.80
```

Bu değerler Research AI doğrulaması tamamlanana kadar bağlayıcı değildir.

---

# 20. Research AI doğrulama kapısı

2E ancak ayrı Research AI raporu alındıktan ve ana yönetici tarafından değerlendirildikten sonra kapanabilir.

Research AI en az şunları incelemelidir:

- Beta-style accumulator seçiminin pedagojik/istatistiksel artı ve eksileri,
- BKT/IRT/AFM/PFA/elo/heuristic gate modelleriyle karşılaştırma,
- `0.80` threshold candidate değerinin olası false-positive/negative etkileri,
- direct/corroborating `1.0/0.5` ayrımının gerekçelendirilip gerekçelendirilemeyeceği,
- H0–H4 `1/.85/.65/.35/0` katsayılarının evidence-based olup olmadığı ve daha güvenli alternatif,
- minimum evidence group / family diversity gate'lerinin mantığı,
- critical production için H0 artifact şartı,
- negative evidence + verification_due/hysteresis davranışı,
- AI evaluator provenance weight yaklaşımı,
- az kullanıcı datasıyla V1 için hangi modelin en güvenli olduğu,
- hangi değerlerin sabit değil configurable/calibrated tutulması gerektiği,
- pilot sırasında hangi metriklerin izlenmesi gerektiği.

Raporda mümkün olduğunca birincil/akademik kaynak, kaynak tarihi, DOI/URL, bulgu, sınırlılık ve güven seviyesi istenmelidir.

---

# 21. 2E tamamlanma kapısı — ŞU AN KARŞILANMADI

2E şu anda **tamamlanmış değildir**.

Kapanması için:

1. ayrı Research AI raporu alınmalı,
2. rapor mevcut 2A–2D bağlayıcı kararlarla karşılaştırılmalı,
3. candidate formül gerekirse revize edilmeli,
4. kalıcı karar kaydı güncellenmeli,
5. acceptance/false-positive senaryoları tekrar kontrol edilmeli,
6. POST-STEP GitHub + MASTER_PLAN sync yapılmalı,
7. ancak sonra 2F aktif yapılmalıdır.

---

# 22. Sıradaki işlem

**2E Research AI doğrulaması.**

2F'ye henüz geçilmez.