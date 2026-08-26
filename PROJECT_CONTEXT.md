# Project Context — Kısa Yaşayan Proje Hafızası

**Son senkron:** 2026-08-27
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

AŞAMA 6, Technical English'ten AI Infrastructure ve professional capstone'a kadar bütün rotayı ölçülebilir alt Skill/Objective haritasına bölecektir.

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

**D-057 / FDM-v0:** canonical summary `docs/FOUNDATIONS_DETAILED_MAP.md`, dataset `curriculum/decomposition/6c_foundations/`. D01–D05; 5 Domain, 14 Module, 46 Topic, 132 Skill, 137 Objective, 145 TopicSkillLink ve 200 prerequisite edge ile internally mapped; FBB 41/47 seed mapping complete, hard graph DAG, 0 blocking review. External validation 6H'ye pending.

**D-058 / SDM-v0:** canonical summary `docs/SYSTEMS_DETAILED_MAP.md`, dataset `curriculum/decomposition/6d_systems/`. D06–D13; 8 Domain, 21 Module, 64 Topic, 192 Skill, 207 Objective, 224 TopicSkillLink ve 313 prerequisite edge (259 hard / 54 soft). 43 accepted 6C Skill clone'lanmadan reuse edildi; 6C+6D birleşik hard graph DAG 324/324; 0 blocking review. External validation 6H'ye pending.

**D-059 / GIM-v0:** canonical summary `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`, dataset `curriculum/decomposition/6e_gpu_ml_inference/`. D14–D22; 9 Domain, 27 Module, 70 Topic, 143 Skill, 159 Objective, 230 TopicSkillLink ve 279 prerequisite edge (247 hard / 32 soft). 59 prior Skill clone'lanmadan reuse edildi; 6C+6D+6E combined hard graph DAG 467/467; 0 blocking review. External validation 6H'ye pending.

**D-060 / PEM-v0:** canonical summary `docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md`, dataset `curriculum/decomposition/6f_professional_engineering/`. D23; 1 Domain, 9 Module, 27 Topic, 76 Skill, 87 Objective. Existing D01–D22 technical capability'leri clone edilmeden professional project/capstone context'lerinde reuse edildi; 0 blocking review. External validation 6H'ye pending.

## 8. İngilizce

English teknik eğitimin global ön koşulu değildir; ilk günden paralel ilerler. Bilinmeyen grammar/vocabulary teknik assessment'ta gizli prerequisite olamaz. Granular English capability map AŞAMA 6C / FDM-v0'da tamamlandı; 6D Systems package'ında da English→technical hard gate yoktur. English-specific progression/CEFR/cadence/integration AŞAMA 7'de kesinleşir.

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

- AŞAMA 1 ✅
- AŞAMA 2 ✅
- AŞAMA 3 ✅
- AŞAMA 4 ✅
- AŞAMA 5 ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6:
  - **6A ✅ GNS-v0 / D-054**
  - **6B ✅ FRDB-v0 / D-056**
  - **6C ✅ FDM-v0 / D-057**
  - **6D ✅ SDM-v0 / D-058**
  - **6E ✅ GIM-v0 / D-059**
  - **6F ✅ PEM-v0 / D-060**
  - **6G 🟡 Weakness localization + remediation mapping — AKTİF, HENÜZ YÜRÜTÜLMEDİ**
  - 6H ⬜
- AŞAMA 7–20 ⬜

**Sıradaki numaralı çalışma 6G'dir.** 6G başlamadan fresh PRE-STEP GitHub refresh; FDM-v0 + SDM-v0 + GIM-v0 + PEM-v0 remediation metadata/review handoff setlerinin yeniden okunması zorunludur.

## 12. Proje hafızası / repository hygiene — D-050

Her numaralı adım sonunda yaşayan current-state dosyaları istisnasız kontrol edilir. `PROJECT_CONTEXT.md`, `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG` ve `DECISIONS` eski step'te bırakılamaz. Ayrıca repo-wide stale-reference taraması yapılır.

Dosya rol matrisi ve exact checklist: `docs/PROJECT_MEMORY_PROTOCOL.md`.

D-043 standalone specialization-stage kararı geri çekilmiştir; canonical değildir.
