# Project Context — Kısa Yaşayan Proje Hafızası

**Son senkron:** 2026-08-28
**Dosya rolü:** Kısa current snapshot. Her numaralı adım sonunda D-050 / `docs/PROJECT_MEMORY_PROTOCOL.md` gereği kontrol edilir ve execution state değiştiyse güncellenir.

Bu dosya sohbet bağlamı kaybolsa bile projenin yönünü ve **şu an nerede olduğumuzu** hızlıca yeniden kurmak için tutulur. Ayrıntılı bootstrap için `docs/START_HERE.md`, uzun/stabil bağlam için `docs/PROJECT_MASTER_CONTEXT.md` canonicaldır.

## 1. Ana kariyer yönü

Seçilen uzmanlaşma:

**Low-Level Systems → Distributed Systems → GPU/CUDA → LLM Inference → AI Infrastructure / ML Systems / GPU Systems**

Güncel ana rota:

**Technical English (parallel) → Python → C → Linux + Git + Shell → DS&A foundations → Modern C++ → Computer Architecture → OS + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases → Containers / Cloud / Observability → Performance Engineering & Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM/SGLang/TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + professional capstones**

Python D-042 ile resmi foundation dilidir; C/C++ yerine geçmez. ML, inference sistemlerini anlayacak gerekli tensor/model/transformer derinliğinde supporting domain olarak tutulur.

## 2. Uzun vadeli hedef — D-041

- Rota gerektiğinde **4+ yıl veya daha uzun** sürebilir.
- 4+ yıl countdown değildir.
- Nihai hedef course completion değil, professional-readiness seviyesinde verified engineering capability'dir.
- Final readiness; required mastery + retention + debugging + transfer + performance + integrated project/capstone evidence ister.
- V1 full 4+ year curriculum'u beklemez; learning engine + ilk 8–12 haftalık production-quality curriculum ile release edilir.

Canonical: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## 3. Granular Capability Map — D-044

Canonical hierarchy:

`Domain → Module → Topic → Skill → Learning Objective`

Gerçek mastery/prerequisite/weakness/remediation mümkün olduğunca Skill/Objective seviyesinde çalışır. `Python zayıf` gibi broad sonuçlar yalnız derived summary olabilir.

AŞAMA 6, Technical English'ten AI Infrastructure ve professional capstone'a kadar bütün rotayı ölçülebilir alt Skill/Objective haritasına böldü ve S6ERQA-v0 / D-062 ile bağımsız external Research QA'dan geçti.

Canonical: `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`.

## 4. Ana ürün ilkesi

> **Zaman geçirmek ilerleme değildir. Yalnızca kanıtlanmış öğrenme ilerlemedir.**

Lesson/task completion, streak, self-confidence, AI-assisted output veya takvim süresi tek başına mastery/readiness değildir.

## 5. Mastery / Planner omurgası

- GRE-v0 / D-031 — valid + prerequisite-valid + H0 + direct + verified + independent evidence.
- RVR-v0 / D-032 — time mastery'yi düşürmez; `review_due` forgetting değildir.
- D-033 — daily capacity hard budget.
- D-034 — LearningNeed / TaskCandidate / Evidence ayrımı.
- PBR-v0 / D-035 — semantic priority.
- PRG-v0 / D-036 — Skill-level prerequisite readiness.
- VDW-v0 / D-037 — validated diagnostic waiver.
- SRR-v0 / D-038 — absence debt/failure değildir; stale plan replay edilmez.
- PDT-v0 / D-039 — structured planner decision trace.

3H simulation: **16/16 scenarios PASS, 20/20 invariants PASS**.

## 6. Assessment foundation — AŞAMA 4 tamamlandı

- 4A ✅ DMA-v0 / D-040 — Daily Micro Assessment.
- 4B ✅ WBA-v0 / D-045 — Weekly Blueprint Assessment.
- 4C ✅ MCA-v0 / D-046 — Monthly Capability Assessment.
- 4D ✅ QAB-v0 / D-047 — Trusted Assessment Resource Bank.
- 4E ✅ AIV-v0 / D-048 — AI Assessment Resource Validation.

Assessment raw score ile broad domain pass/fail yazmaz; Objective-level evidence canonical GRE/RVR/PRG/planner pipeline'ına girer.

## 7. Curriculum backbone / knowledge graph — AŞAMA 5 tamamlandı

**D-049 / `PDM-v0 — Professional Domain Backbone`** canonical kaynak: `docs/CURRICULUM_DOMAIN_MAP.md`.

- 23 route family high-level envelope olarak kilitlendi.
- Technical English parallel track.
- Python + C + Linux/Git/Shell complementary early foundations; DS&A supporting foundation.
- Systems → distributed/platform → performance → GPU/accelerator → inference → multi-GPU → AI/GPU Infrastructure convergence.
- Performance route boyunca cross-cutting capability.
- Open Source / engineering practice / projects / capstones route boyunca artan professional evidence layer.
- Security/reliability ve gerekli math/numerical capability hidden prerequisite bırakılmayacak.
- Tool/vendor isimleri stable systems concept'in yerine geçmeyecek.
- Domain-level relations authoring guidance; runtime hard prerequisite Skill→Skill PRG-v0.

**D-051 / KGC-v0:** canonical `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`. Organization layer ile capability identity ayrıldı; Skill reusable canonical identity, Topic↔Skill many-to-many, Objective exactly-one-Skill, scope-relative requirements, evidence/retention/remediation/English/professional attribution, provenance/freshness, immutable graph versioning ve conservative migration contract'ı kilitlendi.

**D-052 / FBB-v0:** canonical `docs/V1_FOUNDATION_BACKBONE.md`. V1 başlangıç seed subgraph'ı zero-entry bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English olarak tanımlandı. 8–12 hafta calendar gate değil scope-equivalent'tır; Skill/Objective IDs 6A/6C öncesi `authoring_seed` lifecycle'ındadır.

**D-053 / GQA-v0:** canonical `docs/GRAPH_ARCHITECTURE_QA.md`. 5D initial structural blockers ve hidden-prerequisite risklerini corrective seed patch ile düzeltti; hard graph DAG, TopicSkillLink/reuse explicit, English global-gate yok, F5D fixtures PASS.

**D-054 / GNS-v0:** canonical `docs/GRANULARITY_NAMING_STANDARD.md`. 6A Skill/Objective atomization, under/over-fragmentation, shared-vs-specific capability, stable logical ID ve FBB seed ratification/refactor kurallarını kilitledi.

**D-056 / FRDB-v0:** canonical `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md`. 6B, 23 route family için 6C–6F common machine-readable authoring package, entity/relation row, duplicate/reuse, prerequisite, FBB mapping, source/freshness, review queue ve QA contract'ını kilitledi.

**D-057 / FDM-v0:** canonical summary `docs/FOUNDATIONS_DETAILED_MAP.md`, dataset `curriculum/decomposition/6c_foundations/`. D01–D05; 5 Domain, 14 Module, 46 Topic, 132 Skill, 137 Objective, 145 TopicSkillLink ve 200 prerequisite edge ile internally mapped; FBB 41/47 seed mapping complete, hard graph DAG, 0 blocking review. External validation S6ERQA-v0 / D-062 ile tamamlandı.

**D-058 / SDM-v0:** canonical summary `docs/SYSTEMS_DETAILED_MAP.md`, dataset `curriculum/decomposition/6d_systems/`. D06–D13; 8 Domain, 21 Module, 64 Topic, 192 Skill, 207 Objective, 224 TopicSkillLink ve 313 prerequisite edge (259 hard / 54 soft). 43 accepted 6C Skill clone'lanmadan reuse edildi; 6C+6D birleşik hard graph DAG 324/324; 0 blocking review. External validation S6ERQA-v0 / D-062 ile tamamlandı.

**D-059 / GIM-v0:** canonical summary `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`, dataset `curriculum/decomposition/6e_gpu_ml_inference/`. D14–D22; 9 Domain, 27 Module, 70 Topic, 143 Skill, 159 Objective, 230 TopicSkillLink ve 279 prerequisite edge (247 hard / 32 soft). 59 prior Skill clone'lanmadan reuse edildi; 6C+6D+6E combined hard graph DAG 467/467; 0 blocking review. External validation S6ERQA-v0 / D-062 ile tamamlandı.

**D-060 / PEM-v0:** canonical summary `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`, dataset `curriculum/decomposition/6f_professional_engineering/`. D23; 1 Domain, 9 Module, 27 Topic, 76 Skill, 87 Objective. Existing D01–D22 technical capability'leri clone edilmeden professional project/capstone context'lerinde reuse edildi; 0 blocking review. External validation S6ERQA-v0 / D-062 ile tamamlandı.

**D-061 / WLRM-v0:** canonical summary `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md`, dataset `curriculum/decomposition/6g_weakness_remediation/`. 543 accepted Skill + 590 accepted Objective için weakness/remediation overlay; 590 route, 12 attribution rule, 15 strategy; 0 blocking review. External validation S6ERQA-v0 / D-062 ile tamamlandı.

**D-062 / S6ERQA-v0:** canonical `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`. Üç bağımsız external evaluator reconcile edildi; corrective patch sonrası Stage 6 final registry 549 Skill / 608 Objective / 950 prerequisite edge, hard DAG 549/549 ve WLRM exact coverage 549/608. 10/10 6H review resolved.

## 8. İngilizce

English teknik eğitimin global ön koşulu değildir; ilk günden paralel ilerler. Bilinmeyen grammar/vocabulary teknik assessment'ta gizli prerequisite olamaz. Granular English capability map AŞAMA 6C / FDM-v0'da tamamlandı; 6D Systems package'ında da English→technical hard gate yoktur. English-specific progression/CEFR/cadence/integration AŞAMA 7'de kesinleşir.

### 8.1 7A English Entry Diagnostic — EED-v0 / D-063

7A başlangıç ölçümü Stage 6 D01 Technical English graph'ını yeniden sınıflandırmaz; 15 Skill / 15 Objective / 16 English hard edge üzerinde prerequisite-aware diagnostic profile üretir. Self-report evidence değildir; diagnostic mastery GRE-v0/VDW-v0 standardını düşürmez; invalid/prerequisite-contaminated failure target weakness yazmaz. CEFR A1/A2/B1/B2+ mapping 7B'ye bırakılmıştır.

Canonical: `docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md`.

### 8.2 7B Technical English CEFR Progression — TECP-v0 / D-064

D01 15 Skill context-only CEFR-aligned Technical English progression metadata aldı: 5 A1 + 5 A2 + 5 B1 base anchor; 4 canonical Skill bounded B2+ professional evidence-depth extension taşıyor. CEFR alignment mastery/certification/general-English level değildir; source of truth exact Skill/Objective evidence'dır. `review.6c.english.cefr_alignment` resolved edildi.

Canonical: `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`.


### 8.3 7C Daily English Component — DECP-v0 / D-065

Technical English common daily hard capacity içinde parallel track olarak çalışır. Active study day'de open + eligible + safe English need varsa en az bir candidate üretilir; selection/mastery garantisi değildir. Fixed daily minute/percentage/completion/streak/debt yoktur. PBR-v0 track-balance/starvation semantics'i reuse edilir; task mix exact Skill/Objective state'inden türetilir ve spacing RVR-v0'da kalır. Technical-task integration 7D'ye, learner-facing English mastery/CEFR behavior 7E'ye bırakılmıştır.

Canonical: `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`.

### 8.4 7D Technical English Integration — TEIP-v0 / D-066

Technical task'in dili target construct ile eşit sayılmaz. TEIP-v0 dört mode tanımlar: technical-only localized, technical-with-English-exposure, dual-target integrated ve English-primary technical-context. English/CEFR global technical gate değildir; technical ve English target/prerequisite/evidence attribution ayrı tutulur. Hidden English technical negative evidence'ı, hidden specialist technical context English negative evidence'ı contaminate eder. Scaffold evidence/task-validity driven ve reversible'dır; fixed Turkish/English ratio/fading day yoktur.

Canonical: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`.


### 8.5 7E Technical English Mastery Profile — TEPM-v0 / D-067

English learner-facing mastery görünümü exact D01 15 Skill'in GRE/RVR-backed state'inden türetilir; yeni mastery engine değildir. 8 derived Skill presentation state, qualified A1/A2/B1 Technical English base profile, uneven-profile preservation ve B2+ per-capability extension evidence kabul edildi. `review_due` bandı düşürmez; `verification_due` ilk contradiction sonrası uncertainty gösterir; confirmed remediation current profile'ı clean evidence ile yeniden türetir ve historical confirmation provenance korunur. General-English/official CEFR claim veya numeric aggregate yoktur.

Canonical: `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md`.


## 9. Professional-readiness depth

```text
concept
→ guided application
→ independent application
→ debugging
→ explanation
→ transfer
→ delayed retention
→ integrated project
→ performance / production context
```

Uzun curriculum ayrıca Git, testing, build systems, profiling, design docs, observability, incident/postmortem thinking, open-source workflow ve technical communication kapsar.

## 10. Curriculum planning / production ayrımı

```text
AŞAMA 5 = graph/schema/domain backbone
AŞAMA 6 = detailed granular capability map
AŞAMA 15 = first 8–12 week production content
AŞAMA 20 = full professional curriculum + OSS + career + capstones
```

## 10.1 Local manager takeover — D-055

Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. Canonical bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; GitHub durable source of truth ve D-024/D-027/D-050 PRE/POST workflow değişmez. D-055 transition kendi başına numbered step değildir; sonraki 6B execution normal onay/protokol ile tamamlanmıştır.

## 11. Güncel yürütme konumu

- AŞAMA 1–5 ✅
- **AŞAMA 6 ✅ tamamlandı — S6ERQA-v0 / D-062**
  - 6A ✅ GNS-v0 / D-054
  - 6B ✅ FRDB-v0 / D-056
  - 6C ✅ FDM-v0 / D-057
  - 6D ✅ SDM-v0 / D-058
  - 6E ✅ GIM-v0 / D-059
  - 6F ✅ PEM-v0 / D-060
  - 6G ✅ WLRM-v0 / D-061
  - 6H ✅ S6ERQA-v0 / D-062
- **AŞAMA 7 ✅ tamamlandı**
  - 7A ✅ EED-v0 / D-063
  - 7B ✅ TECP-v0 / D-064
  - 7C ✅ DECP-v0 / D-065
  - 7D ✅ TEIP-v0 / D-066
  - 7E ✅ TEPM-v0 / D-067
- **8A ✅ Bilgi mimarisi — UXIA-v0 / D-068**
- **8B ✅ Ana ekran — THUX-v0 / D-069**
- **8C ✅ Günlük çalışma akışı — TRUX-v0 / D-070**
- **8D ✅ Sınav UX — ASUX-v0 / D-071**
- **8E 🟡 Skill/progress/weakness UX — AKTİF, HENÜZ YÜRÜTÜLMEDİ**
- 8F–20 ⬜

Final Stage 6 graph: **549 Skill / 608 Objective / 950 prerequisite edge / 549/549 hard DAG**. WLRM final registry coverage 549/608; 10/10 6H review resolved.

**Sıradaki numaralı çalışma 8E'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.

## 11.1 8A UX Information Architecture — UXIA-v0 / D-068

AŞAMA 8'in semantic information architecture temeli kilitlendi. Primary shell exactly four destination kullanır: `Today · Learn · Progress · Profile`; normal entry `Today`'dir. Assessment, Technical English, AI Tutor, retention/remediation gibi engine/workflow'lar ayrı top-level silo değildir. Exact Skill detail shared surface'tir; planner explanation PDT-v0 reason trace'ten türetilir; browse hierarchy prerequisite graph yerine geçmez. Visual layout/design system ve runtime navigation implementation 8B–10'a bırakılmıştır.

Canonical: `docs/INFORMATION_ARCHITECTURE_SPEC.md` / D-068.

## 11.2 8B Today Home UX — THUX-v0 / D-069

Today/Home semantic contract action-first olarak kilitlendi. Dominant content current valid action'dır; queue yalnız selected PlannedTask'lardan oluşur. Daily capacity time budget context'idir, mastery/progress değildir; Today current-day override ile replan tetikleyebilir. Planner reasons PDT-v0 trace facts'ten bounded biçimde türetilir. Assessment ve Technical English contextual kalır; quota/streak/debt/gradebook shortcuts yoktur. Missed-day fresh-plan, offline/local-core, AI-degraded ve data-recovery semantics açıkça ayrılır. Fixed visual/card/pixel geometry 8F/8G'ye; Task Runner interaction choreography 8C'de kilitlenmiştir.

Canonical: `docs/TODAY_HOME_SCREEN_SPEC.md` / D-069.

## 11.3 8C Daily Working Flow — TRUX-v0 / D-070

Focused günlük çalışma akışı kilitlendi. Task Runner bir execution surface'tir; planner, mastery engine, prerequisite engine veya evidence evaluator değildir. Working session emergent ve ungraded'dır; required task count/duration/percentage yoktur. Tek bir shared focused-flow frame hem Task Runner hem assessment session tarafından devralınır; assessment interior 8D'ye aittir. Lifecycle `enter → orient → work → submit → resolve → transition` olup entry/resume revalidation deterministiktir ve prerequisite/content-version bypass edilemez. Assistance daima talep edilebilir, yalnız talep üzerine H1→H4 yükselir ve H3/H4 öncesi consequence ölçüm dilinde açıklanır; solution exposure sonrası same-item mastery path yoktur ve recheck scheduling planner-owned kalır. Provenance sorulur, çıkarsanmaz; dürüst beyan cezasızdır. In-flight run replan'dan korunur; continuity recomputed planner selection kullanır. AI evaluator yoksa attempt `evaluation_pending` olur ve evidence yazılmaz. Guilt/streak/debt framing, countdown pressure ve cached-list ilerleme yasaktır.

Canonical: `docs/DAILY_WORKING_FLOW_SPEC.md` / D-070.

## 11.4 8D Assessment Session UX — ASUX-v0 / D-071

Assessment session interior ve result presentation kilitlendi. Session bir evidence-collection workflow'udur; gradebook, score-based mastery authority veya ikinci state engine değildir. Üç scope (daily/weekly/monthly) için **tek interior** kullanılır; scope yalnız gösterilen context'tir ve ek evidence ağırlığı kazandırmaz. Submission birimi atomic evidence boundary'dir; bölünmez ve kısmen puanlanmaz. Submit edilen boundary dondurulur; açık blok içinde submit edilmemiş boundary'ler serbestçe gezilebilir. Skip meşrudur ve incorrect sayılmaz. `h0_required` varsayılanı ile allowed-tools policy cevap öncesi açıklanır; objective-appropriate tool kullanımı H0'ı bozmaz. Yardım engellenmez; H1/H2 assisted, H3/H4 solution-exposed olur ve fresh unseen item gerektirir; conversion açık ve cezasızdır, recheck planner-owned kalır. Resume'da unresolved slot beş koşuldan biriyle recompose edilir ve completed valid evidence silinmez. Incomplete session partial olabilir; exam debt yoktur. Item dispute evidence'ı contested tutar fakat auto-invalidate etmez ve undo button değildir. Provisional her yerde etiketlidir; invalid ne kredi ne ceza verir. Result semantic'tir ve altı family kullanır; pass/fail banner, yüzde/harf notu, geçme eşiği ve broad score yasaktır. `not_reliably_measured` first-class'tır. State-change iddiası yalnız canonical değişimde yapılır; mastered Skill'de ilk contradiction `verification_due` açar.

Canonical: `docs/ASSESSMENT_SESSION_UX_SPEC.md` / D-071.

## 12. Proje hafızası / repository hygiene — D-050

Her numaralı adım sonunda yaşayan current-state dosyaları istisnasız kontrol edilir. `PROJECT_CONTEXT.md`, `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG` ve `DECISIONS` eski step'te bırakılamaz. Ayrıca repo-wide stale-reference taraması yapılır.

Dosya rol matrisi ve exact checklist: `docs/PROJECT_MEMORY_PROTOCOL.md`.

D-043 standalone specialization-stage kararı geri çekilmiştir; canonical değildir.
