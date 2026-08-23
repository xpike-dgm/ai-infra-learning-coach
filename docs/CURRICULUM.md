# Curriculum Backbone

Bu belge müfredatın **konu sırasını** tanımlar. Süreler sabit değildir; uygulama mastery ve günlük kapasiteye göre ilerlemeyi adapte eder.

## Ana Kariyer Rotası

1. Computer Fundamentals
2. C
3. Linux
4. Data Structures & Algorithms
5. Modern C++
6. Operating Systems & Memory
7. Concurrency
8. Networking
9. Distributed Systems
10. GPU Architecture
11. CUDA
12. Triton
13. ML/LLM Systems Fundamentals
14. Inference Engines
15. Multi-GPU / Distributed AI
16. AI Infrastructure
17. Open Source & Interview Readiness

## Track 0 — Computer Fundamentals

- CPU nedir?
- RAM nedir?
- disk/storage nedir?
- process/program ayrımı
- binary / bits / bytes
- compilation temel mantığı
- source code → compiler → executable
- terminal ve filesystem temel kavramları

## Track 1 — C

### Foundations
- variables
- primitive types
- operators
- conditions
- loops
- functions

### Memory Foundations
- addresses
- stack
- heap
- pointer basics
- dereference
- arrays and pointers
- pointer arithmetic
- strings
- dynamic allocation
- malloc/calloc/realloc/free
- lifetime
- memory leaks
- dangling pointers
- buffer overflow kavramı

### Structured Programming
- structs
- enums
- headers
- compilation units
- Make/CMake temel
- file I/O

### İlk Sistem Projeleri
- küçük CLI araçları
- dynamic array
- hash map
- basit memory allocator (ilerleyen aşamada)

## Track 2 — Linux

- shell kullanımı
- filesystem
- permissions
- processes
- signals
- pipes
- redirection
- environment variables
- package/build tools
- gcc/clang
- gdb
- valgrind/sanitizers
- procfs temel
- system calls giriş

### Projeler
- mini shell
- process monitor
- file utility

## Track 3 — Data Structures & Algorithms

- arrays
- linked lists
- stacks/queues
- hash tables
- trees
- heaps
- graphs
- sorting/searching
- Big-O
- time vs space trade-offs
- cache locality giriş

LeetCode/algoritma çalışması amaç değil, sistem mülakatları ve problem çözme için araçtır.

## Track 4 — Modern C++

- references
- classes
- RAII
- constructors/destructors
- move semantics
- smart pointers
- STL
- templates
- concepts temel
- error handling
- build systems
- testing
- benchmarking

## Track 5 — Operating Systems & Memory

- process/thread
- virtual memory
- pages/page tables
- TLB
- context switch
- syscalls
- file descriptors
- memory mapping
- cache hierarchy
- cache coherence giriş
- alignment/padding
- allocators

## Track 6 — Concurrency

- threads
- mutex
- condition variables
- atomics
- race conditions
- deadlocks
- memory ordering
- lock-free basics
- producer/consumer
- thread pools

### Projeler
- thread pool
- bounded queue
- SPSC/MPMC queue
- concurrent server component

## Track 7 — Networking

- TCP/IP
- sockets
- DNS temel
- HTTP temel
- blocking/non-blocking I/O
- select/poll/epoll
- io_uring giriş
- serialization
- protobuf
- RPC/gRPC
- latency/bandwidth

### Projeler
- TCP server
- async HTTP/RPC server

## Track 8 — Distributed Systems

- replication
- partitioning/sharding
- consistency
- CAP
- consensus
- Raft
- leader election
- logs
- failure detection
- retries/idempotency
- distributed tracing giriş

### Proje
- Raft tabanlı küçük KV store

## Track 9 — GPU Architecture

Prerequisite: C/C++, memory, concurrency, computer architecture temeli.

- CPU vs GPU
- SIMT
- SM
- warp
- occupancy
- memory hierarchy
- global/shared/register memory
- memory bandwidth
- divergence
- coalescing
- tensor cores giriş

## Track 10 — CUDA

- kernel launch
- grid/block/thread
- memory transfers
- shared memory
- synchronization
- streams
- profiling
- Nsight
- roofline düşüncesi
- optimization patterns

### Projeler
- vector add
- transpose
- convolution
- GEMM
- softmax

## Track 11 — Triton

- Triton execution model
- blocked programming
- memory access
- kernel authoring
- benchmarking
- PyTorch integration

### Projeler
- fused kernels
- optimized softmax
- attention bileşenleri

## Track 12 — ML/LLM Systems Fundamentals

Ana hedef model araştırmacısı olmak değildir; optimize edilen iş yükünü anlamaktır.

- tensor
- neural network temel
- transformer
- attention
- training vs inference
- precision formats
- quantization
- batching
- KV cache
- tokenization temel

## Track 13 — Inference Engines

- inference bottlenecks
- continuous batching
- KV cache management
- PagedAttention
- quantization
- throughput vs latency
- model serving
- vLLM
- SGLang
- llama.cpp mimari inceleme

### Proje
- mini inference server veya mevcut açık kaynak motoruna anlamlı katkı

## Track 14 — Multi-GPU / Distributed AI

- data parallelism
- tensor parallelism
- pipeline parallelism
- collective communication
- AllReduce
- NCCL
- RDMA
- RoCE / InfiniBand kavramsal giriş
- distributed profiling
- failure handling

## Track 15 — AI Infrastructure

- GPU scheduling
- cluster orchestration
- Kubernetes temel/orta seviye
- observability
- model serving architecture
- autoscaling
- cost/performance
- reliability
- capacity planning
- deployment pipelines

## Track 16 — Open Source & Career

- Git/GitHub professional workflow
- issues
- PR review
- contribution etiquette
- benchmark report
- technical writing
- architecture RFC
- systems interview prep
- C++ interview prep
- CUDA interview prep
- distributed systems design

## Portföy Hedefleri

Zaman içinde aşağıdaki sınıflarda kanıt üretilecek:

1. C/Linux sistemi projesi
2. C++ concurrency/performance projesi
3. distributed systems projesi
4. CUDA/Triton benchmark projesi
5. gerçek open-source merged PR

## Müfredat Tasarım Kuralı

Her topic nesnesi en az şu bilgileri taşımalıdır:

- id
- domain
- title
- prerequisites
- learning objectives
- estimated effort
- assessment types
- mastery threshold
- remediation strategy
- retention priority
- resource references

Müfredat mobil uygulama koduna gömülü dev bir sabit liste olmamalı; ayrı veri katmanı olarak yönetilmelidir.
