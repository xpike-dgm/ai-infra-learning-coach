# Curriculum Domain Map — PDM-v0 Professional Domain Backbone

**Adım:** 5A — Ana domain haritası  
**Durum:** TAMAMLANDI  
**Tarih:** 2026-08-25  
**Final model:** `PDM-v0 — Professional Domain Backbone`

Bu belge AI Infra Learning Coach'un 4+ yıllık professional-readiness curriculum'unun **yüksek seviyeli domain envelope'ını**, ana domain sınıflarını ve domainler arası prerequisite/parallel ilişkileri tanımlar.

Bağlayıcı kaynaklar:
- `docs/PROFESSIONAL_READINESS_TARGET.md` — D-041 / D-042 / D-044
- `docs/PRODUCT_REQUIREMENTS.md`
- `docs/LEARNING_ENGINE_SPEC.md` — `Domain → Module → Topic → Skill → Learning Objective`
- `docs/PREREQUISITE_POLICY_SPEC.md` — runtime prerequisite ana seviyesi Skill
- `docs/GRANULAR_CAPABILITY_MAP_PLAN.md` — AŞAMA 6
- `docs/ENGLISH_FOUNDATION_RULES.md`
- `docs/MASTER_PLAN.md`

Ana ilke:

> **Bu domain map bir takvim veya tek çizgili kurs sırası değildir. Domainler curriculum organizasyonu ve professional coverage sınırlarını belirler; gerçek runtime kilitler AŞAMA 5B/6 ve PRG-v0 ile Skill seviyesinde çözülür.**

İkinci ilke:

> **5A'nın işi bütün alt konuları yazmak değil, profesyonel hedef için hangi geniş capability alanlarının var olması gerektiğini ve birbirleriyle nasıl ilişkileneceğini kilitlemektir. Module/Topic/Skill/Objective ayrıntısı AŞAMA 6'ya aittir.**

Üçüncü ilke:

> **Ana hedef yalnız “AI araçları kullanabilmek” değildir. Ortak systems/distributed/performance temeli üzerinde GPU ve inference katmanına ilerleyen, gerektiğinde AI hype'ından bağımsız systems rollerine transfer olabilen mühendislik capability'si hedeflenir.**

---

# 1. Domain sınıfları

PDM-v0 domainleri tek bir `foundation → advanced` listesine sıkıştırmaz. Her domain şu yüksek seviye rollerden birine veya birkaçına sahip olabilir:

```text
parallel_track
common_foundation
systems_core
distributed_platform_core
performance_core
accelerator_core
supporting_domain
inference_systems_core
target_infrastructure
professional_evidence_layer
```

Bu roller mastery state değildir ve sayısal ağırlık değildir. Amaç curriculum authoring sınırlarını açıklamaktır.

---

# 2. Sıfırdan giriş köprüsü

Uzun rotadaki ilk canonical teknik domain Python'dır; ancak gerçekten sıfırdan başlayan kullanıcı için **Computer / Programming Fundamentals** başlangıç köprüsü bulunur.

Bu köprü ayrı bir 4+ yıllık uzmanlık domain'i olmak zorunda değildir; AŞAMA 15'in ilk production paketi içinde aşağıdaki tür temel capability'leri hazırlayabilir:
- bilgisayar/program çalıştırma mental modeli,
- dosya/klasör ve temel terminal kavramları,
- program, source code, interpreter/compiler farklarının başlangıç modeli,
- değişken/ifade/koşul/iteration gibi programlama kavramlarına hazırlık,
- hata mesajı ve debugging'e giriş.

Köprü takvimsel “ön kurs” değildir. Python/C/Linux içindeki gerçek Skill prerequisite'leri karşılandıkça kullanıcı ilgili branch'lere geçer.

---

# 3. Canonical professional domain envelope

AŞAMA 6 aşağıdaki **23 ana route family** üzerinde granular decomposition yapacaktır.

## D01 — Technical English
**Rol:** `parallel_track`, `professional_evidence_layer`

Amaç:
- A0'dan başlayarak teknik dokümantasyon, compiler/terminal mesajları, README/man page, GitHub issue/PR, design doc, API/GPU docs, paper ve teknik iletişim capability'sini paralel geliştirmek.

Sınır:
- teknik domainlerin ön koşulu olarak bütün İngilizceyi bitirme şartı yoktur,
- bilinmeyen English grammar teknik assessment'ta hidden prerequisite olamaz,
- ilerledikçe Turkish/bilingual scaffold azaltılır fakat takvime göre değil evidence'a göre.

İlişki:
- **day-one parallel**,
- bütün teknik domainlerle cross-domain reuse.

## D02 — Python
**Rol:** `common_foundation`

Amaç:
- programlama temeli,
- automation/scripting,
- testing/benchmark araçları,
- infrastructure glue,
- data/tensor/ML ekosistemi,
- ileri aşamada async/multiprocessing/networking/profiling/packaging.

Sınır:
- C/C++/CUDA yerine geçmez,
- yalnız notebook/script düzeyinde bırakılmaz,
- production-quality Python capability'si ileri curriculum'da derinleşir.

İlişki:
- sıfırdan girişte ilk ana programming branch,
- Linux/C ile erken dönemde paralelleşebilir,
- Triton, ML/Transformer, serving tooling ve AI Infra'da yeniden kullanılır.

## D03 — C
**Rol:** `common_foundation`, `systems_core`

Amaç:
- düşük seviye programlama mental modeli,
- addresses/pointers/memory/lifetime,
- compilation/translation model,
- systems debugging ve native tooling temeli.

Sınır:
- C tek başına final systems stack değildir,
- hedef syntax ezberi değil memory/execution modelini gerçek kodla anlamaktır.

İlişki:
- Python ile kısmen paralel ilerleyebilir,
- Modern C++, Architecture, OS/Memory ve CUDA için önemli prerequisite source'tur.

## D04 — Linux + Git + Shell
**Rol:** `common_foundation`, `professional_evidence_layer`

Amaç:
- Linux çalışma ortamı,
- filesystem/process/toolchain,
- shell automation,
- Git version-control workflow,
- compiler/debugger/profiler kullanım zemini.

Sınır:
- Linux yalnız komut ezberi değildir,
- Git yalnız `commit/push` değildir; ileride branch/PR/review/recovery workflow'a genişler.

İlişki:
- Python/C ile erken başlar,
- bütün systems/GPU/infrastructure domainlerinin çalışma zemini.

## D05 — Data Structures & Algorithms Foundations
**Rol:** `common_foundation`, `supporting_domain`

Amaç:
- complexity reasoning,
- temel veri yapıları ve algoritmik problem decomposition,
- performans/memory locality düşüncesine hazırlık.

Sınır:
- competitive-programming uzmanlığı ana hedef değildir,
- route için gereken mühendislik derinliği ve transfer capability'si hedeflenir.

İlişki:
- basic programming capability sonrası başlar,
- C++/distributed/storage/performance alanlarında tekrar kullanılır.

## D06 — Modern C++
**Rol:** `systems_core`

Amaç:
- RAII/ownership/lifetime,
- STL/generic programming,
- modern language/tooling,
- performance-sensitive production systems kodu.

Sınır:
- yalnız dil feature listesi değildir,
- build/test/debug/profile ile birlikte professional systems engineering bağlamında öğretilir.

İlişki:
- C + Linux toolchain + temel memory modeli üzerine oturur,
- concurrency, networking, distributed, GPU host-side ve inference engine source reading için ana dildir.

## D07 — Computer Architecture
**Rol:** `systems_core`, `performance_core`

Amaç:
- CPU execution, ISA-level mental model,
- cache/memory hierarchy,
- pipeline/branch/SIMD,
- latency/throughput düşüncesi.

Sınır:
- chip-design specialization'a dönüşmez,
- systems/GPU performance reasoning için gereken architecture depth hedeflenir.

İlişki:
- C/memory foundations ile birlikte ilerler,
- OS, performance ve GPU architecture için kritik temel sağlar.

## D08 — Operating Systems + Memory
**Rol:** `systems_core`

Amaç:
- processes/threads,
- virtual memory/pages/TLB,
- syscalls/file descriptors,
- scheduling/I/O/memory mapping/allocators,
- userspace ↔ kernel mental modeli.

Sınır:
- yalnız OS teorisi değildir; Linux gözlemleri ve debugging ile bağlanır.

İlişki:
- C + Linux + Architecture foundations üzerine oturur,
- concurrency/networking/containers/performance/distributed için temel oluşturur.

## D09 — Concurrency / Parallel Programming
**Rol:** `systems_core`

Amaç:
- threads/tasks,
- synchronization,
- atomics/memory ordering,
- race/deadlock,
- work decomposition/thread pools.

Sınır:
- yalnız API kullanımı değildir; correctness ve performance birlikte ele alınır.

İlişki:
- OS/thread/memory + C/C++ temelleri gerekir,
- networking/distributed/GPU parallel reasoning'e geçiş sağlar.

## D10 — Networking
**Rol:** `systems_core`, `distributed_platform_core`

Amaç:
- TCP/IP, DNS, HTTP/TLS foundations,
- sockets,
- blocking/non-blocking I/O,
- serialization/RPC,
- latency/bandwidth ve failure reasoning.

Sınır:
- network certification curriculum'u değildir,
- distributed/AI infrastructure için gereken systems networking depth hedeflenir.

İlişki:
- Linux/process/file-descriptor ve programming temeline dayanır,
- concurrency ile kısmen paralel ilerleyebilir,
- distributed systems, serving, cloud ve RDMA için temel sağlar.

## D11 — Distributed Systems + Storage / Databases Foundations
**Rol:** `distributed_platform_core`

Amaç:
- replication/partitioning/consistency,
- failure/timeouts/retries/idempotency,
- consensus/leader concepts,
- transactions/WAL/recovery,
- indexes/storage engines foundations,
- queues/streaming.

Sınır:
- database administrator veya generic data-engineering specialization değildir,
- AI infra/serving systems için gereken distributed state ve storage reasoning hedeflenir.

İlişki:
- networking + concurrency + OS foundations gerekir,
- storage alt branch'leri bazı DS&A/OS capability'leriyle daha erken paralelleşebilir,
- serving engines, cluster systems ve AI Infra için ana prerequisite family'dir.

## D12 — Containers / Cloud / Observability
**Rol:** `distributed_platform_core`, `professional_evidence_layer`

Amaç:
- namespaces/cgroups/container images,
- deployment/configuration,
- Kubernetes/cloud compute-network-storage foundations,
- logs/metrics/traces,
- SLO/reliability basics.

Sınır:
- generic cloud-certification roadmap değildir,
- infrastructure operation ve observability engineering capability'si hedeflenir.

İlişki:
- Linux + networking temelinden sonra başlayabilir,
- advanced orchestration/reliability distributed-systems bilgisinden faydalanır,
- serving/AI Infra ile sürekli entegre olur.

## D13 — Performance Engineering & Profiling
**Rol:** `performance_core`, `professional_evidence_layer`

Amaç:
- measurement methodology,
- reproducible benchmark,
- latency/throughput/tail latency,
- CPU/memory profiling,
- bottleneck attribution,
- capacity/cost trade-offs.

Sınır:
- sona bırakılan tek “optimization dersi” değildir.

İlişki:
- temel measurement alışkanlığı erken başlar,
- advanced profiling Architecture/OS/C++ üzerinde derinleşir,
- GPU/CUDA/inference/AI Infra boyunca cross-cutting capability'dir.

## D14 — GPU Architecture
**Rol:** `accelerator_core`, `performance_core`

Amaç:
- SIMT/warp/SM mental modeli,
- GPU memory hierarchy,
- divergence/coalescing/occupancy,
- compute-vs-bandwidth reasoning,
- tensor-core foundations.

Sınır:
- semiconductor/chip-design specialization değildir.

İlişki:
- Architecture + memory + performance foundations üzerine oturur,
- CUDA/Triton/inference optimization için kritik source domain'dir.

## D15 — CUDA
**Rol:** `accelerator_core`

Amaç:
- grids/blocks/threads,
- memory spaces/transfers,
- synchronization/shared memory/streams,
- kernel correctness/debugging,
- Nsight/profiling ve optimization patterns.

Sınır:
- syntax-only CUDA değildir; profiler ve hardware reasoning zorunlu derinliktir.

İlişki:
- C/C++ + GPU Architecture + performance/memory foundations gerekir,
- Triton, inference kernels ve multi-GPU için temel sağlar.

## D16 — Triton
**Rol:** `accelerator_core`, `inference_systems_core`

Amaç:
- Triton execution/programming model,
- blocked tensor programming,
- kernel authoring/benchmarking,
- PyTorch integration,
- inference-relevant kernel optimization.

Sınır:
- CUDA'nın yerine GPU temellerini atlama yolu değildir.

İlişki:
- Python + GPU Architecture + yeterli CUDA/memory/performance concepts,
- LLM inference optimization ile paralel ve uygulamalı ilerler.

## D17 — ML + Transformer Foundations
**Rol:** `supporting_domain`

Amaç:
- inference sistemlerini anlamak için gereken tensor/numerical model,
- gerekli linear algebra,
- neural-network basics,
- transformer/attention,
- training vs inference,
- precision/data formats.

Sınır:
- genel ML research/scientist curriculum'u değildir,
- model training specialization ana hedef değildir,
- gereken matematik/numerical depth bu domain içinde veya prerequisite module'lerinde verilir.

İlişki:
- Python foundation ile başlayabilir,
- LLM inference internals için zorunlu supporting capability sağlar,
- GPU/CUDA ile kısmen paralel ilerleyebilir.

## D18 — LLM Inference Internals
**Rol:** `inference_systems_core`

Amaç:
- tokenization'e gerekli giriş,
- prefill/decode,
- attention execution,
- KV cache mental model,
- memory/compute bottlenecks,
- latency/throughput trade-offs.

İlişki:
- ML/Transformer foundations + performance + GPU foundations,
- Python/C++ systems reading capability'siyle güçlenir,
- serving engines ve inference optimization için base domain'dir.

## D19 — vLLM / SGLang / TensorRT-LLM-style Serving Systems
**Rol:** `inference_systems_core`, `distributed_platform_core`

Amaç:
- request lifecycle,
- model loading/runtime,
- continuous batching,
- memory-management architecture,
- scheduler/runtime code reading,
- serving failure/reliability/observability.

Sınır:
- tek bir framework'ün UI/API tutorial'ı değildir,
- tool names değişse bile underlying systems concepts korunmalıdır.

İlişki:
- LLM inference + Linux/networking/distributed + Python/C++ + observability foundations,
- D20 optimization domain'i ve AI Infra ile sıkı entegrasyon.

## D20 — KV Cache / Batching / Scheduling / Quantization
**Rol:** `inference_systems_core`, `performance_core`

Amaç:
- KV layout/growth/reuse,
- batching/scheduling/fairness,
- memory pressure,
- precision/quantization foundations,
- accuracy ↔ memory ↔ performance trade-offs.

Sınır:
- yalnız kavram isimlerini bilmek değildir; benchmark ve system behavior evidence gerekir.

İlişki:
- LLM inference internals + serving systems + performance + ML numerical foundations gerekir.

## D21 — Multi-GPU + NCCL + RDMA
**Rol:** `accelerator_core`, `distributed_platform_core`

Amaç:
- collective communication,
- topology,
- NCCL,
- RDMA/RoCE/InfiniBand concepts,
- tensor/pipeline/data/expert parallel concepts,
- distributed GPU profiling/failure handling.

Sınır:
- networking'in yerine geçmez; high-speed distributed accelerator specialization katmanıdır.

İlişki:
- networking + distributed systems + GPU/CUDA + performance foundations,
- AI/GPU Infrastructure için kritik advanced source domain.

## D22 — AI Infrastructure / GPU Infrastructure
**Rol:** `target_infrastructure`

Amaç:
- GPU scheduling/cluster orchestration,
- serving architecture,
- autoscaling/load shedding,
- reliability/observability,
- capacity planning,
- cost/performance,
- deployment pipelines,
- incident/postmortem reasoning.

Sınır:
- tek ürün/cloud vendor specialization değildir,
- systems, distributed, GPU ve inference capability'lerinin integrated target layer'ıdır.

İlişki:
- D11/D12/D13/D18/D19/D20/D21 başta olmak üzere çoklu prerequisite branch'in birleştiği domain.

## D23 — Open Source Contributions + Real Large Projects + Professional Capstones
**Rol:** `professional_evidence_layer`

Amaç:
- repo/source-tree reading,
- issue reproduction,
- tests/benchmarks,
- PR/code review,
- design docs,
- reproducible engineering reports,
- multi-domain integrated projects,
- final professional capstone evidence.

Sınır:
- tüm curriculum bittikten sonra başlayan tek son bölüm değildir.

İlişki:
- erken aşamada küçük Git/project habits,
- orta aşamada integrated systems projects,
- ileri aşamada GPU/inference/AI Infra projects,
- final readiness için bağımsız capstone evidence.

---

# 4. Domain map lineer yol değildir

High-level progression anlaşılabilirlik için şöyle özetlenebilir:

```text
Technical English ───────────────────────────────────────────────▶ parallel

Zero-start bridge
   ├─▶ Python ───────────────────────────────┐
   ├─▶ C ───────────────┐                    │
   └─▶ Linux/Git/Shell ─┼────────────────────┤
                        ▼                    │
                 DS&A foundations            │
                        │                    │
                        ▼                    │
                    Modern C++               │
                        │                    │
      Computer Architecture ◀────────────────┘
                        │
                        ▼
                 OS + Memory
                  ├───────────────┐
                  ▼               ▼
              Concurrency      Networking
                  └──────┬────────┘
                         ▼
          Distributed Systems + Storage/DB
                         │
           ┌─────────────┼──────────────┐
           ▼             ▼              ▼
 Containers/Cloud/Obs  Performance   ML/Transformer support
           │             │              │
           │      Computer Architecture │
           │             ▼              │
           │       GPU Architecture ◀───┘
           │             │
           │           CUDA
           │          ┌──┴──┐
           │          ▼     ▼
           │        Triton  LLM Inference Internals
           │                    │
           └──────────────▶ Serving Systems
                                │
               Performance ─────┼──▶ KV/Batch/Schedule/Quantization
                                │
 Networking + Distributed + GPU/CUDA
                └──────────────▶ Multi-GPU/NCCL/RDMA
                                │
                                ▼
                       AI/GPU Infrastructure
```

Bu şema **curriculum display shortcut**'ıdır. AŞAMA 5B/6 gerçek graph'ta bazı branch'lerin daha erken paralel ilerlemesine izin verecektir.

---

# 5. Parallelism kuralları

## 5.1 Technical English
Bütün rota boyunca paraleldir. Teknik domainleri topluca bloke etmez.

## 5.2 Python / C / Linux
Tamamen seri yapılmaz. Başlangıç foundations oturduktan sonra kontrollü overlap beklenir:
- Python ile programming/automation,
- C ile low-level mental model,
- Linux/Shell/Git ile çalışma ortamı.

## 5.3 Architecture / Modern C++ / OS
Bu üç alan kısmen interleaved olabilir. Örneğin cache/lifetime/memory kavramları gerçek C/C++ örnekleriyle geri beslenebilir.

## 5.4 Performance
Tek final domain değildir. Measurement/benchmark habits erken başlar; advanced profiling daha sonra architecture/OS/GPU/inference üzerinde büyür.

## 5.5 ML/Transformer
GPU'nun tamamlanmasını beklemek zorunda değildir. Python ve gerekli math/tensor foundations sonrası başlayabilir. Ancak inference-system evidence için ilgili systems/GPU prerequisites ayrıca gerekir.

## 5.6 Professional projects / OSS
Finale ertelenmez. Küçük scoped artifacts erken başlar; difficulty/integration breadth curriculum ile birlikte artar.

---

# 6. Ana domain prerequisite semantics

5A domain-level edge'leri **authoring guidance** olarak tanımlar; runtime hard lock değildir.

```text
Domain relation kinds
- foundation_for
- usually_before
- can_parallelize_with
- supporting_for
- integrates_with
- advanced_convergence_into
```

Runtime canonical rule D-021/PRG-v0 ile değişmez:

```text
real prerequisite gate = Skill → Skill
```

Bir Domain henüz derived `mastered` değil diye onun içindeki her dependent olmayan Skill otomatik kilitlenmez.

---

# 7. High-level relation matrix

Aşağıdaki ilişkiler 5B/6 için graph-authoring input'udur:

- Python `foundation_for` automation, Triton, ML tooling, serving/infra tooling.
- C `foundation_for` C++, memory/OS reasoning, CUDA low-level work.
- Linux/Git/Shell `foundation_for` practical systems, build/debug, containers, serving, infrastructure.
- DS&A `supporting_for` C++ systems, storage, distributed problem solving, performance.
- Modern C++ `foundation_for` production systems, concurrency, performance, inference engine source-level work.
- Architecture `foundation_for` OS/performance/GPU architecture reasoning.
- OS/Memory `foundation_for` concurrency, systems networking, containers, profiling.
- Concurrency + Networking `foundation_for` Distributed Systems and serving systems.
- Distributed Systems `foundation_for` AI infrastructure and distributed serving.
- Storage/DB concepts `supporting_for` stateful services, queues, metadata/control-plane reasoning.
- Containers/Cloud/Observability `integrates_with` serving and AI infrastructure.
- Performance `integrates_with` every advanced systems/GPU/inference domain.
- GPU Architecture `foundation_for` CUDA/Triton/performance interpretation.
- CUDA `foundation_for` advanced GPU kernels and multi-GPU execution reasoning.
- Triton `integrates_with` inference kernel optimization.
- ML/Transformer `foundation_for` LLM inference semantics but not general systems foundations.
- LLM Inference `foundation_for` serving + KV/batching/scheduling/quantization.
- Serving Systems `integrates_with` distributed/cloud/observability/performance.
- KV/Batching/Scheduling/Quantization `supporting_for` inference optimization and capacity reasoning.
- Multi-GPU/NCCL/RDMA `advanced_convergence_into` AI/GPU Infrastructure.
- Professional Projects/OSS `integrates_with` all domains and final readiness.

---

# 8. Cross-cutting professional overlays

Aşağıdakiler ayrı generic career domainine dönüştürülmeden birden çok domain içine capability olarak dağıtılır:

- testing,
- debugging,
- build systems,
- Git/branch/PR/code review,
- documentation,
- design docs/RFC thinking,
- reproducible benchmarking,
- logging/metrics/tracing,
- reliability/SLO/incident/postmortem,
- security fundamentals,
- source-code reading,
- issue decomposition,
- technical communication.

AŞAMA 6F bu overlay'leri canonical Skill/Objective'lere bölecek ve hangi technical domainlerde tekrar kullanılacağını gösterecek.

---

# 9. Security kapsam sınırı

Cybersecurity ayrı ana career specialization olarak bu route'a eklenmez.

Ancak professional AI Infrastructure engineer için gerekli güvenlik capability'leri curriculum dışında bırakılamaz. Özellikle ilerleyen aşamalarda:
- least privilege / secrets/config handling,
- dependency/supply-chain awareness,
- network/service exposure basics,
- container/cloud security foundations,
- data/model artifact access basics,
- operational safety

gibi konular ilgili systems/cloud/infrastructure domainlerine cross-cutting Skill olarak yerleştirilir.

AŞAMA 6 exact kapsamı araştırma/QA ile belirler.

---

# 10. Matematik / numerical kapsam sınırı

Ayrı genel matematik derecesi-style curriculum hedeflenmez.

Gerekli matematik/numerical capability:
- DS&A complexity reasoning,
- architecture/performance ölçüm ve oranlar,
- ML/Transformer için gerekli linear algebra/tensor/numerical concepts,
- precision/quantization hata ve trade-off reasoning,
- performance/capacity statistics foundations

gibi ihtiyaç noktasında prerequisite Skill olarak haritalanır.

Amaç eksik matematiği gizli prerequisite yapmak değil; gerektiğinde açık, öğretilebilir ve ölçülebilir Skill'e dönüştürmektir.

---

# 11. Tool/vendor bağımlılığı sınırı

`vLLM`, `SGLang`, `TensorRT-LLM`, CUDA toolkit, Kubernetes veya belirli cloud vendor davranışı **domain'in kendisi değildir**.

Kural:

```text
stable systems concept
+ current implementation/tool examples
+ version/freshness metadata
```

şeklinde authoring yapılır.

Tool değiştiğinde bütün capability graph çökmemeli; yalnız implementation-specific nodes/version metadata güncellenebilmelidir.

---

# 12. Professional-readiness coverage bağlantısı

PDM-v0 final readiness'i tek domain completion yüzdesine dönüştürmez.

Professional target için minimum high-level evidence coverage şu katmanlarda bulunmalıdır:

```text
Foundations
→ Applied Systems Engineering
→ Distributed/Platform Engineering
→ Performance Engineering
→ GPU/Accelerator Engineering
→ LLM Inference Systems
→ Multi-GPU / AI Infrastructure
→ Integrated Professional Project / Capstone
```

Her katmandaki gerçek gate'ler AŞAMA 6 Skill/Objective map + GRE/RVR + professional evidence contracts üzerinden tanımlanır.

---

# 13. AŞAMA 5B handoff

5B graph/metadata sözleşmesi aşağıdaki 5A çıktısını formalize etmelidir:

- Domain entity + role/class metadata,
- Module/Topic placement,
- cross-domain Skill reuse,
- `foundation_for / usually_before / can_parallelize_with / supporting_for / integrates_with` authoring relations,
- runtime Skill→Skill hard/soft prerequisite separation,
- required/critical/optional capability metadata,
- evidence contract refs,
- retention/remediation/diagnostic flags,
- cross-cutting professional capability tags,
- technology/version/freshness metadata,
- project/capstone attribution,
- curriculum version/migration semantics.

5B bu document'taki ASCII route'u doğrudan hard-coded linear sequence'e dönüştürmemelidir.

---

# 14. AŞAMA 6 handoff

AŞAMA 6:
- bu 23 route family'nin her birini Module/Topic/Skill/Objective seviyesine parçalayacak,
- broad domain weakness yerine exact capability diagnosis üretecek,
- shared Skills'i duplicate etmeyecek,
- hidden prerequisite'leri açık edge'lere dönüştürecek,
- cross-domain professional overlays'i gerçek Skills olarak yerleştirecek,
- tool-specific current content ile stable systems concepts'i ayıracak,
- 6H'de bağımsız Research AI ile coverage/prerequisite/current-industry QA yapacak.

---

# 15. PDM-v0 invariants

1. Domain map takvim değildir.
2. Domain completion runtime hard lock değildir; Skill readiness esastır.
3. Technical English paraleldir.
4. Python ve C complementary foundations'tır.
5. Linux/Git/Shell erken ve cross-cutting çalışma ortamıdır.
6. Systems core güçlü olmadan AI Infrastructure'a doğrudan atlanmaz.
7. Performance yalnız sona bırakılmaz; route boyunca büyür.
8. ML/Transformer supporting domain'dir; main ML research specialization değildir.
9. GPU Architecture CUDA/Triton'dan önce conceptual foundation sağlar.
10. Triton CUDA/GPU fundamentals'i bypass etmez.
11. Serving framework adı stable concept'in yerine geçmez.
12. Multi-GPU layer networking/distributed/GPU foundations'i gerektirir.
13. Professional projects/OSS yalnız finalde başlamaz.
14. Final professional readiness tek capstone, tek exam veya domain yüzdesi değildir.
15. Security/reliability/observability professional overlays curriculum dışında bırakılamaz.
16. Gerekli math/numerical knowledge hidden prerequisite olarak bırakılmaz.
17. Domain/Module broad weakness canonical diagnosis atomu değildir.
18. AŞAMA 6 detailed map üretmeden 5A broad list “full curriculum” sayılmaz.

---

# 16. 5A acceptance criteria

5A PASS için:

1. 4+ year professional domain envelope kapsandı.
2. D-042 Python foundation korundu.
3. Technical English parallel semantics açık.
4. Systems → distributed → performance → GPU → inference → AI Infra ana omurga korunuyor.
5. DS&A/Architecture/OS/Memory/Concurrency/Networking sınırları açık.
6. Distributed + Storage/DB ve Containers/Cloud/Observability rolleri açık.
7. Performance cross-cutting semantics açık.
8. GPU/CUDA/Triton ayrımı açık.
9. ML/Transformer supporting-depth sınırı açık.
10. LLM inference / serving / optimization katmanları ayrılmış.
11. Multi-GPU/NCCL/RDMA advanced convergence konumu açık.
12. Open Source / engineering practice / projects-capstone layer açık.
13. Domain-level prerequisite vs runtime Skill prerequisite ayrımı korundu.
14. Security/reliability/math/tool-vendor boundaries açık.
15. AŞAMA 5B ve AŞAMA 6 için temiz handoff var.
16. Full curriculum/lesson content 5A'da yanlışlıkla üretilmedi.

---

# 17. Final 5A kararı

**Final model:** `PDM-v0 — Professional Domain Backbone`

Canonical high-level route:

```text
Technical English — parallel
→ Python
→ C
→ Linux + Git + Shell
→ Data Structures & Algorithms foundations
→ Modern C++
→ Computer Architecture
→ Operating Systems + Memory
→ Concurrency / Parallel Programming
→ Networking
→ Distributed Systems + Storage / Databases foundations
→ Containers / Cloud / Observability
→ Performance Engineering & Profiling
→ GPU Architecture
→ CUDA
→ Triton
→ ML + Transformer foundations
→ LLM Inference Internals
→ vLLM / SGLang / TensorRT-LLM-style serving systems
→ KV Cache / Batching / Scheduling / Quantization
→ Multi-GPU + NCCL + RDMA
→ AI Infrastructure / GPU Infrastructure
→ Open Source contributions + real large projects + professional capstones
```

Bu text order **roadmap summary**'dir; canonical planner davranışı linear değildir. Gerçek executable curriculum graph AŞAMA 5B/6'da Skill-level edges ile oluşturulur.