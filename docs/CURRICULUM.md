# Curriculum Backbone — Planning Draft

**Durum:** BACKBONE / DETAYLI MAP HENÜZ ÜRETİLMEDİ  
**Canonical kararlar:** D-041, D-042, D-044  
**Detaylı decomposition:** AŞAMA 6 / `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`

Bu belge müfredatın yüksek seviyeli omurgasını gösterir. **Gerçek canonical Module/Topic/Skill/Learning Objective dataset'i değildir.**

D-044 sonrası bu dosyadaki broad alanlar mastery atomu veya tam curriculum kabul edilmez. Ayrıntılı curriculum haritası AŞAMA 5 graph/schema backbone üzerine AŞAMA 6'da üretilecektir.

## Güncel Ana Kariyer Rotası

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

Bu sıra tek bir katı linear calendar değildir. AŞAMA 6 gerçek prerequisites, cross-domain shared Skills ve branchable dependencies'i haritalayacaktır.

---

# D-044 Granularity Kuralı

Canonical hierarchy:

`Domain → Module → Topic → Skill → Learning Objective`

Örnek:

```text
Domain: Python
└── Module: Control Flow
    ├── Topic: Conditional Logic
    │   ├── Skill: boolean condition evaluation
    │   └── Skill: if/elif/else branching
    └── Topic: Loops
        ├── Skill: for iteration
        ├── Skill: while termination
        ├── Skill: break/continue control
        └── Skill: loop bug diagnosis
```

Uygulama `Python zayıf` sonucuyla yetinmemeli; hangi Skill/Objective'in zayıf olduğunu ayırt edebilmelidir.

---

# Backbone Family Notes

Aşağıdaki notlar yalnız AŞAMA 6 araştırması için başlangıç family'leridir; final topic/skill listesi değildir.

## Technical English
- foundational grammar/function words
- technical vocabulary
- compiler/terminal English
- docs/README/man pages
- GitHub issues/PRs
- design docs/RFCs
- papers/documentation
- technical interviews/team communication

## Python
- values/types/variables
- operators/expressions
- I/O
- conditionals
- loops
- strings/collections
- functions/scope
- modules/imports
- files/paths
- exceptions/debugging
- iteration/comprehensions
- typing/testing
- environments/dependencies/packaging
- CLI/automation/subprocess
- networking
- async/concurrency
- multiprocessing
- profiling
- NumPy/tensor/PyTorch-facing Python
- benchmark/infra scripting

## C
- syntax/types/control flow/functions
- compilation model
- pointers/addresses
- arrays/strings
- stack/heap/lifetime
- dynamic allocation
- structs/enums
- headers/translation units
- file I/O
- debugging/sanitizers
- build tooling

## Linux + Git + Shell
- filesystem/permissions
- shell/navigation/redirection/pipes
- processes/signals/environment
- package/build tools
- Git commits/branches/merge/rebase basics
- debugger/profiler tooling
- procfs/syscalls foundations
- scripting/automation

## DS&A Foundations
- complexity reasoning
- arrays/lists/stacks/queues
- hash tables
- trees/heaps/graphs
- sorting/searching
- memory/cache locality implications
- problem decomposition

## Modern C++
- references/value categories
- RAII/ownership
- classes/lifetime
- move semantics
- smart pointers
- STL/iterators
- templates/concepts where relevant
- errors/exceptions
- build/test/benchmark/tooling

## Computer Architecture
- ISA/execution
- pipeline
- cache hierarchy
- memory hierarchy
- branch prediction foundations
- SIMD/vectorization
- latency/throughput

## OS + Memory
- process/thread
- virtual memory/pages/TLB
- syscalls/file descriptors
- memory mapping
- scheduling
- I/O
- allocators
- synchronization foundations

## Concurrency / Parallel Programming
- threads/tasks
- mutex/condition variable
- atomics
- race/deadlock
- memory ordering
- producer/consumer
- thread pools
- parallel decomposition

## Networking
- TCP/IP
- DNS/HTTP/TLS foundations
- sockets
- blocking/non-blocking I/O
- epoll/io_uring foundations where relevant
- serialization/RPC
- latency/bandwidth

## Distributed Systems + Storage/Databases
- replication
- partitioning/sharding
- consistency
- consensus/leader election
- failures/timeouts/retries/idempotency
- transactions/WAL/recovery
- indexes/storage engines foundations
- queues/streaming

## Containers / Cloud / Observability
- processes/namespaces/cgroups
- containers/images
- Kubernetes foundations
- deployment/configuration
- logs/metrics/traces
- SLO/reliability foundations
- cloud compute/network/storage concepts

## Performance Engineering & Profiling
- measurement methodology
- latency/throughput/tail latency
- CPU/memory profiling
- flame graphs/perf/eBPF foundations
- benchmark design
- bottleneck attribution
- capacity/cost trade-offs

## GPU Architecture
- CPU vs GPU
- SIMT/warps/SMs
- memory hierarchy
- divergence/coalescing
- occupancy
- tensor cores foundations
- bandwidth/compute limits

## CUDA
- execution model
- grids/blocks/threads
- memory spaces/transfers
- synchronization
- shared memory
- streams
- correctness/debugging
- Nsight/profiling
- optimization patterns

## Triton
- execution/programming model
- blocked programming
- memory access
- kernel authoring
- benchmarking
- PyTorch integration
- fusion/attention-relevant kernels

## ML + Transformer Foundations
- tensors
- linear algebra needed for inference
- neural-network basics
- transformer/attention
- training vs inference
- precision formats
- model architecture needed for systems reasoning

## LLM Inference Internals
- prefill/decode
- tokenization foundations
- memory/compute bottlenecks
- KV cache
- attention execution
- latency/throughput trade-offs

## Serving Engines
- request lifecycle
- continuous batching
- PagedAttention-style memory management
- scheduling
- model loading/runtime
- vLLM/SGLang/TensorRT-LLM architecture reading
- failure/reliability/observability

## KV Cache / Batching / Scheduling / Quantization
- KV layout/growth/reuse
- batching policies
- scheduling/fairness
- memory pressure
- FP8/INT8/INT4 foundations
- accuracy/performance trade-offs

## Multi-GPU + NCCL + RDMA
- collective communication
- AllReduce/AllGather/etc.
- topology
- NCCL
- RDMA/RoCE/InfiniBand concepts
- tensor/pipeline/data/expert parallel concepts
- distributed profiling/failure handling

## AI Infrastructure / GPU Infrastructure
- GPU scheduling
- cluster orchestration
- serving architecture
- autoscaling/load shedding
- observability/reliability
- capacity planning
- cost/performance
- deployment pipelines
- production incidents/postmortems

## Open Source + Large Projects + Capstones
- repository/source-tree reading
- issue reproduction
- test/benchmark contributions
- PR/code review workflow
- technical writing/design docs
- integrated systems projects
- reproducible benchmarks
- professional capstone evidence

---

# Müfredat Tasarım Kuralı

AŞAMA 6'da gerçek nodes en az şu bağlamı taşıyacak şekilde planlanır:
- stable canonical ID
- parent/placement
- prerequisites
- required/criticality
- Learning Objectives
- evidence/assessment requirements
- retention relevance
- remediation/diagnostic mapping
- professional capability tags
- cross-domain reuse
- project/capstone attribution
- version/source/freshness

Müfredat mobil uygulama koduna gömülü dev sabit liste olmamalı; ayrı, versionlanabilir veri katmanı olarak yönetilmelidir.

> **Bu dosya high-level backbone'dur. Ayrıntılı final capability taxonomy AŞAMA 6 tamamlanmadan burada “bitmiş curriculum” olarak kabul edilmez.**
