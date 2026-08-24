# Retention / Forgetting Spec — RVR-v0

**Adım:** 2F — Unutma modeli  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-24  
**Final model:** `RVR-v0 — Retention Verification & Risk`

Bu belge GRE-v0 ile kanıtlanmış bir Skill'in günler/haftalar/aylar boyunca korunup korunmadığını nasıl planlayacağımızı ve nasıl yeniden doğrulayacağımızı tanımlar.

Bağlayıcı kaynaklarla birlikte okunur:
- `docs/LEARNING_BEHAVIOR_RULES.md`
- `docs/TOPIC_STATE_MACHINE.md`
- `docs/MASTERY_SIGNALS_SPEC.md`
- `docs/AI_ASSISTANCE_EVIDENCE_SPEC.md`
- `docs/MASTERY_FORMULA_V0.md`
- `docs/2F_RESEARCH_VALIDATION.md`

Ana ilke:
> **Zamanın geçmesi negatif evidence değildir. Zaman yalnız doğrulama ihtiyacını artırır; Skill'in mevcut yeterliliğini yeni evidence belirler.**

İkinci ilke:
> **Mastery ve retention ayrı ama entegre iki eksendir: mastery “kanıtlandı mı?”, retention “bu kanıtı yeniden doğrulama zamanı geldi mi / çelişki var mı?” sorusunu cevaplar.**

---

## 1. Mastery score time-decay yapmaz

GRE-v0 current mastery score sırf `N gün geçti` diye düşmez. Gözlem yokluğu failure değildir; kullanıcı Skill'i başka gerçek bağlamlarda kullanmış olabilir ve complex Skill için kalibre edilmiş bir latent forgetting probability'miz yoktur.

Time yalnız retention scheduler'a sinyal verir.

---

## 2. İki eksenli state modeli

### Mastery ekseni
Canonical kaynak GRE-v0'dır:
```text
mastery_decision:
- not_mastered
- mastered
```
Historical `previously_mastered` bilgisi korunur. Unresolved contradiction/recheck ayrı flag olabilir.

### Retention ekseni
V1:
```text
untracked
fresh
stable
review_due
verification_due
at_risk
```

- `untracked`: Skill henüz mastered değil.
- `fresh`: yeni mastered/re-mastered; ilk delayed review henüz due değil.
- `stable`: anlamlı delayed H0 retention evidence başarılı ve sıradaki review gelecekte.
- `review_due`: review zamanı geldi/geçti, fakat negatif evidence yok. **Unutuldu demek değildir.**
- `verification_due`: daha önce mastered Skill'de clean independent contradiction/failure görüldü; fresh recheck gerekir.
- `at_risk`: birden fazla/karma evidence retention concern'i güçlendirdi fakat henüz kesin remediation gate'i oluşmadı.

`remediation_required` retention state değil, evidence sonucunda açılan learning/remediation durumudur.

`at_risk` sırf overdue süre yüzünden oluşmaz.

---

## 3. Strong retention evidence

Varsayılan strong retention evidence:
- doğru Objective/Skill attribution,
- prerequisite-valid,
- H0,
- direct/primary,
- evaluator `verified`,
- meaningful delay,
- solution exposure yok,
- bağımsız dependency/testlet group,
- mümkünse farklı variant family/context.

Retention başarısı normal GRE-v0 evidence history'sine de girer; ayrı sahte `retention_score` zorunlu değildir.

---

## 4. Skill türüne göre review formatı

### Factual / declarative
- free recall,
- kısa açık cevap,
- recognition yalnız gerektiğinde corroboration.

### Procedural / coding
- H0 code production,
- gerçek küçük artifact,
- compiler/test output verification.

### Debugging
- yeni bug/context,
- cause isolation,
- fix,
- objective-specific explanation.

### Transfer / systems reasoning
- fresh context,
- farklı problem structure,
- integrated task içinde target Skill'in gerçekten gerekli kullanımı.

Kritik production Skill'i flashcard ile retention-pass yapılamaz.

---

## 5. Retention profile

Skill authoring metadata:
```text
retention_profile:
- factual
- standard
- complex

critical_prerequisite: true | false
```

`critical_prerequisite` ayrı overlay'dir.

### V0 initial review defaults
Aşağıdaki sayılar **engineering heuristic / needs calibration**:
```text
initial_review_days_v0:
  factual: 2
  standard: 4
  complex: 7

critical_initial_review_cap_days_v0: 3
```

Critical Skill:
```text
initial_interval = min(profile_initial_interval, 3 days)
```

---

## 6. Interval growth

V1 cold-start'ta tam FSRS/HLR/ACT-R fit edilmez.

Başarılı due/delayed H0 verification sonrası:
```text
next_interval = min(current_interval * growth_factor, max_interval)
```

V0 defaults — **engineering heuristic / needs calibration**:
```text
standard_growth_factor_v0 = 2.0
critical_growth_factor_v0 = 1.6
standard_max_interval_days_v0 = 180
critical_max_interval_days_v0 = 90
```

Bunlar probability/half-life değildir.

İlk failure sonrası recheck PASS ise aggressive growth yapılmaz:
```text
next_interval = current_interval
```

---

## 7. `review_due` davranışı

`now >= next_review_at`:
```text
retention_state = review_due
```

Ama:
- GRE score değişmez,
- Skill otomatik unmastered olmaz,
- Topic otomatik `weakening` olmaz,
- prerequisite otomatik hard-lock olmaz.

Overdue duration planner priority sinyali olabilir; negatif mastery evidence değildir.

---

## 8. Başarılı delayed review

Fresh/different-family H0 review PASS ise:
1. evidence GRE-v0'a eligible evidence olarak girer,
2. `retention_state = stable`,
3. `last_retention_success_at = now`,
4. interval growth uygulanır,
5. `next_review_at` hesaplanır,
6. unresolved retention verification temizlenir.

PASS = Objective rubric/gate başarısıdır; task completion değildir.

---

## 9. İlk delayed failure — hysteresis

Daha önce mastered Skill'de clean H0 direct retention failure:
```text
retention_state = verification_due
```

- historical mastery anında silinmez,
- GRE-v0 contradiction flag açılır,
- fresh/unseen recheck gerekir,
- exact item hemen tekrar edilmez.

V0:
```text
verification_delay_days_v0 = 1
```
Bu yalnız engineering heuristic'tir; kullanıcı katı 24 saat kilidine alınmaz. Planner meaningful separation sağlayan uygun next session'da recheck seçer.

---

## 10. Verification sonucu

### Recheck PASS
- ilk failure slip/noise olabilir,
- `retention_state = stable`,
- yeni positive evidence GRE'ye girer,
- interval büyütülmez; mevcut interval yeni başarı tarihinden tekrar kullanılır.

### Recheck FAIL
- ikinci clean independent failure GRE evidence'ına girer,
- hysteresis override biter,
- Objective/Skill GRE-v0 gates yeniden hesaplanır,
- gate artık geçmiyorsa `remediation_required`,
- hard-prerequisite dependent new work bekleyebilir,
- remediation sonrası yeniden mastery → retention `fresh` + initial interval.

**Elle `GRE score = 0.50` atanmaz.**

### Partial / evaluator uncertainty
- `partial` veya `provisional`,
- `retention_state = at_risk`,
- targeted reinforcement + başka fresh verification,
- yeterli evidence olmadan ağır remediation yok.

---

## 11. Natural reuse

İleri Topic/project/task içindeki doğal kullanım planned review yerine geçebilir.

Strong natural-retention evidence için hepsi gerekir:
1. target Skill çözüm için structurally essential,
2. target davranış H0 user-produced,
3. target Skill ayrı rubric/test/trace ile verified,
4. global task success'tan component success kör çıkarılmıyor,
5. context/family anlamlı farklı,
6. prerequisite-valid,
7. solution exposure yok.

Bu koşullar sağlanır ve natural reuse **review due/overdue iken** gerçekleşirse planned review'u karşılayabilir.

Due tarihinden çok önceki natural reuse:
- GRE için strong evidence olabilir,
- reinforcement olarak loglanır,
- fakat v0'da review clock'u otomatik ileri taşımak zorunda değildir.

---

## 12. Multi-Skill integrated task

Tek task birden fazla Skill'e retention evidence üretebilir; her Skill için ayrı doğrulanır:
- gerekli miydi,
- observable miydi,
- user-produced mı,
- independent mı,
- rubric/runner/trace target Skill'i doğruluyor mu?

Global `project passed` bütün tagged Skills'i refresh etmez.

**Automatic cluster/descendant refresh yoktur.**

---

## 13. Same-item / same-family

- Exact solution-exposed repeat → strong retention evidence değil.
- Near variant → complex/critical Skill için tek başına strong gate değil; pattern-memory riski.
- Different family/context → varsayılan strong retention tercihi.
- Transfer task → attribution doğruysa en güçlü retention evidence türlerinden biri.

Factual Objective için near-repeat daha fazla değer taşıyabilir; Objective-specific authoring uygulanır.

---

## 14. Critical prerequisite policy

### Critical + review_due
- hard-lock yok,
- planner priority artar,
- mümkünse dependent yeni task öncesi kısa verification interleave edilir,
- yalnız zaman geçti diye başarısız sayılmaz.

### Critical + verification_due
Gerçek negative evidence vardır. D-031 gereği unresolved critical recheck:
```text
prerequisite_ready = false
```
Dependent **yeni** Topic/task recheck'e kadar bekleyebilir; bağımsız dallar devam eder.

### Critical + confirmed GRE gate failure/remediation
Dependent branch bekler; targeted remediation gerekir.
Started Topic geriye dönüp `locked` olmaz.

---

## 15. Topic `weakening` entegrasyonu

`review_due` tek başına Topic'i `weakening` yapmaz.

Topic weakening için **evidence-backed retention concern** gerekir. Örnek:
- required Skill `at_risk`,
- birden fazla required Skill'de ayrı unresolved contradiction,
- fresh recheck partial/ambiguous + corroborating weakness,
- natural-use failure + ek doğrulama concern'i.

Tek clean failure çoğunlukla Skill-level `verification_due` kalır; bütün Topic otomatik düşmez.

Fresh verification success concern'i temizler. Repeated clean failure GRE gate'i düşürürse Topic `remediation_required` olabilir.

---

## 16. Missed days / overdue backlog

7/30/60+ gün yoklukta eski review'lar tek tek task debt olarak taşınmaz.

Dönüşte planner current state'ten aday seçer. Priority input'ları:
- criticality,
- hard-prerequisite centrality,
- actual `verification_due/at_risk`,
- overdue duration,
- upcoming dependent use,
- last strong retention evidence,
- bir integrated task ile birden fazla Skill'i gerçekten ayrı doğrulayabilme.

Representative integrated verification kullanılabilir; root Skill başarısı komşu Skills'e otomatik refresh yaymaz.

2F sabit `8–10 review/day` koymaz. Günlük capacity 3A–3F'de belirlenir. **Backlog dump yasaktır.**

---

## 17. Explainability reason codes

```text
FIRST_DELAYED_REVIEW
REVIEW_DUE
CRITICAL_REVIEW_DUE
NATURAL_REUSE_VERIFIED
RETENTION_FAILURE_FIRST
RETENTION_RECHECK_PASS
RETENTION_RECHECK_FAIL
RETENTION_AT_RISK
REMEDIATION_AFTER_RETENTION_FAILURE
OVERDUE_REPRESENTATIVE_CHECK
DEPENDENT_PREREQ_VERIFICATION_REQUIRED
```

UI sade açıklama gösterebilir; formül/probability iddiası yapmaz.

---

## 18. Compact sufficient state

Kesin DB schema 8C'de. Behavior-level state:
```text
SkillRetentionState
- skill_id
- retention_model_version
- retention_profile
- critical_prerequisite
- state
- current_interval_days
- next_review_at
- last_strong_retention_evidence_at
- last_retention_evidence_id
- successful_delayed_review_count
- unresolved_verification_id
- at_risk_reason_codes
- last_natural_reuse_at
```

History EvidenceEvent'te kalır; normal planner path'i tüm history'yi taramaz.

---

## 19. Performance

D-028:
- `next_review_at` indexed due query,
- O(1)-benzeri state transition,
- normal planner path'inde full event scan yok,
- cihazda heavy model fitting zorunlu değil,
- natural attribution task evaluation sırasında üretilir.

---

## 20. V0 config / evidence status

| Parametre | V0 | Statü |
|---|---:|---|
| factual initial | 2 gün | engineering heuristic / calibrate |
| standard initial | 4 gün | engineering heuristic / calibrate |
| complex initial | 7 gün | engineering heuristic / calibrate |
| critical initial cap | 3 gün | engineering heuristic / calibrate |
| standard growth | ×2.0 | engineering heuristic / calibrate |
| critical growth | ×1.6 | engineering heuristic / calibrate |
| standard max | 180 gün | engineering heuristic / calibrate |
| critical max | 90 gün | engineering heuristic / calibrate |
| verification separation | default 1 gün | engineering heuristic / calibrate |
| mastery score time decay | **yok** | architectural decision / evidence-aligned |
| review_due hard block | **yok** | architectural decision |
| critical verification_due prerequisite-ready | **false** | D-031 integration |
| auto cluster refresh | **yok** | false-positive guardrail |

---

## 21. Pilot calibration — 17C

Ölçülecekler:
- delayed H0 success by profile × interval,
- first-failure → recheck-pass,
- recheck-fail → remediation,
- dependent task'te critical prerequisite failure,
- unnecessary review / repeated easy success,
- natural reuse → later independent verification agreement,
- review minutes / total study minutes,
- long absence sonrası recovery time,
- backlog pressure,
- critical vs standard failure rate,
- interval growth sonrası success/failure.

Hedef başarı bandı şimdiden `75–85%` diye kilitlenmez; retention, practice cost ve false-positive prerequisite riskine göre pilotta seçilir.

---

## 22. Daha sonra benchmark edilebilecek modeller

Yeterli log oluştuğunda FSRS-benzeri D/S/R, HLR, DAS3H/LKT family, ACT-R-inspired veya learned interval/risk model RVR-v0 ile offline benchmark edilebilir.

Yeni model yalnız held-out delayed prediction, false-positive prerequisite risk, review burden, explainability ve mobile cost açısından anlamlı iyileşirse canonical motoru değiştirmelidir.

---

## 23. 2F acceptance criteria

2F tamamdır çünkü:
- mastery vs retention ayrıldı,
- time-based score decay reddedildi,
- retention state machine tanımlandı,
- initial interval/growth v0 config tanımlandı ve heuristic etiketlendi,
- successful/failed delayed review tanımlandı,
- single-failure hysteresis GRE-v0 ile entegre edildi,
- natural reuse strict attribution gate ile tanımlandı,
- same-family/transfer davranışı tanımlandı,
- critical prerequisite behavior tanımlandı,
- Topic weakening entegrasyonu tanımlandı,
- missed-days/backlog davranışı tanımlandı,
- no cluster refresh/no manual GRE reset guardrail'leri eklendi,
- bounded/incremental mobile state tanımlandı,
- pilot calibration planı tanımlandı.

---

## 24. Sonraki adım

AŞAMA 2 tamamlanır.

Sonraki canonical adım: **3A — Günlük kapasite**.

3A başlamadan D-024 gereği yeni PRE-STEP GitHub refresh yapılmalıdır.
