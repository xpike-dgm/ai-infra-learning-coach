# HANDOFF STATE — Güncel Proje Durumu

**Son güncelleme:** 2026-08-27
Repo: `xpike-dgm/ai-infra-learning-coach`

## 0. Zorunlu protokol — D-024 / D-027 / D-050
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

> Her numaralı adım başlamadan PRE-STEP GitHub refresh; bittikten sonra living-memory ALWAYS-CHECK seti + ana çıktı + repo-wide stale-reference scan zorunludur.

D-050 sonrası özellikle `PROJECT_CONTEXT.md`, `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG` ve `DECISIONS` her numaralı step sonunda kontrol edilir. Stable specs active-step state'i kopyalamaz.

## 1. Ürün ve uzun vadeli hedef
Tek kullanıcı için, sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering yolunda günlük yöneten, uygulama içinde öğreten/uygulatan ve yalnız kanıtlanmış öğrenmeyi ilerleme sayan adaptif Android öğrenme koçu.

D-041: full curriculum 4+ yıl veya daha uzun sürebilir; takvim readiness gate değildir. Final hedef verified engineering capability + retention + debugging + transfer + performance + integrated project/capstone evidence'dır.

Canonical target: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## 2. Güncel ana rota

**Technical English (parallel) → Python → C → Linux + Git + Shell → DS&A foundations → Modern C++ → Computer Architecture → OS + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases → Containers / Cloud / Observability → Performance Engineering / Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM/SGLang/TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

Bu sıra roadmap summary'dir; runtime linear takvim değildir.

## 3. Bağlayıcı güncel kararlar

- **D-042:** Python resmi common foundation; C/C++ yerine geçmez.
- **D-043:** standalone specialization-stage yorumu geri çekildi; canonical değil.
- **D-044:** AŞAMA 6 full route'u `Domain → Module → Topic → Skill → Learning Objective` seviyesine bölecek; weakness/remediation Skill/Objective seviyesinde lokalize edilir.
- **D-045:** WBA-v0 Weekly Blueprint Assessment.
- **D-046:** MCA-v0 Monthly Capability Assessment.
- **D-047:** QAB-v0 Trusted Assessment Resource Bank.
- **D-048:** AIV-v0 AI Assessment Resource Validation.
- **D-049:** PDM-v0 Professional Domain Backbone.
- **D-050:** living-memory sync + repo-wide stale-reference audit zorunlu; exact file-role matrix `PROJECT_MEMORY_PROTOCOL.md` içinde.
- **D-051:** KGC-v0 Versioned Curriculum Knowledge Graph Contract; 5B tamamlandı.
- **D-052:** FBB-v0 V1 Foundation Backbone; 5C tamamlandı.
- **D-053:** GQA-v0 Foundation Graph Architecture QA; 5D corrective patch sonrası PASS.
- **D-054:** GNS-v0 Granularity & Naming Standard; 6A semantic decomposition/ID contract tamamlandı.
- **D-055:** Ana manager/koordinatör rolü local çalışan agent'a devredilebilir; takeover bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; project contracts/state değişmez.
- **D-056:** FRDB-v0 Full-Route Decomposition Blueprint; 6C–6F ortak authoring package + QA contract'ı tamamlandı.
- **D-057:** FDM-v0 Foundations Detailed Map; D01–D05 package + FBB seed mapping + internal graph QA tamamlandı.
- **D-058:** SDM-v0 Systems Detailed Map; D06–D13 package + 6C cross-package reuse + birleşik hard-graph QA tamamlandı.

## 4. D-049 / 5A final özeti

Canonical: `docs/CURRICULUM_DOMAIN_MAP.md`.

PDM-v0:
- 23 ana route family high-level envelope olarak korunur.
- Domainler takvim veya mastery atomu değildir.
- Technical English bütün rota boyunca parallel track'tir.
- Python + C + Linux/Git/Shell early complementary foundations'tır; katı seri değildir.
- DS&A common supporting foundation'dır.
- Systems core: Modern C++, Computer Architecture, OS/Memory, Concurrency, Networking.
- Distributed/platform core: Distributed Systems + Storage/DB + Containers/Cloud/Observability.
- Performance Engineering cross-cutting capability'dir; advanced profiling Architecture/OS/GPU/inference boyunca büyür.
- Accelerator core: GPU Architecture → CUDA/Triton.
- ML/Transformer inference için supporting domain'dir; generic ML-research specialization değildir.
- LLM Inference, Serving Systems ve KV/Batching/Scheduling/Quantization ayrı fakat bağlı family'lerdir.
- Multi-GPU/NCCL/RDMA networking + distributed + GPU foundations'in advanced convergence katmanıdır.
- AI/GPU Infrastructure final target integration domainidir.
- Open Source / engineering practice / projects / capstones finalde aniden başlamaz; route boyunca artan professional evidence layer'dır.
- Security/reliability/observability ve gerekli math/numerical knowledge hidden prerequisite bırakılmaz; ilgili domainlere explicit capability olarak dağıtılır.
- Tool/vendor isimleri stable systems concept yerine geçmez; version/freshness metadata ile ayrılır.
- Domain-level authoring relations runtime hard-lock değildir; runtime canonical prerequisite Skill→Skill PRG-v0'dır.

5A ayrı Research AI kullanmadı; mevcut kabul edilmiş professional route'u formalize etti. Bağımsız full coverage/current-industry/prerequisite Research QA AŞAMA 6H'de zorunlu planlanmıştır.

## 5. D-050 repository hygiene audit sonucu

2026-08-25'te repo içindeki dosya seti tek tek audit edildi.

Yapılan kalıcı düzeltmeler:
- `PROJECT_CONTEXT.md` eski 4B state'inden güncel 5B state'ine taşındı ve mandatory living snapshot rolü kilitlendi.
- `PROJECT_MASTER_CONTEXT.md` ve README'den volatile aktif-step duplication kaldırıldı.
- `docs/TODO.md` eski AŞAMA 0 / 3-year plan nedeniyle duplicate+stale olduğu için silindi.
- `docs/LEARNING_ENGINE.md` davranış kaynağı olmaktan çıkarılıp explicit `HISTORICAL / SUPERSEDED` pointer'a dönüştürüldü; canonical kaynak `LEARNING_ENGINE_SPEC.md` ve sonraki specs'tir.
- `docs/ENGLISH_TRACK.md` exact A0→B2 hedefi kilitlenmiş gibi görünmesin diye `NON-CANONICAL SEED NOTES` olarak işaretlendi.
- `ENGLISH_FOUNDATION_RULES.md` D-044 sonrası AŞAMA 6C / AŞAMA 7 ayrımına göre düzeltildi.
- `PROJECT_MEMORY_PROTOCOL` ve `AI_AGENT_WORKFLOW` D-050 mandatory sync/stale-scan kuralıyla güçlendirildi.
- Repo-wide stage/file/status drift taraması bu bakım turunun parçasıdır.

Bu cleanup **numaralı 5B adımını yürütmedi** ve daha önce kabul edilmiş aşama/spec davranışlarını değiştirmedi.

## 6. D-051 / 5B final özeti

Canonical: `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.

KGC-v0 organization (`Domain → Module → Topic`) ile capability/evidence (`Skill → Learning Objective`) identity'sini ayırır; Topic↔Skill many-to-many reuse, Skill→Skill hard/soft prerequisites, scope-relative requirements, evidence/retention/remediation/English/professional attribution, provenance/freshness ve conservative graph migration contract'ını tanımlar.

## 7. D-052 / 5C final özeti

Canonical: `docs/V1_FOUNDATION_BACKBONE.md`.

FBB-v0:
- V1 “8–12 hafta” ifadesini calendar gate değil scope-equivalent content envelope olarak kullanır,
- zero-entry Computer/Programming bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English seed subgraph'ını tanımlar,
- Technical/English/professional-workflow scope'larını ayırır; English global technical blocker değildir,
- KGC-v0 uyumlu Skill/Objective logical IDs üretir fakat 6A/6C öncesi lifecycle `authoring_seed / not_learner_published` kalır,
- shared mental-model Skills ile language-specific production Skills'i ayırır,
- initial hard/soft Skill prerequisite edges PRG-v0 semantics ile tanımlar,
- GRE/QAB/RVR uyumlu evidence/retention/diagnostic/remediation anchor'ları verir,
- AŞAMA 15 production authoring ihtiyaçlarını ve 5D graph-QA fixture'larını tanımlar.

5C ayrı Research AI kullanmadı; 6H external coverage/current-industry/prerequisite Research QA zorunlu kalır.

## 8. D-053 / 5D final özeti

Canonical: `docs/GRAPH_ARCHITECTURE_QA.md`.

GQA-v0:
- initial FBB-v0 audit'inde explicit TopicSkillLink eksikliği ve invalid `reason_kind=supporting` bulundu,
- TopicSkillLink seed matrix FBB'ye eklendi,
- reason kinds KGC controlled vocabulary'ye normalize edildi,
- Python/data/error/module/file, trace/debug, professional debug explanation ve C storage/lifetime hidden-prerequisite riskleri minimal hard edges ile düzeltildi,
- corrected hard graph DAG; self/dangling/conflicting edge yok,
- shared Skill reuse TopicSkillLink ile, clone mastery state yok,
- English global technical hard gate yok,
- F5D-01..F5D-10 fixtures PASS,
- FBB authoring_seed olarak kalır; 6A/6C/6H öncesi learner-published değildir.

5D external Research AI kullanmadı; full coverage/current-industry/prerequisite independent Research QA 6H'de zorunlu kalır.

## 9. D-054 / 6A final özeti

Canonical: `docs/GRANULARITY_NAMING_STANDARD.md`.

GNS-v0:
- organization (`Domain/Module/Topic`) ile capability/evidence (`Skill/Objective`) granularity sınırını operational hale getirir,
- yeni Skill kararını independent evidence + remediation + prerequisite + reuse ayrımına bağlar,
- under/over-fragmentation guard'larını tanımlar,
- shared mental-model capability ile language/tool-specific production capability ayrımını standartlaştırır,
- Objective'i exactly-one-Skill altında atomic observable evidence target olarak sınırlar,
- logical ID formatını lowercase ASCII dotted namespace + snake_case segment şeklinde; locale/order/version bağımsız olarak kilitler,
- week/stage/release/band/difficulty/role bilgisinin logical ID'ye gömülmesini yasaklar,
- display/localization/alias değişimini identity değişiminden ayırır,
- split/merge/re-home/objective-move işlemlerini KGC migration semantics'e bağlar,
- FBB authoring seed'leri için ratify/normalize/split/merge/rehome/deprecate/review status contract'ı tanımlar,
- 6B ortak decomposition authoring template'ine zorunlu alanları devreder.

6A external Research AI kullanmadı; 6H independent Research AI zorunluluğu korunur.

## 9.1 D-055 / Local manager transition

Kullanıcı ana yönetici rolünü local çalışan agent'a devretme kararı verdi.
- Local manager D-024/D-027/D-050 protokolüne aynen uyar.
- İlk takeover: `AGENTS.md` → `LOCAL_MANAGER_HANDOFF` → `START_HERE` → `PROJECT_MEMORY_PROTOCOL` → bütün Markdown repo audit/read.
- Research/Coding/Test separation korunur.
- Transition numbered step değildir.
- Transition anında canonical execution değişmedi; daha sonra kullanıcı onayı ve fresh PRE/POST protokolüyle 6B tamamlandı.

## 9.2 D-056 / 6B final özeti

Canonical: `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md`.

FRDB-v0:
- 23 route family'yi 6C Foundations, 6D Systems, 6E GPU/ML/Inference ve 6F Professional Engineering package'larına atar,
- UTF-8 YAML machine-readable authoring package ve logical collection setini tanımlar,
- organization, Skill, Objective, TopicSkillLink, prerequisite, requirement ve attribution row contract'larını standardize eder,
- GNS-v0 granularity review + unresolved review queue kullanır,
- cross-package duplicate resolver ve canonical shared Skill reuse workflow'unu kilitler,
- FBB seed mapping/ratification contract'ını 6C'ye bağlar,
- evidence/depth, remediation, retention, diagnostic, provenance ve freshness metadata'sını taşır,
- 6G weakness/remediation ve 6H independent Research QA handoff'unu açık tutar,
- physical DB schema, production content veya gerçek node listesi değildir.

## 9.3 D-057 / 6C final özeti

Canonical summary: `docs/FOUNDATIONS_DETAILED_MAP.md`.

Canonical dataset: `curriculum/decomposition/6c_foundations/`.

FDM-v0:
- D01–D05 için 5 Domain / 14 Module / 46 Topic,
- 132 Skill / 137 Objective / 145 TopicSkillLink,
- 200 hard/soft prerequisite edge; hard graph DAG,
- FBB 41/41 Skill + 47/47 Objective seed mapping,
- 33 Skill ratify + 8 broad Skill split,
- Technical English global-gate guard,
- deterministic generator + independent package validator,
- internal result `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`, 0 blocking,
- 6C/6D completion anında 6H external Research QA pending idi; D-062 ile daha sonra external validation tamamlandı; production content yine AŞAMA 15/20 kapsamındadır.

## 9.4 D-058 / 6D final özeti

Canonical summary: `docs/SYSTEMS_DETAILED_MAP.md`.

Canonical dataset: `curriculum/decomposition/6d_systems/`.

SDM-v0:
- D06–D13 için 8 Domain / 21 Module / 64 Topic,
- 192 Skill / 207 Objective / 224 TopicSkillLink,
- 313 prerequisite edge (259 hard / 54 soft); FRDB Pass B ile 52 scaffold edge soft'a indirildi,
- 6C + 6D birleşik hard graph DAG 324/324 node,
- 43 accepted 6C Skill clone'lanmadan reuse edildi; 56 cross-package edge `cross_package_ref` ile etiketli,
- 146 shared stable systems capability / 46 language veya tool-specific capability,
- 13 fast-moving + 21 version-sensitive Skill freshness ve technology dependency metadata'sı taşır,
- Technical English global-gate guard ve branch isolation korundu,
- deterministic generator + bağımsız package validator; her ikisi de PASS,
- internal result `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`, 0 blocking,
- 6C/6D completion anında 6H external Research QA pending idi; D-062 ile daha sonra external validation tamamlandı; production content yine AŞAMA 15/20 kapsamındadır.

## 9.6 D-060 / 6F final özeti

Canonical summary: `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`.  
Canonical dataset: `curriculum/decomposition/6f_professional_engineering/`.

PEM-v0:
- D23 için 1 Domain / 9 Module / 27 Topic,
- 76 Skill / 87 Objective / 110 TopicSkillLink,
- 138 edge (137 hard / 1 soft),
- 25 prior Skill canonical ID ile reuse,
- testing/build/debug/profiling/Git-PR-review/design-doc/ops/security/OSS/project-capstone overlay granularlaştırıldı,
- 6D ve 6E professional-overlay review'ları resolved,
- internal QA PASS_WITH_OPEN_NON_BLOCKING_REVIEWS; 6F completion anında external Research QA 6H'ye pending idi; D-062 ile tamamlandı.

## 10. Tamamlanan aşamalar

- AŞAMA 1 ✅
- AŞAMA 2 ✅ — GRE-v0 / RVR-v0
- AŞAMA 3 ✅ — adaptive planner; 16/16 scenarios, 20/20 invariants PASS
- AŞAMA 4 ✅ — DMA-v0 / WBA-v0 / MCA-v0 / QAB-v0 / AIV-v0
- AŞAMA 5 ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6 ✅ — GNS-v0 / FRDB-v0 / FDM-v0 / SDM-v0 / GIM-v0 / PEM-v0 / WLRM-v0 / S6ERQA-v0
- AŞAMA 7A ✅ — EED-v0 / D-063
- AŞAMA 7B ✅ — TECP-v0 / D-064
- AŞAMA 7C ✅ — DECP-v0 / D-065
- AŞAMA 7D ✅ — TEIP-v0 / D-066
- AŞAMA 7E 🟡 active-not-executed
- 7E–20 ⬜

## 11. Güncel kesin konum

**Son tamamlanan:** `7D — TEIP-v0 / D-066`  
**Aktif:** `7E — English mastery`  
**7E henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**

## 12. D-061 / 6G final özeti — external QA sonrası

Canonical summary: `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`.  
Canonical dataset: `curriculum/decomposition/6g_weakness_remediation/`.

WLRM-v0 final Stage 6 registry'de:
- 549 Skill / 608 Objective exact coverage,
- 608 Objective-specific remediation route,
- invalid/prerequisite-contaminated false-negative guard,
- assisted/provisional evidence ceiling,
- first post-mastery contradiction `verification_due`,
- broad reset / project broadcast guard,
- fresh H0/direct/verified closure,
- 6H ile guidance fading + mastered-target reverification-first + AI-scaffold-not-closure guard'ları eklendi.

## 13. D-062 / 6H final özeti

Canonical: `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`.  
Reconciliation: `research/6h_external_research_ai_report.md`.  
Dataset/QA: `curriculum/decomposition/6h_research_qa/`.

S6ERQA-v0:
- üç bağımsız evaluator başlangıç snapshot'ına `PASS WITH REQUIRED CHANGES` verdi,
- canonical reconciliation 6 stable capability ekledi: NUMA locality/affinity, CUDA async data pipeline, speculative decoding trade-off, prefill/decode disaggregation, MoE routing, expert parallel sharding,
- tool/vendor/model-specific fast-moving ayrıntılar stable Skill şişirmeden version-scoped Objective/example olarak tutuldu,
- final Stage 6 = 23 route / 549 Skill / 608 Objective / 950 prerequisite edge,
- combined hard graph 549/549 DAG,
- WLRM 549/608 exact coverage,
- 10/10 6H-owned review resolved,
- `tools/validate_6h_external_reconciliation.py` PASS.

## 14. D-063 / 7A final özeti

Canonical: `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`.  
Blueprint/QA: `curriculum/english/7a_entry_diagnostic/`.  
Research: `research/7a_english_entry_diagnostic_research.md`.

EED-v0:
- final D01 registry'deki 15 English Skill + 15 Objective exact diagnostic profile scope,
- canonical 16 English hard prerequisite edge ve 15/15 DAG,
- 15 claim/task family,
- prerequisite-aware adaptive probing + pause/resume,
- self-report/certificate/confidence non-evidence,
- diagnostic mastery standardı GRE-v0/VDW-v0'dan daha kolay değil,
- specialist technical knowledge ve unknown grammar/vocabulary hidden prerequisite olamaz,
- invalid/prerequisite-unresolved attempt target negative evidence yazmaz,
- dependent branch failure broadcast yok,
- CEFR final level 7A'da atanmaz; 7B'ye pending,
- final Stage 6 regression + `tools/validate_english_entry_diagnostic.py` PASS.

## 15. D-064 / 7B final özeti

Canonical: `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`.  
Alignment/QA: `curriculum/english/7b_cefr_progression/`.  
Research: `research/7b_technical_english_cefr_research.md`.

TECP-v0:
- 15/15 D01 Skill context-only CEFR progression alignment,
- 5 A1 / 5 A2 / 5 B1 base anchor,
- 16/16 English hard edge band-monotonic,
- 4 bounded B2+ professional evidence-depth extension,
- 10 controlled CEFR scale-family ref,
- current D01 text-first; no general-English/official/certification overclaim,
- CEFR metadata != mastery state,
- no new Skill/Objective/prerequisite edge,
- `review.6c.english.cefr_alignment` resolved,
- EED-v0 historical `pending_7B` handoff marker preserved,
- Stage 6 + EED-v0 + 7B validator PASS.

## 16. D-065 / 7C final özeti

Canonical: `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`.  
Policy/QA: `curriculum/english/7c_daily_component/`.  
Research: `research/7c_daily_english_component_research.md`.

DECP-v0:
- common daily capacity; separate English budget yok,
- active-study-day + open/eligible/safe English need → en az bir candidate,
- fixed minute/percentage/completion/streak/debt yok,
- normal parallel need P3 + PBR balance/starvation `none/watch/promote`,
- state-driven task mix; RVR spacing ownership,
- localized remediation + fresh variant + feedback evidence guards,
- B2+ TECP-v0 semantic boundary korunuyor,
- technical integration 7D'ye, learner-facing English mastery/CEFR behavior 7E'ye deferred,
- independent 7C QA PASS.

## 17. D-066 / 7D final özeti

Canonical: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`.  
Policy/QA: `curriculum/english/7d_technical_integration/`.  
Research: `research/7d_technical_english_integration_research.md`.

TEIP-v0:
- 15 canonical English Skill identity unchanged,
- exactly 4 construct-aware integration mode,
- technical/English targets + prerequisites + evidence separate,
- English/CEFR global technical gate forbidden,
- dual-target overall PASS broadcast forbidden,
- bidirectional language/technical-context contamination guards,
- evidence/task-validity driven reversible scaffold; no fixed ratio/day quota,
- authentic resources + translation/gloss/AI support validity guards,
- 15 safety fixture + independent 49-check QA PASS.

## 18. 7E handoff

7E — English mastery; learner-facing granular English mastery/profile/CEFR summary behavior, B2+ evidence presentation ve English-specific mastery/remediation display semantics'ini EED/TECP/DECP/TEIP + GRE/RVR/WLRM contracts üzerinde kesinleştirecektir. 7E fresh PRE + kullanıcı açık onayı olmadan yürütülmez.
