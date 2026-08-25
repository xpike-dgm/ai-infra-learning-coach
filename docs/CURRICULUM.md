# Curriculum Backbone — Canonical Summary

**Durum:** 5A DOMAIN BACKBONE + 5B GRAPH CONTRACT + 5C V1 FOUNDATION BACKBONE TAMAMLANDI / DETAIL AŞAMA 6'DA  
**Canonical kararlar:** D-041, D-042, D-044, D-049, D-051, D-052  
**5A ana kaynak:** `docs/CURRICULUM_DOMAIN_MAP.md`  
**5B graph contract:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`  
**5D graph QA:** `docs/GRAPH_ARCHITECTURE_QA.md` — GQA-v0 / D-053  
**5C V1 foundation backbone:** `docs/V1_FOUNDATION_BACKBONE.md`  
**Granular decomposition charter:** `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`

Bu dosya hızlı curriculum özetidir. 5A domain-level canonical ilişkileri `docs/CURRICULUM_DOMAIN_MAP.md`; 5B entity/relation/version/migration sözleşmesini `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`; 5C V1 başlangıç authoring-seed subgraph'ını `docs/V1_FOUNDATION_BACKBONE.md` tanımlar. Gerçek full-route Module/Topic/Skill/Learning Objective dataset'i AŞAMA 6 tamamlanmadan “full curriculum” sayılmaz.

## Ana route family'leri

1. Technical English — paralel
2. Python
3. C
4. Linux + Git + Shell
5. Data Structures & Algorithms foundations
6. Modern C++
7. Computer Architecture
8. Operating Systems + Memory
9. Concurrency / Parallel Programming
10. Networking
11. Distributed Systems + Storage / Databases foundations
12. Containers / Cloud / Observability
13. Performance Engineering & Profiling
14. GPU Architecture
15. CUDA
16. Triton
17. ML + Transformer foundations
18. LLM Inference Internals
19. vLLM / SGLang / TensorRT-LLM-style serving systems
20. KV Cache / Batching / Scheduling / Quantization
21. Multi-GPU + NCCL + RDMA
22. AI Infrastructure / GPU Infrastructure
23. Open Source contributions + real large projects + professional capstones

Bu text order roadmap summary'dir; canonical planner lineer değildir.

## PDM-v0 high-level yapı

```text
Technical English ─────────────────────────────────────────▶ parallel

Python + C + Linux/Git/Shell
        ↓
DS&A + Modern C++ + Architecture + OS/Memory
        ↓
Concurrency + Networking
        ↓
Distributed Systems / Storage + Cloud / Observability
        ↓
Performance Engineering ───────────────────────────────┐
        ↓                                               │
GPU Architecture → CUDA → Triton                       │
        ↓                                               │
ML/Transformer support → LLM Inference Internals       │
        ↓                                               │
Serving Systems → KV/Batching/Scheduling/Quantization ◀┘
        ↓
Multi-GPU / NCCL / RDMA
        ↓
AI / GPU Infrastructure
```

Professional engineering, source reading, testing, debugging, Git/PR, benchmarks, reliability/security fundamentals, OSS contributions ve projects/capstones route boyunca artarak ilerler; yalnız finalde başlamaz.

## Bağlayıcı sınırlar

- Domain/Module/Topic mastery atomu değildir; canonical mastery ana seviyesi Skill, evidence Objective'tir.
- Domain-level relationships authoring guidance'dır; runtime hard prerequisite Skill→Skill PRG-v0 ile çözülür.
- Technical English global hard prerequisite değildir.
- Python C/C++'ın yerine geçmez.
- ML/Transformer generic ML-research specialization değildir; inference için supporting depth taşır.
- Performance sona bırakılmaz; measurement/profiling route boyunca büyür.
- Tool/vendor adları stable concept değildir; version/freshness metadata ile yönetilir.
- Gerekli math/numerical/security/reliability knowledge hidden prerequisite bırakılamaz; AŞAMA 6'da explicit capability'lere dönüştürülür.

## Sonraki curriculum işleri

```text
5B ✅ = KGC-v0 graph / metadata contract
5C ✅ = FBB-v0 first 8–12 week scope-equivalent V1 foundation backbone
5D ✅ = GQA-v0 graph architecture QA + corrective seed patch — active next
6A 🟡 = granularity + naming standardı
6B–6H = full granular capability map + independent coverage/prerequisite Research QA
15 = first production-quality lesson/task/assessment package
20 = full professional content expansion + OSS + capstones
```

> Ayrıntılı domain açıklamaları ve parallel/prerequisite authoring ilişkileri için `docs/CURRICULUM_DOMAIN_MAP.md` canonical kaynaktır.