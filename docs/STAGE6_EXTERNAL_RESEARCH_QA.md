# Stage 6 External Research QA — S6ERQA-v0

**Durum:** FINAL / EXTERNALLY RESEARCH-VALIDATED  
**Adım:** 6H  
**Decision:** D-062  
**Tarih:** 2026-08-27

## 1. Amaç ve kabul kararı

6H, Stage 6 granular capability map'in D01–D23 professional-readiness coverage'ını, hidden prerequisite'lerini, duplicate/granularity risklerini, fast-moving tool/runtime freshness'ini, D23 professional evidence yapısını ve WLRM safety davranışını bağımsız dış Research AI'larla doğruladı.

Üç bağımsız evaluator da başlangıç Stage 6 snapshot'ı için **PASS WITH REQUIRED CHANGES** verdi. Manager bu önerileri canonical GNS-v0/KGC-v0/PRG-v0 kurallarıyla reconcile etti; tool/vendor/model ayrıntıları otomatik stable Skill yapılmadı. Kabul edilen corrective patch sonrasında final 6H validator sonucu:

> **PASS — S6ERQA-v0 / D-062**

Stage 6 external Research QA kapanmıştır.

## 2. External evaluator provenance

Canonical reconciliation: `research/6h_external_research_ai_report.md`.

External report digests:
- ChatGPT evaluator: `52d50c17b71df63d16560cf81bd47aac108fa0f1b9776eea1fd7c1787a97eeb4`
- Perplexity evaluator: `86ebef2f6dabfcf10d302e72a5329777b9b3e02e92bf77310e58aaa7cb026568`
- Gemini evaluator: `829b9bcc4dba5f845704193c3ebfbb5b9a4ba26dd3da4130b9ba2eb42abd3bec`

Her üç evaluator da `PASS WITH REQUIRED CHANGES` verdi. Gemini raporu current-industry trend corroboration için kullanıldı; canonical D01–D23 numbering'i değiştirdiği ve bazı model/vendor özelliklerini gereğinden fazla stable Skill'e yükselttiği için exact mapping önerileri otomatik kabul edilmedi.

## 3. Final Stage 6 graph

External-QA corrective patch sonrası:

- **23/23 route family**
- **549 canonical Skill**
- **608 Learning Objective**
- **950 prerequisite edge**
- combined hard graph: **549/549 DAG PASS**
- WLRM coverage: **549/549 Skill + 608/608 Objective**
- 6H-owned reviews: **10/10 resolved**
- global Skill ID uniqueness: PASS
- global Objective ID uniqueness: PASS
- dangling prerequisite/objective owner: 0
- hard/soft same-pair conflict: 0
- self prerequisite: 0

Pre-6H accepted source-package regressions ayrıca kendi historical contract'larıyla PASS kaldı: 6C, 6D, 6E, 6F ve 6G.

## 4. 6H ile eklenen stable capability'ler

GNS-v0 independent evidence/remediation testini geçen **6** capability eklendi:

1. `skill.os.numa_locality_affinity`
   - local/remote NUMA memory cost,
   - CPU/thread/device affinity,
   - topology/measurement tabanlı placement.

2. `skill.cuda.async_data_movement_pipeline`
   - in-kernel staged asynchronous data movement,
   - producer/consumer synchronization,
   - copy-compute overlap + correctness/performance validation.

3. `skill.optimization.speculative_decoding_tradeoff`
   - proposal/draft → target verification,
   - acceptance behavior,
   - compute/memory/latency/throughput trade-off.

4. `skill.serving.prefill_decode_disaggregation`
   - ayrı prefill/decode resource pools,
   - KV-state handoff,
   - routing/transfer cost/SLO/failure semantics.

5. `skill.ml.moe_routing_dataflow`
   - router/top-k expert selection,
   - token dispatch/expert compute/combine dataflow.

6. `skill.multi_gpu.expert_parallel_sharding`
   - expert/token placement,
   - collective/data movement,
   - topology/load imbalance/failure trade-offs.

## 5. Hidden prerequisite patch

External QA ile özellikle şu dependency sınırları explicit hale getirildi:

- cache hierarchy + virtual/physical translation → NUMA locality/affinity,
- NUMA locality/affinity → multi-GPU topology discovery,
- orchestrator scheduling/placement constraints → AI-infra topology-aware placement,
- shared-memory tiling + memory visibility + copy/compute model → CUDA async pipeline,
- autoregressive inference → speculative decoding,
- prefill/decode distinction + KV semantics + worker topology + network budget → prefill/decode disaggregation,
- transformer block dataflow → MoE routing → expert-parallel sharding,
- collective semantics → expert-parallel sharding.

PRG-v0 korundu: broad Domain gate açılmadı; yalnız target evidence'i gerçekten interpretable yapan narrow hard/soft edges eklendi.

## 6. Freshness policy — stable Skill şişirmeme

External raporlarda geçen current teknik ayrıntılar iki gruba ayrıldı.

### Stable capability olmadığı için Objective/example/freshness metadata olarak tutulanlar

- Python free-threaded runtime assumptions,
- Kubernetes DRA / ResourceClaim / DeviceClass / topology-aware/all-or-nothing scheduling,
- CUDA thread-block clusters / DSM / TMA / cp.async-benzeri mekanizmalar / Graphs / memory pools,
- Triton persistent scheduling / asynchronous pipeline / TMA / warp specialization,
- KV offload/tiering, chunked prefill, low-precision FP8/FP4-class format families,
- NCCL/RDMA buffer registration, dma-buf/zero-copy, NVLS/current topology behavior,
- OpenTelemetry Profiles / GenAI convention names,
- SBOM vs build provenance vs signed/verified attestation,
- repository-specific live OSS contribution policy.

### Otomatik stable Skill yapılmayan model/vendor özel örnekler

- MLA,
- MTP,
- DeepSeekMoE / DualPipe,
- FlashInfer,
- vLLM V1 internal APIs,
- NVFP4 / MXFP4 product-format details,
- CUTLASS/CuTe API/layout-specific details.

Bunlar professional context/example olarak kullanılabilir; ancak ayrı canonical learner state için GNS-v0 independence testi gerekir.

## 7. Math / numerical kararı

External QA sonrası **yeni broad math/calculus Domain veya hard route eklenmedi**.

Mevcut tensor-shape, matrix multiplication, probability/normalization, softmax stability, floating-point error, mixed-precision ve quantization reasoning hedef için yeterli bulundu. Quantization/numerical evidence narrow prerequisite ve Objective seviyesinde tutulur; generic advanced-math gate oluşturulmaz.

## 8. D23 professional evidence kararı

D23 broad yapısı korundu. External QA şu non-compensatory guard'ları formalize etti:

- global capstone/project PASS component Skill mastery vermez,
- structurally essential component evidence ayrı attributable kalır,
- final professional readiness en az üç farklı evidence family ister: systems/service + GPU/inference + production/professional,
- en az bir operations/failure-injection/incident-reconstruction evidence artifact gerekir.

External evaluator'ların duplicate adayı olarak işaretlediği OSS patch/PR scope ve submission/evidence Skills GNS-v0 ile tekrar karşılaştırıldı; lifecycle/evidence/remediation sınırları farklı olduğu için merge edilmedi.

## 9. WLRM external behavior kararı

WLRM-v0'nun ana safety davranışları external QA ile doğrulandı:

- invalid/prerequisite-contaminated attempt target weakness üretmez,
- assisted/provisional evidence doğrudan confirmed weakness/mastery oluşturmaz,
- ilk post-mastery contradiction → verification-first,
- broad Topic/Domain reset yok,
- project PASS evidence broadcast yok,
- remediation closure fresh H0 + direct + verified evidence ister.

6H ile ayrıca:
- expertise-aware **guidance fading**,
- previously-mastered target için reverification/retrieval/transfer/debug-before-reteach,
- AI scaffold'ın independent closure evidence yerine geçememesi,
- executable external-behavior QA scenarios
formalize edildi.

BKT zorunlu learner-state modeli yapılmadı; future calibration hypothesis olarak değerlendirilebilir fakat Stage 6 safety contract'ının yerine geçmez.

## 10. 10 review'ın final disposition'ı

6H şu review setini kapattı:

- `review.6c.external_coverage`
- `review.6d.external_coverage`
- `review.6d.platform_tool_freshness`
- `review.6e.external_coverage`
- `review.6e.tool_runtime_freshness`
- `review.6e.math_numerical_coverage`
- `review.6f.external_coverage`
- `review.6f.oss_workflow_freshness`
- `review.6f.capstone_diversity`
- `review.6g.external_behavior_coverage`

Tümünün resolution provenance'ı external reconciliation raporuna ve D-062'ye bağlanmıştır.

## 11. QA zinciri

6H final acceptance zinciri:

1. 6C generator + historical independent validator PASS
2. 6D generator + historical independent validator PASS
3. 6E generator + historical independent validator PASS
4. 6F generator + historical independent validator PASS
5. external corrective patch apply
6. 6G patched registry üzerinde regenerate
7. 6G historical independent validator PASS
8. 6H behavior/review patch apply
9. combined structural graph PASS — 549/549 DAG
10. `tools/validate_6h_external_reconciliation.py` PASS
11. 549/608 registry/WLRM exact-coverage assertion PASS
12. 10/10 6H review closure assertion PASS

Durable implementation:
- `tools/apply_6h_external_qa_patch.py`
- `tools/validate_stage6_structural_qa.py`
- `tools/validate_6h_external_reconciliation.py`
- `research/6h_external_research_ai_report.md`
- `curriculum/decomposition/6h_research_qa/`

## 12. Stage handoff

**Stage 6 — Granular Capability Map tamamlanmıştır.**

Sonraki canonical numbered step:

> **7A — İngilizce başlangıç ölçümü**

7A yine fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.
