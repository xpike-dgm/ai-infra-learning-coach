# Granular Capability Map Plan — AŞAMA 6 Charter

**Durum:** YÜRÜTÜLÜYOR — 6A–6G TAMAMLANDI / 6H AKTİF
**Tarih:** 2026-08-25  
**Karar:** D-044  
**5B canonical schema contract:** `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` / KGC-v0 / D-051
**5C V1 seed input:** `docs/V1_FOUNDATION_BACKBONE.md` / FBB-v0 / D-052
**5D architecture QA input:** `docs/GRAPH_ARCHITECTURE_QA.md` / GQA-v0 / D-053
**6A granularity/naming standard:** `docs/GRANULARITY_NAMING_STANDARD.md` / GNS-v0 / D-054
**6B decomposition blueprint:** `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md` / FRDB-v0 / D-056
**6C Foundations detailed map:** `docs/FOUNDATIONS_DETAILED_MAP.md` + `curriculum/decomposition/6c_foundations/` / FDM-v0 / D-057

Bu belge yeni **AŞAMA 6 — Granular Capability Map / Öğrenme Rotasını Alt Becerilere Bölme** aşamasının amacını ve acceptance kapsamını tanımlar.

## 1. Amaç

Uzun öğrenme rotasındaki büyük başlıkları yalnız `Python`, `Linux`, `CUDA`, `Networking` gibi geniş alanlar halinde bırakmamak; her birini uygulamanın öğretebileceği, ölçebileceği, zayıflığı ayrı yakalayabileceği ve remediation üretebileceği ayrıntılı bir yapıya bölmek.

Canonical yapı:

`Domain → Module → Topic → Skill → Learning Objective`

Bu aşama içerikleri henüz yazmaz. Önce bütün profesyonel rotanın **öğrenme haritasını / capability taxonomy'sini** üretir.

## 2. Neden gerekli?

Uygulama yalnızca `Python zayıf` dememelidir.

Örneğin kullanıcı:
- değişkenleri biliyor,
- koşullu ifadelerde iyi,
- `for` döngüsünde orta,
- `while` ve loop termination mantığında zayıf,
- function scope'ta tekrar gerektiriyor

olabilir.

Bu nedenle geniş Domain/Topic özetleri kullanıcıya gösterilebilir; fakat gerçek tanı, mastery, remediation ve prerequisite davranışı **Skill / Learning Objective** seviyesinde çalışmalıdır.

Örnek:

```text
Python
└── Control Flow
    ├── Conditional Expressions
    │   ├── if / elif / else
    │   └── boolean condition composition
    └── Loops
        ├── for iteration
        ├── while iteration
        ├── break / continue
        ├── range / iterable behavior
        └── loop termination / common bugs
```

Böylece uygulama bütün Python'ı tekrar ettirmek yerine yalnız eksik capability'leri hedefleyebilir.

## 3. Kapsanacak ana rota

AŞAMA 6 aşağıdaki rotanın tamamını ayrıntılı alt bölümlere ayırmayı planlar:

1. Technical English — paralel hat
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
19. vLLM / SGLang / TensorRT-LLM-style systems
20. KV Cache / Batching / Scheduling / Quantization
21. Multi-GPU + NCCL + RDMA
22. AI Infrastructure / GPU Infrastructure
23. Open Source contributions + real large projects + capstones

## 4. Python örnek decomposition beklentisi

AŞAMA 6 yürütüldüğünde Python yalnız `Python Foundations` adıyla bırakılmayacaktır. En azından aşağıdaki family'ler araştırılıp gerçek Skill/Objective haritasına dönüştürülecektir:

- syntax / values / types
- variables and assignment
- operators and expressions
- input/output
- conditionals
- loops
- strings
- lists / tuples / sets / dictionaries
- functions, parameters, return values
- scope
- modules / imports
- files and paths
- errors / exceptions
- debugging
- comprehensions / iteration model
- classes / objects where required
- type hints / typing
- testing
- virtual environments / dependency management
- packaging
- CLI / automation scripting
- subprocess / OS interaction
- networking basics
- async / concurrency
- multiprocessing
- profiling / performance
- data manipulation foundations
- NumPy / tensor/PyTorch-facing Python where route requires
- infrastructure / benchmark scripting

Bu liste final curriculum değildir; AŞAMA 6 Research + QA sırasında eksikler, fazlalıklar ve prerequisite sırası doğrulanır.

## 5. Granularity standardı

Bir Skill:
- bağımsız olarak öğretilebilir/uygulanabilir olmalı,
- ayrı evidence ile ölçülebilmeli,
- başka bir Skill'den anlamlı biçimde ayrılabilmeli,
- zayıf olduğunda hedefli remediation üretilebilmeli,
- gereksiz mikro-parçalanma yaratmamalıdır.

`Python`, `Loops`, `CUDA` gibi çok geniş başlıklar mastery atomu değildir.

Öte yandan her keyword veya syntax karakteri için ayrı Skill yaratılmaz. Granularity, **tanı + öğretim + evidence + prerequisite** açısından anlamlı sınırda tutulur.

## 6. Her node için planlanacak metadata

AŞAMA 6 final haritası KGC-v0 entity/relation/version contract'ına uymalıdır. Uygun seviyede en az şu bilgiler bulunmalıdır:

- canonical ID
- parent / curriculum placement
- prerequisite Skill edges (`hard | soft`)
- required / critical / optional rol
- teaching depth expectation
- evidence modalities / required evidence family
- assessment eligibility
- retention relevance
- remediation tags
- diagnostic eligibility
- professional capability tags
- cross-domain reuse
- project/capstone attribution
- version / source / freshness metadata

## 7. AŞAMA 6 alt adımları

### 6A — Granularity ve naming standardı ✅
Canonical: `docs/GRANULARITY_NAMING_STANDARD.md` — GNS-v0 / D-054.
- Domain/Module/Topic/Skill/Objective semantic sınırları
- stable canonical logical ID convention
- under/over-fragmentation guard
- shared-vs-specific Skill split policy
- Objective atomicity + FBB seed ratification lifecycle

### 6B — Full-route decomposition blueprint ✅
Canonical: `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md` — FRDB-v0 / D-056.
- 23 ana route family için ortak machine-readable decomposition package,
- organization/Skill/Objective/relation row contracts,
- cross-domain shared Skills ve duplicate prevention,
- FBB mapping, review queue ve package QA contract.

### 6C — Foundations detailed map ✅
Canonical: `docs/FOUNDATIONS_DETAILED_MAP.md` + `curriculum/decomposition/6c_foundations/` — FDM-v0 / D-057.
- Technical English
- Python
- C
- Linux / Git / Shell
- DS&A foundations

### 6D — Systems detailed map ✅
Canonical: `docs/SYSTEMS_DETAILED_MAP.md` + `curriculum/decomposition/6d_systems/` — SDM-v0 / D-058.
- Modern C++
- Computer Architecture
- OS / Memory
- Concurrency / Parallelism
- Networking
- Distributed Systems
- Storage / Databases
- Containers / Cloud / Observability
- Performance / Profiling

### 6E — GPU / ML / Inference detailed map ✅
- GPU Architecture
- CUDA
- Triton
- ML / Transformer foundations
- LLM inference internals
- serving engines
- KV cache / batching / scheduling / quantization
- multi-GPU / NCCL / RDMA
- AI Infrastructure / GPU Infrastructure

### 6F — Professional engineering / project map ✅
- Git/open-source workflow
- testing/build/debugging/profiling
- design docs / benchmark reports
- large integrated projects
- capstone capability decomposition

### 6G — Weakness localization ve remediation mapping ✅
Canonical: `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` + `curriculum/decomposition/6g_weakness_remediation/` — WLRM-v0 / D-061.
- her Skill/Objective için ölçülebilir weakness state
- hangi hata hangi alt capability'ye yazılır?
- broad-domain overreaction guard
- targeted reteach/practice/retest mapping

### 6H — Coverage + prerequisite + external research QA 🟡 AKTİF
- hidden prerequisite audit
- missing-domain audit
- duplicate Skill audit
- dead-end/cycle audit
- current industry/tool relevance audit
- Research AI ile bağımsız coverage validation

## 8. AŞAMA 5 ile sınır

**AŞAMA 5** graph'ın iskeletini ve metadata sözleşmesini tasarlar.  
**AŞAMA 6** o iskeleti kullanarak profesyonel rotayı ayrıntılı capability haritasına böler.

Bu ayrım bilinçlidir:

```text
AŞAMA 5 = graph/schema/backbone
AŞAMA 6 = detailed capability taxonomy and weakness-addressable map
AŞAMA 15 = V1 için ilk gerçek lesson/task/assessment content üretimi
AŞAMA 20 = full professional content expansion
```

## 9. Learning Engine ile uyum

D-021 korunur: mastery'nin canonical ana seviyesi Skill, evidence Objective'e bağlanabilir.

AŞAMA 6 yeni bir mastery sistemi kurmaz. Mevcut GRE/RVR/PRG/planner sistemlerinin üzerinde çalışacağı **daha ayrıntılı ve gerçek curriculum node'larını** tanımlar.

Sonuç olarak kullanıcı için şu ayrım mümkün olur:

```text
Python overall: learning
  Variables: strong
  Conditionals: mastered
  Loops: remediation_required
    for iteration: weak
    while termination: weak
    break/continue: stable
  Functions: learning
```

Domain/Module/Topic özetleri derived görünüm olabilir; gerçek müdahale Skill/Objective seviyesine hedeflenir.

## 10. Acceptance gate

AŞAMA 6 ancak şu koşullarda tamamlanır:

- listedeki bütün ana route family'ler decomposition planına dahil,
- her family için Module/Topic/Skill/Objective seviyesi yeterli ayrıntıda,
- broad `X konusunda zayıf` yerine hedefli weakness localization mümkün,
- prerequisite graph kurulabilir durumda,
- assessment/evidence bağları kurulabilir durumda,
- duplicate/cycle/hidden-prerequisite QA geçmiş,
- profesyonel hedefte kritik boşluk bulunmadığına dair bağımsız Research/QA yapılmış,
- sonraki English/UX/architecture/content aşamaları bu haritayı doğrudan kullanabilir durumda.
