# Diagnostic / Fast-Path & Coverage Waiver Policy — VDW-v0

**Adım:** 3E — Hızlı öğrenme  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24  
**Final model:** `VDW-v0 — Validated Diagnostic Waiver`

Bu belge, kullanıcının bir Topic/Skill'i uygulama içinde sıfırdan tekrar öğrenmeden önce zaten bildiğini güvenilir biçimde gösterebilmesini ve yalnız gerçekten kanıtlanan öğretim parçalarını atlayabilmesini tanımlar.

Bağlayıcı kaynaklar:
- `docs/LEARNING_ENGINE_SPEC.md`
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/MASTERY_FORMULA_V0.md`
- `docs/RETENTION_FORGETTING_SPEC.md`
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/PRIORITY_POLICY_SPEC.md`
- `docs/PREREQUISITE_POLICY_SPEC.md`
- `docs/ENGLISH_FOUNDATION_RULES.md`

Ana ilke:

> **Diagnostic, mastery için daha düşük standart kullanan kestirme yol değildir. Aynı GRE-v0 evidence/gate kurallarını daha verimli toplama yoludur.**

İkinci ilke:

> **Skip bir task/lesson silme kararı değil, Objective bazlı doğrulanmış `coverage waiver` kararıdır. Yalnız kanıtlanan kısım atlanır.**

Üçüncü ilke:

> **Kullanıcının `biliyorum` demesi diagnostic'i başlatabilir ama tek başına mastery veya waiver oluşturmaz.**

---

# 1. 3E neyi çözer?

Kullanıcı daha önce C, Linux veya başka bir Skill'i dışarıda öğrenmiş olabilir. Sistem onu otomatik olarak bütün başlangıç derslerine zorlamamalıdır.

Ancak şu anti-pattern de yasaktır:

```text
"Biliyorum" → 1 kolay quiz → Topic mastered
```

3E'nin amacı iki riski aynı anda azaltmaktır:

1. **false-negative / gereksiz tekrar:** bildiği konuyu tekrar tekrar okutmak,
2. **false-positive / yanlış skip:** yüzeysel veya şans eseri başarıyla kritik temel beceriyi atlamak.

---

# 2. Diagnostic ayrı mastery sistemi değildir

Diagnostic attempt'lar normal Attempt/Artifact/Evidence pipeline'ına girer.

```text
Diagnostic Task
    ↓
Attempt / Artifact
    ↓
Prerequisite + contamination validation
    ↓
Assistance / provenance
    ↓
Objective attribution
    ↓
GRE-v0 eligible evidence groups
    ↓
Objective / Skill mastery decision
    ↓
Coverage waiver
    ↓
Topic / PRG readiness / Planner replan
```

Kritik invariant:

```text
diagnostic_mastery_threshold != easier_threshold
```

Diagnostic için ayrı `0.60 geç` veya `1 doğru cevap yeter` kuralı yoktur.

---

# 3. Diagnostic ne zaman açılabilir?

Diagnostic şu kaynaklardan doğabilir:

```text
user_requested_fast_path
planner_diagnostic_opportunity
prior_experience_claim
curriculum_entry_placement
resume_after_external_learning
```

Self-report yalnız scope/başlangıç noktası seçmeye yardım eder.

Örnek:
- kullanıcı `C pointers biliyorum` der,
- sistem pointer Skill/Objectives için diagnostic candidate üretir,
- `biliyorum` beyanı evidence değildir.

Diagnostic kullanıcıya zorunlu değildir; isterse normal öğrenme akışından başlayabilir.

---

# 4. Diagnostic scope — Topic değil önce Objective/Skill

Diagnostic mümkün olduğunca required Learning Objective ve canonical Skill düzeyinde çalışır.

Topic yalnız orchestration/coverage görünümüdür.

Örnek `Basic Pointers` Topic'i:
- address vs value,
- pointer declaration/assignment,
- dereference,
- write-through-pointer

gibi ayrı Objective/Skill hedefleri içerebilir.

Kullanıcı dereference'i biliyor ama address/value açıklamasında eksikse bütün Topic ya `geçti` ya `kaldı` yapılmaz.

---

# 5. Diagnostic TaskCandidate contract

3B TaskCandidate üzerine ek davranış:

```text
primary_purpose = diagnose
independence_mode = h0_required  # waiver/mastery üretecek kanıt için
```

Diagnostic candidate en az:

```text
- target_skill_ids
- target_objective_ids
- objective_attributions
- required_skill_ids
- evidence_contract
- variant_family_id / dependency_group_id
- validation_status
- difficulty / complexity
- base_estimated_minutes
- diagnostic_stage
```

taşıyabilir.

`diagnostic_stage` örnekleri:

```text
probe
confirm
critical_confirm
transfer_confirm
```

Bunlar ayrı mastery değerleri değil, efficient test routing içindir.

---

# 6. Fast path = adaptive probing, düşük standart değil

Diagnostic bütün Topic için sabit uzun sınav olmak zorunda değildir.

V1 şu yaklaşımı kullanabilir:

1. yüksek bilgi değerli ve prerequisite-valid bir **probe** seç,
2. başarı/başarısızlıkta hangi Objective'lerin gerçekten ayrı gözlendiğini çıkar,
3. zaten yeterli evidence olan Objective'i tekrar test etme,
4. eksik GRE gate'i için yalnız gerekli **confirm** task'lerini üret,
5. fail edilen prerequisite varsa dependent probe'ları durdur,
6. partial result varsa yalnız unresolved Objective'lere devam et.

Bu yaklaşım süreyi azaltır ama GRE-v0 gate'lerini azaltmaz.

---

# 7. Tek kolay quiz ile skip yasaktır

Bir Topic/Skill yalnız:
- recognition/MCQ,
- tek basic item,
- aynı family varyantları,
- self-confidence,
- task completion

ile waived/mastered yapılamaz.

Standard Objective için diagnostic evidence yine GRE-v0 default gate'lerini karşılamalıdır:
- recent direct score gate,
- minimum independent H0 direct groups,
- diversity/family gate,
- required direct type,
- verified evaluator,
- no unresolved recheck.

Critical Objective için GRE-v0 critical gate aynen korunur:
- daha fazla independent direct group,
- non-basic evidence,
- coding ise H0 user-authored artifact,
- debugging ise H0 diagnosis/fix,
- gerekiyorsa transfer.

Diagnostic bunların hiçbirini düşürmez.

---

# 8. Integrated diagnostic kullanılabilir ama otomatik toplu pass yok

Tek kapsamlı görev birden fazla Objective/Skill'i ölçebilir; bu hızlı path için değerlidir.

Ancak her component için 3B kuralları uygulanır:

```text
structurally_essential == true
AND separately_observable == true
AND valid objective attribution
```

olmadan global task success o component'e direct evidence vermez.

```text
one_project_pass != whole_topic_waiver
```

Bir integrated diagnostic, ayrı gözlenebilen birkaç Objective için aynı anda evidence oluşturabilir; kalan Objective'ler ayrıca doğrulanır.

---

# 9. Coverage waiver mastery değildir

Yeni artifact:

```text
DiagnosticCoverageWaiver
- waiver_id
- topic_id
- objective_id
- skill_id
- source_evidence_group_ids[]
- issued_at
- curriculum_version
- reason = validated_prior_knowledge
- status: active | superseded | invalidated
```

Waiver'ın anlamı:

> `Bu Objective için zorunlu başlangıç öğretim/coverage adımı, kullanıcının mevcut bağımsız performansıyla doğrulandığı için tekrar zorunlu değildir.`

Waiver:
- current mastery'nin yerine geçmez,
- retention state değildir,
- sonsuza kadar `hâlâ biliyor` iddiası değildir,
- yalnız coverage gate'inin nasıl karşılandığını açıklar.

Current competence her zaman GRE-v0/RVR-v0'dan gelir.

---

# 10. Partial waiver canonical davranıştır

Diagnostic sonucu üç kaba çıktı üretebilir:

```text
full_validated_waiver
partial_validated_waiver
no_waiver
```

## Full validated waiver
Topic'in bütün required Objective'leri için:
- coverage waiver geçerli,
- bağlı required/critical Skill mastery gate'leri geçmiş.

Bu durumda Topic `available → mastered` geçebilir.

## Partial validated waiver
Yalnız bazı Objective'ler güvenilir biçimde kanıtlanmıştır.

Sistem:
- o Objective'lerin başlangıç öğretimini tekrar zorunlu tutmaz,
- unresolved Objective'leri normal teach/practice/assessment akışına bırakır,
- Topic genellikle `learning` olur.

## No waiver
Kullanıcı gerekli gate'leri gösterememiştir.

Sistem normal öğrenmeye döner. Bu ceza değildir.

---

# 11. `available → mastered` diagnostic yolu

Doğrudan geçiş yalnız şu durumda mümkündür:

```text
all required Topic Objectives coverage-satisfied
AND all required/critical canonical Skills GRE-v0 mastered
AND no unresolved critical recheck
AND PRG-v0 prerequisite conditions valid
```

Coverage `lesson_completed` yerine valid diagnostic waiver ile karşılanabilir.

Bu nedenle:

```text
diagnostic success alone != Topic mastered
```

Topic state yine derived'dır.

---

# 12. Diagnostic failure nasıl yorumlanır?

## 12.1 Henüz mastered olmayan / prior-knowledge diagnostic

Kullanıcı bir Objective'i diagnostic'te gösteremezse:
- waiver verilmez,
- ilgili Objective normal öğrenme akışına girer,
- kullanıcı `remediation_required` diye cezalandırılmaz sırf diagnostic'i geçemedi diye,
- baseline negative/partial diagnostic evidence history'de tutulabilir,
- sonraki gerçek evidence GRE-v0 bounded recent window'da doğal olarak güncellenir.

Amaç:

```text
"zaten biliyor musun?" testini geçemedi
!=
"önceden mastered beceride regression doğrulandı"
```

## 12.2 Önceden mastered Skill için diagnostic benzeri recheck

Bu artık 3E fast-path değil, RVR-v0 retention/verification kurallarıdır.

---

# 13. Prerequisite failure diagnostic'i dallandırır

Diagnostic task'in hard prerequisite'i hazır değilse PRG-v0 uygulanır.

Örnek:
- kullanıcı `linked list biliyorum` der,
- pointer dereference prerequisite'i `not_ready` çıkar,
- linked-list production diagnostic'i target için adil değildir.

Sistem:
1. dependent diagnostic candidate'ı bloklar,
2. pointer prerequisite diagnostic/learning need üretir,
3. bağımsız diğer diagnostic/learning branches'i devam ettirebilir.

Öğretilmemiş prerequisite yüzünden target Skill'e negative evidence yazılmaz.

---

# 14. Assistance / AI kullanımı

Waiver veya mastery üretecek diagnostic evidence için GRE-v0 gereği **H0** gerekir.

- H1/H2 diagnostic performansı öğrenme/diagnosis history'sinde kullanılabilir,
- H3/H4 çözüm ifşası waiver üretmez,
- solution-exposed aynı item confirm olarak kullanılamaz,
- fresh/unseen H0 confirm gerekir.

Kullanıcı diagnostic sırasında AI kullanmak isterse bu yasak değildir; fakat o attempt hızlı-skip kanıtı olmaktan çıkar ve gerekirse normal öğrenme/yardım akışına dönüşür.

---

# 15. Evaluator / provenance guard

High-stakes diagnostic:
- trusted/prevalidated item,
- validated parametric family,
- verified deterministic evaluator/artifact check

tercih eder.

Yalnız provisional LLM grading critical waiver/mastery gate'ini tek başına geçiremez.

AI-generated candidate Aşama 4D–4E validation politikası olmadan otomatik trusted diagnostic değildir.

---

# 16. False-positive skip guard

V1 aşağıdaki guard'ları birlikte kullanır:

1. self-report evidence değildir,
2. single-item whole-topic skip yok,
3. recognition-only critical mastery yok,
4. H0 independence zorunlu,
5. prerequisite-validity zorunlu,
6. variant/dependency diversity korunur,
7. required direct evidence türü korunur,
8. critical Objective için GRE-v0 critical gates düşürülemez,
9. integrated task'ta component attribution ayrı olmalı,
10. provisional evaluator tek başına critical pass veremez,
11. solution-exposed item confirm olamaz,
12. partial mastery yalnız partial waiver üretir.

---

# 17. False-negative / gereksiz tekrar guard

Sistem kullanıcıyı zaten kanıtladığı Objective için tekrar başlangıç öğretimine zorlamamalıdır.

Kurallar:
- aktif waiver varsa normal teach candidate o Objective için bastırılır,
- aynı Skill başka Topic'te kullanılıyorsa canonical mastery yeniden sıfırlanmaz,
- mevcut eligible evidence zaten GRE gate'inin bir kısmını karşılıyorsa diagnostic aynı evidence'ı yok sayıp sıfırdan test başlatmaz,
- yalnız eksik gate/family/direct-type için confirm üretir,
- valid external/önceki uygulama içi evidence varsa duplicate work azaltılır.

External artifact/evidence'ın tam trust/provenance import politikası ileride 4/8/13 aşamalarında ayrıntılandırılabilir; 3E otomatik güvenmez.

---

# 18. Curriculum version değişikliği

Waiver objective/curriculum versiyonuna bağlıdır.

Yeni curriculum sürümünde:
- aynı Objective semantik olarak değişmediyse mevcut waiver korunabilir,
- yeni required Objective eklenirse eski waiver yeni Objective'i kapsamaz,
- Objective meaning/rubric materially değişirse waiver yeniden doğrulama isteyebilir.

Eski waiver'ın varlığı yeni öğretilmemiş requirement'ı otomatik geçmiş saymaz.

---

# 19. Diagnostic'in planner priority'si

3C PBR-v0 ile uyumlu:
- normal diagnostic opportunity çoğunlukla P3 `planned_progress`,
- user-requested fast path aynı band içinde `decision_value` yükseltebilir,
- diagnostic bir sonraki branch'i açıp açmayacağını belirliyorsa decision value artar,
- diagnostic P0/P1 repair/verification işlerini sırf kullanıcı skip istedi diye bypass edemez.

Diagnostic süreleri 3A hard capacity içindedir; ayrıca ücretsiz zaman yaratmaz.

---

# 20. Replan sonrası entegrasyon

Diagnostic evidence geldikten sonra sıra:

```text
Attempt/Artifact
→ evidence validation
→ GRE-v0 update
→ coverage waiver update
→ Skill mastery decisions
→ PRG-v0 prerequisite readiness
→ Topic state derive
→ LearningNeed refresh
→ PBR-v0 priority
→ capacity replan
```

Örnek:
- kullanıcı pointer diagnostic'inde bütün required Objective'leri gerçekten kanıtladı,
- `Basic Pointers` coverage waiver tamamlandı,
- canonical pointer Skills mastered,
- dynamic-memory prerequisite artık `ready`,
- planner gereksiz pointer lessons'i kaldırıp yeni eligible branch'e geçebilir.

---

# 21. Diagnostic sonucu reason inputs

3G için machine-readable girdiler:

```text
diagnostic_requested_by_user
diagnostic_probe_selected
diagnostic_confirm_required
diagnostic_partial_waiver
diagnostic_full_waiver
diagnostic_no_waiver
objective_waived_prior_knowledge
objective_not_demonstrated
critical_evidence_missing
prerequisite_blocked_diagnostic
assistance_prevented_waiver
provisional_evaluator_requires_confirm
curriculum_change_requires_recheck
```

UI metinleri 3G/7C/7E'de kesinleşir.

---

# 22. Bounded / deterministic performans

D-028 gereği diagnostic:
- bütün curriculum'u her seferinde taramaz,
- current Topic/branch ve prerequisite closure ile bounded scope'ta çalışır,
- aynı state + diagnostic config + trusted candidate set için aynı next probe/confirm sonucunu üretir,
- LLM'nin keyfi `bunu biliyor sayalım` kararıyla waiver vermez,
- gereken candidate generation async olabilir fakat eligibility/mastery/waiver karar çekirdeği deterministic kalır.

---

# 23. Anti-patterns / yasaklar

1. `Kullanıcı biliyorum dedi → skip`.
2. `Tek MCQ doğru → Topic mastered`.
3. Diagnostic için GRE threshold/gate'i düşürmek.
4. `1 integrated project pass → tüm Skills waived`.
5. H1–H4 assisted attempt'i H0 diagnostic pass saymak.
6. Öğretilmemiş prerequisite yüzünden diagnostic fail'i target negative evidence yapmak.
7. Partial sonuçta bütün Topic'i ya pass ya fail yapmak.
8. Waiver'ı current mastery/retention state sanmak.
9. Provisional AI evaluator ile critical pass vermek.
10. Diagnostic'i günlük capacity dışında ekstra zorunlu iş olarak eklemek.
11. External artifact'ı provenance doğrulamadan trusted mastery saymak.
12. Curriculum değiştiğinde eski waiver'ı yeni Objective'lere otomatik yaymak.

---

# 24. Örnek akışlar

## Örnek A — kullanıcı gerçekten pointer biliyor

```text
User: "Pointers biliyorum"
→ diagnostic probe
→ H0 coding + explanation evidence
→ eksik GRE gate'leri için farklı-family confirm
→ all required Objectives PASS
→ validated coverage waiver
→ Topic available → mastered
→ downstream prerequisite ready
→ planner yeni branch'e geçer
```

## Örnek B — kısmen biliyor

```text
address/value PASS
pointer declaration PASS
dereference FAIL
write-through-pointer unresolved
```

Sonuç:
- ilk iki Objective waiver,
- diğerleri normal learning,
- Topic `learning`,
- kullanıcı ilk iki dersi tekrar etmek zorunda değil.

## Örnek C — critical coding Skill'i MCQ ile bildiğini gösteriyor

Recognition doğru olsa bile:
- direct production gate eksik,
- waiver/mastery tamamlanmaz,
- H0 coding confirm gerekir.

## Örnek D — diagnostic'te AI'dan çözüm alıyor

Attempt öğrenme için kullanılabilir fakat:
- H3/H4 ise waiver yok,
- solution-exposed item confirm olamaz,
- fresh H0 variant gerekir.

---

# 25. 3E acceptance criteria

3E PASS için:

1. Diagnostic ayrı/easier mastery standardı değildir.
2. Self-report yalnız diagnostic trigger/scope'tur.
3. Tek kolay quiz whole-topic skip yapamaz.
4. Objective/Skill seviyesinde partial waiver vardır.
5. Coverage waiver mastery/retention'dan ayrı tutulur.
6. `available → mastered` yalnız coverage + GRE gates birlikte sağlanınca mümkündür.
7. Critical Skill diagnostic'i GRE critical gates'i korur.
8. H0/provenance/evaluator/variant/prerequisite guard'ları korunur.
9. Integrated diagnostic component evidence'ı ayrı attribution ister.
10. Diagnostic fail henüz öğrenilmemiş Skill'de otomatik remediation cezası değildir.
11. Prerequisite contamination target negative evidence üretmez.
12. Diagnostic sonucu PRG/Topic/Planner replan'e bağlanır.
13. Diagnostic daily capacity içindedir.
14. Policy bounded/deterministic ve D-028 uyumludur.

---

# 26. Sonraki adım

`3F — Kaçırılan günler`

3F uzun ara sonrası current-state recovery, backlog dump yasağı, overdue retention/remediation yoğunluğu ve planner'ın yeniden giriş davranışını kesinleştirecektir.