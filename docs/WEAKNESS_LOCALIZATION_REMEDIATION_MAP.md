# WLRM-v0 — Weakness Localization & Remediation Map

**Adım:** 6G  
**Karar:** D-061  
**Durum:** Internal behavior authoring + deterministic QA complete; independent external Research QA pending 6H  
**Canonical dataset:** `curriculum/decomposition/6g_weakness_remediation/`

## 1. Amaç ve kapsam
WLRM-v0, 6C–6F ile kabul edilmiş curriculum registry'si üzerinde learner weakness sinyalinin hangi Objective/Skill'e yazılacağını ve hangi remediation yoluna dönüştürüleceğini formalize eder. 6G yeni curriculum Skill/Objective üretmez ve prerequisite graph'ını değiştirmez.

## 2. Exact coverage
- 4 accepted source package
- 543 Skill weakness profile
- 590 Objective weakness profile
- 590 Objective-specific remediation route
- 12 deterministic failure-attribution rule
- 15 remediation strategy family
- 6 LearningNeed/planner mapping
- 6 existing source remediation tag normalized into the strategy bridge
- 0 blocking / 4 non-blocking review

## 3. Localization invariant
Primary intervention unit Objective'tir; Skill durumu GRE-v0 required/critical Objective gates üzerinden yeniden hesaplanır. Topic/Module/Domain yalnız derived summary/orchestration katmanıdır.

```text
Attempt / Artifact
→ validity + prerequisite + attribution
→ Objective-level weakness signal
→ GRE/RVR verification or remediation state
→ Skill gate recompute
→ branch-local prerequisite effect
→ LearningNeed
→ targeted remediation candidate
→ fresh evidence
→ recompute / close
```

`Bir soru yanlış → Skill başarısız → Topic/Domain reset` canonical davranış değildir.

## 4. Failure attribution safety
- Invalid/ambiguous item, invalid evaluator veya attribution problemi target negative evidence yazamaz.
- Missing/unready prerequisite nedeniyle contaminated attempt target Skill'i cezalandıramaz; prerequisite need açılır ve task/content metadata QA'ya gidebilir.
- Deferred/unattempted task negative evidence değildir.
- Target davranışı gözlenmesini engelleyen environment/tool failure, tool operasyonu target capability değilse learner weakness sayılmaz.
- H1–H4 assisted failure ile provisional/partial evaluator sonucu en fazla `hypothesis` üretir; tek başına confirmed remediation açamaz.
- Mastery öncesi clean H0 + direct + verified + prerequisite-valid negative evidence normal GRE recent window'a girer ve supported weakness üretebilir.
- Mastered Objective/Skill sonrası ilk clean contradiction `verification_due` açar; mastery anında silinmez.
- Fresh/unseen independent recheck tekrar başarısız olursa GRE/RVR yeniden hesaplanır ve gate failure varsa `remediation_required` açılabilir.
- `review_due` tek başına weakness değildir.

## 5. Weakness / misconception state
Generic weakness signal lifecycle:
`none → hypothesis → supported → confirmed → resolved`.

Learner-specific misconception memory curriculum Skill identity'sinden ayrıdır. AI bir misconception hypothesis önerebilir; canonical mastery veya confirmed misconception state'i yalnız LLM kanaatiyle yazılamaz.

## 6. Remediation strategies
WLRM-v0 15 strategy family kullanır: targeted reteach, worked-example reconstruction, micro-drill, state/trace reconstruction, debug localization, prerequisite refresh, failure-scenario replay, measurement replay, production retry, explanation rebuild, misconception contrast, transfer retest, retrieval reinforcement, tool/workflow rehearsal ve fresh independent recheck.

Per-Objective route, accepted `remediation_tags`, Objective evidence profile, required direct evidence ve transfer/artifact gereksinimlerinden deterministic candidate strategy seti türetir.

## 7. Remediation closure
Remediation task'ının tamamlanması remediation'ı kapatmaz. Closure için target Objective'e uygun fresh/context-diverse, H0, direct, verified ve prerequisite-valid evidence GRE/RVR pipeline'ından geçmelidir. Objective transfer veya user-authored artifact gerektiriyorsa closure recheck'i de bunu korur.

Exact same-item immediate repeat güçlü closure evidence değildir. Manual mastery score override yasaktır.

## 8. Planner / prerequisite integration
- weakness hypothesis → `weakness_detected` / diagnose-reinforce-remediate,
- supported pre-mastery weakness → P1 repair path,
- `verification_due` → P1 fresh verification,
- confirmed remediation → varsayılan P1; yalnız gerçek critical/hard-prerequisite integrity blocker varsa P0,
- prerequisite gap → target yerine prerequisite repair need,
- review_due only → retention need, remediation değil.

Confirmed prerequisite weakness yalnız gerçekten dependent hard-prerequisite branch'i etkiler; bağımsız branch'ler devam eder.

## 9. Broad-overreaction guards
- Domain reset yok.
- Topic state weakness'i bütün linked Skills'e geri yaymaz.
- Tek Objective failure GRE gates'i bypass ederek whole-Skill fail flag yazmaz.
- Global project PASS/FAIL bütün component Objectives'e broadcast edilmez.
- Technical English gizli/global technical weakness kaynağı yapılamaz.

## 10. QA
- 6C Foundations regression: PASS
- 6D Systems regression: PASS
- 6E GPU/ML/Inference regression: PASS
- 6F Professional Engineering regression: PASS
- Independent 6G overlay validator: PASS
- exact accepted registry coverage: PASS
- 0 blocking review
- package result: `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`

## 11. Open handoff
- `review.6g.external_behavior_coverage` → 6H independent Research AI
- `review.6g.misconception_taxonomy_expansion` → 14B
- `review.6g.calibration` → 18C
- `review.6g.content_realization` → 15/20

6H external Research QA WLRM-v0 dahil AŞAMA 6 full-route coverage/prerequisite/current-industry validation için zorunludur. 6G internal QA, 6H'nin yerine geçmez; learner publication hâlâ pending'dir.
