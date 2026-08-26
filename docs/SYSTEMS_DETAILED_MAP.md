# Systems Detailed Map — SDM-v0

**Adım:** 6D — Systems detailed map
**Durum:** TAMAMLANDI
**Tarih:** 2026-08-26
**Final model:** `SDM-v0 — Systems Detailed Map`
**Karar:** D-058

Bu belge D06–D13 Systems route family'lerinin FRDB-v0 uyumlu machine-readable authoring package'ını, 6C accepted registry ile cross-package reuse sonucunu ve 6D internal QA kararını özetler.

Canonical dataset:

`curriculum/decomposition/6d_systems/`

Bağlayıcı girdiler:
- `docs/FULL_ROUTE_DECOMPOSITION_BLUEPRINT.md` — FRDB-v0 / D-056,
- `docs/FOUNDATIONS_DETAILED_MAP.md` — FDM-v0 / D-057,
- `docs/GRANULARITY_NAMING_STANDARD.md` — GNS-v0 / D-054,
- `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md` — KGC-v0 / D-051,
- `docs/CURRICULUM_DOMAIN_MAP.md` — PDM-v0 / D-049,
- `docs/GRAPH_ARCHITECTURE_QA.md` — GQA-v0 / D-053,
- `docs/PREREQUISITE_POLICY_SPEC.md` — PRG-v0 / D-036,
- `docs/LEARNING_ENGINE_SPEC.md`.

Ana sonuç:

> **D06–D13 artık broad systems başlık listesi değildir. 192 canonical Skill ve 207 atomic Objective ile weakness, prerequisite, evidence ve remediation'ın alt capability seviyesinde çalışabileceği, 6C registry'sini clone'lamadan tüketen internally-QA-passed authoring map'tir. External coverage/current-industry validation 6H'ye kadar pending kalır.**

---

# 1. Package özeti

| Koleksiyon | Sonuç |
|---|---:|
| Domain | 8 |
| Module | 21 |
| Topic | 64 |
| Skill | 192 |
| Learning Objective | 207 |
| TopicSkillLink | 224 |
| Skill prerequisite edge | 313 (259 hard / 54 soft) |
| Cross-package prerequisite edge (6C→6D) | 56 |
| Clone'lanmadan reuse edilen 6C Skill | 43 |
| Scope-relative capability requirement | 254 |
| Professional attribution | 308 |
| Project attribution | 12 |
| Açık blocking review | 0 |
| Açık non-blocking review | 2 |

Route family dağılımı:

| Route | Domain | Module | Topic | Skill |
|---|---|---:|---:|---:|
| D06 | Modern C++ | 3 | 9 | 30 |
| D07 | Computer Architecture | 2 | 6 | 18 |
| D08 | Operating Systems + Memory | 3 | 8 | 22 |
| D09 | Concurrency / Parallel Programming | 2 | 7 | 20 |
| D10 | Networking | 3 | 9 | 25 |
| D11 | Distributed Systems + Storage/Databases | 3 | 10 | 29 |
| D12 | Containers / Cloud / Observability | 3 | 8 | 27 |
| D13 | Performance Engineering & Profiling | 2 | 7 | 21 |

Internal package sonucu:

`PASS_WITH_OPEN_NON_BLOCKING_REVIEWS`

Package learner-published değildir. `status=authoring_complete_internal_qa`; external coverage gate 6H'de, production resource authoring AŞAMA 15/20'dedir.

---

# 2. D06 — Modern C++

3 Module ve 9 Topic altında dil özelliği listesi yerine ayrı learner state gerektiren capability sınırlarına ayrıldı:

## 2.1 Language ve object model
- translation/compile/link/run modeli ve toolchain flag kontrolü,
- ODR-güvenli interface/implementation sınırı,
- value category davranışı,
- initialization semantics ve constructor/conversion seçimi,
- const ve compile-time qualifier semantiği,
- reference binding ve dangling reference riski,
- object lifetime/scope/destruction sırası,
- RAII resource ownership,
- unique/shared ownership modeli,
- move semantics ve moved-from state,
- special member function kuralları.

## 2.2 Abstraction ve generics
- class invariant ve encapsulation sınırı,
- virtual dispatch ve base/derived lifetime,
- inheritance ile composition arasındaki tasarım seçimi,
- function/class template üretimi,
- overload resolution ve constraint,
- template instantiation diagnostic okuma,
- container seçimi, iterator/algorithm ve invalidation,
- non-owning view lifetime güvenliği.

## 2.3 Engineering practice
- exception safety guarantee seviyeleri,
- error return ile exception arasındaki arayüz kararı,
- precondition/postcondition/invariant kontrolü,
- build system target/dependency/flag tanımı,
- third-party dependency entegrasyonu,
- build süresi ve include grafiği hijyeni,
- test harness, debugger ve sanitizer tabanlı UB lokalizasyonu.

Pointer/memory temel capability'leri C'den clone'lanmadı; `skill.c.pointer_dereference`, `skill.memory.address_value_distinction` ve `skill.memory.storage_lifetime_intuition` canonical kimliğiyle reuse edildi.

---

# 3. D07 — Computer Architecture

2 Module ve 6 Topic:

- instruction execution modeli,
- ISA / microarchitecture / compiler çıktısı sınırı,
- compiler-üretilmiş assembly okuma,
- integer/floating-point temsil ve precision sınırı,
- pipeline hazard, branch predictability ve ILP,
- SIMD data-parallel model ve vectorization engelleri,
- cache hierarchy, miss sınıflandırması, alignment/layout,
- temporal/spatial locality ve layout dönüşümü,
- false sharing,
- latency/throughput ayrımı, roofline bound sınıflandırması,
- hardware counter yorumlama.

Chip-design specialization'a genişletilmedi; architecture derinliği systems/GPU performance reasoning'i besleyecek sınırda tutuldu.

---

# 4. D08 — Operating Systems + Memory

3 Module ve 8 Topic:

- process/address space/isolation modeli,
- thread ile process ayrımı,
- process creation/exec/wait/exit lifecycle'ı,
- syscall sınırı, hata semantiği ve syscall tracing,
- scheduler davranışı, context switch maliyeti, CPU time accounting,
- virtual/physical translation, page fault davranışı, TLB ve page size etkisi,
- memory mapping kullanımı, allocator davranışı, memory kullanım tanısı,
- file descriptor modeli, blocking I/O ve partial I/O semantiği, buffering/flush sınırı,
- signal handling ve graceful shutdown,
- resource limit etkisi ve kernel event gözlemi.

OS teorisi Linux gözlemleriyle bağlandı; `skill.linux.*` capability'leri 6C'den reuse edildi.

---

# 5. D09 — Concurrency / Parallel Programming

2 Module ve 7 Topic; correctness ve performance ayrı capability sınırlarında tutuldu:

- thread lifecycle yönetimi,
- paylaşılan mutable state riski,
- task soyutlaması ile explicit thread yönetimi ayrımı,
- mutex kritik bölge sınırı ve condition variable koordinasyonu,
- lock granularity/contention dengesi,
- data race tanısı, deadlock analizi, nondeterministik hatanın tekrar üretilmesi,
- atomic işlemler, memory ordering, lock-free trade-off'u,
- work decomposition, thread pool kullanımı, deterministic sonuç birleştirme,
- async execution modeli, event loop starvation riski, cancellation/timeout semantiği,
- scaling limit reasoning ve speedup ölçümü.

API kullanımı tek başına capability sayılmadı; her Skill ya gözlemlenebilir correctness ya da ölçülebilir scaling davranışı taşır.

---

# 6. D10 — Networking

3 Module ve 9 Topic:

- layering/encapsulation, addressing/routing, packet capture okuma,
- TCP bağlantı lifecycle'ı, reliability/flow control, UDP trade-off'u,
- DNS resolution davranışı ve service discovery failure modları,
- HTTP request/response semantiği, connection performansı, API hata sözleşmesi,
- TLS handshake/trust modeli ve TLS hata tanısı,
- serialization format seçimi, schema evolution, RPC çağrı semantiği,
- socket client/server, socket hata ve partial I/O, message framing,
- non-blocking I/O, I/O multiplexing, connection/descriptor limitleri,
- latency/bandwidth bütçesi, timeout/retry politikası, katmanlı network arıza tanısı.

Network certification müfredatı hedeflenmedi; distributed ve AI infrastructure için gereken systems networking derinliği hedeflendi.

---

# 7. D11 — Distributed Systems + Storage / Databases

3 Module ve 10 Topic; distributed state ile storage engine reasoning ayrı fakat bağlı tutuldu:

## 7.1 Distributed core
- partial failure modeli,
- clock skew ve event ordering,
- failure detection belirsizliği,
- replication modelleri, partitioning stratejisi, rebalancing/hotspot,
- consistency modelleri, availability/consistency dengesi, client-visible anomaliler,
- quorum, consensus garantileri, leader election/failover ve split-brain riski.

## 7.2 Storage engine
- durability write path,
- write-ahead logging ve crash recovery,
- transaction atomicity, isolation anomalileri, concurrency control mekanizmaları,
- index yapısı trade-off'u, index seçimi, on-disk layout/compaction,
- sorgu maliyeti ve execution plan incelemesi.

## 7.3 Data movement
- queue/stream semantiği, backpressure, streaming ordering/windowing,
- teslim garantisi seviyeleri, idempotent işlem tasarımı, duplicate/out-of-order işleme.

Database administrator veya generic data-engineering specialization'ına genişletilmedi.

---

# 8. D12 — Containers / Cloud / Observability

3 Module ve 8 Topic:

- namespace izolasyon modeli, kaynak limiti kontrolü, container/VM sınırı,
- image layer/cache/reproducibility, image optimizasyonu, supply-chain güvenliği,
- container runtime lifecycle, ağ modeli, storage kalıcılığı,
- declarative workload modeli, placement kısıtları, rollout/health/recovery, workload arıza tanısı,
- configuration yönetimi, secret güvenliği, geri alınabilir deployment,
- cloud kaynak modeli, identity/least-privilege sınırı, infrastructure-as-code tekrar üretilebilirliği,
- structured logging, metric instrumentation, distributed tracing, sinyal korelasyonu,
- SLI/SLO tanımı, alarm sinyal kalitesi, incident response, blameless postmortem.

Tool/vendor adları stable systems concept'in yerine geçmedi. Fast-moving capability'ler `freshness_class=fast_moving` ve `technology_dependency_refs` ile işaretlendi; incident response ve postmortem gibi practice-shaped capability'ler evergreen bırakıldı. Toplam 13 fast-moving ve 21 version-sensitive Skill vardır.

---

# 9. D13 — Performance Engineering & Profiling

2 Module ve 7 Topic; ölçüm disiplini optimizasyondan önce gelir:

- ölçüm hedefi tanımı, ortam gürültüsü kontrolü, ölçüm geçerliliği,
- benchmark workload tasarımı, microbenchmark tuzakları, benchmark tekrar üretilebilirliği,
- varyans/dağılım reasoning, tail latency, regresyon tespiti,
- CPU profili toplama, profil yorumlama, sampling/instrumentation dengesi,
- memory profili, cache davranışı ölçümü, I/O-bound ile CPU-bound ayrımı,
- darboğaz lokalizasyonu, optimizasyon hipotezi doğrulama, uçtan uca latency atfı,
- kapasite/headroom, maliyet verimliliği, performans raporu iletişimi.

Performance sona bırakılmış tek optimization bölümü değildir: 6C'deki `skill.python.profiling_measurement` ve `skill.python.benchmark_automation` bu Skills'in soft prerequisite'i olarak reuse edilir.

---

# 10. Cross-package reuse sonucu

6D hiçbir 6C Skill'ini clone'lamadı.

- 43 accepted 6C Skill, ya cross-package prerequisite kaynağı ya da 6D Topic'lerinde `reinforce` TopicSkillLink olarak yeniden kullanıldı.
- 56 prerequisite edge cross-package'tır ve `cross_package_ref: true` taşır.
- Reuse kayıtları `seed_mappings.yaml` içinde `mapping_class: reused_from_prior_package` ve `disposition: ratify_as_is` ile explicit tutulur; `result_entity_refs` daima orijinal ID'dir.
- 6C ID'lerinin 6D'de yeni Skill/Objective olarak yeniden tanımlanmadığı hem generator hem bağımsız validator tarafından kontrol edilir.
- Prerequisite edge target'ı daima bir 6D Skill'idir; 6D geriye dönük olarak 6C graph'ını değiştirmez.

Duplicate resolver ayrıca şu yakın çiftleri ayrı canonical capability olarak gerekçelendirdi:

| 6C capability | 6D capability | Ayrım |
|---|---|---|
| `skill.python.thread_process_choice` | `skill.concurrency.task_vs_thread_abstraction` | Python runtime seçimi ile dil-bağımsız execution-model tasarım kararı ayrı evidence ister. |
| `skill.python.profiling_measurement` | `skill.performance.cpu_profile_collection` | Python profiler kullanımı ile sistem seviyesinde temsil edici profil toplama ayrı remediation ister. |
| `skill.python.network_client_basic` | `skill.network.http_request_response_model` | Client kütüphanesi kullanımı protokol semantiğinin yerine geçmez. |
| `skill.c.dynamic_allocation_lifecycle` | `skill.os.allocator_behavior_model` | Allocation lifecycle üretimi ile allocator/fragmentation reasoning ayrı state'tir. |

---

# 11. Prerequisite ve branch-isolation sonucu

313 edge için:
- bütün source/target refs mevcut (6C ∪ 6D),
- self edge yok,
- aynı pair'de hard/soft conflict yok,
- duplicate edge yok,
- 6C + 6D birleşik hard graph DAG (324/324 node),
- edge direction source→target,
- Domain/Module/Topic completion gate yok,
- English→unrelated technical hard gate yok,
- soft gap hard block üretmiyor,
- hard prerequisite'i olmayan giriş Skill'leri mevcut; branch zero-eligibility riski yok.

FRDB-v0 §19 Pass B testi uygulandı: yalnız scaffold sağlayan 52 declared source hard'dan soft'a indirildi ve her biri `hard_soft_test_result: downgraded_to_soft_in_pass_b` ile kayıtlıdır. Task/resource'a özgü gereksinimler graph'a şişirilmedi; QAB/Task `required_skill_ids[]` metadata'sına bırakıldı.

---

# 12. Evidence, remediation ve professional attribution

Her Skill:
- observable direct evidence path,
- evidence depth beklentisi (`independent_application`, `debugging`, `transfer`, `performance_measurement`, `production_context`, `delayed_retention`),
- retention profile,
- diagnostic eligibility,
- hedefli remediation tag'leri,
- shared-vs-specific gerekçesi,
- duplicate resolver sonucu,
- provenance/freshness ve gerektiğinde technology dependency,
- scope-relative requirement

taşır.

146 Skill shared stable systems capability, 46 Skill language/tool-specific capability olarak sınıflandırıldı. 14 Skill `critical_prerequisite_candidate` işaretlidir; bunlar downstream branch'lerin adil yorumlanabilirliği için taşıyıcı capability'lerdir.

Her Objective exactly one Skill'e bağlıdır, GRE-v0 defaults'ını değiştirmez, H0 independent evidence ve verified evaluator beklentisini korur. 15 Skill için ikinci bir Objective yazıldı; bunlar aynı capability'nin gerçekten ayrı gözlemlenebilir kanıtıdır (ör. lokalize et + düzelt + doğrula), Skill split gerekçesi değildir.

`project.systems.observable_networked_service` candidate attribution'ı yalnız structurally essential ve separately observable 12 component için yazıldı. Tek project PASS toplu evidence üretmez.

---

# 13. Açık review'lar

Blocking review yoktur.

Non-blocking:
1. `review.6d.external_coverage` — D06–D13 external coverage/current relevance/hidden-prerequisite doğrulaması → 6H.
2. `review.6d.platform_tool_freshness` — fast-moving container/orchestration/cloud capability'leri için review trigger policy versiyonlaması → 6H.
3. `review.6d.professional_overlay_reconciliation` — observability/reliability/performance-report capability'lerinin 6F professional registry ile reconcile edilmesi → 6F.
4. `review.6d.accelerator_forward_reuse` — 6E'nin hangi Systems Skills'i clone'lamadan reuse edeceği → 6E.

Bu review'lar 6D internal authoring completion'ını engellemez; package'ı externally validated veya learner-published yapmaz.

---

# 14. Deterministik authoring/QA

`tools/generate_systems_package.py` canonical contract girdilerinden ve 6C accepted registry'sinden package'ı deterministik üretir; fail-fast 22 kontrol uygular.

`tools/validate_systems_package.py` üretilen YAML'ı generator'a güvenmeden bağımsız doğrular:

- logical collection bütünlüğü ve route partition,
- unique/stable logical ID ve 6C ID collision guard,
- organization parent zinciri,
- Objective exactly-one-Skill, owner namespace tutarlılığı, GRE-v0 evidence profili,
- her Skill için tam bir primary teaching context,
- production Skill'in yalnız recognition ile kanıtlanamaması,
- TopicSkillLink controlled vocabulary ve dangling ref kontrolü,
- prerequisite ref/self/duplicate/conflict, controlled reason kind, cross-package etiket doğruluğu,
- birleşik 6C+6D hard graph DAG,
- English global-gate guard,
- entry-Skill reachability,
- reuse'un seed_mappings'te eksiksiz beyanı,
- source catalog çözünürlüğü ve manifest tutarlılığı,
- blocking review ve 6H external QA guard'ı.

Her iki araç da `PASS` verir. Bunlar production runtime kodu veya physical DB schema değildir; authoring package bakım/validation araçlarıdır.

---

# 15. 6D'nin sınırı

6D kesinleştirmez:
- GPU/CUDA/Triton/ML/inference decomposition'ı → 6E,
- D23 open-source/large-project/capstone decomposition'ı ve professional overlay reconciliation'ı → 6F,
- learner weakness/remediation runtime mapping'i → 6G,
- external coverage/current-industry/source-quality doğrulamasını → 6H,
- lesson/task/resource body'lerini → AŞAMA 15/20,
- physical DB schema'sını → 9C,
- GRE/RVR/PRG/PBR davranışlarını → mevcut canonical specs.

6D ayrı external Research AI kullanmadı; accepted contract'lar ve 6C registry üzerinde internal decomposition authoring'i yapıldı. Bağımsız full coverage/current-industry/prerequisite Research QA 6H'de zorunlu kalır.

---

# 16. 6E handoff

6E GPU / ML / Inference detailed map:
- 6C ve 6D `skills.yaml` registry'lerini birlikte duplicate resolver input'u yapar,
6E GIM-v0 / D-059 bu forward handoff'u tamamladı:
- Systems/performance/concurrency/networking Skills clone'lanmadan canonical ID ile reuse edildi,
- stable GPU/inference concepts ile tool/version-specific capabilities ayrıldı,
- math/numerical hidden prerequisites explicit Skills/edges olarak yakalandı,
- `review.6d.accelerator_forward_reuse` resolved edildi.

**Güncel sonraki numaralı adım:** `6F — Professional engineering / project map`; `review.6d.professional_overlay_reconciliation` 6F'ye açık kalır.


## 6F reconciliation sonucu

`review.6d.professional_overlay_reconciliation` PEM-v0 / D-060 ile resolved edildi. D23 registry observability, reliability, performance, build ve testing capability'lerini canonical 6D Skill ID'leriyle reuse eder; 6D Skill/Objective/prerequisite semantics değiştirilmedi. 6D'de 2 external/freshness non-blocking review 6H'ye açık kalır.
