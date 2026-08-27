# Technical English Mastery Profile Spec — TEPM-v0

**Adım:** 7E — English mastery  
**Durum:** CANDIDATE / QA BEKLİYOR  
**Tarih:** 2026-08-27  
**Candidate model:** `TEPM-v0 — Technical English Mastery Profile`  
**Candidate decision:** `D-067`

Bu belge accepted Technical English graph'ındaki learner state'in kullanıcıya nasıl **dürüst, granular, CEFR-aligned ama certification iddiası olmayan** bir profil olarak sunulacağını tanımlar.

Ana ilke:

> **7E yeni bir mastery engine değildir. English mastery source of truth exact Skill/Objective için GRE-v0; retention truth RVR-v0; prerequisite/remediation truth PRG/WLRM'dir. 7E yalnız bu state'i learner-facing Technical English profile'a deterministik biçimde türetir.**

İkinci ilke:

> **Tek broad English score veya tek general-English CEFR label, uneven capability profile'ın yerini alamaz.**

Üçüncü ilke:

> **`review_due` unutma değildir; `verification_due` ilk contradiction sonrası uncertainty'dir; assisted/provisional/contaminated performance independent mastery değildir.**

---

# 1. Bağlayıcı girdiler

TEPM-v0 şu accepted contract'ları tüketir ve değiştirmez:

- `docs/MASTERY_FORMULA_V0.md` — GRE-v0 / D-031
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0 / D-032
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036
- `docs/DIAGNOSTIC_WAIVER_SPEC.md` — VDW-v0 / D-037
- `docs/QUESTION_BANK_SPEC.md` — QAB-v0 / D-047
- `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md` — AIV-v0 / D-048
- `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` — WLRM-v0 / D-061
- `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md` — EED-v0 / D-063
- `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md` — TECP-v0 / D-064
- `docs/DAILY_ENGLISH_COMPONENT_SPEC.md` — DECP-v0 / D-065
- `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md` — TEIP-v0 / D-066
- `research/7e_english_mastery_profile_research.md`

7E:
- yeni English Skill veya Objective üretmez,
- prerequisite graph'ını değiştirmez,
- yeni mastery threshold/formül üretmez,
- CEFR bandını canonical learner mastery state yapmaz,
- general-English certification/official-level claim üretmez,
- B2+ aggregate completion state üretmez,
- UI ekran tasarımını kesinleştirmez → AŞAMA 8/16,
- runtime DB schema yazmaz → 9C,
- empirical report-label validity/calibration yapmaz → 18.

---

# 2. Canonical truth vs presentation

Canonical learner truth:

```text
Skill / Objective mastery = GRE-v0
retention = RVR-v0
prerequisite readiness = PRG-v0
validated prior-knowledge pathway = VDW-v0
weakness/remediation = WLRM-v0
CEFR anchor metadata = TECP-v0
technical integration/evidence attribution = TEIP-v0
```

7E output:

```text
canonical state
  -> deterministic learner-facing projection
  -> Technical English profile
```

Profile projection hiçbir canonical engine'e geri `mastery=true/false` yazamaz.

---

# 3. Exact D01 scope

7E current final D01 registry'deki exact 15 canonical Skill'i kullanır:

### A1 anchor
1. `skill.english.recognize_core_technical_labels`
2. `skill.english.technical_noun_phrase_recognition`
3. `skill.english.be_and_simple_present_comprehension`
4. `skill.english.imperative_instruction_comprehension`
5. `skill.english.preposition_function_word_comprehension`

### A2 anchor
6. `skill.english.negation_question_comprehension`
7. `skill.english.follow_bilingual_technical_instruction`
8. `skill.english.read_simple_terminal_error_fragments`
9. `skill.english.documentation_navigation`
10. `skill.english.write_command_result_note`

### B1 anchor
11. `skill.english.read_definition_and_constraint`
12. `skill.english.read_procedure_sequence`
13. `skill.english.write_basic_bug_description`
14. `skill.english.explain_simple_technical_process`
15. `skill.english.ask_clarifying_technical_question`

7E bu identity'leri clone/split/rename etmez.

---

# 4. Learner-facing Skill presentation state

Bu state **derived presentation**dır; canonical mastery state değildir.

Controlled vocabulary:

```text
not_yet_evidenced
developing_with_support
developing_independent
confirmed_current
confirmed_review_due
confirmation_verification_due
remediation_required
prerequisite_unresolved
```

## 4.1 Derivation precedence

Bir English Skill için deterministik precedence:

1. confirmed WLRM/GRE remediation gate açıksa → `remediation_required`
2. unresolved GRE/RVR `verification_due` varsa → `confirmation_verification_due`
3. Skill mastered/validated-waiver confirmed ve RVR `review_due` ise → `confirmed_review_due`
4. Skill mastered/validated-waiver confirmed ve retention `fresh|stable` ise → `confirmed_current`
5. exact Skill için hard prerequisite unresolved ve clean target evidence yorumlanamıyorsa → `prerequisite_unresolved`
6. eligible independent H0 direct evidence var ama normal GRE gate henüz geçmiyorsa → `developing_independent`
7. yalnız assisted/provisional/practice evidence varsa → `developing_with_support`
8. aksi halde → `not_yet_evidenced`

Invalid/contaminated evidence positive veya negative presentation state yaratmaz; yalnız ilgili QA/need trace'inde kalır.

## 4.2 Diagnostic waiver presentation

VDW-v0 ile normal standartta doğrulanmış prior knowledge, learner-facing `confirmed_current|confirmed_review_due|confirmation_verification_due` türetiminde mastery-confirmed kaynak olarak kullanılabilir.

UI/provenance isterse source ayrımı gösterilebilir:

```text
confirmation_source:
- learned_and_verified
- validated_entry_evidence
```

Bu iki kaynak için mastery standardı farklı değildir.

---

# 5. Review / verification / remediation semantics

## 5.1 `review_due`

Canonical:

```text
review_due != forgotten
review_due != unmastered
```

Bu nedenle:
- Skill presentation `confirmed_review_due` olur,
- base-band achievement aşağı düşmez,
- learner'a `review recommended/due` sinyali gösterilebilir,
- sırf tarih geçti diye weakness/remediation oluşmaz.

## 5.2 `verification_due`

İlk clean contradiction sonrası:
- historical confirmation korunur,
- learner-facing claim açıkça uncertainty taşır,
- Skill `confirmation_verification_due` olur,
- profile summary `verification_due` badge taşır,
- fresh H0 recheck beklenir.

`verification_due` anında `not mastered` değildir.

## 5.3 Confirmed remediation

Fresh independent recheck sonrası GRE gates gerçekten düşmüş ve WLRM remediation açmışsa:
- affected Skill `remediation_required`,
- current complete-base-band derivation affected Skill'i confirmed sayamaz,
- daha düşük tam band varsa current base summary oraya düşebilir,
- previously-confirmed higher band provenance/history silinmez.

Bu düşüş takvimsel decay değil, yeni clean evidence sonucudur.

---

# 6. Base CEFR-aligned Technical English profile

TECP-v0 base bands:

```text
A1 -> A2 -> B1
```

Her zaman qualified wording kullanılır.

## 6.1 Current complete base band

```text
current_complete_base_band = highest of A1/A2/B1
where every canonical Skill anchored at or below that band
is currently mastery_or_validated_waiver_confirmed
AND no covered Skill is in confirmed remediation
```

`review_due` bandı düşürmez.

Unresolved `verification_due` da historical mastery'yi silmez; fakat current summary `verification_due` qualifier alır.

## 6.2 Base profile status

Controlled vocabulary:

```text
not_yet_complete
complete_current
complete_review_due
complete_verification_due
```

Derivation:
- hiçbir base band complete değil → `not_yet_complete`
- current complete band kapsamındaki confirmed Skills'te review/verification issue yok → `complete_current`
- verification yok ama en az bir covered Skill `review_due` → `complete_review_due`
- en az bir covered historically-confirmed Skill unresolved `verification_due` → `complete_verification_due`

Confirmed remediation sonrası affected band current-complete sayılmaz; profile yeniden exact current state'ten türetilir.

## 6.3 Historical confirmation

Sistem isterse şu provenance'ı saklayabilir/gösterebilir:

```text
historical_highest_confirmed_base_band
historical_confirmed_at
```

Bu current readiness/mastery yerine geçmez; sadece `daha önce doğrulandı, şimdi yeniden doğrulanıyor/remediation var` açıklamasını mümkün kılar.

## 6.4 No broad/general CEFR claim

Allowed:

```text
Technical English — A2 base profile confirmed
Technical English — B1 base profile; verification due
A2 base complete; B1 skills developing
```

Forbidden:

```text
Your English is B1
Official B1
CEFR-certified B1
You passed English B1
```

Current D01 text-first scope dışına taşan speaking/listening/general-language claim yapılmaz.

---

# 7. Uneven profile is first-class

Base summary exact Skill detail'i gizleyemez.

Example:

```text
A2 base profile confirmed
B1 profile:
- read_definition_and_constraint -> confirmed_current
- read_procedure_sequence -> confirmed_current
- write_basic_bug_description -> developing_independent
- explain_simple_technical_process -> developing_with_support
- ask_clarifying_technical_question -> confirmed_review_due
```

Bu profil:
- broad `B1 failed` üretmez,
- zayıflığı exact Skill/Objective'de tutar,
- planner/remediation'a granular state sağlar.

## 7.1 No compensatory average

Forbidden:

```text
english_mastery_percent = average(skill scores)
cefr_numeric_average = mean(A1=1,A2=2,B1=3,...)
```

Bir güçlü Skill başka required Skill açığını kapatamaz.

---

# 8. B2+ professional extension presentation

TECP-v0 extension-eligible Skills only:

1. `skill.english.documentation_navigation`
2. `skill.english.read_definition_and_constraint`
3. `skill.english.read_procedure_sequence`
4. `skill.english.ask_clarifying_technical_question`

7E aggregate `B2+ complete` üretmez.

Per-capability extension presentation:

```text
no_extension_evidence
extension_practice_only
extension_evidence_present
extension_review_due
extension_verification_due
```

## 8.1 Eligible extension evidence

`extension_evidence_present` ancak:
- task/resource TECP-v0 bounded B2+ context within semantic boundary,
- explicit canonical Skill target,
- H0/direct/verified/prerequisite-valid,
- no answer-revealing scaffold,
- normal GRE/QAB/AIV constraints pass
ise yazılabilir.

Bu evidence aynı existing Skill'in normal evidence history'sine girebilir; ayrı B2+ mastery state yaratmaz.

## 8.2 Learner-facing wording

Allowed:

```text
Professional extension evidence present:
- documentation navigation
- definition / constraint reading
```

Forbidden:

```text
B2+ complete
You are B2+
Official Technical English B2+
```

---

# 9. Assistance / scaffold presentation

## 9.1 Assisted progress is useful, but distinct

```text
assisted_success != independent_mastery
```

H1–H4, answer-revealing bilingual translation, model answer exposure veya provisional evaluator:
- learning progress gösterebilir,
- `developing_with_support` üretmeye katkı sağlayabilir,
- independent-confirmed state üretmez.

## 9.2 Existing mastery is not erased by later support

Daha önce H0 evidence ile confirmed Skill'in sonraki practice task'ında yardım kullanması mastery'yi otomatik silmez.

Assistance:
- exact Attempt/evidence provenance'a aittir,
- dependency/hint behavior için kullanılabilir,
- clean contradiction yoksa mastered state'i cezalandırmaz.

## 9.3 Scaffold fading readiness

7E fixed day/ratio üretmez.

Learner-facing support hint'i yalnız semantic olabilir:

```text
support_needed
support_can_be_reduced
independent_use_confirmed
```

Bunlar mastery state değildir; TEIP-v0 evidence/task-validity davranışının presentation helper'ıdır.

---

# 10. Technical integration effects on English profile

TEIP-v0 binding matrix korunur.

### `technical_only_localized`
- English profile update: **none**.
- Turkish/bilingual support technical evidence'ı sırf English olmadığı için düşürmez.

### `technical_with_english_exposure`
- explicit English target yoksa exposure/reinforcement only.
- profile mastery closure yok.

### `dual_target_integrated`
- English component separately observable + explicit target + normal strong-evidence gate geçerse English profile'a evidence girebilir.
- technical PASS global English PASS değildir.

### `english_primary_technical_context`
- technical context ready/controlled/scaffolded olmalı.
- specialist technical ignorance English negative mastery'ye dönüşemez.

---

# 11. Invalid / provisional / contaminated evidence presentation

Aşağıdakiler learner-facing clean weakness/mastery claim üretmez:

- `invalid_prerequisite_contamination`
- `invalid_scaffold_leakage`
- ambiguous item
- evaluator `invalid`
- evaluator `provisional` tek başına
- undeclared technical prerequisite
- undeclared English prerequisite

UI isterse `Bu deneme seviyeni değiştirmedi` gibi explanation gösterebilir.

---

# 12. English Profile Decision Trace

Physical DB schema 9C'ye aittir; 7E semantic trace:

```text
TechnicalEnglishProfileTrace
- skill_id
- objective_ids[]
- cefr_anchor_band
- derived_skill_presentation_state
- confirmation_source
- mastery_decision_ref
- retention_state
- prerequisite_state
- remediation_state
- unresolved_verification_ref
- last_qualifying_independent_evidence_ref
- last_assisted_practice_ref
- b2_plus_extension_state
- profile_reason_codes[]
- profile_policy_version
```

Base summary trace:

```text
TechnicalEnglishBaseProfileSummary
- current_complete_base_band
- base_profile_status
- historical_highest_confirmed_base_band
- review_due_skill_ids[]
- verification_due_skill_ids[]
- remediation_skill_ids[]
- developing_skill_ids[]
- b2_plus_extension_evidence_skill_ids[]
- qualification_scope = technical_english_text_first
- profile_policy_version
```

---

# 13. Reason codes

Controlled vocabulary:

```text
ENGLISH_SKILL_NOT_YET_EVIDENCED
ENGLISH_SKILL_DEVELOPING_ASSISTED
ENGLISH_SKILL_DEVELOPING_INDEPENDENT
ENGLISH_SKILL_CONFIRMED
ENGLISH_SKILL_REVIEW_DUE
ENGLISH_SKILL_VERIFICATION_DUE
ENGLISH_SKILL_REMEDIATION_REQUIRED
ENGLISH_PREREQUISITE_UNRESOLVED
TECHNICAL_ENGLISH_BASE_BAND_COMPLETE
TECHNICAL_ENGLISH_BASE_BAND_REVIEW_DUE
TECHNICAL_ENGLISH_BASE_BAND_VERIFICATION_DUE
TECHNICAL_ENGLISH_UNEVEN_PROFILE
B2_EXTENSION_EVIDENCE_PRESENT
B2_EXTENSION_PRACTICE_ONLY
ENGLISH_EVIDENCE_ASSISTANCE_LIMITED
ENGLISH_EVIDENCE_CONTAMINATED_NO_STATE_CHANGE
TECHNICAL_ONLY_TASK_NO_ENGLISH_ATTRIBUTION
```

---

# 14. Safety scenarios

## E7E-F01 — partial A2
A1 complete, four of five A2 Skills confirmed. Result: current complete band A1; exact A2 gap shown. No averaged A2 pass.

## E7E-F02 — B1 + review due
All A1/A2/B1 Skills mastered; one B1 Skill is `review_due`. Result: B1 base remains complete with `complete_review_due`.

## E7E-F03 — B1 + first contradiction
Previously confirmed B1; one covered Skill `verification_due`. Result: no instant deletion; base summary `complete_verification_due`, exact Skill highlighted.

## E7E-F04 — confirmed B1 remediation
Fresh recheck causes one B1 Skill to fail GRE and remediation opens. Result: current complete band falls to highest lower fully confirmed band; historical B1 provenance retained.

## E7E-F05 — assisted-only production
Bug description succeeds only after answer-revealing scaffold. Result: `developing_with_support`, not confirmed mastery.

## E7E-F06 — independent partial evidence
One clean H0 direct group exists but GRE diversity/gates incomplete. Result: `developing_independent`.

## E7E-F07 — diagnostic waiver
Validated entry evidence satisfies VDW/GRE-compatible requirements. Result: confirmed presentation allowed; provenance says validated entry evidence.

## E7E-F08 — technical-only bilingual task
C task is correct under bilingual instructions. Result: C evidence may be valid; English profile unchanged.

## E7E-F09 — authentic English exposure
Linux task includes an English man-page fragment with no English target. Result: exposure/reinforcement only, no English mastery.

## E7E-F10 — dual-target task
Code artifact passes and English note independently fails cleanly. Result: technical positive and English negative/developing states may coexist; no global broadcast.

## E7E-F11 — hidden technical prerequisite
English reading task secretly requires specialist CUDA reasoning. Failure is contaminated; English profile does not decline.

## E7E-F12 — provisional LLM evaluation
Open-ended English answer receives only provisional evaluator result. Result: no independent-confirmed mastery.

## E7E-F13 — B2+ one-capability evidence
Only documentation-navigation extension has strong evidence. Result: named extension evidence shown; no B2+ completion label.

## E7E-F14 — B2+ assisted practice
B2+ constraint reading succeeds with answer-revealing translation. Result: extension practice only.

## E7E-F15 — lower-band verification issue
A previously confirmed A1 prerequisite becomes `verification_due` while learner had B1 historical confirmation. Result: historical B1 remains; current profile clearly carries verification uncertainty and planner follows PRG/RVR rules.

## E7E-F16 — review overdue for months
No negative evidence, only overdue review. Result: no auto-demotion, no remediation, review signal only.

## E7E-F17 — one strong Skill cannot compensate
Four B1 Skills excellent, one required B1 Skill not mastered. Result: B1 base not current-complete.

## E7E-F18 — invalid attempt
Ambiguous/contaminated English attempt occurs. Result: history may keep attempt; profile state unchanged.

---

# 15. Learner-facing wording contract

Preferred compact labels:

```text
Yetkin / Confirmed
Tekrar zamanı / Review due
Yeniden doğrulama gerekli / Verification due
Gelişiyor — bağımsız kanıt birikiyor
Gelişiyor — destekle
Ön koşul bekliyor
Hedefli tekrar gerekli / Remediation
```

Wording locale UX AŞAMA 8/16'da iyileştirilebilir; semantic meaning değişmez.

Profile heading:

```text
Technical English profile
```

Not:

```text
CEFR-aligned Technical English scope; not an official general-English certification.
```

---

# 16. Progress / analytics guard

7E şu değerleri canonical progress yapmaz:

```text
english_percentage
english_score_0_100
cefr_numeric_average
b2_plus_completion_percentage
english_days_streak
minutes_studied
```

Descriptive counts UX için ileride gösterilebilir fakat mastery/readiness formülü değildir.

---

# 17. 7E acceptance requirements

7E ancak şunlar sağlanırsa tamamlanabilir:

1. 7A/7B/7C/7D exact D01 15-Skill scope değişmeden kalmalı.
2. Yeni Skill/Objective/prerequisite edge oluşmamalı.
3. GRE/RVR/PRG/VDW/WLRM canonical ownership değişmemeli.
4. Exactly 8 derived Skill presentation state olmalı.
5. Base band yalnız `A1|A2|B1` qualified Technical English summary olmalı.
6. `review_due` mastery/band demotion yapmamalı.
7. `verification_due` instant mastery deletion yapmamalı; current uncertainty explicit olmalı.
8. Confirmed remediation current band derivationını evidence'a göre yeniden hesaplayabilmeli; historical confirmation korunmalı.
9. Assisted/provisional/contaminated evidence independent-confirmed state üretememeli.
10. B2+ exactly TECP-v0 dört extension-eligible Skill ile sınırlı kalmalı.
11. Aggregate `B2+ complete` veya general-English/certification claim yasak olmalı.
12. Technical-only/exposure task English mastery broadcast yapmamalı.
13. Dual-target attribution TEIP-v0 component separation'ını korumalı.
14. No numeric English mastery percentage / CEFR average / fixed threshold added.
15. E7E-F01..E7E-F18 safety fixtures deterministic validation'dan geçmeli.
16. Stage 6 + accepted 7B + 7C + 7D regressions PASS olmalı.
17. Independent 7E validator PASS olmalı.
18. D-050 living-memory sync + external-memory validation + repo-wide stale-reference audit PASS olmalı.

---

# 18. Future-stage boundaries

7E tamamlandığında AŞAMA 7 kapanır.

Deferred:
- actual screen hierarchy / interaction design → 8A–8G
- persistent physical schema → 9C
- task runner/profile runtime implementation → 11/12/13
- AI translation/evaluation runtime → 14
- production English lessons/items → 15F/15G
- detailed progress analytics presentation → 16A/16C
- usability/accessibility wording → 17
- empirical report interpretation, thresholds and calibration → 18C/18D
- genuinely broader professional English capabilities beyond D01 semantic scope → 20 + GNS/KGC capability review.

7E yeni runtime implementation yapmaz.
