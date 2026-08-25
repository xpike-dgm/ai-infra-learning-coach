# Project Context — Kısa Yaşayan Proje Hafızası

**Son senkron:** 2026-08-25  
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

## 8. İngilizce

English teknik eğitimin global ön koşulu değildir; ilk günden paralel ilerler. Bilinmeyen grammar/vocabulary teknik assessment'ta gizli prerequisite olamaz. Granular English capability map AŞAMA 6C'de; English-specific progression/CEFR/cadence/integration AŞAMA 7'de kesinleşir.

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

Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. Canonical bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; GitHub durable source of truth ve D-024/D-027/D-050 PRE/POST workflow değişmez. Transition 6B'yi yürütmez; current execution hâlâ 6A ✅ / 6B 🟡 not-executed.

## 11. Güncel yürütme konumu

- AŞAMA 1 ✅
- AŞAMA 2 ✅
- AŞAMA 3 ✅
- AŞAMA 4 ✅
- AŞAMA 5 ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6:
  - **6A ✅ GNS-v0 / D-054**
  - **6B 🟡 Full-route decomposition blueprint — AKTİF, HENÜZ YÜRÜTÜLMEDİ**
  - 6C–6H ⬜
- AŞAMA 7–20 ⬜

**Sıradaki numaralı çalışma 6B'dir.** 6B başlamadan fresh PRE-STEP GitHub refresh zorunludur.

## 12. Proje hafızası / repository hygiene — D-050

Her numaralı adım sonunda yaşayan current-state dosyaları istisnasız kontrol edilir. `PROJECT_CONTEXT.md`, `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG` ve `DECISIONS` eski step'te bırakılamaz. Ayrıca repo-wide stale-reference taraması yapılır.

Dosya rol matrisi ve exact checklist: `docs/PROJECT_MEMORY_PROTOCOL.md`.

D-043 standalone specialization-stage kararı geri çekilmiştir; canonical değildir.