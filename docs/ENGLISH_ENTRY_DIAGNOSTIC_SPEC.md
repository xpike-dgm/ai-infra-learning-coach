# English Entry Diagnostic Spec — EED-v0

**Adım:** 7A — Başlangıç ölçümü  
**Durum:** CANDIDATE — QA + POST-STEP kapanışı bekliyor  
**Tarih:** 2026-08-27  
**Candidate model:** `EED-v0 — English Entry Diagnostic`  
**Candidate decision:** `D-063`

Bu belge AI Infra Learning Coach'un kullanıcı ilk kez English paralel hattına girdiğinde mevcut English capability'lerini nasıl ölçeceğini tanımlar.

Ana amaç:

> **Kullanıcının tek bir broad “İngilizce seviyesi”ni tahmin etmek yerine, Stage 6'da kabul edilmiş D01 Technical English Skill/Objective graph'ında hangi capability'lerin güvenilir evidence taşıdığını, hangilerinin unresolved olduğunu ve hangi prerequisite frontier'dan öğrenmeye başlanması gerektiğini belirlemek.**

7A bir CEFR certification/placement sınavı değildir. A1/A2/B1/B2+ alignment ve kullanıcıya gösterilecek level metadata **7B** kapsamındadır.

---

# 1. Bağlayıcı girdiler

EED-v0 şu canonical contract'ları değiştirmez; onları tüketir:

- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/DIAGNOSTIC_WAIVER_SPEC.md` — VDW-v0
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/QUESTION_BANK_SPEC.md` — QAB-v0
- `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md` — AIV-v0
- `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` — WLRM-v0
- `curriculum/decomposition/6c_foundations/skills.yaml`
- `curriculum/decomposition/6c_foundations/objectives.yaml`
- `curriculum/decomposition/6c_foundations/prerequisite_edges.yaml`
- `research/7a_english_entry_diagnostic_research.md`

Stage 6 final graph ve D01 capability identities değişmez. 7A yeni English Skill yaratmaz, Skill clone'lamaz ve prerequisite graph'ını sessizce değiştirmez.

---

# 2. Research-backed design stance

7A research girdisi üç tasarım yönünü destekler:

1. **Profile before single score:** CEFR descriptor yaklaşımı proficiency'yi farklı activity/competence alanlarında profile olarak ifade etmeye uygundur.
2. **Specific-purpose context:** Technical English assessment gerçek hedef kullanım alanına — documentation, instructions, errors, short technical writing/interaction — uyarlanmalıdır.
3. **Claim → evidence → task:** Evidence-Centered Design yaklaşımı her assessment inference'ının hangi evidence ile ve hangi task'tan üretildiğini explicit tutmayı gerektirir.

Bu nedenle EED-v0 canonical shape'i:

```text
exact Skill / Objective claim
→ required evidence
→ prerequisite-valid task family
→ attempt/artifact
→ contamination + assistance + evaluator validation
→ GRE / VDW / WLRM pipeline
→ granular diagnostic profile
```

---

# 3. 7A'nın ölçtüğü D01 capability seti

7A final Stage 6 D01 registry'sindeki **15 English Skill**'in tamamını diagnostic scope olarak tanır.

## Foundation / receptive branch

1. `skill.english.recognize_core_technical_labels`
2. `skill.english.technical_noun_phrase_recognition`
3. `skill.english.be_and_simple_present_comprehension`
4. `skill.english.imperative_instruction_comprehension`
5. `skill.english.preposition_function_word_comprehension`
6. `skill.english.negation_question_comprehension`

## Applied technical comprehension branch

7. `skill.english.follow_bilingual_technical_instruction`
8. `skill.english.read_simple_terminal_error_fragments`
9. `skill.english.documentation_navigation`
10. `skill.english.read_definition_and_constraint`
11. `skill.english.read_procedure_sequence`

## Technical production / interaction branch

12. `skill.english.write_command_result_note`
13. `skill.english.write_basic_bug_description`
14. `skill.english.explain_simple_technical_process`
15. `skill.english.ask_clarifying_technical_question`

Her Skill'in canonical owner Objective'i/Objective'leri `6c_foundations/objectives.yaml` içinden çözülür. Diagnostic broad Topic pass/fail üretmez.

---

# 4. Non-goals

7A şunları **yapmaz**:

- final A1/A2/B1/B2+ level ataması,
- CEFR ile validasyon iddiası,
- speaking/listening için Stage 6'da olmayan yeni Skill identity icat etme,
- production lesson/resource body yazma,
- sabit “20 soru = seviye” sınavı tasarlama,
- yeni mastery yüzdesi/threshold üretme,
- self-report veya sertifikayı mastery sayma,
- teknik programlama bilgisini English proficiency proxy'si yapma,
- bir root failure'dan bütün English domain'ini `weak` ilan etme.

CEFR alignment ve activity-coverage review 7B'ye; günlük cadence 7C'ye; technical integration 7D'ye; English-specific mastery presentation/behavior 7E'ye gider.

---

# 5. Diagnostic evidence standardı

## 5.1 Diagnostic daha kolay mastery yolu değildir

VDW-v0 invariant aynen korunur:

```text
diagnostic_mastery_standard == normal_GRE_mastery_standard
```

Bir diagnostic attempt:

- normal Attempt/Artifact/Evidence pipeline'ına girer,
- H0 / direct / verified / prerequisite-valid olması gerekiyorsa aynı şartları taşır,
- same-family/testlet dependence guard'ına tabidir,
- Objective attribution'ı exact olmalıdır.

Tek recognition item, self-confidence veya tek kolay correct response coverage waiver üretmez.

## 5.2 Diagnostic observation ile mastery ayrımı

7A iki ayrı şeyi ayırır:

```text
DiagnosticObservation
!=
SkillMasteryDecision
```

Diagnostic bir Skill için faydalı evidence toplayabilir ama GRE hard gate'leri henüz tamamlanmadıysa sonuç yalnız `evidence_present_not_yet_sufficient` kalır.

## 5.3 Negative evidence guard

Bir yanlış cevap ancak:

- task valid,
- target separately observable,
- prerequisite-valid,
- assistance/provenance interpretable,
- evaluator verified,
- ambiguity/technical-knowledge contamination yok

ise target için clean negative evidence adayı olabilir.

Aksi durumda `invalid_or_contaminated` veya `prerequisite_unresolved` yazılır; target weakness yazılmaz.

WLRM-v0 broad reset guard aynen geçerlidir.

---

# 6. Diagnostic profile modeli

7A'nın ana learner-facing olmayan canonical çıktısı:

```text
EnglishEntryDiagnosticProfile
- profile_id
- curriculum_graph_version
- diagnostic_model_version = EED-v0
- started_at
- last_updated_at
- completion_state
- self_report_context
- skill_resolutions[]
- objective_evidence_refs[]
- entry_frontier_skill_ids[]
- unresolved_skill_ids[]
- deferred_skill_ids[]
- cefr_alignment_status = pending_7B
- provenance
```

## 6.1 Skill diagnostic resolution vocabulary

Her D01 Skill yalnız aşağıdaki diagnostic resolution'lardan birini alır:

```text
mastery_or_waiver_confirmed
evidence_present_not_yet_sufficient
learning_needed_clean_evidence
prerequisite_unresolved
not_assessed
user_deferred
invalid_or_contaminated
```

Anlamları:

### `mastery_or_waiver_confirmed`
GRE-v0/VDW-v0 normal gate'leri gerçekten tamamlanmıştır. 7A kendi başına bu status'u uyduramaz.

### `evidence_present_not_yet_sufficient`
Diagnostic'te olumlu, valid evidence vardır fakat GRE/VDW closure için yeterli değildir. Planner gerektiğinde `confirm` / `transfer_confirm` üretir.

### `learning_needed_clean_evidence`
Clean prerequisite-valid negative evidence ilgili Objective/Skill için yeni learning/remediation ihtiyacını destekler. Bu broad Domain failure değildir.

### `prerequisite_unresolved`
Target doğrudan başarısız sayılmaz. Önce hard English prerequisite çözülmelidir.

### `not_assessed`
Diagnostic bu capability'ye henüz ulaşmamıştır veya modality/resource desteği yoktur. Negatif inference yoktur.

### `user_deferred`
Kullanıcı diagnostic'i ertelemiş/pause etmiştir. Negatif inference yoktur.

### `invalid_or_contaminated`
Task/evaluator/technical-context/unknown-language-prerequisite problemi nedeniyle attempt yorumlanamaz.

---

# 7. Self-report contract

Başlangıçta kullanıcıdan isteğe bağlı context alınabilir:

- daha önce İngilizce öğrenip öğrenmediği,
- documentation/error okuma rahatlığı,
- kısa teknik yazı yazma rahatlığı,
- bildiğini düşündüğü genel/teknik alanlar,
- varsa dış sınav/certificate beyanı,
- diagnostic'i kısa oturumlara bölme tercihi.

Ancak:

```text
self_report != evidence
certificate_claim != imported_mastery
confidence != mastery
```

Bu bilgiler yalnız:

- ilk probe sırasını,
- hangi branch'te yüksek information-value task seçileceğini,
- kullanıcıya neden belirli bir probe gösterildiğinin açıklamasını

etkileyebilir.

External certificate import/verification ayrı bir özellik olmadan mastery yazamaz.

---

# 8. Construct-contamination guard — teknik bilgi English'i kirletmez

Technical English ölçülürken task target'ı English ise teknik konu yalnız minimum context sağlamalıdır.

Örnek güvenli context'ler:

- file exists / file missing,
- command runs / fails,
- input / output,
- value / error,
- short option/example block,
- basit fictional CLI/documentation fragment.

Aşağıdakiler English diagnostic için required hidden knowledge yapılamaz:

- Python semantics,
- C pointer reasoning,
- Linux administration,
- CUDA/GPU knowledge,
- distributed-systems knowledge.

Bir item bunlardan birini gerçekten gerektiriyorsa:

```text
technical_context_dependency = true
```

olarak işaretlenir ve 7A baseline English inference'ı için kullanılmaz veya teknik prerequisite ayrıca hazır olmalıdır.

---

# 9. Unknown-English-prerequisite guard

`ENGLISH_FOUNDATION_RULES` aynen geçerlidir:

> Kullanıcıya henüz öğretilmemiş/diagnostic olarak gösterilmemiş grammar veya vocabulary'yi kullanmasını zorunlu kılan production task verilmez ve kullanıcı bu eksik yüzünden target capability'den başarısız sayılmaz.

Bu nedenle diagnostic DAG prerequisite-first çalışır.

Örnek:

```text
recognize labels
→ be/simple-present comprehension
→ negation/question comprehension
→ ask clarifying question
```

`negation_question_comprehension` unresolved iken `ask_clarifying_technical_question` clean-failure probe'u olarak kullanılamaz.

---

# 10. Adaptive diagnostic routing

EED-v0 fixed linear exam değildir.

## 10.1 Stage vocabulary

TaskCandidate `diagnostic_stage`:

```text
entry_probe
branch_probe
confirm
production_probe
transfer_confirm
```

Bunlar mastery state değildir; routing metadata'sıdır.

## 10.2 Başlangıç

Default diagnostic başlangıcı English hard-DAG root capability'lerinden yapılır.

Mevcut D01 graph'ta primary root:

```text
skill.english.recognize_core_technical_labels
```

Self-report daha ileri bir başlangıç iddia etse bile dependent branch için gerekli root/prerequisite evidence yok sayılmaz; sistem gerektiğinde kısa prerequisite probe ile interpretable path kurar.

## 10.3 Probe selection priority

Eligible task'ler arasında semantik öncelik:

1. unresolved hard prerequisite'i en çok açıklığa kavuşturan probe,
2. birden çok downstream capability'nin eligibility'sini açabilecek root/branch probe,
3. prior-knowledge claim'ini doğrulayacak yüksek-information probe,
4. GRE/VDW closure için eksik direct-type/family/transfer evidence'ı,
5. daha pahalı production task.

Bu priority numeric IRT/information-function değildir; cold-start deterministic routing policy'sidir.

## 10.4 Success path

Valid positive probe sonrası:

- target evidence kaydedilir,
- downstream English hard-prerequisite eligibility yeniden hesaplanır,
- GRE/VDW gate tamamlanmadıysa gerekirse `confirm` seçilir,
- daha ileri Skill otomatik mastered yapılmaz.

## 10.5 Failure path

Clean target failure sonrası:

- target için `learning_needed_clean_evidence` adayı oluşabilir,
- o Skill'e hard-dependent diagnostic branch'ler `prerequisite_unresolved` yapılır,
- aynı downstream Skill'ler ayrı ayrı failed sayılmaz,
- bağımsız sibling branch'ler devam edebilir.

## 10.6 Pause / stop

Diagnostic tek oturumda tamamlanmak zorunda değildir.

Stop/pause koşulları:

- kullanıcı erteledi,
- current session capacity sona erdi,
- remaining branches prerequisite-unresolved,
- in-scope branch'ler yeterli evidence resolution aldı,
- trusted resource/evaluator eksikliği nedeniyle valid probe üretilemiyor.

7A sabit dakika veya sabit item sayısı icat etmez. Unresolved state saklanır ve devam oturumunda yeniden kullanılır.

---

# 11. Task-family blueprint

Machine-readable canonical companion:

`curriculum/english/7a_entry_diagnostic/blueprint.yaml`

Her diagnostic claim şunları taşır:

```text
- skill_id
- objective_resolution = canonical_owner_objectives
- hard_prerequisite_skill_ids
- diagnostic_tier
- primary_evidence_type
- task_family_id
- technical_context_policy
- default_stage
```

## 11.1 Recognition / reasoning

Uygun task biçimleri:

- term ↔ meaning matching,
- kısa phrase meaning discrimination,
- sentence/command intent extraction,
- relation/function-word interpretation,
- negation/question meaning classification.

Recognition-only evidence production capability'yi doğrulamaz.

## 11.2 Applied comprehension

Uygun task biçimleri:

- short bilingual instruction execution/selection,
- terminal/error signal extraction,
- documentation heading/example/option navigation,
- definition + constraint extraction,
- procedure sequence + warning/condition tracking.

## 11.3 Production / interaction

Uygun task biçimleri:

- short command/input/result note,
- expected/actual/error bug note,
- simple technical process explanation,
- clarifying technical question.

Text production V1 diagnostic'te primary interoperable modality olabilir. Spoken variant kullanılacaksa target aynı canonical Skill'de kalır ve capture/evaluator güvenilirliği ayrı validation ister; spoken modality yoksa `not_assessed` ile korunur, yazılı başarıdan otomatik speech mastery çıkarılmaz.

---

# 12. Resource ve evaluator güveni

Diagnostic item/task mastery evidence üretme potansiyeline sahip olduğu için:

- trusted QAB resource olabilir,
- AI-generated ise AIV-v0 validation lifecycle'ından geçmelidir,
- ambiguity/answer-key/rubric hatası invalid evidence üretir,
- production response evaluator sonucu `verified` olmadan mastery gate'e girmez,
- LLM tek başına learner mastery state yazamaz.

On-the-fly generated, doğrulanmamış bir item “diagnostic hızlı olsun” gerekçesiyle trusted evidence yapılamaz.

---

# 13. Integrated task policy

Bir short documentation/problem scenario aynı anda birkaç English Objective'i gözletebilir.

Ancak her component için:

```text
structurally_essential == true
AND separately_observable == true
AND exact objective attribution exists
```

olmadan evidence yazılmaz.

Örnek: kullanıcı procedure'u doğru izledi diye `write_basic_bug_description` otomatik pass olmaz.

---

# 14. CEFR handoff — 7B boundary

7A profile'ında:

```text
cefr_alignment_status = pending_7B
cefr_level = null
```

zorunludur.

7A sonucu kullanıcıya `A1`, `A2`, `B1`, `B2` olarak canonical claim yapamaz.

7B şu input'ları alır:

- 15 D01 Skill capability statements,
- 7A diagnostic claim/task families,
- evidence/profile vocabulary,
- CEFR Companion Volume descriptors,
- `review.6c.english.cefr_alignment` open review.

7B alignment sırasında mevcut Stage 6 capability map'in CEFR activity coverage açısından kontrollü migration gerektirip gerektirmediğini ayrıca değerlendirir; 7A bunu önceden varsaymaz.

---

# 15. Planner / learning handoff

Diagnostic sonrası planner broad `English weak` need üretmez.

Örnek output:

```text
entry_frontier_skill_ids:
  - skill.english.be_and_simple_present_comprehension
  - skill.english.imperative_instruction_comprehension

confirmed_or_evidence_present:
  - skill.english.recognize_core_technical_labels

prerequisite_unresolved:
  - skill.english.read_definition_and_constraint
```

Planner yalnız exact Skill/Objective learning/confirm needs üretir.

Technical planner branch'i English profile düşük diye global kilitlenmez. 7D ileride task-level English integration rules'ını genişletecek.

---

# 16. Diagnostic explanation contract

Kullanıcıya açıklanabilir reason-code'lar:

```text
english_entry_probe
prior_knowledge_claim_check
prerequisite_check_before_production
confirm_existing_evidence
resolve_uncertain_branch
fresh_transfer_confirmation
resume_incomplete_diagnostic
```

Açıklama `test sana A2 verdi` gibi unsupported claim kullanmaz.

---

# 17. QA fixtures

7A validator/test suite en az şu davranışları zorunlu tutar:

### E7A-01 — Exact D01 scope
Blueprint accepted Stage 6 D01 English Skill setini eksiksiz ve duplicatesiz kapsar.

### E7A-02 — English hard-DAG match
Blueprint hard prerequisite declarations 6C canonical English→English hard edges ile uyumludur.

### E7A-03 — No technical global gate
English diagnostic technical Skill readiness'ini global prerequisite yapmaz; technical curriculum da English diagnostic sonucu yüzünden global lock olmaz.

### E7A-04 — Objective ownership
Her diagnostic Skill'in en az bir canonical owner Objective'i vardır ve diagnostic eligible'dır.

### E7A-05 — Evidence compatibility
Task family primary evidence type Skill'in canonical direct evidence types setinde bulunur.

### E7A-06 — Self-report non-evidence
Blueprint self-report'tan mastery/waiver üretmez.

### E7A-07 — No easier threshold
7A blueprint yeni pass percentage, fixed item-count mastery veya easier diagnostic threshold içermez.

### E7A-08 — CEFR boundary
`cefr_alignment_status=pending_7B`; 7A canonical level atamaz.

### E7A-09 — Failure contamination guard
Prerequisite-unresolved/invalid attempt target clean failure olarak yazılamaz.

### E7A-10 — Downstream no-fail broadcast
Prerequisite failure downstream branch'leri failed değil `prerequisite_unresolved` yapar.

### E7A-11 — Production prerequisite guard
Production claim'leri prerequisite DAG çözülmeden clean independent failure probe'u olamaz.

### E7A-12 — Resource trust
Mastery/waiver-capable task yalnız QAB/AIV-compatible trusted/validated resource path kullanır.

### E7A-13 — Pause-safe
Incomplete diagnostic unresolved state'i kaybetmeden pause/resume edebilir.

### E7A-14 — Profile not broad score
Output exact Skill resolution/frontier içerir; mandatory broad domain score yoktur.

### E7A-15 — Stage boundary
7B CEFR alignment, 7C cadence, 7D technical integration, 7E English mastery UX/behavior açıkça sonraya bırakılır.

---

# 18. 7A acceptance contract

7A ancak şu koşullarla tamamlanır:

- research basis repository'de,
- `EED-v0` spec repository'de,
- machine-readable blueprint 15/15 D01 English Skill'i kapsıyor,
- canonical prerequisites/evidence compatibility validator PASS,
- CEFR premature-level guard PASS,
- VDW/GRE/PRG/WLRM safety guards PASS,
- independent QA sonucu PASS,
- D-063 decision kaydı yazılmış,
- `review.6c.english.cefr_alignment` 7B'ye açık bırakılmış,
- D-050 POST living-memory sync + repo-wide stale-reference audit tamamlanmış,
- 7B active-not-executed yapılmış.

---

# 19. 7A final candidate özeti

```text
EED-v0
= exact D01 Skill/Objective diagnostic
+ prerequisite-aware adaptive probing
+ claim→evidence→task traceability
+ no easier diagnostic mastery
+ no hidden technical/English prerequisite
+ granular profile/frontier output
+ no premature CEFR level
+ pause/resume safe
+ 7B-ready evidence handoff
```

Bu modelin amacı kullanıcıyı “hangi seviyedesin?” sorusuna tek etiketle sıkıştırmak değil, **hangi English capability'leri gerçekten gösterebildin ve öğrenmeye nereden başlamalıyız?** sorusunu güvenilir biçimde yanıtlamaktır.
