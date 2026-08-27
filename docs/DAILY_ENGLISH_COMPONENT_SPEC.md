# Daily English Component Spec — DECP-v0

**Adım:** 7C — Günlük English bileşeni  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-27  
**Final model:** `DECP-v0 — Daily English Component Policy`  
**Final decision:** `D-065`

Bu belge Technical English paralel hattının günlük planner içinde **hangi koşullarda aday görev üreteceğini, ortak capacity içinde nasıl yarışacağını, hangi task mix'in state'e göre seçileceğini ve English'in düzenli kalırken debt/streak/fixed-quota sistemine dönüşmemesini** tanımlar.

Ana ilke:

> **Daily English = her gün zorunlu dakika veya completion değildir. Eligible/open English learning need varsa günlük planner'da görünür bir English fırsatı oluşturmak; seçim ve süreyi mevcut evidence, prerequisite, retention, priority ve capacity state'ine bırakmaktır.**

İkinci ilke:

> **English ayrı planner değildir. `technical_english` track, mevcut `LearningNeed → TaskCandidate → PBR-v0 → PRG-v0 → capacity → Attempt/Artifact → Evidence` pipeline'ını kullanır.**

---

# 1. Bağlayıcı girdiler

DECP-v0 şu accepted contract'ları tüketir; onları değiştirmez:

- `docs/ADAPTIVE_PLANNER_SPEC.md` — common daily hard budget
- `docs/TASK_TAXONOMY_SPEC.md` — LearningNeed / TaskCandidate / Evidence ayrımı
- `docs/PRIORITY_POLICY_SPEC.md` — PBR-v0
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0
- `docs/MISSED_DAY_RECOVERY_SPEC.md` — absence/debt guard
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md` — EED-v0 / D-063
- `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md` — TECP-v0 / D-064
- `curriculum/english/7a_entry_diagnostic/blueprint.yaml`
- `curriculum/english/7b_cefr_progression/alignment.yaml`
- `research/7c_daily_english_component_research.md`

7C:
- yeni English Skill/Objective yaratmaz,
- CEFR anchor'larını değiştirmez,
- yeni mastery threshold/score icat etmez,
- günlük capacity'yi artırmaz,
- English için fixed yüzde veya fixed dakika kotası yaratmaz,
- English'i technical curriculum'a global hard gate yapmaz.

---

# 2. “Daily” semantiği

## 2.1 Active study day

7C açısından `active_study_day`:

```text
resolved_daily_capacity_minutes > 0
AND planner bugün için fresh plan üretiyor
```

Kullanıcının çalışmadığı gün English miss/failure değildir.

## 2.2 Daily opportunity invariant

Eğer active study day'de:

- en az bir open Technical English LearningNeed varsa,
- need prerequisite/eligibility açısından interpretable ise,
- en az bir güvenli TaskCandidate üretilebiliyorsa,

planner **en az bir English TaskCandidate** üretir.

Bu yalnız candidate-generation invariant'ıdır:

```text
candidate_generated != task_selected
selected != attempted
attempted != mastered
```

P0/P1 integrity/repair work, capacity veya duration-fit nedeniyle English candidate bugün seçilmeyebilir.

## 2.3 No daily obligation

Aşağıdakiler canonical değildir:

```text
must_complete_english_every_day = false
english_streak_gate = false
missed_english_day_is_failure = false
missed_english_day_is_debt = false
fixed_daily_english_minutes = null
fixed_daily_english_percentage = null
```

---

# 3. Common capacity contract

English task'leri `ADAPTIVE_PLANNER_SPEC` içindeki aynı hard budget'ı tüketir.

```text
english_budget != separate_budget
english_minutes + technical_minutes <= common_hard_budget
```

Planner:
- English ihtiyacı ortaya çıktı diye günü otomatik uzatmaz,
- technical task'lerin kalan süresini kör şekilde korumaz,
- bütün LearningNeed havuzunu yeniden değerlendirir.

## 3.1 Çok kısa capacity

`minimum_plannable_block_minutes_v0` altındaki günlerde mevcut 3A davranışı korunur.

Uygun 3–8 dakikalık English retrieval / tiny remediation task varsa önerilebilir. Yoksa English yapılmaması failure değildir.

## 3.2 10+ dakika

Bu bir English minimumu değildir. Yalnız normal planner contract'ının çalışabildiği genel capacity sınırıdır.

Eligible English need varsa candidate oluşturulur; selection PBR-v0 + duration-fit ile yapılır.

---

# 4. English LearningNeed kaynakları

Technical English günlük component yeni ihtiyaç enum'u icat etmez. Mevcut trigger kind'ları kullanır:

```text
new_learning
continue_learning
weakness_detected
remediation_required
retention_review_due
verification_due
diagnostic_opportunity
reinforcement_opportunity
parallel_track_due
```

`integration_opportunity` 7D technical integration ownership'inde esaslaşır.

## 4.1 `parallel_track_due`

Aşağıdaki koşullardan biri varsa oluşturulabilir:

- current D01 progression frontier'da eligible unmastered Skill vardır,
- daha önce başlanmış English Skill'de `continue_learning` vardır,
- hiçbir daha güçlü English-specific need yokken paralel hattın normal ilerlemesi gereklidir.

`parallel_track_due` default P3 `planned_progress` bandındadır.

## 4.2 Stronger needs win

Aynı English Skill için:

```text
verification_due / remediation_required
> retention_review_due
> continue/new/parallel progression
> optional reinforcement
```

Bu exact numeric score değildir; PBR-v0 semantic bands uygulanır.

---

# 5. Track-balance / starvation davranışı

English'in paralel kalması için ayrı fixed quota yerine mevcut PBR-v0 `track_balance_pressure` + `starvation_pressure` kullanılır.

## 5.1 Omission ne zaman balance sinyali üretir?

Yalnız şu durumda:

- active study day,
- English need eligible,
- güvenli candidate mevcut,
- candidate yalnız capacity/priority nedeniyle seçilmedi.

Şunlar balance miss sayılmaz:

- kullanıcı o gün hiç çalışmadı,
- English prerequisite unresolved,
- trusted resource/evaluator yok,
- candidate güvenli süreye sığmıyor ve splittable değil,
- English scope'ta açık need yok.

## 5.2 Semantic pressure buckets

Yeni gün sayısı threshold'u icat edilmez. Existing PBR vocabulary kullanılır:

```text
none
watch
promote
```

- `none`: balance sorunu yok.
- `watch`: eligible English tekrar dışarıda kalıyor; aynı band içinde rank yükselir.
- `promote`: starvation guard tetiklenmiş; P3 parallel-track need gerekirse P2 `maintain_or_continue` davranışına çıkabilir.

Bu promotion:
- P0/P1 integrity work'ü geçmez,
- ineligible task'i eligible yapmaz,
- hard budget'ı aşmaz,
- mastery/evidence üretmez.

## 5.3 Balance ne zaman rahatlar?

English need gerçek bir `Attempt/Artifact` ile çalışıldığında veya açık English need artık kalmadığında scheduling pressure azaltılabilir.

Task selection veya UI'da task görünmesi tek başına mastery değildir.

---

# 6. State-driven daily task mix

7C sabit kategori yüzdeleri kullanmaz.

Forbidden:

```text
20% vocabulary
20% grammar
20% reading
20% writing
20% review
```

Canonical seçim current state'e bağlıdır.

## 6.1 New learning

Yeni capability açılıyorsa varsayılan progression:

```text
explanation / model
→ constrained recognition/comprehension
→ retrieval/application checkpoint
→ controlled production if target permits
→ later independent/transfer evidence
```

Tek oturumda bütün zincirin tamamlanması zorunlu değildir.

Research-backed preferred pattern:
- uzun passive exposition yerine learning boyunca kısa retrieval/application checkpoint'leri,
- fixed item count yok.

## 6.2 Continue learning

Aynı Skill üzerinde önceki context korunabiliyorsa:
- unresolved Objective/evidence gap hedeflenir,
- eski task ID kör biçimde replay edilmez,
- mümkünse farklı item/context family seçilir.

## 6.3 Retention

`retention_review_due` varsa:
- RVR-v0 due state canonicaldır,
- review öncesi answer/model exposure verilmez,
- target Skill'e uygun direct retrieval/production seçilir,
- critical production capability recognition-only review ile retention PASS alamaz.

7C yeni spacing interval'i üretmez.

## 6.4 Remediation / verification

WLRM/GRE/RVR sonucu `verification_due` veya `remediation_required` varsa:
- exact Objective/Skill hedeflenir,
- broad English reset yoktur,
- correction/explanation gerekirse ardından fresh variant gelir,
- aynı hatalı item'i ezberleyerek closure yoktur.

## 6.5 Production

Production task ancak gereken English hard prerequisite'leri hazırsa seçilir.

Unknown grammar/vocabulary eksikliği nedeniyle target production Skill'e clean negative evidence yazılamaz.

## 6.6 Reinforcement / B2+

TECP-v0 B2+ extension:
- base Skill reclassification değildir,
- yalnız extension-eligible existing Skill'lerde,
- existing semantic boundary içinde,
- base progression veya daha urgent retention/remediation need'ini gereksiz yere bloke etmeden
aday olabilir.

B2+ synthetic completion state 7C'de de yaratılmaz.

---

# 7. Activity-family rotation

7A'daki 15 canonical diagnostic task family production content değildir; fakat target/evidence family sınırlarını gösterir.

Daily English authoring/selection şu guard'ları korur:

1. Aynı exact prompt tekrar tekrar kullanılmaz.
2. Mümkünse ardışık evidence aynı dependency/testlet family'ye yığılmaz.
3. Receptive Skill için yalnız kolay recognition ile uzun süre dönülmez; capability scope izin veriyorsa farklı context/response depth kullanılır.
4. Production Skill'de model/solution exposure sonrası çıkan cevap H0 independent evidence sayılmaz.
5. Transfer/retention için fresh context/family tercih edilir.

Bu rotation fixed round-robin değildir; evidence need'e göre seçilir.

---

# 8. Feedback davranışı

Feedback practice/remediation için faydalı olabilir fakat evidence attribution ayrıdır.

## 8.1 Feedback timing

- `assess`, `retain`, `diagnose`, independent production sırasında target response capture edilmeden answer-revealing feedback verilmez.
- Attempt kaydedildikten sonra corrective feedback verilebilir.
- Practice/teach task'lerinde daha erken scaffold kullanılabilir; assistance level kaydedilir.

## 8.2 Corrective feedback

Feedback mümkün olduğunca exact observed issue'ya bağlanır:
- vocabulary/meaning,
- grammar/function word,
- instruction interpretation,
- constraint/sequence comprehension,
- production clarity/accuracy.

Broad `English bad` feedback yasaktır.

## 8.3 Assisted revision

```text
feedback_assisted_revision != independent_mastery_evidence
```

Revision learning/practice evidence olabilir; mastery closure gerekiyorsa fresh H0/direct/verified attempt gerekir.

---

# 9. Scaffold davranışı — 7C sınırı

7C yalnız **English-track içindeki** task support davranışını tanımlar.

- recognition → controlled → independent progression korunur,
- scaffold evidence'a göre azaltılır, takvime göre değil,
- `follow_bilingual_technical_instruction` kendi canonical capability scope'unda bilingual olabilir,
- diğer task'lerde target English construct'ünü answer-revealing biçimde scaffold etmek evidence ceiling yaratır.

Technical Python/C/Linux/CUDA task'ına ne kadar Türkçe/bilingual destek ekleneceği **7D** kapsamındadır.

---

# 10. Daily English plan trace

Planner explainability için selected veya omitted English need en az şu semantik trace'i taşıyabilir:

```text
DailyEnglishDecisionTrace
- english_need_key
- target_skill_ids
- target_objective_ids
- trigger_kind
- eligibility_state
- priority_band
- track_balance_pressure
- starvation_pressure
- candidate_task_family
- estimated_minutes
- selection_result
- selection_reason
- evidence_goal
- assistance_ceiling
```

Allowed `selection_result`:

```text
selected
omitted_higher_priority
omitted_capacity
omitted_no_safe_candidate
ineligible_prerequisite
not_due_or_no_open_need
user_deferred
```

Omission reason mastery state değildir.

---

# 11. Daily session behavior examples

## Example A — normal progression day

State:
- English frontier A1 Skill eligible,
- no urgent English weakness,
- technical work also available.

Result:
- `parallel_track_due` P3 English candidate üretilir,
- common budget içinde technical work ile yarışır,
- seçilirse teach/practice + kısa retrieval checkpoint kullanılabilir.

## Example B — urgent technical blocker

State:
- critical technical prerequisite P0,
- English P3 due,
- capacity dar.

Result:
- English candidate üretilebilir fakat seçilmeyebilir,
- omission failure/debt değildir,
- eligible omission scheduling balance signaline girebilir.

## Example C — English remediation

State:
- clean independent English failure sonrası remediation required.

Result:
- exact Skill/Objective P1 repair candidate,
- generic vocabulary drill'e domain reset yok,
- corrective feedback + fresh variant,
- closure normal GRE/WLRM standardıyla.

## Example D — retention due

State:
- mastered English Skill RVR `review_due`.

Result:
- review candidate passive restudy yerine retrieval/production target'lar,
- PASS/FALL outcome RVR/GRE üzerinden işlenir.

## Example E — 6 dakika capacity

State:
- uygun tiny English retrieval mevcut.

Result:
- micro-task önerilebilir.
- Uygun task yoksa planner hiçbir English task seçmeyebilir; failure yok.

---

# 12. 7C acceptance requirements

7C ancak şunlar sağlanırsa tamamlanabilir:

1. 7A/7B canonical 15 D01 Skill seti değişmeden kalmalı.
2. English separate budget yaratılmamalı; common hard capacity korunmalı.
3. Fixed English minutes/percentage/daily completion/streak gate yasak olmalı.
4. Eligible/open English need için active-study-day candidate-generation invariant tanımlı olmalı.
5. Omission failure/debt olmamalı.
6. PBR-v0 track-balance/starvation semantics reuse edilmeli; fixed missed-day threshold icat edilmemeli.
7. Task mix state-driven olmalı; new/continue/retention/remediation/production/reinforcement ayrımı mevcut trigger/purpose contract'ını kullanmalı.
8. RVR-v0 spacing ownership korunmalı; yeni scientific-looking interval formula eklenmemeli.
9. Recognition→controlled→independent/transfer progression ve unknown-English-prerequisite guard korunmalı.
10. Feedback-assisted revision independent mastery evidence sayılmamalı.
11. B2+ TECP-v0 semantic sınırını aşmamalı.
12. Technical-task bilingual integration 7D'ye, learner-facing English mastery/CEFR behavior 7E'ye bırakılmalı.
13. Stage 6 + 7A + 7B regression ve independent 7C validator PASS olmalı.
14. D-050 living-memory + external-memory + repo-wide stale-reference audit PASS olmalı.

---

# 13. Future-stage boundaries

7C şu işleri yapmaz:

- technical curriculum task'larına English/bilingual scaffold entegrasyonu → **7D**,
- learner-facing English mastery / CEFR summary / completion semantics → **7E**,
- gerçek lesson/item body'leri → **15F / 15G**,
- empirical cadence/interval/item-duration calibration → **18**,
- full professional English capability expansion → **20 / GNS-KGC review**.

---

# 14. Candidate final decision

`DECP-v0 / D-065` kabul edilirse:

> Technical English her active study day'de eligible/open need olduğunda candidate fırsatı üretir; fakat common capacity ve PBR priority altında çalışır. Daily completion, fixed minute/percentage, streak, debt veya failure yaratılmaz. Repeated eligible omission mevcut track-balance/starvation semantics ile scheduling pressure üretir. Task mix exact Skill/Objective state, retention/remediation/evidence ihtiyacına göre dinamik seçilir; spacing RVR-v0'a, technical integration 7D'ye, learner-facing mastery 7E'ye bırakılır.
