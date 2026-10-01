# HANDOFF STATE — Güncel Proje Durumu

**Son güncelleme:** 2026-08-29
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
- **D-070:** TRUX-v0 Task Runner & Daily Working Flow UX; 8C tamamlandı.
- **D-071:** ASUX-v0 Assessment Session & Result UX; 8D tamamlandı.
- **D-072:** SPWX-v0 Progress, Skill State & Weakness UX; 8E tamamlandı.
- **D-073:** VDSX-v0 Visual Design System; 8F tamamlandı.
- **D-074:** WFPX-v0 Wireframe & Prototype Geometry; 8G tamamlandı ve AŞAMA 8 kapandı.
- **D-075:** AMTS-v0 Android Mobile Technology Selection; 9A tamamlandı.
- **D-076:** LFPS-v0 Local-First Persistence Architecture; 9B tamamlandı.
- **D-077:** DDM-v0 Domain Data Model; 9C tamamlandı.
- **D-078:** MSBX-v0 Module & Service Boundaries; 9D tamamlandı.
- **D-079:** AIAX-v0 AI Integration Architecture; 9E tamamlandı.
- **D-080:** dağıtım kapsamı — kişisel kullanım, store dağıtımı yok; numaralı adım değildir.
- **D-081:** TVSX-v0 Test & Verification Strategy; 9F tamamlandı ve AŞAMA 9 kapandı.
- **D-082:** MPSX-v0 Mobile Project Skeleton; 10A tamamlandı, hedef cihaz kaydedildi.
- **D-083:** NSHX-v0 Navigation Shell; 10B tamamlandı.
- **D-084:** DSIX-v0 Design System Implementation; 10C tamamlandı.
- **D-085:** LDBX-v0 Local Database; 10D tamamlandı.
- **D-086:** APHX-v0 App Health; 10E tamamlandı ve AŞAMA 10 kapandı.
- **D-087:** TDYX-v0 Today Interior; 11A tamamlandı ve AŞAMA 11 başladı.
- **D-088:** RNRX-v0 Task Runner; 11B tamamlandı.
- **D-098:** WBAX-v0 Weekly Blueprint Assessment Implementation; 13A tamamlandı.
- **D-099:** AŞAMA 13'e `13F — Tanısal atlama (VDW-v0)` eklendi; kullanıcı kararı, yeniden numaralama yok.
- **D-100:** MCAX-v0 Monthly Capability Assessment Implementation; 13B tamamlandı.
- **D-101:** RVRX-v0 Retention Verification & Risk Implementation; 13C tamamlandı.
- **D-102:** WLRX-v0 Weakness Localization & Remediation Implementation; 13D tamamlandı.
- **D-103:** PCRX-v0 Program Change Report Implementation; 13E tamamlandı.
- **D-104:** VDWX-v0 Validated Diagnostic Waiver Implementation; 13F tamamlandı ve AŞAMA 13 kapandı.

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
- AŞAMA 7 ✅ — EED-v0 / D-063 → TECP-v0 / D-064 → DECP-v0 / D-065 → TEIP-v0 / D-066 → TEPM-v0 / D-067
- 8A ✅ UXIA-v0 / D-068
- 8B ✅ THUX-v0 / D-069
- 8C ✅ TRUX-v0 / D-070
- 8D ✅ ASUX-v0 / D-071
- 8E ✅ SPWX-v0 / D-072
- 8F ✅ VDSX-v0 / D-073
- 8G ✅ WFPX-v0 / D-074 — **AŞAMA 8 tamamlandı**
- 9A ✅ AMTS-v0 / D-075
- 9B ✅ LFPS-v0 / D-076
- 9C ✅ DDM-v0 / D-077
- 9D ✅ MSBX-v0 / D-078
- 9E ✅ AIAX-v0 / D-079
- 9F ✅ TVSX-v0 / D-081 — **AŞAMA 9 kapandı**
- 10A ✅ MPSX-v0 / D-082
- 10B ✅ NSHX-v0 / D-083
- 10C ✅ DSIX-v0 / D-084
- 10D ✅ LDBX-v0 / D-085
- 10E ✅ APHX-v0 / D-086 — **AŞAMA 10 kapandı**
- 11A ✅ TDYX-v0 / D-087
- 11B ✅ RNRX-v0 / D-088
- 11C ✅ SESX-v0 / D-089
- 11D ✅ DMAX-v0 / D-090
- 11E ✅ EODX-v0 / D-091 — **AŞAMA 11 kapandı**
- 12A ✅ MSTX-v0 / D-092
- 12B ✅ PRQX-v0 / D-093
- 12C ✅ PLNX-v0 / D-094
- 12D ✅ RPLX-v0 / D-095
- 12E ✅ RSNX-v0 / D-096
- 12F ✅ VUSX-v0 / D-097
- **AŞAMA 12 TAMAMLANDI**
- 13A ✅ WBAX-v0 / D-098
- 13B ✅ MCAX-v0 / D-100
- 13C ✅ RVRX-v0 / D-101
- 13D ✅ WLRX-v0 / D-102
- 13E ✅ PCRX-v0 / D-103
- 13F ✅ VDWX-v0 / D-104
- **AŞAMA 13 TAMAMLANDI**
- 14A ✅ TUTX-v0 / D-105
- 14B ✅ WAAX-v0 / D-106
- 14C ✅ ALEX-v0 / D-107
- 14D ✅ CDEX-v0 / D-108
- 14E 🟡 active-not-executed
- 14F–20 ⬜

## 11. Güncel kesin konum

**Son tamamlanan:** `14D — CDEX-v0 / D-108`  
**Aktif:** `14E — AI-generated code comprehension check`  
**14E henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**

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

## 18. D-067 / 7E final özeti

Canonical: `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`.  
Policy/QA: `curriculum/english/7e_mastery_profile/`.  
Research: `research/7e_english_mastery_profile_research.md`.

TEPM-v0:
- exact D01 15 Skill identity unchanged,
- 8 learner-facing derived Skill presentation state,
- GRE/RVR/PRG/VDW/WLRM remain canonical state owners,
- qualified A1/A2/B1 Technical English base profile + first-class uneven Skill detail,
- review_due no demotion; verification_due no instant deletion; remediation recomputes current profile from clean evidence,
- historical confirmed band provenance retained,
- no broad/general/official CEFR claim or numeric English aggregate,
- B2+ only named per-capability extension evidence on exact 4 TECP Skills,
- assisted/provisional/contaminated evidence cannot create independent confirmation,
- TEIP cross-track component attribution and contamination guards preserved,
- 18 safety fixture + independent 54-check QA PASS.

AŞAMA 7 tamamlandı.

## 19. 8A handoff

8A — Bilgi mimarisi; accepted product + planner + assessment + granular curriculum + Stage 7 English profile contracts üzerinden uygulamanın ekran/section/navigation information architecture'ını tasarlayacaktır. 8A fresh PRE + kullanıcı açık onayı olmadan yürütülmez.


## 20. D-068 / 8A final özeti

Canonical: `docs/INFORMATION_ARCHITECTURE_SPEC.md`.  
IA/QA: `ux/8a_information_architecture/`.  
Research: `research/8a_information_architecture_research.md`.

UXIA-v0:
- exactly 4 semantic primary destination: Today / Learn / Progress / Profile,
- normal start = Today,
- 6 shared detail surface + 2 focused flow,
- Assessment/English/AI/remediation-retention are contextual, not shell silos,
- exact Skill detail is shared; browse hierarchy never substitutes PRG-v0 prerequisite truth,
- PDT-v0 owns planner explanations; assessment report does not own mastery,
- TEPM-v0 owns qualified Technical English profile semantics,
- local/offline/AI-degraded/recovery states remain cross-cutting,
- concrete visual layout/component implementation is deferred to 8B–10,
- independent 8A QA 49/49 PASS.

## 21. 8B handoff

8B — Ana ekran; UXIA-v0 `today` information ownership üzerinden Today/home content hierarchy, next-action emphasis, plan summary, capacity context and attention/reason presentation'ını tasarlayacaktır. 8B fresh PRE + kullanıcı açık onayı olmadan yürütülmez.


## 22. D-069 / 8B final özeti

Canonical: `docs/TODAY_HOME_SCREEN_SPEC.md`.  
Contract/QA: `ux/8b_today_home/`.  
Synthesis: `research/8b_today_home_research.md`.

THUX-v0:
- Today is canonical planner/state projection, not a second planner,
- 5 semantic content regions with primary action dominant,
- current queue only selected PlannedTasks; no candidate/backlog/debt list,
- recovery → revalidated resume → next task → replan/loading → valid empty/capacity-limited → recoverable-error precedence,
- daily capacity is hard time budget; Today override may replan; persistent preference Profile-owned,
- PDT-v0 owns bounded user-facing reasons,
- assessment/Technical English contextual; quota/streak/gradebook/general-CEFR shortcuts forbidden,
- task completion != mastery; missed day != debt,
- 12 semantic overview states including offline local-core, AI-degraded and data-recovery,
- visual system/geometry and focused interaction choreography remain deferred,
- independent 8B QA 90/90 PASS; Stage 6/7/8A regressions + external memory PASS.

## 23. D-070 / 8C final özeti

Canonical: `docs/DAILY_WORKING_FLOW_SPEC.md`.  
Contract/QA: `ux/8c_daily_working_flow/`.  
Synthesis: `research/8c_daily_working_flow_research.md`.

TRUX-v0:
- Task Runner is an execution surface, never planner/mastery/prerequisite/evidence authority,
- working session is emergent and ungraded; no required count, duration or completion percentage,
- one shared focused-flow frame inherited by `task_runner_flow` and `assessment_session_flow`; assessment interior stays 8D's,
- lifecycle `enter → orient → work → submit → resolve → transition` plus `pause | abandon | recover`,
- deterministic entry/resume revalidation; no prerequisite or content-version bypass; invalidated resume is not negative evidence,
- assistance policy disclosed before independent work; escalation only on request H1→H4; consequence disclosed before H3/H4 in measurement language,
- solution exposure never becomes a same-item mastery path; runner raises `requires_independent_recheck` but never schedules it,
- submission freezes the attempt; post-submit explanation does not retroactively contaminate; no attempt means no evidence,
- provenance asked not inferred; honest disclosure non-punitive; cheating interrogation forbidden,
- 3 pause classes + ResumeContext; in-flight run survives replan; continuity uses recomputed planner selection,
- `evaluation_pending` writes no evidence and is neither pass nor fail; offline preserves local core,
- 4 TEIP integration modes with component-separable dual-target results; no English quota/streak/debt in flow,
- 17 semantic states / 13 forbidden flow anti-patterns; states distinguishable in text,
- independent 8C QA 123/123 PASS; Stage 6/7/8A/8B regressions + external memory PASS.

## 24. D-071 / 8D final özeti

Canonical: `docs/ASSESSMENT_SESSION_UX_SPEC.md`.  
Contract/QA: `ux/8d_assessment_session/`.  
Synthesis: `research/8d_assessment_session_research.md`.

ASUX-v0:
- an assessment session is an evidence-collection workflow, never a gradebook, score-based mastery authority or second state engine,
- exactly one session interior serves daily/weekly/monthly scopes; scope is displayed context and adds no evidence weight,
- the atomic evidence boundary is the submission unit; never split, cut or partially scored,
- submitted boundaries are frozen; unsubmitted boundaries stay navigable within an open block,
- skipping is legitimate: unsubmitted is not incorrect and carries no penalty,
- `h0_required` default and allowed-tools policy are disclosed before responding; objective-appropriate tool use does not break H0,
- in-session assistance is never blocked; H1/H2 assisted, H3/H4 solution-exposed requiring a fresh unseen item; conversion explicit and non-punitive; recheck stays planner-owned,
- pause is not failure/assistance/mastery signal; resume recomposes unresolved slots under five declared conditions without deleting valid evidence,
- incomplete sessions are partial; no exam debt and no missed-cycle failure,
- item disputes hold evidence as contested without auto-invalidating it and never harm user state,
- provisional is labelled everywhere and cannot settle a critical transition; invalid gives neither credit nor penalty,
- the result is semantic across six families; pass/fail banners, grades, thresholds, broad scores and comparisons are forbidden,
- raw counts are informational only; `not_reliably_measured` is first-class and always shown when non-empty,
- a state-change claim requires an actual canonical change; a first contradiction on a mastered Skill opens `verification_due`,
- the in-session result view and the Progress-owned `assessment_report` have distinct non-contradictory roles; the TRUX-v0 frame is inherited; diagnostics remain `task_runner_flow` work,
- 19 semantic states / 15 forbidden anti-patterns,
- independent 8D QA 107/107 PASS, mutation-tested; Stage 6/7/8A/8B/8C regressions + external memory PASS.

## 25. D-072 / 8E final özeti

Canonical: `docs/PROGRESS_SKILL_UX_SPEC.md`.  
Contract/QA: `ux/8e_progress_skill_weakness/`.  
Synthesis: `research/8e_progress_skill_weakness_research.md`.

SPWX-v0:
- Progress is a projection of canonical evidence state, never a mastery engine, score, competence percentage, career tracker or streak dashboard,
- the `TEPM-v0` eight derived presentation states and precedence are generalized to every Skill; no second vocabulary exists and `TEPM-v0` is unchanged,
- eight Turkish Skill labels and six Turkish Topic labels are locked with internal state identities untouched,
- `at_risk` is an attention qualifier, never a ninth primary state and never silently dropped,
- multi-axis truth is ordered, not collapsed; `skill_detail` keeps mastery, retention, prerequisite and weakness axes individually inspectable,
- Topic state is derived orchestration, never a prerequisite claim, never a Skill average, with no percentage,
- progress overview is a demonstrated-capability inventory plus an attention set, and creates no planner priority,
- Progress may count but may not score; counts are labelled inventory and are never divided by a total,
- only `supported` and `confirmed` weakness is shown as weakness; an AI hypothesis is never confirmed; localization never broadcasts,
- `remediation_task_completed != remediation_closed`; closure requires fresh H0 direct verified prerequisite-valid evidence,
- `technical_english_profile` presents TEPM-v0 unchanged with no general/official CEFR, certification or numeric aggregate,
- `learning_history` is not a streak calendar and attendance is never achievement,
- `assessment_report` is longitudinal, owns no mastery and cannot aggregate sessions into a score or competence trend,
- `review_due` stays neutral and non-demoting; `verification_due` states uncertainty without deleting history,
- 10 Progress semantic states / 14 forbidden anti-patterns,
- independent 8E QA 128/128 PASS, cross-validated against the TEPM policy file, TSM, WLRM and the 8D session contract, and mutation-tested; Stage 6/7/8A/8B/8C/8D regressions + external memory PASS.

## 26. D-073 / 8F final özeti

Canonical: `docs/DESIGN_SYSTEM_SPEC.md`.  
Contract/QA: `ux/8f_design_system/`.  
Synthesis: `research/8f_design_system_research.md`.

VDSX-v0:
- the design system is an expression layer and may add no meaning, severity, urgency, ranking or hierarchy: `visual_severity <= canonical_severity`,
- exactly six tones, assigned from meaning rather than from feel, with one declared tone per state,
- all 46 accepted surface states plus 8 Skill states, 6 Topic states and 4 qualifiers are mapped, with none missing and none invented,
- `system_fault` is allowed only on `error_recoverable` and `data_recovery_required`; no learning state may carry an alarm tone,
- attention grouping never upgrades a tone, so `confirmed_review_due` and Topic `weakening` stay neutral,
- typography has 8 roles including `mono`, uses scalable units, stays usable at 200% text, and never truncates state before body content,
- Turkish casing is protected: no locale-naive case transforms, locked labels rendered as authored, no required all-caps,
- colour is specified as semantic roles with WCAG 1.4.3 / 1.4.11 thresholds measured per theme; dark is not an inversion and colour is never the sole carrier,
- 4dp rhythm, minimum 48dp targets, focused-flow exit keeps full target size,
- motion has no persuasive role: countdown, reward-on-completion, decay and streak animation are forbidden and reduced motion loses no information,
- 18 components map to accepted surfaces and owning specs; none invents a surface or state,
- progress-bar shapes are restricted to bounded factual position; gauges, levels, ranks, streaks, heatmaps, leaderboards and trend lines are excluded,
- concrete hex is deliberately not locked; token roles, tone mappings and contrast constraints are, with the palette produced and measured in 8G/10,
- independent 8F QA 121/121 PASS, exhaustiveness cross-validated against the 8A–8E state union, and mutation-tested; Stage 6/7/8A–8E regressions + external memory PASS.

## 27. D-074 / 8G final özeti

Canonical: `docs/WIREFRAME_PROTOTYPE_SPEC.md`.  
Contract/QA: `ux/8g_wireframe_prototype/`.  
Prototype: `ux/8g_wireframe_prototype/prototype.html` (non-binding).  
Synthesis: `research/8g_wireframe_prototype_research.md`.

WFPX-v0:
- geometry arranges accepted meaning and changes no semantic, state, label, tone or ownership,
- three window classes with destination identity and order invariant across all of them,
- six surfaces have declared region geometry, each cross-validated against its owning spec,
- `skill_detail` shows the primary chip and all four axes; the chip never replaces the axis block,
- `progress_overview` keeps inventory counts with no ratio, bar, gauge or percentage,
- focused-flow exit and pause keep 48dp targets and fixed position in every class; no countdown exists,
- the palette is measured independently per theme; dark is not an inversion of light,
- 52 required contrast pairs pass, minima 6.08 text / 3.79 non-text / 6.06 text-on-tone,
- `attention` is violet and red is reserved to `system_fault`, so no traffic-light severity ramp exists,
- stored ratios are evidence; the validator recomputes every one from hex on each run,
- layouts reflow at 200% text and state information is never elided first,
- the prototype is self-contained and explicitly non-binding; it is not an implementation or technology choice,
- independent 8G QA 222/222 PASS, mutation-tested; Stage 6/7/8A–8F regressions + external memory PASS.

**AŞAMA 8 tamamlandı.**

## 28. D-075 / 9A final özeti

Canonical: `docs/MOBILE_TECHNOLOGY_SPEC.md`.  
Contract/QA: `arch/9a_mobile_technology/`.  
Synthesis: `research/9a_mobile_technology_research.md`.

AMTS-v0:
- the technology choice serves the accepted contracts and weakens none; on conflict the contract wins,
- Android native with no cross-platform UI layer in V1 — V1 ships to one platform, so the cross-platform benefit is unavailable while its cost lands exactly where AŞAMA 8 became contractual,
- the portability hedge is the domain core, not the UI,
- Kotlin and Jetpack Compose; a declarative state-driven toolkit matches a token-and-state design system and a Skill appearance that is a deterministic state projection,
- Material 3 is a substrate only and `VDSX-v0` tokens are authoritative; **dynamic colour is disabled** because it would discard the measured palette, the per-theme contrast evidence and the violet-not-amber hue policy,
- the domain core is pure Kotlin with no Android, UI, network or AI dependency, making V1 criterion 8 structural rather than conventional,
- the three window classes map one-to-one onto `WFPX-v0` with invariant destination identity and order,
- every accepted accessibility requirement maps to a platform mechanism,
- default-locale case transforms are forbidden; Turkish casing round-trips and locked `SPWX-v0` labels are never case-transformed,
- `minSdk` is a policy with an API 26 working default, to be confirmed at 10A against the real device, which is not yet recorded,
- an installable APK and real-device QA remain required,
- a six-item bounded verification list is handed to 10A because `AI_AGENT_WORKFLOW` §3 routes framework currency to Research AI and no currency claim is asserted here,
- independent 9A QA 100/100 PASS, cross-validated against the WFPX/VDSX/SPWX/IA contracts and the V1_SCOPE and AI_AGENT_WORKFLOW texts, and mutation-tested; Stage 6/7/8 regressions + external memory PASS.

## 29. D-076 / 9B final özeti

Canonical: `docs/LOCAL_FIRST_PERSISTENCE_SPEC.md`.  
Contract/QA: `arch/9b_local_first_persistence/`.  
Synthesis: `research/9b_local_first_persistence_research.md`.

LFPS-v0:
- evidence is the source of truth and all learner state is a recomputable projection; only new evidence changes demonstrated capability,
- truth records are append-only and invalid evidence is marked rather than erased, so history stays explicable,
- the storage engine is an embedded transactional relational store (SQLite), justified from contracts with rejected alternatives and their losses recorded,
- the mapping/ORM library is deferred to 10A, following the AMTS-v0 precedent that library currency is verified rather than asserted,
- persistence interfaces are core-owned and no storage, Android or filesystem type appears in a core signature,
- curriculum and user state are separately stored and versioned; user records pin the curriculum version and a curriculum update cannot change learner state by itself,
- exposure records are permanent and first-class; losing one is data loss, because a lost record lets a solution-exposed item be served as a fresh independent check,
- one learner action is one transaction; attempts always carry assistance metadata and provenance; `evaluation_pending` writes no evidence,
- migration is forward-only, never destroys evidence, is tested against populated data and fails intact; reverting is a backup restore,
- export reconstructs the profile and records schema and policy versions; restore is atomic and verified and refuses newer-schema sources,
- corruption surfaces `data_recovery_required`, silent reset is forbidden, and an inconsistent projection is repaired by recomputation,
- evidence is not pruned in V1 and any future pruning must be explicit and user-visible,
- independent 9B QA 100/100 PASS, cross-validated against V1_SCOPE, KGC-v0, QAB-v0, ASUX-v0, TRUX-v0, SPWX-v0 and AMTS-v0, and mutation-tested.

## 30. D-077 / 9C final özeti

Canonical: `docs/DOMAIN_DATA_MODEL_SPEC.md`.  
Contract/QA: `arch/9c_domain_data_model/`.  
Synthesis: `research/9c_domain_data_model_research.md`.

DDM-v0:
- the schema enforces the architecture: every LFPS-v0 guarantee is structural rather than conventional,
- three store regions — versioned curriculum, append-only user truth, rebuildable user projections — with no curriculum-to-user foreign key,
- versioned identity is `(logical_id, version)` and every user reference carries the version, because a reference without it silently re-points at the newest version on curriculum update,
- truth tables have no UPDATE or DELETE path; correction is an appended `evidence_disposition` and recomputation excludes by rule, not by absence,
- outcome, evaluator status, independence class and contested are four separate columns, because collapsing any pair erases a real distinction,
- `evidence_event` covers the GRE-v0 field contract that spec explicitly handed to 9C,
- every timestamped row stores instant, learner-local study day and UTC offset, since after DST or travel neither derives reliably from the other,
- every projection row records policy version, truth watermark, build time and input curriculum version, so staleness is detectable,
- the four SPWX-v0 Skill axes are stored separately and the presentation state never replaces them,
- exposure is indexed for the selection-time lookup and is never deleted or archived,
- the physical schema is library-neutral: one table per entity, composite keys, version-carrying foreign keys, constrained-string enums and a monotonic sequence used as the watermark,
- no platform type appears in the core-visible model,
- independent 9C QA 114/114 PASS, cross-validated against LFPS-v0, KGC-v0, GNS-v0, GRE-v0, TRUX-v0, ASUX-v0 and SPWX-v0, and mutation-tested.

## 31. D-078 / 9D final özeti

Canonical: `docs/SERVICE_BOUNDARIES_SPEC.md`.  
Contract/QA: `arch/9d_service_boundaries/`.  
Synthesis: `research/9d_service_boundaries_research.md`.

MSBX-v0:
- boundaries make the guarantees structural: a deterministic core surviving AI and network absence is enforced by the dependency rule rather than by remembering,
- ten modules with a strictly inward dependency rule; `core-*` may never depend on `data-*`, `ai-*` or `app-*`; the graph is acyclic and the rule is checkable,
- `app-wiring` is the composition root, the only module knowing every implementation, and holds no domain logic,
- everything the core needs from outside is a port — persistence, content, clock, evaluator — expressed in core types with no platform type,
- the clock is a port, so time is an input rather than an ambient fact; otherwise timezone logic is untestable and planner output stops being a function of its declared inputs,
- there is no randomness in the core and no seeded random port; ties are broken by a declared total ordering, because a seed would make determinism a configuration rather than a property,
- a null evaluator ships with the product and is not a test fixture; the app builds and runs without `ai-adapter`, open-ended attempts become `evaluation_pending` writing no evidence, and no deterministic capability degrades — V1 criterion 8 is satisfied by wiring,
- each engine owns exactly one state family and writes no other; the planner writes no learner state; cross-engine effects go through `core-application`,
- the transaction boundary lives in `core-application` and engines stay pure policy,
- presentation projection lives in `core-presentation` as pure data and `app-ui` only renders, so capability labelling is testable without a device,
- independent 9D QA 93/93 PASS with the dependency graph computed rather than asserted, and mutation-tested.

## 32. D-079 / 9E final özeti

Canonical: `docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md`.  
Contract/QA: `arch/9e_ai_integration/`.  
Synthesis: `research/9e_ai_integration_research.md`.

AIAX-v0:
- AI is an assistant behind a port and never an authority over mastery, retention, prerequisite, planner or curriculum truth: AI proposes, deterministic engines decide,
- two canonical deferrals were closed here — `LEARNING_BEHAVIOR_RULES` §17 (model selection) and §18 (security/proxy/backend),
- AI's genuine contributions are preserved; the integration bounds authority rather than minimising usefulness,
- evaluator output is schema-constrained and a schema-invalid response is an error, not a verdict; parsing a verdict out of free text is forbidden because a misparse looks like a verdict rather than a failure,
- an uncalibrated LLM evaluation is `provisional`; `verified` requires a deterministic path,
- a seven-outcome taxonomy separates refusal from failure — **a refusal is not a wrong answer** — and every non-answer degrades to `evaluation_pending` writing no evidence,
- the timeout budget is end-to-end across retries; a per-call timeout is not a user-facing guarantee; retries are bounded with no silent background retry against the learner's key,
- the model name lives in configuration with a provider-independent adapter and router; per-task-class defaults are recorded with reasoning and currency must be re-verified at 10A/14,
- deterministic work never calls AI; bulk latency-insensitive work uses batch; cost is never a reason to weaken an evidence rule,
- no hardcoded or shared key ships in the APK and no backend proxy exists in V1; the learner supplies their own key in platform secure storage, it never appears in logs, exports, backups or diagnostics, and the app is fully usable without one,
- only the minimum content needed for the current attempt leaves the device; evidence history, mastery state, plan, profile, exposure, provenance and traces never do,
- generated items enter untrusted, generator and validator are separate, and an unvalidated item cannot produce strong mastery-changing evidence,
- every AI-derived evidence row records provider, model and prompt/schema version,
- independent 9E QA 86/86 PASS, cross-validated against the source contract texts and yaml contracts, with an algorithmic check that the spec names no concrete model identifier as canonical, and mutation-tested.

## 33. D-081 / 9F final özeti

Canonical: `docs/TEST_STRATEGY_SPEC.md`.  
Contract/QA: `arch/9f_test_strategy/`.  
Synthesis: `research/9f_test_strategy_research.md`.

TVSX-v0:
- a guarantee that nothing fails on is a preference; every accepted invariant has a named owning check and an unowned invariant blocks the release by itself,
- five canonical specs had deferred test strategy here — `AMTS-v0`, `LFPS-v0`, `DDM-v0`, `MSBX-v0`, `AIAX-v0` — and all five are answered,
- six tiers, with the device tier the smallest: anything verifiable off-device is verified off-device,
- coverage percentage is not a gate; invariant coverage is, for the same reason this product refuses proxy numbers for learner state,
- negative verification is a tier requirement: the forbidden thing is attempted and must be rejected by the layer that forbids it,
- append-only is verified at the schema level, and correction is verified as an appended disposition leaving the original row unchanged,
- migrations are verified against populated fixtures per prior schema version with evidence, exposure and provenance preserved exactly,
- no check calls a live AI provider; all seven outcomes come from recorded responses and the payload is asserted to carry no history, mastery, plan or profile,
- the null-evaluator path is verified by building without `ai-adapter`, turning V1 criterion 8 into wiring,
- determinism is exercised with an injected clock and repeated byte-identical runs; a flaky check is a failing check and retry-to-green is forbidden,
- six severity classes with `evidence_correctness` always blocking,
- an eleven-condition release gate that maps every V1 criterion and requires the full `tools/validate_*.py` glob,
- a 66-entry invariant register whose every row is a key that genuinely exists in an upstream accepted contract; 9F invents no product semantics,
- what a green suite cannot establish is stated explicitly,
- independent 9F QA 288/288 PASS, mutation-tested 8/8; 26/26 validator sweep PASS.

## 34. D-082 / 10A final özeti

Canonical: `docs/PROJECT_SETUP_SPEC.md`.  
Contract/QA: `arch/10a_project_setup/`.  
Synthesis: `research/10a_project_setup_research.md`.  
Project: `android/`.

MPSX-v0:
- nothing is claimed that was not run: every version came from a current source, every structural rule fails a real build, and every recorded result came from executing the command,
- the build immediately corrected two things this step would otherwise have asserted from memory — no Gradle 9.5 distribution resolves (pinned 9.7.1), and AGP 9.0+ rejects the standalone Kotlin Android plugin,
- toolchain verified 2026-08-31; minSdk 26 / targetSdk 36 / compileSdk 37, the last forced by Compose 1.12,
- the target device is recorded — Poco M6 Pro, `2312FPCA6G`, Android 16 / API 36 — closing the item `AMTS-v0` §8.1 left open; under `D-080` it is the single target device for T6 and V1 criterion 9,
- `minSdk` was not raised toward the device's level: §8.1 asks for the lowest level needing no weakening shim, and 26 is where `java.time` becomes native,
- all six `AMTS-v0` §9 items are answered from current sources, and no compatibility library is needed at 26,
- the ten `MSBX-v0` modules live in `android/` and every module's declared project dependencies equal that contract's `depends_on`, checked mechanically so build and contract cannot drift,
- only the two `app-*` modules apply an Android plugin, which is what keeps T2 and adapter verification off-device,
- `verifyModuleBoundaries` fails the build on a forbidden edge, an unknown layer, a non-root module reaching every layer, or a computed cycle; it is mutation-tested, and testing it exposed and fixed its own cycle reporting,
- the product builds without `:ai-adapter` (9 modules) and selects the shipped `NullEvaluator`, so V1 criterion 8 is demonstrable in one command,
- no DI framework, ORM, HTTP client or architecture-rule library is declared, each for a recorded reason,
- the system clock is read in exactly one place, dynamic colour appears nowhere, and no key or keystore can enter the repository,
- CI runs T3, T1 and both builds plus the full validator glob, and deliberately does not pretend to run T6,
- independent 10A QA 127/127 PASS against the real Gradle and Kotlin files, mutation-tested 7/7; 27/27 sweep PASS.

## 35. D-083 / 10B final özeti

Canonical: `docs/NAVIGATION_SHELL_SPEC.md`.
Contract/QA: `arch/10b_navigation/`.
Synthesis: `research/10b_navigation_research.md`.
Code: `android/core-presentation/.../Navigation.kt`, `android/app-ui/.../AppShell.kt`.

NSHX-v0:
- the navigation rules live in `core-presentation`, not in the UI toolkit, so a violation is a failing test on a laptop rather than something noticed on a phone,
- no external research was needed: the adaptive APIs were verified and pinned at 10A,
- four destinations in the accepted order with `today` first; the enum's declaration order is canonical and there is no second list to drift,
- none of the eleven forbidden top-level ids is a destination, and a test says so,
- one surface object per canonical entity, so `UXIA-v0`'s forbidden contradictory Skill detail pages are unrepresentable rather than discouraged,
- the contextual edge set is closed and compared against `ia.yaml`, because "anything can open anything" is how browse placement quietly starts implying prerequisite truth,
- a focused flow suspends the shell, and shell suppression and safe-exit are derived from the surface so the unsafe state cannot be constructed; the detail pane is suppressed too,
- the return rule is deterministic and treats origin validity as an explicit input, so a replan cannot strand the learner,
- window classes come from `WFPX-v0`'s breakpoints computed in core, not from the toolkit's own bucketing, and change presentation only,
- destinations carry text labels, `stateDescription` and `traversalIndex`; an icon is never the only carrier of meaning,
- four runs executed, including the no-adapter build, so V1 criterion 8 still holds after the shell landed,
- independent 10B QA 104/104 PASS against the real Kotlin source, mutation-tested 8/8; 28/28 sweep PASS.

## 36. D-084 / 10C final özeti

Canonical: `docs/DESIGN_SYSTEM_IMPL_SPEC.md`.
Contract/QA: `arch/10c_design_system/`.
Synthesis: `research/10c_design_system_research.md`.
Code: `android/core-presentation/.../DesignTokens.kt`, `.../Tone.kt`, `android/app-ui/.../CoachTheme.kt`.

DSIX-v0:
- the design system may not add severity the canonical state does not claim, and where a rule can be made unrepresentable instead of merely reviewed, it is,
- tokens are plain data in `core-presentation`, not values buried in the theme file, because contrast has to be recomputed by an ordinary JVM test rather than asserted from a remembered ratio — the shortcut that already failed once at 8G,
- the measured palette is copied exactly and is not revised here; the validator compares every token byte-for-byte with `WFPX-v0`,
- contrast is recomputed from hex in both the product suite and the validator, in both themes, and the recorded minima (6.08 / 3.79 / 6.06) were re-derived from the tokens and matched,
- `LearningTone` has five values and no fault value exists to assign, so giving a learning state the fault tone cannot be written; Material's `error` role carries the system fault tone alone,
- each of the eight Skill states has exactly one declared tone, and three stay neutral deliberately because waiting is not failing,
- attention-group membership never changes a tone, expressed as a named function so the intent is testable; a behavioural mutation of it fails the Kotlin test,
- the 48dp floor is a modifier, text scales to 200%, state is always available as text, and no locale-naive case transform exists,
- dynamic colour stays off and the source scan caught this step's own prose; the comment was reworded rather than the gate loosened, because strict absence is the stronger guarantee,
- independent 10C QA 146/146 PASS, mutation-tested 8/8; 29/29 sweep PASS.

## 37. D-085 / 10D final özeti

Canonical: `docs/LOCAL_DATABASE_SPEC.md`.
Contract/QA: `arch/10d_local_database/`.
Synthesis: `research/10d_local_database_research.md`.
Code: `android/data-persistence/.../Schema.kt`, `.../Migrations.kt`, `.../SqlitePersistence.kt`.

LDBX-v0:
- the storage engine refuses what the architecture forbids, and every refusal is proven by attempting it,
- the first draft of the schema diverged from `DDM-v0` — the evaluator signal enum had been used for the evidence outcome axis, two axes were missing values, the offset was stored in seconds, evidence was keyed to one objective and only four truth tables existed. Its own tests passed because they were written against the same draft; the validator reading the contract caught it, and the schema was rewritten before acceptance. The divergences are recorded,
- the library API was read from the resolved jar with `javap`, not remembered,
- 11 immutable curriculum tables, 13 append-only truth tables (12 DDM entities plus the relational form of plural objective references) and 8 rebuildable projection tables; no user→curriculum foreign key,
- BEFORE UPDATE and BEFORE DELETE triggers abort on every truth and curriculum table, generated from the inventories,
- every DDM allowed value set is a CHECK constraint; pinning is structural; the offset is stored in minutes and a non-whole-minute offset is refused rather than truncated,
- one global monotonic truth sequence is the projection watermark; every projection row carries full provenance and so does the port type,
- migration is forward-only and transactional and was tested against a populated fixture by content, row for row,
- the adapter reads column requirements from SQLite rather than keeping its own list,
- the same schema runs on the JVM for T2 and on the device, verified by finding the arm64-v8a native library inside the APK,
- columns the model does not name are disclosed with their owning steps,
- 22 T2 checks; 9/9 implementation mutations caught, one only after its test was strengthened,
- independent 10D QA 163/163 PASS, validator mutation 9/9 including all three first-draft errors; 30/30 sweep PASS.

## 38. D-086 / 10E final özeti

Canonical: `docs/APP_HEALTH_SPEC.md`.
Contract/QA: `arch/10e_app_health/`.
Synthesis: `research/10e_app_health_research.md`.
Code: `android/core-model/.../StoreHealth.kt`, `android/core-application/.../StoreStartup.kt`, `android/core-presentation/.../AppHealth.kt`, `android/data-persistence/.../StoreOpener.kt`, `.../Backup.kt`, `android/app-ui/.../HealthSurface.kt`, `android/app-wiring/.../CoachApplication.kt`.

APHX-v0:
- no failure of the store is a crash, and no failure of the store is a reset,
- the handoff named two problems; reading the code against the contracts found two more — integrity was never checked on open (`LFPS-v0` §12), and the default build's `AiEvaluator` was `TODO()` and would have crashed on the first open-ended attempt,
- the store belongs to the process: `CoachApplication` starts `StoreStartup` once on a background thread and activities only observe; "never on the caller's thread" is a JVM test with an opener held on a latch; no port was added,
- `StoreOpener` never throws and checks before anything writes: `quick_check` + `foreign_key_check`, then migration, then the full `integrity_check` if a migration ran — the difference between the checks is proven by an index fixture only the full check can see,
- `StoreStatus` and `RecoveryReason` are core types and the reason travels as a type; nothing parses an exception message,
- every recovery case compares the file byte for byte and checks no sidecar appeared, across seven corruption and version shapes,
- the six `UXIA-v0` cross-cutting states with `VDSX-v0` tones and `THUX-v0` precedence; the shell is drawn only during normal use; `ai_unavailable_core_available` only with a working core,
- `HealthAction` has one value, `RECHECK`; no reset, wipe, delete or recreate is representable,
- the restore mechanism (the user's decision: mechanism here, Profile controls at 16D): a verified export, a restore that verifies and migrates only a copy, one atomic rename, the old hot journal set aside, no merge, and refusals that leave both the live profile and the archive byte-identical,
- 16/16 mutations caught — M08 and M09 survived at first and their tests were strengthened; M02's first version did not compile and was not counted,
- T6 was not run: the phone was not connected, and no device result is claimed,
- independent 10E QA 152/152 PASS, validator mutation 12/12; 31/31 sweep PASS.

**AŞAMA 10 TAMAMLANDI.**

## 39. D-087 / 11A final özeti

Canonical: `docs/TODAY_INTERIOR_SPEC.md`.
Contract/QA: `arch/11a_today/`.
Synthesis: `research/11a_today_research.md`.
Code: `android/core-model/.../TodayFacts.kt`, `android/core-presentation/.../TodayPresentation.kt`, `android/core-application/.../TodayFactsQuery.kt`, `android/app-ui/.../TodayScreen.kt`.

TDYX-v0:
- Today is a projection of canonical planner and state truth and computes nothing it was not given; with the planner still at 12 it shows the truthful empty and loading states rather than inventing a plan,
- reading the code against the contracts found two defects nobody had recorded: the content port threw `TODO()`, and the surface registry could hold nulls because it was an initialised field,
- the twelve states, six-step precedence, seven purposes, eight reason families, seven attention families, the region order and the tones are copied from their owning contracts, in their order,
- a stale plan, a blocked task, a replaced plan and an unrevalidated session are filtered out rather than styled differently, so the unsafe state has no code path,
- a reason cannot be constructed without a planner trace fact and carries no free text; no row or capacity field can claim mastery; the capacity verdict is reported, never derived,
- `empty_valid` is produced here and distinguished from `loading_initial_plan` and from the capacity states,
- the read path is read-only, runs on the store thread, refreshes on resume, and invents neither a plan nor a capacity; `curriculumPublished()` is a recorded port refinement, not a fifth port,
- 16/16 mutations caught, three only after their tests were strengthened; independent QA 150/150, validator mutation 16/16 with one miss found and narrowed; 32/32 sweep PASS,
- T6 was not run: the phone was not connected, and no device result is claimed.

## 40. D-088 / 11B final özeti

Canonical: `docs/TASK_RUNNER_SPEC.md`.
Contract/QA: `arch/11b_task_runner/`.
Synthesis: `research/11b_task_runner_research.md`.
Code: `android/core-model/.../AttemptFacts.kt`, `android/core-presentation/.../TaskRunner.kt`, `android/core-application/.../SubmitAttempt.kt`, `android/app-ui/.../TaskRunnerScreen.kt`.

RNRX-v0:
- the Task Runner is an execution surface — not planner, mastery, prerequisite or evidence authority,
- before any runner code, main was found to carry U+0307 from 11A's own sync script; it was fixed and the validator now guards every text file,
- seventeen states, six phases, five entry and five resume conditions and three pause classes are TRUX-v0's; tones are VDSX-v0's and computed in core after a first draft chose them in the UI,
- entry requires every condition to be confirmed; unconfirmed is `unmet`, not `failed`; nothing is startable today, and that changes by engines supplying facts, not by loosening the rule,
- help is always requestable, never granted unrequested, and H3/H4 are never granted before the consequence is disclosed — without a scope condition,
- an attempt is one transaction with its artifact, provenance and assistance, and no evidence; derived facts are not stored; fields the model does not name are listed with owners,
- `appendTruth` returning the row id is a recorded port refinement,
- 18/18 mutations caught on the first run; independent QA 147/147; validator mutation 18/18; 33/33 sweep PASS,
- T6 was not run.

## 41. D-089 / 11C final özeti

Canonical: `docs/SESSION_STATE_SPEC.md`.
Contract/QA: `arch/11c_session_state/`.
Synthesis: `research/11c_session_state_research.md`.
Code: `android/core-model/.../SessionFacts.kt`, `android/core-presentation/.../SessionState.kt`, `android/core-application/.../ResumeCheckpoints.kt`, `PersistencePort.readTruth` + `SqlitePersistence`, `app-ui/.../TaskRunnerScreen.kt`, `app-wiring`.

SESX-v0:
- a pause saves where the work is, never how long it took or how well it went,
- the checkpoint is `SRR-v0`'s `ResumeContext` plus the durable pause kind — the high-stakes mark `TRUX-v0` requires but its field list lacked,
- stored as `resume_context/1`, identity tokens only, decoded strictly; undecodable stays distinct from missing,
- one pause is one transaction and one append-only row; no attempt, evidence, projection or consumed flag; schema unchanged,
- an ordinary pause is durable only with all four safe-checkpoint conditions confirmed; a mid-segment pause has no stored form and never becomes `checkpoint_paused`,
- a resume confirms at most state-intact and the high-stakes condition; no gap threshold was invented (13, 18D); nothing is resumable today; Today offers no checkpoint (12's `continue_learning`),
- the working session is emergent, unscored and not stored (16B owns history); it starts with a started run and ends once with a `TRUX-v0` reason,
- `readTruth` is a recorded port refinement; two broken Turkish words left by 11A were found and guarded,
- 20/20 mutations caught, all by tests; independent QA 146/146; validator mutation 20/20 with one miss found and fixed,
- T6 was not run.

## 42. D-090 / 11D final özeti

Canonical: `docs/DAILY_MICRO_ASSESSMENT_IMPL_SPEC.md`.
Contract/QA: `arch/11d_daily_micro_assessment/`.
Synthesis: `research/11d_daily_micro_assessment_research.md`.
Code: `android/core-model/.../AssessmentFacts.kt`, `.../CurriculumPackage.kt`, `.../ArtifactBody.kt`, `android/core-presentation/.../AssessmentSession.kt`, `android/core-application/.../DailyMicroAssessment.kt`, `android/data-persistence/.../CurriculumStore.kt`, `android/data-curriculum/.../PackageFormat.kt`, `android/app-ui/.../AssessmentSessionScreen.kt`, `app-wiring`.

DMAX-v0:
- an assessment session is an evidence-collection workflow, and an item carries only what its validation, its evaluator and the Objective's evidence profile allow,
- the curriculum region had no writer at all; publishing is now one path, one transaction, refusal decided before any write, and a published version is never overwritten,
- the authored package parses strictly or not at all, and a failed parse serves nothing; item metadata DDM-v0 does not name stays in authored content rather than invented columns,
- trust is the store's validation record, the effective ceiling is the most restrictive applicable rule, and it never exceeds the declared one,
- the Objective decides evidence fit and a mastery measurement needs its direct type,
- exposure is recorded when an item is served and when a solution is revealed, never for an item nobody saw,
- one interior serves all scopes: submitted boundaries freeze, skipping is not incorrect, help is never blocked, recomposition spares completed evidence, and the result is semantic with no score field,
- a short artifact body is carried inside its own reference or refused, never truncated; the schema, the ports and 10D/10E's contracts are untouched,
- **the mutation harness had never run Gradle**; it was fixed, 11D caught 27/27 (three after its tests were strengthened) and 11C's suite was re-run honestly at 20/20,
- three duplicated PROJECT_CONTEXT sections left by 11C's sync were removed and guarded,
- independent QA 188/188, validator mutation 27/27, sweep 35/35,
- T6 was not run.

## 43. D-091 / 11E final özeti

Canonical: `docs/END_OF_DAY_SPEC.md`.
Contract/QA: `arch/11e_end_of_day/`.
Synthesis: `research/11e_end_of_day_research.md`.
Code: `android/core-model/.../DayFacts.kt`, `android/core-presentation/.../EndOfDay.kt`, `android/core-application/.../DayCloseFacts.kt`, `PersistencePort.countTruth` + `SqlitePersistence`, `android/app-ui/.../EndOfDayView.kt`, `TodayScreen`, `app-wiring`.

EODX-v0:
- the end of a day is a boundary in time, not a verdict; the day closes because the study day changed and nothing about the learner's standing changes with it,
- no accepted spec defined an end-of-day surface, so every rule was derived from an owner (SPWX-v0, SRR-v0, DDM-v0, APHX-v0, VDSX-v0, THUX-v0) rather than invented,
- the day is the learner-local study day a row recorded; counting is done against that column and never over an instant range,
- a new day starts empty: no inventory, no unfinished plan and no obligation crosses the boundary, and there is no API that could carry one,
- counts are labelled inventory with no total, ratio, percentage or goal, and counting is not progress,
- a change is claimed only when a canonical engine reported one; with no evidence pipeline the summary says plainly that nothing changed,
- an unreadable count is named, never presented as zero; an empty day is neutral and not a failure; gaps between days are not drawn at all,
- it renders inside Today's day context: no surface invented, no chart, ring or calendar grid,
- `countTruth` is a recorded port refinement that writes nothing; the port count stays four,
- mutation 20/20 (E20 survived at first because a second guard masked the first; the T2 check now asserts each refusal names its own rule), independent QA 128/128, validator mutation 26/26, sweep 36/36,
- T6 was not run.

**AŞAMA 11 TAMAMLANDI** — TDYX-v0 → RNRX-v0 → SESX-v0 → DMAX-v0 → EODX-v0.

## 44. D-092 / 12A final özeti

Canonical: `docs/MASTERY_ENGINE_IMPL_SPEC.md`.
Contract/QA: `arch/12a_mastery_engine/`.
Synthesis: `research/12a_mastery_engine_research.md`.
Code: `android/core-engines/.../MasteryEngine.kt`, `android/core-model/.../EvidenceFacts.kt`, `android/core-application/.../EvidencePipeline.kt`, `.../RebuildMastery.kt`, `PersistencePort.evidenceFor/truthWatermark/latestCurriculumVersion` + `SqlitePersistence`.

MSTX-v0:
- mastery asks one question — can they do it without help? — so assisted, exposed, unverified, contested and prerequisite-contaminated evidence never enters a score, and none of that is a penalty,
- nothing had ever written evidence: `RecordEvidence` closes that, one transaction per attempt, one row per targeted Objective, the Objective's version pinned in its own row,
- a pending evaluation writes nothing, and an unmeasurable answer is `invalid` with no result rather than a zero,
- a dependency group is one group, the window is the last five, the mean is equal-weighted and there are no multipliers,
- a Skill is mastered only when every required and critical Objective passes on its own; an average would let one result hide a missing one,
- both halves of hysteresis: the first clean contradiction opens verification and keeps mastery; a failed recheck resolves it and lets the gates decide again,
- the projection is rebuilt from evidence, writes no truth, carries full provenance, reads the watermark before the evidence and writes only the mastery axis,
- `GRE-v0`'s gate profile has no columns, so it travels as authored content with GRE-v0's defaults; the group rubric is 14's and the mean of a group's rows stands in,
- every constant is GRE-v0's uncalibrated cold-start heuristic, owned by 18C; no percentage, probability or confidence is produced,
- mutation 33/33 (G26 and G33 survived the first run — both were test gaps, not rule gaps), independent QA 164/164, validator mutation 37/37, sweep 37/37,
- T6 was not run, and the engine is not reachable in the app: no path there produces an evaluation yet (12C).

## 45. D-093 / 12B final özeti

Canonical: `docs/PREREQUISITE_ENGINE_IMPL_SPEC.md`.
Contract/QA: `arch/12b_prerequisite_engine/`.
Synthesis: `research/12b_prerequisite_engine_research.md`.
Code: `android/core-engines/.../PrerequisiteEngine.kt`, `android/core-model/.../PrerequisiteFacts.kt`, `android/core-application/.../ResolvePrerequisites.kt` (`ResolvePrerequisites`, `RebuildReadiness`), `PersistencePort.skill/prerequisiteEdgesInto` + `CurriculumStore`/`SqlitePersistence`.

PRQX-v0:
- a missing prerequisite holds back only the work that really depends on it; an independent branch, the blocker's own teaching task and a parallel English track carry on,
- readiness is `PRG-v0`'s four values and never a number; open remediation is `not_ready`, contradicted mastery is `uncertain`, `review_due` is `ready_due` and does not block,
- an axis nobody has evaluated yet is named in the decision and never read as bad news; missing mastery is `not_ready`; a missing readiness fails closed,
- the eligibility matrix is `PRG-v0` §4/§5: normal hard + uncertain is conditional, strict (critical source or the candidate's request) blocks, a soft gap never blocks,
- a task-level requirement is hard even when the graph does not name it; a Skill needed both ways is needed hard; priority is not an input and there is no failure field,
- all 950 authored edges are `draft`: a draft edge is reported as `edge_not_published`, never dropped and never quietly enforced; retired edges stop gating, deprecated ones still gate, the newest edge version is in force,
- `default_prg_v0` is the only strictness profile; unknown profiles, invalidated or unknown lifecycles, self requirements, unpublished Skills and cycles are named metadata problems; the cycle walk reads each Skill once,
- work on a candidate that should have waited records `contaminated`, which the mastery engine already excludes; the value is now core's,
- only `prerequisite_readiness` is written; its watermark is the mastery row's or `0`; `skill_state` assembly under one watermark is 12D's,
- mutation 44/44, independent QA 184/184, validator mutation 42/42, sweep 38/38,
- T6 was not run, and the gate is not reachable in the app: no planner asks it yet (12C).

## 46. D-094 / 12C final özeti

Canonical: `docs/PLANNER_ENGINE_IMPL_SPEC.md`.
Contract/QA: `arch/12c_planner_engine/`.
Synthesis: `research/12c_planner_engine_research.md`.
Code: `android/core-engines/.../PlannerEngine.kt`, `android/core-model/.../PlannerFacts.kt`, `.../PlanTraceCodec.kt`, `android/core-application/.../BuildDailyPlan.kt`, `PersistencePort.publishedSkills` + `ContentPort.taskCandidates`.

PLNX-v0:
- semantic priority first, physical fit second; the gate order is `PDT-v0` §17's and priority never rescues a blocked, invalid or untrusted candidate,
- capacity resolves in D-033's order; the hard budget is never exceeded, the planning budget keeps the `0.10` reserve, and nothing new is taught below the `10`-minute block,
- needs come from the axis each engine owns; an unwritten axis, a draft or retired Skill open nothing, and a deprecated Skill opens no new learning,
- `PBR-v0` bands and the ten-field rank vector, compared field by field and never summed, with a stable tie-break; P0 needs a real blocker, `review_due` is maintenance,
- fit → safe split → smaller alternative → defer; an atomic boundary is never split; one task per need; a need that did not fit is deferred for time, never called less important, never debt,
- no starvation threshold and no reason code is invented; `assess`/`retain`/`diagnose` need a validated candidate; at most five candidates per need,
- the plan is truth, appended in one transaction; the watermark is read before state; everything `planned_task` cannot hold is in the `planner_trace/1` trace, which reads back exactly,
- mutation 55/55, independent QA 226/226, validator mutation 46/46, sweep 39/39,
- T6 was not run, and nothing in the app calls the planner yet (12D).

## 47. D-095 / 12D final özeti

Canonical: `docs/REPLAN_SPEC.md`.
Contract/QA: `arch/12d_replan/`.
Synthesis: `research/12d_replan_research.md`.
Code: `android/core-engines/.../ReplanEngine.kt`, `android/core-model/.../PlannerFacts.kt` (`GenerationKind`, `ReplanTrigger`, `ReplanRecord`, `ReentryContext`, `StoredPlan`), `.../PlanTraceCodec.kt` (`planner_trace/2`), `android/core-application/.../BuildDailyPlan.kt` (`replan`), `PersistencePort.latestPlan/resumeCheckpointRows` + `SqlitePersistence`.

RPLX-v0:
- a plan is replaced by a new version with a reason, never edited: initial when no plan exists, re-entry when the newest plan belongs to another study day, replan when today's plan exists and an event arrived,
- the same day with no event returns the plan already there and writes nothing — 12C's build() used to append an unexplained second initial plan,
- triggers are D-033 §16, `PBR-v0` §17 and `PRG-v0` §19; each carries its `PDT-v0` §8.10 code, `task_completed` carries none, and `user_focus_changed` is not accepted,
- D-033 §8: a new capacity replaces the day, a declared remaining time is the remainder, anything else keeps the day; kept minutes come off the top, the remainder is never negative and follows the day's rule,
- the store cannot say which planned task was started (`attempt` has no `planned_task` link), so the caller that owns in-flight state reports kept positions; each is checked against the previous plan, and a refused replan writes nothing,
- kept work comes first, unchanged and marked; a need a kept task serves is not served twice; an unreadable previous plan is not replaced by guesswork,
- re-entry (`SRR-v0`) replays nothing and keeps nothing, applies the normal day budget, never feeds starvation, and records §17's context and `PDT-v0` §8.8's codes — no score, penalty or debt,
- the latest safe pause makes an open continuation need P2 paused work without selecting it; a high-stakes pause is not continued; a closed need's pause changes nothing,
- `planner_trace/2` records the kept flag, the replan record and the re-entry context; `/1` still decodes strictly,
- re-pointed: `skill_state` assembly → 13; recompute after an attempt and an attempt → planned-task link → 15; calling the planner from the app → 16D; reverse invalidation → 18E,
- mutation 33/33, independent QA 157/157, validator mutation 38/38, sweep 40/40,
- T6 was not run, and nothing in the app calls the planner yet (16D).

## 48. D-096 / 12E final özeti

Canonical: `docs/PLANNER_EXPLANATION_IMPL_SPEC.md`.
Contract/QA: `arch/12e_reason_codes/`.
Synthesis: `research/12e_reason_codes_research.md`.
Code: `android/core-model/.../ReasonCodes.kt`, `.../ExplanationFacts.kt`, `.../PlanTraceCodec.kt` (`planner_trace/3`), `.../PlannerFacts.kt` (`CandidateTrace.relatedSkills`, `StoredPlannedTask`), `android/core-engines/.../PlannerEngine.kt` (`relatedSkills`), `android/core-application/.../PlanReading.kt`, `.../TodayFactsQuery.kt`, `.../PlannerExplanationQuery.kt`, `android/core-presentation/.../PlannerExplanation.kt`, `.../ExplanationCopy.kt`, `.../TodayPresentation.kt`, `android/app-ui/.../PlannerExplanationScreen.kt`, `PersistencePort.latestPlan` + `SqlitePersistence`.

RSNX-v0:
- an explanation is a projection of the decision trace: every statement is a recorded catalogue code or a trace fact, through a private constructor,
- the catalogue is `PDT-v0` §8's ten families and `PRG-v0` §20's nine inputs, in their order; `independent_branch_available` is §20's,
- the trace records each candidate's related Skills at planning time (`PDT-v0` §7 `related_refs`, §11) as `planner_trace/3`; `/2` and `/1` read strictly and cannot claim them; the gate is never re-run to explain,
- a stored plan is read only when its trace describes its own rows (day, positions, Skills, no duplicate, each chosen task's need decision); otherwise it is unreadable and Today shows `error_recoverable` with nothing guessed,
- Today reads the plan: each row points at its stored `planned_task` id, minutes are today's planned part, capacity is the planner's record, kept work is not offered again, and a replan or a waiting need becomes attention linking to the explanation,
- row families come from the need's trigger, a continued safe pause, the fit and the track; an unconfirmed weakness is verified, not repaired; English is the reason only for the parallel track's own cadence; the continuation label no longer claims a start,
- why today puts the need first, then the decisive priority reason, the eligibility it went ahead with and the fit; a band is never shown as the reason; kept work is explained as kept,
- why not today: deferral for time is never lower importance, a waiting need names its Skills, a need nothing serves invents no code; an uncoded replan says only that the plan changed; reconsideration is never a date,
- `ExplanationCopy` is the template fallback with a sentence for every catalogue code, in core beside `DayCopy`; no sentence says forgetting except to deny it, or absence as failure or debt,
- living gates narrowed with guarantees unchanged: `E11A-07`, `E11A-11` (two), `E12C-07`, `E12D-08` (two),
- mutation 52/52, independent QA 219/219, validator mutation 40/40, sweep 41/41,
- T6 was not run, and nothing in the app calls the planner yet (16D); the surface opens and says there is no decision to explain.

## 49. D-097 / 12F final özeti

Canonical: `docs/VIRTUAL_USER_TESTS_SPEC.md`.
Contract/QA: `arch/12f_virtual_user_tests/`.
Synthesis: `research/12f_virtual_user_tests_research.md`.
Code: `android/core-engines/src/testFixtures/.../virtual/VirtualUsers.kt`, `.../VirtualUserScenariosTest.kt`, `android/core-application/.../VirtualUserJourneysTest.kt`, `android/core-presentation/.../VirtualUserExplanationsTest.kt`, `PlannerExplanation.kt` (grouping), `ExplanationCopy.shortNames`, `PlannerExplanationScreen.kt`.

VUSX-v0:
- a virtual user is state, never an answer: needs from Skill state, decisions from the real gate, plans from the real planner, explained traces from what the planner wrote; only owner-supplied needs (parallel track, integration, reinforcement) are given directly,
- defined once as Gradle test fixtures of `core-engines` and used by the engine, journey and explanation suites, on edges `MSBX-v0` already allows,
- 15 of 3H's 16 scenarios run against real code; S06 cannot — `VDW-v0` has no implementation and no owner — and is re-pointed to 13 with invariant 12; invariant 17 is structural here, runtime 18E,
- found: a returning learner's 78 due Skills were 78 not-today rows on `planner_explanation` (`SRR-v0` §9.1); needs that did not come for the same recorded reason are now one entry that drops nothing, and 12E's spec carries an explicit amendment,
- found: 3H's S07 example day holds only when due reviews lack tasks; with a task for every due Skill urgency fills the day and new learning waits for time — no rule changed, the guard is 18C's,
- mutation 27/27 with only the virtual-user suites running (F01, F06 survived first — test gaps), independent QA 148/148, validator mutation 30/30 (W02 found the validator crashing instead of failing), sweep 42/42,
- T6 was not run, and nothing in the app calls the planner yet (16D).

**AŞAMA 12 TAMAMLANDI** — MSTX-v0 → PRQX-v0 → PLNX-v0 → RPLX-v0 → RSNX-v0 → VUSX-v0.

## 50. 13A handoff

13A — Haftalık sınav. `WBA-v0` blueprint kompozisyonu: öğeden önce duruma dayalı plan, rol aileleri kota değil, puan/süre/kategori yüzdesi yok, oturum bölünebilir ve yarım sınav borç değil; 11D'nin tek assessment interior'u hazır. Açık loop'lar: `VDW-v0` tanısal atlaması ve 3H S06 (13), retention/weakness ihtiyaçları ve kodları (13), nihai mikro metin (14), authored içerik (15), planner'ı uygulamadan çağırmak (16D), starvation eşiği (18C), runtime bütçeleri (18E), T6. 13A fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

## 51. D-098 / 13A final özeti

Canonical: `docs/WEEKLY_ASSESSMENT_IMPL_SPEC.md`.
Contract/QA: `arch/13a_weekly_assessment/`.
Synthesis: `research/13a_weekly_assessment_research.md`.
Code: `android/core-model/.../WeeklyAssessmentFacts.kt`, `.../WeeklyBlueprintCodec.kt`, `android/core-engines/.../WeeklyBlueprintEngine.kt`, `android/core-application/.../WeeklyAssessment.kt`, `BuildDailyPlan.kt`, `SubmitAttempt.kt`, `android/core-presentation/.../WeeklyAssessmentSession.kt`, `PersistencePort`/`ContentPort` refinements, `data-persistence` schema v3, `data-curriculum` item keys.

WBAX-v0:
- a week is an identity, not a quota and not a deadline: what is worth measuring comes from state, one Skill is measured once, a slot without a trusted fresh item is not coverage, the week adds no minutes or queue, and a missed week leaves nothing behind,
- the cycle is the ISO week of the recorded study day — a product default the user confirmed; composed once; nothing written when nothing is worth measuring; two missed weeks cannot stack,
- the pool is the planner's own needs (plus owner-supplied parallel track and integration); verification, critical prerequisite confidence (from the planner's trace, gate not re-run), recent progress, retention (`retain`), parallel English; new learning, diagnostics and open remediation are not measured; one role per Skill in §9 order; bands are the planner's,
- items follow `QAB-v0` §31–§33: indexed facets first, a five-item read bound (18E), store trust, a gate that fails closed, no seen item, solved family, repeated family or testlet, no invented duration; every refusal names a rule,
- ready slots become planner candidates for needs it already opened; `BuildDailyPlan` adds this week's unserved slots; no weekly queue, band or minutes,
- one interior; tools disclosed as what every item allows; attempts name their session; the result has no score, a skip is not incorrect, contaminated/invalid/provisional/assisted evidence is first class, and changes are only what engines reported; forward-only root-cause contamination,
- schema v3 completes `assessment_session` with a CHECKed blueprint column, tested against a populated schema-2 database; recomposition appends; four port refinements, port count four,
- five 12x living gates narrowed without weakening; `D-099` appends `13F — Tanısal atlama (VDW-v0)`,
- mutation 42/42 (three first-run mutants replaced), independent QA 210/210, validator mutation 25/25, sweep 43/43,
- T6 was not run; nothing in the app composes a week yet (16D).

## 52. 13B handoff

13B — Aylık sınav. `MCA-v0` aylık kompozisyonu; `WBA-v0` §28'in ortak blueprint/slot/result kontratını kullanır ve 13A'nın composer, codec, planner köprüsü ve interior görünümü başlangıç noktasıdır. Açık loop'lar: geriye dönük contamination (13D), retention ve weakness ihtiyaçları (13C/13D), tanısal atlama (13F), içerik tazeliği (15/18D), haftayı uygulamadan kurmak (16D), T6. 13B fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

## 53. D-100 / 13B final özeti

Canonical: `docs/MONTHLY_ASSESSMENT_IMPL_SPEC.md`.
Contract/QA: `arch/13b_monthly_assessment/`.
Synthesis: `research/13b_monthly_assessment_research.md`.
Code: `android/core-model/.../AssessmentBlueprint.kt`, `.../MonthlyAssessmentFacts.kt`, `.../BlueprintCodec.kt`, `android/core-engines/.../BlueprintComposer.kt`, `.../MonthlyBlueprintEngine.kt`, `android/core-application/.../BlueprintAssessment.kt`, `BuildDailyPlan.kt`, `android/core-presentation/.../BlueprintAssessmentSession.kt`, `data-persistence` schema v4, `data-curriculum` role parsing.

MCAX-v0:
- a month is a wider window, not a heavier exam: what is worth measuring still comes from state, one Skill is measured once, a critical Skill is revalidated only for a reason, a monthly label adds no evidence weight, the month adds no minutes or queue, and a missed month leaves nothing behind,
- 13A's weekly contract became the one common contract (`AssessmentBlueprint` with a scope, `SlotRole`, one scope-parametric `BlueprintComposer`); a mixed blueprint is unrepresentable; no weekly value changed and every 13A test passes,
- the cycle is the calendar month of the recorded study day — a product default following the user-confirmed weekly rule; composed once; the prior monthly session is named, never owed; week and month are independent cycles,
- the pool is the planner's own needs: critical revalidation only for open verification/contradiction, a due review or holding work back; persistent concerns from engine state; longitudinal samples only required Skills inside the window; retention stays `retain`; integration and English; transfer and professional checkpoint have no invented producer (15),
- items must be monthly-eligible and declared for the monthly role; slots are alternatives for needs the planner already opened, and it picks at most one task per need,
- the result has no score; four longitudinal lists hold only clean independent verified evidence of their own role; a clean negative revalidates nothing; the evidence row never learns its scope,
- schema v4 adds a trigger refusing a weekly or monthly row without a blueprint in its own format, tested against a populated v3 database; `monthly_blueprint/1` adds `prior_session`; port count four,
- 13A's validator narrowed to follow the moved code without weakening a guarantee,
- mutation 48/48 (M10 survived the first run; test strengthened; full set re-run), independent QA 259/259, validator mutation 28/28, sweep 44/44,
- T6 was not run; nothing in the app composes a month yet (16D).

## 54. 13C handoff

13C — Spaced repetition. `RVR-v0` retention zamanlaması ve `retention_review_due` ihtiyaçları; aylık `delayed_retention_sampling` ve kritik yeniden doğrulama rolleri ile haftalık `retention_due` rolü bu ihtiyacı planner'ın açtığı gibi okuyor. Açık loop'lar: transfer ve profesyonel kanıt üreticileri (15), geriye dönük contamination (13D), tanısal atlama (13F), ayı/haftayı uygulamadan kurmak (16D), T6. 13C fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

## 55. D-101 / 13C final özeti

Canonical: `docs/RETENTION_IMPL_SPEC.md`.
Contract/QA: `arch/13c_spaced_repetition/`.
Synthesis: `research/13c_spaced_repetition_research.md`.
Code: `android/core-model/.../RetentionFacts.kt`, `EvidenceFacts.kt` (`studyDay`), `android/core-engines/.../RetentionEngine.kt`, `android/core-application/.../RebuildRetention.kt`, `BuildDailyPlan.kt`, `PersistencePort.retentionDueBy`, `data-persistence` schema v5.

RVRX-v0:
- time passing is not negative evidence: the day only says a scheduled check has come; mastery does not decay, a due review is not forgetting, nothing locks,
- retention is replayed from evidence in recording order, with the mastery engine re-asked after every row exactly as `RebuildMastery` would; the rebuild writes no truth,
- transitions: fresh on mastery; first clean failure → `verification_due` (erases nothing); strong check on/after due → `stable`, interval grows; strong use before due → reuse, clock unchanged; uncertain due check → `at_risk`; fresh recheck ≥ 1 study day later → `stable`, interval not grown; lost mastery → `untracked`,
- a near repeat cannot carry a complex or critical review; a recheck must be fresh for every profile; `at_risk` never comes from time,
- `review_due` is derived from the day; `RefreshDueRetention` moves due `fresh`/`stable` schedules by the indexed query and keeps their watermark; `BuildDailyPlan` calls it before reading states; no planner rule changed,
- V0 numbers are `RVR-v0` §20's (heuristic, 18C); growth rounds down; the separation is a rule about credit, not a lock,
- schema v5 completes `retention_state` with §18's fields, the due index and value-set triggers, tested against a populated v4 database; `skill_state` carries the older watermark,
- 13B's schema gate and storage test narrowed without weakening,
- mutation 47/47 (R25/R26/R45 survived the first run; test ordering and a direct UPDATE added; full set re-run), independent QA 164/164, validator mutation 27/27, sweep 45/45,
- T6 was not run; nothing in the app rebuilds engines after evidence yet (16D).

## 56. 13D handoff

13D — Remediation Engine. `WLRM-v0` remediation, zayıflık ekseni, remediation kapanışı, Topic `weakening` (`RVR-v0` §15) ve geriye dönük contamination. Retention artık `verification_due`, `at_risk` ve `REMEDIATION_AFTER_RETENTION_FAILURE` sinyallerini veriyor. Açık loop'lar: motorları kanıttan sonra uygulamadan yeniden kurmak ve `RebuildMastery` watermark'ı (16D), transfer ve profesyonel kanıt üreticileri (15), tanısal atlama (13F), T6. 13D fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

## 57. D-102 / 13D final özeti

Canonical: `docs/WEAKNESS_REMEDIATION_IMPL_SPEC.md`.
Contract/QA: `arch/13d_remediation_engine/`.
Synthesis: `research/13d_remediation_engine_research.md`.
Code: `android/core-model/.../WeaknessFacts.kt`, `android/core-engines/.../WeaknessEngine.kt`, `android/core-application/.../RebuildWeakness.kt`, `MasteryTimeline.kt`, `RebuildRetention.kt`, `BuildDailyPlan.kt`, `BlueprintAssessment.kt`, `data-persistence` schema v6 and disposition reading.

WLRX-v0:
- a failed attempt is not a failed Skill: every row is attributed to its own Objective by `WLRM-v0`'s twelve rules in priority order; nothing broadcasts,
- unattributable or contaminated rows never blame the target; help, a provisional evaluation, a partial or an indirect failure is a hypothesis at most,
- the first clean contradiction after mastery opens verification and erases nothing; a remediation is confirmed only when the mastery engine's gates fail,
- closure needs a fresh clean check; a confirmed remediation closes only when the gates pass again; a finished task, help, the same item or one success never close,
- the engine supplies `weakness_detected` to the planner and the composers; one concern is one need; no planner rule changed,
- dispositions are read newest-first at the store and never written over a row; retroactive contamination corrects only dependent slots (closes 13A/13B's loop),
- `MasteryTimeline` is the one per-row mastery replay shared with retention,
- schema v6 completes `weakness_state`, tested against a populated v5 database; 13C's gates narrowed without weakening,
- user decisions: Topic state → 16C, high-stakes gap policy → 18D,
- mutation 44/44 (D02/D31/D37 survived the first run; tests added or strengthened; full set re-run), independent QA 165/165, validator mutation 28/28, sweep 46/46,
- T6 was not run; nothing in the app rebuilds engines after evidence yet (16D).

## 58. 13E handoff (13E kapanışında karşılandı — §59)

13E — Program değişiklik raporu. Mastery, retention, readiness ve zayıflık artık kanıttan durum yazıyor; program değişikliğini motorların kendi izlerinden raporlamak. Açık loop'lar: motorları ve geriye dönük contamination'ı uygulamadan çağırmak (16D), Topic durumu ve izlerde dispozisyon nedeni (16C), remediation içeriği (15), misconception hafızası (14B), yüksek riskli boşluk politikası (18D), tanısal atlama (13F), T6. 13E fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

## 59. D-103 / 13E final özeti

Canonical: `docs/PROGRAM_CHANGE_REPORT_IMPL_SPEC.md`.
Contract/QA: `arch/13e_program_change_report/`.
Synthesis: `research/13e_program_change_report_research.md`.
Code: `android/core-model/.../ProgramChangeFacts.kt`, `android/core-engines/.../ProgramChangeEngine.kt`, `android/core-application/.../ProgramChanges.kt`, `android/core-presentation/.../ProgramChangePresentation.kt`, `PersistencePort.objectivesOf` (`CurriculumStore`, `SqlitePersistence`).

PCRX-v0:
- a report is a diff of two readings; it can never claim more than the engines wrote,
- a touched Skill is recomputed by its own engines in order (mastery → retention → weakness → readiness), with gate profiles from the published curriculum; an unknown criticality is refused,
- a change is stated only where an axis actually moved; an axis nobody had written is not a before (`unknownBefore`); time alone reports nothing,
- a contradiction after mastery is `verification_opened`, never a demotion, and one verification is named once; a hypothesis is a question, not a gap; correction downgrades report nothing,
- plan changes are needs opened/closed and tasks added/removed between two different plan versions; a first plan is not a change,
- a replan is asked for only when state changed, through the planner's own replan with the event the change names, keeping the day's budget; when nothing changed the result says so plainly,
- `ASUX-v0` §13.1 families are filled from recorded changes only; no score, percentage, blame or "less important",
- one port refinement (`objectivesOf`); four ports; schema unchanged (v6),
- mutation 42/42 (nothing survived the first run), independent QA 162/162, validator mutation 29/29, sweep 47/47,
- T6 was not run; nothing in the app calls the report after a real session yet (16D).

## 60. 13F handoff (13F kapanışında karşılandı — §61)

13F — Tanısal atlama (VDW-v0), D-099 ile eklendi. 12F'nin S06 senaryosu (tanısal atlama) buna bağlı ve koşulamıyordu. Kanıt, kapı, planner, retention, zayıflık motorları artık durum yazıyor ve değişiklikler raporlanıyor. Açık loop'lar: raporu ve yeniden hesaplamayı uygulamadan çağırmak, `stateChangeRefs`'i doldurmak (16D), kalıcı `assessment_report` ve Topic durumu (16C), transfer/artifact kapı alanları ve remediation içeriği (15), misconception hafızası (14B), yüksek riskli boşluk politikası (18D), T6. 13F fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

## 61. D-104 / 13F final özeti

Canonical: `docs/DIAGNOSTIC_WAIVER_IMPL_SPEC.md`.
Contract/QA: `arch/13f_diagnostic_waiver/`.
Synthesis: `research/13f_diagnostic_waiver_research.md`.
Code: `android/core-model/.../DiagnosticFacts.kt`, `android/core-engines/.../DiagnosticWaiverEngine.kt` (plus `PlannerEngine.plan(coverage)`, `WeaknessEngine` narrowing, `ProgramChangeEngine.coverageChange`), `android/core-application/.../Diagnostics.kt`, `android/core-presentation/.../DiagnosticPresentation.kt` (plus `PlannerExplanation`), `data-persistence` schema v7 and `latestAssessmentSessionIn`.

VDWX-v0:
- a diagnostic is not an easier road to mastery; it gathers the same `GRE-v0` evidence through the same pipeline, sooner,
- only the learner opens one (user decision); the claim names the scope and is never evidence; planner-initiated diagnostics → 18B, entry placement → 16D,
- a diagnostic is a `daily` assessment session holding `diagnostic_scope/1`; the newest diagnostic row decides; a new request replaces, a withdrawal ends, nothing is owed,
- a waiver is granted only where the Objective's gates **first** pass on diagnostic evidence, names the window's evidence, and is coverage — never mastery or retention; learning that got there first means no waiver; a correction that removes its evidence withdraws it,
- help or a seen solution, or a clean miss, ends the fast path for that Objective without blame; an untaught miss opens no weakness (user decisions; `WLRM-v0` narrowed for exactly that case); evidence after the fast path ended cannot waive (a bug the tests caught),
- the planner gets one P3 `decisive` `diagnostic_opportunity` per Skill and fresh, trusted, H0 items asking only for missing gates; a lesson still being checked waits, a waived lesson is resolved, only what was shown is skipped; planning reads no evidence,
- full only when every Skill is mastered by the mastery engine; Topic state is 16C's,
- schema v7 adds the `diagnostic_coverage` projection and the `VDW-v0` state family (D-104; 9C/9D contracts unedited); one port refinement; two 13E change kinds; the 12E explanation says what was skipped or is waiting,
- S06 and `PDT-v0` invariant 12 run against real code; all sixteen 3H scenarios run,
- mutation 69/69, independent QA 220/220, validator mutation 29/29, sweep 48/48,
- T6 was not run; nothing in the app offers the fast path yet (16D).

## 62. 14A handoff

14A — Tutor davranış sözleşmesi. AŞAMA 13 kapandı: kanıt, kapı, planner, replan, açıklama, retention, zayıflık, rapor ve tanısal atlama motorları kodda; `AIAX-v0` AI'ı port arkasında yardımcı ve otorite olmayan olarak kilitledi. Açık loop'lar: motorları ve tanıyı uygulamadan çağırmak (16D), Topic durumu ve kalıcı `assessment_report` (16C), authored içerik ve curriculum sürümleri arası waiver (15), misconception hafızası (14B), nihai metin (14), planner kaynaklı tanı (18B), T6. 14A fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

## 63. D-105 / 14A final özeti

Canonical: `docs/TUTOR_BEHAVIOR_CONTRACT_SPEC.md`.
Contract/QA: `arch/14a_tutor_contract/`.
Synthesis: `research/14a_tutor_contract_research.md`.
Code: `android/core-model/.../TutorFacts.kt`, `TutorInstructions.kt`, `AssistanceInterpretation.kt`; `core-ports` `TutorPort`; `core-application` `AskTutor.kt`, `NullTutor.kt`; `core-presentation` `TutorPresentation.kt`; `ai-adapter` `AiTutor.kt`; `app-wiring` `TutorProvider`; `data-persistence` `evidenceFor` (solution exposure).

TUTX-v0:
- the tutor teaches on request and never decides; every piece of help it shows is recorded for what it is; help not shown is recorded nowhere; nothing it says is evidence,
- user decisions: a separate `TutorPort` (declared `port_extension` under D-105, the 9D contract unedited); free questions, the learner choosing the level while an answer is open; the first live call is 14G's,
- five closed intents; help is always requestable; a mismatched ask is redirected, never refused; glossing the target segment is a hint,
- recorded: nothing outside an attempt; a gloss H1 non-target; the ceiling while an answer is open; H4 after it froze; the tutor's own level only refuses, never lowers the record; the H3/H4 disclosure is worded for the moment,
- only the current task leaves the device; the message is built in core and checked off the device; material cannot speak as the app; the reference solution travels only once it gives nothing away,
- `tutor_reply/1` with no room for a verdict; an unshowable reply is `invalid_response`; non-answers record nothing and blame no one; only authored help replaces them,
- found: recorded help never reached evidence (now `AssistanceInterpretation`, `2D` §6); solution exposure never reached the engines (now read by attempt order, schema unchanged),
- eighteen living port-count gates narrowed to MSBX's four plus the declared extension; no guarantee weakened,
- mutation 68/68, independent QA 219/219, validator mutation 30/30, sweep 49/49,
- T6 was not run; nothing in the app calls the tutor yet (16D) and no provider is wired (14G).

## 64. 14B handoff

14B — Yanlış analizi. `TUTX-v0` tutor'un nasıl davrandığını kilitledi (istekler, seviye tavanı, cihazdan çıkan mesaj, yanıt şeması, yanıtsızlık, yardımın kanıt anlamı); `WLRX-v0` atıf kuralları ve misconception hipotez sınırı kodda. Açık loop'lar: misconception hafızası (14B), alternatif anlatım (14C), kod değerlendirme (14D), AI kodunun anlaşılma kontrolü (14E), açık uçlu değerlendirme (14F), adaptör çağrı noktası ve model güncelliği (14G), yazılmış ipucu basamakları (15), paneli çizmek ve uygulamadan çağırmak (16D), konuşma geçmişi (16B), T6. 14B fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

## 65. D-106 / 14B final özeti

Canonical: `docs/WRONG_ANSWER_ANALYSIS_IMPL_SPEC.md`.
Contract/QA: `arch/14b_wrong_answer_analysis/`.
Synthesis: `research/14b_wrong_answer_analysis_research.md`.
Code: `core-model` `MisconceptionFacts.kt` (+ `EvaluationResult.misconceptionHypotheses`); `core-engines` `MisconceptionEngine.kt`; `core-application` `AnalyzeWrongAnswer.kt`, `RebuildWeakness` (`WeaknessEvents`, `MisconceptionRows`), `RecordEvidence` tags; `core-presentation` `WrongAnswerPresentation.kt`; `data-persistence` schema v8, `misconceptionsOf`; `data-curriculum` `[misconception]` section.

WAAX-v0:
- a wrong answer is information, not a verdict; a label is never stronger than its evidence and its source,
- user decisions: a closed, curriculum-authored catalog (undeclared labels are never stored); a hypothesis is shown only as an open question right after the answer,
- found: `misconception_tags` never written; `EvaluationResult` had dropped `AIAX-v0`'s `misconception_hypotheses`; no memory, no catalog,
- labels only on rows that went wrong, for their own Objective, if declared; `deterministic` from `Verified`, `ai_proposed` from `Provisional`,
- memory replayed by the weakness engine's own rule, capped by source; unattributable work and diagnostic baselines move nothing; fresh clean success resolves, time never does,
- the analysis reads the stored attribution through one shared event builder and writes nothing,
- schema v8, one port refinement, no new engine family; `review.6g.misconception_taxonomy_expansion` resolved (labels 15, expansion 18),
- mutation 51/51, independent QA 154/154, validator mutation 28/28, sweep 50/50,
- T6 was not run.

## 66. 14C handoff

14C — Alternatif anlatım. `TUTX-v0` `explain_differently` isteğini ve yanıt sözleşmesini, `WAAX-v0` misconception hafızasını kurdu. Açık loop'lar: authored etiketler ve deterministik anahtarlar (15), hafızanın item seçiminde kullanımı (15), Progress görünümü (16C), analizi uygulamadan çağırmak (16D), kod değerlendirme (14D), açık uçlu değerlendirme (14F), adaptör (14G), T6. 14C fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

## 67. D-107 / 14C final özeti

Canonical: `docs/ALTERNATIVE_EXPLANATION_IMPL_SPEC.md`.
Contract/QA: `arch/14c_alternative_explanation/`.
Synthesis: `research/14c_alternative_explanation_research.md`.
Code: `core-model` `AlternativeExplanationFacts.kt` (+ `TutorAsk.form`, `TutorRequest.form`, `TutorContext.canonicalExplanation`, `TutorRules.authored`, `tutor_instructions/2`); `core-application` `ExplainDifferently.kt`, `AskTutor.showWritten`; `core-presentation` `AlternativeExplanationPresentation.kt`; `core-ports` `ContentPort.explanationsFor`; `data-curriculum` `[explanation]` section.

ALEX-v0:
- when an explanation does not land, the method changes — the scope and the truth do not,
- user decisions: the learner chooses the form from a menu; written explanations first, the tutor only where none fits,
- found: `explain_differently` carried no form; written alternatives had no place; an AI alternative could not be grounded; a misconception contrast would have needed learner state to leave the device,
- seven closed forms; the tutor may write five; a misconception contrast is written-only and offered only for a label the learner's memory holds open; the course's own explanation is not an alternative,
- the least revealing written explanation that fits the ceiling is shown without the tutor; an AI alternative is asked with `<canonical>`, labelled unverified, and the way back is always offered,
- `tutor_instructions/2` (rules 14-15); 14A's validator narrowed for exactly the four additions; schema unchanged; one content-port refinement,
- mutation 42/42, independent QA 123/123, validator mutation 30/30, sweep 51/51,
- T6 was not run.

## 68. 14D handoff

14D — Kod değerlendirme. `AIAX-v0` değerlendirici portu ve yanıtsızlık taksonomisi, `TUTX-v0` tutor sözleşmesi, `WAAX-v0` kapalı misconception kataloğu ve kanıttaki etiketler, `ALEX-v0` alternatif anlatım kodda. Açık loop'lar: yazılı anlatımlar ve karşılaştırmalar (15), menüyü çizmek ve uygulamadan çağırmak (16D), adaptör (14G), biçim kalibrasyonu (18), T6. 14D fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

## 69. D-108 / 14D final özeti

Canonical: `docs/CODE_EVALUATION_IMPL_SPEC.md`.
Contract/QA: `arch/14d_code_evaluation/`.
Synthesis: `research/14d_code_evaluation_research.md`.
Code: `core-model` `CodeEvaluationFacts.kt`; `core-application` `EvaluateCode.kt`; `core-presentation` `CodeEvaluationPresentation.kt`; `core-ports` `ContentPort.codeTestsFor`; `data-curriculum` `[code_test_suite]`/`[code_test]` sections; `tools/code_test_runner.py` + `tools/fixtures/code_test_14d/`.

CDEX-v0:
- code is judged by running it, or it is only an opinion,
- user decisions: tests run on the learner's computer and the report is imported; without tests, an AI's evaluation is provisional evidence only where the task allows it,
- found: no production code produced an evaluation; the deterministic path for code had nowhere to run; nothing kept a test to its Objective; an evaluator port could return `verified`,
- a test speaks only for its Objective; a failed build only for an authored build Objective; what did not run measured nothing; a timeout is a failure,
- a tested task is never put to an AI; a missing or broken report never falls back to an AI; a verified-only task waits for tests; an AI's `verified` is an invalid response,
- schema unchanged; one content-port refinement; results recorded by the existing pipeline,
- mutation 49/49, independent QA 122/122, validator mutation 30/30, sweep 52/52,
- T6 and a C build were not run.

## 70. 14E handoff

14E — AI-generated code comprehension check. `CDEX-v0` kodun ne yaptığını testlerle ölçüyor; testlerin geçmesi anlamak değildir. `2D` provenance (`generated_or_copied`), `TUTX-v0` tutor sözleşmesi ve `ALEX-v0` kodda. Açık loop'lar: gerçek görevlerin testleri (15), raporu artifact olarak saklamak ve uygulamadan çağırmak (16D), kod için adaptör istemi (14G), açık uçlu değerlendirme (14F), AI kod değerlendiricisinin kalibrasyonu (18), T6, C derlemesi. 14E fresh PRE + kullanıcı açık onayı olmadan yürütülmez.
