# PEM-v0 — Professional Engineering / Projects Detailed Map

**Adım:** 6F  
**Karar:** D-060  
**Durum:** Internal authoring + deterministic QA complete; external Research QA pending 6H  
**Canonical dataset:** `curriculum/decomposition/6f_professional_engineering/`

## Kapsam
D23 Open Source Contributions + Real Large Projects + Professional Capstones ayrıntılandırıldı. Cross-cutting professional layer; repository/source reading, issue reproduction/change planning, Git/branch/PR/code review, testing/CI quality, build/release/reproducibility, debugging, profiling/benchmark reporting, design docs/RFC, observability/reliability/incident/postmortem, security/operational safety, OSS workflow ve integrated project/capstone evidence davranışlarını kapsar.

## Final sayılar
- 1 Domain / 9 Module / 27 Topic
- 76 Skill / 87 Objective / 110 TopicSkillLink
- 138 prerequisite edge: 137 hard / 1 soft
- 36 cross-package prerequisite edge
- 25 prior Skill reuse: 8 Foundation + 12 Systems + 5 GPU/ML/Inference
- 152 capability requirement
- 181 professional attribution
- 38 project/capstone attribution

## Professional overlay sonucu
- Existing D01–D22 technical/testing/build/debug/profiling/observability/reliability capability'leri clone edilmedi; canonical Skill ID reuse edildi.
- 6C `project.foundation.reproducible_cli_tool`, 6D `project.systems.observable_networked_service` ve 6E `project.gpu_inference.integrated_serving_stack` D23 professional workflow Skill'leriyle augment edildi.
- Yeni `project.professional.open_source_contribution` familyası tanımlandı.
- Yeni `capstone.professional.ai_infrastructure_system` final integrated capstone familyası tanımlandı; professional D23 behavior ile accepted technical Skill evidence aynı capstone içinde separately-observable component olarak bağlandı.
- Integrated project/capstone PASS bütün tagged Objective'lere otomatik mastery vermez; component evidence ayrı kalır.

## Review reconciliation
- `review.6d.professional_overlay_reconciliation` 6F tarafından resolved edildi.
- `review.6e.professional_overlay_reconciliation` 6F tarafından resolved edildi.
- 6D'de 2, 6E'de 3 external/freshness/math non-blocking review açık kalır ve 6H'ye aittir.

## QA
- 6C regression validator: PASS
- 6D regression validator: PASS
- 6E regression validator: PASS
- Independent 6F package validator: PASS
- 6C+6D+6E+6F combined hard graph: DAG (validator sonucu)
- Package result: `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`
- Open 6F reviews: 0 blocking / 3 non-blocking
- Independent external Research QA 6H'de zorunlu; package henüz externally validated / learner-published değildir.
