# Technical English CEFR Progression Spec — TECP-v0

**Adım:** 7B — A1/A2/B1/B2+ teknik hedefleri  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-27  
**Final model:** `TECP-v0 — Technical English CEFR Progression`  
**Final decision:** `D-064`

Bu belge D01 Technical English capability graph'ını CEFR 2020 Companion Volume ile **bağlama uyarlanmış progression metadata** olarak hizalar.

Ana ilke:

> **CEFR bandı learner mastery state değildir. Canonical gerçek hâlâ exact Skill/Objective evidence'dır; CEFR yalnız Technical English hedeflerini anlaşılır bir progression profile olarak organize eder.**

İkinci ilke:

> **Mevcut D01 Skill anlamı CEFR seviyesi uğruna genişletilmez. B2+ hedefi, yalnız semantik olarak uygun capability'lerde professional evidence-depth extension'dır.**

---

# 1. Bağlayıcı girdiler

TECP-v0 şu accepted kaynakları tüketir ve değiştirmez:

- `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md` — EED-v0 / D-063
- `curriculum/english/7a_entry_diagnostic/blueprint.yaml`
- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0
- `docs/DIAGNOSTIC_WAIVER_SPEC.md` — VDW-v0
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0
- `docs/QUESTION_BANK_SPEC.md` — QAB-v0
- `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md` — AIV-v0
- `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` — WLRM-v0
- `curriculum/decomposition/6c_foundations/review_queue.yaml`
- `research/7b_technical_english_cefr_research.md`

7B:
- yeni Skill/Objective identity üretmez,
- D01 prerequisite graph'ını değiştirmez,
- GRE/VDW threshold'u değiştirmez,
- English'i technical curriculum'un global hard prerequisite'i yapmaz.

---

# 2. CEFR alignment semantics

## 2.1 Ne anlama gelir?

`cefr_anchor_band` şu soruyu cevaplar:

> Bu canonical Technical English Skill'in mevcut literal capability scope'u, gerçek teknik kullanım bağlamında hangi CEFR progression bandında **ana hedef** olarak öğretilip ölçülmeye başlanmalıdır?

Bu alan:
- resmi sınav sonucu değildir,
- Council of Europe onayı değildir,
- mastery state değildir,
- tek başına learner'ı o genel CEFR seviyesinde ilan etmez.

## 2.2 Text-first coverage qualifier

Current D01 map ağırlıklı olarak:
- written reception,
- short written production,
- written/online interaction,
- limited plurilingual/bilingual scaffold

kapsar.

Bu nedenle current product **plain `English B1` / `English B2`** gibi general-language level claim yapamaz.

Allowed wording örnekleri:

```text
Technical English — A2 target profile
Technical English — B1 base profile
B1 base + B2+ professional extension evidence
CEFR-aligned Technical English profile
```

Forbidden wording:

```text
CEFR-certified B1
Official B2 English
Your English level is B2
```

Oral reception/interaction/production coverage ileride gerçekten eklense bile overall level claim ayrıca validity/calibration gerektirir.

---

# 3. Canonical base progression bands

Machine-readable source:

`curriculum/english/7b_cefr_progression/alignment.yaml`

## 3.1 A1 — Technical Entry

Amaç: kısa teknik string/sentence'larda temel meaning ve instruction yapısını çözebilmek.

Canonical A1 anchor Skills:

1. `skill.english.recognize_core_technical_labels`
2. `skill.english.technical_noun_phrase_recognition`
3. `skill.english.be_and_simple_present_comprehension`
4. `skill.english.imperative_instruction_comprehension`
5. `skill.english.preposition_function_word_comprehension`

Bu bandın tamamlanması general English A1 certificate iddiası değildir.

## 3.2 A2 — Technical Operator

Amaç: straightforward task/error/documentation context'inde kısa teknik English'i kullanarak işlem yapabilmek.

Canonical A2 anchor Skills:

1. `skill.english.negation_question_comprehension`
2. `skill.english.follow_bilingual_technical_instruction`
3. `skill.english.read_simple_terminal_error_fragments`
4. `skill.english.documentation_navigation`
5. `skill.english.write_command_result_note`

Bilingual scaffold burada geçici/uygun destek olabilir; higher band evidence için scaffold doğal olarak azalır fakat bu reduction takvime değil evidence'a bağlıdır.

## 3.3 B1 — Technical Independent Base

Amaç: temel professional engineering workflow'larında bağımsız text-first Technical English kullanımı.

Canonical B1 anchor Skills:

1. `skill.english.read_definition_and_constraint`
2. `skill.english.read_procedure_sequence`
3. `skill.english.write_basic_bug_description`
4. `skill.english.explain_simple_technical_process`
5. `skill.english.ask_clarifying_technical_question`

Bu band D01'in canonical **base capability ceiling**'idir: accepted 15 Skill'in literal scope'u burada eksiksiz temsil edilir.

---

# 4. B2+ — Professional Technical Extension

## 4.1 B2+ neden ayrı davranır?

D01 içinde bazı Skill isimleri/capability scope'ları açıkça `simple` veya `basic`tir. 7B bunları B2+ görünümü yaratmak için sessizce genişletemez.

Bu nedenle:

```text
B2_plus != new_mastery_band
B2_plus != rename_B1_skills_as_advanced
B2_plus = bounded professional evidence-depth extension
```

## 4.2 Extension-eligible canonical Skills

Mevcut semantik sınır içinde daha yoğun/professional context varyantı güvenle kullanılabilen Skills:

- `skill.english.documentation_navigation`
- `skill.english.read_definition_and_constraint`
- `skill.english.read_procedure_sequence`
- `skill.english.ask_clarifying_technical_question`

Allowed extension examples:
- daha yoğun dokümanda doğru section/cross-reference bulma,
- birden çok qualifier/exception/constraint'i birlikte izleme,
- procedure içinde condition/warning/dependency akışını takip etme,
- online professional collaboration'da precise clarification isteme.

## 4.3 Explicitly not auto-extended

Aşağıdaki existing semantics B2+ uğruna genişletilemez:

- `read_simple_terminal_error_fragments` → complex log diagnosis değildir,
- `write_basic_bug_description` → full professional incident/RCA report değildir,
- `explain_simple_technical_process` → advanced design/trade-off argument değildir,
- `write_command_result_note` → long-form technical report değildir,
- `follow_bilingual_technical_instruction` → independent advanced English comprehension değildir.

İleride gerçek ayrı capability gerekirse GNS-v0 Capability Independence Test + KGC migration workflow uygulanır. 7B bunu şimdiden yeni Skill olarak icat etmez.

---

# 5. CEFR descriptor-family linkage

TECP-v0 exact CEFR descriptor text'ini curriculum identity olarak kopyalamaz. Bunun yerine stable scale-family refs kullanır.

Allowed family vocabulary:

```text
cefr.overall_reading_comprehension
cefr.reading_for_orientation
cefr.reading_for_information_and_argument
cefr.reading_instructions
cefr.vocabulary_range
cefr.grammatical_accuracy
cefr.overall_written_production
cefr.overall_written_interaction
cefr.goal_oriented_online_transactions_collaboration
cefr.plurilingual_comprehension
```

Her Skill en az bir family ref taşır. Bu ref:
- task authoring guidance verir,
- CEFR descriptor lookup/provenance sağlar,
- Skill identity veya evidence rule değildir.

---

# 6. Profile derivation

## 6.1 Canonical source of truth

Her zaman:

```text
Skill/Objective state + evidence
> CEFR-derived display summary
```

## 6.2 Highest complete base band

UI/analytics isterse aşağıdaki **derived summary** hesaplanabilir:

```text
highest_complete_base_band = highest of A1/A2/B1
where every canonical Skill anchored at or below that band
is mastery_or_waiver_confirmed under normal GRE/VDW rules
```

Bu:
- yeni mastery state değildir,
- raw score değildir,
- CEFR certification değildir,
- bir üst banddaki tek başarısızlıktan alt band evidence'ını silmez.

Uneven profile her zaman gösterilebilir.

## 6.3 B2+ presentation

7B B2+ için synthetic complete/incomplete learner state üretmez.

UI ileride yalnız şunları gösterebilir:
- `B2+ professional extension evidence present` gibi qualified evidence summary,
- hangi extension-capability context'lerinde evidence bulunduğu.

B2+ display/mastery davranışının kesin learner-facing kuralı **7E** kapsamındadır.

---

# 7. Hard prerequisite / band monotonicity

Canonical D01 hard edges değişmez.

Base anchor invariant:

```text
for every English hard edge prerequisite -> target:
  band(prerequisite) <= band(target)
```

Aynı band içinde hard edge olabilir.

Band metadata hiçbir technical Skill'e yeni hard edge oluşturamaz.

---

# 8. Pre-A1 bridge

CEFR Companion Volume Pre-A1'i tanır. AI Infra Learning Coach zero-entry kullanıcıyı desteklediği için Pre-A1 concept'i diagnostic/teaching scaffold context'inde kullanılabilir.

Ancak 7B canonical target bands:

```text
A1 -> A2 -> B1 -> B2+ professional extension
```

şeklindedir.

Pre-A1:
- ayrı course completion gate değildir,
- learner failure label değildir,
- technical curriculum blocker değildir.

Root A1 evidence yoksa planner sadece entry bridge learning need üretir.

---

# 9. Assessment / contamination rules

EED-v0 aynen korunur:

- self-report/certificate/confidence evidence değildir,
- unknown grammar/vocabulary hidden prerequisite olamaz,
- specialist technical knowledge English construct'ünü kirletemez,
- invalid/prerequisite-contaminated attempt negative evidence yazamaz,
- downstream failure broadcast yoktur,
- trusted resource + verified evaluator + normal GRE/VDW gate gerekir.

CEFR bandı task difficulty etiketi olarak tek başına mastery üretmez.

---

# 10. 7B acceptance requirements

7B tamamlanabilmek için:

1. 7A canonical 15 Skill seti alignment dataset'te exact 15/15 bulunmalı.
2. Her Skill exactly-one base anchor band (`A1 | A2 | B1`) almalı.
3. Base dağılım 5 A1 / 5 A2 / 5 B1 olmalı.
4. 16/16 canonical English hard edge için band monotonicity PASS olmalı.
5. B2+ extension listesi canonical Skill subset'i olmalı; `simple/basic` semantics'i aşan auto-extension olmamalı.
6. CEFR scale-family vocabulary kontrollü olmalı.
7. General-English/certification overclaim guard açık olmalı.
8. English→technical global hard gate oluşmamalı.
9. `review.6c.english.cefr_alignment` 7B finalization'da resolution almalı.
10. Final Stage 6 regression + EED-v0 regression + independent 7B validator PASS olmalı.
11. D-050 living-memory + external-memory + repo-wide stale-reference audit PASS olmalı.

---

# 11. Future-stage boundaries

7B şu işleri **yapmaz**:

- günlük English dakika/cadence/task mix → **7C**,
- technical task içinde bilingual scaffold/integration rules → **7D**,
- learner-facing English mastery/CEFR display final behavior → **7E**,
- gerçek production lesson/item bodies → **15F/15G**,
- empirical level-link/calibration validity → **18D**,
- genuinely new professional English capability gerekirse full curriculum capability review → **20 / normal GNS-KGC migration**.

---

# 12. 7B review resolution proposal

`review.6c.english.cefr_alignment` için candidate resolution:

> D01 identities split edilmeden context-only CEFR progression metadata'ya bağlandı. 15 base Skill 5×A1 + 5×A2 + 5×B1 anchor aldı; B2+ yalnız semantik sınırı aşmayan selected professional evidence-depth extension olarak tanımlandı. Current D01 text-first olduğu için plain general-English CEFR/certification claim yasaklandı. No new Skill/Objective/prerequisite edge required in 7B.

Bu resolution QA + POST kabulünden önce canonical `resolved` yapılmaz.
