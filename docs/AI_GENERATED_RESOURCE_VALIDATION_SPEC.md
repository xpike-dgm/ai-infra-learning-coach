# AI-Generated Assessment Resource Validation Specification — AIV-v0

**Adım:** 4E — AI-generated soru doğrulaması  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Final model:** `AIV-v0 — AI Assessment Resource Validation`

Bu belge AI tarafından üretilen assessment resource'ların `QAB-v0 — Trusted Assessment Resource Bank` içine hangi koşullarda alınabileceğini, hangi kullanım tavanına kadar yükseltilebileceğini, hangi durumda practice-only kalacağını ve hangi durumda bloke/invalidated olacağını tanımlar.

Bağlayıcı kaynaklar:
- `docs/QUESTION_BANK_SPEC.md` — QAB-v0 / D-047
- `docs/DAILY_MICRO_ASSESSMENT_SPEC.md` — DMA-v0 / D-040
- `docs/WEEKLY_ASSESSMENT_SPEC.md` — WBA-v0 / D-045
- `docs/MONTHLY_ASSESSMENT_SPEC.md` — MCA-v0 / D-046
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/MASTERY_FORMULA_V0.md` — GRE-v0
- `docs/RETENTION_FORGETTING_SPEC.md` — RVR-v0
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0
- `docs/TASK_TAXONOMY_SPEC.md`
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — D-044

Ana ilke:

> **AI çıktısı kendi doğruluğunun kanıtı değildir. Generated resource önce candidate'dır; exact kullanım tavanı ancak bağımsız, izlenebilir ve risk-seviyesine uygun validation kontrollerinden sonra belirlenir.**

İkinci ilke:

> **Validation tek bir “LLM doğru dedi” cevabı değildir. Schema, correctness, answer/rubric, ambiguity, prerequisite, target/evidence fit, family/dependency/context, evaluator, tool/artifact, freshness ve execution-safety boyutları ayrı kontrol edilir.**

Üçüncü ilke:

> **Validator belirsizliği learner'a risk olarak yansıtılmaz. Bir kontrol unresolved ise resource daha yüksek kullanım seviyesine çıkarılmaz; fail-safe yön her zaman daha düşük use ceiling veya review'dur.**

---

# 1. 4E neyi çözer?

AIV-v0 şu soruları cevaplar:

- AI-generated resource bank'e hangi lifecycle state ile girer?
- Generator kendi output'unu onaylayabilir mi?
- Hangi kontroller deterministic, hangileri AI cross-check, hangileri review gerektirir?
- Teknik olarak doğru görünen fakat ambiguous bir resource ne olur?
- Answer key/rubric yanlışsa ne olur?
- Hidden prerequisite veya henüz öğretilmemiş concept leakage nasıl yakalanır?
- Near duplicate resource yeni independent evidence family sayılır mı?
- Trusted template'ten türeyen AI varyant ne kadar trust miras alabilir?
- Standard mastery ile critical mastery için neden aynı trust yeterli değildir?
- Validator'lar anlaşamazsa ne olur?
- Version-sensitive CUDA/vLLM/API soruları nasıl freshness kontrolünden geçer?
- Sonradan hatalı olduğu anlaşılan resource geçmiş mastery evidence'ını nasıl etkiler?

AIV-v0 şunları yapmaz:
- mastery score formülünü değiştirmez → GRE-v0,
- assessment blueprint composition'ını değiştirmez → DMA/WBA/MCA,
- Question Bank schema'sını yeniden kurmaz → QAB-v0,
- LLM evaluator benchmark accuracy threshold'u icat etmez → AŞAMA 14F / AŞAMA 18 calibration.

---

# 2. Generated candidate başlangıç durumu

AI-generated resource hiçbir zaman doğrudan `trusted` başlamaz.

Başlangıç:

```text
content_origin = ai_generated
lifecycle_status = candidate
selection_eligible = false
requested_use_ceiling = generator/request context
validated_use_ceiling = none
```

Generation provenance en az şunları taşır:

```text
AIResourceGenerationProvenance
- generator_model_ref
- provider_ref?
- generation_prompt_version
- generation_config_ref?
- source_reference_refs[]?
- trusted_template_ref?
- generated_at
- generation_run_id
```

Generator'ın model adı veya kendi confidence beyanı trust sağlamaz.

---

# 3. Generator ile validator ayrımı

Kritik invariant:

```text
generator_output != validation_proof
```

Aynı modelin ikinci prompt ile “doğru mu?” demesi useful cross-check olabilir fakat **bağımsız doğrulama garantisi değildir**.

Validation kanıtı mümkün olduğunda şu sırayla güçlendirilir:

1. deterministic/static checks,
2. executable/compiler/test/property checks,
3. authoritative/reference-grounded checks,
4. independent review context / second-model critique,
5. gerektiğinde manager/human/expert review,
6. ileride empirical calibrated validator/evaluator policy.

Farklı model kullanılması tek başına `verified` anlamına gelmez.

---

# 4. Validation pipeline

Canonical pipeline:

```text
AI-generated candidate
    ↓
A. schema + canonical reference validation
    ↓
B. technical correctness
    ↓
C. answer / rubric / deterministic verifier correctness
    ↓
D. ambiguity / multiple-valid-answer analysis
    ↓
E. target Objective + evidence-modality fit
    ↓
F. prerequisite + forbidden-concept + language leakage audit
    ↓
G. duplicate / near-duplicate / variant-family classification
    ↓
H. dependency / testlet / context / transfer validation
    ↓
I. evaluator + tool + artifact compatibility
    ↓
J. source / technology freshness
    ↓
K. execution / environment safety
    ↓
L. risk-based use-ceiling decision
    ↓
ValidationRecord + lifecycle transition
```

Bir aşamada blocking failure varsa daha yüksek use ceiling verilemez.

---

# 5. Validation check sonucu

Her check tek `confidence percentage` üretmez.

Canonical result:

```text
ValidationCheckResult
- check_id
- check_kind
- status:
    pass
    fail
    uncertain
    not_applicable
- finding_codes[]
- evidence_refs[]
- max_use_ceiling_after_check
- review_required
- validator_ref
- validator_policy_version
```

`uncertain` = pass değildir.

Overall use ceiling weighted-average ile hesaplanmaz:

```text
overall_use_ceiling = tüm blocking/applicable check'lerin izin verdiği en düşük kullanım tavanı
```

Böylece bir alandaki çok güçlü sonuç, başka alandaki integrity problemini matematiksel olarak kapatamaz.

---

# 6. Schema ve canonical reference validation

Deterministic olarak kontrol edilebilir alanlar önce doğrulanır:

- `resource_id` / version formatı,
- valid `resource_kind`, activity ve evidence enums,
- target Skill/Objectives canonical graph'ta mevcut mu,
- declared prerequisite IDs mevcut mu,
- blueprint role/scope değerleri geçerli mi,
- evaluator/tool/artifact metadata tamam mı,
- variant/dependency/context refs geçerli mi,
- required field'lar resource kind için mevcut mu,
- answer/rubric/test ref gerekiyorsa var mı.

Schema incomplete resource user-facing selection'a çıkamaz.

---

# 7. Technical correctness

Resource'un promptundaki teknik iddialar doğrulanmalıdır.

Kontrol yöntemleri resource tipine göre değişir:

### Deterministic / executable örnekler
- Python/C/C++ kodu parse/compile/run,
- unit/hidden tests,
- shell command expected output sandbox check,
- deterministic answer generator,
- property-based test,
- numerical recomputation,
- exact API/runtime behavior için supported-version test.

### Reference-grounded örnekler
Version-sensitive sistemlerde mümkün olduğunda authoritative docs/source/reference kullanılır:
- compiler/tool semantics,
- CUDA toolkit / architecture behavior,
- library/API/CLI behavior,
- vLLM/SGLang/TensorRT-LLM gibi değişebilen serving semantics.

### Non-deterministic/open-ended alan
Tek LLM critique strong correctness proof değildir. Eğer correctness deterministic veya güvenilir reference ile kapatılamıyorsa use ceiling buna göre düşer veya review gerekir.

---

# 8. Answer key / rubric correctness

Bir resource teknik olarak doğru prompt taşısa bile answer key/rubric yanlış olabilir.

Validation:
- expected answer promptu gerçekten çözüyor mu,
- accepted alternatives eksik mi,
- rubric target Objective'i mi ölçüyor,
- coding tests yanlış çözümü geçiriyor mu,
- valid farklı çözümü yanlışlıkla reddediyor mu,
- hidden tests irrelevant behavior'a mı aşırı bağlı,
- debugging rubric yalnız programın çalışmasını mı ölçüyor yoksa diagnosis Objective'ini de görüyor mu,
- benchmark acceptance target performance behavior'ını gerçekten doğruluyor mu.

`wrong answer/rubric` blocking integrity failure'dır.

---

# 9. Ambiguity ve multiple-valid-answer analizi

Resource şu açılardan stress-test edilir:

- birden fazla makul yorum var mı,
- prompt eksik constraint içeriyor mu,
- birden fazla doğru cevap var ama key yalnız birini kabul ediyor mu,
- version/environment varsayımı belirtilmemiş mi,
- wording target davranıştan başka bir beceriyi ölçüyor mu,
- trick wording yanlış negative evidence üretebilir mi.

Unresolved ambiguity:

```text
assessment/mastery use = blocked
```

Practice için bile yanlış feedback verme riski varsa resource selection'a verilmez; önce düzeltilmiş yeni candidate/version gerekir.

---

# 10. Objective / evidence-modality fit

Validator declared target ile actual user behavior'ı karşılaştırır.

Örnekler:
- target `coding_production` ama task doğru kodu seçtiriyor → mismatch,
- target debugging diagnosis ama yalnız output soruyor → mismatch,
- target transfer ama yalnız değişken adları değiştirilmiş lesson clone → transfer claim invalid,
- target technical concept recall ama task gereksiz uzun coding artifact istiyor → target/measurement mismatch olabilir.

Kural:

> **Resource hangi evidence'ı gerçekten üretebiliyorsa use ceiling ve attribution ona göre sınırlandırılır; metadata target'ı AI'nın iddiasına göre değil observable behavior'a göre doğrulanır.**

---

# 11. Prerequisite completeness ve hidden prerequisite audit

Generated resource'un declared prerequisites'i gerçek çözüm yoluyla karşılaştırılır.

Kontrol:
- çözmek için hangi technical Skills gerçekten gerekiyor,
- prompt veya expected solution undeclared prerequisite kullanıyor mu,
- difficulty bilinmeyen concept ekleyerek mi artırılmış,
- generated code/API pattern henüz öğretilmemiş bir concept'e dayanıyor mu,
- exact task requirement graph'taki generic prerequisite'ten daha geniş mi.

Undeclared hard prerequisite tespit edilirse resource yüksek-stakes kullanıma çıkamaz; metadata düzeltilip yeniden validation gerekir.

---

# 12. Forbidden-not-yet concept leakage

Özellikle erken curriculum'da validator:

```text
forbidden_not_yet_concepts[]
```

ile prompt, starter code, expected answer, hints, fixtures ve rubric'in tamamını kontrol eder.

Örnek:
- basic pointer Objective'i için generated çözümün `malloc/free` istemesi,
- beginner Python loop sorusunun bilinmeyen generator expression kullanması,
- Linux foundation task'ının henüz öğretilmemiş regex/awk bilgisini zorunlu kılması.

Leakage learner failure'a dönüştürülemez.

---

# 13. English / language prerequisite audit

Teknik Objective ölçülüyorsa AI-generated wording bilinmeyen English grammar/vocabulary'yi gizli prerequisite yapamaz.

Validator:
- instruction language profile,
- language prerequisite IDs,
- Turkish/bilingual scaffold availability,
- translation equivalence

kontrol eder.

English Objective ölçülüyorsa yalnız öğretilmiş grammar/vocabulary prerequisites kullanılabilir.

Çeviri varyantı yeni independent problem family değildir.

---

# 14. Duplicate ve near-duplicate detection

Validation yalnız exact text hash kontrolü yapmaz.

Katmanlar:
- exact duplicate,
- normalized duplicate,
- same solution template,
- semantic near-duplicate,
- same bug/fix pattern,
- same algorithmic skeleton,
- trusted-template instance,
- translation-equivalent item.

Near duplicate tespit edilirse:
- yeni `resource_id` olsa bile yeni independent family sayılmaz,
- existing `variant_family_id` / dependency relationship atanabilir,
- family classification belirsizse conservative davranılır: diversity credit artırılmaz.

Embedding/LLM similarity yalnız yardımcı sinyal olabilir; family kararı audit edilebilir semantics taşır.

---

# 15. Dependency / testlet validation

AI birden fazla item üretmişse validator:
- aynı stem/context paylaşımını,
- önceki cevaba bağımlılığı,
- shared hidden state'i,
- root prerequisite contamination riskini

kontrol eder.

Local dependence varsa aynı `dependency_group_id/testlet_id` altında gruplanır.

Bir root item başarısızken downstream answers bağımsızmış gibi GRE group sayısını şişiremez.

---

# 16. Context ve transfer claim validation

Generated item'ın `transfer_profile` iddiası ayrıca doğrulanır.

`novel_application` / `cross_topic_context` / `integrated_system_context` diyebilmek için yalnız isim/sayı değişikliği yeterli değildir.

Validator şunlara bakar:
- problem structure gerçekten değişmiş mi,
- target Skill hâlâ structurally essential mı,
- çözüm lesson template'i doğrudan kopyalamakla mı çıkıyor,
- context family gerçekten farklı mı,
- yeni context bilinmeyen prerequisite ekliyor mu.

Transfer claim şüpheliyse resource normal application family olarak downgrade edilir; sahte diversity üretmez.

---

# 17. Integrated resource component attribution

Integrated task için her target Objective ayrı validate edilir:

```text
structurally_essential
AND separately_observable
AND prerequisite_valid
AND evaluator_sufficient
```

AI'nın resource metadata'sına 8 Objective yazması 8 direct evidence üretme hakkı vermez.

Observable olmayan component:
- secondary/contextual olarak tutulabilir,
- direct evidence attribution'dan çıkarılır,
- veya task rubric'i yeniden tasarlanır.

---

# 18. Evaluator compatibility

Resource'un evaluator'ı target behavior'a uygun olmalıdır.

### `verified` için güçlü yollar
- deterministic answer key,
- objective-specific unit/hidden tests,
- compiler/runtime verifier,
- deterministic trace checker,
- validated benchmark criterion,
- prevalidated rubric + ileride calibrated evaluator policy.

### Tek başına yetersiz
- generator modelin kendi self-grade'i,
- uncalibrated single-LLM score,
- açıklaması olmayan model confidence yüzdesi.

Open-ended response yalnız uncalibrated LLM ile değerlendirilebiliyorsa resource structurally valid olabilir fakat critical mastery ceiling'e çıkamaz.

---

# 19. Tool ve artifact compatibility

Validator:
- allowed tools policy gerçekten task ile uyumlu mu,
- required artifact üretilebilir mi,
- environment mevcut mu,
- target user behavior'ın önemli kısmını tool otomatik yapıyor mu,
- external AI izni H0 iddiasıyla çelişiyor mu

kontrol eder.

Örnek:
- Objective profiler kullanmayı ölçüyorsa profiler kullanımı normaldir,
- Objective sıfırdan coding production ise AI autocomplete/full generation target behavior'ı devralıyorsa H0 iddiası yapılamaz.

---

# 20. Execution / environment safety

Generated coding/system task validator sırasında veya kullanıcı ortamında kontrolsüz biçimde çalıştırılmaz.

Minimum safety policy:
- destructive filesystem/system command'ları detect/deny veya sandbox,
- privilege escalation/root requirement explicit policy olmadan yasak,
- uncontrolled network/exfiltration side effects yasak,
- fork bomb / resource exhaustion / unbounded process creation guard,
- secrets/token erişimi yasak,
- destructive cloud/container commands explicit isolated lab olmadan yasak,
- generated binaries/scripts mümkün olduğunda sandboxed runner'da doğrulanır.

Safety failure learner evidence problemi değil content integrity problemidir.

---

# 21. Technology/source freshness

Version-sensitive generated resource:
- technology dependency,
- version constraint,
- source/reference,
- `verified_at`

metadata'sı taşımalıdır.

Freshness sonucu:

```text
current
review_due
stale
```

`stale` resource standard/critical mastery assessment'ta kullanılamaz.

Evergreen concept item gereksiz version bağımlılığı taşımak zorunda değildir.

---

# 22. Risk tier ve use ceiling

AIV-v0 QAB-v0 `use_ceiling` sırasını korur:

```text
practice_only
< low_stakes_assessment
< standard_mastery_eligible
< critical_mastery_eligible
```

Bu bir numeric score değildir; semantic kullanım tavanıdır.

Her validation check kendi `max_use_ceiling_after_check` sonucunu verir. Final ceiling en kısıtlayıcı applicable check'tir.

---

# 23. Minimum policy — practice_only

Bir AI-generated resource user'a practice/remediation için gösterilmeden önce bile en az:
- schema/reference consistency,
- bilinen teknik yanlışlık olmaması,
- answer/feedback path'in tutarlı olması,
- blocking ambiguity olmaması,
- hidden prerequisite/forbidden-concept güvenliği,
- language fairness,
- execution safety

kontrollerini geçmelidir.

Correctness unresolved ise `practice_only` bile verilmez; candidate selection dışı kalır.

Practice use yanlış bilgiyi öğretme lisansı değildir.

---

# 24. Low-stakes assessment promotion

`low_stakes_assessment` için practice minimumlarına ek:
- target Objective fit doğrulanmış,
- evidence modality fit doğrulanmış,
- answer/rubric yeterli,
- prerequisite set complete,
- variant/dependency classification yapılmış,
- evaluator en az bu kullanım için valid,
- solution/answer leakage yok,
- unresolved ambiguity yok

gerekir.

Low-stakes sonuç critical mastery gate'i tek başına geçiremez.

---

# 25. Standard mastery promotion

`standard_mastery_eligible` için ek olarak:
- correctness için güçlü independent/deterministic/reference-backed validation,
- evaluator sonucu `verified` üretmeye uygun,
- exact target attribution güvenilir,
- family/dependency/context sınıflaması mastery diversity'yi sahte büyütmüyor,
- version/freshness current,
- no unresolved validator disagreement,
- required artifact/provenance policy uygulanabilir

gerekir.

Tek uncalibrated LLM validator veya evaluator standard mastery için yeterli proof sayılmaz.

---

# 26. Critical mastery promotion

`critical_mastery_eligible` en yüksek tavanıdır.

Standard şartlara ek:
- hiçbir integrity check `uncertain` değildir,
- target critical evidence profile ile exact uyum vardır,
- coding/debugging/system/performance behavior için mümkün olan deterministic/objective-specific verification bulunur,
- open-ended judgement zorunluysa tek uncalibrated LLM yerine prevalidated rubric + stronger review/calibrated evaluator policy gerekir,
- hidden prerequisite ve contamination audit strict geçmiştir,
- transfer/integration iddiası varsa context/component attribution ayrıca doğrulanmıştır,
- source/version-sensitive behavior current ve audit edilebilirdir,
- trust promotion için explicit `trusted` validation record bulunur.

Deterministic verification doğal olarak mümkün olmayan critical open-ended item, sırf birkaç AI aynı fikirde diye otomatik `critical_mastery_eligible` olmaz. Gerekirse lower ceiling'de kalır ve critical mastery başka modality/resource ile doğrulanır.

---

# 27. Lifecycle promotion

Baseline:

```text
candidate
  ├─ blocking confirmed integrity failure -> invalidated
  ├─ unresolved validation -> candidate / selection_ineligible
  └─ minimum validation passed -> validated

validated
  ├─ practice_only / low_stakes ceiling ile kalabilir
  └─ stronger trusted-use policy passed -> trusted
```

`trusted` statüsü declared target/use profile'a özgüdür; universal trust değildir.

Bir resource'un `validated` olması critical mastery'e eligible olduğu anlamına gelmez.

---

# 28. Trusted template inheritance

Trusted template'ten AI instance üretiminde full trust yalnız şu invariant'lar gerçekten korunuyorsa kısmen miras alınabilir:
- parameter schema/range validated,
- deterministic renderer/generator semantics değişmiyor,
- answer generator deterministic ve aynı invariant'ları kullanıyor,
- prerequisite set değişmiyor,
- target Objective değişmiyor,
- wording semantic rewrite yapılmıyor veya equivalence deterministic olarak korunuyor,
- generated instance constraint validator'ı geçiyor.

AI serbest biçimde promptu yeniden yazdıysa, yeni context eklediyse, solution strategy'yi değiştirdiyse veya prerequisite'i etkileyebilecek semantic değişiklik yaptıysa inheritance kesilir; candidate normal AIV validation'a döner.

Parameterized instances yeni independent variant family yaratmaz.

---

# 29. Validator disagreement / uncertainty

Validator'lar veya deterministic check ile AI review çelişirse:

```text
disagreement != majority vote pass
```

Davranış:
- blocking issue ihtimali varsa promotion durur,
- resource candidate/validated lower ceiling'de tutulur,
- `review_required` açılır,
- gerektiğinde authoritative source/executable checker/manual review istenir,
- learner'a high-impact assessment olarak gösterilmez.

AI modellerinin çoğunluk oyu truth değildir.

---

# 30. Validation retry ve bounded behavior

AIV-v0 recursive sınırsız `generate → critique → regenerate` loop'u kurmaz.

- validation attempts versioned/logged,
- failed candidate gerektiğinde new candidate/version olarak regenerate edilir,
- retry count implementation config'dir,
- UI thread'de ağır multi-model validation yapılmaz,
- high-stakes bank mümkün olduğunca prevalidated içerik kullanır,
- on-demand generated practice resource minimum validation geçemezse trusted bank fallback seçilir.

Exact retry sayısı bilimsel sabit olarak kilitlenmez.

---

# 31. Validation run contract

```text
AIResourceValidationRun
- validation_run_id
- resource_id
- resource_version
- generation_run_id
- requested_use_ceiling
- validation_policy_version
- validator_refs[]
- deterministic_check_artifact_refs[]
- source_reference_refs[]
- check_results[]
- duplicate_family_decision_ref?
- prerequisite_audit_ref?
- safety_audit_ref?
- freshness_audit_ref?
- disagreement_state
- recommended_lifecycle
- validated_use_ceiling
- review_required
- blocking_findings[]
- warning_findings[]
- created_at
```

Bu run QAB-v0 `ValidationRecord` üretir.

---

# 32. Validation reason codes

PDT/assessment trace için baseline:

```text
assessment.ai_validation.candidate_created
assessment.ai_validation.schema_failed
assessment.ai_validation.technical_correctness_failed
assessment.ai_validation.answer_rubric_failed
assessment.ai_validation.ambiguity_detected
assessment.ai_validation.prerequisite_leakage
assessment.ai_validation.language_prerequisite_leakage
assessment.ai_validation.target_mismatch
assessment.ai_validation.evidence_modality_mismatch
assessment.ai_validation.near_duplicate
assessment.ai_validation.variant_family_reused
assessment.ai_validation.dependency_group_assigned
assessment.ai_validation.transfer_claim_downgraded
assessment.ai_validation.evaluator_insufficient
assessment.ai_validation.execution_safety_failed
assessment.ai_validation.content_stale
assessment.ai_validation.validator_disagreement
assessment.ai_validation.review_required
assessment.ai_validation.validated_practice_only
assessment.ai_validation.validated_low_stakes
assessment.ai_validation.validated_standard_mastery
assessment.ai_validation.trusted_critical_mastery
assessment.ai_validation.promotion_blocked_uncertain
assessment.ai_validation.resource_invalidated
assessment.ai_validation.historical_evidence_review_required
```

---

# 33. User report / contested AI-generated item

Kullanıcı AI-generated item'ın hatalı/ambiguous olduğunu bildirirse report otomatik `invalidated` yapmaz; fakat high-impact evidence contested olabilir.

Davranış:
1. exact resource version bulunur,
2. ilgili result/evidence gerekirse `held/provisional` yapılır,
3. revalidation tetiklenir,
4. doğrulanana kadar contested item tek başına negative critical transition üretemez,
5. bug confirmed ise resource invalidated + corrected version + historical evidence review yapılır.

Kullanıcı content bug'ı nedeniyle mastery cezası almaz.

---

# 34. Revalidation triggers

Aşağıdaki durumlar revalidation açabilir:
- new bug/ambiguity report,
- authoritative source behavior change,
- technology/runtime version change,
- answer/rubric/test değişimi,
- evaluator policy değişimi,
- target Objective/prerequisite graph değişimi,
- variant/dependency/family reclassification,
- validation policy version major change,
- trusted template invariant'ının değişmesi,
- historical learner outcomes item integrity problemi düşündürüyor ancak tek başına proof oluşturmuyor.

Revalidation user mastery decay değildir; content trust sürecidir.

---

# 35. Invalidation ve historical evidence repair

Confirmed integrity bug sonrası:

```text
resource_version -> invalidated
new selection -> stop
past attempt refs -> locate
related EvidenceEvents -> invalid/unusable or review-required
GRE/RVR -> versioned recalculation/repair
planner -> current corrected state from fresh evidence
```

Geçmiş kayıtlar sessizce silinmez.

Repair ağırsa background/bounded job olarak uygulanır; UI thread'de full history scan yapılmaz.

---

# 36. Practice generation ile high-stakes generation ayrımı

On-demand AI generation en çok şu alanlarda yararlı olabilir:
- yeni practice variation,
- remediation örneği,
- ekstra açıklama sonrası mini drill,
- low-risk personalized context.

High-stakes mastery/retention/weekly/monthly critical slot'lar varsayılan olarak prevalidated/trusted bank resource'u tercih eder.

On-demand generated item yeterli validation tamamlanmadan sırf kullanıcı bekliyor diye high-stakes slot'a yükseltilmez.

---

# 37. Performance / offline-first implications

AIV validation ağır olabilir.

Kurallar:
- multi-model/reference/executable validation UI thread dışında,
- validation artifacts/cache versioned,
- aynı exact immutable resource version gereksiz yere tekrar validate edilmez,
- freshness/revalidation yalnız ilgili dependency/policy değiştiğinde açılır,
- selection path validation pipeline'ını canlı çalıştırmak zorunda değildir; published bank trust metadata'sını tüketir,
- user session sırasında validator unavailable ise evidence standardı düşürülmez; başka trusted resource/fallback seçilir.

---

# 38. Research / calibration sınırı

4E'de ayrı Research AI kullanılmadı.

Nedeni: bu adım LLM validator için bilimsel accuracy yüzdesi, optimal majority-vote sayısı veya evrensel acceptance threshold'u seçmiyor; güvenlik odaklı deterministic/auditable product-policy tanımlıyor.

Empirik olarak kalibre edilmesi gerekenler ileride:
- AI validator false-accept / false-reject oranları,
- duplicate/family classifier performansı,
- open-response evaluator calibration,
- item difficulty ve exposure davranışı,
- automated review ile manual review trade-off'ları.

Bunlar AŞAMA 14F ve özellikle AŞAMA 18 pilot/calibration'da gerçek benchmark verisiyle ele alınır.

---

# 39. AIV-v0 invariants

1. AI output kendi validation proof'u değildir.
2. AI-generated resource `candidate` başlar.
3. Candidate minimum validation geçmeden user-facing selection'a çıkmaz.
4. Practice-only yanlış content toleransı anlamına gelmez.
5. Validation weighted confidence average değildir; blocking dimension başka başarıyla telafi edilemez.
6. Generator self-review tek başına independent validation değildir.
7. Farklı model çoğunluğu truth garantisi değildir.
8. Wrong answer/rubric blocking integrity failure'dır.
9. Unresolved ambiguity assessment/mastery use'a çıkamaz.
10. Hidden prerequisite learner failure'a dönüştürülemez.
11. Technical Objective için bilinmeyen English gizli prerequisite olamaz.
12. Evidence modality target behavior ile eşleşmelidir.
13. Near duplicate yeni independent family sayılmaz.
14. Family classification uncertain ise diversity credit artırılmaz.
15. Dependency/testlet local dependence korunur.
16. Transfer claim semantic olarak doğrulanmadan transfer diversity sayılmaz.
17. Integrated global pass component evidence'a otomatik yayılmaz.
18. Tek uncalibrated LLM evaluator critical verified evidence üretmez.
19. Execution safety content validation'ın parçasıdır.
20. Version-sensitive resource freshness audit ister.
21. `validated != critical_mastery_eligible`.
22. Trusted-template semantic AI rewrite otomatik trust miras alamaz.
23. Validator disagreement fail-safe olarak promotion'ı durdurur.
24. Invalidated resource yeni selection'a çıkamaz.
25. Confirmed content bug historical evidence repair tetikleyebilir.
26. Content invalidity learner'a ceza değildir.
27. On-demand generation evidence standardını düşürmez.
28. Heavy validation UI thread'de çalışmaz.
29. D-044 granular Objective attribution korunur.
30. GRE/RVR/PRG/DMA/WBA/MCA invariants bypass edilmez.

---

# 40. 4E acceptance criteria

4E PASS için:

1. Generated candidate başlangıç lifecycle/provenance contract'ı tanımlı.
2. Generator/validator ayrımı açık.
3. Schema/reference deterministic validation tanımlı.
4. Technical correctness check tanımlı.
5. Answer/rubric correctness check tanımlı.
6. Ambiguity/multiple-answer behavior tanımlı.
7. Objective/evidence modality fit tanımlı.
8. Prerequisite + forbidden concept + English leakage audit var.
9. Duplicate/near-duplicate/variant-family policy var.
10. Dependency/testlet/context/transfer validation var.
11. Integrated component attribution korunuyor.
12. Evaluator/tool/artifact compatibility tanımlı.
13. Execution/environment safety var.
14. Technology/source freshness var.
15. Semantic risk/use-ceiling promotion policy tanımlı.
16. Practice/low-stakes/standard/critical ayrımı var.
17. Trusted-template inheritance sınırı var.
18. Validator disagreement/uncertainty fail-safe davranışı var.
19. ValidationRun/ValidationRecord audit contract var.
20. Revalidation/invalidation/history repair var.
21. On-demand generation high-stakes shortcut olamıyor.
22. No fake confidence/majority/accuracy threshold.
23. Performance/bounded behavior korunuyor.
24. QAB/GRE/RVR/PRG/DMA/WBA/MCA/D-044 ile uyumlu.
25. AŞAMA 5 knowledge graph'a geçiş için assessment foundation kapanabilir durumda.

---

# 41. Final 4E kararı

**Final model:** `AIV-v0 — AI Assessment Resource Validation`

Canonical akış:

```text
AI-generated candidate
    ↓
schema / refs
    ↓
correctness / answer / rubric / ambiguity
    ↓
target + evidence fit
    ↓
prerequisite + language leakage
    ↓
family / dependency / context / transfer
    ↓
evaluator + tool + artifact + safety
    ↓
freshness
    ↓
risk-based use ceiling
    ↓
validated / trusted / blocked / invalidated
    ↓
QAB-v0 selector
    ↓
Attempt / Artifact
    ↓
normal evidence pipeline
    ↓
GRE / RVR / PRG / Planner
```

AIV-v0 böylece AI-generated assessment content'i hız için kullanırken **yanlış, ambiguous, prerequisite-contaminated veya sahte-diversity üreten bir resource'un mastery-changing evidence'a dönüşmesini engelleyen fail-safe trust katmanını** tamamlar.
