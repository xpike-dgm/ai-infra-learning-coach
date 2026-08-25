# HANDOFF STATE — Güncel Proje Durumu

**Son güncelleme:** 2026-08-25  
Repo: `xpike-dgm/ai-infra-learning-coach`

## 0. Zorunlu protokol — D-024 / D-027 / D-050
Bağlayıcı: `docs/PROJECT_MEMORY_PROTOCOL.md`.

> Her numaralı adım başlamadan PRE-STEP GitHub refresh; bittikten sonra living-memory ALWAYS-CHECK seti + ana çıktı + repo-wide stale-reference scan zorunludur.

D-050 sonrası özellikle `PROJECT_CONTEXT.md`, `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG` ve `DECISIONS` her numaralı step sonunda kontrol edilir. Stable specs active-step state'i kopyalamaz.

## 1. Ürün ve uzun vadeli hedef
Tek kullanıcı için, sıfırdan başlayan kullanıcıyı AI Infrastructure / Systems Engineering yolunda günlük yöneten, uygulama içinde öğreten/uygulatan ve yalnız kanıtlanmış öğrenmeyi ilerleme sayan adaptif Android öğrenme koçu.

D-041: full curriculum 4+ yıl veya daha uzun sürebilir; takvim readiness gate değildir. Final hedef verified engineering capability + retention + debugging + transfer + performance + integrated project/capstone evidence'dır.

Canonical target: `docs/PROFESSIONAL_READINESS_TARGET.md`.

## 2. Güncel ana rota

**Technical English (parallel) → Python → C → Linux + Git + Shell → DS&A foundations → Modern C++ → Computer Architecture → OS + Memory → Concurrency / Parallel Programming → Networking → Distributed Systems + Storage/Databases → Containers / Cloud / Observability → Performance Engineering / Profiling → GPU Architecture → CUDA → Triton → ML + Transformer foundations → LLM Inference Internals → vLLM/SGLang/TensorRT-LLM-style systems → KV Cache / Batching / Scheduling / Quantization → Multi-GPU + NCCL + RDMA → AI Infrastructure / GPU Infrastructure → Open Source + large projects + capstones**

Bu sıra roadmap summary'dir; runtime linear takvim değildir.

## 3. Bağlayıcı güncel kararlar

- **D-042:** Python resmi common foundation; C/C++ yerine geçmez.
- **D-043:** standalone specialization-stage yorumu geri çekildi; canonical değil.
- **D-044:** AŞAMA 6 full route'u `Domain → Module → Topic → Skill → Learning Objective` seviyesine bölecek; weakness/remediation Skill/Objective seviyesinde lokalize edilir.
- **D-045:** WBA-v0 Weekly Blueprint Assessment.
- **D-046:** MCA-v0 Monthly Capability Assessment.
- **D-047:** QAB-v0 Trusted Assessment Resource Bank.
- **D-048:** AIV-v0 AI Assessment Resource Validation.
- **D-049:** PDM-v0 Professional Domain Backbone.
- **D-050:** living-memory sync + repo-wide stale-reference audit zorunlu; exact file-role matrix `PROJECT_MEMORY_PROTOCOL.md` içinde.
- **D-051:** KGC-v0 Versioned Curriculum Knowledge Graph Contract; 5B tamamlandı.
- **D-052:** FBB-v0 V1 Foundation Backbone; 5C tamamlandı.
- **D-053:** GQA-v0 Foundation Graph Architecture QA; 5D corrective patch sonrası PASS.

## 4. D-049 / 5A final özeti

Canonical: `docs/CURRICULUM_DOMAIN_MAP.md`.

PDM-v0:
- 23 ana route family high-level envelope olarak korunur.
- Domainler takvim veya mastery atomu değildir.
- Technical English bütün rota boyunca parallel track'tir.
- Python + C + Linux/Git/Shell early complementary foundations'tır; katı seri değildir.
- DS&A common supporting foundation'dır.
- Systems core: Modern C++, Computer Architecture, OS/Memory, Concurrency, Networking.
- Distributed/platform core: Distributed Systems + Storage/DB + Containers/Cloud/Observability.
- Performance Engineering cross-cutting capability'dir; advanced profiling Architecture/OS/GPU/inference boyunca büyür.
- Accelerator core: GPU Architecture → CUDA/Triton.
- ML/Transformer inference için supporting domain'dir; generic ML-research specialization değildir.
- LLM Inference, Serving Systems ve KV/Batching/Scheduling/Quantization ayrı fakat bağlı family'lerdir.
- Multi-GPU/NCCL/RDMA networking + distributed + GPU foundations'in advanced convergence katmanıdır.
- AI/GPU Infrastructure final target integration domainidir.
- Open Source / engineering practice / projects / capstones finalde aniden başlamaz; route boyunca artan professional evidence layer'dır.
- Security/reliability/observability ve gerekli math/numerical knowledge hidden prerequisite bırakılmaz; ilgili domainlere explicit capability olarak dağıtılır.
- Tool/vendor isimleri stable systems concept yerine geçmez; version/freshness metadata ile ayrılır.
- Domain-level authoring relations runtime hard-lock değildir; runtime canonical prerequisite Skill→Skill PRG-v0'dır.

5A ayrı Research AI kullanmadı; mevcut kabul edilmiş professional route'u formalize etti. Bağımsız full coverage/current-industry/prerequisite Research QA AŞAMA 6H'de zorunlu planlanmıştır.

## 5. D-050 repository hygiene audit sonucu

2026-08-25'te repo içindeki dosya seti tek tek audit edildi.

Yapılan kalıcı düzeltmeler:
- `PROJECT_CONTEXT.md` eski 4B state'inden güncel 5B state'ine taşındı ve mandatory living snapshot rolü kilitlendi.
- `PROJECT_MASTER_CONTEXT.md` ve README'den volatile aktif-step duplication kaldırıldı.
- `docs/TODO.md` eski AŞAMA 0 / 3-year plan nedeniyle duplicate+stale olduğu için silindi.
- `docs/LEARNING_ENGINE.md` davranış kaynağı olmaktan çıkarılıp explicit `HISTORICAL / SUPERSEDED` pointer'a dönüştürüldü; canonical kaynak `LEARNING_ENGINE_SPEC.md` ve sonraki specs'tir.
- `docs/ENGLISH_TRACK.md` exact A0→B2 hedefi kilitlenmiş gibi görünmesin diye `NON-CANONICAL SEED NOTES` olarak işaretlendi.
- `ENGLISH_FOUNDATION_RULES.md` D-044 sonrası AŞAMA 6C / AŞAMA 7 ayrımına göre düzeltildi.
- `PROJECT_MEMORY_PROTOCOL` ve `AI_AGENT_WORKFLOW` D-050 mandatory sync/stale-scan kuralıyla güçlendirildi.
- Repo-wide stage/file/status drift taraması bu bakım turunun parçasıdır.

Bu cleanup **numaralı 5B adımını yürütmedi** ve daha önce kabul edilmiş aşama/spec davranışlarını değiştirmedi.

## 6. D-051 / 5B final özeti

Canonical: `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.

KGC-v0 organization (`Domain → Module → Topic`) ile capability/evidence (`Skill → Learning Objective`) identity'sini ayırır; Topic↔Skill many-to-many reuse, Skill→Skill hard/soft prerequisites, scope-relative requirements, evidence/retention/remediation/English/professional attribution, provenance/freshness ve conservative graph migration contract'ını tanımlar.

## 7. D-052 / 5C final özeti

Canonical: `docs/V1_FOUNDATION_BACKBONE.md`.

FBB-v0:
- V1 “8–12 hafta” ifadesini calendar gate değil scope-equivalent content envelope olarak kullanır,
- zero-entry Computer/Programming bridge + Python + C + Linux/Git/Shell + early DS&A + parallel Technical English seed subgraph'ını tanımlar,
- Technical/English/professional-workflow scope'larını ayırır; English global technical blocker değildir,
- KGC-v0 uyumlu Skill/Objective logical IDs üretir fakat 6A/6C öncesi lifecycle `authoring_seed / not_learner_published` kalır,
- shared mental-model Skills ile language-specific production Skills'i ayırır,
- initial hard/soft Skill prerequisite edges PRG-v0 semantics ile tanımlar,
- GRE/QAB/RVR uyumlu evidence/retention/diagnostic/remediation anchor'ları verir,
- AŞAMA 15 production authoring ihtiyaçlarını ve 5D graph-QA fixture'larını tanımlar.

5C ayrı Research AI kullanmadı; 6H external coverage/current-industry/prerequisite Research QA zorunlu kalır.

## 8. D-053 / 5D final özeti

Canonical: `docs/GRAPH_ARCHITECTURE_QA.md`.

GQA-v0:
- initial FBB-v0 audit'inde explicit TopicSkillLink eksikliği ve invalid `reason_kind=supporting` bulundu,
- TopicSkillLink seed matrix FBB'ye eklendi,
- reason kinds KGC controlled vocabulary'ye normalize edildi,
- Python/data/error/module/file, trace/debug, professional debug explanation ve C storage/lifetime hidden-prerequisite riskleri minimal hard edges ile düzeltildi,
- corrected hard graph DAG; self/dangling/conflicting edge yok,
- shared Skill reuse TopicSkillLink ile, clone mastery state yok,
- English global technical hard gate yok,
- F5D-01..F5D-10 fixtures PASS,
- FBB authoring_seed olarak kalır; 6A/6C/6H öncesi learner-published değildir.

5D external Research AI kullanmadı; full coverage/current-industry/prerequisite independent Research QA 6H'de zorunlu kalır.

## 9. Tamamlanan aşamalar

- AŞAMA 1 ✅
- AŞAMA 2 ✅ — GRE-v0 / RVR-v0
- AŞAMA 3 ✅ — adaptive planner; 16/16 scenarios, 20/20 invariants PASS
- AŞAMA 4 ✅ — DMA-v0 / WBA-v0 / MCA-v0 / QAB-v0 / AIV-v0
- AŞAMA 5 ✅ — PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6:
  - 6A 🟡 Granularity + naming standardı — aktif, henüz yürütülmedi
  - 6B–6H ⬜
- AŞAMA 7–20 ⬜

## 10. Güncel kesin konum

**Aktif:** `6A — Granularity + naming standardı`  
**6A henüz yürütülmedi.**

## 11. 6A'da kesinleştirilecekler

Ana soru:
> 23 route family yüzlerce/binlerce capability'ye ayrılırken hangi semantic sınırda yeni Domain/Module/Topic/Skill/Objective yaratılmalı ve canonical identity nasıl yıllarca stabil tutulmalı?

Kesinleştirilecek:
- entity-level granularity sınırları,
- canonical logical ID naming convention,
- display label vs identity ayrımı,
- over-fragmentation / under-fragmentation guard,
- shared vs language/tool-specific Skill split kriterleri,
- Objective atomicity + observable action standardı,
- FBB seed ratification / split / merge / rename kuralları,
- KGC migration/versioning uyumu,
- 6B decomposition template handoff'u.

6A full route decomposition yapmaz; 6B–6F bunu kullanır.

## 12. 6A için PRE-STEP doğrudan okunacaklar
1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. `PROJECT_CONTEXT.md`
7. `docs/GRAPH_ARCHITECTURE_QA.md`
8. `docs/V1_FOUNDATION_BACKBONE.md`
9. `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`
10. `docs/LEARNING_ENGINE_SPEC.md`
11. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
12. `docs/CURRICULUM_DOMAIN_MAP.md`
13. `docs/PREREQUISITE_POLICY_SPEC.md`
14. `docs/PROJECT_MEMORY_PROTOCOL.md`

6A başlamadan fresh PRE-STEP GitHub refresh zorunludur.
