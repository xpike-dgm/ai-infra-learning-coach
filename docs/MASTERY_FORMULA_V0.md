# Mastery Formula v0 — AI Infra Learning Coach

**Adım:** 2E — Mastery formülü v0  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24

Bu belge 2A–2D'de tanımlanan öğrenme birimleri, evidence türleri ve AI/ipucu davranışını açıklanabilir, deterministik ve ileride kalibre edilebilir bir mastery karar modeline dönüştürür.

Bu belge şu kaynaklarla birlikte bağlayıcıdır:

- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/V1_SUCCESS_CRITERIA.md`

Ana ilke:

> **Mastery tek bir yüzde değildir. Bir `MasteryEvidenceScore` + zorunlu evidence gate'leri + confidence/verification kuralları birlikte karar verir.**

İkinci ilke:

> **v0 sayıları bilimsel sabit değil, açıkça versionlanan başlangıç kalibrasyonudur. Pilot verisiyle ölçülmeden kalıcı gerçek kabul edilmez.**

---

# 1. Neden yalnız weighted average kullanmıyoruz?

Yalnız `doğru sayısı / soru sayısı` veya tek weighted average şu hatalara açıktır:

- 10 kolay recognition sorusu gerçek coding mastery gibi görünebilir,
- aynı soru familyası tekrar tekrar çözülerek puan şişebilir,
- AI tarafından üretilmiş kod doğru çalıştığı için kullanıcı mastered sanılabilir,
- bir production objective yalnız explanation ile geçebilir,
- bir tek çok yüksek puan kritik prerequisite'i açabilir.

Bu nedenle 2E iki ayrı katman kullanır:

1. **Score katmanı:** evidence'ın genel yönünü ve gücünü toplar.
2. **Gate katmanı:** objective'in gerçekten gerekli davranışlarla, bağımsız ve çeşitli kanıtla doğrulandığını kontrol eder.

Score yüksek olsa bile gate eksikse mastery verilmez.

---

# 2. Araştırma sonucu ve model seçimi

2E için yapılan araştırmada üç ana yaklaşım karşılaştırıldı:

## 2.1 Mastery Learning

Bloom'un mastery yaklaşımı açık hedefler, formative assessment, feedback/corrective ve yeniden doğrulamayı öne çıkarır. Farklı uygulamalarda farklı criterion değerleri kullanılmıştır; tek evrensel mastery yüzdesi yoktur.

## 2.2 Bayesian Knowledge Tracing (BKT)

BKT, kullanıcı Skill bilgisini latent bir learned/unlearned state olarak takip eden açıklanabilir klasik bir knowledge tracing yaklaşımıdır. Uygulamalarda örneğin `0.95` gibi mastery probability threshold'ları kullanılmıştır; ancak threshold bağlama/model kalibrasyonuna bağlıdır. 2025 EDM çalışması belirli bir bağlamda daha yüksek `0.98` eşiğinin sonraki performansla daha güçlü ilişki gösterdiğini raporlamıştır.

Bu ürünün şu anki durumunda BKT'yi doğrudan canonical motor yapmak için yeterli kalibre edilmiş öğrenci/item datası yoktur. Ayrıca bizim evidence modelimiz binary quiz'den daha geniştir: coding, debugging, explanation, transfer, AI assistance, provenance ve project evidence birlikte kullanılır.

## 2.3 Item Response Theory (IRT)

IRT item difficulty/discrimination gibi özellikleri veriyle modelleyebilir. Bu nedenle `hard soru = otomatik 1.3x puan` gibi keyfi bir multiplier kullanmak psychometric calibration varmış gibi sahte hassasiyet yaratır.

### 2E kararı

V1 için:

- açıklanabilir,
- local/deterministic,
- az veriyle çalışabilir,
- farklı evidence türlerini destekleyebilir,
- daha sonra BKT/IRT veya başka modelle karşılaştırılabilir

bir **Beta-style weighted evidence accumulator + hard mastery gates** kullanılacaktır.

Bu score **kalibre edilmiş “öğrenmiş olma olasılığı” değildir**. UI'da `84% ihtimalle biliyorsun` gibi sunulmaz.

Araştırma referansları:

- Bloom, B. S. (1968), *Learning for Mastery* — ERIC ED053419.
- Corbett & Anderson (1994) ile başlayan Bayesian Knowledge Tracing yaklaşımı; genel survey: ACM Computing Surveys, DOI `10.1145/3569576`.
- Zhang et al. (2025), *How Much Mastery is Enough Mastery?*, Educational Data Mining 2025.
- OECD PISA item-response-theory açıklamaları — item difficulty/discrimination calibration yaklaşımı.
- Learning/performance ayrımı ve delayed retention/transfer literatürü 2C'deki research notlarıyla birlikte dikkate alınmıştır.

---

# 3. Mastery hesaplama pipeline'ı

Canonical akış:

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

Örnek:

Bir debugging task'inde:

- bug doğru bulundu,
- neden kısmen doğru açıklandı,
- fix yanlış

ise rubric farklı alt maddelerden gerçek `q_i` üretir.

---

# 6. Effective evidence weight `w_i`

v0'da difficulty'ye sahte bilimsel multiplier verilmez. Weight yalnız evidence'ın **target Objective'e ne kadar doğrudan bağlı olduğu**, **yardım seviyesi** ve **evaluator güveni** üzerinden oluşturulur.

```text
w_i = role_weight × assistance_weight × provenance_weight
```

`w_i` maksimum `1.0` olarak tutulur.

## 6.1 Evidence role weight

| Evidence role | Weight |
|---|---:|
| `direct / primary` | `1.00` |
| `corroborating` | `0.50` |
| `contextual` | `0.00` |

Bir evidence türünün direct/corroborating olması Learning Objective profile'ına göre belirlenir.

Örneğin `kod yazabilir` objective'i için coding direct, code-reading corroborating'dir.

## 6.2 Assistance weight

| Yardım | v0 weight | Not |
|---|---:|---|
| `H0` | `1.00` | bağımsız |
| `H1` | `0.85` | hafif orientation |
| `H2` | `0.65` | targeted conceptual hint |
| `H3` | `0.35` veya `0` | yalnız target davranış hâlâ anlamlı biçimde kullanıcı tarafından üretildiyse; aksi halde practice-only |
| `H4` | `0.00` | full solution exposure |

Ek kurallar:

- `practice_only` → positive mastery weight `0`.
- `requires_independent_recheck` → recheck tamamlanana kadar target mastery gate'ini karşılayamaz.
- `generated_or_copied` artifact, production/coding Objective'i için direct positive weight `0`.
- `mixed_authorship` target davranışın önemli bölümünü AI yaptıysa direct production gate'ini karşılayamaz.

Bu değerler **v0 calibration constants**'tır; bilimsel evrensel oran değildir.

## 6.3 Evaluator / provenance weight

| Kaynak | v0 weight |
|---|---:|
| deterministic compiler/test + doğru target attribution | `1.00` |
| prevalidated item + deterministic answer key | `1.00` |
| açık rubric ile güvenilir değerlendirme | `1.00` |
| AI evaluator + açık rubric + high confidence | `0.80` |
| AI evaluator low confidence / ambiguous | `0.00` ve recheck |
| invalid/hatalı item | `0.00` |

AI evaluator weight'i 4E/13F'de gerçek validation sonuçlarıyla yeniden kalibre edilebilir.

---

# 7. Difficulty score multiplier değildir

V0'da:

- `easy/basic`,
- `medium`,
- `hard/transfer`

etiketleri score'u doğrudan çarpmaz.

Difficulty şu işlerde kullanılır:

- item eligibility,
- critical mastery gate,
- soru seçimi,
- transfer/diversity doğrulaması.

Kritik Skill'in bütün evidence'ı yalnız basic item'lardan oluşamaz.

Bu karar ileride gerçek item performance datası oluştuğunda IRT-benzeri difficulty calibration eklenmesine engel değildir.

---

# 8. Duplicate / same-family inflation guard

Mastery score aynı sorunun varyasyonlarıyla şişirilemez.

V0 kuralları:

1. Aynı `evidence_group_id` içindeki correlated kayıtlar tek bağımsız kanıt olarak görülür.
2. Exact aynı item solution-exposed ise yeni positive mastery evidence olarak kullanılmaz.
3. Aynı assessment/session içinde aynı `variant_family`'nin tekrarları bağımsız evidence group sayısını artırmaz; score için yalnız en anlamlı/temsilci sonuç kullanılır.
4. Farklı session'da aynı family tekrar kullanılabilir; fakat **diversity gate** için hâlâ aynı family olarak sayılır.
5. Gerçek transfer için yalnız sayı/değişken adı değişen kopyalar yeni problem yapısı sayılmaz.

2F delayed retention sonrası aynı family'nin zaman ayrımlı tekrarının nasıl yorumlanacağını ayrıca tanımlayacaktır.

---

# 9. Objective MasteryEvidenceScore

V0 score, weak symmetric prior kullanan Beta-style accumulator'dır:

```text
alpha = 1 + Σ(w_i × q_i)
beta  = 1 + Σ(w_i × (1 - q_i))

objective_score = alpha / (alpha + beta)
```

Özellikleri:

- hiç evidence yoksa `0.50`, fakat bu mastery değildir; `insufficient evidence`'dır,
- tek bir perfect direct independent evidence → `0.667`,
- iki perfect direct independent evidence → `0.750`,
- üç perfect direct independent evidence → `0.800`.

Dolayısıyla tek kolay doğru cevap score katmanında bile mastery threshold'a ulaşamaz.

Bu değer **probability of knowledge** olarak yorumlanmaz. Sadece versioned internal `MasteryEvidenceScore`'dur.

---

# 10. V0 mastery threshold

Operational başlangıç threshold:

```text
MASTERY_SCORE_THRESHOLD_V0 = 0.80
```

Bu sayı şu şekilde yorumlanır:

- ürün için başlangıç karar sınırıdır,
- BKT'deki `P(L)=0.80` anlamına gelmez,
- `kullanıcı %80 öğrendi` anlamına gelmez,
- tek başına mastery kararı vermez,
- 17C pilot kalibrasyonunda false-positive / false-negative sonuçlarına göre değişebilir.

Threshold değişirse `mastery_formula_version` ile versionlanmalıdır.

---

# 11. Confidence — score'dan ayrı

V0 confidence istatistiksel posterior confidence diye sunulmaz. Evidence miktarı/bağımsızlığı için açıklanabilir **support band** olarak tutulur.

## LOW

Aşağıdakilerden biri:

- effective evidence mass `< 2.0`,
- iki bağımsız evidence group yok,
- gerekli direct evidence henüz yok.

## MEDIUM

- effective mass `>= 2.0`,
- en az 2 bağımsız evidence group,
- Objective profile'ın required direct evidence'ı mevcut.

## HIGH

- effective mass `>= 3.0`,
- en az 3 bağımsız evidence group,
- en az 2 anlamlı variant/problem family veya eşdeğer context diversity,
- gerekli direct independent evidence mevcut,
- unresolved solution-exposure recheck yok.

`score = 0.90` fakat confidence LOW ise mastered verilmez.

---

# 12. Objective mastery gate

Her Objective'in authoring profile'ında en az şunlar tanımlanabilir:

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

## 12.1 Standard required Objective

Varsayılan PASS için hepsi gerekir:

1. `objective_score >= 0.80`
2. en az bir target-matched **direct independent** evidence
3. en az 2 independent evidence group
4. Objective için gerekli evidence type varsa karşılanmış
5. unresolved `requires_independent_recheck` yok
6. evidence prerequisite-valid

## 12.2 Critical Objective

Standard gate'lere ek olarak:

1. confidence `HIGH`
2. en az 3 independent evidence group
3. en az 2 problem/variant family veya eşdeğer farklı context
4. yalnız basic/easy evidence'dan oluşmama
5. Objective production/coding ise en az bir gerçek `H0 user_authored` target production artifact
6. Objective debugging ise en az bir bağımsız debugging/diagnosis evidence
7. Objective transfer gerektiriyorsa unseen transfer evidence

Critical Skill'i yalnız recognition sorularıyla mastered etmek mümkün değildir.

---

# 13. Objective archetype örnekleri

## Concept / recall Objective

Örnek:

`& operatörünün address ürettiğini kendi cümlesiyle açıklayabilir.`

Direct:

- concept recall,
- explanation.

Corroborating:

- recognition,
- code reading.

Recognition tek başına gate'i geçiremez.

## Coding / production Objective

Örnek:

`Pointer üzerinden x değerini değiştiren kodu kendisi yazabilir.`

Direct:

- coding/production.

Gate:

- en az bir H0 user-authored coding artifact,
- kritikse farklı problem family/context,
- AI-generated code direct gate'i karşılayamaz.

## Debugging Objective

Direct:

- diagnosis + fix,
- objective rubric'e göre neden açıklaması.

Yalnız bug satırını seçeneklerden seçmek debugging production gate'i değildir.

---

# 14. Skill aggregation

Skill mastery score, Skill'in `required` Objective'lerinden derived edilir.

V0 varsayılanı:

```text
skill_score = arithmetic_mean(required_objective_scores)
```

Optional Objective'ler `mastered` gate'ini bloke etmez.

Critical Objective'e yalnız daha yüksek average weight vermek yerine **hard gate** uygulanır. Böylece başka Objective'lerdeki yüksek score kritik bir eksikliği matematiksel olarak gizleyemez.

## Skill mastered şartı

Hepsi gerekir:

1. tüm `required` Objective'ler PASS,
2. tüm `critical` Objective'ler critical gate PASS,
3. `skill_score >= 0.80`,
4. unresolved independent recheck yok,
5. invalid/contaminated evidence üzerine kurulmuş gate yok.

Sonuç:

```text
SkillMasteryDecision = mastered | not_mastered | verification_due
```

`Topic mastered` kararı bundan sonra 2B state machine'deki coverage + Skill mastery gate ile türetilir.

---

# 15. Prerequisite-ready gate

2E'nin başlangıç kuralı:

```text
prerequisite_ready =
    skill_mastered
    AND no_unresolved_critical_recheck
```

2F forgetting/retention modeli geldiğinde `weakening`, due review ve retention riskinin prerequisite-ready davranışına etkisi ayrıca eklenecektir.

Bir AI/LLM doğrudan `prerequisite_ready=true` yazamaz.

---

# 16. Negative evidence

Negative evidence `q_i = 0` olarak aynı accumulator'a girer ve score'u düşürür.

Ancak önemli invariant:

> **Daha önce mastered olmuş Skill tek bir yeni yanlış yüzünden anında unmastered olmaz.**

## Mastered Skill sonrası contradiction rule

İlk temiz, bağımsız ve direct negative evidence:

```text
mastery_state = mastered
verification_due = true
```

olarak yorumlanır.

Sonra fresh/unseen doğrulama gerekir.

- doğrulama positive → `verification_due` temizlenebilir,
- doğrulama da negative → confirmed weakness oluşur; Skill mastery gate yeniden değerlendirilir ve 2B/2F kurallarına göre `weakening` veya `remediation_required` tetiklenebilir.

H3/H4 practice-only başarısızlıkları score'u doğrudan düşürmek yerine misconception/remediation history'sinde kullanılabilir.

Bu hysteresis kullanıcıyı tek hata nedeniyle cezalandırmaz fakat gerçek tekrar eden eksikliği de gizlemez.

---

# 17. Assistance ile mastery örneği

Pointer production Objective'i için:

1. H0, direct coding, q=1.0 → `w=1.0`
2. H0, direct medium coding, q=1.0 → `w=1.0`
3. H1, direct coding, q=1.0 → `w=0.85`

```text
alpha = 1 + 2.85 = 3.85
beta  = 1
score = 3.85 / 4.85 ≈ 0.794
```

Score henüz threshold altında.

Sonra fresh farklı context'te H0 direct coding:

```text
alpha = 4.85
beta  = 1
score ≈ 0.829
```

Eğer:

- en az 3 independent group,
- 2 problem/context family,
- non-basic evidence,
- H0 user-authored direct coding

gate'leri de sağlanmışsa critical Objective mastery PASS olabilir.

Bu örnek yardımın işe yaradığını fakat independent evidence'ın yerini tamamen alamadığını gösterir.

---

# 18. False-positive guardrail senaryoları

## Tek kolay quiz doğru

- score yaklaşık `0.667`
- direct/critical diversity gate yok
- **NOT MASTERED**

## Aynı soru 10 kez doğru

- dedup / same-family guard nedeniyle 10 bağımsız evidence oluşmaz
- **NOT MASTERED**

## AI bütün kodu yazdı ve test geçti

- production weight `0`
- independent coding gate yok
- **NOT MASTERED**

## Beş H2 assisted doğru cevap

Score yükselebilir; fakat required independent direct gate eksikse:

- **NOT MASTERED**

## Theory çok güçlü, coding hiç yok

Production Objective required ise:

- coding gate eksik
- **NOT MASTERED**

## Bir mastered Objective'de tek yeni yanlış

- `verification_due`
- anında mastery reset yok

## Bilinmeyen prerequisite içeren soru yanlış

- invalid/contaminated evidence
- score'a negatif yazılmaz

---

# 19. Explainability contract

Her mastery kararı şu bilgiyi üretebilmelidir:

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

UI kullanıcıya bütün matematiği göstermek zorunda değildir.

Sade örnek:

> “Pointer mantığını iyi biliyorsun fakat bu beceriyi farklı bir soruda bağımsız kodlayabildiğine dair yeterli kanıt henüz yok.”

---

# 20. Incremental / performans dostu hesaplama

D-028 gereği mastery hesabı her ekranda bütün history'yi tekrar taramak zorunda değildir.

Implementation aşamasında Objective bazında versioned aggregate/sufficient-state tutulabilir:

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

Yeni evidence geldiğinde çoğu durumda incremental update yapılabilir.

Evidence invalidation/correction olduğunda güvenli recalculation yolu bulunur.

Bu sayede mastery engine local/deterministic kalırken UI thread üzerinde ağır full-history scan zorunlu olmaz.

Kesin DB/schema 8C, implementation 11A'dadır.

---

# 21. Versioning ve calibration

Config en az şu şekilde versionlanmalıdır:

```text
mastery_formula_version = "v0"
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

Threshold veya multiplier değişikliği sessizce eski kullanıcı history'sini farklı yorumlamamalıdır. Migration/recalculation stratejisi implementation sırasında açıkça versionlanacaktır.

## Pilot calibration — 17C

Özellikle ölçülecekler:

- false-positive mastery: sistem mastered diyor ama fresh transfer/coding başarısız,
- false-negative mastery: kullanıcı tekrar tekrar bağımsız başarılı ama sistem gereksiz yere bekletiyor,
- excessive practice,
- AI-assisted false mastery,
- critical prerequisite bypass,
- remediation frequency.

V0 sayıları bu verilere göre değişebilir.

---

# 22. 2E'de bilinçli olarak yapılmayanlar

Aşağıdakiler 2F veya sonraki adımlara bırakılmıştır:

- time-based decay,
- exact spaced repetition interval,
- mastered Skill'in gün/hafta/ay geçtikçe nasıl weakening olacağı,
- retention evidence'ın zaman fonksiyonu,
- item difficulty'nin gerçek veriyle psychometric calibration'ı,
- IRT/BKT model fitting,
- assessment composition yüzdeleri,
- AI evaluator benchmark/calibration,
- exact DB schema.

---

# 23. 2E kabul kriterleri

2E tamamlanmış kabul edilir çünkü:

- Objective-level deterministic score formülü tanımlandı,
- score ile hard mastery gate ayrıldı,
- single-easy-quiz false positive önlendi,
- direct/corroborating evidence numeric ayrımı yapıldı,
- H0–H4 assistance v0 katsayıları tanımlandı,
- generated/copied code production mastery'den çıkarıldı,
- duplicate/same-family inflation kuralları tanımlandı,
- difficulty'ye sahte multiplier verilmedi; critical difficulty gate'e taşındı,
- Objective standard/critical mastery gate'leri tanımlandı,
- Objective → Skill aggregation tanımlandı,
- confidence support band tanımlandı,
- prerequisite-ready başlangıç gate'i tanımlandı,
- tek yanlış sonrası hysteresis / verification_due davranışı tanımlandı,
- explainability trace sözleşmesi tanımlandı,
- incremental/performance-friendly hesaplama yönü tanımlandı,
- bütün numeric constants v0 calibration olarak versionlanıp pilotta değişebilir hale getirildi.

---

# 24. Sıradaki adım

## 2F — Unutma modeli

2F'de araştırma + tasarımla:

- spaced repetition başlangıç interval'leri,
- successful retrieval sonrası interval büyümesi,
- delayed failure sonrası review sıklaştırma,
- mastery/retention riskinin zamana göre davranışı,
- doğal reuse'un review yerine sayılması,
- `mastered → weakening → mastered/remediation_required` transition mantığı,
- 2E score ile retention state'in nasıl birlikte kullanılacağı

kesinleştirilecektir.

2F başlamadan `PROJECT_MEMORY_PROTOCOL.md` gereği yeni PRE-STEP GitHub refresh yapılmalıdır.