# AI Infra Learning Coach — Master Geliştirme Planı

**Durum:** AKTİF / CANONICAL DETAYLI PLAN  
**Son senkron:** 2026-08-27

Sabit adım kimliklerinin canonical kaynağı `docs/EXECUTION_INDEX.md` dosyasıdır. Bu dosya ayrıntılı checklist ve completion notlarını onunla senkron tutar.

Ana ürün ilkesi:
> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

## Uzun vadeli hedef — D-041
Gerektiğinde 4+ yıl veya daha uzun sürebilecek mastery-gated rota ile AI Infrastructure / ML Systems / GPU Systems alanında profesyonel çalışmaya hazırlanabilecek verified engineering capability oluşturmak. Takvim readiness gate değildir; final readiness mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ister.

V1 full curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality içerikle release edilebilir.

## Güncel rota/plan kararları
- D-042: Python common foundation'ın resmi parçasıdır; C/C++ yerine geçmez.
- D-043: standalone specialization-stage yorumu geri çekilmiştir.
- D-044: **AŞAMA 6 — Granular Capability Map**, bütün rotayı `Domain → Module → Topic → Skill → Learning Objective` seviyesinde ayrıntılandıracaktır.
- D-045: weekly assessment = WBA-v0.
- D-046: monthly assessment = MCA-v0.
- D-047: assessment resource bank = QAB-v0.
- D-048: AI-generated assessment validation = AIV-v0.
- D-049: curriculum domain backbone = PDM-v0.
- D-050: living-memory sync + repo-wide stale-reference audit her numaralı step kapanışında zorunludur.
- D-051: 5B final knowledge-graph contract `KGC-v0`; canonical file `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.
- D-052: 5C final V1 foundation backbone `FBB-v0`; canonical file `docs/V1_FOUNDATION_BACKBONE.md`.
- D-055: main manager role local çalışan agent'a devredilebilir; canonical bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; numbered execution state değişmez.
- D-056: 6B final full-route decomposition blueprint `FRDB-v0`; canonical file `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md`.
- D-057: 6C final Foundations detailed map `FDM-v0`; canonical summary `docs/FOUNDATIONS_DETAILED_MAP.md`, dataset `curriculum/decomposition/6c_foundations/`.
- D-058: 6D final Systems detailed map `SDM-v0`; canonical summary `docs/SYSTEMS_DETAILED_MAP.md`, dataset `curriculum/decomposition/6d_systems/`.
- D-059: 6E final GPU / ML / Inference detailed map `GIM-v0`; canonical summary `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`, dataset `curriculum/decomposition/6e_gpu_ml_inference/`.

## Zorunlu yürütme — D-024 / D-027 / D-050
`PRE-STEP GitHub refresh → gerekiyorsa Research/Coding/QA → spec/çıktı → değerlendirme → ALWAYS-CHECK living-memory sync → repo-wide stale-reference scan → sonraki adım`

ALWAYS-CHECK seti ve dosya rolleri: `docs/PROJECT_MEMORY_PROTOCOL.md`.

---

# AŞAMA 1 — Ürün Çerçevesini Kilitle ✅
### [x] 1A — Ana ürün amacı — `docs/PRODUCT_REQUIREMENTS.md`
### [x] 1B — V1 kapsamı — `docs/V1_SCOPE.md`
### [x] 1C — Başarı kriterleri — `docs/V1_SUCCESS_CRITERIA.md`
### [x] 1D — Non-goals — `docs/NON_GOALS.md`

---

# AŞAMA 2 — Öğrenme ve Mastery Modelini Tasarla ✅
### [x] 2A — Bilgi birimleri — D-021
### [x] 2B — Topic durumları — D-023
### [x] 2C — Mastery sinyalleri — D-025
### [x] 2D — AI / ipucu etkisi — D-026
### [x] 2E — Mastery formülü v0 — GRE-v0 / D-031
### [x] 2F — Unutma modeli — RVR-v0 / D-032

D-044 clarification: broad Domain/Topic tanı atomu değildir; weakness/remediation mümkün olduğunca Skill/Objective seviyesinde lokalize edilir.

---

# AŞAMA 3 — Adaptif Günlük Planlama Motorunu Tasarla ✅
### [x] 3A — Günlük kapasite — D-033
### [x] 3B — Görev kategorileri — D-034
### [x] 3C — Öncelik — PBR-v0 / D-035
### [x] 3D — Prerequisite — PRG-v0 / D-036
### [x] 3E — Hızlı öğrenme — VDW-v0 / D-037
### [x] 3F — Kaçırılan günler — SRR-v0 / D-038
### [x] 3G — Açıklanabilir planner — PDT-v0 / D-039
### [x] 3H — Planner simülasyonu — `docs/PLANNER_SIMULATION_SUITE.md`

**Sonuç:** 16/16 scenarios PASS, 20/20 invariants PASS, 0 critical contradiction.

---

# AŞAMA 4 — Sınav ve Değerlendirme Sistemini Tasarla ✅
### [x] 4A — Günlük mikro değerlendirme — DMA-v0 / D-040
### [x] 4B — Haftalık sınav — WBA-v0 / D-045
### [x] 4C — Aylık yeterlilik sınavı — MCA-v0 / D-046
### [x] 4D — Soru / assessment resource bank — QAB-v0 / D-047
### [x] 4E — AI-generated soru doğrulaması — AIV-v0 / D-048

> **AŞAMA 4 tamamlandı: DMA-v0 + WBA-v0 + MCA-v0 + QAB-v0 + AIV-v0.**

---

# AŞAMA 5 — Curriculum ve Knowledge Graph İskeleti

### [x] 5A — Ana domain haritası — PDM-v0 / D-049
**Final:** `docs/CURRICULUM_DOMAIN_MAP.md`

Final davranış:
- 23 ana route family high-level professional envelope olarak korunur.
- Technical English parallel track.
- Python + C + Linux/Git/Shell complementary early foundations; DS&A supporting common foundation.
- Systems core: Modern C++, Architecture, OS/Memory, Concurrency, Networking.
- Distributed/platform core: Distributed Systems + Storage/DB + Containers/Cloud/Observability.
- Performance route boyunca cross-cutting core'dur; yalnız final optimization bölümü değildir.
- Accelerator core: GPU Architecture → CUDA/Triton.
- ML/Transformer inference için supporting domain; generic ML-research specialization değildir.
- LLM Inference → Serving Systems → KV/Batching/Scheduling/Quantization ayrı bağlı family'lerdir.
- Multi-GPU/NCCL/RDMA networking + distributed + GPU convergence katmanıdır.
- AI/GPU Infrastructure target integration domainidir.
- Open Source/engineering practice/projects/capstones route boyunca büyüyen professional evidence layer'dır.
- Security/reliability/math/numerical ihtiyaçları hidden prerequisite olarak bırakılmaz.
- Tool/vendor isimleri stable systems concept yerine geçmez.
- Domain-level authoring relations runtime hard-lock değildir; Skill→Skill PRG-v0 korunur.

**PRE/POST notu:** 5A fresh GitHub PRE-STEP refresh ile yürütüldü. Ayrı Research AI kullanılmadı; full external coverage/current-industry Research QA AŞAMA 6H'de zorunlu planlandı.

### [x] 5B — Graph / Topic metadata sözleşmesi — KGC-v0 / D-051
**Final:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`

**5B final coverage:**
- Domain/Module/Topic/Skill/Learning Objective entity contract,
- placement vs canonical Skill identity,
- Topic↔Skill many-to-many,
- Skill→Skill hard/soft prerequisite,
- 5A domain authoring relations,
- required/critical/optional semantics,
- evidence/assessment refs,
- retention/remediation/diagnostic metadata,
- Technical English prerequisite metadata,
- cross-domain Skill reuse,
- professional/project/capstone attribution,
- source/version/freshness,
- graph version/migration,
- indexing/bounded traversal/performance contract.

KGC-v0 ayrıca organization-vs-capability identity, scope-relative requirement, conservative graph migration, professional/project attribution ve D-028 bounded graph traversal invariants'ını kilitledi. Ayrı Research AI kullanılmadı; 5B mevcut accepted specs arasında internal contract formalizasyonuydu. External coverage/current-industry validation 6H'de zorunlu kalır.

### [x] 5C — İlk 8–12 haftalık curriculum backbone — FBB-v0 / D-052
**Final:** `docs/V1_FOUNDATION_BACKBONE.md`

**5C final coverage:**
- 8–12 hafta = scope-equivalent, calendar gate değil,
- zero-entry Computer/Programming bridge,
- Python/C/Linux/Git/Shell/early DS&A/parallel English seed subgraph,
- KGC-v0 Skill/Objective authoring-seed skeleton,
- PRG-v0 hard/soft prerequisite edges,
- scope-relative technical/English/professional-workflow requirements,
- GRE/QAB/RVR evidence-retention-diagnostic-remediation anchors,
- 6A/6C ratification lifecycle,
- AŞAMA 15 authoring handoff + 5D QA fixtures.

### [x] 5D — Graph architecture QA — GQA-v0 / D-053
**Final:** `docs/GRAPH_ARCHITECTURE_QA.md`

- initial FBB structural blockers bulundu ve corrective authoring-seed patch uygulandı,
- explicit TopicSkillLink matrix eklendi,
- KGC reason-kind vocabulary normalize edildi,
- hidden prerequisite / accidental zero-eligibility riskleri minimal hard edges ile düzeltildi,
- hard graph DAG / no self-dangling-conflicting edges PASS,
- English global-gate / branch isolation / reachability fixtures PASS,
- 6A/6C ratification + 6H external Research QA guard korunuyor.

> AŞAMA 5 schema/backbone; detailed decomposition AŞAMA 6.

---

# AŞAMA 6 — Granular Capability Map / Öğrenme Rotasını Alt Becerilere Böl
Canonical charter: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

### [x] 6A — Granularity + naming standardı — GNS-v0 / D-054
**Final:** `docs/GRANULARITY_NAMING_STANDARD.md`

**6A final coverage:**
- Domain/Module/Topic/Skill/Objective semantic granularity boundaries,
- Capability Independence Test,
- under/over-fragmentation guards,
- Skill vs Objective split rule,
- shared vs language/tool/context-specific Skill policy,
- stable logical ID convention,
- display/localization/alias vs identity separation,
- version/split/merge/re-home migration rules,
- FBB seed ratification statuses,
- 6B decomposition-template handoff.

### [x] 6B — Full-route decomposition blueprint — FRDB-v0 / D-056
**Final:** `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md`

**6B final coverage:**
- 23 route family 6C–6F package sınırlarına eksiksiz atandı,
- UTF-8 YAML machine-readable authoring package + manifest/source/QA collections tanımlandı,
- organization, Skill, Objective, TopicSkillLink, prerequisite, requirement ve attribution row contract'ları kilitlendi,
- GNS-v0 granularity review + unresolved review queue tanımlandı,
- cross-package duplicate resolver ve shared Skill reuse workflow'u tanımlandı,
- FBB seed mapping/ratification contract'ı 6C'ye bağlandı,
- evidence/depth/remediation/retention/diagnostic/freshness metadata authoring kuralları tanımlandı,
- 6G weakness/remediation ve 6H independent Research QA handoff'u korundu,
- physical DB, production content ve gerçek node listesi kapsam dışında tutuldu.

### [x] 6C — Foundations detailed map — FDM-v0 / D-057
**Final:** `docs/FOUNDATIONS_DETAILED_MAP.md` + `curriculum/decomposition/6c_foundations/`

**6C final coverage:**
- D01–D05 = 5 Domain / 14 Module / 46 Topic,
- 132 Skill / 137 Objective / 145 TopicSkillLink,
- 200 hard/soft prerequisite edge; hard graph DAG,
- FBB 41/41 Skill + 47/47 Objective seed mapping,
- 33 Skill ratify + 8 broad Skill split,
- Technical English global-gate guard,
- Python/C/Linux/Shell/Git/DS&A granular capability boundaries,
- deterministic authoring generator + independent package validator,
- internal QA `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`, 0 blocking,
- 6H external Research QA pending; learner publication yapılmadı.

### [x] 6D — Systems detailed map — SDM-v0 / D-058
**Final:** `docs/SYSTEMS_DETAILED_MAP.md` + `curriculum/decomposition/6d_systems/`

**6D final coverage:**
- D06–D13 = 8 Domain / 21 Module / 64 Topic,
- 192 Skill / 207 Objective / 224 TopicSkillLink,
- 313 prerequisite edge (259 hard / 54 soft); FRDB Pass B ile 52 scaffold edge soft'a indirildi,
- 6C + 6D birleşik hard graph DAG 324/324 node,
- 43 accepted 6C Skill clone'lanmadan reuse edildi; 56 cross-package edge explicit etiketli,
- Modern C++ ownership/lifetime, Architecture, OS/Memory, Concurrency, Networking, Distributed/Storage, Containers/Cloud/Observability ve Performance capability sınırları,
- stable systems concept ile fast-moving tool capability ayrımı + freshness/technology metadata,
- Technical English global-gate guard ve branch isolation korundu,
- deterministic authoring generator + bağımsız package validator,
- internal QA `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`, 0 blocking,
- 6H external Research QA pending; learner publication yapılmadı.

### [x] 6E — GPU / ML / Inference detailed map — GIM-v0 / D-059
**Final:** `docs/GPU_ML_INFERENCE_DETAILED_MAP.md` + `curriculum/decomposition/6e_gpu_ml_inference/`

- D14–D22 = 9 Domain / 27 Module / 70 Topic,
- 143 Skill / 159 Objective / 230 TopicSkillLink,
- 279 prerequisite edge = 247 hard / 32 soft; FRDB Pass-B audit PASS,
- 59 prior Skill reuse (9 Foundation + 50 Systems),
- 6C+6D+6E combined hard graph DAG 467/467,
- deterministic generator + independent validator PASS,
- internal QA `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`, 0 blocking,
- external Research QA 6H'ye pending; learner publication yapılmadı.

### [x] 6F — Professional engineering / project map — PEM-v0 / D-060
**Final:** `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md` + `curriculum/decomposition/6f_professional_engineering/`

- D23 = 1 Domain / 9 Module / 27 Topic,
- 76 Skill / 87 Objective / 110 TopicSkillLink,
- 138 prerequisite edge = 137 hard / 1 soft,
- 25 prior Skill clone'lanmadan reuse edildi,
- Git/PR/code review, testing/CI, build/release, debug/perf, design/RFC, ops/reliability, security, OSS ve project/capstone behavior granularlaştırıldı,
- 6D + 6E professional-overlay review'ları resolved,
- internal QA `PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`, 0 blocking,
- external Research QA 6H'ye pending; learner publication yapılmadı.

### [x] 6G — Weakness localization + remediation mapping — WLRM-v0 / D-061
**Final:** `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` + `curriculum/decomposition/6g_weakness_remediation/`

- final 6H-patched registry için 549 accepted Skill + 608 accepted Objective exact overlay coverage,
- 608 Objective remediation route,
- 12 failure-attribution rule + 15 remediation strategy family,
- invalid/ambiguous/prerequisite-contaminated attempt target weakness yazmıyor,
- H1–H4/provisional/partial signal confirmed remediation'a atlamıyor,
- first clean post-mastery contradiction `verification_due`,
- `review_due` weakness değil,
- broad Topic/Domain reset ve project-component broadcast yok,
- closure fresh H0/direct/verified/prerequisite-valid evidence ile GRE/RVR üzerinden,
- internal QA PASS; D-062 external Research QA sonrası final registry coverage 549/608 ve 6H external behavior review resolved.

### [x] 6H — Coverage / prerequisite / external Research QA — S6ERQA-v0 / D-062
**Final:** `docs/STAGE6_EXTERNAL_RESEARCH_QA.md` + `research/6h_external_research_ai_report.md` + `curriculum/decomposition/6h_research_qa/`

- 3 independent external evaluator → `PASS WITH REQUIRED CHANGES`,
- 6 stable capability addition: NUMA locality/affinity, CUDA async data movement pipeline, speculative decoding trade-off, prefill/decode disaggregation, MoE routing, expert parallel sharding,
- hidden prerequisite + DRA/runtime/freshness + D23/WLRM corrective patch applied,
- vendor/model-specific fast-moving details kept as version-scoped Objective/example unless GNS-v0 independence test passes,
- final Stage 6 = 23 route / 549 Skill / 608 Objective / 950 prerequisite edge,
- combined hard graph 549/549 DAG,
- WLRM exact coverage 549 Skill / 608 Objective,
- 10/10 6H-owned reviews resolved,
- historical 6C–6G regressions + final external reconciliation validator PASS,
- Stage 6 complete; next step 7A.


---

# AŞAMA 7 — İngilizce Paralel Hattı
### [x] 7A — Başlangıç ölçümü — EED-v0 / D-063
**Final:** `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md` + `curriculum/english/7a_entry_diagnostic/`

- 15/15 D01 English Skill ve 15/15 owner Objective diagnostic claim olarak kapsandı,
- canonical 16 English hard edge birebir korundu; hard DAG 15/15,
- 15 task family claim→evidence→task traceability ile tanımlandı,
- GRE-v0/VDW-v0 standardı düşürülmedi; self-report evidence değil,
- unknown-English / specialist-technical hidden prerequisite ve downstream fail broadcast yasak,
- CEFR level ataması 7B'ye deferred; `review.6c.english.cefr_alignment` açık,
- final Stage 6 regression + 7A independent deterministic validator PASS.

### [x] 7B — A1/A2/B1/B2+ teknik hedefleri — TECP-v0 / D-064
**Final:** `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md` + `curriculum/english/7b_cefr_progression/`

- exact 15 D01 Skill context-only CEFR progression metadata ile aligned,
- 5 A1 / 5 A2 / 5 B1 base anchor; 16/16 hard edge band-monotonic,
- 4 bounded B2+ professional evidence-depth extension,
- no general-English certification/official-level claim,
- no new Skill/Objective/prerequisite edge veya numeric CEFR formula,
- `review.6c.english.cefr_alignment` resolved,
- Stage 6 + EED-v0 + 7B validator PASS.

### [x] 7C — Günlük English bileşeni — DECP-v0 / D-065
**Final:** `docs/DAILY_ENGLISH_COMPONENT_SPEC.md` + `curriculum/english/7c_daily_component/`

- common daily hard capacity; no separate English budget/quota,
- active-study-day + open/eligible/safe English need → at least one candidate,
- candidate/selection/attempt/mastery separation,
- normal parallel need P3 + existing PBR track-balance/starvation reuse, no fixed omission-day threshold,
- state-driven new/continue/retention/remediation/production/reinforcement mix,
- RVR-v0 owns spacing; no fake universal interval/minute/percentage,
- feedback-assisted revision != independent mastery evidence,
- TECP-v0 B2+ semantic boundary preserved,
- technical integration deferred to 7D; English mastery/display deferred to 7E,
- Stage 6 + 7B regression + independent 7C validator PASS.

### [x] 7D — Teknik entegrasyon — TEIP-v0 / D-066
**Final:** `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md` + `curriculum/english/7d_technical_integration/`

- exact D01 15 Skill identity unchanged; no graph mutation,
- 4 construct-aware integration mode,
- technical/English target-prerequisite-attribution separation,
- bidirectional construct-contamination guards,
- evidence-driven reversible scaffold; no fixed ratio/day quota,
- authentic resource + translation/gloss integrity guard,
- 15 safety fixture / 49 validator checks PASS.

### [x] 7E — English mastery — TEPM-v0 / D-067
**Final:** `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md` + `curriculum/english/7e_mastery_profile/`

- exact D01 15 Skill identity unchanged; no graph/mastery algorithm mutation,
- 8 learner-facing derived Skill presentation state,
- qualified A1/A2/B1 Technical English base profile + uneven exact Skill detail,
- review_due no demotion; verification_due uncertainty/hysteresis preserved,
- remediation can recompute current band while historical confirmation remains,
- B2+ per-capability evidence only for exact 4 TECP extension Skills,
- no general-English/certification/numeric aggregate overclaim,
- assistance/provisional/contamination and TEIP component-attribution guards preserved,
- 18 safety fixture / 54 validator checks PASS.

> **AŞAMA 7 tamamlandı — EED-v0 + TECP-v0 + DECP-v0 + TEIP-v0 + TEPM-v0.**

---

# AŞAMA 8 — UX ve Ekranlar
### [x] 8A — Bilgi mimarisi — UXIA-v0 / D-068
**Final:** `docs/INFORMATION_ARCHITECTURE_SPEC.md` + `ux/8a_information_architecture/`

- 4 top-level semantic destinations: Today / Learn / Progress / Profile,
- Today normal start destination,
- shared entity/detail surfaces + focused task/assessment flows,
- Assessment/English/AI/remediation are contextual, not top-level silos,
- exact Skill state + PDT explanation + TEPM profile truth ownership preserved,
- no time/streak/task-completion/general-CEFR progress overclaim,
- adaptive navigation semantics stable; concrete component/visual layout deferred,
- independent 8A QA 49/49 PASS.

### [x] 8B — Ana ekran — THUX-v0 / D-069
**Final:** `docs/TODAY_HOME_SCREEN_SPEC.md` + `ux/8b_today_home/`

- action-first primary action + plan/capacity context,
- remaining queue only current selected PlannedTasks; no candidate/backlog/debt leakage,
- safe recovery/resume/next-task/replan/empty precedence,
- current-day capacity override triggers replan; persistent settings remain Profile-owned,
- PDT-v0 bounded reason projection; attention never rescores planner priority,
- contextual assessment + Technical English without quotas/gradebook/general CEFR,
- truthful empty/offline/AI-degraded/recovery states,
- task completion != mastery; missed day != debt,
- 90/90 independent 8B QA PASS.

### [x] 8C — Günlük çalışma akışı — TRUX-v0 / D-070

**8C final coverage:**
- Task Runner execution-surface sınırı; planner/mastery/prerequisite/evidence authority değil,
- emergent ungraded working session; required count/duration/percentage yok,
- shared focused-flow frame `task_runner_flow` + `assessment_session_flow` tarafından devralınır; assessment interior 8D'de,
- lifecycle `enter → orient → work → submit → resolve → transition` + `pause | abandon | recover`,
- deterministic entry/resume revalidation; prerequisite ve content-version bypass yok,
- assistance her zaman talep edilebilir; escalation yalnız talep üzerine H1→H4; H3/H4 öncesi consequence disclosure,
- solution exposure sonrası same-item mastery path yok; recheck scheduling planner-owned,
- provenance sorulur, çıkarsanmaz; dürüst beyan cezasız,
- 3 pause class + ResumeContext; in-flight run replan'dan korunur,
- `evaluation_pending` evidence yazmaz, pass/fail değildir,
- 4 TEIP integration mode component-separable,
- 17 semantic state / 13 forbidden flow anti-pattern,
- independent validator **123/123 PASS**; Stage 6 + Stage 7 + 8A + 8B + external-memory regressions PASS.

### [x] 8D — Sınav UX — ASUX-v0 / D-071

**8D final coverage:**
- üç assessment scope için tek session interior; scope yalnız displayed context,
- atomic evidence boundary submission birimi; bölünmez ve kısmen puanlanmaz,
- submitted boundary frozen; unsubmitted boundary açık blok içinde gezilebilir,
- skip meşru; `unsubmitted_boundary != incorrect`,
- independence mode + allowed tools cevap öncesi açıklanır; objective-appropriate tool H0'ı bozmaz,
- in-session assistance engellenmez; consequence ölçüm dilinde açıklanır; recheck planner-owned,
- beş koşullu slot recomposition; completed valid evidence silinmez,
- incomplete session partial; exam debt yok,
- item dispute contested tutar, auto-invalidate etmez, cezasızdır,
- provisional her yerde etiketli; invalid ne kredi ne ceza,
- semantic result: 6 family; pass/fail banner, grade, threshold ve broad score yasak,
- `not_reliably_measured` first-class ve boş değilse daima gösterilir,
- state-change iddiası yalnız canonical değişimde; ilk contradiction `verification_due`,
- 19 semantic state / 15 forbidden session anti-pattern,
- independent validator **107/107 PASS** (mutation-tested); Stage 6 + Stage 7 + 8A + 8B + 8C + external-memory regressions PASS.

### [x] 8E — Skill/progress/weakness UX — SPWX-v0 / D-072

**8E final coverage:**
- Progress canonical evidence state'in projection'ıdır; mastery engine/score/gradebook değildir,
- TEPM-v0'dan genelleştirilen tek 8-state Skill presentation vokabüleri + aynı precedence; ikinci vokabüler yok,
- `at_risk` attention qualifier'dır; dokuzuncu primary state değildir ve düşürülmez,
- multi-axis truth sıralanır, çökertilmez; `skill_detail` her ekseni incelenebilir tutar,
- 6 Topic UI etiketi kilitlendi; internal ID'ler değişmedi; Topic yüzdesi ve prerequisite iddiası yok,
- progress overview = demonstrated inventory + attention set; planner priority üretmez,
- Progress sayabilir, puanlayamaz: count yalnız etiketli inventory, total'e bölünmez,
- yalnız `supported`/`confirmed` weakness gösterilir; AI hypothesis confirmed olamaz; localization korunur,
- `remediation_task_completed != remediation_closed`; closure canonical evidence ister,
- TEPM-v0 English profile semantiği değişmeden sunulur; general/official CEFR ve numeric aggregate yasak,
- learning history streak calendar değildir; attendance başarı değildir,
- assessment_report longitudinal'dir, mastery sahibi değildir ve session'ları score'a toplayamaz,
- `review_due` nötr, `verification_due` history silmez, görsel severity canonical state'i aşamaz,
- 10 semantic state / 14 forbidden Progress anti-pattern,
- independent validator **128/128 PASS** (TEPM policy + TSM + WLRM + 8D session.yaml çapraz doğrulamalı, mutation-tested); Stage 6 + Stage 7 + 8A–8D + external-memory regressions PASS.

### [ ] 8F — Tasarım sistemi — **AKTİF**
### [ ] 8G — Wireframe/prototip

---

# AŞAMA 9 — Teknik Mimari ve Veri Modeli
### [ ] 9A — Mobil teknoloji seçimi
### [ ] 9B — Veri saklama/local-first
### [ ] 9C — Domain veri modeli
- granular Skill/Objective state,
- assessment resource identity/version/lifecycle,
- AI validation records/use ceilings,
- curriculum graph identities/versions,
- per-user exposure,
- years-long curriculum/user history,
- migrations.
### [ ] 9D — Servis sınırları
### [ ] 9E — AI entegrasyon mimarisi
### [ ] 9F — Test stratejisi / performance budgets

---

# AŞAMA 10 — Mobil Proje İskeleti
### [ ] 10A — Proje kurulumu
### [ ] 10B — Navigation
### [ ] 10C — Design system implementation
### [ ] 10D — Local database
### [ ] 10E — Temel uygulama sağlığı

---

# AŞAMA 11 — Günlük Öğrenme MVP
### [ ] 11A — Today
### [ ] 11B — Task runner
### [ ] 11C — Session state
### [ ] 11D — Günlük mikro quiz
### [ ] 11E — Gün sonu

---

# AŞAMA 12 — Mastery + Planner Implementasyonu
### [ ] 12A — Mastery Engine v1
### [ ] 12B — Prerequisite Engine
### [ ] 12C — Planner Engine v1
### [ ] 12D — Replan
### [ ] 12E — Reason codes
### [ ] 12F — Sanal kullanıcı testleri

---

# AŞAMA 13 — Assessment + Retention + Remediation Implementasyonu
### [ ] 13A — Haftalık sınav
### [ ] 13B — Aylık sınav
### [ ] 13C — Spaced repetition
### [ ] 13D — Remediation Engine
### [ ] 13E — Program değişiklik raporu

---

# AŞAMA 14 — AI Tutor ve Akıllı Değerlendirme
### [ ] 14A — Tutor davranış sözleşmesi
### [ ] 14B — Yanlış analizi
### [ ] 14C — Alternatif anlatım
### [ ] 14D — Kod değerlendirme
### [ ] 14E — AI-generated code comprehension check
### [ ] 14F — Açık uçlu cevap değerlendirme
### [ ] 14G — Provider abstraction/fallback

---

# AŞAMA 15 — İlk 8–12 Haftalık Gerçek Eğitim İçeriği
### [ ] 15A — Computer / Programming Fundamentals
### [ ] 15B — Python Foundations
### [ ] 15C — C Foundations
### [ ] 15D — Memory Foundations
### [ ] 15E — Linux / Git / Shell Foundations
### [ ] 15F — English A0→A1/A2 başlangıç paketi
### [ ] 15G — Assessment content
### [ ] 15H — Content QA

---

# AŞAMA 16 — İlerleme / Analitik / Ayarlar
### [ ] 16A — Skill analytics
### [ ] 16B — Öğrenme geçmişi
### [ ] 16C — Progress / weakness kuralları
### [ ] 16D — Ayarlar
### [ ] 16E — Bildirimler

---

# AŞAMA 17 — UI/UX Polish
### [ ] 17A — Görsel polish
### [ ] 17B — Motion
### [ ] 17C — Kullanılabilirlik
### [ ] 17D — Accessibility

---

# AŞAMA 18 — Pilot / Kalibrasyon / QA
### [ ] 18A — Pilot başlangıcı
### [ ] 18B — Planner gözlemi
### [ ] 18C — Mastery kalibrasyonu
### [ ] 18D — Assessment/item/exposure/validator kalibrasyonu
### [ ] 18E — Teknik / performance QA
### [ ] 18F — Düzeltme döngüsü

---

# AŞAMA 19 — Release APK
### [ ] 19A — Release hazırlığı
### [ ] 19B — Veri güvenilirliği
### [ ] 19C — Final regression
### [ ] 19D — APK / gerçek cihaz
### [ ] 19E — Release dokümantasyonu

**AŞAMA 19 V1 release = professional curriculum completion değildir.**

---

# AŞAMA 20 — Uzun Vadeli Professional Curriculum ve Kariyer Katmanı
### [ ] 20A — Modern C++ + Advanced Python + Professional Tooling
### [ ] 20B — Systems + Architecture + Performance
### [ ] 20C — Networking + Distributed Systems + Storage
### [ ] 20D — GPU Architecture + CUDA
### [ ] 20E — Triton + ML/Transformer + LLM Inference
### [ ] 20F — Serving Engines + KV Cache / Batching / Scheduling / Quantization
### [ ] 20G — Multi-GPU / NCCL / RDMA / AI Infrastructure
### [ ] 20H — Open Source + Engineering Practice + Career Readiness
### [ ] 20I — Sürekli Curriculum QA + Büyük Entegre Projeler + Professional Capstones

---

# Repository Hygiene Maintenance — D-050

Bu bakım numaralı bir stage değildir ve 5B'yi ilerletmez.

- Repo dosya envanteri audit edildi.
- `PROJECT_CONTEXT.md` living snapshot olarak mandatory sync kapsamına alındı ve 5B current state'e getirildi.
- `PROJECT_MASTER_CONTEXT` / README volatile active-step duplication'dan arındırıldı.
- Obsolete `docs/TODO.md` silindi.
- Legacy `docs/LEARNING_ENGINE.md` explicit historical/superseded pointer'a dönüştürüldü.
- `docs/ENGLISH_TRACK.md` non-canonical seed olarak etiketlendi.
- `PROJECT_MEMORY_PROTOCOL` + `AI_AGENT_WORKFLOW` repo-wide stale-reference scan ve ALWAYS-CHECK setiyle güçlendirildi.

---

# Local Manager Transition — D-055

Bu operasyonel handoff numaralı stage değildir. Local manager mevcut accepted specs'i devralır; PRE/POST GitHub memory protocol, Research/Coding/Test separation ve acceptance discipline aynen sürer. Bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`. Transition kendi başına **6B execution değildi**; sonraki kullanıcı onaylı numbered work normal protokolle ilerledi.

---

# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`, `8A–8E`  
**Son tamamlanan:** **`8E — SPWX-v0 / D-072`**  
**Aktif:** **`8F — Tasarım sistemi`** — henüz yürütülmedi.

Bir sonraki yürütme: **8F fresh PRE-STEP → 8A–8E'de kilitlenen semantic destination/state/label setini değiştirmeden typography, color, spacing, iconography, motion ve component library tasarımı → independent QA → D-050 POST sync + stale audit.**

- D-068: 8A final `UXIA-v0`.
- D-069: 8B final `THUX-v0`.
- D-070: 8C final `TRUX-v0`.
- D-071: 8D final `ASUX-v0`.
- D-072: 8E final `SPWX-v0`.
